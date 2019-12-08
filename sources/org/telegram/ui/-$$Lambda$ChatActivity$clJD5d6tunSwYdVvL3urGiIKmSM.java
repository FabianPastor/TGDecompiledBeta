package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$clJD5d6tunSwYdVvL3urGiIKmSM implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean[] f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$clJD5d6tunSwYdVvL3urGiIKmSM(ChatActivity chatActivity, int i, boolean[] zArr) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$processSelectedOption$78$ChatActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
