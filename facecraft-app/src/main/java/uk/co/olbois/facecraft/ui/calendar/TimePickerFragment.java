package uk.co.olbois.facecraft.ui.calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    myCallBack mCallBack;
    Calendar calendar;
    public TimePickerFragment(myCallBack callBack, Calendar calendar){
        this.mCallBack=callBack;
        this.calendar = calendar;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog timePicker;
        //timePicker = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        timePicker = new TimePickerDialog(getActivity(), this, hour, minute, true);

        String time = mCallBack.getMyText();
        if(time != "" && time.contains(":")) {
            String[] times = time.split(":");
            hour = Integer.parseInt(times[0].trim());
            minute = Integer.parseInt(times[1].trim());

            timePicker.updateTime(hour,minute);
        }



        return  timePicker;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        String hour = String.valueOf(view.getHour());
        String minutes = String.valueOf(view.getMinute());

        if(minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        if(hour.toString() == "0") {
            hour = "00";
        }

        //Toast.makeText(getActivity(), hour, Toast.LENGTH_LONG).show();
        //Toast.makeText(getActivity(), minutes, Toast.LENGTH_LONG).show();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        // TODO stop sending "hour + ":" + minute" <1.1>
        this.mCallBack.UpdateTime(hour + " : " + minutes, c);

    }




}
