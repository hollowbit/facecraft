package uk.co.olbois.facecraft.ui.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
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


        final CalendarView calendarView = root.findViewById(R.id.calendarView);

        final TextView timeView = root.findViewById(R.id.editTime);

        final EditText titleView = root.findViewById(R.id.editTextTitle);

        final Calendar calendar = Calendar.getInstance();

        // set the calender time to the current time
        calendar = Calendar.getInstance();

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

                time = timeView.getText().toString();

                long currentDate = System.currentTimeMillis();
                if (unixTime > 0 && !title.isEmpty() && !time.isEmpty()) {
                    if (unixTime >= currentDate) {
                        Toast.makeText(getContext(), "Event " + titleView.getText() + " saved for \n" + date + " at " + time, Toast.LENGTH_SHORT).show();

                        createNewEvent(title, unixTime, time);
                    } else {
                        Toast.makeText(getContext(), "Cannot set an event in the past", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "Cannot save an event with no title", Toast.LENGTH_SHORT).show();
                    } else if (time.isEmpty()) {
                        Toast.makeText(getContext(), "Cannot save an event with no time", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error: could not save the event", Toast.LENGTH_SHORT).show();
                    }

                }

                // TODO: change this line to get "calendar" hour and minute, format it into am/pm <4>
                time = String.valueOf(timeView.getText()).trim();

                Toast.makeText(getContext(), "Event " + titleView.getText() + " saved for \n" + date + " at " + time ,Toast.LENGTH_SHORT).show();
            }
        });

        // When the set time button is clicked, trigger the event and open the dialog fragment

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CalendarActivity) getActivity()).showTimePickerDialog(v);
            }
        });


        return root;

    }
    /**
     * set the onclick listener for setting the time
     * @param onSetTimeClickedListener
     */
    //public void setOnSetTimeClickedListener( OnSetTimeClickedListener onSetTimeClickedListener){
    //    this.onSetTimeClickedListener = onSetTimeClickedListener;
    //}

    public void createNewEvent(String title, long date, String time) {
        //events.insert();

        Event event = new Event()
                .setSummary(title);
        //.setDescription("A chance to hear more about Google's developer products.");

        Date date2 = new Date((long) date);
        Calendar ctime = Calendar.getInstance();
        String[] times = time.trim().split(":");
        String hour = times[0].trim();
        String minute = times[1].trim();
        String hourPlusOne = String.valueOf(Integer.parseInt(hour) + 1);

        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        if (hourPlusOne.length() == 1) {
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

        addEvent(event);

        //Event is created

        /*final String[] EVENT_PROJECTION = new String[]{
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
        ContentResolver cr = getActivity().getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{"hera@example.com", "com.example",
                "hera@example.com"};

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CALENDAR)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CALENDAR},
                        1);
            }
        } else {
            // Permission has already been granted
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
        }*/


    }

    public void addEvent(Event event) {

        //Convert event to unix time
        Date unix = new Date(event.getStart().getDateTime().getValue());

        Calendar cal = Calendar.getInstance();

        cal.setTime(unix);

        //get indiviudal ints from unix time
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);


        //Create intent with individual ints
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, hour, minute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, ((hour + 1) % 24) , minute);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, event.getSummary());
        startActivity(intent);

    /**
     * set the current event time
     * @param calendar
     */
    public void setTime(Calendar calendar) {
        // TODO set the edit text "timeView" here based on "calendar" hour and minute  and format it into am/pm <3>
        this.calendar = calendar;
    }




}
