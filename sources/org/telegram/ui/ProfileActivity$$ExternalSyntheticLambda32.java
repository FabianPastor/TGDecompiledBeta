package org.telegram.ui;

import org.telegram.messenger.LanguageDetector;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda32 implements LanguageDetector.ExceptionCallback {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda32(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void run(Exception exc) {
        ProfileActivity.lambda$processOnClickOrPress$26(this.f$0, exc);
    }
}
