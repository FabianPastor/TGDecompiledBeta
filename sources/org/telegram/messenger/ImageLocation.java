package org.telegram.messenger;

import org.telegram.messenger.DocumentObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class ImageLocation {
    public long access_hash;
    public int currentSize;
    public int dc_id;
    public TLRPC.Document document;
    public long documentId;
    public byte[] file_reference;
    public int imageType;
    public byte[] iv;
    public byte[] key;
    public TLRPC.TL_fileLocationToBeDeprecated location;
    public String path;
    public TLRPC.Photo photo;
    public long photoId;
    public TLRPC.InputPeer photoPeer;
    public boolean photoPeerBig;
    public TLRPC.PhotoSize photoSize;
    public SecureDocument secureDocument;
    public TLRPC.InputStickerSet stickerSet;
    public String thumbSize;
    public WebFile webFile;

    public static ImageLocation getForPath(String str) {
        if (str == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.path = str;
        return imageLocation;
    }

    public static ImageLocation getForSecureDocument(SecureDocument secureDocument2) {
        if (secureDocument2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.secureDocument = secureDocument2;
        return imageLocation;
    }

    public static ImageLocation getForDocument(TLRPC.Document document2) {
        if (document2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.document = document2;
        imageLocation.key = document2.key;
        imageLocation.iv = document2.iv;
        imageLocation.currentSize = document2.size;
        return imageLocation;
    }

    public static ImageLocation getForWebFile(WebFile webFile2) {
        if (webFile2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.webFile = webFile2;
        imageLocation.currentSize = webFile2.size;
        return imageLocation;
    }

    public static ImageLocation getForObject(TLRPC.PhotoSize photoSize2, TLObject tLObject) {
        if (tLObject instanceof TLRPC.Photo) {
            return getForPhoto(photoSize2, (TLRPC.Photo) tLObject);
        }
        if (tLObject instanceof TLRPC.Document) {
            return getForDocument(photoSize2, (TLRPC.Document) tLObject);
        }
        return null;
    }

    public static ImageLocation getForPhoto(TLRPC.PhotoSize photoSize2, TLRPC.Photo photo2) {
        if (photoSize2 instanceof TLRPC.TL_photoStrippedSize) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize2;
            return imageLocation;
        } else if (photoSize2 == null || photo2 == null) {
            return null;
        } else {
            int i = photo2.dc_id;
            if (i == 0) {
                i = photoSize2.location.dc_id;
            }
            return getForPhoto(photoSize2.location, photoSize2.size, photo2, (TLRPC.Document) null, (TLRPC.InputPeer) null, false, i, (TLRPC.InputStickerSet) null, photoSize2.type);
        }
    }

    public static ImageLocation getForUser(TLRPC.User user, boolean z) {
        TLRPC.UserProfilePhoto userProfilePhoto;
        if (user == null || user.access_hash == 0 || (userProfilePhoto = user.photo) == null) {
            return null;
        }
        TLRPC.FileLocation fileLocation = z ? userProfilePhoto.photo_big : userProfilePhoto.photo_small;
        if (fileLocation == null) {
            return null;
        }
        TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
        tL_inputPeerUser.user_id = user.id;
        tL_inputPeerUser.access_hash = user.access_hash;
        int i = user.photo.dc_id;
        if (i == 0) {
            i = fileLocation.dc_id;
        }
        return getForPhoto(fileLocation, 0, (TLRPC.Photo) null, (TLRPC.Document) null, tL_inputPeerUser, z, i, (TLRPC.InputStickerSet) null, (String) null);
    }

    public static ImageLocation getForChat(TLRPC.Chat chat, boolean z) {
        TLRPC.ChatPhoto chatPhoto;
        TLRPC.InputPeer inputPeer;
        if (chat == null || (chatPhoto = chat.photo) == null) {
            return null;
        }
        TLRPC.FileLocation fileLocation = z ? chatPhoto.photo_big : chatPhoto.photo_small;
        if (fileLocation == null) {
            return null;
        }
        if (!ChatObject.isChannel(chat)) {
            inputPeer = new TLRPC.TL_inputPeerChat();
            inputPeer.chat_id = chat.id;
        } else if (chat.access_hash == 0) {
            return null;
        } else {
            inputPeer = new TLRPC.TL_inputPeerChannel();
            inputPeer.channel_id = chat.id;
            inputPeer.access_hash = chat.access_hash;
        }
        TLRPC.InputPeer inputPeer2 = inputPeer;
        int i = chat.photo.dc_id;
        if (i == 0) {
            i = fileLocation.dc_id;
        }
        return getForPhoto(fileLocation, 0, (TLRPC.Photo) null, (TLRPC.Document) null, inputPeer2, z, i, (TLRPC.InputStickerSet) null, (String) null);
    }

    public static ImageLocation getForSticker(TLRPC.PhotoSize photoSize2, TLRPC.Document document2) {
        TLRPC.InputStickerSet inputStickerSet;
        if (photoSize2 instanceof TLRPC.TL_photoStrippedSize) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize2;
            return imageLocation;
        } else if (photoSize2 == null || document2 == null || (inputStickerSet = MediaDataController.getInputStickerSet(document2)) == null) {
            return null;
        } else {
            ImageLocation forPhoto = getForPhoto(photoSize2.location, photoSize2.size, (TLRPC.Photo) null, (TLRPC.Document) null, (TLRPC.InputPeer) null, false, document2.dc_id, inputStickerSet, photoSize2.type);
            if (MessageObject.isAnimatedStickerDocument(document2, true)) {
                forPhoto.imageType = 1;
            }
            return forPhoto;
        }
    }

    public static ImageLocation getForDocument(TLRPC.PhotoSize photoSize2, TLRPC.Document document2) {
        if (photoSize2 instanceof TLRPC.TL_photoStrippedSize) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize2;
            return imageLocation;
        } else if (photoSize2 == null || document2 == null) {
            return null;
        } else {
            return getForPhoto(photoSize2.location, photoSize2.size, (TLRPC.Photo) null, document2, (TLRPC.InputPeer) null, false, document2.dc_id, (TLRPC.InputStickerSet) null, photoSize2.type);
        }
    }

    public static ImageLocation getForLocal(TLRPC.FileLocation fileLocation) {
        if (fileLocation == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.location = new TLRPC.TL_fileLocationToBeDeprecated();
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = imageLocation.location;
        tL_fileLocationToBeDeprecated.local_id = fileLocation.local_id;
        tL_fileLocationToBeDeprecated.volume_id = fileLocation.volume_id;
        tL_fileLocationToBeDeprecated.secret = fileLocation.secret;
        tL_fileLocationToBeDeprecated.dc_id = fileLocation.dc_id;
        return imageLocation;
    }

    private static ImageLocation getForPhoto(TLRPC.FileLocation fileLocation, int i, TLRPC.Photo photo2, TLRPC.Document document2, TLRPC.InputPeer inputPeer, boolean z, int i2, TLRPC.InputStickerSet inputStickerSet, String str) {
        if (fileLocation == null) {
            return null;
        }
        if (photo2 == null && inputPeer == null && inputStickerSet == null && document2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.dc_id = i2;
        imageLocation.photo = photo2;
        imageLocation.currentSize = i;
        imageLocation.photoPeer = inputPeer;
        imageLocation.photoPeerBig = z;
        imageLocation.stickerSet = inputStickerSet;
        if (fileLocation instanceof TLRPC.TL_fileLocationToBeDeprecated) {
            imageLocation.location = (TLRPC.TL_fileLocationToBeDeprecated) fileLocation;
            if (photo2 != null) {
                imageLocation.file_reference = photo2.file_reference;
                imageLocation.access_hash = photo2.access_hash;
                imageLocation.photoId = photo2.id;
                imageLocation.thumbSize = str;
            } else if (document2 != null) {
                imageLocation.file_reference = document2.file_reference;
                imageLocation.access_hash = document2.access_hash;
                imageLocation.documentId = document2.id;
                imageLocation.thumbSize = str;
            }
        } else {
            imageLocation.location = new TLRPC.TL_fileLocationToBeDeprecated();
            TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = imageLocation.location;
            tL_fileLocationToBeDeprecated.local_id = fileLocation.local_id;
            tL_fileLocationToBeDeprecated.volume_id = fileLocation.volume_id;
            tL_fileLocationToBeDeprecated.secret = fileLocation.secret;
            imageLocation.dc_id = fileLocation.dc_id;
            imageLocation.file_reference = fileLocation.file_reference;
            imageLocation.key = fileLocation.key;
            imageLocation.iv = fileLocation.iv;
            imageLocation.access_hash = fileLocation.secret;
        }
        return imageLocation;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0022  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x003c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getStippedKey(java.lang.Object r3, java.lang.Object r4, java.lang.Object r5) {
        /*
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC.WebPage
            java.lang.String r1 = "stripped"
            if (r0 == 0) goto L_0x00f0
            boolean r0 = r4 instanceof org.telegram.messenger.ImageLocation
            if (r0 == 0) goto L_0x001d
            r0 = r4
            org.telegram.messenger.ImageLocation r0 = (org.telegram.messenger.ImageLocation) r0
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            if (r2 == 0) goto L_0x0012
            goto L_0x001e
        L_0x0012:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r0.photoSize
            if (r2 == 0) goto L_0x0017
            goto L_0x001e
        L_0x0017:
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            if (r0 == 0) goto L_0x001d
            r2 = r0
            goto L_0x001e
        L_0x001d:
            r2 = r4
        L_0x001e:
            java.lang.String r4 = "_"
            if (r2 != 0) goto L_0x003c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            java.lang.String r3 = org.telegram.messenger.FileRefController.getKeyForParentObject(r3)
            r0.append(r3)
            r0.append(r4)
            r0.append(r5)
            java.lang.String r3 = r0.toString()
            return r3
        L_0x003c:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC.Document
            if (r5 == 0) goto L_0x005e
            org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC.Document) r2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r1)
            java.lang.String r3 = org.telegram.messenger.FileRefController.getKeyForParentObject(r3)
            r5.append(r3)
            r5.append(r4)
            long r3 = r2.id
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            return r3
        L_0x005e:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC.Photo
            if (r5 == 0) goto L_0x0080
            org.telegram.tgnet.TLRPC$Photo r2 = (org.telegram.tgnet.TLRPC.Photo) r2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r1)
            java.lang.String r3 = org.telegram.messenger.FileRefController.getKeyForParentObject(r3)
            r5.append(r3)
            r5.append(r4)
            long r3 = r2.id
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            return r3
        L_0x0080:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC.PhotoSize
            if (r5 == 0) goto L_0x00c6
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2
            org.telegram.tgnet.TLRPC$FileLocation r5 = r2.location
            if (r5 == 0) goto L_0x00b2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r1)
            java.lang.String r3 = org.telegram.messenger.FileRefController.getKeyForParentObject(r3)
            r5.append(r3)
            r5.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            int r3 = r3.local_id
            r5.append(r3)
            r5.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            long r3 = r3.volume_id
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            return r3
        L_0x00b2:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            java.lang.String r3 = org.telegram.messenger.FileRefController.getKeyForParentObject(r3)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            return r3
        L_0x00c6:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC.FileLocation
            if (r5 == 0) goto L_0x00f0
            org.telegram.tgnet.TLRPC$FileLocation r2 = (org.telegram.tgnet.TLRPC.FileLocation) r2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r1)
            java.lang.String r3 = org.telegram.messenger.FileRefController.getKeyForParentObject(r3)
            r5.append(r3)
            r5.append(r4)
            int r3 = r2.local_id
            r5.append(r3)
            r5.append(r4)
            long r3 = r2.volume_id
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            return r3
        L_0x00f0:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            java.lang.String r3 = org.telegram.messenger.FileRefController.getKeyForParentObject(r3)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLocation.getStippedKey(java.lang.Object, java.lang.Object, java.lang.Object):java.lang.String");
    }

    public String getKey(Object obj, Object obj2, boolean z) {
        if (this.secureDocument != null) {
            return this.secureDocument.secureFile.dc_id + "_" + this.secureDocument.secureFile.id;
        }
        TLRPC.PhotoSize photoSize2 = this.photoSize;
        if (photoSize2 instanceof TLRPC.TL_photoStrippedSize) {
            if (photoSize2.bytes.length > 0) {
                return getStippedKey(obj, obj2, photoSize2);
            }
            return null;
        } else if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id;
        } else {
            WebFile webFile2 = this.webFile;
            if (webFile2 != null) {
                return Utilities.MD5(webFile2.url);
            }
            TLRPC.Document document2 = this.document;
            if (document2 == null) {
                String str = this.path;
                if (str != null) {
                    return Utilities.MD5(str);
                }
                return null;
            } else if (z || !(document2 instanceof DocumentObject.ThemeDocument)) {
                TLRPC.Document document3 = this.document;
                if (document3.id == 0 || document3.dc_id == 0) {
                    return null;
                }
                return this.document.dc_id + "_" + this.document.id;
            } else {
                DocumentObject.ThemeDocument themeDocument = (DocumentObject.ThemeDocument) document2;
                return this.document.dc_id + "_" + this.document.id + "_" + Theme.getBaseThemeKey(themeDocument.themeSettings) + "_" + themeDocument.themeSettings.accent_color + "_" + themeDocument.themeSettings.message_top_color + "_" + themeDocument.themeSettings.message_bottom_color;
            }
        }
    }

    public boolean isEncrypted() {
        return this.key != null;
    }

    public int getSize() {
        TLRPC.PhotoSize photoSize2 = this.photoSize;
        if (photoSize2 != null) {
            return photoSize2.size;
        }
        SecureDocument secureDocument2 = this.secureDocument;
        if (secureDocument2 != null) {
            TLRPC.TL_secureFile tL_secureFile = secureDocument2.secureFile;
            if (tL_secureFile != null) {
                return tL_secureFile.size;
            }
        } else {
            TLRPC.Document document2 = this.document;
            if (document2 != null) {
                return document2.size;
            }
            WebFile webFile2 = this.webFile;
            if (webFile2 != null) {
                return webFile2.size;
            }
        }
        return this.currentSize;
    }
}
