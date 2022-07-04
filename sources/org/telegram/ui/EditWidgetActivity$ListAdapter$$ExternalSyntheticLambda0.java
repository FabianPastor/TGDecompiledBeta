package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.EditWidgetActivity;

public final /* synthetic */ class EditWidgetActivity$ListAdapter$$ExternalSyntheticLambda0 implements View.OnTouchListener {
    public final /* synthetic */ EditWidgetActivity.ListAdapter f$0;
    public final /* synthetic */ GroupCreateUserCell f$1;

    public /* synthetic */ EditWidgetActivity$ListAdapter$$ExternalSyntheticLambda0(EditWidgetActivity.ListAdapter listAdapter, GroupCreateUserCell groupCreateUserCell) {
        this.f$0 = listAdapter;
        this.f$1 = groupCreateUserCell;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.m3441x37efab4f(this.f$1, view, motionEvent);
    }
}
