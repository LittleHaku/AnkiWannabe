interface ButtonListener {
    fun onClicked()
}

class Button {
    lateinit private var listener: ButtonListener
    fun addListener(e: ButtonListener) {
        listener = e
    }

    fun click() = listener.onClicked()
}

fun main() {
    var button = Button()

    button.addListener(object : ButtonListener {
        override fun onClicked() = println("The button has been clicked")
    })

    button.addListener(object : ButtonListener {
        override fun onClicked() = println("I've been clicked but now I'm behaving differently")
    })

    button.click()
}