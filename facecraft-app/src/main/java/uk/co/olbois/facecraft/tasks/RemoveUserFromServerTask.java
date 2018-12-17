package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class RemoveUserFromServerTask extends AsyncTask<SampleUser, Void, Either<Exception, Boolean>> {

    private OnResponseListener<Boolean> onResponseListener;
    private String path;

    //path = /servers/.../members or /servers/.../owners
    public RemoveUserFromServerTask(String path, OnResponseListener<Boolean> onResponseListener){
        this.path = path;
        this.onResponseListener = onResponseListener;
    }

    @Override
    protected Either<Exception, Boolean> doInBackground(SampleUser... sampleUsers) {
        SampleUser u = sampleUsers[0];


        try {
            //Only users from the members list can be deleted, therefore get a list of the members
            HttpResponse response = new HttpRequestBuilder(path)
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            String json = new String(response.getContent(), "UTF8");
            LinkedList<SampleUser> springUsers = new LinkedList<>(Arrays.asList(SampleUser.parseArray(json)));

            //if the user we wish to remove exists in the list of users, delete him from the list
            springUsers.removeIf(s -> s.getUrl().equals(u.getUrl()));


            //EMPTY THE uri-list with an empty put
            response = new HttpRequestBuilder(path)
                    .method(HttpRequestBuilder.Method.PUT)
                    .withRequestBody("text/uri-list", new byte[0])
                    .perform();

            //Patch in the users, effectively append them to the list, one by one.
            for(SampleUser user : springUsers){
                response = new HttpRequestBuilder(path)
                        .method(HttpRequestBuilder.Method.PATCH)
                        .withRequestBody("text/uri-list", user.getUrl().getBytes())
                        .perform();
            }

            return Either.right(true);
        } catch (IOException | ServerException e) {
            return Either.left(e);
        }
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
