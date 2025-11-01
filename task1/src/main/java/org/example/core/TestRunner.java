package org.example.core;

import org.example.annotations.*;
import org.example.csv.Csv;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestRunner {
    public static void runTests(Class<?> aClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Groups groups = groupAll(aClass.getDeclaredMethods());

        List<Method> plan = new ArrayList<>();
        plan.add(groups.beforeSuiteMethod);
        for (Method method : groups.testMethods) {
            plan.addAll(groups.beforeTestMethods);
            plan.add(method);
            plan.addAll(groups.afterTestMethods);
        }
        plan.add(groups.afterSuiteMethod);

        Object instance = aClass.getDeclaredConstructor().newInstance();
        plan.forEach(method -> {
            try {
                call(method, instance);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static void call(Method method, Object instance) throws InvocationTargetException, IllegalAccessException {
        method.trySetAccessible();
        Object target = Modifier.isStatic(method.getModifiers()) ? null : instance;
        if (method.isAnnotationPresent(CsvSource.class)) {
            CsvSource ann = method.getDeclaredAnnotation(CsvSource.class);
            Csv args = new Csv(method, ann.csv());
            method.invoke(target, args.GetArgs());
        } else {
            method.invoke(target);
        }
    }

    static Groups groupAll(Method[] methods) {
        List<Method> beforeTestMethods = new ArrayList<>();
        List<Method> afterTestMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;

        for (Method m : methods) {
            boolean isStatic = Modifier.isStatic(m.getModifiers());
            for (Annotation a : m.getDeclaredAnnotations()) {
                switch (a) {
                    case BeforeSuite _ -> {
                        if (!isStatic) {
                            throw new IllegalStateException("@BeforeSuite должен быть static");
                        }
                        if (beforeSuiteMethod != null) {
                            throw new IllegalStateException("Должен быть ровно один @BeforeSuite");
                        }
                        beforeSuiteMethod = m;
                    }
                    case AfterSuite _ -> {
                        if (!isStatic) {
                            throw new IllegalStateException("@AfterSuite должен быть static");
                        }
                        if (afterSuiteMethod != null) {
                            throw new IllegalStateException("Должен быть ровно один @AfterSuite");
                        }
                        afterSuiteMethod = m;
                    }
                    case BeforeTest _ -> {
                        if (isStatic) {
                            throw new IllegalStateException("@BeforeTest не должны быть static");
                        }
                        beforeTestMethods.add(m);
                    }
                    case AfterTest _ -> {
                        if (isStatic) {
                            throw new IllegalStateException(" @AfterTest не должны быть static");
                        }
                        afterTestMethods.add(m);
                    }
                    case Test _ -> {
                        if (isStatic) {
                            throw new IllegalStateException("@Test не должны быть static");
                        }
                        testMethods.add(m);
                    }
                    default -> {
                    }
                }
            }
        }

        testMethods.sort(Comparator.comparingInt((Method m) -> m.getAnnotation(Test.class).priority()).reversed());

        return new Groups(
                beforeSuiteMethod,
                afterSuiteMethod,
                beforeTestMethods,
                afterTestMethods,
                testMethods
        );
    }

    record Groups(
            Method beforeSuiteMethod,
            Method afterSuiteMethod,
            List<Method> beforeTestMethods,
            List<Method> afterTestMethods,
            List<Method> testMethods
    ) {
    }

}
