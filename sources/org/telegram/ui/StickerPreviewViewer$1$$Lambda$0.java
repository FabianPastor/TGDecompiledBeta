package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.ui.StickerPreviewViewer.AnonymousClass1;

final /* synthetic */ class StickerPreviewViewer$1$$Lambda$0 implements OnClickListener {
    private final AnonymousClass1 arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;

    StickerPreviewViewer$1$$Lambda$0(AnonymousClass1 anonymousClass1, ArrayList arrayList, boolean z) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = arrayList;
        this.arg$3 = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$run$0$StickerPreviewViewer$1(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
