import org.example.core.TestRunner;
import org.example.samples.DemoTests;

void main() {
    try {
        TestRunner.runTests(DemoTests.class);
    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
    }
}
