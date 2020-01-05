package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextBoldCursor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$1vEC6O3lueqPvsr0HLElXf1QyPI implements OnShowListener {
    private final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ -$$Lambda$ThemeActivity$1vEC6O3lueqPvsr0HLElXf1QyPI(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onShow(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeActivity$H6siyBX7X7ikAldHsSXtoPcYOwk(this.f$0));
    }
}
