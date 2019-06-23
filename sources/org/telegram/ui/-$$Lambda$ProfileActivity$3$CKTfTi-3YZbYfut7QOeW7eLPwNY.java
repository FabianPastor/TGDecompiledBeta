package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ProfileActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$3$CKTfTi-3YZbYfut7QOeW7eLPwNY implements OnClickListener {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ User f$1;

    public /* synthetic */ -$$Lambda$ProfileActivity$3$CKTfTi-3YZbYfut7QOeW7eLPwNY(AnonymousClass3 anonymousClass3, User user) {
        this.f$0 = anonymousClass3;
        this.f$1 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onItemClick$2$ProfileActivity$3(this.f$1, dialogInterface, i);
    }
}
