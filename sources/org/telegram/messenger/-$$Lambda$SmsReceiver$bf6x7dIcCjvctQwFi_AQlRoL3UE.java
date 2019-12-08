package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SmsReceiver$bf6x7dIcCjvctQwFi_AQlRoL3UE implements Runnable {
    private final /* synthetic */ String f$0;

    public /* synthetic */ -$$Lambda$SmsReceiver$bf6x7dIcCjvctQwFi_AQlRoL3UE(String str) {
        this.f$0 = str;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, this.f$0);
    }
}
