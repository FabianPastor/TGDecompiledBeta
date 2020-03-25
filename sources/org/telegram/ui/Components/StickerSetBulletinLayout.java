package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.ui.Components.Bulletin;

@SuppressLint({"ViewConstructor"})
public class StickerSetBulletinLayout extends Bulletin.TwoLineLayout {
    public StickerSetBulletinLayout(Context context, TLObject tLObject, int i) {
        super(context);
        TLRPC$StickerSet tLRPC$StickerSet;
        ImageLocation imageLocation;
        TLRPC$Document tLRPC$Document = null;
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
            ArrayList<TLRPC$Document> arrayList = tLRPC$TL_messages_stickerSet.documents;
            if (arrayList != null && !arrayList.isEmpty()) {
                tLRPC$Document = arrayList.get(0);
            }
        } else if (tLObject instanceof TLRPC$StickerSetCovered) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) tLObject;
            tLRPC$StickerSet = tLRPC$StickerSetCovered.set;
            TLRPC$Document tLRPC$Document2 = tLRPC$StickerSetCovered.cover;
            if (tLRPC$Document2 != null) {
                tLRPC$Document = tLRPC$Document2;
            } else if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                tLRPC$Document = tLRPC$StickerSetCovered.covers.get(0);
            }
        } else {
            throw new IllegalArgumentException("Invalid type of the given setObject: " + tLObject.getClass());
        }
        TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$StickerSet;
        if (tLRPC$Document != null) {
            TLObject tLObject2 = tLRPC$StickerSet2.thumb;
            tLObject2 = !(tLObject2 instanceof TLRPC$TL_photoSize) ? tLRPC$Document : tLObject2;
            boolean z = tLObject2 instanceof TLRPC$Document;
            if (z) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) tLObject2, tLRPC$Document);
            }
            ImageLocation imageLocation2 = imageLocation;
            if (z && MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", imageLocation2, (String) null, 0, (Object) tLObject);
            } else if (imageLocation2 == null || imageLocation2.imageType != 1) {
                this.imageView.setImage(imageLocation2, "50_50", "webp", (Drawable) null, tLObject);
            } else {
                this.imageView.setImage(imageLocation2, "50_50", "tgs", (Drawable) null, tLObject);
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, tLObject);
        }
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    if (tLRPC$StickerSet2.masks) {
                        this.titleTextView.setText(LocaleController.getString("AddMasksInstalled", NUM));
                        this.subtitleTextView.setText(LocaleController.formatString("AddMasksInstalledInfo", NUM, tLRPC$StickerSet2.title));
                        return;
                    }
                    this.titleTextView.setText(LocaleController.getString("AddStickersInstalled", NUM));
                    this.subtitleTextView.setText(LocaleController.formatString("AddStickersInstalledInfo", NUM, tLRPC$StickerSet2.title));
                }
            } else if (tLRPC$StickerSet2.masks) {
                this.titleTextView.setText(LocaleController.getString("MasksArchived", NUM));
                this.subtitleTextView.setText(LocaleController.formatString("MasksArchivedInfo", NUM, tLRPC$StickerSet2.title));
            } else {
                this.titleTextView.setText(LocaleController.getString("StickersArchived", NUM));
                this.subtitleTextView.setText(LocaleController.formatString("StickersArchivedInfo", NUM, tLRPC$StickerSet2.title));
            }
        } else if (tLRPC$StickerSet2.masks) {
            this.titleTextView.setText(LocaleController.getString("MasksRemoved", NUM));
            this.subtitleTextView.setText(LocaleController.formatString("MasksRemovedInfo", NUM, tLRPC$StickerSet2.title));
        } else {
            this.titleTextView.setText(LocaleController.getString("StickersRemoved", NUM));
            this.subtitleTextView.setText(LocaleController.formatString("StickersRemovedInfo", NUM, tLRPC$StickerSet2.title));
        }
    }
}
