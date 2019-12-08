package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$VWCUOR2j_GKIfMXwZfHRrJ8b5fU implements OnEditorActionListener {
    public static final /* synthetic */ -$$Lambda$ThemeActivity$VWCUOR2j_GKIfMXwZfHRrJ8b5fU INSTANCE = new -$$Lambda$ThemeActivity$VWCUOR2j_GKIfMXwZfHRrJ8b5fU();

    private /* synthetic */ -$$Lambda$ThemeActivity$VWCUOR2j_GKIfMXwZfHRrJ8b5fU() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
