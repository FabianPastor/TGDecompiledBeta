package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.DocumentObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_photoPathSize;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_secureFile;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes.dex */
public class ImageLocation {
    public static final int TYPE_BIG = 0;
    public static final int TYPE_SMALL = 1;
    public static final int TYPE_STRIPPED = 2;
    public static final int TYPE_VIDEO_THUMB = 3;
    public long access_hash;
    public long currentSize;
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
    public int photoPeerType;
    public TLRPC$PhotoSize photoSize;
    public SecureDocument secureDocument;
    public TLRPC$InputStickerSet stickerSet;
    public String thumbSize;
    public int thumbVersion;
    public long videoSeekTo;
    public WebFile webFile;

    public static ImageLocation getForPath(String str) {
        if (str == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.path = str;
        return imageLocation;
    }

    public static ImageLocation getForSecureDocument(SecureDocument secureDocument) {
        if (secureDocument == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.secureDocument = secureDocument;
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

    public static ImageLocation getForWebFile(WebFile webFile) {
        if (webFile == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.webFile = webFile;
        imageLocation.currentSize = webFile.size;
        return imageLocation;
    }

    public static ImageLocation getForObject(TLRPC$PhotoSize tLRPC$PhotoSize, TLObject tLObject) {
        if (tLObject instanceof TLRPC$Photo) {
            return getForPhoto(tLRPC$PhotoSize, (TLRPC$Photo) tLObject);
        }
        if (tLObject instanceof TLRPC$Document) {
            return getForDocument(tLRPC$PhotoSize, (TLRPC$Document) tLObject);
        }
        if (!(tLObject instanceof TLRPC$Message)) {
            return null;
        }
        return getForMessage(tLRPC$PhotoSize, (TLRPC$Message) tLObject);
    }

    public static ImageLocation getForMessage(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$Message tLRPC$Message) {
        if ((tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) || (tLRPC$PhotoSize instanceof TLRPC$TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = tLRPC$PhotoSize;
            return imageLocation;
        }
        return null;
    }

    public static ImageLocation getForPhoto(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$Photo tLRPC$Photo) {
        if ((tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) || (tLRPC$PhotoSize instanceof TLRPC$TL_photoPathSize)) {
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
            return getForPhoto(tLRPC$PhotoSize.location, tLRPC$PhotoSize.size, tLRPC$Photo, null, null, 1, i, null, tLRPC$PhotoSize.type);
        }
    }

    public static ImageLocation getForUserOrChat(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$User) {
            return getForUser((TLRPC$User) tLObject, i);
        }
        if (!(tLObject instanceof TLRPC$Chat)) {
            return null;
        }
        return getForChat((TLRPC$Chat) tLObject, i);
    }

    public static ImageLocation getForUser(TLRPC$User tLRPC$User, int i) {
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto;
        TLRPC$UserFull userFull;
        TLRPC$Photo tLRPC$Photo;
        ArrayList<TLRPC$VideoSize> arrayList;
        if (tLRPC$User == null || tLRPC$User.access_hash == 0 || (tLRPC$UserProfilePhoto = tLRPC$User.photo) == null) {
            return null;
        }
        if (i == 3) {
            int i2 = UserConfig.selectedAccount;
            if (!MessagesController.getInstance(i2).isPremiumUser(tLRPC$User) || !tLRPC$User.photo.has_video || (userFull = MessagesController.getInstance(i2).getUserFull(tLRPC$User.id)) == null || (tLRPC$Photo = userFull.profile_photo) == null || (arrayList = tLRPC$Photo.video_sizes) == null || arrayList.isEmpty()) {
                return null;
            }
            int i3 = 0;
            TLRPC$VideoSize tLRPC$VideoSize = userFull.profile_photo.video_sizes.get(0);
            while (true) {
                if (i3 >= userFull.profile_photo.video_sizes.size()) {
                    break;
                } else if ("p".equals(userFull.profile_photo.video_sizes.get(i3).type)) {
                    tLRPC$VideoSize = userFull.profile_photo.video_sizes.get(i3);
                    break;
                } else {
                    i3++;
                }
            }
            return getForPhoto(tLRPC$VideoSize, userFull.profile_photo);
        } else if (i == 2) {
            if (tLRPC$UserProfilePhoto.stripped_thumb == null) {
                return null;
            }
            ImageLocation imageLocation = new ImageLocation();
            TLRPC$TL_photoStrippedSize tLRPC$TL_photoStrippedSize = new TLRPC$TL_photoStrippedSize();
            imageLocation.photoSize = tLRPC$TL_photoStrippedSize;
            tLRPC$TL_photoStrippedSize.type = "s";
            tLRPC$TL_photoStrippedSize.bytes = tLRPC$User.photo.stripped_thumb;
            return imageLocation;
        } else {
            TLRPC$FileLocation tLRPC$FileLocation = i == 0 ? tLRPC$UserProfilePhoto.photo_big : tLRPC$UserProfilePhoto.photo_small;
            if (tLRPC$FileLocation == null) {
                return null;
            }
            TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
            tLRPC$TL_inputPeerUser.user_id = tLRPC$User.id;
            tLRPC$TL_inputPeerUser.access_hash = tLRPC$User.access_hash;
            int i4 = tLRPC$User.photo.dc_id;
            if (i4 == 0) {
                i4 = tLRPC$FileLocation.dc_id;
            }
            ImageLocation forPhoto = getForPhoto(tLRPC$FileLocation, 0, null, null, tLRPC$TL_inputPeerUser, i, i4, null, null);
            forPhoto.photoId = tLRPC$User.photo.photo_id;
            return forPhoto;
        }
    }

    public static ImageLocation getForChat(TLRPC$Chat tLRPC$Chat, int i) {
        TLRPC$ChatPhoto tLRPC$ChatPhoto;
        TLRPC$InputPeer tLRPC$TL_inputPeerChat;
        if (tLRPC$Chat == null || (tLRPC$ChatPhoto = tLRPC$Chat.photo) == null) {
            return null;
        }
        if (i == 2) {
            if (tLRPC$ChatPhoto.stripped_thumb == null) {
                return null;
            }
            ImageLocation imageLocation = new ImageLocation();
            TLRPC$TL_photoStrippedSize tLRPC$TL_photoStrippedSize = new TLRPC$TL_photoStrippedSize();
            imageLocation.photoSize = tLRPC$TL_photoStrippedSize;
            tLRPC$TL_photoStrippedSize.type = "s";
            tLRPC$TL_photoStrippedSize.bytes = tLRPC$Chat.photo.stripped_thumb;
            return imageLocation;
        }
        TLRPC$FileLocation tLRPC$FileLocation = i == 0 ? tLRPC$ChatPhoto.photo_big : tLRPC$ChatPhoto.photo_small;
        if (tLRPC$FileLocation == null) {
            return null;
        }
        if (ChatObject.isChannel(tLRPC$Chat)) {
            if (tLRPC$Chat.access_hash == 0) {
                return null;
            }
            tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChannel();
            tLRPC$TL_inputPeerChat.channel_id = tLRPC$Chat.id;
            tLRPC$TL_inputPeerChat.access_hash = tLRPC$Chat.access_hash;
        } else {
            tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
            tLRPC$TL_inputPeerChat.chat_id = tLRPC$Chat.id;
        }
        TLRPC$InputPeer tLRPC$InputPeer = tLRPC$TL_inputPeerChat;
        int i2 = tLRPC$Chat.photo.dc_id;
        if (i2 == 0) {
            i2 = tLRPC$FileLocation.dc_id;
        }
        ImageLocation forPhoto = getForPhoto(tLRPC$FileLocation, 0, null, null, tLRPC$InputPeer, i, i2, null, null);
        forPhoto.photoId = tLRPC$Chat.photo.photo_id;
        return forPhoto;
    }

    public static ImageLocation getForSticker(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$Document tLRPC$Document, int i) {
        TLRPC$InputStickerSet inputStickerSet;
        if ((tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) || (tLRPC$PhotoSize instanceof TLRPC$TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = tLRPC$PhotoSize;
            return imageLocation;
        } else if (tLRPC$PhotoSize == null || tLRPC$Document == null || (inputStickerSet = MediaDataController.getInputStickerSet(tLRPC$Document)) == null) {
            return null;
        } else {
            ImageLocation forPhoto = getForPhoto(tLRPC$PhotoSize.location, tLRPC$PhotoSize.size, null, null, null, 1, tLRPC$Document.dc_id, inputStickerSet, tLRPC$PhotoSize.type);
            if (MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                forPhoto.imageType = 1;
            }
            forPhoto.thumbVersion = i;
            return forPhoto;
        }
    }

    public static ImageLocation getForDocument(TLRPC$VideoSize tLRPC$VideoSize, TLRPC$Document tLRPC$Document) {
        if (tLRPC$VideoSize == null || tLRPC$Document == null) {
            return null;
        }
        ImageLocation forPhoto = getForPhoto(tLRPC$VideoSize.location, tLRPC$VideoSize.size, null, tLRPC$Document, null, 1, tLRPC$Document.dc_id, null, tLRPC$VideoSize.type);
        if ("f".equals(tLRPC$VideoSize.type)) {
            forPhoto.imageType = 1;
        } else {
            forPhoto.imageType = 2;
        }
        return forPhoto;
    }

    public static ImageLocation getForPhoto(TLRPC$VideoSize tLRPC$VideoSize, TLRPC$Photo tLRPC$Photo) {
        if (tLRPC$VideoSize == null || tLRPC$Photo == null) {
            return null;
        }
        ImageLocation forPhoto = getForPhoto(tLRPC$VideoSize.location, tLRPC$VideoSize.size, tLRPC$Photo, null, null, 1, tLRPC$Photo.dc_id, null, tLRPC$VideoSize.type);
        forPhoto.imageType = 2;
        if ((tLRPC$VideoSize.flags & 1) != 0) {
            forPhoto.videoSeekTo = (int) (tLRPC$VideoSize.video_start_ts * 1000.0d);
        }
        return forPhoto;
    }

    public static ImageLocation getForDocument(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$Document tLRPC$Document) {
        if ((tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) || (tLRPC$PhotoSize instanceof TLRPC$TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = tLRPC$PhotoSize;
            return imageLocation;
        } else if (tLRPC$PhotoSize != null && tLRPC$Document != null) {
            return getForPhoto(tLRPC$PhotoSize.location, tLRPC$PhotoSize.size, null, tLRPC$Document, null, 1, tLRPC$Document.dc_id, null, tLRPC$PhotoSize.type);
        } else {
            return null;
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

    private static ImageLocation getForPhoto(TLRPC$FileLocation tLRPC$FileLocation, int i, TLRPC$Photo tLRPC$Photo, TLRPC$Document tLRPC$Document, TLRPC$InputPeer tLRPC$InputPeer, int i2, int i3, TLRPC$InputStickerSet tLRPC$InputStickerSet, String str) {
        if (tLRPC$FileLocation != null) {
            if (tLRPC$Photo == null && tLRPC$InputPeer == null && tLRPC$InputStickerSet == null && tLRPC$Document == null) {
                return null;
            }
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.dc_id = i3;
            imageLocation.photo = tLRPC$Photo;
            imageLocation.currentSize = i;
            imageLocation.photoPeer = tLRPC$InputPeer;
            imageLocation.photoPeerType = i2;
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
        return null;
    }

    public static String getStrippedKey(Object obj, Object obj2, Object obj3) {
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
        if ((tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) || (tLRPC$PhotoSize instanceof TLRPC$TL_photoPathSize)) {
            if (tLRPC$PhotoSize.bytes.length <= 0) {
                return null;
            }
            return getStrippedKey(obj, obj2, tLRPC$PhotoSize);
        } else if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id;
        } else {
            WebFile webFile = this.webFile;
            if (webFile != null) {
                return Utilities.MD5(webFile.url);
            }
            TLRPC$Document tLRPC$Document = this.document;
            if (tLRPC$Document != null) {
                if (!z && (tLRPC$Document instanceof DocumentObject.ThemeDocument)) {
                    DocumentObject.ThemeDocument themeDocument = (DocumentObject.ThemeDocument) tLRPC$Document;
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.document.dc_id);
                    sb.append("_");
                    sb.append(this.document.id);
                    sb.append("_");
                    sb.append(Theme.getBaseThemeKey(themeDocument.themeSettings));
                    sb.append("_");
                    sb.append(themeDocument.themeSettings.accent_color);
                    sb.append("_");
                    int i = 0;
                    sb.append(themeDocument.themeSettings.message_colors.size() > 1 ? themeDocument.themeSettings.message_colors.get(1).intValue() : 0);
                    sb.append("_");
                    if (themeDocument.themeSettings.message_colors.size() > 0) {
                        i = themeDocument.themeSettings.message_colors.get(0).intValue();
                    }
                    sb.append(i);
                    return sb.toString();
                } else if (tLRPC$Document.id == 0 || tLRPC$Document.dc_id == 0) {
                    return null;
                } else {
                    return this.document.dc_id + "_" + this.document.id;
                }
            }
            String str = this.path;
            if (str == null) {
                return null;
            }
            return Utilities.MD5(str);
        }
    }

    public boolean isEncrypted() {
        return this.key != null;
    }

    public long getSize() {
        int i;
        TLRPC$PhotoSize tLRPC$PhotoSize = this.photoSize;
        if (tLRPC$PhotoSize != null) {
            i = tLRPC$PhotoSize.size;
        } else {
            SecureDocument secureDocument = this.secureDocument;
            if (secureDocument != null) {
                TLRPC$TL_secureFile tLRPC$TL_secureFile = secureDocument.secureFile;
                if (tLRPC$TL_secureFile != null) {
                    return tLRPC$TL_secureFile.size;
                }
            } else {
                TLRPC$Document tLRPC$Document = this.document;
                if (tLRPC$Document != null) {
                    return tLRPC$Document.size;
                }
                WebFile webFile = this.webFile;
                if (webFile != null) {
                    i = webFile.size;
                }
            }
            return this.currentSize;
        }
        return i;
    }
}
