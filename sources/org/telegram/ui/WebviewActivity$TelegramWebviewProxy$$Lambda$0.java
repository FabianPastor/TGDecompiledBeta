package org.telegram.ui;

final /* synthetic */ class WebviewActivity$TelegramWebviewProxy$$Lambda$0 implements Runnable {
    private final TelegramWebviewProxy arg$1;
    private final String arg$2;

    WebviewActivity$TelegramWebviewProxy$$Lambda$0(TelegramWebviewProxy telegramWebviewProxy, String str) {
        this.arg$1 = telegramWebviewProxy;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$postEvent$0$WebviewActivity$TelegramWebviewProxy(this.arg$2);
    }
}
