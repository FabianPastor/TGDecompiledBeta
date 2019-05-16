package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;
import org.telegram.ui.ChatActivity.AnonymousClass48;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$48$2D5YNE6pSSIdPM1jM7dSsnPKESA implements AudioSelectActivityDelegate {
    private final /* synthetic */ AnonymousClass48 f$0;
    private final /* synthetic */ BaseFragment f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$48$2D5YNE6pSSIdPM1jM7dSsnPKESA(AnonymousClass48 anonymousClass48, BaseFragment baseFragment) {
        this.f$0 = anonymousClass48;
        this.f$1 = baseFragment;
    }

    public final void didSelectAudio(ArrayList arrayList) {
        this.f$0.lambda$startMusicSelectActivity$0$ChatActivity$48(this.f$1, arrayList);
    }
}
