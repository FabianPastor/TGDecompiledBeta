package org.telegram.ui.Components;

public final /* synthetic */ class TypingDotsDrawable$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ TypingDotsDrawable f$0;

    public /* synthetic */ TypingDotsDrawable$$ExternalSyntheticLambda0(TypingDotsDrawable typingDotsDrawable) {
        this.f$0 = typingDotsDrawable;
    }

    public final void run() {
        this.f$0.checkUpdate();
    }
}
