package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;

final /* synthetic */ class ThemeActivity$$Lambda$6 implements OnEditorActionListener {
    static final OnEditorActionListener $instance = new ThemeActivity$$Lambda$6();

    private ThemeActivity$$Lambda$6() {
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
