package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ContactsActivity.C13095;

final /* synthetic */ class ContactsActivity$5$$Lambda$0 implements OnClickListener {
    private final C13095 arg$1;
    private final String arg$2;

    ContactsActivity$5$$Lambda$0(C13095 c13095, String str) {
        this.arg$1 = c13095;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$ContactsActivity$5(this.arg$2, dialogInterface, i);
    }
}
