package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.ui.-$$Lambda$ChangeNameActivity$vtAdKEThAI1k81afp0SrP9xGvq8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChangeNameActivity$vtAdKEThAI1k81afp0SrP9xGvq8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$ChangeNameActivity$vtAdKEThAI1k81afp0SrP9xGvq8 INSTANCE = new $$Lambda$ChangeNameActivity$vtAdKEThAI1k81afp0SrP9xGvq8();

    private /* synthetic */ $$Lambda$ChangeNameActivity$vtAdKEThAI1k81afp0SrP9xGvq8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChangeNameActivity.lambda$saveName$3(tLObject, tLRPC$TL_error);
    }
}
