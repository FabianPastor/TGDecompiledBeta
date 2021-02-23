package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$mdlTnpigjpqHY-EeoRvtUeHSW3Y  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$mdlTnpigjpqHYEeoRvtUeHSW3Y implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$mdlTnpigjpqHYEeoRvtUeHSW3Y INSTANCE = new $$Lambda$MessagesController$mdlTnpigjpqHYEeoRvtUeHSW3Y();

    private /* synthetic */ $$Lambda$MessagesController$mdlTnpigjpqHYEeoRvtUeHSW3Y() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserFromChat$226(tLObject, tLRPC$TL_error);
    }
}
