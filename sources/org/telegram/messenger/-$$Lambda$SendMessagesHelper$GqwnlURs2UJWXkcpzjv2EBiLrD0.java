package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$GqwnlURs2UJWXkcpzjv2EBiLrD0 implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ TL_messages_sendMultiMedia f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ ArrayList f$5;
    private final /* synthetic */ DelayedMessage f$6;
    private final /* synthetic */ TLObject f$7;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$GqwnlURs2UJWXkcpzjv2EBiLrD0(SendMessagesHelper sendMessagesHelper, TL_error tL_error, ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, TLObject tLObject) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_error;
        this.f$2 = arrayList;
        this.f$3 = tL_messages_sendMultiMedia;
        this.f$4 = arrayList2;
        this.f$5 = arrayList3;
        this.f$6 = delayedMessage;
        this.f$7 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$28$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
