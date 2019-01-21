package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Components.MediaActionDrawable;

final /* synthetic */ class TestActivity$$Lambda$1 implements OnClickListener {
    private final MediaActionDrawable arg$1;

    TestActivity$$Lambda$1(MediaActionDrawable mediaActionDrawable) {
        this.arg$1 = mediaActionDrawable;
    }

    public void onClick(View view) {
        TestActivity.lambda$createView$0$TestActivity(this.arg$1, view);
    }
}
