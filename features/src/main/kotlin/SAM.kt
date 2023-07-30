fun interface Validator {
    fun execute(value: Int): Boolean
}

fun main() {
    val isEven = Validator { value -> value % 2 == 0 }
    val isOdd = Validator { value -> value % 2 != 0 }

    println(isEven.execute(42))
    println(isOdd.execute(42))
}