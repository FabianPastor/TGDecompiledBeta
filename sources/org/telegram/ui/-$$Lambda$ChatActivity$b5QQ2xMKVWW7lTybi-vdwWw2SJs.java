package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messages_sendScheduledMessages;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$b5QQ2xMKVWW7lTybi-vdwWw2SJs implements Runnable {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ TL_messages_sendScheduledMessages f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$b5QQ2xMKVWW7lTybi-vdwWw2SJs(ChatActivity chatActivity, TL_messages_sendScheduledMessages tL_messages_sendScheduledMessages) {
        this.f$0 = chatActivity;
        this.f$1 = tL_messages_sendScheduledMessages;
    }

    public final void run() {
        this.f$0.lambda$null$96$ChatActivity(this.f$1);
    }
}
