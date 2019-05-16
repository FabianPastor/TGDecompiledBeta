package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$mkj69nAVhIC1gtqhKztiCLASSNAMEPgeU implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$mkj69nAVhIC1gtqhKztiCLASSNAMEPgeU INSTANCE = new -$$Lambda$MessagesController$mkj69nAVhIC1gtqhKztiCLASSNAMEPgeU();

    private /* synthetic */ -$$Lambda$MessagesController$mkj69nAVhIC1gtqhKztiCLASSNAMEPgeU() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$completeReadTask$147(tLObject, tL_error);
    }
}
