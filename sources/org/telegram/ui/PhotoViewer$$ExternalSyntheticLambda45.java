package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda45 implements View.OnClickListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ BottomSheet f$2;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda45(PhotoViewer photoViewer, NumberPicker numberPicker, BottomSheet bottomSheet) {
        this.f$0 = photoViewer;
        this.f$1 = numberPicker;
        this.f$2 = bottomSheet;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setParentActivity$27(this.f$1, this.f$2, view);
    }
}
