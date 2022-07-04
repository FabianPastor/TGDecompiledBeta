package org.telegram.ui.Components.voip;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.BetterRatingView;
import org.telegram.ui.Components.EditTextBoldCursor;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda12 implements View.OnClickListener {
    public final /* synthetic */ BetterRatingView f$0;
    public final /* synthetic */ int[] f$1;
    public final /* synthetic */ Context f$10;
    public final /* synthetic */ AlertDialog f$11;
    public final /* synthetic */ TextView f$12;
    public final /* synthetic */ CheckBoxCell f$13;
    public final /* synthetic */ TextView f$14;
    public final /* synthetic */ View f$15;
    public final /* synthetic */ LinearLayout f$2;
    public final /* synthetic */ EditTextBoldCursor f$3;
    public final /* synthetic */ boolean[] f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ File f$9;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda12(BetterRatingView betterRatingView, int[] iArr, LinearLayout linearLayout, EditTextBoldCursor editTextBoldCursor, boolean[] zArr, long j, long j2, boolean z, int i, File file, Context context, AlertDialog alertDialog, TextView textView, CheckBoxCell checkBoxCell, TextView textView2, View view) {
        this.f$0 = betterRatingView;
        this.f$1 = iArr;
        this.f$2 = linearLayout;
        this.f$3 = editTextBoldCursor;
        this.f$4 = zArr;
        this.f$5 = j;
        this.f$6 = j2;
        this.f$7 = z;
        this.f$8 = i;
        this.f$9 = file;
        this.f$10 = context;
        this.f$11 = alertDialog;
        this.f$12 = textView;
        this.f$13 = checkBoxCell;
        this.f$14 = textView2;
        this.f$15 = view;
    }

    public final void onClick(View view) {
        View view2 = view;
        BetterRatingView betterRatingView = this.f$0;
        BetterRatingView betterRatingView2 = betterRatingView;
        VoIPHelper.lambda$showRateAlert$16(betterRatingView2, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, view2);
    }
}
