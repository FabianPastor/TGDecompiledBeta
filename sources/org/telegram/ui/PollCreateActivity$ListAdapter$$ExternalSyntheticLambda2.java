package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.PollCreateActivity;

public final /* synthetic */ class PollCreateActivity$ListAdapter$$ExternalSyntheticLambda2 implements TextView.OnEditorActionListener {
    public final /* synthetic */ PollCreateActivity.ListAdapter f$0;
    public final /* synthetic */ PollEditTextCell f$1;

    public /* synthetic */ PollCreateActivity$ListAdapter$$ExternalSyntheticLambda2(PollCreateActivity.ListAdapter listAdapter, PollEditTextCell pollEditTextCell) {
        this.f$0 = listAdapter;
        this.f$1 = pollEditTextCell;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.m3646x2beeCLASSNAMEb(this.f$1, textView, i, keyEvent);
    }
}
