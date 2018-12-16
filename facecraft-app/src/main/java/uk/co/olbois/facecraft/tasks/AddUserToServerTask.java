package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class AddUserToServerTask extends AsyncTask<SampleUser, Void, Either<Exception, Boolean>> {

    private OnResponseListener<Boolean> onResponseListener;
    private String path;

    public AddUserToServerTask(String path, OnResponseListener<Boolean> onResponseListener){
        this.path = path;
        this.onResponseListener = onResponseListener;
    }

    //path = /servers/.../members
    @Override
    protected Either<Exception, Boolean> doInBackground(SampleUser... sampleUsers) {
        SampleUser u = sampleUsers[0];

        try {
            HttpResponse response = new HttpRequestBuilder(path)
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            String json = new String(response.getContent(), "UTF8");
            LinkedList<SampleUser> springUsers = new LinkedList<>(Arrays.asList(SampleUser.parseArray(json)));

            if(!springUsers.stream().anyMatch(s -> s.getUrl().equals(u.getUrl()))){
                response = new HttpRequestBuilder(path)
                        .method(HttpRequestBuilder.Method.PATCH)
                        .withRequestBody("text/uri-list", u.getUrl().getBytes())
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
