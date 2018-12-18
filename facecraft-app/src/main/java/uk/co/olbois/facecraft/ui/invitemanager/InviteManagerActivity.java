package uk.co.olbois.facecraft.ui.invitemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;

public class InviteManagerActivity extends AppCompatActivity {


    public static class param {
        public static final String INITIAL_USER ="initial_user";
    }

    private InviteManagerFragment inviteManagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        inviteManagerFragment = (InviteManagerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        //Expect to be created out of an intent
        Intent intent = getIntent();
        //retrieve the user that was sent
        SampleUser u = intent.getParcelableExtra(param.INITIAL_USER);

        //set user and set event listener for logout (sends back to login activity)
        inviteManagerFragment.setUser(u);

    }
}
