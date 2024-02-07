fun main() {
    class Person(
        var name: String,
        var age: Int
    ) {
        fun greet() = println("My name is $name")
    }

    // Add your code here
    val people: List<Person> = listOf(Person("Pedrito", 48), Person("Manolito", 26), Person("Javiercito", 21))

    val oldest = people.maxByOrNull() {it.age}

    println("Oldest person: ${oldest?.name}")
}