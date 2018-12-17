package uk.co.olbois.facecraft.ui.chatroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.ui.hub.HubActivity;
import uk.co.olbois.facecraft.ui.hub.HubFragment;

public class ChatroomActivity extends AppCompatActivity {

    private static ChatroomFragment chatroomFragment;

    public static class param {
        public static final String INITIAL_USER = "initial_user";
        public static final String INITIAL_CONNECTION = "initial_connection";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chatroomFragment = (ChatroomFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        //Expect to be created out of an intent
        Intent intent = getIntent();

        //retrieve the user that was sent
        SampleUser u = intent.getParcelableExtra(param.INITIAL_USER);
        ServerConnection serverConnection = intent.getParcelableExtra(param.INITIAL_CONNECTION);

        //set user
        chatroomFragment.setUser(u);
        chatroomFragment.setConnection(serverConnection);
    }

}
