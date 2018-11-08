package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ContactsActivity.C16525;

/* renamed from: org.telegram.ui.ContactsActivity$5$$Lambda$0 */
final /* synthetic */ class ContactsActivity$5$$Lambda$0 implements OnClickListener {
    private final C16525 arg$1;
    private final String arg$2;

    ContactsActivity$5$$Lambda$0(C16525 c16525, String str) {
        this.arg$1 = c16525;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$ContactsActivity$5(this.arg$2, dialogInterface, i);
    }
}
