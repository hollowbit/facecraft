package uk.co.olbois.facecraft.ui.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.ui.chatroom.ChatroomActivity;

public class CalendarActivity extends AppCompatActivity {

    public static class param {
        public static final String INITIAL_USER = "initial_user";
        public static final String INITIAL_CONNECTION = "initial_connection";
    }
    private static CalendarFragment calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendar = (CalendarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);


        //Expect to be created out of an intent
        Intent intent = getIntent();

        ServerConnection c = intent.getParcelableExtra(CalendarActivity.param.INITIAL_CONNECTION);

        calendar.setConnection(c);

    }


    public void showTimePickerDialog(View v) {
        final DialogFragment newFragment = new TimePickerFragment(new myCallBack() {
            @Override
            public void UpdateMyText(String mystr) {
                EditText txtView = (EditText)findViewById(R.id.editTime);
                txtView.setText(mystr);
            }

            @Override
            public String getMyText() {
                EditText txtView = (EditText)findViewById(R.id.editTime);
                return txtView.getText().toString();
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


}
