fun main() {
    val newDeck = Deck("English")

    do {
        print(
            "\n1. Add card\n" +
                    "2. List of cards\n" +
                    "3. Simulation\n" +
                    "4. Read cards from file\n" +
                    "5. Write cards to file\n" +
                    "6. Exit\n" +
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
            4 -> newDeck.readCards("cards")
            5 -> newDeck.writeCards("cards")
            6 -> println("Bye bye!")
        }
    } while (option != 6)
}