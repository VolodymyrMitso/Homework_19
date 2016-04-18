package mitso.v.homework_19;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyGcmListenerService extends GcmListenerService {

    public static final String MY_GCM_LISTENER_SERVICE_TAG  = "GCM_LISTENER_SERVICE";
    private boolean isBitmapMade;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        final String title = data.getString("title");
        final String message = data.getString("message");
        final String imageSrc = data.getString("imageSrc");

        showNotification(title, message, imageSrc);

        Log.e(MY_GCM_LISTENER_SERVICE_TAG, "PUSH RECEIVED");
    }

    private void showNotification(final String message, final String title, final String imageSrc) {

        Bitmap bitmap = getBitmapFromURL(imageSrc);

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_cloud_notification)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound_notification))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false);

        if (!TextUtils.isEmpty(imageSrc) && isBitmapMade) {
            Log.e(MY_GCM_LISTENER_SERVICE_TAG, imageSrc);

            final Intent activityIntent = new Intent(this, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            final PendingIntent openIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_ONE_SHOT);

            if (Build.VERSION.SDK_INT >= 21)
                notificationBuilder.addAction(R.drawable.ic_cloud_notification, "Open App", openIntent);

            final Notification.BigPictureStyle bigPicStyle = new Notification.BigPictureStyle();
            bigPicStyle.setBigContentTitle(title);
            bigPicStyle.setSummaryText(message);

            bigPicStyle.bigPicture(bitmap);

            notificationBuilder.setStyle(bigPicStyle);

        } else {
            Log.e(MY_GCM_LISTENER_SERVICE_TAG, "NULL OR EMPTY");
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(999, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            isBitmapMade = true;
            return bitmap;
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.e(MY_GCM_LISTENER_SERVICE_TAG, errors.toString());
            isBitmapMade = false;
            return null;
        }
    }
}