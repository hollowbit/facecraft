package uk.co.olbois.facecraft.ui.servermanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;

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

        Intent intent = getIntent();
        SampleUser u= intent.getParcelableExtra(param.INITIAL_USER);

        serverManagerFragment.setUser(u);

        serverManagerFragment.setOnLoggedOutListener(new ServerManagerFragment.OnLoggedOutListener() {
            @Override
            public void onLoggedOut() {
                finish();
            }
        });
    }
}
