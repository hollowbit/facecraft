package uk.co.olbois.facecraft.ui.calendar;

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

        View saveBtn = root.findViewById(R.id.btnSaveEvent);

        final CalendarView calendarView = root.findViewById(R.id.calendarView);

        final TextView timeView = root.findViewById(R.id.editTime);

        final EditText titleView = root.findViewById(R.id.editTextTitle);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = "";
                Long unixTime = Long.valueOf(0);
                String time = "";

                unixTime = calendarView.getDate();


                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
                date = sdf.format(unixTime);


                time = String.valueOf(timeView.getText()).trim();

                Toast.makeText(getContext(), "Event " + titleView.getText() + " saved for \n" + date + " at " + time ,Toast.LENGTH_SHORT).show();
            }
        });


        return root;

    }




}
