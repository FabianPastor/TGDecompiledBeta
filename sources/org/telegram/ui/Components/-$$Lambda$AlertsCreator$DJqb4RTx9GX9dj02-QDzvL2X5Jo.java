package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$DJqb4RTx9GX9dj02-QDzvL2X5Jo implements OnEditorActionListener {
    public static final /* synthetic */ -$$Lambda$AlertsCreator$DJqb4RTx9GX9dj02-QDzvL2X5Jo INSTANCE = new -$$Lambda$AlertsCreator$DJqb4RTx9GX9dj02-QDzvL2X5Jo();

    private /* synthetic */ -$$Lambda$AlertsCreator$DJqb4RTx9GX9dj02-QDzvL2X5Jo() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
