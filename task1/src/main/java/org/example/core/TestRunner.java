package org.example.core;

import org.example.annotations.*;
import org.example.csv.Csv;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class TestRunner {
    public static void runTests(Class<?> c) {
        Method[] methods = c.getDeclaredMethods();
        if (isAnnotationNotSingleOrNotStatic(methods, BeforeSuite.class)) {
            throw new IllegalStateException("@BeforeSuite должно быть не больше одного статического метода");
        }

        if (isAnnotationNotSingleOrNotStatic(methods, AfterSuite.class)) {
            throw new IllegalStateException("@AfterSuite должно быть не больше одного статического метода");
        }

        if (isAnnotationNonStaticExcept(methods, Set.of(AfterSuite.class, BeforeSuite.class))) {
            throw new IllegalStateException("Не должно быть статических методов кроме AfterSuite и BeforeSuite");
        }

        List<Method> m = getMethodsByAnnotation(methods, BeforeSuite.class);
        List<Method> bTest = getMethodsByAnnotation(methods, BeforeTest.class);
        List<Method> aTest = getMethodsByAnnotation(methods, AfterTest.class);
        List<Method> t = getMethodsByAnnotation(methods, Test.class);

        List<Method> tPriority = t.stream()
                .sorted(Comparator
                        .comparingInt((Method md) -> md.getAnnotation(Test.class).priority())
                        .reversed()
                )
                .toList();

        m.addAll(
                tPriority.stream()
                        .flatMap(
                                test -> Stream.concat(
                                        Stream.concat(bTest.stream(), Stream.of(test)), aTest.stream())
                        ).toList()
        );
        m.addAll(getMethodsByAnnotation(methods, AfterSuite.class));

        m.forEach(a -> call(c, a));
    }

    static void call(Class<?> c, Method m) {
        try {
            m.trySetAccessible();
            Object instance = c.getDeclaredConstructor().newInstance();
            Object target = Modifier.isStatic(m.getModifiers()) ? null : instance;

            if (m.isAnnotationPresent(CsvSource.class)) {
                CsvSource ann = m.getDeclaredAnnotation(CsvSource.class);
                Csv args = new Csv(m, ann.csv());
                m.invoke(target, args.GetArgs());
            } else {
                m.invoke(target);
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Method threw: " + m, e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke: " + m, e);
        }
    }

    static List<Method> getMethodsByAnnotation(Method[] methods, Class<? extends Annotation> anno) {
        return new ArrayList<>(Arrays.stream(methods).filter(a -> a.isAnnotationPresent(anno)).toList());
    }

    static boolean isAnnotationNotSingleOrNotStatic(Method[] methods, Class<? extends Annotation> anno) {
        long countStatic = Arrays.stream(methods)
                .filter(a -> a.isAnnotationPresent(anno))
                .filter(a -> Modifier.isStatic(a.getModifiers()))
                .limit(2)
                .count();
        long countNonStatic = Arrays.stream(methods)
                .filter(a -> a.isAnnotationPresent(anno))
                .filter(a -> !Modifier.isStatic(a.getModifiers()))
                .limit(2)
                .count();
        return countStatic != 1L || countNonStatic != 0L;
    }

    static boolean isAnnotationNonStaticExcept(Method[] methods,  Set<Class<? extends Annotation>> anno) {
        long count = Arrays.stream(methods)
                .filter(a -> anno.stream().noneMatch(a::isAnnotationPresent))
                .filter(a -> Modifier.isStatic(a.getModifiers()))
                .count();
        return count > 0L;
    }


}
