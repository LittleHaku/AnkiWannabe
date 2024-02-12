interface Drawable {
    fun draw()
    fun report() = println("I am drawable")
}

fun main() {
    open class View(val id: Int) {
        open fun draw() = println("I am view $id")
    }
    class Button(var isButton: Boolean, id: Int) : View(id) {
        override fun draw() = println("I am button $id")
    }








}
