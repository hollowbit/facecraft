package uk.co.olbois.facecraft.ui.serverconsole

import android.support.v4.app.Fragment
import android.os.AsyncTask
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import uk.co.olbois.facecraft.R
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection
import uk.co.olbois.facecraft.tasks.RetrieveConsoleOutputTask
import uk.co.olbois.facecraft.tasks.SendServerCommandTask
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class ServerConsoleFragment : Fragment() {

    private lateinit var commandEditText : EditText
    private lateinit var consoleTextView : TextView

    private val serverResponseCallback : (String) -> Unit = {serverResponse ->
        consoleTextView.append("\n$serverResponse")
    }

    private lateinit var serverConnection: ServerConnection
    private var lastId: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_server_console, container, false)

        commandEditText = root.findViewById(R.id.command_EditText)
        commandEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    sendMessage()
                    true
                }
                else -> false
            }
        }
        consoleTextView = root.findViewById(R.id.console_TextView)
        consoleTextView.movementMethod = ScrollingMovementMethod()
        val sendButton = root.findViewById<Button>(R.id.send_Button)

        sendButton.setOnClickListener { _ ->
            sendMessage()
        }

        return root
    }

    fun setServer(serverConnection: ServerConnection) {
        this.serverConnection = serverConnection

        // setup update tasks
        val timer = object : TimerTask() {
            override fun run() {
                val updateTask = RetrieveConsoleOutputTask(lastId, serverConnection.id) {consoleOutputs ->
                    if (consoleOutputs.isNotEmpty())
                        lastId = consoleOutputs.last().id

                    consoleOutputs.forEach { c -> consoleTextView.append("${c.message}\n") }
                }
                updateTask.execute()
            }
        }

        Timer().schedule(timer, 0, 1000)
    }

    private fun sendMessage() {
        SendServerCommandTask(commandEditText.text.toString(), serverConnection.id).execute()
        commandEditText.text.clear()
    }

}
