package uk.co.olbois.facecraft.ui.servermanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import uk.co.olbois.facecraft.sqlite.DatabaseException;

/**
 * A placeholder fragment containing a simple view.
 */
public class ServerManagerFragment extends Fragment {
    public interface OnLoggedOutListener{
        void onLoggedOut();
    }

    public interface OnConnectListener{
        void onConnect(ServerConnection connection);
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

        connectionsRecyclerView.setHasFixedSize(true);
        connectionsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        connectionsRecyclerView.setAdapter(new ConnectionsAdapter());

        connectionsRecyclerView.getAdapter().notifyDataSetChanged();

        setUpLoggedOutButton();
        setUpCreateConnection();
        return root;
    }

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
    private void setUpCreateConnection(){
        createConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Create Connection!");
                alertDialog.setMessage("Enter Domain : ");
                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp  = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.rounded);

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String connectionString = input.getText().toString();

                        ServerConnection conn = new ServerConnection();
                        conn.setHost(connectionString);
                        conn.setRole(ServerConnection.Role.OWNER);
                        conn.setUserCount(1);
                        conn.setUserId(user.getId());

                        try {
                            udbh.getConnectionsTable().create(conn);
                            refreshList();
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });
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
        }

        public void setConnection(final ServerConnection currentConnection){
            serverInformationTextView.setText("Server : " + currentConnection.getHost() + ":" + currentConnection.getPort());
            userCountTextView.setText("User Count : " + currentConnection.getUserCount());
            roleTextView.setText("Role : " + currentConnection.getRole());

            accessButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if(onConnectListener == null)
                        return;
                    onConnectListener.onConnect(currentConnection);
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

        connections = new ArrayList<ServerConnection>();
        try {
            List<ServerConnection> unfilteredConnections = udbh.getConnectionsTable().readAll();
            for(ServerConnection connection : unfilteredConnections){
                if(connection.getUserId() == user.getId())
                    connections.add(connection);
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        connectionsRecyclerView.getAdapter().notifyDataSetChanged();
    }
}
