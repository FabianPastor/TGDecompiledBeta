package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$ODz_jV3aD_XGfZz-xQsH4uTinsI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$ODz_jV3aD_XGfZzxQsH4uTinsI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$ODz_jV3aD_XGfZzxQsH4uTinsI INSTANCE = new $$Lambda$MessagesController$ODz_jV3aD_XGfZzxQsH4uTinsI();

    private /* synthetic */ $$Lambda$MessagesController$ODz_jV3aD_XGfZzxQsH4uTinsI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$225(tLObject, tLRPC$TL_error);
    }
}
