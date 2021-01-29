package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$MDmCQPpO0aO01IpqquLwzp0_Q8Y  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$MDmCQPpO0aO01IpqquLwzp0_Q8Y implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$MDmCQPpO0aO01IpqquLwzp0_Q8Y INSTANCE = new $$Lambda$AlertsCreator$MDmCQPpO0aO01IpqquLwzp0_Q8Y();

    private /* synthetic */ $$Lambda$AlertsCreator$MDmCQPpO0aO01IpqquLwzp0_Q8Y() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
