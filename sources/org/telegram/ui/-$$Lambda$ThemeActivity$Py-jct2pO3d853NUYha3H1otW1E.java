package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextBoldCursor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$Py-jct2pO3d853NUYha3H1otW1E implements OnShowListener {
    private final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ -$$Lambda$ThemeActivity$Py-jct2pO3d853NUYha3H1otW1E(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onShow(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeActivity$YCA7UJXYZMi_vXUg604HM_r3wUY(this.f$0));
    }
}
