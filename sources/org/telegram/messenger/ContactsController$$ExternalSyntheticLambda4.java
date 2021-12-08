package org.telegram.messenger;

import android.content.SharedPreferences;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ SharedPreferences.Editor f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda4(ContactsController contactsController, SharedPreferences.Editor editor, TLObject tLObject) {
        this.f$0 = contactsController;
        this.f$1 = editor;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m62x16d87244(this.f$1, this.f$2);
    }
}
