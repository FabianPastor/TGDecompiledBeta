package org.telegram.ui.Components;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsItemAnimator$cc5l3oNCPWmFl8oTuclZaKvqaQ8 implements Runnable {
    private final /* synthetic */ DialogsItemAnimator f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$DialogsItemAnimator$cc5l3oNCPWmFl8oTuclZaKvqaQ8(DialogsItemAnimator dialogsItemAnimator, ArrayList arrayList) {
        this.f$0 = dialogsItemAnimator;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$runPendingAnimations$1$DialogsItemAnimator(this.f$1);
    }
}