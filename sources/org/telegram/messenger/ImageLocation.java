package org.telegram.messenger;

import org.telegram.messenger.DocumentObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_secureFile;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.Theme;

public class ImageLocation {
    public long access_hash;
    public int currentSize;
    public int dc_id;
    public TLRPC$Document document;
    public long documentId;
    public byte[] file_reference;
    public int imageType;
    public byte[] iv;
    public byte[] key;
    public TLRPC$TL_fileLocationToBeDeprecated location;
    public String path;
    public TLRPC$Photo photo;
    public long photoId;
    public TLRPC$InputPeer photoPeer;
    public boolean photoPeerBig;
    public TLRPC$PhotoSize photoSize;
    public SecureDocument secureDocument;
    public TLRPC$InputStickerSet stickerSet;
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

    public static ImageLocation getForDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.document = tLRPC$Document;
        imageLocation.key = tLRPC$Document.key;
        imageLocation.iv = tLRPC$Document.iv;
        imageLocation.currentSize = tLRPC$Document.size;
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

    public static ImageLocation getForObject(TLRPC$PhotoSize tLRPC$PhotoSize, TLObject tLObject) {
        if (tLObject instanceof TLRPC$Photo) {
            return getForPhoto(tLRPC$PhotoSize, (TLRPC$Photo) tLObject);
        }
        if (tLObject instanceof TLRPC$Document) {
            return getForDocument(tLRPC$PhotoSize, (TLRPC$Document) tLObject);
        }
        return null;
    }

