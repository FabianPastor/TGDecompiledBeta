package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;
import org.telegram.ui.ChatActivity.AnonymousClass52;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$52$mS5YGju3JHENd3dVt3Zb6PnGz9o implements AudioSelectActivityDelegate {
    private final /* synthetic */ AnonymousClass52 f$0;
    private final /* synthetic */ BaseFragment f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$52$mS5YGju3JHENd3dVt3Zb6PnGz9o(AnonymousClass52 anonymousClass52, BaseFragment baseFragment) {
        this.f$0 = anonymousClass52;
        this.f$1 = baseFragment;
    }

    public final void didSelectAudio(ArrayList arrayList, boolean z, int i) {
        this.f$0.lambda$startMusicSelectActivity$0$ChatActivity$52(this.f$1, arrayList, z, i);
    }
}
