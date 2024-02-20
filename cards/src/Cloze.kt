class Cloze(question: String, answer: String) : Card(question, answer) {
    init {
        require(question.count { it == '*' } == 2) { "Question must contain exactly two asterisks" }
    }

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

            return Cloze(question, answer, date, id, quality, repetitions, interval, nextPracticeDate, easiness)
        }
    }

    // Secondary constructor to rebuild the cards
    constructor(
        question: String,
        answer: String,
        date: String,
        id: String,
        quality: Int,
        repetitions: Int,
        interval: Long,
        nextPracticeDate: String,
        easiness: Double
    ) : this(
        question,
        answer
    )

    override fun toString(): String {
        return "cloze | ${this.question} | ${this.answer} | ${this.date} | ${this.id} | ${this.quality} | ${this.repetitions} | ${this.interval} | ${this.nextPracticeDate} | ${this.easiness}"
    }

    override fun show() {
        print("\n${this.question} (ENTER to see answer)")
        readln()
        val questionWithAnswer = this.question.run {
            val start = indexOf('*')
            val end = lastIndexOf('*')
            if (start != -1 && end != -1 && start < end) {
                replaceRange(start, end + 1, answer)
            } else {
                this
            }
        }
        print("$questionWithAnswer (Type 0 -> Difficult 3 -> Doubt 5 -> Easy): ")
        val quality = readlnOrNull()?.toIntOrNull() ?: -1
        if (quality == 0 || quality == 3 || quality == 5)
            this.quality = quality
        else
            println("Not a valid difficulty")
    }
}
