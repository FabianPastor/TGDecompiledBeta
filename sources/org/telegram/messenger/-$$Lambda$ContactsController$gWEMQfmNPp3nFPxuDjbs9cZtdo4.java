package org.telegram.messenger;

import android.content.SharedPreferences.Editor;
import org.telegram.tgnet.TLObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$gWEMQfmNPp3nFPxuDjbs9cZtdo4 implements Runnable {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ Editor f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$ContactsController$gWEMQfmNPp3nFPxuDjbs9cZtdo4(ContactsController contactsController, Editor editor, TLObject tLObject) {
        this.f$0 = contactsController;
        this.f$1 = editor;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$54$ContactsController(this.f$1, this.f$2);
    }
}
