package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.FilterCreateActivity;

public final /* synthetic */ class FilterCreateActivity$ListAdapter$$ExternalSyntheticLambda0 implements View.OnFocusChangeListener {
    public final /* synthetic */ FilterCreateActivity.ListAdapter f$0;
    public final /* synthetic */ PollEditTextCell f$1;

    public /* synthetic */ FilterCreateActivity$ListAdapter$$ExternalSyntheticLambda0(FilterCreateActivity.ListAdapter listAdapter, PollEditTextCell pollEditTextCell) {
        this.f$0 = listAdapter;
        this.f$1 = pollEditTextCell;
    }

    public final void onFocusChange(View view, boolean z) {
        this.f$0.m2159x999fa0b5(this.f$1, view, z);
    }
}
