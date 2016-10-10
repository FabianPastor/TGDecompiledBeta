package org.telegram.messenger;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineMessage;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_botInlineMediaResult;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaContact;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageText;
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
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionTyping;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio_old;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateShortSentMessage;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;

public class SendMessagesHelper
  implements NotificationCenter.NotificationCenterDelegate
{
  private static volatile SendMessagesHelper Instance = null;
  private TLRPC.ChatFull currentChatInfo = null;
  private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap();
  private LocationProvider locationProvider = new LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate()
  {
    public void onLocationAcquired(Location paramAnonymousLocation)
    {
      SendMessagesHelper.this.sendLocation(paramAnonymousLocation);
      SendMessagesHelper.this.waitingForLocation.clear();
    }
    
    public void onUnableLocationAcquire()
    {
      HashMap localHashMap = new HashMap(SendMessagesHelper.this.waitingForLocation);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, new Object[] { localHashMap });
      SendMessagesHelper.this.waitingForLocation.clear();
    }
  });
  private HashMap<Integer, TLRPC.Message> sendingMessages = new HashMap();
  private HashMap<Integer, MessageObject> unsentMessages = new HashMap();
  private HashMap<String, MessageObject> waitingForCallback = new HashMap();
  private HashMap<String, MessageObject> waitingForLocation = new HashMap();
  
  public SendMessagesHelper()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingStarted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
  }
  
  /* Error */
  private static void fillVideoAttribute(String paramString, TLRPC.TL_documentAttributeVideo paramTL_documentAttributeVideo, VideoEditedInfo paramVideoEditedInfo)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aconst_null
    //   7: astore 7
    //   9: new 235	android/media/MediaMetadataRetriever
    //   12: dup
    //   13: invokespecial 236	android/media/MediaMetadataRetriever:<init>	()V
    //   16: astore 6
    //   18: aload 6
    //   20: aload_0
    //   21: invokevirtual 240	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   24: aload 6
    //   26: bipush 18
    //   28: invokevirtual 244	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   31: astore 5
    //   33: aload 5
    //   35: ifnull +12 -> 47
    //   38: aload_1
    //   39: aload 5
    //   41: invokestatic 250	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   44: putfield 255	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   47: aload 6
    //   49: bipush 19
    //   51: invokevirtual 244	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   54: astore 5
    //   56: aload 5
    //   58: ifnull +12 -> 70
    //   61: aload_1
    //   62: aload 5
    //   64: invokestatic 250	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   67: putfield 258	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   70: aload 6
    //   72: bipush 9
    //   74: invokevirtual 244	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   77: astore 5
    //   79: aload 5
    //   81: ifnull +22 -> 103
    //   84: aload_1
    //   85: aload 5
    //   87: invokestatic 264	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   90: l2f
    //   91: ldc_w 265
    //   94: fdiv
    //   95: f2d
    //   96: invokestatic 271	java/lang/Math:ceil	(D)D
    //   99: d2i
    //   100: putfield 274	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
    //   103: getstatic 279	android/os/Build$VERSION:SDK_INT	I
    //   106: bipush 17
    //   108: if_icmplt +35 -> 143
    //   111: aload 6
    //   113: bipush 24
    //   115: invokevirtual 244	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   118: astore 5
    //   120: aload 5
    //   122: ifnull +21 -> 143
    //   125: aload 5
    //   127: invokestatic 284	org/telegram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   130: invokevirtual 288	java/lang/Integer:intValue	()I
    //   133: istore_3
    //   134: aload_2
    //   135: ifnull +85 -> 220
    //   138: aload_2
    //   139: iload_3
    //   140: putfield 293	org/telegram/messenger/VideoEditedInfo:rotationValue	I
    //   143: iconst_1
    //   144: istore_3
    //   145: aload 6
    //   147: ifnull +8 -> 155
    //   150: aload 6
    //   152: invokevirtual 296	android/media/MediaMetadataRetriever:release	()V
    //   155: iload_3
    //   156: ifne +63 -> 219
    //   159: getstatic 302	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   162: new 304	java/io/File
    //   165: dup
    //   166: aload_0
    //   167: invokespecial 306	java/io/File:<init>	(Ljava/lang/String;)V
    //   170: invokestatic 312	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   173: invokestatic 318	android/media/MediaPlayer:create	(Landroid/content/Context;Landroid/net/Uri;)Landroid/media/MediaPlayer;
    //   176: astore_0
    //   177: aload_0
    //   178: ifnull +41 -> 219
    //   181: aload_1
    //   182: aload_0
    //   183: invokevirtual 321	android/media/MediaPlayer:getDuration	()I
    //   186: i2f
    //   187: ldc_w 265
    //   190: fdiv
    //   191: f2d
    //   192: invokestatic 271	java/lang/Math:ceil	(D)D
    //   195: d2i
    //   196: putfield 274	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
    //   199: aload_1
    //   200: aload_0
    //   201: invokevirtual 324	android/media/MediaPlayer:getVideoWidth	()I
    //   204: putfield 255	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   207: aload_1
    //   208: aload_0
    //   209: invokevirtual 327	android/media/MediaPlayer:getVideoHeight	()I
    //   212: putfield 258	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   215: aload_0
    //   216: invokevirtual 328	android/media/MediaPlayer:release	()V
    //   219: return
    //   220: iload_3
    //   221: bipush 90
    //   223: if_icmpeq +10 -> 233
    //   226: iload_3
    //   227: sipush 270
    //   230: if_icmpne -87 -> 143
    //   233: aload_1
    //   234: getfield 255	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   237: istore_3
    //   238: aload_1
    //   239: aload_1
    //   240: getfield 258	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   243: putfield 255	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   246: aload_1
    //   247: iload_3
    //   248: putfield 258	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   251: goto -108 -> 143
    //   254: astore 5
    //   256: aload 6
    //   258: astore_2
    //   259: aload 5
    //   261: astore 6
    //   263: aload_2
    //   264: astore 5
    //   266: ldc_w 330
    //   269: aload 6
    //   271: invokestatic 336	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   274: iload 4
    //   276: istore_3
    //   277: aload_2
    //   278: ifnull -123 -> 155
    //   281: aload_2
    //   282: invokevirtual 296	android/media/MediaMetadataRetriever:release	()V
    //   285: iload 4
    //   287: istore_3
    //   288: goto -133 -> 155
    //   291: astore_2
    //   292: ldc_w 330
    //   295: aload_2
    //   296: invokestatic 336	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   299: iload 4
    //   301: istore_3
    //   302: goto -147 -> 155
    //   305: astore_2
    //   306: ldc_w 330
    //   309: aload_2
    //   310: invokestatic 336	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   313: goto -158 -> 155
    //   316: astore_0
    //   317: aload 5
    //   319: ifnull +8 -> 327
    //   322: aload 5
    //   324: invokevirtual 296	android/media/MediaMetadataRetriever:release	()V
    //   327: aload_0
    //   328: athrow
    //   329: astore_1
    //   330: ldc_w 330
    //   333: aload_1
    //   334: invokestatic 336	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   337: goto -10 -> 327
    //   340: astore_0
    //   341: ldc_w 330
    //   344: aload_0
    //   345: invokestatic 336	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   348: return
    //   349: astore_0
    //   350: aload 6
    //   352: astore 5
    //   354: goto -37 -> 317
    //   357: astore 6
    //   359: aload 7
    //   361: astore_2
    //   362: goto -99 -> 263
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	365	0	paramString	String
    //   0	365	1	paramTL_documentAttributeVideo	TLRPC.TL_documentAttributeVideo
    //   0	365	2	paramVideoEditedInfo	VideoEditedInfo
    //   133	169	3	i	int
    //   1	299	4	j	int
    //   4	122	5	str	String
    //   254	6	5	localException1	Exception
    //   264	89	5	localObject1	Object
    //   16	335	6	localObject2	Object
    //   357	1	6	localException2	Exception
    //   7	353	7	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   18	33	254	java/lang/Exception
    //   38	47	254	java/lang/Exception
    //   47	56	254	java/lang/Exception
    //   61	70	254	java/lang/Exception
    //   70	79	254	java/lang/Exception
    //   84	103	254	java/lang/Exception
    //   103	120	254	java/lang/Exception
    //   125	134	254	java/lang/Exception
    //   138	143	254	java/lang/Exception
    //   233	251	254	java/lang/Exception
    //   281	285	291	java/lang/Exception
    //   150	155	305	java/lang/Exception
    //   9	18	316	finally
    //   266	274	316	finally
    //   322	327	329	java/lang/Exception
    //   159	177	340	java/lang/Exception
    //   181	219	340	java/lang/Exception
    //   18	33	349	finally
    //   38	47	349	finally
    //   47	56	349	finally
    //   61	70	349	finally
    //   70	79	349	finally
    //   84	103	349	finally
    //   103	120	349	finally
    //   125	134	349	finally
    //   138	143	349	finally
    //   233	251	349	finally
    //   9	18	357	java/lang/Exception
  }
  
  public static SendMessagesHelper getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          SendMessagesHelper localSendMessagesHelper2 = Instance;
          localObject1 = localSendMessagesHelper2;
          if (localSendMessagesHelper2 == null) {
            localObject1 = new SendMessagesHelper();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (SendMessagesHelper)localObject1;
          return (SendMessagesHelper)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localSendMessagesHelper1;
  }
  
  private static String getTrimmedString(String paramString)
  {
    String str = paramString.trim();
    if (str.length() == 0) {
      return str;
    }
    for (;;)
    {
      str = paramString;
      if (!paramString.startsWith("\n")) {
        break;
      }
      paramString = paramString.substring(1);
    }
    while (str.endsWith("\n")) {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }
  
  private void performSendDelayedMessage(DelayedMessage paramDelayedMessage)
  {
    if (paramDelayedMessage.type == 0) {
      if (paramDelayedMessage.httpLocation != null)
      {
        putToDelayedMessages(paramDelayedMessage.httpLocation, paramDelayedMessage);
        ImageLoader.getInstance().loadHttpFile(paramDelayedMessage.httpLocation, "file");
      }
    }
    label882:
    do
    {
      do
      {
        return;
        if (paramDelayedMessage.sendRequest != null)
        {
          localObject = FileLoader.getPathToAttach(paramDelayedMessage.location).toString();
          putToDelayedMessages((String)localObject, paramDelayedMessage);
          FileLoader.getInstance().uploadFile((String)localObject, false, true);
          return;
        }
        localObject = FileLoader.getPathToAttach(paramDelayedMessage.location).toString();
        if ((paramDelayedMessage.sendEncryptedRequest != null) && (paramDelayedMessage.location.dc_id != 0) && (!new File((String)localObject).exists()))
        {
          putToDelayedMessages(FileLoader.getAttachFileName(paramDelayedMessage.location), paramDelayedMessage);
          FileLoader.getInstance().loadFile(paramDelayedMessage.location, "jpg", 0, false);
          return;
        }
        putToDelayedMessages((String)localObject, paramDelayedMessage);
        FileLoader.getInstance().uploadFile((String)localObject, true, true);
        return;
        if (paramDelayedMessage.type == 1)
        {
          if (paramDelayedMessage.videoEditedInfo != null)
          {
            str = paramDelayedMessage.obj.messageOwner.attachPath;
            localObject = str;
            if (str == null) {
              localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.documentLocation.id + ".mp4";
            }
            putToDelayedMessages((String)localObject, paramDelayedMessage);
            MediaController.getInstance().scheduleVideoConvert(paramDelayedMessage.obj);
            return;
          }
          if (paramDelayedMessage.sendRequest != null)
          {
            if ((paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia)) {
              localObject = ((TLRPC.TL_messages_sendMedia)paramDelayedMessage.sendRequest).media;
            }
            while (((TLRPC.InputMedia)localObject).file == null)
            {
              str = paramDelayedMessage.obj.messageOwner.attachPath;
              localObject = str;
              if (str == null) {
                localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.documentLocation.id + ".mp4";
              }
              putToDelayedMessages((String)localObject, paramDelayedMessage);
              if (paramDelayedMessage.obj.videoEditedInfo != null)
              {
                FileLoader.getInstance().uploadFile((String)localObject, false, false, paramDelayedMessage.documentLocation.size);
                return;
                localObject = ((TLRPC.TL_messages_sendBroadcast)paramDelayedMessage.sendRequest).media;
              }
              else
              {
                FileLoader.getInstance().uploadFile((String)localObject, false, false);
                return;
              }
            }
            localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
            putToDelayedMessages((String)localObject, paramDelayedMessage);
            FileLoader.getInstance().uploadFile((String)localObject, false, true);
            return;
          }
          String str = paramDelayedMessage.obj.messageOwner.attachPath;
          localObject = str;
          if (str == null) {
            localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.documentLocation.id + ".mp4";
          }
          putToDelayedMessages((String)localObject, paramDelayedMessage);
          if (paramDelayedMessage.obj.videoEditedInfo != null)
          {
            FileLoader.getInstance().uploadFile((String)localObject, true, false, paramDelayedMessage.documentLocation.size);
            return;
          }
          FileLoader.getInstance().uploadFile((String)localObject, true, false);
          return;
        }
        if (paramDelayedMessage.type != 2) {
          break label882;
        }
        if (paramDelayedMessage.httpLocation != null)
        {
          putToDelayedMessages(paramDelayedMessage.httpLocation, paramDelayedMessage);
          ImageLoader.getInstance().loadHttpFile(paramDelayedMessage.httpLocation, "gif");
          return;
        }
        if (paramDelayedMessage.sendRequest == null) {
          break;
        }
        if ((paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia)) {
          localObject = ((TLRPC.TL_messages_sendMedia)paramDelayedMessage.sendRequest).media;
        }
        while (((TLRPC.InputMedia)localObject).file == null)
        {
          localObject = paramDelayedMessage.obj.messageOwner.attachPath;
          putToDelayedMessages((String)localObject, paramDelayedMessage);
          if (paramDelayedMessage.sendRequest != null)
          {
            FileLoader.getInstance().uploadFile((String)localObject, false, false);
            return;
            localObject = ((TLRPC.TL_messages_sendBroadcast)paramDelayedMessage.sendRequest).media;
          }
          else
          {
            FileLoader.getInstance().uploadFile((String)localObject, true, false);
            return;
          }
        }
      } while ((((TLRPC.InputMedia)localObject).thumb != null) || (paramDelayedMessage.location == null));
      localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
      putToDelayedMessages((String)localObject, paramDelayedMessage);
      FileLoader.getInstance().uploadFile((String)localObject, false, true);
      return;
      localObject = paramDelayedMessage.obj.messageOwner.attachPath;
      if ((paramDelayedMessage.sendEncryptedRequest != null) && (paramDelayedMessage.documentLocation.dc_id != 0) && (!new File((String)localObject).exists()))
      {
        putToDelayedMessages(FileLoader.getAttachFileName(paramDelayedMessage.documentLocation), paramDelayedMessage);
        FileLoader.getInstance().loadFile(paramDelayedMessage.documentLocation, true, false);
        return;
      }
      putToDelayedMessages((String)localObject, paramDelayedMessage);
      FileLoader.getInstance().uploadFile((String)localObject, true, false);
      return;
    } while (paramDelayedMessage.type != 3);
    Object localObject = paramDelayedMessage.obj.messageOwner.attachPath;
    putToDelayedMessages((String)localObject, paramDelayedMessage);
    if (paramDelayedMessage.sendRequest != null)
    {
      FileLoader.getInstance().uploadFile((String)localObject, false, true);
      return;
    }
    FileLoader.getInstance().uploadFile((String)localObject, true, true);
  }
  
  private void performSendMessageRequest(final TLObject paramTLObject, final MessageObject paramMessageObject, final String paramString)
  {
    final TLRPC.Message localMessage = paramMessageObject.messageOwner;
    putToSendingMessages(localMessage);
    ConnectionsManager localConnectionsManager = ConnectionsManager.getInstance();
    paramMessageObject = new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int j = 0;
            final ArrayList localArrayList;
            final Object localObject1;
            final Object localObject2;
            Object localObject3;
            int i;
            if (paramAnonymousTL_error == null)
            {
              final int k = SendMessagesHelper.9.this.val$newMsgObj.id;
              final boolean bool2 = SendMessagesHelper.9.this.val$req instanceof TLRPC.TL_messages_sendBroadcast;
              localArrayList = new ArrayList();
              final String str = SendMessagesHelper.9.this.val$newMsgObj.attachPath;
              if ((paramAnonymousTLObject instanceof TLRPC.TL_updateShortSentMessage))
              {
                localObject1 = (TLRPC.TL_updateShortSentMessage)paramAnonymousTLObject;
                localObject2 = SendMessagesHelper.9.this.val$newMsgObj;
                localObject3 = SendMessagesHelper.9.this.val$newMsgObj;
                i = ((TLRPC.TL_updateShortSentMessage)localObject1).id;
                ((TLRPC.Message)localObject3).id = i;
                ((TLRPC.Message)localObject2).local_id = i;
                SendMessagesHelper.9.this.val$newMsgObj.date = ((TLRPC.TL_updateShortSentMessage)localObject1).date;
                SendMessagesHelper.9.this.val$newMsgObj.entities = ((TLRPC.TL_updateShortSentMessage)localObject1).entities;
                SendMessagesHelper.9.this.val$newMsgObj.out = ((TLRPC.TL_updateShortSentMessage)localObject1).out;
                if (((TLRPC.TL_updateShortSentMessage)localObject1).media != null)
                {
                  SendMessagesHelper.9.this.val$newMsgObj.media = ((TLRPC.TL_updateShortSentMessage)localObject1).media;
                  localObject2 = SendMessagesHelper.9.this.val$newMsgObj;
                  ((TLRPC.Message)localObject2).flags |= 0x200;
                }
                if (((((TLRPC.TL_updateShortSentMessage)localObject1).media instanceof TLRPC.TL_messageMediaGame)) && (!TextUtils.isEmpty(((TLRPC.TL_updateShortSentMessage)localObject1).message))) {
                  SendMessagesHelper.9.this.val$newMsgObj.message = ((TLRPC.TL_updateShortSentMessage)localObject1).message;
                }
                if (!SendMessagesHelper.9.this.val$newMsgObj.entities.isEmpty())
                {
                  localObject2 = SendMessagesHelper.9.this.val$newMsgObj;
                  ((TLRPC.Message)localObject2).flags |= 0x80;
                }
                Utilities.stageQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.getInstance().processNewDifferenceParams(-1, localObject1.pts, localObject1.date, localObject1.pts_count);
                  }
                });
                localArrayList.add(SendMessagesHelper.9.this.val$newMsgObj);
                i = j;
                j = i;
                if (i == 0)
                {
                  SendMessagesHelper.9.this.val$newMsgObj.send_state = 0;
                  localObject1 = NotificationCenter.getInstance();
                  int m = NotificationCenter.messageReceivedByServer;
                  if (!bool2) {
                    break label930;
                  }
                  j = k;
                  label340:
                  ((NotificationCenter)localObject1).postNotificationName(m, new Object[] { Integer.valueOf(k), Integer.valueOf(j), SendMessagesHelper.9.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.9.this.val$newMsgObj.dialog_id) });
                  MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      Object localObject = MessagesStorage.getInstance();
                      long l = SendMessagesHelper.9.this.val$newMsgObj.random_id;
                      int j = k;
                      if (bool2) {}
                      for (int i = k;; i = SendMessagesHelper.9.this.val$newMsgObj.id)
                      {
                        ((MessagesStorage)localObject).updateMessageStateAndId(l, Integer.valueOf(j), i, 0, false, SendMessagesHelper.9.this.val$newMsgObj.to_id.channel_id);
                        MessagesStorage.getInstance().putMessages(localArrayList, true, false, bool2, 0);
                        if (bool2)
                        {
                          localObject = new ArrayList();
                          ((ArrayList)localObject).add(SendMessagesHelper.9.this.val$newMsgObj);
                          MessagesStorage.getInstance().putMessages((ArrayList)localObject, true, false, false, 0);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            if (SendMessagesHelper.9.1.4.this.val$isBroadcast)
                            {
                              i = 0;
                              while (i < SendMessagesHelper.9.1.4.this.val$sentMessages.size())
                              {
                                Object localObject2 = (TLRPC.Message)SendMessagesHelper.9.1.4.this.val$sentMessages.get(i);
                                localObject1 = new ArrayList();
                                localObject2 = new MessageObject((TLRPC.Message)localObject2, null, false);
                                ((ArrayList)localObject1).add(localObject2);
                                MessagesController.getInstance().updateInterfaceWithMessages(((MessageObject)localObject2).getDialogId(), (ArrayList)localObject1, true);
                                i += 1;
                              }
                              NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                            SearchQuery.increasePeerRaiting(SendMessagesHelper.9.this.val$newMsgObj.dialog_id);
                            Object localObject1 = NotificationCenter.getInstance();
                            int j = NotificationCenter.messageReceivedByServer;
                            int k = SendMessagesHelper.9.1.4.this.val$oldId;
                            if (SendMessagesHelper.9.1.4.this.val$isBroadcast) {}
                            for (int i = SendMessagesHelper.9.1.4.this.val$oldId;; i = SendMessagesHelper.9.this.val$newMsgObj.id)
                            {
                              ((NotificationCenter)localObject1).postNotificationName(j, new Object[] { Integer.valueOf(k), Integer.valueOf(i), SendMessagesHelper.9.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.9.this.val$newMsgObj.dialog_id) });
                              SendMessagesHelper.this.processSentMessage(SendMessagesHelper.9.1.4.this.val$oldId);
                              SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.9.1.4.this.val$oldId);
                              return;
                            }
                          }
                        });
                        if ((MessageObject.isVideoMessage(SendMessagesHelper.9.this.val$newMsgObj)) || (MessageObject.isNewGifMessage(SendMessagesHelper.9.this.val$newMsgObj))) {
                          SendMessagesHelper.this.stopVideoService(str);
                        }
                        return;
                      }
                    }
                  });
                }
              }
            }
            for (j = i;; j = 1)
            {
              if (j != 0)
              {
                MessagesStorage.getInstance().markMessageAsSendError(SendMessagesHelper.9.this.val$newMsgObj);
                SendMessagesHelper.9.this.val$newMsgObj.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(SendMessagesHelper.9.this.val$newMsgObj.id) });
                SendMessagesHelper.this.processSentMessage(SendMessagesHelper.9.this.val$newMsgObj.id);
                if ((MessageObject.isVideoMessage(SendMessagesHelper.9.this.val$newMsgObj)) || (MessageObject.isNewGifMessage(SendMessagesHelper.9.this.val$newMsgObj))) {
                  SendMessagesHelper.this.stopVideoService(SendMessagesHelper.9.this.val$newMsgObj.attachPath);
                }
                SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.9.this.val$newMsgObj.id);
              }
              return;
              i = j;
              if (!(paramAnonymousTLObject instanceof TLRPC.Updates)) {
                break;
              }
              localObject3 = ((TLRPC.Updates)paramAnonymousTLObject).updates;
              localObject2 = null;
              i = 0;
              label590:
              localObject1 = localObject2;
              if (i < ((ArrayList)localObject3).size())
              {
                localObject1 = (TLRPC.Update)((ArrayList)localObject3).get(i);
                if ((localObject1 instanceof TLRPC.TL_updateNewMessage))
                {
                  localObject2 = (TLRPC.TL_updateNewMessage)localObject1;
                  localObject1 = ((TLRPC.TL_updateNewMessage)localObject2).message;
                  localArrayList.add(localObject1);
                  SendMessagesHelper.9.this.val$newMsgObj.id = ((TLRPC.TL_updateNewMessage)localObject2).message.id;
                  Utilities.stageQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.getInstance().processNewDifferenceParams(-1, localObject2.pts, -1, localObject2.pts_count);
                    }
                  });
                }
              }
              else
              {
                label678:
                if (localObject1 == null) {
                  break label925;
                }
                localObject3 = (Integer)MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.Message)localObject1).dialog_id));
                localObject2 = localObject3;
                if (localObject3 == null)
                {
                  localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(((TLRPC.Message)localObject1).out, ((TLRPC.Message)localObject1).dialog_id));
                  MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.Message)localObject1).dialog_id), localObject2);
                }
                if (((Integer)localObject2).intValue() >= ((TLRPC.Message)localObject1).id) {
                  break label919;
                }
              }
              label919:
              for (boolean bool1 = true;; bool1 = false)
              {
                ((TLRPC.Message)localObject1).unread = bool1;
                SendMessagesHelper.9.this.val$newMsgObj.id = ((TLRPC.Message)localObject1).id;
                SendMessagesHelper.this.updateMediaPaths(SendMessagesHelper.9.this.val$msgObj, (TLRPC.Message)localObject1, SendMessagesHelper.9.this.val$originalPath, false);
                i = j;
                break;
                if ((localObject1 instanceof TLRPC.TL_updateNewChannelMessage))
                {
                  localObject2 = (TLRPC.TL_updateNewChannelMessage)localObject1;
                  localObject1 = ((TLRPC.TL_updateNewChannelMessage)localObject2).message;
                  localArrayList.add(localObject1);
                  if ((SendMessagesHelper.9.this.val$newMsgObj.flags & 0x80000000) != 0)
                  {
                    localObject3 = ((TLRPC.TL_updateNewChannelMessage)localObject2).message;
                    ((TLRPC.Message)localObject3).flags |= 0x80000000;
                  }
                  Utilities.stageQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.getInstance().processNewChannelDifferenceParams(localObject2.pts, localObject2.pts_count, localObject2.message.to_id.channel_id);
                    }
                  });
                  break label678;
                }
                i += 1;
                break label590;
              }
              label925:
              i = 1;
              break;
              label930:
              j = SendMessagesHelper.9.this.val$newMsgObj.id;
              break label340;
              if (paramAnonymousTL_error.text.equals("PEER_FLOOD")) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(0) });
              }
            }
          }
        });
      }
    };
    paramString = new QuickAckDelegate()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            SendMessagesHelper.10.this.val$newMsgObj.send_state = 0;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByAck, new Object[] { Integer.valueOf(this.val$msg_id) });
          }
        });
      }
    };
    if ((paramTLObject instanceof TLRPC.TL_messages_sendMessage)) {}
    for (int i = 128;; i = 0)
    {
      localConnectionsManager.sendRequest(paramTLObject, paramMessageObject, paramString, i | 0x44);
      return;
    }
  }
  
  public static void prepareSendingAudioDocuments(ArrayList<MessageObject> paramArrayList, final long paramLong, MessageObject paramMessageObject)
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        int m = this.val$messageObjects.size();
        int i = 0;
        final MessageObject localMessageObject;
        final Object localObject1;
        final Object localObject3;
        int j;
        Object localObject2;
        if (i < m)
        {
          localMessageObject = (MessageObject)this.val$messageObjects.get(i);
          localObject1 = localMessageObject.messageOwner.attachPath;
          localObject3 = new File((String)localObject1);
          if ((int)paramLong != 0) {
            break label187;
          }
          j = 1;
          localObject2 = localObject1;
          if (localObject1 != null) {
            localObject2 = (String)localObject1 + "audio" + ((File)localObject3).length();
          }
          localObject1 = null;
          if (j == 0)
          {
            localObject1 = MessagesStorage.getInstance();
            if (j != 0) {
              break label192;
            }
          }
        }
        label187:
        label192:
        for (int k = 1;; k = 4)
        {
          localObject1 = (TLRPC.TL_document)((MessagesStorage)localObject1).getSentFile((String)localObject2, k);
          localObject3 = localObject1;
          if (localObject1 == null) {
            localObject3 = (TLRPC.TL_document)localMessageObject.messageOwner.media.document;
          }
          if (j == 0) {
            break label289;
          }
          j = (int)(paramLong >> 32);
          localObject1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(j));
          if (localObject1 != null) {
            break label197;
          }
          return;
          j = 0;
          break;
        }
        label197:
        if (AndroidUtilities.getPeerLayerVersion(((TLRPC.EncryptedChat)localObject1).layer) < 46) {
          j = 0;
        }
        for (;;)
        {
          if (j < ((TLRPC.TL_document)localObject3).attributes.size())
          {
            if ((((TLRPC.TL_document)localObject3).attributes.get(j) instanceof TLRPC.TL_documentAttributeAudio))
            {
              localObject1 = new TLRPC.TL_documentAttributeAudio_old();
              ((TLRPC.TL_documentAttributeAudio_old)localObject1).duration = ((TLRPC.DocumentAttribute)((TLRPC.TL_document)localObject3).attributes.get(j)).duration;
              ((TLRPC.TL_document)localObject3).attributes.remove(j);
              ((TLRPC.TL_document)localObject3).attributes.add(localObject1);
            }
          }
          else
          {
            label289:
            localObject1 = new HashMap();
            if (localObject2 != null) {
              ((HashMap)localObject1).put("originalPath", localObject2);
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                SendMessagesHelper.getInstance().sendMessage(localObject3, null, localMessageObject.messageOwner.attachPath, SendMessagesHelper.13.this.val$dialog_id, SendMessagesHelper.13.this.val$reply_to_msg, null, localObject1);
              }
            });
            i += 1;
            break;
          }
          j += 1;
        }
      }
    }).start();
  }
  
  public static void prepareSendingBotContextResult(TLRPC.BotInlineResult paramBotInlineResult, HashMap<String, String> paramHashMap, final long paramLong, final MessageObject paramMessageObject)
  {
    if (paramBotInlineResult == null) {}
    do
    {
      return;
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaAuto))
      {
        new Thread(new Runnable()
        {
          public void run()
          {
            String str1 = null;
            TLRPC.TL_document localTL_document = null;
            Object localObject7 = null;
            Object localObject6 = null;
            TLRPC.TL_game localTL_game2 = null;
            final Object localObject5;
            final String str2;
            final TLRPC.TL_game localTL_game1;
            final Object localObject1;
            if ((this.val$result instanceof TLRPC.TL_botInlineMediaResult)) {
              if (this.val$result.type.equals("game"))
              {
                if ((int)paramLong == 0) {
                  return;
                }
                localTL_game2 = new TLRPC.TL_game();
                localTL_game2.title = this.val$result.title;
                localTL_game2.description = this.val$result.description;
                localTL_game2.short_name = this.val$result.id;
                localTL_game2.photo = this.val$result.photo;
                localObject5 = localTL_document;
                str2 = str1;
                localTL_game1 = localTL_game2;
                localObject1 = localObject6;
                if ((this.val$result.document instanceof TLRPC.TL_document))
                {
                  localTL_game2.document = this.val$result.document;
                  localTL_game2.flags |= 0x1;
                  localObject1 = localObject6;
                  localTL_game1 = localTL_game2;
                  str2 = str1;
                  localObject5 = localTL_document;
                }
              }
            }
            for (;;)
            {
              if ((paramMessageObject != null) && (this.val$result.content_url != null)) {
                paramMessageObject.put("originalPath", this.val$result.content_url);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (localObject5 != null)
                  {
                    localObject5.caption = SendMessagesHelper.15.this.val$result.send_message.caption;
                    SendMessagesHelper.getInstance().sendMessage(localObject5, null, str2, SendMessagesHelper.15.this.val$dialog_id, SendMessagesHelper.15.this.val$reply_to_msg, SendMessagesHelper.15.this.val$result.send_message.reply_markup, SendMessagesHelper.15.this.val$params);
                  }
                  do
                  {
                    return;
                    if (localObject1 != null)
                    {
                      localObject1.caption = SendMessagesHelper.15.this.val$result.send_message.caption;
                      SendMessagesHelper.getInstance().sendMessage(localObject1, SendMessagesHelper.15.this.val$result.content_url, SendMessagesHelper.15.this.val$dialog_id, SendMessagesHelper.15.this.val$reply_to_msg, SendMessagesHelper.15.this.val$result.send_message.reply_markup, SendMessagesHelper.15.this.val$params);
                      return;
                    }
                  } while (localTL_game1 == null);
                  SendMessagesHelper.getInstance().sendMessage(localTL_game1, SendMessagesHelper.15.this.val$dialog_id, SendMessagesHelper.15.this.val$result.send_message.reply_markup, SendMessagesHelper.15.this.val$params);
                }
              });
              return;
              if (this.val$result.document != null)
              {
                localObject5 = localTL_document;
                str2 = str1;
                localTL_game1 = localTL_game2;
                localObject1 = localObject6;
                if ((this.val$result.document instanceof TLRPC.TL_document))
                {
                  localObject5 = (TLRPC.TL_document)this.val$result.document;
                  str2 = str1;
                  localTL_game1 = localTL_game2;
                  localObject1 = localObject6;
                }
              }
              else
              {
                localObject5 = localTL_document;
                str2 = str1;
                localTL_game1 = localTL_game2;
                localObject1 = localObject6;
                if (this.val$result.photo != null)
                {
                  localObject5 = localTL_document;
                  str2 = str1;
                  localTL_game1 = localTL_game2;
                  localObject1 = localObject6;
                  if ((this.val$result.photo instanceof TLRPC.TL_photo))
                  {
                    localObject1 = (TLRPC.TL_photo)this.val$result.photo;
                    localObject5 = localTL_document;
                    str2 = str1;
                    localTL_game1 = localTL_game2;
                    continue;
                    localObject5 = localTL_document;
                    str2 = str1;
                    localTL_game1 = localTL_game2;
                    localObject1 = localObject6;
                    if (this.val$result.content_url != null)
                    {
                      localObject1 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.content_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.content_url, "file"));
                      label455:
                      int i;
                      if (((File)localObject1).exists())
                      {
                        str1 = ((File)localObject1).getAbsolutePath();
                        localObject5 = this.val$result.type;
                        i = -1;
                        switch (((String)localObject5).hashCode())
                        {
                        default: 
                          switch (i)
                          {
                          default: 
                            localObject5 = localTL_document;
                            str2 = str1;
                            localTL_game1 = localTL_game2;
                            localObject1 = localObject6;
                            break;
                          case 0: 
                          case 1: 
                          case 2: 
                          case 3: 
                          case 4: 
                          case 5: 
                            label536:
                            localTL_document = new TLRPC.TL_document();
                            localTL_document.id = 0L;
                            localTL_document.size = 0;
                            localTL_document.dc_id = 0;
                            localTL_document.mime_type = this.val$result.content_type;
                            localTL_document.date = ConnectionsManager.getInstance().getCurrentTime();
                            localObject5 = new TLRPC.TL_documentAttributeFilename();
                            localTL_document.attributes.add(localObject5);
                            localObject1 = this.val$result.type;
                            i = -1;
                            switch (((String)localObject1).hashCode())
                            {
                            default: 
                              label740:
                              switch (i)
                              {
                              }
                              break;
                            }
                            break;
                          }
                          break;
                        }
                      }
                      for (;;)
                      {
                        if (((TLRPC.TL_documentAttributeFilename)localObject5).file_name == null) {
                          ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "file";
                        }
                        if (localTL_document.mime_type == null) {
                          localTL_document.mime_type = "application/octet-stream";
                        }
                        localObject5 = localTL_document;
                        str2 = str1;
                        localTL_game1 = localTL_game2;
                        localObject1 = localObject6;
                        if (localTL_document.thumb != null) {
                          break;
                        }
                        localTL_document.thumb = new TLRPC.TL_photoSize();
                        localTL_document.thumb.w = this.val$result.w;
                        localTL_document.thumb.h = this.val$result.h;
                        localTL_document.thumb.size = 0;
                        localTL_document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
                        localTL_document.thumb.type = "x";
                        localObject5 = localTL_document;
                        str2 = str1;
                        localTL_game1 = localTL_game2;
                        localObject1 = localObject6;
                        break;
                        str1 = this.val$result.content_url;
                        break label455;
                        if (!((String)localObject5).equals("audio")) {
                          break label536;
                        }
                        i = 0;
                        break label536;
                        if (!((String)localObject5).equals("voice")) {
                          break label536;
                        }
                        i = 1;
                        break label536;
                        if (!((String)localObject5).equals("file")) {
                          break label536;
                        }
                        i = 2;
                        break label536;
                        if (!((String)localObject5).equals("video")) {
                          break label536;
                        }
                        i = 3;
                        break label536;
                        if (!((String)localObject5).equals("sticker")) {
                          break label536;
                        }
                        i = 4;
                        break label536;
                        if (!((String)localObject5).equals("gif")) {
                          break label536;
                        }
                        i = 5;
                        break label536;
                        if (!((String)localObject5).equals("photo")) {
                          break label536;
                        }
                        i = 6;
                        break label536;
                        if (!((String)localObject1).equals("gif")) {
                          break label740;
                        }
                        i = 0;
                        break label740;
                        if (!((String)localObject1).equals("voice")) {
                          break label740;
                        }
                        i = 1;
                        break label740;
                        if (!((String)localObject1).equals("audio")) {
                          break label740;
                        }
                        i = 2;
                        break label740;
                        if (!((String)localObject1).equals("file")) {
                          break label740;
                        }
                        i = 3;
                        break label740;
                        if (!((String)localObject1).equals("video")) {
                          break label740;
                        }
                        i = 4;
                        break label740;
                        if (!((String)localObject1).equals("sticker")) {
                          break label740;
                        }
                        i = 5;
                        break label740;
                        ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "animation.gif";
                        if (str1.endsWith("mp4"))
                        {
                          localTL_document.mime_type = "video/mp4";
                          localTL_document.attributes.add(new TLRPC.TL_documentAttributeAnimated());
                        }
                        for (;;)
                        {
                          try
                          {
                            if (!str1.endsWith("mp4")) {
                              break label1231;
                            }
                            localObject1 = ThumbnailUtils.createVideoThumbnail(str1, 1);
                            if (localObject1 == null) {
                              break;
                            }
                            localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject1, 90.0F, 90.0F, 55, false);
                            ((Bitmap)localObject1).recycle();
                          }
                          catch (Throwable localThrowable1)
                          {
                            FileLog.e("tmessages", localThrowable1);
                          }
                          break;
                          localTL_document.mime_type = "image/gif";
                          continue;
                          label1231:
                          localObject2 = ImageLoader.loadBitmap(str1, null, 90.0F, 90.0F, true);
                        }
                        Object localObject2 = new TLRPC.TL_documentAttributeAudio();
                        ((TLRPC.TL_documentAttributeAudio)localObject2).duration = this.val$result.duration;
                        ((TLRPC.TL_documentAttributeAudio)localObject2).voice = true;
                        ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "audio.ogg";
                        localTL_document.attributes.add(localObject2);
                        localTL_document.thumb = new TLRPC.TL_photoSizeEmpty();
                        localTL_document.thumb.type = "s";
                        continue;
                        localObject2 = new TLRPC.TL_documentAttributeAudio();
                        ((TLRPC.TL_documentAttributeAudio)localObject2).duration = this.val$result.duration;
                        ((TLRPC.TL_documentAttributeAudio)localObject2).title = this.val$result.title;
                        ((TLRPC.TL_documentAttributeAudio)localObject2).flags |= 0x1;
                        if (this.val$result.description != null)
                        {
                          ((TLRPC.TL_documentAttributeAudio)localObject2).performer = this.val$result.description;
                          ((TLRPC.TL_documentAttributeAudio)localObject2).flags |= 0x2;
                        }
                        ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "audio.mp3";
                        localTL_document.attributes.add(localObject2);
                        localTL_document.thumb = new TLRPC.TL_photoSizeEmpty();
                        localTL_document.thumb.type = "s";
                        continue;
                        i = this.val$result.content_type.indexOf('/');
                        if (i != -1)
                        {
                          ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = ("file." + this.val$result.content_type.substring(i + 1));
                        }
                        else
                        {
                          ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "file";
                          continue;
                          ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "video.mp4";
                          localObject2 = new TLRPC.TL_documentAttributeVideo();
                          ((TLRPC.TL_documentAttributeVideo)localObject2).w = this.val$result.w;
                          ((TLRPC.TL_documentAttributeVideo)localObject2).h = this.val$result.h;
                          ((TLRPC.TL_documentAttributeVideo)localObject2).duration = this.val$result.duration;
                          localTL_document.attributes.add(localObject2);
                          try
                          {
                            localObject2 = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "jpg")).getAbsolutePath(), null, 90.0F, 90.0F, true);
                            if (localObject2 == null) {
                              continue;
                            }
                            localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject2, 90.0F, 90.0F, 55, false);
                            ((Bitmap)localObject2).recycle();
                          }
                          catch (Throwable localThrowable2)
                          {
                            FileLog.e("tmessages", localThrowable2);
                          }
                          continue;
                          Object localObject3 = new TLRPC.TL_documentAttributeSticker();
                          ((TLRPC.TL_documentAttributeSticker)localObject3).alt = "";
                          ((TLRPC.TL_documentAttributeSticker)localObject3).stickerset = new TLRPC.TL_inputStickerSetEmpty();
                          localTL_document.attributes.add(localObject3);
                          localObject3 = new TLRPC.TL_documentAttributeImageSize();
                          ((TLRPC.TL_documentAttributeImageSize)localObject3).w = this.val$result.w;
                          ((TLRPC.TL_documentAttributeImageSize)localObject3).h = this.val$result.h;
                          localTL_document.attributes.add(localObject3);
                          ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "sticker.webp";
                          try
                          {
                            localObject3 = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "webp")).getAbsolutePath(), null, 90.0F, 90.0F, true);
                            if (localObject3 != null)
                            {
                              localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject3, 90.0F, 90.0F, 55, false);
                              ((Bitmap)localObject3).recycle();
                            }
                          }
                          catch (Throwable localThrowable3)
                          {
                            FileLog.e("tmessages", localThrowable3);
                          }
                        }
                      }
                      localObject6 = localObject7;
                      if (localThrowable3.exists()) {
                        localObject6 = SendMessagesHelper.getInstance().generatePhotoSizes(str1, null);
                      }
                      localObject5 = localTL_document;
                      str2 = str1;
                      localTL_game1 = localTL_game2;
                      Object localObject4 = localObject6;
                      if (localObject6 == null)
                      {
                        localObject4 = new TLRPC.TL_photo();
                        ((TLRPC.TL_photo)localObject4).date = ConnectionsManager.getInstance().getCurrentTime();
                        localObject5 = new TLRPC.TL_photoSize();
                        ((TLRPC.TL_photoSize)localObject5).w = this.val$result.w;
                        ((TLRPC.TL_photoSize)localObject5).h = this.val$result.h;
                        ((TLRPC.TL_photoSize)localObject5).size = 1;
                        ((TLRPC.TL_photoSize)localObject5).location = new TLRPC.TL_fileLocationUnavailable();
                        ((TLRPC.TL_photoSize)localObject5).type = "x";
                        ((TLRPC.TL_photo)localObject4).sizes.add(localObject5);
                        localObject5 = localTL_document;
                        str2 = str1;
                        localTL_game1 = localTL_game2;
                      }
                    }
                  }
                }
              }
            }
          }
        }).run();
        return;
      }
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageText))
      {
        localObject = getInstance();
        String str = paramBotInlineResult.send_message.message;
        if (!paramBotInlineResult.send_message.no_webpage) {}
        for (boolean bool = true;; bool = false)
        {
          ((SendMessagesHelper)localObject).sendMessage(str, paramLong, paramMessageObject, null, bool, paramBotInlineResult.send_message.entities, paramBotInlineResult.send_message.reply_markup, paramHashMap);
          return;
        }
      }
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))
      {
        localObject = new TLRPC.TL_messageMediaVenue();
        ((TLRPC.TL_messageMediaVenue)localObject).geo = paramBotInlineResult.send_message.geo;
        ((TLRPC.TL_messageMediaVenue)localObject).address = paramBotInlineResult.send_message.address;
        ((TLRPC.TL_messageMediaVenue)localObject).title = paramBotInlineResult.send_message.title;
        ((TLRPC.TL_messageMediaVenue)localObject).provider = paramBotInlineResult.send_message.provider;
        ((TLRPC.TL_messageMediaVenue)localObject).venue_id = paramBotInlineResult.send_message.venue_id;
        getInstance().sendMessage((TLRPC.MessageMedia)localObject, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
        return;
      }
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo))
      {
        localObject = new TLRPC.TL_messageMediaGeo();
        ((TLRPC.TL_messageMediaGeo)localObject).geo = paramBotInlineResult.send_message.geo;
        getInstance().sendMessage((TLRPC.MessageMedia)localObject, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
        return;
      }
    } while (!(paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaContact));
    Object localObject = new TLRPC.TL_user();
    ((TLRPC.User)localObject).phone = paramBotInlineResult.send_message.phone_number;
    ((TLRPC.User)localObject).first_name = paramBotInlineResult.send_message.first_name;
    ((TLRPC.User)localObject).last_name = paramBotInlineResult.send_message.last_name;
    getInstance().sendMessage((TLRPC.User)localObject, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
  }
  
  public static void prepareSendingDocument(String paramString1, String paramString2, Uri paramUri, String paramString3, long paramLong, MessageObject paramMessageObject)
  {
    if (((paramString1 == null) || (paramString2 == null)) && (paramUri == null)) {
      return;
    }
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList1 = null;
    if (paramUri != null) {
      localArrayList1 = new ArrayList();
    }
    localArrayList2.add(paramString1);
    localArrayList3.add(paramString2);
    prepareSendingDocuments(localArrayList2, localArrayList3, localArrayList1, paramString3, paramLong, paramMessageObject);
  }
  
  private static boolean prepareSendingDocumentInternal(final String paramString1, String paramString2, Uri paramUri, String paramString3, final long paramLong, MessageObject paramMessageObject, String paramString4)
  {
    if (((paramString1 == null) || (paramString1.length() == 0)) && (paramUri == null)) {
      return false;
    }
    if ((paramUri != null) && (AndroidUtilities.isInternalUri(paramUri))) {
      return false;
    }
    if ((paramString1 != null) && (AndroidUtilities.isInternalUri(Uri.fromFile(new File(paramString1))))) {
      return false;
    }
    MimeTypeMap localMimeTypeMap = MimeTypeMap.getSingleton();
    Object localObject2 = null;
    final String str1 = paramString1;
    if (paramUri != null)
    {
      paramString1 = null;
      if (paramString3 != null) {
        paramString1 = localMimeTypeMap.getExtensionFromMimeType(paramString3);
      }
      paramString3 = paramString1;
      if (paramString1 == null) {
        paramString3 = "txt";
      }
      paramString1 = MediaController.copyFileToCache(paramUri, paramString3);
      str1 = paramString1;
      if (paramString1 == null) {
        return false;
      }
    }
    File localFile = new File(str1);
    if ((!localFile.exists()) || (localFile.length() == 0L)) {
      return false;
    }
    boolean bool;
    if ((int)paramLong == 0)
    {
      bool = true;
      if (bool) {
        break label284;
      }
    }
    String str2;
    int j;
    Object localObject1;
    label284:
    for (int i = 1;; i = 0)
    {
      str2 = localFile.getName();
      paramString3 = "";
      j = str1.lastIndexOf('.');
      if (j != -1) {
        paramString3 = str1.substring(j + 1);
      }
      if (!paramString3.toLowerCase().equals("mp3"))
      {
        localObject1 = localObject2;
        if (!paramString3.toLowerCase().equals("m4a")) {
          break label393;
        }
      }
      paramUri = AudioInfo.getAudioInfo(localFile);
      localObject1 = localObject2;
      if (paramUri == null) {
        break label393;
      }
      localObject1 = localObject2;
      if (paramUri.getDuration() == 0L) {
        break label393;
      }
      if (!bool) {
        break label979;
      }
      j = (int)(paramLong >> 32);
      paramString1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(j));
      if (paramString1 != null) {
        break label290;
      }
      return false;
      bool = false;
      break;
    }
    label290:
    if (AndroidUtilities.getPeerLayerVersion(paramString1.layer) >= 46) {
      paramString1 = new TLRPC.TL_documentAttributeAudio();
    }
    for (;;)
    {
      paramString1.duration = ((int)(paramUri.getDuration() / 1000L));
      paramString1.title = paramUri.getTitle();
      paramString1.performer = paramUri.getArtist();
      if (paramString1.title == null)
      {
        paramString1.title = "";
        paramString1.flags |= 0x1;
      }
      localObject1 = paramString1;
      if (paramString1.performer == null)
      {
        paramString1.performer = "";
        paramString1.flags |= 0x2;
        localObject1 = paramString1;
      }
      label393:
      paramUri = paramString2;
      if (paramString2 != null)
      {
        if (localObject1 != null) {
          paramUri = paramString2 + "audio" + localFile.length();
        }
      }
      else
      {
        label433:
        paramString1 = null;
        if (!bool)
        {
          paramString1 = MessagesStorage.getInstance();
          if (bool) {
            break label1022;
          }
          j = 1;
          label452:
          paramString2 = (TLRPC.TL_document)paramString1.getSentFile(paramUri, j);
          paramString1 = paramString2;
          if (paramString2 == null)
          {
            paramString1 = paramString2;
            if (!str1.equals(paramUri))
            {
              paramString1 = paramString2;
              if (!bool)
              {
                paramString1 = MessagesStorage.getInstance();
                paramString2 = str1 + localFile.length();
                if (bool) {
                  break label1028;
                }
                j = 1;
                label523:
                paramString1 = (TLRPC.TL_document)paramString1.getSentFile(paramString2, j);
              }
            }
          }
        }
        paramString2 = paramString1;
        if (paramString1 == null)
        {
          paramString1 = new TLRPC.TL_document();
          paramString1.id = 0L;
          paramString1.date = ConnectionsManager.getInstance().getCurrentTime();
          paramString2 = new TLRPC.TL_documentAttributeFilename();
          paramString2.file_name = str2;
          paramString1.attributes.add(paramString2);
          paramString1.size = ((int)localFile.length());
          paramString1.dc_id = 0;
          if (localObject1 != null) {
            paramString1.attributes.add(localObject1);
          }
          if (paramString3.length() == 0) {
            break label1066;
          }
          if (!paramString3.toLowerCase().equals("webp")) {
            break label1034;
          }
          paramString1.mime_type = "image/webp";
          label643:
          if (!paramString1.mime_type.equals("image/gif")) {}
        }
      }
      try
      {
        paramString3 = ImageLoader.loadBitmap(localFile.getAbsolutePath(), null, 90.0F, 90.0F, true);
        if (paramString3 != null)
        {
          paramString2.file_name = "animation.gif";
          paramString1.thumb = ImageLoader.scaleAndSaveImage(paramString3, 90.0F, 90.0F, 55, bool);
          paramString3.recycle();
        }
        if ((paramString1.mime_type.equals("image/webp")) && (i != 0)) {
          paramString2 = new BitmapFactory.Options();
        }
      }
      catch (Exception paramString2)
      {
        try
        {
          paramString2.inJustDecodeBounds = true;
          paramString3 = new RandomAccessFile(str1, "r");
          localObject1 = paramString3.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, str1.length());
          Utilities.loadWebpImage(null, (ByteBuffer)localObject1, ((ByteBuffer)localObject1).limit(), paramString2, true);
          paramString3.close();
          if ((paramString2.outWidth != 0) && (paramString2.outHeight != 0) && (paramString2.outWidth <= 800) && (paramString2.outHeight <= 800))
          {
            paramString3 = new TLRPC.TL_documentAttributeSticker();
            paramString3.alt = "";
            paramString3.stickerset = new TLRPC.TL_inputStickerSetEmpty();
            paramString1.attributes.add(paramString3);
            paramString3 = new TLRPC.TL_documentAttributeImageSize();
            paramString3.w = paramString2.outWidth;
            paramString3.h = paramString2.outHeight;
            paramString1.attributes.add(paramString3);
          }
          paramString2 = paramString1;
          if (paramString1.thumb == null)
          {
            paramString1.thumb = new TLRPC.TL_photoSizeEmpty();
            paramString1.thumb.type = "s";
            paramString2 = paramString1;
          }
          paramString2.caption = paramString4;
          paramString1 = new HashMap();
          if (paramUri != null) {
            paramString1.put("originalPath", paramUri);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, str1, paramLong, paramString1, null, this.val$params);
            }
          });
          return true;
          paramString1 = new TLRPC.TL_documentAttributeAudio_old();
          continue;
          label979:
          paramString1 = new TLRPC.TL_documentAttributeAudio();
          continue;
          paramUri = paramString2 + "" + localFile.length();
          break label433;
          label1022:
          j = 4;
          break label452;
          label1028:
          j = 4;
          break label523;
          label1034:
          paramString3 = localMimeTypeMap.getMimeTypeFromExtension(paramString3.toLowerCase());
          if (paramString3 != null)
          {
            paramString1.mime_type = paramString3;
            break label643;
          }
          paramString1.mime_type = "application/octet-stream";
          break label643;
          label1066:
          paramString1.mime_type = "application/octet-stream";
          break label643;
          paramString2 = paramString2;
          FileLog.e("tmessages", paramString2);
        }
        catch (Exception paramString3)
        {
          for (;;)
          {
            FileLog.e("tmessages", paramString3);
          }
        }
      }
    }
  }
  
  public static void prepareSendingDocuments(ArrayList<String> paramArrayList1, final ArrayList<String> paramArrayList2, final ArrayList<Uri> paramArrayList, final String paramString, final long paramLong, MessageObject paramMessageObject)
  {
    if (((paramArrayList1 == null) && (paramArrayList2 == null) && (paramArrayList == null)) || ((paramArrayList1 != null) && (paramArrayList2 != null) && (paramArrayList1.size() != paramArrayList2.size()))) {
      return;
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        int i = 0;
        int j = 0;
        if (this.val$paths != null)
        {
          k = 0;
          for (;;)
          {
            i = j;
            if (k >= this.val$paths.size()) {
              break;
            }
            if (!SendMessagesHelper.prepareSendingDocumentInternal((String)this.val$paths.get(k), (String)paramArrayList2.get(k), null, paramString, paramLong, paramArrayList, null)) {
              j = 1;
            }
            k += 1;
          }
        }
        int k = i;
        if (this.val$uris != null)
        {
          j = 0;
          for (;;)
          {
            k = i;
            if (j >= this.val$uris.size()) {
              break;
            }
            if (!SendMessagesHelper.prepareSendingDocumentInternal(null, null, (Uri)this.val$uris.get(j), paramString, paramLong, paramArrayList, null)) {
              i = 1;
            }
            j += 1;
          }
        }
        if (k != 0) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              try
              {
                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", 2131166358), 0).show();
                return;
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
              }
            }
          });
        }
      }
    }).start();
  }
  
  public static void prepareSendingPhoto(String paramString, Uri paramUri, long paramLong, MessageObject paramMessageObject, CharSequence paramCharSequence, ArrayList<TLRPC.InputDocument> paramArrayList)
  {
    Object localObject5 = null;
    Object localObject4 = null;
    Object localObject3 = null;
    Object localObject2 = null;
    Object localObject1 = localObject5;
    if (paramString != null)
    {
      localObject1 = localObject5;
      if (paramString.length() != 0)
      {
        localObject1 = new ArrayList();
        ((ArrayList)localObject1).add(paramString);
      }
    }
    paramString = (String)localObject4;
    if (paramUri != null)
    {
      paramString = new ArrayList();
      paramString.add(paramUri);
    }
    paramUri = (Uri)localObject3;
    if (paramCharSequence != null)
    {
      paramUri = new ArrayList();
      paramUri.add(paramCharSequence.toString());
    }
    paramCharSequence = (CharSequence)localObject2;
    if (paramArrayList != null)
    {
      paramCharSequence = (CharSequence)localObject2;
      if (!paramArrayList.isEmpty())
      {
        paramCharSequence = new ArrayList();
        paramCharSequence.add(new ArrayList(paramArrayList));
      }
    }
    prepareSendingPhotos((ArrayList)localObject1, paramString, paramLong, paramMessageObject, paramUri, paramCharSequence);
  }
  
  public static void prepareSendingPhotos(ArrayList<String> paramArrayList1, ArrayList<Uri> paramArrayList, long paramLong, final MessageObject paramMessageObject, final ArrayList<String> paramArrayList2, final ArrayList<ArrayList<TLRPC.InputDocument>> paramArrayList3)
  {
    if (((paramArrayList1 == null) && (paramArrayList == null)) || ((paramArrayList1 != null) && (paramArrayList1.isEmpty())) || ((paramArrayList != null) && (paramArrayList.isEmpty()))) {
      return;
    }
    ArrayList localArrayList1 = new ArrayList();
    final ArrayList localArrayList2 = new ArrayList();
    if (paramArrayList1 != null) {
      localArrayList1.addAll(paramArrayList1);
    }
    if (paramArrayList != null) {
      localArrayList2.addAll(paramArrayList);
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        int j;
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        int k;
        label37:
        Object localObject3;
        Uri localUri;
        Object localObject8;
        int m;
        label49:
        Object localObject1;
        label79:
        final Object localObject7;
        Object localObject6;
        int n;
        label169:
        int i;
        Object localObject5;
        if ((int)this.val$dialog_id == 0)
        {
          j = 1;
          localObject2 = null;
          localArrayList1 = null;
          localArrayList2 = null;
          if (localArrayList2.isEmpty()) {
            break label290;
          }
          k = localArrayList2.size();
          localObject3 = null;
          localUri = null;
          localObject8 = null;
          m = 0;
          if (m >= k) {
            break label938;
          }
          if (localArrayList2.isEmpty()) {
            break label301;
          }
          localObject1 = (String)localArrayList2.get(m);
          localObject3 = localObject1;
          localObject4 = localObject1;
          localObject7 = localObject3;
          localObject6 = localObject4;
          if (localObject4 == null)
          {
            localObject7 = localObject3;
            localObject6 = localObject4;
            if (localUri != null)
            {
              localObject6 = AndroidUtilities.getPath(localUri);
              localObject7 = localUri.toString();
            }
          }
          n = 0;
          if ((localObject6 == null) || ((!((String)localObject6).endsWith(".gif")) && (!((String)localObject6).endsWith(".webp")))) {
            break label343;
          }
          if (!((String)localObject6).endsWith(".gif")) {
            break label336;
          }
          localObject3 = "gif";
          i = 1;
          localObject5 = localObject6;
          localObject4 = localObject7;
          label179:
          if (i == 0) {
            break label470;
          }
          localObject6 = localObject2;
          if (localObject2 == null)
          {
            localObject6 = new ArrayList();
            localArrayList1 = new ArrayList();
            localArrayList2 = new ArrayList();
          }
          ((ArrayList)localObject6).add(localObject5);
          localArrayList1.add(localObject4);
          if (paramArrayList3 == null) {
            break label464;
          }
          localObject2 = (String)paramArrayList3.get(m);
          label256:
          localArrayList2.add(localObject2);
          localObject2 = localObject6;
        }
        for (;;)
        {
          m += 1;
          localObject8 = localObject3;
          localObject3 = localObject1;
          break label49;
          j = 0;
          break;
          label290:
          k = paramArrayList2.size();
          break label37;
          label301:
          localObject1 = localObject3;
          if (paramArrayList2.isEmpty()) {
            break label79;
          }
          localUri = (Uri)paramArrayList2.get(m);
          localObject1 = localObject3;
          break label79;
          label336:
          localObject3 = "webp";
          break label169;
          label343:
          localObject3 = localObject8;
          i = n;
          localObject4 = localObject7;
          localObject5 = localObject6;
          if (localObject6 != null) {
            break label179;
          }
          localObject3 = localObject8;
          i = n;
          localObject4 = localObject7;
          localObject5 = localObject6;
          if (localUri == null) {
            break label179;
          }
          if (MediaController.isGif(localUri))
          {
            i = 1;
            localObject4 = localUri.toString();
            localObject5 = MediaController.copyFileToCache(localUri, "gif");
            localObject3 = "gif";
            break label179;
          }
          localObject3 = localObject8;
          i = n;
          localObject4 = localObject7;
          localObject5 = localObject6;
          if (!MediaController.isWebp(localUri)) {
            break label179;
          }
          i = 1;
          localObject4 = localUri.toString();
          localObject5 = MediaController.copyFileToCache(localUri, "webp");
          localObject3 = "webp";
          break label179;
          label464:
          localObject2 = null;
          break label256;
          label470:
          if (localObject5 != null)
          {
            localObject6 = new File((String)localObject5);
            localObject6 = (String)localObject4 + ((File)localObject6).length() + "_" + ((File)localObject6).lastModified();
            localObject4 = null;
            if (j == 0)
            {
              localObject4 = MessagesStorage.getInstance();
              if (j != 0) {
                break label779;
              }
              i = 0;
              label542:
              localObject7 = (TLRPC.TL_photo)((MessagesStorage)localObject4).getSentFile((String)localObject6, i);
              localObject4 = localObject7;
              if (localObject7 == null)
              {
                localObject4 = localObject7;
                if (localUri != null)
                {
                  localObject4 = MessagesStorage.getInstance();
                  localObject7 = AndroidUtilities.getPath(localUri);
                  if (j != 0) {
                    break label784;
                  }
                  i = 0;
                  label591:
                  localObject4 = (TLRPC.TL_photo)((MessagesStorage)localObject4).getSentFile((String)localObject7, i);
                }
              }
            }
            localObject7 = localObject4;
            if (localObject4 == null) {
              localObject7 = SendMessagesHelper.getInstance().generatePhotoSizes((String)localObject1, localUri);
            }
            if (localObject7 == null) {
              break label844;
            }
            localObject4 = new HashMap();
            if (paramArrayList3 != null) {
              ((TLRPC.TL_photo)localObject7).caption = ((String)paramArrayList3.get(m));
            }
            if (paramMessageObject == null) {
              break label811;
            }
            localObject5 = (ArrayList)paramMessageObject.get(m);
            if ((localObject5 == null) || (((ArrayList)localObject5).isEmpty())) {
              break label789;
            }
          }
          label779:
          label784:
          label789:
          for (boolean bool = true;; bool = false)
          {
            ((TLRPC.TL_photo)localObject7).has_stickers = bool;
            if (!bool) {
              break label811;
            }
            localObject8 = new SerializedData(((ArrayList)localObject5).size() * 20 + 4);
            ((SerializedData)localObject8).writeInt32(((ArrayList)localObject5).size());
            i = 0;
            while (i < ((ArrayList)localObject5).size())
            {
              ((TLRPC.InputDocument)((ArrayList)localObject5).get(i)).serializeToStream((AbstractSerializedData)localObject8);
              i += 1;
            }
            localObject6 = null;
            break;
            i = 3;
            break label542;
            i = 3;
            break label591;
          }
          ((HashMap)localObject4).put("masks", Utilities.bytesToHex(((SerializedData)localObject8).toByteArray()));
          label811:
          if (localObject6 != null) {
            ((HashMap)localObject4).put("originalPath", localObject6);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper.getInstance().sendMessage(localObject7, null, SendMessagesHelper.18.this.val$dialog_id, SendMessagesHelper.18.this.val$reply_to_msg, null, localObject4);
            }
          });
        }
        label844:
        final Object localObject4 = localObject2;
        if (localObject2 == null)
        {
          localObject4 = new ArrayList();
          localArrayList1 = new ArrayList();
          localArrayList2 = new ArrayList();
        }
        ((ArrayList)localObject4).add(localObject5);
        localArrayList1.add(localObject6);
        if (paramArrayList3 != null) {}
        for (Object localObject2 = (String)paramArrayList3.get(m);; localObject2 = null)
        {
          localArrayList2.add(localObject2);
          localObject2 = localObject4;
          break;
        }
        label938:
        if ((localObject2 != null) && (!((ArrayList)localObject2).isEmpty()))
        {
          i = 0;
          while (i < ((ArrayList)localObject2).size())
          {
            SendMessagesHelper.prepareSendingDocumentInternal((String)((ArrayList)localObject2).get(i), (String)localArrayList1.get(i), null, (String)localObject8, this.val$dialog_id, this.val$reply_to_msg, (String)localArrayList2.get(i));
            i += 1;
          }
        }
      }
    }).start();
  }
  
  public static void prepareSendingPhotosSearch(ArrayList<MediaController.SearchImage> paramArrayList, long paramLong, final MessageObject paramMessageObject)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        boolean bool2;
        int i;
        label13:
        final MediaController.SearchImage localSearchImage;
        final HashMap localHashMap;
        final Object localObject1;
        Object localObject2;
        label87:
        Object localObject5;
        final Object localObject4;
        TLRPC.TL_document localTL_document;
        if ((int)this.val$dialog_id == 0)
        {
          bool2 = true;
          i = 0;
          if (i >= paramMessageObject.size()) {
            return;
          }
          localSearchImage = (MediaController.SearchImage)paramMessageObject.get(i);
          if (localSearchImage.type != 1) {
            break label761;
          }
          localHashMap = new HashMap();
          localObject1 = null;
          if (!(localSearchImage.document instanceof TLRPC.TL_document)) {
            break label575;
          }
          localObject2 = (TLRPC.TL_document)localSearchImage.document;
          localObject1 = FileLoader.getPathToAttach((TLObject)localObject2, true);
          localObject5 = localObject1;
          localObject4 = localObject2;
          if (localObject2 == null)
          {
            if (localSearchImage.localUrl != null) {
              localHashMap.put("url", localSearchImage.localUrl);
            }
            localObject2 = null;
            localTL_document = new TLRPC.TL_document();
            localTL_document.id = 0L;
            localTL_document.date = ConnectionsManager.getInstance().getCurrentTime();
            localObject4 = new TLRPC.TL_documentAttributeFilename();
            ((TLRPC.TL_documentAttributeFilename)localObject4).file_name = "animation.gif";
            localTL_document.attributes.add(localObject4);
            localTL_document.size = localSearchImage.size;
            localTL_document.dc_id = 0;
            if (!((File)localObject1).toString().endsWith("mp4")) {
              break label702;
            }
            localTL_document.mime_type = "video/mp4";
            localTL_document.attributes.add(new TLRPC.TL_documentAttributeAnimated());
            label229:
            if (!((File)localObject1).exists()) {
              break label713;
            }
            localObject2 = localObject1;
            label241:
            localObject4 = localObject2;
            if (localObject2 == null)
            {
              localObject2 = Utilities.MD5(localSearchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.thumbUrl, "jpg");
              localObject2 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2);
              localObject4 = localObject2;
              if (!((File)localObject2).exists()) {
                localObject4 = null;
              }
            }
            if (localObject4 == null) {}
          }
        }
        for (;;)
        {
          try
          {
            if (!((File)localObject4).getAbsolutePath().endsWith("mp4")) {
              continue;
            }
            localObject2 = ThumbnailUtils.createVideoThumbnail(((File)localObject4).getAbsolutePath(), 1);
            if (localObject2 != null)
            {
              localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject2, 90.0F, 90.0F, 55, bool2);
              ((Bitmap)localObject2).recycle();
            }
          }
          catch (Exception localException)
          {
            label575:
            label702:
            label713:
            FileLog.e("tmessages", localException);
            continue;
            localObject1 = ((File)localObject5).toString();
            continue;
          }
          localObject5 = localObject1;
          localObject4 = localTL_document;
          if (localTL_document.thumb == null)
          {
            localTL_document.thumb = new TLRPC.TL_photoSize();
            localTL_document.thumb.w = localSearchImage.width;
            localTL_document.thumb.h = localSearchImage.height;
            localTL_document.thumb.size = 0;
            localTL_document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
            localTL_document.thumb.type = "x";
            localObject4 = localTL_document;
            localObject5 = localObject1;
          }
          if (localSearchImage.caption != null) {
            ((TLRPC.TL_document)localObject4).caption = localSearchImage.caption.toString();
          }
          localObject1 = localSearchImage.imageUrl;
          if (localObject5 != null) {
            continue;
          }
          localObject1 = localSearchImage.imageUrl;
          if ((localHashMap != null) && (localSearchImage.imageUrl != null)) {
            localHashMap.put("originalPath", localSearchImage.imageUrl);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper.getInstance().sendMessage(localObject4, null, localObject1, SendMessagesHelper.16.this.val$dialog_id, SendMessagesHelper.16.this.val$reply_to_msg, null, localHashMap);
            }
          });
          i += 1;
          break label13;
          bool2 = false;
          break;
          localObject2 = localObject1;
          if (!bool2)
          {
            localObject2 = MessagesStorage.getInstance();
            localObject4 = localSearchImage.imageUrl;
            if (bool2) {
              continue;
            }
            j = 1;
            localObject4 = (TLRPC.Document)((MessagesStorage)localObject2).getSentFile((String)localObject4, j);
            localObject2 = localObject1;
            if ((localObject4 instanceof TLRPC.TL_document)) {
              localObject2 = (TLRPC.TL_document)localObject4;
            }
          }
          localObject1 = Utilities.MD5(localSearchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.imageUrl, "jpg");
          localObject1 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject1);
          break label87;
          j = 4;
          continue;
          localTL_document.mime_type = "image/gif";
          break label229;
          localObject1 = null;
          break label241;
          localObject2 = ImageLoader.loadBitmap(((File)localObject4).getAbsolutePath(), null, 90.0F, 90.0F, true);
        }
        label761:
        final boolean bool3 = true;
        boolean bool4 = true;
        Object localObject3 = null;
        if (!bool2)
        {
          localObject1 = MessagesStorage.getInstance();
          localObject3 = localSearchImage.imageUrl;
          if (bool2) {
            break label1212;
          }
        }
        label1212:
        for (int j = 0;; j = 3)
        {
          localObject3 = (TLRPC.TL_photo)((MessagesStorage)localObject1).getSentFile((String)localObject3, j);
          localObject4 = localObject3;
          if (localObject3 == null)
          {
            localObject1 = Utilities.MD5(localSearchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.imageUrl, "jpg");
            localObject4 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject1);
            boolean bool1 = bool4;
            localObject1 = localObject3;
            if (((File)localObject4).exists())
            {
              bool1 = bool4;
              localObject1 = localObject3;
              if (((File)localObject4).length() != 0L)
              {
                localObject3 = SendMessagesHelper.getInstance().generatePhotoSizes(((File)localObject4).toString(), null);
                bool1 = bool4;
                localObject1 = localObject3;
                if (localObject3 != null)
                {
                  bool1 = false;
                  localObject1 = localObject3;
                }
              }
            }
            bool3 = bool1;
            localObject4 = localObject1;
            if (localObject1 == null)
            {
              localObject3 = Utilities.MD5(localSearchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.thumbUrl, "jpg");
              localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
              if (((File)localObject3).exists()) {
                localObject1 = SendMessagesHelper.getInstance().generatePhotoSizes(((File)localObject3).toString(), null);
              }
              bool3 = bool1;
              localObject4 = localObject1;
              if (localObject1 == null)
              {
                localObject4 = new TLRPC.TL_photo();
                ((TLRPC.TL_photo)localObject4).date = ConnectionsManager.getInstance().getCurrentTime();
                localObject1 = new TLRPC.TL_photoSize();
                ((TLRPC.TL_photoSize)localObject1).w = localSearchImage.width;
                ((TLRPC.TL_photoSize)localObject1).h = localSearchImage.height;
                ((TLRPC.TL_photoSize)localObject1).size = 0;
                ((TLRPC.TL_photoSize)localObject1).location = new TLRPC.TL_fileLocationUnavailable();
                ((TLRPC.TL_photoSize)localObject1).type = "x";
                ((TLRPC.TL_photo)localObject4).sizes.add(localObject1);
                bool3 = bool1;
              }
            }
          }
          if (localObject4 == null) {
            break;
          }
          if (localSearchImage.caption != null) {
            ((TLRPC.TL_photo)localObject4).caption = localSearchImage.caption.toString();
          }
          localObject1 = new HashMap();
          if (localSearchImage.imageUrl != null) {
            ((HashMap)localObject1).put("originalPath", localSearchImage.imageUrl);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper localSendMessagesHelper = SendMessagesHelper.getInstance();
              TLRPC.TL_photo localTL_photo = localObject4;
              if (bool3) {}
              for (String str = localSearchImage.imageUrl;; str = null)
              {
                localSendMessagesHelper.sendMessage(localTL_photo, str, SendMessagesHelper.16.this.val$dialog_id, SendMessagesHelper.16.this.val$reply_to_msg, null, localObject1);
                return;
              }
            }
          });
          break;
        }
      }
    }).start();
  }
  
  public static void prepareSendingText(String paramString, final long paramLong)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                String str1 = SendMessagesHelper.getTrimmedString(SendMessagesHelper.17.this.val$text);
                if (str1.length() != 0)
                {
                  int j = (int)Math.ceil(str1.length() / 4096.0F);
                  int i = 0;
                  while (i < j)
                  {
                    String str2 = str1.substring(i * 4096, Math.min((i + 1) * 4096, str1.length()));
                    SendMessagesHelper.getInstance().sendMessage(str2, SendMessagesHelper.17.this.val$dialog_id, null, null, true, null, null, null);
                    i += 1;
                  }
                }
              }
            });
          }
        });
      }
    });
  }
  
  public static void prepareSendingVideo(final String paramString, final long paramLong1, final long paramLong2, int paramInt1, final int paramInt2, VideoEditedInfo paramVideoEditedInfo, long paramLong3, final MessageObject paramMessageObject)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return;
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        boolean bool;
        final Object localObject3;
        Object localObject1;
        Object localObject4;
        final TLRPC.TL_document localTL_document;
        if ((int)this.val$dialog_id == 0)
        {
          bool = true;
          if ((paramString == null) && (!paramLong2.endsWith("mp4"))) {
            break label671;
          }
          localObject3 = paramLong2;
          localObject1 = paramLong2;
          localObject4 = new File((String)localObject1);
          localObject2 = (String)localObject1 + ((File)localObject4).length() + "_" + ((File)localObject4).lastModified();
          localObject1 = localObject2;
          if (paramString != null)
          {
            localObject2 = (String)localObject2 + paramInt2 + "_" + paramString.startTime + "_" + paramString.endTime;
            localObject1 = localObject2;
            if (paramString.resultWidth == paramString.originalWidth) {
              localObject1 = (String)localObject2 + "_" + paramString.resultWidth;
            }
          }
          localTL_document = null;
          if (!bool) {}
          localObject2 = localObject3;
          if (0 == 0)
          {
            localObject2 = ImageLoader.scaleAndSaveImage(ThumbnailUtils.createVideoThumbnail(paramLong2, 1), 90.0F, 90.0F, 55, bool);
            localTL_document = new TLRPC.TL_document();
            localTL_document.thumb = ((TLRPC.PhotoSize)localObject2);
            if (localTL_document.thumb != null) {
              break label541;
            }
            localTL_document.thumb = new TLRPC.TL_photoSizeEmpty();
            localTL_document.thumb.type = "s";
            label283:
            localTL_document.mime_type = "video/mp4";
            UserConfig.saveConfig(false);
            localObject2 = new TLRPC.TL_documentAttributeVideo();
            localTL_document.attributes.add(localObject2);
            if (paramString == null) {
              break label635;
            }
            if (paramString.bitrate != -1) {
              break label554;
            }
            localTL_document.attributes.add(new TLRPC.TL_documentAttributeAnimated());
            SendMessagesHelper.fillVideoAttribute(paramLong2, (TLRPC.TL_documentAttributeVideo)localObject2, paramString);
            localObject3 = paramString;
            localObject4 = paramString;
            int i = ((TLRPC.TL_documentAttributeVideo)localObject2).w;
            ((VideoEditedInfo)localObject4).resultWidth = i;
            ((VideoEditedInfo)localObject3).originalWidth = i;
            localObject3 = paramString;
            localObject4 = paramString;
            i = ((TLRPC.TL_documentAttributeVideo)localObject2).h;
            ((VideoEditedInfo)localObject4).resultHeight = i;
            ((VideoEditedInfo)localObject3).originalHeight = i;
            label421:
            localTL_document.size = ((int)this.val$estimatedSize);
            localObject2 = "-2147483648_" + UserConfig.lastLocalId + ".mp4";
            UserConfig.lastLocalId -= 1;
            localObject2 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2);
            UserConfig.saveConfig(false);
          }
        }
        for (final Object localObject2 = ((File)localObject2).getAbsolutePath();; localObject2 = localObject3)
        {
          localObject3 = new HashMap();
          if (localObject1 != null) {
            ((HashMap)localObject3).put("originalPath", localObject1);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper.getInstance().sendMessage(localTL_document, SendMessagesHelper.19.this.val$videoEditedInfo, localObject2, SendMessagesHelper.19.this.val$dialog_id, SendMessagesHelper.19.this.val$reply_to_msg, null, localObject3);
            }
          });
          return;
          bool = false;
          break;
          label541:
          localTL_document.thumb.type = "s";
          break label283;
          label554:
          ((TLRPC.TL_documentAttributeVideo)localObject2).duration = ((int)(paramInt2 / 1000L));
          if ((paramString.rotationValue == 90) || (paramString.rotationValue == 270))
          {
            ((TLRPC.TL_documentAttributeVideo)localObject2).w = paramLong1;
            ((TLRPC.TL_documentAttributeVideo)localObject2).h = paramMessageObject;
            break label421;
          }
          ((TLRPC.TL_documentAttributeVideo)localObject2).w = paramMessageObject;
          ((TLRPC.TL_documentAttributeVideo)localObject2).h = paramLong1;
          break label421;
          label635:
          if (((File)localObject4).exists()) {
            localTL_document.size = ((int)((File)localObject4).length());
          }
          SendMessagesHelper.fillVideoAttribute(paramLong2, (TLRPC.TL_documentAttributeVideo)localObject2, null);
        }
        label671:
        SendMessagesHelper.prepareSendingDocumentInternal(paramLong2, paramLong2, null, null, this.val$dialog_id, this.val$reply_to_msg, null);
      }
    }).start();
  }
  
  private void putToDelayedMessages(String paramString, DelayedMessage paramDelayedMessage)
  {
    ArrayList localArrayList2 = (ArrayList)this.delayedMessages.get(paramString);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.delayedMessages.put(paramString, localArrayList1);
    }
    localArrayList1.add(paramDelayedMessage);
  }
  
  private void sendLocation(Location paramLocation)
  {
    TLRPC.TL_messageMediaGeo localTL_messageMediaGeo = new TLRPC.TL_messageMediaGeo();
    localTL_messageMediaGeo.geo = new TLRPC.TL_geoPoint();
    localTL_messageMediaGeo.geo.lat = paramLocation.getLatitude();
    localTL_messageMediaGeo.geo._long = paramLocation.getLongitude();
    paramLocation = this.waitingForLocation.entrySet().iterator();
    while (paramLocation.hasNext())
    {
      MessageObject localMessageObject = (MessageObject)((Map.Entry)paramLocation.next()).getValue();
      getInstance().sendMessage(localTL_messageMediaGeo, localMessageObject.getDialogId(), localMessageObject, null, null);
    }
  }
  
  /* Error */
  private void sendMessage(String paramString1, TLRPC.MessageMedia paramMessageMedia, TLRPC.TL_photo paramTL_photo, VideoEditedInfo paramVideoEditedInfo, TLRPC.User paramUser, TLRPC.TL_document paramTL_document, TLRPC.TL_game paramTL_game, long paramLong, String paramString2, MessageObject paramMessageObject1, TLRPC.WebPage paramWebPage, boolean paramBoolean, MessageObject paramMessageObject2, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    // Byte code:
    //   0: lload 8
    //   2: lconst_0
    //   3: lcmp
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aconst_null
    //   9: astore 25
    //   11: aload 25
    //   13: astore 31
    //   15: aload 17
    //   17: ifnull +31 -> 48
    //   20: aload 25
    //   22: astore 31
    //   24: aload 17
    //   26: ldc_w 911
    //   29: invokevirtual 1038	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   32: ifeq +16 -> 48
    //   35: aload 17
    //   37: ldc_w 911
    //   40: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   43: checkcast 340	java/lang/String
    //   46: astore 31
    //   48: aconst_null
    //   49: astore 25
    //   51: aconst_null
    //   52: astore 26
    //   54: iconst_m1
    //   55: istore 18
    //   57: lload 8
    //   59: l2i
    //   60: istore 23
    //   62: lload 8
    //   64: bipush 32
    //   66: lshr
    //   67: l2i
    //   68: istore 22
    //   70: iconst_0
    //   71: istore 20
    //   73: aconst_null
    //   74: astore 27
    //   76: iload 23
    //   78: ifeq +99 -> 177
    //   81: iload 23
    //   83: invokestatic 1042	org/telegram/messenger/MessagesController:getInputPeer	(I)Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   86: astore 32
    //   88: aconst_null
    //   89: astore 38
    //   91: iload 23
    //   93: ifne +90 -> 183
    //   96: invokestatic 740	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   99: iload 22
    //   101: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   104: invokevirtual 748	org/telegram/messenger/MessagesController:getEncryptedChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   107: astore 27
    //   109: aload 27
    //   111: astore 34
    //   113: aload 27
    //   115: ifnonnull +116 -> 231
    //   118: aload 14
    //   120: ifnull -113 -> 7
    //   123: invokestatic 782	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   126: aload 14
    //   128: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   131: invokevirtual 1045	org/telegram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   134: aload 14
    //   136: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   139: iconst_2
    //   140: putfield 1048	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   143: invokestatic 163	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   146: getstatic 1051	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   149: iconst_1
    //   150: anewarray 4	java/lang/Object
    //   153: dup
    //   154: iconst_0
    //   155: aload 14
    //   157: invokevirtual 1054	org/telegram/messenger/MessageObject:getId	()I
    //   160: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   163: aastore
    //   164: invokevirtual 1058	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   167: aload_0
    //   168: aload 14
    //   170: invokevirtual 1054	org/telegram/messenger/MessageObject:getId	()I
    //   173: invokevirtual 1062	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   176: return
    //   177: aconst_null
    //   178: astore 32
    //   180: goto -92 -> 88
    //   183: aload 27
    //   185: astore 34
    //   187: aload 32
    //   189: instanceof 1064
    //   192: ifeq +39 -> 231
    //   195: invokestatic 740	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   198: aload 32
    //   200: getfield 1069	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   203: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   206: invokevirtual 1073	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   209: astore 28
    //   211: aload 28
    //   213: ifnull +618 -> 831
    //   216: aload 28
    //   218: getfield 1078	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   221: ifne +610 -> 831
    //   224: iconst_1
    //   225: istore 20
    //   227: aload 27
    //   229: astore 34
    //   231: aload 14
    //   233: ifnull +1158 -> 1391
    //   236: aload 26
    //   238: astore 25
    //   240: aload 14
    //   242: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   245: astore 26
    //   247: aload 26
    //   249: astore 25
    //   251: aload 14
    //   253: invokevirtual 1081	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   256: ifeq +585 -> 841
    //   259: iconst_4
    //   260: istore 19
    //   262: aload 12
    //   264: astore 37
    //   266: aload 6
    //   268: astore 33
    //   270: aload 5
    //   272: astore 35
    //   274: aload_1
    //   275: astore 36
    //   277: aload 26
    //   279: astore 7
    //   281: aload 7
    //   283: astore 25
    //   285: aload 7
    //   287: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   290: lconst_0
    //   291: lcmp
    //   292: ifne +16 -> 308
    //   295: aload 7
    //   297: astore 25
    //   299: aload 7
    //   301: aload_0
    //   302: invokevirtual 1087	org/telegram/messenger/SendMessagesHelper:getNextRandomId	()J
    //   305: putfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   308: aload 17
    //   310: ifnull +85 -> 395
    //   313: aload 7
    //   315: astore 25
    //   317: aload 17
    //   319: ldc_w 1089
    //   322: invokevirtual 1038	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   325: ifeq +70 -> 395
    //   328: aload 34
    //   330: ifnull +2857 -> 3187
    //   333: aload 7
    //   335: astore 25
    //   337: aload 7
    //   339: aload 17
    //   341: ldc_w 1091
    //   344: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   347: checkcast 340	java/lang/String
    //   350: putfield 1094	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   353: aload 7
    //   355: astore 25
    //   357: aload 7
    //   359: getfield 1094	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   362: ifnonnull +15 -> 377
    //   365: aload 7
    //   367: astore 25
    //   369: aload 7
    //   371: ldc_w 713
    //   374: putfield 1094	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   377: aload 7
    //   379: astore 25
    //   381: aload 7
    //   383: aload 7
    //   385: getfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   388: sipush 2048
    //   391: ior
    //   392: putfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   395: aload 7
    //   397: astore 25
    //   399: aload 7
    //   401: aload 17
    //   403: putfield 1098	org/telegram/tgnet/TLRPC$Message:params	Ljava/util/HashMap;
    //   406: aload 14
    //   408: ifnull +15 -> 423
    //   411: aload 7
    //   413: astore 25
    //   415: aload 14
    //   417: getfield 1101	org/telegram/messenger/MessageObject:resendAsIs	Z
    //   420: ifne +125 -> 545
    //   423: aload 7
    //   425: astore 25
    //   427: aload 7
    //   429: invokestatic 545	org/telegram/tgnet/ConnectionsManager:getInstance	()Lorg/telegram/tgnet/ConnectionsManager;
    //   432: invokevirtual 790	org/telegram/tgnet/ConnectionsManager:getCurrentTime	()I
    //   435: putfield 1102	org/telegram/tgnet/TLRPC$Message:date	I
    //   438: aload 7
    //   440: astore 25
    //   442: aload 32
    //   444: instanceof 1064
    //   447: ifeq +2805 -> 3252
    //   450: iload 20
    //   452: ifeq +31 -> 483
    //   455: aload 7
    //   457: astore 25
    //   459: aload 7
    //   461: iconst_1
    //   462: putfield 1105	org/telegram/tgnet/TLRPC$Message:views	I
    //   465: aload 7
    //   467: astore 25
    //   469: aload 7
    //   471: aload 7
    //   473: getfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   476: sipush 1024
    //   479: ior
    //   480: putfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   483: aload 7
    //   485: astore 25
    //   487: invokestatic 740	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   490: aload 32
    //   492: getfield 1069	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   495: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   498: invokevirtual 1073	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   501: astore_1
    //   502: aload_1
    //   503: ifnull +42 -> 545
    //   506: aload 7
    //   508: astore 25
    //   510: aload_1
    //   511: getfield 1078	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   514: ifeq +2702 -> 3216
    //   517: aload 7
    //   519: astore 25
    //   521: aload 7
    //   523: aload 7
    //   525: getfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   528: ldc_w 1106
    //   531: ior
    //   532: putfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   535: aload 7
    //   537: astore 25
    //   539: aload 7
    //   541: iconst_1
    //   542: putfield 1109	org/telegram/tgnet/TLRPC$Message:unread	Z
    //   545: aload 7
    //   547: astore 25
    //   549: aload 7
    //   551: aload 7
    //   553: getfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   556: sipush 512
    //   559: ior
    //   560: putfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   563: aload 7
    //   565: astore 25
    //   567: aload 7
    //   569: lload 8
    //   571: putfield 1112	org/telegram/tgnet/TLRPC$Message:dialog_id	J
    //   574: aload 11
    //   576: ifnull +73 -> 649
    //   579: aload 34
    //   581: ifnull +2684 -> 3265
    //   584: aload 7
    //   586: astore 25
    //   588: aload 11
    //   590: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   593: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   596: lconst_0
    //   597: lcmp
    //   598: ifeq +2667 -> 3265
    //   601: aload 7
    //   603: astore 25
    //   605: aload 7
    //   607: aload 11
    //   609: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   612: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   615: putfield 1115	org/telegram/tgnet/TLRPC$Message:reply_to_random_id	J
    //   618: aload 7
    //   620: astore 25
    //   622: aload 7
    //   624: aload 7
    //   626: getfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   629: bipush 8
    //   631: ior
    //   632: putfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   635: aload 7
    //   637: astore 25
    //   639: aload 7
    //   641: aload 11
    //   643: invokevirtual 1054	org/telegram/messenger/MessageObject:getId	()I
    //   646: putfield 1118	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   649: aload 16
    //   651: ifnull +36 -> 687
    //   654: aload 34
    //   656: ifnonnull +31 -> 687
    //   659: aload 7
    //   661: astore 25
    //   663: aload 7
    //   665: aload 7
    //   667: getfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   670: bipush 64
    //   672: ior
    //   673: putfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   676: aload 7
    //   678: astore 25
    //   680: aload 7
    //   682: aload 16
    //   684: putfield 1119	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   687: iload 23
    //   689: ifeq +3141 -> 3830
    //   692: iload 22
    //   694: iconst_1
    //   695: if_icmpne +3046 -> 3741
    //   698: aload 7
    //   700: astore 25
    //   702: aload_0
    //   703: getfield 136	org/telegram/messenger/SendMessagesHelper:currentChatInfo	Lorg/telegram/tgnet/TLRPC$ChatFull;
    //   706: ifnonnull +2579 -> 3285
    //   709: aload 7
    //   711: astore 25
    //   713: invokestatic 782	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   716: aload 7
    //   718: invokevirtual 1045	org/telegram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   721: aload 7
    //   723: astore 25
    //   725: invokestatic 163	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   728: getstatic 1051	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   731: iconst_1
    //   732: anewarray 4	java/lang/Object
    //   735: dup
    //   736: iconst_0
    //   737: aload 7
    //   739: getfield 1121	org/telegram/tgnet/TLRPC$Message:id	I
    //   742: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   745: aastore
    //   746: invokevirtual 1058	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   749: aload 7
    //   751: astore 25
    //   753: aload_0
    //   754: aload 7
    //   756: getfield 1121	org/telegram/tgnet/TLRPC$Message:id	I
    //   759: invokevirtual 1062	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   762: return
    //   763: astore_2
    //   764: aconst_null
    //   765: astore_1
    //   766: aload 25
    //   768: astore 7
    //   770: ldc_w 330
    //   773: aload_2
    //   774: invokestatic 336	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   777: invokestatic 782	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   780: aload 7
    //   782: invokevirtual 1045	org/telegram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   785: aload_1
    //   786: ifnull +11 -> 797
    //   789: aload_1
    //   790: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   793: iconst_2
    //   794: putfield 1048	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   797: invokestatic 163	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   800: getstatic 1051	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   803: iconst_1
    //   804: anewarray 4	java/lang/Object
    //   807: dup
    //   808: iconst_0
    //   809: aload 7
    //   811: getfield 1121	org/telegram/tgnet/TLRPC$Message:id	I
    //   814: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   817: aastore
    //   818: invokevirtual 1058	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   821: aload_0
    //   822: aload 7
    //   824: getfield 1121	org/telegram/tgnet/TLRPC$Message:id	I
    //   827: invokevirtual 1062	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   830: return
    //   831: iconst_0
    //   832: istore 20
    //   834: aload 27
    //   836: astore 34
    //   838: goto -607 -> 231
    //   841: aload 26
    //   843: astore 25
    //   845: aload 14
    //   847: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   850: ifne +150 -> 1000
    //   853: aload 26
    //   855: astore 25
    //   857: aload 14
    //   859: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   862: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   865: instanceof 1127
    //   868: ifeq +119 -> 987
    //   871: goto +8378 -> 9249
    //   874: aload 26
    //   876: astore 7
    //   878: iload 18
    //   880: istore 19
    //   882: aload 27
    //   884: astore 36
    //   886: aload 28
    //   888: astore_2
    //   889: aload 29
    //   891: astore_3
    //   892: aload 30
    //   894: astore 35
    //   896: aload 6
    //   898: astore 33
    //   900: aload 12
    //   902: astore 37
    //   904: aload 17
    //   906: ifnull -625 -> 281
    //   909: aload 26
    //   911: astore 7
    //   913: iload 18
    //   915: istore 19
    //   917: aload 27
    //   919: astore 36
    //   921: aload 28
    //   923: astore_2
    //   924: aload 29
    //   926: astore_3
    //   927: aload 30
    //   929: astore 35
    //   931: aload 6
    //   933: astore 33
    //   935: aload 12
    //   937: astore 37
    //   939: aload 26
    //   941: astore 25
    //   943: aload 17
    //   945: ldc_w 1129
    //   948: invokevirtual 1038	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   951: ifeq -670 -> 281
    //   954: bipush 9
    //   956: istore 19
    //   958: aload 26
    //   960: astore 7
    //   962: aload 27
    //   964: astore 36
    //   966: aload 28
    //   968: astore_2
    //   969: aload 29
    //   971: astore_3
    //   972: aload 30
    //   974: astore 35
    //   976: aload 6
    //   978: astore 33
    //   980: aload 12
    //   982: astore 37
    //   984: goto -703 -> 281
    //   987: aload 26
    //   989: astore 25
    //   991: aload 26
    //   993: getfield 1130	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   996: astore_1
    //   997: goto +8252 -> 9249
    //   1000: aload 26
    //   1002: astore 25
    //   1004: aload 14
    //   1006: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   1009: iconst_4
    //   1010: if_icmpne +30 -> 1040
    //   1013: aload 26
    //   1015: astore 25
    //   1017: aload 26
    //   1019: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1022: astore 28
    //   1024: iconst_1
    //   1025: istore 18
    //   1027: aload_1
    //   1028: astore 27
    //   1030: aload_3
    //   1031: astore 29
    //   1033: aload 5
    //   1035: astore 30
    //   1037: goto -163 -> 874
    //   1040: aload 26
    //   1042: astore 25
    //   1044: aload 14
    //   1046: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   1049: iconst_1
    //   1050: if_icmpne +36 -> 1086
    //   1053: aload 26
    //   1055: astore 25
    //   1057: aload 26
    //   1059: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1062: getfield 1136	org/telegram/tgnet/TLRPC$MessageMedia:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   1065: checkcast 1138	org/telegram/tgnet/TLRPC$TL_photo
    //   1068: astore 29
    //   1070: iconst_2
    //   1071: istore 18
    //   1073: aload_1
    //   1074: astore 27
    //   1076: aload_2
    //   1077: astore 28
    //   1079: aload 5
    //   1081: astore 30
    //   1083: goto -209 -> 874
    //   1086: aload 26
    //   1088: astore 25
    //   1090: aload 14
    //   1092: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   1095: iconst_3
    //   1096: if_icmpeq +8 -> 1104
    //   1099: aload 4
    //   1101: ifnull +39 -> 1140
    //   1104: iconst_3
    //   1105: istore 18
    //   1107: aload 26
    //   1109: astore 25
    //   1111: aload 26
    //   1113: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1116: getfield 1142	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1119: checkcast 472	org/telegram/tgnet/TLRPC$TL_document
    //   1122: astore 6
    //   1124: aload_1
    //   1125: astore 27
    //   1127: aload_2
    //   1128: astore 28
    //   1130: aload_3
    //   1131: astore 29
    //   1133: aload 5
    //   1135: astore 30
    //   1137: goto -263 -> 874
    //   1140: aload 26
    //   1142: astore 25
    //   1144: aload 14
    //   1146: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   1149: bipush 12
    //   1151: if_icmpne +84 -> 1235
    //   1154: aload 26
    //   1156: astore 25
    //   1158: new 1144	org/telegram/tgnet/TLRPC$TL_userRequest_old2
    //   1161: dup
    //   1162: invokespecial 1145	org/telegram/tgnet/TLRPC$TL_userRequest_old2:<init>	()V
    //   1165: astore 30
    //   1167: aload 30
    //   1169: aload 26
    //   1171: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1174: getfield 1146	org/telegram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   1177: putfield 659	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   1180: aload 30
    //   1182: aload 26
    //   1184: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1187: getfield 1147	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   1190: putfield 663	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   1193: aload 30
    //   1195: aload 26
    //   1197: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1200: getfield 1148	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   1203: putfield 667	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   1206: aload 30
    //   1208: aload 26
    //   1210: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1213: getfield 1151	org/telegram/tgnet/TLRPC$MessageMedia:user_id	I
    //   1216: putfield 1152	org/telegram/tgnet/TLRPC$User:id	I
    //   1219: bipush 6
    //   1221: istore 18
    //   1223: aload_1
    //   1224: astore 27
    //   1226: aload_2
    //   1227: astore 28
    //   1229: aload_3
    //   1230: astore 29
    //   1232: goto -358 -> 874
    //   1235: aload 26
    //   1237: astore 25
    //   1239: aload 14
    //   1241: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   1244: bipush 8
    //   1246: if_icmpeq +45 -> 1291
    //   1249: aload 26
    //   1251: astore 25
    //   1253: aload 14
    //   1255: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   1258: bipush 9
    //   1260: if_icmpeq +31 -> 1291
    //   1263: aload 26
    //   1265: astore 25
    //   1267: aload 14
    //   1269: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   1272: bipush 13
    //   1274: if_icmpeq +17 -> 1291
    //   1277: aload 26
    //   1279: astore 25
    //   1281: aload 14
    //   1283: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   1286: bipush 14
    //   1288: if_icmpne +40 -> 1328
    //   1291: aload 26
    //   1293: astore 25
    //   1295: aload 26
    //   1297: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1300: getfield 1142	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1303: checkcast 472	org/telegram/tgnet/TLRPC$TL_document
    //   1306: astore 6
    //   1308: bipush 7
    //   1310: istore 18
    //   1312: aload_1
    //   1313: astore 27
    //   1315: aload_2
    //   1316: astore 28
    //   1318: aload_3
    //   1319: astore 29
    //   1321: aload 5
    //   1323: astore 30
    //   1325: goto -451 -> 874
    //   1328: aload 26
    //   1330: astore 25
    //   1332: aload_1
    //   1333: astore 27
    //   1335: aload_2
    //   1336: astore 28
    //   1338: aload_3
    //   1339: astore 29
    //   1341: aload 5
    //   1343: astore 30
    //   1345: aload 14
    //   1347: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   1350: iconst_2
    //   1351: if_icmpne -477 -> 874
    //   1354: aload 26
    //   1356: astore 25
    //   1358: aload 26
    //   1360: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1363: getfield 1142	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1366: checkcast 472	org/telegram/tgnet/TLRPC$TL_document
    //   1369: astore 6
    //   1371: bipush 8
    //   1373: istore 18
    //   1375: aload_1
    //   1376: astore 27
    //   1378: aload_2
    //   1379: astore 28
    //   1381: aload_3
    //   1382: astore 29
    //   1384: aload 5
    //   1386: astore 30
    //   1388: goto -514 -> 874
    //   1391: aload_1
    //   1392: ifnull +358 -> 1750
    //   1395: aload 34
    //   1397: ifnull +304 -> 1701
    //   1400: aload 26
    //   1402: astore 25
    //   1404: aload 34
    //   1406: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   1409: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   1412: bipush 17
    //   1414: if_icmplt +287 -> 1701
    //   1417: aload 26
    //   1419: astore 25
    //   1421: new 1154	org/telegram/tgnet/TLRPC$TL_message_secret
    //   1424: dup
    //   1425: invokespecial 1155	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1428: astore 7
    //   1430: aload 15
    //   1432: ifnull +26 -> 1458
    //   1435: aload 7
    //   1437: astore 25
    //   1439: aload 15
    //   1441: invokevirtual 943	java/util/ArrayList:isEmpty	()Z
    //   1444: ifne +14 -> 1458
    //   1447: aload 7
    //   1449: astore 25
    //   1451: aload 7
    //   1453: aload 15
    //   1455: putfield 1156	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   1458: aload 12
    //   1460: astore 26
    //   1462: aload 34
    //   1464: ifnull +58 -> 1522
    //   1467: aload 7
    //   1469: astore 25
    //   1471: aload 12
    //   1473: astore 26
    //   1475: aload 12
    //   1477: instanceof 1158
    //   1480: ifeq +42 -> 1522
    //   1483: aload 7
    //   1485: astore 25
    //   1487: aload 12
    //   1489: getfield 1163	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1492: ifnull +7776 -> 9268
    //   1495: aload 7
    //   1497: astore 25
    //   1499: new 1165	org/telegram/tgnet/TLRPC$TL_webPageUrlPending
    //   1502: dup
    //   1503: invokespecial 1166	org/telegram/tgnet/TLRPC$TL_webPageUrlPending:<init>	()V
    //   1506: astore 26
    //   1508: aload 7
    //   1510: astore 25
    //   1512: aload 26
    //   1514: aload 12
    //   1516: getfield 1163	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1519: putfield 1163	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1522: aload 26
    //   1524: ifnonnull +193 -> 1717
    //   1527: aload 7
    //   1529: astore 25
    //   1531: aload 7
    //   1533: new 1168	org/telegram/tgnet/TLRPC$TL_messageMediaEmpty
    //   1536: dup
    //   1537: invokespecial 1169	org/telegram/tgnet/TLRPC$TL_messageMediaEmpty:<init>	()V
    //   1540: putfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1543: aload 17
    //   1545: ifnull +7729 -> 9274
    //   1548: aload 7
    //   1550: astore 25
    //   1552: aload 17
    //   1554: ldc_w 1129
    //   1557: invokevirtual 1038	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1560: ifeq +7714 -> 9274
    //   1563: bipush 9
    //   1565: istore 18
    //   1567: aload 7
    //   1569: astore 25
    //   1571: aload 7
    //   1573: aload_1
    //   1574: putfield 1130	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1577: aload 26
    //   1579: astore 27
    //   1581: aload 7
    //   1583: astore 25
    //   1585: aload 7
    //   1587: getfield 450	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1590: ifnonnull +15 -> 1605
    //   1593: aload 7
    //   1595: astore 25
    //   1597: aload 7
    //   1599: ldc_w 713
    //   1602: putfield 450	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1605: aload 7
    //   1607: astore 25
    //   1609: invokestatic 1174	org/telegram/messenger/UserConfig:getNewMessageId	()I
    //   1612: istore 19
    //   1614: aload 7
    //   1616: astore 25
    //   1618: aload 7
    //   1620: iload 19
    //   1622: putfield 1121	org/telegram/tgnet/TLRPC$Message:id	I
    //   1625: aload 7
    //   1627: astore 25
    //   1629: aload 7
    //   1631: iload 19
    //   1633: putfield 1175	org/telegram/tgnet/TLRPC$Message:local_id	I
    //   1636: aload 7
    //   1638: astore 25
    //   1640: aload 7
    //   1642: iconst_1
    //   1643: putfield 1178	org/telegram/tgnet/TLRPC$Message:out	Z
    //   1646: iload 20
    //   1648: ifeq +1506 -> 3154
    //   1651: aload 32
    //   1653: ifnull +1501 -> 3154
    //   1656: aload 7
    //   1658: astore 25
    //   1660: aload 7
    //   1662: aload 32
    //   1664: getfield 1069	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   1667: ineg
    //   1668: putfield 1181	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1671: aload 7
    //   1673: astore 25
    //   1675: iconst_0
    //   1676: invokestatic 1185	org/telegram/messenger/UserConfig:saveConfig	(Z)V
    //   1679: iload 18
    //   1681: istore 19
    //   1683: aload_1
    //   1684: astore 36
    //   1686: aload 5
    //   1688: astore 35
    //   1690: aload 6
    //   1692: astore 33
    //   1694: aload 27
    //   1696: astore 37
    //   1698: goto -1417 -> 281
    //   1701: aload 26
    //   1703: astore 25
    //   1705: new 1187	org/telegram/tgnet/TLRPC$TL_message
    //   1708: dup
    //   1709: invokespecial 1188	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   1712: astore 7
    //   1714: goto -284 -> 1430
    //   1717: aload 7
    //   1719: astore 25
    //   1721: aload 7
    //   1723: new 1190	org/telegram/tgnet/TLRPC$TL_messageMediaWebPage
    //   1726: dup
    //   1727: invokespecial 1191	org/telegram/tgnet/TLRPC$TL_messageMediaWebPage:<init>	()V
    //   1730: putfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1733: aload 7
    //   1735: astore 25
    //   1737: aload 7
    //   1739: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1742: aload 26
    //   1744: putfield 1195	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   1747: goto -204 -> 1543
    //   1750: aload_2
    //   1751: ifnull +107 -> 1858
    //   1754: aload 34
    //   1756: ifnull +86 -> 1842
    //   1759: aload 26
    //   1761: astore 25
    //   1763: aload 34
    //   1765: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   1768: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   1771: bipush 17
    //   1773: if_icmplt +69 -> 1842
    //   1776: aload 26
    //   1778: astore 25
    //   1780: new 1154	org/telegram/tgnet/TLRPC$TL_message_secret
    //   1783: dup
    //   1784: invokespecial 1155	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1787: astore 7
    //   1789: aload 7
    //   1791: astore 25
    //   1793: aload 7
    //   1795: aload_2
    //   1796: putfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1799: aload 7
    //   1801: astore 25
    //   1803: aload 7
    //   1805: ldc_w 713
    //   1808: putfield 1130	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1811: aload 17
    //   1813: ifnull +7467 -> 9280
    //   1816: aload 7
    //   1818: astore 25
    //   1820: aload 17
    //   1822: ldc_w 1129
    //   1825: invokevirtual 1038	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1828: ifeq +7452 -> 9280
    //   1831: bipush 9
    //   1833: istore 18
    //   1835: aload 12
    //   1837: astore 27
    //   1839: goto -258 -> 1581
    //   1842: aload 26
    //   1844: astore 25
    //   1846: new 1187	org/telegram/tgnet/TLRPC$TL_message
    //   1849: dup
    //   1850: invokespecial 1188	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   1853: astore 7
    //   1855: goto -66 -> 1789
    //   1858: aload_3
    //   1859: ifnull +257 -> 2116
    //   1862: aload 34
    //   1864: ifnull +191 -> 2055
    //   1867: aload 26
    //   1869: astore 25
    //   1871: aload 34
    //   1873: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   1876: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   1879: bipush 17
    //   1881: if_icmplt +174 -> 2055
    //   1884: aload 26
    //   1886: astore 25
    //   1888: new 1154	org/telegram/tgnet/TLRPC$TL_message_secret
    //   1891: dup
    //   1892: invokespecial 1155	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1895: astore 7
    //   1897: aload 7
    //   1899: astore 25
    //   1901: aload 7
    //   1903: new 1197	org/telegram/tgnet/TLRPC$TL_messageMediaPhoto
    //   1906: dup
    //   1907: invokespecial 1198	org/telegram/tgnet/TLRPC$TL_messageMediaPhoto:<init>	()V
    //   1910: putfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1913: aload 7
    //   1915: astore 25
    //   1917: aload 7
    //   1919: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1922: astore 27
    //   1924: aload 7
    //   1926: astore 25
    //   1928: aload_3
    //   1929: getfield 1199	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   1932: ifnull +7358 -> 9290
    //   1935: aload 7
    //   1937: astore 25
    //   1939: aload_3
    //   1940: getfield 1199	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   1943: astore 26
    //   1945: aload 7
    //   1947: astore 25
    //   1949: aload 27
    //   1951: aload 26
    //   1953: putfield 1200	org/telegram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   1956: aload 7
    //   1958: astore 25
    //   1960: aload 7
    //   1962: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1965: aload_3
    //   1966: putfield 1136	org/telegram/tgnet/TLRPC$MessageMedia:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   1969: aload 17
    //   1971: ifnull +7327 -> 9298
    //   1974: aload 7
    //   1976: astore 25
    //   1978: aload 17
    //   1980: ldc_w 1129
    //   1983: invokevirtual 1038	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1986: ifeq +7312 -> 9298
    //   1989: bipush 9
    //   1991: istore 18
    //   1993: aload 7
    //   1995: astore 25
    //   1997: aload 7
    //   1999: ldc_w 1202
    //   2002: putfield 1130	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2005: aload 10
    //   2007: ifnull +64 -> 2071
    //   2010: aload 7
    //   2012: astore 25
    //   2014: aload 10
    //   2016: invokevirtual 347	java/lang/String:length	()I
    //   2019: ifle +52 -> 2071
    //   2022: aload 7
    //   2024: astore 25
    //   2026: aload 10
    //   2028: ldc_w 1204
    //   2031: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   2034: ifeq +37 -> 2071
    //   2037: aload 7
    //   2039: astore 25
    //   2041: aload 7
    //   2043: aload 10
    //   2045: putfield 450	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2048: aload 12
    //   2050: astore 27
    //   2052: goto -471 -> 1581
    //   2055: aload 26
    //   2057: astore 25
    //   2059: new 1187	org/telegram/tgnet/TLRPC$TL_message
    //   2062: dup
    //   2063: invokespecial 1188	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2066: astore 7
    //   2068: goto -171 -> 1897
    //   2071: aload 7
    //   2073: astore 25
    //   2075: aload 7
    //   2077: aload_3
    //   2078: getfield 1207	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   2081: aload_3
    //   2082: getfield 1207	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   2085: invokevirtual 931	java/util/ArrayList:size	()I
    //   2088: iconst_1
    //   2089: isub
    //   2090: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   2093: checkcast 904	org/telegram/tgnet/TLRPC$PhotoSize
    //   2096: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2099: iconst_1
    //   2100: invokestatic 1214	org/telegram/messenger/FileLoader:getPathToAttach	(Lorg/telegram/tgnet/TLObject;Z)Ljava/io/File;
    //   2103: invokevirtual 401	java/io/File:toString	()Ljava/lang/String;
    //   2106: putfield 450	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2109: aload 12
    //   2111: astore 27
    //   2113: goto -532 -> 1581
    //   2116: aload 7
    //   2118: ifnull +92 -> 2210
    //   2121: aload 26
    //   2123: astore 25
    //   2125: new 1187	org/telegram/tgnet/TLRPC$TL_message
    //   2128: dup
    //   2129: invokespecial 1188	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2132: astore 26
    //   2134: aload 26
    //   2136: new 1127	org/telegram/tgnet/TLRPC$TL_messageMediaGame
    //   2139: dup
    //   2140: invokespecial 1215	org/telegram/tgnet/TLRPC$TL_messageMediaGame:<init>	()V
    //   2143: putfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2146: aload 26
    //   2148: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2151: ldc_w 713
    //   2154: putfield 1200	org/telegram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   2157: aload 26
    //   2159: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2162: aload 7
    //   2164: putfield 1219	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   2167: aload 26
    //   2169: ldc_w 713
    //   2172: putfield 1130	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2175: aload 17
    //   2177: ifnull +7061 -> 9238
    //   2180: aload 17
    //   2182: ldc_w 1129
    //   2185: invokevirtual 1038	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   2188: istore 24
    //   2190: iload 24
    //   2192: ifeq +7046 -> 9238
    //   2195: bipush 9
    //   2197: istore 18
    //   2199: aload 26
    //   2201: astore 7
    //   2203: aload 12
    //   2205: astore 27
    //   2207: goto -626 -> 1581
    //   2210: aload 5
    //   2212: ifnull +265 -> 2477
    //   2215: aload 34
    //   2217: ifnull +244 -> 2461
    //   2220: aload 26
    //   2222: astore 25
    //   2224: aload 34
    //   2226: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   2229: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   2232: bipush 17
    //   2234: if_icmplt +227 -> 2461
    //   2237: aload 26
    //   2239: astore 25
    //   2241: new 1154	org/telegram/tgnet/TLRPC$TL_message_secret
    //   2244: dup
    //   2245: invokespecial 1155	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   2248: astore 7
    //   2250: aload 7
    //   2252: astore 25
    //   2254: aload 7
    //   2256: new 1221	org/telegram/tgnet/TLRPC$TL_messageMediaContact
    //   2259: dup
    //   2260: invokespecial 1222	org/telegram/tgnet/TLRPC$TL_messageMediaContact:<init>	()V
    //   2263: putfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2266: aload 7
    //   2268: astore 25
    //   2270: aload 7
    //   2272: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2275: aload 5
    //   2277: getfield 659	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   2280: putfield 1146	org/telegram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   2283: aload 7
    //   2285: astore 25
    //   2287: aload 7
    //   2289: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2292: aload 5
    //   2294: getfield 663	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   2297: putfield 1147	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   2300: aload 7
    //   2302: astore 25
    //   2304: aload 7
    //   2306: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2309: aload 5
    //   2311: getfield 667	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   2314: putfield 1148	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   2317: aload 7
    //   2319: astore 25
    //   2321: aload 7
    //   2323: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2326: aload 5
    //   2328: getfield 1152	org/telegram/tgnet/TLRPC$User:id	I
    //   2331: putfield 1151	org/telegram/tgnet/TLRPC$MessageMedia:user_id	I
    //   2334: aload 7
    //   2336: astore 25
    //   2338: aload 7
    //   2340: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2343: getfield 1147	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   2346: ifnonnull +30 -> 2376
    //   2349: aload 7
    //   2351: astore 25
    //   2353: aload 7
    //   2355: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2358: ldc_w 713
    //   2361: putfield 1147	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   2364: aload 7
    //   2366: astore 25
    //   2368: aload 5
    //   2370: ldc_w 713
    //   2373: putfield 663	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   2376: aload 7
    //   2378: astore 25
    //   2380: aload 7
    //   2382: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2385: getfield 1148	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   2388: ifnonnull +30 -> 2418
    //   2391: aload 7
    //   2393: astore 25
    //   2395: aload 7
    //   2397: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2400: ldc_w 713
    //   2403: putfield 1148	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   2406: aload 7
    //   2408: astore 25
    //   2410: aload 5
    //   2412: ldc_w 713
    //   2415: putfield 667	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   2418: aload 7
    //   2420: astore 25
    //   2422: aload 7
    //   2424: ldc_w 713
    //   2427: putfield 1130	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2430: aload 17
    //   2432: ifnull +6872 -> 9304
    //   2435: aload 7
    //   2437: astore 25
    //   2439: aload 17
    //   2441: ldc_w 1129
    //   2444: invokevirtual 1038	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   2447: ifeq +6857 -> 9304
    //   2450: bipush 9
    //   2452: istore 18
    //   2454: aload 12
    //   2456: astore 27
    //   2458: goto -877 -> 1581
    //   2461: aload 26
    //   2463: astore 25
    //   2465: new 1187	org/telegram/tgnet/TLRPC$TL_message
    //   2468: dup
    //   2469: invokespecial 1188	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2472: astore 7
    //   2474: goto -224 -> 2250
    //   2477: aload 25
    //   2479: astore 7
    //   2481: aload 12
    //   2483: astore 27
    //   2485: aload 6
    //   2487: ifnull -906 -> 1581
    //   2490: aload 34
    //   2492: ifnull +366 -> 2858
    //   2495: aload 26
    //   2497: astore 25
    //   2499: aload 34
    //   2501: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   2504: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   2507: bipush 17
    //   2509: if_icmplt +349 -> 2858
    //   2512: aload 26
    //   2514: astore 25
    //   2516: new 1154	org/telegram/tgnet/TLRPC$TL_message_secret
    //   2519: dup
    //   2520: invokespecial 1155	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   2523: astore 26
    //   2525: aload 26
    //   2527: astore 25
    //   2529: aload 26
    //   2531: new 1224	org/telegram/tgnet/TLRPC$TL_messageMediaDocument
    //   2534: dup
    //   2535: invokespecial 1225	org/telegram/tgnet/TLRPC$TL_messageMediaDocument:<init>	()V
    //   2538: putfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2541: aload 26
    //   2543: astore 25
    //   2545: aload 26
    //   2547: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2550: astore 27
    //   2552: aload 26
    //   2554: astore 25
    //   2556: aload 6
    //   2558: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   2561: ifnull +6754 -> 9315
    //   2564: aload 26
    //   2566: astore 25
    //   2568: aload 6
    //   2570: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   2573: astore 7
    //   2575: aload 26
    //   2577: astore 25
    //   2579: aload 27
    //   2581: aload 7
    //   2583: putfield 1200	org/telegram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   2586: aload 26
    //   2588: astore 25
    //   2590: aload 26
    //   2592: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2595: aload 6
    //   2597: putfield 1142	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   2600: aload 17
    //   2602: ifnull +272 -> 2874
    //   2605: aload 26
    //   2607: astore 25
    //   2609: aload 17
    //   2611: ldc_w 1129
    //   2614: invokevirtual 1038	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   2617: ifeq +257 -> 2874
    //   2620: bipush 9
    //   2622: istore 19
    //   2624: aload 4
    //   2626: ifnonnull +287 -> 2913
    //   2629: aload 26
    //   2631: astore 25
    //   2633: aload 26
    //   2635: ldc_w 1202
    //   2638: putfield 1130	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2641: aload 34
    //   2643: ifnull +287 -> 2930
    //   2646: aload 26
    //   2648: astore 25
    //   2650: aload 6
    //   2652: getfield 531	org/telegram/tgnet/TLRPC$TL_document:dc_id	I
    //   2655: ifle +275 -> 2930
    //   2658: aload 26
    //   2660: astore 25
    //   2662: aload 6
    //   2664: invokestatic 1229	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2667: ifne +263 -> 2930
    //   2670: aload 26
    //   2672: astore 25
    //   2674: aload 26
    //   2676: aload 6
    //   2678: invokestatic 398	org/telegram/messenger/FileLoader:getPathToAttach	(Lorg/telegram/tgnet/TLObject;)Ljava/io/File;
    //   2681: invokevirtual 401	java/io/File:toString	()Ljava/lang/String;
    //   2684: putfield 450	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2687: aload 26
    //   2689: astore 7
    //   2691: iload 19
    //   2693: istore 18
    //   2695: aload 12
    //   2697: astore 27
    //   2699: aload 34
    //   2701: ifnull -1120 -> 1581
    //   2704: aload 26
    //   2706: astore 25
    //   2708: aload 26
    //   2710: astore 7
    //   2712: iload 19
    //   2714: istore 18
    //   2716: aload 12
    //   2718: astore 27
    //   2720: aload 6
    //   2722: invokestatic 1229	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2725: ifeq -1144 -> 1581
    //   2728: iconst_0
    //   2729: istore 21
    //   2731: aload 26
    //   2733: astore 25
    //   2735: aload 26
    //   2737: astore 7
    //   2739: iload 19
    //   2741: istore 18
    //   2743: aload 12
    //   2745: astore 27
    //   2747: iload 21
    //   2749: aload 6
    //   2751: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2754: invokevirtual 931	java/util/ArrayList:size	()I
    //   2757: if_icmpge -1176 -> 1581
    //   2760: aload 26
    //   2762: astore 25
    //   2764: aload 6
    //   2766: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2769: iload 21
    //   2771: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   2774: checkcast 1231	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   2777: astore 27
    //   2779: aload 26
    //   2781: astore 25
    //   2783: aload 27
    //   2785: instanceof 881
    //   2788: ifeq +6548 -> 9336
    //   2791: aload 26
    //   2793: astore 25
    //   2795: aload 34
    //   2797: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   2800: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   2803: bipush 46
    //   2805: if_icmpge +139 -> 2944
    //   2808: aload 26
    //   2810: astore 25
    //   2812: aload 6
    //   2814: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2817: iload 21
    //   2819: invokevirtual 1234	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   2822: pop
    //   2823: aload 26
    //   2825: astore 25
    //   2827: aload 6
    //   2829: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2832: new 1236	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_old
    //   2835: dup
    //   2836: invokespecial 1237	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_old:<init>	()V
    //   2839: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2842: pop
    //   2843: aload 26
    //   2845: astore 7
    //   2847: iload 19
    //   2849: istore 18
    //   2851: aload 12
    //   2853: astore 27
    //   2855: goto -1274 -> 1581
    //   2858: aload 26
    //   2860: astore 25
    //   2862: new 1187	org/telegram/tgnet/TLRPC$TL_message
    //   2865: dup
    //   2866: invokespecial 1188	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2869: astore 26
    //   2871: goto -346 -> 2525
    //   2874: aload 26
    //   2876: astore 25
    //   2878: aload 6
    //   2880: invokestatic 1240	org/telegram/messenger/MessageObject:isVideoDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2883: ifne +6440 -> 9323
    //   2886: aload 4
    //   2888: ifnull +6 -> 2894
    //   2891: goto +6432 -> 9323
    //   2894: aload 26
    //   2896: astore 25
    //   2898: aload 6
    //   2900: invokestatic 1243	org/telegram/messenger/MessageObject:isVoiceDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2903: ifeq +6426 -> 9329
    //   2906: bipush 8
    //   2908: istore 19
    //   2910: goto -286 -> 2624
    //   2913: aload 26
    //   2915: astore 25
    //   2917: aload 26
    //   2919: aload 4
    //   2921: invokevirtual 1246	org/telegram/messenger/VideoEditedInfo:getString	()Ljava/lang/String;
    //   2924: putfield 1130	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2927: goto -286 -> 2641
    //   2930: aload 26
    //   2932: astore 25
    //   2934: aload 26
    //   2936: aload 10
    //   2938: putfield 450	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2941: goto -254 -> 2687
    //   2944: aload 26
    //   2946: astore 25
    //   2948: aload 6
    //   2950: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2953: iload 21
    //   2955: invokevirtual 1234	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   2958: pop
    //   2959: aload 26
    //   2961: astore 25
    //   2963: new 1248	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_layer55
    //   2966: dup
    //   2967: invokespecial 1249	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:<init>	()V
    //   2970: astore 7
    //   2972: aload 26
    //   2974: astore 25
    //   2976: aload 6
    //   2978: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2981: aload 7
    //   2983: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2986: pop
    //   2987: aload 26
    //   2989: astore 25
    //   2991: aload 7
    //   2993: aload 27
    //   2995: getfield 1250	org/telegram/tgnet/TLRPC$DocumentAttribute:alt	Ljava/lang/String;
    //   2998: putfield 1251	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:alt	Ljava/lang/String;
    //   3001: aload 26
    //   3003: astore 25
    //   3005: aload 27
    //   3007: getfield 1252	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   3010: ifnull +113 -> 3123
    //   3013: aload 26
    //   3015: astore 25
    //   3017: aload 27
    //   3019: getfield 1252	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   3022: getfield 1255	org/telegram/tgnet/TLRPC$InputStickerSet:id	J
    //   3025: invokestatic 1261	org/telegram/messenger/query/StickersQuery:getStickerSetName	(J)Ljava/lang/String;
    //   3028: astore 27
    //   3030: aload 27
    //   3032: ifnull +60 -> 3092
    //   3035: aload 26
    //   3037: astore 25
    //   3039: aload 27
    //   3041: invokevirtual 347	java/lang/String:length	()I
    //   3044: ifle +48 -> 3092
    //   3047: aload 26
    //   3049: astore 25
    //   3051: aload 7
    //   3053: new 1263	org/telegram/tgnet/TLRPC$TL_inputStickerSetShortName
    //   3056: dup
    //   3057: invokespecial 1264	org/telegram/tgnet/TLRPC$TL_inputStickerSetShortName:<init>	()V
    //   3060: putfield 1265	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   3063: aload 26
    //   3065: astore 25
    //   3067: aload 7
    //   3069: getfield 1265	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   3072: aload 27
    //   3074: putfield 1268	org/telegram/tgnet/TLRPC$InputStickerSet:short_name	Ljava/lang/String;
    //   3077: aload 26
    //   3079: astore 7
    //   3081: iload 19
    //   3083: istore 18
    //   3085: aload 12
    //   3087: astore 27
    //   3089: goto -1508 -> 1581
    //   3092: aload 26
    //   3094: astore 25
    //   3096: aload 7
    //   3098: new 887	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty
    //   3101: dup
    //   3102: invokespecial 888	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty:<init>	()V
    //   3105: putfield 1265	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   3108: aload 26
    //   3110: astore 7
    //   3112: iload 19
    //   3114: istore 18
    //   3116: aload 12
    //   3118: astore 27
    //   3120: goto -1539 -> 1581
    //   3123: aload 26
    //   3125: astore 25
    //   3127: aload 7
    //   3129: new 887	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty
    //   3132: dup
    //   3133: invokespecial 888	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty:<init>	()V
    //   3136: putfield 1265	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   3139: aload 26
    //   3141: astore 7
    //   3143: iload 19
    //   3145: istore 18
    //   3147: aload 12
    //   3149: astore 27
    //   3151: goto -1570 -> 1581
    //   3154: aload 7
    //   3156: astore 25
    //   3158: aload 7
    //   3160: invokestatic 1271	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   3163: putfield 1181	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   3166: aload 7
    //   3168: astore 25
    //   3170: aload 7
    //   3172: aload 7
    //   3174: getfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   3177: sipush 256
    //   3180: ior
    //   3181: putfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   3184: goto -1513 -> 1671
    //   3187: aload 7
    //   3189: astore 25
    //   3191: aload 7
    //   3193: aload 17
    //   3195: ldc_w 1089
    //   3198: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3201: checkcast 340	java/lang/String
    //   3204: invokestatic 284	org/telegram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   3207: invokevirtual 288	java/lang/Integer:intValue	()I
    //   3210: putfield 1274	org/telegram/tgnet/TLRPC$Message:via_bot_id	I
    //   3213: goto -2836 -> 377
    //   3216: aload 7
    //   3218: astore 25
    //   3220: aload 7
    //   3222: iconst_1
    //   3223: putfield 1277	org/telegram/tgnet/TLRPC$Message:post	Z
    //   3226: aload 7
    //   3228: astore 25
    //   3230: aload_1
    //   3231: getfield 1280	org/telegram/tgnet/TLRPC$Chat:signatures	Z
    //   3234: ifeq -2689 -> 545
    //   3237: aload 7
    //   3239: astore 25
    //   3241: aload 7
    //   3243: invokestatic 1271	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   3246: putfield 1181	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   3249: goto -2704 -> 545
    //   3252: aload 7
    //   3254: astore 25
    //   3256: aload 7
    //   3258: iconst_1
    //   3259: putfield 1109	org/telegram/tgnet/TLRPC$Message:unread	Z
    //   3262: goto -2717 -> 545
    //   3265: aload 7
    //   3267: astore 25
    //   3269: aload 7
    //   3271: aload 7
    //   3273: getfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   3276: bipush 8
    //   3278: ior
    //   3279: putfield 1095	org/telegram/tgnet/TLRPC$Message:flags	I
    //   3282: goto -2647 -> 635
    //   3285: aload 7
    //   3287: astore 25
    //   3289: new 675	java/util/ArrayList
    //   3292: dup
    //   3293: invokespecial 676	java/util/ArrayList:<init>	()V
    //   3296: astore 5
    //   3298: aload_0
    //   3299: getfield 136	org/telegram/messenger/SendMessagesHelper:currentChatInfo	Lorg/telegram/tgnet/TLRPC$ChatFull;
    //   3302: getfield 1286	org/telegram/tgnet/TLRPC$ChatFull:participants	Lorg/telegram/tgnet/TLRPC$ChatParticipants;
    //   3305: getfield 1290	org/telegram/tgnet/TLRPC$ChatParticipants:participants	Ljava/util/ArrayList;
    //   3308: invokevirtual 1291	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   3311: astore_1
    //   3312: aload_1
    //   3313: invokeinterface 1022 1 0
    //   3318: ifeq +49 -> 3367
    //   3321: aload_1
    //   3322: invokeinterface 1026 1 0
    //   3327: checkcast 1293	org/telegram/tgnet/TLRPC$ChatParticipant
    //   3330: astore 6
    //   3332: invokestatic 740	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   3335: aload 6
    //   3337: getfield 1294	org/telegram/tgnet/TLRPC$ChatParticipant:user_id	I
    //   3340: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3343: invokevirtual 1298	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   3346: invokestatic 1302	org/telegram/messenger/MessagesController:getInputUser	(Lorg/telegram/tgnet/TLRPC$User;)Lorg/telegram/tgnet/TLRPC$InputUser;
    //   3349: astore 6
    //   3351: aload 6
    //   3353: ifnull -41 -> 3312
    //   3356: aload 5
    //   3358: aload 6
    //   3360: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3363: pop
    //   3364: goto -52 -> 3312
    //   3367: aload 7
    //   3369: new 1304	org/telegram/tgnet/TLRPC$TL_peerChat
    //   3372: dup
    //   3373: invokespecial 1305	org/telegram/tgnet/TLRPC$TL_peerChat:<init>	()V
    //   3376: putfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3379: aload 7
    //   3381: getfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3384: iload 23
    //   3386: putfield 1314	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   3389: aload 34
    //   3391: ifnull +20 -> 3411
    //   3394: aload 7
    //   3396: astore 25
    //   3398: aload 34
    //   3400: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   3403: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   3406: bipush 46
    //   3408: if_icmplt +46 -> 3454
    //   3411: iload 22
    //   3413: iconst_1
    //   3414: if_icmpeq +40 -> 3454
    //   3417: aload 7
    //   3419: astore 25
    //   3421: aload 7
    //   3423: invokestatic 1318	org/telegram/messenger/MessageObject:isVoiceMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Z
    //   3426: ifeq +28 -> 3454
    //   3429: aload 7
    //   3431: astore 25
    //   3433: aload 7
    //   3435: getfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3438: getfield 1319	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   3441: ifne +13 -> 3454
    //   3444: aload 7
    //   3446: astore 25
    //   3448: aload 7
    //   3450: iconst_1
    //   3451: putfield 1322	org/telegram/tgnet/TLRPC$Message:media_unread	Z
    //   3454: aload 7
    //   3456: astore 25
    //   3458: aload 7
    //   3460: iconst_1
    //   3461: putfield 1048	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   3464: aload 7
    //   3466: astore 25
    //   3468: new 441	org/telegram/messenger/MessageObject
    //   3471: dup
    //   3472: aload 7
    //   3474: aconst_null
    //   3475: iconst_1
    //   3476: invokespecial 1325	org/telegram/messenger/MessageObject:<init>	(Lorg/telegram/tgnet/TLRPC$Message;Ljava/util/AbstractMap;Z)V
    //   3479: astore 6
    //   3481: aload 6
    //   3483: aload 11
    //   3485: putfield 1328	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   3488: aload 6
    //   3490: invokevirtual 1081	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   3493: ifne +34 -> 3527
    //   3496: aload 6
    //   3498: getfield 1122	org/telegram/messenger/MessageObject:type	I
    //   3501: iconst_3
    //   3502: if_icmpeq +8 -> 3510
    //   3505: aload 4
    //   3507: ifnull +20 -> 3527
    //   3510: aload 7
    //   3512: getfield 450	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   3515: invokestatic 1333	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   3518: ifne +9 -> 3527
    //   3521: aload 6
    //   3523: iconst_1
    //   3524: putfield 1336	org/telegram/messenger/MessageObject:attachPathExists	Z
    //   3527: new 675	java/util/ArrayList
    //   3530: dup
    //   3531: invokespecial 676	java/util/ArrayList:<init>	()V
    //   3534: astore_1
    //   3535: aload_1
    //   3536: aload 6
    //   3538: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3541: pop
    //   3542: new 675	java/util/ArrayList
    //   3545: dup
    //   3546: invokespecial 676	java/util/ArrayList:<init>	()V
    //   3549: astore 12
    //   3551: aload 12
    //   3553: aload 7
    //   3555: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3558: pop
    //   3559: invokestatic 782	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   3562: aload 12
    //   3564: iconst_0
    //   3565: iconst_1
    //   3566: iconst_0
    //   3567: iconst_0
    //   3568: invokevirtual 1340	org/telegram/messenger/MessagesStorage:putMessages	(Ljava/util/ArrayList;ZZZI)V
    //   3571: invokestatic 740	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   3574: lload 8
    //   3576: aload_1
    //   3577: invokevirtual 1344	org/telegram/messenger/MessagesController:updateInterfaceWithMessages	(JLjava/util/ArrayList;)V
    //   3580: invokestatic 163	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   3583: getstatic 1347	org/telegram/messenger/NotificationCenter:dialogsNeedReload	I
    //   3586: iconst_0
    //   3587: anewarray 4	java/lang/Object
    //   3590: invokevirtual 1058	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   3593: getstatic 1352	org/telegram/messenger/BuildVars:DEBUG_VERSION	Z
    //   3596: ifeq +5755 -> 9351
    //   3599: aload 32
    //   3601: ifnull +5750 -> 9351
    //   3604: ldc_w 330
    //   3607: new 452	java/lang/StringBuilder
    //   3610: dup
    //   3611: invokespecial 453	java/lang/StringBuilder:<init>	()V
    //   3614: ldc_w 1354
    //   3617: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3620: aload 32
    //   3622: getfield 1355	org/telegram/tgnet/TLRPC$InputPeer:user_id	I
    //   3625: invokevirtual 523	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3628: ldc_w 1357
    //   3631: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3634: aload 32
    //   3636: getfield 1358	org/telegram/tgnet/TLRPC$InputPeer:chat_id	I
    //   3639: invokevirtual 523	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3642: ldc_w 1360
    //   3645: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3648: aload 32
    //   3650: getfield 1069	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   3653: invokevirtual 523	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3656: ldc_w 1362
    //   3659: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3662: aload 32
    //   3664: getfield 1365	org/telegram/tgnet/TLRPC$InputPeer:access_hash	J
    //   3667: invokevirtual 479	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   3670: invokevirtual 482	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3673: invokestatic 1367	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   3676: goto +5675 -> 9351
    //   3679: aload 34
    //   3681: ifnonnull +712 -> 4393
    //   3684: aload 5
    //   3686: ifnull +528 -> 4214
    //   3689: new 511	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast
    //   3692: dup
    //   3693: invokespecial 1368	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:<init>	()V
    //   3696: astore_1
    //   3697: new 675	java/util/ArrayList
    //   3700: dup
    //   3701: invokespecial 676	java/util/ArrayList:<init>	()V
    //   3704: astore_2
    //   3705: iconst_0
    //   3706: istore 18
    //   3708: iload 18
    //   3710: aload 5
    //   3712: invokevirtual 931	java/util/ArrayList:size	()I
    //   3715: if_icmpge +462 -> 4177
    //   3718: aload_2
    //   3719: getstatic 1372	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   3722: invokevirtual 1377	java/security/SecureRandom:nextLong	()J
    //   3725: invokestatic 1380	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3728: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3731: pop
    //   3732: iload 18
    //   3734: iconst_1
    //   3735: iadd
    //   3736: istore 18
    //   3738: goto -30 -> 3708
    //   3741: aload 7
    //   3743: astore 25
    //   3745: aload 7
    //   3747: iload 23
    //   3749: invokestatic 1384	org/telegram/messenger/MessagesController:getPeer	(I)Lorg/telegram/tgnet/TLRPC$Peer;
    //   3752: putfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3755: aload 38
    //   3757: astore 5
    //   3759: iload 23
    //   3761: ifle -372 -> 3389
    //   3764: aload 7
    //   3766: astore 25
    //   3768: invokestatic 740	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   3771: iload 23
    //   3773: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3776: invokevirtual 1298	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   3779: astore_1
    //   3780: aload_1
    //   3781: ifnonnull +17 -> 3798
    //   3784: aload 7
    //   3786: astore 25
    //   3788: aload_0
    //   3789: aload 7
    //   3791: getfield 1121	org/telegram/tgnet/TLRPC$Message:id	I
    //   3794: invokevirtual 1062	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   3797: return
    //   3798: aload 7
    //   3800: astore 25
    //   3802: aload 38
    //   3804: astore 5
    //   3806: aload_1
    //   3807: getfield 1386	org/telegram/tgnet/TLRPC$User:bot	Z
    //   3810: ifeq -421 -> 3389
    //   3813: aload 7
    //   3815: astore 25
    //   3817: aload 7
    //   3819: iconst_0
    //   3820: putfield 1109	org/telegram/tgnet/TLRPC$Message:unread	Z
    //   3823: aload 38
    //   3825: astore 5
    //   3827: goto -438 -> 3389
    //   3830: aload 7
    //   3832: astore 25
    //   3834: aload 7
    //   3836: new 1388	org/telegram/tgnet/TLRPC$TL_peerUser
    //   3839: dup
    //   3840: invokespecial 1389	org/telegram/tgnet/TLRPC$TL_peerUser:<init>	()V
    //   3843: putfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3846: aload 7
    //   3848: astore 25
    //   3850: aload 34
    //   3852: getfield 1392	org/telegram/tgnet/TLRPC$EncryptedChat:participant_id	I
    //   3855: invokestatic 1271	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   3858: if_icmpne +168 -> 4026
    //   3861: aload 7
    //   3863: astore 25
    //   3865: aload 7
    //   3867: getfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3870: aload 34
    //   3872: getfield 1395	org/telegram/tgnet/TLRPC$EncryptedChat:admin_id	I
    //   3875: putfield 1396	org/telegram/tgnet/TLRPC$Peer:user_id	I
    //   3878: aload 7
    //   3880: astore 25
    //   3882: aload 7
    //   3884: aload 34
    //   3886: getfield 1399	org/telegram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3889: putfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3892: aload 7
    //   3894: astore 25
    //   3896: aload 38
    //   3898: astore 5
    //   3900: aload 7
    //   3902: getfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3905: ifeq -516 -> 3389
    //   3908: aload 7
    //   3910: astore 25
    //   3912: aload 7
    //   3914: invokestatic 1318	org/telegram/messenger/MessageObject:isVoiceMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Z
    //   3917: ifeq +129 -> 4046
    //   3920: iconst_0
    //   3921: istore 21
    //   3923: iconst_0
    //   3924: istore 18
    //   3926: aload 7
    //   3928: astore 25
    //   3930: iload 21
    //   3932: istore 20
    //   3934: iload 18
    //   3936: aload 7
    //   3938: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3941: getfield 1142	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3944: getfield 1403	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3947: invokevirtual 931	java/util/ArrayList:size	()I
    //   3950: if_icmpge +48 -> 3998
    //   3953: aload 7
    //   3955: astore 25
    //   3957: aload 7
    //   3959: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3962: getfield 1142	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3965: getfield 1403	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3968: iload 18
    //   3970: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3973: checkcast 1231	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   3976: astore_1
    //   3977: aload 7
    //   3979: astore 25
    //   3981: aload_1
    //   3982: instanceof 758
    //   3985: ifeq +5391 -> 9376
    //   3988: aload 7
    //   3990: astore 25
    //   3992: aload_1
    //   3993: getfield 1404	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   3996: istore 20
    //   3998: aload 7
    //   4000: astore 25
    //   4002: aload 7
    //   4004: aload 34
    //   4006: getfield 1399	org/telegram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   4009: iload 20
    //   4011: iconst_1
    //   4012: iadd
    //   4013: invokestatic 1408	java/lang/Math:max	(II)I
    //   4016: putfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   4019: aload 38
    //   4021: astore 5
    //   4023: goto -634 -> 3389
    //   4026: aload 7
    //   4028: astore 25
    //   4030: aload 7
    //   4032: getfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   4035: aload 34
    //   4037: getfield 1392	org/telegram/tgnet/TLRPC$EncryptedChat:participant_id	I
    //   4040: putfield 1396	org/telegram/tgnet/TLRPC$Peer:user_id	I
    //   4043: goto -165 -> 3878
    //   4046: aload 7
    //   4048: astore 25
    //   4050: aload 38
    //   4052: astore 5
    //   4054: aload 7
    //   4056: invokestatic 1411	org/telegram/messenger/MessageObject:isVideoMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Z
    //   4059: ifeq -670 -> 3389
    //   4062: iconst_0
    //   4063: istore 21
    //   4065: iconst_0
    //   4066: istore 18
    //   4068: aload 7
    //   4070: astore 25
    //   4072: iload 21
    //   4074: istore 20
    //   4076: iload 18
    //   4078: aload 7
    //   4080: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4083: getfield 1142	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   4086: getfield 1403	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   4089: invokevirtual 931	java/util/ArrayList:size	()I
    //   4092: if_icmpge +48 -> 4140
    //   4095: aload 7
    //   4097: astore 25
    //   4099: aload 7
    //   4101: getfield 1125	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4104: getfield 1142	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   4107: getfield 1403	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   4110: iload 18
    //   4112: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4115: checkcast 1231	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   4118: astore_1
    //   4119: aload 7
    //   4121: astore 25
    //   4123: aload_1
    //   4124: instanceof 252
    //   4127: ifeq +41 -> 4168
    //   4130: aload 7
    //   4132: astore 25
    //   4134: aload_1
    //   4135: getfield 1404	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   4138: istore 20
    //   4140: aload 7
    //   4142: astore 25
    //   4144: aload 7
    //   4146: aload 34
    //   4148: getfield 1399	org/telegram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   4151: iload 20
    //   4153: iconst_1
    //   4154: iadd
    //   4155: invokestatic 1408	java/lang/Math:max	(II)I
    //   4158: putfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   4161: aload 38
    //   4163: astore 5
    //   4165: goto -776 -> 3389
    //   4168: iload 18
    //   4170: iconst_1
    //   4171: iadd
    //   4172: istore 18
    //   4174: goto -106 -> 4068
    //   4177: aload_1
    //   4178: aload 36
    //   4180: putfield 1412	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:message	Ljava/lang/String;
    //   4183: aload_1
    //   4184: aload 5
    //   4186: putfield 1415	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:contacts	Ljava/util/ArrayList;
    //   4189: aload_1
    //   4190: new 1417	org/telegram/tgnet/TLRPC$TL_inputMediaEmpty
    //   4193: dup
    //   4194: invokespecial 1418	org/telegram/tgnet/TLRPC$TL_inputMediaEmpty:<init>	()V
    //   4197: putfield 512	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   4200: aload_1
    //   4201: aload_2
    //   4202: putfield 1420	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:random_id	Ljava/util/ArrayList;
    //   4205: aload_0
    //   4206: aload_1
    //   4207: aload 6
    //   4209: aconst_null
    //   4210: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   4213: return
    //   4214: new 553	org/telegram/tgnet/TLRPC$TL_messages_sendMessage
    //   4217: dup
    //   4218: invokespecial 1423	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:<init>	()V
    //   4221: astore_1
    //   4222: aload_1
    //   4223: aload 36
    //   4225: putfield 1424	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:message	Ljava/lang/String;
    //   4228: aload 14
    //   4230: ifnonnull +5162 -> 9392
    //   4233: iconst_1
    //   4234: istore 24
    //   4236: aload_1
    //   4237: iload 24
    //   4239: putfield 1427	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:clear_draft	Z
    //   4242: aload 7
    //   4244: getfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   4247: instanceof 1429
    //   4250: ifeq +44 -> 4294
    //   4253: aload_1
    //   4254: getstatic 302	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   4257: ldc_w 1431
    //   4260: iconst_0
    //   4261: invokevirtual 1437	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   4264: new 452	java/lang/StringBuilder
    //   4267: dup
    //   4268: invokespecial 453	java/lang/StringBuilder:<init>	()V
    //   4271: ldc_w 1439
    //   4274: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4277: lload 8
    //   4279: invokevirtual 479	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   4282: invokevirtual 482	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4285: iconst_0
    //   4286: invokeinterface 1445 3 0
    //   4291: putfield 1448	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:silent	Z
    //   4294: aload_1
    //   4295: aload 32
    //   4297: putfield 1452	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   4300: aload_1
    //   4301: aload 7
    //   4303: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4306: putfield 1453	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:random_id	J
    //   4309: aload 11
    //   4311: ifnull +22 -> 4333
    //   4314: aload_1
    //   4315: aload_1
    //   4316: getfield 1454	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4319: iconst_1
    //   4320: ior
    //   4321: putfield 1454	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4324: aload_1
    //   4325: aload 11
    //   4327: invokevirtual 1054	org/telegram/messenger/MessageObject:getId	()I
    //   4330: putfield 1455	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:reply_to_msg_id	I
    //   4333: iload 13
    //   4335: ifne +8 -> 4343
    //   4338: aload_1
    //   4339: iconst_1
    //   4340: putfield 1456	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:no_webpage	Z
    //   4343: aload 15
    //   4345: ifnull +28 -> 4373
    //   4348: aload 15
    //   4350: invokevirtual 943	java/util/ArrayList:isEmpty	()Z
    //   4353: ifne +20 -> 4373
    //   4356: aload_1
    //   4357: aload 15
    //   4359: putfield 1457	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:entities	Ljava/util/ArrayList;
    //   4362: aload_1
    //   4363: aload_1
    //   4364: getfield 1454	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4367: bipush 8
    //   4369: ior
    //   4370: putfield 1454	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4373: aload_0
    //   4374: aload_1
    //   4375: aload 6
    //   4377: aconst_null
    //   4378: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   4381: aload 14
    //   4383: ifnonnull -4376 -> 7
    //   4386: lload 8
    //   4388: iconst_0
    //   4389: invokestatic 1463	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   4392: return
    //   4393: aload 34
    //   4395: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   4398: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   4401: bipush 46
    //   4403: if_icmplt +234 -> 4637
    //   4406: new 1465	org/telegram/tgnet/TLRPC$TL_decryptedMessage
    //   4409: dup
    //   4410: invokespecial 1466	org/telegram/tgnet/TLRPC$TL_decryptedMessage:<init>	()V
    //   4413: astore_2
    //   4414: aload_2
    //   4415: aload 7
    //   4417: getfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   4420: putfield 1467	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   4423: aload 15
    //   4425: ifnull +29 -> 4454
    //   4428: aload 15
    //   4430: invokevirtual 943	java/util/ArrayList:isEmpty	()Z
    //   4433: ifne +21 -> 4454
    //   4436: aload_2
    //   4437: aload 15
    //   4439: putfield 1468	org/telegram/tgnet/TLRPC$TL_decryptedMessage:entities	Ljava/util/ArrayList;
    //   4442: aload_2
    //   4443: aload_2
    //   4444: getfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4447: sipush 128
    //   4450: ior
    //   4451: putfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4454: aload_2
    //   4455: astore_1
    //   4456: aload 11
    //   4458: ifnull +43 -> 4501
    //   4461: aload_2
    //   4462: astore_1
    //   4463: aload 11
    //   4465: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4468: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4471: lconst_0
    //   4472: lcmp
    //   4473: ifeq +28 -> 4501
    //   4476: aload_2
    //   4477: aload 11
    //   4479: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4482: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4485: putfield 1470	org/telegram/tgnet/TLRPC$TL_decryptedMessage:reply_to_random_id	J
    //   4488: aload_2
    //   4489: aload_2
    //   4490: getfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4493: bipush 8
    //   4495: ior
    //   4496: putfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4499: aload_2
    //   4500: astore_1
    //   4501: aload 17
    //   4503: ifnull +41 -> 4544
    //   4506: aload 17
    //   4508: ldc_w 1091
    //   4511: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4514: ifnull +30 -> 4544
    //   4517: aload_1
    //   4518: aload 17
    //   4520: ldc_w 1091
    //   4523: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4526: checkcast 340	java/lang/String
    //   4529: putfield 1471	org/telegram/tgnet/TLRPC$TL_decryptedMessage:via_bot_name	Ljava/lang/String;
    //   4532: aload_1
    //   4533: aload_1
    //   4534: getfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4537: sipush 2048
    //   4540: ior
    //   4541: putfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4544: aload_1
    //   4545: aload 7
    //   4547: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4550: putfield 1472	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_id	J
    //   4553: aload_1
    //   4554: aload 36
    //   4556: putfield 1473	org/telegram/tgnet/TLRPC$TL_decryptedMessage:message	Ljava/lang/String;
    //   4559: aload 37
    //   4561: ifnull +138 -> 4699
    //   4564: aload 37
    //   4566: getfield 1163	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   4569: ifnull +130 -> 4699
    //   4572: aload_1
    //   4573: new 1475	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaWebPage
    //   4576: dup
    //   4577: invokespecial 1476	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaWebPage:<init>	()V
    //   4580: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   4583: aload_1
    //   4584: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   4587: aload 37
    //   4589: getfield 1163	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   4592: putfield 1482	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:url	Ljava/lang/String;
    //   4595: aload_1
    //   4596: aload_1
    //   4597: getfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4600: sipush 512
    //   4603: ior
    //   4604: putfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4607: invokestatic 1487	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   4610: aload_1
    //   4611: aload 6
    //   4613: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4616: aload 34
    //   4618: aconst_null
    //   4619: aconst_null
    //   4620: aload 6
    //   4622: invokevirtual 1491	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   4625: aload 14
    //   4627: ifnonnull -4620 -> 7
    //   4630: lload 8
    //   4632: iconst_0
    //   4633: invokestatic 1463	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   4636: return
    //   4637: aload 34
    //   4639: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   4642: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   4645: bipush 17
    //   4647: if_icmplt +23 -> 4670
    //   4650: new 1493	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17
    //   4653: dup
    //   4654: invokespecial 1494	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17:<init>	()V
    //   4657: astore_1
    //   4658: aload_1
    //   4659: aload 7
    //   4661: getfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   4664: putfield 1467	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   4667: goto -166 -> 4501
    //   4670: new 1496	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8
    //   4673: dup
    //   4674: invokespecial 1497	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8:<init>	()V
    //   4677: astore_1
    //   4678: aload_1
    //   4679: bipush 15
    //   4681: newarray <illegal type>
    //   4683: putfield 1501	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   4686: getstatic 1372	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   4689: aload_1
    //   4690: getfield 1501	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   4693: invokevirtual 1505	java/security/SecureRandom:nextBytes	([B)V
    //   4696: goto -195 -> 4501
    //   4699: aload_1
    //   4700: new 1507	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaEmpty
    //   4703: dup
    //   4704: invokespecial 1508	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaEmpty:<init>	()V
    //   4707: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   4710: goto -103 -> 4607
    //   4713: aload 34
    //   4715: ifnonnull +1553 -> 6268
    //   4718: aconst_null
    //   4719: astore_1
    //   4720: aconst_null
    //   4721: astore 15
    //   4723: aconst_null
    //   4724: astore 12
    //   4726: iload 19
    //   4728: iconst_1
    //   4729: if_icmpne +4709 -> 9438
    //   4732: aload_2
    //   4733: instanceof 615
    //   4736: ifeq +145 -> 4881
    //   4739: new 1510	org/telegram/tgnet/TLRPC$TL_inputMediaVenue
    //   4742: dup
    //   4743: invokespecial 1511	org/telegram/tgnet/TLRPC$TL_inputMediaVenue:<init>	()V
    //   4746: astore_1
    //   4747: aload_1
    //   4748: aload_2
    //   4749: getfield 1512	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   4752: putfield 1513	org/telegram/tgnet/TLRPC$InputMedia:address	Ljava/lang/String;
    //   4755: aload_1
    //   4756: aload_2
    //   4757: getfield 1514	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   4760: putfield 1515	org/telegram/tgnet/TLRPC$InputMedia:title	Ljava/lang/String;
    //   4763: aload_1
    //   4764: aload_2
    //   4765: getfield 1516	org/telegram/tgnet/TLRPC$MessageMedia:provider	Ljava/lang/String;
    //   4768: putfield 1517	org/telegram/tgnet/TLRPC$InputMedia:provider	Ljava/lang/String;
    //   4771: aload_1
    //   4772: aload_2
    //   4773: getfield 1518	org/telegram/tgnet/TLRPC$MessageMedia:venue_id	Ljava/lang/String;
    //   4776: putfield 1519	org/telegram/tgnet/TLRPC$InputMedia:venue_id	Ljava/lang/String;
    //   4779: aload_1
    //   4780: new 1521	org/telegram/tgnet/TLRPC$TL_inputGeoPoint
    //   4783: dup
    //   4784: invokespecial 1522	org/telegram/tgnet/TLRPC$TL_inputGeoPoint:<init>	()V
    //   4787: putfield 1526	org/telegram/tgnet/TLRPC$InputMedia:geo_point	Lorg/telegram/tgnet/TLRPC$InputGeoPoint;
    //   4790: aload_1
    //   4791: getfield 1526	org/telegram/tgnet/TLRPC$InputMedia:geo_point	Lorg/telegram/tgnet/TLRPC$InputGeoPoint;
    //   4794: aload_2
    //   4795: getfield 1527	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   4798: getfield 1001	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   4801: putfield 1530	org/telegram/tgnet/TLRPC$InputGeoPoint:lat	D
    //   4804: aload_1
    //   4805: getfield 1526	org/telegram/tgnet/TLRPC$InputMedia:geo_point	Lorg/telegram/tgnet/TLRPC$InputGeoPoint;
    //   4808: aload_2
    //   4809: getfield 1527	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   4812: getfield 1007	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   4815: putfield 1531	org/telegram/tgnet/TLRPC$InputGeoPoint:_long	D
    //   4818: aload 12
    //   4820: astore_2
    //   4821: aload 5
    //   4823: ifnull +1169 -> 5992
    //   4826: new 511	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast
    //   4829: dup
    //   4830: invokespecial 1368	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:<init>	()V
    //   4833: astore 4
    //   4835: new 675	java/util/ArrayList
    //   4838: dup
    //   4839: invokespecial 676	java/util/ArrayList:<init>	()V
    //   4842: astore 10
    //   4844: iconst_0
    //   4845: istore 18
    //   4847: iload 18
    //   4849: aload 5
    //   4851: invokevirtual 931	java/util/ArrayList:size	()I
    //   4854: if_icmpge +1064 -> 5918
    //   4857: aload 10
    //   4859: getstatic 1372	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   4862: invokevirtual 1377	java/security/SecureRandom:nextLong	()J
    //   4865: invokestatic 1380	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   4868: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   4871: pop
    //   4872: iload 18
    //   4874: iconst_1
    //   4875: iadd
    //   4876: istore 18
    //   4878: goto -31 -> 4847
    //   4881: new 1533	org/telegram/tgnet/TLRPC$TL_inputMediaGeoPoint
    //   4884: dup
    //   4885: invokespecial 1534	org/telegram/tgnet/TLRPC$TL_inputMediaGeoPoint:<init>	()V
    //   4888: astore_1
    //   4889: goto -110 -> 4779
    //   4892: aload_3
    //   4893: getfield 1535	org/telegram/tgnet/TLRPC$TL_photo:access_hash	J
    //   4896: lconst_0
    //   4897: lcmp
    //   4898: ifne +214 -> 5112
    //   4901: new 1537	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedPhoto
    //   4904: dup
    //   4905: invokespecial 1538	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedPhoto:<init>	()V
    //   4908: astore 4
    //   4910: aload_3
    //   4911: getfield 1199	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4914: ifnull +4544 -> 9458
    //   4917: aload_3
    //   4918: getfield 1199	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4921: astore_1
    //   4922: aload 4
    //   4924: aload_1
    //   4925: putfield 1539	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   4928: aload 17
    //   4930: ifnull +88 -> 5018
    //   4933: aload 17
    //   4935: ldc_w 1541
    //   4938: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4941: checkcast 340	java/lang/String
    //   4944: astore_1
    //   4945: aload_1
    //   4946: ifnull +72 -> 5018
    //   4949: new 1543	org/telegram/tgnet/SerializedData
    //   4952: dup
    //   4953: aload_1
    //   4954: invokestatic 1547	org/telegram/messenger/Utilities:hexToBytes	(Ljava/lang/String;)[B
    //   4957: invokespecial 1549	org/telegram/tgnet/SerializedData:<init>	([B)V
    //   4960: astore_1
    //   4961: aload_1
    //   4962: iconst_0
    //   4963: invokevirtual 1553	org/telegram/tgnet/SerializedData:readInt32	(Z)I
    //   4966: istore 20
    //   4968: iconst_0
    //   4969: istore 18
    //   4971: iload 18
    //   4973: iload 20
    //   4975: if_icmpge +31 -> 5006
    //   4978: aload 4
    //   4980: getfield 1556	org/telegram/tgnet/TLRPC$InputMedia:stickers	Ljava/util/ArrayList;
    //   4983: aload_1
    //   4984: aload_1
    //   4985: iconst_0
    //   4986: invokevirtual 1553	org/telegram/tgnet/SerializedData:readInt32	(Z)I
    //   4989: iconst_0
    //   4990: invokestatic 1562	org/telegram/tgnet/TLRPC$InputDocument:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   4993: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   4996: pop
    //   4997: iload 18
    //   4999: iconst_1
    //   5000: iadd
    //   5001: istore 18
    //   5003: goto -32 -> 4971
    //   5006: aload 4
    //   5008: aload 4
    //   5010: getfield 1563	org/telegram/tgnet/TLRPC$InputMedia:flags	I
    //   5013: iconst_1
    //   5014: ior
    //   5015: putfield 1563	org/telegram/tgnet/TLRPC$InputMedia:flags	I
    //   5018: new 98	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   5021: dup
    //   5022: aload_0
    //   5023: invokespecial 1564	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   5026: astore_2
    //   5027: aload_2
    //   5028: aload 31
    //   5030: putfield 1566	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   5033: aload_2
    //   5034: iconst_0
    //   5035: putfield 365	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   5038: aload_2
    //   5039: aload 6
    //   5041: putfield 439	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   5044: aload 10
    //   5046: ifnull +34 -> 5080
    //   5049: aload 10
    //   5051: invokevirtual 347	java/lang/String:length	()I
    //   5054: ifle +26 -> 5080
    //   5057: aload 10
    //   5059: ldc_w 1204
    //   5062: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   5065: ifeq +15 -> 5080
    //   5068: aload_2
    //   5069: aload 10
    //   5071: putfield 369	org/telegram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   5074: aload 4
    //   5076: astore_1
    //   5077: goto -256 -> 4821
    //   5080: aload_2
    //   5081: aload_3
    //   5082: getfield 1207	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   5085: aload_3
    //   5086: getfield 1207	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   5089: invokevirtual 931	java/util/ArrayList:size	()I
    //   5092: iconst_1
    //   5093: isub
    //   5094: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5097: checkcast 904	org/telegram/tgnet/TLRPC$PhotoSize
    //   5100: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5103: putfield 392	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5106: aload 4
    //   5108: astore_1
    //   5109: goto -288 -> 4821
    //   5112: new 1568	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto
    //   5115: dup
    //   5116: invokespecial 1569	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:<init>	()V
    //   5119: astore_2
    //   5120: aload_2
    //   5121: new 1571	org/telegram/tgnet/TLRPC$TL_inputPhoto
    //   5124: dup
    //   5125: invokespecial 1572	org/telegram/tgnet/TLRPC$TL_inputPhoto:<init>	()V
    //   5128: putfield 1575	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/telegram/tgnet/TLRPC$InputPhoto;
    //   5131: aload_3
    //   5132: getfield 1199	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   5135: ifnull +4330 -> 9465
    //   5138: aload_3
    //   5139: getfield 1199	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   5142: astore_1
    //   5143: aload_2
    //   5144: aload_1
    //   5145: putfield 1576	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:caption	Ljava/lang/String;
    //   5148: aload_2
    //   5149: getfield 1575	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/telegram/tgnet/TLRPC$InputPhoto;
    //   5152: aload_3
    //   5153: getfield 1577	org/telegram/tgnet/TLRPC$TL_photo:id	J
    //   5156: putfield 1580	org/telegram/tgnet/TLRPC$InputPhoto:id	J
    //   5159: aload_2
    //   5160: getfield 1575	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/telegram/tgnet/TLRPC$InputPhoto;
    //   5163: aload_3
    //   5164: getfield 1535	org/telegram/tgnet/TLRPC$TL_photo:access_hash	J
    //   5167: putfield 1581	org/telegram/tgnet/TLRPC$InputPhoto:access_hash	J
    //   5170: aload_2
    //   5171: astore_1
    //   5172: aload 12
    //   5174: astore_2
    //   5175: goto -354 -> 4821
    //   5178: iload 19
    //   5180: iconst_3
    //   5181: if_icmpne +203 -> 5384
    //   5184: aload 33
    //   5186: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5189: lconst_0
    //   5190: lcmp
    //   5191: ifne +123 -> 5314
    //   5194: aload 33
    //   5196: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5199: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5202: ifnull +101 -> 5303
    //   5205: new 1584	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument
    //   5208: dup
    //   5209: invokespecial 1585	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument:<init>	()V
    //   5212: astore_1
    //   5213: aload 33
    //   5215: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5218: ifnull +4254 -> 9472
    //   5221: aload 33
    //   5223: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5226: astore_2
    //   5227: aload_1
    //   5228: aload_2
    //   5229: putfield 1539	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   5232: aload_1
    //   5233: aload 33
    //   5235: getfield 809	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   5238: putfield 1586	org/telegram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   5241: aload_1
    //   5242: aload 33
    //   5244: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   5247: putfield 1587	org/telegram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   5250: new 98	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   5253: dup
    //   5254: aload_0
    //   5255: invokespecial 1564	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   5258: astore_2
    //   5259: aload_2
    //   5260: aload 31
    //   5262: putfield 1566	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   5265: aload_2
    //   5266: iconst_1
    //   5267: putfield 365	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   5270: aload_2
    //   5271: aload 6
    //   5273: putfield 439	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   5276: aload_2
    //   5277: aload 33
    //   5279: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5282: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5285: putfield 392	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5288: aload_2
    //   5289: aload 33
    //   5291: putfield 470	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   5294: aload_2
    //   5295: aload 4
    //   5297: putfield 435	org/telegram/messenger/SendMessagesHelper$DelayedMessage:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   5300: goto -479 -> 4821
    //   5303: new 1589	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   5306: dup
    //   5307: invokespecial 1590	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   5310: astore_1
    //   5311: goto -98 -> 5213
    //   5314: new 1592	org/telegram/tgnet/TLRPC$TL_inputMediaDocument
    //   5317: dup
    //   5318: invokespecial 1593	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   5321: astore_2
    //   5322: aload_2
    //   5323: new 1595	org/telegram/tgnet/TLRPC$TL_inputDocument
    //   5326: dup
    //   5327: invokespecial 1596	org/telegram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   5330: putfield 1599	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5333: aload 33
    //   5335: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5338: ifnull +4141 -> 9479
    //   5341: aload 33
    //   5343: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5346: astore_1
    //   5347: aload_2
    //   5348: aload_1
    //   5349: putfield 1600	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   5352: aload_2
    //   5353: getfield 1599	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5356: aload 33
    //   5358: getfield 476	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   5361: putfield 1601	org/telegram/tgnet/TLRPC$InputDocument:id	J
    //   5364: aload_2
    //   5365: getfield 1599	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5368: aload 33
    //   5370: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5373: putfield 1602	org/telegram/tgnet/TLRPC$InputDocument:access_hash	J
    //   5376: aload_2
    //   5377: astore_1
    //   5378: aload 12
    //   5380: astore_2
    //   5381: goto -560 -> 4821
    //   5384: iload 19
    //   5386: bipush 6
    //   5388: if_icmpne +4098 -> 9486
    //   5391: new 1604	org/telegram/tgnet/TLRPC$TL_inputMediaContact
    //   5394: dup
    //   5395: invokespecial 1605	org/telegram/tgnet/TLRPC$TL_inputMediaContact:<init>	()V
    //   5398: astore_1
    //   5399: aload_1
    //   5400: aload 35
    //   5402: getfield 659	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   5405: putfield 1606	org/telegram/tgnet/TLRPC$InputMedia:phone_number	Ljava/lang/String;
    //   5408: aload_1
    //   5409: aload 35
    //   5411: getfield 663	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   5414: putfield 1607	org/telegram/tgnet/TLRPC$InputMedia:first_name	Ljava/lang/String;
    //   5417: aload_1
    //   5418: aload 35
    //   5420: getfield 667	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   5423: putfield 1608	org/telegram/tgnet/TLRPC$InputMedia:last_name	Ljava/lang/String;
    //   5426: aload 12
    //   5428: astore_2
    //   5429: goto -608 -> 4821
    //   5432: aload 33
    //   5434: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5437: lconst_0
    //   5438: lcmp
    //   5439: ifne +238 -> 5677
    //   5442: aload 34
    //   5444: ifnonnull +142 -> 5586
    //   5447: aload 31
    //   5449: ifnull +137 -> 5586
    //   5452: aload 31
    //   5454: invokevirtual 347	java/lang/String:length	()I
    //   5457: ifle +129 -> 5586
    //   5460: aload 31
    //   5462: ldc_w 1204
    //   5465: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   5468: ifeq +118 -> 5586
    //   5471: aload 17
    //   5473: ifnull +113 -> 5586
    //   5476: new 1610	org/telegram/tgnet/TLRPC$TL_inputMediaGifExternal
    //   5479: dup
    //   5480: invokespecial 1611	org/telegram/tgnet/TLRPC$TL_inputMediaGifExternal:<init>	()V
    //   5483: astore 4
    //   5485: aload 17
    //   5487: ldc_w 1612
    //   5490: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5493: checkcast 340	java/lang/String
    //   5496: ldc_w 1614
    //   5499: invokevirtual 1618	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   5502: astore 10
    //   5504: aload 15
    //   5506: astore_2
    //   5507: aload 4
    //   5509: astore_1
    //   5510: aload 10
    //   5512: arraylength
    //   5513: iconst_2
    //   5514: if_icmpne +30 -> 5544
    //   5517: aload 4
    //   5519: checkcast 1610	org/telegram/tgnet/TLRPC$TL_inputMediaGifExternal
    //   5522: aload 10
    //   5524: iconst_0
    //   5525: aaload
    //   5526: putfield 1619	org/telegram/tgnet/TLRPC$TL_inputMediaGifExternal:url	Ljava/lang/String;
    //   5529: aload 4
    //   5531: aload 10
    //   5533: iconst_1
    //   5534: aaload
    //   5535: putfield 1622	org/telegram/tgnet/TLRPC$InputMedia:q	Ljava/lang/String;
    //   5538: aload 4
    //   5540: astore_1
    //   5541: aload 15
    //   5543: astore_2
    //   5544: aload_1
    //   5545: aload 33
    //   5547: getfield 809	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   5550: putfield 1586	org/telegram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   5553: aload_1
    //   5554: aload 33
    //   5556: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   5559: putfield 1587	org/telegram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   5562: aload 33
    //   5564: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5567: ifnull +3936 -> 9503
    //   5570: aload 33
    //   5572: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5575: astore 4
    //   5577: aload_1
    //   5578: aload 4
    //   5580: putfield 1539	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   5583: goto -762 -> 4821
    //   5586: aload 33
    //   5588: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5591: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5594: ifnull +72 -> 5666
    //   5597: aload 33
    //   5599: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5602: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5605: instanceof 1624
    //   5608: ifeq +58 -> 5666
    //   5611: new 1584	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument
    //   5614: dup
    //   5615: invokespecial 1585	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument:<init>	()V
    //   5618: astore_1
    //   5619: new 98	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   5622: dup
    //   5623: aload_0
    //   5624: invokespecial 1564	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   5627: astore_2
    //   5628: aload_2
    //   5629: aload 31
    //   5631: putfield 1566	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   5634: aload_2
    //   5635: iconst_2
    //   5636: putfield 365	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   5639: aload_2
    //   5640: aload 6
    //   5642: putfield 439	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   5645: aload_2
    //   5646: aload 33
    //   5648: putfield 470	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   5651: aload_2
    //   5652: aload 33
    //   5654: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5657: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5660: putfield 392	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5663: goto -119 -> 5544
    //   5666: new 1589	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   5669: dup
    //   5670: invokespecial 1590	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   5673: astore_1
    //   5674: goto -55 -> 5619
    //   5677: new 1592	org/telegram/tgnet/TLRPC$TL_inputMediaDocument
    //   5680: dup
    //   5681: invokespecial 1593	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   5684: astore_2
    //   5685: aload_2
    //   5686: new 1595	org/telegram/tgnet/TLRPC$TL_inputDocument
    //   5689: dup
    //   5690: invokespecial 1596	org/telegram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   5693: putfield 1599	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5696: aload_2
    //   5697: getfield 1599	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5700: aload 33
    //   5702: getfield 476	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   5705: putfield 1601	org/telegram/tgnet/TLRPC$InputDocument:id	J
    //   5708: aload_2
    //   5709: getfield 1599	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5712: aload 33
    //   5714: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5717: putfield 1602	org/telegram/tgnet/TLRPC$InputDocument:access_hash	J
    //   5720: aload 33
    //   5722: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5725: ifnull +3786 -> 9511
    //   5728: aload 33
    //   5730: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5733: astore_1
    //   5734: aload_2
    //   5735: aload_1
    //   5736: putfield 1600	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   5739: aload_2
    //   5740: astore_1
    //   5741: aload 12
    //   5743: astore_2
    //   5744: goto -923 -> 4821
    //   5747: aload 12
    //   5749: astore_2
    //   5750: iload 19
    //   5752: bipush 8
    //   5754: if_icmpne -933 -> 4821
    //   5757: aload 33
    //   5759: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5762: lconst_0
    //   5763: lcmp
    //   5764: ifne +84 -> 5848
    //   5767: new 1589	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   5770: dup
    //   5771: invokespecial 1590	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   5774: astore 4
    //   5776: aload 4
    //   5778: aload 33
    //   5780: getfield 809	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   5783: putfield 1586	org/telegram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   5786: aload 4
    //   5788: aload 33
    //   5790: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   5793: putfield 1587	org/telegram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   5796: aload 33
    //   5798: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5801: ifnull +3717 -> 9518
    //   5804: aload 33
    //   5806: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5809: astore_1
    //   5810: aload 4
    //   5812: aload_1
    //   5813: putfield 1539	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   5816: new 98	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   5819: dup
    //   5820: aload_0
    //   5821: invokespecial 1564	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   5824: astore_2
    //   5825: aload_2
    //   5826: iconst_3
    //   5827: putfield 365	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   5830: aload_2
    //   5831: aload 6
    //   5833: putfield 439	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   5836: aload_2
    //   5837: aload 33
    //   5839: putfield 470	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   5842: aload 4
    //   5844: astore_1
    //   5845: goto -1024 -> 4821
    //   5848: new 1592	org/telegram/tgnet/TLRPC$TL_inputMediaDocument
    //   5851: dup
    //   5852: invokespecial 1593	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   5855: astore_2
    //   5856: aload_2
    //   5857: new 1595	org/telegram/tgnet/TLRPC$TL_inputDocument
    //   5860: dup
    //   5861: invokespecial 1596	org/telegram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   5864: putfield 1599	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5867: aload 33
    //   5869: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5872: ifnull +3653 -> 9525
    //   5875: aload 33
    //   5877: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5880: astore_1
    //   5881: aload_2
    //   5882: aload_1
    //   5883: putfield 1600	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   5886: aload_2
    //   5887: getfield 1599	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5890: aload 33
    //   5892: getfield 476	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   5895: putfield 1601	org/telegram/tgnet/TLRPC$InputDocument:id	J
    //   5898: aload_2
    //   5899: getfield 1599	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5902: aload 33
    //   5904: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5907: putfield 1602	org/telegram/tgnet/TLRPC$InputDocument:access_hash	J
    //   5910: aload_2
    //   5911: astore_1
    //   5912: aload 12
    //   5914: astore_2
    //   5915: goto -1094 -> 4821
    //   5918: aload 4
    //   5920: aload 5
    //   5922: putfield 1415	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:contacts	Ljava/util/ArrayList;
    //   5925: aload 4
    //   5927: aload_1
    //   5928: putfield 512	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   5931: aload 4
    //   5933: aload 10
    //   5935: putfield 1420	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:random_id	Ljava/util/ArrayList;
    //   5938: aload 4
    //   5940: ldc_w 713
    //   5943: putfield 1412	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:message	Ljava/lang/String;
    //   5946: aload_2
    //   5947: ifnull +9 -> 5956
    //   5950: aload_2
    //   5951: aload 4
    //   5953: putfield 388	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendRequest	Lorg/telegram/tgnet/TLObject;
    //   5956: aload 4
    //   5958: astore_1
    //   5959: aload_1
    //   5960: astore 4
    //   5962: aload 14
    //   5964: ifnonnull +12 -> 5976
    //   5967: lload 8
    //   5969: iconst_0
    //   5970: invokestatic 1463	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   5973: aload_1
    //   5974: astore 4
    //   5976: iload 19
    //   5978: iconst_1
    //   5979: if_icmpne +138 -> 6117
    //   5982: aload_0
    //   5983: aload 4
    //   5985: aload 6
    //   5987: aconst_null
    //   5988: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5991: return
    //   5992: new 493	org/telegram/tgnet/TLRPC$TL_messages_sendMedia
    //   5995: dup
    //   5996: invokespecial 1625	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:<init>	()V
    //   5999: astore 4
    //   6001: aload 4
    //   6003: aload 32
    //   6005: putfield 1626	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   6008: aload 7
    //   6010: getfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   6013: instanceof 1429
    //   6016: ifeq +45 -> 6061
    //   6019: aload 4
    //   6021: getstatic 302	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   6024: ldc_w 1431
    //   6027: iconst_0
    //   6028: invokevirtual 1437	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   6031: new 452	java/lang/StringBuilder
    //   6034: dup
    //   6035: invokespecial 453	java/lang/StringBuilder:<init>	()V
    //   6038: ldc_w 1439
    //   6041: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6044: lload 8
    //   6046: invokevirtual 479	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   6049: invokevirtual 482	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   6052: iconst_0
    //   6053: invokeinterface 1445 3 0
    //   6058: putfield 1627	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:silent	Z
    //   6061: aload 4
    //   6063: aload 7
    //   6065: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6068: putfield 1628	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:random_id	J
    //   6071: aload 4
    //   6073: aload_1
    //   6074: putfield 497	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   6077: aload 11
    //   6079: ifnull +25 -> 6104
    //   6082: aload 4
    //   6084: aload 4
    //   6086: getfield 1629	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:flags	I
    //   6089: iconst_1
    //   6090: ior
    //   6091: putfield 1629	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:flags	I
    //   6094: aload 4
    //   6096: aload 11
    //   6098: invokevirtual 1054	org/telegram/messenger/MessageObject:getId	()I
    //   6101: putfield 1630	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:reply_to_msg_id	I
    //   6104: aload_2
    //   6105: ifnull +3427 -> 9532
    //   6108: aload_2
    //   6109: aload 4
    //   6111: putfield 388	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendRequest	Lorg/telegram/tgnet/TLObject;
    //   6114: goto +3418 -> 9532
    //   6117: iload 19
    //   6119: iconst_2
    //   6120: if_icmpne +28 -> 6148
    //   6123: aload_3
    //   6124: getfield 1535	org/telegram/tgnet/TLRPC$TL_photo:access_hash	J
    //   6127: lconst_0
    //   6128: lcmp
    //   6129: ifne +9 -> 6138
    //   6132: aload_0
    //   6133: aload_2
    //   6134: invokespecial 224	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   6137: return
    //   6138: aload_0
    //   6139: aload 4
    //   6141: aload 6
    //   6143: aconst_null
    //   6144: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   6147: return
    //   6148: iload 19
    //   6150: iconst_3
    //   6151: if_icmpne +29 -> 6180
    //   6154: aload 33
    //   6156: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   6159: lconst_0
    //   6160: lcmp
    //   6161: ifne +9 -> 6170
    //   6164: aload_0
    //   6165: aload_2
    //   6166: invokespecial 224	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   6169: return
    //   6170: aload_0
    //   6171: aload 4
    //   6173: aload 6
    //   6175: aconst_null
    //   6176: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   6179: return
    //   6180: iload 19
    //   6182: bipush 6
    //   6184: if_icmpne +13 -> 6197
    //   6187: aload_0
    //   6188: aload 4
    //   6190: aload 6
    //   6192: aconst_null
    //   6193: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   6196: return
    //   6197: iload 19
    //   6199: bipush 7
    //   6201: if_icmpne +34 -> 6235
    //   6204: aload 33
    //   6206: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   6209: lconst_0
    //   6210: lcmp
    //   6211: ifne +13 -> 6224
    //   6214: aload_2
    //   6215: ifnull +9 -> 6224
    //   6218: aload_0
    //   6219: aload_2
    //   6220: invokespecial 224	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   6223: return
    //   6224: aload_0
    //   6225: aload 4
    //   6227: aload 6
    //   6229: aload 31
    //   6231: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   6234: return
    //   6235: iload 19
    //   6237: bipush 8
    //   6239: if_icmpne -6232 -> 7
    //   6242: aload 33
    //   6244: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   6247: lconst_0
    //   6248: lcmp
    //   6249: ifne +9 -> 6258
    //   6252: aload_0
    //   6253: aload_2
    //   6254: invokespecial 224	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   6257: return
    //   6258: aload_0
    //   6259: aload 4
    //   6261: aload 6
    //   6263: aconst_null
    //   6264: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   6267: return
    //   6268: aload 34
    //   6270: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6273: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6276: bipush 46
    //   6278: if_icmplt +302 -> 6580
    //   6281: new 1465	org/telegram/tgnet/TLRPC$TL_decryptedMessage
    //   6284: dup
    //   6285: invokespecial 1466	org/telegram/tgnet/TLRPC$TL_decryptedMessage:<init>	()V
    //   6288: astore_1
    //   6289: aload_1
    //   6290: aload 7
    //   6292: getfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   6295: putfield 1467	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   6298: aload 15
    //   6300: ifnull +29 -> 6329
    //   6303: aload 15
    //   6305: invokevirtual 943	java/util/ArrayList:isEmpty	()Z
    //   6308: ifne +21 -> 6329
    //   6311: aload_1
    //   6312: aload 15
    //   6314: putfield 1468	org/telegram/tgnet/TLRPC$TL_decryptedMessage:entities	Ljava/util/ArrayList;
    //   6317: aload_1
    //   6318: aload_1
    //   6319: getfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6322: sipush 128
    //   6325: ior
    //   6326: putfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6329: aload 11
    //   6331: ifnull +39 -> 6370
    //   6334: aload 11
    //   6336: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6339: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6342: lconst_0
    //   6343: lcmp
    //   6344: ifeq +26 -> 6370
    //   6347: aload_1
    //   6348: aload 11
    //   6350: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6353: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6356: putfield 1470	org/telegram/tgnet/TLRPC$TL_decryptedMessage:reply_to_random_id	J
    //   6359: aload_1
    //   6360: aload_1
    //   6361: getfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6364: bipush 8
    //   6366: ior
    //   6367: putfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6370: aload_1
    //   6371: aload_1
    //   6372: getfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6375: sipush 512
    //   6378: ior
    //   6379: putfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6382: aload 17
    //   6384: ifnull +41 -> 6425
    //   6387: aload 17
    //   6389: ldc_w 1091
    //   6392: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   6395: ifnull +30 -> 6425
    //   6398: aload_1
    //   6399: aload 17
    //   6401: ldc_w 1091
    //   6404: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   6407: checkcast 340	java/lang/String
    //   6410: putfield 1471	org/telegram/tgnet/TLRPC$TL_decryptedMessage:via_bot_name	Ljava/lang/String;
    //   6413: aload_1
    //   6414: aload_1
    //   6415: getfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6418: sipush 2048
    //   6421: ior
    //   6422: putfield 1469	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6425: aload_1
    //   6426: aload 7
    //   6428: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6431: putfield 1472	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_id	J
    //   6434: aload_1
    //   6435: ldc_w 713
    //   6438: putfield 1473	org/telegram/tgnet/TLRPC$TL_decryptedMessage:message	Ljava/lang/String;
    //   6441: iload 19
    //   6443: iconst_1
    //   6444: if_icmpne +3091 -> 9535
    //   6447: aload_2
    //   6448: instanceof 615
    //   6451: ifeq +191 -> 6642
    //   6454: aload 34
    //   6456: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6459: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6462: bipush 46
    //   6464: if_icmplt +178 -> 6642
    //   6467: aload_1
    //   6468: new 1632	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVenue
    //   6471: dup
    //   6472: invokespecial 1633	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVenue:<init>	()V
    //   6475: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6478: aload_1
    //   6479: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6482: aload_2
    //   6483: getfield 1512	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   6486: putfield 1634	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:address	Ljava/lang/String;
    //   6489: aload_1
    //   6490: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6493: aload_2
    //   6494: getfield 1514	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   6497: putfield 1635	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:title	Ljava/lang/String;
    //   6500: aload_1
    //   6501: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6504: aload_2
    //   6505: getfield 1516	org/telegram/tgnet/TLRPC$MessageMedia:provider	Ljava/lang/String;
    //   6508: putfield 1636	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:provider	Ljava/lang/String;
    //   6511: aload_1
    //   6512: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6515: aload_2
    //   6516: getfield 1518	org/telegram/tgnet/TLRPC$MessageMedia:venue_id	Ljava/lang/String;
    //   6519: putfield 1637	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:venue_id	Ljava/lang/String;
    //   6522: aload_1
    //   6523: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6526: aload_2
    //   6527: getfield 1527	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   6530: getfield 1001	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   6533: putfield 1638	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:lat	D
    //   6536: aload_1
    //   6537: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6540: aload_2
    //   6541: getfield 1527	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   6544: getfield 1007	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   6547: putfield 1639	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:_long	D
    //   6550: invokestatic 1487	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   6553: aload_1
    //   6554: aload 6
    //   6556: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6559: aload 34
    //   6561: aconst_null
    //   6562: aconst_null
    //   6563: aload 6
    //   6565: invokevirtual 1491	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   6568: aload 14
    //   6570: ifnonnull -6563 -> 7
    //   6573: lload 8
    //   6575: iconst_0
    //   6576: invokestatic 1463	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   6579: return
    //   6580: aload 34
    //   6582: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6585: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6588: bipush 17
    //   6590: if_icmplt +23 -> 6613
    //   6593: new 1493	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17
    //   6596: dup
    //   6597: invokespecial 1494	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17:<init>	()V
    //   6600: astore_1
    //   6601: aload_1
    //   6602: aload 7
    //   6604: getfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   6607: putfield 1467	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   6610: goto -228 -> 6382
    //   6613: new 1496	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8
    //   6616: dup
    //   6617: invokespecial 1497	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8:<init>	()V
    //   6620: astore_1
    //   6621: aload_1
    //   6622: bipush 15
    //   6624: newarray <illegal type>
    //   6626: putfield 1501	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   6629: getstatic 1372	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   6632: aload_1
    //   6633: getfield 1501	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   6636: invokevirtual 1505	java/security/SecureRandom:nextBytes	([B)V
    //   6639: goto -257 -> 6382
    //   6642: aload_1
    //   6643: new 1641	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaGeoPoint
    //   6646: dup
    //   6647: invokespecial 1642	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaGeoPoint:<init>	()V
    //   6650: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6653: goto -131 -> 6522
    //   6656: aload_3
    //   6657: getfield 1207	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6660: iconst_0
    //   6661: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6664: checkcast 904	org/telegram/tgnet/TLRPC$PhotoSize
    //   6667: astore 5
    //   6669: aload_3
    //   6670: getfield 1207	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6673: aload_3
    //   6674: getfield 1207	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6677: invokevirtual 931	java/util/ArrayList:size	()I
    //   6680: iconst_1
    //   6681: isub
    //   6682: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6685: checkcast 904	org/telegram/tgnet/TLRPC$PhotoSize
    //   6688: astore 4
    //   6690: aload 5
    //   6692: invokestatic 1646	org/telegram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/telegram/tgnet/TLRPC$PhotoSize;)V
    //   6695: aload 34
    //   6697: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6700: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6703: bipush 46
    //   6705: if_icmplt +223 -> 6928
    //   6708: aload_1
    //   6709: new 1648	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   6712: dup
    //   6713: invokespecial 1649	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:<init>	()V
    //   6716: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6719: aload_1
    //   6720: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6723: astore 11
    //   6725: aload_3
    //   6726: getfield 1199	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   6729: ifnull +2826 -> 9555
    //   6732: aload_3
    //   6733: getfield 1199	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   6736: astore_2
    //   6737: aload 11
    //   6739: aload_2
    //   6740: putfield 1650	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   6743: aload 5
    //   6745: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6748: ifnull +164 -> 6912
    //   6751: aload_1
    //   6752: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6755: checkcast 1648	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   6758: aload 5
    //   6760: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6763: putfield 1655	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:thumb	[B
    //   6766: aload_1
    //   6767: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6770: aload 5
    //   6772: getfield 1656	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6775: putfield 1659	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   6778: aload_1
    //   6779: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6782: aload 5
    //   6784: getfield 1660	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   6787: putfield 1663	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   6790: aload_1
    //   6791: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6794: aload 4
    //   6796: getfield 1660	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   6799: putfield 1664	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:w	I
    //   6802: aload_1
    //   6803: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6806: aload 4
    //   6808: getfield 1656	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6811: putfield 1665	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:h	I
    //   6814: aload_1
    //   6815: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6818: aload 4
    //   6820: getfield 1666	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   6823: putfield 1667	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   6826: aload 4
    //   6828: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6831: getfield 1670	org/telegram/tgnet/TLRPC$FileLocation:key	[B
    //   6834: ifnonnull +176 -> 7010
    //   6837: new 98	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   6840: dup
    //   6841: aload_0
    //   6842: invokespecial 1564	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   6845: astore_2
    //   6846: aload_2
    //   6847: aload 31
    //   6849: putfield 1566	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   6852: aload_2
    //   6853: aload_1
    //   6854: putfield 412	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   6857: aload_2
    //   6858: iconst_0
    //   6859: putfield 365	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   6862: aload_2
    //   6863: aload 6
    //   6865: putfield 439	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   6868: aload_2
    //   6869: aload 34
    //   6871: putfield 1674	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   6874: aload 10
    //   6876: ifnull +105 -> 6981
    //   6879: aload 10
    //   6881: invokevirtual 347	java/lang/String:length	()I
    //   6884: ifle +97 -> 6981
    //   6887: aload 10
    //   6889: ldc_w 1204
    //   6892: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   6895: ifeq +86 -> 6981
    //   6898: aload_2
    //   6899: aload 10
    //   6901: putfield 369	org/telegram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   6904: aload_0
    //   6905: aload_2
    //   6906: invokespecial 224	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   6909: goto -341 -> 6568
    //   6912: aload_1
    //   6913: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6916: checkcast 1648	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   6919: iconst_0
    //   6920: newarray <illegal type>
    //   6922: putfield 1655	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:thumb	[B
    //   6925: goto -159 -> 6766
    //   6928: aload_1
    //   6929: new 1676	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8
    //   6932: dup
    //   6933: invokespecial 1677	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8:<init>	()V
    //   6936: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6939: aload 5
    //   6941: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6944: ifnull +21 -> 6965
    //   6947: aload_1
    //   6948: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6951: checkcast 1676	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8
    //   6954: aload 5
    //   6956: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6959: putfield 1678	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8:thumb	[B
    //   6962: goto -196 -> 6766
    //   6965: aload_1
    //   6966: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6969: checkcast 1676	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8
    //   6972: iconst_0
    //   6973: newarray <illegal type>
    //   6975: putfield 1678	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8:thumb	[B
    //   6978: goto -212 -> 6766
    //   6981: aload_2
    //   6982: aload_3
    //   6983: getfield 1207	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6986: aload_3
    //   6987: getfield 1207	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6990: invokevirtual 931	java/util/ArrayList:size	()I
    //   6993: iconst_1
    //   6994: isub
    //   6995: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6998: checkcast 904	org/telegram/tgnet/TLRPC$PhotoSize
    //   7001: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   7004: putfield 392	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   7007: goto -103 -> 6904
    //   7010: new 1680	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile
    //   7013: dup
    //   7014: invokespecial 1681	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   7017: astore_2
    //   7018: aload_2
    //   7019: aload 4
    //   7021: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   7024: getfield 515	org/telegram/tgnet/TLRPC$FileLocation:volume_id	J
    //   7027: putfield 1682	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   7030: aload_2
    //   7031: aload 4
    //   7033: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   7036: getfield 1685	org/telegram/tgnet/TLRPC$FileLocation:secret	J
    //   7039: putfield 1686	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   7042: aload_1
    //   7043: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7046: aload 4
    //   7048: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   7051: getfield 1670	org/telegram/tgnet/TLRPC$FileLocation:key	[B
    //   7054: putfield 1687	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   7057: aload_1
    //   7058: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7061: aload 4
    //   7063: getfield 1211	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   7066: getfield 1690	org/telegram/tgnet/TLRPC$FileLocation:iv	[B
    //   7069: putfield 1691	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   7072: invokestatic 1487	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7075: aload_1
    //   7076: aload 6
    //   7078: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7081: aload 34
    //   7083: aload_2
    //   7084: aconst_null
    //   7085: aload 6
    //   7087: invokevirtual 1491	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7090: goto -522 -> 6568
    //   7093: iload 19
    //   7095: iconst_3
    //   7096: if_icmpne +606 -> 7702
    //   7099: aload 33
    //   7101: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7104: invokestatic 1646	org/telegram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/telegram/tgnet/TLRPC$PhotoSize;)V
    //   7107: aload 34
    //   7109: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   7112: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   7115: bipush 46
    //   7117: if_icmplt +367 -> 7484
    //   7120: aload 33
    //   7122: invokestatic 1694	org/telegram/messenger/MessageObject:isNewGifDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   7125: ifeq +292 -> 7417
    //   7128: aload_1
    //   7129: new 1696	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7132: dup
    //   7133: invokespecial 1697	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   7136: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7139: aload_1
    //   7140: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7143: aload 33
    //   7145: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   7148: putfield 1698	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   7151: aload 33
    //   7153: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7156: ifnull +245 -> 7401
    //   7159: aload 33
    //   7161: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7164: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7167: ifnull +234 -> 7401
    //   7170: aload_1
    //   7171: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7174: checkcast 1696	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7177: aload 33
    //   7179: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7182: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7185: putfield 1699	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   7188: aload_1
    //   7189: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7192: astore_3
    //   7193: aload 33
    //   7195: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   7198: ifnull +2364 -> 9562
    //   7201: aload 33
    //   7203: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   7206: astore_2
    //   7207: aload_3
    //   7208: aload_2
    //   7209: putfield 1650	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   7212: aload_1
    //   7213: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7216: ldc_w 1701
    //   7219: putfield 1702	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   7222: aload_1
    //   7223: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7226: aload 33
    //   7228: getfield 506	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   7231: putfield 1667	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   7234: iconst_0
    //   7235: istore 18
    //   7237: iload 18
    //   7239: aload 33
    //   7241: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   7244: invokevirtual 931	java/util/ArrayList:size	()I
    //   7247: if_icmpge +57 -> 7304
    //   7250: aload 33
    //   7252: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   7255: iload 18
    //   7257: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   7260: checkcast 1231	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   7263: astore_2
    //   7264: aload_2
    //   7265: instanceof 252
    //   7268: ifeq +2301 -> 9569
    //   7271: aload_1
    //   7272: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7275: aload_2
    //   7276: getfield 1703	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   7279: putfield 1664	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:w	I
    //   7282: aload_1
    //   7283: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7286: aload_2
    //   7287: getfield 1704	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   7290: putfield 1665	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:h	I
    //   7293: aload_1
    //   7294: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7297: aload_2
    //   7298: getfield 1404	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   7301: putfield 1705	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:duration	I
    //   7304: aload_1
    //   7305: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7308: aload 33
    //   7310: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7313: getfield 1656	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7316: putfield 1659	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7319: aload_1
    //   7320: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7323: aload 33
    //   7325: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7328: getfield 1660	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7331: putfield 1663	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7334: aload 33
    //   7336: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   7339: lconst_0
    //   7340: lcmp
    //   7341: ifne +290 -> 7631
    //   7344: new 98	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   7347: dup
    //   7348: aload_0
    //   7349: invokespecial 1564	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   7352: astore_2
    //   7353: aload_2
    //   7354: aload 31
    //   7356: putfield 1566	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   7359: aload_2
    //   7360: aload_1
    //   7361: putfield 412	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   7364: aload_2
    //   7365: iconst_1
    //   7366: putfield 365	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   7369: aload_2
    //   7370: aload 6
    //   7372: putfield 439	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   7375: aload_2
    //   7376: aload 34
    //   7378: putfield 1674	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   7381: aload_2
    //   7382: aload 33
    //   7384: putfield 470	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   7387: aload_2
    //   7388: aload 4
    //   7390: putfield 435	org/telegram/messenger/SendMessagesHelper$DelayedMessage:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   7393: aload_0
    //   7394: aload_2
    //   7395: invokespecial 224	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   7398: goto -830 -> 6568
    //   7401: aload_1
    //   7402: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7405: checkcast 1696	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7408: iconst_0
    //   7409: newarray <illegal type>
    //   7411: putfield 1699	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   7414: goto -226 -> 7188
    //   7417: aload_1
    //   7418: new 1707	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   7421: dup
    //   7422: invokespecial 1708	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:<init>	()V
    //   7425: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7428: aload 33
    //   7430: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7433: ifnull +35 -> 7468
    //   7436: aload 33
    //   7438: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7441: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7444: ifnull +24 -> 7468
    //   7447: aload_1
    //   7448: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7451: checkcast 1707	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   7454: aload 33
    //   7456: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7459: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7462: putfield 1709	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:thumb	[B
    //   7465: goto -277 -> 7188
    //   7468: aload_1
    //   7469: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7472: checkcast 1707	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   7475: iconst_0
    //   7476: newarray <illegal type>
    //   7478: putfield 1709	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:thumb	[B
    //   7481: goto -293 -> 7188
    //   7484: aload 34
    //   7486: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   7489: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   7492: bipush 17
    //   7494: if_icmplt +70 -> 7564
    //   7497: aload_1
    //   7498: new 1711	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17
    //   7501: dup
    //   7502: invokespecial 1712	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17:<init>	()V
    //   7505: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7508: aload 33
    //   7510: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7513: ifnull +35 -> 7548
    //   7516: aload 33
    //   7518: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7521: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7524: ifnull +24 -> 7548
    //   7527: aload_1
    //   7528: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7531: checkcast 1711	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17
    //   7534: aload 33
    //   7536: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7539: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7542: putfield 1713	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17:thumb	[B
    //   7545: goto -357 -> 7188
    //   7548: aload_1
    //   7549: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7552: checkcast 1711	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17
    //   7555: iconst_0
    //   7556: newarray <illegal type>
    //   7558: putfield 1713	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17:thumb	[B
    //   7561: goto -373 -> 7188
    //   7564: aload_1
    //   7565: new 1715	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8
    //   7568: dup
    //   7569: invokespecial 1716	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8:<init>	()V
    //   7572: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7575: aload 33
    //   7577: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7580: ifnull +35 -> 7615
    //   7583: aload 33
    //   7585: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7588: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7591: ifnull +24 -> 7615
    //   7594: aload_1
    //   7595: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7598: checkcast 1715	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8
    //   7601: aload 33
    //   7603: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7606: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7609: putfield 1717	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8:thumb	[B
    //   7612: goto -424 -> 7188
    //   7615: aload_1
    //   7616: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7619: checkcast 1715	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8
    //   7622: iconst_0
    //   7623: newarray <illegal type>
    //   7625: putfield 1717	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8:thumb	[B
    //   7628: goto -440 -> 7188
    //   7631: new 1680	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile
    //   7634: dup
    //   7635: invokespecial 1681	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   7638: astore_2
    //   7639: aload_2
    //   7640: aload 33
    //   7642: getfield 476	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   7645: putfield 1682	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   7648: aload_2
    //   7649: aload 33
    //   7651: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   7654: putfield 1686	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   7657: aload_1
    //   7658: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7661: aload 33
    //   7663: getfield 1718	org/telegram/tgnet/TLRPC$TL_document:key	[B
    //   7666: putfield 1687	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   7669: aload_1
    //   7670: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7673: aload 33
    //   7675: getfield 1719	org/telegram/tgnet/TLRPC$TL_document:iv	[B
    //   7678: putfield 1691	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   7681: invokestatic 1487	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7684: aload_1
    //   7685: aload 6
    //   7687: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7690: aload 34
    //   7692: aload_2
    //   7693: aconst_null
    //   7694: aload 6
    //   7696: invokevirtual 1491	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7699: goto -1131 -> 6568
    //   7702: iload 19
    //   7704: bipush 6
    //   7706: if_icmpne +1872 -> 9578
    //   7709: aload_1
    //   7710: new 1721	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaContact
    //   7713: dup
    //   7714: invokespecial 1722	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaContact:<init>	()V
    //   7717: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7720: aload_1
    //   7721: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7724: aload 35
    //   7726: getfield 659	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   7729: putfield 1723	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:phone_number	Ljava/lang/String;
    //   7732: aload_1
    //   7733: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7736: aload 35
    //   7738: getfield 663	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   7741: putfield 1724	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:first_name	Ljava/lang/String;
    //   7744: aload_1
    //   7745: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7748: aload 35
    //   7750: getfield 667	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   7753: putfield 1725	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:last_name	Ljava/lang/String;
    //   7756: aload_1
    //   7757: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7760: aload 35
    //   7762: getfield 1152	org/telegram/tgnet/TLRPC$User:id	I
    //   7765: putfield 1726	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:user_id	I
    //   7768: invokestatic 1487	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7771: aload_1
    //   7772: aload 6
    //   7774: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7777: aload 34
    //   7779: aconst_null
    //   7780: aconst_null
    //   7781: aload 6
    //   7783: invokevirtual 1491	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7786: goto -1218 -> 6568
    //   7789: aload 33
    //   7791: invokestatic 1229	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   7794: ifeq +178 -> 7972
    //   7797: aload_1
    //   7798: new 1728	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7801: dup
    //   7802: invokespecial 1729	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:<init>	()V
    //   7805: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7808: aload_1
    //   7809: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7812: aload 33
    //   7814: getfield 476	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   7817: putfield 1730	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:id	J
    //   7820: aload_1
    //   7821: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7824: aload 33
    //   7826: getfield 793	org/telegram/tgnet/TLRPC$TL_document:date	I
    //   7829: putfield 1731	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:date	I
    //   7832: aload_1
    //   7833: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7836: aload 33
    //   7838: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   7841: putfield 1732	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:access_hash	J
    //   7844: aload_1
    //   7845: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7848: aload 33
    //   7850: getfield 809	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   7853: putfield 1702	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   7856: aload_1
    //   7857: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7860: aload 33
    //   7862: getfield 506	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   7865: putfield 1667	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   7868: aload_1
    //   7869: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7872: aload 33
    //   7874: getfield 531	org/telegram/tgnet/TLRPC$TL_document:dc_id	I
    //   7877: putfield 1733	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:dc_id	I
    //   7880: aload_1
    //   7881: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7884: aload 33
    //   7886: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   7889: putfield 1698	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   7892: aload 33
    //   7894: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7897: ifnonnull +57 -> 7954
    //   7900: aload_1
    //   7901: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7904: checkcast 1728	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7907: new 899	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty
    //   7910: dup
    //   7911: invokespecial 900	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty:<init>	()V
    //   7914: putfield 1734	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7917: aload_1
    //   7918: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7921: checkcast 1728	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7924: getfield 1734	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7927: ldc_w 902
    //   7930: putfield 906	org/telegram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
    //   7933: invokestatic 1487	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7936: aload_1
    //   7937: aload 6
    //   7939: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7942: aload 34
    //   7944: aconst_null
    //   7945: aconst_null
    //   7946: aload 6
    //   7948: invokevirtual 1491	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7951: goto -1383 -> 6568
    //   7954: aload_1
    //   7955: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7958: checkcast 1728	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7961: aload 33
    //   7963: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7966: putfield 1734	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7969: goto -36 -> 7933
    //   7972: aload 33
    //   7974: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7977: invokestatic 1646	org/telegram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/telegram/tgnet/TLRPC$PhotoSize;)V
    //   7980: aload 34
    //   7982: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   7985: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   7988: bipush 46
    //   7990: if_icmplt +262 -> 8252
    //   7993: aload_1
    //   7994: new 1696	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7997: dup
    //   7998: invokespecial 1697	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   8001: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8004: aload_1
    //   8005: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8008: aload 33
    //   8010: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8013: putfield 1698	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   8016: aload_1
    //   8017: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8020: astore_3
    //   8021: aload 33
    //   8023: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   8026: ifnull +1574 -> 9600
    //   8029: aload 33
    //   8031: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   8034: astore_2
    //   8035: aload_3
    //   8036: aload_2
    //   8037: putfield 1650	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   8040: aload 33
    //   8042: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8045: ifnull +175 -> 8220
    //   8048: aload 33
    //   8050: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8053: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8056: ifnull +164 -> 8220
    //   8059: aload_1
    //   8060: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8063: checkcast 1696	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8066: aload 33
    //   8068: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8071: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8074: putfield 1699	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   8077: aload_1
    //   8078: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8081: aload 33
    //   8083: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8086: getfield 1656	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   8089: putfield 1659	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8092: aload_1
    //   8093: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8096: aload 33
    //   8098: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8101: getfield 1660	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   8104: putfield 1663	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8107: aload_1
    //   8108: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8111: aload 33
    //   8113: getfield 506	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   8116: putfield 1667	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   8119: aload_1
    //   8120: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8123: aload 33
    //   8125: getfield 809	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   8128: putfield 1702	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   8131: aload 33
    //   8133: getfield 1718	org/telegram/tgnet/TLRPC$TL_document:key	[B
    //   8136: ifnonnull +241 -> 8377
    //   8139: new 98	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   8142: dup
    //   8143: aload_0
    //   8144: invokespecial 1564	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   8147: astore_2
    //   8148: aload_2
    //   8149: aload 31
    //   8151: putfield 1566	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   8154: aload_2
    //   8155: aload_1
    //   8156: putfield 412	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   8159: aload_2
    //   8160: iconst_2
    //   8161: putfield 365	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   8164: aload_2
    //   8165: aload 6
    //   8167: putfield 439	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   8170: aload_2
    //   8171: aload 34
    //   8173: putfield 1674	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   8176: aload 10
    //   8178: ifnull +28 -> 8206
    //   8181: aload 10
    //   8183: invokevirtual 347	java/lang/String:length	()I
    //   8186: ifle +20 -> 8206
    //   8189: aload 10
    //   8191: ldc_w 1204
    //   8194: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   8197: ifeq +9 -> 8206
    //   8200: aload_2
    //   8201: aload 10
    //   8203: putfield 369	org/telegram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   8206: aload_2
    //   8207: aload 33
    //   8209: putfield 470	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   8212: aload_0
    //   8213: aload_2
    //   8214: invokespecial 224	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   8217: goto -1649 -> 6568
    //   8220: aload_1
    //   8221: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8224: checkcast 1696	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8227: iconst_0
    //   8228: newarray <illegal type>
    //   8230: putfield 1699	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   8233: aload_1
    //   8234: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8237: iconst_0
    //   8238: putfield 1659	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8241: aload_1
    //   8242: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8245: iconst_0
    //   8246: putfield 1663	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8249: goto -142 -> 8107
    //   8252: aload_1
    //   8253: new 1736	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8
    //   8256: dup
    //   8257: invokespecial 1737	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8:<init>	()V
    //   8260: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8263: aload_1
    //   8264: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8267: aload 33
    //   8269: invokestatic 1741	org/telegram/messenger/FileLoader:getDocumentFileName	(Lorg/telegram/tgnet/TLRPC$Document;)Ljava/lang/String;
    //   8272: putfield 1742	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:file_name	Ljava/lang/String;
    //   8275: aload 33
    //   8277: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8280: ifnull +65 -> 8345
    //   8283: aload 33
    //   8285: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8288: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8291: ifnull +54 -> 8345
    //   8294: aload_1
    //   8295: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8298: checkcast 1736	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8
    //   8301: aload 33
    //   8303: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8306: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8309: putfield 1743	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8:thumb	[B
    //   8312: aload_1
    //   8313: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8316: aload 33
    //   8318: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8321: getfield 1656	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   8324: putfield 1659	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8327: aload_1
    //   8328: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8331: aload 33
    //   8333: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8336: getfield 1660	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   8339: putfield 1663	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8342: goto -235 -> 8107
    //   8345: aload_1
    //   8346: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8349: checkcast 1736	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8
    //   8352: iconst_0
    //   8353: newarray <illegal type>
    //   8355: putfield 1743	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8:thumb	[B
    //   8358: aload_1
    //   8359: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8362: iconst_0
    //   8363: putfield 1659	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8366: aload_1
    //   8367: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8370: iconst_0
    //   8371: putfield 1663	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8374: goto -267 -> 8107
    //   8377: new 1680	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile
    //   8380: dup
    //   8381: invokespecial 1681	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   8384: astore_2
    //   8385: aload_2
    //   8386: aload 33
    //   8388: getfield 476	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   8391: putfield 1682	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   8394: aload_2
    //   8395: aload 33
    //   8397: getfield 1582	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   8400: putfield 1686	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   8403: aload_1
    //   8404: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8407: aload 33
    //   8409: getfield 1718	org/telegram/tgnet/TLRPC$TL_document:key	[B
    //   8412: putfield 1687	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   8415: aload_1
    //   8416: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8419: aload 33
    //   8421: getfield 1719	org/telegram/tgnet/TLRPC$TL_document:iv	[B
    //   8424: putfield 1691	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   8427: invokestatic 1487	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   8430: aload_1
    //   8431: aload 6
    //   8433: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8436: aload 34
    //   8438: aload_2
    //   8439: aconst_null
    //   8440: aload 6
    //   8442: invokevirtual 1491	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   8445: goto -1877 -> 6568
    //   8448: iload 19
    //   8450: bipush 8
    //   8452: if_icmpne -1884 -> 6568
    //   8455: new 98	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   8458: dup
    //   8459: aload_0
    //   8460: invokespecial 1564	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   8463: astore_3
    //   8464: aload_3
    //   8465: aload 34
    //   8467: putfield 1674	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   8470: aload_3
    //   8471: aload_1
    //   8472: putfield 412	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   8475: aload_3
    //   8476: aload 6
    //   8478: putfield 439	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   8481: aload_3
    //   8482: aload 33
    //   8484: putfield 470	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   8487: aload_3
    //   8488: iconst_3
    //   8489: putfield 365	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   8492: aload 34
    //   8494: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   8497: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   8500: bipush 46
    //   8502: if_icmplt +189 -> 8691
    //   8505: aload_1
    //   8506: new 1696	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8509: dup
    //   8510: invokespecial 1697	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   8513: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8516: aload_1
    //   8517: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8520: aload 33
    //   8522: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8525: putfield 1698	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   8528: aload_1
    //   8529: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8532: astore 4
    //   8534: aload 33
    //   8536: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   8539: ifnull +1068 -> 9607
    //   8542: aload 33
    //   8544: getfield 909	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   8547: astore_2
    //   8548: aload 4
    //   8550: aload_2
    //   8551: putfield 1650	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   8554: aload 33
    //   8556: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8559: ifnull +100 -> 8659
    //   8562: aload 33
    //   8564: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8567: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8570: ifnull +89 -> 8659
    //   8573: aload_1
    //   8574: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8577: checkcast 1696	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8580: aload 33
    //   8582: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8585: getfield 1653	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8588: putfield 1699	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   8591: aload_1
    //   8592: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8595: aload 33
    //   8597: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8600: getfield 1656	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   8603: putfield 1659	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8606: aload_1
    //   8607: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8610: aload 33
    //   8612: getfield 828	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8615: getfield 1660	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   8618: putfield 1663	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8621: aload_1
    //   8622: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8625: aload 33
    //   8627: getfield 809	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   8630: putfield 1702	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   8633: aload_1
    //   8634: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8637: aload 33
    //   8639: getfield 506	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   8642: putfield 1667	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   8645: aload_3
    //   8646: aload 31
    //   8648: putfield 1566	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   8651: aload_0
    //   8652: aload_3
    //   8653: invokespecial 224	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   8656: goto -2088 -> 6568
    //   8659: aload_1
    //   8660: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8663: checkcast 1696	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8666: iconst_0
    //   8667: newarray <illegal type>
    //   8669: putfield 1699	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   8672: aload_1
    //   8673: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8676: iconst_0
    //   8677: putfield 1659	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8680: aload_1
    //   8681: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8684: iconst_0
    //   8685: putfield 1663	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8688: goto -67 -> 8621
    //   8691: aload 34
    //   8693: getfield 753	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   8696: invokestatic 756	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   8699: bipush 17
    //   8701: if_icmplt +92 -> 8793
    //   8704: aload_1
    //   8705: new 1745	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio
    //   8708: dup
    //   8709: invokespecial 1746	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio:<init>	()V
    //   8712: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8715: goto +899 -> 9614
    //   8718: iload 18
    //   8720: aload 33
    //   8722: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8725: invokevirtual 931	java/util/ArrayList:size	()I
    //   8728: if_icmpge +35 -> 8763
    //   8731: aload 33
    //   8733: getfield 802	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8736: iload 18
    //   8738: invokevirtual 1210	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   8741: checkcast 1231	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   8744: astore_2
    //   8745: aload_2
    //   8746: instanceof 758
    //   8749: ifeq +871 -> 9620
    //   8752: aload_1
    //   8753: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8756: aload_2
    //   8757: getfield 1404	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   8760: putfield 1705	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:duration	I
    //   8763: aload_1
    //   8764: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8767: ldc_w 1748
    //   8770: putfield 1702	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   8773: aload_1
    //   8774: getfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8777: aload 33
    //   8779: getfield 506	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   8782: putfield 1667	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   8785: aload_3
    //   8786: iconst_3
    //   8787: putfield 365	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   8790: goto -139 -> 8651
    //   8793: aload_1
    //   8794: new 1750	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio_layer8
    //   8797: dup
    //   8798: invokespecial 1751	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio_layer8:<init>	()V
    //   8801: putfield 1479	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8804: goto +810 -> 9614
    //   8807: iload 19
    //   8809: iconst_4
    //   8810: if_icmpne +241 -> 9051
    //   8813: new 1753	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages
    //   8816: dup
    //   8817: invokespecial 1754	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:<init>	()V
    //   8820: astore_1
    //   8821: aload_1
    //   8822: aload 32
    //   8824: putfield 1757	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:to_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8827: aload_1
    //   8828: aload 14
    //   8830: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8833: getfield 1760	org/telegram/tgnet/TLRPC$Message:with_my_score	Z
    //   8836: putfield 1761	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:with_my_score	Z
    //   8839: aload 14
    //   8841: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8844: getfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   8847: ifeq +168 -> 9015
    //   8850: invokestatic 740	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   8853: aload 14
    //   8855: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8858: getfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   8861: ineg
    //   8862: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8865: invokevirtual 1073	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   8868: astore_2
    //   8869: aload_1
    //   8870: new 1064	org/telegram/tgnet/TLRPC$TL_inputPeerChannel
    //   8873: dup
    //   8874: invokespecial 1762	org/telegram/tgnet/TLRPC$TL_inputPeerChannel:<init>	()V
    //   8877: putfield 1765	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8880: aload_1
    //   8881: getfield 1765	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8884: aload 14
    //   8886: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8889: getfield 1400	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   8892: ineg
    //   8893: putfield 1069	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   8896: aload_2
    //   8897: ifnull +14 -> 8911
    //   8900: aload_1
    //   8901: getfield 1765	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8904: aload_2
    //   8905: getfield 1766	org/telegram/tgnet/TLRPC$Chat:access_hash	J
    //   8908: putfield 1365	org/telegram/tgnet/TLRPC$InputPeer:access_hash	J
    //   8911: aload 14
    //   8913: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8916: getfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   8919: instanceof 1429
    //   8922: ifeq +44 -> 8966
    //   8925: aload_1
    //   8926: getstatic 302	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   8929: ldc_w 1431
    //   8932: iconst_0
    //   8933: invokevirtual 1437	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   8936: new 452	java/lang/StringBuilder
    //   8939: dup
    //   8940: invokespecial 453	java/lang/StringBuilder:<init>	()V
    //   8943: ldc_w 1439
    //   8946: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   8949: lload 8
    //   8951: invokevirtual 479	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   8954: invokevirtual 482	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   8957: iconst_0
    //   8958: invokeinterface 1445 3 0
    //   8963: putfield 1767	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:silent	Z
    //   8966: aload_1
    //   8967: getfield 1768	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:random_id	Ljava/util/ArrayList;
    //   8970: aload 7
    //   8972: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   8975: invokestatic 1380	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   8978: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   8981: pop
    //   8982: aload 14
    //   8984: invokevirtual 1054	org/telegram/messenger/MessageObject:getId	()I
    //   8987: iflt +42 -> 9029
    //   8990: aload_1
    //   8991: getfield 1770	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:id	Ljava/util/ArrayList;
    //   8994: aload 14
    //   8996: invokevirtual 1054	org/telegram/messenger/MessageObject:getId	()I
    //   8999: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9002: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   9005: pop
    //   9006: aload_0
    //   9007: aload_1
    //   9008: aload 6
    //   9010: aconst_null
    //   9011: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   9014: return
    //   9015: aload_1
    //   9016: new 1772	org/telegram/tgnet/TLRPC$TL_inputPeerEmpty
    //   9019: dup
    //   9020: invokespecial 1773	org/telegram/tgnet/TLRPC$TL_inputPeerEmpty:<init>	()V
    //   9023: putfield 1765	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   9026: goto -115 -> 8911
    //   9029: aload_1
    //   9030: getfield 1770	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:id	Ljava/util/ArrayList;
    //   9033: aload 14
    //   9035: getfield 445	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9038: getfield 1776	org/telegram/tgnet/TLRPC$Message:fwd_msg_id	I
    //   9041: invokestatic 744	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9044: invokevirtual 680	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   9047: pop
    //   9048: goto -42 -> 9006
    //   9051: iload 19
    //   9053: bipush 9
    //   9055: if_icmpne -9048 -> 7
    //   9058: new 1778	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult
    //   9061: dup
    //   9062: invokespecial 1779	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:<init>	()V
    //   9065: astore_1
    //   9066: aload_1
    //   9067: aload 32
    //   9069: putfield 1780	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   9072: aload_1
    //   9073: aload 7
    //   9075: getfield 1084	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   9078: putfield 1781	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:random_id	J
    //   9081: aload 11
    //   9083: ifnull +22 -> 9105
    //   9086: aload_1
    //   9087: aload_1
    //   9088: getfield 1782	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:flags	I
    //   9091: iconst_1
    //   9092: ior
    //   9093: putfield 1782	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:flags	I
    //   9096: aload_1
    //   9097: aload 11
    //   9099: invokevirtual 1054	org/telegram/messenger/MessageObject:getId	()I
    //   9102: putfield 1783	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:reply_to_msg_id	I
    //   9105: aload 7
    //   9107: getfield 1309	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   9110: instanceof 1429
    //   9113: ifeq +44 -> 9157
    //   9116: aload_1
    //   9117: getstatic 302	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   9120: ldc_w 1431
    //   9123: iconst_0
    //   9124: invokevirtual 1437	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   9127: new 452	java/lang/StringBuilder
    //   9130: dup
    //   9131: invokespecial 453	java/lang/StringBuilder:<init>	()V
    //   9134: ldc_w 1439
    //   9137: invokevirtual 466	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   9140: lload 8
    //   9142: invokevirtual 479	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   9145: invokevirtual 482	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   9148: iconst_0
    //   9149: invokeinterface 1445 3 0
    //   9154: putfield 1784	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:silent	Z
    //   9157: aload_1
    //   9158: aload 17
    //   9160: ldc_w 1129
    //   9163: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   9166: checkcast 340	java/lang/String
    //   9169: invokestatic 1787	org/telegram/messenger/Utilities:parseLong	(Ljava/lang/String;)Ljava/lang/Long;
    //   9172: invokevirtual 1790	java/lang/Long:longValue	()J
    //   9175: putfield 1792	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:query_id	J
    //   9178: aload_1
    //   9179: aload 17
    //   9181: ldc_w 1793
    //   9184: invokevirtual 986	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   9187: checkcast 340	java/lang/String
    //   9190: putfield 1795	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:id	Ljava/lang/String;
    //   9193: aload 14
    //   9195: ifnonnull +14 -> 9209
    //   9198: aload_1
    //   9199: iconst_1
    //   9200: putfield 1796	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:clear_draft	Z
    //   9203: lload 8
    //   9205: iconst_0
    //   9206: invokestatic 1463	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   9209: aload_0
    //   9210: aload_1
    //   9211: aload 6
    //   9213: aconst_null
    //   9214: invokespecial 1422	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   9217: return
    //   9218: astore_2
    //   9219: aconst_null
    //   9220: astore_1
    //   9221: aload 26
    //   9223: astore 7
    //   9225: goto -8455 -> 770
    //   9228: astore_2
    //   9229: aconst_null
    //   9230: astore_1
    //   9231: aload 26
    //   9233: astore 7
    //   9235: goto -8465 -> 770
    //   9238: aload 26
    //   9240: astore 7
    //   9242: aload 12
    //   9244: astore 27
    //   9246: goto -7665 -> 1581
    //   9249: iconst_0
    //   9250: istore 18
    //   9252: aload_1
    //   9253: astore 27
    //   9255: aload_2
    //   9256: astore 28
    //   9258: aload_3
    //   9259: astore 29
    //   9261: aload 5
    //   9263: astore 30
    //   9265: goto -8391 -> 874
    //   9268: aconst_null
    //   9269: astore 26
    //   9271: goto -7749 -> 1522
    //   9274: iconst_0
    //   9275: istore 18
    //   9277: goto -7710 -> 1567
    //   9280: iconst_1
    //   9281: istore 18
    //   9283: aload 12
    //   9285: astore 27
    //   9287: goto -7706 -> 1581
    //   9290: ldc_w 713
    //   9293: astore 26
    //   9295: goto -7350 -> 1945
    //   9298: iconst_2
    //   9299: istore 18
    //   9301: goto -7308 -> 1993
    //   9304: bipush 6
    //   9306: istore 18
    //   9308: aload 12
    //   9310: astore 27
    //   9312: goto -7731 -> 1581
    //   9315: ldc_w 713
    //   9318: astore 7
    //   9320: goto -6745 -> 2575
    //   9323: iconst_3
    //   9324: istore 19
    //   9326: goto -6702 -> 2624
    //   9329: bipush 7
    //   9331: istore 19
    //   9333: goto -6709 -> 2624
    //   9336: iload 21
    //   9338: iconst_1
    //   9339: iadd
    //   9340: istore 21
    //   9342: goto -6611 -> 2731
    //   9345: astore_2
    //   9346: aconst_null
    //   9347: astore_1
    //   9348: goto -8578 -> 770
    //   9351: iload 19
    //   9353: ifeq -5674 -> 3679
    //   9356: iload 19
    //   9358: bipush 9
    //   9360: if_icmpne +38 -> 9398
    //   9363: aload 36
    //   9365: ifnull +33 -> 9398
    //   9368: aload 34
    //   9370: ifnull +28 -> 9398
    //   9373: goto -5694 -> 3679
    //   9376: iload 18
    //   9378: iconst_1
    //   9379: iadd
    //   9380: istore 18
    //   9382: goto -5456 -> 3926
    //   9385: astore_2
    //   9386: aload 6
    //   9388: astore_1
    //   9389: goto -8619 -> 770
    //   9392: iconst_0
    //   9393: istore 24
    //   9395: goto -5159 -> 4236
    //   9398: iload 19
    //   9400: iconst_1
    //   9401: if_icmplt +9 -> 9410
    //   9404: iload 19
    //   9406: iconst_3
    //   9407: if_icmple -4694 -> 4713
    //   9410: iload 19
    //   9412: iconst_5
    //   9413: if_icmplt +10 -> 9423
    //   9416: iload 19
    //   9418: bipush 8
    //   9420: if_icmple -4707 -> 4713
    //   9423: iload 19
    //   9425: bipush 9
    //   9427: if_icmpne -620 -> 8807
    //   9430: aload 34
    //   9432: ifnull -625 -> 8807
    //   9435: goto -4722 -> 4713
    //   9438: iload 19
    //   9440: iconst_2
    //   9441: if_icmpeq -4549 -> 4892
    //   9444: iload 19
    //   9446: bipush 9
    //   9448: if_icmpne -4270 -> 5178
    //   9451: aload_3
    //   9452: ifnull -4274 -> 5178
    //   9455: goto -4563 -> 4892
    //   9458: ldc_w 713
    //   9461: astore_1
    //   9462: goto -4540 -> 4922
    //   9465: ldc_w 713
    //   9468: astore_1
    //   9469: goto -4326 -> 5143
    //   9472: ldc_w 713
    //   9475: astore_2
    //   9476: goto -4249 -> 5227
    //   9479: ldc_w 713
    //   9482: astore_1
    //   9483: goto -4136 -> 5347
    //   9486: iload 19
    //   9488: bipush 7
    //   9490: if_icmpeq -4058 -> 5432
    //   9493: iload 19
    //   9495: bipush 9
    //   9497: if_icmpne -3750 -> 5747
    //   9500: goto -4068 -> 5432
    //   9503: ldc_w 713
    //   9506: astore 4
    //   9508: goto -3931 -> 5577
    //   9511: ldc_w 713
    //   9514: astore_1
    //   9515: goto -3781 -> 5734
    //   9518: ldc_w 713
    //   9521: astore_1
    //   9522: goto -3712 -> 5810
    //   9525: ldc_w 713
    //   9528: astore_1
    //   9529: goto -3648 -> 5881
    //   9532: goto -3556 -> 5976
    //   9535: iload 19
    //   9537: iconst_2
    //   9538: if_icmpeq -2882 -> 6656
    //   9541: iload 19
    //   9543: bipush 9
    //   9545: if_icmpne -2452 -> 7093
    //   9548: aload_3
    //   9549: ifnull -2456 -> 7093
    //   9552: goto -2896 -> 6656
    //   9555: ldc_w 713
    //   9558: astore_2
    //   9559: goto -2822 -> 6737
    //   9562: ldc_w 713
    //   9565: astore_2
    //   9566: goto -2359 -> 7207
    //   9569: iload 18
    //   9571: iconst_1
    //   9572: iadd
    //   9573: istore 18
    //   9575: goto -2338 -> 7237
    //   9578: iload 19
    //   9580: bipush 7
    //   9582: if_icmpeq -1793 -> 7789
    //   9585: iload 19
    //   9587: bipush 9
    //   9589: if_icmpne -1141 -> 8448
    //   9592: aload 33
    //   9594: ifnull -1146 -> 8448
    //   9597: goto -1808 -> 7789
    //   9600: ldc_w 713
    //   9603: astore_2
    //   9604: goto -1569 -> 8035
    //   9607: ldc_w 713
    //   9610: astore_2
    //   9611: goto -1063 -> 8548
    //   9614: iconst_0
    //   9615: istore 18
    //   9617: goto -899 -> 8718
    //   9620: iload 18
    //   9622: iconst_1
    //   9623: iadd
    //   9624: istore 18
    //   9626: goto -908 -> 8718
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	9629	0	this	SendMessagesHelper
    //   0	9629	1	paramString1	String
    //   0	9629	2	paramMessageMedia	TLRPC.MessageMedia
    //   0	9629	3	paramTL_photo	TLRPC.TL_photo
    //   0	9629	4	paramVideoEditedInfo	VideoEditedInfo
    //   0	9629	5	paramUser	TLRPC.User
    //   0	9629	6	paramTL_document	TLRPC.TL_document
    //   0	9629	7	paramTL_game	TLRPC.TL_game
    //   0	9629	8	paramLong	long
    //   0	9629	10	paramString2	String
    //   0	9629	11	paramMessageObject1	MessageObject
    //   0	9629	12	paramWebPage	TLRPC.WebPage
    //   0	9629	13	paramBoolean	boolean
    //   0	9629	14	paramMessageObject2	MessageObject
    //   0	9629	15	paramArrayList	ArrayList<TLRPC.MessageEntity>
    //   0	9629	16	paramReplyMarkup	TLRPC.ReplyMarkup
    //   0	9629	17	paramHashMap	HashMap<String, String>
    //   55	9570	18	i	int
    //   260	9330	19	j	int
    //   71	4905	20	k	int
    //   2729	6612	21	m	int
    //   68	3347	22	n	int
    //   60	3712	23	i1	int
    //   2188	7206	24	bool	boolean
    //   9	4134	25	localObject1	Object
    //   52	9242	26	localObject2	Object
    //   74	9237	27	localObject3	Object
    //   209	9048	28	localObject4	Object
    //   889	8371	29	localTL_photo	TLRPC.TL_photo
    //   892	8372	30	localObject5	Object
    //   13	8634	31	localObject6	Object
    //   86	8982	32	localInputPeer	TLRPC.InputPeer
    //   268	9325	33	localTL_document	TLRPC.TL_document
    //   111	9320	34	localObject7	Object
    //   272	7489	35	localObject8	Object
    //   275	9089	36	localObject9	Object
    //   264	4324	37	localObject10	Object
    //   89	4073	38	localObject11	Object
    // Exception table:
    //   from	to	target	type
    //   240	247	763	java/lang/Exception
    //   251	259	763	java/lang/Exception
    //   285	295	763	java/lang/Exception
    //   299	308	763	java/lang/Exception
    //   317	328	763	java/lang/Exception
    //   337	353	763	java/lang/Exception
    //   357	365	763	java/lang/Exception
    //   369	377	763	java/lang/Exception
    //   381	395	763	java/lang/Exception
    //   399	406	763	java/lang/Exception
    //   415	423	763	java/lang/Exception
    //   427	438	763	java/lang/Exception
    //   442	450	763	java/lang/Exception
    //   459	465	763	java/lang/Exception
    //   469	483	763	java/lang/Exception
    //   487	502	763	java/lang/Exception
    //   510	517	763	java/lang/Exception
    //   521	535	763	java/lang/Exception
    //   539	545	763	java/lang/Exception
    //   549	563	763	java/lang/Exception
    //   567	574	763	java/lang/Exception
    //   588	601	763	java/lang/Exception
    //   605	618	763	java/lang/Exception
    //   622	635	763	java/lang/Exception
    //   639	649	763	java/lang/Exception
    //   663	676	763	java/lang/Exception
    //   680	687	763	java/lang/Exception
    //   702	709	763	java/lang/Exception
    //   713	721	763	java/lang/Exception
    //   725	749	763	java/lang/Exception
    //   753	762	763	java/lang/Exception
    //   845	853	763	java/lang/Exception
    //   857	871	763	java/lang/Exception
    //   943	954	763	java/lang/Exception
    //   991	997	763	java/lang/Exception
    //   1004	1013	763	java/lang/Exception
    //   1017	1024	763	java/lang/Exception
    //   1044	1053	763	java/lang/Exception
    //   1057	1070	763	java/lang/Exception
    //   1090	1099	763	java/lang/Exception
    //   1111	1124	763	java/lang/Exception
    //   1144	1154	763	java/lang/Exception
    //   1158	1167	763	java/lang/Exception
    //   1239	1249	763	java/lang/Exception
    //   1253	1263	763	java/lang/Exception
    //   1267	1277	763	java/lang/Exception
    //   1281	1291	763	java/lang/Exception
    //   1295	1308	763	java/lang/Exception
    //   1345	1354	763	java/lang/Exception
    //   1358	1371	763	java/lang/Exception
    //   1404	1417	763	java/lang/Exception
    //   1421	1430	763	java/lang/Exception
    //   1439	1447	763	java/lang/Exception
    //   1451	1458	763	java/lang/Exception
    //   1475	1483	763	java/lang/Exception
    //   1487	1495	763	java/lang/Exception
    //   1499	1508	763	java/lang/Exception
    //   1512	1522	763	java/lang/Exception
    //   1531	1543	763	java/lang/Exception
    //   1552	1563	763	java/lang/Exception
    //   1571	1577	763	java/lang/Exception
    //   1585	1593	763	java/lang/Exception
    //   1597	1605	763	java/lang/Exception
    //   1609	1614	763	java/lang/Exception
    //   1618	1625	763	java/lang/Exception
    //   1629	1636	763	java/lang/Exception
    //   1640	1646	763	java/lang/Exception
    //   1660	1671	763	java/lang/Exception
    //   1675	1679	763	java/lang/Exception
    //   1705	1714	763	java/lang/Exception
    //   1721	1733	763	java/lang/Exception
    //   1737	1747	763	java/lang/Exception
    //   1763	1776	763	java/lang/Exception
    //   1780	1789	763	java/lang/Exception
    //   1793	1799	763	java/lang/Exception
    //   1803	1811	763	java/lang/Exception
    //   1820	1831	763	java/lang/Exception
    //   1846	1855	763	java/lang/Exception
    //   1871	1884	763	java/lang/Exception
    //   1888	1897	763	java/lang/Exception
    //   1901	1913	763	java/lang/Exception
    //   1917	1924	763	java/lang/Exception
    //   1928	1935	763	java/lang/Exception
    //   1939	1945	763	java/lang/Exception
    //   1949	1956	763	java/lang/Exception
    //   1960	1969	763	java/lang/Exception
    //   1978	1989	763	java/lang/Exception
    //   1997	2005	763	java/lang/Exception
    //   2014	2022	763	java/lang/Exception
    //   2026	2037	763	java/lang/Exception
    //   2041	2048	763	java/lang/Exception
    //   2059	2068	763	java/lang/Exception
    //   2075	2109	763	java/lang/Exception
    //   2125	2134	763	java/lang/Exception
    //   2224	2237	763	java/lang/Exception
    //   2241	2250	763	java/lang/Exception
    //   2254	2266	763	java/lang/Exception
    //   2270	2283	763	java/lang/Exception
    //   2287	2300	763	java/lang/Exception
    //   2304	2317	763	java/lang/Exception
    //   2321	2334	763	java/lang/Exception
    //   2338	2349	763	java/lang/Exception
    //   2353	2364	763	java/lang/Exception
    //   2368	2376	763	java/lang/Exception
    //   2380	2391	763	java/lang/Exception
    //   2395	2406	763	java/lang/Exception
    //   2410	2418	763	java/lang/Exception
    //   2422	2430	763	java/lang/Exception
    //   2439	2450	763	java/lang/Exception
    //   2465	2474	763	java/lang/Exception
    //   2499	2512	763	java/lang/Exception
    //   2516	2525	763	java/lang/Exception
    //   2529	2541	763	java/lang/Exception
    //   2545	2552	763	java/lang/Exception
    //   2556	2564	763	java/lang/Exception
    //   2568	2575	763	java/lang/Exception
    //   2579	2586	763	java/lang/Exception
    //   2590	2600	763	java/lang/Exception
    //   2609	2620	763	java/lang/Exception
    //   2633	2641	763	java/lang/Exception
    //   2650	2658	763	java/lang/Exception
    //   2662	2670	763	java/lang/Exception
    //   2674	2687	763	java/lang/Exception
    //   2720	2728	763	java/lang/Exception
    //   2747	2760	763	java/lang/Exception
    //   2764	2779	763	java/lang/Exception
    //   2783	2791	763	java/lang/Exception
    //   2795	2808	763	java/lang/Exception
    //   2812	2823	763	java/lang/Exception
    //   2827	2843	763	java/lang/Exception
    //   2862	2871	763	java/lang/Exception
    //   2878	2886	763	java/lang/Exception
    //   2898	2906	763	java/lang/Exception
    //   2917	2927	763	java/lang/Exception
    //   2934	2941	763	java/lang/Exception
    //   2948	2959	763	java/lang/Exception
    //   2963	2972	763	java/lang/Exception
    //   2976	2987	763	java/lang/Exception
    //   2991	3001	763	java/lang/Exception
    //   3005	3013	763	java/lang/Exception
    //   3017	3030	763	java/lang/Exception
    //   3039	3047	763	java/lang/Exception
    //   3051	3063	763	java/lang/Exception
    //   3067	3077	763	java/lang/Exception
    //   3096	3108	763	java/lang/Exception
    //   3127	3139	763	java/lang/Exception
    //   3158	3166	763	java/lang/Exception
    //   3170	3184	763	java/lang/Exception
    //   3191	3213	763	java/lang/Exception
    //   3220	3226	763	java/lang/Exception
    //   3230	3237	763	java/lang/Exception
    //   3241	3249	763	java/lang/Exception
    //   3256	3262	763	java/lang/Exception
    //   3269	3282	763	java/lang/Exception
    //   3289	3298	763	java/lang/Exception
    //   3398	3411	763	java/lang/Exception
    //   3421	3429	763	java/lang/Exception
    //   3433	3444	763	java/lang/Exception
    //   3448	3454	763	java/lang/Exception
    //   3458	3464	763	java/lang/Exception
    //   3468	3481	763	java/lang/Exception
    //   3745	3755	763	java/lang/Exception
    //   3768	3780	763	java/lang/Exception
    //   3788	3797	763	java/lang/Exception
    //   3806	3813	763	java/lang/Exception
    //   3817	3823	763	java/lang/Exception
    //   3834	3846	763	java/lang/Exception
    //   3850	3861	763	java/lang/Exception
    //   3865	3878	763	java/lang/Exception
    //   3882	3892	763	java/lang/Exception
    //   3900	3908	763	java/lang/Exception
    //   3912	3920	763	java/lang/Exception
    //   3934	3953	763	java/lang/Exception
    //   3957	3977	763	java/lang/Exception
    //   3981	3988	763	java/lang/Exception
    //   3992	3998	763	java/lang/Exception
    //   4002	4019	763	java/lang/Exception
    //   4030	4043	763	java/lang/Exception
    //   4054	4062	763	java/lang/Exception
    //   4076	4095	763	java/lang/Exception
    //   4099	4119	763	java/lang/Exception
    //   4123	4130	763	java/lang/Exception
    //   4134	4140	763	java/lang/Exception
    //   4144	4161	763	java/lang/Exception
    //   1167	1219	9218	java/lang/Exception
    //   2134	2175	9228	java/lang/Exception
    //   2180	2190	9228	java/lang/Exception
    //   3298	3312	9345	java/lang/Exception
    //   3312	3351	9345	java/lang/Exception
    //   3356	3364	9345	java/lang/Exception
    //   3367	3389	9345	java/lang/Exception
    //   3481	3505	9385	java/lang/Exception
    //   3510	3527	9385	java/lang/Exception
    //   3527	3599	9385	java/lang/Exception
    //   3604	3676	9385	java/lang/Exception
    //   3689	3705	9385	java/lang/Exception
    //   3708	3732	9385	java/lang/Exception
    //   4177	4213	9385	java/lang/Exception
    //   4214	4228	9385	java/lang/Exception
    //   4236	4294	9385	java/lang/Exception
    //   4294	4309	9385	java/lang/Exception
    //   4314	4333	9385	java/lang/Exception
    //   4338	4343	9385	java/lang/Exception
    //   4348	4373	9385	java/lang/Exception
    //   4373	4381	9385	java/lang/Exception
    //   4386	4392	9385	java/lang/Exception
    //   4393	4423	9385	java/lang/Exception
    //   4428	4454	9385	java/lang/Exception
    //   4463	4499	9385	java/lang/Exception
    //   4506	4544	9385	java/lang/Exception
    //   4544	4559	9385	java/lang/Exception
    //   4564	4607	9385	java/lang/Exception
    //   4607	4625	9385	java/lang/Exception
    //   4630	4636	9385	java/lang/Exception
    //   4637	4667	9385	java/lang/Exception
    //   4670	4696	9385	java/lang/Exception
    //   4699	4710	9385	java/lang/Exception
    //   4732	4779	9385	java/lang/Exception
    //   4779	4818	9385	java/lang/Exception
    //   4826	4844	9385	java/lang/Exception
    //   4847	4872	9385	java/lang/Exception
    //   4881	4889	9385	java/lang/Exception
    //   4892	4922	9385	java/lang/Exception
    //   4922	4928	9385	java/lang/Exception
    //   4933	4945	9385	java/lang/Exception
    //   4949	4968	9385	java/lang/Exception
    //   4978	4997	9385	java/lang/Exception
    //   5006	5018	9385	java/lang/Exception
    //   5018	5044	9385	java/lang/Exception
    //   5049	5074	9385	java/lang/Exception
    //   5080	5106	9385	java/lang/Exception
    //   5112	5143	9385	java/lang/Exception
    //   5143	5170	9385	java/lang/Exception
    //   5184	5213	9385	java/lang/Exception
    //   5213	5227	9385	java/lang/Exception
    //   5227	5300	9385	java/lang/Exception
    //   5303	5311	9385	java/lang/Exception
    //   5314	5347	9385	java/lang/Exception
    //   5347	5376	9385	java/lang/Exception
    //   5391	5426	9385	java/lang/Exception
    //   5432	5442	9385	java/lang/Exception
    //   5452	5471	9385	java/lang/Exception
    //   5476	5504	9385	java/lang/Exception
    //   5510	5538	9385	java/lang/Exception
    //   5544	5577	9385	java/lang/Exception
    //   5577	5583	9385	java/lang/Exception
    //   5586	5619	9385	java/lang/Exception
    //   5619	5663	9385	java/lang/Exception
    //   5666	5674	9385	java/lang/Exception
    //   5677	5734	9385	java/lang/Exception
    //   5734	5739	9385	java/lang/Exception
    //   5757	5810	9385	java/lang/Exception
    //   5810	5842	9385	java/lang/Exception
    //   5848	5881	9385	java/lang/Exception
    //   5881	5910	9385	java/lang/Exception
    //   5918	5946	9385	java/lang/Exception
    //   5950	5956	9385	java/lang/Exception
    //   5967	5973	9385	java/lang/Exception
    //   5982	5991	9385	java/lang/Exception
    //   5992	6061	9385	java/lang/Exception
    //   6061	6077	9385	java/lang/Exception
    //   6082	6104	9385	java/lang/Exception
    //   6108	6114	9385	java/lang/Exception
    //   6123	6137	9385	java/lang/Exception
    //   6138	6147	9385	java/lang/Exception
    //   6154	6169	9385	java/lang/Exception
    //   6170	6179	9385	java/lang/Exception
    //   6187	6196	9385	java/lang/Exception
    //   6204	6214	9385	java/lang/Exception
    //   6218	6223	9385	java/lang/Exception
    //   6224	6234	9385	java/lang/Exception
    //   6242	6257	9385	java/lang/Exception
    //   6258	6267	9385	java/lang/Exception
    //   6268	6298	9385	java/lang/Exception
    //   6303	6329	9385	java/lang/Exception
    //   6334	6370	9385	java/lang/Exception
    //   6370	6382	9385	java/lang/Exception
    //   6387	6425	9385	java/lang/Exception
    //   6425	6441	9385	java/lang/Exception
    //   6447	6522	9385	java/lang/Exception
    //   6522	6568	9385	java/lang/Exception
    //   6573	6579	9385	java/lang/Exception
    //   6580	6610	9385	java/lang/Exception
    //   6613	6639	9385	java/lang/Exception
    //   6642	6653	9385	java/lang/Exception
    //   6656	6737	9385	java/lang/Exception
    //   6737	6766	9385	java/lang/Exception
    //   6766	6874	9385	java/lang/Exception
    //   6879	6904	9385	java/lang/Exception
    //   6904	6909	9385	java/lang/Exception
    //   6912	6925	9385	java/lang/Exception
    //   6928	6962	9385	java/lang/Exception
    //   6965	6978	9385	java/lang/Exception
    //   6981	7007	9385	java/lang/Exception
    //   7010	7090	9385	java/lang/Exception
    //   7099	7188	9385	java/lang/Exception
    //   7188	7207	9385	java/lang/Exception
    //   7207	7234	9385	java/lang/Exception
    //   7237	7304	9385	java/lang/Exception
    //   7304	7398	9385	java/lang/Exception
    //   7401	7414	9385	java/lang/Exception
    //   7417	7465	9385	java/lang/Exception
    //   7468	7481	9385	java/lang/Exception
    //   7484	7545	9385	java/lang/Exception
    //   7548	7561	9385	java/lang/Exception
    //   7564	7612	9385	java/lang/Exception
    //   7615	7628	9385	java/lang/Exception
    //   7631	7699	9385	java/lang/Exception
    //   7709	7786	9385	java/lang/Exception
    //   7789	7933	9385	java/lang/Exception
    //   7933	7951	9385	java/lang/Exception
    //   7954	7969	9385	java/lang/Exception
    //   7972	8035	9385	java/lang/Exception
    //   8035	8107	9385	java/lang/Exception
    //   8107	8176	9385	java/lang/Exception
    //   8181	8206	9385	java/lang/Exception
    //   8206	8217	9385	java/lang/Exception
    //   8220	8249	9385	java/lang/Exception
    //   8252	8342	9385	java/lang/Exception
    //   8345	8374	9385	java/lang/Exception
    //   8377	8445	9385	java/lang/Exception
    //   8455	8548	9385	java/lang/Exception
    //   8548	8621	9385	java/lang/Exception
    //   8621	8651	9385	java/lang/Exception
    //   8651	8656	9385	java/lang/Exception
    //   8659	8688	9385	java/lang/Exception
    //   8691	8715	9385	java/lang/Exception
    //   8718	8763	9385	java/lang/Exception
    //   8763	8790	9385	java/lang/Exception
    //   8793	8804	9385	java/lang/Exception
    //   8813	8896	9385	java/lang/Exception
    //   8900	8911	9385	java/lang/Exception
    //   8911	8966	9385	java/lang/Exception
    //   8966	9006	9385	java/lang/Exception
    //   9006	9014	9385	java/lang/Exception
    //   9015	9026	9385	java/lang/Exception
    //   9029	9048	9385	java/lang/Exception
    //   9058	9081	9385	java/lang/Exception
    //   9086	9105	9385	java/lang/Exception
    //   9105	9157	9385	java/lang/Exception
    //   9157	9193	9385	java/lang/Exception
    //   9198	9209	9385	java/lang/Exception
    //   9209	9217	9385	java/lang/Exception
  }
  
  private void updateMediaPaths(MessageObject paramMessageObject, TLRPC.Message paramMessage, String paramString, boolean paramBoolean)
  {
    TLRPC.Message localMessage = paramMessageObject.messageOwner;
    if (paramMessage == null) {}
    label198:
    label269:
    label272:
    label342:
    label1000:
    label1258:
    label1348:
    label1517:
    label1617:
    label1636:
    do
    {
      do
      {
        Object localObject2;
        do
        {
          return;
          int i;
          Object localObject1;
          String str;
          Object localObject3;
          if (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessage.media.photo != null) && ((localMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (localMessage.media.photo != null))
          {
            MessagesStorage.getInstance().putSentFile(paramString, paramMessage.media.photo, 0);
            if ((localMessage.media.photo.sizes.size() == 1) && ((((TLRPC.PhotoSize)localMessage.media.photo.sizes.get(0)).location instanceof TLRPC.TL_fileLocationUnavailable)))
            {
              localMessage.media.photo.sizes = paramMessage.media.photo.sizes;
              paramMessage.message = localMessage.message;
              paramMessage.attachPath = localMessage.attachPath;
              localMessage.media.photo.id = paramMessage.media.photo.id;
              localMessage.media.photo.access_hash = paramMessage.media.photo.access_hash;
              return;
            }
            i = 0;
            if (i < paramMessage.media.photo.sizes.size())
            {
              paramString = (TLRPC.PhotoSize)paramMessage.media.photo.sizes.get(i);
              if ((paramString != null) && (paramString.location != null) && (!(paramString instanceof TLRPC.TL_photoSizeEmpty)) && (paramString.type != null)) {
                break label269;
              }
            }
            do
            {
              i += 1;
              break label198;
              break;
              int j = 0;
              if (j < localMessage.media.photo.sizes.size())
              {
                localObject1 = (TLRPC.PhotoSize)localMessage.media.photo.sizes.get(j);
                if ((localObject1 != null) && (((TLRPC.PhotoSize)localObject1).location != null) && (((TLRPC.PhotoSize)localObject1).type != null)) {
                  break label342;
                }
              }
              while (((((TLRPC.PhotoSize)localObject1).location.volume_id != -2147483648L) || (!paramString.type.equals(((TLRPC.PhotoSize)localObject1).type))) && ((paramString.w != ((TLRPC.PhotoSize)localObject1).w) || (paramString.h != ((TLRPC.PhotoSize)localObject1).h)))
              {
                j += 1;
                break label272;
                break;
              }
              localObject2 = ((TLRPC.PhotoSize)localObject1).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject1).location.local_id;
              str = paramString.location.volume_id + "_" + paramString.location.local_id;
            } while (((String)localObject2).equals(str));
            localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2 + ".jpg");
            if ((paramMessage.media.photo.sizes.size() == 1) || (paramString.w > 90) || (paramString.h > 90)) {}
            for (paramMessageObject = FileLoader.getPathToAttach(paramString);; paramMessageObject = new File(FileLoader.getInstance().getDirectory(4), str + ".jpg"))
            {
              ((File)localObject3).renameTo(paramMessageObject);
              ImageLoader.getInstance().replaceImageInCache((String)localObject2, str, paramString.location, paramBoolean);
              ((TLRPC.PhotoSize)localObject1).location = paramString.location;
              ((TLRPC.PhotoSize)localObject1).size = paramString.size;
              break;
            }
          }
          if ((!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) || (paramMessage.media.document == null) || (!(localMessage.media instanceof TLRPC.TL_messageMediaDocument)) || (localMessage.media.document == null)) {
            break label1636;
          }
          if (MessageObject.isVideoMessage(paramMessage))
          {
            MessagesStorage.getInstance().putSentFile(paramString, paramMessage.media.document, 2);
            paramMessage.attachPath = localMessage.attachPath;
            localObject1 = localMessage.media.document.thumb;
            localObject2 = paramMessage.media.document.thumb;
            if ((localObject1 == null) || (((TLRPC.PhotoSize)localObject1).location == null) || (((TLRPC.PhotoSize)localObject1).location.volume_id != -2147483648L) || (localObject2 == null) || (((TLRPC.PhotoSize)localObject2).location == null) || ((localObject2 instanceof TLRPC.TL_photoSizeEmpty)) || ((localObject1 instanceof TLRPC.TL_photoSizeEmpty))) {
              break label1258;
            }
            str = ((TLRPC.PhotoSize)localObject1).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject1).location.local_id;
            localObject3 = ((TLRPC.PhotoSize)localObject2).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject2).location.local_id;
            if (!str.equals(localObject3))
            {
              new File(FileLoader.getInstance().getDirectory(4), str + ".jpg").renameTo(new File(FileLoader.getInstance().getDirectory(4), (String)localObject3 + ".jpg"));
              ImageLoader.getInstance().replaceImageInCache(str, (String)localObject3, ((TLRPC.PhotoSize)localObject2).location, paramBoolean);
              ((TLRPC.PhotoSize)localObject1).location = ((TLRPC.PhotoSize)localObject2).location;
              ((TLRPC.PhotoSize)localObject1).size = ((TLRPC.PhotoSize)localObject2).size;
            }
            localMessage.media.document.dc_id = paramMessage.media.document.dc_id;
            localMessage.media.document.id = paramMessage.media.document.id;
            localMessage.media.document.access_hash = paramMessage.media.document.access_hash;
            localObject2 = null;
            i = 0;
          }
          for (;;)
          {
            localObject1 = localObject2;
            if (i < localMessage.media.document.attributes.size())
            {
              localObject1 = (TLRPC.DocumentAttribute)localMessage.media.document.attributes.get(i);
              if ((localObject1 instanceof TLRPC.TL_documentAttributeAudio)) {
                localObject1 = ((TLRPC.DocumentAttribute)localObject1).waveform;
              }
            }
            else
            {
              localMessage.media.document.attributes = paramMessage.media.document.attributes;
              if (localObject1 == null) {
                break label1348;
              }
              i = 0;
              while (i < localMessage.media.document.attributes.size())
              {
                localObject2 = (TLRPC.DocumentAttribute)localMessage.media.document.attributes.get(i);
                if ((localObject2 instanceof TLRPC.TL_documentAttributeAudio))
                {
                  ((TLRPC.DocumentAttribute)localObject2).waveform = ((byte[])localObject1);
                  ((TLRPC.DocumentAttribute)localObject2).flags |= 0x4;
                }
                i += 1;
              }
              if (MessageObject.isVoiceMessage(paramMessage)) {
                break;
              }
              MessagesStorage.getInstance().putSentFile(paramString, paramMessage.media.document, 1);
              break;
              if ((localObject1 != null) && (MessageObject.isStickerMessage(paramMessage)) && (((TLRPC.PhotoSize)localObject1).location != null))
              {
                ((TLRPC.PhotoSize)localObject2).location = ((TLRPC.PhotoSize)localObject1).location;
                break label1000;
              }
              if (((localObject1 == null) || (!(((TLRPC.PhotoSize)localObject1).location instanceof TLRPC.TL_fileLocationUnavailable))) && (!(localObject1 instanceof TLRPC.TL_photoSizeEmpty))) {
                break label1000;
              }
              localMessage.media.document.thumb = paramMessage.media.document.thumb;
              break label1000;
            }
            i += 1;
          }
          localMessage.media.document.size = paramMessage.media.document.size;
          localMessage.media.document.mime_type = paramMessage.media.document.mime_type;
          if (((paramMessage.flags & 0x4) == 0) && (MessageObject.isOut(paramMessage)))
          {
            if (!MessageObject.isNewGifDocument(paramMessage.media.document)) {
              break label1517;
            }
            StickersQuery.addRecentGif(paramMessage.media.document, paramMessage.date);
          }
          for (;;)
          {
            if ((localMessage.attachPath == null) || (!localMessage.attachPath.startsWith(FileLoader.getInstance().getDirectory(4).getAbsolutePath()))) {
              break label1617;
            }
            localObject1 = new File(localMessage.attachPath);
            localObject2 = FileLoader.getPathToAttach(paramMessage.media.document);
            if (((File)localObject1).renameTo((File)localObject2)) {
              break;
            }
            paramMessage.attachPath = localMessage.attachPath;
            paramMessage.message = localMessage.message;
            return;
            if (MessageObject.isStickerDocument(paramMessage.media.document)) {
              StickersQuery.addRecentSticker(0, paramMessage.media.document, paramMessage.date);
            }
          }
          if (MessageObject.isVideoMessage(paramMessage))
          {
            paramMessageObject.attachPathExists = true;
            return;
          }
          paramMessageObject.mediaExists = paramMessageObject.attachPathExists;
          paramMessageObject.attachPathExists = false;
          localMessage.attachPath = "";
        } while ((paramString == null) || (!paramString.startsWith("http")));
        MessagesStorage.getInstance().addRecentLocalFile(paramString, ((File)localObject2).toString(), localMessage.media.document);
        return;
        paramMessage.attachPath = localMessage.attachPath;
        paramMessage.message = localMessage.message;
        return;
        if (((paramMessage.media instanceof TLRPC.TL_messageMediaContact)) && ((localMessage.media instanceof TLRPC.TL_messageMediaContact)))
        {
          localMessage.media = paramMessage.media;
          return;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
        {
          localMessage.media = paramMessage.media;
          return;
        }
      } while (!(paramMessage.media instanceof TLRPC.TL_messageMediaGame));
      localMessage.media = paramMessage.media;
    } while ((!(localMessage.media instanceof TLRPC.TL_messageMediaGame)) || (TextUtils.isEmpty(paramMessage.message)));
    localMessage.entities = paramMessage.entities;
    localMessage.message = paramMessage.message;
  }
  
  public void cancelSendingMessage(MessageObject paramMessageObject)
  {
    Object localObject1 = null;
    boolean bool = false;
    Iterator localIterator = this.delayedMessages.entrySet().iterator();
    label154:
    while (localIterator.hasNext())
    {
      Object localObject2 = (Map.Entry)localIterator.next();
      ArrayList localArrayList = (ArrayList)((Map.Entry)localObject2).getValue();
      int i = 0;
      for (;;)
      {
        if (i >= localArrayList.size()) {
          break label154;
        }
        DelayedMessage localDelayedMessage = (DelayedMessage)localArrayList.get(i);
        if (localDelayedMessage.obj.getId() == paramMessageObject.getId())
        {
          localArrayList.remove(i);
          MediaController.getInstance().cancelVideoConvert(localDelayedMessage.obj);
          if (localArrayList.size() != 0) {
            break;
          }
          localObject2 = (String)((Map.Entry)localObject2).getKey();
          localObject1 = localObject2;
          if (localDelayedMessage.sendEncryptedRequest == null) {
            break;
          }
          bool = true;
          localObject1 = localObject2;
          break;
        }
        i += 1;
      }
    }
    if (localObject1 != null)
    {
      if (!((String)localObject1).startsWith("http")) {
        break label229;
      }
      ImageLoader.getInstance().cancelLoadHttpFile((String)localObject1);
    }
    for (;;)
    {
      stopVideoService((String)localObject1);
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).add(Integer.valueOf(paramMessageObject.getId()));
      MessagesController.getInstance().deleteMessages((ArrayList)localObject1, null, null, paramMessageObject.messageOwner.to_id.channel_id);
      return;
      label229:
      FileLoader.getInstance().cancelUploadFile((String)localObject1, bool);
    }
  }
  
  public void checkUnsentMessages()
  {
    MessagesStorage.getInstance().getUnsentMessages(1000);
  }
  
  public void cleanup()
  {
    this.delayedMessages.clear();
    this.unsentMessages.clear();
    this.sendingMessages.clear();
    this.waitingForLocation.clear();
    this.waitingForCallback.clear();
    this.currentChatInfo = null;
    this.locationProvider.stop();
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    final Object localObject2;
    final Object localObject3;
    label99:
    label143:
    int i;
    label188:
    label426:
    long l;
    if (paramInt == NotificationCenter.FileDidUpload)
    {
      localObject2 = (String)paramVarArgs[0];
      localObject3 = (TLRPC.InputFile)paramVarArgs[1];
      TLRPC.InputEncryptedFile localInputEncryptedFile = (TLRPC.InputEncryptedFile)paramVarArgs[2];
      ArrayList localArrayList = (ArrayList)this.delayedMessages.get(localObject2);
      if (localArrayList != null)
      {
        paramInt = 0;
        if (paramInt < localArrayList.size())
        {
          DelayedMessage localDelayedMessage = (DelayedMessage)localArrayList.get(paramInt);
          localObject1 = null;
          if ((localDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia))
          {
            localObject1 = ((TLRPC.TL_messages_sendMedia)localDelayedMessage.sendRequest).media;
            if ((localObject3 == null) || (localObject1 == null)) {
              break label426;
            }
            if (localDelayedMessage.type != 0) {
              break label188;
            }
            ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
            performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
            localArrayList.remove(paramInt);
            i = paramInt - 1;
          }
          for (;;)
          {
            paramInt = i + 1;
            break;
            if (!(localDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendBroadcast)) {
              break label99;
            }
            localObject1 = ((TLRPC.TL_messages_sendBroadcast)localDelayedMessage.sendRequest).media;
            break label99;
            if (localDelayedMessage.type == 1)
            {
              if (((TLRPC.InputMedia)localObject1).file == null)
              {
                ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
                if ((((TLRPC.InputMedia)localObject1).thumb == null) && (localDelayedMessage.location != null))
                {
                  performSendDelayedMessage(localDelayedMessage);
                  break label143;
                }
                performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
                break label143;
              }
              ((TLRPC.InputMedia)localObject1).thumb = ((TLRPC.InputFile)localObject3);
              performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
              break label143;
            }
            if (localDelayedMessage.type == 2)
            {
              if (((TLRPC.InputMedia)localObject1).file == null)
              {
                ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
                if ((((TLRPC.InputMedia)localObject1).thumb == null) && (localDelayedMessage.location != null))
                {
                  performSendDelayedMessage(localDelayedMessage);
                  break label143;
                }
                performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
                break label143;
              }
              ((TLRPC.InputMedia)localObject1).thumb = ((TLRPC.InputFile)localObject3);
              performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
              break label143;
            }
            if (localDelayedMessage.type != 3) {
              break label143;
            }
            ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
            performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
            break label143;
            i = paramInt;
            if (localInputEncryptedFile != null)
            {
              i = paramInt;
              if (localDelayedMessage.sendEncryptedRequest != null)
              {
                if (((localDelayedMessage.sendEncryptedRequest.media instanceof TLRPC.TL_decryptedMessageMediaVideo)) || ((localDelayedMessage.sendEncryptedRequest.media instanceof TLRPC.TL_decryptedMessageMediaPhoto)) || ((localDelayedMessage.sendEncryptedRequest.media instanceof TLRPC.TL_decryptedMessageMediaDocument)))
                {
                  l = ((Long)paramVarArgs[5]).longValue();
                  localDelayedMessage.sendEncryptedRequest.media.size = ((int)l);
                }
                localDelayedMessage.sendEncryptedRequest.media.key = ((byte[])paramVarArgs[3]);
                localDelayedMessage.sendEncryptedRequest.media.iv = ((byte[])paramVarArgs[4]);
                SecretChatHelper.getInstance().performSendEncryptedRequest(localDelayedMessage.sendEncryptedRequest, localDelayedMessage.obj.messageOwner, localDelayedMessage.encryptedChat, localInputEncryptedFile, localDelayedMessage.originalPath, localDelayedMessage.obj);
                localArrayList.remove(paramInt);
                i = paramInt - 1;
              }
            }
          }
        }
        if (localArrayList.isEmpty()) {
          this.delayedMessages.remove(localObject2);
        }
      }
    }
    label982:
    label1169:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                boolean bool;
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          return;
                          if (paramInt != NotificationCenter.FileDidFailUpload) {
                            break;
                          }
                          localObject1 = (String)paramVarArgs[0];
                          bool = ((Boolean)paramVarArgs[1]).booleanValue();
                          paramVarArgs = (ArrayList)this.delayedMessages.get(localObject1);
                        } while (paramVarArgs == null);
                        for (paramInt = 0; paramInt < paramVarArgs.size(); paramInt = i + 1)
                        {
                          localObject2 = (DelayedMessage)paramVarArgs.get(paramInt);
                          if ((!bool) || (((DelayedMessage)localObject2).sendEncryptedRequest == null))
                          {
                            i = paramInt;
                            if (!bool)
                            {
                              i = paramInt;
                              if (((DelayedMessage)localObject2).sendRequest == null) {}
                            }
                          }
                          else
                          {
                            MessagesStorage.getInstance().markMessageAsSendError(((DelayedMessage)localObject2).obj.messageOwner);
                            ((DelayedMessage)localObject2).obj.messageOwner.send_state = 2;
                            paramVarArgs.remove(paramInt);
                            i = paramInt - 1;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((DelayedMessage)localObject2).obj.getId()) });
                            processSentMessage(((DelayedMessage)localObject2).obj.getId());
                          }
                        }
                      } while (!paramVarArgs.isEmpty());
                      this.delayedMessages.remove(localObject1);
                      return;
                      if (paramInt != NotificationCenter.FilePreparingStarted) {
                        break;
                      }
                      localObject1 = (MessageObject)paramVarArgs[0];
                      paramVarArgs = (String)paramVarArgs[1];
                      paramVarArgs = (ArrayList)this.delayedMessages.get(((MessageObject)localObject1).messageOwner.attachPath);
                    } while (paramVarArgs == null);
                    paramInt = 0;
                    for (;;)
                    {
                      if (paramInt < paramVarArgs.size())
                      {
                        localObject2 = (DelayedMessage)paramVarArgs.get(paramInt);
                        if (((DelayedMessage)localObject2).obj == localObject1)
                        {
                          ((DelayedMessage)localObject2).videoEditedInfo = null;
                          performSendDelayedMessage((DelayedMessage)localObject2);
                          paramVarArgs.remove(paramInt);
                        }
                      }
                      else
                      {
                        if (!paramVarArgs.isEmpty()) {
                          break;
                        }
                        this.delayedMessages.remove(((MessageObject)localObject1).messageOwner.attachPath);
                        return;
                      }
                      paramInt += 1;
                    }
                    if (paramInt != NotificationCenter.FileNewChunkAvailable) {
                      break label1169;
                    }
                    localObject1 = (MessageObject)paramVarArgs[0];
                    localObject2 = (String)paramVarArgs[1];
                    l = ((Long)paramVarArgs[2]).longValue();
                    if ((int)((MessageObject)localObject1).getDialogId() != 0) {
                      break;
                    }
                    bool = true;
                    FileLoader.getInstance().checkUploadNewDataAvailable((String)localObject2, bool, l);
                  } while (l == 0L);
                  paramVarArgs = (ArrayList)this.delayedMessages.get(((MessageObject)localObject1).messageOwner.attachPath);
                } while (paramVarArgs == null);
                paramInt = 0;
                for (;;)
                {
                  if (paramInt < paramVarArgs.size())
                  {
                    localObject2 = (DelayedMessage)paramVarArgs.get(paramInt);
                    if (((DelayedMessage)localObject2).obj == localObject1)
                    {
                      ((DelayedMessage)localObject2).obj.videoEditedInfo = null;
                      ((DelayedMessage)localObject2).obj.messageOwner.message = "-1";
                      ((DelayedMessage)localObject2).obj.messageOwner.media.document.size = ((int)l);
                      localObject3 = new ArrayList();
                      ((ArrayList)localObject3).add(((DelayedMessage)localObject2).obj.messageOwner);
                      MessagesStorage.getInstance().putMessages((ArrayList)localObject3, false, true, false, 0);
                    }
                  }
                  else
                  {
                    if (!paramVarArgs.isEmpty()) {
                      break;
                    }
                    this.delayedMessages.remove(((MessageObject)localObject1).messageOwner.attachPath);
                    return;
                    bool = false;
                    break label982;
                  }
                  paramInt += 1;
                }
                if (paramInt != NotificationCenter.FilePreparingFailed) {
                  break;
                }
                localObject1 = (MessageObject)paramVarArgs[0];
                paramVarArgs = (String)paramVarArgs[1];
                stopVideoService(((MessageObject)localObject1).messageOwner.attachPath);
                localObject2 = (ArrayList)this.delayedMessages.get(paramVarArgs);
              } while (localObject2 == null);
              for (paramInt = 0; paramInt < ((ArrayList)localObject2).size(); paramInt = i + 1)
              {
                localObject3 = (DelayedMessage)((ArrayList)localObject2).get(paramInt);
                i = paramInt;
                if (((DelayedMessage)localObject3).obj == localObject1)
                {
                  MessagesStorage.getInstance().markMessageAsSendError(((DelayedMessage)localObject3).obj.messageOwner);
                  ((DelayedMessage)localObject3).obj.messageOwner.send_state = 2;
                  ((ArrayList)localObject2).remove(paramInt);
                  i = paramInt - 1;
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((DelayedMessage)localObject3).obj.getId()) });
                  processSentMessage(((DelayedMessage)localObject3).obj.getId());
                }
              }
            } while (!((ArrayList)localObject2).isEmpty());
            this.delayedMessages.remove(paramVarArgs);
            return;
            if (paramInt != NotificationCenter.httpFileDidLoaded) {
              break;
            }
            paramVarArgs = (String)paramVarArgs[0];
            localObject1 = (ArrayList)this.delayedMessages.get(paramVarArgs);
          } while (localObject1 == null);
          paramInt = 0;
          if (paramInt < ((ArrayList)localObject1).size())
          {
            localObject2 = (DelayedMessage)((ArrayList)localObject1).get(paramInt);
            if (((DelayedMessage)localObject2).type == 0)
            {
              localObject3 = Utilities.MD5(((DelayedMessage)localObject2).httpLocation) + "." + ImageLoader.getHttpUrlExtension(((DelayedMessage)localObject2).httpLocation, "file");
              localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
              Utilities.globalQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      if (this.val$photo != null)
                      {
                        SendMessagesHelper.2.this.val$message.httpLocation = null;
                        SendMessagesHelper.2.this.val$message.obj.messageOwner.media.photo = this.val$photo;
                        SendMessagesHelper.2.this.val$message.obj.messageOwner.attachPath = SendMessagesHelper.2.this.val$cacheFile.toString();
                        SendMessagesHelper.2.this.val$message.location = ((TLRPC.PhotoSize)this.val$photo.sizes.get(this.val$photo.sizes.size() - 1)).location;
                        ArrayList localArrayList = new ArrayList();
                        localArrayList.add(SendMessagesHelper.2.this.val$message.obj.messageOwner);
                        MessagesStorage.getInstance().putMessages(localArrayList, false, true, false, 0);
                        SendMessagesHelper.this.performSendDelayedMessage(SendMessagesHelper.2.this.val$message);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, new Object[] { SendMessagesHelper.2.this.val$message.obj });
                        return;
                      }
                      FileLog.e("tmessages", "can't load image " + SendMessagesHelper.2.this.val$message.httpLocation + " to file " + SendMessagesHelper.2.this.val$cacheFile.toString());
                      MessagesStorage.getInstance().markMessageAsSendError(SendMessagesHelper.2.this.val$message.obj.messageOwner);
                      SendMessagesHelper.2.this.val$message.obj.messageOwner.send_state = 2;
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(SendMessagesHelper.2.this.val$message.obj.getId()) });
                      SendMessagesHelper.this.processSentMessage(SendMessagesHelper.2.this.val$message.obj.getId());
                    }
                  });
                }
              });
            }
            for (;;)
            {
              paramInt += 1;
              break;
              if (((DelayedMessage)localObject2).type == 2)
              {
                localObject3 = Utilities.MD5(((DelayedMessage)localObject2).httpLocation) + ".gif";
                localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
                Utilities.globalQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    boolean bool = true;
                    if ((localObject2.documentLocation.thumb.location instanceof TLRPC.TL_fileLocationUnavailable)) {}
                    for (;;)
                    {
                      try
                      {
                        Bitmap localBitmap = ImageLoader.loadBitmap(localObject3.getAbsolutePath(), null, 90.0F, 90.0F, true);
                        if (localBitmap != null)
                        {
                          TLRPC.TL_document localTL_document = localObject2.documentLocation;
                          if (localObject2.sendEncryptedRequest == null) {
                            continue;
                          }
                          localTL_document.thumb = ImageLoader.scaleAndSaveImage(localBitmap, 90.0F, 90.0F, 55, bool);
                          localBitmap.recycle();
                        }
                      }
                      catch (Exception localException)
                      {
                        localObject2.documentLocation.thumb = null;
                        FileLog.e("tmessages", localException);
                        continue;
                      }
                      if (localObject2.documentLocation.thumb == null)
                      {
                        localObject2.documentLocation.thumb = new TLRPC.TL_photoSizeEmpty();
                        localObject2.documentLocation.thumb.type = "s";
                      }
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          SendMessagesHelper.3.this.val$message.httpLocation = null;
                          SendMessagesHelper.3.this.val$message.obj.messageOwner.attachPath = SendMessagesHelper.3.this.val$cacheFile.toString();
                          SendMessagesHelper.3.this.val$message.location = SendMessagesHelper.3.this.val$message.documentLocation.thumb.location;
                          ArrayList localArrayList = new ArrayList();
                          localArrayList.add(SendMessagesHelper.3.this.val$message.obj.messageOwner);
                          MessagesStorage.getInstance().putMessages(localArrayList, false, true, false, 0);
                          SendMessagesHelper.this.performSendDelayedMessage(SendMessagesHelper.3.this.val$message);
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, new Object[] { SendMessagesHelper.3.this.val$message.obj });
                        }
                      });
                      return;
                      bool = false;
                    }
                  }
                });
              }
            }
          }
          this.delayedMessages.remove(paramVarArgs);
          return;
          if (paramInt != NotificationCenter.FileDidLoaded) {
            break;
          }
          paramVarArgs = (String)paramVarArgs[0];
          localObject1 = (ArrayList)this.delayedMessages.get(paramVarArgs);
        } while (localObject1 == null);
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject1).size())
        {
          performSendDelayedMessage((DelayedMessage)((ArrayList)localObject1).get(paramInt));
          paramInt += 1;
        }
        this.delayedMessages.remove(paramVarArgs);
        return;
      } while ((paramInt != NotificationCenter.httpFileDidFailedLoad) && (paramInt != NotificationCenter.FileDidFailedLoad));
      paramVarArgs = (String)paramVarArgs[0];
      localObject1 = (ArrayList)this.delayedMessages.get(paramVarArgs);
    } while (localObject1 == null);
    Object localObject1 = ((ArrayList)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (DelayedMessage)((Iterator)localObject1).next();
      MessagesStorage.getInstance().markMessageAsSendError(((DelayedMessage)localObject2).obj.messageOwner);
      ((DelayedMessage)localObject2).obj.messageOwner.send_state = 2;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((DelayedMessage)localObject2).obj.getId()) });
      processSentMessage(((DelayedMessage)localObject2).obj.getId());
    }
    this.delayedMessages.remove(paramVarArgs);
  }
  
  public int editMessage(MessageObject paramMessageObject, String paramString, boolean paramBoolean, final BaseFragment paramBaseFragment, ArrayList<TLRPC.MessageEntity> paramArrayList, final Runnable paramRunnable)
  {
    boolean bool = false;
    if ((paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null) || (paramRunnable == null)) {
      return 0;
    }
    TLRPC.TL_messages_editMessage localTL_messages_editMessage = new TLRPC.TL_messages_editMessage();
    localTL_messages_editMessage.peer = MessagesController.getInputPeer((int)paramMessageObject.getDialogId());
    localTL_messages_editMessage.message = paramString;
    localTL_messages_editMessage.flags |= 0x800;
    localTL_messages_editMessage.id = paramMessageObject.getId();
    if (!paramBoolean) {
      bool = true;
    }
    localTL_messages_editMessage.no_webpage = bool;
    if (paramArrayList != null)
    {
      localTL_messages_editMessage.entities = paramArrayList;
      localTL_messages_editMessage.flags |= 0x8;
    }
    ConnectionsManager.getInstance().sendRequest(localTL_messages_editMessage, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            SendMessagesHelper.5.this.val$callback.run();
          }
        });
        if (paramAnonymousTL_error == null) {
          MessagesController.getInstance().processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
        }
        while (paramAnonymousTL_error.text.equals("MESSAGE_NOT_MODIFIED")) {
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(SendMessagesHelper.5.this.val$fragment.getParentActivity());
            localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
            localBuilder.setMessage(LocaleController.getString("EditMessageError", 2131165595));
            localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
            SendMessagesHelper.5.this.val$fragment.showDialog(localBuilder.create());
          }
        });
      }
    });
  }
  
  public TLRPC.TL_photo generatePhotoSizes(String paramString, Uri paramUri)
  {
    Bitmap localBitmap2 = ImageLoader.loadBitmap(paramString, paramUri, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), true);
    Bitmap localBitmap1 = localBitmap2;
    if (localBitmap2 == null)
    {
      localBitmap1 = localBitmap2;
      if (AndroidUtilities.getPhotoSize() != 800) {
        localBitmap1 = ImageLoader.loadBitmap(paramString, paramUri, 800.0F, 800.0F, true);
      }
    }
    paramString = new ArrayList();
    paramUri = ImageLoader.scaleAndSaveImage(localBitmap1, 90.0F, 90.0F, 55, true);
    if (paramUri != null) {
      paramString.add(paramUri);
    }
    paramUri = ImageLoader.scaleAndSaveImage(localBitmap1, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
    if (paramUri != null) {
      paramString.add(paramUri);
    }
    if (localBitmap1 != null) {
      localBitmap1.recycle();
    }
    if (paramString.isEmpty()) {
      return null;
    }
    UserConfig.saveConfig(false);
    paramUri = new TLRPC.TL_photo();
    paramUri.date = ConnectionsManager.getInstance().getCurrentTime();
    paramUri.sizes = paramString;
    return paramUri;
  }
  
  protected ArrayList<DelayedMessage> getDelayedMessages(String paramString)
  {
    return (ArrayList)this.delayedMessages.get(paramString);
  }
  
  protected long getNextRandomId()
  {
    for (long l = 0L; l == 0L; l = Utilities.random.nextLong()) {}
    return l;
  }
  
  public boolean isSendingCallback(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    return (paramMessageObject != null) && (paramKeyboardButton != null) && (this.waitingForCallback.containsKey(paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data)));
  }
  
  public boolean isSendingCurrentLocation(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    return (paramMessageObject != null) && (paramKeyboardButton != null) && (this.waitingForLocation.containsKey(paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data)));
  }
  
  public boolean isSendingMessage(int paramInt)
  {
    return this.sendingMessages.containsKey(Integer.valueOf(paramInt));
  }
  
  public void processForwardFromMyName(MessageObject paramMessageObject, long paramLong)
  {
    if (paramMessageObject == null) {
      return;
    }
    if ((paramMessageObject.messageOwner.media != null) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)))
    {
      if ((paramMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photo))
      {
        sendMessage((TLRPC.TL_photo)paramMessageObject.messageOwner.media.photo, null, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if ((paramMessageObject.messageOwner.media.document instanceof TLRPC.TL_document))
      {
        sendMessage((TLRPC.TL_document)paramMessageObject.messageOwner.media.document, null, paramMessageObject.messageOwner.attachPath, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)))
      {
        sendMessage(paramMessageObject.messageOwner.media, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if (paramMessageObject.messageOwner.media.phone_number != null)
      {
        localObject = new TLRPC.TL_userContact_old2();
        ((TLRPC.User)localObject).phone = paramMessageObject.messageOwner.media.phone_number;
        ((TLRPC.User)localObject).first_name = paramMessageObject.messageOwner.media.first_name;
        ((TLRPC.User)localObject).last_name = paramMessageObject.messageOwner.media.last_name;
        ((TLRPC.User)localObject).id = paramMessageObject.messageOwner.media.user_id;
        sendMessage((TLRPC.User)localObject, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      localObject = new ArrayList();
      ((ArrayList)localObject).add(paramMessageObject);
      sendMessage((ArrayList)localObject, paramLong);
      return;
    }
    if (paramMessageObject.messageOwner.message != null)
    {
      localObject = null;
      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
        localObject = paramMessageObject.messageOwner.media.webpage;
      }
      sendMessage(paramMessageObject.messageOwner.message, paramLong, paramMessageObject.replyMessageObject, (TLRPC.WebPage)localObject, true, paramMessageObject.messageOwner.entities, null, null);
      return;
    }
    Object localObject = new ArrayList();
    ((ArrayList)localObject).add(paramMessageObject);
    sendMessage((ArrayList)localObject, paramLong);
  }
  
  protected void processSentMessage(int paramInt)
  {
    int i = this.unsentMessages.size();
    this.unsentMessages.remove(Integer.valueOf(paramInt));
    if ((i != 0) && (this.unsentMessages.size() == 0)) {
      checkUnsentMessages();
    }
  }
  
  protected void processUnsentMessages(final ArrayList<TLRPC.Message> paramArrayList, final ArrayList<TLRPC.User> paramArrayList1, final ArrayList<TLRPC.Chat> paramArrayList2, final ArrayList<TLRPC.EncryptedChat> paramArrayList3)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().putUsers(paramArrayList1, true);
        MessagesController.getInstance().putChats(paramArrayList2, true);
        MessagesController.getInstance().putEncryptedChats(paramArrayList3, true);
        int i = 0;
        while (i < paramArrayList.size())
        {
          MessageObject localMessageObject = new MessageObject((TLRPC.Message)paramArrayList.get(i), null, false);
          SendMessagesHelper.this.retrySendMessage(localMessageObject, true);
          i += 1;
        }
      }
    });
  }
  
  protected void putToSendingMessages(TLRPC.Message paramMessage)
  {
    this.sendingMessages.put(Integer.valueOf(paramMessage.id), paramMessage);
  }
  
  protected void removeFromSendingMessages(int paramInt)
  {
    this.sendingMessages.remove(Integer.valueOf(paramInt));
  }
  
  public boolean retrySendMessage(MessageObject paramMessageObject, boolean paramBoolean)
  {
    if (paramMessageObject.getId() >= 0) {
      return false;
    }
    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction))
    {
      int i = (int)(paramMessageObject.getDialogId() >> 32);
      TLRPC.EncryptedChat localEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i));
      if (localEncryptedChat == null)
      {
        MessagesStorage.getInstance().markMessageAsSendError(paramMessageObject.messageOwner);
        paramMessageObject.messageOwner.send_state = 2;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramMessageObject.getId()) });
        processSentMessage(paramMessageObject.getId());
        return false;
      }
      if (paramMessageObject.messageOwner.random_id == 0L) {
        paramMessageObject.messageOwner.random_id = getNextRandomId();
      }
      if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
        SecretChatHelper.getInstance().sendTTLMessage(localEncryptedChat, paramMessageObject.messageOwner);
      }
      for (;;)
      {
        return true;
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionDeleteMessages)) {
          SecretChatHelper.getInstance().sendMessagesDeleteMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
        } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionFlushHistory)) {
          SecretChatHelper.getInstance().sendClearHistoryMessage(localEncryptedChat, paramMessageObject.messageOwner);
        } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNotifyLayer)) {
          SecretChatHelper.getInstance().sendNotifyLayerMessage(localEncryptedChat, paramMessageObject.messageOwner);
        } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionReadMessages)) {
          SecretChatHelper.getInstance().sendMessagesReadMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
        } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) {
          SecretChatHelper.getInstance().sendScreenshotMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
        } else if ((!(paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionTyping)) && (!(paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionResend))) {
          if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionCommitKey)) {
            SecretChatHelper.getInstance().sendCommitKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
          } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAbortKey)) {
            SecretChatHelper.getInstance().sendAbortKeyMessage(localEncryptedChat, paramMessageObject.messageOwner, 0L);
          } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionRequestKey)) {
            SecretChatHelper.getInstance().sendRequestKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
          } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAcceptKey)) {
            SecretChatHelper.getInstance().sendAcceptKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
          } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNoop)) {
            SecretChatHelper.getInstance().sendNoopMessage(localEncryptedChat, paramMessageObject.messageOwner);
          }
        }
      }
    }
    if (paramBoolean) {
      this.unsentMessages.put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
    }
    sendMessage(paramMessageObject);
    return true;
  }
  
  public void sendCallback(final MessageObject paramMessageObject, final TLRPC.KeyboardButton paramKeyboardButton, final ChatActivity paramChatActivity)
  {
    if ((paramMessageObject == null) || (paramKeyboardButton == null) || (paramChatActivity == null)) {
      return;
    }
    final String str = paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data);
    this.waitingForCallback.put(str, paramMessageObject);
    TLRPC.TL_messages_getBotCallbackAnswer localTL_messages_getBotCallbackAnswer = new TLRPC.TL_messages_getBotCallbackAnswer();
    localTL_messages_getBotCallbackAnswer.peer = MessagesController.getInputPeer((int)paramMessageObject.getDialogId());
    localTL_messages_getBotCallbackAnswer.msg_id = paramMessageObject.getId();
    localTL_messages_getBotCallbackAnswer.game = (paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame);
    if (paramKeyboardButton.data != null)
    {
      localTL_messages_getBotCallbackAnswer.flags |= 0x1;
      localTL_messages_getBotCallbackAnswer.data = paramKeyboardButton.data;
    }
    ConnectionsManager.getInstance().sendRequest(localTL_messages_getBotCallbackAnswer, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            Object localObject1 = null;
            boolean bool = true;
            Object localObject2;
            if (paramAnonymousTLObject != null)
            {
              localObject2 = (TLRPC.TL_messages_botCallbackAnswer)paramAnonymousTLObject;
              if (((TLRPC.TL_messages_botCallbackAnswer)localObject2).message == null) {
                break label227;
              }
              if (!((TLRPC.TL_messages_botCallbackAnswer)localObject2).alert) {
                break label146;
              }
              if (SendMessagesHelper.6.this.val$parentFragment.getParentActivity() == null) {
                return;
              }
              localObject1 = new AlertDialog.Builder(SendMessagesHelper.6.this.val$parentFragment.getParentActivity());
              ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165299));
              ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
              ((AlertDialog.Builder)localObject1).setMessage(((TLRPC.TL_messages_botCallbackAnswer)localObject2).message);
              SendMessagesHelper.6.this.val$parentFragment.showDialog(((AlertDialog.Builder)localObject1).create());
            }
            for (;;)
            {
              SendMessagesHelper.this.waitingForCallback.remove(SendMessagesHelper.6.this.val$key);
              return;
              label146:
              int i = SendMessagesHelper.6.this.val$messageObject.messageOwner.from_id;
              if (SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id != 0) {
                i = SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id;
              }
              localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(i));
              if (localObject1 == null) {
                break;
              }
              SendMessagesHelper.6.this.val$parentFragment.showAlert((TLRPC.User)localObject1, ((TLRPC.TL_messages_botCallbackAnswer)localObject2).message);
              continue;
              label227:
              if (((TLRPC.TL_messages_botCallbackAnswer)localObject2).url != null)
              {
                if (SendMessagesHelper.6.this.val$parentFragment.getParentActivity() == null) {
                  break;
                }
                i = SendMessagesHelper.6.this.val$messageObject.messageOwner.from_id;
                if (SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id != 0) {
                  i = SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id;
                }
                Object localObject3 = MessagesController.getInstance().getUser(Integer.valueOf(i));
                int j;
                label319:
                MessageObject localMessageObject;
                if ((localObject3 != null) && (((TLRPC.User)localObject3).verified))
                {
                  j = 1;
                  if (!(SendMessagesHelper.6.this.val$button instanceof TLRPC.TL_keyboardButtonGame)) {
                    break label466;
                  }
                  if ((SendMessagesHelper.6.this.val$messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {
                    localObject1 = SendMessagesHelper.6.this.val$messageObject.messageOwner.media.game;
                  }
                  if (localObject1 == null) {
                    break;
                  }
                  localObject3 = SendMessagesHelper.6.this.val$parentFragment;
                  localMessageObject = SendMessagesHelper.6.this.val$messageObject;
                  localObject2 = ((TLRPC.TL_messages_botCallbackAnswer)localObject2).url;
                  if ((j != 0) || (!ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("askgame_" + i, true))) {
                    break label461;
                  }
                }
                for (;;)
                {
                  ((ChatActivity)localObject3).showOpenGameAlert((TLRPC.TL_game)localObject1, localMessageObject, (String)localObject2, bool, i);
                  break;
                  j = 0;
                  break label319;
                  label461:
                  bool = false;
                }
                label466:
                SendMessagesHelper.6.this.val$parentFragment.showOpenUrlAlert(((TLRPC.TL_messages_botCallbackAnswer)localObject2).url, false);
              }
            }
          }
        });
      }
    }, 2);
  }
  
  public void sendCurrentLocation(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    paramKeyboardButton = paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data);
    this.waitingForLocation.put(paramKeyboardButton, paramMessageObject);
    this.locationProvider.start();
  }
  
  public void sendGame(TLRPC.InputPeer paramInputPeer, TLRPC.TL_inputMediaGame paramTL_inputMediaGame, final long paramLong1, long paramLong2)
  {
    if ((paramInputPeer == null) || (paramTL_inputMediaGame == null)) {
      return;
    }
    TLRPC.TL_messages_sendMedia localTL_messages_sendMedia = new TLRPC.TL_messages_sendMedia();
    localTL_messages_sendMedia.peer = paramInputPeer;
    if ((localTL_messages_sendMedia.peer instanceof TLRPC.TL_inputPeerChannel)) {
      localTL_messages_sendMedia.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + paramInputPeer.channel_id, false);
    }
    long l;
    Object localObject;
    if (paramLong1 != 0L)
    {
      l = paramLong1;
      localTL_messages_sendMedia.random_id = l;
      localTL_messages_sendMedia.media = paramTL_inputMediaGame;
      if (paramLong2 != 0L) {
        break label211;
      }
      localObject = null;
    }
    for (;;)
    {
      try
      {
        localNativeByteBuffer = new NativeByteBuffer(paramInputPeer.getObjectSize() + paramTL_inputMediaGame.getObjectSize() + 4 + 8);
      }
      catch (Exception paramTL_inputMediaGame)
      {
        paramInputPeer = (TLRPC.InputPeer)localObject;
      }
      try
      {
        localNativeByteBuffer.writeInt32(3);
        localNativeByteBuffer.writeInt64(paramLong1);
        paramInputPeer.serializeToStream(localNativeByteBuffer);
        paramTL_inputMediaGame.serializeToStream(localNativeByteBuffer);
        paramInputPeer = localNativeByteBuffer;
        paramLong1 = MessagesStorage.getInstance().createPendingTask(paramInputPeer);
        ConnectionsManager.getInstance().sendRequest(localTL_messages_sendMedia, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null) {
              MessagesController.getInstance().processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
            }
            if (paramLong1 != 0L) {
              MessagesStorage.getInstance().removePendingTask(paramLong1);
            }
          }
        });
        return;
      }
      catch (Exception paramTL_inputMediaGame)
      {
        for (;;)
        {
          paramInputPeer = localNativeByteBuffer;
        }
      }
      l = getNextRandomId();
      break;
      FileLog.e("tmessages", paramTL_inputMediaGame);
      continue;
      label211:
      paramLong1 = paramLong2;
    }
  }
  
  public void sendMessage(String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.WebPage paramWebPage, boolean paramBoolean, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(paramString, null, null, null, null, null, null, paramLong, null, paramMessageObject, paramWebPage, paramBoolean, null, paramArrayList, paramReplyMarkup, paramHashMap);
  }
  
  public void sendMessage(ArrayList<MessageObject> paramArrayList, final long paramLong)
  {
    if (((int)paramLong == 0) || (paramArrayList == null) || (paramArrayList.isEmpty())) {}
    final TLRPC.Peer localPeer;
    boolean bool1;
    boolean bool2;
    do
    {
      return;
      i = (int)paramLong;
      localPeer = MessagesController.getPeer((int)paramLong);
      bool1 = false;
      bool2 = false;
      if (i <= 0) {
        break;
      }
    } while (MessagesController.getInstance().getUser(Integer.valueOf(i)) == null);
    label53:
    final Object localObject2 = new ArrayList();
    final Object localObject5 = new ArrayList();
    Object localObject1 = new ArrayList();
    Object localObject4 = new ArrayList();
    final Object localObject3 = new HashMap();
    TLRPC.InputPeer localInputPeer = MessagesController.getInputPeer(i);
    int i = 0;
    label108:
    MessageObject localMessageObject;
    Object localObject10;
    Object localObject9;
    Object localObject8;
    Object localObject7;
    if (i < paramArrayList.size())
    {
      localMessageObject = (MessageObject)paramArrayList.get(i);
      if (localMessageObject.getId() > 0) {
        break label224;
      }
      localObject10 = localObject1;
      localObject9 = localObject2;
      localObject8 = localObject3;
      localObject7 = localObject4;
      localObject6 = localObject5;
    }
    label224:
    int j;
    label490:
    label503:
    label610:
    do
    {
      do
      {
        i += 1;
        localObject5 = localObject6;
        localObject4 = localObject7;
        localObject3 = localObject8;
        localObject2 = localObject9;
        localObject1 = localObject10;
        break label108;
        break;
        localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-i));
        if (!ChatObject.isChannel((TLRPC.Chat)localObject1)) {
          break label53;
        }
        bool1 = ((TLRPC.Chat)localObject1).megagroup;
        bool2 = ((TLRPC.Chat)localObject1).signatures;
        break label53;
        localObject6 = new TLRPC.TL_message();
        if (!localMessageObject.isForwarded()) {
          break label1252;
        }
        ((TLRPC.Message)localObject6).fwd_from = localMessageObject.messageOwner.fwd_from;
        ((TLRPC.Message)localObject6).media = localMessageObject.messageOwner.media;
        ((TLRPC.Message)localObject6).flags = 4;
        if (((TLRPC.Message)localObject6).media != null) {
          ((TLRPC.Message)localObject6).flags |= 0x200;
        }
        if (bool1) {
          ((TLRPC.Message)localObject6).flags |= 0x80000000;
        }
        if (localMessageObject.messageOwner.via_bot_id != 0)
        {
          ((TLRPC.Message)localObject6).via_bot_id = localMessageObject.messageOwner.via_bot_id;
          ((TLRPC.Message)localObject6).flags |= 0x800;
        }
        ((TLRPC.Message)localObject6).message = localMessageObject.messageOwner.message;
        ((TLRPC.Message)localObject6).fwd_msg_id = localMessageObject.getId();
        ((TLRPC.Message)localObject6).attachPath = localMessageObject.messageOwner.attachPath;
        ((TLRPC.Message)localObject6).entities = localMessageObject.messageOwner.entities;
        if (!((TLRPC.Message)localObject6).entities.isEmpty()) {
          ((TLRPC.Message)localObject6).flags |= 0x80;
        }
        if (((TLRPC.Message)localObject6).attachPath == null) {
          ((TLRPC.Message)localObject6).attachPath = "";
        }
        j = UserConfig.getNewMessageId();
        ((TLRPC.Message)localObject6).id = j;
        ((TLRPC.Message)localObject6).local_id = j;
        ((TLRPC.Message)localObject6).out = true;
        if ((localPeer.channel_id == 0) || (bool1)) {
          break label1464;
        }
        if (!bool2) {
          break label1453;
        }
        j = UserConfig.getClientUserId();
        ((TLRPC.Message)localObject6).from_id = j;
        ((TLRPC.Message)localObject6).post = true;
        if (((TLRPC.Message)localObject6).random_id == 0L) {
          ((TLRPC.Message)localObject6).random_id = getNextRandomId();
        }
        ((ArrayList)localObject1).add(Long.valueOf(((TLRPC.Message)localObject6).random_id));
        ((HashMap)localObject3).put(Long.valueOf(((TLRPC.Message)localObject6).random_id), localObject6);
        ((ArrayList)localObject4).add(Integer.valueOf(((TLRPC.Message)localObject6).fwd_msg_id));
        ((TLRPC.Message)localObject6).date = ConnectionsManager.getInstance().getCurrentTime();
        if (!(localInputPeer instanceof TLRPC.TL_inputPeerChannel)) {
          break label1498;
        }
        if (bool1) {
          break label1489;
        }
        ((TLRPC.Message)localObject6).views = 1;
        ((TLRPC.Message)localObject6).flags |= 0x400;
        ((TLRPC.Message)localObject6).dialog_id = paramLong;
        ((TLRPC.Message)localObject6).to_id = localPeer;
        if ((MessageObject.isVoiceMessage((TLRPC.Message)localObject6)) && (((TLRPC.Message)localObject6).to_id.channel_id == 0)) {
          ((TLRPC.Message)localObject6).media_unread = true;
        }
        if ((localMessageObject.messageOwner.to_id instanceof TLRPC.TL_peerChannel)) {
          ((TLRPC.Message)localObject6).ttl = (-localMessageObject.messageOwner.to_id.channel_id);
        }
        localObject7 = new MessageObject((TLRPC.Message)localObject6, null, true);
        ((MessageObject)localObject7).messageOwner.send_state = 1;
        ((ArrayList)localObject2).add(localObject7);
        ((ArrayList)localObject5).add(localObject6);
        putToSendingMessages((TLRPC.Message)localObject6);
        if (BuildVars.DEBUG_VERSION) {
          FileLog.e("tmessages", "forward message user_id = " + localInputPeer.user_id + " chat_id = " + localInputPeer.chat_id + " channel_id = " + localInputPeer.channel_id + " access_hash = " + localInputPeer.access_hash);
        }
        if ((((ArrayList)localObject5).size() == 100) || (i == paramArrayList.size() - 1)) {
          break label896;
        }
        localObject6 = localObject5;
        localObject7 = localObject4;
        localObject8 = localObject3;
        localObject9 = localObject2;
        localObject10 = localObject1;
      } while (i == paramArrayList.size() - 1);
      localObject6 = localObject5;
      localObject7 = localObject4;
      localObject8 = localObject3;
      localObject9 = localObject2;
      localObject10 = localObject1;
    } while (((MessageObject)paramArrayList.get(i + 1)).getDialogId() == localMessageObject.getDialogId());
    label896:
    MessagesStorage.getInstance().putMessages(new ArrayList((Collection)localObject5), false, true, false, 0);
    MessagesController.getInstance().updateInterfaceWithMessages(paramLong, (ArrayList)localObject2);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    UserConfig.saveConfig(false);
    Object localObject6 = new TLRPC.TL_messages_forwardMessages();
    ((TLRPC.TL_messages_forwardMessages)localObject6).to_peer = localInputPeer;
    if ((((TLRPC.TL_messages_forwardMessages)localObject6).to_peer instanceof TLRPC.TL_inputPeerChannel)) {
      ((TLRPC.TL_messages_forwardMessages)localObject6).silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + paramLong, false);
    }
    if ((localMessageObject.messageOwner.to_id instanceof TLRPC.TL_peerChannel))
    {
      localObject7 = MessagesController.getInstance().getChat(Integer.valueOf(localMessageObject.messageOwner.to_id.channel_id));
      ((TLRPC.TL_messages_forwardMessages)localObject6).from_peer = new TLRPC.TL_inputPeerChannel();
      ((TLRPC.TL_messages_forwardMessages)localObject6).from_peer.channel_id = localMessageObject.messageOwner.to_id.channel_id;
      if (localObject7 != null) {
        ((TLRPC.TL_messages_forwardMessages)localObject6).from_peer.access_hash = ((TLRPC.Chat)localObject7).access_hash;
      }
      label1094:
      ((TLRPC.TL_messages_forwardMessages)localObject6).random_id = ((ArrayList)localObject1);
      ((TLRPC.TL_messages_forwardMessages)localObject6).id = ((ArrayList)localObject4);
      if ((paramArrayList.size() != 1) || (!((MessageObject)paramArrayList.get(0)).messageOwner.with_my_score)) {
        break label1564;
      }
    }
    label1252:
    label1453:
    label1464:
    label1489:
    label1498:
    label1564:
    for (boolean bool3 = true;; bool3 = false)
    {
      ((TLRPC.TL_messages_forwardMessages)localObject6).with_my_score = bool3;
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject6, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            HashMap localHashMap = new HashMap();
            TLRPC.Updates localUpdates = (TLRPC.Updates)paramAnonymousTLObject;
            final int j;
            for (i = 0; i < localUpdates.updates.size(); i = j + 1)
            {
              paramAnonymousTLObject = (TLRPC.Update)localUpdates.updates.get(i);
              j = i;
              if ((paramAnonymousTLObject instanceof TLRPC.TL_updateMessageID))
              {
                paramAnonymousTLObject = (TLRPC.TL_updateMessageID)paramAnonymousTLObject;
                localHashMap.put(Integer.valueOf(paramAnonymousTLObject.id), Long.valueOf(paramAnonymousTLObject.random_id));
                localUpdates.updates.remove(i);
                j = i - 1;
              }
            }
            paramAnonymousTL_error = (Integer)MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(paramLong));
            paramAnonymousTLObject = paramAnonymousTL_error;
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, paramLong));
              MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(paramLong), paramAnonymousTLObject);
            }
            i = 0;
            if (i < localUpdates.updates.size())
            {
              Object localObject2 = (TLRPC.Update)localUpdates.updates.get(i);
              label242:
              boolean bool;
              label256:
              final Object localObject1;
              if (((localObject2 instanceof TLRPC.TL_updateNewMessage)) || ((localObject2 instanceof TLRPC.TL_updateNewChannelMessage)))
              {
                if (!(localObject2 instanceof TLRPC.TL_updateNewMessage)) {
                  break label310;
                }
                paramAnonymousTL_error = ((TLRPC.TL_updateNewMessage)localObject2).message;
                MessagesController.getInstance().processNewDifferenceParams(-1, ((TLRPC.Update)localObject2).pts, -1, ((TLRPC.Update)localObject2).pts_count);
                if (paramAnonymousTLObject.intValue() >= paramAnonymousTL_error.id) {
                  break label373;
                }
                bool = true;
                paramAnonymousTL_error.unread = bool;
                localObject1 = (Long)localHashMap.get(Integer.valueOf(paramAnonymousTL_error.id));
                if (localObject1 != null)
                {
                  localObject1 = (TLRPC.Message)localObject5.get(localObject1);
                  if (localObject1 != null) {
                    break label379;
                  }
                }
              }
              for (;;)
              {
                i += 1;
                break;
                label310:
                localObject1 = ((TLRPC.TL_updateNewChannelMessage)localObject2).message;
                MessagesController.getInstance().processNewChannelDifferenceParams(((TLRPC.Update)localObject2).pts, ((TLRPC.Update)localObject2).pts_count, ((TLRPC.Message)localObject1).to_id.channel_id);
                paramAnonymousTL_error = (TLRPC.TL_error)localObject1;
                if (!localObject3) {
                  break label242;
                }
                ((TLRPC.Message)localObject1).flags |= 0x80000000;
                paramAnonymousTL_error = (TLRPC.TL_error)localObject1;
                break label242;
                label373:
                bool = false;
                break label256;
                label379:
                j = localObject2.indexOf(localObject1);
                if (j != -1)
                {
                  localObject2 = (MessageObject)localPeer.get(j);
                  localObject2.remove(j);
                  localPeer.remove(j);
                  j = ((TLRPC.Message)localObject1).id;
                  final ArrayList localArrayList = new ArrayList();
                  localArrayList.add(paramAnonymousTL_error);
                  ((TLRPC.Message)localObject1).id = paramAnonymousTL_error.id;
                  SendMessagesHelper.this.updateMediaPaths((MessageObject)localObject2, paramAnonymousTL_error, null, true);
                  MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      MessagesStorage.getInstance().updateMessageStateAndId(localObject1.random_id, Integer.valueOf(j), localObject1.id, 0, false, SendMessagesHelper.4.this.val$to_id.channel_id);
                      MessagesStorage.getInstance().putMessages(localArrayList, true, false, false, 0);
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          SendMessagesHelper.4.1.this.val$newMsgObj.send_state = 0;
                          SearchQuery.increasePeerRaiting(SendMessagesHelper.4.this.val$peer);
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { Integer.valueOf(SendMessagesHelper.4.1.this.val$oldId), Integer.valueOf(SendMessagesHelper.4.1.this.val$message.id), SendMessagesHelper.4.1.this.val$message, Long.valueOf(SendMessagesHelper.4.this.val$peer) });
                          SendMessagesHelper.this.processSentMessage(SendMessagesHelper.4.1.this.val$oldId);
                          SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.4.1.this.val$oldId);
                        }
                      });
                      if ((MessageObject.isVideoMessage(localObject1)) || (MessageObject.isNewGifMessage(localObject1))) {
                        SendMessagesHelper.this.stopVideoService(localObject1.attachPath);
                      }
                    }
                  });
                }
              }
            }
          }
          else
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (paramAnonymousTL_error.text.equals("PEER_FLOOD")) {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(0) });
                }
              }
            });
          }
          int i = 0;
          while (i < localObject2.size())
          {
            paramAnonymousTLObject = (TLRPC.Message)localObject2.get(i);
            MessagesStorage.getInstance().markMessageAsSendError(paramAnonymousTLObject);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                paramAnonymousTLObject.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramAnonymousTLObject.id) });
                SendMessagesHelper.this.processSentMessage(paramAnonymousTLObject.id);
                if ((MessageObject.isVideoMessage(paramAnonymousTLObject)) || (MessageObject.isNewGifMessage(paramAnonymousTLObject))) {
                  SendMessagesHelper.this.stopVideoService(paramAnonymousTLObject.attachPath);
                }
                SendMessagesHelper.this.removeFromSendingMessages(paramAnonymousTLObject.id);
              }
            });
            i += 1;
          }
        }
      }, 68);
      localObject6 = localObject5;
      localObject7 = localObject4;
      localObject8 = localObject3;
      localObject9 = localObject2;
      localObject10 = localObject1;
      if (i == paramArrayList.size() - 1) {
        break;
      }
      localObject9 = new ArrayList();
      localObject6 = new ArrayList();
      localObject10 = new ArrayList();
      localObject7 = new ArrayList();
      localObject8 = new HashMap();
      break;
      ((TLRPC.Message)localObject6).fwd_from = new TLRPC.TL_messageFwdHeader();
      if (localMessageObject.isFromUser())
      {
        ((TLRPC.Message)localObject6).fwd_from.from_id = localMessageObject.messageOwner.from_id;
        localObject7 = ((TLRPC.Message)localObject6).fwd_from;
        ((TLRPC.TL_messageFwdHeader)localObject7).flags |= 0x1;
      }
      for (;;)
      {
        ((TLRPC.Message)localObject6).date = localMessageObject.messageOwner.date;
        break;
        ((TLRPC.Message)localObject6).fwd_from.channel_id = localMessageObject.messageOwner.to_id.channel_id;
        localObject7 = ((TLRPC.Message)localObject6).fwd_from;
        ((TLRPC.TL_messageFwdHeader)localObject7).flags |= 0x2;
        if (localMessageObject.messageOwner.post)
        {
          ((TLRPC.Message)localObject6).fwd_from.channel_post = localMessageObject.getId();
          localObject7 = ((TLRPC.Message)localObject6).fwd_from;
          ((TLRPC.TL_messageFwdHeader)localObject7).flags |= 0x4;
          if (localMessageObject.messageOwner.from_id > 0)
          {
            ((TLRPC.Message)localObject6).fwd_from.from_id = localMessageObject.messageOwner.from_id;
            localObject7 = ((TLRPC.Message)localObject6).fwd_from;
            ((TLRPC.TL_messageFwdHeader)localObject7).flags |= 0x1;
          }
        }
      }
      j = -localPeer.channel_id;
      break label490;
      ((TLRPC.Message)localObject6).from_id = UserConfig.getClientUserId();
      ((TLRPC.Message)localObject6).flags |= 0x100;
      break label503;
      ((TLRPC.Message)localObject6).unread = true;
      break label610;
      if ((localMessageObject.messageOwner.flags & 0x400) != 0)
      {
        ((TLRPC.Message)localObject6).views = localMessageObject.messageOwner.views;
        ((TLRPC.Message)localObject6).flags |= 0x400;
      }
      ((TLRPC.Message)localObject6).unread = true;
      break label610;
      ((TLRPC.TL_messages_forwardMessages)localObject6).from_peer = new TLRPC.TL_inputPeerEmpty();
      break label1094;
    }
  }
  
  public void sendMessage(MessageObject paramMessageObject)
  {
    sendMessage(null, null, null, null, null, null, null, paramMessageObject.getDialogId(), paramMessageObject.messageOwner.attachPath, null, null, true, paramMessageObject, null, paramMessageObject.messageOwner.reply_markup, paramMessageObject.messageOwner.params);
  }
  
  public void sendMessage(TLRPC.MessageMedia paramMessageMedia, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, paramMessageMedia, null, null, null, null, null, paramLong, null, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void sendMessage(TLRPC.TL_document paramTL_document, VideoEditedInfo paramVideoEditedInfo, String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, paramVideoEditedInfo, null, paramTL_document, null, paramLong, paramString, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void sendMessage(TLRPC.TL_game paramTL_game, long paramLong, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, null, null, null, paramTL_game, paramLong, null, null, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void sendMessage(TLRPC.TL_photo paramTL_photo, String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, paramTL_photo, null, null, null, null, paramLong, paramString, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void sendMessage(TLRPC.User paramUser, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, null, paramUser, null, null, paramLong, null, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void sendSticker(TLRPC.Document paramDocument, long paramLong, MessageObject paramMessageObject)
  {
    if (paramDocument == null) {}
    int i;
    do
    {
      return;
      localObject = paramDocument;
      if ((int)paramLong != 0) {
        break;
      }
      i = (int)(paramLong >> 32);
    } while (MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i)) == null);
    Object localObject = paramDocument;
    File localFile;
    if ((paramDocument.thumb instanceof TLRPC.TL_photoSize))
    {
      localFile = FileLoader.getPathToAttach(paramDocument.thumb, true);
      localObject = paramDocument;
      if (!localFile.exists()) {}
    }
    try
    {
      i = (int)localFile.length();
      byte[] arrayOfByte = new byte[(int)localFile.length()];
      new RandomAccessFile(localFile, "r").readFully(arrayOfByte);
      localObject = new TLRPC.TL_document();
      ((TLRPC.TL_document)localObject).thumb = new TLRPC.TL_photoCachedSize();
      ((TLRPC.TL_document)localObject).thumb.location = paramDocument.thumb.location;
      ((TLRPC.TL_document)localObject).thumb.size = paramDocument.thumb.size;
      ((TLRPC.TL_document)localObject).thumb.w = paramDocument.thumb.w;
      ((TLRPC.TL_document)localObject).thumb.h = paramDocument.thumb.h;
      ((TLRPC.TL_document)localObject).thumb.type = paramDocument.thumb.type;
      ((TLRPC.TL_document)localObject).thumb.bytes = arrayOfByte;
      ((TLRPC.TL_document)localObject).id = paramDocument.id;
      ((TLRPC.TL_document)localObject).access_hash = paramDocument.access_hash;
      ((TLRPC.TL_document)localObject).date = paramDocument.date;
      ((TLRPC.TL_document)localObject).mime_type = paramDocument.mime_type;
      ((TLRPC.TL_document)localObject).size = paramDocument.size;
      ((TLRPC.TL_document)localObject).dc_id = paramDocument.dc_id;
      ((TLRPC.TL_document)localObject).attributes = paramDocument.attributes;
      if (((TLRPC.TL_document)localObject).mime_type == null) {
        ((TLRPC.TL_document)localObject).mime_type = "";
      }
      getInstance().sendMessage((TLRPC.TL_document)localObject, null, null, paramLong, paramMessageObject, null, null);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
        TLRPC.Document localDocument = paramDocument;
      }
    }
  }
  
  public void setCurrentChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.currentChatInfo = paramChatFull;
  }
  
  protected void stopVideoService(final String paramString)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.stopEncodingService, new Object[] { SendMessagesHelper.8.this.val$path });
          }
        });
      }
    });
  }
  
  protected class DelayedMessage
  {
    public TLRPC.TL_document documentLocation;
    public TLRPC.EncryptedChat encryptedChat;
    public String httpLocation;
    public TLRPC.FileLocation location;
    public MessageObject obj;
    public String originalPath;
    public TLRPC.TL_decryptedMessage sendEncryptedRequest;
    public TLObject sendRequest;
    public int type;
    public VideoEditedInfo videoEditedInfo;
    
    protected DelayedMessage() {}
  }
  
  public static class LocationProvider
  {
    private LocationProviderDelegate delegate;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener(null);
    private Location lastKnownLocation;
    private LocationManager locationManager;
    private Runnable locationQueryCancelRunnable;
    private GpsLocationListener networkLocationListener = new GpsLocationListener(null);
    
    public LocationProvider() {}
    
    public LocationProvider(LocationProviderDelegate paramLocationProviderDelegate)
    {
      this.delegate = paramLocationProviderDelegate;
    }
    
    private void cleanup()
    {
      this.locationManager.removeUpdates(this.gpsLocationListener);
      this.locationManager.removeUpdates(this.networkLocationListener);
      this.lastKnownLocation = null;
      this.locationQueryCancelRunnable = null;
    }
    
    public void setDelegate(LocationProviderDelegate paramLocationProviderDelegate)
    {
      this.delegate = paramLocationProviderDelegate;
    }
    
    public void start()
    {
      if (this.locationManager == null) {
        this.locationManager = ((LocationManager)ApplicationLoader.applicationContext.getSystemService("location"));
      }
      try
      {
        this.locationManager.requestLocationUpdates("gps", 1L, 0.0F, this.gpsLocationListener);
      }
      catch (Exception localException2)
      {
        try
        {
          this.locationManager.requestLocationUpdates("network", 1L, 0.0F, this.networkLocationListener);
        }
        catch (Exception localException2)
        {
          try
          {
            for (;;)
            {
              this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
              if (this.lastKnownLocation == null) {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
              }
              if (this.locationQueryCancelRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
              }
              this.locationQueryCancelRunnable = new Runnable()
              {
                public void run()
                {
                  if (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable != this) {
                    return;
                  }
                  if (SendMessagesHelper.LocationProvider.this.delegate != null)
                  {
                    if (SendMessagesHelper.LocationProvider.this.lastKnownLocation == null) {
                      break label59;
                    }
                    SendMessagesHelper.LocationProvider.this.delegate.onLocationAcquired(SendMessagesHelper.LocationProvider.this.lastKnownLocation);
                  }
                  for (;;)
                  {
                    SendMessagesHelper.LocationProvider.this.cleanup();
                    return;
                    label59:
                    SendMessagesHelper.LocationProvider.this.delegate.onUnableLocationAcquire();
                  }
                }
              };
              AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, 5000L);
              return;
              localException1 = localException1;
              FileLog.e("tmessages", localException1);
              continue;
              localException2 = localException2;
              FileLog.e("tmessages", localException2);
            }
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException3);
            }
          }
        }
      }
    }
    
    public void stop()
    {
      if (this.locationManager == null) {
        return;
      }
      if (this.locationQueryCancelRunnable != null) {
        AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
      }
      cleanup();
    }
    
    private class GpsLocationListener
      implements LocationListener
    {
      private GpsLocationListener() {}
      
      public void onLocationChanged(Location paramLocation)
      {
        if ((paramLocation == null) || (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable == null)) {}
        do
        {
          return;
          FileLog.e("tmessages", "found location " + paramLocation);
          SendMessagesHelper.LocationProvider.access$402(SendMessagesHelper.LocationProvider.this, paramLocation);
        } while (paramLocation.getAccuracy() >= 100.0F);
        if (SendMessagesHelper.LocationProvider.this.delegate != null) {
          SendMessagesHelper.LocationProvider.this.delegate.onLocationAcquired(paramLocation);
        }
        if (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable != null) {
          AndroidUtilities.cancelRunOnUIThread(SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable);
        }
        SendMessagesHelper.LocationProvider.this.cleanup();
      }
      
      public void onProviderDisabled(String paramString) {}
      
      public void onProviderEnabled(String paramString) {}
      
      public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
    }
    
    public static abstract interface LocationProviderDelegate
    {
      public abstract void onLocationAcquired(Location paramLocation);
      
      public abstract void onUnableLocationAcquire();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/SendMessagesHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */