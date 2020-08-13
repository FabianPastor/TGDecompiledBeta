package org.telegram.messenger.voip;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.ui.Components.voip.VoIPHelper;

public final class VoIPPendingCall {
    private final Activity activity;
    private Handler handler;
    private NotificationCenter notificationCenter;
    private final NotificationCenter.NotificationCenterDelegate observer = new NotificationCenter.NotificationCenterDelegate() {
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.didUpdateConnectionState) {
                boolean unused = VoIPPendingCall.this.onConnectionStateUpdated(i2, false);
            }
        }
    };
    private final Runnable releaseRunnable = new Runnable() {
        public final void run() {
            VoIPPendingCall.this.lambda$new$0$VoIPPendingCall();
        }
    };
    private boolean released;
    private final int userId;
    private final boolean video;

    public static VoIPPendingCall startOrSchedule(Activity activity2, int i, boolean z) {
        return new VoIPPendingCall(activity2, i, z, 1000);
    }

    public /* synthetic */ void lambda$new$0$VoIPPendingCall() {
        onConnectionStateUpdated(UserConfig.selectedAccount, true);
    }

    private VoIPPendingCall(Activity activity2, int i, boolean z, long j) {
        this.activity = activity2;
        this.userId = i;
        this.video = z;
        if (!onConnectionStateUpdated(UserConfig.selectedAccount, false)) {
            NotificationCenter instance = NotificationCenter.getInstance(UserConfig.selectedAccount);
            this.notificationCenter = instance;
            instance.addObserver(this.observer, NotificationCenter.didUpdateConnectionState);
            Handler handler2 = new Handler(Looper.myLooper());
            this.handler = handler2;
            handler2.postDelayed(this.releaseRunnable, j);
        }
    }

    /* access modifiers changed from: private */
    public boolean onConnectionStateUpdated(int i, boolean z) {
        boolean z2 = false;
        if (this.released || (!z && !isConnected(i) && !isAirplaneMode())) {
            return false;
        }
        MessagesController instance = MessagesController.getInstance(i);
        TLRPC$User user = instance.getUser(Integer.valueOf(this.userId));
        if (user != null) {
            TLRPC$UserFull userFull = instance.getUserFull(user.id);
            boolean z3 = this.video;
            if (userFull != null && userFull.video_calls_available) {
                z2 = true;
            }
            VoIPHelper.startCall(user, z3, z2, this.activity, userFull);
        } else if (isAirplaneMode()) {
            VoIPHelper.startCall((TLRPC$User) null, this.video, false, this.activity, (TLRPC$UserFull) null);
        }
        release();
        return true;
    }

    private boolean isConnected(int i) {
        return ConnectionsManager.getInstance(i).getConnectionState() == 3;
    }

    private boolean isAirplaneMode() {
        return Settings.System.getInt(this.activity.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public void release() {
        if (!this.released) {
            NotificationCenter notificationCenter2 = this.notificationCenter;
            if (notificationCenter2 != null) {
                notificationCenter2.removeObserver(this.observer, NotificationCenter.didUpdateConnectionState);
            }
            Handler handler2 = this.handler;
            if (handler2 != null) {
                handler2.removeCallbacks(this.releaseRunnable);
            }
            this.released = true;
        }
    }
}
