package org.telegram.ui.Components;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TypingDotsDrawable$6mZKSEfaAngfDGlsoqdZ2efS_EU implements Runnable {
    private final /* synthetic */ TypingDotsDrawable f$0;

    public /* synthetic */ -$$Lambda$TypingDotsDrawable$6mZKSEfaAngfDGlsoqdZ2efS_EU(TypingDotsDrawable typingDotsDrawable) {
        this.f$0 = typingDotsDrawable;
    }

    public final void run() {
        this.f$0.checkUpdate();
    }
}
