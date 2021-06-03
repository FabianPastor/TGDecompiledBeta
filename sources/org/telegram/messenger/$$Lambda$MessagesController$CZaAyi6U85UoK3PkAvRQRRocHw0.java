package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$CZaAyi6U85UoK3PkAvRQRRocHw0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$CZaAyi6U85UoK3PkAvRQRRocHw0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$CZaAyi6U85UoK3PkAvRQRRocHw0 INSTANCE = new $$Lambda$MessagesController$CZaAyi6U85UoK3PkAvRQRRocHw0();

    private /* synthetic */ $$Lambda$MessagesController$CZaAyi6U85UoK3PkAvRQRRocHw0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePromoDialog$98(tLObject, tLRPC$TL_error);
    }
}
