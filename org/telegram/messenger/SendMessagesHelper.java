package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaCodecInfo;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.Toast;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.support.SparseLongArray;
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
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.DecryptedMessage;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
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
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
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
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaContact;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaGeoPoint;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage_layer45;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker_layer55;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo_layer65;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFile;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputMediaContact;
import org.telegram.tgnet.TLRPC.TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_inputMediaGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputMediaGifExternal;
import org.telegram.tgnet.TLRPC.TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputMediaVenue;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhoto;
import org.telegram.tgnet.TLRPC.TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendScreenshotNotification;
import org.telegram.tgnet.TLRPC.TL_messages_uploadMedia;
import org.telegram.tgnet.TLRPC.TL_payments_getPaymentForm;
import org.telegram.tgnet.TLRPC.TL_payments_getPaymentReceipt;
import org.telegram.tgnet.TLRPC.TL_payments_paymentForm;
import org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
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
import org.telegram.tgnet.TLRPC.TL_userRequest_old2;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.PaymentFormActivity;

public class SendMessagesHelper
  implements NotificationCenter.NotificationCenterDelegate
{
  private static volatile SendMessagesHelper[] Instance;
  private static DispatchQueue mediaSendQueue = new DispatchQueue("mediaSendQueue");
  private static ThreadPoolExecutor mediaSendThreadPool;
  private int currentAccount;
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
      NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, new Object[] { localHashMap });
      SendMessagesHelper.this.waitingForLocation.clear();
    }
  });
  private SparseArray<TLRPC.Message> sendingMessages = new SparseArray();
  private SparseArray<MessageObject> unsentMessages = new SparseArray();
  private HashMap<String, Boolean> waitingForCallback = new HashMap();
  private HashMap<String, MessageObject> waitingForLocation = new HashMap();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 17) {}
    for (int i = Runtime.getRuntime().availableProcessors();; i = 2)
    {
      mediaSendThreadPool = new ThreadPoolExecutor(i, i, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
      Instance = new SendMessagesHelper[3];
      return;
    }
  }
  
  public SendMessagesHelper(int paramInt)
  {
    this.currentAccount = paramInt;
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).addObserver(SendMessagesHelper.this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).addObserver(SendMessagesHelper.this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).addObserver(SendMessagesHelper.this, NotificationCenter.FilePreparingStarted);
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).addObserver(SendMessagesHelper.this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).addObserver(SendMessagesHelper.this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).addObserver(SendMessagesHelper.this, NotificationCenter.httpFileDidFailedLoad);
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).addObserver(SendMessagesHelper.this, NotificationCenter.httpFileDidLoaded);
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).addObserver(SendMessagesHelper.this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).addObserver(SendMessagesHelper.this, NotificationCenter.FileDidFailedLoad);
      }
    });
  }
  
  private static VideoEditedInfo createCompressionSettings(String paramString)
  {
    localObject1 = null;
    i = 0;
    j = 0;
    f1 = 0.0F;
    l1 = 0L;
    l2 = 0L;
    k = 25;
    try
    {
      localObject2 = new com/coremedia/iso/IsoFile;
      ((IsoFile)localObject2).<init>(paramString);
      localList = Path.getPaths((Container)localObject2, "/moov/trak/");
      if ((Path.getPath((Container)localObject2, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") == null) && (BuildVars.LOGS_ENABLED)) {
        FileLog.d("video hasn't mp4a atom");
      }
      if (Path.getPath((Container)localObject2, "/moov/trak/mdia/minf/stbl/stsd/avc1/") != null) {
        break label90;
      }
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("video hasn't avc1 atom");
      }
      paramString = null;
    }
    catch (Exception paramString)
    {
      for (;;)
      {
        Object localObject2;
        List localList;
        int m;
        TrackBox localTrackBox;
        long l4;
        Object localObject3;
        Object localObject4;
        int n;
        float f2;
        long l6;
        int i3;
        FileLog.e(paramString);
        paramString = null;
        continue;
        long l5 = l2;
        int i1 = n;
        i = j;
        Object localObject6 = localObject1;
        int i2 = k;
        long l3 = l6;
        if (l1 != 0L)
        {
          double d = ((MediaHeaderBox)localObject2).getTimescale();
          l3 = l1 / (i3 - 1);
          i2 = (int)(d / l3);
          l3 = l6;
          localObject6 = localObject1;
          i = j;
          i1 = n;
          l5 = l2;
        }
        for (;;)
        {
          m++;
          l2 = l5;
          j = i1;
          localObject1 = localObject6;
          k = i2;
          l1 = l3;
          break;
          l5 = l2 + l6;
          i1 = j;
          localObject6 = localObject1;
          i2 = k;
          l3 = l1;
        }
        if (localObject1 == null)
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("video hasn't trackHeaderBox atom");
          }
          paramString = null;
        }
        else if (Build.VERSION.SDK_INT < 18)
        {
          try
          {
            localObject3 = MediaController.selectCodec("video/avc");
            if (localObject3 == null)
            {
              if (BuildVars.LOGS_ENABLED) {
                FileLog.d("no codec info for video/avc");
              }
              paramString = null;
              continue;
            }
            localObject2 = ((MediaCodecInfo)localObject3).getName();
            if ((((String)localObject2).equals("OMX.google.h264.encoder")) || (((String)localObject2).equals("OMX.ST.VFM.H264Enc")) || (((String)localObject2).equals("OMX.Exynos.avc.enc")) || (((String)localObject2).equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER")) || (((String)localObject2).equals("OMX.MARVELL.VIDEO.H264ENCODER")) || (((String)localObject2).equals("OMX.k3.video.encoder.avc")) || (((String)localObject2).equals("OMX.TI.DUCATI1.VIDEO.H264E")))
            {
              if (BuildVars.LOGS_ENABLED)
              {
                paramString = new java/lang/StringBuilder;
                paramString.<init>();
                FileLog.d("unsupported encoder = " + (String)localObject2);
              }
              paramString = null;
              continue;
            }
            if (MediaController.selectColorFormat((MediaCodecInfo)localObject3, "video/avc") != 0) {
              break label928;
            }
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("no color format for video/avc");
            }
            paramString = null;
          }
          catch (Exception paramString)
          {
            paramString = null;
          }
        }
        else
        {
          f2 = f1 * 1000.0F;
          localObject2 = new VideoEditedInfo();
          ((VideoEditedInfo)localObject2).startTime = -1L;
          ((VideoEditedInfo)localObject2).endTime = -1L;
          ((VideoEditedInfo)localObject2).bitrate = j;
          ((VideoEditedInfo)localObject2).originalPath = paramString;
          ((VideoEditedInfo)localObject2).framerate = k;
          ((VideoEditedInfo)localObject2).estimatedDuration = (Math.ceil(f2));
          n = (int)((TrackHeaderBox)localObject1).getWidth();
          ((VideoEditedInfo)localObject2).originalWidth = n;
          ((VideoEditedInfo)localObject2).resultWidth = n;
          n = (int)((TrackHeaderBox)localObject1).getHeight();
          ((VideoEditedInfo)localObject2).originalHeight = n;
          ((VideoEditedInfo)localObject2).resultHeight = n;
          localObject3 = ((TrackHeaderBox)localObject1).getMatrix();
          if (((Matrix)localObject3).equals(Matrix.ROTATE_90))
          {
            ((VideoEditedInfo)localObject2).rotationValue = 90;
            k = MessagesController.getGlobalMainSettings().getInt("compress_video2", 1);
            if ((((VideoEditedInfo)localObject2).originalWidth <= 1280) && (((VideoEditedInfo)localObject2).originalHeight <= 1280)) {
              break label1388;
            }
            n = 5;
            m = k;
            if (k >= n) {
              m = n - 1;
            }
            i2 = j;
            l3 = l1;
            if (m != n - 1) {
              switch (m)
              {
              default: 
                k = 2500000;
                f1 = 1280.0F;
                if (((VideoEditedInfo)localObject2).originalWidth <= ((VideoEditedInfo)localObject2).originalHeight) {
                  break;
                }
              }
            }
          }
          for (f1 /= ((VideoEditedInfo)localObject2).originalWidth;; f1 /= ((VideoEditedInfo)localObject2).originalHeight)
          {
            ((VideoEditedInfo)localObject2).resultWidth = (Math.round(((VideoEditedInfo)localObject2).originalWidth * f1 / 2.0F) * 2);
            ((VideoEditedInfo)localObject2).resultHeight = (Math.round(((VideoEditedInfo)localObject2).originalHeight * f1 / 2.0F) * 2);
            i2 = j;
            l3 = l1;
            if (j != 0)
            {
              i2 = Math.min(k, (int)(i / f1));
              l3 = (i2 / 8 * f2 / 1000.0F);
            }
            if (m != n - 1) {
              break label1531;
            }
            ((VideoEditedInfo)localObject2).resultWidth = ((VideoEditedInfo)localObject2).originalWidth;
            ((VideoEditedInfo)localObject2).resultHeight = ((VideoEditedInfo)localObject2).originalHeight;
            ((VideoEditedInfo)localObject2).bitrate = i;
            ((VideoEditedInfo)localObject2).estimatedSize = ((int)new File(paramString).length());
            paramString = (String)localObject2;
            break;
            if (((Matrix)localObject3).equals(Matrix.ROTATE_180))
            {
              ((VideoEditedInfo)localObject2).rotationValue = 180;
              break label1058;
            }
            if (((Matrix)localObject3).equals(Matrix.ROTATE_270))
            {
              ((VideoEditedInfo)localObject2).rotationValue = 270;
              break label1058;
            }
            ((VideoEditedInfo)localObject2).rotationValue = 0;
            break label1058;
            if ((((VideoEditedInfo)localObject2).originalWidth > 848) || (((VideoEditedInfo)localObject2).originalHeight > 848))
            {
              n = 4;
              break label1097;
            }
            if ((((VideoEditedInfo)localObject2).originalWidth > 640) || (((VideoEditedInfo)localObject2).originalHeight > 640))
            {
              n = 3;
              break label1097;
            }
            if ((((VideoEditedInfo)localObject2).originalWidth > 480) || (((VideoEditedInfo)localObject2).originalHeight > 480))
            {
              n = 2;
              break label1097;
            }
            n = 1;
            break label1097;
            f1 = 432.0F;
            k = 400000;
            break label1170;
            f1 = 640.0F;
            k = 900000;
            break label1170;
            f1 = 848.0F;
            k = 1100000;
            break label1170;
          }
          ((VideoEditedInfo)localObject2).bitrate = i2;
          ((VideoEditedInfo)localObject2).estimatedSize = ((int)(l2 + l3));
          ((VideoEditedInfo)localObject2).estimatedSize += ((VideoEditedInfo)localObject2).estimatedSize / 32768L * 16L;
          paramString = (String)localObject2;
        }
      }
    }
    return paramString;
    label90:
    m = 0;
    if (m < localList.size())
    {
      localTrackBox = (TrackBox)localList.get(m);
      l3 = 0L;
      l4 = 0L;
      localObject3 = null;
      localObject4 = null;
      localObject2 = localObject4;
      l5 = l3;
      try
      {
        localObject5 = localTrackBox.getMediaBox();
        localObject3 = localObject5;
        localObject2 = localObject4;
        l5 = l3;
        localObject4 = ((MediaBox)localObject5).getMediaHeaderBox();
        localObject3 = localObject5;
        localObject2 = localObject4;
        l5 = l3;
        long[] arrayOfLong = ((MediaBox)localObject5).getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes();
        for (n = 0;; n++)
        {
          localObject3 = localObject5;
          localObject2 = localObject4;
          l5 = l3;
          if (n >= arrayOfLong.length) {
            break;
          }
          l3 += arrayOfLong[n];
        }
        localObject3 = localObject5;
        localObject2 = localObject4;
        l5 = l3;
        f2 = (float)((MediaHeaderBox)localObject4).getDuration();
        localObject3 = localObject5;
        localObject2 = localObject4;
        l5 = l3;
        l6 = ((MediaHeaderBox)localObject4).getTimescale();
        f1 = f2 / (float)l6;
        l4 = (int)((float)(8L * l3) / f1);
        l6 = l3;
        localObject2 = localObject4;
        localObject3 = localObject5;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Object localObject5;
          FileLog.e(localException);
          l6 = l5;
        }
      }
      localObject4 = localTrackBox.getTrackHeaderBox();
      if ((((TrackHeaderBox)localObject4).getWidth() != 0.0D) && (((TrackHeaderBox)localObject4).getHeight() != 0.0D)) {
        if ((localObject1 != null) && (((TrackHeaderBox)localObject1).getWidth() >= ((TrackHeaderBox)localObject4).getWidth()))
        {
          l5 = l2;
          i1 = j;
          localObject5 = localObject1;
          i2 = k;
          l3 = l1;
          if (((TrackHeaderBox)localObject1).getHeight() >= ((TrackHeaderBox)localObject4).getHeight()) {}
        }
        else
        {
          localObject1 = localObject4;
          i = (int)(l4 / 100000L * 100000L);
          j = i;
          n = i;
          if (i > 900000) {
            n = 900000;
          }
          l6 = l1 + l6;
          l5 = l2;
          i1 = n;
          i = j;
          localObject5 = localObject1;
          i2 = k;
          l3 = l6;
          if (localObject3 != null)
          {
            l5 = l2;
            i1 = n;
            i = j;
            localObject5 = localObject1;
            i2 = k;
            l3 = l6;
            if (localObject2 != null)
            {
              localObject3 = ((MediaBox)localObject3).getMediaInformationBox().getSampleTableBox().getTimeToSampleBox();
              l5 = l2;
              i1 = n;
              i = j;
              localObject5 = localObject1;
              i2 = k;
              l3 = l6;
              if (localObject3 != null)
              {
                localObject3 = ((TimeToSampleBox)localObject3).getEntries();
                l1 = 0L;
                i3 = Math.min(((List)localObject3).size(), 11);
                for (i = 1; i < i3; i++) {
                  l1 += ((TimeToSampleBox.Entry)((List)localObject3).get(i)).getDelta();
                }
              }
            }
          }
        }
      }
    }
  }
  
  /* Error */
  private static Bitmap createVideoThumbnail(String paramString, long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: new 627	android/media/MediaMetadataRetriever
    //   5: dup
    //   6: invokespecial 628	android/media/MediaMetadataRetriever:<init>	()V
    //   9: astore 4
    //   11: aload 4
    //   13: aload_0
    //   14: invokevirtual 631	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   17: aload 4
    //   19: lload_1
    //   20: iconst_1
    //   21: invokevirtual 635	android/media/MediaMetadataRetriever:getFrameAtTime	(JI)Landroid/graphics/Bitmap;
    //   24: astore_0
    //   25: aload_0
    //   26: astore_3
    //   27: aload 4
    //   29: invokevirtual 638	android/media/MediaMetadataRetriever:release	()V
    //   32: aload_3
    //   33: ifnonnull +28 -> 61
    //   36: aconst_null
    //   37: astore_0
    //   38: aload_0
    //   39: areturn
    //   40: astore_0
    //   41: aload 4
    //   43: invokevirtual 638	android/media/MediaMetadataRetriever:release	()V
    //   46: goto -14 -> 32
    //   49: astore_0
    //   50: goto -18 -> 32
    //   53: astore_0
    //   54: aload 4
    //   56: invokevirtual 638	android/media/MediaMetadataRetriever:release	()V
    //   59: aload_0
    //   60: athrow
    //   61: aload_3
    //   62: invokevirtual 642	android/graphics/Bitmap:getWidth	()I
    //   65: istore 5
    //   67: aload_3
    //   68: invokevirtual 644	android/graphics/Bitmap:getHeight	()I
    //   71: istore 6
    //   73: iload 5
    //   75: iload 6
    //   77: invokestatic 647	java/lang/Math:max	(II)I
    //   80: istore 7
    //   82: aload_3
    //   83: astore_0
    //   84: iload 7
    //   86: bipush 90
    //   88: if_icmple +52 -> 140
    //   91: ldc_w 648
    //   94: iload 7
    //   96: i2f
    //   97: fdiv
    //   98: fstore 8
    //   100: aload_3
    //   101: iload 5
    //   103: i2f
    //   104: fload 8
    //   106: fmul
    //   107: invokestatic 599	java/lang/Math:round	(F)I
    //   110: iload 6
    //   112: i2f
    //   113: fload 8
    //   115: fmul
    //   116: invokestatic 599	java/lang/Math:round	(F)I
    //   119: iconst_1
    //   120: invokestatic 654	org/telegram/messenger/Bitmaps:createScaledBitmap	(Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
    //   123: astore 4
    //   125: aload_3
    //   126: astore_0
    //   127: aload 4
    //   129: aload_3
    //   130: if_acmpeq +10 -> 140
    //   133: aload_3
    //   134: invokevirtual 657	android/graphics/Bitmap:recycle	()V
    //   137: aload 4
    //   139: astore_0
    //   140: goto -102 -> 38
    //   143: astore_0
    //   144: goto -112 -> 32
    //   147: astore_3
    //   148: goto -89 -> 59
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	151	0	paramString	String
    //   0	151	1	paramLong	long
    //   1	133	3	str	String
    //   147	1	3	localRuntimeException	RuntimeException
    //   9	129	4	localObject	Object
    //   65	37	5	i	int
    //   71	40	6	j	int
    //   80	15	7	k	int
    //   98	16	8	f	float
    // Exception table:
    //   from	to	target	type
    //   11	25	40	java/lang/Exception
    //   41	46	49	java/lang/RuntimeException
    //   11	25	53	finally
    //   27	32	143	java/lang/RuntimeException
    //   54	59	147	java/lang/RuntimeException
  }
  
  /* Error */
  private static void fillVideoAttribute(String paramString, TLRPC.TL_documentAttributeVideo paramTL_documentAttributeVideo, VideoEditedInfo paramVideoEditedInfo)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 5
    //   8: aload 4
    //   10: astore 6
    //   12: new 627	android/media/MediaMetadataRetriever
    //   15: astore 7
    //   17: aload 4
    //   19: astore 6
    //   21: aload 7
    //   23: invokespecial 628	android/media/MediaMetadataRetriever:<init>	()V
    //   26: aload 7
    //   28: aload_0
    //   29: invokevirtual 631	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   32: aload 7
    //   34: bipush 18
    //   36: invokevirtual 661	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   39: astore 6
    //   41: aload 6
    //   43: ifnull +12 -> 55
    //   46: aload_1
    //   47: aload 6
    //   49: invokestatic 667	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   52: putfield 672	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   55: aload 7
    //   57: bipush 19
    //   59: invokevirtual 661	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   62: astore 6
    //   64: aload 6
    //   66: ifnull +12 -> 78
    //   69: aload_1
    //   70: aload 6
    //   72: invokestatic 667	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   75: putfield 675	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   78: aload 7
    //   80: bipush 9
    //   82: invokevirtual 661	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   85: astore 6
    //   87: aload 6
    //   89: ifnull +22 -> 111
    //   92: aload_1
    //   93: aload 6
    //   95: invokestatic 681	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   98: l2f
    //   99: ldc_w 524
    //   102: fdiv
    //   103: f2d
    //   104: invokestatic 550	java/lang/Math:ceil	(D)D
    //   107: d2i
    //   108: putfield 684	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
    //   111: getstatic 191	android/os/Build$VERSION:SDK_INT	I
    //   114: bipush 17
    //   116: if_icmplt +37 -> 153
    //   119: aload 7
    //   121: bipush 24
    //   123: invokevirtual 661	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   126: astore 6
    //   128: aload 6
    //   130: ifnull +23 -> 153
    //   133: aload 6
    //   135: invokestatic 689	org/telegram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   138: invokevirtual 692	java/lang/Integer:intValue	()I
    //   141: istore 8
    //   143: aload_2
    //   144: ifnull +94 -> 238
    //   147: aload_2
    //   148: iload 8
    //   150: putfield 579	org/telegram/messenger/VideoEditedInfo:rotationValue	I
    //   153: iconst_1
    //   154: istore 8
    //   156: aload 7
    //   158: ifnull +8 -> 166
    //   161: aload 7
    //   163: invokevirtual 638	android/media/MediaMetadataRetriever:release	()V
    //   166: iload 8
    //   168: ifne +69 -> 237
    //   171: getstatic 698	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   174: astore 6
    //   176: new 601	java/io/File
    //   179: astore_2
    //   180: aload_2
    //   181: aload_0
    //   182: invokespecial 602	java/io/File:<init>	(Ljava/lang/String;)V
    //   185: aload 6
    //   187: aload_2
    //   188: invokestatic 704	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   191: invokestatic 710	android/media/MediaPlayer:create	(Landroid/content/Context;Landroid/net/Uri;)Landroid/media/MediaPlayer;
    //   194: astore_0
    //   195: aload_0
    //   196: ifnull +41 -> 237
    //   199: aload_1
    //   200: aload_0
    //   201: invokevirtual 712	android/media/MediaPlayer:getDuration	()I
    //   204: i2f
    //   205: ldc_w 524
    //   208: fdiv
    //   209: f2d
    //   210: invokestatic 550	java/lang/Math:ceil	(D)D
    //   213: d2i
    //   214: putfield 684	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
    //   217: aload_1
    //   218: aload_0
    //   219: invokevirtual 715	android/media/MediaPlayer:getVideoWidth	()I
    //   222: putfield 672	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   225: aload_1
    //   226: aload_0
    //   227: invokevirtual 718	android/media/MediaPlayer:getVideoHeight	()I
    //   230: putfield 675	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   233: aload_0
    //   234: invokevirtual 719	android/media/MediaPlayer:release	()V
    //   237: return
    //   238: iload 8
    //   240: bipush 90
    //   242: if_icmpeq +11 -> 253
    //   245: iload 8
    //   247: sipush 270
    //   250: if_icmpne -97 -> 153
    //   253: aload_1
    //   254: getfield 672	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   257: istore 8
    //   259: aload_1
    //   260: aload_1
    //   261: getfield 675	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   264: putfield 672	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   267: aload_1
    //   268: iload 8
    //   270: putfield 675	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   273: goto -120 -> 153
    //   276: astore 6
    //   278: aload 7
    //   280: astore_2
    //   281: aload 6
    //   283: astore 7
    //   285: aload_2
    //   286: astore 6
    //   288: aload 7
    //   290: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   293: iload_3
    //   294: istore 8
    //   296: aload_2
    //   297: ifnull -131 -> 166
    //   300: aload_2
    //   301: invokevirtual 638	android/media/MediaMetadataRetriever:release	()V
    //   304: iload_3
    //   305: istore 8
    //   307: goto -141 -> 166
    //   310: astore_2
    //   311: aload_2
    //   312: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   315: iload_3
    //   316: istore 8
    //   318: goto -152 -> 166
    //   321: astore_2
    //   322: aload_2
    //   323: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   326: goto -160 -> 166
    //   329: astore_0
    //   330: aload 6
    //   332: ifnull +8 -> 340
    //   335: aload 6
    //   337: invokevirtual 638	android/media/MediaMetadataRetriever:release	()V
    //   340: aload_0
    //   341: athrow
    //   342: astore_1
    //   343: aload_1
    //   344: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   347: goto -7 -> 340
    //   350: astore_0
    //   351: aload_0
    //   352: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   355: goto -118 -> 237
    //   358: astore_0
    //   359: aload 7
    //   361: astore 6
    //   363: goto -33 -> 330
    //   366: astore 7
    //   368: aload 5
    //   370: astore_2
    //   371: goto -86 -> 285
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	374	0	paramString	String
    //   0	374	1	paramTL_documentAttributeVideo	TLRPC.TL_documentAttributeVideo
    //   0	374	2	paramVideoEditedInfo	VideoEditedInfo
    //   1	315	3	i	int
    //   3	15	4	localObject1	Object
    //   6	363	5	localObject2	Object
    //   10	176	6	localObject3	Object
    //   276	6	6	localException1	Exception
    //   286	76	6	localObject4	Object
    //   15	345	7	localObject5	Object
    //   366	1	7	localException2	Exception
    //   141	176	8	j	int
    // Exception table:
    //   from	to	target	type
    //   26	41	276	java/lang/Exception
    //   46	55	276	java/lang/Exception
    //   55	64	276	java/lang/Exception
    //   69	78	276	java/lang/Exception
    //   78	87	276	java/lang/Exception
    //   92	111	276	java/lang/Exception
    //   111	128	276	java/lang/Exception
    //   133	143	276	java/lang/Exception
    //   147	153	276	java/lang/Exception
    //   253	273	276	java/lang/Exception
    //   300	304	310	java/lang/Exception
    //   161	166	321	java/lang/Exception
    //   12	17	329	finally
    //   21	26	329	finally
    //   288	293	329	finally
    //   335	340	342	java/lang/Exception
    //   171	195	350	java/lang/Exception
    //   199	237	350	java/lang/Exception
    //   26	41	358	finally
    //   46	55	358	finally
    //   55	64	358	finally
    //   69	78	358	finally
    //   78	87	358	finally
    //   92	111	358	finally
    //   111	128	358	finally
    //   133	143	358	finally
    //   147	153	358	finally
    //   253	273	358	finally
    //   12	17	366	java/lang/Exception
    //   21	26	366	java/lang/Exception
  }
  
  private DelayedMessage findMaxDelayedMessageForMessageId(int paramInt, long paramLong)
  {
    Object localObject1 = null;
    int i = Integer.MIN_VALUE;
    Iterator localIterator = this.delayedMessages.entrySet().iterator();
    while (localIterator.hasNext())
    {
      ArrayList localArrayList = (ArrayList)((Map.Entry)localIterator.next()).getValue();
      int j = localArrayList.size();
      int k = 0;
      Object localObject2 = localObject1;
      int m = i;
      i = m;
      localObject1 = localObject2;
      if (k < j)
      {
        DelayedMessage localDelayedMessage = (DelayedMessage)localArrayList.get(k);
        int n;
        if (localDelayedMessage.type != 4)
        {
          n = m;
          localObject1 = localObject2;
          if (localDelayedMessage.type != 0) {}
        }
        else
        {
          n = m;
          localObject1 = localObject2;
          if (localDelayedMessage.peer == paramLong)
          {
            n = 0;
            if (localDelayedMessage.obj == null) {
              break label238;
            }
            i = localDelayedMessage.obj.getId();
          }
        }
        for (;;)
        {
          n = m;
          localObject1 = localObject2;
          if (i != 0)
          {
            n = m;
            localObject1 = localObject2;
            if (i > paramInt)
            {
              n = m;
              localObject1 = localObject2;
              if (localObject2 == null)
              {
                n = m;
                localObject1 = localObject2;
                if (m < i)
                {
                  localObject1 = localDelayedMessage;
                  n = i;
                }
              }
            }
          }
          k++;
          m = n;
          localObject2 = localObject1;
          break;
          label238:
          i = n;
          if (localDelayedMessage.messageObjects != null)
          {
            i = n;
            if (!localDelayedMessage.messageObjects.isEmpty()) {
              i = ((MessageObject)localDelayedMessage.messageObjects.get(localDelayedMessage.messageObjects.size() - 1)).getId();
            }
          }
        }
      }
    }
    return (DelayedMessage)localObject1;
  }
  
  public static SendMessagesHelper getInstance(int paramInt)
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
        localObject2 = new org/telegram/messenger/SendMessagesHelper;
        ((SendMessagesHelper)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (SendMessagesHelper)localObject2;
    }
    finally {}
  }
  
  private static String getTrimmedString(String paramString)
  {
    String str = paramString.trim();
    if (str.length() == 0) {}
    for (paramString = str;; paramString = str)
    {
      return paramString;
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
    }
  }
  
  private void performSendDelayedMessage(DelayedMessage paramDelayedMessage)
  {
    performSendDelayedMessage(paramDelayedMessage, -1);
  }
  
  private void performSendDelayedMessage(DelayedMessage paramDelayedMessage, int paramInt)
  {
    if (paramDelayedMessage.type == 0) {
      if (paramDelayedMessage.httpLocation != null)
      {
        putToDelayedMessages(paramDelayedMessage.httpLocation, paramDelayedMessage);
        ImageLoader.getInstance().loadHttpFile(paramDelayedMessage.httpLocation, "file", this.currentAccount);
      }
    }
    Object localObject1;
    Object localObject2;
    Object localObject3;
    label383:
    label402:
    label571:
    label680:
    label794:
    label1061:
    boolean bool1;
    label1133:
    label1139:
    do
    {
      for (;;)
      {
        return;
        if (paramDelayedMessage.sendRequest != null)
        {
          localObject1 = FileLoader.getPathToAttach(paramDelayedMessage.location).toString();
          putToDelayedMessages((String)localObject1, paramDelayedMessage);
          FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, false, true, 16777216);
        }
        else
        {
          localObject1 = FileLoader.getPathToAttach(paramDelayedMessage.location).toString();
          localObject2 = localObject1;
          if (paramDelayedMessage.sendEncryptedRequest != null)
          {
            localObject2 = localObject1;
            if (paramDelayedMessage.location.dc_id != 0)
            {
              localObject2 = new File((String)localObject1);
              localObject3 = localObject2;
              if (!((File)localObject2).exists())
              {
                localObject1 = FileLoader.getPathToAttach(paramDelayedMessage.location, true).toString();
                localObject3 = new File((String)localObject1);
              }
              localObject2 = localObject1;
              if (!((File)localObject3).exists())
              {
                putToDelayedMessages(FileLoader.getAttachFileName(paramDelayedMessage.location), paramDelayedMessage);
                FileLoader.getInstance(this.currentAccount).loadFile(paramDelayedMessage.location, "jpg", 0, 0);
                continue;
              }
            }
          }
          putToDelayedMessages((String)localObject2, paramDelayedMessage);
          FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject2, true, true, 16777216);
          continue;
          if (paramDelayedMessage.type == 1)
          {
            if ((paramDelayedMessage.videoEditedInfo != null) && (paramDelayedMessage.videoEditedInfo.needConvert()))
            {
              localObject3 = paramDelayedMessage.obj.messageOwner.attachPath;
              localObject2 = paramDelayedMessage.obj.getDocument();
              localObject1 = localObject3;
              if (localObject3 == null) {
                localObject1 = FileLoader.getDirectory(4) + "/" + ((TLRPC.Document)localObject2).id + ".mp4";
              }
              putToDelayedMessages((String)localObject1, paramDelayedMessage);
              MediaController.getInstance().scheduleVideoConvert(paramDelayedMessage.obj);
            }
            else
            {
              if (paramDelayedMessage.videoEditedInfo != null)
              {
                if (paramDelayedMessage.videoEditedInfo.file == null) {
                  break label571;
                }
                if ((paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia))
                {
                  localObject1 = ((TLRPC.TL_messages_sendMedia)paramDelayedMessage.sendRequest).media;
                  ((TLRPC.InputMedia)localObject1).file = paramDelayedMessage.videoEditedInfo.file;
                  paramDelayedMessage.videoEditedInfo.file = null;
                }
              }
              else
              {
                if (paramDelayedMessage.sendRequest == null) {
                  break label794;
                }
                if (!(paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia)) {
                  break label680;
                }
                localObject1 = ((TLRPC.TL_messages_sendMedia)paramDelayedMessage.sendRequest).media;
              }
              for (;;)
              {
                if (((TLRPC.InputMedia)localObject1).file == null)
                {
                  localObject3 = paramDelayedMessage.obj.messageOwner.attachPath;
                  localObject2 = paramDelayedMessage.obj.getDocument();
                  localObject1 = localObject3;
                  if (localObject3 == null) {
                    localObject1 = FileLoader.getDirectory(4) + "/" + ((TLRPC.Document)localObject2).id + ".mp4";
                  }
                  putToDelayedMessages((String)localObject1, paramDelayedMessage);
                  if ((paramDelayedMessage.obj.videoEditedInfo != null) && (paramDelayedMessage.obj.videoEditedInfo.needConvert()))
                  {
                    FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, false, false, ((TLRPC.Document)localObject2).size, 33554432);
                    break;
                    localObject1 = ((TLRPC.TL_messages_sendBroadcast)paramDelayedMessage.sendRequest).media;
                    break label383;
                    if (paramDelayedMessage.videoEditedInfo.encryptedFile == null) {
                      break label402;
                    }
                    localObject1 = (TLRPC.TL_decryptedMessage)paramDelayedMessage.sendEncryptedRequest;
                    ((TLRPC.TL_decryptedMessage)localObject1).media.size = ((int)paramDelayedMessage.videoEditedInfo.estimatedSize);
                    ((TLRPC.TL_decryptedMessage)localObject1).media.key = paramDelayedMessage.videoEditedInfo.key;
                    ((TLRPC.TL_decryptedMessage)localObject1).media.iv = paramDelayedMessage.videoEditedInfo.iv;
                    SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject1, paramDelayedMessage.obj.messageOwner, paramDelayedMessage.encryptedChat, paramDelayedMessage.videoEditedInfo.encryptedFile, paramDelayedMessage.originalPath, paramDelayedMessage.obj);
                    paramDelayedMessage.videoEditedInfo.encryptedFile = null;
                    break;
                    localObject1 = ((TLRPC.TL_messages_sendBroadcast)paramDelayedMessage.sendRequest).media;
                    continue;
                  }
                  FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, false, false, 33554432);
                  break;
                }
              }
              localObject1 = FileLoader.getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
              putToDelayedMessages((String)localObject1, paramDelayedMessage);
              FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, false, true, 16777216);
              continue;
              localObject3 = paramDelayedMessage.obj.messageOwner.attachPath;
              localObject2 = paramDelayedMessage.obj.getDocument();
              localObject1 = localObject3;
              if (localObject3 == null) {
                localObject1 = FileLoader.getDirectory(4) + "/" + ((TLRPC.Document)localObject2).id + ".mp4";
              }
              if ((paramDelayedMessage.sendEncryptedRequest != null) && (((TLRPC.Document)localObject2).dc_id != 0) && (!new File((String)localObject1).exists()))
              {
                putToDelayedMessages(FileLoader.getAttachFileName((TLObject)localObject2), paramDelayedMessage);
                FileLoader.getInstance(this.currentAccount).loadFile((TLRPC.Document)localObject2, true, 0);
              }
              else
              {
                putToDelayedMessages((String)localObject1, paramDelayedMessage);
                if ((paramDelayedMessage.obj.videoEditedInfo != null) && (paramDelayedMessage.obj.videoEditedInfo.needConvert())) {
                  FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, true, false, ((TLRPC.Document)localObject2).size, 33554432);
                } else {
                  FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, true, false, 33554432);
                }
              }
            }
          }
          else
          {
            if (paramDelayedMessage.type != 2) {
              break;
            }
            if (paramDelayedMessage.httpLocation != null)
            {
              putToDelayedMessages(paramDelayedMessage.httpLocation, paramDelayedMessage);
              ImageLoader.getInstance().loadHttpFile(paramDelayedMessage.httpLocation, "gif", this.currentAccount);
            }
            else if (paramDelayedMessage.sendRequest != null)
            {
              if ((paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia))
              {
                localObject1 = ((TLRPC.TL_messages_sendMedia)paramDelayedMessage.sendRequest).media;
                if (((TLRPC.InputMedia)localObject1).file != null) {
                  break label1139;
                }
                localObject1 = paramDelayedMessage.obj.messageOwner.attachPath;
                putToDelayedMessages((String)localObject1, paramDelayedMessage);
                localObject3 = FileLoader.getInstance(this.currentAccount);
                if (paramDelayedMessage.sendRequest != null) {
                  break label1133;
                }
              }
              for (bool1 = true;; bool1 = false)
              {
                ((FileLoader)localObject3).uploadFile((String)localObject1, bool1, false, 67108864);
                break;
                localObject1 = ((TLRPC.TL_messages_sendBroadcast)paramDelayedMessage.sendRequest).media;
                break label1061;
              }
              if ((((TLRPC.InputMedia)localObject1).thumb == null) && (paramDelayedMessage.location != null))
              {
                localObject1 = FileLoader.getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
                putToDelayedMessages((String)localObject1, paramDelayedMessage);
                FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, false, true, 16777216);
              }
            }
            else
            {
              localObject1 = paramDelayedMessage.obj.messageOwner.attachPath;
              localObject3 = paramDelayedMessage.obj.getDocument();
              if ((paramDelayedMessage.sendEncryptedRequest != null) && (((TLRPC.Document)localObject3).dc_id != 0) && (!new File((String)localObject1).exists()))
              {
                putToDelayedMessages(FileLoader.getAttachFileName((TLObject)localObject3), paramDelayedMessage);
                FileLoader.getInstance(this.currentAccount).loadFile((TLRPC.Document)localObject3, true, 0);
              }
              else
              {
                putToDelayedMessages((String)localObject1, paramDelayedMessage);
                FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, true, false, 67108864);
              }
            }
          }
        }
      }
      if (paramDelayedMessage.type == 3)
      {
        localObject1 = paramDelayedMessage.obj.messageOwner.attachPath;
        putToDelayedMessages((String)localObject1, paramDelayedMessage);
        localObject3 = FileLoader.getInstance(this.currentAccount);
        if (paramDelayedMessage.sendRequest == null) {}
        for (bool1 = true;; bool1 = false)
        {
          ((FileLoader)localObject3).uploadFile((String)localObject1, bool1, true, 50331648);
          break;
        }
      }
    } while (paramDelayedMessage.type != 4);
    label1415:
    int i;
    Object localObject4;
    if (paramInt < 0)
    {
      bool1 = true;
      if ((paramDelayedMessage.location == null) && (paramDelayedMessage.httpLocation == null) && (!paramDelayedMessage.upload) && (paramInt < 0)) {
        break label2501;
      }
      i = paramInt;
      if (paramInt < 0) {
        i = paramDelayedMessage.messageObjects.size() - 1;
      }
      localObject2 = (MessageObject)paramDelayedMessage.messageObjects.get(i);
      if (((MessageObject)localObject2).getDocument() == null) {
        break label2250;
      }
      if (paramDelayedMessage.videoEditedInfo == null) {
        break label1676;
      }
      localObject3 = ((MessageObject)localObject2).messageOwner.attachPath;
      localObject4 = ((MessageObject)localObject2).getDocument();
      localObject1 = localObject3;
      if (localObject3 == null) {
        localObject1 = FileLoader.getDirectory(4) + "/" + ((TLRPC.Document)localObject4).id + ".mp4";
      }
      putToDelayedMessages((String)localObject1, paramDelayedMessage);
      paramDelayedMessage.extraHashMap.put(localObject2, localObject1);
      paramDelayedMessage.extraHashMap.put((String)localObject1 + "_i", localObject2);
      if (paramDelayedMessage.location != null) {
        paramDelayedMessage.extraHashMap.put((String)localObject1 + "_t", paramDelayedMessage.location);
      }
      MediaController.getInstance().scheduleVideoConvert((MessageObject)localObject2);
      label1644:
      paramDelayedMessage.videoEditedInfo = null;
      paramDelayedMessage.location = null;
      label1654:
      paramDelayedMessage.upload = false;
    }
    for (;;)
    {
      sendReadyToSendGroup(paramDelayedMessage, bool1, true);
      break;
      bool1 = false;
      break label1415;
      label1676:
      localObject4 = ((MessageObject)localObject2).getDocument();
      localObject3 = ((MessageObject)localObject2).messageOwner.attachPath;
      localObject1 = localObject3;
      if (localObject3 == null) {
        localObject1 = FileLoader.getDirectory(4) + "/" + ((TLRPC.Document)localObject4).id + ".mp4";
      }
      if (paramDelayedMessage.sendRequest != null)
      {
        localObject3 = ((TLRPC.TL_inputSingleMedia)((TLRPC.TL_messages_sendMultiMedia)paramDelayedMessage.sendRequest).multi_media.get(i)).media;
        if (((TLRPC.InputMedia)localObject3).file == null)
        {
          putToDelayedMessages((String)localObject1, paramDelayedMessage);
          paramDelayedMessage.extraHashMap.put(localObject2, localObject1);
          paramDelayedMessage.extraHashMap.put(localObject1, localObject3);
          paramDelayedMessage.extraHashMap.put((String)localObject1 + "_i", localObject2);
          if (paramDelayedMessage.location != null) {
            paramDelayedMessage.extraHashMap.put((String)localObject1 + "_t", paramDelayedMessage.location);
          }
          if ((((MessageObject)localObject2).videoEditedInfo != null) && (((MessageObject)localObject2).videoEditedInfo.needConvert()))
          {
            FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, false, false, ((TLRPC.Document)localObject4).size, 33554432);
            break label1644;
          }
          FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, false, false, 33554432);
          break label1644;
        }
        localObject4 = FileLoader.getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
        putToDelayedMessages((String)localObject4, paramDelayedMessage);
        paramDelayedMessage.extraHashMap.put((String)localObject4 + "_o", localObject1);
        paramDelayedMessage.extraHashMap.put(localObject2, localObject4);
        paramDelayedMessage.extraHashMap.put(localObject4, localObject3);
        FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject4, false, true, 16777216);
        break label1644;
      }
      localObject3 = (TLRPC.TL_messages_sendEncryptedMultiMedia)paramDelayedMessage.sendEncryptedRequest;
      putToDelayedMessages((String)localObject1, paramDelayedMessage);
      paramDelayedMessage.extraHashMap.put(localObject2, localObject1);
      paramDelayedMessage.extraHashMap.put(localObject1, ((TLRPC.TL_messages_sendEncryptedMultiMedia)localObject3).files.get(i));
      paramDelayedMessage.extraHashMap.put((String)localObject1 + "_i", localObject2);
      if (paramDelayedMessage.location != null) {
        paramDelayedMessage.extraHashMap.put((String)localObject1 + "_t", paramDelayedMessage.location);
      }
      if ((((MessageObject)localObject2).videoEditedInfo != null) && (((MessageObject)localObject2).videoEditedInfo.needConvert()))
      {
        FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, true, false, ((TLRPC.Document)localObject4).size, 33554432);
        break label1644;
      }
      FileLoader.getInstance(this.currentAccount).uploadFile((String)localObject1, true, false, 33554432);
      break label1644;
      label2250:
      if (paramDelayedMessage.httpLocation != null)
      {
        putToDelayedMessages(paramDelayedMessage.httpLocation, paramDelayedMessage);
        paramDelayedMessage.extraHashMap.put(localObject2, paramDelayedMessage.httpLocation);
        paramDelayedMessage.extraHashMap.put(paramDelayedMessage.httpLocation, localObject2);
        ImageLoader.getInstance().loadHttpFile(paramDelayedMessage.httpLocation, "file", this.currentAccount);
        paramDelayedMessage.httpLocation = null;
        break label1654;
      }
      if (paramDelayedMessage.sendRequest != null)
      {
        localObject1 = ((TLRPC.TL_inputSingleMedia)((TLRPC.TL_messages_sendMultiMedia)paramDelayedMessage.sendRequest).multi_media.get(i)).media;
        label2348:
        localObject3 = FileLoader.getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
        putToDelayedMessages((String)localObject3, paramDelayedMessage);
        paramDelayedMessage.extraHashMap.put(localObject3, localObject1);
        paramDelayedMessage.extraHashMap.put(localObject2, localObject3);
        localObject1 = FileLoader.getInstance(this.currentAccount);
        if (paramDelayedMessage.sendEncryptedRequest == null) {
          break label2495;
        }
      }
      label2495:
      for (boolean bool2 = true;; bool2 = false)
      {
        ((FileLoader)localObject1).uploadFile((String)localObject3, bool2, true, 16777216);
        paramDelayedMessage.location = null;
        break;
        localObject1 = (TLObject)((TLRPC.TL_messages_sendEncryptedMultiMedia)paramDelayedMessage.sendEncryptedRequest).files.get(i);
        break label2348;
      }
      label2501:
      if (!paramDelayedMessage.messageObjects.isEmpty()) {
        putToSendingMessages(((MessageObject)paramDelayedMessage.messageObjects.get(paramDelayedMessage.messageObjects.size() - 1)).messageOwner);
      }
    }
  }
  
  private void performSendMessageRequest(TLObject paramTLObject, MessageObject paramMessageObject, String paramString)
  {
    performSendMessageRequest(paramTLObject, paramMessageObject, paramString, null, false);
  }
  
  private void performSendMessageRequest(final TLObject paramTLObject, final MessageObject paramMessageObject, final String paramString, DelayedMessage paramDelayedMessage, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      localObject = findMaxDelayedMessageForMessageId(paramMessageObject.getId(), paramMessageObject.getDialogId());
      if (localObject != null)
      {
        ((DelayedMessage)localObject).addDelayedRequest(paramTLObject, paramMessageObject, paramString);
        if ((paramDelayedMessage != null) && (paramDelayedMessage.requests != null)) {
          ((DelayedMessage)localObject).requests.addAll(paramDelayedMessage.requests);
        }
        return;
      }
    }
    final TLRPC.Message localMessage = paramMessageObject.messageOwner;
    putToSendingMessages(localMessage);
    Object localObject = ConnectionsManager.getInstance(this.currentAccount);
    paramMessageObject = new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int i = 0;
            int j = 0;
            final ArrayList localArrayList;
            final Object localObject1;
            Object localObject2;
            final Object localObject3;
            if (paramAnonymousTL_error == null)
            {
              final int k = SendMessagesHelper.13.this.val$newMsgObj.id;
              final boolean bool1 = SendMessagesHelper.13.this.val$req instanceof TLRPC.TL_messages_sendBroadcast;
              localArrayList = new ArrayList();
              final String str = SendMessagesHelper.13.this.val$newMsgObj.attachPath;
              if ((paramAnonymousTLObject instanceof TLRPC.TL_updateShortSentMessage))
              {
                localObject1 = (TLRPC.TL_updateShortSentMessage)paramAnonymousTLObject;
                localObject2 = SendMessagesHelper.13.this.val$newMsgObj;
                localObject3 = SendMessagesHelper.13.this.val$newMsgObj;
                i = ((TLRPC.TL_updateShortSentMessage)localObject1).id;
                ((TLRPC.Message)localObject3).id = i;
                ((TLRPC.Message)localObject2).local_id = i;
                SendMessagesHelper.13.this.val$newMsgObj.date = ((TLRPC.TL_updateShortSentMessage)localObject1).date;
                SendMessagesHelper.13.this.val$newMsgObj.entities = ((TLRPC.TL_updateShortSentMessage)localObject1).entities;
                SendMessagesHelper.13.this.val$newMsgObj.out = ((TLRPC.TL_updateShortSentMessage)localObject1).out;
                if (((TLRPC.TL_updateShortSentMessage)localObject1).media != null)
                {
                  SendMessagesHelper.13.this.val$newMsgObj.media = ((TLRPC.TL_updateShortSentMessage)localObject1).media;
                  localObject3 = SendMessagesHelper.13.this.val$newMsgObj;
                  ((TLRPC.Message)localObject3).flags |= 0x200;
                  ImageLoader.saveMessageThumbs(SendMessagesHelper.13.this.val$newMsgObj);
                }
                if (((((TLRPC.TL_updateShortSentMessage)localObject1).media instanceof TLRPC.TL_messageMediaGame)) && (!TextUtils.isEmpty(((TLRPC.TL_updateShortSentMessage)localObject1).message))) {
                  SendMessagesHelper.13.this.val$newMsgObj.message = ((TLRPC.TL_updateShortSentMessage)localObject1).message;
                }
                if (!SendMessagesHelper.13.this.val$newMsgObj.entities.isEmpty())
                {
                  localObject3 = SendMessagesHelper.13.this.val$newMsgObj;
                  ((TLRPC.Message)localObject3).flags |= 0x80;
                }
                Utilities.stageQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewDifferenceParams(-1, localObject1.pts, localObject1.date, localObject1.pts_count);
                  }
                });
                localArrayList.add(SendMessagesHelper.13.this.val$newMsgObj);
                if (MessageObject.isLiveLocationMessage(SendMessagesHelper.13.this.val$newMsgObj)) {
                  LocationController.getInstance(SendMessagesHelper.this.currentAccount).addSharingLocation(SendMessagesHelper.13.this.val$newMsgObj.dialog_id, SendMessagesHelper.13.this.val$newMsgObj.id, SendMessagesHelper.13.this.val$newMsgObj.media.period, SendMessagesHelper.13.this.val$newMsgObj);
                }
                i = j;
                if (j == 0)
                {
                  StatsController.getInstance(SendMessagesHelper.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, 1);
                  SendMessagesHelper.13.this.val$newMsgObj.send_state = 0;
                  localObject1 = NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount);
                  int m = NotificationCenter.messageReceivedByServer;
                  if (!bool1) {
                    break label1136;
                  }
                  i = k;
                  label450:
                  ((NotificationCenter)localObject1).postNotificationName(m, new Object[] { Integer.valueOf(k), Integer.valueOf(i), SendMessagesHelper.13.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.13.this.val$newMsgObj.dialog_id) });
                  MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      Object localObject = MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount);
                      long l = SendMessagesHelper.13.this.val$newMsgObj.random_id;
                      int i = k;
                      if (bool1) {}
                      for (int j = k;; j = SendMessagesHelper.13.this.val$newMsgObj.id)
                      {
                        ((MessagesStorage)localObject).updateMessageStateAndId(l, Integer.valueOf(i), j, 0, false, SendMessagesHelper.13.this.val$newMsgObj.to_id.channel_id);
                        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(localArrayList, true, false, bool1, 0);
                        if (bool1)
                        {
                          localObject = new ArrayList();
                          ((ArrayList)localObject).add(SendMessagesHelper.13.this.val$newMsgObj);
                          MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages((ArrayList)localObject, true, false, false, 0);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            if (SendMessagesHelper.13.1.5.this.val$isBroadcast)
                            {
                              for (i = 0; i < SendMessagesHelper.13.1.5.this.val$sentMessages.size(); i++)
                              {
                                Object localObject1 = (TLRPC.Message)SendMessagesHelper.13.1.5.this.val$sentMessages.get(i);
                                localObject2 = new ArrayList();
                                localObject1 = new MessageObject(SendMessagesHelper.this.currentAccount, (TLRPC.Message)localObject1, false);
                                ((ArrayList)localObject2).add(localObject1);
                                MessagesController.getInstance(SendMessagesHelper.this.currentAccount).updateInterfaceWithMessages(((MessageObject)localObject1).getDialogId(), (ArrayList)localObject2, true);
                              }
                              NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                            DataQuery.getInstance(SendMessagesHelper.this.currentAccount).increasePeerRaiting(SendMessagesHelper.13.this.val$newMsgObj.dialog_id);
                            Object localObject2 = NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount);
                            int j = NotificationCenter.messageReceivedByServer;
                            int k = SendMessagesHelper.13.1.5.this.val$oldId;
                            if (SendMessagesHelper.13.1.5.this.val$isBroadcast) {}
                            for (int i = SendMessagesHelper.13.1.5.this.val$oldId;; i = SendMessagesHelper.13.this.val$newMsgObj.id)
                            {
                              ((NotificationCenter)localObject2).postNotificationName(j, new Object[] { Integer.valueOf(k), Integer.valueOf(i), SendMessagesHelper.13.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.13.this.val$newMsgObj.dialog_id) });
                              SendMessagesHelper.this.processSentMessage(SendMessagesHelper.13.1.5.this.val$oldId);
                              SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.13.1.5.this.val$oldId);
                              return;
                            }
                          }
                        });
                        if ((MessageObject.isVideoMessage(SendMessagesHelper.13.this.val$newMsgObj)) || (MessageObject.isRoundVideoMessage(SendMessagesHelper.13.this.val$newMsgObj)) || (MessageObject.isNewGifMessage(SendMessagesHelper.13.this.val$newMsgObj))) {
                          SendMessagesHelper.this.stopVideoService(str);
                        }
                        return;
                      }
                    }
                  });
                }
              }
            }
            for (i = j;; i = 1)
            {
              if (i != 0)
              {
                MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(SendMessagesHelper.13.this.val$newMsgObj);
                SendMessagesHelper.13.this.val$newMsgObj.send_state = 2;
                NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(SendMessagesHelper.13.this.val$newMsgObj.id) });
                SendMessagesHelper.this.processSentMessage(SendMessagesHelper.13.this.val$newMsgObj.id);
                if ((MessageObject.isVideoMessage(SendMessagesHelper.13.this.val$newMsgObj)) || (MessageObject.isRoundVideoMessage(SendMessagesHelper.13.this.val$newMsgObj)) || (MessageObject.isNewGifMessage(SendMessagesHelper.13.this.val$newMsgObj))) {
                  SendMessagesHelper.this.stopVideoService(SendMessagesHelper.13.this.val$newMsgObj.attachPath);
                }
                SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.13.this.val$newMsgObj.id);
              }
              return;
              if (!(paramAnonymousTLObject instanceof TLRPC.Updates)) {
                break;
              }
              final TLRPC.Updates localUpdates = (TLRPC.Updates)paramAnonymousTLObject;
              localObject2 = ((TLRPC.Updates)paramAnonymousTLObject).updates;
              localObject3 = null;
              j = 0;
              label750:
              localObject1 = localObject3;
              label827:
              boolean bool2;
              if (j < ((ArrayList)localObject2).size())
              {
                localObject1 = (TLRPC.Update)((ArrayList)localObject2).get(j);
                if ((localObject1 instanceof TLRPC.TL_updateNewMessage))
                {
                  localObject3 = (TLRPC.TL_updateNewMessage)localObject1;
                  localObject1 = ((TLRPC.TL_updateNewMessage)localObject3).message;
                  localArrayList.add(localObject1);
                  Utilities.stageQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewDifferenceParams(-1, localObject3.pts, -1, localObject3.pts_count);
                    }
                  });
                  ((ArrayList)localObject2).remove(j);
                }
              }
              else
              {
                if (localObject1 == null) {
                  break label1131;
                }
                ImageLoader.saveMessageThumbs((TLRPC.Message)localObject1);
                localObject2 = (Integer)MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.Message)localObject1).dialog_id));
                localObject3 = localObject2;
                if (localObject2 == null)
                {
                  localObject3 = Integer.valueOf(MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getDialogReadMax(((TLRPC.Message)localObject1).out, ((TLRPC.Message)localObject1).dialog_id));
                  MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.Message)localObject1).dialog_id), localObject3);
                }
                if (((Integer)localObject3).intValue() >= ((TLRPC.Message)localObject1).id) {
                  break label1125;
                }
                bool2 = true;
                label955:
                ((TLRPC.Message)localObject1).unread = bool2;
                SendMessagesHelper.13.this.val$newMsgObj.id = ((TLRPC.Message)localObject1).id;
                SendMessagesHelper.this.updateMediaPaths(SendMessagesHelper.13.this.val$msgObj, (TLRPC.Message)localObject1, SendMessagesHelper.13.this.val$originalPath, false);
              }
              label1125:
              label1131:
              for (j = i;; j = 1)
              {
                Utilities.stageQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates(localUpdates, false);
                  }
                });
                break;
                if ((localObject1 instanceof TLRPC.TL_updateNewChannelMessage))
                {
                  localObject3 = (TLRPC.TL_updateNewChannelMessage)localObject1;
                  localObject1 = ((TLRPC.TL_updateNewChannelMessage)localObject3).message;
                  localArrayList.add(localObject1);
                  if ((SendMessagesHelper.13.this.val$newMsgObj.flags & 0x80000000) != 0)
                  {
                    TLRPC.Message localMessage = ((TLRPC.TL_updateNewChannelMessage)localObject3).message;
                    localMessage.flags |= 0x80000000;
                  }
                  Utilities.stageQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewChannelDifferenceParams(localObject3.pts, localObject3.pts_count, localObject3.message.to_id.channel_id);
                    }
                  });
                  ((ArrayList)localObject2).remove(j);
                  break label827;
                }
                j++;
                break label750;
                bool2 = false;
                break label955;
              }
              label1136:
              i = SendMessagesHelper.13.this.val$newMsgObj.id;
              break label450;
              AlertsCreator.processError(SendMessagesHelper.this.currentAccount, paramAnonymousTL_error, null, SendMessagesHelper.13.this.val$req, new Object[0]);
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
            SendMessagesHelper.14.this.val$newMsgObj.send_state = 0;
            NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByAck, new Object[] { Integer.valueOf(this.val$msg_id) });
          }
        });
      }
    };
    if ((paramTLObject instanceof TLRPC.TL_messages_sendMessage)) {}
    for (int i = 128;; i = 0)
    {
      ((ConnectionsManager)localObject).sendRequest(paramTLObject, paramMessageObject, paramString, i | 0x44);
      if (paramDelayedMessage == null) {
        break;
      }
      paramDelayedMessage.sendDelayedRequests();
      break;
    }
  }
  
  private void performSendMessageRequestMulti(final TLRPC.TL_messages_sendMultiMedia paramTL_messages_sendMultiMedia, final ArrayList<MessageObject> paramArrayList, final ArrayList<String> paramArrayList1)
  {
    for (int i = 0; i < paramArrayList.size(); i++) {
      putToSendingMessages(((MessageObject)paramArrayList.get(i)).messageOwner);
    }
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramTL_messages_sendMultiMedia, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int i = 0;
            Object localObject1;
            final int k;
            if (paramAnonymousTL_error == null)
            {
              SparseArray localSparseArray = new SparseArray();
              LongSparseArray localLongSparseArray = new LongSparseArray();
              final TLRPC.Updates localUpdates = (TLRPC.Updates)paramAnonymousTLObject;
              localObject1 = ((TLRPC.Updates)paramAnonymousTLObject).updates;
              int j = 0;
              final Object localObject2;
              if (j < ((ArrayList)localObject1).size())
              {
                localObject2 = (TLRPC.Update)((ArrayList)localObject1).get(j);
                if ((localObject2 instanceof TLRPC.TL_updateMessageID))
                {
                  localObject2 = (TLRPC.TL_updateMessageID)localObject2;
                  localLongSparseArray.put(((TLRPC.TL_updateMessageID)localObject2).random_id, Integer.valueOf(((TLRPC.TL_updateMessageID)localObject2).id));
                  ((ArrayList)localObject1).remove(j);
                  k = j - 1;
                }
                for (;;)
                {
                  j = k + 1;
                  break;
                  if ((localObject2 instanceof TLRPC.TL_updateNewMessage))
                  {
                    localObject2 = (TLRPC.TL_updateNewMessage)localObject2;
                    localSparseArray.put(((TLRPC.TL_updateNewMessage)localObject2).message.id, ((TLRPC.TL_updateNewMessage)localObject2).message);
                    Utilities.stageQueue.postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewDifferenceParams(-1, localObject2.pts, -1, localObject2.pts_count);
                      }
                    });
                    ((ArrayList)localObject1).remove(j);
                    k = j - 1;
                  }
                  else
                  {
                    k = j;
                    if ((localObject2 instanceof TLRPC.TL_updateNewChannelMessage))
                    {
                      localObject2 = (TLRPC.TL_updateNewChannelMessage)localObject2;
                      localSparseArray.put(((TLRPC.TL_updateNewChannelMessage)localObject2).message.id, ((TLRPC.TL_updateNewChannelMessage)localObject2).message);
                      Utilities.stageQueue.postRunnable(new Runnable()
                      {
                        public void run()
                        {
                          MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewChannelDifferenceParams(localObject2.pts, localObject2.pts_count, localObject2.message.to_id.channel_id);
                        }
                      });
                      ((ArrayList)localObject1).remove(j);
                      k = j - 1;
                    }
                  }
                }
              }
              j = 0;
              k = i;
              if (j < SendMessagesHelper.12.this.val$msgObjs.size())
              {
                MessageObject localMessageObject = (MessageObject)SendMessagesHelper.12.this.val$msgObjs.get(j);
                String str = (String)SendMessagesHelper.12.this.val$originalPaths.get(j);
                final TLRPC.Message localMessage1 = localMessageObject.messageOwner;
                k = localMessage1.id;
                final ArrayList localArrayList = new ArrayList();
                localObject1 = localMessage1.attachPath;
                localObject1 = (Integer)localLongSparseArray.get(localMessage1.random_id);
                if (localObject1 != null)
                {
                  TLRPC.Message localMessage2 = (TLRPC.Message)localSparseArray.get(((Integer)localObject1).intValue());
                  if (localMessage2 != null)
                  {
                    localArrayList.add(localMessage2);
                    localMessage1.id = localMessage2.id;
                    if ((localMessage1.flags & 0x80000000) != 0) {
                      localMessage2.flags |= 0x80000000;
                    }
                    localObject2 = (Integer)MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(localMessage2.dialog_id));
                    localObject1 = localObject2;
                    if (localObject2 == null)
                    {
                      localObject1 = Integer.valueOf(MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getDialogReadMax(localMessage2.out, localMessage2.dialog_id));
                      MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(localMessage2.dialog_id), localObject1);
                    }
                    if (((Integer)localObject1).intValue() < localMessage2.id) {}
                    for (boolean bool = true;; bool = false)
                    {
                      localMessage2.unread = bool;
                      SendMessagesHelper.this.updateMediaPaths(localMessageObject, localMessage2, str, false);
                      if (0 == 0)
                      {
                        StatsController.getInstance(SendMessagesHelper.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, 1);
                        localMessage1.send_state = 0;
                        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { Integer.valueOf(k), Integer.valueOf(localMessage1.id), localMessage1, Long.valueOf(localMessage1.dialog_id) });
                        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                        {
                          public void run()
                          {
                            MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).updateMessageStateAndId(localMessage1.random_id, Integer.valueOf(k), localMessage1.id, 0, false, localMessage1.to_id.channel_id);
                            MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(localArrayList, true, false, false, 0);
                            AndroidUtilities.runOnUIThread(new Runnable()
                            {
                              public void run()
                              {
                                DataQuery.getInstance(SendMessagesHelper.this.currentAccount).increasePeerRaiting(SendMessagesHelper.12.1.3.this.val$newMsgObj.dialog_id);
                                NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { Integer.valueOf(SendMessagesHelper.12.1.3.this.val$oldId), Integer.valueOf(SendMessagesHelper.12.1.3.this.val$newMsgObj.id), SendMessagesHelper.12.1.3.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.12.1.3.this.val$newMsgObj.dialog_id) });
                                SendMessagesHelper.this.processSentMessage(SendMessagesHelper.12.1.3.this.val$oldId);
                                SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.12.1.3.this.val$oldId);
                              }
                            });
                          }
                        });
                      }
                      j++;
                      break;
                    }
                  }
                  k = 1;
                }
              }
              else
              {
                Utilities.stageQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates(localUpdates, false);
                  }
                });
              }
            }
            for (;;)
            {
              if (k == 0) {
                return;
              }
              for (k = 0; k < SendMessagesHelper.12.this.val$msgObjs.size(); k++)
              {
                localObject1 = ((MessageObject)SendMessagesHelper.12.this.val$msgObjs.get(k)).messageOwner;
                MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError((TLRPC.Message)localObject1);
                ((TLRPC.Message)localObject1).send_state = 2;
                NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((TLRPC.Message)localObject1).id) });
                SendMessagesHelper.this.processSentMessage(((TLRPC.Message)localObject1).id);
                SendMessagesHelper.this.removeFromSendingMessages(((TLRPC.Message)localObject1).id);
              }
              k = 1;
              break;
              AlertsCreator.processError(SendMessagesHelper.this.currentAccount, paramAnonymousTL_error, null, SendMessagesHelper.12.this.val$req, new Object[0]);
              k = 1;
            }
          }
        });
      }
    }, null, 68);
  }
  
  public static void prepareSendingAudioDocuments(ArrayList<MessageObject> paramArrayList, final long paramLong, final MessageObject paramMessageObject)
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        int i = this.val$messageObjects.size();
        for (int j = 0;; j++)
        {
          final MessageObject localMessageObject;
          final Object localObject2;
          int k;
          Object localObject3;
          if (j < i)
          {
            localMessageObject = (MessageObject)this.val$messageObjects.get(j);
            localObject1 = localMessageObject.messageOwner.attachPath;
            localObject2 = new File((String)localObject1);
            if ((int)paramLong != 0) {
              break label194;
            }
            k = 1;
            localObject3 = localObject1;
            if (localObject1 != null) {
              localObject3 = (String)localObject1 + "audio" + ((File)localObject2).length();
            }
            localObject1 = null;
            if (k == 0)
            {
              localObject1 = MessagesStorage.getInstance(paramMessageObject);
              if (k != 0) {
                break label200;
              }
            }
          }
          label194:
          label200:
          for (int m = 1;; m = 4)
          {
            localObject1 = (TLRPC.TL_document)((MessagesStorage)localObject1).getSentFile((String)localObject3, m);
            localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = (TLRPC.TL_document)localMessageObject.messageOwner.media.document;
            }
            if (k == 0) {
              break label206;
            }
            k = (int)(paramLong >> 32);
            if (MessagesController.getInstance(paramMessageObject).getEncryptedChat(Integer.valueOf(k)) != null) {
              break label206;
            }
            return;
            k = 0;
            break;
          }
          label206:
          final Object localObject1 = new HashMap();
          if (localObject3 != null) {
            ((HashMap)localObject1).put("originalPath", localObject3);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper.getInstance(SendMessagesHelper.17.this.val$currentAccount).sendMessage(localObject2, null, localMessageObject.messageOwner.attachPath, SendMessagesHelper.17.this.val$dialog_id, SendMessagesHelper.17.this.val$reply_to_msg, null, null, null, localObject1, 0);
            }
          });
        }
      }
    }).start();
  }
  
  public static void prepareSendingBotContextResult(TLRPC.BotInlineResult paramBotInlineResult, final HashMap<String, String> paramHashMap, final long paramLong, final MessageObject paramMessageObject)
  {
    if (paramBotInlineResult == null) {}
    for (;;)
    {
      return;
      int i = UserConfig.selectedAccount;
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaAuto))
      {
        new Thread(new Runnable()
        {
          public void run()
          {
            String str = null;
            TLRPC.TL_document localTL_document = null;
            Object localObject1 = null;
            Object localObject2 = null;
            TLRPC.TL_game localTL_game = null;
            final Object localObject3;
            final Object localObject4;
            final Object localObject5;
            final Object localObject6;
            if ((this.val$result instanceof TLRPC.TL_botInlineMediaResult)) {
              if (this.val$result.type.equals("game"))
              {
                if ((int)paramLong == 0) {
                  return;
                }
                localTL_game = new TLRPC.TL_game();
                localTL_game.title = this.val$result.title;
                localTL_game.description = this.val$result.description;
                localTL_game.short_name = this.val$result.id;
                localTL_game.photo = this.val$result.photo;
                localObject3 = localTL_document;
                localObject4 = str;
                localObject5 = localTL_game;
                localObject6 = localObject2;
                if ((this.val$result.document instanceof TLRPC.TL_document))
                {
                  localTL_game.document = this.val$result.document;
                  localTL_game.flags |= 0x1;
                  localObject6 = localObject2;
                  localObject5 = localTL_game;
                  localObject4 = str;
                  localObject3 = localTL_document;
                }
              }
            }
            for (;;)
            {
              if ((paramMessageObject != null) && (this.val$result.content != null)) {
                paramMessageObject.put("originalPath", this.val$result.content.url);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (localObject3 != null) {
                    SendMessagesHelper.getInstance(SendMessagesHelper.19.this.val$currentAccount).sendMessage(localObject3, null, localObject4, SendMessagesHelper.19.this.val$dialog_id, SendMessagesHelper.19.this.val$reply_to_msg, SendMessagesHelper.19.this.val$result.send_message.message, SendMessagesHelper.19.this.val$result.send_message.entities, SendMessagesHelper.19.this.val$result.send_message.reply_markup, SendMessagesHelper.19.this.val$params, 0);
                  }
                  for (;;)
                  {
                    return;
                    if (localObject6 != null)
                    {
                      SendMessagesHelper localSendMessagesHelper = SendMessagesHelper.getInstance(SendMessagesHelper.19.this.val$currentAccount);
                      TLRPC.TL_photo localTL_photo = localObject6;
                      if (SendMessagesHelper.19.this.val$result.content != null) {}
                      for (String str = SendMessagesHelper.19.this.val$result.content.url;; str = null)
                      {
                        localSendMessagesHelper.sendMessage(localTL_photo, str, SendMessagesHelper.19.this.val$dialog_id, SendMessagesHelper.19.this.val$reply_to_msg, SendMessagesHelper.19.this.val$result.send_message.message, SendMessagesHelper.19.this.val$result.send_message.entities, SendMessagesHelper.19.this.val$result.send_message.reply_markup, SendMessagesHelper.19.this.val$params, 0);
                        break;
                      }
                    }
                    if (localObject5 != null) {
                      SendMessagesHelper.getInstance(SendMessagesHelper.19.this.val$currentAccount).sendMessage(localObject5, SendMessagesHelper.19.this.val$dialog_id, SendMessagesHelper.19.this.val$result.send_message.reply_markup, SendMessagesHelper.19.this.val$params);
                    }
                  }
                }
              });
              break;
              if (this.val$result.document != null)
              {
                localObject3 = localTL_document;
                localObject4 = str;
                localObject5 = localTL_game;
                localObject6 = localObject2;
                if ((this.val$result.document instanceof TLRPC.TL_document))
                {
                  localObject3 = (TLRPC.TL_document)this.val$result.document;
                  localObject4 = str;
                  localObject5 = localTL_game;
                  localObject6 = localObject2;
                }
              }
              else
              {
                localObject3 = localTL_document;
                localObject4 = str;
                localObject5 = localTL_game;
                localObject6 = localObject2;
                if (this.val$result.photo != null)
                {
                  localObject3 = localTL_document;
                  localObject4 = str;
                  localObject5 = localTL_game;
                  localObject6 = localObject2;
                  if ((this.val$result.photo instanceof TLRPC.TL_photo))
                  {
                    localObject6 = (TLRPC.TL_photo)this.val$result.photo;
                    localObject3 = localTL_document;
                    localObject4 = str;
                    localObject5 = localTL_game;
                    continue;
                    localObject3 = localTL_document;
                    localObject4 = str;
                    localObject5 = localTL_game;
                    localObject6 = localObject2;
                    if (this.val$result.content != null)
                    {
                      localObject6 = new File(FileLoader.getDirectory(4), Utilities.MD5(this.val$result.content.url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.content.url, "file"));
                      label466:
                      int i;
                      if (((File)localObject6).exists())
                      {
                        str = ((File)localObject6).getAbsolutePath();
                        localObject3 = this.val$result.type;
                        i = -1;
                        switch (((String)localObject3).hashCode())
                        {
                        default: 
                          switch (i)
                          {
                          default: 
                            localObject3 = localTL_document;
                            localObject4 = str;
                            localObject5 = localTL_game;
                            localObject6 = localObject2;
                            break;
                          case 0: 
                          case 1: 
                          case 2: 
                          case 3: 
                          case 4: 
                          case 5: 
                            label548:
                            localTL_document = new TLRPC.TL_document();
                            localTL_document.id = 0L;
                            localTL_document.size = 0;
                            localTL_document.dc_id = 0;
                            localTL_document.mime_type = this.val$result.content.mime_type;
                            localTL_document.date = ConnectionsManager.getInstance(paramHashMap).getCurrentTime();
                            localObject3 = new TLRPC.TL_documentAttributeFilename();
                            localTL_document.attributes.add(localObject3);
                            localObject6 = this.val$result.type;
                            i = -1;
                            switch (((String)localObject6).hashCode())
                            {
                            default: 
                              label756:
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
                        if (((TLRPC.TL_documentAttributeFilename)localObject3).file_name == null) {
                          ((TLRPC.TL_documentAttributeFilename)localObject3).file_name = "file";
                        }
                        if (localTL_document.mime_type == null) {
                          localTL_document.mime_type = "application/octet-stream";
                        }
                        localObject3 = localTL_document;
                        localObject4 = str;
                        localObject5 = localTL_game;
                        localObject6 = localObject2;
                        if (localTL_document.thumb != null) {
                          break;
                        }
                        localTL_document.thumb = new TLRPC.TL_photoSize();
                        localObject6 = MessageObject.getInlineResultWidthAndHeight(this.val$result);
                        localTL_document.thumb.w = localObject6[0];
                        localTL_document.thumb.h = localObject6[1];
                        localTL_document.thumb.size = 0;
                        localTL_document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
                        localTL_document.thumb.type = "x";
                        localObject3 = localTL_document;
                        localObject4 = str;
                        localObject5 = localTL_game;
                        localObject6 = localObject2;
                        break;
                        str = this.val$result.content.url;
                        break label466;
                        if (!((String)localObject3).equals("audio")) {
                          break label548;
                        }
                        i = 0;
                        break label548;
                        if (!((String)localObject3).equals("voice")) {
                          break label548;
                        }
                        i = 1;
                        break label548;
                        if (!((String)localObject3).equals("file")) {
                          break label548;
                        }
                        i = 2;
                        break label548;
                        if (!((String)localObject3).equals("video")) {
                          break label548;
                        }
                        i = 3;
                        break label548;
                        if (!((String)localObject3).equals("sticker")) {
                          break label548;
                        }
                        i = 4;
                        break label548;
                        if (!((String)localObject3).equals("gif")) {
                          break label548;
                        }
                        i = 5;
                        break label548;
                        if (!((String)localObject3).equals("photo")) {
                          break label548;
                        }
                        i = 6;
                        break label548;
                        if (!((String)localObject6).equals("gif")) {
                          break label756;
                        }
                        i = 0;
                        break label756;
                        if (!((String)localObject6).equals("voice")) {
                          break label756;
                        }
                        i = 1;
                        break label756;
                        if (!((String)localObject6).equals("audio")) {
                          break label756;
                        }
                        i = 2;
                        break label756;
                        if (!((String)localObject6).equals("file")) {
                          break label756;
                        }
                        i = 3;
                        break label756;
                        if (!((String)localObject6).equals("video")) {
                          break label756;
                        }
                        i = 4;
                        break label756;
                        if (!((String)localObject6).equals("sticker")) {
                          break label756;
                        }
                        i = 5;
                        break label756;
                        ((TLRPC.TL_documentAttributeFilename)localObject3).file_name = "animation.gif";
                        if (str.endsWith("mp4"))
                        {
                          localTL_document.mime_type = "video/mp4";
                          localTL_document.attributes.add(new TLRPC.TL_documentAttributeAnimated());
                        }
                        for (;;)
                        {
                          try
                          {
                            if (!str.endsWith("mp4")) {
                              break label1265;
                            }
                            localObject6 = ThumbnailUtils.createVideoThumbnail(str, 1);
                            if (localObject6 == null) {
                              break;
                            }
                            localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject6, 90.0F, 90.0F, 55, false);
                            ((Bitmap)localObject6).recycle();
                          }
                          catch (Throwable localThrowable1)
                          {
                            FileLog.e(localThrowable1);
                          }
                          break;
                          localTL_document.mime_type = "image/gif";
                          continue;
                          label1265:
                          localObject7 = ImageLoader.loadBitmap(str, null, 90.0F, 90.0F, true);
                        }
                        Object localObject7 = new TLRPC.TL_documentAttributeAudio();
                        ((TLRPC.TL_documentAttributeAudio)localObject7).duration = MessageObject.getInlineResultDuration(this.val$result);
                        ((TLRPC.TL_documentAttributeAudio)localObject7).voice = true;
                        ((TLRPC.TL_documentAttributeFilename)localObject3).file_name = "audio.ogg";
                        localTL_document.attributes.add(localObject7);
                        localTL_document.thumb = new TLRPC.TL_photoSizeEmpty();
                        localTL_document.thumb.type = "s";
                        continue;
                        localObject7 = new TLRPC.TL_documentAttributeAudio();
                        ((TLRPC.TL_documentAttributeAudio)localObject7).duration = MessageObject.getInlineResultDuration(this.val$result);
                        ((TLRPC.TL_documentAttributeAudio)localObject7).title = this.val$result.title;
                        ((TLRPC.TL_documentAttributeAudio)localObject7).flags |= 0x1;
                        if (this.val$result.description != null)
                        {
                          ((TLRPC.TL_documentAttributeAudio)localObject7).performer = this.val$result.description;
                          ((TLRPC.TL_documentAttributeAudio)localObject7).flags |= 0x2;
                        }
                        ((TLRPC.TL_documentAttributeFilename)localObject3).file_name = "audio.mp3";
                        localTL_document.attributes.add(localObject7);
                        localTL_document.thumb = new TLRPC.TL_photoSizeEmpty();
                        localTL_document.thumb.type = "s";
                        continue;
                        i = this.val$result.content.mime_type.lastIndexOf('/');
                        if (i != -1)
                        {
                          ((TLRPC.TL_documentAttributeFilename)localObject3).file_name = ("file." + this.val$result.content.mime_type.substring(i + 1));
                        }
                        else
                        {
                          ((TLRPC.TL_documentAttributeFilename)localObject3).file_name = "file";
                          continue;
                          ((TLRPC.TL_documentAttributeFilename)localObject3).file_name = "video.mp4";
                          localObject7 = new TLRPC.TL_documentAttributeVideo();
                          localObject4 = MessageObject.getInlineResultWidthAndHeight(this.val$result);
                          ((TLRPC.TL_documentAttributeVideo)localObject7).w = localObject4[0];
                          ((TLRPC.TL_documentAttributeVideo)localObject7).h = localObject4[1];
                          ((TLRPC.TL_documentAttributeVideo)localObject7).duration = MessageObject.getInlineResultDuration(this.val$result);
                          ((TLRPC.TL_documentAttributeVideo)localObject7).supports_streaming = true;
                          localTL_document.attributes.add(localObject7);
                          try
                          {
                            if (this.val$result.thumb == null) {
                              continue;
                            }
                            localObject4 = new java/io/File;
                            localObject7 = FileLoader.getDirectory(4);
                            localObject5 = new java/lang/StringBuilder;
                            ((StringBuilder)localObject5).<init>();
                            ((File)localObject4).<init>((File)localObject7, Utilities.MD5(this.val$result.thumb.url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb.url, "jpg"));
                            localObject7 = ImageLoader.loadBitmap(((File)localObject4).getAbsolutePath(), null, 90.0F, 90.0F, true);
                            if (localObject7 == null) {
                              continue;
                            }
                            localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject7, 90.0F, 90.0F, 55, false);
                            ((Bitmap)localObject7).recycle();
                          }
                          catch (Throwable localThrowable2)
                          {
                            FileLog.e(localThrowable2);
                          }
                          continue;
                          Object localObject8 = new TLRPC.TL_documentAttributeSticker();
                          ((TLRPC.TL_documentAttributeSticker)localObject8).alt = "";
                          ((TLRPC.TL_documentAttributeSticker)localObject8).stickerset = new TLRPC.TL_inputStickerSetEmpty();
                          localTL_document.attributes.add(localObject8);
                          localObject4 = new TLRPC.TL_documentAttributeImageSize();
                          localObject8 = MessageObject.getInlineResultWidthAndHeight(this.val$result);
                          ((TLRPC.TL_documentAttributeImageSize)localObject4).w = localObject8[0];
                          ((TLRPC.TL_documentAttributeImageSize)localObject4).h = localObject8[1];
                          localTL_document.attributes.add(localObject4);
                          ((TLRPC.TL_documentAttributeFilename)localObject3).file_name = "sticker.webp";
                          try
                          {
                            if (this.val$result.thumb != null)
                            {
                              localObject8 = new java/io/File;
                              localObject4 = FileLoader.getDirectory(4);
                              localObject5 = new java/lang/StringBuilder;
                              ((StringBuilder)localObject5).<init>();
                              ((File)localObject8).<init>((File)localObject4, Utilities.MD5(this.val$result.thumb.url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb.url, "webp"));
                              localObject8 = ImageLoader.loadBitmap(((File)localObject8).getAbsolutePath(), null, 90.0F, 90.0F, true);
                              if (localObject8 != null)
                              {
                                localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject8, 90.0F, 90.0F, 55, false);
                                ((Bitmap)localObject8).recycle();
                              }
                            }
                          }
                          catch (Throwable localThrowable3)
                          {
                            FileLog.e(localThrowable3);
                          }
                        }
                      }
                      localObject2 = localObject1;
                      if (localThrowable3.exists()) {
                        localObject2 = SendMessagesHelper.getInstance(paramHashMap).generatePhotoSizes(str, null);
                      }
                      localObject3 = localTL_document;
                      localObject4 = str;
                      localObject5 = localTL_game;
                      Object localObject9 = localObject2;
                      if (localObject2 == null)
                      {
                        localObject9 = new TLRPC.TL_photo();
                        ((TLRPC.TL_photo)localObject9).date = ConnectionsManager.getInstance(paramHashMap).getCurrentTime();
                        localObject3 = new TLRPC.TL_photoSize();
                        localObject2 = MessageObject.getInlineResultWidthAndHeight(this.val$result);
                        ((TLRPC.TL_photoSize)localObject3).w = localObject2[0];
                        ((TLRPC.TL_photoSize)localObject3).h = localObject2[1];
                        ((TLRPC.TL_photoSize)localObject3).size = 1;
                        ((TLRPC.TL_photoSize)localObject3).location = new TLRPC.TL_fileLocationUnavailable();
                        ((TLRPC.TL_photoSize)localObject3).type = "x";
                        ((TLRPC.TL_photo)localObject9).sizes.add(localObject3);
                        localObject3 = localTL_document;
                        localObject4 = str;
                        localObject5 = localTL_game;
                      }
                    }
                  }
                }
              }
            }
          }
        }).run();
      }
      else
      {
        Object localObject1;
        if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageText))
        {
          String str = null;
          localObject1 = str;
          int j;
          if ((int)paramLong == 0)
          {
            j = 0;
            label72:
            localObject1 = str;
            if (j < paramBotInlineResult.send_message.entities.size())
            {
              localObject2 = (TLRPC.MessageEntity)paramBotInlineResult.send_message.entities.get(j);
              if (!(localObject2 instanceof TLRPC.TL_messageEntityUrl)) {
                break label217;
              }
              localObject1 = new TLRPC.TL_webPagePending();
              ((TLRPC.WebPage)localObject1).url = paramBotInlineResult.send_message.message.substring(((TLRPC.MessageEntity)localObject2).offset, ((TLRPC.MessageEntity)localObject2).offset + ((TLRPC.MessageEntity)localObject2).length);
            }
          }
          Object localObject2 = getInstance(i);
          str = paramBotInlineResult.send_message.message;
          if (!paramBotInlineResult.send_message.no_webpage) {}
          for (boolean bool = true;; bool = false)
          {
            ((SendMessagesHelper)localObject2).sendMessage(str, paramLong, paramMessageObject, (TLRPC.WebPage)localObject1, bool, paramBotInlineResult.send_message.entities, paramBotInlineResult.send_message.reply_markup, paramHashMap);
            break;
            label217:
            j++;
            break label72;
          }
        }
        if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))
        {
          localObject1 = new TLRPC.TL_messageMediaVenue();
          ((TLRPC.TL_messageMediaVenue)localObject1).geo = paramBotInlineResult.send_message.geo;
          ((TLRPC.TL_messageMediaVenue)localObject1).address = paramBotInlineResult.send_message.address;
          ((TLRPC.TL_messageMediaVenue)localObject1).title = paramBotInlineResult.send_message.title;
          ((TLRPC.TL_messageMediaVenue)localObject1).provider = paramBotInlineResult.send_message.provider;
          ((TLRPC.TL_messageMediaVenue)localObject1).venue_id = paramBotInlineResult.send_message.venue_id;
          ((TLRPC.TL_messageMediaVenue)localObject1).venue_type = "";
          getInstance(i).sendMessage((TLRPC.MessageMedia)localObject1, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
        }
        else if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo))
        {
          if (paramBotInlineResult.send_message.period != 0)
          {
            localObject1 = new TLRPC.TL_messageMediaGeoLive();
            ((TLRPC.TL_messageMediaGeoLive)localObject1).period = paramBotInlineResult.send_message.period;
            ((TLRPC.TL_messageMediaGeoLive)localObject1).geo = paramBotInlineResult.send_message.geo;
            getInstance(i).sendMessage((TLRPC.MessageMedia)localObject1, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
          }
          else
          {
            localObject1 = new TLRPC.TL_messageMediaGeo();
            ((TLRPC.TL_messageMediaGeo)localObject1).geo = paramBotInlineResult.send_message.geo;
            getInstance(i).sendMessage((TLRPC.MessageMedia)localObject1, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
          }
        }
        else if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaContact))
        {
          localObject1 = new TLRPC.TL_user();
          ((TLRPC.User)localObject1).phone = paramBotInlineResult.send_message.phone_number;
          ((TLRPC.User)localObject1).first_name = paramBotInlineResult.send_message.first_name;
          ((TLRPC.User)localObject1).last_name = paramBotInlineResult.send_message.last_name;
          getInstance(i).sendMessage((TLRPC.User)localObject1, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
        }
      }
    }
  }
  
  public static void prepareSendingDocument(String paramString1, String paramString2, Uri paramUri, String paramString3, long paramLong, MessageObject paramMessageObject, InputContentInfoCompat paramInputContentInfoCompat)
  {
    if (((paramString1 == null) || (paramString2 == null)) && (paramUri == null)) {}
    for (;;)
    {
      return;
      ArrayList localArrayList1 = new ArrayList();
      ArrayList localArrayList2 = new ArrayList();
      ArrayList localArrayList3 = null;
      if (paramUri != null)
      {
        localArrayList3 = new ArrayList();
        localArrayList3.add(paramUri);
      }
      if (paramString1 != null)
      {
        localArrayList1.add(paramString1);
        localArrayList2.add(paramString2);
      }
      prepareSendingDocuments(localArrayList1, localArrayList2, localArrayList3, paramString3, paramLong, paramMessageObject, paramInputContentInfoCompat);
    }
  }
  
  /* Error */
  private static boolean prepareSendingDocumentInternal(int paramInt, final String paramString1, final String paramString2, Uri paramUri, final String paramString3, final long paramLong, MessageObject paramMessageObject, CharSequence paramCharSequence, final ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +10 -> 11
    //   4: aload_1
    //   5: invokevirtual 782	java/lang/String:length	()I
    //   8: ifne +13 -> 21
    //   11: aload_3
    //   12: ifnonnull +9 -> 21
    //   15: iconst_0
    //   16: istore 10
    //   18: iload 10
    //   20: ireturn
    //   21: aload_3
    //   22: ifnull +16 -> 38
    //   25: aload_3
    //   26: invokestatic 1227	org/telegram/messenger/AndroidUtilities:isInternalUri	(Landroid/net/Uri;)Z
    //   29: ifeq +9 -> 38
    //   32: iconst_0
    //   33: istore 10
    //   35: goto -17 -> 18
    //   38: aload_1
    //   39: ifnull +26 -> 65
    //   42: new 601	java/io/File
    //   45: dup
    //   46: aload_1
    //   47: invokespecial 602	java/io/File:<init>	(Ljava/lang/String;)V
    //   50: invokestatic 704	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   53: invokestatic 1227	org/telegram/messenger/AndroidUtilities:isInternalUri	(Landroid/net/Uri;)Z
    //   56: ifeq +9 -> 65
    //   59: iconst_0
    //   60: istore 10
    //   62: goto -44 -> 18
    //   65: invokestatic 1233	android/webkit/MimeTypeMap:getSingleton	()Landroid/webkit/MimeTypeMap;
    //   68: astore 11
    //   70: aconst_null
    //   71: astore 12
    //   73: aconst_null
    //   74: astore 13
    //   76: aconst_null
    //   77: astore 14
    //   79: aload_1
    //   80: astore 15
    //   82: aload_3
    //   83: ifnull +69 -> 152
    //   86: iconst_0
    //   87: istore 16
    //   89: aload 14
    //   91: astore_1
    //   92: aload 4
    //   94: ifnull +11 -> 105
    //   97: aload 11
    //   99: aload 4
    //   101: invokevirtual 1236	android/webkit/MimeTypeMap:getExtensionFromMimeType	(Ljava/lang/String;)Ljava/lang/String;
    //   104: astore_1
    //   105: aload_1
    //   106: ifnonnull +23 -> 129
    //   109: ldc_w 1238
    //   112: astore_1
    //   113: aload_3
    //   114: aload_1
    //   115: invokestatic 1242	org/telegram/messenger/MediaController:copyFileToCache	(Landroid/net/Uri;Ljava/lang/String;)Ljava/lang/String;
    //   118: astore_3
    //   119: aload_3
    //   120: ifnonnull +15 -> 135
    //   123: iconst_0
    //   124: istore 10
    //   126: goto -108 -> 18
    //   129: iconst_1
    //   130: istore 16
    //   132: goto -19 -> 113
    //   135: aload_1
    //   136: astore 13
    //   138: aload_3
    //   139: astore 15
    //   141: iload 16
    //   143: ifne +9 -> 152
    //   146: aconst_null
    //   147: astore 13
    //   149: aload_3
    //   150: astore 15
    //   152: new 601	java/io/File
    //   155: dup
    //   156: aload 15
    //   158: invokespecial 602	java/io/File:<init>	(Ljava/lang/String;)V
    //   161: astore 17
    //   163: aload 17
    //   165: invokevirtual 849	java/io/File:exists	()Z
    //   168: ifeq +13 -> 181
    //   171: aload 17
    //   173: invokevirtual 605	java/io/File:length	()J
    //   176: lconst_0
    //   177: lcmp
    //   178: ifne +9 -> 187
    //   181: iconst_0
    //   182: istore 10
    //   184: goto -166 -> 18
    //   187: lload 5
    //   189: l2i
    //   190: ifne +981 -> 1171
    //   193: iconst_1
    //   194: istore 10
    //   196: iload 10
    //   198: ifne +979 -> 1177
    //   201: iconst_1
    //   202: istore 18
    //   204: aload 17
    //   206: invokevirtual 1243	java/io/File:getName	()Ljava/lang/String;
    //   209: astore 19
    //   211: ldc_w 1168
    //   214: astore 14
    //   216: aload 13
    //   218: ifnull +965 -> 1183
    //   221: aload 13
    //   223: astore 14
    //   225: aload 14
    //   227: invokevirtual 1246	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   230: astore 20
    //   232: aconst_null
    //   233: astore 13
    //   235: aconst_null
    //   236: astore 21
    //   238: aconst_null
    //   239: astore 22
    //   241: aconst_null
    //   242: astore 23
    //   244: aconst_null
    //   245: astore 4
    //   247: aconst_null
    //   248: astore 24
    //   250: aconst_null
    //   251: astore 25
    //   253: aconst_null
    //   254: astore 26
    //   256: iconst_0
    //   257: istore 27
    //   259: iconst_0
    //   260: istore 28
    //   262: iconst_0
    //   263: istore 16
    //   265: iconst_0
    //   266: istore 29
    //   268: iconst_0
    //   269: istore 30
    //   271: iconst_0
    //   272: istore 31
    //   274: aload 20
    //   276: ldc_w 1248
    //   279: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   282: ifne +14 -> 296
    //   285: aload 20
    //   287: ldc_w 1250
    //   290: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   293: ifeq +919 -> 1212
    //   296: aload 17
    //   298: invokestatic 1256	org/telegram/messenger/audioinfo/AudioInfo:getAudioInfo	(Ljava/io/File;)Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   301: astore 4
    //   303: iload 31
    //   305: istore 32
    //   307: iload 28
    //   309: istore 33
    //   311: aload 23
    //   313: astore_3
    //   314: aload 26
    //   316: astore_1
    //   317: aload 4
    //   319: ifnull +47 -> 366
    //   322: iload 31
    //   324: istore 32
    //   326: iload 28
    //   328: istore 33
    //   330: aload 23
    //   332: astore_3
    //   333: aload 26
    //   335: astore_1
    //   336: aload 4
    //   338: invokevirtual 1257	org/telegram/messenger/audioinfo/AudioInfo:getDuration	()J
    //   341: lconst_0
    //   342: lcmp
    //   343: ifeq +23 -> 366
    //   346: aload 4
    //   348: invokevirtual 1260	org/telegram/messenger/audioinfo/AudioInfo:getArtist	()Ljava/lang/String;
    //   351: astore_3
    //   352: aload 4
    //   354: invokevirtual 1263	org/telegram/messenger/audioinfo/AudioInfo:getTitle	()Ljava/lang/String;
    //   357: astore_1
    //   358: iload 28
    //   360: istore 33
    //   362: iload 31
    //   364: istore 32
    //   366: aload 12
    //   368: astore 4
    //   370: iload 32
    //   372: ifeq +106 -> 478
    //   375: new 1265	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio
    //   378: dup
    //   379: invokespecial 1266	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:<init>	()V
    //   382: astore 13
    //   384: aload 13
    //   386: iload 32
    //   388: putfield 1267	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:duration	I
    //   391: aload 13
    //   393: aload_1
    //   394: putfield 1268	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:title	Ljava/lang/String;
    //   397: aload 13
    //   399: aload_3
    //   400: putfield 1271	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:performer	Ljava/lang/String;
    //   403: aload 13
    //   405: getfield 1268	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:title	Ljava/lang/String;
    //   408: ifnonnull +11 -> 419
    //   411: aload 13
    //   413: ldc_w 1168
    //   416: putfield 1268	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:title	Ljava/lang/String;
    //   419: aload 13
    //   421: aload 13
    //   423: getfield 1274	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:flags	I
    //   426: iconst_1
    //   427: ior
    //   428: putfield 1274	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:flags	I
    //   431: aload 13
    //   433: getfield 1271	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:performer	Ljava/lang/String;
    //   436: ifnonnull +11 -> 447
    //   439: aload 13
    //   441: ldc_w 1168
    //   444: putfield 1271	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:performer	Ljava/lang/String;
    //   447: aload 13
    //   449: aload 13
    //   451: getfield 1274	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:flags	I
    //   454: iconst_2
    //   455: ior
    //   456: putfield 1274	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:flags	I
    //   459: aload 13
    //   461: astore 4
    //   463: iload 33
    //   465: ifeq +13 -> 478
    //   468: aload 13
    //   470: iconst_1
    //   471: putfield 1277	org/telegram/tgnet/TLRPC$TL_documentAttributeAudio:voice	Z
    //   474: aload 13
    //   476: astore 4
    //   478: iconst_0
    //   479: istore 32
    //   481: iload 32
    //   483: istore 16
    //   485: aload_2
    //   486: astore_3
    //   487: aload_2
    //   488: ifnull +18 -> 506
    //   491: aload_2
    //   492: ldc_w 1279
    //   495: invokevirtual 794	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   498: ifeq +1125 -> 1623
    //   501: iconst_1
    //   502: istore 16
    //   504: aload_2
    //   505: astore_3
    //   506: aconst_null
    //   507: astore_2
    //   508: aload_2
    //   509: astore_1
    //   510: iload 16
    //   512: ifne +106 -> 618
    //   515: aload_2
    //   516: astore_1
    //   517: iload 10
    //   519: ifne +99 -> 618
    //   522: iload_0
    //   523: invokestatic 1284	org/telegram/messenger/MessagesStorage:getInstance	(I)Lorg/telegram/messenger/MessagesStorage;
    //   526: astore_1
    //   527: iload 10
    //   529: ifne +1171 -> 1700
    //   532: iconst_1
    //   533: istore 16
    //   535: aload_1
    //   536: aload_3
    //   537: iload 16
    //   539: invokevirtual 1288	org/telegram/messenger/MessagesStorage:getSentFile	(Ljava/lang/String;I)Lorg/telegram/tgnet/TLObject;
    //   542: checkcast 1290	org/telegram/tgnet/TLRPC$TL_document
    //   545: astore_2
    //   546: aload_2
    //   547: astore_1
    //   548: aload_2
    //   549: ifnonnull +69 -> 618
    //   552: aload_2
    //   553: astore_1
    //   554: aload 15
    //   556: aload_3
    //   557: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   560: ifne +58 -> 618
    //   563: aload_2
    //   564: astore_1
    //   565: iload 10
    //   567: ifne +51 -> 618
    //   570: iload_0
    //   571: invokestatic 1284	org/telegram/messenger/MessagesStorage:getInstance	(I)Lorg/telegram/messenger/MessagesStorage;
    //   574: astore_2
    //   575: new 507	java/lang/StringBuilder
    //   578: dup
    //   579: invokespecial 508	java/lang/StringBuilder:<init>	()V
    //   582: aload 15
    //   584: invokevirtual 514	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   587: aload 17
    //   589: invokevirtual 605	java/io/File:length	()J
    //   592: invokevirtual 899	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   595: invokevirtual 517	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   598: astore_1
    //   599: iload 10
    //   601: ifne +1105 -> 1706
    //   604: iconst_1
    //   605: istore 16
    //   607: aload_2
    //   608: aload_1
    //   609: iload 16
    //   611: invokevirtual 1288	org/telegram/messenger/MessagesStorage:getSentFile	(Ljava/lang/String;I)Lorg/telegram/tgnet/TLObject;
    //   614: checkcast 1290	org/telegram/tgnet/TLRPC$TL_document
    //   617: astore_1
    //   618: aload_1
    //   619: astore_2
    //   620: aload_1
    //   621: ifnonnull +485 -> 1106
    //   624: new 1290	org/telegram/tgnet/TLRPC$TL_document
    //   627: dup
    //   628: invokespecial 1291	org/telegram/tgnet/TLRPC$TL_document:<init>	()V
    //   631: astore_1
    //   632: aload_1
    //   633: lconst_0
    //   634: putfield 1292	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   637: aload_1
    //   638: iload_0
    //   639: invokestatic 1047	org/telegram/tgnet/ConnectionsManager:getInstance	(I)Lorg/telegram/tgnet/ConnectionsManager;
    //   642: invokevirtual 1295	org/telegram/tgnet/ConnectionsManager:getCurrentTime	()I
    //   645: putfield 1298	org/telegram/tgnet/TLRPC$TL_document:date	I
    //   648: new 1300	org/telegram/tgnet/TLRPC$TL_documentAttributeFilename
    //   651: dup
    //   652: invokespecial 1301	org/telegram/tgnet/TLRPC$TL_documentAttributeFilename:<init>	()V
    //   655: astore_2
    //   656: aload_2
    //   657: aload 19
    //   659: putfield 1304	org/telegram/tgnet/TLRPC$TL_documentAttributeFilename:file_name	Ljava/lang/String;
    //   662: aload_1
    //   663: getfield 1307	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   666: aload_2
    //   667: invokevirtual 1219	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   670: pop
    //   671: aload_1
    //   672: aload 17
    //   674: invokevirtual 605	java/io/File:length	()J
    //   677: l2i
    //   678: putfield 1308	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   681: aload_1
    //   682: iconst_0
    //   683: putfield 1309	org/telegram/tgnet/TLRPC$TL_document:dc_id	I
    //   686: aload 4
    //   688: ifnull +13 -> 701
    //   691: aload_1
    //   692: getfield 1307	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   695: aload 4
    //   697: invokevirtual 1219	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   700: pop
    //   701: aload 14
    //   703: invokevirtual 782	java/lang/String:length	()I
    //   706: ifeq +1124 -> 1830
    //   709: iconst_m1
    //   710: istore 16
    //   712: aload 20
    //   714: invokevirtual 1312	java/lang/String:hashCode	()I
    //   717: lookupswitch	default:+43->760, 109967:+1029->1746, 3145576:+1046->1763, 3418175:+1012->1729, 3645340:+995->1712
    //   760: iload 16
    //   762: tableswitch	default:+30->792, 0:+1018->1780, 1:+1028->1790, 2:+1038->1800, 3:+1048->1810
    //   792: aload 11
    //   794: aload 20
    //   796: invokevirtual 1315	android/webkit/MimeTypeMap:getMimeTypeFromExtension	(Ljava/lang/String;)Ljava/lang/String;
    //   799: astore 4
    //   801: aload 4
    //   803: ifnull +1017 -> 1820
    //   806: aload_1
    //   807: aload 4
    //   809: putfield 1318	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   812: aload_1
    //   813: getfield 1318	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   816: ldc_w 1320
    //   819: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   822: ifeq +57 -> 879
    //   825: aload 17
    //   827: invokevirtual 1323	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   830: aconst_null
    //   831: ldc_w 648
    //   834: ldc_w 648
    //   837: iconst_1
    //   838: invokestatic 1327	org/telegram/messenger/ImageLoader:loadBitmap	(Ljava/lang/String;Landroid/net/Uri;FFZ)Landroid/graphics/Bitmap;
    //   841: astore 4
    //   843: aload 4
    //   845: ifnull +34 -> 879
    //   848: aload_2
    //   849: ldc_w 1329
    //   852: putfield 1304	org/telegram/tgnet/TLRPC$TL_documentAttributeFilename:file_name	Ljava/lang/String;
    //   855: aload_1
    //   856: aload 4
    //   858: ldc_w 648
    //   861: ldc_w 648
    //   864: bipush 55
    //   866: iload 10
    //   868: invokestatic 1333	org/telegram/messenger/ImageLoader:scaleAndSaveImage	(Landroid/graphics/Bitmap;FFIZ)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   871: putfield 1336	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   874: aload 4
    //   876: invokevirtual 657	android/graphics/Bitmap:recycle	()V
    //   879: aload_1
    //   880: getfield 1318	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   883: ldc_w 1338
    //   886: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   889: ifeq +185 -> 1074
    //   892: iload 18
    //   894: ifeq +180 -> 1074
    //   897: new 1340	android/graphics/BitmapFactory$Options
    //   900: dup
    //   901: invokespecial 1341	android/graphics/BitmapFactory$Options:<init>	()V
    //   904: astore_2
    //   905: aload_2
    //   906: iconst_1
    //   907: putfield 1344	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   910: new 1346	java/io/RandomAccessFile
    //   913: astore 13
    //   915: aload 13
    //   917: aload 15
    //   919: ldc_w 1348
    //   922: invokespecial 1351	java/io/RandomAccessFile:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   925: aload 13
    //   927: invokevirtual 1355	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   930: getstatic 1361	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   933: lconst_0
    //   934: aload 15
    //   936: invokevirtual 782	java/lang/String:length	()I
    //   939: i2l
    //   940: invokevirtual 1367	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   943: astore 4
    //   945: aconst_null
    //   946: aload 4
    //   948: aload 4
    //   950: invokevirtual 1372	java/nio/ByteBuffer:limit	()I
    //   953: aload_2
    //   954: iconst_1
    //   955: invokestatic 1376	org/telegram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
    //   958: pop
    //   959: aload 13
    //   961: invokevirtual 1379	java/io/RandomAccessFile:close	()V
    //   964: aload_2
    //   965: getfield 1382	android/graphics/BitmapFactory$Options:outWidth	I
    //   968: ifeq +106 -> 1074
    //   971: aload_2
    //   972: getfield 1385	android/graphics/BitmapFactory$Options:outHeight	I
    //   975: ifeq +99 -> 1074
    //   978: aload_2
    //   979: getfield 1382	android/graphics/BitmapFactory$Options:outWidth	I
    //   982: sipush 800
    //   985: if_icmpgt +89 -> 1074
    //   988: aload_2
    //   989: getfield 1385	android/graphics/BitmapFactory$Options:outHeight	I
    //   992: sipush 800
    //   995: if_icmpgt +79 -> 1074
    //   998: new 1387	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker
    //   1001: dup
    //   1002: invokespecial 1388	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker:<init>	()V
    //   1005: astore 4
    //   1007: aload 4
    //   1009: ldc_w 1168
    //   1012: putfield 1391	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker:alt	Ljava/lang/String;
    //   1015: aload 4
    //   1017: new 1393	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty
    //   1020: dup
    //   1021: invokespecial 1394	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty:<init>	()V
    //   1024: putfield 1398	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   1027: aload_1
    //   1028: getfield 1307	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   1031: aload 4
    //   1033: invokevirtual 1219	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1036: pop
    //   1037: new 1400	org/telegram/tgnet/TLRPC$TL_documentAttributeImageSize
    //   1040: dup
    //   1041: invokespecial 1401	org/telegram/tgnet/TLRPC$TL_documentAttributeImageSize:<init>	()V
    //   1044: astore 4
    //   1046: aload 4
    //   1048: aload_2
    //   1049: getfield 1382	android/graphics/BitmapFactory$Options:outWidth	I
    //   1052: putfield 1402	org/telegram/tgnet/TLRPC$TL_documentAttributeImageSize:w	I
    //   1055: aload 4
    //   1057: aload_2
    //   1058: getfield 1385	android/graphics/BitmapFactory$Options:outHeight	I
    //   1061: putfield 1403	org/telegram/tgnet/TLRPC$TL_documentAttributeImageSize:h	I
    //   1064: aload_1
    //   1065: getfield 1307	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   1068: aload 4
    //   1070: invokevirtual 1219	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1073: pop
    //   1074: aload_1
    //   1075: astore_2
    //   1076: aload_1
    //   1077: getfield 1336	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   1080: ifnonnull +26 -> 1106
    //   1083: aload_1
    //   1084: new 1405	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty
    //   1087: dup
    //   1088: invokespecial 1406	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty:<init>	()V
    //   1091: putfield 1336	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   1094: aload_1
    //   1095: getfield 1336	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   1098: ldc_w 1408
    //   1101: putfield 1412	org/telegram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
    //   1104: aload_1
    //   1105: astore_2
    //   1106: aload 8
    //   1108: ifnull +750 -> 1858
    //   1111: aload 8
    //   1113: invokeinterface 1415 1 0
    //   1118: astore_1
    //   1119: new 229	java/util/HashMap
    //   1122: dup
    //   1123: invokespecial 230	java/util/HashMap:<init>	()V
    //   1126: astore 4
    //   1128: aload_3
    //   1129: ifnull +13 -> 1142
    //   1132: aload 4
    //   1134: ldc_w 1416
    //   1137: aload_3
    //   1138: invokevirtual 999	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1141: pop
    //   1142: new 54	org/telegram/messenger/SendMessagesHelper$16
    //   1145: dup
    //   1146: iload_0
    //   1147: aload_2
    //   1148: aload 15
    //   1150: lload 5
    //   1152: aload 7
    //   1154: aload_1
    //   1155: aload 9
    //   1157: aload 4
    //   1159: invokespecial 1419	org/telegram/messenger/SendMessagesHelper$16:<init>	(ILorg/telegram/tgnet/TLRPC$TL_document;Ljava/lang/String;JLorg/telegram/messenger/MessageObject;Ljava/lang/String;Ljava/util/ArrayList;Ljava/util/HashMap;)V
    //   1162: invokestatic 260	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
    //   1165: iconst_1
    //   1166: istore 10
    //   1168: goto -1150 -> 18
    //   1171: iconst_0
    //   1172: istore 10
    //   1174: goto -978 -> 196
    //   1177: iconst_0
    //   1178: istore 18
    //   1180: goto -976 -> 204
    //   1183: aload 15
    //   1185: bipush 46
    //   1187: invokevirtual 1423	java/lang/String:lastIndexOf	(I)I
    //   1190: istore 16
    //   1192: iload 16
    //   1194: iconst_m1
    //   1195: if_icmpeq -970 -> 225
    //   1198: aload 15
    //   1200: iload 16
    //   1202: iconst_1
    //   1203: iadd
    //   1204: invokevirtual 791	java/lang/String:substring	(I)Ljava/lang/String;
    //   1207: astore 14
    //   1209: goto -984 -> 225
    //   1212: aload 20
    //   1214: ldc_w 1425
    //   1217: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1220: ifne +39 -> 1259
    //   1223: aload 20
    //   1225: ldc_w 1427
    //   1228: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1231: ifne +28 -> 1259
    //   1234: iload 31
    //   1236: istore 32
    //   1238: iload 28
    //   1240: istore 33
    //   1242: aload 23
    //   1244: astore_3
    //   1245: aload 26
    //   1247: astore_1
    //   1248: aload 20
    //   1250: ldc_w 1429
    //   1253: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1256: ifeq -890 -> 366
    //   1259: aconst_null
    //   1260: astore 26
    //   1262: aconst_null
    //   1263: astore_3
    //   1264: aload 26
    //   1266: astore_1
    //   1267: new 627	android/media/MediaMetadataRetriever
    //   1270: astore 23
    //   1272: aload 26
    //   1274: astore_1
    //   1275: aload 23
    //   1277: invokespecial 628	android/media/MediaMetadataRetriever:<init>	()V
    //   1280: iload 30
    //   1282: istore 32
    //   1284: aload 22
    //   1286: astore_3
    //   1287: aload 25
    //   1289: astore_1
    //   1290: aload 23
    //   1292: aload 17
    //   1294: invokevirtual 1323	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   1297: invokevirtual 631	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   1300: iload 30
    //   1302: istore 32
    //   1304: aload 22
    //   1306: astore_3
    //   1307: aload 25
    //   1309: astore_1
    //   1310: aload 23
    //   1312: bipush 9
    //   1314: invokevirtual 661	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   1317: astore 21
    //   1319: aload 21
    //   1321: ifnull +67 -> 1388
    //   1324: iload 30
    //   1326: istore 32
    //   1328: aload 22
    //   1330: astore_3
    //   1331: aload 25
    //   1333: astore_1
    //   1334: aload 21
    //   1336: invokestatic 681	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   1339: l2f
    //   1340: ldc_w 524
    //   1343: fdiv
    //   1344: f2d
    //   1345: invokestatic 550	java/lang/Math:ceil	(D)D
    //   1348: d2i
    //   1349: istore 16
    //   1351: iload 16
    //   1353: istore 32
    //   1355: aload 22
    //   1357: astore_3
    //   1358: aload 25
    //   1360: astore_1
    //   1361: aload 23
    //   1363: bipush 7
    //   1365: invokevirtual 661	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   1368: astore 4
    //   1370: iload 16
    //   1372: istore 32
    //   1374: aload 22
    //   1376: astore_3
    //   1377: aload 4
    //   1379: astore_1
    //   1380: aload 23
    //   1382: iconst_2
    //   1383: invokevirtual 661	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   1386: astore 13
    //   1388: iload 27
    //   1390: istore 30
    //   1392: iload 16
    //   1394: istore 32
    //   1396: aload 13
    //   1398: astore_3
    //   1399: aload 4
    //   1401: astore_1
    //   1402: aload 20
    //   1404: ldc_w 1427
    //   1407: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1410: ifeq +36 -> 1446
    //   1413: iload 16
    //   1415: istore 32
    //   1417: aload 13
    //   1419: astore_3
    //   1420: aload 4
    //   1422: astore_1
    //   1423: aload 17
    //   1425: invokevirtual 1323	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   1428: invokestatic 1432	org/telegram/messenger/MediaController:isOpusFile	(Ljava/lang/String;)I
    //   1431: istore 33
    //   1433: iload 27
    //   1435: istore 30
    //   1437: iload 33
    //   1439: iconst_1
    //   1440: if_icmpne +6 -> 1446
    //   1443: iconst_1
    //   1444: istore 30
    //   1446: iload 16
    //   1448: istore 32
    //   1450: iload 30
    //   1452: istore 33
    //   1454: aload 13
    //   1456: astore_3
    //   1457: aload 4
    //   1459: astore_1
    //   1460: aload 23
    //   1462: ifnull -1096 -> 366
    //   1465: aload 23
    //   1467: invokevirtual 638	android/media/MediaMetadataRetriever:release	()V
    //   1470: iload 16
    //   1472: istore 32
    //   1474: iload 30
    //   1476: istore 33
    //   1478: aload 13
    //   1480: astore_3
    //   1481: aload 4
    //   1483: astore_1
    //   1484: goto -1118 -> 366
    //   1487: astore_1
    //   1488: aload_1
    //   1489: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1492: iload 16
    //   1494: istore 32
    //   1496: iload 30
    //   1498: istore 33
    //   1500: aload 13
    //   1502: astore_3
    //   1503: aload 4
    //   1505: astore_1
    //   1506: goto -1140 -> 366
    //   1509: astore 22
    //   1511: aload 24
    //   1513: astore 13
    //   1515: aload 21
    //   1517: astore 4
    //   1519: aload_3
    //   1520: astore 23
    //   1522: iload 29
    //   1524: istore 16
    //   1526: aload 23
    //   1528: astore_1
    //   1529: aload 22
    //   1531: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1534: iload 16
    //   1536: istore 32
    //   1538: iload 28
    //   1540: istore 33
    //   1542: aload 4
    //   1544: astore_3
    //   1545: aload 13
    //   1547: astore_1
    //   1548: aload 23
    //   1550: ifnull -1184 -> 366
    //   1553: aload 23
    //   1555: invokevirtual 638	android/media/MediaMetadataRetriever:release	()V
    //   1558: iload 16
    //   1560: istore 32
    //   1562: iload 28
    //   1564: istore 33
    //   1566: aload 4
    //   1568: astore_3
    //   1569: aload 13
    //   1571: astore_1
    //   1572: goto -1206 -> 366
    //   1575: astore_1
    //   1576: aload_1
    //   1577: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1580: iload 16
    //   1582: istore 32
    //   1584: iload 28
    //   1586: istore 33
    //   1588: aload 4
    //   1590: astore_3
    //   1591: aload 13
    //   1593: astore_1
    //   1594: goto -1228 -> 366
    //   1597: astore_2
    //   1598: aload_1
    //   1599: astore 23
    //   1601: aload_2
    //   1602: astore_1
    //   1603: aload 23
    //   1605: ifnull +8 -> 1613
    //   1608: aload 23
    //   1610: invokevirtual 638	android/media/MediaMetadataRetriever:release	()V
    //   1613: aload_1
    //   1614: athrow
    //   1615: astore_2
    //   1616: aload_2
    //   1617: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1620: goto -7 -> 1613
    //   1623: aload 4
    //   1625: ifnull +39 -> 1664
    //   1628: new 507	java/lang/StringBuilder
    //   1631: dup
    //   1632: invokespecial 508	java/lang/StringBuilder:<init>	()V
    //   1635: aload_2
    //   1636: invokevirtual 514	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1639: ldc_w 1434
    //   1642: invokevirtual 514	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1645: aload 17
    //   1647: invokevirtual 605	java/io/File:length	()J
    //   1650: invokevirtual 899	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1653: invokevirtual 517	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1656: astore_3
    //   1657: iload 32
    //   1659: istore 16
    //   1661: goto -1155 -> 506
    //   1664: new 507	java/lang/StringBuilder
    //   1667: dup
    //   1668: invokespecial 508	java/lang/StringBuilder:<init>	()V
    //   1671: aload_2
    //   1672: invokevirtual 514	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1675: ldc_w 1168
    //   1678: invokevirtual 514	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1681: aload 17
    //   1683: invokevirtual 605	java/io/File:length	()J
    //   1686: invokevirtual 899	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1689: invokevirtual 517	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1692: astore_3
    //   1693: iload 32
    //   1695: istore 16
    //   1697: goto -1191 -> 506
    //   1700: iconst_4
    //   1701: istore 16
    //   1703: goto -1168 -> 535
    //   1706: iconst_4
    //   1707: istore 16
    //   1709: goto -1102 -> 607
    //   1712: aload 20
    //   1714: ldc_w 1436
    //   1717: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1720: ifeq -960 -> 760
    //   1723: iconst_0
    //   1724: istore 16
    //   1726: goto -966 -> 760
    //   1729: aload 20
    //   1731: ldc_w 1425
    //   1734: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1737: ifeq -977 -> 760
    //   1740: iconst_1
    //   1741: istore 16
    //   1743: goto -983 -> 760
    //   1746: aload 20
    //   1748: ldc_w 1427
    //   1751: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1754: ifeq -994 -> 760
    //   1757: iconst_2
    //   1758: istore 16
    //   1760: goto -1000 -> 760
    //   1763: aload 20
    //   1765: ldc_w 1429
    //   1768: invokevirtual 493	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1771: ifeq -1011 -> 760
    //   1774: iconst_3
    //   1775: istore 16
    //   1777: goto -1017 -> 760
    //   1780: aload_1
    //   1781: ldc_w 1338
    //   1784: putfield 1318	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   1787: goto -975 -> 812
    //   1790: aload_1
    //   1791: ldc_w 1438
    //   1794: putfield 1318	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   1797: goto -985 -> 812
    //   1800: aload_1
    //   1801: ldc_w 1440
    //   1804: putfield 1318	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   1807: goto -995 -> 812
    //   1810: aload_1
    //   1811: ldc_w 1442
    //   1814: putfield 1318	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   1817: goto -1005 -> 812
    //   1820: aload_1
    //   1821: ldc_w 1444
    //   1824: putfield 1318	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   1827: goto -1015 -> 812
    //   1830: aload_1
    //   1831: ldc_w 1444
    //   1834: putfield 1318	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   1837: goto -1025 -> 812
    //   1840: astore_2
    //   1841: aload_2
    //   1842: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1845: goto -966 -> 879
    //   1848: astore 4
    //   1850: aload 4
    //   1852: invokestatic 467	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1855: goto -891 -> 964
    //   1858: ldc_w 1168
    //   1861: astore_1
    //   1862: goto -743 -> 1119
    //   1865: astore_1
    //   1866: goto -263 -> 1603
    //   1869: astore 22
    //   1871: iload 32
    //   1873: istore 16
    //   1875: aload_3
    //   1876: astore 4
    //   1878: aload_1
    //   1879: astore 13
    //   1881: goto -355 -> 1526
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1884	0	paramInt	int
    //   0	1884	1	paramString1	String
    //   0	1884	2	paramString2	String
    //   0	1884	3	paramUri	Uri
    //   0	1884	4	paramString3	String
    //   0	1884	5	paramLong	long
    //   0	1884	7	paramMessageObject	MessageObject
    //   0	1884	8	paramCharSequence	CharSequence
    //   0	1884	9	paramArrayList	ArrayList<TLRPC.MessageEntity>
    //   16	1157	10	bool	boolean
    //   68	725	11	localMimeTypeMap	android.webkit.MimeTypeMap
    //   71	296	12	localObject1	Object
    //   74	1806	13	localObject2	Object
    //   77	1131	14	localObject3	Object
    //   80	1119	15	localObject4	Object
    //   87	1787	16	i	int
    //   161	1521	17	localFile	File
    //   202	977	18	j	int
    //   209	449	19	str1	String
    //   230	1534	20	str2	String
    //   236	1280	21	str3	String
    //   239	1136	22	localObject5	Object
    //   1509	21	22	localException1	Exception
    //   1869	1	22	localException2	Exception
    //   242	1367	23	localObject6	Object
    //   248	1264	24	localObject7	Object
    //   251	1108	25	localObject8	Object
    //   254	1019	26	localObject9	Object
    //   257	1177	27	k	int
    //   260	1325	28	m	int
    //   266	1257	29	n	int
    //   269	1228	30	i1	int
    //   272	963	31	i2	int
    //   305	1567	32	i3	int
    //   309	1278	33	i4	int
    // Exception table:
    //   from	to	target	type
    //   1465	1470	1487	java/lang/Exception
    //   1267	1272	1509	java/lang/Exception
    //   1275	1280	1509	java/lang/Exception
    //   1553	1558	1575	java/lang/Exception
    //   1267	1272	1597	finally
    //   1275	1280	1597	finally
    //   1529	1534	1597	finally
    //   1608	1613	1615	java/lang/Exception
    //   825	843	1840	java/lang/Exception
    //   848	879	1840	java/lang/Exception
    //   905	964	1848	java/lang/Exception
    //   1290	1300	1865	finally
    //   1310	1319	1865	finally
    //   1334	1351	1865	finally
    //   1361	1370	1865	finally
    //   1380	1388	1865	finally
    //   1402	1413	1865	finally
    //   1423	1433	1865	finally
    //   1290	1300	1869	java/lang/Exception
    //   1310	1319	1869	java/lang/Exception
    //   1334	1351	1869	java/lang/Exception
    //   1361	1370	1869	java/lang/Exception
    //   1380	1388	1869	java/lang/Exception
    //   1402	1413	1869	java/lang/Exception
    //   1423	1433	1869	java/lang/Exception
  }
  
  public static void prepareSendingDocuments(ArrayList<String> paramArrayList1, final ArrayList<String> paramArrayList2, final ArrayList<Uri> paramArrayList, final String paramString, final long paramLong, MessageObject paramMessageObject, final InputContentInfoCompat paramInputContentInfoCompat)
  {
    if (((paramArrayList1 == null) && (paramArrayList2 == null) && (paramArrayList == null)) || ((paramArrayList1 != null) && (paramArrayList2 != null) && (paramArrayList1.size() != paramArrayList2.size()))) {}
    for (;;)
    {
      return;
      new Thread(new Runnable()
      {
        public void run()
        {
          int i = 0;
          int j = 0;
          if (this.val$paths != null) {
            for (k = 0;; k++)
            {
              i = j;
              if (k >= this.val$paths.size()) {
                break;
              }
              if (!SendMessagesHelper.prepareSendingDocumentInternal(this.val$currentAccount, (String)this.val$paths.get(k), (String)paramArrayList2.get(k), null, paramString, paramLong, paramArrayList, null, null)) {
                j = 1;
              }
            }
          }
          int k = i;
          if (paramInputContentInfoCompat != null) {
            for (j = 0;; j++)
            {
              k = i;
              if (j >= paramInputContentInfoCompat.size()) {
                break;
              }
              if (!SendMessagesHelper.prepareSendingDocumentInternal(this.val$currentAccount, null, null, (Uri)paramInputContentInfoCompat.get(j), paramString, paramLong, paramArrayList, null, null)) {
                i = 1;
              }
            }
          }
          if (this.val$inputContent != null) {
            this.val$inputContent.releasePermission();
          }
          if (k != 0) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                try
                {
                  Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", NUM), 0).show();
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
      }).start();
    }
  }
  
  public static void prepareSendingMedia(ArrayList<SendingMediaInfo> paramArrayList, final long paramLong, final MessageObject paramMessageObject, final InputContentInfoCompat paramInputContentInfoCompat, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    if (paramArrayList.isEmpty()) {}
    for (;;)
    {
      return;
      int i = UserConfig.selectedAccount;
      mediaSendQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          long l1 = System.currentTimeMillis();
          int i = this.val$media.size();
          boolean bool1;
          int j;
          int k;
          final Object localObject1;
          label119:
          final Object localObject4;
          if ((int)paramLong == 0)
          {
            bool1 = true;
            j = 0;
            k = j;
            if (bool1)
            {
              m = (int)(paramLong >> 32);
              localObject1 = MessagesController.getInstance(paramBoolean1).getEncryptedChat(Integer.valueOf(m));
              k = j;
              if (localObject1 != null) {
                k = AndroidUtilities.getPeerLayerVersion(((TLRPC.EncryptedChat)localObject1).layer);
              }
            }
            if (((bool1) && (k < 73)) || (paramBoolean2) || (!paramMessageObject)) {
              break label552;
            }
            localObject2 = new HashMap();
            j = 0;
            localObject3 = localObject2;
            if (j >= i) {
              break label555;
            }
            localObject4 = (SendMessagesHelper.SendingMediaInfo)this.val$media.get(j);
            if ((((SendMessagesHelper.SendingMediaInfo)localObject4).searchImage == null) && (!((SendMessagesHelper.SendingMediaInfo)localObject4).isVideo))
            {
              localObject7 = ((SendMessagesHelper.SendingMediaInfo)localObject4).path;
              localObject8 = ((SendMessagesHelper.SendingMediaInfo)localObject4).path;
              localObject1 = localObject7;
              localObject9 = localObject8;
              if (localObject8 == null)
              {
                localObject1 = localObject7;
                localObject9 = localObject8;
                if (((SendMessagesHelper.SendingMediaInfo)localObject4).uri != null)
                {
                  localObject9 = AndroidUtilities.getPath(((SendMessagesHelper.SendingMediaInfo)localObject4).uri);
                  localObject1 = ((SendMessagesHelper.SendingMediaInfo)localObject4).uri.toString();
                }
              }
              if ((localObject9 == null) || ((!((String)localObject9).endsWith(".gif")) && (!((String)localObject9).endsWith(".webp")))) {
                break label259;
              }
            }
          }
          for (;;)
          {
            j++;
            break label119;
            bool1 = false;
            break;
            label259:
            if ((localObject9 != null) || (((SendMessagesHelper.SendingMediaInfo)localObject4).uri == null) || ((!MediaController.isGif(((SendMessagesHelper.SendingMediaInfo)localObject4).uri)) && (!MediaController.isWebp(((SendMessagesHelper.SendingMediaInfo)localObject4).uri))))
            {
              if (localObject9 != null)
              {
                localObject9 = new File((String)localObject9);
                localObject9 = (String)localObject1 + ((File)localObject9).length() + "_" + ((File)localObject9).lastModified();
                label348:
                localObject7 = null;
                localObject1 = localObject7;
                if (!bool1)
                {
                  localObject1 = localObject7;
                  if (((SendMessagesHelper.SendingMediaInfo)localObject4).ttl == 0)
                  {
                    localObject1 = MessagesStorage.getInstance(paramBoolean1);
                    if (bool1) {
                      break label506;
                    }
                    m = 0;
                    label389:
                    localObject9 = (TLRPC.TL_photo)((MessagesStorage)localObject1).getSentFile((String)localObject9, m);
                    localObject1 = localObject9;
                    if (localObject9 == null)
                    {
                      localObject1 = localObject9;
                      if (((SendMessagesHelper.SendingMediaInfo)localObject4).uri != null)
                      {
                        localObject1 = MessagesStorage.getInstance(paramBoolean1);
                        localObject9 = AndroidUtilities.getPath(((SendMessagesHelper.SendingMediaInfo)localObject4).uri);
                        if (bool1) {
                          break label512;
                        }
                      }
                    }
                  }
                }
              }
              label506:
              label512:
              for (m = 0;; m = 3)
              {
                localObject1 = (TLRPC.TL_photo)((MessagesStorage)localObject1).getSentFile((String)localObject9, m);
                localObject9 = new SendMessagesHelper.MediaSendPrepareWorker(null);
                ((HashMap)localObject2).put(localObject4, localObject9);
                if (localObject1 == null) {
                  break label518;
                }
                ((SendMessagesHelper.MediaSendPrepareWorker)localObject9).photo = ((TLRPC.TL_photo)localObject1);
                break;
                localObject9 = null;
                break label348;
                m = 3;
                break label389;
              }
              label518:
              ((SendMessagesHelper.MediaSendPrepareWorker)localObject9).sync = new CountDownLatch(1);
              SendMessagesHelper.mediaSendThreadPool.execute(new Runnable()
              {
                public void run()
                {
                  localObject9.photo = SendMessagesHelper.getInstance(SendMessagesHelper.21.this.val$currentAccount).generatePhotoSizes(localObject4.path, localObject4.uri);
                  localObject9.sync.countDown();
                }
              });
            }
          }
          label552:
          Object localObject3 = null;
          label555:
          long l2 = 0L;
          final long l3 = 0L;
          Object localObject2 = null;
          final Object localObject9 = null;
          Object localObject8 = null;
          Object localObject7 = null;
          Object localObject10 = null;
          int m = 0;
          int n = 0;
          if (n < i)
          {
            final SendMessagesHelper.SendingMediaInfo localSendingMediaInfo = (SendMessagesHelper.SendingMediaInfo)this.val$media.get(n);
            long l4 = l2;
            long l5 = l3;
            j = m;
            if (paramMessageObject) {
              if (bool1)
              {
                l4 = l2;
                l5 = l3;
                j = m;
                if (k < 73) {}
              }
              else
              {
                l4 = l2;
                l5 = l3;
                j = m;
                if (i > 1)
                {
                  l4 = l2;
                  l5 = l3;
                  j = m;
                  if (m % 10 == 0)
                  {
                    l4 = Utilities.random.nextLong();
                    l5 = l4;
                    j = 0;
                  }
                }
              }
            }
            final Object localObject11;
            Object localObject12;
            final Object localObject13;
            final Object localObject14;
            if (localSendingMediaInfo.searchImage != null) {
              if (localSendingMediaInfo.searchImage.type == 1)
              {
                localObject11 = new HashMap();
                localObject1 = null;
                if ((localSendingMediaInfo.searchImage.document instanceof TLRPC.TL_document))
                {
                  localObject4 = (TLRPC.TL_document)localSendingMediaInfo.searchImage.document;
                  localObject1 = FileLoader.getPathToAttach((TLObject)localObject4, true);
                  localObject12 = localObject1;
                  localObject13 = localObject4;
                  if (localObject4 == null)
                  {
                    if (localSendingMediaInfo.searchImage.localUrl != null) {
                      ((HashMap)localObject11).put("url", localSendingMediaInfo.searchImage.localUrl);
                    }
                    localObject4 = null;
                    localObject14 = new TLRPC.TL_document();
                    ((TLRPC.TL_document)localObject14).id = 0L;
                    ((TLRPC.TL_document)localObject14).date = ConnectionsManager.getInstance(paramBoolean1).getCurrentTime();
                    localObject13 = new TLRPC.TL_documentAttributeFilename();
                    ((TLRPC.TL_documentAttributeFilename)localObject13).file_name = "animation.gif";
                    ((TLRPC.TL_document)localObject14).attributes.add(localObject13);
                    ((TLRPC.TL_document)localObject14).size = localSendingMediaInfo.searchImage.size;
                    ((TLRPC.TL_document)localObject14).dc_id = 0;
                    if (!((File)localObject1).toString().endsWith("mp4")) {
                      break label1463;
                    }
                    ((TLRPC.TL_document)localObject14).mime_type = "video/mp4";
                    ((TLRPC.TL_document)localObject14).attributes.add(new TLRPC.TL_documentAttributeAnimated());
                    label923:
                    if (!((File)localObject1).exists()) {
                      break label1474;
                    }
                    localObject4 = localObject1;
                    label935:
                    localObject13 = localObject4;
                    if (localObject4 == null)
                    {
                      localObject4 = Utilities.MD5(localSendingMediaInfo.searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(localSendingMediaInfo.searchImage.thumbUrl, "jpg");
                      localObject4 = new File(FileLoader.getDirectory(4), (String)localObject4);
                      localObject13 = localObject4;
                      if (!((File)localObject4).exists()) {
                        localObject13 = null;
                      }
                    }
                    if (localObject13 == null) {}
                  }
                }
                try
                {
                  if (!((File)localObject13).getAbsolutePath().endsWith("mp4")) {
                    break label1480;
                  }
                  localObject4 = ThumbnailUtils.createVideoThumbnail(((File)localObject13).getAbsolutePath(), 1);
                  label1053:
                  if (localObject4 != null)
                  {
                    ((TLRPC.TL_document)localObject14).thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject4, 90.0F, 90.0F, 55, bool1);
                    ((Bitmap)localObject4).recycle();
                  }
                }
                catch (Exception localException1)
                {
                  for (;;)
                  {
                    FileLog.e(localException1);
                    continue;
                    localObject1 = ((File)localObject12).toString();
                  }
                }
                localObject12 = localObject1;
                localObject13 = localObject14;
                if (((TLRPC.TL_document)localObject14).thumb == null)
                {
                  ((TLRPC.TL_document)localObject14).thumb = new TLRPC.TL_photoSize();
                  ((TLRPC.TL_document)localObject14).thumb.w = localSendingMediaInfo.searchImage.width;
                  ((TLRPC.TL_document)localObject14).thumb.h = localSendingMediaInfo.searchImage.height;
                  ((TLRPC.TL_document)localObject14).thumb.size = 0;
                  ((TLRPC.TL_document)localObject14).thumb.location = new TLRPC.TL_fileLocationUnavailable();
                  ((TLRPC.TL_document)localObject14).thumb.type = "x";
                  localObject13 = localObject14;
                  localObject12 = localObject1;
                }
                localObject1 = localSendingMediaInfo.searchImage.imageUrl;
                if (localObject12 == null)
                {
                  localObject1 = localSendingMediaInfo.searchImage.imageUrl;
                  if ((localObject11 != null) && (localSendingMediaInfo.searchImage.imageUrl != null)) {
                    ((HashMap)localObject11).put("originalPath", localSendingMediaInfo.searchImage.imageUrl);
                  }
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      SendMessagesHelper.getInstance(SendMessagesHelper.21.this.val$currentAccount).sendMessage(localObject13, null, localObject1, SendMessagesHelper.21.this.val$dialog_id, SendMessagesHelper.21.this.val$reply_to_msg, localSendingMediaInfo.caption, localSendingMediaInfo.entities, null, localObject11, 0);
                    }
                  });
                  localObject14 = localObject9;
                  localObject12 = localObject7;
                  localObject13 = localObject8;
                  localObject4 = localObject2;
                  m = j;
                  l3 = l5;
                  localObject11 = localObject10;
                }
              }
            }
            for (;;)
            {
              n++;
              localObject10 = localObject11;
              l2 = l4;
              localObject2 = localObject4;
              localObject8 = localObject13;
              localObject7 = localObject12;
              localObject9 = localObject14;
              break;
              localObject4 = localObject1;
              if (!bool1)
              {
                localObject13 = MessagesStorage.getInstance(paramBoolean1);
                localObject4 = localSendingMediaInfo.searchImage.imageUrl;
                if (bool1) {
                  break label1457;
                }
              }
              label1457:
              for (m = 1;; m = 4)
              {
                localObject13 = (TLRPC.Document)((MessagesStorage)localObject13).getSentFile((String)localObject4, m);
                localObject4 = localObject1;
                if ((localObject13 instanceof TLRPC.TL_document)) {
                  localObject4 = (TLRPC.TL_document)localObject13;
                }
                localObject1 = Utilities.MD5(localSendingMediaInfo.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(localSendingMediaInfo.searchImage.imageUrl, "jpg");
                localObject1 = new File(FileLoader.getDirectory(4), (String)localObject1);
                break;
              }
              label1463:
              ((TLRPC.TL_document)localObject14).mime_type = "image/gif";
              break label923;
              label1474:
              localObject1 = null;
              break label935;
              label1480:
              localObject4 = ImageLoader.loadBitmap(((File)localObject13).getAbsolutePath(), null, 90.0F, 90.0F, true);
              break label1053;
              final boolean bool2 = true;
              boolean bool3 = true;
              localObject1 = null;
              localObject13 = localObject1;
              final Object localObject5;
              if (!bool1)
              {
                localObject13 = localObject1;
                if (localSendingMediaInfo.ttl == 0)
                {
                  localObject5 = MessagesStorage.getInstance(paramBoolean1);
                  localObject1 = localSendingMediaInfo.searchImage.imageUrl;
                  if (bool1) {
                    break label2155;
                  }
                }
              }
              boolean bool4;
              label2155:
              for (m = 0;; m = 3)
              {
                localObject13 = (TLRPC.TL_photo)((MessagesStorage)localObject5).getSentFile((String)localObject1, m);
                localObject1 = localObject13;
                if (localObject13 == null)
                {
                  localObject1 = Utilities.MD5(localSendingMediaInfo.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(localSendingMediaInfo.searchImage.imageUrl, "jpg");
                  localObject1 = new File(FileLoader.getDirectory(4), (String)localObject1);
                  bool4 = bool3;
                  localObject5 = localObject13;
                  if (((File)localObject1).exists())
                  {
                    bool4 = bool3;
                    localObject5 = localObject13;
                    if (((File)localObject1).length() != 0L)
                    {
                      localObject1 = SendMessagesHelper.getInstance(paramBoolean1).generatePhotoSizes(((File)localObject1).toString(), null);
                      bool4 = bool3;
                      localObject5 = localObject1;
                      if (localObject1 != null)
                      {
                        bool4 = false;
                        localObject5 = localObject1;
                      }
                    }
                  }
                  bool2 = bool4;
                  localObject1 = localObject5;
                  if (localObject5 == null)
                  {
                    localObject1 = Utilities.MD5(localSendingMediaInfo.searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(localSendingMediaInfo.searchImage.thumbUrl, "jpg");
                    localObject1 = new File(FileLoader.getDirectory(4), (String)localObject1);
                    if (((File)localObject1).exists()) {
                      localObject5 = SendMessagesHelper.getInstance(paramBoolean1).generatePhotoSizes(((File)localObject1).toString(), null);
                    }
                    bool2 = bool4;
                    localObject1 = localObject5;
                    if (localObject5 == null)
                    {
                      localObject1 = new TLRPC.TL_photo();
                      ((TLRPC.TL_photo)localObject1).date = ConnectionsManager.getInstance(paramBoolean1).getCurrentTime();
                      localObject5 = new TLRPC.TL_photoSize();
                      ((TLRPC.TL_photoSize)localObject5).w = localSendingMediaInfo.searchImage.width;
                      ((TLRPC.TL_photoSize)localObject5).h = localSendingMediaInfo.searchImage.height;
                      ((TLRPC.TL_photoSize)localObject5).size = 0;
                      ((TLRPC.TL_photoSize)localObject5).location = new TLRPC.TL_fileLocationUnavailable();
                      ((TLRPC.TL_photoSize)localObject5).type = "x";
                      ((TLRPC.TL_photo)localObject1).sizes.add(localObject5);
                      bool2 = bool4;
                    }
                  }
                }
                localObject11 = localObject10;
                l3 = l5;
                m = j;
                localObject5 = localObject2;
                localObject13 = localObject8;
                localObject12 = localObject7;
                localObject14 = localObject9;
                if (localObject1 == null) {
                  break;
                }
                localObject5 = new HashMap();
                if (localSendingMediaInfo.searchImage.imageUrl != null) {
                  ((HashMap)localObject5).put("originalPath", localSendingMediaInfo.searchImage.imageUrl);
                }
                l3 = l5;
                m = j;
                if (paramMessageObject)
                {
                  j++;
                  ((HashMap)localObject5).put("groupId", "" + l4);
                  if (j != 10)
                  {
                    l3 = l5;
                    m = j;
                    if (n != i - 1) {}
                  }
                  else
                  {
                    ((HashMap)localObject5).put("final", "1");
                    l3 = 0L;
                    m = j;
                  }
                }
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    SendMessagesHelper localSendMessagesHelper = SendMessagesHelper.getInstance(SendMessagesHelper.21.this.val$currentAccount);
                    TLRPC.TL_photo localTL_photo = localObject1;
                    if (bool2) {}
                    for (String str = localSendingMediaInfo.searchImage.imageUrl;; str = null)
                    {
                      localSendMessagesHelper.sendMessage(localTL_photo, str, SendMessagesHelper.21.this.val$dialog_id, SendMessagesHelper.21.this.val$reply_to_msg, localSendingMediaInfo.caption, localSendingMediaInfo.entities, null, localObject5, localSendingMediaInfo.ttl);
                      return;
                    }
                  }
                });
                localObject11 = localObject10;
                localObject5 = localObject2;
                localObject13 = localObject8;
                localObject12 = localObject7;
                localObject14 = localObject9;
                break;
              }
              if (localSendingMediaInfo.isVideo)
              {
                localObject11 = null;
                String str;
                File localFile;
                if (paramBoolean2)
                {
                  localObject13 = null;
                  if ((paramBoolean2) || ((localObject13 == null) && (!localSendingMediaInfo.path.endsWith("mp4")))) {
                    break label3230;
                  }
                  str = localSendingMediaInfo.path;
                  localObject1 = localSendingMediaInfo.path;
                  localFile = new File((String)localObject1);
                  l3 = 0L;
                  bool4 = false;
                  localObject1 = (String)localObject1 + localFile.length() + "_" + localFile.lastModified();
                  localObject12 = localObject1;
                  if (localObject13 != null)
                  {
                    bool4 = ((VideoEditedInfo)localObject13).muted;
                    localObject5 = new StringBuilder().append((String)localObject1).append(((VideoEditedInfo)localObject13).estimatedDuration).append("_").append(((VideoEditedInfo)localObject13).startTime).append("_").append(((VideoEditedInfo)localObject13).endTime);
                    if (!((VideoEditedInfo)localObject13).muted) {
                      break label3039;
                    }
                    localObject1 = "_m";
                    label2354:
                    localObject5 = (String)localObject1;
                    localObject1 = localObject5;
                    if (((VideoEditedInfo)localObject13).resultWidth != ((VideoEditedInfo)localObject13).originalWidth) {
                      localObject1 = (String)localObject5 + "_" + ((VideoEditedInfo)localObject13).resultWidth;
                    }
                    if (((VideoEditedInfo)localObject13).startTime < 0L) {
                      break label3047;
                    }
                    l3 = ((VideoEditedInfo)localObject13).startTime;
                    localObject12 = localObject1;
                  }
                  label2434:
                  localObject5 = null;
                  localObject1 = localObject5;
                  if (!bool1)
                  {
                    localObject1 = localObject5;
                    if (localSendingMediaInfo.ttl == 0)
                    {
                      localObject1 = MessagesStorage.getInstance(paramBoolean1);
                      if (bool1) {
                        break label3057;
                      }
                      m = 2;
                      label2475:
                      localObject1 = (TLRPC.TL_document)((MessagesStorage)localObject1).getSentFile((String)localObject12, m);
                    }
                  }
                  localObject14 = localObject1;
                  localObject5 = str;
                  if (localObject1 == null)
                  {
                    localObject1 = SendMessagesHelper.createVideoThumbnail(localSendingMediaInfo.path, l3);
                    localObject5 = localObject1;
                    if (localObject1 == null) {
                      localObject5 = ThumbnailUtils.createVideoThumbnail(localSendingMediaInfo.path, 1);
                    }
                    localObject11 = ImageLoader.scaleAndSaveImage((Bitmap)localObject5, 90.0F, 90.0F, 55, bool1);
                    localObject1 = localObject5;
                    if (localObject5 != null)
                    {
                      localObject1 = localObject5;
                      if (localObject11 != null) {
                        localObject1 = null;
                      }
                    }
                    localObject14 = new TLRPC.TL_document();
                    ((TLRPC.TL_document)localObject14).thumb = ((TLRPC.PhotoSize)localObject11);
                    if (((TLRPC.TL_document)localObject14).thumb != null) {
                      break label3063;
                    }
                    ((TLRPC.TL_document)localObject14).thumb = new TLRPC.TL_photoSizeEmpty();
                    ((TLRPC.TL_document)localObject14).thumb.type = "s";
                    label2619:
                    ((TLRPC.TL_document)localObject14).mime_type = "video/mp4";
                    UserConfig.getInstance(paramBoolean1).saveConfig(false);
                    if (!bool1) {
                      break label3089;
                    }
                    if (k < 66) {
                      break label3077;
                    }
                    localObject5 = new TLRPC.TL_documentAttributeVideo();
                    label2659:
                    ((TLRPC.TL_document)localObject14).attributes.add(localObject5);
                    if ((localObject13 == null) || (!((VideoEditedInfo)localObject13).needConvert())) {
                      break label3189;
                    }
                    if (!((VideoEditedInfo)localObject13).muted) {
                      break label3107;
                    }
                    ((TLRPC.TL_document)localObject14).attributes.add(new TLRPC.TL_documentAttributeAnimated());
                    SendMessagesHelper.fillVideoAttribute(localSendingMediaInfo.path, (TLRPC.TL_documentAttributeVideo)localObject5, (VideoEditedInfo)localObject13);
                    ((VideoEditedInfo)localObject13).originalWidth = ((TLRPC.TL_documentAttributeVideo)localObject5).w;
                    ((VideoEditedInfo)localObject13).originalHeight = ((TLRPC.TL_documentAttributeVideo)localObject5).h;
                    ((TLRPC.TL_documentAttributeVideo)localObject5).w = ((VideoEditedInfo)localObject13).resultWidth;
                    ((TLRPC.TL_documentAttributeVideo)localObject5).h = ((VideoEditedInfo)localObject13).resultHeight;
                    label2759:
                    ((TLRPC.TL_document)localObject14).size = ((int)((VideoEditedInfo)localObject13).estimatedSize);
                    localObject5 = "-2147483648_" + SharedConfig.getLastLocalId() + ".mp4";
                    localObject5 = new File(FileLoader.getDirectory(4), (String)localObject5);
                    SharedConfig.saveConfig();
                    localObject5 = ((File)localObject5).getAbsolutePath();
                  }
                }
                for (localObject11 = localObject1;; localObject11 = localObject1)
                {
                  localObject1 = new HashMap();
                  if (localObject12 != null) {
                    ((HashMap)localObject1).put("originalPath", localObject12);
                  }
                  l3 = l5;
                  m = j;
                  if (!bool4)
                  {
                    l3 = l5;
                    m = j;
                    if (paramMessageObject)
                    {
                      j++;
                      ((HashMap)localObject1).put("groupId", "" + l4);
                      if (j != 10)
                      {
                        l3 = l5;
                        m = j;
                        if (n != i - 1) {}
                      }
                      else
                      {
                        ((HashMap)localObject1).put("final", "1");
                        l3 = 0L;
                        m = j;
                      }
                    }
                  }
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      if ((localObject11 != null) && (this.val$thumbKeyFinal != null)) {
                        ImageLoader.getInstance().putImageToCache(new BitmapDrawable(localObject11), this.val$thumbKeyFinal);
                      }
                      SendMessagesHelper.getInstance(SendMessagesHelper.21.this.val$currentAccount).sendMessage(localObject14, localObject13, localObject5, SendMessagesHelper.21.this.val$dialog_id, SendMessagesHelper.21.this.val$reply_to_msg, localSendingMediaInfo.caption, localSendingMediaInfo.entities, null, localObject1, localSendingMediaInfo.ttl);
                    }
                  });
                  localObject11 = localObject10;
                  localObject5 = localObject2;
                  localObject13 = localObject8;
                  localObject12 = localObject7;
                  localObject14 = localObject9;
                  break;
                  if (localSendingMediaInfo.videoEditedInfo != null) {}
                  for (localObject1 = localSendingMediaInfo.videoEditedInfo;; localObject1 = SendMessagesHelper.createCompressionSettings(localSendingMediaInfo.path))
                  {
                    localObject13 = localObject1;
                    break;
                  }
                  label3039:
                  localObject1 = "";
                  break label2354;
                  label3047:
                  l3 = 0L;
                  localObject12 = localObject1;
                  break label2434;
                  label3057:
                  m = 5;
                  break label2475;
                  label3063:
                  ((TLRPC.TL_document)localObject14).thumb.type = "s";
                  break label2619;
                  label3077:
                  localObject5 = new TLRPC.TL_documentAttributeVideo_layer65();
                  break label2659;
                  label3089:
                  localObject5 = new TLRPC.TL_documentAttributeVideo();
                  ((TLRPC.TL_documentAttributeVideo)localObject5).supports_streaming = true;
                  break label2659;
                  label3107:
                  ((TLRPC.TL_documentAttributeVideo)localObject5).duration = ((int)(((VideoEditedInfo)localObject13).estimatedDuration / 1000L));
                  if ((((VideoEditedInfo)localObject13).rotationValue == 90) || (((VideoEditedInfo)localObject13).rotationValue == 270))
                  {
                    ((TLRPC.TL_documentAttributeVideo)localObject5).w = ((VideoEditedInfo)localObject13).resultHeight;
                    ((TLRPC.TL_documentAttributeVideo)localObject5).h = ((VideoEditedInfo)localObject13).resultWidth;
                    break label2759;
                  }
                  ((TLRPC.TL_documentAttributeVideo)localObject5).w = ((VideoEditedInfo)localObject13).resultWidth;
                  ((TLRPC.TL_documentAttributeVideo)localObject5).h = ((VideoEditedInfo)localObject13).resultHeight;
                  break label2759;
                  label3189:
                  if (localFile.exists()) {
                    ((TLRPC.TL_document)localObject14).size = ((int)localFile.length());
                  }
                  SendMessagesHelper.fillVideoAttribute(localSendingMediaInfo.path, (TLRPC.TL_documentAttributeVideo)localObject5, null);
                  localObject5 = str;
                }
                label3230:
                SendMessagesHelper.prepareSendingDocumentInternal(paramBoolean1, localSendingMediaInfo.path, localSendingMediaInfo.path, null, null, paramLong, paramInputContentInfoCompat, localSendingMediaInfo.caption, localSendingMediaInfo.entities);
                localObject11 = localObject10;
                l3 = l5;
                m = j;
                localObject5 = localObject2;
                localObject13 = localObject8;
                localObject12 = localObject7;
                localObject14 = localObject9;
              }
              else
              {
                localObject1 = localSendingMediaInfo.path;
                localObject5 = localSendingMediaInfo.path;
                localObject14 = localObject1;
                localObject13 = localObject5;
                if (localObject5 == null)
                {
                  localObject14 = localObject1;
                  localObject13 = localObject5;
                  if (localSendingMediaInfo.uri != null)
                  {
                    localObject13 = AndroidUtilities.getPath(localSendingMediaInfo.uri);
                    localObject14 = localSendingMediaInfo.uri.toString();
                  }
                }
                int i1 = 0;
                if (paramBoolean2)
                {
                  m = 1;
                  localObject1 = FileLoader.getFileExtension(new File((String)localObject13));
                  localObject12 = localObject13;
                  localObject5 = localObject14;
                }
                for (;;)
                {
                  if (m == 0) {
                    break label3729;
                  }
                  localObject13 = localObject2;
                  if (localObject2 == null)
                  {
                    localObject13 = new ArrayList();
                    localObject9 = new ArrayList();
                    localObject8 = new ArrayList();
                    localObject7 = new ArrayList();
                  }
                  ((ArrayList)localObject13).add(localObject12);
                  ((ArrayList)localObject9).add(localObject5);
                  ((ArrayList)localObject8).add(localSendingMediaInfo.caption);
                  ((ArrayList)localObject7).add(localSendingMediaInfo.entities);
                  localObject11 = localObject1;
                  l3 = l5;
                  m = j;
                  localObject5 = localObject13;
                  localObject13 = localObject8;
                  localObject12 = localObject7;
                  localObject14 = localObject9;
                  break;
                  if ((localObject13 != null) && ((((String)localObject13).endsWith(".gif")) || (((String)localObject13).endsWith(".webp"))))
                  {
                    if (((String)localObject13).endsWith(".gif")) {}
                    for (localObject1 = "gif";; localObject1 = "webp")
                    {
                      m = 1;
                      localObject5 = localObject14;
                      localObject12 = localObject13;
                      break;
                    }
                  }
                  localObject1 = localObject10;
                  m = i1;
                  localObject5 = localObject14;
                  localObject12 = localObject13;
                  if (localObject13 == null)
                  {
                    localObject1 = localObject10;
                    m = i1;
                    localObject5 = localObject14;
                    localObject12 = localObject13;
                    if (localSendingMediaInfo.uri != null) {
                      if (MediaController.isGif(localSendingMediaInfo.uri))
                      {
                        m = 1;
                        localObject5 = localSendingMediaInfo.uri.toString();
                        localObject12 = MediaController.copyFileToCache(localSendingMediaInfo.uri, "gif");
                        localObject1 = "gif";
                      }
                      else
                      {
                        localObject1 = localObject10;
                        m = i1;
                        localObject5 = localObject14;
                        localObject12 = localObject13;
                        if (MediaController.isWebp(localSendingMediaInfo.uri))
                        {
                          m = 1;
                          localObject5 = localSendingMediaInfo.uri.toString();
                          localObject12 = MediaController.copyFileToCache(localSendingMediaInfo.uri, "webp");
                          localObject1 = "webp";
                        }
                      }
                    }
                  }
                }
                label3729:
                if (localObject12 != null)
                {
                  localObject13 = new File((String)localObject12);
                  localObject10 = (String)localObject5 + ((File)localObject13).length() + "_" + ((File)localObject13).lastModified();
                  localObject5 = null;
                  if (localObject3 == null) {
                    break label3973;
                  }
                  localObject14 = (SendMessagesHelper.MediaSendPrepareWorker)((HashMap)localObject3).get(localSendingMediaInfo);
                  localObject13 = ((SendMessagesHelper.MediaSendPrepareWorker)localObject14).photo;
                  localObject5 = localObject13;
                  if (localObject13 != null) {}
                }
                for (;;)
                {
                  try
                  {
                    ((SendMessagesHelper.MediaSendPrepareWorker)localObject14).sync.await();
                    localObject5 = ((SendMessagesHelper.MediaSendPrepareWorker)localObject14).photo;
                    if (localObject5 == null) {
                      break label4302;
                    }
                    localObject13 = new HashMap();
                    if ((localSendingMediaInfo.masks == null) || (localSendingMediaInfo.masks.isEmpty())) {
                      break label4133;
                    }
                    bool4 = true;
                    ((TLRPC.TL_photo)localObject5).has_stickers = bool4;
                    if (!bool4) {
                      break label4156;
                    }
                    localObject12 = new SerializedData(localSendingMediaInfo.masks.size() * 20 + 4);
                    ((SerializedData)localObject12).writeInt32(localSendingMediaInfo.masks.size());
                    m = 0;
                    if (m >= localSendingMediaInfo.masks.size()) {
                      break label4139;
                    }
                    ((TLRPC.InputDocument)localSendingMediaInfo.masks.get(m)).serializeToStream((AbstractSerializedData)localObject12);
                    m++;
                    continue;
                    localObject10 = null;
                  }
                  catch (Exception localException2)
                  {
                    FileLog.e(localException2);
                    continue;
                  }
                  label3973:
                  localObject13 = localException2;
                  if (!bool1)
                  {
                    localObject13 = localException2;
                    if (localSendingMediaInfo.ttl == 0)
                    {
                      localObject6 = MessagesStorage.getInstance(paramBoolean1);
                      if (bool1) {
                        break label4121;
                      }
                      m = 0;
                      label4011:
                      localObject6 = (TLRPC.TL_photo)((MessagesStorage)localObject6).getSentFile((String)localObject10, m);
                      localObject13 = localObject6;
                      if (localObject6 == null)
                      {
                        localObject13 = localObject6;
                        if (localSendingMediaInfo.uri != null)
                        {
                          localObject13 = MessagesStorage.getInstance(paramBoolean1);
                          localObject6 = AndroidUtilities.getPath(localSendingMediaInfo.uri);
                          if (bool1) {
                            break label4127;
                          }
                        }
                      }
                    }
                  }
                  label4121:
                  label4127:
                  for (m = 0;; m = 3)
                  {
                    localObject13 = (TLRPC.TL_photo)((MessagesStorage)localObject13).getSentFile((String)localObject6, m);
                    localObject6 = localObject13;
                    if (localObject13 != null) {
                      break;
                    }
                    localObject6 = SendMessagesHelper.getInstance(paramBoolean1).generatePhotoSizes(localSendingMediaInfo.path, localSendingMediaInfo.uri);
                    break;
                    m = 3;
                    break label4011;
                  }
                  label4133:
                  bool4 = false;
                }
                label4139:
                ((HashMap)localObject13).put("masks", Utilities.bytesToHex(((SerializedData)localObject12).toByteArray()));
                label4156:
                if (localObject10 != null) {
                  ((HashMap)localObject13).put("originalPath", localObject10);
                }
                l3 = l5;
                m = j;
                if (paramMessageObject)
                {
                  j++;
                  ((HashMap)localObject13).put("groupId", "" + l4);
                  if (j != 10)
                  {
                    l3 = l5;
                    m = j;
                    if (n != i - 1) {}
                  }
                  else
                  {
                    ((HashMap)localObject13).put("final", "1");
                    l3 = 0L;
                    m = j;
                  }
                }
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    SendMessagesHelper.getInstance(SendMessagesHelper.21.this.val$currentAccount).sendMessage(localObject6, null, SendMessagesHelper.21.this.val$dialog_id, SendMessagesHelper.21.this.val$reply_to_msg, localSendingMediaInfo.caption, localSendingMediaInfo.entities, null, localObject13, localSendingMediaInfo.ttl);
                  }
                });
                localObject11 = localObject1;
                final Object localObject6 = localObject2;
                localObject13 = localObject8;
                localObject12 = localObject7;
                localObject14 = localObject9;
                continue;
                label4302:
                localObject6 = localObject2;
                if (localObject2 == null)
                {
                  localObject6 = new ArrayList();
                  localObject9 = new ArrayList();
                  localObject8 = new ArrayList();
                  localObject7 = new ArrayList();
                }
                ((ArrayList)localObject6).add(localObject12);
                ((ArrayList)localObject9).add(localObject10);
                ((ArrayList)localObject8).add(localSendingMediaInfo.caption);
                ((ArrayList)localObject7).add(localSendingMediaInfo.entities);
                localObject11 = localObject1;
                l3 = l5;
                m = j;
                localObject13 = localObject8;
                localObject12 = localObject7;
                localObject14 = localObject9;
              }
            }
          }
          if (l3 != 0L) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                SendMessagesHelper localSendMessagesHelper = SendMessagesHelper.getInstance(SendMessagesHelper.21.this.val$currentAccount);
                Object localObject = (ArrayList)localSendMessagesHelper.delayedMessages.get("group_" + l3);
                if ((localObject != null) && (!((ArrayList)localObject).isEmpty()))
                {
                  localObject = (SendMessagesHelper.DelayedMessage)((ArrayList)localObject).get(0);
                  MessageObject localMessageObject = (MessageObject)((SendMessagesHelper.DelayedMessage)localObject).messageObjects.get(((SendMessagesHelper.DelayedMessage)localObject).messageObjects.size() - 1);
                  ((SendMessagesHelper.DelayedMessage)localObject).finalGroupMessage = localMessageObject.getId();
                  localMessageObject.messageOwner.params.put("final", "1");
                  TLRPC.TL_messages_messages localTL_messages_messages = new TLRPC.TL_messages_messages();
                  localTL_messages_messages.messages.add(localMessageObject.messageOwner);
                  MessagesStorage.getInstance(SendMessagesHelper.21.this.val$currentAccount).putMessages(localTL_messages_messages, ((SendMessagesHelper.DelayedMessage)localObject).peer, -2, 0, false);
                  localSendMessagesHelper.sendReadyToSendGroup((SendMessagesHelper.DelayedMessage)localObject, true, true);
                }
              }
            });
          }
          if (this.val$inputContent != null) {
            this.val$inputContent.releasePermission();
          }
          if ((localObject2 != null) && (!((ArrayList)localObject2).isEmpty())) {
            for (j = 0; j < ((ArrayList)localObject2).size(); j++) {
              SendMessagesHelper.prepareSendingDocumentInternal(paramBoolean1, (String)((ArrayList)localObject2).get(j), (String)((ArrayList)localObject9).get(j), null, (String)localObject10, paramLong, paramInputContentInfoCompat, (CharSequence)((ArrayList)localObject8).get(j), (ArrayList)((ArrayList)localObject7).get(j));
            }
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("total send time = " + (System.currentTimeMillis() - l1));
          }
        }
      });
    }
  }
  
  public static void prepareSendingPhoto(String paramString, Uri paramUri, long paramLong, MessageObject paramMessageObject, CharSequence paramCharSequence, ArrayList<TLRPC.MessageEntity> paramArrayList, ArrayList<TLRPC.InputDocument> paramArrayList1, InputContentInfoCompat paramInputContentInfoCompat, int paramInt)
  {
    SendingMediaInfo localSendingMediaInfo = new SendingMediaInfo();
    localSendingMediaInfo.path = paramString;
    localSendingMediaInfo.uri = paramUri;
    if (paramCharSequence != null) {
      localSendingMediaInfo.caption = paramCharSequence.toString();
    }
    localSendingMediaInfo.entities = paramArrayList;
    localSendingMediaInfo.ttl = paramInt;
    if ((paramArrayList1 != null) && (!paramArrayList1.isEmpty())) {
      localSendingMediaInfo.masks = new ArrayList(paramArrayList1);
    }
    paramString = new ArrayList();
    paramString.add(localSendingMediaInfo);
    prepareSendingMedia(paramString, paramLong, paramMessageObject, paramInputContentInfoCompat, false, false);
  }
  
  public static void prepareSendingText(String paramString, final long paramLong)
  {
    final int i = UserConfig.selectedAccount;
    MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new Runnable()
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
                String str1 = SendMessagesHelper.getTrimmedString(SendMessagesHelper.20.this.val$text);
                if (str1.length() != 0)
                {
                  int i = (int)Math.ceil(str1.length() / 4096.0F);
                  for (int j = 0; j < i; j++)
                  {
                    String str2 = str1.substring(j * 4096, Math.min((j + 1) * 4096, str1.length()));
                    SendMessagesHelper.getInstance(SendMessagesHelper.20.this.val$currentAccount).sendMessage(str2, SendMessagesHelper.20.this.val$dialog_id, null, null, true, null, null, null);
                  }
                }
              }
            });
          }
        });
      }
    });
  }
  
  public static void prepareSendingVideo(final String paramString, final long paramLong1, long paramLong2, final int paramInt1, final int paramInt2, VideoEditedInfo paramVideoEditedInfo, final long paramLong3, final MessageObject paramMessageObject, final CharSequence paramCharSequence, ArrayList<TLRPC.MessageEntity> paramArrayList, final int paramInt3)
  {
    if ((paramString == null) || (paramString.length() == 0)) {}
    for (;;)
    {
      return;
      new Thread(new Runnable()
      {
        public void run()
        {
          final VideoEditedInfo localVideoEditedInfo;
          boolean bool1;
          label22:
          boolean bool2;
          label35:
          final Object localObject1;
          final Object localObject2;
          Object localObject3;
          final Object localObject4;
          final Object localObject5;
          File localFile;
          long l;
          final Object localObject6;
          Object localObject7;
          label200:
          label275:
          int i;
          label313:
          final Object localObject8;
          if (this.val$info != null)
          {
            localVideoEditedInfo = this.val$info;
            if ((int)paramLong3 != 0) {
              break label654;
            }
            bool1 = true;
            if ((localVideoEditedInfo == null) || (!localVideoEditedInfo.roundVideo)) {
              break label659;
            }
            bool2 = true;
            localObject1 = null;
            localObject2 = null;
            localObject3 = null;
            if ((localVideoEditedInfo == null) && (!paramString.endsWith("mp4")) && (!bool2)) {
              break label1255;
            }
            localObject4 = paramString;
            localObject5 = paramString;
            localFile = new File((String)localObject5);
            l = 0L;
            localObject6 = (String)localObject5 + localFile.length() + "_" + localFile.lastModified();
            localObject7 = localObject6;
            if (localVideoEditedInfo != null)
            {
              localObject5 = localObject6;
              if (!bool2)
              {
                localObject6 = new StringBuilder().append((String)localObject6).append(paramInt3).append("_").append(localVideoEditedInfo.startTime).append("_").append(localVideoEditedInfo.endTime);
                if (!localVideoEditedInfo.muted) {
                  break label664;
                }
                localObject5 = "_m";
                localObject6 = (String)localObject5;
                localObject5 = localObject6;
                if (localVideoEditedInfo.resultWidth != localVideoEditedInfo.originalWidth) {
                  localObject5 = (String)localObject6 + "_" + localVideoEditedInfo.resultWidth;
                }
              }
              if (localVideoEditedInfo.startTime < 0L) {
                break label671;
              }
              l = localVideoEditedInfo.startTime;
              localObject7 = localObject5;
            }
            localObject6 = null;
            localObject5 = localObject6;
            if (!bool1)
            {
              localObject5 = localObject6;
              if (paramInt2 == 0)
              {
                localObject5 = MessagesStorage.getInstance(paramInt1);
                if (bool1) {
                  break label681;
                }
                i = 2;
                localObject5 = (TLRPC.TL_document)((MessagesStorage)localObject5).getSentFile((String)localObject7, i);
              }
            }
            localObject6 = localObject1;
            localObject8 = localObject5;
            localObject1 = localObject4;
            if (localObject5 != null) {
              break label1033;
            }
            localObject5 = SendMessagesHelper.createVideoThumbnail(paramString, l);
            localObject1 = localObject5;
            if (localObject5 == null) {
              localObject1 = ThumbnailUtils.createVideoThumbnail(paramString, 1);
            }
            localObject2 = ImageLoader.scaleAndSaveImage((Bitmap)localObject1, 90.0F, 90.0F, 55, bool1);
            localObject6 = localObject1;
            localObject5 = localObject3;
            if (localObject1 != null)
            {
              localObject6 = localObject1;
              localObject5 = localObject3;
              if (localObject2 != null)
              {
                if (!bool2) {
                  break label822;
                }
                if (!bool1) {
                  break label693;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                  break label687;
                }
                i = 0;
                label433:
                Utilities.blurBitmap(localObject1, 7, i, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight(), ((Bitmap)localObject1).getRowBytes());
                localObject5 = String.format(((TLRPC.PhotoSize)localObject2).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject2).location.local_id + "@%d_%d_b2", new Object[] { Integer.valueOf((int)(AndroidUtilities.roundMessageSize / AndroidUtilities.density)), Integer.valueOf((int)(AndroidUtilities.roundMessageSize / AndroidUtilities.density)) });
                localObject6 = localObject1;
              }
            }
            label542:
            localObject8 = new TLRPC.TL_document();
            ((TLRPC.TL_document)localObject8).thumb = ((TLRPC.PhotoSize)localObject2);
            if (((TLRPC.TL_document)localObject8).thumb != null) {
              break label832;
            }
            ((TLRPC.TL_document)localObject8).thumb = new TLRPC.TL_photoSizeEmpty();
            ((TLRPC.TL_document)localObject8).thumb.type = "s";
            label588:
            ((TLRPC.TL_document)localObject8).mime_type = "video/mp4";
            UserConfig.getInstance(paramInt1).saveConfig(false);
            if (!bool1) {
              break label1115;
            }
            i = (int)(paramLong3 >> 32);
            localObject1 = MessagesController.getInstance(paramInt1).getEncryptedChat(Integer.valueOf(i));
            if (localObject1 != null) {
              break label845;
            }
          }
          for (;;)
          {
            return;
            localVideoEditedInfo = SendMessagesHelper.createCompressionSettings(paramString);
            break;
            label654:
            bool1 = false;
            break label22;
            label659:
            bool2 = false;
            break label35;
            label664:
            localObject5 = "";
            break label200;
            label671:
            l = 0L;
            localObject7 = localObject5;
            break label275;
            label681:
            i = 5;
            break label313;
            label687:
            i = 1;
            break label433;
            label693:
            if (Build.VERSION.SDK_INT < 21) {}
            for (i = 0;; i = 1)
            {
              Utilities.blurBitmap(localObject1, 3, i, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight(), ((Bitmap)localObject1).getRowBytes());
              localObject5 = String.format(((TLRPC.PhotoSize)localObject2).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject2).location.local_id + "@%d_%d_b", new Object[] { Integer.valueOf((int)(AndroidUtilities.roundMessageSize / AndroidUtilities.density)), Integer.valueOf((int)(AndroidUtilities.roundMessageSize / AndroidUtilities.density)) });
              localObject6 = localObject1;
              break;
            }
            label822:
            localObject6 = null;
            localObject5 = localObject3;
            break label542;
            label832:
            ((TLRPC.TL_document)localObject8).thumb.type = "s";
            break label588;
            label845:
            if (AndroidUtilities.getPeerLayerVersion(((TLRPC.EncryptedChat)localObject1).layer) >= 66)
            {
              localObject1 = new TLRPC.TL_documentAttributeVideo();
              label867:
              ((TLRPC.TL_documentAttributeVideo)localObject1).round_message = bool2;
              ((TLRPC.TL_document)localObject8).attributes.add(localObject1);
              if ((localVideoEditedInfo == null) || (!localVideoEditedInfo.needConvert())) {
                break label1208;
              }
              if (!localVideoEditedInfo.muted) {
                break label1133;
              }
              ((TLRPC.TL_document)localObject8).attributes.add(new TLRPC.TL_documentAttributeAnimated());
              SendMessagesHelper.fillVideoAttribute(paramString, (TLRPC.TL_documentAttributeVideo)localObject1, localVideoEditedInfo);
              localVideoEditedInfo.originalWidth = ((TLRPC.TL_documentAttributeVideo)localObject1).w;
              localVideoEditedInfo.originalHeight = ((TLRPC.TL_documentAttributeVideo)localObject1).h;
              ((TLRPC.TL_documentAttributeVideo)localObject1).w = localVideoEditedInfo.resultWidth;
              ((TLRPC.TL_documentAttributeVideo)localObject1).h = localVideoEditedInfo.resultHeight;
              label964:
              ((TLRPC.TL_document)localObject8).size = ((int)paramMessageObject);
              localObject1 = "-2147483648_" + SharedConfig.getLastLocalId() + ".mp4";
              localObject1 = new File(FileLoader.getDirectory(4), (String)localObject1);
              SharedConfig.saveConfig();
              localObject1 = ((File)localObject1).getAbsolutePath();
              localObject2 = localObject5;
              label1033:
              localObject4 = new HashMap();
              if (this.val$caption == null) {
                break label1248;
              }
            }
            label1115:
            label1133:
            label1208:
            label1248:
            for (localObject5 = this.val$caption.toString();; localObject5 = "")
            {
              if (localObject7 != null) {
                ((HashMap)localObject4).put("originalPath", localObject7);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if ((localObject6 != null) && (localObject2 != null)) {
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(localObject6), localObject2);
                  }
                  SendMessagesHelper.getInstance(SendMessagesHelper.22.this.val$currentAccount).sendMessage(localObject8, localVideoEditedInfo, localObject1, SendMessagesHelper.22.this.val$dialog_id, SendMessagesHelper.22.this.val$reply_to_msg, localObject5, SendMessagesHelper.22.this.val$entities, null, localObject4, SendMessagesHelper.22.this.val$ttl);
                }
              });
              break;
              localObject1 = new TLRPC.TL_documentAttributeVideo_layer65();
              break label867;
              localObject1 = new TLRPC.TL_documentAttributeVideo();
              ((TLRPC.TL_documentAttributeVideo)localObject1).supports_streaming = true;
              break label867;
              ((TLRPC.TL_documentAttributeVideo)localObject1).duration = ((int)(paramInt3 / 1000L));
              if ((localVideoEditedInfo.rotationValue == 90) || (localVideoEditedInfo.rotationValue == 270))
              {
                ((TLRPC.TL_documentAttributeVideo)localObject1).w = paramLong1;
                ((TLRPC.TL_documentAttributeVideo)localObject1).h = paramCharSequence;
                break label964;
              }
              ((TLRPC.TL_documentAttributeVideo)localObject1).w = paramCharSequence;
              ((TLRPC.TL_documentAttributeVideo)localObject1).h = paramLong1;
              break label964;
              if (localFile.exists()) {
                ((TLRPC.TL_document)localObject8).size = ((int)localFile.length());
              }
              SendMessagesHelper.fillVideoAttribute(paramString, (TLRPC.TL_documentAttributeVideo)localObject1, null);
              localObject1 = localObject4;
              localObject2 = localObject5;
              break label1033;
            }
            label1255:
            SendMessagesHelper.prepareSendingDocumentInternal(paramInt1, paramString, paramString, null, null, paramLong3, this.val$reply_to_msg, this.val$caption, this.val$entities);
          }
        }
      }).start();
    }
  }
  
  private void putToDelayedMessages(String paramString, DelayedMessage paramDelayedMessage)
  {
    ArrayList localArrayList1 = (ArrayList)this.delayedMessages.get(paramString);
    ArrayList localArrayList2 = localArrayList1;
    if (localArrayList1 == null)
    {
      localArrayList2 = new ArrayList();
      this.delayedMessages.put(paramString, localArrayList2);
    }
    localArrayList2.add(paramDelayedMessage);
  }
  
  private void sendLocation(Location paramLocation)
  {
    TLRPC.TL_messageMediaGeo localTL_messageMediaGeo = new TLRPC.TL_messageMediaGeo();
    localTL_messageMediaGeo.geo = new TLRPC.TL_geoPoint();
    localTL_messageMediaGeo.geo.lat = paramLocation.getLatitude();
    localTL_messageMediaGeo.geo._long = paramLocation.getLongitude();
    Iterator localIterator = this.waitingForLocation.entrySet().iterator();
    while (localIterator.hasNext())
    {
      paramLocation = (MessageObject)((Map.Entry)localIterator.next()).getValue();
      sendMessage(localTL_messageMediaGeo, paramLocation.getDialogId(), paramLocation, null, null);
    }
  }
  
  private void sendMessage(String paramString1, String paramString2, TLRPC.MessageMedia paramMessageMedia, TLRPC.TL_photo paramTL_photo, VideoEditedInfo paramVideoEditedInfo, TLRPC.User paramUser, TLRPC.TL_document paramTL_document, TLRPC.TL_game paramTL_game, long paramLong, String paramString3, MessageObject paramMessageObject1, TLRPC.WebPage paramWebPage, boolean paramBoolean, MessageObject paramMessageObject2, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap, int paramInt)
  {
    if (paramLong == 0L) {
      return;
    }
    String str1 = paramString2;
    if (paramString1 == null)
    {
      str1 = paramString2;
      if (paramString2 == null) {
        str1 = "";
      }
    }
    paramString2 = null;
    String str2 = paramString2;
    if (paramHashMap != null)
    {
      str2 = paramString2;
      if (paramHashMap.containsKey("originalPath")) {
        str2 = (String)paramHashMap.get("originalPath");
      }
    }
    paramString2 = null;
    Object localObject1 = null;
    int i = -1;
    int j = (int)paramLong;
    int k = (int)(paramLong >> 32);
    int m = 0;
    Object localObject2 = null;
    if (j != 0) {}
    Object localObject3;
    for (TLRPC.InputPeer localInputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);; localInputPeer = null)
    {
      localObject3 = null;
      if (j != 0) {
        break label219;
      }
      localObject2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(k));
      localObject4 = localObject2;
      if (localObject2 != null) {
        break label271;
      }
      if (paramMessageObject2 == null) {
        break;
      }
      MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(paramMessageObject2.messageOwner);
      paramMessageObject2.messageOwner.send_state = 2;
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramMessageObject2.getId()) });
      processSentMessage(paramMessageObject2.getId());
      break;
    }
    label219:
    Object localObject4 = localObject2;
    Object localObject5;
    if ((localInputPeer instanceof TLRPC.TL_inputPeerChannel))
    {
      localObject5 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(localInputPeer.channel_id));
      if ((localObject5 == null) || (((TLRPC.Chat)localObject5).megagroup)) {
        break label869;
      }
      m = 1;
      localObject4 = localObject2;
    }
    label271:
    int n;
    Object localObject6;
    Object localObject7;
    TLRPC.TL_document localTL_document;
    Object localObject8;
    Object localObject9;
    label412:
    label577:
    label662:
    label801:
    label869:
    label879:
    label907:
    Object localObject11;
    for (;;)
    {
      if (paramMessageObject2 != null)
      {
        paramString2 = (String)localObject1;
        try
        {
          localObject1 = paramMessageObject2.messageOwner;
          paramString2 = (String)localObject1;
          if (!paramMessageObject2.isForwarded()) {
            break label879;
          }
          n = 4;
          localObject6 = paramHashMap;
          localObject7 = paramWebPage;
          localTL_document = paramTL_document;
          localObject8 = paramMessageMedia;
          localObject9 = paramString1;
          paramTL_game = (TLRPC.TL_game)localObject1;
          paramString2 = paramTL_game;
          if (paramTL_game.random_id == 0L)
          {
            paramString2 = paramTL_game;
            paramTL_game.random_id = getNextRandomId();
          }
          if (localObject6 != null)
          {
            paramString2 = paramTL_game;
            if (((HashMap)localObject6).containsKey("bot"))
            {
              if (localObject4 == null) {
                break label3443;
              }
              paramString2 = paramTL_game;
              paramTL_game.via_bot_name = ((String)((HashMap)localObject6).get("bot_name"));
              paramString2 = paramTL_game;
              if (paramTL_game.via_bot_name == null)
              {
                paramString2 = paramTL_game;
                paramTL_game.via_bot_name = "";
              }
              paramString2 = paramTL_game;
              paramTL_game.flags |= 0x800;
            }
          }
          paramString2 = paramTL_game;
          paramTL_game.params = ((HashMap)localObject6);
          if (paramMessageObject2 != null)
          {
            paramString2 = paramTL_game;
            if (paramMessageObject2.resendAsIs) {}
          }
          else
          {
            paramString2 = paramTL_game;
            paramTL_game.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            paramString2 = paramTL_game;
            if (!(localInputPeer instanceof TLRPC.TL_inputPeerChannel)) {
              break label3511;
            }
            if (m != 0)
            {
              paramString2 = paramTL_game;
              paramTL_game.views = 1;
              paramString2 = paramTL_game;
              paramTL_game.flags |= 0x400;
            }
            paramString2 = paramTL_game;
            paramString1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(localInputPeer.channel_id));
            if (paramString1 != null)
            {
              paramString2 = paramTL_game;
              if (!paramString1.megagroup) {
                break label3471;
              }
              paramString2 = paramTL_game;
              paramTL_game.flags |= 0x80000000;
              paramString2 = paramTL_game;
              paramTL_game.unread = true;
            }
          }
          paramString2 = paramTL_game;
          paramTL_game.flags |= 0x200;
          paramString2 = paramTL_game;
          paramTL_game.dialog_id = paramLong;
          if (paramMessageObject1 != null)
          {
            if (localObject4 == null) {
              break label3523;
            }
            paramString2 = paramTL_game;
            if (paramMessageObject1.messageOwner.random_id == 0L) {
              break label3523;
            }
            paramString2 = paramTL_game;
            paramTL_game.reply_to_random_id = paramMessageObject1.messageOwner.random_id;
            paramString2 = paramTL_game;
            paramTL_game.flags |= 0x8;
            paramString2 = paramTL_game;
            paramTL_game.reply_to_msg_id = paramMessageObject1.getId();
          }
          if ((paramReplyMarkup != null) && (localObject4 == null))
          {
            paramString2 = paramTL_game;
            paramTL_game.flags |= 0x40;
            paramString2 = paramTL_game;
            paramTL_game.reply_markup = paramReplyMarkup;
          }
          if (j == 0) {
            break label4261;
          }
          if (k != 1) {
            break label4167;
          }
          paramString2 = paramTL_game;
          if (this.currentChatInfo != null) {
            break label3542;
          }
          paramString2 = paramTL_game;
          MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(paramTL_game);
          paramString2 = paramTL_game;
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramTL_game.id) });
          paramString2 = paramTL_game;
          processSentMessage(paramTL_game.id);
        }
        catch (Exception paramString1)
        {
          paramMessageMedia = null;
          paramTL_game = paramString2;
          paramString2 = paramMessageMedia;
        }
        FileLog.e(paramString1);
        MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(paramTL_game);
        if (paramString2 != null) {
          paramString2.messageOwner.send_state = 2;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramTL_game.id) });
        processSentMessage(paramTL_game.id);
        break;
        m = 0;
        localObject4 = localObject2;
        continue;
        paramString2 = (String)localObject1;
        Object localObject10;
        if (paramMessageObject2.type == 0)
        {
          paramString2 = (String)localObject1;
          if ((paramMessageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
          {
            i = 0;
            localObject10 = paramUser;
            localObject11 = paramTL_photo;
            localObject5 = paramMessageMedia;
            localObject2 = paramString1;
          }
        }
        for (;;)
        {
          paramTL_game = (TLRPC.TL_game)localObject1;
          n = i;
          localObject9 = localObject2;
          localObject8 = localObject5;
          paramTL_photo = (TLRPC.TL_photo)localObject11;
          paramUser = (TLRPC.User)localObject10;
          localTL_document = paramTL_document;
          localObject7 = paramWebPage;
          localObject6 = paramHashMap;
          if (paramHashMap == null) {
            break;
          }
          paramTL_game = (TLRPC.TL_game)localObject1;
          n = i;
          localObject9 = localObject2;
          localObject8 = localObject5;
          paramTL_photo = (TLRPC.TL_photo)localObject11;
          paramUser = (TLRPC.User)localObject10;
          localTL_document = paramTL_document;
          localObject7 = paramWebPage;
          localObject6 = paramHashMap;
          paramString2 = (String)localObject1;
          if (!paramHashMap.containsKey("query_id")) {
            break;
          }
          n = 9;
          paramTL_game = (TLRPC.TL_game)localObject1;
          localObject9 = localObject2;
          localObject8 = localObject5;
          paramTL_photo = (TLRPC.TL_photo)localObject11;
          paramUser = (TLRPC.User)localObject10;
          localTL_document = paramTL_document;
          localObject7 = paramWebPage;
          localObject6 = paramHashMap;
          break;
          paramString2 = (String)localObject1;
          paramString1 = ((TLRPC.Message)localObject1).message;
          break label907;
          paramString2 = (String)localObject1;
          if (paramMessageObject2.type == 4)
          {
            paramString2 = (String)localObject1;
            localObject5 = ((TLRPC.Message)localObject1).media;
            i = 1;
            localObject2 = paramString1;
            localObject11 = paramTL_photo;
            localObject10 = paramUser;
          }
          else
          {
            paramString2 = (String)localObject1;
            if (paramMessageObject2.type == 1)
            {
              paramString2 = (String)localObject1;
              localObject11 = (TLRPC.TL_photo)((TLRPC.Message)localObject1).media.photo;
              i = 2;
              localObject2 = paramString1;
              localObject5 = paramMessageMedia;
              localObject10 = paramUser;
            }
            else
            {
              paramString2 = (String)localObject1;
              if (paramMessageObject2.type != 3)
              {
                paramString2 = (String)localObject1;
                if ((paramMessageObject2.type != 5) && (paramVideoEditedInfo == null)) {}
              }
              else
              {
                i = 3;
                paramString2 = (String)localObject1;
                paramTL_document = (TLRPC.TL_document)((TLRPC.Message)localObject1).media.document;
                localObject2 = paramString1;
                localObject5 = paramMessageMedia;
                localObject11 = paramTL_photo;
                localObject10 = paramUser;
                continue;
              }
              paramString2 = (String)localObject1;
              if (paramMessageObject2.type == 12)
              {
                paramString2 = (String)localObject1;
                localObject10 = new TLRPC.TL_userRequest_old2();
              }
            }
          }
          try
          {
            ((TLRPC.User)localObject10).phone = ((TLRPC.Message)localObject1).media.phone_number;
            ((TLRPC.User)localObject10).first_name = ((TLRPC.Message)localObject1).media.first_name;
            ((TLRPC.User)localObject10).last_name = ((TLRPC.Message)localObject1).media.last_name;
            ((TLRPC.User)localObject10).id = ((TLRPC.Message)localObject1).media.user_id;
            i = 6;
            localObject2 = paramString1;
            localObject5 = paramMessageMedia;
            localObject11 = paramTL_photo;
          }
          catch (Exception paramString1)
          {
            paramString2 = null;
            paramTL_game = (TLRPC.TL_game)localObject1;
          }
          paramString2 = (String)localObject1;
          if (paramMessageObject2.type != 8)
          {
            paramString2 = (String)localObject1;
            if (paramMessageObject2.type != 9)
            {
              paramString2 = (String)localObject1;
              if (paramMessageObject2.type != 13)
              {
                paramString2 = (String)localObject1;
                if (paramMessageObject2.type != 14) {
                  break label1397;
                }
              }
            }
          }
          paramString2 = (String)localObject1;
          paramTL_document = (TLRPC.TL_document)((TLRPC.Message)localObject1).media.document;
          i = 7;
          localObject2 = paramString1;
          localObject5 = paramMessageMedia;
          localObject11 = paramTL_photo;
          localObject10 = paramUser;
          continue;
          label1397:
          paramString2 = (String)localObject1;
          localObject2 = paramString1;
          localObject5 = paramMessageMedia;
          localObject11 = paramTL_photo;
          localObject10 = paramUser;
          if (paramMessageObject2.type == 2)
          {
            paramString2 = (String)localObject1;
            paramTL_document = (TLRPC.TL_document)((TLRPC.Message)localObject1).media.document;
            i = 8;
            localObject2 = paramString1;
            localObject5 = paramMessageMedia;
            localObject11 = paramTL_photo;
            localObject10 = paramUser;
          }
        }
      }
    }
    if (paramString1 != null) {
      if (localObject4 != null)
      {
        paramString2 = (String)localObject1;
        paramTL_game = new org/telegram/tgnet/TLRPC$TL_message_secret;
        paramString2 = (String)localObject1;
        paramTL_game.<init>();
        label1485:
        localObject1 = paramWebPage;
        if (localObject4 != null)
        {
          paramString2 = paramTL_game;
          localObject1 = paramWebPage;
          if ((paramWebPage instanceof TLRPC.TL_webPagePending))
          {
            paramString2 = paramTL_game;
            if (paramWebPage.url == null) {
              break label1822;
            }
            paramString2 = paramTL_game;
            localObject1 = new org/telegram/tgnet/TLRPC$TL_webPageUrlPending;
            paramString2 = paramTL_game;
            ((TLRPC.TL_webPageUrlPending)localObject1).<init>();
            paramString2 = paramTL_game;
            ((TLRPC.WebPage)localObject1).url = paramWebPage.url;
          }
        }
        label1549:
        if (localObject1 != null) {
          break label1828;
        }
        paramString2 = paramTL_game;
        paramWebPage = new org/telegram/tgnet/TLRPC$TL_messageMediaEmpty;
        paramString2 = paramTL_game;
        paramWebPage.<init>();
        paramString2 = paramTL_game;
        paramTL_game.media = paramWebPage;
        label1580:
        if (paramHashMap == null) {
          break label1870;
        }
        paramString2 = paramTL_game;
        if (!paramHashMap.containsKey("query_id")) {
          break label1870;
        }
        i = 9;
        label1603:
        paramString2 = paramTL_game;
        paramTL_game.message = paramString1;
        localObject2 = paramHashMap;
        localObject11 = localObject1;
      }
    }
    for (;;)
    {
      label1620:
      if (paramArrayList != null)
      {
        paramString2 = paramTL_game;
        if (!paramArrayList.isEmpty())
        {
          paramString2 = paramTL_game;
          paramTL_game.entities = paramArrayList;
          paramString2 = paramTL_game;
          paramTL_game.flags |= 0x80;
        }
      }
      if (str1 != null)
      {
        paramString2 = paramTL_game;
        paramTL_game.message = str1;
        label1678:
        paramString2 = paramTL_game;
        if (paramTL_game.attachPath == null)
        {
          paramString2 = paramTL_game;
          paramTL_game.attachPath = "";
        }
        paramString2 = paramTL_game;
        n = UserConfig.getInstance(this.currentAccount).getNewMessageId();
        paramString2 = paramTL_game;
        paramTL_game.id = n;
        paramString2 = paramTL_game;
        paramTL_game.local_id = n;
        paramString2 = paramTL_game;
        paramTL_game.out = true;
        if ((m == 0) || (localInputPeer == null)) {
          break label3405;
        }
        paramString2 = paramTL_game;
        paramTL_game.from_id = (-localInputPeer.channel_id);
      }
      for (;;)
      {
        paramString2 = paramTL_game;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        n = i;
        localObject9 = paramString1;
        localObject8 = paramMessageMedia;
        localTL_document = paramTL_document;
        localObject7 = localObject11;
        localObject6 = localObject2;
        break;
        paramString2 = (String)localObject1;
        paramTL_game = new TLRPC.TL_message();
        break label1485;
        label1822:
        localObject1 = null;
        break label1549;
        label1828:
        paramString2 = paramTL_game;
        paramWebPage = new org/telegram/tgnet/TLRPC$TL_messageMediaWebPage;
        paramString2 = paramTL_game;
        paramWebPage.<init>();
        paramString2 = paramTL_game;
        paramTL_game.media = paramWebPage;
        paramString2 = paramTL_game;
        paramTL_game.media.webpage = ((TLRPC.WebPage)localObject1);
        break label1580;
        label1870:
        i = 0;
        break label1603;
        if (paramMessageMedia != null)
        {
          if (localObject4 != null)
          {
            paramString2 = (String)localObject1;
            paramTL_game = new org/telegram/tgnet/TLRPC$TL_message_secret;
            paramString2 = (String)localObject1;
            paramTL_game.<init>();
          }
          for (;;)
          {
            paramString2 = paramTL_game;
            paramTL_game.media = paramMessageMedia;
            if (paramHashMap == null) {
              break label1959;
            }
            paramString2 = paramTL_game;
            if (!paramHashMap.containsKey("query_id")) {
              break label1959;
            }
            i = 9;
            localObject11 = paramWebPage;
            localObject2 = paramHashMap;
            break;
            paramString2 = (String)localObject1;
            paramTL_game = new TLRPC.TL_message();
          }
          label1959:
          i = 1;
          localObject11 = paramWebPage;
          localObject2 = paramHashMap;
          break label1620;
        }
        if (paramTL_photo != null)
        {
          if (localObject4 != null)
          {
            paramString2 = (String)localObject1;
            paramTL_game = new org/telegram/tgnet/TLRPC$TL_message_secret;
            paramString2 = (String)localObject1;
            paramTL_game.<init>();
            label1999:
            paramString2 = paramTL_game;
            localObject1 = new org/telegram/tgnet/TLRPC$TL_messageMediaPhoto;
            paramString2 = paramTL_game;
            ((TLRPC.TL_messageMediaPhoto)localObject1).<init>();
            paramString2 = paramTL_game;
            paramTL_game.media = ((TLRPC.MessageMedia)localObject1);
            paramString2 = paramTL_game;
            localObject1 = paramTL_game.media;
            paramString2 = paramTL_game;
            ((TLRPC.MessageMedia)localObject1).flags |= 0x3;
            if (paramArrayList != null)
            {
              paramString2 = paramTL_game;
              paramTL_game.entities = paramArrayList;
            }
            if (paramInt != 0)
            {
              paramString2 = paramTL_game;
              paramTL_game.media.ttl_seconds = paramInt;
              paramString2 = paramTL_game;
              paramTL_game.ttl = paramInt;
              paramString2 = paramTL_game;
              localObject1 = paramTL_game.media;
              paramString2 = paramTL_game;
              ((TLRPC.MessageMedia)localObject1).flags |= 0x4;
            }
            paramString2 = paramTL_game;
            paramTL_game.media.photo = paramTL_photo;
            if (paramHashMap == null) {
              break label2220;
            }
            paramString2 = paramTL_game;
            if (!paramHashMap.containsKey("query_id")) {
              break label2220;
            }
          }
          label2220:
          for (i = 9;; i = 2)
          {
            if (paramString3 == null) {
              break label2226;
            }
            paramString2 = paramTL_game;
            if (paramString3.length() <= 0) {
              break label2226;
            }
            paramString2 = paramTL_game;
            if (!paramString3.startsWith("http")) {
              break label2226;
            }
            paramString2 = paramTL_game;
            paramTL_game.attachPath = paramString3;
            localObject11 = paramWebPage;
            localObject2 = paramHashMap;
            break;
            paramString2 = (String)localObject1;
            paramTL_game = new TLRPC.TL_message();
            break label1999;
          }
          label2226:
          paramString2 = paramTL_game;
          paramTL_game.attachPath = FileLoader.getPathToAttach(((TLRPC.PhotoSize)paramTL_photo.sizes.get(paramTL_photo.sizes.size() - 1)).location, true).toString();
          localObject11 = paramWebPage;
          localObject2 = paramHashMap;
          break label1620;
        }
        if (paramTL_game != null)
        {
          paramString2 = (String)localObject1;
          localObject1 = new TLRPC.TL_message();
        }
        try
        {
          paramString2 = new org/telegram/tgnet/TLRPC$TL_messageMediaGame;
          paramString2.<init>();
          ((TLRPC.Message)localObject1).media = paramString2;
          ((TLRPC.Message)localObject1).media.game = paramTL_game;
          if (paramHashMap == null) {
            break label11066;
          }
          bool = paramHashMap.containsKey("query_id");
          if (!bool) {
            break label11066;
          }
          i = 9;
          paramTL_game = (TLRPC.TL_game)localObject1;
          localObject11 = paramWebPage;
          localObject2 = paramHashMap;
        }
        catch (Exception paramString1)
        {
          boolean bool;
          label2599:
          int i1;
          paramString2 = null;
          paramTL_game = (TLRPC.TL_game)localObject1;
        }
        if (paramUser != null)
        {
          if (localObject4 != null)
          {
            paramString2 = (String)localObject1;
            paramTL_game = new org/telegram/tgnet/TLRPC$TL_message_secret;
            paramString2 = (String)localObject1;
            paramTL_game.<init>();
          }
          for (;;)
          {
            paramString2 = paramTL_game;
            localObject1 = new org/telegram/tgnet/TLRPC$TL_messageMediaContact;
            paramString2 = paramTL_game;
            ((TLRPC.TL_messageMediaContact)localObject1).<init>();
            paramString2 = paramTL_game;
            paramTL_game.media = ((TLRPC.MessageMedia)localObject1);
            paramString2 = paramTL_game;
            paramTL_game.media.phone_number = paramUser.phone;
            paramString2 = paramTL_game;
            paramTL_game.media.first_name = paramUser.first_name;
            paramString2 = paramTL_game;
            paramTL_game.media.last_name = paramUser.last_name;
            paramString2 = paramTL_game;
            paramTL_game.media.user_id = paramUser.id;
            paramString2 = paramTL_game;
            if (paramTL_game.media.first_name == null)
            {
              paramString2 = paramTL_game;
              paramTL_game.media.first_name = "";
              paramString2 = paramTL_game;
              paramUser.first_name = "";
            }
            paramString2 = paramTL_game;
            if (paramTL_game.media.last_name == null)
            {
              paramString2 = paramTL_game;
              paramTL_game.media.last_name = "";
              paramString2 = paramTL_game;
              paramUser.last_name = "";
            }
            if (paramHashMap == null) {
              break label2599;
            }
            paramString2 = paramTL_game;
            if (!paramHashMap.containsKey("query_id")) {
              break label2599;
            }
            i = 9;
            localObject11 = paramWebPage;
            localObject2 = paramHashMap;
            break;
            paramString2 = (String)localObject1;
            paramTL_game = new TLRPC.TL_message();
          }
          i = 6;
          localObject11 = paramWebPage;
          localObject2 = paramHashMap;
          break label1620;
        }
        paramTL_game = paramString2;
        localObject11 = paramWebPage;
        localObject2 = paramHashMap;
        if (paramTL_document == null) {
          break label1620;
        }
        if (localObject4 != null)
        {
          paramString2 = (String)localObject1;
          paramTL_game = new org/telegram/tgnet/TLRPC$TL_message_secret;
          paramString2 = (String)localObject1;
          paramTL_game.<init>();
          localObject1 = paramTL_game;
          label2655:
          paramString2 = (String)localObject1;
          paramTL_game = new org/telegram/tgnet/TLRPC$TL_messageMediaDocument;
          paramString2 = (String)localObject1;
          paramTL_game.<init>();
          paramString2 = (String)localObject1;
          ((TLRPC.Message)localObject1).media = paramTL_game;
          paramString2 = (String)localObject1;
          paramTL_game = ((TLRPC.Message)localObject1).media;
          paramString2 = (String)localObject1;
          paramTL_game.flags |= 0x3;
          if (paramInt != 0)
          {
            paramString2 = (String)localObject1;
            ((TLRPC.Message)localObject1).media.ttl_seconds = paramInt;
            paramString2 = (String)localObject1;
            ((TLRPC.Message)localObject1).ttl = paramInt;
            paramString2 = (String)localObject1;
            paramTL_game = ((TLRPC.Message)localObject1).media;
            paramString2 = (String)localObject1;
            paramTL_game.flags |= 0x4;
          }
          paramString2 = (String)localObject1;
          ((TLRPC.Message)localObject1).media.document = paramTL_document;
          if (paramHashMap == null) {
            break label3187;
          }
          paramString2 = (String)localObject1;
          if (!paramHashMap.containsKey("query_id")) {
            break label3187;
          }
          n = 9;
          label2795:
          localObject5 = paramHashMap;
          if (paramVideoEditedInfo != null)
          {
            paramString2 = (String)localObject1;
            paramTL_game = paramVideoEditedInfo.getString();
            localObject5 = paramHashMap;
            if (paramHashMap == null)
            {
              paramString2 = (String)localObject1;
              localObject5 = new java/util/HashMap;
              paramString2 = (String)localObject1;
              ((HashMap)localObject5).<init>();
            }
            paramString2 = (String)localObject1;
            ((HashMap)localObject5).put("ve", paramTL_game);
          }
          if (localObject4 == null) {
            break label3245;
          }
          paramString2 = (String)localObject1;
          if (paramTL_document.dc_id <= 0) {
            break label3245;
          }
          paramString2 = (String)localObject1;
          if (MessageObject.isStickerDocument(paramTL_document)) {
            break label3245;
          }
          paramString2 = (String)localObject1;
          ((TLRPC.Message)localObject1).attachPath = FileLoader.getPathToAttach(paramTL_document).toString();
          label2896:
          paramTL_game = (TLRPC.TL_game)localObject1;
          i = n;
          localObject11 = paramWebPage;
          localObject2 = localObject5;
          if (localObject4 == null) {
            break label1620;
          }
          paramString2 = (String)localObject1;
          paramTL_game = (TLRPC.TL_game)localObject1;
          i = n;
          localObject11 = paramWebPage;
          localObject2 = localObject5;
          if (!MessageObject.isStickerDocument(paramTL_document)) {
            break label1620;
          }
        }
        for (i1 = 0;; i1++)
        {
          paramString2 = (String)localObject1;
          paramTL_game = (TLRPC.TL_game)localObject1;
          i = n;
          localObject11 = paramWebPage;
          localObject2 = localObject5;
          if (i1 >= paramTL_document.attributes.size()) {
            break;
          }
          paramString2 = (String)localObject1;
          paramTL_game = (TLRPC.DocumentAttribute)paramTL_document.attributes.get(i1);
          paramString2 = (String)localObject1;
          if ((paramTL_game instanceof TLRPC.TL_documentAttributeSticker))
          {
            paramString2 = (String)localObject1;
            paramTL_document.attributes.remove(i1);
            paramString2 = (String)localObject1;
            paramHashMap = new org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_layer55;
            paramString2 = (String)localObject1;
            paramHashMap.<init>();
            paramString2 = (String)localObject1;
            paramTL_document.attributes.add(paramHashMap);
            paramString2 = (String)localObject1;
            paramHashMap.alt = paramTL_game.alt;
            paramString2 = (String)localObject1;
            if (paramTL_game.stickerset != null)
            {
              paramString2 = (String)localObject1;
              if ((paramTL_game.stickerset instanceof TLRPC.TL_inputStickerSetShortName)) {
                paramString2 = (String)localObject1;
              }
              for (paramTL_game = paramTL_game.stickerset.short_name;; paramTL_game = DataQuery.getInstance(this.currentAccount).getStickerSetName(paramTL_game.stickerset.id))
              {
                paramString2 = (String)localObject1;
                if (TextUtils.isEmpty(paramTL_game)) {
                  break label3284;
                }
                paramString2 = (String)localObject1;
                localObject2 = new org/telegram/tgnet/TLRPC$TL_inputStickerSetShortName;
                paramString2 = (String)localObject1;
                ((TLRPC.TL_inputStickerSetShortName)localObject2).<init>();
                paramString2 = (String)localObject1;
                paramHashMap.stickerset = ((TLRPC.InputStickerSet)localObject2);
                paramString2 = (String)localObject1;
                paramHashMap.stickerset.short_name = paramTL_game;
                paramTL_game = (TLRPC.TL_game)localObject1;
                i = n;
                localObject11 = paramWebPage;
                localObject2 = localObject5;
                break;
                paramString2 = (String)localObject1;
                localObject1 = new TLRPC.TL_message();
                break label2655;
                label3187:
                paramString2 = (String)localObject1;
                if (!MessageObject.isVideoDocument(paramTL_document))
                {
                  paramString2 = (String)localObject1;
                  if ((!MessageObject.isRoundVideoDocument(paramTL_document)) && (paramVideoEditedInfo == null)) {}
                }
                else
                {
                  n = 3;
                  break label2795;
                }
                paramString2 = (String)localObject1;
                if (MessageObject.isVoiceDocument(paramTL_document))
                {
                  n = 8;
                  break label2795;
                }
                n = 7;
                break label2795;
                label3245:
                paramString2 = (String)localObject1;
                ((TLRPC.Message)localObject1).attachPath = paramString3;
                break label2896;
                paramString2 = (String)localObject1;
              }
              label3284:
              paramString2 = (String)localObject1;
              paramTL_game = new org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty;
              paramString2 = (String)localObject1;
              paramTL_game.<init>();
              paramString2 = (String)localObject1;
              paramHashMap.stickerset = paramTL_game;
              paramTL_game = (TLRPC.TL_game)localObject1;
              i = n;
              localObject11 = paramWebPage;
              localObject2 = localObject5;
              break;
            }
            paramString2 = (String)localObject1;
            paramTL_game = new org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty;
            paramString2 = (String)localObject1;
            paramTL_game.<init>();
            paramString2 = (String)localObject1;
            paramHashMap.stickerset = paramTL_game;
            paramTL_game = (TLRPC.TL_game)localObject1;
            i = n;
            localObject11 = paramWebPage;
            localObject2 = localObject5;
            break;
          }
        }
        paramString2 = paramTL_game;
        if (paramTL_game.message != null) {
          break label1678;
        }
        paramString2 = paramTL_game;
        paramTL_game.message = "";
        break label1678;
        label3405:
        paramString2 = paramTL_game;
        paramTL_game.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
        paramString2 = paramTL_game;
        paramTL_game.flags |= 0x100;
      }
      label3443:
      paramString2 = paramTL_game;
      paramTL_game.via_bot_id = Utilities.parseInt((String)((HashMap)localObject6).get("bot")).intValue();
      break label412;
      label3471:
      paramString2 = paramTL_game;
      paramTL_game.post = true;
      paramString2 = paramTL_game;
      if (!paramString1.signatures) {
        break label577;
      }
      paramString2 = paramTL_game;
      paramTL_game.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
      break label577;
      label3511:
      paramString2 = paramTL_game;
      paramTL_game.unread = true;
      break label577;
      label3523:
      paramString2 = paramTL_game;
      paramTL_game.flags |= 0x8;
      break label662;
      label3542:
      paramString2 = paramTL_game;
      paramMessageMedia = new java/util/ArrayList;
      paramString2 = paramTL_game;
      paramMessageMedia.<init>();
      try
      {
        paramString1 = this.currentChatInfo.participants.participants.iterator();
        while (paramString1.hasNext())
        {
          paramString2 = (TLRPC.ChatParticipant)paramString1.next();
          paramString2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramString2.user_id));
          paramString2 = MessagesController.getInstance(this.currentAccount).getInputUser(paramString2);
          if (paramString2 != null) {
            paramMessageMedia.add(paramString2);
          }
        }
      }
      catch (Exception paramString1)
      {
        paramString2 = null;
      }
      paramString1 = new org/telegram/tgnet/TLRPC$TL_peerChat;
      paramString1.<init>();
      paramTL_game.to_id = paramString1;
      paramTL_game.to_id.chat_id = j;
      label3662:
      if (k != 1)
      {
        paramString2 = paramTL_game;
        if (!MessageObject.isVoiceMessage(paramTL_game))
        {
          paramString2 = paramTL_game;
          if (!MessageObject.isRoundVideoMessage(paramTL_game)) {}
        }
        else
        {
          paramString2 = paramTL_game;
          paramTL_game.media_unread = true;
        }
      }
      paramString2 = paramTL_game;
      paramTL_game.send_state = 1;
      paramString2 = paramTL_game;
      paramWebPage = new MessageObject(this.currentAccount, paramTL_game, true);
      for (;;)
      {
        long l1;
        long l2;
        try
        {
          paramWebPage.replyMessageObject = paramMessageObject1;
          if ((!paramWebPage.isForwarded()) && ((paramWebPage.type == 3) || (paramVideoEditedInfo != null) || (paramWebPage.type == 2)) && (!TextUtils.isEmpty(paramTL_game.attachPath))) {
            paramWebPage.attachPathExists = true;
          }
          paramTL_document = paramVideoEditedInfo;
          if (paramWebPage.videoEditedInfo != null)
          {
            paramTL_document = paramVideoEditedInfo;
            if (paramVideoEditedInfo == null) {
              paramTL_document = paramWebPage.videoEditedInfo;
            }
          }
          l1 = 0L;
          i = 0;
          l2 = l1;
          if (localObject6 != null)
          {
            paramString1 = (String)((HashMap)localObject6).get("groupId");
            if (paramString1 != null)
            {
              l1 = Utilities.parseLong(paramString1).longValue();
              paramTL_game.grouped_id = l1;
              paramTL_game.flags |= 0x20000;
            }
            if (((HashMap)localObject6).get("final") != null)
            {
              i = 1;
              l2 = l1;
            }
          }
          else
          {
            if (l2 != 0L) {
              continue;
            }
            paramString2 = new java/util/ArrayList;
            paramString2.<init>();
            paramString2.add(paramWebPage);
            paramString1 = new java/util/ArrayList;
            paramString1.<init>();
            paramString1.add(paramTL_game);
            MessagesStorage.getInstance(this.currentAccount).putMessages(paramString1, false, true, false, 0);
            MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(paramLong, paramString2);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            paramString1 = null;
            paramVideoEditedInfo = paramString1;
          }
          try
          {
            if ((BuildVars.LOGS_ENABLED) && (localInputPeer != null))
            {
              paramVideoEditedInfo = paramString1;
              paramString2 = new java/lang/StringBuilder;
              paramVideoEditedInfo = paramString1;
              paramString2.<init>();
              paramVideoEditedInfo = paramString1;
              FileLog.d("send message user_id = " + localInputPeer.user_id + " chat_id = " + localInputPeer.chat_id + " channel_id = " + localInputPeer.channel_id + " access_hash = " + localInputPeer.access_hash);
            }
            if ((n == 0) || ((n == 9) && (localObject9 != null) && (localObject4 != null)))
            {
              if (localObject4 == null)
              {
                if (paramMessageMedia != null)
                {
                  paramVideoEditedInfo = paramString1;
                  paramString2 = new org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast;
                  paramVideoEditedInfo = paramString1;
                  paramString2.<init>();
                  paramVideoEditedInfo = paramString1;
                  paramTL_photo = new java/util/ArrayList;
                  paramVideoEditedInfo = paramString1;
                  paramTL_photo.<init>();
                  paramInt = 0;
                  paramVideoEditedInfo = paramString1;
                  if (paramInt < paramMessageMedia.size())
                  {
                    paramVideoEditedInfo = paramString1;
                    paramTL_photo.add(Long.valueOf(Utilities.random.nextLong()));
                    paramInt++;
                    continue;
                    label4167:
                    paramString2 = paramTL_game;
                    paramTL_game.to_id = MessagesController.getInstance(this.currentAccount).getPeer(j);
                    paramMessageMedia = (TLRPC.MessageMedia)localObject3;
                    if (j <= 0) {
                      break label3662;
                    }
                    paramString2 = paramTL_game;
                    paramString1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(j));
                    if (paramString1 == null)
                    {
                      paramString2 = paramTL_game;
                      processSentMessage(paramTL_game.id);
                      break;
                    }
                    paramString2 = paramTL_game;
                    paramMessageMedia = (TLRPC.MessageMedia)localObject3;
                    if (!paramString1.bot) {
                      break label3662;
                    }
                    paramString2 = paramTL_game;
                    paramTL_game.unread = false;
                    paramMessageMedia = (TLRPC.MessageMedia)localObject3;
                    break label3662;
                    label4261:
                    paramString2 = paramTL_game;
                    paramString1 = new org/telegram/tgnet/TLRPC$TL_peerUser;
                    paramString2 = paramTL_game;
                    paramString1.<init>();
                    paramString2 = paramTL_game;
                    paramTL_game.to_id = paramString1;
                    paramString2 = paramTL_game;
                    if (((TLRPC.EncryptedChat)localObject4).participant_id == UserConfig.getInstance(this.currentAccount).getClientUserId())
                    {
                      paramString2 = paramTL_game;
                      paramTL_game.to_id.user_id = ((TLRPC.EncryptedChat)localObject4).admin_id;
                      if (paramInt == 0) {
                        continue;
                      }
                      paramString2 = paramTL_game;
                      paramTL_game.ttl = paramInt;
                      paramString2 = paramTL_game;
                      paramMessageMedia = (TLRPC.MessageMedia)localObject3;
                      if (paramTL_game.ttl == 0) {
                        break label3662;
                      }
                      paramString2 = paramTL_game;
                      paramMessageMedia = (TLRPC.MessageMedia)localObject3;
                      if (paramTL_game.media.document == null) {
                        break label3662;
                      }
                      paramString2 = paramTL_game;
                      if (!MessageObject.isVoiceMessage(paramTL_game)) {
                        continue;
                      }
                      i1 = 0;
                      m = 0;
                      paramString2 = paramTL_game;
                      i = i1;
                      if (m < paramTL_game.media.document.attributes.size())
                      {
                        paramString2 = paramTL_game;
                        paramString1 = (TLRPC.DocumentAttribute)paramTL_game.media.document.attributes.get(m);
                        paramString2 = paramTL_game;
                        if (!(paramString1 instanceof TLRPC.TL_documentAttributeAudio)) {
                          continue;
                        }
                        paramString2 = paramTL_game;
                        i = paramString1.duration;
                      }
                      paramString2 = paramTL_game;
                      paramTL_game.ttl = Math.max(paramTL_game.ttl, i + 1);
                      paramMessageMedia = (TLRPC.MessageMedia)localObject3;
                      break label3662;
                    }
                    paramString2 = paramTL_game;
                    paramTL_game.to_id.user_id = ((TLRPC.EncryptedChat)localObject4).participant_id;
                    continue;
                    paramString2 = paramTL_game;
                    paramTL_game.ttl = ((TLRPC.EncryptedChat)localObject4).ttl;
                    paramString2 = paramTL_game;
                    if (paramTL_game.ttl == 0) {
                      continue;
                    }
                    paramString2 = paramTL_game;
                    if (paramTL_game.media == null) {
                      continue;
                    }
                    paramString2 = paramTL_game;
                    paramTL_game.media.ttl_seconds = paramTL_game.ttl;
                    paramString2 = paramTL_game;
                    paramString1 = paramTL_game.media;
                    paramString2 = paramTL_game;
                    paramString1.flags |= 0x4;
                    continue;
                    m++;
                    continue;
                    paramString2 = paramTL_game;
                    if (!MessageObject.isVideoMessage(paramTL_game))
                    {
                      paramString2 = paramTL_game;
                      paramMessageMedia = (TLRPC.MessageMedia)localObject3;
                      if (!MessageObject.isRoundVideoMessage(paramTL_game)) {
                        break label3662;
                      }
                    }
                    i1 = 0;
                    i = 0;
                    paramString2 = paramTL_game;
                    m = i1;
                    if (i < paramTL_game.media.document.attributes.size())
                    {
                      paramString2 = paramTL_game;
                      paramString1 = (TLRPC.DocumentAttribute)paramTL_game.media.document.attributes.get(i);
                      paramString2 = paramTL_game;
                      if ((paramString1 instanceof TLRPC.TL_documentAttributeVideo))
                      {
                        paramString2 = paramTL_game;
                        m = paramString1.duration;
                      }
                    }
                    else
                    {
                      paramString2 = paramTL_game;
                      paramTL_game.ttl = Math.max(paramTL_game.ttl, m + 1);
                      paramMessageMedia = (TLRPC.MessageMedia)localObject3;
                      break label3662;
                    }
                    i++;
                    continue;
                    i = 0;
                    l2 = l1;
                    continue;
                    paramString1 = new java/lang/StringBuilder;
                    paramString1.<init>();
                    paramString1 = "group_" + l2;
                    paramString1 = (ArrayList)this.delayedMessages.get(paramString1);
                    if (paramString1 == null) {
                      break label11061;
                    }
                    paramString1 = (DelayedMessage)paramString1.get(0);
                    if (paramString1 != null) {
                      break label11058;
                    }
                    paramVideoEditedInfo = paramString1;
                    paramString1 = new DelayedMessage(paramLong);
                    paramString1.type = 4;
                    paramString1.groupId = l2;
                    paramString2 = new java/util/ArrayList;
                    paramString2.<init>();
                    paramString1.messageObjects = paramString2;
                    paramString2 = new java/util/ArrayList;
                    paramString2.<init>();
                    paramString1.messages = paramString2;
                    paramString2 = new java/util/ArrayList;
                    paramString2.<init>();
                    paramString1.originalPaths = paramString2;
                    paramString2 = new java/util/HashMap;
                    paramString2.<init>();
                    paramString1.extraHashMap = paramString2;
                    paramString1.encryptedChat = ((TLRPC.EncryptedChat)localObject4);
                    if (i != 0) {
                      paramString1.finalGroupMessage = paramTL_game.id;
                    }
                    continue;
                  }
                  paramVideoEditedInfo = paramString1;
                  paramString2.message = ((String)localObject9);
                  paramVideoEditedInfo = paramString1;
                  paramString2.contacts = paramMessageMedia;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia = new org/telegram/tgnet/TLRPC$TL_inputMediaEmpty;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.<init>();
                  paramVideoEditedInfo = paramString1;
                  paramString2.media = paramMessageMedia;
                  paramVideoEditedInfo = paramString1;
                  paramString2.random_id = paramTL_photo;
                  paramVideoEditedInfo = paramString1;
                  performSendMessageRequest(paramString2, paramWebPage, null);
                  break;
                }
                paramVideoEditedInfo = paramString1;
                paramString2 = new org/telegram/tgnet/TLRPC$TL_messages_sendMessage;
                paramVideoEditedInfo = paramString1;
                paramString2.<init>();
                paramVideoEditedInfo = paramString1;
                paramString2.message = ((String)localObject9);
                if (paramMessageObject2 == null)
                {
                  bool = true;
                  paramVideoEditedInfo = paramString1;
                  paramString2.clear_draft = bool;
                  paramVideoEditedInfo = paramString1;
                  if ((paramTL_game.to_id instanceof TLRPC.TL_peerChannel))
                  {
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia = MessagesController.getNotificationsSettings(this.currentAccount);
                    paramVideoEditedInfo = paramString1;
                    paramTL_photo = new java/lang/StringBuilder;
                    paramVideoEditedInfo = paramString1;
                    paramTL_photo.<init>();
                    paramVideoEditedInfo = paramString1;
                    paramString2.silent = paramMessageMedia.getBoolean("silent_" + paramLong, false);
                  }
                  paramVideoEditedInfo = paramString1;
                  paramString2.peer = localInputPeer;
                  paramVideoEditedInfo = paramString1;
                  paramString2.random_id = paramTL_game.random_id;
                  paramVideoEditedInfo = paramString1;
                  if (paramTL_game.reply_to_msg_id != 0)
                  {
                    paramVideoEditedInfo = paramString1;
                    paramString2.flags |= 0x1;
                    paramVideoEditedInfo = paramString1;
                    paramString2.reply_to_msg_id = paramTL_game.reply_to_msg_id;
                  }
                  if (!paramBoolean)
                  {
                    paramVideoEditedInfo = paramString1;
                    paramString2.no_webpage = true;
                  }
                  if (paramArrayList != null)
                  {
                    paramVideoEditedInfo = paramString1;
                    if (!paramArrayList.isEmpty())
                    {
                      paramVideoEditedInfo = paramString1;
                      paramString2.entities = paramArrayList;
                      paramVideoEditedInfo = paramString1;
                      paramString2.flags |= 0x8;
                    }
                  }
                  paramVideoEditedInfo = paramString1;
                  performSendMessageRequest(paramString2, paramWebPage, null);
                  if (paramMessageObject2 == null)
                  {
                    paramVideoEditedInfo = paramString1;
                    DataQuery.getInstance(this.currentAccount).cleanDraft(paramLong, false);
                  }
                  break;
                }
                bool = false;
                continue;
              }
              paramVideoEditedInfo = paramString1;
              if (AndroidUtilities.getPeerLayerVersion(((TLRPC.EncryptedChat)localObject4).layer) >= 73)
              {
                paramVideoEditedInfo = paramString1;
                paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessage;
                paramVideoEditedInfo = paramString1;
                paramString2.<init>();
                paramVideoEditedInfo = paramString1;
                paramString2.ttl = paramTL_game.ttl;
                if (paramArrayList != null)
                {
                  paramVideoEditedInfo = paramString1;
                  if (!paramArrayList.isEmpty())
                  {
                    paramVideoEditedInfo = paramString1;
                    paramString2.entities = paramArrayList;
                    paramVideoEditedInfo = paramString1;
                    paramString2.flags |= 0x80;
                  }
                }
                paramVideoEditedInfo = paramString1;
                if (paramTL_game.reply_to_random_id != 0L)
                {
                  paramVideoEditedInfo = paramString1;
                  paramString2.reply_to_random_id = paramTL_game.reply_to_random_id;
                  paramVideoEditedInfo = paramString1;
                  paramString2.flags |= 0x8;
                }
                if (localObject6 != null)
                {
                  paramVideoEditedInfo = paramString1;
                  if (((HashMap)localObject6).get("bot_name") != null)
                  {
                    paramVideoEditedInfo = paramString1;
                    paramString2.via_bot_name = ((String)((HashMap)localObject6).get("bot_name"));
                    paramVideoEditedInfo = paramString1;
                    paramString2.flags |= 0x800;
                  }
                }
                paramVideoEditedInfo = paramString1;
                paramString2.random_id = paramTL_game.random_id;
                paramVideoEditedInfo = paramString1;
                paramString2.message = ((String)localObject9);
                if (localObject7 == null) {
                  continue;
                }
                paramVideoEditedInfo = paramString1;
                if (((TLRPC.WebPage)localObject7).url == null) {
                  continue;
                }
                paramVideoEditedInfo = paramString1;
                paramMessageMedia = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaWebPage;
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.<init>();
                paramVideoEditedInfo = paramString1;
                paramString2.media = paramMessageMedia;
                paramVideoEditedInfo = paramString1;
                paramString2.media.url = ((TLRPC.WebPage)localObject7).url;
                paramVideoEditedInfo = paramString1;
                paramString2.flags |= 0x200;
                paramVideoEditedInfo = paramString1;
                SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(paramString2, paramWebPage.messageOwner, (TLRPC.EncryptedChat)localObject4, null, null, paramWebPage);
                if (paramMessageObject2 == null)
                {
                  paramVideoEditedInfo = paramString1;
                  DataQuery.getInstance(this.currentAccount).cleanDraft(paramLong, false);
                }
                break;
              }
              paramVideoEditedInfo = paramString1;
              paramString2 = new TLRPC.TL_decryptedMessage_layer45();
              continue;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaEmpty;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.<init>();
              paramVideoEditedInfo = paramString1;
              paramString2.media = paramMessageMedia;
              continue;
            }
          }
          catch (Exception paramString1)
          {
            paramString2 = paramWebPage;
          }
          if (((n < 1) || (n > 3)) && ((n < 5) || (n > 8)) && ((n != 9) || (localObject4 == null))) {
            break label10396;
          }
          if (localObject4 != null) {
            break label7742;
          }
          paramString2 = null;
          if (n == 1)
          {
            paramVideoEditedInfo = paramString1;
            if ((localObject8 instanceof TLRPC.TL_messageMediaVenue))
            {
              paramVideoEditedInfo = paramString1;
              paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaVenue;
              paramVideoEditedInfo = paramString1;
              paramString2.<init>();
              paramVideoEditedInfo = paramString1;
              paramString2.address = ((TLRPC.MessageMedia)localObject8).address;
              paramVideoEditedInfo = paramString1;
              paramString2.title = ((TLRPC.MessageMedia)localObject8).title;
              paramVideoEditedInfo = paramString1;
              paramString2.provider = ((TLRPC.MessageMedia)localObject8).provider;
              paramVideoEditedInfo = paramString1;
              paramString2.venue_id = ((TLRPC.MessageMedia)localObject8).venue_id;
              paramVideoEditedInfo = paramString1;
              paramString2.venue_type = "";
              paramVideoEditedInfo = paramString1;
              paramUser = new org/telegram/tgnet/TLRPC$TL_inputGeoPoint;
              paramVideoEditedInfo = paramString1;
              paramUser.<init>();
              paramVideoEditedInfo = paramString1;
              paramString2.geo_point = paramUser;
              paramVideoEditedInfo = paramString1;
              paramString2.geo_point.lat = ((TLRPC.MessageMedia)localObject8).geo.lat;
              paramVideoEditedInfo = paramString1;
              paramString2.geo_point._long = ((TLRPC.MessageMedia)localObject8).geo._long;
              label5747:
              if (paramMessageMedia == null) {
                break label7156;
              }
              paramVideoEditedInfo = new org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast;
              paramVideoEditedInfo.<init>();
              paramUser = new java/util/ArrayList;
              paramUser.<init>();
              paramInt = 0;
              if (paramInt >= paramMessageMedia.size()) {
                break label7079;
              }
              paramUser.add(Long.valueOf(Utilities.random.nextLong()));
              paramInt++;
              continue;
            }
            paramVideoEditedInfo = paramString1;
            if ((localObject8 instanceof TLRPC.TL_messageMediaGeoLive))
            {
              paramVideoEditedInfo = paramString1;
              paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaGeoLive;
              paramVideoEditedInfo = paramString1;
              paramString2.<init>();
              paramVideoEditedInfo = paramString1;
              paramString2.period = ((TLRPC.MessageMedia)localObject8).period;
              continue;
            }
            paramVideoEditedInfo = paramString1;
            paramString2 = new TLRPC.TL_inputMediaGeoPoint();
            continue;
          }
          if ((n != 2) && ((n != 9) || (paramTL_photo == null))) {
            break label6228;
          }
          paramVideoEditedInfo = paramString1;
          if (paramTL_photo.access_hash != 0L) {
            break label6156;
          }
          paramVideoEditedInfo = paramString1;
          paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaUploadedPhoto;
          paramVideoEditedInfo = paramString1;
          paramString2.<init>();
          if (paramInt != 0)
          {
            paramVideoEditedInfo = paramString1;
            paramString2.ttl_seconds = paramInt;
            paramVideoEditedInfo = paramString1;
            paramTL_game.ttl = paramInt;
            paramVideoEditedInfo = paramString1;
            paramString2.flags |= 0x2;
          }
          if (localObject6 != null)
          {
            paramVideoEditedInfo = paramString1;
            paramUser = (String)((HashMap)localObject6).get("masks");
            if (paramUser != null)
            {
              paramVideoEditedInfo = paramString1;
              paramTL_document = new org/telegram/tgnet/SerializedData;
              paramVideoEditedInfo = paramString1;
              paramTL_document.<init>(Utilities.hexToBytes(paramUser));
              paramVideoEditedInfo = paramString1;
              i = paramTL_document.readInt32(false);
              paramInt = 0;
              if (paramInt < i)
              {
                paramVideoEditedInfo = paramString1;
                paramString2.stickers.add(TLRPC.InputDocument.TLdeserialize(paramTL_document, paramTL_document.readInt32(false), false));
                paramInt++;
                continue;
              }
              paramVideoEditedInfo = paramString1;
              paramString2.flags |= 0x1;
            }
          }
          if (paramString1 != null) {
            break label11055;
          }
          paramVideoEditedInfo = paramString1;
          paramString1 = new DelayedMessage(paramLong);
          paramString1.type = 0;
          paramString1.obj = paramWebPage;
          paramString1.originalPath = str2;
          if ((paramString3 != null) && (paramString3.length() > 0) && (paramString3.startsWith("http")))
          {
            paramString1.httpLocation = paramString3;
            continue;
          }
        }
        catch (Exception paramString1)
        {
          paramString2 = paramWebPage;
        }
        paramString1.location = ((TLRPC.PhotoSize)paramTL_photo.sizes.get(paramTL_photo.sizes.size() - 1)).location;
        continue;
        label6156:
        paramVideoEditedInfo = paramString1;
        paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaPhoto;
        paramVideoEditedInfo = paramString1;
        paramString2.<init>();
        paramVideoEditedInfo = paramString1;
        paramUser = new org/telegram/tgnet/TLRPC$TL_inputPhoto;
        paramVideoEditedInfo = paramString1;
        paramUser.<init>();
        paramVideoEditedInfo = paramString1;
        paramString2.id = paramUser;
        paramVideoEditedInfo = paramString1;
        paramString2.id.id = paramTL_photo.id;
        paramVideoEditedInfo = paramString1;
        paramString2.id.access_hash = paramTL_photo.access_hash;
        continue;
        label6228:
        if (n == 3)
        {
          paramVideoEditedInfo = paramString1;
          if (localTL_document.access_hash == 0L)
          {
            paramVideoEditedInfo = paramString1;
            paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument;
            paramVideoEditedInfo = paramString1;
            paramString2.<init>();
            paramVideoEditedInfo = paramString1;
            paramString2.mime_type = localTL_document.mime_type;
            paramVideoEditedInfo = paramString1;
            paramString2.attributes = localTL_document.attributes;
            paramVideoEditedInfo = paramString1;
            if (!MessageObject.isRoundVideoDocument(localTL_document)) {
              if (paramTL_document != null)
              {
                paramVideoEditedInfo = paramString1;
                if (!paramTL_document.muted)
                {
                  paramVideoEditedInfo = paramString1;
                  if (paramTL_document.roundVideo) {}
                }
              }
              else
              {
                paramVideoEditedInfo = paramString1;
                paramString2.nosound_video = true;
              }
            }
            if (paramInt != 0)
            {
              paramVideoEditedInfo = paramString1;
              paramString2.ttl_seconds = paramInt;
              paramVideoEditedInfo = paramString1;
              paramTL_game.ttl = paramInt;
              paramVideoEditedInfo = paramString1;
              paramString2.flags |= 0x2;
            }
            if (paramString1 != null) {
              break label11052;
            }
            paramVideoEditedInfo = paramString1;
            paramString1 = new DelayedMessage(paramLong);
            paramString1.type = 1;
            paramString1.obj = paramWebPage;
            paramString1.originalPath = str2;
          }
        }
        label7079:
        label7156:
        label7558:
        label7742:
        label7807:
        label8147:
        label8169:
        label8230:
        label8285:
        label8310:
        label8444:
        label8676:
        label8782:
        label8907:
        label8950:
        label9269:
        label9275:
        label10064:
        label10374:
        label10390:
        label10396:
        label10542:
        label10704:
        label11039:
        label11044:
        label11052:
        for (;;)
        {
          paramString1.location = localTL_document.thumb.location;
          paramString1.videoEditedInfo = paramTL_document;
          break label5747;
          paramVideoEditedInfo = paramString1;
          paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaDocument;
          paramVideoEditedInfo = paramString1;
          paramString2.<init>();
          paramVideoEditedInfo = paramString1;
          paramUser = new org/telegram/tgnet/TLRPC$TL_inputDocument;
          paramVideoEditedInfo = paramString1;
          paramUser.<init>();
          paramVideoEditedInfo = paramString1;
          paramString2.id = paramUser;
          paramVideoEditedInfo = paramString1;
          paramString2.id.id = localTL_document.id;
          paramVideoEditedInfo = paramString1;
          paramString2.id.access_hash = localTL_document.access_hash;
          break label5747;
          if (n == 6)
          {
            paramVideoEditedInfo = paramString1;
            paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaContact;
            paramVideoEditedInfo = paramString1;
            paramString2.<init>();
            paramVideoEditedInfo = paramString1;
            paramString2.phone_number = paramUser.phone;
            paramVideoEditedInfo = paramString1;
            paramString2.first_name = paramUser.first_name;
            paramVideoEditedInfo = paramString1;
            paramString2.last_name = paramUser.last_name;
            break label5747;
          }
          if ((n == 7) || (n == 9))
          {
            paramVideoEditedInfo = paramString1;
            if (localTL_document.access_hash == 0L)
            {
              if ((localObject4 == null) && (str2 != null))
              {
                paramVideoEditedInfo = paramString1;
                if (str2.length() > 0)
                {
                  paramVideoEditedInfo = paramString1;
                  if ((str2.startsWith("http")) && (localObject6 != null))
                  {
                    paramVideoEditedInfo = paramString1;
                    paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaGifExternal;
                    paramVideoEditedInfo = paramString1;
                    paramString2.<init>();
                    paramVideoEditedInfo = paramString1;
                    paramUser = ((String)((HashMap)localObject6).get("url")).split("\\|");
                    paramVideoEditedInfo = paramString1;
                    if (paramUser.length == 2)
                    {
                      paramVideoEditedInfo = paramString1;
                      ((TLRPC.TL_inputMediaGifExternal)paramString2).url = paramUser[0];
                      paramVideoEditedInfo = paramString1;
                      paramString2.q = paramUser[1];
                    }
                  }
                }
              }
              for (;;)
              {
                paramString2.mime_type = localTL_document.mime_type;
                paramString2.attributes = localTL_document.attributes;
                break;
                paramVideoEditedInfo = paramString1;
                paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument;
                paramVideoEditedInfo = paramString1;
                paramString2.<init>();
                if (paramInt != 0)
                {
                  paramVideoEditedInfo = paramString1;
                  paramString2.ttl_seconds = paramInt;
                  paramVideoEditedInfo = paramString1;
                  paramTL_game.ttl = paramInt;
                  paramVideoEditedInfo = paramString1;
                  paramString2.flags |= 0x2;
                }
                paramVideoEditedInfo = paramString1;
                paramString1 = new DelayedMessage(paramLong);
                paramString1.originalPath = str2;
                paramString1.type = 2;
                paramString1.obj = paramWebPage;
                paramString1.location = localTL_document.thumb.location;
              }
            }
            paramVideoEditedInfo = paramString1;
            paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaDocument;
            paramVideoEditedInfo = paramString1;
            paramString2.<init>();
            paramVideoEditedInfo = paramString1;
            paramUser = new org/telegram/tgnet/TLRPC$TL_inputDocument;
            paramVideoEditedInfo = paramString1;
            paramUser.<init>();
            paramVideoEditedInfo = paramString1;
            paramString2.id = paramUser;
            paramVideoEditedInfo = paramString1;
            paramString2.id.id = localTL_document.id;
            paramVideoEditedInfo = paramString1;
            paramString2.id.access_hash = localTL_document.access_hash;
            break label5747;
          }
          if (n == 8)
          {
            paramVideoEditedInfo = paramString1;
            if (localTL_document.access_hash == 0L)
            {
              paramVideoEditedInfo = paramString1;
              paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument;
              paramVideoEditedInfo = paramString1;
              paramString2.<init>();
              paramVideoEditedInfo = paramString1;
              paramString2.mime_type = localTL_document.mime_type;
              paramVideoEditedInfo = paramString1;
              paramString2.attributes = localTL_document.attributes;
              if (paramInt != 0)
              {
                paramVideoEditedInfo = paramString1;
                paramString2.ttl_seconds = paramInt;
                paramVideoEditedInfo = paramString1;
                paramTL_game.ttl = paramInt;
                paramVideoEditedInfo = paramString1;
                paramString2.flags |= 0x2;
              }
              paramVideoEditedInfo = paramString1;
              paramString1 = new DelayedMessage(paramLong);
              paramString1.type = 3;
              paramString1.obj = paramWebPage;
              break label5747;
            }
            paramVideoEditedInfo = paramString1;
            paramString2 = new org/telegram/tgnet/TLRPC$TL_inputMediaDocument;
            paramVideoEditedInfo = paramString1;
            paramString2.<init>();
            paramVideoEditedInfo = paramString1;
            paramUser = new org/telegram/tgnet/TLRPC$TL_inputDocument;
            paramVideoEditedInfo = paramString1;
            paramUser.<init>();
            paramVideoEditedInfo = paramString1;
            paramString2.id = paramUser;
            paramVideoEditedInfo = paramString1;
            paramString2.id.id = localTL_document.id;
            paramVideoEditedInfo = paramString1;
            paramString2.id.access_hash = localTL_document.access_hash;
            break label5747;
            paramVideoEditedInfo.contacts = paramMessageMedia;
            paramVideoEditedInfo.media = paramString2;
            paramVideoEditedInfo.random_id = paramUser;
            paramVideoEditedInfo.message = "";
            if (paramString1 != null) {
              paramString1.sendRequest = paramVideoEditedInfo;
            }
            paramMessageMedia = paramVideoEditedInfo;
            paramString2 = paramMessageMedia;
            if (paramMessageObject2 == null)
            {
              DataQuery.getInstance(this.currentAccount).cleanDraft(paramLong, false);
              paramString2 = paramMessageMedia;
            }
            for (;;)
            {
              if (l2 == 0L) {
                break label7558;
              }
              performSendDelayedMessage(paramString1);
              break;
              if (l2 != 0L)
              {
                if (paramString1.sendRequest != null) {
                  paramMessageMedia = (TLRPC.TL_messages_sendMultiMedia)paramString1.sendRequest;
                }
                for (;;)
                {
                  paramString1.messageObjects.add(paramWebPage);
                  paramString1.messages.add(paramTL_game);
                  paramString1.originalPaths.add(str2);
                  paramVideoEditedInfo = new org/telegram/tgnet/TLRPC$TL_inputSingleMedia;
                  paramVideoEditedInfo.<init>();
                  paramVideoEditedInfo.random_id = paramTL_game.random_id;
                  paramVideoEditedInfo.media = paramString2;
                  paramVideoEditedInfo.message = str1;
                  if ((paramArrayList != null) && (!paramArrayList.isEmpty()))
                  {
                    paramVideoEditedInfo.entities = paramArrayList;
                    paramVideoEditedInfo.flags |= 0x1;
                  }
                  paramMessageMedia.multi_media.add(paramVideoEditedInfo);
                  paramString2 = paramMessageMedia;
                  break;
                  paramMessageMedia = new org/telegram/tgnet/TLRPC$TL_messages_sendMultiMedia;
                  paramMessageMedia.<init>();
                  paramMessageMedia.peer = localInputPeer;
                  if ((paramTL_game.to_id instanceof TLRPC.TL_peerChannel))
                  {
                    paramVideoEditedInfo = MessagesController.getNotificationsSettings(this.currentAccount);
                    paramUser = new java/lang/StringBuilder;
                    paramUser.<init>();
                    paramMessageMedia.silent = paramVideoEditedInfo.getBoolean("silent_" + paramLong, false);
                  }
                  if (paramTL_game.reply_to_msg_id != 0)
                  {
                    paramMessageMedia.flags |= 0x1;
                    paramMessageMedia.reply_to_msg_id = paramTL_game.reply_to_msg_id;
                  }
                  paramString1.sendRequest = paramMessageMedia;
                }
              }
              paramMessageMedia = new org/telegram/tgnet/TLRPC$TL_messages_sendMedia;
              paramMessageMedia.<init>();
              paramMessageMedia.peer = localInputPeer;
              if ((paramTL_game.to_id instanceof TLRPC.TL_peerChannel))
              {
                paramVideoEditedInfo = MessagesController.getNotificationsSettings(this.currentAccount);
                paramUser = new java/lang/StringBuilder;
                paramUser.<init>();
                paramMessageMedia.silent = paramVideoEditedInfo.getBoolean("silent_" + paramLong, false);
              }
              if (paramTL_game.reply_to_msg_id != 0)
              {
                paramMessageMedia.flags |= 0x1;
                paramMessageMedia.reply_to_msg_id = paramTL_game.reply_to_msg_id;
              }
              paramMessageMedia.random_id = paramTL_game.random_id;
              paramMessageMedia.media = paramString2;
              paramMessageMedia.message = str1;
              if ((paramArrayList != null) && (!paramArrayList.isEmpty()))
              {
                paramMessageMedia.entities = paramArrayList;
                paramMessageMedia.flags |= 0x8;
              }
              if (paramString1 != null) {
                paramString1.sendRequest = paramMessageMedia;
              }
              paramString2 = paramMessageMedia;
            }
            if (n == 1)
            {
              performSendMessageRequest(paramString2, paramWebPage, null);
              break;
            }
            if (n == 2)
            {
              if (paramTL_photo.access_hash == 0L)
              {
                performSendDelayedMessage(paramString1);
                break;
              }
              performSendMessageRequest(paramString2, paramWebPage, null, null, true);
              break;
            }
            if (n == 3)
            {
              if (localTL_document.access_hash == 0L)
              {
                performSendDelayedMessage(paramString1);
                break;
              }
              performSendMessageRequest(paramString2, paramWebPage, null);
              break;
            }
            if (n == 6)
            {
              performSendMessageRequest(paramString2, paramWebPage, null);
              break;
            }
            if (n == 7)
            {
              if ((localTL_document.access_hash == 0L) && (paramString1 != null))
              {
                performSendDelayedMessage(paramString1);
                break;
              }
              performSendMessageRequest(paramString2, paramWebPage, str2);
              break;
            }
            if (n != 8) {
              break;
            }
            if (localTL_document.access_hash == 0L)
            {
              performSendDelayedMessage(paramString1);
              break;
            }
            performSendMessageRequest(paramString2, paramWebPage, null);
            break;
            paramVideoEditedInfo = paramString1;
            if (AndroidUtilities.getPeerLayerVersion(((TLRPC.EncryptedChat)localObject4).layer) >= 73)
            {
              paramVideoEditedInfo = paramString1;
              paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessage;
              paramVideoEditedInfo = paramString1;
              paramString2.<init>();
              paramMessageMedia = paramString2;
              if (l2 != 0L)
              {
                paramVideoEditedInfo = paramString1;
                paramString2.grouped_id = l2;
                paramVideoEditedInfo = paramString1;
                paramString2.flags |= 0x20000;
                paramMessageMedia = paramString2;
              }
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.ttl = paramTL_game.ttl;
              if (paramArrayList != null)
              {
                paramVideoEditedInfo = paramString1;
                if (!paramArrayList.isEmpty())
                {
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.entities = paramArrayList;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.flags |= 0x80;
                }
              }
              paramVideoEditedInfo = paramString1;
              if (paramTL_game.reply_to_random_id != 0L)
              {
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.reply_to_random_id = paramTL_game.reply_to_random_id;
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.flags |= 0x8;
              }
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.flags |= 0x200;
              if (localObject6 != null)
              {
                paramVideoEditedInfo = paramString1;
                if (((HashMap)localObject6).get("bot_name") != null)
                {
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.via_bot_name = ((String)((HashMap)localObject6).get("bot_name"));
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.flags |= 0x800;
                }
              }
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.random_id = paramTL_game.random_id;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.message = "";
              if (n != 1) {
                break label8310;
              }
              paramVideoEditedInfo = paramString1;
              if (!(localObject8 instanceof TLRPC.TL_messageMediaVenue)) {
                break label8285;
              }
              paramVideoEditedInfo = paramString1;
              paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVenue;
              paramVideoEditedInfo = paramString1;
              paramString2.<init>();
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media = paramString2;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media.address = ((TLRPC.MessageMedia)localObject8).address;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media.title = ((TLRPC.MessageMedia)localObject8).title;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media.provider = ((TLRPC.MessageMedia)localObject8).provider;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media.venue_id = ((TLRPC.MessageMedia)localObject8).venue_id;
            }
            for (;;)
            {
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media.lat = ((TLRPC.MessageMedia)localObject8).geo.lat;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media._long = ((TLRPC.MessageMedia)localObject8).geo._long;
              paramVideoEditedInfo = paramString1;
              SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(paramMessageMedia, paramWebPage.messageOwner, (TLRPC.EncryptedChat)localObject4, null, null, paramWebPage);
              if (l2 != 0L)
              {
                if (paramString1.sendEncryptedRequest == null) {
                  break label10374;
                }
                paramString2 = (TLRPC.TL_messages_sendEncryptedMultiMedia)paramString1.sendEncryptedRequest;
                paramString1.messageObjects.add(paramWebPage);
                paramString1.messages.add(paramTL_game);
                paramString1.originalPaths.add(str2);
                paramString1.upload = true;
                paramString2.messages.add(paramMessageMedia);
                paramMessageMedia = new org/telegram/tgnet/TLRPC$TL_inputEncryptedFile;
                paramMessageMedia.<init>();
                if (n != 3) {
                  break label10390;
                }
                l1 = 1L;
                paramMessageMedia.id = l1;
                paramString2.files.add(paramMessageMedia);
                performSendDelayedMessage(paramString1);
              }
              if (paramMessageObject2 != null) {
                break;
              }
              DataQuery.getInstance(this.currentAccount).cleanDraft(paramLong, false);
              break;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia = new TLRPC.TL_decryptedMessage_layer45();
              break label7807;
              paramVideoEditedInfo = paramString1;
              paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaGeoPoint;
              paramVideoEditedInfo = paramString1;
              paramString2.<init>();
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media = paramString2;
            }
            if ((n == 2) || ((n == 9) && (paramTL_photo != null)))
            {
              paramVideoEditedInfo = paramString1;
              paramUser = (TLRPC.PhotoSize)paramTL_photo.sizes.get(0);
              paramVideoEditedInfo = paramString1;
              paramString2 = (TLRPC.PhotoSize)paramTL_photo.sizes.get(paramTL_photo.sizes.size() - 1);
              paramVideoEditedInfo = paramString1;
              ImageLoader.fillPhotoSizeWithBytes(paramUser);
              paramVideoEditedInfo = paramString1;
              paramTL_document = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto;
              paramVideoEditedInfo = paramString1;
              paramTL_document.<init>();
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media = paramTL_document;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media.caption = str1;
              paramVideoEditedInfo = paramString1;
              if (paramUser.bytes != null)
              {
                paramVideoEditedInfo = paramString1;
                ((TLRPC.TL_decryptedMessageMediaPhoto)paramMessageMedia.media).thumb = paramUser.bytes;
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.media.thumb_h = paramUser.h;
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.media.thumb_w = paramUser.w;
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.media.w = paramString2.w;
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.media.h = paramString2.h;
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.media.size = paramString2.size;
                paramVideoEditedInfo = paramString1;
                if ((paramString2.location.key != null) && (l2 == 0L)) {
                  break label8676;
                }
                if (paramString1 != null) {
                  break label11044;
                }
                paramVideoEditedInfo = paramString1;
                paramString2 = new DelayedMessage(paramLong);
                paramString2.encryptedChat = ((TLRPC.EncryptedChat)localObject4);
                paramString2.type = 0;
                paramString2.originalPath = str2;
                paramString2.sendEncryptedRequest = paramMessageMedia;
                paramString2.obj = paramWebPage;
              }
            }
            for (;;)
            {
              if ((!TextUtils.isEmpty(paramString3)) && (paramString3.startsWith("http"))) {
                paramString2.httpLocation = paramString3;
              }
              for (;;)
              {
                paramString1 = paramString2;
                if (l2 != 0L) {
                  break;
                }
                performSendDelayedMessage(paramString2);
                paramString1 = paramString2;
                break;
                paramVideoEditedInfo = paramString1;
                ((TLRPC.TL_decryptedMessageMediaPhoto)paramMessageMedia.media).thumb = new byte[0];
                break label8444;
                paramString2.location = ((TLRPC.PhotoSize)paramTL_photo.sizes.get(paramTL_photo.sizes.size() - 1)).location;
              }
              paramVideoEditedInfo = paramString1;
              paramTL_photo = new org/telegram/tgnet/TLRPC$TL_inputEncryptedFile;
              paramVideoEditedInfo = paramString1;
              paramTL_photo.<init>();
              paramVideoEditedInfo = paramString1;
              paramTL_photo.id = paramString2.location.volume_id;
              paramVideoEditedInfo = paramString1;
              paramTL_photo.access_hash = paramString2.location.secret;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media.key = paramString2.location.key;
              paramVideoEditedInfo = paramString1;
              paramMessageMedia.media.iv = paramString2.location.iv;
              paramVideoEditedInfo = paramString1;
              SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(paramMessageMedia, paramWebPage.messageOwner, (TLRPC.EncryptedChat)localObject4, paramTL_photo, null, paramWebPage);
              break label8147;
              if (n == 3)
              {
                paramVideoEditedInfo = paramString1;
                ImageLoader.fillPhotoSizeWithBytes(localTL_document.thumb);
                paramVideoEditedInfo = paramString1;
                if (!MessageObject.isNewGifDocument(localTL_document))
                {
                  paramVideoEditedInfo = paramString1;
                  if (!MessageObject.isRoundVideoDocument(localTL_document)) {}
                }
                else
                {
                  paramVideoEditedInfo = paramString1;
                  paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument;
                  paramVideoEditedInfo = paramString1;
                  paramString2.<init>();
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media = paramString2;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media.attributes = localTL_document.attributes;
                  paramVideoEditedInfo = paramString1;
                  if (localTL_document.thumb != null)
                  {
                    paramVideoEditedInfo = paramString1;
                    if (localTL_document.thumb.bytes != null)
                    {
                      paramVideoEditedInfo = paramString1;
                      ((TLRPC.TL_decryptedMessageMediaDocument)paramMessageMedia.media).thumb = localTL_document.thumb.bytes;
                      paramVideoEditedInfo = paramString1;
                      paramMessageMedia.media.caption = str1;
                      paramVideoEditedInfo = paramString1;
                      paramMessageMedia.media.mime_type = "video/mp4";
                      paramVideoEditedInfo = paramString1;
                      paramMessageMedia.media.size = localTL_document.size;
                      paramInt = 0;
                      paramVideoEditedInfo = paramString1;
                      if (paramInt < localTL_document.attributes.size())
                      {
                        paramVideoEditedInfo = paramString1;
                        paramString2 = (TLRPC.DocumentAttribute)localTL_document.attributes.get(paramInt);
                        paramVideoEditedInfo = paramString1;
                        if (!(paramString2 instanceof TLRPC.TL_documentAttributeVideo)) {
                          break label9269;
                        }
                        paramVideoEditedInfo = paramString1;
                        paramMessageMedia.media.w = paramString2.w;
                        paramVideoEditedInfo = paramString1;
                        paramMessageMedia.media.h = paramString2.h;
                        paramVideoEditedInfo = paramString1;
                        paramMessageMedia.media.duration = paramString2.duration;
                      }
                      paramVideoEditedInfo = paramString1;
                      paramMessageMedia.media.thumb_h = localTL_document.thumb.h;
                      paramVideoEditedInfo = paramString1;
                      paramMessageMedia.media.thumb_w = localTL_document.thumb.w;
                      paramVideoEditedInfo = paramString1;
                      if ((localTL_document.key != null) && (l2 == 0L)) {
                        break label9275;
                      }
                      if (paramString1 != null) {
                        break label11039;
                      }
                      paramVideoEditedInfo = paramString1;
                      paramString2 = new DelayedMessage(paramLong);
                      paramString2.encryptedChat = ((TLRPC.EncryptedChat)localObject4);
                      paramString2.type = 1;
                      paramString2.sendEncryptedRequest = paramMessageMedia;
                      paramString2.originalPath = str2;
                      paramString2.obj = paramWebPage;
                    }
                  }
                }
              }
              for (;;)
              {
                paramString2.videoEditedInfo = paramTL_document;
                paramString1 = paramString2;
                if (l2 != 0L) {
                  break label8147;
                }
                performSendDelayedMessage(paramString2);
                paramString1 = paramString2;
                break label8147;
                paramVideoEditedInfo = paramString1;
                ((TLRPC.TL_decryptedMessageMediaDocument)paramMessageMedia.media).thumb = new byte[0];
                break label8907;
                paramVideoEditedInfo = paramString1;
                paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo;
                paramVideoEditedInfo = paramString1;
                paramString2.<init>();
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.media = paramString2;
                paramVideoEditedInfo = paramString1;
                if (localTL_document.thumb != null)
                {
                  paramVideoEditedInfo = paramString1;
                  if (localTL_document.thumb.bytes != null)
                  {
                    paramVideoEditedInfo = paramString1;
                    ((TLRPC.TL_decryptedMessageMediaVideo)paramMessageMedia.media).thumb = localTL_document.thumb.bytes;
                    break label8907;
                  }
                }
                paramVideoEditedInfo = paramString1;
                ((TLRPC.TL_decryptedMessageMediaVideo)paramMessageMedia.media).thumb = new byte[0];
                break label8907;
                paramInt++;
                break label8950;
                paramVideoEditedInfo = paramString1;
                paramString2 = new org/telegram/tgnet/TLRPC$TL_inputEncryptedFile;
                paramVideoEditedInfo = paramString1;
                paramString2.<init>();
                paramVideoEditedInfo = paramString1;
                paramString2.id = localTL_document.id;
                paramVideoEditedInfo = paramString1;
                paramString2.access_hash = localTL_document.access_hash;
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.media.key = localTL_document.key;
                paramVideoEditedInfo = paramString1;
                paramMessageMedia.media.iv = localTL_document.iv;
                paramVideoEditedInfo = paramString1;
                SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(paramMessageMedia, paramWebPage.messageOwner, (TLRPC.EncryptedChat)localObject4, paramString2, null, paramWebPage);
                break label8147;
                if (n == 6)
                {
                  paramVideoEditedInfo = paramString1;
                  paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaContact;
                  paramVideoEditedInfo = paramString1;
                  paramString2.<init>();
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media = paramString2;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media.phone_number = paramUser.phone;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media.first_name = paramUser.first_name;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media.last_name = paramUser.last_name;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media.user_id = paramUser.id;
                  paramVideoEditedInfo = paramString1;
                  SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(paramMessageMedia, paramWebPage.messageOwner, (TLRPC.EncryptedChat)localObject4, null, null, paramWebPage);
                  break label8147;
                }
                if ((n == 7) || ((n == 9) && (localTL_document != null)))
                {
                  paramVideoEditedInfo = paramString1;
                  if (MessageObject.isStickerDocument(localTL_document))
                  {
                    paramVideoEditedInfo = paramString1;
                    paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument;
                    paramVideoEditedInfo = paramString1;
                    paramString2.<init>();
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media = paramString2;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.id = localTL_document.id;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.date = localTL_document.date;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.access_hash = localTL_document.access_hash;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.mime_type = localTL_document.mime_type;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.size = localTL_document.size;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.dc_id = localTL_document.dc_id;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.attributes = localTL_document.attributes;
                    paramVideoEditedInfo = paramString1;
                    if (localTL_document.thumb == null)
                    {
                      paramVideoEditedInfo = paramString1;
                      paramString2 = (TLRPC.TL_decryptedMessageMediaExternalDocument)paramMessageMedia.media;
                      paramVideoEditedInfo = paramString1;
                      paramTL_photo = new org/telegram/tgnet/TLRPC$TL_photoSizeEmpty;
                      paramVideoEditedInfo = paramString1;
                      paramTL_photo.<init>();
                      paramVideoEditedInfo = paramString1;
                      paramString2.thumb = paramTL_photo;
                      paramVideoEditedInfo = paramString1;
                      ((TLRPC.TL_decryptedMessageMediaExternalDocument)paramMessageMedia.media).thumb.type = "s";
                    }
                    for (;;)
                    {
                      paramVideoEditedInfo = paramString1;
                      SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(paramMessageMedia, paramWebPage.messageOwner, (TLRPC.EncryptedChat)localObject4, null, null, paramWebPage);
                      break;
                      paramVideoEditedInfo = paramString1;
                      ((TLRPC.TL_decryptedMessageMediaExternalDocument)paramMessageMedia.media).thumb = localTL_document.thumb;
                    }
                  }
                  paramVideoEditedInfo = paramString1;
                  ImageLoader.fillPhotoSizeWithBytes(localTL_document.thumb);
                  paramVideoEditedInfo = paramString1;
                  paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument;
                  paramVideoEditedInfo = paramString1;
                  paramString2.<init>();
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media = paramString2;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media.attributes = localTL_document.attributes;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media.caption = str1;
                  paramVideoEditedInfo = paramString1;
                  if (localTL_document.thumb != null)
                  {
                    paramVideoEditedInfo = paramString1;
                    if (localTL_document.thumb.bytes != null)
                    {
                      paramVideoEditedInfo = paramString1;
                      ((TLRPC.TL_decryptedMessageMediaDocument)paramMessageMedia.media).thumb = localTL_document.thumb.bytes;
                      paramVideoEditedInfo = paramString1;
                      paramMessageMedia.media.thumb_h = localTL_document.thumb.h;
                      paramVideoEditedInfo = paramString1;
                    }
                  }
                  for (paramMessageMedia.media.thumb_w = localTL_document.thumb.w;; paramMessageMedia.media.thumb_w = 0)
                  {
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.size = localTL_document.size;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.mime_type = localTL_document.mime_type;
                    paramVideoEditedInfo = paramString1;
                    if (localTL_document.key != null) {
                      break label10064;
                    }
                    paramVideoEditedInfo = paramString1;
                    paramString1 = new DelayedMessage(paramLong);
                    paramString1.originalPath = str2;
                    paramString1.sendEncryptedRequest = paramMessageMedia;
                    paramString1.type = 2;
                    paramString1.obj = paramWebPage;
                    paramString1.encryptedChat = ((TLRPC.EncryptedChat)localObject4);
                    if ((paramString3 != null) && (paramString3.length() > 0) && (paramString3.startsWith("http"))) {
                      paramString1.httpLocation = paramString3;
                    }
                    performSendDelayedMessage(paramString1);
                    break;
                    paramVideoEditedInfo = paramString1;
                    ((TLRPC.TL_decryptedMessageMediaDocument)paramMessageMedia.media).thumb = new byte[0];
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.media.thumb_h = 0;
                    paramVideoEditedInfo = paramString1;
                  }
                  paramVideoEditedInfo = paramString1;
                  paramString2 = new org/telegram/tgnet/TLRPC$TL_inputEncryptedFile;
                  paramVideoEditedInfo = paramString1;
                  paramString2.<init>();
                  paramVideoEditedInfo = paramString1;
                  paramString2.id = localTL_document.id;
                  paramVideoEditedInfo = paramString1;
                  paramString2.access_hash = localTL_document.access_hash;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media.key = localTL_document.key;
                  paramVideoEditedInfo = paramString1;
                  paramMessageMedia.media.iv = localTL_document.iv;
                  paramVideoEditedInfo = paramString1;
                  SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(paramMessageMedia, paramWebPage.messageOwner, (TLRPC.EncryptedChat)localObject4, paramString2, null, paramWebPage);
                  break label8147;
                }
                if (n != 8) {
                  break label8782;
                }
                paramVideoEditedInfo = paramString1;
                paramString1 = new DelayedMessage(paramLong);
                paramString1.encryptedChat = ((TLRPC.EncryptedChat)localObject4);
                paramString1.sendEncryptedRequest = paramMessageMedia;
                paramString1.obj = paramWebPage;
                paramString1.type = 3;
                paramString2 = new org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument;
                paramString2.<init>();
                paramMessageMedia.media = paramString2;
                paramMessageMedia.media.attributes = localTL_document.attributes;
                paramMessageMedia.media.caption = str1;
                if ((localTL_document.thumb != null) && (localTL_document.thumb.bytes != null))
                {
                  ((TLRPC.TL_decryptedMessageMediaDocument)paramMessageMedia.media).thumb = localTL_document.thumb.bytes;
                  paramMessageMedia.media.thumb_h = localTL_document.thumb.h;
                }
                for (paramMessageMedia.media.thumb_w = localTL_document.thumb.w;; paramMessageMedia.media.thumb_w = 0)
                {
                  paramMessageMedia.media.mime_type = localTL_document.mime_type;
                  paramMessageMedia.media.size = localTL_document.size;
                  paramString1.originalPath = str2;
                  performSendDelayedMessage(paramString1);
                  break;
                  ((TLRPC.TL_decryptedMessageMediaDocument)paramMessageMedia.media).thumb = new byte[0];
                  paramMessageMedia.media.thumb_h = 0;
                }
                paramString2 = new org/telegram/tgnet/TLRPC$TL_messages_sendEncryptedMultiMedia;
                paramString2.<init>();
                paramString1.sendEncryptedRequest = paramString2;
                break label8169;
                l1 = 0L;
                break label8230;
                if (n == 4)
                {
                  paramVideoEditedInfo = paramString1;
                  paramString2 = new org/telegram/tgnet/TLRPC$TL_messages_forwardMessages;
                  paramVideoEditedInfo = paramString1;
                  paramString2.<init>();
                  paramVideoEditedInfo = paramString1;
                  paramString2.to_peer = localInputPeer;
                  paramVideoEditedInfo = paramString1;
                  paramString2.with_my_score = paramMessageObject2.messageOwner.with_my_score;
                  paramVideoEditedInfo = paramString1;
                  if (paramMessageObject2.messageOwner.ttl != 0)
                  {
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-paramMessageObject2.messageOwner.ttl));
                    paramVideoEditedInfo = paramString1;
                    paramTL_photo = new org/telegram/tgnet/TLRPC$TL_inputPeerChannel;
                    paramVideoEditedInfo = paramString1;
                    paramTL_photo.<init>();
                    paramVideoEditedInfo = paramString1;
                    paramString2.from_peer = paramTL_photo;
                    paramVideoEditedInfo = paramString1;
                    paramString2.from_peer.channel_id = (-paramMessageObject2.messageOwner.ttl);
                    if (paramMessageMedia != null)
                    {
                      paramVideoEditedInfo = paramString1;
                      paramString2.from_peer.access_hash = paramMessageMedia.access_hash;
                    }
                    paramVideoEditedInfo = paramString1;
                    if ((paramMessageObject2.messageOwner.to_id instanceof TLRPC.TL_peerChannel))
                    {
                      paramVideoEditedInfo = paramString1;
                      paramMessageMedia = MessagesController.getNotificationsSettings(this.currentAccount);
                      paramVideoEditedInfo = paramString1;
                      paramTL_photo = new java/lang/StringBuilder;
                      paramVideoEditedInfo = paramString1;
                      paramTL_photo.<init>();
                      paramVideoEditedInfo = paramString1;
                      paramString2.silent = paramMessageMedia.getBoolean("silent_" + paramLong, false);
                    }
                    paramVideoEditedInfo = paramString1;
                    paramString2.random_id.add(Long.valueOf(paramTL_game.random_id));
                    paramVideoEditedInfo = paramString1;
                    if (paramMessageObject2.getId() < 0) {
                      break label10704;
                    }
                    paramVideoEditedInfo = paramString1;
                    paramString2.id.add(Integer.valueOf(paramMessageObject2.getId()));
                  }
                  for (;;)
                  {
                    paramVideoEditedInfo = paramString1;
                    performSendMessageRequest(paramString2, paramWebPage, null);
                    break;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia = new org/telegram/tgnet/TLRPC$TL_inputPeerEmpty;
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia.<init>();
                    paramVideoEditedInfo = paramString1;
                    paramString2.from_peer = paramMessageMedia;
                    break label10542;
                    paramVideoEditedInfo = paramString1;
                    if (paramMessageObject2.messageOwner.fwd_msg_id != 0)
                    {
                      paramVideoEditedInfo = paramString1;
                      paramString2.id.add(Integer.valueOf(paramMessageObject2.messageOwner.fwd_msg_id));
                    }
                    else
                    {
                      paramVideoEditedInfo = paramString1;
                      if (paramMessageObject2.messageOwner.fwd_from != null)
                      {
                        paramVideoEditedInfo = paramString1;
                        paramString2.id.add(Integer.valueOf(paramMessageObject2.messageOwner.fwd_from.channel_post));
                      }
                    }
                  }
                }
                if (n == 9)
                {
                  paramVideoEditedInfo = paramString1;
                  paramString2 = new org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult;
                  paramVideoEditedInfo = paramString1;
                  paramString2.<init>();
                  paramVideoEditedInfo = paramString1;
                  paramString2.peer = localInputPeer;
                  paramVideoEditedInfo = paramString1;
                  paramString2.random_id = paramTL_game.random_id;
                  paramVideoEditedInfo = paramString1;
                  if (paramTL_game.reply_to_msg_id != 0)
                  {
                    paramVideoEditedInfo = paramString1;
                    paramString2.flags |= 0x1;
                    paramVideoEditedInfo = paramString1;
                    paramString2.reply_to_msg_id = paramTL_game.reply_to_msg_id;
                  }
                  paramVideoEditedInfo = paramString1;
                  if ((paramTL_game.to_id instanceof TLRPC.TL_peerChannel))
                  {
                    paramVideoEditedInfo = paramString1;
                    paramMessageMedia = MessagesController.getNotificationsSettings(this.currentAccount);
                    paramVideoEditedInfo = paramString1;
                    paramTL_photo = new java/lang/StringBuilder;
                    paramVideoEditedInfo = paramString1;
                    paramTL_photo.<init>();
                    paramVideoEditedInfo = paramString1;
                    paramString2.silent = paramMessageMedia.getBoolean("silent_" + paramLong, false);
                  }
                  paramVideoEditedInfo = paramString1;
                  paramString2.query_id = Utilities.parseLong((String)((HashMap)localObject6).get("query_id")).longValue();
                  paramVideoEditedInfo = paramString1;
                  paramString2.id = ((String)((HashMap)localObject6).get("id"));
                  if (paramMessageObject2 == null)
                  {
                    paramVideoEditedInfo = paramString1;
                    paramString2.clear_draft = true;
                    paramVideoEditedInfo = paramString1;
                    DataQuery.getInstance(this.currentAccount).cleanDraft(paramLong, false);
                  }
                  paramVideoEditedInfo = paramString1;
                  performSendMessageRequest(paramString2, paramWebPage, null);
                }
                break;
                break label801;
                break label801;
                paramString2 = paramString1;
              }
              paramString2 = paramString1;
            }
          }
          break label5747;
        }
        label11055:
        continue;
        label11058:
        continue;
        label11061:
        paramString1 = null;
      }
      label11066:
      paramTL_game = (TLRPC.TL_game)localObject1;
      localObject11 = paramWebPage;
      localObject2 = paramHashMap;
    }
  }
  
  private void sendReadyToSendGroup(DelayedMessage paramDelayedMessage, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramDelayedMessage.messageObjects.isEmpty()) {
      paramDelayedMessage.markAsError();
    }
    for (;;)
    {
      return;
      localObject = "group_" + paramDelayedMessage.groupId;
      if (paramDelayedMessage.finalGroupMessage != ((MessageObject)paramDelayedMessage.messageObjects.get(paramDelayedMessage.messageObjects.size() - 1)).getId())
      {
        if (paramBoolean1) {
          putToDelayedMessages((String)localObject, paramDelayedMessage);
        }
      }
      else
      {
        if (paramBoolean1)
        {
          this.delayedMessages.remove(localObject);
          MessagesStorage.getInstance(this.currentAccount).putMessages(paramDelayedMessage.messages, false, true, false, 0);
          MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(paramDelayedMessage.peer, paramDelayedMessage.messageObjects);
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        if (!(paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMultiMedia)) {
          break;
        }
        localObject = (TLRPC.TL_messages_sendMultiMedia)paramDelayedMessage.sendRequest;
        for (i = 0;; i++)
        {
          if (i >= ((TLRPC.TL_messages_sendMultiMedia)localObject).multi_media.size()) {
            break label225;
          }
          TLRPC.InputMedia localInputMedia = ((TLRPC.TL_inputSingleMedia)((TLRPC.TL_messages_sendMultiMedia)localObject).multi_media.get(i)).media;
          if (((localInputMedia instanceof TLRPC.TL_inputMediaUploadedPhoto)) || ((localInputMedia instanceof TLRPC.TL_inputMediaUploadedDocument))) {
            break;
          }
        }
        label225:
        if (!paramBoolean2) {
          break label338;
        }
        localObject = findMaxDelayedMessageForMessageId(paramDelayedMessage.finalGroupMessage, paramDelayedMessage.peer);
        if (localObject == null) {
          break label338;
        }
        ((DelayedMessage)localObject).addDelayedRequest(paramDelayedMessage.sendRequest, paramDelayedMessage.messageObjects, paramDelayedMessage.originalPaths);
        if (paramDelayedMessage.requests != null) {
          ((DelayedMessage)localObject).requests.addAll(paramDelayedMessage.requests);
        }
      }
    }
    Object localObject = (TLRPC.TL_messages_sendEncryptedMultiMedia)paramDelayedMessage.sendEncryptedRequest;
    for (int i = 0;; i++)
    {
      if (i >= ((TLRPC.TL_messages_sendEncryptedMultiMedia)localObject).files.size()) {
        break label338;
      }
      if (((TLRPC.InputEncryptedFile)((TLRPC.TL_messages_sendEncryptedMultiMedia)localObject).files.get(i) instanceof TLRPC.TL_inputEncryptedFile)) {
        break;
      }
    }
    label338:
    if ((paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMultiMedia)) {
      performSendMessageRequestMulti((TLRPC.TL_messages_sendMultiMedia)paramDelayedMessage.sendRequest, paramDelayedMessage.messageObjects, paramDelayedMessage.originalPaths);
    }
    for (;;)
    {
      paramDelayedMessage.sendDelayedRequests();
      break;
      SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest((TLRPC.TL_messages_sendEncryptedMultiMedia)paramDelayedMessage.sendEncryptedRequest, paramDelayedMessage);
    }
  }
  
  private void updateMediaPaths(MessageObject paramMessageObject, TLRPC.Message paramMessage, String paramString, boolean paramBoolean)
  {
    TLRPC.Message localMessage = paramMessageObject.messageOwner;
    if (paramMessage == null) {}
    for (;;)
    {
      return;
      int i;
      label214:
      Object localObject1;
      label287:
      label290:
      TLRPC.PhotoSize localPhotoSize;
      label357:
      Object localObject2;
      Object localObject3;
      if (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessage.media.photo != null) && ((localMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (localMessage.media.photo != null))
      {
        if (paramMessage.media.ttl_seconds == 0) {
          MessagesStorage.getInstance(this.currentAccount).putSentFile(paramString, paramMessage.media.photo, 0);
        }
        if ((localMessage.media.photo.sizes.size() == 1) && ((((TLRPC.PhotoSize)localMessage.media.photo.sizes.get(0)).location instanceof TLRPC.TL_fileLocationUnavailable)))
        {
          localMessage.media.photo.sizes = paramMessage.media.photo.sizes;
          paramMessage.message = localMessage.message;
          paramMessage.attachPath = localMessage.attachPath;
          localMessage.media.photo.id = paramMessage.media.photo.id;
          localMessage.media.photo.access_hash = paramMessage.media.photo.access_hash;
        }
        else
        {
          i = 0;
          if (i < paramMessage.media.photo.sizes.size())
          {
            localObject1 = (TLRPC.PhotoSize)paramMessage.media.photo.sizes.get(i);
            if ((localObject1 != null) && (((TLRPC.PhotoSize)localObject1).location != null) && (!(localObject1 instanceof TLRPC.TL_photoSizeEmpty)) && (((TLRPC.PhotoSize)localObject1).type != null)) {
              break label287;
            }
          }
          do
          {
            i++;
            break label214;
            break;
            int j = 0;
            if (j < localMessage.media.photo.sizes.size())
            {
              localPhotoSize = (TLRPC.PhotoSize)localMessage.media.photo.sizes.get(j);
              if ((localPhotoSize != null) && (localPhotoSize.location != null) && (localPhotoSize.type != null)) {
                break label357;
              }
            }
            while (((localPhotoSize.location.volume_id != -2147483648L) || (!((TLRPC.PhotoSize)localObject1).type.equals(localPhotoSize.type))) && ((((TLRPC.PhotoSize)localObject1).w != localPhotoSize.w) || (((TLRPC.PhotoSize)localObject1).h != localPhotoSize.h)))
            {
              j++;
              break label290;
              break;
            }
            localObject2 = localPhotoSize.location.volume_id + "_" + localPhotoSize.location.local_id;
            localObject3 = ((TLRPC.PhotoSize)localObject1).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject1).location.local_id;
          } while (((String)localObject2).equals(localObject3));
          paramString = new File(FileLoader.getDirectory(4), (String)localObject2 + ".jpg");
          if ((paramMessage.media.ttl_seconds == 0) && ((paramMessage.media.photo.sizes.size() == 1) || (((TLRPC.PhotoSize)localObject1).w > 90) || (((TLRPC.PhotoSize)localObject1).h > 90))) {}
          for (paramMessageObject = FileLoader.getPathToAttach((TLObject)localObject1);; paramMessageObject = new File(FileLoader.getDirectory(4), (String)localObject3 + ".jpg"))
          {
            paramString.renameTo(paramMessageObject);
            ImageLoader.getInstance().replaceImageInCache((String)localObject2, (String)localObject3, ((TLRPC.PhotoSize)localObject1).location, paramBoolean);
            localPhotoSize.location = ((TLRPC.PhotoSize)localObject1).location;
            localPhotoSize.size = ((TLRPC.PhotoSize)localObject1).size;
            break;
          }
        }
      }
      else if (((paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) && (paramMessage.media.document != null) && ((localMessage.media instanceof TLRPC.TL_messageMediaDocument)) && (localMessage.media.document != null))
      {
        if (MessageObject.isVideoMessage(paramMessage))
        {
          if (paramMessage.media.ttl_seconds == 0) {
            MessagesStorage.getInstance(this.currentAccount).putSentFile(paramString, paramMessage.media.document, 2);
          }
          paramMessage.attachPath = localMessage.attachPath;
          localObject2 = localMessage.media.document.thumb;
          localPhotoSize = paramMessage.media.document.thumb;
          if ((localObject2 == null) || (((TLRPC.PhotoSize)localObject2).location == null) || (((TLRPC.PhotoSize)localObject2).location.volume_id != -2147483648L) || (localPhotoSize == null) || (localPhotoSize.location == null) || ((localPhotoSize instanceof TLRPC.TL_photoSizeEmpty)) || ((localObject2 instanceof TLRPC.TL_photoSizeEmpty))) {
            break label1312;
          }
          localObject1 = ((TLRPC.PhotoSize)localObject2).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject2).location.local_id;
          localObject3 = localPhotoSize.location.volume_id + "_" + localPhotoSize.location.local_id;
          if (!((String)localObject1).equals(localObject3))
          {
            new File(FileLoader.getDirectory(4), (String)localObject1 + ".jpg").renameTo(new File(FileLoader.getDirectory(4), (String)localObject3 + ".jpg"));
            ImageLoader.getInstance().replaceImageInCache((String)localObject1, (String)localObject3, localPhotoSize.location, paramBoolean);
            ((TLRPC.PhotoSize)localObject2).location = localPhotoSize.location;
            ((TLRPC.PhotoSize)localObject2).size = localPhotoSize.size;
          }
          label1036:
          localMessage.media.document.dc_id = paramMessage.media.document.dc_id;
          localMessage.media.document.id = paramMessage.media.document.id;
          localMessage.media.document.access_hash = paramMessage.media.document.access_hash;
          localObject3 = null;
        }
        for (i = 0;; i++)
        {
          localObject2 = localObject3;
          if (i < localMessage.media.document.attributes.size())
          {
            localObject2 = (TLRPC.DocumentAttribute)localMessage.media.document.attributes.get(i);
            if ((localObject2 instanceof TLRPC.TL_documentAttributeAudio)) {
              localObject2 = ((TLRPC.DocumentAttribute)localObject2).waveform;
            }
          }
          else
          {
            localMessage.media.document.attributes = paramMessage.media.document.attributes;
            if (localObject2 == null) {
              break label1399;
            }
            for (i = 0; i < localMessage.media.document.attributes.size(); i++)
            {
              localObject3 = (TLRPC.DocumentAttribute)localMessage.media.document.attributes.get(i);
              if ((localObject3 instanceof TLRPC.TL_documentAttributeAudio))
              {
                ((TLRPC.DocumentAttribute)localObject3).waveform = ((byte[])localObject2);
                ((TLRPC.DocumentAttribute)localObject3).flags |= 0x4;
              }
            }
            if ((MessageObject.isVoiceMessage(paramMessage)) || (MessageObject.isRoundVideoMessage(paramMessage)) || (paramMessage.media.ttl_seconds != 0)) {
              break;
            }
            MessagesStorage.getInstance(this.currentAccount).putSentFile(paramString, paramMessage.media.document, 1);
            break;
            label1312:
            if ((localObject2 != null) && (MessageObject.isStickerMessage(paramMessage)) && (((TLRPC.PhotoSize)localObject2).location != null))
            {
              localPhotoSize.location = ((TLRPC.PhotoSize)localObject2).location;
              break label1036;
            }
            if (((localObject2 == null) || (!(((TLRPC.PhotoSize)localObject2).location instanceof TLRPC.TL_fileLocationUnavailable))) && (!(localObject2 instanceof TLRPC.TL_photoSizeEmpty))) {
              break label1036;
            }
            localMessage.media.document.thumb = paramMessage.media.document.thumb;
            break label1036;
          }
        }
        label1399:
        localMessage.media.document.size = paramMessage.media.document.size;
        localMessage.media.document.mime_type = paramMessage.media.document.mime_type;
        if (((paramMessage.flags & 0x4) == 0) && (MessageObject.isOut(paramMessage)))
        {
          if (MessageObject.isNewGifDocument(paramMessage.media.document)) {
            DataQuery.getInstance(this.currentAccount).addRecentGif(paramMessage.media.document, paramMessage.date);
          }
        }
        else
        {
          label1491:
          if ((localMessage.attachPath == null) || (!localMessage.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath()))) {
            break label1715;
          }
          localObject2 = new File(localMessage.attachPath);
          localObject3 = paramMessage.media.document;
          if (paramMessage.media.ttl_seconds == 0) {
            break label1632;
          }
        }
        label1632:
        for (paramBoolean = true;; paramBoolean = false)
        {
          localObject3 = FileLoader.getPathToAttach((TLObject)localObject3, paramBoolean);
          if (((File)localObject2).renameTo((File)localObject3)) {
            break label1638;
          }
          paramMessage.attachPath = localMessage.attachPath;
          paramMessage.message = localMessage.message;
          break;
          if (!MessageObject.isStickerDocument(paramMessage.media.document)) {
            break label1491;
          }
          DataQuery.getInstance(this.currentAccount).addRecentSticker(0, paramMessage.media.document, paramMessage.date, false);
          break label1491;
        }
        label1638:
        if (MessageObject.isVideoMessage(paramMessage))
        {
          paramMessageObject.attachPathExists = true;
        }
        else
        {
          paramMessageObject.mediaExists = paramMessageObject.attachPathExists;
          paramMessageObject.attachPathExists = false;
          localMessage.attachPath = "";
          if ((paramString != null) && (paramString.startsWith("http")))
          {
            MessagesStorage.getInstance(this.currentAccount).addRecentLocalFile(paramString, ((File)localObject3).toString(), localMessage.media.document);
            continue;
            label1715:
            paramMessage.attachPath = localMessage.attachPath;
            paramMessage.message = localMessage.message;
          }
        }
      }
      else if (((paramMessage.media instanceof TLRPC.TL_messageMediaContact)) && ((localMessage.media instanceof TLRPC.TL_messageMediaContact)))
      {
        localMessage.media = paramMessage.media;
      }
      else if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      {
        localMessage.media = paramMessage.media;
      }
      else if ((paramMessage.media instanceof TLRPC.TL_messageMediaGame))
      {
        localMessage.media = paramMessage.media;
        if (((localMessage.media instanceof TLRPC.TL_messageMediaGame)) && (!TextUtils.isEmpty(paramMessage.message)))
        {
          localMessage.entities = paramMessage.entities;
          localMessage.message = paramMessage.message;
        }
      }
    }
  }
  
  private void uploadMultiMedia(final DelayedMessage paramDelayedMessage, final TLRPC.InputMedia paramInputMedia, TLRPC.InputEncryptedFile paramInputEncryptedFile, String paramString)
  {
    if (paramInputMedia != null)
    {
      paramInputEncryptedFile = (TLRPC.TL_messages_sendMultiMedia)paramDelayedMessage.sendRequest;
      i = 0;
      if (i < paramInputEncryptedFile.multi_media.size())
      {
        if (((TLRPC.TL_inputSingleMedia)paramInputEncryptedFile.multi_media.get(i)).media == paramInputMedia)
        {
          putToSendingMessages((TLRPC.Message)paramDelayedMessage.messages.get(i));
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileUploadProgressChanged, new Object[] { paramString, Float.valueOf(1.0F), Boolean.valueOf(false) });
        }
      }
      else
      {
        paramInputEncryptedFile = new TLRPC.TL_messages_uploadMedia();
        paramInputEncryptedFile.media = paramInputMedia;
        paramInputEncryptedFile.peer = ((TLRPC.TL_messages_sendMultiMedia)paramDelayedMessage.sendRequest).peer;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramInputEncryptedFile, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                TLRPC.TL_messages_sendMultiMedia localTL_messages_sendMultiMedia = null;
                Object localObject = localTL_messages_sendMultiMedia;
                TLRPC.MessageMedia localMessageMedia;
                int i;
                if (paramAnonymousTLObject != null)
                {
                  localMessageMedia = (TLRPC.MessageMedia)paramAnonymousTLObject;
                  if (((SendMessagesHelper.10.this.val$inputMedia instanceof TLRPC.TL_inputMediaUploadedPhoto)) && ((localMessageMedia instanceof TLRPC.TL_messageMediaPhoto)))
                  {
                    localObject = new TLRPC.TL_inputMediaPhoto();
                    ((TLRPC.TL_inputMediaPhoto)localObject).id = new TLRPC.TL_inputPhoto();
                    ((TLRPC.TL_inputMediaPhoto)localObject).id.id = localMessageMedia.photo.id;
                    ((TLRPC.TL_inputMediaPhoto)localObject).id.access_hash = localMessageMedia.photo.access_hash;
                  }
                }
                else
                {
                  if (localObject == null) {
                    break label297;
                  }
                  if (SendMessagesHelper.10.this.val$inputMedia.ttl_seconds != 0)
                  {
                    ((TLRPC.InputMedia)localObject).ttl_seconds = SendMessagesHelper.10.this.val$inputMedia.ttl_seconds;
                    ((TLRPC.InputMedia)localObject).flags |= 0x1;
                  }
                  localTL_messages_sendMultiMedia = (TLRPC.TL_messages_sendMultiMedia)SendMessagesHelper.10.this.val$message.sendRequest;
                  i = 0;
                  label144:
                  if (i < localTL_messages_sendMultiMedia.multi_media.size())
                  {
                    if (((TLRPC.TL_inputSingleMedia)localTL_messages_sendMultiMedia.multi_media.get(i)).media != SendMessagesHelper.10.this.val$inputMedia) {
                      break label291;
                    }
                    ((TLRPC.TL_inputSingleMedia)localTL_messages_sendMultiMedia.multi_media.get(i)).media = ((TLRPC.InputMedia)localObject);
                  }
                  SendMessagesHelper.this.sendReadyToSendGroup(SendMessagesHelper.10.this.val$message, false, true);
                }
                for (;;)
                {
                  return;
                  localObject = localTL_messages_sendMultiMedia;
                  if (!(SendMessagesHelper.10.this.val$inputMedia instanceof TLRPC.TL_inputMediaUploadedDocument)) {
                    break;
                  }
                  localObject = localTL_messages_sendMultiMedia;
                  if (!(localMessageMedia instanceof TLRPC.TL_messageMediaDocument)) {
                    break;
                  }
                  localObject = new TLRPC.TL_inputMediaDocument();
                  ((TLRPC.TL_inputMediaDocument)localObject).id = new TLRPC.TL_inputDocument();
                  ((TLRPC.TL_inputMediaDocument)localObject).id.id = localMessageMedia.document.id;
                  ((TLRPC.TL_inputMediaDocument)localObject).id.access_hash = localMessageMedia.document.access_hash;
                  break;
                  label291:
                  i++;
                  break label144;
                  label297:
                  SendMessagesHelper.10.this.val$message.markAsError();
                }
              }
            });
          }
        });
      }
    }
    while (paramInputEncryptedFile == null) {
      for (;;)
      {
        return;
        i++;
      }
    }
    paramInputMedia = (TLRPC.TL_messages_sendEncryptedMultiMedia)paramDelayedMessage.sendEncryptedRequest;
    for (int i = 0;; i++) {
      if (i < paramInputMedia.files.size())
      {
        if (paramInputMedia.files.get(i) == paramInputEncryptedFile)
        {
          putToSendingMessages((TLRPC.Message)paramDelayedMessage.messages.get(i));
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileUploadProgressChanged, new Object[] { paramString, Float.valueOf(1.0F), Boolean.valueOf(false) });
        }
      }
      else
      {
        sendReadyToSendGroup(paramDelayedMessage, false, true);
        break;
      }
    }
  }
  
  public void cancelSendingMessage(MessageObject paramMessageObject)
  {
    ArrayList localArrayList = new ArrayList();
    boolean bool = false;
    Iterator localIterator = this.delayedMessages.entrySet().iterator();
    label101:
    label271:
    label306:
    label500:
    while (localIterator.hasNext())
    {
      localObject1 = (Map.Entry)localIterator.next();
      Object localObject2 = (ArrayList)((Map.Entry)localObject1).getValue();
      for (i = 0;; i++)
      {
        if (i >= ((ArrayList)localObject2).size()) {
          break label500;
        }
        DelayedMessage localDelayedMessage = (DelayedMessage)((ArrayList)localObject2).get(i);
        if (localDelayedMessage.type == 4)
        {
          int j = -1;
          localObject1 = null;
          i = 0;
          int k = j;
          if (i < localDelayedMessage.messageObjects.size())
          {
            localObject1 = (MessageObject)localDelayedMessage.messageObjects.get(i);
            if (((MessageObject)localObject1).getId() == paramMessageObject.getId()) {
              k = i;
            }
          }
          else
          {
            if (k < 0) {
              break;
            }
            localDelayedMessage.messageObjects.remove(k);
            localDelayedMessage.messages.remove(k);
            localDelayedMessage.originalPaths.remove(k);
            if (localDelayedMessage.sendRequest == null) {
              break label271;
            }
            ((TLRPC.TL_messages_sendMultiMedia)localDelayedMessage.sendRequest).multi_media.remove(k);
          }
          for (;;)
          {
            MediaController.getInstance().cancelVideoConvert(paramMessageObject);
            localObject1 = (String)localDelayedMessage.extraHashMap.get(localObject1);
            if (localObject1 != null) {
              localArrayList.add(localObject1);
            }
            if (!localDelayedMessage.messageObjects.isEmpty()) {
              break label306;
            }
            localDelayedMessage.sendDelayedRequests();
            break;
            i++;
            break label101;
            localObject2 = (TLRPC.TL_messages_sendEncryptedMultiMedia)localDelayedMessage.sendEncryptedRequest;
            ((TLRPC.TL_messages_sendEncryptedMultiMedia)localObject2).messages.remove(k);
            ((TLRPC.TL_messages_sendEncryptedMultiMedia)localObject2).files.remove(k);
          }
          if (localDelayedMessage.finalGroupMessage == paramMessageObject.getId())
          {
            localObject1 = (MessageObject)localDelayedMessage.messageObjects.get(localDelayedMessage.messageObjects.size() - 1);
            localDelayedMessage.finalGroupMessage = ((MessageObject)localObject1).getId();
            ((MessageObject)localObject1).messageOwner.params.put("final", "1");
            localObject2 = new TLRPC.TL_messages_messages();
            ((TLRPC.TL_messages_messages)localObject2).messages.add(((MessageObject)localObject1).messageOwner);
            MessagesStorage.getInstance(this.currentAccount).putMessages((TLRPC.messages_Messages)localObject2, localDelayedMessage.peer, -2, 0, false);
          }
          sendReadyToSendGroup(localDelayedMessage, false, true);
          break;
        }
        if (localDelayedMessage.obj.getId() == paramMessageObject.getId())
        {
          ((ArrayList)localObject2).remove(i);
          localDelayedMessage.sendDelayedRequests();
          MediaController.getInstance().cancelVideoConvert(localDelayedMessage.obj);
          if (((ArrayList)localObject2).size() != 0) {
            break;
          }
          localArrayList.add(((Map.Entry)localObject1).getKey());
          if (localDelayedMessage.sendEncryptedRequest == null) {
            break;
          }
          bool = true;
          break;
        }
      }
    }
    int i = 0;
    if (i < localArrayList.size())
    {
      localObject1 = (String)localArrayList.get(i);
      if (((String)localObject1).startsWith("http")) {
        ImageLoader.getInstance().cancelLoadHttpFile((String)localObject1);
      }
      for (;;)
      {
        stopVideoService((String)localObject1);
        this.delayedMessages.remove(localObject1);
        i++;
        break;
        FileLoader.getInstance(this.currentAccount).cancelUploadFile((String)localObject1, bool);
      }
    }
    Object localObject1 = new ArrayList();
    ((ArrayList)localObject1).add(Integer.valueOf(paramMessageObject.getId()));
    MessagesController.getInstance(this.currentAccount).deleteMessages((ArrayList)localObject1, null, null, paramMessageObject.messageOwner.to_id.channel_id, false);
  }
  
  public void checkUnsentMessages()
  {
    MessagesStorage.getInstance(this.currentAccount).getUnsentMessages(1000);
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
  
  public void didReceivedNotification(int paramInt1, int paramInt2, final Object... paramVarArgs)
  {
    final Object localObject1;
    final Object localObject2;
    final Object localObject3;
    Object localObject4;
    label99:
    label146:
    label220:
    label742:
    label940:
    long l1;
    if (paramInt1 == NotificationCenter.FileDidUpload)
    {
      localObject1 = (String)paramVarArgs[0];
      TLRPC.InputFile localInputFile = (TLRPC.InputFile)paramVarArgs[1];
      TLRPC.InputEncryptedFile localInputEncryptedFile1 = (TLRPC.InputEncryptedFile)paramVarArgs[2];
      localObject2 = (ArrayList)this.delayedMessages.get(localObject1);
      if (localObject2 != null)
      {
        paramInt1 = 0;
        if (paramInt1 < ((ArrayList)localObject2).size())
        {
          localObject3 = (DelayedMessage)((ArrayList)localObject2).get(paramInt1);
          localObject4 = null;
          if ((((DelayedMessage)localObject3).sendRequest instanceof TLRPC.TL_messages_sendMedia))
          {
            localObject4 = ((TLRPC.TL_messages_sendMedia)((DelayedMessage)localObject3).sendRequest).media;
            if ((localInputFile == null) || (localObject4 == null)) {
              break label742;
            }
            if (((DelayedMessage)localObject3).type != 0) {
              break label220;
            }
            ((TLRPC.InputMedia)localObject4).file = localInputFile;
            performSendMessageRequest(((DelayedMessage)localObject3).sendRequest, ((DelayedMessage)localObject3).obj, ((DelayedMessage)localObject3).originalPath, (DelayedMessage)localObject3, true);
            ((ArrayList)localObject2).remove(paramInt1);
            paramInt2 = paramInt1 - 1;
          }
          Object localObject5;
          do
          {
            do
            {
              paramInt1 = paramInt2 + 1;
              break;
              if ((((DelayedMessage)localObject3).sendRequest instanceof TLRPC.TL_messages_sendBroadcast))
              {
                localObject4 = ((TLRPC.TL_messages_sendBroadcast)((DelayedMessage)localObject3).sendRequest).media;
                break label99;
              }
              if (!(((DelayedMessage)localObject3).sendRequest instanceof TLRPC.TL_messages_sendMultiMedia)) {
                break label99;
              }
              localObject4 = (TLRPC.InputMedia)((DelayedMessage)localObject3).extraHashMap.get(localObject1);
              break label99;
              if (((DelayedMessage)localObject3).type == 1)
              {
                if (((TLRPC.InputMedia)localObject4).file == null)
                {
                  ((TLRPC.InputMedia)localObject4).file = localInputFile;
                  if ((((TLRPC.InputMedia)localObject4).thumb == null) && (((DelayedMessage)localObject3).location != null))
                  {
                    performSendDelayedMessage((DelayedMessage)localObject3);
                    break label146;
                  }
                  performSendMessageRequest(((DelayedMessage)localObject3).sendRequest, ((DelayedMessage)localObject3).obj, ((DelayedMessage)localObject3).originalPath);
                  break label146;
                }
                ((TLRPC.InputMedia)localObject4).thumb = localInputFile;
                ((TLRPC.InputMedia)localObject4).flags |= 0x4;
                performSendMessageRequest(((DelayedMessage)localObject3).sendRequest, ((DelayedMessage)localObject3).obj, ((DelayedMessage)localObject3).originalPath);
                break label146;
              }
              if (((DelayedMessage)localObject3).type == 2)
              {
                if (((TLRPC.InputMedia)localObject4).file == null)
                {
                  ((TLRPC.InputMedia)localObject4).file = localInputFile;
                  if ((((TLRPC.InputMedia)localObject4).thumb == null) && (((DelayedMessage)localObject3).location != null))
                  {
                    performSendDelayedMessage((DelayedMessage)localObject3);
                    break label146;
                  }
                  performSendMessageRequest(((DelayedMessage)localObject3).sendRequest, ((DelayedMessage)localObject3).obj, ((DelayedMessage)localObject3).originalPath);
                  break label146;
                }
                ((TLRPC.InputMedia)localObject4).thumb = localInputFile;
                ((TLRPC.InputMedia)localObject4).flags |= 0x4;
                performSendMessageRequest(((DelayedMessage)localObject3).sendRequest, ((DelayedMessage)localObject3).obj, ((DelayedMessage)localObject3).originalPath);
                break label146;
              }
              if (((DelayedMessage)localObject3).type == 3)
              {
                ((TLRPC.InputMedia)localObject4).file = localInputFile;
                performSendMessageRequest(((DelayedMessage)localObject3).sendRequest, ((DelayedMessage)localObject3).obj, ((DelayedMessage)localObject3).originalPath);
                break label146;
              }
              if (((DelayedMessage)localObject3).type != 4) {
                break label146;
              }
              if ((localObject4 instanceof TLRPC.TL_inputMediaUploadedDocument))
              {
                if (((TLRPC.InputMedia)localObject4).file == null)
                {
                  ((TLRPC.InputMedia)localObject4).file = localInputFile;
                  localObject5 = (MessageObject)((DelayedMessage)localObject3).extraHashMap.get((String)localObject1 + "_i");
                  paramInt2 = ((DelayedMessage)localObject3).messageObjects.indexOf(localObject5);
                  ((DelayedMessage)localObject3).location = ((TLRPC.FileLocation)((DelayedMessage)localObject3).extraHashMap.get((String)localObject1 + "_t"));
                  stopVideoService(((MessageObject)((DelayedMessage)localObject3).messageObjects.get(paramInt2)).messageOwner.attachPath);
                  if ((((TLRPC.InputMedia)localObject4).thumb == null) && (((DelayedMessage)localObject3).location != null))
                  {
                    performSendDelayedMessage((DelayedMessage)localObject3, paramInt2);
                    break label146;
                  }
                  uploadMultiMedia((DelayedMessage)localObject3, (TLRPC.InputMedia)localObject4, null, (String)localObject1);
                  break label146;
                }
                ((TLRPC.InputMedia)localObject4).thumb = localInputFile;
                ((TLRPC.InputMedia)localObject4).flags |= 0x4;
                uploadMultiMedia((DelayedMessage)localObject3, (TLRPC.InputMedia)localObject4, null, (String)((DelayedMessage)localObject3).extraHashMap.get((String)localObject1 + "_o"));
                break label146;
              }
              ((TLRPC.InputMedia)localObject4).file = localInputFile;
              uploadMultiMedia((DelayedMessage)localObject3, (TLRPC.InputMedia)localObject4, null, (String)localObject1);
              break label146;
              paramInt2 = paramInt1;
            } while (localInputEncryptedFile1 == null);
            paramInt2 = paramInt1;
          } while (((DelayedMessage)localObject3).sendEncryptedRequest == null);
          localObject4 = null;
          if (((DelayedMessage)localObject3).type == 4)
          {
            localObject5 = (TLRPC.TL_messages_sendEncryptedMultiMedia)((DelayedMessage)localObject3).sendEncryptedRequest;
            TLRPC.InputEncryptedFile localInputEncryptedFile2 = (TLRPC.InputEncryptedFile)((DelayedMessage)localObject3).extraHashMap.get(localObject1);
            paramInt2 = ((TLRPC.TL_messages_sendEncryptedMultiMedia)localObject5).files.indexOf(localInputEncryptedFile2);
            if (paramInt2 >= 0)
            {
              ((TLRPC.TL_messages_sendEncryptedMultiMedia)localObject5).files.set(paramInt2, localInputEncryptedFile1);
              if (localInputEncryptedFile2.id == 1L)
              {
                localObject4 = (MessageObject)((DelayedMessage)localObject3).extraHashMap.get((String)localObject1 + "_i");
                ((DelayedMessage)localObject3).location = ((TLRPC.FileLocation)((DelayedMessage)localObject3).extraHashMap.get((String)localObject1 + "_t"));
                stopVideoService(((MessageObject)((DelayedMessage)localObject3).messageObjects.get(paramInt2)).messageOwner.attachPath);
              }
              localObject4 = (TLRPC.TL_decryptedMessage)((TLRPC.TL_messages_sendEncryptedMultiMedia)localObject5).messages.get(paramInt2);
            }
            if (localObject4 != null)
            {
              if (((((TLRPC.TL_decryptedMessage)localObject4).media instanceof TLRPC.TL_decryptedMessageMediaVideo)) || ((((TLRPC.TL_decryptedMessage)localObject4).media instanceof TLRPC.TL_decryptedMessageMediaPhoto)) || ((((TLRPC.TL_decryptedMessage)localObject4).media instanceof TLRPC.TL_decryptedMessageMediaDocument)))
              {
                l1 = ((Long)paramVarArgs[5]).longValue();
                ((TLRPC.TL_decryptedMessage)localObject4).media.size = ((int)l1);
              }
              ((TLRPC.TL_decryptedMessage)localObject4).media.key = ((byte[])paramVarArgs[3]);
              ((TLRPC.TL_decryptedMessage)localObject4).media.iv = ((byte[])paramVarArgs[4]);
              if (((DelayedMessage)localObject3).type != 4) {
                break label1075;
              }
              uploadMultiMedia((DelayedMessage)localObject3, null, localInputEncryptedFile1, (String)localObject1);
            }
          }
          for (;;)
          {
            ((ArrayList)localObject2).remove(paramInt1);
            paramInt2 = paramInt1 - 1;
            break;
            localObject4 = (TLRPC.TL_decryptedMessage)((DelayedMessage)localObject3).sendEncryptedRequest;
            break label940;
            label1075:
            SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest((TLRPC.DecryptedMessage)localObject4, ((DelayedMessage)localObject3).obj.messageOwner, ((DelayedMessage)localObject3).encryptedChat, localInputEncryptedFile1, ((DelayedMessage)localObject3).originalPath, ((DelayedMessage)localObject3).obj);
          }
        }
        if (((ArrayList)localObject2).isEmpty()) {
          this.delayedMessages.remove(localObject1);
        }
      }
    }
    for (;;)
    {
      return;
      boolean bool;
      if (paramInt1 == NotificationCenter.FileDidFailUpload)
      {
        localObject4 = (String)paramVarArgs[0];
        bool = ((Boolean)paramVarArgs[1]).booleanValue();
        paramVarArgs = (ArrayList)this.delayedMessages.get(localObject4);
        if (paramVarArgs != null)
        {
          for (paramInt1 = 0; paramInt1 < paramVarArgs.size(); paramInt1 = paramInt2 + 1)
          {
            localObject2 = (DelayedMessage)paramVarArgs.get(paramInt1);
            if ((!bool) || (((DelayedMessage)localObject2).sendEncryptedRequest == null))
            {
              paramInt2 = paramInt1;
              if (!bool)
              {
                paramInt2 = paramInt1;
                if (((DelayedMessage)localObject2).sendRequest == null) {}
              }
            }
            else
            {
              ((DelayedMessage)localObject2).markAsError();
              paramVarArgs.remove(paramInt1);
              paramInt2 = paramInt1 - 1;
            }
          }
          if (paramVarArgs.isEmpty()) {
            this.delayedMessages.remove(localObject4);
          }
        }
      }
      else if (paramInt1 == NotificationCenter.FilePreparingStarted)
      {
        localObject4 = (MessageObject)paramVarArgs[0];
        if (((MessageObject)localObject4).getId() != 0)
        {
          paramVarArgs = (String)paramVarArgs[1];
          localObject2 = (ArrayList)this.delayedMessages.get(((MessageObject)localObject4).messageOwner.attachPath);
          if (localObject2 != null) {
            label1444:
            label1471:
            label1473:
            for (paramInt1 = 0;; paramInt1++)
            {
              if (paramInt1 < ((ArrayList)localObject2).size())
              {
                paramVarArgs = (DelayedMessage)((ArrayList)localObject2).get(paramInt1);
                if (paramVarArgs.type != 4) {
                  break label1444;
                }
                paramInt2 = paramVarArgs.messageObjects.indexOf(localObject4);
                paramVarArgs.location = ((TLRPC.FileLocation)paramVarArgs.extraHashMap.get(((MessageObject)localObject4).messageOwner.attachPath + "_t"));
                performSendDelayedMessage(paramVarArgs, paramInt2);
                ((ArrayList)localObject2).remove(paramInt1);
              }
              for (;;)
              {
                if (!((ArrayList)localObject2).isEmpty()) {
                  break label1471;
                }
                this.delayedMessages.remove(((MessageObject)localObject4).messageOwner.attachPath);
                break;
                if (paramVarArgs.obj != localObject4) {
                  break label1473;
                }
                paramVarArgs.videoEditedInfo = null;
                performSendDelayedMessage(paramVarArgs);
                ((ArrayList)localObject2).remove(paramInt1);
              }
              break;
            }
          }
        }
      }
      else if (paramInt1 == NotificationCenter.FileNewChunkAvailable)
      {
        localObject4 = (MessageObject)paramVarArgs[0];
        if (((MessageObject)localObject4).getId() != 0)
        {
          localObject2 = (String)paramVarArgs[1];
          l1 = ((Long)paramVarArgs[2]).longValue();
          long l2 = ((Long)paramVarArgs[3]).longValue();
          if ((int)((MessageObject)localObject4).getDialogId() == 0)
          {
            bool = true;
            label1544:
            FileLoader.getInstance(this.currentAccount).checkUploadNewDataAvailable((String)localObject2, bool, l1, l2);
            if (l2 == 0L) {
              continue;
            }
            localObject2 = (ArrayList)this.delayedMessages.get(((MessageObject)localObject4).messageOwner.attachPath);
            if (localObject2 == null) {
              continue;
            }
            paramInt1 = 0;
            label1596:
            if (paramInt1 < ((ArrayList)localObject2).size())
            {
              paramVarArgs = (DelayedMessage)((ArrayList)localObject2).get(paramInt1);
              if (paramVarArgs.type != 4) {
                break label1745;
              }
              paramInt2 = 0;
              if (paramInt2 < paramVarArgs.messageObjects.size())
              {
                localObject3 = (MessageObject)paramVarArgs.messageObjects.get(paramInt2);
                if (localObject3 != localObject4) {
                  break label1739;
                }
                ((MessageObject)localObject3).videoEditedInfo = null;
                ((MessageObject)localObject3).messageOwner.params.remove("ve");
                ((MessageObject)localObject3).messageOwner.media.document.size = ((int)l2);
                paramVarArgs = new ArrayList();
                paramVarArgs.add(((MessageObject)localObject3).messageOwner);
                MessagesStorage.getInstance(this.currentAccount).putMessages(paramVarArgs, false, true, false, 0);
              }
            }
          }
          label1739:
          label1745:
          while (paramVarArgs.obj != localObject4) {
            for (;;)
            {
              paramInt1++;
              break label1596;
              break;
              bool = false;
              break label1544;
              paramInt2++;
            }
          }
          paramVarArgs.obj.videoEditedInfo = null;
          paramVarArgs.obj.messageOwner.params.remove("ve");
          paramVarArgs.obj.messageOwner.media.document.size = ((int)l2);
          localObject4 = new ArrayList();
          ((ArrayList)localObject4).add(paramVarArgs.obj.messageOwner);
          MessagesStorage.getInstance(this.currentAccount).putMessages((ArrayList)localObject4, false, true, false, 0);
        }
      }
      else if (paramInt1 == NotificationCenter.FilePreparingFailed)
      {
        localObject4 = (MessageObject)paramVarArgs[0];
        if (((MessageObject)localObject4).getId() != 0)
        {
          localObject2 = (String)paramVarArgs[1];
          stopVideoService(((MessageObject)localObject4).messageOwner.attachPath);
          paramVarArgs = (ArrayList)this.delayedMessages.get(localObject2);
          if (paramVarArgs != null)
          {
            paramInt1 = 0;
            if (paramInt1 < paramVarArgs.size())
            {
              localObject3 = (DelayedMessage)paramVarArgs.get(paramInt1);
              int i;
              if (((DelayedMessage)localObject3).type == 4)
              {
                i = 0;
                label1931:
                paramInt2 = paramInt1;
                if (i < ((DelayedMessage)localObject3).messages.size())
                {
                  if (((DelayedMessage)localObject3).messageObjects.get(i) != localObject4) {
                    break label1983;
                  }
                  ((DelayedMessage)localObject3).markAsError();
                  paramVarArgs.remove(paramInt1);
                  paramInt2 = paramInt1 - 1;
                }
              }
              for (;;)
              {
                paramInt1 = paramInt2 + 1;
                break;
                label1983:
                i++;
                break label1931;
                paramInt2 = paramInt1;
                if (((DelayedMessage)localObject3).obj == localObject4)
                {
                  ((DelayedMessage)localObject3).markAsError();
                  paramVarArgs.remove(paramInt1);
                  paramInt2 = paramInt1 - 1;
                }
              }
            }
            if (paramVarArgs.isEmpty()) {
              this.delayedMessages.remove(localObject2);
            }
          }
        }
      }
      else if (paramInt1 == NotificationCenter.httpFileDidLoaded)
      {
        localObject2 = (String)paramVarArgs[0];
        localObject4 = (ArrayList)this.delayedMessages.get(localObject2);
        if (localObject4 != null)
        {
          paramInt2 = 0;
          if (paramInt2 < ((ArrayList)localObject4).size())
          {
            localObject3 = (DelayedMessage)((ArrayList)localObject4).get(paramInt2);
            paramInt1 = -1;
            if (((DelayedMessage)localObject3).type == 0)
            {
              paramInt1 = 0;
              paramVarArgs = ((DelayedMessage)localObject3).obj;
              label2113:
              if (paramInt1 != 0) {
                break label2261;
              }
              localObject1 = Utilities.MD5((String)localObject2) + "." + ImageLoader.getHttpUrlExtension((String)localObject2, "file");
              localObject1 = new File(FileLoader.getDirectory(4), (String)localObject1);
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
                        SendMessagesHelper.3.this.val$messageObject.messageOwner.media.photo = this.val$photo;
                        SendMessagesHelper.3.this.val$messageObject.messageOwner.attachPath = SendMessagesHelper.3.this.val$cacheFile.toString();
                        ArrayList localArrayList = new ArrayList();
                        localArrayList.add(SendMessagesHelper.3.this.val$messageObject.messageOwner);
                        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(localArrayList, false, true, false, 0);
                        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, new Object[] { SendMessagesHelper.3.this.val$messageObject.messageOwner });
                        SendMessagesHelper.3.this.val$message.location = ((TLRPC.PhotoSize)this.val$photo.sizes.get(this.val$photo.sizes.size() - 1)).location;
                        SendMessagesHelper.3.this.val$message.httpLocation = null;
                        if (SendMessagesHelper.3.this.val$message.type == 4) {
                          SendMessagesHelper.this.performSendDelayedMessage(SendMessagesHelper.3.this.val$message, SendMessagesHelper.3.this.val$message.messageObjects.indexOf(SendMessagesHelper.3.this.val$messageObject));
                        }
                      }
                      for (;;)
                      {
                        return;
                        SendMessagesHelper.this.performSendDelayedMessage(SendMessagesHelper.3.this.val$message);
                        continue;
                        if (BuildVars.LOGS_ENABLED) {
                          FileLog.e("can't load image " + SendMessagesHelper.3.this.val$path + " to file " + SendMessagesHelper.3.this.val$cacheFile.toString());
                        }
                        SendMessagesHelper.3.this.val$message.markAsError();
                      }
                    }
                  });
                }
              });
            }
            for (;;)
            {
              paramInt2++;
              break;
              if (((DelayedMessage)localObject3).type == 2)
              {
                paramInt1 = 1;
                paramVarArgs = ((DelayedMessage)localObject3).obj;
                break label2113;
              }
              if (((DelayedMessage)localObject3).type == 4)
              {
                paramVarArgs = (MessageObject)((DelayedMessage)localObject3).extraHashMap.get(localObject2);
                if (paramVarArgs.getDocument() != null)
                {
                  paramInt1 = 1;
                  break label2113;
                }
                paramInt1 = 0;
                break label2113;
              }
              paramVarArgs = null;
              break label2113;
              label2261:
              if (paramInt1 == 1)
              {
                localObject1 = Utilities.MD5((String)localObject2) + ".gif";
                localObject1 = new File(FileLoader.getDirectory(4), (String)localObject1);
                Utilities.globalQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    boolean bool = true;
                    localDocument = localObject3.obj.getDocument();
                    if ((localDocument.thumb.location instanceof TLRPC.TL_fileLocationUnavailable)) {}
                    for (;;)
                    {
                      try
                      {
                        Bitmap localBitmap = ImageLoader.loadBitmap(localObject1.getAbsolutePath(), null, 90.0F, 90.0F, true);
                        if (localBitmap != null)
                        {
                          if (localObject3.sendEncryptedRequest == null) {
                            continue;
                          }
                          localDocument.thumb = ImageLoader.scaleAndSaveImage(localBitmap, 90.0F, 90.0F, 55, bool);
                          localBitmap.recycle();
                        }
                      }
                      catch (Exception localException)
                      {
                        localDocument.thumb = null;
                        FileLog.e(localException);
                        continue;
                      }
                      if (localDocument.thumb == null)
                      {
                        localDocument.thumb = new TLRPC.TL_photoSizeEmpty();
                        localDocument.thumb.type = "s";
                      }
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          SendMessagesHelper.4.this.val$message.httpLocation = null;
                          SendMessagesHelper.4.this.val$message.obj.messageOwner.attachPath = SendMessagesHelper.4.this.val$cacheFile.toString();
                          SendMessagesHelper.4.this.val$message.location = localDocument.thumb.location;
                          ArrayList localArrayList = new ArrayList();
                          localArrayList.add(SendMessagesHelper.4.this.val$messageObject.messageOwner);
                          MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(localArrayList, false, true, false, 0);
                          SendMessagesHelper.this.performSendDelayedMessage(SendMessagesHelper.4.this.val$message);
                          NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, new Object[] { SendMessagesHelper.4.this.val$message.obj.messageOwner });
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
          this.delayedMessages.remove(localObject2);
        }
      }
      else if (paramInt1 == NotificationCenter.FileDidLoaded)
      {
        paramVarArgs = (String)paramVarArgs[0];
        localObject4 = (ArrayList)this.delayedMessages.get(paramVarArgs);
        if (localObject4 != null)
        {
          for (paramInt1 = 0; paramInt1 < ((ArrayList)localObject4).size(); paramInt1++) {
            performSendDelayedMessage((DelayedMessage)((ArrayList)localObject4).get(paramInt1));
          }
          this.delayedMessages.remove(paramVarArgs);
        }
      }
      else if ((paramInt1 == NotificationCenter.httpFileDidFailedLoad) || (paramInt1 == NotificationCenter.FileDidFailedLoad))
      {
        paramVarArgs = (String)paramVarArgs[0];
        localObject4 = (ArrayList)this.delayedMessages.get(paramVarArgs);
        if (localObject4 != null)
        {
          for (paramInt1 = 0; paramInt1 < ((ArrayList)localObject4).size(); paramInt1++) {
            ((DelayedMessage)((ArrayList)localObject4).get(paramInt1)).markAsError();
          }
          this.delayedMessages.remove(paramVarArgs);
        }
      }
    }
  }
  
  public int editMessage(MessageObject paramMessageObject, String paramString, boolean paramBoolean, final BaseFragment paramBaseFragment, ArrayList<TLRPC.MessageEntity> paramArrayList, final Runnable paramRunnable)
  {
    boolean bool = false;
    int i = 0;
    int j = i;
    if (paramBaseFragment != null)
    {
      j = i;
      if (paramBaseFragment.getParentActivity() != null) {
        if (paramRunnable != null) {
          break label39;
        }
      }
    }
    label39:
    final TLRPC.TL_messages_editMessage localTL_messages_editMessage;
    for (j = i;; j = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_editMessage, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                SendMessagesHelper.6.this.val$callback.run();
              }
            });
            if (paramAnonymousTL_error == null) {
              MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
            }
            for (;;)
            {
              return;
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  AlertsCreator.processError(SendMessagesHelper.this.currentAccount, paramAnonymousTL_error, SendMessagesHelper.6.this.val$fragment, SendMessagesHelper.6.this.val$req, new Object[0]);
                }
              });
            }
          }
        }))
    {
      return j;
      localTL_messages_editMessage = new TLRPC.TL_messages_editMessage();
      localTL_messages_editMessage.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int)paramMessageObject.getDialogId());
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
    }
  }
  
  public TLRPC.TL_photo generatePhotoSizes(String paramString, Uri paramUri)
  {
    Bitmap localBitmap1 = ImageLoader.loadBitmap(paramString, paramUri, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), true);
    Bitmap localBitmap2 = localBitmap1;
    if (localBitmap1 == null)
    {
      localBitmap2 = localBitmap1;
      if (AndroidUtilities.getPhotoSize() != 800) {
        localBitmap2 = ImageLoader.loadBitmap(paramString, paramUri, 800.0F, 800.0F, true);
      }
    }
    paramUri = new ArrayList();
    paramString = ImageLoader.scaleAndSaveImage(localBitmap2, 90.0F, 90.0F, 55, true);
    if (paramString != null) {
      paramUri.add(paramString);
    }
    paramString = ImageLoader.scaleAndSaveImage(localBitmap2, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
    if (paramString != null) {
      paramUri.add(paramString);
    }
    if (localBitmap2 != null) {
      localBitmap2.recycle();
    }
    if (paramUri.isEmpty()) {
      paramString = null;
    }
    for (;;)
    {
      return paramString;
      UserConfig.getInstance(this.currentAccount).saveConfig(false);
      paramString = new TLRPC.TL_photo();
      paramString.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      paramString.sizes = paramUri;
    }
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
    boolean bool;
    if ((paramMessageObject == null) || (paramKeyboardButton == null))
    {
      bool = false;
      return bool;
    }
    int i;
    if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame)) {
      i = 1;
    }
    for (;;)
    {
      paramMessageObject = paramMessageObject.getDialogId() + "_" + paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data) + "_" + i;
      bool = this.waitingForCallback.containsKey(paramMessageObject);
      break;
      if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonBuy)) {
        i = 2;
      } else {
        i = 0;
      }
    }
  }
  
  public boolean isSendingCurrentLocation(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    boolean bool;
    if ((paramMessageObject == null) || (paramKeyboardButton == null))
    {
      bool = false;
      return bool;
    }
    StringBuilder localStringBuilder = new StringBuilder().append(paramMessageObject.getDialogId()).append("_").append(paramMessageObject.getId()).append("_").append(Utilities.bytesToHex(paramKeyboardButton.data)).append("_");
    if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame)) {}
    for (paramMessageObject = "1";; paramMessageObject = "0")
    {
      paramMessageObject = paramMessageObject;
      bool = this.waitingForLocation.containsKey(paramMessageObject);
      break;
    }
  }
  
  public boolean isSendingMessage(int paramInt)
  {
    if (this.sendingMessages.indexOfKey(paramInt) >= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void processForwardFromMyName(MessageObject paramMessageObject, long paramLong)
  {
    if (paramMessageObject == null) {}
    for (;;)
    {
      return;
      Object localObject1;
      if ((paramMessageObject.messageOwner.media != null) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)))
      {
        if ((paramMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photo))
        {
          sendMessage((TLRPC.TL_photo)paramMessageObject.messageOwner.media.photo, null, paramLong, paramMessageObject.replyMessageObject, paramMessageObject.messageOwner.message, paramMessageObject.messageOwner.entities, null, null, paramMessageObject.messageOwner.media.ttl_seconds);
        }
        else if ((paramMessageObject.messageOwner.media.document instanceof TLRPC.TL_document))
        {
          sendMessage((TLRPC.TL_document)paramMessageObject.messageOwner.media.document, null, paramMessageObject.messageOwner.attachPath, paramLong, paramMessageObject.replyMessageObject, paramMessageObject.messageOwner.message, paramMessageObject.messageOwner.entities, null, null, paramMessageObject.messageOwner.media.ttl_seconds);
        }
        else if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)))
        {
          sendMessage(paramMessageObject.messageOwner.media, paramLong, paramMessageObject.replyMessageObject, null, null);
        }
        else if (paramMessageObject.messageOwner.media.phone_number != null)
        {
          localObject1 = new TLRPC.TL_userContact_old2();
          ((TLRPC.User)localObject1).phone = paramMessageObject.messageOwner.media.phone_number;
          ((TLRPC.User)localObject1).first_name = paramMessageObject.messageOwner.media.first_name;
          ((TLRPC.User)localObject1).last_name = paramMessageObject.messageOwner.media.last_name;
          ((TLRPC.User)localObject1).id = paramMessageObject.messageOwner.media.user_id;
          sendMessage((TLRPC.User)localObject1, paramLong, paramMessageObject.replyMessageObject, null, null);
        }
        else if ((int)paramLong != 0)
        {
          localObject1 = new ArrayList();
          ((ArrayList)localObject1).add(paramMessageObject);
          sendMessage((ArrayList)localObject1, paramLong);
        }
      }
      else if (paramMessageObject.messageOwner.message != null)
      {
        localObject1 = null;
        if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
          localObject1 = paramMessageObject.messageOwner.media.webpage;
        }
        if ((paramMessageObject.messageOwner.entities != null) && (!paramMessageObject.messageOwner.entities.isEmpty()))
        {
          ArrayList localArrayList = new ArrayList();
          for (int i = 0;; i++)
          {
            localObject2 = localArrayList;
            if (i >= paramMessageObject.messageOwner.entities.size()) {
              break;
            }
            localObject2 = (TLRPC.MessageEntity)paramMessageObject.messageOwner.entities.get(i);
            if (((localObject2 instanceof TLRPC.TL_messageEntityBold)) || ((localObject2 instanceof TLRPC.TL_messageEntityItalic)) || ((localObject2 instanceof TLRPC.TL_messageEntityPre)) || ((localObject2 instanceof TLRPC.TL_messageEntityCode)) || ((localObject2 instanceof TLRPC.TL_messageEntityTextUrl))) {
              localArrayList.add(localObject2);
            }
          }
        }
        Object localObject2 = null;
        sendMessage(paramMessageObject.messageOwner.message, paramLong, paramMessageObject.replyMessageObject, (TLRPC.WebPage)localObject1, true, (ArrayList)localObject2, null, null);
      }
      else if ((int)paramLong != 0)
      {
        localObject1 = new ArrayList();
        ((ArrayList)localObject1).add(paramMessageObject);
        sendMessage((ArrayList)localObject1, paramLong);
      }
    }
  }
  
  protected void processSentMessage(int paramInt)
  {
    int i = this.unsentMessages.size();
    this.unsentMessages.remove(paramInt);
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
        MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putUsers(paramArrayList1, true);
        MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putChats(paramArrayList2, true);
        MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putEncryptedChats(paramArrayList3, true);
        for (int i = 0; i < paramArrayList.size(); i++)
        {
          Object localObject = (TLRPC.Message)paramArrayList.get(i);
          localObject = new MessageObject(SendMessagesHelper.this.currentAccount, (TLRPC.Message)localObject, false);
          SendMessagesHelper.this.retrySendMessage((MessageObject)localObject, true);
        }
      }
    });
  }
  
  protected void putToSendingMessages(TLRPC.Message paramMessage)
  {
    this.sendingMessages.put(paramMessage.id, paramMessage);
  }
  
  protected void removeFromSendingMessages(int paramInt)
  {
    this.sendingMessages.remove(paramInt);
  }
  
  public boolean retrySendMessage(MessageObject paramMessageObject, boolean paramBoolean)
  {
    boolean bool = false;
    if (paramMessageObject.getId() >= 0) {
      paramBoolean = bool;
    }
    for (;;)
    {
      return paramBoolean;
      if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction))
      {
        int i = (int)(paramMessageObject.getDialogId() >> 32);
        TLRPC.EncryptedChat localEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i));
        if (localEncryptedChat == null)
        {
          MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(paramMessageObject.messageOwner);
          paramMessageObject.messageOwner.send_state = 2;
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramMessageObject.getId()) });
          processSentMessage(paramMessageObject.getId());
          paramBoolean = bool;
        }
        else
        {
          if (paramMessageObject.messageOwner.random_id == 0L) {
            paramMessageObject.messageOwner.random_id = getNextRandomId();
          }
          if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
            SecretChatHelper.getInstance(this.currentAccount).sendTTLMessage(localEncryptedChat, paramMessageObject.messageOwner);
          }
          for (;;)
          {
            paramBoolean = true;
            break;
            if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionDeleteMessages)) {
              SecretChatHelper.getInstance(this.currentAccount).sendMessagesDeleteMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
            } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionFlushHistory)) {
              SecretChatHelper.getInstance(this.currentAccount).sendClearHistoryMessage(localEncryptedChat, paramMessageObject.messageOwner);
            } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNotifyLayer)) {
              SecretChatHelper.getInstance(this.currentAccount).sendNotifyLayerMessage(localEncryptedChat, paramMessageObject.messageOwner);
            } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionReadMessages)) {
              SecretChatHelper.getInstance(this.currentAccount).sendMessagesReadMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
            } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) {
              SecretChatHelper.getInstance(this.currentAccount).sendScreenshotMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
            } else if ((!(paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionTyping)) && (!(paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionResend))) {
              if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionCommitKey)) {
                SecretChatHelper.getInstance(this.currentAccount).sendCommitKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
              } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAbortKey)) {
                SecretChatHelper.getInstance(this.currentAccount).sendAbortKeyMessage(localEncryptedChat, paramMessageObject.messageOwner, 0L);
              } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionRequestKey)) {
                SecretChatHelper.getInstance(this.currentAccount).sendRequestKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
              } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAcceptKey)) {
                SecretChatHelper.getInstance(this.currentAccount).sendAcceptKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
              } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNoop)) {
                SecretChatHelper.getInstance(this.currentAccount).sendNoopMessage(localEncryptedChat, paramMessageObject.messageOwner);
              }
            }
          }
        }
      }
      else
      {
        if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionScreenshotTaken)) {
          sendScreenshotMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int)paramMessageObject.getDialogId())), paramMessageObject.messageOwner.reply_to_msg_id, paramMessageObject.messageOwner);
        }
        if (paramBoolean) {
          this.unsentMessages.put(paramMessageObject.getId(), paramMessageObject);
        }
        sendMessage(paramMessageObject);
        paramBoolean = true;
      }
    }
  }
  
  public void sendCallback(final boolean paramBoolean, final MessageObject paramMessageObject, final TLRPC.KeyboardButton paramKeyboardButton, final ChatActivity paramChatActivity)
  {
    if ((paramMessageObject == null) || (paramKeyboardButton == null) || (paramChatActivity == null)) {}
    for (;;)
    {
      return;
      int i;
      if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame))
      {
        paramBoolean = false;
        i = 1;
      }
      final Object localObject;
      for (;;)
      {
        localObject = paramMessageObject.getDialogId() + "_" + paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data) + "_" + i;
        this.waitingForCallback.put(localObject, Boolean.valueOf(true));
        paramChatActivity = new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                SendMessagesHelper.this.waitingForCallback.remove(SendMessagesHelper.8.this.val$key);
                if ((SendMessagesHelper.8.this.val$cacheFinal) && (paramAnonymousTLObject == null)) {
                  SendMessagesHelper.this.sendCallback(false, SendMessagesHelper.8.this.val$messageObject, SendMessagesHelper.8.this.val$button, SendMessagesHelper.8.this.val$parentFragment);
                }
                for (;;)
                {
                  return;
                  if (paramAnonymousTLObject != null)
                  {
                    Object localObject1;
                    if ((SendMessagesHelper.8.this.val$button instanceof TLRPC.TL_keyboardButtonBuy))
                    {
                      if ((paramAnonymousTLObject instanceof TLRPC.TL_payments_paymentForm))
                      {
                        localObject1 = (TLRPC.TL_payments_paymentForm)paramAnonymousTLObject;
                        MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putUsers(((TLRPC.TL_payments_paymentForm)localObject1).users, false);
                        SendMessagesHelper.8.this.val$parentFragment.presentFragment(new PaymentFormActivity((TLRPC.TL_payments_paymentForm)localObject1, SendMessagesHelper.8.this.val$messageObject));
                      }
                      else if ((paramAnonymousTLObject instanceof TLRPC.TL_payments_paymentReceipt))
                      {
                        SendMessagesHelper.8.this.val$parentFragment.presentFragment(new PaymentFormActivity(SendMessagesHelper.8.this.val$messageObject, (TLRPC.TL_payments_paymentReceipt)paramAnonymousTLObject));
                      }
                    }
                    else
                    {
                      Object localObject2 = (TLRPC.TL_messages_botCallbackAnswer)paramAnonymousTLObject;
                      if ((!SendMessagesHelper.8.this.val$cacheFinal) && (((TLRPC.TL_messages_botCallbackAnswer)localObject2).cache_time != 0)) {
                        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).saveBotCache(SendMessagesHelper.8.this.val$key, (TLObject)localObject2);
                      }
                      int i;
                      Object localObject3;
                      if (((TLRPC.TL_messages_botCallbackAnswer)localObject2).message != null)
                      {
                        if (((TLRPC.TL_messages_botCallbackAnswer)localObject2).alert)
                        {
                          if (SendMessagesHelper.8.this.val$parentFragment.getParentActivity() != null)
                          {
                            localObject1 = new AlertDialog.Builder(SendMessagesHelper.8.this.val$parentFragment.getParentActivity());
                            ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", NUM));
                            ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", NUM), null);
                            ((AlertDialog.Builder)localObject1).setMessage(((TLRPC.TL_messages_botCallbackAnswer)localObject2).message);
                            SendMessagesHelper.8.this.val$parentFragment.showDialog(((AlertDialog.Builder)localObject1).create());
                          }
                        }
                        else
                        {
                          i = SendMessagesHelper.8.this.val$messageObject.messageOwner.from_id;
                          if (SendMessagesHelper.8.this.val$messageObject.messageOwner.via_bot_id != 0) {
                            i = SendMessagesHelper.8.this.val$messageObject.messageOwner.via_bot_id;
                          }
                          localObject1 = null;
                          if (i > 0)
                          {
                            localObject3 = MessagesController.getInstance(SendMessagesHelper.this.currentAccount).getUser(Integer.valueOf(i));
                            if (localObject3 != null) {
                              localObject1 = ContactsController.formatName(((TLRPC.User)localObject3).first_name, ((TLRPC.User)localObject3).last_name);
                            }
                          }
                          for (;;)
                          {
                            localObject3 = localObject1;
                            if (localObject1 == null) {
                              localObject3 = "bot";
                            }
                            SendMessagesHelper.8.this.val$parentFragment.showAlert((String)localObject3, ((TLRPC.TL_messages_botCallbackAnswer)localObject2).message);
                            break;
                            localObject3 = MessagesController.getInstance(SendMessagesHelper.this.currentAccount).getChat(Integer.valueOf(-i));
                            if (localObject3 != null) {
                              localObject1 = ((TLRPC.Chat)localObject3).title;
                            }
                          }
                        }
                      }
                      else if ((((TLRPC.TL_messages_botCallbackAnswer)localObject2).url != null) && (SendMessagesHelper.8.this.val$parentFragment.getParentActivity() != null))
                      {
                        i = SendMessagesHelper.8.this.val$messageObject.messageOwner.from_id;
                        if (SendMessagesHelper.8.this.val$messageObject.messageOwner.via_bot_id != 0) {
                          i = SendMessagesHelper.8.this.val$messageObject.messageOwner.via_bot_id;
                        }
                        localObject1 = MessagesController.getInstance(SendMessagesHelper.this.currentAccount).getUser(Integer.valueOf(i));
                        int j;
                        label607:
                        label656:
                        ChatActivity localChatActivity;
                        if ((localObject1 != null) && (((TLRPC.User)localObject1).verified))
                        {
                          j = 1;
                          if (!(SendMessagesHelper.8.this.val$button instanceof TLRPC.TL_keyboardButtonGame)) {
                            break label764;
                          }
                          if (!(SendMessagesHelper.8.this.val$messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {
                            break label753;
                          }
                          localObject1 = SendMessagesHelper.8.this.val$messageObject.messageOwner.media.game;
                          if (localObject1 == null) {
                            break label756;
                          }
                          localChatActivity = SendMessagesHelper.8.this.val$parentFragment;
                          localObject3 = SendMessagesHelper.8.this.val$messageObject;
                          localObject2 = ((TLRPC.TL_messages_botCallbackAnswer)localObject2).url;
                          if ((j != 0) || (!MessagesController.getNotificationsSettings(SendMessagesHelper.this.currentAccount).getBoolean("askgame_" + i, true))) {
                            break label758;
                          }
                        }
                        label753:
                        label756:
                        label758:
                        for (boolean bool = true;; bool = false)
                        {
                          localChatActivity.showOpenGameAlert((TLRPC.TL_game)localObject1, (MessageObject)localObject3, (String)localObject2, bool, i);
                          break;
                          j = 0;
                          break label607;
                          localObject1 = null;
                          break label656;
                          break;
                        }
                        label764:
                        SendMessagesHelper.8.this.val$parentFragment.showOpenUrlAlert(((TLRPC.TL_messages_botCallbackAnswer)localObject2).url, false);
                      }
                    }
                  }
                }
              }
            });
          }
        };
        if (!paramBoolean) {
          break label156;
        }
        MessagesStorage.getInstance(this.currentAccount).getBotCache((String)localObject, paramChatActivity);
        break;
        if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonBuy)) {
          i = 2;
        } else {
          i = 0;
        }
      }
      label156:
      if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonBuy))
      {
        if ((paramMessageObject.messageOwner.media.flags & 0x4) == 0)
        {
          paramKeyboardButton = new TLRPC.TL_payments_getPaymentForm();
          paramKeyboardButton.msg_id = paramMessageObject.getId();
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramKeyboardButton, paramChatActivity, 2);
        }
        else
        {
          paramKeyboardButton = new TLRPC.TL_payments_getPaymentReceipt();
          paramKeyboardButton.msg_id = paramMessageObject.messageOwner.media.receipt_msg_id;
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramKeyboardButton, paramChatActivity, 2);
        }
      }
      else
      {
        localObject = new TLRPC.TL_messages_getBotCallbackAnswer();
        ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int)paramMessageObject.getDialogId());
        ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).msg_id = paramMessageObject.getId();
        ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).game = (paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame);
        if (paramKeyboardButton.data != null)
        {
          ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).flags |= 0x1;
          ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).data = paramKeyboardButton.data;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, paramChatActivity, 2);
      }
    }
  }
  
  public void sendCurrentLocation(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    if ((paramMessageObject == null) || (paramKeyboardButton == null)) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder().append(paramMessageObject.getDialogId()).append("_").append(paramMessageObject.getId()).append("_").append(Utilities.bytesToHex(paramKeyboardButton.data)).append("_");
    if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame)) {}
    for (paramKeyboardButton = "1";; paramKeyboardButton = "0")
    {
      paramKeyboardButton = paramKeyboardButton;
      this.waitingForLocation.put(paramKeyboardButton, paramMessageObject);
      this.locationProvider.start();
      break;
    }
  }
  
  public void sendGame(TLRPC.InputPeer paramInputPeer, TLRPC.TL_inputMediaGame paramTL_inputMediaGame, final long paramLong1, long paramLong2)
  {
    if ((paramInputPeer == null) || (paramTL_inputMediaGame == null)) {
      return;
    }
    TLRPC.TL_messages_sendMedia localTL_messages_sendMedia = new TLRPC.TL_messages_sendMedia();
    localTL_messages_sendMedia.peer = paramInputPeer;
    if ((localTL_messages_sendMedia.peer instanceof TLRPC.TL_inputPeerChannel)) {
      localTL_messages_sendMedia.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + paramInputPeer.channel_id, false);
    }
    long l;
    label85:
    Object localObject;
    if (paramLong1 != 0L)
    {
      l = paramLong1;
      localTL_messages_sendMedia.random_id = l;
      localTL_messages_sendMedia.message = "";
      localTL_messages_sendMedia.media = paramTL_inputMediaGame;
      if (paramLong2 != 0L) {
        break label224;
      }
      localObject = null;
    }
    for (;;)
    {
      try
      {
        localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
        localNativeByteBuffer.<init>(paramInputPeer.getObjectSize() + paramTL_inputMediaGame.getObjectSize() + 4 + 8);
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
        paramLong1 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(paramInputPeer);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_sendMedia, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null) {
              MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
            }
            if (paramLong1 != 0L) {
              MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).removePendingTask(paramLong1);
            }
          }
        });
      }
      catch (Exception paramTL_inputMediaGame)
      {
        for (;;)
        {
          paramInputPeer = localNativeByteBuffer;
        }
      }
      l = getNextRandomId();
      break label85;
      FileLog.e(paramTL_inputMediaGame);
      continue;
      label224:
      paramLong1 = paramLong2;
    }
  }
  
  public int sendMessage(ArrayList<MessageObject> paramArrayList, final long paramLong)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      i = 0;
    }
    int j;
    int k;
    int m;
    final TLRPC.Peer localPeer;
    boolean bool1;
    int n;
    int i1;
    int i2;
    int i4;
    Object localObject1;
    final boolean bool2;
    label210:
    label240:
    LongSparseArray localLongSparseArray;
    final Object localObject2;
    final Object localObject3;
    Object localObject4;
    final Object localObject5;
    TLRPC.InputPeer localInputPeer;
    label331:
    label342:
    do
    {
      for (;;)
      {
        return i;
        j = (int)paramLong;
        k = 0;
        m = 0;
        if (j == 0) {
          break label2678;
        }
        localPeer = MessagesController.getInstance(this.currentAccount).getPeer((int)paramLong);
        bool1 = false;
        n = 0;
        m = 1;
        i1 = 1;
        i = 1;
        if (j <= 0) {
          break;
        }
        i2 = i1;
        i3 = i;
        i4 = m;
        if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(j)) != null) {
          break label240;
        }
        i = 0;
      }
      localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-j));
      i2 = i1;
      i3 = i;
      i4 = m;
      if (ChatObject.isChannel((TLRPC.Chat)localObject1))
      {
        bool2 = ((TLRPC.Chat)localObject1).megagroup;
        bool3 = ((TLRPC.Chat)localObject1).signatures;
        i2 = i1;
        i3 = i;
        i4 = m;
        bool1 = bool2;
        n = bool3;
        if (((TLRPC.Chat)localObject1).banned_rights != null)
        {
          if (((TLRPC.Chat)localObject1).banned_rights.send_stickers) {
            break;
          }
          i = 1;
          if (((TLRPC.Chat)localObject1).banned_rights.send_media) {
            break label466;
          }
          m = 1;
          if (((TLRPC.Chat)localObject1).banned_rights.embed_links) {
            break label472;
          }
          i3 = 1;
          n = bool3;
          bool1 = bool2;
          i4 = i;
          i2 = m;
        }
      }
      localLongSparseArray = new LongSparseArray();
      localObject2 = new ArrayList();
      localObject3 = new ArrayList();
      localObject1 = new ArrayList();
      localObject4 = new ArrayList();
      localObject5 = new LongSparseArray();
      localInputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
      j = UserConfig.getInstance(this.currentAccount).getClientUserId();
      if (paramLong != j) {
        break label494;
      }
      bool2 = true;
      i = 0;
      m = k;
      k = i;
      i = m;
    } while (k >= paramArrayList.size());
    MessageObject localMessageObject = (MessageObject)paramArrayList.get(k);
    Object localObject6 = localObject3;
    final Object localObject7 = localObject4;
    Object localObject8 = localObject5;
    Object localObject9 = localObject2;
    Object localObject10 = localObject1;
    int i = m;
    if (localMessageObject.getId() > 0)
    {
      if (!localMessageObject.needDrawBluredPreview()) {
        break label500;
      }
      i = m;
      localObject10 = localObject1;
      localObject9 = localObject2;
      localObject8 = localObject5;
      localObject7 = localObject4;
      localObject6 = localObject3;
    }
    label466:
    label472:
    label494:
    label500:
    label672:
    label720:
    label860:
    label956:
    long l;
    label1332:
    label1345:
    label1452:
    do
    {
      do
      {
        for (;;)
        {
          k++;
          localObject3 = localObject6;
          localObject4 = localObject7;
          localObject5 = localObject8;
          localObject2 = localObject9;
          localObject1 = localObject10;
          m = i;
          break label342;
          i = 0;
          break;
          m = 0;
          break label210;
          i3 = 0;
          i2 = m;
          i4 = i;
          bool1 = bool2;
          n = bool3;
          break label240;
          bool2 = false;
          break label331;
          if ((i4 == 0) && ((localMessageObject.isSticker()) || (localMessageObject.isGif()) || (localMessageObject.isGame())))
          {
            localObject6 = localObject3;
            localObject7 = localObject4;
            localObject8 = localObject5;
            localObject9 = localObject2;
            localObject10 = localObject1;
            i = m;
            if (m == 0)
            {
              i = 1;
              localObject6 = localObject3;
              localObject7 = localObject4;
              localObject8 = localObject5;
              localObject9 = localObject2;
              localObject10 = localObject1;
            }
          }
          else
          {
            if ((i2 != 0) || ((!(localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (!(localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)))) {
              break label672;
            }
            localObject6 = localObject3;
            localObject7 = localObject4;
            localObject8 = localObject5;
            localObject9 = localObject2;
            localObject10 = localObject1;
            i = m;
            if (m == 0)
            {
              i = 2;
              localObject6 = localObject3;
              localObject7 = localObject4;
              localObject8 = localObject5;
              localObject9 = localObject2;
              localObject10 = localObject1;
            }
          }
        }
        i1 = 0;
        localObject8 = new TLRPC.TL_message();
        if ((localMessageObject.getDialogId() != j) || (localMessageObject.messageOwner.from_id != UserConfig.getInstance(this.currentAccount).getClientUserId())) {
          break label2165;
        }
        i = 1;
        if (!localMessageObject.isForwarded()) {
          break label2171;
        }
        ((TLRPC.Message)localObject8).fwd_from = new TLRPC.TL_messageFwdHeader();
        ((TLRPC.Message)localObject8).fwd_from.flags = localMessageObject.messageOwner.fwd_from.flags;
        ((TLRPC.Message)localObject8).fwd_from.from_id = localMessageObject.messageOwner.fwd_from.from_id;
        ((TLRPC.Message)localObject8).fwd_from.date = localMessageObject.messageOwner.fwd_from.date;
        ((TLRPC.Message)localObject8).fwd_from.channel_id = localMessageObject.messageOwner.fwd_from.channel_id;
        ((TLRPC.Message)localObject8).fwd_from.channel_post = localMessageObject.messageOwner.fwd_from.channel_post;
        ((TLRPC.Message)localObject8).fwd_from.post_author = localMessageObject.messageOwner.fwd_from.post_author;
        ((TLRPC.Message)localObject8).flags = 4;
        if ((paramLong == j) && (((TLRPC.Message)localObject8).fwd_from != null))
        {
          localObject6 = ((TLRPC.Message)localObject8).fwd_from;
          ((TLRPC.MessageFwdHeader)localObject6).flags |= 0x10;
          ((TLRPC.Message)localObject8).fwd_from.saved_from_msg_id = localMessageObject.getId();
          ((TLRPC.Message)localObject8).fwd_from.saved_from_peer = localMessageObject.messageOwner.to_id;
        }
        if ((i3 != 0) || (!(localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))) {
          break label2532;
        }
        ((TLRPC.Message)localObject8).media = new TLRPC.TL_messageMediaEmpty();
        if (((TLRPC.Message)localObject8).media != null) {
          ((TLRPC.Message)localObject8).flags |= 0x200;
        }
        if (bool1) {
          ((TLRPC.Message)localObject8).flags |= 0x80000000;
        }
        if (localMessageObject.messageOwner.via_bot_id != 0)
        {
          ((TLRPC.Message)localObject8).via_bot_id = localMessageObject.messageOwner.via_bot_id;
          ((TLRPC.Message)localObject8).flags |= 0x800;
        }
        ((TLRPC.Message)localObject8).message = localMessageObject.messageOwner.message;
        ((TLRPC.Message)localObject8).fwd_msg_id = localMessageObject.getId();
        ((TLRPC.Message)localObject8).attachPath = localMessageObject.messageOwner.attachPath;
        ((TLRPC.Message)localObject8).entities = localMessageObject.messageOwner.entities;
        if (!((TLRPC.Message)localObject8).entities.isEmpty()) {
          ((TLRPC.Message)localObject8).flags |= 0x80;
        }
        if (((TLRPC.Message)localObject8).attachPath == null) {
          ((TLRPC.Message)localObject8).attachPath = "";
        }
        i = UserConfig.getInstance(this.currentAccount).getNewMessageId();
        ((TLRPC.Message)localObject8).id = i;
        ((TLRPC.Message)localObject8).local_id = i;
        ((TLRPC.Message)localObject8).out = true;
        l = localMessageObject.messageOwner.grouped_id;
        if (l != 0L)
        {
          localObject7 = (Long)localLongSparseArray.get(localMessageObject.messageOwner.grouped_id);
          localObject6 = localObject7;
          if (localObject7 == null)
          {
            localObject6 = Long.valueOf(Utilities.random.nextLong());
            localLongSparseArray.put(localMessageObject.messageOwner.grouped_id, localObject6);
          }
          ((TLRPC.Message)localObject8).grouped_id = ((Long)localObject6).longValue();
          ((TLRPC.Message)localObject8).flags |= 0x20000;
        }
        i = i1;
        if (k != paramArrayList.size() - 1)
        {
          i = i1;
          if (((MessageObject)paramArrayList.get(k + 1)).messageOwner.grouped_id != localMessageObject.messageOwner.grouped_id) {
            i = 1;
          }
        }
        if ((localPeer.channel_id == 0) || (bool1)) {
          break label2559;
        }
        if (n == 0) {
          break label2548;
        }
        i1 = UserConfig.getInstance(this.currentAccount).getClientUserId();
        ((TLRPC.Message)localObject8).from_id = i1;
        ((TLRPC.Message)localObject8).post = true;
        if (((TLRPC.Message)localObject8).random_id == 0L) {
          ((TLRPC.Message)localObject8).random_id = getNextRandomId();
        }
        ((ArrayList)localObject1).add(Long.valueOf(((TLRPC.Message)localObject8).random_id));
        ((LongSparseArray)localObject5).put(((TLRPC.Message)localObject8).random_id, localObject8);
        ((ArrayList)localObject4).add(Integer.valueOf(((TLRPC.Message)localObject8).fwd_msg_id));
        ((TLRPC.Message)localObject8).date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (!(localInputPeer instanceof TLRPC.TL_inputPeerChannel)) {
          break label2600;
        }
        if (bool1) {
          break label2591;
        }
        ((TLRPC.Message)localObject8).views = 1;
        ((TLRPC.Message)localObject8).flags |= 0x400;
        ((TLRPC.Message)localObject8).dialog_id = paramLong;
        ((TLRPC.Message)localObject8).to_id = localPeer;
        if ((MessageObject.isVoiceMessage((TLRPC.Message)localObject8)) || (MessageObject.isRoundVideoMessage((TLRPC.Message)localObject8))) {
          ((TLRPC.Message)localObject8).media_unread = true;
        }
        if ((localMessageObject.messageOwner.to_id instanceof TLRPC.TL_peerChannel)) {
          ((TLRPC.Message)localObject8).ttl = (-localMessageObject.messageOwner.to_id.channel_id);
        }
        localObject6 = new MessageObject(this.currentAccount, (TLRPC.Message)localObject8, true);
        ((MessageObject)localObject6).messageOwner.send_state = 1;
        ((ArrayList)localObject2).add(localObject6);
        ((ArrayList)localObject3).add(localObject8);
        putToSendingMessages((TLRPC.Message)localObject8);
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("forward message user_id = " + localInputPeer.user_id + " chat_id = " + localInputPeer.chat_id + " channel_id = " + localInputPeer.channel_id + " access_hash = " + localInputPeer.access_hash);
        }
        if (((i != 0) && (((ArrayList)localObject3).size() > 0)) || (((ArrayList)localObject3).size() == 100) || (k == paramArrayList.size() - 1)) {
          break label1756;
        }
        localObject6 = localObject3;
        localObject7 = localObject4;
        localObject8 = localObject5;
        localObject9 = localObject2;
        localObject10 = localObject1;
        i = m;
      } while (k == paramArrayList.size() - 1);
      localObject6 = localObject3;
      localObject7 = localObject4;
      localObject8 = localObject5;
      localObject9 = localObject2;
      localObject10 = localObject1;
      i = m;
    } while (((MessageObject)paramArrayList.get(k + 1)).getDialogId() == localMessageObject.getDialogId());
    label1756:
    MessagesStorage.getInstance(this.currentAccount).putMessages(new ArrayList((Collection)localObject3), false, true, false, 0);
    MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(paramLong, (ArrayList)localObject2);
    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    UserConfig.getInstance(this.currentAccount).saveConfig(false);
    localObject7 = new TLRPC.TL_messages_forwardMessages();
    ((TLRPC.TL_messages_forwardMessages)localObject7).to_peer = localInputPeer;
    if (l != 0L)
    {
      bool3 = true;
      label1846:
      ((TLRPC.TL_messages_forwardMessages)localObject7).grouped = bool3;
      if ((((TLRPC.TL_messages_forwardMessages)localObject7).to_peer instanceof TLRPC.TL_inputPeerChannel)) {
        ((TLRPC.TL_messages_forwardMessages)localObject7).silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + paramLong, false);
      }
      if (!(localMessageObject.messageOwner.to_id instanceof TLRPC.TL_peerChannel)) {
        break label2657;
      }
      localObject6 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(localMessageObject.messageOwner.to_id.channel_id));
      ((TLRPC.TL_messages_forwardMessages)localObject7).from_peer = new TLRPC.TL_inputPeerChannel();
      ((TLRPC.TL_messages_forwardMessages)localObject7).from_peer.channel_id = localMessageObject.messageOwner.to_id.channel_id;
      if (localObject6 != null) {
        ((TLRPC.TL_messages_forwardMessages)localObject7).from_peer.access_hash = ((TLRPC.Chat)localObject6).access_hash;
      }
      label1991:
      ((TLRPC.TL_messages_forwardMessages)localObject7).random_id = ((ArrayList)localObject1);
      ((TLRPC.TL_messages_forwardMessages)localObject7).id = ((ArrayList)localObject4);
      if ((paramArrayList.size() != 1) || (!((MessageObject)paramArrayList.get(0)).messageOwner.with_my_score)) {
        break label2672;
      }
    }
    label2165:
    label2171:
    label2263:
    label2430:
    label2532:
    label2548:
    label2559:
    label2591:
    label2600:
    label2657:
    label2672:
    for (boolean bool3 = true;; bool3 = false)
    {
      ((TLRPC.TL_messages_forwardMessages)localObject7).with_my_score = bool3;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject7, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          int i;
          if (paramAnonymousTL_error == null)
          {
            SparseLongArray localSparseLongArray = new SparseLongArray();
            TLRPC.Updates localUpdates = (TLRPC.Updates)paramAnonymousTLObject;
            for (i = 0; i < localUpdates.updates.size(); i = j + 1)
            {
              paramAnonymousTLObject = (TLRPC.Update)localUpdates.updates.get(i);
              j = i;
              if ((paramAnonymousTLObject instanceof TLRPC.TL_updateMessageID))
              {
                paramAnonymousTLObject = (TLRPC.TL_updateMessageID)paramAnonymousTLObject;
                localSparseLongArray.put(paramAnonymousTLObject.id, paramAnonymousTLObject.random_id);
                localUpdates.updates.remove(i);
                j = i - 1;
              }
            }
            paramAnonymousTL_error = (Integer)MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(paramLong));
            paramAnonymousTLObject = paramAnonymousTL_error;
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = Integer.valueOf(MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getDialogReadMax(true, paramLong));
              MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(paramLong), paramAnonymousTLObject);
            }
            int j = 0;
            i = 0;
            if (i < localUpdates.updates.size())
            {
              paramAnonymousTL_error = (TLRPC.Update)localUpdates.updates.get(i);
              final int k;
              int m;
              final Object localObject;
              label295:
              boolean bool;
              if (!(paramAnonymousTL_error instanceof TLRPC.TL_updateNewMessage))
              {
                k = i;
                m = j;
                if (!(paramAnonymousTL_error instanceof TLRPC.TL_updateNewChannelMessage)) {}
              }
              else
              {
                localUpdates.updates.remove(i);
                i--;
                if (!(paramAnonymousTL_error instanceof TLRPC.TL_updateNewMessage)) {
                  break label406;
                }
                localObject = (TLRPC.TL_updateNewMessage)paramAnonymousTL_error;
                paramAnonymousTL_error = ((TLRPC.TL_updateNewMessage)localObject).message;
                MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewDifferenceParams(-1, ((TLRPC.TL_updateNewMessage)localObject).pts, -1, ((TLRPC.TL_updateNewMessage)localObject).pts_count);
                ImageLoader.saveMessageThumbs(paramAnonymousTL_error);
                if (paramAnonymousTLObject.intValue() >= paramAnonymousTL_error.id) {
                  break label475;
                }
                bool = true;
                label313:
                paramAnonymousTL_error.unread = bool;
                if (localObject5)
                {
                  paramAnonymousTL_error.out = true;
                  paramAnonymousTL_error.unread = false;
                  paramAnonymousTL_error.media_unread = false;
                }
                long l = localSparseLongArray.get(paramAnonymousTL_error.id);
                k = i;
                m = j;
                if (l != 0L)
                {
                  localObject = (TLRPC.Message)localObject3.get(l);
                  if (localObject != null) {
                    break label481;
                  }
                  m = j;
                  k = i;
                }
              }
              for (;;)
              {
                i = k + 1;
                j = m;
                break;
                label406:
                paramAnonymousTL_error = (TLRPC.TL_updateNewChannelMessage)paramAnonymousTL_error;
                localObject = paramAnonymousTL_error.message;
                MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewChannelDifferenceParams(paramAnonymousTL_error.pts, paramAnonymousTL_error.pts_count, ((TLRPC.Message)localObject).to_id.channel_id);
                paramAnonymousTL_error = (TLRPC.TL_error)localObject;
                if (!bool2) {
                  break label295;
                }
                ((TLRPC.Message)localObject).flags |= 0x80000000;
                paramAnonymousTL_error = (TLRPC.TL_error)localObject;
                break label295;
                label475:
                bool = false;
                break label313;
                label481:
                int n = localObject2.indexOf(localObject);
                k = i;
                m = j;
                if (n != -1)
                {
                  MessageObject localMessageObject = (MessageObject)localPeer.get(n);
                  localObject2.remove(n);
                  localPeer.remove(n);
                  k = ((TLRPC.Message)localObject).id;
                  final ArrayList localArrayList = new ArrayList();
                  localArrayList.add(paramAnonymousTL_error);
                  ((TLRPC.Message)localObject).id = paramAnonymousTL_error.id;
                  m = j + 1;
                  SendMessagesHelper.this.updateMediaPaths(localMessageObject, paramAnonymousTL_error, null, true);
                  MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).updateMessageStateAndId(localObject.random_id, Integer.valueOf(k), localObject.id, 0, false, SendMessagesHelper.5.this.val$to_id.channel_id);
                      MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(localArrayList, true, false, false, 0);
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          SendMessagesHelper.5.1.this.val$newMsgObj.send_state = 0;
                          DataQuery.getInstance(SendMessagesHelper.this.currentAccount).increasePeerRaiting(SendMessagesHelper.5.this.val$peer);
                          NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { Integer.valueOf(SendMessagesHelper.5.1.this.val$oldId), Integer.valueOf(SendMessagesHelper.5.1.this.val$message.id), SendMessagesHelper.5.1.this.val$message, Long.valueOf(SendMessagesHelper.5.this.val$peer) });
                          SendMessagesHelper.this.processSentMessage(SendMessagesHelper.5.1.this.val$oldId);
                          SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.5.1.this.val$oldId);
                        }
                      });
                    }
                  });
                  k = i;
                }
              }
            }
            if (!localUpdates.updates.isEmpty()) {
              MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates(localUpdates, false);
            }
            StatsController.getInstance(SendMessagesHelper.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, j);
          }
          for (;;)
          {
            for (i = 0; i < localObject2.size(); i++)
            {
              paramAnonymousTLObject = (TLRPC.Message)localObject2.get(i);
              MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(paramAnonymousTLObject);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  paramAnonymousTLObject.send_state = 2;
                  NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramAnonymousTLObject.id) });
                  SendMessagesHelper.this.processSentMessage(paramAnonymousTLObject.id);
                  SendMessagesHelper.this.removeFromSendingMessages(paramAnonymousTLObject.id);
                }
              });
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                AlertsCreator.processError(SendMessagesHelper.this.currentAccount, paramAnonymousTL_error, null, SendMessagesHelper.5.this.val$req, new Object[0]);
              }
            });
          }
        }
      }, 68);
      localObject6 = localObject3;
      localObject7 = localObject4;
      localObject8 = localObject5;
      localObject9 = localObject2;
      localObject10 = localObject1;
      i = m;
      if (k == paramArrayList.size() - 1) {
        break;
      }
      localObject9 = new ArrayList();
      localObject6 = new ArrayList();
      localObject10 = new ArrayList();
      localObject7 = new ArrayList();
      localObject8 = new LongSparseArray();
      i = m;
      break;
      i = 0;
      break label720;
      if (i != 0) {
        break label860;
      }
      ((TLRPC.Message)localObject8).fwd_from = new TLRPC.TL_messageFwdHeader();
      ((TLRPC.Message)localObject8).fwd_from.channel_post = localMessageObject.getId();
      localObject6 = ((TLRPC.Message)localObject8).fwd_from;
      ((TLRPC.MessageFwdHeader)localObject6).flags |= 0x4;
      if (localMessageObject.isFromUser())
      {
        ((TLRPC.Message)localObject8).fwd_from.from_id = localMessageObject.messageOwner.from_id;
        localObject6 = ((TLRPC.Message)localObject8).fwd_from;
        ((TLRPC.MessageFwdHeader)localObject6).flags |= 0x1;
        if (localMessageObject.messageOwner.post_author == null) {
          break label2430;
        }
        ((TLRPC.Message)localObject8).fwd_from.post_author = localMessageObject.messageOwner.post_author;
        localObject6 = ((TLRPC.Message)localObject8).fwd_from;
        ((TLRPC.MessageFwdHeader)localObject6).flags |= 0x8;
      }
      for (;;)
      {
        ((TLRPC.Message)localObject8).date = localMessageObject.messageOwner.date;
        ((TLRPC.Message)localObject8).flags = 4;
        break;
        ((TLRPC.Message)localObject8).fwd_from.channel_id = localMessageObject.messageOwner.to_id.channel_id;
        localObject6 = ((TLRPC.Message)localObject8).fwd_from;
        ((TLRPC.MessageFwdHeader)localObject6).flags |= 0x2;
        if ((!localMessageObject.messageOwner.post) || (localMessageObject.messageOwner.from_id <= 0)) {
          break label2263;
        }
        ((TLRPC.Message)localObject8).fwd_from.from_id = localMessageObject.messageOwner.from_id;
        localObject6 = ((TLRPC.Message)localObject8).fwd_from;
        ((TLRPC.MessageFwdHeader)localObject6).flags |= 0x1;
        break label2263;
        if ((!localMessageObject.isOutOwner()) && (localMessageObject.messageOwner.from_id > 0) && (localMessageObject.messageOwner.post))
        {
          localObject6 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(localMessageObject.messageOwner.from_id));
          if (localObject6 != null)
          {
            ((TLRPC.Message)localObject8).fwd_from.post_author = ContactsController.formatName(((TLRPC.User)localObject6).first_name, ((TLRPC.User)localObject6).last_name);
            localObject6 = ((TLRPC.Message)localObject8).fwd_from;
            ((TLRPC.MessageFwdHeader)localObject6).flags |= 0x8;
          }
        }
      }
      ((TLRPC.Message)localObject8).media = localMessageObject.messageOwner.media;
      break label956;
      i1 = -localPeer.channel_id;
      break label1332;
      ((TLRPC.Message)localObject8).from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
      ((TLRPC.Message)localObject8).flags |= 0x100;
      break label1345;
      ((TLRPC.Message)localObject8).unread = true;
      break label1452;
      if ((localMessageObject.messageOwner.flags & 0x400) != 0)
      {
        ((TLRPC.Message)localObject8).views = localMessageObject.messageOwner.views;
        ((TLRPC.Message)localObject8).flags |= 0x400;
      }
      ((TLRPC.Message)localObject8).unread = true;
      break label1452;
      bool3 = false;
      break label1846;
      ((TLRPC.TL_messages_forwardMessages)localObject7).from_peer = new TLRPC.TL_inputPeerEmpty();
      break label1991;
    }
    label2678:
    for (int i3 = 0;; i3++)
    {
      i = m;
      if (i3 >= paramArrayList.size()) {
        break;
      }
      processForwardFromMyName((MessageObject)paramArrayList.get(i3), paramLong);
    }
  }
  
  public void sendMessage(String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.WebPage paramWebPage, boolean paramBoolean, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(paramString, null, null, null, null, null, null, null, paramLong, null, paramMessageObject, paramWebPage, paramBoolean, null, paramArrayList, paramReplyMarkup, paramHashMap, 0);
  }
  
  public void sendMessage(MessageObject paramMessageObject)
  {
    sendMessage(null, null, null, null, null, null, null, null, paramMessageObject.getDialogId(), paramMessageObject.messageOwner.attachPath, null, null, true, paramMessageObject, null, paramMessageObject.messageOwner.reply_markup, paramMessageObject.messageOwner.params, 0);
  }
  
  public void sendMessage(TLRPC.MessageMedia paramMessageMedia, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, paramMessageMedia, null, null, null, null, null, paramLong, null, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap, 0);
  }
  
  public void sendMessage(TLRPC.TL_document paramTL_document, VideoEditedInfo paramVideoEditedInfo, String paramString1, long paramLong, MessageObject paramMessageObject, String paramString2, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap, int paramInt)
  {
    sendMessage(null, paramString2, null, null, paramVideoEditedInfo, null, paramTL_document, null, paramLong, paramString1, paramMessageObject, null, true, null, paramArrayList, paramReplyMarkup, paramHashMap, paramInt);
  }
  
  public void sendMessage(TLRPC.TL_game paramTL_game, long paramLong, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, null, null, null, null, paramTL_game, paramLong, null, null, null, true, null, null, paramReplyMarkup, paramHashMap, 0);
  }
  
  public void sendMessage(TLRPC.TL_photo paramTL_photo, String paramString1, long paramLong, MessageObject paramMessageObject, String paramString2, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap, int paramInt)
  {
    sendMessage(null, paramString2, null, paramTL_photo, null, null, null, null, paramLong, paramString1, paramMessageObject, null, true, null, paramArrayList, paramReplyMarkup, paramHashMap, paramInt);
  }
  
  public void sendMessage(TLRPC.User paramUser, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, null, null, paramUser, null, null, paramLong, null, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap, 0);
  }
  
  public void sendNotificationCallback(final long paramLong, int paramInt, final byte[] paramArrayOfByte)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        int i = (int)paramLong;
        final String str = paramLong + "_" + paramArrayOfByte + "_" + Utilities.bytesToHex(this.val$data) + "_" + 0;
        SendMessagesHelper.this.waitingForCallback.put(str, Boolean.valueOf(true));
        Object localObject;
        if (i > 0) {
          if (MessagesController.getInstance(SendMessagesHelper.this.currentAccount).getUser(Integer.valueOf(i)) == null)
          {
            localObject = MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getUserSync(i);
            if (localObject != null) {
              MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putUser((TLRPC.User)localObject, true);
            }
          }
        }
        for (;;)
        {
          localObject = new TLRPC.TL_messages_getBotCallbackAnswer();
          ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).peer = MessagesController.getInstance(SendMessagesHelper.this.currentAccount).getInputPeer(i);
          ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).msg_id = paramArrayOfByte;
          ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).game = false;
          if (this.val$data != null)
          {
            ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).flags |= 0x1;
            ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).data = this.val$data;
          }
          ConnectionsManager.getInstance(SendMessagesHelper.this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  SendMessagesHelper.this.waitingForCallback.remove(SendMessagesHelper.7.1.this.val$key);
                }
              });
            }
          }, 2);
          MessagesController.getInstance(SendMessagesHelper.this.currentAccount).markDialogAsRead(paramLong, paramArrayOfByte, paramArrayOfByte, 0, false, 0, true);
          return;
          if (MessagesController.getInstance(SendMessagesHelper.this.currentAccount).getChat(Integer.valueOf(-i)) == null)
          {
            localObject = MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getChatSync(-i);
            if (localObject != null) {
              MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putChat((TLRPC.Chat)localObject, true);
            }
          }
        }
      }
    });
  }
  
  public void sendScreenshotMessage(TLRPC.User paramUser, int paramInt, TLRPC.Message paramMessage)
  {
    if ((paramUser == null) || (paramInt == 0) || (paramUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId())) {
      return;
    }
    TLRPC.TL_messages_sendScreenshotNotification localTL_messages_sendScreenshotNotification = new TLRPC.TL_messages_sendScreenshotNotification();
    localTL_messages_sendScreenshotNotification.peer = new TLRPC.TL_inputPeerUser();
    localTL_messages_sendScreenshotNotification.peer.access_hash = paramUser.access_hash;
    localTL_messages_sendScreenshotNotification.peer.user_id = paramUser.id;
    if (paramMessage != null)
    {
      paramUser = paramMessage;
      localTL_messages_sendScreenshotNotification.reply_to_msg_id = paramInt;
      localTL_messages_sendScreenshotNotification.random_id = paramMessage.random_id;
    }
    for (;;)
    {
      localTL_messages_sendScreenshotNotification.random_id = paramUser.random_id;
      paramMessage = new MessageObject(this.currentAccount, paramUser, false);
      paramMessage.messageOwner.send_state = 1;
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(paramMessage);
      MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(paramUser.dialog_id, localArrayList);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      localArrayList = new ArrayList();
      localArrayList.add(paramUser);
      MessagesStorage.getInstance(this.currentAccount).putMessages(localArrayList, false, true, false, 0);
      performSendMessageRequest(localTL_messages_sendScreenshotNotification, paramMessage, null);
      break;
      paramMessage = new TLRPC.TL_messageService();
      paramMessage.random_id = getNextRandomId();
      paramMessage.dialog_id = paramUser.id;
      paramMessage.unread = true;
      paramMessage.out = true;
      int i = UserConfig.getInstance(this.currentAccount).getNewMessageId();
      paramMessage.id = i;
      paramMessage.local_id = i;
      paramMessage.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
      paramMessage.flags |= 0x100;
      paramMessage.flags |= 0x8;
      paramMessage.reply_to_msg_id = paramInt;
      paramMessage.to_id = new TLRPC.TL_peerUser();
      paramMessage.to_id.user_id = paramUser.id;
      paramMessage.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      paramMessage.action = new TLRPC.TL_messageActionScreenshotTaken();
      UserConfig.getInstance(this.currentAccount).saveConfig(false);
      paramUser = paramMessage;
    }
  }
  
  public void sendSticker(TLRPC.Document paramDocument, long paramLong, MessageObject paramMessageObject)
  {
    if (paramDocument == null) {}
    for (;;)
    {
      return;
      Object localObject1 = paramDocument;
      int i;
      Object localObject2;
      if ((int)paramLong == 0)
      {
        i = (int)(paramLong >> 32);
        if (MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i)) == null) {
          continue;
        }
        localObject1 = new TLRPC.TL_document();
        ((TLRPC.TL_document)localObject1).id = paramDocument.id;
        ((TLRPC.TL_document)localObject1).access_hash = paramDocument.access_hash;
        ((TLRPC.TL_document)localObject1).date = paramDocument.date;
        ((TLRPC.TL_document)localObject1).mime_type = paramDocument.mime_type;
        ((TLRPC.TL_document)localObject1).size = paramDocument.size;
        ((TLRPC.TL_document)localObject1).dc_id = paramDocument.dc_id;
        ((TLRPC.TL_document)localObject1).attributes = new ArrayList(paramDocument.attributes);
        if (((TLRPC.TL_document)localObject1).mime_type == null) {
          ((TLRPC.TL_document)localObject1).mime_type = "";
        }
        if ((paramDocument.thumb instanceof TLRPC.TL_photoSize))
        {
          localObject2 = FileLoader.getPathToAttach(paramDocument.thumb, true);
          if (!((File)localObject2).exists()) {}
        }
      }
      try
      {
        i = (int)((File)localObject2).length();
        byte[] arrayOfByte = new byte[(int)((File)localObject2).length()];
        RandomAccessFile localRandomAccessFile = new java/io/RandomAccessFile;
        localRandomAccessFile.<init>((File)localObject2, "r");
        localRandomAccessFile.readFully(arrayOfByte);
        localObject2 = new org/telegram/tgnet/TLRPC$TL_photoCachedSize;
        ((TLRPC.TL_photoCachedSize)localObject2).<init>();
        ((TLRPC.TL_document)localObject1).thumb = ((TLRPC.PhotoSize)localObject2);
        ((TLRPC.TL_document)localObject1).thumb.location = paramDocument.thumb.location;
        ((TLRPC.TL_document)localObject1).thumb.size = paramDocument.thumb.size;
        ((TLRPC.TL_document)localObject1).thumb.w = paramDocument.thumb.w;
        ((TLRPC.TL_document)localObject1).thumb.h = paramDocument.thumb.h;
        ((TLRPC.TL_document)localObject1).thumb.type = paramDocument.thumb.type;
        ((TLRPC.TL_document)localObject1).thumb.bytes = arrayOfByte;
        if (((TLRPC.TL_document)localObject1).thumb == null)
        {
          ((TLRPC.TL_document)localObject1).thumb = new TLRPC.TL_photoSizeEmpty();
          ((TLRPC.TL_document)localObject1).thumb.type = "s";
        }
        if (!(localObject1 instanceof TLRPC.TL_document)) {
          continue;
        }
        sendMessage((TLRPC.TL_document)localObject1, null, null, paramLong, paramMessageObject, null, null, null, null, 0);
      }
      catch (Exception paramDocument)
      {
        for (;;)
        {
          FileLog.e(paramDocument);
        }
      }
    }
  }
  
  public void setCurrentChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.currentChatInfo = paramChatFull;
  }
  
  protected void stopVideoService(final String paramString)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, new Object[] { SendMessagesHelper.11.this.val$path, Integer.valueOf(SendMessagesHelper.this.currentAccount) });
          }
        });
      }
    });
  }
  
  protected class DelayedMessage
  {
    public TLRPC.EncryptedChat encryptedChat;
    public HashMap<Object, Object> extraHashMap;
    public int finalGroupMessage;
    public long groupId;
    public String httpLocation;
    public TLRPC.FileLocation location;
    public ArrayList<MessageObject> messageObjects;
    public ArrayList<TLRPC.Message> messages;
    public MessageObject obj;
    public String originalPath;
    public ArrayList<String> originalPaths;
    public long peer;
    ArrayList<SendMessagesHelper.DelayedMessageSendAfterRequest> requests;
    public TLObject sendEncryptedRequest;
    public TLObject sendRequest;
    public int type;
    public boolean upload;
    public VideoEditedInfo videoEditedInfo;
    
    public DelayedMessage(long paramLong)
    {
      this.peer = paramLong;
    }
    
    public void addDelayedRequest(TLObject paramTLObject, ArrayList<MessageObject> paramArrayList, ArrayList<String> paramArrayList1)
    {
      SendMessagesHelper.DelayedMessageSendAfterRequest localDelayedMessageSendAfterRequest = new SendMessagesHelper.DelayedMessageSendAfterRequest(SendMessagesHelper.this);
      localDelayedMessageSendAfterRequest.request = paramTLObject;
      localDelayedMessageSendAfterRequest.msgObjs = paramArrayList;
      localDelayedMessageSendAfterRequest.originalPaths = paramArrayList1;
      if (this.requests == null) {
        this.requests = new ArrayList();
      }
      this.requests.add(localDelayedMessageSendAfterRequest);
    }
    
    public void addDelayedRequest(TLObject paramTLObject, MessageObject paramMessageObject, String paramString)
    {
      SendMessagesHelper.DelayedMessageSendAfterRequest localDelayedMessageSendAfterRequest = new SendMessagesHelper.DelayedMessageSendAfterRequest(SendMessagesHelper.this);
      localDelayedMessageSendAfterRequest.request = paramTLObject;
      localDelayedMessageSendAfterRequest.msgObj = paramMessageObject;
      localDelayedMessageSendAfterRequest.originalPath = paramString;
      if (this.requests == null) {
        this.requests = new ArrayList();
      }
      this.requests.add(localDelayedMessageSendAfterRequest);
    }
    
    public void markAsError()
    {
      if (this.type == 4)
      {
        for (int i = 0; i < this.messageObjects.size(); i++)
        {
          MessageObject localMessageObject = (MessageObject)this.messageObjects.get(i);
          MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(localMessageObject.messageOwner);
          localMessageObject.messageOwner.send_state = 2;
          NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(localMessageObject.getId()) });
          SendMessagesHelper.this.processSentMessage(localMessageObject.getId());
        }
        SendMessagesHelper.this.delayedMessages.remove("group_" + this.groupId);
      }
      for (;;)
      {
        sendDelayedRequests();
        return;
        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(this.obj.messageOwner);
        this.obj.messageOwner.send_state = 2;
        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(this.obj.getId()) });
        SendMessagesHelper.this.processSentMessage(this.obj.getId());
      }
    }
    
    public void sendDelayedRequests()
    {
      if ((this.requests == null) || ((this.type != 4) && (this.type != 0))) {}
      for (;;)
      {
        return;
        int i = this.requests.size();
        int j = 0;
        if (j < i)
        {
          SendMessagesHelper.DelayedMessageSendAfterRequest localDelayedMessageSendAfterRequest = (SendMessagesHelper.DelayedMessageSendAfterRequest)this.requests.get(j);
          if ((localDelayedMessageSendAfterRequest.request instanceof TLRPC.TL_messages_sendEncryptedMultiMedia)) {
            SecretChatHelper.getInstance(SendMessagesHelper.this.currentAccount).performSendEncryptedRequest((TLRPC.TL_messages_sendEncryptedMultiMedia)localDelayedMessageSendAfterRequest.request, this);
          }
          for (;;)
          {
            j++;
            break;
            if ((localDelayedMessageSendAfterRequest.request instanceof TLRPC.TL_messages_sendMultiMedia)) {
              SendMessagesHelper.this.performSendMessageRequestMulti((TLRPC.TL_messages_sendMultiMedia)localDelayedMessageSendAfterRequest.request, localDelayedMessageSendAfterRequest.msgObjs, localDelayedMessageSendAfterRequest.originalPaths);
            } else {
              SendMessagesHelper.this.performSendMessageRequest(localDelayedMessageSendAfterRequest.request, localDelayedMessageSendAfterRequest.msgObj, localDelayedMessageSendAfterRequest.originalPath);
            }
          }
        }
        this.requests = null;
      }
    }
  }
  
  protected class DelayedMessageSendAfterRequest
  {
    public MessageObject msgObj;
    public ArrayList<MessageObject> msgObjs;
    public String originalPath;
    public ArrayList<String> originalPaths;
    public TLObject request;
    
    protected DelayedMessageSendAfterRequest() {}
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
                      break label61;
                    }
                    SendMessagesHelper.LocationProvider.this.delegate.onLocationAcquired(SendMessagesHelper.LocationProvider.this.lastKnownLocation);
                  }
                  for (;;)
                  {
                    SendMessagesHelper.LocationProvider.this.cleanup();
                    break;
                    label61:
                    SendMessagesHelper.LocationProvider.this.delegate.onUnableLocationAcquire();
                  }
                }
              };
              AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, 5000L);
              return;
              localException1 = localException1;
              FileLog.e(localException1);
              continue;
              localException2 = localException2;
              FileLog.e(localException2);
            }
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              FileLog.e(localException3);
            }
          }
        }
      }
    }
    
    public void stop()
    {
      if (this.locationManager == null) {}
      for (;;)
      {
        return;
        if (this.locationQueryCancelRunnable != null) {
          AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
        }
        cleanup();
      }
    }
    
    private class GpsLocationListener
      implements LocationListener
    {
      private GpsLocationListener() {}
      
      public void onLocationChanged(Location paramLocation)
      {
        if ((paramLocation == null) || (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable == null)) {}
        for (;;)
        {
          return;
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("found location " + paramLocation);
          }
          SendMessagesHelper.LocationProvider.access$502(SendMessagesHelper.LocationProvider.this, paramLocation);
          if (paramLocation.getAccuracy() < 100.0F)
          {
            if (SendMessagesHelper.LocationProvider.this.delegate != null) {
              SendMessagesHelper.LocationProvider.this.delegate.onLocationAcquired(paramLocation);
            }
            if (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable != null) {
              AndroidUtilities.cancelRunOnUIThread(SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable);
            }
            SendMessagesHelper.LocationProvider.this.cleanup();
          }
        }
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
  
  private static class MediaSendPrepareWorker
  {
    public volatile TLRPC.TL_photo photo;
    public CountDownLatch sync;
  }
  
  public static class SendingMediaInfo
  {
    public String caption;
    public ArrayList<TLRPC.MessageEntity> entities;
    public boolean isVideo;
    public ArrayList<TLRPC.InputDocument> masks;
    public String path;
    public MediaController.SearchImage searchImage;
    public int ttl;
    public Uri uri;
    public VideoEditedInfo videoEditedInfo;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/SendMessagesHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */