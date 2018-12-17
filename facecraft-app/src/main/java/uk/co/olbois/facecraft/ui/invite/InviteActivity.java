package uk.co.olbois.facecraft.ui.invite;

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

public class InviteActivity extends AppCompatActivity {

    private InviteFragment inviteFragment;

    public static class param {
        public static final String INITIAL_CONNECTION = "initial_connection";
        public static final String INITIAL_USER ="initial_user";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        inviteFragment = (InviteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        //Expect to be created out of an intent
        Intent intent = getIntent();
        //retrieve the user that was sent
        ServerConnection c = intent.getParcelableExtra(param.INITIAL_CONNECTION);
        SampleUser sampleUser = intent.getParcelableExtra(param.INITIAL_USER);

        inviteFragment.setConnection(c);
    }

}
