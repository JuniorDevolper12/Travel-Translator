import android.os.AsyncTask
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

 class TranslateTask(
    private val sourceLang: String,
    private val targetLang: String,
    private val text: String,
    private val callback: (String?) -> Unit
) : AsyncTask<Void, Void, String?>() {

    override fun doInBackground(vararg params: Void?): String? {
        var result: String? = null
        try {
            val url = URL("https://deep-translate1.p.rapidapi.com/language/translate/v2")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("x-rapidapi-key", "7c6c88394bmsh78748b39ef90971p16b981jsn5c205dc0e5e0")
            connection.setRequestProperty("x-rapidapi-host", "deep-translate1.p.rapidapi.com")
            connection.setRequestProperty("Content-Type", "application/json")

            val jsonBody = JSONObject().apply {
                put("q", text)
                put("source", sourceLang)

                put("target",targetLang)
            }.toString()

            connection.doOutput = true
            connection.outputStream.write(jsonBody.toByteArray())
            connection.outputStream.flush()
            connection.outputStream.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(inputStream)
                val data = jsonResponse.getJSONObject("data")
                val translations = data.getJSONObject("translations") // Changed from JSONArray to JSONObject
                result = translations.getString("translatedText")
            } else {
                result = "Error: $responseCode"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result = "Translation failed"
        }
        return result
    }

    override fun onPostExecute(result: String?) {
        callback(result)
    }
}
