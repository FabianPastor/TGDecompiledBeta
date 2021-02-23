package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$LKEzX_OxhMF5gNNJ0Z4zvDRqF9c  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$LKEzX_OxhMF5gNNJ0Z4zvDRqF9c implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$LKEzX_OxhMF5gNNJ0Z4zvDRqF9c INSTANCE = new $$Lambda$AlertsCreator$LKEzX_OxhMF5gNNJ0Z4zvDRqF9c();

    private /* synthetic */ $$Lambda$AlertsCreator$LKEzX_OxhMF5gNNJ0Z4zvDRqF9c() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
