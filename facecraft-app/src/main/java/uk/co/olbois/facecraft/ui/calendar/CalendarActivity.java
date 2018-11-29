package uk.co.olbois.facecraft.ui.calendar;

import android.content.Context;
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

public class CalendarActivity extends AppCompatActivity {

    public static class param {
        public static final String INITIAL_USER = "initial_user";
        public static final String INITIAL_CONNECTION = "initial_connection";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(new myCallBack() {
            @Override
            public void UpdateMyText(String mystr) {
                EditText txtView = (EditText)findViewById(R.id.editTime);
                txtView.setText(mystr);
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
