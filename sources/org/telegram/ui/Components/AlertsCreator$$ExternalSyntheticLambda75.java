package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda75 implements TextView.OnEditorActionListener {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda75 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda75();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda75() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
