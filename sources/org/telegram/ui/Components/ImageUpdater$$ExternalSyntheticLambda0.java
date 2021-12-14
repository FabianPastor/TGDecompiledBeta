package org.telegram.ui.Components;

import android.content.DialogInterface;
import java.util.ArrayList;

public final /* synthetic */ class ImageUpdater$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ImageUpdater f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ ImageUpdater$$ExternalSyntheticLambda0(ImageUpdater imageUpdater, ArrayList arrayList, Runnable runnable) {
        this.f$0 = imageUpdater;
        this.f$1 = arrayList;
        this.f$2 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$openMenu$0(this.f$1, this.f$2, dialogInterface, i);
    }
}
