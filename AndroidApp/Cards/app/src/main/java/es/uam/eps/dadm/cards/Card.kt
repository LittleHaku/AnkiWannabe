package es.uam.eps.dadm.cards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalDateTime.parse
import java.util.UUID
import kotlin.math.roundToLong

@Entity(tableName = "cards_table")
open class Card(
    @ColumnInfo(name = "card_question") var question: String,
    var answer: String,
    var date: String = now().toString(),
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var deckId: String = UUID.randomUUID().toString(),
    var userId: String
) {
    var quality: Int = 0
    var repetitions: Int = 0
    var interval: Long = 1L
    var nextPracticeDate: String = date
    var easiness: Double = 2.5

    constructor() : this("", "", userId = "")


    override fun toString(): String {
        return "card | $question | $answer | $date | $id | $deckId |$quality | $repetitions | $interval | $nextPracticeDate | $easiness"
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

            // interval
            val fakeInterval = when (if (diff < 3) 0 else repetitions + 1) {
                0, 1 -> 1
                2 -> 6
                else -> (interval * fakeEasiness).roundToLong()
            }
            intervals[diff] = fakeInterval
        }

        return intervals
    }


    // Changed to LocalDate because it is 08 May 2024 00:36, and the cards that have nextPractice
    // with today's date aren't working because the time is set to 13:09 and they should be displayed
    fun isDue(date: LocalDate) = parse(nextPracticeDate).toLocalDate() <= date


}