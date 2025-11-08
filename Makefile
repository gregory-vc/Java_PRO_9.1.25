run_task1:
	./gradlew ":task1:run"

run_task2:
	./gradlew ":task2:run"

run_task3:
	./gradlew ":task3:run"

build_task4:
	mvn -f task4/pom.xml compile

run_task4:
	mvn -f task4/pom.xml exec:exec

build_task5:
	mvn -f task5/pom.xml compile

run_task5:
	mvn -f task5/pom.xml exec:exec