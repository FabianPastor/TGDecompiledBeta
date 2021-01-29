package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$ContactsController$21fDIh5PkZMGrZvoXXeEuxK5Cns  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ContactsController$21fDIh5PkZMGrZvoXXeEuxK5Cns implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$ContactsController$21fDIh5PkZMGrZvoXXeEuxK5Cns INSTANCE = new $$Lambda$ContactsController$21fDIh5PkZMGrZvoXXeEuxK5Cns();

    private /* synthetic */ $$Lambda$ContactsController$21fDIh5PkZMGrZvoXXeEuxK5Cns() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ContactsController.lambda$resetImportedContacts$9(tLObject, tLRPC$TL_error);
    }
}
