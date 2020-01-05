package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$1UlSeYagrlax_3OOs5dqbDoqf8E implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ TL_messages_sendMultiMedia f$6;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$1UlSeYagrlax_3OOs5dqbDoqf8E(SendMessagesHelper sendMessagesHelper, TL_error tL_error, TLObject tLObject, ArrayList arrayList, ArrayList arrayList2, boolean z, TL_messages_sendMultiMedia tL_messages_sendMultiMedia) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = arrayList;
        this.f$4 = arrayList2;
        this.f$5 = z;
        this.f$6 = tL_messages_sendMultiMedia;
    }

    public final void run() {
        this.f$0.lambda$null$37$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
