import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Deck (
    var name: String,
    var id: String = java.util.UUID.randomUUID().toString()
    ) {
    val cards = mutableListOf<Card>()

    fun addCard() {
        println("Adding card to deck")
        print("Type the type (0 -> Card 1 -> Cloze): ")
        val type = readlnOrNull()?.toIntOrNull()
        if (type == null || type < 0 || type > 1) {
            println("That type is not valid...")
            return
        }
        print("Type the question: ")
        val question = readlnOrNull()
        print("Type the answer: ")
        val answer = readlnOrNull()
        if (answer == null || question == null) {
            println("There was an error")
        } else {
            try {
                val card = if (type == 0) {
                    Card(question, answer)
                } else {
                    Cloze(question, answer)
                }
                cards.add(card)
                println("Card added successfully")
            } catch (e: IllegalArgumentException) {
                println("Error creating card: ${e.message}")
            }
        }
    }

    fun listCards() {
        println("\nList of Cards: ")
        for (card in cards) {
            println("${card.question} -> ${card.answer}")
        }
    }

    fun simulate(period: Int) {
        println("Simulation of deck $name:")
        var now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (i in 1..period) {
            println("Day: ${now.format(formatter)}")
            for (card in cards) {
                if (now.format(formatter) == card.nextPracticeDate) {
                    card.show()
                    card.update(now)
                    card.details()
                }
            }
            now = now.plusDays(1)
        }
    }
}