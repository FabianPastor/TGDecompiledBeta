package org.telegram.ui.Components.Crop;

import android.content.DialogInterface;

public final /* synthetic */ class CropView$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ CropView f$0;
    public final /* synthetic */ Integer[][] f$1;

    public /* synthetic */ CropView$$ExternalSyntheticLambda4(CropView cropView, Integer[][] numArr) {
        this.f$0 = cropView;
        this.f$1 = numArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showAspectRatioDialog$3(this.f$1, dialogInterface, i);
    }
}
