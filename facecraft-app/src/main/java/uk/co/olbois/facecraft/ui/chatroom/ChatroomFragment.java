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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.message.Message;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.HttpProgress;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.tasks.RetrieveCurrentMessagesTask;
import uk.co.olbois.facecraft.tasks.SendMessageTask;
import uk.co.olbois.facecraft.tasks.ValidateLoginTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatroomFragment extends Fragment {

    // List of messages in the chatroom
    private List<Message> messageData;

    // The currently logged in user
    private SampleUser sampleUser;

    // The current server connection
    private ServerConnection serverConnection;

    private MessageAdapter messageAdapter;

    private SendMessageTask sendMessageTask;

    private TimerTask timerTask;

    private Timer timer;

    public ChatroomFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chatroom, container, false);

        Button sendButton = view.findViewById(R.id.send_message_button);
        final EditText input = view.findViewById(R.id.text_edit_text);

        // Generate sample messages (for prototype demo)
        messageData = new ArrayList<>();

        // Set the message recycler view adapter
        final RecyclerView messageRecyclerView = view.findViewById(R.id.message_recycler_view);
        messageAdapter = new MessageAdapter(messageData);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager((getContext())));
        messageRecyclerView.setAdapter(messageAdapter);

        // Scroll to the bottom of the recycler view
        messageRecyclerView.scrollToPosition(messageData.size() -1);

        // Add message to the recycler view when the send button is clicked
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send the message and clear the message box
                sendMessage(input.getText().toString());
                input.setText("");

                // Notify the data changed
                messageAdapter.notifyDataSetChanged();

                // scroll to the bottom of th recycler view
                messageRecyclerView.scrollToPosition(messageData.size() -1);
            }
        });

        return view;
    }

    /**
     * Message view holder for the chatroom recycler view
     */
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

            SimpleDateFormat formatter = new SimpleDateFormat("EE MMM d 'at' HH:mm");

            time.setText(formatter.format(message.getDate()));
            messageText.setText(message.getMessage());
        }
    }

    /**
     * Message Adapter for the chatroom recycler view
     */
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
     * Generates a list of sample messages (for prototype demo)
     * @return

    private List<Message> generateMessages() {
        List<Message> sampleData = new ArrayList<Message>();

        sampleData.add(new Message("JJ", "app", "Hi, my name is JJ", new Date(), ));
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
     * a sample response message (for prototype demo)

    private void sampleRespond() {

        messageData.add(new Message("nate2", "game", "hello user!", new Date()));
    }*/

    /**
     * adds a message to the list of messages
     * @param in the message
     */
    private void sendMessage(String in) {

        //messageData.add(new Message(sampleUser.getUsername(), "app", in, new Date(), serverConnection.getId(), null));

        sendMessageTask = new SendMessageTask("/messages" , new OnResponseListener<Boolean>() {
            @Override
            public void onResponse(Boolean data) {

            }

            @Override
            public void onProgress(HttpProgress value) {

            }

            @Override
            public void onError(Exception error) {

                Toast.makeText(getContext(), "There was an error sending your message", Toast.LENGTH_SHORT).show();
            }
        });

        // send the message to the database
        sendMessageTask.execute(new Message(sampleUser.getUsername(), "app", in, new Date(), serverConnection.getId(), null));
    }

    /**
     * Set the currently logged in user, called at activity start
     * @param u the user
     */
    public void setUser(SampleUser u) {
        this.sampleUser = u;
    }

    /**
     * Set the currently logged in user, called at activity start
     * @param c the user
     */
    public void setConnection(ServerConnection c) {
        this.serverConnection = c;

        retrieve();
    }

    public void retrieve() {

        timerTask = new TimerTask() {
            @Override
            public void run() {
                RetrieveCurrentMessagesTask retrieveCurrentMessagesTask = new RetrieveCurrentMessagesTask("/messages", new OnResponseListener<List<Message>>() {
                    @Override
                    public void onResponse(List<Message> data) {
                        messageData.clear();
                        for (Message m : data) {
                            if (m.getServerAddr().equals(serverConnection.getId()))
                                messageData.add(m);
                        }
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onProgress(HttpProgress value) {

                    }

                    @Override
                    public void onError(Exception error) {

                    }
                });

                retrieveCurrentMessagesTask.execute();
            }
        };

        timer = new Timer("Timer");
        timer.schedule(timerTask, 0, 5000);


    }
    

}
