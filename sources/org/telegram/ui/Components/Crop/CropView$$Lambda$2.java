package org.telegram.ui.Components.Crop;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class CropView$$Lambda$2 implements OnClickListener {
    private final CropView arg$1;
    private final Integer[][] arg$2;

    CropView$$Lambda$2(CropView cropView, Integer[][] numArr) {
        this.arg$1 = cropView;
        this.arg$2 = numArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showAspectRatioDialog$2$CropView(this.arg$2, dialogInterface, i);
    }
}
