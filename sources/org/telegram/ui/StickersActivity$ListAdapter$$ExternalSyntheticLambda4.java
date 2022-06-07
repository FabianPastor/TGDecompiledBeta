package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.StickersActivity;

public final /* synthetic */ class StickersActivity$ListAdapter$$ExternalSyntheticLambda4 implements View.OnTouchListener {
    public final /* synthetic */ StickersActivity.ListAdapter f$0;
    public final /* synthetic */ StickerSetCell f$1;

    public /* synthetic */ StickersActivity$ListAdapter$$ExternalSyntheticLambda4(StickersActivity.ListAdapter listAdapter, StickerSetCell stickerSetCell) {
        this.f$0 = listAdapter;
        this.f$1 = stickerSetCell;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.lambda$onCreateViewHolder$2(this.f$1, view, motionEvent);
    }
}
