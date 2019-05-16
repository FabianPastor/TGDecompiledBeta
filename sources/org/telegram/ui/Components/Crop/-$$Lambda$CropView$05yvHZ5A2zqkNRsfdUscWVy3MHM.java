package org.telegram.ui.Components.Crop;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CropView$05yvHZ5A2zqkNRsfdUscWVy3MHM implements OnClickListener {
    private final /* synthetic */ CropView f$0;
    private final /* synthetic */ Integer[][] f$1;

    public /* synthetic */ -$$Lambda$CropView$05yvHZ5A2zqkNRsfdUscWVy3MHM(CropView cropView, Integer[][] numArr) {
        this.f$0 = cropView;
        this.f$1 = numArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showAspectRatioDialog$2$CropView(this.f$1, dialogInterface, i);
    }
}
