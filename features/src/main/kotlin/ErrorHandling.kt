import java.lang.IllegalArgumentException
import java.util.UUID
import java.util.logging.Logger


fun convert(value: String): Result<UUID> {
    return try {
        Result.success(UUID.fromString(value))
    } catch (e: IllegalArgumentException) {
        Result.failure(e)
    }
}


fun main () {
    // Default to a random UUID if the conversion fails
    val default = convert("1234").getOrDefault(UUID.randomUUID())
    println("default: $default")

    // Return null if the conversion fails
    val nullValue = convert("12345").getOrNull()
    println("nullValue: $nullValue")

    // Throw an exception if the conversion fails
    val exception = convert("12345").exceptionOrNull()
    println("exception: $exception")

    // Map if the conversion succeeds
    convert("3d8bd6c5-0f94-4a79-8533-fccae18351b4").map {
        Logger.getLogger("ErrorHandling").info("Converted value: $it")
        println("value: $it")
    }

    convert("12345").getOrElse {
        Logger.getLogger("ErrorHandling").warning("Conversion failed")
        println("Conversion failed")
    }

}