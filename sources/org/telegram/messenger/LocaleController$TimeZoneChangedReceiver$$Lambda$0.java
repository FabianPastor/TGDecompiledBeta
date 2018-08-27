package org.telegram.messenger;

final /* synthetic */ class LocaleController$TimeZoneChangedReceiver$$Lambda$0 implements Runnable {
    private final TimeZoneChangedReceiver arg$1;

    LocaleController$TimeZoneChangedReceiver$$Lambda$0(TimeZoneChangedReceiver timeZoneChangedReceiver) {
        this.arg$1 = timeZoneChangedReceiver;
    }

    public void run() {
        this.arg$1.lambda$onReceive$0$LocaleController$TimeZoneChangedReceiver();
    }
}
