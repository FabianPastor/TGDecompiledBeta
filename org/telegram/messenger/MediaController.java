package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
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
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.TextureView;
import android.widget.FrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSink;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.video.InputSurface;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.messenger.video.OutputSurface;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.telegram.ui.PhotoViewer;

public class MediaController implements OnAudioFocusChangeListener, NotificationCenterDelegate, SensorEventListener {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    public static final int AUTODOWNLOAD_MASK_AUDIO = 2;
    public static final int AUTODOWNLOAD_MASK_DOCUMENT = 8;
    public static final int AUTODOWNLOAD_MASK_GIF = 32;
    public static final int AUTODOWNLOAD_MASK_MUSIC = 16;
    public static final int AUTODOWNLOAD_MASK_PHOTO = 1;
    public static final int AUTODOWNLOAD_MASK_VIDEO = 4;
    public static final int AUTODOWNLOAD_MASK_VIDEOMESSAGE = 64;
    private static volatile MediaController Instance = null;
    public static final String MIME_TYPE = "video/avc";
    private static final int PROCESSOR_TYPE_INTEL = 2;
    private static final int PROCESSOR_TYPE_MTK = 3;
    private static final int PROCESSOR_TYPE_OTHER = 0;
    private static final int PROCESSOR_TYPE_QCOM = 1;
    private static final int PROCESSOR_TYPE_SEC = 4;
    private static final int PROCESSOR_TYPE_TI = 5;
    private static final float VOLUME_DUCK = 0.2f;
    private static final float VOLUME_NORMAL = 1.0f;
    public static AlbumEntry allMediaAlbumEntry;
    public static AlbumEntry allPhotosAlbumEntry;
    private static Runnable broadcastPhotosRunnable;
    private static final String[] projectionPhotos = new String[]{"_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "orientation"};
    private static final String[] projectionVideo = new String[]{"_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "duration"};
    public static int[] readArgs = new int[3];
    private Sensor accelerometerSensor;
    private boolean accelerometerVertical;
    private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap();
    private boolean allowStartRecord;
    private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList();
    private int audioFocus = 0;
    private AudioInfo audioInfo;
    private MediaPlayer audioPlayer = null;
    private AudioRecord audioRecorder;
    private AudioTrack audioTrackPlayer = null;
    private boolean autoplayGifs = true;
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
    private boolean isDrawingWasReady;
    private boolean isPaused = false;
    private int lastCheckMask = 0;
    private long lastMediaCheckTime = 0;
    private long lastPlayPcm;
    private long lastProgress = 0;
    private float lastProximityValue = -100.0f;
    private EncryptedChat lastSecretChat = null;
    private long lastSecretChatEnterTime = 0;
    private long lastSecretChatLeaveTime = 0;
    private ArrayList<Long> lastSecretChatVisibleMessages = null;
    private int lastTag = 0;
    private long lastTimestamp = 0;
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
    private PipRoundVideoView pipRoundVideoView;
    private int pipSwitchingState;
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
    private WakeLock proximityWakeLock;
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
    private Runnable recordRunnable = new Runnable() {
        public void run() {
            if (MediaController.this.audioRecorder != null) {
                ByteBuffer buffer;
                if (MediaController.this.recordBuffers.isEmpty()) {
                    buffer = ByteBuffer.allocateDirect(MediaController.this.recordBufferSize);
                    buffer.order(ByteOrder.nativeOrder());
                } else {
                    buffer = (ByteBuffer) MediaController.this.recordBuffers.get(0);
                    MediaController.this.recordBuffers.remove(0);
                }
                buffer.rewind();
                int len = MediaController.this.audioRecorder.read(buffer, buffer.capacity());
                if (len > 0) {
                    buffer.limit(len);
                    double d = 0.0d;
                    try {
                        float sampleStep;
                        long newSamplesCount = MediaController.this.samplesCount + ((long) (len / 2));
                        int currentPart = (int) ((((double) MediaController.this.samplesCount) / ((double) newSamplesCount)) * ((double) MediaController.this.recordSamples.length));
                        int newPart = MediaController.this.recordSamples.length - currentPart;
                        if (currentPart != 0) {
                            sampleStep = ((float) MediaController.this.recordSamples.length) / ((float) currentPart);
                            float currentNum = 0.0f;
                            for (int a = 0; a < currentPart; a++) {
                                MediaController.this.recordSamples[a] = MediaController.this.recordSamples[(int) currentNum];
                                currentNum += sampleStep;
                            }
                        }
                        int currentNum2 = currentPart;
                        float nextNum = 0.0f;
                        sampleStep = (((float) len) / 2.0f) / ((float) newPart);
                        for (int i = 0; i < len / 2; i++) {
                            short peak = buffer.getShort();
                            if (peak > (short) 2500) {
                                d += (double) (peak * peak);
                            }
                            if (i == ((int) nextNum) && currentNum2 < MediaController.this.recordSamples.length) {
                                MediaController.this.recordSamples[currentNum2] = peak;
                                nextNum += sampleStep;
                                currentNum2++;
                            }
                        }
                        MediaController.this.samplesCount = newSamplesCount;
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    buffer.position(0);
                    final double amplitude = Math.sqrt((d / ((double) len)) / 2.0d);
                    final ByteBuffer finalBuffer = buffer;
                    final boolean flush = len != buffer.capacity();
                    if (len != 0) {
                        MediaController.this.fileEncodingQueue.postRunnable(new Runnable() {
                            public void run() {
                                while (finalBuffer.hasRemaining()) {
                                    int oldLimit = -1;
                                    if (finalBuffer.remaining() > MediaController.this.fileBuffer.remaining()) {
                                        oldLimit = finalBuffer.limit();
                                        finalBuffer.limit(MediaController.this.fileBuffer.remaining() + finalBuffer.position());
                                    }
                                    MediaController.this.fileBuffer.put(finalBuffer);
                                    if (MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit() || flush) {
                                        if (MediaController.this.writeFrame(MediaController.this.fileBuffer, !flush ? MediaController.this.fileBuffer.limit() : finalBuffer.position()) != 0) {
                                            MediaController.this.fileBuffer.rewind();
                                            MediaController.this.recordTimeCount = MediaController.this.recordTimeCount + ((long) ((MediaController.this.fileBuffer.limit() / 2) / 16));
                                        }
                                    }
                                    if (oldLimit != -1) {
                                        finalBuffer.limit(oldLimit);
                                    }
                                }
                                MediaController.this.recordQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        MediaController.this.recordBuffers.add(finalBuffer);
                                    }
                                });
                            }
                        });
                    }
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(amplitude));
                        }
                    });
                    return;
                }
                MediaController.this.recordBuffers.add(buffer);
                MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
            }
        }
    };
    private short[] recordSamples = new short[1024];
    private Runnable recordStartRunnable;
    private long recordStartTime;
    private long recordTimeCount;
    private TL_document recordingAudio;
    private File recordingAudioFile;
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
    private ArrayList<DownloadObject> videoMessageDownloadQueue = new ArrayList();
    private VideoPlayer videoPlayer;
    private final Object videoQueueSync = new Object();
    private ArrayList<MessageObject> voiceMessagesPlaylist;
    private HashMap<Integer, MessageObject> voiceMessagesPlaylistMap;
    private boolean voiceMessagesPlaylistUnread;
    public int wifiDownloadMask = 0;

    public static class AlbumEntry {
        public int bucketId;
        public String bucketName;
        public PhotoEntry coverPhoto;
        public ArrayList<PhotoEntry> photos = new ArrayList();
        public HashMap<Integer, PhotoEntry> photosByIds = new HashMap();

        public AlbumEntry(int bucketId, String bucketName, PhotoEntry coverPhoto) {
            this.bucketId = bucketId;
            this.bucketName = bucketName;
            this.coverPhoto = coverPhoto;
        }

        public void addPhoto(PhotoEntry photoEntry) {
            this.photos.add(photoEntry);
            this.photosByIds.put(Integer.valueOf(photoEntry.imageId), photoEntry);
        }
    }

    private class AudioBuffer {
        ByteBuffer buffer;
        byte[] bufferBytes;
        int finished;
        long pcmOffset;
        int size;

        public AudioBuffer(int capacity) {
            this.buffer = ByteBuffer.allocateDirect(capacity);
            this.bufferBytes = new byte[capacity];
        }
    }

    public static class AudioEntry {
        public String author;
        public int duration;
        public String genre;
        public long id;
        public MessageObject messageObject;
        public String path;
        public String title;
    }

    private class ExternalObserver extends ContentObserver {
        public ExternalObserver() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MediaController.this.processMediaObserver(Media.EXTERNAL_CONTENT_URI);
        }
    }

    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str);

        void onProgressDownload(String str, float f);

        void onProgressUpload(String str, float f, boolean z);

        void onSuccessDownload(String str);
    }

    private class GalleryObserverExternal extends ContentObserver {
        public GalleryObserverExternal() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.this.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.this.refreshGalleryRunnable = new Runnable() {
                public void run() {
                    MediaController.this.refreshGalleryRunnable = null;
                    MediaController.loadGalleryPhotosAlbums(0);
                }
            }, 2000);
        }
    }

    private class GalleryObserverInternal extends ContentObserver {
        public GalleryObserverInternal() {
            super(null);
        }

        private void scheduleReloadRunnable() {
            AndroidUtilities.runOnUIThread(MediaController.this.refreshGalleryRunnable = new Runnable() {
                public void run() {
                    if (PhotoViewer.getInstance().isVisible()) {
                        GalleryObserverInternal.this.scheduleReloadRunnable();
                        return;
                    }
                    MediaController.this.refreshGalleryRunnable = null;
                    MediaController.loadGalleryPhotosAlbums(0);
                }
            }, 2000);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.this.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
            }
            scheduleReloadRunnable();
        }
    }

    private class InternalObserver extends ContentObserver {
        public InternalObserver() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MediaController.this.processMediaObserver(Media.INTERNAL_CONTENT_URI);
        }
    }

    public static class PhotoEntry {
        public int bucketId;
        public CharSequence caption;
        public long dateTaken;
        public int duration;
        public VideoEditedInfo editedInfo;
        public int imageId;
        public String imagePath;
        public boolean isVideo;
        public int orientation;
        public String path;
        public ArrayList<InputDocument> stickers = new ArrayList();
        public String thumbPath;

        public PhotoEntry(int bucketId, int imageId, long dateTaken, String path, int orientation, boolean isVideo) {
            this.bucketId = bucketId;
            this.imageId = imageId;
            this.dateTaken = dateTaken;
            this.path = path;
            if (isVideo) {
                this.duration = orientation;
            } else {
                this.orientation = orientation;
            }
            this.isVideo = isVideo;
        }
    }

    public static class SearchImage {
        public CharSequence caption;
        public int date;
        public Document document;
        public int height;
        public String id;
        public String imagePath;
        public String imageUrl;
        public String localUrl;
        public int size;
        public ArrayList<InputDocument> stickers = new ArrayList();
        public String thumbPath;
        public String thumbUrl;
        public int type;
        public int width;
    }

    private final class StopMediaObserverRunnable implements Runnable {
        public int currentObserverToken;

        private StopMediaObserverRunnable() {
            this.currentObserverToken = 0;
        }

        public void run() {
            if (this.currentObserverToken == MediaController.this.startObserverToken) {
                try {
                    if (MediaController.this.internalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
                        MediaController.this.internalObserver = null;
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                try {
                    if (MediaController.this.externalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
                        MediaController.this.externalObserver = null;
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
        }
    }

    private static class VideoConvertRunnable implements Runnable {
        private MessageObject messageObject;

        private VideoConvertRunnable(MessageObject message) {
            this.messageObject = message;
        }

        public void run() {
            MediaController.getInstance().convertVideo(this.messageObject);
        }

        public static void runConversion(final MessageObject obj) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread th = new Thread(new VideoConvertRunnable(obj), "VideoConvertRunnable");
                        th.start();
                        th.join();
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }).start();
        }
    }

    private native void closeOpusFile();

    private native long getTotalPcmDuration();

    private native int isOpusFile(String str);

    private native int openOpusFile(String str);

    private native void readOpusFile(ByteBuffer byteBuffer, int i, int[] iArr);

    private native int seekOpusFile(float f);

    private native int startRecord(String str);

    private native void stopRecord();

    private native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    public static MediaController getInstance() {
        MediaController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        MediaController localInstance2 = new MediaController();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public MediaController() {
        try {
            int a;
            this.recordBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
            if (this.recordBufferSize <= 0) {
                this.recordBufferSize = 1280;
            }
            this.playerBufferSize = AudioTrack.getMinBufferSize(48000, 4, 2);
            if (this.playerBufferSize <= 0) {
                this.playerBufferSize = 3840;
            }
            for (a = 0; a < 5; a++) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                buffer.order(ByteOrder.nativeOrder());
                this.recordBuffers.add(buffer);
            }
            for (a = 0; a < 3; a++) {
                this.freePlayerBuffers.add(new AudioBuffer(this.playerBufferSize));
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            this.sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
            this.linearSensor = this.sensorManager.getDefaultSensor(10);
            this.gravitySensor = this.sensorManager.getDefaultSensor(9);
            if (this.linearSensor == null || this.gravitySensor == null) {
                FileLog.e("gravity or linear sensor not found");
                this.accelerometerSensor = this.sensorManager.getDefaultSensor(1);
                this.linearSensor = null;
                this.gravitySensor = null;
            }
            this.proximitySensor = this.sensorManager.getDefaultSensor(8);
            this.proximityWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        this.recordQueue = new DispatchQueue("recordQueue");
        this.recordQueue.setPriority(10);
        this.fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue.setPriority(10);
        this.playerQueue = new DispatchQueue("playerQueue");
        this.fileDecodingQueue = new DispatchQueue("fileDecodingQueue");
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.mobileDataDownloadMask = preferences.getInt("mobileDataDownloadMask", 115);
        this.wifiDownloadMask = preferences.getInt("wifiDownloadMask", 115);
        this.roamingDownloadMask = preferences.getInt("roamingDownloadMask", 0);
        this.saveToGallery = preferences.getBoolean("save_gallery", false);
        this.autoplayGifs = preferences.getBoolean("autoplay_gif", true);
        this.raiseToSpeak = preferences.getBoolean("raise_to_speak", true);
        this.customTabs = preferences.getBoolean("custom_tabs", true);
        this.directShare = preferences.getBoolean("direct_share", true);
        this.shuffleMusic = preferences.getBoolean("shuffleMusic", false);
        this.repeatMode = preferences.getInt("repeatMode", 0);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
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
        ApplicationLoader.applicationContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                MediaController.this.checkAutodownloadSettings();
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (UserConfig.isClientActivated()) {
            checkAutodownloadSettings();
        }
        if (VERSION.SDK_INT >= 16) {
            this.mediaProjections = new String[]{"_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height"};
        } else {
            this.mediaProjections = new String[]{"_data", "_display_name", "bucket_display_name", "datetaken", "title"};
        }
        try {
            ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(Media.EXTERNAL_CONTENT_URI, false, new GalleryObserverExternal());
        } catch (Throwable e22) {
            FileLog.e(e22);
        }
        try {
            ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(Media.INTERNAL_CONTENT_URI, false, new GalleryObserverInternal());
        } catch (Throwable e222) {
            FileLog.e(e222);
        }
        try {
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                public void onCallStateChanged(final int state, String incomingNumber) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            EmbedBottomSheet embedBottomSheet;
                            if (state == 1) {
                                if (MediaController.this.isPlayingMessage(MediaController.this.getPlayingMessageObject()) && !MediaController.this.isMessagePaused()) {
                                    MediaController.this.pauseMessage(MediaController.this.getPlayingMessageObject());
                                } else if (!(MediaController.this.recordStartRunnable == null && MediaController.this.recordingAudio == null)) {
                                    MediaController.this.stopRecording(2);
                                }
                                embedBottomSheet = EmbedBottomSheet.getInstance();
                                if (embedBottomSheet != null) {
                                    embedBottomSheet.pause();
                                }
                                MediaController.this.callInProgress = true;
                            } else if (state == 0) {
                                MediaController.this.callInProgress = false;
                            } else if (state == 2) {
                                embedBottomSheet = EmbedBottomSheet.getInstance();
                                if (embedBottomSheet != null) {
                                    embedBottomSheet.pause();
                                }
                                MediaController.this.callInProgress = true;
                            }
                        }
                    });
                }
            };
            TelephonyManager mgr = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (mgr != null) {
                mgr.listen(phoneStateListener, 32);
            }
        } catch (Throwable e2222) {
            FileLog.e(e2222);
        }
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                pauseMessage(getPlayingMessageObject());
            }
            this.hasAudioFocus = 0;
            this.audioFocus = 0;
        } else if (focusChange == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                if (isPlayingMessage(getPlayingMessageObject()) && isMessagePaused()) {
                    playMessage(getPlayingMessageObject());
                }
            }
        } else if (focusChange == -3) {
            this.audioFocus = 1;
        } else if (focusChange == -2) {
            this.audioFocus = 0;
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                pauseMessage(getPlayingMessageObject());
                this.resumeAudioOnFocusGain = true;
            }
        }
        setPlayerVolume();
    }

    private void setPlayerVolume() {
        try {
            float volume;
            if (this.audioFocus != 1) {
                volume = VOLUME_NORMAL;
            } else {
                volume = VOLUME_DUCK;
            }
            if (this.audioPlayer != null) {
                this.audioPlayer.setVolume(volume, volume);
            } else if (this.audioTrackPlayer != null) {
                this.audioTrackPlayer.setStereoVolume(volume, volume);
            } else if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(volume);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void startProgressTimer(final MessageObject currentPlayingMessageObject) {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            this.progressTimer = new Timer();
            this.progressTimer.schedule(new TimerTask() {
                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (currentPlayingMessageObject == null) {
                                    return;
                                }
                                if ((MediaController.this.audioPlayer != null || MediaController.this.audioTrackPlayer != null || MediaController.this.videoPlayer != null) && !MediaController.this.isPaused) {
                                    try {
                                        if (MediaController.this.ignoreFirstProgress != 0) {
                                            MediaController.this.ignoreFirstProgress = MediaController.this.ignoreFirstProgress - 1;
                                            return;
                                        }
                                        long progress;
                                        float value;
                                        if (MediaController.this.videoPlayer != null) {
                                            progress = MediaController.this.videoPlayer.getCurrentPosition();
                                            value = ((float) MediaController.this.lastProgress) / ((float) MediaController.this.videoPlayer.getDuration());
                                            if (progress <= MediaController.this.lastProgress || value >= MediaController.VOLUME_NORMAL) {
                                                return;
                                            }
                                        } else if (MediaController.this.audioPlayer != null) {
                                            progress = (long) MediaController.this.audioPlayer.getCurrentPosition();
                                            value = ((float) MediaController.this.lastProgress) / ((float) MediaController.this.audioPlayer.getDuration());
                                            if (progress <= MediaController.this.lastProgress) {
                                                return;
                                            }
                                        } else {
                                            progress = (long) ((int) (((float) MediaController.this.lastPlayPcm) / 48.0f));
                                            value = ((float) MediaController.this.lastPlayPcm) / ((float) MediaController.this.currentTotalPcmDuration);
                                            if (progress == MediaController.this.lastProgress) {
                                                return;
                                            }
                                        }
                                        MediaController.this.lastProgress = progress;
                                        currentPlayingMessageObject.audioProgress = value;
                                        currentPlayingMessageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(currentPlayingMessageObject.getId()), Float.valueOf(value));
                                    } catch (Throwable e) {
                                        FileLog.e(e);
                                    }
                                }
                            }
                        });
                    }
                }
            }, 0, 17);
        }
    }

    private void stopProgressTimer() {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public void cleanup() {
        cleanupPlayer(false, true);
        this.audioInfo = null;
        this.playMusicAgain = false;
        this.photoDownloadQueue.clear();
        this.audioDownloadQueue.clear();
        this.videoMessageDownloadQueue.clear();
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

    protected int getAutodownloadMask() {
        int mask = 0;
        if (!((this.mobileDataDownloadMask & 1) == 0 && (this.wifiDownloadMask & 1) == 0 && (this.roamingDownloadMask & 1) == 0)) {
            mask = 0 | 1;
        }
        if (!((this.mobileDataDownloadMask & 2) == 0 && (this.wifiDownloadMask & 2) == 0 && (this.roamingDownloadMask & 2) == 0)) {
            mask |= 2;
        }
        if (!((this.mobileDataDownloadMask & 64) == 0 && (this.wifiDownloadMask & 64) == 0 && (this.roamingDownloadMask & 64) == 0)) {
            mask |= 64;
        }
        if (!((this.mobileDataDownloadMask & 4) == 0 && (this.wifiDownloadMask & 4) == 0 && (this.roamingDownloadMask & 4) == 0)) {
            mask |= 4;
        }
        if (!((this.mobileDataDownloadMask & 8) == 0 && (this.wifiDownloadMask & 8) == 0 && (this.roamingDownloadMask & 8) == 0)) {
            mask |= 8;
        }
        if (!((this.mobileDataDownloadMask & 16) == 0 && (this.wifiDownloadMask & 16) == 0 && (this.roamingDownloadMask & 16) == 0)) {
            mask |= 16;
        }
        if ((this.mobileDataDownloadMask & 32) == 0 && (this.wifiDownloadMask & 32) == 0 && (this.roamingDownloadMask & 32) == 0) {
            return mask;
        }
        return mask | 32;
    }

    public void checkAutodownloadSettings() {
        int currentMask = getCurrentDownloadMask();
        if (currentMask != this.lastCheckMask) {
            int a;
            this.lastCheckMask = currentMask;
            if ((currentMask & 1) == 0) {
                for (a = 0; a < this.photoDownloadQueue.size(); a++) {
                    FileLoader.getInstance().cancelLoadFile((PhotoSize) ((DownloadObject) this.photoDownloadQueue.get(a)).object);
                }
                this.photoDownloadQueue.clear();
            } else if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(1);
            }
            if ((currentMask & 2) == 0) {
                for (a = 0; a < this.audioDownloadQueue.size(); a++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.audioDownloadQueue.get(a)).object);
                }
                this.audioDownloadQueue.clear();
            } else if (this.audioDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(2);
            }
            if ((currentMask & 64) == 0) {
                for (a = 0; a < this.videoMessageDownloadQueue.size(); a++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.videoMessageDownloadQueue.get(a)).object);
                }
                this.videoMessageDownloadQueue.clear();
            } else if (this.videoMessageDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(64);
            }
            if ((currentMask & 8) == 0) {
                for (a = 0; a < this.documentDownloadQueue.size(); a++) {
                    FileLoader.getInstance().cancelLoadFile(((DownloadObject) this.documentDownloadQueue.get(a)).object);
                }
                this.documentDownloadQueue.clear();
            } else if (this.documentDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(8);
            }
            if ((currentMask & 4) == 0) {
                for (a = 0; a < this.videoDownloadQueue.size(); a++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.videoDownloadQueue.get(a)).object);
                }
                this.videoDownloadQueue.clear();
            } else if (this.videoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(4);
            }
            if ((currentMask & 16) == 0) {
                for (a = 0; a < this.musicDownloadQueue.size(); a++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.musicDownloadQueue.get(a)).object);
                }
                this.musicDownloadQueue.clear();
            } else if (this.musicDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(16);
            }
            if ((currentMask & 32) == 0) {
                for (a = 0; a < this.gifDownloadQueue.size(); a++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.gifDownloadQueue.get(a)).object);
                }
                this.gifDownloadQueue.clear();
            } else if (this.gifDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(32);
            }
            int mask = getAutodownloadMask();
            if (mask == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(0);
                return;
            }
            if ((mask & 1) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(1);
            }
            if ((mask & 2) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(2);
            }
            if ((mask & 64) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(64);
            }
            if ((mask & 4) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(4);
            }
            if ((mask & 8) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(8);
            }
            if ((mask & 16) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(16);
            }
            if ((mask & 32) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(32);
            }
        }
    }

    public boolean canDownloadMedia(int type) {
        return (getCurrentDownloadMask() & type) != 0;
    }

    private int getCurrentDownloadMask() {
        if (ConnectionsManager.isConnectedToWiFi()) {
            return this.wifiDownloadMask;
        }
        if (ConnectionsManager.isRoaming()) {
            return this.roamingDownloadMask;
        }
        return this.mobileDataDownloadMask;
    }

    protected void processDownloadObjects(int type, ArrayList<DownloadObject> objects) {
        if (!objects.isEmpty()) {
            ArrayList<DownloadObject> queue = null;
            if (type == 1) {
                queue = this.photoDownloadQueue;
            } else if (type == 2) {
                queue = this.audioDownloadQueue;
            } else if (type == 64) {
                queue = this.videoMessageDownloadQueue;
            } else if (type == 4) {
                queue = this.videoDownloadQueue;
            } else if (type == 8) {
                queue = this.documentDownloadQueue;
            } else if (type == 16) {
                queue = this.musicDownloadQueue;
            } else if (type == 32) {
                queue = this.gifDownloadQueue;
            }
            for (int a = 0; a < objects.size(); a++) {
                String path;
                DownloadObject downloadObject = (DownloadObject) objects.get(a);
                if (downloadObject.object instanceof Document) {
                    path = FileLoader.getAttachFileName(downloadObject.object);
                } else {
                    path = FileLoader.getAttachFileName(downloadObject.object);
                }
                if (!this.downloadQueueKeys.containsKey(path)) {
                    boolean added = true;
                    if (downloadObject.object instanceof PhotoSize) {
                        FileLoader.getInstance().loadFile((PhotoSize) downloadObject.object, null, false);
                    } else if (downloadObject.object instanceof Document) {
                        FileLoader.getInstance().loadFile((Document) downloadObject.object, false, false);
                    } else {
                        added = false;
                    }
                    if (added) {
                        queue.add(downloadObject);
                        this.downloadQueueKeys.put(path, downloadObject);
                    }
                }
            }
        }
    }

    protected void newDownloadObjectsAvailable(int downloadMask) {
        int mask = getCurrentDownloadMask();
        if (!((mask & 1) == 0 || (downloadMask & 1) == 0 || !this.photoDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(1);
        }
        if (!((mask & 2) == 0 || (downloadMask & 2) == 0 || !this.audioDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(2);
        }
        if (!((mask & 64) == 0 || (downloadMask & 64) == 0 || !this.videoMessageDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(64);
        }
        if (!((mask & 4) == 0 || (downloadMask & 4) == 0 || !this.videoDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(4);
        }
        if (!((mask & 8) == 0 || (downloadMask & 8) == 0 || !this.documentDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(8);
        }
        if (!((mask & 16) == 0 || (downloadMask & 16) == 0 || !this.musicDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(16);
        }
        if ((mask & 32) != 0 && (downloadMask & 32) != 0 && this.gifDownloadQueue.isEmpty()) {
            MessagesStorage.getInstance().getDownloadQueue(32);
        }
    }

    private void checkDownloadFinished(String fileName, int state) {
        DownloadObject downloadObject = (DownloadObject) this.downloadQueueKeys.get(fileName);
        if (downloadObject != null) {
            this.downloadQueueKeys.remove(fileName);
            if (state == 0 || state == 2) {
                MessagesStorage.getInstance().removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            if (downloadObject.type == 1) {
                this.photoDownloadQueue.remove(downloadObject);
                if (this.photoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(1);
                }
            } else if (downloadObject.type == 2) {
                this.audioDownloadQueue.remove(downloadObject);
                if (this.audioDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(2);
                }
            } else if (downloadObject.type == 64) {
                this.videoMessageDownloadQueue.remove(downloadObject);
                if (this.videoMessageDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(64);
                }
            } else if (downloadObject.type == 4) {
                this.videoDownloadQueue.remove(downloadObject);
                if (this.videoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(4);
                }
            } else if (downloadObject.type == 8) {
                this.documentDownloadQueue.remove(downloadObject);
                if (this.documentDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(8);
                }
            } else if (downloadObject.type == 16) {
                this.musicDownloadQueue.remove(downloadObject);
                if (this.musicDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(16);
                }
            } else if (downloadObject.type == 32) {
                this.gifDownloadQueue.remove(downloadObject);
                if (this.gifDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(32);
                }
            }
        }
    }

    public void startMediaObserver() {
        ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
        this.startObserverToken++;
        try {
            if (this.internalObserver == null) {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri = Media.EXTERNAL_CONTENT_URI;
                ContentObserver externalObserver = new ExternalObserver();
                this.externalObserver = externalObserver;
                contentResolver.registerContentObserver(uri, false, externalObserver);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            if (this.externalObserver == null) {
                contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                uri = Media.INTERNAL_CONTENT_URI;
                externalObserver = new InternalObserver();
                this.internalObserver = externalObserver;
                contentResolver.registerContentObserver(uri, false, externalObserver);
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
    }

    public void stopMediaObserver() {
        if (this.stopMediaObserverRunnable == null) {
            this.stopMediaObserverRunnable = new StopMediaObserverRunnable();
        }
        this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
        ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public void processMediaObserver(Uri uri) {
        try {
            Point size = AndroidUtilities.getRealScreenSize();
            Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
            ArrayList<Long> screenshotDates = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String val = "";
                    String data = cursor.getString(0);
                    String display_name = cursor.getString(1);
                    String album_name = cursor.getString(2);
                    long date = cursor.getLong(3);
                    String title = cursor.getString(4);
                    int photoW = 0;
                    int photoH = 0;
                    if (VERSION.SDK_INT >= 16) {
                        photoW = cursor.getInt(5);
                        photoH = cursor.getInt(6);
                    }
                    if ((data != null && data.toLowerCase().contains("screenshot")) || ((display_name != null && display_name.toLowerCase().contains("screenshot")) || ((album_name != null && album_name.toLowerCase().contains("screenshot")) || (title != null && title.toLowerCase().contains("screenshot"))))) {
                        if (photoW == 0 || photoH == 0) {
                            try {
                                Options bmOptions = new Options();
                                bmOptions.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(data, bmOptions);
                                photoW = bmOptions.outWidth;
                                photoH = bmOptions.outHeight;
                            } catch (Exception e) {
                                screenshotDates.add(Long.valueOf(date));
                            }
                        }
                        if (photoW <= 0 || photoH <= 0 || ((photoW == size.x && photoH == size.y) || (photoH == size.x && photoW == size.y))) {
                            screenshotDates.add(Long.valueOf(date));
                        }
                    }
                }
                cursor.close();
            }
            if (!screenshotDates.isEmpty()) {
                final ArrayList<Long> arrayList = screenshotDates;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
                        MediaController.this.checkScreenshots(arrayList);
                    }
                });
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
    }

    private void checkScreenshots(ArrayList<Long> dates) {
        if (dates != null && !dates.isEmpty() && this.lastSecretChatEnterTime != 0 && this.lastSecretChat != null && (this.lastSecretChat instanceof TL_encryptedChat)) {
            boolean send = false;
            Iterator it = dates.iterator();
            while (it.hasNext()) {
                Long date = (Long) it.next();
                if ((this.lastMediaCheckTime == 0 || date.longValue() > this.lastMediaCheckTime) && date.longValue() >= this.lastSecretChatEnterTime) {
                    if (this.lastSecretChatLeaveTime == 0 || date.longValue() <= this.lastSecretChatLeaveTime + 2000) {
                        this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, date.longValue());
                        send = true;
                    }
                }
            }
            if (send) {
                SecretChatHelper.getInstance().sendScreenshotMessage(this.lastSecretChat, this.lastSecretChatVisibleMessages, null);
            }
        }
    }

    public void setLastEncryptedChatParams(long enterTime, long leaveTime, EncryptedChat encryptedChat, ArrayList<Long> visibleMessages) {
        this.lastSecretChatEnterTime = enterTime;
        this.lastSecretChatLeaveTime = leaveTime;
        this.lastSecretChat = encryptedChat;
        this.lastSecretChatVisibleMessages = visibleMessages;
    }

    public int generateObserverTag() {
        int i = this.lastTag;
        this.lastTag = i + 1;
        return i;
    }

    public void addLoadingFileObserver(String fileName, FileDownloadProgressListener observer) {
        addLoadingFileObserver(fileName, null, observer);
    }

    public void addLoadingFileObserver(String fileName, MessageObject messageObject, FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.addLaterArray.put(fileName, observer);
            return;
        }
        removeLoadingFileObserver(observer);
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.loadingFileObservers.put(fileName, arrayList);
        }
        arrayList.add(new WeakReference(observer));
        if (messageObject != null) {
            ArrayList<MessageObject> messageObjects = (ArrayList) this.loadingFileMessagesObservers.get(fileName);
            if (messageObjects == null) {
                messageObjects = new ArrayList();
                this.loadingFileMessagesObservers.put(fileName, messageObjects);
            }
            messageObjects.add(messageObject);
        }
        this.observersByTag.put(Integer.valueOf(observer.getObserverTag()), fileName);
    }

    public void removeLoadingFileObserver(FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.deleteLaterArray.add(observer);
            return;
        }
        String fileName = (String) this.observersByTag.get(Integer.valueOf(observer.getObserverTag()));
        if (fileName != null) {
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                int a = 0;
                while (a < arrayList.size()) {
                    WeakReference<FileDownloadProgressListener> reference = (WeakReference) arrayList.get(a);
                    if (reference.get() == null || reference.get() == observer) {
                        arrayList.remove(a);
                        a--;
                    }
                    a++;
                }
                if (arrayList.isEmpty()) {
                    this.loadingFileObservers.remove(fileName);
                }
            }
            this.observersByTag.remove(Integer.valueOf(observer.getObserverTag()));
        }
    }

    private void processLaterArrays() {
        for (Entry<String, FileDownloadProgressListener> listener : this.addLaterArray.entrySet()) {
            addLoadingFileObserver((String) listener.getKey(), (FileDownloadProgressListener) listener.getValue());
        }
        this.addLaterArray.clear();
        Iterator it = this.deleteLaterArray.iterator();
        while (it.hasNext()) {
            removeLoadingFileObserver((FileDownloadProgressListener) it.next());
        }
        this.deleteLaterArray.clear();
    }

    public void didReceivedNotification(int id, Object... args) {
        String fileName;
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList;
        int a;
        WeakReference<FileDownloadProgressListener> reference;
        if (id == NotificationCenter.FileDidFailedLoad || id == NotificationCenter.httpFileDidFailedLoad) {
            this.listenerInProgress = true;
            fileName = args[0];
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                for (a = 0; a < arrayList.size(); a++) {
                    reference = (WeakReference) arrayList.get(a);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onFailedDownload(fileName);
                        this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener) reference.get()).getObserverTag()));
                    }
                }
                this.loadingFileObservers.remove(fileName);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, ((Integer) args[1]).intValue());
        } else if (id == NotificationCenter.FileDidLoaded || id == NotificationCenter.httpFileDidLoaded) {
            this.listenerInProgress = true;
            fileName = (String) args[0];
            if (this.downloadingCurrentMessage && this.playingMessageObject != null && FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(fileName)) {
                this.playMusicAgain = true;
                playMessage(this.playingMessageObject);
            }
            ArrayList<MessageObject> messageObjects = (ArrayList) this.loadingFileMessagesObservers.get(fileName);
            if (messageObjects != null) {
                for (a = 0; a < messageObjects.size(); a++) {
                    ((MessageObject) messageObjects.get(a)).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(fileName);
            }
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                for (a = 0; a < arrayList.size(); a++) {
                    reference = (WeakReference) arrayList.get(a);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onSuccessDownload(fileName);
                        this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener) reference.get()).getObserverTag()));
                    }
                }
                this.loadingFileObservers.remove(fileName);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, 0);
        } else if (id == NotificationCenter.FileLoadProgressChanged) {
            this.listenerInProgress = true;
            fileName = (String) args[0];
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                progress = args[1];
                r26 = arrayList.iterator();
                while (r26.hasNext()) {
                    reference = (WeakReference) r26.next();
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onProgressDownload(fileName, progress.floatValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
        } else if (id == NotificationCenter.FileUploadProgressChanged) {
            this.listenerInProgress = true;
            fileName = (String) args[0];
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                progress = (Float) args[1];
                Boolean enc = args[2];
                r26 = arrayList.iterator();
                while (r26.hasNext()) {
                    reference = (WeakReference) r26.next();
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onProgressUpload(fileName, progress.floatValue(), enc.booleanValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            try {
                ArrayList<DelayedMessage> delayedMessages = SendMessagesHelper.getInstance().getDelayedMessages(fileName);
                if (delayedMessages != null) {
                    for (a = 0; a < delayedMessages.size(); a++) {
                        DelayedMessage delayedMessage = (DelayedMessage) delayedMessages.get(a);
                        if (delayedMessage.encryptedChat == null) {
                            long dialog_id = delayedMessage.obj.getDialogId();
                            Long lastTime = (Long) this.typingTimes.get(Long.valueOf(dialog_id));
                            if (lastTime == null || lastTime.longValue() + 4000 < System.currentTimeMillis()) {
                                if (MessageObject.isRoundVideoDocument(delayedMessage.documentLocation)) {
                                    MessagesController.getInstance().sendTyping(dialog_id, 8, 0);
                                } else if (MessageObject.isVideoDocument(delayedMessage.documentLocation)) {
                                    MessagesController.getInstance().sendTyping(dialog_id, 5, 0);
                                } else if (delayedMessage.documentLocation != null) {
                                    MessagesController.getInstance().sendTyping(dialog_id, 3, 0);
                                } else if (delayedMessage.location != null) {
                                    MessagesController.getInstance().sendTyping(dialog_id, 4, 0);
                                }
                                this.typingTimes.put(Long.valueOf(dialog_id), Long.valueOf(System.currentTimeMillis()));
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            int channelId = ((Integer) args[1]).intValue();
            ArrayList<Integer> markAsDeletedMessages = args[0];
            if (this.playingMessageObject != null && channelId == this.playingMessageObject.messageOwner.to_id.channel_id && markAsDeletedMessages.contains(Integer.valueOf(this.playingMessageObject.getId()))) {
                cleanupPlayer(true, true);
            }
            if (this.voiceMessagesPlaylist != null && !this.voiceMessagesPlaylist.isEmpty() && channelId == ((MessageObject) this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id) {
                for (a = 0; a < markAsDeletedMessages.size(); a++) {
                    messageObject = (MessageObject) this.voiceMessagesPlaylistMap.remove(markAsDeletedMessages.get(a));
                    if (messageObject != null) {
                        this.voiceMessagesPlaylist.remove(messageObject);
                    }
                }
            }
        } else if (id == NotificationCenter.removeAllMessagesFromDialog) {
            did = ((Long) args[0]).longValue();
            if (this.playingMessageObject != null && this.playingMessageObject.getDialogId() == did) {
                cleanupPlayer(false, true);
            }
        } else if (id == NotificationCenter.musicDidLoaded) {
            did = ((Long) args[0]).longValue();
            if (this.playingMessageObject != null && this.playingMessageObject.isMusic() && this.playingMessageObject.getDialogId() == did) {
                ArrayList<MessageObject> arrayList2 = args[1];
                this.playlist.addAll(0, arrayList2);
                if (this.shuffleMusic) {
                    buildShuffledPlayList();
                    this.currentPlaylistNum = 0;
                    return;
                }
                this.currentPlaylistNum += arrayList2.size();
            }
        } else if (id == NotificationCenter.didReceivedNewMessages && this.voiceMessagesPlaylist != null && !this.voiceMessagesPlaylist.isEmpty()) {
            if (((Long) args[0]).longValue() == ((MessageObject) this.voiceMessagesPlaylist.get(0)).getDialogId()) {
                ArrayList<MessageObject> arr = args[1];
                for (a = 0; a < arr.size(); a++) {
                    messageObject = (MessageObject) arr.get(a);
                    if ((messageObject.isVoice() || messageObject.isRoundVideo()) && (!this.voiceMessagesPlaylistUnread || (messageObject.isContentUnread() && !messageObject.isOut()))) {
                        this.voiceMessagesPlaylist.add(messageObject);
                        this.voiceMessagesPlaylistMap.put(Integer.valueOf(messageObject.getId()), messageObject);
                    }
                }
            }
        }
    }

    private void checkDecoderQueue() {
        this.fileDecodingQueue.postRunnable(new Runnable() {
            public void run() {
                if (MediaController.this.decodingFinished) {
                    MediaController.this.checkPlayerQueue();
                    return;
                }
                boolean was = false;
                while (true) {
                    AudioBuffer buffer = null;
                    synchronized (MediaController.this.playerSync) {
                        if (!MediaController.this.freePlayerBuffers.isEmpty()) {
                            buffer = (AudioBuffer) MediaController.this.freePlayerBuffers.get(0);
                            MediaController.this.freePlayerBuffers.remove(0);
                        }
                        if (!MediaController.this.usedPlayerBuffers.isEmpty()) {
                            was = true;
                        }
                    }
                    if (buffer == null) {
                        break;
                    }
                    MediaController.this.readOpusFile(buffer.buffer, MediaController.this.playerBufferSize, MediaController.readArgs);
                    buffer.size = MediaController.readArgs[0];
                    buffer.pcmOffset = (long) MediaController.readArgs[1];
                    buffer.finished = MediaController.readArgs[2];
                    if (buffer.finished == 1) {
                        MediaController.this.decodingFinished = true;
                    }
                    if (buffer.size == 0) {
                        break;
                    }
                    buffer.buffer.rewind();
                    buffer.buffer.get(buffer.bufferBytes);
                    synchronized (MediaController.this.playerSync) {
                        MediaController.this.usedPlayerBuffers.add(buffer);
                    }
                    was = true;
                }
                synchronized (MediaController.this.playerSync) {
                    MediaController.this.freePlayerBuffers.add(buffer);
                }
                if (was) {
                    MediaController.this.checkPlayerQueue();
                }
            }
        });
    }

    private void checkPlayerQueue() {
        this.playerQueue.postRunnable(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (MediaController.this.playerObjectSync) {
                    if (MediaController.this.audioTrackPlayer == null || MediaController.this.audioTrackPlayer.getPlayState() != 3) {
                    }
                }
                MediaController.this.buffersWrited = MediaController.this.buffersWrited + 1;
                if (count > 0) {
                    final long pcm = buffer.pcmOffset;
                    final int marker = buffer.finished == 1 ? count : -1;
                    final int finalBuffersWrited = MediaController.this.buffersWrited;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MediaController.this.lastPlayPcm = pcm;
                            if (marker != -1) {
                                if (MediaController.this.audioTrackPlayer != null) {
                                    MediaController.this.audioTrackPlayer.setNotificationMarkerPosition(1);
                                }
                                if (finalBuffersWrited == 1) {
                                    MediaController.this.cleanupPlayer(true, true, true);
                                }
                            }
                        }
                    });
                }
                if (buffer.finished != 1) {
                    MediaController.this.checkPlayerQueue();
                }
                if (buffer == null || !(buffer == null || buffer.finished == 1)) {
                    MediaController.this.checkDecoderQueue();
                }
                if (buffer != null) {
                    synchronized (MediaController.this.playerSync) {
                        MediaController.this.freePlayerBuffers.add(buffer);
                    }
                }
            }
        });
    }

    protected boolean isRecordingAudio() {
        return (this.recordStartRunnable == null && this.recordingAudio == null) ? false : true;
    }

    private boolean isNearToSensor(float value) {
        return value < 5.0f && value != this.proximitySensor.getMaximumRange();
    }

    public void onSensorChanged(SensorEvent event) {
        if (this.sensorsStarted && VoIPService.getSharedInstance() == null) {
            if (event.sensor == this.proximitySensor) {
                FileLog.e("proximity changed to " + event.values[0]);
                if (this.lastProximityValue == -100.0f) {
                    this.lastProximityValue = event.values[0];
                } else if (this.lastProximityValue != event.values[0]) {
                    this.proximityHasDifferentValues = true;
                }
                if (this.proximityHasDifferentValues) {
                    this.proximityTouched = isNearToSensor(event.values[0]);
                }
            } else if (event.sensor == this.accelerometerSensor) {
                double alpha;
                if (this.lastTimestamp == 0) {
                    alpha = 0.9800000190734863d;
                } else {
                    alpha = 1.0d / (1.0d + (((double) (event.timestamp - this.lastTimestamp)) / 1.0E9d));
                }
                this.lastTimestamp = event.timestamp;
                this.gravity[0] = (float) ((((double) this.gravity[0]) * alpha) + ((1.0d - alpha) * ((double) event.values[0])));
                this.gravity[1] = (float) ((((double) this.gravity[1]) * alpha) + ((1.0d - alpha) * ((double) event.values[1])));
                this.gravity[2] = (float) ((((double) this.gravity[2]) * alpha) + ((1.0d - alpha) * ((double) event.values[2])));
                this.gravityFast[0] = (0.8f * this.gravity[0]) + (0.19999999f * event.values[0]);
                this.gravityFast[1] = (0.8f * this.gravity[1]) + (0.19999999f * event.values[1]);
                this.gravityFast[2] = (0.8f * this.gravity[2]) + (0.19999999f * event.values[2]);
                this.linearAcceleration[0] = event.values[0] - this.gravity[0];
                this.linearAcceleration[1] = event.values[1] - this.gravity[1];
                this.linearAcceleration[2] = event.values[2] - this.gravity[2];
            } else if (event.sensor == this.linearSensor) {
                this.linearAcceleration[0] = event.values[0];
                this.linearAcceleration[1] = event.values[1];
                this.linearAcceleration[2] = event.values[2];
            } else if (event.sensor == this.gravitySensor) {
                float[] fArr = this.gravityFast;
                float[] fArr2 = this.gravity;
                float f = event.values[0];
                fArr2[0] = f;
                fArr[0] = f;
                fArr = this.gravityFast;
                fArr2 = this.gravity;
                f = event.values[1];
                fArr2[1] = f;
                fArr[1] = f;
                fArr = this.gravityFast;
                fArr2 = this.gravity;
                f = event.values[2];
                fArr2[2] = f;
                fArr[2] = f;
            }
            if (event.sensor == this.linearSensor || event.sensor == this.gravitySensor || event.sensor == this.accelerometerSensor) {
                float val = ((this.gravity[0] * this.linearAcceleration[0]) + (this.gravity[1] * this.linearAcceleration[1])) + (this.gravity[2] * this.linearAcceleration[2]);
                if (this.raisedToBack != 6) {
                    if (val <= 0.0f || this.previousAccValue <= 0.0f) {
                        if (val < 0.0f && this.previousAccValue < 0.0f) {
                            if (this.raisedToTop != 6 || val >= -15.0f) {
                                if (val > -15.0f) {
                                    this.countLess++;
                                }
                                if (!(this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                                    this.raisedToTop = 0;
                                    this.raisedToBack = 0;
                                    this.countLess = 0;
                                }
                            } else if (this.raisedToBack < 6) {
                                this.raisedToBack++;
                                if (this.raisedToBack == 6) {
                                    this.raisedToTop = 0;
                                    this.countLess = 0;
                                    this.timeSinceRaise = System.currentTimeMillis();
                                }
                            }
                        }
                    } else if (val <= 15.0f || this.raisedToBack != 0) {
                        if (val < 15.0f) {
                            this.countLess++;
                        }
                        if (!(this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                            this.raisedToBack = 0;
                            this.raisedToTop = 0;
                            this.countLess = 0;
                        }
                    } else if (this.raisedToTop < 6 && !this.proximityTouched) {
                        this.raisedToTop++;
                        if (this.raisedToTop == 6) {
                            this.countLess = 0;
                        }
                    }
                }
                this.previousAccValue = val;
                boolean z = this.gravityFast[1] > 2.5f && Math.abs(this.gravityFast[2]) < 4.0f && Math.abs(this.gravityFast[0]) > 1.5f;
                this.accelerometerVertical = z;
            }
            if (this.raisedToBack == 6 && this.accelerometerVertical && this.proximityTouched && !NotificationsController.getInstance().audioManager.isWiredHeadsetOn()) {
                FileLog.e("sensor values reached");
                if (this.playingMessageObject == null && this.recordStartRunnable == null && this.recordingAudio == null && !PhotoViewer.getInstance().isVisible() && ApplicationLoader.isScreenOn && !this.inputFieldHasText && this.allowStartRecord && this.raiseChat != null && !this.callInProgress) {
                    if (!this.raiseToEarRecord) {
                        FileLog.e("start record");
                        this.useFrontSpeaker = true;
                        if (!this.raiseChat.playFirstUnreadVoiceMessage()) {
                            this.raiseToEarRecord = true;
                            this.useFrontSpeaker = false;
                            startRecording(this.raiseChat.getDialogId(), null);
                        }
                        this.ignoreOnPause = true;
                        if (!(!this.proximityHasDifferentValues || this.proximityWakeLock == null || this.proximityWakeLock.isHeld())) {
                            this.proximityWakeLock.acquire();
                        }
                    }
                } else if (this.playingMessageObject != null && ((this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker)) {
                    FileLog.e("start listen");
                    if (!(!this.proximityHasDifferentValues || this.proximityWakeLock == null || this.proximityWakeLock.isHeld())) {
                        this.proximityWakeLock.acquire();
                    }
                    this.useFrontSpeaker = true;
                    startAudioAgain(false);
                    this.ignoreOnPause = true;
                }
                this.raisedToBack = 0;
                this.raisedToTop = 0;
                this.countLess = 0;
            } else if (this.proximityTouched) {
                if (this.playingMessageObject != null && ((this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker)) {
                    FileLog.e("start listen by proximity only");
                    if (!(!this.proximityHasDifferentValues || this.proximityWakeLock == null || this.proximityWakeLock.isHeld())) {
                        this.proximityWakeLock.acquire();
                    }
                    this.useFrontSpeaker = true;
                    startAudioAgain(false);
                    this.ignoreOnPause = true;
                }
            } else if (!this.proximityTouched) {
                if (this.raiseToEarRecord) {
                    FileLog.e("stop record");
                    stopRecording(2);
                    this.raiseToEarRecord = false;
                    this.ignoreOnPause = false;
                    if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                        this.proximityWakeLock.release();
                    }
                } else if (this.useFrontSpeaker) {
                    FileLog.e("stop listen");
                    this.useFrontSpeaker = false;
                    startAudioAgain(true);
                    this.ignoreOnPause = false;
                    if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                        this.proximityWakeLock.release();
                    }
                }
            }
            if (this.timeSinceRaise != 0 && this.raisedToBack == 6 && Math.abs(System.currentTimeMillis() - this.timeSinceRaise) > 1000) {
                this.raisedToBack = 0;
                this.raisedToTop = 0;
                this.countLess = 0;
                this.timeSinceRaise = 0;
            }
        }
    }

    public void startRecordingIfFromSpeaker() {
        if (this.useFrontSpeaker && this.raiseChat != null && this.allowStartRecord) {
            this.raiseToEarRecord = true;
            startRecording(this.raiseChat.getDialogId(), null);
            this.ignoreOnPause = true;
        }
    }

    private void startAudioAgain(boolean paused) {
        int i = 0;
        if (this.playingMessageObject != null) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioRouteChanged, Boolean.valueOf(this.useFrontSpeaker));
            if (this.videoPlayer != null) {
                VideoPlayer videoPlayer = this.videoPlayer;
                if (!this.useFrontSpeaker) {
                    i = 3;
                }
                videoPlayer.setStreamType(i);
                if (paused) {
                    this.videoPlayer.pause();
                    return;
                } else {
                    this.videoPlayer.play();
                    return;
                }
            }
            boolean post;
            if (this.audioPlayer != null) {
                post = true;
            } else {
                post = false;
            }
            final MessageObject currentMessageObject = this.playingMessageObject;
            float progress = this.playingMessageObject.audioProgress;
            cleanupPlayer(false, true);
            currentMessageObject.audioProgress = progress;
            playMessage(currentMessageObject);
            if (!paused) {
                return;
            }
            if (post) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        MediaController.this.pauseMessage(currentMessageObject);
                    }
                }, 100);
            } else {
                pauseMessage(currentMessageObject);
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void setInputFieldHasText(boolean value) {
        this.inputFieldHasText = value;
    }

    public void setAllowStartRecord(boolean value) {
        this.allowStartRecord = value;
    }

    public void startRaiseToEarSensors(ChatActivity chatActivity) {
        if (chatActivity == null) {
            return;
        }
        if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null) {
            this.raiseChat = chatActivity;
            if (!this.raiseToSpeak) {
                if (this.playingMessageObject == null) {
                    return;
                }
                if (!(this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo())) {
                    return;
                }
            }
            if (!this.sensorsStarted) {
                float[] fArr = this.gravity;
                float[] fArr2 = this.gravity;
                this.gravity[2] = 0.0f;
                fArr2[1] = 0.0f;
                fArr[0] = 0.0f;
                fArr = this.linearAcceleration;
                fArr2 = this.linearAcceleration;
                this.linearAcceleration[2] = 0.0f;
                fArr2[1] = 0.0f;
                fArr[0] = 0.0f;
                fArr = this.gravityFast;
                fArr2 = this.gravityFast;
                this.gravityFast[2] = 0.0f;
                fArr2[1] = 0.0f;
                fArr[0] = 0.0f;
                this.lastTimestamp = 0;
                this.previousAccValue = 0.0f;
                this.raisedToTop = 0;
                this.countLess = 0;
                this.raisedToBack = 0;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public void run() {
                        if (MediaController.this.gravitySensor != null) {
                            MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.gravitySensor, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS);
                        }
                        if (MediaController.this.linearSensor != null) {
                            MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.linearSensor, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS);
                        }
                        if (MediaController.this.accelerometerSensor != null) {
                            MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.accelerometerSensor, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS);
                        }
                        MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.proximitySensor, 3);
                    }
                });
                this.sensorsStarted = true;
            }
        }
    }

    public void stopRaiseToEarSensors(ChatActivity chatActivity) {
        if (this.ignoreOnPause) {
            this.ignoreOnPause = false;
        } else if (this.sensorsStarted && !this.ignoreOnPause) {
            if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null && this.raiseChat == chatActivity) {
                this.raiseChat = null;
                stopRecording(0);
                this.sensorsStarted = false;
                this.accelerometerVertical = false;
                this.proximityTouched = false;
                this.raiseToEarRecord = false;
                this.useFrontSpeaker = false;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public void run() {
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
                if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    public void cleanupPlayer(boolean notify, boolean stopService) {
        cleanupPlayer(notify, stopService, false);
    }

    public void cleanupPlayer(boolean notify, boolean stopService, boolean byVoiceEnd) {
        if (this.audioPlayer != null) {
            try {
                this.audioPlayer.reset();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            try {
                this.audioPlayer.stop();
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
            try {
                this.audioPlayer.release();
            } catch (Throwable e22) {
                FileLog.e(e22);
            }
            this.audioPlayer = null;
        } else if (this.audioTrackPlayer != null) {
            synchronized (this.playerObjectSync) {
                try {
                    this.audioTrackPlayer.pause();
                    this.audioTrackPlayer.flush();
                } catch (Throwable e222) {
                    FileLog.e(e222);
                }
                try {
                    this.audioTrackPlayer.release();
                } catch (Throwable e2222) {
                    FileLog.e(e2222);
                }
                this.audioTrackPlayer = null;
            }
        } else if (this.videoPlayer != null) {
            this.currentAspectRatioFrameLayout = null;
            this.currentTextureViewContainer = null;
            this.currentAspectRatioFrameLayoutReady = false;
            this.currentTextureView = null;
            this.videoPlayer.releasePlayer();
            this.videoPlayer = null;
            try {
                this.baseActivity.getWindow().clearFlags(128);
            } catch (Throwable e22222) {
                FileLog.e(e22222);
            }
        }
        stopProgressTimer();
        this.lastProgress = 0;
        this.buffersWrited = 0;
        this.isPaused = false;
        if (this.playingMessageObject != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance().cancelLoadFile(this.playingMessageObject.getDocument());
            }
            MessageObject lastFile = this.playingMessageObject;
            this.playingMessageObject.audioProgress = 0.0f;
            this.playingMessageObject.audioProgressSec = 0;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (notify) {
                NotificationsController.getInstance().audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                if (this.voiceMessagesPlaylist != null) {
                    if (byVoiceEnd && this.voiceMessagesPlaylist.get(0) == lastFile) {
                        this.voiceMessagesPlaylist.remove(0);
                        this.voiceMessagesPlaylistMap.remove(Integer.valueOf(lastFile.getId()));
                        if (this.voiceMessagesPlaylist.isEmpty()) {
                            this.voiceMessagesPlaylist = null;
                            this.voiceMessagesPlaylistMap = null;
                        }
                    } else {
                        this.voiceMessagesPlaylist = null;
                        this.voiceMessagesPlaylistMap = null;
                    }
                }
                if (this.voiceMessagesPlaylist != null) {
                    MessageObject nextVoiceMessage = (MessageObject) this.voiceMessagesPlaylist.get(0);
                    playMessage(nextVoiceMessage);
                    if (!(nextVoiceMessage.isRoundVideo() || this.pipRoundVideoView == null)) {
                        this.pipRoundVideoView.close(true);
                        this.pipRoundVideoView = null;
                    }
                } else {
                    if ((lastFile.isVoice() || lastFile.isRoundVideo()) && lastFile.getId() != 0) {
                        startRecordingIfFromSpeaker();
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(lastFile.getId()), Boolean.valueOf(stopService));
                    this.pipSwitchingState = 0;
                    if (this.pipRoundVideoView != null) {
                        this.pipRoundVideoView.close(true);
                        this.pipRoundVideoView = null;
                    }
                }
            }
            if (stopService) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
        }
        if (!this.useFrontSpeaker && !this.raiseToSpeak) {
            ChatActivity chat = this.raiseChat;
            stopRaiseToEarSensors(this.raiseChat);
            this.raiseChat = chat;
        }
    }

    private void seekOpusPlayer(final float progress) {
        if (progress != VOLUME_NORMAL) {
            if (!this.isPaused) {
                this.audioTrackPlayer.pause();
            }
            this.audioTrackPlayer.flush();
            this.fileDecodingQueue.postRunnable(new Runnable() {
                public void run() {
                    MediaController.this.seekOpusFile(progress);
                    synchronized (MediaController.this.playerSync) {
                        MediaController.this.freePlayerBuffers.addAll(MediaController.this.usedPlayerBuffers);
                        MediaController.this.usedPlayerBuffers.clear();
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (!MediaController.this.isPaused) {
                                MediaController.this.ignoreFirstProgress = 3;
                                MediaController.this.lastPlayPcm = (long) (((float) MediaController.this.currentTotalPcmDuration) * progress);
                                if (MediaController.this.audioTrackPlayer != null) {
                                    MediaController.this.audioTrackPlayer.play();
                                }
                                MediaController.this.lastProgress = (long) ((int) ((((float) MediaController.this.currentTotalPcmDuration) / 48.0f) * progress));
                                MediaController.this.checkPlayerQueue();
                            }
                        }
                    });
                }
            });
        }
    }

    public boolean seekToProgress(MessageObject messageObject, float progress) {
        if ((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null) {
            return false;
        }
        if (this.playingMessageObject != null && this.playingMessageObject.getId() != messageObject.getId()) {
            return false;
        }
        try {
            if (this.audioPlayer != null) {
                int seekTo = (int) (((float) this.audioPlayer.getDuration()) * progress);
                this.audioPlayer.seekTo(seekTo);
                this.lastProgress = (long) seekTo;
            } else if (this.audioTrackPlayer != null) {
                seekOpusPlayer(progress);
            } else if (this.videoPlayer != null) {
                this.videoPlayer.seekTo((long) (((float) this.videoPlayer.getDuration()) * progress));
            }
            return true;
        } catch (Throwable e) {
            FileLog.e(e);
            return false;
        }
    }

    public MessageObject getPlayingMessageObject() {
        return this.playingMessageObject;
    }

    public int getPlayingMessageObjectNum() {
        return this.currentPlaylistNum;
    }

    private void buildShuffledPlayList() {
        if (!this.playlist.isEmpty()) {
            ArrayList<MessageObject> all = new ArrayList(this.playlist);
            this.shuffledPlaylist.clear();
            MessageObject messageObject = (MessageObject) this.playlist.get(this.currentPlaylistNum);
            all.remove(this.currentPlaylistNum);
            this.shuffledPlaylist.add(messageObject);
            int count = all.size();
            for (int a = 0; a < count; a++) {
                int index = Utilities.random.nextInt(all.size());
                this.shuffledPlaylist.add(all.get(index));
                all.remove(index);
            }
        }
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current) {
        return setPlaylist(messageObjects, current, true);
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current, boolean loadMusic) {
        boolean z = true;
        if (this.playingMessageObject == current) {
            return playMessage(current);
        }
        boolean z2;
        if (loadMusic) {
            z2 = false;
        } else {
            z2 = true;
        }
        this.forceLoopCurrentPlaylist = z2;
        if (this.playlist.isEmpty()) {
            z = false;
        }
        this.playMusicAgain = z;
        this.playlist.clear();
        for (int a = messageObjects.size() - 1; a >= 0; a--) {
            MessageObject messageObject = (MessageObject) messageObjects.get(a);
            if (messageObject.isMusic()) {
                this.playlist.add(messageObject);
            }
        }
        this.currentPlaylistNum = this.playlist.indexOf(current);
        if (this.currentPlaylistNum == -1) {
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.currentPlaylistNum = this.playlist.size();
            this.playlist.add(current);
        }
        if (current.isMusic()) {
            if (this.shuffleMusic) {
                buildShuffledPlayList();
                this.currentPlaylistNum = 0;
            }
            if (loadMusic) {
                SharedMediaQuery.loadMusic(current.getDialogId(), ((MessageObject) this.playlist.get(0)).getId());
            }
        }
        return playMessage(current);
    }

    public void playNextMessage() {
        playNextMessage(false);
    }

    public void playMessageAtIndex(int index) {
        if (this.currentPlaylistNum >= 0 && this.currentPlaylistNum < this.playlist.size()) {
            this.currentPlaylistNum = index;
            this.playMusicAgain = true;
            playMessage((MessageObject) this.playlist.get(this.currentPlaylistNum));
        }
    }

    private void playNextMessage(boolean byStop) {
        ArrayList<MessageObject> currentPlayList = this.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (byStop && this.repeatMode == 2 && !this.forceLoopCurrentPlaylist) {
            cleanupPlayer(false, false);
            playMessage((MessageObject) currentPlayList.get(this.currentPlaylistNum));
            return;
        }
        this.currentPlaylistNum++;
        if (this.currentPlaylistNum >= currentPlayList.size()) {
            this.currentPlaylistNum = 0;
            if (byStop && this.repeatMode == 0 && !this.forceLoopCurrentPlaylist) {
                if (this.audioPlayer != null || this.audioTrackPlayer != null || this.videoPlayer != null) {
                    if (this.audioPlayer != null) {
                        try {
                            this.audioPlayer.reset();
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                        try {
                            this.audioPlayer.stop();
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                        }
                        try {
                            this.audioPlayer.release();
                        } catch (Throwable e22) {
                            FileLog.e(e22);
                        }
                        this.audioPlayer = null;
                    } else if (this.audioTrackPlayer != null) {
                        synchronized (this.playerObjectSync) {
                            try {
                                this.audioTrackPlayer.pause();
                                this.audioTrackPlayer.flush();
                            } catch (Throwable e222) {
                                FileLog.e(e222);
                            }
                            try {
                                this.audioTrackPlayer.release();
                            } catch (Throwable e2222) {
                                FileLog.e(e2222);
                            }
                            this.audioTrackPlayer = null;
                        }
                    } else if (this.videoPlayer != null) {
                        this.currentAspectRatioFrameLayout = null;
                        this.currentTextureViewContainer = null;
                        this.currentAspectRatioFrameLayoutReady = false;
                        this.currentTextureView = null;
                        this.videoPlayer.releasePlayer();
                        this.videoPlayer = null;
                        try {
                            this.baseActivity.getWindow().clearFlags(128);
                        } catch (Throwable e22222) {
                            FileLog.e(e22222);
                        }
                    }
                    stopProgressTimer();
                    this.lastProgress = 0;
                    this.buffersWrited = 0;
                    this.isPaused = true;
                    this.playingMessageObject.audioProgress = 0.0f;
                    this.playingMessageObject.audioProgressSec = 0;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
                    return;
                }
                return;
            }
        }
        if (this.currentPlaylistNum >= 0 && this.currentPlaylistNum < currentPlayList.size()) {
            this.playMusicAgain = true;
            playMessage((MessageObject) currentPlayList.get(this.currentPlaylistNum));
        }
    }

    public void playPreviousMessage() {
        ArrayList<MessageObject> currentPlayList = this.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!currentPlayList.isEmpty()) {
            MessageObject currentSong = (MessageObject) currentPlayList.get(this.currentPlaylistNum);
            if (currentSong.audioProgressSec > 10) {
                getInstance().seekToProgress(currentSong, 0.0f);
                return;
            }
            this.currentPlaylistNum--;
            if (this.currentPlaylistNum < 0) {
                this.currentPlaylistNum = currentPlayList.size() - 1;
            }
            if (this.currentPlaylistNum >= 0 && this.currentPlaylistNum < currentPlayList.size()) {
                this.playMusicAgain = true;
                playMessage((MessageObject) currentPlayList.get(this.currentPlaylistNum));
            }
        }
    }

    private void checkIsNextVoiceFileDownloaded() {
        if (this.voiceMessagesPlaylist != null && this.voiceMessagesPlaylist.size() >= 2) {
            MessageObject nextAudio = (MessageObject) this.voiceMessagesPlaylist.get(true);
            File file = null;
            if (nextAudio.messageOwner.attachPath != null && nextAudio.messageOwner.attachPath.length() > 0) {
                file = new File(nextAudio.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            File cacheFile = file != null ? file : FileLoader.getPathToMessage(nextAudio.messageOwner);
            if (cacheFile == null || !cacheFile.exists()) {
                boolean z = false;
            }
            if (cacheFile != null && cacheFile != file && !cacheFile.exists()) {
                FileLoader.getInstance().loadFile(nextAudio.getDocument(), false, false);
            }
        }
    }

    private void checkIsNextMusicFileDownloaded() {
        if ((getCurrentDownloadMask() & 16) != 0) {
            ArrayList<MessageObject> currentPlayList = this.shuffleMusic ? this.shuffledPlaylist : this.playlist;
            if (currentPlayList != null && currentPlayList.size() >= 2) {
                int nextIndex = this.currentPlaylistNum + 1;
                if (nextIndex >= currentPlayList.size()) {
                    nextIndex = 0;
                }
                MessageObject nextAudio = (MessageObject) currentPlayList.get(nextIndex);
                File file = null;
                if (nextAudio.messageOwner.attachPath != null && nextAudio.messageOwner.attachPath.length() > 0) {
                    file = new File(nextAudio.messageOwner.attachPath);
                    if (!file.exists()) {
                        file = null;
                    }
                }
                File cacheFile = file != null ? file : FileLoader.getPathToMessage(nextAudio.messageOwner);
                if (cacheFile == null || !cacheFile.exists()) {
                    boolean z = false;
                }
                if (cacheFile != null && cacheFile != file && !cacheFile.exists() && nextAudio.isMusic()) {
                    FileLoader.getInstance().loadFile(nextAudio.getDocument(), false, false);
                }
            }
        }
    }

    public void setVoiceMessagesPlaylist(ArrayList<MessageObject> playlist, boolean unread) {
        this.voiceMessagesPlaylist = playlist;
        if (this.voiceMessagesPlaylist != null) {
            this.voiceMessagesPlaylistUnread = unread;
            this.voiceMessagesPlaylistMap = new HashMap();
            for (int a = 0; a < this.voiceMessagesPlaylist.size(); a++) {
                MessageObject messageObject = (MessageObject) this.voiceMessagesPlaylist.get(a);
                this.voiceMessagesPlaylistMap.put(Integer.valueOf(messageObject.getId()), messageObject);
            }
        }
    }

    private void checkAudioFocus(MessageObject messageObject) {
        int neededAudioFocus;
        if (!messageObject.isVoice() && !messageObject.isRoundVideo()) {
            neededAudioFocus = 1;
        } else if (this.useFrontSpeaker) {
            neededAudioFocus = 3;
        } else {
            neededAudioFocus = 2;
        }
        if (this.hasAudioFocus != neededAudioFocus) {
            int result;
            this.hasAudioFocus = neededAudioFocus;
            if (neededAudioFocus == 3) {
                result = NotificationsController.getInstance().audioManager.requestAudioFocus(this, 0, 1);
            } else {
                result = NotificationsController.getInstance().audioManager.requestAudioFocus(this, 3, neededAudioFocus == 2 ? 3 : 1);
            }
            if (result == 1) {
                this.audioFocus = 2;
            }
        }
    }

    public void setCurrentRoundVisible(boolean visible) {
        if (this.currentAspectRatioFrameLayout != null) {
            if (visible) {
                if (this.pipRoundVideoView != null) {
                    this.pipSwitchingState = 2;
                    this.pipRoundVideoView.close(true);
                    this.pipRoundVideoView = null;
                } else if (this.currentAspectRatioFrameLayout != null) {
                    if (this.currentAspectRatioFrameLayout.getParent() == null) {
                        this.currentTextureViewContainer.addView(this.currentAspectRatioFrameLayout);
                    }
                    this.videoPlayer.setTextureView(this.currentTextureView);
                }
            } else if (this.currentAspectRatioFrameLayout.getParent() != null) {
                this.pipSwitchingState = 1;
                this.currentTextureViewContainer.removeView(this.currentAspectRatioFrameLayout);
            } else {
                if (this.pipRoundVideoView == null) {
                    try {
                        this.pipRoundVideoView = new PipRoundVideoView();
                        this.pipRoundVideoView.show(this.baseActivity, new Runnable() {
                            public void run() {
                                MediaController.this.cleanupPlayer(true, true);
                            }
                        });
                    } catch (Exception e) {
                        this.pipRoundVideoView = null;
                    }
                }
                if (this.pipRoundVideoView != null) {
                    this.videoPlayer.setTextureView(this.pipRoundVideoView.getTextureView());
                }
            }
        }
    }

    public void setTextureView(TextureView textureView, AspectRatioFrameLayout aspectRatioFrameLayout, FrameLayout container, boolean set) {
        boolean z = true;
        if (!set && this.currentTextureView == textureView) {
            this.pipSwitchingState = 1;
            this.currentTextureView = null;
            this.currentAspectRatioFrameLayout = null;
            this.currentTextureViewContainer = null;
        } else if (this.videoPlayer != null && textureView != this.currentTextureView) {
            if (aspectRatioFrameLayout == null || !aspectRatioFrameLayout.isDrawingReady()) {
                z = false;
            }
            this.isDrawingWasReady = z;
            this.currentTextureView = textureView;
            if (this.pipRoundVideoView != null) {
                this.videoPlayer.setTextureView(this.pipRoundVideoView.getTextureView());
            } else {
                this.videoPlayer.setTextureView(this.currentTextureView);
            }
            this.currentAspectRatioFrameLayout = aspectRatioFrameLayout;
            this.currentTextureViewContainer = container;
            if (this.currentAspectRatioFrameLayoutReady && this.currentAspectRatioFrameLayout != null) {
                if (this.currentAspectRatioFrameLayout != null) {
                    this.currentAspectRatioFrameLayout.setAspectRatio(this.currentAspectRatioFrameLayoutRatio, this.currentAspectRatioFrameLayoutRotation);
                }
                if (this.currentTextureViewContainer.getVisibility() != 0) {
                    this.currentTextureViewContainer.setVisibility(0);
                }
            }
        }
    }

    public void setBaseActivity(Activity activity, boolean set) {
        if (set) {
            this.baseActivity = activity;
        } else if (this.baseActivity == activity) {
            this.baseActivity = null;
        }
    }

    public boolean playMessage(MessageObject messageObject) {
        if (messageObject == null) {
            return false;
        }
        if ((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || this.playingMessageObject == null || messageObject.getId() != this.playingMessageObject.getId()) {
            File cacheFile;
            if (!messageObject.isOut() && messageObject.isContentUnread() && messageObject.messageOwner.to_id.channel_id == 0) {
                MessagesController.getInstance().markMessageContentAsRead(messageObject);
            }
            boolean notify = !this.playMusicAgain;
            if (this.playingMessageObject != null) {
                notify = false;
            }
            cleanupPlayer(notify, false);
            this.playMusicAgain = false;
            File file = null;
            if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
                file = new File(messageObject.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file != null) {
                cacheFile = file;
            } else {
                cacheFile = FileLoader.getPathToMessage(messageObject.messageOwner);
            }
            if (cacheFile == null || cacheFile == file || cacheFile.exists()) {
                this.downloadingCurrentMessage = false;
                if (messageObject.isMusic()) {
                    checkIsNextMusicFileDownloaded();
                } else {
                    checkIsNextVoiceFileDownloaded();
                }
                if (this.currentAspectRatioFrameLayout != null) {
                    this.isDrawingWasReady = false;
                    this.currentAspectRatioFrameLayout.setDrawingReady(false);
                }
                if (messageObject.isRoundVideo()) {
                    this.playlist.clear();
                    this.shuffledPlaylist.clear();
                    this.videoPlayer = new VideoPlayer();
                    this.videoPlayer.setDelegate(new VideoPlayerDelegate() {
                        public void onStateChanged(boolean playWhenReady, int playbackState) {
                            if (MediaController.this.videoPlayer != null) {
                                if (playbackState == 4 || playbackState == 1) {
                                    try {
                                        MediaController.this.baseActivity.getWindow().clearFlags(128);
                                    } catch (Throwable e) {
                                        FileLog.e(e);
                                    }
                                } else {
                                    try {
                                        MediaController.this.baseActivity.getWindow().addFlags(128);
                                    } catch (Throwable e2) {
                                        FileLog.e(e2);
                                    }
                                }
                                if (playbackState == 3) {
                                    MediaController.this.currentAspectRatioFrameLayoutReady = true;
                                    if (MediaController.this.currentTextureViewContainer != null && MediaController.this.currentTextureViewContainer.getVisibility() != 0) {
                                        MediaController.this.currentTextureViewContainer.setVisibility(0);
                                    }
                                } else if (MediaController.this.videoPlayer.isPlaying() && playbackState == 4) {
                                    MediaController.this.cleanupPlayer(true, true, true);
                                }
                            }
                        }

                        public void onError(Exception e) {
                            FileLog.e((Throwable) e);
                        }

                        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                            MediaController.this.currentAspectRatioFrameLayoutRotation = unappliedRotationDegrees;
                            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                                int temp = width;
                                width = height;
                                height = temp;
                            }
                            MediaController.this.currentAspectRatioFrameLayoutRatio = height == 0 ? MediaController.VOLUME_NORMAL : (((float) width) * pixelWidthHeightRatio) / ((float) height);
                            if (MediaController.this.currentAspectRatioFrameLayout != null) {
                                MediaController.this.currentAspectRatioFrameLayout.setAspectRatio(MediaController.this.currentAspectRatioFrameLayoutRatio, MediaController.this.currentAspectRatioFrameLayoutRotation);
                            }
                        }

                        public void onRenderedFirstFrame() {
                            if (MediaController.this.currentAspectRatioFrameLayout != null && !MediaController.this.currentAspectRatioFrameLayout.isDrawingReady()) {
                                MediaController.this.isDrawingWasReady = true;
                                MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                                if (MediaController.this.currentTextureViewContainer != null && MediaController.this.currentTextureViewContainer.getVisibility() != 0) {
                                    MediaController.this.currentTextureViewContainer.setVisibility(0);
                                }
                            }
                        }

                        @TargetApi(16)
                        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                            if (MediaController.this.videoPlayer == null) {
                                return false;
                            }
                            if (MediaController.this.pipSwitchingState == 2) {
                                if (MediaController.this.currentAspectRatioFrameLayout != null) {
                                    if (MediaController.this.isDrawingWasReady) {
                                        MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                                    }
                                    if (MediaController.this.currentAspectRatioFrameLayout.getParent() == null) {
                                        MediaController.this.currentTextureViewContainer.addView(MediaController.this.currentAspectRatioFrameLayout);
                                    }
                                    if (MediaController.this.currentTextureView.getSurfaceTexture() != surfaceTexture) {
                                        MediaController.this.currentTextureView.setSurfaceTexture(surfaceTexture);
                                    }
                                    MediaController.this.videoPlayer.setTextureView(MediaController.this.currentTextureView);
                                }
                                MediaController.this.pipSwitchingState = 0;
                                return true;
                            } else if (MediaController.this.pipSwitchingState != 1) {
                                return false;
                            } else {
                                if (MediaController.this.baseActivity != null) {
                                    if (MediaController.this.pipRoundVideoView == null) {
                                        try {
                                            MediaController.this.pipRoundVideoView = new PipRoundVideoView();
                                            MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new Runnable() {
                                                public void run() {
                                                    MediaController.this.cleanupPlayer(true, true);
                                                }
                                            });
                                        } catch (Exception e) {
                                            MediaController.this.pipRoundVideoView = null;
                                        }
                                    }
                                    if (MediaController.this.pipRoundVideoView != null) {
                                        if (MediaController.this.pipRoundVideoView.getTextureView().getSurfaceTexture() != surfaceTexture) {
                                            MediaController.this.pipRoundVideoView.getTextureView().setSurfaceTexture(surfaceTexture);
                                        }
                                        MediaController.this.videoPlayer.setTextureView(MediaController.this.pipRoundVideoView.getTextureView());
                                    }
                                }
                                MediaController.this.pipSwitchingState = 0;
                                return true;
                            }
                        }

                        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                        }
                    });
                    this.currentAspectRatioFrameLayoutReady = false;
                    if (this.pipRoundVideoView != null || !MessagesController.getInstance().isDialogCreated(messageObject.getDialogId())) {
                        if (this.pipRoundVideoView == null) {
                            try {
                                this.pipRoundVideoView = new PipRoundVideoView();
                                this.pipRoundVideoView.show(this.baseActivity, new Runnable() {
                                    public void run() {
                                        MediaController.this.cleanupPlayer(true, true);
                                    }
                                });
                            } catch (Exception e) {
                                this.pipRoundVideoView = null;
                            }
                        }
                        if (this.pipRoundVideoView != null) {
                            this.videoPlayer.setTextureView(this.pipRoundVideoView.getTextureView());
                        }
                    } else if (this.currentTextureView != null) {
                        this.videoPlayer.setTextureView(this.currentTextureView);
                    }
                    this.videoPlayer.preparePlayer(Uri.fromFile(cacheFile), "other");
                    this.videoPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                    this.videoPlayer.play();
                } else if (isOpusFile(cacheFile.getAbsolutePath()) == 1) {
                    this.playlist.clear();
                    this.shuffledPlaylist.clear();
                    synchronized (this.playerObjectSync) {
                        try {
                            this.ignoreFirstProgress = 3;
                            Semaphore semaphore = new Semaphore(0);
                            final Boolean[] result = new Boolean[1];
                            final Semaphore semaphore2 = semaphore;
                            this.fileDecodingQueue.postRunnable(new Runnable() {
                                public void run() {
                                    boolean z;
                                    Boolean[] boolArr = result;
                                    if (MediaController.this.openOpusFile(cacheFile.getAbsolutePath()) != 0) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    boolArr[0] = Boolean.valueOf(z);
                                    semaphore2.release();
                                }
                            });
                            semaphore.acquire();
                            if (result[0].booleanValue()) {
                                this.currentTotalPcmDuration = getTotalPcmDuration();
                                this.audioTrackPlayer = new AudioTrack(this.useFrontSpeaker ? 0 : 3, 48000, 4, 2, this.playerBufferSize, 1);
                                this.audioTrackPlayer.setStereoVolume(VOLUME_NORMAL, VOLUME_NORMAL);
                                this.audioTrackPlayer.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
                                    public void onMarkerReached(AudioTrack audioTrack) {
                                        MediaController.this.cleanupPlayer(true, true, true);
                                    }

                                    public void onPeriodicNotification(AudioTrack audioTrack) {
                                    }
                                });
                                this.audioTrackPlayer.play();
                            } else {
                                return false;
                            }
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                            if (this.audioTrackPlayer != null) {
                                this.audioTrackPlayer.release();
                                this.audioTrackPlayer = null;
                                this.isPaused = false;
                                this.playingMessageObject = null;
                                this.downloadingCurrentMessage = false;
                            }
                            return false;
                        }
                    }
                } else {
                    try {
                        this.audioPlayer = new MediaPlayer();
                        this.audioPlayer.setAudioStreamType(this.useFrontSpeaker ? 0 : 3);
                        this.audioPlayer.setDataSource(cacheFile.getAbsolutePath());
                        final MessageObject messageObject2 = messageObject;
                        this.audioPlayer.setOnCompletionListener(new OnCompletionListener() {
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                if (MediaController.this.playlist.isEmpty() || MediaController.this.playlist.size() <= 1) {
                                    MediaController mediaController = MediaController.this;
                                    boolean z = messageObject2 != null && messageObject2.isVoice();
                                    mediaController.cleanupPlayer(true, true, z);
                                    return;
                                }
                                MediaController.this.playNextMessage(true);
                            }
                        });
                        this.audioPlayer.prepare();
                        this.audioPlayer.start();
                        if (messageObject.isVoice()) {
                            this.audioInfo = null;
                            this.playlist.clear();
                            this.shuffledPlaylist.clear();
                        } else {
                            try {
                                this.audioInfo = AudioInfo.getAudioInfo(cacheFile);
                            } catch (Throwable e22) {
                                FileLog.e(e22);
                            }
                        }
                    } catch (Throwable e222) {
                        FileLog.e(e222);
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.messagePlayingPlayStateChanged;
                        Object[] objArr = new Object[1];
                        objArr[0] = Integer.valueOf(this.playingMessageObject != null ? this.playingMessageObject.getId() : 0);
                        instance.postNotificationName(i, objArr);
                        if (this.audioPlayer != null) {
                            this.audioPlayer.release();
                            this.audioPlayer = null;
                            this.isPaused = false;
                            this.playingMessageObject = null;
                            this.downloadingCurrentMessage = false;
                        }
                        return false;
                    }
                }
                checkAudioFocus(messageObject);
                setPlayerVolume();
                this.isPaused = false;
                this.lastProgress = 0;
                this.lastPlayPcm = 0;
                this.playingMessageObject = messageObject;
                if (!this.raiseToSpeak) {
                    startRaiseToEarSensors(this.raiseChat);
                }
                startProgressTimer(this.playingMessageObject);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingDidStarted, messageObject);
                if (this.videoPlayer != null) {
                    try {
                        if (this.playingMessageObject.audioProgress != 0.0f) {
                            this.videoPlayer.seekTo((long) ((int) (((float) this.videoPlayer.getDuration()) * this.playingMessageObject.audioProgress)));
                        }
                    } catch (Throwable e23) {
                        this.playingMessageObject.audioProgress = 0.0f;
                        this.playingMessageObject.audioProgressSec = 0;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                        FileLog.e(e23);
                    }
                } else if (this.audioPlayer != null) {
                    try {
                        if (this.playingMessageObject.audioProgress != 0.0f) {
                            this.audioPlayer.seekTo((int) (((float) this.audioPlayer.getDuration()) * this.playingMessageObject.audioProgress));
                        }
                    } catch (Throwable e232) {
                        this.playingMessageObject.audioProgress = 0.0f;
                        this.playingMessageObject.audioProgressSec = 0;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                        FileLog.e(e232);
                    }
                } else if (this.audioTrackPlayer != null) {
                    if (this.playingMessageObject.audioProgress == VOLUME_NORMAL) {
                        this.playingMessageObject.audioProgress = 0.0f;
                    }
                    this.fileDecodingQueue.postRunnable(new Runnable() {
                        public void run() {
                            try {
                                if (!(MediaController.this.playingMessageObject == null || MediaController.this.playingMessageObject.audioProgress == 0.0f)) {
                                    MediaController.this.lastPlayPcm = (long) (((float) MediaController.this.currentTotalPcmDuration) * MediaController.this.playingMessageObject.audioProgress);
                                    MediaController.this.seekOpusFile(MediaController.this.playingMessageObject.audioProgress);
                                }
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                            synchronized (MediaController.this.playerSync) {
                                MediaController.this.freePlayerBuffers.addAll(MediaController.this.usedPlayerBuffers);
                                MediaController.this.usedPlayerBuffers.clear();
                            }
                            MediaController.this.decodingFinished = false;
                            MediaController.this.checkPlayerQueue();
                        }
                    });
                }
                if (this.playingMessageObject.isMusic()) {
                    ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                } else {
                    ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                }
                return true;
            }
            FileLoader.getInstance().loadFile(messageObject.getDocument(), false, false);
            this.downloadingCurrentMessage = true;
            this.isPaused = false;
            this.lastProgress = 0;
            this.lastPlayPcm = 0;
            this.audioInfo = null;
            this.playingMessageObject = messageObject;
            if (this.playingMessageObject.getDocument() != null) {
                ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            } else {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        }
        if (this.isPaused) {
            resumeAudio(messageObject);
        }
        if (!this.raiseToSpeak) {
            startRaiseToEarSensors(this.raiseChat);
        }
        return true;
    }

    public void stopAudio() {
        if ((this.audioTrackPlayer != null || this.audioPlayer != null || this.videoPlayer != null) && this.playingMessageObject != null) {
            try {
                if (this.audioPlayer != null) {
                    try {
                        this.audioPlayer.reset();
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    this.audioPlayer.stop();
                    try {
                        if (this.audioPlayer != null) {
                            this.audioPlayer.release();
                            this.audioPlayer = null;
                        } else if (this.audioTrackPlayer != null) {
                            synchronized (this.playerObjectSync) {
                                this.audioTrackPlayer.release();
                                this.audioTrackPlayer = null;
                            }
                        } else if (this.videoPlayer != null) {
                            this.currentAspectRatioFrameLayout = null;
                            this.currentTextureViewContainer = null;
                            this.currentAspectRatioFrameLayoutReady = false;
                            this.currentTextureView = null;
                            this.videoPlayer.releasePlayer();
                            this.videoPlayer = null;
                            try {
                                this.baseActivity.getWindow().clearFlags(128);
                            } catch (Throwable e2) {
                                FileLog.e(e2);
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.e(e22);
                    }
                    stopProgressTimer();
                    this.playingMessageObject = null;
                    this.downloadingCurrentMessage = false;
                    this.isPaused = false;
                    ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                }
                if (this.audioTrackPlayer != null) {
                    this.audioTrackPlayer.pause();
                    this.audioTrackPlayer.flush();
                } else if (this.videoPlayer != null) {
                    this.videoPlayer.pause();
                }
                if (this.audioPlayer != null) {
                    this.audioPlayer.release();
                    this.audioPlayer = null;
                } else if (this.audioTrackPlayer != null) {
                    synchronized (this.playerObjectSync) {
                        this.audioTrackPlayer.release();
                        this.audioTrackPlayer = null;
                    }
                } else if (this.videoPlayer != null) {
                    this.currentAspectRatioFrameLayout = null;
                    this.currentTextureViewContainer = null;
                    this.currentAspectRatioFrameLayoutReady = false;
                    this.currentTextureView = null;
                    this.videoPlayer.releasePlayer();
                    this.videoPlayer = null;
                    this.baseActivity.getWindow().clearFlags(128);
                }
                stopProgressTimer();
                this.playingMessageObject = null;
                this.downloadingCurrentMessage = false;
                this.isPaused = false;
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            } catch (Throwable e222) {
                FileLog.e(e222);
            }
        }
    }

    public AudioInfo getAudioInfo() {
        return this.audioInfo;
    }

    public boolean isShuffleMusic() {
        return this.shuffleMusic;
    }

    public int getRepeatMode() {
        return this.repeatMode;
    }

    public void toggleShuffleMusic() {
        boolean z;
        if (this.shuffleMusic) {
            z = false;
        } else {
            z = true;
        }
        this.shuffleMusic = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        editor.putBoolean("shuffleMusic", this.shuffleMusic);
        editor.commit();
        if (this.shuffleMusic) {
            buildShuffledPlayList();
            this.currentPlaylistNum = 0;
        } else if (this.playingMessageObject != null) {
            this.currentPlaylistNum = this.playlist.indexOf(this.playingMessageObject);
            if (this.currentPlaylistNum == -1) {
                this.playlist.clear();
                this.shuffledPlaylist.clear();
                cleanupPlayer(true, true);
            }
        }
    }

    public void toggleRepeatMode() {
        this.repeatMode++;
        if (this.repeatMode > 2) {
            this.repeatMode = 0;
        }
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        editor.putInt("repeatMode", this.repeatMode);
        editor.commit();
    }

    public boolean pauseMessage(MessageObject messageObject) {
        if ((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null) {
            return false;
        }
        if (this.playingMessageObject != null && this.playingMessageObject.getId() != messageObject.getId()) {
            return false;
        }
        stopProgressTimer();
        try {
            if (this.audioPlayer != null) {
                this.audioPlayer.pause();
            } else if (this.audioTrackPlayer != null) {
                this.audioTrackPlayer.pause();
            } else if (this.videoPlayer != null) {
                this.videoPlayer.pause();
            }
            this.isPaused = true;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        } catch (Throwable e) {
            FileLog.e(e);
            this.isPaused = false;
            return false;
        }
    }

    public boolean resumeAudio(MessageObject messageObject) {
        if ((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null) {
            return false;
        }
        if (this.playingMessageObject != null && this.playingMessageObject.getId() != messageObject.getId()) {
            return false;
        }
        try {
            startProgressTimer(this.playingMessageObject);
            if (this.audioPlayer != null) {
                this.audioPlayer.start();
            } else if (this.audioTrackPlayer != null) {
                this.audioTrackPlayer.play();
                checkPlayerQueue();
            } else if (this.videoPlayer != null) {
                this.videoPlayer.play();
            }
            checkAudioFocus(messageObject);
            this.isPaused = false;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        } catch (Throwable e) {
            FileLog.e(e);
            return false;
        }
    }

    public boolean isRoundVideoDrawingReady() {
        return this.currentAspectRatioFrameLayout != null && this.currentAspectRatioFrameLayout.isDrawingReady();
    }

    public boolean isPlayingMessage(MessageObject messageObject) {
        return ((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || (this.playingMessageObject != null && (this.playingMessageObject.eventId != messageObject.eventId || this.playingMessageObject.getId() != messageObject.getId() || this.downloadingCurrentMessage))) ? false : true;
    }

    public boolean isMessagePaused() {
        return this.isPaused || this.downloadingCurrentMessage;
    }

    public boolean isDownloadingCurrentMessage() {
        return this.downloadingCurrentMessage;
    }

    public void startRecording(final long dialog_id, final MessageObject reply_to_msg) {
        long j = 50;
        boolean paused = false;
        if (!(this.playingMessageObject == null || !isPlayingMessage(this.playingMessageObject) || isMessagePaused())) {
            paused = true;
            pauseMessage(this.playingMessageObject);
        }
        try {
            ((Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        Runnable anonymousClass20 = new Runnable() {
            public void run() {
                if (MediaController.this.audioRecorder != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MediaController.this.recordStartRunnable = null;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                        }
                    });
                    return;
                }
                MediaController.this.recordingAudio = new TL_document();
                MediaController.this.recordingAudio.dc_id = Integer.MIN_VALUE;
                MediaController.this.recordingAudio.id = (long) UserConfig.lastLocalId;
                MediaController.this.recordingAudio.user_id = UserConfig.getClientUserId();
                MediaController.this.recordingAudio.mime_type = "audio/ogg";
                MediaController.this.recordingAudio.thumb = new TL_photoSizeEmpty();
                MediaController.this.recordingAudio.thumb.type = "s";
                UserConfig.lastLocalId--;
                UserConfig.saveConfig(false);
                MediaController.this.recordingAudioFile = new File(FileLoader.getInstance().getDirectory(4), FileLoader.getAttachFileName(MediaController.this.recordingAudio));
                try {
                    if (MediaController.this.startRecord(MediaController.this.recordingAudioFile.getAbsolutePath()) == 0) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MediaController.this.recordStartRunnable = null;
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                            }
                        });
                        return;
                    }
                    MediaController.this.audioRecorder = new AudioRecord(1, 16000, 16, 2, MediaController.this.recordBufferSize * 10);
                    MediaController.this.recordStartTime = System.currentTimeMillis();
                    MediaController.this.recordTimeCount = 0;
                    MediaController.this.samplesCount = 0;
                    MediaController.this.recordDialogId = dialog_id;
                    MediaController.this.recordReplyingMessageObject = reply_to_msg;
                    MediaController.this.fileBuffer.rewind();
                    MediaController.this.audioRecorder.startRecording();
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MediaController.this.recordStartRunnable = null;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[0]);
                        }
                    });
                } catch (Throwable e) {
                    FileLog.e(e);
                    MediaController.this.recordingAudio = null;
                    MediaController.this.stopRecord();
                    MediaController.this.recordingAudioFile.delete();
                    MediaController.this.recordingAudioFile = null;
                    try {
                        MediaController.this.audioRecorder.release();
                        MediaController.this.audioRecorder = null;
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MediaController.this.recordStartRunnable = null;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                        }
                    });
                }
            }
        };
        this.recordStartRunnable = anonymousClass20;
        if (paused) {
            j = 500;
        }
        dispatchQueue.postRunnable(anonymousClass20, j);
    }

    public void generateWaveform(MessageObject messageObject) {
        final String id = messageObject.getId() + "_" + messageObject.getDialogId();
        final String path = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(id)) {
            this.generatingWaveform.put(id, messageObject);
            Utilities.globalQueue.postRunnable(new Runnable() {
                public void run() {
                    final byte[] waveform = MediaController.getInstance().getWaveform(path);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessageObject messageObject = (MessageObject) MediaController.this.generatingWaveform.remove(id);
                            if (messageObject != null && waveform != null) {
                                for (int a = 0; a < messageObject.getDocument().attributes.size(); a++) {
                                    DocumentAttribute attribute = (DocumentAttribute) messageObject.getDocument().attributes.get(a);
                                    if (attribute instanceof TL_documentAttributeAudio) {
                                        attribute.waveform = waveform;
                                        attribute.flags |= 4;
                                        break;
                                    }
                                }
                                messages_Messages messagesRes = new TL_messages_messages();
                                messagesRes.messages.add(messageObject.messageOwner);
                                MessagesStorage.getInstance().putMessages(messagesRes, messageObject.getDialogId(), -1, 0, false);
                                new ArrayList().add(messageObject);
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList);
                            }
                        }
                    });
                }
            });
        }
    }

    private void stopRecordingInternal(final int send) {
        if (send != 0) {
            final TL_document audioToSend = this.recordingAudio;
            final File recordingAudioFileToSend = this.recordingAudioFile;
            this.fileEncodingQueue.postRunnable(new Runnable() {
                public void run() {
                    MediaController.this.stopRecord();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            VideoEditedInfo videoEditedInfo = null;
                            audioToSend.date = ConnectionsManager.getInstance().getCurrentTime();
                            audioToSend.size = (int) recordingAudioFileToSend.length();
                            TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                            attributeAudio.voice = true;
                            attributeAudio.waveform = MediaController.this.getWaveform2(MediaController.this.recordSamples, MediaController.this.recordSamples.length);
                            if (attributeAudio.waveform != null) {
                                attributeAudio.flags |= 4;
                            }
                            long duration = MediaController.this.recordTimeCount;
                            attributeAudio.duration = (int) (MediaController.this.recordTimeCount / 1000);
                            audioToSend.attributes.add(attributeAudio);
                            if (duration > 700) {
                                TL_document tL_document;
                                if (send == 1) {
                                    SendMessagesHelper.getInstance().sendMessage(audioToSend, null, recordingAudioFileToSend.getAbsolutePath(), MediaController.this.recordDialogId, MediaController.this.recordReplyingMessageObject, null, null);
                                }
                                NotificationCenter instance = NotificationCenter.getInstance();
                                int i = NotificationCenter.audioDidSent;
                                Object[] objArr = new Object[2];
                                if (send == 2) {
                                    tL_document = audioToSend;
                                } else {
                                    tL_document = null;
                                }
                                objArr[0] = tL_document;
                                if (send == 2) {
                                    videoEditedInfo = recordingAudioFileToSend.getAbsolutePath();
                                }
                                objArr[1] = videoEditedInfo;
                                instance.postNotificationName(i, objArr);
                                return;
                            }
                            recordingAudioFileToSend.delete();
                        }
                    });
                }
            });
        }
        try {
            if (this.audioRecorder != null) {
                this.audioRecorder.release();
                this.audioRecorder = null;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        this.recordingAudio = null;
        this.recordingAudioFile = null;
    }

    public void stopRecording(final int send) {
        if (this.recordStartRunnable != null) {
            this.recordQueue.cancelRunnable(this.recordStartRunnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new Runnable() {
            public void run() {
                if (MediaController.this.audioRecorder != null) {
                    try {
                        MediaController.this.sendAfterDone = send;
                        MediaController.this.audioRecorder.stop();
                    } catch (Throwable e) {
                        FileLog.e(e);
                        if (MediaController.this.recordingAudioFile != null) {
                            MediaController.this.recordingAudioFile.delete();
                        }
                    }
                    if (send == 0) {
                        MediaController.this.stopRecordingInternal(0);
                    }
                    try {
                        ((Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50);
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            int i = 1;
                            NotificationCenter instance = NotificationCenter.getInstance();
                            int i2 = NotificationCenter.recordStopped;
                            Object[] objArr = new Object[1];
                            if (send != 2) {
                                i = 0;
                            }
                            objArr[0] = Integer.valueOf(i);
                            instance.postNotificationName(i2, objArr);
                        }
                    });
                }
            }
        });
    }

    public static void saveFile(String fullPath, Context context, int type, String name, String mime) {
        Throwable e;
        final AlertDialog finalProgress;
        final int i;
        final String str;
        final String str2;
        if (fullPath != null) {
            File file = null;
            if (!(fullPath == null || fullPath.length() == 0)) {
                file = new File(fullPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file != null) {
                final File sourceFile = file;
                final boolean[] cancelled = new boolean[1];
                if (sourceFile.exists()) {
                    AlertDialog progressDialog = null;
                    if (context != null) {
                        try {
                            AlertDialog progressDialog2 = new AlertDialog(context, 2);
                            try {
                                progressDialog2.setMessage(LocaleController.getString("Loading", R.string.Loading));
                                progressDialog2.setCanceledOnTouchOutside(false);
                                progressDialog2.setCancelable(true);
                                progressDialog2.setOnCancelListener(new OnCancelListener() {
                                    public void onCancel(DialogInterface dialog) {
                                        cancelled[0] = true;
                                    }
                                });
                                progressDialog2.show();
                                progressDialog = progressDialog2;
                            } catch (Exception e2) {
                                e = e2;
                                progressDialog = progressDialog2;
                                FileLog.e(e);
                                finalProgress = progressDialog;
                                i = type;
                                str = name;
                                str2 = mime;
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            File destFile;
                                            long a;
                                            if (i == 0) {
                                                destFile = AndroidUtilities.generatePicturePath();
                                            } else if (i == 1) {
                                                destFile = AndroidUtilities.generateVideoPath();
                                            } else {
                                                File dir;
                                                if (i == 2) {
                                                    dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                                } else {
                                                    dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                                                }
                                                dir.mkdir();
                                                destFile = new File(dir, str);
                                                if (destFile.exists()) {
                                                    int idx = str.lastIndexOf(46);
                                                    for (a = null; a < 10; a++) {
                                                        String newName;
                                                        if (idx != -1) {
                                                            newName = str.substring(0, idx) + "(" + (a + 1) + ")" + str.substring(idx);
                                                        } else {
                                                            newName = str + "(" + (a + 1) + ")";
                                                        }
                                                        destFile = new File(dir, newName);
                                                        if (!destFile.exists()) {
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            if (!destFile.exists()) {
                                                destFile.createNewFile();
                                            }
                                            FileChannel source = null;
                                            FileChannel destination = null;
                                            boolean result = true;
                                            long lastProgress = System.currentTimeMillis() - 500;
                                            try {
                                                source = new FileInputStream(sourceFile).getChannel();
                                                destination = new FileOutputStream(destFile).getChannel();
                                                long size = source.size();
                                                for (a = 0; a < size && !cancelled[0]; a += 4096) {
                                                    destination.transferFrom(source, a, Math.min(4096, size - a));
                                                    if (finalProgress != null && lastProgress <= System.currentTimeMillis() - 500) {
                                                        lastProgress = System.currentTimeMillis();
                                                        final int i = (int) ((((float) a) / ((float) size)) * 100.0f);
                                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                                            public void run() {
                                                                try {
                                                                    finalProgress.setProgress(i);
                                                                } catch (Throwable e) {
                                                                    FileLog.e(e);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                                if (source != null) {
                                                    try {
                                                        source.close();
                                                    } catch (Exception e) {
                                                    }
                                                }
                                                if (destination != null) {
                                                    try {
                                                        destination.close();
                                                    } catch (Exception e2) {
                                                    }
                                                }
                                            } catch (Throwable e3) {
                                                FileLog.e(e3);
                                                result = false;
                                                if (source != null) {
                                                    try {
                                                        source.close();
                                                    } catch (Exception e4) {
                                                    }
                                                }
                                                if (destination != null) {
                                                    try {
                                                        destination.close();
                                                    } catch (Exception e5) {
                                                    }
                                                }
                                            } catch (Throwable th) {
                                                if (source != null) {
                                                    try {
                                                        source.close();
                                                    } catch (Exception e6) {
                                                    }
                                                }
                                                if (destination != null) {
                                                    try {
                                                        destination.close();
                                                    } catch (Exception e7) {
                                                    }
                                                }
                                            }
                                            if (cancelled[0]) {
                                                destFile.delete();
                                                result = false;
                                            }
                                            if (result) {
                                                if (i == 2) {
                                                    ((DownloadManager) ApplicationLoader.applicationContext.getSystemService("download")).addCompletedDownload(destFile.getName(), destFile.getName(), false, str2, destFile.getAbsolutePath(), destFile.length(), true);
                                                } else {
                                                    AndroidUtilities.addMediaToGallery(Uri.fromFile(destFile));
                                                }
                                            }
                                        } catch (Throwable e32) {
                                            FileLog.e(e32);
                                        }
                                        if (finalProgress != null) {
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    try {
                                                        finalProgress.dismiss();
                                                    } catch (Throwable e) {
                                                        FileLog.e(e);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }).start();
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.e(e);
                            finalProgress = progressDialog;
                            i = type;
                            str = name;
                            str2 = mime;
                            new Thread(/* anonymous class already generated */).start();
                        }
                    }
                    finalProgress = progressDialog;
                    i = type;
                    str = name;
                    str2 = mime;
                    new Thread(/* anonymous class already generated */).start();
                }
            }
        }
    }

    public static boolean isWebp(Uri uri) {
        boolean z = false;
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[12];
            if (inputStream.read(header, 0, 12) == 12) {
                String str = new String(header);
                if (str != null) {
                    str = str.toLowerCase();
                    if (str.startsWith("riff") && str.endsWith("webp")) {
                        z = true;
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable e2) {
                                FileLog.e(e2);
                            }
                        }
                        return z;
                    }
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.e(e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.e(e2222);
                }
            }
        }
        return z;
    }

    public static boolean isGif(Uri uri) {
        boolean z = false;
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[3];
            if (inputStream.read(header, 0, 3) == 3) {
                String str = new String(header);
                if (str != null && str.equalsIgnoreCase("gif")) {
                    z = true;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                        }
                    }
                    return z;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.e(e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.e(e2222);
                }
            }
        }
        return z;
    }

    public static String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals(Param.CONTENT)) {
            Cursor cursor = null;
            try {
                cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex("_display_name"));
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable e) {
                FileLog.e(e);
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result != null) {
            return result;
        }
        result = uri.getPath();
        int cut = result.lastIndexOf(47);
        if (cut != -1) {
            return result.substring(cut + 1);
        }
        return result;
    }

    public static String copyFileToCache(Uri uri, String ext) {
        Throwable e;
        Throwable th;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            String name = getFileName(uri);
            if (name == null) {
                int id = UserConfig.lastLocalId;
                UserConfig.lastLocalId--;
                UserConfig.saveConfig(false);
                name = String.format(Locale.US, "%d.%s", new Object[]{Integer.valueOf(id), ext});
            }
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            File f = new File(FileLoader.getInstance().getDirectory(4), name);
            FileOutputStream output = new FileOutputStream(f);
            try {
                byte[] buffer = new byte[CacheDataSink.DEFAULT_BUFFER_SIZE];
                while (true) {
                    int len = inputStream.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    output.write(buffer, 0, len);
                }
                String absolutePath = f.getAbsolutePath();
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (Throwable e22) {
                        FileLog.e(e22);
                    }
                }
                fileOutputStream = output;
                return absolutePath;
            } catch (Exception e3) {
                e = e3;
                fileOutputStream = output;
                try {
                    FileLog.e(e);
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e222) {
                            FileLog.e(e222);
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e2222) {
                            FileLog.e(e2222);
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e22222) {
                            FileLog.e(e22222);
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e222222) {
                            FileLog.e(e222222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fileOutputStream = output;
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            FileLog.e(e);
            if (inputStream != null) {
                inputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            return null;
        }
    }

    public void toggleSaveToGallery() {
        boolean z;
        if (this.saveToGallery) {
            z = false;
        } else {
            z = true;
        }
        this.saveToGallery = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        editor.putBoolean("save_gallery", this.saveToGallery);
        editor.commit();
        checkSaveToGalleryFiles();
    }

    public void toggleAutoplayGifs() {
        boolean z;
        if (this.autoplayGifs) {
            z = false;
        } else {
            z = true;
        }
        this.autoplayGifs = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        editor.putBoolean("autoplay_gif", this.autoplayGifs);
        editor.commit();
    }

    public void toogleRaiseToSpeak() {
        boolean z;
        if (this.raiseToSpeak) {
            z = false;
        } else {
            z = true;
        }
        this.raiseToSpeak = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        editor.putBoolean("raise_to_speak", this.raiseToSpeak);
        editor.commit();
    }

    public void toggleCustomTabs() {
        boolean z;
        if (this.customTabs) {
            z = false;
        } else {
            z = true;
        }
        this.customTabs = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        editor.putBoolean("custom_tabs", this.customTabs);
        editor.commit();
    }

    public void toggleDirectShare() {
        boolean z;
        if (this.directShare) {
            z = false;
        } else {
            z = true;
        }
        this.directShare = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        editor.putBoolean("direct_share", this.directShare);
        editor.commit();
    }

    public void checkSaveToGalleryFiles() {
        try {
            File telegramPath = new File(Environment.getExternalStorageDirectory(), "Telegram");
            File imagePath = new File(telegramPath, "Telegram Images");
            imagePath.mkdir();
            File videoPath = new File(telegramPath, "Telegram Video");
            videoPath.mkdir();
            if (this.saveToGallery) {
                if (imagePath.isDirectory()) {
                    new File(imagePath, ".nomedia").delete();
                }
                if (videoPath.isDirectory()) {
                    new File(videoPath, ".nomedia").delete();
                    return;
                }
                return;
            }
            if (imagePath.isDirectory()) {
                new File(imagePath, ".nomedia").createNewFile();
            }
            if (videoPath.isDirectory()) {
                new File(videoPath, ".nomedia").createNewFile();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public boolean canSaveToGallery() {
        return this.saveToGallery;
    }

    public boolean canAutoplayGifs() {
        return this.autoplayGifs;
    }

    public boolean canRaiseToSpeak() {
        return this.raiseToSpeak;
    }

    public boolean canCustomTabs() {
        return this.customTabs;
    }

    public boolean canDirectShare() {
        return this.directShare;
    }

    public static void loadGalleryPhotosAlbums(final int guid) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Throwable e;
                int imageIdColumn;
                int bucketIdColumn;
                int bucketNameColumn;
                int dataColumn;
                int dateColumn;
                AlbumEntry allMediaAlbum;
                int imageId;
                int bucketId;
                String bucketName;
                String path;
                long dateTaken;
                AlbumEntry albumEntry;
                Throwable th;
                Integer mediaCameraAlbumId;
                int durationColumn;
                long duration;
                PhotoEntry photoEntry;
                int a;
                ArrayList<AlbumEntry> mediaAlbumsSorted = new ArrayList();
                ArrayList<AlbumEntry> photoAlbumsSorted = new ArrayList();
                HashMap<Integer, AlbumEntry> mediaAlbums = new HashMap();
                HashMap<Integer, AlbumEntry> photoAlbums = new HashMap();
                AlbumEntry albumEntry2 = null;
                AlbumEntry albumEntry3 = null;
                String cameraFolder = null;
                try {
                    cameraFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/";
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
                Integer num = null;
                Integer photoCameraAlbumId = null;
                Cursor cursor = null;
                if (VERSION.SDK_INT < 23 || (VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0)) {
                    cursor = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Media.EXTERNAL_CONTENT_URI, MediaController.projectionPhotos, null, null, "datetaken DESC");
                    if (cursor != null) {
                        imageIdColumn = cursor.getColumnIndex("_id");
                        bucketIdColumn = cursor.getColumnIndex("bucket_id");
                        bucketNameColumn = cursor.getColumnIndex("bucket_display_name");
                        dataColumn = cursor.getColumnIndex("_data");
                        dateColumn = cursor.getColumnIndex("datetaken");
                        int orientationColumn = cursor.getColumnIndex("orientation");
                        allMediaAlbum = null;
                        AlbumEntry allPhotosAlbum = null;
                        while (cursor.moveToNext()) {
                            try {
                                imageId = cursor.getInt(imageIdColumn);
                                bucketId = cursor.getInt(bucketIdColumn);
                                bucketName = cursor.getString(bucketNameColumn);
                                path = cursor.getString(dataColumn);
                                dateTaken = cursor.getLong(dateColumn);
                                int orientation = cursor.getInt(orientationColumn);
                                if (!(path == null || path.length() == 0)) {
                                    AlbumEntry albumEntry4;
                                    PhotoEntry photoEntry2 = new PhotoEntry(bucketId, imageId, dateTaken, path, orientation, false);
                                    if (allPhotosAlbum == null) {
                                        albumEntry = new AlbumEntry(0, LocaleController.getString("AllPhotos", R.string.AllPhotos), photoEntry2);
                                        try {
                                            photoAlbumsSorted.add(0, albumEntry);
                                        } catch (Throwable th2) {
                                            th = th2;
                                            albumEntry3 = allMediaAlbum;
                                        }
                                    } else {
                                        albumEntry2 = allPhotosAlbum;
                                    }
                                    if (allMediaAlbum == null) {
                                        albumEntry3 = new AlbumEntry(0, LocaleController.getString("AllMedia", R.string.AllMedia), photoEntry2);
                                        try {
                                            mediaAlbumsSorted.add(0, albumEntry3);
                                        } catch (Throwable th3) {
                                            e2 = th3;
                                        }
                                    } else {
                                        albumEntry3 = allMediaAlbum;
                                    }
                                    albumEntry2.addPhoto(photoEntry2);
                                    albumEntry3.addPhoto(photoEntry2);
                                    albumEntry4 = (AlbumEntry) mediaAlbums.get(Integer.valueOf(bucketId));
                                    if (albumEntry4 == null) {
                                        albumEntry = new AlbumEntry(bucketId, bucketName, photoEntry2);
                                        mediaAlbums.put(Integer.valueOf(bucketId), albumEntry);
                                        if (num != null || cameraFolder == null || path == null || !path.startsWith(cameraFolder)) {
                                            mediaAlbumsSorted.add(albumEntry);
                                        } else {
                                            mediaAlbumsSorted.add(0, albumEntry);
                                            num = Integer.valueOf(bucketId);
                                        }
                                    }
                                    albumEntry4.addPhoto(photoEntry2);
                                    albumEntry4 = (AlbumEntry) photoAlbums.get(Integer.valueOf(bucketId));
                                    if (albumEntry4 == null) {
                                        albumEntry = new AlbumEntry(bucketId, bucketName, photoEntry2);
                                        photoAlbums.put(Integer.valueOf(bucketId), albumEntry);
                                        if (photoCameraAlbumId != null || cameraFolder == null || path == null || !path.startsWith(cameraFolder)) {
                                            photoAlbumsSorted.add(albumEntry);
                                        } else {
                                            photoAlbumsSorted.add(0, albumEntry);
                                            photoCameraAlbumId = Integer.valueOf(bucketId);
                                        }
                                    }
                                    albumEntry4.addPhoto(photoEntry2);
                                    allMediaAlbum = albumEntry3;
                                    allPhotosAlbum = albumEntry2;
                                }
                            } catch (Throwable th4) {
                                th = th4;
                                albumEntry3 = allMediaAlbum;
                                albumEntry2 = allPhotosAlbum;
                            }
                        }
                        albumEntry3 = allMediaAlbum;
                        albumEntry2 = allPhotosAlbum;
                    }
                }
                if (cursor != null) {
                    try {
                        cursor.close();
                        mediaCameraAlbumId = num;
                        allMediaAlbum = albumEntry3;
                    } catch (Throwable e22) {
                        FileLog.e(e22);
                        mediaCameraAlbumId = num;
                        allMediaAlbum = albumEntry3;
                    }
                    if (VERSION.SDK_INT < 23 || (VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0)) {
                        cursor = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, MediaController.projectionVideo, null, null, "datetaken DESC");
                        if (cursor != null) {
                            imageIdColumn = cursor.getColumnIndex("_id");
                            bucketIdColumn = cursor.getColumnIndex("bucket_id");
                            bucketNameColumn = cursor.getColumnIndex("bucket_display_name");
                            dataColumn = cursor.getColumnIndex("_data");
                            dateColumn = cursor.getColumnIndex("datetaken");
                            durationColumn = cursor.getColumnIndex("duration");
                            while (cursor.moveToNext()) {
                                imageId = cursor.getInt(imageIdColumn);
                                bucketId = cursor.getInt(bucketIdColumn);
                                bucketName = cursor.getString(bucketNameColumn);
                                path = cursor.getString(dataColumn);
                                dateTaken = cursor.getLong(dateColumn);
                                duration = cursor.getLong(durationColumn);
                                if (!(path == null || path.length() == 0)) {
                                    photoEntry = new PhotoEntry(bucketId, imageId, dateTaken, path, (int) (duration / 1000), true);
                                    if (allMediaAlbum != null) {
                                        albumEntry3 = new AlbumEntry(0, LocaleController.getString("AllMedia", R.string.AllMedia), photoEntry);
                                        try {
                                            mediaAlbumsSorted.add(0, albumEntry3);
                                        } catch (Throwable th5) {
                                            th = th5;
                                            num = mediaCameraAlbumId;
                                        }
                                    } else {
                                        albumEntry3 = allMediaAlbum;
                                    }
                                    albumEntry3.addPhoto(photoEntry);
                                    albumEntry4 = (AlbumEntry) mediaAlbums.get(Integer.valueOf(bucketId));
                                    if (albumEntry4 == null) {
                                        albumEntry = new AlbumEntry(bucketId, bucketName, photoEntry);
                                        mediaAlbums.put(Integer.valueOf(bucketId), albumEntry);
                                        if (mediaCameraAlbumId == null || cameraFolder == null || path == null || !path.startsWith(cameraFolder)) {
                                            mediaAlbumsSorted.add(albumEntry);
                                        } else {
                                            mediaAlbumsSorted.add(0, albumEntry);
                                            num = Integer.valueOf(bucketId);
                                            albumEntry4.addPhoto(photoEntry);
                                            mediaCameraAlbumId = num;
                                            allMediaAlbum = albumEntry3;
                                        }
                                    }
                                    num = mediaCameraAlbumId;
                                    try {
                                        albumEntry4.addPhoto(photoEntry);
                                        mediaCameraAlbumId = num;
                                        allMediaAlbum = albumEntry3;
                                    } catch (Throwable th6) {
                                        e22 = th6;
                                    }
                                }
                            }
                        }
                    }
                    num = mediaCameraAlbumId;
                    albumEntry3 = allMediaAlbum;
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Throwable e222) {
                            FileLog.e(e222);
                        }
                    }
                    for (a = 0; a < mediaAlbumsSorted.size(); a++) {
                        Collections.sort(((AlbumEntry) mediaAlbumsSorted.get(a)).photos, new Comparator<PhotoEntry>() {
                            public int compare(PhotoEntry o1, PhotoEntry o2) {
                                if (o1.dateTaken < o2.dateTaken) {
                                    return 1;
                                }
                                if (o1.dateTaken > o2.dateTaken) {
                                    return -1;
                                }
                                return 0;
                            }
                        });
                    }
                    MediaController.broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, num, albumEntry3, albumEntry2, 0);
                }
                mediaCameraAlbumId = num;
                allMediaAlbum = albumEntry3;
                try {
                    cursor = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, MediaController.projectionVideo, null, null, "datetaken DESC");
                    if (cursor != null) {
                        imageIdColumn = cursor.getColumnIndex("_id");
                        bucketIdColumn = cursor.getColumnIndex("bucket_id");
                        bucketNameColumn = cursor.getColumnIndex("bucket_display_name");
                        dataColumn = cursor.getColumnIndex("_data");
                        dateColumn = cursor.getColumnIndex("datetaken");
                        durationColumn = cursor.getColumnIndex("duration");
                        while (cursor.moveToNext()) {
                            imageId = cursor.getInt(imageIdColumn);
                            bucketId = cursor.getInt(bucketIdColumn);
                            bucketName = cursor.getString(bucketNameColumn);
                            path = cursor.getString(dataColumn);
                            dateTaken = cursor.getLong(dateColumn);
                            duration = cursor.getLong(durationColumn);
                            photoEntry = new PhotoEntry(bucketId, imageId, dateTaken, path, (int) (duration / 1000), true);
                            if (allMediaAlbum != null) {
                                albumEntry3 = allMediaAlbum;
                            } else {
                                albumEntry3 = new AlbumEntry(0, LocaleController.getString("AllMedia", R.string.AllMedia), photoEntry);
                                mediaAlbumsSorted.add(0, albumEntry3);
                            }
                            albumEntry3.addPhoto(photoEntry);
                            albumEntry4 = (AlbumEntry) mediaAlbums.get(Integer.valueOf(bucketId));
                            if (albumEntry4 == null) {
                                albumEntry = new AlbumEntry(bucketId, bucketName, photoEntry);
                                mediaAlbums.put(Integer.valueOf(bucketId), albumEntry);
                                if (mediaCameraAlbumId == null) {
                                }
                                mediaAlbumsSorted.add(albumEntry);
                            }
                            num = mediaCameraAlbumId;
                            albumEntry4.addPhoto(photoEntry);
                            mediaCameraAlbumId = num;
                            allMediaAlbum = albumEntry3;
                        }
                    }
                    num = mediaCameraAlbumId;
                    albumEntry3 = allMediaAlbum;
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th7) {
                    th = th7;
                    num = mediaCameraAlbumId;
                    albumEntry3 = allMediaAlbum;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
                for (a = 0; a < mediaAlbumsSorted.size(); a++) {
                    Collections.sort(((AlbumEntry) mediaAlbumsSorted.get(a)).photos, /* anonymous class already generated */);
                }
                MediaController.broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, num, albumEntry3, albumEntry2, 0);
                try {
                    FileLog.e(e222);
                    if (cursor != null) {
                        try {
                            cursor.close();
                            mediaCameraAlbumId = num;
                            allMediaAlbum = albumEntry3;
                        } catch (Throwable e2222) {
                            FileLog.e(e2222);
                            mediaCameraAlbumId = num;
                            allMediaAlbum = albumEntry3;
                        }
                        cursor = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, MediaController.projectionVideo, null, null, "datetaken DESC");
                        if (cursor != null) {
                            imageIdColumn = cursor.getColumnIndex("_id");
                            bucketIdColumn = cursor.getColumnIndex("bucket_id");
                            bucketNameColumn = cursor.getColumnIndex("bucket_display_name");
                            dataColumn = cursor.getColumnIndex("_data");
                            dateColumn = cursor.getColumnIndex("datetaken");
                            durationColumn = cursor.getColumnIndex("duration");
                            while (cursor.moveToNext()) {
                                imageId = cursor.getInt(imageIdColumn);
                                bucketId = cursor.getInt(bucketIdColumn);
                                bucketName = cursor.getString(bucketNameColumn);
                                path = cursor.getString(dataColumn);
                                dateTaken = cursor.getLong(dateColumn);
                                duration = cursor.getLong(durationColumn);
                                photoEntry = new PhotoEntry(bucketId, imageId, dateTaken, path, (int) (duration / 1000), true);
                                if (allMediaAlbum != null) {
                                    albumEntry3 = new AlbumEntry(0, LocaleController.getString("AllMedia", R.string.AllMedia), photoEntry);
                                    mediaAlbumsSorted.add(0, albumEntry3);
                                } else {
                                    albumEntry3 = allMediaAlbum;
                                }
                                albumEntry3.addPhoto(photoEntry);
                                albumEntry4 = (AlbumEntry) mediaAlbums.get(Integer.valueOf(bucketId));
                                if (albumEntry4 == null) {
                                    albumEntry = new AlbumEntry(bucketId, bucketName, photoEntry);
                                    mediaAlbums.put(Integer.valueOf(bucketId), albumEntry);
                                    if (mediaCameraAlbumId == null) {
                                    }
                                    mediaAlbumsSorted.add(albumEntry);
                                }
                                num = mediaCameraAlbumId;
                                albumEntry4.addPhoto(photoEntry);
                                mediaCameraAlbumId = num;
                                allMediaAlbum = albumEntry3;
                            }
                        }
                        num = mediaCameraAlbumId;
                        albumEntry3 = allMediaAlbum;
                        if (cursor != null) {
                            cursor.close();
                        }
                        for (a = 0; a < mediaAlbumsSorted.size(); a++) {
                            Collections.sort(((AlbumEntry) mediaAlbumsSorted.get(a)).photos, /* anonymous class already generated */);
                        }
                        MediaController.broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, num, albumEntry3, albumEntry2, 0);
                    }
                    mediaCameraAlbumId = num;
                    allMediaAlbum = albumEntry3;
                    cursor = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, MediaController.projectionVideo, null, null, "datetaken DESC");
                    if (cursor != null) {
                        imageIdColumn = cursor.getColumnIndex("_id");
                        bucketIdColumn = cursor.getColumnIndex("bucket_id");
                        bucketNameColumn = cursor.getColumnIndex("bucket_display_name");
                        dataColumn = cursor.getColumnIndex("_data");
                        dateColumn = cursor.getColumnIndex("datetaken");
                        durationColumn = cursor.getColumnIndex("duration");
                        while (cursor.moveToNext()) {
                            imageId = cursor.getInt(imageIdColumn);
                            bucketId = cursor.getInt(bucketIdColumn);
                            bucketName = cursor.getString(bucketNameColumn);
                            path = cursor.getString(dataColumn);
                            dateTaken = cursor.getLong(dateColumn);
                            duration = cursor.getLong(durationColumn);
                            photoEntry = new PhotoEntry(bucketId, imageId, dateTaken, path, (int) (duration / 1000), true);
                            if (allMediaAlbum != null) {
                                albumEntry3 = allMediaAlbum;
                            } else {
                                albumEntry3 = new AlbumEntry(0, LocaleController.getString("AllMedia", R.string.AllMedia), photoEntry);
                                mediaAlbumsSorted.add(0, albumEntry3);
                            }
                            albumEntry3.addPhoto(photoEntry);
                            albumEntry4 = (AlbumEntry) mediaAlbums.get(Integer.valueOf(bucketId));
                            if (albumEntry4 == null) {
                                albumEntry = new AlbumEntry(bucketId, bucketName, photoEntry);
                                mediaAlbums.put(Integer.valueOf(bucketId), albumEntry);
                                if (mediaCameraAlbumId == null) {
                                }
                                mediaAlbumsSorted.add(albumEntry);
                            }
                            num = mediaCameraAlbumId;
                            albumEntry4.addPhoto(photoEntry);
                            mediaCameraAlbumId = num;
                            allMediaAlbum = albumEntry3;
                        }
                    }
                    num = mediaCameraAlbumId;
                    albumEntry3 = allMediaAlbum;
                    if (cursor != null) {
                        cursor.close();
                    }
                    for (a = 0; a < mediaAlbumsSorted.size(); a++) {
                        Collections.sort(((AlbumEntry) mediaAlbumsSorted.get(a)).photos, /* anonymous class already generated */);
                    }
                    MediaController.broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, num, albumEntry3, albumEntry2, 0);
                } catch (Throwable th8) {
                    th = th8;
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Throwable e22222) {
                            FileLog.e(e22222);
                        }
                    }
                    throw th;
                }
            }
        });
        thread.setPriority(1);
        thread.start();
    }

    private static void broadcastNewPhotos(int guid, ArrayList<AlbumEntry> mediaAlbumsSorted, ArrayList<AlbumEntry> photoAlbumsSorted, Integer cameraAlbumIdFinal, AlbumEntry allMediaAlbumFinal, AlbumEntry allPhotosAlbumFinal, int delay) {
        if (broadcastPhotosRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(broadcastPhotosRunnable);
        }
        final int i = guid;
        final ArrayList<AlbumEntry> arrayList = mediaAlbumsSorted;
        final ArrayList<AlbumEntry> arrayList2 = photoAlbumsSorted;
        final Integer num = cameraAlbumIdFinal;
        final AlbumEntry albumEntry = allMediaAlbumFinal;
        final AlbumEntry albumEntry2 = allPhotosAlbumFinal;
        Runnable anonymousClass27 = new Runnable() {
            public void run() {
                if (PhotoViewer.getInstance().isVisible()) {
                    MediaController.broadcastNewPhotos(i, arrayList, arrayList2, num, albumEntry, albumEntry2, 1000);
                    return;
                }
                MediaController.broadcastPhotosRunnable = null;
                MediaController.allPhotosAlbumEntry = albumEntry2;
                MediaController.allMediaAlbumEntry = albumEntry;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.albumsDidLoaded, Integer.valueOf(i), arrayList, arrayList2, num);
            }
        };
        broadcastPhotosRunnable = anonymousClass27;
        AndroidUtilities.runOnUIThread(anonymousClass27, (long) delay);
    }

    public void scheduleVideoConvert(MessageObject messageObject) {
        scheduleVideoConvert(messageObject, false);
    }

    public boolean scheduleVideoConvert(MessageObject messageObject, boolean isEmpty) {
        if (isEmpty && !this.videoConvertQueue.isEmpty()) {
            return false;
        }
        if (isEmpty) {
            new File(messageObject.messageOwner.attachPath).delete();
        }
        this.videoConvertQueue.add(messageObject);
        if (this.videoConvertQueue.size() != 1) {
            return true;
        }
        startVideoConvertFromQueue();
        return true;
    }

    public void cancelVideoConvert(MessageObject messageObject) {
        if (messageObject == null) {
            synchronized (this.videoConvertSync) {
                this.cancelCurrentVideoConversion = true;
            }
        } else if (!this.videoConvertQueue.isEmpty()) {
            if (this.videoConvertQueue.get(0) == messageObject) {
                synchronized (this.videoConvertSync) {
                    this.cancelCurrentVideoConversion = true;
                }
                return;
            }
            this.videoConvertQueue.remove(messageObject);
        }
    }

    private boolean startVideoConvertFromQueue() {
        if (this.videoConvertQueue.isEmpty()) {
            return false;
        }
        synchronized (this.videoConvertSync) {
            this.cancelCurrentVideoConversion = false;
        }
        MessageObject messageObject = (MessageObject) this.videoConvertQueue.get(0);
        Intent intent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
        intent.putExtra("path", messageObject.messageOwner.attachPath);
        if (messageObject.messageOwner.media.document != null) {
            for (int a = 0; a < messageObject.messageOwner.media.document.attributes.size(); a++) {
                if (((DocumentAttribute) messageObject.messageOwner.media.document.attributes.get(a)) instanceof TL_documentAttributeAnimated) {
                    intent.putExtra("gif", true);
                    break;
                }
            }
        }
        if (messageObject.getId() != 0) {
            ApplicationLoader.applicationContext.startService(intent);
        }
        VideoConvertRunnable.runConversion(messageObject);
        return true;
    }

    @SuppressLint({"NewApi"})
    public static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo lastCodecInfo = null;
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (codecInfo.isEncoder()) {
                for (String type : codecInfo.getSupportedTypes()) {
                    if (type.equalsIgnoreCase(mimeType)) {
                        lastCodecInfo = codecInfo;
                        if (!lastCodecInfo.getName().equals("OMX.SEC.avc.enc")) {
                            return lastCodecInfo;
                        }
                        if (lastCodecInfo.getName().equals("OMX.SEC.AVC.Encoder")) {
                            return lastCodecInfo;
                        }
                    }
                }
                continue;
            }
        }
        return lastCodecInfo;
    }

    private static boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            case 19:
            case 20:
            case 21:
            case 39:
            case 2130706688:
                return true;
            default:
                return false;
        }
    }

    @SuppressLint({"NewApi"})
    public static int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
        CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        int lastColorFormat = 0;
        for (int colorFormat : capabilities.colorFormats) {
            if (isRecognizedFormat(colorFormat)) {
                lastColorFormat = colorFormat;
                if (!codecInfo.getName().equals("OMX.SEC.AVC.Encoder") || colorFormat != 19) {
                    return colorFormat;
                }
            }
        }
        return lastColorFormat;
    }

    @TargetApi(16)
    private int selectTrack(MediaExtractor extractor, boolean audio) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            String mime = extractor.getTrackFormat(i).getString("mime");
            if (audio) {
                if (mime.startsWith("audio/")) {
                    return i;
                }
            } else if (mime.startsWith("video/")) {
                return i;
            }
        }
        return -5;
    }

    private void didWriteData(MessageObject messageObject, File file, boolean last, boolean error) {
        final boolean firstWrite = this.videoConvertFirstWrite;
        if (firstWrite) {
            this.videoConvertFirstWrite = false;
        }
        final boolean z = error;
        final boolean z2 = last;
        final MessageObject messageObject2 = messageObject;
        final File file2 = file;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (z || z2) {
                    synchronized (MediaController.this.videoConvertSync) {
                        MediaController.this.cancelCurrentVideoConversion = false;
                    }
                    MediaController.this.videoConvertQueue.remove(messageObject2);
                    MediaController.this.startVideoConvertFromQueue();
                }
                if (z) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingFailed, messageObject2, file2.toString());
                    return;
                }
                if (firstWrite) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingStarted, messageObject2, file2.toString());
                }
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.FileNewChunkAvailable;
                Object[] objArr = new Object[3];
                objArr[0] = messageObject2;
                objArr[1] = file2.toString();
                objArr[2] = Long.valueOf(z2 ? file2.length() : 0);
                instance.postNotificationName(i, objArr);
            }
        });
    }

    @TargetApi(16)
    private long readAndWriteTrack(MessageObject messageObject, MediaExtractor extractor, MP4Builder mediaMuxer, BufferInfo info, long start, long end, File file, boolean isAudio) throws Exception {
        int trackIndex = selectTrack(extractor, isAudio);
        if (trackIndex < 0) {
            return -1;
        }
        extractor.selectTrack(trackIndex);
        MediaFormat trackFormat = extractor.getTrackFormat(trackIndex);
        int muxerTrackIndex = mediaMuxer.addTrack(trackFormat, isAudio);
        int maxBufferSize = trackFormat.getInteger("max-input-size");
        boolean inputDone = false;
        if (start > 0) {
            extractor.seekTo(start, 0);
        } else {
            extractor.seekTo(0, 0);
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
        long startTime = -1;
        checkConversionCanceled();
        while (!inputDone) {
            checkConversionCanceled();
            boolean eof = false;
            int index = extractor.getSampleTrackIndex();
            if (index == trackIndex) {
                info.size = extractor.readSampleData(buffer, 0);
                if (VERSION.SDK_INT < 21) {
                    buffer.position(0);
                    buffer.limit(info.size);
                }
                if (!isAudio) {
                    byte[] array = buffer.array();
                    if (array != null) {
                        int offset = buffer.arrayOffset();
                        int len = offset + buffer.limit();
                        int writeStart = -1;
                        int a = offset;
                        while (a <= len - 4) {
                            if ((array[a] == (byte) 0 && array[a + 1] == (byte) 0 && array[a + 2] == (byte) 0 && array[a + 3] == (byte) 1) || a == len - 4) {
                                if (writeStart != -1) {
                                    int l = (a - writeStart) - (a != len + -4 ? 4 : 0);
                                    array[writeStart] = (byte) (l >> 24);
                                    array[writeStart + 1] = (byte) (l >> 16);
                                    array[writeStart + 2] = (byte) (l >> 8);
                                    array[writeStart + 3] = (byte) l;
                                    writeStart = a;
                                } else {
                                    writeStart = a;
                                }
                            }
                            a++;
                        }
                    }
                }
                if (info.size >= 0) {
                    info.presentationTimeUs = extractor.getSampleTime();
                } else {
                    info.size = 0;
                    eof = true;
                }
                if (info.size > 0 && !eof) {
                    if (start > 0 && startTime == -1) {
                        startTime = info.presentationTimeUs;
                    }
                    if (end < 0 || info.presentationTimeUs < end) {
                        info.offset = 0;
                        info.flags = extractor.getSampleFlags();
                        if (mediaMuxer.writeSampleData(muxerTrackIndex, buffer, info, false)) {
                            didWriteData(messageObject, file, false, false);
                        }
                    } else {
                        eof = true;
                    }
                }
                if (!eof) {
                    extractor.advance();
                }
            } else if (index == -1) {
                eof = true;
            } else {
                extractor.advance();
            }
            if (eof) {
                inputDone = true;
            }
        }
        extractor.unselectTrack(trackIndex);
        return startTime;
    }

    private void checkConversionCanceled() throws Exception {
        synchronized (this.videoConvertSync) {
            boolean cancelConversion = this.cancelCurrentVideoConversion;
        }
        if (cancelConversion) {
            throw new RuntimeException("canceled conversion");
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @TargetApi(16)
    private boolean convertVideo(MessageObject messageObject) {
        long videoStartTime;
        Throwable e;
        Throwable th;
        String videoPath = messageObject.videoEditedInfo.originalPath;
        long startTime = messageObject.videoEditedInfo.startTime;
        long endTime = messageObject.videoEditedInfo.endTime;
        int resultWidth = messageObject.videoEditedInfo.resultWidth;
        int resultHeight = messageObject.videoEditedInfo.resultHeight;
        int rotationValue = messageObject.videoEditedInfo.rotationValue;
        int originalWidth = messageObject.videoEditedInfo.originalWidth;
        int originalHeight = messageObject.videoEditedInfo.originalHeight;
        int bitrate = messageObject.videoEditedInfo.bitrate;
        int rotateRender = 0;
        File file = new File(messageObject.messageOwner.attachPath);
        int temp;
        if (VERSION.SDK_INT < 18 && resultHeight > resultWidth && resultWidth != originalWidth && resultHeight != originalHeight) {
            temp = resultHeight;
            resultHeight = resultWidth;
            resultWidth = temp;
            rotationValue = 90;
            rotateRender = 270;
        } else if (VERSION.SDK_INT > 20) {
            if (rotationValue == 90) {
                temp = resultHeight;
                resultHeight = resultWidth;
                resultWidth = temp;
                rotationValue = 0;
                rotateRender = 270;
            } else if (rotationValue == 180) {
                rotateRender = 180;
                rotationValue = 0;
            } else if (rotationValue == 270) {
                temp = resultHeight;
                resultHeight = resultWidth;
                resultWidth = temp;
                rotationValue = 0;
                rotateRender = 90;
            }
        }
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("videoconvert", 0);
        file = new File(videoPath);
        if (messageObject.getId() != 0) {
            boolean isPreviousOk = preferences.getBoolean("isPreviousOk", true);
            preferences.edit().putBoolean("isPreviousOk", false).commit();
            if (!(file.canRead() && isPreviousOk)) {
                didWriteData(messageObject, file, true, true);
                preferences.edit().putBoolean("isPreviousOk", true).commit();
                return false;
            }
        }
        this.videoConvertFirstWrite = true;
        boolean error = false;
        long videoStartTime2 = startTime;
        long time = System.currentTimeMillis();
        if (resultWidth == 0 || resultHeight == 0) {
            preferences.edit().putBoolean("isPreviousOk", true).commit();
            didWriteData(messageObject, file, true, true);
            return false;
        }
        MP4Builder mP4Builder = null;
        MediaExtractor extractor = null;
        try {
            BufferInfo info = new BufferInfo();
            Mp4Movie movie = new Mp4Movie();
            movie.setCacheFile(file);
            movie.setRotation(rotationValue);
            movie.setSize(resultWidth, resultHeight);
            mP4Builder = new MP4Builder().createMovie(movie);
            MediaExtractor extractor2 = new MediaExtractor();
            try {
                extractor2.setDataSource(videoPath);
                checkConversionCanceled();
                long videoTime;
                if (resultWidth == originalWidth && resultHeight == originalHeight && rotateRender == 0 && !messageObject.videoEditedInfo.roundVideo) {
                    videoTime = readAndWriteTrack(messageObject, extractor2, mP4Builder, info, startTime, endTime, file, false);
                    if (videoTime != -1) {
                        videoStartTime = videoTime;
                    }
                    videoStartTime = videoStartTime2;
                } else {
                    int videoIndex = selectTrack(extractor2, false);
                    if (videoIndex >= 0) {
                        int colorFormat;
                        MediaCodec decoder = null;
                        MediaCodec encoder = null;
                        InputSurface inputSurface = null;
                        OutputSurface outputSurface = null;
                        videoTime = -1;
                        boolean outputDone = false;
                        boolean inputDone = false;
                        boolean decoderDone = false;
                        int swapUV = 0;
                        int videoTrackIndex = -5;
                        int processorType = 0;
                        String manufacturer = Build.MANUFACTURER.toLowerCase();
                        if (VERSION.SDK_INT < 18) {
                            MediaCodecInfo codecInfo = selectCodec("video/avc");
                            colorFormat = selectColorFormat(codecInfo, "video/avc");
                            if (colorFormat == 0) {
                                throw new RuntimeException("no supported color format");
                            }
                            String codecName = codecInfo.getName();
                            if (codecName.contains("OMX.qcom.")) {
                                processorType = 1;
                                if (VERSION.SDK_INT == 16) {
                                    if (!manufacturer.equals("lge")) {
                                    }
                                    swapUV = 1;
                                }
                            } else {
                                if (codecName.contains("OMX.Intel.")) {
                                    processorType = 2;
                                } else {
                                    if (codecName.equals("OMX.MTK.VIDEO.ENCODER.AVC")) {
                                        processorType = 3;
                                    } else {
                                        if (codecName.equals("OMX.SEC.AVC.Encoder")) {
                                            processorType = 4;
                                            swapUV = 1;
                                        } else {
                                            if (codecName.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                processorType = 5;
                                            }
                                        }
                                    }
                                }
                            }
                            FileLog.e("codec = " + codecInfo.getName() + " manufacturer = " + manufacturer + "device = " + Build.MODEL);
                        } else {
                            colorFormat = NUM;
                        }
                        FileLog.e("colorFormat = " + colorFormat);
                        int resultHeightAligned = resultHeight;
                        int padding = 0;
                        int bufferSize = ((resultWidth * resultHeight) * 3) / 2;
                        if (processorType == 0) {
                            if (resultHeight % 16 != 0) {
                                padding = resultWidth * ((resultHeightAligned + (16 - (resultHeight % 16))) - resultHeight);
                                bufferSize += (padding * 5) / 4;
                            }
                        } else if (processorType == 1) {
                            if (!manufacturer.toLowerCase().equals("lge")) {
                                padding = (((resultWidth * resultHeight) + 2047) & -2048) - (resultWidth * resultHeight);
                                bufferSize += padding;
                            }
                        } else if (processorType != 5 && processorType == 3) {
                            if (manufacturer.equals("baidu")) {
                                padding = resultWidth * ((resultHeightAligned + (16 - (resultHeight % 16))) - resultHeight);
                                bufferSize += (padding * 5) / 4;
                            }
                        }
                        extractor2.selectTrack(videoIndex);
                        if (startTime > 0) {
                            extractor2.seekTo(startTime, 0);
                        } else {
                            extractor2.seekTo(0, 0);
                        }
                        MediaFormat inputFormat = extractor2.getTrackFormat(videoIndex);
                        MediaFormat outputFormat = MediaFormat.createVideoFormat("video/avc", resultWidth, resultHeight);
                        outputFormat.setInteger("color-format", colorFormat);
                        outputFormat.setInteger("bitrate", bitrate > 0 ? bitrate : 921600);
                        outputFormat.setInteger("frame-rate", 25);
                        outputFormat.setInteger("i-frame-interval", 10);
                        if (VERSION.SDK_INT < 18) {
                            outputFormat.setInteger("stride", resultWidth + 32);
                            outputFormat.setInteger("slice-height", resultHeight);
                        }
                        encoder = MediaCodec.createEncoderByType("video/avc");
                        encoder.configure(outputFormat, null, null, 1);
                        if (VERSION.SDK_INT >= 18) {
                            InputSurface inputSurface2 = new InputSurface(encoder.createInputSurface());
                            try {
                                inputSurface2.makeCurrent();
                                inputSurface = inputSurface2;
                            } catch (Exception e2) {
                                e = e2;
                                inputSurface = inputSurface2;
                                FileLog.e(e);
                                error = true;
                                videoStartTime = videoStartTime2;
                                extractor2.unselectTrack(videoIndex);
                                if (outputSurface != null) {
                                    outputSurface.release();
                                }
                                if (inputSurface != null) {
                                    inputSurface.release();
                                }
                                if (decoder != null) {
                                    decoder.stop();
                                    decoder.release();
                                }
                                if (encoder != null) {
                                    encoder.stop();
                                    encoder.release();
                                }
                                checkConversionCanceled();
                                readAndWriteTrack(messageObject, extractor2, mP4Builder, info, videoStartTime, endTime, file, true);
                                if (extractor2 != null) {
                                    extractor2.release();
                                }
                                if (mP4Builder != null) {
                                    try {
                                        mP4Builder.finishMovie();
                                    } catch (Throwable e3) {
                                        FileLog.e(e3);
                                    }
                                }
                                FileLog.e("time = " + (System.currentTimeMillis() - time));
                                extractor = extractor2;
                                preferences.edit().putBoolean("isPreviousOk", true).commit();
                                didWriteData(messageObject, file, true, error);
                                return true;
                            } catch (Throwable th2) {
                                th = th2;
                                extractor = extractor2;
                                videoStartTime = videoStartTime2;
                            }
                        }
                        try {
                            encoder.start();
                            decoder = MediaCodec.createDecoderByType(inputFormat.getString("mime"));
                            if (VERSION.SDK_INT >= 18) {
                                outputSurface = new OutputSurface();
                            } else {
                                outputSurface = new OutputSurface(resultWidth, resultHeight, rotateRender);
                            }
                            decoder.configure(inputFormat, outputSurface.getSurface(), null, 0);
                            decoder.start();
                            ByteBuffer[] decoderInputBuffers = null;
                            ByteBuffer[] encoderOutputBuffers = null;
                            ByteBuffer[] encoderInputBuffers = null;
                            if (VERSION.SDK_INT < 21) {
                                decoderInputBuffers = decoder.getInputBuffers();
                                encoderOutputBuffers = encoder.getOutputBuffers();
                                if (VERSION.SDK_INT < 18) {
                                    encoderInputBuffers = encoder.getInputBuffers();
                                }
                            }
                            checkConversionCanceled();
                            while (!outputDone) {
                                int inputBufIndex;
                                checkConversionCanceled();
                                if (!inputDone) {
                                    boolean eof = false;
                                    int index = extractor2.getSampleTrackIndex();
                                    if (index == videoIndex) {
                                        inputBufIndex = decoder.dequeueInputBuffer(2500);
                                        if (inputBufIndex >= 0) {
                                            ByteBuffer inputBuf;
                                            if (VERSION.SDK_INT < 21) {
                                                inputBuf = decoderInputBuffers[inputBufIndex];
                                            } else {
                                                inputBuf = decoder.getInputBuffer(inputBufIndex);
                                            }
                                            int chunkSize = extractor2.readSampleData(inputBuf, 0);
                                            if (chunkSize < 0) {
                                                decoder.queueInputBuffer(inputBufIndex, 0, 0, 0, 4);
                                                inputDone = true;
                                            } else {
                                                decoder.queueInputBuffer(inputBufIndex, 0, chunkSize, extractor2.getSampleTime(), 0);
                                                extractor2.advance();
                                            }
                                        }
                                    } else if (index == -1) {
                                        eof = true;
                                    }
                                    if (eof) {
                                        inputBufIndex = decoder.dequeueInputBuffer(2500);
                                        if (inputBufIndex >= 0) {
                                            decoder.queueInputBuffer(inputBufIndex, 0, 0, 0, 4);
                                            inputDone = true;
                                        }
                                    }
                                }
                                boolean decoderOutputAvailable = !decoderDone;
                                boolean encoderOutputAvailable = true;
                                while (true) {
                                    if (decoderOutputAvailable || encoderOutputAvailable) {
                                        checkConversionCanceled();
                                        int encoderStatus = encoder.dequeueOutputBuffer(info, 2500);
                                        if (encoderStatus == -1) {
                                            encoderOutputAvailable = false;
                                        } else if (encoderStatus == -3) {
                                            if (VERSION.SDK_INT < 21) {
                                                encoderOutputBuffers = encoder.getOutputBuffers();
                                            }
                                        } else if (encoderStatus == -2) {
                                            newFormat = encoder.getOutputFormat();
                                            if (videoTrackIndex == -5) {
                                                videoTrackIndex = mP4Builder.addTrack(newFormat, false);
                                            }
                                        } else if (encoderStatus < 0) {
                                            throw new RuntimeException("unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                                        } else {
                                            ByteBuffer encodedData;
                                            if (VERSION.SDK_INT < 21) {
                                                encodedData = encoderOutputBuffers[encoderStatus];
                                            } else {
                                                encodedData = encoder.getOutputBuffer(encoderStatus);
                                            }
                                            if (encodedData == null) {
                                                throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                                            }
                                            if (info.size > 1) {
                                                if ((info.flags & 2) == 0) {
                                                    if (mP4Builder.writeSampleData(videoTrackIndex, encodedData, info, true)) {
                                                        didWriteData(messageObject, file, false, false);
                                                    }
                                                } else if (videoTrackIndex == -5) {
                                                    byte[] csd = new byte[info.size];
                                                    encodedData.limit(info.offset + info.size);
                                                    encodedData.position(info.offset);
                                                    encodedData.get(csd);
                                                    ByteBuffer sps = null;
                                                    ByteBuffer pps = null;
                                                    int a = info.size - 1;
                                                    while (a >= 0 && a > 3) {
                                                        if (csd[a] == (byte) 1 && csd[a - 1] == (byte) 0 && csd[a - 2] == (byte) 0 && csd[a - 3] == (byte) 0) {
                                                            sps = ByteBuffer.allocate(a - 3);
                                                            pps = ByteBuffer.allocate(info.size - (a - 3));
                                                            sps.put(csd, 0, a - 3).position(0);
                                                            pps.put(csd, a - 3, info.size - (a - 3)).position(0);
                                                            break;
                                                        }
                                                        a--;
                                                    }
                                                    newFormat = MediaFormat.createVideoFormat("video/avc", resultWidth, resultHeight);
                                                    if (!(sps == null || pps == null)) {
                                                        newFormat.setByteBuffer("csd-0", sps);
                                                        newFormat.setByteBuffer("csd-1", pps);
                                                    }
                                                    videoTrackIndex = mP4Builder.addTrack(newFormat, false);
                                                }
                                            }
                                            outputDone = (info.flags & 4) != 0;
                                            encoder.releaseOutputBuffer(encoderStatus, false);
                                        }
                                        if (encoderStatus == -1 && !decoderDone) {
                                            int decoderStatus = decoder.dequeueOutputBuffer(info, 2500);
                                            if (decoderStatus == -1) {
                                                decoderOutputAvailable = false;
                                            } else if (decoderStatus == -3) {
                                                continue;
                                            } else if (decoderStatus == -2) {
                                                FileLog.e("newFormat = " + decoder.getOutputFormat());
                                            } else if (decoderStatus < 0) {
                                                throw new RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                                            } else {
                                                boolean doRender = VERSION.SDK_INT >= 18 ? info.size != 0 : (info.size == 0 && info.presentationTimeUs == 0) ? false : true;
                                                if (endTime > 0 && info.presentationTimeUs >= endTime) {
                                                    inputDone = true;
                                                    decoderDone = true;
                                                    doRender = false;
                                                    info.flags |= 4;
                                                }
                                                if (startTime > 0 && videoTime == -1) {
                                                    if (info.presentationTimeUs < startTime) {
                                                        doRender = false;
                                                        FileLog.e("drop frame startTime = " + startTime + " present time = " + info.presentationTimeUs);
                                                    } else {
                                                        videoTime = info.presentationTimeUs;
                                                    }
                                                }
                                                decoder.releaseOutputBuffer(decoderStatus, doRender);
                                                if (doRender) {
                                                    boolean errorWait = false;
                                                    outputSurface.awaitNewImage();
                                                    if (!errorWait) {
                                                        if (VERSION.SDK_INT >= 18) {
                                                            outputSurface.drawImage(false);
                                                            inputSurface.setPresentationTime(info.presentationTimeUs * 1000);
                                                            inputSurface.swapBuffers();
                                                        } else {
                                                            inputBufIndex = encoder.dequeueInputBuffer(2500);
                                                            if (inputBufIndex >= 0) {
                                                                outputSurface.drawImage(true);
                                                                ByteBuffer rgbBuf = outputSurface.getFrame();
                                                                ByteBuffer yuvBuf = encoderInputBuffers[inputBufIndex];
                                                                yuvBuf.clear();
                                                                Utilities.convertVideoFrame(rgbBuf, yuvBuf, colorFormat, resultWidth, resultHeight, padding, swapUV);
                                                                encoder.queueInputBuffer(inputBufIndex, 0, bufferSize, info.presentationTimeUs, 0);
                                                            } else {
                                                                FileLog.e("input buffer not available");
                                                            }
                                                        }
                                                    }
                                                }
                                                if ((info.flags & 4) != 0) {
                                                    decoderOutputAvailable = false;
                                                    FileLog.e("decoder stream end");
                                                    if (VERSION.SDK_INT >= 18) {
                                                        encoder.signalEndOfInputStream();
                                                    } else {
                                                        inputBufIndex = encoder.dequeueInputBuffer(2500);
                                                        if (inputBufIndex >= 0) {
                                                            encoder.queueInputBuffer(inputBufIndex, 0, 1, info.presentationTimeUs, 4);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (videoTime != -1) {
                                videoStartTime = videoTime;
                            } else {
                                videoStartTime = videoStartTime2;
                            }
                        } catch (Exception e4) {
                            e3 = e4;
                        } catch (Throwable th22) {
                            th = th22;
                            extractor = extractor2;
                            videoStartTime = videoStartTime2;
                        }
                        try {
                            extractor2.unselectTrack(videoIndex);
                            if (outputSurface != null) {
                                outputSurface.release();
                            }
                            if (inputSurface != null) {
                                inputSurface.release();
                            }
                            if (decoder != null) {
                                decoder.stop();
                                decoder.release();
                            }
                            if (encoder != null) {
                                encoder.stop();
                                encoder.release();
                            }
                            checkConversionCanceled();
                        } catch (Exception e5) {
                            e3 = e5;
                            extractor = extractor2;
                            error = true;
                            try {
                                FileLog.e(e3);
                                if (extractor != null) {
                                    extractor.release();
                                }
                                if (mP4Builder != null) {
                                    try {
                                        mP4Builder.finishMovie();
                                    } catch (Throwable e32) {
                                        FileLog.e(e32);
                                    }
                                }
                                FileLog.e("time = " + (System.currentTimeMillis() - time));
                                preferences.edit().putBoolean("isPreviousOk", true).commit();
                                didWriteData(messageObject, file, true, error);
                                return true;
                            } catch (Throwable th3) {
                                th = th3;
                                if (extractor != null) {
                                    extractor.release();
                                }
                                if (mP4Builder != null) {
                                    try {
                                        mP4Builder.finishMovie();
                                    } catch (Throwable e322) {
                                        FileLog.e(e322);
                                    }
                                }
                                FileLog.e("time = " + (System.currentTimeMillis() - time));
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            extractor = extractor2;
                            if (extractor != null) {
                                extractor.release();
                            }
                            if (mP4Builder != null) {
                                mP4Builder.finishMovie();
                            }
                            FileLog.e("time = " + (System.currentTimeMillis() - time));
                            throw th;
                        }
                    }
                    videoStartTime = videoStartTime2;
                }
                if (!(error || bitrate == -1)) {
                    readAndWriteTrack(messageObject, extractor2, mP4Builder, info, videoStartTime, endTime, file, true);
                }
                if (extractor2 != null) {
                    extractor2.release();
                }
                if (mP4Builder != null) {
                    mP4Builder.finishMovie();
                }
                FileLog.e("time = " + (System.currentTimeMillis() - time));
                extractor = extractor2;
            } catch (Exception e6) {
                e322 = e6;
                extractor = extractor2;
                videoStartTime = videoStartTime2;
                error = true;
                FileLog.e(e322);
                if (extractor != null) {
                    extractor.release();
                }
                if (mP4Builder != null) {
                    mP4Builder.finishMovie();
                }
                FileLog.e("time = " + (System.currentTimeMillis() - time));
                preferences.edit().putBoolean("isPreviousOk", true).commit();
                didWriteData(messageObject, file, true, error);
                return true;
            } catch (Throwable th222) {
                th = th222;
                extractor = extractor2;
                videoStartTime = videoStartTime2;
            }
        } catch (Exception e7) {
            e322 = e7;
            videoStartTime = videoStartTime2;
            error = true;
            FileLog.e(e322);
            if (extractor != null) {
                extractor.release();
            }
            if (mP4Builder != null) {
                mP4Builder.finishMovie();
            }
            FileLog.e("time = " + (System.currentTimeMillis() - time));
            preferences.edit().putBoolean("isPreviousOk", true).commit();
            didWriteData(messageObject, file, true, error);
            return true;
        } catch (Throwable th5) {
            th = th5;
            videoStartTime = videoStartTime2;
            if (extractor != null) {
                extractor.release();
            }
            if (mP4Builder != null) {
                mP4Builder.finishMovie();
            }
            FileLog.e("time = " + (System.currentTimeMillis() - time));
            throw th;
        }
        preferences.edit().putBoolean("isPreviousOk", true).commit();
        didWriteData(messageObject, file, true, error);
        return true;
    }
}
