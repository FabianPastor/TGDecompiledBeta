package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$UpZcB_L92bZrGJkH0CSwUF9nhJQ implements RequestDelegate {
    private final /* synthetic */ ContactsController f$0;

    public /* synthetic */ -$$Lambda$ContactsController$UpZcB_L92bZrGJkH0CSwUF9nhJQ(ContactsController contactsController) {
        this.f$0 = contactsController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadPrivacySettings$57$ContactsController(tLObject, tL_error);
    }
}
