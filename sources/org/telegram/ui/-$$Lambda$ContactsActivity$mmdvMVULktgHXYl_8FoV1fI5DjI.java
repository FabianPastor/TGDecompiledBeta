package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsActivity$mmdvMVULktgHXYl_8FoV1fI5DjI implements OnClickListener {
    private final /* synthetic */ ContactsActivity f$0;
    private final /* synthetic */ User f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$ContactsActivity$mmdvMVULktgHXYl_8FoV1fI5DjI(ContactsActivity contactsActivity, User user, String str) {
        this.f$0 = contactsActivity;
        this.f$1 = user;
        this.f$2 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didSelectResult$3$ContactsActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
