package uk.co.olbois.facecraft.ui.calendar;

import android.icu.util.Calendar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import uk.co.olbois.facecraft.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CalendarFragment extends Fragment {

    public CalendarFragment() {
    }

    public interface OnSetTimeClickedListener{
        void onSetTimeClicked(Calendar calendar);
    }

    private OnSetTimeClickedListener onSetTimeClickedListener;

    private Calendar calendar;

    // TODO                                                                                         here  <2>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);


        Button saveBtn = root.findViewById(R.id.btnSaveEvent);
        Button setTimeBtn = root.findViewById(R.id.btnSetTime);
        final CalendarView calendarView = root.findViewById(R.id.calendarView);
        // TODO: make this --v a field       move to ------------------------------------------------^ <2>
        final TextView timeView = root.findViewById(R.id.editTime);
        final EditText titleView = root.findViewById(R.id.editTextTitle);

        // set the calender time to the current time
        calendar = Calendar.getInstance();

        // Set the on click listener for the save button
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // formatted date
                String date;
                // event date
                Long unixTime;
                //calendar time
                String time;

                unixTime = calendarView.getDate();


                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
                date = sdf.format(unixTime);

                // TODO: change this line to get "calendar" hour and minute, format it into am/pm <4>
                time = String.valueOf(timeView.getText()).trim();

                Toast.makeText(getContext(), "Event " + titleView.getText() + " saved for \n" + date + " at " + time ,Toast.LENGTH_SHORT).show();
            }
        });

        // When the set time button is clicked, trigger the event and open the dialog fragment
        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetTimeClickedListener.onSetTimeClicked(calendar);
            }
        });

        return root;

    }

    /**
     * set the onclick listener for setting the time
     * @param onSetTimeClickedListener
     */
    public void setOnSetTimeClickedListener( OnSetTimeClickedListener onSetTimeClickedListener){
        this.onSetTimeClickedListener = onSetTimeClickedListener;
    }

    /**
     * set the current event time
     * @param calendar
     */
    public void setTime(Calendar calendar) {
        // TODO set the edit text "timeView" here based on "calendar" hour and minute  and format it into am/pm <3>
        this.calendar = calendar;
    }
}
