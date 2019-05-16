package org.telegram.messenger;

import android.content.SharedPreferences.Editor;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$h5vCC-HpjKgEFkNX9o79KFdTfVk implements RequestDelegate {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ Editor f$1;

    public /* synthetic */ -$$Lambda$ContactsController$h5vCC-HpjKgEFkNX9o79KFdTfVk(ContactsController contactsController, Editor editor) {
        this.f$0 = contactsController;
        this.f$1 = editor;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$reloadContactsStatuses$55$ContactsController(this.f$1, tLObject, tL_error);
    }
}
