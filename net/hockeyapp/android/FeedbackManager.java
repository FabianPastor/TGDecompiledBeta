package net.hockeyapp.android;

import android.content.BroadcastReceiver;
import net.hockeyapp.android.objects.FeedbackUserDataElement;

public class FeedbackManager {
    private static String identifier = null;
    private static FeedbackManagerListener lastListener = null;
    private static boolean notificationActive = false;
    private static BroadcastReceiver receiver = null;
    private static FeedbackUserDataElement requireUserEmail = FeedbackUserDataElement.REQUIRED;
    private static FeedbackUserDataElement requireUserName = FeedbackUserDataElement.REQUIRED;
    private static String urlString = null;

    public static FeedbackManagerListener getLastListener() {
        return lastListener;
    }

    public static FeedbackUserDataElement getRequireUserName() {
        return requireUserName;
    }

    public static FeedbackUserDataElement getRequireUserEmail() {
        return requireUserEmail;
    }
}
