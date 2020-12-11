package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$tPB-REl-SbdnQjMg5iILigkPbJ0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$tPBRElSbdnQjMg5iILigkPbJ0 implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$tPBRElSbdnQjMg5iILigkPbJ0 INSTANCE = new $$Lambda$AlertsCreator$tPBRElSbdnQjMg5iILigkPbJ0();

    private /* synthetic */ $$Lambda$AlertsCreator$tPBRElSbdnQjMg5iILigkPbJ0() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
