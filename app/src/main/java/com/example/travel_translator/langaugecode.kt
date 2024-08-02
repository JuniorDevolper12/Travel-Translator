import java.util.Locale

class LanguageHelper {
     fun getLanguageCode(languageName: String): String? {
        val locale = Locale.getAvailableLocales().find { it.displayLanguage.equals(languageName, ignoreCase = true) }
        return locale?.language
    }
     fun getAllLanguages(): List<String> {
        val locales = Locale.getAvailableLocales()
        val languagesSet = mutableSetOf<String>()

        for (locale in locales) {
            val language = locale.displayLanguage
            if (language.isNotBlank()) {
                languagesSet.add(language)
            }
        }

        // Return the sorted list of unique languages
        return languagesSet.sorted()
    }

}
