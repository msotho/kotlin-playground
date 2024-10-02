import java.awt.Color
import java.math.BigDecimal

class Camera(private val filters: List<(color: Color) -> Color>) {
    fun snap(input: Color): Color {
        return filters.fold(input) { color, filter -> filter(color) }
    }
}

class Calculator(private val operations: List<(number: Int) -> Int>) {
    fun calculate(input: Int): Int {
        return operations.fold(input) { number, operation -> operation(number) }
    }
}

fun <T: BigDecimal> T.fold(initial: T, operation: (T) -> T): T {
    return operation(initial)
}

fun main() {
    val camera = Camera(
        listOf(
            { color: Color -> color.brighter() },
            { color: Color -> color.darker() }
        ))
    val snap = camera.snap(Color(125, 125, 125))
    println(snap)

    fun add() = { number: Int -> number + 1 }
    val multiply = { number: Int -> number * 10 }

    val calculator = Calculator(listOf(add(), multiply))

    val calculate = calculator.calculate(1)
    println(calculate)

    val c = BigDecimal.ONE.let {
        it.plus(BigDecimal.ONE)
    }
    println(c)

    val fold = BigDecimal(1).fold(BigDecimal(1)) { number -> number + BigDecimal(1) }
}