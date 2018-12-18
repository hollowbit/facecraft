package uk.co.olbois.facecraft.ui.hub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
//import uk.co.olbois.facecraft.ui.calendar.CalendarActivity;
import uk.co.olbois.facecraft.ui.chatroom.ChatroomActivity;
import uk.co.olbois.facecraft.ui.chatroom.ChatroomFragment;
import uk.co.olbois.facecraft.ui.invite.InviteActivity;
import uk.co.olbois.facecraft.ui.invite.InviteFragment;
import uk.co.olbois.facecraft.ui.login.LoginActivity;
import uk.co.olbois.facecraft.ui.serverconsole.ServerConsoleActivity;
import uk.co.olbois.facecraft.ui.servermanager.ServerManagerActivity;
import uk.co.olbois.facecraft.ui.servermanager.ServerManagerFragment;

public class HubActivity extends AppCompatActivity {

    private HubFragment hubFragment;

    public static class param {
        public static final String INITIAL_USER = "initial_user";
        public static final String INITIAL_CONNECTION = "initial_connection";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hubFragment = (HubFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        //Expect to be created out of an intent
        Intent intent = getIntent();

        //retrieve the user that was sent
        SampleUser u = intent.getParcelableExtra(param.INITIAL_USER);
        ServerConnection serverConnection = intent.getParcelableExtra(param.INITIAL_CONNECTION);

        //set user and connection
        hubFragment.setUser(u);
        hubFragment.setConnection(serverConnection);

        // send data to the next fragment
        hubFragment.setOnFragmentChosenListener(new HubFragment.OnFragmentChosenListener() {
            @Override
            public void onFragmentChosen(ServerConnection connection,SampleUser u, String fragment) {

                Intent sendIntent;

                switch (fragment) {
                    case "invite":
                        sendIntent = new Intent(HubActivity.this, InviteActivity.class);
                        sendIntent.putExtra(InviteActivity.param.INITIAL_CONNECTION, connection);
                        sendIntent.putExtra(InviteActivity.param.INITIAL_USER, u);
                        startActivityForResult(sendIntent, 0);
                        break;
                    case "chat":
                        sendIntent = new Intent(HubActivity.this, ChatroomActivity.class);
                        sendIntent.putExtra(ChatroomActivity.param.INITIAL_CONNECTION, connection);
                        sendIntent.putExtra(ChatroomActivity.param.INITIAL_USER, u);
                        startActivityForResult(sendIntent, 0);
                        break;
                    /*case "event":
                        sendIntent = new Intent(HubActivity.this, CalendarActivity.class);
                        sendIntent.putExtra(CalendarActivity.param.INITIAL_USER, u);
                        startActivityForResult(sendIntent, 0);
                        break;*/

                    case "server":
                        sendIntent = new Intent(HubActivity.this, ServerConsoleActivity.class);
                        sendIntent.putExtra(ServerConsoleActivity.INITIAL_CONNECTION, serverConnection);
                        startActivityForResult(sendIntent, 0);
                        break;

                }
            }
        });
    }

}
