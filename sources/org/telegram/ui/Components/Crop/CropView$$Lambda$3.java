package org.telegram.ui.Components.Crop;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

final /* synthetic */ class CropView$$Lambda$3 implements OnCancelListener {
    private final CropView arg$1;

    CropView$$Lambda$3(CropView cropView) {
        this.arg$1 = cropView;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$showAspectRatioDialog$3$CropView(dialogInterface);
    }
}