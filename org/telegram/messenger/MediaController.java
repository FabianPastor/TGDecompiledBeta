package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import android.telephony.PhoneStateListener;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.PhotoFilterView.CurvesToolValue;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.telegram.ui.PhotoViewer;

public class MediaController
  implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, NotificationCenter.NotificationCenterDelegate
{
  private static final int AUDIO_FOCUSED = 2;
  private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
  private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
  private static volatile MediaController Instance;
  public static final String MIME_TYPE = "video/avc";
  private static final int PROCESSOR_TYPE_INTEL = 2;
  private static final int PROCESSOR_TYPE_MTK = 3;
  private static final int PROCESSOR_TYPE_OTHER = 0;
  private static final int PROCESSOR_TYPE_QCOM = 1;
  private static final int PROCESSOR_TYPE_SEC = 4;
  private static final int PROCESSOR_TYPE_TI = 5;
  private static final float VOLUME_DUCK = 0.2F;
  private static final float VOLUME_NORMAL = 1.0F;
  public static AlbumEntry allMediaAlbumEntry;
  public static AlbumEntry allPhotosAlbumEntry;
  private static Runnable broadcastPhotosRunnable;
  private static final String[] projectionPhotos = { "_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "orientation" };
  private static final String[] projectionVideo = { "_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "duration" };
  public static int[] readArgs = new int[3];
  private static Runnable refreshGalleryRunnable;
  private Sensor accelerometerSensor;
  private boolean accelerometerVertical;
  private boolean allowStartRecord;
  private int audioFocus = 0;
  private AudioInfo audioInfo;
  private VideoPlayer audioPlayer = null;
  private AudioRecord audioRecorder;
  private AudioTrack audioTrackPlayer = null;
  private Activity baseActivity;
  private int buffersWrited;
  private boolean callInProgress;
  private boolean cancelCurrentVideoConversion = false;
  private int countLess;
  private AspectRatioFrameLayout currentAspectRatioFrameLayout;
  private float currentAspectRatioFrameLayoutRatio;
  private boolean currentAspectRatioFrameLayoutReady;
  private int currentAspectRatioFrameLayoutRotation;
  private int currentPlaylistNum;
  private TextureView currentTextureView;
  private FrameLayout currentTextureViewContainer;
  private long currentTotalPcmDuration;
  private boolean decodingFinished = false;
  private boolean downloadingCurrentMessage;
  private ExternalObserver externalObserver;
  private View feedbackView;
  private ByteBuffer fileBuffer;
  private DispatchQueue fileDecodingQueue;
  private DispatchQueue fileEncodingQueue;
  private BaseFragment flagSecureFragment;
  private boolean forceLoopCurrentPlaylist;
  private ArrayList<AudioBuffer> freePlayerBuffers = new ArrayList();
  private HashMap<String, MessageObject> generatingWaveform = new HashMap();
  private float[] gravity = new float[3];
  private float[] gravityFast = new float[3];
  private Sensor gravitySensor;
  private int hasAudioFocus;
  private int ignoreFirstProgress = 0;
  private boolean ignoreOnPause;
  private boolean ignoreProximity;
  private boolean inputFieldHasText;
  private InternalObserver internalObserver;
  private boolean isDrawingWasReady;
  private boolean isPaused = false;
  private int lastChatAccount;
  private long lastChatEnterTime;
  private long lastChatLeaveTime;
  private ArrayList<Long> lastChatVisibleMessages;
  private long lastMediaCheckTime;
  private int lastMessageId;
  private long lastPlayPcm;
  private long lastProgress = 0L;
  private float lastProximityValue = -100.0F;
  private TLRPC.EncryptedChat lastSecretChat;
  private long lastTimestamp = 0L;
  private TLRPC.User lastUser;
  private float[] linearAcceleration = new float[3];
  private Sensor linearSensor;
  private String[] mediaProjections = null;
  private PipRoundVideoView pipRoundVideoView;
  private int pipSwitchingState;
  private boolean playMusicAgain;
  private int playerBufferSize = 3840;
  private final Object playerObjectSync = new Object();
  private DispatchQueue playerQueue;
  private final Object playerSync = new Object();
  private MessageObject playingMessageObject;
  private ArrayList<MessageObject> playlist = new ArrayList();
  private float previousAccValue;
  private Timer progressTimer = null;
  private final Object progressTimerSync = new Object();
  private boolean proximityHasDifferentValues;
  private Sensor proximitySensor;
  private boolean proximityTouched;
  private PowerManager.WakeLock proximityWakeLock;
  private ChatActivity raiseChat;
  private boolean raiseToEarRecord;
  private int raisedToBack;
  private int raisedToTop;
  private int raisedToTopSign;
  private int recordBufferSize = 1280;
  private ArrayList<ByteBuffer> recordBuffers = new ArrayList();
  private long recordDialogId;
  private DispatchQueue recordQueue = new DispatchQueue("recordQueue");
  private MessageObject recordReplyingMessageObject;
  private Runnable recordRunnable = new Runnable()
  {
    public void run()
    {
      final ByteBuffer localByteBuffer;
      if (MediaController.this.audioRecorder != null) {
        if (!MediaController.this.recordBuffers.isEmpty())
        {
          localByteBuffer = (ByteBuffer)MediaController.this.recordBuffers.get(0);
          MediaController.this.recordBuffers.remove(0);
        }
      }
      for (;;)
      {
        localByteBuffer.rewind();
        int i = MediaController.this.audioRecorder.read(localByteBuffer, localByteBuffer.capacity());
        if (i <= 0) {
          break label508;
        }
        localByteBuffer.limit(i);
        double d1 = 0.0D;
        final double d2 = d1;
        try
        {
          long l = MediaController.this.samplesCount + i / 2;
          d2 = d1;
          int j = (int)(MediaController.this.samplesCount / l * MediaController.this.recordSamples.length);
          d2 = d1;
          int k = MediaController.this.recordSamples.length;
          float f2;
          if (j != 0)
          {
            d2 = d1;
            f1 = MediaController.this.recordSamples.length / j;
            f2 = 0.0F;
            m = 0;
            for (;;)
            {
              if (m < j)
              {
                d2 = d1;
                MediaController.this.recordSamples[m] = ((short)MediaController.this.recordSamples[((int)f2)]);
                f2 += f1;
                m++;
                continue;
                localByteBuffer = ByteBuffer.allocateDirect(MediaController.this.recordBufferSize);
                localByteBuffer.order(ByteOrder.nativeOrder());
                break;
              }
            }
          }
          int n = j;
          float f1 = 0.0F;
          float f3 = i / 2.0F / (k - j);
          int m = 0;
          for (;;)
          {
            d2 = d1;
            if (m >= i / 2) {
              break;
            }
            d2 = d1;
            k = localByteBuffer.getShort();
            double d3 = d1;
            if (k > 2500) {
              d3 = d1 + k * k;
            }
            j = n;
            f2 = f1;
            if (m == (int)f1)
            {
              j = n;
              f2 = f1;
              d2 = d3;
              if (n < MediaController.this.recordSamples.length)
              {
                d2 = d3;
                MediaController.this.recordSamples[n] = ((short)k);
                f2 = f1 + f3;
                j = n + 1;
              }
            }
            m++;
            n = j;
            f1 = f2;
            d1 = d3;
          }
          d2 = d1;
          MediaController.access$302(MediaController.this, l);
          d2 = d1;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
            continue;
            final boolean bool = false;
          }
        }
      }
      localByteBuffer.position(0);
      d2 = Math.sqrt(d2 / i / 2.0D);
      if (i != localByteBuffer.capacity())
      {
        bool = true;
        if (i != 0) {
          MediaController.this.fileEncodingQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              if (localByteBuffer.hasRemaining())
              {
                int i = -1;
                if (localByteBuffer.remaining() > MediaController.this.fileBuffer.remaining())
                {
                  i = localByteBuffer.limit();
                  localByteBuffer.limit(MediaController.this.fileBuffer.remaining() + localByteBuffer.position());
                }
                MediaController.this.fileBuffer.put(localByteBuffer);
                MediaController localMediaController;
                ByteBuffer localByteBuffer;
                if ((MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit()) || (bool))
                {
                  localMediaController = MediaController.this;
                  localByteBuffer = MediaController.this.fileBuffer;
                  if (bool) {
                    break label249;
                  }
                }
                label249:
                for (int j = MediaController.this.fileBuffer.limit();; j = localByteBuffer.position())
                {
                  if (localMediaController.writeFrame(localByteBuffer, j) != 0)
                  {
                    MediaController.this.fileBuffer.rewind();
                    MediaController.access$702(MediaController.this, MediaController.this.recordTimeCount + MediaController.this.fileBuffer.limit() / 2 / 16);
                  }
                  if (i == -1) {
                    break;
                  }
                  localByteBuffer.limit(i);
                  break;
                }
              }
              MediaController.this.recordQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  MediaController.this.recordBuffers.add(MediaController.1.1.this.val$finalBuffer);
                }
              });
            }
          });
        }
        MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, new Object[] { Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(d2) });
          }
        });
      }
      for (;;)
      {
        return;
        label508:
        MediaController.this.recordBuffers.add(localByteBuffer);
        MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
      }
    }
  };
  private short[] recordSamples = new short['Ѐ'];
  private Runnable recordStartRunnable;
  private long recordStartTime;
  private long recordTimeCount;
  private TLRPC.TL_document recordingAudio;
  private File recordingAudioFile;
  private int recordingCurrentAccount;
  private boolean resumeAudioOnFocusGain;
  private long samplesCount;
  private float seekToProgressPending;
  private int sendAfterDone;
  private SensorManager sensorManager;
  private boolean sensorsStarted;
  private ArrayList<MessageObject> shuffledPlaylist = new ArrayList();
  private SmsObserver smsObserver;
  private int startObserverToken;
  private StopMediaObserverRunnable stopMediaObserverRunnable;
  private final Object sync = new Object();
  private long timeSinceRaise;
  private boolean useFrontSpeaker;
  private ArrayList<AudioBuffer> usedPlayerBuffers = new ArrayList();
  private boolean videoConvertFirstWrite = true;
  private ArrayList<MessageObject> videoConvertQueue = new ArrayList();
  private final Object videoConvertSync = new Object();
  private VideoPlayer videoPlayer;
  private final Object videoQueueSync = new Object();
  private ArrayList<MessageObject> voiceMessagesPlaylist;
  private SparseArray<MessageObject> voiceMessagesPlaylistMap;
  private boolean voiceMessagesPlaylistUnread;
  
  public MediaController()
  {
    this.recordQueue.setPriority(10);
    this.fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
    this.fileEncodingQueue.setPriority(10);
    this.playerQueue = new DispatchQueue("playerQueue");
    this.fileDecodingQueue = new DispatchQueue("fileDecodingQueue");
    this.recordQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MediaController.access$202(MediaController.this, AudioRecord.getMinBufferSize(16000, 16, 2));
          if (MediaController.this.recordBufferSize <= 0) {
            MediaController.access$202(MediaController.this, 1280);
          }
          MediaController.access$2202(MediaController.this, AudioTrack.getMinBufferSize(48000, 4, 2));
          if (MediaController.this.playerBufferSize <= 0) {
            MediaController.access$2202(MediaController.this, 3840);
          }
          Object localObject;
          for (int i = 0; i < 5; i++)
          {
            localObject = ByteBuffer.allocateDirect(4096);
            ((ByteBuffer)localObject).order(ByteOrder.nativeOrder());
            MediaController.this.recordBuffers.add(localObject);
          }
          for (i = 0; i < 3; i++)
          {
            localObject = MediaController.this.freePlayerBuffers;
            MediaController.AudioBuffer localAudioBuffer = new org/telegram/messenger/MediaController$AudioBuffer;
            localAudioBuffer.<init>(MediaController.this, MediaController.this.playerBufferSize);
            ((ArrayList)localObject).add(localAudioBuffer);
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   4: getstatic 31	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   7: ldc 33
        //   9: invokevirtual 39	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
        //   12: checkcast 41	android/hardware/SensorManager
        //   15: invokestatic 45	org/telegram/messenger/MediaController:access$2402	(Lorg/telegram/messenger/MediaController;Landroid/hardware/SensorManager;)Landroid/hardware/SensorManager;
        //   18: pop
        //   19: aload_0
        //   20: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   23: aload_0
        //   24: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   27: invokestatic 49	org/telegram/messenger/MediaController:access$2400	(Lorg/telegram/messenger/MediaController;)Landroid/hardware/SensorManager;
        //   30: bipush 10
        //   32: invokevirtual 53	android/hardware/SensorManager:getDefaultSensor	(I)Landroid/hardware/Sensor;
        //   35: invokestatic 57	org/telegram/messenger/MediaController:access$2502	(Lorg/telegram/messenger/MediaController;Landroid/hardware/Sensor;)Landroid/hardware/Sensor;
        //   38: pop
        //   39: aload_0
        //   40: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   43: aload_0
        //   44: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   47: invokestatic 49	org/telegram/messenger/MediaController:access$2400	(Lorg/telegram/messenger/MediaController;)Landroid/hardware/SensorManager;
        //   50: bipush 9
        //   52: invokevirtual 53	android/hardware/SensorManager:getDefaultSensor	(I)Landroid/hardware/Sensor;
        //   55: invokestatic 60	org/telegram/messenger/MediaController:access$2602	(Lorg/telegram/messenger/MediaController;Landroid/hardware/Sensor;)Landroid/hardware/Sensor;
        //   58: pop
        //   59: aload_0
        //   60: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   63: invokestatic 64	org/telegram/messenger/MediaController:access$2500	(Lorg/telegram/messenger/MediaController;)Landroid/hardware/Sensor;
        //   66: ifnull +13 -> 79
        //   69: aload_0
        //   70: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   73: invokestatic 67	org/telegram/messenger/MediaController:access$2600	(Lorg/telegram/messenger/MediaController;)Landroid/hardware/Sensor;
        //   76: ifnonnull +51 -> 127
        //   79: getstatic 73	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
        //   82: ifeq +8 -> 90
        //   85: ldc 75
        //   87: invokestatic 81	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
        //   90: aload_0
        //   91: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   94: aload_0
        //   95: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   98: invokestatic 49	org/telegram/messenger/MediaController:access$2400	(Lorg/telegram/messenger/MediaController;)Landroid/hardware/SensorManager;
        //   101: iconst_1
        //   102: invokevirtual 53	android/hardware/SensorManager:getDefaultSensor	(I)Landroid/hardware/Sensor;
        //   105: invokestatic 84	org/telegram/messenger/MediaController:access$2702	(Lorg/telegram/messenger/MediaController;Landroid/hardware/Sensor;)Landroid/hardware/Sensor;
        //   108: pop
        //   109: aload_0
        //   110: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   113: aconst_null
        //   114: invokestatic 57	org/telegram/messenger/MediaController:access$2502	(Lorg/telegram/messenger/MediaController;Landroid/hardware/Sensor;)Landroid/hardware/Sensor;
        //   117: pop
        //   118: aload_0
        //   119: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   122: aconst_null
        //   123: invokestatic 60	org/telegram/messenger/MediaController:access$2602	(Lorg/telegram/messenger/MediaController;Landroid/hardware/Sensor;)Landroid/hardware/Sensor;
        //   126: pop
        //   127: aload_0
        //   128: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   131: aload_0
        //   132: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   135: invokestatic 49	org/telegram/messenger/MediaController:access$2400	(Lorg/telegram/messenger/MediaController;)Landroid/hardware/SensorManager;
        //   138: bipush 8
        //   140: invokevirtual 53	android/hardware/SensorManager:getDefaultSensor	(I)Landroid/hardware/Sensor;
        //   143: invokestatic 87	org/telegram/messenger/MediaController:access$2802	(Lorg/telegram/messenger/MediaController;Landroid/hardware/Sensor;)Landroid/hardware/Sensor;
        //   146: pop
        //   147: getstatic 31	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   150: ldc 89
        //   152: invokevirtual 39	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
        //   155: checkcast 91	android/os/PowerManager
        //   158: astore_1
        //   159: aload_0
        //   160: getfield 20	org/telegram/messenger/MediaController$4:this$0	Lorg/telegram/messenger/MediaController;
        //   163: aload_1
        //   164: bipush 32
        //   166: ldc 93
        //   168: invokevirtual 97	android/os/PowerManager:newWakeLock	(ILjava/lang/String;)Landroid/os/PowerManager$WakeLock;
        //   171: invokestatic 101	org/telegram/messenger/MediaController:access$2902	(Lorg/telegram/messenger/MediaController;Landroid/os/PowerManager$WakeLock;)Landroid/os/PowerManager$WakeLock;
        //   174: pop
        //   175: new 13	org/telegram/messenger/MediaController$4$1
        //   178: astore_2
        //   179: aload_2
        //   180: aload_0
        //   181: invokespecial 104	org/telegram/messenger/MediaController$4$1:<init>	(Lorg/telegram/messenger/MediaController$4;)V
        //   184: getstatic 31	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   187: ldc 106
        //   189: invokevirtual 39	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
        //   192: checkcast 108	android/telephony/TelephonyManager
        //   195: astore_1
        //   196: aload_1
        //   197: ifnull +10 -> 207
        //   200: aload_1
        //   201: aload_2
        //   202: bipush 32
        //   204: invokevirtual 112	android/telephony/TelephonyManager:listen	(Landroid/telephony/PhoneStateListener;I)V
        //   207: return
        //   208: astore_1
        //   209: aload_1
        //   210: invokestatic 116	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   213: goto -38 -> 175
        //   216: astore_1
        //   217: aload_1
        //   218: invokestatic 116	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   221: goto -14 -> 207
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	224	0	this	4
        //   158	43	1	localObject	Object
        //   208	2	1	localException1	Exception
        //   216	2	1	localException2	Exception
        //   178	24	2	local1	1
        // Exception table:
        //   from	to	target	type
        //   0	79	208	java/lang/Exception
        //   79	90	208	java/lang/Exception
        //   90	127	208	java/lang/Exception
        //   127	175	208	java/lang/Exception
        //   175	196	216	java/lang/Exception
        //   200	207	216	java/lang/Exception
      }
    });
    this.fileBuffer = ByteBuffer.allocateDirect(1920);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        for (int i = 0; i < 3; i++)
        {
          NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.FileDidLoaded);
          NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.httpFileDidLoaded);
          NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.didReceivedNewMessages);
          NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.messagesDeleted);
          NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.removeAllMessagesFromDialog);
          NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.musicDidLoaded);
          NotificationCenter.getGlobalInstance().addObserver(MediaController.this, NotificationCenter.playerDidStartPlaying);
        }
      }
    });
    this.mediaProjections = new String[] { "_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height" };
    ContentResolver localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
    try
    {
      localObject1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      localObject2 = new org/telegram/messenger/MediaController$GalleryObserverExternal;
      ((GalleryObserverExternal)localObject2).<init>(this);
      localContentResolver.registerContentObserver((Uri)localObject1, true, (ContentObserver)localObject2);
    }
    catch (Exception localException4)
    {
      try
      {
        localObject1 = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        localObject2 = new org/telegram/messenger/MediaController$GalleryObserverInternal;
        ((GalleryObserverInternal)localObject2).<init>(this);
        localContentResolver.registerContentObserver((Uri)localObject1, true, (ContentObserver)localObject2);
      }
      catch (Exception localException4)
      {
        try
        {
          localObject2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
          localObject1 = new org/telegram/messenger/MediaController$GalleryObserverExternal;
          ((GalleryObserverExternal)localObject1).<init>(this);
          localContentResolver.registerContentObserver((Uri)localObject2, true, (ContentObserver)localObject1);
        }
        catch (Exception localException4)
        {
          try
          {
            for (;;)
            {
              Object localObject1 = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
              Object localObject2 = new org/telegram/messenger/MediaController$GalleryObserverInternal;
              ((GalleryObserverInternal)localObject2).<init>(this);
              localContentResolver.registerContentObserver((Uri)localObject1, true, (ContentObserver)localObject2);
              return;
              localException2 = localException2;
              FileLog.e(localException2);
              continue;
              localException3 = localException3;
              FileLog.e(localException3);
              continue;
              localException4 = localException4;
              FileLog.e(localException4);
            }
          }
          catch (Exception localException1)
          {
            for (;;)
            {
              FileLog.e(localException1);
            }
          }
        }
      }
    }
  }
  
  private static void broadcastNewPhotos(int paramInt1, final ArrayList<AlbumEntry> paramArrayList1, final ArrayList<AlbumEntry> paramArrayList2, final Integer paramInteger, final AlbumEntry paramAlbumEntry1, final AlbumEntry paramAlbumEntry2, int paramInt2)
  {
    if (broadcastPhotosRunnable != null) {
      AndroidUtilities.cancelRunOnUIThread(broadcastPhotosRunnable);
    }
    paramArrayList1 = new Runnable()
    {
      public void run()
      {
        if (PhotoViewer.getInstance().isVisible()) {
          MediaController.broadcastNewPhotos(this.val$guid, paramArrayList1, paramArrayList2, paramInteger, paramAlbumEntry1, paramAlbumEntry2, 1000);
        }
        for (;;)
        {
          return;
          MediaController.access$8002(null);
          MediaController.allPhotosAlbumEntry = paramAlbumEntry2;
          MediaController.allMediaAlbumEntry = paramAlbumEntry1;
          for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.albumsDidLoaded, new Object[] { Integer.valueOf(this.val$guid), paramArrayList1, paramArrayList2, paramInteger });
          }
        }
      }
    };
    broadcastPhotosRunnable = paramArrayList1;
    AndroidUtilities.runOnUIThread(paramArrayList1, paramInt2);
  }
  
  private void buildShuffledPlayList()
  {
    if (this.playlist.isEmpty()) {}
    for (;;)
    {
      return;
      ArrayList localArrayList = new ArrayList(this.playlist);
      this.shuffledPlaylist.clear();
      MessageObject localMessageObject = (MessageObject)this.playlist.get(this.currentPlaylistNum);
      localArrayList.remove(this.currentPlaylistNum);
      this.shuffledPlaylist.add(localMessageObject);
      int i = localArrayList.size();
      for (int j = 0; j < i; j++)
      {
        int k = Utilities.random.nextInt(localArrayList.size());
        this.shuffledPlaylist.add(localArrayList.get(k));
        localArrayList.remove(k);
      }
    }
  }
  
  private void checkAudioFocus(MessageObject paramMessageObject)
  {
    if ((paramMessageObject.isVoice()) || (paramMessageObject.isRoundVideo())) {
      if (this.useFrontSpeaker) {
        i = 3;
      }
    }
    for (;;)
    {
      if (this.hasAudioFocus != i)
      {
        this.hasAudioFocus = i;
        if (i != 3) {
          break;
        }
        i = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
        if (i == 1) {
          this.audioFocus = 2;
        }
      }
      return;
      i = 2;
      continue;
      i = 1;
    }
    paramMessageObject = NotificationsController.audioManager;
    if (i == 2) {}
    for (int i = 3;; i = 1)
    {
      i = paramMessageObject.requestAudioFocus(this, 3, i);
      break;
    }
  }
  
  private void checkConversionCanceled()
  {
    synchronized (this.videoConvertSync)
    {
      boolean bool = this.cancelCurrentVideoConversion;
      if (bool) {
        throw new RuntimeException("canceled conversion");
      }
    }
  }
  
  private void checkDecoderQueue()
  {
    this.fileDecodingQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (MediaController.this.decodingFinished) {
          MediaController.this.checkPlayerQueue();
        }
        for (;;)
        {
          return;
          int i = 0;
          for (;;)
          {
            MediaController.AudioBuffer localAudioBuffer = null;
            synchronized (MediaController.this.playerSync)
            {
              if (!MediaController.this.freePlayerBuffers.isEmpty())
              {
                localAudioBuffer = (MediaController.AudioBuffer)MediaController.this.freePlayerBuffers.get(0);
                MediaController.this.freePlayerBuffers.remove(0);
              }
              if (!MediaController.this.usedPlayerBuffers.isEmpty()) {
                i = 1;
              }
              if (localAudioBuffer == null) {
                break label249;
              }
              MediaController.this.readOpusFile(localAudioBuffer.buffer, MediaController.this.playerBufferSize, MediaController.readArgs);
              localAudioBuffer.size = MediaController.readArgs[0];
              localAudioBuffer.pcmOffset = MediaController.readArgs[1];
              localAudioBuffer.finished = MediaController.readArgs[2];
              if (localAudioBuffer.finished == 1) {
                MediaController.access$4802(MediaController.this, true);
              }
              if (localAudioBuffer.size == 0) {
                break;
              }
              localAudioBuffer.buffer.rewind();
              localAudioBuffer.buffer.get(localAudioBuffer.bufferBytes);
            }
            synchronized (MediaController.this.playerSync)
            {
              MediaController.this.usedPlayerBuffers.add(localAudioBuffer);
              i = 1;
              continue;
              localObject1 = finally;
              throw ((Throwable)localObject1);
            }
          }
          synchronized (MediaController.this.playerSync)
          {
            MediaController.this.freePlayerBuffers.add(localObject2);
            label249:
            if (i == 0) {
              continue;
            }
            MediaController.this.checkPlayerQueue();
          }
        }
      }
    });
  }
  
  public static void checkGallery()
  {
    if ((Build.VERSION.SDK_INT < 24) || (allPhotosAlbumEntry == null)) {}
    for (;;)
    {
      return;
      int i = allPhotosAlbumEntry.photos.size();
      Utilities.globalQueue.postRunnable(new Runnable()
      {
        @SuppressLint({"NewApi"})
        public void run()
        {
          i = 0;
          j = 0;
          Object localObject1 = null;
          Object localObject2 = null;
          Object localObject3 = null;
          int k = j;
          localObject4 = localObject1;
          localObject6 = localObject2;
          try
          {
            if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0)
            {
              localObject4 = localObject1;
              localObject6 = localObject2;
              localObject1 = MediaStore.Images.Media.query(ApplicationLoader.applicationContext.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { "COUNT(_id)" }, null, null, null);
              k = j;
              localObject3 = localObject1;
              if (localObject1 != null)
              {
                k = j;
                localObject3 = localObject1;
                localObject4 = localObject1;
                localObject6 = localObject1;
                if (((Cursor)localObject1).moveToNext())
                {
                  localObject4 = localObject1;
                  localObject6 = localObject1;
                  j = ((Cursor)localObject1).getInt(0);
                  k = 0 + j;
                  localObject3 = localObject1;
                }
              }
            }
            j = k;
            localObject6 = localObject3;
            if (localObject3 != null)
            {
              ((Cursor)localObject3).close();
              localObject6 = localObject3;
              j = k;
            }
          }
          catch (Throwable localThrowable1)
          {
            for (;;)
            {
              localObject6 = localObject4;
              FileLog.e(localThrowable1);
              j = i;
              localObject6 = localObject4;
              if (localObject4 != null)
              {
                ((Cursor)localObject4).close();
                j = i;
                localObject6 = localObject4;
              }
            }
          }
          finally
          {
            if (localObject6 == null) {
              break label363;
            }
            ((Cursor)localObject6).close();
          }
          k = j;
          localObject1 = localObject6;
          localObject3 = localObject6;
          localObject4 = localObject6;
          try
          {
            if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0)
            {
              localObject3 = localObject6;
              localObject4 = localObject6;
              localObject6 = MediaStore.Images.Media.query(ApplicationLoader.applicationContext.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[] { "COUNT(_id)" }, null, null, null);
              k = j;
              localObject1 = localObject6;
              if (localObject6 != null)
              {
                k = j;
                localObject1 = localObject6;
                localObject3 = localObject6;
                localObject4 = localObject6;
                if (((Cursor)localObject6).moveToNext())
                {
                  localObject3 = localObject6;
                  localObject4 = localObject6;
                  k = ((Cursor)localObject6).getInt(0);
                  k = j + k;
                  localObject1 = localObject6;
                }
              }
            }
            i = k;
            if (localObject1 != null)
            {
              ((Cursor)localObject1).close();
              i = k;
            }
          }
          catch (Throwable localThrowable3)
          {
            for (;;)
            {
              localThrowable2 = localThrowable1;
              FileLog.e(localThrowable3);
              i = j;
              if (localThrowable1 != null)
              {
                localThrowable1.close();
                i = j;
              }
            }
          }
          finally
          {
            Throwable localThrowable2;
            if (localThrowable2 == null) {
              break label410;
            }
            localThrowable2.close();
          }
          if (this.val$prevSize != i)
          {
            if (MediaController.refreshGalleryRunnable != null)
            {
              AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
              MediaController.access$1702(null);
            }
            MediaController.loadGalleryPhotosAlbums(0);
          }
        }
      }, 2000L);
    }
  }
  
  private void checkIsNextMusicFileDownloaded(int paramInt)
  {
    if ((DownloadController.getInstance(paramInt).getCurrentDownloadMask() & 0x10) == 0) {}
    label25:
    label64:
    label140:
    label203:
    label205:
    label230:
    label243:
    label244:
    for (;;)
    {
      return;
      Object localObject1;
      int i;
      int j;
      MessageObject localMessageObject;
      Object localObject2;
      if (SharedConfig.shuffleMusic)
      {
        localObject1 = this.shuffledPlaylist;
        if ((localObject1 == null) || (((ArrayList)localObject1).size() < 2)) {
          break label203;
        }
        if (!SharedConfig.playOrderReversed) {
          break label205;
        }
        i = this.currentPlaylistNum + 1;
        j = i;
        if (i >= ((ArrayList)localObject1).size()) {
          j = 0;
        }
        localMessageObject = (MessageObject)((ArrayList)localObject1).get(j);
        if (!DownloadController.getInstance(paramInt).canDownloadMedia(localMessageObject)) {
          continue;
        }
        localObject1 = null;
        if (!TextUtils.isEmpty(localMessageObject.messageOwner.attachPath))
        {
          localObject2 = new File(localMessageObject.messageOwner.attachPath);
          localObject1 = localObject2;
          if (!((File)localObject2).exists()) {
            localObject1 = null;
          }
        }
        if (localObject1 == null) {
          break label230;
        }
        localObject2 = localObject1;
        if ((localObject2 == null) || (!((File)localObject2).exists())) {
          break label243;
        }
      }
      for (;;)
      {
        if ((localObject2 == null) || (localObject2 == localObject1) || (((File)localObject2).exists()) || (!localMessageObject.isMusic())) {
          break label244;
        }
        FileLoader.getInstance(paramInt).loadFile(localMessageObject.getDocument(), false, 0);
        break;
        localObject1 = this.playlist;
        break label25;
        break;
        i = this.currentPlaylistNum - 1;
        j = i;
        if (i >= 0) {
          break label64;
        }
        j = ((ArrayList)localObject1).size() - 1;
        break label64;
        localObject2 = FileLoader.getPathToMessage(localMessageObject.messageOwner);
        break label140;
      }
    }
  }
  
  private void checkIsNextVoiceFileDownloaded(int paramInt)
  {
    if ((this.voiceMessagesPlaylist == null) || (this.voiceMessagesPlaylist.size() < 2)) {}
    label98:
    label153:
    label154:
    for (;;)
    {
      return;
      MessageObject localMessageObject = (MessageObject)this.voiceMessagesPlaylist.get(1);
      Object localObject1 = null;
      Object localObject2 = localObject1;
      if (localMessageObject.messageOwner.attachPath != null)
      {
        localObject2 = localObject1;
        if (localMessageObject.messageOwner.attachPath.length() > 0)
        {
          localObject1 = new File(localMessageObject.messageOwner.attachPath);
          localObject2 = localObject1;
          if (!((File)localObject1).exists()) {
            localObject2 = null;
          }
        }
      }
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        if ((localObject1 == null) || (!((File)localObject1).exists())) {
          break label153;
        }
      }
      for (;;)
      {
        if ((localObject1 == null) || (localObject1 == localObject2) || (((File)localObject1).exists())) {
          break label154;
        }
        FileLoader.getInstance(paramInt).loadFile(localMessageObject.getDocument(), false, 0);
        break;
        localObject1 = FileLoader.getPathToMessage(localMessageObject.messageOwner);
        break label98;
      }
    }
  }
  
  private void checkPlayerQueue()
  {
    this.playerQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          MediaController.AudioBuffer localAudioBuffer;
          int i;
          synchronized (MediaController.this.playerObjectSync)
          {
            if ((MediaController.this.audioTrackPlayer == null) || (MediaController.this.audioTrackPlayer.getPlayState() != 3)) {
              return;
            }
            localAudioBuffer = null;
            synchronized (MediaController.this.playerSync)
            {
              if (!MediaController.this.usedPlayerBuffers.isEmpty())
              {
                localAudioBuffer = (MediaController.AudioBuffer)MediaController.this.usedPlayerBuffers.get(0);
                MediaController.this.usedPlayerBuffers.remove(0);
              }
              if (localAudioBuffer != null) {
                i = 0;
              }
            }
          }
          try
          {
            j = MediaController.this.audioTrackPlayer.write(localAudioBuffer.bufferBytes, 0, localAudioBuffer.size);
            MediaController.access$5408(MediaController.this);
            if (j > 0)
            {
              final long l = localAudioBuffer.pcmOffset;
              if (localAudioBuffer.finished == 1) {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MediaController.access$4102(MediaController.this, l);
                    if (this.val$marker != -1)
                    {
                      if (MediaController.this.audioTrackPlayer != null) {
                        MediaController.this.audioTrackPlayer.setNotificationMarkerPosition(1);
                      }
                      if (this.val$finalBuffersWrited == 1) {
                        MediaController.this.cleanupPlayer(true, true, true);
                      }
                    }
                  }
                });
              }
            }
            else
            {
              if (localAudioBuffer.finished != 1) {
                MediaController.this.checkPlayerQueue();
              }
              if ((localAudioBuffer == null) || ((localAudioBuffer != null) && (localAudioBuffer.finished != 1))) {
                MediaController.this.checkDecoderQueue();
              }
              if (localAudioBuffer == null) {
                continue;
              }
              synchronized (MediaController.this.playerSync)
              {
                MediaController.this.freePlayerBuffers.add(localAudioBuffer);
              }
              localObject3 = finally;
              throw ((Throwable)localObject3);
              localObject4 = finally;
              throw ((Throwable)localObject4);
            }
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
              int j = i;
              continue;
              j = -1;
            }
          }
        }
      }
    });
  }
  
  private void checkScreenshots(ArrayList<Long> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()) || (this.lastChatEnterTime == 0L) || ((this.lastUser == null) && (!(this.lastSecretChat instanceof TLRPC.TL_encryptedChat)))) {}
    for (;;)
    {
      return;
      int i = 0;
      int j = 0;
      if (j < paramArrayList.size())
      {
        Long localLong = (Long)paramArrayList.get(j);
        int k;
        if ((this.lastMediaCheckTime != 0L) && (localLong.longValue() <= this.lastMediaCheckTime)) {
          k = i;
        }
        for (;;)
        {
          j++;
          i = k;
          break;
          k = i;
          if (localLong.longValue() >= this.lastChatEnterTime) {
            if (this.lastChatLeaveTime != 0L)
            {
              k = i;
              if (localLong.longValue() > this.lastChatLeaveTime + 2000L) {}
            }
            else
            {
              this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, localLong.longValue());
              k = 1;
            }
          }
        }
      }
      if (i != 0) {
        if (this.lastSecretChat != null) {
          SecretChatHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastSecretChat, this.lastChatVisibleMessages, null);
        } else {
          SendMessagesHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastUser, this.lastMessageId, null);
        }
      }
    }
  }
  
  private native void closeOpusFile();
  
  /* Error */
  private boolean convertVideo(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   4: getfield 1085	org/telegram/messenger/VideoEditedInfo:originalPath	Ljava/lang/String;
    //   7: astore_2
    //   8: aload_1
    //   9: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   12: getfield 1088	org/telegram/messenger/VideoEditedInfo:startTime	J
    //   15: lstore_3
    //   16: aload_1
    //   17: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   20: getfield 1091	org/telegram/messenger/VideoEditedInfo:endTime	J
    //   23: lstore 5
    //   25: aload_1
    //   26: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   29: getfield 1094	org/telegram/messenger/VideoEditedInfo:resultWidth	I
    //   32: istore 7
    //   34: aload_1
    //   35: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   38: getfield 1097	org/telegram/messenger/VideoEditedInfo:resultHeight	I
    //   41: istore 8
    //   43: aload_1
    //   44: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   47: getfield 1100	org/telegram/messenger/VideoEditedInfo:rotationValue	I
    //   50: istore 9
    //   52: aload_1
    //   53: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   56: getfield 1103	org/telegram/messenger/VideoEditedInfo:originalWidth	I
    //   59: istore 10
    //   61: aload_1
    //   62: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   65: getfield 1106	org/telegram/messenger/VideoEditedInfo:originalHeight	I
    //   68: istore 11
    //   70: aload_1
    //   71: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   74: getfield 1109	org/telegram/messenger/VideoEditedInfo:framerate	I
    //   77: istore 12
    //   79: aload_1
    //   80: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   83: getfield 1112	org/telegram/messenger/VideoEditedInfo:bitrate	I
    //   86: istore 13
    //   88: iconst_0
    //   89: istore 14
    //   91: aload_1
    //   92: invokevirtual 1115	org/telegram/messenger/MessageObject:getDialogId	()J
    //   95: l2i
    //   96: ifne +182 -> 278
    //   99: iconst_1
    //   100: istore 15
    //   102: new 998	java/io/File
    //   105: dup
    //   106: aload_1
    //   107: getfield 986	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   110: getfield 991	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   113: invokespecial 999	java/io/File:<init>	(Ljava/lang/String;)V
    //   116: astore 16
    //   118: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   121: bipush 18
    //   123: if_icmpge +161 -> 284
    //   126: iload 8
    //   128: iload 7
    //   130: if_icmple +154 -> 284
    //   133: iload 7
    //   135: iload 10
    //   137: if_icmpeq +147 -> 284
    //   140: iload 8
    //   142: iload 11
    //   144: if_icmpeq +140 -> 284
    //   147: iload 7
    //   149: istore 17
    //   151: iload 8
    //   153: istore 18
    //   155: bipush 90
    //   157: istore 19
    //   159: sipush 270
    //   162: istore 20
    //   164: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   167: ldc_w 1117
    //   170: iconst_0
    //   171: invokevirtual 1121	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   174: astore 21
    //   176: new 998	java/io/File
    //   179: dup
    //   180: aload_2
    //   181: invokespecial 999	java/io/File:<init>	(Ljava/lang/String;)V
    //   184: astore 22
    //   186: aload_1
    //   187: invokevirtual 1124	org/telegram/messenger/MessageObject:getId	()I
    //   190: ifeq +213 -> 403
    //   193: aload 21
    //   195: ldc_w 1126
    //   198: iconst_1
    //   199: invokeinterface 1132 3 0
    //   204: istore 23
    //   206: aload 21
    //   208: invokeinterface 1136 1 0
    //   213: ldc_w 1126
    //   216: iconst_0
    //   217: invokeinterface 1142 3 0
    //   222: invokeinterface 1145 1 0
    //   227: pop
    //   228: aload 22
    //   230: invokevirtual 1148	java/io/File:canRead	()Z
    //   233: ifeq +8 -> 241
    //   236: iload 23
    //   238: ifne +165 -> 403
    //   241: aload_0
    //   242: aload_1
    //   243: aload 16
    //   245: iconst_1
    //   246: iconst_1
    //   247: invokespecial 1152	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   250: aload 21
    //   252: invokeinterface 1136 1 0
    //   257: ldc_w 1126
    //   260: iconst_1
    //   261: invokeinterface 1142 3 0
    //   266: invokeinterface 1145 1 0
    //   271: pop
    //   272: iconst_0
    //   273: istore 15
    //   275: iload 15
    //   277: ireturn
    //   278: iconst_0
    //   279: istore 15
    //   281: goto -179 -> 102
    //   284: iload 8
    //   286: istore 17
    //   288: iload 7
    //   290: istore 18
    //   292: iload 14
    //   294: istore 20
    //   296: iload 9
    //   298: istore 19
    //   300: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   303: bipush 20
    //   305: if_icmple -141 -> 164
    //   308: iload 9
    //   310: bipush 90
    //   312: if_icmpne +22 -> 334
    //   315: iload 7
    //   317: istore 17
    //   319: iload 8
    //   321: istore 18
    //   323: iconst_0
    //   324: istore 19
    //   326: sipush 270
    //   329: istore 20
    //   331: goto -167 -> 164
    //   334: iload 9
    //   336: sipush 180
    //   339: if_icmpne +22 -> 361
    //   342: sipush 180
    //   345: istore 20
    //   347: iconst_0
    //   348: istore 19
    //   350: iload 8
    //   352: istore 17
    //   354: iload 7
    //   356: istore 18
    //   358: goto -194 -> 164
    //   361: iload 8
    //   363: istore 17
    //   365: iload 7
    //   367: istore 18
    //   369: iload 14
    //   371: istore 20
    //   373: iload 9
    //   375: istore 19
    //   377: iload 9
    //   379: sipush 270
    //   382: if_icmpne -218 -> 164
    //   385: iload 7
    //   387: istore 17
    //   389: iload 8
    //   391: istore 18
    //   393: iconst_0
    //   394: istore 19
    //   396: bipush 90
    //   398: istore 20
    //   400: goto -236 -> 164
    //   403: aload_0
    //   404: iconst_1
    //   405: putfield 404	org/telegram/messenger/MediaController:videoConvertFirstWrite	Z
    //   408: iconst_0
    //   409: istore 23
    //   411: iconst_0
    //   412: istore 24
    //   414: invokestatic 1157	java/lang/System:currentTimeMillis	()J
    //   417: lstore 25
    //   419: iload 18
    //   421: ifeq +6241 -> 6662
    //   424: iload 17
    //   426: ifeq +6236 -> 6662
    //   429: aconst_null
    //   430: astore 27
    //   432: aconst_null
    //   433: astore 22
    //   435: aconst_null
    //   436: astore 28
    //   438: aconst_null
    //   439: astore 29
    //   441: aload 28
    //   443: astore 30
    //   445: aload 22
    //   447: astore 31
    //   449: aload 27
    //   451: astore 32
    //   453: new 1159	android/media/MediaCodec$BufferInfo
    //   456: astore 33
    //   458: aload 28
    //   460: astore 30
    //   462: aload 22
    //   464: astore 31
    //   466: aload 27
    //   468: astore 32
    //   470: aload 33
    //   472: invokespecial 1160	android/media/MediaCodec$BufferInfo:<init>	()V
    //   475: aload 28
    //   477: astore 30
    //   479: aload 22
    //   481: astore 31
    //   483: aload 27
    //   485: astore 32
    //   487: new 1162	org/telegram/messenger/video/Mp4Movie
    //   490: astore 34
    //   492: aload 28
    //   494: astore 30
    //   496: aload 22
    //   498: astore 31
    //   500: aload 27
    //   502: astore 32
    //   504: aload 34
    //   506: invokespecial 1163	org/telegram/messenger/video/Mp4Movie:<init>	()V
    //   509: aload 28
    //   511: astore 30
    //   513: aload 22
    //   515: astore 31
    //   517: aload 27
    //   519: astore 32
    //   521: aload 34
    //   523: aload 16
    //   525: invokevirtual 1167	org/telegram/messenger/video/Mp4Movie:setCacheFile	(Ljava/io/File;)V
    //   528: aload 28
    //   530: astore 30
    //   532: aload 22
    //   534: astore 31
    //   536: aload 27
    //   538: astore 32
    //   540: aload 34
    //   542: iload 19
    //   544: invokevirtual 1170	org/telegram/messenger/video/Mp4Movie:setRotation	(I)V
    //   547: aload 28
    //   549: astore 30
    //   551: aload 22
    //   553: astore 31
    //   555: aload 27
    //   557: astore 32
    //   559: aload 34
    //   561: iload 18
    //   563: iload 17
    //   565: invokevirtual 1174	org/telegram/messenger/video/Mp4Movie:setSize	(II)V
    //   568: aload 28
    //   570: astore 30
    //   572: aload 22
    //   574: astore 31
    //   576: aload 27
    //   578: astore 32
    //   580: new 1176	org/telegram/messenger/video/MP4Builder
    //   583: astore 35
    //   585: aload 28
    //   587: astore 30
    //   589: aload 22
    //   591: astore 31
    //   593: aload 27
    //   595: astore 32
    //   597: aload 35
    //   599: invokespecial 1177	org/telegram/messenger/video/MP4Builder:<init>	()V
    //   602: aload 28
    //   604: astore 30
    //   606: aload 22
    //   608: astore 31
    //   610: aload 27
    //   612: astore 32
    //   614: aload 35
    //   616: aload 34
    //   618: iload 15
    //   620: invokevirtual 1181	org/telegram/messenger/video/MP4Builder:createMovie	(Lorg/telegram/messenger/video/Mp4Movie;Z)Lorg/telegram/messenger/video/MP4Builder;
    //   623: astore 22
    //   625: aload 28
    //   627: astore 30
    //   629: aload 22
    //   631: astore 31
    //   633: aload 22
    //   635: astore 32
    //   637: new 1183	android/media/MediaExtractor
    //   640: astore 35
    //   642: aload 28
    //   644: astore 30
    //   646: aload 22
    //   648: astore 31
    //   650: aload 22
    //   652: astore 32
    //   654: aload 35
    //   656: invokespecial 1184	android/media/MediaExtractor:<init>	()V
    //   659: aload 35
    //   661: aload_2
    //   662: invokevirtual 1187	android/media/MediaExtractor:setDataSource	(Ljava/lang/String;)V
    //   665: aload_0
    //   666: invokespecial 1189	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   669: iload 18
    //   671: iload 10
    //   673: if_icmpne +25 -> 698
    //   676: iload 17
    //   678: iload 11
    //   680: if_icmpne +18 -> 698
    //   683: iload 20
    //   685: ifne +13 -> 698
    //   688: aload_1
    //   689: getfield 1080	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   692: getfield 1192	org/telegram/messenger/VideoEditedInfo:roundVideo	Z
    //   695: ifeq +5809 -> 6504
    //   698: aload_0
    //   699: aload 35
    //   701: iconst_0
    //   702: invokespecial 1196	org/telegram/messenger/MediaController:findTrack	(Landroid/media/MediaExtractor;Z)I
    //   705: istore 36
    //   707: iload 13
    //   709: iconst_m1
    //   710: if_icmpeq +395 -> 1105
    //   713: aload_0
    //   714: aload 35
    //   716: iconst_1
    //   717: invokespecial 1196	org/telegram/messenger/MediaController:findTrack	(Landroid/media/MediaExtractor;Z)I
    //   720: istore 14
    //   722: iload 23
    //   724: istore 15
    //   726: iload 36
    //   728: iflt +285 -> 1013
    //   731: aconst_null
    //   732: astore 37
    //   734: aconst_null
    //   735: astore 38
    //   737: aconst_null
    //   738: astore 31
    //   740: aconst_null
    //   741: astore 39
    //   743: aconst_null
    //   744: astore 40
    //   746: aconst_null
    //   747: astore 34
    //   749: aconst_null
    //   750: astore 30
    //   752: ldc2_w 1197
    //   755: lstore 41
    //   757: iconst_0
    //   758: istore 43
    //   760: iconst_0
    //   761: istore 44
    //   763: iconst_0
    //   764: istore 45
    //   766: iconst_0
    //   767: istore 11
    //   769: iconst_0
    //   770: istore 9
    //   772: bipush -5
    //   774: istore 46
    //   776: bipush -5
    //   778: istore 47
    //   780: iconst_0
    //   781: istore 19
    //   783: iconst_0
    //   784: istore 8
    //   786: aload 38
    //   788: astore 32
    //   790: aload 31
    //   792: astore 29
    //   794: aload 40
    //   796: astore 27
    //   798: aload 30
    //   800: astore_2
    //   801: getstatic 1203	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   804: invokevirtual 1207	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   807: astore 28
    //   809: aload 38
    //   811: astore 32
    //   813: aload 31
    //   815: astore 29
    //   817: aload 40
    //   819: astore 27
    //   821: aload 30
    //   823: astore_2
    //   824: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   827: bipush 18
    //   829: if_icmpge +2303 -> 3132
    //   832: aload 38
    //   834: astore 32
    //   836: aload 31
    //   838: astore 29
    //   840: aload 40
    //   842: astore 27
    //   844: aload 30
    //   846: astore_2
    //   847: ldc -88
    //   849: invokestatic 1211	org/telegram/messenger/MediaController:selectCodec	(Ljava/lang/String;)Landroid/media/MediaCodecInfo;
    //   852: astore 48
    //   854: aload 38
    //   856: astore 32
    //   858: aload 31
    //   860: astore 29
    //   862: aload 40
    //   864: astore 27
    //   866: aload 30
    //   868: astore_2
    //   869: aload 48
    //   871: ldc -88
    //   873: invokestatic 1215	org/telegram/messenger/MediaController:selectColorFormat	(Landroid/media/MediaCodecInfo;Ljava/lang/String;)I
    //   876: istore 7
    //   878: iload 7
    //   880: ifne +231 -> 1111
    //   883: aload 38
    //   885: astore 32
    //   887: aload 31
    //   889: astore 29
    //   891: aload 40
    //   893: astore 27
    //   895: aload 30
    //   897: astore_2
    //   898: new 940	java/lang/RuntimeException
    //   901: astore 28
    //   903: aload 38
    //   905: astore 32
    //   907: aload 31
    //   909: astore 29
    //   911: aload 40
    //   913: astore 27
    //   915: aload 30
    //   917: astore_2
    //   918: aload 28
    //   920: ldc_w 1217
    //   923: invokespecial 943	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   926: aload 38
    //   928: astore 32
    //   930: aload 31
    //   932: astore 29
    //   934: aload 40
    //   936: astore 27
    //   938: aload 30
    //   940: astore_2
    //   941: aload 28
    //   943: athrow
    //   944: astore 31
    //   946: aload 31
    //   948: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   951: iconst_1
    //   952: istore 15
    //   954: aload 35
    //   956: iload 36
    //   958: invokevirtual 1220	android/media/MediaExtractor:unselectTrack	(I)V
    //   961: aload_2
    //   962: ifnull +7 -> 969
    //   965: aload_2
    //   966: invokevirtual 1225	org/telegram/messenger/video/OutputSurface:release	()V
    //   969: aload 27
    //   971: ifnull +8 -> 979
    //   974: aload 27
    //   976: invokevirtual 1228	org/telegram/messenger/video/InputSurface:release	()V
    //   979: aload 32
    //   981: ifnull +13 -> 994
    //   984: aload 32
    //   986: invokevirtual 1233	android/media/MediaCodec:stop	()V
    //   989: aload 32
    //   991: invokevirtual 1234	android/media/MediaCodec:release	()V
    //   994: aload 29
    //   996: ifnull +13 -> 1009
    //   999: aload 29
    //   1001: invokevirtual 1233	android/media/MediaCodec:stop	()V
    //   1004: aload 29
    //   1006: invokevirtual 1234	android/media/MediaCodec:release	()V
    //   1009: aload_0
    //   1010: invokespecial 1189	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   1013: aload 35
    //   1015: ifnull +8 -> 1023
    //   1018: aload 35
    //   1020: invokevirtual 1235	android/media/MediaExtractor:release	()V
    //   1023: aload 22
    //   1025: ifnull +8 -> 1033
    //   1028: aload 22
    //   1030: invokevirtual 1238	org/telegram/messenger/video/MP4Builder:finishMovie	()V
    //   1033: getstatic 1243	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   1036: ifeq +5704 -> 6740
    //   1039: new 1245	java/lang/StringBuilder
    //   1042: dup
    //   1043: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   1046: ldc_w 1248
    //   1049: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1052: invokestatic 1157	java/lang/System:currentTimeMillis	()J
    //   1055: lload 25
    //   1057: lsub
    //   1058: invokevirtual 1255	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1061: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1064: invokestatic 1261	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   1067: aload 21
    //   1069: invokeinterface 1136 1 0
    //   1074: ldc_w 1126
    //   1077: iconst_1
    //   1078: invokeinterface 1142 3 0
    //   1083: invokeinterface 1145 1 0
    //   1088: pop
    //   1089: aload_0
    //   1090: aload_1
    //   1091: aload 16
    //   1093: iconst_1
    //   1094: iload 15
    //   1096: invokespecial 1152	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   1099: iconst_1
    //   1100: istore 15
    //   1102: goto -827 -> 275
    //   1105: iconst_m1
    //   1106: istore 14
    //   1108: goto -386 -> 722
    //   1111: aload 38
    //   1113: astore 32
    //   1115: aload 31
    //   1117: astore 29
    //   1119: aload 40
    //   1121: astore 27
    //   1123: aload 30
    //   1125: astore_2
    //   1126: aload 48
    //   1128: invokevirtual 1266	android/media/MediaCodecInfo:getName	()Ljava/lang/String;
    //   1131: astore 49
    //   1133: aload 38
    //   1135: astore 32
    //   1137: aload 31
    //   1139: astore 29
    //   1141: aload 40
    //   1143: astore 27
    //   1145: aload 30
    //   1147: astore_2
    //   1148: aload 49
    //   1150: ldc_w 1268
    //   1153: invokevirtual 1271	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   1156: ifeq +1825 -> 2981
    //   1159: iconst_1
    //   1160: istore 10
    //   1162: aload 38
    //   1164: astore 32
    //   1166: aload 31
    //   1168: astore 29
    //   1170: aload 40
    //   1172: astore 27
    //   1174: aload 30
    //   1176: astore_2
    //   1177: iload 10
    //   1179: istore 19
    //   1181: iload 9
    //   1183: istore 8
    //   1185: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   1188: bipush 16
    //   1190: if_icmpne +70 -> 1260
    //   1193: aload 38
    //   1195: astore 32
    //   1197: aload 31
    //   1199: astore 29
    //   1201: aload 40
    //   1203: astore 27
    //   1205: aload 30
    //   1207: astore_2
    //   1208: aload 28
    //   1210: ldc_w 1273
    //   1213: invokevirtual 1276	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1216: ifne +37 -> 1253
    //   1219: aload 38
    //   1221: astore 32
    //   1223: aload 31
    //   1225: astore 29
    //   1227: aload 40
    //   1229: astore 27
    //   1231: aload 30
    //   1233: astore_2
    //   1234: iload 10
    //   1236: istore 19
    //   1238: iload 9
    //   1240: istore 8
    //   1242: aload 28
    //   1244: ldc_w 1278
    //   1247: invokevirtual 1276	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1250: ifeq +10 -> 1260
    //   1253: iconst_1
    //   1254: istore 8
    //   1256: iload 10
    //   1258: istore 19
    //   1260: aload 38
    //   1262: astore 32
    //   1264: aload 31
    //   1266: astore 29
    //   1268: aload 40
    //   1270: astore 27
    //   1272: aload 30
    //   1274: astore_2
    //   1275: iload 7
    //   1277: istore 10
    //   1279: iload 19
    //   1281: istore 9
    //   1283: iload 8
    //   1285: istore 11
    //   1287: getstatic 1243	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   1290: ifeq +115 -> 1405
    //   1293: aload 38
    //   1295: astore 32
    //   1297: aload 31
    //   1299: astore 29
    //   1301: aload 40
    //   1303: astore 27
    //   1305: aload 30
    //   1307: astore_2
    //   1308: new 1245	java/lang/StringBuilder
    //   1311: astore 49
    //   1313: aload 38
    //   1315: astore 32
    //   1317: aload 31
    //   1319: astore 29
    //   1321: aload 40
    //   1323: astore 27
    //   1325: aload 30
    //   1327: astore_2
    //   1328: aload 49
    //   1330: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   1333: aload 38
    //   1335: astore 32
    //   1337: aload 31
    //   1339: astore 29
    //   1341: aload 40
    //   1343: astore 27
    //   1345: aload 30
    //   1347: astore_2
    //   1348: aload 49
    //   1350: ldc_w 1280
    //   1353: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1356: aload 48
    //   1358: invokevirtual 1266	android/media/MediaCodecInfo:getName	()Ljava/lang/String;
    //   1361: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1364: ldc_w 1282
    //   1367: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1370: aload 28
    //   1372: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1375: ldc_w 1284
    //   1378: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1381: getstatic 1287	android/os/Build:MODEL	Ljava/lang/String;
    //   1384: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1387: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1390: invokestatic 1261	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   1393: iload 8
    //   1395: istore 11
    //   1397: iload 19
    //   1399: istore 9
    //   1401: iload 7
    //   1403: istore 10
    //   1405: aload 38
    //   1407: astore 32
    //   1409: aload 31
    //   1411: astore 29
    //   1413: aload 40
    //   1415: astore 27
    //   1417: aload 30
    //   1419: astore_2
    //   1420: getstatic 1243	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   1423: ifeq +77 -> 1500
    //   1426: aload 38
    //   1428: astore 32
    //   1430: aload 31
    //   1432: astore 29
    //   1434: aload 40
    //   1436: astore 27
    //   1438: aload 30
    //   1440: astore_2
    //   1441: new 1245	java/lang/StringBuilder
    //   1444: astore 48
    //   1446: aload 38
    //   1448: astore 32
    //   1450: aload 31
    //   1452: astore 29
    //   1454: aload 40
    //   1456: astore 27
    //   1458: aload 30
    //   1460: astore_2
    //   1461: aload 48
    //   1463: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   1466: aload 38
    //   1468: astore 32
    //   1470: aload 31
    //   1472: astore 29
    //   1474: aload 40
    //   1476: astore 27
    //   1478: aload 30
    //   1480: astore_2
    //   1481: aload 48
    //   1483: ldc_w 1289
    //   1486: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1489: iload 10
    //   1491: invokevirtual 1292	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1494: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1497: invokestatic 1261	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   1500: iconst_0
    //   1501: istore 50
    //   1503: aload 38
    //   1505: astore 32
    //   1507: aload 31
    //   1509: astore 29
    //   1511: aload 40
    //   1513: astore 27
    //   1515: aload 30
    //   1517: astore_2
    //   1518: iload 18
    //   1520: iload 17
    //   1522: imul
    //   1523: iconst_3
    //   1524: imul
    //   1525: iconst_2
    //   1526: idiv
    //   1527: istore 19
    //   1529: iload 9
    //   1531: ifne +1613 -> 3144
    //   1534: iload 19
    //   1536: istore 8
    //   1538: iload 50
    //   1540: istore 7
    //   1542: iload 17
    //   1544: bipush 16
    //   1546: irem
    //   1547: ifeq +48 -> 1595
    //   1550: iload 18
    //   1552: iload 17
    //   1554: bipush 16
    //   1556: iload 17
    //   1558: bipush 16
    //   1560: irem
    //   1561: isub
    //   1562: iadd
    //   1563: iload 17
    //   1565: isub
    //   1566: imul
    //   1567: istore 7
    //   1569: aload 38
    //   1571: astore 32
    //   1573: aload 31
    //   1575: astore 29
    //   1577: aload 40
    //   1579: astore 27
    //   1581: aload 30
    //   1583: astore_2
    //   1584: iload 19
    //   1586: iload 7
    //   1588: iconst_5
    //   1589: imul
    //   1590: iconst_4
    //   1591: idiv
    //   1592: iadd
    //   1593: istore 8
    //   1595: aload 38
    //   1597: astore 32
    //   1599: aload 31
    //   1601: astore 29
    //   1603: aload 40
    //   1605: astore 27
    //   1607: aload 30
    //   1609: astore_2
    //   1610: aload 35
    //   1612: iload 36
    //   1614: invokevirtual 1295	android/media/MediaExtractor:selectTrack	(I)V
    //   1617: aload 38
    //   1619: astore 32
    //   1621: aload 31
    //   1623: astore 29
    //   1625: aload 40
    //   1627: astore 27
    //   1629: aload 30
    //   1631: astore_2
    //   1632: aload 35
    //   1634: iload 36
    //   1636: invokevirtual 1299	android/media/MediaExtractor:getTrackFormat	(I)Landroid/media/MediaFormat;
    //   1639: astore 49
    //   1641: aconst_null
    //   1642: astore 48
    //   1644: iload 14
    //   1646: iflt +102 -> 1748
    //   1649: aload 38
    //   1651: astore 32
    //   1653: aload 31
    //   1655: astore 29
    //   1657: aload 40
    //   1659: astore 27
    //   1661: aload 30
    //   1663: astore_2
    //   1664: aload 35
    //   1666: iload 14
    //   1668: invokevirtual 1295	android/media/MediaExtractor:selectTrack	(I)V
    //   1671: aload 38
    //   1673: astore 32
    //   1675: aload 31
    //   1677: astore 29
    //   1679: aload 40
    //   1681: astore 27
    //   1683: aload 30
    //   1685: astore_2
    //   1686: aload 35
    //   1688: iload 14
    //   1690: invokevirtual 1299	android/media/MediaExtractor:getTrackFormat	(I)Landroid/media/MediaFormat;
    //   1693: astore 28
    //   1695: aload 38
    //   1697: astore 32
    //   1699: aload 31
    //   1701: astore 29
    //   1703: aload 40
    //   1705: astore 27
    //   1707: aload 30
    //   1709: astore_2
    //   1710: aload 28
    //   1712: ldc_w 1301
    //   1715: invokevirtual 1306	android/media/MediaFormat:getInteger	(Ljava/lang/String;)I
    //   1718: invokestatic 492	java/nio/ByteBuffer:allocateDirect	(I)Ljava/nio/ByteBuffer;
    //   1721: astore 48
    //   1723: aload 38
    //   1725: astore 32
    //   1727: aload 31
    //   1729: astore 29
    //   1731: aload 40
    //   1733: astore 27
    //   1735: aload 30
    //   1737: astore_2
    //   1738: aload 22
    //   1740: aload 28
    //   1742: iconst_1
    //   1743: invokevirtual 1310	org/telegram/messenger/video/MP4Builder:addTrack	(Landroid/media/MediaFormat;Z)I
    //   1746: istore 47
    //   1748: lload_3
    //   1749: lconst_0
    //   1750: lcmp
    //   1751: ifle +1577 -> 3328
    //   1754: aload 38
    //   1756: astore 32
    //   1758: aload 31
    //   1760: astore 29
    //   1762: aload 40
    //   1764: astore 27
    //   1766: aload 30
    //   1768: astore_2
    //   1769: aload 35
    //   1771: lload_3
    //   1772: iconst_0
    //   1773: invokevirtual 1314	android/media/MediaExtractor:seekTo	(JI)V
    //   1776: aload 38
    //   1778: astore 32
    //   1780: aload 31
    //   1782: astore 29
    //   1784: aload 40
    //   1786: astore 27
    //   1788: aload 30
    //   1790: astore_2
    //   1791: ldc -88
    //   1793: iload 18
    //   1795: iload 17
    //   1797: invokestatic 1318	android/media/MediaFormat:createVideoFormat	(Ljava/lang/String;II)Landroid/media/MediaFormat;
    //   1800: astore 51
    //   1802: aload 38
    //   1804: astore 32
    //   1806: aload 31
    //   1808: astore 29
    //   1810: aload 40
    //   1812: astore 27
    //   1814: aload 30
    //   1816: astore_2
    //   1817: aload 51
    //   1819: ldc_w 1320
    //   1822: iload 10
    //   1824: invokevirtual 1324	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1827: iload 13
    //   1829: ifle +1585 -> 3414
    //   1832: iload 13
    //   1834: istore 19
    //   1836: aload 38
    //   1838: astore 32
    //   1840: aload 31
    //   1842: astore 29
    //   1844: aload 40
    //   1846: astore 27
    //   1848: aload 30
    //   1850: astore_2
    //   1851: aload 51
    //   1853: ldc_w 1325
    //   1856: iload 19
    //   1858: invokevirtual 1324	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1861: iload 12
    //   1863: ifeq +1559 -> 3422
    //   1866: iload 12
    //   1868: istore 19
    //   1870: aload 38
    //   1872: astore 32
    //   1874: aload 31
    //   1876: astore 29
    //   1878: aload 40
    //   1880: astore 27
    //   1882: aload 30
    //   1884: astore_2
    //   1885: aload 51
    //   1887: ldc_w 1327
    //   1890: iload 19
    //   1892: invokevirtual 1324	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1895: aload 38
    //   1897: astore 32
    //   1899: aload 31
    //   1901: astore 29
    //   1903: aload 40
    //   1905: astore 27
    //   1907: aload 30
    //   1909: astore_2
    //   1910: aload 51
    //   1912: ldc_w 1329
    //   1915: bipush 10
    //   1917: invokevirtual 1324	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1920: aload 38
    //   1922: astore 32
    //   1924: aload 31
    //   1926: astore 29
    //   1928: aload 40
    //   1930: astore 27
    //   1932: aload 30
    //   1934: astore_2
    //   1935: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   1938: bipush 18
    //   1940: if_icmpge +56 -> 1996
    //   1943: aload 38
    //   1945: astore 32
    //   1947: aload 31
    //   1949: astore 29
    //   1951: aload 40
    //   1953: astore 27
    //   1955: aload 30
    //   1957: astore_2
    //   1958: aload 51
    //   1960: ldc_w 1331
    //   1963: iload 18
    //   1965: bipush 32
    //   1967: iadd
    //   1968: invokevirtual 1324	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1971: aload 38
    //   1973: astore 32
    //   1975: aload 31
    //   1977: astore 29
    //   1979: aload 40
    //   1981: astore 27
    //   1983: aload 30
    //   1985: astore_2
    //   1986: aload 51
    //   1988: ldc_w 1333
    //   1991: iload 17
    //   1993: invokevirtual 1324	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1996: aload 38
    //   1998: astore 32
    //   2000: aload 31
    //   2002: astore 29
    //   2004: aload 40
    //   2006: astore 27
    //   2008: aload 30
    //   2010: astore_2
    //   2011: ldc -88
    //   2013: invokestatic 1337	android/media/MediaCodec:createEncoderByType	(Ljava/lang/String;)Landroid/media/MediaCodec;
    //   2016: astore 28
    //   2018: aload 38
    //   2020: astore 32
    //   2022: aload 28
    //   2024: astore 29
    //   2026: aload 40
    //   2028: astore 27
    //   2030: aload 30
    //   2032: astore_2
    //   2033: aload 28
    //   2035: aload 51
    //   2037: aconst_null
    //   2038: aconst_null
    //   2039: iconst_1
    //   2040: invokevirtual 1341	android/media/MediaCodec:configure	(Landroid/media/MediaFormat;Landroid/view/Surface;Landroid/media/MediaCrypto;I)V
    //   2043: aload 38
    //   2045: astore 32
    //   2047: aload 28
    //   2049: astore 29
    //   2051: aload 40
    //   2053: astore 27
    //   2055: aload 30
    //   2057: astore_2
    //   2058: aload 39
    //   2060: astore 31
    //   2062: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   2065: bipush 18
    //   2067: if_icmplt +53 -> 2120
    //   2070: aload 38
    //   2072: astore 32
    //   2074: aload 28
    //   2076: astore 29
    //   2078: aload 40
    //   2080: astore 27
    //   2082: aload 30
    //   2084: astore_2
    //   2085: new 1227	org/telegram/messenger/video/InputSurface
    //   2088: astore 31
    //   2090: aload 38
    //   2092: astore 32
    //   2094: aload 28
    //   2096: astore 29
    //   2098: aload 40
    //   2100: astore 27
    //   2102: aload 30
    //   2104: astore_2
    //   2105: aload 31
    //   2107: aload 28
    //   2109: invokevirtual 1345	android/media/MediaCodec:createInputSurface	()Landroid/view/Surface;
    //   2112: invokespecial 1348	org/telegram/messenger/video/InputSurface:<init>	(Landroid/view/Surface;)V
    //   2115: aload 31
    //   2117: invokevirtual 1351	org/telegram/messenger/video/InputSurface:makeCurrent	()V
    //   2120: aload 38
    //   2122: astore 32
    //   2124: aload 28
    //   2126: astore 29
    //   2128: aload 31
    //   2130: astore 27
    //   2132: aload 30
    //   2134: astore_2
    //   2135: aload 28
    //   2137: invokevirtual 1354	android/media/MediaCodec:start	()V
    //   2140: aload 38
    //   2142: astore 32
    //   2144: aload 28
    //   2146: astore 29
    //   2148: aload 31
    //   2150: astore 27
    //   2152: aload 30
    //   2154: astore_2
    //   2155: aload 49
    //   2157: ldc_w 1356
    //   2160: invokevirtual 1360	android/media/MediaFormat:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   2163: invokestatic 1363	android/media/MediaCodec:createDecoderByType	(Ljava/lang/String;)Landroid/media/MediaCodec;
    //   2166: astore 37
    //   2168: aload 37
    //   2170: astore 32
    //   2172: aload 28
    //   2174: astore 29
    //   2176: aload 31
    //   2178: astore 27
    //   2180: aload 30
    //   2182: astore_2
    //   2183: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   2186: bipush 18
    //   2188: if_icmplt +1241 -> 3429
    //   2191: aload 37
    //   2193: astore 32
    //   2195: aload 28
    //   2197: astore 29
    //   2199: aload 31
    //   2201: astore 27
    //   2203: aload 30
    //   2205: astore_2
    //   2206: new 1222	org/telegram/messenger/video/OutputSurface
    //   2209: astore 34
    //   2211: aload 37
    //   2213: astore 32
    //   2215: aload 28
    //   2217: astore 29
    //   2219: aload 31
    //   2221: astore 27
    //   2223: aload 30
    //   2225: astore_2
    //   2226: aload 34
    //   2228: invokespecial 1364	org/telegram/messenger/video/OutputSurface:<init>	()V
    //   2231: aload 34
    //   2233: astore 30
    //   2235: aload 37
    //   2237: astore 32
    //   2239: aload 28
    //   2241: astore 29
    //   2243: aload 31
    //   2245: astore 27
    //   2247: aload 30
    //   2249: astore_2
    //   2250: aload 37
    //   2252: aload 49
    //   2254: aload 30
    //   2256: invokevirtual 1367	org/telegram/messenger/video/OutputSurface:getSurface	()Landroid/view/Surface;
    //   2259: aconst_null
    //   2260: iconst_0
    //   2261: invokevirtual 1341	android/media/MediaCodec:configure	(Landroid/media/MediaFormat;Landroid/view/Surface;Landroid/media/MediaCrypto;I)V
    //   2264: aload 37
    //   2266: astore 32
    //   2268: aload 28
    //   2270: astore 29
    //   2272: aload 31
    //   2274: astore 27
    //   2276: aload 30
    //   2278: astore_2
    //   2279: aload 37
    //   2281: invokevirtual 1354	android/media/MediaCodec:start	()V
    //   2284: aconst_null
    //   2285: astore 40
    //   2287: aconst_null
    //   2288: astore 34
    //   2290: aconst_null
    //   2291: astore 39
    //   2293: aload 37
    //   2295: astore 32
    //   2297: aload 28
    //   2299: astore 29
    //   2301: aload 31
    //   2303: astore 27
    //   2305: aload 30
    //   2307: astore_2
    //   2308: aload 39
    //   2310: astore 38
    //   2312: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   2315: bipush 21
    //   2317: if_icmpge +112 -> 2429
    //   2320: aload 37
    //   2322: astore 32
    //   2324: aload 28
    //   2326: astore 29
    //   2328: aload 31
    //   2330: astore 27
    //   2332: aload 30
    //   2334: astore_2
    //   2335: aload 37
    //   2337: invokevirtual 1371	android/media/MediaCodec:getInputBuffers	()[Ljava/nio/ByteBuffer;
    //   2340: astore 51
    //   2342: aload 37
    //   2344: astore 32
    //   2346: aload 28
    //   2348: astore 29
    //   2350: aload 31
    //   2352: astore 27
    //   2354: aload 30
    //   2356: astore_2
    //   2357: aload 28
    //   2359: invokevirtual 1374	android/media/MediaCodec:getOutputBuffers	()[Ljava/nio/ByteBuffer;
    //   2362: astore 49
    //   2364: aload 37
    //   2366: astore 32
    //   2368: aload 28
    //   2370: astore 29
    //   2372: aload 31
    //   2374: astore 27
    //   2376: aload 30
    //   2378: astore_2
    //   2379: aload 51
    //   2381: astore 40
    //   2383: aload 39
    //   2385: astore 38
    //   2387: aload 49
    //   2389: astore 34
    //   2391: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   2394: bipush 18
    //   2396: if_icmpge +33 -> 2429
    //   2399: aload 37
    //   2401: astore 32
    //   2403: aload 28
    //   2405: astore 29
    //   2407: aload 31
    //   2409: astore 27
    //   2411: aload 30
    //   2413: astore_2
    //   2414: aload 28
    //   2416: invokevirtual 1371	android/media/MediaCodec:getInputBuffers	()[Ljava/nio/ByteBuffer;
    //   2419: astore 38
    //   2421: aload 49
    //   2423: astore 34
    //   2425: aload 51
    //   2427: astore 40
    //   2429: aload 37
    //   2431: astore 32
    //   2433: aload 28
    //   2435: astore 29
    //   2437: aload 31
    //   2439: astore 27
    //   2441: aload 30
    //   2443: astore_2
    //   2444: aload_0
    //   2445: invokespecial 1189	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   2448: iload 44
    //   2450: istore 19
    //   2452: aload 34
    //   2454: astore 39
    //   2456: aload 37
    //   2458: astore 32
    //   2460: aload 28
    //   2462: astore 29
    //   2464: iload 24
    //   2466: istore 15
    //   2468: aload 31
    //   2470: astore 27
    //   2472: aload 30
    //   2474: astore_2
    //   2475: iload 43
    //   2477: ifne -1523 -> 954
    //   2480: aload 37
    //   2482: astore 32
    //   2484: aload 28
    //   2486: astore 29
    //   2488: aload 31
    //   2490: astore 27
    //   2492: aload 30
    //   2494: astore_2
    //   2495: aload_0
    //   2496: invokespecial 1189	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   2499: iload 19
    //   2501: istore 20
    //   2503: iload 19
    //   2505: ifne +238 -> 2743
    //   2508: iconst_0
    //   2509: istore 20
    //   2511: aload 37
    //   2513: astore 32
    //   2515: aload 28
    //   2517: astore 29
    //   2519: aload 31
    //   2521: astore 27
    //   2523: aload 30
    //   2525: astore_2
    //   2526: aload 35
    //   2528: invokevirtual 1377	android/media/MediaExtractor:getSampleTrackIndex	()I
    //   2531: istore 13
    //   2533: iload 13
    //   2535: iload 36
    //   2537: if_icmpne +1015 -> 3552
    //   2540: aload 37
    //   2542: astore 32
    //   2544: aload 28
    //   2546: astore 29
    //   2548: aload 31
    //   2550: astore 27
    //   2552: aload 30
    //   2554: astore_2
    //   2555: aload 37
    //   2557: ldc2_w 1378
    //   2560: invokevirtual 1383	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   2563: istore 13
    //   2565: iload 20
    //   2567: istore 12
    //   2569: iload 19
    //   2571: istore 9
    //   2573: iload 13
    //   2575: iflt +96 -> 2671
    //   2578: aload 37
    //   2580: astore 32
    //   2582: aload 28
    //   2584: astore 29
    //   2586: aload 31
    //   2588: astore 27
    //   2590: aload 30
    //   2592: astore_2
    //   2593: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   2596: bipush 21
    //   2598: if_icmpge +864 -> 3462
    //   2601: aload 40
    //   2603: iload 13
    //   2605: aaload
    //   2606: astore 34
    //   2608: aload 37
    //   2610: astore 32
    //   2612: aload 28
    //   2614: astore 29
    //   2616: aload 31
    //   2618: astore 27
    //   2620: aload 30
    //   2622: astore_2
    //   2623: aload 35
    //   2625: aload 34
    //   2627: iconst_0
    //   2628: invokevirtual 1386	android/media/MediaExtractor:readSampleData	(Ljava/nio/ByteBuffer;I)I
    //   2631: istore 9
    //   2633: iload 9
    //   2635: ifge +854 -> 3489
    //   2638: aload 37
    //   2640: astore 32
    //   2642: aload 28
    //   2644: astore 29
    //   2646: aload 31
    //   2648: astore 27
    //   2650: aload 30
    //   2652: astore_2
    //   2653: aload 37
    //   2655: iload 13
    //   2657: iconst_0
    //   2658: iconst_0
    //   2659: lconst_0
    //   2660: iconst_4
    //   2661: invokevirtual 1390	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2664: iconst_1
    //   2665: istore 9
    //   2667: iload 20
    //   2669: istore 12
    //   2671: iload 9
    //   2673: istore 20
    //   2675: iload 12
    //   2677: ifeq +66 -> 2743
    //   2680: aload 37
    //   2682: astore 32
    //   2684: aload 28
    //   2686: astore 29
    //   2688: aload 31
    //   2690: astore 27
    //   2692: aload 30
    //   2694: astore_2
    //   2695: aload 37
    //   2697: ldc2_w 1378
    //   2700: invokevirtual 1383	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   2703: istore 19
    //   2705: iload 9
    //   2707: istore 20
    //   2709: iload 19
    //   2711: iflt +32 -> 2743
    //   2714: aload 37
    //   2716: astore 32
    //   2718: aload 28
    //   2720: astore 29
    //   2722: aload 31
    //   2724: astore 27
    //   2726: aload 30
    //   2728: astore_2
    //   2729: aload 37
    //   2731: iload 19
    //   2733: iconst_0
    //   2734: iconst_0
    //   2735: lconst_0
    //   2736: iconst_4
    //   2737: invokevirtual 1390	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2740: iconst_1
    //   2741: istore 20
    //   2743: iload 45
    //   2745: ifne +1230 -> 3975
    //   2748: iconst_1
    //   2749: istore 19
    //   2751: iconst_1
    //   2752: istore 44
    //   2754: iload 46
    //   2756: istore 12
    //   2758: lload 41
    //   2760: lstore 52
    //   2762: iload 43
    //   2764: istore 50
    //   2766: iload 20
    //   2768: istore 9
    //   2770: aload 39
    //   2772: astore 34
    //   2774: iload 19
    //   2776: istore 20
    //   2778: iload 45
    //   2780: istore 13
    //   2782: iload 20
    //   2784: ifne +32 -> 2816
    //   2787: iload 13
    //   2789: istore 45
    //   2791: aload 34
    //   2793: astore 39
    //   2795: iload 9
    //   2797: istore 19
    //   2799: iload 50
    //   2801: istore 43
    //   2803: lload 52
    //   2805: lstore 41
    //   2807: iload 12
    //   2809: istore 46
    //   2811: iload 44
    //   2813: ifeq -357 -> 2456
    //   2816: aload 37
    //   2818: astore 32
    //   2820: aload 28
    //   2822: astore 29
    //   2824: aload 31
    //   2826: astore 27
    //   2828: aload 30
    //   2830: astore_2
    //   2831: aload_0
    //   2832: invokespecial 1189	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   2835: aload 37
    //   2837: astore 32
    //   2839: aload 28
    //   2841: astore 29
    //   2843: aload 31
    //   2845: astore 27
    //   2847: aload 30
    //   2849: astore_2
    //   2850: aload 28
    //   2852: aload 33
    //   2854: ldc2_w 1378
    //   2857: invokevirtual 1394	android/media/MediaCodec:dequeueOutputBuffer	(Landroid/media/MediaCodec$BufferInfo;J)I
    //   2860: istore 45
    //   2862: iload 45
    //   2864: iconst_m1
    //   2865: if_icmpne +1116 -> 3981
    //   2868: iconst_0
    //   2869: istore 46
    //   2871: iload 12
    //   2873: istore 19
    //   2875: iload 50
    //   2877: istore 43
    //   2879: aload 34
    //   2881: astore 39
    //   2883: iload 46
    //   2885: istore 44
    //   2887: aload 39
    //   2889: astore 34
    //   2891: iload 43
    //   2893: istore 50
    //   2895: iload 19
    //   2897: istore 12
    //   2899: iload 45
    //   2901: iconst_m1
    //   2902: if_icmpne -120 -> 2782
    //   2905: iload 46
    //   2907: istore 44
    //   2909: aload 39
    //   2911: astore 34
    //   2913: iload 43
    //   2915: istore 50
    //   2917: iload 19
    //   2919: istore 12
    //   2921: iload 13
    //   2923: ifne -141 -> 2782
    //   2926: aload 37
    //   2928: astore 32
    //   2930: aload 28
    //   2932: astore 29
    //   2934: aload 31
    //   2936: astore 27
    //   2938: aload 30
    //   2940: astore_2
    //   2941: aload 37
    //   2943: aload 33
    //   2945: ldc2_w 1378
    //   2948: invokevirtual 1394	android/media/MediaCodec:dequeueOutputBuffer	(Landroid/media/MediaCodec$BufferInfo;J)I
    //   2951: istore 54
    //   2953: iload 54
    //   2955: iconst_m1
    //   2956: if_icmpne +2147 -> 5103
    //   2959: iconst_0
    //   2960: istore 20
    //   2962: iload 46
    //   2964: istore 44
    //   2966: aload 39
    //   2968: astore 34
    //   2970: iload 43
    //   2972: istore 50
    //   2974: iload 19
    //   2976: istore 12
    //   2978: goto -196 -> 2782
    //   2981: aload 38
    //   2983: astore 32
    //   2985: aload 31
    //   2987: astore 29
    //   2989: aload 40
    //   2991: astore 27
    //   2993: aload 30
    //   2995: astore_2
    //   2996: aload 49
    //   2998: ldc_w 1396
    //   3001: invokevirtual 1271	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   3004: ifeq +13 -> 3017
    //   3007: iconst_2
    //   3008: istore 19
    //   3010: iload 9
    //   3012: istore 8
    //   3014: goto -1754 -> 1260
    //   3017: aload 38
    //   3019: astore 32
    //   3021: aload 31
    //   3023: astore 29
    //   3025: aload 40
    //   3027: astore 27
    //   3029: aload 30
    //   3031: astore_2
    //   3032: aload 49
    //   3034: ldc_w 1398
    //   3037: invokevirtual 1276	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3040: ifeq +13 -> 3053
    //   3043: iconst_3
    //   3044: istore 19
    //   3046: iload 9
    //   3048: istore 8
    //   3050: goto -1790 -> 1260
    //   3053: aload 38
    //   3055: astore 32
    //   3057: aload 31
    //   3059: astore 29
    //   3061: aload 40
    //   3063: astore 27
    //   3065: aload 30
    //   3067: astore_2
    //   3068: aload 49
    //   3070: ldc_w 1400
    //   3073: invokevirtual 1276	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3076: ifeq +12 -> 3088
    //   3079: iconst_4
    //   3080: istore 19
    //   3082: iconst_1
    //   3083: istore 8
    //   3085: goto -1825 -> 1260
    //   3088: aload 38
    //   3090: astore 32
    //   3092: aload 31
    //   3094: astore 29
    //   3096: aload 40
    //   3098: astore 27
    //   3100: aload 30
    //   3102: astore_2
    //   3103: iload 8
    //   3105: istore 19
    //   3107: iload 9
    //   3109: istore 8
    //   3111: aload 49
    //   3113: ldc_w 1402
    //   3116: invokevirtual 1276	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3119: ifeq -1859 -> 1260
    //   3122: iconst_5
    //   3123: istore 19
    //   3125: iload 9
    //   3127: istore 8
    //   3129: goto -1869 -> 1260
    //   3132: ldc_w 1403
    //   3135: istore 10
    //   3137: iload 19
    //   3139: istore 9
    //   3141: goto -1736 -> 1405
    //   3144: iload 9
    //   3146: iconst_1
    //   3147: if_icmpne +71 -> 3218
    //   3150: aload 38
    //   3152: astore 32
    //   3154: aload 31
    //   3156: astore 29
    //   3158: aload 40
    //   3160: astore 27
    //   3162: aload 30
    //   3164: astore_2
    //   3165: iload 19
    //   3167: istore 8
    //   3169: iload 50
    //   3171: istore 7
    //   3173: aload 28
    //   3175: invokevirtual 1207	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   3178: ldc_w 1273
    //   3181: invokevirtual 1276	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3184: ifne -1589 -> 1595
    //   3187: iload 18
    //   3189: iload 17
    //   3191: imul
    //   3192: sipush 2047
    //   3195: iadd
    //   3196: sipush 63488
    //   3199: iand
    //   3200: iload 18
    //   3202: iload 17
    //   3204: imul
    //   3205: isub
    //   3206: istore 7
    //   3208: iload 19
    //   3210: iload 7
    //   3212: iadd
    //   3213: istore 8
    //   3215: goto -1620 -> 1595
    //   3218: iload 19
    //   3220: istore 8
    //   3222: iload 50
    //   3224: istore 7
    //   3226: iload 9
    //   3228: iconst_5
    //   3229: if_icmpeq -1634 -> 1595
    //   3232: iload 19
    //   3234: istore 8
    //   3236: iload 50
    //   3238: istore 7
    //   3240: iload 9
    //   3242: iconst_3
    //   3243: if_icmpne -1648 -> 1595
    //   3246: aload 38
    //   3248: astore 32
    //   3250: aload 31
    //   3252: astore 29
    //   3254: aload 40
    //   3256: astore 27
    //   3258: aload 30
    //   3260: astore_2
    //   3261: iload 19
    //   3263: istore 8
    //   3265: iload 50
    //   3267: istore 7
    //   3269: aload 28
    //   3271: ldc_w 1405
    //   3274: invokevirtual 1276	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3277: ifeq -1682 -> 1595
    //   3280: iload 18
    //   3282: iload 17
    //   3284: bipush 16
    //   3286: iload 17
    //   3288: bipush 16
    //   3290: irem
    //   3291: isub
    //   3292: iadd
    //   3293: iload 17
    //   3295: isub
    //   3296: imul
    //   3297: istore 7
    //   3299: aload 38
    //   3301: astore 32
    //   3303: aload 31
    //   3305: astore 29
    //   3307: aload 40
    //   3309: astore 27
    //   3311: aload 30
    //   3313: astore_2
    //   3314: iload 19
    //   3316: iload 7
    //   3318: iconst_5
    //   3319: imul
    //   3320: iconst_4
    //   3321: idiv
    //   3322: iadd
    //   3323: istore 8
    //   3325: goto -1730 -> 1595
    //   3328: aload 38
    //   3330: astore 32
    //   3332: aload 31
    //   3334: astore 29
    //   3336: aload 40
    //   3338: astore 27
    //   3340: aload 30
    //   3342: astore_2
    //   3343: aload 35
    //   3345: lconst_0
    //   3346: iconst_0
    //   3347: invokevirtual 1314	android/media/MediaExtractor:seekTo	(JI)V
    //   3350: goto -1574 -> 1776
    //   3353: astore_1
    //   3354: aload 35
    //   3356: astore 30
    //   3358: aload 30
    //   3360: ifnull +8 -> 3368
    //   3363: aload 30
    //   3365: invokevirtual 1235	android/media/MediaExtractor:release	()V
    //   3368: aload 22
    //   3370: ifnull +8 -> 3378
    //   3373: aload 22
    //   3375: invokevirtual 1238	org/telegram/messenger/video/MP4Builder:finishMovie	()V
    //   3378: getstatic 1243	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   3381: ifeq +31 -> 3412
    //   3384: new 1245	java/lang/StringBuilder
    //   3387: dup
    //   3388: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   3391: ldc_w 1248
    //   3394: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3397: invokestatic 1157	java/lang/System:currentTimeMillis	()J
    //   3400: lload 25
    //   3402: lsub
    //   3403: invokevirtual 1255	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   3406: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3409: invokestatic 1261	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   3412: aload_1
    //   3413: athrow
    //   3414: ldc_w 1406
    //   3417: istore 19
    //   3419: goto -1583 -> 1836
    //   3422: bipush 25
    //   3424: istore 19
    //   3426: goto -1556 -> 1870
    //   3429: aload 37
    //   3431: astore 32
    //   3433: aload 28
    //   3435: astore 29
    //   3437: aload 31
    //   3439: astore 27
    //   3441: aload 30
    //   3443: astore_2
    //   3444: new 1222	org/telegram/messenger/video/OutputSurface
    //   3447: dup
    //   3448: iload 18
    //   3450: iload 17
    //   3452: iload 20
    //   3454: invokespecial 1409	org/telegram/messenger/video/OutputSurface:<init>	(III)V
    //   3457: astore 30
    //   3459: goto -1224 -> 2235
    //   3462: aload 37
    //   3464: astore 32
    //   3466: aload 28
    //   3468: astore 29
    //   3470: aload 31
    //   3472: astore 27
    //   3474: aload 30
    //   3476: astore_2
    //   3477: aload 37
    //   3479: iload 13
    //   3481: invokevirtual 1412	android/media/MediaCodec:getInputBuffer	(I)Ljava/nio/ByteBuffer;
    //   3484: astore 34
    //   3486: goto -878 -> 2608
    //   3489: aload 37
    //   3491: astore 32
    //   3493: aload 28
    //   3495: astore 29
    //   3497: aload 31
    //   3499: astore 27
    //   3501: aload 30
    //   3503: astore_2
    //   3504: aload 37
    //   3506: iload 13
    //   3508: iconst_0
    //   3509: iload 9
    //   3511: aload 35
    //   3513: invokevirtual 1415	android/media/MediaExtractor:getSampleTime	()J
    //   3516: iconst_0
    //   3517: invokevirtual 1390	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   3520: aload 37
    //   3522: astore 32
    //   3524: aload 28
    //   3526: astore 29
    //   3528: aload 31
    //   3530: astore 27
    //   3532: aload 30
    //   3534: astore_2
    //   3535: aload 35
    //   3537: invokevirtual 1418	android/media/MediaExtractor:advance	()Z
    //   3540: pop
    //   3541: iload 20
    //   3543: istore 12
    //   3545: iload 19
    //   3547: istore 9
    //   3549: goto -878 -> 2671
    //   3552: iload 14
    //   3554: iconst_m1
    //   3555: if_icmpeq +396 -> 3951
    //   3558: iload 13
    //   3560: iload 14
    //   3562: if_icmpne +389 -> 3951
    //   3565: aload 37
    //   3567: astore 32
    //   3569: aload 28
    //   3571: astore 29
    //   3573: aload 31
    //   3575: astore 27
    //   3577: aload 30
    //   3579: astore_2
    //   3580: aload 33
    //   3582: aload 35
    //   3584: aload 48
    //   3586: iconst_0
    //   3587: invokevirtual 1386	android/media/MediaExtractor:readSampleData	(Ljava/nio/ByteBuffer;I)I
    //   3590: putfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   3593: aload 37
    //   3595: astore 32
    //   3597: aload 28
    //   3599: astore 29
    //   3601: aload 31
    //   3603: astore 27
    //   3605: aload 30
    //   3607: astore_2
    //   3608: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   3611: bipush 21
    //   3613: if_icmpge +51 -> 3664
    //   3616: aload 37
    //   3618: astore 32
    //   3620: aload 28
    //   3622: astore 29
    //   3624: aload 31
    //   3626: astore 27
    //   3628: aload 30
    //   3630: astore_2
    //   3631: aload 48
    //   3633: iconst_0
    //   3634: invokevirtual 1424	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   3637: pop
    //   3638: aload 37
    //   3640: astore 32
    //   3642: aload 28
    //   3644: astore 29
    //   3646: aload 31
    //   3648: astore 27
    //   3650: aload 30
    //   3652: astore_2
    //   3653: aload 48
    //   3655: aload 33
    //   3657: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   3660: invokevirtual 1427	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   3663: pop
    //   3664: aload 37
    //   3666: astore 32
    //   3668: aload 28
    //   3670: astore 29
    //   3672: aload 31
    //   3674: astore 27
    //   3676: aload 30
    //   3678: astore_2
    //   3679: aload 33
    //   3681: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   3684: iflt +240 -> 3924
    //   3687: aload 37
    //   3689: astore 32
    //   3691: aload 28
    //   3693: astore 29
    //   3695: aload 31
    //   3697: astore 27
    //   3699: aload 30
    //   3701: astore_2
    //   3702: aload 33
    //   3704: aload 35
    //   3706: invokevirtual 1415	android/media/MediaExtractor:getSampleTime	()J
    //   3709: putfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   3712: aload 37
    //   3714: astore 32
    //   3716: aload 28
    //   3718: astore 29
    //   3720: aload 31
    //   3722: astore 27
    //   3724: aload 30
    //   3726: astore_2
    //   3727: aload 35
    //   3729: invokevirtual 1418	android/media/MediaExtractor:advance	()Z
    //   3732: pop
    //   3733: aload 37
    //   3735: astore 32
    //   3737: aload 28
    //   3739: astore 29
    //   3741: aload 31
    //   3743: astore 27
    //   3745: aload 30
    //   3747: astore_2
    //   3748: iload 20
    //   3750: istore 12
    //   3752: iload 19
    //   3754: istore 9
    //   3756: aload 33
    //   3758: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   3761: ifle -1090 -> 2671
    //   3764: lload 5
    //   3766: lconst_0
    //   3767: lcmp
    //   3768: iflt +37 -> 3805
    //   3771: aload 37
    //   3773: astore 32
    //   3775: aload 28
    //   3777: astore 29
    //   3779: aload 31
    //   3781: astore 27
    //   3783: aload 30
    //   3785: astore_2
    //   3786: iload 20
    //   3788: istore 12
    //   3790: iload 19
    //   3792: istore 9
    //   3794: aload 33
    //   3796: getfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   3799: lload 5
    //   3801: lcmp
    //   3802: ifge -1131 -> 2671
    //   3805: aload 37
    //   3807: astore 32
    //   3809: aload 28
    //   3811: astore 29
    //   3813: aload 31
    //   3815: astore 27
    //   3817: aload 30
    //   3819: astore_2
    //   3820: aload 33
    //   3822: iconst_0
    //   3823: putfield 1433	android/media/MediaCodec$BufferInfo:offset	I
    //   3826: aload 37
    //   3828: astore 32
    //   3830: aload 28
    //   3832: astore 29
    //   3834: aload 31
    //   3836: astore 27
    //   3838: aload 30
    //   3840: astore_2
    //   3841: aload 33
    //   3843: aload 35
    //   3845: invokevirtual 1436	android/media/MediaExtractor:getSampleFlags	()I
    //   3848: putfield 1439	android/media/MediaCodec$BufferInfo:flags	I
    //   3851: aload 37
    //   3853: astore 32
    //   3855: aload 28
    //   3857: astore 29
    //   3859: aload 31
    //   3861: astore 27
    //   3863: aload 30
    //   3865: astore_2
    //   3866: iload 20
    //   3868: istore 12
    //   3870: iload 19
    //   3872: istore 9
    //   3874: aload 22
    //   3876: iload 47
    //   3878: aload 48
    //   3880: aload 33
    //   3882: iconst_0
    //   3883: invokevirtual 1443	org/telegram/messenger/video/MP4Builder:writeSampleData	(ILjava/nio/ByteBuffer;Landroid/media/MediaCodec$BufferInfo;Z)Z
    //   3886: ifeq -1215 -> 2671
    //   3889: aload 37
    //   3891: astore 32
    //   3893: aload 28
    //   3895: astore 29
    //   3897: aload 31
    //   3899: astore 27
    //   3901: aload 30
    //   3903: astore_2
    //   3904: aload_0
    //   3905: aload_1
    //   3906: aload 16
    //   3908: iconst_0
    //   3909: iconst_0
    //   3910: invokespecial 1152	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   3913: iload 20
    //   3915: istore 12
    //   3917: iload 19
    //   3919: istore 9
    //   3921: goto -1250 -> 2671
    //   3924: aload 37
    //   3926: astore 32
    //   3928: aload 28
    //   3930: astore 29
    //   3932: aload 31
    //   3934: astore 27
    //   3936: aload 30
    //   3938: astore_2
    //   3939: aload 33
    //   3941: iconst_0
    //   3942: putfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   3945: iconst_1
    //   3946: istore 19
    //   3948: goto -215 -> 3733
    //   3951: iload 20
    //   3953: istore 12
    //   3955: iload 19
    //   3957: istore 9
    //   3959: iload 13
    //   3961: iconst_m1
    //   3962: if_icmpne -1291 -> 2671
    //   3965: iconst_1
    //   3966: istore 12
    //   3968: iload 19
    //   3970: istore 9
    //   3972: goto -1301 -> 2671
    //   3975: iconst_0
    //   3976: istore 19
    //   3978: goto -1227 -> 2751
    //   3981: iload 45
    //   3983: bipush -3
    //   3985: if_icmpne +79 -> 4064
    //   3988: aload 37
    //   3990: astore 32
    //   3992: aload 28
    //   3994: astore 29
    //   3996: aload 31
    //   3998: astore 27
    //   4000: aload 30
    //   4002: astore_2
    //   4003: iload 44
    //   4005: istore 46
    //   4007: aload 34
    //   4009: astore 39
    //   4011: iload 50
    //   4013: istore 43
    //   4015: iload 12
    //   4017: istore 19
    //   4019: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   4022: bipush 21
    //   4024: if_icmpge -1141 -> 2883
    //   4027: aload 37
    //   4029: astore 32
    //   4031: aload 28
    //   4033: astore 29
    //   4035: aload 31
    //   4037: astore 27
    //   4039: aload 30
    //   4041: astore_2
    //   4042: aload 28
    //   4044: invokevirtual 1374	android/media/MediaCodec:getOutputBuffers	()[Ljava/nio/ByteBuffer;
    //   4047: astore 39
    //   4049: iload 44
    //   4051: istore 46
    //   4053: iload 50
    //   4055: istore 43
    //   4057: iload 12
    //   4059: istore 19
    //   4061: goto -1178 -> 2883
    //   4064: iload 45
    //   4066: bipush -2
    //   4068: if_icmpne +88 -> 4156
    //   4071: aload 37
    //   4073: astore 32
    //   4075: aload 28
    //   4077: astore 29
    //   4079: aload 31
    //   4081: astore 27
    //   4083: aload 30
    //   4085: astore_2
    //   4086: aload 28
    //   4088: invokevirtual 1447	android/media/MediaCodec:getOutputFormat	()Landroid/media/MediaFormat;
    //   4091: astore 49
    //   4093: iload 44
    //   4095: istore 46
    //   4097: aload 34
    //   4099: astore 39
    //   4101: iload 50
    //   4103: istore 43
    //   4105: iload 12
    //   4107: istore 19
    //   4109: iload 12
    //   4111: bipush -5
    //   4113: if_icmpne -1230 -> 2883
    //   4116: aload 37
    //   4118: astore 32
    //   4120: aload 28
    //   4122: astore 29
    //   4124: aload 31
    //   4126: astore 27
    //   4128: aload 30
    //   4130: astore_2
    //   4131: aload 22
    //   4133: aload 49
    //   4135: iconst_0
    //   4136: invokevirtual 1310	org/telegram/messenger/video/MP4Builder:addTrack	(Landroid/media/MediaFormat;Z)I
    //   4139: istore 19
    //   4141: iload 44
    //   4143: istore 46
    //   4145: aload 34
    //   4147: astore 39
    //   4149: iload 50
    //   4151: istore 43
    //   4153: goto -1270 -> 2883
    //   4156: iload 45
    //   4158: ifge +117 -> 4275
    //   4161: aload 37
    //   4163: astore 32
    //   4165: aload 28
    //   4167: astore 29
    //   4169: aload 31
    //   4171: astore 27
    //   4173: aload 30
    //   4175: astore_2
    //   4176: new 940	java/lang/RuntimeException
    //   4179: astore 34
    //   4181: aload 37
    //   4183: astore 32
    //   4185: aload 28
    //   4187: astore 29
    //   4189: aload 31
    //   4191: astore 27
    //   4193: aload 30
    //   4195: astore_2
    //   4196: new 1245	java/lang/StringBuilder
    //   4199: astore 48
    //   4201: aload 37
    //   4203: astore 32
    //   4205: aload 28
    //   4207: astore 29
    //   4209: aload 31
    //   4211: astore 27
    //   4213: aload 30
    //   4215: astore_2
    //   4216: aload 48
    //   4218: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   4221: aload 37
    //   4223: astore 32
    //   4225: aload 28
    //   4227: astore 29
    //   4229: aload 31
    //   4231: astore 27
    //   4233: aload 30
    //   4235: astore_2
    //   4236: aload 34
    //   4238: aload 48
    //   4240: ldc_w 1449
    //   4243: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4246: iload 45
    //   4248: invokevirtual 1292	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4251: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4254: invokespecial 943	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   4257: aload 37
    //   4259: astore 32
    //   4261: aload 28
    //   4263: astore 29
    //   4265: aload 31
    //   4267: astore 27
    //   4269: aload 30
    //   4271: astore_2
    //   4272: aload 34
    //   4274: athrow
    //   4275: aload 37
    //   4277: astore 32
    //   4279: aload 28
    //   4281: astore 29
    //   4283: aload 31
    //   4285: astore 27
    //   4287: aload 30
    //   4289: astore_2
    //   4290: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   4293: bipush 21
    //   4295: if_icmpge +135 -> 4430
    //   4298: aload 34
    //   4300: iload 45
    //   4302: aaload
    //   4303: astore 39
    //   4305: aload 39
    //   4307: ifnonnull +150 -> 4457
    //   4310: aload 37
    //   4312: astore 32
    //   4314: aload 28
    //   4316: astore 29
    //   4318: aload 31
    //   4320: astore 27
    //   4322: aload 30
    //   4324: astore_2
    //   4325: new 940	java/lang/RuntimeException
    //   4328: astore 34
    //   4330: aload 37
    //   4332: astore 32
    //   4334: aload 28
    //   4336: astore 29
    //   4338: aload 31
    //   4340: astore 27
    //   4342: aload 30
    //   4344: astore_2
    //   4345: new 1245	java/lang/StringBuilder
    //   4348: astore 48
    //   4350: aload 37
    //   4352: astore 32
    //   4354: aload 28
    //   4356: astore 29
    //   4358: aload 31
    //   4360: astore 27
    //   4362: aload 30
    //   4364: astore_2
    //   4365: aload 48
    //   4367: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   4370: aload 37
    //   4372: astore 32
    //   4374: aload 28
    //   4376: astore 29
    //   4378: aload 31
    //   4380: astore 27
    //   4382: aload 30
    //   4384: astore_2
    //   4385: aload 34
    //   4387: aload 48
    //   4389: ldc_w 1451
    //   4392: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4395: iload 45
    //   4397: invokevirtual 1292	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4400: ldc_w 1453
    //   4403: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4406: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4409: invokespecial 943	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   4412: aload 37
    //   4414: astore 32
    //   4416: aload 28
    //   4418: astore 29
    //   4420: aload 31
    //   4422: astore 27
    //   4424: aload 30
    //   4426: astore_2
    //   4427: aload 34
    //   4429: athrow
    //   4430: aload 37
    //   4432: astore 32
    //   4434: aload 28
    //   4436: astore 29
    //   4438: aload 31
    //   4440: astore 27
    //   4442: aload 30
    //   4444: astore_2
    //   4445: aload 28
    //   4447: iload 45
    //   4449: invokevirtual 1456	android/media/MediaCodec:getOutputBuffer	(I)Ljava/nio/ByteBuffer;
    //   4452: astore 39
    //   4454: goto -149 -> 4305
    //   4457: aload 37
    //   4459: astore 32
    //   4461: aload 28
    //   4463: astore 29
    //   4465: aload 31
    //   4467: astore 27
    //   4469: aload 30
    //   4471: astore_2
    //   4472: iload 12
    //   4474: istore 19
    //   4476: aload 33
    //   4478: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   4481: iconst_1
    //   4482: if_icmple +90 -> 4572
    //   4485: aload 37
    //   4487: astore 32
    //   4489: aload 28
    //   4491: astore 29
    //   4493: aload 31
    //   4495: astore 27
    //   4497: aload 30
    //   4499: astore_2
    //   4500: aload 33
    //   4502: getfield 1439	android/media/MediaCodec$BufferInfo:flags	I
    //   4505: iconst_2
    //   4506: iand
    //   4507: ifne +131 -> 4638
    //   4510: aload 37
    //   4512: astore 32
    //   4514: aload 28
    //   4516: astore 29
    //   4518: aload 31
    //   4520: astore 27
    //   4522: aload 30
    //   4524: astore_2
    //   4525: iload 12
    //   4527: istore 19
    //   4529: aload 22
    //   4531: iload 12
    //   4533: aload 39
    //   4535: aload 33
    //   4537: iconst_1
    //   4538: invokevirtual 1443	org/telegram/messenger/video/MP4Builder:writeSampleData	(ILjava/nio/ByteBuffer;Landroid/media/MediaCodec$BufferInfo;Z)Z
    //   4541: ifeq +31 -> 4572
    //   4544: aload 37
    //   4546: astore 32
    //   4548: aload 28
    //   4550: astore 29
    //   4552: aload 31
    //   4554: astore 27
    //   4556: aload 30
    //   4558: astore_2
    //   4559: aload_0
    //   4560: aload_1
    //   4561: aload 16
    //   4563: iconst_0
    //   4564: iconst_0
    //   4565: invokespecial 1152	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   4568: iload 12
    //   4570: istore 19
    //   4572: aload 37
    //   4574: astore 32
    //   4576: aload 28
    //   4578: astore 29
    //   4580: aload 31
    //   4582: astore 27
    //   4584: aload 30
    //   4586: astore_2
    //   4587: aload 33
    //   4589: getfield 1439	android/media/MediaCodec$BufferInfo:flags	I
    //   4592: iconst_4
    //   4593: iand
    //   4594: ifeq +503 -> 5097
    //   4597: iconst_1
    //   4598: istore 12
    //   4600: aload 37
    //   4602: astore 32
    //   4604: aload 28
    //   4606: astore 29
    //   4608: aload 31
    //   4610: astore 27
    //   4612: aload 30
    //   4614: astore_2
    //   4615: aload 28
    //   4617: iload 45
    //   4619: iconst_0
    //   4620: invokevirtual 1460	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   4623: iload 44
    //   4625: istore 46
    //   4627: aload 34
    //   4629: astore 39
    //   4631: iload 12
    //   4633: istore 43
    //   4635: goto -1752 -> 2883
    //   4638: iload 12
    //   4640: istore 19
    //   4642: iload 12
    //   4644: bipush -5
    //   4646: if_icmpne -74 -> 4572
    //   4649: aload 37
    //   4651: astore 32
    //   4653: aload 28
    //   4655: astore 29
    //   4657: aload 31
    //   4659: astore 27
    //   4661: aload 30
    //   4663: astore_2
    //   4664: aload 33
    //   4666: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   4669: newarray <illegal type>
    //   4671: astore 55
    //   4673: aload 37
    //   4675: astore 32
    //   4677: aload 28
    //   4679: astore 29
    //   4681: aload 31
    //   4683: astore 27
    //   4685: aload 30
    //   4687: astore_2
    //   4688: aload 39
    //   4690: aload 33
    //   4692: getfield 1433	android/media/MediaCodec$BufferInfo:offset	I
    //   4695: aload 33
    //   4697: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   4700: iadd
    //   4701: invokevirtual 1427	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   4704: pop
    //   4705: aload 37
    //   4707: astore 32
    //   4709: aload 28
    //   4711: astore 29
    //   4713: aload 31
    //   4715: astore 27
    //   4717: aload 30
    //   4719: astore_2
    //   4720: aload 39
    //   4722: aload 33
    //   4724: getfield 1433	android/media/MediaCodec$BufferInfo:offset	I
    //   4727: invokevirtual 1424	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   4730: pop
    //   4731: aload 37
    //   4733: astore 32
    //   4735: aload 28
    //   4737: astore 29
    //   4739: aload 31
    //   4741: astore 27
    //   4743: aload 30
    //   4745: astore_2
    //   4746: aload 39
    //   4748: aload 55
    //   4750: invokevirtual 1463	java/nio/ByteBuffer:get	([B)Ljava/nio/ByteBuffer;
    //   4753: pop
    //   4754: aconst_null
    //   4755: astore 56
    //   4757: aconst_null
    //   4758: astore 51
    //   4760: aload 37
    //   4762: astore 32
    //   4764: aload 28
    //   4766: astore 29
    //   4768: aload 31
    //   4770: astore 27
    //   4772: aload 30
    //   4774: astore_2
    //   4775: aload 33
    //   4777: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   4780: iconst_1
    //   4781: isub
    //   4782: istore 19
    //   4784: aload 51
    //   4786: astore 49
    //   4788: aload 56
    //   4790: astore 39
    //   4792: iload 19
    //   4794: iflt +183 -> 4977
    //   4797: aload 51
    //   4799: astore 49
    //   4801: aload 56
    //   4803: astore 39
    //   4805: iload 19
    //   4807: iconst_3
    //   4808: if_icmple +169 -> 4977
    //   4811: aload 55
    //   4813: iload 19
    //   4815: baload
    //   4816: iconst_1
    //   4817: if_icmpne +274 -> 5091
    //   4820: aload 55
    //   4822: iload 19
    //   4824: iconst_1
    //   4825: isub
    //   4826: baload
    //   4827: ifne +264 -> 5091
    //   4830: aload 55
    //   4832: iload 19
    //   4834: iconst_2
    //   4835: isub
    //   4836: baload
    //   4837: ifne +254 -> 5091
    //   4840: aload 55
    //   4842: iload 19
    //   4844: iconst_3
    //   4845: isub
    //   4846: baload
    //   4847: ifne +244 -> 5091
    //   4850: aload 37
    //   4852: astore 32
    //   4854: aload 28
    //   4856: astore 29
    //   4858: aload 31
    //   4860: astore 27
    //   4862: aload 30
    //   4864: astore_2
    //   4865: iload 19
    //   4867: iconst_3
    //   4868: isub
    //   4869: invokestatic 1466	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   4872: astore 39
    //   4874: aload 37
    //   4876: astore 32
    //   4878: aload 28
    //   4880: astore 29
    //   4882: aload 31
    //   4884: astore 27
    //   4886: aload 30
    //   4888: astore_2
    //   4889: aload 33
    //   4891: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   4894: iload 19
    //   4896: iconst_3
    //   4897: isub
    //   4898: isub
    //   4899: invokestatic 1466	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   4902: astore 49
    //   4904: aload 37
    //   4906: astore 32
    //   4908: aload 28
    //   4910: astore 29
    //   4912: aload 31
    //   4914: astore 27
    //   4916: aload 30
    //   4918: astore_2
    //   4919: aload 39
    //   4921: aload 55
    //   4923: iconst_0
    //   4924: iload 19
    //   4926: iconst_3
    //   4927: isub
    //   4928: invokevirtual 1470	java/nio/ByteBuffer:put	([BII)Ljava/nio/ByteBuffer;
    //   4931: iconst_0
    //   4932: invokevirtual 1424	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   4935: pop
    //   4936: aload 37
    //   4938: astore 32
    //   4940: aload 28
    //   4942: astore 29
    //   4944: aload 31
    //   4946: astore 27
    //   4948: aload 30
    //   4950: astore_2
    //   4951: aload 49
    //   4953: aload 55
    //   4955: iload 19
    //   4957: iconst_3
    //   4958: isub
    //   4959: aload 33
    //   4961: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   4964: iload 19
    //   4966: iconst_3
    //   4967: isub
    //   4968: isub
    //   4969: invokevirtual 1470	java/nio/ByteBuffer:put	([BII)Ljava/nio/ByteBuffer;
    //   4972: iconst_0
    //   4973: invokevirtual 1424	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   4976: pop
    //   4977: aload 37
    //   4979: astore 32
    //   4981: aload 28
    //   4983: astore 29
    //   4985: aload 31
    //   4987: astore 27
    //   4989: aload 30
    //   4991: astore_2
    //   4992: ldc -88
    //   4994: iload 18
    //   4996: iload 17
    //   4998: invokestatic 1318	android/media/MediaFormat:createVideoFormat	(Ljava/lang/String;II)Landroid/media/MediaFormat;
    //   5001: astore 51
    //   5003: aload 39
    //   5005: ifnull +58 -> 5063
    //   5008: aload 49
    //   5010: ifnull +53 -> 5063
    //   5013: aload 37
    //   5015: astore 32
    //   5017: aload 28
    //   5019: astore 29
    //   5021: aload 31
    //   5023: astore 27
    //   5025: aload 30
    //   5027: astore_2
    //   5028: aload 51
    //   5030: ldc_w 1472
    //   5033: aload 39
    //   5035: invokevirtual 1476	android/media/MediaFormat:setByteBuffer	(Ljava/lang/String;Ljava/nio/ByteBuffer;)V
    //   5038: aload 37
    //   5040: astore 32
    //   5042: aload 28
    //   5044: astore 29
    //   5046: aload 31
    //   5048: astore 27
    //   5050: aload 30
    //   5052: astore_2
    //   5053: aload 51
    //   5055: ldc_w 1478
    //   5058: aload 49
    //   5060: invokevirtual 1476	android/media/MediaFormat:setByteBuffer	(Ljava/lang/String;Ljava/nio/ByteBuffer;)V
    //   5063: aload 37
    //   5065: astore 32
    //   5067: aload 28
    //   5069: astore 29
    //   5071: aload 31
    //   5073: astore 27
    //   5075: aload 30
    //   5077: astore_2
    //   5078: aload 22
    //   5080: aload 51
    //   5082: iconst_0
    //   5083: invokevirtual 1310	org/telegram/messenger/video/MP4Builder:addTrack	(Landroid/media/MediaFormat;Z)I
    //   5086: istore 19
    //   5088: goto -516 -> 4572
    //   5091: iinc 19 -1
    //   5094: goto -310 -> 4784
    //   5097: iconst_0
    //   5098: istore 12
    //   5100: goto -500 -> 4600
    //   5103: iload 46
    //   5105: istore 44
    //   5107: aload 39
    //   5109: astore 34
    //   5111: iload 43
    //   5113: istore 50
    //   5115: iload 19
    //   5117: istore 12
    //   5119: iload 54
    //   5121: bipush -3
    //   5123: if_icmpeq -2341 -> 2782
    //   5126: iload 54
    //   5128: bipush -2
    //   5130: if_icmpne +155 -> 5285
    //   5133: aload 37
    //   5135: astore 32
    //   5137: aload 28
    //   5139: astore 29
    //   5141: aload 31
    //   5143: astore 27
    //   5145: aload 30
    //   5147: astore_2
    //   5148: aload 37
    //   5150: invokevirtual 1447	android/media/MediaCodec:getOutputFormat	()Landroid/media/MediaFormat;
    //   5153: astore 49
    //   5155: aload 37
    //   5157: astore 32
    //   5159: aload 28
    //   5161: astore 29
    //   5163: aload 31
    //   5165: astore 27
    //   5167: aload 30
    //   5169: astore_2
    //   5170: iload 46
    //   5172: istore 44
    //   5174: aload 39
    //   5176: astore 34
    //   5178: iload 43
    //   5180: istore 50
    //   5182: iload 19
    //   5184: istore 12
    //   5186: getstatic 1243	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   5189: ifeq -2407 -> 2782
    //   5192: aload 37
    //   5194: astore 32
    //   5196: aload 28
    //   5198: astore 29
    //   5200: aload 31
    //   5202: astore 27
    //   5204: aload 30
    //   5206: astore_2
    //   5207: new 1245	java/lang/StringBuilder
    //   5210: astore 34
    //   5212: aload 37
    //   5214: astore 32
    //   5216: aload 28
    //   5218: astore 29
    //   5220: aload 31
    //   5222: astore 27
    //   5224: aload 30
    //   5226: astore_2
    //   5227: aload 34
    //   5229: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   5232: aload 37
    //   5234: astore 32
    //   5236: aload 28
    //   5238: astore 29
    //   5240: aload 31
    //   5242: astore 27
    //   5244: aload 30
    //   5246: astore_2
    //   5247: aload 34
    //   5249: ldc_w 1480
    //   5252: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5255: aload 49
    //   5257: invokevirtual 1483	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5260: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5263: invokestatic 1261	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   5266: iload 46
    //   5268: istore 44
    //   5270: aload 39
    //   5272: astore 34
    //   5274: iload 43
    //   5276: istore 50
    //   5278: iload 19
    //   5280: istore 12
    //   5282: goto -2500 -> 2782
    //   5285: iload 54
    //   5287: ifge +117 -> 5404
    //   5290: aload 37
    //   5292: astore 32
    //   5294: aload 28
    //   5296: astore 29
    //   5298: aload 31
    //   5300: astore 27
    //   5302: aload 30
    //   5304: astore_2
    //   5305: new 940	java/lang/RuntimeException
    //   5308: astore 34
    //   5310: aload 37
    //   5312: astore 32
    //   5314: aload 28
    //   5316: astore 29
    //   5318: aload 31
    //   5320: astore 27
    //   5322: aload 30
    //   5324: astore_2
    //   5325: new 1245	java/lang/StringBuilder
    //   5328: astore 48
    //   5330: aload 37
    //   5332: astore 32
    //   5334: aload 28
    //   5336: astore 29
    //   5338: aload 31
    //   5340: astore 27
    //   5342: aload 30
    //   5344: astore_2
    //   5345: aload 48
    //   5347: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   5350: aload 37
    //   5352: astore 32
    //   5354: aload 28
    //   5356: astore 29
    //   5358: aload 31
    //   5360: astore 27
    //   5362: aload 30
    //   5364: astore_2
    //   5365: aload 34
    //   5367: aload 48
    //   5369: ldc_w 1485
    //   5372: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5375: iload 54
    //   5377: invokevirtual 1292	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   5380: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5383: invokespecial 943	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   5386: aload 37
    //   5388: astore 32
    //   5390: aload 28
    //   5392: astore 29
    //   5394: aload 31
    //   5396: astore 27
    //   5398: aload 30
    //   5400: astore_2
    //   5401: aload 34
    //   5403: athrow
    //   5404: aload 37
    //   5406: astore 32
    //   5408: aload 28
    //   5410: astore 29
    //   5412: aload 31
    //   5414: astore 27
    //   5416: aload 30
    //   5418: astore_2
    //   5419: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   5422: bipush 18
    //   5424: if_icmplt +623 -> 6047
    //   5427: aload 37
    //   5429: astore 32
    //   5431: aload 28
    //   5433: astore 29
    //   5435: aload 31
    //   5437: astore 27
    //   5439: aload 30
    //   5441: astore_2
    //   5442: aload 33
    //   5444: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   5447: ifeq +594 -> 6041
    //   5450: iconst_1
    //   5451: istore 15
    //   5453: iload 13
    //   5455: istore 57
    //   5457: iload 15
    //   5459: istore 23
    //   5461: iload 9
    //   5463: istore 45
    //   5465: lload 5
    //   5467: lconst_0
    //   5468: lcmp
    //   5469: ifle +77 -> 5546
    //   5472: aload 37
    //   5474: astore 32
    //   5476: aload 28
    //   5478: astore 29
    //   5480: aload 31
    //   5482: astore 27
    //   5484: aload 30
    //   5486: astore_2
    //   5487: iload 13
    //   5489: istore 57
    //   5491: iload 15
    //   5493: istore 23
    //   5495: iload 9
    //   5497: istore 45
    //   5499: aload 33
    //   5501: getfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   5504: lload 5
    //   5506: lcmp
    //   5507: iflt +39 -> 5546
    //   5510: iconst_1
    //   5511: istore 45
    //   5513: iconst_1
    //   5514: istore 57
    //   5516: iconst_0
    //   5517: istore 23
    //   5519: aload 37
    //   5521: astore 32
    //   5523: aload 28
    //   5525: astore 29
    //   5527: aload 31
    //   5529: astore 27
    //   5531: aload 30
    //   5533: astore_2
    //   5534: aload 33
    //   5536: aload 33
    //   5538: getfield 1439	android/media/MediaCodec$BufferInfo:flags	I
    //   5541: iconst_4
    //   5542: ior
    //   5543: putfield 1439	android/media/MediaCodec$BufferInfo:flags	I
    //   5546: iload 23
    //   5548: istore 15
    //   5550: lload 52
    //   5552: lstore 41
    //   5554: lload_3
    //   5555: lconst_0
    //   5556: lcmp
    //   5557: ifle +172 -> 5729
    //   5560: iload 23
    //   5562: istore 15
    //   5564: lload 52
    //   5566: lstore 41
    //   5568: lload 52
    //   5570: ldc2_w 1197
    //   5573: lcmp
    //   5574: ifne +155 -> 5729
    //   5577: aload 37
    //   5579: astore 32
    //   5581: aload 28
    //   5583: astore 29
    //   5585: aload 31
    //   5587: astore 27
    //   5589: aload 30
    //   5591: astore_2
    //   5592: aload 33
    //   5594: getfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   5597: lload_3
    //   5598: lcmp
    //   5599: ifge +508 -> 6107
    //   5602: iconst_0
    //   5603: istore 23
    //   5605: aload 37
    //   5607: astore 32
    //   5609: aload 28
    //   5611: astore 29
    //   5613: aload 31
    //   5615: astore 27
    //   5617: aload 30
    //   5619: astore_2
    //   5620: iload 23
    //   5622: istore 15
    //   5624: lload 52
    //   5626: lstore 41
    //   5628: getstatic 1243	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   5631: ifeq +98 -> 5729
    //   5634: aload 37
    //   5636: astore 32
    //   5638: aload 28
    //   5640: astore 29
    //   5642: aload 31
    //   5644: astore 27
    //   5646: aload 30
    //   5648: astore_2
    //   5649: new 1245	java/lang/StringBuilder
    //   5652: astore 34
    //   5654: aload 37
    //   5656: astore 32
    //   5658: aload 28
    //   5660: astore 29
    //   5662: aload 31
    //   5664: astore 27
    //   5666: aload 30
    //   5668: astore_2
    //   5669: aload 34
    //   5671: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   5674: aload 37
    //   5676: astore 32
    //   5678: aload 28
    //   5680: astore 29
    //   5682: aload 31
    //   5684: astore 27
    //   5686: aload 30
    //   5688: astore_2
    //   5689: aload 34
    //   5691: ldc_w 1487
    //   5694: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5697: lload_3
    //   5698: invokevirtual 1255	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   5701: ldc_w 1489
    //   5704: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5707: aload 33
    //   5709: getfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   5712: invokevirtual 1255	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   5715: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5718: invokestatic 1261	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   5721: lload 52
    //   5723: lstore 41
    //   5725: iload 23
    //   5727: istore 15
    //   5729: aload 37
    //   5731: astore 32
    //   5733: aload 28
    //   5735: astore 29
    //   5737: aload 31
    //   5739: astore 27
    //   5741: aload 30
    //   5743: astore_2
    //   5744: aload 37
    //   5746: iload 54
    //   5748: iload 15
    //   5750: invokevirtual 1460	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   5753: iload 15
    //   5755: ifeq +110 -> 5865
    //   5758: iconst_0
    //   5759: istore 9
    //   5761: aload 30
    //   5763: invokevirtual 1492	org/telegram/messenger/video/OutputSurface:awaitNewImage	()V
    //   5766: iload 9
    //   5768: ifne +97 -> 5865
    //   5771: aload 37
    //   5773: astore 32
    //   5775: aload 28
    //   5777: astore 29
    //   5779: aload 31
    //   5781: astore 27
    //   5783: aload 30
    //   5785: astore_2
    //   5786: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   5789: bipush 18
    //   5791: if_icmplt +373 -> 6164
    //   5794: aload 37
    //   5796: astore 32
    //   5798: aload 28
    //   5800: astore 29
    //   5802: aload 31
    //   5804: astore 27
    //   5806: aload 30
    //   5808: astore_2
    //   5809: aload 30
    //   5811: iconst_0
    //   5812: invokevirtual 1495	org/telegram/messenger/video/OutputSurface:drawImage	(Z)V
    //   5815: aload 37
    //   5817: astore 32
    //   5819: aload 28
    //   5821: astore 29
    //   5823: aload 31
    //   5825: astore 27
    //   5827: aload 30
    //   5829: astore_2
    //   5830: aload 31
    //   5832: aload 33
    //   5834: getfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   5837: ldc2_w 1496
    //   5840: lmul
    //   5841: invokevirtual 1501	org/telegram/messenger/video/InputSurface:setPresentationTime	(J)V
    //   5844: aload 37
    //   5846: astore 32
    //   5848: aload 28
    //   5850: astore 29
    //   5852: aload 31
    //   5854: astore 27
    //   5856: aload 30
    //   5858: astore_2
    //   5859: aload 31
    //   5861: invokevirtual 1504	org/telegram/messenger/video/InputSurface:swapBuffers	()Z
    //   5864: pop
    //   5865: aload 37
    //   5867: astore 32
    //   5869: aload 28
    //   5871: astore 29
    //   5873: aload 31
    //   5875: astore 27
    //   5877: aload 30
    //   5879: astore_2
    //   5880: iload 57
    //   5882: istore 13
    //   5884: iload 46
    //   5886: istore 44
    //   5888: aload 39
    //   5890: astore 34
    //   5892: iload 45
    //   5894: istore 9
    //   5896: iload 43
    //   5898: istore 50
    //   5900: lload 41
    //   5902: lstore 52
    //   5904: iload 19
    //   5906: istore 12
    //   5908: aload 33
    //   5910: getfield 1439	android/media/MediaCodec$BufferInfo:flags	I
    //   5913: iconst_4
    //   5914: iand
    //   5915: ifeq -3133 -> 2782
    //   5918: iconst_0
    //   5919: istore 54
    //   5921: aload 37
    //   5923: astore 32
    //   5925: aload 28
    //   5927: astore 29
    //   5929: aload 31
    //   5931: astore 27
    //   5933: aload 30
    //   5935: astore_2
    //   5936: getstatic 1243	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   5939: ifeq +24 -> 5963
    //   5942: aload 37
    //   5944: astore 32
    //   5946: aload 28
    //   5948: astore 29
    //   5950: aload 31
    //   5952: astore 27
    //   5954: aload 30
    //   5956: astore_2
    //   5957: ldc_w 1506
    //   5960: invokestatic 1261	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   5963: aload 37
    //   5965: astore 32
    //   5967: aload 28
    //   5969: astore 29
    //   5971: aload 31
    //   5973: astore 27
    //   5975: aload 30
    //   5977: astore_2
    //   5978: getstatic 950	android/os/Build$VERSION:SDK_INT	I
    //   5981: bipush 18
    //   5983: if_icmplt +394 -> 6377
    //   5986: aload 37
    //   5988: astore 32
    //   5990: aload 28
    //   5992: astore 29
    //   5994: aload 31
    //   5996: astore 27
    //   5998: aload 30
    //   6000: astore_2
    //   6001: aload 28
    //   6003: invokevirtual 1509	android/media/MediaCodec:signalEndOfInputStream	()V
    //   6006: iload 57
    //   6008: istore 13
    //   6010: iload 54
    //   6012: istore 20
    //   6014: iload 46
    //   6016: istore 44
    //   6018: aload 39
    //   6020: astore 34
    //   6022: iload 45
    //   6024: istore 9
    //   6026: iload 43
    //   6028: istore 50
    //   6030: lload 41
    //   6032: lstore 52
    //   6034: iload 19
    //   6036: istore 12
    //   6038: goto -3256 -> 2782
    //   6041: iconst_0
    //   6042: istore 15
    //   6044: goto -591 -> 5453
    //   6047: aload 37
    //   6049: astore 32
    //   6051: aload 28
    //   6053: astore 29
    //   6055: aload 31
    //   6057: astore 27
    //   6059: aload 30
    //   6061: astore_2
    //   6062: aload 33
    //   6064: getfield 1420	android/media/MediaCodec$BufferInfo:size	I
    //   6067: ifne +28 -> 6095
    //   6070: aload 37
    //   6072: astore 32
    //   6074: aload 28
    //   6076: astore 29
    //   6078: aload 31
    //   6080: astore 27
    //   6082: aload 30
    //   6084: astore_2
    //   6085: aload 33
    //   6087: getfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   6090: lconst_0
    //   6091: lcmp
    //   6092: ifeq +9 -> 6101
    //   6095: iconst_1
    //   6096: istore 15
    //   6098: goto -645 -> 5453
    //   6101: iconst_0
    //   6102: istore 15
    //   6104: goto -6 -> 6098
    //   6107: aload 37
    //   6109: astore 32
    //   6111: aload 28
    //   6113: astore 29
    //   6115: aload 31
    //   6117: astore 27
    //   6119: aload 30
    //   6121: astore_2
    //   6122: aload 33
    //   6124: getfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   6127: lstore 41
    //   6129: iload 23
    //   6131: istore 15
    //   6133: goto -404 -> 5729
    //   6136: astore 34
    //   6138: iconst_1
    //   6139: istore 9
    //   6141: aload 37
    //   6143: astore 32
    //   6145: aload 28
    //   6147: astore 29
    //   6149: aload 31
    //   6151: astore 27
    //   6153: aload 30
    //   6155: astore_2
    //   6156: aload 34
    //   6158: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   6161: goto -395 -> 5766
    //   6164: aload 37
    //   6166: astore 32
    //   6168: aload 28
    //   6170: astore 29
    //   6172: aload 31
    //   6174: astore 27
    //   6176: aload 30
    //   6178: astore_2
    //   6179: aload 28
    //   6181: ldc2_w 1378
    //   6184: invokevirtual 1383	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   6187: istore 9
    //   6189: iload 9
    //   6191: iflt +141 -> 6332
    //   6194: aload 37
    //   6196: astore 32
    //   6198: aload 28
    //   6200: astore 29
    //   6202: aload 31
    //   6204: astore 27
    //   6206: aload 30
    //   6208: astore_2
    //   6209: aload 30
    //   6211: iconst_1
    //   6212: invokevirtual 1495	org/telegram/messenger/video/OutputSurface:drawImage	(Z)V
    //   6215: aload 37
    //   6217: astore 32
    //   6219: aload 28
    //   6221: astore 29
    //   6223: aload 31
    //   6225: astore 27
    //   6227: aload 30
    //   6229: astore_2
    //   6230: aload 30
    //   6232: invokevirtual 1513	org/telegram/messenger/video/OutputSurface:getFrame	()Ljava/nio/ByteBuffer;
    //   6235: astore 49
    //   6237: aload 38
    //   6239: iload 9
    //   6241: aaload
    //   6242: astore 34
    //   6244: aload 37
    //   6246: astore 32
    //   6248: aload 28
    //   6250: astore 29
    //   6252: aload 31
    //   6254: astore 27
    //   6256: aload 30
    //   6258: astore_2
    //   6259: aload 34
    //   6261: invokevirtual 1516	java/nio/ByteBuffer:clear	()Ljava/nio/Buffer;
    //   6264: pop
    //   6265: aload 37
    //   6267: astore 32
    //   6269: aload 28
    //   6271: astore 29
    //   6273: aload 31
    //   6275: astore 27
    //   6277: aload 30
    //   6279: astore_2
    //   6280: aload 49
    //   6282: aload 34
    //   6284: iload 10
    //   6286: iload 18
    //   6288: iload 17
    //   6290: iload 7
    //   6292: iload 11
    //   6294: invokestatic 1520	org/telegram/messenger/Utilities:convertVideoFrame	(Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;IIIII)I
    //   6297: pop
    //   6298: aload 37
    //   6300: astore 32
    //   6302: aload 28
    //   6304: astore 29
    //   6306: aload 31
    //   6308: astore 27
    //   6310: aload 30
    //   6312: astore_2
    //   6313: aload 28
    //   6315: iload 9
    //   6317: iconst_0
    //   6318: iload 8
    //   6320: aload 33
    //   6322: getfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   6325: iconst_0
    //   6326: invokevirtual 1390	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   6329: goto -464 -> 5865
    //   6332: aload 37
    //   6334: astore 32
    //   6336: aload 28
    //   6338: astore 29
    //   6340: aload 31
    //   6342: astore 27
    //   6344: aload 30
    //   6346: astore_2
    //   6347: getstatic 1243	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   6350: ifeq -485 -> 5865
    //   6353: aload 37
    //   6355: astore 32
    //   6357: aload 28
    //   6359: astore 29
    //   6361: aload 31
    //   6363: astore 27
    //   6365: aload 30
    //   6367: astore_2
    //   6368: ldc_w 1522
    //   6371: invokestatic 1261	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   6374: goto -509 -> 5865
    //   6377: aload 37
    //   6379: astore 32
    //   6381: aload 28
    //   6383: astore 29
    //   6385: aload 31
    //   6387: astore 27
    //   6389: aload 30
    //   6391: astore_2
    //   6392: aload 28
    //   6394: ldc2_w 1378
    //   6397: invokevirtual 1383	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   6400: istore 58
    //   6402: iload 57
    //   6404: istore 13
    //   6406: iload 54
    //   6408: istore 20
    //   6410: iload 46
    //   6412: istore 44
    //   6414: aload 39
    //   6416: astore 34
    //   6418: iload 45
    //   6420: istore 9
    //   6422: iload 43
    //   6424: istore 50
    //   6426: lload 41
    //   6428: lstore 52
    //   6430: iload 19
    //   6432: istore 12
    //   6434: iload 58
    //   6436: iflt -3654 -> 2782
    //   6439: aload 37
    //   6441: astore 32
    //   6443: aload 28
    //   6445: astore 29
    //   6447: aload 31
    //   6449: astore 27
    //   6451: aload 30
    //   6453: astore_2
    //   6454: aload 28
    //   6456: iload 58
    //   6458: iconst_0
    //   6459: iconst_1
    //   6460: aload 33
    //   6462: getfield 1430	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   6465: iconst_4
    //   6466: invokevirtual 1390	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   6469: iload 57
    //   6471: istore 13
    //   6473: iload 54
    //   6475: istore 20
    //   6477: iload 46
    //   6479: istore 44
    //   6481: aload 39
    //   6483: astore 34
    //   6485: iload 45
    //   6487: istore 9
    //   6489: iload 43
    //   6491: istore 50
    //   6493: lload 41
    //   6495: lstore 52
    //   6497: iload 19
    //   6499: istore 12
    //   6501: goto -3719 -> 2782
    //   6504: iload 13
    //   6506: iconst_m1
    //   6507: if_icmpeq +119 -> 6626
    //   6510: iconst_1
    //   6511: istore 15
    //   6513: aload_0
    //   6514: aload_1
    //   6515: aload 35
    //   6517: aload 22
    //   6519: aload 33
    //   6521: lload_3
    //   6522: lload 5
    //   6524: aload 16
    //   6526: iload 15
    //   6528: invokespecial 1526	org/telegram/messenger/MediaController:readAndWriteTracks	(Lorg/telegram/messenger/MessageObject;Landroid/media/MediaExtractor;Lorg/telegram/messenger/video/MP4Builder;Landroid/media/MediaCodec$BufferInfo;JJLjava/io/File;Z)J
    //   6531: pop2
    //   6532: iload 23
    //   6534: istore 15
    //   6536: goto -5523 -> 1013
    //   6539: astore 28
    //   6541: aload 35
    //   6543: astore 29
    //   6545: iconst_1
    //   6546: istore 23
    //   6548: aload 29
    //   6550: astore 30
    //   6552: aload 22
    //   6554: astore 31
    //   6556: aload 28
    //   6558: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   6561: aload 29
    //   6563: ifnull +8 -> 6571
    //   6566: aload 29
    //   6568: invokevirtual 1235	android/media/MediaExtractor:release	()V
    //   6571: aload 22
    //   6573: ifnull +8 -> 6581
    //   6576: aload 22
    //   6578: invokevirtual 1238	org/telegram/messenger/video/MP4Builder:finishMovie	()V
    //   6581: iload 23
    //   6583: istore 15
    //   6585: getstatic 1243	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   6588: ifeq -5521 -> 1067
    //   6591: new 1245	java/lang/StringBuilder
    //   6594: dup
    //   6595: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   6598: ldc_w 1248
    //   6601: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   6604: invokestatic 1157	java/lang/System:currentTimeMillis	()J
    //   6607: lload 25
    //   6609: lsub
    //   6610: invokevirtual 1255	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   6613: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   6616: invokestatic 1261	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   6619: iload 23
    //   6621: istore 15
    //   6623: goto -5556 -> 1067
    //   6626: iconst_0
    //   6627: istore 15
    //   6629: goto -116 -> 6513
    //   6632: astore 22
    //   6634: aload 22
    //   6636: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   6639: goto -5606 -> 1033
    //   6642: astore 22
    //   6644: aload 22
    //   6646: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   6649: goto -68 -> 6581
    //   6652: astore 22
    //   6654: aload 22
    //   6656: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   6659: goto -3281 -> 3378
    //   6662: aload 21
    //   6664: invokeinterface 1136 1 0
    //   6669: ldc_w 1126
    //   6672: iconst_1
    //   6673: invokeinterface 1142 3 0
    //   6678: invokeinterface 1145 1 0
    //   6683: pop
    //   6684: aload_0
    //   6685: aload_1
    //   6686: aload 16
    //   6688: iconst_1
    //   6689: iconst_1
    //   6690: invokespecial 1152	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   6693: iconst_0
    //   6694: istore 15
    //   6696: goto -6421 -> 275
    //   6699: astore_1
    //   6700: aload 31
    //   6702: astore 22
    //   6704: goto -3346 -> 3358
    //   6707: astore 28
    //   6709: aload 32
    //   6711: astore 22
    //   6713: goto -168 -> 6545
    //   6716: astore 30
    //   6718: aload 31
    //   6720: astore 27
    //   6722: aload 37
    //   6724: astore 32
    //   6726: aload 30
    //   6728: astore 31
    //   6730: aload 28
    //   6732: astore 29
    //   6734: aload 34
    //   6736: astore_2
    //   6737: goto -5791 -> 946
    //   6740: goto -5673 -> 1067
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	6743	0	this	MediaController
    //   0	6743	1	paramMessageObject	MessageObject
    //   7	6730	2	localObject1	Object
    //   15	6507	3	l1	long
    //   23	6500	5	l2	long
    //   32	6259	7	i	int
    //   41	6278	8	j	int
    //   50	6438	9	k	int
    //   59	6226	10	m	int
    //   68	6225	11	n	int
    //   77	6423	12	i1	int
    //   86	6422	13	i2	int
    //   89	3474	14	i3	int
    //   100	6595	15	bool1	boolean
    //   116	6571	16	localFile	File
    //   149	6140	17	i4	int
    //   153	6134	18	i5	int
    //   157	6341	19	i6	int
    //   162	6314	20	i7	int
    //   174	6489	21	localSharedPreferences	android.content.SharedPreferences
    //   184	6393	22	localObject2	Object
    //   6632	3	22	localException1	Exception
    //   6642	3	22	localException2	Exception
    //   6652	3	22	localException3	Exception
    //   6702	10	22	localObject3	Object
    //   204	6416	23	bool2	boolean
    //   412	2053	24	bool3	boolean
    //   417	6191	25	l3	long
    //   430	6291	27	localObject4	Object
    //   436	6019	28	localObject5	Object
    //   6539	18	28	localException4	Exception
    //   6707	24	28	localException5	Exception
    //   439	6294	29	localObject6	Object
    //   443	6108	30	localObject7	Object
    //   6716	11	30	localException6	Exception
    //   447	484	31	localObject8	Object
    //   944	1057	31	localException7	Exception
    //   2060	4669	31	localObject9	Object
    //   451	6274	32	localObject10	Object
    //   456	6064	33	localBufferInfo	MediaCodec.BufferInfo
    //   490	5531	34	localObject11	Object
    //   6136	21	34	localException8	Exception
    //   6242	493	34	localObject12	Object
    //   583	5959	35	localObject13	Object
    //   705	1833	36	i8	int
    //   732	5991	37	localMediaCodec	android.media.MediaCodec
    //   735	5503	38	localObject14	Object
    //   741	5741	39	localObject15	Object
    //   744	2593	40	localObject16	Object
    //   755	5739	41	l4	long
    //   758	5732	43	i9	int
    //   761	5719	44	i10	int
    //   764	5722	45	i11	int
    //   774	5704	46	i12	int
    //   778	3099	47	i13	int
    //   852	4516	48	localObject17	Object
    //   1131	5150	49	localObject18	Object
    //   1501	4991	50	i14	int
    //   1800	3281	51	localObject19	Object
    //   2760	3736	52	l5	long
    //   2951	3523	54	i15	int
    //   4671	283	55	arrayOfByte	byte[]
    //   4755	47	56	localObject20	Object
    //   5455	1015	57	i16	int
    //   6400	57	58	i17	int
    // Exception table:
    //   from	to	target	type
    //   801	809	944	java/lang/Exception
    //   824	832	944	java/lang/Exception
    //   847	854	944	java/lang/Exception
    //   869	878	944	java/lang/Exception
    //   898	903	944	java/lang/Exception
    //   918	926	944	java/lang/Exception
    //   941	944	944	java/lang/Exception
    //   1126	1133	944	java/lang/Exception
    //   1148	1159	944	java/lang/Exception
    //   1185	1193	944	java/lang/Exception
    //   1208	1219	944	java/lang/Exception
    //   1242	1253	944	java/lang/Exception
    //   1287	1293	944	java/lang/Exception
    //   1308	1313	944	java/lang/Exception
    //   1328	1333	944	java/lang/Exception
    //   1348	1393	944	java/lang/Exception
    //   1420	1426	944	java/lang/Exception
    //   1441	1446	944	java/lang/Exception
    //   1461	1466	944	java/lang/Exception
    //   1481	1500	944	java/lang/Exception
    //   1518	1529	944	java/lang/Exception
    //   1584	1595	944	java/lang/Exception
    //   1610	1617	944	java/lang/Exception
    //   1632	1641	944	java/lang/Exception
    //   1664	1671	944	java/lang/Exception
    //   1686	1695	944	java/lang/Exception
    //   1710	1723	944	java/lang/Exception
    //   1738	1748	944	java/lang/Exception
    //   1769	1776	944	java/lang/Exception
    //   1791	1802	944	java/lang/Exception
    //   1817	1827	944	java/lang/Exception
    //   1851	1861	944	java/lang/Exception
    //   1885	1895	944	java/lang/Exception
    //   1910	1920	944	java/lang/Exception
    //   1935	1943	944	java/lang/Exception
    //   1958	1971	944	java/lang/Exception
    //   1986	1996	944	java/lang/Exception
    //   2011	2018	944	java/lang/Exception
    //   2033	2043	944	java/lang/Exception
    //   2062	2070	944	java/lang/Exception
    //   2085	2090	944	java/lang/Exception
    //   2105	2115	944	java/lang/Exception
    //   2135	2140	944	java/lang/Exception
    //   2155	2168	944	java/lang/Exception
    //   2183	2191	944	java/lang/Exception
    //   2206	2211	944	java/lang/Exception
    //   2226	2231	944	java/lang/Exception
    //   2250	2264	944	java/lang/Exception
    //   2279	2284	944	java/lang/Exception
    //   2312	2320	944	java/lang/Exception
    //   2335	2342	944	java/lang/Exception
    //   2357	2364	944	java/lang/Exception
    //   2391	2399	944	java/lang/Exception
    //   2414	2421	944	java/lang/Exception
    //   2444	2448	944	java/lang/Exception
    //   2495	2499	944	java/lang/Exception
    //   2526	2533	944	java/lang/Exception
    //   2555	2565	944	java/lang/Exception
    //   2593	2601	944	java/lang/Exception
    //   2623	2633	944	java/lang/Exception
    //   2653	2664	944	java/lang/Exception
    //   2695	2705	944	java/lang/Exception
    //   2729	2740	944	java/lang/Exception
    //   2831	2835	944	java/lang/Exception
    //   2850	2862	944	java/lang/Exception
    //   2941	2953	944	java/lang/Exception
    //   2996	3007	944	java/lang/Exception
    //   3032	3043	944	java/lang/Exception
    //   3068	3079	944	java/lang/Exception
    //   3111	3122	944	java/lang/Exception
    //   3173	3187	944	java/lang/Exception
    //   3269	3280	944	java/lang/Exception
    //   3314	3325	944	java/lang/Exception
    //   3343	3350	944	java/lang/Exception
    //   3444	3459	944	java/lang/Exception
    //   3477	3486	944	java/lang/Exception
    //   3504	3520	944	java/lang/Exception
    //   3535	3541	944	java/lang/Exception
    //   3580	3593	944	java/lang/Exception
    //   3608	3616	944	java/lang/Exception
    //   3631	3638	944	java/lang/Exception
    //   3653	3664	944	java/lang/Exception
    //   3679	3687	944	java/lang/Exception
    //   3702	3712	944	java/lang/Exception
    //   3727	3733	944	java/lang/Exception
    //   3756	3764	944	java/lang/Exception
    //   3794	3805	944	java/lang/Exception
    //   3820	3826	944	java/lang/Exception
    //   3841	3851	944	java/lang/Exception
    //   3874	3889	944	java/lang/Exception
    //   3904	3913	944	java/lang/Exception
    //   3939	3945	944	java/lang/Exception
    //   4019	4027	944	java/lang/Exception
    //   4042	4049	944	java/lang/Exception
    //   4086	4093	944	java/lang/Exception
    //   4131	4141	944	java/lang/Exception
    //   4176	4181	944	java/lang/Exception
    //   4196	4201	944	java/lang/Exception
    //   4216	4221	944	java/lang/Exception
    //   4236	4257	944	java/lang/Exception
    //   4272	4275	944	java/lang/Exception
    //   4290	4298	944	java/lang/Exception
    //   4325	4330	944	java/lang/Exception
    //   4345	4350	944	java/lang/Exception
    //   4365	4370	944	java/lang/Exception
    //   4385	4412	944	java/lang/Exception
    //   4427	4430	944	java/lang/Exception
    //   4445	4454	944	java/lang/Exception
    //   4476	4485	944	java/lang/Exception
    //   4500	4510	944	java/lang/Exception
    //   4529	4544	944	java/lang/Exception
    //   4559	4568	944	java/lang/Exception
    //   4587	4597	944	java/lang/Exception
    //   4615	4623	944	java/lang/Exception
    //   4664	4673	944	java/lang/Exception
    //   4688	4705	944	java/lang/Exception
    //   4720	4731	944	java/lang/Exception
    //   4746	4754	944	java/lang/Exception
    //   4775	4784	944	java/lang/Exception
    //   4865	4874	944	java/lang/Exception
    //   4889	4904	944	java/lang/Exception
    //   4919	4936	944	java/lang/Exception
    //   4951	4977	944	java/lang/Exception
    //   4992	5003	944	java/lang/Exception
    //   5028	5038	944	java/lang/Exception
    //   5053	5063	944	java/lang/Exception
    //   5078	5088	944	java/lang/Exception
    //   5148	5155	944	java/lang/Exception
    //   5186	5192	944	java/lang/Exception
    //   5207	5212	944	java/lang/Exception
    //   5227	5232	944	java/lang/Exception
    //   5247	5266	944	java/lang/Exception
    //   5305	5310	944	java/lang/Exception
    //   5325	5330	944	java/lang/Exception
    //   5345	5350	944	java/lang/Exception
    //   5365	5386	944	java/lang/Exception
    //   5401	5404	944	java/lang/Exception
    //   5419	5427	944	java/lang/Exception
    //   5442	5450	944	java/lang/Exception
    //   5499	5510	944	java/lang/Exception
    //   5534	5546	944	java/lang/Exception
    //   5592	5602	944	java/lang/Exception
    //   5628	5634	944	java/lang/Exception
    //   5649	5654	944	java/lang/Exception
    //   5669	5674	944	java/lang/Exception
    //   5689	5721	944	java/lang/Exception
    //   5744	5753	944	java/lang/Exception
    //   5786	5794	944	java/lang/Exception
    //   5809	5815	944	java/lang/Exception
    //   5830	5844	944	java/lang/Exception
    //   5859	5865	944	java/lang/Exception
    //   5908	5918	944	java/lang/Exception
    //   5936	5942	944	java/lang/Exception
    //   5957	5963	944	java/lang/Exception
    //   5978	5986	944	java/lang/Exception
    //   6001	6006	944	java/lang/Exception
    //   6062	6070	944	java/lang/Exception
    //   6085	6095	944	java/lang/Exception
    //   6122	6129	944	java/lang/Exception
    //   6156	6161	944	java/lang/Exception
    //   6179	6189	944	java/lang/Exception
    //   6209	6215	944	java/lang/Exception
    //   6230	6237	944	java/lang/Exception
    //   6259	6265	944	java/lang/Exception
    //   6280	6298	944	java/lang/Exception
    //   6313	6329	944	java/lang/Exception
    //   6347	6353	944	java/lang/Exception
    //   6368	6374	944	java/lang/Exception
    //   6392	6402	944	java/lang/Exception
    //   6454	6469	944	java/lang/Exception
    //   659	669	3353	finally
    //   688	698	3353	finally
    //   698	707	3353	finally
    //   713	722	3353	finally
    //   801	809	3353	finally
    //   824	832	3353	finally
    //   847	854	3353	finally
    //   869	878	3353	finally
    //   898	903	3353	finally
    //   918	926	3353	finally
    //   941	944	3353	finally
    //   946	951	3353	finally
    //   954	961	3353	finally
    //   965	969	3353	finally
    //   974	979	3353	finally
    //   984	994	3353	finally
    //   999	1009	3353	finally
    //   1009	1013	3353	finally
    //   1126	1133	3353	finally
    //   1148	1159	3353	finally
    //   1185	1193	3353	finally
    //   1208	1219	3353	finally
    //   1242	1253	3353	finally
    //   1287	1293	3353	finally
    //   1308	1313	3353	finally
    //   1328	1333	3353	finally
    //   1348	1393	3353	finally
    //   1420	1426	3353	finally
    //   1441	1446	3353	finally
    //   1461	1466	3353	finally
    //   1481	1500	3353	finally
    //   1518	1529	3353	finally
    //   1584	1595	3353	finally
    //   1610	1617	3353	finally
    //   1632	1641	3353	finally
    //   1664	1671	3353	finally
    //   1686	1695	3353	finally
    //   1710	1723	3353	finally
    //   1738	1748	3353	finally
    //   1769	1776	3353	finally
    //   1791	1802	3353	finally
    //   1817	1827	3353	finally
    //   1851	1861	3353	finally
    //   1885	1895	3353	finally
    //   1910	1920	3353	finally
    //   1935	1943	3353	finally
    //   1958	1971	3353	finally
    //   1986	1996	3353	finally
    //   2011	2018	3353	finally
    //   2033	2043	3353	finally
    //   2062	2070	3353	finally
    //   2085	2090	3353	finally
    //   2105	2115	3353	finally
    //   2115	2120	3353	finally
    //   2135	2140	3353	finally
    //   2155	2168	3353	finally
    //   2183	2191	3353	finally
    //   2206	2211	3353	finally
    //   2226	2231	3353	finally
    //   2250	2264	3353	finally
    //   2279	2284	3353	finally
    //   2312	2320	3353	finally
    //   2335	2342	3353	finally
    //   2357	2364	3353	finally
    //   2391	2399	3353	finally
    //   2414	2421	3353	finally
    //   2444	2448	3353	finally
    //   2495	2499	3353	finally
    //   2526	2533	3353	finally
    //   2555	2565	3353	finally
    //   2593	2601	3353	finally
    //   2623	2633	3353	finally
    //   2653	2664	3353	finally
    //   2695	2705	3353	finally
    //   2729	2740	3353	finally
    //   2831	2835	3353	finally
    //   2850	2862	3353	finally
    //   2941	2953	3353	finally
    //   2996	3007	3353	finally
    //   3032	3043	3353	finally
    //   3068	3079	3353	finally
    //   3111	3122	3353	finally
    //   3173	3187	3353	finally
    //   3269	3280	3353	finally
    //   3314	3325	3353	finally
    //   3343	3350	3353	finally
    //   3444	3459	3353	finally
    //   3477	3486	3353	finally
    //   3504	3520	3353	finally
    //   3535	3541	3353	finally
    //   3580	3593	3353	finally
    //   3608	3616	3353	finally
    //   3631	3638	3353	finally
    //   3653	3664	3353	finally
    //   3679	3687	3353	finally
    //   3702	3712	3353	finally
    //   3727	3733	3353	finally
    //   3756	3764	3353	finally
    //   3794	3805	3353	finally
    //   3820	3826	3353	finally
    //   3841	3851	3353	finally
    //   3874	3889	3353	finally
    //   3904	3913	3353	finally
    //   3939	3945	3353	finally
    //   4019	4027	3353	finally
    //   4042	4049	3353	finally
    //   4086	4093	3353	finally
    //   4131	4141	3353	finally
    //   4176	4181	3353	finally
    //   4196	4201	3353	finally
    //   4216	4221	3353	finally
    //   4236	4257	3353	finally
    //   4272	4275	3353	finally
    //   4290	4298	3353	finally
    //   4325	4330	3353	finally
    //   4345	4350	3353	finally
    //   4365	4370	3353	finally
    //   4385	4412	3353	finally
    //   4427	4430	3353	finally
    //   4445	4454	3353	finally
    //   4476	4485	3353	finally
    //   4500	4510	3353	finally
    //   4529	4544	3353	finally
    //   4559	4568	3353	finally
    //   4587	4597	3353	finally
    //   4615	4623	3353	finally
    //   4664	4673	3353	finally
    //   4688	4705	3353	finally
    //   4720	4731	3353	finally
    //   4746	4754	3353	finally
    //   4775	4784	3353	finally
    //   4865	4874	3353	finally
    //   4889	4904	3353	finally
    //   4919	4936	3353	finally
    //   4951	4977	3353	finally
    //   4992	5003	3353	finally
    //   5028	5038	3353	finally
    //   5053	5063	3353	finally
    //   5078	5088	3353	finally
    //   5148	5155	3353	finally
    //   5186	5192	3353	finally
    //   5207	5212	3353	finally
    //   5227	5232	3353	finally
    //   5247	5266	3353	finally
    //   5305	5310	3353	finally
    //   5325	5330	3353	finally
    //   5345	5350	3353	finally
    //   5365	5386	3353	finally
    //   5401	5404	3353	finally
    //   5419	5427	3353	finally
    //   5442	5450	3353	finally
    //   5499	5510	3353	finally
    //   5534	5546	3353	finally
    //   5592	5602	3353	finally
    //   5628	5634	3353	finally
    //   5649	5654	3353	finally
    //   5669	5674	3353	finally
    //   5689	5721	3353	finally
    //   5744	5753	3353	finally
    //   5761	5766	3353	finally
    //   5786	5794	3353	finally
    //   5809	5815	3353	finally
    //   5830	5844	3353	finally
    //   5859	5865	3353	finally
    //   5908	5918	3353	finally
    //   5936	5942	3353	finally
    //   5957	5963	3353	finally
    //   5978	5986	3353	finally
    //   6001	6006	3353	finally
    //   6062	6070	3353	finally
    //   6085	6095	3353	finally
    //   6122	6129	3353	finally
    //   6156	6161	3353	finally
    //   6179	6189	3353	finally
    //   6209	6215	3353	finally
    //   6230	6237	3353	finally
    //   6259	6265	3353	finally
    //   6280	6298	3353	finally
    //   6313	6329	3353	finally
    //   6347	6353	3353	finally
    //   6368	6374	3353	finally
    //   6392	6402	3353	finally
    //   6454	6469	3353	finally
    //   6513	6532	3353	finally
    //   5761	5766	6136	java/lang/Exception
    //   659	669	6539	java/lang/Exception
    //   688	698	6539	java/lang/Exception
    //   698	707	6539	java/lang/Exception
    //   713	722	6539	java/lang/Exception
    //   946	951	6539	java/lang/Exception
    //   954	961	6539	java/lang/Exception
    //   965	969	6539	java/lang/Exception
    //   974	979	6539	java/lang/Exception
    //   984	994	6539	java/lang/Exception
    //   999	1009	6539	java/lang/Exception
    //   1009	1013	6539	java/lang/Exception
    //   6513	6532	6539	java/lang/Exception
    //   1028	1033	6632	java/lang/Exception
    //   6576	6581	6642	java/lang/Exception
    //   3373	3378	6652	java/lang/Exception
    //   453	458	6699	finally
    //   470	475	6699	finally
    //   487	492	6699	finally
    //   504	509	6699	finally
    //   521	528	6699	finally
    //   540	547	6699	finally
    //   559	568	6699	finally
    //   580	585	6699	finally
    //   597	602	6699	finally
    //   614	625	6699	finally
    //   637	642	6699	finally
    //   654	659	6699	finally
    //   6556	6561	6699	finally
    //   453	458	6707	java/lang/Exception
    //   470	475	6707	java/lang/Exception
    //   487	492	6707	java/lang/Exception
    //   504	509	6707	java/lang/Exception
    //   521	528	6707	java/lang/Exception
    //   540	547	6707	java/lang/Exception
    //   559	568	6707	java/lang/Exception
    //   580	585	6707	java/lang/Exception
    //   597	602	6707	java/lang/Exception
    //   614	625	6707	java/lang/Exception
    //   637	642	6707	java/lang/Exception
    //   654	659	6707	java/lang/Exception
    //   2115	2120	6716	java/lang/Exception
  }
  
  /* Error */
  public static String copyFileToCache(Uri paramUri, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: aconst_null
    //   5: astore 4
    //   7: aconst_null
    //   8: astore 5
    //   10: aload_3
    //   11: astore 6
    //   13: aload 4
    //   15: astore 7
    //   17: aload_2
    //   18: astore 8
    //   20: aload_0
    //   21: invokestatic 1532	org/telegram/messenger/MediaController:getFileName	(Landroid/net/Uri;)Ljava/lang/String;
    //   24: invokestatic 1535	org/telegram/messenger/FileLoader:fixFileName	(Ljava/lang/String;)Ljava/lang/String;
    //   27: astore 9
    //   29: aload 9
    //   31: astore 10
    //   33: aload 9
    //   35: ifnonnull +68 -> 103
    //   38: aload_3
    //   39: astore 6
    //   41: aload 4
    //   43: astore 7
    //   45: aload_2
    //   46: astore 8
    //   48: invokestatic 1538	org/telegram/messenger/SharedConfig:getLastLocalId	()I
    //   51: istore 11
    //   53: aload_3
    //   54: astore 6
    //   56: aload 4
    //   58: astore 7
    //   60: aload_2
    //   61: astore 8
    //   63: invokestatic 1541	org/telegram/messenger/SharedConfig:saveConfig	()V
    //   66: aload_3
    //   67: astore 6
    //   69: aload 4
    //   71: astore 7
    //   73: aload_2
    //   74: astore 8
    //   76: getstatic 1547	java/util/Locale:US	Ljava/util/Locale;
    //   79: ldc_w 1549
    //   82: iconst_2
    //   83: anewarray 4	java/lang/Object
    //   86: dup
    //   87: iconst_0
    //   88: iload 11
    //   90: invokestatic 1555	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   93: aastore
    //   94: dup
    //   95: iconst_1
    //   96: aload_1
    //   97: aastore
    //   98: invokestatic 1559	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   101: astore 10
    //   103: aload_3
    //   104: astore 6
    //   106: aload 4
    //   108: astore 7
    //   110: aload_2
    //   111: astore 8
    //   113: new 998	java/io/File
    //   116: astore_1
    //   117: aload_3
    //   118: astore 6
    //   120: aload 4
    //   122: astore 7
    //   124: aload_2
    //   125: astore 8
    //   127: aload_1
    //   128: iconst_4
    //   129: invokestatic 1563	org/telegram/messenger/FileLoader:getDirectory	(I)Ljava/io/File;
    //   132: ldc_w 1565
    //   135: invokespecial 1568	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   138: aload_3
    //   139: astore 6
    //   141: aload 4
    //   143: astore 7
    //   145: aload_2
    //   146: astore 8
    //   148: aload_1
    //   149: invokevirtual 1571	java/io/File:mkdirs	()Z
    //   152: pop
    //   153: aload_3
    //   154: astore 6
    //   156: aload 4
    //   158: astore 7
    //   160: aload_2
    //   161: astore 8
    //   163: new 998	java/io/File
    //   166: astore 9
    //   168: aload_3
    //   169: astore 6
    //   171: aload 4
    //   173: astore 7
    //   175: aload_2
    //   176: astore 8
    //   178: aload 9
    //   180: aload_1
    //   181: aload 10
    //   183: invokespecial 1568	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   186: aload_3
    //   187: astore 6
    //   189: aload 4
    //   191: astore 7
    //   193: aload_2
    //   194: astore 8
    //   196: aload 9
    //   198: invokestatic 1577	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   201: invokestatic 1581	org/telegram/messenger/AndroidUtilities:isInternalUri	(Landroid/net/Uri;)Z
    //   204: istore 12
    //   206: iload 12
    //   208: ifeq +51 -> 259
    //   211: aconst_null
    //   212: astore_1
    //   213: iconst_0
    //   214: ifeq +11 -> 225
    //   217: new 1583	java/lang/NullPointerException
    //   220: dup
    //   221: invokespecial 1584	java/lang/NullPointerException:<init>	()V
    //   224: athrow
    //   225: aload_1
    //   226: astore_0
    //   227: iconst_0
    //   228: ifeq +11 -> 239
    //   231: new 1583	java/lang/NullPointerException
    //   234: dup
    //   235: invokespecial 1584	java/lang/NullPointerException:<init>	()V
    //   238: athrow
    //   239: aload_0
    //   240: areturn
    //   241: astore_0
    //   242: aload_0
    //   243: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   246: goto -21 -> 225
    //   249: astore_0
    //   250: aload_0
    //   251: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   254: aload_1
    //   255: astore_0
    //   256: goto -17 -> 239
    //   259: aload_3
    //   260: astore 6
    //   262: aload 4
    //   264: astore 7
    //   266: aload_2
    //   267: astore 8
    //   269: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   272: invokevirtual 520	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   275: aload_0
    //   276: invokevirtual 1588	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   279: astore_0
    //   280: aload_0
    //   281: astore 6
    //   283: aload 4
    //   285: astore 7
    //   287: aload_0
    //   288: astore 8
    //   290: new 1590	java/io/FileOutputStream
    //   293: astore_1
    //   294: aload_0
    //   295: astore 6
    //   297: aload 4
    //   299: astore 7
    //   301: aload_0
    //   302: astore 8
    //   304: aload_1
    //   305: aload 9
    //   307: invokespecial 1592	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   310: sipush 20480
    //   313: newarray <illegal type>
    //   315: astore 6
    //   317: aload_0
    //   318: aload 6
    //   320: invokevirtual 1598	java/io/InputStream:read	([B)I
    //   323: istore 11
    //   325: iload 11
    //   327: iconst_m1
    //   328: if_icmpeq +49 -> 377
    //   331: aload_1
    //   332: aload 6
    //   334: iconst_0
    //   335: iload 11
    //   337: invokevirtual 1602	java/io/FileOutputStream:write	([BII)V
    //   340: goto -23 -> 317
    //   343: astore 10
    //   345: aload_0
    //   346: astore 6
    //   348: aload_1
    //   349: astore 7
    //   351: aload 10
    //   353: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   356: aload_0
    //   357: ifnull +7 -> 364
    //   360: aload_0
    //   361: invokevirtual 1605	java/io/InputStream:close	()V
    //   364: aload_1
    //   365: ifnull +7 -> 372
    //   368: aload_1
    //   369: invokevirtual 1606	java/io/FileOutputStream:close	()V
    //   372: aconst_null
    //   373: astore_0
    //   374: goto -135 -> 239
    //   377: aload 9
    //   379: invokevirtual 1609	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   382: astore 6
    //   384: aload_0
    //   385: ifnull +7 -> 392
    //   388: aload_0
    //   389: invokevirtual 1605	java/io/InputStream:close	()V
    //   392: aload_1
    //   393: ifnull +7 -> 400
    //   396: aload_1
    //   397: invokevirtual 1606	java/io/FileOutputStream:close	()V
    //   400: aload 6
    //   402: astore_0
    //   403: goto -164 -> 239
    //   406: astore_0
    //   407: aload_0
    //   408: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   411: goto -19 -> 392
    //   414: astore_0
    //   415: aload_0
    //   416: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   419: goto -19 -> 400
    //   422: astore_0
    //   423: aload_0
    //   424: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   427: goto -63 -> 364
    //   430: astore_0
    //   431: aload_0
    //   432: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   435: goto -63 -> 372
    //   438: astore_0
    //   439: aload 6
    //   441: astore_1
    //   442: aload_1
    //   443: ifnull +7 -> 450
    //   446: aload_1
    //   447: invokevirtual 1605	java/io/InputStream:close	()V
    //   450: aload 7
    //   452: ifnull +8 -> 460
    //   455: aload 7
    //   457: invokevirtual 1606	java/io/FileOutputStream:close	()V
    //   460: aload_0
    //   461: athrow
    //   462: astore_1
    //   463: aload_1
    //   464: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   467: goto -17 -> 450
    //   470: astore_1
    //   471: aload_1
    //   472: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   475: goto -15 -> 460
    //   478: astore 6
    //   480: aload_1
    //   481: astore 7
    //   483: aload_0
    //   484: astore_1
    //   485: aload 6
    //   487: astore_0
    //   488: goto -46 -> 442
    //   491: astore 10
    //   493: aload 8
    //   495: astore_0
    //   496: aload 5
    //   498: astore_1
    //   499: goto -154 -> 345
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	502	0	paramUri	Uri
    //   0	502	1	paramString	String
    //   1	266	2	localObject1	Object
    //   3	257	3	localObject2	Object
    //   5	293	4	localObject3	Object
    //   8	489	5	localObject4	Object
    //   11	429	6	localObject5	Object
    //   478	8	6	localObject6	Object
    //   15	467	7	localObject7	Object
    //   18	476	8	localObject8	Object
    //   27	351	9	localObject9	Object
    //   31	151	10	localObject10	Object
    //   343	9	10	localException1	Exception
    //   491	1	10	localException2	Exception
    //   51	285	11	i	int
    //   204	3	12	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   217	225	241	java/lang/Exception
    //   231	239	249	java/lang/Exception
    //   310	317	343	java/lang/Exception
    //   317	325	343	java/lang/Exception
    //   331	340	343	java/lang/Exception
    //   377	384	343	java/lang/Exception
    //   388	392	406	java/lang/Exception
    //   396	400	414	java/lang/Exception
    //   360	364	422	java/lang/Exception
    //   368	372	430	java/lang/Exception
    //   20	29	438	finally
    //   48	53	438	finally
    //   63	66	438	finally
    //   76	103	438	finally
    //   113	117	438	finally
    //   127	138	438	finally
    //   148	153	438	finally
    //   163	168	438	finally
    //   178	186	438	finally
    //   196	206	438	finally
    //   269	280	438	finally
    //   290	294	438	finally
    //   304	310	438	finally
    //   351	356	438	finally
    //   446	450	462	java/lang/Exception
    //   455	460	470	java/lang/Exception
    //   310	317	478	finally
    //   317	325	478	finally
    //   331	340	478	finally
    //   377	384	478	finally
    //   20	29	491	java/lang/Exception
    //   48	53	491	java/lang/Exception
    //   63	66	491	java/lang/Exception
    //   76	103	491	java/lang/Exception
    //   113	117	491	java/lang/Exception
    //   127	138	491	java/lang/Exception
    //   148	153	491	java/lang/Exception
    //   163	168	491	java/lang/Exception
    //   178	186	491	java/lang/Exception
    //   196	206	491	java/lang/Exception
    //   269	280	491	java/lang/Exception
    //   290	294	491	java/lang/Exception
    //   304	310	491	java/lang/Exception
  }
  
  private void didWriteData(final MessageObject paramMessageObject, final File paramFile, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    final boolean bool = this.videoConvertFirstWrite;
    if (bool) {
      this.videoConvertFirstWrite = false;
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if ((paramBoolean2) || (paramBoolean1)) {}
        synchronized (MediaController.this.videoConvertSync)
        {
          MediaController.access$8202(MediaController.this, false);
          MediaController.this.videoConvertQueue.remove(paramMessageObject);
          MediaController.this.startVideoConvertFromQueue();
          if (paramBoolean2)
          {
            NotificationCenter.getInstance(paramMessageObject.currentAccount).postNotificationName(NotificationCenter.FilePreparingFailed, new Object[] { paramMessageObject, paramFile.toString() });
            return;
          }
        }
        if (bool) {
          NotificationCenter.getInstance(paramMessageObject.currentAccount).postNotificationName(NotificationCenter.FilePreparingStarted, new Object[] { paramMessageObject, paramFile.toString() });
        }
        NotificationCenter localNotificationCenter = NotificationCenter.getInstance(paramMessageObject.currentAccount);
        int i = NotificationCenter.FileNewChunkAvailable;
        ??? = paramMessageObject;
        String str = paramFile.toString();
        long l1 = paramFile.length();
        if (paramBoolean1) {}
        for (long l2 = paramFile.length();; l2 = 0L)
        {
          localNotificationCenter.postNotificationName(i, new Object[] { ???, str, Long.valueOf(l1), Long.valueOf(l2) });
          break;
        }
      }
    });
  }
  
  private int findTrack(MediaExtractor paramMediaExtractor, boolean paramBoolean)
  {
    int i = paramMediaExtractor.getTrackCount();
    int j = 0;
    String str;
    int k;
    if (j < i)
    {
      str = paramMediaExtractor.getTrackFormat(j).getString("mime");
      if (paramBoolean)
      {
        if (!str.startsWith("audio/")) {
          break label65;
        }
        k = j;
      }
    }
    for (;;)
    {
      return k;
      k = j;
      if (!str.startsWith("video/"))
      {
        label65:
        j++;
        break;
        k = -5;
      }
    }
  }
  
  public static String getFileName(Uri paramUri)
  {
    localObject1 = null;
    String str = null;
    localObject2 = localObject1;
    if (paramUri.getScheme().equals("content"))
    {
      localObject2 = null;
      localObject3 = null;
    }
    try
    {
      Cursor localCursor = ApplicationLoader.applicationContext.getContentResolver().query(paramUri, new String[] { "_display_name" }, null, null, null);
      localObject3 = localCursor;
      localObject2 = localCursor;
      if (localCursor.moveToFirst())
      {
        localObject3 = localCursor;
        localObject2 = localCursor;
        str = localCursor.getString(localCursor.getColumnIndex("_display_name"));
      }
      localObject2 = str;
      if (localCursor != null)
      {
        localCursor.close();
        localObject2 = str;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        int i;
        localObject2 = localObject3;
        FileLog.e(localException);
        localObject2 = localObject1;
        if (localObject3 != null)
        {
          ((Cursor)localObject3).close();
          localObject2 = localObject1;
        }
      }
    }
    finally
    {
      if (localObject2 == null) {
        break label187;
      }
      ((Cursor)localObject2).close();
    }
    localObject3 = localObject2;
    if (localObject2 == null)
    {
      paramUri = paramUri.getPath();
      i = paramUri.lastIndexOf('/');
      localObject3 = paramUri;
      if (i != -1) {
        localObject3 = paramUri.substring(i + 1);
      }
    }
    return (String)localObject3;
  }
  
  /* Error */
  public static MediaController getInstance()
  {
    // Byte code:
    //   0: getstatic 1656	org/telegram/messenger/MediaController:Instance	Lorg/telegram/messenger/MediaController;
    //   3: astore_0
    //   4: aload_0
    //   5: astore_1
    //   6: aload_0
    //   7: ifnonnull +31 -> 38
    //   10: ldc 2
    //   12: monitorenter
    //   13: getstatic 1656	org/telegram/messenger/MediaController:Instance	Lorg/telegram/messenger/MediaController;
    //   16: astore_0
    //   17: aload_0
    //   18: astore_1
    //   19: aload_0
    //   20: ifnonnull +15 -> 35
    //   23: new 2	org/telegram/messenger/MediaController
    //   26: astore_1
    //   27: aload_1
    //   28: invokespecial 1657	org/telegram/messenger/MediaController:<init>	()V
    //   31: aload_1
    //   32: putstatic 1656	org/telegram/messenger/MediaController:Instance	Lorg/telegram/messenger/MediaController;
    //   35: ldc 2
    //   37: monitorexit
    //   38: aload_1
    //   39: areturn
    //   40: astore_1
    //   41: ldc 2
    //   43: monitorexit
    //   44: aload_1
    //   45: athrow
    //   46: astore_1
    //   47: goto -6 -> 41
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	17	0	localMediaController1	MediaController
    //   5	34	1	localMediaController2	MediaController
    //   40	5	1	localObject1	Object
    //   46	1	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   13	17	40	finally
    //   23	31	40	finally
    //   35	38	40	finally
    //   41	44	40	finally
    //   31	35	46	finally
  }
  
  private native long getTotalPcmDuration();
  
  public static boolean isGif(Uri paramUri)
  {
    boolean bool1 = false;
    Object localObject = null;
    Uri localUri = null;
    for (;;)
    {
      try
      {
        paramUri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(paramUri);
        localUri = paramUri;
        localObject = paramUri;
        byte[] arrayOfByte = new byte[3];
        localUri = paramUri;
        localObject = paramUri;
        if (paramUri.read(arrayOfByte, 0, 3) != 3) {
          continue;
        }
        localUri = paramUri;
        localObject = paramUri;
        String str = new java/lang/String;
        localUri = paramUri;
        localObject = paramUri;
        str.<init>(arrayOfByte);
        if (str == null) {
          continue;
        }
        localUri = paramUri;
        localObject = paramUri;
        bool2 = str.equalsIgnoreCase("gif");
        if (!bool2) {
          continue;
        }
        bool1 = true;
        bool2 = bool1;
      }
      catch (Exception paramUri)
      {
        localObject = localUri;
        FileLog.e(paramUri);
        boolean bool2 = bool1;
        if (localUri == null) {
          continue;
        }
        try
        {
          localUri.close();
          bool2 = bool1;
        }
        catch (Exception paramUri)
        {
          FileLog.e(paramUri);
          bool2 = bool1;
        }
        continue;
      }
      finally
      {
        if (localObject == null) {
          break;
        }
      }
      try
      {
        paramUri.close();
        bool2 = bool1;
      }
      catch (Exception paramUri)
      {
        FileLog.e(paramUri);
        bool2 = bool1;
        continue;
      }
      return bool2;
      bool2 = bool1;
      if (paramUri != null) {
        try
        {
          paramUri.close();
          bool2 = bool1;
        }
        catch (Exception paramUri)
        {
          FileLog.e(paramUri);
          bool2 = bool1;
        }
      }
    }
    try
    {
      ((InputStream)localObject).close();
      throw paramUri;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  private boolean isNearToSensor(float paramFloat)
  {
    if ((paramFloat < 5.0F) && (paramFloat != this.proximitySensor.getMaximumRange())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static native int isOpusFile(String paramString);
  
  private static boolean isRecognizedFormat(int paramInt)
  {
    switch (paramInt)
    {
    }
    for (boolean bool = false;; bool = true) {
      return bool;
    }
  }
  
  private boolean isSamePlayingMessage(MessageObject paramMessageObject)
  {
    boolean bool = true;
    int i;
    int j;
    if ((this.playingMessageObject != null) && (this.playingMessageObject.getDialogId() == paramMessageObject.getDialogId()) && (this.playingMessageObject.getId() == paramMessageObject.getId())) {
      if (this.playingMessageObject.eventId == 0L)
      {
        i = 1;
        if (paramMessageObject.eventId != 0L) {
          break label77;
        }
        j = 1;
        label64:
        if (i != j) {
          break label83;
        }
      }
    }
    for (;;)
    {
      return bool;
      i = 0;
      break;
      label77:
      j = 0;
      break label64;
      label83:
      bool = false;
    }
  }
  
  public static boolean isWebp(Uri paramUri)
  {
    boolean bool1 = false;
    Object localObject1 = null;
    Uri localUri = null;
    for (;;)
    {
      try
      {
        paramUri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(paramUri);
        localUri = paramUri;
        localObject1 = paramUri;
        Object localObject2 = new byte[12];
        localUri = paramUri;
        localObject1 = paramUri;
        if (paramUri.read((byte[])localObject2, 0, 12) != 12) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        String str = new java/lang/String;
        localUri = paramUri;
        localObject1 = paramUri;
        str.<init>((byte[])localObject2);
        if (str == null) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        localObject2 = str.toLowerCase();
        localUri = paramUri;
        localObject1 = paramUri;
        if (!((String)localObject2).startsWith("riff")) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        bool2 = ((String)localObject2).endsWith("webp");
        if (!bool2) {
          continue;
        }
        bool1 = true;
        bool2 = bool1;
      }
      catch (Exception paramUri)
      {
        localObject1 = localUri;
        FileLog.e(paramUri);
        boolean bool2 = bool1;
        if (localUri == null) {
          continue;
        }
        try
        {
          localUri.close();
          bool2 = bool1;
        }
        catch (Exception paramUri)
        {
          FileLog.e(paramUri);
          bool2 = bool1;
        }
        continue;
      }
      finally
      {
        if (localObject1 == null) {
          break;
        }
      }
      try
      {
        paramUri.close();
        bool2 = bool1;
      }
      catch (Exception paramUri)
      {
        FileLog.e(paramUri);
        bool2 = bool1;
        continue;
      }
      return bool2;
      bool2 = bool1;
      if (paramUri != null) {
        try
        {
          paramUri.close();
          bool2 = bool1;
        }
        catch (Exception paramUri)
        {
          FileLog.e(paramUri);
          bool2 = bool1;
        }
      }
    }
    try
    {
      ((InputStream)localObject1).close();
      throw paramUri;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public static void loadGalleryPhotosAlbums(int paramInt)
  {
    Thread localThread = new Thread(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: new 29	java/util/ArrayList
        //   3: dup
        //   4: invokespecial 30	java/util/ArrayList:<init>	()V
        //   7: astore_1
        //   8: new 29	java/util/ArrayList
        //   11: dup
        //   12: invokespecial 30	java/util/ArrayList:<init>	()V
        //   15: astore_2
        //   16: new 32	android/util/SparseArray
        //   19: dup
        //   20: invokespecial 33	android/util/SparseArray:<init>	()V
        //   23: astore_3
        //   24: new 32	android/util/SparseArray
        //   27: dup
        //   28: invokespecial 33	android/util/SparseArray:<init>	()V
        //   31: astore 4
        //   33: aconst_null
        //   34: astore 5
        //   36: aconst_null
        //   37: astore 6
        //   39: aconst_null
        //   40: astore 7
        //   42: aconst_null
        //   43: astore 8
        //   45: aconst_null
        //   46: astore 9
        //   48: new 35	java/lang/StringBuilder
        //   51: astore 10
        //   53: aload 10
        //   55: invokespecial 36	java/lang/StringBuilder:<init>	()V
        //   58: aload 10
        //   60: getstatic 42	android/os/Environment:DIRECTORY_DCIM	Ljava/lang/String;
        //   63: invokestatic 46	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
        //   66: invokevirtual 52	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   69: invokevirtual 56	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   72: ldc 58
        //   74: invokevirtual 56	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   77: invokevirtual 61	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   80: astore 10
        //   82: aload 10
        //   84: astore 9
        //   86: aconst_null
        //   87: astore 11
        //   89: aconst_null
        //   90: astore 12
        //   92: aconst_null
        //   93: astore 13
        //   95: aconst_null
        //   96: astore 14
        //   98: aconst_null
        //   99: astore 15
        //   101: aconst_null
        //   102: astore 16
        //   104: aconst_null
        //   105: astore 17
        //   107: aload 11
        //   109: astore 18
        //   111: aload 8
        //   113: astore 19
        //   115: aload 6
        //   117: astore 20
        //   119: aload 17
        //   121: astore 21
        //   123: aload 15
        //   125: astore 22
        //   127: getstatic 66	android/os/Build$VERSION:SDK_INT	I
        //   130: bipush 23
        //   132: if_icmplt +94 -> 226
        //   135: aload 11
        //   137: astore 18
        //   139: aload 8
        //   141: astore 19
        //   143: aload 6
        //   145: astore 20
        //   147: aload 17
        //   149: astore 21
        //   151: aload 15
        //   153: astore 22
        //   155: aload 12
        //   157: astore 23
        //   159: aload 7
        //   161: astore 10
        //   163: aload 5
        //   165: astore 24
        //   167: aload 16
        //   169: astore 25
        //   171: getstatic 66	android/os/Build$VERSION:SDK_INT	I
        //   174: bipush 23
        //   176: if_icmplt +2249 -> 2425
        //   179: aload 11
        //   181: astore 18
        //   183: aload 8
        //   185: astore 19
        //   187: aload 6
        //   189: astore 20
        //   191: aload 17
        //   193: astore 21
        //   195: aload 15
        //   197: astore 22
        //   199: aload 12
        //   201: astore 23
        //   203: aload 7
        //   205: astore 10
        //   207: aload 5
        //   209: astore 24
        //   211: aload 16
        //   213: astore 25
        //   215: getstatic 72	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   218: ldc 74
        //   220: invokevirtual 80	android/content/Context:checkSelfPermission	(Ljava/lang/String;)I
        //   223: ifne +2202 -> 2425
        //   226: aload 11
        //   228: astore 18
        //   230: aload 8
        //   232: astore 19
        //   234: aload 6
        //   236: astore 20
        //   238: aload 17
        //   240: astore 21
        //   242: aload 15
        //   244: astore 22
        //   246: getstatic 72	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   249: invokevirtual 84	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
        //   252: getstatic 90	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
        //   255: invokestatic 94	org/telegram/messenger/MediaController:access$7700	()[Ljava/lang/String;
        //   258: aconst_null
        //   259: aconst_null
        //   260: ldc 96
        //   262: invokestatic 100	android/provider/MediaStore$Images$Media:query	(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   265: astore 17
        //   267: aload 12
        //   269: astore 23
        //   271: aload 7
        //   273: astore 10
        //   275: aload 5
        //   277: astore 24
        //   279: aload 17
        //   281: astore 25
        //   283: aload 17
        //   285: ifnull +2140 -> 2425
        //   288: aload 11
        //   290: astore 18
        //   292: aload 8
        //   294: astore 19
        //   296: aload 6
        //   298: astore 20
        //   300: aload 17
        //   302: astore 21
        //   304: aload 17
        //   306: astore 22
        //   308: aload 17
        //   310: ldc 102
        //   312: invokeinterface 107 2 0
        //   317: istore 26
        //   319: aload 11
        //   321: astore 18
        //   323: aload 8
        //   325: astore 19
        //   327: aload 6
        //   329: astore 20
        //   331: aload 17
        //   333: astore 21
        //   335: aload 17
        //   337: astore 22
        //   339: aload 17
        //   341: ldc 109
        //   343: invokeinterface 107 2 0
        //   348: istore 27
        //   350: aload 11
        //   352: astore 18
        //   354: aload 8
        //   356: astore 19
        //   358: aload 6
        //   360: astore 20
        //   362: aload 17
        //   364: astore 21
        //   366: aload 17
        //   368: astore 22
        //   370: aload 17
        //   372: ldc 111
        //   374: invokeinterface 107 2 0
        //   379: istore 28
        //   381: aload 11
        //   383: astore 18
        //   385: aload 8
        //   387: astore 19
        //   389: aload 6
        //   391: astore 20
        //   393: aload 17
        //   395: astore 21
        //   397: aload 17
        //   399: astore 22
        //   401: aload 17
        //   403: ldc 113
        //   405: invokeinterface 107 2 0
        //   410: istore 29
        //   412: aload 11
        //   414: astore 18
        //   416: aload 8
        //   418: astore 19
        //   420: aload 6
        //   422: astore 20
        //   424: aload 17
        //   426: astore 21
        //   428: aload 17
        //   430: astore 22
        //   432: aload 17
        //   434: ldc 115
        //   436: invokeinterface 107 2 0
        //   441: istore 30
        //   443: aload 11
        //   445: astore 18
        //   447: aload 8
        //   449: astore 19
        //   451: aload 6
        //   453: astore 20
        //   455: aload 17
        //   457: astore 21
        //   459: aload 17
        //   461: astore 22
        //   463: aload 17
        //   465: ldc 117
        //   467: invokeinterface 107 2 0
        //   472: istore 31
        //   474: aconst_null
        //   475: astore 10
        //   477: aconst_null
        //   478: astore 25
        //   480: aload 14
        //   482: astore 6
        //   484: aload 13
        //   486: astore 23
        //   488: aload 17
        //   490: invokeinterface 121 1 0
        //   495: ifeq +1922 -> 2417
        //   498: aload 17
        //   500: iload 26
        //   502: invokeinterface 125 2 0
        //   507: istore 32
        //   509: aload 17
        //   511: iload 27
        //   513: invokeinterface 125 2 0
        //   518: istore 33
        //   520: aload 17
        //   522: iload 28
        //   524: invokeinterface 129 2 0
        //   529: astore 7
        //   531: aload 17
        //   533: iload 29
        //   535: invokeinterface 129 2 0
        //   540: astore 14
        //   542: aload 17
        //   544: iload 30
        //   546: invokeinterface 133 2 0
        //   551: lstore 34
        //   553: aload 17
        //   555: iload 31
        //   557: invokeinterface 125 2 0
        //   562: istore 36
        //   564: aload 14
        //   566: ifnull -78 -> 488
        //   569: aload 14
        //   571: invokevirtual 139	java/lang/String:length	()I
        //   574: ifeq -86 -> 488
        //   577: new 141	org/telegram/messenger/MediaController$PhotoEntry
        //   580: astore 5
        //   582: aload 5
        //   584: iload 33
        //   586: iload 32
        //   588: lload 34
        //   590: aload 14
        //   592: iload 36
        //   594: iconst_0
        //   595: invokespecial 144	org/telegram/messenger/MediaController$PhotoEntry:<init>	(IIJLjava/lang/String;IZ)V
        //   598: aload 25
        //   600: ifnonnull +2244 -> 2844
        //   603: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   606: astore 18
        //   608: aload 18
        //   610: iconst_0
        //   611: ldc -108
        //   613: ldc -107
        //   615: invokestatic 154	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   618: aload 5
        //   620: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   623: aload 18
        //   625: astore 22
        //   627: aload_2
        //   628: iconst_0
        //   629: aload 18
        //   631: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   634: aload 18
        //   636: astore 25
        //   638: aload 10
        //   640: ifnonnull +2201 -> 2841
        //   643: aload 25
        //   645: astore 22
        //   647: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   650: astore 24
        //   652: aload 25
        //   654: astore 22
        //   656: aload 24
        //   658: iconst_0
        //   659: ldc -93
        //   661: ldc -92
        //   663: invokestatic 154	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   666: aload 5
        //   668: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   671: aload 23
        //   673: astore 18
        //   675: aload 24
        //   677: astore 19
        //   679: aload 25
        //   681: astore 20
        //   683: aload 17
        //   685: astore 21
        //   687: aload 17
        //   689: astore 22
        //   691: aload_1
        //   692: iconst_0
        //   693: aload 24
        //   695: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   698: aload 24
        //   700: astore 10
        //   702: aload 23
        //   704: astore 18
        //   706: aload 10
        //   708: astore 19
        //   710: aload 25
        //   712: astore 20
        //   714: aload 17
        //   716: astore 21
        //   718: aload 17
        //   720: astore 22
        //   722: aload 25
        //   724: aload 5
        //   726: invokevirtual 168	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   729: aload 23
        //   731: astore 18
        //   733: aload 10
        //   735: astore 19
        //   737: aload 25
        //   739: astore 20
        //   741: aload 17
        //   743: astore 21
        //   745: aload 17
        //   747: astore 22
        //   749: aload 10
        //   751: aload 5
        //   753: invokevirtual 168	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   756: aload 23
        //   758: astore 18
        //   760: aload 10
        //   762: astore 19
        //   764: aload 25
        //   766: astore 20
        //   768: aload 17
        //   770: astore 21
        //   772: aload 17
        //   774: astore 22
        //   776: aload_3
        //   777: iload 33
        //   779: invokevirtual 172	android/util/SparseArray:get	(I)Ljava/lang/Object;
        //   782: checkcast 146	org/telegram/messenger/MediaController$AlbumEntry
        //   785: astore 8
        //   787: aload 23
        //   789: astore 24
        //   791: aload 8
        //   793: astore 13
        //   795: aload 8
        //   797: ifnonnull +186 -> 983
        //   800: aload 23
        //   802: astore 18
        //   804: aload 10
        //   806: astore 19
        //   808: aload 25
        //   810: astore 20
        //   812: aload 17
        //   814: astore 21
        //   816: aload 17
        //   818: astore 22
        //   820: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   823: astore 13
        //   825: aload 23
        //   827: astore 18
        //   829: aload 10
        //   831: astore 19
        //   833: aload 25
        //   835: astore 20
        //   837: aload 17
        //   839: astore 21
        //   841: aload 17
        //   843: astore 22
        //   845: aload 13
        //   847: iload 33
        //   849: aload 7
        //   851: aload 5
        //   853: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   856: aload 23
        //   858: astore 18
        //   860: aload 10
        //   862: astore 19
        //   864: aload 25
        //   866: astore 20
        //   868: aload 17
        //   870: astore 21
        //   872: aload 17
        //   874: astore 22
        //   876: aload_3
        //   877: iload 33
        //   879: aload 13
        //   881: invokevirtual 175	android/util/SparseArray:put	(ILjava/lang/Object;)V
        //   884: aload 23
        //   886: ifnonnull +401 -> 1287
        //   889: aload 9
        //   891: ifnull +396 -> 1287
        //   894: aload 14
        //   896: ifnull +391 -> 1287
        //   899: aload 23
        //   901: astore 18
        //   903: aload 10
        //   905: astore 19
        //   907: aload 25
        //   909: astore 20
        //   911: aload 17
        //   913: astore 21
        //   915: aload 17
        //   917: astore 22
        //   919: aload 14
        //   921: aload 9
        //   923: invokevirtual 179	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   926: ifeq +361 -> 1287
        //   929: aload 23
        //   931: astore 18
        //   933: aload 10
        //   935: astore 19
        //   937: aload 25
        //   939: astore 20
        //   941: aload 17
        //   943: astore 21
        //   945: aload 17
        //   947: astore 22
        //   949: aload_1
        //   950: iconst_0
        //   951: aload 13
        //   953: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   956: aload 23
        //   958: astore 18
        //   960: aload 10
        //   962: astore 19
        //   964: aload 25
        //   966: astore 20
        //   968: aload 17
        //   970: astore 21
        //   972: aload 17
        //   974: astore 22
        //   976: iload 33
        //   978: invokestatic 185	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   981: astore 24
        //   983: aload 24
        //   985: astore 18
        //   987: aload 10
        //   989: astore 19
        //   991: aload 25
        //   993: astore 20
        //   995: aload 17
        //   997: astore 21
        //   999: aload 17
        //   1001: astore 22
        //   1003: aload 13
        //   1005: aload 5
        //   1007: invokevirtual 168	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   1010: aload 24
        //   1012: astore 18
        //   1014: aload 10
        //   1016: astore 19
        //   1018: aload 25
        //   1020: astore 20
        //   1022: aload 17
        //   1024: astore 21
        //   1026: aload 17
        //   1028: astore 22
        //   1030: aload 4
        //   1032: iload 33
        //   1034: invokevirtual 172	android/util/SparseArray:get	(I)Ljava/lang/Object;
        //   1037: checkcast 146	org/telegram/messenger/MediaController$AlbumEntry
        //   1040: astore 8
        //   1042: aload 8
        //   1044: astore 23
        //   1046: aload 6
        //   1048: astore 13
        //   1050: aload 8
        //   1052: ifnonnull +187 -> 1239
        //   1055: aload 24
        //   1057: astore 18
        //   1059: aload 10
        //   1061: astore 19
        //   1063: aload 25
        //   1065: astore 20
        //   1067: aload 17
        //   1069: astore 21
        //   1071: aload 17
        //   1073: astore 22
        //   1075: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   1078: astore 23
        //   1080: aload 24
        //   1082: astore 18
        //   1084: aload 10
        //   1086: astore 19
        //   1088: aload 25
        //   1090: astore 20
        //   1092: aload 17
        //   1094: astore 21
        //   1096: aload 17
        //   1098: astore 22
        //   1100: aload 23
        //   1102: iload 33
        //   1104: aload 7
        //   1106: aload 5
        //   1108: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   1111: aload 24
        //   1113: astore 18
        //   1115: aload 10
        //   1117: astore 19
        //   1119: aload 25
        //   1121: astore 20
        //   1123: aload 17
        //   1125: astore 21
        //   1127: aload 17
        //   1129: astore 22
        //   1131: aload 4
        //   1133: iload 33
        //   1135: aload 23
        //   1137: invokevirtual 175	android/util/SparseArray:put	(ILjava/lang/Object;)V
        //   1140: aload 6
        //   1142: ifnonnull +1220 -> 2362
        //   1145: aload 9
        //   1147: ifnull +1215 -> 2362
        //   1150: aload 14
        //   1152: ifnull +1210 -> 2362
        //   1155: aload 24
        //   1157: astore 18
        //   1159: aload 10
        //   1161: astore 19
        //   1163: aload 25
        //   1165: astore 20
        //   1167: aload 17
        //   1169: astore 21
        //   1171: aload 17
        //   1173: astore 22
        //   1175: aload 14
        //   1177: aload 9
        //   1179: invokevirtual 179	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   1182: ifeq +1180 -> 2362
        //   1185: aload 24
        //   1187: astore 18
        //   1189: aload 10
        //   1191: astore 19
        //   1193: aload 25
        //   1195: astore 20
        //   1197: aload 17
        //   1199: astore 21
        //   1201: aload 17
        //   1203: astore 22
        //   1205: aload_2
        //   1206: iconst_0
        //   1207: aload 23
        //   1209: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   1212: aload 24
        //   1214: astore 18
        //   1216: aload 10
        //   1218: astore 19
        //   1220: aload 25
        //   1222: astore 20
        //   1224: aload 17
        //   1226: astore 21
        //   1228: aload 17
        //   1230: astore 22
        //   1232: iload 33
        //   1234: invokestatic 185	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1237: astore 13
        //   1239: aload 24
        //   1241: astore 18
        //   1243: aload 10
        //   1245: astore 19
        //   1247: aload 25
        //   1249: astore 20
        //   1251: aload 17
        //   1253: astore 21
        //   1255: aload 17
        //   1257: astore 22
        //   1259: aload 23
        //   1261: aload 5
        //   1263: invokevirtual 168	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   1266: aload 24
        //   1268: astore 23
        //   1270: aload 13
        //   1272: astore 6
        //   1274: goto -786 -> 488
        //   1277: astore 10
        //   1279: aload 10
        //   1281: invokestatic 191	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   1284: goto -1198 -> 86
        //   1287: aload 23
        //   1289: astore 18
        //   1291: aload 10
        //   1293: astore 19
        //   1295: aload 25
        //   1297: astore 20
        //   1299: aload 17
        //   1301: astore 21
        //   1303: aload 17
        //   1305: astore 22
        //   1307: aload_1
        //   1308: aload 13
        //   1310: invokevirtual 194	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   1313: pop
        //   1314: aload 23
        //   1316: astore 24
        //   1318: goto -335 -> 983
        //   1321: astore 17
        //   1323: aload 21
        //   1325: astore 23
        //   1327: aload 20
        //   1329: astore 25
        //   1331: aload 19
        //   1333: astore 10
        //   1335: aload 23
        //   1337: astore 22
        //   1339: aload 17
        //   1341: invokestatic 191	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   1344: aload 18
        //   1346: astore 20
        //   1348: aload 10
        //   1350: astore 19
        //   1352: aload 25
        //   1354: astore 22
        //   1356: aload 23
        //   1358: astore 17
        //   1360: aload 23
        //   1362: ifnull +1460 -> 2822
        //   1365: aload 23
        //   1367: invokeinterface 197 1 0
        //   1372: aload 18
        //   1374: astore 17
        //   1376: aload 25
        //   1378: astore 24
        //   1380: aload 10
        //   1382: astore 18
        //   1384: aload 23
        //   1386: astore 25
        //   1388: aload 17
        //   1390: astore 22
        //   1392: aload 10
        //   1394: astore 19
        //   1396: aload 23
        //   1398: astore 19
        //   1400: aload 17
        //   1402: astore 13
        //   1404: getstatic 66	android/os/Build$VERSION:SDK_INT	I
        //   1407: bipush 23
        //   1409: if_icmplt +94 -> 1503
        //   1412: aload 10
        //   1414: astore 20
        //   1416: aload 23
        //   1418: astore 6
        //   1420: aload 17
        //   1422: astore 21
        //   1424: aload 10
        //   1426: astore 18
        //   1428: aload 23
        //   1430: astore 25
        //   1432: aload 17
        //   1434: astore 22
        //   1436: aload 10
        //   1438: astore 19
        //   1440: aload 23
        //   1442: astore 19
        //   1444: aload 17
        //   1446: astore 13
        //   1448: getstatic 66	android/os/Build$VERSION:SDK_INT	I
        //   1451: bipush 23
        //   1453: if_icmplt +1079 -> 2532
        //   1456: aload 10
        //   1458: astore 20
        //   1460: aload 23
        //   1462: astore 6
        //   1464: aload 17
        //   1466: astore 21
        //   1468: aload 10
        //   1470: astore 18
        //   1472: aload 23
        //   1474: astore 25
        //   1476: aload 17
        //   1478: astore 22
        //   1480: aload 10
        //   1482: astore 19
        //   1484: aload 23
        //   1486: astore 19
        //   1488: aload 17
        //   1490: astore 13
        //   1492: getstatic 72	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   1495: ldc 74
        //   1497: invokevirtual 80	android/content/Context:checkSelfPermission	(Ljava/lang/String;)I
        //   1500: ifne +1032 -> 2532
        //   1503: aload 10
        //   1505: astore 18
        //   1507: aload 23
        //   1509: astore 25
        //   1511: aload 17
        //   1513: astore 22
        //   1515: aload 10
        //   1517: astore 19
        //   1519: aload 23
        //   1521: astore 19
        //   1523: aload 17
        //   1525: astore 13
        //   1527: getstatic 72	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   1530: invokevirtual 84	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
        //   1533: getstatic 200	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
        //   1536: invokestatic 203	org/telegram/messenger/MediaController:access$7800	()[Ljava/lang/String;
        //   1539: aconst_null
        //   1540: aconst_null
        //   1541: ldc 96
        //   1543: invokestatic 100	android/provider/MediaStore$Images$Media:query	(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   1546: astore 23
        //   1548: aload 10
        //   1550: astore 20
        //   1552: aload 23
        //   1554: astore 6
        //   1556: aload 17
        //   1558: astore 21
        //   1560: aload 23
        //   1562: ifnull +970 -> 2532
        //   1565: aload 10
        //   1567: astore 18
        //   1569: aload 23
        //   1571: astore 25
        //   1573: aload 17
        //   1575: astore 22
        //   1577: aload 10
        //   1579: astore 19
        //   1581: aload 23
        //   1583: astore 19
        //   1585: aload 17
        //   1587: astore 13
        //   1589: aload 23
        //   1591: ldc 102
        //   1593: invokeinterface 107 2 0
        //   1598: istore 26
        //   1600: aload 10
        //   1602: astore 18
        //   1604: aload 23
        //   1606: astore 25
        //   1608: aload 17
        //   1610: astore 22
        //   1612: aload 10
        //   1614: astore 19
        //   1616: aload 23
        //   1618: astore 19
        //   1620: aload 17
        //   1622: astore 13
        //   1624: aload 23
        //   1626: ldc 109
        //   1628: invokeinterface 107 2 0
        //   1633: istore 36
        //   1635: aload 10
        //   1637: astore 18
        //   1639: aload 23
        //   1641: astore 25
        //   1643: aload 17
        //   1645: astore 22
        //   1647: aload 10
        //   1649: astore 19
        //   1651: aload 23
        //   1653: astore 19
        //   1655: aload 17
        //   1657: astore 13
        //   1659: aload 23
        //   1661: ldc 111
        //   1663: invokeinterface 107 2 0
        //   1668: istore 31
        //   1670: aload 10
        //   1672: astore 18
        //   1674: aload 23
        //   1676: astore 25
        //   1678: aload 17
        //   1680: astore 22
        //   1682: aload 10
        //   1684: astore 19
        //   1686: aload 23
        //   1688: astore 19
        //   1690: aload 17
        //   1692: astore 13
        //   1694: aload 23
        //   1696: ldc 113
        //   1698: invokeinterface 107 2 0
        //   1703: istore 28
        //   1705: aload 10
        //   1707: astore 18
        //   1709: aload 23
        //   1711: astore 25
        //   1713: aload 17
        //   1715: astore 22
        //   1717: aload 10
        //   1719: astore 19
        //   1721: aload 23
        //   1723: astore 19
        //   1725: aload 17
        //   1727: astore 13
        //   1729: aload 23
        //   1731: ldc 115
        //   1733: invokeinterface 107 2 0
        //   1738: istore 27
        //   1740: aload 10
        //   1742: astore 18
        //   1744: aload 23
        //   1746: astore 25
        //   1748: aload 17
        //   1750: astore 22
        //   1752: aload 10
        //   1754: astore 19
        //   1756: aload 23
        //   1758: astore 19
        //   1760: aload 17
        //   1762: astore 13
        //   1764: aload 23
        //   1766: ldc -51
        //   1768: invokeinterface 107 2 0
        //   1773: istore 30
        //   1775: aload 10
        //   1777: astore 20
        //   1779: aload 23
        //   1781: astore 6
        //   1783: aload 17
        //   1785: astore 21
        //   1787: aload 10
        //   1789: astore 18
        //   1791: aload 23
        //   1793: astore 25
        //   1795: aload 17
        //   1797: astore 22
        //   1799: aload 10
        //   1801: astore 19
        //   1803: aload 23
        //   1805: astore 19
        //   1807: aload 17
        //   1809: astore 13
        //   1811: aload 23
        //   1813: invokeinterface 121 1 0
        //   1818: ifeq +714 -> 2532
        //   1821: aload 10
        //   1823: astore 18
        //   1825: aload 23
        //   1827: astore 25
        //   1829: aload 17
        //   1831: astore 22
        //   1833: aload 10
        //   1835: astore 19
        //   1837: aload 23
        //   1839: astore 19
        //   1841: aload 17
        //   1843: astore 13
        //   1845: aload 23
        //   1847: iload 26
        //   1849: invokeinterface 125 2 0
        //   1854: istore 29
        //   1856: aload 10
        //   1858: astore 18
        //   1860: aload 23
        //   1862: astore 25
        //   1864: aload 17
        //   1866: astore 22
        //   1868: aload 10
        //   1870: astore 19
        //   1872: aload 23
        //   1874: astore 19
        //   1876: aload 17
        //   1878: astore 13
        //   1880: aload 23
        //   1882: iload 36
        //   1884: invokeinterface 125 2 0
        //   1889: istore 33
        //   1891: aload 10
        //   1893: astore 18
        //   1895: aload 23
        //   1897: astore 25
        //   1899: aload 17
        //   1901: astore 22
        //   1903: aload 10
        //   1905: astore 19
        //   1907: aload 23
        //   1909: astore 19
        //   1911: aload 17
        //   1913: astore 13
        //   1915: aload 23
        //   1917: iload 31
        //   1919: invokeinterface 129 2 0
        //   1924: astore 8
        //   1926: aload 10
        //   1928: astore 18
        //   1930: aload 23
        //   1932: astore 25
        //   1934: aload 17
        //   1936: astore 22
        //   1938: aload 10
        //   1940: astore 19
        //   1942: aload 23
        //   1944: astore 19
        //   1946: aload 17
        //   1948: astore 13
        //   1950: aload 23
        //   1952: iload 28
        //   1954: invokeinterface 129 2 0
        //   1959: astore 6
        //   1961: aload 10
        //   1963: astore 18
        //   1965: aload 23
        //   1967: astore 25
        //   1969: aload 17
        //   1971: astore 22
        //   1973: aload 10
        //   1975: astore 19
        //   1977: aload 23
        //   1979: astore 19
        //   1981: aload 17
        //   1983: astore 13
        //   1985: aload 23
        //   1987: iload 27
        //   1989: invokeinterface 133 2 0
        //   1994: lstore 37
        //   1996: aload 10
        //   1998: astore 18
        //   2000: aload 23
        //   2002: astore 25
        //   2004: aload 17
        //   2006: astore 22
        //   2008: aload 10
        //   2010: astore 19
        //   2012: aload 23
        //   2014: astore 19
        //   2016: aload 17
        //   2018: astore 13
        //   2020: aload 23
        //   2022: iload 30
        //   2024: invokeinterface 133 2 0
        //   2029: lstore 34
        //   2031: aload 6
        //   2033: ifnull -258 -> 1775
        //   2036: aload 10
        //   2038: astore 18
        //   2040: aload 23
        //   2042: astore 25
        //   2044: aload 17
        //   2046: astore 22
        //   2048: aload 10
        //   2050: astore 19
        //   2052: aload 23
        //   2054: astore 19
        //   2056: aload 17
        //   2058: astore 13
        //   2060: aload 6
        //   2062: invokevirtual 139	java/lang/String:length	()I
        //   2065: ifeq -290 -> 1775
        //   2068: aload 10
        //   2070: astore 18
        //   2072: aload 23
        //   2074: astore 25
        //   2076: aload 17
        //   2078: astore 22
        //   2080: aload 10
        //   2082: astore 19
        //   2084: aload 23
        //   2086: astore 19
        //   2088: aload 17
        //   2090: astore 13
        //   2092: new 141	org/telegram/messenger/MediaController$PhotoEntry
        //   2095: astore 21
        //   2097: aload 10
        //   2099: astore 18
        //   2101: aload 23
        //   2103: astore 25
        //   2105: aload 17
        //   2107: astore 22
        //   2109: aload 10
        //   2111: astore 19
        //   2113: aload 23
        //   2115: astore 19
        //   2117: aload 17
        //   2119: astore 13
        //   2121: aload 21
        //   2123: iload 33
        //   2125: iload 29
        //   2127: lload 37
        //   2129: aload 6
        //   2131: lload 34
        //   2133: ldc2_w 206
        //   2136: ldiv
        //   2137: l2i
        //   2138: iconst_1
        //   2139: invokespecial 144	org/telegram/messenger/MediaController$PhotoEntry:<init>	(IIJLjava/lang/String;IZ)V
        //   2142: aload 10
        //   2144: ifnonnull +675 -> 2819
        //   2147: aload 10
        //   2149: astore 18
        //   2151: aload 23
        //   2153: astore 25
        //   2155: aload 17
        //   2157: astore 22
        //   2159: aload 10
        //   2161: astore 19
        //   2163: aload 23
        //   2165: astore 19
        //   2167: aload 17
        //   2169: astore 13
        //   2171: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   2174: astore 20
        //   2176: aload 10
        //   2178: astore 18
        //   2180: aload 23
        //   2182: astore 25
        //   2184: aload 17
        //   2186: astore 22
        //   2188: aload 10
        //   2190: astore 19
        //   2192: aload 23
        //   2194: astore 19
        //   2196: aload 17
        //   2198: astore 13
        //   2200: aload 20
        //   2202: iconst_0
        //   2203: ldc -93
        //   2205: ldc -92
        //   2207: invokestatic 154	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   2210: aload 21
        //   2212: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   2215: aload 20
        //   2217: astore 25
        //   2219: aload_1
        //   2220: iconst_0
        //   2221: aload 20
        //   2223: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   2226: aload 20
        //   2228: astore 10
        //   2230: aload 10
        //   2232: astore 25
        //   2234: aload 10
        //   2236: aload 21
        //   2238: invokevirtual 168	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   2241: aload 10
        //   2243: astore 25
        //   2245: aload_3
        //   2246: iload 33
        //   2248: invokevirtual 172	android/util/SparseArray:get	(I)Ljava/lang/Object;
        //   2251: checkcast 146	org/telegram/messenger/MediaController$AlbumEntry
        //   2254: astore 18
        //   2256: aload 18
        //   2258: astore 25
        //   2260: aload 18
        //   2262: ifnonnull +263 -> 2525
        //   2265: aload 10
        //   2267: astore 25
        //   2269: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   2272: astore 18
        //   2274: aload 10
        //   2276: astore 25
        //   2278: aload 18
        //   2280: iload 33
        //   2282: aload 8
        //   2284: aload 21
        //   2286: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   2289: aload 10
        //   2291: astore 25
        //   2293: aload_3
        //   2294: iload 33
        //   2296: aload 18
        //   2298: invokevirtual 175	android/util/SparseArray:put	(ILjava/lang/Object;)V
        //   2301: aload 17
        //   2303: ifnonnull +207 -> 2510
        //   2306: aload 9
        //   2308: ifnull +202 -> 2510
        //   2311: aload 6
        //   2313: ifnull +197 -> 2510
        //   2316: aload 10
        //   2318: astore 25
        //   2320: aload 6
        //   2322: aload 9
        //   2324: invokevirtual 179	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   2327: ifeq +183 -> 2510
        //   2330: aload 10
        //   2332: astore 25
        //   2334: aload_1
        //   2335: iconst_0
        //   2336: aload 18
        //   2338: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   2341: iload 33
        //   2343: invokestatic 185	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   2346: astore 17
        //   2348: aload 23
        //   2350: astore 25
        //   2352: aload 18
        //   2354: aload 21
        //   2356: invokevirtual 168	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   2359: goto -584 -> 1775
        //   2362: aload 24
        //   2364: astore 18
        //   2366: aload 10
        //   2368: astore 19
        //   2370: aload 25
        //   2372: astore 20
        //   2374: aload 17
        //   2376: astore 21
        //   2378: aload 17
        //   2380: astore 22
        //   2382: aload_2
        //   2383: aload 23
        //   2385: invokevirtual 194	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   2388: pop
        //   2389: aload 6
        //   2391: astore 13
        //   2393: goto -1154 -> 1239
        //   2396: astore 10
        //   2398: aload 22
        //   2400: astore 17
        //   2402: aload 17
        //   2404: ifnull +10 -> 2414
        //   2407: aload 17
        //   2409: invokeinterface 197 1 0
        //   2414: aload 10
        //   2416: athrow
        //   2417: aload 25
        //   2419: astore 24
        //   2421: aload 17
        //   2423: astore 25
        //   2425: aload 23
        //   2427: astore 20
        //   2429: aload 10
        //   2431: astore 19
        //   2433: aload 24
        //   2435: astore 22
        //   2437: aload 25
        //   2439: astore 17
        //   2441: aload 25
        //   2443: ifnull +379 -> 2822
        //   2446: aload 25
        //   2448: invokeinterface 197 1 0
        //   2453: aload 23
        //   2455: astore 17
        //   2457: aload 25
        //   2459: astore 23
        //   2461: goto -1081 -> 1380
        //   2464: astore 17
        //   2466: aload 17
        //   2468: invokestatic 191	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   2471: aload 23
        //   2473: astore 17
        //   2475: aload 25
        //   2477: astore 23
        //   2479: goto -1099 -> 1380
        //   2482: astore 17
        //   2484: aload 17
        //   2486: invokestatic 191	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   2489: aload 18
        //   2491: astore 17
        //   2493: aload 25
        //   2495: astore 24
        //   2497: goto -1117 -> 1380
        //   2500: astore 17
        //   2502: aload 17
        //   2504: invokestatic 191	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   2507: goto -93 -> 2414
        //   2510: aload 10
        //   2512: astore 25
        //   2514: aload_1
        //   2515: aload 18
        //   2517: invokevirtual 194	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   2520: pop
        //   2521: aload 18
        //   2523: astore 25
        //   2525: aload 25
        //   2527: astore 18
        //   2529: goto -181 -> 2348
        //   2532: aload 21
        //   2534: astore 18
        //   2536: aload 20
        //   2538: astore 25
        //   2540: aload 6
        //   2542: ifnull +18 -> 2560
        //   2545: aload 6
        //   2547: invokeinterface 197 1 0
        //   2552: aload 20
        //   2554: astore 25
        //   2556: aload 21
        //   2558: astore 18
        //   2560: iconst_0
        //   2561: istore 29
        //   2563: iload 29
        //   2565: aload_1
        //   2566: invokevirtual 210	java/util/ArrayList:size	()I
        //   2569: if_icmpge +157 -> 2726
        //   2572: aload_1
        //   2573: iload 29
        //   2575: invokevirtual 211	java/util/ArrayList:get	(I)Ljava/lang/Object;
        //   2578: checkcast 146	org/telegram/messenger/MediaController$AlbumEntry
        //   2581: getfield 215	org/telegram/messenger/MediaController$AlbumEntry:photos	Ljava/util/ArrayList;
        //   2584: new 13	org/telegram/messenger/MediaController$29$1
        //   2587: dup
        //   2588: aload_0
        //   2589: invokespecial 218	org/telegram/messenger/MediaController$29$1:<init>	(Lorg/telegram/messenger/MediaController$29;)V
        //   2592: invokestatic 224	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
        //   2595: iinc 29 1
        //   2598: goto -35 -> 2563
        //   2601: astore 10
        //   2603: aload 10
        //   2605: invokestatic 191	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   2608: aload 21
        //   2610: astore 18
        //   2612: aload 20
        //   2614: astore 25
        //   2616: goto -56 -> 2560
        //   2619: astore 23
        //   2621: aload 22
        //   2623: astore 17
        //   2625: aload 18
        //   2627: astore 10
        //   2629: aload 23
        //   2631: astore 18
        //   2633: aload 25
        //   2635: astore 23
        //   2637: aload 23
        //   2639: astore 25
        //   2641: aload 18
        //   2643: invokestatic 191	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   2646: aload 17
        //   2648: astore 18
        //   2650: aload 10
        //   2652: astore 25
        //   2654: aload 23
        //   2656: ifnull -96 -> 2560
        //   2659: aload 23
        //   2661: invokeinterface 197 1 0
        //   2666: aload 17
        //   2668: astore 18
        //   2670: aload 10
        //   2672: astore 25
        //   2674: goto -114 -> 2560
        //   2677: astore 23
        //   2679: aload 23
        //   2681: invokestatic 191	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   2684: aload 17
        //   2686: astore 18
        //   2688: aload 10
        //   2690: astore 25
        //   2692: goto -132 -> 2560
        //   2695: astore 10
        //   2697: aload 19
        //   2699: astore 23
        //   2701: aload 23
        //   2703: ifnull +10 -> 2713
        //   2706: aload 23
        //   2708: invokeinterface 197 1 0
        //   2713: aload 10
        //   2715: athrow
        //   2716: astore 17
        //   2718: aload 17
        //   2720: invokestatic 191	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   2723: goto -10 -> 2713
        //   2726: aload_0
        //   2727: getfield 18	org/telegram/messenger/MediaController$29:val$guid	I
        //   2730: aload_1
        //   2731: aload_2
        //   2732: aload 18
        //   2734: aload 25
        //   2736: aload 24
        //   2738: iconst_0
        //   2739: invokestatic 228	org/telegram/messenger/MediaController:access$7900	(ILjava/util/ArrayList;Ljava/util/ArrayList;Ljava/lang/Integer;Lorg/telegram/messenger/MediaController$AlbumEntry;Lorg/telegram/messenger/MediaController$AlbumEntry;I)V
        //   2742: return
        //   2743: astore 10
        //   2745: goto -44 -> 2701
        //   2748: astore 10
        //   2750: aload 25
        //   2752: astore 23
        //   2754: goto -53 -> 2701
        //   2757: astore 18
        //   2759: aload 25
        //   2761: astore 10
        //   2763: goto -126 -> 2637
        //   2766: astore 18
        //   2768: goto -131 -> 2637
        //   2771: astore 10
        //   2773: goto -371 -> 2402
        //   2776: astore 10
        //   2778: goto -376 -> 2402
        //   2781: astore 22
        //   2783: aload 23
        //   2785: astore 18
        //   2787: aload 17
        //   2789: astore 23
        //   2791: aload 22
        //   2793: astore 17
        //   2795: goto -1460 -> 1335
        //   2798: astore 19
        //   2800: aload 23
        //   2802: astore 18
        //   2804: aload 22
        //   2806: astore 25
        //   2808: aload 17
        //   2810: astore 23
        //   2812: aload 19
        //   2814: astore 17
        //   2816: goto -1481 -> 1335
        //   2819: goto -589 -> 2230
        //   2822: aload 19
        //   2824: astore 10
        //   2826: aload 22
        //   2828: astore 24
        //   2830: aload 17
        //   2832: astore 23
        //   2834: aload 20
        //   2836: astore 17
        //   2838: goto -1458 -> 1380
        //   2841: goto -2139 -> 702
        //   2844: goto -2206 -> 638
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	2847	0	this	29
        //   7	2724	1	localArrayList1	ArrayList
        //   15	2717	2	localArrayList2	ArrayList
        //   23	2271	3	localSparseArray1	SparseArray
        //   31	1101	4	localSparseArray2	SparseArray
        //   34	1228	5	localPhotoEntry	MediaController.PhotoEntry
        //   37	2509	6	localObject1	Object
        //   40	1065	7	str1	String
        //   43	2240	8	localObject2	Object
        //   46	2277	9	localObject3	Object
        //   51	1193	10	localObject4	Object
        //   1277	15	10	localException1	Exception
        //   1333	1034	10	localObject5	Object
        //   2396	115	10	localObject6	Object
        //   2601	3	10	localException2	Exception
        //   2627	62	10	localObject7	Object
        //   2695	19	10	localObject8	Object
        //   2743	1	10	localObject9	Object
        //   2748	1	10	localObject10	Object
        //   2761	1	10	localObject11	Object
        //   2771	1	10	localObject12	Object
        //   2776	1	10	localObject13	Object
        //   2824	1	10	localObject14	Object
        //   87	357	11	localObject15	Object
        //   90	178	12	localObject16	Object
        //   93	2299	13	localObject17	Object
        //   96	1080	14	str2	String
        //   99	144	15	localObject18	Object
        //   102	110	16	localObject19	Object
        //   105	1199	17	localCursor	Cursor
        //   1321	19	17	localThrowable1	Throwable
        //   1358	1098	17	localObject20	Object
        //   2464	3	17	localException3	Exception
        //   2473	1	17	localObject21	Object
        //   2482	3	17	localException4	Exception
        //   2491	1	17	localObject22	Object
        //   2500	3	17	localException5	Exception
        //   2623	62	17	localObject23	Object
        //   2716	72	17	localException6	Exception
        //   2793	44	17	localObject24	Object
        //   109	2624	18	localObject25	Object
        //   2757	1	18	localThrowable2	Throwable
        //   2766	1	18	localThrowable3	Throwable
        //   2785	18	18	localObject26	Object
        //   113	2585	19	localObject27	Object
        //   2798	25	19	localThrowable4	Throwable
        //   117	2718	20	localObject28	Object
        //   121	2488	21	localObject29	Object
        //   125	2497	22	localObject30	Object
        //   2781	46	22	localThrowable5	Throwable
        //   157	2321	23	localObject31	Object
        //   2619	11	23	localThrowable6	Throwable
        //   2635	25	23	localObject32	Object
        //   2677	3	23	localException7	Exception
        //   2699	134	23	localObject33	Object
        //   165	2664	24	localObject34	Object
        //   169	2638	25	localObject35	Object
        //   317	1531	26	i	int
        //   348	1640	27	j	int
        //   379	1574	28	k	int
        //   410	2186	29	m	int
        //   441	1582	30	n	int
        //   472	1446	31	i1	int
        //   507	80	32	i2	int
        //   518	1824	33	i3	int
        //   551	1581	34	l1	long
        //   562	1321	36	i4	int
        //   1994	134	37	l2	long
        // Exception table:
        //   from	to	target	type
        //   48	82	1277	java/lang/Exception
        //   127	135	1321	java/lang/Throwable
        //   171	179	1321	java/lang/Throwable
        //   215	226	1321	java/lang/Throwable
        //   246	267	1321	java/lang/Throwable
        //   308	319	1321	java/lang/Throwable
        //   339	350	1321	java/lang/Throwable
        //   370	381	1321	java/lang/Throwable
        //   401	412	1321	java/lang/Throwable
        //   432	443	1321	java/lang/Throwable
        //   463	474	1321	java/lang/Throwable
        //   691	698	1321	java/lang/Throwable
        //   722	729	1321	java/lang/Throwable
        //   749	756	1321	java/lang/Throwable
        //   776	787	1321	java/lang/Throwable
        //   820	825	1321	java/lang/Throwable
        //   845	856	1321	java/lang/Throwable
        //   876	884	1321	java/lang/Throwable
        //   919	929	1321	java/lang/Throwable
        //   949	956	1321	java/lang/Throwable
        //   976	983	1321	java/lang/Throwable
        //   1003	1010	1321	java/lang/Throwable
        //   1030	1042	1321	java/lang/Throwable
        //   1075	1080	1321	java/lang/Throwable
        //   1100	1111	1321	java/lang/Throwable
        //   1131	1140	1321	java/lang/Throwable
        //   1175	1185	1321	java/lang/Throwable
        //   1205	1212	1321	java/lang/Throwable
        //   1232	1239	1321	java/lang/Throwable
        //   1259	1266	1321	java/lang/Throwable
        //   1307	1314	1321	java/lang/Throwable
        //   2382	2389	1321	java/lang/Throwable
        //   127	135	2396	finally
        //   171	179	2396	finally
        //   215	226	2396	finally
        //   246	267	2396	finally
        //   308	319	2396	finally
        //   339	350	2396	finally
        //   370	381	2396	finally
        //   401	412	2396	finally
        //   432	443	2396	finally
        //   463	474	2396	finally
        //   691	698	2396	finally
        //   722	729	2396	finally
        //   749	756	2396	finally
        //   776	787	2396	finally
        //   820	825	2396	finally
        //   845	856	2396	finally
        //   876	884	2396	finally
        //   919	929	2396	finally
        //   949	956	2396	finally
        //   976	983	2396	finally
        //   1003	1010	2396	finally
        //   1030	1042	2396	finally
        //   1075	1080	2396	finally
        //   1100	1111	2396	finally
        //   1131	1140	2396	finally
        //   1175	1185	2396	finally
        //   1205	1212	2396	finally
        //   1232	1239	2396	finally
        //   1259	1266	2396	finally
        //   1307	1314	2396	finally
        //   1339	1344	2396	finally
        //   2382	2389	2396	finally
        //   2446	2453	2464	java/lang/Exception
        //   1365	1372	2482	java/lang/Exception
        //   2407	2414	2500	java/lang/Exception
        //   2545	2552	2601	java/lang/Exception
        //   1404	1412	2619	java/lang/Throwable
        //   1448	1456	2619	java/lang/Throwable
        //   1492	1503	2619	java/lang/Throwable
        //   1527	1548	2619	java/lang/Throwable
        //   1589	1600	2619	java/lang/Throwable
        //   1624	1635	2619	java/lang/Throwable
        //   1659	1670	2619	java/lang/Throwable
        //   1694	1705	2619	java/lang/Throwable
        //   1729	1740	2619	java/lang/Throwable
        //   1764	1775	2619	java/lang/Throwable
        //   1811	1821	2619	java/lang/Throwable
        //   1845	1856	2619	java/lang/Throwable
        //   1880	1891	2619	java/lang/Throwable
        //   1915	1926	2619	java/lang/Throwable
        //   1950	1961	2619	java/lang/Throwable
        //   1985	1996	2619	java/lang/Throwable
        //   2020	2031	2619	java/lang/Throwable
        //   2060	2068	2619	java/lang/Throwable
        //   2092	2097	2619	java/lang/Throwable
        //   2121	2142	2619	java/lang/Throwable
        //   2171	2176	2619	java/lang/Throwable
        //   2200	2215	2619	java/lang/Throwable
        //   2659	2666	2677	java/lang/Exception
        //   1404	1412	2695	finally
        //   1448	1456	2695	finally
        //   1492	1503	2695	finally
        //   1527	1548	2695	finally
        //   1589	1600	2695	finally
        //   1624	1635	2695	finally
        //   1659	1670	2695	finally
        //   1694	1705	2695	finally
        //   1729	1740	2695	finally
        //   1764	1775	2695	finally
        //   1811	1821	2695	finally
        //   1845	1856	2695	finally
        //   1880	1891	2695	finally
        //   1915	1926	2695	finally
        //   1950	1961	2695	finally
        //   1985	1996	2695	finally
        //   2020	2031	2695	finally
        //   2060	2068	2695	finally
        //   2092	2097	2695	finally
        //   2121	2142	2695	finally
        //   2171	2176	2695	finally
        //   2200	2215	2695	finally
        //   2706	2713	2716	java/lang/Exception
        //   2219	2226	2743	finally
        //   2234	2241	2743	finally
        //   2245	2256	2743	finally
        //   2269	2274	2743	finally
        //   2278	2289	2743	finally
        //   2293	2301	2743	finally
        //   2320	2330	2743	finally
        //   2334	2341	2743	finally
        //   2514	2521	2743	finally
        //   2352	2359	2748	finally
        //   2641	2646	2748	finally
        //   2219	2226	2757	java/lang/Throwable
        //   2234	2241	2757	java/lang/Throwable
        //   2245	2256	2757	java/lang/Throwable
        //   2269	2274	2757	java/lang/Throwable
        //   2278	2289	2757	java/lang/Throwable
        //   2293	2301	2757	java/lang/Throwable
        //   2320	2330	2757	java/lang/Throwable
        //   2334	2341	2757	java/lang/Throwable
        //   2514	2521	2757	java/lang/Throwable
        //   2352	2359	2766	java/lang/Throwable
        //   488	564	2771	finally
        //   569	598	2771	finally
        //   603	623	2771	finally
        //   627	634	2776	finally
        //   647	652	2776	finally
        //   656	671	2776	finally
        //   488	564	2781	java/lang/Throwable
        //   569	598	2781	java/lang/Throwable
        //   603	623	2781	java/lang/Throwable
        //   627	634	2798	java/lang/Throwable
        //   647	652	2798	java/lang/Throwable
        //   656	671	2798	java/lang/Throwable
      }
    });
    localThread.setPriority(1);
    localThread.start();
  }
  
  private native int openOpusFile(String paramString);
  
  private void playNextMessageWithoutOrder(boolean paramBoolean)
  {
    ArrayList localArrayList;
    if (SharedConfig.shuffleMusic)
    {
      localArrayList = this.shuffledPlaylist;
      if ((!paramBoolean) || (SharedConfig.repeatMode != 2) || (this.forceLoopCurrentPlaylist)) {
        break label60;
      }
      cleanupPlayer(false, false);
      playMessage((MessageObject)localArrayList.get(this.currentPlaylistNum));
    }
    for (;;)
    {
      return;
      localArrayList = this.playlist;
      break;
      label60:
      int i = 0;
      if (SharedConfig.playOrderReversed)
      {
        this.currentPlaylistNum += 1;
        if (this.currentPlaylistNum >= localArrayList.size())
        {
          this.currentPlaylistNum = 0;
          i = 1;
        }
        label96:
        if ((i == 0) || (!paramBoolean) || (SharedConfig.repeatMode != 0) || (this.forceLoopCurrentPlaylist)) {
          break label443;
        }
        if ((this.audioPlayer == null) && (this.audioTrackPlayer == null) && (this.videoPlayer == null)) {
          continue;
        }
        if (this.audioPlayer == null) {
          break label308;
        }
      }
      for (;;)
      {
        try
        {
          this.audioPlayer.releasePlayer();
          this.audioPlayer = null;
          stopProgressTimer();
          this.lastProgress = 0L;
          this.buffersWrited = 0;
          this.isPaused = true;
          this.playingMessageObject.audioProgress = 0.0F;
          this.playingMessageObject.audioProgressSec = 0;
          NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0) });
          NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
          break;
          this.currentPlaylistNum -= 1;
          if (this.currentPlaylistNum >= 0) {
            break label96;
          }
          this.currentPlaylistNum = (localArrayList.size() - 1);
          i = 1;
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
          continue;
        }
        label308:
        if (this.audioTrackPlayer != null)
        {
          try
          {
            synchronized (this.playerObjectSync)
            {
              this.audioTrackPlayer.pause();
              this.audioTrackPlayer.flush();
            }
          }
          catch (Exception localException3)
          {
            try
            {
              for (;;)
              {
                this.audioTrackPlayer.release();
                this.audioTrackPlayer = null;
                break;
                localObject2 = finally;
                throw ((Throwable)localObject2);
                localException3 = localException3;
                FileLog.e(localException3);
              }
            }
            catch (Exception localException4)
            {
              for (;;)
              {
                FileLog.e(localException4);
              }
            }
          }
        }
        else if (this.videoPlayer != null)
        {
          this.currentAspectRatioFrameLayout = null;
          this.currentTextureViewContainer = null;
          this.currentAspectRatioFrameLayoutReady = false;
          this.currentTextureView = null;
          this.videoPlayer.releasePlayer();
          this.videoPlayer = null;
          try
          {
            this.baseActivity.getWindow().clearFlags(128);
          }
          catch (Exception localException2)
          {
            FileLog.e(localException2);
          }
        }
      }
      label443:
      if ((this.currentPlaylistNum >= 0) && (this.currentPlaylistNum < localException2.size()))
      {
        if (this.playingMessageObject != null) {
          this.playingMessageObject.resetPlayingProgress();
        }
        this.playMusicAgain = true;
        playMessage((MessageObject)localException2.get(this.currentPlaylistNum));
      }
    }
  }
  
  private void processMediaObserver(Uri paramUri)
  {
    Object localObject1;
    Cursor localCursor;
    try
    {
      localObject1 = AndroidUtilities.getRealScreenSize();
      localCursor = ApplicationLoader.applicationContext.getContentResolver().query(paramUri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
      paramUri = new java/util/ArrayList;
      paramUri.<init>();
      if (localCursor != null) {
        while (localCursor.moveToNext())
        {
          String str1 = localCursor.getString(0);
          Object localObject2 = localCursor.getString(1);
          String str2 = localCursor.getString(2);
          long l = localCursor.getLong(3);
          String str3 = localCursor.getString(4);
          int i = localCursor.getInt(5);
          int j = localCursor.getInt(6);
          if (((str1 == null) || (!str1.toLowerCase().contains("screenshot"))) && ((localObject2 == null) || (!((String)localObject2).toLowerCase().contains("screenshot"))) && ((str2 == null) || (!str2.toLowerCase().contains("screenshot"))))
          {
            if (str3 != null)
            {
              boolean bool = str3.toLowerCase().contains("screenshot");
              if (!bool) {
                break;
              }
            }
          }
          else
          {
            int k;
            if (i != 0)
            {
              k = j;
              if (j != 0) {
                break label241;
              }
            }
            try
            {
              localObject2 = new android/graphics/BitmapFactory$Options;
              ((BitmapFactory.Options)localObject2).<init>();
              ((BitmapFactory.Options)localObject2).inJustDecodeBounds = true;
              BitmapFactory.decodeFile(str1, (BitmapFactory.Options)localObject2);
              i = ((BitmapFactory.Options)localObject2).outWidth;
              k = ((BitmapFactory.Options)localObject2).outHeight;
              label241:
              if ((i > 0) && (k > 0) && ((i != ((android.graphics.Point)localObject1).x) || (k != ((android.graphics.Point)localObject1).y)) && ((k != ((android.graphics.Point)localObject1).x) || (i != ((android.graphics.Point)localObject1).y))) {
                continue;
              }
              paramUri.add(Long.valueOf(l));
            }
            catch (Exception localException)
            {
              paramUri.add(Long.valueOf(l));
            }
            continue;
            return;
          }
        }
      }
    }
    catch (Exception paramUri)
    {
      FileLog.e(paramUri);
    }
    for (;;)
    {
      localCursor.close();
      if (!paramUri.isEmpty())
      {
        localObject1 = new org/telegram/messenger/MediaController$8;
        ((8)localObject1).<init>(this, paramUri);
        AndroidUtilities.runOnUIThread((Runnable)localObject1);
      }
    }
  }
  
  private long readAndWriteTracks(MessageObject paramMessageObject, MediaExtractor paramMediaExtractor, MP4Builder paramMP4Builder, MediaCodec.BufferInfo paramBufferInfo, long paramLong1, long paramLong2, File paramFile, boolean paramBoolean)
    throws Exception
  {
    int i = findTrack(paramMediaExtractor, false);
    int j;
    int m;
    int n;
    int i1;
    Object localObject;
    label85:
    int i2;
    label146:
    long l1;
    int i3;
    int i4;
    label216:
    int i6;
    if (paramBoolean)
    {
      j = findTrack(paramMediaExtractor, true);
      int k = -1;
      m = -1;
      n = 0;
      i1 = 0;
      if (i >= 0)
      {
        paramMediaExtractor.selectTrack(i);
        localObject = paramMediaExtractor.getTrackFormat(i);
        k = paramMP4Builder.addTrack((MediaFormat)localObject, false);
        i1 = ((MediaFormat)localObject).getInteger("max-input-size");
        if (paramLong1 <= 0L) {
          break label447;
        }
        paramMediaExtractor.seekTo(paramLong1, 0);
      }
      i2 = i1;
      if (j >= 0)
      {
        paramMediaExtractor.selectTrack(j);
        localObject = paramMediaExtractor.getTrackFormat(j);
        m = paramMP4Builder.addTrack((MediaFormat)localObject, true);
        i2 = Math.max(((MediaFormat)localObject).getInteger("max-input-size"), i1);
        if (paramLong1 <= 0L) {
          break label456;
        }
        paramMediaExtractor.seekTo(paramLong1, 0);
      }
      localObject = ByteBuffer.allocateDirect(i2);
      if ((j < 0) && (i < 0)) {
        break label794;
      }
      l1 = -1L;
      checkConversionCanceled();
      if (n != 0) {
        break label761;
      }
      checkConversionCanceled();
      i1 = 0;
      i3 = 0;
      paramBufferInfo.size = paramMediaExtractor.readSampleData((ByteBuffer)localObject, 0);
      i4 = paramMediaExtractor.getSampleTrackIndex();
      if (i4 != i) {
        break label465;
      }
      i2 = k;
      if (i2 == -1) {
        break label729;
      }
      if (Build.VERSION.SDK_INT < 21)
      {
        ((ByteBuffer)localObject).position(0);
        ((ByteBuffer)localObject).limit(paramBufferInfo.size);
      }
      if (i4 == j) {
        break label498;
      }
      byte[] arrayOfByte = ((ByteBuffer)localObject).array();
      if (arrayOfByte == null) {
        break label498;
      }
      i1 = ((ByteBuffer)localObject).arrayOffset();
      int i5 = i1 + ((ByteBuffer)localObject).limit();
      i6 = -1;
      label287:
      if (i1 > i5 - 4) {
        break label498;
      }
      if ((arrayOfByte[i1] != 0) || (arrayOfByte[(i1 + 1)] != 0) || (arrayOfByte[(i1 + 2)] != 0) || (arrayOfByte[(i1 + 3)] != 1))
      {
        i7 = i6;
        if (i1 != i5 - 4) {}
      }
      else
      {
        if (i6 == -1) {
          break label491;
        }
        if (i1 == i5 - 4) {
          break label485;
        }
        i7 = 4;
        label366:
        i7 = i1 - i6 - i7;
        arrayOfByte[i6] = ((byte)(byte)(i7 >> 24));
        arrayOfByte[(i6 + 1)] = ((byte)(byte)(i7 >> 16));
        arrayOfByte[(i6 + 2)] = ((byte)(byte)(i7 >> 8));
        arrayOfByte[(i6 + 3)] = ((byte)(byte)i7);
      }
    }
    label447:
    label456:
    label465:
    label485:
    label491:
    for (int i7 = i1;; i7 = i1)
    {
      i1++;
      i6 = i7;
      break label287;
      j = -1;
      break;
      paramMediaExtractor.seekTo(0L, 0);
      break label85;
      paramMediaExtractor.seekTo(0L, 0);
      break label146;
      if (i4 == j)
      {
        i2 = m;
        break label216;
      }
      i2 = -1;
      break label216;
      i7 = 0;
      break label366;
    }
    label498:
    label519:
    long l2;
    long l3;
    if (paramBufferInfo.size >= 0)
    {
      paramBufferInfo.presentationTimeUs = paramMediaExtractor.getSampleTime();
      i7 = i3;
      i1 = i7;
      l2 = l1;
      if (paramBufferInfo.size > 0)
      {
        i1 = i7;
        l2 = l1;
        if (i7 == 0)
        {
          l3 = l1;
          if (i4 == i)
          {
            l3 = l1;
            if (paramLong1 > 0L)
            {
              l3 = l1;
              if (l1 == -1L) {
                l3 = paramBufferInfo.presentationTimeUs;
              }
            }
          }
          if ((paramLong2 >= 0L) && (paramBufferInfo.presentationTimeUs >= paramLong2)) {
            break label719;
          }
          paramBufferInfo.offset = 0;
          paramBufferInfo.flags = paramMediaExtractor.getSampleFlags();
          i1 = i7;
          l2 = l3;
          if (paramMP4Builder.writeSampleData(i2, (ByteBuffer)localObject, paramBufferInfo, false))
          {
            didWriteData(paramMessageObject, paramFile, false, false);
            l2 = l3;
            i1 = i7;
          }
        }
      }
      label662:
      i2 = i1;
      l3 = l2;
      if (i1 == 0)
      {
        paramMediaExtractor.advance();
        l3 = l2;
        i2 = i1;
      }
    }
    for (;;)
    {
      l1 = l3;
      if (i2 == 0) {
        break;
      }
      n = 1;
      l1 = l3;
      break;
      paramBufferInfo.size = 0;
      i7 = 1;
      break label519;
      label719:
      i1 = 1;
      l2 = l3;
      break label662;
      label729:
      if (i4 == -1)
      {
        i2 = 1;
        l3 = l1;
      }
      else
      {
        paramMediaExtractor.advance();
        i2 = i1;
        l3 = l1;
      }
    }
    label761:
    if (i >= 0) {
      paramMediaExtractor.unselectTrack(i);
    }
    paramLong1 = l1;
    if (j >= 0) {
      paramMediaExtractor.unselectTrack(j);
    }
    label794:
    for (paramLong1 = l1;; paramLong1 = -1L) {
      return paramLong1;
    }
  }
  
  private native void readOpusFile(ByteBuffer paramByteBuffer, int paramInt, int[] paramArrayOfInt);
  
  private void readSms() {}
  
  public static void saveFile(final String paramString1, Context paramContext, int paramInt, final String paramString2, final String paramString3)
  {
    if (paramString1 == null) {}
    final Object localObject2;
    final boolean[] arrayOfBoolean;
    do
    {
      do
      {
        return;
        localObject1 = null;
        localObject2 = localObject1;
        if (paramString1 != null)
        {
          localObject2 = localObject1;
          if (paramString1.length() != 0)
          {
            paramString1 = new File(paramString1);
            localObject2 = paramString1;
            if (!paramString1.exists()) {
              localObject2 = null;
            }
          }
        }
      } while (localObject2 == null);
      arrayOfBoolean = new boolean[1];
      arrayOfBoolean[0] = false;
    } while (!((File)localObject2).exists());
    Object localObject1 = null;
    Object localObject3 = null;
    paramString1 = (String)localObject3;
    if (paramContext != null)
    {
      paramString1 = (String)localObject3;
      if (paramInt == 0) {}
    }
    for (;;)
    {
      try
      {
        paramString1 = new org/telegram/ui/ActionBar/AlertDialog;
        paramString1.<init>(paramContext, 2);
      }
      catch (Exception paramContext)
      {
        try
        {
          paramString1.setMessage(LocaleController.getString("Loading", NUM));
          paramString1.setCanceledOnTouchOutside(false);
          paramString1.setCancelable(true);
          paramContext = new org/telegram/messenger/MediaController$27;
          paramContext.<init>(arrayOfBoolean);
          paramString1.setOnCancelListener(paramContext);
          paramString1.show();
          new Thread(new Runnable()
          {
            /* Error */
            public void run()
            {
              // Byte code:
              //   0: aload_0
              //   1: getfield 30	org/telegram/messenger/MediaController$28:val$type	I
              //   4: ifne +334 -> 338
              //   7: invokestatic 53	org/telegram/messenger/AndroidUtilities:generatePicturePath	()Ljava/io/File;
              //   10: astore_1
              //   11: aload_1
              //   12: invokevirtual 59	java/io/File:exists	()Z
              //   15: ifne +8 -> 23
              //   18: aload_1
              //   19: invokevirtual 62	java/io/File:createNewFile	()Z
              //   22: pop
              //   23: aconst_null
              //   24: astore_2
              //   25: aconst_null
              //   26: astore_3
              //   27: aconst_null
              //   28: astore 4
              //   30: aconst_null
              //   31: astore 5
              //   33: iconst_1
              //   34: istore 6
              //   36: invokestatic 68	java/lang/System:currentTimeMillis	()J
              //   39: lstore 7
              //   41: lload 7
              //   43: ldc2_w 69
              //   46: lsub
              //   47: lstore 9
              //   49: aload 5
              //   51: astore 11
              //   53: aload_3
              //   54: astore 12
              //   56: aload 4
              //   58: astore 13
              //   60: aload_2
              //   61: astore 14
              //   63: new 72	java/io/FileInputStream
              //   66: astore 15
              //   68: aload 5
              //   70: astore 11
              //   72: aload_3
              //   73: astore 12
              //   75: aload 4
              //   77: astore 13
              //   79: aload_2
              //   80: astore 14
              //   82: aload 15
              //   84: aload_0
              //   85: getfield 34	org/telegram/messenger/MediaController$28:val$sourceFile	Ljava/io/File;
              //   88: invokespecial 75	java/io/FileInputStream:<init>	(Ljava/io/File;)V
              //   91: aload 5
              //   93: astore 11
              //   95: aload_3
              //   96: astore 12
              //   98: aload 4
              //   100: astore 13
              //   102: aload_2
              //   103: astore 14
              //   105: aload 15
              //   107: invokevirtual 79	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
              //   110: astore_3
              //   111: aload 5
              //   113: astore 11
              //   115: aload_3
              //   116: astore 12
              //   118: aload 4
              //   120: astore 13
              //   122: aload_3
              //   123: astore 14
              //   125: new 81	java/io/FileOutputStream
              //   128: astore_2
              //   129: aload 5
              //   131: astore 11
              //   133: aload_3
              //   134: astore 12
              //   136: aload 4
              //   138: astore 13
              //   140: aload_3
              //   141: astore 14
              //   143: aload_2
              //   144: aload_1
              //   145: invokespecial 82	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
              //   148: aload 5
              //   150: astore 11
              //   152: aload_3
              //   153: astore 12
              //   155: aload 4
              //   157: astore 13
              //   159: aload_3
              //   160: astore 14
              //   162: aload_2
              //   163: invokevirtual 83	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
              //   166: astore 5
              //   168: aload 5
              //   170: astore 11
              //   172: aload_3
              //   173: astore 12
              //   175: aload 5
              //   177: astore 13
              //   179: aload_3
              //   180: astore 14
              //   182: aload_3
              //   183: invokevirtual 88	java/nio/channels/FileChannel:size	()J
              //   186: lstore 16
              //   188: lconst_0
              //   189: lstore 18
              //   191: lload 18
              //   193: lload 16
              //   195: lcmp
              //   196: ifge +30 -> 226
              //   199: aload 5
              //   201: astore 11
              //   203: aload_3
              //   204: astore 12
              //   206: aload 5
              //   208: astore 13
              //   210: aload_3
              //   211: astore 14
              //   213: aload_0
              //   214: getfield 36	org/telegram/messenger/MediaController$28:val$cancelled	[Z
              //   217: iconst_0
              //   218: baload
              //   219: istore 20
              //   221: iload 20
              //   223: ifeq +348 -> 571
              //   226: aload_3
              //   227: ifnull +7 -> 234
              //   230: aload_3
              //   231: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   234: iload 6
              //   236: istore 21
              //   238: aload 5
              //   240: ifnull +12 -> 252
              //   243: aload 5
              //   245: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   248: iload 6
              //   250: istore 21
              //   252: aload_0
              //   253: getfield 36	org/telegram/messenger/MediaController$28:val$cancelled	[Z
              //   256: iconst_0
              //   257: baload
              //   258: ifeq +11 -> 269
              //   261: aload_1
              //   262: invokevirtual 94	java/io/File:delete	()Z
              //   265: pop
              //   266: iconst_0
              //   267: istore 21
              //   269: iload 21
              //   271: ifeq +48 -> 319
              //   274: aload_0
              //   275: getfield 30	org/telegram/messenger/MediaController$28:val$type	I
              //   278: iconst_2
              //   279: if_icmpne +576 -> 855
              //   282: getstatic 100	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
              //   285: ldc 102
              //   287: invokevirtual 108	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
              //   290: checkcast 110	android/app/DownloadManager
              //   293: aload_1
              //   294: invokevirtual 114	java/io/File:getName	()Ljava/lang/String;
              //   297: aload_1
              //   298: invokevirtual 114	java/io/File:getName	()Ljava/lang/String;
              //   301: iconst_0
              //   302: aload_0
              //   303: getfield 40	org/telegram/messenger/MediaController$28:val$mime	Ljava/lang/String;
              //   306: aload_1
              //   307: invokevirtual 117	java/io/File:getAbsolutePath	()Ljava/lang/String;
              //   310: aload_1
              //   311: invokevirtual 120	java/io/File:length	()J
              //   314: iconst_1
              //   315: invokevirtual 124	android/app/DownloadManager:addCompletedDownload	(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;JZ)J
              //   318: pop2
              //   319: aload_0
              //   320: getfield 38	org/telegram/messenger/MediaController$28:val$finalProgress	Lorg/telegram/ui/ActionBar/AlertDialog;
              //   323: ifnull +14 -> 337
              //   326: new 15	org/telegram/messenger/MediaController$28$2
              //   329: dup
              //   330: aload_0
              //   331: invokespecial 127	org/telegram/messenger/MediaController$28$2:<init>	(Lorg/telegram/messenger/MediaController$28;)V
              //   334: invokestatic 131	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
              //   337: return
              //   338: aload_0
              //   339: getfield 30	org/telegram/messenger/MediaController$28:val$type	I
              //   342: iconst_1
              //   343: if_icmpne +10 -> 353
              //   346: invokestatic 134	org/telegram/messenger/AndroidUtilities:generateVideoPath	()Ljava/io/File;
              //   349: astore_1
              //   350: goto -339 -> 11
              //   353: aload_0
              //   354: getfield 30	org/telegram/messenger/MediaController$28:val$type	I
              //   357: iconst_2
              //   358: if_icmpne +162 -> 520
              //   361: getstatic 139	android/os/Environment:DIRECTORY_DOWNLOADS	Ljava/lang/String;
              //   364: invokestatic 143	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
              //   367: astore 13
              //   369: aload 13
              //   371: invokevirtual 146	java/io/File:mkdir	()Z
              //   374: pop
              //   375: new 55	java/io/File
              //   378: astore 14
              //   380: aload 14
              //   382: aload 13
              //   384: aload_0
              //   385: getfield 32	org/telegram/messenger/MediaController$28:val$name	Ljava/lang/String;
              //   388: invokespecial 149	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
              //   391: aload 14
              //   393: astore_1
              //   394: aload 14
              //   396: invokevirtual 59	java/io/File:exists	()Z
              //   399: ifeq -388 -> 11
              //   402: aload_0
              //   403: getfield 32	org/telegram/messenger/MediaController$28:val$name	Ljava/lang/String;
              //   406: bipush 46
              //   408: invokevirtual 155	java/lang/String:lastIndexOf	(I)I
              //   411: istore 6
              //   413: iconst_0
              //   414: istore 21
              //   416: aload 14
              //   418: astore_1
              //   419: iload 21
              //   421: bipush 10
              //   423: if_icmpge -412 -> 11
              //   426: iload 6
              //   428: iconst_m1
              //   429: if_icmpeq +102 -> 531
              //   432: new 157	java/lang/StringBuilder
              //   435: astore_1
              //   436: aload_1
              //   437: invokespecial 158	java/lang/StringBuilder:<init>	()V
              //   440: aload_1
              //   441: aload_0
              //   442: getfield 32	org/telegram/messenger/MediaController$28:val$name	Ljava/lang/String;
              //   445: iconst_0
              //   446: iload 6
              //   448: invokevirtual 162	java/lang/String:substring	(II)Ljava/lang/String;
              //   451: invokevirtual 166	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   454: ldc -88
              //   456: invokevirtual 166	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   459: iload 21
              //   461: iconst_1
              //   462: iadd
              //   463: invokevirtual 171	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
              //   466: ldc -83
              //   468: invokevirtual 166	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   471: aload_0
              //   472: getfield 32	org/telegram/messenger/MediaController$28:val$name	Ljava/lang/String;
              //   475: iload 6
              //   477: invokevirtual 176	java/lang/String:substring	(I)Ljava/lang/String;
              //   480: invokevirtual 166	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   483: invokevirtual 179	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   486: astore_1
              //   487: new 55	java/io/File
              //   490: astore 14
              //   492: aload 14
              //   494: aload 13
              //   496: aload_1
              //   497: invokespecial 149	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
              //   500: aload 14
              //   502: astore_1
              //   503: aload 14
              //   505: invokevirtual 59	java/io/File:exists	()Z
              //   508: ifeq -497 -> 11
              //   511: iinc 21 1
              //   514: aload 14
              //   516: astore_1
              //   517: goto -98 -> 419
              //   520: getstatic 182	android/os/Environment:DIRECTORY_MUSIC	Ljava/lang/String;
              //   523: invokestatic 143	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
              //   526: astore 13
              //   528: goto -159 -> 369
              //   531: new 157	java/lang/StringBuilder
              //   534: astore_1
              //   535: aload_1
              //   536: invokespecial 158	java/lang/StringBuilder:<init>	()V
              //   539: aload_1
              //   540: aload_0
              //   541: getfield 32	org/telegram/messenger/MediaController$28:val$name	Ljava/lang/String;
              //   544: invokevirtual 166	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   547: ldc -88
              //   549: invokevirtual 166	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   552: iload 21
              //   554: iconst_1
              //   555: iadd
              //   556: invokevirtual 171	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
              //   559: ldc -83
              //   561: invokevirtual 166	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   564: invokevirtual 179	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   567: astore_1
              //   568: goto -81 -> 487
              //   571: aload 5
              //   573: astore 11
              //   575: aload_3
              //   576: astore 12
              //   578: aload 5
              //   580: astore 13
              //   582: aload_3
              //   583: astore 14
              //   585: aload 5
              //   587: aload_3
              //   588: lload 18
              //   590: ldc2_w 183
              //   593: lload 16
              //   595: lload 18
              //   597: lsub
              //   598: invokestatic 190	java/lang/Math:min	(JJ)J
              //   601: invokevirtual 194	java/nio/channels/FileChannel:transferFrom	(Ljava/nio/channels/ReadableByteChannel;JJ)J
              //   604: pop2
              //   605: lload 9
              //   607: lstore 7
              //   609: aload 5
              //   611: astore 11
              //   613: aload_3
              //   614: astore 12
              //   616: aload 5
              //   618: astore 13
              //   620: aload_3
              //   621: astore 14
              //   623: aload_0
              //   624: getfield 38	org/telegram/messenger/MediaController$28:val$finalProgress	Lorg/telegram/ui/ActionBar/AlertDialog;
              //   627: ifnull +126 -> 753
              //   630: lload 9
              //   632: lstore 7
              //   634: aload 5
              //   636: astore 11
              //   638: aload_3
              //   639: astore 12
              //   641: aload 5
              //   643: astore 13
              //   645: aload_3
              //   646: astore 14
              //   648: lload 9
              //   650: invokestatic 68	java/lang/System:currentTimeMillis	()J
              //   653: ldc2_w 69
              //   656: lsub
              //   657: lcmp
              //   658: ifgt +95 -> 753
              //   661: aload 5
              //   663: astore 11
              //   665: aload_3
              //   666: astore 12
              //   668: aload 5
              //   670: astore 13
              //   672: aload_3
              //   673: astore 14
              //   675: invokestatic 68	java/lang/System:currentTimeMillis	()J
              //   678: lstore 7
              //   680: lload 18
              //   682: l2f
              //   683: lload 16
              //   685: l2f
              //   686: fdiv
              //   687: ldc -61
              //   689: fmul
              //   690: f2i
              //   691: istore 21
              //   693: aload 5
              //   695: astore 11
              //   697: aload_3
              //   698: astore 12
              //   700: aload 5
              //   702: astore 13
              //   704: aload_3
              //   705: astore 14
              //   707: new 13	org/telegram/messenger/MediaController$28$1
              //   710: astore 4
              //   712: aload 5
              //   714: astore 11
              //   716: aload_3
              //   717: astore 12
              //   719: aload 5
              //   721: astore 13
              //   723: aload_3
              //   724: astore 14
              //   726: aload 4
              //   728: aload_0
              //   729: iload 21
              //   731: invokespecial 198	org/telegram/messenger/MediaController$28$1:<init>	(Lorg/telegram/messenger/MediaController$28;I)V
              //   734: aload 5
              //   736: astore 11
              //   738: aload_3
              //   739: astore 12
              //   741: aload 5
              //   743: astore 13
              //   745: aload_3
              //   746: astore 14
              //   748: aload 4
              //   750: invokestatic 131	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
              //   753: lload 18
              //   755: ldc2_w 183
              //   758: ladd
              //   759: lstore 18
              //   761: lload 7
              //   763: lstore 9
              //   765: goto -574 -> 191
              //   768: astore_3
              //   769: aload 11
              //   771: astore 13
              //   773: aload 12
              //   775: astore 14
              //   777: aload_3
              //   778: invokestatic 204	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
              //   781: iconst_0
              //   782: istore 6
              //   784: aload 12
              //   786: ifnull +8 -> 794
              //   789: aload 12
              //   791: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   794: iload 6
              //   796: istore 21
              //   798: aload 11
              //   800: ifnull -548 -> 252
              //   803: aload 11
              //   805: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   808: iload 6
              //   810: istore 21
              //   812: goto -560 -> 252
              //   815: astore 13
              //   817: iload 6
              //   819: istore 21
              //   821: goto -569 -> 252
              //   824: astore_1
              //   825: aload 14
              //   827: ifnull +8 -> 835
              //   830: aload 14
              //   832: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   835: aload 13
              //   837: ifnull +8 -> 845
              //   840: aload 13
              //   842: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   845: aload_1
              //   846: athrow
              //   847: astore_1
              //   848: aload_1
              //   849: invokestatic 204	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
              //   852: goto -533 -> 319
              //   855: aload_1
              //   856: invokestatic 210	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
              //   859: invokestatic 214	org/telegram/messenger/AndroidUtilities:addMediaToGallery	(Landroid/net/Uri;)V
              //   862: goto -543 -> 319
              //   865: astore 13
              //   867: goto -633 -> 234
              //   870: astore 13
              //   872: iload 6
              //   874: istore 21
              //   876: goto -624 -> 252
              //   879: astore 13
              //   881: goto -87 -> 794
              //   884: astore 14
              //   886: goto -51 -> 835
              //   889: astore 13
              //   891: goto -46 -> 845
              // Local variable table:
              //   start	length	slot	name	signature
              //   0	894	0	this	28
              //   10	558	1	localObject1	Object
              //   824	22	1	localObject2	Object
              //   847	9	1	localException1	Exception
              //   24	139	2	localFileOutputStream	java.io.FileOutputStream
              //   26	720	3	localFileChannel1	java.nio.channels.FileChannel
              //   768	10	3	localException2	Exception
              //   28	721	4	local1	1
              //   31	711	5	localFileChannel2	java.nio.channels.FileChannel
              //   34	839	6	i	int
              //   39	723	7	l1	long
              //   47	717	9	l2	long
              //   51	753	11	localFileChannel3	java.nio.channels.FileChannel
              //   54	736	12	localFileChannel4	java.nio.channels.FileChannel
              //   58	714	13	localObject3	Object
              //   815	26	13	localException3	Exception
              //   865	1	13	localException4	Exception
              //   870	1	13	localException5	Exception
              //   879	1	13	localException6	Exception
              //   889	1	13	localException7	Exception
              //   61	770	14	localObject4	Object
              //   884	1	14	localException8	Exception
              //   66	40	15	localFileInputStream	java.io.FileInputStream
              //   186	498	16	l3	long
              //   189	571	18	l4	long
              //   219	3	20	j	int
              //   236	639	21	k	int
              // Exception table:
              //   from	to	target	type
              //   63	68	768	java/lang/Exception
              //   82	91	768	java/lang/Exception
              //   105	111	768	java/lang/Exception
              //   125	129	768	java/lang/Exception
              //   143	148	768	java/lang/Exception
              //   162	168	768	java/lang/Exception
              //   182	188	768	java/lang/Exception
              //   213	221	768	java/lang/Exception
              //   585	605	768	java/lang/Exception
              //   623	630	768	java/lang/Exception
              //   648	661	768	java/lang/Exception
              //   675	680	768	java/lang/Exception
              //   707	712	768	java/lang/Exception
              //   726	734	768	java/lang/Exception
              //   748	753	768	java/lang/Exception
              //   803	808	815	java/lang/Exception
              //   63	68	824	finally
              //   82	91	824	finally
              //   105	111	824	finally
              //   125	129	824	finally
              //   143	148	824	finally
              //   162	168	824	finally
              //   182	188	824	finally
              //   213	221	824	finally
              //   585	605	824	finally
              //   623	630	824	finally
              //   648	661	824	finally
              //   675	680	824	finally
              //   707	712	824	finally
              //   726	734	824	finally
              //   748	753	824	finally
              //   777	781	824	finally
              //   0	11	847	java/lang/Exception
              //   11	23	847	java/lang/Exception
              //   36	41	847	java/lang/Exception
              //   252	266	847	java/lang/Exception
              //   274	319	847	java/lang/Exception
              //   338	350	847	java/lang/Exception
              //   353	369	847	java/lang/Exception
              //   369	391	847	java/lang/Exception
              //   394	413	847	java/lang/Exception
              //   432	487	847	java/lang/Exception
              //   487	500	847	java/lang/Exception
              //   503	511	847	java/lang/Exception
              //   520	528	847	java/lang/Exception
              //   531	568	847	java/lang/Exception
              //   845	847	847	java/lang/Exception
              //   855	862	847	java/lang/Exception
              //   230	234	865	java/lang/Exception
              //   243	248	870	java/lang/Exception
              //   789	794	879	java/lang/Exception
              //   830	835	884	java/lang/Exception
              //   840	845	889	java/lang/Exception
            }
          }).start();
        }
        catch (Exception paramContext)
        {
          for (;;) {}
        }
        paramContext = paramContext;
        paramString1 = (String)localObject1;
      }
      FileLog.e(paramContext);
    }
  }
  
  private native int seekOpusFile(float paramFloat);
  
  private void seekOpusPlayer(final float paramFloat)
  {
    if (paramFloat == 1.0F) {}
    for (;;)
    {
      return;
      if (!this.isPaused) {
        this.audioTrackPlayer.pause();
      }
      this.audioTrackPlayer.flush();
      this.fileDecodingQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MediaController.this.seekOpusFile(paramFloat);
          synchronized (MediaController.this.playerSync)
          {
            MediaController.this.freePlayerBuffers.addAll(MediaController.this.usedPlayerBuffers);
            MediaController.this.usedPlayerBuffers.clear();
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (!MediaController.this.isPaused)
                {
                  MediaController.access$3902(MediaController.this, 3);
                  MediaController.access$4102(MediaController.this, ((float)MediaController.this.currentTotalPcmDuration * MediaController.14.this.val$progress));
                  if (MediaController.this.audioTrackPlayer != null) {
                    MediaController.this.audioTrackPlayer.play();
                  }
                  MediaController.access$4302(MediaController.this, (int)((float)MediaController.this.currentTotalPcmDuration / 48.0F * MediaController.14.this.val$progress));
                  MediaController.this.checkPlayerQueue();
                }
              }
            });
            return;
          }
        }
      });
    }
  }
  
  @SuppressLint({"NewApi"})
  public static MediaCodecInfo selectCodec(String paramString)
  {
    int i = MediaCodecList.getCodecCount();
    Object localObject1 = null;
    int j = 0;
    Object localObject2;
    int m;
    String str;
    if (j < i)
    {
      MediaCodecInfo localMediaCodecInfo = MediaCodecList.getCodecInfoAt(j);
      if (!localMediaCodecInfo.isEncoder()) {
        localObject2 = localObject1;
      }
      String[] arrayOfString;
      int k;
      do
      {
        j++;
        localObject1 = localObject2;
        break;
        arrayOfString = localMediaCodecInfo.getSupportedTypes();
        k = arrayOfString.length;
        m = 0;
        localObject2 = localObject1;
      } while (m >= k);
      localObject2 = localObject1;
      if (arrayOfString[m].equalsIgnoreCase(paramString))
      {
        localObject1 = localMediaCodecInfo;
        str = ((MediaCodecInfo)localObject1).getName();
        localObject2 = localObject1;
        if (str != null) {
          if (!str.equals("OMX.SEC.avc.enc")) {
            paramString = (String)localObject1;
          }
        }
      }
    }
    for (;;)
    {
      return paramString;
      localObject2 = localObject1;
      if (str.equals("OMX.SEC.AVC.Encoder"))
      {
        paramString = (String)localObject1;
      }
      else
      {
        m++;
        localObject1 = localObject2;
        break;
        paramString = (String)localObject1;
      }
    }
  }
  
  @SuppressLint({"NewApi"})
  public static int selectColorFormat(MediaCodecInfo paramMediaCodecInfo, String paramString)
  {
    paramString = paramMediaCodecInfo.getCapabilitiesForType(paramString);
    int i = 0;
    int j = 0;
    if (j < paramString.colorFormats.length)
    {
      int k = paramString.colorFormats[j];
      if (isRecognizedFormat(k))
      {
        i = k;
        if ((!paramMediaCodecInfo.getName().equals("OMX.SEC.AVC.Encoder")) || (k != 19)) {
          i = k;
        }
      }
    }
    for (;;)
    {
      return i;
      j++;
      break;
    }
  }
  
  private void setPlayerVolume()
  {
    for (;;)
    {
      try
      {
        if (this.audioFocus != 1)
        {
          f = 1.0F;
          if (this.audioPlayer == null) {
            continue;
          }
          this.audioPlayer.setVolume(f);
          return;
        }
      }
      catch (Exception localException)
      {
        float f;
        FileLog.e(localException);
        continue;
        if (this.videoPlayer == null) {
          continue;
        }
        this.videoPlayer.setVolume(f);
        continue;
      }
      f = 0.2F;
      continue;
      if (this.audioTrackPlayer == null) {
        continue;
      }
      this.audioTrackPlayer.setStereoVolume(f, f);
    }
  }
  
  private void setUseFrontSpeaker(boolean paramBoolean)
  {
    this.useFrontSpeaker = paramBoolean;
    AudioManager localAudioManager = NotificationsController.audioManager;
    if (this.useFrontSpeaker)
    {
      localAudioManager.setBluetoothScoOn(false);
      localAudioManager.setSpeakerphoneOn(false);
    }
    for (;;)
    {
      return;
      localAudioManager.setSpeakerphoneOn(true);
    }
  }
  
  private void startAudioAgain(boolean paramBoolean)
  {
    int i = 0;
    if (this.playingMessageObject == null) {}
    for (;;)
    {
      return;
      NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.audioRouteChanged, new Object[] { Boolean.valueOf(this.useFrontSpeaker) });
      final Object localObject;
      if (this.videoPlayer != null)
      {
        localObject = this.videoPlayer;
        if (this.useFrontSpeaker) {}
        for (;;)
        {
          ((VideoPlayer)localObject).setStreamType(i);
          if (paramBoolean) {
            break label83;
          }
          this.videoPlayer.play();
          break;
          i = 3;
        }
        label83:
        this.videoPlayer.pause();
      }
      else
      {
        if (this.audioPlayer != null) {}
        for (i = 1;; i = 0)
        {
          localObject = this.playingMessageObject;
          float f = this.playingMessageObject.audioProgress;
          cleanupPlayer(false, true);
          ((MessageObject)localObject).audioProgress = f;
          playMessage((MessageObject)localObject);
          if (!paramBoolean) {
            break;
          }
          if (i == 0) {
            break label165;
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MediaController.this.pauseMessage(localObject);
            }
          }, 100L);
          break;
        }
        label165:
        pauseMessage((MessageObject)localObject);
      }
    }
  }
  
  private void startProgressTimer(MessageObject paramMessageObject)
  {
    synchronized (this.progressTimerSync)
    {
      Object localObject2 = this.progressTimer;
      if (localObject2 != null) {}
      try
      {
        this.progressTimer.cancel();
        this.progressTimer = null;
        paramMessageObject.getFileName();
        localObject2 = new java/util/Timer;
        ((Timer)localObject2).<init>();
        this.progressTimer = ((Timer)localObject2);
        Timer localTimer = this.progressTimer;
        localObject2 = new org/telegram/messenger/MediaController$6;
        ((6)localObject2).<init>(this, paramMessageObject);
        localTimer.schedule((TimerTask)localObject2, 0L, 17L);
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
  }
  
  private native int startRecord(String paramString);
  
  private boolean startVideoConvertFromQueue()
  {
    boolean bool = true;
    Intent localIntent;
    int i;
    if (!this.videoConvertQueue.isEmpty()) {
      synchronized (this.videoConvertSync)
      {
        this.cancelCurrentVideoConversion = false;
        ??? = (MessageObject)this.videoConvertQueue.get(0);
        localIntent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
        localIntent.putExtra("path", ((MessageObject)???).messageOwner.attachPath);
        localIntent.putExtra("currentAccount", ((MessageObject)???).currentAccount);
        if (((MessageObject)???).messageOwner.media.document != null)
        {
          i = 0;
          if (i < ((MessageObject)???).messageOwner.media.document.attributes.size())
          {
            if (!((TLRPC.DocumentAttribute)((MessageObject)???).messageOwner.media.document.attributes.get(i) instanceof TLRPC.TL_documentAttributeAnimated)) {
              break label178;
            }
            localIntent.putExtra("gif", true);
          }
        }
        if (((MessageObject)???).getId() == 0) {}
      }
    }
    for (;;)
    {
      try
      {
        ApplicationLoader.applicationContext.startService(localIntent);
        VideoConvertRunnable.runConversion((MessageObject)???);
        return bool;
        localObject2 = finally;
        throw ((Throwable)localObject2);
        label178:
        i++;
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
        continue;
      }
      bool = false;
    }
  }
  
  private void stopProgressTimer()
  {
    synchronized (this.progressTimerSync)
    {
      Timer localTimer = this.progressTimer;
      if (localTimer != null) {}
      try
      {
        this.progressTimer.cancel();
        this.progressTimer = null;
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
  }
  
  private native void stopRecord();
  
  private void stopRecordingInternal(final int paramInt)
  {
    if (paramInt != 0)
    {
      final TLRPC.TL_document localTL_document = this.recordingAudio;
      final File localFile = this.recordingAudioFile;
      this.fileEncodingQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MediaController.this.stopRecord();
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MediaController.25.this.val$audioToSend.date = ConnectionsManager.getInstance(MediaController.this.recordingCurrentAccount).getCurrentTime();
              MediaController.25.this.val$audioToSend.size = ((int)MediaController.25.this.val$recordingAudioFileToSend.length());
              Object localObject = new TLRPC.TL_documentAttributeAudio();
              ((TLRPC.TL_documentAttributeAudio)localObject).voice = true;
              ((TLRPC.TL_documentAttributeAudio)localObject).waveform = MediaController.this.getWaveform2(MediaController.this.recordSamples, MediaController.this.recordSamples.length);
              if (((TLRPC.TL_documentAttributeAudio)localObject).waveform != null) {
                ((TLRPC.TL_documentAttributeAudio)localObject).flags |= 0x4;
              }
              long l = MediaController.this.recordTimeCount;
              ((TLRPC.TL_documentAttributeAudio)localObject).duration = ((int)(MediaController.this.recordTimeCount / 1000L));
              MediaController.25.this.val$audioToSend.attributes.add(localObject);
              String str;
              if (l > 700L)
              {
                if (MediaController.25.this.val$send == 1) {
                  SendMessagesHelper.getInstance(MediaController.this.recordingCurrentAccount).sendMessage(MediaController.25.this.val$audioToSend, null, MediaController.25.this.val$recordingAudioFileToSend.getAbsolutePath(), MediaController.this.recordDialogId, MediaController.this.recordReplyingMessageObject, null, null, null, null, 0);
                }
                NotificationCenter localNotificationCenter = NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount);
                int i = NotificationCenter.audioDidSent;
                if (MediaController.25.this.val$send == 2)
                {
                  localObject = MediaController.25.this.val$audioToSend;
                  if (MediaController.25.this.val$send != 2) {
                    break label323;
                  }
                  str = MediaController.25.this.val$recordingAudioFileToSend.getAbsolutePath();
                  label297:
                  localNotificationCenter.postNotificationName(i, new Object[] { localObject, str });
                }
              }
              for (;;)
              {
                return;
                localObject = null;
                break;
                label323:
                str = null;
                break label297;
                MediaController.25.this.val$recordingAudioFileToSend.delete();
              }
            }
          });
        }
      });
    }
    try
    {
      if (this.audioRecorder != null)
      {
        this.audioRecorder.release();
        this.audioRecorder = null;
      }
      this.recordingAudio = null;
      this.recordingAudioFile = null;
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
  
  private native int writeFrame(ByteBuffer paramByteBuffer, int paramInt);
  
  public void cancelVideoConvert(MessageObject paramMessageObject)
  {
    if (paramMessageObject == null) {}
    label123:
    for (;;)
    {
      synchronized (this.videoConvertSync)
      {
        this.cancelCurrentVideoConversion = true;
        return;
      }
      if (!this.videoConvertQueue.isEmpty()) {
        for (int i = 0;; i++)
        {
          if (i >= this.videoConvertQueue.size()) {
            break label123;
          }
          ??? = (MessageObject)this.videoConvertQueue.get(i);
          if ((((MessageObject)???).getId() == paramMessageObject.getId()) && (((MessageObject)???).currentAccount == paramMessageObject.currentAccount))
          {
            if (i == 0) {
              synchronized (this.videoConvertSync)
              {
                this.cancelCurrentVideoConversion = true;
              }
            }
            this.videoConvertQueue.remove(i);
            break;
          }
        }
      }
    }
  }
  
  protected void checkIsNextMediaFileDownloaded()
  {
    if ((this.playingMessageObject == null) || (!this.playingMessageObject.isMusic())) {}
    for (;;)
    {
      return;
      checkIsNextMusicFileDownloaded(this.playingMessageObject.currentAccount);
    }
  }
  
  public void cleanup()
  {
    cleanupPlayer(false, true);
    this.audioInfo = null;
    this.playMusicAgain = false;
    for (int i = 0; i < 3; i++) {
      DownloadController.getInstance(i).cleanup();
    }
    this.videoConvertQueue.clear();
    this.playlist.clear();
    this.shuffledPlaylist.clear();
    this.generatingWaveform.clear();
    this.voiceMessagesPlaylist = null;
    this.voiceMessagesPlaylistMap = null;
    cancelVideoConvert(null);
  }
  
  public void cleanupPlayer(boolean paramBoolean1, boolean paramBoolean2)
  {
    cleanupPlayer(paramBoolean1, paramBoolean2, false);
  }
  
  public void cleanupPlayer(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (this.audioPlayer != null) {}
    for (;;)
    {
      try
      {
        this.audioPlayer.releasePlayer();
        this.audioPlayer = null;
        stopProgressTimer();
        this.lastProgress = 0L;
        this.buffersWrited = 0;
        this.isPaused = false;
        Object localObject1;
        if ((!this.useFrontSpeaker) && (!SharedConfig.raiseToSpeak))
        {
          localObject1 = this.raiseChat;
          stopRaiseToEarSensors(this.raiseChat);
          this.raiseChat = ((ChatActivity)localObject1);
        }
        if (this.playingMessageObject != null)
        {
          if (this.downloadingCurrentMessage) {
            FileLoader.getInstance(this.playingMessageObject.currentAccount).cancelLoadFile(this.playingMessageObject.getDocument());
          }
          localObject1 = this.playingMessageObject;
          if (paramBoolean1)
          {
            this.playingMessageObject.resetPlayingProgress();
            NotificationCenter.getInstance(((MessageObject)localObject1).currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0) });
          }
          this.playingMessageObject = null;
          this.downloadingCurrentMessage = false;
          if (paramBoolean1)
          {
            NotificationsController.audioManager.abandonAudioFocus(this);
            this.hasAudioFocus = 0;
            if (this.voiceMessagesPlaylist != null)
            {
              if ((!paramBoolean3) || (this.voiceMessagesPlaylist.get(0) != localObject1)) {
                break label487;
              }
              this.voiceMessagesPlaylist.remove(0);
              this.voiceMessagesPlaylistMap.remove(((MessageObject)localObject1).getId());
              if (this.voiceMessagesPlaylist.isEmpty())
              {
                this.voiceMessagesPlaylist = null;
                this.voiceMessagesPlaylistMap = null;
              }
            }
            if (this.voiceMessagesPlaylist == null) {
              break label500;
            }
            localObject1 = (MessageObject)this.voiceMessagesPlaylist.get(0);
            playMessage((MessageObject)localObject1);
            if ((!((MessageObject)localObject1).isRoundVideo()) && (this.pipRoundVideoView != null))
            {
              this.pipRoundVideoView.close(true);
              this.pipRoundVideoView = null;
            }
          }
          if (paramBoolean2)
          {
            localObject1 = new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class);
            ApplicationLoader.applicationContext.stopService((Intent)localObject1);
          }
        }
        return;
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
        continue;
      }
      if (this.audioTrackPlayer != null)
      {
        try
        {
          synchronized (this.playerObjectSync)
          {
            this.audioTrackPlayer.pause();
            this.audioTrackPlayer.flush();
          }
        }
        catch (Exception localException3)
        {
          try
          {
            for (;;)
            {
              this.audioTrackPlayer.release();
              this.audioTrackPlayer = null;
              break;
              localObject3 = finally;
              throw ((Throwable)localObject3);
              localException3 = localException3;
              FileLog.e(localException3);
            }
          }
          catch (Exception localException4)
          {
            for (;;)
            {
              FileLog.e(localException4);
            }
          }
        }
      }
      else if (this.videoPlayer != null)
      {
        this.currentAspectRatioFrameLayout = null;
        this.currentTextureViewContainer = null;
        this.currentAspectRatioFrameLayoutReady = false;
        this.currentTextureView = null;
        this.videoPlayer.releasePlayer();
        this.videoPlayer = null;
        try
        {
          this.baseActivity.getWindow().clearFlags(128);
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
        }
        continue;
        label487:
        this.voiceMessagesPlaylist = null;
        this.voiceMessagesPlaylistMap = null;
        continue;
        label500:
        if (((localException2.isVoice()) || (localException2.isRoundVideo())) && (localException2.getId() != 0)) {
          startRecordingIfFromSpeaker();
        }
        NotificationCenter.getInstance(localException2.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, new Object[] { Integer.valueOf(localException2.getId()), Boolean.valueOf(paramBoolean2) });
        this.pipSwitchingState = 0;
        if (this.pipRoundVideoView != null)
        {
          this.pipRoundVideoView.close(true);
          this.pipRoundVideoView = null;
        }
      }
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if ((paramInt1 == NotificationCenter.FileDidLoaded) || (paramInt1 == NotificationCenter.httpFileDidLoaded))
    {
      paramVarArgs = (String)paramVarArgs[0];
      if ((this.downloadingCurrentMessage) && (this.playingMessageObject != null) && (this.playingMessageObject.currentAccount == paramInt2) && (FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(paramVarArgs)))
      {
        this.playMusicAgain = true;
        playMessage(this.playingMessageObject);
      }
    }
    for (;;)
    {
      return;
      Object localObject;
      if (paramInt1 == NotificationCenter.messagesDeleted)
      {
        paramInt1 = ((Integer)paramVarArgs[1]).intValue();
        ArrayList localArrayList = (ArrayList)paramVarArgs[0];
        if ((this.playingMessageObject != null) && (paramInt1 == this.playingMessageObject.messageOwner.to_id.channel_id) && (localArrayList.contains(Integer.valueOf(this.playingMessageObject.getId())))) {
          cleanupPlayer(true, true);
        }
        if ((this.voiceMessagesPlaylist != null) && (!this.voiceMessagesPlaylist.isEmpty()) && (paramInt1 == ((MessageObject)this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id)) {
          for (paramInt1 = 0; paramInt1 < localArrayList.size(); paramInt1++)
          {
            localObject = (Integer)localArrayList.get(paramInt1);
            paramVarArgs = (MessageObject)this.voiceMessagesPlaylistMap.get(((Integer)localObject).intValue());
            this.voiceMessagesPlaylistMap.remove(((Integer)localObject).intValue());
            if (paramVarArgs != null) {
              this.voiceMessagesPlaylist.remove(paramVarArgs);
            }
          }
        }
      }
      else
      {
        long l;
        if (paramInt1 == NotificationCenter.removeAllMessagesFromDialog)
        {
          l = ((Long)paramVarArgs[0]).longValue();
          if ((this.playingMessageObject != null) && (this.playingMessageObject.getDialogId() == l)) {
            cleanupPlayer(false, true);
          }
        }
        else if (paramInt1 == NotificationCenter.musicDidLoaded)
        {
          l = ((Long)paramVarArgs[0]).longValue();
          if ((this.playingMessageObject != null) && (this.playingMessageObject.isMusic()) && (this.playingMessageObject.getDialogId() == l))
          {
            paramVarArgs = (ArrayList)paramVarArgs[1];
            this.playlist.addAll(0, paramVarArgs);
            if (SharedConfig.shuffleMusic)
            {
              buildShuffledPlayList();
              this.currentPlaylistNum = 0;
            }
            else
            {
              this.currentPlaylistNum += paramVarArgs.size();
            }
          }
        }
        else if (paramInt1 == NotificationCenter.didReceivedNewMessages)
        {
          if ((this.voiceMessagesPlaylist != null) && (!this.voiceMessagesPlaylist.isEmpty()))
          {
            localObject = (MessageObject)this.voiceMessagesPlaylist.get(0);
            if (((Long)paramVarArgs[0]).longValue() == ((MessageObject)localObject).getDialogId())
            {
              localObject = (ArrayList)paramVarArgs[1];
              for (paramInt1 = 0; paramInt1 < ((ArrayList)localObject).size(); paramInt1++)
              {
                paramVarArgs = (MessageObject)((ArrayList)localObject).get(paramInt1);
                if (((paramVarArgs.isVoice()) || (paramVarArgs.isRoundVideo())) && ((!this.voiceMessagesPlaylistUnread) || ((paramVarArgs.isContentUnread()) && (!paramVarArgs.isOut()))))
                {
                  this.voiceMessagesPlaylist.add(paramVarArgs);
                  this.voiceMessagesPlaylistMap.put(paramVarArgs.getId(), paramVarArgs);
                }
              }
            }
          }
        }
        else if (paramInt1 == NotificationCenter.playerDidStartPlaying)
        {
          paramVarArgs = (VideoPlayer)paramVarArgs[0];
          if (!getInstance().isCurrentPlayer(paramVarArgs)) {
            getInstance().pauseMessage(getInstance().getPlayingMessageObject());
          }
        }
      }
    }
  }
  
  public boolean findMessageInPlaylistAndPlay(MessageObject paramMessageObject)
  {
    int i = this.playlist.indexOf(paramMessageObject);
    if (i == -1) {}
    for (boolean bool = playMessage(paramMessageObject);; bool = true)
    {
      return bool;
      playMessageAtIndex(i);
    }
  }
  
  public void generateWaveform(MessageObject paramMessageObject)
  {
    final String str1 = paramMessageObject.getId() + "_" + paramMessageObject.getDialogId();
    final String str2 = FileLoader.getPathToMessage(paramMessageObject.messageOwner).getAbsolutePath();
    if (this.generatingWaveform.containsKey(str1)) {}
    for (;;)
    {
      return;
      this.generatingWaveform.put(str1, paramMessageObject);
      Utilities.globalQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessageObject localMessageObject = (MessageObject)MediaController.this.generatingWaveform.remove(MediaController.24.this.val$id);
              if (localMessageObject == null) {
                return;
              }
              if (this.val$waveform != null) {}
              for (int i = 0;; i++)
              {
                Object localObject;
                if (i < localMessageObject.getDocument().attributes.size())
                {
                  localObject = (TLRPC.DocumentAttribute)localMessageObject.getDocument().attributes.get(i);
                  if ((localObject instanceof TLRPC.TL_documentAttributeAudio))
                  {
                    ((TLRPC.DocumentAttribute)localObject).waveform = this.val$waveform;
                    ((TLRPC.DocumentAttribute)localObject).flags |= 0x4;
                  }
                }
                else
                {
                  localObject = new TLRPC.TL_messages_messages();
                  ((TLRPC.TL_messages_messages)localObject).messages.add(localMessageObject.messageOwner);
                  MessagesStorage.getInstance(localMessageObject.currentAccount).putMessages((TLRPC.messages_Messages)localObject, localMessageObject.getDialogId(), -1, 0, false);
                  localObject = new ArrayList();
                  ((ArrayList)localObject).add(localMessageObject);
                  NotificationCenter.getInstance(localMessageObject.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(localMessageObject.getDialogId()), localObject });
                  break;
                  break;
                }
              }
            }
          });
        }
      });
    }
  }
  
  public AudioInfo getAudioInfo()
  {
    return this.audioInfo;
  }
  
  public MessageObject getPlayingMessageObject()
  {
    return this.playingMessageObject;
  }
  
  public int getPlayingMessageObjectNum()
  {
    return this.currentPlaylistNum;
  }
  
  public ArrayList<MessageObject> getPlaylist()
  {
    return this.playlist;
  }
  
  public native byte[] getWaveform(String paramString);
  
  public native byte[] getWaveform2(short[] paramArrayOfShort, int paramInt);
  
  public boolean isCurrentPlayer(VideoPlayer paramVideoPlayer)
  {
    if ((this.videoPlayer == paramVideoPlayer) || (this.audioPlayer == paramVideoPlayer)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isDownloadingCurrentMessage()
  {
    return this.downloadingCurrentMessage;
  }
  
  public boolean isMessagePaused()
  {
    if ((this.isPaused) || (this.downloadingCurrentMessage)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isPlayingMessage(MessageObject paramMessageObject)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    boolean bool3 = false;
    if ((this.audioTrackPlayer == null) && (this.audioPlayer == null))
    {
      bool4 = bool3;
      if (this.videoPlayer == null) {}
    }
    else
    {
      bool4 = bool3;
      if (paramMessageObject != null)
      {
        if (this.playingMessageObject != null) {
          break label54;
        }
        bool4 = bool3;
      }
    }
    label54:
    do
    {
      return bool4;
      if ((this.playingMessageObject.eventId != 0L) && (this.playingMessageObject.eventId == paramMessageObject.eventId))
      {
        if (!this.downloadingCurrentMessage) {}
        for (bool4 = bool2;; bool4 = false) {
          break;
        }
      }
      bool4 = bool3;
    } while (!isSamePlayingMessage(paramMessageObject));
    if (!this.downloadingCurrentMessage) {}
    for (boolean bool4 = bool1;; bool4 = false) {
      break;
    }
  }
  
  protected boolean isRecordingAudio()
  {
    if ((this.recordStartRunnable != null) || (this.recordingAudio != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isRecordingOrListeningByProximity()
  {
    if ((this.proximityTouched) && ((isRecordingAudio()) || ((this.playingMessageObject != null) && ((this.playingMessageObject.isVoice()) || (this.playingMessageObject.isRoundVideo()))))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isRoundVideoDrawingReady()
  {
    if ((this.currentAspectRatioFrameLayout != null) && (this.currentAspectRatioFrameLayout.isDrawingReady())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
  
  public void onAudioFocusChange(int paramInt)
  {
    if (paramInt == -1)
    {
      if ((isPlayingMessage(getPlayingMessageObject())) && (!isMessagePaused())) {
        pauseMessage(this.playingMessageObject);
      }
      this.hasAudioFocus = 0;
      this.audioFocus = 0;
    }
    for (;;)
    {
      setPlayerVolume();
      return;
      if (paramInt == 1)
      {
        this.audioFocus = 2;
        if (this.resumeAudioOnFocusGain)
        {
          this.resumeAudioOnFocusGain = false;
          if ((isPlayingMessage(getPlayingMessageObject())) && (isMessagePaused())) {
            playMessage(getPlayingMessageObject());
          }
        }
      }
      else if (paramInt == -3)
      {
        this.audioFocus = 1;
      }
      else if (paramInt == -2)
      {
        this.audioFocus = 0;
        if ((isPlayingMessage(getPlayingMessageObject())) && (!isMessagePaused()))
        {
          pauseMessage(this.playingMessageObject);
          this.resumeAudioOnFocusGain = true;
        }
      }
    }
  }
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    if ((!this.sensorsStarted) || (VoIPService.getSharedInstance() != null)) {}
    label80:
    label101:
    label231:
    label331:
    label384:
    label609:
    label1153:
    label1158:
    label1179:
    label1241:
    label1388:
    label1394:
    label1489:
    label1732:
    for (;;)
    {
      return;
      float f;
      int i;
      int j;
      boolean bool;
      if (paramSensorEvent.sensor == this.proximitySensor)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("proximity changed to " + paramSensorEvent.values[0]);
        }
        if (this.lastProximityValue == -100.0F)
        {
          this.lastProximityValue = paramSensorEvent.values[0];
          if (this.proximityHasDifferentValues) {
            this.proximityTouched = isNearToSensor(paramSensorEvent.values[0]);
          }
          if ((paramSensorEvent.sensor == this.linearSensor) || (paramSensorEvent.sensor == this.gravitySensor) || (paramSensorEvent.sensor == this.accelerometerSensor))
          {
            f = this.gravity[0] * this.linearAcceleration[0] + this.gravity[1] * this.linearAcceleration[1] + this.gravity[2] * this.linearAcceleration[2];
            if ((this.raisedToBack != 6) && (((f > 0.0F) && (this.previousAccValue > 0.0F)) || ((f < 0.0F) && (this.previousAccValue < 0.0F))))
            {
              if (f <= 0.0F) {
                break label1158;
              }
              if (f <= 15.0F) {
                break label1153;
              }
              i = 1;
              j = 1;
              if ((this.raisedToTopSign == 0) || (this.raisedToTopSign == j)) {
                break label1241;
              }
              if ((this.raisedToTop != 6) || (i == 0)) {
                break label1179;
              }
              if (this.raisedToBack < 6)
              {
                this.raisedToBack += 1;
                if (this.raisedToBack == 6)
                {
                  this.raisedToTop = 0;
                  this.raisedToTopSign = 0;
                  this.countLess = 0;
                  this.timeSinceRaise = System.currentTimeMillis();
                  if ((BuildVars.LOGS_ENABLED) && (BuildVars.DEBUG_PRIVATE_VERSION)) {
                    FileLog.d("motion detected");
                  }
                }
              }
            }
            this.previousAccValue = f;
            if ((this.gravityFast[1] <= 2.5F) || (Math.abs(this.gravityFast[2]) >= 4.0F) || (Math.abs(this.gravityFast[0]) <= 1.5F)) {
              break label1388;
            }
            bool = true;
            this.accelerometerVertical = bool;
          }
          if ((this.raisedToBack != 6) || (!this.accelerometerVertical) || (!this.proximityTouched) || (NotificationsController.audioManager.isWiredHeadsetOn())) {
            break label1489;
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("sensor values reached");
          }
          if ((this.playingMessageObject != null) || (this.recordStartRunnable != null) || (this.recordingAudio != null) || (PhotoViewer.getInstance().isVisible()) || (!ApplicationLoader.isScreenOn) || (this.inputFieldHasText) || (!this.allowStartRecord) || (this.raiseChat == null) || (this.callInProgress)) {
            break label1394;
          }
          if (!this.raiseToEarRecord)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("start record");
            }
            this.useFrontSpeaker = true;
            if (!this.raiseChat.playFirstUnreadVoiceMessage())
            {
              this.raiseToEarRecord = true;
              this.useFrontSpeaker = false;
              startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null);
            }
            if (this.useFrontSpeaker) {
              setUseFrontSpeaker(true);
            }
            this.ignoreOnPause = true;
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.acquire();
            }
          }
          this.raisedToBack = 0;
          this.raisedToTop = 0;
          this.raisedToTopSign = 0;
          this.countLess = 0;
        }
      }
      for (;;)
      {
        if ((this.timeSinceRaise == 0L) || (this.raisedToBack != 6) || (Math.abs(System.currentTimeMillis() - this.timeSinceRaise) <= 1000L)) {
          break label1732;
        }
        this.raisedToBack = 0;
        this.raisedToTop = 0;
        this.raisedToTopSign = 0;
        this.countLess = 0;
        this.timeSinceRaise = 0L;
        break;
        if (this.lastProximityValue == paramSensorEvent.values[0]) {
          break label80;
        }
        this.proximityHasDifferentValues = true;
        break label80;
        if (paramSensorEvent.sensor == this.accelerometerSensor)
        {
          if (this.lastTimestamp == 0L) {}
          for (double d = 0.9800000190734863D;; d = 1.0D / (1.0D + (paramSensorEvent.timestamp - this.lastTimestamp) / 1.0E9D))
          {
            this.lastTimestamp = paramSensorEvent.timestamp;
            this.gravity[0] = ((float)(this.gravity[0] * d + (1.0D - d) * paramSensorEvent.values[0]));
            this.gravity[1] = ((float)(this.gravity[1] * d + (1.0D - d) * paramSensorEvent.values[1]));
            this.gravity[2] = ((float)(this.gravity[2] * d + (1.0D - d) * paramSensorEvent.values[2]));
            this.gravityFast[0] = (0.8F * this.gravity[0] + 0.19999999F * paramSensorEvent.values[0]);
            this.gravityFast[1] = (0.8F * this.gravity[1] + 0.19999999F * paramSensorEvent.values[1]);
            this.gravityFast[2] = (0.8F * this.gravity[2] + 0.19999999F * paramSensorEvent.values[2]);
            this.linearAcceleration[0] = (paramSensorEvent.values[0] - this.gravity[0]);
            this.linearAcceleration[1] = (paramSensorEvent.values[1] - this.gravity[1]);
            this.linearAcceleration[2] = (paramSensorEvent.values[2] - this.gravity[2]);
            break;
          }
        }
        if (paramSensorEvent.sensor == this.linearSensor)
        {
          this.linearAcceleration[0] = paramSensorEvent.values[0];
          this.linearAcceleration[1] = paramSensorEvent.values[1];
          this.linearAcceleration[2] = paramSensorEvent.values[2];
          break label101;
        }
        if (paramSensorEvent.sensor != this.gravitySensor) {
          break label101;
        }
        float[] arrayOfFloat1 = this.gravityFast;
        float[] arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[0];
        arrayOfFloat2[0] = f;
        arrayOfFloat1[0] = f;
        arrayOfFloat2 = this.gravityFast;
        arrayOfFloat1 = this.gravity;
        f = paramSensorEvent.values[1];
        arrayOfFloat1[1] = f;
        arrayOfFloat2[1] = f;
        arrayOfFloat1 = this.gravityFast;
        arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[2];
        arrayOfFloat2[2] = f;
        arrayOfFloat1[2] = f;
        break label101;
        i = 0;
        break label231;
        if (f < -15.0F) {}
        for (i = 1;; i = 0)
        {
          j = 2;
          break;
        }
        if (i == 0) {
          this.countLess += 1;
        }
        if ((this.countLess != 10) && (this.raisedToTop == 6) && (this.raisedToBack == 0)) {
          break label331;
        }
        this.raisedToTop = 0;
        this.raisedToTopSign = 0;
        this.raisedToBack = 0;
        this.countLess = 0;
        break label331;
        if ((i != 0) && (this.raisedToBack == 0) && ((this.raisedToTopSign == 0) || (this.raisedToTopSign == j)))
        {
          if ((this.raisedToTop >= 6) || (this.proximityTouched)) {
            break label331;
          }
          this.raisedToTopSign = j;
          this.raisedToTop += 1;
          if (this.raisedToTop != 6) {
            break label331;
          }
          this.countLess = 0;
          break label331;
        }
        if (i == 0) {
          this.countLess += 1;
        }
        if ((this.raisedToTopSign == j) && (this.countLess != 10) && (this.raisedToTop == 6) && (this.raisedToBack == 0)) {
          break label331;
        }
        this.raisedToBack = 0;
        this.raisedToTop = 0;
        this.raisedToTopSign = 0;
        this.countLess = 0;
        break label331;
        bool = false;
        break label384;
        if ((this.playingMessageObject == null) || ((!this.playingMessageObject.isVoice()) && (!this.playingMessageObject.isRoundVideo())) || (this.useFrontSpeaker)) {
          break label609;
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("start listen");
        }
        if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld())) {
          this.proximityWakeLock.acquire();
        }
        setUseFrontSpeaker(true);
        startAudioAgain(false);
        this.ignoreOnPause = true;
        break label609;
        if (this.proximityTouched)
        {
          if ((this.playingMessageObject != null) && ((this.playingMessageObject.isVoice()) || (this.playingMessageObject.isRoundVideo())) && (!this.useFrontSpeaker))
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("start listen by proximity only");
            }
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.acquire();
            }
            setUseFrontSpeaker(true);
            startAudioAgain(false);
            this.ignoreOnPause = true;
          }
        }
        else if (!this.proximityTouched) {
          if (this.raiseToEarRecord)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("stop record");
            }
            stopRecording(2);
            this.raiseToEarRecord = false;
            this.ignoreOnPause = false;
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.release();
            }
          }
          else if (this.useFrontSpeaker)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("stop listen");
            }
            this.useFrontSpeaker = false;
            startAudioAgain(true);
            this.ignoreOnPause = false;
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.release();
            }
          }
        }
      }
    }
  }
  
  public boolean pauseMessage(MessageObject paramMessageObject)
  {
    boolean bool1 = false;
    boolean bool2;
    if ((this.audioTrackPlayer == null) && (this.audioPlayer == null))
    {
      bool2 = bool1;
      if (this.videoPlayer == null) {}
    }
    else
    {
      bool2 = bool1;
      if (paramMessageObject != null)
      {
        bool2 = bool1;
        if (this.playingMessageObject != null)
        {
          if (isSamePlayingMessage(paramMessageObject)) {
            break label52;
          }
          bool2 = bool1;
        }
      }
    }
    return bool2;
    label52:
    stopProgressTimer();
    for (;;)
    {
      try
      {
        if (this.audioPlayer != null)
        {
          this.audioPlayer.pause();
          this.isPaused = true;
          NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
          bool2 = true;
          break;
        }
        if (this.audioTrackPlayer != null)
        {
          this.audioTrackPlayer.pause();
          continue;
        }
      }
      catch (Exception paramMessageObject)
      {
        FileLog.e(paramMessageObject);
        this.isPaused = false;
        bool2 = bool1;
      }
      if (this.videoPlayer != null) {
        this.videoPlayer.pause();
      }
    }
  }
  
  /* Error */
  public boolean playMessage(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +7 -> 8
    //   4: iconst_0
    //   5: istore_2
    //   6: iload_2
    //   7: ireturn
    //   8: aload_0
    //   9: getfield 415	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   12: ifnonnull +17 -> 29
    //   15: aload_0
    //   16: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   19: ifnonnull +10 -> 29
    //   22: aload_0
    //   23: getfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   26: ifnull +43 -> 69
    //   29: aload_0
    //   30: aload_1
    //   31: invokespecial 2179	org/telegram/messenger/MediaController:isSamePlayingMessage	(Lorg/telegram/messenger/MessageObject;)Z
    //   34: ifeq +35 -> 69
    //   37: aload_0
    //   38: getfield 411	org/telegram/messenger/MediaController:isPaused	Z
    //   41: ifeq +9 -> 50
    //   44: aload_0
    //   45: aload_1
    //   46: invokevirtual 2335	org/telegram/messenger/MediaController:resumeAudio	(Lorg/telegram/messenger/MessageObject;)Z
    //   49: pop
    //   50: getstatic 2037	org/telegram/messenger/SharedConfig:raiseToSpeak	Z
    //   53: ifne +11 -> 64
    //   56: aload_0
    //   57: aload_0
    //   58: getfield 2039	org/telegram/messenger/MediaController:raiseChat	Lorg/telegram/ui/ChatActivity;
    //   61: invokevirtual 2338	org/telegram/messenger/MediaController:startRaiseToEarSensors	(Lorg/telegram/ui/ChatActivity;)V
    //   64: iconst_1
    //   65: istore_2
    //   66: goto -60 -> 6
    //   69: aload_1
    //   70: invokevirtual 2128	org/telegram/messenger/MessageObject:isOut	()Z
    //   73: ifne +21 -> 94
    //   76: aload_1
    //   77: invokevirtual 2125	org/telegram/messenger/MessageObject:isContentUnread	()Z
    //   80: ifeq +14 -> 94
    //   83: aload_1
    //   84: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   87: invokestatic 2343	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   90: aload_1
    //   91: invokevirtual 2346	org/telegram/messenger/MessagesController:markMessageContentAsRead	(Lorg/telegram/messenger/MessageObject;)V
    //   94: aload_0
    //   95: getfield 1771	org/telegram/messenger/MediaController:playMusicAgain	Z
    //   98: ifne +305 -> 403
    //   101: iconst_1
    //   102: istore_2
    //   103: aload_0
    //   104: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   107: ifnull +23 -> 130
    //   110: iconst_0
    //   111: istore_3
    //   112: iload_3
    //   113: istore_2
    //   114: aload_0
    //   115: getfield 1771	org/telegram/messenger/MediaController:playMusicAgain	Z
    //   118: ifne +12 -> 130
    //   121: aload_0
    //   122: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   125: invokevirtual 1769	org/telegram/messenger/MessageObject:resetPlayingProgress	()V
    //   128: iload_3
    //   129: istore_2
    //   130: aload_0
    //   131: iload_2
    //   132: iconst_0
    //   133: invokevirtual 1711	org/telegram/messenger/MediaController:cleanupPlayer	(ZZ)V
    //   136: aload_0
    //   137: iconst_0
    //   138: putfield 1771	org/telegram/messenger/MediaController:playMusicAgain	Z
    //   141: aload_0
    //   142: fconst_0
    //   143: putfield 685	org/telegram/messenger/MediaController:seekToProgressPending	F
    //   146: aconst_null
    //   147: astore 4
    //   149: iconst_0
    //   150: istore_3
    //   151: iload_3
    //   152: istore_2
    //   153: aload 4
    //   155: astore 5
    //   157: aload_1
    //   158: getfield 986	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   161: getfield 991	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   164: ifnull +55 -> 219
    //   167: iload_3
    //   168: istore_2
    //   169: aload 4
    //   171: astore 5
    //   173: aload_1
    //   174: getfield 986	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   177: getfield 991	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   180: invokevirtual 1028	java/lang/String:length	()I
    //   183: ifle +36 -> 219
    //   186: new 998	java/io/File
    //   189: dup
    //   190: aload_1
    //   191: getfield 986	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   194: getfield 991	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   197: invokespecial 999	java/io/File:<init>	(Ljava/lang/String;)V
    //   200: astore 5
    //   202: aload 5
    //   204: invokevirtual 1002	java/io/File:exists	()Z
    //   207: istore_3
    //   208: iload_3
    //   209: istore_2
    //   210: iload_3
    //   211: ifne +8 -> 219
    //   214: aconst_null
    //   215: astore 5
    //   217: iload_3
    //   218: istore_2
    //   219: aload 5
    //   221: ifnull +187 -> 408
    //   224: aload 5
    //   226: astore 4
    //   228: getstatic 2349	org/telegram/messenger/SharedConfig:streamMedia	Z
    //   231: ifeq +189 -> 420
    //   234: aload_1
    //   235: invokevirtual 1005	org/telegram/messenger/MessageObject:isMusic	()Z
    //   238: ifeq +182 -> 420
    //   241: aload_1
    //   242: invokevirtual 1115	org/telegram/messenger/MessageObject:getDialogId	()J
    //   245: l2i
    //   246: ifeq +174 -> 420
    //   249: iconst_1
    //   250: istore 6
    //   252: iload_2
    //   253: istore_3
    //   254: aload 4
    //   256: ifnull +203 -> 459
    //   259: iload_2
    //   260: istore_3
    //   261: aload 4
    //   263: aload 5
    //   265: if_acmpeq +194 -> 459
    //   268: aload 4
    //   270: invokevirtual 1002	java/io/File:exists	()Z
    //   273: istore_2
    //   274: iload_2
    //   275: istore_3
    //   276: iload_2
    //   277: ifne +182 -> 459
    //   280: iload_2
    //   281: istore_3
    //   282: iload 6
    //   284: ifne +175 -> 459
    //   287: aload_1
    //   288: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   291: invokestatic 1010	org/telegram/messenger/FileLoader:getInstance	(I)Lorg/telegram/messenger/FileLoader;
    //   294: aload_1
    //   295: invokevirtual 1014	org/telegram/messenger/MessageObject:getDocument	()Lorg/telegram/tgnet/TLRPC$Document;
    //   298: iconst_0
    //   299: iconst_0
    //   300: invokevirtual 1018	org/telegram/messenger/FileLoader:loadFile	(Lorg/telegram/tgnet/TLRPC$Document;ZI)V
    //   303: aload_0
    //   304: iconst_1
    //   305: putfield 2045	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   308: aload_0
    //   309: iconst_0
    //   310: putfield 411	org/telegram/messenger/MediaController:isPaused	Z
    //   313: aload_0
    //   314: lconst_0
    //   315: putfield 417	org/telegram/messenger/MediaController:lastProgress	J
    //   318: aload_0
    //   319: lconst_0
    //   320: putfield 690	org/telegram/messenger/MediaController:lastPlayPcm	J
    //   323: aload_0
    //   324: aconst_null
    //   325: putfield 2024	org/telegram/messenger/MediaController:audioInfo	Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   328: aload_0
    //   329: aload_1
    //   330: putfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   333: aload_0
    //   334: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   337: invokevirtual 1005	org/telegram/messenger/MessageObject:isMusic	()Z
    //   340: ifeq +94 -> 434
    //   343: new 1967	android/content/Intent
    //   346: dup
    //   347: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   350: ldc_w 2063
    //   353: invokespecial 1972	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   356: astore_1
    //   357: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   360: aload_1
    //   361: invokevirtual 2008	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   364: pop
    //   365: aload_0
    //   366: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   369: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   372: invokestatic 1736	org/telegram/messenger/NotificationCenter:getInstance	(I)Lorg/telegram/messenger/NotificationCenter;
    //   375: getstatic 1746	org/telegram/messenger/NotificationCenter:messagePlayingPlayStateChanged	I
    //   378: iconst_1
    //   379: anewarray 4	java/lang/Object
    //   382: dup
    //   383: iconst_0
    //   384: aload_0
    //   385: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   388: invokevirtual 1124	org/telegram/messenger/MessageObject:getId	()I
    //   391: invokestatic 1555	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   394: aastore
    //   395: invokevirtual 1743	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   398: iconst_1
    //   399: istore_2
    //   400: goto -394 -> 6
    //   403: iconst_0
    //   404: istore_2
    //   405: goto -302 -> 103
    //   408: aload_1
    //   409: getfield 986	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   412: invokestatic 1022	org/telegram/messenger/FileLoader:getPathToMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Ljava/io/File;
    //   415: astore 4
    //   417: goto -189 -> 228
    //   420: iconst_0
    //   421: istore 6
    //   423: goto -171 -> 252
    //   426: astore_1
    //   427: aload_1
    //   428: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   431: goto -66 -> 365
    //   434: new 1967	android/content/Intent
    //   437: dup
    //   438: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   441: ldc_w 2063
    //   444: invokespecial 1972	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   447: astore_1
    //   448: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   451: aload_1
    //   452: invokevirtual 2067	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   455: pop
    //   456: goto -91 -> 365
    //   459: aload_0
    //   460: iconst_0
    //   461: putfield 2045	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   464: aload_1
    //   465: invokevirtual 1005	org/telegram/messenger/MessageObject:isMusic	()Z
    //   468: ifeq +412 -> 880
    //   471: aload_0
    //   472: aload_1
    //   473: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   476: invokespecial 2021	org/telegram/messenger/MediaController:checkIsNextMusicFileDownloaded	(I)V
    //   479: aload_0
    //   480: getfield 771	org/telegram/messenger/MediaController:currentAspectRatioFrameLayout	Lorg/telegram/messenger/exoplayer2/ui/AspectRatioFrameLayout;
    //   483: ifnull +16 -> 499
    //   486: aload_0
    //   487: iconst_0
    //   488: putfield 774	org/telegram/messenger/MediaController:isDrawingWasReady	Z
    //   491: aload_0
    //   492: getfield 771	org/telegram/messenger/MediaController:currentAspectRatioFrameLayout	Lorg/telegram/messenger/exoplayer2/ui/AspectRatioFrameLayout;
    //   495: iconst_0
    //   496: invokevirtual 2352	org/telegram/messenger/exoplayer2/ui/AspectRatioFrameLayout:setDrawingReady	(Z)V
    //   499: aload_1
    //   500: invokevirtual 921	org/telegram/messenger/MessageObject:isRoundVideo	()Z
    //   503: ifeq +425 -> 928
    //   506: aload_0
    //   507: getfield 429	org/telegram/messenger/MediaController:playlist	Ljava/util/ArrayList;
    //   510: invokevirtual 884	java/util/ArrayList:clear	()V
    //   513: aload_0
    //   514: getfield 431	org/telegram/messenger/MediaController:shuffledPlaylist	Ljava/util/ArrayList;
    //   517: invokevirtual 884	java/util/ArrayList:clear	()V
    //   520: aload_0
    //   521: new 1716	org/telegram/ui/Components/VideoPlayer
    //   524: dup
    //   525: invokespecial 2353	org/telegram/ui/Components/VideoPlayer:<init>	()V
    //   528: putfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   531: aload_0
    //   532: getfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   535: new 36	org/telegram/messenger/MediaController$16
    //   538: dup
    //   539: aload_0
    //   540: invokespecial 2354	org/telegram/messenger/MediaController$16:<init>	(Lorg/telegram/messenger/MediaController;)V
    //   543: invokevirtual 2358	org/telegram/ui/Components/VideoPlayer:setDelegate	(Lorg/telegram/ui/Components/VideoPlayer$VideoPlayerDelegate;)V
    //   546: aload_0
    //   547: iconst_0
    //   548: putfield 749	org/telegram/messenger/MediaController:currentAspectRatioFrameLayoutReady	Z
    //   551: aload_0
    //   552: getfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   555: ifnonnull +20 -> 575
    //   558: aload_1
    //   559: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   562: invokestatic 2343	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   565: aload_1
    //   566: invokevirtual 1115	org/telegram/messenger/MessageObject:getDialogId	()J
    //   569: invokevirtual 2362	org/telegram/messenger/MessagesController:isDialogCreated	(J)Z
    //   572: ifne +329 -> 901
    //   575: aload_0
    //   576: getfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   579: ifnonnull +51 -> 630
    //   582: new 2059	org/telegram/ui/Components/PipRoundVideoView
    //   585: astore 5
    //   587: aload 5
    //   589: invokespecial 2363	org/telegram/ui/Components/PipRoundVideoView:<init>	()V
    //   592: aload_0
    //   593: aload 5
    //   595: putfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   598: aload_0
    //   599: getfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   602: astore 7
    //   604: aload_0
    //   605: getfield 746	org/telegram/messenger/MediaController:baseActivity	Landroid/app/Activity;
    //   608: astore 5
    //   610: new 40	org/telegram/messenger/MediaController$17
    //   613: astore 8
    //   615: aload 8
    //   617: aload_0
    //   618: invokespecial 2364	org/telegram/messenger/MediaController$17:<init>	(Lorg/telegram/messenger/MediaController;)V
    //   621: aload 7
    //   623: aload 5
    //   625: aload 8
    //   627: invokevirtual 2367	org/telegram/ui/Components/PipRoundVideoView:show	(Landroid/app/Activity;Ljava/lang/Runnable;)V
    //   630: aload_0
    //   631: getfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   634: ifnull +17 -> 651
    //   637: aload_0
    //   638: getfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   641: aload_0
    //   642: getfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   645: invokevirtual 2371	org/telegram/ui/Components/PipRoundVideoView:getTextureView	()Landroid/view/TextureView;
    //   648: invokevirtual 2375	org/telegram/ui/Components/VideoPlayer:setTextureView	(Landroid/view/TextureView;)V
    //   651: aload_0
    //   652: getfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   655: aload 4
    //   657: invokestatic 1577	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   660: ldc_w 2377
    //   663: invokevirtual 2381	org/telegram/ui/Components/VideoPlayer:preparePlayer	(Landroid/net/Uri;Ljava/lang/String;)V
    //   666: aload_0
    //   667: getfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   670: astore 5
    //   672: aload_0
    //   673: getfield 923	org/telegram/messenger/MediaController:useFrontSpeaker	Z
    //   676: ifeq +246 -> 922
    //   679: iconst_0
    //   680: istore 6
    //   682: aload 5
    //   684: iload 6
    //   686: invokevirtual 1935	org/telegram/ui/Components/VideoPlayer:setStreamType	(I)V
    //   689: aload_0
    //   690: getfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   693: invokevirtual 1938	org/telegram/ui/Components/VideoPlayer:play	()V
    //   696: aload_0
    //   697: aload_1
    //   698: invokespecial 2383	org/telegram/messenger/MediaController:checkAudioFocus	(Lorg/telegram/messenger/MessageObject;)V
    //   701: aload_0
    //   702: invokespecial 2200	org/telegram/messenger/MediaController:setPlayerVolume	()V
    //   705: aload_0
    //   706: iconst_0
    //   707: putfield 411	org/telegram/messenger/MediaController:isPaused	Z
    //   710: aload_0
    //   711: lconst_0
    //   712: putfield 417	org/telegram/messenger/MediaController:lastProgress	J
    //   715: aload_0
    //   716: lconst_0
    //   717: putfield 690	org/telegram/messenger/MediaController:lastPlayPcm	J
    //   720: aload_0
    //   721: aload_1
    //   722: putfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   725: getstatic 2037	org/telegram/messenger/SharedConfig:raiseToSpeak	Z
    //   728: ifne +11 -> 739
    //   731: aload_0
    //   732: aload_0
    //   733: getfield 2039	org/telegram/messenger/MediaController:raiseChat	Lorg/telegram/ui/ChatActivity;
    //   736: invokevirtual 2338	org/telegram/messenger/MediaController:startRaiseToEarSensors	(Lorg/telegram/ui/ChatActivity;)V
    //   739: aload_0
    //   740: aload_0
    //   741: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   744: invokespecial 2385	org/telegram/messenger/MediaController:startProgressTimer	(Lorg/telegram/messenger/MessageObject;)V
    //   747: aload_1
    //   748: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   751: invokestatic 1736	org/telegram/messenger/NotificationCenter:getInstance	(I)Lorg/telegram/messenger/NotificationCenter;
    //   754: getstatic 2388	org/telegram/messenger/NotificationCenter:messagePlayingDidStarted	I
    //   757: iconst_1
    //   758: anewarray 4	java/lang/Object
    //   761: dup
    //   762: iconst_0
    //   763: aload_1
    //   764: aastore
    //   765: invokevirtual 1743	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   768: aload_0
    //   769: getfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   772: ifnull +992 -> 1764
    //   775: aload_0
    //   776: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   779: getfield 1725	org/telegram/messenger/MessageObject:audioProgress	F
    //   782: fconst_0
    //   783: fcmpl
    //   784: ifeq +59 -> 843
    //   787: aload_0
    //   788: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   791: invokevirtual 2391	org/telegram/ui/Components/VideoPlayer:getDuration	()J
    //   794: lstore 9
    //   796: lload 9
    //   798: lstore 11
    //   800: lload 9
    //   802: ldc2_w 2392
    //   805: lcmp
    //   806: ifne +13 -> 819
    //   809: aload_0
    //   810: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   813: invokevirtual 2395	org/telegram/messenger/MessageObject:getDuration	()I
    //   816: i2l
    //   817: lstore 11
    //   819: lload 11
    //   821: l2f
    //   822: aload_0
    //   823: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   826: getfield 1725	org/telegram/messenger/MessageObject:audioProgress	F
    //   829: fmul
    //   830: f2i
    //   831: istore 6
    //   833: aload_0
    //   834: getfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   837: iload 6
    //   839: i2l
    //   840: invokevirtual 2397	org/telegram/ui/Components/VideoPlayer:seekTo	(J)V
    //   843: aload_0
    //   844: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   847: invokevirtual 1005	org/telegram/messenger/MessageObject:isMusic	()Z
    //   850: ifeq +1099 -> 1949
    //   853: new 1967	android/content/Intent
    //   856: dup
    //   857: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   860: ldc_w 2063
    //   863: invokespecial 1972	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   866: astore_1
    //   867: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   870: aload_1
    //   871: invokevirtual 2008	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   874: pop
    //   875: iconst_1
    //   876: istore_2
    //   877: goto -871 -> 6
    //   880: aload_0
    //   881: aload_1
    //   882: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   885: invokespecial 2399	org/telegram/messenger/MediaController:checkIsNextVoiceFileDownloaded	(I)V
    //   888: goto -409 -> 479
    //   891: astore 5
    //   893: aload_0
    //   894: aconst_null
    //   895: putfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   898: goto -268 -> 630
    //   901: aload_0
    //   902: getfield 783	org/telegram/messenger/MediaController:currentTextureView	Landroid/view/TextureView;
    //   905: ifnull -254 -> 651
    //   908: aload_0
    //   909: getfield 674	org/telegram/messenger/MediaController:videoPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   912: aload_0
    //   913: getfield 783	org/telegram/messenger/MediaController:currentTextureView	Landroid/view/TextureView;
    //   916: invokevirtual 2375	org/telegram/ui/Components/VideoPlayer:setTextureView	(Landroid/view/TextureView;)V
    //   919: goto -268 -> 651
    //   922: iconst_3
    //   923: istore 6
    //   925: goto -243 -> 682
    //   928: aload_1
    //   929: invokevirtual 1005	org/telegram/messenger/MessageObject:isMusic	()Z
    //   932: ifne +287 -> 1219
    //   935: aload 4
    //   937: invokevirtual 1609	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   940: invokestatic 2401	org/telegram/messenger/MediaController:isOpusFile	(Ljava/lang/String;)I
    //   943: iconst_1
    //   944: if_icmpne +275 -> 1219
    //   947: aload_0
    //   948: getfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   951: ifnull +16 -> 967
    //   954: aload_0
    //   955: getfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   958: iconst_1
    //   959: invokevirtual 2061	org/telegram/ui/Components/PipRoundVideoView:close	(Z)V
    //   962: aload_0
    //   963: aconst_null
    //   964: putfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   967: aload_0
    //   968: getfield 429	org/telegram/messenger/MediaController:playlist	Ljava/util/ArrayList;
    //   971: invokevirtual 884	java/util/ArrayList:clear	()V
    //   974: aload_0
    //   975: getfield 431	org/telegram/messenger/MediaController:shuffledPlaylist	Ljava/util/ArrayList;
    //   978: invokevirtual 884	java/util/ArrayList:clear	()V
    //   981: aload_0
    //   982: getfield 433	org/telegram/messenger/MediaController:playerObjectSync	Ljava/lang/Object;
    //   985: astore 5
    //   987: aload 5
    //   989: monitorenter
    //   990: aload_0
    //   991: iconst_3
    //   992: putfield 423	org/telegram/messenger/MediaController:ignoreFirstProgress	I
    //   995: new 2403	java/util/concurrent/CountDownLatch
    //   998: astore 7
    //   1000: aload 7
    //   1002: iconst_1
    //   1003: invokespecial 2404	java/util/concurrent/CountDownLatch:<init>	(I)V
    //   1006: iconst_1
    //   1007: anewarray 1929	java/lang/Boolean
    //   1010: astore 8
    //   1012: aload_0
    //   1013: getfield 475	org/telegram/messenger/MediaController:fileDecodingQueue	Lorg/telegram/messenger/DispatchQueue;
    //   1016: astore 13
    //   1018: new 42	org/telegram/messenger/MediaController$18
    //   1021: astore 14
    //   1023: aload 14
    //   1025: aload_0
    //   1026: aload 8
    //   1028: aload 4
    //   1030: aload 7
    //   1032: invokespecial 2407	org/telegram/messenger/MediaController$18:<init>	(Lorg/telegram/messenger/MediaController;[Ljava/lang/Boolean;Ljava/io/File;Ljava/util/concurrent/CountDownLatch;)V
    //   1035: aload 13
    //   1037: aload 14
    //   1039: invokevirtual 480	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   1042: aload 7
    //   1044: invokevirtual 2410	java/util/concurrent/CountDownLatch:await	()V
    //   1047: aload 8
    //   1049: iconst_0
    //   1050: aaload
    //   1051: invokevirtual 2413	java/lang/Boolean:booleanValue	()Z
    //   1054: istore_2
    //   1055: iload_2
    //   1056: ifne +17 -> 1073
    //   1059: iconst_0
    //   1060: istore_2
    //   1061: aload 5
    //   1063: monitorexit
    //   1064: goto -1058 -> 6
    //   1067: astore_1
    //   1068: aload 5
    //   1070: monitorexit
    //   1071: aload_1
    //   1072: athrow
    //   1073: aload_0
    //   1074: aload_0
    //   1075: invokespecial 2415	org/telegram/messenger/MediaController:getTotalPcmDuration	()J
    //   1078: putfield 694	org/telegram/messenger/MediaController:currentTotalPcmDuration	J
    //   1081: new 1748	android/media/AudioTrack
    //   1084: astore 4
    //   1086: aload_0
    //   1087: getfield 923	org/telegram/messenger/MediaController:useFrontSpeaker	Z
    //   1090: ifeq +76 -> 1166
    //   1093: iconst_0
    //   1094: istore 6
    //   1096: aload 4
    //   1098: iload 6
    //   1100: ldc_w 2416
    //   1103: iconst_4
    //   1104: iconst_2
    //   1105: aload_0
    //   1106: getfield 419	org/telegram/messenger/MediaController:playerBufferSize	I
    //   1109: iconst_1
    //   1110: invokespecial 2419	android/media/AudioTrack:<init>	(IIIIII)V
    //   1113: aload_0
    //   1114: aload 4
    //   1116: putfield 415	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1119: aload_0
    //   1120: getfield 415	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1123: fconst_1
    //   1124: fconst_1
    //   1125: invokevirtual 1916	android/media/AudioTrack:setStereoVolume	(FF)I
    //   1128: pop
    //   1129: aload_0
    //   1130: getfield 415	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1133: astore 4
    //   1135: new 44	org/telegram/messenger/MediaController$19
    //   1138: astore 7
    //   1140: aload 7
    //   1142: aload_0
    //   1143: invokespecial 2420	org/telegram/messenger/MediaController$19:<init>	(Lorg/telegram/messenger/MediaController;)V
    //   1146: aload 4
    //   1148: aload 7
    //   1150: invokevirtual 2424	android/media/AudioTrack:setPlaybackPositionUpdateListener	(Landroid/media/AudioTrack$OnPlaybackPositionUpdateListener;)V
    //   1153: aload_0
    //   1154: getfield 415	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1157: invokevirtual 2425	android/media/AudioTrack:play	()V
    //   1160: aload 5
    //   1162: monitorexit
    //   1163: goto -467 -> 696
    //   1166: iconst_3
    //   1167: istore 6
    //   1169: goto -73 -> 1096
    //   1172: astore_1
    //   1173: aload_1
    //   1174: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1177: aload_0
    //   1178: getfield 415	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1181: ifnull +30 -> 1211
    //   1184: aload_0
    //   1185: getfield 415	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1188: invokevirtual 1755	android/media/AudioTrack:release	()V
    //   1191: aload_0
    //   1192: aconst_null
    //   1193: putfield 415	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1196: aload_0
    //   1197: iconst_0
    //   1198: putfield 411	org/telegram/messenger/MediaController:isPaused	Z
    //   1201: aload_0
    //   1202: aconst_null
    //   1203: putfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1206: aload_0
    //   1207: iconst_0
    //   1208: putfield 2045	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   1211: iconst_0
    //   1212: istore_2
    //   1213: aload 5
    //   1215: monitorexit
    //   1216: goto -1210 -> 6
    //   1219: aload_0
    //   1220: getfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   1223: ifnull +16 -> 1239
    //   1226: aload_0
    //   1227: getfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   1230: iconst_1
    //   1231: invokevirtual 2061	org/telegram/ui/Components/PipRoundVideoView:close	(Z)V
    //   1234: aload_0
    //   1235: aconst_null
    //   1236: putfield 787	org/telegram/messenger/MediaController:pipRoundVideoView	Lorg/telegram/ui/Components/PipRoundVideoView;
    //   1239: new 1716	org/telegram/ui/Components/VideoPlayer
    //   1242: astore 7
    //   1244: aload 7
    //   1246: invokespecial 2353	org/telegram/ui/Components/VideoPlayer:<init>	()V
    //   1249: aload_0
    //   1250: aload 7
    //   1252: putfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1255: aload_0
    //   1256: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1259: astore 7
    //   1261: aload_0
    //   1262: getfield 923	org/telegram/messenger/MediaController:useFrontSpeaker	Z
    //   1265: ifeq +217 -> 1482
    //   1268: iconst_0
    //   1269: istore 6
    //   1271: aload 7
    //   1273: iload 6
    //   1275: invokevirtual 1935	org/telegram/ui/Components/VideoPlayer:setStreamType	(I)V
    //   1278: aload_0
    //   1279: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1282: astore 8
    //   1284: new 48	org/telegram/messenger/MediaController$20
    //   1287: astore 7
    //   1289: aload 7
    //   1291: aload_0
    //   1292: aload_1
    //   1293: invokespecial 2426	org/telegram/messenger/MediaController$20:<init>	(Lorg/telegram/messenger/MediaController;Lorg/telegram/messenger/MessageObject;)V
    //   1296: aload 8
    //   1298: aload 7
    //   1300: invokevirtual 2358	org/telegram/ui/Components/VideoPlayer:setDelegate	(Lorg/telegram/ui/Components/VideoPlayer$VideoPlayerDelegate;)V
    //   1303: iload_3
    //   1304: ifeq +184 -> 1488
    //   1307: aload_1
    //   1308: getfield 2429	org/telegram/messenger/MessageObject:mediaExists	Z
    //   1311: ifne +27 -> 1338
    //   1314: aload 4
    //   1316: aload 5
    //   1318: if_acmpeq +20 -> 1338
    //   1321: new 50	org/telegram/messenger/MediaController$21
    //   1324: astore 5
    //   1326: aload 5
    //   1328: aload_0
    //   1329: aload_1
    //   1330: invokespecial 2430	org/telegram/messenger/MediaController$21:<init>	(Lorg/telegram/messenger/MediaController;Lorg/telegram/messenger/MessageObject;)V
    //   1333: aload 5
    //   1335: invokestatic 500	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
    //   1338: aload_0
    //   1339: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1342: aload 4
    //   1344: invokestatic 1577	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   1347: ldc_w 2377
    //   1350: invokevirtual 2381	org/telegram/ui/Components/VideoPlayer:preparePlayer	(Landroid/net/Uri;Ljava/lang/String;)V
    //   1353: aload_0
    //   1354: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1357: invokevirtual 1938	org/telegram/ui/Components/VideoPlayer:play	()V
    //   1360: aload_1
    //   1361: invokevirtual 918	org/telegram/messenger/MessageObject:isVoice	()Z
    //   1364: ifeq +309 -> 1673
    //   1367: aload_0
    //   1368: aconst_null
    //   1369: putfield 2024	org/telegram/messenger/MediaController:audioInfo	Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   1372: aload_0
    //   1373: getfield 429	org/telegram/messenger/MediaController:playlist	Ljava/util/ArrayList;
    //   1376: invokevirtual 884	java/util/ArrayList:clear	()V
    //   1379: aload_0
    //   1380: getfield 431	org/telegram/messenger/MediaController:shuffledPlaylist	Ljava/util/ArrayList;
    //   1383: invokevirtual 884	java/util/ArrayList:clear	()V
    //   1386: goto -690 -> 696
    //   1389: astore 5
    //   1391: aload 5
    //   1393: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1396: aload_1
    //   1397: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   1400: invokestatic 1736	org/telegram/messenger/NotificationCenter:getInstance	(I)Lorg/telegram/messenger/NotificationCenter;
    //   1403: astore_1
    //   1404: getstatic 1746	org/telegram/messenger/NotificationCenter:messagePlayingPlayStateChanged	I
    //   1407: istore 15
    //   1409: aload_0
    //   1410: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1413: ifnull +282 -> 1695
    //   1416: aload_0
    //   1417: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1420: invokevirtual 1124	org/telegram/messenger/MessageObject:getId	()I
    //   1423: istore 6
    //   1425: aload_1
    //   1426: iload 15
    //   1428: iconst_1
    //   1429: anewarray 4	java/lang/Object
    //   1432: dup
    //   1433: iconst_0
    //   1434: iload 6
    //   1436: invokestatic 1555	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1439: aastore
    //   1440: invokevirtual 1743	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   1443: aload_0
    //   1444: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1447: ifnull +30 -> 1477
    //   1450: aload_0
    //   1451: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1454: invokevirtual 1719	org/telegram/ui/Components/VideoPlayer:releasePlayer	()V
    //   1457: aload_0
    //   1458: aconst_null
    //   1459: putfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1462: aload_0
    //   1463: iconst_0
    //   1464: putfield 411	org/telegram/messenger/MediaController:isPaused	Z
    //   1467: aload_0
    //   1468: aconst_null
    //   1469: putfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1472: aload_0
    //   1473: iconst_0
    //   1474: putfield 2045	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   1477: iconst_0
    //   1478: istore_2
    //   1479: goto -1473 -> 6
    //   1482: iconst_3
    //   1483: istore 6
    //   1485: goto -214 -> 1271
    //   1488: aload_1
    //   1489: invokevirtual 1014	org/telegram/messenger/MessageObject:getDocument	()Lorg/telegram/tgnet/TLRPC$Document;
    //   1492: astore 5
    //   1494: new 1245	java/lang/StringBuilder
    //   1497: astore 7
    //   1499: aload 7
    //   1501: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   1504: aload 7
    //   1506: ldc_w 2432
    //   1509: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1512: aload_1
    //   1513: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   1516: invokevirtual 1292	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1519: ldc_w 2434
    //   1522: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1525: aload 5
    //   1527: getfield 2437	org/telegram/tgnet/TLRPC$Document:id	J
    //   1530: invokevirtual 1255	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1533: ldc_w 2439
    //   1536: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1539: aload 5
    //   1541: getfield 2442	org/telegram/tgnet/TLRPC$Document:access_hash	J
    //   1544: invokevirtual 1255	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1547: ldc_w 2444
    //   1550: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1553: aload 5
    //   1555: getfield 2447	org/telegram/tgnet/TLRPC$Document:dc_id	I
    //   1558: invokevirtual 1292	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1561: ldc_w 2449
    //   1564: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1567: aload 5
    //   1569: getfield 2450	org/telegram/tgnet/TLRPC$Document:size	I
    //   1572: invokevirtual 1292	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1575: ldc_w 2452
    //   1578: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1581: aload 5
    //   1583: getfield 2455	org/telegram/tgnet/TLRPC$Document:mime_type	Ljava/lang/String;
    //   1586: ldc_w 2457
    //   1589: invokestatic 2463	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   1592: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1595: ldc_w 2465
    //   1598: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1601: aload 5
    //   1603: invokestatic 2469	org/telegram/messenger/FileLoader:getDocumentFileName	(Lorg/telegram/tgnet/TLRPC$Document;)Ljava/lang/String;
    //   1606: ldc_w 2457
    //   1609: invokestatic 2463	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   1612: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1615: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1618: astore 7
    //   1620: new 1245	java/lang/StringBuilder
    //   1623: astore 5
    //   1625: aload 5
    //   1627: invokespecial 1246	java/lang/StringBuilder:<init>	()V
    //   1630: aload 5
    //   1632: ldc_w 2471
    //   1635: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1638: aload_1
    //   1639: invokevirtual 1955	org/telegram/messenger/MessageObject:getFileName	()Ljava/lang/String;
    //   1642: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1645: aload 7
    //   1647: invokevirtual 1252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1650: invokevirtual 1258	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1653: invokestatic 2475	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   1656: astore 5
    //   1658: aload_0
    //   1659: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1662: aload 5
    //   1664: ldc_w 2377
    //   1667: invokevirtual 2381	org/telegram/ui/Components/VideoPlayer:preparePlayer	(Landroid/net/Uri;Ljava/lang/String;)V
    //   1670: goto -317 -> 1353
    //   1673: aload_0
    //   1674: aload 4
    //   1676: invokestatic 2480	org/telegram/messenger/audioinfo/AudioInfo:getAudioInfo	(Ljava/io/File;)Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   1679: putfield 2024	org/telegram/messenger/MediaController:audioInfo	Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   1682: goto -986 -> 696
    //   1685: astore 5
    //   1687: aload 5
    //   1689: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1692: goto -996 -> 696
    //   1695: iconst_0
    //   1696: istore 6
    //   1698: goto -273 -> 1425
    //   1701: astore 5
    //   1703: aload_0
    //   1704: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1707: fconst_0
    //   1708: putfield 1725	org/telegram/messenger/MessageObject:audioProgress	F
    //   1711: aload_0
    //   1712: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1715: iconst_0
    //   1716: putfield 1728	org/telegram/messenger/MessageObject:audioProgressSec	I
    //   1719: aload_1
    //   1720: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   1723: invokestatic 1736	org/telegram/messenger/NotificationCenter:getInstance	(I)Lorg/telegram/messenger/NotificationCenter;
    //   1726: getstatic 1739	org/telegram/messenger/NotificationCenter:messagePlayingProgressDidChanged	I
    //   1729: iconst_2
    //   1730: anewarray 4	java/lang/Object
    //   1733: dup
    //   1734: iconst_0
    //   1735: aload_0
    //   1736: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1739: invokevirtual 1124	org/telegram/messenger/MessageObject:getId	()I
    //   1742: invokestatic 1555	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1745: aastore
    //   1746: dup
    //   1747: iconst_1
    //   1748: iconst_0
    //   1749: invokestatic 1555	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1752: aastore
    //   1753: invokevirtual 1743	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   1756: aload 5
    //   1758: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1761: goto -918 -> 843
    //   1764: aload_0
    //   1765: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1768: ifnull +128 -> 1896
    //   1771: aload_0
    //   1772: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1775: getfield 1725	org/telegram/messenger/MessageObject:audioProgress	F
    //   1778: fconst_0
    //   1779: fcmpl
    //   1780: ifeq -937 -> 843
    //   1783: aload_0
    //   1784: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1787: invokevirtual 2391	org/telegram/ui/Components/VideoPlayer:getDuration	()J
    //   1790: lstore 9
    //   1792: lload 9
    //   1794: lstore 11
    //   1796: lload 9
    //   1798: ldc2_w 2392
    //   1801: lcmp
    //   1802: ifne +13 -> 1815
    //   1805: aload_0
    //   1806: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1809: invokevirtual 2395	org/telegram/messenger/MessageObject:getDuration	()I
    //   1812: i2l
    //   1813: lstore 11
    //   1815: lload 11
    //   1817: l2f
    //   1818: aload_0
    //   1819: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1822: getfield 1725	org/telegram/messenger/MessageObject:audioProgress	F
    //   1825: fmul
    //   1826: f2i
    //   1827: istore 6
    //   1829: aload_0
    //   1830: getfield 413	org/telegram/messenger/MediaController:audioPlayer	Lorg/telegram/ui/Components/VideoPlayer;
    //   1833: iload 6
    //   1835: i2l
    //   1836: invokevirtual 2397	org/telegram/ui/Components/VideoPlayer:seekTo	(J)V
    //   1839: goto -996 -> 843
    //   1842: astore 5
    //   1844: aload_0
    //   1845: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1848: invokevirtual 1769	org/telegram/messenger/MessageObject:resetPlayingProgress	()V
    //   1851: aload_1
    //   1852: getfield 1731	org/telegram/messenger/MessageObject:currentAccount	I
    //   1855: invokestatic 1736	org/telegram/messenger/NotificationCenter:getInstance	(I)Lorg/telegram/messenger/NotificationCenter;
    //   1858: getstatic 1739	org/telegram/messenger/NotificationCenter:messagePlayingProgressDidChanged	I
    //   1861: iconst_2
    //   1862: anewarray 4	java/lang/Object
    //   1865: dup
    //   1866: iconst_0
    //   1867: aload_0
    //   1868: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1871: invokevirtual 1124	org/telegram/messenger/MessageObject:getId	()I
    //   1874: invokestatic 1555	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1877: aastore
    //   1878: dup
    //   1879: iconst_1
    //   1880: iconst_0
    //   1881: invokestatic 1555	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1884: aastore
    //   1885: invokevirtual 1743	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   1888: aload 5
    //   1890: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1893: goto -1050 -> 843
    //   1896: aload_0
    //   1897: getfield 415	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1900: ifnull -1057 -> 843
    //   1903: aload_0
    //   1904: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1907: getfield 1725	org/telegram/messenger/MessageObject:audioProgress	F
    //   1910: fconst_1
    //   1911: fcmpl
    //   1912: ifne +11 -> 1923
    //   1915: aload_0
    //   1916: getfield 649	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1919: fconst_0
    //   1920: putfield 1725	org/telegram/messenger/MessageObject:audioProgress	F
    //   1923: aload_0
    //   1924: getfield 475	org/telegram/messenger/MediaController:fileDecodingQueue	Lorg/telegram/messenger/DispatchQueue;
    //   1927: new 52	org/telegram/messenger/MediaController$22
    //   1930: dup
    //   1931: aload_0
    //   1932: invokespecial 2481	org/telegram/messenger/MediaController$22:<init>	(Lorg/telegram/messenger/MediaController;)V
    //   1935: invokevirtual 480	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   1938: goto -1095 -> 843
    //   1941: astore_1
    //   1942: aload_1
    //   1943: invokestatic 547	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1946: goto -1071 -> 875
    //   1949: new 1967	android/content/Intent
    //   1952: dup
    //   1953: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   1956: ldc_w 2063
    //   1959: invokespecial 1972	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   1962: astore_1
    //   1963: getstatic 514	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   1966: aload_1
    //   1967: invokevirtual 2067	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   1970: pop
    //   1971: goto -1096 -> 875
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1974	0	this	MediaController
    //   0	1974	1	paramMessageObject	MessageObject
    //   5	1474	2	bool1	boolean
    //   111	1193	3	bool2	boolean
    //   147	1528	4	localObject1	Object
    //   155	528	5	localObject2	Object
    //   891	1	5	localException1	Exception
    //   1389	3	5	localException2	Exception
    //   1492	171	5	localObject4	Object
    //   1685	3	5	localException3	Exception
    //   1701	56	5	localException4	Exception
    //   1842	47	5	localException5	Exception
    //   250	1584	6	i	int
    //   602	1044	7	localObject5	Object
    //   613	684	8	localObject6	Object
    //   794	1003	9	l1	long
    //   798	1018	11	l2	long
    //   1016	20	13	localDispatchQueue	DispatchQueue
    //   1021	17	14	local18	18
    //   1407	20	15	j	int
    // Exception table:
    //   from	to	target	type
    //   357	365	426	java/lang/Throwable
    //   582	630	891	java/lang/Exception
    //   990	1055	1067	finally
    //   1061	1064	1067	finally
    //   1068	1071	1067	finally
    //   1073	1093	1067	finally
    //   1096	1160	1067	finally
    //   1160	1163	1067	finally
    //   1173	1211	1067	finally
    //   1213	1216	1067	finally
    //   990	1055	1172	java/lang/Exception
    //   1073	1093	1172	java/lang/Exception
    //   1096	1160	1172	java/lang/Exception
    //   1239	1268	1389	java/lang/Exception
    //   1271	1303	1389	java/lang/Exception
    //   1307	1314	1389	java/lang/Exception
    //   1321	1338	1389	java/lang/Exception
    //   1338	1353	1389	java/lang/Exception
    //   1353	1386	1389	java/lang/Exception
    //   1488	1670	1389	java/lang/Exception
    //   1687	1692	1389	java/lang/Exception
    //   1673	1682	1685	java/lang/Exception
    //   775	796	1701	java/lang/Exception
    //   809	819	1701	java/lang/Exception
    //   819	843	1701	java/lang/Exception
    //   1771	1792	1842	java/lang/Exception
    //   1805	1815	1842	java/lang/Exception
    //   1815	1839	1842	java/lang/Exception
    //   867	875	1941	java/lang/Throwable
  }
  
  public void playMessageAtIndex(int paramInt)
  {
    if ((this.currentPlaylistNum < 0) || (this.currentPlaylistNum >= this.playlist.size())) {}
    for (;;)
    {
      return;
      this.currentPlaylistNum = paramInt;
      this.playMusicAgain = true;
      if (this.playingMessageObject != null) {
        this.playingMessageObject.resetPlayingProgress();
      }
      playMessage((MessageObject)this.playlist.get(this.currentPlaylistNum));
    }
  }
  
  public void playNextMessage()
  {
    playNextMessageWithoutOrder(false);
  }
  
  public void playPreviousMessage()
  {
    ArrayList localArrayList;
    if (SharedConfig.shuffleMusic)
    {
      localArrayList = this.shuffledPlaylist;
      if ((!localArrayList.isEmpty()) && (this.currentPlaylistNum >= 0) && (this.currentPlaylistNum < localArrayList.size())) {
        break label45;
      }
    }
    label45:
    label178:
    for (;;)
    {
      return;
      localArrayList = this.playlist;
      break;
      MessageObject localMessageObject = (MessageObject)localArrayList.get(this.currentPlaylistNum);
      if (localMessageObject.audioProgressSec > 10)
      {
        seekToProgress(localMessageObject, 0.0F);
      }
      else
      {
        if (SharedConfig.playOrderReversed)
        {
          this.currentPlaylistNum -= 1;
          if (this.currentPlaylistNum < 0) {
            this.currentPlaylistNum = (localArrayList.size() - 1);
          }
        }
        for (;;)
        {
          if ((this.currentPlaylistNum < 0) || (this.currentPlaylistNum >= localArrayList.size())) {
            break label178;
          }
          this.playMusicAgain = true;
          playMessage((MessageObject)localArrayList.get(this.currentPlaylistNum));
          break;
          this.currentPlaylistNum += 1;
          if (this.currentPlaylistNum >= localArrayList.size()) {
            this.currentPlaylistNum = 0;
          }
        }
      }
    }
  }
  
  public boolean resumeAudio(MessageObject paramMessageObject)
  {
    boolean bool1 = false;
    boolean bool2;
    if ((this.audioTrackPlayer == null) && (this.audioPlayer == null))
    {
      bool2 = bool1;
      if (this.videoPlayer == null) {}
    }
    else
    {
      bool2 = bool1;
      if (paramMessageObject != null)
      {
        bool2 = bool1;
        if (this.playingMessageObject != null)
        {
          if (isSamePlayingMessage(paramMessageObject)) {
            break label52;
          }
          bool2 = bool1;
        }
      }
    }
    return bool2;
    for (;;)
    {
      try
      {
        label52:
        startProgressTimer(this.playingMessageObject);
        if (this.audioPlayer != null)
        {
          this.audioPlayer.play();
          checkAudioFocus(paramMessageObject);
          this.isPaused = false;
          NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
          bool2 = true;
          break;
        }
        if (this.audioTrackPlayer != null)
        {
          this.audioTrackPlayer.play();
          checkPlayerQueue();
          continue;
        }
      }
      catch (Exception paramMessageObject)
      {
        FileLog.e(paramMessageObject);
        bool2 = bool1;
      }
      if (this.videoPlayer != null) {
        this.videoPlayer.play();
      }
    }
  }
  
  public void scheduleVideoConvert(MessageObject paramMessageObject)
  {
    scheduleVideoConvert(paramMessageObject, false);
  }
  
  public boolean scheduleVideoConvert(MessageObject paramMessageObject, boolean paramBoolean)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramMessageObject != null)
    {
      if (paramMessageObject.videoEditedInfo != null) {
        break label22;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      label22:
      if (paramBoolean)
      {
        bool2 = bool1;
        if (!this.videoConvertQueue.isEmpty()) {}
      }
      else
      {
        if (paramBoolean) {
          new File(paramMessageObject.messageOwner.attachPath).delete();
        }
        this.videoConvertQueue.add(paramMessageObject);
        if (this.videoConvertQueue.size() == 1) {
          startVideoConvertFromQueue();
        }
        bool2 = true;
      }
    }
  }
  
  public boolean seekToProgress(MessageObject paramMessageObject, float paramFloat)
  {
    boolean bool1 = false;
    boolean bool2;
    if ((this.audioTrackPlayer == null) && (this.audioPlayer == null))
    {
      bool2 = bool1;
      if (this.videoPlayer == null) {}
    }
    else
    {
      bool2 = bool1;
      if (paramMessageObject != null)
      {
        bool2 = bool1;
        if (this.playingMessageObject != null)
        {
          if (isSamePlayingMessage(paramMessageObject)) {
            break label57;
          }
          bool2 = bool1;
        }
      }
    }
    return bool2;
    for (;;)
    {
      try
      {
        label57:
        if (this.audioPlayer != null)
        {
          long l = this.audioPlayer.getDuration();
          if (l == -9223372036854775807L)
          {
            this.seekToProgressPending = paramFloat;
            bool2 = true;
            break;
          }
          int i = (int)((float)l * paramFloat);
          this.audioPlayer.seekTo(i);
          this.lastProgress = i;
          continue;
        }
      }
      catch (Exception paramMessageObject)
      {
        FileLog.e(paramMessageObject);
        bool2 = bool1;
      }
      if (this.audioTrackPlayer != null) {
        seekOpusPlayer(paramFloat);
      } else if (this.videoPlayer != null) {
        this.videoPlayer.seekTo(((float)this.videoPlayer.getDuration() * paramFloat));
      }
    }
  }
  
  public void setAllowStartRecord(boolean paramBoolean)
  {
    this.allowStartRecord = paramBoolean;
  }
  
  public void setBaseActivity(Activity paramActivity, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.baseActivity = paramActivity;
    }
    for (;;)
    {
      return;
      if (this.baseActivity == paramActivity) {
        this.baseActivity = null;
      }
    }
  }
  
  public void setCurrentRoundVisible(boolean paramBoolean)
  {
    if (this.currentAspectRatioFrameLayout == null) {}
    for (;;)
    {
      return;
      if (paramBoolean)
      {
        if (this.pipRoundVideoView != null)
        {
          this.pipSwitchingState = 2;
          this.pipRoundVideoView.close(true);
          this.pipRoundVideoView = null;
          continue;
        }
        if (this.currentAspectRatioFrameLayout == null) {
          continue;
        }
        if (this.currentAspectRatioFrameLayout.getParent() == null) {
          this.currentTextureViewContainer.addView(this.currentAspectRatioFrameLayout);
        }
        this.videoPlayer.setTextureView(this.currentTextureView);
        continue;
      }
      if (this.currentAspectRatioFrameLayout.getParent() != null)
      {
        this.pipSwitchingState = 1;
        this.currentTextureViewContainer.removeView(this.currentAspectRatioFrameLayout);
        continue;
      }
      if (this.pipRoundVideoView == null) {}
      try
      {
        PipRoundVideoView localPipRoundVideoView = new org/telegram/ui/Components/PipRoundVideoView;
        localPipRoundVideoView.<init>();
        this.pipRoundVideoView = localPipRoundVideoView;
        localPipRoundVideoView = this.pipRoundVideoView;
        Activity localActivity = this.baseActivity;
        Runnable local15 = new org/telegram/messenger/MediaController$15;
        local15.<init>(this);
        localPipRoundVideoView.show(localActivity, local15);
        if (this.pipRoundVideoView == null) {
          continue;
        }
        this.videoPlayer.setTextureView(this.pipRoundVideoView.getTextureView());
      }
      catch (Exception localException)
      {
        for (;;)
        {
          this.pipRoundVideoView = null;
        }
      }
    }
  }
  
  public void setFeedbackView(View paramView, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.feedbackView = paramView;
    }
    for (;;)
    {
      return;
      if (this.feedbackView == paramView) {
        this.feedbackView = null;
      }
    }
  }
  
  public void setFlagSecure(BaseFragment paramBaseFragment, boolean paramBoolean)
  {
    if (paramBoolean) {}
    try
    {
      paramBaseFragment.getParentActivity().getWindow().setFlags(8192, 8192);
      this.flagSecureFragment = paramBaseFragment;
      for (;;)
      {
        return;
        if (this.flagSecureFragment != paramBaseFragment) {
          continue;
        }
        try
        {
          paramBaseFragment.getParentActivity().getWindow().clearFlags(8192);
          this.flagSecureFragment = null;
        }
        catch (Exception paramBaseFragment)
        {
          for (;;) {}
        }
      }
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public void setInputFieldHasText(boolean paramBoolean)
  {
    this.inputFieldHasText = paramBoolean;
  }
  
  public void setLastVisibleMessageIds(int paramInt1, long paramLong1, long paramLong2, TLRPC.User paramUser, TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList, int paramInt2)
  {
    this.lastChatEnterTime = paramLong1;
    this.lastChatLeaveTime = paramLong2;
    this.lastChatAccount = paramInt1;
    this.lastSecretChat = paramEncryptedChat;
    this.lastUser = paramUser;
    this.lastMessageId = paramInt2;
    this.lastChatVisibleMessages = paramArrayList;
  }
  
  public boolean setPlaylist(ArrayList<MessageObject> paramArrayList, MessageObject paramMessageObject)
  {
    return setPlaylist(paramArrayList, paramMessageObject, true);
  }
  
  public boolean setPlaylist(ArrayList<MessageObject> paramArrayList, MessageObject paramMessageObject, boolean paramBoolean)
  {
    boolean bool1 = true;
    if (this.playingMessageObject == paramMessageObject) {}
    for (paramBoolean = playMessage(paramMessageObject);; paramBoolean = playMessage(paramMessageObject))
    {
      return paramBoolean;
      if (!paramBoolean)
      {
        bool2 = true;
        this.forceLoopCurrentPlaylist = bool2;
        if (this.playlist.isEmpty()) {
          break label113;
        }
      }
      label113:
      for (boolean bool2 = bool1;; bool2 = false)
      {
        this.playMusicAgain = bool2;
        this.playlist.clear();
        for (int i = paramArrayList.size() - 1; i >= 0; i--)
        {
          MessageObject localMessageObject = (MessageObject)paramArrayList.get(i);
          if (localMessageObject.isMusic()) {
            this.playlist.add(localMessageObject);
          }
        }
        bool2 = false;
        break;
      }
      this.currentPlaylistNum = this.playlist.indexOf(paramMessageObject);
      if (this.currentPlaylistNum == -1)
      {
        this.playlist.clear();
        this.shuffledPlaylist.clear();
        this.currentPlaylistNum = this.playlist.size();
        this.playlist.add(paramMessageObject);
      }
      if (paramMessageObject.isMusic())
      {
        if (SharedConfig.shuffleMusic)
        {
          buildShuffledPlayList();
          this.currentPlaylistNum = 0;
        }
        if (paramBoolean) {
          DataQuery.getInstance(paramMessageObject.currentAccount).loadMusic(paramMessageObject.getDialogId(), ((MessageObject)this.playlist.get(0)).getIdWithChannel());
        }
      }
    }
  }
  
  public void setReplyingMessage(MessageObject paramMessageObject)
  {
    this.recordReplyingMessageObject = paramMessageObject;
  }
  
  public void setTextureView(TextureView paramTextureView, AspectRatioFrameLayout paramAspectRatioFrameLayout, FrameLayout paramFrameLayout, boolean paramBoolean)
  {
    boolean bool = true;
    if (paramTextureView == null) {}
    do
    {
      for (;;)
      {
        return;
        if ((paramBoolean) || (this.currentTextureView != paramTextureView)) {
          break;
        }
        this.pipSwitchingState = 1;
        this.currentTextureView = null;
        this.currentAspectRatioFrameLayout = null;
        this.currentTextureViewContainer = null;
      }
    } while ((this.videoPlayer == null) || (paramTextureView == this.currentTextureView));
    if ((paramAspectRatioFrameLayout != null) && (paramAspectRatioFrameLayout.isDrawingReady()))
    {
      paramBoolean = bool;
      label74:
      this.isDrawingWasReady = paramBoolean;
      this.currentTextureView = paramTextureView;
      if (this.pipRoundVideoView == null) {
        break label179;
      }
      this.videoPlayer.setTextureView(this.pipRoundVideoView.getTextureView());
    }
    for (;;)
    {
      this.currentAspectRatioFrameLayout = paramAspectRatioFrameLayout;
      this.currentTextureViewContainer = paramFrameLayout;
      if ((!this.currentAspectRatioFrameLayoutReady) || (this.currentAspectRatioFrameLayout == null)) {
        break;
      }
      if (this.currentAspectRatioFrameLayout != null) {
        this.currentAspectRatioFrameLayout.setAspectRatio(this.currentAspectRatioFrameLayoutRatio, this.currentAspectRatioFrameLayoutRotation);
      }
      if (this.currentTextureViewContainer.getVisibility() == 0) {
        break;
      }
      this.currentTextureViewContainer.setVisibility(0);
      break;
      paramBoolean = false;
      break label74;
      label179:
      this.videoPlayer.setTextureView(this.currentTextureView);
    }
  }
  
  public void setVoiceMessagesPlaylist(ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    this.voiceMessagesPlaylist = paramArrayList;
    if (this.voiceMessagesPlaylist != null)
    {
      this.voiceMessagesPlaylistUnread = paramBoolean;
      this.voiceMessagesPlaylistMap = new SparseArray();
      for (int i = 0; i < this.voiceMessagesPlaylist.size(); i++)
      {
        paramArrayList = (MessageObject)this.voiceMessagesPlaylist.get(i);
        this.voiceMessagesPlaylistMap.put(paramArrayList.getId(), paramArrayList);
      }
    }
  }
  
  public void startMediaObserver()
  {
    ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
    this.startObserverToken += 1;
    try
    {
      if (this.internalObserver == null)
      {
        localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
        localObject1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        localObject2 = new org/telegram/messenger/MediaController$ExternalObserver;
        ((ExternalObserver)localObject2).<init>(this);
        this.externalObserver = ((ExternalObserver)localObject2);
        localContentResolver.registerContentObserver((Uri)localObject1, false, (ContentObserver)localObject2);
      }
    }
    catch (Exception localException1)
    {
      try
      {
        for (;;)
        {
          ContentResolver localContentResolver;
          Object localObject1;
          Object localObject2;
          if (this.externalObserver == null)
          {
            localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
            localObject2 = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            localObject1 = new org/telegram/messenger/MediaController$InternalObserver;
            ((InternalObserver)localObject1).<init>(this);
            this.internalObserver = ((InternalObserver)localObject1);
            localContentResolver.registerContentObserver((Uri)localObject2, false, (ContentObserver)localObject1);
          }
          return;
          localException1 = localException1;
          FileLog.e(localException1);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
        }
      }
    }
  }
  
  public void startRaiseToEarSensors(ChatActivity paramChatActivity)
  {
    if ((paramChatActivity == null) || ((this.accelerometerSensor == null) && ((this.gravitySensor == null) || (this.linearAcceleration == null))) || (this.proximitySensor == null)) {}
    for (;;)
    {
      return;
      this.raiseChat = paramChatActivity;
      if (((SharedConfig.raiseToSpeak) || ((this.playingMessageObject != null) && ((this.playingMessageObject.isVoice()) || (this.playingMessageObject.isRoundVideo())))) && (!this.sensorsStarted))
      {
        paramChatActivity = this.gravity;
        float[] arrayOfFloat = this.gravity;
        this.gravity[2] = 0.0F;
        arrayOfFloat[1] = 0.0F;
        paramChatActivity[0] = 0.0F;
        paramChatActivity = this.linearAcceleration;
        arrayOfFloat = this.linearAcceleration;
        this.linearAcceleration[2] = 0.0F;
        arrayOfFloat[1] = 0.0F;
        paramChatActivity[0] = 0.0F;
        paramChatActivity = this.gravityFast;
        arrayOfFloat = this.gravityFast;
        this.gravityFast[2] = 0.0F;
        arrayOfFloat[1] = 0.0F;
        paramChatActivity[0] = 0.0F;
        this.lastTimestamp = 0L;
        this.previousAccValue = 0.0F;
        this.raisedToTop = 0;
        this.raisedToTopSign = 0;
        this.countLess = 0;
        this.raisedToBack = 0;
        Utilities.globalQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            if (MediaController.this.gravitySensor != null) {
              MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.gravitySensor, 30000);
            }
            if (MediaController.this.linearSensor != null) {
              MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.linearSensor, 30000);
            }
            if (MediaController.this.accelerometerSensor != null) {
              MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.accelerometerSensor, 30000);
            }
            MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.proximitySensor, 3);
          }
        });
        this.sensorsStarted = true;
      }
    }
  }
  
  public void startRecording(final int paramInt, final long paramLong, MessageObject paramMessageObject)
  {
    int i = 0;
    int j = i;
    if (this.playingMessageObject != null)
    {
      j = i;
      if (isPlayingMessage(this.playingMessageObject))
      {
        j = i;
        if (!isMessagePaused())
        {
          j = 1;
          pauseMessage(this.playingMessageObject);
        }
      }
    }
    try
    {
      this.feedbackView.performHapticFeedback(3, 2);
      DispatchQueue localDispatchQueue = this.recordQueue;
      paramMessageObject = new Runnable()
      {
        public void run()
        {
          if (MediaController.this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$3102(MediaController.this, null);
                NotificationCenter.getInstance(MediaController.23.this.val$currentAccount).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
              }
            });
          }
          for (;;)
          {
            return;
            MediaController.access$3202(MediaController.this, new TLRPC.TL_document());
            MediaController.this.recordingAudio.dc_id = Integer.MIN_VALUE;
            MediaController.this.recordingAudio.id = SharedConfig.getLastLocalId();
            MediaController.this.recordingAudio.user_id = UserConfig.getInstance(paramInt).getClientUserId();
            MediaController.this.recordingAudio.mime_type = "audio/ogg";
            MediaController.this.recordingAudio.thumb = new TLRPC.TL_photoSizeEmpty();
            MediaController.this.recordingAudio.thumb.type = "s";
            SharedConfig.saveConfig();
            MediaController.access$7002(MediaController.this, new File(FileLoader.getDirectory(4), FileLoader.getAttachFileName(MediaController.this.recordingAudio)));
            try
            {
              if (MediaController.this.startRecord(MediaController.this.recordingAudioFile.getAbsolutePath()) == 0)
              {
                Runnable local2 = new org/telegram/messenger/MediaController$23$2;
                local2.<init>(this);
                AndroidUtilities.runOnUIThread(local2);
              }
            }
            catch (Exception localException1)
            {
              FileLog.e(localException1);
              MediaController.access$3202(MediaController.this, null);
              MediaController.this.stopRecord();
              MediaController.this.recordingAudioFile.delete();
              MediaController.access$7002(MediaController.this, null);
            }
            try
            {
              MediaController.this.audioRecorder.release();
              MediaController.access$002(MediaController.this, null);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MediaController.access$3102(MediaController.this, null);
                  NotificationCenter.getInstance(MediaController.23.this.val$currentAccount).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
              });
              continue;
              MediaController localMediaController = MediaController.this;
              AudioRecord localAudioRecord = new android/media/AudioRecord;
              localAudioRecord.<init>(1, 16000, 16, 2, MediaController.this.recordBufferSize * 10);
              MediaController.access$002(localMediaController, localAudioRecord);
              MediaController.access$1102(MediaController.this, System.currentTimeMillis());
              MediaController.access$702(MediaController.this, 0L);
              MediaController.access$302(MediaController.this, 0L);
              MediaController.access$7202(MediaController.this, paramLong);
              MediaController.access$1202(MediaController.this, paramInt);
              MediaController.access$7302(MediaController.this, this.val$reply_to_msg);
              MediaController.this.fileBuffer.rewind();
              MediaController.this.audioRecorder.startRecording();
              MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MediaController.access$3102(MediaController.this, null);
                  NotificationCenter.getInstance(MediaController.23.this.val$currentAccount).postNotificationName(NotificationCenter.recordStarted, new Object[0]);
                }
              });
            }
            catch (Exception localException2)
            {
              for (;;)
              {
                FileLog.e(localException2);
              }
            }
          }
        }
      };
      this.recordStartRunnable = paramMessageObject;
      if (j != 0) {}
      for (paramLong = 500L;; paramLong = 50L)
      {
        localDispatchQueue.postRunnable(paramMessageObject, paramLong);
        return;
      }
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public void startRecordingIfFromSpeaker()
  {
    if ((!this.useFrontSpeaker) || (this.raiseChat == null) || (!this.allowStartRecord)) {}
    for (;;)
    {
      return;
      this.raiseToEarRecord = true;
      startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null);
      this.ignoreOnPause = true;
    }
  }
  
  public void startSmsObserver()
  {
    try
    {
      if (this.smsObserver == null)
      {
        ContentResolver localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
        localObject = Uri.parse("content://sms");
        SmsObserver localSmsObserver = new org/telegram/messenger/MediaController$SmsObserver;
        localSmsObserver.<init>(this);
        this.smsObserver = localSmsObserver;
        localContentResolver.registerContentObserver((Uri)localObject, false, localSmsObserver);
      }
      Object localObject = new org/telegram/messenger/MediaController$7;
      ((7)localObject).<init>(this);
      AndroidUtilities.runOnUIThread((Runnable)localObject, 300000L);
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
  
  public void stopAudio()
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null) && (this.videoPlayer == null)) || (this.playingMessageObject == null)) {}
    for (;;)
    {
      return;
      try
      {
        if (this.audioPlayer != null) {
          this.audioPlayer.pause();
        }
      }
      catch (Exception localException1)
      {
        try
        {
          label43:
          if (this.audioPlayer != null)
          {
            this.audioPlayer.releasePlayer();
            this.audioPlayer = null;
          }
          for (;;)
          {
            stopProgressTimer();
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            this.isPaused = false;
            Intent localIntent = new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class);
            ApplicationLoader.applicationContext.stopService(localIntent);
            break;
            if (this.audioTrackPlayer != null)
            {
              this.audioTrackPlayer.pause();
              this.audioTrackPlayer.flush();
              break label43;
              localException1 = localException1;
              FileLog.e(localException1);
              break label43;
            }
            if (this.videoPlayer == null) {
              break label43;
            }
            this.videoPlayer.pause();
            break label43;
            if (this.audioTrackPlayer != null) {
              synchronized (this.playerObjectSync)
              {
                this.audioTrackPlayer.release();
                this.audioTrackPlayer = null;
              }
            }
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e(localException2);
            continue;
            if (this.videoPlayer != null)
            {
              this.currentAspectRatioFrameLayout = null;
              this.currentTextureViewContainer = null;
              this.currentAspectRatioFrameLayoutReady = false;
              this.currentTextureView = null;
              this.videoPlayer.releasePlayer();
              this.videoPlayer = null;
              try
              {
                this.baseActivity.getWindow().clearFlags(128);
              }
              catch (Exception localException3)
              {
                FileLog.e(localException3);
              }
            }
          }
        }
      }
    }
  }
  
  public void stopMediaObserver()
  {
    if (this.stopMediaObserverRunnable == null) {
      this.stopMediaObserverRunnable = new StopMediaObserverRunnable(null);
    }
    this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
    ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, 5000L);
  }
  
  public void stopRaiseToEarSensors(ChatActivity paramChatActivity)
  {
    if (this.ignoreOnPause) {
      this.ignoreOnPause = false;
    }
    for (;;)
    {
      return;
      stopRecording(0);
      if ((this.sensorsStarted) && (!this.ignoreOnPause) && ((this.accelerometerSensor != null) || ((this.gravitySensor != null) && (this.linearAcceleration != null))) && (this.proximitySensor != null) && (this.raiseChat == paramChatActivity))
      {
        this.raiseChat = null;
        this.sensorsStarted = false;
        this.accelerometerVertical = false;
        this.proximityTouched = false;
        this.raiseToEarRecord = false;
        this.useFrontSpeaker = false;
        Utilities.globalQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            if (MediaController.this.linearSensor != null) {
              MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.linearSensor);
            }
            if (MediaController.this.gravitySensor != null) {
              MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.gravitySensor);
            }
            if (MediaController.this.accelerometerSensor != null) {
              MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.accelerometerSensor);
            }
            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.proximitySensor);
          }
        });
        if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (this.proximityWakeLock.isHeld())) {
          this.proximityWakeLock.release();
        }
      }
    }
  }
  
  public void stopRecording(final int paramInt)
  {
    if (this.recordStartRunnable != null)
    {
      this.recordQueue.cancelRunnable(this.recordStartRunnable);
      this.recordStartRunnable = null;
    }
    this.recordQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (MediaController.this.audioRecorder == null) {}
        for (;;)
        {
          return;
          try
          {
            MediaController.access$1302(MediaController.this, paramInt);
            MediaController.this.audioRecorder.stop();
            if (paramInt == 0) {
              MediaController.this.stopRecordingInternal(0);
            }
          }
          catch (Exception localException1)
          {
            try
            {
              do
              {
                MediaController.this.feedbackView.performHapticFeedback(3, 2);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    int i = 1;
                    NotificationCenter localNotificationCenter = NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount);
                    int j = NotificationCenter.recordStopped;
                    if (MediaController.26.this.val$send == 2) {}
                    for (;;)
                    {
                      localNotificationCenter.postNotificationName(j, new Object[] { Integer.valueOf(i) });
                      return;
                      i = 0;
                    }
                  }
                });
                break;
                localException1 = localException1;
                FileLog.e(localException1);
              } while (MediaController.this.recordingAudioFile == null);
              MediaController.this.recordingAudioFile.delete();
            }
            catch (Exception localException2)
            {
              for (;;) {}
            }
          }
        }
      }
    });
  }
  
  public void toggleShuffleMusic(int paramInt)
  {
    boolean bool = SharedConfig.shuffleMusic;
    SharedConfig.toggleShuffleMusic(paramInt);
    if (bool != SharedConfig.shuffleMusic)
    {
      if (!SharedConfig.shuffleMusic) {
        break label31;
      }
      buildShuffledPlayList();
      this.currentPlaylistNum = 0;
    }
    for (;;)
    {
      return;
      label31:
      if (this.playingMessageObject != null)
      {
        this.currentPlaylistNum = this.playlist.indexOf(this.playingMessageObject);
        if (this.currentPlaylistNum == -1)
        {
          this.playlist.clear();
          this.shuffledPlaylist.clear();
          cleanupPlayer(true, true);
        }
      }
    }
  }
  
  public static class AlbumEntry
  {
    public int bucketId;
    public String bucketName;
    public MediaController.PhotoEntry coverPhoto;
    public ArrayList<MediaController.PhotoEntry> photos = new ArrayList();
    public SparseArray<MediaController.PhotoEntry> photosByIds = new SparseArray();
    
    public AlbumEntry(int paramInt, String paramString, MediaController.PhotoEntry paramPhotoEntry)
    {
      this.bucketId = paramInt;
      this.bucketName = paramString;
      this.coverPhoto = paramPhotoEntry;
    }
    
    public void addPhoto(MediaController.PhotoEntry paramPhotoEntry)
    {
      this.photos.add(paramPhotoEntry);
      this.photosByIds.put(paramPhotoEntry.imageId, paramPhotoEntry);
    }
  }
  
  private class AudioBuffer
  {
    ByteBuffer buffer;
    byte[] bufferBytes;
    int finished;
    long pcmOffset;
    int size;
    
    public AudioBuffer(int paramInt)
    {
      this.buffer = ByteBuffer.allocateDirect(paramInt);
      this.bufferBytes = new byte[paramInt];
    }
  }
  
  public static class AudioEntry
  {
    public String author;
    public int duration;
    public String genre;
    public long id;
    public MessageObject messageObject;
    public String path;
    public String title;
  }
  
  private class ExternalObserver
    extends ContentObserver
  {
    public ExternalObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      MediaController.this.processMediaObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }
  }
  
  private class GalleryObserverExternal
    extends ContentObserver
  {
    public GalleryObserverExternal()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      if (MediaController.refreshGalleryRunnable != null) {
        AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
      }
      AndroidUtilities.runOnUIThread(MediaController.access$1702(new Runnable()
      {
        public void run()
        {
          MediaController.access$1702(null);
          MediaController.loadGalleryPhotosAlbums(0);
        }
      }), 2000L);
    }
  }
  
  private class GalleryObserverInternal
    extends ContentObserver
  {
    public GalleryObserverInternal()
    {
      super();
    }
    
    private void scheduleReloadRunnable()
    {
      AndroidUtilities.runOnUIThread(MediaController.access$1702(new Runnable()
      {
        public void run()
        {
          if (PhotoViewer.getInstance().isVisible()) {
            MediaController.GalleryObserverInternal.this.scheduleReloadRunnable();
          }
          for (;;)
          {
            return;
            MediaController.access$1702(null);
            MediaController.loadGalleryPhotosAlbums(0);
          }
        }
      }), 2000L);
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      if (MediaController.refreshGalleryRunnable != null) {
        AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
      }
      scheduleReloadRunnable();
    }
  }
  
  private class InternalObserver
    extends ContentObserver
  {
    public InternalObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      MediaController.this.processMediaObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    }
  }
  
  public static class PhotoEntry
  {
    public int bucketId;
    public CharSequence caption;
    public long dateTaken;
    public int duration;
    public VideoEditedInfo editedInfo;
    public ArrayList<TLRPC.MessageEntity> entities;
    public int imageId;
    public String imagePath;
    public boolean isCropped;
    public boolean isFiltered;
    public boolean isMuted;
    public boolean isPainted;
    public boolean isVideo;
    public int orientation;
    public String path;
    public MediaController.SavedFilterState savedFilterState;
    public ArrayList<TLRPC.InputDocument> stickers = new ArrayList();
    public String thumbPath;
    public int ttl;
    
    public PhotoEntry(int paramInt1, int paramInt2, long paramLong, String paramString, int paramInt3, boolean paramBoolean)
    {
      this.bucketId = paramInt1;
      this.imageId = paramInt2;
      this.dateTaken = paramLong;
      this.path = paramString;
      if (paramBoolean) {
        this.duration = paramInt3;
      }
      for (;;)
      {
        this.isVideo = paramBoolean;
        return;
        this.orientation = paramInt3;
      }
    }
    
    public void reset()
    {
      this.isFiltered = false;
      this.isPainted = false;
      this.isCropped = false;
      this.ttl = 0;
      this.imagePath = null;
      if (!this.isVideo) {
        this.thumbPath = null;
      }
      this.editedInfo = null;
      this.caption = null;
      this.entities = null;
      this.savedFilterState = null;
      this.stickers.clear();
    }
  }
  
  public static class SavedFilterState
  {
    public float blurAngle;
    public float blurExcludeBlurSize;
    public org.telegram.ui.Components.Point blurExcludePoint;
    public float blurExcludeSize;
    public int blurType;
    public float contrastValue;
    public PhotoFilterView.CurvesToolValue curvesToolValue = new PhotoFilterView.CurvesToolValue();
    public float enhanceValue;
    public float exposureValue;
    public float fadeValue;
    public float grainValue;
    public float highlightsValue;
    public float saturationValue;
    public float shadowsValue;
    public float sharpenValue;
    public int tintHighlightsColor;
    public int tintShadowsColor;
    public float vignetteValue;
    public float warmthValue;
  }
  
  public static class SearchImage
  {
    public CharSequence caption;
    public int date;
    public TLRPC.Document document;
    public ArrayList<TLRPC.MessageEntity> entities;
    public int height;
    public String id;
    public String imagePath;
    public String imageUrl;
    public boolean isCropped;
    public boolean isFiltered;
    public boolean isPainted;
    public String localUrl;
    public MediaController.SavedFilterState savedFilterState;
    public int size;
    public ArrayList<TLRPC.InputDocument> stickers = new ArrayList();
    public String thumbPath;
    public String thumbUrl;
    public int ttl;
    public int type;
    public int width;
    
    public void reset()
    {
      this.isFiltered = false;
      this.isPainted = false;
      this.isCropped = false;
      this.ttl = 0;
      this.imagePath = null;
      this.thumbPath = null;
      this.caption = null;
      this.entities = null;
      this.savedFilterState = null;
      this.stickers.clear();
    }
  }
  
  private class SmsObserver
    extends ContentObserver
  {
    public SmsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      MediaController.this.readSms();
    }
  }
  
  private final class StopMediaObserverRunnable
    implements Runnable
  {
    public int currentObserverToken = 0;
    
    private StopMediaObserverRunnable() {}
    
    public void run()
    {
      if (this.currentObserverToken == MediaController.this.startObserverToken) {}
      try
      {
        if (MediaController.this.internalObserver != null)
        {
          ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
          MediaController.access$2002(MediaController.this, null);
        }
      }
      catch (Exception localException1)
      {
        try
        {
          for (;;)
          {
            if (MediaController.this.externalObserver != null)
            {
              ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
              MediaController.access$2102(MediaController.this, null);
            }
            return;
            localException1 = localException1;
            FileLog.e(localException1);
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e(localException2);
          }
        }
      }
    }
  }
  
  private static class VideoConvertRunnable
    implements Runnable
  {
    private MessageObject messageObject;
    
    private VideoConvertRunnable(MessageObject paramMessageObject)
    {
      this.messageObject = paramMessageObject;
    }
    
    public static void runConversion(MessageObject paramMessageObject)
    {
      new Thread(new Runnable()
      {
        public void run()
        {
          try
          {
            MediaController.VideoConvertRunnable localVideoConvertRunnable = new org/telegram/messenger/MediaController$VideoConvertRunnable;
            localVideoConvertRunnable.<init>(this.val$obj, null);
            Thread localThread = new java/lang/Thread;
            localThread.<init>(localVideoConvertRunnable, "VideoConvertRunnable");
            localThread.start();
            localThread.join();
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
      }).start();
    }
    
    public void run()
    {
      MediaController.getInstance().convertVideo(this.messageObject);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MediaController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */