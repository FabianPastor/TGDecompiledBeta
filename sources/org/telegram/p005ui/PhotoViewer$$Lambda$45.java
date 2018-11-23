package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.Components.NumberPicker;

/* renamed from: org.telegram.ui.PhotoViewer$$Lambda$45 */
final /* synthetic */ class PhotoViewer$$Lambda$45 implements OnClickListener {
    private final PhotoViewer arg$1;
    private final NumberPicker arg$2;
    private final BottomSheet arg$3;

    PhotoViewer$$Lambda$45(PhotoViewer photoViewer, NumberPicker numberPicker, BottomSheet bottomSheet) {
        this.arg$1 = photoViewer;
        this.arg$2 = numberPicker;
        this.arg$3 = bottomSheet;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$15$PhotoViewer(this.arg$2, this.arg$3, view);
    }
}
