package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;

final /* synthetic */ class ThemeActivity$$Lambda$2 implements OnEditorActionListener {
    static final OnEditorActionListener $instance = new ThemeActivity$$Lambda$2();

    private ThemeActivity$$Lambda$2() {
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
