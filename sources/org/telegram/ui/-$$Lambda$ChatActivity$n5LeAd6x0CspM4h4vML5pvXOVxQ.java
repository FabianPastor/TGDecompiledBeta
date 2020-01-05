package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$n5LeAd6x0CspM4h4vML5pvXOVxQ implements AudioSelectActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ BaseFragment f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$n5LeAd6x0CspM4h4vML5pvXOVxQ(ChatActivity chatActivity, BaseFragment baseFragment) {
        this.f$0 = chatActivity;
        this.f$1 = baseFragment;
    }

    public final void didSelectAudio(ArrayList arrayList, CharSequence charSequence, boolean z, int i) {
        this.f$0.lambda$openMusicSelectActivity$53$ChatActivity(this.f$1, arrayList, charSequence, z, i);
    }
}
