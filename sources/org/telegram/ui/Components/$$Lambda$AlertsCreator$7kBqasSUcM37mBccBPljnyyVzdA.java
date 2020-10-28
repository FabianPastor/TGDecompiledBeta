package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$7kBqasSUcM37mBccBPljnyyVzdA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$7kBqasSUcM37mBccBPljnyyVzdA implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$7kBqasSUcM37mBccBPljnyyVzdA INSTANCE = new $$Lambda$AlertsCreator$7kBqasSUcM37mBccBPljnyyVzdA();

    private /* synthetic */ $$Lambda$AlertsCreator$7kBqasSUcM37mBccBPljnyyVzdA() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
