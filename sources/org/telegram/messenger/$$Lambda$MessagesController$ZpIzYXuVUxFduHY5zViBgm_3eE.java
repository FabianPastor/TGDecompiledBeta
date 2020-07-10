package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$ZpIzYXu-VUxFduHY5zViBgm_3eE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$ZpIzYXuVUxFduHY5zViBgm_3eE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$ZpIzYXuVUxFduHY5zViBgm_3eE INSTANCE = new $$Lambda$MessagesController$ZpIzYXuVUxFduHY5zViBgm_3eE();

    private /* synthetic */ $$Lambda$MessagesController$ZpIzYXuVUxFduHY5zViBgm_3eE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$268(tLObject, tLRPC$TL_error);
    }
}
