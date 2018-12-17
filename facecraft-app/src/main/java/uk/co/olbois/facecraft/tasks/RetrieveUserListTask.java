package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import uk.co.olbois.facecraft.model.Invite;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class RetrieveUserListTask extends AsyncTask<Void, Void, Either<Exception, List<SampleUser>>> {

    String path;
    OnResponseListener<List<SampleUser>> onResponseListener;

    //path = /servers/...
    public RetrieveUserListTask(String path, OnResponseListener<List<SampleUser>> onResponseListener){
        this.path = path;
        this.onResponseListener = onResponseListener;
    }

    @Override
    protected Either<Exception, List<SampleUser>> doInBackground(Void... voids) {try {
        //Retrieve a list of the owners, and give them the "Owner" role.
        HttpResponse response = new HttpRequestBuilder(path + "/owners")
                .method(HttpRequestBuilder.Method.GET)
                .perform();

        String json = new String(response.getContent(), "UTF8");
        SampleUser[] serverOwners = SampleUser.parseArray(json);

        for(SampleUser u : serverOwners){
            u.setRole(ServerConnection.Role.OWNER);
        }

        //Retrieve a list of the members, and give them the "Member" role.
        response = new HttpRequestBuilder(path + "/members")
                .method(HttpRequestBuilder.Method.GET)
                .perform();

        json = new String(response.getContent(), "UTF8");
        SampleUser[] serverMembers = SampleUser.parseArray(json);

        for(SampleUser u : serverMembers){
            u.setRole(ServerConnection.Role.MEMBER);
        }


        //Retrieve ALL users
        response = new HttpRequestBuilder("/users")
                .method(HttpRequestBuilder.Method.GET)
                .perform();

        json = new String(response.getContent(), "UTF8");
        LinkedList<SampleUser> allUsers = new LinkedList<SampleUser>(Arrays.asList(SampleUser.parseArray(json)));


        LinkedList<SampleUser> users = new LinkedList<>();
        users.addAll(Arrays.asList(serverOwners));
        users.addAll(Arrays.asList(serverMembers));

        //go through every user in the list, remove them from AllUsers list.
        for(SampleUser u: users){
            allUsers.removeIf(user -> user.getUrl().equals(u.getUrl()));
        }

        //retrieve all invites!
        response = new HttpRequestBuilder("/invites")
                .method(HttpRequestBuilder.Method.GET)
                .perform();

        json = new String(response.getContent(), "UTF8");
        Invite[] invites = Invite.parseArray(json);

        //Finally go through all invites and remove them from the AllUsers list (Dont want a user being invited twice!)
        for(Invite i : invites){
            response = new HttpRequestBuilder("/invites/" + i.getId() + "/invited_user_id")
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            json = new String(response.getContent(), "UTF8");
            SampleUser u = SampleUser.parse(json);

            allUsers.removeIf(user -> user.getUrl().equals(u.getUrl()));
        }

        users.addAll(allUsers);

        return Either.right(users);

        } catch (IOException | ServerException e) {
            return Either.left(e);
        }
    }

    @Override
    protected void onPostExecute(Either<Exception, List<SampleUser>> either) {
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
