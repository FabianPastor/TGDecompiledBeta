package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$AbTZFLT5AeZ8Xev0g7z_25zx9YM implements RequestDelegate {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ContactsController$AbTZFLT5AeZ8Xev0g7z_25zx9YM(ContactsController contactsController, int i) {
        this.f$0 = contactsController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadPrivacySettings$59$ContactsController(this.f$1, tLObject, tL_error);
    }
}
