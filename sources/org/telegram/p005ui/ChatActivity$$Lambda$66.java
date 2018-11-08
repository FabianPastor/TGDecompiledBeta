package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$66 */
final /* synthetic */ class ChatActivity$$Lambda$66 implements RequestDelegate {
    static final RequestDelegate $instance = new ChatActivity$$Lambda$66();

    private ChatActivity$$Lambda$66() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        ChatActivity.lambda$null$65$ChatActivity(tLObject, tL_error);
    }
}
