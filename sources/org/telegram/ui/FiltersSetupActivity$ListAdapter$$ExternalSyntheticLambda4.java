package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import org.telegram.ui.FiltersSetupActivity;

public final /* synthetic */ class FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda4 implements View.OnTouchListener {
    public final /* synthetic */ FiltersSetupActivity.ListAdapter f$0;
    public final /* synthetic */ FiltersSetupActivity.FilterCell f$1;

    public /* synthetic */ FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda4(FiltersSetupActivity.ListAdapter listAdapter, FiltersSetupActivity.FilterCell filterCell) {
        this.f$0 = listAdapter;
        this.f$1 = filterCell;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.lambda$onCreateViewHolder$0(this.f$1, view, motionEvent);
    }
}
