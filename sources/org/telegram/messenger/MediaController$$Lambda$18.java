package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.MediaController.AlbumEntry;

final /* synthetic */ class MediaController$$Lambda$18 implements Runnable {
    private final int arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;
    private final Integer arg$4;
    private final AlbumEntry arg$5;
    private final AlbumEntry arg$6;

    MediaController$$Lambda$18(int i, ArrayList arrayList, ArrayList arrayList2, Integer num, AlbumEntry albumEntry, AlbumEntry albumEntry2) {
        this.arg$1 = i;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
        this.arg$4 = num;
        this.arg$5 = albumEntry;
        this.arg$6 = albumEntry2;
    }

    public void run() {
        MediaController.lambda$broadcastNewPhotos$28$MediaController(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}