package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uk.co.olbois.facecraft.model.message.Message;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class RetrieveCurrentMessagesTask extends AsyncTask<Void, Void, Either<Exception, List<Message>>> {

    String path;
    OnResponseListener<List<Message>> onResponseListener;

    //path = /messages/...
    public RetrieveCurrentMessagesTask(String path, OnResponseListener<List<Message>> onResponseListener){
        this.path = path;
        this.onResponseListener = onResponseListener;
    }

    @Override
    protected Either<Exception, List<Message>> doInBackground(Void... voids) {

        try {

        HttpResponse response = new HttpRequestBuilder(path + "/messages")
                .method(HttpRequestBuilder.Method.GET)
                .perform();

        String json = new String(response.getContent(), "UTF8");
        Message[] currentMessages = Message.Companion.parseArray(json);

        /*response = new HttpRequestBuilder(path + "/members")
                .method(HttpRequestBuilder.Method.GET)
                .perform();

        json = new String(response.getContent(), "UTF8");
        SampleUser[] serverMembers = SampleUser.parseArray(json);

        for(SampleUser u : serverMembers){
            u.setRole(ServerConnection.Role.MEMBER);
        }


        response = new HttpRequestBuilder("/users")
                .method(HttpRequestBuilder.Method.GET)
                .perform();

        json = new String(response.getContent(), "UTF8");
        LinkedList<SampleUser> allUsers = new LinkedList<SampleUser>(Arrays.asList(SampleUser.parseArray(json)));


        LinkedList<SampleUser> users = new LinkedList<>();
        users.addAll(Arrays.asList(serverOwners));
        users.addAll(Arrays.asList(serverMembers));

        for(SampleUser u1: users){
            boolean found = false;
            SampleUser temp = null;
            for(SampleUser u2:allUsers){
                if(u1.getUrl().equals(u2.getUrl())){
                    found = true;
                    temp = u2;
                    break;
                }
            }
            if(found)
                allUsers.remove(temp);
        }

        response = new HttpRequestBuilder("/invites")
                .method(HttpRequestBuilder.Method.GET)
                .perform();

        json = new String(response.getContent(), "UTF8");
        Invite[] invites = Invite.parseArray(json);

        for(Invite i : invites){
            response = new HttpRequestBuilder("/invites/" + i.getId() + "/invited_user_id")
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            json = new String(response.getContent(), "UTF8");
            SampleUser u = SampleUser.parse(json);

            allUsers.removeIf(user -> user.getUrl().equals(u.getUrl()));
        }

        users.addAll(allUsers);*/

        return Either.right(Arrays.asList(currentMessages));

    } catch (IOException | ServerException e) {
        return Either.left(e);
    }
    }

    @Override
    protected void onPostExecute(Either<Exception, List<Message>> either) {
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
