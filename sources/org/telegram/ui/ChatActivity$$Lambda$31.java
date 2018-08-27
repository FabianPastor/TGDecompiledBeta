package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;

final /* synthetic */ class ChatActivity$$Lambda$31 implements AudioSelectActivityDelegate {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$31(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void didSelectAudio(ArrayList arrayList) {
        this.arg$1.lambda$processSelectedAttach$38$ChatActivity(arrayList);
    }
}
