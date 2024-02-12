import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Deck (
    var name: String,
    var id: String = java.util.UUID.randomUUID().toString()
    ) {
    val cards = mutableListOf<Card>()

    fun addCard() {
        println("Adding card to deck")
        print("Type the question: ")
        val question = readlnOrNull()
        print("Type the answer: ")
        val answer = readlnOrNull()
        if (answer == null || question == null)
            println("There was an error")
        else {
            cards.add(Card(question, answer))
            println("Card added successfully")
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