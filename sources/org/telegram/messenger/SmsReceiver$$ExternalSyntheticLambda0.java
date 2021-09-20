package org.telegram.messenger;

public final /* synthetic */ class SmsReceiver$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ String f$0;

    public /* synthetic */ SmsReceiver$$ExternalSyntheticLambda0(String str) {
        this.f$0 = str;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, this.f$0);
    }
}
