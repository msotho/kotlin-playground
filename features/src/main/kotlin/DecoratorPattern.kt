import java.awt.Color

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
}