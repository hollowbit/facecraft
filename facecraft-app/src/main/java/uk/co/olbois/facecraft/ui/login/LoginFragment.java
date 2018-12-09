package uk.co.olbois.facecraft.ui.login;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.UniversalDatabaseHandler;
import uk.co.olbois.facecraft.sqlite.DatabaseException;

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
                boolean found = false;
                SampleUser u = null;
                for(SampleUser currentUser : users){
                    String password = currentUser.getPassword();
                    String sentPassword = passwordEditText.getText().toString();
                    if(currentUser.getUsername().toLowerCase().equals(usernameEditText.getText().toString().toLowerCase())
                            && currentUser.getPassword().equals(passwordEditText.getText().toString())){
                        found = true;
                        u = currentUser;
                        break;
                    }
                }

                if(found){
                    if(onLoggedInListener == null)
                        return;
                    onLoggedInListener.onLoggedIn(u);
                }
                else{
                    Toast.makeText(getContext(), "Invalid Username / Password", Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            }
        });
    }

    public void clearFields(){
        usernameEditText.setText("");
        passwordEditText.setText("");
    }
}
