package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;
import org.telegram.ui.ChatActivity.AnonymousClass50;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$50$6IfyO7WRX5q4i12M9ztytme5Ykw implements AudioSelectActivityDelegate {
    private final /* synthetic */ AnonymousClass50 f$0;
    private final /* synthetic */ BaseFragment f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$50$6IfyO7WRX5q4i12M9ztytme5Ykw(AnonymousClass50 anonymousClass50, BaseFragment baseFragment) {
        this.f$0 = anonymousClass50;
        this.f$1 = baseFragment;
    }

    public final void didSelectAudio(ArrayList arrayList) {
        this.f$0.lambda$startMusicSelectActivity$0$ChatActivity$50(this.f$1, arrayList);
    }
}
