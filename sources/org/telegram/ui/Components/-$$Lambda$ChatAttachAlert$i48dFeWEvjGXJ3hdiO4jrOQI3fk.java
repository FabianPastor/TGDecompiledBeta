package org.telegram.ui.Components;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.messenger.MediaController.AlbumEntry;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatAttachAlert$i48dFeWEvjGXJ3hdiO4jrOQI3fk implements Comparator {
    private final /* synthetic */ ArrayList f$0;

    public /* synthetic */ -$$Lambda$ChatAttachAlert$i48dFeWEvjGXJ3hdiO4jrOQI3fk(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final int compare(Object obj, Object obj2) {
        return ChatAttachAlert.lambda$updateAlbumsDropDown$23(this.f$0, (AlbumEntry) obj, (AlbumEntry) obj2);
    }
}
