package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$dErdFDW74Y2Ys9Xcaqwa3GuT1J0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$dErdFDW74Y2Ys9Xcaqwa3GuT1J0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$dErdFDW74Y2Ys9Xcaqwa3GuT1J0 INSTANCE = new $$Lambda$MessagesController$dErdFDW74Y2Ys9Xcaqwa3GuT1J0();

    private /* synthetic */ $$Lambda$MessagesController$dErdFDW74Y2Ys9Xcaqwa3GuT1J0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$168(tLObject, tLRPC$TL_error);
    }
}