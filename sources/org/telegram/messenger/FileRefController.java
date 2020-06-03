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

    public void requestReference(Object obj, Object... objArr) {
        TLRPC$InputFileLocation tLRPC$InputFileLocation;
        String str;
        TLRPC$WebPage tLRPC$WebPage;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start loading request reference for parent = " + obj + " args = " + objArr[0]);
        }
        int i = 1;
        if (objArr[0] instanceof TLRPC$TL_inputSingleMedia) {
            TLRPC$InputMedia tLRPC$InputMedia = objArr[0].media;
            if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaDocument) {
                TLRPC$TL_inputMediaDocument tLRPC$TL_inputMediaDocument = (TLRPC$TL_inputMediaDocument) tLRPC$InputMedia;
                str = "file_" + tLRPC$TL_inputMediaDocument.id.id;
                tLRPC$InputFileLocation = new TLRPC$TL_inputDocumentFileLocation();
                tLRPC$InputFileLocation.id = tLRPC$TL_inputMediaDocument.id.id;
            } else if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaPhoto) {
                TLRPC$TL_inputMediaPhoto tLRPC$TL_inputMediaPhoto = (TLRPC$TL_inputMediaPhoto) tLRPC$InputMedia;
                str = "photo_" + tLRPC$TL_inputMediaPhoto.id.id;
                tLRPC$InputFileLocation = new TLRPC$TL_inputPhotoFileLocation();
                tLRPC$InputFileLocation.id = tLRPC$TL_inputMediaPhoto.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TLRPC$TL_messages_sendMultiMedia) {
            TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = objArr[0];
            ArrayList arrayList = (ArrayList) obj;
            this.multiMediaCache.put(tLRPC$TL_messages_sendMultiMedia, objArr);
            int size = tLRPC$TL_messages_sendMultiMedia.multi_media.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$TL_inputSingleMedia tLRPC$TL_inputSingleMedia = tLRPC$TL_messages_sendMultiMedia.multi_media.get(i2);
                Object obj2 = arrayList.get(i2);
                if (obj2 != null) {
                    requestReference(obj2, tLRPC$TL_inputSingleMedia, tLRPC$TL_messages_sendMultiMedia);
                }
            }
            return;
        } else if (objArr[0] instanceof TLRPC$TL_messages_sendMedia) {
            TLRPC$InputMedia tLRPC$InputMedia2 = objArr[0].media;
            if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaDocument) {
                TLRPC$TL_inputMediaDocument tLRPC$TL_inputMediaDocument2 = (TLRPC$TL_inputMediaDocument) tLRPC$InputMedia2;
                str = "file_" + tLRPC$TL_inputMediaDocument2.id.id;
                tLRPC$InputFileLocation = new TLRPC$TL_inputDocumentFileLocation();
                tLRPC$InputFileLocation.id = tLRPC$TL_inputMediaDocument2.id.id;
            } else if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaPhoto) {
                TLRPC$TL_inputMediaPhoto tLRPC$TL_inputMediaPhoto2 = (TLRPC$TL_inputMediaPhoto) tLRPC$InputMedia2;
                str = "photo_" + tLRPC$TL_inputMediaPhoto2.id.id;
                tLRPC$InputFileLocation = new TLRPC$TL_inputPhotoFileLocation();
                tLRPC$InputFileLocation.id = tLRPC$TL_inputMediaPhoto2.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TLRPC$TL_messages_editMessage) {
            TLRPC$InputMedia tLRPC$InputMedia3 = objArr[0].media;
            if (tLRPC$InputMedia3 instanceof TLRPC$TL_inputMediaDocument) {
                TLRPC$TL_inputMediaDocument tLRPC$TL_inputMediaDocument3 = (TLRPC$TL_inputMediaDocument) tLRPC$InputMedia3;
                str = "file_" + tLRPC$TL_inputMediaDocument3.id.id;
                tLRPC$InputFileLocation = new TLRPC$TL_inputDocumentFileLocation();
                tLRPC$InputFileLocation.id = tLRPC$TL_inputMediaDocument3.id.id;
            } else if (tLRPC$InputMedia3 instanceof TLRPC$TL_inputMediaPhoto) {
                TLRPC$TL_inputMediaPhoto tLRPC$TL_inputMediaPhoto3 = (TLRPC$TL_inputMediaPhoto) tLRPC$InputMedia3;
                str = "photo_" + tLRPC$TL_inputMediaPhoto3.id.id;
                tLRPC$InputFileLocation = new TLRPC$TL_inputPhotoFileLocation();
                tLRPC$InputFileLocation.id = tLRPC$TL_inputMediaPhoto3.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TLRPC$TL_messages_saveGif) {
            TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif = objArr[0];
            str = "file_" + tLRPC$TL_messages_saveGif.id.id;
            tLRPC$InputFileLocation = new TLRPC$TL_inputDocumentFileLocation();
            tLRPC$InputFileLocation.id = tLRPC$TL_messages_saveGif.id.id;
        } else if (objArr[0] instanceof TLRPC$TL_messages_saveRecentSticker) {
            TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker = objArr[0];
            str = "file_" + tLRPC$TL_messages_saveRecentSticker.id.id;
            tLRPC$InputFileLocation = new TLRPC$TL_inputDocumentFileLocation();
            tLRPC$InputFileLocation.id = tLRPC$TL_messages_saveRecentSticker.id.id;
        } else if (objArr[0] instanceof TLRPC$TL_messages_faveSticker) {
            TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker = objArr[0];
            str = "file_" + tLRPC$TL_messages_faveSticker.id.id;
            tLRPC$InputFileLocation = new TLRPC$TL_inputDocumentFileLocation();
            tLRPC$InputFileLocation.id = tLRPC$TL_messages_faveSticker.id.id;
        } else if (objArr[0] instanceof TLRPC$TL_messages_getAttachedStickers) {
            TLRPC$InputStickeredMedia tLRPC$InputStickeredMedia = objArr[0].media;
            if (tLRPC$InputStickeredMedia instanceof TLRPC$TL_inputStickeredMediaDocument) {
                TLRPC$TL_inputStickeredMediaDocument tLRPC$TL_inputStickeredMediaDocument = (TLRPC$TL_inputStickeredMediaDocument) tLRPC$InputStickeredMedia;
                str = "file_" + tLRPC$TL_inputStickeredMediaDocument.id.id;
                tLRPC$InputFileLocation = new TLRPC$TL_inputDocumentFileLocation();
                tLRPC$InputFileLocation.id = tLRPC$TL_inputStickeredMediaDocument.id.id;
            } else if (tLRPC$InputStickeredMedia instanceof TLRPC$TL_inputStickeredMediaPhoto) {
                TLRPC$TL_inputStickeredMediaPhoto tLRPC$TL_inputStickeredMediaPhoto = (TLRPC$TL_inputStickeredMediaPhoto) tLRPC$InputStickeredMedia;
                str = "photo_" + tLRPC$TL_inputStickeredMediaPhoto.id.id;
                tLRPC$InputFileLocation = new TLRPC$TL_inputPhotoFileLocation();
                tLRPC$InputFileLocation.id = tLRPC$TL_inputStickeredMediaPhoto.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TLRPC$TL_inputFileLocation) {
            tLRPC$InputFileLocation = (TLRPC$TL_inputFileLocation) objArr[0];
            str = "loc_" + tLRPC$InputFileLocation.local_id + "_" + tLRPC$InputFileLocation.volume_id;
        } else if (objArr[0] instanceof TLRPC$TL_inputDocumentFileLocation) {
            TLRPC$TL_inputDocumentFileLocation tLRPC$TL_inputDocumentFileLocation = objArr[0];
            str = "file_" + tLRPC$TL_inputDocumentFileLocation.id;
            tLRPC$InputFileLocation = tLRPC$TL_inputDocumentFileLocation;
        } else if (objArr[0] instanceof TLRPC$TL_inputPhotoFileLocation) {
            tLRPC$InputFileLocation = (TLRPC$TL_inputPhotoFileLocation) objArr[0];
            str = "photo_" + tLRPC$InputFileLocation.id;
        } else {
            sendErrorToObject(objArr, 0);
            return;
        }
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            if (messageObject.getRealId() < 0 && (tLRPC$WebPage = messageObject.messageOwner.media.webpage) != null) {
                obj = tLRPC$WebPage;
            }
        }
        String keyForParentObject = getKeyForParentObject(obj);
        if (keyForParentObject == null) {
            sendErrorToObject(objArr, 0);
            return;
        }
        Requester requester = new Requester();
        Object[] unused = requester.args = objArr;
        TLRPC$InputFileLocation unused2 = requester.location = tLRPC$InputFileLocation;
        String unused3 = requester.locationKey = str;
        ArrayList arrayList2 = this.locationRequester.get(str);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList();
            this.locationRequester.put(str, arrayList2);
        } else {
            i = 0;
        }
        arrayList2.add(requester);
        ArrayList arrayList3 = this.parentRequester.get(keyForParentObject);
        if (arrayList3 == null) {
            arrayList3 = new ArrayList();
            this.parentRequester.put(keyForParentObject, arrayList3);
            i++;
        }
        arrayList3.add(requester);
        if (i == 2) {
            cleanupCache();
            CachedResult cachedResponse = getCachedResponse(str);
            if (cachedResponse == null) {
                CachedResult cachedResponse2 = getCachedResponse(keyForParentObject);
                if (cachedResponse2 != null) {
                    if (!onRequestComplete(str, keyForParentObject, cachedResponse2.response, false)) {
                        this.responseCache.remove(keyForParentObject);
                    } else {
                        return;
                    }
                }
            } else if (!onRequestComplete(str, keyForParentObject, cachedResponse.response, false)) {
                this.responseCache.remove(str);
            } else {
                return;
            }
            requestReferenceFromServer(obj, str, keyForParentObject, objArr);
        }
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
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

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
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

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
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

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
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

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
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

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
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

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
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

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
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

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
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

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
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ String f$2;

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
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

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
                            private final /* synthetic */ String f$1;
                            private final /* synthetic */ String f$2;

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
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ String f$2;

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
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

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
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

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
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

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
                            private final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$1;
                            private final /* synthetic */ Object[] f$2;

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
                private final /* synthetic */ FileRefController.Requester f$1;

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
                private final /* synthetic */ FileRefController.Requester f$1;

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
                    private final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$1;
                    private final /* synthetic */ Object[] f$2;

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
                private final /* synthetic */ Object[] f$1;

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

    /* JADX WARNING: type inference failed for: r6v0 */
    /* JADX WARNING: type inference failed for: r6v2 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r6v1, types: [boolean, int] */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0104 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0152 A[LOOP:2: B:37:0x0099->B:73:0x0152, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean onRequestComplete(java.lang.String r23, java.lang.String r24, org.telegram.tgnet.TLObject r25, boolean r26) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r2 = r24
            r3 = r25
            r4 = 0
            r6 = 1
            if (r2 == 0) goto L_0x004b
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r7 = r0.parentRequester
            java.lang.Object r7 = r7.get(r2)
            java.util.ArrayList r7 = (java.util.ArrayList) r7
            if (r7 == 0) goto L_0x004b
            int r8 = r7.size()
            r9 = 0
            r10 = 0
        L_0x001c:
            if (r9 >= r8) goto L_0x0040
            java.lang.Object r11 = r7.get(r9)
            org.telegram.messenger.FileRefController$Requester r11 = (org.telegram.messenger.FileRefController.Requester) r11
            boolean r12 = r11.completed
            if (r12 == 0) goto L_0x002b
            goto L_0x003d
        L_0x002b:
            java.lang.String r11 = r11.locationKey
            if (r26 == 0) goto L_0x0035
            if (r10 != 0) goto L_0x0035
            r12 = 1
            goto L_0x0036
        L_0x0035:
            r12 = 0
        L_0x0036:
            boolean r11 = r0.onRequestComplete(r11, r4, r3, r12)
            if (r11 == 0) goto L_0x003d
            r10 = 1
        L_0x003d:
            int r9 = r9 + 1
            goto L_0x001c
        L_0x0040:
            if (r10 == 0) goto L_0x0045
            r0.putReponseToCache(r2, r3)
        L_0x0045:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r7 = r0.parentRequester
            r7.remove(r2)
            goto L_0x004c
        L_0x004b:
            r10 = 0
        L_0x004c:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r0.locationRequester
            java.lang.Object r2 = r2.get(r1)
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            if (r2 != 0) goto L_0x0057
            return r10
        L_0x0057:
            int r7 = r2.size()
            r9 = r4
            r11 = r9
            r12 = r11
            r8 = 0
        L_0x005f:
            if (r8 >= r7) goto L_0x0424
            java.lang.Object r13 = r2.get(r8)
            org.telegram.messenger.FileRefController$Requester r13 = (org.telegram.messenger.FileRefController.Requester) r13
            boolean r14 = r13.completed
            if (r14 == 0) goto L_0x0074
            r24 = r2
            r14 = r4
            r2 = 0
            r5 = 1
            goto L_0x041c
        L_0x0074:
            org.telegram.tgnet.TLRPC$InputFileLocation r14 = r13.location
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_inputFileLocation
            if (r14 == 0) goto L_0x0080
            org.telegram.tgnet.TLRPC$InputFileLocation[] r11 = new org.telegram.tgnet.TLRPC$InputFileLocation[r6]
            boolean[] r9 = new boolean[r6]
        L_0x0080:
            boolean unused = r13.completed = r6
            boolean r14 = r3 instanceof org.telegram.tgnet.TLRPC$messages_Messages
            if (r14 == 0) goto L_0x018a
            r14 = r3
            org.telegram.tgnet.TLRPC$messages_Messages r14 = (org.telegram.tgnet.TLRPC$messages_Messages) r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            boolean r15 = r15.isEmpty()
            if (r15 != 0) goto L_0x0187
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            int r15 = r15.size()
            r4 = 0
        L_0x0099:
            if (r4 >= r15) goto L_0x0159
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r6 = r14.messages
            java.lang.Object r6 = r6.get(r4)
            org.telegram.tgnet.TLRPC$Message r6 = (org.telegram.tgnet.TLRPC$Message) r6
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r6.media
            if (r5 == 0) goto L_0x00ef
            r24 = r2
            org.telegram.tgnet.TLRPC$Document r2 = r5.document
            if (r2 == 0) goto L_0x00b6
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            goto L_0x0101
        L_0x00b6:
            org.telegram.tgnet.TLRPC$TL_game r2 = r5.game
            if (r2 == 0) goto L_0x00d5
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r2 != 0) goto L_0x0101
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Photo) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            goto L_0x0101
        L_0x00d5:
            org.telegram.tgnet.TLRPC$Photo r2 = r5.photo
            if (r2 == 0) goto L_0x00e2
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Photo) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            goto L_0x0101
        L_0x00e2:
            org.telegram.tgnet.TLRPC$WebPage r2 = r5.webpage
            if (r2 == 0) goto L_0x0102
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$WebPage) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            goto L_0x0101
        L_0x00ef:
            r24 = r2
            org.telegram.tgnet.TLRPC$MessageAction r2 = r6.action
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r5 == 0) goto L_0x0102
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Photo) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
        L_0x0101:
            r12 = r2
        L_0x0102:
            if (r12 == 0) goto L_0x0152
            if (r26 == 0) goto L_0x015b
            org.telegram.tgnet.TLRPC$Peer r2 = r6.to_id
            if (r2 == 0) goto L_0x013a
            int r2 = r2.channel_id
            if (r2 == 0) goto L_0x013a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r14.chats
            int r2 = r2.size()
            r4 = 0
        L_0x0115:
            if (r4 >= r2) goto L_0x013a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r14.chats
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
            int r15 = r5.id
            r16 = r2
            org.telegram.tgnet.TLRPC$Peer r2 = r6.to_id
            int r2 = r2.channel_id
            if (r15 != r2) goto L_0x0135
            boolean r2 = r5.megagroup
            if (r2 == 0) goto L_0x013a
            int r2 = r6.flags
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            r2 = r2 | r4
            r6.flags = r2
            goto L_0x013a
        L_0x0135:
            int r4 = r4 + 1
            r2 = r16
            goto L_0x0115
        L_0x013a:
            org.telegram.messenger.MessagesStorage r16 = r22.getMessagesStorage()
            int r2 = r0.currentAccount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r14.chats
            r21 = 0
            r17 = r6
            r18 = r2
            r19 = r4
            r20 = r5
            r16.replaceMessageIfExists(r17, r18, r19, r20, r21)
            goto L_0x015b
        L_0x0152:
            int r4 = r4 + 1
            r2 = r24
            r6 = 1
            goto L_0x0099
        L_0x0159:
            r24 = r2
        L_0x015b:
            if (r12 != 0) goto L_0x019c
            org.telegram.messenger.MessagesStorage r16 = r22.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r14.messages
            r4 = 0
            java.lang.Object r2 = r2.get(r4)
            r17 = r2
            org.telegram.tgnet.TLRPC$Message r17 = (org.telegram.tgnet.TLRPC$Message) r17
            int r2 = r0.currentAccount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r14.chats
            r21 = 1
            r18 = r2
            r19 = r4
            r20 = r5
            r16.replaceMessageIfExists(r17, r18, r19, r20, r21)
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r2 == 0) goto L_0x019c
            java.lang.String r2 = "file ref not found in messages, replacing message"
            org.telegram.messenger.FileLog.d(r2)
            goto L_0x019c
        L_0x0187:
            r24 = r2
            goto L_0x019c
        L_0x018a:
            r24 = r2
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$WebPage
            if (r2 == 0) goto L_0x019f
            r2 = r3
            org.telegram.tgnet.TLRPC$WebPage r2 = (org.telegram.tgnet.TLRPC$WebPage) r2
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r13.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$WebPage) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r4, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            r12 = r2
        L_0x019c:
            r14 = 0
            goto L_0x0404
        L_0x019f:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_account_wallPapers
            if (r2 == 0) goto L_0x01d6
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_account_wallPapers r2 = (org.telegram.tgnet.TLRPC$TL_account_wallPapers) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r4 = r2.wallpapers
            int r4 = r4.size()
            r5 = 0
        L_0x01ad:
            if (r5 >= r4) goto L_0x01c7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r6 = r2.wallpapers
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$TL_wallPaper r6 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r6
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x01c4
            goto L_0x01c7
        L_0x01c4:
            int r5 = r5 + 1
            goto L_0x01ad
        L_0x01c7:
            if (r12 == 0) goto L_0x019c
            if (r26 == 0) goto L_0x019c
            org.telegram.messenger.MessagesStorage r4 = r22.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r2 = r2.wallpapers
            r5 = 1
            r4.putWallpapers(r2, r5)
            goto L_0x019c
        L_0x01d6:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r2 == 0) goto L_0x01fd
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r2
            org.telegram.tgnet.TLRPC$Document r4 = r2.document
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r4 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r4, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r4 == 0) goto L_0x01fb
            if (r26 == 0) goto L_0x01fb
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r5.add(r2)
            org.telegram.messenger.MessagesStorage r2 = r22.getMessagesStorage()
            r6 = 0
            r2.putWallpapers(r5, r6)
        L_0x01fb:
            r12 = r4
            goto L_0x019c
        L_0x01fd:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_theme
            if (r2 == 0) goto L_0x021b
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_theme r2 = (org.telegram.tgnet.TLRPC$TL_theme) r2
            org.telegram.tgnet.TLRPC$Document r4 = r2.document
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r4 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r4, (org.telegram.tgnet.TLRPC$InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r4 == 0) goto L_0x01fb
            if (r26 == 0) goto L_0x01fb
            org.telegram.messenger.-$$Lambda$FileRefController$SNJwoq0kr06ZjWyihuJvKSpuIz0 r5 = new org.telegram.messenger.-$$Lambda$FileRefController$SNJwoq0kr06ZjWyihuJvKSpuIz0
            r5.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5)
            goto L_0x01fb
        L_0x021b:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$Vector
            if (r2 == 0) goto L_0x02a9
            r2 = r3
            org.telegram.tgnet.TLRPC$Vector r2 = (org.telegram.tgnet.TLRPC$Vector) r2
            java.util.ArrayList<java.lang.Object> r4 = r2.objects
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x019c
            java.util.ArrayList<java.lang.Object> r4 = r2.objects
            int r4 = r4.size()
            r5 = 0
        L_0x0231:
            if (r5 >= r4) goto L_0x019c
            java.util.ArrayList<java.lang.Object> r6 = r2.objects
            java.lang.Object r6 = r6.get(r5)
            boolean r14 = r6 instanceof org.telegram.tgnet.TLRPC$User
            if (r14 == 0) goto L_0x026e
            org.telegram.tgnet.TLRPC$User r6 = (org.telegram.tgnet.TLRPC$User) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC$User) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r26 == 0) goto L_0x0269
            if (r12 == 0) goto L_0x0269
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            r14.add(r6)
            org.telegram.messenger.MessagesStorage r15 = r22.getMessagesStorage()
            r16 = r2
            r17 = r4
            r2 = 0
            r4 = 1
            r15.putUsersAndChats(r14, r2, r4, r4)
            org.telegram.messenger.-$$Lambda$FileRefController$_4y5prO3JWgmruc7MhfACo84xB8 r2 = new org.telegram.messenger.-$$Lambda$FileRefController$_4y5prO3JWgmruc7MhfACo84xB8
            r2.<init>(r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            goto L_0x029e
        L_0x0269:
            r16 = r2
            r17 = r4
            goto L_0x029e
        L_0x026e:
            r16 = r2
            r17 = r4
            boolean r2 = r6 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r2 == 0) goto L_0x029e
            org.telegram.tgnet.TLRPC$Chat r6 = (org.telegram.tgnet.TLRPC$Chat) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r13.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC$Chat) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r2, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r26 == 0) goto L_0x029d
            if (r2 == 0) goto L_0x029d
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r4.add(r6)
            org.telegram.messenger.MessagesStorage r12 = r22.getMessagesStorage()
            r14 = 0
            r15 = 1
            r12.putUsersAndChats(r14, r4, r15, r15)
            org.telegram.messenger.-$$Lambda$FileRefController$e8OtXvlo5kjEKEtnDDj1BG232-0 r4 = new org.telegram.messenger.-$$Lambda$FileRefController$e8OtXvlo5kjEKEtnDDj1BG232-0
            r4.<init>(r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
        L_0x029d:
            r12 = r2
        L_0x029e:
            if (r12 == 0) goto L_0x02a2
            goto L_0x019c
        L_0x02a2:
            int r5 = r5 + 1
            r2 = r16
            r4 = r17
            goto L_0x0231
        L_0x02a9:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_chats
            if (r2 == 0) goto L_0x02f4
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_chats r2 = (org.telegram.tgnet.TLRPC$TL_messages_chats) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r2.chats
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x019c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r2.chats
            int r4 = r4.size()
            r5 = 0
        L_0x02bf:
            if (r5 >= r4) goto L_0x019c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r2.chats
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Chat r6 = (org.telegram.tgnet.TLRPC$Chat) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC$Chat) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x02f0
            if (r26 == 0) goto L_0x019c
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r2.add(r6)
            org.telegram.messenger.MessagesStorage r4 = r22.getMessagesStorage()
            r5 = 1
            r14 = 0
            r4.putUsersAndChats(r14, r2, r5, r5)
            org.telegram.messenger.-$$Lambda$FileRefController$tUyca42NNsTxyQpe3m9-DSncaao r2 = new org.telegram.messenger.-$$Lambda$FileRefController$tUyca42NNsTxyQpe3m9-DSncaao
            r2.<init>(r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            goto L_0x0404
        L_0x02f0:
            r14 = 0
            int r5 = r5 + 1
            goto L_0x02bf
        L_0x02f4:
            r14 = 0
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_savedGifs
            if (r2 == 0) goto L_0x0332
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_savedGifs r2 = (org.telegram.tgnet.TLRPC$TL_messages_savedGifs) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.gifs
            int r4 = r4.size()
            r5 = 0
        L_0x0303:
            if (r5 >= r4) goto L_0x031b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r2.gifs
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC$Document) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x0318
            goto L_0x031b
        L_0x0318:
            int r5 = r5 + 1
            goto L_0x0303
        L_0x031b:
            if (r26 == 0) goto L_0x0404
            org.telegram.messenger.MediaDataController r16 = r22.getMediaDataController()
            r17 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r2.gifs
            r19 = 1
            r20 = 0
            r21 = 1
            r18 = r2
            r16.processLoadedRecentDocuments(r17, r18, r19, r20, r21)
            goto L_0x0404
        L_0x0332:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            if (r2 == 0) goto L_0x0366
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r2
            if (r12 != 0) goto L_0x035a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.documents
            int r4 = r4.size()
            r5 = 0
        L_0x0342:
            if (r5 >= r4) goto L_0x035a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r2.documents
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC$Document) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x0357
            goto L_0x035a
        L_0x0357:
            int r5 = r5 + 1
            goto L_0x0342
        L_0x035a:
            if (r26 == 0) goto L_0x0404
            org.telegram.messenger.-$$Lambda$FileRefController$q_nWD12uKB_1zPGAdz0IXAdyGJg r4 = new org.telegram.messenger.-$$Lambda$FileRefController$q_nWD12uKB_1zPGAdz0IXAdyGJg
            r4.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            goto L_0x0404
        L_0x0366:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_recentStickers
            if (r2 == 0) goto L_0x03a2
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_recentStickers r2 = (org.telegram.tgnet.TLRPC$TL_messages_recentStickers) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.stickers
            int r4 = r4.size()
            r5 = 0
        L_0x0374:
            if (r5 >= r4) goto L_0x038c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r2.stickers
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC$Document) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x0389
            goto L_0x038c
        L_0x0389:
            int r5 = r5 + 1
            goto L_0x0374
        L_0x038c:
            if (r26 == 0) goto L_0x0404
            org.telegram.messenger.MediaDataController r16 = r22.getMediaDataController()
            r17 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r2.stickers
            r19 = 0
            r20 = 0
            r21 = 1
            r18 = r2
            r16.processLoadedRecentDocuments(r17, r18, r19, r20, r21)
            goto L_0x0404
        L_0x03a2:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messages_favedStickers
            if (r2 == 0) goto L_0x03de
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_favedStickers r2 = (org.telegram.tgnet.TLRPC$TL_messages_favedStickers) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.stickers
            int r4 = r4.size()
            r5 = 0
        L_0x03b0:
            if (r5 >= r4) goto L_0x03c8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r2.stickers
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC$Document) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC$Document) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x03c5
            goto L_0x03c8
        L_0x03c5:
            int r5 = r5 + 1
            goto L_0x03b0
        L_0x03c8:
            if (r26 == 0) goto L_0x0404
            org.telegram.messenger.MediaDataController r16 = r22.getMediaDataController()
            r17 = 2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r2.stickers
            r19 = 0
            r20 = 0
            r21 = 1
            r18 = r2
            r16.processLoadedRecentDocuments(r17, r18, r19, r20, r21)
            goto L_0x0404
        L_0x03de:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$photos_Photos
            if (r2 == 0) goto L_0x0404
            r2 = r3
            org.telegram.tgnet.TLRPC$photos_Photos r2 = (org.telegram.tgnet.TLRPC$photos_Photos) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r4 = r2.photos
            int r4 = r4.size()
            r5 = 0
        L_0x03ec:
            if (r5 >= r4) goto L_0x0404
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r6 = r2.photos
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Photo r6 = (org.telegram.tgnet.TLRPC$Photo) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC$Photo) r6, (org.telegram.tgnet.TLRPC$InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x0401
            goto L_0x0404
        L_0x0401:
            int r5 = r5 + 1
            goto L_0x03ec
        L_0x0404:
            if (r12 == 0) goto L_0x0413
            r2 = 0
            if (r11 == 0) goto L_0x040c
            r4 = r11[r2]
            goto L_0x040d
        L_0x040c:
            r4 = r14
        L_0x040d:
            r0.onUpdateObjectReference(r13, r12, r4)
            r5 = 1
            r10 = 1
            goto L_0x041c
        L_0x0413:
            r2 = 0
            java.lang.Object[] r4 = r13.args
            r5 = 1
            r0.sendErrorToObject(r4, r5)
        L_0x041c:
            int r8 = r8 + 1
            r2 = r24
            r4 = r14
            r6 = 1
            goto L_0x005f
        L_0x0424:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r0.locationRequester
            r2.remove(r1)
            if (r10 == 0) goto L_0x042e
            r0.putReponseToCache(r1, r3)
        L_0x042e:
            return r10
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
