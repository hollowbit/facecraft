package uk.co.olbois.facecraft.ui.serverconsole

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import uk.co.olbois.facecraft.R

import kotlinx.android.synthetic.main.activity_server_console.*

class ServerConsoleActivity : AppCompatActivity() {

    companion object {
        const val INITIAL_CONNECTION = "initial_connection"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_console)
        setSupportActionBar(toolbar)

        val serverConsoleFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ServerConsoleFragment?
        serverConsoleFragment?.setServer(intent.getParcelableExtra(INITIAL_CONNECTION))
    }

}
