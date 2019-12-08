package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$cgMrRs8JF5fB1lmaK4fOqKaLPqs implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$cgMrRs8JF5fB1lmaK4fOqKaLPqs(ChatActivity chatActivity, String str) {
        this.f$0 = chatActivity;
        this.f$1 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showOpenUrlAlert$97$ChatActivity(this.f$1, dialogInterface, i);
    }
}
