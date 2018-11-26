package uk.co.olbois.facecraft.ui.serverconsole

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.co.olbois.facecraft.R

/**
 * A placeholder fragment containing a simple view.
 */
class ServerConsoleActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_server_console, container, false)
    }
}
