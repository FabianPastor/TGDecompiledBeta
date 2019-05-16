package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.PhotoViewer.AnonymousClass7;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$7$It5atnO7wprrf4dhdkFyC0mozx4 implements OnClickListener {
    private final /* synthetic */ AnonymousClass7 f$0;
    private final /* synthetic */ boolean[] f$1;

    public /* synthetic */ -$$Lambda$PhotoViewer$7$It5atnO7wprrf4dhdkFyC0mozx4(AnonymousClass7 anonymousClass7, boolean[] zArr) {
        this.f$0 = anonymousClass7;
        this.f$1 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onItemClick$2$PhotoViewer$7(this.f$1, dialogInterface, i);
    }
}
