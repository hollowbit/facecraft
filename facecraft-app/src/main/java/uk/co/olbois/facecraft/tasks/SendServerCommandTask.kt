package uk.co.olbois.facecraft.tasks

import android.os.AsyncTask
import uk.co.olbois.facecraft.model.consoleoutput.ConsoleOutput
import uk.co.olbois.facecraft.server.HttpRequestBuilder

class SendServerCommandTask(private val command: String, private val address: String) : AsyncTask<Void, Void, Unit>() {

    override fun doInBackground(vararg params: Void?) {
        try {
            HttpRequestBuilder("servercommand/send?serverAddress=$address")
                    .method(HttpRequestBuilder.Method.POST)
                    .withRequestBody("text/plain", command.toByteArray())
                    .expectingStatus(200)
                    .perform()
        } catch (ex: Exception){}
    }

}