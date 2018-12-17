package uk.co.olbois.facecraft.ui.serverconsole

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import uk.co.olbois.facecraft.R

import kotlinx.android.synthetic.main.activity_server_console.*

class ServerConsoleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_console)
        setSupportActionBar(toolbar)
    }

}
