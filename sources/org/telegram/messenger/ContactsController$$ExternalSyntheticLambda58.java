package org.telegram.messenger;

import android.content.SharedPreferences;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda58 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ SharedPreferences.Editor f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda58(ContactsController contactsController, SharedPreferences.Editor editor) {
        this.f$0 = contactsController;
        this.f$1 = editor;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$reloadContactsStatuses$58(this.f$1, tLObject, tLRPC$TL_error);
    }
}
