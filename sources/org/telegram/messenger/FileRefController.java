package org.telegram.messenger;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC.TL_account_wallPapers;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channels_getChannels;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_chat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC.TL_inputSecureFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickeredMediaDocument;
import org.telegram.tgnet.TLRPC.TL_inputStickeredMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_inputWallPaper;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_faveSticker;
import org.telegram.tgnet.TLRPC.TL_messages_favedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getChats;
import org.telegram.tgnet.TLRPC.TL_messages_getFavedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_recentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC.TL_messages_savedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC.TL_users_getUsers;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;

public class FileRefController {
    private static volatile FileRefController[] Instance = new FileRefController[3];
    private int currentAccount;
    private long lastCleanupTime = SystemClock.uptimeMillis();
    private HashMap<String, ArrayList<Requester>> locationRequester = new HashMap();
    private HashMap<TL_messages_sendMultiMedia, Object[]> multiMediaCache = new HashMap();
    private HashMap<String, ArrayList<Requester>> parentRequester = new HashMap();
    private HashMap<String, CachedResult> responseCache = new HashMap();

    private class CachedResult {
        private long firstQueryTime;
        private long lastQueryTime;
        private TLObject response;

        private CachedResult() {
        }
    }

    private class Requester {
        private Object[] args;
        private boolean completed;
        private InputFileLocation location;
        private String locationKey;

        private Requester() {
        }
    }

