package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.PhotoViewer.AnonymousClass7;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$7$MBP2lcO9C4pgMJ4ntN8Td94R7bI implements DialogsActivityDelegate {
    private final /* synthetic */ AnonymousClass7 f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$PhotoViewer$7$MBP2lcO9C4pgMJ4ntN8Td94R7bI(AnonymousClass7 anonymousClass7, ArrayList arrayList) {
        this.f$0 = anonymousClass7;
        this.f$1 = arrayList;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.lambda$onItemClick$0$PhotoViewer$7(this.f$1, dialogsActivity, arrayList, charSequence, z);
    }
}
