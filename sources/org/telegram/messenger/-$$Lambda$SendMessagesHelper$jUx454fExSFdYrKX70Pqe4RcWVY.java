package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.ui.ChatActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$jUx454fExSFdYrKX70Pqe4RcWVY implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ MessageObject f$4;
    private final /* synthetic */ KeyboardButton f$5;
    private final /* synthetic */ ChatActivity f$6;
    private final /* synthetic */ TLObject[] f$7;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$jUx454fExSFdYrKX70Pqe4RcWVY(SendMessagesHelper sendMessagesHelper, String str, boolean z, TLObject tLObject, MessageObject messageObject, KeyboardButton keyboardButton, ChatActivity chatActivity, TLObject[] tLObjectArr) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = str;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = messageObject;
        this.f$5 = keyboardButton;
        this.f$6 = chatActivity;
        this.f$7 = tLObjectArr;
    }

    public final void run() {
        this.f$0.lambda$null$19$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
