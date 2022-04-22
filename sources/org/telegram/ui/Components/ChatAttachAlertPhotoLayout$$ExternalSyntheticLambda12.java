package org.telegram.ui.Components;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda12 implements Comparator {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda12(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final int compare(Object obj, Object obj2) {
        return ChatAttachAlertPhotoLayout.lambda$updateAlbumsDropDown$8(this.f$0, (MediaController.AlbumEntry) obj, (MediaController.AlbumEntry) obj2);
    }
}
