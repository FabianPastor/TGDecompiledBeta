package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda71 implements TextView.OnEditorActionListener {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda71 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda71();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda71() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
