package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.PhotoViewer;

public class MediaController
  implements AudioManager.OnAudioFocusChangeListener, NotificationCenter.NotificationCenterDelegate, SensorEventListener
{
  private static final int AUDIO_FOCUSED = 2;
  private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
  private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
  public static final int AUTODOWNLOAD_MASK_AUDIO = 2;
  public static final int AUTODOWNLOAD_MASK_DOCUMENT = 8;
  public static final int AUTODOWNLOAD_MASK_GIF = 32;
  public static final int AUTODOWNLOAD_MASK_MUSIC = 16;
  public static final int AUTODOWNLOAD_MASK_PHOTO = 1;
  public static final int AUTODOWNLOAD_MASK_VIDEO = 4;
  private static volatile MediaController Instance = null;
  public static final String MIME_TYPE = "video/avc";
  private static final int PROCESSOR_TYPE_INTEL = 2;
  private static final int PROCESSOR_TYPE_MTK = 3;
  private static final int PROCESSOR_TYPE_OTHER = 0;
  private static final int PROCESSOR_TYPE_QCOM = 1;
  private static final int PROCESSOR_TYPE_SEC = 4;
  private static final int PROCESSOR_TYPE_TI = 5;
  private static final float VOLUME_DUCK = 0.2F;
  private static final float VOLUME_NORMAL = 1.0F;
  public static AlbumEntry allPhotosAlbumEntry;
  private static Runnable broadcastPhotosRunnable;
  private static final String[] projectionPhotos;
  private static final String[] projectionVideo;
  public static int[] readArgs = new int[3];
  private Sensor accelerometerSensor;
  private boolean accelerometerVertical;
  private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap();
  private boolean allowStartRecord;
  private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList();
  private int audioFocus = 0;
  private AudioInfo audioInfo;
  private MediaPlayer audioPlayer = null;
  private AudioRecord audioRecorder = null;
  private AudioTrack audioTrackPlayer = null;
  private boolean autoplayGifs = true;
  private int buffersWrited;
  private boolean callInProgress;
  private boolean cancelCurrentVideoConversion = false;
  private int countLess;
  private int currentPlaylistNum;
  private long currentTotalPcmDuration;
  private boolean customTabs = true;
  private boolean decodingFinished = false;
  private ArrayList<FileDownloadProgressListener> deleteLaterArray = new ArrayList();
  private boolean directShare = true;
  private ArrayList<DownloadObject> documentDownloadQueue = new ArrayList();
  private HashMap<String, DownloadObject> downloadQueueKeys = new HashMap();
  private boolean downloadingCurrentMessage;
  private ExternalObserver externalObserver = null;
  private ByteBuffer fileBuffer;
  private DispatchQueue fileDecodingQueue;
  private DispatchQueue fileEncodingQueue;
  private boolean forceLoopCurrentPlaylist;
  private ArrayList<AudioBuffer> freePlayerBuffers = new ArrayList();
  private HashMap<String, MessageObject> generatingWaveform = new HashMap();
  private ArrayList<DownloadObject> gifDownloadQueue = new ArrayList();
  private float[] gravity = new float[3];
  private float[] gravityFast = new float[3];
  private Sensor gravitySensor;
  private int hasAudioFocus;
  private int ignoreFirstProgress = 0;
  private boolean ignoreOnPause;
  private boolean ignoreProximity;
  private boolean inputFieldHasText;
  private InternalObserver internalObserver = null;
  private boolean isPaused = false;
  private int lastCheckMask = 0;
  private long lastMediaCheckTime = 0L;
  private long lastPlayPcm;
  private int lastProgress = 0;
  private float lastProximityValue = -100.0F;
  private TLRPC.EncryptedChat lastSecretChat = null;
  private long lastSecretChatEnterTime = 0L;
  private long lastSecretChatLeaveTime = 0L;
  private ArrayList<Long> lastSecretChatVisibleMessages = null;
  private int lastTag = 0;
  private long lastTimestamp = 0L;
  private float[] linearAcceleration = new float[3];
  private Sensor linearSensor;
  private boolean listenerInProgress = false;
  private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers = new HashMap();
  private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers = new HashMap();
  private String[] mediaProjections = null;
  public int mobileDataDownloadMask = 0;
  private ArrayList<DownloadObject> musicDownloadQueue = new ArrayList();
  private HashMap<Integer, String> observersByTag = new HashMap();
  private ArrayList<DownloadObject> photoDownloadQueue = new ArrayList();
  private boolean playMusicAgain;
  private int playerBufferSize = 0;
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
  private boolean raiseToSpeak = true;
  private int raisedToBack;
  private int raisedToTop;
  private int recordBufferSize;
  private ArrayList<ByteBuffer> recordBuffers = new ArrayList();
  private long recordDialogId;
  private DispatchQueue recordQueue;
  private MessageObject recordReplyingMessageObject;
  private Runnable recordRunnable = new Runnable()
  {
    public void run()
    {
      final ByteBuffer localByteBuffer;
      int n;
      double d2;
      final double d1;
      if (MediaController.this.audioRecorder != null) {
        if (!MediaController.this.recordBuffers.isEmpty())
        {
          localByteBuffer = (ByteBuffer)MediaController.this.recordBuffers.get(0);
          MediaController.this.recordBuffers.remove(0);
          localByteBuffer.rewind();
          n = MediaController.this.audioRecorder.read(localByteBuffer, localByteBuffer.capacity());
          if (n <= 0) {
            break label510;
          }
          localByteBuffer.limit(n);
          d2 = 0.0D;
          d1 = d2;
        }
      }
      for (;;)
      {
        int k;
        int m;
        float f2;
        double d3;
        try
        {
          long l = MediaController.this.samplesCount + n / 2;
          d1 = d2;
          k = (int)(MediaController.this.samplesCount / l * MediaController.this.recordSamples.length);
          d1 = d2;
          m = MediaController.this.recordSamples.length;
          if (k != 0)
          {
            d1 = d2;
            f2 = MediaController.this.recordSamples.length / k;
            f1 = 0.0F;
            j = 0;
            if (j < k)
            {
              d1 = d2;
              MediaController.this.recordSamples[j] = MediaController.this.recordSamples[((int)f1)];
              f1 += f2;
              j += 1;
              continue;
              localByteBuffer = ByteBuffer.allocateDirect(MediaController.this.recordBufferSize);
              localByteBuffer.order(ByteOrder.nativeOrder());
              break;
            }
          }
          j = k;
          f1 = 0.0F;
          float f3 = n / 2.0F / (m - k);
          k = 0;
          d1 = d2;
          if (k < n / 2)
          {
            d1 = d2;
            int i = localByteBuffer.getShort();
            d3 = d2;
            if (i > 2500) {
              d3 = d2 + i * i;
            }
            m = j;
            f2 = f1;
            if (k != (int)f1) {
              break label538;
            }
            d1 = d3;
            m = j;
            f2 = f1;
            if (j >= MediaController.this.recordSamples.length) {
              break label538;
            }
            d1 = d3;
            MediaController.this.recordSamples[j] = i;
            f2 = f1 + f3;
            m = j + 1;
            break label538;
          }
          d1 = d2;
          MediaController.access$302(MediaController.this, l);
          d1 = d2;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          continue;
          final boolean bool = false;
          continue;
        }
        localByteBuffer.position(0);
        d1 = Math.sqrt(d1 / n / 2.0D);
        if (n != localByteBuffer.capacity())
        {
          bool = true;
          if (n != 0) {
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
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordProgressChanged, new Object[] { Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(d1) });
            }
          });
          return;
        }
        label510:
        MediaController.this.recordBuffers.add(localByteBuffer);
        MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
        return;
        label538:
        k += 1;
        int j = m;
        float f1 = f2;
        d2 = d3;
      }
    }
  };
  private short[] recordSamples = new short['Ѐ'];
  private Runnable recordStartRunnable;
  private long recordStartTime;
  private long recordTimeCount;
  private TLRPC.TL_document recordingAudio = null;
  private File recordingAudioFile = null;
  private Runnable refreshGalleryRunnable;
  private int repeatMode;
  private boolean resumeAudioOnFocusGain;
  public int roamingDownloadMask = 0;
  private long samplesCount;
  private boolean saveToGallery = true;
  private int sendAfterDone;
  private SensorManager sensorManager;
  private boolean sensorsStarted;
  private boolean shuffleMusic;
  private ArrayList<MessageObject> shuffledPlaylist = new ArrayList();
  private int startObserverToken = 0;
  private StopMediaObserverRunnable stopMediaObserverRunnable = null;
  private final Object sync = new Object();
  private long timeSinceRaise;
  private HashMap<Long, Long> typingTimes = new HashMap();
  private boolean useFrontSpeaker;
  private ArrayList<AudioBuffer> usedPlayerBuffers = new ArrayList();
  private boolean videoConvertFirstWrite = true;
  private ArrayList<MessageObject> videoConvertQueue = new ArrayList();
  private final Object videoConvertSync = new Object();
  private ArrayList<DownloadObject> videoDownloadQueue = new ArrayList();
  private final Object videoQueueSync = new Object();
  private ArrayList<MessageObject> voiceMessagesPlaylist;
  private HashMap<Integer, MessageObject> voiceMessagesPlaylistMap;
  private boolean voiceMessagesPlaylistUnread;
  public int wifiDownloadMask = 0;
  
  static
  {
    projectionPhotos = new String[] { "_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "orientation" };
    projectionVideo = new String[] { "_id", "bucket_id", "bucket_display_name", "_data", "datetaken" };
  }
  
  public MediaController()
  {
    for (;;)
    {
      try
      {
        this.recordBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
        if (this.recordBufferSize <= 0) {
          this.recordBufferSize = 1280;
        }
        this.playerBufferSize = AudioTrack.getMinBufferSize(48000, 4, 2);
        if (this.playerBufferSize > 0) {
          continue;
        }
        this.playerBufferSize = 3840;
      }
      catch (Exception localException1)
      {
        ByteBuffer localByteBuffer;
        FileLog.e("tmessages", localException1);
        try
        {
          this.sensorManager = ((SensorManager)ApplicationLoader.applicationContext.getSystemService("sensor"));
          this.linearSensor = this.sensorManager.getDefaultSensor(10);
          this.gravitySensor = this.sensorManager.getDefaultSensor(9);
          if ((this.linearSensor == null) || (this.gravitySensor == null))
          {
            FileLog.e("tmessages", "gravity or linear sensor not found");
            this.accelerometerSensor = this.sensorManager.getDefaultSensor(1);
            this.linearSensor = null;
            this.gravitySensor = null;
          }
          this.proximitySensor = this.sensorManager.getDefaultSensor(8);
          this.proximityWakeLock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
        }
        catch (Exception localException3)
        {
          try
          {
            ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, new GalleryObserverExternal());
          }
          catch (Exception localException3)
          {
            try
            {
              Object localObject1;
              Object localObject2;
              ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, false, new GalleryObserverInternal());
              try
              {
                localObject1 = new PhoneStateListener()
                {
                  public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
                  {
                    if (paramAnonymousInt == 1) {
                      if ((!MediaController.this.isPlayingAudio(MediaController.this.getPlayingMessageObject())) || (MediaController.this.isAudioPaused())) {}
                    }
                    do
                    {
                      MediaController.this.pauseAudio(MediaController.this.getPlayingMessageObject());
                      for (;;)
                      {
                        MediaController.access$2102(MediaController.this, true);
                        return;
                        if ((MediaController.this.recordStartRunnable != null) || (MediaController.this.recordingAudio != null)) {
                          MediaController.this.stopRecording(2);
                        }
                      }
                      if (paramAnonymousInt == 0)
                      {
                        MediaController.access$2102(MediaController.this, false);
                        return;
                      }
                    } while (paramAnonymousInt != 2);
                    MediaController.access$2102(MediaController.this, true);
                  }
                };
                localObject2 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
                if (localObject2 != null) {
                  ((TelephonyManager)localObject2).listen((PhoneStateListener)localObject1, 32);
                }
                return;
              }
              catch (Exception localException5)
              {
                FileLog.e("tmessages", localException5);
                return;
              }
              localException2 = localException2;
              FileLog.e("tmessages", localException2);
              continue;
              this.mediaProjections = new String[] { "_data", "_display_name", "bucket_display_name", "datetaken", "title" };
              continue;
              localException3 = localException3;
              FileLog.e("tmessages", localException3);
              continue;
            }
            catch (Exception localException4)
            {
              FileLog.e("tmessages", localException4);
              continue;
            }
          }
        }
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        this.recordQueue = new DispatchQueue("recordQueue");
        this.recordQueue.setPriority(10);
        this.fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue.setPriority(10);
        this.playerQueue = new DispatchQueue("playerQueue");
        this.fileDecodingQueue = new DispatchQueue("fileDecodingQueue");
        localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.mobileDataDownloadMask = ((SharedPreferences)localObject1).getInt("mobileDataDownloadMask", 51);
        this.wifiDownloadMask = ((SharedPreferences)localObject1).getInt("wifiDownloadMask", 51);
        this.roamingDownloadMask = ((SharedPreferences)localObject1).getInt("roamingDownloadMask", 0);
        this.saveToGallery = ((SharedPreferences)localObject1).getBoolean("save_gallery", false);
        this.autoplayGifs = ((SharedPreferences)localObject1).getBoolean("autoplay_gif", true);
        this.raiseToSpeak = ((SharedPreferences)localObject1).getBoolean("raise_to_speak", true);
        this.customTabs = ((SharedPreferences)localObject1).getBoolean("custom_tabs", true);
        this.directShare = ((SharedPreferences)localObject1).getBoolean("direct_share", true);
        this.shuffleMusic = ((SharedPreferences)localObject1).getBoolean("shuffleMusic", false);
        this.repeatMode = ((SharedPreferences)localObject1).getInt("repeatMode", 0);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.didReceivedNewMessages);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileUploadProgressChanged);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.musicDidLoaded);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.httpFileDidLoaded);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.httpFileDidFailedLoad);
          }
        });
        localObject1 = new BroadcastReceiver()
        {
          public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
          {
            MediaController.this.checkAutodownloadSettings();
          }
        };
        localObject2 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        ApplicationLoader.applicationContext.registerReceiver((BroadcastReceiver)localObject1, (IntentFilter)localObject2);
        if (!UserConfig.isClientActivated()) {
          continue;
        }
        checkAutodownloadSettings();
        if (Build.VERSION.SDK_INT < 16) {
          continue;
        }
        this.mediaProjections = new String[] { "_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height" };
        int i = 0;
        continue;
        i = 0;
        continue;
      }
      if (i >= 5) {
        continue;
      }
      localByteBuffer = ByteBuffer.allocateDirect(4096);
      localByteBuffer.order(ByteOrder.nativeOrder());
      this.recordBuffers.add(localByteBuffer);
      i += 1;
    }
    while (i < 3)
    {
      this.freePlayerBuffers.add(new AudioBuffer(this.playerBufferSize));
      i += 1;
    }
  }
  
  private static void broadcastNewPhotos(int paramInt1, final ArrayList<AlbumEntry> paramArrayList1, final Integer paramInteger1, final ArrayList<AlbumEntry> paramArrayList2, final Integer paramInteger2, final AlbumEntry paramAlbumEntry, int paramInt2)
  {
    if (broadcastPhotosRunnable != null) {
      AndroidUtilities.cancelRunOnUIThread(broadcastPhotosRunnable);
    }
    paramArrayList1 = new Runnable()
    {
      public void run()
      {
        if (PhotoViewer.getInstance().isVisible())
        {
          MediaController.broadcastNewPhotos(this.val$guid, paramArrayList1, paramInteger1, paramArrayList2, paramInteger2, paramAlbumEntry, 1000);
          return;
        }
        MediaController.access$6102(null);
        MediaController.allPhotosAlbumEntry = paramAlbumEntry;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.albumsDidLoaded, new Object[] { Integer.valueOf(this.val$guid), paramArrayList1, paramInteger1, paramArrayList2, paramInteger2 });
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
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        int k = Utilities.random.nextInt(localArrayList.size());
        this.shuffledPlaylist.add(localArrayList.get(k));
        localArrayList.remove(k);
        i += 1;
      }
    }
  }
  
  private void checkAudioFocus(MessageObject paramMessageObject)
  {
    if (paramMessageObject.isVoice()) {
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
        i = NotificationsController.getInstance().audioManager.requestAudioFocus(this, 0, 1);
        if (i == 1) {
          this.audioFocus = 2;
        }
      }
      return;
      i = 2;
      continue;
      i = 1;
    }
    paramMessageObject = NotificationsController.getInstance().audioManager;
    if (i == 2) {}
    for (int i = 3;; i = 1)
    {
      i = paramMessageObject.requestAudioFocus(this, 3, i);
      break;
    }
  }
  
  private void checkConversionCanceled()
    throws Exception
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
                MediaController.access$3202(MediaController.this, true);
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
            return;
          }
        }
      }
    });
  }
  
  private void checkDownloadFinished(String paramString, int paramInt)
  {
    DownloadObject localDownloadObject = (DownloadObject)this.downloadQueueKeys.get(paramString);
    if (localDownloadObject != null)
    {
      this.downloadQueueKeys.remove(paramString);
      if ((paramInt == 0) || (paramInt == 2)) {
        MessagesStorage.getInstance().removeFromDownloadQueue(localDownloadObject.id, localDownloadObject.type, false);
      }
      if (localDownloadObject.type != 1) {
        break label82;
      }
      this.photoDownloadQueue.remove(localDownloadObject);
      if (this.photoDownloadQueue.isEmpty()) {
        newDownloadObjectsAvailable(1);
      }
    }
    label82:
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
                return;
                if (localDownloadObject.type != 2) {
                  break;
                }
                this.audioDownloadQueue.remove(localDownloadObject);
              } while (!this.audioDownloadQueue.isEmpty());
              newDownloadObjectsAvailable(2);
              return;
              if (localDownloadObject.type != 4) {
                break;
              }
              this.videoDownloadQueue.remove(localDownloadObject);
            } while (!this.videoDownloadQueue.isEmpty());
            newDownloadObjectsAvailable(4);
            return;
            if (localDownloadObject.type != 8) {
              break;
            }
            this.documentDownloadQueue.remove(localDownloadObject);
          } while (!this.documentDownloadQueue.isEmpty());
          newDownloadObjectsAvailable(8);
          return;
          if (localDownloadObject.type != 16) {
            break;
          }
          this.musicDownloadQueue.remove(localDownloadObject);
        } while (!this.musicDownloadQueue.isEmpty());
        newDownloadObjectsAvailable(16);
        return;
      } while (localDownloadObject.type != 32);
      this.gifDownloadQueue.remove(localDownloadObject);
    } while (!this.gifDownloadQueue.isEmpty());
    newDownloadObjectsAvailable(32);
  }
  
  private void checkIsNextMusicFileDownloaded()
  {
    if ((getCurrentDownloadMask() & 0x10) == 0) {}
    label23:
    label135:
    label195:
    label197:
    label210:
    label211:
    for (;;)
    {
      return;
      Object localObject1;
      MessageObject localMessageObject;
      Object localObject2;
      if (this.shuffleMusic)
      {
        localObject1 = this.shuffledPlaylist;
        if ((localObject1 == null) || (((ArrayList)localObject1).size() < 2)) {
          break label195;
        }
        int j = this.currentPlaylistNum + 1;
        int i = j;
        if (j >= ((ArrayList)localObject1).size()) {
          i = 0;
        }
        localMessageObject = (MessageObject)((ArrayList)localObject1).get(i);
        localObject2 = null;
        localObject1 = localObject2;
        if (localMessageObject.messageOwner.attachPath != null)
        {
          localObject1 = localObject2;
          if (localMessageObject.messageOwner.attachPath.length() > 0)
          {
            localObject2 = new File(localMessageObject.messageOwner.attachPath);
            localObject1 = localObject2;
            if (!((File)localObject2).exists()) {
              localObject1 = null;
            }
          }
        }
        if (localObject1 == null) {
          break label197;
        }
        localObject2 = localObject1;
        if ((localObject2 == null) || (!((File)localObject2).exists())) {
          break label210;
        }
      }
      for (;;)
      {
        if ((localObject2 == null) || (localObject2 == localObject1) || (((File)localObject2).exists()) || (!localMessageObject.isMusic())) {
          break label211;
        }
        FileLoader.getInstance().loadFile(localMessageObject.getDocument(), false, false);
        return;
        localObject1 = this.playlist;
        break label23;
        break;
        localObject2 = FileLoader.getPathToMessage(localMessageObject.messageOwner);
        break label135;
      }
    }
  }
  
  private void checkPlayerQueue()
  {
    this.playerQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        synchronized (MediaController.this.playerObjectSync)
        {
          if ((MediaController.this.audioTrackPlayer == null) || (MediaController.this.audioTrackPlayer.getPlayState() != 3)) {
            return;
          }
          ??? = null;
        }
        int i;
        synchronized (MediaController.this.playerSync)
        {
          if (!MediaController.this.usedPlayerBuffers.isEmpty())
          {
            ??? = (MediaController.AudioBuffer)MediaController.this.usedPlayerBuffers.get(0);
            MediaController.this.usedPlayerBuffers.remove(0);
          }
          if (??? != null) {
            i = 0;
          }
          try
          {
            int j = MediaController.this.audioTrackPlayer.write(((MediaController.AudioBuffer)???).bufferBytes, 0, ((MediaController.AudioBuffer)???).size);
            i = j;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              final long l;
              FileLog.e("tmessages", localException);
              continue;
              i = -1;
            }
          }
          MediaController.access$4008(MediaController.this);
          if (i > 0)
          {
            l = ((MediaController.AudioBuffer)???).pcmOffset;
            if (((MediaController.AudioBuffer)???).finished == 1) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MediaController.access$2802(MediaController.this, l);
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
            if (((MediaController.AudioBuffer)???).finished != 1) {
              MediaController.this.checkPlayerQueue();
            }
            if ((??? == null) || ((??? != null) && (((MediaController.AudioBuffer)???).finished != 1))) {
              MediaController.this.checkDecoderQueue();
            }
            if (??? == null) {
              return;
            }
            synchronized (MediaController.this.playerSync)
            {
              MediaController.this.freePlayerBuffers.add(???);
              return;
            }
            localObject5 = finally;
            throw ((Throwable)localObject5);
          }
        }
      }
    });
  }
  
  private void checkScreenshots(ArrayList<Long> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()) || (this.lastSecretChatEnterTime == 0L) || (this.lastSecretChat == null) || (!(this.lastSecretChat instanceof TLRPC.TL_encryptedChat))) {}
    int i;
    do
    {
      return;
      i = 0;
      paramArrayList = paramArrayList.iterator();
      while (paramArrayList.hasNext())
      {
        Long localLong = (Long)paramArrayList.next();
        if (((this.lastMediaCheckTime == 0L) || (localLong.longValue() > this.lastMediaCheckTime)) && (localLong.longValue() >= this.lastSecretChatEnterTime) && ((this.lastSecretChatLeaveTime == 0L) || (localLong.longValue() <= this.lastSecretChatLeaveTime + 2000L)))
        {
          this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, localLong.longValue());
          i = 1;
        }
      }
    } while (i == 0);
    SecretChatHelper.getInstance().sendScreenshotMessage(this.lastSecretChat, this.lastSecretChatVisibleMessages, null);
  }
  
  private native void closeOpusFile();
  
  /* Error */
  @TargetApi(16)
  private boolean convertVideo(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 1133	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   4: getfield 1138	org/telegram/messenger/VideoEditedInfo:originalPath	Ljava/lang/String;
    //   7: astore 36
    //   9: aload_1
    //   10: getfield 1133	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   13: getfield 1141	org/telegram/messenger/VideoEditedInfo:startTime	J
    //   16: lstore 23
    //   18: aload_1
    //   19: getfield 1133	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   22: getfield 1144	org/telegram/messenger/VideoEditedInfo:endTime	J
    //   25: lstore 29
    //   27: aload_1
    //   28: getfield 1133	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   31: getfield 1147	org/telegram/messenger/VideoEditedInfo:resultWidth	I
    //   34: istore 7
    //   36: aload_1
    //   37: getfield 1133	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   40: getfield 1150	org/telegram/messenger/VideoEditedInfo:resultHeight	I
    //   43: istore 6
    //   45: aload_1
    //   46: getfield 1133	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   49: getfield 1153	org/telegram/messenger/VideoEditedInfo:rotationValue	I
    //   52: istore 8
    //   54: aload_1
    //   55: getfield 1133	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   58: getfield 1156	org/telegram/messenger/VideoEditedInfo:originalWidth	I
    //   61: istore 10
    //   63: aload_1
    //   64: getfield 1133	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   67: getfield 1159	org/telegram/messenger/VideoEditedInfo:originalHeight	I
    //   70: istore 11
    //   72: aload_1
    //   73: getfield 1133	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   76: getfield 1162	org/telegram/messenger/VideoEditedInfo:bitrate	I
    //   79: istore 19
    //   81: iconst_0
    //   82: istore 9
    //   84: new 1062	java/io/File
    //   87: dup
    //   88: aload_1
    //   89: getfield 1052	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   92: getfield 1057	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   95: invokespecial 1063	java/io/File:<init>	(Ljava/lang/String;)V
    //   98: astore 53
    //   100: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   103: bipush 18
    //   105: if_icmpge +143 -> 248
    //   108: iload 6
    //   110: iload 7
    //   112: if_icmple +136 -> 248
    //   115: iload 7
    //   117: iload 10
    //   119: if_icmpeq +129 -> 248
    //   122: iload 6
    //   124: iload 11
    //   126: if_icmpeq +122 -> 248
    //   129: iload 7
    //   131: istore 4
    //   133: iload 6
    //   135: istore 5
    //   137: bipush 90
    //   139: istore_2
    //   140: sipush 270
    //   143: istore_3
    //   144: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   147: ldc_w 1164
    //   150: iconst_0
    //   151: invokevirtual 632	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   154: astore 54
    //   156: aload 54
    //   158: ldc_w 1166
    //   161: iconst_1
    //   162: invokeinterface 647 3 0
    //   167: istore 33
    //   169: aload 54
    //   171: invokeinterface 1170 1 0
    //   176: ldc_w 1166
    //   179: iconst_0
    //   180: invokeinterface 1176 3 0
    //   185: invokeinterface 1179 1 0
    //   190: pop
    //   191: new 1062	java/io/File
    //   194: dup
    //   195: aload 36
    //   197: invokespecial 1063	java/io/File:<init>	(Ljava/lang/String;)V
    //   200: astore 42
    //   202: aload 42
    //   204: invokevirtual 1182	java/io/File:canRead	()Z
    //   207: ifeq +8 -> 215
    //   210: iload 33
    //   212: ifne +145 -> 357
    //   215: aload_0
    //   216: aload_1
    //   217: aload 53
    //   219: iconst_1
    //   220: iconst_1
    //   221: invokespecial 1186	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   224: aload 54
    //   226: invokeinterface 1170 1 0
    //   231: ldc_w 1166
    //   234: iconst_1
    //   235: invokeinterface 1176 3 0
    //   240: invokeinterface 1179 1 0
    //   245: pop
    //   246: iconst_0
    //   247: ireturn
    //   248: iload 6
    //   250: istore 4
    //   252: iload 7
    //   254: istore 5
    //   256: iload 9
    //   258: istore_3
    //   259: iload 8
    //   261: istore_2
    //   262: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   265: bipush 20
    //   267: if_icmple -123 -> 144
    //   270: iload 8
    //   272: bipush 90
    //   274: if_icmpne +20 -> 294
    //   277: iload 7
    //   279: istore 4
    //   281: iload 6
    //   283: istore 5
    //   285: iconst_0
    //   286: istore_2
    //   287: sipush 270
    //   290: istore_3
    //   291: goto -147 -> 144
    //   294: iload 8
    //   296: sipush 180
    //   299: if_icmpne +20 -> 319
    //   302: sipush 180
    //   305: istore_3
    //   306: iconst_0
    //   307: istore_2
    //   308: iload 6
    //   310: istore 4
    //   312: iload 7
    //   314: istore 5
    //   316: goto -172 -> 144
    //   319: iload 6
    //   321: istore 4
    //   323: iload 7
    //   325: istore 5
    //   327: iload 9
    //   329: istore_3
    //   330: iload 8
    //   332: istore_2
    //   333: iload 8
    //   335: sipush 270
    //   338: if_icmpne -194 -> 144
    //   341: iload 7
    //   343: istore 4
    //   345: iload 6
    //   347: istore 5
    //   349: iconst_0
    //   350: istore_2
    //   351: bipush 90
    //   353: istore_3
    //   354: goto -210 -> 144
    //   357: aload_0
    //   358: iconst_1
    //   359: putfield 398	org/telegram/messenger/MediaController:videoConvertFirstWrite	Z
    //   362: iconst_0
    //   363: istore 33
    //   365: iconst_0
    //   366: istore 35
    //   368: invokestatic 1191	java/lang/System:currentTimeMillis	()J
    //   371: lstore 31
    //   373: iload 5
    //   375: ifeq +4969 -> 5344
    //   378: iload 4
    //   380: ifeq +4964 -> 5344
    //   383: aconst_null
    //   384: astore 37
    //   386: aconst_null
    //   387: astore 36
    //   389: aconst_null
    //   390: astore 41
    //   392: aconst_null
    //   393: astore 40
    //   395: aload 36
    //   397: astore 39
    //   399: aload 37
    //   401: astore 38
    //   403: new 1193	android/media/MediaCodec$BufferInfo
    //   406: dup
    //   407: invokespecial 1194	android/media/MediaCodec$BufferInfo:<init>	()V
    //   410: astore 55
    //   412: aload 36
    //   414: astore 39
    //   416: aload 37
    //   418: astore 38
    //   420: new 1196	org/telegram/messenger/video/Mp4Movie
    //   423: dup
    //   424: invokespecial 1197	org/telegram/messenger/video/Mp4Movie:<init>	()V
    //   427: astore 43
    //   429: aload 36
    //   431: astore 39
    //   433: aload 37
    //   435: astore 38
    //   437: aload 43
    //   439: aload 53
    //   441: invokevirtual 1201	org/telegram/messenger/video/Mp4Movie:setCacheFile	(Ljava/io/File;)V
    //   444: aload 36
    //   446: astore 39
    //   448: aload 37
    //   450: astore 38
    //   452: aload 43
    //   454: iload_2
    //   455: invokevirtual 1204	org/telegram/messenger/video/Mp4Movie:setRotation	(I)V
    //   458: aload 36
    //   460: astore 39
    //   462: aload 37
    //   464: astore 38
    //   466: aload 43
    //   468: iload 5
    //   470: iload 4
    //   472: invokevirtual 1208	org/telegram/messenger/video/Mp4Movie:setSize	(II)V
    //   475: aload 36
    //   477: astore 39
    //   479: aload 37
    //   481: astore 38
    //   483: new 1210	org/telegram/messenger/video/MP4Builder
    //   486: dup
    //   487: invokespecial 1211	org/telegram/messenger/video/MP4Builder:<init>	()V
    //   490: aload 43
    //   492: invokevirtual 1215	org/telegram/messenger/video/MP4Builder:createMovie	(Lorg/telegram/messenger/video/Mp4Movie;)Lorg/telegram/messenger/video/MP4Builder;
    //   495: astore 36
    //   497: aload 36
    //   499: astore 39
    //   501: aload 36
    //   503: astore 38
    //   505: new 1217	android/media/MediaExtractor
    //   508: dup
    //   509: invokespecial 1218	android/media/MediaExtractor:<init>	()V
    //   512: astore 37
    //   514: aload 37
    //   516: aload 42
    //   518: invokevirtual 1222	java/io/File:toString	()Ljava/lang/String;
    //   521: invokevirtual 1225	android/media/MediaExtractor:setDataSource	(Ljava/lang/String;)V
    //   524: aload_0
    //   525: invokespecial 1227	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   528: iload 5
    //   530: iload 10
    //   532: if_icmpne +14 -> 546
    //   535: iload 4
    //   537: iload 11
    //   539: if_icmpne +7 -> 546
    //   542: iload_3
    //   543: ifeq +4650 -> 5193
    //   546: aload_0
    //   547: aload 37
    //   549: iconst_0
    //   550: invokespecial 1231	org/telegram/messenger/MediaController:selectTrack	(Landroid/media/MediaExtractor;Z)I
    //   553: istore 21
    //   555: iload 21
    //   557: iflt +4871 -> 5428
    //   560: aconst_null
    //   561: astore 46
    //   563: aconst_null
    //   564: astore 47
    //   566: aconst_null
    //   567: astore 41
    //   569: aconst_null
    //   570: astore 38
    //   572: aconst_null
    //   573: astore 48
    //   575: aconst_null
    //   576: astore 39
    //   578: aconst_null
    //   579: astore 45
    //   581: ldc2_w 1232
    //   584: lstore 25
    //   586: iconst_0
    //   587: istore 15
    //   589: iconst_0
    //   590: istore 8
    //   592: iconst_0
    //   593: istore 16
    //   595: iconst_0
    //   596: istore 11
    //   598: iconst_0
    //   599: istore 7
    //   601: bipush -5
    //   603: istore 14
    //   605: iconst_0
    //   606: istore_2
    //   607: iconst_0
    //   608: istore 6
    //   610: aload 47
    //   612: astore 40
    //   614: aload 41
    //   616: astore 42
    //   618: aload 48
    //   620: astore 43
    //   622: aload 45
    //   624: astore 44
    //   626: getstatic 1238	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   629: invokevirtual 1241	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   632: astore 49
    //   634: aload 47
    //   636: astore 40
    //   638: aload 41
    //   640: astore 42
    //   642: aload 48
    //   644: astore 43
    //   646: aload 45
    //   648: astore 44
    //   650: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   653: bipush 18
    //   655: if_icmpge +4869 -> 5524
    //   658: aload 47
    //   660: astore 40
    //   662: aload 41
    //   664: astore 42
    //   666: aload 48
    //   668: astore 43
    //   670: aload 45
    //   672: astore 44
    //   674: ldc -101
    //   676: invokestatic 1245	org/telegram/messenger/MediaController:selectCodec	(Ljava/lang/String;)Landroid/media/MediaCodecInfo;
    //   679: astore 50
    //   681: aload 47
    //   683: astore 40
    //   685: aload 41
    //   687: astore 42
    //   689: aload 48
    //   691: astore 43
    //   693: aload 45
    //   695: astore 44
    //   697: aload 50
    //   699: ldc -101
    //   701: invokestatic 1249	org/telegram/messenger/MediaController:selectColorFormat	(Landroid/media/MediaCodecInfo;Ljava/lang/String;)I
    //   704: istore 10
    //   706: iload 10
    //   708: ifne +232 -> 940
    //   711: aload 47
    //   713: astore 40
    //   715: aload 41
    //   717: astore 42
    //   719: aload 48
    //   721: astore 43
    //   723: aload 45
    //   725: astore 44
    //   727: new 1007	java/lang/RuntimeException
    //   730: dup
    //   731: ldc_w 1251
    //   734: invokespecial 1010	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   737: athrow
    //   738: astore 38
    //   740: aload 42
    //   742: astore 41
    //   744: ldc_w 550
    //   747: aload 38
    //   749: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   752: iconst_1
    //   753: istore 33
    //   755: aload 44
    //   757: astore 39
    //   759: aload 43
    //   761: astore 38
    //   763: aload 37
    //   765: iload 21
    //   767: invokevirtual 1254	android/media/MediaExtractor:unselectTrack	(I)V
    //   770: aload 39
    //   772: ifnull +8 -> 780
    //   775: aload 39
    //   777: invokevirtual 1259	org/telegram/messenger/video/OutputSurface:release	()V
    //   780: aload 38
    //   782: ifnull +8 -> 790
    //   785: aload 38
    //   787: invokevirtual 1262	org/telegram/messenger/video/InputSurface:release	()V
    //   790: aload 40
    //   792: ifnull +13 -> 805
    //   795: aload 40
    //   797: invokevirtual 1267	android/media/MediaCodec:stop	()V
    //   800: aload 40
    //   802: invokevirtual 1268	android/media/MediaCodec:release	()V
    //   805: aload 41
    //   807: ifnull +13 -> 820
    //   810: aload 41
    //   812: invokevirtual 1267	android/media/MediaCodec:stop	()V
    //   815: aload 41
    //   817: invokevirtual 1268	android/media/MediaCodec:release	()V
    //   820: aload_0
    //   821: invokespecial 1227	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   824: iload 33
    //   826: ifne +28 -> 854
    //   829: iload 19
    //   831: iconst_m1
    //   832: if_icmpeq +22 -> 854
    //   835: aload_0
    //   836: aload_1
    //   837: aload 37
    //   839: aload 36
    //   841: aload 55
    //   843: lload 23
    //   845: lload 29
    //   847: aload 53
    //   849: iconst_1
    //   850: invokespecial 1272	org/telegram/messenger/MediaController:readAndWriteTrack	(Lorg/telegram/messenger/MessageObject;Landroid/media/MediaExtractor;Lorg/telegram/messenger/video/MP4Builder;Landroid/media/MediaCodec$BufferInfo;JJLjava/io/File;Z)J
    //   853: pop2
    //   854: aload 37
    //   856: ifnull +8 -> 864
    //   859: aload 37
    //   861: invokevirtual 1273	android/media/MediaExtractor:release	()V
    //   864: aload 36
    //   866: ifnull +9 -> 875
    //   869: aload 36
    //   871: iconst_0
    //   872: invokevirtual 1276	org/telegram/messenger/video/MP4Builder:finishMovie	(Z)V
    //   875: ldc_w 550
    //   878: new 1278	java/lang/StringBuilder
    //   881: dup
    //   882: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   885: ldc_w 1281
    //   888: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   891: invokestatic 1191	java/lang/System:currentTimeMillis	()J
    //   894: lload 31
    //   896: lsub
    //   897: invokevirtual 1288	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   900: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   903: invokestatic 587	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   906: aload 54
    //   908: invokeinterface 1170 1 0
    //   913: ldc_w 1166
    //   916: iconst_1
    //   917: invokeinterface 1176 3 0
    //   922: invokeinterface 1179 1 0
    //   927: pop
    //   928: aload_0
    //   929: aload_1
    //   930: aload 53
    //   932: iconst_1
    //   933: iload 33
    //   935: invokespecial 1186	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   938: iconst_1
    //   939: ireturn
    //   940: aload 47
    //   942: astore 40
    //   944: aload 41
    //   946: astore 42
    //   948: aload 48
    //   950: astore 43
    //   952: aload 45
    //   954: astore 44
    //   956: aload 50
    //   958: invokevirtual 1294	android/media/MediaCodecInfo:getName	()Ljava/lang/String;
    //   961: astore 51
    //   963: aload 47
    //   965: astore 40
    //   967: aload 41
    //   969: astore 42
    //   971: aload 48
    //   973: astore 43
    //   975: aload 45
    //   977: astore 44
    //   979: aload 51
    //   981: ldc_w 1296
    //   984: invokevirtual 1300	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   987: ifeq +1485 -> 2472
    //   990: iconst_1
    //   991: istore 9
    //   993: aload 47
    //   995: astore 40
    //   997: aload 41
    //   999: astore 42
    //   1001: aload 48
    //   1003: astore 43
    //   1005: aload 45
    //   1007: astore 44
    //   1009: iload 9
    //   1011: istore_2
    //   1012: iload 7
    //   1014: istore 6
    //   1016: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   1019: bipush 16
    //   1021: if_icmpne +67 -> 1088
    //   1024: aload 47
    //   1026: astore 40
    //   1028: aload 41
    //   1030: astore 42
    //   1032: aload 48
    //   1034: astore 43
    //   1036: aload 45
    //   1038: astore 44
    //   1040: aload 49
    //   1042: ldc_w 1302
    //   1045: invokevirtual 1305	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1048: ifne +4394 -> 5442
    //   1051: aload 47
    //   1053: astore 40
    //   1055: aload 41
    //   1057: astore 42
    //   1059: aload 48
    //   1061: astore 43
    //   1063: aload 45
    //   1065: astore 44
    //   1067: iload 9
    //   1069: istore_2
    //   1070: iload 7
    //   1072: istore 6
    //   1074: aload 49
    //   1076: ldc_w 1307
    //   1079: invokevirtual 1305	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1082: ifeq +6 -> 1088
    //   1085: goto +4357 -> 5442
    //   1088: aload 47
    //   1090: astore 40
    //   1092: aload 41
    //   1094: astore 42
    //   1096: aload 48
    //   1098: astore 43
    //   1100: aload 45
    //   1102: astore 44
    //   1104: ldc_w 550
    //   1107: new 1278	java/lang/StringBuilder
    //   1110: dup
    //   1111: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   1114: ldc_w 1309
    //   1117: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1120: aload 50
    //   1122: invokevirtual 1294	android/media/MediaCodecInfo:getName	()Ljava/lang/String;
    //   1125: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1128: ldc_w 1311
    //   1131: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1134: aload 49
    //   1136: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1139: ldc_w 1313
    //   1142: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1145: getstatic 1316	android/os/Build:MODEL	Ljava/lang/String;
    //   1148: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1151: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1154: invokestatic 587	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   1157: iload 6
    //   1159: istore 11
    //   1161: iload_2
    //   1162: istore 7
    //   1164: aload 47
    //   1166: astore 40
    //   1168: aload 41
    //   1170: astore 42
    //   1172: aload 48
    //   1174: astore 43
    //   1176: aload 45
    //   1178: astore 44
    //   1180: ldc_w 550
    //   1183: new 1278	java/lang/StringBuilder
    //   1186: dup
    //   1187: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   1190: ldc_w 1318
    //   1193: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1196: iload 10
    //   1198: invokevirtual 1321	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1201: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1204: invokestatic 587	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   1207: iconst_0
    //   1208: istore 12
    //   1210: aload 47
    //   1212: astore 40
    //   1214: aload 41
    //   1216: astore 42
    //   1218: aload 48
    //   1220: astore 43
    //   1222: aload 45
    //   1224: astore 44
    //   1226: iload 5
    //   1228: iload 4
    //   1230: imul
    //   1231: iconst_3
    //   1232: imul
    //   1233: iconst_2
    //   1234: idiv
    //   1235: istore 9
    //   1237: iload 7
    //   1239: ifne +1383 -> 2622
    //   1242: iload 9
    //   1244: istore_2
    //   1245: iload 12
    //   1247: istore 6
    //   1249: iload 4
    //   1251: bipush 16
    //   1253: irem
    //   1254: ifeq +48 -> 1302
    //   1257: iload 5
    //   1259: iload 4
    //   1261: bipush 16
    //   1263: iload 4
    //   1265: bipush 16
    //   1267: irem
    //   1268: isub
    //   1269: iadd
    //   1270: iload 4
    //   1272: isub
    //   1273: imul
    //   1274: istore 6
    //   1276: aload 47
    //   1278: astore 40
    //   1280: aload 41
    //   1282: astore 42
    //   1284: aload 48
    //   1286: astore 43
    //   1288: aload 45
    //   1290: astore 44
    //   1292: iload 9
    //   1294: iload 6
    //   1296: iconst_5
    //   1297: imul
    //   1298: iconst_4
    //   1299: idiv
    //   1300: iadd
    //   1301: istore_2
    //   1302: aload 47
    //   1304: astore 40
    //   1306: aload 41
    //   1308: astore 42
    //   1310: aload 48
    //   1312: astore 43
    //   1314: aload 45
    //   1316: astore 44
    //   1318: aload 37
    //   1320: iload 21
    //   1322: invokevirtual 1323	android/media/MediaExtractor:selectTrack	(I)V
    //   1325: lload 23
    //   1327: lconst_0
    //   1328: lcmp
    //   1329: ifle +1474 -> 2803
    //   1332: aload 47
    //   1334: astore 40
    //   1336: aload 41
    //   1338: astore 42
    //   1340: aload 48
    //   1342: astore 43
    //   1344: aload 45
    //   1346: astore 44
    //   1348: aload 37
    //   1350: lload 23
    //   1352: iconst_0
    //   1353: invokevirtual 1327	android/media/MediaExtractor:seekTo	(JI)V
    //   1356: aload 47
    //   1358: astore 40
    //   1360: aload 41
    //   1362: astore 42
    //   1364: aload 48
    //   1366: astore 43
    //   1368: aload 45
    //   1370: astore 44
    //   1372: aload 37
    //   1374: iload 21
    //   1376: invokevirtual 1331	android/media/MediaExtractor:getTrackFormat	(I)Landroid/media/MediaFormat;
    //   1379: astore 49
    //   1381: aload 47
    //   1383: astore 40
    //   1385: aload 41
    //   1387: astore 42
    //   1389: aload 48
    //   1391: astore 43
    //   1393: aload 45
    //   1395: astore 44
    //   1397: ldc -101
    //   1399: iload 5
    //   1401: iload 4
    //   1403: invokestatic 1337	android/media/MediaFormat:createVideoFormat	(Ljava/lang/String;II)Landroid/media/MediaFormat;
    //   1406: astore 50
    //   1408: aload 47
    //   1410: astore 40
    //   1412: aload 41
    //   1414: astore 42
    //   1416: aload 48
    //   1418: astore 43
    //   1420: aload 45
    //   1422: astore 44
    //   1424: aload 50
    //   1426: ldc_w 1339
    //   1429: iload 10
    //   1431: invokevirtual 1342	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1434: iload 19
    //   1436: ifle +1448 -> 2884
    //   1439: iload 19
    //   1441: istore 7
    //   1443: aload 47
    //   1445: astore 40
    //   1447: aload 41
    //   1449: astore 42
    //   1451: aload 48
    //   1453: astore 43
    //   1455: aload 45
    //   1457: astore 44
    //   1459: aload 50
    //   1461: ldc_w 1343
    //   1464: iload 7
    //   1466: invokevirtual 1342	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1469: aload 47
    //   1471: astore 40
    //   1473: aload 41
    //   1475: astore 42
    //   1477: aload 48
    //   1479: astore 43
    //   1481: aload 45
    //   1483: astore 44
    //   1485: aload 50
    //   1487: ldc_w 1345
    //   1490: bipush 25
    //   1492: invokevirtual 1342	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1495: aload 47
    //   1497: astore 40
    //   1499: aload 41
    //   1501: astore 42
    //   1503: aload 48
    //   1505: astore 43
    //   1507: aload 45
    //   1509: astore 44
    //   1511: aload 50
    //   1513: ldc_w 1347
    //   1516: bipush 10
    //   1518: invokevirtual 1342	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1521: aload 47
    //   1523: astore 40
    //   1525: aload 41
    //   1527: astore 42
    //   1529: aload 48
    //   1531: astore 43
    //   1533: aload 45
    //   1535: astore 44
    //   1537: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   1540: bipush 18
    //   1542: if_icmpge +58 -> 1600
    //   1545: aload 47
    //   1547: astore 40
    //   1549: aload 41
    //   1551: astore 42
    //   1553: aload 48
    //   1555: astore 43
    //   1557: aload 45
    //   1559: astore 44
    //   1561: aload 50
    //   1563: ldc_w 1349
    //   1566: iload 5
    //   1568: bipush 32
    //   1570: iadd
    //   1571: invokevirtual 1342	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1574: aload 47
    //   1576: astore 40
    //   1578: aload 41
    //   1580: astore 42
    //   1582: aload 48
    //   1584: astore 43
    //   1586: aload 45
    //   1588: astore 44
    //   1590: aload 50
    //   1592: ldc_w 1351
    //   1595: iload 4
    //   1597: invokevirtual 1342	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1600: aload 47
    //   1602: astore 40
    //   1604: aload 41
    //   1606: astore 42
    //   1608: aload 48
    //   1610: astore 43
    //   1612: aload 45
    //   1614: astore 44
    //   1616: ldc -101
    //   1618: invokestatic 1355	android/media/MediaCodec:createEncoderByType	(Ljava/lang/String;)Landroid/media/MediaCodec;
    //   1621: astore 41
    //   1623: aload 47
    //   1625: astore 40
    //   1627: aload 41
    //   1629: astore 42
    //   1631: aload 48
    //   1633: astore 43
    //   1635: aload 45
    //   1637: astore 44
    //   1639: aload 41
    //   1641: aload 50
    //   1643: aconst_null
    //   1644: aconst_null
    //   1645: iconst_1
    //   1646: invokevirtual 1359	android/media/MediaCodec:configure	(Landroid/media/MediaFormat;Landroid/view/Surface;Landroid/media/MediaCrypto;I)V
    //   1649: aload 47
    //   1651: astore 40
    //   1653: aload 41
    //   1655: astore 42
    //   1657: aload 48
    //   1659: astore 43
    //   1661: aload 45
    //   1663: astore 44
    //   1665: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   1668: bipush 18
    //   1670: if_icmplt +38 -> 1708
    //   1673: aload 47
    //   1675: astore 40
    //   1677: aload 41
    //   1679: astore 42
    //   1681: aload 48
    //   1683: astore 43
    //   1685: aload 45
    //   1687: astore 44
    //   1689: new 1261	org/telegram/messenger/video/InputSurface
    //   1692: dup
    //   1693: aload 41
    //   1695: invokevirtual 1363	android/media/MediaCodec:createInputSurface	()Landroid/view/Surface;
    //   1698: invokespecial 1366	org/telegram/messenger/video/InputSurface:<init>	(Landroid/view/Surface;)V
    //   1701: astore 38
    //   1703: aload 38
    //   1705: invokevirtual 1369	org/telegram/messenger/video/InputSurface:makeCurrent	()V
    //   1708: aload 47
    //   1710: astore 40
    //   1712: aload 41
    //   1714: astore 42
    //   1716: aload 38
    //   1718: astore 43
    //   1720: aload 45
    //   1722: astore 44
    //   1724: aload 41
    //   1726: invokevirtual 1372	android/media/MediaCodec:start	()V
    //   1729: aload 47
    //   1731: astore 40
    //   1733: aload 41
    //   1735: astore 42
    //   1737: aload 38
    //   1739: astore 43
    //   1741: aload 45
    //   1743: astore 44
    //   1745: aload 49
    //   1747: ldc_w 1374
    //   1750: invokevirtual 1378	android/media/MediaFormat:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   1753: invokestatic 1381	android/media/MediaCodec:createDecoderByType	(Ljava/lang/String;)Landroid/media/MediaCodec;
    //   1756: astore 46
    //   1758: aload 46
    //   1760: astore 40
    //   1762: aload 41
    //   1764: astore 42
    //   1766: aload 38
    //   1768: astore 43
    //   1770: aload 45
    //   1772: astore 44
    //   1774: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   1777: bipush 18
    //   1779: if_icmplt +1113 -> 2892
    //   1782: aload 46
    //   1784: astore 40
    //   1786: aload 41
    //   1788: astore 42
    //   1790: aload 38
    //   1792: astore 43
    //   1794: aload 45
    //   1796: astore 44
    //   1798: new 1256	org/telegram/messenger/video/OutputSurface
    //   1801: dup
    //   1802: invokespecial 1382	org/telegram/messenger/video/OutputSurface:<init>	()V
    //   1805: astore 39
    //   1807: aload 46
    //   1809: astore 40
    //   1811: aload 41
    //   1813: astore 42
    //   1815: aload 38
    //   1817: astore 43
    //   1819: aload 39
    //   1821: astore 44
    //   1823: aload 46
    //   1825: aload 49
    //   1827: aload 39
    //   1829: invokevirtual 1385	org/telegram/messenger/video/OutputSurface:getSurface	()Landroid/view/Surface;
    //   1832: aconst_null
    //   1833: iconst_0
    //   1834: invokevirtual 1359	android/media/MediaCodec:configure	(Landroid/media/MediaFormat;Landroid/view/Surface;Landroid/media/MediaCrypto;I)V
    //   1837: aload 46
    //   1839: astore 40
    //   1841: aload 41
    //   1843: astore 42
    //   1845: aload 38
    //   1847: astore 43
    //   1849: aload 39
    //   1851: astore 44
    //   1853: aload 46
    //   1855: invokevirtual 1372	android/media/MediaCodec:start	()V
    //   1858: aconst_null
    //   1859: astore 47
    //   1861: aconst_null
    //   1862: astore 45
    //   1864: aconst_null
    //   1865: astore 49
    //   1867: aload 46
    //   1869: astore 40
    //   1871: aload 41
    //   1873: astore 42
    //   1875: aload 38
    //   1877: astore 43
    //   1879: aload 39
    //   1881: astore 44
    //   1883: aload 49
    //   1885: astore 48
    //   1887: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   1890: bipush 21
    //   1892: if_icmpge +116 -> 2008
    //   1895: aload 46
    //   1897: astore 40
    //   1899: aload 41
    //   1901: astore 42
    //   1903: aload 38
    //   1905: astore 43
    //   1907: aload 39
    //   1909: astore 44
    //   1911: aload 46
    //   1913: invokevirtual 1389	android/media/MediaCodec:getInputBuffers	()[Ljava/nio/ByteBuffer;
    //   1916: astore 50
    //   1918: aload 46
    //   1920: astore 40
    //   1922: aload 41
    //   1924: astore 42
    //   1926: aload 38
    //   1928: astore 43
    //   1930: aload 39
    //   1932: astore 44
    //   1934: aload 41
    //   1936: invokevirtual 1392	android/media/MediaCodec:getOutputBuffers	()[Ljava/nio/ByteBuffer;
    //   1939: astore 51
    //   1941: aload 46
    //   1943: astore 40
    //   1945: aload 41
    //   1947: astore 42
    //   1949: aload 38
    //   1951: astore 43
    //   1953: aload 39
    //   1955: astore 44
    //   1957: aload 50
    //   1959: astore 47
    //   1961: aload 49
    //   1963: astore 48
    //   1965: aload 51
    //   1967: astore 45
    //   1969: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   1972: bipush 18
    //   1974: if_icmpge +34 -> 2008
    //   1977: aload 46
    //   1979: astore 40
    //   1981: aload 41
    //   1983: astore 42
    //   1985: aload 38
    //   1987: astore 43
    //   1989: aload 39
    //   1991: astore 44
    //   1993: aload 41
    //   1995: invokevirtual 1389	android/media/MediaCodec:getInputBuffers	()[Ljava/nio/ByteBuffer;
    //   1998: astore 48
    //   2000: aload 51
    //   2002: astore 45
    //   2004: aload 50
    //   2006: astore 47
    //   2008: aload 46
    //   2010: astore 40
    //   2012: aload 41
    //   2014: astore 42
    //   2016: aload 38
    //   2018: astore 43
    //   2020: aload 39
    //   2022: astore 44
    //   2024: aload_0
    //   2025: invokespecial 1227	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   2028: iload 8
    //   2030: istore_3
    //   2031: aload 45
    //   2033: astore 49
    //   2035: iload 15
    //   2037: ifne +3132 -> 5169
    //   2040: aload 46
    //   2042: astore 40
    //   2044: aload 41
    //   2046: astore 42
    //   2048: aload 38
    //   2050: astore 43
    //   2052: aload 39
    //   2054: astore 44
    //   2056: aload_0
    //   2057: invokespecial 1227	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   2060: iload_3
    //   2061: istore 7
    //   2063: iload_3
    //   2064: ifne +3387 -> 5451
    //   2067: iconst_0
    //   2068: istore 7
    //   2070: aload 46
    //   2072: astore 40
    //   2074: aload 41
    //   2076: astore 42
    //   2078: aload 38
    //   2080: astore 43
    //   2082: aload 39
    //   2084: astore 44
    //   2086: aload 37
    //   2088: invokevirtual 1395	android/media/MediaExtractor:getSampleTrackIndex	()I
    //   2091: istore 12
    //   2093: iload 12
    //   2095: iload 21
    //   2097: if_icmpne +3438 -> 5535
    //   2100: aload 46
    //   2102: astore 40
    //   2104: aload 41
    //   2106: astore 42
    //   2108: aload 38
    //   2110: astore 43
    //   2112: aload 39
    //   2114: astore 44
    //   2116: aload 46
    //   2118: ldc2_w 1396
    //   2121: invokevirtual 1401	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   2124: istore 12
    //   2126: iload 7
    //   2128: istore 9
    //   2130: iload_3
    //   2131: istore 8
    //   2133: iload 12
    //   2135: iflt +99 -> 2234
    //   2138: aload 46
    //   2140: astore 40
    //   2142: aload 41
    //   2144: astore 42
    //   2146: aload 38
    //   2148: astore 43
    //   2150: aload 39
    //   2152: astore 44
    //   2154: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   2157: bipush 21
    //   2159: if_icmpge +766 -> 2925
    //   2162: aload 47
    //   2164: iload 12
    //   2166: aaload
    //   2167: astore 45
    //   2169: aload 46
    //   2171: astore 40
    //   2173: aload 41
    //   2175: astore 42
    //   2177: aload 38
    //   2179: astore 43
    //   2181: aload 39
    //   2183: astore 44
    //   2185: aload 37
    //   2187: aload 45
    //   2189: iconst_0
    //   2190: invokevirtual 1404	android/media/MediaExtractor:readSampleData	(Ljava/nio/ByteBuffer;I)I
    //   2193: istore 8
    //   2195: iload 8
    //   2197: ifge +756 -> 2953
    //   2200: aload 46
    //   2202: astore 40
    //   2204: aload 41
    //   2206: astore 42
    //   2208: aload 38
    //   2210: astore 43
    //   2212: aload 39
    //   2214: astore 44
    //   2216: aload 46
    //   2218: iload 12
    //   2220: iconst_0
    //   2221: iconst_0
    //   2222: lconst_0
    //   2223: iconst_4
    //   2224: invokevirtual 1408	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2227: iconst_1
    //   2228: istore 8
    //   2230: iload 7
    //   2232: istore 9
    //   2234: iload 8
    //   2236: istore 7
    //   2238: iload 9
    //   2240: ifeq +3211 -> 5451
    //   2243: aload 46
    //   2245: astore 40
    //   2247: aload 41
    //   2249: astore 42
    //   2251: aload 38
    //   2253: astore 43
    //   2255: aload 39
    //   2257: astore 44
    //   2259: aload 46
    //   2261: ldc2_w 1396
    //   2264: invokevirtual 1401	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   2267: istore_3
    //   2268: iload 8
    //   2270: istore 7
    //   2272: iload_3
    //   2273: iflt +3178 -> 5451
    //   2276: aload 46
    //   2278: astore 40
    //   2280: aload 41
    //   2282: astore 42
    //   2284: aload 38
    //   2286: astore 43
    //   2288: aload 39
    //   2290: astore 44
    //   2292: aload 46
    //   2294: iload_3
    //   2295: iconst_0
    //   2296: iconst_0
    //   2297: lconst_0
    //   2298: iconst_4
    //   2299: invokevirtual 1408	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2302: iconst_1
    //   2303: istore 7
    //   2305: goto +3146 -> 5451
    //   2308: aload 46
    //   2310: astore 40
    //   2312: aload 41
    //   2314: astore 42
    //   2316: aload 38
    //   2318: astore 43
    //   2320: aload 39
    //   2322: astore 44
    //   2324: aload_0
    //   2325: invokespecial 1227	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   2328: aload 46
    //   2330: astore 40
    //   2332: aload 41
    //   2334: astore 42
    //   2336: aload 38
    //   2338: astore 43
    //   2340: aload 39
    //   2342: astore 44
    //   2344: aload 41
    //   2346: aload 55
    //   2348: ldc2_w 1396
    //   2351: invokevirtual 1412	android/media/MediaCodec:dequeueOutputBuffer	(Landroid/media/MediaCodec$BufferInfo;J)I
    //   2354: istore 16
    //   2356: iload 16
    //   2358: iconst_m1
    //   2359: if_icmpne +658 -> 3017
    //   2362: iconst_0
    //   2363: istore 14
    //   2365: iload 9
    //   2367: istore_3
    //   2368: iload 17
    //   2370: istore 15
    //   2372: aload 45
    //   2374: astore 49
    //   2376: iload 14
    //   2378: istore 13
    //   2380: aload 49
    //   2382: astore 45
    //   2384: iload 15
    //   2386: istore 17
    //   2388: iload_3
    //   2389: istore 9
    //   2391: iload 16
    //   2393: iconst_m1
    //   2394: if_icmpne +3094 -> 5488
    //   2397: iload 14
    //   2399: istore 13
    //   2401: aload 49
    //   2403: astore 45
    //   2405: iload 15
    //   2407: istore 17
    //   2409: iload_3
    //   2410: istore 9
    //   2412: iload 12
    //   2414: ifne +3074 -> 5488
    //   2417: aload 46
    //   2419: astore 40
    //   2421: aload 41
    //   2423: astore 42
    //   2425: aload 38
    //   2427: astore 43
    //   2429: aload 39
    //   2431: astore 44
    //   2433: aload 46
    //   2435: aload 55
    //   2437: ldc2_w 1396
    //   2440: invokevirtual 1412	android/media/MediaCodec:dequeueOutputBuffer	(Landroid/media/MediaCodec$BufferInfo;J)I
    //   2443: istore 20
    //   2445: iload 20
    //   2447: iconst_m1
    //   2448: if_icmpne +1545 -> 3993
    //   2451: iconst_0
    //   2452: istore 7
    //   2454: iload 14
    //   2456: istore 13
    //   2458: aload 49
    //   2460: astore 45
    //   2462: iload 15
    //   2464: istore 17
    //   2466: iload_3
    //   2467: istore 9
    //   2469: goto +3019 -> 5488
    //   2472: aload 47
    //   2474: astore 40
    //   2476: aload 41
    //   2478: astore 42
    //   2480: aload 48
    //   2482: astore 43
    //   2484: aload 45
    //   2486: astore 44
    //   2488: aload 51
    //   2490: ldc_w 1414
    //   2493: invokevirtual 1300	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   2496: ifeq +12 -> 2508
    //   2499: iconst_2
    //   2500: istore_2
    //   2501: iload 7
    //   2503: istore 6
    //   2505: goto -1417 -> 1088
    //   2508: aload 47
    //   2510: astore 40
    //   2512: aload 41
    //   2514: astore 42
    //   2516: aload 48
    //   2518: astore 43
    //   2520: aload 45
    //   2522: astore 44
    //   2524: aload 51
    //   2526: ldc_w 1416
    //   2529: invokevirtual 1305	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2532: ifeq +12 -> 2544
    //   2535: iconst_3
    //   2536: istore_2
    //   2537: iload 7
    //   2539: istore 6
    //   2541: goto -1453 -> 1088
    //   2544: aload 47
    //   2546: astore 40
    //   2548: aload 41
    //   2550: astore 42
    //   2552: aload 48
    //   2554: astore 43
    //   2556: aload 45
    //   2558: astore 44
    //   2560: aload 51
    //   2562: ldc_w 1418
    //   2565: invokevirtual 1305	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2568: ifeq +11 -> 2579
    //   2571: iconst_4
    //   2572: istore_2
    //   2573: iconst_1
    //   2574: istore 6
    //   2576: goto -1488 -> 1088
    //   2579: aload 47
    //   2581: astore 40
    //   2583: aload 41
    //   2585: astore 42
    //   2587: aload 48
    //   2589: astore 43
    //   2591: aload 45
    //   2593: astore 44
    //   2595: iload 6
    //   2597: istore_2
    //   2598: iload 7
    //   2600: istore 6
    //   2602: aload 51
    //   2604: ldc_w 1420
    //   2607: invokevirtual 1305	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2610: ifeq -1522 -> 1088
    //   2613: iconst_5
    //   2614: istore_2
    //   2615: iload 7
    //   2617: istore 6
    //   2619: goto -1531 -> 1088
    //   2622: iload 7
    //   2624: iconst_1
    //   2625: if_icmpne +70 -> 2695
    //   2628: aload 47
    //   2630: astore 40
    //   2632: aload 41
    //   2634: astore 42
    //   2636: aload 48
    //   2638: astore 43
    //   2640: aload 45
    //   2642: astore 44
    //   2644: iload 9
    //   2646: istore_2
    //   2647: iload 12
    //   2649: istore 6
    //   2651: aload 49
    //   2653: invokevirtual 1241	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   2656: ldc_w 1302
    //   2659: invokevirtual 1305	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2662: ifne -1360 -> 1302
    //   2665: iload 5
    //   2667: iload 4
    //   2669: imul
    //   2670: sipush 2047
    //   2673: iadd
    //   2674: sipush 63488
    //   2677: iand
    //   2678: iload 5
    //   2680: iload 4
    //   2682: imul
    //   2683: isub
    //   2684: istore 6
    //   2686: iload 9
    //   2688: iload 6
    //   2690: iadd
    //   2691: istore_2
    //   2692: goto -1390 -> 1302
    //   2695: iload 9
    //   2697: istore_2
    //   2698: iload 12
    //   2700: istore 6
    //   2702: iload 7
    //   2704: iconst_5
    //   2705: if_icmpeq -1403 -> 1302
    //   2708: iload 9
    //   2710: istore_2
    //   2711: iload 12
    //   2713: istore 6
    //   2715: iload 7
    //   2717: iconst_3
    //   2718: if_icmpne -1416 -> 1302
    //   2721: aload 47
    //   2723: astore 40
    //   2725: aload 41
    //   2727: astore 42
    //   2729: aload 48
    //   2731: astore 43
    //   2733: aload 45
    //   2735: astore 44
    //   2737: iload 9
    //   2739: istore_2
    //   2740: iload 12
    //   2742: istore 6
    //   2744: aload 49
    //   2746: ldc_w 1422
    //   2749: invokevirtual 1305	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2752: ifeq -1450 -> 1302
    //   2755: iload 5
    //   2757: iload 4
    //   2759: bipush 16
    //   2761: iload 4
    //   2763: bipush 16
    //   2765: irem
    //   2766: isub
    //   2767: iadd
    //   2768: iload 4
    //   2770: isub
    //   2771: imul
    //   2772: istore 6
    //   2774: aload 47
    //   2776: astore 40
    //   2778: aload 41
    //   2780: astore 42
    //   2782: aload 48
    //   2784: astore 43
    //   2786: aload 45
    //   2788: astore 44
    //   2790: iload 9
    //   2792: iload 6
    //   2794: iconst_5
    //   2795: imul
    //   2796: iconst_4
    //   2797: idiv
    //   2798: iadd
    //   2799: istore_2
    //   2800: goto -1498 -> 1302
    //   2803: aload 47
    //   2805: astore 40
    //   2807: aload 41
    //   2809: astore 42
    //   2811: aload 48
    //   2813: astore 43
    //   2815: aload 45
    //   2817: astore 44
    //   2819: aload 37
    //   2821: lconst_0
    //   2822: iconst_0
    //   2823: invokevirtual 1327	android/media/MediaExtractor:seekTo	(JI)V
    //   2826: goto -1470 -> 1356
    //   2829: astore_1
    //   2830: aload 37
    //   2832: ifnull +8 -> 2840
    //   2835: aload 37
    //   2837: invokevirtual 1273	android/media/MediaExtractor:release	()V
    //   2840: aload 36
    //   2842: ifnull +9 -> 2851
    //   2845: aload 36
    //   2847: iconst_0
    //   2848: invokevirtual 1276	org/telegram/messenger/video/MP4Builder:finishMovie	(Z)V
    //   2851: ldc_w 550
    //   2854: new 1278	java/lang/StringBuilder
    //   2857: dup
    //   2858: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   2861: ldc_w 1281
    //   2864: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2867: invokestatic 1191	java/lang/System:currentTimeMillis	()J
    //   2870: lload 31
    //   2872: lsub
    //   2873: invokevirtual 1288	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   2876: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2879: invokestatic 587	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   2882: aload_1
    //   2883: athrow
    //   2884: ldc_w 1423
    //   2887: istore 7
    //   2889: goto -1446 -> 1443
    //   2892: aload 46
    //   2894: astore 40
    //   2896: aload 41
    //   2898: astore 42
    //   2900: aload 38
    //   2902: astore 43
    //   2904: aload 45
    //   2906: astore 44
    //   2908: new 1256	org/telegram/messenger/video/OutputSurface
    //   2911: dup
    //   2912: iload 5
    //   2914: iload 4
    //   2916: iload_3
    //   2917: invokespecial 1426	org/telegram/messenger/video/OutputSurface:<init>	(III)V
    //   2920: astore 39
    //   2922: goto -1115 -> 1807
    //   2925: aload 46
    //   2927: astore 40
    //   2929: aload 41
    //   2931: astore 42
    //   2933: aload 38
    //   2935: astore 43
    //   2937: aload 39
    //   2939: astore 44
    //   2941: aload 46
    //   2943: iload 12
    //   2945: invokevirtual 1429	android/media/MediaCodec:getInputBuffer	(I)Ljava/nio/ByteBuffer;
    //   2948: astore 45
    //   2950: goto -781 -> 2169
    //   2953: aload 46
    //   2955: astore 40
    //   2957: aload 41
    //   2959: astore 42
    //   2961: aload 38
    //   2963: astore 43
    //   2965: aload 39
    //   2967: astore 44
    //   2969: aload 46
    //   2971: iload 12
    //   2973: iconst_0
    //   2974: iload 8
    //   2976: aload 37
    //   2978: invokevirtual 1432	android/media/MediaExtractor:getSampleTime	()J
    //   2981: iconst_0
    //   2982: invokevirtual 1408	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2985: aload 46
    //   2987: astore 40
    //   2989: aload 41
    //   2991: astore 42
    //   2993: aload 38
    //   2995: astore 43
    //   2997: aload 39
    //   2999: astore 44
    //   3001: aload 37
    //   3003: invokevirtual 1435	android/media/MediaExtractor:advance	()Z
    //   3006: pop
    //   3007: iload 7
    //   3009: istore 9
    //   3011: iload_3
    //   3012: istore 8
    //   3014: goto -780 -> 2234
    //   3017: iload 16
    //   3019: bipush -3
    //   3021: if_icmpne +79 -> 3100
    //   3024: aload 46
    //   3026: astore 40
    //   3028: aload 41
    //   3030: astore 42
    //   3032: aload 38
    //   3034: astore 43
    //   3036: aload 39
    //   3038: astore 44
    //   3040: iload 13
    //   3042: istore 14
    //   3044: aload 45
    //   3046: astore 49
    //   3048: iload 17
    //   3050: istore 15
    //   3052: iload 9
    //   3054: istore_3
    //   3055: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   3058: bipush 21
    //   3060: if_icmpge -684 -> 2376
    //   3063: aload 46
    //   3065: astore 40
    //   3067: aload 41
    //   3069: astore 42
    //   3071: aload 38
    //   3073: astore 43
    //   3075: aload 39
    //   3077: astore 44
    //   3079: aload 41
    //   3081: invokevirtual 1392	android/media/MediaCodec:getOutputBuffers	()[Ljava/nio/ByteBuffer;
    //   3084: astore 49
    //   3086: iload 13
    //   3088: istore 14
    //   3090: iload 17
    //   3092: istore 15
    //   3094: iload 9
    //   3096: istore_3
    //   3097: goto -721 -> 2376
    //   3100: iload 16
    //   3102: bipush -2
    //   3104: if_icmpne +88 -> 3192
    //   3107: aload 46
    //   3109: astore 40
    //   3111: aload 41
    //   3113: astore 42
    //   3115: aload 38
    //   3117: astore 43
    //   3119: aload 39
    //   3121: astore 44
    //   3123: aload 41
    //   3125: invokevirtual 1439	android/media/MediaCodec:getOutputFormat	()Landroid/media/MediaFormat;
    //   3128: astore 50
    //   3130: iload 13
    //   3132: istore 14
    //   3134: aload 45
    //   3136: astore 49
    //   3138: iload 17
    //   3140: istore 15
    //   3142: iload 9
    //   3144: istore_3
    //   3145: iload 9
    //   3147: bipush -5
    //   3149: if_icmpne -773 -> 2376
    //   3152: aload 46
    //   3154: astore 40
    //   3156: aload 41
    //   3158: astore 42
    //   3160: aload 38
    //   3162: astore 43
    //   3164: aload 39
    //   3166: astore 44
    //   3168: aload 36
    //   3170: aload 50
    //   3172: iconst_0
    //   3173: invokevirtual 1443	org/telegram/messenger/video/MP4Builder:addTrack	(Landroid/media/MediaFormat;Z)I
    //   3176: istore_3
    //   3177: iload 13
    //   3179: istore 14
    //   3181: aload 45
    //   3183: astore 49
    //   3185: iload 17
    //   3187: istore 15
    //   3189: goto -813 -> 2376
    //   3192: iload 16
    //   3194: ifge +48 -> 3242
    //   3197: aload 46
    //   3199: astore 40
    //   3201: aload 41
    //   3203: astore 42
    //   3205: aload 38
    //   3207: astore 43
    //   3209: aload 39
    //   3211: astore 44
    //   3213: new 1007	java/lang/RuntimeException
    //   3216: dup
    //   3217: new 1278	java/lang/StringBuilder
    //   3220: dup
    //   3221: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   3224: ldc_w 1445
    //   3227: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3230: iload 16
    //   3232: invokevirtual 1321	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3235: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3238: invokespecial 1010	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   3241: athrow
    //   3242: aload 46
    //   3244: astore 40
    //   3246: aload 41
    //   3248: astore 42
    //   3250: aload 38
    //   3252: astore 43
    //   3254: aload 39
    //   3256: astore 44
    //   3258: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   3261: bipush 21
    //   3263: if_icmpge +66 -> 3329
    //   3266: aload 45
    //   3268: iload 16
    //   3270: aaload
    //   3271: astore 49
    //   3273: aload 49
    //   3275: ifnonnull +82 -> 3357
    //   3278: aload 46
    //   3280: astore 40
    //   3282: aload 41
    //   3284: astore 42
    //   3286: aload 38
    //   3288: astore 43
    //   3290: aload 39
    //   3292: astore 44
    //   3294: new 1007	java/lang/RuntimeException
    //   3297: dup
    //   3298: new 1278	java/lang/StringBuilder
    //   3301: dup
    //   3302: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   3305: ldc_w 1447
    //   3308: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3311: iload 16
    //   3313: invokevirtual 1321	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3316: ldc_w 1449
    //   3319: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3322: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3325: invokespecial 1010	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   3328: athrow
    //   3329: aload 46
    //   3331: astore 40
    //   3333: aload 41
    //   3335: astore 42
    //   3337: aload 38
    //   3339: astore 43
    //   3341: aload 39
    //   3343: astore 44
    //   3345: aload 41
    //   3347: iload 16
    //   3349: invokevirtual 1452	android/media/MediaCodec:getOutputBuffer	(I)Ljava/nio/ByteBuffer;
    //   3352: astore 49
    //   3354: goto -81 -> 3273
    //   3357: aload 46
    //   3359: astore 40
    //   3361: aload 41
    //   3363: astore 42
    //   3365: aload 38
    //   3367: astore 43
    //   3369: aload 39
    //   3371: astore 44
    //   3373: iload 9
    //   3375: istore_3
    //   3376: aload 55
    //   3378: getfield 1454	android/media/MediaCodec$BufferInfo:size	I
    //   3381: iconst_1
    //   3382: if_icmple +91 -> 3473
    //   3385: aload 46
    //   3387: astore 40
    //   3389: aload 41
    //   3391: astore 42
    //   3393: aload 38
    //   3395: astore 43
    //   3397: aload 39
    //   3399: astore 44
    //   3401: aload 55
    //   3403: getfield 1457	android/media/MediaCodec$BufferInfo:flags	I
    //   3406: iconst_2
    //   3407: iand
    //   3408: ifne +133 -> 3541
    //   3411: aload 46
    //   3413: astore 40
    //   3415: aload 41
    //   3417: astore 42
    //   3419: aload 38
    //   3421: astore 43
    //   3423: aload 39
    //   3425: astore 44
    //   3427: iload 9
    //   3429: istore_3
    //   3430: aload 36
    //   3432: iload 9
    //   3434: aload 49
    //   3436: aload 55
    //   3438: iconst_0
    //   3439: invokevirtual 1461	org/telegram/messenger/video/MP4Builder:writeSampleData	(ILjava/nio/ByteBuffer;Landroid/media/MediaCodec$BufferInfo;Z)Z
    //   3442: ifeq +31 -> 3473
    //   3445: aload 46
    //   3447: astore 40
    //   3449: aload 41
    //   3451: astore 42
    //   3453: aload 38
    //   3455: astore 43
    //   3457: aload 39
    //   3459: astore 44
    //   3461: aload_0
    //   3462: aload_1
    //   3463: aload 53
    //   3465: iconst_0
    //   3466: iconst_0
    //   3467: invokespecial 1186	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   3470: iload 9
    //   3472: istore_3
    //   3473: aload 46
    //   3475: astore 40
    //   3477: aload 41
    //   3479: astore 42
    //   3481: aload 38
    //   3483: astore 43
    //   3485: aload 39
    //   3487: astore 44
    //   3489: aload 55
    //   3491: getfield 1457	android/media/MediaCodec$BufferInfo:flags	I
    //   3494: iconst_4
    //   3495: iand
    //   3496: ifeq +2073 -> 5569
    //   3499: iconst_1
    //   3500: istore 9
    //   3502: aload 46
    //   3504: astore 40
    //   3506: aload 41
    //   3508: astore 42
    //   3510: aload 38
    //   3512: astore 43
    //   3514: aload 39
    //   3516: astore 44
    //   3518: aload 41
    //   3520: iload 16
    //   3522: iconst_0
    //   3523: invokevirtual 1465	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   3526: iload 13
    //   3528: istore 14
    //   3530: aload 45
    //   3532: astore 49
    //   3534: iload 9
    //   3536: istore 15
    //   3538: goto -1162 -> 2376
    //   3541: iload 9
    //   3543: istore_3
    //   3544: iload 9
    //   3546: bipush -5
    //   3548: if_icmpne -75 -> 3473
    //   3551: aload 46
    //   3553: astore 40
    //   3555: aload 41
    //   3557: astore 42
    //   3559: aload 38
    //   3561: astore 43
    //   3563: aload 39
    //   3565: astore 44
    //   3567: aload 55
    //   3569: getfield 1454	android/media/MediaCodec$BufferInfo:size	I
    //   3572: newarray <illegal type>
    //   3574: astore 56
    //   3576: aload 46
    //   3578: astore 40
    //   3580: aload 41
    //   3582: astore 42
    //   3584: aload 38
    //   3586: astore 43
    //   3588: aload 39
    //   3590: astore 44
    //   3592: aload 49
    //   3594: aload 55
    //   3596: getfield 1468	android/media/MediaCodec$BufferInfo:offset	I
    //   3599: aload 55
    //   3601: getfield 1454	android/media/MediaCodec$BufferInfo:size	I
    //   3604: iadd
    //   3605: invokevirtual 1472	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   3608: pop
    //   3609: aload 46
    //   3611: astore 40
    //   3613: aload 41
    //   3615: astore 42
    //   3617: aload 38
    //   3619: astore 43
    //   3621: aload 39
    //   3623: astore 44
    //   3625: aload 49
    //   3627: aload 55
    //   3629: getfield 1468	android/media/MediaCodec$BufferInfo:offset	I
    //   3632: invokevirtual 1475	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   3635: pop
    //   3636: aload 46
    //   3638: astore 40
    //   3640: aload 41
    //   3642: astore 42
    //   3644: aload 38
    //   3646: astore 43
    //   3648: aload 39
    //   3650: astore 44
    //   3652: aload 49
    //   3654: aload 56
    //   3656: invokevirtual 1478	java/nio/ByteBuffer:get	([B)Ljava/nio/ByteBuffer;
    //   3659: pop
    //   3660: aconst_null
    //   3661: astore 51
    //   3663: aconst_null
    //   3664: astore 52
    //   3666: aload 46
    //   3668: astore 40
    //   3670: aload 41
    //   3672: astore 42
    //   3674: aload 38
    //   3676: astore 43
    //   3678: aload 39
    //   3680: astore 44
    //   3682: aload 55
    //   3684: getfield 1454	android/media/MediaCodec$BufferInfo:size	I
    //   3687: iconst_1
    //   3688: isub
    //   3689: istore_3
    //   3690: aload 52
    //   3692: astore 50
    //   3694: aload 51
    //   3696: astore 49
    //   3698: iload_3
    //   3699: iflt +177 -> 3876
    //   3702: aload 52
    //   3704: astore 50
    //   3706: aload 51
    //   3708: astore 49
    //   3710: iload_3
    //   3711: iconst_3
    //   3712: if_icmple +164 -> 3876
    //   3715: aload 56
    //   3717: iload_3
    //   3718: baload
    //   3719: iconst_1
    //   3720: if_icmpne +1842 -> 5562
    //   3723: aload 56
    //   3725: iload_3
    //   3726: iconst_1
    //   3727: isub
    //   3728: baload
    //   3729: ifne +1833 -> 5562
    //   3732: aload 56
    //   3734: iload_3
    //   3735: iconst_2
    //   3736: isub
    //   3737: baload
    //   3738: ifne +1824 -> 5562
    //   3741: aload 56
    //   3743: iload_3
    //   3744: iconst_3
    //   3745: isub
    //   3746: baload
    //   3747: ifne +1815 -> 5562
    //   3750: aload 46
    //   3752: astore 40
    //   3754: aload 41
    //   3756: astore 42
    //   3758: aload 38
    //   3760: astore 43
    //   3762: aload 39
    //   3764: astore 44
    //   3766: iload_3
    //   3767: iconst_3
    //   3768: isub
    //   3769: invokestatic 1481	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   3772: astore 49
    //   3774: aload 46
    //   3776: astore 40
    //   3778: aload 41
    //   3780: astore 42
    //   3782: aload 38
    //   3784: astore 43
    //   3786: aload 39
    //   3788: astore 44
    //   3790: aload 55
    //   3792: getfield 1454	android/media/MediaCodec$BufferInfo:size	I
    //   3795: iload_3
    //   3796: iconst_3
    //   3797: isub
    //   3798: isub
    //   3799: invokestatic 1481	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   3802: astore 50
    //   3804: aload 46
    //   3806: astore 40
    //   3808: aload 41
    //   3810: astore 42
    //   3812: aload 38
    //   3814: astore 43
    //   3816: aload 39
    //   3818: astore 44
    //   3820: aload 49
    //   3822: aload 56
    //   3824: iconst_0
    //   3825: iload_3
    //   3826: iconst_3
    //   3827: isub
    //   3828: invokevirtual 1485	java/nio/ByteBuffer:put	([BII)Ljava/nio/ByteBuffer;
    //   3831: iconst_0
    //   3832: invokevirtual 1475	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   3835: pop
    //   3836: aload 46
    //   3838: astore 40
    //   3840: aload 41
    //   3842: astore 42
    //   3844: aload 38
    //   3846: astore 43
    //   3848: aload 39
    //   3850: astore 44
    //   3852: aload 50
    //   3854: aload 56
    //   3856: iload_3
    //   3857: iconst_3
    //   3858: isub
    //   3859: aload 55
    //   3861: getfield 1454	android/media/MediaCodec$BufferInfo:size	I
    //   3864: iload_3
    //   3865: iconst_3
    //   3866: isub
    //   3867: isub
    //   3868: invokevirtual 1485	java/nio/ByteBuffer:put	([BII)Ljava/nio/ByteBuffer;
    //   3871: iconst_0
    //   3872: invokevirtual 1475	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   3875: pop
    //   3876: aload 46
    //   3878: astore 40
    //   3880: aload 41
    //   3882: astore 42
    //   3884: aload 38
    //   3886: astore 43
    //   3888: aload 39
    //   3890: astore 44
    //   3892: ldc -101
    //   3894: iload 5
    //   3896: iload 4
    //   3898: invokestatic 1337	android/media/MediaFormat:createVideoFormat	(Ljava/lang/String;II)Landroid/media/MediaFormat;
    //   3901: astore 51
    //   3903: aload 49
    //   3905: ifnull +60 -> 3965
    //   3908: aload 50
    //   3910: ifnull +55 -> 3965
    //   3913: aload 46
    //   3915: astore 40
    //   3917: aload 41
    //   3919: astore 42
    //   3921: aload 38
    //   3923: astore 43
    //   3925: aload 39
    //   3927: astore 44
    //   3929: aload 51
    //   3931: ldc_w 1487
    //   3934: aload 49
    //   3936: invokevirtual 1491	android/media/MediaFormat:setByteBuffer	(Ljava/lang/String;Ljava/nio/ByteBuffer;)V
    //   3939: aload 46
    //   3941: astore 40
    //   3943: aload 41
    //   3945: astore 42
    //   3947: aload 38
    //   3949: astore 43
    //   3951: aload 39
    //   3953: astore 44
    //   3955: aload 51
    //   3957: ldc_w 1493
    //   3960: aload 50
    //   3962: invokevirtual 1491	android/media/MediaFormat:setByteBuffer	(Ljava/lang/String;Ljava/nio/ByteBuffer;)V
    //   3965: aload 46
    //   3967: astore 40
    //   3969: aload 41
    //   3971: astore 42
    //   3973: aload 38
    //   3975: astore 43
    //   3977: aload 39
    //   3979: astore 44
    //   3981: aload 36
    //   3983: aload 51
    //   3985: iconst_0
    //   3986: invokevirtual 1443	org/telegram/messenger/video/MP4Builder:addTrack	(Landroid/media/MediaFormat;Z)I
    //   3989: istore_3
    //   3990: goto -517 -> 3473
    //   3993: iload 14
    //   3995: istore 13
    //   3997: aload 49
    //   3999: astore 45
    //   4001: iload 15
    //   4003: istore 17
    //   4005: iload_3
    //   4006: istore 9
    //   4008: iload 20
    //   4010: bipush -3
    //   4012: if_icmpeq +1476 -> 5488
    //   4015: iload 20
    //   4017: bipush -2
    //   4019: if_icmpne +87 -> 4106
    //   4022: aload 46
    //   4024: astore 40
    //   4026: aload 41
    //   4028: astore 42
    //   4030: aload 38
    //   4032: astore 43
    //   4034: aload 39
    //   4036: astore 44
    //   4038: aload 46
    //   4040: invokevirtual 1439	android/media/MediaCodec:getOutputFormat	()Landroid/media/MediaFormat;
    //   4043: astore 45
    //   4045: aload 46
    //   4047: astore 40
    //   4049: aload 41
    //   4051: astore 42
    //   4053: aload 38
    //   4055: astore 43
    //   4057: aload 39
    //   4059: astore 44
    //   4061: ldc_w 550
    //   4064: new 1278	java/lang/StringBuilder
    //   4067: dup
    //   4068: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   4071: ldc_w 1495
    //   4074: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4077: aload 45
    //   4079: invokevirtual 1498	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4082: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4085: invokestatic 587	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   4088: iload 14
    //   4090: istore 13
    //   4092: aload 49
    //   4094: astore 45
    //   4096: iload 15
    //   4098: istore 17
    //   4100: iload_3
    //   4101: istore 9
    //   4103: goto +1385 -> 5488
    //   4106: iload 20
    //   4108: ifge +48 -> 4156
    //   4111: aload 46
    //   4113: astore 40
    //   4115: aload 41
    //   4117: astore 42
    //   4119: aload 38
    //   4121: astore 43
    //   4123: aload 39
    //   4125: astore 44
    //   4127: new 1007	java/lang/RuntimeException
    //   4130: dup
    //   4131: new 1278	java/lang/StringBuilder
    //   4134: dup
    //   4135: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   4138: ldc_w 1500
    //   4141: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4144: iload 20
    //   4146: invokevirtual 1321	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4149: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4152: invokespecial 1010	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   4155: athrow
    //   4156: aload 46
    //   4158: astore 40
    //   4160: aload 41
    //   4162: astore 42
    //   4164: aload 38
    //   4166: astore 43
    //   4168: aload 39
    //   4170: astore 44
    //   4172: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   4175: bipush 18
    //   4177: if_icmplt +549 -> 4726
    //   4180: aload 46
    //   4182: astore 40
    //   4184: aload 41
    //   4186: astore 42
    //   4188: aload 38
    //   4190: astore 43
    //   4192: aload 39
    //   4194: astore 44
    //   4196: aload 55
    //   4198: getfield 1454	android/media/MediaCodec$BufferInfo:size	I
    //   4201: ifeq +1374 -> 5575
    //   4204: iconst_1
    //   4205: istore 33
    //   4207: iload 12
    //   4209: istore 18
    //   4211: iload 33
    //   4213: istore 34
    //   4215: iload 8
    //   4217: istore 16
    //   4219: lload 29
    //   4221: lconst_0
    //   4222: lcmp
    //   4223: ifle +79 -> 4302
    //   4226: aload 46
    //   4228: astore 40
    //   4230: aload 41
    //   4232: astore 42
    //   4234: aload 38
    //   4236: astore 43
    //   4238: aload 39
    //   4240: astore 44
    //   4242: iload 12
    //   4244: istore 18
    //   4246: iload 33
    //   4248: istore 34
    //   4250: iload 8
    //   4252: istore 16
    //   4254: aload 55
    //   4256: getfield 1503	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4259: lload 29
    //   4261: lcmp
    //   4262: iflt +40 -> 4302
    //   4265: iconst_1
    //   4266: istore 16
    //   4268: iconst_1
    //   4269: istore 18
    //   4271: iconst_0
    //   4272: istore 34
    //   4274: aload 46
    //   4276: astore 40
    //   4278: aload 41
    //   4280: astore 42
    //   4282: aload 38
    //   4284: astore 43
    //   4286: aload 39
    //   4288: astore 44
    //   4290: aload 55
    //   4292: aload 55
    //   4294: getfield 1457	android/media/MediaCodec$BufferInfo:flags	I
    //   4297: iconst_4
    //   4298: ior
    //   4299: putfield 1457	android/media/MediaCodec$BufferInfo:flags	I
    //   4302: iload 34
    //   4304: istore 33
    //   4306: lload 27
    //   4308: lstore 25
    //   4310: lload 23
    //   4312: lconst_0
    //   4313: lcmp
    //   4314: ifle +111 -> 4425
    //   4317: iload 34
    //   4319: istore 33
    //   4321: lload 27
    //   4323: lstore 25
    //   4325: lload 27
    //   4327: ldc2_w 1232
    //   4330: lcmp
    //   4331: ifne +94 -> 4425
    //   4334: aload 46
    //   4336: astore 40
    //   4338: aload 41
    //   4340: astore 42
    //   4342: aload 38
    //   4344: astore 43
    //   4346: aload 39
    //   4348: astore 44
    //   4350: aload 55
    //   4352: getfield 1503	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4355: lload 23
    //   4357: lcmp
    //   4358: ifge +421 -> 4779
    //   4361: iconst_0
    //   4362: istore 33
    //   4364: aload 46
    //   4366: astore 40
    //   4368: aload 41
    //   4370: astore 42
    //   4372: aload 38
    //   4374: astore 43
    //   4376: aload 39
    //   4378: astore 44
    //   4380: ldc_w 550
    //   4383: new 1278	java/lang/StringBuilder
    //   4386: dup
    //   4387: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   4390: ldc_w 1505
    //   4393: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4396: lload 23
    //   4398: invokevirtual 1288	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   4401: ldc_w 1507
    //   4404: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4407: aload 55
    //   4409: getfield 1503	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4412: invokevirtual 1288	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   4415: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4418: invokestatic 587	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   4421: lload 27
    //   4423: lstore 25
    //   4425: aload 46
    //   4427: astore 40
    //   4429: aload 41
    //   4431: astore 42
    //   4433: aload 38
    //   4435: astore 43
    //   4437: aload 39
    //   4439: astore 44
    //   4441: aload 46
    //   4443: iload 20
    //   4445: iload 33
    //   4447: invokevirtual 1465	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   4450: iload 33
    //   4452: ifeq +114 -> 4566
    //   4455: iconst_0
    //   4456: istore 8
    //   4458: aload 39
    //   4460: invokevirtual 1510	org/telegram/messenger/video/OutputSurface:awaitNewImage	()V
    //   4463: iload 8
    //   4465: ifne +101 -> 4566
    //   4468: aload 46
    //   4470: astore 40
    //   4472: aload 41
    //   4474: astore 42
    //   4476: aload 38
    //   4478: astore 43
    //   4480: aload 39
    //   4482: astore 44
    //   4484: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   4487: bipush 18
    //   4489: if_icmplt +352 -> 4841
    //   4492: aload 46
    //   4494: astore 40
    //   4496: aload 41
    //   4498: astore 42
    //   4500: aload 38
    //   4502: astore 43
    //   4504: aload 39
    //   4506: astore 44
    //   4508: aload 39
    //   4510: iconst_0
    //   4511: invokevirtual 1513	org/telegram/messenger/video/OutputSurface:drawImage	(Z)V
    //   4514: aload 46
    //   4516: astore 40
    //   4518: aload 41
    //   4520: astore 42
    //   4522: aload 38
    //   4524: astore 43
    //   4526: aload 39
    //   4528: astore 44
    //   4530: aload 38
    //   4532: aload 55
    //   4534: getfield 1503	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4537: ldc2_w 1514
    //   4540: lmul
    //   4541: invokevirtual 1519	org/telegram/messenger/video/InputSurface:setPresentationTime	(J)V
    //   4544: aload 46
    //   4546: astore 40
    //   4548: aload 41
    //   4550: astore 42
    //   4552: aload 38
    //   4554: astore 43
    //   4556: aload 39
    //   4558: astore 44
    //   4560: aload 38
    //   4562: invokevirtual 1522	org/telegram/messenger/video/InputSurface:swapBuffers	()Z
    //   4565: pop
    //   4566: aload 46
    //   4568: astore 40
    //   4570: aload 41
    //   4572: astore 42
    //   4574: aload 38
    //   4576: astore 43
    //   4578: aload 39
    //   4580: astore 44
    //   4582: iload 18
    //   4584: istore 12
    //   4586: iload 14
    //   4588: istore 13
    //   4590: aload 49
    //   4592: astore 45
    //   4594: iload 16
    //   4596: istore 8
    //   4598: iload 15
    //   4600: istore 17
    //   4602: iload_3
    //   4603: istore 9
    //   4605: lload 25
    //   4607: lstore 27
    //   4609: aload 55
    //   4611: getfield 1457	android/media/MediaCodec$BufferInfo:flags	I
    //   4614: iconst_4
    //   4615: iand
    //   4616: ifeq +872 -> 5488
    //   4619: iconst_0
    //   4620: istore 20
    //   4622: aload 46
    //   4624: astore 40
    //   4626: aload 41
    //   4628: astore 42
    //   4630: aload 38
    //   4632: astore 43
    //   4634: aload 39
    //   4636: astore 44
    //   4638: ldc_w 550
    //   4641: ldc_w 1524
    //   4644: invokestatic 587	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   4647: aload 46
    //   4649: astore 40
    //   4651: aload 41
    //   4653: astore 42
    //   4655: aload 38
    //   4657: astore 43
    //   4659: aload 39
    //   4661: astore 44
    //   4663: getstatic 692	android/os/Build$VERSION:SDK_INT	I
    //   4666: bipush 18
    //   4668: if_icmplt +374 -> 5042
    //   4671: aload 46
    //   4673: astore 40
    //   4675: aload 41
    //   4677: astore 42
    //   4679: aload 38
    //   4681: astore 43
    //   4683: aload 39
    //   4685: astore 44
    //   4687: aload 41
    //   4689: invokevirtual 1527	android/media/MediaCodec:signalEndOfInputStream	()V
    //   4692: iload 18
    //   4694: istore 12
    //   4696: iload 20
    //   4698: istore 7
    //   4700: iload 14
    //   4702: istore 13
    //   4704: aload 49
    //   4706: astore 45
    //   4708: iload 16
    //   4710: istore 8
    //   4712: iload 15
    //   4714: istore 17
    //   4716: iload_3
    //   4717: istore 9
    //   4719: lload 25
    //   4721: lstore 27
    //   4723: goto +765 -> 5488
    //   4726: aload 46
    //   4728: astore 40
    //   4730: aload 41
    //   4732: astore 42
    //   4734: aload 38
    //   4736: astore 43
    //   4738: aload 39
    //   4740: astore 44
    //   4742: aload 55
    //   4744: getfield 1454	android/media/MediaCodec$BufferInfo:size	I
    //   4747: ifne +834 -> 5581
    //   4750: aload 46
    //   4752: astore 40
    //   4754: aload 41
    //   4756: astore 42
    //   4758: aload 38
    //   4760: astore 43
    //   4762: aload 39
    //   4764: astore 44
    //   4766: aload 55
    //   4768: getfield 1503	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4771: lconst_0
    //   4772: lcmp
    //   4773: ifeq +814 -> 5587
    //   4776: goto +805 -> 5581
    //   4779: aload 46
    //   4781: astore 40
    //   4783: aload 41
    //   4785: astore 42
    //   4787: aload 38
    //   4789: astore 43
    //   4791: aload 39
    //   4793: astore 44
    //   4795: aload 55
    //   4797: getfield 1503	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4800: lstore 25
    //   4802: iload 34
    //   4804: istore 33
    //   4806: goto -381 -> 4425
    //   4809: astore 45
    //   4811: iconst_1
    //   4812: istore 8
    //   4814: aload 46
    //   4816: astore 40
    //   4818: aload 41
    //   4820: astore 42
    //   4822: aload 38
    //   4824: astore 43
    //   4826: aload 39
    //   4828: astore 44
    //   4830: ldc_w 550
    //   4833: aload 45
    //   4835: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4838: goto -375 -> 4463
    //   4841: aload 46
    //   4843: astore 40
    //   4845: aload 41
    //   4847: astore 42
    //   4849: aload 38
    //   4851: astore 43
    //   4853: aload 39
    //   4855: astore 44
    //   4857: aload 41
    //   4859: ldc2_w 1396
    //   4862: invokevirtual 1401	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   4865: istore 8
    //   4867: iload 8
    //   4869: iflt +145 -> 5014
    //   4872: aload 46
    //   4874: astore 40
    //   4876: aload 41
    //   4878: astore 42
    //   4880: aload 38
    //   4882: astore 43
    //   4884: aload 39
    //   4886: astore 44
    //   4888: aload 39
    //   4890: iconst_1
    //   4891: invokevirtual 1513	org/telegram/messenger/video/OutputSurface:drawImage	(Z)V
    //   4894: aload 46
    //   4896: astore 40
    //   4898: aload 41
    //   4900: astore 42
    //   4902: aload 38
    //   4904: astore 43
    //   4906: aload 39
    //   4908: astore 44
    //   4910: aload 39
    //   4912: invokevirtual 1531	org/telegram/messenger/video/OutputSurface:getFrame	()Ljava/nio/ByteBuffer;
    //   4915: astore 45
    //   4917: aload 48
    //   4919: iload 8
    //   4921: aaload
    //   4922: astore 50
    //   4924: aload 46
    //   4926: astore 40
    //   4928: aload 41
    //   4930: astore 42
    //   4932: aload 38
    //   4934: astore 43
    //   4936: aload 39
    //   4938: astore 44
    //   4940: aload 50
    //   4942: invokevirtual 1534	java/nio/ByteBuffer:clear	()Ljava/nio/Buffer;
    //   4945: pop
    //   4946: aload 46
    //   4948: astore 40
    //   4950: aload 41
    //   4952: astore 42
    //   4954: aload 38
    //   4956: astore 43
    //   4958: aload 39
    //   4960: astore 44
    //   4962: aload 45
    //   4964: aload 50
    //   4966: iload 10
    //   4968: iload 5
    //   4970: iload 4
    //   4972: iload 6
    //   4974: iload 11
    //   4976: invokestatic 1538	org/telegram/messenger/Utilities:convertVideoFrame	(Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;IIIII)I
    //   4979: pop
    //   4980: aload 46
    //   4982: astore 40
    //   4984: aload 41
    //   4986: astore 42
    //   4988: aload 38
    //   4990: astore 43
    //   4992: aload 39
    //   4994: astore 44
    //   4996: aload 41
    //   4998: iload 8
    //   5000: iconst_0
    //   5001: iload_2
    //   5002: aload 55
    //   5004: getfield 1503	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   5007: iconst_0
    //   5008: invokevirtual 1408	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   5011: goto -445 -> 4566
    //   5014: aload 46
    //   5016: astore 40
    //   5018: aload 41
    //   5020: astore 42
    //   5022: aload 38
    //   5024: astore 43
    //   5026: aload 39
    //   5028: astore 44
    //   5030: ldc_w 550
    //   5033: ldc_w 1540
    //   5036: invokestatic 587	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   5039: goto -473 -> 4566
    //   5042: aload 46
    //   5044: astore 40
    //   5046: aload 41
    //   5048: astore 42
    //   5050: aload 38
    //   5052: astore 43
    //   5054: aload 39
    //   5056: astore 44
    //   5058: aload 41
    //   5060: ldc2_w 1396
    //   5063: invokevirtual 1401	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   5066: istore 22
    //   5068: iload 18
    //   5070: istore 12
    //   5072: iload 20
    //   5074: istore 7
    //   5076: iload 14
    //   5078: istore 13
    //   5080: aload 49
    //   5082: astore 45
    //   5084: iload 16
    //   5086: istore 8
    //   5088: iload 15
    //   5090: istore 17
    //   5092: iload_3
    //   5093: istore 9
    //   5095: lload 25
    //   5097: lstore 27
    //   5099: iload 22
    //   5101: iflt +387 -> 5488
    //   5104: aload 46
    //   5106: astore 40
    //   5108: aload 41
    //   5110: astore 42
    //   5112: aload 38
    //   5114: astore 43
    //   5116: aload 39
    //   5118: astore 44
    //   5120: aload 41
    //   5122: iload 22
    //   5124: iconst_0
    //   5125: iconst_1
    //   5126: aload 55
    //   5128: getfield 1503	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   5131: iconst_4
    //   5132: invokevirtual 1408	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   5135: iload 18
    //   5137: istore 12
    //   5139: iload 20
    //   5141: istore 7
    //   5143: iload 14
    //   5145: istore 13
    //   5147: aload 49
    //   5149: astore 45
    //   5151: iload 16
    //   5153: istore 8
    //   5155: iload 15
    //   5157: istore 17
    //   5159: iload_3
    //   5160: istore 9
    //   5162: lload 25
    //   5164: lstore 27
    //   5166: goto +322 -> 5488
    //   5169: lload 25
    //   5171: ldc2_w 1232
    //   5174: lcmp
    //   5175: ifeq +256 -> 5431
    //   5178: lload 25
    //   5180: lstore 23
    //   5182: aload 46
    //   5184: astore 40
    //   5186: iload 35
    //   5188: istore 33
    //   5190: goto -4427 -> 763
    //   5193: aload_0
    //   5194: aload_1
    //   5195: aload 37
    //   5197: aload 36
    //   5199: aload 55
    //   5201: lload 23
    //   5203: lload 29
    //   5205: aload 53
    //   5207: iconst_0
    //   5208: invokespecial 1272	org/telegram/messenger/MediaController:readAndWriteTrack	(Lorg/telegram/messenger/MessageObject;Landroid/media/MediaExtractor;Lorg/telegram/messenger/video/MP4Builder;Landroid/media/MediaCodec$BufferInfo;JJLjava/io/File;Z)J
    //   5211: lstore 25
    //   5213: lload 25
    //   5215: ldc2_w 1232
    //   5218: lcmp
    //   5219: ifeq +209 -> 5428
    //   5222: lload 25
    //   5224: lstore 23
    //   5226: goto -4402 -> 824
    //   5229: astore 36
    //   5231: ldc_w 550
    //   5234: aload 36
    //   5236: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   5239: goto -4364 -> 875
    //   5242: astore 38
    //   5244: aload 39
    //   5246: astore 36
    //   5248: aload 41
    //   5250: astore 37
    //   5252: iconst_1
    //   5253: istore 33
    //   5255: ldc_w 550
    //   5258: aload 38
    //   5260: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   5263: aload 37
    //   5265: ifnull +8 -> 5273
    //   5268: aload 37
    //   5270: invokevirtual 1273	android/media/MediaExtractor:release	()V
    //   5273: aload 36
    //   5275: ifnull +9 -> 5284
    //   5278: aload 36
    //   5280: iconst_0
    //   5281: invokevirtual 1276	org/telegram/messenger/video/MP4Builder:finishMovie	(Z)V
    //   5284: ldc_w 550
    //   5287: new 1278	java/lang/StringBuilder
    //   5290: dup
    //   5291: invokespecial 1279	java/lang/StringBuilder:<init>	()V
    //   5294: ldc_w 1281
    //   5297: invokevirtual 1285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5300: invokestatic 1191	java/lang/System:currentTimeMillis	()J
    //   5303: lload 31
    //   5305: lsub
    //   5306: invokevirtual 1288	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   5309: invokevirtual 1289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5312: invokestatic 587	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   5315: goto -4409 -> 906
    //   5318: astore 36
    //   5320: ldc_w 550
    //   5323: aload 36
    //   5325: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   5328: goto -44 -> 5284
    //   5331: astore 36
    //   5333: ldc_w 550
    //   5336: aload 36
    //   5338: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   5341: goto -2490 -> 2851
    //   5344: aload 54
    //   5346: invokeinterface 1170 1 0
    //   5351: ldc_w 1166
    //   5354: iconst_1
    //   5355: invokeinterface 1176 3 0
    //   5360: invokeinterface 1179 1 0
    //   5365: pop
    //   5366: aload_0
    //   5367: aload_1
    //   5368: aload 53
    //   5370: iconst_1
    //   5371: iconst_1
    //   5372: invokespecial 1186	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   5375: iconst_0
    //   5376: ireturn
    //   5377: astore_1
    //   5378: aload 40
    //   5380: astore 37
    //   5382: aload 38
    //   5384: astore 36
    //   5386: goto -2556 -> 2830
    //   5389: astore_1
    //   5390: goto -2560 -> 2830
    //   5393: astore_1
    //   5394: goto -2564 -> 2830
    //   5397: astore 38
    //   5399: goto -147 -> 5252
    //   5402: astore 38
    //   5404: goto -152 -> 5252
    //   5407: astore 42
    //   5409: aload 38
    //   5411: astore 43
    //   5413: aload 46
    //   5415: astore 40
    //   5417: aload 42
    //   5419: astore 38
    //   5421: aload 39
    //   5423: astore 44
    //   5425: goto -4681 -> 744
    //   5428: goto -4604 -> 824
    //   5431: aload 46
    //   5433: astore 40
    //   5435: iload 35
    //   5437: istore 33
    //   5439: goto -4676 -> 763
    //   5442: iconst_1
    //   5443: istore 6
    //   5445: iload 9
    //   5447: istore_2
    //   5448: goto -4360 -> 1088
    //   5451: iload 16
    //   5453: ifne +104 -> 5557
    //   5456: iconst_1
    //   5457: istore_3
    //   5458: iconst_1
    //   5459: istore 13
    //   5461: lload 25
    //   5463: lstore 27
    //   5465: iload 14
    //   5467: istore 9
    //   5469: iload 15
    //   5471: istore 17
    //   5473: iload 7
    //   5475: istore 8
    //   5477: aload 49
    //   5479: astore 45
    //   5481: iload_3
    //   5482: istore 7
    //   5484: iload 16
    //   5486: istore 12
    //   5488: iload 7
    //   5490: ifne -3182 -> 2308
    //   5493: iload 12
    //   5495: istore 16
    //   5497: aload 45
    //   5499: astore 49
    //   5501: iload 8
    //   5503: istore_3
    //   5504: iload 17
    //   5506: istore 15
    //   5508: iload 9
    //   5510: istore 14
    //   5512: lload 27
    //   5514: lstore 25
    //   5516: iload 13
    //   5518: ifeq -3483 -> 2035
    //   5521: goto -3213 -> 2308
    //   5524: ldc_w 1541
    //   5527: istore 10
    //   5529: iload_2
    //   5530: istore 7
    //   5532: goto -4368 -> 1164
    //   5535: iload 7
    //   5537: istore 9
    //   5539: iload_3
    //   5540: istore 8
    //   5542: iload 12
    //   5544: iconst_m1
    //   5545: if_icmpne -3311 -> 2234
    //   5548: iconst_1
    //   5549: istore 9
    //   5551: iload_3
    //   5552: istore 8
    //   5554: goto -3320 -> 2234
    //   5557: iconst_0
    //   5558: istore_3
    //   5559: goto -101 -> 5458
    //   5562: iload_3
    //   5563: iconst_1
    //   5564: isub
    //   5565: istore_3
    //   5566: goto -1876 -> 3690
    //   5569: iconst_0
    //   5570: istore 9
    //   5572: goto -2070 -> 3502
    //   5575: iconst_0
    //   5576: istore 33
    //   5578: goto -1371 -> 4207
    //   5581: iconst_1
    //   5582: istore 33
    //   5584: goto -1377 -> 4207
    //   5587: iconst_0
    //   5588: istore 33
    //   5590: goto -6 -> 5584
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	5593	0	this	MediaController
    //   0	5593	1	paramMessageObject	MessageObject
    //   139	5391	2	i	int
    //   143	5423	3	j	int
    //   131	4840	4	k	int
    //   135	4834	5	m	int
    //   43	5401	6	n	int
    //   34	5502	7	i1	int
    //   52	5501	8	i2	int
    //   82	5489	9	i3	int
    //   61	5467	10	i4	int
    //   70	4905	11	i5	int
    //   1208	4338	12	i6	int
    //   2378	3139	13	i7	int
    //   603	4908	14	i8	int
    //   587	4920	15	i9	int
    //   593	4903	16	i10	int
    //   2368	3137	17	i11	int
    //   4209	927	18	i12	int
    //   79	1361	19	i13	int
    //   2443	2697	20	i14	int
    //   553	1545	21	i15	int
    //   5066	57	22	i16	int
    //   16	5209	23	l1	long
    //   584	4931	25	l2	long
    //   4306	116	27	localObject1	Object
    //   4607	906	27	l3	long
    //   25	5179	29	l4	long
    //   371	4933	31	l5	long
    //   167	5422	33	bool1	boolean
    //   4213	590	34	bool2	boolean
    //   366	5070	35	bool3	boolean
    //   7	5191	36	localObject2	Object
    //   5229	6	36	localException1	Exception
    //   5246	33	36	localObject3	Object
    //   5318	6	36	localException2	Exception
    //   5331	6	36	localException3	Exception
    //   5384	1	36	localObject4	Object
    //   384	4997	37	localObject5	Object
    //   401	170	38	localObject6	Object
    //   738	10	38	localException4	Exception
    //   761	4352	38	localObject7	Object
    //   5242	141	38	localException5	Exception
    //   5397	1	38	localException6	Exception
    //   5402	8	38	localException7	Exception
    //   5419	1	38	localException8	Exception
    //   397	5025	39	localObject8	Object
    //   393	5041	40	localObject9	Object
    //   390	4859	41	localObject10	Object
    //   200	4911	42	localObject11	Object
    //   5407	11	42	localException9	Exception
    //   427	4985	43	localObject12	Object
    //   624	4800	44	localObject13	Object
    //   579	4128	45	localObject14	Object
    //   4809	25	45	localException10	Exception
    //   4915	583	45	localObject15	Object
    //   561	4871	46	localMediaCodec	android.media.MediaCodec
    //   564	2240	47	localObject16	Object
    //   573	4345	48	localObject17	Object
    //   632	4868	49	localObject18	Object
    //   679	4286	50	localObject19	Object
    //   961	3023	51	localObject20	Object
    //   3664	39	52	localObject21	Object
    //   98	5271	53	localFile	File
    //   154	5191	54	localSharedPreferences	SharedPreferences
    //   410	4790	55	localBufferInfo	MediaCodec.BufferInfo
    //   3574	281	56	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   626	634	738	java/lang/Exception
    //   650	658	738	java/lang/Exception
    //   674	681	738	java/lang/Exception
    //   697	706	738	java/lang/Exception
    //   727	738	738	java/lang/Exception
    //   956	963	738	java/lang/Exception
    //   979	990	738	java/lang/Exception
    //   1016	1024	738	java/lang/Exception
    //   1040	1051	738	java/lang/Exception
    //   1074	1085	738	java/lang/Exception
    //   1104	1157	738	java/lang/Exception
    //   1180	1207	738	java/lang/Exception
    //   1226	1237	738	java/lang/Exception
    //   1292	1302	738	java/lang/Exception
    //   1318	1325	738	java/lang/Exception
    //   1348	1356	738	java/lang/Exception
    //   1372	1381	738	java/lang/Exception
    //   1397	1408	738	java/lang/Exception
    //   1424	1434	738	java/lang/Exception
    //   1459	1469	738	java/lang/Exception
    //   1485	1495	738	java/lang/Exception
    //   1511	1521	738	java/lang/Exception
    //   1537	1545	738	java/lang/Exception
    //   1561	1574	738	java/lang/Exception
    //   1590	1600	738	java/lang/Exception
    //   1616	1623	738	java/lang/Exception
    //   1639	1649	738	java/lang/Exception
    //   1665	1673	738	java/lang/Exception
    //   1689	1703	738	java/lang/Exception
    //   1724	1729	738	java/lang/Exception
    //   1745	1758	738	java/lang/Exception
    //   1774	1782	738	java/lang/Exception
    //   1798	1807	738	java/lang/Exception
    //   1823	1837	738	java/lang/Exception
    //   1853	1858	738	java/lang/Exception
    //   1887	1895	738	java/lang/Exception
    //   1911	1918	738	java/lang/Exception
    //   1934	1941	738	java/lang/Exception
    //   1969	1977	738	java/lang/Exception
    //   1993	2000	738	java/lang/Exception
    //   2024	2028	738	java/lang/Exception
    //   2056	2060	738	java/lang/Exception
    //   2086	2093	738	java/lang/Exception
    //   2116	2126	738	java/lang/Exception
    //   2154	2162	738	java/lang/Exception
    //   2185	2195	738	java/lang/Exception
    //   2216	2227	738	java/lang/Exception
    //   2259	2268	738	java/lang/Exception
    //   2292	2302	738	java/lang/Exception
    //   2324	2328	738	java/lang/Exception
    //   2344	2356	738	java/lang/Exception
    //   2433	2445	738	java/lang/Exception
    //   2488	2499	738	java/lang/Exception
    //   2524	2535	738	java/lang/Exception
    //   2560	2571	738	java/lang/Exception
    //   2602	2613	738	java/lang/Exception
    //   2651	2665	738	java/lang/Exception
    //   2744	2755	738	java/lang/Exception
    //   2790	2800	738	java/lang/Exception
    //   2819	2826	738	java/lang/Exception
    //   2908	2922	738	java/lang/Exception
    //   2941	2950	738	java/lang/Exception
    //   2969	2985	738	java/lang/Exception
    //   3001	3007	738	java/lang/Exception
    //   3055	3063	738	java/lang/Exception
    //   3079	3086	738	java/lang/Exception
    //   3123	3130	738	java/lang/Exception
    //   3168	3177	738	java/lang/Exception
    //   3213	3242	738	java/lang/Exception
    //   3258	3266	738	java/lang/Exception
    //   3294	3329	738	java/lang/Exception
    //   3345	3354	738	java/lang/Exception
    //   3376	3385	738	java/lang/Exception
    //   3401	3411	738	java/lang/Exception
    //   3430	3445	738	java/lang/Exception
    //   3461	3470	738	java/lang/Exception
    //   3489	3499	738	java/lang/Exception
    //   3518	3526	738	java/lang/Exception
    //   3567	3576	738	java/lang/Exception
    //   3592	3609	738	java/lang/Exception
    //   3625	3636	738	java/lang/Exception
    //   3652	3660	738	java/lang/Exception
    //   3682	3690	738	java/lang/Exception
    //   3766	3774	738	java/lang/Exception
    //   3790	3804	738	java/lang/Exception
    //   3820	3836	738	java/lang/Exception
    //   3852	3876	738	java/lang/Exception
    //   3892	3903	738	java/lang/Exception
    //   3929	3939	738	java/lang/Exception
    //   3955	3965	738	java/lang/Exception
    //   3981	3990	738	java/lang/Exception
    //   4038	4045	738	java/lang/Exception
    //   4061	4088	738	java/lang/Exception
    //   4127	4156	738	java/lang/Exception
    //   4172	4180	738	java/lang/Exception
    //   4196	4204	738	java/lang/Exception
    //   4254	4265	738	java/lang/Exception
    //   4290	4302	738	java/lang/Exception
    //   4350	4361	738	java/lang/Exception
    //   4380	4421	738	java/lang/Exception
    //   4441	4450	738	java/lang/Exception
    //   4484	4492	738	java/lang/Exception
    //   4508	4514	738	java/lang/Exception
    //   4530	4544	738	java/lang/Exception
    //   4560	4566	738	java/lang/Exception
    //   4609	4619	738	java/lang/Exception
    //   4638	4647	738	java/lang/Exception
    //   4663	4671	738	java/lang/Exception
    //   4687	4692	738	java/lang/Exception
    //   4742	4750	738	java/lang/Exception
    //   4766	4776	738	java/lang/Exception
    //   4795	4802	738	java/lang/Exception
    //   4830	4838	738	java/lang/Exception
    //   4857	4867	738	java/lang/Exception
    //   4888	4894	738	java/lang/Exception
    //   4910	4917	738	java/lang/Exception
    //   4940	4946	738	java/lang/Exception
    //   4962	4980	738	java/lang/Exception
    //   4996	5011	738	java/lang/Exception
    //   5030	5039	738	java/lang/Exception
    //   5058	5068	738	java/lang/Exception
    //   5120	5135	738	java/lang/Exception
    //   514	528	2829	finally
    //   546	555	2829	finally
    //   626	634	2829	finally
    //   650	658	2829	finally
    //   674	681	2829	finally
    //   697	706	2829	finally
    //   727	738	2829	finally
    //   744	752	2829	finally
    //   956	963	2829	finally
    //   979	990	2829	finally
    //   1016	1024	2829	finally
    //   1040	1051	2829	finally
    //   1074	1085	2829	finally
    //   1104	1157	2829	finally
    //   1180	1207	2829	finally
    //   1226	1237	2829	finally
    //   1292	1302	2829	finally
    //   1318	1325	2829	finally
    //   1348	1356	2829	finally
    //   1372	1381	2829	finally
    //   1397	1408	2829	finally
    //   1424	1434	2829	finally
    //   1459	1469	2829	finally
    //   1485	1495	2829	finally
    //   1511	1521	2829	finally
    //   1537	1545	2829	finally
    //   1561	1574	2829	finally
    //   1590	1600	2829	finally
    //   1616	1623	2829	finally
    //   1639	1649	2829	finally
    //   1665	1673	2829	finally
    //   1689	1703	2829	finally
    //   1703	1708	2829	finally
    //   1724	1729	2829	finally
    //   1745	1758	2829	finally
    //   1774	1782	2829	finally
    //   1798	1807	2829	finally
    //   1823	1837	2829	finally
    //   1853	1858	2829	finally
    //   1887	1895	2829	finally
    //   1911	1918	2829	finally
    //   1934	1941	2829	finally
    //   1969	1977	2829	finally
    //   1993	2000	2829	finally
    //   2024	2028	2829	finally
    //   2056	2060	2829	finally
    //   2086	2093	2829	finally
    //   2116	2126	2829	finally
    //   2154	2162	2829	finally
    //   2185	2195	2829	finally
    //   2216	2227	2829	finally
    //   2259	2268	2829	finally
    //   2292	2302	2829	finally
    //   2324	2328	2829	finally
    //   2344	2356	2829	finally
    //   2433	2445	2829	finally
    //   2488	2499	2829	finally
    //   2524	2535	2829	finally
    //   2560	2571	2829	finally
    //   2602	2613	2829	finally
    //   2651	2665	2829	finally
    //   2744	2755	2829	finally
    //   2790	2800	2829	finally
    //   2819	2826	2829	finally
    //   2908	2922	2829	finally
    //   2941	2950	2829	finally
    //   2969	2985	2829	finally
    //   3001	3007	2829	finally
    //   3055	3063	2829	finally
    //   3079	3086	2829	finally
    //   3123	3130	2829	finally
    //   3168	3177	2829	finally
    //   3213	3242	2829	finally
    //   3258	3266	2829	finally
    //   3294	3329	2829	finally
    //   3345	3354	2829	finally
    //   3376	3385	2829	finally
    //   3401	3411	2829	finally
    //   3430	3445	2829	finally
    //   3461	3470	2829	finally
    //   3489	3499	2829	finally
    //   3518	3526	2829	finally
    //   3567	3576	2829	finally
    //   3592	3609	2829	finally
    //   3625	3636	2829	finally
    //   3652	3660	2829	finally
    //   3682	3690	2829	finally
    //   3766	3774	2829	finally
    //   3790	3804	2829	finally
    //   3820	3836	2829	finally
    //   3852	3876	2829	finally
    //   3892	3903	2829	finally
    //   3929	3939	2829	finally
    //   3955	3965	2829	finally
    //   3981	3990	2829	finally
    //   4038	4045	2829	finally
    //   4061	4088	2829	finally
    //   4127	4156	2829	finally
    //   4172	4180	2829	finally
    //   4196	4204	2829	finally
    //   4254	4265	2829	finally
    //   4290	4302	2829	finally
    //   4350	4361	2829	finally
    //   4380	4421	2829	finally
    //   4441	4450	2829	finally
    //   4458	4463	2829	finally
    //   4484	4492	2829	finally
    //   4508	4514	2829	finally
    //   4530	4544	2829	finally
    //   4560	4566	2829	finally
    //   4609	4619	2829	finally
    //   4638	4647	2829	finally
    //   4663	4671	2829	finally
    //   4687	4692	2829	finally
    //   4742	4750	2829	finally
    //   4766	4776	2829	finally
    //   4795	4802	2829	finally
    //   4830	4838	2829	finally
    //   4857	4867	2829	finally
    //   4888	4894	2829	finally
    //   4910	4917	2829	finally
    //   4940	4946	2829	finally
    //   4962	4980	2829	finally
    //   4996	5011	2829	finally
    //   5030	5039	2829	finally
    //   5058	5068	2829	finally
    //   5120	5135	2829	finally
    //   5193	5213	2829	finally
    //   4458	4463	4809	java/lang/Exception
    //   869	875	5229	java/lang/Exception
    //   403	412	5242	java/lang/Exception
    //   420	429	5242	java/lang/Exception
    //   437	444	5242	java/lang/Exception
    //   452	458	5242	java/lang/Exception
    //   466	475	5242	java/lang/Exception
    //   483	497	5242	java/lang/Exception
    //   505	514	5242	java/lang/Exception
    //   5278	5284	5318	java/lang/Exception
    //   2845	2851	5331	java/lang/Exception
    //   403	412	5377	finally
    //   420	429	5377	finally
    //   437	444	5377	finally
    //   452	458	5377	finally
    //   466	475	5377	finally
    //   483	497	5377	finally
    //   505	514	5377	finally
    //   763	770	5389	finally
    //   775	780	5389	finally
    //   785	790	5389	finally
    //   795	805	5389	finally
    //   810	820	5389	finally
    //   820	824	5389	finally
    //   835	854	5389	finally
    //   5255	5263	5393	finally
    //   514	528	5397	java/lang/Exception
    //   546	555	5397	java/lang/Exception
    //   744	752	5397	java/lang/Exception
    //   5193	5213	5397	java/lang/Exception
    //   763	770	5402	java/lang/Exception
    //   775	780	5402	java/lang/Exception
    //   785	790	5402	java/lang/Exception
    //   795	805	5402	java/lang/Exception
    //   810	820	5402	java/lang/Exception
    //   820	824	5402	java/lang/Exception
    //   835	854	5402	java/lang/Exception
    //   1703	1708	5407	java/lang/Exception
  }
  
  /* Error */
  public static String copyFileToCache(Uri paramUri, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 10
    //   3: aconst_null
    //   4: astore 9
    //   6: aconst_null
    //   7: astore 8
    //   9: aconst_null
    //   10: astore 7
    //   12: aload 9
    //   14: astore_3
    //   15: aload 8
    //   17: astore 4
    //   19: aload 10
    //   21: astore 5
    //   23: aload_0
    //   24: invokestatic 1548	org/telegram/messenger/MediaController:getFileName	(Landroid/net/Uri;)Ljava/lang/String;
    //   27: astore 11
    //   29: aload 11
    //   31: astore 6
    //   33: aload 11
    //   35: ifnonnull +89 -> 124
    //   38: aload 9
    //   40: astore_3
    //   41: aload 8
    //   43: astore 4
    //   45: aload 10
    //   47: astore 5
    //   49: getstatic 1551	org/telegram/messenger/UserConfig:lastLocalId	I
    //   52: istore_2
    //   53: aload 9
    //   55: astore_3
    //   56: aload 8
    //   58: astore 4
    //   60: aload 10
    //   62: astore 5
    //   64: getstatic 1551	org/telegram/messenger/UserConfig:lastLocalId	I
    //   67: iconst_1
    //   68: isub
    //   69: putstatic 1551	org/telegram/messenger/UserConfig:lastLocalId	I
    //   72: aload 9
    //   74: astore_3
    //   75: aload 8
    //   77: astore 4
    //   79: aload 10
    //   81: astore 5
    //   83: iconst_0
    //   84: invokestatic 1554	org/telegram/messenger/UserConfig:saveConfig	(Z)V
    //   87: aload 9
    //   89: astore_3
    //   90: aload 8
    //   92: astore 4
    //   94: aload 10
    //   96: astore 5
    //   98: getstatic 1560	java/util/Locale:US	Ljava/util/Locale;
    //   101: ldc_w 1562
    //   104: iconst_2
    //   105: anewarray 4	java/lang/Object
    //   108: dup
    //   109: iconst_0
    //   110: iload_2
    //   111: invokestatic 1568	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   114: aastore
    //   115: dup
    //   116: iconst_1
    //   117: aload_1
    //   118: aastore
    //   119: invokestatic 1572	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   122: astore 6
    //   124: aload 9
    //   126: astore_3
    //   127: aload 8
    //   129: astore 4
    //   131: aload 10
    //   133: astore 5
    //   135: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   138: invokevirtual 704	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   141: aload_0
    //   142: invokevirtual 1576	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   145: astore_0
    //   146: aload_0
    //   147: astore_3
    //   148: aload 8
    //   150: astore 4
    //   152: aload_0
    //   153: astore 5
    //   155: new 1062	java/io/File
    //   158: dup
    //   159: invokestatic 1074	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   162: iconst_4
    //   163: invokevirtual 1580	org/telegram/messenger/FileLoader:getDirectory	(I)Ljava/io/File;
    //   166: aload 6
    //   168: invokespecial 1583	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   171: astore 6
    //   173: aload_0
    //   174: astore_3
    //   175: aload 8
    //   177: astore 4
    //   179: aload_0
    //   180: astore 5
    //   182: new 1585	java/io/FileOutputStream
    //   185: dup
    //   186: aload 6
    //   188: invokespecial 1587	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   191: astore_1
    //   192: sipush 20480
    //   195: newarray <illegal type>
    //   197: astore_3
    //   198: aload_0
    //   199: aload_3
    //   200: invokevirtual 1593	java/io/InputStream:read	([B)I
    //   203: istore_2
    //   204: iload_2
    //   205: iconst_m1
    //   206: if_icmpeq +46 -> 252
    //   209: aload_1
    //   210: aload_3
    //   211: iconst_0
    //   212: iload_2
    //   213: invokevirtual 1597	java/io/FileOutputStream:write	([BII)V
    //   216: goto -18 -> 198
    //   219: astore 6
    //   221: aload_0
    //   222: astore_3
    //   223: aload_1
    //   224: astore 4
    //   226: ldc_w 550
    //   229: aload 6
    //   231: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   234: aload_0
    //   235: ifnull +7 -> 242
    //   238: aload_0
    //   239: invokevirtual 1600	java/io/InputStream:close	()V
    //   242: aload_1
    //   243: ifnull +7 -> 250
    //   246: aload_1
    //   247: invokevirtual 1601	java/io/FileOutputStream:close	()V
    //   250: aconst_null
    //   251: areturn
    //   252: aload 6
    //   254: invokevirtual 1604	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   257: astore_3
    //   258: aload_0
    //   259: ifnull +7 -> 266
    //   262: aload_0
    //   263: invokevirtual 1600	java/io/InputStream:close	()V
    //   266: aload_1
    //   267: ifnull +7 -> 274
    //   270: aload_1
    //   271: invokevirtual 1601	java/io/FileOutputStream:close	()V
    //   274: aload_3
    //   275: areturn
    //   276: astore_0
    //   277: ldc_w 550
    //   280: aload_0
    //   281: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   284: goto -18 -> 266
    //   287: astore_0
    //   288: ldc_w 550
    //   291: aload_0
    //   292: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   295: goto -21 -> 274
    //   298: astore_0
    //   299: ldc_w 550
    //   302: aload_0
    //   303: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   306: goto -64 -> 242
    //   309: astore_0
    //   310: ldc_w 550
    //   313: aload_0
    //   314: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   317: goto -67 -> 250
    //   320: astore_0
    //   321: aload_3
    //   322: ifnull +7 -> 329
    //   325: aload_3
    //   326: invokevirtual 1600	java/io/InputStream:close	()V
    //   329: aload 4
    //   331: ifnull +8 -> 339
    //   334: aload 4
    //   336: invokevirtual 1601	java/io/FileOutputStream:close	()V
    //   339: aload_0
    //   340: athrow
    //   341: astore_1
    //   342: ldc_w 550
    //   345: aload_1
    //   346: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   349: goto -20 -> 329
    //   352: astore_1
    //   353: ldc_w 550
    //   356: aload_1
    //   357: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   360: goto -21 -> 339
    //   363: astore 5
    //   365: aload_0
    //   366: astore_3
    //   367: aload_1
    //   368: astore 4
    //   370: aload 5
    //   372: astore_0
    //   373: goto -52 -> 321
    //   376: astore 6
    //   378: aload 5
    //   380: astore_0
    //   381: aload 7
    //   383: astore_1
    //   384: goto -163 -> 221
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	387	0	paramUri	Uri
    //   0	387	1	paramString	String
    //   52	161	2	i	int
    //   14	353	3	localObject1	Object
    //   17	352	4	localObject2	Object
    //   21	160	5	localObject3	Object
    //   363	16	5	localObject4	Object
    //   31	156	6	localObject5	Object
    //   219	34	6	localException1	Exception
    //   376	1	6	localException2	Exception
    //   10	372	7	localObject6	Object
    //   7	169	8	localObject7	Object
    //   4	121	9	localObject8	Object
    //   1	131	10	localObject9	Object
    //   27	7	11	str	String
    // Exception table:
    //   from	to	target	type
    //   192	198	219	java/lang/Exception
    //   198	204	219	java/lang/Exception
    //   209	216	219	java/lang/Exception
    //   252	258	219	java/lang/Exception
    //   262	266	276	java/lang/Exception
    //   270	274	287	java/lang/Exception
    //   238	242	298	java/lang/Exception
    //   246	250	309	java/lang/Exception
    //   23	29	320	finally
    //   49	53	320	finally
    //   64	72	320	finally
    //   83	87	320	finally
    //   98	124	320	finally
    //   135	146	320	finally
    //   155	173	320	finally
    //   182	192	320	finally
    //   226	234	320	finally
    //   325	329	341	java/lang/Exception
    //   334	339	352	java/lang/Exception
    //   192	198	363	finally
    //   198	204	363	finally
    //   209	216	363	finally
    //   252	258	363	finally
    //   23	29	376	java/lang/Exception
    //   49	53	376	java/lang/Exception
    //   64	72	376	java/lang/Exception
    //   83	87	376	java/lang/Exception
    //   98	124	376	java/lang/Exception
    //   135	146	376	java/lang/Exception
    //   155	173	376	java/lang/Exception
    //   182	192	376	java/lang/Exception
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
        if (paramBoolean2) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingFailed, new Object[] { paramMessageObject, paramFile.toString() });
        }
        for (;;)
        {
          if ((paramBoolean2) || (paramBoolean1)) {}
          synchronized (MediaController.this.videoConvertSync)
          {
            MediaController.access$6302(MediaController.this, false);
            MediaController.this.videoConvertQueue.remove(paramMessageObject);
            MediaController.this.startVideoConvertFromQueue();
            return;
            if (bool) {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingStarted, new Object[] { paramMessageObject, paramFile.toString() });
            }
            ??? = NotificationCenter.getInstance();
            int i = NotificationCenter.FileNewChunkAvailable;
            MessageObject localMessageObject = paramMessageObject;
            String str = paramFile.toString();
            if (paramBoolean1)
            {
              l = paramFile.length();
              ((NotificationCenter)???).postNotificationName(i, new Object[] { localMessageObject, str, Long.valueOf(l) });
              continue;
            }
            long l = 0L;
          }
        }
      }
    });
  }
  
  private int getCurrentDownloadMask()
  {
    if (ConnectionsManager.isConnectedToWiFi()) {
      return this.wifiDownloadMask;
    }
    if (ConnectionsManager.isRoaming()) {
      return this.roamingDownloadMask;
    }
    return this.mobileDataDownloadMask;
  }
  
  public static String getFileName(Uri paramUri)
  {
    localObject3 = null;
    String str = null;
    localObject2 = localObject3;
    if (paramUri.getScheme().equals("content"))
    {
      localObject2 = null;
      localObject1 = null;
    }
    try
    {
      Cursor localCursor = ApplicationLoader.applicationContext.getContentResolver().query(paramUri, new String[] { "_display_name" }, null, null, null);
      localObject1 = localCursor;
      localObject2 = localCursor;
      if (localCursor.moveToFirst())
      {
        localObject1 = localCursor;
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
        localObject2 = localObject1;
        FileLog.e("tmessages", localException);
        localObject2 = localObject3;
        if (localObject1 != null)
        {
          ((Cursor)localObject1).close();
          localObject2 = localObject3;
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
    localObject1 = localObject2;
    if (localObject2 == null)
    {
      paramUri = paramUri.getPath();
      i = paramUri.lastIndexOf('/');
      localObject1 = paramUri;
      if (i != -1) {
        localObject1 = paramUri.substring(i + 1);
      }
    }
    return (String)localObject1;
  }
  
  public static MediaController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          MediaController localMediaController2 = Instance;
          localObject1 = localMediaController2;
          if (localMediaController2 == null) {
            localObject1 = new MediaController();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (MediaController)localObject1;
          return (MediaController)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localMediaController1;
  }
  
  private native long getTotalPcmDuration();
  
  public static boolean isGif(Uri paramUri)
  {
    boolean bool1 = false;
    Object localObject1 = null;
    Uri localUri = null;
    do
    {
      try
      {
        paramUri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(paramUri);
        localUri = paramUri;
        localObject1 = paramUri;
        Object localObject2 = new byte[3];
        localUri = paramUri;
        localObject1 = paramUri;
        if (paramUri.read((byte[])localObject2, 0, 3) != 3) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        localObject2 = new String((byte[])localObject2);
        if (localObject2 == null) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        bool2 = ((String)localObject2).equalsIgnoreCase("gif");
        if (!bool2) {
          continue;
        }
        bool2 = true;
        bool1 = bool2;
      }
      catch (Exception paramUri)
      {
        do
        {
          boolean bool2;
          localObject1 = localUri;
          FileLog.e("tmessages", paramUri);
        } while (localUri == null);
        try
        {
          localUri.close();
          return false;
        }
        catch (Exception paramUri)
        {
          FileLog.e("tmessages", paramUri);
          return false;
        }
      }
      finally
      {
        if (localObject1 == null) {
          break label172;
        }
      }
      try
      {
        paramUri.close();
        bool1 = bool2;
        return bool1;
      }
      catch (Exception paramUri)
      {
        FileLog.e("tmessages", paramUri);
        return true;
      }
    } while (paramUri == null);
    try
    {
      paramUri.close();
      return false;
    }
    catch (Exception paramUri)
    {
      FileLog.e("tmessages", paramUri);
      return false;
    }
    try
    {
      ((InputStream)localObject1).close();
      label172:
      throw paramUri;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  private boolean isNearToSensor(float paramFloat)
  {
    return (paramFloat < 5.0F) && (paramFloat != this.proximitySensor.getMaximumRange());
  }
  
  private native int isOpusFile(String paramString);
  
  private static boolean isRecognizedFormat(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public static boolean isWebp(Uri paramUri)
  {
    boolean bool1 = false;
    Object localObject1 = null;
    Uri localUri = null;
    do
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
        localObject2 = new String((byte[])localObject2);
        if (localObject2 == null) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        localObject2 = ((String)localObject2).toLowerCase();
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
        bool2 = true;
        bool1 = bool2;
      }
      catch (Exception paramUri)
      {
        do
        {
          boolean bool2;
          localObject1 = localUri;
          FileLog.e("tmessages", paramUri);
        } while (localUri == null);
        try
        {
          localUri.close();
          return false;
        }
        catch (Exception paramUri)
        {
          FileLog.e("tmessages", paramUri);
          return false;
        }
      }
      finally
      {
        if (localObject1 == null) {
          break label203;
        }
      }
      try
      {
        paramUri.close();
        bool1 = bool2;
        return bool1;
      }
      catch (Exception paramUri)
      {
        FileLog.e("tmessages", paramUri);
        return true;
      }
    } while (paramUri == null);
    try
    {
      paramUri.close();
      return false;
    }
    catch (Exception paramUri)
    {
      FileLog.e("tmessages", paramUri);
      return false;
    }
    try
    {
      ((InputStream)localObject1).close();
      label203:
      throw paramUri;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
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
        //   0: new 27	java/util/ArrayList
        //   3: dup
        //   4: invokespecial 28	java/util/ArrayList:<init>	()V
        //   7: astore 31
        //   9: new 27	java/util/ArrayList
        //   12: dup
        //   13: invokespecial 28	java/util/ArrayList:<init>	()V
        //   16: astore 32
        //   18: new 30	java/util/HashMap
        //   21: dup
        //   22: invokespecial 31	java/util/HashMap:<init>	()V
        //   25: astore 33
        //   27: aconst_null
        //   28: astore 26
        //   30: aconst_null
        //   31: astore 25
        //   33: new 33	java/lang/StringBuilder
        //   36: dup
        //   37: invokespecial 34	java/lang/StringBuilder:<init>	()V
        //   40: getstatic 40	android/os/Environment:DIRECTORY_DCIM	Ljava/lang/String;
        //   43: invokestatic 44	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
        //   46: invokevirtual 50	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   49: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   52: ldc 56
        //   54: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   57: ldc 58
        //   59: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   62: invokevirtual 61	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   65: astore 34
        //   67: aconst_null
        //   68: astore 27
        //   70: aconst_null
        //   71: astore 28
        //   73: aconst_null
        //   74: astore 20
        //   76: aconst_null
        //   77: astore 22
        //   79: aconst_null
        //   80: astore 23
        //   82: aconst_null
        //   83: astore 24
        //   85: aconst_null
        //   86: astore 21
        //   88: aconst_null
        //   89: astore 29
        //   91: aconst_null
        //   92: astore 30
        //   94: aconst_null
        //   95: astore 12
        //   97: aload 25
        //   99: astore 15
        //   101: aload 27
        //   103: astore 16
        //   105: aload 12
        //   107: astore 18
        //   109: aload 30
        //   111: astore 14
        //   113: getstatic 66	android/os/Build$VERSION:SDK_INT	I
        //   116: bipush 23
        //   118: if_icmplt +78 -> 196
        //   121: aload 25
        //   123: astore 15
        //   125: aload 27
        //   127: astore 16
        //   129: aload 12
        //   131: astore 18
        //   133: aload 26
        //   135: astore 19
        //   137: aload 28
        //   139: astore 13
        //   141: aload 29
        //   143: astore 17
        //   145: aload 30
        //   147: astore 14
        //   149: getstatic 66	android/os/Build$VERSION:SDK_INT	I
        //   152: bipush 23
        //   154: if_icmplt +1587 -> 1741
        //   157: aload 25
        //   159: astore 15
        //   161: aload 27
        //   163: astore 16
        //   165: aload 12
        //   167: astore 18
        //   169: aload 26
        //   171: astore 19
        //   173: aload 28
        //   175: astore 13
        //   177: aload 29
        //   179: astore 17
        //   181: aload 30
        //   183: astore 14
        //   185: getstatic 72	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   188: ldc 74
        //   190: invokevirtual 80	android/content/Context:checkSelfPermission	(Ljava/lang/String;)I
        //   193: ifne +1548 -> 1741
        //   196: aload 25
        //   198: astore 15
        //   200: aload 27
        //   202: astore 16
        //   204: aload 12
        //   206: astore 18
        //   208: aload 30
        //   210: astore 14
        //   212: getstatic 72	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   215: invokevirtual 84	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
        //   218: getstatic 90	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
        //   221: invokestatic 94	org/telegram/messenger/MediaController:access$5800	()[Ljava/lang/String;
        //   224: aconst_null
        //   225: aconst_null
        //   226: ldc 96
        //   228: invokestatic 100	android/provider/MediaStore$Images$Media:query	(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   231: astore 12
        //   233: aload 26
        //   235: astore 19
        //   237: aload 28
        //   239: astore 13
        //   241: aload 12
        //   243: astore 17
        //   245: aload 12
        //   247: ifnull +1494 -> 1741
        //   250: aload 25
        //   252: astore 15
        //   254: aload 27
        //   256: astore 16
        //   258: aload 12
        //   260: astore 18
        //   262: aload 12
        //   264: astore 14
        //   266: aload 12
        //   268: ldc 102
        //   270: invokeinterface 107 2 0
        //   275: istore_1
        //   276: aload 25
        //   278: astore 15
        //   280: aload 27
        //   282: astore 16
        //   284: aload 12
        //   286: astore 18
        //   288: aload 12
        //   290: astore 14
        //   292: aload 12
        //   294: ldc 109
        //   296: invokeinterface 107 2 0
        //   301: istore_2
        //   302: aload 25
        //   304: astore 15
        //   306: aload 27
        //   308: astore 16
        //   310: aload 12
        //   312: astore 18
        //   314: aload 12
        //   316: astore 14
        //   318: aload 12
        //   320: ldc 111
        //   322: invokeinterface 107 2 0
        //   327: istore_3
        //   328: aload 25
        //   330: astore 15
        //   332: aload 27
        //   334: astore 16
        //   336: aload 12
        //   338: astore 18
        //   340: aload 12
        //   342: astore 14
        //   344: aload 12
        //   346: ldc 113
        //   348: invokeinterface 107 2 0
        //   353: istore 4
        //   355: aload 25
        //   357: astore 15
        //   359: aload 27
        //   361: astore 16
        //   363: aload 12
        //   365: astore 18
        //   367: aload 12
        //   369: astore 14
        //   371: aload 12
        //   373: ldc 115
        //   375: invokeinterface 107 2 0
        //   380: istore 5
        //   382: aload 25
        //   384: astore 15
        //   386: aload 27
        //   388: astore 16
        //   390: aload 12
        //   392: astore 18
        //   394: aload 12
        //   396: astore 14
        //   398: aload 12
        //   400: ldc 117
        //   402: invokeinterface 107 2 0
        //   407: istore 6
        //   409: aconst_null
        //   410: astore 14
        //   412: aload 20
        //   414: astore 13
        //   416: aload 12
        //   418: invokeinterface 121 1 0
        //   423: ifeq +1310 -> 1733
        //   426: aload 12
        //   428: iload_1
        //   429: invokeinterface 125 2 0
        //   434: istore 7
        //   436: aload 12
        //   438: iload_2
        //   439: invokeinterface 125 2 0
        //   444: istore 8
        //   446: aload 12
        //   448: iload_3
        //   449: invokeinterface 129 2 0
        //   454: astore 28
        //   456: aload 12
        //   458: iload 4
        //   460: invokeinterface 129 2 0
        //   465: astore 27
        //   467: aload 12
        //   469: iload 5
        //   471: invokeinterface 133 2 0
        //   476: lstore 10
        //   478: aload 12
        //   480: iload 6
        //   482: invokeinterface 125 2 0
        //   487: istore 9
        //   489: aload 27
        //   491: ifnull -75 -> 416
        //   494: aload 27
        //   496: invokevirtual 139	java/lang/String:length	()I
        //   499: ifeq -83 -> 416
        //   502: new 141	org/telegram/messenger/MediaController$PhotoEntry
        //   505: dup
        //   506: iload 8
        //   508: iload 7
        //   510: lload 10
        //   512: aload 27
        //   514: iload 9
        //   516: iconst_0
        //   517: invokespecial 144	org/telegram/messenger/MediaController$PhotoEntry:<init>	(IIJLjava/lang/String;IZ)V
        //   520: astore 26
        //   522: aload 14
        //   524: ifnonnull +1560 -> 2084
        //   527: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   530: dup
        //   531: iconst_0
        //   532: ldc -108
        //   534: ldc -107
        //   536: invokestatic 154	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   539: aload 26
        //   541: iconst_0
        //   542: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;Z)V
        //   545: astore 17
        //   547: aload 17
        //   549: astore 15
        //   551: aload 13
        //   553: astore 16
        //   555: aload 12
        //   557: astore 18
        //   559: aload 12
        //   561: astore 14
        //   563: aload 31
        //   565: iconst_0
        //   566: aload 17
        //   568: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   571: aload 17
        //   573: ifnull +26 -> 599
        //   576: aload 17
        //   578: astore 15
        //   580: aload 13
        //   582: astore 16
        //   584: aload 12
        //   586: astore 18
        //   588: aload 12
        //   590: astore 14
        //   592: aload 17
        //   594: aload 26
        //   596: invokevirtual 165	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   599: aload 17
        //   601: astore 15
        //   603: aload 13
        //   605: astore 16
        //   607: aload 12
        //   609: astore 18
        //   611: aload 12
        //   613: astore 14
        //   615: aload 33
        //   617: iload 8
        //   619: invokestatic 171	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   622: invokevirtual 175	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   625: checkcast 146	org/telegram/messenger/MediaController$AlbumEntry
        //   628: astore 25
        //   630: aload 25
        //   632: astore 20
        //   634: aload 13
        //   636: astore 19
        //   638: aload 25
        //   640: ifnonnull +152 -> 792
        //   643: aload 17
        //   645: astore 15
        //   647: aload 13
        //   649: astore 16
        //   651: aload 12
        //   653: astore 18
        //   655: aload 12
        //   657: astore 14
        //   659: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   662: dup
        //   663: iload 8
        //   665: aload 28
        //   667: aload 26
        //   669: iconst_0
        //   670: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;Z)V
        //   673: astore 20
        //   675: aload 17
        //   677: astore 15
        //   679: aload 13
        //   681: astore 16
        //   683: aload 12
        //   685: astore 18
        //   687: aload 12
        //   689: astore 14
        //   691: aload 33
        //   693: iload 8
        //   695: invokestatic 171	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   698: aload 20
        //   700: invokevirtual 179	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   703: pop
        //   704: aload 13
        //   706: ifnonnull +120 -> 826
        //   709: aload 34
        //   711: ifnull +115 -> 826
        //   714: aload 27
        //   716: ifnull +110 -> 826
        //   719: aload 17
        //   721: astore 15
        //   723: aload 13
        //   725: astore 16
        //   727: aload 12
        //   729: astore 18
        //   731: aload 12
        //   733: astore 14
        //   735: aload 27
        //   737: aload 34
        //   739: invokevirtual 183	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   742: ifeq +84 -> 826
        //   745: aload 17
        //   747: astore 15
        //   749: aload 13
        //   751: astore 16
        //   753: aload 12
        //   755: astore 18
        //   757: aload 12
        //   759: astore 14
        //   761: aload 31
        //   763: iconst_0
        //   764: aload 20
        //   766: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   769: aload 17
        //   771: astore 15
        //   773: aload 13
        //   775: astore 16
        //   777: aload 12
        //   779: astore 18
        //   781: aload 12
        //   783: astore 14
        //   785: iload 8
        //   787: invokestatic 171	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   790: astore 19
        //   792: aload 17
        //   794: astore 15
        //   796: aload 19
        //   798: astore 16
        //   800: aload 12
        //   802: astore 18
        //   804: aload 12
        //   806: astore 14
        //   808: aload 20
        //   810: aload 26
        //   812: invokevirtual 165	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   815: aload 17
        //   817: astore 14
        //   819: aload 19
        //   821: astore 13
        //   823: goto -407 -> 416
        //   826: aload 17
        //   828: astore 15
        //   830: aload 13
        //   832: astore 16
        //   834: aload 12
        //   836: astore 18
        //   838: aload 12
        //   840: astore 14
        //   842: aload 31
        //   844: aload 20
        //   846: invokevirtual 186	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   849: pop
        //   850: aload 13
        //   852: astore 19
        //   854: goto -62 -> 792
        //   857: astore 12
        //   859: aload 18
        //   861: astore 13
        //   863: aload 13
        //   865: astore 14
        //   867: ldc -68
        //   869: aload 12
        //   871: invokestatic 194	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   874: aload 15
        //   876: astore 18
        //   878: aload 16
        //   880: astore 20
        //   882: aload 13
        //   884: astore 12
        //   886: aload 13
        //   888: ifnull +22 -> 910
        //   891: aload 13
        //   893: invokeinterface 197 1 0
        //   898: aload 13
        //   900: astore 12
        //   902: aload 16
        //   904: astore 20
        //   906: aload 15
        //   908: astore 18
        //   910: aload 23
        //   912: astore 15
        //   914: aload 12
        //   916: astore 14
        //   918: aload 24
        //   920: astore 16
        //   922: aload 12
        //   924: astore 16
        //   926: getstatic 66	android/os/Build$VERSION:SDK_INT	I
        //   929: bipush 23
        //   931: if_icmplt +70 -> 1001
        //   934: aload 22
        //   936: astore 17
        //   938: aload 12
        //   940: astore 19
        //   942: aload 23
        //   944: astore 15
        //   946: aload 12
        //   948: astore 14
        //   950: aload 24
        //   952: astore 16
        //   954: aload 12
        //   956: astore 16
        //   958: getstatic 66	android/os/Build$VERSION:SDK_INT	I
        //   961: bipush 23
        //   963: if_icmplt +933 -> 1896
        //   966: aload 22
        //   968: astore 17
        //   970: aload 12
        //   972: astore 19
        //   974: aload 23
        //   976: astore 15
        //   978: aload 12
        //   980: astore 14
        //   982: aload 24
        //   984: astore 16
        //   986: aload 12
        //   988: astore 16
        //   990: getstatic 72	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   993: ldc 74
        //   995: invokevirtual 80	android/content/Context:checkSelfPermission	(Ljava/lang/String;)I
        //   998: ifne +898 -> 1896
        //   1001: aload 23
        //   1003: astore 15
        //   1005: aload 12
        //   1007: astore 14
        //   1009: aload 24
        //   1011: astore 16
        //   1013: aload 12
        //   1015: astore 16
        //   1017: aload 33
        //   1019: invokevirtual 200	java/util/HashMap:clear	()V
        //   1022: aconst_null
        //   1023: astore 25
        //   1025: aload 23
        //   1027: astore 15
        //   1029: aload 12
        //   1031: astore 14
        //   1033: aload 24
        //   1035: astore 16
        //   1037: aload 12
        //   1039: astore 16
        //   1041: getstatic 72	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   1044: invokevirtual 84	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
        //   1047: getstatic 203	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
        //   1050: invokestatic 206	org/telegram/messenger/MediaController:access$5900	()[Ljava/lang/String;
        //   1053: aconst_null
        //   1054: aconst_null
        //   1055: ldc 96
        //   1057: invokestatic 100	android/provider/MediaStore$Images$Media:query	(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   1060: astore 13
        //   1062: aload 22
        //   1064: astore 17
        //   1066: aload 13
        //   1068: astore 19
        //   1070: aload 13
        //   1072: ifnull +824 -> 1896
        //   1075: aload 23
        //   1077: astore 15
        //   1079: aload 13
        //   1081: astore 14
        //   1083: aload 24
        //   1085: astore 16
        //   1087: aload 13
        //   1089: astore 16
        //   1091: aload 13
        //   1093: ldc 102
        //   1095: invokeinterface 107 2 0
        //   1100: istore_1
        //   1101: aload 23
        //   1103: astore 15
        //   1105: aload 13
        //   1107: astore 14
        //   1109: aload 24
        //   1111: astore 16
        //   1113: aload 13
        //   1115: astore 16
        //   1117: aload 13
        //   1119: ldc 109
        //   1121: invokeinterface 107 2 0
        //   1126: istore_2
        //   1127: aload 23
        //   1129: astore 15
        //   1131: aload 13
        //   1133: astore 14
        //   1135: aload 24
        //   1137: astore 16
        //   1139: aload 13
        //   1141: astore 16
        //   1143: aload 13
        //   1145: ldc 111
        //   1147: invokeinterface 107 2 0
        //   1152: istore_3
        //   1153: aload 23
        //   1155: astore 15
        //   1157: aload 13
        //   1159: astore 14
        //   1161: aload 24
        //   1163: astore 16
        //   1165: aload 13
        //   1167: astore 16
        //   1169: aload 13
        //   1171: ldc 113
        //   1173: invokeinterface 107 2 0
        //   1178: istore 4
        //   1180: aload 23
        //   1182: astore 15
        //   1184: aload 13
        //   1186: astore 14
        //   1188: aload 24
        //   1190: astore 16
        //   1192: aload 13
        //   1194: astore 16
        //   1196: aload 13
        //   1198: ldc 115
        //   1200: invokeinterface 107 2 0
        //   1205: istore 5
        //   1207: aload 21
        //   1209: astore 12
        //   1211: aload 25
        //   1213: astore 21
        //   1215: aload 12
        //   1217: astore 17
        //   1219: aload 13
        //   1221: astore 19
        //   1223: aload 12
        //   1225: astore 15
        //   1227: aload 13
        //   1229: astore 14
        //   1231: aload 12
        //   1233: astore 16
        //   1235: aload 13
        //   1237: astore 16
        //   1239: aload 13
        //   1241: invokeinterface 121 1 0
        //   1246: ifeq +650 -> 1896
        //   1249: aload 12
        //   1251: astore 15
        //   1253: aload 13
        //   1255: astore 14
        //   1257: aload 12
        //   1259: astore 16
        //   1261: aload 13
        //   1263: astore 16
        //   1265: aload 13
        //   1267: iload_1
        //   1268: invokeinterface 125 2 0
        //   1273: istore 6
        //   1275: aload 12
        //   1277: astore 15
        //   1279: aload 13
        //   1281: astore 14
        //   1283: aload 12
        //   1285: astore 16
        //   1287: aload 13
        //   1289: astore 16
        //   1291: aload 13
        //   1293: iload_2
        //   1294: invokeinterface 125 2 0
        //   1299: istore 7
        //   1301: aload 12
        //   1303: astore 15
        //   1305: aload 13
        //   1307: astore 14
        //   1309: aload 12
        //   1311: astore 16
        //   1313: aload 13
        //   1315: astore 16
        //   1317: aload 13
        //   1319: iload_3
        //   1320: invokeinterface 129 2 0
        //   1325: astore 24
        //   1327: aload 12
        //   1329: astore 15
        //   1331: aload 13
        //   1333: astore 14
        //   1335: aload 12
        //   1337: astore 16
        //   1339: aload 13
        //   1341: astore 16
        //   1343: aload 13
        //   1345: iload 4
        //   1347: invokeinterface 129 2 0
        //   1352: astore 23
        //   1354: aload 12
        //   1356: astore 15
        //   1358: aload 13
        //   1360: astore 14
        //   1362: aload 12
        //   1364: astore 16
        //   1366: aload 13
        //   1368: astore 16
        //   1370: aload 13
        //   1372: iload 5
        //   1374: invokeinterface 133 2 0
        //   1379: lstore 10
        //   1381: aload 23
        //   1383: ifnull -168 -> 1215
        //   1386: aload 12
        //   1388: astore 15
        //   1390: aload 13
        //   1392: astore 14
        //   1394: aload 12
        //   1396: astore 16
        //   1398: aload 13
        //   1400: astore 16
        //   1402: aload 23
        //   1404: invokevirtual 139	java/lang/String:length	()I
        //   1407: ifeq -192 -> 1215
        //   1410: aload 12
        //   1412: astore 15
        //   1414: aload 13
        //   1416: astore 14
        //   1418: aload 12
        //   1420: astore 16
        //   1422: aload 13
        //   1424: astore 16
        //   1426: new 141	org/telegram/messenger/MediaController$PhotoEntry
        //   1429: dup
        //   1430: iload 7
        //   1432: iload 6
        //   1434: lload 10
        //   1436: aload 23
        //   1438: iconst_0
        //   1439: iconst_1
        //   1440: invokespecial 144	org/telegram/messenger/MediaController$PhotoEntry:<init>	(IIJLjava/lang/String;IZ)V
        //   1443: astore 22
        //   1445: aload 21
        //   1447: astore 17
        //   1449: aload 21
        //   1451: ifnonnull +63 -> 1514
        //   1454: aload 12
        //   1456: astore 15
        //   1458: aload 13
        //   1460: astore 14
        //   1462: aload 12
        //   1464: astore 16
        //   1466: aload 13
        //   1468: astore 16
        //   1470: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   1473: dup
        //   1474: iconst_0
        //   1475: ldc -48
        //   1477: ldc -47
        //   1479: invokestatic 154	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   1482: aload 22
        //   1484: iconst_1
        //   1485: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;Z)V
        //   1488: astore 17
        //   1490: aload 12
        //   1492: astore 15
        //   1494: aload 13
        //   1496: astore 14
        //   1498: aload 12
        //   1500: astore 16
        //   1502: aload 13
        //   1504: astore 16
        //   1506: aload 32
        //   1508: iconst_0
        //   1509: aload 17
        //   1511: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   1514: aload 17
        //   1516: ifnull +26 -> 1542
        //   1519: aload 12
        //   1521: astore 15
        //   1523: aload 13
        //   1525: astore 14
        //   1527: aload 12
        //   1529: astore 16
        //   1531: aload 13
        //   1533: astore 16
        //   1535: aload 17
        //   1537: aload 22
        //   1539: invokevirtual 165	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   1542: aload 12
        //   1544: astore 15
        //   1546: aload 13
        //   1548: astore 14
        //   1550: aload 12
        //   1552: astore 16
        //   1554: aload 13
        //   1556: astore 16
        //   1558: aload 33
        //   1560: iload 7
        //   1562: invokestatic 171	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1565: invokevirtual 175	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   1568: checkcast 146	org/telegram/messenger/MediaController$AlbumEntry
        //   1571: astore 19
        //   1573: aload 19
        //   1575: astore 14
        //   1577: aload 19
        //   1579: ifnonnull +310 -> 1889
        //   1582: aload 12
        //   1584: astore 15
        //   1586: aload 13
        //   1588: astore 14
        //   1590: aload 12
        //   1592: astore 16
        //   1594: aload 13
        //   1596: astore 16
        //   1598: new 146	org/telegram/messenger/MediaController$AlbumEntry
        //   1601: dup
        //   1602: iload 7
        //   1604: aload 24
        //   1606: aload 22
        //   1608: iconst_1
        //   1609: invokespecial 157	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;Z)V
        //   1612: astore 19
        //   1614: aload 12
        //   1616: astore 15
        //   1618: aload 13
        //   1620: astore 14
        //   1622: aload 12
        //   1624: astore 16
        //   1626: aload 13
        //   1628: astore 16
        //   1630: aload 33
        //   1632: iload 7
        //   1634: invokestatic 171	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1637: aload 19
        //   1639: invokevirtual 179	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   1642: pop
        //   1643: aload 12
        //   1645: ifnonnull +216 -> 1861
        //   1648: aload 34
        //   1650: ifnull +211 -> 1861
        //   1653: aload 23
        //   1655: ifnull +206 -> 1861
        //   1658: aload 12
        //   1660: astore 15
        //   1662: aload 13
        //   1664: astore 14
        //   1666: aload 12
        //   1668: astore 16
        //   1670: aload 13
        //   1672: astore 16
        //   1674: aload 23
        //   1676: aload 34
        //   1678: invokevirtual 183	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   1681: ifeq +180 -> 1861
        //   1684: aload 12
        //   1686: astore 15
        //   1688: aload 13
        //   1690: astore 14
        //   1692: aload 12
        //   1694: astore 16
        //   1696: aload 13
        //   1698: astore 16
        //   1700: aload 32
        //   1702: iconst_0
        //   1703: aload 19
        //   1705: invokevirtual 161	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   1708: iload 7
        //   1710: invokestatic 171	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1713: astore 12
        //   1715: aload 13
        //   1717: astore 14
        //   1719: aload 19
        //   1721: aload 22
        //   1723: invokevirtual 165	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   1726: aload 17
        //   1728: astore 21
        //   1730: goto -515 -> 1215
        //   1733: aload 12
        //   1735: astore 17
        //   1737: aload 14
        //   1739: astore 19
        //   1741: aload 19
        //   1743: astore 18
        //   1745: aload 13
        //   1747: astore 20
        //   1749: aload 17
        //   1751: astore 12
        //   1753: aload 17
        //   1755: ifnull -845 -> 910
        //   1758: aload 17
        //   1760: invokeinterface 197 1 0
        //   1765: aload 19
        //   1767: astore 18
        //   1769: aload 13
        //   1771: astore 20
        //   1773: aload 17
        //   1775: astore 12
        //   1777: goto -867 -> 910
        //   1780: astore 12
        //   1782: ldc -68
        //   1784: aload 12
        //   1786: invokestatic 194	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1789: aload 19
        //   1791: astore 18
        //   1793: aload 13
        //   1795: astore 20
        //   1797: aload 17
        //   1799: astore 12
        //   1801: goto -891 -> 910
        //   1804: astore 12
        //   1806: ldc -68
        //   1808: aload 12
        //   1810: invokestatic 194	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1813: aload 15
        //   1815: astore 18
        //   1817: aload 16
        //   1819: astore 20
        //   1821: aload 13
        //   1823: astore 12
        //   1825: goto -915 -> 910
        //   1828: astore 13
        //   1830: aload 14
        //   1832: astore 12
        //   1834: aload 12
        //   1836: ifnull +10 -> 1846
        //   1839: aload 12
        //   1841: invokeinterface 197 1 0
        //   1846: aload 13
        //   1848: athrow
        //   1849: astore 12
        //   1851: ldc -68
        //   1853: aload 12
        //   1855: invokestatic 194	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1858: goto -12 -> 1846
        //   1861: aload 12
        //   1863: astore 15
        //   1865: aload 13
        //   1867: astore 14
        //   1869: aload 12
        //   1871: astore 16
        //   1873: aload 13
        //   1875: astore 16
        //   1877: aload 32
        //   1879: aload 19
        //   1881: invokevirtual 186	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   1884: pop
        //   1885: aload 19
        //   1887: astore 14
        //   1889: aload 14
        //   1891: astore 19
        //   1893: goto -178 -> 1715
        //   1896: aload 17
        //   1898: astore 14
        //   1900: aload 19
        //   1902: ifnull +14 -> 1916
        //   1905: aload 19
        //   1907: invokeinterface 197 1 0
        //   1912: aload 17
        //   1914: astore 14
        //   1916: aload_0
        //   1917: getfield 16	org/telegram/messenger/MediaController$23:val$guid	I
        //   1920: aload 31
        //   1922: aload 20
        //   1924: aload 32
        //   1926: aload 14
        //   1928: aload 18
        //   1930: iconst_0
        //   1931: invokestatic 213	org/telegram/messenger/MediaController:access$6000	(ILjava/util/ArrayList;Ljava/lang/Integer;Ljava/util/ArrayList;Ljava/lang/Integer;Lorg/telegram/messenger/MediaController$AlbumEntry;I)V
        //   1934: return
        //   1935: astore 12
        //   1937: ldc -68
        //   1939: aload 12
        //   1941: invokestatic 194	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1944: aload 17
        //   1946: astore 14
        //   1948: goto -32 -> 1916
        //   1951: astore 13
        //   1953: aload 15
        //   1955: astore 12
        //   1957: aload 13
        //   1959: astore 15
        //   1961: aload 14
        //   1963: astore 13
        //   1965: aload 13
        //   1967: astore 14
        //   1969: ldc -68
        //   1971: aload 15
        //   1973: invokestatic 194	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1976: aload 12
        //   1978: astore 14
        //   1980: aload 13
        //   1982: ifnull -66 -> 1916
        //   1985: aload 13
        //   1987: invokeinterface 197 1 0
        //   1992: aload 12
        //   1994: astore 14
        //   1996: goto -80 -> 1916
        //   1999: astore 13
        //   2001: ldc -68
        //   2003: aload 13
        //   2005: invokestatic 194	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   2008: aload 12
        //   2010: astore 14
        //   2012: goto -96 -> 1916
        //   2015: astore 12
        //   2017: aload 16
        //   2019: astore 14
        //   2021: aload 14
        //   2023: ifnull +10 -> 2033
        //   2026: aload 14
        //   2028: invokeinterface 197 1 0
        //   2033: aload 12
        //   2035: athrow
        //   2036: astore 13
        //   2038: ldc -68
        //   2040: aload 13
        //   2042: invokestatic 194	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   2045: goto -12 -> 2033
        //   2048: astore 12
        //   2050: goto -29 -> 2021
        //   2053: astore 15
        //   2055: goto -90 -> 1965
        //   2058: astore 13
        //   2060: goto -226 -> 1834
        //   2063: astore 17
        //   2065: aload 14
        //   2067: astore 15
        //   2069: aload 13
        //   2071: astore 16
        //   2073: aload 12
        //   2075: astore 13
        //   2077: aload 17
        //   2079: astore 12
        //   2081: goto -1218 -> 863
        //   2084: aload 14
        //   2086: astore 17
        //   2088: goto -1517 -> 571
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	2091	0	this	23
        //   275	993	1	i	int
        //   301	993	2	j	int
        //   327	993	3	k	int
        //   353	993	4	m	int
        //   380	993	5	n	int
        //   407	1026	6	i1	int
        //   434	1275	7	i2	int
        //   444	342	8	i3	int
        //   487	28	9	i4	int
        //   476	959	10	l	long
        //   95	744	12	localCursor	Cursor
        //   857	13	12	localThrowable1	Throwable
        //   884	892	12	localObject1	Object
        //   1780	5	12	localException1	Exception
        //   1799	1	12	localObject2	Object
        //   1804	5	12	localException2	Exception
        //   1823	17	12	localObject3	Object
        //   1849	21	12	localException3	Exception
        //   1935	5	12	localException4	Exception
        //   1955	54	12	localObject4	Object
        //   2015	19	12	localObject5	Object
        //   2048	26	12	localObject6	Object
        //   2079	1	12	localObject7	Object
        //   139	1683	13	localObject8	Object
        //   1828	46	13	localObject9	Object
        //   1951	7	13	localThrowable2	Throwable
        //   1963	23	13	localObject10	Object
        //   1999	5	13	localException5	Exception
        //   2036	5	13	localException6	Exception
        //   2058	12	13	localObject11	Object
        //   2075	1	13	localObject12	Object
        //   111	1974	14	localObject13	Object
        //   99	1873	15	localObject14	Object
        //   2053	1	15	localThrowable3	Throwable
        //   2067	1	15	localObject15	Object
        //   103	1969	16	localObject16	Object
        //   143	1802	17	localObject17	Object
        //   2063	15	17	localThrowable4	Throwable
        //   2086	1	17	localObject18	Object
        //   107	1822	18	localObject19	Object
        //   135	1771	19	localObject20	Object
        //   74	1849	20	localObject21	Object
        //   86	1643	21	localObject22	Object
        //   77	1645	22	localPhotoEntry1	MediaController.PhotoEntry
        //   80	1595	23	str1	String
        //   83	1522	24	str2	String
        //   31	1181	25	localAlbumEntry	MediaController.AlbumEntry
        //   28	783	26	localPhotoEntry2	MediaController.PhotoEntry
        //   68	668	27	str3	String
        //   71	595	28	str4	String
        //   89	89	29	localObject23	Object
        //   92	117	30	localObject24	Object
        //   7	1914	31	localArrayList1	ArrayList
        //   16	1909	32	localArrayList2	ArrayList
        //   25	1606	33	localHashMap	HashMap
        //   65	1612	34	str5	String
        // Exception table:
        //   from	to	target	type
        //   113	121	857	java/lang/Throwable
        //   149	157	857	java/lang/Throwable
        //   185	196	857	java/lang/Throwable
        //   212	233	857	java/lang/Throwable
        //   266	276	857	java/lang/Throwable
        //   292	302	857	java/lang/Throwable
        //   318	328	857	java/lang/Throwable
        //   344	355	857	java/lang/Throwable
        //   371	382	857	java/lang/Throwable
        //   398	409	857	java/lang/Throwable
        //   563	571	857	java/lang/Throwable
        //   592	599	857	java/lang/Throwable
        //   615	630	857	java/lang/Throwable
        //   659	675	857	java/lang/Throwable
        //   691	704	857	java/lang/Throwable
        //   735	745	857	java/lang/Throwable
        //   761	769	857	java/lang/Throwable
        //   785	792	857	java/lang/Throwable
        //   808	815	857	java/lang/Throwable
        //   842	850	857	java/lang/Throwable
        //   1758	1765	1780	java/lang/Exception
        //   891	898	1804	java/lang/Exception
        //   113	121	1828	finally
        //   149	157	1828	finally
        //   185	196	1828	finally
        //   212	233	1828	finally
        //   266	276	1828	finally
        //   292	302	1828	finally
        //   318	328	1828	finally
        //   344	355	1828	finally
        //   371	382	1828	finally
        //   398	409	1828	finally
        //   563	571	1828	finally
        //   592	599	1828	finally
        //   615	630	1828	finally
        //   659	675	1828	finally
        //   691	704	1828	finally
        //   735	745	1828	finally
        //   761	769	1828	finally
        //   785	792	1828	finally
        //   808	815	1828	finally
        //   842	850	1828	finally
        //   867	874	1828	finally
        //   1839	1846	1849	java/lang/Exception
        //   1905	1912	1935	java/lang/Exception
        //   926	934	1951	java/lang/Throwable
        //   958	966	1951	java/lang/Throwable
        //   990	1001	1951	java/lang/Throwable
        //   1017	1022	1951	java/lang/Throwable
        //   1041	1062	1951	java/lang/Throwable
        //   1091	1101	1951	java/lang/Throwable
        //   1117	1127	1951	java/lang/Throwable
        //   1143	1153	1951	java/lang/Throwable
        //   1169	1180	1951	java/lang/Throwable
        //   1196	1207	1951	java/lang/Throwable
        //   1239	1249	1951	java/lang/Throwable
        //   1265	1275	1951	java/lang/Throwable
        //   1291	1301	1951	java/lang/Throwable
        //   1317	1327	1951	java/lang/Throwable
        //   1343	1354	1951	java/lang/Throwable
        //   1370	1381	1951	java/lang/Throwable
        //   1402	1410	1951	java/lang/Throwable
        //   1426	1445	1951	java/lang/Throwable
        //   1470	1490	1951	java/lang/Throwable
        //   1506	1514	1951	java/lang/Throwable
        //   1535	1542	1951	java/lang/Throwable
        //   1558	1573	1951	java/lang/Throwable
        //   1598	1614	1951	java/lang/Throwable
        //   1630	1643	1951	java/lang/Throwable
        //   1674	1684	1951	java/lang/Throwable
        //   1700	1708	1951	java/lang/Throwable
        //   1877	1885	1951	java/lang/Throwable
        //   1985	1992	1999	java/lang/Exception
        //   926	934	2015	finally
        //   958	966	2015	finally
        //   990	1001	2015	finally
        //   1017	1022	2015	finally
        //   1041	1062	2015	finally
        //   1091	1101	2015	finally
        //   1117	1127	2015	finally
        //   1143	1153	2015	finally
        //   1169	1180	2015	finally
        //   1196	1207	2015	finally
        //   1239	1249	2015	finally
        //   1265	1275	2015	finally
        //   1291	1301	2015	finally
        //   1317	1327	2015	finally
        //   1343	1354	2015	finally
        //   1370	1381	2015	finally
        //   1402	1410	2015	finally
        //   1426	1445	2015	finally
        //   1470	1490	2015	finally
        //   1506	1514	2015	finally
        //   1535	1542	2015	finally
        //   1558	1573	2015	finally
        //   1598	1614	2015	finally
        //   1630	1643	2015	finally
        //   1674	1684	2015	finally
        //   1700	1708	2015	finally
        //   1877	1885	2015	finally
        //   2026	2033	2036	java/lang/Exception
        //   1719	1726	2048	finally
        //   1969	1976	2048	finally
        //   1719	1726	2053	java/lang/Throwable
        //   416	489	2058	finally
        //   494	522	2058	finally
        //   527	547	2058	finally
        //   416	489	2063	java/lang/Throwable
        //   494	522	2063	java/lang/Throwable
        //   527	547	2063	java/lang/Throwable
      }
    });
    localThread.setPriority(1);
    localThread.start();
  }
  
  private native int openOpusFile(String paramString);
  
  private void playNextMessage(boolean paramBoolean)
  {
    ArrayList localArrayList;
    if (this.shuffleMusic)
    {
      localArrayList = this.shuffledPlaylist;
      if ((!paramBoolean) || (this.repeatMode != 2) || (this.forceLoopCurrentPlaylist)) {
        break label62;
      }
      cleanupPlayer(false, false);
      playAudio((MessageObject)localArrayList.get(this.currentPlaylistNum));
    }
    label62:
    label353:
    do
    {
      do
      {
        return;
        localArrayList = this.playlist;
        break;
        this.currentPlaylistNum += 1;
        if (this.currentPlaylistNum < localArrayList.size()) {
          break label353;
        }
        this.currentPlaylistNum = 0;
        if ((!paramBoolean) || (this.repeatMode != 0) || (this.forceLoopCurrentPlaylist)) {
          break label353;
        }
      } while ((this.audioPlayer == null) && (this.audioTrackPlayer == null));
      if (this.audioPlayer != null) {}
      for (;;)
      {
        try
        {
          this.audioPlayer.reset();
        }
        catch (Exception localException2)
        {
          try
          {
            this.audioPlayer.stop();
          }
          catch (Exception localException2)
          {
            try
            {
              this.audioPlayer.release();
              this.audioPlayer = null;
              stopProgressTimer();
              this.lastProgress = 0;
              this.buffersWrited = 0;
              this.isPaused = true;
              this.playingMessageObject.audioProgress = 0.0F;
              this.playingMessageObject.audioProgressSec = 0;
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0) });
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
              return;
              localException1 = localException1;
              FileLog.e("tmessages", localException1);
              continue;
              localException2 = localException2;
              FileLog.e("tmessages", localException2);
              continue;
            }
            catch (Exception localException3)
            {
              FileLog.e("tmessages", localException3);
              continue;
            }
          }
        }
        if (this.audioTrackPlayer != null) {
          try
          {
            synchronized (this.playerObjectSync)
            {
              this.audioTrackPlayer.pause();
              this.audioTrackPlayer.flush();
            }
          }
          catch (Exception localException4)
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
                localException4 = localException4;
                FileLog.e("tmessages", localException4);
              }
            }
            catch (Exception localException5)
            {
              for (;;)
              {
                FileLog.e("tmessages", localException5);
              }
            }
          }
        }
      }
    } while ((this.currentPlaylistNum < 0) || (this.currentPlaylistNum >= ((ArrayList)???).size()));
    this.playMusicAgain = true;
    playAudio((MessageObject)((ArrayList)???).get(this.currentPlaylistNum));
  }
  
  private void processLaterArrays()
  {
    Iterator localIterator = this.addLaterArray.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      addLoadingFileObserver((String)localEntry.getKey(), (FileDownloadProgressListener)localEntry.getValue());
    }
    this.addLaterArray.clear();
    localIterator = this.deleteLaterArray.iterator();
    while (localIterator.hasNext()) {
      removeLoadingFileObserver((FileDownloadProgressListener)localIterator.next());
    }
    this.deleteLaterArray.clear();
  }
  
  @TargetApi(16)
  private long readAndWriteTrack(MessageObject paramMessageObject, MediaExtractor paramMediaExtractor, MP4Builder paramMP4Builder, MediaCodec.BufferInfo paramBufferInfo, long paramLong1, long paramLong2, File paramFile, boolean paramBoolean)
    throws Exception
  {
    int m = selectTrack(paramMediaExtractor, paramBoolean);
    if (m >= 0)
    {
      paramMediaExtractor.selectTrack(m);
      Object localObject = paramMediaExtractor.getTrackFormat(m);
      int n = paramMP4Builder.addTrack((MediaFormat)localObject, paramBoolean);
      int i = ((MediaFormat)localObject).getInteger("max-input-size");
      int k = 0;
      long l1;
      long l2;
      label86:
      int j;
      int i1;
      label143:
      long l3;
      label290:
      long l5;
      long l6;
      if (paramLong1 > 0L)
      {
        paramMediaExtractor.seekTo(paramLong1, 0);
        localObject = ByteBuffer.allocateDirect(i);
        l1 = -1L;
        checkConversionCanceled();
        l2 = -100L;
        if (k != 0) {
          break label422;
        }
        checkConversionCanceled();
        i = 0;
        j = 0;
        i1 = paramMediaExtractor.getSampleTrackIndex();
        if (i1 != m) {
          break label382;
        }
        paramBufferInfo.size = paramMediaExtractor.readSampleData((ByteBuffer)localObject, 0);
        if (paramBufferInfo.size < 0) {
          break label360;
        }
        paramBufferInfo.presentationTimeUs = paramMediaExtractor.getSampleTime();
        i = j;
        l3 = l2;
        long l4 = l1;
        if (paramBufferInfo.size > 0)
        {
          i = j;
          l3 = l2;
          l4 = l1;
          if (j == 0)
          {
            l4 = l1;
            if (paramLong1 > 0L)
            {
              l4 = l1;
              if (l1 == -1L) {
                l4 = paramBufferInfo.presentationTimeUs;
              }
            }
            if ((paramLong2 >= 0L) && (paramBufferInfo.presentationTimeUs >= paramLong2)) {
              break label372;
            }
            if (paramBufferInfo.presentationTimeUs > l2)
            {
              paramBufferInfo.offset = 0;
              paramBufferInfo.flags = paramMediaExtractor.getSampleFlags();
              if (paramMP4Builder.writeSampleData(n, (ByteBuffer)localObject, paramBufferInfo, paramBoolean)) {
                didWriteData(paramMessageObject, paramFile, false, false);
              }
            }
            l3 = paramBufferInfo.presentationTimeUs;
            i = j;
          }
        }
        j = i;
        l5 = l3;
        l6 = l4;
        if (i == 0)
        {
          paramMediaExtractor.advance();
          l6 = l4;
          l5 = l3;
          j = i;
        }
      }
      for (;;)
      {
        l2 = l5;
        l1 = l6;
        if (j == 0) {
          break label86;
        }
        k = 1;
        l2 = l5;
        l1 = l6;
        break label86;
        paramMediaExtractor.seekTo(0L, 0);
        break;
        label360:
        paramBufferInfo.size = 0;
        j = 1;
        break label143;
        label372:
        i = 1;
        l3 = l2;
        break label290;
        label382:
        if (i1 == -1)
        {
          j = 1;
          l5 = l2;
          l6 = l1;
        }
        else
        {
          paramMediaExtractor.advance();
          j = i;
          l5 = l2;
          l6 = l1;
        }
      }
      label422:
      paramMediaExtractor.unselectTrack(m);
      return l1;
    }
    return -1L;
  }
  
  private native void readOpusFile(ByteBuffer paramByteBuffer, int paramInt, int[] paramArrayOfInt);
  
  public static void saveFile(final String paramString1, Context paramContext, int paramInt, final String paramString2, final String paramString3)
  {
    if (paramString1 == null) {}
    final Object localObject1;
    final boolean[] arrayOfBoolean;
    do
    {
      do
      {
        return;
        localObject2 = null;
        localObject1 = localObject2;
        if (paramString1 != null)
        {
          localObject1 = localObject2;
          if (paramString1.length() != 0)
          {
            paramString1 = new File(paramString1);
            localObject1 = paramString1;
            if (!paramString1.exists()) {
              localObject1 = null;
            }
          }
        }
      } while (localObject1 == null);
      arrayOfBoolean = new boolean[1];
    } while (!((File)localObject1).exists());
    Object localObject2 = null;
    paramString1 = null;
    if (paramContext != null) {}
    for (;;)
    {
      try
      {
        paramString1 = new ProgressDialog(paramContext);
        FileLog.e("tmessages", paramContext);
      }
      catch (Exception paramContext)
      {
        try
        {
          paramString1.setMessage(LocaleController.getString("Loading", 2131165834));
          paramString1.setCanceledOnTouchOutside(false);
          paramString1.setCancelable(true);
          paramString1.setProgressStyle(1);
          paramString1.setMax(100);
          paramString1.setOnCancelListener(new DialogInterface.OnCancelListener()
          {
            public void onCancel(DialogInterface paramAnonymousDialogInterface)
            {
              this.val$cancelled[0] = true;
            }
          });
          paramString1.show();
          new Thread(new Runnable()
          {
            /* Error */
            public void run()
            {
              // Byte code:
              //   0: aconst_null
              //   1: astore 12
              //   3: aload_0
              //   4: getfield 30	org/telegram/messenger/MediaController$22:val$type	I
              //   7: ifne +275 -> 282
              //   10: invokestatic 53	org/telegram/messenger/AndroidUtilities:generatePicturePath	()Ljava/io/File;
              //   13: astore 12
              //   15: aload 12
              //   17: invokevirtual 59	java/io/File:exists	()Z
              //   20: ifne +9 -> 29
              //   23: aload 12
              //   25: invokevirtual 62	java/io/File:createNewFile	()Z
              //   28: pop
              //   29: aconst_null
              //   30: astore 16
              //   32: aconst_null
              //   33: astore 15
              //   35: aconst_null
              //   36: astore 19
              //   38: aconst_null
              //   39: astore 18
              //   41: iconst_1
              //   42: istore_2
              //   43: invokestatic 68	java/lang/System:currentTimeMillis	()J
              //   46: lstore_3
              //   47: lload_3
              //   48: ldc2_w 69
              //   51: lsub
              //   52: lstore 5
              //   54: aload 18
              //   56: astore 13
              //   58: aload 19
              //   60: astore 14
              //   62: new 72	java/io/FileInputStream
              //   65: dup
              //   66: aload_0
              //   67: getfield 34	org/telegram/messenger/MediaController$22:val$sourceFile	Ljava/io/File;
              //   70: invokespecial 75	java/io/FileInputStream:<init>	(Ljava/io/File;)V
              //   73: invokevirtual 79	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
              //   76: astore 17
              //   78: aload 18
              //   80: astore 13
              //   82: aload 17
              //   84: astore 15
              //   86: aload 19
              //   88: astore 14
              //   90: aload 17
              //   92: astore 16
              //   94: new 81	java/io/FileOutputStream
              //   97: dup
              //   98: aload 12
              //   100: invokespecial 82	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
              //   103: invokevirtual 83	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
              //   106: astore 18
              //   108: aload 18
              //   110: astore 13
              //   112: aload 17
              //   114: astore 15
              //   116: aload 18
              //   118: astore 14
              //   120: aload 17
              //   122: astore 16
              //   124: aload 17
              //   126: invokevirtual 88	java/nio/channels/FileChannel:size	()J
              //   129: lstore 9
              //   131: lconst_0
              //   132: lstore_3
              //   133: lload_3
              //   134: lload 9
              //   136: lcmp
              //   137: ifge +32 -> 169
              //   140: aload 18
              //   142: astore 13
              //   144: aload 17
              //   146: astore 15
              //   148: aload 18
              //   150: astore 14
              //   152: aload 17
              //   154: astore 16
              //   156: aload_0
              //   157: getfield 36	org/telegram/messenger/MediaController$22:val$cancelled	[Z
              //   160: iconst_0
              //   161: baload
              //   162: istore 11
              //   164: iload 11
              //   166: ifeq +212 -> 378
              //   169: aload 17
              //   171: ifnull +8 -> 179
              //   174: aload 17
              //   176: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   179: iload_2
              //   180: istore_1
              //   181: aload 18
              //   183: ifnull +10 -> 193
              //   186: aload 18
              //   188: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   191: iload_2
              //   192: istore_1
              //   193: aload_0
              //   194: getfield 36	org/telegram/messenger/MediaController$22:val$cancelled	[Z
              //   197: iconst_0
              //   198: baload
              //   199: ifeq +11 -> 210
              //   202: aload 12
              //   204: invokevirtual 94	java/io/File:delete	()Z
              //   207: pop
              //   208: iconst_0
              //   209: istore_1
              //   210: iload_1
              //   211: ifeq +52 -> 263
              //   214: aload_0
              //   215: getfield 30	org/telegram/messenger/MediaController$22:val$type	I
              //   218: iconst_2
              //   219: if_icmpne +415 -> 634
              //   222: getstatic 100	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
              //   225: ldc 102
              //   227: invokevirtual 108	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
              //   230: checkcast 110	android/app/DownloadManager
              //   233: aload 12
              //   235: invokevirtual 114	java/io/File:getName	()Ljava/lang/String;
              //   238: aload 12
              //   240: invokevirtual 114	java/io/File:getName	()Ljava/lang/String;
              //   243: iconst_0
              //   244: aload_0
              //   245: getfield 40	org/telegram/messenger/MediaController$22:val$mime	Ljava/lang/String;
              //   248: aload 12
              //   250: invokevirtual 117	java/io/File:getAbsolutePath	()Ljava/lang/String;
              //   253: aload 12
              //   255: invokevirtual 120	java/io/File:length	()J
              //   258: iconst_1
              //   259: invokevirtual 124	android/app/DownloadManager:addCompletedDownload	(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;JZ)J
              //   262: pop2
              //   263: aload_0
              //   264: getfield 38	org/telegram/messenger/MediaController$22:val$finalProgress	Landroid/app/ProgressDialog;
              //   267: ifnull +14 -> 281
              //   270: new 15	org/telegram/messenger/MediaController$22$2
              //   273: dup
              //   274: aload_0
              //   275: invokespecial 127	org/telegram/messenger/MediaController$22$2:<init>	(Lorg/telegram/messenger/MediaController$22;)V
              //   278: invokestatic 131	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
              //   281: return
              //   282: aload_0
              //   283: getfield 30	org/telegram/messenger/MediaController$22:val$type	I
              //   286: iconst_1
              //   287: if_icmpne +11 -> 298
              //   290: invokestatic 134	org/telegram/messenger/AndroidUtilities:generateVideoPath	()Ljava/io/File;
              //   293: astore 12
              //   295: goto -280 -> 15
              //   298: aload_0
              //   299: getfield 30	org/telegram/messenger/MediaController$22:val$type	I
              //   302: iconst_2
              //   303: if_icmpne +35 -> 338
              //   306: getstatic 139	android/os/Environment:DIRECTORY_DOWNLOADS	Ljava/lang/String;
              //   309: invokestatic 143	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
              //   312: astore 12
              //   314: aload 12
              //   316: invokevirtual 146	java/io/File:mkdir	()Z
              //   319: pop
              //   320: new 55	java/io/File
              //   323: dup
              //   324: aload 12
              //   326: aload_0
              //   327: getfield 32	org/telegram/messenger/MediaController$22:val$name	Ljava/lang/String;
              //   330: invokespecial 149	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
              //   333: astore 12
              //   335: goto -320 -> 15
              //   338: aload_0
              //   339: getfield 30	org/telegram/messenger/MediaController$22:val$type	I
              //   342: iconst_3
              //   343: if_icmpne -328 -> 15
              //   346: getstatic 152	android/os/Environment:DIRECTORY_MUSIC	Ljava/lang/String;
              //   349: invokestatic 143	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
              //   352: astore 12
              //   354: aload 12
              //   356: invokevirtual 155	java/io/File:mkdirs	()Z
              //   359: pop
              //   360: new 55	java/io/File
              //   363: dup
              //   364: aload 12
              //   366: aload_0
              //   367: getfield 32	org/telegram/messenger/MediaController$22:val$name	Ljava/lang/String;
              //   370: invokespecial 149	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
              //   373: astore 12
              //   375: goto -360 -> 15
              //   378: aload 18
              //   380: astore 13
              //   382: aload 17
              //   384: astore 15
              //   386: aload 18
              //   388: astore 14
              //   390: aload 17
              //   392: astore 16
              //   394: aload 18
              //   396: aload 17
              //   398: lload_3
              //   399: ldc2_w 156
              //   402: lload 9
              //   404: lload_3
              //   405: lsub
              //   406: invokestatic 163	java/lang/Math:min	(JJ)J
              //   409: invokevirtual 167	java/nio/channels/FileChannel:transferFrom	(Ljava/nio/channels/ReadableByteChannel;JJ)J
              //   412: pop2
              //   413: lload 5
              //   415: lstore 7
              //   417: aload 18
              //   419: astore 13
              //   421: aload 17
              //   423: astore 15
              //   425: aload 18
              //   427: astore 14
              //   429: aload 17
              //   431: astore 16
              //   433: aload_0
              //   434: getfield 38	org/telegram/messenger/MediaController$22:val$finalProgress	Landroid/app/ProgressDialog;
              //   437: ifnull +94 -> 531
              //   440: lload 5
              //   442: lstore 7
              //   444: aload 18
              //   446: astore 13
              //   448: aload 17
              //   450: astore 15
              //   452: aload 18
              //   454: astore 14
              //   456: aload 17
              //   458: astore 16
              //   460: lload 5
              //   462: invokestatic 68	java/lang/System:currentTimeMillis	()J
              //   465: ldc2_w 69
              //   468: lsub
              //   469: lcmp
              //   470: ifgt +61 -> 531
              //   473: aload 18
              //   475: astore 13
              //   477: aload 17
              //   479: astore 15
              //   481: aload 18
              //   483: astore 14
              //   485: aload 17
              //   487: astore 16
              //   489: invokestatic 68	java/lang/System:currentTimeMillis	()J
              //   492: lstore 7
              //   494: aload 18
              //   496: astore 13
              //   498: aload 17
              //   500: astore 15
              //   502: aload 18
              //   504: astore 14
              //   506: aload 17
              //   508: astore 16
              //   510: new 13	org/telegram/messenger/MediaController$22$1
              //   513: dup
              //   514: aload_0
              //   515: lload_3
              //   516: l2f
              //   517: lload 9
              //   519: l2f
              //   520: fdiv
              //   521: ldc -88
              //   523: fmul
              //   524: f2i
              //   525: invokespecial 171	org/telegram/messenger/MediaController$22$1:<init>	(Lorg/telegram/messenger/MediaController$22;I)V
              //   528: invokestatic 131	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
              //   531: lload_3
              //   532: ldc2_w 156
              //   535: ladd
              //   536: lstore_3
              //   537: lload 7
              //   539: lstore 5
              //   541: goto -408 -> 133
              //   544: astore 17
              //   546: aload 13
              //   548: astore 14
              //   550: aload 15
              //   552: astore 16
              //   554: ldc -83
              //   556: aload 17
              //   558: invokestatic 179	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
              //   561: iconst_0
              //   562: istore_2
              //   563: aload 15
              //   565: ifnull +8 -> 573
              //   568: aload 15
              //   570: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   573: iload_2
              //   574: istore_1
              //   575: aload 13
              //   577: ifnull -384 -> 193
              //   580: aload 13
              //   582: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   585: iload_2
              //   586: istore_1
              //   587: goto -394 -> 193
              //   590: astore 13
              //   592: iload_2
              //   593: istore_1
              //   594: goto -401 -> 193
              //   597: astore 12
              //   599: aload 16
              //   601: ifnull +8 -> 609
              //   604: aload 16
              //   606: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   609: aload 14
              //   611: ifnull +8 -> 619
              //   614: aload 14
              //   616: invokevirtual 91	java/nio/channels/FileChannel:close	()V
              //   619: aload 12
              //   621: athrow
              //   622: astore 12
              //   624: ldc -83
              //   626: aload 12
              //   628: invokestatic 179	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
              //   631: goto -368 -> 263
              //   634: aload 12
              //   636: invokestatic 185	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
              //   639: invokestatic 189	org/telegram/messenger/AndroidUtilities:addMediaToGallery	(Landroid/net/Uri;)V
              //   642: goto -379 -> 263
              //   645: astore 13
              //   647: goto -468 -> 179
              //   650: astore 13
              //   652: iload_2
              //   653: istore_1
              //   654: goto -461 -> 193
              //   657: astore 14
              //   659: goto -86 -> 573
              //   662: astore 13
              //   664: goto -55 -> 609
              //   667: astore 13
              //   669: goto -50 -> 619
              // Local variable table:
              //   start	length	slot	name	signature
              //   0	672	0	this	22
              //   180	474	1	i	int
              //   42	611	2	j	int
              //   46	491	3	l1	long
              //   52	488	5	l2	long
              //   415	123	7	l3	long
              //   129	389	9	l4	long
              //   162	3	11	k	int
              //   1	373	12	localFile	File
              //   597	23	12	localObject1	Object
              //   622	13	12	localException1	Exception
              //   56	525	13	localFileChannel1	java.nio.channels.FileChannel
              //   590	1	13	localException2	Exception
              //   645	1	13	localException3	Exception
              //   650	1	13	localException4	Exception
              //   662	1	13	localException5	Exception
              //   667	1	13	localException6	Exception
              //   60	555	14	localObject2	Object
              //   657	1	14	localException7	Exception
              //   33	536	15	localObject3	Object
              //   30	575	16	localObject4	Object
              //   76	431	17	localFileChannel2	java.nio.channels.FileChannel
              //   544	13	17	localException8	Exception
              //   39	464	18	localFileChannel3	java.nio.channels.FileChannel
              //   36	51	19	localObject5	Object
              // Exception table:
              //   from	to	target	type
              //   62	78	544	java/lang/Exception
              //   94	108	544	java/lang/Exception
              //   124	131	544	java/lang/Exception
              //   156	164	544	java/lang/Exception
              //   394	413	544	java/lang/Exception
              //   433	440	544	java/lang/Exception
              //   460	473	544	java/lang/Exception
              //   489	494	544	java/lang/Exception
              //   510	531	544	java/lang/Exception
              //   580	585	590	java/lang/Exception
              //   62	78	597	finally
              //   94	108	597	finally
              //   124	131	597	finally
              //   156	164	597	finally
              //   394	413	597	finally
              //   433	440	597	finally
              //   460	473	597	finally
              //   489	494	597	finally
              //   510	531	597	finally
              //   554	561	597	finally
              //   3	15	622	java/lang/Exception
              //   15	29	622	java/lang/Exception
              //   43	47	622	java/lang/Exception
              //   193	208	622	java/lang/Exception
              //   214	263	622	java/lang/Exception
              //   282	295	622	java/lang/Exception
              //   298	335	622	java/lang/Exception
              //   338	375	622	java/lang/Exception
              //   619	622	622	java/lang/Exception
              //   634	642	622	java/lang/Exception
              //   174	179	645	java/lang/Exception
              //   186	191	650	java/lang/Exception
              //   568	573	657	java/lang/Exception
              //   604	609	662	java/lang/Exception
              //   614	619	667	java/lang/Exception
            }
          }).start();
          return;
        }
        catch (Exception paramContext)
        {
          for (;;) {}
        }
        paramContext = paramContext;
        paramString1 = (String)localObject2;
      }
    }
  }
  
  private native int seekOpusFile(float paramFloat);
  
  private void seekOpusPlayer(final float paramFloat)
  {
    if (paramFloat == 1.0F) {
      return;
    }
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
                MediaController.access$2602(MediaController.this, 3);
                MediaController.access$2802(MediaController.this, ((float)MediaController.this.currentTotalPcmDuration * MediaController.12.this.val$progress));
                if (MediaController.this.audioTrackPlayer != null) {
                  MediaController.this.audioTrackPlayer.play();
                }
                MediaController.access$2702(MediaController.this, (int)((float)MediaController.this.currentTotalPcmDuration / 48.0F * MediaController.12.this.val$progress));
                MediaController.this.checkPlayerQueue();
              }
            }
          });
          return;
        }
      }
    });
  }
  
  @SuppressLint({"NewApi"})
  public static MediaCodecInfo selectCodec(String paramString)
  {
    int k = MediaCodecList.getCodecCount();
    Object localObject1 = null;
    int i = 0;
    while (i < k)
    {
      MediaCodecInfo localMediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
      Object localObject2;
      if (!localMediaCodecInfo.isEncoder())
      {
        localObject2 = localObject1;
        i += 1;
        localObject1 = localObject2;
      }
      else
      {
        String[] arrayOfString = localMediaCodecInfo.getSupportedTypes();
        int m = arrayOfString.length;
        int j = 0;
        for (;;)
        {
          localObject2 = localObject1;
          if (j >= m) {
            break;
          }
          localObject2 = localObject1;
          if (arrayOfString[j].equalsIgnoreCase(paramString))
          {
            localObject1 = localMediaCodecInfo;
            if (!((MediaCodecInfo)localObject1).getName().equals("OMX.SEC.avc.enc")) {
              return (MediaCodecInfo)localObject1;
            }
            localObject2 = localObject1;
            if (((MediaCodecInfo)localObject1).getName().equals("OMX.SEC.AVC.Encoder")) {
              return (MediaCodecInfo)localObject1;
            }
          }
          j += 1;
          localObject1 = localObject2;
        }
      }
    }
    return (MediaCodecInfo)localObject1;
  }
  
  @SuppressLint({"NewApi"})
  public static int selectColorFormat(MediaCodecInfo paramMediaCodecInfo, String paramString)
  {
    paramString = paramMediaCodecInfo.getCapabilitiesForType(paramString);
    int j = 0;
    int i = 0;
    while (i < paramString.colorFormats.length)
    {
      int k = paramString.colorFormats[i];
      if (isRecognizedFormat(k))
      {
        j = k;
        if ((!paramMediaCodecInfo.getName().equals("OMX.SEC.AVC.Encoder")) || (k != 19)) {
          return k;
        }
      }
      i += 1;
    }
    return j;
  }
  
  @TargetApi(16)
  private int selectTrack(MediaExtractor paramMediaExtractor, boolean paramBoolean)
  {
    int j = paramMediaExtractor.getTrackCount();
    int i = 0;
    while (i < j)
    {
      String str = paramMediaExtractor.getTrackFormat(i).getString("mime");
      if (paramBoolean)
      {
        if (!str.startsWith("audio/")) {}
      }
      else {
        while (str.startsWith("video/")) {
          return i;
        }
      }
      i += 1;
    }
    return -5;
  }
  
  private void setPlayerVolume()
  {
    for (;;)
    {
      try
      {
        if (this.audioFocus == 1) {
          break label54;
        }
        f = 1.0F;
        if (this.audioPlayer != null)
        {
          this.audioPlayer.setVolume(f, f);
          return;
        }
        if (this.audioTrackPlayer != null)
        {
          this.audioTrackPlayer.setStereoVolume(f, f);
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
      return;
      label54:
      float f = 0.2F;
    }
  }
  
  private void startAudioAgain(boolean paramBoolean)
  {
    if (this.playingMessageObject == null) {
      return;
    }
    if (this.audioPlayer != null) {}
    final MessageObject localMessageObject;
    for (int i = 1;; i = 0)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioRouteChanged, new Object[] { Boolean.valueOf(this.useFrontSpeaker) });
      localMessageObject = this.playingMessageObject;
      float f = this.playingMessageObject.audioProgress;
      cleanupPlayer(false, true);
      localMessageObject.audioProgress = f;
      playAudio(localMessageObject);
      if (!paramBoolean) {
        break;
      }
      if (i == 0) {
        break label103;
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MediaController.this.pauseAudio(localMessageObject);
        }
      }, 100L);
      return;
    }
    label103:
    pauseAudio(localMessageObject);
  }
  
  private void startProgressTimer(final MessageObject paramMessageObject)
  {
    synchronized (this.progressTimerSync)
    {
      Timer localTimer = this.progressTimer;
      if (localTimer != null) {}
      try
      {
        this.progressTimer.cancel();
        this.progressTimer = null;
        this.progressTimer = new Timer();
        this.progressTimer.schedule(new TimerTask()
        {
          public void run()
          {
            synchronized (MediaController.this.sync)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if ((MediaController.5.this.val$currentPlayingMessageObject != null) && ((MediaController.this.audioPlayer != null) || (MediaController.this.audioTrackPlayer != null)) && (!MediaController.this.isPaused))
                  {
                    int j;
                    int k;
                    do
                    {
                      try
                      {
                        if (MediaController.this.ignoreFirstProgress != 0)
                        {
                          MediaController.access$2610(MediaController.this);
                          return;
                        }
                        if (MediaController.this.audioPlayer != null)
                        {
                          i = MediaController.this.audioPlayer.getCurrentPosition();
                          f = MediaController.this.lastProgress / MediaController.this.audioPlayer.getDuration();
                          if (i <= MediaController.this.lastProgress) {
                            break;
                          }
                          MediaController.access$2702(MediaController.this, i);
                          MediaController.5.this.val$currentPlayingMessageObject.audioProgress = f;
                          MediaController.5.this.val$currentPlayingMessageObject.audioProgressSec = (MediaController.this.lastProgress / 1000);
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, new Object[] { Integer.valueOf(MediaController.5.this.val$currentPlayingMessageObject.getId()), Float.valueOf(f) });
                          return;
                        }
                      }
                      catch (Exception localException)
                      {
                        FileLog.e("tmessages", localException);
                        return;
                      }
                      j = (int)((float)MediaController.this.lastPlayPcm / 48.0F);
                      float f = (float)MediaController.this.lastPlayPcm / (float)MediaController.this.currentTotalPcmDuration;
                      k = MediaController.this.lastProgress;
                      int i = j;
                    } while (j != k);
                  }
                }
              });
              return;
            }
          }
        }, 0L, 17L);
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
  }
  
  private native int startRecord(String paramString);
  
  private void startVideoConvertFromQueue()
  {
    if (!this.videoConvertQueue.isEmpty()) {}
    for (;;)
    {
      int i;
      synchronized (this.videoConvertSync)
      {
        this.cancelCurrentVideoConversion = false;
        ??? = (MessageObject)this.videoConvertQueue.get(0);
        Intent localIntent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
        localIntent.putExtra("path", ((MessageObject)???).messageOwner.attachPath);
        if (((MessageObject)???).messageOwner.media.document != null)
        {
          i = 0;
          if (i < ((MessageObject)???).messageOwner.media.document.attributes.size())
          {
            if (!((TLRPC.DocumentAttribute)((MessageObject)???).messageOwner.media.document.attributes.get(i) instanceof TLRPC.TL_documentAttributeAnimated)) {
              break label153;
            }
            localIntent.putExtra("gif", true);
          }
        }
        ApplicationLoader.applicationContext.startService(localIntent);
        VideoConvertRunnable.runConversion((MessageObject)???);
        return;
      }
      label153:
      i += 1;
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
          FileLog.e("tmessages", localException);
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
              String str = null;
              MediaController.19.this.val$audioToSend.date = ConnectionsManager.getInstance().getCurrentTime();
              MediaController.19.this.val$audioToSend.size = ((int)MediaController.19.this.val$recordingAudioFileToSend.length());
              Object localObject = new TLRPC.TL_documentAttributeAudio();
              ((TLRPC.TL_documentAttributeAudio)localObject).voice = true;
              ((TLRPC.TL_documentAttributeAudio)localObject).waveform = MediaController.this.getWaveform2(MediaController.this.recordSamples, MediaController.this.recordSamples.length);
              if (((TLRPC.TL_documentAttributeAudio)localObject).waveform != null) {
                ((TLRPC.TL_documentAttributeAudio)localObject).flags |= 0x4;
              }
              long l = MediaController.this.recordTimeCount;
              ((TLRPC.TL_documentAttributeAudio)localObject).duration = ((int)(MediaController.this.recordTimeCount / 1000L));
              MediaController.19.this.val$audioToSend.attributes.add(localObject);
              if (l > 700L)
              {
                if (MediaController.19.this.val$send == 1) {
                  SendMessagesHelper.getInstance().sendMessage(MediaController.19.this.val$audioToSend, null, MediaController.19.this.val$recordingAudioFileToSend.getAbsolutePath(), MediaController.this.recordDialogId, MediaController.this.recordReplyingMessageObject, null, null);
                }
                NotificationCenter localNotificationCenter = NotificationCenter.getInstance();
                int i = NotificationCenter.audioDidSent;
                if (MediaController.19.this.val$send == 2) {}
                for (localObject = MediaController.19.this.val$audioToSend;; localObject = null)
                {
                  if (MediaController.19.this.val$send == 2) {
                    str = MediaController.19.this.val$recordingAudioFileToSend.getAbsolutePath();
                  }
                  localNotificationCenter.postNotificationName(i, new Object[] { localObject, str });
                  return;
                }
              }
              MediaController.19.this.val$recordingAudioFileToSend.delete();
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
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  private native int writeFrame(ByteBuffer paramByteBuffer, int paramInt);
  
  public void addLoadingFileObserver(String paramString, FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    addLoadingFileObserver(paramString, null, paramFileDownloadProgressListener);
  }
  
  public void addLoadingFileObserver(String paramString, MessageObject paramMessageObject, FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    if (this.listenerInProgress)
    {
      this.addLaterArray.put(paramString, paramFileDownloadProgressListener);
      return;
    }
    removeLoadingFileObserver(paramFileDownloadProgressListener);
    ArrayList localArrayList2 = (ArrayList)this.loadingFileObservers.get(paramString);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.loadingFileObservers.put(paramString, localArrayList1);
    }
    localArrayList1.add(new WeakReference(paramFileDownloadProgressListener));
    if (paramMessageObject != null)
    {
      localArrayList2 = (ArrayList)this.loadingFileMessagesObservers.get(paramString);
      localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.loadingFileMessagesObservers.put(paramString, localArrayList1);
      }
      localArrayList1.add(paramMessageObject);
    }
    this.observersByTag.put(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()), paramString);
  }
  
  public boolean canAutoplayGifs()
  {
    return this.autoplayGifs;
  }
  
  public boolean canCustomTabs()
  {
    return this.customTabs;
  }
  
  public boolean canDirectShare()
  {
    return this.directShare;
  }
  
  public boolean canDownloadMedia(int paramInt)
  {
    return (getCurrentDownloadMask() & paramInt) != 0;
  }
  
  public boolean canRaiseToSpeak()
  {
    return this.raiseToSpeak;
  }
  
  public boolean canSaveToGallery()
  {
    return this.saveToGallery;
  }
  
  public void cancelVideoConvert(MessageObject arg1)
  {
    if (??? == null) {
      synchronized (this.videoConvertSync)
      {
        this.cancelCurrentVideoConversion = true;
        return;
      }
    }
    if (!this.videoConvertQueue.isEmpty())
    {
      if (this.videoConvertQueue.get(0) == ???) {}
      synchronized (this.videoConvertSync)
      {
        this.cancelCurrentVideoConversion = true;
        this.videoConvertQueue.remove(???);
        return;
      }
    }
  }
  
  public void checkAutodownloadSettings()
  {
    int j = getCurrentDownloadMask();
    if (j == this.lastCheckMask) {}
    label61:
    label84:
    label105:
    label128:
    int i;
    label223:
    label278:
    label333:
    label388:
    label443:
    label498:
    do
    {
      return;
      this.lastCheckMask = j;
      if ((j & 0x1) != 0)
      {
        if (this.photoDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(1);
        }
        if ((j & 0x2) == 0) {
          break label223;
        }
        if (this.audioDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(2);
        }
        if ((j & 0x8) == 0) {
          break label278;
        }
        if (this.documentDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(8);
        }
        if ((j & 0x4) == 0) {
          break label333;
        }
        if (this.videoDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(4);
        }
        if ((j & 0x10) == 0) {
          break label388;
        }
        if (this.musicDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(16);
        }
        if ((j & 0x20) == 0) {
          break label443;
        }
        if (this.gifDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(32);
        }
      }
      for (;;)
      {
        i = getAutodownloadMask();
        if (i != 0) {
          break label498;
        }
        MessagesStorage.getInstance().clearDownloadQueue(0);
        return;
        i = 0;
        Object localObject;
        while (i < this.photoDownloadQueue.size())
        {
          localObject = (DownloadObject)this.photoDownloadQueue.get(i);
          FileLoader.getInstance().cancelLoadFile((TLRPC.PhotoSize)((DownloadObject)localObject).object);
          i += 1;
        }
        this.photoDownloadQueue.clear();
        break;
        i = 0;
        while (i < this.audioDownloadQueue.size())
        {
          localObject = (DownloadObject)this.audioDownloadQueue.get(i);
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)((DownloadObject)localObject).object);
          i += 1;
        }
        this.audioDownloadQueue.clear();
        break label61;
        i = 0;
        while (i < this.documentDownloadQueue.size())
        {
          localObject = (TLRPC.Document)((DownloadObject)this.documentDownloadQueue.get(i)).object;
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
          i += 1;
        }
        this.documentDownloadQueue.clear();
        break label84;
        i = 0;
        while (i < this.videoDownloadQueue.size())
        {
          localObject = (DownloadObject)this.videoDownloadQueue.get(i);
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)((DownloadObject)localObject).object);
          i += 1;
        }
        this.videoDownloadQueue.clear();
        break label105;
        i = 0;
        while (i < this.musicDownloadQueue.size())
        {
          localObject = (TLRPC.Document)((DownloadObject)this.musicDownloadQueue.get(i)).object;
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
          i += 1;
        }
        this.musicDownloadQueue.clear();
        break label128;
        i = 0;
        while (i < this.gifDownloadQueue.size())
        {
          localObject = (TLRPC.Document)((DownloadObject)this.gifDownloadQueue.get(i)).object;
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
          i += 1;
        }
        this.gifDownloadQueue.clear();
      }
      if ((i & 0x1) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(1);
      }
      if ((i & 0x2) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(2);
      }
      if ((i & 0x4) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(4);
      }
      if ((i & 0x8) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(8);
      }
      if ((i & 0x10) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(16);
      }
    } while ((i & 0x20) != 0);
    MessagesStorage.getInstance().clearDownloadQueue(32);
  }
  
  public void checkSaveToGalleryFiles()
  {
    try
    {
      File localFile2 = new File(Environment.getExternalStorageDirectory(), "Telegram");
      File localFile1 = new File(localFile2, "Telegram Images");
      localFile1.mkdir();
      localFile2 = new File(localFile2, "Telegram Video");
      localFile2.mkdir();
      if (this.saveToGallery)
      {
        if (localFile1.isDirectory()) {
          new File(localFile1, ".nomedia").delete();
        }
        if (localFile2.isDirectory()) {
          new File(localFile2, ".nomedia").delete();
        }
      }
      else
      {
        if (localFile1.isDirectory()) {
          new File(localFile1, ".nomedia").createNewFile();
        }
        if (localFile2.isDirectory())
        {
          new File(localFile2, ".nomedia").createNewFile();
          return;
        }
      }
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void cleanup()
  {
    cleanupPlayer(false, true);
    this.audioInfo = null;
    this.playMusicAgain = false;
    this.photoDownloadQueue.clear();
    this.audioDownloadQueue.clear();
    this.documentDownloadQueue.clear();
    this.videoDownloadQueue.clear();
    this.musicDownloadQueue.clear();
    this.gifDownloadQueue.clear();
    this.downloadQueueKeys.clear();
    this.videoConvertQueue.clear();
    this.playlist.clear();
    this.shuffledPlaylist.clear();
    this.generatingWaveform.clear();
    this.typingTimes.clear();
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
        this.audioPlayer.reset();
      }
      catch (Exception localException2)
      {
        try
        {
          this.audioPlayer.stop();
        }
        catch (Exception localException2)
        {
          try
          {
            this.audioPlayer.release();
            this.audioPlayer = null;
            stopProgressTimer();
            this.lastProgress = 0;
            this.buffersWrited = 0;
            this.isPaused = false;
            Object localObject1;
            if (this.playingMessageObject != null)
            {
              if (this.downloadingCurrentMessage) {
                FileLoader.getInstance().cancelLoadFile(this.playingMessageObject.getDocument());
              }
              localObject1 = this.playingMessageObject;
              this.playingMessageObject.audioProgress = 0.0F;
              this.playingMessageObject.audioProgressSec = 0;
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0) });
              this.playingMessageObject = null;
              this.downloadingCurrentMessage = false;
              if (paramBoolean1)
              {
                NotificationsController.getInstance().audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                if (this.voiceMessagesPlaylist != null)
                {
                  if ((!paramBoolean3) || (this.voiceMessagesPlaylist.get(0) != localObject1)) {
                    break label440;
                  }
                  this.voiceMessagesPlaylist.remove(0);
                  this.voiceMessagesPlaylistMap.remove(Integer.valueOf(((MessageObject)localObject1).getId()));
                  if (this.voiceMessagesPlaylist.isEmpty())
                  {
                    this.voiceMessagesPlaylist = null;
                    this.voiceMessagesPlaylistMap = null;
                  }
                }
                if (this.voiceMessagesPlaylist == null) {
                  break label453;
                }
                playAudio((MessageObject)this.voiceMessagesPlaylist.get(0));
              }
              if (paramBoolean2)
              {
                localObject1 = new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class);
                ApplicationLoader.applicationContext.stopService((Intent)localObject1);
              }
            }
            if ((!this.useFrontSpeaker) && (!this.raiseToSpeak))
            {
              localObject1 = this.raiseChat;
              stopRaiseToEarSensors(this.raiseChat);
              this.raiseChat = ((ChatActivity)localObject1);
            }
            return;
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
            continue;
            localException2 = localException2;
            FileLog.e("tmessages", localException2);
            continue;
          }
          catch (Exception localException3)
          {
            FileLog.e("tmessages", localException3);
            continue;
          }
        }
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
        catch (Exception localException4)
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
              localException4 = localException4;
              FileLog.e("tmessages", localException4);
            }
          }
          catch (Exception localException5)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException5);
            }
          }
        }
        label440:
        this.voiceMessagesPlaylist = null;
        this.voiceMessagesPlaylistMap = null;
        continue;
        label453:
        if ((((MessageObject)???).isVoice()) && (((MessageObject)???).getId() != 0)) {
          startRecordingIfFromSpeaker();
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioDidReset, new Object[] { Integer.valueOf(((MessageObject)???).getId()), Boolean.valueOf(paramBoolean2) });
      }
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if ((paramInt == NotificationCenter.FileDidFailedLoad) || (paramInt == NotificationCenter.httpFileDidFailedLoad))
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramVarArgs[0];
      localObject2 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject2 != null)
      {
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject2).size())
        {
          localObject3 = (WeakReference)((ArrayList)localObject2).get(paramInt);
          if (((WeakReference)localObject3).get() != null)
          {
            ((FileDownloadProgressListener)((WeakReference)localObject3).get()).onFailedDownload((String)localObject1);
            this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener)((WeakReference)localObject3).get()).getObserverTag()));
          }
          paramInt += 1;
        }
        this.loadingFileObservers.remove(localObject1);
      }
      this.listenerInProgress = false;
      processLaterArrays();
      checkDownloadFinished((String)localObject1, ((Integer)paramVarArgs[1]).intValue());
      return;
    }
    if ((paramInt == NotificationCenter.FileDidLoaded) || (paramInt == NotificationCenter.httpFileDidLoaded))
    {
      this.listenerInProgress = true;
      paramVarArgs = (String)paramVarArgs[0];
      if ((this.downloadingCurrentMessage) && (this.playingMessageObject != null) && (FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(paramVarArgs)))
      {
        this.playMusicAgain = true;
        playAudio(this.playingMessageObject);
      }
      localObject1 = (ArrayList)this.loadingFileMessagesObservers.get(paramVarArgs);
      if (localObject1 != null)
      {
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject1).size())
        {
          ((MessageObject)((ArrayList)localObject1).get(paramInt)).mediaExists = true;
          paramInt += 1;
        }
        this.loadingFileMessagesObservers.remove(paramVarArgs);
      }
      localObject1 = (ArrayList)this.loadingFileObservers.get(paramVarArgs);
      if (localObject1 != null)
      {
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject1).size())
        {
          localObject2 = (WeakReference)((ArrayList)localObject1).get(paramInt);
          if (((WeakReference)localObject2).get() != null)
          {
            ((FileDownloadProgressListener)((WeakReference)localObject2).get()).onSuccessDownload(paramVarArgs);
            this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener)((WeakReference)localObject2).get()).getObserverTag()));
          }
          paramInt += 1;
        }
        this.loadingFileObservers.remove(paramVarArgs);
      }
      this.listenerInProgress = false;
      processLaterArrays();
      checkDownloadFinished(paramVarArgs, 0);
      return;
    }
    if (paramInt == NotificationCenter.FileLoadProgressChanged)
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramVarArgs[0];
      localObject2 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject2 != null)
      {
        paramVarArgs = (Float)paramVarArgs[1];
        localObject2 = ((ArrayList)localObject2).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (WeakReference)((Iterator)localObject2).next();
          if (((WeakReference)localObject3).get() != null) {
            ((FileDownloadProgressListener)((WeakReference)localObject3).get()).onProgressDownload((String)localObject1, paramVarArgs.floatValue());
          }
        }
      }
      this.listenerInProgress = false;
      processLaterArrays();
      return;
    }
    if (paramInt == NotificationCenter.FileUploadProgressChanged)
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramVarArgs[0];
      localObject3 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject3 != null)
      {
        localObject2 = (Float)paramVarArgs[1];
        paramVarArgs = (Boolean)paramVarArgs[2];
        localObject3 = ((ArrayList)localObject3).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          WeakReference localWeakReference = (WeakReference)((Iterator)localObject3).next();
          if (localWeakReference.get() != null) {
            ((FileDownloadProgressListener)localWeakReference.get()).onProgressUpload((String)localObject1, ((Float)localObject2).floatValue(), paramVarArgs.booleanValue());
          }
        }
      }
      this.listenerInProgress = false;
      processLaterArrays();
    }
    for (;;)
    {
      long l;
      try
      {
        paramVarArgs = SendMessagesHelper.getInstance().getDelayedMessages((String)localObject1);
        if (paramVarArgs == null) {
          break;
        }
        paramInt = 0;
        if (paramInt >= paramVarArgs.size()) {
          break;
        }
        localObject1 = (SendMessagesHelper.DelayedMessage)paramVarArgs.get(paramInt);
        if (((SendMessagesHelper.DelayedMessage)localObject1).encryptedChat != null) {
          break label1269;
        }
        l = ((SendMessagesHelper.DelayedMessage)localObject1).obj.getDialogId();
        localObject2 = (Long)this.typingTimes.get(Long.valueOf(l));
        if ((localObject2 != null) && (((Long)localObject2).longValue() + 4000L >= System.currentTimeMillis())) {
          break label1269;
        }
        if (MessageObject.isVideoDocument(((SendMessagesHelper.DelayedMessage)localObject1).documentLocation))
        {
          MessagesController.getInstance().sendTyping(l, 5, 0);
          this.typingTimes.put(Long.valueOf(l), Long.valueOf(System.currentTimeMillis()));
          break label1269;
        }
        if (((SendMessagesHelper.DelayedMessage)localObject1).documentLocation != null)
        {
          MessagesController.getInstance().sendTyping(l, 3, 0);
          continue;
        }
        if (((SendMessagesHelper.DelayedMessage)localObject1).location == null) {
          continue;
        }
      }
      catch (Exception paramVarArgs)
      {
        FileLog.e("tmessages", paramVarArgs);
        return;
      }
      MessagesController.getInstance().sendTyping(l, 4, 0);
      continue;
      if (paramInt == NotificationCenter.messagesDeleted)
      {
        paramInt = ((Integer)paramVarArgs[1]).intValue();
        paramVarArgs = (ArrayList)paramVarArgs[0];
        if ((this.playingMessageObject != null) && (paramInt == this.playingMessageObject.messageOwner.to_id.channel_id) && (paramVarArgs.contains(Integer.valueOf(this.playingMessageObject.getId())))) {
          cleanupPlayer(true, true);
        }
        if ((this.voiceMessagesPlaylist == null) || (this.voiceMessagesPlaylist.isEmpty()) || (paramInt != ((MessageObject)this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id)) {
          break;
        }
        paramInt = 0;
        while (paramInt < paramVarArgs.size())
        {
          localObject1 = (MessageObject)this.voiceMessagesPlaylistMap.remove(paramVarArgs.get(paramInt));
          if (localObject1 != null) {
            this.voiceMessagesPlaylist.remove(localObject1);
          }
          paramInt += 1;
        }
        break;
      }
      if (paramInt == NotificationCenter.removeAllMessagesFromDialog)
      {
        l = ((Long)paramVarArgs[0]).longValue();
        if ((this.playingMessageObject == null) || (this.playingMessageObject.getDialogId() != l)) {
          break;
        }
        cleanupPlayer(false, true);
        return;
      }
      if (paramInt == NotificationCenter.musicDidLoaded)
      {
        l = ((Long)paramVarArgs[0]).longValue();
        if ((this.playingMessageObject == null) || (!this.playingMessageObject.isMusic()) || (this.playingMessageObject.getDialogId() != l)) {
          break;
        }
        paramVarArgs = (ArrayList)paramVarArgs[1];
        this.playlist.addAll(0, paramVarArgs);
        if (this.shuffleMusic)
        {
          buildShuffledPlayList();
          this.currentPlaylistNum = 0;
          return;
        }
        this.currentPlaylistNum += paramVarArgs.size();
        return;
      }
      if ((paramInt != NotificationCenter.didReceivedNewMessages) || (this.voiceMessagesPlaylist == null) || (this.voiceMessagesPlaylist.isEmpty())) {
        break;
      }
      localObject1 = (MessageObject)this.voiceMessagesPlaylist.get(0);
      if (((Long)paramVarArgs[0]).longValue() != ((MessageObject)localObject1).getDialogId()) {
        break;
      }
      paramVarArgs = (ArrayList)paramVarArgs[1];
      paramInt = 0;
      while (paramInt < paramVarArgs.size())
      {
        localObject1 = (MessageObject)paramVarArgs.get(paramInt);
        if ((((MessageObject)localObject1).isVoice()) && ((!this.voiceMessagesPlaylistUnread) || ((((MessageObject)localObject1).isContentUnread()) && (!((MessageObject)localObject1).isOut()))))
        {
          this.voiceMessagesPlaylist.add(localObject1);
          this.voiceMessagesPlaylistMap.put(Integer.valueOf(((MessageObject)localObject1).getId()), localObject1);
        }
        paramInt += 1;
      }
      break;
      label1269:
      paramInt += 1;
    }
  }
  
  public int generateObserverTag()
  {
    int i = this.lastTag;
    this.lastTag = (i + 1);
    return i;
  }
  
  public void generateWaveform(MessageObject paramMessageObject)
  {
    final String str1 = paramMessageObject.getId() + "_" + paramMessageObject.getDialogId();
    final String str2 = FileLoader.getPathToMessage(paramMessageObject.messageOwner).getAbsolutePath();
    if (this.generatingWaveform.containsKey(str1)) {
      return;
    }
    this.generatingWaveform.put(str1, paramMessageObject);
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessageObject localMessageObject = (MessageObject)MediaController.this.generatingWaveform.remove(MediaController.18.this.val$id);
            if (localMessageObject == null) {}
            while (this.val$waveform == null) {
              return;
            }
            int i = 0;
            for (;;)
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
                MessagesStorage.getInstance().putMessages((TLRPC.messages_Messages)localObject, localMessageObject.getDialogId(), -1, 0, false);
                localObject = new ArrayList();
                ((ArrayList)localObject).add(localMessageObject);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(localMessageObject.getDialogId()), localObject });
                return;
              }
              i += 1;
            }
          }
        });
      }
    });
  }
  
  public AudioInfo getAudioInfo()
  {
    return this.audioInfo;
  }
  
  protected int getAutodownloadMask()
  {
    int j = 0;
    if (((this.mobileDataDownloadMask & 0x1) != 0) || ((this.wifiDownloadMask & 0x1) != 0) || ((this.roamingDownloadMask & 0x1) != 0)) {
      j = 0x0 | 0x1;
    }
    int i;
    if (((this.mobileDataDownloadMask & 0x2) == 0) && ((this.wifiDownloadMask & 0x2) == 0))
    {
      i = j;
      if ((this.roamingDownloadMask & 0x2) == 0) {}
    }
    else
    {
      i = j | 0x2;
    }
    if (((this.mobileDataDownloadMask & 0x4) == 0) && ((this.wifiDownloadMask & 0x4) == 0))
    {
      j = i;
      if ((this.roamingDownloadMask & 0x4) == 0) {}
    }
    else
    {
      j = i | 0x4;
    }
    if (((this.mobileDataDownloadMask & 0x8) == 0) && ((this.wifiDownloadMask & 0x8) == 0))
    {
      i = j;
      if ((this.roamingDownloadMask & 0x8) == 0) {}
    }
    else
    {
      i = j | 0x8;
    }
    if (((this.mobileDataDownloadMask & 0x10) == 0) && ((this.wifiDownloadMask & 0x10) == 0))
    {
      j = i;
      if ((this.roamingDownloadMask & 0x10) == 0) {}
    }
    else
    {
      j = i | 0x10;
    }
    if (((this.mobileDataDownloadMask & 0x20) == 0) && ((this.wifiDownloadMask & 0x20) == 0))
    {
      i = j;
      if ((this.roamingDownloadMask & 0x20) == 0) {}
    }
    else
    {
      i = j | 0x20;
    }
    return i;
  }
  
  public MessageObject getPlayingMessageObject()
  {
    return this.playingMessageObject;
  }
  
  public int getPlayingMessageObjectNum()
  {
    return this.currentPlaylistNum;
  }
  
  public int getRepeatMode()
  {
    return this.repeatMode;
  }
  
  public native byte[] getWaveform(String paramString);
  
  public native byte[] getWaveform2(short[] paramArrayOfShort, int paramInt);
  
  public boolean isAudioPaused()
  {
    return (this.isPaused) || (this.downloadingCurrentMessage);
  }
  
  public boolean isDownloadingCurrentMessage()
  {
    return this.downloadingCurrentMessage;
  }
  
  public boolean isPlayingAudio(MessageObject paramMessageObject)
  {
    return ((this.audioTrackPlayer != null) || (this.audioPlayer != null)) && (paramMessageObject != null) && (this.playingMessageObject != null) && ((this.playingMessageObject == null) || ((this.playingMessageObject.getId() == paramMessageObject.getId()) && (!this.downloadingCurrentMessage)));
  }
  
  protected boolean isRecordingAudio()
  {
    return (this.recordStartRunnable != null) || (this.recordingAudio != null);
  }
  
  public boolean isShuffleMusic()
  {
    return this.shuffleMusic;
  }
  
  protected void newDownloadObjectsAvailable(int paramInt)
  {
    int i = getCurrentDownloadMask();
    if (((i & 0x1) != 0) && ((paramInt & 0x1) != 0) && (this.photoDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(1);
    }
    if (((i & 0x2) != 0) && ((paramInt & 0x2) != 0) && (this.audioDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(2);
    }
    if (((i & 0x4) != 0) && ((paramInt & 0x4) != 0) && (this.videoDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(4);
    }
    if (((i & 0x8) != 0) && ((paramInt & 0x8) != 0) && (this.documentDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(8);
    }
    if (((i & 0x10) != 0) && ((paramInt & 0x10) != 0) && (this.musicDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(16);
    }
    if (((i & 0x20) != 0) && ((paramInt & 0x20) != 0) && (this.gifDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(32);
    }
  }
  
  public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
  
  public void onAudioFocusChange(int paramInt)
  {
    if (paramInt == -1)
    {
      if ((isPlayingAudio(getPlayingMessageObject())) && (!isAudioPaused())) {
        pauseAudio(getPlayingMessageObject());
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
          if ((isPlayingAudio(getPlayingMessageObject())) && (isAudioPaused())) {
            playAudio(getPlayingMessageObject());
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
        if ((isPlayingAudio(getPlayingMessageObject())) && (!isAudioPaused()))
        {
          pauseAudio(getPlayingMessageObject());
          this.resumeAudioOnFocusGain = true;
        }
      }
    }
  }
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    if (!this.sensorsStarted) {}
    label92:
    label249:
    label303:
    label506:
    label1039:
    label1101:
    label1245:
    label1251:
    label1333:
    label1557:
    for (;;)
    {
      return;
      float f;
      boolean bool;
      if (paramSensorEvent.sensor == this.proximitySensor)
      {
        FileLog.e("tmessages", "proximity changed to " + paramSensorEvent.values[0]);
        if (this.lastProximityValue == -100.0F)
        {
          this.lastProximityValue = paramSensorEvent.values[0];
          if (this.proximityHasDifferentValues) {
            this.proximityTouched = isNearToSensor(paramSensorEvent.values[0]);
          }
          if ((paramSensorEvent.sensor == this.linearSensor) || (paramSensorEvent.sensor == this.gravitySensor) || (paramSensorEvent.sensor == this.accelerometerSensor))
          {
            f = this.gravity[0] * this.linearAcceleration[0] + this.gravity[1] * this.linearAcceleration[1] + this.gravity[2] * this.linearAcceleration[2];
            if (this.raisedToBack != 6)
            {
              if ((f <= 0.0F) || (this.previousAccValue <= 0.0F)) {
                break label1101;
              }
              if ((f <= 15.0F) || (this.raisedToBack != 0)) {
                break label1039;
              }
              if ((this.raisedToTop < 6) && (!this.proximityTouched))
              {
                this.raisedToTop += 1;
                if (this.raisedToTop == 6) {
                  this.countLess = 0;
                }
              }
            }
            this.previousAccValue = f;
            if ((this.gravityFast[1] <= 2.5F) || (Math.abs(this.gravityFast[2]) >= 4.0F) || (Math.abs(this.gravityFast[0]) <= 1.5F)) {
              break label1245;
            }
            bool = true;
            this.accelerometerVertical = bool;
          }
          if ((this.raisedToBack != 6) || (!this.accelerometerVertical) || (!this.proximityTouched) || (NotificationsController.getInstance().audioManager.isWiredHeadsetOn())) {
            break label1333;
          }
          FileLog.e("tmessages", "sensor values reached");
          if ((this.playingMessageObject != null) || (this.recordStartRunnable != null) || (this.recordingAudio != null) || (PhotoViewer.getInstance().isVisible()) || (!ApplicationLoader.isScreenOn) || (this.inputFieldHasText) || (!this.allowStartRecord) || (this.raiseChat == null) || (this.callInProgress)) {
            break label1251;
          }
          if (!this.raiseToEarRecord)
          {
            FileLog.e("tmessages", "start record");
            this.useFrontSpeaker = true;
            if (!this.raiseChat.playFirstUnreadVoiceMessage())
            {
              this.raiseToEarRecord = true;
              this.useFrontSpeaker = false;
              startRecording(this.raiseChat.getDialogId(), null);
            }
            this.ignoreOnPause = true;
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.acquire();
            }
          }
          this.raisedToBack = 0;
          this.raisedToTop = 0;
          this.countLess = 0;
        }
      }
      for (;;)
      {
        if ((this.timeSinceRaise == 0L) || (this.raisedToBack != 6) || (Math.abs(System.currentTimeMillis() - this.timeSinceRaise) <= 1000L)) {
          break label1557;
        }
        this.raisedToBack = 0;
        this.raisedToTop = 0;
        this.countLess = 0;
        this.timeSinceRaise = 0L;
        return;
        if (this.lastProximityValue == paramSensorEvent.values[0]) {
          break;
        }
        this.proximityHasDifferentValues = true;
        break;
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
          break label92;
        }
        if (paramSensorEvent.sensor != this.gravitySensor) {
          break label92;
        }
        float[] arrayOfFloat1 = this.gravityFast;
        float[] arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[0];
        arrayOfFloat2[0] = f;
        arrayOfFloat1[0] = f;
        arrayOfFloat1 = this.gravityFast;
        arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[1];
        arrayOfFloat2[1] = f;
        arrayOfFloat1[1] = f;
        arrayOfFloat1 = this.gravityFast;
        arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[2];
        arrayOfFloat2[2] = f;
        arrayOfFloat1[2] = f;
        break label92;
        if (f < 15.0F) {
          this.countLess += 1;
        }
        if ((this.countLess != 10) && (this.raisedToTop == 6) && (this.raisedToBack == 0)) {
          break label249;
        }
        this.raisedToBack = 0;
        this.raisedToTop = 0;
        this.countLess = 0;
        break label249;
        if ((f >= 0.0F) || (this.previousAccValue >= 0.0F)) {
          break label249;
        }
        if ((this.raisedToTop == 6) && (f < -15.0F))
        {
          if (this.raisedToBack >= 6) {
            break label249;
          }
          this.raisedToBack += 1;
          if (this.raisedToBack != 6) {
            break label249;
          }
          this.raisedToTop = 0;
          this.countLess = 0;
          this.timeSinceRaise = System.currentTimeMillis();
          break label249;
        }
        if (f > -15.0F) {
          this.countLess += 1;
        }
        if ((this.countLess != 10) && (this.raisedToTop == 6) && (this.raisedToBack == 0)) {
          break label249;
        }
        this.raisedToTop = 0;
        this.raisedToBack = 0;
        this.countLess = 0;
        break label249;
        bool = false;
        break label303;
        if ((this.playingMessageObject == null) || (!this.playingMessageObject.isVoice()) || (this.useFrontSpeaker)) {
          break label506;
        }
        FileLog.e("tmessages", "start listen");
        if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld())) {
          this.proximityWakeLock.acquire();
        }
        this.useFrontSpeaker = true;
        startAudioAgain(false);
        this.ignoreOnPause = true;
        break label506;
        if (this.proximityTouched)
        {
          if ((this.playingMessageObject != null) && (this.playingMessageObject.isVoice()) && (!this.useFrontSpeaker))
          {
            FileLog.e("tmessages", "start listen by proximity only");
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.acquire();
            }
            this.useFrontSpeaker = true;
            startAudioAgain(false);
            this.ignoreOnPause = true;
          }
        }
        else if (!this.proximityTouched) {
          if (this.raiseToEarRecord)
          {
            FileLog.e("tmessages", "stop record");
            stopRecording(2);
            this.raiseToEarRecord = false;
            this.ignoreOnPause = false;
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.release();
            }
          }
          else if (this.useFrontSpeaker)
          {
            FileLog.e("tmessages", "stop listen");
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
  
  public boolean pauseAudio(MessageObject paramMessageObject)
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null)) || (paramMessageObject == null) || (this.playingMessageObject == null) || ((this.playingMessageObject != null) && (this.playingMessageObject.getId() != paramMessageObject.getId()))) {
      return false;
    }
    stopProgressTimer();
    try
    {
      if (this.audioPlayer != null) {
        this.audioPlayer.pause();
      }
      for (;;)
      {
        this.isPaused = true;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
        return true;
        if (this.audioTrackPlayer != null) {
          this.audioTrackPlayer.pause();
        }
      }
      return false;
    }
    catch (Exception paramMessageObject)
    {
      FileLog.e("tmessages", paramMessageObject);
      this.isPaused = false;
    }
  }
  
  /* Error */
  public boolean playAudio(final MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +5 -> 6
    //   4: iconst_0
    //   5: ireturn
    //   6: aload_0
    //   7: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   10: ifnonnull +10 -> 20
    //   13: aload_0
    //   14: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   17: ifnull +54 -> 71
    //   20: aload_0
    //   21: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   24: ifnull +47 -> 71
    //   27: aload_1
    //   28: invokevirtual 1732	org/telegram/messenger/MessageObject:getId	()I
    //   31: aload_0
    //   32: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   35: invokevirtual 1732	org/telegram/messenger/MessageObject:getId	()I
    //   38: if_icmpne +33 -> 71
    //   41: aload_0
    //   42: getfield 448	org/telegram/messenger/MediaController:isPaused	Z
    //   45: ifeq +9 -> 54
    //   48: aload_0
    //   49: aload_1
    //   50: invokevirtual 2358	org/telegram/messenger/MediaController:resumeAudio	(Lorg/telegram/messenger/MessageObject;)Z
    //   53: pop
    //   54: aload_0
    //   55: getfield 428	org/telegram/messenger/MediaController:raiseToSpeak	Z
    //   58: ifne +11 -> 69
    //   61: aload_0
    //   62: aload_0
    //   63: getfield 2048	org/telegram/messenger/MediaController:raiseChat	Lorg/telegram/ui/ChatActivity;
    //   66: invokevirtual 2361	org/telegram/messenger/MediaController:startRaiseToEarSensors	(Lorg/telegram/ui/ChatActivity;)V
    //   69: iconst_1
    //   70: ireturn
    //   71: aload_1
    //   72: invokevirtual 2197	org/telegram/messenger/MessageObject:isOut	()Z
    //   75: ifne +30 -> 105
    //   78: aload_1
    //   79: invokevirtual 2194	org/telegram/messenger/MessageObject:isContentUnread	()Z
    //   82: ifeq +23 -> 105
    //   85: aload_1
    //   86: getfield 1052	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   89: getfield 2167	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   92: getfield 2172	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   95: ifne +10 -> 105
    //   98: invokestatic 2152	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   101: aload_1
    //   102: invokevirtual 2364	org/telegram/messenger/MessagesController:markMessageContentAsRead	(Lorg/telegram/messenger/MessageObject;)V
    //   105: aload_0
    //   106: getfield 1748	org/telegram/messenger/MediaController:playMusicAgain	Z
    //   109: ifne +231 -> 340
    //   112: iconst_1
    //   113: istore 4
    //   115: aload_0
    //   116: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   119: ifnull +6 -> 125
    //   122: iconst_0
    //   123: istore 4
    //   125: aload_0
    //   126: iload 4
    //   128: iconst_0
    //   129: invokevirtual 1702	org/telegram/messenger/MediaController:cleanupPlayer	(ZZ)V
    //   132: aload_0
    //   133: iconst_0
    //   134: putfield 1748	org/telegram/messenger/MediaController:playMusicAgain	Z
    //   137: aconst_null
    //   138: astore 6
    //   140: aload 6
    //   142: astore 5
    //   144: aload_1
    //   145: getfield 1052	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   148: getfield 1057	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   151: ifnull +51 -> 202
    //   154: aload 6
    //   156: astore 5
    //   158: aload_1
    //   159: getfield 1052	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   162: getfield 1057	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   165: invokevirtual 1060	java/lang/String:length	()I
    //   168: ifle +34 -> 202
    //   171: new 1062	java/io/File
    //   174: dup
    //   175: aload_1
    //   176: getfield 1052	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   179: getfield 1057	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   182: invokespecial 1063	java/io/File:<init>	(Ljava/lang/String;)V
    //   185: astore 6
    //   187: aload 6
    //   189: astore 5
    //   191: aload 6
    //   193: invokevirtual 1066	java/io/File:exists	()Z
    //   196: ifne +6 -> 202
    //   199: aconst_null
    //   200: astore 5
    //   202: aload 5
    //   204: ifnull +142 -> 346
    //   207: aload 5
    //   209: astore 6
    //   211: aload 6
    //   213: ifnull +170 -> 383
    //   216: aload 6
    //   218: aload 5
    //   220: if_acmpeq +163 -> 383
    //   223: aload 6
    //   225: invokevirtual 1066	java/io/File:exists	()Z
    //   228: ifne +155 -> 383
    //   231: aload_1
    //   232: invokevirtual 1069	org/telegram/messenger/MessageObject:isMusic	()Z
    //   235: ifeq +148 -> 383
    //   238: invokestatic 1074	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   241: aload_1
    //   242: invokevirtual 1078	org/telegram/messenger/MessageObject:getDocument	()Lorg/telegram/tgnet/TLRPC$Document;
    //   245: iconst_0
    //   246: iconst_0
    //   247: invokevirtual 1082	org/telegram/messenger/FileLoader:loadFile	(Lorg/telegram/tgnet/TLRPC$Document;ZZ)V
    //   250: aload_0
    //   251: iconst_1
    //   252: putfield 2036	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   255: aload_0
    //   256: iconst_0
    //   257: putfield 448	org/telegram/messenger/MediaController:isPaused	Z
    //   260: aload_0
    //   261: iconst_0
    //   262: putfield 454	org/telegram/messenger/MediaController:lastProgress	I
    //   265: aload_0
    //   266: lconst_0
    //   267: putfield 797	org/telegram/messenger/MediaController:lastPlayPcm	J
    //   270: aload_0
    //   271: aconst_null
    //   272: putfield 2025	org/telegram/messenger/MediaController:audioInfo	Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   275: aload_0
    //   276: aload_1
    //   277: putfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   280: aload_0
    //   281: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   284: invokevirtual 1078	org/telegram/messenger/MessageObject:getDocument	()Lorg/telegram/tgnet/TLRPC$Document;
    //   287: ifnull +71 -> 358
    //   290: new 1911	android/content/Intent
    //   293: dup
    //   294: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   297: ldc_w 2042
    //   300: invokespecial 1916	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   303: astore_1
    //   304: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   307: aload_1
    //   308: invokevirtual 1948	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   311: pop
    //   312: invokestatic 1726	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   315: getstatic 1739	org/telegram/messenger/NotificationCenter:audioPlayStateChanged	I
    //   318: iconst_1
    //   319: anewarray 4	java/lang/Object
    //   322: dup
    //   323: iconst_0
    //   324: aload_0
    //   325: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   328: invokevirtual 1732	org/telegram/messenger/MessageObject:getId	()I
    //   331: invokestatic 1568	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   334: aastore
    //   335: invokevirtual 1736	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   338: iconst_1
    //   339: ireturn
    //   340: iconst_0
    //   341: istore 4
    //   343: goto -228 -> 115
    //   346: aload_1
    //   347: getfield 1052	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   350: invokestatic 1086	org/telegram/messenger/FileLoader:getPathToMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Ljava/io/File;
    //   353: astore 6
    //   355: goto -144 -> 211
    //   358: new 1911	android/content/Intent
    //   361: dup
    //   362: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   365: ldc_w 2042
    //   368: invokespecial 1916	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   371: astore_1
    //   372: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   375: aload_1
    //   376: invokevirtual 2046	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   379: pop
    //   380: goto -68 -> 312
    //   383: aload_0
    //   384: iconst_0
    //   385: putfield 2036	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   388: aload_1
    //   389: invokevirtual 1069	org/telegram/messenger/MessageObject:isMusic	()Z
    //   392: ifeq +7 -> 399
    //   395: aload_0
    //   396: invokespecial 2366	org/telegram/messenger/MediaController:checkIsNextMusicFileDownloaded	()V
    //   399: aload_0
    //   400: aload 6
    //   402: invokevirtual 1604	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   405: invokespecial 2368	org/telegram/messenger/MediaController:isOpusFile	(Ljava/lang/String;)I
    //   408: iconst_1
    //   409: if_icmpne +372 -> 781
    //   412: aload_0
    //   413: getfield 466	org/telegram/messenger/MediaController:playlist	Ljava/util/ArrayList;
    //   416: invokevirtual 952	java/util/ArrayList:clear	()V
    //   419: aload_0
    //   420: getfield 468	org/telegram/messenger/MediaController:shuffledPlaylist	Ljava/util/ArrayList;
    //   423: invokevirtual 952	java/util/ArrayList:clear	()V
    //   426: aload_0
    //   427: getfield 482	org/telegram/messenger/MediaController:playerObjectSync	Ljava/lang/Object;
    //   430: astore 5
    //   432: aload 5
    //   434: monitorenter
    //   435: aload_0
    //   436: iconst_3
    //   437: putfield 460	org/telegram/messenger/MediaController:ignoreFirstProgress	I
    //   440: new 2370	java/util/concurrent/Semaphore
    //   443: dup
    //   444: iconst_0
    //   445: invokespecial 2371	java/util/concurrent/Semaphore:<init>	(I)V
    //   448: astore 7
    //   450: iconst_1
    //   451: anewarray 1884	java/lang/Boolean
    //   454: astore 8
    //   456: aload_0
    //   457: getfield 626	org/telegram/messenger/MediaController:fileDecodingQueue	Lorg/telegram/messenger/DispatchQueue;
    //   460: new 28	org/telegram/messenger/MediaController$13
    //   463: dup
    //   464: aload_0
    //   465: aload 8
    //   467: aload 6
    //   469: aload 7
    //   471: invokespecial 2374	org/telegram/messenger/MediaController$13:<init>	(Lorg/telegram/messenger/MediaController;[Ljava/lang/Boolean;Ljava/io/File;Ljava/util/concurrent/Semaphore;)V
    //   474: invokevirtual 1015	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   477: aload 7
    //   479: invokevirtual 2375	java/util/concurrent/Semaphore:acquire	()V
    //   482: aload 8
    //   484: iconst_0
    //   485: aaload
    //   486: invokevirtual 2111	java/lang/Boolean:booleanValue	()Z
    //   489: istore 4
    //   491: iload 4
    //   493: ifne +14 -> 507
    //   496: aload 5
    //   498: monitorexit
    //   499: iconst_0
    //   500: ireturn
    //   501: astore_1
    //   502: aload 5
    //   504: monitorexit
    //   505: aload_1
    //   506: athrow
    //   507: aload_0
    //   508: aload_0
    //   509: invokespecial 2377	org/telegram/messenger/MediaController:getTotalPcmDuration	()J
    //   512: putfield 801	org/telegram/messenger/MediaController:currentTotalPcmDuration	J
    //   515: aload_0
    //   516: getfield 986	org/telegram/messenger/MediaController:useFrontSpeaker	Z
    //   519: ifeq +210 -> 729
    //   522: iconst_0
    //   523: istore_2
    //   524: aload_0
    //   525: new 524	android/media/AudioTrack
    //   528: dup
    //   529: iload_2
    //   530: ldc_w 522
    //   533: iconst_4
    //   534: iconst_2
    //   535: aload_0
    //   536: getfield 456	org/telegram/messenger/MediaController:playerBufferSize	I
    //   539: iconst_1
    //   540: invokespecial 2380	android/media/AudioTrack:<init>	(IIIIII)V
    //   543: putfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   546: aload_0
    //   547: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   550: fconst_1
    //   551: fconst_1
    //   552: invokevirtual 1878	android/media/AudioTrack:setStereoVolume	(FF)I
    //   555: pop
    //   556: aload_0
    //   557: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   560: new 30	org/telegram/messenger/MediaController$14
    //   563: dup
    //   564: aload_0
    //   565: invokespecial 2381	org/telegram/messenger/MediaController$14:<init>	(Lorg/telegram/messenger/MediaController;)V
    //   568: invokevirtual 2385	android/media/AudioTrack:setPlaybackPositionUpdateListener	(Landroid/media/AudioTrack$OnPlaybackPositionUpdateListener;)V
    //   571: aload_0
    //   572: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   575: invokevirtual 2388	android/media/AudioTrack:play	()V
    //   578: aload 5
    //   580: monitorexit
    //   581: aload_0
    //   582: aload_1
    //   583: invokespecial 2390	org/telegram/messenger/MediaController:checkAudioFocus	(Lorg/telegram/messenger/MessageObject;)V
    //   586: aload_0
    //   587: invokespecial 2239	org/telegram/messenger/MediaController:setPlayerVolume	()V
    //   590: aload_0
    //   591: iconst_0
    //   592: putfield 448	org/telegram/messenger/MediaController:isPaused	Z
    //   595: aload_0
    //   596: iconst_0
    //   597: putfield 454	org/telegram/messenger/MediaController:lastProgress	I
    //   600: aload_0
    //   601: lconst_0
    //   602: putfield 797	org/telegram/messenger/MediaController:lastPlayPcm	J
    //   605: aload_0
    //   606: aload_1
    //   607: putfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   610: aload_0
    //   611: getfield 428	org/telegram/messenger/MediaController:raiseToSpeak	Z
    //   614: ifne +11 -> 625
    //   617: aload_0
    //   618: aload_0
    //   619: getfield 2048	org/telegram/messenger/MediaController:raiseChat	Lorg/telegram/ui/ChatActivity;
    //   622: invokevirtual 2361	org/telegram/messenger/MediaController:startRaiseToEarSensors	(Lorg/telegram/ui/ChatActivity;)V
    //   625: aload_0
    //   626: aload_0
    //   627: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   630: invokespecial 2392	org/telegram/messenger/MediaController:startProgressTimer	(Lorg/telegram/messenger/MessageObject;)V
    //   633: invokestatic 1726	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   636: getstatic 2395	org/telegram/messenger/NotificationCenter:audioDidStarted	I
    //   639: iconst_1
    //   640: anewarray 4	java/lang/Object
    //   643: dup
    //   644: iconst_0
    //   645: aload_1
    //   646: aastore
    //   647: invokevirtual 1736	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   650: aload_0
    //   651: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   654: ifnull +408 -> 1062
    //   657: aload_0
    //   658: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   661: getfield 1718	org/telegram/messenger/MessageObject:audioProgress	F
    //   664: fconst_0
    //   665: fcmpl
    //   666: ifeq +29 -> 695
    //   669: aload_0
    //   670: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   673: invokevirtual 2398	android/media/MediaPlayer:getDuration	()I
    //   676: i2f
    //   677: aload_0
    //   678: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   681: getfield 1718	org/telegram/messenger/MessageObject:audioProgress	F
    //   684: fmul
    //   685: f2i
    //   686: istore_2
    //   687: aload_0
    //   688: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   691: iload_2
    //   692: invokevirtual 2400	android/media/MediaPlayer:seekTo	(I)V
    //   695: aload_0
    //   696: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   699: invokevirtual 1069	org/telegram/messenger/MessageObject:isMusic	()Z
    //   702: ifeq +405 -> 1107
    //   705: new 1911	android/content/Intent
    //   708: dup
    //   709: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   712: ldc_w 2042
    //   715: invokespecial 1916	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   718: astore_1
    //   719: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   722: aload_1
    //   723: invokevirtual 1948	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   726: pop
    //   727: iconst_1
    //   728: ireturn
    //   729: iconst_3
    //   730: istore_2
    //   731: goto -207 -> 524
    //   734: astore_1
    //   735: ldc_w 550
    //   738: aload_1
    //   739: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   742: aload_0
    //   743: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   746: ifnull +30 -> 776
    //   749: aload_0
    //   750: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   753: invokevirtual 1746	android/media/AudioTrack:release	()V
    //   756: aload_0
    //   757: aconst_null
    //   758: putfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   761: aload_0
    //   762: iconst_0
    //   763: putfield 448	org/telegram/messenger/MediaController:isPaused	Z
    //   766: aload_0
    //   767: aconst_null
    //   768: putfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   771: aload_0
    //   772: iconst_0
    //   773: putfield 2036	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   776: aload 5
    //   778: monitorexit
    //   779: iconst_0
    //   780: ireturn
    //   781: aload_0
    //   782: new 1707	android/media/MediaPlayer
    //   785: dup
    //   786: invokespecial 2401	android/media/MediaPlayer:<init>	()V
    //   789: putfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   792: aload_0
    //   793: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   796: astore 5
    //   798: aload_0
    //   799: getfield 986	org/telegram/messenger/MediaController:useFrontSpeaker	Z
    //   802: ifeq +165 -> 967
    //   805: iconst_0
    //   806: istore_2
    //   807: aload 5
    //   809: iload_2
    //   810: invokevirtual 2404	android/media/MediaPlayer:setAudioStreamType	(I)V
    //   813: aload_0
    //   814: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   817: aload 6
    //   819: invokevirtual 1604	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   822: invokevirtual 2405	android/media/MediaPlayer:setDataSource	(Ljava/lang/String;)V
    //   825: aload_0
    //   826: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   829: new 32	org/telegram/messenger/MediaController$15
    //   832: dup
    //   833: aload_0
    //   834: aload_1
    //   835: invokespecial 2406	org/telegram/messenger/MediaController$15:<init>	(Lorg/telegram/messenger/MediaController;Lorg/telegram/messenger/MessageObject;)V
    //   838: invokevirtual 2410	android/media/MediaPlayer:setOnCompletionListener	(Landroid/media/MediaPlayer$OnCompletionListener;)V
    //   841: aload_0
    //   842: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   845: invokevirtual 2413	android/media/MediaPlayer:prepare	()V
    //   848: aload_0
    //   849: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   852: invokevirtual 2414	android/media/MediaPlayer:start	()V
    //   855: aload_1
    //   856: invokevirtual 984	org/telegram/messenger/MessageObject:isVoice	()Z
    //   859: ifeq +113 -> 972
    //   862: aload_0
    //   863: aconst_null
    //   864: putfield 2025	org/telegram/messenger/MediaController:audioInfo	Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   867: aload_0
    //   868: getfield 466	org/telegram/messenger/MediaController:playlist	Ljava/util/ArrayList;
    //   871: invokevirtual 952	java/util/ArrayList:clear	()V
    //   874: aload_0
    //   875: getfield 468	org/telegram/messenger/MediaController:shuffledPlaylist	Ljava/util/ArrayList;
    //   878: invokevirtual 952	java/util/ArrayList:clear	()V
    //   881: goto -300 -> 581
    //   884: astore_1
    //   885: ldc_w 550
    //   888: aload_1
    //   889: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   892: invokestatic 1726	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   895: astore_1
    //   896: getstatic 1739	org/telegram/messenger/NotificationCenter:audioPlayStateChanged	I
    //   899: istore_3
    //   900: aload_0
    //   901: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   904: ifnull +93 -> 997
    //   907: aload_0
    //   908: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   911: invokevirtual 1732	org/telegram/messenger/MessageObject:getId	()I
    //   914: istore_2
    //   915: aload_1
    //   916: iload_3
    //   917: iconst_1
    //   918: anewarray 4	java/lang/Object
    //   921: dup
    //   922: iconst_0
    //   923: iload_2
    //   924: invokestatic 1568	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   927: aastore
    //   928: invokevirtual 1736	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   931: aload_0
    //   932: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   935: ifnull +30 -> 965
    //   938: aload_0
    //   939: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   942: invokevirtual 1712	android/media/MediaPlayer:release	()V
    //   945: aload_0
    //   946: aconst_null
    //   947: putfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   950: aload_0
    //   951: iconst_0
    //   952: putfield 448	org/telegram/messenger/MediaController:isPaused	Z
    //   955: aload_0
    //   956: aconst_null
    //   957: putfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   960: aload_0
    //   961: iconst_0
    //   962: putfield 2036	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   965: iconst_0
    //   966: ireturn
    //   967: iconst_3
    //   968: istore_2
    //   969: goto -162 -> 807
    //   972: aload_0
    //   973: aload 6
    //   975: invokestatic 2419	org/telegram/messenger/audioinfo/AudioInfo:getAudioInfo	(Ljava/io/File;)Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   978: putfield 2025	org/telegram/messenger/MediaController:audioInfo	Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   981: goto -400 -> 581
    //   984: astore 5
    //   986: ldc_w 550
    //   989: aload 5
    //   991: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   994: goto -413 -> 581
    //   997: iconst_0
    //   998: istore_2
    //   999: goto -84 -> 915
    //   1002: astore_1
    //   1003: aload_0
    //   1004: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1007: fconst_0
    //   1008: putfield 1718	org/telegram/messenger/MessageObject:audioProgress	F
    //   1011: aload_0
    //   1012: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1015: iconst_0
    //   1016: putfield 1721	org/telegram/messenger/MessageObject:audioProgressSec	I
    //   1019: invokestatic 1726	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   1022: getstatic 1729	org/telegram/messenger/NotificationCenter:audioProgressDidChanged	I
    //   1025: iconst_2
    //   1026: anewarray 4	java/lang/Object
    //   1029: dup
    //   1030: iconst_0
    //   1031: aload_0
    //   1032: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1035: invokevirtual 1732	org/telegram/messenger/MessageObject:getId	()I
    //   1038: invokestatic 1568	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1041: aastore
    //   1042: dup
    //   1043: iconst_1
    //   1044: iconst_0
    //   1045: invokestatic 1568	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1048: aastore
    //   1049: invokevirtual 1736	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   1052: ldc_w 550
    //   1055: aload_1
    //   1056: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1059: goto -364 -> 695
    //   1062: aload_0
    //   1063: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1066: ifnull -371 -> 695
    //   1069: aload_0
    //   1070: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1073: getfield 1718	org/telegram/messenger/MessageObject:audioProgress	F
    //   1076: fconst_1
    //   1077: fcmpl
    //   1078: ifne +11 -> 1089
    //   1081: aload_0
    //   1082: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1085: fconst_0
    //   1086: putfield 1718	org/telegram/messenger/MessageObject:audioProgress	F
    //   1089: aload_0
    //   1090: getfield 626	org/telegram/messenger/MediaController:fileDecodingQueue	Lorg/telegram/messenger/DispatchQueue;
    //   1093: new 34	org/telegram/messenger/MediaController$16
    //   1096: dup
    //   1097: aload_0
    //   1098: invokespecial 2420	org/telegram/messenger/MediaController$16:<init>	(Lorg/telegram/messenger/MediaController;)V
    //   1101: invokevirtual 1015	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   1104: goto -409 -> 695
    //   1107: new 1911	android/content/Intent
    //   1110: dup
    //   1111: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   1114: ldc_w 2042
    //   1117: invokespecial 1916	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   1120: astore_1
    //   1121: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   1124: aload_1
    //   1125: invokevirtual 2046	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   1128: pop
    //   1129: goto -402 -> 727
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1132	0	this	MediaController
    //   0	1132	1	paramMessageObject	MessageObject
    //   523	476	2	i	int
    //   899	18	3	j	int
    //   113	379	4	bool	boolean
    //   984	6	5	localException	Exception
    //   138	836	6	localObject2	Object
    //   448	30	7	localSemaphore	Semaphore
    //   454	29	8	arrayOfBoolean	Boolean[]
    // Exception table:
    //   from	to	target	type
    //   435	491	501	finally
    //   496	499	501	finally
    //   502	505	501	finally
    //   507	522	501	finally
    //   524	578	501	finally
    //   578	581	501	finally
    //   735	776	501	finally
    //   776	779	501	finally
    //   435	491	734	java/lang/Exception
    //   507	522	734	java/lang/Exception
    //   524	578	734	java/lang/Exception
    //   781	805	884	java/lang/Exception
    //   807	881	884	java/lang/Exception
    //   986	994	884	java/lang/Exception
    //   972	981	984	java/lang/Exception
    //   657	695	1002	java/lang/Exception
  }
  
  public void playMessageAtIndex(int paramInt)
  {
    if ((this.currentPlaylistNum < 0) || (this.currentPlaylistNum >= this.playlist.size())) {
      return;
    }
    this.currentPlaylistNum = paramInt;
    this.playMusicAgain = true;
    playAudio((MessageObject)this.playlist.get(this.currentPlaylistNum));
  }
  
  public void playNextMessage()
  {
    playNextMessage(false);
  }
  
  public void playPreviousMessage()
  {
    if (this.shuffleMusic) {}
    for (ArrayList localArrayList = this.shuffledPlaylist;; localArrayList = this.playlist)
    {
      this.currentPlaylistNum -= 1;
      if (this.currentPlaylistNum < 0) {
        this.currentPlaylistNum = (localArrayList.size() - 1);
      }
      if ((this.currentPlaylistNum >= 0) && (this.currentPlaylistNum < localArrayList.size())) {
        break;
      }
      return;
    }
    this.playMusicAgain = true;
    playAudio((MessageObject)localArrayList.get(this.currentPlaylistNum));
  }
  
  protected void processDownloadObjects(int paramInt, ArrayList<DownloadObject> paramArrayList)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    ArrayList localArrayList = null;
    label22:
    label24:
    DownloadObject localDownloadObject;
    String str;
    if (paramInt == 1)
    {
      localArrayList = this.photoDownloadQueue;
      paramInt = 0;
      if (paramInt < paramArrayList.size())
      {
        localDownloadObject = (DownloadObject)paramArrayList.get(paramInt);
        if (!(localDownloadObject.object instanceof TLRPC.Document)) {
          break label158;
        }
        str = FileLoader.getAttachFileName((TLRPC.Document)localDownloadObject.object);
        label66:
        if (!this.downloadQueueKeys.containsKey(str)) {
          break label171;
        }
      }
    }
    label158:
    label171:
    label264:
    for (;;)
    {
      paramInt += 1;
      break label24;
      break;
      if (paramInt == 2)
      {
        localArrayList = this.audioDownloadQueue;
        break label22;
      }
      if (paramInt == 4)
      {
        localArrayList = this.videoDownloadQueue;
        break label22;
      }
      if (paramInt == 8)
      {
        localArrayList = this.documentDownloadQueue;
        break label22;
      }
      if (paramInt == 16)
      {
        localArrayList = this.musicDownloadQueue;
        break label22;
      }
      if (paramInt != 32) {
        break label22;
      }
      localArrayList = this.gifDownloadQueue;
      break label22;
      str = FileLoader.getAttachFileName(localDownloadObject.object);
      break label66;
      int i = 1;
      if ((localDownloadObject.object instanceof TLRPC.PhotoSize)) {
        FileLoader.getInstance().loadFile((TLRPC.PhotoSize)localDownloadObject.object, null, false);
      }
      for (;;)
      {
        if (i == 0) {
          break label264;
        }
        localArrayList.add(localDownloadObject);
        this.downloadQueueKeys.put(str, localDownloadObject);
        break;
        if ((localDownloadObject.object instanceof TLRPC.Document))
        {
          TLRPC.Document localDocument = (TLRPC.Document)localDownloadObject.object;
          FileLoader.getInstance().loadFile(localDocument, false, false);
        }
        else
        {
          i = 0;
        }
      }
    }
  }
  
  public void processMediaObserver(Uri paramUri)
  {
    final ArrayList localArrayList;
    label248:
    do
    {
      try
      {
        Point localPoint = AndroidUtilities.getRealScreenSize();
        paramUri = ApplicationLoader.applicationContext.getContentResolver().query(paramUri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
        localArrayList = new ArrayList();
        if (paramUri != null)
        {
          while (paramUri.moveToNext())
          {
            String str1 = paramUri.getString(0);
            Object localObject = paramUri.getString(1);
            String str2 = paramUri.getString(2);
            long l = paramUri.getLong(3);
            String str3 = paramUri.getString(4);
            int j = 0;
            int i = 0;
            if (Build.VERSION.SDK_INT >= 16)
            {
              j = paramUri.getInt(5);
              i = paramUri.getInt(6);
            }
            if (((str1 == null) || (!str1.toLowerCase().contains("screenshot"))) && ((localObject == null) || (!((String)localObject).toLowerCase().contains("screenshot"))) && ((str2 == null) || (!str2.toLowerCase().contains("screenshot"))))
            {
              if (str3 != null)
              {
                boolean bool = str3.toLowerCase().contains("screenshot");
                if (!bool) {}
              }
            }
            else
            {
              int k;
              if (j != 0)
              {
                k = i;
                if (i != 0) {
                  break label248;
                }
              }
              try
              {
                localObject = new BitmapFactory.Options();
                ((BitmapFactory.Options)localObject).inJustDecodeBounds = true;
                BitmapFactory.decodeFile(str1, (BitmapFactory.Options)localObject);
                j = ((BitmapFactory.Options)localObject).outWidth;
                k = ((BitmapFactory.Options)localObject).outHeight;
                if ((j <= 0) || (k <= 0) || ((j == localPoint.x) && (k == localPoint.y)) || ((k == localPoint.x) && (j == localPoint.y))) {
                  localArrayList.add(Long.valueOf(l));
                }
              }
              catch (Exception localException)
              {
                localArrayList.add(Long.valueOf(l));
              }
            }
          }
          paramUri.close();
        }
      }
      catch (Exception paramUri)
      {
        FileLog.e("tmessages", paramUri);
        return;
      }
    } while (localArrayList.isEmpty());
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
        MediaController.this.checkScreenshots(localArrayList);
      }
    });
  }
  
  public void removeLoadingFileObserver(FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    if (this.listenerInProgress) {
      this.deleteLaterArray.add(paramFileDownloadProgressListener);
    }
    String str;
    do
    {
      return;
      str = (String)this.observersByTag.get(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()));
    } while (str == null);
    ArrayList localArrayList = (ArrayList)this.loadingFileObservers.get(str);
    if (localArrayList != null)
    {
      int j;
      for (int i = 0; i < localArrayList.size(); i = j + 1)
      {
        WeakReference localWeakReference = (WeakReference)localArrayList.get(i);
        if (localWeakReference.get() != null)
        {
          j = i;
          if (localWeakReference.get() != paramFileDownloadProgressListener) {}
        }
        else
        {
          localArrayList.remove(i);
          j = i - 1;
        }
      }
      if (localArrayList.isEmpty()) {
        this.loadingFileObservers.remove(str);
      }
    }
    this.observersByTag.remove(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()));
  }
  
  public boolean resumeAudio(MessageObject paramMessageObject)
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null)) || (paramMessageObject == null) || (this.playingMessageObject == null) || ((this.playingMessageObject != null) && (this.playingMessageObject.getId() != paramMessageObject.getId()))) {
      return false;
    }
    try
    {
      startProgressTimer(paramMessageObject);
      if (this.audioPlayer != null) {
        this.audioPlayer.start();
      }
      for (;;)
      {
        checkAudioFocus(paramMessageObject);
        this.isPaused = false;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
        return true;
        if (this.audioTrackPlayer != null)
        {
          this.audioTrackPlayer.play();
          checkPlayerQueue();
        }
      }
      return false;
    }
    catch (Exception paramMessageObject)
    {
      FileLog.e("tmessages", paramMessageObject);
    }
  }
  
  public void scheduleVideoConvert(MessageObject paramMessageObject)
  {
    this.videoConvertQueue.add(paramMessageObject);
    if (this.videoConvertQueue.size() == 1) {
      startVideoConvertFromQueue();
    }
  }
  
  public boolean seekToProgress(MessageObject paramMessageObject, float paramFloat)
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null)) || (paramMessageObject == null) || (this.playingMessageObject == null) || ((this.playingMessageObject != null) && (this.playingMessageObject.getId() != paramMessageObject.getId()))) {
      return false;
    }
    try
    {
      if (this.audioPlayer != null)
      {
        int i = (int)(this.audioPlayer.getDuration() * paramFloat);
        this.audioPlayer.seekTo(i);
        this.lastProgress = i;
      }
      else if (this.audioTrackPlayer != null)
      {
        seekOpusPlayer(paramFloat);
      }
    }
    catch (Exception paramMessageObject)
    {
      FileLog.e("tmessages", paramMessageObject);
      return false;
    }
    return true;
  }
  
  public void setAllowStartRecord(boolean paramBoolean)
  {
    this.allowStartRecord = paramBoolean;
  }
  
  public void setInputFieldHasText(boolean paramBoolean)
  {
    this.inputFieldHasText = paramBoolean;
  }
  
  public void setLastEncryptedChatParams(long paramLong1, long paramLong2, TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList)
  {
    this.lastSecretChatEnterTime = paramLong1;
    this.lastSecretChatLeaveTime = paramLong2;
    this.lastSecretChat = paramEncryptedChat;
    this.lastSecretChatVisibleMessages = paramArrayList;
  }
  
  public boolean setPlaylist(ArrayList<MessageObject> paramArrayList, MessageObject paramMessageObject)
  {
    return setPlaylist(paramArrayList, paramMessageObject, true);
  }
  
  public boolean setPlaylist(ArrayList<MessageObject> paramArrayList, MessageObject paramMessageObject, boolean paramBoolean)
  {
    boolean bool2 = true;
    if (this.playingMessageObject == paramMessageObject) {
      return playAudio(paramMessageObject);
    }
    if (!paramBoolean)
    {
      bool1 = true;
      this.forceLoopCurrentPlaylist = bool1;
      if (this.playlist.isEmpty()) {
        break label114;
      }
    }
    label114:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.playMusicAgain = bool1;
      this.playlist.clear();
      int i = paramArrayList.size() - 1;
      while (i >= 0)
      {
        MessageObject localMessageObject = (MessageObject)paramArrayList.get(i);
        if (localMessageObject.isMusic()) {
          this.playlist.add(localMessageObject);
        }
        i -= 1;
      }
      bool1 = false;
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
      if (this.shuffleMusic)
      {
        buildShuffledPlayList();
        this.currentPlaylistNum = 0;
      }
      if (paramBoolean) {
        SharedMediaQuery.loadMusic(paramMessageObject.getDialogId(), ((MessageObject)this.playlist.get(0)).getId());
      }
    }
    return playAudio(paramMessageObject);
  }
  
  public void setVoiceMessagesPlaylist(ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    this.voiceMessagesPlaylist = paramArrayList;
    if (this.voiceMessagesPlaylist != null)
    {
      this.voiceMessagesPlaylistUnread = paramBoolean;
      this.voiceMessagesPlaylistMap = new HashMap();
      int i = 0;
      while (i < this.voiceMessagesPlaylist.size())
      {
        paramArrayList = (MessageObject)this.voiceMessagesPlaylist.get(i);
        this.voiceMessagesPlaylistMap.put(Integer.valueOf(paramArrayList.getId()), paramArrayList);
        i += 1;
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
        localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        localObject = new ExternalObserver();
        this.externalObserver = ((ExternalObserver)localObject);
        localContentResolver.registerContentObserver(localUri, false, (ContentObserver)localObject);
      }
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        try
        {
          ContentResolver localContentResolver;
          Uri localUri;
          Object localObject;
          if (this.externalObserver == null)
          {
            localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
            localUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            localObject = new InternalObserver();
            this.internalObserver = ((InternalObserver)localObject);
            localContentResolver.registerContentObserver(localUri, false, (ContentObserver)localObject);
          }
          return;
        }
        catch (Exception localException2)
        {
          FileLog.e("tmessages", localException2);
        }
        localException1 = localException1;
        FileLog.e("tmessages", localException1);
      }
    }
  }
  
  public void startRaiseToEarSensors(ChatActivity paramChatActivity)
  {
    if ((paramChatActivity == null) || ((this.accelerometerSensor == null) && ((this.gravitySensor == null) || (this.linearAcceleration == null))) || (this.proximitySensor == null)) {}
    do
    {
      return;
      this.raiseChat = paramChatActivity;
    } while (((!this.raiseToSpeak) && ((this.playingMessageObject == null) || (!this.playingMessageObject.isVoice()))) || (this.sensorsStarted));
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
  
  public void startRecording(final long paramLong, MessageObject paramMessageObject)
  {
    long l = 50L;
    int j = 0;
    int i = j;
    if (this.playingMessageObject != null)
    {
      i = j;
      if (isPlayingAudio(this.playingMessageObject))
      {
        i = j;
        if (!isAudioPaused())
        {
          i = 1;
          pauseAudio(this.playingMessageObject);
        }
      }
    }
    try
    {
      ((Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50L);
      DispatchQueue localDispatchQueue = this.recordQueue;
      paramMessageObject = new Runnable()
      {
        public void run()
        {
          if (MediaController.this.audioRecorder != null)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$1902(MediaController.this, null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
              }
            });
            return;
          }
          MediaController.access$2002(MediaController.this, new TLRPC.TL_document());
          MediaController.this.recordingAudio.dc_id = Integer.MIN_VALUE;
          MediaController.this.recordingAudio.id = UserConfig.lastLocalId;
          MediaController.this.recordingAudio.user_id = UserConfig.getClientUserId();
          MediaController.this.recordingAudio.mime_type = "audio/ogg";
          MediaController.this.recordingAudio.thumb = new TLRPC.TL_photoSizeEmpty();
          MediaController.this.recordingAudio.thumb.type = "s";
          UserConfig.lastLocalId -= 1;
          UserConfig.saveConfig(false);
          MediaController.access$5202(MediaController.this, new File(FileLoader.getInstance().getDirectory(4), FileLoader.getAttachFileName(MediaController.this.recordingAudio)));
          try
          {
            if (MediaController.this.startRecord(MediaController.this.recordingAudioFile.getAbsolutePath()) == 0)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MediaController.access$1902(MediaController.this, null);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
              });
              return;
            }
          }
          catch (Exception localException1)
          {
            FileLog.e("tmessages", localException1);
            MediaController.access$2002(MediaController.this, null);
            MediaController.this.stopRecord();
            MediaController.this.recordingAudioFile.delete();
            MediaController.access$5202(MediaController.this, null);
          }
          try
          {
            MediaController.this.audioRecorder.release();
            MediaController.access$002(MediaController.this, null);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$1902(MediaController.this, null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
              }
            });
            return;
            MediaController.access$002(MediaController.this, new AudioRecord(1, 16000, 16, 2, MediaController.this.recordBufferSize * 10));
            MediaController.access$1102(MediaController.this, System.currentTimeMillis());
            MediaController.access$702(MediaController.this, 0L);
            MediaController.access$302(MediaController.this, 0L);
            MediaController.access$5402(MediaController.this, paramLong);
            MediaController.access$5502(MediaController.this, this.val$reply_to_msg);
            MediaController.this.fileBuffer.rewind();
            MediaController.this.audioRecorder.startRecording();
            MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$1902(MediaController.this, null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[0]);
              }
            });
            return;
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException2);
            }
          }
        }
      };
      this.recordStartRunnable = paramMessageObject;
      paramLong = l;
      if (i != 0) {
        paramLong = 500L;
      }
      localDispatchQueue.postRunnable(paramMessageObject, paramLong);
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
  
  public void startRecordingIfFromSpeaker()
  {
    if ((!this.useFrontSpeaker) || (this.raiseChat == null) || (!this.allowStartRecord)) {
      return;
    }
    this.raiseToEarRecord = true;
    startRecording(this.raiseChat.getDialogId(), null);
    this.ignoreOnPause = true;
  }
  
  /* Error */
  public void stopAudio()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   4: ifnonnull +10 -> 14
    //   7: aload_0
    //   8: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   11: ifnull +10 -> 21
    //   14: aload_0
    //   15: getfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   18: ifnonnull +4 -> 22
    //   21: return
    //   22: aload_0
    //   23: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   26: astore_1
    //   27: aload_1
    //   28: ifnull +100 -> 128
    //   31: aload_0
    //   32: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   35: invokevirtual 1710	android/media/MediaPlayer:reset	()V
    //   38: aload_0
    //   39: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   42: invokevirtual 1711	android/media/MediaPlayer:stop	()V
    //   45: aload_0
    //   46: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   49: ifnull +103 -> 152
    //   52: aload_0
    //   53: getfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   56: invokevirtual 1712	android/media/MediaPlayer:release	()V
    //   59: aload_0
    //   60: aconst_null
    //   61: putfield 450	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   64: aload_0
    //   65: invokespecial 1715	org/telegram/messenger/MediaController:stopProgressTimer	()V
    //   68: aload_0
    //   69: aconst_null
    //   70: putfield 870	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   73: aload_0
    //   74: iconst_0
    //   75: putfield 2036	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   78: aload_0
    //   79: iconst_0
    //   80: putfield 448	org/telegram/messenger/MediaController:isPaused	Z
    //   83: new 1911	android/content/Intent
    //   86: dup
    //   87: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   90: ldc_w 2042
    //   93: invokespecial 1916	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   96: astore_1
    //   97: getstatic 562	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   100: aload_1
    //   101: invokevirtual 2046	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   104: pop
    //   105: return
    //   106: astore_1
    //   107: ldc_w 550
    //   110: aload_1
    //   111: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   114: goto -76 -> 38
    //   117: astore_1
    //   118: ldc_w 550
    //   121: aload_1
    //   122: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   125: goto -80 -> 45
    //   128: aload_0
    //   129: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   132: ifnull -87 -> 45
    //   135: aload_0
    //   136: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   139: invokevirtual 1742	android/media/AudioTrack:pause	()V
    //   142: aload_0
    //   143: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   146: invokevirtual 1745	android/media/AudioTrack:flush	()V
    //   149: goto -104 -> 45
    //   152: aload_0
    //   153: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   156: ifnull -92 -> 64
    //   159: aload_0
    //   160: getfield 482	org/telegram/messenger/MediaController:playerObjectSync	Ljava/lang/Object;
    //   163: astore_1
    //   164: aload_1
    //   165: monitorenter
    //   166: aload_0
    //   167: getfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   170: invokevirtual 1746	android/media/AudioTrack:release	()V
    //   173: aload_0
    //   174: aconst_null
    //   175: putfield 452	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   178: aload_1
    //   179: monitorexit
    //   180: goto -116 -> 64
    //   183: astore_2
    //   184: aload_1
    //   185: monitorexit
    //   186: aload_2
    //   187: athrow
    //   188: astore_1
    //   189: ldc_w 550
    //   192: aload_1
    //   193: invokestatic 556	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   196: goto -132 -> 64
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	199	0	this	MediaController
    //   26	75	1	localObject1	Object
    //   106	5	1	localException1	Exception
    //   117	5	1	localException2	Exception
    //   188	5	1	localException3	Exception
    //   183	4	2	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   31	38	106	java/lang/Exception
    //   22	27	117	java/lang/Exception
    //   38	45	117	java/lang/Exception
    //   107	114	117	java/lang/Exception
    //   128	149	117	java/lang/Exception
    //   166	180	183	finally
    //   184	186	183	finally
    //   45	64	188	java/lang/Exception
    //   152	166	188	java/lang/Exception
    //   186	188	188	java/lang/Exception
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
    do
    {
      do
      {
        return;
      } while ((!this.sensorsStarted) || (this.ignoreOnPause) || ((this.accelerometerSensor == null) && ((this.gravitySensor == null) || (this.linearAcceleration == null))) || (this.proximitySensor == null) || (this.raiseChat != paramChatActivity));
      this.raiseChat = null;
      stopRecording(0);
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
    } while ((!this.proximityHasDifferentValues) || (this.proximityWakeLock == null) || (!this.proximityWakeLock.isHeld()));
    this.proximityWakeLock.release();
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
        if (MediaController.this.audioRecorder == null) {
          return;
        }
        try
        {
          MediaController.access$1202(MediaController.this, paramInt);
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
              ((Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50L);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
                }
              });
              return;
              localException1 = localException1;
              FileLog.e("tmessages", localException1);
            } while (MediaController.this.recordingAudioFile == null);
            MediaController.this.recordingAudioFile.delete();
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException2);
            }
          }
        }
      }
    });
  }
  
  public void toggleAutoplayGifs()
  {
    if (!this.autoplayGifs) {}
    for (boolean bool = true;; bool = false)
    {
      this.autoplayGifs = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("autoplay_gif", this.autoplayGifs);
      localEditor.commit();
      return;
    }
  }
  
  public void toggleCustomTabs()
  {
    if (!this.customTabs) {}
    for (boolean bool = true;; bool = false)
    {
      this.customTabs = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("custom_tabs", this.customTabs);
      localEditor.commit();
      return;
    }
  }
  
  public void toggleDirectShare()
  {
    if (!this.directShare) {}
    for (boolean bool = true;; bool = false)
    {
      this.directShare = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("direct_share", this.directShare);
      localEditor.commit();
      return;
    }
  }
  
  public void toggleRepeatMode()
  {
    this.repeatMode += 1;
    if (this.repeatMode > 2) {
      this.repeatMode = 0;
    }
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
    localEditor.putInt("repeatMode", this.repeatMode);
    localEditor.commit();
  }
  
  public void toggleSaveToGallery()
  {
    if (!this.saveToGallery) {}
    for (boolean bool = true;; bool = false)
    {
      this.saveToGallery = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("save_gallery", this.saveToGallery);
      localEditor.commit();
      checkSaveToGalleryFiles();
      return;
    }
  }
  
  public void toggleShuffleMusic()
  {
    boolean bool;
    if (!this.shuffleMusic)
    {
      bool = true;
      this.shuffleMusic = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("shuffleMusic", this.shuffleMusic);
      localEditor.commit();
      if (!this.shuffleMusic) {
        break label73;
      }
      buildShuffledPlayList();
      this.currentPlaylistNum = 0;
    }
    label73:
    do
    {
      do
      {
        return;
        bool = false;
        break;
      } while (this.playingMessageObject == null);
      this.currentPlaylistNum = this.playlist.indexOf(this.playingMessageObject);
    } while (this.currentPlaylistNum != -1);
    this.playlist.clear();
    this.shuffledPlaylist.clear();
    cleanupPlayer(true, true);
  }
  
  public void toogleRaiseToSpeak()
  {
    if (!this.raiseToSpeak) {}
    for (boolean bool = true;; bool = false)
    {
      this.raiseToSpeak = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("raise_to_speak", this.raiseToSpeak);
      localEditor.commit();
      return;
    }
  }
  
  public static class AlbumEntry
  {
    public int bucketId;
    public String bucketName;
    public MediaController.PhotoEntry coverPhoto;
    public boolean isVideo;
    public ArrayList<MediaController.PhotoEntry> photos = new ArrayList();
    public HashMap<Integer, MediaController.PhotoEntry> photosByIds = new HashMap();
    
    public AlbumEntry(int paramInt, String paramString, MediaController.PhotoEntry paramPhotoEntry, boolean paramBoolean)
    {
      this.bucketId = paramInt;
      this.bucketName = paramString;
      this.coverPhoto = paramPhotoEntry;
      this.isVideo = paramBoolean;
    }
    
    public void addPhoto(MediaController.PhotoEntry paramPhotoEntry)
    {
      this.photos.add(paramPhotoEntry);
      this.photosByIds.put(Integer.valueOf(paramPhotoEntry.imageId), paramPhotoEntry);
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
  
  public static abstract interface FileDownloadProgressListener
  {
    public abstract int getObserverTag();
    
    public abstract void onFailedDownload(String paramString);
    
    public abstract void onProgressDownload(String paramString, float paramFloat);
    
    public abstract void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean);
    
    public abstract void onSuccessDownload(String paramString);
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
      if (MediaController.this.refreshGalleryRunnable != null) {
        AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
      }
      AndroidUtilities.runOnUIThread(MediaController.access$1402(MediaController.this, new Runnable()
      {
        public void run()
        {
          MediaController.access$1402(MediaController.this, null);
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
      AndroidUtilities.runOnUIThread(MediaController.access$1402(MediaController.this, new Runnable()
      {
        public void run()
        {
          if (PhotoViewer.getInstance().isVisible())
          {
            MediaController.GalleryObserverInternal.this.scheduleReloadRunnable();
            return;
          }
          MediaController.access$1402(MediaController.this, null);
          MediaController.loadGalleryPhotosAlbums(0);
        }
      }), 2000L);
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      if (MediaController.this.refreshGalleryRunnable != null) {
        AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
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
    public int imageId;
    public String imagePath;
    public boolean isVideo;
    public int orientation;
    public String path;
    public ArrayList<TLRPC.InputDocument> stickers = new ArrayList();
    public String thumbPath;
    
    public PhotoEntry(int paramInt1, int paramInt2, long paramLong, String paramString, int paramInt3, boolean paramBoolean)
    {
      this.bucketId = paramInt1;
      this.imageId = paramInt2;
      this.dateTaken = paramLong;
      this.path = paramString;
      this.orientation = paramInt3;
      this.isVideo = paramBoolean;
    }
  }
  
  public static class SearchImage
  {
    public CharSequence caption;
    public int date;
    public TLRPC.Document document;
    public int height;
    public String id;
    public String imagePath;
    public String imageUrl;
    public String localUrl;
    public int size;
    public ArrayList<TLRPC.InputDocument> stickers = new ArrayList();
    public String thumbPath;
    public String thumbUrl;
    public int type;
    public int width;
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
          MediaController.access$1702(MediaController.this, null);
        }
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          try
          {
            if (MediaController.this.externalObserver != null)
            {
              ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
              MediaController.access$1802(MediaController.this, null);
            }
            return;
          }
          catch (Exception localException2)
          {
            FileLog.e("tmessages", localException2);
          }
          localException1 = localException1;
          FileLog.e("tmessages", localException1);
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
            Thread localThread = new Thread(new MediaController.VideoConvertRunnable(this.val$obj, null), "VideoConvertRunnable");
            localThread.start();
            localThread.join();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
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