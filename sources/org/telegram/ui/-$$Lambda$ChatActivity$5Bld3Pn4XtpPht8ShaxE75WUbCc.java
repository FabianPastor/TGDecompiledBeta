package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$5Bld3Pn4XtpPht8ShaxE75WUbCc implements AudioSelectActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ BaseFragment f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$5Bld3Pn4XtpPht8ShaxE75WUbCc(ChatActivity chatActivity, BaseFragment baseFragment) {
        this.f$0 = chatActivity;
        this.f$1 = baseFragment;
    }

    public final void didSelectAudio(ArrayList arrayList, CharSequence charSequence, boolean z, int i) {
        this.f$0.lambda$openMusicSelectActivity$50$ChatActivity(this.f$1, arrayList, charSequence, z, i);
    }
}
