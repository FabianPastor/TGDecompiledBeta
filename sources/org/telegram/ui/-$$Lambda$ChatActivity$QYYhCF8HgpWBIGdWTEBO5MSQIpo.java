package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_sendScheduledMessages;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$QYYhCF8HgpWBIGdWTEBO5MSQIpo implements RequestDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ TL_messages_sendScheduledMessages f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$QYYhCF8HgpWBIGdWTEBO5MSQIpo(ChatActivity chatActivity, TL_messages_sendScheduledMessages tL_messages_sendScheduledMessages) {
        this.f$0 = chatActivity;
        this.f$1 = tL_messages_sendScheduledMessages;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$processSelectedOption$93$ChatActivity(this.f$1, tLObject, tL_error);
    }
}