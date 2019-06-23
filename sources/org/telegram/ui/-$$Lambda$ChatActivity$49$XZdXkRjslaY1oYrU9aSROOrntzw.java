package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;
import org.telegram.ui.ChatActivity.AnonymousClass49;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$49$XZdXkRjslaY1oYrU9aSROOrntzw implements AudioSelectActivityDelegate {
    private final /* synthetic */ AnonymousClass49 f$0;
    private final /* synthetic */ BaseFragment f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$49$XZdXkRjslaY1oYrU9aSROOrntzw(AnonymousClass49 anonymousClass49, BaseFragment baseFragment) {
        this.f$0 = anonymousClass49;
        this.f$1 = baseFragment;
    }

    public final void didSelectAudio(ArrayList arrayList) {
        this.f$0.lambda$startMusicSelectActivity$0$ChatActivity$49(this.f$1, arrayList);
    }
}
