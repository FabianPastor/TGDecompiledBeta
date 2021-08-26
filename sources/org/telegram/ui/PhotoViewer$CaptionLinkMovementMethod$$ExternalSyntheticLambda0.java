package org.telegram.ui;

import android.content.DialogInterface;
import android.text.style.URLSpan;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$CaptionLinkMovementMethod$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PhotoViewer.CaptionLinkMovementMethod f$0;
    public final /* synthetic */ URLSpan f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ PhotoViewer$CaptionLinkMovementMethod$$ExternalSyntheticLambda0(PhotoViewer.CaptionLinkMovementMethod captionLinkMovementMethod, URLSpan uRLSpan, int i) {
        this.f$0 = captionLinkMovementMethod;
        this.f$1 = uRLSpan;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onLongClick$0(this.f$1, this.f$2, dialogInterface, i);
    }
}
