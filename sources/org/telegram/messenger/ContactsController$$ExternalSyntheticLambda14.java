package org.telegram.messenger;

import android.content.SharedPreferences;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ SharedPreferences.Editor f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda14(ContactsController contactsController, SharedPreferences.Editor editor, TLObject tLObject) {
        this.f$0 = contactsController;
        this.f$1 = editor;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$reloadContactsStatuses$57(this.f$1, this.f$2);
    }
}
