fun main() {
    val decks = mutableListOf(Deck("English"))
    var currentDeck = decks[0]

    do {
        print(
            "\n1. Add card\n" +
                    "2. List cards\n" +
                    "3. Simulate current deck\n" +
                    "4. Read cards from file to current deck\n" +
                    "5. Write cards from current deck to file\n" +
                    "6. Example Histogram (1000 cards over 30 days with 90% chance)\n" +
                    "7. List all decks\n" +
                    "8. Add new deck\n" +
                    "9. Remove a deck\n" +
                    "10. Choose the working deck\n" +
                    "11. Exit\n" +
                    "Choose an option: "
        )

        val option: Int? = readlnOrNull()?.toIntOrNull()
        if (option == null) {
            println("Not a valid option")
            return
        }
        when (option) {
            1 -> currentDeck.addCard()
            2 -> currentDeck.listCards()
            3 -> simulate(currentDeck)
            4 -> currentDeck.readCards("cards")
            5 -> currentDeck.writeCards("cards")
            6 -> {
                val cards = simulate(30, 1000, 0.90, 0.08, 0.02)
                histogram(cards)
            }
            7 -> {

                decks.forEachIndexed { index, deck ->
                    println("${index + 1}. ${deck.name}")
                }
            }
            8 -> {
                currentDeck = addNewDeck(decks, currentDeck)
            }
            9 -> {
                currentDeck = removeDeck(decks, currentDeck)
            }
            10 -> {
                currentDeck = changeDeck(decks, currentDeck)
            }
            11 -> {
                println("Bye bye!")
                return;
            }
        }
    } while (true)
}

private fun changeDeck(decks: MutableList<Deck>, currentDeck: Deck): Deck {
    var currentDeck1 = currentDeck
    print("Enter the number of the deck to work with: ")
    val deckNumber = readlnOrNull()?.toIntOrNull()
    if (deckNumber != null && deckNumber in 1..decks.size) {
        currentDeck1 = decks[deckNumber - 1]
        println("Switched to the ${currentDeck1.name} deck")
    } else {
        println("Not a valid deck number")
    }
    return currentDeck1
}

private fun removeDeck(decks: MutableList<Deck>, currentDeck: Deck): Deck {
    var currentDeck1 = currentDeck
    if (decks.size == 1) {
        println("You only have one deck, dont remove it!")
    } else {
        decks.forEachIndexed { index, deck ->
            println("${index + 1}. ${deck.name}")
        }
        print("Enter the number of the deck to remove: ")
        val deckNumber = readlnOrNull()?.toIntOrNull()
        if (deckNumber != null && deckNumber in 1..decks.size) {
            val removedDeck = decks.removeAt(deckNumber - 1)
            println("Removed the ${removedDeck.name} deck")
            if (currentDeck1 == removedDeck) {
                currentDeck1 = decks.getOrNull(0) ?: Deck("Default")
            }
        } else {
            println("Not a valid deck number")
        }

        println("Now working on ${currentDeck1.name}")
    }
    return currentDeck1
}

private fun addNewDeck(decks: MutableList<Deck>, currentDeck: Deck): Deck {
    var currentDeck1 = currentDeck
    print("Enter the name of the new deck: ")
    val deckName = readlnOrNull()
    if (deckName == null) {
        println("Not a valid name")
    }
    val deck = Deck(deckName.toString())
    decks.add(deck)
    println("Added the $deckName deck")
    currentDeck1 = deck
    println("Changed working deck to ${deck.name}")
    return currentDeck1
}

private fun simulate(deck: Deck) {
    print("For how many days do you want to simulate the deck? ")
    val days = readlnOrNull()?.toInt()
    if (days == null) {
        println("Not a valid time period")
    } else {
        deck.simulate(days)
    }
}
