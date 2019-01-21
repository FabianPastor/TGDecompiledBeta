package org.telegram.messenger;

final /* synthetic */ class SmsReceiver$$Lambda$0 implements Runnable {
    private final String arg$1;

    SmsReceiver$$Lambda$0(String str) {
        this.arg$1 = str;
    }

    public void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, this.arg$1);
    }
}
