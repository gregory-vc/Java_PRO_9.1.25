package org.example.core;

import org.example.annotations.*;
import org.example.csv.Csv;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestRunner {
    static final PerAnno EMPTY = new PerAnno(List.of(), 0L, 0L);

    public static void runTests(Class<?> aClass) {
        Groups groups = groupAll(aClass.getDeclaredMethods());

        if (Set.of(groups.beforeSuite, groups.afterSuite).stream()
                .anyMatch(TestRunner::isAnnotationNotSingleOrNotStatic)) {
            throw new IllegalStateException("должно быть не больше одного статического метода beforeSuite или afterSuite");
        }

        if (Set.of(groups.test, groups.beforeTest, groups.afterTest).stream()
                .anyMatch(TestRunner::isAnnotationNonStatic)) {
            throw new IllegalStateException("не должно быть статических методов test, beforeTest или afterTest");
        }

        List<Method> methodList = new ArrayList<>(groups.beforeSuite.methods);

        List<Method> tPriority = groups.test.methods.stream()
                .sorted(Comparator
                        .comparingInt((Method md) -> md.getAnnotation(Test.class).priority())
                        .reversed()
                )
                .toList();

        methodList.addAll(
                tPriority.stream()
                        .flatMap(
                                test -> Stream.concat(
                                        Stream.concat(groups.beforeTest.methods.stream(), Stream.of(test)), groups.afterTest.methods.stream())
                        ).toList()
        );
        methodList.addAll(groups.afterSuite.methods);

        methodList.forEach(a -> call(aClass, a));
    }

    static void call(Class<?> aClass, Method method) {
        try {
            method.trySetAccessible();
            Object instance = aClass.getDeclaredConstructor().newInstance();
            Object target = Modifier.isStatic(method.getModifiers()) ? null : instance;

            if (method.isAnnotationPresent(CsvSource.class)) {
                CsvSource ann = method.getDeclaredAnnotation(CsvSource.class);
                Csv args = new Csv(method, ann.csv());
                method.invoke(target, args.GetArgs());
            } else {
                method.invoke(target);
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Method threw: " + method, e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke: " + method, e);
        }
    }

    static Map<Class<? extends Annotation>, PerAnno> groupByAnnotationsStream(Method[] methods, Collection<Class<? extends Annotation>> want) {
        Set<Class<? extends Annotation>> wantSet = (want instanceof Set)
                ? (Set<Class<? extends Annotation>>) want
                : new HashSet<>(want);

        return Arrays.stream(methods)
                .flatMap(m -> {
                    boolean isStatic = Modifier.isStatic(m.getModifiers());
                    return Arrays.stream(m.getDeclaredAnnotations())
                            .map(Annotation::annotationType)
                            .filter(wantSet::contains)
                            .map(a -> new Entry(a, m, isStatic));
                })
                .collect(Collectors.groupingBy(
                        Entry::anno,
                        LinkedHashMap::new,
                        Collectors.teeing(
                                Collectors.mapping(Entry::method, Collectors.toList()),
                                Collectors.summarizingLong(e -> e.isStatic() ? 1L : 0L),
                                (list, stat) -> new PerAnno(list, stat.getSum(), list.size() - stat.getSum())
                        )
                ));
    }

    static boolean isAnnotationNotSingleOrNotStatic(PerAnno anno) {
        return anno.staticCount > 1L || anno.instanceCount != 0L;
    }

    static boolean isAnnotationNonStatic(PerAnno anno) {
        return anno.staticCount > 0L;
    }

    static Groups groupAll(Method[] methods) {
        Map<Class<? extends Annotation>, PerAnno> grouped = groupByAnnotationsStream(methods,
                List.of(BeforeSuite.class, AfterSuite.class, BeforeTest.class, AfterTest.class, Test.class));

        return new Groups(
                grouped.getOrDefault(BeforeSuite.class, EMPTY),
                grouped.getOrDefault(BeforeTest.class, EMPTY),
                grouped.getOrDefault(AfterTest.class, EMPTY),
                grouped.getOrDefault(Test.class, EMPTY),
                grouped.getOrDefault(AfterSuite.class, EMPTY)
        );
    }

    record Entry(Class<? extends Annotation> anno, Method method, boolean isStatic) {
    }

    record PerAnno(List<Method> methods, long staticCount, long instanceCount) {
    }

    record Groups(
            PerAnno beforeSuite,
            PerAnno beforeTest,
            PerAnno afterTest,
            PerAnno test,
            PerAnno afterSuite
    ) {
    }

}
