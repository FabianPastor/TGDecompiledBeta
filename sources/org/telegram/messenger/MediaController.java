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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSink;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.PhotoFilterView.CurvesToolValue;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.PhotoViewer;

public class MediaController implements SensorEventListener, OnAudioFocusChangeListener, NotificationCenterDelegate {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
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
    private long lastProgress = 0;
    private float lastProximityValue = -100.0f;
    private EncryptedChat lastSecretChat;
    private long lastTimestamp = 0;
    private User lastUser;
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
    private WakeLock proximityWakeLock;
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
    private Runnable recordRunnable = new C02541();
    private short[] recordSamples = new short[1024];
    private Runnable recordStartRunnable;
    private long recordStartTime;
    private long recordTimeCount;
    private TL_document recordingAudio;
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

    /* renamed from: org.telegram.messenger.MediaController$1 */
    class C02541 implements Runnable {
        C02541() {
        }

        public void run() {
            Throwable th;
            final double sqrt;
            if (MediaController.this.audioRecorder != null) {
                ByteBuffer allocateDirect;
                boolean z = false;
                if (MediaController.this.recordBuffers.isEmpty()) {
                    allocateDirect = ByteBuffer.allocateDirect(MediaController.this.recordBufferSize);
                    allocateDirect.order(ByteOrder.nativeOrder());
                } else {
                    allocateDirect = (ByteBuffer) MediaController.this.recordBuffers.get(0);
                    MediaController.this.recordBuffers.remove(0);
                }
                allocateDirect.rewind();
                int read = MediaController.this.audioRecorder.read(allocateDirect, allocateDirect.capacity());
                if (read > 0) {
                    double d;
                    allocateDirect.limit(read);
                    try {
                        long access$300 = MediaController.this.samplesCount + ((long) (read / 2));
                        int access$3002 = (int) ((((double) MediaController.this.samplesCount) / ((double) access$300)) * ((double) MediaController.this.recordSamples.length));
                        int length = MediaController.this.recordSamples.length - access$3002;
                        float f = 0.0f;
                        if (access$3002 != 0) {
                            float length2 = ((float) MediaController.this.recordSamples.length) / ((float) access$3002);
                            float f2 = 0.0f;
                            for (int i = 0; i < access$3002; i++) {
                                MediaController.this.recordSamples[i] = MediaController.this.recordSamples[(int) f2];
                                f2 += length2;
                            }
                        }
                        float f3 = (((float) read) / 2.0f) / ((float) length);
                        int i2 = 0;
                        d = 0.0d;
                        while (i2 < read / 2) {
                            try {
                                short s = allocateDirect.getShort();
                                if (s > (short) 2500) {
                                    d += (double) (s * s);
                                }
                                if (i2 == ((int) f) && access$3002 < MediaController.this.recordSamples.length) {
                                    MediaController.this.recordSamples[access$3002] = s;
                                    f += f3;
                                    access$3002++;
                                }
                                i2++;
                            } catch (Throwable e) {
                                th = e;
                            }
                        }
                        MediaController.this.samplesCount = access$300;
                    } catch (Throwable e2) {
                        th = e2;
                        d = 0.0d;
                        FileLog.m3e(th);
                        allocateDirect.position(0);
                        sqrt = Math.sqrt((d / ((double) read)) / 2.0d);
                        if (read != allocateDirect.capacity()) {
                            z = true;
                        }
                        if (read != 0) {
                            MediaController.this.fileEncodingQueue.postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.MediaController$1$1$1 */
                                class C02481 implements Runnable {
                                    C02481() {
                                    }

                                    public void run() {
                                        MediaController.this.recordBuffers.add(allocateDirect);
                                    }
                                }

                                public void run() {
                                    while (allocateDirect.hasRemaining()) {
                                        int limit;
                                        if (allocateDirect.remaining() > MediaController.this.fileBuffer.remaining()) {
                                            limit = allocateDirect.limit();
                                            allocateDirect.limit(MediaController.this.fileBuffer.remaining() + allocateDirect.position());
                                        } else {
                                            limit = -1;
                                        }
                                        MediaController.this.fileBuffer.put(allocateDirect);
                                        if (MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit() || z) {
                                            if (MediaController.this.writeFrame(MediaController.this.fileBuffer, !z ? MediaController.this.fileBuffer.limit() : allocateDirect.position()) != 0) {
                                                MediaController.this.fileBuffer.rewind();
                                                MediaController.this.recordTimeCount = MediaController.this.recordTimeCount + ((long) ((MediaController.this.fileBuffer.limit() / 2) / 16));
                                            }
                                        }
                                        if (limit != -1) {
                                            allocateDirect.limit(limit);
                                        }
                                    }
                                    MediaController.this.recordQueue.postRunnable(new C02481());
                                }
                            });
                        }
                        MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(sqrt));
                            }
                        });
                        return;
                    }
                    allocateDirect.position(0);
                    sqrt = Math.sqrt((d / ((double) read)) / 2.0d);
                    if (read != allocateDirect.capacity()) {
                        z = true;
                    }
                    if (read != 0) {
                        MediaController.this.fileEncodingQueue.postRunnable(/* anonymous class already generated */);
                    }
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    return;
                }
                MediaController.this.recordBuffers.add(allocateDirect);
                MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$3 */
    class C02663 implements Runnable {
        C02663() {
        }

        public void run() {
            try {
                MediaController.this.recordBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
                if (MediaController.this.recordBufferSize <= 0) {
                    MediaController.this.recordBufferSize = 1280;
                }
                MediaController.this.playerBufferSize = AudioTrack.getMinBufferSize(48000, 4, 2);
                if (MediaController.this.playerBufferSize <= 0) {
                    MediaController.this.playerBufferSize = 3840;
                }
                int i = 0;
                for (int i2 = 0; i2 < 5; i2++) {
                    ByteBuffer allocateDirect = ByteBuffer.allocateDirect(4096);
                    allocateDirect.order(ByteOrder.nativeOrder());
                    MediaController.this.recordBuffers.add(allocateDirect);
                }
                while (i < 3) {
                    MediaController.this.freePlayerBuffers.add(new AudioBuffer(MediaController.this.playerBufferSize));
                    i++;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$4 */
    class C02694 implements Runnable {

        /* renamed from: org.telegram.messenger.MediaController$4$1 */
        class C02681 extends PhoneStateListener {
            C02681() {
            }

            public void onCallStateChanged(final int i, String str) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        EmbedBottomSheet instance;
                        if (i == 1) {
                            if (MediaController.this.isPlayingMessage(MediaController.this.playingMessageObject) && !MediaController.this.isMessagePaused()) {
                                MediaController.this.pauseMessage(MediaController.this.playingMessageObject);
                            } else if (!(MediaController.this.recordStartRunnable == null && MediaController.this.recordingAudio == null)) {
                                MediaController.this.stopRecording(2);
                            }
                            instance = EmbedBottomSheet.getInstance();
                            if (instance != null) {
                                instance.pause();
                            }
                            MediaController.this.callInProgress = true;
                        } else if (i == 0) {
                            MediaController.this.callInProgress = false;
                        } else if (i == 2) {
                            instance = EmbedBottomSheet.getInstance();
                            if (instance != null) {
                                instance.pause();
                            }
                            MediaController.this.callInProgress = true;
                        }
                    }
                });
            }
        }

        C02694() {
        }

        public void run() {
            try {
                MediaController.this.sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
                MediaController.this.linearSensor = MediaController.this.sensorManager.getDefaultSensor(10);
                MediaController.this.gravitySensor = MediaController.this.sensorManager.getDefaultSensor(9);
                if (MediaController.this.linearSensor == null || MediaController.this.gravitySensor == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("gravity or linear sensor not found");
                    }
                    MediaController.this.accelerometerSensor = MediaController.this.sensorManager.getDefaultSensor(1);
                    MediaController.this.linearSensor = null;
                    MediaController.this.gravitySensor = null;
                }
                MediaController.this.proximitySensor = MediaController.this.sensorManager.getDefaultSensor(8);
                MediaController.this.proximityWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            try {
                PhoneStateListener c02681 = new C02681();
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    telephonyManager.listen(c02681, 32);
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$5 */
    class C02705 implements Runnable {
        C02705() {
        }

        public void run() {
            for (int i = 0; i < 3; i++) {
                NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.FileDidLoaded);
                NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.httpFileDidLoaded);
                NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.didReceivedNewMessages);
                NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.messagesDeleted);
                NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.removeAllMessagesFromDialog);
                NotificationCenter.getInstance(i).addObserver(MediaController.this, NotificationCenter.musicDidLoaded);
                NotificationCenter.getGlobalInstance().addObserver(MediaController.this, NotificationCenter.playerDidStartPlaying);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$7 */
    class C02737 implements Runnable {
        C02737() {
        }

        public void run() {
            try {
                if (MediaController.this.smsObserver != null) {
                    ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.smsObserver);
                    MediaController.this.smsObserver = null;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$9 */
    class C02759 implements Runnable {
        C02759() {
        }

        public void run() {
            if (MediaController.this.decodingFinished) {
                MediaController.this.checkPlayerQueue();
                return;
            }
            int i = 0;
            while (true) {
                AudioBuffer audioBuffer = null;
                synchronized (MediaController.this.playerSync) {
                    if (!MediaController.this.freePlayerBuffers.isEmpty()) {
                        audioBuffer = (AudioBuffer) MediaController.this.freePlayerBuffers.get(0);
                        MediaController.this.freePlayerBuffers.remove(0);
                    }
                    if (!MediaController.this.usedPlayerBuffers.isEmpty()) {
                        i = true;
                    }
                }
                if (audioBuffer == null) {
                    break;
                }
                MediaController.this.readOpusFile(audioBuffer.buffer, MediaController.this.playerBufferSize, MediaController.readArgs);
                audioBuffer.size = MediaController.readArgs[0];
                audioBuffer.pcmOffset = (long) MediaController.readArgs[1];
                audioBuffer.finished = MediaController.readArgs[2];
                if (audioBuffer.finished == 1) {
                    MediaController.this.decodingFinished = true;
                }
                if (audioBuffer.size == 0) {
                    break;
                }
                audioBuffer.buffer.rewind();
                audioBuffer.buffer.get(audioBuffer.bufferBytes);
                synchronized (MediaController.this.playerSync) {
                    MediaController.this.usedPlayerBuffers.add(audioBuffer);
                }
                boolean z = true;
            }
            synchronized (MediaController.this.playerSync) {
                MediaController.this.freePlayerBuffers.add(audioBuffer);
            }
            if (i != 0) {
                MediaController.this.checkPlayerQueue();
            }
        }
    }

    public static class AlbumEntry {
        public int bucketId;
        public String bucketName;
        public PhotoEntry coverPhoto;
        public ArrayList<PhotoEntry> photos = new ArrayList();
        public SparseArray<PhotoEntry> photosByIds = new SparseArray();

        public AlbumEntry(int i, String str, PhotoEntry photoEntry) {
            this.bucketId = i;
            this.bucketName = str;
            this.coverPhoto = photoEntry;
        }

        public void addPhoto(PhotoEntry photoEntry) {
            this.photos.add(photoEntry);
            this.photosByIds.put(photoEntry.imageId, photoEntry);
        }
    }

    private class AudioBuffer {
        ByteBuffer buffer;
        byte[] bufferBytes;
        int finished;
        long pcmOffset;
        int size;

        public AudioBuffer(int i) {
            this.buffer = ByteBuffer.allocateDirect(i);
            this.bufferBytes = new byte[i];
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

        public void onChange(boolean z) {
            super.onChange(z);
            MediaController.this.processMediaObserver(Media.EXTERNAL_CONTENT_URI);
        }
    }

    private class GalleryObserverExternal extends ContentObserver {

        /* renamed from: org.telegram.messenger.MediaController$GalleryObserverExternal$1 */
        class C02761 implements Runnable {
            C02761() {
            }

            public void run() {
                MediaController.refreshGalleryRunnable = null;
                MediaController.loadGalleryPhotosAlbums(0);
            }
        }

        public GalleryObserverExternal() {
            super(null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            if (MediaController.refreshGalleryRunnable) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new C02761(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    private class GalleryObserverInternal extends ContentObserver {

        /* renamed from: org.telegram.messenger.MediaController$GalleryObserverInternal$1 */
        class C02771 implements Runnable {
            C02771() {
            }

            public void run() {
                if (PhotoViewer.getInstance().isVisible()) {
                    GalleryObserverInternal.this.scheduleReloadRunnable();
                    return;
                }
                MediaController.refreshGalleryRunnable = null;
                MediaController.loadGalleryPhotosAlbums(0);
            }
        }

        public GalleryObserverInternal() {
            super(null);
        }

        private void scheduleReloadRunnable() {
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new C02771(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            if (MediaController.refreshGalleryRunnable) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            scheduleReloadRunnable();
        }
    }

    private class InternalObserver extends ContentObserver {
        public InternalObserver() {
            super(null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            MediaController.this.processMediaObserver(Media.INTERNAL_CONTENT_URI);
        }
    }

    public static class PhotoEntry {
        public int bucketId;
        public CharSequence caption;
        public long dateTaken;
        public int duration;
        public VideoEditedInfo editedInfo;
        public ArrayList<MessageEntity> entities;
        public int imageId;
        public String imagePath;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isMuted;
        public boolean isPainted;
        public boolean isVideo;
        public int orientation;
        public String path;
        public SavedFilterState savedFilterState;
        public ArrayList<InputDocument> stickers = new ArrayList();
        public String thumbPath;
        public int ttl;

        public PhotoEntry(int i, int i2, long j, String str, int i3, boolean z) {
            this.bucketId = i;
            this.imageId = i2;
            this.dateTaken = j;
            this.path = str;
            if (z) {
                this.duration = i3;
            } else {
                this.orientation = i3;
            }
            this.isVideo = z;
        }

        public void reset() {
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

    public static class SavedFilterState {
        public float blurAngle;
        public float blurExcludeBlurSize;
        public Point blurExcludePoint;
        public float blurExcludeSize;
        public int blurType;
        public float contrastValue;
        public CurvesToolValue curvesToolValue = new CurvesToolValue();
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

    public static class SearchImage {
        public CharSequence caption;
        public int date;
        public Document document;
        public ArrayList<MessageEntity> entities;
        public int height;
        public String id;
        public String imagePath;
        public String imageUrl;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isPainted;
        public String localUrl;
        public SavedFilterState savedFilterState;
        public int size;
        public ArrayList<InputDocument> stickers = new ArrayList();
        public String thumbPath;
        public String thumbUrl;
        public int ttl;
        public int type;
        public int width;

        public void reset() {
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

    private class SmsObserver extends ContentObserver {
        public SmsObserver() {
            super(null);
        }

        public void onChange(boolean z) {
            MediaController.this.readSms();
        }
    }

    private final class StopMediaObserverRunnable implements Runnable {
        public int currentObserverToken;

        private StopMediaObserverRunnable() {
            this.currentObserverToken = null;
        }

        public void run() {
            if (this.currentObserverToken == MediaController.this.startObserverToken) {
                try {
                    if (MediaController.this.internalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
                        MediaController.this.internalObserver = null;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                try {
                    if (MediaController.this.externalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
                        MediaController.this.externalObserver = null;
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
        }
    }

    private static class VideoConvertRunnable implements Runnable {
        private MessageObject messageObject;

        private VideoConvertRunnable(MessageObject messageObject) {
            this.messageObject = messageObject;
        }

        public void run() {
            MediaController.getInstance().convertVideo(this.messageObject);
        }

        public static void runConversion(final MessageObject messageObject) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread thread = new Thread(new VideoConvertRunnable(messageObject), "VideoConvertRunnable");
                        thread.start();
                        thread.join();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            }).start();
        }
    }

    private native void closeOpusFile();

    private native long getTotalPcmDuration();

    public static native int isOpusFile(String str);

    private static boolean isRecognizedFormat(int i) {
        if (!(i == 39 || i == NUM)) {
            switch (i) {
                case 19:
                case 20:
                case 21:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private native int openOpusFile(String str);

    private native void readOpusFile(ByteBuffer byteBuffer, int i, int[] iArr);

    private void readSms() {
    }

    private native int seekOpusFile(float f);

    private native int startRecord(String str);

    private native void stopRecord();

    private native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public static void checkGallery() {
        if (VERSION.SDK_INT >= 24) {
            if (allPhotosAlbumEntry != null) {
                final int size = allPhotosAlbumEntry.photos.size();
                Utilities.globalQueue.postRunnable(new Runnable() {
                    @SuppressLint({"NewApi"})
                    public void run() {
                        Cursor query;
                        int i;
                        Cursor query2;
                        Throwable th;
                        Throwable th2;
                        Cursor cursor = null;
                        try {
                            if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                                query = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Media.EXTERNAL_CONTENT_URI, new String[]{"COUNT(_id)"}, null, null, null);
                                if (query != null) {
                                    try {
                                        if (query.moveToNext()) {
                                            i = query.getInt(0) + 0;
                                            if (query != null) {
                                                query.close();
                                            }
                                            if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                                                query2 = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, new String[]{"COUNT(_id)"}, null, null, null);
                                                if (query2 != null) {
                                                    try {
                                                        if (query2.moveToNext()) {
                                                            i += query2.getInt(0);
                                                        }
                                                    } catch (Throwable th3) {
                                                        th = th3;
                                                        query = query2;
                                                        if (query != null) {
                                                            query.close();
                                                        }
                                                        throw th;
                                                    }
                                                }
                                            }
                                            query2 = query;
                                            if (query2 != null) {
                                                query2.close();
                                            }
                                            if (size != i) {
                                                if (MediaController.refreshGalleryRunnable != null) {
                                                    AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
                                                    MediaController.refreshGalleryRunnable = null;
                                                }
                                                MediaController.loadGalleryPhotosAlbums(0);
                                            }
                                        }
                                    } catch (Throwable th4) {
                                        th2 = th4;
                                        try {
                                            FileLog.m3e(th2);
                                            if (query != null) {
                                                query.close();
                                            }
                                            i = 0;
                                            if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                                                query2 = query;
                                            } else {
                                                query2 = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, new String[]{"COUNT(_id)"}, null, null, null);
                                                if (query2 != null) {
                                                    if (query2.moveToNext()) {
                                                        i += query2.getInt(0);
                                                    }
                                                }
                                            }
                                            if (query2 != null) {
                                                query2.close();
                                            }
                                            if (size != i) {
                                                if (MediaController.refreshGalleryRunnable != null) {
                                                    AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
                                                    MediaController.refreshGalleryRunnable = null;
                                                }
                                                MediaController.loadGalleryPhotosAlbums(0);
                                            }
                                        } catch (Throwable th5) {
                                            th = th5;
                                            cursor = query;
                                            if (cursor != null) {
                                                cursor.close();
                                            }
                                            throw th;
                                        }
                                    }
                                }
                            }
                            query = null;
                            i = 0;
                            if (query != null) {
                                query.close();
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            if (cursor != null) {
                                cursor.close();
                            }
                            throw th;
                        }
                        try {
                            if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                                query2 = query;
                            } else {
                                query2 = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, new String[]{"COUNT(_id)"}, null, null, null);
                                if (query2 != null) {
                                    if (query2.moveToNext()) {
                                        i += query2.getInt(0);
                                    }
                                }
                            }
                            if (query2 != null) {
                                query2.close();
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            FileLog.m3e(th);
                            if (query != null) {
                                query.close();
                            }
                            if (size != i) {
                                if (MediaController.refreshGalleryRunnable != null) {
                                    AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
                                    MediaController.refreshGalleryRunnable = null;
                                }
                                MediaController.loadGalleryPhotosAlbums(0);
                            }
                        }
                        if (size != i) {
                            if (MediaController.refreshGalleryRunnable != null) {
                                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
                                MediaController.refreshGalleryRunnable = null;
                            }
                            MediaController.loadGalleryPhotosAlbums(0);
                        }
                    }
                }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        }
    }

    public static MediaController getInstance() {
        MediaController mediaController = Instance;
        if (mediaController == null) {
            synchronized (MediaController.class) {
                mediaController = Instance;
                if (mediaController == null) {
                    mediaController = new MediaController();
                    Instance = mediaController;
                }
            }
        }
        return mediaController;
    }

    public MediaController() {
        this.recordQueue.setPriority(10);
        this.fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue.setPriority(10);
        this.playerQueue = new DispatchQueue("playerQueue");
        this.fileDecodingQueue = new DispatchQueue("fileDecodingQueue");
        this.recordQueue.postRunnable(new C02663());
        Utilities.globalQueue.postRunnable(new C02694());
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new C02705());
        this.mediaProjections = new String[]{"_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height"};
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        try {
            contentResolver.registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        try {
            contentResolver.registerContentObserver(Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        try {
            contentResolver.registerContentObserver(Video.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Throwable e22) {
            FileLog.m3e(e22);
        }
        try {
            contentResolver.registerContentObserver(Video.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Throwable e3) {
            FileLog.m3e(e3);
        }
    }

    public void onAudioFocusChange(int i) {
        if (i == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) != 0 && isMessagePaused() == 0) {
                pauseMessage(this.playingMessageObject);
            }
            this.hasAudioFocus = 0;
            this.audioFocus = 0;
        } else if (i == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain != 0) {
                this.resumeAudioOnFocusGain = false;
                if (!(isPlayingMessage(getPlayingMessageObject()) == 0 || isMessagePaused() == 0)) {
                    playMessage(getPlayingMessageObject());
                }
            }
        } else if (i == -3) {
            this.audioFocus = 1;
        } else if (i == -2) {
            this.audioFocus = 0;
            if (isPlayingMessage(getPlayingMessageObject()) != 0 && isMessagePaused() == 0) {
                pauseMessage(this.playingMessageObject);
                this.resumeAudioOnFocusGain = true;
            }
        }
        setPlayerVolume();
    }

    private void setPlayerVolume() {
        try {
            float f = this.audioFocus != 1 ? VOLUME_NORMAL : VOLUME_DUCK;
            if (this.audioPlayer != null) {
                this.audioPlayer.setVolume(f);
            } else if (this.audioTrackPlayer != null) {
                this.audioTrackPlayer.setStereoVolume(f, f);
            } else if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(f);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void startProgressTimer(final MessageObject messageObject) {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
            messageObject.getFileName();
            this.progressTimer = new Timer();
            this.progressTimer.schedule(new TimerTask() {

                /* renamed from: org.telegram.messenger.MediaController$6$1 */
                class C02711 implements Runnable {
                    C02711() {
                    }

                    public void run() {
                        if (!(messageObject == null || ((MediaController.this.audioPlayer == null && MediaController.this.audioTrackPlayer == null && MediaController.this.videoPlayer == null) || MediaController.this.isPaused))) {
                            try {
                                if (MediaController.this.ignoreFirstProgress != 0) {
                                    MediaController.this.ignoreFirstProgress = MediaController.this.ignoreFirstProgress - 1;
                                    return;
                                }
                                long duration;
                                long currentPosition;
                                float bufferedPosition;
                                float f = 0.0f;
                                if (MediaController.this.videoPlayer != null) {
                                    duration = MediaController.this.videoPlayer.getDuration();
                                    currentPosition = MediaController.this.videoPlayer.getCurrentPosition();
                                    float f2 = (float) duration;
                                    bufferedPosition = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / f2;
                                    if (duration >= 0) {
                                        f = ((float) currentPosition) / f2;
                                    }
                                    if (currentPosition >= 0 && f < MediaController.VOLUME_NORMAL) {
                                        float f3 = f;
                                        f = bufferedPosition;
                                        bufferedPosition = f3;
                                    } else {
                                        return;
                                    }
                                } else if (MediaController.this.audioPlayer != null) {
                                    duration = MediaController.this.audioPlayer.getDuration();
                                    currentPosition = MediaController.this.audioPlayer.getCurrentPosition();
                                    bufferedPosition = (duration == C0542C.TIME_UNSET || duration < 0) ? 0.0f : ((float) currentPosition) / ((float) duration);
                                    float bufferedPosition2 = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) duration);
                                    if (duration != C0542C.TIME_UNSET && currentPosition >= 0) {
                                        if (MediaController.this.seekToProgressPending == 0.0f) {
                                            f = bufferedPosition2;
                                        }
                                    }
                                    return;
                                } else {
                                    currentPosition = (long) ((int) (((float) MediaController.this.lastPlayPcm) / 48.0f));
                                    bufferedPosition = ((float) MediaController.this.lastPlayPcm) / ((float) MediaController.this.currentTotalPcmDuration);
                                    if (currentPosition != MediaController.this.lastProgress) {
                                        duration = 0;
                                    } else {
                                        return;
                                    }
                                }
                                MediaController.this.lastProgress = currentPosition;
                                messageObject.audioPlayerDuration = (int) (duration / 1000);
                                messageObject.audioProgress = bufferedPosition;
                                messageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                                messageObject.bufferedProgress = f;
                                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(messageObject.getId()), Float.valueOf(bufferedPosition));
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    }
                }

                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new C02711());
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
                    FileLog.m3e(e);
                }
            }
        }
    }

    public void cleanup() {
        int i = 0;
        cleanupPlayer(false, true);
        this.audioInfo = null;
        this.playMusicAgain = false;
        while (i < 3) {
            DownloadController.getInstance(i).cleanup();
            i++;
        }
        this.videoConvertQueue.clear();
        this.playlist.clear();
        this.shuffledPlaylist.clear();
        this.generatingWaveform.clear();
        this.voiceMessagesPlaylist = null;
        this.voiceMessagesPlaylistMap = null;
        cancelVideoConvert(null);
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
            FileLog.m3e(e);
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
            FileLog.m3e(e2);
        }
    }

    public void startSmsObserver() {
        try {
            if (this.smsObserver == null) {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri parse = Uri.parse("content://sms");
                ContentObserver smsObserver = new SmsObserver();
                this.smsObserver = smsObserver;
                contentResolver.registerContentObserver(parse, false, smsObserver);
            }
            AndroidUtilities.runOnUIThread(new C02737(), 300000);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void stopMediaObserver() {
        if (this.stopMediaObserverRunnable == null) {
            this.stopMediaObserverRunnable = new StopMediaObserverRunnable();
        }
        this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
        ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    private void processMediaObserver(android.net.Uri r14) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r13 = this;
        r0 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();	 Catch:{ Exception -> 0x00c7 }
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00c7 }
        r2 = r1.getContentResolver();	 Catch:{ Exception -> 0x00c7 }
        r4 = r13.mediaProjections;	 Catch:{ Exception -> 0x00c7 }
        r5 = 0;	 Catch:{ Exception -> 0x00c7 }
        r6 = 0;	 Catch:{ Exception -> 0x00c7 }
        r7 = "date_added DESC LIMIT 1";	 Catch:{ Exception -> 0x00c7 }
        r3 = r14;	 Catch:{ Exception -> 0x00c7 }
        r14 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x00c7 }
        r1 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00c7 }
        r1.<init>();	 Catch:{ Exception -> 0x00c7 }
        if (r14 == 0) goto L_0x00b8;	 Catch:{ Exception -> 0x00c7 }
    L_0x001c:
        r2 = r14.moveToNext();	 Catch:{ Exception -> 0x00c7 }
        if (r2 == 0) goto L_0x00b5;	 Catch:{ Exception -> 0x00c7 }
    L_0x0022:
        r2 = 0;	 Catch:{ Exception -> 0x00c7 }
        r2 = r14.getString(r2);	 Catch:{ Exception -> 0x00c7 }
        r3 = 1;	 Catch:{ Exception -> 0x00c7 }
        r4 = r14.getString(r3);	 Catch:{ Exception -> 0x00c7 }
        r5 = 2;	 Catch:{ Exception -> 0x00c7 }
        r5 = r14.getString(r5);	 Catch:{ Exception -> 0x00c7 }
        r6 = 3;	 Catch:{ Exception -> 0x00c7 }
        r6 = r14.getLong(r6);	 Catch:{ Exception -> 0x00c7 }
        r8 = 4;	 Catch:{ Exception -> 0x00c7 }
        r8 = r14.getString(r8);	 Catch:{ Exception -> 0x00c7 }
        r9 = 5;	 Catch:{ Exception -> 0x00c7 }
        r9 = r14.getInt(r9);	 Catch:{ Exception -> 0x00c7 }
        r10 = 6;	 Catch:{ Exception -> 0x00c7 }
        r10 = r14.getInt(r10);	 Catch:{ Exception -> 0x00c7 }
        if (r2 == 0) goto L_0x0053;	 Catch:{ Exception -> 0x00c7 }
    L_0x0047:
        r11 = r2.toLowerCase();	 Catch:{ Exception -> 0x00c7 }
        r12 = "screenshot";	 Catch:{ Exception -> 0x00c7 }
        r11 = r11.contains(r12);	 Catch:{ Exception -> 0x00c7 }
        if (r11 != 0) goto L_0x007d;	 Catch:{ Exception -> 0x00c7 }
    L_0x0053:
        if (r4 == 0) goto L_0x0061;	 Catch:{ Exception -> 0x00c7 }
    L_0x0055:
        r4 = r4.toLowerCase();	 Catch:{ Exception -> 0x00c7 }
        r11 = "screenshot";	 Catch:{ Exception -> 0x00c7 }
        r4 = r4.contains(r11);	 Catch:{ Exception -> 0x00c7 }
        if (r4 != 0) goto L_0x007d;	 Catch:{ Exception -> 0x00c7 }
    L_0x0061:
        if (r5 == 0) goto L_0x006f;	 Catch:{ Exception -> 0x00c7 }
    L_0x0063:
        r4 = r5.toLowerCase();	 Catch:{ Exception -> 0x00c7 }
        r5 = "screenshot";	 Catch:{ Exception -> 0x00c7 }
        r4 = r4.contains(r5);	 Catch:{ Exception -> 0x00c7 }
        if (r4 != 0) goto L_0x007d;	 Catch:{ Exception -> 0x00c7 }
    L_0x006f:
        if (r8 == 0) goto L_0x001c;	 Catch:{ Exception -> 0x00c7 }
    L_0x0071:
        r4 = r8.toLowerCase();	 Catch:{ Exception -> 0x00c7 }
        r5 = "screenshot";	 Catch:{ Exception -> 0x00c7 }
        r4 = r4.contains(r5);	 Catch:{ Exception -> 0x00c7 }
        if (r4 == 0) goto L_0x001c;
    L_0x007d:
        if (r9 == 0) goto L_0x0081;
    L_0x007f:
        if (r10 != 0) goto L_0x008f;
    L_0x0081:
        r4 = new android.graphics.BitmapFactory$Options;	 Catch:{ Exception -> 0x00ac }
        r4.<init>();	 Catch:{ Exception -> 0x00ac }
        r4.inJustDecodeBounds = r3;	 Catch:{ Exception -> 0x00ac }
        android.graphics.BitmapFactory.decodeFile(r2, r4);	 Catch:{ Exception -> 0x00ac }
        r9 = r4.outWidth;	 Catch:{ Exception -> 0x00ac }
        r10 = r4.outHeight;	 Catch:{ Exception -> 0x00ac }
    L_0x008f:
        if (r9 <= 0) goto L_0x00a3;	 Catch:{ Exception -> 0x00ac }
    L_0x0091:
        if (r10 <= 0) goto L_0x00a3;	 Catch:{ Exception -> 0x00ac }
    L_0x0093:
        r2 = r0.x;	 Catch:{ Exception -> 0x00ac }
        if (r9 != r2) goto L_0x009b;	 Catch:{ Exception -> 0x00ac }
    L_0x0097:
        r2 = r0.y;	 Catch:{ Exception -> 0x00ac }
        if (r10 == r2) goto L_0x00a3;	 Catch:{ Exception -> 0x00ac }
    L_0x009b:
        r2 = r0.x;	 Catch:{ Exception -> 0x00ac }
        if (r10 != r2) goto L_0x001c;	 Catch:{ Exception -> 0x00ac }
    L_0x009f:
        r2 = r0.y;	 Catch:{ Exception -> 0x00ac }
        if (r9 != r2) goto L_0x001c;	 Catch:{ Exception -> 0x00ac }
    L_0x00a3:
        r2 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x00ac }
        r1.add(r2);	 Catch:{ Exception -> 0x00ac }
        goto L_0x001c;
    L_0x00ac:
        r2 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x00c7 }
        r1.add(r2);	 Catch:{ Exception -> 0x00c7 }
        goto L_0x001c;	 Catch:{ Exception -> 0x00c7 }
    L_0x00b5:
        r14.close();	 Catch:{ Exception -> 0x00c7 }
    L_0x00b8:
        r14 = r1.isEmpty();	 Catch:{ Exception -> 0x00c7 }
        if (r14 != 0) goto L_0x00cb;	 Catch:{ Exception -> 0x00c7 }
    L_0x00be:
        r14 = new org.telegram.messenger.MediaController$8;	 Catch:{ Exception -> 0x00c7 }
        r14.<init>(r1);	 Catch:{ Exception -> 0x00c7 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r14);	 Catch:{ Exception -> 0x00c7 }
        goto L_0x00cb;
    L_0x00c7:
        r14 = move-exception;
        org.telegram.messenger.FileLog.m3e(r14);
    L_0x00cb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.processMediaObserver(android.net.Uri):void");
    }

    private void checkScreenshots(ArrayList<Long> arrayList) {
        if (!(arrayList == null || arrayList.isEmpty() || this.lastChatEnterTime == 0)) {
            if (this.lastUser != null || (this.lastSecretChat instanceof TL_encryptedChat)) {
                int i = 0;
                int i2 = 0;
                while (i < arrayList.size()) {
                    Long l = (Long) arrayList.get(i);
                    if (this.lastMediaCheckTime == 0 || l.longValue() > this.lastMediaCheckTime) {
                        if (l.longValue() >= this.lastChatEnterTime && (this.lastChatLeaveTime == 0 || l.longValue() <= this.lastChatLeaveTime + AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS)) {
                            this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, l.longValue());
                            i2 = 1;
                        }
                    }
                    i++;
                }
                if (i2 != 0) {
                    if (this.lastSecretChat != null) {
                        SecretChatHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastSecretChat, this.lastChatVisibleMessages, null);
                    } else {
                        SendMessagesHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastUser, this.lastMessageId, null);
                    }
                }
            }
        }
    }

    public void setLastVisibleMessageIds(int i, long j, long j2, User user, EncryptedChat encryptedChat, ArrayList<Long> arrayList, int i2) {
        this.lastChatEnterTime = j;
        this.lastChatLeaveTime = j2;
        this.lastChatAccount = i;
        this.lastSecretChat = encryptedChat;
        this.lastUser = user;
        this.lastMessageId = i2;
        this.lastChatVisibleMessages = arrayList;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i != NotificationCenter.FileDidLoaded) {
            if (i != NotificationCenter.httpFileDidLoaded) {
                if (i == NotificationCenter.messagesDeleted) {
                    i = ((Integer) objArr[1]).intValue();
                    ArrayList arrayList = (ArrayList) objArr[0];
                    if (!(this.playingMessageObject == null || i != this.playingMessageObject.messageOwner.to_id.channel_id || arrayList.contains(Integer.valueOf(this.playingMessageObject.getId())) == null)) {
                        cleanupPlayer(true, true);
                    }
                    if (this.voiceMessagesPlaylist != null && this.voiceMessagesPlaylist.isEmpty() == null && i == ((MessageObject) this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id) {
                        while (i3 < arrayList.size()) {
                            Integer num = (Integer) arrayList.get(i3);
                            MessageObject messageObject = (MessageObject) this.voiceMessagesPlaylistMap.get(num.intValue());
                            this.voiceMessagesPlaylistMap.remove(num.intValue());
                            if (messageObject != null) {
                                this.voiceMessagesPlaylist.remove(messageObject);
                            }
                            i3++;
                        }
                        return;
                    }
                    return;
                } else if (i == NotificationCenter.removeAllMessagesFromDialog) {
                    i = ((Long) objArr[0]).longValue();
                    if (this.playingMessageObject != null && this.playingMessageObject.getDialogId() == i) {
                        cleanupPlayer(false, true);
                        return;
                    }
                    return;
                } else if (i == NotificationCenter.musicDidLoaded) {
                    i = ((Long) objArr[0]).longValue();
                    if (this.playingMessageObject != null && this.playingMessageObject.isMusic() && this.playingMessageObject.getDialogId() == i) {
                        r6 = (ArrayList) objArr[1];
                        this.playlist.addAll(0, r6);
                        if (SharedConfig.shuffleMusic != 0) {
                            buildShuffledPlayList();
                            this.currentPlaylistNum = 0;
                            return;
                        }
                        this.currentPlaylistNum += r6.size();
                        return;
                    }
                    return;
                } else if (i == NotificationCenter.didReceivedNewMessages) {
                    if (this.voiceMessagesPlaylist != 0 && this.voiceMessagesPlaylist.isEmpty() == 0) {
                        if (((Long) objArr[0]).longValue() == ((MessageObject) this.voiceMessagesPlaylist.get(0)).getDialogId()) {
                            r6 = (ArrayList) objArr[1];
                            while (i3 < r6.size()) {
                                MessageObject messageObject2 = (MessageObject) r6.get(i3);
                                if (!(messageObject2.isVoice() == null && messageObject2.isRoundVideo() == null) && (this.voiceMessagesPlaylistUnread == null || (messageObject2.isContentUnread() != null && messageObject2.isOut() == null))) {
                                    this.voiceMessagesPlaylist.add(messageObject2);
                                    this.voiceMessagesPlaylistMap.put(messageObject2.getId(), messageObject2);
                                }
                                i3++;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                } else if (i == NotificationCenter.playerDidStartPlaying) {
                    if (getInstance().isCurrentPlayer((VideoPlayer) objArr[0]) == 0) {
                        getInstance().pauseMessage(getInstance().getPlayingMessageObject());
                        return;
                    }
                    return;
                } else {
                    return;
                }
            }
        }
        String str = (String) objArr[0];
        if (this.downloadingCurrentMessage != null && this.playingMessageObject != null && this.playingMessageObject.currentAccount == i2 && FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(str) != 0) {
            this.playMusicAgain = true;
            playMessage(this.playingMessageObject);
        }
    }

    private void checkDecoderQueue() {
        this.fileDecodingQueue.postRunnable(new C02759());
    }

    private void checkPlayerQueue() {
        this.playerQueue.postRunnable(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (MediaController.this.playerObjectSync) {
                    if (MediaController.this.audioTrackPlayer != null) {
                        if (MediaController.this.audioTrackPlayer.getPlayState() != 3) {
                        }
                    }
                    return;
                }
                MediaController.this.buffersWrited = MediaController.this.buffersWrited + 1;
                int i;
                if (i > 0) {
                    final long j = r0.pcmOffset;
                    if (r0.finished != 1) {
                        i = -1;
                    }
                    final int i2 = i;
                    final int access$5400 = MediaController.this.buffersWrited;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MediaController.this.lastPlayPcm = j;
                            if (i2 != -1) {
                                if (MediaController.this.audioTrackPlayer != null) {
                                    MediaController.this.audioTrackPlayer.setNotificationMarkerPosition(1);
                                }
                                if (access$5400 == 1) {
                                    MediaController.this.cleanupPlayer(true, true, true);
                                }
                            }
                        }
                    });
                }
                if (r0.finished != 1) {
                    MediaController.this.checkPlayerQueue();
                }
                if (r0 == null || !(r0 == null || r0.finished == 1)) {
                    MediaController.this.checkDecoderQueue();
                }
                if (r0 != null) {
                    synchronized (MediaController.this.playerSync) {
                        MediaController.this.freePlayerBuffers.add(r0);
                    }
                }
            }
        });
    }

    protected boolean isRecordingAudio() {
        if (this.recordStartRunnable == null) {
            if (this.recordingAudio == null) {
                return false;
            }
        }
        return true;
    }

    private boolean isNearToSensor(float f) {
        return f < 5.0f && f != this.proximitySensor.getMaximumRange();
    }

    public boolean isRecordingOrListeningByProximity() {
        return this.proximityTouched && (isRecordingAudio() || (this.playingMessageObject != null && (this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo())));
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (this.sensorsStarted) {
            if (VoIPService.getSharedInstance() == null) {
                if (sensorEvent.sensor == this.proximitySensor) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("proximity changed to ");
                        stringBuilder.append(sensorEvent.values[0]);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    if (this.lastProximityValue == -100.0f) {
                        this.lastProximityValue = sensorEvent.values[0];
                    } else if (this.lastProximityValue != sensorEvent.values[0]) {
                        this.proximityHasDifferentValues = true;
                    }
                    if (this.proximityHasDifferentValues) {
                        this.proximityTouched = isNearToSensor(sensorEvent.values[0]);
                    }
                } else if (sensorEvent.sensor == this.accelerometerSensor) {
                    double d = this.lastTimestamp == 0 ? 0.9800000190734863d : 1.0d / ((((double) (sensorEvent.timestamp - this.lastTimestamp)) / 1.0E9d) + 1.0d);
                    this.lastTimestamp = sensorEvent.timestamp;
                    double d2 = 1.0d - d;
                    this.gravity[0] = (float) ((((double) this.gravity[0]) * d) + (((double) sensorEvent.values[0]) * d2));
                    this.gravity[1] = (float) ((((double) this.gravity[1]) * d) + (((double) sensorEvent.values[1]) * d2));
                    this.gravity[2] = (float) ((d * ((double) this.gravity[2])) + (d2 * ((double) sensorEvent.values[2])));
                    this.gravityFast[0] = (this.gravity[0] * 0.8f) + (sensorEvent.values[0] * 0.19999999f);
                    this.gravityFast[1] = (this.gravity[1] * 0.8f) + (sensorEvent.values[1] * 0.19999999f);
                    this.gravityFast[2] = (0.8f * this.gravity[2]) + (0.19999999f * sensorEvent.values[2]);
                    this.linearAcceleration[0] = sensorEvent.values[0] - this.gravity[0];
                    this.linearAcceleration[1] = sensorEvent.values[1] - this.gravity[1];
                    this.linearAcceleration[2] = sensorEvent.values[2] - this.gravity[2];
                } else if (sensorEvent.sensor == this.linearSensor) {
                    this.linearAcceleration[0] = sensorEvent.values[0];
                    this.linearAcceleration[1] = sensorEvent.values[1];
                    this.linearAcceleration[2] = sensorEvent.values[2];
                } else if (sensorEvent.sensor == this.gravitySensor) {
                    float[] fArr = this.gravityFast;
                    float[] fArr2 = this.gravity;
                    float f = sensorEvent.values[0];
                    fArr2[0] = f;
                    fArr[0] = f;
                    fArr = this.gravityFast;
                    fArr2 = this.gravity;
                    f = sensorEvent.values[1];
                    fArr2[1] = f;
                    fArr[1] = f;
                    fArr = this.gravityFast;
                    fArr2 = this.gravity;
                    f = sensorEvent.values[2];
                    fArr2[2] = f;
                    fArr[2] = f;
                }
                if (sensorEvent.sensor == this.linearSensor || sensorEvent.sensor == this.gravitySensor || sensorEvent.sensor == this.accelerometerSensor) {
                    sensorEvent = ((this.gravity[0] * this.linearAcceleration[0]) + (this.gravity[1] * this.linearAcceleration[1])) + (this.gravity[2] * this.linearAcceleration[2]);
                    if (this.raisedToBack != 6 && ((sensorEvent > null && this.previousAccValue > 0.0f) || (sensorEvent < null && this.previousAccValue < 0.0f))) {
                        boolean z;
                        int i;
                        if (sensorEvent > null) {
                            z = sensorEvent > 15.0f;
                            i = 1;
                        } else {
                            z = sensorEvent < -15.0f;
                            i = 2;
                        }
                        if (this.raisedToTopSign == 0 || this.raisedToTopSign == i) {
                            if (!z || this.raisedToBack != 0 || (this.raisedToTopSign != 0 && this.raisedToTopSign != i)) {
                                if (!z) {
                                    this.countLess++;
                                }
                                if (!(this.raisedToTopSign == i && this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                                    this.raisedToBack = 0;
                                    this.raisedToTop = 0;
                                    this.raisedToTopSign = 0;
                                    this.countLess = 0;
                                }
                            } else if (this.raisedToTop < 6 && !this.proximityTouched) {
                                this.raisedToTopSign = i;
                                this.raisedToTop++;
                                if (this.raisedToTop == 6) {
                                    this.countLess = 0;
                                }
                            }
                        } else if (this.raisedToTop != 6 || !z) {
                            if (!z) {
                                this.countLess++;
                            }
                            if (!(this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                                this.raisedToTop = 0;
                                this.raisedToTopSign = 0;
                                this.raisedToBack = 0;
                                this.countLess = 0;
                            }
                        } else if (this.raisedToBack < 6) {
                            this.raisedToBack++;
                            if (this.raisedToBack == 6) {
                                this.raisedToTop = 0;
                                this.raisedToTopSign = 0;
                                this.countLess = 0;
                                this.timeSinceRaise = System.currentTimeMillis();
                                if (BuildVars.LOGS_ENABLED && BuildVars.DEBUG_PRIVATE_VERSION) {
                                    FileLog.m0d("motion detected");
                                }
                            }
                        }
                    }
                    this.previousAccValue = sensorEvent;
                    sensorEvent = (this.gravityFast[1] <= 2.5f || Math.abs(this.gravityFast[2]) >= 4.0f || Math.abs(this.gravityFast[0]) <= 1.5f) ? null : 1;
                    this.accelerometerVertical = sensorEvent;
                }
                if (this.raisedToBack == 6 && this.accelerometerVertical != null && this.proximityTouched != null && NotificationsController.audioManager.isWiredHeadsetOn() == null) {
                    if (BuildVars.LOGS_ENABLED != null) {
                        FileLog.m0d("sensor values reached");
                    }
                    if (this.playingMessageObject == null && this.recordStartRunnable == null && this.recordingAudio == null && PhotoViewer.getInstance().isVisible() == null && ApplicationLoader.isScreenOn != null && this.inputFieldHasText == null && this.allowStartRecord != null && this.raiseChat != null && this.callInProgress == null) {
                        if (this.raiseToEarRecord == null) {
                            if (BuildVars.LOGS_ENABLED != null) {
                                FileLog.m0d("start record");
                            }
                            this.useFrontSpeaker = true;
                            if (this.raiseChat.playFirstUnreadVoiceMessage() == null) {
                                this.raiseToEarRecord = true;
                                this.useFrontSpeaker = false;
                                startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null);
                            }
                            if (this.useFrontSpeaker != null) {
                                setUseFrontSpeaker(true);
                            }
                            this.ignoreOnPause = true;
                            if (!(this.proximityHasDifferentValues == null || this.proximityWakeLock == null || this.proximityWakeLock.isHeld() != null)) {
                                this.proximityWakeLock.acquire();
                            }
                        }
                    } else if (this.playingMessageObject != null && (!(this.playingMessageObject.isVoice() == null && this.playingMessageObject.isRoundVideo() == null) && this.useFrontSpeaker == null)) {
                        if (BuildVars.LOGS_ENABLED != null) {
                            FileLog.m0d("start listen");
                        }
                        if (!(this.proximityHasDifferentValues == null || this.proximityWakeLock == null || this.proximityWakeLock.isHeld() != null)) {
                            this.proximityWakeLock.acquire();
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        this.ignoreOnPause = true;
                    }
                    this.raisedToBack = 0;
                    this.raisedToTop = 0;
                    this.raisedToTopSign = 0;
                    this.countLess = 0;
                } else if (this.proximityTouched != null) {
                    if (this.playingMessageObject != null && (!(this.playingMessageObject.isVoice() == null && this.playingMessageObject.isRoundVideo() == null) && this.useFrontSpeaker == null)) {
                        if (BuildVars.LOGS_ENABLED != null) {
                            FileLog.m0d("start listen by proximity only");
                        }
                        if (!(this.proximityHasDifferentValues == null || this.proximityWakeLock == null || this.proximityWakeLock.isHeld() != null)) {
                            this.proximityWakeLock.acquire();
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        this.ignoreOnPause = true;
                    }
                } else if (this.proximityTouched == null) {
                    if (this.raiseToEarRecord != null) {
                        if (BuildVars.LOGS_ENABLED != null) {
                            FileLog.m0d("stop record");
                        }
                        stopRecording(2);
                        this.raiseToEarRecord = false;
                        this.ignoreOnPause = false;
                        if (!(this.proximityHasDifferentValues == null || this.proximityWakeLock == null || this.proximityWakeLock.isHeld() == null)) {
                            this.proximityWakeLock.release();
                        }
                    } else if (this.useFrontSpeaker != null) {
                        if (BuildVars.LOGS_ENABLED != null) {
                            FileLog.m0d("stop listen");
                        }
                        this.useFrontSpeaker = false;
                        startAudioAgain(true);
                        this.ignoreOnPause = false;
                        if (!(this.proximityHasDifferentValues == null || this.proximityWakeLock == null || this.proximityWakeLock.isHeld() == null)) {
                            this.proximityWakeLock.release();
                        }
                    }
                }
                if (this.timeSinceRaise != 0 && this.raisedToBack == 6 && Math.abs(System.currentTimeMillis() - this.timeSinceRaise) > 1000) {
                    this.raisedToBack = 0;
                    this.raisedToTop = 0;
                    this.raisedToTopSign = 0;
                    this.countLess = 0;
                    this.timeSinceRaise = 0;
                }
            }
        }
    }

    private void setUseFrontSpeaker(boolean z) {
        this.useFrontSpeaker = z;
        z = NotificationsController.audioManager;
        if (this.useFrontSpeaker) {
            z.setBluetoothScoOn(false);
            z.setSpeakerphoneOn(false);
            return;
        }
        z.setSpeakerphoneOn(true);
    }

    public void startRecordingIfFromSpeaker() {
        if (this.useFrontSpeaker && this.raiseChat != null) {
            if (this.allowStartRecord) {
                this.raiseToEarRecord = true;
                startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null);
                this.ignoreOnPause = true;
            }
        }
    }

    private void startAudioAgain(boolean z) {
        if (this.playingMessageObject != null) {
            NotificationCenter instance = NotificationCenter.getInstance(this.playingMessageObject.currentAccount);
            int i = NotificationCenter.audioRouteChanged;
            Object[] objArr = new Object[1];
            int i2 = 0;
            objArr[0] = Boolean.valueOf(this.useFrontSpeaker);
            instance.postNotificationName(i, objArr);
            if (this.videoPlayer != null) {
                VideoPlayer videoPlayer = this.videoPlayer;
                if (!this.useFrontSpeaker) {
                    i2 = 3;
                }
                videoPlayer.setStreamType(i2);
                if (z) {
                    this.videoPlayer.pause();
                } else {
                    this.videoPlayer.play();
                }
            } else {
                boolean z2 = this.audioPlayer != null;
                final MessageObject messageObject = this.playingMessageObject;
                float f = this.playingMessageObject.audioProgress;
                cleanupPlayer(false, true);
                messageObject.audioProgress = f;
                playMessage(messageObject);
                if (z) {
                    if (z2) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MediaController.this.pauseMessage(messageObject);
                            }
                        }, 100);
                    } else {
                        pauseMessage(messageObject);
                    }
                }
            }
        }
    }

    public void setInputFieldHasText(boolean z) {
        this.inputFieldHasText = z;
    }

    public void setAllowStartRecord(boolean z) {
        this.allowStartRecord = z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startRaiseToEarSensors(ChatActivity chatActivity) {
        if (!(chatActivity == null || (this.accelerometerSensor == null && (this.gravitySensor == null || this.linearAcceleration == null)))) {
            if (this.proximitySensor != null) {
                this.raiseChat = chatActivity;
                if (!(SharedConfig.raiseToSpeak == null && (this.playingMessageObject == null || (this.playingMessageObject.isVoice() == null && this.playingMessageObject.isRoundVideo() == null))) && this.sensorsStarted == null) {
                    chatActivity = this.gravity;
                    float[] fArr = this.gravity;
                    this.gravity[2] = 0.0f;
                    fArr[1] = 0.0f;
                    chatActivity[0] = null;
                    chatActivity = this.linearAcceleration;
                    float[] fArr2 = this.linearAcceleration;
                    this.linearAcceleration[2] = 0.0f;
                    fArr2[1] = 0.0f;
                    chatActivity[0] = null;
                    chatActivity = this.gravityFast;
                    fArr2 = this.gravityFast;
                    this.gravityFast[2] = 0.0f;
                    fArr2[1] = 0.0f;
                    chatActivity[0] = null;
                    this.lastTimestamp = 0;
                    this.previousAccValue = 0.0f;
                    this.raisedToTop = 0;
                    this.raisedToTopSign = 0;
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
    }

    public void stopRaiseToEarSensors(ChatActivity chatActivity) {
        if (this.ignoreOnPause) {
            this.ignoreOnPause = false;
            return;
        }
        stopRecording(0);
        if (!(!this.sensorsStarted || this.ignoreOnPause || ((this.accelerometerSensor == null && (this.gravitySensor == null || this.linearAcceleration == null)) || this.proximitySensor == null))) {
            if (this.raiseChat == chatActivity) {
                this.raiseChat = null;
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
                if (!(this.proximityHasDifferentValues == null || this.proximityWakeLock == null || this.proximityWakeLock.isHeld() == null)) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    public void cleanupPlayer(boolean z, boolean z2) {
        cleanupPlayer(z, z2, false);
    }

    public void cleanupPlayer(boolean z, boolean z2, boolean z3) {
        if (this.audioPlayer != null) {
            try {
                this.audioPlayer.releasePlayer();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            this.audioPlayer = null;
        } else if (this.audioTrackPlayer != null) {
            synchronized (this.playerObjectSync) {
                try {
                    this.audioTrackPlayer.pause();
                    this.audioTrackPlayer.flush();
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
                try {
                    this.audioTrackPlayer.release();
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
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
            } catch (Throwable e3) {
                FileLog.m3e(e3);
            }
        }
        stopProgressTimer();
        this.lastProgress = 0;
        this.buffersWrited = 0;
        this.isPaused = false;
        if (!(this.useFrontSpeaker || SharedConfig.raiseToSpeak)) {
            ChatActivity chatActivity = this.raiseChat;
            stopRaiseToEarSensors(this.raiseChat);
            this.raiseChat = chatActivity;
        }
        if (this.playingMessageObject != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance(this.playingMessageObject.currentAccount).cancelLoadFile(this.playingMessageObject.getDocument());
            }
            MessageObject messageObject = this.playingMessageObject;
            if (z) {
                this.playingMessageObject.resetPlayingProgress();
                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
            }
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (z) {
                NotificationsController.audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                if (this.voiceMessagesPlaylist) {
                    if (z3 && this.voiceMessagesPlaylist.get(0) == messageObject) {
                        this.voiceMessagesPlaylist.remove(0);
                        this.voiceMessagesPlaylistMap.remove(messageObject.getId());
                        if (this.voiceMessagesPlaylist.isEmpty()) {
                            this.voiceMessagesPlaylist = null;
                            this.voiceMessagesPlaylistMap = null;
                        }
                    } else {
                        this.voiceMessagesPlaylist = null;
                        this.voiceMessagesPlaylistMap = null;
                    }
                }
                if (this.voiceMessagesPlaylist) {
                    MessageObject messageObject2 = (MessageObject) this.voiceMessagesPlaylist.get(0);
                    playMessage(messageObject2);
                    if (!messageObject2.isRoundVideo() && this.pipRoundVideoView) {
                        this.pipRoundVideoView.close(true);
                        this.pipRoundVideoView = null;
                    }
                } else {
                    if ((messageObject.isVoice() || messageObject.isRoundVideo()) && messageObject.getId()) {
                        startRecordingIfFromSpeaker();
                    }
                    NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(messageObject.getId()), Boolean.valueOf(z2));
                    this.pipSwitchingState = 0;
                    if (this.pipRoundVideoView) {
                        this.pipRoundVideoView.close(true);
                        this.pipRoundVideoView = null;
                    }
                }
            }
            if (z2) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                return;
            }
            return;
        }
        return;
    }

    private void seekOpusPlayer(final float f) {
        if (f != VOLUME_NORMAL) {
            if (!this.isPaused) {
                this.audioTrackPlayer.pause();
            }
            this.audioTrackPlayer.flush();
            this.fileDecodingQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MediaController$14$1 */
                class C02521 implements Runnable {
                    C02521() {
                    }

                    public void run() {
                        if (!MediaController.this.isPaused) {
                            MediaController.this.ignoreFirstProgress = 3;
                            MediaController.this.lastPlayPcm = (long) (((float) MediaController.this.currentTotalPcmDuration) * f);
                            if (MediaController.this.audioTrackPlayer != null) {
                                MediaController.this.audioTrackPlayer.play();
                            }
                            MediaController.this.lastProgress = (long) ((int) ((((float) MediaController.this.currentTotalPcmDuration) / 48.0f) * f));
                            MediaController.this.checkPlayerQueue();
                        }
                    }
                }

                public void run() {
                    MediaController.this.seekOpusFile(f);
                    synchronized (MediaController.this.playerSync) {
                        MediaController.this.freePlayerBuffers.addAll(MediaController.this.usedPlayerBuffers);
                        MediaController.this.usedPlayerBuffers.clear();
                    }
                    AndroidUtilities.runOnUIThread(new C02521());
                }
            });
        }
    }

    private boolean isSamePlayingMessage(MessageObject messageObject) {
        if (this.playingMessageObject == null || this.playingMessageObject.getDialogId() != messageObject.getDialogId() || this.playingMessageObject.getId() != messageObject.getId()) {
            return false;
        }
        return ((this.playingMessageObject.eventId > 0 ? 1 : (this.playingMessageObject.eventId == 0 ? 0 : -1)) == 0) == ((messageObject.eventId > 0 ? 1 : (messageObject.eventId == 0 ? 0 : -1)) == null ? 1 : null);
    }

    public boolean seekToProgress(MessageObject messageObject, float f) {
        if (!((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null)) {
            if (isSamePlayingMessage(messageObject) != null) {
                try {
                    if (this.audioPlayer != null) {
                        long duration = this.audioPlayer.getDuration();
                        if (duration == C0542C.TIME_UNSET) {
                            this.seekToProgressPending = f;
                        } else {
                            duration = (long) ((int) (((float) duration) * f));
                            this.audioPlayer.seekTo(duration);
                            this.lastProgress = duration;
                        }
                    } else if (this.audioTrackPlayer != null) {
                        seekOpusPlayer(f);
                    } else if (this.videoPlayer != null) {
                        this.videoPlayer.seekTo((long) (((float) this.videoPlayer.getDuration()) * f));
                    }
                    return true;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return false;
                }
            }
        }
        return false;
    }

    public MessageObject getPlayingMessageObject() {
        return this.playingMessageObject;
    }

    public int getPlayingMessageObjectNum() {
        return this.currentPlaylistNum;
    }

    private void buildShuffledPlayList() {
        if (!this.playlist.isEmpty()) {
            ArrayList arrayList = new ArrayList(this.playlist);
            this.shuffledPlaylist.clear();
            MessageObject messageObject = (MessageObject) this.playlist.get(this.currentPlaylistNum);
            arrayList.remove(this.currentPlaylistNum);
            this.shuffledPlaylist.add(messageObject);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                int nextInt = Utilities.random.nextInt(arrayList.size());
                this.shuffledPlaylist.add(arrayList.get(nextInt));
                arrayList.remove(nextInt);
            }
        }
    }

    public boolean setPlaylist(ArrayList<MessageObject> arrayList, MessageObject messageObject) {
        return setPlaylist(arrayList, messageObject, true);
    }

    public boolean setPlaylist(ArrayList<MessageObject> arrayList, MessageObject messageObject, boolean z) {
        if (this.playingMessageObject == messageObject) {
            return playMessage(messageObject);
        }
        this.forceLoopCurrentPlaylist = z ^ 1;
        this.playMusicAgain = this.playlist.isEmpty() ^ 1;
        this.playlist.clear();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            MessageObject messageObject2 = (MessageObject) arrayList.get(size);
            if (messageObject2.isMusic()) {
                this.playlist.add(messageObject2);
            }
        }
        this.currentPlaylistNum = this.playlist.indexOf(messageObject);
        if (this.currentPlaylistNum == -1) {
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.currentPlaylistNum = this.playlist.size();
            this.playlist.add(messageObject);
        }
        if (messageObject.isMusic() != null) {
            if (SharedConfig.shuffleMusic != null) {
                buildShuffledPlayList();
                this.currentPlaylistNum = 0;
            }
            if (z) {
                DataQuery.getInstance(messageObject.currentAccount).loadMusic(messageObject.getDialogId(), ((MessageObject) this.playlist.get(0)).getIdWithChannel());
            }
        }
        return playMessage(messageObject);
    }

    public void playNextMessage() {
        playNextMessageWithoutOrder(false);
    }

    public boolean findMessageInPlaylistAndPlay(MessageObject messageObject) {
        int indexOf = this.playlist.indexOf(messageObject);
        if (indexOf == -1) {
            return playMessage(messageObject);
        }
        playMessageAtIndex(indexOf);
        return true;
    }

    public void playMessageAtIndex(int i) {
        if (this.currentPlaylistNum >= 0) {
            if (this.currentPlaylistNum < this.playlist.size()) {
                this.currentPlaylistNum = i;
                this.playMusicAgain = true;
                if (this.playingMessageObject != 0) {
                    this.playingMessageObject.resetPlayingProgress();
                }
                playMessage((MessageObject) this.playlist.get(this.currentPlaylistNum));
            }
        }
    }

    private void playNextMessageWithoutOrder(boolean z) {
        ArrayList arrayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (z && SharedConfig.repeatMode == 2 && !this.forceLoopCurrentPlaylist) {
            cleanupPlayer(false, false);
            playMessage((MessageObject) arrayList.get(this.currentPlaylistNum));
            return;
        }
        boolean z2;
        if (SharedConfig.playOrderReversed) {
            this.currentPlaylistNum++;
            if (this.currentPlaylistNum >= arrayList.size()) {
                this.currentPlaylistNum = 0;
            }
            z2 = false;
            if (z2 || !z || SharedConfig.repeatMode || this.forceLoopCurrentPlaylist) {
                if (this.currentPlaylistNum < false) {
                    if (this.currentPlaylistNum >= arrayList.size()) {
                        if (this.playingMessageObject) {
                            this.playingMessageObject.resetPlayingProgress();
                        }
                        this.playMusicAgain = true;
                        playMessage((MessageObject) arrayList.get(this.currentPlaylistNum));
                        return;
                    }
                }
                return;
            }
            if (this.audioPlayer || this.audioTrackPlayer || this.videoPlayer) {
                if (this.audioPlayer) {
                    try {
                        this.audioPlayer.releasePlayer();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    this.audioPlayer = null;
                } else if (this.audioTrackPlayer) {
                    synchronized (this.playerObjectSync) {
                        try {
                            this.audioTrackPlayer.pause();
                            this.audioTrackPlayer.flush();
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                        try {
                            this.audioTrackPlayer.release();
                        } catch (Throwable e22) {
                            FileLog.m3e(e22);
                        }
                        this.audioTrackPlayer = null;
                    }
                } else if (this.videoPlayer) {
                    this.currentAspectRatioFrameLayout = null;
                    this.currentTextureViewContainer = null;
                    this.currentAspectRatioFrameLayoutReady = false;
                    this.currentTextureView = null;
                    this.videoPlayer.releasePlayer();
                    this.videoPlayer = null;
                    try {
                        this.baseActivity.getWindow().clearFlags(128);
                    } catch (Throwable e3) {
                        FileLog.m3e(e3);
                    }
                }
                stopProgressTimer();
                this.lastProgress = 0;
                this.buffersWrited = 0;
                this.isPaused = true;
                this.playingMessageObject.audioProgress = 0.0f;
                this.playingMessageObject.audioProgressSec = 0;
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            }
            return;
        }
        this.currentPlaylistNum--;
        if (this.currentPlaylistNum < 0) {
            this.currentPlaylistNum = arrayList.size() - 1;
        }
        z2 = false;
        if (z2) {
        }
        if (this.currentPlaylistNum < false) {
            if (this.currentPlaylistNum >= arrayList.size()) {
                if (this.playingMessageObject) {
                    this.playingMessageObject.resetPlayingProgress();
                }
                this.playMusicAgain = true;
                playMessage((MessageObject) arrayList.get(this.currentPlaylistNum));
                return;
            }
        }
        return;
        z2 = true;
        if (z2) {
        }
        if (this.currentPlaylistNum < false) {
            if (this.currentPlaylistNum >= arrayList.size()) {
                if (this.playingMessageObject) {
                    this.playingMessageObject.resetPlayingProgress();
                }
                this.playMusicAgain = true;
                playMessage((MessageObject) arrayList.get(this.currentPlaylistNum));
                return;
            }
        }
        return;
    }

    public void playPreviousMessage() {
        ArrayList arrayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!arrayList.isEmpty() && this.currentPlaylistNum >= 0) {
            if (this.currentPlaylistNum < arrayList.size()) {
                MessageObject messageObject = (MessageObject) arrayList.get(this.currentPlaylistNum);
                if (messageObject.audioProgressSec > 10) {
                    seekToProgress(messageObject, 0.0f);
                    return;
                }
                if (SharedConfig.playOrderReversed) {
                    this.currentPlaylistNum--;
                    if (this.currentPlaylistNum < 0) {
                        this.currentPlaylistNum = arrayList.size() - 1;
                    }
                } else {
                    this.currentPlaylistNum++;
                    if (this.currentPlaylistNum >= arrayList.size()) {
                        this.currentPlaylistNum = 0;
                    }
                }
                if (this.currentPlaylistNum >= 0) {
                    if (this.currentPlaylistNum < arrayList.size()) {
                        this.playMusicAgain = true;
                        playMessage((MessageObject) arrayList.get(this.currentPlaylistNum));
                    }
                }
            }
        }
    }

    protected void checkIsNextMediaFileDownloaded() {
        if (this.playingMessageObject != null) {
            if (this.playingMessageObject.isMusic()) {
                checkIsNextMusicFileDownloaded(this.playingMessageObject.currentAccount);
            }
        }
    }

    private void checkIsNextVoiceFileDownloaded(int i) {
        if (this.voiceMessagesPlaylist != null) {
            if (this.voiceMessagesPlaylist.size() >= 2) {
                File file;
                MessageObject messageObject = (MessageObject) this.voiceMessagesPlaylist.get(1);
                File file2 = null;
                if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
                    file = new File(messageObject.messageOwner.attachPath);
                    if (file.exists()) {
                        file2 = file;
                    }
                }
                if (file2 != null) {
                    file = file2;
                } else {
                    file = FileLoader.getPathToMessage(messageObject.messageOwner);
                }
                if (file != null) {
                    boolean exists = file.exists();
                }
                if (!(file == null || file == file2 || file.exists())) {
                    FileLoader.getInstance(i).loadFile(messageObject.getDocument(), false, 0);
                }
            }
        }
    }

    private void checkIsNextMusicFileDownloaded(int i) {
        if ((DownloadController.getInstance(i).getCurrentDownloadMask() & 16) != 0) {
            ArrayList arrayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
            if (arrayList != null) {
                if (arrayList.size() >= 2) {
                    int i2;
                    if (SharedConfig.playOrderReversed) {
                        i2 = this.currentPlaylistNum + 1;
                        if (i2 >= arrayList.size()) {
                            i2 = 0;
                        }
                    } else {
                        i2 = this.currentPlaylistNum - 1;
                        if (i2 < 0) {
                            i2 = arrayList.size() - 1;
                        }
                    }
                    MessageObject messageObject = (MessageObject) arrayList.get(i2);
                    if (DownloadController.getInstance(i).canDownloadMedia(messageObject)) {
                        File file;
                        File file2 = null;
                        if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                            file = new File(messageObject.messageOwner.attachPath);
                            if (file.exists()) {
                                file2 = file;
                            }
                        }
                        if (file2 != null) {
                            file = file2;
                        } else {
                            file = FileLoader.getPathToMessage(messageObject.messageOwner);
                        }
                        if (file != null) {
                            boolean exists = file.exists();
                        }
                        if (!(file == null || file == file2 || file.exists() || !messageObject.isMusic())) {
                            FileLoader.getInstance(i).loadFile(messageObject.getDocument(), false, 0);
                        }
                    }
                }
            }
        }
    }

    public void setVoiceMessagesPlaylist(ArrayList<MessageObject> arrayList, boolean z) {
        this.voiceMessagesPlaylist = arrayList;
        if (this.voiceMessagesPlaylist != null) {
            this.voiceMessagesPlaylistUnread = z;
            this.voiceMessagesPlaylistMap = new SparseArray();
            for (arrayList = null; arrayList < this.voiceMessagesPlaylist.size(); arrayList++) {
                MessageObject messageObject = (MessageObject) this.voiceMessagesPlaylist.get(arrayList);
                this.voiceMessagesPlaylistMap.put(messageObject.getId(), messageObject);
            }
        }
    }

    private void checkAudioFocus(MessageObject messageObject) {
        if (!messageObject.isVoice()) {
            if (messageObject.isRoundVideo() == null) {
                messageObject = 1;
                if (this.hasAudioFocus != messageObject) {
                    this.hasAudioFocus = messageObject;
                    if (messageObject != 3) {
                        messageObject = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
                    } else {
                        messageObject = NotificationsController.audioManager.requestAudioFocus(this, 3, messageObject != 2 ? 3 : 1);
                    }
                    if (messageObject == 1) {
                        this.audioFocus = 2;
                    }
                }
            }
        }
        messageObject = this.useFrontSpeaker != null ? 3 : 2;
        if (this.hasAudioFocus != messageObject) {
            this.hasAudioFocus = messageObject;
            if (messageObject != 3) {
                if (messageObject != 2) {
                }
                messageObject = NotificationsController.audioManager.requestAudioFocus(this, 3, messageObject != 2 ? 3 : 1);
            } else {
                messageObject = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
            }
            if (messageObject == 1) {
                this.audioFocus = 2;
            }
        }
    }

    public void setCurrentRoundVisible(boolean r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r3 = this;
        r0 = r3.currentAspectRatioFrameLayout;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = 0;
        r1 = 1;
        if (r4 == 0) goto L_0x0033;
    L_0x0009:
        r4 = r3.pipRoundVideoView;
        if (r4 == 0) goto L_0x0018;
    L_0x000d:
        r4 = 2;
        r3.pipSwitchingState = r4;
        r4 = r3.pipRoundVideoView;
        r4.close(r1);
        r3.pipRoundVideoView = r0;
        goto L_0x006e;
    L_0x0018:
        r4 = r3.currentAspectRatioFrameLayout;
        if (r4 == 0) goto L_0x006e;
    L_0x001c:
        r4 = r3.currentAspectRatioFrameLayout;
        r4 = r4.getParent();
        if (r4 != 0) goto L_0x002b;
    L_0x0024:
        r4 = r3.currentTextureViewContainer;
        r0 = r3.currentAspectRatioFrameLayout;
        r4.addView(r0);
    L_0x002b:
        r4 = r3.videoPlayer;
        r0 = r3.currentTextureView;
        r4.setTextureView(r0);
        goto L_0x006e;
    L_0x0033:
        r4 = r3.currentAspectRatioFrameLayout;
        r4 = r4.getParent();
        if (r4 == 0) goto L_0x0045;
    L_0x003b:
        r3.pipSwitchingState = r1;
        r4 = r3.currentTextureViewContainer;
        r0 = r3.currentAspectRatioFrameLayout;
        r4.removeView(r0);
        goto L_0x006e;
    L_0x0045:
        r4 = r3.pipRoundVideoView;
        if (r4 != 0) goto L_0x005f;
    L_0x0049:
        r4 = new org.telegram.ui.Components.PipRoundVideoView;	 Catch:{ Exception -> 0x005d }
        r4.<init>();	 Catch:{ Exception -> 0x005d }
        r3.pipRoundVideoView = r4;	 Catch:{ Exception -> 0x005d }
        r4 = r3.pipRoundVideoView;	 Catch:{ Exception -> 0x005d }
        r1 = r3.baseActivity;	 Catch:{ Exception -> 0x005d }
        r2 = new org.telegram.messenger.MediaController$15;	 Catch:{ Exception -> 0x005d }
        r2.<init>();	 Catch:{ Exception -> 0x005d }
        r4.show(r1, r2);	 Catch:{ Exception -> 0x005d }
        goto L_0x005f;
    L_0x005d:
        r3.pipRoundVideoView = r0;
    L_0x005f:
        r4 = r3.pipRoundVideoView;
        if (r4 == 0) goto L_0x006e;
    L_0x0063:
        r4 = r3.videoPlayer;
        r0 = r3.pipRoundVideoView;
        r0 = r0.getTextureView();
        r4.setTextureView(r0);
    L_0x006e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.setCurrentRoundVisible(boolean):void");
    }

    public void setTextureView(TextureView textureView, AspectRatioFrameLayout aspectRatioFrameLayout, FrameLayout frameLayout, boolean z) {
        if (textureView != null) {
            boolean z2 = true;
            if (z || this.currentTextureView != textureView) {
                if (this.videoPlayer) {
                    if (textureView != this.currentTextureView) {
                        if (aspectRatioFrameLayout == null || !aspectRatioFrameLayout.isDrawingReady()) {
                            z2 = false;
                        }
                        this.isDrawingWasReady = z2;
                        this.currentTextureView = textureView;
                        if (this.pipRoundVideoView != null) {
                            this.videoPlayer.setTextureView(this.pipRoundVideoView.getTextureView());
                        } else {
                            this.videoPlayer.setTextureView(this.currentTextureView);
                        }
                        this.currentAspectRatioFrameLayout = aspectRatioFrameLayout;
                        this.currentTextureViewContainer = frameLayout;
                        if (!(this.currentAspectRatioFrameLayoutReady == null || this.currentAspectRatioFrameLayout == null)) {
                            if (this.currentAspectRatioFrameLayout != null) {
                                this.currentAspectRatioFrameLayout.setAspectRatio(this.currentAspectRatioFrameLayoutRatio, this.currentAspectRatioFrameLayoutRotation);
                            }
                            if (this.currentTextureViewContainer.getVisibility() != null) {
                                this.currentTextureViewContainer.setVisibility(0);
                            }
                        }
                        return;
                    }
                }
                return;
            }
            this.pipSwitchingState = 1;
            this.currentTextureView = null;
            this.currentAspectRatioFrameLayout = null;
            this.currentTextureViewContainer = null;
        }
    }

    public void setFlagSecure(org.telegram.ui.ActionBar.BaseFragment r2, boolean r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        r0 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        if (r3 == 0) goto L_0x0012;
    L_0x0004:
        r3 = r2.getParentActivity();	 Catch:{ Exception -> 0x000f }
        r3 = r3.getWindow();	 Catch:{ Exception -> 0x000f }
        r3.setFlags(r0, r0);	 Catch:{ Exception -> 0x000f }
    L_0x000f:
        r1.flagSecureFragment = r2;
        goto L_0x0024;
    L_0x0012:
        r3 = r1.flagSecureFragment;
        if (r3 != r2) goto L_0x0024;
    L_0x0016:
        r2 = r2.getParentActivity();	 Catch:{ Exception -> 0x0021 }
        r2 = r2.getWindow();	 Catch:{ Exception -> 0x0021 }
        r2.clearFlags(r0);	 Catch:{ Exception -> 0x0021 }
    L_0x0021:
        r2 = 0;
        r1.flagSecureFragment = r2;
    L_0x0024:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.setFlagSecure(org.telegram.ui.ActionBar.BaseFragment, boolean):void");
    }

    public void setBaseActivity(Activity activity, boolean z) {
        if (z) {
            this.baseActivity = activity;
        } else if (this.baseActivity == activity) {
            this.baseActivity = null;
        }
    }

    public void setFeedbackView(View view, boolean z) {
        if (z) {
            this.feedbackView = view;
        } else if (this.feedbackView == view) {
            this.feedbackView = null;
        }
    }

    public boolean playMessage(org.telegram.messenger.MessageObject r23) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r22 = this;
        r1 = r22;
        r2 = r23;
        r3 = 0;
        if (r2 != 0) goto L_0x0008;
    L_0x0007:
        return r3;
    L_0x0008:
        r4 = r1.audioTrackPlayer;
        r5 = 1;
        if (r4 != 0) goto L_0x0015;
    L_0x000d:
        r4 = r1.audioPlayer;
        if (r4 != 0) goto L_0x0015;
    L_0x0011:
        r4 = r1.videoPlayer;
        if (r4 == 0) goto L_0x002c;
    L_0x0015:
        r4 = r22.isSamePlayingMessage(r23);
        if (r4 == 0) goto L_0x002c;
    L_0x001b:
        r3 = r1.isPaused;
        if (r3 == 0) goto L_0x0022;
    L_0x001f:
        r22.resumeAudio(r23);
    L_0x0022:
        r2 = org.telegram.messenger.SharedConfig.raiseToSpeak;
        if (r2 != 0) goto L_0x002b;
    L_0x0026:
        r2 = r1.raiseChat;
        r1.startRaiseToEarSensors(r2);
    L_0x002b:
        return r5;
    L_0x002c:
        r4 = r23.isOut();
        if (r4 != 0) goto L_0x0041;
    L_0x0032:
        r4 = r23.isContentUnread();
        if (r4 == 0) goto L_0x0041;
    L_0x0038:
        r4 = r2.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4.markMessageContentAsRead(r2);
    L_0x0041:
        r4 = r1.playMusicAgain;
        r4 = r4 ^ r5;
        r6 = r1.playingMessageObject;
        if (r6 == 0) goto L_0x0052;
    L_0x0048:
        r4 = r1.playMusicAgain;
        if (r4 != 0) goto L_0x0051;
    L_0x004c:
        r4 = r1.playingMessageObject;
        r4.resetPlayingProgress();
    L_0x0051:
        r4 = r3;
    L_0x0052:
        r1.cleanupPlayer(r4, r3);
        r1.playMusicAgain = r3;
        r4 = 0;
        r1.seekToProgressPending = r4;
        r6 = r2.messageOwner;
        r6 = r6.attachPath;
        r7 = 0;
        if (r6 == 0) goto L_0x007b;
    L_0x0061:
        r6 = r2.messageOwner;
        r6 = r6.attachPath;
        r6 = r6.length();
        if (r6 <= 0) goto L_0x007b;
    L_0x006b:
        r6 = new java.io.File;
        r8 = r2.messageOwner;
        r8 = r8.attachPath;
        r6.<init>(r8);
        r8 = r6.exists();
        if (r8 != 0) goto L_0x007d;
    L_0x007a:
        goto L_0x007c;
    L_0x007b:
        r8 = r3;
    L_0x007c:
        r6 = r7;
    L_0x007d:
        if (r6 == 0) goto L_0x0081;
    L_0x007f:
        r9 = r6;
        goto L_0x0087;
    L_0x0081:
        r9 = r2.messageOwner;
        r9 = org.telegram.messenger.FileLoader.getPathToMessage(r9);
    L_0x0087:
        r10 = org.telegram.messenger.SharedConfig.streamMedia;
        if (r10 == 0) goto L_0x009a;
    L_0x008b:
        r10 = r23.isMusic();
        if (r10 == 0) goto L_0x009a;
    L_0x0091:
        r10 = r23.getDialogId();
        r10 = (int) r10;
        if (r10 == 0) goto L_0x009a;
    L_0x0098:
        r10 = r5;
        goto L_0x009b;
    L_0x009a:
        r10 = r3;
    L_0x009b:
        r11 = 0;
        if (r9 == 0) goto L_0x0109;
    L_0x009f:
        if (r9 == r6) goto L_0x0109;
    L_0x00a1:
        r8 = r9.exists();
        if (r8 != 0) goto L_0x0109;
    L_0x00a7:
        if (r10 != 0) goto L_0x0109;
    L_0x00a9:
        r4 = r2.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r6 = r23.getDocument();
        r4.loadFile(r6, r3, r3);
        r1.downloadingCurrentMessage = r5;
        r1.isPaused = r3;
        r1.lastProgress = r11;
        r1.lastPlayPcm = r11;
        r1.audioInfo = r7;
        r1.playingMessageObject = r2;
        r2 = r1.playingMessageObject;
        r2 = r2.isMusic();
        if (r2 == 0) goto L_0x00df;
    L_0x00ca:
        r2 = new android.content.Intent;
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r6 = org.telegram.messenger.MusicPlayerService.class;
        r2.<init>(r4, r6);
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x00d9 }
        r4.startService(r2);	 Catch:{ Throwable -> 0x00d9 }
        goto L_0x00ed;
    L_0x00d9:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.m3e(r2);
        goto L_0x00ed;
    L_0x00df:
        r2 = new android.content.Intent;
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r6 = org.telegram.messenger.MusicPlayerService.class;
        r2.<init>(r4, r6);
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4.stopService(r2);
    L_0x00ed:
        r2 = r1.playingMessageObject;
        r2 = r2.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r4 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r6 = new java.lang.Object[r5];
        r7 = r1.playingMessageObject;
        r7 = r7.getId();
        r7 = java.lang.Integer.valueOf(r7);
        r6[r3] = r7;
        r2.postNotificationName(r4, r6);
        return r5;
    L_0x0109:
        r1.downloadingCurrentMessage = r3;
        r10 = r23.isMusic();
        if (r10 == 0) goto L_0x0117;
    L_0x0111:
        r10 = r2.currentAccount;
        r1.checkIsNextMusicFileDownloaded(r10);
        goto L_0x011c;
    L_0x0117:
        r10 = r2.currentAccount;
        r1.checkIsNextVoiceFileDownloaded(r10);
    L_0x011c:
        r10 = r1.currentAspectRatioFrameLayout;
        if (r10 == 0) goto L_0x0127;
    L_0x0120:
        r1.isDrawingWasReady = r3;
        r10 = r1.currentAspectRatioFrameLayout;
        r10.setDrawingReady(r3);
    L_0x0127:
        r10 = r23.isRoundVideo();
        r13 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r14 = 3;
        if (r10 == 0) goto L_0x01b3;
    L_0x0130:
        r6 = r1.playlist;
        r6.clear();
        r6 = r1.shuffledPlaylist;
        r6.clear();
        r6 = new org.telegram.ui.Components.VideoPlayer;
        r6.<init>();
        r1.videoPlayer = r6;
        r6 = r1.videoPlayer;
        r8 = new org.telegram.messenger.MediaController$16;
        r8.<init>();
        r6.setDelegate(r8);
        r1.currentAspectRatioFrameLayoutReady = r3;
        r6 = r1.pipRoundVideoView;
        if (r6 != 0) goto L_0x016e;
    L_0x0151:
        r6 = r2.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r11 = r23.getDialogId();
        r6 = r6.isDialogCreated(r11);
        if (r6 != 0) goto L_0x0162;
    L_0x0161:
        goto L_0x016e;
    L_0x0162:
        r6 = r1.currentTextureView;
        if (r6 == 0) goto L_0x0197;
    L_0x0166:
        r6 = r1.videoPlayer;
        r7 = r1.currentTextureView;
        r6.setTextureView(r7);
        goto L_0x0197;
    L_0x016e:
        r6 = r1.pipRoundVideoView;
        if (r6 != 0) goto L_0x0188;
    L_0x0172:
        r6 = new org.telegram.ui.Components.PipRoundVideoView;	 Catch:{ Exception -> 0x0186 }
        r6.<init>();	 Catch:{ Exception -> 0x0186 }
        r1.pipRoundVideoView = r6;	 Catch:{ Exception -> 0x0186 }
        r6 = r1.pipRoundVideoView;	 Catch:{ Exception -> 0x0186 }
        r8 = r1.baseActivity;	 Catch:{ Exception -> 0x0186 }
        r10 = new org.telegram.messenger.MediaController$17;	 Catch:{ Exception -> 0x0186 }
        r10.<init>();	 Catch:{ Exception -> 0x0186 }
        r6.show(r8, r10);	 Catch:{ Exception -> 0x0186 }
        goto L_0x0188;
    L_0x0186:
        r1.pipRoundVideoView = r7;
    L_0x0188:
        r6 = r1.pipRoundVideoView;
        if (r6 == 0) goto L_0x0197;
    L_0x018c:
        r6 = r1.videoPlayer;
        r7 = r1.pipRoundVideoView;
        r7 = r7.getTextureView();
        r6.setTextureView(r7);
    L_0x0197:
        r6 = r1.videoPlayer;
        r7 = android.net.Uri.fromFile(r9);
        r8 = "other";
        r6.preparePlayer(r7, r8);
        r6 = r1.videoPlayer;
        r7 = r1.useFrontSpeaker;
        if (r7 == 0) goto L_0x01a9;
    L_0x01a8:
        r14 = r3;
    L_0x01a9:
        r6.setStreamType(r14);
        r6 = r1.videoPlayer;
        r6.play();
        goto L_0x033e;
    L_0x01b3:
        r10 = r23.isMusic();
        if (r10 != 0) goto L_0x0253;
    L_0x01b9:
        r10 = r9.getAbsolutePath();
        r10 = isOpusFile(r10);
        if (r10 != r5) goto L_0x0253;
    L_0x01c3:
        r6 = r1.pipRoundVideoView;
        if (r6 == 0) goto L_0x01ce;
    L_0x01c7:
        r6 = r1.pipRoundVideoView;
        r6.close(r5);
        r1.pipRoundVideoView = r7;
    L_0x01ce:
        r6 = r1.playlist;
        r6.clear();
        r6 = r1.shuffledPlaylist;
        r6.clear();
        r10 = r1.playerObjectSync;
        monitor-enter(r10);
        r1.ignoreFirstProgress = r14;	 Catch:{ Exception -> 0x0239 }
        r6 = new java.util.concurrent.CountDownLatch;	 Catch:{ Exception -> 0x0239 }
        r6.<init>(r5);	 Catch:{ Exception -> 0x0239 }
        r8 = new java.lang.Boolean[r5];	 Catch:{ Exception -> 0x0239 }
        r11 = r1.fileDecodingQueue;	 Catch:{ Exception -> 0x0239 }
        r12 = new org.telegram.messenger.MediaController$18;	 Catch:{ Exception -> 0x0239 }
        r12.<init>(r8, r9, r6);	 Catch:{ Exception -> 0x0239 }
        r11.postRunnable(r12);	 Catch:{ Exception -> 0x0239 }
        r6.await();	 Catch:{ Exception -> 0x0239 }
        r6 = r8[r3];	 Catch:{ Exception -> 0x0239 }
        r6 = r6.booleanValue();	 Catch:{ Exception -> 0x0239 }
        if (r6 != 0) goto L_0x01fb;
    L_0x01f9:
        monitor-exit(r10);	 Catch:{ all -> 0x0236 }
        return r3;
    L_0x01fb:
        r8 = r22.getTotalPcmDuration();	 Catch:{ Exception -> 0x0239 }
        r1.currentTotalPcmDuration = r8;	 Catch:{ Exception -> 0x0239 }
        r6 = new android.media.AudioTrack;	 Catch:{ Exception -> 0x0239 }
        r8 = r1.useFrontSpeaker;	 Catch:{ Exception -> 0x0239 }
        if (r8 == 0) goto L_0x020a;	 Catch:{ Exception -> 0x0239 }
    L_0x0207:
        r16 = r3;	 Catch:{ Exception -> 0x0239 }
        goto L_0x020c;	 Catch:{ Exception -> 0x0239 }
    L_0x020a:
        r16 = r14;	 Catch:{ Exception -> 0x0239 }
    L_0x020c:
        r17 = 48000; // 0xbb80 float:6.7262E-41 double:2.3715E-319;	 Catch:{ Exception -> 0x0239 }
        r18 = 4;	 Catch:{ Exception -> 0x0239 }
        r19 = 2;	 Catch:{ Exception -> 0x0239 }
        r8 = r1.playerBufferSize;	 Catch:{ Exception -> 0x0239 }
        r21 = 1;	 Catch:{ Exception -> 0x0239 }
        r15 = r6;	 Catch:{ Exception -> 0x0239 }
        r20 = r8;	 Catch:{ Exception -> 0x0239 }
        r15.<init>(r16, r17, r18, r19, r20, r21);	 Catch:{ Exception -> 0x0239 }
        r1.audioTrackPlayer = r6;	 Catch:{ Exception -> 0x0239 }
        r6 = r1.audioTrackPlayer;	 Catch:{ Exception -> 0x0239 }
        r6.setStereoVolume(r13, r13);	 Catch:{ Exception -> 0x0239 }
        r6 = r1.audioTrackPlayer;	 Catch:{ Exception -> 0x0239 }
        r8 = new org.telegram.messenger.MediaController$19;	 Catch:{ Exception -> 0x0239 }
        r8.<init>();	 Catch:{ Exception -> 0x0239 }
        r6.setPlaybackPositionUpdateListener(r8);	 Catch:{ Exception -> 0x0239 }
        r6 = r1.audioTrackPlayer;	 Catch:{ Exception -> 0x0239 }
        r6.play();	 Catch:{ Exception -> 0x0239 }
        monitor-exit(r10);	 Catch:{ all -> 0x0236 }
        goto L_0x033e;	 Catch:{ all -> 0x0236 }
    L_0x0236:
        r0 = move-exception;	 Catch:{ all -> 0x0236 }
        r2 = r0;	 Catch:{ all -> 0x0236 }
        goto L_0x0251;	 Catch:{ all -> 0x0236 }
    L_0x0239:
        r0 = move-exception;	 Catch:{ all -> 0x0236 }
        r2 = r0;	 Catch:{ all -> 0x0236 }
        org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ all -> 0x0236 }
        r2 = r1.audioTrackPlayer;	 Catch:{ all -> 0x0236 }
        if (r2 == 0) goto L_0x024f;	 Catch:{ all -> 0x0236 }
    L_0x0242:
        r2 = r1.audioTrackPlayer;	 Catch:{ all -> 0x0236 }
        r2.release();	 Catch:{ all -> 0x0236 }
        r1.audioTrackPlayer = r7;	 Catch:{ all -> 0x0236 }
        r1.isPaused = r3;	 Catch:{ all -> 0x0236 }
        r1.playingMessageObject = r7;	 Catch:{ all -> 0x0236 }
        r1.downloadingCurrentMessage = r3;	 Catch:{ all -> 0x0236 }
    L_0x024f:
        monitor-exit(r10);	 Catch:{ all -> 0x0236 }
        return r3;	 Catch:{ all -> 0x0236 }
    L_0x0251:
        monitor-exit(r10);	 Catch:{ all -> 0x0236 }
        throw r2;
    L_0x0253:
        r10 = r1.pipRoundVideoView;
        if (r10 == 0) goto L_0x025e;
    L_0x0257:
        r10 = r1.pipRoundVideoView;
        r10.close(r5);
        r1.pipRoundVideoView = r7;
    L_0x025e:
        r10 = new org.telegram.ui.Components.VideoPlayer;	 Catch:{ Exception -> 0x0465 }
        r10.<init>();	 Catch:{ Exception -> 0x0465 }
        r1.audioPlayer = r10;	 Catch:{ Exception -> 0x0465 }
        r10 = r1.audioPlayer;	 Catch:{ Exception -> 0x0465 }
        r11 = r1.useFrontSpeaker;	 Catch:{ Exception -> 0x0465 }
        if (r11 == 0) goto L_0x026c;	 Catch:{ Exception -> 0x0465 }
    L_0x026b:
        r14 = r3;	 Catch:{ Exception -> 0x0465 }
    L_0x026c:
        r10.setStreamType(r14);	 Catch:{ Exception -> 0x0465 }
        r10 = r1.audioPlayer;	 Catch:{ Exception -> 0x0465 }
        r11 = new org.telegram.messenger.MediaController$20;	 Catch:{ Exception -> 0x0465 }
        r11.<init>(r2);	 Catch:{ Exception -> 0x0465 }
        r10.setDelegate(r11);	 Catch:{ Exception -> 0x0465 }
        if (r8 == 0) goto L_0x0296;	 Catch:{ Exception -> 0x0465 }
    L_0x027b:
        r8 = r2.mediaExists;	 Catch:{ Exception -> 0x0465 }
        if (r8 != 0) goto L_0x0289;	 Catch:{ Exception -> 0x0465 }
    L_0x027f:
        if (r9 == r6) goto L_0x0289;	 Catch:{ Exception -> 0x0465 }
    L_0x0281:
        r6 = new org.telegram.messenger.MediaController$21;	 Catch:{ Exception -> 0x0465 }
        r6.<init>(r2);	 Catch:{ Exception -> 0x0465 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r6);	 Catch:{ Exception -> 0x0465 }
    L_0x0289:
        r6 = r1.audioPlayer;	 Catch:{ Exception -> 0x0465 }
        r8 = android.net.Uri.fromFile(r9);	 Catch:{ Exception -> 0x0465 }
        r10 = "other";	 Catch:{ Exception -> 0x0465 }
        r6.preparePlayer(r8, r10);	 Catch:{ Exception -> 0x0465 }
        goto L_0x031a;	 Catch:{ Exception -> 0x0465 }
    L_0x0296:
        r6 = r23.getDocument();	 Catch:{ Exception -> 0x0465 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0465 }
        r8.<init>();	 Catch:{ Exception -> 0x0465 }
        r10 = "?account=";	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = r2.currentAccount;	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = "&id=";	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = r6.id;	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = "&hash=";	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = r6.access_hash;	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = "&dc=";	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = r6.dc_id;	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = "&size=";	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = r6.size;	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = "&mime=";	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = r6.mime_type;	 Catch:{ Exception -> 0x0465 }
        r11 = "UTF-8";	 Catch:{ Exception -> 0x0465 }
        r10 = java.net.URLEncoder.encode(r10, r11);	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = "&name=";	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r6);	 Catch:{ Exception -> 0x0465 }
        r10 = "UTF-8";	 Catch:{ Exception -> 0x0465 }
        r6 = java.net.URLEncoder.encode(r6, r10);	 Catch:{ Exception -> 0x0465 }
        r8.append(r6);	 Catch:{ Exception -> 0x0465 }
        r6 = r8.toString();	 Catch:{ Exception -> 0x0465 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0465 }
        r8.<init>();	 Catch:{ Exception -> 0x0465 }
        r10 = "tg://";	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r10 = r23.getFileName();	 Catch:{ Exception -> 0x0465 }
        r8.append(r10);	 Catch:{ Exception -> 0x0465 }
        r8.append(r6);	 Catch:{ Exception -> 0x0465 }
        r6 = r8.toString();	 Catch:{ Exception -> 0x0465 }
        r6 = android.net.Uri.parse(r6);	 Catch:{ Exception -> 0x0465 }
        r8 = r1.audioPlayer;	 Catch:{ Exception -> 0x0465 }
        r10 = "other";	 Catch:{ Exception -> 0x0465 }
        r8.preparePlayer(r6, r10);	 Catch:{ Exception -> 0x0465 }
    L_0x031a:
        r6 = r1.audioPlayer;	 Catch:{ Exception -> 0x0465 }
        r6.play();	 Catch:{ Exception -> 0x0465 }
        r6 = r23.isVoice();	 Catch:{ Exception -> 0x0465 }
        if (r6 == 0) goto L_0x0332;	 Catch:{ Exception -> 0x0465 }
    L_0x0325:
        r1.audioInfo = r7;	 Catch:{ Exception -> 0x0465 }
        r6 = r1.playlist;	 Catch:{ Exception -> 0x0465 }
        r6.clear();	 Catch:{ Exception -> 0x0465 }
        r6 = r1.shuffledPlaylist;	 Catch:{ Exception -> 0x0465 }
        r6.clear();	 Catch:{ Exception -> 0x0465 }
        goto L_0x033e;
    L_0x0332:
        r6 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r9);	 Catch:{ Exception -> 0x0339 }
        r1.audioInfo = r6;	 Catch:{ Exception -> 0x0339 }
        goto L_0x033e;
    L_0x0339:
        r0 = move-exception;
        r6 = r0;
        org.telegram.messenger.FileLog.m3e(r6);	 Catch:{ Exception -> 0x0465 }
    L_0x033e:
        r22.checkAudioFocus(r23);
        r22.setPlayerVolume();
        r1.isPaused = r3;
        r6 = 0;
        r1.lastProgress = r6;
        r1.lastPlayPcm = r6;
        r1.playingMessageObject = r2;
        r6 = org.telegram.messenger.SharedConfig.raiseToSpeak;
        if (r6 != 0) goto L_0x0357;
    L_0x0352:
        r6 = r1.raiseChat;
        r1.startRaiseToEarSensors(r6);
    L_0x0357:
        r6 = r1.playingMessageObject;
        r1.startProgressTimer(r6);
        r6 = r2.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.messagePlayingDidStarted;
        r8 = new java.lang.Object[r5];
        r8[r3] = r2;
        r6.postNotificationName(r7, r8);
        r6 = r1.videoPlayer;
        r7 = 2;
        r8 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        if (r6 == 0) goto L_0x03ca;
    L_0x0375:
        r6 = r1.playingMessageObject;	 Catch:{ Exception -> 0x039d }
        r6 = r6.audioProgress;	 Catch:{ Exception -> 0x039d }
        r6 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));	 Catch:{ Exception -> 0x039d }
        if (r6 == 0) goto L_0x0439;	 Catch:{ Exception -> 0x039d }
    L_0x037d:
        r6 = r1.audioPlayer;	 Catch:{ Exception -> 0x039d }
        r10 = r6.getDuration();	 Catch:{ Exception -> 0x039d }
        r6 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x039d }
        if (r6 != 0) goto L_0x038e;	 Catch:{ Exception -> 0x039d }
    L_0x0387:
        r6 = r1.playingMessageObject;	 Catch:{ Exception -> 0x039d }
        r6 = r6.getDuration();	 Catch:{ Exception -> 0x039d }
        r10 = (long) r6;	 Catch:{ Exception -> 0x039d }
    L_0x038e:
        r6 = (float) r10;	 Catch:{ Exception -> 0x039d }
        r8 = r1.playingMessageObject;	 Catch:{ Exception -> 0x039d }
        r8 = r8.audioProgress;	 Catch:{ Exception -> 0x039d }
        r6 = r6 * r8;	 Catch:{ Exception -> 0x039d }
        r6 = (int) r6;	 Catch:{ Exception -> 0x039d }
        r8 = r1.videoPlayer;	 Catch:{ Exception -> 0x039d }
        r9 = (long) r6;	 Catch:{ Exception -> 0x039d }
        r8.seekTo(r9);	 Catch:{ Exception -> 0x039d }
        goto L_0x0439;
    L_0x039d:
        r0 = move-exception;
        r6 = r0;
        r8 = r1.playingMessageObject;
        r8.audioProgress = r4;
        r4 = r1.playingMessageObject;
        r4.audioProgressSec = r3;
        r2 = r2.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r4 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r7 = new java.lang.Object[r7];
        r8 = r1.playingMessageObject;
        r8 = r8.getId();
        r8 = java.lang.Integer.valueOf(r8);
        r7[r3] = r8;
        r3 = java.lang.Integer.valueOf(r3);
        r7[r5] = r3;
        r2.postNotificationName(r4, r7);
        org.telegram.messenger.FileLog.m3e(r6);
        goto L_0x0439;
    L_0x03ca:
        r6 = r1.audioPlayer;
        if (r6 == 0) goto L_0x041f;
    L_0x03ce:
        r6 = r1.playingMessageObject;	 Catch:{ Exception -> 0x03f5 }
        r6 = r6.audioProgress;	 Catch:{ Exception -> 0x03f5 }
        r4 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));	 Catch:{ Exception -> 0x03f5 }
        if (r4 == 0) goto L_0x0439;	 Catch:{ Exception -> 0x03f5 }
    L_0x03d6:
        r4 = r1.audioPlayer;	 Catch:{ Exception -> 0x03f5 }
        r10 = r4.getDuration();	 Catch:{ Exception -> 0x03f5 }
        r4 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x03f5 }
        if (r4 != 0) goto L_0x03e7;	 Catch:{ Exception -> 0x03f5 }
    L_0x03e0:
        r4 = r1.playingMessageObject;	 Catch:{ Exception -> 0x03f5 }
        r4 = r4.getDuration();	 Catch:{ Exception -> 0x03f5 }
        r10 = (long) r4;	 Catch:{ Exception -> 0x03f5 }
    L_0x03e7:
        r4 = (float) r10;	 Catch:{ Exception -> 0x03f5 }
        r6 = r1.playingMessageObject;	 Catch:{ Exception -> 0x03f5 }
        r6 = r6.audioProgress;	 Catch:{ Exception -> 0x03f5 }
        r4 = r4 * r6;	 Catch:{ Exception -> 0x03f5 }
        r4 = (int) r4;	 Catch:{ Exception -> 0x03f5 }
        r6 = r1.audioPlayer;	 Catch:{ Exception -> 0x03f5 }
        r8 = (long) r4;	 Catch:{ Exception -> 0x03f5 }
        r6.seekTo(r8);	 Catch:{ Exception -> 0x03f5 }
        goto L_0x0439;
    L_0x03f5:
        r0 = move-exception;
        r4 = r0;
        r6 = r1.playingMessageObject;
        r6.resetPlayingProgress();
        r2 = r2.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r6 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r7 = new java.lang.Object[r7];
        r8 = r1.playingMessageObject;
        r8 = r8.getId();
        r8 = java.lang.Integer.valueOf(r8);
        r7[r3] = r8;
        r3 = java.lang.Integer.valueOf(r3);
        r7[r5] = r3;
        r2.postNotificationName(r6, r7);
        org.telegram.messenger.FileLog.m3e(r4);
        goto L_0x0439;
    L_0x041f:
        r2 = r1.audioTrackPlayer;
        if (r2 == 0) goto L_0x0439;
    L_0x0423:
        r2 = r1.playingMessageObject;
        r2 = r2.audioProgress;
        r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1));
        if (r2 != 0) goto L_0x042f;
    L_0x042b:
        r2 = r1.playingMessageObject;
        r2.audioProgress = r4;
    L_0x042f:
        r2 = r1.fileDecodingQueue;
        r3 = new org.telegram.messenger.MediaController$22;
        r3.<init>();
        r2.postRunnable(r3);
    L_0x0439:
        r2 = r1.playingMessageObject;
        r2 = r2.isMusic();
        if (r2 == 0) goto L_0x0456;
    L_0x0441:
        r2 = new android.content.Intent;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = org.telegram.messenger.MusicPlayerService.class;
        r2.<init>(r3, r4);
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0450 }
        r3.startService(r2);	 Catch:{ Throwable -> 0x0450 }
        goto L_0x0464;
    L_0x0450:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.m3e(r2);
        goto L_0x0464;
    L_0x0456:
        r2 = new android.content.Intent;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = org.telegram.messenger.MusicPlayerService.class;
        r2.<init>(r3, r4);
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3.stopService(r2);
    L_0x0464:
        return r5;
    L_0x0465:
        r0 = move-exception;
        r4 = r0;
        org.telegram.messenger.FileLog.m3e(r4);
        r2 = r2.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r4 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r5 = new java.lang.Object[r5];
        r6 = r1.playingMessageObject;
        if (r6 == 0) goto L_0x047f;
    L_0x0478:
        r6 = r1.playingMessageObject;
        r6 = r6.getId();
        goto L_0x0480;
    L_0x047f:
        r6 = r3;
    L_0x0480:
        r6 = java.lang.Integer.valueOf(r6);
        r5[r3] = r6;
        r2.postNotificationName(r4, r5);
        r2 = r1.audioPlayer;
        if (r2 == 0) goto L_0x049a;
    L_0x048d:
        r2 = r1.audioPlayer;
        r2.releasePlayer();
        r1.audioPlayer = r7;
        r1.isPaused = r3;
        r1.playingMessageObject = r7;
        r1.downloadingCurrentMessage = r3;
    L_0x049a:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.playMessage(org.telegram.messenger.MessageObject):boolean");
    }

    public void stopAudio() {
        if ((this.audioTrackPlayer != null || this.audioPlayer != null || this.videoPlayer != null) && this.playingMessageObject != null) {
            try {
                if (this.audioPlayer != null) {
                    this.audioPlayer.pause();
                } else if (this.audioTrackPlayer != null) {
                    this.audioTrackPlayer.pause();
                    this.audioTrackPlayer.flush();
                } else if (this.videoPlayer != null) {
                    this.videoPlayer.pause();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            try {
                if (this.audioPlayer != null) {
                    this.audioPlayer.releasePlayer();
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
                        FileLog.m3e(e2);
                    }
                }
            } catch (Throwable e22) {
                FileLog.m3e(e22);
            }
            stopProgressTimer();
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            this.isPaused = false;
            ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
        }
    }

    public AudioInfo getAudioInfo() {
        return this.audioInfo;
    }

    public void toggleShuffleMusic(int i) {
        boolean z = SharedConfig.shuffleMusic;
        SharedConfig.toggleShuffleMusic(i);
        if (z == SharedConfig.shuffleMusic) {
            return;
        }
        if (SharedConfig.shuffleMusic != 0) {
            buildShuffledPlayList();
            this.currentPlaylistNum = 0;
        } else if (this.playingMessageObject != 0) {
            this.currentPlaylistNum = this.playlist.indexOf(this.playingMessageObject);
            if (this.currentPlaylistNum == -1) {
                this.playlist.clear();
                this.shuffledPlaylist.clear();
                cleanupPlayer(true, true);
            }
        }
    }

    public boolean isCurrentPlayer(VideoPlayer videoPlayer) {
        if (this.videoPlayer != videoPlayer) {
            if (this.audioPlayer != videoPlayer) {
                return null;
            }
        }
        return true;
    }

    public boolean pauseMessage(MessageObject messageObject) {
        if (!((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null)) {
            if (isSamePlayingMessage(messageObject) != null) {
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
                    NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
                    return true;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    this.isPaused = false;
                    return false;
                }
            }
        }
        return false;
    }

    public boolean resumeAudio(MessageObject messageObject) {
        if (!((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null)) {
            if (isSamePlayingMessage(messageObject)) {
                try {
                    startProgressTimer(this.playingMessageObject);
                    if (this.audioPlayer != null) {
                        this.audioPlayer.play();
                    } else if (this.audioTrackPlayer != null) {
                        this.audioTrackPlayer.play();
                        checkPlayerQueue();
                    } else if (this.videoPlayer != null) {
                        this.videoPlayer.play();
                    }
                    checkAudioFocus(messageObject);
                    this.isPaused = false;
                    NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
                    return true;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return false;
                }
            }
        }
        return false;
    }

    public boolean isRoundVideoDrawingReady() {
        return this.currentAspectRatioFrameLayout != null && this.currentAspectRatioFrameLayout.isDrawingReady();
    }

    public ArrayList<MessageObject> getPlaylist() {
        return this.playlist;
    }

    public boolean isPlayingMessage(MessageObject messageObject) {
        if (!((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null)) {
            if (this.playingMessageObject != null) {
                if (this.playingMessageObject.eventId != 0 && this.playingMessageObject.eventId == messageObject.eventId) {
                    return this.downloadingCurrentMessage ^ 1;
                }
                if (isSamePlayingMessage(messageObject) != null) {
                    return this.downloadingCurrentMessage ^ 1;
                }
                return false;
            }
        }
        return false;
    }

    public boolean isMessagePaused() {
        if (!this.isPaused) {
            if (!this.downloadingCurrentMessage) {
                return false;
            }
        }
        return true;
    }

    public boolean isDownloadingCurrentMessage() {
        return this.downloadingCurrentMessage;
    }

    public void setReplyingMessage(MessageObject messageObject) {
        this.recordReplyingMessageObject = messageObject;
    }

    public void startRecording(int r10, long r11, org.telegram.messenger.MessageObject r13) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r9 = this;
        r0 = r9.playingMessageObject;
        if (r0 == 0) goto L_0x0019;
    L_0x0004:
        r0 = r9.playingMessageObject;
        r0 = r9.isPlayingMessage(r0);
        if (r0 == 0) goto L_0x0019;
    L_0x000c:
        r0 = r9.isMessagePaused();
        if (r0 != 0) goto L_0x0019;
    L_0x0012:
        r0 = 1;
        r1 = r9.playingMessageObject;
        r9.pauseMessage(r1);
        goto L_0x001a;
    L_0x0019:
        r0 = 0;
    L_0x001a:
        r1 = r9.feedbackView;	 Catch:{ Exception -> 0x0021 }
        r2 = 3;	 Catch:{ Exception -> 0x0021 }
        r3 = 2;	 Catch:{ Exception -> 0x0021 }
        r1.performHapticFeedback(r2, r3);	 Catch:{ Exception -> 0x0021 }
    L_0x0021:
        r1 = r9.recordQueue;
        r8 = new org.telegram.messenger.MediaController$23;
        r2 = r8;
        r3 = r9;
        r4 = r10;
        r5 = r11;
        r7 = r13;
        r2.<init>(r4, r5, r7);
        r9.recordStartRunnable = r8;
        if (r0 == 0) goto L_0x0034;
    L_0x0031:
        r10 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        goto L_0x0036;
    L_0x0034:
        r10 = 50;
    L_0x0036:
        r1.postRunnable(r8, r10);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.startRecording(int, long, org.telegram.messenger.MessageObject):void");
    }

    public void generateWaveform(MessageObject messageObject) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageObject.getId());
        stringBuilder.append("_");
        stringBuilder.append(messageObject.getDialogId());
        final String stringBuilder2 = stringBuilder.toString();
        final String absolutePath = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(stringBuilder2)) {
            this.generatingWaveform.put(stringBuilder2, messageObject);
            Utilities.globalQueue.postRunnable(new Runnable() {
                public void run() {
                    final byte[] waveform = MediaController.this.getWaveform(absolutePath);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessageObject messageObject = (MessageObject) MediaController.this.generatingWaveform.remove(stringBuilder2);
                            if (!(messageObject == null || waveform == null)) {
                                for (int i = 0; i < messageObject.getDocument().attributes.size(); i++) {
                                    DocumentAttribute documentAttribute = (DocumentAttribute) messageObject.getDocument().attributes.get(i);
                                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                                        documentAttribute.waveform = waveform;
                                        documentAttribute.flags |= 4;
                                        break;
                                    }
                                }
                                messages_Messages tL_messages_messages = new TL_messages_messages();
                                tL_messages_messages.messages.add(messageObject.messageOwner);
                                MessagesStorage.getInstance(messageObject.currentAccount).putMessages(tL_messages_messages, messageObject.getDialogId(), -1, 0, false);
                                new ArrayList().add(messageObject);
                                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), r2);
                            }
                        }
                    });
                }
            });
        }
    }

    private void stopRecordingInternal(final int i) {
        if (i != 0) {
            final TL_document tL_document = this.recordingAudio;
            final File file = this.recordingAudioFile;
            this.fileEncodingQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MediaController$25$1 */
                class C02601 implements Runnable {
                    C02601() {
                    }

                    public void run() {
                        tL_document.date = ConnectionsManager.getInstance(MediaController.this.recordingCurrentAccount).getCurrentTime();
                        tL_document.size = (int) file.length();
                        TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                        tL_documentAttributeAudio.voice = true;
                        tL_documentAttributeAudio.waveform = MediaController.this.getWaveform2(MediaController.this.recordSamples, MediaController.this.recordSamples.length);
                        if (tL_documentAttributeAudio.waveform != null) {
                            tL_documentAttributeAudio.flags |= 4;
                        }
                        long access$700 = MediaController.this.recordTimeCount;
                        tL_documentAttributeAudio.duration = (int) (MediaController.this.recordTimeCount / 1000);
                        tL_document.attributes.add(tL_documentAttributeAudio);
                        if (access$700 > 700) {
                            if (i == 1) {
                                SendMessagesHelper.getInstance(MediaController.this.recordingCurrentAccount).sendMessage(tL_document, null, file.getAbsolutePath(), MediaController.this.recordDialogId, MediaController.this.recordReplyingMessageObject, null, null, null, null, 0);
                            }
                            NotificationCenter instance = NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount);
                            int i = NotificationCenter.audioDidSent;
                            Object[] objArr = new Object[2];
                            String str = null;
                            objArr[0] = i == 2 ? tL_document : null;
                            if (i == 2) {
                                str = file.getAbsolutePath();
                            }
                            objArr[1] = str;
                            instance.postNotificationName(i, objArr);
                            return;
                        }
                        file.delete();
                    }
                }

                public void run() {
                    MediaController.this.stopRecord();
                    AndroidUtilities.runOnUIThread(new C02601());
                }
            });
        }
        try {
            if (this.audioRecorder != null) {
                this.audioRecorder.release();
                this.audioRecorder = null;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        this.recordingAudio = null;
        this.recordingAudioFile = null;
    }

    public void stopRecording(final int i) {
        if (this.recordStartRunnable != null) {
            this.recordQueue.cancelRunnable(this.recordStartRunnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MediaController$26$1 */
            class C02611 implements Runnable {
                C02611() {
                }

                public void run() {
                    NotificationCenter instance = NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount);
                    int i = NotificationCenter.recordStopped;
                    int i2 = 1;
                    Object[] objArr = new Object[1];
                    if (i != 2) {
                        i2 = 0;
                    }
                    objArr[0] = Integer.valueOf(i2);
                    instance.postNotificationName(i, objArr);
                }
            }

            public void run() {
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                /*
                r3 = this;
                r0 = org.telegram.messenger.MediaController.this;
                r0 = r0.audioRecorder;
                if (r0 != 0) goto L_0x0009;
            L_0x0008:
                return;
            L_0x0009:
                r0 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x001a }
                r1 = r3;	 Catch:{ Exception -> 0x001a }
                r0.sendAfterDone = r1;	 Catch:{ Exception -> 0x001a }
                r0 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x001a }
                r0 = r0.audioRecorder;	 Catch:{ Exception -> 0x001a }
                r0.stop();	 Catch:{ Exception -> 0x001a }
                goto L_0x002f;
            L_0x001a:
                r0 = move-exception;
                org.telegram.messenger.FileLog.m3e(r0);
                r0 = org.telegram.messenger.MediaController.this;
                r0 = r0.recordingAudioFile;
                if (r0 == 0) goto L_0x002f;
            L_0x0026:
                r0 = org.telegram.messenger.MediaController.this;
                r0 = r0.recordingAudioFile;
                r0.delete();
            L_0x002f:
                r0 = r3;
                if (r0 != 0) goto L_0x0039;
            L_0x0033:
                r0 = org.telegram.messenger.MediaController.this;
                r1 = 0;
                r0.stopRecordingInternal(r1);
            L_0x0039:
                r0 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x0044 }
                r0 = r0.feedbackView;	 Catch:{ Exception -> 0x0044 }
                r1 = 3;	 Catch:{ Exception -> 0x0044 }
                r2 = 2;	 Catch:{ Exception -> 0x0044 }
                r0.performHapticFeedback(r1, r2);	 Catch:{ Exception -> 0x0044 }
            L_0x0044:
                r0 = new org.telegram.messenger.MediaController$26$1;
                r0.<init>();
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.26.run():void");
            }
        });
    }

    public static void saveFile(String str, Context context, int i, String str2, String str3) {
        final boolean[] zArr;
        Throwable e;
        AlertDialog alertDialog;
        final int i2;
        final String str4;
        final String str5;
        if (str != null) {
            File file;
            AlertDialog alertDialog2;
            AlertDialog alertDialog3 = null;
            if (!(str == null || str.length() == 0)) {
                File file2 = new File(str);
                if (file2.exists() != null) {
                    file = file2;
                    if (file == null) {
                        zArr = new boolean[]{false};
                        if (file.exists()) {
                            if (!(context == null || i == 0)) {
                                try {
                                    alertDialog2 = new AlertDialog(context, 2);
                                } catch (Exception e2) {
                                    e = e2;
                                    FileLog.m3e(e);
                                    alertDialog = alertDialog3;
                                    i2 = i;
                                    str4 = str2;
                                    str5 = str3;
                                    new Thread(new Runnable() {

                                        /* renamed from: org.telegram.messenger.MediaController$28$2 */
                                        class C02632 implements Runnable {
                                            C02632() {
                                            }

                                            public void run() {
                                                try {
                                                    alertDialog.dismiss();
                                                } catch (Throwable e) {
                                                    FileLog.m3e(e);
                                                }
                                            }
                                        }

                                        public void run() {
                                            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                                            /*
                                            r22 = this;
                                            r1 = r22;
                                            r2 = r3;	 Catch:{ Exception -> 0x019d }
                                            r3 = 2;	 Catch:{ Exception -> 0x019d }
                                            r4 = 1;	 Catch:{ Exception -> 0x019d }
                                            r5 = 0;	 Catch:{ Exception -> 0x019d }
                                            if (r2 != 0) goto L_0x000f;	 Catch:{ Exception -> 0x019d }
                                        L_0x0009:
                                            r2 = org.telegram.messenger.AndroidUtilities.generatePicturePath();	 Catch:{ Exception -> 0x019d }
                                            goto L_0x00a7;	 Catch:{ Exception -> 0x019d }
                                        L_0x000f:
                                            r2 = r3;	 Catch:{ Exception -> 0x019d }
                                            if (r2 != r4) goto L_0x0019;	 Catch:{ Exception -> 0x019d }
                                        L_0x0013:
                                            r2 = org.telegram.messenger.AndroidUtilities.generateVideoPath();	 Catch:{ Exception -> 0x019d }
                                            goto L_0x00a7;	 Catch:{ Exception -> 0x019d }
                                        L_0x0019:
                                            r2 = r3;	 Catch:{ Exception -> 0x019d }
                                            if (r2 != r3) goto L_0x0024;	 Catch:{ Exception -> 0x019d }
                                        L_0x001d:
                                            r2 = android.os.Environment.DIRECTORY_DOWNLOADS;	 Catch:{ Exception -> 0x019d }
                                            r2 = android.os.Environment.getExternalStoragePublicDirectory(r2);	 Catch:{ Exception -> 0x019d }
                                            goto L_0x002a;	 Catch:{ Exception -> 0x019d }
                                        L_0x0024:
                                            r2 = android.os.Environment.DIRECTORY_MUSIC;	 Catch:{ Exception -> 0x019d }
                                            r2 = android.os.Environment.getExternalStoragePublicDirectory(r2);	 Catch:{ Exception -> 0x019d }
                                        L_0x002a:
                                            r2.mkdir();	 Catch:{ Exception -> 0x019d }
                                            r6 = new java.io.File;	 Catch:{ Exception -> 0x019d }
                                            r7 = r4;	 Catch:{ Exception -> 0x019d }
                                            r6.<init>(r2, r7);	 Catch:{ Exception -> 0x019d }
                                            r7 = r6.exists();	 Catch:{ Exception -> 0x019d }
                                            if (r7 == 0) goto L_0x00a6;	 Catch:{ Exception -> 0x019d }
                                        L_0x003a:
                                            r7 = r4;	 Catch:{ Exception -> 0x019d }
                                            r8 = 46;	 Catch:{ Exception -> 0x019d }
                                            r7 = r7.lastIndexOf(r8);	 Catch:{ Exception -> 0x019d }
                                            r8 = r6;	 Catch:{ Exception -> 0x019d }
                                            r6 = r5;	 Catch:{ Exception -> 0x019d }
                                        L_0x0044:
                                            r9 = 10;	 Catch:{ Exception -> 0x019d }
                                            if (r6 >= r9) goto L_0x00a4;	 Catch:{ Exception -> 0x019d }
                                        L_0x0048:
                                            r8 = -1;	 Catch:{ Exception -> 0x019d }
                                            if (r7 == r8) goto L_0x0076;	 Catch:{ Exception -> 0x019d }
                                        L_0x004b:
                                            r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019d }
                                            r8.<init>();	 Catch:{ Exception -> 0x019d }
                                            r9 = r4;	 Catch:{ Exception -> 0x019d }
                                            r9 = r9.substring(r5, r7);	 Catch:{ Exception -> 0x019d }
                                            r8.append(r9);	 Catch:{ Exception -> 0x019d }
                                            r9 = "(";	 Catch:{ Exception -> 0x019d }
                                            r8.append(r9);	 Catch:{ Exception -> 0x019d }
                                            r9 = r6 + 1;	 Catch:{ Exception -> 0x019d }
                                            r8.append(r9);	 Catch:{ Exception -> 0x019d }
                                            r9 = ")";	 Catch:{ Exception -> 0x019d }
                                            r8.append(r9);	 Catch:{ Exception -> 0x019d }
                                            r9 = r4;	 Catch:{ Exception -> 0x019d }
                                            r9 = r9.substring(r7);	 Catch:{ Exception -> 0x019d }
                                            r8.append(r9);	 Catch:{ Exception -> 0x019d }
                                            r8 = r8.toString();	 Catch:{ Exception -> 0x019d }
                                            goto L_0x0093;	 Catch:{ Exception -> 0x019d }
                                        L_0x0076:
                                            r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019d }
                                            r8.<init>();	 Catch:{ Exception -> 0x019d }
                                            r9 = r4;	 Catch:{ Exception -> 0x019d }
                                            r8.append(r9);	 Catch:{ Exception -> 0x019d }
                                            r9 = "(";	 Catch:{ Exception -> 0x019d }
                                            r8.append(r9);	 Catch:{ Exception -> 0x019d }
                                            r9 = r6 + 1;	 Catch:{ Exception -> 0x019d }
                                            r8.append(r9);	 Catch:{ Exception -> 0x019d }
                                            r9 = ")";	 Catch:{ Exception -> 0x019d }
                                            r8.append(r9);	 Catch:{ Exception -> 0x019d }
                                            r8 = r8.toString();	 Catch:{ Exception -> 0x019d }
                                        L_0x0093:
                                            r9 = new java.io.File;	 Catch:{ Exception -> 0x019d }
                                            r9.<init>(r2, r8);	 Catch:{ Exception -> 0x019d }
                                            r8 = r9.exists();	 Catch:{ Exception -> 0x019d }
                                            if (r8 != 0) goto L_0x00a0;	 Catch:{ Exception -> 0x019d }
                                        L_0x009e:
                                            r2 = r9;	 Catch:{ Exception -> 0x019d }
                                            goto L_0x00a7;	 Catch:{ Exception -> 0x019d }
                                        L_0x00a0:
                                            r6 = r6 + 1;	 Catch:{ Exception -> 0x019d }
                                            r8 = r9;	 Catch:{ Exception -> 0x019d }
                                            goto L_0x0044;	 Catch:{ Exception -> 0x019d }
                                        L_0x00a4:
                                            r2 = r8;	 Catch:{ Exception -> 0x019d }
                                            goto L_0x00a7;	 Catch:{ Exception -> 0x019d }
                                        L_0x00a6:
                                            r2 = r6;	 Catch:{ Exception -> 0x019d }
                                        L_0x00a7:
                                            r6 = r2.exists();	 Catch:{ Exception -> 0x019d }
                                            if (r6 != 0) goto L_0x00b0;	 Catch:{ Exception -> 0x019d }
                                        L_0x00ad:
                                            r2.createNewFile();	 Catch:{ Exception -> 0x019d }
                                        L_0x00b0:
                                            r6 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x019d }
                                            r8 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
                                            r10 = r6 - r8;
                                            r6 = 0;
                                            r7 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0140, all -> 0x013b }
                                            r12 = r5;	 Catch:{ Exception -> 0x0140, all -> 0x013b }
                                            r7.<init>(r12);	 Catch:{ Exception -> 0x0140, all -> 0x013b }
                                            r7 = r7.getChannel();	 Catch:{ Exception -> 0x0140, all -> 0x013b }
                                            r12 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0136, all -> 0x0131 }
                                            r12.<init>(r2);	 Catch:{ Exception -> 0x0136, all -> 0x0131 }
                                            r12 = r12.getChannel();	 Catch:{ Exception -> 0x0136, all -> 0x0131 }
                                            r14 = r7.size();	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r16 = 0;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r19 = r10;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r10 = r16;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                        L_0x00d7:
                                            r6 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            if (r6 >= 0) goto L_0x011e;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                        L_0x00db:
                                            r6 = r6;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r6 = r6[r5];	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            if (r6 == 0) goto L_0x00e2;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                        L_0x00e1:
                                            goto L_0x011e;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                        L_0x00e2:
                                            r3 = r14 - r10;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r5 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r17 = java.lang.Math.min(r5, r3);	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r13 = r12;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r3 = r14;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r14 = r7;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r15 = r10;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r13.transferFrom(r14, r15, r17);	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r13 = r7;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            if (r13 == 0) goto L_0x0114;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                        L_0x00f5:
                                            r13 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r15 = r13 - r8;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r13 = (r19 > r15 ? 1 : (r19 == r15 ? 0 : -1));	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            if (r13 > 0) goto L_0x0114;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                        L_0x00ff:
                                            r13 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r15 = (float) r10;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r8 = (float) r3;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r15 = r15 / r8;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r8 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r15 = r15 * r8;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r8 = (int) r15;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r9 = new org.telegram.messenger.MediaController$28$1;	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r9.<init>(r8);	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);	 Catch:{ Exception -> 0x012e, all -> 0x012a }
                                            r19 = r13;
                                        L_0x0114:
                                            r8 = r10 + r5;
                                            r14 = r3;
                                            r10 = r8;
                                            r3 = 2;
                                            r4 = 1;
                                            r5 = 0;
                                            r8 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
                                            goto L_0x00d7;
                                        L_0x011e:
                                            if (r7 == 0) goto L_0x0123;
                                        L_0x0120:
                                            r7.close();	 Catch:{ Exception -> 0x0123 }
                                        L_0x0123:
                                            if (r12 == 0) goto L_0x0128;
                                        L_0x0125:
                                            r12.close();	 Catch:{ Exception -> 0x0128 }
                                        L_0x0128:
                                            r5 = 1;
                                            goto L_0x0151;
                                        L_0x012a:
                                            r0 = move-exception;
                                            r2 = r0;
                                            goto L_0x0192;
                                        L_0x012e:
                                            r0 = move-exception;
                                            r3 = r0;
                                            goto L_0x0139;
                                        L_0x0131:
                                            r0 = move-exception;
                                            r2 = r0;
                                            r12 = r6;
                                            goto L_0x0192;
                                        L_0x0136:
                                            r0 = move-exception;
                                            r3 = r0;
                                            r12 = r6;
                                        L_0x0139:
                                            r6 = r7;
                                            goto L_0x0143;
                                        L_0x013b:
                                            r0 = move-exception;
                                            r2 = r0;
                                            r7 = r6;
                                            r12 = r7;
                                            goto L_0x0192;
                                        L_0x0140:
                                            r0 = move-exception;
                                            r3 = r0;
                                            r12 = r6;
                                        L_0x0143:
                                            org.telegram.messenger.FileLog.m3e(r3);	 Catch:{ all -> 0x018f }
                                            if (r6 == 0) goto L_0x014b;
                                        L_0x0148:
                                            r6.close();	 Catch:{ Exception -> 0x014b }
                                        L_0x014b:
                                            if (r12 == 0) goto L_0x0150;
                                        L_0x014d:
                                            r12.close();	 Catch:{ Exception -> 0x0150 }
                                        L_0x0150:
                                            r5 = 0;
                                        L_0x0151:
                                            r3 = r6;	 Catch:{ Exception -> 0x019d }
                                            r4 = 0;	 Catch:{ Exception -> 0x019d }
                                            r3 = r3[r4];	 Catch:{ Exception -> 0x019d }
                                            if (r3 == 0) goto L_0x015c;	 Catch:{ Exception -> 0x019d }
                                        L_0x0158:
                                            r2.delete();	 Catch:{ Exception -> 0x019d }
                                            goto L_0x015d;	 Catch:{ Exception -> 0x019d }
                                        L_0x015c:
                                            r4 = r5;	 Catch:{ Exception -> 0x019d }
                                        L_0x015d:
                                            if (r4 == 0) goto L_0x01a2;	 Catch:{ Exception -> 0x019d }
                                        L_0x015f:
                                            r3 = r3;	 Catch:{ Exception -> 0x019d }
                                            r4 = 2;	 Catch:{ Exception -> 0x019d }
                                            if (r3 != r4) goto L_0x0187;	 Catch:{ Exception -> 0x019d }
                                        L_0x0164:
                                            r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x019d }
                                            r4 = "download";	 Catch:{ Exception -> 0x019d }
                                            r3 = r3.getSystemService(r4);	 Catch:{ Exception -> 0x019d }
                                            r4 = r3;	 Catch:{ Exception -> 0x019d }
                                            r4 = (android.app.DownloadManager) r4;	 Catch:{ Exception -> 0x019d }
                                            r5 = r2.getName();	 Catch:{ Exception -> 0x019d }
                                            r6 = r2.getName();	 Catch:{ Exception -> 0x019d }
                                            r7 = 0;	 Catch:{ Exception -> 0x019d }
                                            r8 = r8;	 Catch:{ Exception -> 0x019d }
                                            r9 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x019d }
                                            r10 = r2.length();	 Catch:{ Exception -> 0x019d }
                                            r12 = 1;	 Catch:{ Exception -> 0x019d }
                                            r4.addCompletedDownload(r5, r6, r7, r8, r9, r10, r12);	 Catch:{ Exception -> 0x019d }
                                            goto L_0x01a2;	 Catch:{ Exception -> 0x019d }
                                        L_0x0187:
                                            r2 = android.net.Uri.fromFile(r2);	 Catch:{ Exception -> 0x019d }
                                            org.telegram.messenger.AndroidUtilities.addMediaToGallery(r2);	 Catch:{ Exception -> 0x019d }
                                            goto L_0x01a2;
                                        L_0x018f:
                                            r0 = move-exception;
                                            r2 = r0;
                                            r7 = r6;
                                        L_0x0192:
                                            if (r7 == 0) goto L_0x0197;
                                        L_0x0194:
                                            r7.close();	 Catch:{ Exception -> 0x0197 }
                                        L_0x0197:
                                            if (r12 == 0) goto L_0x019c;
                                        L_0x0199:
                                            r12.close();	 Catch:{ Exception -> 0x019c }
                                        L_0x019c:
                                            throw r2;	 Catch:{ Exception -> 0x019d }
                                        L_0x019d:
                                            r0 = move-exception;
                                            r2 = r0;
                                            org.telegram.messenger.FileLog.m3e(r2);
                                        L_0x01a2:
                                            r2 = r7;
                                            if (r2 == 0) goto L_0x01ae;
                                        L_0x01a6:
                                            r2 = new org.telegram.messenger.MediaController$28$2;
                                            r2.<init>();
                                            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
                                        L_0x01ae:
                                            return;
                                            */
                                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.28.run():void");
                                        }
                                    }).start();
                                }
                                try {
                                    alertDialog2.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                                    alertDialog2.setCanceledOnTouchOutside(false);
                                    alertDialog2.setCancelable(true);
                                    alertDialog2.setOnCancelListener(new OnCancelListener() {
                                        public void onCancel(DialogInterface dialogInterface) {
                                            zArr[0] = true;
                                        }
                                    });
                                    alertDialog2.show();
                                    alertDialog = alertDialog2;
                                } catch (Exception e3) {
                                    e = e3;
                                    alertDialog3 = alertDialog2;
                                    FileLog.m3e(e);
                                    alertDialog = alertDialog3;
                                    i2 = i;
                                    str4 = str2;
                                    str5 = str3;
                                    new Thread(/* anonymous class already generated */).start();
                                }
                                i2 = i;
                                str4 = str2;
                                str5 = str3;
                                new Thread(/* anonymous class already generated */).start();
                            }
                            alertDialog = alertDialog3;
                            i2 = i;
                            str4 = str2;
                            str5 = str3;
                            new Thread(/* anonymous class already generated */).start();
                        }
                    }
                }
            }
            file = null;
            if (file == null) {
                zArr = new boolean[]{false};
                if (file.exists()) {
                    alertDialog2 = new AlertDialog(context, 2);
                    alertDialog2.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                    alertDialog2.setCanceledOnTouchOutside(false);
                    alertDialog2.setCancelable(true);
                    alertDialog2.setOnCancelListener(/* anonymous class already generated */);
                    alertDialog2.show();
                    alertDialog = alertDialog2;
                    i2 = i;
                    str4 = str2;
                    str5 = str3;
                    new Thread(/* anonymous class already generated */).start();
                }
            }
        }
    }

    public static boolean isWebp(Uri uri) {
        Throwable e;
        Throwable th;
        try {
            uri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            try {
                byte[] bArr = new byte[12];
                if (uri.read(bArr, 0, 12) == 12) {
                    String str = new String(bArr);
                    if (str != null) {
                        str = str.toLowerCase();
                        if (str.startsWith("riff") && str.endsWith("webp")) {
                            if (uri != null) {
                                try {
                                    uri.close();
                                } catch (Throwable e2) {
                                    FileLog.m3e(e2);
                                }
                            }
                            return true;
                        }
                    }
                }
                if (uri != null) {
                    try {
                        uri.close();
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                }
            } catch (Exception e3) {
                e = e3;
                try {
                    FileLog.m3e(e);
                    if (uri != null) {
                        uri.close();
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    if (uri != null) {
                        try {
                            uri.close();
                        } catch (Throwable e222) {
                            FileLog.m3e(e222);
                        }
                    }
                    throw th;
                }
            }
        } catch (Uri uri2) {
            e = uri2;
            uri2 = null;
            FileLog.m3e(e);
            if (uri2 != null) {
                uri2.close();
            }
            return false;
        } catch (Throwable th3) {
            th = th3;
            uri2 = null;
            if (uri2 != null) {
                uri2.close();
            }
            throw th;
        }
        return false;
    }

    public static boolean isGif(Uri uri) {
        Throwable e;
        Throwable th;
        try {
            uri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            try {
                byte[] bArr = new byte[3];
                if (uri.read(bArr, 0, 3) == 3) {
                    String str = new String(bArr);
                    if (str != null && str.equalsIgnoreCase("gif")) {
                        if (uri != null) {
                            try {
                                uri.close();
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                        }
                        return true;
                    }
                }
                if (uri != null) {
                    try {
                        uri.close();
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                }
            } catch (Exception e3) {
                e = e3;
                try {
                    FileLog.m3e(e);
                    if (uri != null) {
                        uri.close();
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    if (uri != null) {
                        try {
                            uri.close();
                        } catch (Throwable e222) {
                            FileLog.m3e(e222);
                        }
                    }
                    throw th;
                }
            }
        } catch (Uri uri2) {
            e = uri2;
            uri2 = null;
            FileLog.m3e(e);
            if (uri2 != null) {
                uri2.close();
            }
            return false;
        } catch (Throwable th3) {
            th = th3;
            uri2 = null;
            if (uri2 != null) {
                uri2.close();
            }
            throw th;
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String getFileName(Uri uri) {
        Throwable e;
        Cursor cursor;
        String str = null;
        if (uri.getScheme().equals("content")) {
            Cursor query;
            try {
                query = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
                try {
                    if (query.moveToFirst()) {
                        str = query.getString(query.getColumnIndex("_display_name"));
                    }
                } catch (Exception e2) {
                    e = e2;
                    try {
                        FileLog.m3e(e);
                        if (query != null) {
                            query.close();
                        }
                        if (str == null) {
                            return str;
                        }
                        str = uri.getPath();
                        uri = str.lastIndexOf(47);
                        return uri != -1 ? str : str.substring(uri + 1);
                    } catch (Throwable th) {
                        uri = th;
                        cursor = query;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw uri;
                    }
                }
            } catch (Exception e3) {
                e = e3;
                query = null;
                FileLog.m3e(e);
                if (query != null) {
                    query.close();
                }
                if (str == null) {
                    return str;
                }
                str = uri.getPath();
                uri = str.lastIndexOf(47);
                if (uri != -1) {
                }
            } catch (Throwable th2) {
                uri = th2;
                if (cursor != null) {
                    cursor.close();
                }
                throw uri;
            }
        }
        if (str == null) {
            return str;
        }
        str = uri.getPath();
        uri = str.lastIndexOf(47);
        if (uri != -1) {
        }
    }

    public static String copyFileToCache(Uri uri, String str) {
        Throwable e;
        String str2;
        FileOutputStream fileOutputStream;
        InputStream inputStream = null;
        try {
            String fixFileName = FileLoader.fixFileName(getFileName(uri));
            if (fixFileName == null) {
                int lastLocalId = SharedConfig.getLastLocalId();
                SharedConfig.saveConfig();
                fixFileName = String.format(Locale.US, "%d.%s", new Object[]{Integer.valueOf(lastLocalId), str});
            }
            str = new File(FileLoader.getDirectory(4), "sharing/");
            str.mkdirs();
            File file = new File(str, fixFileName);
            if (AndroidUtilities.isInternalUri(Uri.fromFile(file)) != null) {
                return null;
            }
            uri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            try {
                str = new FileOutputStream(file);
                try {
                    byte[] bArr = new byte[CacheDataSink.DEFAULT_BUFFER_SIZE];
                    while (true) {
                        int read = uri.read(bArr);
                        if (read == -1) {
                            break;
                        }
                        str.write(bArr, 0, read);
                    }
                    fixFileName = file.getAbsolutePath();
                    if (uri != null) {
                        try {
                            uri.close();
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                    if (str != null) {
                        try {
                            str.close();
                        } catch (Throwable e22) {
                            FileLog.m3e(e22);
                        }
                    }
                    return fixFileName;
                } catch (Throwable e3) {
                    str2 = str;
                    str = uri;
                    e22 = e3;
                    fileOutputStream = str2;
                    try {
                        FileLog.m3e(e22);
                        if (str != null) {
                            try {
                                str.close();
                            } catch (Throwable e222) {
                                FileLog.m3e(e222);
                            }
                        }
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable e2222) {
                                FileLog.m3e(e2222);
                            }
                        }
                        return null;
                    } catch (Throwable th) {
                        uri = th;
                        inputStream = str;
                        str = fileOutputStream;
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable e4) {
                                FileLog.m3e(e4);
                            }
                        }
                        if (str != null) {
                            try {
                                str.close();
                            } catch (Throwable e5) {
                                FileLog.m3e(e5);
                            }
                        }
                        throw uri;
                    }
                } catch (Throwable e42) {
                    Throwable th2 = e42;
                    inputStream = uri;
                    uri = th2;
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (str != null) {
                        str.close();
                    }
                    throw uri;
                }
            } catch (String str3) {
                fileOutputStream = null;
                str2 = str3;
                str3 = uri;
                e2222 = str2;
                FileLog.m3e(e2222);
                if (str3 != null) {
                    str3.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                return null;
            } catch (String str32) {
                inputStream = uri;
                uri = str32;
                str32 = null;
                if (inputStream != null) {
                    inputStream.close();
                }
                if (str32 != null) {
                    str32.close();
                }
                throw uri;
            }
        } catch (Exception e6) {
            e2222 = e6;
            str32 = null;
            fileOutputStream = str32;
            FileLog.m3e(e2222);
            if (str32 != null) {
                str32.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            return null;
        } catch (Throwable th3) {
            uri = th3;
            str32 = null;
            if (inputStream != null) {
                inputStream.close();
            }
            if (str32 != null) {
                str32.close();
            }
            throw uri;
        }
    }

    public static void loadGalleryPhotosAlbums(final int i) {
        Thread thread = new Thread(new Runnable() {

            /* renamed from: org.telegram.messenger.MediaController$29$1 */
            class C02641 implements Comparator<PhotoEntry> {
                C02641() {
                }

                public int compare(PhotoEntry photoEntry, PhotoEntry photoEntry2) {
                    if (photoEntry.dateTaken < photoEntry2.dateTaken) {
                        return 1;
                    }
                    return photoEntry.dateTaken > photoEntry2.dateTaken ? -1 : null;
                }
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                AlbumEntry albumEntry;
                Integer num;
                AlbumEntry albumEntry2;
                int i;
                AlbumEntry albumEntry3;
                Integer num2;
                Throwable th;
                Throwable th2;
                AnonymousClass29 anonymousClass29 = this;
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                SparseArray sparseArray = new SparseArray();
                SparseArray sparseArray2 = new SparseArray();
                String stringBuilder;
                try {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
                    stringBuilder2.append("/Camera/");
                    stringBuilder = stringBuilder2.toString();
                } catch (Throwable e) {
                    Throwable e2;
                    FileLog.m3e(e2);
                    stringBuilder = null;
                }
                Cursor cursor;
                Cursor query;
                int columnIndex;
                int columnIndex2;
                int columnIndex3;
                int columnIndex4;
                int columnIndex5;
                int i2;
                int i3;
                String string;
                String string2;
                long j;
                long j2;
                Cursor cursor2;
                int i4;
                int i5;
                PhotoEntry photoEntry;
                int i6;
                AlbumEntry albumEntry4;
                try {
                    Cursor cursor3;
                    if (VERSION.SDK_INT >= 23) {
                        if (VERSION.SDK_INT < 23 || ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                            cursor = null;
                            albumEntry = null;
                            num = null;
                            albumEntry2 = null;
                            if (cursor != null) {
                                try {
                                    cursor.close();
                                } catch (Throwable e22) {
                                    FileLog.m3e(e22);
                                }
                            }
                            try {
                                if (VERSION.SDK_INT >= 23) {
                                    try {
                                        if (VERSION.SDK_INT >= 23 || ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                                            i = 0;
                                            if (cursor != null) {
                                                try {
                                                    cursor.close();
                                                } catch (Exception e3) {
                                                    e22 = e3;
                                                    FileLog.m3e(e22);
                                                    albumEntry3 = albumEntry;
                                                    num2 = num;
                                                    for (i = 
/*
Method generation error in method: org.telegram.messenger.MediaController.29.run():void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r9_35 'i' int) = (r9_31 'i' int), (r9_31 'i' int), (r9_33 'i' int), (r9_33 'i' int), (r9_34 'i' int) binds: {(r9_31 'i' int)=B:184:0x030e, (r9_31 'i' int)=B:187:0x0313, (r9_33 'i' int)=B:196:0x031f, (r9_33 'i' int)=B:199:0x0324, (r9_34 'i' int)=B:201:0x0326} in method: org.telegram.messenger.MediaController.29.run():void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:184)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeCatchBlock(RegionGen.java:323)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:290)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:279)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:279)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:279)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.InsnGen.inlineAnonymousConstr(InsnGen.java:608)
	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:559)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:334)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:211)
	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:101)
	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:686)
	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:574)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:334)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:203)
	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:100)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:50)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:75)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:12)
	at jadx.core.ProcessClass.process(ProcessClass.java:40)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:537)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:509)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 83 more

*/
                                                });
                                                thread.setPriority(1);
                                                thread.start();
                                            }

                                            private static void broadcastNewPhotos(int i, ArrayList<AlbumEntry> arrayList, ArrayList<AlbumEntry> arrayList2, Integer num, AlbumEntry albumEntry, AlbumEntry albumEntry2, int i2) {
                                                if (broadcastPhotosRunnable != null) {
                                                    AndroidUtilities.cancelRunOnUIThread(broadcastPhotosRunnable);
                                                }
                                                final int i3 = i;
                                                final ArrayList<AlbumEntry> arrayList3 = arrayList;
                                                final ArrayList<AlbumEntry> arrayList4 = arrayList2;
                                                final Integer num2 = num;
                                                final AlbumEntry albumEntry3 = albumEntry;
                                                final AlbumEntry albumEntry4 = albumEntry2;
                                                Runnable anonymousClass30 = new Runnable() {
                                                    public void run() {
                                                        if (PhotoViewer.getInstance().isVisible()) {
                                                            MediaController.broadcastNewPhotos(i3, arrayList3, arrayList4, num2, albumEntry3, albumEntry4, 1000);
                                                            return;
                                                        }
                                                        MediaController.broadcastPhotosRunnable = null;
                                                        MediaController.allPhotosAlbumEntry = albumEntry4;
                                                        MediaController.allMediaAlbumEntry = albumEntry3;
                                                        for (int i = 0; i < 3; i++) {
                                                            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.albumsDidLoaded, Integer.valueOf(i3), arrayList3, arrayList4, num2);
                                                        }
                                                    }
                                                };
                                                broadcastPhotosRunnable = anonymousClass30;
                                                AndroidUtilities.runOnUIThread(anonymousClass30, (long) i2);
                                            }

                                            public void scheduleVideoConvert(MessageObject messageObject) {
                                                scheduleVideoConvert(messageObject, false);
                                            }

                                            public boolean scheduleVideoConvert(MessageObject messageObject, boolean z) {
                                                if (messageObject != null) {
                                                    if (messageObject.videoEditedInfo != null) {
                                                        if (z && !this.videoConvertQueue.isEmpty()) {
                                                            return false;
                                                        }
                                                        if (z) {
                                                            new File(messageObject.messageOwner.attachPath).delete();
                                                        }
                                                        this.videoConvertQueue.add(messageObject);
                                                        if (this.videoConvertQueue.size() == 1) {
                                                            startVideoConvertFromQueue();
                                                        }
                                                        return true;
                                                    }
                                                }
                                                return false;
                                            }

                                            public void cancelVideoConvert(MessageObject messageObject) {
                                                if (messageObject == null) {
                                                    synchronized (this.videoConvertSync) {
                                                        this.cancelCurrentVideoConversion = true;
                                                    }
                                                } else if (!this.videoConvertQueue.isEmpty()) {
                                                    int i = 0;
                                                    while (i < this.videoConvertQueue.size()) {
                                                        MessageObject messageObject2 = (MessageObject) this.videoConvertQueue.get(i);
                                                        if (messageObject2.getId() != messageObject.getId() || messageObject2.currentAccount != messageObject.currentAccount) {
                                                            i++;
                                                        } else if (i == 0) {
                                                            synchronized (this.videoConvertSync) {
                                                                this.cancelCurrentVideoConversion = true;
                                                            }
                                                            return;
                                                        } else {
                                                            this.videoConvertQueue.remove(i);
                                                            return;
                                                        }
                                                    }
                                                }
                                            }

                                            private boolean startVideoConvertFromQueue() {
                                                int i = 0;
                                                if (this.videoConvertQueue.isEmpty()) {
                                                    return false;
                                                }
                                                synchronized (this.videoConvertSync) {
                                                    this.cancelCurrentVideoConversion = false;
                                                }
                                                MessageObject messageObject = (MessageObject) this.videoConvertQueue.get(0);
                                                Intent intent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
                                                intent.putExtra("path", messageObject.messageOwner.attachPath);
                                                intent.putExtra("currentAccount", messageObject.currentAccount);
                                                if (messageObject.messageOwner.media.document != null) {
                                                    while (i < messageObject.messageOwner.media.document.attributes.size()) {
                                                        if (((DocumentAttribute) messageObject.messageOwner.media.document.attributes.get(i)) instanceof TL_documentAttributeAnimated) {
                                                            intent.putExtra("gif", true);
                                                            break;
                                                        }
                                                        i++;
                                                    }
                                                }
                                                if (messageObject.getId() != 0) {
                                                    try {
                                                        ApplicationLoader.applicationContext.startService(intent);
                                                    } catch (Throwable th) {
                                                        FileLog.m3e(th);
                                                    }
                                                }
                                                VideoConvertRunnable.runConversion(messageObject);
                                                return true;
                                            }

                                            @SuppressLint({"NewApi"})
                                            public static MediaCodecInfo selectCodec(String str) {
                                                int codecCount = MediaCodecList.getCodecCount();
                                                MediaCodecInfo mediaCodecInfo = null;
                                                for (int i = 0; i < codecCount; i++) {
                                                    MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
                                                    if (codecInfoAt.isEncoder()) {
                                                        MediaCodecInfo mediaCodecInfo2 = mediaCodecInfo;
                                                        for (String equalsIgnoreCase : codecInfoAt.getSupportedTypes()) {
                                                            if (equalsIgnoreCase.equalsIgnoreCase(str)) {
                                                                String name = codecInfoAt.getName();
                                                                if (name != null && (!name.equals("OMX.SEC.avc.enc") || name.equals("OMX.SEC.AVC.Encoder"))) {
                                                                    return codecInfoAt;
                                                                }
                                                                mediaCodecInfo2 = codecInfoAt;
                                                            }
                                                        }
                                                        mediaCodecInfo = mediaCodecInfo2;
                                                    }
                                                }
                                                return mediaCodecInfo;
                                            }

                                            @SuppressLint({"NewApi"})
                                            public static int selectColorFormat(MediaCodecInfo mediaCodecInfo, String str) {
                                                str = mediaCodecInfo.getCapabilitiesForType(str);
                                                int i = 0;
                                                int i2 = 0;
                                                while (i < str.colorFormats.length) {
                                                    int i3 = str.colorFormats[i];
                                                    if (isRecognizedFormat(i3)) {
                                                        if (mediaCodecInfo.getName().equals("OMX.SEC.AVC.Encoder")) {
                                                            if (i3 == 19) {
                                                                i2 = i3;
                                                            }
                                                        }
                                                        return i3;
                                                    }
                                                    i++;
                                                }
                                                return i2;
                                            }

                                            private int findTrack(MediaExtractor mediaExtractor, boolean z) {
                                                int trackCount = mediaExtractor.getTrackCount();
                                                for (int i = 0; i < trackCount; i++) {
                                                    String string = mediaExtractor.getTrackFormat(i).getString("mime");
                                                    if (z) {
                                                        if (string.startsWith("audio/")) {
                                                            return i;
                                                        }
                                                    } else if (string.startsWith("video/")) {
                                                        return i;
                                                    }
                                                }
                                                return -5;
                                            }

                                            private void didWriteData(MessageObject messageObject, File file, boolean z, boolean z2) {
                                                final boolean z3 = this.videoConvertFirstWrite;
                                                if (z3) {
                                                    this.videoConvertFirstWrite = false;
                                                }
                                                final boolean z4 = z2;
                                                final boolean z5 = z;
                                                final MessageObject messageObject2 = messageObject;
                                                final File file2 = file;
                                                AndroidUtilities.runOnUIThread(new Runnable() {
                                                    public void run() {
                                                        if (z4 || z5) {
                                                            synchronized (MediaController.this.videoConvertSync) {
                                                                MediaController.this.cancelCurrentVideoConversion = false;
                                                            }
                                                            MediaController.this.videoConvertQueue.remove(messageObject2);
                                                            MediaController.this.startVideoConvertFromQueue();
                                                        }
                                                        if (z4) {
                                                            NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.FilePreparingFailed, messageObject2, file2.toString());
                                                            return;
                                                        }
                                                        if (z3) {
                                                            NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.FilePreparingStarted, messageObject2, file2.toString());
                                                        }
                                                        NotificationCenter instance = NotificationCenter.getInstance(messageObject2.currentAccount);
                                                        int i = NotificationCenter.FileNewChunkAvailable;
                                                        Object[] objArr = new Object[4];
                                                        objArr[0] = messageObject2;
                                                        objArr[1] = file2.toString();
                                                        objArr[2] = Long.valueOf(file2.length());
                                                        objArr[3] = Long.valueOf(z5 ? file2.length() : 0);
                                                        instance.postNotificationName(i, objArr);
                                                    }
                                                });
                                            }

                                            private long readAndWriteTracks(MessageObject messageObject, MediaExtractor mediaExtractor, MP4Builder mP4Builder, BufferInfo bufferInfo, long j, long j2, File file, boolean z) throws Exception {
                                                MediaController mediaController;
                                                int addTrack;
                                                int integer;
                                                int addTrack2;
                                                int i;
                                                MediaController mediaController2;
                                                MediaExtractor mediaExtractor2 = mediaExtractor;
                                                MP4Builder mP4Builder2 = mP4Builder;
                                                BufferInfo bufferInfo2 = bufferInfo;
                                                long j3 = j;
                                                boolean z2 = false;
                                                int findTrack = findTrack(mediaExtractor2, false);
                                                int findTrack2 = z ? findTrack(mediaExtractor2, true) : -1;
                                                if (findTrack >= 0) {
                                                    mediaExtractor2.selectTrack(findTrack);
                                                    MediaFormat trackFormat = mediaExtractor2.getTrackFormat(findTrack);
                                                    addTrack = mP4Builder2.addTrack(trackFormat, false);
                                                    integer = trackFormat.getInteger("max-input-size");
                                                    if (j3 > 0) {
                                                        mediaExtractor2.seekTo(j3, 0);
                                                    } else {
                                                        mediaExtractor2.seekTo(0, 0);
                                                    }
                                                } else {
                                                    integer = 0;
                                                    addTrack = -1;
                                                }
                                                if (findTrack2 >= 0) {
                                                    mediaExtractor2.selectTrack(findTrack2);
                                                    trackFormat = mediaExtractor2.getTrackFormat(findTrack2);
                                                    addTrack2 = mP4Builder2.addTrack(trackFormat, true);
                                                    integer = Math.max(trackFormat.getInteger("max-input-size"), integer);
                                                    if (j3 > 0) {
                                                        mediaExtractor2.seekTo(j3, 0);
                                                    } else {
                                                        mediaExtractor2.seekTo(0, 0);
                                                    }
                                                } else {
                                                    addTrack2 = -1;
                                                }
                                                ByteBuffer allocateDirect = ByteBuffer.allocateDirect(integer);
                                                if (findTrack2 < 0) {
                                                    if (findTrack < 0) {
                                                        return -1;
                                                    }
                                                }
                                                checkConversionCanceled();
                                                int i2 = 0;
                                                long j4 = -1;
                                                while (i2 == 0) {
                                                    int i3;
                                                    int arrayOffset;
                                                    boolean z3;
                                                    MP4Builder mP4Builder3;
                                                    boolean z4;
                                                    checkConversionCanceled();
                                                    bufferInfo2.size = mediaExtractor2.readSampleData(allocateDirect, z2);
                                                    int sampleTrackIndex = mediaExtractor.getSampleTrackIndex();
                                                    int i4 = sampleTrackIndex == findTrack ? addTrack : sampleTrackIndex == findTrack2 ? addTrack2 : -1;
                                                    File file2;
                                                    if (i4 != -1) {
                                                        boolean z5;
                                                        MessageObject messageObject2;
                                                        i3 = i2;
                                                        if (VERSION.SDK_INT < 21) {
                                                            allocateDirect.position(z2);
                                                            allocateDirect.limit(bufferInfo2.size);
                                                        }
                                                        if (sampleTrackIndex != findTrack2) {
                                                            byte[] array = allocateDirect.array();
                                                            if (array != null) {
                                                                arrayOffset = allocateDirect.arrayOffset();
                                                                int limit = arrayOffset + allocateDirect.limit();
                                                                int i5 = -1;
                                                                while (true) {
                                                                    int i6 = 4;
                                                                    i = findTrack2;
                                                                    findTrack2 = limit - 4;
                                                                    if (arrayOffset > findTrack2) {
                                                                        break;
                                                                    }
                                                                    int i7;
                                                                    if (array[arrayOffset] == (byte) 0 && array[arrayOffset + 1] == (byte) 0 && array[arrayOffset + 2] == (byte) 0) {
                                                                        if (array[arrayOffset + 3] != (byte) 1) {
                                                                        }
                                                                        if (i5 != -1) {
                                                                            i7 = arrayOffset - i5;
                                                                            if (arrayOffset != findTrack2) {
                                                                                i6 = 0;
                                                                            }
                                                                            i7 -= i6;
                                                                            array[i5] = (byte) (i7 >> 24);
                                                                            array[i5 + 1] = (byte) (i7 >> 16);
                                                                            array[i5 + 2] = (byte) (i7 >> 8);
                                                                            array[i5 + 3] = (byte) i7;
                                                                        }
                                                                        i5 = arrayOffset;
                                                                        arrayOffset++;
                                                                        findTrack2 = i;
                                                                        mediaController = this;
                                                                        mP4Builder2 = mP4Builder;
                                                                    }
                                                                    if (arrayOffset != findTrack2) {
                                                                        arrayOffset++;
                                                                        findTrack2 = i;
                                                                        mediaController = this;
                                                                        mP4Builder2 = mP4Builder;
                                                                    }
                                                                    if (i5 != -1) {
                                                                        i7 = arrayOffset - i5;
                                                                        if (arrayOffset != findTrack2) {
                                                                            i6 = 0;
                                                                        }
                                                                        i7 -= i6;
                                                                        array[i5] = (byte) (i7 >> 24);
                                                                        array[i5 + 1] = (byte) (i7 >> 16);
                                                                        array[i5 + 2] = (byte) (i7 >> 8);
                                                                        array[i5 + 3] = (byte) i7;
                                                                    }
                                                                    i5 = arrayOffset;
                                                                    arrayOffset++;
                                                                    findTrack2 = i;
                                                                    mediaController = this;
                                                                    mP4Builder2 = mP4Builder;
                                                                }
                                                                if (bufferInfo2.size < 0) {
                                                                    bufferInfo2.presentationTimeUs = mediaExtractor.getSampleTime();
                                                                    z5 = false;
                                                                } else {
                                                                    bufferInfo2.size = 0;
                                                                    z5 = true;
                                                                }
                                                                if (bufferInfo2.size > 0 || r0) {
                                                                    messageObject2 = messageObject;
                                                                    file2 = file;
                                                                } else {
                                                                    long j5;
                                                                    if (sampleTrackIndex == findTrack) {
                                                                        j5 = 0;
                                                                        if (j3 > 0 && j4 == -1) {
                                                                            j4 = bufferInfo2.presentationTimeUs;
                                                                        }
                                                                    } else {
                                                                        j5 = 0;
                                                                    }
                                                                    if (j2 >= j5) {
                                                                        if (bufferInfo2.presentationTimeUs >= j2) {
                                                                            messageObject2 = messageObject;
                                                                            file2 = file;
                                                                            z5 = true;
                                                                        }
                                                                    }
                                                                    z3 = false;
                                                                    bufferInfo2.offset = 0;
                                                                    bufferInfo2.flags = mediaExtractor.getSampleFlags();
                                                                    mP4Builder3 = mP4Builder;
                                                                    arrayOffset = 1;
                                                                    if (mP4Builder3.writeSampleData(i4, allocateDirect, bufferInfo2, false)) {
                                                                        didWriteData(messageObject, file, false, false);
                                                                    } else {
                                                                        messageObject2 = messageObject;
                                                                        file2 = file;
                                                                        mediaController2 = this;
                                                                    }
                                                                    if (!z5) {
                                                                        mediaExtractor.advance();
                                                                    }
                                                                    z4 = z5;
                                                                }
                                                                z3 = false;
                                                                mP4Builder3 = mP4Builder;
                                                                mediaController2 = this;
                                                                arrayOffset = 1;
                                                                if (z5) {
                                                                    mediaExtractor.advance();
                                                                }
                                                                z4 = z5;
                                                            }
                                                        }
                                                        i = findTrack2;
                                                        if (bufferInfo2.size < 0) {
                                                            bufferInfo2.size = 0;
                                                            z5 = true;
                                                        } else {
                                                            bufferInfo2.presentationTimeUs = mediaExtractor.getSampleTime();
                                                            z5 = false;
                                                        }
                                                        if (bufferInfo2.size > 0) {
                                                        }
                                                        messageObject2 = messageObject;
                                                        file2 = file;
                                                        z3 = false;
                                                        mP4Builder3 = mP4Builder;
                                                        mediaController2 = this;
                                                        arrayOffset = 1;
                                                        if (z5) {
                                                            mediaExtractor.advance();
                                                        }
                                                        z4 = z5;
                                                    } else {
                                                        mediaController2 = mediaController;
                                                        i3 = i2;
                                                        i = findTrack2;
                                                        arrayOffset = 1;
                                                        file2 = file;
                                                        boolean z6 = z2;
                                                        mP4Builder3 = mP4Builder2;
                                                        z3 = z6;
                                                        if (sampleTrackIndex == -1) {
                                                            z4 = true;
                                                        } else {
                                                            mediaExtractor.advance();
                                                            z4 = z3;
                                                        }
                                                    }
                                                    if (z4) {
                                                        i3 = arrayOffset;
                                                    }
                                                    mediaController = mediaController2;
                                                    i2 = i3;
                                                    findTrack2 = i;
                                                    MP4Builder mP4Builder4 = mP4Builder3;
                                                    z2 = z3;
                                                    mP4Builder2 = mP4Builder4;
                                                }
                                                mediaController2 = mediaController;
                                                i = findTrack2;
                                                if (findTrack >= 0) {
                                                    mediaExtractor2.unselectTrack(findTrack);
                                                }
                                                if (i >= 0) {
                                                    mediaExtractor2.unselectTrack(i);
                                                }
                                                return j4;
                                            }

                                            private void checkConversionCanceled() throws Exception {
                                                synchronized (this.videoConvertSync) {
                                                    boolean z = this.cancelCurrentVideoConversion;
                                                }
                                                if (z) {
                                                    throw new RuntimeException("canceled conversion");
                                                }
                                            }

                                            /* JADX WARNING: inconsistent code. */
                                            /* Code decompiled incorrectly, please refer to instructions dump. */
                                            private boolean convertVideo(org.telegram.messenger.MessageObject r71) {
                                                /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MessageObject):boolean. bs: [B:55:0x011f, B:261:0x0422]
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:86)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                                                /*
                                                r70 = this;
                                                r12 = r70;
                                                r13 = r71;
                                                r1 = r13.videoEditedInfo;
                                                r1 = r1.originalPath;
                                                r2 = r13.videoEditedInfo;
                                                r6 = r2.startTime;
                                                r2 = r13.videoEditedInfo;
                                                r8 = r2.endTime;
                                                r2 = r13.videoEditedInfo;
                                                r2 = r2.resultWidth;
                                                r3 = r13.videoEditedInfo;
                                                r3 = r3.resultHeight;
                                                r4 = r13.videoEditedInfo;
                                                r4 = r4.rotationValue;
                                                r5 = r13.videoEditedInfo;
                                                r5 = r5.originalWidth;
                                                r10 = r13.videoEditedInfo;
                                                r10 = r10.originalHeight;
                                                r11 = r13.videoEditedInfo;
                                                r11 = r11.bitrate;
                                                r14 = r71.getDialogId();
                                                r14 = (int) r14;
                                                if (r14 != 0) goto L_0x0031;
                                            L_0x002f:
                                                r14 = 1;
                                                goto L_0x0032;
                                            L_0x0031:
                                                r14 = 0;
                                            L_0x0032:
                                                r15 = new java.io.File;
                                                r17 = r8;
                                                r8 = r13.messageOwner;
                                                r8 = r8.attachPath;
                                                r15.<init>(r8);
                                                r8 = android.os.Build.VERSION.SDK_INT;
                                                r9 = 18;
                                                if (r8 >= r9) goto L_0x004f;
                                            L_0x0043:
                                                if (r3 <= r2) goto L_0x004f;
                                            L_0x0045:
                                                if (r2 == r5) goto L_0x004f;
                                            L_0x0047:
                                                if (r3 == r10) goto L_0x004f;
                                            L_0x0049:
                                                r4 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
                                                r8 = r4;
                                                r4 = 90;
                                                goto L_0x0072;
                                            L_0x004f:
                                                r8 = android.os.Build.VERSION.SDK_INT;
                                                r9 = 20;
                                                if (r8 <= r9) goto L_0x006c;
                                            L_0x0055:
                                                r8 = 90;
                                                if (r4 != r8) goto L_0x005e;
                                            L_0x0059:
                                                r4 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
                                                r8 = r4;
                                            L_0x005c:
                                                r4 = 0;
                                                goto L_0x0072;
                                            L_0x005e:
                                                r9 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
                                                if (r4 != r9) goto L_0x0067;
                                            L_0x0062:
                                                r4 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
                                                r8 = r4;
                                                r4 = 0;
                                                goto L_0x006d;
                                            L_0x0067:
                                                r9 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
                                                if (r4 != r9) goto L_0x006c;
                                            L_0x006b:
                                                goto L_0x005c;
                                            L_0x006c:
                                                r8 = 0;
                                            L_0x006d:
                                                r69 = r3;
                                                r3 = r2;
                                                r2 = r69;
                                            L_0x0072:
                                                r9 = org.telegram.messenger.ApplicationLoader.applicationContext;
                                                r27 = r6;
                                                r6 = "videoconvert";
                                                r7 = 0;
                                                r9 = r9.getSharedPreferences(r6, r7);
                                                r6 = new java.io.File;
                                                r6.<init>(r1);
                                                r16 = r71.getId();
                                                if (r16 == 0) goto L_0x00be;
                                            L_0x0088:
                                                r7 = "isPreviousOk";
                                                r30 = r11;
                                                r11 = 1;
                                                r7 = r9.getBoolean(r7, r11);
                                                r11 = r9.edit();
                                                r31 = r8;
                                                r8 = "isPreviousOk";
                                                r32 = r10;
                                                r10 = 0;
                                                r8 = r11.putBoolean(r8, r10);
                                                r8.commit();
                                                r6 = r6.canRead();
                                                if (r6 == 0) goto L_0x00ab;
                                            L_0x00a9:
                                                if (r7 != 0) goto L_0x00c4;
                                            L_0x00ab:
                                                r6 = 1;
                                                r12.didWriteData(r13, r15, r6, r6);
                                                r1 = r9.edit();
                                                r2 = "isPreviousOk";
                                                r1 = r1.putBoolean(r2, r6);
                                                r1.commit();
                                                r1 = 0;
                                                return r1;
                                            L_0x00be:
                                                r31 = r8;
                                                r32 = r10;
                                                r30 = r11;
                                            L_0x00c4:
                                                r6 = 1;
                                                r12.videoConvertFirstWrite = r6;
                                                r33 = java.lang.System.currentTimeMillis();
                                                if (r3 == 0) goto L_0x0962;
                                            L_0x00cd:
                                                if (r2 == 0) goto L_0x0962;
                                            L_0x00cf:
                                                r7 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r7.<init>();	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r8 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r8.<init>();	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r8.setCacheFile(r15);	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r8.setRotation(r4);	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r8.setSize(r3, r2);	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r4 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r4.<init>();	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r14 = r4.createMovie(r8, r14);	 Catch:{ Exception -> 0x08e3, all -> 0x08de }
                                                r11 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x08d7, all -> 0x08d3 }
                                                r11.<init>();	 Catch:{ Exception -> 0x08d7, all -> 0x08d3 }
                                                r11.setDataSource(r1);	 Catch:{ Exception -> 0x08cb, all -> 0x08c6 }
                                                r70.checkConversionCanceled();	 Catch:{ Exception -> 0x08cb, all -> 0x08c6 }
                                                r1 = -1;
                                                if (r3 != r5) goto L_0x0136;
                                            L_0x00f9:
                                                r4 = r32;
                                                if (r2 != r4) goto L_0x0136;
                                            L_0x00fd:
                                                if (r31 != 0) goto L_0x0136;
                                            L_0x00ff:
                                                r4 = r13.videoEditedInfo;	 Catch:{ Exception -> 0x012d, all -> 0x0127 }
                                                r4 = r4.roundVideo;	 Catch:{ Exception -> 0x012d, all -> 0x0127 }
                                                if (r4 == 0) goto L_0x0106;
                                            L_0x0105:
                                                goto L_0x0136;
                                            L_0x0106:
                                                r4 = r30;
                                                if (r4 == r1) goto L_0x010d;
                                            L_0x010a:
                                                r19 = 1;
                                                goto L_0x010f;
                                            L_0x010d:
                                                r19 = 0;
                                            L_0x010f:
                                                r1 = r12;
                                                r2 = r13;
                                                r3 = r11;
                                                r4 = r14;
                                                r5 = r7;
                                                r6 = r27;
                                                r10 = r9;
                                                r8 = r17;
                                                r35 = r10;
                                                r10 = r15;
                                                r13 = r11;
                                                r11 = r19;
                                                r1.readAndWriteTracks(r2, r3, r4, r5, r6, r8, r10, r11);	 Catch:{ Exception -> 0x014a, all -> 0x0148 }
                                            L_0x0122:
                                                r2 = r13;
                                                r13 = r15;
                                                r15 = 0;
                                                goto L_0x0891;
                                            L_0x0127:
                                                r0 = move-exception;
                                                r13 = r11;
                                            L_0x0129:
                                                r1 = r0;
                                                r2 = r13;
                                                goto L_0x0934;
                                            L_0x012d:
                                                r0 = move-exception;
                                                r35 = r9;
                                                r13 = r11;
                                            L_0x0131:
                                                r1 = r0;
                                                r6 = r13;
                                                r13 = r15;
                                                goto L_0x08ea;
                                            L_0x0136:
                                                r35 = r9;
                                                r13 = r11;
                                                r4 = r30;
                                                r5 = 0;
                                                r8 = r12.findTrack(r13, r5);	 Catch:{ Exception -> 0x08c3, all -> 0x08c0 }
                                                if (r4 == r1) goto L_0x014c;
                                            L_0x0142:
                                                r5 = 1;
                                                r9 = r12.findTrack(r13, r5);	 Catch:{ Exception -> 0x014a, all -> 0x0148 }
                                                goto L_0x014d;
                                            L_0x0148:
                                                r0 = move-exception;
                                                goto L_0x0129;
                                            L_0x014a:
                                                r0 = move-exception;
                                                goto L_0x0131;
                                            L_0x014c:
                                                r9 = r1;
                                            L_0x014d:
                                                if (r8 < 0) goto L_0x0122;
                                            L_0x014f:
                                                r5 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x085c, all -> 0x08c0 }
                                                r5 = r5.toLowerCase();	 Catch:{ Exception -> 0x085c, all -> 0x08c0 }
                                                r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x085c, all -> 0x08c0 }
                                                r19 = 2;
                                                r29 = 4;
                                                r1 = 18;
                                                if (r10 >= r1) goto L_0x020b;
                                            L_0x015f:
                                                r1 = "video/avc";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r1 = selectCodec(r1);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r10 = "video/avc";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r10 = selectColorFormat(r1, r10);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r10 != 0) goto L_0x0175;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x016d:
                                                r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r2 = "no supported color format";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r1.<init>(r2);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                throw r1;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x0175:
                                                r11 = r1.getName();	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = "OMX.qcom.";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r11.contains(r6);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r6 == 0) goto L_0x019d;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x0181:
                                                r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r11 = 16;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r6 != r11) goto L_0x019a;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x0187:
                                                r6 = "lge";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r5.equals(r6);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r6 != 0) goto L_0x0197;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x018f:
                                                r6 = "nokia";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r5.equals(r6);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r6 == 0) goto L_0x019a;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x0197:
                                                r6 = 1;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x0198:
                                                r11 = 1;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                goto L_0x01c9;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x019a:
                                                r6 = 1;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x019b:
                                                r11 = 0;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                goto L_0x01c9;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x019d:
                                                r6 = "OMX.Intel.";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r11.contains(r6);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r6 == 0) goto L_0x01a8;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01a5:
                                                r6 = r19;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                goto L_0x019b;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01a8:
                                                r6 = "OMX.MTK.VIDEO.ENCODER.AVC";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r11.equals(r6);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r6 == 0) goto L_0x01b2;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01b0:
                                                r6 = 3;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                goto L_0x019b;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01b2:
                                                r6 = "OMX.SEC.AVC.Encoder";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r11.equals(r6);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r6 == 0) goto L_0x01bd;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01ba:
                                                r6 = r29;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                goto L_0x0198;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01bd:
                                                r6 = "OMX.TI.DUCATI1.VIDEO.H264E";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r11.equals(r6);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r6 == 0) goto L_0x01c7;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01c5:
                                                r6 = 5;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                goto L_0x019b;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01c7:
                                                r6 = 0;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                goto L_0x019b;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01c9:
                                                r20 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r20 == 0) goto L_0x01fc;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x01cd:
                                                r38 = r6;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6.<init>();	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r39 = r10;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r10 = "codec = ";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6.append(r10);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r1 = r1.getName();	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6.append(r1);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r1 = " manufacturer = ";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6.append(r1);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6.append(r5);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r1 = "device = ";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6.append(r1);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r1 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6.append(r1);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r1 = r6.toString();	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                org.telegram.messenger.FileLog.m0d(r1);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                goto L_0x0200;
                                            L_0x01fc:
                                                r38 = r6;
                                                r39 = r10;
                                            L_0x0200:
                                                r6 = r38;
                                                r1 = r39;
                                                goto L_0x0210;
                                            L_0x0205:
                                                r0 = move-exception;
                                                r1 = r0;
                                                r2 = r13;
                                                r13 = r15;
                                                goto L_0x0860;
                                            L_0x020b:
                                                r1 = NUM; // 0x7f000789 float:1.701803E38 double:1.0527098025E-314;
                                                r6 = 0;
                                                r11 = 0;
                                            L_0x0210:
                                                r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x085c, all -> 0x08c0 }
                                                if (r10 == 0) goto L_0x022b;
                                            L_0x0214:
                                                r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r10.<init>();	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r40 = r11;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r11 = "colorFormat = ";	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r10.append(r11);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r10.append(r1);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r10 = r10.toString();	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                org.telegram.messenger.FileLog.m0d(r10);	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                goto L_0x022d;
                                            L_0x022b:
                                                r40 = r11;
                                            L_0x022d:
                                                r10 = r3 * r2;
                                                r11 = r10 * 3;
                                                r11 = r11 / 2;	 Catch:{ Exception -> 0x085c, all -> 0x08c0 }
                                                if (r6 != 0) goto L_0x024e;
                                            L_0x0235:
                                                r5 = r2 % 16;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                if (r5 == 0) goto L_0x024b;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                            L_0x0239:
                                                r5 = r2 % 16;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = 16;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r6 - r5;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r6 + r2;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r6 - r2;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r5 = r3 * r6;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r5 * 5;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r6 = r6 / 4;	 Catch:{ Exception -> 0x0205, all -> 0x0148 }
                                                r11 = r11 + r6;
                                                r41 = r15;
                                                r15 = r5;
                                                goto L_0x028d;
                                            L_0x024b:
                                                r41 = r15;
                                                goto L_0x028c;
                                            L_0x024e:
                                                r41 = r15;
                                                r15 = 1;
                                                if (r6 != r15) goto L_0x026e;
                                            L_0x0253:
                                                r5 = r5.toLowerCase();	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = "lge";	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r5 = r5.equals(r6);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                if (r5 != 0) goto L_0x028c;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x025f:
                                                r5 = r10 + 2047;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r5 = r5 & -2048;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r15 = r5 - r10;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r11 = r11 + r15;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                goto L_0x028d;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x0267:
                                                r0 = move-exception;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r1 = r0;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r2 = r13;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r13 = r41;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                goto L_0x0860;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x026e:
                                                r10 = 5;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                if (r6 != r10) goto L_0x0272;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x0271:
                                                goto L_0x028c;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x0272:
                                                r10 = 3;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                if (r6 != r10) goto L_0x028c;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x0275:
                                                r6 = "baidu";	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r5 = r5.equals(r6);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                if (r5 == 0) goto L_0x028c;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x027d:
                                                r5 = r2 % 16;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = 16;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = r6 - r5;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = r6 + r2;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = r6 - r2;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r15 = r3 * r6;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r5 = r15 * 5;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r5 = r5 / 4;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r11 = r11 + r5;
                                                goto L_0x028d;
                                            L_0x028c:
                                                r15 = 0;
                                            L_0x028d:
                                                r13.selectTrack(r8);	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r5 = r13.getTrackFormat(r8);	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                if (r9 < 0) goto L_0x02b5;
                                            L_0x0296:
                                                r13.selectTrack(r9);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r10 = r13.getTrackFormat(r9);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = "max-input-size";	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = r10.getInteger(r6);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = java.nio.ByteBuffer.allocateDirect(r6);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r42 = r6;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = 1;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r10 = r14.addTrack(r10, r6);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r44 = r10;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r43 = r11;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = r42;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                goto L_0x02ba;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x02b5:
                                                r43 = r11;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r6 = 0;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r44 = -5;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x02ba:
                                                r10 = 0;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r20 = (r27 > r10 ? 1 : (r27 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                if (r20 <= 0) goto L_0x02cb;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x02c0:
                                                r45 = r15;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r10 = r27;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r15 = 0;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r13.seekTo(r10, r15);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r46 = r10;
                                                goto L_0x02d3;
                                            L_0x02cb:
                                                r45 = r15;
                                                r46 = r27;
                                                r15 = 0;
                                                r13.seekTo(r10, r15);	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                            L_0x02d3:
                                                r10 = "video/avc";	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r10 = android.media.MediaFormat.createVideoFormat(r10, r3, r2);	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r11 = "color-format";	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r10.setInteger(r11, r1);	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r11 = "bitrate";	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                if (r4 <= 0) goto L_0x02e3;	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                            L_0x02e2:
                                                goto L_0x02e6;	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                            L_0x02e3:
                                                r4 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                            L_0x02e6:
                                                r10.setInteger(r11, r4);	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r4 = "frame-rate";	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r11 = 25;	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r10.setInteger(r4, r11);	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r4 = "i-frame-interval";	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r11 = 10;	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r10.setInteger(r4, r11);	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r11 = 18;
                                                if (r4 >= r11) goto L_0x0309;
                                            L_0x02fd:
                                                r4 = "stride";	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r11 = r3 + 32;	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r10.setInteger(r4, r11);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r4 = "slice-height";	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                                r10.setInteger(r4, r2);	 Catch:{ Exception -> 0x0267, all -> 0x0148 }
                                            L_0x0309:
                                                r4 = "video/avc";	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r4 = android.media.MediaCodec.createEncoderByType(r4);	 Catch:{ Exception -> 0x0857, all -> 0x08c0 }
                                                r11 = 1;
                                                r15 = 0;
                                                r4.configure(r10, r15, r15, r11);	 Catch:{ Exception -> 0x0850, all -> 0x08c0 }
                                                r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0850, all -> 0x08c0 }
                                                r11 = 18;
                                                if (r10 < r11) goto L_0x0335;
                                            L_0x031a:
                                                r10 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x032e, all -> 0x0148 }
                                                r11 = r4.createInputSurface();	 Catch:{ Exception -> 0x032e, all -> 0x0148 }
                                                r10.<init>(r11);	 Catch:{ Exception -> 0x032e, all -> 0x0148 }
                                                r10.makeCurrent();	 Catch:{ Exception -> 0x0327, all -> 0x0148 }
                                                goto L_0x0336;
                                            L_0x0327:
                                                r0 = move-exception;
                                                r1 = r0;
                                                r2 = r13;
                                                r13 = r41;
                                                goto L_0x0862;
                                            L_0x032e:
                                                r0 = move-exception;
                                                r1 = r0;
                                                r2 = r13;
                                                r13 = r41;
                                                goto L_0x0861;
                                            L_0x0335:
                                                r10 = 0;
                                            L_0x0336:
                                                r4.start();	 Catch:{ Exception -> 0x0847, all -> 0x08c0 }
                                                r11 = "mime";	 Catch:{ Exception -> 0x0847, all -> 0x08c0 }
                                                r11 = r5.getString(r11);	 Catch:{ Exception -> 0x0847, all -> 0x08c0 }
                                                r11 = android.media.MediaCodec.createDecoderByType(r11);	 Catch:{ Exception -> 0x0847, all -> 0x08c0 }
                                                r15 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x083e, all -> 0x08c0 }
                                                r48 = r1;
                                                r1 = 18;
                                                if (r15 < r1) goto L_0x0358;
                                            L_0x034b:
                                                r1 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0351, all -> 0x0148 }
                                                r1.<init>();	 Catch:{ Exception -> 0x0351, all -> 0x0148 }
                                                goto L_0x035f;
                                            L_0x0351:
                                                r0 = move-exception;
                                                r1 = r0;
                                                r2 = r13;
                                                r13 = r41;
                                                goto L_0x0863;
                                            L_0x0358:
                                                r1 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x083e, all -> 0x08c0 }
                                                r15 = r31;	 Catch:{ Exception -> 0x083e, all -> 0x08c0 }
                                                r1.<init>(r3, r2, r15);	 Catch:{ Exception -> 0x083e, all -> 0x08c0 }
                                            L_0x035f:
                                                r15 = r1.getSurface();	 Catch:{ Exception -> 0x0833, all -> 0x08c0 }
                                                r50 = r1;
                                                r49 = r10;
                                                r1 = 0;
                                                r10 = 0;
                                                r11.configure(r5, r15, r1, r10);	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r11.start();	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r10 = 21;
                                                if (r5 >= r10) goto L_0x0393;
                                            L_0x0375:
                                                r5 = r11.getInputBuffers();	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r15 = r4.getOutputBuffers();	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r10 = 18;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                if (r1 >= r10) goto L_0x0388;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x0383:
                                                r1 = r4.getInputBuffers();	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                goto L_0x0396;
                                            L_0x0388:
                                                r1 = 0;
                                                goto L_0x0396;
                                            L_0x038a:
                                                r0 = move-exception;
                                                r1 = r0;
                                                r2 = r13;
                                                r13 = r41;
                                            L_0x038f:
                                                r10 = r49;
                                                goto L_0x0865;
                                            L_0x0393:
                                                r1 = 0;
                                                r5 = 0;
                                                r15 = 0;
                                            L_0x0396:
                                                r70.checkConversionCanceled();	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r30 = r15;	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r10 = 0;	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r15 = 0;	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r27 = 0;	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r28 = -5;	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r36 = -1;	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                            L_0x03a3:
                                                if (r10 != 0) goto L_0x081a;	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                            L_0x03a5:
                                                r70.checkConversionCanceled();	 Catch:{ Exception -> 0x0827, all -> 0x08c0 }
                                                r52 = r1;
                                                r51 = r2;
                                                if (r15 != 0) goto L_0x04a2;
                                            L_0x03ae:
                                                r1 = r13.getSampleTrackIndex();	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                if (r1 != r8) goto L_0x0402;
                                            L_0x03b4:
                                                r53 = r3;
                                                r2 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
                                                r1 = r11.dequeueInputBuffer(r2);	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                if (r1 < 0) goto L_0x03f6;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x03be:
                                                r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r3 = 21;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                if (r2 >= r3) goto L_0x03c8;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x03c4:
                                                r2 = r5[r1];	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x03c6:
                                                r3 = 0;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                goto L_0x03cd;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x03c8:
                                                r2 = r11.getInputBuffer(r1);	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                goto L_0x03c6;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x03cd:
                                                r23 = r13.readSampleData(r2, r3);	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                if (r23 >= 0) goto L_0x03e4;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x03d3:
                                                r22 = 0;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r23 = 0;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r24 = 0;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r26 = 4;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r20 = r11;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r21 = r1;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r20.queueInputBuffer(r21, r22, r23, r24, r26);	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r15 = 1;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                goto L_0x03f6;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x03e4:
                                                r22 = 0;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r24 = r13.getSampleTime();	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r26 = 0;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r20 = r11;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r21 = r1;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r20.queueInputBuffer(r21, r22, r23, r24, r26);	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r13.advance();	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x03f6:
                                                r55 = r5;
                                                r2 = r13;
                                                r13 = r41;
                                                r54 = r44;
                                                r1 = 0;
                                                r3 = r71;
                                                goto L_0x047b;
                                            L_0x0402:
                                                r53 = r3;
                                                r2 = -1;
                                                if (r9 == r2) goto L_0x046c;
                                            L_0x0407:
                                                if (r1 != r9) goto L_0x046c;
                                            L_0x0409:
                                                r2 = 0;
                                                r1 = r13.readSampleData(r6, r2);	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                r7.size = r1;	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                r3 = 21;
                                                if (r1 >= r3) goto L_0x041e;
                                            L_0x0416:
                                                r6.position(r2);	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r1 = r7.size;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r6.limit(r1);	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                            L_0x041e:
                                                r1 = r7.size;	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                if (r1 < 0) goto L_0x042c;
                                            L_0x0422:
                                                r1 = r13.getSampleTime();	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r7.presentationTimeUs = r1;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r13.advance();	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                goto L_0x0430;
                                            L_0x042c:
                                                r1 = 0;
                                                r7.size = r1;	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                r15 = 1;	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                            L_0x0430:
                                                r1 = r7.size;	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                if (r1 <= 0) goto L_0x0462;
                                            L_0x0434:
                                                r1 = 0;
                                                r3 = (r17 > r1 ? 1 : (r17 == r1 ? 0 : -1));
                                                if (r3 < 0) goto L_0x0440;
                                            L_0x043a:
                                                r1 = r7.presentationTimeUs;	 Catch:{ Exception -> 0x038a, all -> 0x0148 }
                                                r3 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1));
                                                if (r3 >= 0) goto L_0x0462;
                                            L_0x0440:
                                                r1 = 0;
                                                r7.offset = r1;	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                r2 = r13.getSampleFlags();	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                r7.flags = r2;	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                r2 = r44;	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                r3 = r14.writeSampleData(r2, r6, r7, r1);	 Catch:{ Exception -> 0x049b, all -> 0x08c0 }
                                                if (r3 == 0) goto L_0x045c;
                                            L_0x0451:
                                                r54 = r2;
                                                r2 = r13;
                                                r13 = r41;
                                                r3 = r71;
                                                r12.didWriteData(r3, r13, r1, r1);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x0469;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x045c:
                                                r54 = r2;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r2 = r13;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r13 = r41;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x0467;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0462:
                                                r2 = r13;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r13 = r41;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r54 = r44;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0467:
                                                r3 = r71;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0469:
                                                r55 = r5;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x047a;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x046c:
                                                r2 = r13;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r13 = r41;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r54 = r44;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3 = r71;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r55 = r5;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5 = -1;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r1 != r5) goto L_0x047a;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0478:
                                                r1 = 1;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x047b;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x047a:
                                                r1 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x047b:
                                                if (r1 == 0) goto L_0x0498;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x047d:
                                                r56 = r6;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r21 = r11.dequeueInputBuffer(r5);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r21 < 0) goto L_0x04af;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0487:
                                                r22 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r23 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r24 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r26 = 4;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r20 = r11;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r20.queueInputBuffer(r21, r22, r23, r24, r26);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r15 = 1;
                                                goto L_0x04af;
                                            L_0x0496:
                                                r0 = move-exception;
                                                goto L_0x049f;
                                            L_0x0498:
                                                r56 = r6;
                                                goto L_0x04af;
                                            L_0x049b:
                                                r0 = move-exception;
                                                r2 = r13;
                                                r13 = r41;
                                            L_0x049f:
                                                r1 = r0;
                                                goto L_0x038f;
                                            L_0x04a2:
                                                r53 = r3;
                                                r55 = r5;
                                                r56 = r6;
                                                r2 = r13;
                                                r13 = r41;
                                                r54 = r44;
                                                r3 = r71;
                                            L_0x04af:
                                                r1 = r27 ^ 1;
                                                r5 = r15;
                                                r6 = r28;
                                                r15 = r1;
                                                r1 = 1;
                                            L_0x04b6:
                                                if (r15 != 0) goto L_0x04cf;
                                            L_0x04b8:
                                                if (r1 == 0) goto L_0x04bb;
                                            L_0x04ba:
                                                goto L_0x04cf;
                                            L_0x04bb:
                                                r15 = r5;
                                                r28 = r6;
                                                r41 = r13;
                                                r1 = r52;
                                                r3 = r53;
                                                r44 = r54;
                                                r5 = r55;
                                                r6 = r56;
                                                r13 = r2;
                                                r2 = r51;
                                                goto L_0x03a3;
                                            L_0x04cf:
                                                r70.checkConversionCanceled();	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r59 = r1;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r57 = r9;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r58 = r10;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r9 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r1 = r4.dequeueOutputBuffer(r7, r9);	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r9 = -1;
                                                if (r1 != r9) goto L_0x04f0;
                                            L_0x04e1:
                                                r60 = r5;
                                                r3 = r9;
                                                r61 = r15;
                                                r15 = r51;
                                                r9 = r53;
                                                r10 = r58;
                                                r59 = 0;
                                                goto L_0x0610;
                                            L_0x04f0:
                                                r9 = -3;
                                                if (r1 != r9) goto L_0x050a;
                                            L_0x04f3:
                                                r9 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = 21;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r9 >= r10) goto L_0x04fd;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x04f9:
                                                r30 = r4.getOutputBuffers();	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x04fd:
                                                r60 = r5;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r61 = r15;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r15 = r51;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r9 = r53;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = r58;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0507:
                                                r3 = -1;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x0610;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x050a:
                                                r9 = -2;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r1 != r9) goto L_0x051a;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x050d:
                                                r9 = r4.getOutputFormat();	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = -5;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r6 != r10) goto L_0x04fd;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0514:
                                                r10 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6 = r14.addTrack(r9, r10);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x04fd;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x051a:
                                                if (r1 >= 0) goto L_0x0533;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x051c:
                                                r5 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6.<init>();	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r7 = "unexpected result from encoder.dequeueOutputBuffer: ";	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6.append(r7);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6.append(r1);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r1 = r6.toString();	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5.<init>(r1);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                throw r5;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0533:
                                                r9 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r10 = 21;
                                                if (r9 >= r10) goto L_0x053c;
                                            L_0x0539:
                                                r9 = r30[r1];	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x0540;
                                            L_0x053c:
                                                r9 = r4.getOutputBuffer(r1);	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                            L_0x0540:
                                                if (r9 != 0) goto L_0x055e;
                                            L_0x0542:
                                                r5 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6.<init>();	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r7 = "encoderOutputBuffer ";	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6.append(r7);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6.append(r1);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r1 = " was null";	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6.append(r1);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r1 = r6.toString();	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5.<init>(r1);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                throw r5;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x055e:
                                                r10 = r7.size;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r60 = r5;
                                                r5 = 1;
                                                if (r10 <= r5) goto L_0x05fa;
                                            L_0x0565:
                                                r10 = r7.flags;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = r10 & 2;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r10 != 0) goto L_0x0577;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x056b:
                                                r9 = r14.writeSampleData(r6, r9, r7, r5);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r9 == 0) goto L_0x05fa;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0571:
                                                r5 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r12.didWriteData(r3, r13, r5, r5);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x05fa;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0577:
                                                r5 = -5;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r6 != r5) goto L_0x05fa;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x057a:
                                                r6 = r7.size;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6 = new byte[r6];	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = r7.offset;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5 = r7.size;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = r10 + r5;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r9.limit(r10);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5 = r7.offset;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r9.position(r5);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r9.get(r6);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5 = r7.size;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r9 = 1;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5 = r5 - r9;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0592:
                                                if (r5 < 0) goto L_0x05d7;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0594:
                                                r10 = 3;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r5 <= r10) goto L_0x05d7;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x0597:
                                                r10 = r6[r5];	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r10 != r9) goto L_0x05cd;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x059b:
                                                r9 = r5 + -1;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r9 = r6[r9];	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r9 != 0) goto L_0x05cd;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x05a1:
                                                r9 = r5 + -2;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r9 = r6[r9];	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r9 != 0) goto L_0x05cd;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x05a7:
                                                r9 = r5 + -3;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = r6[r9];	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r10 != 0) goto L_0x05cd;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x05ad:
                                                r5 = java.nio.ByteBuffer.allocate(r9);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = r7.size;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = r10 - r9;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = java.nio.ByteBuffer.allocate(r10);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r61 = r15;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r15 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3 = r5.put(r6, r15, r9);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3.position(r15);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3 = r7.size;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3 = r3 - r9;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3 = r10.put(r6, r9, r3);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3.position(r15);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x05db;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x05cd:
                                                r61 = r15;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5 = r5 + -1;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r15 = r61;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3 = r71;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r9 = 1;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                goto L_0x0592;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x05d7:
                                                r61 = r15;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r10 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x05db:
                                                r3 = "video/avc";	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r15 = r51;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r9 = r53;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3 = android.media.MediaFormat.createVideoFormat(r3, r9, r15);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                if (r5 == 0) goto L_0x05f3;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x05e7:
                                                if (r10 == 0) goto L_0x05f3;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x05e9:
                                                r6 = "csd-0";	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3.setByteBuffer(r6, r5);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r5 = "csd-1";	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3.setByteBuffer(r5, r10);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                            L_0x05f3:
                                                r5 = 0;	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r3 = r14.addTrack(r3, r5);	 Catch:{ Exception -> 0x0496, all -> 0x088d }
                                                r6 = r3;
                                                goto L_0x0600;
                                            L_0x05fa:
                                                r61 = r15;
                                                r15 = r51;
                                                r9 = r53;
                                            L_0x0600:
                                                r3 = r7.flags;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r3 = r3 & 4;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                if (r3 == 0) goto L_0x0609;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                            L_0x0606:
                                                r3 = 0;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r10 = 1;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                goto L_0x060b;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                            L_0x0609:
                                                r3 = 0;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                r10 = 0;	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                            L_0x060b:
                                                r4.releaseOutputBuffer(r1, r3);	 Catch:{ Exception -> 0x0817, all -> 0x088d }
                                                goto L_0x0507;
                                            L_0x0610:
                                                if (r1 == r3) goto L_0x0622;
                                            L_0x0612:
                                                r53 = r9;
                                                r51 = r15;
                                                r9 = r57;
                                                r1 = r59;
                                                r5 = r60;
                                                r15 = r61;
                                            L_0x061e:
                                                r3 = r71;
                                                goto L_0x04b6;
                                            L_0x0622:
                                                if (r27 != 0) goto L_0x07f7;
                                            L_0x0624:
                                                r62 = r4;
                                                r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
                                                r1 = r11.dequeueOutputBuffer(r7, r3);	 Catch:{ Exception -> 0x07ee, all -> 0x088d }
                                                r3 = -1;
                                                if (r1 != r3) goto L_0x063f;
                                            L_0x062f:
                                                r68 = r6;
                                                r65 = r46;
                                                r67 = r49;
                                                r1 = r50;
                                                r5 = r60;
                                                r3 = r62;
                                            L_0x063b:
                                                r61 = 0;
                                                goto L_0x0802;
                                            L_0x063f:
                                                r4 = -3;
                                                if (r1 != r4) goto L_0x064e;
                                            L_0x0642:
                                                r68 = r6;
                                                r65 = r46;
                                                r67 = r49;
                                                r1 = r50;
                                                r3 = r62;
                                                goto L_0x0800;
                                            L_0x064e:
                                                r4 = -2;
                                                if (r1 != r4) goto L_0x0676;
                                            L_0x0651:
                                                r1 = r11.getOutputFormat();	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                if (r4 == 0) goto L_0x0642;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x0659:
                                                r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4.<init>();	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r5 = "newFormat = ";	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4.append(r5);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4.append(r1);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r1 = r4.toString();	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                org.telegram.messenger.FileLog.m0d(r1);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                goto L_0x0642;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x066e:
                                                r0 = move-exception;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r1 = r0;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r10 = r49;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4 = r62;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                goto L_0x0865;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x0676:
                                                if (r1 >= 0) goto L_0x068f;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x0678:
                                                r3 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4.<init>();	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r5 = "unexpected result from decoder.dequeueOutputBuffer: ";	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4.append(r5);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4.append(r1);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r1 = r4.toString();	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3.<init>(r1);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                throw r3;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x068f:
                                                r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x07ee, all -> 0x088d }
                                                r5 = 18;
                                                if (r4 < r5) goto L_0x069f;
                                            L_0x0695:
                                                r4 = r7.size;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                if (r4 == 0) goto L_0x069b;
                                            L_0x0699:
                                                r4 = 1;
                                                goto L_0x069c;
                                            L_0x069b:
                                                r4 = 0;
                                            L_0x069c:
                                                r20 = 0;
                                                goto L_0x06b1;
                                            L_0x069f:
                                                r4 = r7.size;	 Catch:{ Exception -> 0x07ee, all -> 0x088d }
                                                if (r4 != 0) goto L_0x06ae;
                                            L_0x06a3:
                                                r4 = r7.presentationTimeUs;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r20 = 0;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r22 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1));	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                if (r22 == 0) goto L_0x06ac;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06ab:
                                                goto L_0x06b0;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06ac:
                                                r4 = 0;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                goto L_0x06b1;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06ae:
                                                r20 = 0;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06b0:
                                                r4 = 1;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06b1:
                                                r5 = (r17 > r20 ? 1 : (r17 == r20 ? 0 : -1));	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                if (r5 <= 0) goto L_0x06cb;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06b5:
                                                r63 = r4;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3 = r7.presentationTimeUs;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r5 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1));	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                if (r5 < 0) goto L_0x06cd;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06bd:
                                                r3 = r7.flags;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3 = r3 | 4;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r7.flags = r3;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3 = 0;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r5 = 1;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r27 = 1;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r63 = 0;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                goto L_0x06d1;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06cb:
                                                r63 = r4;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06cd:
                                                r5 = r60;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3 = 0;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06d1:
                                                r20 = (r46 > r3 ? 1 : (r46 == r3 ? 0 : -1));	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                if (r20 <= 0) goto L_0x0719;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06d5:
                                                r20 = -1;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r22 = (r36 > r20 ? 1 : (r36 == r20 ? 0 : -1));	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                if (r22 != 0) goto L_0x0719;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06db:
                                                r3 = r7.presentationTimeUs;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r20 = (r3 > r46 ? 1 : (r3 == r46 ? 0 : -1));	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                if (r20 >= 0) goto L_0x0710;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06e1:
                                                r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                if (r3 == 0) goto L_0x070a;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x06e5:
                                                r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3.<init>();	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4 = "drop frame startTime = ";	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3.append(r4);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r64 = r5;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4 = r46;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3.append(r4);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r65 = r4;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4 = " present time = ";	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3.append(r4);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r4 = r7.presentationTimeUs;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3.append(r4);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3 = r3.toString();	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                org.telegram.messenger.FileLog.m0d(r3);	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                goto L_0x070e;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x070a:
                                                r64 = r5;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r65 = r46;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x070e:
                                                r3 = 0;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                goto L_0x071f;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                            L_0x0710:
                                                r64 = r5;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r65 = r46;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r3 = r7.presentationTimeUs;	 Catch:{ Exception -> 0x066e, all -> 0x088d }
                                                r36 = r3;
                                                goto L_0x071d;
                                            L_0x0719:
                                                r64 = r5;
                                                r65 = r46;
                                            L_0x071d:
                                                r3 = r63;
                                            L_0x071f:
                                                r11.releaseOutputBuffer(r1, r3);	 Catch:{ Exception -> 0x07ee, all -> 0x088d }
                                                if (r3 == 0) goto L_0x07a7;
                                            L_0x0724:
                                                r1 = r50;
                                                r1.awaitNewImage();	 Catch:{ Exception -> 0x072b, all -> 0x088d }
                                                r3 = 0;
                                                goto L_0x0730;
                                            L_0x072b:
                                                r0 = move-exception;
                                                org.telegram.messenger.FileLog.m3e(r0);	 Catch:{ Exception -> 0x07a1, all -> 0x088d }
                                                r3 = 1;	 Catch:{ Exception -> 0x07a1, all -> 0x088d }
                                            L_0x0730:
                                                if (r3 != 0) goto L_0x079e;	 Catch:{ Exception -> 0x07a1, all -> 0x088d }
                                            L_0x0732:
                                                r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x07a1, all -> 0x088d }
                                                r4 = 18;
                                                if (r3 < r4) goto L_0x075a;
                                            L_0x0738:
                                                r3 = 0;
                                                r1.drawImage(r3);	 Catch:{ Exception -> 0x0750, all -> 0x088d }
                                                r3 = r7.presentationTimeUs;	 Catch:{ Exception -> 0x0750, all -> 0x088d }
                                                r20 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
                                                r3 = r3 * r20;
                                                r5 = r49;
                                                r5.setPresentationTime(r3);	 Catch:{ Exception -> 0x074e, all -> 0x088d }
                                                r5.swapBuffers();	 Catch:{ Exception -> 0x074e, all -> 0x088d }
                                                r67 = r5;
                                                goto L_0x07ab;
                                            L_0x074e:
                                                r0 = move-exception;
                                                goto L_0x0753;
                                            L_0x0750:
                                                r0 = move-exception;
                                                r5 = r49;
                                            L_0x0753:
                                                r50 = r1;
                                                r10 = r5;
                                                r4 = r62;
                                                goto L_0x083c;
                                            L_0x075a:
                                                r67 = r49;
                                                r3 = r62;
                                                r4 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
                                                r28 = r3.dequeueInputBuffer(r4);	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                if (r28 < 0) goto L_0x0794;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x0766:
                                                r4 = 1;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r1.drawImage(r4);	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r20 = r1.getFrame();	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r4 = r52[r28];	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r4.clear();	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r21 = r4;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r22 = r48;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r23 = r9;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r24 = r15;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r25 = r45;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r26 = r40;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                org.telegram.messenger.Utilities.convertVideoFrame(r20, r21, r22, r23, r24, r25, r26);	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r22 = 0;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r4 = r7.presentationTimeUs;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r26 = 0;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r20 = r3;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r21 = r28;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r23 = r43;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r24 = r4;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r20.queueInputBuffer(r21, r22, r23, r24, r26);	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                goto L_0x07ad;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x0794:
                                                r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                if (r4 == 0) goto L_0x07ad;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x0798:
                                                r4 = "input buffer not available";	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                org.telegram.messenger.FileLog.m0d(r4);	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                goto L_0x07ad;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x079e:
                                                r67 = r49;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                goto L_0x07ab;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07a1:
                                                r0 = move-exception;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r67 = r49;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r3 = r62;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                goto L_0x07eb;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07a7:
                                                r67 = r49;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r1 = r50;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07ab:
                                                r3 = r62;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07ad:
                                                r4 = r7.flags;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r4 = r4 & 4;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                if (r4 == 0) goto L_0x07e5;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07b3:
                                                r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                if (r4 == 0) goto L_0x07bc;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07b7:
                                                r4 = "decoder stream end";	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                org.telegram.messenger.FileLog.m0d(r4);	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07bc:
                                                r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r5 = 18;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                if (r4 < r5) goto L_0x07c8;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07c2:
                                                r3.signalEndOfInputStream();	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r68 = r6;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                goto L_0x07e1;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07c8:
                                                r68 = r6;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r21 = r3.dequeueInputBuffer(r5);	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                if (r21 < 0) goto L_0x07e1;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07d2:
                                                r22 = 0;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r23 = 1;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r5 = r7.presentationTimeUs;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r26 = 4;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r20 = r3;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r24 = r5;	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                                r20.queueInputBuffer(r21, r22, r23, r24, r26);	 Catch:{ Exception -> 0x07ea, all -> 0x088d }
                                            L_0x07e1:
                                                r5 = r64;
                                                goto L_0x063b;
                                            L_0x07e5:
                                                r68 = r6;
                                                r5 = r64;
                                                goto L_0x0802;
                                            L_0x07ea:
                                                r0 = move-exception;
                                            L_0x07eb:
                                                r50 = r1;
                                                goto L_0x07f5;
                                            L_0x07ee:
                                                r0 = move-exception;
                                                r67 = r49;
                                                r1 = r50;
                                                r3 = r62;
                                            L_0x07f5:
                                                r4 = r3;
                                                goto L_0x0830;
                                            L_0x07f7:
                                                r3 = r4;
                                                r68 = r6;
                                                r65 = r46;
                                                r67 = r49;
                                                r1 = r50;
                                            L_0x0800:
                                                r5 = r60;
                                            L_0x0802:
                                                r50 = r1;
                                                r4 = r3;
                                                r53 = r9;
                                                r51 = r15;
                                                r9 = r57;
                                                r1 = r59;
                                                r15 = r61;
                                                r46 = r65;
                                                r49 = r67;
                                                r6 = r68;
                                                goto L_0x061e;
                                            L_0x0817:
                                                r0 = move-exception;
                                                r3 = r4;
                                                goto L_0x082c;
                                            L_0x081a:
                                                r3 = r4;
                                                r2 = r13;
                                                r13 = r41;
                                                r67 = r49;
                                                r1 = r50;
                                                r10 = r67;
                                                r15 = 0;
                                                goto L_0x086c;
                                            L_0x0827:
                                                r0 = move-exception;
                                                r3 = r4;
                                                r2 = r13;
                                                r13 = r41;
                                            L_0x082c:
                                                r67 = r49;
                                                r1 = r50;
                                            L_0x0830:
                                                r10 = r67;
                                                goto L_0x083c;
                                            L_0x0833:
                                                r0 = move-exception;
                                                r3 = r4;
                                                r67 = r10;
                                                r2 = r13;
                                                r13 = r41;
                                                r50 = r1;
                                            L_0x083c:
                                                r1 = r0;
                                                goto L_0x0865;
                                            L_0x083e:
                                                r0 = move-exception;
                                                r3 = r4;
                                                r67 = r10;
                                                r2 = r13;
                                                r13 = r41;
                                                r1 = r0;
                                                goto L_0x0863;
                                            L_0x0847:
                                                r0 = move-exception;
                                                r3 = r4;
                                                r67 = r10;
                                                r2 = r13;
                                                r13 = r41;
                                                r1 = r0;
                                                goto L_0x0862;
                                            L_0x0850:
                                                r0 = move-exception;
                                                r3 = r4;
                                                r2 = r13;
                                                r13 = r41;
                                                r1 = r0;
                                                goto L_0x0861;
                                            L_0x0857:
                                                r0 = move-exception;
                                                r2 = r13;
                                                r13 = r41;
                                                goto L_0x085f;
                                            L_0x085c:
                                                r0 = move-exception;
                                                r2 = r13;
                                                r13 = r15;
                                            L_0x085f:
                                                r1 = r0;
                                            L_0x0860:
                                                r4 = 0;
                                            L_0x0861:
                                                r10 = 0;
                                            L_0x0862:
                                                r11 = 0;
                                            L_0x0863:
                                                r50 = 0;
                                            L_0x0865:
                                                org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                                r3 = r4;	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                                r1 = r50;	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                                r15 = 1;	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                            L_0x086c:
                                                r2.unselectTrack(r8);	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                                if (r1 == 0) goto L_0x0874;	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                            L_0x0871:
                                                r1.release();	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                            L_0x0874:
                                                if (r10 == 0) goto L_0x0879;	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                            L_0x0876:
                                                r10.release();	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                            L_0x0879:
                                                if (r11 == 0) goto L_0x0881;	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                            L_0x087b:
                                                r11.stop();	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                                r11.release();	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                            L_0x0881:
                                                if (r3 == 0) goto L_0x0889;	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                            L_0x0883:
                                                r3.stop();	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                                r3.release();	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                            L_0x0889:
                                                r70.checkConversionCanceled();	 Catch:{ Exception -> 0x088f, all -> 0x088d }
                                                goto L_0x0891;
                                            L_0x088d:
                                                r0 = move-exception;
                                                goto L_0x08c8;
                                            L_0x088f:
                                                r0 = move-exception;
                                                goto L_0x08d0;
                                            L_0x0891:
                                                if (r2 == 0) goto L_0x0896;
                                            L_0x0893:
                                                r2.release();
                                            L_0x0896:
                                                if (r14 == 0) goto L_0x08a0;
                                            L_0x0898:
                                                r14.finishMovie();	 Catch:{ Exception -> 0x089c }
                                                goto L_0x08a0;
                                            L_0x089c:
                                                r0 = move-exception;
                                                org.telegram.messenger.FileLog.m3e(r0);
                                            L_0x08a0:
                                                r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
                                                if (r1 == 0) goto L_0x091b;
                                            L_0x08a4:
                                                r1 = new java.lang.StringBuilder;
                                                r1.<init>();
                                                r2 = "time = ";
                                                r1.append(r2);
                                                r2 = java.lang.System.currentTimeMillis();
                                                r4 = r2 - r33;
                                                r1.append(r4);
                                                r1 = r1.toString();
                                                org.telegram.messenger.FileLog.m0d(r1);
                                                goto L_0x091b;
                                            L_0x08c0:
                                                r0 = move-exception;
                                                r2 = r13;
                                                goto L_0x08c8;
                                            L_0x08c3:
                                                r0 = move-exception;
                                                r2 = r13;
                                                goto L_0x08cf;
                                            L_0x08c6:
                                                r0 = move-exception;
                                                r2 = r11;
                                            L_0x08c8:
                                                r1 = r0;
                                                goto L_0x0934;
                                            L_0x08cb:
                                                r0 = move-exception;
                                                r35 = r9;
                                                r2 = r11;
                                            L_0x08cf:
                                                r13 = r15;
                                            L_0x08d0:
                                                r1 = r0;
                                                r6 = r2;
                                                goto L_0x08ea;
                                            L_0x08d3:
                                                r0 = move-exception;
                                                r1 = r0;
                                                r2 = 0;
                                                goto L_0x0934;
                                            L_0x08d7:
                                                r0 = move-exception;
                                                r35 = r9;
                                                r13 = r15;
                                                r1 = r0;
                                                r6 = 0;
                                                goto L_0x08ea;
                                            L_0x08de:
                                                r0 = move-exception;
                                                r1 = r0;
                                                r2 = 0;
                                                r14 = 0;
                                                goto L_0x0934;
                                            L_0x08e3:
                                                r0 = move-exception;
                                                r35 = r9;
                                                r13 = r15;
                                                r1 = r0;
                                                r6 = 0;
                                                r14 = 0;
                                            L_0x08ea:
                                                org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ all -> 0x0931 }
                                                if (r6 == 0) goto L_0x08f2;
                                            L_0x08ef:
                                                r6.release();
                                            L_0x08f2:
                                                if (r14 == 0) goto L_0x08fc;
                                            L_0x08f4:
                                                r14.finishMovie();	 Catch:{ Exception -> 0x08f8 }
                                                goto L_0x08fc;
                                            L_0x08f8:
                                                r0 = move-exception;
                                                org.telegram.messenger.FileLog.m3e(r0);
                                            L_0x08fc:
                                                r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
                                                if (r1 == 0) goto L_0x091a;
                                            L_0x0900:
                                                r1 = new java.lang.StringBuilder;
                                                r1.<init>();
                                                r2 = "time = ";
                                                r1.append(r2);
                                                r2 = java.lang.System.currentTimeMillis();
                                                r4 = r2 - r33;
                                                r1.append(r4);
                                                r1 = r1.toString();
                                                org.telegram.messenger.FileLog.m0d(r1);
                                            L_0x091a:
                                                r15 = 1;
                                            L_0x091b:
                                                r1 = r35;
                                                r1 = r1.edit();
                                                r2 = "isPreviousOk";
                                                r3 = 1;
                                                r1 = r1.putBoolean(r2, r3);
                                                r1.commit();
                                                r2 = r71;
                                                r12.didWriteData(r2, r13, r3, r15);
                                                return r3;
                                            L_0x0931:
                                                r0 = move-exception;
                                                r1 = r0;
                                                r2 = r6;
                                            L_0x0934:
                                                if (r2 == 0) goto L_0x0939;
                                            L_0x0936:
                                                r2.release();
                                            L_0x0939:
                                                if (r14 == 0) goto L_0x0943;
                                            L_0x093b:
                                                r14.finishMovie();	 Catch:{ Exception -> 0x093f }
                                                goto L_0x0943;
                                            L_0x093f:
                                                r0 = move-exception;
                                                org.telegram.messenger.FileLog.m3e(r0);
                                            L_0x0943:
                                                r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
                                                if (r2 == 0) goto L_0x0961;
                                            L_0x0947:
                                                r2 = new java.lang.StringBuilder;
                                                r2.<init>();
                                                r3 = "time = ";
                                                r2.append(r3);
                                                r3 = java.lang.System.currentTimeMillis();
                                                r5 = r3 - r33;
                                                r2.append(r5);
                                                r2 = r2.toString();
                                                org.telegram.messenger.FileLog.m0d(r2);
                                            L_0x0961:
                                                throw r1;
                                            L_0x0962:
                                                r1 = r9;
                                                r2 = r13;
                                                r13 = r15;
                                                r1 = r1.edit();
                                                r3 = "isPreviousOk";
                                                r4 = 1;
                                                r1 = r1.putBoolean(r3, r4);
                                                r1.commit();
                                                r12.didWriteData(r2, r13, r4, r4);
                                                r1 = 0;
                                                return r1;
                                                */
                                                throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MessageObject):boolean");
                                            }
                                        }
