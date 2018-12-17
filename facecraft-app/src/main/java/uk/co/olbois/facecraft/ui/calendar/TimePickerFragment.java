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

        // Use the passed in time as the default values for the picker
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        String hour = String.valueOf(view.getHour());
        String minutes = String.valueOf(view.getMinute());
        //Toast.makeText(getActivity(), hour, Toast.LENGTH_LONG).show();
        //Toast.makeText(getActivity(), minutes, Toast.LENGTH_LONG).show();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        // TODO stop sending "hour + ":" + minute" <1.1>
        this.mCallBack.UpdateTime(hour + " : " + minutes, c);

    }
}
