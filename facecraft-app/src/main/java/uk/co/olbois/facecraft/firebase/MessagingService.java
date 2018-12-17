package uk.co.olbois.facecraft.firebase;
import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.ServerException;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();
        Map<String, String> data = remoteMessage.getData();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String[] inviteUrl = data.get("invite_url").split("/");
        String path = "/" + inviteUrl[inviteUrl.length-2] + "/" + inviteUrl[inviteUrl.length-1];

        NotificationChannel channel = new NotificationChannel("inv_ch","Invite Channel", NotificationManager.IMPORTANCE_DEFAULT );
        channel.setDescription("Channel used for invite management!");

        mNotificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "inv_ch")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody);

        int mNotificationId = (int)System.currentTimeMillis();
        mNotificationManager.notify(mNotificationId, mBuilder.build());

        try {
            if(true){
                HttpResponse response = new HttpRequestBuilder(path + "/invited_user_id")
                        .method(HttpRequestBuilder.Method.GET)
                        .perform();
                String json = new String(response.getContent(), "UTF8");
                SampleUser u = SampleUser.parse(json);

                response = new HttpRequestBuilder(path + "/server")
                        .method(HttpRequestBuilder.Method.GET)
                        .perform();
                json = new String(response.getContent(), "UTF8");
                ServerConnection c = ServerConnection.parse(json);

                response = new HttpRequestBuilder("/servers/" + c.getId() + "/members")
                        .method(HttpRequestBuilder.Method.PATCH)
                        .withRequestBody("text/uri-list", u.getUrl().getBytes())
                        .perform();

                response = new HttpRequestBuilder(path)
                        .method(HttpRequestBuilder.Method.DELETE)
                        .perform();
            }
            else{
                HttpResponse response = new HttpRequestBuilder(path)
                        .method(HttpRequestBuilder.Method.DELETE)
                        .perform();
            }
        } catch (IOException | ServerException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNewToken(String token){
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void scheduleJob(){

    }

    private void sendRegistrationToServer(String token){

    }

    private void sendNotification(String messageBody){

    }
}
