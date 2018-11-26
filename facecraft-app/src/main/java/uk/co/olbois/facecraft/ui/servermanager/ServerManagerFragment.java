package uk.co.olbois.facecraft.ui.servermanager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import uk.co.olbois.facecraft.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ServerManagerFragment extends Fragment {

    private Button createConnectionButton;
    private Button logoutButton;
    private RecyclerView connectionsRecyclerView;
    private TextView welcomeTextView;

    public ServerManagerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_server_manager, container, false);

        createConnectionButton = root.findViewById(R.id.createConnection_Button);
        logoutButton = root.findViewById(R.id.logout_Button);
        connectionsRecyclerView = root.findViewById(R.id.connections_RecyclerView);
        welcomeTextView = root.findViewById(R.id.welcome_TextView);


        return root;
    }
}
