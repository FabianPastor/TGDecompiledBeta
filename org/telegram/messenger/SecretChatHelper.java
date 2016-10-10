package org.telegram.messenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLClassStore;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.DecryptedMessage;
import org.telegram.tgnet.TLRPC.DecryptedMessageAction;
import org.telegram.tgnet.TLRPC.DecryptedMessageMedia;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.EncryptedFile;
import org.telegram.tgnet.TLRPC.EncryptedMessage;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAbortKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAcceptKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionCommitKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionFlushHistory;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNoop;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionReadMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionRequestKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionResend;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageLayer;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaAudio;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaContact;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument_layer8;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaGeoPoint;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageService;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageService_layer8;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_encryptedFile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_acceptEncryption;
import org.telegram.tgnet.TLRPC.TL_messages_dhConfig;
import org.telegram.tgnet.TLRPC.TL_messages_discardEncryption;
import org.telegram.tgnet.TLRPC.TL_messages_getDhConfig;
import org.telegram.tgnet.TLRPC.TL_messages_requestEncryption;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncrypted;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedFile;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedService;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_updateEncryption;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_DhConfig;
import org.telegram.tgnet.TLRPC.messages_SentEncryptedMessage;

public class SecretChatHelper
{
  public static final int CURRENT_SECRET_CHAT_LAYER = 46;
  private static volatile SecretChatHelper Instance = null;
  private HashMap<Integer, TLRPC.EncryptedChat> acceptingChats = new HashMap();
  public ArrayList<TLRPC.Update> delayedEncryptedChatUpdates = new ArrayList();
  private ArrayList<Long> pendingEncMessagesToDelete = new ArrayList();
  private HashMap<Integer, ArrayList<TL_decryptedMessageHolder>> secretHolesQueue = new HashMap();
  private ArrayList<Integer> sendingNotifyLayer = new ArrayList();
  private boolean startingSecretChat = false;
  
