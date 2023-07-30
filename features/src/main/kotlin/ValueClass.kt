@JvmInline
value class Username (private val value: String) {
    companion object {
        operator fun invoke(value: String): Username {
            if (value.length !in 3..20) {
                throw IllegalArgumentException("Username must be between 3 and 20 characters")
            }

            return Username(value)
        }
    }

    override fun toString() = value

}

@JvmInline
value class Age(private val value: Short) {
    companion object {
        operator fun invoke(value: Short): Age {
            if (value !in 0..150) {
                throw IllegalArgumentException("Age must be between 0 and 150")
            }

            return Age(value)
        }
    }

    operator fun plus(other: Age) = Age((value.plus(other.value)).toShort())

    operator fun minus(other: Age) = Age(value.minus(other.value).toShort())

}

val youngAge = Age(25)
val oldAge = Age(65)

val username = Username("johndoe")

fun main() {
    println(username)
    println(username.toString())

    val plusAge = youngAge.plus(oldAge)
    println(plusAge)
}
