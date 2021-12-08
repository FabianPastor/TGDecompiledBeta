package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda69 implements TextView.OnEditorActionListener {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda69 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda69();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda69() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
