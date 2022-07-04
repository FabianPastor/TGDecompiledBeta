package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.MediaController;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda40 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ Integer f$3;
    public final /* synthetic */ MediaController.AlbumEntry f$4;
    public final /* synthetic */ MediaController.AlbumEntry f$5;
    public final /* synthetic */ MediaController.AlbumEntry f$6;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda40(int i, ArrayList arrayList, ArrayList arrayList2, Integer num, MediaController.AlbumEntry albumEntry, MediaController.AlbumEntry albumEntry2, MediaController.AlbumEntry albumEntry3) {
        this.f$0 = i;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = num;
        this.f$4 = albumEntry;
        this.f$5 = albumEntry2;
        this.f$6 = albumEntry3;
    }

    public final void run() {
        MediaController.lambda$broadcastNewPhotos$41(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
