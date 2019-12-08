package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$YTGj6OlsiQkZNyaL_Nt7kmB10Bo implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ TL_messages_sendMultiMedia f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ DelayedMessage f$5;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$YTGj6OlsiQkZNyaL_Nt7kmB10Bo(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = arrayList;
        this.f$2 = tL_messages_sendMultiMedia;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
        this.f$5 = delayedMessage;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$performSendMessageRequestMulti$30$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
