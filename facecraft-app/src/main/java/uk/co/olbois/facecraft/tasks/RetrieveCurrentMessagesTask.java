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

        return Either.right(Arrays.asList(currentMessages));

    } catch (IOException | ServerException e) {
        return Either.left(e);
    }
    }

    /**
     * return a list of messages if there was no error
     * otherwise return the exception
     * @param either
     */
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