  private void applyPeerLayer(final TLRPC.EncryptedChat paramEncryptedChat, int paramInt)
  {
    int i = AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer);
    if (paramInt <= i) {
      return;
    }
    if ((paramEncryptedChat.key_hash.length == 16) && (i >= 46)) {}
    try
    {
      byte[] arrayOfByte1 = Utilities.computeSHA256(paramEncryptedChat.auth_key, 0, paramEncryptedChat.auth_key.length);
      byte[] arrayOfByte2 = new byte[36];
      System.arraycopy(paramEncryptedChat.key_hash, 0, arrayOfByte2, 0, 16);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 16, 20);
      paramEncryptedChat.key_hash = arrayOfByte2;
      MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
      paramEncryptedChat.layer = AndroidUtilities.setPeerLayerVersion(paramEncryptedChat.layer, paramInt);
      MessagesStorage.getInstance().updateEncryptedChatLayer(paramEncryptedChat);
      if (i < 46) {
        sendNotifyLayerMessage(paramEncryptedChat, null);
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { paramEncryptedChat });
        }
      });
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        FileLog.e("tmessages", localThrowable);
      }
    }
  }
  
  private TLRPC.Message createDeleteMessage(int paramInt1, int paramInt2, int paramInt3, long paramLong, TLRPC.EncryptedChat paramEncryptedChat)
  {
    TLRPC.TL_messageService localTL_messageService = new TLRPC.TL_messageService();
    localTL_messageService.action = new TLRPC.TL_messageEncryptedAction();
    localTL_messageService.action.encryptedAction = new TLRPC.TL_decryptedMessageActionDeleteMessages();
    localTL_messageService.action.encryptedAction.random_ids.add(Long.valueOf(paramLong));
    localTL_messageService.id = paramInt1;
    localTL_messageService.local_id = paramInt1;
    localTL_messageService.from_id = UserConfig.getClientUserId();
    localTL_messageService.unread = true;
    localTL_messageService.out = true;
    localTL_messageService.flags = 256;
    localTL_messageService.dialog_id = (paramEncryptedChat.id << 32);
    localTL_messageService.to_id = new TLRPC.TL_peerUser();
    localTL_messageService.send_state = 1;
    localTL_messageService.seq_in = paramInt3;
    localTL_messageService.seq_out = paramInt2;
    if (paramEncryptedChat.participant_id == UserConfig.getClientUserId()) {}
    for (localTL_messageService.to_id.user_id = paramEncryptedChat.admin_id;; localTL_messageService.to_id.user_id = paramEncryptedChat.participant_id)
    {
      localTL_messageService.date = 0;
      localTL_messageService.random_id = paramLong;
      return localTL_messageService;
    }
  }
  
  private TLRPC.TL_messageService createServiceSecretMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.DecryptedMessageAction paramDecryptedMessageAction)
  {
    TLRPC.TL_messageService localTL_messageService = new TLRPC.TL_messageService();
    localTL_messageService.action = new TLRPC.TL_messageEncryptedAction();
    localTL_messageService.action.encryptedAction = paramDecryptedMessageAction;
    int i = UserConfig.getNewMessageId();
    localTL_messageService.id = i;
    localTL_messageService.local_id = i;
    localTL_messageService.from_id = UserConfig.getClientUserId();
    localTL_messageService.unread = true;
    localTL_messageService.out = true;
    localTL_messageService.flags = 256;
    localTL_messageService.dialog_id = (paramEncryptedChat.id << 32);
    localTL_messageService.to_id = new TLRPC.TL_peerUser();
    localTL_messageService.send_state = 1;
    if (paramEncryptedChat.participant_id == UserConfig.getClientUserId())
    {
      localTL_messageService.to_id.user_id = paramEncryptedChat.admin_id;
      if ((!(paramDecryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) && (!(paramDecryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))) {
        break label211;
      }
    }
    label211:
    for (localTL_messageService.date = ConnectionsManager.getInstance().getCurrentTime();; localTL_messageService.date = 0)
    {
      localTL_messageService.random_id = SendMessagesHelper.getInstance().getNextRandomId();
      UserConfig.saveConfig(false);
      paramEncryptedChat = new ArrayList();
      paramEncryptedChat.add(localTL_messageService);
      MessagesStorage.getInstance().putMessages(paramEncryptedChat, false, true, true, 0);
      return localTL_messageService;
      localTL_messageService.to_id.user_id = paramEncryptedChat.participant_id;
      break;
    }
  }
  
  public static SecretChatHelper getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          SecretChatHelper localSecretChatHelper2 = Instance;
          localObject1 = localSecretChatHelper2;
          if (localSecretChatHelper2 == null) {
            localObject1 = new SecretChatHelper();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (SecretChatHelper)localObject1;
          return (SecretChatHelper)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localSecretChatHelper1;
  }
  
  public static boolean isSecretInvisibleMessage(TLRPC.Message paramMessage)
  {
    return ((paramMessage.action instanceof TLRPC.TL_messageEncryptedAction)) && (!(paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) && (!(paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL));
  }
  
  public static boolean isSecretVisibleMessage(TLRPC.Message paramMessage)
  {
    return ((paramMessage.action instanceof TLRPC.TL_messageEncryptedAction)) && (((paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) || ((paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)));
  }
  
  private void resendMessages(final int paramInt1, final int paramInt2, final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null) {
      return;
    }
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        int i;
        Object localObject3;
        final ArrayList localArrayList;
        SQLiteCursor localSQLiteCursor;
        for (;;)
        {
          try
          {
            int j = paramInt1;
            i = j;
            if (paramEncryptedChat.admin_id == UserConfig.getClientUserId())
            {
              i = j;
              if (j % 2 == 0) {
                i = j + 1;
              }
            }
            Object localObject1 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", new Object[] { Integer.valueOf(paramEncryptedChat.id), Integer.valueOf(i), Integer.valueOf(i), Integer.valueOf(paramInt2), Integer.valueOf(paramInt2) }), new Object[0]);
            boolean bool = ((SQLiteCursor)localObject1).next();
            ((SQLiteCursor)localObject1).dispose();
            if (bool) {
              return;
            }
            long l3 = paramEncryptedChat.id << 32;
            localObject3 = new HashMap();
            localArrayList = new ArrayList();
            j = i;
            if (j < paramInt2)
            {
              ((HashMap)localObject3).put(Integer.valueOf(j), null);
              j += 2;
            }
            else
            {
              localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms as r ON r.mid = s.mid LEFT JOIN messages as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[] { Long.valueOf(l3), Integer.valueOf(i), Integer.valueOf(paramInt2) }), new Object[0]);
              if (!localSQLiteCursor.next()) {
                break;
              }
              long l2 = localSQLiteCursor.longValue(1);
              long l1 = l2;
              if (l2 == 0L) {
                l1 = Utilities.random.nextLong();
              }
              j = localSQLiteCursor.intValue(2);
              int k = localSQLiteCursor.intValue(3);
              int m = localSQLiteCursor.intValue(5);
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
              if (localNativeByteBuffer != null)
              {
                localObject1 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                ((TLRPC.Message)localObject1).random_id = l1;
                ((TLRPC.Message)localObject1).dialog_id = l3;
                ((TLRPC.Message)localObject1).seq_in = j;
                ((TLRPC.Message)localObject1).seq_out = k;
                ((TLRPC.Message)localObject1).ttl = localSQLiteCursor.intValue(4);
                localArrayList.add(localObject1);
                ((HashMap)localObject3).remove(Integer.valueOf(k));
              }
              else
              {
                localObject2 = SecretChatHelper.this.createDeleteMessage(m, k, j, l1, paramEncryptedChat);
              }
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
        }
        localSQLiteCursor.dispose();
        if (!((HashMap)localObject3).isEmpty())
        {
          localObject2 = ((HashMap)localObject3).entrySet().iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = (Map.Entry)((Iterator)localObject2).next();
            localArrayList.add(SecretChatHelper.this.createDeleteMessage(UserConfig.getNewMessageId(), ((Integer)((Map.Entry)localObject3).getKey()).intValue(), 0, Utilities.random.nextLong(), paramEncryptedChat));
          }
          UserConfig.saveConfig(false);
        }
        Collections.sort(localArrayList, new Comparator()
        {
          public int compare(TLRPC.Message paramAnonymous2Message1, TLRPC.Message paramAnonymous2Message2)
          {
            return AndroidUtilities.compare(paramAnonymous2Message1.seq_out, paramAnonymous2Message2.seq_out);
          }
        });
        Object localObject2 = new ArrayList();
        ((ArrayList)localObject2).add(paramEncryptedChat);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int i = 0;
            while (i < localArrayList.size())
            {
              MessageObject localMessageObject = new MessageObject((TLRPC.Message)localArrayList.get(i), null, false);
              localMessageObject.resendAsIs = true;
              SendMessagesHelper.getInstance().retrySendMessage(localMessageObject, true);
              i += 1;
            }
          }
        });
        SendMessagesHelper.getInstance().processUnsentMessages(localArrayList, new ArrayList(), new ArrayList(), (ArrayList)localObject2);
        MessagesStorage.getInstance().getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[] { Integer.valueOf(paramEncryptedChat.id), Integer.valueOf(i), Integer.valueOf(paramInt2) })).stepThis().dispose();
      }
    });
  }
  
  private void updateMediaPaths(MessageObject paramMessageObject, TLRPC.EncryptedFile paramEncryptedFile, TLRPC.DecryptedMessage paramDecryptedMessage, String paramString)
  {
    paramString = paramMessageObject.messageOwner;
    if (paramEncryptedFile != null)
    {
      if ((!(paramString.media instanceof TLRPC.TL_messageMediaPhoto)) || (paramString.media.photo == null)) {
        break label308;
      }
      paramMessageObject = (TLRPC.PhotoSize)paramString.media.photo.sizes.get(paramString.media.photo.sizes.size() - 1);
      localObject = paramMessageObject.location.volume_id + "_" + paramMessageObject.location.local_id;
      paramMessageObject.location = new TLRPC.TL_fileEncryptedLocation();
      paramMessageObject.location.key = paramDecryptedMessage.media.key;
      paramMessageObject.location.iv = paramDecryptedMessage.media.iv;
      paramMessageObject.location.dc_id = paramEncryptedFile.dc_id;
      paramMessageObject.location.volume_id = paramEncryptedFile.id;
      paramMessageObject.location.secret = paramEncryptedFile.access_hash;
      paramMessageObject.location.local_id = paramEncryptedFile.key_fingerprint;
      paramEncryptedFile = paramMessageObject.location.volume_id + "_" + paramMessageObject.location.local_id;
      new File(FileLoader.getInstance().getDirectory(4), (String)localObject + ".jpg").renameTo(FileLoader.getPathToAttach(paramMessageObject));
      ImageLoader.getInstance().replaceImageInCache((String)localObject, paramEncryptedFile, paramMessageObject.location, true);
      paramMessageObject = new ArrayList();
      paramMessageObject.add(paramString);
      MessagesStorage.getInstance().putMessages(paramMessageObject, false, true, false, 0);
    }
    label308:
    while ((!(paramString.media instanceof TLRPC.TL_messageMediaDocument)) || (paramString.media.document == null)) {
      return;
    }
    Object localObject = paramString.media.document;
    paramString.media.document = new TLRPC.TL_documentEncrypted();
    paramString.media.document.id = paramEncryptedFile.id;
    paramString.media.document.access_hash = paramEncryptedFile.access_hash;
    paramString.media.document.date = ((TLRPC.Document)localObject).date;
    paramString.media.document.attributes = ((TLRPC.Document)localObject).attributes;
    paramString.media.document.mime_type = ((TLRPC.Document)localObject).mime_type;
    paramString.media.document.size = paramEncryptedFile.size;
    paramString.media.document.key = paramDecryptedMessage.media.key;
    paramString.media.document.iv = paramDecryptedMessage.media.iv;
    paramString.media.document.thumb = ((TLRPC.Document)localObject).thumb;
    paramString.media.document.dc_id = paramEncryptedFile.dc_id;
    paramDecryptedMessage = paramString.media.document;
    if (((TLRPC.Document)localObject).caption != null) {}
    for (paramEncryptedFile = ((TLRPC.Document)localObject).caption;; paramEncryptedFile = "")
    {
      paramDecryptedMessage.caption = paramEncryptedFile;
      if ((paramString.attachPath != null) && (paramString.attachPath.startsWith(FileLoader.getInstance().getDirectory(4).getAbsolutePath())) && (new File(paramString.attachPath).renameTo(FileLoader.getPathToAttach(paramString.media.document))))
      {
        paramMessageObject.mediaExists = paramMessageObject.attachPathExists;
        paramMessageObject.attachPathExists = false;
        paramString.attachPath = "";
      }
      paramMessageObject = new ArrayList();
      paramMessageObject.add(paramString);
      MessagesStorage.getInstance().putMessages(paramMessageObject, false, true, false, 0);
      return;
    }
  }
  
  public void acceptSecretChat(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (this.acceptingChats.get(Integer.valueOf(paramEncryptedChat.id)) != null) {
      return;
    }
    this.acceptingChats.put(Integer.valueOf(paramEncryptedChat.id), paramEncryptedChat);
    TLRPC.TL_messages_getDhConfig localTL_messages_getDhConfig = new TLRPC.TL_messages_getDhConfig();
    localTL_messages_getDhConfig.random_length = 256;
    localTL_messages_getDhConfig.version = MessagesStorage.lastSecretVersion;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_getDhConfig, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTL_error = (TLRPC.messages_DhConfig)paramAnonymousTLObject;
          if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_dhConfig))
          {
            if (!Utilities.isGoodPrime(paramAnonymousTL_error.p, paramAnonymousTL_error.g))
            {
              SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(paramEncryptedChat.id));
              SecretChatHelper.this.declineSecretChat(paramEncryptedChat.id);
              return;
            }
            MessagesStorage.secretPBytes = paramAnonymousTL_error.p;
            MessagesStorage.secretG = paramAnonymousTL_error.g;
            MessagesStorage.lastSecretVersion = paramAnonymousTL_error.version;
            MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
          }
          byte[] arrayOfByte = new byte['Ā'];
          int i = 0;
          while (i < 256)
          {
            arrayOfByte[i] = ((byte)((byte)(int)(Utilities.random.nextDouble() * 256.0D) ^ paramAnonymousTL_error.random[i]));
            i += 1;
          }
          paramEncryptedChat.a_or_b = arrayOfByte;
          paramEncryptedChat.seq_in = 1;
          paramEncryptedChat.seq_out = 0;
          Object localObject = new BigInteger(1, MessagesStorage.secretPBytes);
          paramAnonymousTLObject = BigInteger.valueOf(MessagesStorage.secretG).modPow(new BigInteger(1, arrayOfByte), (BigInteger)localObject);
          BigInteger localBigInteger = new BigInteger(1, paramEncryptedChat.g_a);
          if (!Utilities.isGoodGaAndGb(localBigInteger, (BigInteger)localObject))
          {
            SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(paramEncryptedChat.id));
            SecretChatHelper.this.declineSecretChat(paramEncryptedChat.id);
            return;
          }
          paramAnonymousTLObject = paramAnonymousTLObject.toByteArray();
          paramAnonymousTL_error = paramAnonymousTLObject;
          if (paramAnonymousTLObject.length > 256)
          {
            paramAnonymousTL_error = new byte['Ā'];
            System.arraycopy(paramAnonymousTLObject, 1, paramAnonymousTL_error, 0, 256);
          }
          arrayOfByte = localBigInteger.modPow(new BigInteger(1, arrayOfByte), (BigInteger)localObject).toByteArray();
          if (arrayOfByte.length > 256)
          {
            paramAnonymousTLObject = new byte['Ā'];
            System.arraycopy(arrayOfByte, arrayOfByte.length - 256, paramAnonymousTLObject, 0, 256);
          }
          for (;;)
          {
            localObject = Utilities.computeSHA1(paramAnonymousTLObject);
            arrayOfByte = new byte[8];
            System.arraycopy(localObject, localObject.length - 8, arrayOfByte, 0, 8);
            paramEncryptedChat.auth_key = paramAnonymousTLObject;
            paramEncryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
            paramAnonymousTLObject = new TLRPC.TL_messages_acceptEncryption();
            paramAnonymousTLObject.g_b = paramAnonymousTL_error;
            paramAnonymousTLObject.peer = new TLRPC.TL_inputEncryptedChat();
            paramAnonymousTLObject.peer.chat_id = paramEncryptedChat.id;
            paramAnonymousTLObject.peer.access_hash = paramEncryptedChat.access_hash;
            paramAnonymousTLObject.key_fingerprint = Utilities.bytesToLong(arrayOfByte);
            ConnectionsManager.getInstance().sendRequest(paramAnonymousTLObject, new RequestDelegate()
            {
              public void run(final TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
              {
                SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(SecretChatHelper.13.this.val$encryptedChat.id));
                if (paramAnonymous2TL_error == null)
                {
                  paramAnonymous2TLObject = (TLRPC.EncryptedChat)paramAnonymous2TLObject;
                  paramAnonymous2TLObject.auth_key = SecretChatHelper.13.this.val$encryptedChat.auth_key;
                  paramAnonymous2TLObject.user_id = SecretChatHelper.13.this.val$encryptedChat.user_id;
                  paramAnonymous2TLObject.seq_in = SecretChatHelper.13.this.val$encryptedChat.seq_in;
                  paramAnonymous2TLObject.seq_out = SecretChatHelper.13.this.val$encryptedChat.seq_out;
                  paramAnonymous2TLObject.key_create_date = SecretChatHelper.13.this.val$encryptedChat.key_create_date;
                  paramAnonymous2TLObject.key_use_count_in = SecretChatHelper.13.this.val$encryptedChat.key_use_count_in;
                  paramAnonymous2TLObject.key_use_count_out = SecretChatHelper.13.this.val$encryptedChat.key_use_count_out;
                  MessagesStorage.getInstance().updateEncryptedChat(paramAnonymous2TLObject);
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.getInstance().putEncryptedChat(paramAnonymous2TLObject, false);
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { paramAnonymous2TLObject });
                      SecretChatHelper.this.sendNotifyLayerMessage(paramAnonymous2TLObject, null);
                    }
                  });
                }
              }
            });
            return;
            paramAnonymousTLObject = arrayOfByte;
            if (arrayOfByte.length < 256)
            {
              paramAnonymousTLObject = new byte['Ā'];
              System.arraycopy(arrayOfByte, 0, paramAnonymousTLObject, 256 - arrayOfByte.length, arrayOfByte.length);
              i = 0;
              while (i < 256 - arrayOfByte.length)
              {
                arrayOfByte[i] = 0;
                i += 1;
              }
            }
          }
        }
        SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(paramEncryptedChat.id));
      }
    });
  }
  
  public void checkSecretHoles(TLRPC.EncryptedChat paramEncryptedChat, ArrayList<TLRPC.Message> paramArrayList)
  {
    ArrayList localArrayList = (ArrayList)this.secretHolesQueue.get(Integer.valueOf(paramEncryptedChat.id));
    if (localArrayList == null) {}
    int j;
    do
    {
      return;
      Collections.sort(localArrayList, new Comparator()
      {
        public int compare(SecretChatHelper.TL_decryptedMessageHolder paramAnonymousTL_decryptedMessageHolder1, SecretChatHelper.TL_decryptedMessageHolder paramAnonymousTL_decryptedMessageHolder2)
        {
          if (paramAnonymousTL_decryptedMessageHolder1.layer.out_seq_no > paramAnonymousTL_decryptedMessageHolder2.layer.out_seq_no) {
            return 1;
          }
          if (paramAnonymousTL_decryptedMessageHolder1.layer.out_seq_no < paramAnonymousTL_decryptedMessageHolder2.layer.out_seq_no) {
            return -1;
          }
          return 0;
        }
      });
      j = 0;
      for (int i = 0; localArrayList.size() > 0; i = i - 1 + 1)
      {
        Object localObject = (TL_decryptedMessageHolder)localArrayList.get(i);
        if ((((TL_decryptedMessageHolder)localObject).layer.out_seq_no != paramEncryptedChat.seq_in) && (paramEncryptedChat.seq_in != ((TL_decryptedMessageHolder)localObject).layer.out_seq_no - 2)) {
          break;
        }
        applyPeerLayer(paramEncryptedChat, ((TL_decryptedMessageHolder)localObject).layer.layer);
        paramEncryptedChat.seq_in = ((TL_decryptedMessageHolder)localObject).layer.out_seq_no;
        localArrayList.remove(i);
        j = 1;
        localObject = processDecryptedObject(paramEncryptedChat, ((TL_decryptedMessageHolder)localObject).file, ((TL_decryptedMessageHolder)localObject).date, ((TL_decryptedMessageHolder)localObject).random_id, ((TL_decryptedMessageHolder)localObject).layer.message, ((TL_decryptedMessageHolder)localObject).new_key_used);
        if (localObject != null) {
          paramArrayList.add(localObject);
        }
      }
      if (localArrayList.isEmpty()) {
        this.secretHolesQueue.remove(Integer.valueOf(paramEncryptedChat.id));
      }
    } while (j == 0);
    MessagesStorage.getInstance().updateEncryptedChatSeq(paramEncryptedChat);
  }
  
  public void cleanup()
  {
    this.sendingNotifyLayer.clear();
    this.acceptingChats.clear();
    this.secretHolesQueue.clear();
    this.delayedEncryptedChatUpdates.clear();
    this.pendingEncMessagesToDelete.clear();
    this.startingSecretChat = false;
  }
  
  public void declineSecretChat(int paramInt)
  {
    TLRPC.TL_messages_discardEncryption localTL_messages_discardEncryption = new TLRPC.TL_messages_discardEncryption();
    localTL_messages_discardEncryption.chat_id = paramInt;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_discardEncryption, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  protected ArrayList<TLRPC.Message> decryptMessage(final TLRPC.EncryptedMessage paramEncryptedMessage)
  {
    TLRPC.EncryptedChat localEncryptedChat = MessagesController.getInstance().getEncryptedChatDB(paramEncryptedMessage.chat_id);
    if ((localEncryptedChat == null) || ((localEncryptedChat instanceof TLRPC.TL_encryptedChatDiscarded))) {
      return null;
    }
    for (;;)
    {
      Object localObject3;
      long l;
      boolean bool1;
      try
      {
        localObject3 = new NativeByteBuffer(paramEncryptedMessage.bytes.length);
        ((NativeByteBuffer)localObject3).writeBytes(paramEncryptedMessage.bytes);
        ((NativeByteBuffer)localObject3).position(0);
        l = ((NativeByteBuffer)localObject3).readInt64(false);
        localObject2 = null;
        boolean bool2 = false;
        int i;
        if (localEncryptedChat.key_fingerprint == l)
        {
          localObject1 = localEncryptedChat.auth_key;
          bool1 = bool2;
          if (localObject1 == null) {
            break label883;
          }
          localObject2 = ((NativeByteBuffer)localObject3).readData(16, false);
          localObject1 = MessageKeyData.generateMessageKeyData((byte[])localObject1, (byte[])localObject2, false);
          Utilities.aesIgeEncryption(((NativeByteBuffer)localObject3).buffer, ((MessageKeyData)localObject1).aesKey, ((MessageKeyData)localObject1).aesIv, false, false, 24, ((NativeByteBuffer)localObject3).limit() - 24);
          i = ((NativeByteBuffer)localObject3).readInt32(false);
          if (i < 0) {
            break;
          }
          if (i > ((NativeByteBuffer)localObject3).limit() - 28) {
            break;
          }
        }
        else
        {
          bool1 = bool2;
          localObject1 = localObject2;
          if (localEncryptedChat.future_key_fingerprint == 0L) {
            continue;
          }
          bool1 = bool2;
          localObject1 = localObject2;
          if (localEncryptedChat.future_key_fingerprint != l) {
            continue;
          }
          localObject1 = localEncryptedChat.future_auth_key;
          bool1 = true;
          continue;
        }
        localObject1 = Utilities.computeSHA1(((NativeByteBuffer)localObject3).buffer, 24, Math.min(i + 4 + 24, ((NativeByteBuffer)localObject3).buffer.limit()));
        if (!Utilities.arraysEquals((byte[])localObject2, 0, (byte[])localObject1, localObject1.length - 16)) {
          return null;
        }
        localObject2 = TLClassStore.Instance().TLdeserialize((NativeByteBuffer)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
        ((NativeByteBuffer)localObject3).reuse();
        if ((!bool1) && (AndroidUtilities.getPeerLayerVersion(localEncryptedChat.layer) >= 20)) {
          localEncryptedChat.key_use_count_in = ((short)(localEncryptedChat.key_use_count_in + 1));
        }
        localObject1 = localObject2;
        if (!(localObject2 instanceof TLRPC.TL_decryptedMessageLayer)) {
          break label829;
        }
        localObject3 = (TLRPC.TL_decryptedMessageLayer)localObject2;
        if ((localEncryptedChat.seq_in == 0) && (localEncryptedChat.seq_out == 0))
        {
          if (localEncryptedChat.admin_id == UserConfig.getClientUserId()) {
            localEncryptedChat.seq_out = 1;
          }
        }
        else
        {
          if (((TLRPC.TL_decryptedMessageLayer)localObject3).random_bytes.length >= 15) {
            break label412;
          }
          FileLog.e("tmessages", "got random bytes less than needed");
          return null;
        }
        localEncryptedChat.seq_in = 1;
        continue;
        FileLog.e("tmessages", "current chat in_seq = " + localEncryptedChat.seq_in + " out_seq = " + localEncryptedChat.seq_out);
      }
      catch (Exception paramEncryptedMessage)
      {
        FileLog.e("tmessages", paramEncryptedMessage);
        return null;
      }
      label412:
      FileLog.e("tmessages", "got message with in_seq = " + ((TLRPC.TL_decryptedMessageLayer)localObject3).in_seq_no + " out_seq = " + ((TLRPC.TL_decryptedMessageLayer)localObject3).out_seq_no);
      if (((TLRPC.TL_decryptedMessageLayer)localObject3).out_seq_no < localEncryptedChat.seq_in) {
        return null;
      }
      if ((localEncryptedChat.seq_in != ((TLRPC.TL_decryptedMessageLayer)localObject3).out_seq_no) && (localEncryptedChat.seq_in != ((TLRPC.TL_decryptedMessageLayer)localObject3).out_seq_no - 2))
      {
        FileLog.e("tmessages", "got hole");
        localObject2 = (ArrayList)this.secretHolesQueue.get(Integer.valueOf(localEncryptedChat.id));
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new ArrayList();
          this.secretHolesQueue.put(Integer.valueOf(localEncryptedChat.id), localObject1);
        }
        if (((ArrayList)localObject1).size() >= 4)
        {
          this.secretHolesQueue.remove(Integer.valueOf(localEncryptedChat.id));
          paramEncryptedMessage = new TLRPC.TL_encryptedChatDiscarded();
          paramEncryptedMessage.id = localEncryptedChat.id;
          paramEncryptedMessage.user_id = localEncryptedChat.user_id;
          paramEncryptedMessage.auth_key = localEncryptedChat.auth_key;
          paramEncryptedMessage.key_create_date = localEncryptedChat.key_create_date;
          paramEncryptedMessage.key_use_count_in = localEncryptedChat.key_use_count_in;
          paramEncryptedMessage.key_use_count_out = localEncryptedChat.key_use_count_out;
          paramEncryptedMessage.seq_in = localEncryptedChat.seq_in;
          paramEncryptedMessage.seq_out = localEncryptedChat.seq_out;
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.getInstance().putEncryptedChat(paramEncryptedMessage, false);
              MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedMessage);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { paramEncryptedMessage });
            }
          });
          declineSecretChat(localEncryptedChat.id);
          return null;
        }
        localObject2 = new TL_decryptedMessageHolder();
        ((TL_decryptedMessageHolder)localObject2).layer = ((TLRPC.TL_decryptedMessageLayer)localObject3);
        ((TL_decryptedMessageHolder)localObject2).file = paramEncryptedMessage.file;
        ((TL_decryptedMessageHolder)localObject2).random_id = paramEncryptedMessage.random_id;
        ((TL_decryptedMessageHolder)localObject2).date = paramEncryptedMessage.date;
        ((TL_decryptedMessageHolder)localObject2).new_key_used = bool1;
        ((ArrayList)localObject1).add(localObject2);
        return null;
      }
      applyPeerLayer(localEncryptedChat, ((TLRPC.TL_decryptedMessageLayer)localObject3).layer);
      localEncryptedChat.seq_in = ((TLRPC.TL_decryptedMessageLayer)localObject3).out_seq_no;
      MessagesStorage.getInstance().updateEncryptedChatSeq(localEncryptedChat);
      Object localObject1 = ((TLRPC.TL_decryptedMessageLayer)localObject3).message;
      label829:
      Object localObject2 = new ArrayList();
      paramEncryptedMessage = processDecryptedObject(localEncryptedChat, paramEncryptedMessage.file, paramEncryptedMessage.date, paramEncryptedMessage.random_id, (TLObject)localObject1, bool1);
      if (paramEncryptedMessage != null) {
        ((ArrayList)localObject2).add(paramEncryptedMessage);
      }
      checkSecretHoles(localEncryptedChat, (ArrayList)localObject2);
      return (ArrayList<TLRPC.Message>)localObject2;
      label883:
      ((NativeByteBuffer)localObject3).reuse();
      FileLog.e("tmessages", String.format("fingerprint mismatch %x", new Object[] { Long.valueOf(l) }));
    }
    return null;
  }
  
  protected void performSendEncryptedRequest(final TLRPC.DecryptedMessage paramDecryptedMessage, final TLRPC.Message paramMessage, final TLRPC.EncryptedChat paramEncryptedChat, final TLRPC.InputEncryptedFile paramInputEncryptedFile, final String paramString, final MessageObject paramMessageObject)
  {
    if ((paramDecryptedMessage == null) || (paramEncryptedChat.auth_key == null) || ((paramEncryptedChat instanceof TLRPC.TL_encryptedChatRequested)) || ((paramEncryptedChat instanceof TLRPC.TL_encryptedChatWaiting))) {
      return;
    }
    SendMessagesHelper.getInstance().putToSendingMessages(paramMessage);
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          Object localObject3;
          try
          {
            if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) < 17) {
              break label819;
            }
            localObject3 = new TLRPC.TL_decryptedMessageLayer();
            ((TLRPC.TL_decryptedMessageLayer)localObject3).layer = Math.min(Math.max(17, AndroidUtilities.getMyLayerVersion(paramEncryptedChat.layer)), AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer));
            ((TLRPC.TL_decryptedMessageLayer)localObject3).message = paramDecryptedMessage;
            ((TLRPC.TL_decryptedMessageLayer)localObject3).random_bytes = new byte[15];
            Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageLayer)localObject3).random_bytes);
            Object localObject1 = localObject3;
            if ((paramEncryptedChat.seq_in == 0) && (paramEncryptedChat.seq_out == 0))
            {
              if (paramEncryptedChat.admin_id == UserConfig.getClientUserId()) {
                paramEncryptedChat.seq_out = 1;
              }
            }
            else
            {
              if ((paramMessage.seq_in != 0) || (paramMessage.seq_out != 0)) {
                break label792;
              }
              ((TLRPC.TL_decryptedMessageLayer)localObject3).in_seq_no = paramEncryptedChat.seq_in;
              ((TLRPC.TL_decryptedMessageLayer)localObject3).out_seq_no = paramEncryptedChat.seq_out;
              Object localObject4 = paramEncryptedChat;
              ((TLRPC.EncryptedChat)localObject4).seq_out += 2;
              if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 20)
              {
                if (paramEncryptedChat.key_create_date == 0) {
                  paramEncryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                }
                localObject4 = paramEncryptedChat;
                ((TLRPC.EncryptedChat)localObject4).key_use_count_out = ((short)(((TLRPC.EncryptedChat)localObject4).key_use_count_out + 1));
                if (((paramEncryptedChat.key_use_count_out >= 100) || (paramEncryptedChat.key_create_date < ConnectionsManager.getInstance().getCurrentTime() - 604800)) && (paramEncryptedChat.exchange_id == 0L) && (paramEncryptedChat.future_key_fingerprint == 0L)) {
                  SecretChatHelper.this.requestNewSecretChatKey(paramEncryptedChat);
                }
              }
              MessagesStorage.getInstance().updateEncryptedChatSeq(paramEncryptedChat);
              if (paramMessage != null)
              {
                paramMessage.seq_in = ((TLRPC.TL_decryptedMessageLayer)localObject3).in_seq_no;
                paramMessage.seq_out = ((TLRPC.TL_decryptedMessageLayer)localObject3).out_seq_no;
                MessagesStorage.getInstance().setMessageSeq(paramMessage.id, paramMessage.seq_in, paramMessage.seq_out);
              }
              FileLog.e("tmessages", paramDecryptedMessage + " send message with in_seq = " + ((TLRPC.TL_decryptedMessageLayer)localObject3).in_seq_no + " out_seq = " + ((TLRPC.TL_decryptedMessageLayer)localObject3).out_seq_no);
              i = ((TLObject)localObject1).getObjectSize();
              localObject3 = new NativeByteBuffer(i + 4);
              ((NativeByteBuffer)localObject3).writeInt32(i);
              ((TLObject)localObject1).serializeToStream((AbstractSerializedData)localObject3);
              localObject4 = Utilities.computeSHA1(((NativeByteBuffer)localObject3).buffer);
              localObject1 = new byte[16];
              if (localObject4.length != 0) {
                System.arraycopy(localObject4, localObject4.length - 16, localObject1, 0, 16);
              }
              MessageKeyData localMessageKeyData = MessageKeyData.generateMessageKeyData(paramEncryptedChat.auth_key, (byte[])localObject1, false);
              int j = ((NativeByteBuffer)localObject3).length();
              if (j % 16 == 0) {
                break label969;
              }
              i = 16 - j % 16;
              localObject4 = new NativeByteBuffer(j + i);
              ((NativeByteBuffer)localObject3).position(0);
              ((NativeByteBuffer)localObject4).writeBytes((NativeByteBuffer)localObject3);
              if (i != 0)
              {
                byte[] arrayOfByte = new byte[i];
                Utilities.random.nextBytes(arrayOfByte);
                ((NativeByteBuffer)localObject4).writeBytes(arrayOfByte);
              }
              ((NativeByteBuffer)localObject3).reuse();
              Utilities.aesIgeEncryption(((NativeByteBuffer)localObject4).buffer, localMessageKeyData.aesKey, localMessageKeyData.aesIv, true, false, 0, ((NativeByteBuffer)localObject4).limit());
              localObject3 = new NativeByteBuffer(localObject1.length + 8 + ((NativeByteBuffer)localObject4).length());
              ((NativeByteBuffer)localObject4).position(0);
              ((NativeByteBuffer)localObject3).writeInt64(paramEncryptedChat.key_fingerprint);
              ((NativeByteBuffer)localObject3).writeBytes((byte[])localObject1);
              ((NativeByteBuffer)localObject3).writeBytes((NativeByteBuffer)localObject4);
              ((NativeByteBuffer)localObject4).reuse();
              ((NativeByteBuffer)localObject3).position(0);
              if (paramInputEncryptedFile != null) {
                break label894;
              }
              if (!(paramDecryptedMessage instanceof TLRPC.TL_decryptedMessageService)) {
                break label827;
              }
              localObject1 = new TLRPC.TL_messages_sendEncryptedService();
              ((TLRPC.TL_messages_sendEncryptedService)localObject1).data = ((NativeByteBuffer)localObject3);
              ((TLRPC.TL_messages_sendEncryptedService)localObject1).random_id = paramDecryptedMessage.random_id;
              ((TLRPC.TL_messages_sendEncryptedService)localObject1).peer = new TLRPC.TL_inputEncryptedChat();
              ((TLRPC.TL_messages_sendEncryptedService)localObject1).peer.chat_id = paramEncryptedChat.id;
              ((TLRPC.TL_messages_sendEncryptedService)localObject1).peer.access_hash = paramEncryptedChat.access_hash;
              ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
              {
                public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
                {
                  Object localObject2;
                  Object localObject1;
                  if ((paramAnonymous2TL_error == null) && ((SecretChatHelper.4.this.val$req.action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer)))
                  {
                    localObject2 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(SecretChatHelper.4.this.val$chat.id));
                    localObject1 = localObject2;
                    if (localObject2 == null) {
                      localObject1 = SecretChatHelper.4.this.val$chat;
                    }
                    if (((TLRPC.EncryptedChat)localObject1).key_hash == null) {
                      ((TLRPC.EncryptedChat)localObject1).key_hash = AndroidUtilities.calcAuthKeyHash(((TLRPC.EncryptedChat)localObject1).auth_key);
                    }
                    if ((AndroidUtilities.getPeerLayerVersion(((TLRPC.EncryptedChat)localObject1).layer) < 46) || (((TLRPC.EncryptedChat)localObject1).key_hash.length != 16)) {}
                  }
                  try
                  {
                    localObject2 = Utilities.computeSHA256(SecretChatHelper.4.this.val$chat.auth_key, 0, SecretChatHelper.4.this.val$chat.auth_key.length);
                    byte[] arrayOfByte = new byte[36];
                    System.arraycopy(SecretChatHelper.4.this.val$chat.key_hash, 0, arrayOfByte, 0, 16);
                    System.arraycopy(localObject2, 0, arrayOfByte, 16, 20);
                    ((TLRPC.EncryptedChat)localObject1).key_hash = arrayOfByte;
                    MessagesStorage.getInstance().updateEncryptedChat((TLRPC.EncryptedChat)localObject1);
                    SecretChatHelper.this.sendingNotifyLayer.remove(Integer.valueOf(((TLRPC.EncryptedChat)localObject1).id));
                    ((TLRPC.EncryptedChat)localObject1).layer = AndroidUtilities.setMyLayerVersion(((TLRPC.EncryptedChat)localObject1).layer, 46);
                    MessagesStorage.getInstance().updateEncryptedChatLayer((TLRPC.EncryptedChat)localObject1);
                    if (SecretChatHelper.4.this.val$newMsgObj != null)
                    {
                      if (paramAnonymous2TL_error == null)
                      {
                        paramAnonymous2TL_error = SecretChatHelper.4.this.val$newMsgObj.attachPath;
                        paramAnonymous2TLObject = (TLRPC.messages_SentEncryptedMessage)paramAnonymous2TLObject;
                        if (SecretChatHelper.isSecretVisibleMessage(SecretChatHelper.4.this.val$newMsgObj)) {
                          SecretChatHelper.4.this.val$newMsgObj.date = paramAnonymous2TLObject.date;
                        }
                        if ((SecretChatHelper.4.this.val$newMsg != null) && ((paramAnonymous2TLObject.file instanceof TLRPC.TL_encryptedFile))) {
                          SecretChatHelper.this.updateMediaPaths(SecretChatHelper.4.this.val$newMsg, paramAnonymous2TLObject.file, SecretChatHelper.4.this.val$req, SecretChatHelper.4.this.val$originalPath);
                        }
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
                        {
                          public void run()
                          {
                            if (SecretChatHelper.isSecretInvisibleMessage(SecretChatHelper.4.this.val$newMsgObj)) {
                              paramAnonymous2TLObject.date = 0;
                            }
                            MessagesStorage.getInstance().updateMessageStateAndId(SecretChatHelper.4.this.val$newMsgObj.random_id, Integer.valueOf(SecretChatHelper.4.this.val$newMsgObj.id), SecretChatHelper.4.this.val$newMsgObj.id, paramAnonymous2TLObject.date, false, 0);
                            AndroidUtilities.runOnUIThread(new Runnable()
                            {
                              public void run()
                              {
                                SecretChatHelper.4.this.val$newMsgObj.send_state = 0;
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { Integer.valueOf(SecretChatHelper.4.this.val$newMsgObj.id), Integer.valueOf(SecretChatHelper.4.this.val$newMsgObj.id), SecretChatHelper.4.this.val$newMsgObj, Long.valueOf(SecretChatHelper.4.this.val$newMsgObj.dialog_id) });
                                SendMessagesHelper.getInstance().processSentMessage(SecretChatHelper.4.this.val$newMsgObj.id);
                                if ((MessageObject.isVideoMessage(SecretChatHelper.4.this.val$newMsgObj)) || (MessageObject.isNewGifMessage(SecretChatHelper.4.this.val$newMsgObj))) {
                                  SendMessagesHelper.getInstance().stopVideoService(SecretChatHelper.4.1.1.this.val$attachPath);
                                }
                                SendMessagesHelper.getInstance().removeFromSendingMessages(SecretChatHelper.4.this.val$newMsgObj.id);
                              }
                            });
                          }
                        });
                      }
                    }
                    else {
                      return;
                    }
                  }
                  catch (Throwable localThrowable)
                  {
                    for (;;)
                    {
                      FileLog.e("tmessages", localThrowable);
                    }
                    MessagesStorage.getInstance().markMessageAsSendError(SecretChatHelper.4.this.val$newMsgObj);
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        SecretChatHelper.4.this.val$newMsgObj.send_state = 2;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(SecretChatHelper.4.this.val$newMsgObj.id) });
                        SendMessagesHelper.getInstance().processSentMessage(SecretChatHelper.4.this.val$newMsgObj.id);
                        if ((MessageObject.isVideoMessage(SecretChatHelper.4.this.val$newMsgObj)) || (MessageObject.isNewGifMessage(SecretChatHelper.4.this.val$newMsgObj))) {
                          SendMessagesHelper.getInstance().stopVideoService(SecretChatHelper.4.this.val$newMsgObj.attachPath);
                        }
                        SendMessagesHelper.getInstance().removeFromSendingMessages(SecretChatHelper.4.this.val$newMsgObj.id);
                      }
                    });
                  }
                }
              }, 64);
              return;
            }
            paramEncryptedChat.seq_in = 1;
            continue;
            ((TLRPC.TL_decryptedMessageLayer)localObject3).in_seq_no = paramMessage.seq_in;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          label792:
          ((TLRPC.TL_decryptedMessageLayer)localObject3).out_seq_no = paramMessage.seq_out;
          continue;
          label819:
          Object localObject2 = paramDecryptedMessage;
          continue;
          label827:
          localObject2 = new TLRPC.TL_messages_sendEncrypted();
          ((TLRPC.TL_messages_sendEncrypted)localObject2).data = ((NativeByteBuffer)localObject3);
          ((TLRPC.TL_messages_sendEncrypted)localObject2).random_id = paramDecryptedMessage.random_id;
          ((TLRPC.TL_messages_sendEncrypted)localObject2).peer = new TLRPC.TL_inputEncryptedChat();
          ((TLRPC.TL_messages_sendEncrypted)localObject2).peer.chat_id = paramEncryptedChat.id;
          ((TLRPC.TL_messages_sendEncrypted)localObject2).peer.access_hash = paramEncryptedChat.access_hash;
          continue;
          label894:
          localObject2 = new TLRPC.TL_messages_sendEncryptedFile();
          ((TLRPC.TL_messages_sendEncryptedFile)localObject2).data = ((NativeByteBuffer)localObject3);
          ((TLRPC.TL_messages_sendEncryptedFile)localObject2).random_id = paramDecryptedMessage.random_id;
          ((TLRPC.TL_messages_sendEncryptedFile)localObject2).peer = new TLRPC.TL_inputEncryptedChat();
          ((TLRPC.TL_messages_sendEncryptedFile)localObject2).peer.chat_id = paramEncryptedChat.id;
          ((TLRPC.TL_messages_sendEncryptedFile)localObject2).peer.access_hash = paramEncryptedChat.access_hash;
          ((TLRPC.TL_messages_sendEncryptedFile)localObject2).file = paramInputEncryptedFile;
          continue;
          label969:
          int i = 0;
        }
      }
    });
  }
  
  public void processAcceptedSecretChat(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    final Object localObject1 = new BigInteger(1, MessagesStorage.secretPBytes);
    Object localObject2 = new BigInteger(1, paramEncryptedChat.g_a_or_b);
    if (!Utilities.isGoodGaAndGb((BigInteger)localObject2, (BigInteger)localObject1))
    {
      declineSecretChat(paramEncryptedChat.id);
      return;
    }
    localObject2 = ((BigInteger)localObject2).modPow(new BigInteger(1, paramEncryptedChat.a_or_b), (BigInteger)localObject1).toByteArray();
    if (localObject2.length > 256)
    {
      localObject1 = new byte['Ā'];
      System.arraycopy(localObject2, localObject2.length - 256, localObject1, 0, 256);
    }
    for (;;)
    {
      localObject2 = Utilities.computeSHA1((byte[])localObject1);
      byte[] arrayOfByte = new byte[8];
      System.arraycopy(localObject2, localObject2.length - 8, arrayOfByte, 0, 8);
      long l = Utilities.bytesToLong(arrayOfByte);
      if (paramEncryptedChat.key_fingerprint != l) {
        break;
      }
      paramEncryptedChat.auth_key = ((byte[])localObject1);
      paramEncryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
      paramEncryptedChat.seq_in = 0;
      paramEncryptedChat.seq_out = 1;
      MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.getInstance().putEncryptedChat(paramEncryptedChat, false);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { paramEncryptedChat });
          SecretChatHelper.this.sendNotifyLayerMessage(paramEncryptedChat, null);
        }
      });
      return;
      localObject1 = localObject2;
      if (localObject2.length < 256)
      {
        localObject1 = new byte['Ā'];
        System.arraycopy(localObject2, 0, localObject1, 256 - localObject2.length, localObject2.length);
        int i = 0;
        while (i < 256 - localObject2.length)
        {
          localObject2[i] = 0;
          i += 1;
        }
      }
    }
    localObject1 = new TLRPC.TL_encryptedChatDiscarded();
    ((TLRPC.TL_encryptedChatDiscarded)localObject1).id = paramEncryptedChat.id;
    ((TLRPC.TL_encryptedChatDiscarded)localObject1).user_id = paramEncryptedChat.user_id;
    ((TLRPC.TL_encryptedChatDiscarded)localObject1).auth_key = paramEncryptedChat.auth_key;
    ((TLRPC.TL_encryptedChatDiscarded)localObject1).key_create_date = paramEncryptedChat.key_create_date;
    ((TLRPC.TL_encryptedChatDiscarded)localObject1).key_use_count_in = paramEncryptedChat.key_use_count_in;
    ((TLRPC.TL_encryptedChatDiscarded)localObject1).key_use_count_out = paramEncryptedChat.key_use_count_out;
    ((TLRPC.TL_encryptedChatDiscarded)localObject1).seq_in = paramEncryptedChat.seq_in;
    ((TLRPC.TL_encryptedChatDiscarded)localObject1).seq_out = paramEncryptedChat.seq_out;
    MessagesStorage.getInstance().updateEncryptedChat((TLRPC.EncryptedChat)localObject1);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().putEncryptedChat(localObject1, false);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { localObject1 });
      }
    });
    declineSecretChat(paramEncryptedChat.id);
  }
  
  public TLRPC.Message processDecryptedObject(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.EncryptedFile paramEncryptedFile, int paramInt, long paramLong, TLObject paramTLObject, boolean paramBoolean)
  {
    Object localObject1;
    label173:
    label465:
    label486:
    Object localObject2;
    if (paramTLObject != null)
    {
      int j = paramEncryptedChat.admin_id;
      int i = j;
      if (j == UserConfig.getClientUserId()) {
        i = paramEncryptedChat.participant_id;
      }
      if ((AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 20) && (paramEncryptedChat.exchange_id == 0L) && (paramEncryptedChat.future_key_fingerprint == 0L) && (paramEncryptedChat.key_use_count_in >= 120)) {
        requestNewSecretChatKey(paramEncryptedChat);
      }
      if ((paramEncryptedChat.exchange_id == 0L) && (paramEncryptedChat.future_key_fingerprint != 0L) && (!paramBoolean))
      {
        paramEncryptedChat.future_auth_key = new byte['Ā'];
        paramEncryptedChat.future_key_fingerprint = 0L;
        MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
        if (!(paramTLObject instanceof TLRPC.TL_decryptedMessage)) {
          break label3124;
        }
        localObject1 = (TLRPC.TL_decryptedMessage)paramTLObject;
        if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) < 17) {
          break label465;
        }
        paramTLObject = new TLRPC.TL_message_secret();
        paramTLObject.ttl = ((TLRPC.TL_decryptedMessage)localObject1).ttl;
        paramTLObject.entities = ((TLRPC.TL_decryptedMessage)localObject1).entities;
        paramTLObject.message = ((TLRPC.TL_decryptedMessage)localObject1).message;
        paramTLObject.date = paramInt;
        j = UserConfig.getNewMessageId();
        paramTLObject.id = j;
        paramTLObject.local_id = j;
        UserConfig.saveConfig(false);
        paramTLObject.from_id = i;
        paramTLObject.to_id = new TLRPC.TL_peerUser();
        paramTLObject.random_id = paramLong;
        paramTLObject.to_id.user_id = UserConfig.getClientUserId();
        paramTLObject.unread = true;
        paramTLObject.flags = 768;
        if ((((TLRPC.TL_decryptedMessage)localObject1).via_bot_name != null) && (((TLRPC.TL_decryptedMessage)localObject1).via_bot_name.length() > 0))
        {
          paramTLObject.via_bot_name = ((TLRPC.TL_decryptedMessage)localObject1).via_bot_name;
          paramTLObject.flags |= 0x800;
        }
        paramTLObject.dialog_id = (paramEncryptedChat.id << 32);
        if (((TLRPC.TL_decryptedMessage)localObject1).reply_to_random_id != 0L)
        {
          paramTLObject.reply_to_random_id = ((TLRPC.TL_decryptedMessage)localObject1).reply_to_random_id;
          paramTLObject.flags |= 0x8;
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media != null) && (!(((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaEmpty))) {
          break label486;
        }
        paramTLObject.media = new TLRPC.TL_messageMediaEmpty();
      }
      label1315:
      label1696:
      label1836:
      label1946:
      label2001:
      label2281:
      label2303:
      label2311:
      do
      {
        return paramTLObject;
        if ((paramEncryptedChat.exchange_id == 0L) || (!paramBoolean)) {
          break;
        }
        paramEncryptedChat.key_fingerprint = paramEncryptedChat.future_key_fingerprint;
        paramEncryptedChat.auth_key = paramEncryptedChat.future_auth_key;
        paramEncryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
        paramEncryptedChat.future_auth_key = new byte['Ā'];
        paramEncryptedChat.future_key_fingerprint = 0L;
        paramEncryptedChat.key_use_count_in = 0;
        paramEncryptedChat.key_use_count_out = 0;
        paramEncryptedChat.exchange_id = 0L;
        MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
        break;
        paramTLObject = new TLRPC.TL_message();
        paramTLObject.ttl = paramEncryptedChat.ttl;
        break label173;
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaWebPage))
        {
          paramTLObject.media = new TLRPC.TL_messageMediaWebPage();
          paramTLObject.media.webpage = new TLRPC.TL_webPageUrlPending();
          paramTLObject.media.webpage.url = ((TLRPC.TL_decryptedMessage)localObject1).media.url;
          return paramTLObject;
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaContact))
        {
          paramTLObject.media = new TLRPC.TL_messageMediaContact();
          paramTLObject.media.last_name = ((TLRPC.TL_decryptedMessage)localObject1).media.last_name;
          paramTLObject.media.first_name = ((TLRPC.TL_decryptedMessage)localObject1).media.first_name;
          paramTLObject.media.phone_number = ((TLRPC.TL_decryptedMessage)localObject1).media.phone_number;
          paramTLObject.media.user_id = ((TLRPC.TL_decryptedMessage)localObject1).media.user_id;
          return paramTLObject;
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaGeoPoint))
        {
          paramTLObject.media = new TLRPC.TL_messageMediaGeo();
          paramTLObject.media.geo = new TLRPC.TL_geoPoint();
          paramTLObject.media.geo.lat = ((TLRPC.TL_decryptedMessage)localObject1).media.lat;
          paramTLObject.media.geo._long = ((TLRPC.TL_decryptedMessage)localObject1).media._long;
          return paramTLObject;
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaPhoto))
        {
          if ((((TLRPC.TL_decryptedMessage)localObject1).media.key == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.key.length != 32) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv.length != 32)) {
            return null;
          }
          paramTLObject.media = new TLRPC.TL_messageMediaPhoto();
          localObject2 = paramTLObject.media;
          if (((TLRPC.TL_decryptedMessage)localObject1).media.caption != null) {}
          for (paramEncryptedChat = ((TLRPC.TL_decryptedMessage)localObject1).media.caption;; paramEncryptedChat = "")
          {
            ((TLRPC.MessageMedia)localObject2).caption = paramEncryptedChat;
            paramTLObject.media.photo = new TLRPC.TL_photo();
            paramTLObject.media.photo.date = paramTLObject.date;
            paramEncryptedChat = ((TLRPC.TL_decryptedMessageMediaPhoto)((TLRPC.TL_decryptedMessage)localObject1).media).thumb;
            if ((paramEncryptedChat != null) && (paramEncryptedChat.length != 0) && (paramEncryptedChat.length <= 6000) && (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_w <= 100) && (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_h <= 100))
            {
              localObject2 = new TLRPC.TL_photoCachedSize();
              ((TLRPC.TL_photoCachedSize)localObject2).w = ((TLRPC.TL_decryptedMessage)localObject1).media.thumb_w;
              ((TLRPC.TL_photoCachedSize)localObject2).h = ((TLRPC.TL_decryptedMessage)localObject1).media.thumb_h;
              ((TLRPC.TL_photoCachedSize)localObject2).bytes = paramEncryptedChat;
              ((TLRPC.TL_photoCachedSize)localObject2).type = "s";
              ((TLRPC.TL_photoCachedSize)localObject2).location = new TLRPC.TL_fileLocationUnavailable();
              paramTLObject.media.photo.sizes.add(localObject2);
            }
            paramEncryptedChat = new TLRPC.TL_photoSize();
            paramEncryptedChat.w = ((TLRPC.TL_decryptedMessage)localObject1).media.w;
            paramEncryptedChat.h = ((TLRPC.TL_decryptedMessage)localObject1).media.h;
            paramEncryptedChat.type = "x";
            paramEncryptedChat.size = paramEncryptedFile.size;
            paramEncryptedChat.location = new TLRPC.TL_fileEncryptedLocation();
            paramEncryptedChat.location.key = ((TLRPC.TL_decryptedMessage)localObject1).media.key;
            paramEncryptedChat.location.iv = ((TLRPC.TL_decryptedMessage)localObject1).media.iv;
            paramEncryptedChat.location.dc_id = paramEncryptedFile.dc_id;
            paramEncryptedChat.location.volume_id = paramEncryptedFile.id;
            paramEncryptedChat.location.secret = paramEncryptedFile.access_hash;
            paramEncryptedChat.location.local_id = paramEncryptedFile.key_fingerprint;
            paramTLObject.media.photo.sizes.add(paramEncryptedChat);
            return paramTLObject;
          }
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaVideo))
        {
          if ((((TLRPC.TL_decryptedMessage)localObject1).media.key == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.key.length != 32) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv.length != 32)) {
            return null;
          }
          paramTLObject.media = new TLRPC.TL_messageMediaDocument();
          paramTLObject.media.document = new TLRPC.TL_documentEncrypted();
          paramTLObject.media.document.key = ((TLRPC.TL_decryptedMessage)localObject1).media.key;
          paramTLObject.media.document.iv = ((TLRPC.TL_decryptedMessage)localObject1).media.iv;
          paramTLObject.media.document.dc_id = paramEncryptedFile.dc_id;
          localObject2 = paramTLObject.media;
          if (((TLRPC.TL_decryptedMessage)localObject1).media.caption != null)
          {
            paramEncryptedChat = ((TLRPC.TL_decryptedMessage)localObject1).media.caption;
            ((TLRPC.MessageMedia)localObject2).caption = paramEncryptedChat;
            paramTLObject.media.document.date = paramInt;
            paramTLObject.media.document.size = paramEncryptedFile.size;
            paramTLObject.media.document.id = paramEncryptedFile.id;
            paramTLObject.media.document.access_hash = paramEncryptedFile.access_hash;
            paramTLObject.media.document.mime_type = ((TLRPC.TL_decryptedMessage)localObject1).media.mime_type;
            if (paramTLObject.media.document.mime_type == null) {
              paramTLObject.media.document.mime_type = "video/mp4";
            }
            paramEncryptedChat = ((TLRPC.TL_decryptedMessageMediaVideo)((TLRPC.TL_decryptedMessage)localObject1).media).thumb;
            if ((paramEncryptedChat == null) || (paramEncryptedChat.length == 0) || (paramEncryptedChat.length > 6000) || (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_w > 100) || (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_h > 100)) {
              break label1696;
            }
            paramTLObject.media.document.thumb = new TLRPC.TL_photoCachedSize();
            paramTLObject.media.document.thumb.bytes = paramEncryptedChat;
            paramTLObject.media.document.thumb.w = ((TLRPC.TL_decryptedMessage)localObject1).media.thumb_w;
            paramTLObject.media.document.thumb.h = ((TLRPC.TL_decryptedMessage)localObject1).media.thumb_h;
            paramTLObject.media.document.thumb.type = "s";
            paramTLObject.media.document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
          }
          for (;;)
          {
            paramEncryptedChat = new TLRPC.TL_documentAttributeVideo();
            paramEncryptedChat.w = ((TLRPC.TL_decryptedMessage)localObject1).media.w;
            paramEncryptedChat.h = ((TLRPC.TL_decryptedMessage)localObject1).media.h;
            paramEncryptedChat.duration = ((TLRPC.TL_decryptedMessage)localObject1).media.duration;
            paramTLObject.media.document.attributes.add(paramEncryptedChat);
            if (paramTLObject.ttl == 0) {
              break;
            }
            paramTLObject.ttl = Math.max(((TLRPC.TL_decryptedMessage)localObject1).media.duration + 2, paramTLObject.ttl);
            return paramTLObject;
            paramEncryptedChat = "";
            break label1315;
            paramTLObject.media.document.thumb = new TLRPC.TL_photoSizeEmpty();
            paramTLObject.media.document.thumb.type = "s";
          }
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaDocument))
        {
          if ((((TLRPC.TL_decryptedMessage)localObject1).media.key == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.key.length != 32) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv.length != 32)) {
            return null;
          }
          paramTLObject.media = new TLRPC.TL_messageMediaDocument();
          localObject2 = paramTLObject.media;
          if (((TLRPC.TL_decryptedMessage)localObject1).media.caption != null)
          {
            paramEncryptedChat = ((TLRPC.TL_decryptedMessage)localObject1).media.caption;
            ((TLRPC.MessageMedia)localObject2).caption = paramEncryptedChat;
            paramTLObject.media.document = new TLRPC.TL_documentEncrypted();
            paramTLObject.media.document.id = paramEncryptedFile.id;
            paramTLObject.media.document.access_hash = paramEncryptedFile.access_hash;
            paramTLObject.media.document.date = paramInt;
            if (!(((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaDocument_layer8)) {
              break label2281;
            }
            paramEncryptedChat = new TLRPC.TL_documentAttributeFilename();
            paramEncryptedChat.file_name = ((TLRPC.TL_decryptedMessage)localObject1).media.file_name;
            paramTLObject.media.document.attributes.add(paramEncryptedChat);
            paramTLObject.media.document.mime_type = ((TLRPC.TL_decryptedMessage)localObject1).media.mime_type;
            paramEncryptedChat = paramTLObject.media.document;
            if (((TLRPC.TL_decryptedMessage)localObject1).media.size == 0) {
              break label2303;
            }
            paramInt = Math.min(((TLRPC.TL_decryptedMessage)localObject1).media.size, paramEncryptedFile.size);
            paramEncryptedChat.size = paramInt;
            paramTLObject.media.document.key = ((TLRPC.TL_decryptedMessage)localObject1).media.key;
            paramTLObject.media.document.iv = ((TLRPC.TL_decryptedMessage)localObject1).media.iv;
            if (paramTLObject.media.document.mime_type == null) {
              paramTLObject.media.document.mime_type = "";
            }
            paramEncryptedChat = ((TLRPC.TL_decryptedMessageMediaDocument)((TLRPC.TL_decryptedMessage)localObject1).media).thumb;
            if ((paramEncryptedChat == null) || (paramEncryptedChat.length == 0) || (paramEncryptedChat.length > 6000) || (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_w > 100) || (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_h > 100)) {
              break label2311;
            }
            paramTLObject.media.document.thumb = new TLRPC.TL_photoCachedSize();
            paramTLObject.media.document.thumb.bytes = paramEncryptedChat;
            paramTLObject.media.document.thumb.w = ((TLRPC.TL_decryptedMessage)localObject1).media.thumb_w;
            paramTLObject.media.document.thumb.h = ((TLRPC.TL_decryptedMessage)localObject1).media.thumb_h;
            paramTLObject.media.document.thumb.type = "s";
            paramTLObject.media.document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
          }
          for (;;)
          {
            paramTLObject.media.document.dc_id = paramEncryptedFile.dc_id;
            if (!MessageObject.isVoiceMessage(paramTLObject)) {
              break;
            }
            paramTLObject.media_unread = true;
            return paramTLObject;
            paramEncryptedChat = "";
            break label1836;
            paramTLObject.media.document.attributes = ((TLRPC.TL_decryptedMessage)localObject1).media.attributes;
            break label1946;
            paramInt = paramEncryptedFile.size;
            break label2001;
            paramTLObject.media.document.thumb = new TLRPC.TL_photoSizeEmpty();
            paramTLObject.media.document.thumb.type = "s";
          }
        }
        if (!(((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaExternalDocument)) {
          break label2584;
        }
        paramTLObject.media = new TLRPC.TL_messageMediaDocument();
        paramTLObject.media.caption = "";
        paramTLObject.media.document = new TLRPC.TL_document();
        paramTLObject.media.document.id = ((TLRPC.TL_decryptedMessage)localObject1).media.id;
        paramTLObject.media.document.access_hash = ((TLRPC.TL_decryptedMessage)localObject1).media.access_hash;
        paramTLObject.media.document.date = ((TLRPC.TL_decryptedMessage)localObject1).media.date;
        paramTLObject.media.document.attributes = ((TLRPC.TL_decryptedMessage)localObject1).media.attributes;
        paramTLObject.media.document.mime_type = ((TLRPC.TL_decryptedMessage)localObject1).media.mime_type;
        paramTLObject.media.document.dc_id = ((TLRPC.TL_decryptedMessage)localObject1).media.dc_id;
        paramTLObject.media.document.size = ((TLRPC.TL_decryptedMessage)localObject1).media.size;
        paramTLObject.media.document.thumb = ((TLRPC.TL_decryptedMessageMediaExternalDocument)((TLRPC.TL_decryptedMessage)localObject1).media).thumb;
      } while (paramTLObject.media.document.mime_type != null);
      paramTLObject.media.document.mime_type = "";
      return paramTLObject;
      label2584:
      if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaAudio))
      {
        if ((((TLRPC.TL_decryptedMessage)localObject1).media.key == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.key.length != 32) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv.length != 32)) {
          return null;
        }
        paramTLObject.media = new TLRPC.TL_messageMediaDocument();
        paramTLObject.media.document = new TLRPC.TL_documentEncrypted();
        paramTLObject.media.document.key = ((TLRPC.TL_decryptedMessage)localObject1).media.key;
        paramTLObject.media.document.iv = ((TLRPC.TL_decryptedMessage)localObject1).media.iv;
        paramTLObject.media.document.id = paramEncryptedFile.id;
        paramTLObject.media.document.access_hash = paramEncryptedFile.access_hash;
        paramTLObject.media.document.date = paramInt;
        paramTLObject.media.document.size = paramEncryptedFile.size;
        paramTLObject.media.document.dc_id = paramEncryptedFile.dc_id;
        paramTLObject.media.document.mime_type = ((TLRPC.TL_decryptedMessage)localObject1).media.mime_type;
        paramTLObject.media.document.thumb = new TLRPC.TL_photoSizeEmpty();
        paramTLObject.media.document.thumb.type = "s";
        paramEncryptedFile = paramTLObject.media;
        if (((TLRPC.TL_decryptedMessage)localObject1).media.caption != null) {}
        for (paramEncryptedChat = ((TLRPC.TL_decryptedMessage)localObject1).media.caption;; paramEncryptedChat = "")
        {
          paramEncryptedFile.caption = paramEncryptedChat;
          if (paramTLObject.media.document.mime_type == null) {
            paramTLObject.media.document.mime_type = "audio/ogg";
          }
          paramEncryptedChat = new TLRPC.TL_documentAttributeAudio();
          paramEncryptedChat.duration = ((TLRPC.TL_decryptedMessage)localObject1).media.duration;
          paramEncryptedChat.voice = true;
          paramTLObject.media.document.attributes.add(paramEncryptedChat);
          if (paramTLObject.ttl == 0) {
            break;
          }
          paramTLObject.ttl = Math.max(((TLRPC.TL_decryptedMessage)localObject1).media.duration + 1, paramTLObject.ttl);
          return paramTLObject;
        }
      }
      if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaVenue))
      {
        paramTLObject.media = new TLRPC.TL_messageMediaVenue();
        paramTLObject.media.geo = new TLRPC.TL_geoPoint();
        paramTLObject.media.geo.lat = ((TLRPC.TL_decryptedMessage)localObject1).media.lat;
        paramTLObject.media.geo._long = ((TLRPC.TL_decryptedMessage)localObject1).media._long;
        paramTLObject.media.title = ((TLRPC.TL_decryptedMessage)localObject1).media.title;
        paramTLObject.media.address = ((TLRPC.TL_decryptedMessage)localObject1).media.address;
        paramTLObject.media.provider = ((TLRPC.TL_decryptedMessage)localObject1).media.provider;
        paramTLObject.media.venue_id = ((TLRPC.TL_decryptedMessage)localObject1).media.venue_id;
        return paramTLObject;
      }
      return null;
      label3124:
      if ((paramTLObject instanceof TLRPC.TL_decryptedMessageService))
      {
        localObject2 = (TLRPC.TL_decryptedMessageService)paramTLObject;
        if (((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) || ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)))
        {
          paramEncryptedFile = new TLRPC.TL_messageService();
          if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))
          {
            paramEncryptedFile.action = new TLRPC.TL_messageEncryptedAction();
            if ((((TLRPC.TL_decryptedMessageService)localObject2).action.ttl_seconds < 0) || (((TLRPC.TL_decryptedMessageService)localObject2).action.ttl_seconds > 31536000)) {
              ((TLRPC.TL_decryptedMessageService)localObject2).action.ttl_seconds = 31536000;
            }
            paramEncryptedChat.ttl = ((TLRPC.TL_decryptedMessageService)localObject2).action.ttl_seconds;
            paramEncryptedFile.action.encryptedAction = ((TLRPC.TL_decryptedMessageService)localObject2).action;
            MessagesStorage.getInstance().updateEncryptedChatTTL(paramEncryptedChat);
          }
          for (;;)
          {
            j = UserConfig.getNewMessageId();
            paramEncryptedFile.id = j;
            paramEncryptedFile.local_id = j;
            UserConfig.saveConfig(false);
            paramEncryptedFile.unread = true;
            paramEncryptedFile.flags = 256;
            paramEncryptedFile.date = paramInt;
            paramEncryptedFile.from_id = i;
            paramEncryptedFile.to_id = new TLRPC.TL_peerUser();
            paramEncryptedFile.to_id.user_id = UserConfig.getClientUserId();
            paramEncryptedFile.dialog_id = (paramEncryptedChat.id << 32);
            return paramEncryptedFile;
            if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages))
            {
              paramEncryptedFile.action = new TLRPC.TL_messageEncryptedAction();
              paramEncryptedFile.action.encryptedAction = ((TLRPC.TL_decryptedMessageService)localObject2).action;
            }
          }
        }
        if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionFlushHistory))
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.val$did));
              if (localTL_dialog != null)
              {
                localTL_dialog.unread_count = 0;
                MessagesController.getInstance().dialogMessage.remove(Long.valueOf(localTL_dialog.id));
              }
              MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
              {
                public void run()
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationsController.getInstance().processReadMessages(null, SecretChatHelper.6.this.val$did, 0, Integer.MAX_VALUE, false);
                      HashMap localHashMap = new HashMap();
                      localHashMap.put(Long.valueOf(SecretChatHelper.6.this.val$did), Integer.valueOf(0));
                      NotificationsController.getInstance().processDialogsUpdateRead(localHashMap);
                    }
                  });
                }
              });
              MessagesStorage.getInstance().deleteDialog(this.val$did, 1);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, new Object[] { Long.valueOf(this.val$did), Boolean.valueOf(false) });
            }
          });
          return null;
        }
        if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionDeleteMessages))
        {
          if (!((TLRPC.TL_decryptedMessageService)localObject2).action.random_ids.isEmpty()) {
            this.pendingEncMessagesToDelete.addAll(((TLRPC.TL_decryptedMessageService)localObject2).action.random_ids);
          }
          return null;
        }
        if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionReadMessages)) {
          if (!((TLRPC.TL_decryptedMessageService)localObject2).action.random_ids.isEmpty())
          {
            paramInt = ConnectionsManager.getInstance().getCurrentTime();
            MessagesStorage.getInstance().createTaskForSecretChat(paramEncryptedChat.id, paramInt, paramInt, 1, ((TLRPC.TL_decryptedMessageService)localObject2).action.random_ids);
          }
        }
      }
    }
    for (;;)
    {
      return null;
      if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer))
      {
        applyPeerLayer(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject2).action.layer);
      }
      else
      {
        if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionRequestKey))
        {
          if (paramEncryptedChat.exchange_id != 0L)
          {
            if (paramEncryptedChat.exchange_id > ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id)
            {
              FileLog.e("tmessages", "we already have request key with higher exchange_id");
              return null;
            }
            sendAbortKeyMessage(paramEncryptedChat, null, paramEncryptedChat.exchange_id);
          }
          localObject1 = new byte['Ā'];
          Utilities.random.nextBytes((byte[])localObject1);
          Object localObject3 = new BigInteger(1, MessagesStorage.secretPBytes);
          paramEncryptedFile = BigInteger.valueOf(MessagesStorage.secretG).modPow(new BigInteger(1, (byte[])localObject1), (BigInteger)localObject3);
          BigInteger localBigInteger = new BigInteger(1, ((TLRPC.TL_decryptedMessageService)localObject2).action.g_a);
          if (!Utilities.isGoodGaAndGb(localBigInteger, (BigInteger)localObject3))
          {
            sendAbortKeyMessage(paramEncryptedChat, null, ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id);
            return null;
          }
          paramEncryptedFile = paramEncryptedFile.toByteArray();
          paramTLObject = paramEncryptedFile;
          if (paramEncryptedFile.length > 256)
          {
            paramTLObject = new byte['Ā'];
            System.arraycopy(paramEncryptedFile, 1, paramTLObject, 0, 256);
          }
          localObject1 = localBigInteger.modPow(new BigInteger(1, (byte[])localObject1), (BigInteger)localObject3).toByteArray();
          if (localObject1.length > 256)
          {
            paramEncryptedFile = new byte['Ā'];
            System.arraycopy(localObject1, localObject1.length - 256, paramEncryptedFile, 0, 256);
          }
          for (;;)
          {
            localObject1 = Utilities.computeSHA1(paramEncryptedFile);
            localObject3 = new byte[8];
            System.arraycopy(localObject1, localObject1.length - 8, localObject3, 0, 8);
            paramEncryptedChat.exchange_id = ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id;
            paramEncryptedChat.future_auth_key = paramEncryptedFile;
            paramEncryptedChat.future_key_fingerprint = Utilities.bytesToLong((byte[])localObject3);
            paramEncryptedChat.g_a_or_b = paramTLObject;
            MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
            sendAcceptKeyMessage(paramEncryptedChat, null);
            break;
            paramEncryptedFile = (TLRPC.EncryptedFile)localObject1;
            if (localObject1.length < 256)
            {
              paramEncryptedFile = new byte['Ā'];
              System.arraycopy(localObject1, 0, paramEncryptedFile, 256 - localObject1.length, localObject1.length);
              paramInt = 0;
              while (paramInt < 256 - localObject1.length)
              {
                localObject1[paramInt] = 0;
                paramInt += 1;
              }
            }
          }
        }
        if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionAcceptKey))
        {
          if (paramEncryptedChat.exchange_id == ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id)
          {
            paramEncryptedFile = new BigInteger(1, MessagesStorage.secretPBytes);
            paramTLObject = new BigInteger(1, ((TLRPC.TL_decryptedMessageService)localObject2).action.g_b);
            if (!Utilities.isGoodGaAndGb(paramTLObject, paramEncryptedFile))
            {
              paramEncryptedChat.future_auth_key = new byte['Ā'];
              paramEncryptedChat.future_key_fingerprint = 0L;
              paramEncryptedChat.exchange_id = 0L;
              MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
              sendAbortKeyMessage(paramEncryptedChat, null, ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id);
              return null;
            }
            paramTLObject = paramTLObject.modPow(new BigInteger(1, paramEncryptedChat.a_or_b), paramEncryptedFile).toByteArray();
            if (paramTLObject.length > 256)
            {
              paramEncryptedFile = new byte['Ā'];
              System.arraycopy(paramTLObject, paramTLObject.length - 256, paramEncryptedFile, 0, 256);
            }
            for (;;)
            {
              paramTLObject = Utilities.computeSHA1(paramEncryptedFile);
              localObject1 = new byte[8];
              System.arraycopy(paramTLObject, paramTLObject.length - 8, localObject1, 0, 8);
              paramLong = Utilities.bytesToLong((byte[])localObject1);
              if (((TLRPC.TL_decryptedMessageService)localObject2).action.key_fingerprint != paramLong) {
                break label4211;
              }
              paramEncryptedChat.future_auth_key = paramEncryptedFile;
              paramEncryptedChat.future_key_fingerprint = paramLong;
              MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
              sendCommitKeyMessage(paramEncryptedChat, null);
              break;
              paramEncryptedFile = paramTLObject;
              if (paramTLObject.length < 256)
              {
                paramEncryptedFile = new byte['Ā'];
                System.arraycopy(paramTLObject, 0, paramEncryptedFile, 256 - paramTLObject.length, paramTLObject.length);
                paramInt = 0;
                while (paramInt < 256 - paramTLObject.length)
                {
                  paramTLObject[paramInt] = 0;
                  paramInt += 1;
                }
              }
            }
            label4211:
            paramEncryptedChat.future_auth_key = new byte['Ā'];
            paramEncryptedChat.future_key_fingerprint = 0L;
            paramEncryptedChat.exchange_id = 0L;
            MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
            sendAbortKeyMessage(paramEncryptedChat, null, ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id);
          }
          else
          {
            paramEncryptedChat.future_auth_key = new byte['Ā'];
            paramEncryptedChat.future_key_fingerprint = 0L;
            paramEncryptedChat.exchange_id = 0L;
            MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
            sendAbortKeyMessage(paramEncryptedChat, null, ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id);
          }
        }
        else if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionCommitKey))
        {
          if ((paramEncryptedChat.exchange_id == ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id) && (paramEncryptedChat.future_key_fingerprint == ((TLRPC.TL_decryptedMessageService)localObject2).action.key_fingerprint))
          {
            paramLong = paramEncryptedChat.key_fingerprint;
            paramEncryptedFile = paramEncryptedChat.auth_key;
            paramEncryptedChat.key_fingerprint = paramEncryptedChat.future_key_fingerprint;
            paramEncryptedChat.auth_key = paramEncryptedChat.future_auth_key;
            paramEncryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
            paramEncryptedChat.future_auth_key = paramEncryptedFile;
            paramEncryptedChat.future_key_fingerprint = paramLong;
            paramEncryptedChat.key_use_count_in = 0;
            paramEncryptedChat.key_use_count_out = 0;
            paramEncryptedChat.exchange_id = 0L;
            MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
            sendNoopMessage(paramEncryptedChat, null);
          }
          else
          {
            paramEncryptedChat.future_auth_key = new byte['Ā'];
            paramEncryptedChat.future_key_fingerprint = 0L;
            paramEncryptedChat.exchange_id = 0L;
            MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
            sendAbortKeyMessage(paramEncryptedChat, null, ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id);
          }
        }
        else if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionAbortKey))
        {
          if (paramEncryptedChat.exchange_id == ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id)
          {
            paramEncryptedChat.future_auth_key = new byte['Ā'];
            paramEncryptedChat.future_key_fingerprint = 0L;
            paramEncryptedChat.exchange_id = 0L;
            MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
          }
        }
        else if (!(((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionNoop)) {
          if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionResend))
          {
            resendMessages(((TLRPC.TL_decryptedMessageService)localObject2).action.start_seq_no, ((TLRPC.TL_decryptedMessageService)localObject2).action.end_seq_no, paramEncryptedChat);
          }
          else
          {
            return null;
            FileLog.e("tmessages", "unknown message " + paramTLObject);
            continue;
            FileLog.e("tmessages", "unknown TLObject");
          }
        }
      }
    }
  }
  
  protected void processPendingEncMessages()
  {
    if (!this.pendingEncMessagesToDelete.isEmpty())
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          int i = 0;
          while (i < this.val$pendingEncMessagesToDeleteCopy.size())
          {
            MessageObject localMessageObject = (MessageObject)MessagesController.getInstance().dialogMessagesByRandomIds.get(this.val$pendingEncMessagesToDeleteCopy.get(i));
            if (localMessageObject != null) {
              localMessageObject.deleted = true;
            }
            i += 1;
          }
        }
      });
      ArrayList localArrayList = new ArrayList(this.pendingEncMessagesToDelete);
      MessagesStorage.getInstance().markMessagesAsDeletedByRandoms(localArrayList);
      this.pendingEncMessagesToDelete.clear();
    }
  }
  
  protected void processUpdateEncryption(TLRPC.TL_updateEncryption paramTL_updateEncryption, final ConcurrentHashMap<Integer, TLRPC.User> paramConcurrentHashMap)
  {
    final TLRPC.EncryptedChat localEncryptedChat = paramTL_updateEncryption.chat;
    long l = localEncryptedChat.id;
    final Object localObject = MessagesController.getInstance().getEncryptedChatDB(localEncryptedChat.id);
    if (((localEncryptedChat instanceof TLRPC.TL_encryptedChatRequested)) && (localObject == null))
    {
      int j = localEncryptedChat.participant_id;
      int i = j;
      if (j == UserConfig.getClientUserId()) {
        i = localEncryptedChat.admin_id;
      }
      TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(i));
      localObject = localUser;
      if (localUser == null) {
        localObject = (TLRPC.User)paramConcurrentHashMap.get(Integer.valueOf(i));
      }
      localEncryptedChat.user_id = i;
      paramConcurrentHashMap = new TLRPC.TL_dialog();
      paramConcurrentHashMap.id = (l << 32);
      paramConcurrentHashMap.unread_count = 0;
      paramConcurrentHashMap.top_message = 0;
      paramConcurrentHashMap.last_message_date = paramTL_updateEncryption.date;
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.getInstance().dialogs_dict.put(Long.valueOf(paramConcurrentHashMap.id), paramConcurrentHashMap);
          MessagesController.getInstance().dialogs.add(paramConcurrentHashMap);
          MessagesController.getInstance().putEncryptedChat(localEncryptedChat, false);
          MessagesController.getInstance().sortDialogs(null);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
      });
      MessagesStorage.getInstance().putEncryptedChat(localEncryptedChat, (TLRPC.User)localObject, paramConcurrentHashMap);
      getInstance().acceptSecretChat(localEncryptedChat);
    }
    do
    {
      return;
      if (!(localEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
        break;
      }
      if ((localObject != null) && ((localObject instanceof TLRPC.TL_encryptedChatWaiting)) && ((((TLRPC.EncryptedChat)localObject).auth_key == null) || (((TLRPC.EncryptedChat)localObject).auth_key.length == 1)))
      {
        localEncryptedChat.a_or_b = ((TLRPC.EncryptedChat)localObject).a_or_b;
        localEncryptedChat.user_id = ((TLRPC.EncryptedChat)localObject).user_id;
        processAcceptedSecretChat(localEncryptedChat);
        return;
      }
    } while ((localObject != null) || (!this.startingSecretChat));
    this.delayedEncryptedChatUpdates.add(paramTL_updateEncryption);
    return;
    if (localObject != null)
    {
      localEncryptedChat.user_id = ((TLRPC.EncryptedChat)localObject).user_id;
      localEncryptedChat.auth_key = ((TLRPC.EncryptedChat)localObject).auth_key;
      localEncryptedChat.key_create_date = ((TLRPC.EncryptedChat)localObject).key_create_date;
      localEncryptedChat.key_use_count_in = ((TLRPC.EncryptedChat)localObject).key_use_count_in;
      localEncryptedChat.key_use_count_out = ((TLRPC.EncryptedChat)localObject).key_use_count_out;
      localEncryptedChat.ttl = ((TLRPC.EncryptedChat)localObject).ttl;
      localEncryptedChat.seq_in = ((TLRPC.EncryptedChat)localObject).seq_in;
      localEncryptedChat.seq_out = ((TLRPC.EncryptedChat)localObject).seq_out;
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (localObject != null) {
          MessagesController.getInstance().putEncryptedChat(localEncryptedChat, false);
        }
        MessagesStorage.getInstance().updateEncryptedChat(localEncryptedChat);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { localEncryptedChat });
      }
    });
  }
  
  public void requestNewSecretChatKey(TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) < 20) {
      return;
    }
    byte[] arrayOfByte3 = new byte['Ā'];
    Utilities.random.nextBytes(arrayOfByte3);
    byte[] arrayOfByte2 = BigInteger.valueOf(MessagesStorage.secretG).modPow(new BigInteger(1, arrayOfByte3), new BigInteger(1, MessagesStorage.secretPBytes)).toByteArray();
    byte[] arrayOfByte1 = arrayOfByte2;
    if (arrayOfByte2.length > 256)
    {
      arrayOfByte1 = new byte['Ā'];
      System.arraycopy(arrayOfByte2, 1, arrayOfByte1, 0, 256);
    }
    paramEncryptedChat.exchange_id = SendMessagesHelper.getInstance().getNextRandomId();
    paramEncryptedChat.a_or_b = arrayOfByte3;
    paramEncryptedChat.g_a = arrayOfByte1;
    MessagesStorage.getInstance().updateEncryptedChat(paramEncryptedChat);
    sendRequestKeyMessage(paramEncryptedChat, null);
  }
  
  public void sendAbortKeyMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage, long paramLong)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label98;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label98:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionAbortKey();
      ((TLRPC.TL_decryptedMessageService)localObject).action.exchange_id = paramLong;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
    }
  }
  
  public void sendAcceptKeyMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label91;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label91:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionAcceptKey();
      ((TLRPC.TL_decryptedMessageService)localObject).action.exchange_id = paramEncryptedChat.exchange_id;
      ((TLRPC.TL_decryptedMessageService)localObject).action.key_fingerprint = paramEncryptedChat.future_key_fingerprint;
      ((TLRPC.TL_decryptedMessageService)localObject).action.g_b = paramEncryptedChat.g_a_or_b;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
    }
  }
  
  public void sendClearHistoryMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label91;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label91:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionFlushHistory();
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
    }
  }
  
  public void sendCommitKeyMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label91;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label91:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionCommitKey();
      ((TLRPC.TL_decryptedMessageService)localObject).action.exchange_id = paramEncryptedChat.exchange_id;
      ((TLRPC.TL_decryptedMessageService)localObject).action.key_fingerprint = paramEncryptedChat.future_key_fingerprint;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
    }
  }
  
  public void sendMessagesDeleteMessage(TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label98;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label98:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionDeleteMessages();
      ((TLRPC.TL_decryptedMessageService)localObject).action.random_ids = paramArrayList;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
    }
  }
  
  public void sendMessagesReadMessage(TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label98;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label98:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionReadMessages();
      ((TLRPC.TL_decryptedMessageService)localObject).action.random_ids = paramArrayList;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
    }
  }
  
  public void sendNoopMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label91;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label91:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionNoop();
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
    }
  }
  
  public void sendNotifyLayerMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {}
    while (this.sendingNotifyLayer.contains(Integer.valueOf(paramEncryptedChat.id))) {
      return;
    }
    this.sendingNotifyLayer.add(Integer.valueOf(paramEncryptedChat.id));
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label123;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label123:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionNotifyLayer();
      ((TLRPC.TL_decryptedMessageService)localObject).action.layer = 46;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
    }
  }
  
  public void sendRequestKeyMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label91;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label91:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionRequestKey();
      ((TLRPC.TL_decryptedMessageService)localObject).action.exchange_id = paramEncryptedChat.exchange_id;
      ((TLRPC.TL_decryptedMessageService)localObject).action.g_a = paramEncryptedChat.g_a;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
    }
  }
  
  public void sendScreenshotMessage(TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label98;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label98:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionScreenshotMessages();
      ((TLRPC.TL_decryptedMessageService)localObject).action.random_ids = paramArrayList;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
      paramArrayList = new MessageObject(paramMessage, null, false);
      paramArrayList.messageOwner.send_state = 1;
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(paramArrayList);
      MessagesController.getInstance().updateInterfaceWithMessages(paramMessage.dialog_id, localArrayList);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }
  }
  
  public void sendTTLMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    Object localObject;
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 17)
    {
      localObject = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label91;
      }
      ((TLRPC.TL_decryptedMessageService)localObject).action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      ((TLRPC.TL_decryptedMessageService)localObject).random_id = paramMessage.random_id;
      performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject, paramMessage, paramEncryptedChat, null, null, null);
      return;
      localObject = new TLRPC.TL_decryptedMessageService_layer8();
      ((TLRPC.TL_decryptedMessageService)localObject).random_bytes = new byte[15];
      Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageService)localObject).random_bytes);
      break;
      label91:
      ((TLRPC.TL_decryptedMessageService)localObject).action = new TLRPC.TL_decryptedMessageActionSetMessageTTL();
      ((TLRPC.TL_decryptedMessageService)localObject).action.ttl_seconds = paramEncryptedChat.ttl;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, ((TLRPC.TL_decryptedMessageService)localObject).action);
      MessageObject localMessageObject = new MessageObject(paramMessage, null, false);
      localMessageObject.messageOwner.send_state = 1;
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(localMessageObject);
      MessagesController.getInstance().updateInterfaceWithMessages(paramMessage.dialog_id, localArrayList);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }
  }
  
  public void startSecretChat(final Context paramContext, final TLRPC.User paramUser)
  {
    if ((paramUser == null) || (paramContext == null)) {
      return;
    }
    this.startingSecretChat = true;
    final ProgressDialog localProgressDialog = new ProgressDialog(paramContext);
    localProgressDialog.setMessage(LocaleController.getString("Loading", 2131165834));
    localProgressDialog.setCanceledOnTouchOutside(false);
    localProgressDialog.setCancelable(false);
    TLRPC.TL_messages_getDhConfig localTL_messages_getDhConfig = new TLRPC.TL_messages_getDhConfig();
    localTL_messages_getDhConfig.random_length = 256;
    localTL_messages_getDhConfig.version = MessagesStorage.lastSecretVersion;
    final int i = ConnectionsManager.getInstance().sendRequest(localTL_messages_getDhConfig, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTL_error = (TLRPC.messages_DhConfig)paramAnonymousTLObject;
          if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_dhConfig))
          {
            if (!Utilities.isGoodPrime(paramAnonymousTL_error.p, paramAnonymousTL_error.g))
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    if (!((Activity)SecretChatHelper.14.this.val$context).isFinishing()) {
                      SecretChatHelper.14.this.val$progressDialog.dismiss();
                    }
                    return;
                  }
                  catch (Exception localException)
                  {
                    FileLog.e("tmessages", localException);
                  }
                }
              });
              return;
            }
            MessagesStorage.secretPBytes = paramAnonymousTL_error.p;
            MessagesStorage.secretG = paramAnonymousTL_error.g;
            MessagesStorage.lastSecretVersion = paramAnonymousTL_error.version;
            MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
          }
          final byte[] arrayOfByte = new byte['Ā'];
          int i = 0;
          while (i < 256)
          {
            arrayOfByte[i] = ((byte)((byte)(int)(Utilities.random.nextDouble() * 256.0D) ^ paramAnonymousTL_error.random[i]));
            i += 1;
          }
          paramAnonymousTL_error = BigInteger.valueOf(MessagesStorage.secretG).modPow(new BigInteger(1, arrayOfByte), new BigInteger(1, MessagesStorage.secretPBytes)).toByteArray();
          paramAnonymousTLObject = paramAnonymousTL_error;
          if (paramAnonymousTL_error.length > 256)
          {
            paramAnonymousTLObject = new byte['Ā'];
            System.arraycopy(paramAnonymousTL_error, 1, paramAnonymousTLObject, 0, 256);
          }
          paramAnonymousTL_error = new TLRPC.TL_messages_requestEncryption();
          paramAnonymousTL_error.g_a = paramAnonymousTLObject;
          paramAnonymousTL_error.user_id = MessagesController.getInputUser(paramUser);
          paramAnonymousTL_error.random_id = Utilities.random.nextInt();
          ConnectionsManager.getInstance().sendRequest(paramAnonymousTL_error, new RequestDelegate()
          {
            public void run(final TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              if (paramAnonymous2TL_error == null)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    SecretChatHelper.access$402(SecretChatHelper.this, false);
                    if (!((Activity)SecretChatHelper.14.this.val$context).isFinishing()) {}
                    try
                    {
                      SecretChatHelper.14.this.val$progressDialog.dismiss();
                      TLRPC.EncryptedChat localEncryptedChat = (TLRPC.EncryptedChat)paramAnonymous2TLObject;
                      localEncryptedChat.user_id = localEncryptedChat.participant_id;
                      localEncryptedChat.seq_in = 0;
                      localEncryptedChat.seq_out = 1;
                      localEncryptedChat.a_or_b = SecretChatHelper.14.2.this.val$salt;
                      MessagesController.getInstance().putEncryptedChat(localEncryptedChat, false);
                      TLRPC.TL_dialog localTL_dialog = new TLRPC.TL_dialog();
                      localTL_dialog.id = (localEncryptedChat.id << 32);
                      localTL_dialog.unread_count = 0;
                      localTL_dialog.top_message = 0;
                      localTL_dialog.last_message_date = ConnectionsManager.getInstance().getCurrentTime();
                      MessagesController.getInstance().dialogs_dict.put(Long.valueOf(localTL_dialog.id), localTL_dialog);
                      MessagesController.getInstance().dialogs.add(localTL_dialog);
                      MessagesController.getInstance().sortDialogs(null);
                      MessagesStorage.getInstance().putEncryptedChat(localEncryptedChat, SecretChatHelper.14.this.val$user, localTL_dialog);
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatCreated, new Object[] { localEncryptedChat });
                      Utilities.stageQueue.postRunnable(new Runnable()
                      {
                        public void run()
                        {
                          if (!SecretChatHelper.this.delayedEncryptedChatUpdates.isEmpty())
                          {
                            MessagesController.getInstance().processUpdateArray(SecretChatHelper.this.delayedEncryptedChatUpdates, null, null, false);
                            SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                          }
                        }
                      });
                      return;
                    }
                    catch (Exception localException)
                    {
                      for (;;)
                      {
                        FileLog.e("tmessages", localException);
                      }
                    }
                  }
                });
                return;
              }
              SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (!((Activity)SecretChatHelper.14.this.val$context).isFinishing()) {
                    SecretChatHelper.access$402(SecretChatHelper.this, false);
                  }
                  try
                  {
                    SecretChatHelper.14.this.val$progressDialog.dismiss();
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(SecretChatHelper.14.this.val$context);
                    localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
                    localBuilder.setMessage(LocaleController.getString("CreateEncryptedChatError", 2131165534));
                    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
                    localBuilder.show().setCanceledOnTouchOutside(true);
                    return;
                  }
                  catch (Exception localException)
                  {
                    for (;;)
                    {
                      FileLog.e("tmessages", localException);
                    }
                  }
                }
              });
            }
          }, 2);
          return;
        }
        SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            SecretChatHelper.access$402(SecretChatHelper.this, false);
            if (!((Activity)SecretChatHelper.14.this.val$context).isFinishing()) {}
            try
            {
              SecretChatHelper.14.this.val$progressDialog.dismiss();
              return;
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
            }
          }
        });
      }
    }, 2);
    localProgressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165386), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ConnectionsManager.getInstance().cancelRequest(i, true);
        try
        {
          paramAnonymousDialogInterface.dismiss();
          return;
        }
        catch (Exception paramAnonymousDialogInterface)
        {
          FileLog.e("tmessages", paramAnonymousDialogInterface);
        }
      }
    });
    try
    {
      localProgressDialog.show();
      return;
    }
    catch (Exception paramContext) {}
  }
  
  public static class TL_decryptedMessageHolder
    extends TLObject
  {
    public static int constructor = 1431655929;
    public int date;
    public TLRPC.EncryptedFile file;
    public TLRPC.TL_decryptedMessageLayer layer;
    public boolean new_key_used;
    public long random_id;
    
    public void readParams(AbstractSerializedData paramAbstractSerializedData, boolean paramBoolean)
    {
      this.random_id = paramAbstractSerializedData.readInt64(paramBoolean);
      this.date = paramAbstractSerializedData.readInt32(paramBoolean);
      this.layer = TLRPC.TL_decryptedMessageLayer.TLdeserialize(paramAbstractSerializedData, paramAbstractSerializedData.readInt32(paramBoolean), paramBoolean);
      if (paramAbstractSerializedData.readBool(paramBoolean)) {
        this.file = TLRPC.EncryptedFile.TLdeserialize(paramAbstractSerializedData, paramAbstractSerializedData.readInt32(paramBoolean), paramBoolean);
      }
      this.new_key_used = paramAbstractSerializedData.readBool(paramBoolean);
    }
    
    public void serializeToStream(AbstractSerializedData paramAbstractSerializedData)
    {
      paramAbstractSerializedData.writeInt32(constructor);
      paramAbstractSerializedData.writeInt64(this.random_id);
      paramAbstractSerializedData.writeInt32(this.date);
      this.layer.serializeToStream(paramAbstractSerializedData);
      if (this.file != null) {}
      for (boolean bool = true;; bool = false)
      {
        paramAbstractSerializedData.writeBool(bool);
        if (this.file != null) {
          this.file.serializeToStream(paramAbstractSerializedData);
        }
        paramAbstractSerializedData.writeBool(this.new_key_used);
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/SecretChatHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */