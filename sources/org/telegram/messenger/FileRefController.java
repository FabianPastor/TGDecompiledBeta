package org.telegram.messenger;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFileLocation;
import org.telegram.tgnet.TLRPC$InputMedia;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$InputStickeredMedia;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$Page;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_account_getTheme;
import org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC$TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC$TL_channel;
import org.telegram.tgnet.TLRPC$TL_channels_getChannels;
import org.telegram.tgnet.TLRPC$TL_channels_getMessages;
import org.telegram.tgnet.TLRPC$TL_chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument;
import org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_inputTheme;
import org.telegram.tgnet.TLRPC$TL_inputWallPaper;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.tgnet.TLRPC$TL_messages_faveSticker;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getChats;
import org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC$TL_messages_getScheduledMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC$TL_messages_saveGif;
import org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_users_getUsers;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$TL_webPageAttributeTheme;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$WebPage;

public class FileRefController extends BaseController {
    private static volatile FileRefController[] Instance = new FileRefController[3];
    private ArrayList<Waiter> favStickersWaiter = new ArrayList<>();
    private long lastCleanupTime = SystemClock.elapsedRealtime();
    private HashMap<String, ArrayList<Requester>> locationRequester = new HashMap<>();
    private HashMap<TLRPC$TL_messages_sendMultiMedia, Object[]> multiMediaCache = new HashMap<>();
    private HashMap<String, ArrayList<Requester>> parentRequester = new HashMap<>();
    private ArrayList<Waiter> recentStickersWaiter = new ArrayList<>();
    private HashMap<String, CachedResult> responseCache = new HashMap<>();
    private ArrayList<Waiter> savedGifsWaiters = new ArrayList<>();
    private ArrayList<Waiter> wallpaperWaiters = new ArrayList<>();

