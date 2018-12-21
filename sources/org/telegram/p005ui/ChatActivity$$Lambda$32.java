package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.AudioSelectActivity.AudioSelectActivityDelegate;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$32 */
final /* synthetic */ class ChatActivity$$Lambda$32 implements AudioSelectActivityDelegate {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$32(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void didSelectAudio(ArrayList arrayList) {
        this.arg$1.lambda$processSelectedAttach$39$ChatActivity(arrayList);
    }
}
