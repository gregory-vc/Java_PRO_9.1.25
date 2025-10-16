void main() {

    // Найдите в списке целых чисел 3-е наибольшее число (пример: 5 2 10 9 4 3 10 1 13 => 10)

    Stream<Integer> s = Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13);

    Integer answer = s.sorted(Comparator.reverseOrder())
            .skip(2)
            .findFirst()
            .orElseThrow();

    IO.println(answer);

    // Найдите в списке целых чисел 3-е наибольшее «уникальное» число (пример: 5 2 10 9 4 3 10 1 13 => 9, в отличие от прошлой задачи здесь разные 10 считает за одно число)

    Stream<Integer> s2 = Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13);

    Integer answer2 = s2.distinct()
            .sorted(Comparator.reverseOrder())
            .skip(2)
            .findFirst()
            .orElseThrow();

    IO.println(answer2);

    // Имеется список объектов типа Сотрудник (имя, возраст, должность), необходимо получить список имен 3 самых старших сотрудников с должностью «Инженер», в порядке убывания возраста

    List<Worker> workers = new ArrayList<>(List.of(
            new Worker("Alice", 28, Role.ENGINEERING),
            new Worker("Bob", 31, Role.ENGINEERING),
            new Worker("Charlie", 26, Role.ENGINEERING),
            new Worker("Diana", 35, Role.MANAGEMENT),
            new Worker("Ethan", 29, Role.QA),
            new Worker("Fiona", 33, Role.PRODUCT),
            new Worker("George", 41, Role.PROJECT),
            new Worker("Hannah", 27, Role.ANALYTICS),
            new Worker("Ivan", 38, Role.ENGINEERING),
            new Worker("Julia", 24, Role.ENGINEERING),
            new Worker("Kevin", 32, Role.ENGINEERING),
            new Worker("Lena", 30, Role.MANAGEMENT),
            new Worker("Mark", 36, Role.DATA),
            new Worker("Nina", 25, Role.QA),
            new Worker("Oscar", 29, Role.PRODUCT),
            new Worker("Paula", 34, Role.DEVOPS),
            new Worker("Quinn", 23, Role.DESIGN),
            new Worker("Rita", 40, Role.MANAGEMENT),
            new Worker("Sam", 37, Role.PROJECT),
            new Worker("Tina", 28, Role.ANALYTICS)
    ));

    List<Worker> elderEng = workers.stream()
            .filter(w -> w.role == Role.ENGINEERING)
            .sorted(Comparator.comparingInt((Worker w) -> w.age).reversed())
            .limit(3)
            .toList();

    elderEng.forEach(w -> IO.println(w.name + " " + w.age + " " + w.role.toString()));

    // Имеется список объектов типа Сотрудник (имя, возраст, должность), посчитайте средний возраст сотрудников с должностью «Инженер»

    double avgAgeEng = workers.stream()
            .filter(w -> w.role == Role.ENGINEERING)
            .mapToInt(w -> w.age)
            .average()
            .orElse(0);

    IO.println(avgAgeEng);

}