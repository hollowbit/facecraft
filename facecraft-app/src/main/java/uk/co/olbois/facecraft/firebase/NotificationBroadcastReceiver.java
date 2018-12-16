package uk.co.olbois.facecraft.firebase;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.IOException;

import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.ServerException;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static class param {
        public static final String INITIAL_MESSAGE = "initial_message";
        public static final String NOTIFICATION_ID = "id";
    }

    public static final String ACTION_ACCEPT="uk.co.olbois.facecraft.ACCEPT";
    public static final String ACTION_DENY = "uk.co.olbois.facecraft.DENY";

    @Override
    public void onReceive(Context context, Intent intent) {

        //retrieve the message that was sent
        String[] messageData = intent.getStringArrayExtra(param.INITIAL_MESSAGE);
        int notification_id = intent.getIntExtra(param.NOTIFICATION_ID, 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String action = intent.getAction();

        PendingResult pendingResult = goAsync();
        Task asyncTask;
        switch(action){
            case ACTION_ACCEPT:
                asyncTask = new Task(pendingResult, true, messageData);
                asyncTask.execute();
                break;
            case ACTION_DENY:
                asyncTask = new Task(pendingResult, false, messageData);
                asyncTask.execute();
                break;
        }

        notificationManager.cancel(notification_id);
    }

    private static class Task extends AsyncTask<Void, Void, Either<Exception, Boolean>> {
        private final PendingResult pendingResult;
        private final String[] messageData;
        private final Boolean accepted;

        private Task(PendingResult pendingResult, Boolean accepted, String[] message){
            this.pendingResult = pendingResult;
            this.accepted = accepted;
            this.messageData = message;
        }

        @Override
        protected Either<Exception, Boolean> doInBackground(Void... voids) {

            try {
                if(accepted){
                    HttpResponse response = new HttpRequestBuilder(messageData[2] + "/invited_user_id")
                            .method(HttpRequestBuilder.Method.GET)
                            .perform();
                    String json = new String(response.getContent(), "UTF8");
                    SampleUser u = SampleUser.parse(json);

                    response = new HttpRequestBuilder(messageData[2] + "/server")
                            .method(HttpRequestBuilder.Method.GET)
                            .perform();
                    json = new String(response.getContent(), "UTF8");
                    ServerConnection c = ServerConnection.parse(json);

                    response = new HttpRequestBuilder("/servers/" + c.getId() + "/members")
                            .method(HttpRequestBuilder.Method.PATCH)
                            .withRequestBody("text/uri-list", u.getUrl().getBytes())
                            .perform();

                    response = new HttpRequestBuilder(messageData[2])
                            .method(HttpRequestBuilder.Method.DELETE)
                            .perform();
                }
                else{
                    HttpResponse response = new HttpRequestBuilder(messageData[2])
                            .method(HttpRequestBuilder.Method.DELETE)
                            .perform();
                }
            } catch (IOException | ServerException e) {
            }
            return Either.right(true);
        }

        @Override
        protected void onPostExecute(Either<Exception, Boolean> either) {
            pendingResult.finish();

        }
    }
}
