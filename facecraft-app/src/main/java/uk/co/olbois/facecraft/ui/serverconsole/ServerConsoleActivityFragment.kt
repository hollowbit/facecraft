package uk.co.olbois.facecraft.ui.serverconsole

import android.support.v4.app.Fragment
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import uk.co.olbois.facecraft.R
import uk.co.olbois.facecraft.controladapter.TestServerConsoleAdapterImpl

/**
 * A placeholder fragment containing a simple view.
 */
class ServerConsoleActivityFragment : Fragment() {

    private lateinit var commandEditText : EditText
    private lateinit var consoleTextView : TextView

    private val serverResponseCallback : (String) -> Unit = {serverResponse ->
        consoleTextView.append("\n$serverResponse")
    }

    private val serverConsoleAdapter = TestServerConsoleAdapterImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_server_console, container, false)

        commandEditText = root.findViewById<EditText>(R.id.command_EditText)
        commandEditText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    sendMessage()
                    true
                }
                else -> false
            }
        }
        consoleTextView = root.findViewById<TextView>(R.id.console_TextView)
        consoleTextView.movementMethod = ScrollingMovementMethod()
        val sendButton = root.findViewById<Button>(R.id.send_Button)

        sendButton.setOnClickListener { _ ->
            sendMessage()
        }

        return root
    }

    private fun sendMessage() {
        serverConsoleAdapter.sendMessageToServer(commandEditText.text.toString(), serverResponseCallback)
        commandEditText.text.clear()
    }

}
