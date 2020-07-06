package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$1SfHimLyH_um-5poc8N_OJfsXyg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$1SfHimLyH_um5poc8N_OJfsXyg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$1SfHimLyH_um5poc8N_OJfsXyg INSTANCE = new $$Lambda$MessagesController$1SfHimLyH_um5poc8N_OJfsXyg();

    private /* synthetic */ $$Lambda$MessagesController$1SfHimLyH_um5poc8N_OJfsXyg() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$169(tLObject, tLRPC$TL_error);
    }
}
