package org.telegram.ui.Components;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.messenger.MediaController.AlbumEntry;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatAttachAlert$rVZEYwbzmmg-AwHB2cx3WFhCcZY implements Comparator {
    private final /* synthetic */ ArrayList f$0;

    public /* synthetic */ -$$Lambda$ChatAttachAlert$rVZEYwbzmmg-AwHB2cx3WFhCcZY(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final int compare(Object obj, Object obj2) {
        return ChatAttachAlert.lambda$updateAlbumsDropDown$19(this.f$0, (AlbumEntry) obj, (AlbumEntry) obj2);
    }
}
