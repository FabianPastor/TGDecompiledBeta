package org.telegram.ui;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.LanguageDetector;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda156 implements LanguageDetector.ExceptionCallback {
    public final /* synthetic */ AtomicBoolean f$0;
    public final /* synthetic */ AtomicReference f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda156(AtomicBoolean atomicBoolean, AtomicReference atomicReference) {
        this.f$0 = atomicBoolean;
        this.f$1 = atomicReference;
    }

    public final void run(Exception exc) {
        ChatActivity.lambda$createMenu$132(this.f$0, this.f$1, exc);
    }
}
