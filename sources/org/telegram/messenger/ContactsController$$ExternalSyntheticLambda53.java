package org.telegram.messenger;

import android.content.SharedPreferences;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda53 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ SharedPreferences.Editor f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda53(ContactsController contactsController, SharedPreferences.Editor editor) {
        this.f$0 = contactsController;
        this.f$1 = editor;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m54xfCLASSNAMEe105(this.f$1, tLObject, tL_error);
    }
}
