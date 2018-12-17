package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import io.grpc.Server;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpProgress;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class ChangeUserRoleTask extends AsyncTask<SampleUser, Void, Either<Exception, Boolean>> {

    private ServerConnection.Role role;
    private OnResponseListener<Boolean> onResponseListener;
    private ServerConnection connection;

    public ChangeUserRoleTask(ServerConnection.Role role, ServerConnection connection, OnResponseListener<Boolean> onResponseListener){
        this.role = role;
        this.onResponseListener = onResponseListener;
        this.connection = connection;
    }

    @Override
    protected Either<Exception, Boolean> doInBackground(SampleUser... sampleUsers) {
        SampleUser u = sampleUsers[0];
        u.setRole(role);

        try {
            //We can only ever change a user's role to the "Owner" role, therefore patch him in!
            HttpResponse response = new HttpRequestBuilder("/servers/" + connection.getId() + "/owners")
                .method(HttpRequestBuilder.Method.PATCH)
                .withRequestBody("text/uri-list", u.getUrl().getBytes())
                .perform();

            //A little bit of code re-usage, delete the user from the members list effectively
            RemoveUserFromServerTask removeUserFromServerTask = new RemoveUserFromServerTask("/servers/" + connection.getId() + "/members", new OnResponseListener<Boolean>() {
                @Override
                public void onResponse(Boolean data) {

                }

                @Override
                public void onProgress(HttpProgress value) {

                }

                @Override
                public void onError(Exception error) {

                }
            });

            removeUserFromServerTask.execute(u);
        } catch (IOException | ServerException e) {
            return Either.left(e);
        }
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
