package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import org.telegram.ui.Components.ThemeEditorView.AnonymousClass1;

final /* synthetic */ class ThemeEditorView$1$$Lambda$1 implements OnDismissListener {
    private final AnonymousClass1 arg$1;

    ThemeEditorView$1$$Lambda$1(AnonymousClass1 anonymousClass1) {
        this.arg$1 = anonymousClass1;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$onTouchEvent$1$ThemeEditorView$1(dialogInterface);
    }
}
