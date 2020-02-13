package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$CMQfj4D7MzL-evGjQh5r5_Wbhj8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$CMQfj4D7MzLevGjQh5r5_Wbhj8 implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$CMQfj4D7MzLevGjQh5r5_Wbhj8 INSTANCE = new $$Lambda$AlertsCreator$CMQfj4D7MzLevGjQh5r5_Wbhj8();

    private /* synthetic */ $$Lambda$AlertsCreator$CMQfj4D7MzLevGjQh5r5_Wbhj8() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
