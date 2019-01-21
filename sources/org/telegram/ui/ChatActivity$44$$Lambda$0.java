package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;
import org.telegram.ui.ChatActivity.AnonymousClass44;

final /* synthetic */ class ChatActivity$44$$Lambda$0 implements AudioSelectActivityDelegate {
    private final AnonymousClass44 arg$1;
    private final BaseFragment arg$2;

    ChatActivity$44$$Lambda$0(AnonymousClass44 anonymousClass44, BaseFragment baseFragment) {
        this.arg$1 = anonymousClass44;
        this.arg$2 = baseFragment;
    }

    public void didSelectAudio(ArrayList arrayList) {
        this.arg$1.lambda$startMusicSelectActivity$0$ChatActivity$44(this.arg$2, arrayList);
    }
}
