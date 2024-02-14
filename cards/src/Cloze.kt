class Cloze(question: String, answer: String) : Card(question, answer) {
    override fun show() {
        print("\n${this.question} (ENTER to see answer)")
        readlnOrNull()
        val questionWithAnswer = this.question.run {
            val start = this.indexOf('*')
            val end = this.lastIndexOf('*')
            if (start != -1 && end != -1 && start < end) {
                this.replaceRange(start, end + 1, answer)
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
