package uk.co.olbois.facecraft.ui.servermanager;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.UniversalDatabaseHandler;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.sqlite.DatabaseException;

/**
 * A placeholder fragment containing a simple view.
 */
public class ServerManagerFragment extends Fragment {

    private Button createConnectionButton;
    private Button logoutButton;
    private RecyclerView connectionsRecyclerView;
    private TextView welcomeTextView;
    private UniversalDatabaseHandler udbh;
    private List<ServerConnection> connections;

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

        try {
            connections = udbh.getConnectionsTable().readAll();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        connectionsRecyclerView.setHasFixedSize(true);
        connectionsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        connectionsRecyclerView.setAdapter(new ConnectionsAdapter());

        connectionsRecyclerView.getAdapter().notifyDataSetChanged();
        return root;
    }

    private void setupCreateConnection(){
        createConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        public void setConnection(ServerConnection currentConnection){
            serverInformationTextView.setText("Server : " + currentConnection.getHost() + ":" + currentConnection.getPort());
            userCountTextView.setText("User Count : " + currentConnection.getUserCount());
            roleTextView.setText("Role : " + currentConnection.getRole());

            accessButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Connecting!", Toast.LENGTH_LONG).show();
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
}
