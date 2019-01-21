package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class ThemeEditorView$EditorAlert$ColorPicker$$Lambda$0 implements OnEditorActionListener {
    static final OnEditorActionListener $instance = new ThemeEditorView$EditorAlert$ColorPicker$$Lambda$0();

    private ThemeEditorView$EditorAlert$ColorPicker$$Lambda$0() {
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return ColorPicker.lambda$new$0$ThemeEditorView$EditorAlert$ColorPicker(textView, i, keyEvent);
    }
}
