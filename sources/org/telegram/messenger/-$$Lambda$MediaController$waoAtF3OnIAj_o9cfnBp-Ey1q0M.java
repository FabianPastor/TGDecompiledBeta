package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.MediaController.AlbumEntry;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$waoAtF3OnIAj_o9cfnBp-Ey1q0M implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ Integer f$3;
    private final /* synthetic */ AlbumEntry f$4;
    private final /* synthetic */ AlbumEntry f$5;
    private final /* synthetic */ AlbumEntry f$6;

    public /* synthetic */ -$$Lambda$MediaController$waoAtF3OnIAj_o9cfnBp-Ey1q0M(int i, ArrayList arrayList, ArrayList arrayList2, Integer num, AlbumEntry albumEntry, AlbumEntry albumEntry2, AlbumEntry albumEntry3) {
        this.f$0 = i;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = num;
        this.f$4 = albumEntry;
        this.f$5 = albumEntry2;
        this.f$6 = albumEntry3;
    }

    public final void run() {
        MediaController.lambda$broadcastNewPhotos$30(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
