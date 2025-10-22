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

    public static void runTests(Class<?> c) {
        Groups g = groupAll(c.getDeclaredMethods());

        if (Set.of(g.BeforeSuite, g.AfterSuite).stream()
                .anyMatch(TestRunner::isAnnotationNotSingleOrNotStatic)) {
            throw new IllegalStateException("должно быть не больше одного статического метода BeforeSuite или AfterSuite");
        }

        if (Set.of(g.Test, g.BeforeTest, g.AfterTest).stream()
                .anyMatch(TestRunner::isAnnotationNonStatic)) {
            throw new IllegalStateException("не должно быть статических методов Test, BeforeTest или AfterTest");
        }

        List<Method> m = new ArrayList<>(g.BeforeSuite.methods);

        List<Method> tPriority = g.Test.methods.stream()
                .sorted(Comparator
                        .comparingInt((Method md) -> md.getAnnotation(Test.class).priority())
                        .reversed()
                )
                .toList();

        m.addAll(
                tPriority.stream()
                        .flatMap(
                                test -> Stream.concat(
                                        Stream.concat(g.BeforeTest.methods.stream(), Stream.of(test)), g.AfterTest.methods.stream())
                        ).toList()
        );
        m.addAll(g.AfterSuite.methods);

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
        Map<Class<? extends Annotation>, PerAnno> g = groupByAnnotationsStream(methods,
                List.of(BeforeSuite.class, AfterSuite.class, BeforeTest.class, AfterTest.class, Test.class));

        return new Groups(
                g.getOrDefault(BeforeSuite.class, EMPTY),
                g.getOrDefault(BeforeTest.class, EMPTY),
                g.getOrDefault(AfterTest.class, EMPTY),
                g.getOrDefault(Test.class, EMPTY),
                g.getOrDefault(AfterSuite.class, EMPTY)
        );
    }

    record Entry(Class<? extends Annotation> anno, Method method, boolean isStatic) {
    }

    record PerAnno(List<Method> methods, long staticCount, long instanceCount) {
    }

    record Groups(
            PerAnno BeforeSuite,
            PerAnno BeforeTest,
            PerAnno AfterTest,
            PerAnno Test,
            PerAnno AfterSuite
    ) {
    }

}
