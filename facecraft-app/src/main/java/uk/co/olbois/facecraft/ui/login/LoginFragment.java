package uk.co.olbois.facecraft.ui.login;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.List;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.UniversalDatabaseHandler;
import uk.co.olbois.facecraft.server.HttpProgress;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.sqlite.DatabaseException;
import uk.co.olbois.facecraft.tasks.RegisterUserTask;
import uk.co.olbois.facecraft.tasks.ValidateLoginTask;
import uk.co.olbois.facecraft.ui.SpringApplication;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    public interface OnLoggedInListener{
        void onLoggedIn(SampleUser u);
    }

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MIN_PASSWORD_LENGTH = 4;

    private OnLoggedInListener onLoggedInListener;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;

    private UniversalDatabaseHandler dbh;
    private List<SampleUser> users;
    private SpringApplication application;

    public void setOnLoggedInListener(OnLoggedInListener onLoggedInListener){
        this.onLoggedInListener = onLoggedInListener;
    }

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        //retrieve view elements
        usernameEditText = root.findViewById(R.id.username_EditText);
        passwordEditText = root.findViewById(R.id.password_EditText);
        loginButton = root.findViewById(R.id.login_Button);
        registerButton = root.findViewById(R.id.register_Button);

        application = (SpringApplication) getContext().getApplicationContext();
        FirebaseApp.initializeApp(getContext());

        //Initialize register and login button functionality
        setupLoginButton();
        setupRegisterButton();
        return root;
    }

    /**
     * initializes the login button for our fragment, which in turn sets up the
     * on click listener
     */
    private void setupLoginButton(){
        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                loginButton.setEnabled(false);
                registerButton.setEnabled(false);

                setupValidateTask();
            }
        });
    }

    /**
     * initializes the register button for our fragment, which in turn sets up the
     * on click listener
     */
    private void setupRegisterButton(){
        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //Make sure valid inputs
                if(usernameEditText.getText().toString().length() < MIN_USERNAME_LENGTH || passwordEditText.getText().toString().length() < MIN_PASSWORD_LENGTH){
                    Toast.makeText(getContext(), "Your password or username is too short!", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginButton.setEnabled(false);
                registerButton.setEnabled(false);

                setupRegisterTask();
            }
        });
    }

    /**
     * To clear the password and username input boxes
     */
    public void clearFields(){
        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    /**
     * This function initializes a "ValidateLoginTask" to perform, which allows the server to see if
     * the entered credentials match with something in our database.
     */
    public void setupValidateTask(){
        //This task is run when the user attempts to log in, it sends the username and password
        //and attempts to find a matching user in the database
        ValidateLoginTask validateLoginTask = new ValidateLoginTask("/users", usernameEditText.getText().toString().toLowerCase(), passwordEditText.getText().toString(), new OnResponseListener<SampleUser>() {
            @Override
            public void onResponse(SampleUser data) {

                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                //Fire the onloggedin event, if the listener exists.
                if(onLoggedInListener == null)
                    return;
                onLoggedInListener.onLoggedIn(data);
            }

            @Override
            public void onProgress(HttpProgress value) {

            }

            @Override
            public void onError(Exception error) {

                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                //Something went wrong, either couldnt access database or invalid credentials
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                clearFields();
            }
        });

        //This is a key point!
        //Every time the user logs in, we update the device token in the database
        //This is what allows us to differentiate users in the database!

        //This is us retrieving the device unique token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String newToken = instanceIdResult.getToken();
                validateLoginTask.execute(newToken);
            }
        });
    }

    public void setupRegisterTask(){

        RegisterUserTask registerUserTask = new RegisterUserTask("/users", usernameEditText.getText().toString(), passwordEditText.getText().toString(), new OnResponseListener<SampleUser>() {
            @Override
            public void onResponse(SampleUser data) {
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                //Fire the onloggedin event, if the listener exists.
                if(onLoggedInListener == null)
                    return;
                onLoggedInListener.onLoggedIn(data);
            }

            @Override
            public void onProgress(HttpProgress value) {

            }

            @Override
            public void onError(Exception error) {

                loginButton.setEnabled(true);
                registerButton.setEnabled(true);

                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                clearFields();
            }
        });



        //This is us retrieving the device unique token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String newToken = instanceIdResult.getToken();
                registerUserTask.execute(newToken);
            }
        });
    }
}