    public static FileRefController getInstance(int num) {
        Throwable th;
        FileRefController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FileRefController.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        FileRefController[] fileRefControllerArr = Instance;
                        FileRefController localInstance2 = new FileRefController(num);
                        try {
                            fileRefControllerArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public FileRefController(int instance) {
        this.currentAccount = instance;
    }

    public static String getKeyForParentObject(Object parentObject) {
        if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            return "message" + messageObject.getRealId() + "_" + messageObject.getChannelId();
        } else if (parentObject instanceof Message) {
            Message message = (Message) parentObject;
            return "message" + message.id + "_" + (message.to_id != null ? message.to_id.channel_id : 0);
        } else if (parentObject instanceof WebPage) {
            return "webpage" + ((WebPage) parentObject).id;
        } else if (parentObject instanceof User) {
            return "user" + ((User) parentObject).id;
        } else if (parentObject instanceof Chat) {
            return "chat" + ((Chat) parentObject).id;
        } else if (parentObject instanceof String) {
            return "str" + ((String) parentObject);
        } else if (parentObject instanceof TL_messages_stickerSet) {
            return "set" + ((TL_messages_stickerSet) parentObject).set.id;
        } else if (parentObject instanceof StickerSetCovered) {
            return "set" + ((StickerSetCovered) parentObject).set.id;
        } else if (parentObject instanceof InputStickerSet) {
            return "set" + ((InputStickerSet) parentObject).id;
        } else if (!(parentObject instanceof TL_wallPaper)) {
            return parentObject != null ? "" + parentObject : null;
        } else {
            return "wallpaper" + ((TL_wallPaper) parentObject).id;
        }
    }

    public void requestReference(Object parentObject, Object... args) {
        String locationKey;
        InputFileLocation location;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start loading request reference for parent = " + parentObject + " args = " + args[0]);
        }
        TL_inputMediaDocument mediaDocument;
        TL_inputMediaPhoto mediaPhoto;
        if (args[0] instanceof TL_inputSingleMedia) {
            TL_inputSingleMedia req = args[0];
            if (req.media instanceof TL_inputMediaDocument) {
                mediaDocument = req.media;
                locationKey = "file_" + mediaDocument.id.id;
                location = new TL_inputDocumentFileLocation();
                location.id = mediaDocument.id.id;
            } else if (req.media instanceof TL_inputMediaPhoto) {
                mediaPhoto = req.media;
                locationKey = "photo_" + mediaPhoto.id.id;
                location = new TL_inputSecureFileLocation();
                location.id = mediaPhoto.id.id;
            } else {
                sendErrorToObject(args, 0);
                return;
            }
        } else if (args[0] instanceof TL_messages_sendMultiMedia) {
            TL_messages_sendMultiMedia req2 = args[0];
            ArrayList<Object> parentObjects = (ArrayList) parentObject;
            this.multiMediaCache.put(req2, args);
            int size = req2.multi_media.size();
            for (int a = 0; a < size; a++) {
                TL_inputSingleMedia media = (TL_inputSingleMedia) req2.multi_media.get(a);
                parentObject = parentObjects.get(a);
                if (parentObject != null) {
                    requestReference(parentObject, media, req2);
                }
            }
            return;
        } else if (args[0] instanceof TL_messages_sendMedia) {
            TL_messages_sendMedia req3 = args[0];
            if (req3.media instanceof TL_inputMediaDocument) {
                mediaDocument = (TL_inputMediaDocument) req3.media;
                locationKey = "file_" + mediaDocument.id.id;
                location = new TL_inputDocumentFileLocation();
                location.id = mediaDocument.id.id;
            } else if (req3.media instanceof TL_inputMediaPhoto) {
                mediaPhoto = (TL_inputMediaPhoto) req3.media;
                locationKey = "photo_" + mediaPhoto.id.id;
                location = new TL_inputSecureFileLocation();
                location.id = mediaPhoto.id.id;
            } else {
                sendErrorToObject(args, 0);
                return;
            }
        } else if (args[0] instanceof TL_messages_editMessage) {
            TL_messages_editMessage req4 = args[0];
            if (req4.media instanceof TL_inputMediaDocument) {
                mediaDocument = (TL_inputMediaDocument) req4.media;
                locationKey = "file_" + mediaDocument.id.id;
                location = new TL_inputDocumentFileLocation();
                location.id = mediaDocument.id.id;
            } else if (req4.media instanceof TL_inputMediaPhoto) {
                mediaPhoto = (TL_inputMediaPhoto) req4.media;
                locationKey = "photo_" + mediaPhoto.id.id;
                location = new TL_inputSecureFileLocation();
                location.id = mediaPhoto.id.id;
            } else {
                sendErrorToObject(args, 0);
                return;
            }
        } else if (args[0] instanceof TL_messages_saveGif) {
            TL_messages_saveGif req5 = args[0];
            locationKey = "file_" + req5.id.id;
            location = new TL_inputDocumentFileLocation();
            location.id = req5.id.id;
        } else if (args[0] instanceof TL_messages_saveRecentSticker) {
            TL_messages_saveRecentSticker req6 = args[0];
            locationKey = "file_" + req6.id.id;
            location = new TL_inputDocumentFileLocation();
            location.id = req6.id.id;
        } else if (args[0] instanceof TL_messages_faveSticker) {
            TL_messages_faveSticker req7 = args[0];
            locationKey = "file_" + req7.id.id;
            location = new TL_inputDocumentFileLocation();
            location.id = req7.id.id;
        } else if (args[0] instanceof TL_messages_getAttachedStickers) {
            TL_messages_getAttachedStickers req8 = args[0];
            if (req8.media instanceof TL_inputStickeredMediaDocument) {
                TL_inputStickeredMediaDocument mediaDocument2 = req8.media;
                locationKey = "file_" + mediaDocument2.id.id;
                location = new TL_inputDocumentFileLocation();
                location.id = mediaDocument2.id.id;
            } else if (req8.media instanceof TL_inputStickeredMediaPhoto) {
                TL_inputStickeredMediaPhoto mediaPhoto2 = req8.media;
                locationKey = "photo_" + mediaPhoto2.id.id;
                location = new TL_inputSecureFileLocation();
                location.id = mediaPhoto2.id.id;
            } else {
                sendErrorToObject(args, 0);
                return;
            }
        } else if (args[0] instanceof TL_inputFileLocation) {
            TL_inputFileLocation location2 = (TL_inputFileLocation) args[0];
            locationKey = "loc_" + location2.local_id + "_" + location2.volume_id;
        } else if (args[0] instanceof TL_inputDocumentFileLocation) {
            TL_inputDocumentFileLocation location22 = (TL_inputDocumentFileLocation) args[0];
            locationKey = "file_" + location22.id;
        } else {
            sendErrorToObject(args, 0);
            return;
        }
        if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            if (messageObject.getRealId() < 0 && messageObject.messageOwner.media.webpage != null) {
                parentObject = messageObject.messageOwner.media.webpage;
            }
        }
        String parentKey = getKeyForParentObject(parentObject);
        if (parentKey == null) {
            sendErrorToObject(args, 0);
            return;
        }
        Requester requester = new Requester();
        requester.args = args;
        requester.location = location22;
        requester.locationKey = locationKey;
        int added = 0;
        ArrayList<Requester> arrayList = (ArrayList) this.locationRequester.get(locationKey);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.locationRequester.put(locationKey, arrayList);
            added = 0 + 1;
        }
        arrayList.add(requester);
        arrayList = (ArrayList) this.parentRequester.get(parentKey);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.parentRequester.put(parentKey, arrayList);
            added++;
        }
        arrayList.add(requester);
        if (added == 2) {
            cleanupCache();
            CachedResult cachedResult = getCachedResponse(locationKey);
            if (cachedResult == null) {
                cachedResult = getCachedResponse(parentKey);
                if (cachedResult != null) {
                    if (!onRequestComplete(locationKey, parentKey, cachedResult.response, false)) {
                        this.responseCache.remove(parentKey);
                    } else {
                        return;
                    }
                }
            } else if (!onRequestComplete(locationKey, parentKey, cachedResult.response, false)) {
                this.responseCache.remove(locationKey);
            } else {
                return;
            }
            requestReferenceFromServer(parentObject, locationKey, parentKey, args);
        }
    }

    private void requestReferenceFromServer(Object parentObject, String locationKey, String parentKey, Object[] args) {
        int channelId;
        TL_channels_getMessages req;
        TL_messages_getMessages req2;
        TL_messages_getStickerSet req3;
        if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            channelId = messageObject.getChannelId();
            if (channelId != 0) {
                req = new TL_channels_getMessages();
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
                req.id.add(Integer.valueOf(messageObject.getRealId()));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new FileRefController$$Lambda$0(this, locationKey, parentKey));
                return;
            }
            req2 = new TL_messages_getMessages();
            req2.id.add(Integer.valueOf(messageObject.getRealId()));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new FileRefController$$Lambda$1(this, locationKey, parentKey));
        } else if (parentObject instanceof TL_wallPaper) {
            TL_wallPaper wallPaper = (TL_wallPaper) parentObject;
            TL_account_getWallPaper req4 = new TL_account_getWallPaper();
            TL_inputWallPaper inputWallPaper = new TL_inputWallPaper();
            inputWallPaper.id = wallPaper.id;
            inputWallPaper.access_hash = wallPaper.access_hash;
            req4.wallpaper = inputWallPaper;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req4, new FileRefController$$Lambda$2(this, locationKey, parentKey));
        } else if (parentObject instanceof WebPage) {
            WebPage webPage = (WebPage) parentObject;
            TL_messages_getWebPage req5 = new TL_messages_getWebPage();
            req5.url = webPage.url;
            req5.hash = 0;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req5, new FileRefController$$Lambda$3(this, locationKey, parentKey));
        } else if (parentObject instanceof User) {
            User user = (User) parentObject;
            TL_users_getUsers req6 = new TL_users_getUsers();
            req6.id.add(MessagesController.getInstance(this.currentAccount).getInputUser(user));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req6, new FileRefController$$Lambda$4(this, locationKey, parentKey));
        } else if (parentObject instanceof Chat) {
            Chat chat = (Chat) parentObject;
            if (chat instanceof TL_chat) {
                TL_messages_getChats req7 = new TL_messages_getChats();
                req7.id.add(Integer.valueOf(chat.id));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req7, new FileRefController$$Lambda$5(this, locationKey, parentKey));
            } else if (chat instanceof TL_channel) {
                TL_channels_getChannels req8 = new TL_channels_getChannels();
                req8.id.add(MessagesController.getInputChannel(chat));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req8, new FileRefController$$Lambda$6(this, locationKey, parentKey));
            }
        } else if (parentObject instanceof String) {
            String string = (String) parentObject;
            if ("wallpaper".equals(string)) {
                TL_account_getWallPapers req9 = new TL_account_getWallPapers();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req9, new FileRefController$$Lambda$7(this, locationKey, parentKey));
            } else if (string.startsWith("gif")) {
                TL_messages_getSavedGifs req10 = new TL_messages_getSavedGifs();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req10, new FileRefController$$Lambda$8(this, locationKey, parentKey));
            } else if ("recent".equals(string)) {
                TL_messages_getRecentStickers req11 = new TL_messages_getRecentStickers();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req11, new FileRefController$$Lambda$9(this, locationKey, parentKey));
            } else if ("fav".equals(string)) {
                TL_messages_getFavedStickers req12 = new TL_messages_getFavedStickers();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req12, new FileRefController$$Lambda$10(this, locationKey, parentKey));
            } else if (string.startsWith("avatar_")) {
                int id = Utilities.parseInt(string).intValue();
                if (id > 0) {
                    TL_photos_getUserPhotos req13 = new TL_photos_getUserPhotos();
                    req13.limit = 80;
                    req13.offset = 0;
                    req13.max_id = 0;
                    req13.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(id);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req13, new FileRefController$$Lambda$11(this, locationKey, parentKey));
                    return;
                }
                TL_messages_search req14 = new TL_messages_search();
                req14.filter = new TL_inputMessagesFilterChatPhotos();
                req14.limit = 80;
                req14.offset_id = 0;
                req14.q = "";
                req14.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(id);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req14, new FileRefController$$Lambda$12(this, locationKey, parentKey));
            } else if (string.startsWith("sent_")) {
                String[] params = string.split("_");
                if (params.length == 3) {
                    channelId = Utilities.parseInt(params[1]).intValue();
                    if (channelId != 0) {
                        req = new TL_channels_getMessages();
                        req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
                        req.id.add(Utilities.parseInt(params[2]));
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new FileRefController$$Lambda$13(this, locationKey, parentKey));
                        return;
                    }
                    req2 = new TL_messages_getMessages();
                    req2.id.add(Utilities.parseInt(params[2]));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new FileRefController$$Lambda$14(this, locationKey, parentKey));
                    return;
                }
                sendErrorToObject(args, 0);
            } else {
                sendErrorToObject(args, 0);
            }
        } else if (parentObject instanceof TL_messages_stickerSet) {
            TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) parentObject;
            req3 = new TL_messages_getStickerSet();
            req3.stickerset = new TL_inputStickerSetID();
            req3.stickerset.id = stickerSet.set.id;
            req3.stickerset.access_hash = stickerSet.set.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req3, new FileRefController$$Lambda$15(this, locationKey, parentKey));
        } else if (parentObject instanceof StickerSetCovered) {
            StickerSetCovered stickerSet2 = (StickerSetCovered) parentObject;
            req3 = new TL_messages_getStickerSet();
            req3.stickerset = new TL_inputStickerSetID();
            req3.stickerset.id = stickerSet2.set.id;
            req3.stickerset.access_hash = stickerSet2.set.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req3, new FileRefController$$Lambda$16(this, locationKey, parentKey));
        } else if (parentObject instanceof InputStickerSet) {
            req3 = new TL_messages_getStickerSet();
            req3.stickerset = (InputStickerSet) parentObject;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req3, new FileRefController$$Lambda$17(this, locationKey, parentKey));
        } else {
            sendErrorToObject(args, 0);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$0$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$1$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$2$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$3$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$4$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$5$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$6$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$7$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$8$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$9$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$10$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$11$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$12$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$13$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$14$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$15$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$16$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$requestReferenceFromServer$17$FileRefController(String locationKey, String parentKey, TLObject response, TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    private void onUpdateObjectReference(Requester requester, byte[] file_reference) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("fileref updated for " + requester.args[0] + " " + requester.locationKey);
        }
        TLObject req;
        if (requester.args[0] instanceof TL_inputSingleMedia) {
            TL_messages_sendMultiMedia multiMedia = requester.args[1];
            Object[] objects = (Object[]) this.multiMediaCache.get(multiMedia);
            if (objects != null) {
                TL_inputSingleMedia req2 = requester.args[0];
                if (req2.media instanceof TL_inputMediaDocument) {
                    req2.media.id.file_reference = file_reference;
                } else if (req2.media instanceof TL_inputMediaPhoto) {
                    ((TL_inputMediaPhoto) req2.media).id.file_reference = file_reference;
                }
                int index = multiMedia.multi_media.indexOf(req2);
                if (index >= 0) {
                    ArrayList<Object> parentObjects = objects[3];
                    parentObjects.set(index, null);
                    boolean done = true;
                    for (int a = 0; a < parentObjects.size(); a++) {
                        if (parentObjects.get(a) != null) {
                            done = false;
                        }
                    }
                    if (done) {
                        this.multiMediaCache.remove(multiMedia);
                        SendMessagesHelper.getInstance(this.currentAccount).performSendMessageRequestMulti(multiMedia, (ArrayList) objects[1], (ArrayList) objects[2], null, (DelayedMessage) objects[4]);
                    }
                }
            }
        } else if (requester.args[0] instanceof TL_messages_sendMedia) {
            TL_messages_sendMedia req3 = requester.args[0];
            if (req3.media instanceof TL_inputMediaDocument) {
                ((TL_inputMediaDocument) req3.media).id.file_reference = file_reference;
            } else if (req3.media instanceof TL_inputMediaPhoto) {
                ((TL_inputMediaPhoto) req3.media).id.file_reference = file_reference;
            }
            SendMessagesHelper.getInstance(this.currentAccount).performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (DelayedMessage) requester.args[5], null);
        } else if (requester.args[0] instanceof TL_messages_editMessage) {
            TL_messages_editMessage req4 = requester.args[0];
            if (req4.media instanceof TL_inputMediaDocument) {
                ((TL_inputMediaDocument) req4.media).id.file_reference = file_reference;
            } else if (req4.media instanceof TL_inputMediaPhoto) {
                ((TL_inputMediaPhoto) req4.media).id.file_reference = file_reference;
            }
            SendMessagesHelper.getInstance(this.currentAccount).performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (DelayedMessage) requester.args[5], null);
        } else if (requester.args[0] instanceof TL_messages_saveGif) {
            req = requester.args[0];
            req.id.file_reference = file_reference;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, FileRefController$$Lambda$18.$instance);
        } else if (requester.args[0] instanceof TL_messages_saveRecentSticker) {
            req = requester.args[0];
            req.id.file_reference = file_reference;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, FileRefController$$Lambda$19.$instance);
        } else if (requester.args[0] instanceof TL_messages_faveSticker) {
            req = requester.args[0];
            req.id.file_reference = file_reference;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, FileRefController$$Lambda$20.$instance);
        } else if (requester.args[0] instanceof TL_messages_getAttachedStickers) {
            req = requester.args[0];
            if (req.media instanceof TL_inputStickeredMediaDocument) {
                req.media.id.file_reference = file_reference;
            } else if (req.media instanceof TL_inputStickeredMediaPhoto) {
                ((TL_inputStickeredMediaPhoto) req.media).id.file_reference = file_reference;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, (RequestDelegate) requester.args[1]);
        } else if (requester.args[1] instanceof FileLoadOperation) {
            requester.location.file_reference = file_reference;
            FileLoadOperation fileLoadOperation = requester.args[1];
            fileLoadOperation.requestingReference = false;
            fileLoadOperation.startDownloadRequest();
        }
    }

    static final /* synthetic */ void lambda$onUpdateObjectReference$18$FileRefController(TLObject response, TL_error error) {
    }

    static final /* synthetic */ void lambda$onUpdateObjectReference$19$FileRefController(TLObject response, TL_error error) {
    }

    static final /* synthetic */ void lambda$onUpdateObjectReference$20$FileRefController(TLObject response, TL_error error) {
    }

    private void sendErrorToObject(Object[] args, int reason) {
        FileLoadOperation fileLoadOperation;
        if (args[0] instanceof TL_inputSingleMedia) {
            TL_messages_sendMultiMedia req = args[1];
            Object[] objects = (Object[]) this.multiMediaCache.get(req);
            if (objects != null) {
                this.multiMediaCache.remove(req);
                SendMessagesHelper.getInstance(this.currentAccount).performSendMessageRequestMulti(req, (ArrayList) objects[1], (ArrayList) objects[2], null, (DelayedMessage) objects[4]);
            }
        } else if ((args[0] instanceof TL_messages_sendMedia) || (args[0] instanceof TL_messages_editMessage)) {
            SendMessagesHelper.getInstance(this.currentAccount).performSendMessageRequest((TLObject) args[0], (MessageObject) args[1], (String) args[2], (DelayedMessage) args[3], ((Boolean) args[4]).booleanValue(), (DelayedMessage) args[5], null);
        } else if (args[0] instanceof TL_messages_saveGif) {
            TL_messages_saveGif tL_messages_saveGif = (TL_messages_saveGif) args[0];
        } else if (args[0] instanceof TL_messages_saveRecentSticker) {
            TL_messages_saveRecentSticker tL_messages_saveRecentSticker = (TL_messages_saveRecentSticker) args[0];
        } else if (args[0] instanceof TL_messages_faveSticker) {
            TL_messages_faveSticker tL_messages_faveSticker = (TL_messages_faveSticker) args[0];
        } else if (args[0] instanceof TL_messages_getAttachedStickers) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(args[0], (RequestDelegate) args[1]);
        } else if (reason == 0) {
            TL_error error = new TL_error();
            error.text = "not found parent object to request reference";
            error.code = 400;
            if (args[1] instanceof FileLoadOperation) {
                fileLoadOperation = args[1];
                fileLoadOperation.requestingReference = false;
                fileLoadOperation.processRequestResult((RequestInfo) args[2], error);
            }
        } else if (reason == 1 && (args[1] instanceof FileLoadOperation)) {
            fileLoadOperation = (FileLoadOperation) args[1];
            fileLoadOperation.requestingReference = false;
            fileLoadOperation.onFail(false, 0);
        }
    }

    private boolean onRequestComplete(String locationKey, String parentKey, TLObject response, boolean cache) {
        ArrayList<Requester> arrayList;
        int N;
        int q;
        Requester requester;
        boolean found = false;
        if (parentKey != null) {
            arrayList = (ArrayList) this.parentRequester.get(parentKey);
            if (arrayList != null) {
                N = arrayList.size();
                for (q = 0; q < N; q++) {
                    requester = (Requester) arrayList.get(q);
                    if (!requester.completed) {
                        String access$300 = requester.locationKey;
                        boolean z = cache && !found;
                        if (onRequestComplete(access$300, null, response, z)) {
                            found = true;
                        }
                    }
                }
                if (found) {
                    putReponseToCache(parentKey, response);
                }
                this.parentRequester.remove(parentKey);
            }
        }
        byte[] result = null;
        arrayList = (ArrayList) this.locationRequester.get(locationKey);
        if (arrayList == null) {
            return found;
        }
        N = arrayList.size();
        for (q = 0; q < N; q++) {
            requester = (Requester) arrayList.get(q);
            if (!requester.completed) {
                requester.completed = true;
                int i;
                Chat chat;
                int size10;
                ArrayList<Chat> arrayList1;
                int size2;
                int b;
                if (response instanceof messages_Messages) {
                    messages_Messages res = (messages_Messages) response;
                    if (!res.messages.isEmpty()) {
                        i = 0;
                        int size3 = res.messages.size();
                        while (i < size3) {
                            Message message = (Message) res.messages.get(i);
                            if (message.media != null) {
                                if (message.media.document != null) {
                                    result = getFileReference(message.media.document, requester.location);
                                } else if (message.media.game != null) {
                                    result = getFileReference(message.media.game.document, requester.location);
                                    if (result == null) {
                                        result = getFileReference(message.media.game.photo, requester.location);
                                    }
                                } else if (message.media.photo != null) {
                                    result = getFileReference(message.media.photo, requester.location);
                                } else if (message.media.webpage != null) {
                                    result = getFileReference(message.media.webpage, requester.location);
                                }
                            } else if (message.action instanceof TL_messageActionChatEditPhoto) {
                                result = getFileReference(message.action.photo, requester.location);
                            }
                            if (result == null) {
                                i++;
                            } else if (cache) {
                                if (!(message.to_id == null || message.to_id.channel_id == 0)) {
                                    int a = 0;
                                    int N2 = res.chats.size();
                                    while (a < N2) {
                                        chat = (Chat) res.chats.get(a);
                                        if (chat.id != message.to_id.channel_id) {
                                            a++;
                                        } else if (chat.megagroup) {
                                            message.flags |= Integer.MIN_VALUE;
                                        }
                                    }
                                }
                                MessagesStorage.getInstance(this.currentAccount).replaceMessageIfExists(message);
                            }
                        }
                    }
                } else if (response instanceof WebPage) {
                    result = getFileReference((WebPage) response, requester.location);
                } else if (response instanceof TL_account_wallPapers) {
                    TL_account_wallPapers accountWallPapers = (TL_account_wallPapers) response;
                    size10 = accountWallPapers.wallpapers.size();
                    for (i = 0; i < size10; i++) {
                        result = getFileReference(((TL_wallPaper) accountWallPapers.wallpapers.get(i)).document, requester.location);
                        if (result != null) {
                            break;
                        }
                    }
                    if (result != null && cache) {
                        MessagesStorage.getInstance(this.currentAccount).putWallpapers(accountWallPapers.wallpapers, 1);
                    }
                } else if (response instanceof TL_wallPaper) {
                    TL_wallPaper wallPaper = (TL_wallPaper) response;
                    result = getFileReference(wallPaper.document, requester.location);
                    if (result != null && cache) {
                        ArrayList<WallPaper> wallpapers = new ArrayList();
                        wallpapers.add(wallPaper);
                        MessagesStorage.getInstance(this.currentAccount).putWallpapers(wallpapers, 0);
                    }
                } else if (response instanceof Vector) {
                    Vector vector = (Vector) response;
                    if (!vector.objects.isEmpty()) {
                        size10 = vector.objects.size();
                        for (i = 0; i < size10; i++) {
                            User object = vector.objects.get(i);
                            if (object instanceof User) {
                                User user = object;
                                result = getFileReference(user.photo, requester.location);
                                if (cache && result != null) {
                                    ArrayList<User> arrayList12 = new ArrayList();
                                    arrayList12.add(user);
                                    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList12, null, true, true);
                                    AndroidUtilities.runOnUIThread(new FileRefController$$Lambda$21(this, user));
                                }
                            } else if (object instanceof Chat) {
                                chat = (Chat) object;
                                result = getFileReference(chat.photo, requester.location);
                                if (cache && result != null) {
                                    arrayList1 = new ArrayList();
                                    arrayList1.add(chat);
                                    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, arrayList1, true, true);
                                    AndroidUtilities.runOnUIThread(new FileRefController$$Lambda$22(this, chat));
                                }
                            }
                            if (result != null) {
                                break;
                            }
                        }
                    }
                } else if (response instanceof TL_messages_chats) {
                    TL_messages_chats res2 = (TL_messages_chats) response;
                    if (!res2.chats.isEmpty()) {
                        i = 0;
                        size10 = res2.chats.size();
                        while (i < size10) {
                            chat = (Chat) res2.chats.get(i);
                            result = getFileReference(chat.photo, requester.location);
                            if (result == null) {
                                i++;
                            } else if (cache) {
                                arrayList1 = new ArrayList();
                                arrayList1.add(chat);
                                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, arrayList1, true, true);
                                AndroidUtilities.runOnUIThread(new FileRefController$$Lambda$23(this, chat));
                            }
                        }
                    }
                } else if (response instanceof TL_messages_savedGifs) {
                    TL_messages_savedGifs savedGifs = (TL_messages_savedGifs) response;
                    size2 = savedGifs.gifs.size();
                    for (b = 0; b < size2; b++) {
                        result = getFileReference((Document) savedGifs.gifs.get(b), requester.location);
                        if (result != null) {
                            break;
                        }
                    }
                    if (cache) {
                        DataQuery.getInstance(this.currentAccount).processLoadedRecentDocuments(0, savedGifs.gifs, true, 0, true);
                    }
                } else if (response instanceof TL_messages_stickerSet) {
                    TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) response;
                    if (stickerSet.set.thumb instanceof TL_photoSize) {
                        result = getFileReference(stickerSet.set.thumb, requester.location);
                    }
                    if (result == null) {
                        size2 = stickerSet.documents.size();
                        for (b = 0; b < size2; b++) {
                            result = getFileReference((Document) stickerSet.documents.get(b), requester.location);
                            if (result != null) {
                                break;
                            }
                        }
                    }
                    if (cache) {
                        AndroidUtilities.runOnUIThread(new FileRefController$$Lambda$24(this, stickerSet));
                    }
                } else if (response instanceof TL_messages_recentStickers) {
                    TL_messages_recentStickers recentStickers = (TL_messages_recentStickers) response;
                    size2 = recentStickers.stickers.size();
                    for (b = 0; b < size2; b++) {
                        result = getFileReference((Document) recentStickers.stickers.get(b), requester.location);
                        if (result != null) {
                            break;
                        }
                    }
                    if (cache) {
                        DataQuery.getInstance(this.currentAccount).processLoadedRecentDocuments(0, recentStickers.stickers, false, 0, true);
                    }
                } else if (response instanceof TL_messages_favedStickers) {
                    TL_messages_favedStickers favedStickers = (TL_messages_favedStickers) response;
                    size2 = favedStickers.stickers.size();
                    for (b = 0; b < size2; b++) {
                        result = getFileReference((Document) favedStickers.stickers.get(b), requester.location);
                        if (result != null) {
                            break;
                        }
                    }
                    if (cache) {
                        DataQuery.getInstance(this.currentAccount).processLoadedRecentDocuments(2, favedStickers.stickers, false, 0, true);
                    }
                } else if (response instanceof photos_Photos) {
                    photos_Photos res3 = (photos_Photos) response;
                    int size = res3.photos.size();
                    for (b = 0; b < size; b++) {
                        result = getFileReference((Photo) res3.photos.get(b), requester.location);
                        if (result != null) {
                            break;
                        }
                    }
                }
                if (result != null) {
                    onUpdateObjectReference(requester, result);
                    found = true;
                } else {
                    sendErrorToObject(requester.args, 1);
                }
            }
        }
        this.locationRequester.remove(locationKey);
        if (found) {
            putReponseToCache(locationKey, response);
        }
        boolean z2 = found;
        return found;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onRequestComplete$21$FileRefController(User user) {
        MessagesController.getInstance(this.currentAccount).putUser(user, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onRequestComplete$22$FileRefController(Chat chat) {
        MessagesController.getInstance(this.currentAccount).putChat(chat, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onRequestComplete$23$FileRefController(Chat chat) {
        MessagesController.getInstance(this.currentAccount).putChat(chat, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onRequestComplete$24$FileRefController(TL_messages_stickerSet stickerSet) {
        DataQuery.getInstance(this.currentAccount).replaceStickerSet(stickerSet);
    }

    private void cleanupCache() {
        if (Math.abs(SystemClock.uptimeMillis() - this.lastCleanupTime) >= 600000) {
            this.lastCleanupTime = SystemClock.uptimeMillis();
            ArrayList<String> keysToDelete = null;
            for (Entry<String, CachedResult> entry : this.responseCache.entrySet()) {
                if (Math.abs(SystemClock.uptimeMillis() - ((CachedResult) entry.getValue()).firstQueryTime) >= 600000) {
                    if (keysToDelete == null) {
                        keysToDelete = new ArrayList();
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
        CachedResult cachedResult = (CachedResult) this.responseCache.get(key);
        if (cachedResult == null || Math.abs(SystemClock.uptimeMillis() - cachedResult.firstQueryTime) < 600000) {
            return cachedResult;
        }
        this.responseCache.remove(key);
        return null;
    }

    private void putReponseToCache(String key, TLObject response) {
        CachedResult cachedResult = (CachedResult) this.responseCache.get(key);
        if (cachedResult == null) {
            cachedResult = new CachedResult();
            cachedResult.response = response;
            cachedResult.firstQueryTime = SystemClock.uptimeMillis();
            this.responseCache.put(key, cachedResult);
        }
        cachedResult.lastQueryTime = SystemClock.uptimeMillis();
    }

    private byte[] getFileReference(Document document, InputFileLocation location) {
        if (document == null || location == null) {
            return null;
        }
        if (!(location instanceof TL_inputDocumentFileLocation)) {
            int size = document.thumbs.size();
            for (int a = 0; a < size; a++) {
                byte[] result = getFileReference((PhotoSize) document.thumbs.get(a), location);
                if (result != null) {
                    return result;
                }
            }
        } else if (document.id == location.id) {
            return document.file_reference;
        }
        return null;
    }

    private byte[] getFileReference(UserProfilePhoto photo, InputFileLocation location) {
        if (photo == null || !(location instanceof TL_inputFileLocation)) {
            return null;
        }
        byte[] result = getFileReference(photo.photo_small, location);
        if (result == null) {
            return getFileReference(photo.photo_big, location);
        }
        return result;
    }

    private byte[] getFileReference(ChatPhoto photo, InputFileLocation location) {
        if (photo == null || !(location instanceof TL_inputFileLocation)) {
            return null;
        }
        byte[] result = getFileReference(photo.photo_small, location);
        if (result == null) {
            return getFileReference(photo.photo_big, location);
        }
        return result;
    }

    private byte[] getFileReference(Photo photo, InputFileLocation location) {
        if (photo == null) {
            return null;
        }
        if (location instanceof TL_inputSecureFileLocation) {
            return photo.id == location.id ? photo.file_reference : null;
        } else if (!(location instanceof TL_inputFileLocation)) {
            return null;
        } else {
            int size = photo.sizes.size();
            for (int a = 0; a < size; a++) {
                byte[] result = getFileReference((PhotoSize) photo.sizes.get(a), location);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    private byte[] getFileReference(PhotoSize photoSize, InputFileLocation location) {
        if (photoSize == null || !(location instanceof TL_inputFileLocation)) {
            return null;
        }
        return getFileReference(photoSize.location, location);
    }

    private byte[] getFileReference(FileLocation fileLocation, InputFileLocation location) {
        if (fileLocation != null && (location instanceof TL_inputFileLocation) && fileLocation.local_id == location.local_id && fileLocation.volume_id == location.volume_id) {
            return fileLocation.file_reference;
        }
        return null;
    }

    private byte[] getFileReference(WebPage webpage, InputFileLocation location) {
        byte[] result = getFileReference(webpage.document, location);
        if (result != null) {
            return result;
        }
        result = getFileReference(webpage.photo, location);
        byte[] bArr;
        if (result != null) {
            bArr = result;
            return result;
        }
        if (result == null && webpage.cached_page != null) {
            int b;
            int size2 = webpage.cached_page.documents.size();
            for (b = 0; b < size2; b++) {
                result = getFileReference((Document) webpage.cached_page.documents.get(b), location);
                if (result != null) {
                    bArr = result;
                    return result;
                }
            }
            size2 = webpage.cached_page.photos.size();
            for (b = 0; b < size2; b++) {
                result = getFileReference((Photo) webpage.cached_page.photos.get(b), location);
                if (result != null) {
                    bArr = result;
                    return result;
                }
            }
        }
        bArr = result;
        return null;
    }

    public static boolean isFileRefError(String error) {
        return "FILEREF_EXPIRED".equals(error) || "FILE_REFERENCE_EXPIRED".equals(error) || "FILE_REFERENCE_EMPTY".equals(error) || (error != null && error.startsWith("FILE_REFERENCE_"));
    }
}
