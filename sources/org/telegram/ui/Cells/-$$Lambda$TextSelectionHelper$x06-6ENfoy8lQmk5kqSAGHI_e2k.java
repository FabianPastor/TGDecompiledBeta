package org.telegram.ui.Cells;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TextSelectionHelper$x06-6ENfoy8lQmk5kqSAGHI_e2k implements OnTouchListener {
    private final /* synthetic */ TextSelectionHelper f$0;

    public /* synthetic */ -$$Lambda$TextSelectionHelper$x06-6ENfoy8lQmk5kqSAGHI_e2k(TextSelectionHelper textSelectionHelper) {
        this.f$0 = textSelectionHelper;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.lambda$showActions$1$TextSelectionHelper(view, motionEvent);
    }
}
