package uk.co.olbois.facecraft.ui.chatroom;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.olbois.facecraft.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatroomActivityFragment extends Fragment {

    public ChatroomActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatroom, container, false);
    }
}
