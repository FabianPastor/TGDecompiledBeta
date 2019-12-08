package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$2jCjNxIDB86P3v8tJd7kuH27GIk implements AudioSelectActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$2jCjNxIDB86P3v8tJd7kuH27GIk(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void didSelectAudio(ArrayList arrayList, boolean z, int i) {
        this.f$0.lambda$processSelectedAttach$50$ChatActivity(arrayList, z, i);
    }
}
