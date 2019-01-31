package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class WallpaperActivity$ColorPicker$$Lambda$0 implements OnEditorActionListener {
    static final OnEditorActionListener $instance = new WallpaperActivity$ColorPicker$$Lambda$0();

    private WallpaperActivity$ColorPicker$$Lambda$0() {
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return ColorPicker.lambda$new$0$WallpaperActivity$ColorPicker(textView, i, keyEvent);
    }
}
