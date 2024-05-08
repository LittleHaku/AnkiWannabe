package es.uam.eps.dadm.cards

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val MAX_NUMBER_CARDS_KEY = "max_number_cards"
        private const val MAX_NUMBER_CARDS_DEFAULT = "20"
        private const val LOGGED_IN_KEY = "logged_in_key"
        private const val SHOW_ANSWERS = "show_answers"
        private const val SHOW_ANSWERS_DEFAULT = true

        fun getMaximumNumberOfCards(context: Context): Int {
            return PreferenceManager.getDefaultSharedPreferences(context)
                // Uses default if the input isn't a number
                .getString(MAX_NUMBER_CARDS_KEY, MAX_NUMBER_CARDS_DEFAULT)?.toIntOrNull()
                ?: MAX_NUMBER_CARDS_DEFAULT.toInt()
        }

        fun setLoggedIn(context: Context, loggedin: Boolean) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putBoolean(LOGGED_IN_KEY, loggedin)
            editor.apply()
        }

        fun getShowAnswers(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SHOW_ANSWERS, SHOW_ANSWERS_DEFAULT)
        }
    }
}