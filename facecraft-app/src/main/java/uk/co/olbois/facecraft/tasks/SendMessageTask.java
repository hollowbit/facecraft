package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
        SimpleDateFormat sdl = new SimpleDateFormat("MMM dd, YYYY h:mm:ss aa");
        String dt = sdl.format(message.getDate());
        String msgStr = message.getUsername() + "Ω" + message.getSenderType() + "Ω" + message.getMessage()+ "Ω" + dt + "Ω" + message.getServerAddr() + "Ω" + message.getServerAddr();
        try {

            HttpResponse send = new HttpRequestBuilder(path + "/send")
                    .method(HttpRequestBuilder.Method.POST)
                    .withRequestBody("application/json", msgStr.getBytes())
                    .perform();


            if (message.getSenderType() != "null") {
                HttpResponse send2 = new HttpRequestBuilder(path)
                        .method(HttpRequestBuilder.Method.POST)
                        .withRequestBody("application/json", message.format().getBytes())
                        .perform();
            }

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
}//var username : String, var senderType : String, var message : String, var date : Date, var serverAddr : String, var url : String?
