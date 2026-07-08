package dev.kalpeshmore.spendwise.util

import android.content.Context
import java.text.NumberFormat
import java.util.Locale
import androidx.core.content.edit

/**
 * Centralized currency format management for the SpendWise app.
 *
 * Handles reading/writing the user's preferred currency locale from SharedPreferences,
 * and providing a single source of truth for currency formatting across all screens.
 */
object LocaleHelper {

    private const val PREFS_NAME = "spendwise_prefs"
    private const val KEY_LOCALE = "app_locale"

    data class CurrencyConfig(
        val locale: Locale,
        val countryName: String,
        val currencyCode: String
    ) {
        val displayName: String
            get() = "$countryName ($currencyCode)"
    }

    /**
     * Returns the list of locales/currencies the app supports.
     */
    fun getSupportedCurrencies(): List<CurrencyConfig> = listOf(
        CurrencyConfig(Locale.US, "United States", "USD"),
        CurrencyConfig(Locale.UK, "United Kingdom", "GBP"),
        CurrencyConfig(Locale.forLanguageTag("hi-IN"), "India", "INR"),
        CurrencyConfig(Locale.JAPAN, "Japan", "JPY"),
        CurrencyConfig(Locale.GERMANY, "Germany", "EUR"),
        CurrencyConfig(Locale.FRANCE, "France", "EUR")
    )

    /**
     * Returns the saved IETF language tag (e.g. "en-US"), or null if none saved.
     */
    fun getSavedLocaleTag(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LOCALE, null)
    }

    /**
     * Persists the chosen locale's IETF language tag.
     */
    fun saveLocaleTag(context: Context, tag: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_LOCALE, tag)
            }
    }

    /**
     * Returns the [Locale] for the saved tag, falling back to system default
     * if no preference has been set.
     */
    fun getConfiguredLocale(context: Context): Locale {
        val tag = getSavedLocaleTag(context) ?: return Locale.getDefault()
        return Locale.forLanguageTag(tag)
    }

    /**
     * Single source of truth for currency formatting across the entire app.
     * All screens must use this instead of creating their own NumberFormat instances.
     */
    fun getCurrencyFormatter(context: Context): NumberFormat {
        return NumberFormat.getCurrencyInstance(getConfiguredLocale(context))
    }
}
