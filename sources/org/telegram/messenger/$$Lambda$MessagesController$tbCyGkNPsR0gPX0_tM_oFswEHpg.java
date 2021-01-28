package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$tbCyGkNPsR0gPX0_tM_oFswEHpg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$tbCyGkNPsR0gPX0_tM_oFswEHpg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$tbCyGkNPsR0gPX0_tM_oFswEHpg INSTANCE = new $$Lambda$MessagesController$tbCyGkNPsR0gPX0_tM_oFswEHpg();

    private /* synthetic */ $$Lambda$MessagesController$tbCyGkNPsR0gPX0_tM_oFswEHpg() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$removeSuggestion$16(tLObject, tLRPC$TL_error);
    }
}
