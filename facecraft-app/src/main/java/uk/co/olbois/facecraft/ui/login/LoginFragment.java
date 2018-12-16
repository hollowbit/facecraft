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
import uk.co.olbois.facecraft.tasks.ValidateLoginTask;
import uk.co.olbois.facecraft.ui.SpringApplication;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    public interface OnLoggedInListener{
        void onLoggedIn(SampleUser u);
    }

    private OnLoggedInListener onLoggedInListener;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

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

        usernameEditText = root.findViewById(R.id.username_EditText);
        passwordEditText = root.findViewById(R.id.password_EditText);
        loginButton = root.findViewById(R.id.login_Button);

        application = (SpringApplication) getContext().getApplicationContext();

        dbh = new UniversalDatabaseHandler(getContext());
        try {
            users = dbh.getSampleUserTable().readAll();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        setupLoginButton();

        return root;
    }

    private void setupLoginButton(){
        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                setupValidateTask();
            }
        });
    }

    public void clearFields(){
        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    public void setupValidateTask(){
        ValidateLoginTask validateLoginTask = new ValidateLoginTask("/users", usernameEditText.getText().toString().toLowerCase(), passwordEditText.getText().toString(), new OnResponseListener<SampleUser>() {
            @Override
            public void onResponse(SampleUser data) {
                if(onLoggedInListener == null)
                    return;
                onLoggedInListener.onLoggedIn(data);
            }

            @Override
            public void onProgress(HttpProgress value) {

            }

            @Override
            public void onError(Exception error) {

                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                clearFields();
            }
        });
        FirebaseApp.initializeApp(getContext());

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String newToken = instanceIdResult.getToken();
                validateLoginTask.execute(newToken);
            }
        });
    }
}
