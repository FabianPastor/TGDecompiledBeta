package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChatActivity$$Lambda$53 implements RequestDelegate {
    static final RequestDelegate $instance = new ChatActivity$$Lambda$53();

    private ChatActivity$$Lambda$53() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$68(tLObject));
    }
}
