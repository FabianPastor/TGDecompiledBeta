package org.telegram.ui.Components;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.messenger.MediaController.AlbumEntry;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatAttachAlert$3QtRWTxQxMgu3VLPPr1QTRHO5_M implements Comparator {
    private final /* synthetic */ ArrayList f$0;

    public /* synthetic */ -$$Lambda$ChatAttachAlert$3QtRWTxQxMgu3VLPPr1QTRHO5_M(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final int compare(Object obj, Object obj2) {
        return ChatAttachAlert.lambda$updateAlbumsDropDown$18(this.f$0, (AlbumEntry) obj, (AlbumEntry) obj2);
    }
}
