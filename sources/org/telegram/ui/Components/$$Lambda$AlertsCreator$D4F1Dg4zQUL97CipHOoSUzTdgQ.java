package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$D4F1Dg4zQUL-97CipHOoSUzTdgQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$D4F1Dg4zQUL97CipHOoSUzTdgQ implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$D4F1Dg4zQUL97CipHOoSUzTdgQ INSTANCE = new $$Lambda$AlertsCreator$D4F1Dg4zQUL97CipHOoSUzTdgQ();

    private /* synthetic */ $$Lambda$AlertsCreator$D4F1Dg4zQUL97CipHOoSUzTdgQ() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
