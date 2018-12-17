package uk.co.olbois.facecraft.ui.servermanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.UniversalDatabaseHandler;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.HttpProgress;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.sqlite.DatabaseException;
import uk.co.olbois.facecraft.tasks.RetrieveUserServersTask;

/**
 * This is the fragment where you see all of the connections you are a part of
 *
 * This is effectively a way point into the Hub Activity, where you can get to
 * the main part of this project!
 */
public class ServerManagerFragment extends Fragment {
    public interface OnLoggedOutListener{
        void onLoggedOut();
    }

    public interface OnConnectListener{
        void onConnect(ServerConnection connection, SampleUser user);
    }

    public interface OnManageInviteClickedListener{
        void onManageInviteClicked(SampleUser u);
    }

    private Button manageInvitesButton;
    private Button logoutButton;
    private RecyclerView connectionsRecyclerView;
    private TextView welcomeTextView;
    private UniversalDatabaseHandler udbh;
    private List<ServerConnection> connections;
    private SampleUser user;

    private OnLoggedOutListener onLoggedOutListener;
    private OnConnectListener onConnectListener;
    private OnManageInviteClickedListener onManageInviteClickedListener;

    public void setOnLoggedOutListener(OnLoggedOutListener onLoggedOutListener){
        this.onLoggedOutListener = onLoggedOutListener;
    }

    public void setOnConnectListener(OnConnectListener onConnectListener){
        this.onConnectListener = onConnectListener;
    }

    public void setOnManageInviteClickedListener(OnManageInviteClickedListener onManageInviteClickedListener){
        this.onManageInviteClickedListener = onManageInviteClickedListener;
    }

    public ServerManagerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_server_manager, container, false);

        manageInvitesButton = root.findViewById(R.id.manageInvite_Button);
        logoutButton = root.findViewById(R.id.logout_Button);
        connectionsRecyclerView = root.findViewById(R.id.connections_RecyclerView);
        welcomeTextView = root.findViewById(R.id.welcome_TextView);

        udbh = new UniversalDatabaseHandler(getContext());

        //Set up recycler view
        connectionsRecyclerView.setHasFixedSize(true);
        connectionsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        connectionsRecyclerView.setAdapter(new ConnectionsAdapter());

        //refresh it
        connectionsRecyclerView.getAdapter().notifyDataSetChanged();

        setUpLoggedOutButton();
        setUpManageInviteButton();

        return root;
    }

    //Creates a clickListener for logout that throws a onLoggedOut custom event.
    private void setUpLoggedOutButton(){
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onLoggedOutListener == null)
                    return;
                onLoggedOutListener.onLoggedOut();
            }
        });
    }

    private void setUpManageInviteButton(){
        manageInvitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onManageInviteClickedListener == null)
                    return;
                onManageInviteClickedListener.onManageInviteClicked(user);
            }
        });
    }

    /**
     * Private inner class of a View Holder, set up for the connections so that we may
     * inflate them appropriately with the correct information
     */
    private class ConnectionsViewHolder extends RecyclerView.ViewHolder{

        private TextView serverInformationTextView;
        private TextView userCountTextView;
        private TextView roleTextView;
        private Button accessButton;
        public ConnectionsViewHolder(@NonNull View itemView) {
            super(itemView);
            serverInformationTextView = itemView.findViewById(R.id.serverInformation_TextView);
            userCountTextView = itemView.findViewById(R.id.userCount_TextView);
            roleTextView = itemView.findViewById(R.id.role_TextView);
            accessButton = itemView.findViewById(R.id.access_Button);
            serverInformationTextView.setSelected(true);
            serverInformationTextView.setHorizontallyScrolling(true);
        }

        /**
         * This is the function that binds the data to the view elements.
         * @param currentConnection
         */
        public void setConnection(final ServerConnection currentConnection){
            //set Ui elements based on particular connection
            serverInformationTextView.setText("Server : " + currentConnection.getName());
            userCountTextView.setText("User Count : " + currentConnection.getUserCount());
            roleTextView.setText("Role : " + currentConnection.getRole());

            //set up access button event handler, sends a connection to HubActivity
            accessButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if(onConnectListener == null)
                        return;
                    onConnectListener.onConnect(currentConnection, user);
                }
            });
        }
    }

    /**
     * private inner class of an adapter for our connections recycler view, this is what
     * inflates the views based on a viewHolder.
     */
    private class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsViewHolder>{

        @NonNull
        @Override
        public ConnectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_connection, parent, false);
            ConnectionsViewHolder vh = new ConnectionsViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ConnectionsViewHolder connectionsViewHolder, int position) {
            connectionsViewHolder.setConnection(connections.get(position));
        }

        @Override
        public int getItemCount() {
            return connections.size();
        }
    }

    /**
     * Generally called when the fragment is being created, this initializes the user field
     * in this fragment so that we can retrieve appropriate data later on.
     * @param u
     */
    public void setUser(SampleUser u){
        this.user = u;

        welcomeTextView.setText("Welcome, " + user.getUsername() + " !");
        refreshList();
    }

    /**
     * This function refreshes the list of servers by pulling data from the database!
     */
    public void refreshList(){
        if(user == null)
            return;

        //create a new connections list, dont populate old one
        connections = new ArrayList<ServerConnection>();
        //This asynchronous task retrieves all the servers the user is a part of!
        RetrieveUserServersTask retrieveUserServersTask = new RetrieveUserServersTask("/users/" + user.getId(), new OnResponseListener<List<ServerConnection>>() {
            @Override
            public void onResponse(List<ServerConnection> data) {
                connections.addAll(data);
                connectionsRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onProgress(HttpProgress value) {

            }

            @Override
            public void onError(Exception error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        retrieveUserServersTask.execute();
    }
}
