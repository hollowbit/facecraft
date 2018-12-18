package uk.co.olbois.facecraft.ui.servermanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.ui.hub.HubActivity;
import uk.co.olbois.facecraft.ui.invite.InviteActivity;
import uk.co.olbois.facecraft.ui.invite.InviteFragment;
import uk.co.olbois.facecraft.ui.invitemanager.InviteManagerActivity;

public class ServerManagerActivity extends AppCompatActivity {

    private ServerManagerFragment serverManagerFragment;

    public static class param {
        public static final String INITIAL_USER = "initial_user";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serverManagerFragment = (ServerManagerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        //Expect to be created out of an intent
        Intent intent = getIntent();
        //retrieve the user that was sent
        SampleUser u= intent.getParcelableExtra(param.INITIAL_USER);

        //set user and set event listener for logout (sends back to login activity)
        serverManagerFragment.setUser(u);
        serverManagerFragment.setOnLoggedOutListener(new ServerManagerFragment.OnLoggedOutListener() {
            @Override
            public void onLoggedOut() {
                finish();
            }
        });

        //set event listener for reaching the hub with a connection
        serverManagerFragment.setOnConnectListener(new ServerManagerFragment.OnConnectListener() {

            Intent intent = new Intent(ServerManagerActivity.this, HubActivity.class);
            @Override
            public void onConnect(ServerConnection connection, SampleUser user) {

                intent.putExtra(HubActivity.param.INITIAL_CONNECTION, connection);
                intent.putExtra(HubActivity.param.INITIAL_USER, user);
                startActivityForResult(intent, 0);
            }
        });

        serverManagerFragment.setOnManageInviteClickedListener(new ServerManagerFragment.OnManageInviteClickedListener() {
            Intent intent = new Intent(ServerManagerActivity.this, InviteManagerActivity.class);
            @Override
            public void onManageInviteClicked(SampleUser u) {
                intent.putExtra(InviteManagerActivity.param.INITIAL_USER, u);
                startActivityForResult(intent, 0);
            }
        });
    }

    //When the HubActivity is finished, this is called, just refresh list for now.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        serverManagerFragment.refreshList();
    }
}
