sealed interface User

data class Admin(val name: String) : User

data class Customer(val name: String) : User

fun printUser(user: User) {
    when (user) {
        is Admin -> println("Admin: ${user.name}")
        is Customer -> println("Customer: ${user.name}")
    }
}

fun main() {
    val admin = Admin("John")
    val customer = Customer("Jane")
    printUser(admin)
    printUser(customer)
}