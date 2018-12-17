package uk.co.olbois.facecraft.ui.calendar;

import android.content.ContentResolver;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.provider.CalendarContract;
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

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

import uk.co.olbois.facecraft.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CalendarFragment extends Fragment {

    private com.google.api.services.calendar.Calendar service;

    public CalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        final View saveBtn = root.findViewById(R.id.btnSaveEvent);

        final View setTimeBtn = root.findViewById(R.id.btnSetTime);

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
                    saveBtn.setClickable(false);
                    Toast.makeText(getContext(), "Cannot set an event in the past" ,Toast.LENGTH_SHORT).show();
                    //calendarView.setDate(-1);
                }
                else {
                    saveBtn.setClickable(true);
                    calendarView.setDate(date);
                }

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = "";
                Long unixTime = Long.valueOf(0);
                String time;
                String title = titleView.getText().toString();

                unixTime = calendarView.getDate();


                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
                date = sdf.format(unixTime);


                time = timeView.getText().toString();

                long currentDate = System.currentTimeMillis();
                if( unixTime > 0 && !title.isEmpty() && !time.isEmpty() ) {
                    if (unixTime >= currentDate){
                    Toast.makeText(getContext(), "Event " + titleView.getText() + " saved for \n" + date + " at " + time, Toast.LENGTH_SHORT).show();

                        createNewEvent(title,unixTime,time);
                    }
                    else {
                        Toast.makeText(getContext(), "Cannot set an event in the past" ,Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if(title.isEmpty())
                    {
                        Toast.makeText(getContext(), "Cannot save an event with no title", Toast.LENGTH_SHORT).show();
                    }
                    else if(time.isEmpty()) {
                        Toast.makeText(getContext(), "Cannot save an event with no time", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Error: could not save the event", Toast.LENGTH_SHORT).show();
                    }

                }






            }
        });

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CalendarActivity)getActivity()).showTimePickerDialog(v);
            }
        });




        return root;

    }





    public void createNewEvent(String title,long date,String time){
        //events.insert();

        Event event = new Event()
                .setSummary(title);
                //.setDescription("A chance to hear more about Google's developer products.");

        Date date2 = new Date((long)date);
        Calendar ctime = Calendar.getInstance();
        String[] times = time.trim().split(":");
        String hour = times[0].trim();
        String minute = times[1].trim();
        String hourPlusOne = String.valueOf(Integer.parseInt(hour) + 1);

        if(minute.length() == 1) {
            minute = "0" + minute;
        }
        if(hour.length() == 1) {
            hour = "0" + hour;
        }
        if(hourPlusOne.length() == 1) {
            hourPlusOne = "0" + hourPlusOne;
        }




        DateTime dt = new DateTime(date2);

        dt.isDateOnly();

        String[] dates = dt.toString().split("T");
        String[] dates2 = dt.toString().split("-");

        String realdate = dates[0] + "T" + hour + ":" + minute + ":00-" + dates2[3];
        String realdateOneHourPlus = dates[0] + "T" + hourPlusOne + ":" + String.valueOf(minute) + ":00-" + dates2[3];

                                                //2015-05-28T09:00:00-07:00
        DateTime startDateTime = new DateTime(realdate);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/New_York");
        event.setStart(start);

        DateTime endDateTime = new DateTime(realdateOneHourPlus);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/New_York");
        event.setEnd(end);

        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        // Run query
        Cursor cur = null;
        ContentResolver cr =  getActivity().getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {"hera@example.com", "com.example",
                "hera@example.com"};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            // Do something with the values...
        }


    }




}
