package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda35 implements View.OnClickListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ BottomSheet f$2;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda35(PhotoViewer photoViewer, NumberPicker numberPicker, BottomSheet bottomSheet) {
        this.f$0 = photoViewer;
        this.f$1 = numberPicker;
        this.f$2 = bottomSheet;
    }

    public final void onClick(View view) {
        this.f$0.m4271lambda$setParentActivity$27$orgtelegramuiPhotoViewer(this.f$1, this.f$2, view);
    }
}
