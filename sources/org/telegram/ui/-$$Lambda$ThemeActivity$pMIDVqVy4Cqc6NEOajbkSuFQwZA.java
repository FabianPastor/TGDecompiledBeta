package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextBoldCursor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$pMIDVqVy4Cqc6NEOajbkSuFQwZA implements OnShowListener {
    private final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ -$$Lambda$ThemeActivity$pMIDVqVy4Cqc6NEOajbkSuFQwZA(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onShow(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeActivity$m4EmqHR619o6faN5Q_KiPQtpKTk(this.f$0));
    }
}
