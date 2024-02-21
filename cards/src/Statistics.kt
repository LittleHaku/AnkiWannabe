import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.*
import java.time.format.DateTimeFormatter

fun histogram(cards: List<Card>) {
    val groups = cards.groupBy {
        // i dont understand why in the course it is writen like this:
        // arent we risking if we do this at the end of the year that we go from 365 to 1 so the sort will fail??

        //parse(it.nextPracticeDate).dayOfYear

        // since we are using Local Date Time i cant parse it directly bc we have time, this way i just get the date
        // without the time
        val dayOfYear = parse(it.nextPracticeDate).dayOfYear
        val year = parse(it.nextPracticeDate).year
        LocalDate.ofYearDay(year, dayOfYear)
    }

    val sorted = groups.toSortedMap()

    val minDate = sorted.keys.minOrNull() ?: LocalDate.now()
    val maxDate = sorted.keys.maxOrNull() ?: LocalDate.now()

    var currentDate = minDate
    while (!currentDate.isAfter(maxDate)) {
        val size = sorted[currentDate]?.size ?: 0
        println("$currentDate \t -> $size")
        currentDate = currentDate.plusDays(1)
    }

}


fun simulate(
    daysAgo: Long,
    numberOfCards: Long,
    pEasy: Double,
    pDoubt: Double,
    pHard: Double
): List<Card> {
    // Create a list of cards with appropriate dates
    var date = now().minusDays(daysAgo)
    val cards: MutableList<Card> = mutableListOf()
    // val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    for (i in (0 until numberOfCards)) {
        val card = Card("$i", "$i")
        card.date = date.toString()
        card.nextPracticeDate = card.date
        cards.add(card)
    }

    // Simulation until the current date
    repeat((1..daysAgo).count()) {
        // Spaced repetition of the set of cards
        for (c in cards) {
            if (date >= parse(c.nextPracticeDate)) {
                c.quality = assignQuality(pEasy, pDoubt, pHard)
                c.update(date)
            }
        }

        date = date.plusDays(1)
    }

    return cards
}

fun assignQuality(
    pEasy: Double,
    pDoubt: Double,
    pHard: Double
): Int {
    val random = Math.random()
    return when {
        random < pEasy -> 5
        random < pEasy + pDoubt -> 3
        else -> 0
    }
}