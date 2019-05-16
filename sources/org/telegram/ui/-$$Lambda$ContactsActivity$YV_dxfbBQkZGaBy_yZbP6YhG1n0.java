package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsActivity$YV_dxfbBQkZGaBy_yZbP6YhG1n0 implements OnClickListener {
    private final /* synthetic */ ContactsActivity f$0;
    private final /* synthetic */ User f$1;
    private final /* synthetic */ EditText f$2;

    public /* synthetic */ -$$Lambda$ContactsActivity$YV_dxfbBQkZGaBy_yZbP6YhG1n0(ContactsActivity contactsActivity, User user, EditText editText) {
        this.f$0 = contactsActivity;
        this.f$1 = user;
        this.f$2 = editText;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didSelectResult$4$ContactsActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
