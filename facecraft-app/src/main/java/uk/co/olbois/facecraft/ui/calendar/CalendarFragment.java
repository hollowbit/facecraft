package uk.co.olbois.facecraft.ui.calendar;

import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;

import uk.co.olbois.facecraft.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CalendarFragment extends Fragment {

    public CalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        View saveBtn = root.findViewById(R.id.btnSaveEvent);

        final CalendarView calendarView = root.findViewById(R.id.calendarView);

        final TextView timeView = root.findViewById(R.id.editTime);

        final EditText titleView = root.findViewById(R.id.editTextTitle);

        final Calendar calendar = Calendar.getInstance();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                /*calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);*/

                calendar.set(year,month,dayOfMonth);

                long date =  calendar.getTime().getTime();
                long currentDate = System.currentTimeMillis();
                if(date < currentDate){
                    Toast.makeText(getContext(), "Cannot set an event in the past" ,Toast.LENGTH_SHORT).show();
                    //calendarView.setDate(-1);
                }
                else {
                    calendarView.setDate(date);
                }

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = "";
                Long unixTime = Long.valueOf(0);
                int time = 0;

                unixTime = calendarView.getDate();


                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
                date = sdf.format(unixTime);


                time = Integer.parseInt((String)(timeView.getText()));
                if( unixTime > 0 ){
                    Toast.makeText(getContext(), "Event " + titleView.getText() + " saved for \n" + date + " at " + time ,Toast.LENGTH_SHORT).show();
                }
            }
        });





        return root;

    }





    public void createNewEvent(){
        //events.insert();
    }




}
