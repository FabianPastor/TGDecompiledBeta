package org.telegram.messenger.voip;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.ui.Components.voip.VoIPHelper;

public final class VoIPPendingCall {
    private AccountInstance accountInstance;
    private final Activity activity;
    private Handler handler;
    private NotificationCenter notificationCenter;
    private final NotificationCenter.NotificationCenterDelegate observer;
    private final Runnable releaseRunnable;
    private boolean released;
    private final int userId;
    private final boolean video;

    public static VoIPPendingCall startOrSchedule(Activity activity2, int i, boolean z, AccountInstance accountInstance2) {
        return new VoIPPendingCall(activity2, i, z, 1000, accountInstance2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$VoIPPendingCall(int i, int i2, Object[] objArr) {
        if (i == NotificationCenter.didUpdateConnectionState) {
            onConnectionStateUpdated(false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$VoIPPendingCall() {
        onConnectionStateUpdated(true);
    }

    private VoIPPendingCall(Activity activity2, int i, boolean z, long j, AccountInstance accountInstance2) {
        $$Lambda$VoIPPendingCall$d9Fcvb5T4_aQs_zzulCXCagMFw r0 = new NotificationCenter.NotificationCenterDelegate() {
            public final void didReceivedNotification(int i, int i2, Object[] objArr) {
                VoIPPendingCall.this.lambda$new$0$VoIPPendingCall(i, i2, objArr);
            }
        };
        this.observer = r0;
        $$Lambda$VoIPPendingCall$8pKAoVuJaHx14zmyaJ4c3Y1teI r1 = new Runnable() {
            public final void run() {
                VoIPPendingCall.this.lambda$new$1$VoIPPendingCall();
            }
        };
        this.releaseRunnable = r1;
        this.activity = activity2;
        this.userId = i;
        this.video = z;
        this.accountInstance = accountInstance2;
        if (!onConnectionStateUpdated(false)) {
            NotificationCenter instance = NotificationCenter.getInstance(UserConfig.selectedAccount);
            this.notificationCenter = instance;
            instance.addObserver(r0, NotificationCenter.didUpdateConnectionState);
            Handler handler2 = new Handler(Looper.myLooper());
            this.handler = handler2;
            handler2.postDelayed(r1, j);
        }
    }

    private boolean onConnectionStateUpdated(boolean z) {
        if (this.released || (!z && !isConnected(this.accountInstance) && !isAirplaneMode())) {
            return false;
        }
        MessagesController messagesController = this.accountInstance.getMessagesController();
        TLRPC$User user = messagesController.getUser(Integer.valueOf(this.userId));
        if (user != null) {
            TLRPC$UserFull userFull = messagesController.getUserFull(user.id);
            VoIPHelper.startCall(user, this.video, userFull != null && userFull.video_calls_available, this.activity, userFull, this.accountInstance);
        } else if (isAirplaneMode()) {
            VoIPHelper.startCall((TLRPC$User) null, this.video, false, this.activity, (TLRPC$UserFull) null, this.accountInstance);
        }
        release();
        return true;
    }

    private boolean isConnected(AccountInstance accountInstance2) {
        return accountInstance2.getConnectionsManager().getConnectionState() == 3;
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
