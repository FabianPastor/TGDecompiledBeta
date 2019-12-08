package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsActivity$_d9wRXWZ087epm3Z44uYz0Fl9RA implements OnClickListener {
    private final /* synthetic */ DialogsActivity f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$DialogsActivity$_d9wRXWZ087epm3Z44uYz0Fl9RA(DialogsActivity dialogsActivity, long j) {
        this.f$0 = dialogsActivity;
        this.f$1 = j;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didSelectResult$21$DialogsActivity(this.f$1, dialogInterface, i);
    }
}
