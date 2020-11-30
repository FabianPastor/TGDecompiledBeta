package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$rIvS82C4RGl9-mySFbW2kP0HYUA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$rIvS82C4RGl9mySFbW2kP0HYUA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$rIvS82C4RGl9mySFbW2kP0HYUA INSTANCE = new $$Lambda$MessagesController$rIvS82C4RGl9mySFbW2kP0HYUA();

    private /* synthetic */ $$Lambda$MessagesController$rIvS82C4RGl9mySFbW2kP0HYUA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePeerSettingsBar$46(tLObject, tLRPC$TL_error);
    }
}
