package uk.co.olbois.facecraft.tasks;

import android.content.BroadcastReceiver;
import android.os.AsyncTask;

import java.io.IOException;

import uk.co.olbois.facecraft.model.Invite;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class HandleInviteTask extends AsyncTask<Invite, Void, Either<Exception, Boolean>> {
    private final String invitePath;
    private final Boolean accepted;
    private OnResponseListener<Boolean> onResponseListener;

    public HandleInviteTask(Boolean accepted, String path, OnResponseListener<Boolean> onResponseListener) {
        this.accepted = accepted;
        this.invitePath = path;
        this.onResponseListener = onResponseListener;
    }

    @Override
    protected Either<Exception, Boolean> doInBackground(Invite... invites) {
        Invite i = invites[0];
        try {
            //Accept button was clicked
            if (accepted) {
                //Get initial invite sent in, make sure it still exists!
                HttpResponse response = new HttpRequestBuilder(invitePath + "/" + i.getId())
                        .method(HttpRequestBuilder.Method.GET)
                        .perform();

                if(response.getCode() == 404){
                    return Either.left(new IOException("That Invite no longer exists on the database!"));
                }

                //Get the user who sent the invite
                response = new HttpRequestBuilder(invitePath + "/" + i.getId() + "/invited_user_id")
                        .method(HttpRequestBuilder.Method.GET)
                        .expectingStatus(200)
                        .perform();

                if(response.getCode() == 404){
                    return Either.left(new IOException("That user no longer exists on the database!"));
                }

                String json = new String(response.getContent(), "UTF8");
                SampleUser u = SampleUser.parse(json);

                //Get the server the user was invited to
                response = new HttpRequestBuilder(invitePath + "/" + i.getId() + "/server")
                        .method(HttpRequestBuilder.Method.GET)
                        .perform();

                if(response.getCode() == 404){
                    return Either.left(new IOException("That server no longer exists on the database!"));
                }

                json = new String(response.getContent(), "UTF8");
                ServerConnection c = ServerConnection.parse(json);

                //Add the user into the servers "members" list
                response = new HttpRequestBuilder("/servers/" + c.getId() + "/members")
                        .method(HttpRequestBuilder.Method.PATCH)
                        .withRequestBody("text/uri-list", u.getUrl().getBytes())
                        .perform();

                //Delete the invite
                response = new HttpRequestBuilder(invitePath + "/" + i.getId())
                        .method(HttpRequestBuilder.Method.DELETE)
                        .perform();
            }
            //Decline button was clicked
            else {
                //Delete the invite from the database!
                HttpResponse response = new HttpRequestBuilder(invitePath + "/" + i.getId())
                        .method(HttpRequestBuilder.Method.DELETE)
                        .perform();
            }
        } catch (IOException | ServerException e) {
            return Either.left(e);
        }
        return Either.right(true);
    }

    @Override
    protected void onPostExecute(Either<Exception, Boolean> either) {
        if (onResponseListener == null)
            return;

        switch (either.getType()) {
            case LEFT:
                onResponseListener.onError(either.getLeft());
                break;
            case RIGHT:
                onResponseListener.onResponse(either.getRight());
                break;
        }
    }
}