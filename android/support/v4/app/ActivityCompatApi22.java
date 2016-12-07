package android.support.v4.app;

import android.app.Activity;
import android.net.Uri;

class ActivityCompatApi22 {
    ActivityCompatApi22() {
    }

    public static Uri getReferrer(Activity activity) {
        return activity.getReferrer();
    }
}
