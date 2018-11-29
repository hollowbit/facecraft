package uk.co.olbois.facecraft.ui.chatroom;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.message.Message;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatroomFragment extends Fragment {

    private List<Message> messageData;
    private SampleUser sampleUser;

    public ChatroomFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chatroom, container, false);

        Button sendButton = view.findViewById(R.id.send_message_button);
        final EditText input = view.findViewById(R.id.text_edit_text);

        messageData = generateMessages();

        // Set the message recycler view adapter
        final RecyclerView messageRecyclerView = view.findViewById(R.id.message_recycler_view);
        final MessageAdapter messageAdapter = new MessageAdapter(messageData);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager((getContext())));
        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.scrollToPosition(messageData.size() -1);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send the message and clear the message box
                sendMessage(input.getText().toString());
                input.setText("");

                // sample a response every 2 messages
                if (messageData.size() % 3 == 0) {
                    sampleRespond();
                }

                // Notify the data changed
                messageAdapter.notifyDataSetChanged();

                // scroll to the bottom of th recycler view
                messageRecyclerView.scrollToPosition(messageData.size() -1);
            }
        });

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

    /**
     * Generates a list of sample messages
     * @return
     */
    private List<Message> generateMessages() {
        List<Message> sampleData = new ArrayList<Message>();

        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date()));
        sampleData.add(new Message("Nate", "game", "Hi, my name is Nate", new Date()));
        sampleData.add(new Message("Alex", "app", "U guys are losers", new Date()));
        sampleData.add(new Message("Ahmed", "app", "Hi everyone", new Date()));
        sampleData.add(new Message("Ian", "game", "Emacs", new Date()));
        sampleData.add(new Message("Jothua", "game", "hey guyth im jothua", new Date()));
        sampleData.add(new Message("Kennard", "app", "I have many logins", new Date()));
        sampleData.add(new Message("Natunaial", "game", "tuna", new Date()));
        sampleData.add(new Message("JayJay2", "app", "Not all heroes wear caps", new Date()));
        sampleData.add(new Message("Jessii", "game", "I spell my name with an i", new Date()));


        return sampleData;
    }

    /**
     * a sample response message
     */
    private void sampleRespond() {

        messageData.add(new Message("nate2", "game", "hello user!", new Date()));
    }

    /**
     * adds a message to the list of messages
     * @param in
     */
    private void sendMessage(String in) {

        messageData.add(new Message(sampleUser.getUsername(), "app", in, new Date()));
    }

    public void setUser(SampleUser u) {
        this.sampleUser = u;
    }
    

}
