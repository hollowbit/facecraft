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

        //retrieve the message that was sent and the notification id (timestamp pretty much)
        String invitePath = intent.getStringExtra(param.INITIAL_MESSAGE);
        int notification_id = intent.getIntExtra(param.NOTIFICATION_ID, 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Action is either "ACCEPT" or "DENY" ( the two buttons of notification)
        String action = intent.getAction();

        PendingResult pendingResult = goAsync();
        HandleInviteTask asyncTask;
        switch(action){
            case ACTION_ACCEPT:
                asyncTask = new HandleInviteTask(pendingResult, true, invitePath);
                asyncTask.execute();
                break;
            case ACTION_DENY:
                asyncTask = new HandleInviteTask(pendingResult, false, invitePath);
                asyncTask.execute();
                break;
        }

        notificationManager.cancel(notification_id);
    }

    //An asynchronous task that handles a invite in the database based on whether the decline or accept button was clicked
    private static class HandleInviteTask extends AsyncTask<Void, Void, Either<Exception, Boolean>> {
        private final PendingResult pendingResult;
        private final String invitePath;
        private final Boolean accepted;

        private HandleInviteTask(PendingResult pendingResult, Boolean accepted, String message){
            this.pendingResult = pendingResult;
            this.accepted = accepted;
            this.invitePath = message;
        }

        @Override
        protected Either<Exception, Boolean> doInBackground(Void... voids) {

            try {
                //Accept button was clicked
                if(accepted){
                    //Get the user who sent the invite
                    HttpResponse response = new HttpRequestBuilder(invitePath + "/invited_user_id")
                            .method(HttpRequestBuilder.Method.GET)
                            .perform();
                    String json = new String(response.getContent(), "UTF8");
                    SampleUser u = SampleUser.parse(json);

                    //Get the server the user was invited to
                    response = new HttpRequestBuilder(invitePath + "/server")
                            .method(HttpRequestBuilder.Method.GET)
                            .perform();
                    json = new String(response.getContent(), "UTF8");
                    ServerConnection c = ServerConnection.parse(json);

                    //Add the user into the servers "members" list
                    response = new HttpRequestBuilder("/servers/" + c.getId() + "/members")
                            .method(HttpRequestBuilder.Method.PATCH)
                            .withRequestBody("text/uri-list", u.getUrl().getBytes())
                            .perform();

                    //Delete the invite
                    response = new HttpRequestBuilder(invitePath)
                            .method(HttpRequestBuilder.Method.DELETE)
                            .perform();
                }
                //Decline button was clicked
                else{
                    //Delete the invite from the database!
                    HttpResponse response = new HttpRequestBuilder(invitePath)
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
