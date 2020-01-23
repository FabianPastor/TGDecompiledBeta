package org.telegram.messenger;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.InputStickeredMedia;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Page;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_account_getTheme;
import org.telegram.tgnet.TLRPC.TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_getWallPapers;
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
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhotoFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickeredMediaDocument;
import org.telegram.tgnet.TLRPC.TL_inputStickeredMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_inputTheme;
import org.telegram.tgnet.TLRPC.TL_inputWallPaper;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_faveSticker;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getChats;
import org.telegram.tgnet.TLRPC.TL_messages_getFavedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_getScheduledMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.tgnet.TLRPC.TL_users_getUsers;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_webPageAttributeTheme;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.WebPage;

public class FileRefController extends BaseController {
    private static volatile FileRefController[] Instance = new FileRefController[3];
    private long lastCleanupTime = SystemClock.elapsedRealtime();
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

    static /* synthetic */ void lambda$onUpdateObjectReference$23(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$24(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$25(TLObject tLObject, TL_error tL_error) {
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
        String str = "message";
        String str2 = "_";
        int channelId;
        StringBuilder stringBuilder;
        StringBuilder stringBuilder2;
        String str3;
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            channelId = messageObject.getChannelId();
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(messageObject.getRealId());
            stringBuilder.append(str2);
            stringBuilder.append(channelId);
            stringBuilder.append(str2);
            stringBuilder.append(messageObject.scheduled);
            return stringBuilder.toString();
        } else if (obj instanceof Message) {
            Message message = (Message) obj;
            Peer peer = message.to_id;
            channelId = peer != null ? peer.channel_id : 0;
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(message.id);
            stringBuilder.append(str2);
            stringBuilder.append(channelId);
            return stringBuilder.toString();
        } else if (obj instanceof WebPage) {
            WebPage webPage = (WebPage) obj;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("webpage");
            stringBuilder2.append(webPage.id);
            return stringBuilder2.toString();
        } else if (obj instanceof User) {
            User user = (User) obj;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("user");
            stringBuilder2.append(user.id);
            return stringBuilder2.toString();
        } else if (obj instanceof Chat) {
            Chat chat = (Chat) obj;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("chat");
            stringBuilder2.append(chat.id);
            return stringBuilder2.toString();
        } else if (obj instanceof String) {
            str3 = (String) obj;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("str");
            stringBuilder2.append(str3);
            return stringBuilder2.toString();
        } else {
            str = "set";
            if (obj instanceof TL_messages_stickerSet) {
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) obj;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(tL_messages_stickerSet.set.id);
                return stringBuilder2.toString();
            } else if (obj instanceof StickerSetCovered) {
                StickerSetCovered stickerSetCovered = (StickerSetCovered) obj;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(stickerSetCovered.set.id);
                return stringBuilder2.toString();
            } else if (obj instanceof InputStickerSet) {
                InputStickerSet inputStickerSet = (InputStickerSet) obj;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(inputStickerSet.id);
                return stringBuilder2.toString();
            } else if (obj instanceof TL_wallPaper) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("wallpaper");
                stringBuilder2.append(tL_wallPaper.id);
                return stringBuilder2.toString();
            } else if (obj instanceof TL_theme) {
                TL_theme tL_theme = (TL_theme) obj;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("theme");
                stringBuilder2.append(tL_theme.id);
                return stringBuilder2.toString();
            } else {
                if (obj != null) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("");
                    stringBuilder2.append(obj);
                    str3 = stringBuilder2.toString();
                } else {
                    str3 = null;
                }
                return str3;
            }
        }
    }

    public void requestReference(Object obj, Object... objArr) {
        StringBuilder stringBuilder;
        InputFileLocation tL_inputDocumentFileLocation;
        if (BuildVars.LOGS_ENABLED) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("start loading request reference for parent = ");
            stringBuilder.append(obj);
            stringBuilder.append(" args = ");
            stringBuilder.append(objArr[0]);
            FileLog.d(stringBuilder.toString());
        }
        int i = 1;
        String str = "photo_";
        String str2 = "file_";
        InputMedia inputMedia;
        TL_inputMediaDocument tL_inputMediaDocument;
        StringBuilder stringBuilder2;
        TL_inputMediaPhoto tL_inputMediaPhoto;
        StringBuilder stringBuilder3;
        if (objArr[0] instanceof TL_inputSingleMedia) {
            inputMedia = ((TL_inputSingleMedia) objArr[0]).media;
            if (inputMedia instanceof TL_inputMediaDocument) {
                tL_inputMediaDocument = (TL_inputMediaDocument) inputMedia;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str2);
                stringBuilder2.append(tL_inputMediaDocument.id.id);
                str = stringBuilder2.toString();
                tL_inputDocumentFileLocation = new TL_inputDocumentFileLocation();
                tL_inputDocumentFileLocation.id = tL_inputMediaDocument.id.id;
            } else if (inputMedia instanceof TL_inputMediaPhoto) {
                tL_inputMediaPhoto = (TL_inputMediaPhoto) inputMedia;
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append(str);
                stringBuilder3.append(tL_inputMediaPhoto.id.id);
                str = stringBuilder3.toString();
                tL_inputDocumentFileLocation = new TL_inputPhotoFileLocation();
                tL_inputDocumentFileLocation.id = tL_inputMediaPhoto.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TL_messages_sendMultiMedia) {
            TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TL_messages_sendMultiMedia) objArr[0];
            ArrayList arrayList = (ArrayList) obj;
            this.multiMediaCache.put(tL_messages_sendMultiMedia, objArr);
            int size = tL_messages_sendMultiMedia.multi_media.size();
            for (int i2 = 0; i2 < size; i2++) {
                TL_inputSingleMedia tL_inputSingleMedia = (TL_inputSingleMedia) tL_messages_sendMultiMedia.multi_media.get(i2);
                Object obj2 = arrayList.get(i2);
                if (obj2 != null) {
                    requestReference(obj2, tL_inputSingleMedia, tL_messages_sendMultiMedia);
                }
            }
            return;
        } else if (objArr[0] instanceof TL_messages_sendMedia) {
            inputMedia = ((TL_messages_sendMedia) objArr[0]).media;
            if (inputMedia instanceof TL_inputMediaDocument) {
                tL_inputMediaDocument = (TL_inputMediaDocument) inputMedia;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str2);
                stringBuilder2.append(tL_inputMediaDocument.id.id);
                str = stringBuilder2.toString();
                tL_inputDocumentFileLocation = new TL_inputDocumentFileLocation();
                tL_inputDocumentFileLocation.id = tL_inputMediaDocument.id.id;
            } else if (inputMedia instanceof TL_inputMediaPhoto) {
                tL_inputMediaPhoto = (TL_inputMediaPhoto) inputMedia;
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append(str);
                stringBuilder3.append(tL_inputMediaPhoto.id.id);
                str = stringBuilder3.toString();
                tL_inputDocumentFileLocation = new TL_inputPhotoFileLocation();
                tL_inputDocumentFileLocation.id = tL_inputMediaPhoto.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TL_messages_editMessage) {
            inputMedia = ((TL_messages_editMessage) objArr[0]).media;
            if (inputMedia instanceof TL_inputMediaDocument) {
                tL_inputMediaDocument = (TL_inputMediaDocument) inputMedia;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str2);
                stringBuilder2.append(tL_inputMediaDocument.id.id);
                str = stringBuilder2.toString();
                tL_inputDocumentFileLocation = new TL_inputDocumentFileLocation();
                tL_inputDocumentFileLocation.id = tL_inputMediaDocument.id.id;
            } else if (inputMedia instanceof TL_inputMediaPhoto) {
                tL_inputMediaPhoto = (TL_inputMediaPhoto) inputMedia;
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append(str);
                stringBuilder3.append(tL_inputMediaPhoto.id.id);
                str = stringBuilder3.toString();
                tL_inputDocumentFileLocation = new TL_inputPhotoFileLocation();
                tL_inputDocumentFileLocation.id = tL_inputMediaPhoto.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TL_messages_saveGif) {
            TL_messages_saveGif tL_messages_saveGif = (TL_messages_saveGif) objArr[0];
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str2);
            stringBuilder2.append(tL_messages_saveGif.id.id);
            str = stringBuilder2.toString();
            tL_inputDocumentFileLocation = new TL_inputDocumentFileLocation();
            tL_inputDocumentFileLocation.id = tL_messages_saveGif.id.id;
        } else if (objArr[0] instanceof TL_messages_saveRecentSticker) {
            TL_messages_saveRecentSticker tL_messages_saveRecentSticker = (TL_messages_saveRecentSticker) objArr[0];
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str2);
            stringBuilder2.append(tL_messages_saveRecentSticker.id.id);
            str = stringBuilder2.toString();
            tL_inputDocumentFileLocation = new TL_inputDocumentFileLocation();
            tL_inputDocumentFileLocation.id = tL_messages_saveRecentSticker.id.id;
        } else if (objArr[0] instanceof TL_messages_faveSticker) {
            TL_messages_faveSticker tL_messages_faveSticker = (TL_messages_faveSticker) objArr[0];
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str2);
            stringBuilder2.append(tL_messages_faveSticker.id.id);
            str = stringBuilder2.toString();
            tL_inputDocumentFileLocation = new TL_inputDocumentFileLocation();
            tL_inputDocumentFileLocation.id = tL_messages_faveSticker.id.id;
        } else if (objArr[0] instanceof TL_messages_getAttachedStickers) {
            InputStickeredMedia inputStickeredMedia = ((TL_messages_getAttachedStickers) objArr[0]).media;
            if (inputStickeredMedia instanceof TL_inputStickeredMediaDocument) {
                TL_inputStickeredMediaDocument tL_inputStickeredMediaDocument = (TL_inputStickeredMediaDocument) inputStickeredMedia;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str2);
                stringBuilder2.append(tL_inputStickeredMediaDocument.id.id);
                str = stringBuilder2.toString();
                tL_inputDocumentFileLocation = new TL_inputDocumentFileLocation();
                tL_inputDocumentFileLocation.id = tL_inputStickeredMediaDocument.id.id;
            } else if (inputStickeredMedia instanceof TL_inputStickeredMediaPhoto) {
                TL_inputStickeredMediaPhoto tL_inputStickeredMediaPhoto = (TL_inputStickeredMediaPhoto) inputStickeredMedia;
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append(str);
                stringBuilder3.append(tL_inputStickeredMediaPhoto.id.id);
                str = stringBuilder3.toString();
                tL_inputDocumentFileLocation = new TL_inputPhotoFileLocation();
                tL_inputDocumentFileLocation.id = tL_inputStickeredMediaPhoto.id.id;
            } else {
                sendErrorToObject(objArr, 0);
                return;
            }
        } else if (objArr[0] instanceof TL_inputFileLocation) {
            tL_inputDocumentFileLocation = (TL_inputFileLocation) objArr[0];
            stringBuilder = new StringBuilder();
            stringBuilder.append("loc_");
            stringBuilder.append(tL_inputDocumentFileLocation.local_id);
            stringBuilder.append("_");
            stringBuilder.append(tL_inputDocumentFileLocation.volume_id);
            str = stringBuilder.toString();
        } else if (objArr[0] instanceof TL_inputDocumentFileLocation) {
            InputFileLocation inputFileLocation = (TL_inputDocumentFileLocation) objArr[0];
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str2);
            stringBuilder2.append(inputFileLocation.id);
            str = stringBuilder2.toString();
            tL_inputDocumentFileLocation = inputFileLocation;
        } else if (objArr[0] instanceof TL_inputPhotoFileLocation) {
            tL_inputDocumentFileLocation = (TL_inputPhotoFileLocation) objArr[0];
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(tL_inputDocumentFileLocation.id);
            str = stringBuilder.toString();
        } else {
            sendErrorToObject(objArr, 0);
            return;
        }
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            if (messageObject.getRealId() < 0) {
                WebPage webPage = messageObject.messageOwner.media.webpage;
                if (webPage != null) {
                    obj = webPage;
                }
            }
        }
        String keyForParentObject = getKeyForParentObject(obj);
        if (keyForParentObject == null) {
            sendErrorToObject(objArr, 0);
            return;
        }
        Requester requester = new Requester();
        requester.args = objArr;
        requester.location = tL_inputDocumentFileLocation;
        requester.locationKey = str;
        ArrayList arrayList2 = (ArrayList) this.locationRequester.get(str);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList();
            this.locationRequester.put(str, arrayList2);
        } else {
            i = 0;
        }
        arrayList2.add(requester);
        arrayList2 = (ArrayList) this.parentRequester.get(keyForParentObject);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList();
            this.parentRequester.put(keyForParentObject, arrayList2);
            i++;
        }
        arrayList2.add(requester);
        if (i == 2) {
            cleanupCache();
            CachedResult cachedResponse = getCachedResponse(str);
            if (cachedResponse == null) {
                cachedResponse = getCachedResponse(keyForParentObject);
                if (cachedResponse != null) {
                    if (!onRequestComplete(str, keyForParentObject, cachedResponse.response, false)) {
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
        int channelId;
        TL_messages_getMessages tL_messages_getMessages;
        TL_messages_getStickerSet tL_messages_getStickerSet;
        InputStickerSet inputStickerSet;
        StickerSet stickerSet;
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            channelId = messageObject.getChannelId();
            if (messageObject.scheduled) {
                TL_messages_getScheduledMessages tL_messages_getScheduledMessages = new TL_messages_getScheduledMessages();
                tL_messages_getScheduledMessages.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
                tL_messages_getScheduledMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tL_messages_getScheduledMessages, new -$$Lambda$FileRefController$bJSiLyN_Loo2lNffdEwzSUZ6du0(this, str, str2));
            } else if (channelId != 0) {
                TL_channels_getMessages tL_channels_getMessages = new TL_channels_getMessages();
                tL_channels_getMessages.channel = getMessagesController().getInputChannel(channelId);
                tL_channels_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tL_channels_getMessages, new -$$Lambda$FileRefController$iH_RpP96708b-YCozHWKOTcxbr4(this, str, str2));
            } else {
                tL_messages_getMessages = new TL_messages_getMessages();
                tL_messages_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tL_messages_getMessages, new -$$Lambda$FileRefController$l7gUgVrfRVPQOPlPV4-4bUexFYw(this, str, str2));
            }
        } else if (obj instanceof TL_wallPaper) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
            TL_account_getWallPaper tL_account_getWallPaper = new TL_account_getWallPaper();
            TL_inputWallPaper tL_inputWallPaper = new TL_inputWallPaper();
            tL_inputWallPaper.id = tL_wallPaper.id;
            tL_inputWallPaper.access_hash = tL_wallPaper.access_hash;
            tL_account_getWallPaper.wallpaper = tL_inputWallPaper;
            getConnectionsManager().sendRequest(tL_account_getWallPaper, new -$$Lambda$FileRefController$ZZalHmw878mE45n2TIDtpdup6rk(this, str, str2));
        } else if (obj instanceof TL_theme) {
            TL_theme tL_theme = (TL_theme) obj;
            TL_account_getTheme tL_account_getTheme = new TL_account_getTheme();
            TL_inputTheme tL_inputTheme = new TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            tL_account_getTheme.theme = tL_inputTheme;
            tL_account_getTheme.format = "android";
            getConnectionsManager().sendRequest(tL_account_getTheme, new -$$Lambda$FileRefController$PPQViQbtvXgiD0HkQf0oK3_ZBkE(this, str, str2));
        } else if (obj instanceof WebPage) {
            WebPage webPage = (WebPage) obj;
            TL_messages_getWebPage tL_messages_getWebPage = new TL_messages_getWebPage();
            tL_messages_getWebPage.url = webPage.url;
            tL_messages_getWebPage.hash = 0;
            getConnectionsManager().sendRequest(tL_messages_getWebPage, new -$$Lambda$FileRefController$7qx1abCFn6GfdxglKDbFuGfloVQ(this, str, str2));
        } else if (obj instanceof User) {
            User user = (User) obj;
            TL_users_getUsers tL_users_getUsers = new TL_users_getUsers();
            tL_users_getUsers.id.add(getMessagesController().getInputUser(user));
            getConnectionsManager().sendRequest(tL_users_getUsers, new -$$Lambda$FileRefController$MN7WJPmFdFnXGwTYeLjLskDZA_s(this, str, str2));
        } else if (obj instanceof Chat) {
            Chat chat = (Chat) obj;
            if (chat instanceof TL_chat) {
                TL_messages_getChats tL_messages_getChats = new TL_messages_getChats();
                tL_messages_getChats.id.add(Integer.valueOf(chat.id));
                getConnectionsManager().sendRequest(tL_messages_getChats, new -$$Lambda$FileRefController$hU-_PNtgoV5UVJ0PgXNS0w6t-Ck(this, str, str2));
            } else if (chat instanceof TL_channel) {
                TL_channels_getChannels tL_channels_getChannels = new TL_channels_getChannels();
                tL_channels_getChannels.id.add(MessagesController.getInputChannel(chat));
                getConnectionsManager().sendRequest(tL_channels_getChannels, new -$$Lambda$FileRefController$XmvlggjDShdh6wwrH2NBXtZN860(this, str, str2));
            }
        } else if (obj instanceof String) {
            String str3 = (String) obj;
            if ("wallpaper".equals(str3)) {
                getConnectionsManager().sendRequest(new TL_account_getWallPapers(), new -$$Lambda$FileRefController$1aFkU4DGV4no_EoB8k2dbx7DLBY(this, str, str2));
            } else if (str3.startsWith("gif")) {
                getConnectionsManager().sendRequest(new TL_messages_getSavedGifs(), new -$$Lambda$FileRefController$4w92eKYUWTkjdDZrk7Ab6kZgCz4(this, str, str2));
            } else if ("recent".equals(str3)) {
                getConnectionsManager().sendRequest(new TL_messages_getRecentStickers(), new -$$Lambda$FileRefController$lc6KWNmCjlTmUjUWy2DU6yide1g(this, str, str2));
            } else if ("fav".equals(str3)) {
                getConnectionsManager().sendRequest(new TL_messages_getFavedStickers(), new -$$Lambda$FileRefController$WYAn7hVey6Q-NHtwvjaWCOxf8sA(this, str, str2));
            } else if (str3.startsWith("avatar_")) {
                int intValue = Utilities.parseInt(str3).intValue();
                if (intValue > 0) {
                    TL_photos_getUserPhotos tL_photos_getUserPhotos = new TL_photos_getUserPhotos();
                    tL_photos_getUserPhotos.limit = 80;
                    tL_photos_getUserPhotos.offset = 0;
                    tL_photos_getUserPhotos.max_id = 0;
                    tL_photos_getUserPhotos.user_id = getMessagesController().getInputUser(intValue);
                    getConnectionsManager().sendRequest(tL_photos_getUserPhotos, new -$$Lambda$FileRefController$AUpNheLWJQs7kyRgJ6tIGIQTBNY(this, str, str2));
                    return;
                }
                TL_messages_search tL_messages_search = new TL_messages_search();
                tL_messages_search.filter = new TL_inputMessagesFilterChatPhotos();
                tL_messages_search.limit = 80;
                tL_messages_search.offset_id = 0;
                tL_messages_search.q = "";
                tL_messages_search.peer = getMessagesController().getInputPeer(intValue);
                getConnectionsManager().sendRequest(tL_messages_search, new -$$Lambda$FileRefController$oHc4Ko0S36uJ174mUmftFNV5oEU(this, str, str2));
            } else if (str3.startsWith("sent_")) {
                String[] split = str3.split("_");
                if (split.length == 3) {
                    channelId = Utilities.parseInt(split[1]).intValue();
                    if (channelId != 0) {
                        TL_channels_getMessages tL_channels_getMessages2 = new TL_channels_getMessages();
                        tL_channels_getMessages2.channel = getMessagesController().getInputChannel(channelId);
                        tL_channels_getMessages2.id.add(Utilities.parseInt(split[2]));
                        getConnectionsManager().sendRequest(tL_channels_getMessages2, new -$$Lambda$FileRefController$exbLp78Kychyc6GzkLXULN8LNGg(this, str, str2));
                        return;
                    }
                    tL_messages_getMessages = new TL_messages_getMessages();
                    tL_messages_getMessages.id.add(Utilities.parseInt(split[2]));
                    getConnectionsManager().sendRequest(tL_messages_getMessages, new -$$Lambda$FileRefController$oQwONz9oH9KbjBHI1GVgQcuvYoU(this, str, str2));
                    return;
                }
                sendErrorToObject(objArr, 0);
            } else {
                sendErrorToObject(objArr, 0);
            }
        } else if (obj instanceof TL_messages_stickerSet) {
            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) obj;
            tL_messages_getStickerSet = new TL_messages_getStickerSet();
            tL_messages_getStickerSet.stickerset = new TL_inputStickerSetID();
            inputStickerSet = tL_messages_getStickerSet.stickerset;
            stickerSet = tL_messages_stickerSet.set;
            inputStickerSet.id = stickerSet.id;
            inputStickerSet.access_hash = stickerSet.access_hash;
            getConnectionsManager().sendRequest(tL_messages_getStickerSet, new -$$Lambda$FileRefController$taVyElfntghqykv41aff7_1-UM8(this, str, str2));
        } else if (obj instanceof StickerSetCovered) {
            StickerSetCovered stickerSetCovered = (StickerSetCovered) obj;
            tL_messages_getStickerSet = new TL_messages_getStickerSet();
            tL_messages_getStickerSet.stickerset = new TL_inputStickerSetID();
            inputStickerSet = tL_messages_getStickerSet.stickerset;
            stickerSet = stickerSetCovered.set;
            inputStickerSet.id = stickerSet.id;
            inputStickerSet.access_hash = stickerSet.access_hash;
            getConnectionsManager().sendRequest(tL_messages_getStickerSet, new -$$Lambda$FileRefController$NLI3_bovApy1_Ow_Ce8DVrjfkXY(this, str, str2));
        } else if (obj instanceof InputStickerSet) {
            tL_messages_getStickerSet = new TL_messages_getStickerSet();
            tL_messages_getStickerSet.stickerset = (InputStickerSet) obj;
            getConnectionsManager().sendRequest(tL_messages_getStickerSet, new -$$Lambda$FileRefController$fDaFG0iwCYPICuIQ6EQ-5sdKaSc(this, str, str2));
        } else {
            sendErrorToObject(objArr, 0);
        }
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$0$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$1$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$2$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$3$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$4$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$5$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$6$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$7$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$8$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$9$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$10$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$11$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$12$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$13$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$14$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$15$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, false);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$16$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, false);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$17$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$18$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$19$FileRefController(String str, String str2, TLObject tLObject, TL_error tL_error) {
        onRequestComplete(str, str2, tLObject, true);
    }

    private void onUpdateObjectReference(Requester requester, byte[] bArr, InputFileLocation inputFileLocation) {
        if (BuildVars.DEBUG_VERSION) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("fileref updated for ");
            stringBuilder.append(requester.args[0]);
            stringBuilder.append(" ");
            stringBuilder.append(requester.locationKey);
            FileLog.d(stringBuilder.toString());
        }
        int i = 1;
        InputMedia inputMedia;
        if (requester.args[0] instanceof TL_inputSingleMedia) {
            TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TL_messages_sendMultiMedia) requester.args[1];
            Object[] objArr = (Object[]) this.multiMediaCache.get(tL_messages_sendMultiMedia);
            if (objArr != null) {
                TL_inputSingleMedia tL_inputSingleMedia = (TL_inputSingleMedia) requester.args[0];
                InputMedia inputMedia2 = tL_inputSingleMedia.media;
                if (inputMedia2 instanceof TL_inputMediaDocument) {
                    ((TL_inputMediaDocument) inputMedia2).id.file_reference = bArr;
                } else if (inputMedia2 instanceof TL_inputMediaPhoto) {
                    ((TL_inputMediaPhoto) inputMedia2).id.file_reference = bArr;
                }
                int indexOf = tL_messages_sendMultiMedia.multi_media.indexOf(tL_inputSingleMedia);
                if (indexOf >= 0) {
                    ArrayList arrayList = (ArrayList) objArr[3];
                    arrayList.set(indexOf, null);
                    for (indexOf = 0; indexOf < arrayList.size(); indexOf++) {
                        if (arrayList.get(indexOf) != null) {
                            i = 0;
                        }
                    }
                    if (i != 0) {
                        this.multiMediaCache.remove(tL_messages_sendMultiMedia);
                        AndroidUtilities.runOnUIThread(new -$$Lambda$FileRefController$VgdcA9EaLIhDdlvN-ujnTFb_q1E(this, tL_messages_sendMultiMedia, objArr));
                    }
                }
            }
        } else if (requester.args[0] instanceof TL_messages_sendMedia) {
            inputMedia = ((TL_messages_sendMedia) requester.args[0]).media;
            if (inputMedia instanceof TL_inputMediaDocument) {
                ((TL_inputMediaDocument) inputMedia).id.file_reference = bArr;
            } else if (inputMedia instanceof TL_inputMediaPhoto) {
                ((TL_inputMediaPhoto) inputMedia).id.file_reference = bArr;
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$FileRefController$1zpnUJNBrU6DBGS-JQg1_hSnrvE(this, requester));
        } else if (requester.args[0] instanceof TL_messages_editMessage) {
            inputMedia = ((TL_messages_editMessage) requester.args[0]).media;
            if (inputMedia instanceof TL_inputMediaDocument) {
                ((TL_inputMediaDocument) inputMedia).id.file_reference = bArr;
            } else if (inputMedia instanceof TL_inputMediaPhoto) {
                ((TL_inputMediaPhoto) inputMedia).id.file_reference = bArr;
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$FileRefController$LQx2jXezBZ6lvpleIJww70xS9AA(this, requester));
        } else if (requester.args[0] instanceof TL_messages_saveGif) {
            TL_messages_saveGif tL_messages_saveGif = (TL_messages_saveGif) requester.args[0];
            tL_messages_saveGif.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tL_messages_saveGif, -$$Lambda$FileRefController$B8e86bveS3knOZa0Cg1HvRKtz44.INSTANCE);
        } else if (requester.args[0] instanceof TL_messages_saveRecentSticker) {
            TL_messages_saveRecentSticker tL_messages_saveRecentSticker = (TL_messages_saveRecentSticker) requester.args[0];
            tL_messages_saveRecentSticker.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tL_messages_saveRecentSticker, -$$Lambda$FileRefController$9VZxhYR0Ao1KW4hoVDK7k1sUZVI.INSTANCE);
        } else if (requester.args[0] instanceof TL_messages_faveSticker) {
            TL_messages_faveSticker tL_messages_faveSticker = (TL_messages_faveSticker) requester.args[0];
            tL_messages_faveSticker.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tL_messages_faveSticker, -$$Lambda$FileRefController$9vHgXK5LhN8UmJHe149yk5CGvC8.INSTANCE);
        } else if (requester.args[0] instanceof TL_messages_getAttachedStickers) {
            TL_messages_getAttachedStickers tL_messages_getAttachedStickers = (TL_messages_getAttachedStickers) requester.args[0];
            InputStickeredMedia inputStickeredMedia = tL_messages_getAttachedStickers.media;
            if (inputStickeredMedia instanceof TL_inputStickeredMediaDocument) {
                ((TL_inputStickeredMediaDocument) inputStickeredMedia).id.file_reference = bArr;
            } else if (inputStickeredMedia instanceof TL_inputStickeredMediaPhoto) {
                ((TL_inputStickeredMediaPhoto) inputStickeredMedia).id.file_reference = bArr;
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

    public /* synthetic */ void lambda$onUpdateObjectReference$20$FileRefController(TL_messages_sendMultiMedia tL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, (ArrayList) objArr[1], (ArrayList) objArr[2], null, (DelayedMessage) objArr[4], ((Boolean) objArr[5]).booleanValue());
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$21$FileRefController(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (DelayedMessage) requester.args[5], null, ((Boolean) requester.args[6]).booleanValue());
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$22$FileRefController(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (DelayedMessage) requester.args[5], null, ((Boolean) requester.args[6]).booleanValue());
    }

    private void sendErrorToObject(Object[] objArr, int i) {
        if (objArr[0] instanceof TL_inputSingleMedia) {
            TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TL_messages_sendMultiMedia) objArr[1];
            Object[] objArr2 = (Object[]) this.multiMediaCache.get(tL_messages_sendMultiMedia);
            if (objArr2 != null) {
                this.multiMediaCache.remove(tL_messages_sendMultiMedia);
                AndroidUtilities.runOnUIThread(new -$$Lambda$FileRefController$aeJobPtYqWiH7OZqZRslXDjvNEk(this, tL_messages_sendMultiMedia, objArr2));
            }
        } else if ((objArr[0] instanceof TL_messages_sendMedia) || (objArr[0] instanceof TL_messages_editMessage)) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$FileRefController$aOKUj2HwIwYMYPjujIv_D4C_mTE(this, objArr));
        } else if (objArr[0] instanceof TL_messages_saveGif) {
            TL_messages_saveGif tL_messages_saveGif = (TL_messages_saveGif) objArr[0];
        } else if (objArr[0] instanceof TL_messages_saveRecentSticker) {
            TL_messages_saveRecentSticker tL_messages_saveRecentSticker = (TL_messages_saveRecentSticker) objArr[0];
        } else if (objArr[0] instanceof TL_messages_faveSticker) {
            TL_messages_faveSticker tL_messages_faveSticker = (TL_messages_faveSticker) objArr[0];
        } else if (objArr[0] instanceof TL_messages_getAttachedStickers) {
            getConnectionsManager().sendRequest((TL_messages_getAttachedStickers) objArr[0], (RequestDelegate) objArr[1]);
        } else if (i == 0) {
            TL_error tL_error = new TL_error();
            tL_error.text = "not found parent object to request reference";
            tL_error.code = 400;
            if (objArr[1] instanceof FileLoadOperation) {
                FileLoadOperation fileLoadOperation = (FileLoadOperation) objArr[1];
                fileLoadOperation.requestingReference = false;
                fileLoadOperation.processRequestResult((RequestInfo) objArr[2], tL_error);
            }
        } else if (i == 1 && (objArr[1] instanceof FileLoadOperation)) {
            FileLoadOperation fileLoadOperation2 = (FileLoadOperation) objArr[1];
            fileLoadOperation2.requestingReference = false;
            fileLoadOperation2.onFail(false, 0);
        }
    }

    public /* synthetic */ void lambda$sendErrorToObject$26$FileRefController(TL_messages_sendMultiMedia tL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, (ArrayList) objArr[1], (ArrayList) objArr[2], null, (DelayedMessage) objArr[4], ((Boolean) objArr[5]).booleanValue());
    }

    public /* synthetic */ void lambda$sendErrorToObject$27$FileRefController(Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) objArr[0], (MessageObject) objArr[1], (String) objArr[2], (DelayedMessage) objArr[3], ((Boolean) objArr[4]).booleanValue(), (DelayedMessage) objArr[5], null, ((Boolean) objArr[6]).booleanValue());
    }

    /* JADX WARNING: Removed duplicated region for block: B:78:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0153 A:{LOOP_END, LOOP:2: B:37:0x009b->B:74:0x0153} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0103 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0103 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0153 A:{LOOP_END, LOOP:2: B:37:0x009b->B:74:0x0153} */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0415  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0056 A:{RETURN} */
    private boolean onRequestComplete(java.lang.String r25, java.lang.String r26, org.telegram.tgnet.TLObject r27, boolean r28) {
        /*
        r24 = this;
        r0 = r24;
        r1 = r25;
        r2 = r26;
        r3 = r27;
        r4 = 0;
        r6 = 1;
        if (r2 == 0) goto L_0x004b;
    L_0x000c:
        r7 = r0.parentRequester;
        r7 = r7.get(r2);
        r7 = (java.util.ArrayList) r7;
        if (r7 == 0) goto L_0x004b;
    L_0x0016:
        r8 = r7.size();
        r9 = 0;
        r10 = 0;
    L_0x001c:
        if (r9 >= r8) goto L_0x0040;
    L_0x001e:
        r11 = r7.get(r9);
        r11 = (org.telegram.messenger.FileRefController.Requester) r11;
        r12 = r11.completed;
        if (r12 == 0) goto L_0x002b;
    L_0x002a:
        goto L_0x003d;
    L_0x002b:
        r11 = r11.locationKey;
        if (r28 == 0) goto L_0x0035;
    L_0x0031:
        if (r10 != 0) goto L_0x0035;
    L_0x0033:
        r12 = 1;
        goto L_0x0036;
    L_0x0035:
        r12 = 0;
    L_0x0036:
        r11 = r0.onRequestComplete(r11, r4, r3, r12);
        if (r11 == 0) goto L_0x003d;
    L_0x003c:
        r10 = 1;
    L_0x003d:
        r9 = r9 + 1;
        goto L_0x001c;
    L_0x0040:
        if (r10 == 0) goto L_0x0045;
    L_0x0042:
        r0.putReponseToCache(r2, r3);
    L_0x0045:
        r7 = r0.parentRequester;
        r7.remove(r2);
        goto L_0x004c;
    L_0x004b:
        r10 = 0;
    L_0x004c:
        r2 = r0.locationRequester;
        r2 = r2.get(r1);
        r2 = (java.util.ArrayList) r2;
        if (r2 != 0) goto L_0x0057;
    L_0x0056:
        return r10;
    L_0x0057:
        r7 = r2.size();
        r9 = r4;
        r11 = r9;
        r12 = r11;
        r8 = 0;
    L_0x005f:
        if (r8 >= r7) goto L_0x0426;
    L_0x0061:
        r13 = r2.get(r8);
        r13 = (org.telegram.messenger.FileRefController.Requester) r13;
        r14 = r13.completed;
        if (r14 == 0) goto L_0x0074;
    L_0x006d:
        r26 = r2;
        r14 = r4;
        r2 = 0;
        r5 = 1;
        goto L_0x041e;
    L_0x0074:
        r14 = r13.location;
        r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_inputFileLocation;
        if (r14 == 0) goto L_0x0080;
    L_0x007c:
        r11 = new org.telegram.tgnet.TLRPC.InputFileLocation[r6];
        r9 = new boolean[r6];
    L_0x0080:
        r13.completed = r6;
        r14 = r3 instanceof org.telegram.tgnet.TLRPC.messages_Messages;
        if (r14 == 0) goto L_0x018e;
    L_0x0087:
        r14 = r3;
        r14 = (org.telegram.tgnet.TLRPC.messages_Messages) r14;
        r15 = r14.messages;
        r15 = r15.isEmpty();
        if (r15 != 0) goto L_0x018b;
    L_0x0092:
        r15 = r14.messages;
        r15 = r15.size();
        r16 = r12;
        r12 = 0;
    L_0x009b:
        if (r12 >= r15) goto L_0x015b;
    L_0x009d:
        r4 = r14.messages;
        r4 = r4.get(r12);
        r4 = (org.telegram.tgnet.TLRPC.Message) r4;
        r6 = r4.media;
        if (r6 == 0) goto L_0x00ef;
    L_0x00a9:
        r5 = r6.document;
        if (r5 == 0) goto L_0x00b6;
    L_0x00ad:
        r6 = r13.location;
        r5 = r0.getFileReference(r5, r6, r9, r11);
        goto L_0x00ff;
    L_0x00b6:
        r5 = r6.game;
        if (r5 == 0) goto L_0x00d5;
    L_0x00ba:
        r5 = r5.document;
        r6 = r13.location;
        r5 = r0.getFileReference(r5, r6, r9, r11);
        if (r5 != 0) goto L_0x00ff;
    L_0x00c6:
        r5 = r4.media;
        r5 = r5.game;
        r5 = r5.photo;
        r6 = r13.location;
        r5 = r0.getFileReference(r5, r6, r9, r11);
        goto L_0x00ff;
    L_0x00d5:
        r5 = r6.photo;
        if (r5 == 0) goto L_0x00e2;
    L_0x00d9:
        r6 = r13.location;
        r5 = r0.getFileReference(r5, r6, r9, r11);
        goto L_0x00ff;
    L_0x00e2:
        r5 = r6.webpage;
        if (r5 == 0) goto L_0x0101;
    L_0x00e6:
        r6 = r13.location;
        r5 = r0.getFileReference(r5, r6, r9, r11);
        goto L_0x00ff;
    L_0x00ef:
        r5 = r4.action;
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
        if (r6 == 0) goto L_0x0101;
    L_0x00f5:
        r5 = r5.photo;
        r6 = r13.location;
        r5 = r0.getFileReference(r5, r6, r9, r11);
    L_0x00ff:
        r16 = r5;
    L_0x0101:
        if (r16 == 0) goto L_0x0153;
    L_0x0103:
        if (r28 == 0) goto L_0x015b;
    L_0x0105:
        r5 = r4.to_id;
        if (r5 == 0) goto L_0x0139;
    L_0x0109:
        r5 = r5.channel_id;
        if (r5 == 0) goto L_0x0139;
    L_0x010d:
        r5 = r14.chats;
        r5 = r5.size();
        r6 = 0;
    L_0x0114:
        if (r6 >= r5) goto L_0x0139;
    L_0x0116:
        r12 = r14.chats;
        r12 = r12.get(r6);
        r12 = (org.telegram.tgnet.TLRPC.Chat) r12;
        r15 = r12.id;
        r26 = r2;
        r2 = r4.to_id;
        r2 = r2.channel_id;
        if (r15 != r2) goto L_0x0134;
    L_0x0128:
        r2 = r12.megagroup;
        if (r2 == 0) goto L_0x013b;
    L_0x012c:
        r2 = r4.flags;
        r5 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r2 = r2 | r5;
        r4.flags = r2;
        goto L_0x013b;
    L_0x0134:
        r6 = r6 + 1;
        r2 = r26;
        goto L_0x0114;
    L_0x0139:
        r26 = r2;
    L_0x013b:
        r18 = r24.getMessagesStorage();
        r2 = r0.currentAccount;
        r5 = r14.users;
        r6 = r14.chats;
        r23 = 0;
        r19 = r4;
        r20 = r2;
        r21 = r5;
        r22 = r6;
        r18.replaceMessageIfExists(r19, r20, r21, r22, r23);
        goto L_0x015d;
    L_0x0153:
        r26 = r2;
        r12 = r12 + 1;
        r4 = 0;
        r6 = 1;
        goto L_0x009b;
    L_0x015b:
        r26 = r2;
    L_0x015d:
        r12 = r16;
        if (r12 != 0) goto L_0x019f;
    L_0x0161:
        r18 = r24.getMessagesStorage();
        r2 = r14.messages;
        r4 = 0;
        r2 = r2.get(r4);
        r19 = r2;
        r19 = (org.telegram.tgnet.TLRPC.Message) r19;
        r2 = r0.currentAccount;
        r4 = r14.users;
        r5 = r14.chats;
        r23 = 1;
        r20 = r2;
        r21 = r4;
        r22 = r5;
        r18.replaceMessageIfExists(r19, r20, r21, r22, r23);
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;
        if (r2 == 0) goto L_0x019f;
    L_0x0185:
        r2 = "file ref not found in messages, replacing message";
        org.telegram.messenger.FileLog.d(r2);
        goto L_0x019f;
    L_0x018b:
        r26 = r2;
        goto L_0x019f;
    L_0x018e:
        r26 = r2;
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.WebPage;
        if (r2 == 0) goto L_0x01a2;
    L_0x0194:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.WebPage) r2;
        r4 = r13.location;
        r12 = r0.getFileReference(r2, r4, r9, r11);
    L_0x019f:
        r14 = 0;
        goto L_0x0406;
    L_0x01a2:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_account_wallPapers;
        if (r2 == 0) goto L_0x01d9;
    L_0x01a6:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_account_wallPapers) r2;
        r4 = r2.wallpapers;
        r4 = r4.size();
        r5 = 0;
    L_0x01b0:
        if (r5 >= r4) goto L_0x01ca;
    L_0x01b2:
        r6 = r2.wallpapers;
        r6 = r6.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r6;
        r6 = r6.document;
        r12 = r13.location;
        r12 = r0.getFileReference(r6, r12, r9, r11);
        if (r12 == 0) goto L_0x01c7;
    L_0x01c6:
        goto L_0x01ca;
    L_0x01c7:
        r5 = r5 + 1;
        goto L_0x01b0;
    L_0x01ca:
        if (r12 == 0) goto L_0x019f;
    L_0x01cc:
        if (r28 == 0) goto L_0x019f;
    L_0x01ce:
        r4 = r24.getMessagesStorage();
        r2 = r2.wallpapers;
        r5 = 1;
        r4.putWallpapers(r2, r5);
        goto L_0x019f;
    L_0x01d9:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper;
        if (r2 == 0) goto L_0x01ff;
    L_0x01dd:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r2;
        r4 = r2.document;
        r5 = r13.location;
        r12 = r0.getFileReference(r4, r5, r9, r11);
        if (r12 == 0) goto L_0x019f;
    L_0x01ec:
        if (r28 == 0) goto L_0x019f;
    L_0x01ee:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r4.add(r2);
        r2 = r24.getMessagesStorage();
        r5 = 0;
        r2.putWallpapers(r4, r5);
        goto L_0x019f;
    L_0x01ff:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_theme;
        if (r2 == 0) goto L_0x021d;
    L_0x0203:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_theme) r2;
        r4 = r2.document;
        r5 = r13.location;
        r12 = r0.getFileReference(r4, r5, r9, r11);
        if (r12 == 0) goto L_0x019f;
    L_0x0212:
        if (r28 == 0) goto L_0x019f;
    L_0x0214:
        r4 = new org.telegram.messenger.-$$Lambda$FileRefController$SNJwoq0kr06ZjWyihuJvKSpuIz0;
        r4.<init>(r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
        goto L_0x019f;
    L_0x021d:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.Vector;
        if (r2 == 0) goto L_0x02ab;
    L_0x0221:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.Vector) r2;
        r4 = r2.objects;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x019f;
    L_0x022c:
        r4 = r2.objects;
        r4 = r4.size();
        r5 = 0;
    L_0x0233:
        if (r5 >= r4) goto L_0x019f;
    L_0x0235:
        r6 = r2.objects;
        r6 = r6.get(r5);
        r14 = r6 instanceof org.telegram.tgnet.TLRPC.User;
        if (r14 == 0) goto L_0x0270;
    L_0x023f:
        r6 = (org.telegram.tgnet.TLRPC.User) r6;
        r12 = r13.location;
        r12 = r0.getFileReference(r6, r12, r9, r11);
        if (r28 == 0) goto L_0x026b;
    L_0x024b:
        if (r12 == 0) goto L_0x026b;
    L_0x024d:
        r14 = new java.util.ArrayList;
        r14.<init>();
        r14.add(r6);
        r15 = r24.getMessagesStorage();
        r16 = r2;
        r18 = r4;
        r2 = 0;
        r4 = 1;
        r15.putUsersAndChats(r14, r2, r4, r4);
        r2 = new org.telegram.messenger.-$$Lambda$FileRefController$_4y5prO3JWgmruc7MhfACo84xB8;
        r2.<init>(r0, r6);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
        goto L_0x02a0;
    L_0x026b:
        r16 = r2;
        r18 = r4;
        goto L_0x02a0;
    L_0x0270:
        r16 = r2;
        r18 = r4;
        r2 = r6 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r2 == 0) goto L_0x02a0;
    L_0x0278:
        r6 = (org.telegram.tgnet.TLRPC.Chat) r6;
        r2 = r13.location;
        r2 = r0.getFileReference(r6, r2, r9, r11);
        if (r28 == 0) goto L_0x029f;
    L_0x0284:
        if (r2 == 0) goto L_0x029f;
    L_0x0286:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r4.add(r6);
        r12 = r24.getMessagesStorage();
        r14 = 0;
        r15 = 1;
        r12.putUsersAndChats(r14, r4, r15, r15);
        r4 = new org.telegram.messenger.-$$Lambda$FileRefController$e8OtXvlo5kjEKEtnDDj1BG232-0;
        r4.<init>(r0, r6);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x029f:
        r12 = r2;
    L_0x02a0:
        if (r12 == 0) goto L_0x02a4;
    L_0x02a2:
        goto L_0x019f;
    L_0x02a4:
        r5 = r5 + 1;
        r2 = r16;
        r4 = r18;
        goto L_0x0233;
    L_0x02ab:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_chats;
        if (r2 == 0) goto L_0x02f6;
    L_0x02af:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_messages_chats) r2;
        r4 = r2.chats;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x019f;
    L_0x02ba:
        r4 = r2.chats;
        r4 = r4.size();
        r5 = 0;
    L_0x02c1:
        if (r5 >= r4) goto L_0x019f;
    L_0x02c3:
        r6 = r2.chats;
        r6 = r6.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.Chat) r6;
        r12 = r13.location;
        r12 = r0.getFileReference(r6, r12, r9, r11);
        if (r12 == 0) goto L_0x02f2;
    L_0x02d5:
        if (r28 == 0) goto L_0x019f;
    L_0x02d7:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r2.add(r6);
        r4 = r24.getMessagesStorage();
        r5 = 1;
        r14 = 0;
        r4.putUsersAndChats(r14, r2, r5, r5);
        r2 = new org.telegram.messenger.-$$Lambda$FileRefController$tUyca42NNsTxyQpe3m9-DSncaao;
        r2.<init>(r0, r6);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
        goto L_0x0406;
    L_0x02f2:
        r14 = 0;
        r5 = r5 + 1;
        goto L_0x02c1;
    L_0x02f6:
        r14 = 0;
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_savedGifs;
        if (r2 == 0) goto L_0x0334;
    L_0x02fb:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_messages_savedGifs) r2;
        r4 = r2.gifs;
        r4 = r4.size();
        r5 = 0;
    L_0x0305:
        if (r5 >= r4) goto L_0x031d;
    L_0x0307:
        r6 = r2.gifs;
        r6 = r6.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r12 = r13.location;
        r12 = r0.getFileReference(r6, r12, r9, r11);
        if (r12 == 0) goto L_0x031a;
    L_0x0319:
        goto L_0x031d;
    L_0x031a:
        r5 = r5 + 1;
        goto L_0x0305;
    L_0x031d:
        if (r28 == 0) goto L_0x0406;
    L_0x031f:
        r15 = r24.getMediaDataController();
        r16 = 0;
        r2 = r2.gifs;
        r18 = 1;
        r19 = 0;
        r20 = 1;
        r17 = r2;
        r15.processLoadedRecentDocuments(r16, r17, r18, r19, r20);
        goto L_0x0406;
    L_0x0334:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
        if (r2 == 0) goto L_0x0368;
    L_0x0338:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r2;
        if (r12 != 0) goto L_0x035c;
    L_0x033d:
        r4 = r2.documents;
        r4 = r4.size();
        r5 = 0;
    L_0x0344:
        if (r5 >= r4) goto L_0x035c;
    L_0x0346:
        r6 = r2.documents;
        r6 = r6.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r12 = r13.location;
        r12 = r0.getFileReference(r6, r12, r9, r11);
        if (r12 == 0) goto L_0x0359;
    L_0x0358:
        goto L_0x035c;
    L_0x0359:
        r5 = r5 + 1;
        goto L_0x0344;
    L_0x035c:
        if (r28 == 0) goto L_0x0406;
    L_0x035e:
        r4 = new org.telegram.messenger.-$$Lambda$FileRefController$q_nWD12uKB_1zPGAdz0IXAdyGJg;
        r4.<init>(r0, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
        goto L_0x0406;
    L_0x0368:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_recentStickers;
        if (r2 == 0) goto L_0x03a4;
    L_0x036c:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_messages_recentStickers) r2;
        r4 = r2.stickers;
        r4 = r4.size();
        r5 = 0;
    L_0x0376:
        if (r5 >= r4) goto L_0x038e;
    L_0x0378:
        r6 = r2.stickers;
        r6 = r6.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r12 = r13.location;
        r12 = r0.getFileReference(r6, r12, r9, r11);
        if (r12 == 0) goto L_0x038b;
    L_0x038a:
        goto L_0x038e;
    L_0x038b:
        r5 = r5 + 1;
        goto L_0x0376;
    L_0x038e:
        if (r28 == 0) goto L_0x0406;
    L_0x0390:
        r15 = r24.getMediaDataController();
        r16 = 0;
        r2 = r2.stickers;
        r18 = 0;
        r19 = 0;
        r20 = 1;
        r17 = r2;
        r15.processLoadedRecentDocuments(r16, r17, r18, r19, r20);
        goto L_0x0406;
    L_0x03a4:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messages_favedStickers;
        if (r2 == 0) goto L_0x03e0;
    L_0x03a8:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_messages_favedStickers) r2;
        r4 = r2.stickers;
        r4 = r4.size();
        r5 = 0;
    L_0x03b2:
        if (r5 >= r4) goto L_0x03ca;
    L_0x03b4:
        r6 = r2.stickers;
        r6 = r6.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r12 = r13.location;
        r12 = r0.getFileReference(r6, r12, r9, r11);
        if (r12 == 0) goto L_0x03c7;
    L_0x03c6:
        goto L_0x03ca;
    L_0x03c7:
        r5 = r5 + 1;
        goto L_0x03b2;
    L_0x03ca:
        if (r28 == 0) goto L_0x0406;
    L_0x03cc:
        r15 = r24.getMediaDataController();
        r16 = 2;
        r2 = r2.stickers;
        r18 = 0;
        r19 = 0;
        r20 = 1;
        r17 = r2;
        r15.processLoadedRecentDocuments(r16, r17, r18, r19, r20);
        goto L_0x0406;
    L_0x03e0:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.photos_Photos;
        if (r2 == 0) goto L_0x0406;
    L_0x03e4:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.photos_Photos) r2;
        r4 = r2.photos;
        r4 = r4.size();
        r5 = 0;
    L_0x03ee:
        if (r5 >= r4) goto L_0x0406;
    L_0x03f0:
        r6 = r2.photos;
        r6 = r6.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.Photo) r6;
        r12 = r13.location;
        r12 = r0.getFileReference(r6, r12, r9, r11);
        if (r12 == 0) goto L_0x0403;
    L_0x0402:
        goto L_0x0406;
    L_0x0403:
        r5 = r5 + 1;
        goto L_0x03ee;
    L_0x0406:
        if (r12 == 0) goto L_0x0415;
    L_0x0408:
        r2 = 0;
        if (r11 == 0) goto L_0x040e;
    L_0x040b:
        r4 = r11[r2];
        goto L_0x040f;
    L_0x040e:
        r4 = r14;
    L_0x040f:
        r0.onUpdateObjectReference(r13, r12, r4);
        r5 = 1;
        r10 = 1;
        goto L_0x041e;
    L_0x0415:
        r2 = 0;
        r4 = r13.args;
        r5 = 1;
        r0.sendErrorToObject(r4, r5);
    L_0x041e:
        r8 = r8 + 1;
        r2 = r26;
        r4 = r14;
        r6 = 1;
        goto L_0x005f;
    L_0x0426:
        r2 = r0.locationRequester;
        r2.remove(r1);
        if (r10 == 0) goto L_0x0430;
    L_0x042d:
        r0.putReponseToCache(r1, r3);
    L_0x0430:
        return r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.onRequestComplete(java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, boolean):boolean");
    }

    public /* synthetic */ void lambda$onRequestComplete$29$FileRefController(User user) {
        getMessagesController().putUser(user, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$30$FileRefController(Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$31$FileRefController(Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$32$FileRefController(TL_messages_stickerSet tL_messages_stickerSet) {
        getMediaDataController().replaceStickerSet(tL_messages_stickerSet);
    }

    private void cleanupCache() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastCleanupTime) >= 600000) {
            this.lastCleanupTime = SystemClock.elapsedRealtime();
            ArrayList arrayList = null;
            for (Entry entry : this.responseCache.entrySet()) {
                if (Math.abs(SystemClock.elapsedRealtime() - ((CachedResult) entry.getValue()).firstQueryTime) >= 600000) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(entry.getKey());
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
        CachedResult cachedResult = (CachedResult) this.responseCache.get(str);
        if (cachedResult == null || Math.abs(SystemClock.elapsedRealtime() - cachedResult.firstQueryTime) < 600000) {
            return cachedResult;
        }
        this.responseCache.remove(str);
        return null;
    }

    private void putReponseToCache(String str, TLObject tLObject) {
        CachedResult cachedResult = (CachedResult) this.responseCache.get(str);
        if (cachedResult == null) {
            cachedResult = new CachedResult();
            cachedResult.response = tLObject;
            cachedResult.firstQueryTime = SystemClock.uptimeMillis();
            this.responseCache.put(str, cachedResult);
        }
        cachedResult.lastQueryTime = SystemClock.uptimeMillis();
    }

    private byte[] getFileReference(Document document, InputFileLocation inputFileLocation, boolean[] zArr, InputFileLocation[] inputFileLocationArr) {
        if (!(document == null || inputFileLocation == null)) {
            if (!(inputFileLocation instanceof TL_inputDocumentFileLocation)) {
                int size = document.thumbs.size();
                int i = 0;
                while (i < size) {
                    PhotoSize photoSize = (PhotoSize) document.thumbs.get(i);
                    byte[] fileReference = getFileReference(photoSize, inputFileLocation, zArr);
                    if (zArr != null && zArr[0]) {
                        inputFileLocationArr[0] = new TL_inputDocumentFileLocation();
                        inputFileLocationArr[0].id = document.id;
                        inputFileLocationArr[0].volume_id = inputFileLocation.volume_id;
                        inputFileLocationArr[0].local_id = inputFileLocation.local_id;
                        inputFileLocationArr[0].access_hash = document.access_hash;
                        inputFileLocation = inputFileLocationArr[0];
                        byte[] bArr = document.file_reference;
                        inputFileLocation.file_reference = bArr;
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

    private boolean getPeerReferenceReplacement(User user, Chat chat, boolean z, InputFileLocation inputFileLocation, InputFileLocation[] inputFileLocationArr, boolean[] zArr) {
        if (zArr == null || !zArr[0]) {
            return false;
        }
        InputPeer inputPeer;
        inputFileLocationArr[0] = new TL_inputPeerPhotoFileLocation();
        InputFileLocation inputFileLocation2 = inputFileLocationArr[0];
        long j = inputFileLocation.volume_id;
        inputFileLocation2.id = j;
        inputFileLocationArr[0].volume_id = j;
        inputFileLocationArr[0].local_id = inputFileLocation.local_id;
        inputFileLocationArr[0].big = z;
        if (user != null) {
            InputPeer tL_inputPeerUser = new TL_inputPeerUser();
            tL_inputPeerUser.user_id = user.id;
            tL_inputPeerUser.access_hash = user.access_hash;
            inputPeer = tL_inputPeerUser;
        } else if (ChatObject.isChannel(chat)) {
            inputPeer = new TL_inputPeerChat();
            inputPeer.chat_id = chat.id;
        } else {
            inputPeer = new TL_inputPeerChannel();
            inputPeer.channel_id = chat.id;
            inputPeer.access_hash = chat.access_hash;
        }
        inputFileLocationArr[0].peer = inputPeer;
        return true;
    }

    private byte[] getFileReference(User user, InputFileLocation inputFileLocation, boolean[] zArr, InputFileLocation[] inputFileLocationArr) {
        if (user != null) {
            UserProfilePhoto userProfilePhoto = user.photo;
            if (userProfilePhoto != null && (inputFileLocation instanceof TL_inputFileLocation)) {
                byte[] fileReference = getFileReference(userProfilePhoto.photo_small, inputFileLocation, zArr);
                if (getPeerReferenceReplacement(user, null, false, inputFileLocation, inputFileLocationArr, zArr)) {
                    return new byte[0];
                }
                if (fileReference == null) {
                    fileReference = getFileReference(user.photo.photo_big, inputFileLocation, zArr);
                    if (getPeerReferenceReplacement(user, null, true, inputFileLocation, inputFileLocationArr, zArr)) {
                        return new byte[0];
                    }
                }
                return fileReference;
            }
        }
        return null;
    }

    private byte[] getFileReference(Chat chat, InputFileLocation inputFileLocation, boolean[] zArr, InputFileLocation[] inputFileLocationArr) {
        if (chat != null) {
            ChatPhoto chatPhoto = chat.photo;
            if (chatPhoto != null && (inputFileLocation instanceof TL_inputFileLocation)) {
                byte[] fileReference = getFileReference(chatPhoto.photo_small, inputFileLocation, zArr);
                if (getPeerReferenceReplacement(null, chat, false, inputFileLocation, inputFileLocationArr, zArr)) {
                    return new byte[0];
                }
                if (fileReference == null) {
                    fileReference = getFileReference(chat.photo.photo_big, inputFileLocation, zArr);
                    if (getPeerReferenceReplacement(null, chat, true, inputFileLocation, inputFileLocationArr, zArr)) {
                        return new byte[0];
                    }
                }
                return fileReference;
            }
        }
        return null;
    }

    private byte[] getFileReference(Photo photo, InputFileLocation inputFileLocation, boolean[] zArr, InputFileLocation[] inputFileLocationArr) {
        byte[] bArr = null;
        if (photo == null) {
            return null;
        }
        if (inputFileLocation instanceof TL_inputPhotoFileLocation) {
            if (photo.id == inputFileLocation.id) {
                bArr = photo.file_reference;
            }
            return bArr;
        }
        if (inputFileLocation instanceof TL_inputFileLocation) {
            int size = photo.sizes.size();
            int i = 0;
            while (i < size) {
                PhotoSize photoSize = (PhotoSize) photo.sizes.get(i);
                byte[] fileReference = getFileReference(photoSize, inputFileLocation, zArr);
                if (zArr != null && zArr[0]) {
                    inputFileLocationArr[0] = new TL_inputPhotoFileLocation();
                    inputFileLocationArr[0].id = photo.id;
                    inputFileLocationArr[0].volume_id = inputFileLocation.volume_id;
                    inputFileLocationArr[0].local_id = inputFileLocation.local_id;
                    inputFileLocationArr[0].access_hash = photo.access_hash;
                    inputFileLocation = inputFileLocationArr[0];
                    byte[] bArr2 = photo.file_reference;
                    inputFileLocation.file_reference = bArr2;
                    inputFileLocationArr[0].thumb_size = photoSize.type;
                    return bArr2;
                } else if (fileReference != null) {
                    return fileReference;
                } else {
                    i++;
                }
            }
        }
        return null;
    }

    private byte[] getFileReference(PhotoSize photoSize, InputFileLocation inputFileLocation, boolean[] zArr) {
        return (photoSize == null || !(inputFileLocation instanceof TL_inputFileLocation)) ? null : getFileReference(photoSize.location, inputFileLocation, zArr);
    }

    private byte[] getFileReference(FileLocation fileLocation, InputFileLocation inputFileLocation, boolean[] zArr) {
        if (fileLocation == null || !(inputFileLocation instanceof TL_inputFileLocation) || fileLocation.local_id != inputFileLocation.local_id || fileLocation.volume_id != inputFileLocation.volume_id) {
            return null;
        }
        if (fileLocation.file_reference == null && zArr != null) {
            zArr[0] = true;
        }
        return fileLocation.file_reference;
    }

    private byte[] getFileReference(WebPage webPage, InputFileLocation inputFileLocation, boolean[] zArr, InputFileLocation[] inputFileLocationArr) {
        byte[] fileReference = getFileReference(webPage.document, inputFileLocation, zArr, inputFileLocationArr);
        if (fileReference != null) {
            return fileReference;
        }
        fileReference = getFileReference(webPage.photo, inputFileLocation, zArr, inputFileLocationArr);
        if (fileReference != null) {
            return fileReference;
        }
        int size;
        int i;
        if (!webPage.attributes.isEmpty()) {
            size = webPage.attributes.size();
            for (i = 0; i < size; i++) {
                TL_webPageAttributeTheme tL_webPageAttributeTheme = (TL_webPageAttributeTheme) webPage.attributes.get(i);
                int size2 = tL_webPageAttributeTheme.documents.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    byte[] fileReference2 = getFileReference((Document) tL_webPageAttributeTheme.documents.get(i2), inputFileLocation, zArr, inputFileLocationArr);
                    if (fileReference2 != null) {
                        return fileReference2;
                    }
                }
            }
        }
        Page page = webPage.cached_page;
        if (page != null) {
            size = page.documents.size();
            for (i = 0; i < size; i++) {
                byte[] fileReference3 = getFileReference((Document) webPage.cached_page.documents.get(i), inputFileLocation, zArr, inputFileLocationArr);
                if (fileReference3 != null) {
                    return fileReference3;
                }
            }
            size = webPage.cached_page.photos.size();
            for (int i3 = 0; i3 < size; i3++) {
                byte[] fileReference4 = getFileReference((Photo) webPage.cached_page.photos.get(i3), inputFileLocation, zArr, inputFileLocationArr);
                if (fileReference4 != null) {
                    return fileReference4;
                }
            }
        }
        return null;
    }

    public static boolean isFileRefError(String str) {
        return "FILEREF_EXPIRED".equals(str) || "FILE_REFERENCE_EXPIRED".equals(str) || "FILE_REFERENCE_EMPTY".equals(str) || (str != null && str.startsWith("FILE_REFERENCE_"));
    }
}
