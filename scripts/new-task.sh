#!/usr/bin/env bash
set -euo pipefail

if [[ ${#} -lt 1 ]]; then
  echo "Usage: scripts/new-task.sh <moduleName>" >&2
  exit 1
fi

NAME="$1"

if [[ ! "$NAME" =~ ^[a-zA-Z0-9_-]+$ ]]; then
  echo "Invalid module name: $NAME" >&2
  exit 1
fi

ROOT_DIR=$(cd "$(dirname "$0")"/.. && pwd)
MOD_DIR="$ROOT_DIR/$NAME"

if [[ -e "$MOD_DIR" ]]; then
  echo "Module '$NAME' already exists at $MOD_DIR" >&2
  exit 1
fi

mkdir -p "$MOD_DIR/src/main/java"

cat > "$MOD_DIR/build.gradle.kts" <<'KTS'
plugins {
    application
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("App")
}
KTS

cat > "$MOD_DIR/src/main/java/App.java" <<'JAVA'
void main() {
    IO.println("Hello from new task!");
}
JAVA

# Add include to settings.gradle.kts if not present
SETTINGS_FILE="$ROOT_DIR/settings.gradle.kts"
INCLUDE_LINE="include(\"$NAME\")"

if ! grep -Fq "$INCLUDE_LINE" "$SETTINGS_FILE"; then
  printf '\n%s\n' "$INCLUDE_LINE" >> "$SETTINGS_FILE"
  echo "Added $INCLUDE_LINE to settings.gradle.kts"
else
  echo "settings.gradle.kts already includes module '$NAME'"
fi

echo "Module '$NAME' created. Run: ./gradlew :$NAME:run"
