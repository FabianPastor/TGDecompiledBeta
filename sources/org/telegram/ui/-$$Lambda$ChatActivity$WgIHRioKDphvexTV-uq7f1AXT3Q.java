package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$WgIHRioKDphvexTV-uq7f1AXT3Q implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$WgIHRioKDphvexTV-uq7f1AXT3Q(ChatActivity chatActivity, String str) {
        this.f$0 = chatActivity;
        this.f$1 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showOpenUrlAlert$102$ChatActivity(this.f$1, dialogInterface, i);
    }
}
