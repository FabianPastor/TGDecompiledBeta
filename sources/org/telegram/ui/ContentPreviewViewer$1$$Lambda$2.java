package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.ui.ContentPreviewViewer.AnonymousClass1;

final /* synthetic */ class ContentPreviewViewer$1$$Lambda$2 implements OnClickListener {
    private final AnonymousClass1 arg$1;
    private final ArrayList arg$2;

    ContentPreviewViewer$1$$Lambda$2(AnonymousClass1 anonymousClass1, ArrayList arrayList) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = arrayList;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$run$2$ContentPreviewViewer$1(this.arg$2, dialogInterface, i);
    }
}
