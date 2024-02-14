class Cloze(question: String, answer: String) : Card(question, answer) {
    init {
        require(question.count { it == '*' } == 2) { "Question must contain exactly two asterisks" }
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
