package org.telegram.messenger;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class FileRefController extends BaseController {
    private static volatile FileRefController[] Instance = new FileRefController[3];
    private long lastCleanupTime = SystemClock.elapsedRealtime();
    private HashMap<String, ArrayList<Requester>> locationRequester = new HashMap<>();
    private HashMap<TLRPC.TL_messages_sendMultiMedia, Object[]> multiMediaCache = new HashMap<>();
    private HashMap<String, ArrayList<Requester>> parentRequester = new HashMap<>();
    private HashMap<String, CachedResult> responseCache = new HashMap<>();

    static /* synthetic */ void lambda$onUpdateObjectReference$23(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$24(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$25(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    private class Requester {
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

    private class CachedResult {
        /* access modifiers changed from: private */
        public long firstQueryTime;
        /* access modifiers changed from: private */
        public long lastQueryTime;
        /* access modifiers changed from: private */
        public TLObject response;

        private CachedResult() {
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
        } else if (obj instanceof TLRPC.Message) {
            TLRPC.Message message = (TLRPC.Message) obj;
            TLRPC.Peer peer = message.to_id;
            int i = peer != null ? peer.channel_id : 0;
            return "message" + message.id + "_" + i;
        } else if (obj instanceof TLRPC.WebPage) {
            return "webpage" + ((TLRPC.WebPage) obj).id;
        } else if (obj instanceof TLRPC.User) {
            return "user" + ((TLRPC.User) obj).id;
        } else if (obj instanceof TLRPC.Chat) {
            return "chat" + ((TLRPC.Chat) obj).id;
        } else if (obj instanceof String) {
            return "str" + ((String) obj);
        } else if (obj instanceof TLRPC.TL_messages_stickerSet) {
            return "set" + ((TLRPC.TL_messages_stickerSet) obj).set.id;
        } else if (obj instanceof TLRPC.StickerSetCovered) {
            return "set" + ((TLRPC.StickerSetCovered) obj).set.id;
        } else if (obj instanceof TLRPC.InputStickerSet) {
            return "set" + ((TLRPC.InputStickerSet) obj).id;
        } else if (obj instanceof TLRPC.TL_wallPaper) {
            return "wallpaper" + ((TLRPC.TL_wallPaper) obj).id;
        } else if (obj instanceof TLRPC.TL_theme) {
            return "theme" + ((TLRPC.TL_theme) obj).id;
        } else if (obj == null) {
            return null;
        } else {
            return "" + obj;
        }
    }

    public void requestReference(Object obj, Object... objArr) {
        TLRPC.InputFileLocation inputFileLocation;
        String str;
        TLRPC.WebPage webPage;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start loading request reference for parent = " + obj + " args = " + objArr[0]);
        }
        int i = 1;
        if (objArr[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.InputMedia inputMedia = objArr[0].media;
            if (inputMedia instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument tL_inputMediaDocument = (TLRPC.TL_inputMediaDocument) inputMedia;
                str = "file_" + tL_inputMediaDocument.id.id;
                inputFileLocation = new TLRPC.TL_inputDocumentFileLocation();
                inputFileLocation.id = tL_inputMediaDocument.id.id;
            } else if (inputMedia instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto = (TLRPC.TL_inputMediaPhoto) inputMedia;
                str = "photo_" + tL_inputMediaPhoto.id.id;
                inputFileLocation = new TLRPC.TL_inputPhotoFileLocation();
                inputFileLocation.id = tL_inputMediaPhoto.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TLRPC.TL_messages_sendMultiMedia) {
            TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = objArr[0];
            ArrayList arrayList = (ArrayList) obj;
            this.multiMediaCache.put(tL_messages_sendMultiMedia, objArr);
            int size = tL_messages_sendMultiMedia.multi_media.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC.TL_inputSingleMedia tL_inputSingleMedia = tL_messages_sendMultiMedia.multi_media.get(i2);
                Object obj2 = arrayList.get(i2);
                if (obj2 != null) {
                    requestReference(obj2, tL_inputSingleMedia, tL_messages_sendMultiMedia);
                }
            }
            return;
        } else if (objArr[0] instanceof TLRPC.TL_messages_sendMedia) {
            TLRPC.InputMedia inputMedia2 = objArr[0].media;
            if (inputMedia2 instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument tL_inputMediaDocument2 = (TLRPC.TL_inputMediaDocument) inputMedia2;
                str = "file_" + tL_inputMediaDocument2.id.id;
                inputFileLocation = new TLRPC.TL_inputDocumentFileLocation();
                inputFileLocation.id = tL_inputMediaDocument2.id.id;
            } else if (inputMedia2 instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto2 = (TLRPC.TL_inputMediaPhoto) inputMedia2;
                str = "photo_" + tL_inputMediaPhoto2.id.id;
                inputFileLocation = new TLRPC.TL_inputPhotoFileLocation();
                inputFileLocation.id = tL_inputMediaPhoto2.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TLRPC.TL_messages_editMessage) {
            TLRPC.InputMedia inputMedia3 = objArr[0].media;
            if (inputMedia3 instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument tL_inputMediaDocument3 = (TLRPC.TL_inputMediaDocument) inputMedia3;
                str = "file_" + tL_inputMediaDocument3.id.id;
                inputFileLocation = new TLRPC.TL_inputDocumentFileLocation();
                inputFileLocation.id = tL_inputMediaDocument3.id.id;
            } else if (inputMedia3 instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto3 = (TLRPC.TL_inputMediaPhoto) inputMedia3;
                str = "photo_" + tL_inputMediaPhoto3.id.id;
                inputFileLocation = new TLRPC.TL_inputPhotoFileLocation();
                inputFileLocation.id = tL_inputMediaPhoto3.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif tL_messages_saveGif = objArr[0];
            str = "file_" + tL_messages_saveGif.id.id;
            inputFileLocation = new TLRPC.TL_inputDocumentFileLocation();
            inputFileLocation.id = tL_messages_saveGif.id.id;
        } else if (objArr[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = objArr[0];
            str = "file_" + tL_messages_saveRecentSticker.id.id;
            inputFileLocation = new TLRPC.TL_inputDocumentFileLocation();
            inputFileLocation.id = tL_messages_saveRecentSticker.id.id;
        } else if (objArr[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker tL_messages_faveSticker = objArr[0];
            str = "file_" + tL_messages_faveSticker.id.id;
            inputFileLocation = new TLRPC.TL_inputDocumentFileLocation();
            inputFileLocation.id = tL_messages_faveSticker.id.id;
        } else if (objArr[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            TLRPC.InputStickeredMedia inputStickeredMedia = objArr[0].media;
            if (inputStickeredMedia instanceof TLRPC.TL_inputStickeredMediaDocument) {
                TLRPC.TL_inputStickeredMediaDocument tL_inputStickeredMediaDocument = (TLRPC.TL_inputStickeredMediaDocument) inputStickeredMedia;
                str = "file_" + tL_inputStickeredMediaDocument.id.id;
                inputFileLocation = new TLRPC.TL_inputDocumentFileLocation();
                inputFileLocation.id = tL_inputStickeredMediaDocument.id.id;
            } else if (inputStickeredMedia instanceof TLRPC.TL_inputStickeredMediaPhoto) {
                TLRPC.TL_inputStickeredMediaPhoto tL_inputStickeredMediaPhoto = (TLRPC.TL_inputStickeredMediaPhoto) inputStickeredMedia;
                str = "photo_" + tL_inputStickeredMediaPhoto.id.id;
                inputFileLocation = new TLRPC.TL_inputPhotoFileLocation();
                inputFileLocation.id = tL_inputStickeredMediaPhoto.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TLRPC.TL_inputFileLocation) {
            inputFileLocation = (TLRPC.TL_inputFileLocation) objArr[0];
            str = "loc_" + inputFileLocation.local_id + "_" + inputFileLocation.volume_id;
        } else if (objArr[0] instanceof TLRPC.TL_inputDocumentFileLocation) {
            TLRPC.TL_inputDocumentFileLocation tL_inputDocumentFileLocation = objArr[0];
            str = "file_" + tL_inputDocumentFileLocation.id;
            inputFileLocation = tL_inputDocumentFileLocation;
        } else if (objArr[0] instanceof TLRPC.TL_inputPhotoFileLocation) {
            inputFileLocation = (TLRPC.TL_inputPhotoFileLocation) objArr[0];
            str = "photo_" + inputFileLocation.id;
        } else {
            sendErrorToObject(objArr, 0);
            return;
        }
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            if (messageObject.getRealId() < 0 && (webPage = messageObject.messageOwner.media.webpage) != null) {
                obj = webPage;
            }
        }
        String keyForParentObject = getKeyForParentObject(obj);
        if (keyForParentObject == null) {
            sendErrorToObject(objArr, 0);
            return;
        }
        Requester requester = new Requester();
        Object[] unused = requester.args = objArr;
        TLRPC.InputFileLocation unused2 = requester.location = inputFileLocation;
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

    private void requestReferenceFromServer(Object obj, String str, String str2, Object[] objArr) {
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            int channelId = messageObject.getChannelId();
            if (messageObject.scheduled) {
                TLRPC.TL_messages_getScheduledMessages tL_messages_getScheduledMessages = new TLRPC.TL_messages_getScheduledMessages();
                tL_messages_getScheduledMessages.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
                tL_messages_getScheduledMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tL_messages_getScheduledMessages, new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$0$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (channelId != 0) {
                TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
                tL_channels_getMessages.channel = getMessagesController().getInputChannel(channelId);
                tL_channels_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tL_channels_getMessages, new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$1$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else {
                TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
                tL_messages_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tL_messages_getMessages, new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$2$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            }
        } else if (obj instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) obj;
            TLRPC.TL_account_getWallPaper tL_account_getWallPaper = new TLRPC.TL_account_getWallPaper();
            TLRPC.TL_inputWallPaper tL_inputWallPaper = new TLRPC.TL_inputWallPaper();
            tL_inputWallPaper.id = tL_wallPaper.id;
            tL_inputWallPaper.access_hash = tL_wallPaper.access_hash;
            tL_account_getWallPaper.wallpaper = tL_inputWallPaper;
            getConnectionsManager().sendRequest(tL_account_getWallPaper, new RequestDelegate(str, str2) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$3$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (obj instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme tL_theme = (TLRPC.TL_theme) obj;
            TLRPC.TL_account_getTheme tL_account_getTheme = new TLRPC.TL_account_getTheme();
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            tL_account_getTheme.theme = tL_inputTheme;
            tL_account_getTheme.format = "android";
            getConnectionsManager().sendRequest(tL_account_getTheme, new RequestDelegate(str, str2) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$4$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (obj instanceof TLRPC.WebPage) {
            TLRPC.TL_messages_getWebPage tL_messages_getWebPage = new TLRPC.TL_messages_getWebPage();
            tL_messages_getWebPage.url = ((TLRPC.WebPage) obj).url;
            tL_messages_getWebPage.hash = 0;
            getConnectionsManager().sendRequest(tL_messages_getWebPage, new RequestDelegate(str, str2) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$5$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (obj instanceof TLRPC.User) {
            TLRPC.TL_users_getUsers tL_users_getUsers = new TLRPC.TL_users_getUsers();
            tL_users_getUsers.id.add(getMessagesController().getInputUser((TLRPC.User) obj));
            getConnectionsManager().sendRequest(tL_users_getUsers, new RequestDelegate(str, str2) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$6$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (obj instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) obj;
            if (chat instanceof TLRPC.TL_chat) {
                TLRPC.TL_messages_getChats tL_messages_getChats = new TLRPC.TL_messages_getChats();
                tL_messages_getChats.id.add(Integer.valueOf(chat.id));
                getConnectionsManager().sendRequest(tL_messages_getChats, new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$7$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (chat instanceof TLRPC.TL_channel) {
                TLRPC.TL_channels_getChannels tL_channels_getChannels = new TLRPC.TL_channels_getChannels();
                tL_channels_getChannels.id.add(MessagesController.getInputChannel(chat));
                getConnectionsManager().sendRequest(tL_channels_getChannels, new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$8$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            }
        } else if (obj instanceof String) {
            String str3 = (String) obj;
            if ("wallpaper".equals(str3)) {
                getConnectionsManager().sendRequest(new TLRPC.TL_account_getWallPapers(), new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$9$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (str3.startsWith("gif")) {
                getConnectionsManager().sendRequest(new TLRPC.TL_messages_getSavedGifs(), new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$10$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if ("recent".equals(str3)) {
                getConnectionsManager().sendRequest(new TLRPC.TL_messages_getRecentStickers(), new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$11$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if ("fav".equals(str3)) {
                getConnectionsManager().sendRequest(new TLRPC.TL_messages_getFavedStickers(), new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$12$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (str3.startsWith("avatar_")) {
                int intValue = Utilities.parseInt(str3).intValue();
                if (intValue > 0) {
                    TLRPC.TL_photos_getUserPhotos tL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
                    tL_photos_getUserPhotos.limit = 80;
                    tL_photos_getUserPhotos.offset = 0;
                    tL_photos_getUserPhotos.max_id = 0;
                    tL_photos_getUserPhotos.user_id = getMessagesController().getInputUser(intValue);
                    getConnectionsManager().sendRequest(tL_photos_getUserPhotos, new RequestDelegate(str, str2) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ String f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$13$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    });
                    return;
                }
                TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
                tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
                tL_messages_search.limit = 80;
                tL_messages_search.offset_id = 0;
                tL_messages_search.q = "";
                tL_messages_search.peer = getMessagesController().getInputPeer(intValue);
                getConnectionsManager().sendRequest(tL_messages_search, new RequestDelegate(str, str2) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$14$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (str3.startsWith("sent_")) {
                String[] split = str3.split("_");
                if (split.length == 3) {
                    int intValue2 = Utilities.parseInt(split[1]).intValue();
                    if (intValue2 != 0) {
                        TLRPC.TL_channels_getMessages tL_channels_getMessages2 = new TLRPC.TL_channels_getMessages();
                        tL_channels_getMessages2.channel = getMessagesController().getInputChannel(intValue2);
                        tL_channels_getMessages2.id.add(Utilities.parseInt(split[2]));
                        getConnectionsManager().sendRequest(tL_channels_getMessages2, new RequestDelegate(str, str2) {
                            private final /* synthetic */ String f$1;
                            private final /* synthetic */ String f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$15$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                            }
                        });
                        return;
                    }
                    TLRPC.TL_messages_getMessages tL_messages_getMessages2 = new TLRPC.TL_messages_getMessages();
                    tL_messages_getMessages2.id.add(Utilities.parseInt(split[2]));
                    getConnectionsManager().sendRequest(tL_messages_getMessages2, new RequestDelegate(str, str2) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ String f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$16$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    });
                    return;
                }
                sendErrorToObject(objArr, 0);
            } else {
                sendErrorToObject(objArr, 0);
            }
        } else if (obj instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet = new TLRPC.TL_messages_getStickerSet();
            tL_messages_getStickerSet.stickerset = new TLRPC.TL_inputStickerSetID();
            TLRPC.InputStickerSet inputStickerSet = tL_messages_getStickerSet.stickerset;
            TLRPC.StickerSet stickerSet = ((TLRPC.TL_messages_stickerSet) obj).set;
            inputStickerSet.id = stickerSet.id;
            inputStickerSet.access_hash = stickerSet.access_hash;
            getConnectionsManager().sendRequest(tL_messages_getStickerSet, new RequestDelegate(str, str2) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$17$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (obj instanceof TLRPC.StickerSetCovered) {
            TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet2 = new TLRPC.TL_messages_getStickerSet();
            tL_messages_getStickerSet2.stickerset = new TLRPC.TL_inputStickerSetID();
            TLRPC.InputStickerSet inputStickerSet2 = tL_messages_getStickerSet2.stickerset;
            TLRPC.StickerSet stickerSet2 = ((TLRPC.StickerSetCovered) obj).set;
            inputStickerSet2.id = stickerSet2.id;
            inputStickerSet2.access_hash = stickerSet2.access_hash;
            getConnectionsManager().sendRequest(tL_messages_getStickerSet2, new RequestDelegate(str, str2) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$18$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (obj instanceof TLRPC.InputStickerSet) {
            TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet3 = new TLRPC.TL_messages_getStickerSet();
            tL_messages_getStickerSet3.stickerset = (TLRPC.InputStickerSet) obj;
            getConnectionsManager().sendRequest(tL_messages_getStickerSet3, new RequestDelegate(str, str2) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$19$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else {
            sendErrorToObject(objArr, 0);
        }
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$0$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$1$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$2$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$3$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$4$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$5$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$6$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$7$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$8$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$9$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$10$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$11$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$12$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$13$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$14$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$15$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, false);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$16$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, false);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$17$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$18$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$19$FileRefController(String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    private void onUpdateObjectReference(Requester requester, byte[] bArr, TLRPC.InputFileLocation inputFileLocation) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("fileref updated for " + requester.args[0] + " " + requester.locationKey);
        }
        boolean z = true;
        if (requester.args[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TLRPC.TL_messages_sendMultiMedia) requester.args[1];
            Object[] objArr = this.multiMediaCache.get(tL_messages_sendMultiMedia);
            if (objArr != null) {
                TLRPC.TL_inputSingleMedia tL_inputSingleMedia = (TLRPC.TL_inputSingleMedia) requester.args[0];
                TLRPC.InputMedia inputMedia = tL_inputSingleMedia.media;
                if (inputMedia instanceof TLRPC.TL_inputMediaDocument) {
                    ((TLRPC.TL_inputMediaDocument) inputMedia).id.file_reference = bArr;
                } else if (inputMedia instanceof TLRPC.TL_inputMediaPhoto) {
                    ((TLRPC.TL_inputMediaPhoto) inputMedia).id.file_reference = bArr;
                }
                int indexOf = tL_messages_sendMultiMedia.multi_media.indexOf(tL_inputSingleMedia);
                if (indexOf >= 0) {
                    ArrayList arrayList = (ArrayList) objArr[3];
                    arrayList.set(indexOf, (Object) null);
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i) != null) {
                            z = false;
                        }
                    }
                    if (z) {
                        this.multiMediaCache.remove(tL_messages_sendMultiMedia);
                        AndroidUtilities.runOnUIThread(new Runnable(tL_messages_sendMultiMedia, objArr) {
                            private final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$1;
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
        } else if (requester.args[0] instanceof TLRPC.TL_messages_sendMedia) {
            TLRPC.InputMedia inputMedia2 = ((TLRPC.TL_messages_sendMedia) requester.args[0]).media;
            if (inputMedia2 instanceof TLRPC.TL_inputMediaDocument) {
                ((TLRPC.TL_inputMediaDocument) inputMedia2).id.file_reference = bArr;
            } else if (inputMedia2 instanceof TLRPC.TL_inputMediaPhoto) {
                ((TLRPC.TL_inputMediaPhoto) inputMedia2).id.file_reference = bArr;
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
        } else if (requester.args[0] instanceof TLRPC.TL_messages_editMessage) {
            TLRPC.InputMedia inputMedia3 = ((TLRPC.TL_messages_editMessage) requester.args[0]).media;
            if (inputMedia3 instanceof TLRPC.TL_inputMediaDocument) {
                ((TLRPC.TL_inputMediaDocument) inputMedia3).id.file_reference = bArr;
            } else if (inputMedia3 instanceof TLRPC.TL_inputMediaPhoto) {
                ((TLRPC.TL_inputMediaPhoto) inputMedia3).id.file_reference = bArr;
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
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif tL_messages_saveGif = (TLRPC.TL_messages_saveGif) requester.args[0];
            tL_messages_saveGif.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tL_messages_saveGif, $$Lambda$FileRefController$B8e86bveS3knOZa0Cg1HvRKtz44.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = (TLRPC.TL_messages_saveRecentSticker) requester.args[0];
            tL_messages_saveRecentSticker.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tL_messages_saveRecentSticker, $$Lambda$FileRefController$9VZxhYR0Ao1KW4hoVDK7k1sUZVI.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker tL_messages_faveSticker = (TLRPC.TL_messages_faveSticker) requester.args[0];
            tL_messages_faveSticker.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tL_messages_faveSticker, $$Lambda$FileRefController$9vHgXK5LhN8UmJHe149yk5CGvC8.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            TLRPC.TL_messages_getAttachedStickers tL_messages_getAttachedStickers = (TLRPC.TL_messages_getAttachedStickers) requester.args[0];
            TLRPC.InputStickeredMedia inputStickeredMedia = tL_messages_getAttachedStickers.media;
            if (inputStickeredMedia instanceof TLRPC.TL_inputStickeredMediaDocument) {
                ((TLRPC.TL_inputStickeredMediaDocument) inputStickeredMedia).id.file_reference = bArr;
            } else if (inputStickeredMedia instanceof TLRPC.TL_inputStickeredMediaPhoto) {
                ((TLRPC.TL_inputStickeredMediaPhoto) inputStickeredMedia).id.file_reference = bArr;
            }
            getConnectionsManager().sendRequest(tL_messages_getAttachedStickers, (RequestDelegate) requester.args[1]);
        } else if (requester.args[1] instanceof FileLoadOperation) {
            FileLoadOperation fileLoadOperation = (FileLoadOperation) requester.args[1];
            if (inputFileLocation != null) {
                fileLoadOperation.location = inputFileLocation;
            } else {
                requester.location.file_reference = bArr;
            }
            fileLoadOperation.requestingReference = false;
            fileLoadOperation.startDownloadRequest();
        }
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$20$FileRefController(TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, objArr[1], objArr[2], (ArrayList<Object>) null, objArr[4], objArr[5].booleanValue());
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$21$FileRefController(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, ((Boolean) requester.args[6]).booleanValue());
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$22$FileRefController(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, ((Boolean) requester.args[6]).booleanValue());
    }

    private void sendErrorToObject(Object[] objArr, int i) {
        if (objArr[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = objArr[1];
            Object[] objArr2 = this.multiMediaCache.get(tL_messages_sendMultiMedia);
            if (objArr2 != null) {
                this.multiMediaCache.remove(tL_messages_sendMultiMedia);
                AndroidUtilities.runOnUIThread(new Runnable(tL_messages_sendMultiMedia, objArr2) {
                    private final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$1;
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
        } else if ((objArr[0] instanceof TLRPC.TL_messages_sendMedia) || (objArr[0] instanceof TLRPC.TL_messages_editMessage)) {
            AndroidUtilities.runOnUIThread(new Runnable(objArr) {
                private final /* synthetic */ Object[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileRefController.this.lambda$sendErrorToObject$27$FileRefController(this.f$1);
                }
            });
        } else if (objArr[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif tL_messages_saveGif = objArr[0];
        } else if (objArr[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = objArr[0];
        } else if (objArr[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker tL_messages_faveSticker = objArr[0];
        } else if (objArr[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            getConnectionsManager().sendRequest(objArr[0], objArr[1]);
        } else if (i == 0) {
            TLRPC.TL_error tL_error = new TLRPC.TL_error();
            tL_error.text = "not found parent object to request reference";
            tL_error.code = 400;
            if (objArr[1] instanceof FileLoadOperation) {
                FileLoadOperation fileLoadOperation = objArr[1];
                fileLoadOperation.requestingReference = false;
                fileLoadOperation.processRequestResult(objArr[2], tL_error);
            }
        } else if (i == 1 && (objArr[1] instanceof FileLoadOperation)) {
            FileLoadOperation fileLoadOperation2 = objArr[1];
            fileLoadOperation2.requestingReference = false;
            fileLoadOperation2.onFail(false, 0);
        }
    }

    public /* synthetic */ void lambda$sendErrorToObject$26$FileRefController(TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, objArr[1], objArr[2], (ArrayList<Object>) null, objArr[4], objArr[5].booleanValue());
    }

    public /* synthetic */ void lambda$sendErrorToObject$27$FileRefController(Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequest(objArr[0], objArr[1], objArr[2], objArr[3], objArr[4].booleanValue(), objArr[5], (Object) null, objArr[6].booleanValue());
    }

    /* JADX WARNING: type inference failed for: r6v0 */
    /* JADX WARNING: type inference failed for: r6v2 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r6v1, types: [int, boolean] */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0415  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0103 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0153 A[LOOP:2: B:37:0x009b->B:74:0x0153, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean onRequestComplete(java.lang.String r25, java.lang.String r26, org.telegram.tgnet.TLObject r27, boolean r28) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            r2 = r26
            r3 = r27
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
            if (r28 == 0) goto L_0x0035
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
            if (r8 >= r7) goto L_0x0426
            java.lang.Object r13 = r2.get(r8)
            org.telegram.messenger.FileRefController$Requester r13 = (org.telegram.messenger.FileRefController.Requester) r13
            boolean r14 = r13.completed
            if (r14 == 0) goto L_0x0074
            r26 = r2
            r14 = r4
            r2 = 0
            r5 = 1
            goto L_0x041e
        L_0x0074:
            org.telegram.tgnet.TLRPC$InputFileLocation r14 = r13.location
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_inputFileLocation
            if (r14 == 0) goto L_0x0080
            org.telegram.tgnet.TLRPC$InputFileLocation[] r11 = new org.telegram.tgnet.TLRPC.InputFileLocation[r6]
            boolean[] r9 = new boolean[r6]
        L_0x0080:
            boolean unused = r13.completed = r6
            boolean r14 = r3 instanceof org.telegram.tgnet.TLRPC.messages_Messages
            if (r14 == 0) goto L_0x018e
            r14 = r3
            org.telegram.tgnet.TLRPC$messages_Messages r14 = (org.telegram.tgnet.TLRPC.messages_Messages) r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            boolean r15 = r15.isEmpty()
            if (r15 != 0) goto L_0x018b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            int r15 = r15.size()
            r16 = r12
            r12 = 0
        L_0x009b:
            if (r12 >= r15) goto L_0x015b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r14.messages
            java.lang.Object r4 = r4.get(r12)
            org.telegram.tgnet.TLRPC$Message r4 = (org.telegram.tgnet.TLRPC.Message) r4
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r4.media
            if (r6 == 0) goto L_0x00ef
            org.telegram.tgnet.TLRPC$Document r5 = r6.document
            if (r5 == 0) goto L_0x00b6
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r13.location
            byte[] r5 = r0.getFileReference((org.telegram.tgnet.TLRPC.Document) r5, (org.telegram.tgnet.TLRPC.InputFileLocation) r6, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            goto L_0x00ff
        L_0x00b6:
            org.telegram.tgnet.TLRPC$TL_game r5 = r6.game
            if (r5 == 0) goto L_0x00d5
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r13.location
            byte[] r5 = r0.getFileReference((org.telegram.tgnet.TLRPC.Document) r5, (org.telegram.tgnet.TLRPC.InputFileLocation) r6, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r5 != 0) goto L_0x00ff
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r4.media
            org.telegram.tgnet.TLRPC$TL_game r5 = r5.game
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r13.location
            byte[] r5 = r0.getFileReference((org.telegram.tgnet.TLRPC.Photo) r5, (org.telegram.tgnet.TLRPC.InputFileLocation) r6, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            goto L_0x00ff
        L_0x00d5:
            org.telegram.tgnet.TLRPC$Photo r5 = r6.photo
            if (r5 == 0) goto L_0x00e2
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r13.location
            byte[] r5 = r0.getFileReference((org.telegram.tgnet.TLRPC.Photo) r5, (org.telegram.tgnet.TLRPC.InputFileLocation) r6, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            goto L_0x00ff
        L_0x00e2:
            org.telegram.tgnet.TLRPC$WebPage r5 = r6.webpage
            if (r5 == 0) goto L_0x0101
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r13.location
            byte[] r5 = r0.getFileReference((org.telegram.tgnet.TLRPC.WebPage) r5, (org.telegram.tgnet.TLRPC.InputFileLocation) r6, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            goto L_0x00ff
        L_0x00ef:
            org.telegram.tgnet.TLRPC$MessageAction r5 = r4.action
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r6 == 0) goto L_0x0101
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r13.location
            byte[] r5 = r0.getFileReference((org.telegram.tgnet.TLRPC.Photo) r5, (org.telegram.tgnet.TLRPC.InputFileLocation) r6, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
        L_0x00ff:
            r16 = r5
        L_0x0101:
            if (r16 == 0) goto L_0x0153
            if (r28 == 0) goto L_0x015b
            org.telegram.tgnet.TLRPC$Peer r5 = r4.to_id
            if (r5 == 0) goto L_0x0139
            int r5 = r5.channel_id
            if (r5 == 0) goto L_0x0139
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r14.chats
            int r5 = r5.size()
            r6 = 0
        L_0x0114:
            if (r6 >= r5) goto L_0x0139
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r14.chats
            java.lang.Object r12 = r12.get(r6)
            org.telegram.tgnet.TLRPC$Chat r12 = (org.telegram.tgnet.TLRPC.Chat) r12
            int r15 = r12.id
            r26 = r2
            org.telegram.tgnet.TLRPC$Peer r2 = r4.to_id
            int r2 = r2.channel_id
            if (r15 != r2) goto L_0x0134
            boolean r2 = r12.megagroup
            if (r2 == 0) goto L_0x013b
            int r2 = r4.flags
            r5 = -2147483648(0xfffffffvar_, float:-0.0)
            r2 = r2 | r5
            r4.flags = r2
            goto L_0x013b
        L_0x0134:
            int r6 = r6 + 1
            r2 = r26
            goto L_0x0114
        L_0x0139:
            r26 = r2
        L_0x013b:
            org.telegram.messenger.MessagesStorage r18 = r24.getMessagesStorage()
            int r2 = r0.currentAccount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r14.chats
            r23 = 0
            r19 = r4
            r20 = r2
            r21 = r5
            r22 = r6
            r18.replaceMessageIfExists(r19, r20, r21, r22, r23)
            goto L_0x015d
        L_0x0153:
            r26 = r2
            int r12 = r12 + 1
            r4 = 0
            r6 = 1
            goto L_0x009b
        L_0x015b:
            r26 = r2
        L_0x015d:
            r12 = r16
            if (r12 != 0) goto L_0x019f
            org.telegram.messenger.MessagesStorage r18 = r24.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r14.messages
            r4 = 0
            java.lang.Object r2 = r2.get(r4)
            r19 = r2
            org.telegram.tgnet.TLRPC$Message r19 = (org.telegram.tgnet.TLRPC.Message) r19
            int r2 = r0.currentAccount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r14.chats
            r23 = 1
            r20 = r2
            r21 = r4
            r22 = r5
            r18.replaceMessageIfExists(r19, r20, r21, r22, r23)
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r2 == 0) goto L_0x019f
            java.lang.String r2 = "file ref not found in messages, replacing message"
            org.telegram.messenger.FileLog.d(r2)
            goto L_0x019f
        L_0x018b:
            r26 = r2
            goto L_0x019f
        L_0x018e:
            r26 = r2
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.WebPage
            if (r2 == 0) goto L_0x01a2
            r2 = r3
            org.telegram.tgnet.TLRPC$WebPage r2 = (org.telegram.tgnet.TLRPC.WebPage) r2
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.WebPage) r2, (org.telegram.tgnet.TLRPC.InputFileLocation) r4, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
        L_0x019f:
            r14 = 0
            goto L_0x0406
        L_0x01a2:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_account_wallPapers
            if (r2 == 0) goto L_0x01d9
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_account_wallPapers r2 = (org.telegram.tgnet.TLRPC.TL_account_wallPapers) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r4 = r2.wallpapers
            int r4 = r4.size()
            r5 = 0
        L_0x01b0:
            if (r5 >= r4) goto L_0x01ca
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r6 = r2.wallpapers
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$TL_wallPaper r6 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r6
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.Document) r6, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x01c7
            goto L_0x01ca
        L_0x01c7:
            int r5 = r5 + 1
            goto L_0x01b0
        L_0x01ca:
            if (r12 == 0) goto L_0x019f
            if (r28 == 0) goto L_0x019f
            org.telegram.messenger.MessagesStorage r4 = r24.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r2 = r2.wallpapers
            r5 = 1
            r4.putWallpapers(r2, r5)
            goto L_0x019f
        L_0x01d9:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r2 == 0) goto L_0x01ff
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r2
            org.telegram.tgnet.TLRPC$Document r4 = r2.document
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.Document) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x019f
            if (r28 == 0) goto L_0x019f
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r4.add(r2)
            org.telegram.messenger.MessagesStorage r2 = r24.getMessagesStorage()
            r5 = 0
            r2.putWallpapers(r4, r5)
            goto L_0x019f
        L_0x01ff:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_theme
            if (r2 == 0) goto L_0x021d
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_theme r2 = (org.telegram.tgnet.TLRPC.TL_theme) r2
            org.telegram.tgnet.TLRPC$Document r4 = r2.document
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.Document) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r5, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x019f
            if (r28 == 0) goto L_0x019f
            org.telegram.messenger.-$$Lambda$FileRefController$SNJwoq0kr06ZjWyihuJvKSpuIz0 r4 = new org.telegram.messenger.-$$Lambda$FileRefController$SNJwoq0kr06ZjWyihuJvKSpuIz0
            r4.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            goto L_0x019f
        L_0x021d:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.Vector
            if (r2 == 0) goto L_0x02ab
            r2 = r3
            org.telegram.tgnet.TLRPC$Vector r2 = (org.telegram.tgnet.TLRPC.Vector) r2
            java.util.ArrayList<java.lang.Object> r4 = r2.objects
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x019f
            java.util.ArrayList<java.lang.Object> r4 = r2.objects
            int r4 = r4.size()
            r5 = 0
        L_0x0233:
            if (r5 >= r4) goto L_0x019f
            java.util.ArrayList<java.lang.Object> r6 = r2.objects
            java.lang.Object r6 = r6.get(r5)
            boolean r14 = r6 instanceof org.telegram.tgnet.TLRPC.User
            if (r14 == 0) goto L_0x0270
            org.telegram.tgnet.TLRPC$User r6 = (org.telegram.tgnet.TLRPC.User) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.User) r6, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r28 == 0) goto L_0x026b
            if (r12 == 0) goto L_0x026b
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            r14.add(r6)
            org.telegram.messenger.MessagesStorage r15 = r24.getMessagesStorage()
            r16 = r2
            r18 = r4
            r2 = 0
            r4 = 1
            r15.putUsersAndChats(r14, r2, r4, r4)
            org.telegram.messenger.-$$Lambda$FileRefController$_4y5prO3JWgmruc7MhfACo84xB8 r2 = new org.telegram.messenger.-$$Lambda$FileRefController$_4y5prO3JWgmruc7MhfACo84xB8
            r2.<init>(r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            goto L_0x02a0
        L_0x026b:
            r16 = r2
            r18 = r4
            goto L_0x02a0
        L_0x0270:
            r16 = r2
            r18 = r4
            boolean r2 = r6 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r2 == 0) goto L_0x02a0
            org.telegram.tgnet.TLRPC$Chat r6 = (org.telegram.tgnet.TLRPC.Chat) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r13.location
            byte[] r2 = r0.getFileReference((org.telegram.tgnet.TLRPC.Chat) r6, (org.telegram.tgnet.TLRPC.InputFileLocation) r2, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r28 == 0) goto L_0x029f
            if (r2 == 0) goto L_0x029f
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r4.add(r6)
            org.telegram.messenger.MessagesStorage r12 = r24.getMessagesStorage()
            r14 = 0
            r15 = 1
            r12.putUsersAndChats(r14, r4, r15, r15)
            org.telegram.messenger.-$$Lambda$FileRefController$e8OtXvlo5kjEKEtnDDj1BG232-0 r4 = new org.telegram.messenger.-$$Lambda$FileRefController$e8OtXvlo5kjEKEtnDDj1BG232-0
            r4.<init>(r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
        L_0x029f:
            r12 = r2
        L_0x02a0:
            if (r12 == 0) goto L_0x02a4
            goto L_0x019f
        L_0x02a4:
            int r5 = r5 + 1
            r2 = r16
            r4 = r18
            goto L_0x0233
        L_0x02ab:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_chats
            if (r2 == 0) goto L_0x02f6
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_chats r2 = (org.telegram.tgnet.TLRPC.TL_messages_chats) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r2.chats
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x019f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r2.chats
            int r4 = r4.size()
            r5 = 0
        L_0x02c1:
            if (r5 >= r4) goto L_0x019f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r2.chats
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Chat r6 = (org.telegram.tgnet.TLRPC.Chat) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.Chat) r6, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x02f2
            if (r28 == 0) goto L_0x019f
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r2.add(r6)
            org.telegram.messenger.MessagesStorage r4 = r24.getMessagesStorage()
            r5 = 1
            r14 = 0
            r4.putUsersAndChats(r14, r2, r5, r5)
            org.telegram.messenger.-$$Lambda$FileRefController$tUyca42NNsTxyQpe3m9-DSncaao r2 = new org.telegram.messenger.-$$Lambda$FileRefController$tUyca42NNsTxyQpe3m9-DSncaao
            r2.<init>(r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            goto L_0x0406
        L_0x02f2:
            r14 = 0
            int r5 = r5 + 1
            goto L_0x02c1
        L_0x02f6:
            r14 = 0
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_savedGifs
            if (r2 == 0) goto L_0x0334
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_savedGifs r2 = (org.telegram.tgnet.TLRPC.TL_messages_savedGifs) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.gifs
            int r4 = r4.size()
            r5 = 0
        L_0x0305:
            if (r5 >= r4) goto L_0x031d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r2.gifs
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC.Document) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.Document) r6, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x031a
            goto L_0x031d
        L_0x031a:
            int r5 = r5 + 1
            goto L_0x0305
        L_0x031d:
            if (r28 == 0) goto L_0x0406
            org.telegram.messenger.MediaDataController r15 = r24.getMediaDataController()
            r16 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r2.gifs
            r18 = 1
            r19 = 0
            r20 = 1
            r17 = r2
            r15.processLoadedRecentDocuments(r16, r17, r18, r19, r20)
            goto L_0x0406
        L_0x0334:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
            if (r2 == 0) goto L_0x0368
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r2
            if (r12 != 0) goto L_0x035c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.documents
            int r4 = r4.size()
            r5 = 0
        L_0x0344:
            if (r5 >= r4) goto L_0x035c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r2.documents
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC.Document) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.Document) r6, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x0359
            goto L_0x035c
        L_0x0359:
            int r5 = r5 + 1
            goto L_0x0344
        L_0x035c:
            if (r28 == 0) goto L_0x0406
            org.telegram.messenger.-$$Lambda$FileRefController$q_nWD12uKB_1zPGAdz0IXAdyGJg r4 = new org.telegram.messenger.-$$Lambda$FileRefController$q_nWD12uKB_1zPGAdz0IXAdyGJg
            r4.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            goto L_0x0406
        L_0x0368:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_recentStickers
            if (r2 == 0) goto L_0x03a4
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_recentStickers r2 = (org.telegram.tgnet.TLRPC.TL_messages_recentStickers) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.stickers
            int r4 = r4.size()
            r5 = 0
        L_0x0376:
            if (r5 >= r4) goto L_0x038e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r2.stickers
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC.Document) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.Document) r6, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x038b
            goto L_0x038e
        L_0x038b:
            int r5 = r5 + 1
            goto L_0x0376
        L_0x038e:
            if (r28 == 0) goto L_0x0406
            org.telegram.messenger.MediaDataController r15 = r24.getMediaDataController()
            r16 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r2.stickers
            r18 = 0
            r19 = 0
            r20 = 1
            r17 = r2
            r15.processLoadedRecentDocuments(r16, r17, r18, r19, r20)
            goto L_0x0406
        L_0x03a4:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_favedStickers
            if (r2 == 0) goto L_0x03e0
            r2 = r3
            org.telegram.tgnet.TLRPC$TL_messages_favedStickers r2 = (org.telegram.tgnet.TLRPC.TL_messages_favedStickers) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r2.stickers
            int r4 = r4.size()
            r5 = 0
        L_0x03b2:
            if (r5 >= r4) goto L_0x03ca
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r2.stickers
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC.Document) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.Document) r6, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x03c7
            goto L_0x03ca
        L_0x03c7:
            int r5 = r5 + 1
            goto L_0x03b2
        L_0x03ca:
            if (r28 == 0) goto L_0x0406
            org.telegram.messenger.MediaDataController r15 = r24.getMediaDataController()
            r16 = 2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r2.stickers
            r18 = 0
            r19 = 0
            r20 = 1
            r17 = r2
            r15.processLoadedRecentDocuments(r16, r17, r18, r19, r20)
            goto L_0x0406
        L_0x03e0:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC.photos_Photos
            if (r2 == 0) goto L_0x0406
            r2 = r3
            org.telegram.tgnet.TLRPC$photos_Photos r2 = (org.telegram.tgnet.TLRPC.photos_Photos) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r4 = r2.photos
            int r4 = r4.size()
            r5 = 0
        L_0x03ee:
            if (r5 >= r4) goto L_0x0406
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r6 = r2.photos
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Photo r6 = (org.telegram.tgnet.TLRPC.Photo) r6
            org.telegram.tgnet.TLRPC$InputFileLocation r12 = r13.location
            byte[] r12 = r0.getFileReference((org.telegram.tgnet.TLRPC.Photo) r6, (org.telegram.tgnet.TLRPC.InputFileLocation) r12, (boolean[]) r9, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r11)
            if (r12 == 0) goto L_0x0403
            goto L_0x0406
        L_0x0403:
            int r5 = r5 + 1
            goto L_0x03ee
        L_0x0406:
            if (r12 == 0) goto L_0x0415
            r2 = 0
            if (r11 == 0) goto L_0x040e
            r4 = r11[r2]
            goto L_0x040f
        L_0x040e:
            r4 = r14
        L_0x040f:
            r0.onUpdateObjectReference(r13, r12, r4)
            r5 = 1
            r10 = 1
            goto L_0x041e
        L_0x0415:
            r2 = 0
            java.lang.Object[] r4 = r13.args
            r5 = 1
            r0.sendErrorToObject(r4, r5)
        L_0x041e:
            int r8 = r8 + 1
            r2 = r26
            r4 = r14
            r6 = 1
            goto L_0x005f
        L_0x0426:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r0.locationRequester
            r2.remove(r1)
            if (r10 == 0) goto L_0x0430
            r0.putReponseToCache(r1, r3)
        L_0x0430:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.onRequestComplete(java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, boolean):boolean");
    }

    public /* synthetic */ void lambda$onRequestComplete$29$FileRefController(TLRPC.User user) {
        getMessagesController().putUser(user, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$30$FileRefController(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$31$FileRefController(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$32$FileRefController(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        getMediaDataController().replaceStickerSet(tL_messages_stickerSet);
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

    private byte[] getFileReference(TLRPC.Document document, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        if (!(document == null || inputFileLocation == null)) {
            if (!(inputFileLocation instanceof TLRPC.TL_inputDocumentFileLocation)) {
                int size = document.thumbs.size();
                int i = 0;
                while (i < size) {
                    TLRPC.PhotoSize photoSize = document.thumbs.get(i);
                    byte[] fileReference = getFileReference(photoSize, inputFileLocation, zArr);
                    if (zArr != null && zArr[0]) {
                        inputFileLocationArr[0] = new TLRPC.TL_inputDocumentFileLocation();
                        inputFileLocationArr[0].id = document.id;
                        inputFileLocationArr[0].volume_id = inputFileLocation.volume_id;
                        inputFileLocationArr[0].local_id = inputFileLocation.local_id;
                        inputFileLocationArr[0].access_hash = document.access_hash;
                        TLRPC.InputFileLocation inputFileLocation2 = inputFileLocationArr[0];
                        byte[] bArr = document.file_reference;
                        inputFileLocation2.file_reference = bArr;
                        inputFileLocationArr[0].thumb_size = photoSize.type;
                        return bArr;
                    } else if (fileReference != null) {
                        return fileReference;
                    } else {
                        i++;
                    }
                }
            } else if (document.id == inputFileLocation.id) {
                return document.file_reference;
            }
        }
        return null;
    }

    private boolean getPeerReferenceReplacement(TLRPC.User user, TLRPC.Chat chat, boolean z, TLRPC.InputFileLocation inputFileLocation, TLRPC.InputFileLocation[] inputFileLocationArr, boolean[] zArr) {
        TLRPC.InputPeer inputPeer;
        if (zArr == null || !zArr[0]) {
            return false;
        }
        inputFileLocationArr[0] = new TLRPC.TL_inputPeerPhotoFileLocation();
        TLRPC.InputFileLocation inputFileLocation2 = inputFileLocationArr[0];
        long j = inputFileLocation.volume_id;
        inputFileLocation2.id = j;
        inputFileLocationArr[0].volume_id = j;
        inputFileLocationArr[0].local_id = inputFileLocation.local_id;
        inputFileLocationArr[0].big = z;
        if (user != null) {
            TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
            tL_inputPeerUser.user_id = user.id;
            tL_inputPeerUser.access_hash = user.access_hash;
            inputPeer = tL_inputPeerUser;
        } else if (ChatObject.isChannel(chat)) {
            inputPeer = new TLRPC.TL_inputPeerChat();
            inputPeer.chat_id = chat.id;
        } else {
            inputPeer = new TLRPC.TL_inputPeerChannel();
            inputPeer.channel_id = chat.id;
            inputPeer.access_hash = chat.access_hash;
        }
        inputFileLocationArr[0].peer = inputPeer;
        return true;
    }

    private byte[] getFileReference(TLRPC.User user, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        TLRPC.UserProfilePhoto userProfilePhoto;
        if (user == null || (userProfilePhoto = user.photo) == null || !(inputFileLocation instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        byte[] fileReference = getFileReference(userProfilePhoto.photo_small, inputFileLocation, zArr);
        if (getPeerReferenceReplacement(user, (TLRPC.Chat) null, false, inputFileLocation, inputFileLocationArr, zArr)) {
            return new byte[0];
        }
        if (fileReference == null) {
            fileReference = getFileReference(user.photo.photo_big, inputFileLocation, zArr);
            if (getPeerReferenceReplacement(user, (TLRPC.Chat) null, true, inputFileLocation, inputFileLocationArr, zArr)) {
                return new byte[0];
            }
        }
        return fileReference;
    }

    private byte[] getFileReference(TLRPC.Chat chat, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        TLRPC.ChatPhoto chatPhoto;
        if (chat == null || (chatPhoto = chat.photo) == null || !(inputFileLocation instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        byte[] fileReference = getFileReference(chatPhoto.photo_small, inputFileLocation, zArr);
        if (getPeerReferenceReplacement((TLRPC.User) null, chat, false, inputFileLocation, inputFileLocationArr, zArr)) {
            return new byte[0];
        }
        if (fileReference == null) {
            fileReference = getFileReference(chat.photo.photo_big, inputFileLocation, zArr);
            if (getPeerReferenceReplacement((TLRPC.User) null, chat, true, inputFileLocation, inputFileLocationArr, zArr)) {
                return new byte[0];
            }
        }
        return fileReference;
    }

    private byte[] getFileReference(TLRPC.Photo photo, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        if (photo == null) {
            return null;
        }
        if (!(inputFileLocation instanceof TLRPC.TL_inputPhotoFileLocation)) {
            if (inputFileLocation instanceof TLRPC.TL_inputFileLocation) {
                int size = photo.sizes.size();
                int i = 0;
                while (i < size) {
                    TLRPC.PhotoSize photoSize = photo.sizes.get(i);
                    byte[] fileReference = getFileReference(photoSize, inputFileLocation, zArr);
                    if (zArr != null && zArr[0]) {
                        inputFileLocationArr[0] = new TLRPC.TL_inputPhotoFileLocation();
                        inputFileLocationArr[0].id = photo.id;
                        inputFileLocationArr[0].volume_id = inputFileLocation.volume_id;
                        inputFileLocationArr[0].local_id = inputFileLocation.local_id;
                        inputFileLocationArr[0].access_hash = photo.access_hash;
                        TLRPC.InputFileLocation inputFileLocation2 = inputFileLocationArr[0];
                        byte[] bArr = photo.file_reference;
                        inputFileLocation2.file_reference = bArr;
                        inputFileLocationArr[0].thumb_size = photoSize.type;
                        return bArr;
                    } else if (fileReference != null) {
                        return fileReference;
                    } else {
                        i++;
                    }
                }
            }
            return null;
        } else if (photo.id == inputFileLocation.id) {
            return photo.file_reference;
        } else {
            return null;
        }
    }

    private byte[] getFileReference(TLRPC.PhotoSize photoSize, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr) {
        if (photoSize == null || !(inputFileLocation instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        return getFileReference(photoSize.location, inputFileLocation, zArr);
    }

    private byte[] getFileReference(TLRPC.FileLocation fileLocation, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr) {
        if (fileLocation == null || !(inputFileLocation instanceof TLRPC.TL_inputFileLocation) || fileLocation.local_id != inputFileLocation.local_id || fileLocation.volume_id != inputFileLocation.volume_id) {
            return null;
        }
        if (fileLocation.file_reference == null && zArr != null) {
            zArr[0] = true;
        }
        return fileLocation.file_reference;
    }

    private byte[] getFileReference(TLRPC.WebPage webPage, TLRPC.InputFileLocation inputFileLocation, boolean[] zArr, TLRPC.InputFileLocation[] inputFileLocationArr) {
        byte[] fileReference = getFileReference(webPage.document, inputFileLocation, zArr, inputFileLocationArr);
        if (fileReference != null) {
            return fileReference;
        }
        byte[] fileReference2 = getFileReference(webPage.photo, inputFileLocation, zArr, inputFileLocationArr);
        if (fileReference2 != null) {
            return fileReference2;
        }
        if (!webPage.attributes.isEmpty()) {
            int size = webPage.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC.TL_webPageAttributeTheme tL_webPageAttributeTheme = webPage.attributes.get(i);
                int size2 = tL_webPageAttributeTheme.documents.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    byte[] fileReference3 = getFileReference(tL_webPageAttributeTheme.documents.get(i2), inputFileLocation, zArr, inputFileLocationArr);
                    if (fileReference3 != null) {
                        return fileReference3;
                    }
                }
            }
        }
        TLRPC.Page page = webPage.cached_page;
        if (page == null) {
            return null;
        }
        int size3 = page.documents.size();
        for (int i3 = 0; i3 < size3; i3++) {
            byte[] fileReference4 = getFileReference(webPage.cached_page.documents.get(i3), inputFileLocation, zArr, inputFileLocationArr);
            if (fileReference4 != null) {
                return fileReference4;
            }
        }
        int size4 = webPage.cached_page.photos.size();
        for (int i4 = 0; i4 < size4; i4++) {
            byte[] fileReference5 = getFileReference(webPage.cached_page.photos.get(i4), inputFileLocation, zArr, inputFileLocationArr);
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
