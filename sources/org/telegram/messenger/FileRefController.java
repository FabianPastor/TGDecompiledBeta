package org.telegram.messenger;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class FileRefController extends BaseController {
    private static volatile FileRefController[] Instance = new FileRefController[4];
    private ArrayList<Waiter> favStickersWaiter = new ArrayList<>();
    private long lastCleanupTime = SystemClock.elapsedRealtime();
    private HashMap<String, ArrayList<Requester>> locationRequester = new HashMap<>();
    private HashMap<TLRPC.TL_messages_sendMultiMedia, Object[]> multiMediaCache = new HashMap<>();
    private HashMap<String, ArrayList<Requester>> parentRequester = new HashMap<>();
    private ArrayList<Waiter> recentStickersWaiter = new ArrayList<>();
    private HashMap<String, CachedResult> responseCache = new HashMap<>();
    private ArrayList<Waiter> savedGifsWaiters = new ArrayList<>();
    private ArrayList<Waiter> wallpaperWaiters = new ArrayList<>();

    private static class Requester {
        /* access modifiers changed from: private */
        public Object[] args;
        /* access modifiers changed from: private */
        public boolean completed;
        /* access modifiers changed from: private */
        public TLRPC.InputFileLocation location;
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

        public Waiter(String loc, String parent) {
            this.locationKey = loc;
            this.parentKey = parent;
        }
    }

    public static FileRefController getInstance(int num) {
        FileRefController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FileRefController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    FileRefController[] fileRefControllerArr = Instance;
                    FileRefController fileRefController = new FileRefController(num);
                    localInstance = fileRefController;
                    fileRefControllerArr[num] = fileRefController;
                }
            }
        }
        return localInstance;
    }

    public FileRefController(int instance) {
        super(instance);
    }

    public static String getKeyForParentObject(Object parentObject) {
        if (parentObject instanceof TLRPC.TL_availableReaction) {
            return "available_reaction_" + ((TLRPC.TL_availableReaction) parentObject).reaction;
        } else if (parentObject instanceof TLRPC.BotInfo) {
            return "bot_info_" + ((TLRPC.BotInfo) parentObject).user_id;
        } else if (parentObject instanceof TLRPC.TL_attachMenuBot) {
            long botId = ((TLRPC.TL_attachMenuBot) parentObject).bot_id;
            return "attach_menu_bot_" + botId;
        } else if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            long channelId = messageObject.getChannelId();
            return "message" + messageObject.getRealId() + "_" + channelId + "_" + messageObject.scheduled;
        } else if (parentObject instanceof TLRPC.Message) {
            TLRPC.Message message = (TLRPC.Message) parentObject;
            long channelId2 = message.peer_id != null ? message.peer_id.channel_id : 0;
            return "message" + message.id + "_" + channelId2 + "_" + message.from_scheduled;
        } else if (parentObject instanceof TLRPC.WebPage) {
            return "webpage" + ((TLRPC.WebPage) parentObject).id;
        } else if (parentObject instanceof TLRPC.User) {
            return "user" + ((TLRPC.User) parentObject).id;
        } else if (parentObject instanceof TLRPC.Chat) {
            return "chat" + ((TLRPC.Chat) parentObject).id;
        } else if (parentObject instanceof String) {
            return "str" + ((String) parentObject);
        } else if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
            return "set" + ((TLRPC.TL_messages_stickerSet) parentObject).set.id;
        } else if (parentObject instanceof TLRPC.StickerSetCovered) {
            return "set" + ((TLRPC.StickerSetCovered) parentObject).set.id;
        } else if (parentObject instanceof TLRPC.InputStickerSet) {
            return "set" + ((TLRPC.InputStickerSet) parentObject).id;
        } else if (parentObject instanceof TLRPC.TL_wallPaper) {
            return "wallpaper" + ((TLRPC.TL_wallPaper) parentObject).id;
        } else if (parentObject instanceof TLRPC.TL_theme) {
            return "theme" + ((TLRPC.TL_theme) parentObject).id;
        } else if (parentObject == null) {
            return null;
        } else {
            return "" + parentObject;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:119:0x03ca  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x032a  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x032e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestReference(java.lang.Object r18, java.lang.Object... r19) {
        /*
            r17 = this;
            r6 = r17
            r0 = r18
            r7 = r19
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r2 = 0
            if (r1 == 0) goto L_0x0029
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "start loading request reference for parent = "
            r1.append(r3)
            r1.append(r0)
            java.lang.String r3 = " args = "
            r1.append(r3)
            r3 = r7[r2]
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.FileLog.d(r1)
        L_0x0029:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputSingleMedia
            r3 = 2
            java.lang.String r4 = "photo_"
            java.lang.String r5 = "file_"
            if (r1 == 0) goto L_0x0092
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r1 = (org.telegram.tgnet.TLRPC.TL_inputSingleMedia) r1
            org.telegram.tgnet.TLRPC$InputMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaDocument
            if (r8 == 0) goto L_0x0061
            org.telegram.tgnet.TLRPC$InputMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r4 = (org.telegram.tgnet.TLRPC.TL_inputMediaDocument) r4
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r4.id
            long r9 = r5.id
            r8.append(r9)
            java.lang.String r5 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r9 = r4.id
            long r9 = r9.id
            r8.id = r9
            goto L_0x008a
        L_0x0061:
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaPhoto
            if (r5 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r5 = (org.telegram.tgnet.TLRPC.TL_inputMediaPhoto) r5
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r5.id
            long r9 = r4.id
            r8.append(r9)
            java.lang.String r4 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r9 = r5.id
            long r9 = r9.id
            r8.id = r9
            r5 = r4
        L_0x008a:
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x008e:
            r6.sendErrorToObject(r7, r2)
            return
        L_0x0092:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia
            if (r1 == 0) goto L_0x00ca
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r1
            r4 = r0
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia, java.lang.Object[]> r5 = r6.multiMediaCache
            r5.put(r1, r7)
            r5 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r8 = r1.multi_media
            int r8 = r8.size()
        L_0x00ab:
            if (r5 >= r8) goto L_0x00c9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r9 = r1.multi_media
            java.lang.Object r9 = r9.get(r5)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r9 = (org.telegram.tgnet.TLRPC.TL_inputSingleMedia) r9
            java.lang.Object r0 = r4.get(r5)
            if (r0 != 0) goto L_0x00bc
            goto L_0x00c6
        L_0x00bc:
            java.lang.Object[] r10 = new java.lang.Object[r3]
            r10[r2] = r9
            r11 = 1
            r10[r11] = r1
            r6.requestReference(r0, r10)
        L_0x00c6:
            int r5 = r5 + 1
            goto L_0x00ab
        L_0x00c9:
            return
        L_0x00ca:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMedia
            if (r1 == 0) goto L_0x012e
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r1 = (org.telegram.tgnet.TLRPC.TL_messages_sendMedia) r1
            org.telegram.tgnet.TLRPC$InputMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaDocument
            if (r8 == 0) goto L_0x00fd
            org.telegram.tgnet.TLRPC$InputMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r4 = (org.telegram.tgnet.TLRPC.TL_inputMediaDocument) r4
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r4.id
            long r9 = r5.id
            r8.append(r9)
            java.lang.String r5 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r9 = r4.id
            long r9 = r9.id
            r8.id = r9
            goto L_0x0126
        L_0x00fd:
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaPhoto
            if (r5 == 0) goto L_0x012a
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r5 = (org.telegram.tgnet.TLRPC.TL_inputMediaPhoto) r5
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r5.id
            long r9 = r4.id
            r8.append(r9)
            java.lang.String r4 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r9 = r5.id
            long r9 = r9.id
            r8.id = r9
            r5 = r4
        L_0x0126:
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x012a:
            r6.sendErrorToObject(r7, r2)
            return
        L_0x012e:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_editMessage
            if (r1 == 0) goto L_0x0192
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r1 = (org.telegram.tgnet.TLRPC.TL_messages_editMessage) r1
            org.telegram.tgnet.TLRPC$InputMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaDocument
            if (r8 == 0) goto L_0x0161
            org.telegram.tgnet.TLRPC$InputMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r4 = (org.telegram.tgnet.TLRPC.TL_inputMediaDocument) r4
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r4.id
            long r9 = r5.id
            r8.append(r9)
            java.lang.String r5 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r9 = r4.id
            long r9 = r9.id
            r8.id = r9
            goto L_0x018a
        L_0x0161:
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaPhoto
            if (r5 == 0) goto L_0x018e
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r5 = (org.telegram.tgnet.TLRPC.TL_inputMediaPhoto) r5
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r5.id
            long r9 = r4.id
            r8.append(r9)
            java.lang.String r4 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r9 = r5.id
            long r9 = r9.id
            r8.id = r9
            r5 = r4
        L_0x018a:
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x018e:
            r6.sendErrorToObject(r7, r2)
            return
        L_0x0192:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_saveGif
            if (r1 == 0) goto L_0x01bf
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_saveGif r1 = (org.telegram.tgnet.TLRPC.TL_messages_saveGif) r1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r1.id
            long r8 = r5.id
            r4.append(r8)
            java.lang.String r5 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r4 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r4.<init>()
            r8 = r4
            org.telegram.tgnet.TLRPC$InputDocument r4 = r1.id
            long r9 = r4.id
            r8.id = r9
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x01bf:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker
            if (r1 == 0) goto L_0x01ec
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker r1 = (org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker) r1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r1.id
            long r8 = r5.id
            r4.append(r8)
            java.lang.String r5 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r4 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r4.<init>()
            r8 = r4
            org.telegram.tgnet.TLRPC$InputDocument r4 = r1.id
            long r9 = r4.id
            r8.id = r9
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x01ec:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_faveSticker
            if (r1 == 0) goto L_0x0219
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_faveSticker r1 = (org.telegram.tgnet.TLRPC.TL_messages_faveSticker) r1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r1.id
            long r8 = r5.id
            r4.append(r8)
            java.lang.String r5 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r4 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r4.<init>()
            r8 = r4
            org.telegram.tgnet.TLRPC$InputDocument r4 = r1.id
            long r9 = r4.id
            r8.id = r9
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x0219:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers
            if (r1 == 0) goto L_0x027d
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers r1 = (org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers) r1
            org.telegram.tgnet.TLRPC$InputStickeredMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputStickeredMediaDocument
            if (r8 == 0) goto L_0x024c
            org.telegram.tgnet.TLRPC$InputStickeredMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument r4 = (org.telegram.tgnet.TLRPC.TL_inputStickeredMediaDocument) r4
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r4.id
            long r9 = r5.id
            r8.append(r9)
            java.lang.String r5 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r9 = r4.id
            long r9 = r9.id
            r8.id = r9
            goto L_0x0275
        L_0x024c:
            org.telegram.tgnet.TLRPC$InputStickeredMedia r5 = r1.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputStickeredMediaPhoto
            if (r5 == 0) goto L_0x0279
            org.telegram.tgnet.TLRPC$InputStickeredMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto r5 = (org.telegram.tgnet.TLRPC.TL_inputStickeredMediaPhoto) r5
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r5.id
            long r9 = r4.id
            r8.append(r9)
            java.lang.String r4 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r9 = r5.id
            long r9 = r9.id
            r8.id = r9
            r5 = r4
        L_0x0275:
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x0279:
            r6.sendErrorToObject(r7, r2)
            return
        L_0x027d:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputFileLocation
            if (r1 == 0) goto L_0x02a8
            r1 = r7[r2]
            r8 = r1
            org.telegram.tgnet.TLRPC$TL_inputFileLocation r8 = (org.telegram.tgnet.TLRPC.TL_inputFileLocation) r8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "loc_"
            r1.append(r4)
            int r4 = r8.local_id
            r1.append(r4)
            java.lang.String r4 = "_"
            r1.append(r4)
            long r4 = r8.volume_id
            r1.append(r4)
            java.lang.String r5 = r1.toString()
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x02a8:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation
            if (r1 == 0) goto L_0x02c7
            r1 = r7[r2]
            r8 = r1
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = (org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation) r8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            long r4 = r8.id
            r1.append(r4)
            java.lang.String r5 = r1.toString()
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x02c7:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputPhotoFileLocation
            if (r1 == 0) goto L_0x02e6
            r1 = r7[r2]
            r8 = r1
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = (org.telegram.tgnet.TLRPC.TL_inputPhotoFileLocation) r8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            long r4 = r8.id
            r1.append(r4)
            java.lang.String r5 = r1.toString()
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x02e6:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation
            if (r1 == 0) goto L_0x0402
            r1 = r7[r2]
            r8 = r1
            org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r8 = (org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation) r8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "avatar_"
            r1.append(r4)
            long r4 = r8.id
            r1.append(r4)
            java.lang.String r5 = r1.toString()
            r9 = r8
            r8 = r5
        L_0x0306:
            boolean r1 = r0 instanceof org.telegram.messenger.MessageObject
            if (r1 == 0) goto L_0x0323
            r1 = r0
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r4 = r1.getRealId()
            if (r4 >= 0) goto L_0x0323
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            if (r4 == 0) goto L_0x0323
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r4.webpage
            r10 = r0
            goto L_0x0324
        L_0x0323:
            r10 = r0
        L_0x0324:
            java.lang.String r11 = getKeyForParentObject(r10)
            if (r11 != 0) goto L_0x032e
            r6.sendErrorToObject(r7, r2)
            return
        L_0x032e:
            org.telegram.messenger.FileRefController$Requester r0 = new org.telegram.messenger.FileRefController$Requester
            r1 = 0
            r0.<init>()
            r12 = r0
            java.lang.Object[] unused = r12.args = r7
            org.telegram.tgnet.TLRPC.InputFileLocation unused = r12.location = r9
            java.lang.String unused = r12.locationKey = r8
            r0 = 0
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r1 = r6.locationRequester
            java.lang.Object r1 = r1.get(r8)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 != 0) goto L_0x0356
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r1 = r2
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r6.locationRequester
            r2.put(r8, r1)
            int r0 = r0 + 1
        L_0x0356:
            r1.add(r12)
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r6.parentRequester
            java.lang.Object r2 = r2.get(r11)
            r1 = r2
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 != 0) goto L_0x0374
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r1 = r2
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r6.parentRequester
            r2.put(r11, r1)
            int r0 = r0 + 1
            r13 = r0
            r14 = r1
            goto L_0x0376
        L_0x0374:
            r13 = r0
            r14 = r1
        L_0x0376:
            r14.add(r12)
            if (r13 == r3) goto L_0x037c
            return
        L_0x037c:
            r0 = r8
            boolean r1 = r10 instanceof java.lang.String
            if (r1 == 0) goto L_0x03c0
            r1 = r10
            java.lang.String r1 = (java.lang.String) r1
            java.lang.String r2 = "wallpaper"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0390
            java.lang.String r0 = "wallpaper"
            r15 = r0
            goto L_0x03c1
        L_0x0390:
            java.lang.String r2 = "gif"
            boolean r2 = r1.startsWith(r2)
            if (r2 == 0) goto L_0x039c
            java.lang.String r0 = "gif"
            r15 = r0
            goto L_0x03c1
        L_0x039c:
            java.lang.String r2 = "recent"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x03a8
            java.lang.String r0 = "recent"
            r15 = r0
            goto L_0x03c1
        L_0x03a8:
            java.lang.String r2 = "fav"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x03b4
            java.lang.String r0 = "fav"
            r15 = r0
            goto L_0x03c1
        L_0x03b4:
            java.lang.String r2 = "update"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x03c0
            java.lang.String r0 = "update"
            r15 = r0
            goto L_0x03c1
        L_0x03c0:
            r15 = r0
        L_0x03c1:
            r17.cleanupCache()
            org.telegram.messenger.FileRefController$CachedResult r16 = r6.getCachedResponse(r15)
            if (r16 == 0) goto L_0x03e1
            org.telegram.tgnet.TLObject r3 = r16.response
            r4 = 0
            r5 = 1
            r0 = r17
            r1 = r8
            r2 = r11
            boolean r0 = r0.onRequestComplete(r1, r2, r3, r4, r5)
            if (r0 != 0) goto L_0x03e0
            java.util.HashMap<java.lang.String, org.telegram.messenger.FileRefController$CachedResult> r0 = r6.responseCache
            r0.remove(r8)
            goto L_0x03fe
        L_0x03e0:
            return
        L_0x03e1:
            org.telegram.messenger.FileRefController$CachedResult r16 = r6.getCachedResponse(r11)
            if (r16 == 0) goto L_0x03fe
            org.telegram.tgnet.TLObject r3 = r16.response
            r4 = 0
            r5 = 1
            r0 = r17
            r1 = r8
            r2 = r11
            boolean r0 = r0.onRequestComplete(r1, r2, r3, r4, r5)
            if (r0 != 0) goto L_0x03fd
            java.util.HashMap<java.lang.String, org.telegram.messenger.FileRefController$CachedResult> r0 = r6.responseCache
            r0.remove(r11)
            goto L_0x03fe
        L_0x03fd:
            return
        L_0x03fe:
            r6.requestReferenceFromServer(r10, r8, r11, r7)
            return
        L_0x0402:
            r6.sendErrorToObject(r7, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.requestReference(java.lang.Object, java.lang.Object[]):void");
    }

    private void broadcastWaitersData(ArrayList<Waiter> waiters, TLObject response) {
        int a = 0;
        int N = waiters.size();
        while (a < N) {
            Waiter waiter = waiters.get(a);
            onRequestComplete(waiter.locationKey, waiter.parentKey, response, a == N + -1, false);
            a++;
        }
        waiters.clear();
    }

    private void requestReferenceFromServer(Object parentObject, String locationKey, String parentKey, Object[] args) {
        if (parentObject instanceof TLRPC.TL_availableReaction) {
            TLRPC.TL_messages_getAvailableReactions req = new TLRPC.TL_messages_getAvailableReactions();
            req.hash = 0;
            getConnectionsManager().sendRequest(req, new FileRefController$$ExternalSyntheticLambda5(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.BotInfo) {
            TLRPC.TL_users_getFullUser req2 = new TLRPC.TL_users_getFullUser();
            req2.id = getMessagesController().getInputUser(((TLRPC.BotInfo) parentObject).user_id);
            getConnectionsManager().sendRequest(req2, new FileRefController$$ExternalSyntheticLambda6(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.TL_attachMenuBot) {
            TLRPC.TL_messages_getAttachMenuBot req3 = new TLRPC.TL_messages_getAttachMenuBot();
            req3.bot = getMessagesController().getInputUser(((TLRPC.TL_attachMenuBot) parentObject).bot_id);
            getConnectionsManager().sendRequest(req3, new FileRefController$$ExternalSyntheticLambda14(this, locationKey, parentKey));
        } else if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            long channelId = messageObject.getChannelId();
            if (messageObject.scheduled) {
                TLRPC.TL_messages_getScheduledMessages req4 = new TLRPC.TL_messages_getScheduledMessages();
                req4.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
                req4.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req4, new FileRefController$$ExternalSyntheticLambda19(this, locationKey, parentKey));
            } else if (channelId != 0) {
                TLRPC.TL_channels_getMessages req5 = new TLRPC.TL_channels_getMessages();
                req5.channel = getMessagesController().getInputChannel(channelId);
                req5.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req5, new FileRefController$$ExternalSyntheticLambda20(this, locationKey, parentKey));
            } else {
                TLRPC.TL_messages_getMessages req6 = new TLRPC.TL_messages_getMessages();
                req6.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req6, new FileRefController$$ExternalSyntheticLambda21(this, locationKey, parentKey));
            }
        } else if (parentObject instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) parentObject;
            TLRPC.TL_account_getWallPaper req7 = new TLRPC.TL_account_getWallPaper();
            TLRPC.TL_inputWallPaper inputWallPaper = new TLRPC.TL_inputWallPaper();
            inputWallPaper.id = wallPaper.id;
            inputWallPaper.access_hash = wallPaper.access_hash;
            req7.wallpaper = inputWallPaper;
            getConnectionsManager().sendRequest(req7, new FileRefController$$ExternalSyntheticLambda23(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme theme = (TLRPC.TL_theme) parentObject;
            TLRPC.TL_account_getTheme req8 = new TLRPC.TL_account_getTheme();
            TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
            inputTheme.id = theme.id;
            inputTheme.access_hash = theme.access_hash;
            req8.theme = inputTheme;
            req8.format = "android";
            getConnectionsManager().sendRequest(req8, new FileRefController$$ExternalSyntheticLambda24(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.WebPage) {
            TLRPC.TL_messages_getWebPage req9 = new TLRPC.TL_messages_getWebPage();
            req9.url = ((TLRPC.WebPage) parentObject).url;
            req9.hash = 0;
            getConnectionsManager().sendRequest(req9, new FileRefController$$ExternalSyntheticLambda25(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.User) {
            TLRPC.TL_users_getUsers req10 = new TLRPC.TL_users_getUsers();
            req10.id.add(getMessagesController().getInputUser((TLRPC.User) parentObject));
            getConnectionsManager().sendRequest(req10, new FileRefController$$ExternalSyntheticLambda26(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) parentObject;
            if (chat instanceof TLRPC.TL_chat) {
                TLRPC.TL_messages_getChats req11 = new TLRPC.TL_messages_getChats();
                req11.id.add(Long.valueOf(chat.id));
                getConnectionsManager().sendRequest(req11, new FileRefController$$ExternalSyntheticLambda7(this, locationKey, parentKey));
            } else if (chat instanceof TLRPC.TL_channel) {
                TLRPC.TL_channels_getChannels req12 = new TLRPC.TL_channels_getChannels();
                req12.id.add(MessagesController.getInputChannel(chat));
                getConnectionsManager().sendRequest(req12, new FileRefController$$ExternalSyntheticLambda8(this, locationKey, parentKey));
            }
        } else if (parentObject instanceof String) {
            String string = (String) parentObject;
            if ("wallpaper".equals(string)) {
                if (this.wallpaperWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_account_getWallPapers(), new FileRefController$$ExternalSyntheticLambda1(this));
                }
                this.wallpaperWaiters.add(new Waiter(locationKey, parentKey));
            } else if (string.startsWith("gif")) {
                if (this.savedGifsWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_messages_getSavedGifs(), new FileRefController$$ExternalSyntheticLambda2(this));
                }
                this.savedGifsWaiters.add(new Waiter(locationKey, parentKey));
            } else if ("recent".equals(string)) {
                if (this.recentStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_messages_getRecentStickers(), new FileRefController$$ExternalSyntheticLambda3(this));
                }
                this.recentStickersWaiter.add(new Waiter(locationKey, parentKey));
            } else if ("fav".equals(string)) {
                if (this.favStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_messages_getFavedStickers(), new FileRefController$$ExternalSyntheticLambda4(this));
                }
                this.favStickersWaiter.add(new Waiter(locationKey, parentKey));
            } else if ("update".equals(string)) {
                TLRPC.TL_help_getAppUpdate req13 = new TLRPC.TL_help_getAppUpdate();
                try {
                    req13.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
                } catch (Exception e) {
                }
                if (req13.source == null) {
                    req13.source = "";
                }
                getConnectionsManager().sendRequest(req13, new FileRefController$$ExternalSyntheticLambda9(this, locationKey, parentKey));
            } else if (string.startsWith("avatar_")) {
                long id = Utilities.parseLong(string).longValue();
                if (id > 0) {
                    TLRPC.TL_photos_getUserPhotos req14 = new TLRPC.TL_photos_getUserPhotos();
                    req14.limit = 80;
                    req14.offset = 0;
                    req14.max_id = 0;
                    req14.user_id = getMessagesController().getInputUser(id);
                    getConnectionsManager().sendRequest(req14, new FileRefController$$ExternalSyntheticLambda10(this, locationKey, parentKey));
                    return;
                }
                TLRPC.TL_messages_search req15 = new TLRPC.TL_messages_search();
                req15.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
                req15.limit = 80;
                req15.offset_id = 0;
                req15.q = "";
                req15.peer = getMessagesController().getInputPeer(id);
                getConnectionsManager().sendRequest(req15, new FileRefController$$ExternalSyntheticLambda12(this, locationKey, parentKey));
            } else if (string.startsWith("sent_")) {
                String[] params = string.split("_");
                if (params.length == 3) {
                    long channelId2 = Utilities.parseLong(params[1]).longValue();
                    if (channelId2 != 0) {
                        TLRPC.TL_channels_getMessages req16 = new TLRPC.TL_channels_getMessages();
                        req16.channel = getMessagesController().getInputChannel(channelId2);
                        req16.id.add(Utilities.parseInt((CharSequence) params[2]));
                        getConnectionsManager().sendRequest(req16, new FileRefController$$ExternalSyntheticLambda13(this, locationKey, parentKey));
                        return;
                    }
                    TLRPC.TL_messages_getMessages req17 = new TLRPC.TL_messages_getMessages();
                    req17.id.add(Utilities.parseInt((CharSequence) params[2]));
                    getConnectionsManager().sendRequest(req17, new FileRefController$$ExternalSyntheticLambda15(this, locationKey, parentKey));
                    return;
                }
                sendErrorToObject(args, 0);
            } else {
                sendErrorToObject(args, 0);
            }
        } else if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet stickerSet = (TLRPC.TL_messages_stickerSet) parentObject;
            TLRPC.TL_messages_getStickerSet req18 = new TLRPC.TL_messages_getStickerSet();
            req18.stickerset = new TLRPC.TL_inputStickerSetID();
            req18.stickerset.id = stickerSet.set.id;
            req18.stickerset.access_hash = stickerSet.set.access_hash;
            getConnectionsManager().sendRequest(req18, new FileRefController$$ExternalSyntheticLambda16(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.StickerSetCovered) {
            TLRPC.StickerSetCovered stickerSet2 = (TLRPC.StickerSetCovered) parentObject;
            TLRPC.TL_messages_getStickerSet req19 = new TLRPC.TL_messages_getStickerSet();
            req19.stickerset = new TLRPC.TL_inputStickerSetID();
            req19.stickerset.id = stickerSet2.set.id;
            req19.stickerset.access_hash = stickerSet2.set.access_hash;
            getConnectionsManager().sendRequest(req19, new FileRefController$$ExternalSyntheticLambda17(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.InputStickerSet) {
            TLRPC.TL_messages_getStickerSet req20 = new TLRPC.TL_messages_getStickerSet();
            req20.stickerset = (TLRPC.InputStickerSet) parentObject;
            getConnectionsManager().sendRequest(req20, new FileRefController$$ExternalSyntheticLambda18(this, locationKey, parentKey));
        } else {
            sendErrorToObject(args, 0);
        }
    }

    /* renamed from: lambda$requestReferenceFromServer$0$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1841xedaf0ec8(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$1$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1842xvar_e7(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$2$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1853xfCLASSNAME(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$3$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1858x3deae25(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$4$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1859xb43e344(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$5$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1860x12a91863(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$6$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1861x1a0e4d82(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$7$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1862x217382a1(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$8$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1863x28d8b7c0(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$9$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1864x303decdf(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$10$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1843x3c3ad4fd(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$11$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1844x43a00a1c(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$12$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1845x4b053f3b(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.wallpaperWaiters, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$13$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1846x526a745a(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.savedGifsWaiters, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$14$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1847x59cfa979(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.recentStickersWaiter, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$15$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1848x6134de98(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.favStickersWaiter, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$16$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1849x689a13b7(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$17$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1850x6ffvar_d6(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$18$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1851x77647df5(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$19$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1852x7ec9b314(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$20$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1854x217CLASSNAMEbe(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$21$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1855x28e178dd(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$22$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1856x3046adfc(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$23$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1857x37abe31b(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    private boolean isSameReference(byte[] oldRef, byte[] newRef) {
        return Arrays.equals(oldRef, newRef);
    }

    private boolean onUpdateObjectReference(Requester requester, byte[] file_reference, TLRPC.InputFileLocation locationReplacement, boolean fromCache) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("fileref updated for " + requester.args[0] + " " + requester.locationKey);
        }
        if (requester.args[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.TL_messages_sendMultiMedia multiMedia = (TLRPC.TL_messages_sendMultiMedia) requester.args[1];
            Object[] objects = this.multiMediaCache.get(multiMedia);
            if (objects == null) {
                return true;
            }
            TLRPC.TL_inputSingleMedia req = (TLRPC.TL_inputSingleMedia) requester.args[0];
            if (req.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument = (TLRPC.TL_inputMediaDocument) req.media;
                if (fromCache && isSameReference(mediaDocument.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument.id.file_reference = file_reference;
            } else if (req.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto = (TLRPC.TL_inputMediaPhoto) req.media;
                if (fromCache && isSameReference(mediaPhoto.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto.id.file_reference = file_reference;
            }
            int index = multiMedia.multi_media.indexOf(req);
            if (index < 0) {
                return true;
            }
            ArrayList<Object> parentObjects = (ArrayList) objects[3];
            parentObjects.set(index, (Object) null);
            boolean done = true;
            for (int a = 0; a < parentObjects.size(); a++) {
                if (parentObjects.get(a) != null) {
                    done = false;
                }
            }
            if (done) {
                this.multiMediaCache.remove(multiMedia);
                AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda31(this, multiMedia, objects));
            }
        } else if (requester.args[0] instanceof TLRPC.TL_messages_sendMedia) {
            TLRPC.TL_messages_sendMedia req2 = (TLRPC.TL_messages_sendMedia) requester.args[0];
            if (req2.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument2 = (TLRPC.TL_inputMediaDocument) req2.media;
                if (fromCache && isSameReference(mediaDocument2.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument2.id.file_reference = file_reference;
            } else if (req2.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto2 = (TLRPC.TL_inputMediaPhoto) req2.media;
                if (fromCache && isSameReference(mediaPhoto2.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto2.id.file_reference = file_reference;
            }
            AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda0(this, requester));
        } else if (requester.args[0] instanceof TLRPC.TL_messages_editMessage) {
            TLRPC.TL_messages_editMessage req3 = (TLRPC.TL_messages_editMessage) requester.args[0];
            if (req3.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument3 = (TLRPC.TL_inputMediaDocument) req3.media;
                if (fromCache && isSameReference(mediaDocument3.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument3.id.file_reference = file_reference;
            } else if (req3.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto3 = (TLRPC.TL_inputMediaPhoto) req3.media;
                if (fromCache && isSameReference(mediaPhoto3.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto3.id.file_reference = file_reference;
            }
            AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda11(this, requester));
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif req4 = (TLRPC.TL_messages_saveGif) requester.args[0];
            if (fromCache && isSameReference(req4.id.file_reference, file_reference)) {
                return false;
            }
            req4.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req4, FileRefController$$ExternalSyntheticLambda27.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker req5 = (TLRPC.TL_messages_saveRecentSticker) requester.args[0];
            if (fromCache && isSameReference(req5.id.file_reference, file_reference)) {
                return false;
            }
            req5.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req5, FileRefController$$ExternalSyntheticLambda28.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker req6 = (TLRPC.TL_messages_faveSticker) requester.args[0];
            if (fromCache && isSameReference(req6.id.file_reference, file_reference)) {
                return false;
            }
            req6.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req6, FileRefController$$ExternalSyntheticLambda29.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            TLRPC.TL_messages_getAttachedStickers req7 = (TLRPC.TL_messages_getAttachedStickers) requester.args[0];
            if (req7.media instanceof TLRPC.TL_inputStickeredMediaDocument) {
                TLRPC.TL_inputStickeredMediaDocument mediaDocument4 = (TLRPC.TL_inputStickeredMediaDocument) req7.media;
                if (fromCache && isSameReference(mediaDocument4.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument4.id.file_reference = file_reference;
            } else if (req7.media instanceof TLRPC.TL_inputStickeredMediaPhoto) {
                TLRPC.TL_inputStickeredMediaPhoto mediaPhoto4 = (TLRPC.TL_inputStickeredMediaPhoto) req7.media;
                if (fromCache && isSameReference(mediaPhoto4.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto4.id.file_reference = file_reference;
            }
            getConnectionsManager().sendRequest(req7, (RequestDelegate) requester.args[1]);
        } else if (requester.args[1] instanceof FileLoadOperation) {
            FileLoadOperation fileLoadOperation = (FileLoadOperation) requester.args[1];
            if (locationReplacement != null) {
                if (fromCache && isSameReference(fileLoadOperation.location.file_reference, locationReplacement.file_reference)) {
                    return false;
                }
                fileLoadOperation.location = locationReplacement;
            } else if (fromCache && isSameReference(requester.location.file_reference, file_reference)) {
                return false;
            } else {
                requester.location.file_reference = file_reference;
            }
            fileLoadOperation.requestingReference = false;
            fileLoadOperation.startDownloadRequest();
        }
        return true;
    }

    /* renamed from: lambda$onUpdateObjectReference$24$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1838xe1584ddb(TLRPC.TL_messages_sendMultiMedia multiMedia, Object[] objects) {
        TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = multiMedia;
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, objects[1], objects[2], (ArrayList<Object>) null, objects[4], objects[5].booleanValue());
    }

    /* renamed from: lambda$onUpdateObjectReference$25$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1839xe8bd82fa(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, (HashMap<String, String>) null, ((Boolean) requester.args[6]).booleanValue());
    }

    /* renamed from: lambda$onUpdateObjectReference$26$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1840xvar_b819(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, (HashMap<String, String>) null, ((Boolean) requester.args[6]).booleanValue());
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$27(TLObject response, TLRPC.TL_error error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$28(TLObject response, TLRPC.TL_error error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$29(TLObject response, TLRPC.TL_error error) {
    }

    private void sendErrorToObject(Object[] args, int reason) {
        if (args[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.TL_messages_sendMultiMedia req = args[1];
            Object[] objects = this.multiMediaCache.get(req);
            if (objects != null) {
                this.multiMediaCache.remove(req);
                AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda32(this, req, objects));
            }
        } else if ((args[0] instanceof TLRPC.TL_messages_sendMedia) || (args[0] instanceof TLRPC.TL_messages_editMessage)) {
            AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda35(this, args));
        } else if (args[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif tL_messages_saveGif = args[0];
        } else if (args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = args[0];
        } else if (args[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker tL_messages_faveSticker = args[0];
        } else if (args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            getConnectionsManager().sendRequest(args[0], args[1]);
        } else if (reason == 0) {
            TLRPC.TL_error error = new TLRPC.TL_error();
            error.text = "not found parent object to request reference";
            error.code = 400;
            if (args[1] instanceof FileLoadOperation) {
                FileLoadOperation fileLoadOperation = args[1];
                fileLoadOperation.requestingReference = false;
                fileLoadOperation.processRequestResult(args[2], error);
            }
        } else if (reason == 1 && (args[1] instanceof FileLoadOperation)) {
            FileLoadOperation fileLoadOperation2 = args[1];
            fileLoadOperation2.requestingReference = false;
            fileLoadOperation2.onFail(false, 0);
        }
    }

    /* renamed from: lambda$sendErrorToObject$30$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1865xa4770CLASSNAME(TLRPC.TL_messages_sendMultiMedia req, Object[] objects) {
        TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = req;
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, objects[1], objects[2], (ArrayList<Object>) null, objects[4], objects[5].booleanValue());
    }

    /* renamed from: lambda$sendErrorToObject$31$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1866xabdCLASSNAME(Object[] args) {
        getSendMessagesHelper().performSendMessageRequest(args[0], args[1], args[2], args[3], args[4].booleanValue(), args[5], (Object) null, (HashMap<String, String>) null, args[6].booleanValue());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v41, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v56, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v58, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r12v0 */
    /* JADX WARNING: type inference failed for: r12v57 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r12v1, types: [int, boolean] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0165 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0183 A[LOOP:2: B:53:0x00d9->B:80:0x0183, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean onRequestComplete(java.lang.String r32, java.lang.String r33, org.telegram.tgnet.TLObject r34, boolean r35, boolean r36) {
        /*
            r31 = this;
            r6 = r31
            r7 = r32
            r8 = r33
            r9 = r34
            r0 = 0
            r1 = r33
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_account_wallPapers
            if (r2 == 0) goto L_0x0013
            java.lang.String r1 = "wallpaper"
            r10 = r1
            goto L_0x002c
        L_0x0013:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_savedGifs
            if (r2 == 0) goto L_0x001b
            java.lang.String r1 = "gif"
            r10 = r1
            goto L_0x002c
        L_0x001b:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_recentStickers
            if (r2 == 0) goto L_0x0023
            java.lang.String r1 = "recent"
            r10 = r1
            goto L_0x002c
        L_0x0023:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_favedStickers
            if (r2 == 0) goto L_0x002b
            java.lang.String r1 = "fav"
            r10 = r1
            goto L_0x002c
        L_0x002b:
            r10 = r1
        L_0x002c:
            r12 = 1
            if (r8 == 0) goto L_0x007f
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r1 = r6.parentRequester
            java.lang.Object r1 = r1.get(r8)
            r13 = r1
            java.util.ArrayList r13 = (java.util.ArrayList) r13
            if (r13 == 0) goto L_0x007f
            r1 = 0
            int r14 = r13.size()
            r15 = r0
            r5 = r1
        L_0x0041:
            if (r5 >= r14) goto L_0x0072
            java.lang.Object r0 = r13.get(r5)
            r16 = r0
            org.telegram.messenger.FileRefController$Requester r16 = (org.telegram.messenger.FileRefController.Requester) r16
            boolean r0 = r16.completed
            if (r0 == 0) goto L_0x0054
            r17 = r5
            goto L_0x006f
        L_0x0054:
            java.lang.String r1 = r16.locationKey
            r2 = 0
            if (r35 == 0) goto L_0x005f
            if (r15 != 0) goto L_0x005f
            r4 = 1
            goto L_0x0060
        L_0x005f:
            r4 = 0
        L_0x0060:
            r0 = r31
            r3 = r34
            r17 = r5
            r5 = r36
            boolean r0 = r0.onRequestComplete(r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x006f
            r15 = 1
        L_0x006f:
            int r5 = r17 + 1
            goto L_0x0041
        L_0x0072:
            r17 = r5
            if (r15 == 0) goto L_0x0079
            r6.putReponseToCache(r10, r9)
        L_0x0079:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r0 = r6.parentRequester
            r0.remove(r8)
            r0 = r15
        L_0x007f:
            r1 = 0
            r2 = 0
            r3 = 0
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r4 = r6.locationRequester
            java.lang.Object r4 = r4.get(r7)
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            if (r4 != 0) goto L_0x008d
            return r0
        L_0x008d:
            r5 = r32
            r10 = 0
            int r13 = r4.size()
        L_0x0094:
            if (r10 >= r13) goto L_0x064c
            java.lang.Object r14 = r4.get(r10)
            org.telegram.messenger.FileRefController$Requester r14 = (org.telegram.messenger.FileRefController.Requester) r14
            boolean r15 = r14.completed
            if (r15 == 0) goto L_0x00ac
            r15 = r0
            r19 = r4
            r21 = r13
            r11 = 0
            r0 = r36
            goto L_0x0641
        L_0x00ac:
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputFileLocation
            if (r15 != 0) goto L_0x00bc
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation
            if (r15 == 0) goto L_0x00c0
        L_0x00bc:
            org.telegram.tgnet.TLRPC$InputFileLocation[] r2 = new org.telegram.tgnet.TLRPC.InputFileLocation[r12]
            boolean[] r3 = new boolean[r12]
        L_0x00c0:
            boolean unused = r14.completed = r12
            boolean r15 = r9 instanceof org.telegram.tgnet.TLRPC.messages_Messages
            if (r15 == 0) goto L_0x01c6
            r15 = r9
            org.telegram.tgnet.TLRPC$messages_Messages r15 = (org.telegram.tgnet.TLRPC.messages_Messages) r15
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r12 = r15.messages
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x01bc
            r12 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r15.messages
            int r11 = r11.size()
        L_0x00d9:
            if (r12 >= r11) goto L_0x0193
            r18 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r15.messages
            java.lang.Object r1 = r1.get(r12)
            org.telegram.tgnet.TLRPC$Message r1 = (org.telegram.tgnet.TLRPC.Message) r1
            r19 = r4
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            if (r4 == 0) goto L_0x014e
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            if (r4 == 0) goto L_0x00fe
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0163
        L_0x00fe:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            if (r4 == 0) goto L_0x0128
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r4 != 0) goto L_0x0125
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r1.media
            org.telegram.tgnet.TLRPC$TL_game r8 = r8.game
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            r18 = r4
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Photo) r8, (org.telegram.tgnet.TLRPC.InputFileLocation) r4, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0163
        L_0x0125:
            r18 = r4
            goto L_0x0163
        L_0x0128:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            if (r4 == 0) goto L_0x013b
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Photo) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0163
        L_0x013b:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            if (r4 == 0) goto L_0x0161
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.WebPage) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0163
        L_0x014e:
            org.telegram.tgnet.TLRPC$MessageAction r4 = r1.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r4 == 0) goto L_0x0161
            org.telegram.tgnet.TLRPC$MessageAction r4 = r1.action
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Photo) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0163
        L_0x0161:
            r4 = r18
        L_0x0163:
            if (r4 == 0) goto L_0x0183
            if (r35 == 0) goto L_0x017a
            org.telegram.messenger.MessagesStorage r8 = r31.getMessagesStorage()
            r18 = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r15.users
            r20 = r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r11 = r15.chats
            r21 = r13
            r13 = 0
            r8.replaceMessageIfExists(r1, r4, r11, r13)
            goto L_0x0180
        L_0x017a:
            r18 = r4
            r20 = r11
            r21 = r13
        L_0x0180:
            r1 = r18
            goto L_0x019b
        L_0x0183:
            r18 = r4
            r20 = r11
            r21 = r13
            int r12 = r12 + 1
            r8 = r33
            r1 = r18
            r4 = r19
            goto L_0x00d9
        L_0x0193:
            r18 = r1
            r19 = r4
            r20 = r11
            r21 = r13
        L_0x019b:
            if (r1 != 0) goto L_0x01c0
            org.telegram.messenger.MessagesStorage r4 = r31.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r8 = r15.messages
            r11 = 0
            java.lang.Object r8 = r8.get(r11)
            org.telegram.tgnet.TLRPC$Message r8 = (org.telegram.tgnet.TLRPC.Message) r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r15.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r15.chats
            r13 = 1
            r4.replaceMessageIfExists(r8, r11, r12, r13)
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r4 == 0) goto L_0x01c0
            java.lang.String r4 = "file ref not found in messages, replacing message"
            org.telegram.messenger.FileLog.d(r4)
            goto L_0x01c0
        L_0x01bc:
            r19 = r4
            r21 = r13
        L_0x01c0:
            r15 = r0
            r8 = r2
            r4 = r3
            r0 = 0
            goto L_0x061a
        L_0x01c6:
            r19 = r4
            r21 = r13
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_availableReactions
            r11 = 1000(0x3e8, double:4.94E-321)
            if (r4 == 0) goto L_0x025a
            r4 = r9
            org.telegram.tgnet.TLRPC$TL_messages_availableReactions r4 = (org.telegram.tgnet.TLRPC.TL_messages_availableReactions) r4
            org.telegram.messenger.MediaDataController r8 = r31.getMediaDataController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_availableReaction> r13 = r4.reactions
            int r15 = r4.hash
            long r22 = java.lang.System.currentTimeMillis()
            long r11 = r22 / r11
            int r12 = (int) r11
            r11 = 0
            r8.processLoadedReactions(r13, r15, r12, r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_availableReaction> r8 = r4.reactions
            java.util.Iterator r8 = r8.iterator()
        L_0x01ec:
            boolean r11 = r8.hasNext()
            if (r11 == 0) goto L_0x0254
            java.lang.Object r11 = r8.next()
            org.telegram.tgnet.TLRPC$TL_availableReaction r11 = (org.telegram.tgnet.TLRPC.TL_availableReaction) r11
            org.telegram.tgnet.TLRPC$Document r12 = r11.static_icon
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0205
            goto L_0x0254
        L_0x0205:
            org.telegram.tgnet.TLRPC$Document r12 = r11.appear_animation
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0212
            goto L_0x0254
        L_0x0212:
            org.telegram.tgnet.TLRPC$Document r12 = r11.select_animation
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x021f
            goto L_0x0254
        L_0x021f:
            org.telegram.tgnet.TLRPC$Document r12 = r11.activate_animation
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x022c
            goto L_0x0254
        L_0x022c:
            org.telegram.tgnet.TLRPC$Document r12 = r11.effect_animation
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0239
            goto L_0x0254
        L_0x0239:
            org.telegram.tgnet.TLRPC$Document r12 = r11.around_animation
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0246
            goto L_0x0254
        L_0x0246:
            org.telegram.tgnet.TLRPC$Document r12 = r11.center_icon
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0253
            goto L_0x0254
        L_0x0253:
            goto L_0x01ec
        L_0x0254:
            r15 = r0
            r8 = r2
            r4 = r3
            r0 = 0
            goto L_0x061a
        L_0x025a:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_users_userFull
            if (r4 == 0) goto L_0x02a5
            r4 = r9
            org.telegram.tgnet.TLRPC$TL_users_userFull r4 = (org.telegram.tgnet.TLRPC.TL_users_userFull) r4
            org.telegram.messenger.MessagesController r8 = r31.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r4.users
            r12 = 0
            r8.putUsers(r11, r12)
            org.telegram.messenger.MessagesController r8 = r31.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r11 = r4.chats
            r8.putChats(r11, r12)
            org.telegram.tgnet.TLRPC$UserFull r8 = r4.full_user
            org.telegram.tgnet.TLRPC$BotInfo r11 = r8.bot_info
            if (r11 == 0) goto L_0x029f
            org.telegram.messenger.MessagesStorage r12 = r31.getMessagesStorage()
            r13 = 1
            r12.updateUserInfo(r8, r13)
            org.telegram.tgnet.TLRPC$Document r12 = r11.description_document
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0295
            r15 = r0
            r11 = 0
            r12 = 1
            r0 = r36
            goto L_0x0641
        L_0x0295:
            org.telegram.tgnet.TLRPC$Photo r12 = r11.description_photo
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Photo) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
        L_0x029f:
            r15 = r0
            r8 = r2
            r4 = r3
            r0 = 0
            goto L_0x061a
        L_0x02a5:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_attachMenuBotsBot
            if (r4 == 0) goto L_0x033e
            r4 = r9
            org.telegram.tgnet.TLRPC$TL_attachMenuBotsBot r4 = (org.telegram.tgnet.TLRPC.TL_attachMenuBotsBot) r4
            org.telegram.tgnet.TLRPC$TL_attachMenuBot r4 = r4.bot
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon> r8 = r4.icons
            java.util.Iterator r8 = r8.iterator()
        L_0x02b4:
            boolean r13 = r8.hasNext()
            if (r13 == 0) goto L_0x02d0
            java.lang.Object r13 = r8.next()
            org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon r13 = (org.telegram.tgnet.TLRPC.TL_attachMenuBotIcon) r13
            org.telegram.tgnet.TLRPC$Document r15 = r13.icon
            org.telegram.tgnet.TLRPC$InputFileLocation r11 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r15, (org.telegram.tgnet.TLRPC.InputFileLocation) r11, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x02cd
            goto L_0x02d0
        L_0x02cd:
            r11 = 1000(0x3e8, double:4.94E-321)
            goto L_0x02b4
        L_0x02d0:
            if (r35 == 0) goto L_0x032e
            org.telegram.messenger.MediaDataController r8 = r31.getMediaDataController()
            org.telegram.tgnet.TLRPC$TL_attachMenuBots r8 = r8.getAttachMenuBots()
            java.util.ArrayList r11 = new java.util.ArrayList
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_attachMenuBot> r12 = r8.bots
            r11.<init>(r12)
            r12 = 0
        L_0x02e2:
            int r13 = r11.size()
            if (r12 >= r13) goto L_0x030b
            java.lang.Object r13 = r11.get(r12)
            org.telegram.tgnet.TLRPC$TL_attachMenuBot r13 = (org.telegram.tgnet.TLRPC.TL_attachMenuBot) r13
            r15 = r0
            r18 = r1
            long r0 = r13.bot_id
            r20 = r2
            r30 = r3
            long r2 = r4.bot_id
            int r24 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r24 != 0) goto L_0x0301
            r11.set(r12, r4)
            goto L_0x0312
        L_0x0301:
            int r12 = r12 + 1
            r0 = r15
            r1 = r18
            r2 = r20
            r3 = r30
            goto L_0x02e2
        L_0x030b:
            r15 = r0
            r18 = r1
            r20 = r2
            r30 = r3
        L_0x0312:
            r8.bots = r11
            org.telegram.messenger.MediaDataController r24 = r31.getMediaDataController()
            long r0 = r8.hash
            long r2 = java.lang.System.currentTimeMillis()
            r12 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 / r12
            int r3 = (int) r2
            r29 = 0
            r25 = r8
            r26 = r0
            r28 = r3
            r24.processLoadedMenuBots(r25, r26, r28, r29)
            goto L_0x0335
        L_0x032e:
            r15 = r0
            r18 = r1
            r20 = r2
            r30 = r3
        L_0x0335:
            r1 = r18
            r8 = r20
            r4 = r30
            r0 = 0
            goto L_0x061a
        L_0x033e:
            r15 = r0
            r20 = r2
            r30 = r3
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_help_appUpdate
            if (r0 == 0) goto L_0x0367
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r0 = (org.telegram.tgnet.TLRPC.TL_help_appUpdate) r0
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r14.location
            r8 = r20
            r4 = r30
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r2, (org.telegram.tgnet.TLRPC.InputFileLocation) r3, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 != 0) goto L_0x0364
            org.telegram.tgnet.TLRPC$Document r2 = r0.sticker
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r2, (org.telegram.tgnet.TLRPC.InputFileLocation) r3, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
        L_0x0364:
            r0 = 0
            goto L_0x061a
        L_0x0367:
            r8 = r20
            r4 = r30
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.WebPage
            if (r0 == 0) goto L_0x037d
            r0 = r9
            org.telegram.tgnet.TLRPC$WebPage r0 = (org.telegram.tgnet.TLRPC.WebPage) r0
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.WebPage) r0, (org.telegram.tgnet.TLRPC.InputFileLocation) r2, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            r0 = 0
            goto L_0x061a
        L_0x037d:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_account_wallPapers
            if (r0 == 0) goto L_0x03b6
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_account_wallPapers r0 = (org.telegram.tgnet.TLRPC.TL_account_wallPapers) r0
            r2 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r3 = r0.wallpapers
            int r3 = r3.size()
        L_0x038b:
            if (r2 >= r3) goto L_0x03a5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r11 = r0.wallpapers
            java.lang.Object r11 = r11.get(r2)
            org.telegram.tgnet.TLRPC$WallPaper r11 = (org.telegram.tgnet.TLRPC.WallPaper) r11
            org.telegram.tgnet.TLRPC$Document r11 = r11.document
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r11, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 == 0) goto L_0x03a2
            goto L_0x03a5
        L_0x03a2:
            int r2 = r2 + 1
            goto L_0x038b
        L_0x03a5:
            if (r1 == 0) goto L_0x03b3
            if (r35 == 0) goto L_0x03b3
            org.telegram.messenger.MessagesStorage r2 = r31.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r3 = r0.wallpapers
            r11 = 1
            r2.putWallpapers(r3, r11)
        L_0x03b3:
            r0 = 0
            goto L_0x061a
        L_0x03b6:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r0 == 0) goto L_0x03de
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r0
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r2, (org.telegram.tgnet.TLRPC.InputFileLocation) r3, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 == 0) goto L_0x03db
            if (r35 == 0) goto L_0x03db
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r2.add(r0)
            org.telegram.messenger.MessagesStorage r3 = r31.getMessagesStorage()
            r11 = 0
            r3.putWallpapers(r2, r11)
        L_0x03db:
            r0 = 0
            goto L_0x061a
        L_0x03de:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_theme
            if (r0 == 0) goto L_0x03fe
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_theme r0 = (org.telegram.tgnet.TLRPC.TL_theme) r0
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r2, (org.telegram.tgnet.TLRPC.InputFileLocation) r3, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 == 0) goto L_0x03fb
            if (r35 == 0) goto L_0x03fb
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda36 r2 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda36
            r2.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x03fb:
            r0 = 0
            goto L_0x061a
        L_0x03fe:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.Vector
            if (r0 == 0) goto L_0x04a4
            r0 = r9
            org.telegram.tgnet.TLRPC$Vector r0 = (org.telegram.tgnet.TLRPC.Vector) r0
            java.util.ArrayList<java.lang.Object> r2 = r0.objects
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x049f
            r2 = 0
            java.util.ArrayList<java.lang.Object> r3 = r0.objects
            int r3 = r3.size()
        L_0x0414:
            if (r2 >= r3) goto L_0x049a
            java.util.ArrayList<java.lang.Object> r11 = r0.objects
            java.lang.Object r11 = r11.get(r2)
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.User
            if (r12 == 0) goto L_0x0458
            r12 = r11
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC.User) r12
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.User) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r35 == 0) goto L_0x044f
            if (r1 == 0) goto L_0x044f
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            r13.add(r12)
            r18 = r0
            org.telegram.messenger.MessagesStorage r0 = r31.getMessagesStorage()
            r20 = r1
            r22 = r3
            r1 = 0
            r3 = 1
            r0.putUsersAndChats(r13, r1, r3, r3)
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda34 r0 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda34
            r0.<init>(r6, r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0455
        L_0x044f:
            r18 = r0
            r20 = r1
            r22 = r3
        L_0x0455:
            r1 = r20
            goto L_0x048f
        L_0x0458:
            r18 = r0
            r22 = r3
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r0 == 0) goto L_0x048f
            r0 = r11
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Chat) r0, (org.telegram.tgnet.TLRPC.InputFileLocation) r3, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r35 == 0) goto L_0x048b
            if (r1 == 0) goto L_0x048b
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r3.add(r0)
            org.telegram.messenger.MessagesStorage r12 = r31.getMessagesStorage()
            r20 = r1
            r1 = 1
            r13 = 0
            r12.putUsersAndChats(r13, r3, r1, r1)
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda22 r1 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda22
            r1.<init>(r6, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x048d
        L_0x048b:
            r20 = r1
        L_0x048d:
            r1 = r20
        L_0x048f:
            if (r1 == 0) goto L_0x0492
            goto L_0x04a1
        L_0x0492:
            int r2 = r2 + 1
            r0 = r18
            r3 = r22
            goto L_0x0414
        L_0x049a:
            r18 = r0
            r22 = r3
            goto L_0x04a1
        L_0x049f:
            r18 = r0
        L_0x04a1:
            r0 = 0
            goto L_0x061a
        L_0x04a4:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_chats
            if (r0 == 0) goto L_0x0509
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_messages_chats r0 = (org.telegram.tgnet.TLRPC.TL_messages_chats) r0
            java.util.ArrayList r2 = r0.chats
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0504
            r2 = 0
            java.util.ArrayList r3 = r0.chats
            int r3 = r3.size()
        L_0x04ba:
            if (r2 >= r3) goto L_0x0500
            java.util.ArrayList r11 = r0.chats
            java.lang.Object r11 = r11.get(r2)
            org.telegram.tgnet.TLRPC$Chat r11 = (org.telegram.tgnet.TLRPC.Chat) r11
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Chat) r11, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 == 0) goto L_0x04f6
            if (r35 == 0) goto L_0x04ee
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            r12.add(r11)
            org.telegram.messenger.MessagesStorage r13 = r31.getMessagesStorage()
            r18 = r0
            r17 = r1
            r0 = 0
            r1 = 1
            r13.putUsersAndChats(r0, r12, r1, r1)
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda30 r1 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda30
            r1.<init>(r6, r11)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x04f3
        L_0x04ee:
            r18 = r0
            r17 = r1
            r0 = 0
        L_0x04f3:
            r1 = r17
            goto L_0x0507
        L_0x04f6:
            r18 = r0
            r17 = r1
            r0 = 0
            int r2 = r2 + 1
            r0 = r18
            goto L_0x04ba
        L_0x0500:
            r18 = r0
            r0 = 0
            goto L_0x0507
        L_0x0504:
            r18 = r0
            r0 = 0
        L_0x0507:
            goto L_0x061a
        L_0x0509:
            r0 = 0
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_savedGifs
            if (r2 == 0) goto L_0x0547
            r2 = r9
            org.telegram.tgnet.TLRPC$TL_messages_savedGifs r2 = (org.telegram.tgnet.TLRPC.TL_messages_savedGifs) r2
            r3 = 0
            java.util.ArrayList r11 = r2.gifs
            int r11 = r11.size()
        L_0x0518:
            if (r3 >= r11) goto L_0x0530
            java.util.ArrayList r12 = r2.gifs
            java.lang.Object r12 = r12.get(r3)
            org.telegram.tgnet.TLRPC$Document r12 = (org.telegram.tgnet.TLRPC.Document) r12
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 == 0) goto L_0x052d
            goto L_0x0530
        L_0x052d:
            int r3 = r3 + 1
            goto L_0x0518
        L_0x0530:
            if (r35 == 0) goto L_0x0545
            org.telegram.messenger.MediaDataController r22 = r31.getMediaDataController()
            r23 = 0
            java.util.ArrayList r3 = r2.gifs
            r25 = 1
            r26 = 0
            r27 = 1
            r24 = r3
            r22.processLoadedRecentDocuments(r23, r24, r25, r26, r27)
        L_0x0545:
            goto L_0x061a
        L_0x0547:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
            if (r2 == 0) goto L_0x057b
            r2 = r9
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r2
            if (r1 != 0) goto L_0x056f
            r3 = 0
            java.util.ArrayList r11 = r2.documents
            int r11 = r11.size()
        L_0x0557:
            if (r3 >= r11) goto L_0x056f
            java.util.ArrayList r12 = r2.documents
            java.lang.Object r12 = r12.get(r3)
            org.telegram.tgnet.TLRPC$Document r12 = (org.telegram.tgnet.TLRPC.Document) r12
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 == 0) goto L_0x056c
            goto L_0x056f
        L_0x056c:
            int r3 = r3 + 1
            goto L_0x0557
        L_0x056f:
            if (r35 == 0) goto L_0x0579
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda33 r3 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda33
            r3.<init>(r6, r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
        L_0x0579:
            goto L_0x061a
        L_0x057b:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_recentStickers
            if (r2 == 0) goto L_0x05b7
            r2 = r9
            org.telegram.tgnet.TLRPC$TL_messages_recentStickers r2 = (org.telegram.tgnet.TLRPC.TL_messages_recentStickers) r2
            r3 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r11 = r2.stickers
            int r11 = r11.size()
        L_0x0589:
            if (r3 >= r11) goto L_0x05a1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r12 = r2.stickers
            java.lang.Object r12 = r12.get(r3)
            org.telegram.tgnet.TLRPC$Document r12 = (org.telegram.tgnet.TLRPC.Document) r12
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 == 0) goto L_0x059e
            goto L_0x05a1
        L_0x059e:
            int r3 = r3 + 1
            goto L_0x0589
        L_0x05a1:
            if (r35 == 0) goto L_0x05b6
            org.telegram.messenger.MediaDataController r22 = r31.getMediaDataController()
            r23 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r2.stickers
            r25 = 0
            r26 = 0
            r27 = 1
            r24 = r3
            r22.processLoadedRecentDocuments(r23, r24, r25, r26, r27)
        L_0x05b6:
            goto L_0x061a
        L_0x05b7:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_favedStickers
            if (r2 == 0) goto L_0x05f3
            r2 = r9
            org.telegram.tgnet.TLRPC$TL_messages_favedStickers r2 = (org.telegram.tgnet.TLRPC.TL_messages_favedStickers) r2
            r3 = 0
            java.util.ArrayList r11 = r2.stickers
            int r11 = r11.size()
        L_0x05c5:
            if (r3 >= r11) goto L_0x05dd
            java.util.ArrayList r12 = r2.stickers
            java.lang.Object r12 = r12.get(r3)
            org.telegram.tgnet.TLRPC$Document r12 = (org.telegram.tgnet.TLRPC.Document) r12
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 == 0) goto L_0x05da
            goto L_0x05dd
        L_0x05da:
            int r3 = r3 + 1
            goto L_0x05c5
        L_0x05dd:
            if (r35 == 0) goto L_0x0619
            org.telegram.messenger.MediaDataController r22 = r31.getMediaDataController()
            r23 = 2
            java.util.ArrayList r3 = r2.stickers
            r25 = 0
            r26 = 0
            r27 = 1
            r24 = r3
            r22.processLoadedRecentDocuments(r23, r24, r25, r26, r27)
            goto L_0x0619
        L_0x05f3:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.photos_Photos
            if (r2 == 0) goto L_0x0619
            r2 = r9
            org.telegram.tgnet.TLRPC$photos_Photos r2 = (org.telegram.tgnet.TLRPC.photos_Photos) r2
            r3 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r11 = r2.photos
            int r11 = r11.size()
        L_0x0601:
            if (r3 >= r11) goto L_0x061a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r12 = r2.photos
            java.lang.Object r12 = r12.get(r3)
            org.telegram.tgnet.TLRPC$Photo r12 = (org.telegram.tgnet.TLRPC.Photo) r12
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Photo) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r4, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r8)
            if (r1 == 0) goto L_0x0616
            goto L_0x061a
        L_0x0616:
            int r3 = r3 + 1
            goto L_0x0601
        L_0x0619:
        L_0x061a:
            if (r1 == 0) goto L_0x0634
            if (r8 == 0) goto L_0x0622
            r11 = 0
            r12 = r8[r11]
            goto L_0x0624
        L_0x0622:
            r11 = 0
            r12 = r0
        L_0x0624:
            r0 = r36
            boolean r2 = r6.onUpdateObjectReference(r14, r1, r12, r0)
            if (r2 == 0) goto L_0x0632
            r2 = 1
            r15 = r2
            r3 = r4
            r2 = r8
            r12 = 1
            goto L_0x0641
        L_0x0632:
            r12 = 1
            goto L_0x063f
        L_0x0634:
            r0 = r36
            r11 = 0
            java.lang.Object[] r2 = r14.args
            r12 = 1
            r6.sendErrorToObject(r2, r12)
        L_0x063f:
            r3 = r4
            r2 = r8
        L_0x0641:
            int r10 = r10 + 1
            r8 = r33
            r0 = r15
            r4 = r19
            r13 = r21
            goto L_0x0094
        L_0x064c:
            r15 = r0
            r19 = r4
            r21 = r13
            r0 = r36
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r4 = r6.locationRequester
            r4.remove(r7)
            if (r15 == 0) goto L_0x065d
            r6.putReponseToCache(r5, r9)
        L_0x065d:
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.onRequestComplete(java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, boolean, boolean):boolean");
    }

    /* renamed from: lambda$onRequestComplete$33$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1834x97cCLASSNAME(TLRPC.User user) {
        getMessagesController().putUser(user, false);
    }

    /* renamed from: lambda$onRequestComplete$34$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1835x9var_ae41(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    /* renamed from: lambda$onRequestComplete$35$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1836xa696e360(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    /* renamed from: lambda$onRequestComplete$36$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m1837xadfCLASSNAMEf(TLRPC.TL_messages_stickerSet stickerSet) {
        getMediaDataController().replaceStickerSet(stickerSet);
    }

    private void cleanupCache() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastCleanupTime) >= 600000) {
            this.lastCleanupTime = SystemClock.elapsedRealtime();
            ArrayList<String> keysToDelete = null;
            for (Map.Entry<String, CachedResult> entry : this.responseCache.entrySet()) {
                if (Math.abs(SystemClock.elapsedRealtime() - entry.getValue().firstQueryTime) >= 600000) {
                    if (keysToDelete == null) {
                        keysToDelete = new ArrayList<>();
                    }
                    keysToDelete.add(entry.getKey());
                }
            }
            if (keysToDelete != null) {
                int size = keysToDelete.size();
                for (int a = 0; a < size; a++) {
                    this.responseCache.remove(keysToDelete.get(a));
                }
            }
        }
    }

    private CachedResult getCachedResponse(String key) {
        CachedResult cachedResult = this.responseCache.get(key);
        if (cachedResult == null || Math.abs(SystemClock.elapsedRealtime() - cachedResult.firstQueryTime) < 600000) {
            return cachedResult;
        }
        this.responseCache.remove(key);
        return null;
    }

    private void putReponseToCache(String key, TLObject response) {
        CachedResult cachedResult = this.responseCache.get(key);
        if (cachedResult == null) {
            cachedResult = new CachedResult();
            TLObject unused = cachedResult.response = response;
            long unused2 = cachedResult.firstQueryTime = SystemClock.uptimeMillis();
            this.responseCache.put(key, cachedResult);
        }
        long unused3 = cachedResult.lastQueryTime = SystemClock.uptimeMillis();
    }

    private byte[] getFileReference(TLRPC.Document document, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (document == null || location == null) {
            return null;
        }
        if (!(location instanceof TLRPC.TL_inputDocumentFileLocation)) {
            int a = 0;
            int size = document.thumbs.size();
            while (a < size) {
                TLRPC.PhotoSize photoSize = document.thumbs.get(a);
                byte[] result = getFileReference(photoSize, location, needReplacement);
                if (needReplacement != null && needReplacement[0]) {
                    replacement[0] = new TLRPC.TL_inputDocumentFileLocation();
                    replacement[0].id = document.id;
                    replacement[0].volume_id = location.volume_id;
                    replacement[0].local_id = location.local_id;
                    replacement[0].access_hash = document.access_hash;
                    replacement[0].file_reference = document.file_reference;
                    replacement[0].thumb_size = photoSize.type;
                    return document.file_reference;
                } else if (result != null) {
                    return result;
                } else {
                    a++;
                }
            }
        } else if (document.id == location.id) {
            return document.file_reference;
        }
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v10, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getPeerReferenceReplacement(org.telegram.tgnet.TLRPC.User r6, org.telegram.tgnet.TLRPC.Chat r7, boolean r8, org.telegram.tgnet.TLRPC.InputFileLocation r9, org.telegram.tgnet.TLRPC.InputFileLocation[] r10, boolean[] r11) {
        /*
            r5 = this;
            r0 = 0
            if (r11 == 0) goto L_0x005c
            boolean r1 = r11[r0]
            if (r1 == 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation
            r1.<init>()
            long r2 = r9.volume_id
            r1.id = r2
            long r2 = r9.volume_id
            r1.volume_id = r2
            int r2 = r9.local_id
            r1.local_id = r2
            r1.big = r8
            if (r6 == 0) goto L_0x0031
            org.telegram.tgnet.TLRPC$TL_inputPeerUser r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerUser
            r2.<init>()
            long r3 = r6.id
            r2.user_id = r3
            long r3 = r6.access_hash
            r2.access_hash = r3
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r6.photo
            long r3 = r3.photo_id
            r1.photo_id = r3
            goto L_0x0056
        L_0x0031:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r2 == 0) goto L_0x0046
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            r2.<init>()
            long r3 = r7.id
            r2.channel_id = r3
            long r3 = r7.access_hash
            r2.access_hash = r3
            goto L_0x0050
        L_0x0046:
            org.telegram.tgnet.TLRPC$TL_inputPeerChat r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerChat
            r2.<init>()
            long r3 = r7.id
            r2.chat_id = r3
            r3 = r2
        L_0x0050:
            org.telegram.tgnet.TLRPC$ChatPhoto r3 = r7.photo
            long r3 = r3.photo_id
            r1.photo_id = r3
        L_0x0056:
            r1.peer = r2
            r10[r0] = r1
            r0 = 1
            return r0
        L_0x005c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.getPeerReferenceReplacement(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean, org.telegram.tgnet.TLRPC$InputFileLocation, org.telegram.tgnet.TLRPC$InputFileLocation[], boolean[]):boolean");
    }

    private byte[] getFileReference(TLRPC.User user, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (user == null || user.photo == null || !(location instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        byte[] result = getFileReference(user.photo.photo_small, location, needReplacement);
        if (getPeerReferenceReplacement(user, (TLRPC.Chat) null, false, location, replacement, needReplacement)) {
            return new byte[0];
        }
        if (result == null) {
            result = getFileReference(user.photo.photo_big, location, needReplacement);
            if (getPeerReferenceReplacement(user, (TLRPC.Chat) null, true, location, replacement, needReplacement)) {
                return new byte[0];
            }
        }
        return result;
    }

    private byte[] getFileReference(TLRPC.Chat chat, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (chat == null || chat.photo == null || (!(location instanceof TLRPC.TL_inputFileLocation) && !(location instanceof TLRPC.TL_inputPeerPhotoFileLocation))) {
            return null;
        }
        if (location instanceof TLRPC.TL_inputPeerPhotoFileLocation) {
            needReplacement[0] = true;
            if (getPeerReferenceReplacement((TLRPC.User) null, chat, false, location, replacement, needReplacement)) {
                return new byte[0];
            }
            return null;
        }
        byte[] result = getFileReference(chat.photo.photo_small, location, needReplacement);
        if (getPeerReferenceReplacement((TLRPC.User) null, chat, false, location, replacement, needReplacement)) {
            return new byte[0];
        }
        if (result == null) {
            result = getFileReference(chat.photo.photo_big, location, needReplacement);
            if (getPeerReferenceReplacement((TLRPC.User) null, chat, true, location, replacement, needReplacement)) {
                return new byte[0];
            }
        }
        return result;
    }

    private byte[] getFileReference(TLRPC.Photo photo, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (photo == null) {
            return null;
        }
        if (!(location instanceof TLRPC.TL_inputPhotoFileLocation)) {
            if (location instanceof TLRPC.TL_inputFileLocation) {
                int a = 0;
                int size = photo.sizes.size();
                while (a < size) {
                    TLRPC.PhotoSize photoSize = photo.sizes.get(a);
                    byte[] result = getFileReference(photoSize, location, needReplacement);
                    if (needReplacement != null && needReplacement[0]) {
                        replacement[0] = new TLRPC.TL_inputPhotoFileLocation();
                        replacement[0].id = photo.id;
                        replacement[0].volume_id = location.volume_id;
                        replacement[0].local_id = location.local_id;
                        replacement[0].access_hash = photo.access_hash;
                        replacement[0].file_reference = photo.file_reference;
                        replacement[0].thumb_size = photoSize.type;
                        return photo.file_reference;
                    } else if (result != null) {
                        return result;
                    } else {
                        a++;
                    }
                }
            }
            return null;
        } else if (photo.id == location.id) {
            return photo.file_reference;
        } else {
            return null;
        }
    }

    private byte[] getFileReference(TLRPC.PhotoSize photoSize, TLRPC.InputFileLocation location, boolean[] needReplacement) {
        if (photoSize == null || !(location instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        return getFileReference(photoSize.location, location, needReplacement);
    }

    private byte[] getFileReference(TLRPC.FileLocation fileLocation, TLRPC.InputFileLocation location, boolean[] needReplacement) {
        if (fileLocation == null || !(location instanceof TLRPC.TL_inputFileLocation) || fileLocation.local_id != location.local_id || fileLocation.volume_id != location.volume_id) {
            return null;
        }
        if (fileLocation.file_reference == null && needReplacement != null) {
            needReplacement[0] = true;
        }
        return fileLocation.file_reference;
    }

    private byte[] getFileReference(TLRPC.WebPage webpage, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        byte[] result = getFileReference(webpage.document, location, needReplacement, replacement);
        if (result != null) {
            return result;
        }
        byte[] result2 = getFileReference(webpage.photo, location, needReplacement, replacement);
        if (result2 != null) {
            return result2;
        }
        if (!webpage.attributes.isEmpty()) {
            int size1 = webpage.attributes.size();
            for (int a = 0; a < size1; a++) {
                TLRPC.TL_webPageAttributeTheme attribute = webpage.attributes.get(a);
                int size2 = attribute.documents.size();
                for (int b = 0; b < size2; b++) {
                    byte[] result3 = getFileReference(attribute.documents.get(b), location, needReplacement, replacement);
                    if (result3 != null) {
                        return result3;
                    }
                }
            }
        }
        if (webpage.cached_page == null) {
            return null;
        }
        int size22 = webpage.cached_page.documents.size();
        for (int b2 = 0; b2 < size22; b2++) {
            byte[] result4 = getFileReference(webpage.cached_page.documents.get(b2), location, needReplacement, replacement);
            if (result4 != null) {
                return result4;
            }
        }
        int size23 = webpage.cached_page.photos.size();
        for (int b3 = 0; b3 < size23; b3++) {
            byte[] result5 = getFileReference(webpage.cached_page.photos.get(b3), location, needReplacement, replacement);
            if (result5 != null) {
                return result5;
            }
        }
        return null;
    }

    public static boolean isFileRefError(String error) {
        return "FILEREF_EXPIRED".equals(error) || "FILE_REFERENCE_EXPIRED".equals(error) || "FILE_REFERENCE_EMPTY".equals(error) || (error != null && error.startsWith("FILE_REFERENCE_"));
    }
}
