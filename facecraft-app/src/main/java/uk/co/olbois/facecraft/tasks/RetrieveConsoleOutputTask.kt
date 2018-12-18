package uk.co.olbois.facecraft.tasks

import android.os.AsyncTask
import android.util.Log
import uk.co.olbois.facecraft.model.consoleoutput.ConsoleOutput
import uk.co.olbois.facecraft.server.HttpRequestBuilder

class RetrieveConsoleOutputTask(private val lastId: Long, private val address: String, private val onResponseListener: (Array<ConsoleOutput>) -> Unit) : AsyncTask<Void, Void, Array<ConsoleOutput>?>() {


    override fun doInBackground(vararg params: Void?): Array<ConsoleOutput>? {
        try {
            val response = HttpRequestBuilder("consoleoutputs/search/findAllSinceLastByServer?lastId=$lastId&serverAddress=$address")
                    .method(HttpRequestBuilder.Method.GET)
                    .expectingStatus(200)
                    .perform()
            return ConsoleOutput.parseArray(String(response.content))
        } catch (ex: Exception){}

        return null
    }

    override fun onPostExecute(result: Array<ConsoleOutput>?) {
        super.onPostExecute(result)
        if (result != null) {
            onResponseListener.invoke(result)
        }
    }

}
