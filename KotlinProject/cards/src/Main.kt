fun main() {
    val decks = mutableListOf(Deck("English"))
    var currentDeck = decks[0]

    do {
        print(
            """
1. Add card
2. List cards
3. Remove Cards
4. Simulate current deck
5. Read cards from file to current deck
6. Write cards from current deck to file
7. Example Histogram (1000 cards over 30 days with 90% chance)
8. Deck Options
9. Exit
Choose an option: """
        )

        val option: Int? = readlnOrNull()?.toIntOrNull()
        if (option == null) {
            println("Not a valid option")
            return
        }
        when (option) {
            1 -> currentDeck.addCard()
            2 -> currentDeck.listCards()
            3 -> deleteCard(currentDeck, decks)
            4 -> simulate(currentDeck)
            5 -> loadDeck(currentDeck)
            6 -> saveDeck(currentDeck)
            7 -> histogram(simulate(30, 1000, 0.90, 0.08, 0.02))
            8 -> {
                print(
                    "\n1. List all decks\n" +
                            "2. Add new deck\n" +
                            "3. Remove a deck\n" +
                            "4. Choose the working deck\n" +
                            "Choose an option: "
                )

                val deckOption: Int? = readlnOrNull()?.toIntOrNull()
                if (deckOption == null) {
                    println("Not a valid option")
                    return
                }
                when (deckOption) {
                    1 -> listDecks(decks)
                    2 -> currentDeck = addNewDeck(decks, currentDeck)
                    3 -> currentDeck = removeDeck(decks, currentDeck)
                    4 -> currentDeck = changeDeck(decks, currentDeck)
                }
            }
            9 -> {
                println("Bye bye!")
                return
            }
        }
    } while (true)
}

private fun saveDeck(currentDeck: Deck) {
    print("Choose the name of the file to load (no .txt) or empty for deck's name: ")
    var name = readlnOrNull() ?: currentDeck.name
    if (name == "") name = currentDeck.name
    currentDeck.writeCards(name)
}

private fun loadDeck(currentDeck: Deck) {
    print("Choose the name of the file (no .txt) or empty for deck's name: ")
    var name = readlnOrNull() ?: currentDeck.name
    if (name == "") name = currentDeck.name
    try {
        currentDeck.readCards(name)
    } catch (e: Exception) {
        println("That file doesn't exist")
    }
}

private fun deleteCard(currentDeck: Deck, decks: MutableList<Deck>) {
    currentDeck.listCards()
    print("Enter the number of the card to remove: ")
    val cardNumber = readlnOrNull()?.toIntOrNull()
    if (cardNumber != null && cardNumber in 1..decks.size) {
        val removedCard = currentDeck.cards.removeAt(cardNumber - 1)
        println("Removed the ${removedCard.question} card")
    } else {
        println("Not a valid card number")
    }
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
        listDecks(decks)
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

private fun listDecks(decks: MutableList<Deck>) {
    decks.forEachIndexed { index, deck ->
        println("${index + 1}. ${deck.name}")
    }
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
    val days = readlnOrNull()?.toIntOrNull()
    if (days == null) {
        println("Not a valid time period")
    } else {
        deck.simulate(days)
    }
}
