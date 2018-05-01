package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.LongSparseArray;
import android.util.SparseArray;
import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
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
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

public class SecretChatHelper
{
  public static final int CURRENT_SECRET_CHAT_LAYER = 73;
  private static volatile SecretChatHelper[] Instance = new SecretChatHelper[3];
  private SparseArray<TLRPC.EncryptedChat> acceptingChats = new SparseArray();
  private int currentAccount;
  public ArrayList<TLRPC.Update> delayedEncryptedChatUpdates = new ArrayList();
  private ArrayList<Long> pendingEncMessagesToDelete = new ArrayList();
  private SparseArray<ArrayList<TL_decryptedMessageHolder>> secretHolesQueue = new SparseArray();
  private ArrayList<Integer> sendingNotifyLayer = new ArrayList();
  private boolean startingSecretChat = false;
  
  public SecretChatHelper(int paramInt)
  {
    this.currentAccount = paramInt;
  }
  
  private void applyPeerLayer(final TLRPC.EncryptedChat paramEncryptedChat, int paramInt)
  {
    int i = AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer);
    if (paramInt <= i) {}
    for (;;)
    {
      return;
      if ((paramEncryptedChat.key_hash.length == 16) && (i >= 46)) {}
      try
      {
        byte[] arrayOfByte1 = Utilities.computeSHA256(paramEncryptedChat.auth_key, 0, paramEncryptedChat.auth_key.length);
        byte[] arrayOfByte2 = new byte[36];
        System.arraycopy(paramEncryptedChat.key_hash, 0, arrayOfByte2, 0, 16);
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 16, 20);
        paramEncryptedChat.key_hash = arrayOfByte2;
        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
        paramEncryptedChat.layer = AndroidUtilities.setPeerLayerVersion(paramEncryptedChat.layer, paramInt);
        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatLayer(paramEncryptedChat);
        if (i < 73) {
          sendNotifyLayerMessage(paramEncryptedChat, null);
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { paramEncryptedChat });
          }
        });
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          FileLog.e(localThrowable);
        }
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
    localTL_messageService.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
    localTL_messageService.unread = true;
    localTL_messageService.out = true;
    localTL_messageService.flags = 256;
    localTL_messageService.dialog_id = (paramEncryptedChat.id << 32);
    localTL_messageService.to_id = new TLRPC.TL_peerUser();
    localTL_messageService.send_state = 1;
    localTL_messageService.seq_in = paramInt3;
    localTL_messageService.seq_out = paramInt2;
    if (paramEncryptedChat.participant_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {}
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
    int i = UserConfig.getInstance(this.currentAccount).getNewMessageId();
    localTL_messageService.id = i;
    localTL_messageService.local_id = i;
    localTL_messageService.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
    localTL_messageService.unread = true;
    localTL_messageService.out = true;
    localTL_messageService.flags = 256;
    localTL_messageService.dialog_id = (paramEncryptedChat.id << 32);
    localTL_messageService.to_id = new TLRPC.TL_peerUser();
    localTL_messageService.send_state = 1;
    if (paramEncryptedChat.participant_id == UserConfig.getInstance(this.currentAccount).getClientUserId())
    {
      localTL_messageService.to_id.user_id = paramEncryptedChat.admin_id;
      if ((!(paramDecryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) && (!(paramDecryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))) {
        break label236;
      }
    }
    label236:
    for (localTL_messageService.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();; localTL_messageService.date = 0)
    {
      localTL_messageService.random_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
      UserConfig.getInstance(this.currentAccount).saveConfig(false);
      paramEncryptedChat = new ArrayList();
      paramEncryptedChat.add(localTL_messageService);
      MessagesStorage.getInstance(this.currentAccount).putMessages(paramEncryptedChat, false, true, true, 0);
      return localTL_messageService;
      localTL_messageService.to_id.user_id = paramEncryptedChat.participant_id;
      break;
    }
  }
  
  private boolean decryptWithMtProtoVersion(NativeByteBuffer paramNativeByteBuffer, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramInt == 1) {
      paramBoolean1 = false;
    }
    MessageKeyData localMessageKeyData = MessageKeyData.generateMessageKeyData(paramArrayOfByte1, paramArrayOfByte2, paramBoolean1, paramInt);
    Utilities.aesIgeEncryption(paramNativeByteBuffer.buffer, localMessageKeyData.aesKey, localMessageKeyData.aesIv, false, false, 24, paramNativeByteBuffer.limit() - 24);
    int i = paramNativeByteBuffer.readInt32(false);
    int j;
    if (paramInt == 2) {
      if (paramBoolean1)
      {
        j = 8;
        if (Utilities.arraysEquals(paramArrayOfByte2, 0, Utilities.computeSHA256(paramArrayOfByte1, j + 88, 32, paramNativeByteBuffer.buffer, 24, paramNativeByteBuffer.buffer.limit()), 8)) {
          break label273;
        }
        if (paramBoolean2)
        {
          Utilities.aesIgeEncryption(paramNativeByteBuffer.buffer, localMessageKeyData.aesKey, localMessageKeyData.aesIv, true, false, 24, paramNativeByteBuffer.limit() - 24);
          paramNativeByteBuffer.position(24);
        }
        paramBoolean1 = false;
      }
    }
    for (;;)
    {
      return paramBoolean1;
      j = 0;
      break;
      int k = i + 28;
      if (k >= paramNativeByteBuffer.buffer.limit() - 15)
      {
        j = k;
        if (k <= paramNativeByteBuffer.buffer.limit()) {}
      }
      else
      {
        j = paramNativeByteBuffer.buffer.limit();
      }
      paramArrayOfByte1 = Utilities.computeSHA1(paramNativeByteBuffer.buffer, 24, j);
      if (!Utilities.arraysEquals(paramArrayOfByte2, 0, paramArrayOfByte1, paramArrayOfByte1.length - 16))
      {
        if (paramBoolean2)
        {
          Utilities.aesIgeEncryption(paramNativeByteBuffer.buffer, localMessageKeyData.aesKey, localMessageKeyData.aesIv, true, false, 24, paramNativeByteBuffer.limit() - 24);
          paramNativeByteBuffer.position(24);
        }
        paramBoolean1 = false;
      }
      else
      {
        label273:
        if ((i <= 0) || (i > paramNativeByteBuffer.limit() - 28))
        {
          paramBoolean1 = false;
        }
        else
        {
          j = paramNativeByteBuffer.limit() - 28 - i;
          if (((paramInt == 2) && ((j < 12) || (j > 1024))) || ((paramInt == 1) && (j > 15))) {
            paramBoolean1 = false;
          } else {
            paramBoolean1 = true;
          }
        }
      }
    }
  }
  
  public static SecretChatHelper getInstance(int paramInt)
  {
    Object localObject1 = Instance[paramInt];
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject1 = Instance[paramInt];
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = Instance;
        localObject2 = new org/telegram/messenger/SecretChatHelper;
        ((SecretChatHelper)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (SecretChatHelper)localObject2;
    }
    finally {}
  }
  
  public static boolean isSecretInvisibleMessage(TLRPC.Message paramMessage)
  {
    if (((paramMessage.action instanceof TLRPC.TL_messageEncryptedAction)) && (!(paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) && (!(paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isSecretVisibleMessage(TLRPC.Message paramMessage)
  {
    if (((paramMessage.action instanceof TLRPC.TL_messageEncryptedAction)) && (((paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) || ((paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void resendMessages(final int paramInt1, final int paramInt2, final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if ((paramEncryptedChat == null) || (paramInt2 - paramInt1 < 0)) {}
    for (;;)
    {
      return;
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            i = paramInt1;
            j = i;
            if (paramEncryptedChat.admin_id == UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId())
            {
              j = i;
              if (i % 2 == 0) {
                j = i + 1;
              }
            }
            localObject1 = MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", new Object[] { Integer.valueOf(paramEncryptedChat.id), Integer.valueOf(j), Integer.valueOf(j), Integer.valueOf(paramInt2), Integer.valueOf(paramInt2) }), new Object[0]);
            boolean bool = ((SQLiteCursor)localObject1).next();
            ((SQLiteCursor)localObject1).dispose();
            if (bool) {
              return;
            }
          }
          catch (Exception localException)
          {
            for (;;)
            {
              int i;
              int j;
              Object localObject1;
              long l1;
              ArrayList localArrayList;
              long l3;
              int k;
              int m;
              FileLog.e(localException);
              continue;
              Object localObject2 = SecretChatHelper.this.createDeleteMessage(m, k, i, l3, paramEncryptedChat);
              continue;
              label441:
              ((SQLiteCursor)localObject4).dispose();
              if (((SparseArray)localObject3).size() != 0)
              {
                for (i = 0; i < ((SparseArray)localObject3).size(); i++) {
                  localArrayList.add(SecretChatHelper.this.createDeleteMessage(UserConfig.getInstance(SecretChatHelper.this.currentAccount).getNewMessageId(), ((SparseArray)localObject3).keyAt(i), 0, Utilities.random.nextLong(), paramEncryptedChat));
                }
                UserConfig.getInstance(SecretChatHelper.this.currentAccount).saveConfig(false);
              }
              localObject2 = new org/telegram/messenger/SecretChatHelper$7$1;
              ((1)localObject2).<init>(this);
              Collections.sort(localArrayList, (Comparator)localObject2);
              localObject2 = new java/util/ArrayList;
              ((ArrayList)localObject2).<init>();
              ((ArrayList)localObject2).add(paramEncryptedChat);
              Object localObject4 = new org/telegram/messenger/SecretChatHelper$7$2;
              ((2)localObject4).<init>(this, localArrayList);
              AndroidUtilities.runOnUIThread((Runnable)localObject4);
              Object localObject5 = SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount);
              localObject4 = new java/util/ArrayList;
              ((ArrayList)localObject4).<init>();
              Object localObject3 = new java/util/ArrayList;
              ((ArrayList)localObject3).<init>();
              ((SendMessagesHelper)localObject5).processUnsentMessages(localArrayList, (ArrayList)localObject4, (ArrayList)localObject3, (ArrayList)localObject2);
              MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[] { Integer.valueOf(paramEncryptedChat.id), Integer.valueOf(j), Integer.valueOf(paramInt2) })).stepThis().dispose();
            }
          }
          l1 = paramEncryptedChat.id << 32;
          localObject3 = new android/util/SparseArray;
          ((SparseArray)localObject3).<init>();
          localArrayList = new java/util/ArrayList;
          localArrayList.<init>();
          for (i = j; i < paramInt2; i += 2) {
            ((SparseArray)localObject3).put(i, null);
          }
          localObject4 = MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms as r ON r.mid = s.mid LEFT JOIN messages as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[] { Long.valueOf(l1), Integer.valueOf(j), Integer.valueOf(paramInt2) }), new Object[0]);
          for (;;)
          {
            if (!((SQLiteCursor)localObject4).next()) {
              break label441;
            }
            long l2 = ((SQLiteCursor)localObject4).longValue(1);
            l3 = l2;
            if (l2 == 0L) {
              l3 = Utilities.random.nextLong();
            }
            i = ((SQLiteCursor)localObject4).intValue(2);
            k = ((SQLiteCursor)localObject4).intValue(3);
            m = ((SQLiteCursor)localObject4).intValue(5);
            localObject5 = ((SQLiteCursor)localObject4).byteBufferValue(0);
            if (localObject5 == null) {
              break;
            }
            localObject1 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject5, ((NativeByteBuffer)localObject5).readInt32(false), false);
            ((TLRPC.Message)localObject1).readAttachPath((AbstractSerializedData)localObject5, UserConfig.getInstance(SecretChatHelper.this.currentAccount).clientUserId);
            ((NativeByteBuffer)localObject5).reuse();
            ((TLRPC.Message)localObject1).random_id = l3;
            ((TLRPC.Message)localObject1).dialog_id = l1;
            ((TLRPC.Message)localObject1).seq_in = i;
            ((TLRPC.Message)localObject1).seq_out = k;
            ((TLRPC.Message)localObject1).ttl = ((SQLiteCursor)localObject4).intValue(4);
            localArrayList.add(localObject1);
            ((SparseArray)localObject3).remove(k);
          }
        }
      });
    }
  }
  
  private void updateMediaPaths(MessageObject paramMessageObject, TLRPC.EncryptedFile paramEncryptedFile, TLRPC.DecryptedMessage paramDecryptedMessage, String paramString)
  {
    paramString = paramMessageObject.messageOwner;
    Object localObject;
    if (paramEncryptedFile != null)
    {
      if ((!(paramString.media instanceof TLRPC.TL_messageMediaPhoto)) || (paramString.media.photo == null)) {
        break label309;
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
      new File(FileLoader.getDirectory(4), (String)localObject + ".jpg").renameTo(FileLoader.getPathToAttach(paramMessageObject));
      ImageLoader.getInstance().replaceImageInCache((String)localObject, paramEncryptedFile, paramMessageObject.location, true);
      paramMessageObject = new ArrayList();
      paramMessageObject.add(paramString);
      MessagesStorage.getInstance(this.currentAccount).putMessages(paramMessageObject, false, true, false, 0);
    }
    for (;;)
    {
      return;
      label309:
      if (((paramString.media instanceof TLRPC.TL_messageMediaDocument)) && (paramString.media.document != null))
      {
        localObject = paramString.media.document;
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
        if ((paramString.attachPath != null) && (paramString.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath())) && (new File(paramString.attachPath).renameTo(FileLoader.getPathToAttach(paramString.media.document))))
        {
          paramMessageObject.mediaExists = paramMessageObject.attachPathExists;
          paramMessageObject.attachPathExists = false;
          paramString.attachPath = "";
        }
        paramMessageObject = new ArrayList();
        paramMessageObject.add(paramString);
        MessagesStorage.getInstance(this.currentAccount).putMessages(paramMessageObject, false, true, false, 0);
      }
    }
  }
  
  public void acceptSecretChat(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (this.acceptingChats.get(paramEncryptedChat.id) != null) {}
    for (;;)
    {
      return;
      this.acceptingChats.put(paramEncryptedChat.id, paramEncryptedChat);
      TLRPC.TL_messages_getDhConfig localTL_messages_getDhConfig = new TLRPC.TL_messages_getDhConfig();
      localTL_messages_getDhConfig.random_length = 256;
      localTL_messages_getDhConfig.version = MessagesStorage.getInstance(this.currentAccount).getLastSecretVersion();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getDhConfig, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTL_error = (TLRPC.messages_DhConfig)paramAnonymousTLObject;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_dhConfig)) {
              if (!Utilities.isGoodPrime(paramAnonymousTL_error.p, paramAnonymousTL_error.g))
              {
                SecretChatHelper.this.acceptingChats.remove(paramEncryptedChat.id);
                SecretChatHelper.this.declineSecretChat(paramEncryptedChat.id);
              }
            }
          }
          for (;;)
          {
            return;
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretPBytes(paramAnonymousTL_error.p);
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretG(paramAnonymousTL_error.g);
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setLastSecretVersion(paramAnonymousTL_error.version);
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).saveSecretParams(MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getLastSecretVersion(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
            byte[] arrayOfByte = new byte['Ā'];
            for (int i = 0; i < 256; i++) {
              arrayOfByte[i] = ((byte)(byte)((byte)(int)(Utilities.random.nextDouble() * 256.0D) ^ paramAnonymousTL_error.random[i]));
            }
            paramEncryptedChat.a_or_b = arrayOfByte;
            paramEncryptedChat.seq_in = -1;
            paramEncryptedChat.seq_out = 0;
            Object localObject = new BigInteger(1, MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
            paramAnonymousTLObject = BigInteger.valueOf(MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG()).modPow(new BigInteger(1, arrayOfByte), (BigInteger)localObject);
            BigInteger localBigInteger = new BigInteger(1, paramEncryptedChat.g_a);
            if (!Utilities.isGoodGaAndGb(localBigInteger, (BigInteger)localObject))
            {
              SecretChatHelper.this.acceptingChats.remove(paramEncryptedChat.id);
              SecretChatHelper.this.declineSecretChat(paramEncryptedChat.id);
            }
            else
            {
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
                paramEncryptedChat.key_create_date = ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime();
                paramAnonymousTLObject = new TLRPC.TL_messages_acceptEncryption();
                paramAnonymousTLObject.g_b = paramAnonymousTL_error;
                paramAnonymousTLObject.peer = new TLRPC.TL_inputEncryptedChat();
                paramAnonymousTLObject.peer.chat_id = paramEncryptedChat.id;
                paramAnonymousTLObject.peer.access_hash = paramEncryptedChat.access_hash;
                paramAnonymousTLObject.key_fingerprint = Utilities.bytesToLong(arrayOfByte);
                ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).sendRequest(paramAnonymousTLObject, new RequestDelegate()
                {
                  public void run(final TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
                  {
                    SecretChatHelper.this.acceptingChats.remove(SecretChatHelper.13.this.val$encryptedChat.id);
                    if (paramAnonymous2TL_error == null)
                    {
                      paramAnonymous2TLObject = (TLRPC.EncryptedChat)paramAnonymous2TLObject;
                      paramAnonymous2TLObject.auth_key = SecretChatHelper.13.this.val$encryptedChat.auth_key;
                      paramAnonymous2TLObject.user_id = SecretChatHelper.13.this.val$encryptedChat.user_id;
                      paramAnonymous2TLObject.seq_in = SecretChatHelper.13.this.val$encryptedChat.seq_in;
                      paramAnonymous2TLObject.seq_out = SecretChatHelper.13.this.val$encryptedChat.seq_out;
                      paramAnonymous2TLObject.key_create_date = SecretChatHelper.13.this.val$encryptedChat.key_create_date;
                      paramAnonymous2TLObject.key_use_count_in = ((short)SecretChatHelper.13.this.val$encryptedChat.key_use_count_in);
                      paramAnonymous2TLObject.key_use_count_out = ((short)SecretChatHelper.13.this.val$encryptedChat.key_use_count_out);
                      MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(paramAnonymous2TLObject);
                      MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(paramAnonymous2TLObject, false);
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { paramAnonymous2TLObject });
                          SecretChatHelper.this.sendNotifyLayerMessage(paramAnonymous2TLObject, null);
                        }
                      });
                    }
                  }
                });
                break;
                paramAnonymousTLObject = arrayOfByte;
                if (arrayOfByte.length < 256)
                {
                  paramAnonymousTLObject = new byte['Ā'];
                  System.arraycopy(arrayOfByte, 0, paramAnonymousTLObject, 256 - arrayOfByte.length, arrayOfByte.length);
                  for (i = 0; i < 256 - arrayOfByte.length; i++) {
                    arrayOfByte[i] = ((byte)0);
                  }
                }
              }
              SecretChatHelper.this.acceptingChats.remove(paramEncryptedChat.id);
            }
          }
        }
      });
    }
  }
  
  public void checkSecretHoles(TLRPC.EncryptedChat paramEncryptedChat, ArrayList<TLRPC.Message> paramArrayList)
  {
    ArrayList localArrayList = (ArrayList)this.secretHolesQueue.get(paramEncryptedChat.id);
    if (localArrayList == null) {}
    for (;;)
    {
      return;
      Collections.sort(localArrayList, new Comparator()
      {
        public int compare(SecretChatHelper.TL_decryptedMessageHolder paramAnonymousTL_decryptedMessageHolder1, SecretChatHelper.TL_decryptedMessageHolder paramAnonymousTL_decryptedMessageHolder2)
        {
          int i;
          if (paramAnonymousTL_decryptedMessageHolder1.layer.out_seq_no > paramAnonymousTL_decryptedMessageHolder2.layer.out_seq_no) {
            i = 1;
          }
          for (;;)
          {
            return i;
            if (paramAnonymousTL_decryptedMessageHolder1.layer.out_seq_no < paramAnonymousTL_decryptedMessageHolder2.layer.out_seq_no) {
              i = -1;
            } else {
              i = 0;
            }
          }
        }
      });
      int i = 0;
      for (int j = 0; localArrayList.size() > 0; j = j - 1 + 1)
      {
        Object localObject = (TL_decryptedMessageHolder)localArrayList.get(j);
        if ((((TL_decryptedMessageHolder)localObject).layer.out_seq_no != paramEncryptedChat.seq_in) && (paramEncryptedChat.seq_in != ((TL_decryptedMessageHolder)localObject).layer.out_seq_no - 2)) {
          break;
        }
        applyPeerLayer(paramEncryptedChat, ((TL_decryptedMessageHolder)localObject).layer.layer);
        paramEncryptedChat.seq_in = ((TL_decryptedMessageHolder)localObject).layer.out_seq_no;
        paramEncryptedChat.in_seq_no = ((TL_decryptedMessageHolder)localObject).layer.in_seq_no;
        localArrayList.remove(j);
        i = 1;
        if (((TL_decryptedMessageHolder)localObject).decryptedWithVersion == 2) {
          paramEncryptedChat.mtproto_seq = Math.min(paramEncryptedChat.mtproto_seq, paramEncryptedChat.seq_in);
        }
        localObject = processDecryptedObject(paramEncryptedChat, ((TL_decryptedMessageHolder)localObject).file, ((TL_decryptedMessageHolder)localObject).date, ((TL_decryptedMessageHolder)localObject).layer.message, ((TL_decryptedMessageHolder)localObject).new_key_used);
        if (localObject != null) {
          paramArrayList.add(localObject);
        }
      }
      if (localArrayList.isEmpty()) {
        this.secretHolesQueue.remove(paramEncryptedChat.id);
      }
      if (i != 0) {
        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatSeq(paramEncryptedChat, true);
      }
    }
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
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_discardEncryption, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  protected ArrayList<TLRPC.Message> decryptMessage(TLRPC.EncryptedMessage paramEncryptedMessage)
  {
    TLRPC.EncryptedChat localEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChatDB(paramEncryptedMessage.chat_id, true);
    if ((localEncryptedChat == null) || ((localEncryptedChat instanceof TLRPC.TL_encryptedChatDiscarded))) {
      paramEncryptedMessage = null;
    }
    Object localObject1;
    long l;
    Object localObject2;
    Object localObject3;
    boolean bool2;
    label90:
    label105:
    int j;
    for (;;)
    {
      return paramEncryptedMessage;
      try
      {
        localObject1 = new org/telegram/tgnet/NativeByteBuffer;
        ((NativeByteBuffer)localObject1).<init>(paramEncryptedMessage.bytes.length);
        ((NativeByteBuffer)localObject1).writeBytes(paramEncryptedMessage.bytes);
        ((NativeByteBuffer)localObject1).position(0);
        l = ((NativeByteBuffer)localObject1).readInt64(false);
        localObject2 = null;
        boolean bool1 = false;
        int i;
        if (localEncryptedChat.key_fingerprint == l)
        {
          localObject3 = localEncryptedChat.auth_key;
          bool2 = bool1;
          if (AndroidUtilities.getPeerLayerVersion(localEncryptedChat.layer) < 73) {
            break label270;
          }
          i = 2;
          j = i;
          if (localObject3 == null) {
            break label1000;
          }
          localObject2 = ((NativeByteBuffer)localObject1).readData(16, false);
          if (localEncryptedChat.admin_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            break label276;
          }
        }
        boolean bool4;
        label270:
        label276:
        for (bool1 = true;; bool1 = false)
        {
          boolean bool3 = true;
          bool4 = bool3;
          if (j == 2)
          {
            bool4 = bool3;
            if (localEncryptedChat.mtproto_seq != 0) {
              bool4 = false;
            }
          }
          if (decryptWithMtProtoVersion((NativeByteBuffer)localObject1, (byte[])localObject3, (byte[])localObject2, i, bool1, bool4)) {
            break label307;
          }
          if (i != 2) {
            break label282;
          }
          j = 1;
          if ((bool4) && (decryptWithMtProtoVersion((NativeByteBuffer)localObject1, (byte[])localObject3, (byte[])localObject2, 1, bool1, false))) {
            break label307;
          }
          paramEncryptedMessage = null;
          break;
          localObject3 = localObject2;
          bool2 = bool1;
          if (localEncryptedChat.future_key_fingerprint == 0L) {
            break label90;
          }
          localObject3 = localObject2;
          bool2 = bool1;
          if (localEncryptedChat.future_key_fingerprint != l) {
            break label90;
          }
          localObject3 = localEncryptedChat.future_auth_key;
          bool2 = true;
          break label90;
          i = 1;
          break label105;
        }
        label282:
        j = 2;
        if (!decryptWithMtProtoVersion((NativeByteBuffer)localObject1, (byte[])localObject3, (byte[])localObject2, 2, bool1, bool4))
        {
          paramEncryptedMessage = null;
        }
        else
        {
          label307:
          localObject2 = TLClassStore.Instance().TLdeserialize((NativeByteBuffer)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
          ((NativeByteBuffer)localObject1).reuse();
          if ((!bool2) && (AndroidUtilities.getPeerLayerVersion(localEncryptedChat.layer) >= 20)) {
            localEncryptedChat.key_use_count_in = ((short)(short)(localEncryptedChat.key_use_count_in + 1));
          }
          if ((localObject2 instanceof TLRPC.TL_decryptedMessageLayer))
          {
            localObject1 = (TLRPC.TL_decryptedMessageLayer)localObject2;
            if ((localEncryptedChat.seq_in == 0) && (localEncryptedChat.seq_out == 0))
            {
              if (localEncryptedChat.admin_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                break label438;
              }
              localEncryptedChat.seq_out = 1;
              localEncryptedChat.seq_in = -2;
            }
            for (;;)
            {
              if (((TLRPC.TL_decryptedMessageLayer)localObject1).random_bytes.length < 15)
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.e("got random bytes less than needed");
                }
                paramEncryptedMessage = null;
                break;
                label438:
                localEncryptedChat.seq_in = -1;
                continue;
                paramEncryptedMessage = null;
              }
            }
          }
        }
      }
      catch (Exception paramEncryptedMessage)
      {
        FileLog.e(paramEncryptedMessage);
      }
    }
    for (;;)
    {
      break;
      if (BuildVars.LOGS_ENABLED)
      {
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        FileLog.d("current chat in_seq = " + localEncryptedChat.seq_in + " out_seq = " + localEncryptedChat.seq_out);
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        FileLog.d("got message with in_seq = " + ((TLRPC.TL_decryptedMessageLayer)localObject1).in_seq_no + " out_seq = " + ((TLRPC.TL_decryptedMessageLayer)localObject1).out_seq_no);
      }
      if (((TLRPC.TL_decryptedMessageLayer)localObject1).out_seq_no <= localEncryptedChat.seq_in)
      {
        paramEncryptedMessage = null;
        break;
      }
      if ((j == 1) && (localEncryptedChat.mtproto_seq != 0) && (((TLRPC.TL_decryptedMessageLayer)localObject1).out_seq_no >= localEncryptedChat.mtproto_seq))
      {
        paramEncryptedMessage = null;
        break;
      }
      if (localEncryptedChat.seq_in != ((TLRPC.TL_decryptedMessageLayer)localObject1).out_seq_no - 2)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("got hole");
        }
        localObject2 = (ArrayList)this.secretHolesQueue.get(localEncryptedChat.id);
        localObject3 = localObject2;
        if (localObject2 == null)
        {
          localObject3 = new java/util/ArrayList;
          ((ArrayList)localObject3).<init>();
          this.secretHolesQueue.put(localEncryptedChat.id, localObject3);
        }
        if (((ArrayList)localObject3).size() >= 4)
        {
          this.secretHolesQueue.remove(localEncryptedChat.id);
          paramEncryptedMessage = new org/telegram/tgnet/TLRPC$TL_encryptedChatDiscarded;
          paramEncryptedMessage.<init>();
          paramEncryptedMessage.id = localEncryptedChat.id;
          paramEncryptedMessage.user_id = localEncryptedChat.user_id;
          paramEncryptedMessage.auth_key = localEncryptedChat.auth_key;
          paramEncryptedMessage.key_create_date = localEncryptedChat.key_create_date;
          paramEncryptedMessage.key_use_count_in = ((short)localEncryptedChat.key_use_count_in);
          paramEncryptedMessage.key_use_count_out = ((short)localEncryptedChat.key_use_count_out);
          paramEncryptedMessage.seq_in = localEncryptedChat.seq_in;
          paramEncryptedMessage.seq_out = localEncryptedChat.seq_out;
          localObject3 = new org/telegram/messenger/SecretChatHelper$9;
          ((9)localObject3).<init>(this, paramEncryptedMessage);
          AndroidUtilities.runOnUIThread((Runnable)localObject3);
          declineSecretChat(localEncryptedChat.id);
          paramEncryptedMessage = null;
          break;
        }
        localObject2 = new org/telegram/messenger/SecretChatHelper$TL_decryptedMessageHolder;
        ((TL_decryptedMessageHolder)localObject2).<init>();
        ((TL_decryptedMessageHolder)localObject2).layer = ((TLRPC.TL_decryptedMessageLayer)localObject1);
        ((TL_decryptedMessageHolder)localObject2).file = paramEncryptedMessage.file;
        ((TL_decryptedMessageHolder)localObject2).date = paramEncryptedMessage.date;
        ((TL_decryptedMessageHolder)localObject2).new_key_used = bool2;
        ((TL_decryptedMessageHolder)localObject2).decryptedWithVersion = j;
        ((ArrayList)localObject3).add(localObject2);
        paramEncryptedMessage = null;
        break;
      }
      if (j == 2) {
        localEncryptedChat.mtproto_seq = Math.min(localEncryptedChat.mtproto_seq, localEncryptedChat.seq_in);
      }
      applyPeerLayer(localEncryptedChat, ((TLRPC.TL_decryptedMessageLayer)localObject1).layer);
      localEncryptedChat.seq_in = ((TLRPC.TL_decryptedMessageLayer)localObject1).out_seq_no;
      localEncryptedChat.in_seq_no = ((TLRPC.TL_decryptedMessageLayer)localObject1).in_seq_no;
      MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatSeq(localEncryptedChat, true);
      localObject3 = ((TLRPC.TL_decryptedMessageLayer)localObject1).message;
      do
      {
        localObject2 = new java/util/ArrayList;
        ((ArrayList)localObject2).<init>();
        paramEncryptedMessage = processDecryptedObject(localEncryptedChat, paramEncryptedMessage.file, paramEncryptedMessage.date, (TLObject)localObject3, bool2);
        if (paramEncryptedMessage != null) {
          ((ArrayList)localObject2).add(paramEncryptedMessage);
        }
        checkSecretHoles(localEncryptedChat, (ArrayList)localObject2);
        paramEncryptedMessage = (TLRPC.EncryptedMessage)localObject2;
        break;
        if (!(localObject2 instanceof TLRPC.TL_decryptedMessageService)) {
          break label995;
        }
        localObject3 = localObject2;
      } while ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer));
      label995:
      paramEncryptedMessage = null;
      break;
      label1000:
      ((NativeByteBuffer)localObject1).reuse();
      if (BuildVars.LOGS_ENABLED) {
        FileLog.e(String.format("fingerprint mismatch %x", new Object[] { Long.valueOf(l) }));
      }
    }
  }
  
  protected void performSendEncryptedRequest(final TLRPC.DecryptedMessage paramDecryptedMessage, final TLRPC.Message paramMessage, final TLRPC.EncryptedChat paramEncryptedChat, final TLRPC.InputEncryptedFile paramInputEncryptedFile, final String paramString, final MessageObject paramMessageObject)
  {
    if ((paramDecryptedMessage == null) || (paramEncryptedChat.auth_key == null) || ((paramEncryptedChat instanceof TLRPC.TL_encryptedChatRequested)) || ((paramEncryptedChat instanceof TLRPC.TL_encryptedChatWaiting))) {}
    for (;;)
    {
      return;
      SendMessagesHelper.getInstance(this.currentAccount).putToSendingMessages(paramMessage);
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            try
            {
              localObject1 = new org/telegram/tgnet/TLRPC$TL_decryptedMessageLayer;
              ((TLRPC.TL_decryptedMessageLayer)localObject1).<init>();
              ((TLRPC.TL_decryptedMessageLayer)localObject1).layer = Math.min(Math.max(46, AndroidUtilities.getMyLayerVersion(paramEncryptedChat.layer)), Math.max(46, AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer)));
              ((TLRPC.TL_decryptedMessageLayer)localObject1).message = paramDecryptedMessage;
              ((TLRPC.TL_decryptedMessageLayer)localObject1).random_bytes = new byte[15];
              Utilities.random.nextBytes(((TLRPC.TL_decryptedMessageLayer)localObject1).random_bytes);
              if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) < 73) {
                continue;
              }
              i = 2;
              if ((paramEncryptedChat.seq_in == 0) && (paramEncryptedChat.seq_out == 0))
              {
                if (paramEncryptedChat.admin_id != UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId()) {
                  continue;
                }
                paramEncryptedChat.seq_out = 1;
                paramEncryptedChat.seq_in = -2;
              }
              if ((paramMessage.seq_in != 0) || (paramMessage.seq_out != 0)) {
                continue;
              }
              if (paramEncryptedChat.seq_in <= 0) {
                continue;
              }
              j = paramEncryptedChat.seq_in;
            }
            catch (Exception localException)
            {
              Object localObject1;
              int i;
              Object localObject4;
              int k;
              int m;
              FileLog.e(localException);
              continue;
              int j = paramEncryptedChat.seq_in + 2;
              continue;
              localException.in_seq_no = paramMessage.seq_in;
              localException.out_seq_no = paramMessage.seq_out;
              continue;
              j = 0;
              continue;
              boolean bool = false;
              continue;
              j = 0;
              continue;
              byte[] arrayOfByte = Utilities.computeSHA1(((NativeByteBuffer)localObject4).buffer);
              System.arraycopy(arrayOfByte, arrayOfByte.length - 16, localObject3, 0, 16);
              continue;
              Object localObject2 = new org/telegram/tgnet/TLRPC$TL_messages_sendEncrypted;
              ((TLRPC.TL_messages_sendEncrypted)localObject2).<init>();
              ((TLRPC.TL_messages_sendEncrypted)localObject2).data = ((NativeByteBuffer)localObject4);
              ((TLRPC.TL_messages_sendEncrypted)localObject2).random_id = paramDecryptedMessage.random_id;
              Object localObject3 = new org/telegram/tgnet/TLRPC$TL_inputEncryptedChat;
              ((TLRPC.TL_inputEncryptedChat)localObject3).<init>();
              ((TLRPC.TL_messages_sendEncrypted)localObject2).peer = ((TLRPC.TL_inputEncryptedChat)localObject3);
              ((TLRPC.TL_messages_sendEncrypted)localObject2).peer.chat_id = paramEncryptedChat.id;
              ((TLRPC.TL_messages_sendEncrypted)localObject2).peer.access_hash = paramEncryptedChat.access_hash;
              continue;
              localObject2 = new org/telegram/tgnet/TLRPC$TL_messages_sendEncryptedFile;
              ((TLRPC.TL_messages_sendEncryptedFile)localObject2).<init>();
              ((TLRPC.TL_messages_sendEncryptedFile)localObject2).data = ((NativeByteBuffer)localObject4);
              ((TLRPC.TL_messages_sendEncryptedFile)localObject2).random_id = paramDecryptedMessage.random_id;
              localObject3 = new org/telegram/tgnet/TLRPC$TL_inputEncryptedChat;
              ((TLRPC.TL_inputEncryptedChat)localObject3).<init>();
              ((TLRPC.TL_messages_sendEncryptedFile)localObject2).peer = ((TLRPC.TL_inputEncryptedChat)localObject3);
              ((TLRPC.TL_messages_sendEncryptedFile)localObject2).peer.chat_id = paramEncryptedChat.id;
              ((TLRPC.TL_messages_sendEncryptedFile)localObject2).peer.access_hash = paramEncryptedChat.access_hash;
              ((TLRPC.TL_messages_sendEncryptedFile)localObject2).file = paramInputEncryptedFile;
              continue;
            }
            ((TLRPC.TL_decryptedMessageLayer)localObject1).in_seq_no = j;
            ((TLRPC.TL_decryptedMessageLayer)localObject1).out_seq_no = paramEncryptedChat.seq_out;
            localObject3 = paramEncryptedChat;
            ((TLRPC.EncryptedChat)localObject3).seq_out += 2;
            if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 20)
            {
              if (paramEncryptedChat.key_create_date == 0) {
                paramEncryptedChat.key_create_date = ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime();
              }
              localObject3 = paramEncryptedChat;
              ((TLRPC.EncryptedChat)localObject3).key_use_count_out = ((short)(short)(((TLRPC.EncryptedChat)localObject3).key_use_count_out + 1));
              if (((paramEncryptedChat.key_use_count_out >= 100) || (paramEncryptedChat.key_create_date < ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime() - 604800)) && (paramEncryptedChat.exchange_id == 0L) && (paramEncryptedChat.future_key_fingerprint == 0L)) {
                SecretChatHelper.this.requestNewSecretChatKey(paramEncryptedChat);
              }
            }
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChatSeq(paramEncryptedChat, false);
            if (paramMessage != null)
            {
              paramMessage.seq_in = ((TLRPC.TL_decryptedMessageLayer)localObject1).in_seq_no;
              paramMessage.seq_out = ((TLRPC.TL_decryptedMessageLayer)localObject1).out_seq_no;
              MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setMessageSeq(paramMessage.id, paramMessage.seq_in, paramMessage.seq_out);
            }
            if (BuildVars.LOGS_ENABLED)
            {
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              FileLog.d(paramDecryptedMessage + " send message with in_seq = " + ((TLRPC.TL_decryptedMessageLayer)localObject1).in_seq_no + " out_seq = " + ((TLRPC.TL_decryptedMessageLayer)localObject1).out_seq_no);
            }
            j = ((TLObject)localObject1).getObjectSize();
            localObject4 = new org/telegram/tgnet/NativeByteBuffer;
            ((NativeByteBuffer)localObject4).<init>(j + 4);
            ((NativeByteBuffer)localObject4).writeInt32(j);
            ((TLObject)localObject1).serializeToStream((AbstractSerializedData)localObject4);
            k = ((NativeByteBuffer)localObject4).length();
            if (k % 16 == 0) {
              continue;
            }
            j = 16 - k % 16;
            m = j;
            if (i == 2) {
              m = j + (Utilities.random.nextInt(3) + 2) * 16;
            }
            localObject1 = new org/telegram/tgnet/NativeByteBuffer;
            ((NativeByteBuffer)localObject1).<init>(k + m);
            ((NativeByteBuffer)localObject4).position(0);
            ((NativeByteBuffer)localObject1).writeBytes((NativeByteBuffer)localObject4);
            if (m != 0)
            {
              localObject3 = new byte[m];
              Utilities.random.nextBytes((byte[])localObject3);
              ((NativeByteBuffer)localObject1).writeBytes((byte[])localObject3);
            }
            localObject3 = new byte[16];
            if ((i != 2) || (paramEncryptedChat.admin_id == UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId())) {
              continue;
            }
            bool = true;
            if (i != 2) {
              continue;
            }
            arrayOfByte = paramEncryptedChat.auth_key;
            if (!bool) {
              continue;
            }
            j = 8;
            System.arraycopy(Utilities.computeSHA256(arrayOfByte, j + 88, 32, ((NativeByteBuffer)localObject1).buffer, 0, ((NativeByteBuffer)localObject1).buffer.limit()), 8, localObject3, 0, 16);
            ((NativeByteBuffer)localObject4).reuse();
            localObject4 = MessageKeyData.generateMessageKeyData(paramEncryptedChat.auth_key, (byte[])localObject3, bool, i);
            Utilities.aesIgeEncryption(((NativeByteBuffer)localObject1).buffer, ((MessageKeyData)localObject4).aesKey, ((MessageKeyData)localObject4).aesIv, true, false, 0, ((NativeByteBuffer)localObject1).limit());
            localObject4 = new org/telegram/tgnet/NativeByteBuffer;
            ((NativeByteBuffer)localObject4).<init>(localObject3.length + 8 + ((NativeByteBuffer)localObject1).length());
            ((NativeByteBuffer)localObject1).position(0);
            ((NativeByteBuffer)localObject4).writeInt64(paramEncryptedChat.key_fingerprint);
            ((NativeByteBuffer)localObject4).writeBytes((byte[])localObject3);
            ((NativeByteBuffer)localObject4).writeBytes((NativeByteBuffer)localObject1);
            ((NativeByteBuffer)localObject1).reuse();
            ((NativeByteBuffer)localObject4).position(0);
            if (paramInputEncryptedFile != null) {
              continue;
            }
            if (!(paramDecryptedMessage instanceof TLRPC.TL_decryptedMessageService)) {
              continue;
            }
            localObject1 = new org/telegram/tgnet/TLRPC$TL_messages_sendEncryptedService;
            ((TLRPC.TL_messages_sendEncryptedService)localObject1).<init>();
            ((TLRPC.TL_messages_sendEncryptedService)localObject1).data = ((NativeByteBuffer)localObject4);
            ((TLRPC.TL_messages_sendEncryptedService)localObject1).random_id = paramDecryptedMessage.random_id;
            localObject3 = new org/telegram/tgnet/TLRPC$TL_inputEncryptedChat;
            ((TLRPC.TL_inputEncryptedChat)localObject3).<init>();
            ((TLRPC.TL_messages_sendEncryptedService)localObject1).peer = ((TLRPC.TL_inputEncryptedChat)localObject3);
            ((TLRPC.TL_messages_sendEncryptedService)localObject1).peer.chat_id = paramEncryptedChat.id;
            ((TLRPC.TL_messages_sendEncryptedService)localObject1).peer.access_hash = paramEncryptedChat.access_hash;
            localObject4 = ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount);
            localObject3 = new org/telegram/messenger/SecretChatHelper$4$1;
            ((1)localObject3).<init>(this);
            ((ConnectionsManager)localObject4).sendRequest((TLObject)localObject1, (RequestDelegate)localObject3, 64);
            return;
            i = 1;
            continue;
            paramEncryptedChat.seq_in = -1;
          }
        }
      });
    }
  }
  
  protected void performSendEncryptedRequest(TLRPC.TL_messages_sendEncryptedMultiMedia paramTL_messages_sendEncryptedMultiMedia, SendMessagesHelper.DelayedMessage paramDelayedMessage)
  {
    for (int i = 0; i < paramTL_messages_sendEncryptedMultiMedia.files.size(); i++) {
      performSendEncryptedRequest((TLRPC.DecryptedMessage)paramTL_messages_sendEncryptedMultiMedia.messages.get(i), (TLRPC.Message)paramDelayedMessage.messages.get(i), paramDelayedMessage.encryptedChat, (TLRPC.InputEncryptedFile)paramTL_messages_sendEncryptedMultiMedia.files.get(i), (String)paramDelayedMessage.originalPaths.get(i), (MessageObject)paramDelayedMessage.messageObjects.get(i));
    }
  }
  
  public void processAcceptedSecretChat(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    final Object localObject1 = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
    Object localObject2 = new BigInteger(1, paramEncryptedChat.g_a_or_b);
    if (!Utilities.isGoodGaAndGb((BigInteger)localObject2, (BigInteger)localObject1)) {
      declineSecretChat(paramEncryptedChat.id);
    }
    for (;;)
    {
      return;
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
          break label268;
        }
        paramEncryptedChat.auth_key = ((byte[])localObject1);
        paramEncryptedChat.key_create_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        paramEncryptedChat.seq_in = -2;
        paramEncryptedChat.seq_out = 1;
        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
        MessagesController.getInstance(this.currentAccount).putEncryptedChat(paramEncryptedChat, false);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { paramEncryptedChat });
            SecretChatHelper.this.sendNotifyLayerMessage(paramEncryptedChat, null);
          }
        });
        break;
        localObject1 = localObject2;
        if (localObject2.length < 256)
        {
          localObject1 = new byte['Ā'];
          System.arraycopy(localObject2, 0, localObject1, 256 - localObject2.length, localObject2.length);
          for (int i = 0; i < 256 - localObject2.length; i++) {
            localObject2[i] = ((byte)0);
          }
        }
      }
      label268:
      localObject1 = new TLRPC.TL_encryptedChatDiscarded();
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).id = paramEncryptedChat.id;
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).user_id = paramEncryptedChat.user_id;
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).auth_key = paramEncryptedChat.auth_key;
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).key_create_date = paramEncryptedChat.key_create_date;
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).key_use_count_in = ((short)paramEncryptedChat.key_use_count_in);
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).key_use_count_out = ((short)paramEncryptedChat.key_use_count_out);
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).seq_in = paramEncryptedChat.seq_in;
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).seq_out = paramEncryptedChat.seq_out;
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).admin_id = paramEncryptedChat.admin_id;
      ((TLRPC.TL_encryptedChatDiscarded)localObject1).mtproto_seq = paramEncryptedChat.mtproto_seq;
      MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat((TLRPC.EncryptedChat)localObject1);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(localObject1, false);
          NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { localObject1 });
        }
      });
      declineSecretChat(paramEncryptedChat.id);
    }
  }
  
  public TLRPC.Message processDecryptedObject(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.EncryptedFile paramEncryptedFile, int paramInt, TLObject paramTLObject, boolean paramBoolean)
  {
    Object localObject1;
    label184:
    label452:
    label600:
    label621:
    Object localObject2;
    if (paramTLObject != null)
    {
      int i = paramEncryptedChat.admin_id;
      int j = i;
      if (i == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
        j = paramEncryptedChat.participant_id;
      }
      if ((AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) >= 20) && (paramEncryptedChat.exchange_id == 0L) && (paramEncryptedChat.future_key_fingerprint == 0L) && (paramEncryptedChat.key_use_count_in >= 120)) {
        requestNewSecretChatKey(paramEncryptedChat);
      }
      if ((paramEncryptedChat.exchange_id == 0L) && (paramEncryptedChat.future_key_fingerprint != 0L) && (!paramBoolean))
      {
        paramEncryptedChat.future_auth_key = new byte['Ā'];
        paramEncryptedChat.future_key_fingerprint = 0L;
        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
        if (!(paramTLObject instanceof TLRPC.TL_decryptedMessage)) {
          break label3421;
        }
        localObject1 = (TLRPC.TL_decryptedMessage)paramTLObject;
        if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) < 17) {
          break label600;
        }
        paramTLObject = new TLRPC.TL_message_secret();
        paramTLObject.ttl = ((TLRPC.TL_decryptedMessage)localObject1).ttl;
        paramTLObject.entities = ((TLRPC.TL_decryptedMessage)localObject1).entities;
        paramTLObject.message = ((TLRPC.TL_decryptedMessage)localObject1).message;
        paramTLObject.date = paramInt;
        i = UserConfig.getInstance(this.currentAccount).getNewMessageId();
        paramTLObject.id = i;
        paramTLObject.local_id = i;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        paramTLObject.from_id = j;
        paramTLObject.to_id = new TLRPC.TL_peerUser();
        paramTLObject.random_id = ((TLRPC.TL_decryptedMessage)localObject1).random_id;
        paramTLObject.to_id.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
        paramTLObject.unread = true;
        paramTLObject.flags = 768;
        if ((((TLRPC.TL_decryptedMessage)localObject1).via_bot_name != null) && (((TLRPC.TL_decryptedMessage)localObject1).via_bot_name.length() > 0))
        {
          paramTLObject.via_bot_name = ((TLRPC.TL_decryptedMessage)localObject1).via_bot_name;
          paramTLObject.flags |= 0x800;
        }
        if (((TLRPC.TL_decryptedMessage)localObject1).grouped_id != 0L)
        {
          paramTLObject.grouped_id = ((TLRPC.TL_decryptedMessage)localObject1).grouped_id;
          paramTLObject.flags |= 0x20000;
        }
        paramTLObject.dialog_id = (paramEncryptedChat.id << 32);
        if (((TLRPC.TL_decryptedMessage)localObject1).reply_to_random_id != 0L)
        {
          paramTLObject.reply_to_random_id = ((TLRPC.TL_decryptedMessage)localObject1).reply_to_random_id;
          paramTLObject.flags |= 0x8;
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media != null) && (!(((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaEmpty))) {
          break label621;
        }
        paramTLObject.media = new TLRPC.TL_messageMediaEmpty();
        paramEncryptedChat = paramTLObject;
        if (paramTLObject.ttl != 0)
        {
          paramEncryptedChat = paramTLObject;
          if (paramTLObject.media.ttl_seconds == 0)
          {
            paramTLObject.media.ttl_seconds = paramTLObject.ttl;
            paramEncryptedChat = paramTLObject.media;
            paramEncryptedChat.flags |= 0x4;
            paramEncryptedChat = paramTLObject;
          }
        }
      }
      for (;;)
      {
        return paramEncryptedChat;
        if ((paramEncryptedChat.exchange_id == 0L) || (!paramBoolean)) {
          break;
        }
        paramEncryptedChat.key_fingerprint = paramEncryptedChat.future_key_fingerprint;
        paramEncryptedChat.auth_key = paramEncryptedChat.future_auth_key;
        paramEncryptedChat.key_create_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        paramEncryptedChat.future_auth_key = new byte['Ā'];
        paramEncryptedChat.future_key_fingerprint = 0L;
        paramEncryptedChat.key_use_count_in = ((short)0);
        paramEncryptedChat.key_use_count_out = ((short)0);
        paramEncryptedChat.exchange_id = 0L;
        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
        break;
        paramTLObject = new TLRPC.TL_message();
        paramTLObject.ttl = paramEncryptedChat.ttl;
        break label184;
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaWebPage))
        {
          paramTLObject.media = new TLRPC.TL_messageMediaWebPage();
          paramTLObject.media.webpage = new TLRPC.TL_webPageUrlPending();
          paramTLObject.media.webpage.url = ((TLRPC.TL_decryptedMessage)localObject1).media.url;
          break label452;
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaContact))
        {
          paramTLObject.media = new TLRPC.TL_messageMediaContact();
          paramTLObject.media.last_name = ((TLRPC.TL_decryptedMessage)localObject1).media.last_name;
          paramTLObject.media.first_name = ((TLRPC.TL_decryptedMessage)localObject1).media.first_name;
          paramTLObject.media.phone_number = ((TLRPC.TL_decryptedMessage)localObject1).media.phone_number;
          paramTLObject.media.user_id = ((TLRPC.TL_decryptedMessage)localObject1).media.user_id;
          break label452;
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaGeoPoint))
        {
          paramTLObject.media = new TLRPC.TL_messageMediaGeo();
          paramTLObject.media.geo = new TLRPC.TL_geoPoint();
          paramTLObject.media.geo.lat = ((TLRPC.TL_decryptedMessage)localObject1).media.lat;
          paramTLObject.media.geo._long = ((TLRPC.TL_decryptedMessage)localObject1).media._long;
          break label452;
        }
        if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaPhoto))
        {
          if ((((TLRPC.TL_decryptedMessage)localObject1).media.key == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.key.length != 32) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv.length != 32))
          {
            paramEncryptedChat = null;
          }
          else
          {
            paramTLObject.media = new TLRPC.TL_messageMediaPhoto();
            paramEncryptedChat = paramTLObject.media;
            paramEncryptedChat.flags |= 0x3;
            if (((TLRPC.TL_decryptedMessage)localObject1).media.caption != null) {}
            for (paramEncryptedChat = ((TLRPC.TL_decryptedMessage)localObject1).media.caption;; paramEncryptedChat = "")
            {
              paramTLObject.message = paramEncryptedChat;
              paramTLObject.media.photo = new TLRPC.TL_photo();
              paramTLObject.media.photo.date = paramTLObject.date;
              localObject2 = ((TLRPC.TL_decryptedMessageMediaPhoto)((TLRPC.TL_decryptedMessage)localObject1).media).thumb;
              if ((localObject2 != null) && (localObject2.length != 0) && (localObject2.length <= 6000) && (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_w <= 100) && (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_h <= 100))
              {
                paramEncryptedChat = new TLRPC.TL_photoCachedSize();
                paramEncryptedChat.w = ((TLRPC.TL_decryptedMessage)localObject1).media.thumb_w;
                paramEncryptedChat.h = ((TLRPC.TL_decryptedMessage)localObject1).media.thumb_h;
                paramEncryptedChat.bytes = ((byte[])localObject2);
                paramEncryptedChat.type = "s";
                paramEncryptedChat.location = new TLRPC.TL_fileLocationUnavailable();
                paramTLObject.media.photo.sizes.add(paramEncryptedChat);
              }
              if (paramTLObject.ttl != 0)
              {
                paramTLObject.media.ttl_seconds = paramTLObject.ttl;
                paramEncryptedChat = paramTLObject.media;
                paramEncryptedChat.flags |= 0x4;
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
              break;
            }
          }
        }
        else if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaVideo))
        {
          if ((((TLRPC.TL_decryptedMessage)localObject1).media.key == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.key.length != 32) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv.length != 32))
          {
            paramEncryptedChat = null;
          }
          else
          {
            paramTLObject.media = new TLRPC.TL_messageMediaDocument();
            paramEncryptedChat = paramTLObject.media;
            paramEncryptedChat.flags |= 0x3;
            paramTLObject.media.document = new TLRPC.TL_documentEncrypted();
            paramTLObject.media.document.key = ((TLRPC.TL_decryptedMessage)localObject1).media.key;
            paramTLObject.media.document.iv = ((TLRPC.TL_decryptedMessage)localObject1).media.iv;
            paramTLObject.media.document.dc_id = paramEncryptedFile.dc_id;
            if (((TLRPC.TL_decryptedMessage)localObject1).media.caption != null)
            {
              paramEncryptedChat = ((TLRPC.TL_decryptedMessage)localObject1).media.caption;
              label1509:
              paramTLObject.message = paramEncryptedChat;
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
                break label1932;
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
              paramEncryptedChat.supports_streaming = false;
              paramTLObject.media.document.attributes.add(paramEncryptedChat);
              if (paramTLObject.ttl != 0)
              {
                paramTLObject.media.ttl_seconds = paramTLObject.ttl;
                paramEncryptedChat = paramTLObject.media;
                paramEncryptedChat.flags |= 0x4;
              }
              if (paramTLObject.ttl == 0) {
                break;
              }
              paramTLObject.ttl = Math.max(((TLRPC.TL_decryptedMessage)localObject1).media.duration + 1, paramTLObject.ttl);
              break;
              paramEncryptedChat = "";
              break label1509;
              label1932:
              paramTLObject.media.document.thumb = new TLRPC.TL_photoSizeEmpty();
              paramTLObject.media.document.thumb.type = "s";
            }
          }
        }
        else if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaDocument))
        {
          if ((((TLRPC.TL_decryptedMessage)localObject1).media.key == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.key.length != 32) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv.length != 32))
          {
            paramEncryptedChat = null;
          }
          else
          {
            paramTLObject.media = new TLRPC.TL_messageMediaDocument();
            paramEncryptedChat = paramTLObject.media;
            paramEncryptedChat.flags |= 0x3;
            if (((TLRPC.TL_decryptedMessage)localObject1).media.caption != null)
            {
              paramEncryptedChat = ((TLRPC.TL_decryptedMessage)localObject1).media.caption;
              label2084:
              paramTLObject.message = paramEncryptedChat;
              paramTLObject.media.document = new TLRPC.TL_documentEncrypted();
              paramTLObject.media.document.id = paramEncryptedFile.id;
              paramTLObject.media.document.access_hash = paramEncryptedFile.access_hash;
              paramTLObject.media.document.date = paramInt;
              if (!(((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaDocument_layer8)) {
                break label2537;
              }
              paramEncryptedChat = new TLRPC.TL_documentAttributeFilename();
              paramEncryptedChat.file_name = ((TLRPC.TL_decryptedMessage)localObject1).media.file_name;
              paramTLObject.media.document.attributes.add(paramEncryptedChat);
              label2194:
              paramTLObject.media.document.mime_type = ((TLRPC.TL_decryptedMessage)localObject1).media.mime_type;
              paramEncryptedChat = paramTLObject.media.document;
              if (((TLRPC.TL_decryptedMessage)localObject1).media.size == 0) {
                break label2559;
              }
              paramInt = Math.min(((TLRPC.TL_decryptedMessage)localObject1).media.size, paramEncryptedFile.size);
              label2249:
              paramEncryptedChat.size = paramInt;
              paramTLObject.media.document.key = ((TLRPC.TL_decryptedMessage)localObject1).media.key;
              paramTLObject.media.document.iv = ((TLRPC.TL_decryptedMessage)localObject1).media.iv;
              if (paramTLObject.media.document.mime_type == null) {
                paramTLObject.media.document.mime_type = "";
              }
              paramEncryptedChat = ((TLRPC.TL_decryptedMessageMediaDocument)((TLRPC.TL_decryptedMessage)localObject1).media).thumb;
              if ((paramEncryptedChat == null) || (paramEncryptedChat.length == 0) || (paramEncryptedChat.length > 6000) || (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_w > 100) || (((TLRPC.TL_decryptedMessage)localObject1).media.thumb_h > 100)) {
                break label2567;
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
              if ((!MessageObject.isVoiceMessage(paramTLObject)) && (!MessageObject.isRoundVideoMessage(paramTLObject))) {
                break;
              }
              paramTLObject.media_unread = true;
              break;
              paramEncryptedChat = "";
              break label2084;
              label2537:
              paramTLObject.media.document.attributes = ((TLRPC.TL_decryptedMessage)localObject1).media.attributes;
              break label2194;
              label2559:
              paramInt = paramEncryptedFile.size;
              break label2249;
              label2567:
              paramTLObject.media.document.thumb = new TLRPC.TL_photoSizeEmpty();
              paramTLObject.media.document.thumb.type = "s";
            }
          }
        }
        else
        {
          if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaExternalDocument))
          {
            paramTLObject.media = new TLRPC.TL_messageMediaDocument();
            paramEncryptedChat = paramTLObject.media;
            paramEncryptedChat.flags |= 0x3;
            paramTLObject.message = "";
            paramTLObject.media.document = new TLRPC.TL_document();
            paramTLObject.media.document.id = ((TLRPC.TL_decryptedMessage)localObject1).media.id;
            paramTLObject.media.document.access_hash = ((TLRPC.TL_decryptedMessage)localObject1).media.access_hash;
            paramTLObject.media.document.date = ((TLRPC.TL_decryptedMessage)localObject1).media.date;
            paramTLObject.media.document.attributes = ((TLRPC.TL_decryptedMessage)localObject1).media.attributes;
            paramTLObject.media.document.mime_type = ((TLRPC.TL_decryptedMessage)localObject1).media.mime_type;
            paramTLObject.media.document.dc_id = ((TLRPC.TL_decryptedMessage)localObject1).media.dc_id;
            paramTLObject.media.document.size = ((TLRPC.TL_decryptedMessage)localObject1).media.size;
            paramTLObject.media.document.thumb = ((TLRPC.TL_decryptedMessageMediaExternalDocument)((TLRPC.TL_decryptedMessage)localObject1).media).thumb;
            if (paramTLObject.media.document.mime_type != null) {
              break label452;
            }
            paramTLObject.media.document.mime_type = "";
            break label452;
          }
          if ((((TLRPC.TL_decryptedMessage)localObject1).media instanceof TLRPC.TL_decryptedMessageMediaAudio))
          {
            if ((((TLRPC.TL_decryptedMessage)localObject1).media.key == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.key.length != 32) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv == null) || (((TLRPC.TL_decryptedMessage)localObject1).media.iv.length != 32))
            {
              paramEncryptedChat = null;
            }
            else
            {
              paramTLObject.media = new TLRPC.TL_messageMediaDocument();
              paramEncryptedChat = paramTLObject.media;
              paramEncryptedChat.flags |= 0x3;
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
              if (((TLRPC.TL_decryptedMessage)localObject1).media.caption != null) {}
              for (paramEncryptedChat = ((TLRPC.TL_decryptedMessage)localObject1).media.caption;; paramEncryptedChat = "")
              {
                paramTLObject.message = paramEncryptedChat;
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
                break;
              }
            }
          }
          else
          {
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
              paramTLObject.media.venue_type = "";
              break label452;
            }
            paramEncryptedChat = null;
            continue;
            label3421:
            if (!(paramTLObject instanceof TLRPC.TL_decryptedMessageService)) {
              break label5049;
            }
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
                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatTTL(paramEncryptedChat);
              }
              for (;;)
              {
                i = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                paramEncryptedFile.id = i;
                paramEncryptedFile.local_id = i;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                paramEncryptedFile.unread = true;
                paramEncryptedFile.flags = 256;
                paramEncryptedFile.date = paramInt;
                paramEncryptedFile.from_id = j;
                paramEncryptedFile.to_id = new TLRPC.TL_peerUser();
                paramEncryptedFile.to_id.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                paramEncryptedFile.dialog_id = (paramEncryptedChat.id << 32);
                paramEncryptedChat = paramEncryptedFile;
                break;
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
                  TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs_dict.get(this.val$did);
                  if (localTL_dialog != null)
                  {
                    localTL_dialog.unread_count = 0;
                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogMessage.remove(localTL_dialog.id);
                  }
                  MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          NotificationsController.getInstance(SecretChatHelper.this.currentAccount).processReadMessages(null, SecretChatHelper.6.this.val$did, 0, Integer.MAX_VALUE, false);
                          LongSparseArray localLongSparseArray = new LongSparseArray(1);
                          localLongSparseArray.put(SecretChatHelper.6.this.val$did, Integer.valueOf(0));
                          NotificationsController.getInstance(SecretChatHelper.this.currentAccount).processDialogsUpdateRead(localLongSparseArray);
                        }
                      });
                    }
                  });
                  MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).deleteDialog(this.val$did, 1);
                  NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                  NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, new Object[] { Long.valueOf(this.val$did), Boolean.valueOf(false) });
                }
              });
              paramEncryptedChat = null;
            }
            else
            {
              if (!(((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionDeleteMessages)) {
                break label3780;
              }
              if (!((TLRPC.TL_decryptedMessageService)localObject2).action.random_ids.isEmpty()) {
                this.pendingEncMessagesToDelete.addAll(((TLRPC.TL_decryptedMessageService)localObject2).action.random_ids);
              }
              paramEncryptedChat = null;
            }
          }
        }
      }
      label3780:
      if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionReadMessages)) {
        if (!((TLRPC.TL_decryptedMessageService)localObject2).action.random_ids.isEmpty())
        {
          paramInt = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
          MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(paramEncryptedChat.id, paramInt, paramInt, 1, ((TLRPC.TL_decryptedMessageService)localObject2).action.random_ids);
        }
      }
    }
    for (;;)
    {
      paramEncryptedChat = null;
      break;
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
              if (BuildVars.LOGS_ENABLED) {
                FileLog.d("we already have request key with higher exchange_id");
              }
              paramEncryptedChat = null;
              break;
            }
            sendAbortKeyMessage(paramEncryptedChat, null, paramEncryptedChat.exchange_id);
          }
          byte[] arrayOfByte = new byte['Ā'];
          Utilities.random.nextBytes(arrayOfByte);
          localObject1 = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
          paramEncryptedFile = BigInteger.valueOf(MessagesStorage.getInstance(this.currentAccount).getSecretG()).modPow(new BigInteger(1, arrayOfByte), (BigInteger)localObject1);
          BigInteger localBigInteger = new BigInteger(1, ((TLRPC.TL_decryptedMessageService)localObject2).action.g_a);
          if (!Utilities.isGoodGaAndGb(localBigInteger, (BigInteger)localObject1))
          {
            sendAbortKeyMessage(paramEncryptedChat, null, ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id);
            paramEncryptedChat = null;
            break;
          }
          paramEncryptedFile = paramEncryptedFile.toByteArray();
          paramTLObject = paramEncryptedFile;
          if (paramEncryptedFile.length > 256)
          {
            paramTLObject = new byte['Ā'];
            System.arraycopy(paramEncryptedFile, 1, paramTLObject, 0, 256);
          }
          localObject1 = localBigInteger.modPow(new BigInteger(1, arrayOfByte), (BigInteger)localObject1).toByteArray();
          if (localObject1.length > 256)
          {
            paramEncryptedFile = new byte['Ā'];
            System.arraycopy(localObject1, localObject1.length - 256, paramEncryptedFile, 0, 256);
          }
          for (;;)
          {
            localObject1 = Utilities.computeSHA1(paramEncryptedFile);
            arrayOfByte = new byte[8];
            System.arraycopy(localObject1, localObject1.length - 8, arrayOfByte, 0, 8);
            paramEncryptedChat.exchange_id = ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id;
            paramEncryptedChat.future_auth_key = paramEncryptedFile;
            paramEncryptedChat.future_key_fingerprint = Utilities.bytesToLong(arrayOfByte);
            paramEncryptedChat.g_a_or_b = paramTLObject;
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
            sendAcceptKeyMessage(paramEncryptedChat, null);
            break;
            paramEncryptedFile = (TLRPC.EncryptedFile)localObject1;
            if (localObject1.length < 256)
            {
              paramEncryptedFile = new byte['Ā'];
              System.arraycopy(localObject1, 0, paramEncryptedFile, 256 - localObject1.length, localObject1.length);
              for (paramInt = 0; paramInt < 256 - localObject1.length; paramInt++) {
                localObject1[paramInt] = ((byte)0);
              }
            }
          }
        }
        long l;
        if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionAcceptKey))
        {
          if (paramEncryptedChat.exchange_id == ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id)
          {
            paramTLObject = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
            paramEncryptedFile = new BigInteger(1, ((TLRPC.TL_decryptedMessageService)localObject2).action.g_b);
            if (!Utilities.isGoodGaAndGb(paramEncryptedFile, paramTLObject))
            {
              paramEncryptedChat.future_auth_key = new byte['Ā'];
              paramEncryptedChat.future_key_fingerprint = 0L;
              paramEncryptedChat.exchange_id = 0L;
              MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
              sendAbortKeyMessage(paramEncryptedChat, null, ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id);
              paramEncryptedChat = null;
              break;
            }
            paramTLObject = paramEncryptedFile.modPow(new BigInteger(1, paramEncryptedChat.a_or_b), paramTLObject).toByteArray();
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
              l = Utilities.bytesToLong((byte[])localObject1);
              if (((TLRPC.TL_decryptedMessageService)localObject2).action.key_fingerprint != l) {
                break label4599;
              }
              paramEncryptedChat.future_auth_key = paramEncryptedFile;
              paramEncryptedChat.future_key_fingerprint = l;
              MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
              sendCommitKeyMessage(paramEncryptedChat, null);
              break;
              paramEncryptedFile = paramTLObject;
              if (paramTLObject.length < 256)
              {
                paramEncryptedFile = new byte['Ā'];
                System.arraycopy(paramTLObject, 0, paramEncryptedFile, 256 - paramTLObject.length, paramTLObject.length);
                for (paramInt = 0; paramInt < 256 - paramTLObject.length; paramInt++) {
                  paramTLObject[paramInt] = ((byte)0);
                }
              }
            }
            label4599:
            paramEncryptedChat.future_auth_key = new byte['Ā'];
            paramEncryptedChat.future_key_fingerprint = 0L;
            paramEncryptedChat.exchange_id = 0L;
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
            sendAbortKeyMessage(paramEncryptedChat, null, ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id);
            continue;
          }
          paramEncryptedChat.future_auth_key = new byte['Ā'];
          paramEncryptedChat.future_key_fingerprint = 0L;
          paramEncryptedChat.exchange_id = 0L;
          MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
          sendAbortKeyMessage(paramEncryptedChat, null, ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id);
          continue;
        }
        if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionCommitKey))
        {
          if ((paramEncryptedChat.exchange_id == ((TLRPC.TL_decryptedMessageService)localObject2).action.exchange_id) && (paramEncryptedChat.future_key_fingerprint == ((TLRPC.TL_decryptedMessageService)localObject2).action.key_fingerprint))
          {
            l = paramEncryptedChat.key_fingerprint;
            paramEncryptedFile = paramEncryptedChat.auth_key;
            paramEncryptedChat.key_fingerprint = paramEncryptedChat.future_key_fingerprint;
            paramEncryptedChat.auth_key = paramEncryptedChat.future_auth_key;
            paramEncryptedChat.key_create_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            paramEncryptedChat.future_auth_key = paramEncryptedFile;
            paramEncryptedChat.future_key_fingerprint = l;
            paramEncryptedChat.key_use_count_in = ((short)0);
            paramEncryptedChat.key_use_count_out = ((short)0);
            paramEncryptedChat.exchange_id = 0L;
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
            sendNoopMessage(paramEncryptedChat, null);
          }
          else
          {
            paramEncryptedChat.future_auth_key = new byte['Ā'];
            paramEncryptedChat.future_key_fingerprint = 0L;
            paramEncryptedChat.exchange_id = 0L;
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
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
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
          }
        }
        else if (!(((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionNoop))
        {
          if ((((TLRPC.TL_decryptedMessageService)localObject2).action instanceof TLRPC.TL_decryptedMessageActionResend))
          {
            if ((((TLRPC.TL_decryptedMessageService)localObject2).action.end_seq_no < paramEncryptedChat.in_seq_no) || (((TLRPC.TL_decryptedMessageService)localObject2).action.end_seq_no < ((TLRPC.TL_decryptedMessageService)localObject2).action.start_seq_no))
            {
              paramEncryptedChat = null;
              break;
            }
            if (((TLRPC.TL_decryptedMessageService)localObject2).action.start_seq_no < paramEncryptedChat.in_seq_no) {
              ((TLRPC.TL_decryptedMessageService)localObject2).action.start_seq_no = paramEncryptedChat.in_seq_no;
            }
            resendMessages(((TLRPC.TL_decryptedMessageService)localObject2).action.start_seq_no, ((TLRPC.TL_decryptedMessageService)localObject2).action.end_seq_no, paramEncryptedChat);
            continue;
          }
          paramEncryptedChat = null;
          break;
          label5049:
          if (BuildVars.LOGS_ENABLED)
          {
            FileLog.e("unknown message " + paramTLObject);
            continue;
            if (BuildVars.LOGS_ENABLED) {
              FileLog.e("unknown TLObject");
            }
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
          for (int i = 0; i < this.val$pendingEncMessagesToDeleteCopy.size(); i++)
          {
            MessageObject localMessageObject = (MessageObject)MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogMessagesByRandomIds.get(((Long)this.val$pendingEncMessagesToDeleteCopy.get(i)).longValue());
            if (localMessageObject != null) {
              localMessageObject.deleted = true;
            }
          }
        }
      });
      ArrayList localArrayList = new ArrayList(this.pendingEncMessagesToDelete);
      MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeletedByRandoms(localArrayList);
      this.pendingEncMessagesToDelete.clear();
    }
  }
  
  protected void processUpdateEncryption(TLRPC.TL_updateEncryption paramTL_updateEncryption, final ConcurrentHashMap<Integer, TLRPC.User> paramConcurrentHashMap)
  {
    final TLRPC.EncryptedChat localEncryptedChat = paramTL_updateEncryption.chat;
    long l = localEncryptedChat.id;
    final Object localObject = MessagesController.getInstance(this.currentAccount).getEncryptedChatDB(localEncryptedChat.id, false);
    if (((localEncryptedChat instanceof TLRPC.TL_encryptedChatRequested)) && (localObject == null))
    {
      int i = localEncryptedChat.participant_id;
      int j = i;
      if (i == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
        j = localEncryptedChat.admin_id;
      }
      TLRPC.User localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(j));
      localObject = localUser;
      if (localUser == null) {
        localObject = (TLRPC.User)paramConcurrentHashMap.get(Integer.valueOf(j));
      }
      localEncryptedChat.user_id = j;
      paramConcurrentHashMap = new TLRPC.TL_dialog();
      paramConcurrentHashMap.id = (l << 32);
      paramConcurrentHashMap.unread_count = 0;
      paramConcurrentHashMap.top_message = 0;
      paramConcurrentHashMap.last_message_date = paramTL_updateEncryption.date;
      MessagesController.getInstance(this.currentAccount).putEncryptedChat(localEncryptedChat, false);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs_dict.put(paramConcurrentHashMap.id, paramConcurrentHashMap);
          MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs.add(paramConcurrentHashMap);
          MessagesController.getInstance(SecretChatHelper.this.currentAccount).sortDialogs(null);
          NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
      });
      MessagesStorage.getInstance(this.currentAccount).putEncryptedChat(localEncryptedChat, (TLRPC.User)localObject, paramConcurrentHashMap);
      acceptSecretChat(localEncryptedChat);
    }
    for (;;)
    {
      return;
      if ((localEncryptedChat instanceof TLRPC.TL_encryptedChat))
      {
        if ((localObject != null) && ((localObject instanceof TLRPC.TL_encryptedChatWaiting)) && ((((TLRPC.EncryptedChat)localObject).auth_key == null) || (((TLRPC.EncryptedChat)localObject).auth_key.length == 1)))
        {
          localEncryptedChat.a_or_b = ((TLRPC.EncryptedChat)localObject).a_or_b;
          localEncryptedChat.user_id = ((TLRPC.EncryptedChat)localObject).user_id;
          processAcceptedSecretChat(localEncryptedChat);
        }
        else if ((localObject == null) && (this.startingSecretChat))
        {
          this.delayedEncryptedChatUpdates.add(paramTL_updateEncryption);
        }
      }
      else
      {
        if (localObject != null)
        {
          localEncryptedChat.user_id = ((TLRPC.EncryptedChat)localObject).user_id;
          localEncryptedChat.auth_key = ((TLRPC.EncryptedChat)localObject).auth_key;
          localEncryptedChat.key_create_date = ((TLRPC.EncryptedChat)localObject).key_create_date;
          localEncryptedChat.key_use_count_in = ((short)((TLRPC.EncryptedChat)localObject).key_use_count_in);
          localEncryptedChat.key_use_count_out = ((short)((TLRPC.EncryptedChat)localObject).key_use_count_out);
          localEncryptedChat.ttl = ((TLRPC.EncryptedChat)localObject).ttl;
          localEncryptedChat.seq_in = ((TLRPC.EncryptedChat)localObject).seq_in;
          localEncryptedChat.seq_out = ((TLRPC.EncryptedChat)localObject).seq_out;
          localEncryptedChat.admin_id = ((TLRPC.EncryptedChat)localObject).admin_id;
          localEncryptedChat.mtproto_seq = ((TLRPC.EncryptedChat)localObject).mtproto_seq;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (localObject != null) {
              MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(localEncryptedChat, false);
            }
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(localEncryptedChat);
            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, new Object[] { localEncryptedChat });
          }
        });
      }
    }
  }
  
  public void requestNewSecretChatKey(TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (AndroidUtilities.getPeerLayerVersion(paramEncryptedChat.layer) < 20) {}
    for (;;)
    {
      return;
      byte[] arrayOfByte1 = new byte['Ā'];
      Utilities.random.nextBytes(arrayOfByte1);
      byte[] arrayOfByte2 = BigInteger.valueOf(MessagesStorage.getInstance(this.currentAccount).getSecretG()).modPow(new BigInteger(1, arrayOfByte1), new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes())).toByteArray();
      byte[] arrayOfByte3 = arrayOfByte2;
      if (arrayOfByte2.length > 256)
      {
        arrayOfByte3 = new byte['Ā'];
        System.arraycopy(arrayOfByte2, 1, arrayOfByte3, 0, 256);
      }
      paramEncryptedChat.exchange_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
      paramEncryptedChat.a_or_b = arrayOfByte1;
      paramEncryptedChat.g_a = arrayOfByte3;
      MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(paramEncryptedChat);
      sendRequestKeyMessage(paramEncryptedChat, null);
    }
  }
  
  public void sendAbortKeyMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage, long paramLong)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionAbortKey();
      localTL_decryptedMessageService.action.exchange_id = paramLong;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
    }
  }
  
  public void sendAcceptKeyMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionAcceptKey();
      localTL_decryptedMessageService.action.exchange_id = paramEncryptedChat.exchange_id;
      localTL_decryptedMessageService.action.key_fingerprint = paramEncryptedChat.future_key_fingerprint;
      localTL_decryptedMessageService.action.g_b = paramEncryptedChat.g_a_or_b;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
    }
  }
  
  public void sendClearHistoryMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionFlushHistory();
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
    }
  }
  
  public void sendCommitKeyMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionCommitKey();
      localTL_decryptedMessageService.action.exchange_id = paramEncryptedChat.exchange_id;
      localTL_decryptedMessageService.action.key_fingerprint = paramEncryptedChat.future_key_fingerprint;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
    }
  }
  
  public void sendMessagesDeleteMessage(TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionDeleteMessages();
      localTL_decryptedMessageService.action.random_ids = paramArrayList;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
    }
  }
  
  public void sendMessagesReadMessage(TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionReadMessages();
      localTL_decryptedMessageService.action.random_ids = paramArrayList;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
    }
  }
  
  public void sendNoopMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionNoop();
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
    }
  }
  
  public void sendNotifyLayerMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService;
    if (!this.sendingNotifyLayer.contains(Integer.valueOf(paramEncryptedChat.id)))
    {
      this.sendingNotifyLayer.add(Integer.valueOf(paramEncryptedChat.id));
      localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
      if (paramMessage == null) {
        break label84;
      }
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      break;
      label84:
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionNotifyLayer();
      localTL_decryptedMessageService.action.layer = 73;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
    }
  }
  
  public void sendRequestKeyMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionRequestKey();
      localTL_decryptedMessageService.action.exchange_id = paramEncryptedChat.exchange_id;
      localTL_decryptedMessageService.action.g_a = paramEncryptedChat.g_a;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
    }
  }
  
  public void sendScreenshotMessage(TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionScreenshotMessages();
      localTL_decryptedMessageService.action.random_ids = paramArrayList;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
      MessageObject localMessageObject = new MessageObject(this.currentAccount, paramMessage, false);
      localMessageObject.messageOwner.send_state = 1;
      paramArrayList = new ArrayList();
      paramArrayList.add(localMessageObject);
      MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(paramMessage.dialog_id, paramArrayList);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }
  }
  
  public void sendTTLMessage(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.Message paramMessage)
  {
    if (!(paramEncryptedChat instanceof TLRPC.TL_encryptedChat)) {
      return;
    }
    TLRPC.TL_decryptedMessageService localTL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
    if (paramMessage != null) {
      localTL_decryptedMessageService.action = paramMessage.action.encryptedAction;
    }
    for (;;)
    {
      localTL_decryptedMessageService.random_id = paramMessage.random_id;
      performSendEncryptedRequest(localTL_decryptedMessageService, paramMessage, paramEncryptedChat, null, null, null);
      break;
      localTL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionSetMessageTTL();
      localTL_decryptedMessageService.action.ttl_seconds = paramEncryptedChat.ttl;
      paramMessage = createServiceSecretMessage(paramEncryptedChat, localTL_decryptedMessageService.action);
      MessageObject localMessageObject = new MessageObject(this.currentAccount, paramMessage, false);
      localMessageObject.messageOwner.send_state = 1;
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(localMessageObject);
      MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(paramMessage.dialog_id, localArrayList);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }
  }
  
  public void startSecretChat(final Context paramContext, final TLRPC.User paramUser)
  {
    if ((paramUser == null) || (paramContext == null)) {}
    for (;;)
    {
      return;
      this.startingSecretChat = true;
      final AlertDialog localAlertDialog = new AlertDialog(paramContext, 1);
      localAlertDialog.setMessage(LocaleController.getString("Loading", NUM));
      localAlertDialog.setCanceledOnTouchOutside(false);
      localAlertDialog.setCancelable(false);
      TLRPC.TL_messages_getDhConfig localTL_messages_getDhConfig = new TLRPC.TL_messages_getDhConfig();
      localTL_messages_getDhConfig.random_length = 256;
      localTL_messages_getDhConfig.version = MessagesStorage.getInstance(this.currentAccount).getLastSecretVersion();
      final int i = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getDhConfig, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTL_error = (TLRPC.messages_DhConfig)paramAnonymousTLObject;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_dhConfig)) {
              if (!Utilities.isGoodPrime(paramAnonymousTL_error.p, paramAnonymousTL_error.g)) {
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
                      for (;;)
                      {
                        FileLog.e(localException);
                      }
                    }
                  }
                });
              }
            }
          }
          for (;;)
          {
            return;
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretPBytes(paramAnonymousTL_error.p);
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretG(paramAnonymousTL_error.g);
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setLastSecretVersion(paramAnonymousTL_error.version);
            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).saveSecretParams(MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getLastSecretVersion(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
            final byte[] arrayOfByte = new byte['Ā'];
            for (int i = 0; i < 256; i++) {
              arrayOfByte[i] = ((byte)(byte)((byte)(int)(Utilities.random.nextDouble() * 256.0D) ^ paramAnonymousTL_error.random[i]));
            }
            paramAnonymousTL_error = BigInteger.valueOf(MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG()).modPow(new BigInteger(1, arrayOfByte), new BigInteger(1, MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes())).toByteArray();
            paramAnonymousTLObject = paramAnonymousTL_error;
            if (paramAnonymousTL_error.length > 256)
            {
              paramAnonymousTLObject = new byte['Ā'];
              System.arraycopy(paramAnonymousTL_error, 1, paramAnonymousTLObject, 0, 256);
            }
            paramAnonymousTL_error = new TLRPC.TL_messages_requestEncryption();
            paramAnonymousTL_error.g_a = paramAnonymousTLObject;
            paramAnonymousTL_error.user_id = MessagesController.getInstance(SecretChatHelper.this.currentAccount).getInputUser(paramUser);
            paramAnonymousTL_error.random_id = Utilities.random.nextInt();
            ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).sendRequest(paramAnonymousTL_error, new RequestDelegate()
            {
              public void run(final TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
              {
                if (paramAnonymous2TL_error == null) {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      SecretChatHelper.access$502(SecretChatHelper.this, false);
                      if (!((Activity)SecretChatHelper.14.this.val$context).isFinishing()) {}
                      try
                      {
                        SecretChatHelper.14.this.val$progressDialog.dismiss();
                        TLRPC.EncryptedChat localEncryptedChat = (TLRPC.EncryptedChat)paramAnonymous2TLObject;
                        localEncryptedChat.user_id = localEncryptedChat.participant_id;
                        localEncryptedChat.seq_in = -2;
                        localEncryptedChat.seq_out = 1;
                        localEncryptedChat.a_or_b = SecretChatHelper.14.2.this.val$salt;
                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(localEncryptedChat, false);
                        TLRPC.TL_dialog localTL_dialog = new TLRPC.TL_dialog();
                        localTL_dialog.id = (localEncryptedChat.id << 32);
                        localTL_dialog.unread_count = 0;
                        localTL_dialog.top_message = 0;
                        localTL_dialog.last_message_date = ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime();
                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs_dict.put(localTL_dialog.id, localTL_dialog);
                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs.add(localTL_dialog);
                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).sortDialogs(null);
                        MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(localEncryptedChat, SecretChatHelper.14.this.val$user, localTL_dialog);
                        NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatCreated, new Object[] { localEncryptedChat });
                        Utilities.stageQueue.postRunnable(new Runnable()
                        {
                          public void run()
                          {
                            if (!SecretChatHelper.this.delayedEncryptedChatUpdates.isEmpty())
                            {
                              MessagesController.getInstance(SecretChatHelper.this.currentAccount).processUpdateArray(SecretChatHelper.this.delayedEncryptedChatUpdates, null, null, false);
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
                          FileLog.e(localException);
                        }
                      }
                    }
                  });
                }
                for (;;)
                {
                  return;
                  SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      if (!((Activity)SecretChatHelper.14.this.val$context).isFinishing()) {
                        SecretChatHelper.access$502(SecretChatHelper.this, false);
                      }
                      try
                      {
                        SecretChatHelper.14.this.val$progressDialog.dismiss();
                        AlertDialog.Builder localBuilder = new AlertDialog.Builder(SecretChatHelper.14.this.val$context);
                        localBuilder.setTitle(LocaleController.getString("AppName", NUM));
                        localBuilder.setMessage(LocaleController.getString("CreateEncryptedChatError", NUM));
                        localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                        localBuilder.show().setCanceledOnTouchOutside(true);
                        return;
                      }
                      catch (Exception localException)
                      {
                        for (;;)
                        {
                          FileLog.e(localException);
                        }
                      }
                    }
                  });
                }
              }
            }, 2);
            continue;
            SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                SecretChatHelper.access$502(SecretChatHelper.this, false);
                if (!((Activity)SecretChatHelper.14.this.val$context).isFinishing()) {}
                try
                {
                  SecretChatHelper.14.this.val$progressDialog.dismiss();
                  return;
                }
                catch (Exception localException)
                {
                  for (;;)
                  {
                    FileLog.e(localException);
                  }
                }
              }
            });
          }
        }
      }, 2);
      localAlertDialog.setButton(-2, LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).cancelRequest(i, true);
          try
          {
            paramAnonymousDialogInterface.dismiss();
            return;
          }
          catch (Exception paramAnonymousDialogInterface)
          {
            for (;;)
            {
              FileLog.e(paramAnonymousDialogInterface);
            }
          }
        }
      });
      try
      {
        localAlertDialog.show();
      }
      catch (Exception paramContext) {}
    }
  }
  
  public static class TL_decryptedMessageHolder
    extends TLObject
  {
    public static int constructor = NUM;
    public int date;
    public int decryptedWithVersion;
    public TLRPC.EncryptedFile file;
    public TLRPC.TL_decryptedMessageLayer layer;
    public boolean new_key_used;
    
    public void readParams(AbstractSerializedData paramAbstractSerializedData, boolean paramBoolean)
    {
      paramAbstractSerializedData.readInt64(paramBoolean);
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
      paramAbstractSerializedData.writeInt64(0L);
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