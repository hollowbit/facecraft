package uk.co.olbois.facecraft.ui.calendar;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;


import uk.co.olbois.facecraft.R;

public class CalendarActivity extends AppCompatActivity {

    private CalendarFragment calendarFragment;

    public static class param {
        // useless for now (to be used for sending user and connection)
        public static final String INITIAL_USER = "initial_user";
        public static final String INITIAL_CONNECTION = "initial_connection";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        calendarFragment.setOnSetTimeClickedListener(new CalendarFragment.OnSetTimeClickedListener() {
            @Override
            public void onSetTimeClicked(Calendar calendar) {
                showTimePickerDialog(calendar);
            }
        });
    }

    /**
     * Show the time picker dialog when the event is triggered
     * @param calendar
     */
    //public void showTimePickerDialog(final Calendar calendar) {
    //    DialogFragment newFragment = new TimePickerFragment(new myCallBack() {

    public void showTimePickerDialog(View v) {
        final DialogFragment newFragment = new TimePickerFragment(new myCallBack() {

          @Override
            // TODO : get rid of mystr parameter <1.2>
            public void UpdateTime(String mystr, Calendar calendar1) {

                // TODO get rid of "mystr" and these 2 lines   <1.3>
                EditText txtView = (EditText)findViewById(R.id.editTime);
                txtView.setText(mystr);

            }
                calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                calendarFragment.setTime(calendar1);
            }
        }, calendar);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


}
