package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.p005ui.StickerPreviewViewer.CLASSNAME;

/* renamed from: org.telegram.ui.StickerPreviewViewer$1$$Lambda$0 */
final /* synthetic */ class StickerPreviewViewer$1$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;

    StickerPreviewViewer$1$$Lambda$0(CLASSNAME CLASSNAME, ArrayList arrayList, boolean z) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = arrayList;
        this.arg$3 = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$run$0$StickerPreviewViewer$1(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
