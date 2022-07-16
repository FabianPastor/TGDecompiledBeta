package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.ui.Components.EmojiView;

/* compiled from: EmojiPacksAlert */
class EmojiPacksLoader implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount;
    public ArrayList<EmojiView.CustomEmoji>[] data;
    public ArrayList<TLRPC$InputStickerSet> inputStickerSets;
    public ArrayList<TLRPC$TL_messages_stickerSet> stickerSets;

    /* access modifiers changed from: protected */
    public void onUpdate() {
        throw null;
    }

    public EmojiPacksLoader(int i, ArrayList<TLRPC$InputStickerSet> arrayList) {
        this.currentAccount = i;
        this.inputStickerSets = arrayList == null ? new ArrayList<>() : arrayList;
        init();
    }

    private void init() {
        this.stickerSets = new ArrayList<>(this.inputStickerSets.size());
        this.data = new ArrayList[this.inputStickerSets.size()];
        for (int i = 0; i < this.data.length; i++) {
            TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSets.get(i), false);
            this.stickerSets.add(stickerSet);
            putStickerSet(i, stickerSet);
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.groupStickersDidLoad) {
            for (int i3 = 0; i3 < this.stickerSets.size(); i3++) {
                if (this.stickerSets.get(i3) == null) {
                    TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSets.get(i3), true);
                    this.stickerSets.set(i3, stickerSet);
                    if (stickerSet != null) {
                        putStickerSet(i3, stickerSet);
                    }
                }
            }
            onUpdate();
        }
    }

    private void putStickerSet(int i, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        if (i >= 0) {
            ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
            if (i < arrayListArr.length) {
                int i2 = 0;
                if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents == null) {
                    arrayListArr[i] = new ArrayList<>(12);
                    while (i2 < 12) {
                        this.data[i].add((Object) null);
                        i2++;
                    }
                    return;
                }
                arrayListArr[i] = new ArrayList<>();
                while (i2 < tLRPC$TL_messages_stickerSet.documents.size()) {
                    TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i2);
                    if (tLRPC$Document == null) {
                        this.data[i].add((Object) null);
                    } else {
                        EmojiView.CustomEmoji customEmoji = new EmojiView.CustomEmoji();
                        findEmoticon(tLRPC$TL_messages_stickerSet, tLRPC$Document.id);
                        customEmoji.stickerSet = tLRPC$TL_messages_stickerSet;
                        customEmoji.documentId = tLRPC$Document.id;
                        this.data[i].add(customEmoji);
                    }
                    i2++;
                }
            }
        }
    }

    public int getItemsCount() {
        int i = 0;
        int i2 = 0;
        while (true) {
            ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
            if (i >= arrayListArr.length) {
                return i2;
            }
            i2 += arrayListArr[i].size() + 1;
            i++;
        }
    }

    public String findEmoticon(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, long j) {
        if (tLRPC$TL_messages_stickerSet == null) {
            return null;
        }
        for (int i = 0; i < tLRPC$TL_messages_stickerSet.packs.size(); i++) {
            TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i);
            ArrayList<Long> arrayList = tLRPC$TL_stickerPack.documents;
            if (arrayList != null && arrayList.contains(Long.valueOf(j))) {
                return tLRPC$TL_stickerPack.emoticon;
            }
        }
        return null;
    }
}
