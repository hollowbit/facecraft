package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uk.co.olbois.facecraft.model.Invite;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class SendInvitationTask extends AsyncTask<SampleUser, Void, Either<Exception, Boolean>> {
    private OnResponseListener<Boolean> onResponseListener;
    private String path;
    private ServerConnection connection;

    public SendInvitationTask(String path, ServerConnection connection, OnResponseListener onResponseListener){
        this.onResponseListener = onResponseListener;
        this.path = path;
        this.connection = connection;
    }

    @Override
    protected Either<Exception, Boolean> doInBackground(SampleUser... users) {
        //Get access to the firestore database
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        SampleUser sender = users[0];
        SampleUser receiver = users[1];
        //create an invite out of the sent fields
        Invite invite = new Invite();
        invite.setServer(connection.getUrl());
        invite.setInvited_by(sender.getUrl());
        invite.setInvited_user_id(receiver.getUrl());

        try {
            //create the invite on the springio database
            HttpResponse response = new HttpRequestBuilder(path)
                    .method(HttpRequestBuilder.Method.POST)
                    .withRequestBody("application/json", invite.format().getBytes())
                    .perform();
            invite.setUrl(response.getHeaderFields().get("Location").get(0));
        } catch (IOException | ServerException e) {
            return Either.left(e);
        }

        //create a hash map with all the data we wish to send to the firebase database
        Map<String, Object> invite_payload = new HashMap<>();
        invite_payload.put("to", receiver.getUsername());
        invite_payload.put("device_token", receiver.getDeviceToken());
        invite_payload.put("server", connection.getName());
        invite_payload.put("invited_by", invite.getInvited_by());
        invite_payload.put("invited_user_id", invite.getInvited_user_id());
        invite_payload.put("server_id", invite.getServer());
        invite_payload.put("invite_url", invite.getUrl());


        //Send the hashmap to the invites collection on my firebase database
        mFirestore.collection("invites")
                .add(invite_payload);
        return Either.right(true);
    }


    @Override
    protected void onPostExecute(Either<Exception, Boolean> either) {
        if(onResponseListener == null)
            return;

        switch(either.getType()){
            case LEFT:
                onResponseListener.onError(either.getLeft());
                break;
            case RIGHT:
                onResponseListener.onResponse(either.getRight());
                break;
        }
    }
}
