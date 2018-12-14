package uk.co.olbois.facecraft.ui.invite;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.UniversalDatabaseHandler;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.sqlite.DatabaseException;

public class InviteFragment extends Fragment {

    private ServerConnection connection;
    private TextView serverTextView;
    private RecyclerView usersRecyclerView;
    private RecyclerView currentUsersRecyclerView;
    private UniversalDatabaseHandler udbh;
    private List<SampleUser> users;
    private List<Pair<SampleUser, ServerConnection>> currentConnections;
    private ServerConnection.Role[] rolePossibilities;

    private FirebaseFirestore mFirestore;

    public InviteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_invite, container, false);

        //retrieve UI elements
        serverTextView = root.findViewById(R.id.currentServer_TextView);
        usersRecyclerView = root.findViewById(R.id.users_RecyclerView);
        currentUsersRecyclerView = root.findViewById(R.id.currentUsers_RecyclerView);

        udbh = new UniversalDatabaseHandler(getContext());

        //set up recycler view
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        usersRecyclerView.setAdapter(new UsersAdapter());

        usersRecyclerView.getAdapter().notifyDataSetChanged();


        currentUsersRecyclerView.setHasFixedSize(true);
        currentUsersRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        currentUsersRecyclerView.setAdapter(new CurrentUsersAdapter());

        currentUsersRecyclerView.getAdapter().notifyDataSetChanged();
        rolePossibilities = new ServerConnection.Role[]{ServerConnection.Role.MEMBER, ServerConnection.Role.ADMIN};

        mFirestore = FirebaseFirestore.getInstance();

        return root;
    }

    //A Users View Holder, effectively takes in a user, and inflates a view using the users information!
    private class UsersViewHolder extends RecyclerView.ViewHolder{

        private TextView usernameTextView;
        private Button inviteButton;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_TextView);
            inviteButton = itemView.findViewById(R.id.invite_Button);
        }

        //Set user for this particular view, this assigns the values to the textviews AND sets up the invite button
        public void setUser(final SampleUser currentUser){
            usernameTextView.setText("User : " + currentUser.getUsername());

            inviteButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    //update old connection
                    connection.setUserCount(connection.getUserCount() + 1);
                    //create new connection with old connections data + new user
                    ServerConnection c = new ServerConnection();
                    c.setUserId(currentUser.getId());
                    c.setUserCount(connection.getUserCount());
                    c.setRole(ServerConnection.Role.MEMBER);
                    c.setHost(connection.getHost());
                    c.setPort(connection.getPort());

                    Map<String, Object> invite = new HashMap<>();
                    invite.put("to", currentUser.getUsername());
                    invite.put("device_token", currentUser.getDeviceToken());
                    invite.put("server", connection.getHost() + connection.getPort());

                    mFirestore.collection("invites")
                            .add(invite)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getContext(), "Snapshot with id " + documentReference.getId() , Toast.LENGTH_SHORT).show();
                                }
                            });

                    try {
                        //create and update on database
                        udbh.getConnectionsTable().create(c);
                        udbh.getConnectionsTable().update(connection);
                        //refresh list
                        currentConnections.add(new Pair<>(currentUser, c));
                        users.remove(currentUser);
                        usersRecyclerView.getAdapter().notifyDataSetChanged();
                        currentUsersRecyclerView.getAdapter().notifyDataSetChanged();
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder>{

        @NonNull
        @Override
        public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
            UsersViewHolder vh = new UsersViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull UsersViewHolder connectionsViewHolder, int position) {
            connectionsViewHolder.setUser(users.get(position));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
    //A Users View Holder, effectively takes in a user, and inflates a view using the users information!
    private class CurrentUsersViewHolder extends RecyclerView.ViewHolder{

        private TextView usernameTextView;
        private TextView roleTextView;
        private Button removeButton;
        private Spinner roleSpinner;
        private int check;
        public CurrentUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_TextView);
            roleSpinner = itemView.findViewById(R.id.roles_Spinner);
            removeButton = itemView.findViewById(R.id.remove_Button);
            roleTextView = itemView.findViewById(R.id.role_TextView);
            check = 0;
        }

        //Set user for this particular view, this assigns the values to the textviews AND sets up the invite button
        public void setUser(final Pair<SampleUser, ServerConnection> currentValues){
            usernameTextView.setText("User : " + currentValues.first.getUsername());
            roleTextView.setText("Role : " + currentValues.second.getRole().toString());

            if(connection.getRole().compareTo(currentValues.second.getRole()) <= 0){
                removeButton.setEnabled(false);
            }

            ArrayAdapter<ServerConnection.Role> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, rolePossibilities);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            roleSpinner.setAdapter(adapter);

            if(connection.getRole() != ServerConnection.Role.OWNER){
                roleSpinner.setEnabled(false);
                roleSpinner.setVisibility(View.INVISIBLE);
            }


            removeButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    users.add(currentValues.first);
                    currentConnections.remove(currentValues);
                    connection.setUserCount(connection.getUserCount()-1);

                    try {
                        udbh.getConnectionsTable().update(connection);
                        udbh.getConnectionsTable().delete(currentValues.second);
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                    }

                    usersRecyclerView.getAdapter().notifyDataSetChanged();
                    currentUsersRecyclerView.getAdapter().notifyDataSetChanged();
                }
            });

            roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    if(++check > 1){
                        currentValues.second.setRole(rolePossibilities[position]);
                        try {
                            udbh.getConnectionsTable().update(currentValues.second);
                            roleTextView.setText("Role : " + currentValues.second.getRole().toString());
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), rolePossibilities[position].toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private class CurrentUsersAdapter extends RecyclerView.Adapter<CurrentUsersViewHolder>{

        @NonNull
        @Override
        public CurrentUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_current_user, parent, false);
            CurrentUsersViewHolder vh = new CurrentUsersViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull CurrentUsersViewHolder usersViewHolder, int position) {
            usersViewHolder.setUser(currentConnections.get(position));
        }

        @Override
        public int getItemCount() {
            return currentConnections.size();
        }
    }



    //Takes in a connection, and sets it for this particular fragment
    //Also creates a list of users not in the sent in connection
    public void setConnection(ServerConnection c){
        this.connection = c;

        //set UI texts
        serverTextView.setText("Server : " + connection.getHost() + " !");

        //new list, dont populate old one
        this.users = new ArrayList<>();
        this.currentConnections = new ArrayList<>();

        //get connections and users from sqlite db
        try {
            List<ServerConnection> allConnections = udbh.getConnectionsTable().readAll();
            List<SampleUser> unfilteredUsers = udbh.getSampleUserTable().readAll();

            //Go through each user
            for(SampleUser u : unfilteredUsers){
                boolean found = false;
                //go through each connection
                for(ServerConnection conn : allConnections){
                    //if the user has a connection = to the current connection, found
                    if(u.getId() == conn.getUserId() && conn.getHost().equals(this.connection.getHost())){
                        found = true;
                        if(u.getId() != connection.getUserId())
                            currentConnections.add(new Pair<>(u, conn));
                        break;
                    }
                }

                //check if user isn't in the connection
                if(!found)
                    users.add(u);
            }

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        if(usersRecyclerView != null){
            usersRecyclerView.getAdapter().notifyDataSetChanged();
            currentUsersRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

}
