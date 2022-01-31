package org.telegram.ui.Cells;

import android.content.DialogInterface;
import android.text.style.ClickableSpan;
import org.telegram.ui.Cells.AboutLinkCell;

public final /* synthetic */ class AboutLinkCell$3$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ AboutLinkCell.AnonymousClass3 f$0;
    public final /* synthetic */ ClickableSpan f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ AboutLinkCell$3$$ExternalSyntheticLambda0(AboutLinkCell.AnonymousClass3 r1, ClickableSpan clickableSpan, String str) {
        this.f$0 = r1;
        this.f$1 = clickableSpan;
        this.f$2 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$run$0(this.f$1, this.f$2, dialogInterface, i);
    }
}
