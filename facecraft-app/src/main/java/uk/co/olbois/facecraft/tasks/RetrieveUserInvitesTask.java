package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;
import android.util.Pair;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import io.grpc.Server;
import uk.co.olbois.facecraft.model.Invite;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.message.Message;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class RetrieveUserInvitesTask extends AsyncTask<SampleUser, Void, Either<Exception, List<Pair<ServerConnection, Invite>>>> {
    private String path;
    private OnResponseListener<List<Pair<ServerConnection,Invite>>> onResponseListener;

    public RetrieveUserInvitesTask(String path, OnResponseListener<List<Pair<ServerConnection,Invite>>> onResponseListener){
        this.path = path;
        this.onResponseListener = onResponseListener;
    }

    @Override
    protected Either<Exception, List<Pair<ServerConnection, Invite>>> doInBackground(SampleUser... sampleUsers) {
        SampleUser u = sampleUsers[0];

        HttpResponse response = null;
        try {

            List<Pair<ServerConnection, Invite>> userInvites = new LinkedList<>();

            //get all invites
            response = new HttpRequestBuilder(path)
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            String json = new String(response.getContent(), "UTF8");
            Invite[] allInvites = Invite.parseArray(json);

            //Loop through each invite, check if the received user is equal to the passed user
            for(Invite i : allInvites){
                response = new HttpRequestBuilder(path + "/" + i.getId() + "/invited_user_id")
                        .method(HttpRequestBuilder.Method.GET)
                        .perform();

                json = new String(response.getContent(), "UTF8");
                SampleUser inviteUser = SampleUser.parse(json);

                //We've found an invite for the user
                if(inviteUser.getUrl().equals(u.getUrl())){
                    response = new HttpRequestBuilder(path + "/" + i.getId() + "/server")
                            .method(HttpRequestBuilder.Method.GET)
                            .perform();

                    json = new String(response.getContent(), "UTF8");
                    ServerConnection connection = ServerConnection.parse(json);

                    userInvites.add(new Pair(connection, i));
                }
            }


            return Either.right(userInvites);

        } catch (IOException | ServerException e) {
            return Either.left(e);
        }
    }



    @Override
    protected void onPostExecute(Either<Exception, List<Pair<ServerConnection, Invite>>> either) {
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
