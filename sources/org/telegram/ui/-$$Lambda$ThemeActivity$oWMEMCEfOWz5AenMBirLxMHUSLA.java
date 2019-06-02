package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$oWMEMCEfOWz5AenMBirLxMHUSLA implements OnEditorActionListener {
    public static final /* synthetic */ -$$Lambda$ThemeActivity$oWMEMCEfOWz5AenMBirLxMHUSLA INSTANCE = new -$$Lambda$ThemeActivity$oWMEMCEfOWz5AenMBirLxMHUSLA();

    private /* synthetic */ -$$Lambda$ThemeActivity$oWMEMCEfOWz5AenMBirLxMHUSLA() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
