package uk.co.olbois.facecraft.ui.chatroom;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.message.Message;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatroomActivityFragment extends Fragment {

    private List<Message> messageData;

    public ChatroomActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chatroom, container, false);

        messageData = generateMessages();

        // Set the message recycler view adapter
        RecyclerView messageRecyclerView = view.findViewById(R.id.message_recycler_view);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager((getContext())));
        messageRecyclerView.setAdapter(new MessageAdapter(messageData));

        return view;
    }

    private  class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private TextView userType;
        private TextView time;
        private TextView messageText;

        private final View root;
        private Message message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;

            // Get all message properties
            username = root.findViewById(R.id.username_text_view);
            userType = root.findViewById(R.id.user_type_text_view);
            time = root.findViewById(R.id.time_text_view);
            messageText = root.findViewById(R.id.message_text_view);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void setMessage(Message message) {
            this.message = message;

            // Set the message texts
            username.setText(message.getUsername());
            userType.setText(message.getSenderType());

            // TODO format date
            time.setText(message.getTime().toString());
            messageText.setText(message.getContent());
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

    private List<Message> generateMessages() {
        List<Message> sampleData = new ArrayList<Message>();

        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));
        sampleData.add(new Message("Nate", "app", "Hi, my name is Nate", new Date()));
        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));
        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));
        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));
        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));
        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));
        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));
        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));
        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));


        return sampleData;
    }
    

}
