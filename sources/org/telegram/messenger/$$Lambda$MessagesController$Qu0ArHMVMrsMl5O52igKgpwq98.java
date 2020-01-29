package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$Qu0A-rHMVMrsMl5O52igKgpwq98  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$Qu0ArHMVMrsMl5O52igKgpwq98 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$Qu0ArHMVMrsMl5O52igKgpwq98 INSTANCE = new $$Lambda$MessagesController$Qu0ArHMVMrsMl5O52igKgpwq98();

    private /* synthetic */ $$Lambda$MessagesController$Qu0ArHMVMrsMl5O52igKgpwq98() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$unregistedPush$201(tLObject, tL_error);
    }
}
