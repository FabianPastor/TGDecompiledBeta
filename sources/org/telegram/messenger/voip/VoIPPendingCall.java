package org.telegram.messenger.voip;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
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
                boolean unused = VoIPPendingCall.this.onConnectionStateUpdated(i2);
            }
        }
    };
    private final Runnable releaseRunnable = new Runnable() {
        public final void run() {
            VoIPPendingCall.this.release();
        }
    };
    private boolean released;
    private final int userId;
    private final boolean video;

    public static VoIPPendingCall startOrSchedule(Activity activity2, int i, boolean z) {
        return new VoIPPendingCall(activity2, i, z, 2000);
    }

    private VoIPPendingCall(Activity activity2, int i, boolean z, long j) {
        this.activity = activity2;
        this.userId = i;
        this.video = z;
        if (!onConnectionStateUpdated(UserConfig.selectedAccount)) {
            NotificationCenter instance = NotificationCenter.getInstance(UserConfig.selectedAccount);
            this.notificationCenter = instance;
            instance.addObserver(this.observer, NotificationCenter.didUpdateConnectionState);
            Handler handler2 = new Handler(Looper.myLooper());
            this.handler = handler2;
            handler2.postDelayed(this.releaseRunnable, j);
        }
    }

    /* access modifiers changed from: private */
    public boolean onConnectionStateUpdated(int i) {
        boolean z = false;
        if (this.released || ConnectionsManager.getInstance(i).getConnectionState() != 3) {
            return false;
        }
        MessagesController instance = MessagesController.getInstance(i);
        TLRPC$User user = instance.getUser(Integer.valueOf(this.userId));
        if (user != null) {
            TLRPC$UserFull userFull = instance.getUserFull(user.id);
            boolean z2 = this.video;
            if (userFull != null && userFull.video_calls_available) {
                z = true;
            }
            VoIPHelper.startCall(user, z2, z, this.activity, userFull);
        }
        release();
        return true;
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
