package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$gdAAgN1VQPZuBf_VhpZbRU8IgcA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$gdAAgN1VQPZuBf_VhpZbRU8IgcA implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$AlertsCreator$gdAAgN1VQPZuBf_VhpZbRU8IgcA INSTANCE = new $$Lambda$AlertsCreator$gdAAgN1VQPZuBf_VhpZbRU8IgcA();

    private /* synthetic */ $$Lambda$AlertsCreator$gdAAgN1VQPZuBf_VhpZbRU8IgcA() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
