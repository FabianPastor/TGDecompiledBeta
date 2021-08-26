package org.telegram.messenger;

public final /* synthetic */ class NotificationRepeat$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ int f$0;

    public /* synthetic */ NotificationRepeat$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void run() {
        NotificationsController.getInstance(this.f$0).repeatNotificationMaybe();
    }
}
