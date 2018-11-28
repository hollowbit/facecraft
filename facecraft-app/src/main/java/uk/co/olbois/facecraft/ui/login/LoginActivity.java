package uk.co.olbois.facecraft.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.ui.servermanager.ServerManagerActivity;

public class LoginActivity extends AppCompatActivity {
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        loginFragment.setOnLoggedInListener(new LoginFragment.OnLoggedInListener() {
            Intent intent = new Intent(LoginActivity.this, ServerManagerActivity.class);
            @Override
            public void onLoggedIn(SampleUser u) {


                intent.putExtra(ServerManagerActivity.param.INITIAL_USER, u);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        loginFragment.clearFields();
    }
}
