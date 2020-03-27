package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$1hIcMK4VCnPdE4xO6eqA_7-C_Ac  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$1hIcMK4VCnPdE4xO6eqA_7C_Ac implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$1hIcMK4VCnPdE4xO6eqA_7C_Ac INSTANCE = new $$Lambda$MediaDataController$1hIcMK4VCnPdE4xO6eqA_7C_Ac();

    private /* synthetic */ $$Lambda$MediaDataController$1hIcMK4VCnPdE4xO6eqA_7C_Ac() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$86(tLObject, tLRPC$TL_error);
    }
}
