package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ContactsActivity.C15645;

final /* synthetic */ class ContactsActivity$5$$Lambda$0 implements OnClickListener {
    private final C15645 arg$1;
    private final String arg$2;

    ContactsActivity$5$$Lambda$0(C15645 c15645, String str) {
        this.arg$1 = c15645;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$ContactsActivity$5(this.arg$2, dialogInterface, i);
    }
}
