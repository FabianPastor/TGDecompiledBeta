package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$8f9JaQIiJ9OIWz2EJrP24HCLASSNAMEHw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$8f9JaQIiJ9OIWz2EJrP24HCLASSNAMEHw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$8f9JaQIiJ9OIWz2EJrP24HCLASSNAMEHw INSTANCE = new $$Lambda$MediaDataController$8f9JaQIiJ9OIWz2EJrP24HCLASSNAMEHw();

    private /* synthetic */ $$Lambda$MediaDataController$8f9JaQIiJ9OIWz2EJrP24HCLASSNAMEHw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$31(tLObject, tLRPC$TL_error);
    }
}
