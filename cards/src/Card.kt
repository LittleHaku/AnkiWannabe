import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.roundToLong

open class Card(
    var question: String,
    var answer: String,
    var date: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    var id: String = UUID.randomUUID().toString(),
    var quality: Int = 0,
    var repetitions: Int = 0,
    var interval: Long = 1L,
    var nextPracticeDate: String = date,
    var easiness: Double = 2.5

) {
    companion object {
        fun fromString(string: String): Card {
            val parts = string.split("|").map { it.trim() }
            val question = parts[1]
            val answer = parts[2]
            val date = parts[3]
            val id = parts[4]
            val quality = parts[5].toInt()
            val repetitions = parts[6].toInt()
            val interval = parts[7].toLong()
            val nextPracticeDate = parts[8]
            val easiness = parts[9].toDouble()

            return Card(question, answer, date, id, quality, repetitions, interval, nextPracticeDate, easiness)
        }
    }


    override fun toString(): String {
        return "card | ${this.question} | ${this.answer} | ${this.date} | ${this.id} | ${this.quality} | ${this.repetitions} | ${this.interval} | ${this.nextPracticeDate} | ${this.easiness}"
    }

    open fun show() {
        print("\n${this.question} (ENTER to see answer)")
        readln()
        print("${this.answer} (Type 0 -> Difficult 3 -> Doubt 5 -> Easy): ")
        val quality = readlnOrNull()?.toIntOrNull() ?: -1
        if (quality == 0 || quality == 3 || quality == 5)
            this.quality = quality
        else
            println("Not a valid difficulty")
    }

    fun update(currentDate: LocalDateTime) {
        // easiness
        val newEasiness = this.easiness + 0.1 - (5 - this.quality) * (0.08 + (5 - this.quality) * 0.02)
        this.easiness = if (newEasiness < 1.3) 1.3 else newEasiness

        // reps
        this.repetitions = if (this.quality < 3) 0 else this.repetitions + 1


        // interval
        this.interval = when (this.repetitions) {
            0, 1 -> 1
            2 -> 6
            else -> (this.interval * this.easiness).roundToLong()
        }

        // date
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        this.nextPracticeDate = currentDate.plusDays(interval).format(formatter)
    }

    fun details() {
        println(
            "eas = %.2f rep = ${this.repetitions} int = ${this.interval} next = ${this.nextPracticeDate}".format(
                this.easiness
            )
        )
    }

    fun simulate(period: Long) {
        println("Simulation of the card $question:")
        var now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (i in 1..period) {
            println("Date: ${now.format(formatter)}")
            if (now.format(formatter) == this.nextPracticeDate) {
                this.show()
                this.update(now)
                this.details()
            }
            now = now.plusDays(1)
        }
    }


}