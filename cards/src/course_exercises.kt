import java.io.File

class Country(var name: String, var beer: Int, var spirits: Int, var wine: Int)

fun main() {
    val lines: List<String> = File("cards/data/drinks.txt").readLines()
    val countries: MutableList<Country> = mutableListOf()
    var chunks: List<String>
    var name: String
    var beer: Int
    var spirits: Int
    var wine: Int

    for (line in lines) {
        chunks = line.split(",")
        name = chunks[0]
        beer = chunks[1].toInt()
        spirits = chunks[2].toInt()
        wine = chunks[3].toInt()
        countries += Country(name, beer, spirits, wine)
    }

    fun selector(country: Country) : Int {
        return maxOf(country.beer, country.spirits, country.wine)
    }
    countries.sortByDescending { selector(it) }
    countries.forEach { println("${it.name} : ${it.beer} ${it.spirits} ${it.wine} ") }
}