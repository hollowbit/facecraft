package uk.co.olbois.facecraft.ui.chatroom;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.message.Message;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatroomActivityFragment extends Fragment {

    public ChatroomActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chatroom, container, false);


        return view;
    }

    private  class MessageViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private Message message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void setMessage(Message message) {
            this.message = message;

        }
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

        private List<Message> data;

        public MessageAdapter(List<Message> data) {
            this.data = data;
        }

        public List<Message> getData() {
            return data;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MessageViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_message, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder noteViewHolder, int position) {
            noteViewHolder.setMessage(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


    }
    

}
