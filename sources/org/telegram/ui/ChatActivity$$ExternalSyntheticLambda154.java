package org.telegram.ui;

import org.telegram.messenger.FileLog;
import org.telegram.messenger.LanguageDetector;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda154 implements LanguageDetector.ExceptionCallback {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda154 INSTANCE = new ChatActivity$$ExternalSyntheticLambda154();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda154() {
    }

    public final void run(Exception exc) {
        FileLog.e((Throwable) exc);
    }
}
