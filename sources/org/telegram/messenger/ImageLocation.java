package org.telegram.messenger;

import org.telegram.messenger.DocumentObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class ImageLocation {
    public static final int TYPE_BIG = 0;
    public static final int TYPE_SMALL = 1;
    public static final int TYPE_STRIPPED = 2;
    public static final int TYPE_VIDEO_THUMB = 3;
    public long access_hash;
    public long currentSize;
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
    public int photoPeerType;
    public TLRPC.PhotoSize photoSize;
    public SecureDocument secureDocument;
    public TLRPC.InputStickerSet stickerSet;
    public String thumbSize;
    public int thumbVersion;
    public long videoSeekTo;
    public WebFile webFile;

    public static ImageLocation getForPath(String path2) {
        if (path2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.path = path2;
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
        imageLocation.currentSize = (long) webFile2.size;
        return imageLocation;
    }

    public static ImageLocation getForObject(TLRPC.PhotoSize photoSize2, TLObject object) {
        if (object instanceof TLRPC.Photo) {
            return getForPhoto(photoSize2, (TLRPC.Photo) object);
        }
        if (object instanceof TLRPC.Document) {
            return getForDocument(photoSize2, (TLRPC.Document) object);
        }
        return null;
    }

    public static ImageLocation getForPhoto(TLRPC.PhotoSize photoSize2, TLRPC.Photo photo2) {
        int dc_id2;
        if ((photoSize2 instanceof TLRPC.TL_photoStrippedSize) || (photoSize2 instanceof TLRPC.TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize2;
            return imageLocation;
        } else if (photoSize2 == null || photo2 == null) {
            return null;
        } else {
            if (photo2.dc_id != 0) {
                dc_id2 = photo2.dc_id;
            } else {
                dc_id2 = photoSize2.location.dc_id;
            }
            return getForPhoto(photoSize2.location, photoSize2.size, photo2, (TLRPC.Document) null, (TLRPC.InputPeer) null, 1, dc_id2, (TLRPC.InputStickerSet) null, photoSize2.type);
        }
    }

    public static ImageLocation getForUserOrChat(TLObject object, int type) {
        if (object instanceof TLRPC.User) {
            return getForUser((TLRPC.User) object, type);
        }
        if (object instanceof TLRPC.Chat) {
            return getForChat((TLRPC.Chat) object, type);
        }
        return null;
    }

    public static ImageLocation getForUser(TLRPC.User user, int type) {
        int dc_id2;
        TLRPC.UserFull userFull;
        if (user == null || user.access_hash == 0 || user.photo == null) {
            return null;
        }
        if (type == 3) {
            int currentAccount = UserConfig.selectedAccount;
            if (!MessagesController.getInstance(currentAccount).isPremiumUser(user) || !user.photo.has_video || (userFull = MessagesController.getInstance(currentAccount).getUserFull(user.id)) == null || userFull.profile_photo == null || userFull.profile_photo.video_sizes == null || userFull.profile_photo.video_sizes.isEmpty()) {
                return null;
            }
            TLRPC.VideoSize videoSize = userFull.profile_photo.video_sizes.get(0);
            int i = 0;
            while (true) {
                if (i >= userFull.profile_photo.video_sizes.size()) {
                    break;
                } else if ("p".equals(userFull.profile_photo.video_sizes.get(i).type)) {
                    videoSize = userFull.profile_photo.video_sizes.get(i);
                    break;
                } else {
                    i++;
                }
            }
            return getForPhoto(videoSize, userFull.profile_photo);
        } else if (type != 2) {
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            TLRPC.FileLocation fileLocation = type == 0 ? userProfilePhoto.photo_big : userProfilePhoto.photo_small;
            if (fileLocation == null) {
                return null;
            }
            TLRPC.TL_inputPeerUser inputPeer = new TLRPC.TL_inputPeerUser();
            inputPeer.user_id = user.id;
            inputPeer.access_hash = user.access_hash;
            if (user.photo.dc_id != 0) {
                dc_id2 = user.photo.dc_id;
            } else {
                dc_id2 = fileLocation.dc_id;
            }
            ImageLocation location2 = getForPhoto(fileLocation, 0, (TLRPC.Photo) null, (TLRPC.Document) null, inputPeer, type, dc_id2, (TLRPC.InputStickerSet) null, (String) null);
            location2.photoId = user.photo.photo_id;
            return location2;
        } else if (user.photo.stripped_thumb == null) {
            return null;
        } else {
            ImageLocation imageLocation = new ImageLocation();
            TLRPC.TL_photoStrippedSize tL_photoStrippedSize = new TLRPC.TL_photoStrippedSize();
            imageLocation.photoSize = tL_photoStrippedSize;
            tL_photoStrippedSize.type = "s";
            imageLocation.photoSize.bytes = user.photo.stripped_thumb;
            return imageLocation;
        }
    }

    public static ImageLocation getForChat(TLRPC.Chat chat, int type) {
        TLRPC.InputPeer inputPeer;
        int dc_id2;
        if (chat == null || chat.photo == null) {
            return null;
        }
        if (type != 2) {
            TLRPC.ChatPhoto chatPhoto = chat.photo;
            TLRPC.FileLocation fileLocation = type == 0 ? chatPhoto.photo_big : chatPhoto.photo_small;
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
            if (chat.photo.dc_id != 0) {
                dc_id2 = chat.photo.dc_id;
            } else {
                dc_id2 = fileLocation.dc_id;
            }
            ImageLocation location2 = getForPhoto(fileLocation, 0, (TLRPC.Photo) null, (TLRPC.Document) null, inputPeer, type, dc_id2, (TLRPC.InputStickerSet) null, (String) null);
            location2.photoId = chat.photo.photo_id;
            return location2;
        } else if (chat.photo.stripped_thumb == null) {
            return null;
        } else {
            ImageLocation imageLocation = new ImageLocation();
            TLRPC.TL_photoStrippedSize tL_photoStrippedSize = new TLRPC.TL_photoStrippedSize();
            imageLocation.photoSize = tL_photoStrippedSize;
            tL_photoStrippedSize.type = "s";
            imageLocation.photoSize.bytes = chat.photo.stripped_thumb;
            return imageLocation;
        }
    }

    public static ImageLocation getForSticker(TLRPC.PhotoSize photoSize2, TLRPC.Document sticker, int thumbVersion2) {
        TLRPC.InputStickerSet stickerSet2;
        if ((photoSize2 instanceof TLRPC.TL_photoStrippedSize) || (photoSize2 instanceof TLRPC.TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize2;
            return imageLocation;
        } else if (photoSize2 == null || sticker == null || (stickerSet2 = MediaDataController.getInputStickerSet(sticker)) == null) {
            return null;
        } else {
            ImageLocation imageLocation2 = getForPhoto(photoSize2.location, photoSize2.size, (TLRPC.Photo) null, (TLRPC.Document) null, (TLRPC.InputPeer) null, 1, sticker.dc_id, stickerSet2, photoSize2.type);
            if (MessageObject.isAnimatedStickerDocument(sticker, true)) {
                imageLocation2.imageType = 1;
            }
            imageLocation2.thumbVersion = thumbVersion2;
            return imageLocation2;
        }
    }

    public static ImageLocation getForDocument(TLRPC.VideoSize videoSize, TLRPC.Document document2) {
        if (videoSize == null || document2 == null) {
            return null;
        }
        ImageLocation location2 = getForPhoto(videoSize.location, videoSize.size, (TLRPC.Photo) null, document2, (TLRPC.InputPeer) null, 1, document2.dc_id, (TLRPC.InputStickerSet) null, videoSize.type);
        if ("f".equals(videoSize.type)) {
            location2.imageType = 1;
        } else {
            location2.imageType = 2;
        }
        return location2;
    }

    public static ImageLocation getForPhoto(TLRPC.VideoSize videoSize, TLRPC.Photo photo2) {
        if (videoSize == null || photo2 == null) {
            return null;
        }
        ImageLocation location2 = getForPhoto(videoSize.location, videoSize.size, photo2, (TLRPC.Document) null, (TLRPC.InputPeer) null, 1, photo2.dc_id, (TLRPC.InputStickerSet) null, videoSize.type);
        location2.imageType = 2;
        if ((videoSize.flags & 1) != 0) {
            location2.videoSeekTo = (long) ((int) (videoSize.video_start_ts * 1000.0d));
        }
        return location2;
    }

    public static ImageLocation getForDocument(TLRPC.PhotoSize photoSize2, TLRPC.Document document2) {
        if ((photoSize2 instanceof TLRPC.TL_photoStrippedSize) || (photoSize2 instanceof TLRPC.TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize2;
            return imageLocation;
        } else if (photoSize2 == null || document2 == null) {
            return null;
        } else {
            return getForPhoto(photoSize2.location, photoSize2.size, (TLRPC.Photo) null, document2, (TLRPC.InputPeer) null, 1, document2.dc_id, (TLRPC.InputStickerSet) null, photoSize2.type);
        }
    }

    public static ImageLocation getForLocal(TLRPC.FileLocation location2) {
        if (location2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = new TLRPC.TL_fileLocationToBeDeprecated();
        imageLocation.location = tL_fileLocationToBeDeprecated;
        tL_fileLocationToBeDeprecated.local_id = location2.local_id;
        imageLocation.location.volume_id = location2.volume_id;
        imageLocation.location.secret = location2.secret;
        imageLocation.location.dc_id = location2.dc_id;
        return imageLocation;
    }

    private static ImageLocation getForPhoto(TLRPC.FileLocation location2, int size, TLRPC.Photo photo2, TLRPC.Document document2, TLRPC.InputPeer photoPeer2, int photoPeerType2, int dc_id2, TLRPC.InputStickerSet stickerSet2, String thumbSize2) {
        if (location2 == null) {
            return null;
        }
        if (photo2 == null && photoPeer2 == null && stickerSet2 == null && document2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.dc_id = dc_id2;
        imageLocation.photo = photo2;
        imageLocation.currentSize = (long) size;
        imageLocation.photoPeer = photoPeer2;
        imageLocation.photoPeerType = photoPeerType2;
        imageLocation.stickerSet = stickerSet2;
        if (location2 instanceof TLRPC.TL_fileLocationToBeDeprecated) {
            imageLocation.location = (TLRPC.TL_fileLocationToBeDeprecated) location2;
            if (photo2 != null) {
                imageLocation.file_reference = photo2.file_reference;
                imageLocation.access_hash = photo2.access_hash;
                imageLocation.photoId = photo2.id;
                imageLocation.thumbSize = thumbSize2;
            } else if (document2 != null) {
                imageLocation.file_reference = document2.file_reference;
                imageLocation.access_hash = document2.access_hash;
                imageLocation.documentId = document2.id;
                imageLocation.thumbSize = thumbSize2;
            }
        } else {
            TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = new TLRPC.TL_fileLocationToBeDeprecated();
            imageLocation.location = tL_fileLocationToBeDeprecated;
            tL_fileLocationToBeDeprecated.local_id = location2.local_id;
            imageLocation.location.volume_id = location2.volume_id;
            imageLocation.location.secret = location2.secret;
            imageLocation.dc_id = location2.dc_id;
            imageLocation.file_reference = location2.file_reference;
            imageLocation.key = location2.key;
            imageLocation.iv = location2.iv;
            imageLocation.access_hash = location2.secret;
        }
        return imageLocation;
    }

    public static String getStrippedKey(Object parentObject, Object fullObject, Object strippedObject) {
        if (parentObject instanceof TLRPC.WebPage) {
            if (fullObject instanceof ImageLocation) {
                ImageLocation imageLocation = (ImageLocation) fullObject;
                if (imageLocation.document != null) {
                    fullObject = imageLocation.document;
                } else if (imageLocation.photoSize != null) {
                    fullObject = imageLocation.photoSize;
                } else if (imageLocation.photo != null) {
                    fullObject = imageLocation.photo;
                }
            }
            if (fullObject == null) {
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + strippedObject;
            } else if (fullObject instanceof TLRPC.Document) {
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + ((TLRPC.Document) fullObject).id;
            } else if (fullObject instanceof TLRPC.Photo) {
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + ((TLRPC.Photo) fullObject).id;
            } else if (fullObject instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize size = (TLRPC.PhotoSize) fullObject;
                if (size.location != null) {
                    return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + size.location.local_id + "_" + size.location.volume_id;
                }
                return "stripped" + FileRefController.getKeyForParentObject(parentObject);
            } else if (fullObject instanceof TLRPC.FileLocation) {
                TLRPC.FileLocation loc = (TLRPC.FileLocation) fullObject;
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + loc.local_id + "_" + loc.volume_id;
            }
        }
        return "stripped" + FileRefController.getKeyForParentObject(parentObject);
    }

    public String getKey(Object parentObject, Object fullObject, boolean url) {
        if (this.secureDocument != null) {
            return this.secureDocument.secureFile.dc_id + "_" + this.secureDocument.secureFile.id;
        }
        TLRPC.PhotoSize photoSize2 = this.photoSize;
        if ((photoSize2 instanceof TLRPC.TL_photoStrippedSize) || (photoSize2 instanceof TLRPC.TL_photoPathSize)) {
            if (photoSize2.bytes.length > 0) {
                return getStrippedKey(parentObject, fullObject, this.photoSize);
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
            } else if (!url && (document2 instanceof DocumentObject.ThemeDocument)) {
                DocumentObject.ThemeDocument themeDocument = (DocumentObject.ThemeDocument) document2;
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
            } else if (document2.id == 0 || this.document.dc_id == 0) {
                return null;
            } else {
                return this.document.dc_id + "_" + this.document.id;
            }
        }
    }

    public boolean isEncrypted() {
        return this.key != null;
    }

    public long getSize() {
        TLRPC.PhotoSize photoSize2 = this.photoSize;
        if (photoSize2 != null) {
            return (long) photoSize2.size;
        }
        SecureDocument secureDocument2 = this.secureDocument;
        if (secureDocument2 == null) {
            TLRPC.Document document2 = this.document;
            if (document2 != null) {
                return document2.size;
            }
            WebFile webFile2 = this.webFile;
            if (webFile2 != null) {
                return (long) webFile2.size;
            }
        } else if (secureDocument2.secureFile != null) {
            return this.secureDocument.secureFile.size;
        }
        return this.currentSize;
    }
}
