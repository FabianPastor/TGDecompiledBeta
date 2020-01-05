package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import org.telegram.messenger.AndroidUtilities;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$dywCIYGCwAk2dvnBomvTJlKTZmI implements OnShowListener {
    private final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$dywCIYGCwAk2dvnBomvTJlKTZmI(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onShow(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$AlertsCreator$03EDLJ-rQ6BiGiRZlp5CqvnXg0g(this.f$0));
    }
}
