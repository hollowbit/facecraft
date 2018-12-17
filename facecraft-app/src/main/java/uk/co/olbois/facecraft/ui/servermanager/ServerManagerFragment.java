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
 * A placeholder fragment containing a simple view.
 */
public class ServerManagerFragment extends Fragment {
    public interface OnLoggedOutListener{
        void onLoggedOut();
    }

    public interface OnConnectListener{
        void onConnect(ServerConnection connection, SampleUser user);
    }

    private Button createConnectionButton;
    private Button logoutButton;
    private RecyclerView connectionsRecyclerView;
    private TextView welcomeTextView;
    private UniversalDatabaseHandler udbh;
    private List<ServerConnection> connections;
    private SampleUser user;

    private OnLoggedOutListener onLoggedOutListener;
    private OnConnectListener onConnectListener;

    public void setOnLoggedOutListener(OnLoggedOutListener onLoggedOutListener){
        this.onLoggedOutListener = onLoggedOutListener;
    }

    public void setOnConnectListener(OnConnectListener onConnectListener){
        this.onConnectListener = onConnectListener;
    }

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

        udbh = new UniversalDatabaseHandler(getContext());

        //Set up recycler view
        connectionsRecyclerView.setHasFixedSize(true);
        connectionsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        connectionsRecyclerView.setAdapter(new ConnectionsAdapter());

        //refresh it
        connectionsRecyclerView.getAdapter().notifyDataSetChanged();

        setUpLoggedOutButton();
        setUpCreateConnection();

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
    //creates a alert dialog to create a new connection (SQLite only for now)
    private void setUpCreateConnection(){
        /*
        createConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up alert dialog basic information
                final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Create Connection!");
                alertDialog.setMessage("Enter Domain : ");

                //The view to show is an EditText instead of a textview
                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp  = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.rounded);

                //set up Create button on Alert Dialog
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String connectionString = input.getText().toString();

                        //create the connection locally, then create it on sqlite db
                        ServerConnection conn = new ServerConnection();
                        conn.setName(connectionString);
                        conn.setRole(ServerConnection.Role.OWNER);
                        conn.setUserCount(1);
                        conn.setUserId(user.getId());


                        try {
                            List<ServerConnection> allServers = udbh.getConnectionsTable().readAll();

                            boolean found = false;
                            for(ServerConnection c : allServers){
                                if(c.getName().toLowerCase().equals(conn.getName().toLowerCase())){
                                    found = true;
                                    break;
                                }
                            }

                            if(!found || conn.getName() == null){
                                udbh.getConnectionsTable().create(conn);
                                refreshList();
                            }
                            else{
                                Toast.makeText(getContext(), "That server connection already exists!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing more to do, exit out of alert dialog
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });
        */
    }

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

    public void setUser(SampleUser u){
        this.user = u;

        welcomeTextView.setText("Welcome, " + user.getUsername() + " !");

        refreshList();
    }

    public void refreshList(){
        if(user == null)
            return;

        //create a new connections list, dont populate old one
        connections = new ArrayList<ServerConnection>();
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
        /*
        try {
            //get all connections and populate in-data connections array
            List<ServerConnection> unfilteredConnections = udbh.getConnectionsTable().readAll();
            for(ServerConnection connection : unfilteredConnections){
                if(connection.getUserId() == user.getId())
                    connections.add(connection);
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        //refresh adapter!
        */
    }
}
