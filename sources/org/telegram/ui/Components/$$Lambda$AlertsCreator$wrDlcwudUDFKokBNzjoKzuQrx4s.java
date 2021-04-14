package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$wrDlcwudUDFKokBNzjoKzuQrx4s  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$wrDlcwudUDFKokBNzjoKzuQrx4s implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$wrDlcwudUDFKokBNzjoKzuQrx4s INSTANCE = new $$Lambda$AlertsCreator$wrDlcwudUDFKokBNzjoKzuQrx4s();

    private /* synthetic */ $$Lambda$AlertsCreator$wrDlcwudUDFKokBNzjoKzuQrx4s() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
