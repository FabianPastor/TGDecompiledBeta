package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$dmu_eIUtJ8qrXK2aY-B-h75TVSc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$dmu_eIUtJ8qrXK2aYBh75TVSc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$dmu_eIUtJ8qrXK2aYBh75TVSc INSTANCE = new $$Lambda$MessagesController$dmu_eIUtJ8qrXK2aYBh75TVSc();

    private /* synthetic */ $$Lambda$MessagesController$dmu_eIUtJ8qrXK2aYBh75TVSc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$187(tLObject, tLRPC$TL_error);
    }
}
