package org.telegram.p005ui;

import org.telegram.p005ui.Adapters.StickersAdapter.StickersAdapterDelegate;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$27 */
final /* synthetic */ class ChatActivity$$Lambda$27 implements StickersAdapterDelegate {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$27(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void needChangePanelVisibility(boolean z) {
        this.arg$1.lambda$initStickers$35$ChatActivity(z);
    }
}
