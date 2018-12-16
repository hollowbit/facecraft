package uk.co.olbois.facecraft.firebase;
import uk.co.olbois.facecraft.R;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.ServerException;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
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

        Map<String, String> data = remoteMessage.getData();
        String[] messageData = new String[3];

        messageData[0] = data.get("title");
        messageData[1] = data.get("body");

        String[] inviteUrl = data.get("invite_url").split("/");
        messageData[2] = "/" + inviteUrl[inviteUrl.length-2] + "/" + inviteUrl[inviteUrl.length-1];




        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("inv_ch","Invite Channel", NotificationManager.IMPORTANCE_DEFAULT );
        channel.setDescription("Channel used for invite management!");
        mNotificationManager.createNotificationChannel(channel);

        int mNotificationId = (int)System.currentTimeMillis();


        String ACTION_ACCEPT="uk.co.olbois.facecraft.ACCEPT";
        String ACTION_DENY = "uk.co.olbois.facecraft.DENY";

        Intent acceptIntent = new Intent(this, NotificationBroadcastReceiver.class);
        acceptIntent.setAction(ACTION_ACCEPT);
        acceptIntent.putExtra(NotificationBroadcastReceiver.param.INITIAL_MESSAGE, messageData);
        acceptIntent.putExtra(NotificationBroadcastReceiver.param.NOTIFICATION_ID, mNotificationId);

        Intent declineIntent = new Intent(this, NotificationBroadcastReceiver.class);
        declineIntent.setAction(ACTION_DENY);
        declineIntent.putExtra(NotificationBroadcastReceiver.param.INITIAL_MESSAGE, messageData);
        declineIntent.putExtra(NotificationBroadcastReceiver.param.NOTIFICATION_ID, mNotificationId);

        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent declinePendingIntent = PendingIntent.getBroadcast(this, 0, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "inv_ch")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageData[0])
                .setContentText(messageData[1])
                .addAction(R.drawable.rounded, "ACCEPT", acceptPendingIntent)
                .addAction(R.drawable.rounded, "DECLINE", declinePendingIntent);

        mNotificationManager.notify(mNotificationId, mBuilder.build());
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
