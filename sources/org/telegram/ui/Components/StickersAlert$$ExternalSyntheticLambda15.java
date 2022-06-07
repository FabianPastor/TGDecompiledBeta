package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda15 implements TextView.OnEditorActionListener {
    public final /* synthetic */ AlertDialog.Builder f$0;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda15(AlertDialog.Builder builder) {
        this.f$0 = builder;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return StickersAlert.lambda$showNameEnterAlert$22(this.f$0, textView, i, keyEvent);
    }
}
