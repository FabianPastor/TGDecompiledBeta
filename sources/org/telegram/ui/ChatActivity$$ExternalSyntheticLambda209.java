package org.telegram.ui;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.LanguageDetector;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda209 implements LanguageDetector.ExceptionCallback {
    public final /* synthetic */ AtomicBoolean f$0;
    public final /* synthetic */ AtomicReference f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda209(AtomicBoolean atomicBoolean, AtomicReference atomicReference) {
        this.f$0 = atomicBoolean;
        this.f$1 = atomicReference;
    }

    public final void run(Exception exc) {
        ChatActivity.lambda$createMenu$172(this.f$0, this.f$1, exc);
    }
}
