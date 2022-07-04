package org.telegram.ui.Components;

import android.view.View;
import android.widget.TextView;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda12 implements View.OnClickListener {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ int[] f$1;
    public final /* synthetic */ EditTextBoldCursor f$2;
    public final /* synthetic */ TextView f$3;
    public final /* synthetic */ TextView f$4;
    public final /* synthetic */ AlertDialog.Builder f$5;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda12(StickersAlert stickersAlert, int[] iArr, EditTextBoldCursor editTextBoldCursor, TextView textView, TextView textView2, AlertDialog.Builder builder) {
        this.f$0 = stickersAlert;
        this.f$1 = iArr;
        this.f$2 = editTextBoldCursor;
        this.f$3 = textView;
        this.f$4 = textView2;
        this.f$5 = builder;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showNameEnterAlert$29(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
    }
}
