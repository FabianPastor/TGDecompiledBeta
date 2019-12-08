package org.telegram.ui.Components;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.messenger.MediaController.AlbumEntry;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatAttachAlert$LsmqthiQk3XAtsrdBvAMcj7eB6E implements Comparator {
    private final /* synthetic */ ArrayList f$0;

    public /* synthetic */ -$$Lambda$ChatAttachAlert$LsmqthiQk3XAtsrdBvAMcj7eB6E(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final int compare(Object obj, Object obj2) {
        return ChatAttachAlert.lambda$updateAlbumsDropDown$22(this.f$0, (AlbumEntry) obj, (AlbumEntry) obj2);
    }
}
