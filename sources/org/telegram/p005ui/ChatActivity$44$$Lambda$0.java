package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.AudioSelectActivity.AudioSelectActivityDelegate;
import org.telegram.p005ui.ChatActivity.CLASSNAME;

/* renamed from: org.telegram.ui.ChatActivity$44$$Lambda$0 */
final /* synthetic */ class ChatActivity$44$$Lambda$0 implements AudioSelectActivityDelegate {
    private final CLASSNAME arg$1;
    private final BaseFragment arg$2;

    ChatActivity$44$$Lambda$0(CLASSNAME CLASSNAME, BaseFragment baseFragment) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = baseFragment;
    }

    public void didSelectAudio(ArrayList arrayList) {
        this.arg$1.lambda$startMusicSelectActivity$0$ChatActivity$44(this.arg$2, arrayList);
    }
}
