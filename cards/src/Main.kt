fun main() {
    var newDeck = Deck("English")
    newDeck.cards.add(Card("To wake up", "Despertarse"))
    newDeck.cards.add(Card("To come up with", "Proponer"))


    do {
        print(
            "\n1. Add card\n" +
                    "2. List of cards\n" +
                    "3. Simulation\n" +
                    "4. Exit\n" +
                    "Choose an option: "
        )

        val option: Int? = readLine()?.toIntOrNull()
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