    static /* synthetic */ void lambda$onUpdateObjectReference$23(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$24(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$25(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    private static class Requester {
        /* access modifiers changed from: private */
        public Object[] args;
        /* access modifiers changed from: private */
        public boolean completed;
        /* access modifiers changed from: private */
        public TLRPC$InputFileLocation location;
        /* access modifiers changed from: private */
        public String locationKey;

        private Requester() {
        }
    }

    private static class CachedResult {
        /* access modifiers changed from: private */
        public long firstQueryTime;
        /* access modifiers changed from: private */
        public long lastQueryTime;
        /* access modifiers changed from: private */
        public TLObject response;

        private CachedResult() {
        }
    }

    private static class Waiter {
        /* access modifiers changed from: private */
        public String locationKey;
        /* access modifiers changed from: private */
        public String parentKey;

        public Waiter(String str, String str2) {
            this.locationKey = str;
            this.parentKey = str2;
        }
    }

    public static FileRefController getInstance(int i) {
        FileRefController fileRefController = Instance[i];
        if (fileRefController == null) {
            synchronized (FileRefController.class) {
                fileRefController = Instance[i];
                if (fileRefController == null) {
                    FileRefController[] fileRefControllerArr = Instance;
                    FileRefController fileRefController2 = new FileRefController(i);
                    fileRefControllerArr[i] = fileRefController2;
                    fileRefController = fileRefController2;
                }
            }
        }
        return fileRefController;
    }

    public FileRefController(int i) {
        super(i);
    }

    public static String getKeyForParentObject(Object obj) {
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            int channelId = messageObject.getChannelId();
            return "message" + messageObject.getRealId() + "_" + channelId + "_" + messageObject.scheduled;
        } else if (obj instanceof TLRPC$Message) {
            TLRPC$Message tLRPC$Message = (TLRPC$Message) obj;
            TLRPC$Peer tLRPC$Peer = tLRPC$Message.to_id;
            int i = tLRPC$Peer != null ? tLRPC$Peer.channel_id : 0;
            return "message" + tLRPC$Message.id + "_" + i;
        } else if (obj instanceof TLRPC$WebPage) {
            return "webpage" + ((TLRPC$WebPage) obj).id;
        } else if (obj instanceof TLRPC$User) {
            return "user" + ((TLRPC$User) obj).id;
        } else if (obj instanceof TLRPC$Chat) {
            return "chat" + ((TLRPC$Chat) obj).id;
        } else if (obj instanceof String) {
            return "str" + ((String) obj);
        } else if (obj instanceof TLRPC$TL_messages_stickerSet) {
            return "set" + ((TLRPC$TL_messages_stickerSet) obj).set.id;
        } else if (obj instanceof TLRPC$StickerSetCovered) {
            return "set" + ((TLRPC$StickerSetCovered) obj).set.id;
        } else if (obj instanceof TLRPC$InputStickerSet) {
            return "set" + ((TLRPC$InputStickerSet) obj).id;
        } else if (obj instanceof TLRPC$TL_wallPaper) {
            return "wallpaper" + ((TLRPC$TL_wallPaper) obj).id;
        } else if (obj instanceof TLRPC$TL_theme) {
            return "theme" + ((TLRPC$TL_theme) obj).id;
        } else if (obj == null) {
            return null;
        } else {
            return "" + obj;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:103:0x033d, code lost:
        if (r3.equals(r2) != false) goto L_0x0341;
     */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x034a  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x035b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestReference(java.lang.Object r10, java.lang.Object... r11) {
        /*
            r9 = this;
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r1 = 0
            if (r0 == 0) goto L_0x0023
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "start loading request reference for parent = "
            r0.append(r2)
            r0.append(r10)
            java.lang.String r2 = " args = "
            r0.append(r2)
            r2 = r11[r1]
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0023:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputSingleMedia
            r2 = 2
            r3 = 1
            java.lang.String r4 = "photo_"
            java.lang.String r5 = "file_"
            if (r0 == 0) goto L_0x0085
            r0 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r0 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r0
            org.telegram.tgnet.TLRPC$InputMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaDocument
            if (r6 == 0) goto L_0x005b
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaDocument) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x005b:
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaPhoto
            if (r5 == 0) goto L_0x0081
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaPhoto) r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r0.id
            long r6 = r4.id
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x0081:
            r9.sendErrorToObject(r11, r1)
            return
        L_0x0085:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia
            if (r0 == 0) goto L_0x00bb
            r0 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r0 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r0
            java.util.ArrayList r10 = (java.util.ArrayList) r10
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia, java.lang.Object[]> r4 = r9.multiMediaCache
            r4.put(r0, r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r11 = r0.multi_media
            int r11 = r11.size()
            r4 = 0
        L_0x009d:
            if (r4 >= r11) goto L_0x00ba
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r5 = r0.multi_media
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r5 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r5
            java.lang.Object r6 = r10.get(r4)
            if (r6 != 0) goto L_0x00ae
            goto L_0x00b7
        L_0x00ae:
            java.lang.Object[] r7 = new java.lang.Object[r2]
            r7[r1] = r5
            r7[r3] = r0
            r9.requestReference(r6, r7)
        L_0x00b7:
            int r4 = r4 + 1
            goto L_0x009d
        L_0x00ba:
            return
        L_0x00bb:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_sendMedia
            if (r0 == 0) goto L_0x0117
            r0 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r0 = (org.telegram.tgnet.TLRPC$TL_messages_sendMedia) r0
            org.telegram.tgnet.TLRPC$InputMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaDocument
            if (r6 == 0) goto L_0x00ed
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaDocument) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x00ed:
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaPhoto
            if (r5 == 0) goto L_0x0113
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaPhoto) r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r0.id
            long r6 = r4.id
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x0113:
            r9.sendErrorToObject(r11, r1)
            return
        L_0x0117:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_editMessage
            if (r0 == 0) goto L_0x0173
            r0 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r0 = (org.telegram.tgnet.TLRPC$TL_messages_editMessage) r0
            org.telegram.tgnet.TLRPC$InputMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaDocument
            if (r6 == 0) goto L_0x0149
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaDocument) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x0149:
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaPhoto
            if (r5 == 0) goto L_0x016f
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaPhoto) r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r0.id
            long r6 = r4.id
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x016f:
            r9.sendErrorToObject(r11, r1)
            return
        L_0x0173:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_saveGif
            if (r0 == 0) goto L_0x019d
            r0 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_messages_saveGif r0 = (org.telegram.tgnet.TLRPC$TL_messages_saveGif) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x019d:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker
            if (r0 == 0) goto L_0x01c7
            r0 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker r0 = (org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x01c7:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_faveSticker
            if (r0 == 0) goto L_0x01f1
            r0 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_messages_faveSticker r0 = (org.telegram.tgnet.TLRPC$TL_messages_faveSticker) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x01f1:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers
            if (r0 == 0) goto L_0x024c
            r0 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers r0 = (org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers) r0
            org.telegram.tgnet.TLRPC$InputStickeredMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument
            if (r6 == 0) goto L_0x0223
            org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument r0 = (org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x0223:
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto
            if (r5 == 0) goto L_0x0248
            org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto r0 = (org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto) r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r0.id
            long r6 = r4.id
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ae
        L_0x0248:
            r9.sendErrorToObject(r11, r1)
            return
        L_0x024c:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputFileLocation
            if (r0 == 0) goto L_0x0275
            r0 = r11[r1]
            r5 = r0
            org.telegram.tgnet.TLRPC$TL_inputFileLocation r5 = (org.telegram.tgnet.TLRPC$TL_inputFileLocation) r5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "loc_"
            r0.append(r4)
            int r4 = r5.local_id
            r0.append(r4)
            java.lang.String r4 = "_"
            r0.append(r4)
            long r6 = r5.volume_id
            r0.append(r6)
            java.lang.String r4 = r0.toString()
            goto L_0x02ae
        L_0x0275:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            if (r0 == 0) goto L_0x0292
            r0 = r11[r1]
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r0 = (org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            long r5 = r0.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r5 = r0
            goto L_0x02ae
        L_0x0292:
            r0 = r11[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            if (r0 == 0) goto L_0x0376
            r0 = r11[r1]
            r5 = r0
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = (org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation) r5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r4)
            long r6 = r5.id
            r0.append(r6)
            java.lang.String r4 = r0.toString()
        L_0x02ae:
            boolean r0 = r10 instanceof org.telegram.messenger.MessageObject
            if (r0 == 0) goto L_0x02c4
            r0 = r10
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r6 = r0.getRealId()
            if (r6 >= 0) goto L_0x02c4
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x02c4
            r10 = r0
        L_0x02c4:
            java.lang.String r0 = getKeyForParentObject(r10)
            if (r0 != 0) goto L_0x02ce
            r9.sendErrorToObject(r11, r1)
            return
        L_0x02ce:
            org.telegram.messenger.FileRefController$Requester r6 = new org.telegram.messenger.FileRefController$Requester
            r7 = 0
            r6.<init>()
            java.lang.Object[] unused = r6.args = r11
            org.telegram.tgnet.TLRPC$InputFileLocation unused = r6.location = r5
            java.lang.String unused = r6.locationKey = r4
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r5 = r9.locationRequester
            java.lang.Object r5 = r5.get(r4)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x02f2
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r7 = r9.locationRequester
            r7.put(r4, r5)
            goto L_0x02f3
        L_0x02f2:
            r3 = 0
        L_0x02f3:
            r5.add(r6)
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r5 = r9.parentRequester
            java.lang.Object r5 = r5.get(r0)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x030c
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r7 = r9.parentRequester
            r7.put(r0, r5)
            int r3 = r3 + 1
        L_0x030c:
            r5.add(r6)
            if (r3 == r2) goto L_0x0312
            return
        L_0x0312:
            boolean r2 = r10 instanceof java.lang.String
            java.lang.String r3 = "fav"
            java.lang.String r5 = "recent"
            java.lang.String r6 = "gif"
            java.lang.String r7 = "wallpaper"
            if (r2 == 0) goto L_0x0340
            r2 = r10
            java.lang.String r2 = (java.lang.String) r2
            boolean r8 = r7.equals(r2)
            if (r8 == 0) goto L_0x0329
            r3 = r7
            goto L_0x0341
        L_0x0329:
            boolean r7 = r2.startsWith(r6)
            if (r7 == 0) goto L_0x0331
            r3 = r6
            goto L_0x0341
        L_0x0331:
            boolean r6 = r5.equals(r2)
            if (r6 == 0) goto L_0x0339
            r3 = r5
            goto L_0x0341
        L_0x0339:
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0340
            goto L_0x0341
        L_0x0340:
            r3 = r4
        L_0x0341:
            r9.cleanupCache()
            org.telegram.messenger.FileRefController$CachedResult r2 = r9.getCachedResponse(r3)
            if (r2 == 0) goto L_0x035b
            org.telegram.tgnet.TLObject r2 = r2.response
            boolean r1 = r9.onRequestComplete(r4, r0, r2, r1)
            if (r1 != 0) goto L_0x035a
            java.util.HashMap<java.lang.String, org.telegram.messenger.FileRefController$CachedResult> r1 = r9.responseCache
            r1.remove(r4)
            goto L_0x0372
        L_0x035a:
            return
        L_0x035b:
            org.telegram.messenger.FileRefController$CachedResult r2 = r9.getCachedResponse(r0)
            if (r2 == 0) goto L_0x0372
            org.telegram.tgnet.TLObject r2 = r2.response
            boolean r1 = r9.onRequestComplete(r4, r0, r2, r1)
            if (r1 != 0) goto L_0x0371
            java.util.HashMap<java.lang.String, org.telegram.messenger.FileRefController$CachedResult> r1 = r9.responseCache
            r1.remove(r0)
            goto L_0x0372
        L_0x0371:
            return
        L_0x0372:
            r9.requestReferenceFromServer(r10, r4, r0, r11)
            return
        L_0x0376:
            r9.sendErrorToObject(r11, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.requestReference(java.lang.Object, java.lang.Object[]):void");
    }

    private void broadcastWaitersData(ArrayList<Waiter> arrayList, TLObject tLObject) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Waiter waiter = arrayList.get(i);
            onRequestComplete(waiter.locationKey, waiter.parentKey, tLObject, i == size + -1);
            i++;
        }
        arrayList.clear();
    }

    private void requestReferenceFromServer(Object obj, String str, String str2, Object[] objArr) {
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            int channelId = messageObject.getChannelId();
            if (messageObject.scheduled) {
                TLRPC$TL_messages_getScheduledMessages tLRPC$TL_messages_getScheduledMessages = new TLRPC$TL_messages_getScheduledMessages();
                tLRPC$TL_messages_getScheduledMessages.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
                tLRPC$TL_messages_getScheduledMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getScheduledMessages, new RequestDelegate(str, str2) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$0$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            } else if (channelId != 0) {
                TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
                tLRPC$TL_channels_getMessages.channel = getMessagesController().getInputChannel(channelId);
                tLRPC$TL_channels_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tLRPC$TL_channels_getMessages, new RequestDelegate(str, str2) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$1$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            } else {
                TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages = new TLRPC$TL_messages_getMessages();
                tLRPC$TL_messages_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages, new RequestDelegate(str, str2) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$2$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            }
        } else if (obj instanceof TLRPC$TL_wallPaper) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj;
            TLRPC$TL_account_getWallPaper tLRPC$TL_account_getWallPaper = new TLRPC$TL_account_getWallPaper();
            TLRPC$TL_inputWallPaper tLRPC$TL_inputWallPaper = new TLRPC$TL_inputWallPaper();
            tLRPC$TL_inputWallPaper.id = tLRPC$TL_wallPaper.id;
            tLRPC$TL_inputWallPaper.access_hash = tLRPC$TL_wallPaper.access_hash;
            tLRPC$TL_account_getWallPaper.wallpaper = tLRPC$TL_inputWallPaper;
            getConnectionsManager().sendRequest(tLRPC$TL_account_getWallPaper, new RequestDelegate(str, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$3$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
        } else if (obj instanceof TLRPC$TL_theme) {
            TLRPC$TL_theme tLRPC$TL_theme = (TLRPC$TL_theme) obj;
            TLRPC$TL_account_getTheme tLRPC$TL_account_getTheme = new TLRPC$TL_account_getTheme();
            TLRPC$TL_inputTheme tLRPC$TL_inputTheme = new TLRPC$TL_inputTheme();
            tLRPC$TL_inputTheme.id = tLRPC$TL_theme.id;
            tLRPC$TL_inputTheme.access_hash = tLRPC$TL_theme.access_hash;
            tLRPC$TL_account_getTheme.theme = tLRPC$TL_inputTheme;
            tLRPC$TL_account_getTheme.format = "android";
            getConnectionsManager().sendRequest(tLRPC$TL_account_getTheme, new RequestDelegate(str, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$4$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
        } else if (obj instanceof TLRPC$WebPage) {
            TLRPC$TL_messages_getWebPage tLRPC$TL_messages_getWebPage = new TLRPC$TL_messages_getWebPage();
            tLRPC$TL_messages_getWebPage.url = ((TLRPC$WebPage) obj).url;
            tLRPC$TL_messages_getWebPage.hash = 0;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getWebPage, new RequestDelegate(str, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$5$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
        } else if (obj instanceof TLRPC$User) {
            TLRPC$TL_users_getUsers tLRPC$TL_users_getUsers = new TLRPC$TL_users_getUsers();
            tLRPC$TL_users_getUsers.id.add(getMessagesController().getInputUser((TLRPC$User) obj));
            getConnectionsManager().sendRequest(tLRPC$TL_users_getUsers, new RequestDelegate(str, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$6$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
        } else if (obj instanceof TLRPC$Chat) {
            TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) obj;
            if (tLRPC$Chat instanceof TLRPC$TL_chat) {
                TLRPC$TL_messages_getChats tLRPC$TL_messages_getChats = new TLRPC$TL_messages_getChats();
                tLRPC$TL_messages_getChats.id.add(Integer.valueOf(tLRPC$Chat.id));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getChats, new RequestDelegate(str, str2) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$7$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            } else if (tLRPC$Chat instanceof TLRPC$TL_channel) {
                TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
                tLRPC$TL_channels_getChannels.id.add(MessagesController.getInputChannel(tLRPC$Chat));
                getConnectionsManager().sendRequest(tLRPC$TL_channels_getChannels, new RequestDelegate(str, str2) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$8$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            }
        } else if (obj instanceof String) {
            String str3 = (String) obj;
            if ("wallpaper".equals(str3)) {
                if (this.wallpaperWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC$TL_account_getWallPapers(), new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$9$FileRefController(tLObject, tLRPC$TL_error);
                        }
                    });
                }
                this.wallpaperWaiters.add(new Waiter(str, str2));
            } else if (str3.startsWith("gif")) {
                if (this.savedGifsWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC$TL_messages_getSavedGifs(), new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$10$FileRefController(tLObject, tLRPC$TL_error);
                        }
                    });
                }
                this.savedGifsWaiters.add(new Waiter(str, str2));
            } else if ("recent".equals(str3)) {
                if (this.recentStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC$TL_messages_getRecentStickers(), new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$11$FileRefController(tLObject, tLRPC$TL_error);
                        }
                    });
                }
                this.recentStickersWaiter.add(new Waiter(str, str2));
            } else if ("fav".equals(str3)) {
                if (this.favStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC$TL_messages_getFavedStickers(), new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$12$FileRefController(tLObject, tLRPC$TL_error);
                        }
                    });
                }
                this.favStickersWaiter.add(new Waiter(str, str2));
            } else if (str3.startsWith("avatar_")) {
                int intValue = Utilities.parseInt(str3).intValue();
                if (intValue > 0) {
                    TLRPC$TL_photos_getUserPhotos tLRPC$TL_photos_getUserPhotos = new TLRPC$TL_photos_getUserPhotos();
                    tLRPC$TL_photos_getUserPhotos.limit = 80;
                    tLRPC$TL_photos_getUserPhotos.offset = 0;
                    tLRPC$TL_photos_getUserPhotos.max_id = 0;
                    tLRPC$TL_photos_getUserPhotos.user_id = getMessagesController().getInputUser(intValue);
                    getConnectionsManager().sendRequest(tLRPC$TL_photos_getUserPhotos, new RequestDelegate(str, str2) {
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ String f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$13$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                        }
                    });
                    return;
                }
                TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
                tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterChatPhotos();
                tLRPC$TL_messages_search.limit = 80;
                tLRPC$TL_messages_search.offset_id = 0;
                tLRPC$TL_messages_search.q = "";
                tLRPC$TL_messages_search.peer = getMessagesController().getInputPeer(intValue);
                getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new RequestDelegate(str, str2) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$14$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            } else if (str3.startsWith("sent_")) {
                String[] split = str3.split("_");
                if (split.length == 3) {
                    int intValue2 = Utilities.parseInt(split[1]).intValue();
                    if (intValue2 != 0) {
                        TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages2 = new TLRPC$TL_channels_getMessages();
                        tLRPC$TL_channels_getMessages2.channel = getMessagesController().getInputChannel(intValue2);
                        tLRPC$TL_channels_getMessages2.id.add(Utilities.parseInt(split[2]));
                        getConnectionsManager().sendRequest(tLRPC$TL_channels_getMessages2, new RequestDelegate(str, str2) {
                            public final /* synthetic */ String f$1;
                            public final /* synthetic */ String f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$15$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                            }
                        });
                        return;
                    }
                    TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages2 = new TLRPC$TL_messages_getMessages();
                    tLRPC$TL_messages_getMessages2.id.add(Utilities.parseInt(split[2]));
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages2, new RequestDelegate(str, str2) {
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ String f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$16$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                        }
                    });
                    return;
                }
                sendErrorToObject(objArr, 0);
            } else {
                sendErrorToObject(objArr, 0);
            }
        } else if (obj instanceof TLRPC$TL_messages_stickerSet) {
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
            TLRPC$StickerSet tLRPC$StickerSet = ((TLRPC$TL_messages_stickerSet) obj).set;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate(str, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$17$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
        } else if (obj instanceof TLRPC$StickerSetCovered) {
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet2 = new TLRPC$TL_messages_getStickerSet();
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID2 = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_messages_getStickerSet2.stickerset = tLRPC$TL_inputStickerSetID2;
            TLRPC$StickerSet tLRPC$StickerSet2 = ((TLRPC$StickerSetCovered) obj).set;
            tLRPC$TL_inputStickerSetID2.id = tLRPC$StickerSet2.id;
            tLRPC$TL_inputStickerSetID2.access_hash = tLRPC$StickerSet2.access_hash;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet2, new RequestDelegate(str, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$18$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
        } else if (obj instanceof TLRPC$InputStickerSet) {
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet3 = new TLRPC$TL_messages_getStickerSet();
            tLRPC$TL_messages_getStickerSet3.stickerset = (TLRPC$InputStickerSet) obj;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet3, new RequestDelegate(str, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$19$FileRefController(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
        } else {
            sendErrorToObject(objArr, 0);
        }
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$0$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$1$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$2$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$3$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$4$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$5$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$6$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$7$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$8$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$9$FileRefController(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        broadcastWaitersData(this.wallpaperWaiters, tLObject);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$10$FileRefController(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        broadcastWaitersData(this.savedGifsWaiters, tLObject);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$11$FileRefController(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        broadcastWaitersData(this.recentStickersWaiter, tLObject);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$12$FileRefController(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        broadcastWaitersData(this.favStickersWaiter, tLObject);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$13$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$14$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$15$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, false);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$16$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, false);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$17$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$18$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$19$FileRefController(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    private void onUpdateObjectReference(Requester requester, byte[] bArr, TLRPC$InputFileLocation tLRPC$InputFileLocation) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("fileref updated for " + requester.args[0] + " " + requester.locationKey);
        }
        boolean z = true;
        if (requester.args[0] instanceof TLRPC$TL_inputSingleMedia) {
            TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = (TLRPC$TL_messages_sendMultiMedia) requester.args[1];
            Object[] objArr = this.multiMediaCache.get(tLRPC$TL_messages_sendMultiMedia);
            if (objArr != null) {
                TLRPC$TL_inputSingleMedia tLRPC$TL_inputSingleMedia = (TLRPC$TL_inputSingleMedia) requester.args[0];
                TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_inputSingleMedia.media;
                if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaDocument) {
                    ((TLRPC$TL_inputMediaDocument) tLRPC$InputMedia).id.file_reference = bArr;
                } else if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaPhoto) {
                    ((TLRPC$TL_inputMediaPhoto) tLRPC$InputMedia).id.file_reference = bArr;
                }
                int indexOf = tLRPC$TL_messages_sendMultiMedia.multi_media.indexOf(tLRPC$TL_inputSingleMedia);
                if (indexOf >= 0) {
                    ArrayList arrayList = (ArrayList) objArr[3];
                    arrayList.set(indexOf, (Object) null);
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i) != null) {
                            z = false;
                        }
                    }
                    if (z) {
                        this.multiMediaCache.remove(tLRPC$TL_messages_sendMultiMedia);
                        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_messages_sendMultiMedia, objArr) {
                            public final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$1;
                            public final /* synthetic */ Object[] f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                FileRefController.this.lambda$onUpdateObjectReference$20$FileRefController(this.f$1, this.f$2);
                            }
                        });
                    }
                }
            }
        } else if (requester.args[0] instanceof TLRPC$TL_messages_sendMedia) {
            TLRPC$InputMedia tLRPC$InputMedia2 = ((TLRPC$TL_messages_sendMedia) requester.args[0]).media;
            if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaDocument) {
                ((TLRPC$TL_inputMediaDocument) tLRPC$InputMedia2).id.file_reference = bArr;
            } else if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaPhoto) {
                ((TLRPC$TL_inputMediaPhoto) tLRPC$InputMedia2).id.file_reference = bArr;
            }
            AndroidUtilities.runOnUIThread(new Runnable(requester) {
                public final /* synthetic */ FileRefController.Requester f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileRefController.this.lambda$onUpdateObjectReference$21$FileRefController(this.f$1);
                }
            });
        } else if (requester.args[0] instanceof TLRPC$TL_messages_editMessage) {
            TLRPC$InputMedia tLRPC$InputMedia3 = ((TLRPC$TL_messages_editMessage) requester.args[0]).media;
            if (tLRPC$InputMedia3 instanceof TLRPC$TL_inputMediaDocument) {
                ((TLRPC$TL_inputMediaDocument) tLRPC$InputMedia3).id.file_reference = bArr;
            } else if (tLRPC$InputMedia3 instanceof TLRPC$TL_inputMediaPhoto) {
                ((TLRPC$TL_inputMediaPhoto) tLRPC$InputMedia3).id.file_reference = bArr;
            }
            AndroidUtilities.runOnUIThread(new Runnable(requester) {
                public final /* synthetic */ FileRefController.Requester f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileRefController.this.lambda$onUpdateObjectReference$22$FileRefController(this.f$1);
                }
            });
        } else if (requester.args[0] instanceof TLRPC$TL_messages_saveGif) {
            TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif = (TLRPC$TL_messages_saveGif) requester.args[0];
            tLRPC$TL_messages_saveGif.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_saveGif, $$Lambda$FileRefController$B8e86bveS3knOZa0Cg1HvRKtz44.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC$TL_messages_saveRecentSticker) {
            TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker = (TLRPC$TL_messages_saveRecentSticker) requester.args[0];
            tLRPC$TL_messages_saveRecentSticker.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_saveRecentSticker, $$Lambda$FileRefController$9VZxhYR0Ao1KW4hoVDK7k1sUZVI.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC$TL_messages_faveSticker) {
            TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker = (TLRPC$TL_messages_faveSticker) requester.args[0];
            tLRPC$TL_messages_faveSticker.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_faveSticker, $$Lambda$FileRefController$9vHgXK5LhN8UmJHe149yk5CGvC8.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC$TL_messages_getAttachedStickers) {
            TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers = (TLRPC$TL_messages_getAttachedStickers) requester.args[0];
            TLRPC$InputStickeredMedia tLRPC$InputStickeredMedia = tLRPC$TL_messages_getAttachedStickers.media;
            if (tLRPC$InputStickeredMedia instanceof TLRPC$TL_inputStickeredMediaDocument) {
                ((TLRPC$TL_inputStickeredMediaDocument) tLRPC$InputStickeredMedia).id.file_reference = bArr;
            } else if (tLRPC$InputStickeredMedia instanceof TLRPC$TL_inputStickeredMediaPhoto) {
                ((TLRPC$TL_inputStickeredMediaPhoto) tLRPC$InputStickeredMedia).id.file_reference = bArr;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getAttachedStickers, (RequestDelegate) requester.args[1]);
        } else if (requester.args[1] instanceof FileLoadOperation) {
            FileLoadOperation fileLoadOperation = (FileLoadOperation) requester.args[1];
            if (tLRPC$InputFileLocation != null) {
                fileLoadOperation.location = tLRPC$InputFileLocation;
            } else {
                requester.location.file_reference = bArr;
            }
            fileLoadOperation.requestingReference = false;
            fileLoadOperation.startDownloadRequest();
        }
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$20$FileRefController(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tLRPC$TL_messages_sendMultiMedia, objArr[1], objArr[2], (ArrayList<Object>) null, objArr[4], objArr[5].booleanValue());
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$21$FileRefController(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, ((Boolean) requester.args[6]).booleanValue());
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$22$FileRefController(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, ((Boolean) requester.args[6]).booleanValue());
    }

    private void sendErrorToObject(Object[] objArr, int i) {
        if (objArr[0] instanceof TLRPC$TL_inputSingleMedia) {
            TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = objArr[1];
            Object[] objArr2 = this.multiMediaCache.get(tLRPC$TL_messages_sendMultiMedia);
            if (objArr2 != null) {
                this.multiMediaCache.remove(tLRPC$TL_messages_sendMultiMedia);
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_messages_sendMultiMedia, objArr2) {
                    public final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$1;
                    public final /* synthetic */ Object[] f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        FileRefController.this.lambda$sendErrorToObject$26$FileRefController(this.f$1, this.f$2);
                    }
                });
            }
        } else if ((objArr[0] instanceof TLRPC$TL_messages_sendMedia) || (objArr[0] instanceof TLRPC$TL_messages_editMessage)) {
            AndroidUtilities.runOnUIThread(new Runnable(objArr) {
                public final /* synthetic */ Object[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileRefController.this.lambda$sendErrorToObject$27$FileRefController(this.f$1);
                }
            });
        } else if (objArr[0] instanceof TLRPC$TL_messages_saveGif) {
            TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif = objArr[0];
        } else if (objArr[0] instanceof TLRPC$TL_messages_saveRecentSticker) {
            TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker = objArr[0];
        } else if (objArr[0] instanceof TLRPC$TL_messages_faveSticker) {
            TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker = objArr[0];
        } else if (objArr[0] instanceof TLRPC$TL_messages_getAttachedStickers) {
            getConnectionsManager().sendRequest(objArr[0], objArr[1]);
        } else if (i == 0) {
            TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
            tLRPC$TL_error.text = "not found parent object to request reference";
            tLRPC$TL_error.code = 400;
            if (objArr[1] instanceof FileLoadOperation) {
                FileLoadOperation fileLoadOperation = objArr[1];
                fileLoadOperation.requestingReference = false;
                fileLoadOperation.processRequestResult(objArr[2], tLRPC$TL_error);
            }
        } else if (i == 1 && (objArr[1] instanceof FileLoadOperation)) {
            FileLoadOperation fileLoadOperation2 = objArr[1];
            fileLoadOperation2.requestingReference = false;
            fileLoadOperation2.onFail(false, 0);
        }
    }

    public /* synthetic */ void lambda$sendErrorToObject$26$FileRefController(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tLRPC$TL_messages_sendMultiMedia, objArr[1], objArr[2], (ArrayList<Object>) null, objArr[4], objArr[5].booleanValue());
    }

    public /* synthetic */ void lambda$sendErrorToObject$27$FileRefController(Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequest(objArr[0], objArr[1], objArr[2], objArr[3], objArr[4].booleanValue(), objArr[5], (Object) null, objArr[6].booleanValue());
    }

    /* JADX WARNING: type inference failed for: r8v0 */
    /* JADX WARNING: type inference failed for: r8v2 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r8v1, types: [boolean, int] */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0421  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x042e  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0126 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x016b A[LOOP:2: B:50:0x00b9->B:87:0x016b, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean onRequestComplete(java.lang.String r26, java.lang.String r27, org.telegram.tgnet.TLObject r28, boolean r29) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            r2 = r27
            r3 = r28
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_account_wallPapers
            if (r4 == 0) goto L_0x000f
            java.lang.String r5 = "wallpaper"
            goto L_0x0025
        L_0x000f:
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_savedGifs
            if (r5 == 0) goto L_0x0016
            java.lang.String r5 = "gif"
            goto L_0x0025
        L_0x0016:
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_recentStickers
            if (r5 == 0) goto L_0x001d
            java.lang.String r5 = "recent"
            goto L_0x0025
        L_0x001d:
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_favedStickers
            if (r5 == 0) goto L_0x0024
            java.lang.String r5 = "fav"
            goto L_0x0025
        L_0x0024:
            r5 = r2
        L_0x0025:
            r6 = 0
            r8 = 1
            if (r2 == 0) goto L_0x0068
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r9 = r0.parentRequester
            java.lang.Object r9 = r9.get(r2)
            java.util.ArrayList r9 = (java.util.ArrayList) r9
            if (r9 == 0) goto L_0x0068
            int r10 = r9.size()
            r11 = 0
            r12 = 0
        L_0x0039:
            if (r11 >= r10) goto L_0x005d
            java.lang.Object r13 = r9.get(r11)
            org.telegram.messenger.FileRefController$Requester r13 = (org.telegram.messenger.FileRefController.Requester) r13
            boolean r14 = r13.completed
            if (r14 == 0) goto L_0x0048
            goto L_0x005a
        L_0x0048:
            java.lang.String r13 = r13.locationKey
            if (r29 == 0) goto L_0x0052
            if (r12 != 0) goto L_0x0052
            r14 = 1
            goto L_0x0053
        L_0x0052:
            r14 = 0
        L_0x0053:
            boolean r13 = r0.onRequestComplete(r13, r6, r3, r14)
            if (r13 == 0) goto L_0x005a
            r12 = 1
        L_0x005a:
            int r11 = r11 + 1
            goto L_0x0039
        L_0x005d:
            if (r12 == 0) goto L_0x0062
            r0.putReponseToCache(r5, r3)
        L_0x0062:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r5 = r0.parentRequester
            r5.remove(r2)
            goto L_0x0069
        L_0x0068:
            r12 = 0
        L_0x0069:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r0.locationRequester
            java.lang.Object r2 = r2.get(r1)
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            if (r2 != 0) goto L_0x0074
            return r12
        L_0x0074:
            int r5 = r2.size()
            r10 = r6
            r11 = r10
            r13 = r11
            r9 = 0
        L_0x007c:
            if (r9 >= r5) goto L_0x0442
            java.lang.Object r14 = r2.get(r9)
            org.telegram.messenger.FileRefController$Requester r14 = (org.telegram.messenger.FileRefController.Requester) r14
            boolean r15 = r14.completed
            if (r15 == 0) goto L_0x0094
            r27 = r2
            r18 = r4
            r16 = r5
            r2 = 0
            r5 = 1
            goto L_0x0437
        L_0x0094:
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_inputFileLocation
            if (r15 == 0) goto L_0x00a0
            org.telegram.tgnet.TLRPC$InputFileLocation[] r11 = new org.telegram.tgnet.TLRPC$InputFileLocation[r8]
            boolean[] r10 = new boolean[r8]
        L_0x00a0:
            boolean unused = r14.completed = r8
            boolean r15 = r3 instanceof org.telegram.tgnet.TLRPC$messages_Messages
            if (r15 == 0) goto L_0x019e
            r15 = r3
            org.telegram.tgnet.TLRPC$messages_Messages r15 = (org.telegram.tgnet.TLRPC$messages_Messages) r15
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r6 = r15.messages
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0199
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r6 = r15.messages
            int r6 = r6.size()
            r8 = 0
        L_0x00b9:
            if (r8 >= r6) goto L_0x0174
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r7 = r15.messages
            java.lang.Object r7 = r7.get(r8)
            org.telegram.tgnet.TLRPC$Message r7 = (org.telegram.tgnet.TLRPC$Message) r7
            r27 = r2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            if (r2 == 0) goto L_0x0111
            r16 = r5
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            if (r5 == 0) goto L_0x00d8
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r14.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r2, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            goto L_0x0123
        L_0x00d8:
            org.telegram.tgnet.TLRPC$TL_game r5 = r2.game
            if (r5 == 0) goto L_0x00f7
            org.telegram.tgnet.TLRPC$Document r2 = r5.document
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r14.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r2 != 0) goto L_0x0123
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r14.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Photo) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            goto L_0x0123
        L_0x00f7:
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            if (r5 == 0) goto L_0x0104
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r14.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Photo) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r2, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            goto L_0x0123
        L_0x0104:
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            if (r2 == 0) goto L_0x0124
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r14.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$WebPage) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            goto L_0x0123
        L_0x0111:
            r16 = r5
            org.telegram.tgnet.TLRPC$MessageAction r2 = r7.action
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r5 == 0) goto L_0x0124
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r14.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Photo) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
        L_0x0123:
            r13 = r2
        L_0x0124:
            if (r13 == 0) goto L_0x016b
            if (r29 == 0) goto L_0x0169
            org.telegram.tgnet.TLRPC$Peer r2 = r7.to_id
            if (r2 == 0) goto L_0x015c
            int r2 = r2.channel_id
            if (r2 == 0) goto L_0x015c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r15.chats
            int r2 = r2.size()
            r5 = 0
        L_0x0137:
            if (r5 >= r2) goto L_0x015c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r15.chats
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Chat r6 = (org.telegram.tgnet.TLRPC$Chat) r6
            int r8 = r6.id
            r17 = r2
            org.telegram.tgnet.TLRPC$Peer r2 = r7.to_id
            int r2 = r2.channel_id
            if (r8 != r2) goto L_0x0157
            boolean r2 = r6.megagroup
            if (r2 == 0) goto L_0x015c
            int r2 = r7.flags
            r5 = -2147483648(0xfffffffvar_, float:-0.0)
            r2 = r2 | r5
            r7.flags = r2
            goto L_0x015c
        L_0x0157:
            int r5 = r5 + 1
            r2 = r17
            goto L_0x0137
        L_0x015c:
            org.telegram.messenger.MessagesStorage r2 = r25.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r15.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r15.chats
            r8 = 0
            r2.replaceMessageIfExists(r7, r5, r6, r8)
            goto L_0x0178
        L_0x0169:
            r8 = 0
            goto L_0x0178
        L_0x016b:
            r2 = 0
            int r8 = r8 + 1
            r2 = r27
            r5 = r16
            goto L_0x00b9
        L_0x0174:
            r27 = r2
            r16 = r5
        L_0x0178:
            r2 = 0
            if (r13 != 0) goto L_0x01b2
            org.telegram.messenger.MessagesStorage r5 = r25.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r6 = r15.messages
            java.lang.Object r6 = r6.get(r2)
            org.telegram.tgnet.TLRPC$Message r6 = (org.telegram.tgnet.TLRPC$Message) r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r15.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r7 = r15.chats
            r8 = 1
            r5.replaceMessageIfExists(r6, r2, r7, r8)
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r2 == 0) goto L_0x01b2
            java.lang.String r2 = "file ref not found in messages, replacing message"
            org.telegram.messenger.FileLog.d(r2)
            goto L_0x01b2
        L_0x0199:
            r27 = r2
            r16 = r5
            goto L_0x01b2
        L_0x019e:
            r27 = r2
            r16 = r5
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$WebPage
            if (r2 == 0) goto L_0x01b7
            r2 = r3
            org.telegram.tgnet.TLRPC$WebPage r2 = (org.telegram.tgnet.TLRPC$WebPage) r2
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r14.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$WebPage) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            r13 = r2
        L_0x01b2:
            r18 = r4
        L_0x01b4:
            r6 = 0
            goto L_0x041f
        L_0x01b7:
            if (r4 == 0) goto L_0x01ec
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_account_wallPapers r2 = (org.telegram.tgnet.TLRPC$TL_account_wallPapers) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r5 = r2.wallpapers
            int r5 = r5.size()
            r6 = 0
        L_0x01c3:
            if (r6 >= r5) goto L_0x01dd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r7 = r2.wallpapers
            java.lang.Object r7 = r7.get(r6)
            org.telegram.tgnet.TLRPC$TL_wallPaper r7 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r7
            org.telegram.tgnet.TLRPC$Document r7 = r7.document
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r13 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r7, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r13 == 0) goto L_0x01da
            goto L_0x01dd
        L_0x01da:
            int r6 = r6 + 1
            goto L_0x01c3
        L_0x01dd:
            if (r13 == 0) goto L_0x01b2
            if (r29 == 0) goto L_0x01b2
            org.telegram.messenger.MessagesStorage r5 = r25.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r2 = r2.wallpapers
            r6 = 1
            r5.putWallpapers(r2, r6)
            goto L_0x01b2
        L_0x01ec:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r2 == 0) goto L_0x0215
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r2
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r14.location
            byte[] r5 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r6, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r5 == 0) goto L_0x0211
            if (r29 == 0) goto L_0x0211
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r6.add(r2)
            org.telegram.messenger.MessagesStorage r2 = r25.getMessagesStorage()
            r7 = 0
            r2.putWallpapers(r6, r7)
        L_0x0211:
            r18 = r4
            r13 = r5
            goto L_0x01b4
        L_0x0215:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_theme
            if (r2 == 0) goto L_0x0233
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_theme r2 = (org.telegram.tgnet.TLRPC$TL_theme) r2
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r14.location
            byte[] r5 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r6, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r5 == 0) goto L_0x0211
            if (r29 == 0) goto L_0x0211
            org.telegram.messenger.-$$Lambda$FileRefController$SNJwoq0kr06ZjWyihuJvKSpuIz0 r6 = new org.telegram.messenger.-$$Lambda$FileRefController$SNJwoq0kr06ZjWyihuJvKSpuIz0
            r6.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)
            goto L_0x0211
        L_0x0233:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$Vector
            if (r2 == 0) goto L_0x02c2
            r2 = r3
            org.telegram.tgnet.TLRPC$Vector r2 = (org.telegram.tgnet.TLRPC$Vector) r2
            java.util.ArrayList<java.lang.Object> r5 = r2.objects
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x01b2
            java.util.ArrayList<java.lang.Object> r5 = r2.objects
            int r5 = r5.size()
            r8 = 0
        L_0x0249:
            if (r8 >= r5) goto L_0x01b2
            java.util.ArrayList<java.lang.Object> r6 = r2.objects
            java.lang.Object r6 = r6.get(r8)
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$User
            if (r7 == 0) goto L_0x0287
            org.telegram.tgnet.TLRPC$User r6 = (org.telegram.tgnet.TLRPC$User) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r7 = r0.getFileReference((org.telegram.tgnet.TLRPC$User) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r7, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r29 == 0) goto L_0x0281
            if (r7 == 0) goto L_0x0281
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            r13.add(r6)
            org.telegram.messenger.MessagesStorage r15 = r25.getMessagesStorage()
            r17 = r2
            r18 = r4
            r2 = 0
            r4 = 1
            r15.putUsersAndChats(r13, r2, r4, r4)
            org.telegram.messenger.-$$Lambda$FileRefController$_4y5prO3JWgmruc7MhfACo84xB8 r2 = new org.telegram.messenger.-$$Lambda$FileRefController$_4y5prO3JWgmruc7MhfACo84xB8
            r2.<init>(r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            goto L_0x0285
        L_0x0281:
            r17 = r2
            r18 = r4
        L_0x0285:
            r13 = r7
            goto L_0x02b7
        L_0x0287:
            r17 = r2
            r18 = r4
            boolean r2 = r6 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r2 == 0) goto L_0x02b7
            org.telegram.tgnet.TLRPC$Chat r6 = (org.telegram.tgnet.TLRPC$Chat) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r14.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Chat) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r2, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r29 == 0) goto L_0x02b6
            if (r2 == 0) goto L_0x02b6
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r4.add(r6)
            org.telegram.messenger.MessagesStorage r7 = r25.getMessagesStorage()
            r13 = 0
            r15 = 1
            r7.putUsersAndChats(r13, r4, r15, r15)
            org.telegram.messenger.-$$Lambda$FileRefController$e8OtXvlo5kjEKEtnDDj1BG232-0 r4 = new org.telegram.messenger.-$$Lambda$FileRefController$e8OtXvlo5kjEKEtnDDj1BG232-0
            r4.<init>(r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
        L_0x02b6:
            r13 = r2
        L_0x02b7:
            if (r13 == 0) goto L_0x02bb
            goto L_0x01b4
        L_0x02bb:
            int r8 = r8 + 1
            r2 = r17
            r4 = r18
            goto L_0x0249
        L_0x02c2:
            r18 = r4
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_chats
            if (r2 == 0) goto L_0x030f
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_chats r2 = (org.telegram.tgnet.TLRPC$TL_messages_chats) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r2.chats
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x01b4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r2.chats
            int r4 = r4.size()
            r8 = 0
        L_0x02da:
            if (r8 >= r4) goto L_0x01b4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r2.chats
            java.lang.Object r5 = r5.get(r8)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r14.location
            byte[] r13 = r0.getFileReference((org.telegram.tgnet.TLRPC$Chat) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r6, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r13 == 0) goto L_0x030b
            if (r29 == 0) goto L_0x01b4
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r2.add(r5)
            org.telegram.messenger.MessagesStorage r4 = r25.getMessagesStorage()
            r6 = 0
            r7 = 1
            r4.putUsersAndChats(r6, r2, r7, r7)
            org.telegram.messenger.-$$Lambda$FileRefController$tUyca42NNsTxyQpe3m9-DSncaao r2 = new org.telegram.messenger.-$$Lambda$FileRefController$tUyca42NNsTxyQpe3m9-DSncaao
            r2.<init>(r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            goto L_0x041f
        L_0x030b:
            r6 = 0
            int r8 = r8 + 1
            goto L_0x02da
        L_0x030f:
            r6 = 0
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_savedGifs
            if (r2 == 0) goto L_0x034d
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_savedGifs r2 = (org.telegram.tgnet.TLRPC$TL_messages_savedGifs) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.gifs
            int r4 = r4.size()
            r8 = 0
        L_0x031e:
            if (r8 >= r4) goto L_0x0336
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r5 = r2.gifs
            java.lang.Object r5 = r5.get(r8)
            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
            org.telegram.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r13 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r7, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r13 == 0) goto L_0x0333
            goto L_0x0336
        L_0x0333:
            int r8 = r8 + 1
            goto L_0x031e
        L_0x0336:
            if (r29 == 0) goto L_0x041f
            org.telegram.messenger.MediaDataController r19 = r25.getMediaDataController()
            r20 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r2.gifs
            r22 = 1
            r23 = 0
            r24 = 1
            r21 = r2
            r19.processLoadedRecentDocuments(r20, r21, r22, r23, r24)
            goto L_0x041f
        L_0x034d:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            if (r2 == 0) goto L_0x0381
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r2
            if (r13 != 0) goto L_0x0375
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.documents
            int r4 = r4.size()
            r8 = 0
        L_0x035d:
            if (r8 >= r4) goto L_0x0375
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r5 = r2.documents
            java.lang.Object r5 = r5.get(r8)
            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
            org.telegram.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r13 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r7, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r13 == 0) goto L_0x0372
            goto L_0x0375
        L_0x0372:
            int r8 = r8 + 1
            goto L_0x035d
        L_0x0375:
            if (r29 == 0) goto L_0x041f
            org.telegram.messenger.-$$Lambda$FileRefController$q_nWD12uKB_1zPGAdz0IXAdyGJg r4 = new org.telegram.messenger.-$$Lambda$FileRefController$q_nWD12uKB_1zPGAdz0IXAdyGJg
            r4.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            goto L_0x041f
        L_0x0381:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_recentStickers
            if (r2 == 0) goto L_0x03bd
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_recentStickers r2 = (org.telegram.tgnet.TLRPC$TL_messages_recentStickers) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.stickers
            int r4 = r4.size()
            r8 = 0
        L_0x038f:
            if (r8 >= r4) goto L_0x03a7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r5 = r2.stickers
            java.lang.Object r5 = r5.get(r8)
            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
            org.telegram.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r13 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r7, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r13 == 0) goto L_0x03a4
            goto L_0x03a7
        L_0x03a4:
            int r8 = r8 + 1
            goto L_0x038f
        L_0x03a7:
            if (r29 == 0) goto L_0x041f
            org.telegram.messenger.MediaDataController r19 = r25.getMediaDataController()
            r20 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r2.stickers
            r22 = 0
            r23 = 0
            r24 = 1
            r21 = r2
            r19.processLoadedRecentDocuments(r20, r21, r22, r23, r24)
            goto L_0x041f
        L_0x03bd:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_favedStickers
            if (r2 == 0) goto L_0x03f9
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_favedStickers r2 = (org.telegram.tgnet.TLRPC$TL_messages_favedStickers) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.stickers
            int r4 = r4.size()
            r8 = 0
        L_0x03cb:
            if (r8 >= r4) goto L_0x03e3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r5 = r2.stickers
            java.lang.Object r5 = r5.get(r8)
            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
            org.telegram.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r13 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r7, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r13 == 0) goto L_0x03e0
            goto L_0x03e3
        L_0x03e0:
            int r8 = r8 + 1
            goto L_0x03cb
        L_0x03e3:
            if (r29 == 0) goto L_0x041f
            org.telegram.messenger.MediaDataController r19 = r25.getMediaDataController()
            r20 = 2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r2.stickers
            r22 = 0
            r23 = 0
            r24 = 1
            r21 = r2
            r19.processLoadedRecentDocuments(r20, r21, r22, r23, r24)
            goto L_0x041f
        L_0x03f9:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$photos_Photos
            if (r2 == 0) goto L_0x041f
            r2 = r3
            org.telegram.tgnet.TLRPC$photos_Photos r2 = (org.telegram.tgnet.TLRPC$photos_Photos) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r4 = r2.photos
            int r4 = r4.size()
            r8 = 0
        L_0x0407:
            if (r8 >= r4) goto L_0x041f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r5 = r2.photos
            java.lang.Object r5 = r5.get(r8)
            org.telegram.tgnet.TLRPC$Photo r5 = (org.telegram.tgnet.TLRPC$Photo) r5
            org.telegram.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r13 = r0.getFileReference((org.telegram.tgnet.TLRPC$Photo) r5, (org.telegram.tgnet.TLRPC$InputFileLocation) r7, (boolean[]) r10, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r13 == 0) goto L_0x041c
            goto L_0x041f
        L_0x041c:
            int r8 = r8 + 1
            goto L_0x0407
        L_0x041f:
            if (r13 == 0) goto L_0x042e
            r2 = 0
            if (r11 == 0) goto L_0x0427
            r4 = r11[r2]
            goto L_0x0428
        L_0x0427:
            r4 = r6
        L_0x0428:
            r0.onUpdateObjectReference(r14, r13, r4)
            r5 = 1
            r12 = 1
            goto L_0x0437
        L_0x042e:
            r2 = 0
            java.lang.Object[] r4 = r14.args
            r5 = 1
            r0.sendErrorToObject(r4, r5)
        L_0x0437:
            int r9 = r9 + 1
            r2 = r27
            r5 = r16
            r4 = r18
            r8 = 1
            goto L_0x007c
        L_0x0442:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r0.locationRequester
            r2.remove(r1)
            if (r12 == 0) goto L_0x044c
            r0.putReponseToCache(r1, r3)
        L_0x044c:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.onRequestComplete(java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, boolean):boolean");
    }

    public /* synthetic */ void lambda$onRequestComplete$29$FileRefController(TLRPC$User tLRPC$User) {
        getMessagesController().putUser(tLRPC$User, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$30$FileRefController(TLRPC$Chat tLRPC$Chat) {
        getMessagesController().putChat(tLRPC$Chat, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$31$FileRefController(TLRPC$Chat tLRPC$Chat) {
        getMessagesController().putChat(tLRPC$Chat, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$32$FileRefController(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        getMediaDataController().replaceStickerSet(tLRPC$TL_messages_stickerSet);
    }

    private void cleanupCache() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastCleanupTime) >= 600000) {
            this.lastCleanupTime = SystemClock.elapsedRealtime();
            ArrayList arrayList = null;
            for (Map.Entry next : this.responseCache.entrySet()) {
                if (Math.abs(SystemClock.elapsedRealtime() - ((CachedResult) next.getValue()).firstQueryTime) >= 600000) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(next.getKey());
                }
            }
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    this.responseCache.remove(arrayList.get(i));
                }
            }
        }
    }

    private CachedResult getCachedResponse(String str) {
        CachedResult cachedResult = this.responseCache.get(str);
        if (cachedResult == null || Math.abs(SystemClock.elapsedRealtime() - cachedResult.firstQueryTime) < 600000) {
            return cachedResult;
        }
        this.responseCache.remove(str);
        return null;
    }

    private void putReponseToCache(String str, TLObject tLObject) {
        CachedResult cachedResult = this.responseCache.get(str);
        if (cachedResult == null) {
            cachedResult = new CachedResult();
            TLObject unused = cachedResult.response = tLObject;
            long unused2 = cachedResult.firstQueryTime = SystemClock.uptimeMillis();
            this.responseCache.put(str, cachedResult);
        }
        long unused3 = cachedResult.lastQueryTime = SystemClock.uptimeMillis();
    }

    private byte[] getFileReference(TLRPC$Document tLRPC$Document, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        if (!(tLRPC$Document == null || tLRPC$InputFileLocation == null)) {
            if (!(tLRPC$InputFileLocation instanceof TLRPC$TL_inputDocumentFileLocation)) {
                int size = tLRPC$Document.thumbs.size();
                int i = 0;
                while (i < size) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$Document.thumbs.get(i);
                    byte[] fileReference = getFileReference(tLRPC$PhotoSize, tLRPC$InputFileLocation, zArr);
                    if (zArr != null && zArr[0]) {
                        tLRPC$InputFileLocationArr[0] = new TLRPC$TL_inputDocumentFileLocation();
                        tLRPC$InputFileLocationArr[0].id = tLRPC$Document.id;
                        tLRPC$InputFileLocationArr[0].volume_id = tLRPC$InputFileLocation.volume_id;
                        tLRPC$InputFileLocationArr[0].local_id = tLRPC$InputFileLocation.local_id;
                        tLRPC$InputFileLocationArr[0].access_hash = tLRPC$Document.access_hash;
                        TLRPC$InputFileLocation tLRPC$InputFileLocation2 = tLRPC$InputFileLocationArr[0];
                        byte[] bArr = tLRPC$Document.file_reference;
                        tLRPC$InputFileLocation2.file_reference = bArr;
                        tLRPC$InputFileLocationArr[0].thumb_size = tLRPC$PhotoSize.type;
                        return bArr;
                    } else if (fileReference != null) {
                        return fileReference;
                    } else {
                        i++;
                    }
                }
            } else if (tLRPC$Document.id == tLRPC$InputFileLocation.id) {
                return tLRPC$Document.file_reference;
            }
        }
        return null;
    }

    private boolean getPeerReferenceReplacement(TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z, TLRPC$InputFileLocation tLRPC$InputFileLocation, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr, boolean[] zArr) {
        TLRPC$InputPeer tLRPC$InputPeer;
        TLRPC$InputPeer tLRPC$InputPeer2;
        if (zArr == null || !zArr[0]) {
            return false;
        }
        tLRPC$InputFileLocationArr[0] = new TLRPC$TL_inputPeerPhotoFileLocation();
        TLRPC$InputFileLocation tLRPC$InputFileLocation2 = tLRPC$InputFileLocationArr[0];
        long j = tLRPC$InputFileLocation.volume_id;
        tLRPC$InputFileLocation2.id = j;
        tLRPC$InputFileLocationArr[0].volume_id = j;
        tLRPC$InputFileLocationArr[0].local_id = tLRPC$InputFileLocation.local_id;
        tLRPC$InputFileLocationArr[0].big = z;
        if (tLRPC$User != null) {
            tLRPC$InputPeer = new TLRPC$TL_inputPeerUser();
            tLRPC$InputPeer.user_id = tLRPC$User.id;
            tLRPC$InputPeer.access_hash = tLRPC$User.access_hash;
        } else {
            if (ChatObject.isChannel(tLRPC$Chat)) {
                tLRPC$InputPeer2 = new TLRPC$TL_inputPeerChat();
                tLRPC$InputPeer2.chat_id = tLRPC$Chat.id;
            } else {
                tLRPC$InputPeer2 = new TLRPC$TL_inputPeerChannel();
                tLRPC$InputPeer2.channel_id = tLRPC$Chat.id;
                tLRPC$InputPeer2.access_hash = tLRPC$Chat.access_hash;
            }
            tLRPC$InputPeer = tLRPC$InputPeer2;
        }
        tLRPC$InputFileLocationArr[0].peer = tLRPC$InputPeer;
        return true;
    }

    private byte[] getFileReference(TLRPC$User tLRPC$User, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto;
        if (tLRPC$User == null || (tLRPC$UserProfilePhoto = tLRPC$User.photo) == null || !(tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation)) {
            return null;
        }
        byte[] fileReference = getFileReference(tLRPC$UserProfilePhoto.photo_small, tLRPC$InputFileLocation, zArr);
        if (getPeerReferenceReplacement(tLRPC$User, (TLRPC$Chat) null, false, tLRPC$InputFileLocation, tLRPC$InputFileLocationArr, zArr)) {
            return new byte[0];
        }
        if (fileReference == null) {
            fileReference = getFileReference(tLRPC$User.photo.photo_big, tLRPC$InputFileLocation, zArr);
            if (getPeerReferenceReplacement(tLRPC$User, (TLRPC$Chat) null, true, tLRPC$InputFileLocation, tLRPC$InputFileLocationArr, zArr)) {
                return new byte[0];
            }
        }
        return fileReference;
    }

    private byte[] getFileReference(TLRPC$Chat tLRPC$Chat, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        TLRPC$ChatPhoto tLRPC$ChatPhoto;
        if (tLRPC$Chat == null || (tLRPC$ChatPhoto = tLRPC$Chat.photo) == null || !(tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation)) {
            return null;
        }
        byte[] fileReference = getFileReference(tLRPC$ChatPhoto.photo_small, tLRPC$InputFileLocation, zArr);
        if (getPeerReferenceReplacement((TLRPC$User) null, tLRPC$Chat, false, tLRPC$InputFileLocation, tLRPC$InputFileLocationArr, zArr)) {
            return new byte[0];
        }
        if (fileReference == null) {
            fileReference = getFileReference(tLRPC$Chat.photo.photo_big, tLRPC$InputFileLocation, zArr);
            if (getPeerReferenceReplacement((TLRPC$User) null, tLRPC$Chat, true, tLRPC$InputFileLocation, tLRPC$InputFileLocationArr, zArr)) {
                return new byte[0];
            }
        }
        return fileReference;
    }

    private byte[] getFileReference(TLRPC$Photo tLRPC$Photo, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        if (tLRPC$Photo == null) {
            return null;
        }
        if (!(tLRPC$InputFileLocation instanceof TLRPC$TL_inputPhotoFileLocation)) {
            if (tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation) {
                int size = tLRPC$Photo.sizes.size();
                int i = 0;
                while (i < size) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$Photo.sizes.get(i);
                    byte[] fileReference = getFileReference(tLRPC$PhotoSize, tLRPC$InputFileLocation, zArr);
                    if (zArr != null && zArr[0]) {
                        tLRPC$InputFileLocationArr[0] = new TLRPC$TL_inputPhotoFileLocation();
                        tLRPC$InputFileLocationArr[0].id = tLRPC$Photo.id;
                        tLRPC$InputFileLocationArr[0].volume_id = tLRPC$InputFileLocation.volume_id;
                        tLRPC$InputFileLocationArr[0].local_id = tLRPC$InputFileLocation.local_id;
                        tLRPC$InputFileLocationArr[0].access_hash = tLRPC$Photo.access_hash;
                        TLRPC$InputFileLocation tLRPC$InputFileLocation2 = tLRPC$InputFileLocationArr[0];
                        byte[] bArr = tLRPC$Photo.file_reference;
                        tLRPC$InputFileLocation2.file_reference = bArr;
                        tLRPC$InputFileLocationArr[0].thumb_size = tLRPC$PhotoSize.type;
                        return bArr;
                    } else if (fileReference != null) {
                        return fileReference;
                    } else {
                        i++;
                    }
                }
            }
            return null;
        } else if (tLRPC$Photo.id == tLRPC$InputFileLocation.id) {
            return tLRPC$Photo.file_reference;
        } else {
            return null;
        }
    }

    private byte[] getFileReference(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr) {
        if (tLRPC$PhotoSize == null || !(tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation)) {
            return null;
        }
        return getFileReference(tLRPC$PhotoSize.location, tLRPC$InputFileLocation, zArr);
    }

    private byte[] getFileReference(TLRPC$FileLocation tLRPC$FileLocation, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr) {
        if (tLRPC$FileLocation == null || !(tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation) || tLRPC$FileLocation.local_id != tLRPC$InputFileLocation.local_id || tLRPC$FileLocation.volume_id != tLRPC$InputFileLocation.volume_id) {
            return null;
        }
        if (tLRPC$FileLocation.file_reference == null && zArr != null) {
            zArr[0] = true;
        }
        return tLRPC$FileLocation.file_reference;
    }

    private byte[] getFileReference(TLRPC$WebPage tLRPC$WebPage, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        byte[] fileReference = getFileReference(tLRPC$WebPage.document, tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
        if (fileReference != null) {
            return fileReference;
        }
        byte[] fileReference2 = getFileReference(tLRPC$WebPage.photo, tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
        if (fileReference2 != null) {
            return fileReference2;
        }
        if (!tLRPC$WebPage.attributes.isEmpty()) {
            int size = tLRPC$WebPage.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_webPageAttributeTheme tLRPC$TL_webPageAttributeTheme = tLRPC$WebPage.attributes.get(i);
                int size2 = tLRPC$TL_webPageAttributeTheme.documents.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    byte[] fileReference3 = getFileReference(tLRPC$TL_webPageAttributeTheme.documents.get(i2), tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
                    if (fileReference3 != null) {
                        return fileReference3;
                    }
                }
            }
        }
        TLRPC$Page tLRPC$Page = tLRPC$WebPage.cached_page;
        if (tLRPC$Page == null) {
            return null;
        }
        int size3 = tLRPC$Page.documents.size();
        for (int i3 = 0; i3 < size3; i3++) {
            byte[] fileReference4 = getFileReference(tLRPC$WebPage.cached_page.documents.get(i3), tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
            if (fileReference4 != null) {
                return fileReference4;
            }
        }
        int size4 = tLRPC$WebPage.cached_page.photos.size();
        for (int i4 = 0; i4 < size4; i4++) {
            byte[] fileReference5 = getFileReference(tLRPC$WebPage.cached_page.photos.get(i4), tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
            if (fileReference5 != null) {
                return fileReference5;
            }
        }
        return null;
    }

    public static boolean isFileRefError(String str) {
        return "FILEREF_EXPIRED".equals(str) || "FILE_REFERENCE_EXPIRED".equals(str) || "FILE_REFERENCE_EMPTY".equals(str) || (str != null && str.startsWith("FILE_REFERENCE_"));
    }
}
