inline fun sumValues(values: List<Int>, strategy: (Int) -> Int): Int {
    // OR return values.sumOf(strategy)
    // OR values.sumOf { value -> strategy(value) }
    return values.sumOf { strategy(it) }
}

inline fun sumValuesUsingPredicate(values: List<Int>, strategy: (Int) -> Boolean): Int {
    // OR return values.filter(strategy).sum()
    return values.filter { value -> strategy(value) }.sum()
}


fun main() {
    val values = listOf(1, 2, 3, 4, 5)
    val sum = sumValues(values) { it * 2 }
    println(sum)

    val sum2 = sumValuesUsingPredicate(values) { it % 2 == 0 }
    println(sum2)

    val sumIgnore = sumValuesUsingPredicate(values) { true }
    println(sumIgnore)

    val isOdd = { number: Int -> number % 2 != 0 }
    val sumOdd = sumValuesUsingPredicate(values, isOdd)
    println(sumOdd)
}