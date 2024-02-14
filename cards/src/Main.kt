fun main() {
    val newDeck = Deck("English")

    do {
        print(
            "\n1. Add card\n" +
                    "2. List of cards\n" +
                    "3. Simulation\n" +
                    "4. Exit\n" +
                    "Choose an option: "
        )

        val option: Int? = readlnOrNull()?.toIntOrNull()
        if (option == null) {
            println("Not a valid option")
            return
        }
        when (option) {
            1 -> newDeck.addCard()
            2 -> newDeck.listCards()
            3 -> newDeck.simulate(10)
            4 -> println("Bye bye!")
        }
    } while (option != 4)
}