package org.telegram.ui.Components.Crop;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CropView$MBImByPOxBcWFdcV4k41dkfucIY implements OnCancelListener {
    private final /* synthetic */ CropView f$0;

    public /* synthetic */ -$$Lambda$CropView$MBImByPOxBcWFdcV4k41dkfucIY(CropView cropView) {
        this.f$0 = cropView;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$showAspectRatioDialog$3$CropView(dialogInterface);
    }
}
