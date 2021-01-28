package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$K6uO3A_mqxkTuehWo8QdlnPeGZ4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$K6uO3A_mqxkTuehWo8QdlnPeGZ4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$K6uO3A_mqxkTuehWo8QdlnPeGZ4 INSTANCE = new $$Lambda$MessagesController$K6uO3A_mqxkTuehWo8QdlnPeGZ4();

    private /* synthetic */ $$Lambda$MessagesController$K6uO3A_mqxkTuehWo8QdlnPeGZ4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserFromChat$220(tLObject, tLRPC$TL_error);
    }
}
