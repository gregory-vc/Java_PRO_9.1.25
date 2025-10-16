package org.example.samples;

import org.example.annotations.*;

public class DemoTests {

    @BeforeSuite
    public static void Method1() {
        IO.println("Method1 BeforeSuite");
    }

    @AfterSuite
    public static void Method2() {
        IO.println("Method2 AfterSuite");
    }

    @Test(priority = 1)
    public void Method3() {
        IO.println("Method3 Test with priority 1");
    }

    @Test(priority = 2)
    public void Method4() {
        IO.println("Method4 Test with priority 2");
    }

    @Test(priority = 3)
    @CsvSource(csv = "10, Java, 20, true")
    public void Method5(int a, String b, int c, boolean d) {
        IO.println("Method5 Test with priority 3: int a = "+a+", String b = "+b+", int c = "+c+", boolean d = " + d);
    }

    @BeforeTest
    public void Method6() {
        IO.println("Method6 BeforeTest");
    }

    @AfterTest
    public void Method7() {
        IO.println("Method7 AfterTest");
    }
}
