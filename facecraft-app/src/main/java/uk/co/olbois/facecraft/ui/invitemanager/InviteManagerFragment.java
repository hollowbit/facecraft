package uk.co.olbois.facecraft.ui.invitemanager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.Invite;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.HttpProgress;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.tasks.HandleInviteTask;
import uk.co.olbois.facecraft.tasks.RetrieveUserInvitesTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class InviteManagerFragment extends Fragment {

    public InviteManagerFragment() {
    }

    private RecyclerView invitesRecyclerView;
    private SampleUser user;
    private List<Pair<ServerConnection, Invite>> invites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_invite_manager, container, false);


        invitesRecyclerView = root.findViewById(R.id.invite_RecyclerView);

        invitesRecyclerView.setHasFixedSize(true);
        invitesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        invitesRecyclerView.setAdapter(new InvitesAdapter());
        invitesRecyclerView.getAdapter().notifyDataSetChanged();

        return root;
    }



    /**
     * A view holder to allow us to bind specific user data to a view in our recycler view
     */
    private class InvitesViewHolder extends RecyclerView.ViewHolder{

        private TextView serverTextView;
        private Button acceptButton;
        private Button denyButton;
        public InvitesViewHolder(@NonNull View itemView) {
            super(itemView);
            serverTextView = itemView.findViewById(R.id.server_TextView);
            acceptButton = itemView.findViewById(R.id.accept_Button);
            denyButton = itemView.findViewById(R.id.deny_Button);
        }

        //Set user for this particular view, this assigns the values to the textviews AND sets up the invite button
        public void setUser(final Pair<ServerConnection, Invite> invite){
            serverTextView.setText("Server : " + invite.first.getName());



            acceptButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    runHandleTask(invite, true);
                }
            });

            denyButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    runHandleTask(invite, false);
                }
            });
        }
    }

    /**
     * An Adapter for users that aren't in our server connection, this inflates a view based on
     * a particular view holder
     */
    private class InvitesAdapter extends RecyclerView.Adapter<InvitesViewHolder>{

        @NonNull
        @Override
        public InvitesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_invite, parent, false);
            InvitesViewHolder vh = new InvitesViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull InvitesViewHolder connectionsViewHolder, int position) {
            connectionsViewHolder.setUser(invites.get(position));
        }

        @Override
        public int getItemCount() {
            return invites.size();
        }
    }

    public void setUser(SampleUser u){
        this.user = u;

        invites = new LinkedList<>();
        RetrieveUserInvitesTask retrieveUserInvitesTask = new RetrieveUserInvitesTask("/invites", new OnResponseListener<List<Pair<ServerConnection, Invite>>>() {
            @Override
            public void onResponse(List<Pair<ServerConnection, Invite>> data) {
                invites = data;
                invitesRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onProgress(HttpProgress value) {

            }

            @Override
            public void onError(Exception error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        retrieveUserInvitesTask.execute(u);

    }

    public void runHandleTask(Pair<ServerConnection, Invite> invitePair, Boolean accepted){


        HandleInviteTask handleInviteTask = new HandleInviteTask(accepted, "/invites", new OnResponseListener<Boolean>(){

            @Override
            public void onResponse(Boolean data) {
                invites.remove(invitePair);
                invitesRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onProgress(HttpProgress value) {

            }

            @Override
            public void onError(Exception error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                invites.remove(invitePair);
                invitesRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        handleInviteTask.execute(invitePair.second);
    }
}
