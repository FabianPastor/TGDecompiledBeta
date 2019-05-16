package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EditTextCaption$8tXURyNItaU0tMIyLqohmCvoG40 implements OnShowListener {
    private final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ -$$Lambda$EditTextCaption$8tXURyNItaU0tMIyLqohmCvoG40(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onShow(DialogInterface dialogInterface) {
        EditTextCaption.lambda$makeSelectedUrl$1(this.f$0, dialogInterface);
    }
}
