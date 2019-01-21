package org.telegram.ui;

final /* synthetic */ class PaymentFormActivity$TelegramWebviewProxy$$Lambda$0 implements Runnable {
    private final TelegramWebviewProxy arg$1;
    private final String arg$2;
    private final String arg$3;

    PaymentFormActivity$TelegramWebviewProxy$$Lambda$0(TelegramWebviewProxy telegramWebviewProxy, String str, String str2) {
        this.arg$1 = telegramWebviewProxy;
        this.arg$2 = str;
        this.arg$3 = str2;
    }

    public void run() {
        this.arg$1.lambda$postEvent$0$PaymentFormActivity$TelegramWebviewProxy(this.arg$2, this.arg$3);
    }
}
