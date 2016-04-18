package mitso.v.homework_19;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends AppCompatActivity {

    private GoogleCloudMessaging gsm;
    private String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        GcmPushHelper.checkGooglePlayServices(this);

        gsm = GoogleCloudMessaging.getInstance(this);
        regId = GcmPushHelper.getRegistrationId(this);

        if (TextUtils.isEmpty(regId)) {
            GcmPushHelper.registerGSMAsync(this, gsm);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(999);
    }
}
