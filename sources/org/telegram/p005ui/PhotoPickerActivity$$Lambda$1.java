package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.PhotoPickerActivity$$Lambda$1 */
final /* synthetic */ class PhotoPickerActivity$$Lambda$1 implements OnItemLongClickListener {
    private final PhotoPickerActivity arg$1;

    PhotoPickerActivity$$Lambda$1(PhotoPickerActivity photoPickerActivity) {
        this.arg$1 = photoPickerActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$PhotoPickerActivity(view, i);
    }
}