    public static ImageLocation getForPhoto(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$Photo tLRPC$Photo) {
        if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = tLRPC$PhotoSize;
            return imageLocation;
        } else if (tLRPC$PhotoSize == null || tLRPC$Photo == null) {
            return null;
        } else {
            int i = tLRPC$Photo.dc_id;
            if (i == 0) {
                i = tLRPC$PhotoSize.location.dc_id;
            }
            return getForPhoto(tLRPC$PhotoSize.location, tLRPC$PhotoSize.size, tLRPC$Photo, (TLRPC$Document) null, (TLRPC$InputPeer) null, false, i, (TLRPC$InputStickerSet) null, tLRPC$PhotoSize.type);
        }
    }

    public static ImageLocation getForUser(TLRPC$User tLRPC$User, boolean z) {
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto;
        if (tLRPC$User == null || tLRPC$User.access_hash == 0 || (tLRPC$UserProfilePhoto = tLRPC$User.photo) == null) {
            return null;
        }
        TLRPC$FileLocation tLRPC$FileLocation = z ? tLRPC$UserProfilePhoto.photo_big : tLRPC$UserProfilePhoto.photo_small;
        if (tLRPC$FileLocation == null) {
            return null;
        }
        TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
        tLRPC$TL_inputPeerUser.user_id = tLRPC$User.id;
        tLRPC$TL_inputPeerUser.access_hash = tLRPC$User.access_hash;
        int i = tLRPC$User.photo.dc_id;
        if (i == 0) {
            i = tLRPC$FileLocation.dc_id;
        }
        return getForPhoto(tLRPC$FileLocation, 0, (TLRPC$Photo) null, (TLRPC$Document) null, tLRPC$TL_inputPeerUser, z, i, (TLRPC$InputStickerSet) null, (String) null);
    }

    public static ImageLocation getForChat(TLRPC$Chat tLRPC$Chat, boolean z) {
        TLRPC$ChatPhoto tLRPC$ChatPhoto;
        TLRPC$InputPeer tLRPC$InputPeer;
        if (tLRPC$Chat == null || (tLRPC$ChatPhoto = tLRPC$Chat.photo) == null) {
            return null;
        }
        TLRPC$FileLocation tLRPC$FileLocation = z ? tLRPC$ChatPhoto.photo_big : tLRPC$ChatPhoto.photo_small;
        if (tLRPC$FileLocation == null) {
            return null;
        }
        if (!ChatObject.isChannel(tLRPC$Chat)) {
            tLRPC$InputPeer = new TLRPC$TL_inputPeerChat();
            tLRPC$InputPeer.chat_id = tLRPC$Chat.id;
        } else if (tLRPC$Chat.access_hash == 0) {
            return null;
        } else {
            tLRPC$InputPeer = new TLRPC$TL_inputPeerChannel();
            tLRPC$InputPeer.channel_id = tLRPC$Chat.id;
            tLRPC$InputPeer.access_hash = tLRPC$Chat.access_hash;
        }
        TLRPC$InputPeer tLRPC$InputPeer2 = tLRPC$InputPeer;
        int i = tLRPC$Chat.photo.dc_id;
        if (i == 0) {
            i = tLRPC$FileLocation.dc_id;
        }
        return getForPhoto(tLRPC$FileLocation, 0, (TLRPC$Photo) null, (TLRPC$Document) null, tLRPC$InputPeer2, z, i, (TLRPC$InputStickerSet) null, (String) null);
    }

    public static ImageLocation getForSticker(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$Document tLRPC$Document) {
        TLRPC$InputStickerSet inputStickerSet;
        if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = tLRPC$PhotoSize;
            return imageLocation;
        } else if (tLRPC$PhotoSize == null || tLRPC$Document == null || (inputStickerSet = MediaDataController.getInputStickerSet(tLRPC$Document)) == null) {
            return null;
        } else {
            ImageLocation forPhoto = getForPhoto(tLRPC$PhotoSize.location, tLRPC$PhotoSize.size, (TLRPC$Photo) null, (TLRPC$Document) null, (TLRPC$InputPeer) null, false, tLRPC$Document.dc_id, inputStickerSet, tLRPC$PhotoSize.type);
            if (MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                forPhoto.imageType = 1;
            }
            return forPhoto;
        }
    }

    public static ImageLocation getForDocument(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$Document tLRPC$Document) {
        if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = tLRPC$PhotoSize;
            return imageLocation;
        } else if (tLRPC$PhotoSize == null || tLRPC$Document == null) {
            return null;
        } else {
            return getForPhoto(tLRPC$PhotoSize.location, tLRPC$PhotoSize.size, (TLRPC$Photo) null, tLRPC$Document, (TLRPC$InputPeer) null, false, tLRPC$Document.dc_id, (TLRPC$InputStickerSet) null, tLRPC$PhotoSize.type);
        }
    }

    public static ImageLocation getForLocal(TLRPC$FileLocation tLRPC$FileLocation) {
        if (tLRPC$FileLocation == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = new TLRPC$TL_fileLocationToBeDeprecated();
        imageLocation.location = tLRPC$TL_fileLocationToBeDeprecated;
        tLRPC$TL_fileLocationToBeDeprecated.local_id = tLRPC$FileLocation.local_id;
        tLRPC$TL_fileLocationToBeDeprecated.volume_id = tLRPC$FileLocation.volume_id;
        tLRPC$TL_fileLocationToBeDeprecated.secret = tLRPC$FileLocation.secret;
        tLRPC$TL_fileLocationToBeDeprecated.dc_id = tLRPC$FileLocation.dc_id;
        return imageLocation;
    }

    private static ImageLocation getForPhoto(TLRPC$FileLocation tLRPC$FileLocation, int i, TLRPC$Photo tLRPC$Photo, TLRPC$Document tLRPC$Document, TLRPC$InputPeer tLRPC$InputPeer, boolean z, int i2, TLRPC$InputStickerSet tLRPC$InputStickerSet, String str) {
        if (tLRPC$FileLocation == null) {
            return null;
        }
        if (tLRPC$Photo == null && tLRPC$InputPeer == null && tLRPC$InputStickerSet == null && tLRPC$Document == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.dc_id = i2;
        imageLocation.photo = tLRPC$Photo;
        imageLocation.currentSize = i;
        imageLocation.photoPeer = tLRPC$InputPeer;
        imageLocation.photoPeerBig = z;
        imageLocation.stickerSet = tLRPC$InputStickerSet;
        if (tLRPC$FileLocation instanceof TLRPC$TL_fileLocationToBeDeprecated) {
            imageLocation.location = (TLRPC$TL_fileLocationToBeDeprecated) tLRPC$FileLocation;
            if (tLRPC$Photo != null) {
                imageLocation.file_reference = tLRPC$Photo.file_reference;
                imageLocation.access_hash = tLRPC$Photo.access_hash;
                imageLocation.photoId = tLRPC$Photo.id;
                imageLocation.thumbSize = str;
            } else if (tLRPC$Document != null) {
                imageLocation.file_reference = tLRPC$Document.file_reference;
                imageLocation.access_hash = tLRPC$Document.access_hash;
                imageLocation.documentId = tLRPC$Document.id;
                imageLocation.thumbSize = str;
            }
        } else {
            TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = new TLRPC$TL_fileLocationToBeDeprecated();
            imageLocation.location = tLRPC$TL_fileLocationToBeDeprecated;
            tLRPC$TL_fileLocationToBeDeprecated.local_id = tLRPC$FileLocation.local_id;
            tLRPC$TL_fileLocationToBeDeprecated.volume_id = tLRPC$FileLocation.volume_id;
            tLRPC$TL_fileLocationToBeDeprecated.secret = tLRPC$FileLocation.secret;
            imageLocation.dc_id = tLRPC$FileLocation.dc_id;
            imageLocation.file_reference = tLRPC$FileLocation.file_reference;
            imageLocation.key = tLRPC$FileLocation.key;
            imageLocation.iv = tLRPC$FileLocation.iv;
            imageLocation.access_hash = tLRPC$FileLocation.secret;
        }
        return imageLocation;
    }

    public static String getStippedKey(Object obj, Object obj2, Object obj3) {
        if (obj instanceof TLRPC$WebPage) {
            if (obj2 instanceof ImageLocation) {
                ImageLocation imageLocation = (ImageLocation) obj2;
                Object obj4 = imageLocation.document;
                if (obj4 == null && (obj4 = imageLocation.photoSize) == null) {
                    TLRPC$Photo tLRPC$Photo = imageLocation.photo;
                    if (tLRPC$Photo != null) {
                        obj2 = tLRPC$Photo;
                    }
                } else {
                    obj2 = obj4;
                }
            }
            if (obj2 == null) {
                return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + obj3;
            } else if (obj2 instanceof TLRPC$Document) {
                return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + ((TLRPC$Document) obj2).id;
            } else if (obj2 instanceof TLRPC$Photo) {
                return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + ((TLRPC$Photo) obj2).id;
            } else if (obj2 instanceof TLRPC$PhotoSize) {
                TLRPC$PhotoSize tLRPC$PhotoSize = (TLRPC$PhotoSize) obj2;
                if (tLRPC$PhotoSize.location != null) {
                    return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + tLRPC$PhotoSize.location.local_id + "_" + tLRPC$PhotoSize.location.volume_id;
                }
                return "stripped" + FileRefController.getKeyForParentObject(obj);
            } else if (obj2 instanceof TLRPC$FileLocation) {
                TLRPC$FileLocation tLRPC$FileLocation = (TLRPC$FileLocation) obj2;
                return "stripped" + FileRefController.getKeyForParentObject(obj) + "_" + tLRPC$FileLocation.local_id + "_" + tLRPC$FileLocation.volume_id;
            }
        }
        return "stripped" + FileRefController.getKeyForParentObject(obj);
    }

    public String getKey(Object obj, Object obj2, boolean z) {
        if (this.secureDocument != null) {
            return this.secureDocument.secureFile.dc_id + "_" + this.secureDocument.secureFile.id;
        }
        TLRPC$PhotoSize tLRPC$PhotoSize = this.photoSize;
        if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
            if (tLRPC$PhotoSize.bytes.length > 0) {
                return getStippedKey(obj, obj2, tLRPC$PhotoSize);
            }
            return null;
        } else if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id;
        } else {
            WebFile webFile2 = this.webFile;
            if (webFile2 != null) {
                return Utilities.MD5(webFile2.url);
            }
            TLRPC$Document tLRPC$Document = this.document;
            if (tLRPC$Document == null) {
                String str = this.path;
                if (str != null) {
                    return Utilities.MD5(str);
                }
                return null;
            } else if (z || !(tLRPC$Document instanceof DocumentObject.ThemeDocument)) {
                TLRPC$Document tLRPC$Document2 = this.document;
                if (tLRPC$Document2.id == 0 || tLRPC$Document2.dc_id == 0) {
                    return null;
                }
                return this.document.dc_id + "_" + this.document.id;
            } else {
                DocumentObject.ThemeDocument themeDocument = (DocumentObject.ThemeDocument) tLRPC$Document;
                return this.document.dc_id + "_" + this.document.id + "_" + Theme.getBaseThemeKey(themeDocument.themeSettings) + "_" + themeDocument.themeSettings.accent_color + "_" + themeDocument.themeSettings.message_top_color + "_" + themeDocument.themeSettings.message_bottom_color;
            }
        }
    }

    public boolean isEncrypted() {
        return this.key != null;
    }

    public int getSize() {
        TLRPC$PhotoSize tLRPC$PhotoSize = this.photoSize;
        if (tLRPC$PhotoSize != null) {
            return tLRPC$PhotoSize.size;
        }
        SecureDocument secureDocument2 = this.secureDocument;
        if (secureDocument2 != null) {
            TLRPC$TL_secureFile tLRPC$TL_secureFile = secureDocument2.secureFile;
            if (tLRPC$TL_secureFile != null) {
                return tLRPC$TL_secureFile.size;
            }
        } else {
            TLRPC$Document tLRPC$Document = this.document;
            if (tLRPC$Document != null) {
                return tLRPC$Document.size;
            }
            WebFile webFile2 = this.webFile;
            if (webFile2 != null) {
                return webFile2.size;
            }
        }
        return this.currentSize;
    }
}
