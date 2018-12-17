package uk.co.olbois.facecraft.ui.invite;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.UniversalDatabaseHandler;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.HttpProgress;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.tasks.ChangeUserRoleTask;
import uk.co.olbois.facecraft.tasks.RemoveUserFromServerTask;
import uk.co.olbois.facecraft.tasks.RetrieveUserListTask;
import uk.co.olbois.facecraft.tasks.SendInvitationTask;

public class InviteFragment extends Fragment {

    private ServerConnection connection;
    private SampleUser user;
    private TextView serverTextView;
    private RecyclerView usersRecyclerView;
    private RecyclerView currentUsersRecyclerView;
    private UniversalDatabaseHandler udbh;
    private List<SampleUser> users;
    private List<SampleUser> currentConnections;
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

        //set up recycler view for users not in the server
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        usersRecyclerView.setAdapter(new UsersAdapter());
        usersRecyclerView.getAdapter().notifyDataSetChanged();


        //set up recycler view for users in the server
        currentUsersRecyclerView.setHasFixedSize(true);
        currentUsersRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        currentUsersRecyclerView.setAdapter(new CurrentUsersAdapter());

        currentUsersRecyclerView.getAdapter().notifyDataSetChanged();
        rolePossibilities = new ServerConnection.Role[]{ServerConnection.Role.MEMBER, ServerConnection.Role.OWNER};

        mFirestore = FirebaseFirestore.getInstance();

        return root;
    }

    /**
     * A view holder to allow us to bind specific user data to a view in our recycler view
     */
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

                    //This sends an invite to a specific user in the springio database, ALSO sends a document
                    // to the firebase database!
                    SendInvitationTask sendInvitationTask = new SendInvitationTask("/invites", connection, new OnResponseListener<Boolean>() {
                        @Override
                        public void onResponse(Boolean data) {
                            users.remove(currentUser);
                            usersRecyclerView.getAdapter().notifyDataSetChanged();
                            Toast.makeText(getContext(), "sent", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgress(HttpProgress value) {

                        }

                        @Override
                        public void onError(Exception error) {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    sendInvitationTask.execute(user, currentUser);
                }
            });
        }
    }

    /**
     * An Adapter for users that aren't in our server connection, this inflates a view based on
     * a particular view holder
     */
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

    /**
     * A View Holder for users that ARE in our server connection, this binds a particular user's
     * data with a view.
     */
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
        public void setUser(final SampleUser currentValues){
            check = 0;
            usernameTextView.setText("User : " + currentValues.getUsername());
            roleTextView.setText("Role : " + currentValues.getRole().toString());

            //Is our role above their role? If not, disable the buttons!
            if(connection.getRole().compareTo(currentValues.getRole()) <= 0){
                removeButton.setEnabled(false);
                roleSpinner.setEnabled(false);
            }

            ArrayAdapter<ServerConnection.Role> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, rolePossibilities);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            roleSpinner.setAdapter(adapter);

            //if we aren't the owner of a server, we can't change a users role!
            if(connection.getRole() != ServerConnection.Role.OWNER){
                roleSpinner.setEnabled(false);
                roleSpinner.setVisibility(View.INVISIBLE);
            }


            removeButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    //This task deletes a member from the members list on the database.
                    RemoveUserFromServerTask removeUserFromServerTask = new RemoveUserFromServerTask("/servers/" + connection.getId() + "/members", new OnResponseListener<Boolean>() {
                        @Override
                        public void onResponse(Boolean data) {
                            //Refresh the lists locally instead of querying the server again
                            users.add(currentValues);
                            currentConnections.remove(currentValues);

                            //Refresh those recycler views!
                            usersRecyclerView.getAdapter().notifyDataSetChanged();
                            currentUsersRecyclerView.getAdapter().notifyDataSetChanged();
                        }

                        @Override
                        public void onProgress(HttpProgress value) {

                        }

                        @Override
                        public void onError(Exception error) {
                            Toast.makeText(getContext(),error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    removeUserFromServerTask.execute(currentValues);
                }
            });

            //Set up the role Spinner action for when an item is selected in the spinner
            roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    if(++check > 1){

                        //This task runs and changes a user's role in our database, really only used for changing a
                        //member to a owner.
                        ChangeUserRoleTask changeUserRoleTask = new ChangeUserRoleTask(rolePossibilities[position], connection, new OnResponseListener<Boolean>() {
                            @Override
                            public void onResponse(Boolean data) {
                                //set the variables locally instead of making another request to the server
                                currentValues.setRole(rolePossibilities[position]);
                                roleTextView.setText("Role : " + currentValues.getRole().toString());
                                removeButton.setEnabled(false);
                                roleSpinner.setEnabled(false);
                            }

                            @Override
                            public void onProgress(HttpProgress value) {

                            }

                            @Override
                            public void onError(Exception error) {
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        changeUserRoleTask.execute(currentValues);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    /**
     * An Adapter for users that ARE in our server connection, this effectively inflates a view
     * based on a view holder while supplying the user to the view holder
     */
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
    public void setConnectionAndUser(ServerConnection c, SampleUser u){
        this.connection = c;
        this.user = u;

        //set UI texts
        serverTextView.setText("Server : " + connection.getName() + " !");

        //new list, dont populate old one
        this.users = new ArrayList<>();
        this.currentConnections = new ArrayList<>();

        //This task retrieves all users from the database that are in our current server!
        RetrieveUserListTask retrieveUserListTask = new RetrieveUserListTask("/servers/" + connection.getId(), new OnResponseListener<List<SampleUser>>() {
            @Override
            public void onResponse(List<SampleUser> data) {
                for(SampleUser u : data){
                    //users who are not in our server added to users list
                    if(u.getRole() == ServerConnection.Role.OTHER){
                        users.add(u);
                    }
                    else{
                        //These users are in our server!
                        currentConnections.add(u );
                    }
                }

                //Refresh the recyclerviews!
                if(usersRecyclerView != null){
                    usersRecyclerView.getAdapter().notifyDataSetChanged();
                    currentUsersRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onProgress(HttpProgress value) {

            }

            @Override
            public void onError(Exception error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        retrieveUserListTask.execute();
    }
}
