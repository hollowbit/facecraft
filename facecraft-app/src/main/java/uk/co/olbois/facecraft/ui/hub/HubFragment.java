package uk.co.olbois.facecraft.ui.hub;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.ui.login.LoginFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class HubFragment extends Fragment {

    public interface OnFragmentChosenListener{
        void onFragmentChosen(ServerConnection c, SampleUser u, String fragment);
    }

    private OnFragmentChosenListener onFragmentChosenListener;
    private SampleUser user;
    private ServerConnection connection;
    private Button inviteButton;
    private Button serverConsoleButton;

    public HubFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_hub, container, false);

        // get all buttons
        Button chatButton = view.findViewById(R.id.button_chatroom);
        inviteButton = view.findViewById(R.id.button_invite);
        serverConsoleButton = view.findViewById(R.id.button_server_manager);
        Button eventButton = view.findViewById(R.id.button_event);

        // set button on click listeners
        // On a button click, start the specified fragment

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentChosenListener.onFragmentChosen(connection, user, "chat");
            }
        });

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentChosenListener.onFragmentChosen(connection, user, "invite");
            }
        });

        serverConsoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentChosenListener.onFragmentChosen(connection, user, "server");
            }
        });

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentChosenListener.onFragmentChosen(connection, user, "event");
            }
        });

        return view;
    }

    public void setOnFragmentChosenListener( OnFragmentChosenListener onFragmentChosenListener){
        this.onFragmentChosenListener = onFragmentChosenListener;
    }

    public void setUser(SampleUser u) {
        this.user = u;
    }
    public void setConnection(ServerConnection c) {
        this.connection = c;
        if(connection.getRole() == ServerConnection.Role.MEMBER) {
            inviteButton.setEnabled(false);
            serverConsoleButton.setEnabled(false);
        }
    }

    public SampleUser getUser() {
        return user;
    }
}
