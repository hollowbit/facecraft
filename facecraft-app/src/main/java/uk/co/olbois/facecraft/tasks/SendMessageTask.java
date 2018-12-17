package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.message.Message;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class SendMessageTask extends AsyncTask<Message, Void, Either<Exception, Boolean>> {

    private OnResponseListener<Boolean> onResponseListener;
    private String path;

    public SendMessageTask(String path, OnResponseListener<Boolean> onResponseListener){
        this.path = path;
        this.onResponseListener = onResponseListener;
    }

    //path = /servers/.../members
    @Override
    protected Either<Exception, Boolean> doInBackground(Message... messages) {

        Message message  = messages[0];

        try {

            HttpResponse send = new HttpRequestBuilder(path)
                    .method(HttpRequestBuilder.Method.POST)
                    .withRequestBody("application/json", message.format().getBytes())
                    .perform();

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
