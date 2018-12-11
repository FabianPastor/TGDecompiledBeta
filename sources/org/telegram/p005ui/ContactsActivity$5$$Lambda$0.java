package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ContactsActivity.CLASSNAME;

/* renamed from: org.telegram.ui.ContactsActivity$5$$Lambda$0 */
final /* synthetic */ class ContactsActivity$5$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final String arg$2;

    ContactsActivity$5$$Lambda$0(CLASSNAME CLASSNAME, String str) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$ContactsActivity$5(this.arg$2, dialogInterface, i);
    }
}
