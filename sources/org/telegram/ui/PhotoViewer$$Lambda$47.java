package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.NumberPicker;

final /* synthetic */ class PhotoViewer$$Lambda$47 implements OnClickListener {
    private final PhotoViewer arg$1;
    private final NumberPicker arg$2;
    private final BottomSheet arg$3;

    PhotoViewer$$Lambda$47(PhotoViewer photoViewer, NumberPicker numberPicker, BottomSheet bottomSheet) {
        this.arg$1 = photoViewer;
        this.arg$2 = numberPicker;
        this.arg$3 = bottomSheet;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$16$PhotoViewer(this.arg$2, this.arg$3, view);
    }
}