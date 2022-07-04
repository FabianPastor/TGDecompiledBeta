package org.telegram.messenger.voip;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.voip.VoIPHelper;

public final class VoIPPendingCall {
    private AccountInstance accountInstance;
    private final Activity activity;
    private Handler handler;
    private NotificationCenter notificationCenter;
    private final NotificationCenter.NotificationCenterDelegate observer;
    private final Runnable releaseRunnable;
    private boolean released;
    private final long userId;
    private final boolean video;

    public static VoIPPendingCall startOrSchedule(Activity activity2, long userId2, boolean video2, AccountInstance accountInstance2) {
        return new VoIPPendingCall(activity2, userId2, video2, 1000, accountInstance2);
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-voip-VoIPPendingCall  reason: not valid java name */
    public /* synthetic */ void m2432lambda$new$0$orgtelegrammessengervoipVoIPPendingCall(int id, int account, Object[] args) {
        if (id == NotificationCenter.didUpdateConnectionState) {
            onConnectionStateUpdated(false);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-messenger-voip-VoIPPendingCall  reason: not valid java name */
    public /* synthetic */ void m2433lambda$new$1$orgtelegrammessengervoipVoIPPendingCall() {
        onConnectionStateUpdated(true);
    }

    private VoIPPendingCall(Activity activity2, long userId2, boolean video2, long expirationTime, AccountInstance accountInstance2) {
        VoIPPendingCall$$ExternalSyntheticLambda1 voIPPendingCall$$ExternalSyntheticLambda1 = new VoIPPendingCall$$ExternalSyntheticLambda1(this);
        this.observer = voIPPendingCall$$ExternalSyntheticLambda1;
        VoIPPendingCall$$ExternalSyntheticLambda0 voIPPendingCall$$ExternalSyntheticLambda0 = new VoIPPendingCall$$ExternalSyntheticLambda0(this);
        this.releaseRunnable = voIPPendingCall$$ExternalSyntheticLambda0;
        this.activity = activity2;
        this.userId = userId2;
        this.video = video2;
        this.accountInstance = accountInstance2;
        if (!onConnectionStateUpdated(false)) {
            NotificationCenter instance = NotificationCenter.getInstance(UserConfig.selectedAccount);
            this.notificationCenter = instance;
            instance.addObserver(voIPPendingCall$$ExternalSyntheticLambda1, NotificationCenter.didUpdateConnectionState);
            Handler handler2 = new Handler(Looper.myLooper());
            this.handler = handler2;
            handler2.postDelayed(voIPPendingCall$$ExternalSyntheticLambda0, expirationTime);
        }
    }

    private boolean onConnectionStateUpdated(boolean force) {
        if (this.released || (!force && !isConnected(this.accountInstance) && !isAirplaneMode())) {
            return false;
        }
        MessagesController messagesController = this.accountInstance.getMessagesController();
        TLRPC.User user = messagesController.getUser(Long.valueOf(this.userId));
        if (user != null) {
            TLRPC.UserFull userFull = messagesController.getUserFull(user.id);
            VoIPHelper.startCall(user, this.video, userFull != null && userFull.video_calls_available, this.activity, userFull, this.accountInstance);
        } else if (isAirplaneMode()) {
            VoIPHelper.startCall((TLRPC.User) null, this.video, false, this.activity, (TLRPC.UserFull) null, this.accountInstance);
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
