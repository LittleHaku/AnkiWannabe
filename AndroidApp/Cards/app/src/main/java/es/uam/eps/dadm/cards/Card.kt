package es.uam.eps.dadm.cards
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.roundToLong

@Entity(tableName = "cards_table")
open class Card(
    @ColumnInfo(name = "card_question")
    var question: String,
    var answer: String,
    var date: String = now().toString(),
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var deckId: String = UUID.randomUUID().toString(),
    var quality: Int = 0,
    var repetitions: Int = 0,
    var interval: Long = 1L,
    var nextPracticeDate: String = date,
    var easiness: Double = 2.5

) {
    companion object {
        /*fun fromString(string: String): Card {
            val parts = string.split("|").map { it.trim() }
            val question = parts[1]
            val answer = parts[2]
            val date = parts[3]
            val id = parts[4]
            val deckId = parts[5]
            val quality = parts[6].toInt()
            val repetitions = parts[7].toInt()
            val interval = parts[8].toLong()
            val nextPracticeDate = parts[9]
            val easiness = parts[10].toDouble()

            return Card(question, answer, date, id, deckId,quality, repetitions, interval, nextPracticeDate, easiness)
        }*/
    }


    override fun toString(): String {
        return "card | $question | $answer | $date | $id | $deckId |$quality | $repetitions | $interval | $nextPracticeDate | $easiness"
    }

    open fun show() {
        print("\n$question (ENTER to see answer)")
        readln()
        print("$answer (Type 0 -> Difficult 3 -> Doubt 5 -> Easy): ")
        val quality = readlnOrNull()?.toIntOrNull() ?: -1
        if (quality == 0 || quality == 3 || quality == 5)
            this.quality = quality
        else
            println("Not a valid difficulty")
    }

    fun update(currentDate: LocalDateTime) {
        val interval = intervalComputation()

        // date
        nextPracticeDate = currentDate.plusDays(interval).toString()
    }

    private fun intervalComputation(): Long {
        // easiness
        val newEasiness = easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)
        easiness = if (newEasiness < 1.3) 1.3 else newEasiness

        // reps
        repetitions = if (quality < 3) 0 else repetitions + 1


        // interval
        interval = when (repetitions) {
            0, 1 -> 1
            2 -> 6
            else -> (interval * easiness).roundToLong()
        }
        return interval
    }

    // This is to calculate the intervals that are shown under the difficulty buttons
    fun possibleNextPractice(): Map<Int, Long> {
        val difficulties = listOf(5, 3, 0)
        val intervals = mutableMapOf<Int, Long>()

        for (diff in difficulties) {
            // easiness
            val newEasiness = easiness + 0.1 - (5 - diff) * (0.08 + (5 - diff) * 0.02)
            val fakeEasiness = if (newEasiness < 1.3) 1.3 else newEasiness

            // reps
            val fakeRepetitions = if (diff < 3) 0 else repetitions + 1

            // interval
            val fakeInterval = when (fakeRepetitions) {
                0, 1 -> 1
                2 -> 6
                else -> (interval * fakeEasiness).roundToLong()
            }
            intervals[diff] = fakeInterval
        }

        return intervals
    }


    fun isDue(date: LocalDateTime) =
        parse(nextPracticeDate) <= date

    fun details() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = parse(nextPracticeDate).format(formatter)

        println(
            "eas = %.2f rep = $repetitions int = $interval next = $date".format(easiness)
        )
    }

    /*fun simulate(period: Long) {
        println("Simulation of the card $question:")
        var now = now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (i in 1..period) {
            println("Date: ${now.format(formatter)}")
            if (now >= parse(nextPracticeDate)) {
                show()
                update(now)
                details()
            }
            now = now.plusDays(1)
        }
    }*/




}