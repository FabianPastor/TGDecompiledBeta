package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;

public class StickerSetBulletinLayout extends Bulletin.TwoLineLayout {
    public static final int TYPE_ADDED = 2;
    public static final int TYPE_ADDED_TO_FAVORITES = 5;
    public static final int TYPE_ARCHIVED = 1;
    public static final int TYPE_EMPTY = -1;
    public static final int TYPE_REMOVED = 0;
    public static final int TYPE_REMOVED_FROM_FAVORITES = 4;
    public static final int TYPE_REMOVED_FROM_RECENT = 3;

    public @interface Type {
    }

    public StickerSetBulletinLayout(Context context, TLObject setObject, int type) {
        this(context, setObject, type, (TLRPC.Document) null, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StickerSetBulletinLayout(Context context, TLObject setObject, int type, TLRPC.Document sticker, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        TLRPC.StickerSet stickerSet;
        TLRPC.Document sticker2;
        TLObject object;
        ImageLocation imageLocation;
        TLRPC.Document sticker3;
        TLRPC.Document sticker4;
        TLObject tLObject = setObject;
        if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet obj = (TLRPC.TL_messages_stickerSet) tLObject;
            TLRPC.StickerSet stickerSet2 = obj.set;
            ArrayList<TLRPC.Document> documents = obj.documents;
            if (documents == null || documents.isEmpty()) {
                sticker4 = null;
            } else {
                sticker4 = documents.get(0);
            }
            stickerSet = stickerSet2;
            sticker2 = sticker4;
        } else if (tLObject instanceof TLRPC.StickerSetCovered) {
            TLRPC.StickerSetCovered obj2 = (TLRPC.StickerSetCovered) tLObject;
            TLRPC.StickerSet stickerSet3 = obj2.set;
            if (obj2.cover != null) {
                sticker3 = obj2.cover;
            } else if (!obj2.covers.isEmpty()) {
                sticker3 = obj2.covers.get(0);
            } else {
                sticker3 = null;
            }
            stickerSet = stickerSet3;
            sticker2 = sticker3;
        } else if (sticker != null || tLObject == null || !BuildVars.DEBUG_VERSION) {
            sticker2 = sticker;
            stickerSet = null;
        } else {
            throw new IllegalArgumentException("Invalid type of the given setObject: " + setObject.getClass());
        }
        if (sticker2 != null) {
            TLObject object2 = stickerSet == null ? null : FileLoader.getClosestPhotoSizeWithSize(stickerSet.thumbs, 90);
            if (object2 == null) {
                object = sticker2;
            } else {
                object = object2;
            }
            if (object instanceof TLRPC.Document) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(sticker2.thumbs, 90), sticker2);
            } else {
                TLRPC.PhotoSize thumb = (TLRPC.PhotoSize) object;
                int thumbVersion = 0;
                if (tLObject instanceof TLRPC.StickerSetCovered) {
                    thumbVersion = ((TLRPC.StickerSetCovered) tLObject).set.thumb_version;
                } else if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
                    thumbVersion = ((TLRPC.TL_messages_stickerSet) tLObject).set.thumb_version;
                }
                imageLocation = ImageLocation.getForSticker(thumb, sticker2, thumbVersion);
            }
            if (!(object instanceof TLRPC.Document) || !MessageObject.isAnimatedStickerDocument(sticker2, true)) {
                ImageLocation imageLocation2 = imageLocation;
                if (imageLocation2 == null || imageLocation2.imageType != 1) {
                    this.imageView.setImage(imageLocation2, "50_50", "webp", (Drawable) null, (Object) setObject);
                } else {
                    this.imageView.setImage(imageLocation2, "50_50", "tgs", (Drawable) null, (Object) setObject);
                }
            } else {
                ImageLocation imageLocation3 = imageLocation;
                this.imageView.setImage(ImageLocation.getForDocument(sticker2), "50_50", imageLocation, (String) null, 0, (Object) setObject);
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, (Object) setObject);
        }
        switch (type) {
            case 0:
                if (stickerSet.masks) {
                    this.titleTextView.setText(LocaleController.getString("MasksRemoved", NUM));
                    this.subtitleTextView.setText(LocaleController.formatString("MasksRemovedInfo", NUM, stickerSet.title));
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("StickersRemoved", NUM));
                this.subtitleTextView.setText(LocaleController.formatString("StickersRemovedInfo", NUM, stickerSet.title));
                return;
            case 1:
                if (stickerSet.masks) {
                    this.titleTextView.setText(LocaleController.getString("MasksArchived", NUM));
                    this.subtitleTextView.setText(LocaleController.formatString("MasksArchivedInfo", NUM, stickerSet.title));
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("StickersArchived", NUM));
                this.subtitleTextView.setText(LocaleController.formatString("StickersArchivedInfo", NUM, stickerSet.title));
                return;
            case 2:
                if (stickerSet.masks) {
                    this.titleTextView.setText(LocaleController.getString("AddMasksInstalled", NUM));
                    this.subtitleTextView.setText(LocaleController.formatString("AddMasksInstalledInfo", NUM, stickerSet.title));
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("AddStickersInstalled", NUM));
                this.subtitleTextView.setText(LocaleController.formatString("AddStickersInstalledInfo", NUM, stickerSet.title));
                return;
            case 3:
                this.titleTextView.setText(LocaleController.getString("RemovedFromRecent", NUM));
                this.subtitleTextView.setVisibility(8);
                return;
            case 4:
                this.titleTextView.setText(LocaleController.getString("RemovedFromFavorites", NUM));
                this.subtitleTextView.setVisibility(8);
                return;
            case 5:
                this.titleTextView.setText(LocaleController.getString("AddedToFavorites", NUM));
                this.subtitleTextView.setVisibility(8);
                return;
            default:
                return;
        }
    }
}
