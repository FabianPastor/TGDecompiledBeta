package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
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
import com.google.android.exoplayer2.C0016C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.p004ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.video.InputSurface;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.messenger.video.OutputSurface;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ChatActivity;
import org.telegram.p005ui.Components.EmbedBottomSheet;
import org.telegram.p005ui.Components.PhotoFilterView.CurvesToolValue;
import org.telegram.p005ui.Components.PipRoundVideoView;
import org.telegram.p005ui.Components.Point;
import org.telegram.p005ui.Components.VideoPlayer;
import org.telegram.p005ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.telegram.p005ui.PhotoViewer;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;

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
    private static Runnable refreshGalleryRunnable;
    private Sensor accelerometerSensor;
    private boolean accelerometerVertical;
    private boolean allowStartRecord;
    private int audioFocus = 0;
    private AudioInfo audioInfo;
    private VideoPlayer audioPlayer = null;
    private AudioRecord audioRecorder;
    private Activity baseActivity;
    private boolean callInProgress;
    private boolean cancelCurrentVideoConversion = false;
    private int countLess;
    private AspectRatioFrameLayout currentAspectRatioFrameLayout;
    private float currentAspectRatioFrameLayoutRatio;
    private boolean currentAspectRatioFrameLayoutReady;
    private int currentAspectRatioFrameLayoutRotation;
    private float currentPlaybackSpeed = VOLUME_NORMAL;
    private int currentPlaylistNum;
    private TextureView currentTextureView;
    private FrameLayout currentTextureViewContainer;
    private boolean downloadingCurrentMessage;
    private ExternalObserver externalObserver;
    private View feedbackView;
    private ByteBuffer fileBuffer;
    private DispatchQueue fileEncodingQueue;
    private BaseFragment flagSecureFragment;
    private boolean forceLoopCurrentPlaylist;
    private HashMap<String, MessageObject> generatingWaveform = new HashMap();
    private float[] gravity = new float[3];
    private float[] gravityFast = new float[3];
    private Sensor gravitySensor;
    private int hasAudioFocus;
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
    private long lastProgress = 0;
    private float lastProximityValue = -100.0f;
    private EncryptedChat lastSecretChat;
    private long lastTimestamp = 0;
    private User lastUser;
    private float[] linearAcceleration = new float[3];
    private Sensor linearSensor;
    private String[] mediaProjections;
    private PipRoundVideoView pipRoundVideoView;
    private int pipSwitchingState;
    private boolean playMusicAgain;
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
    private Runnable recordRunnable = new C04691();
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
    private boolean videoConvertFirstWrite = true;
    private ArrayList<MessageObject> videoConvertQueue = new ArrayList();
    private final Object videoConvertSync = new Object();
    private VideoPlayer videoPlayer;
    private final Object videoQueueSync = new Object();
    private ArrayList<MessageObject> voiceMessagesPlaylist;
    private SparseArray<MessageObject> voiceMessagesPlaylistMap;
    private boolean voiceMessagesPlaylistUnread;

    /* renamed from: org.telegram.messenger.MediaController$10 */
    class C045410 implements Runnable {
        C045410() {
        }

        public void run() {
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
    }

    /* renamed from: org.telegram.messenger.MediaController$11 */
    class C045511 implements Runnable {
        C045511() {
        }

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
    }

    /* renamed from: org.telegram.messenger.MediaController$12 */
    class C045612 implements Runnable {
        C045612() {
        }

        public void run() {
            MediaController.this.cleanupPlayer(true, true);
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$14 */
    class C045814 implements Runnable {
        C045814() {
        }

        public void run() {
            MediaController.this.cleanupPlayer(true, true);
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$1 */
    class C04691 implements Runnable {
        C04691() {
        }

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
                    double sum = 0.0d;
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
                                sum += (double) (peak * peak);
                            }
                            if (i == ((int) nextNum) && currentNum2 < MediaController.this.recordSamples.length) {
                                MediaController.this.recordSamples[currentNum2] = peak;
                                nextNum += sampleStep;
                                currentNum2++;
                            }
                        }
                        MediaController.this.samplesCount = newSamplesCount;
                    } catch (Throwable e) {
                        FileLog.m14e(e);
                    }
                    buffer.position(0);
                    final double amplitude = Math.sqrt((sum / ((double) len)) / 2.0d);
                    final ByteBuffer finalBuffer = buffer;
                    final boolean flush = len != buffer.capacity();
                    if (len != 0) {
                        MediaController.this.fileEncodingQueue.postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.MediaController$1$1$1 */
                            class C04511 implements Runnable {
                                C04511() {
                                }

                                public void run() {
                                    MediaController.this.recordBuffers.add(finalBuffer);
                                }
                            }

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
                                MediaController.this.recordQueue.postRunnable(new C04511());
                            }
                        });
                    }
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(amplitude));
                        }
                    });
                    return;
                }
                MediaController.this.recordBuffers.add(buffer);
                MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$3 */
    class C04813 implements Runnable {
        C04813() {
        }

        public void run() {
            try {
                MediaController.this.recordBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
                if (MediaController.this.recordBufferSize <= 0) {
                    MediaController.this.recordBufferSize = 1280;
                }
                for (int a = 0; a < 5; a++) {
                    ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                    buffer.order(ByteOrder.nativeOrder());
                    MediaController.this.recordBuffers.add(buffer);
                }
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$4 */
    class C04844 implements Runnable {

        /* renamed from: org.telegram.messenger.MediaController$4$1 */
        class C04831 extends PhoneStateListener {
            C04831() {
            }

            public void onCallStateChanged(final int state, String incomingNumber) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        EmbedBottomSheet embedBottomSheet;
                        if (state == 1) {
                            if (MediaController.this.isPlayingMessage(MediaController.this.playingMessageObject) && !MediaController.this.isMessagePaused()) {
                                MediaController.this.pauseMessage(MediaController.this.playingMessageObject);
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
        }

        C04844() {
        }

        public void run() {
            try {
                MediaController.this.currentPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("playbackSpeed", MediaController.VOLUME_NORMAL);
                MediaController.this.sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
                MediaController.this.linearSensor = MediaController.this.sensorManager.getDefaultSensor(10);
                MediaController.this.gravitySensor = MediaController.this.sensorManager.getDefaultSensor(9);
                if (MediaController.this.linearSensor == null || MediaController.this.gravitySensor == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m11d("gravity or linear sensor not found");
                    }
                    MediaController.this.accelerometerSensor = MediaController.this.sensorManager.getDefaultSensor(1);
                    MediaController.this.linearSensor = null;
                    MediaController.this.gravitySensor = null;
                }
                MediaController.this.proximitySensor = MediaController.this.sensorManager.getDefaultSensor(8);
                MediaController.this.proximityWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
            try {
                PhoneStateListener phoneStateListener = new C04831();
                TelephonyManager mgr = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (mgr != null) {
                    mgr.listen(phoneStateListener, 32);
                }
            } catch (Throwable e2) {
                FileLog.m14e(e2);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$5 */
    class C04855 implements Runnable {
        C04855() {
        }

        public void run() {
            for (int a = 0; a < 3; a++) {
                NotificationCenter.getInstance(a).addObserver(MediaController.this, NotificationCenter.FileDidLoaded);
                NotificationCenter.getInstance(a).addObserver(MediaController.this, NotificationCenter.httpFileDidLoaded);
                NotificationCenter.getInstance(a).addObserver(MediaController.this, NotificationCenter.didReceivedNewMessages);
                NotificationCenter.getInstance(a).addObserver(MediaController.this, NotificationCenter.messagesDeleted);
                NotificationCenter.getInstance(a).addObserver(MediaController.this, NotificationCenter.removeAllMessagesFromDialog);
                NotificationCenter.getInstance(a).addObserver(MediaController.this, NotificationCenter.musicDidLoaded);
                NotificationCenter.getGlobalInstance().addObserver(MediaController.this, NotificationCenter.playerDidStartPlaying);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$7 */
    class C04887 implements Runnable {
        C04887() {
        }

        public void run() {
            try {
                if (MediaController.this.smsObserver != null) {
                    ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.smsObserver);
                    MediaController.this.smsObserver = null;
                }
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
        }
    }

    public static class AlbumEntry {
        public int bucketId;
        public String bucketName;
        public PhotoEntry coverPhoto;
        public ArrayList<PhotoEntry> photos = new ArrayList();
        public SparseArray<PhotoEntry> photosByIds = new SparseArray();

        public AlbumEntry(int bucketId, String bucketName, PhotoEntry coverPhoto) {
            this.bucketId = bucketId;
            this.bucketName = bucketName;
            this.coverPhoto = coverPhoto;
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

        public AudioBuffer(int capacity) {
            this.buffer = ByteBuffer.allocateDirect(capacity);
            this.bufferBytes = new byte[capacity];
        }
    }

    public static class AudioEntry {
        public String author;
        public int duration;
        public String genre;
        /* renamed from: id */
        public long f52id;
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

    private class GalleryObserverExternal extends ContentObserver {

        /* renamed from: org.telegram.messenger.MediaController$GalleryObserverExternal$1 */
        class C04911 implements Runnable {
            C04911() {
            }

            public void run() {
                MediaController.refreshGalleryRunnable = null;
                MediaController.loadGalleryPhotosAlbums(0);
            }
        }

        public GalleryObserverExternal() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new C04911(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    private class GalleryObserverInternal extends ContentObserver {

        /* renamed from: org.telegram.messenger.MediaController$GalleryObserverInternal$1 */
        class C04921 implements Runnable {
            C04921() {
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
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new C04921(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
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
        /* renamed from: id */
        public String f53id;
        public String imagePath;
        public String imageUrl;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isPainted;
        public String localUrl;
        public Photo photo;
        public PhotoSize photoSize;
        public SavedFilterState savedFilterState;
        public int size;
        public ArrayList<InputDocument> stickers = new ArrayList();
        public String thumbPath;
        public PhotoSize thumbPhotoSize;
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

        public String getAttachName() {
            if (this.photoSize != null) {
                return FileLoader.getAttachFileName(this.photoSize);
            }
            if (this.document != null) {
                return FileLoader.getAttachFileName(this.document);
            }
            if (!(this.type == 1 || this.localUrl == null || this.localUrl.length() <= 0)) {
                File file = new File(this.localUrl);
                if (file.exists()) {
                    return file.getName();
                }
                this.localUrl = TtmlNode.ANONYMOUS_REGION_ID;
            }
            return Utilities.MD5(this.imageUrl) + "." + ImageLoader.getHttpUrlExtension(this.imageUrl, "jpg");
        }

        public String getPathToAttach() {
            if (this.photoSize != null) {
                return FileLoader.getPathToAttach(this.photoSize, true).getAbsolutePath();
            }
            if (this.document != null) {
                return FileLoader.getPathToAttach(this.document, true).getAbsolutePath();
            }
            return this.imageUrl;
        }
    }

    private class SmsObserver extends ContentObserver {
        public SmsObserver() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            MediaController.this.readSms();
        }
    }

    private final class StopMediaObserverRunnable implements Runnable {
        public int currentObserverToken;

        private StopMediaObserverRunnable() {
            this.currentObserverToken = 0;
        }

        /* synthetic */ StopMediaObserverRunnable(MediaController x0, C04691 x1) {
            this();
        }

        public void run() {
            if (this.currentObserverToken == MediaController.this.startObserverToken) {
                try {
                    if (MediaController.this.internalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
                        MediaController.this.internalObserver = null;
                    }
                } catch (Throwable e) {
                    FileLog.m14e(e);
                }
                try {
                    if (MediaController.this.externalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
                        MediaController.this.externalObserver = null;
                    }
                } catch (Throwable e2) {
                    FileLog.m14e(e2);
                }
            }
        }
    }

    private static class VideoConvertRunnable implements Runnable {
        private MessageObject messageObject;

        /* synthetic */ VideoConvertRunnable(MessageObject x0, C04691 x1) {
            this(x0);
        }

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
                        Thread th = new Thread(new VideoConvertRunnable(obj, null), "VideoConvertRunnable");
                        th.start();
                        th.join();
                    } catch (Throwable e) {
                        FileLog.m14e(e);
                    }
                }
            }).start();
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$13 */
    class C183413 implements VideoPlayerDelegate {

        /* renamed from: org.telegram.messenger.MediaController$13$1 */
        class C04571 implements Runnable {
            C04571() {
            }

            public void run() {
                MediaController.this.cleanupPlayer(true, true);
            }
        }

        C183413() {
        }

        public void onStateChanged(boolean playWhenReady, int playbackState) {
            if (MediaController.this.videoPlayer != null) {
                if (playbackState == 4 || playbackState == 1) {
                    try {
                        MediaController.this.baseActivity.getWindow().clearFlags(128);
                    } catch (Throwable e) {
                        FileLog.m14e(e);
                    }
                } else {
                    try {
                        MediaController.this.baseActivity.getWindow().addFlags(128);
                    } catch (Throwable e2) {
                        FileLog.m14e(e2);
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
            FileLog.m14e((Throwable) e);
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
                            MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new C04571());
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
    }

    public static native int isOpusFile(String str);

    private native int startRecord(String str);

    private native void stopRecord();

    private native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    private void readSms() {
    }

    public static void checkGallery() {
        if (VERSION.SDK_INT >= 24 && allPhotosAlbumEntry != null) {
            final int prevSize = allPhotosAlbumEntry.photos.size();
            Utilities.globalQueue.postRunnable(new Runnable() {
                @SuppressLint({"NewApi"})
                public void run() {
                    int count = 0;
                    Cursor cursor = null;
                    try {
                        if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                            cursor = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Media.EXTERNAL_CONTENT_URI, new String[]{"COUNT(_id)"}, null, null, null);
                            if (cursor != null && cursor.moveToNext()) {
                                count = 0 + cursor.getInt(0);
                            }
                        }
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (Throwable th) {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    try {
                        if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                            cursor = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, new String[]{"COUNT(_id)"}, null, null, null);
                            if (cursor != null && cursor.moveToNext()) {
                                count += cursor.getInt(0);
                            }
                        }
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (Throwable th2) {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    if (prevSize != count) {
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

    public static MediaController getInstance() {
        Throwable th;
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

    public MediaController() {
        this.recordQueue.setPriority(10);
        this.fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue.setPriority(10);
        this.recordQueue.postRunnable(new C04813());
        Utilities.globalQueue.postRunnable(new C04844());
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new C04855());
        this.mediaProjections = new String[]{"_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height"};
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        try {
            contentResolver.registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Throwable e) {
            FileLog.m14e(e);
        }
        try {
            contentResolver.registerContentObserver(Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Throwable e2) {
            FileLog.m14e(e2);
        }
        try {
            contentResolver.registerContentObserver(Video.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Throwable e22) {
            FileLog.m14e(e22);
        }
        try {
            contentResolver.registerContentObserver(Video.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Throwable e222) {
            FileLog.m14e(e222);
        }
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                pauseMessage(this.playingMessageObject);
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
                pauseMessage(this.playingMessageObject);
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
                this.audioPlayer.setVolume(volume);
            } else if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(volume);
            }
        } catch (Throwable e) {
            FileLog.m14e(e);
        }
    }

    private void startProgressTimer(final MessageObject currentPlayingMessageObject) {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Throwable e) {
                    FileLog.m14e(e);
                }
            }
            String fileName = currentPlayingMessageObject.getFileName();
            this.progressTimer = new Timer();
            this.progressTimer.schedule(new TimerTask() {

                /* renamed from: org.telegram.messenger.MediaController$6$1 */
                class C04861 implements Runnable {
                    C04861() {
                    }

                    public void run() {
                        if (currentPlayingMessageObject == null) {
                            return;
                        }
                        if ((MediaController.this.audioPlayer != null || MediaController.this.videoPlayer != null) && !MediaController.this.isPaused) {
                            try {
                                long duration;
                                long progress;
                                float bufferedValue;
                                float value;
                                if (MediaController.this.videoPlayer != null) {
                                    duration = MediaController.this.videoPlayer.getDuration();
                                    progress = MediaController.this.videoPlayer.getCurrentPosition();
                                    bufferedValue = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / ((float) duration);
                                    if (duration >= 0) {
                                        value = ((float) progress) / ((float) duration);
                                    } else {
                                        value = 0.0f;
                                    }
                                    if (progress < 0 || value >= MediaController.VOLUME_NORMAL) {
                                        return;
                                    }
                                }
                                duration = MediaController.this.audioPlayer.getDuration();
                                progress = MediaController.this.audioPlayer.getCurrentPosition();
                                if (duration == C0016C.TIME_UNSET || duration < 0) {
                                    value = 0.0f;
                                } else {
                                    value = ((float) progress) / ((float) duration);
                                }
                                bufferedValue = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) duration);
                                if (duration == C0016C.TIME_UNSET || progress < 0 || MediaController.this.seekToProgressPending != 0.0f) {
                                    return;
                                }
                                MediaController.this.lastProgress = progress;
                                currentPlayingMessageObject.audioPlayerDuration = (int) (duration / 1000);
                                currentPlayingMessageObject.audioProgress = value;
                                currentPlayingMessageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                                currentPlayingMessageObject.bufferedProgress = bufferedValue;
                                NotificationCenter.getInstance(currentPlayingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(currentPlayingMessageObject.getId()), Float.valueOf(value));
                            } catch (Throwable e) {
                                FileLog.m14e(e);
                            }
                        }
                    }
                }

                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new C04861());
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
                    FileLog.m14e(e);
                }
            }
        }
    }

    public void cleanup() {
        cleanupPlayer(false, true);
        this.audioInfo = null;
        this.playMusicAgain = false;
        for (int a = 0; a < 3; a++) {
            DownloadController.getInstance(a).cleanup();
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
        ContentResolver contentResolver;
        Uri uri;
        ContentObserver externalObserver;
        ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
        this.startObserverToken++;
        try {
            if (this.internalObserver == null) {
                contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                uri = Media.EXTERNAL_CONTENT_URI;
                externalObserver = new ExternalObserver();
                this.externalObserver = externalObserver;
                contentResolver.registerContentObserver(uri, false, externalObserver);
            }
        } catch (Throwable e) {
            FileLog.m14e(e);
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
            FileLog.m14e(e2);
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
            AndroidUtilities.runOnUIThread(new C04887(), 300000);
        } catch (Throwable e) {
            FileLog.m14e(e);
        }
    }

    public void stopMediaObserver() {
        if (this.stopMediaObserverRunnable == null) {
            this.stopMediaObserverRunnable = new StopMediaObserverRunnable(this, null);
        }
        this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
        ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    private void processMediaObserver(Uri uri) {
        Cursor cursor = null;
        try {
            android.graphics.Point size = AndroidUtilities.getRealScreenSize();
            cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
            ArrayList<Long> screenshotDates = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String val = TtmlNode.ANONYMOUS_REGION_ID;
                    String data = cursor.getString(0);
                    String display_name = cursor.getString(1);
                    String album_name = cursor.getString(2);
                    long date = cursor.getLong(3);
                    String title = cursor.getString(4);
                    int photoW = cursor.getInt(5);
                    int photoH = cursor.getInt(6);
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
                        NotificationCenter.getInstance(MediaController.this.lastChatAccount).postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
                        MediaController.this.checkScreenshots(arrayList);
                    }
                });
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e2) {
                }
            }
        } catch (Throwable e3) {
            FileLog.m14e(e3);
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e4) {
                }
            }
        } catch (Throwable th) {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e5) {
                }
            }
        }
    }

    private void checkScreenshots(ArrayList<Long> dates) {
        if (dates != null && !dates.isEmpty() && this.lastChatEnterTime != 0) {
            if (this.lastUser != null || (this.lastSecretChat instanceof TL_encryptedChat)) {
                boolean send = false;
                for (int a = 0; a < dates.size(); a++) {
                    Long date = (Long) dates.get(a);
                    if ((this.lastMediaCheckTime == 0 || date.longValue() > this.lastMediaCheckTime) && date.longValue() >= this.lastChatEnterTime && (this.lastChatLeaveTime == 0 || date.longValue() <= this.lastChatLeaveTime + AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS)) {
                        this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, date.longValue());
                        send = true;
                    }
                }
                if (!send) {
                    return;
                }
                if (this.lastSecretChat != null) {
                    SecretChatHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastSecretChat, this.lastChatVisibleMessages, null);
                } else {
                    SendMessagesHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastUser, this.lastMessageId, null);
                }
            }
        }
    }

    public void setLastVisibleMessageIds(int account, long enterTime, long leaveTime, User user, EncryptedChat encryptedChat, ArrayList<Long> visibleMessages, int visibleMessage) {
        this.lastChatEnterTime = enterTime;
        this.lastChatLeaveTime = leaveTime;
        this.lastChatAccount = account;
        this.lastSecretChat = encryptedChat;
        this.lastUser = user;
        this.lastMessageId = visibleMessage;
        this.lastChatVisibleMessages = visibleMessages;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int a;
        MessageObject messageObject;
        long did;
        if (id == NotificationCenter.FileDidLoaded || id == NotificationCenter.httpFileDidLoaded) {
            String fileName = args[0];
            if (this.downloadingCurrentMessage && this.playingMessageObject != null && this.playingMessageObject.currentAccount == account && FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(fileName)) {
                this.playMusicAgain = true;
                playMessage(this.playingMessageObject);
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            int channelId = ((Integer) args[1]).intValue();
            ArrayList<Integer> markAsDeletedMessages = args[0];
            if (this.playingMessageObject != null && channelId == this.playingMessageObject.messageOwner.to_id.channel_id && markAsDeletedMessages.contains(Integer.valueOf(this.playingMessageObject.getId()))) {
                cleanupPlayer(true, true);
            }
            if (this.voiceMessagesPlaylist != null && !this.voiceMessagesPlaylist.isEmpty() && channelId == ((MessageObject) this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id) {
                for (a = 0; a < markAsDeletedMessages.size(); a++) {
                    Integer key = (Integer) markAsDeletedMessages.get(a);
                    messageObject = (MessageObject) this.voiceMessagesPlaylistMap.get(key.intValue());
                    this.voiceMessagesPlaylistMap.remove(key.intValue());
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
                ArrayList<MessageObject> arrayList = args[1];
                this.playlist.addAll(0, arrayList);
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                    this.currentPlaylistNum = 0;
                    return;
                }
                this.currentPlaylistNum += arrayList.size();
            }
        } else if (id == NotificationCenter.didReceivedNewMessages) {
            if (this.voiceMessagesPlaylist != null && !this.voiceMessagesPlaylist.isEmpty()) {
                if (((Long) args[0]).longValue() == ((MessageObject) this.voiceMessagesPlaylist.get(0)).getDialogId()) {
                    ArrayList<MessageObject> arr = args[1];
                    for (a = 0; a < arr.size(); a++) {
                        messageObject = (MessageObject) arr.get(a);
                        if ((messageObject.isVoice() || messageObject.isRoundVideo()) && (!this.voiceMessagesPlaylistUnread || (messageObject.isContentUnread() && !messageObject.isOut()))) {
                            this.voiceMessagesPlaylist.add(messageObject);
                            this.voiceMessagesPlaylistMap.put(messageObject.getId(), messageObject);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.playerDidStartPlaying) {
            if (!getInstance().isCurrentPlayer(args[0])) {
                getInstance().pauseMessage(getInstance().getPlayingMessageObject());
            }
        }
    }

    protected boolean isRecordingAudio() {
        return (this.recordStartRunnable == null && this.recordingAudio == null) ? false : true;
    }

    private boolean isNearToSensor(float value) {
        return value < 5.0f && value != this.proximitySensor.getMaximumRange();
    }

    public boolean isRecordingOrListeningByProximity() {
        return this.proximityTouched && (isRecordingAudio() || (this.playingMessageObject != null && (this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo())));
    }

    public void onSensorChanged(SensorEvent event) {
        if (this.sensorsStarted && VoIPService.getSharedInstance() == null) {
            if (event.sensor == this.proximitySensor) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m11d("proximity changed to " + event.values[0]);
                }
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
                if (this.raisedToBack != 6 && ((val > 0.0f && this.previousAccValue > 0.0f) || (val < 0.0f && this.previousAccValue < 0.0f))) {
                    boolean goodValue;
                    int sign;
                    if (val > 0.0f) {
                        goodValue = val > 15.0f;
                        sign = 1;
                    } else {
                        goodValue = val < -15.0f;
                        sign = 2;
                    }
                    if (this.raisedToTopSign == 0 || this.raisedToTopSign == sign) {
                        if (!goodValue || this.raisedToBack != 0 || (this.raisedToTopSign != 0 && this.raisedToTopSign != sign)) {
                            if (!goodValue) {
                                this.countLess++;
                            }
                            if (!(this.raisedToTopSign == sign && this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                                this.raisedToBack = 0;
                                this.raisedToTop = 0;
                                this.raisedToTopSign = 0;
                                this.countLess = 0;
                            }
                        } else if (this.raisedToTop < 6 && !this.proximityTouched) {
                            this.raisedToTopSign = sign;
                            this.raisedToTop++;
                            if (this.raisedToTop == 6) {
                                this.countLess = 0;
                            }
                        }
                    } else if (this.raisedToTop != 6 || !goodValue) {
                        if (!goodValue) {
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
                                FileLog.m11d("motion detected");
                            }
                        }
                    }
                }
                this.previousAccValue = val;
                boolean z = this.gravityFast[1] > 2.5f && Math.abs(this.gravityFast[2]) < 4.0f && Math.abs(this.gravityFast[0]) > 1.5f;
                this.accelerometerVertical = z;
            }
            if (this.raisedToBack == 6 && this.accelerometerVertical && this.proximityTouched && !NotificationsController.audioManager.isWiredHeadsetOn()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m11d("sensor values reached");
                }
                if (this.playingMessageObject == null && this.recordStartRunnable == null && this.recordingAudio == null && !PhotoViewer.getInstance().isVisible() && ApplicationLoader.isScreenOn && !this.inputFieldHasText && this.allowStartRecord && this.raiseChat != null && !this.callInProgress) {
                    if (!this.raiseToEarRecord) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m11d("start record");
                        }
                        this.useFrontSpeaker = true;
                        if (!this.raiseChat.playFirstUnreadVoiceMessage()) {
                            this.raiseToEarRecord = true;
                            this.useFrontSpeaker = false;
                            startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null);
                        }
                        if (this.useFrontSpeaker) {
                            setUseFrontSpeaker(true);
                        }
                        this.ignoreOnPause = true;
                        if (!(!this.proximityHasDifferentValues || this.proximityWakeLock == null || this.proximityWakeLock.isHeld())) {
                            this.proximityWakeLock.acquire();
                        }
                    }
                } else if (this.playingMessageObject != null && ((this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m11d("start listen");
                    }
                    if (!(!this.proximityHasDifferentValues || this.proximityWakeLock == null || this.proximityWakeLock.isHeld())) {
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
            } else if (this.proximityTouched) {
                if (!(this.playingMessageObject == null || ApplicationLoader.mainInterfacePaused || ((!this.playingMessageObject.isVoice() && !this.playingMessageObject.isRoundVideo()) || this.useFrontSpeaker))) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m11d("start listen by proximity only");
                    }
                    if (!(!this.proximityHasDifferentValues || this.proximityWakeLock == null || this.proximityWakeLock.isHeld())) {
                        this.proximityWakeLock.acquire();
                    }
                    setUseFrontSpeaker(true);
                    startAudioAgain(false);
                    this.ignoreOnPause = true;
                }
            } else if (!this.proximityTouched) {
                if (this.raiseToEarRecord) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m11d("stop record");
                    }
                    stopRecording(2);
                    this.raiseToEarRecord = false;
                    this.ignoreOnPause = false;
                    if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                        this.proximityWakeLock.release();
                    }
                } else if (this.useFrontSpeaker) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m11d("stop listen");
                    }
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
                this.raisedToTopSign = 0;
                this.countLess = 0;
                this.timeSinceRaise = 0;
            }
        }
    }

    private void setUseFrontSpeaker(boolean value) {
        this.useFrontSpeaker = value;
        AudioManager audioManager = NotificationsController.audioManager;
        if (this.useFrontSpeaker) {
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(false);
            return;
        }
        audioManager.setSpeakerphoneOn(true);
    }

    public void startRecordingIfFromSpeaker() {
        if (this.useFrontSpeaker && this.raiseChat != null && this.allowStartRecord) {
            this.raiseToEarRecord = true;
            startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null);
            this.ignoreOnPause = true;
        }
    }

    private void startAudioAgain(boolean paused) {
        int i = 0;
        if (this.playingMessageObject != null) {
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.audioRouteChanged, Boolean.valueOf(this.useFrontSpeaker));
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
            if (!SharedConfig.raiseToSpeak) {
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
                this.raisedToTopSign = 0;
                this.countLess = 0;
                this.raisedToBack = 0;
                Utilities.globalQueue.postRunnable(new C045410());
                this.sensorsStarted = true;
            }
        }
    }

    public void stopRaiseToEarSensors(ChatActivity chatActivity, boolean fromChat) {
        if (this.ignoreOnPause) {
            this.ignoreOnPause = false;
            return;
        }
        stopRecording(fromChat ? 2 : 0);
        if (this.sensorsStarted && !this.ignoreOnPause) {
            if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null && this.raiseChat == chatActivity) {
                this.raiseChat = null;
                this.sensorsStarted = false;
                this.accelerometerVertical = false;
                this.proximityTouched = false;
                this.raiseToEarRecord = false;
                this.useFrontSpeaker = false;
                Utilities.globalQueue.postRunnable(new C045511());
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
                this.audioPlayer.releasePlayer();
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
            this.audioPlayer = null;
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
                FileLog.m14e(e2);
            }
        }
        stopProgressTimer();
        this.lastProgress = 0;
        this.isPaused = false;
        if (!(this.useFrontSpeaker || SharedConfig.raiseToSpeak)) {
            ChatActivity chat = this.raiseChat;
            stopRaiseToEarSensors(this.raiseChat, false);
            this.raiseChat = chat;
        }
        if (this.playingMessageObject != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance(this.playingMessageObject.currentAccount).cancelLoadFile(this.playingMessageObject.getDocument());
            }
            MessageObject lastFile = this.playingMessageObject;
            if (notify) {
                this.playingMessageObject.resetPlayingProgress();
                NotificationCenter.getInstance(lastFile.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
            }
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (notify) {
                NotificationsController.audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                int index = -1;
                if (this.voiceMessagesPlaylist != null) {
                    if (byVoiceEnd) {
                        index = this.voiceMessagesPlaylist.indexOf(lastFile);
                        if (index >= 0) {
                            this.voiceMessagesPlaylist.remove(index);
                            this.voiceMessagesPlaylistMap.remove(lastFile.getId());
                            if (this.voiceMessagesPlaylist.isEmpty()) {
                                this.voiceMessagesPlaylist = null;
                                this.voiceMessagesPlaylistMap = null;
                            }
                        }
                    }
                    this.voiceMessagesPlaylist = null;
                    this.voiceMessagesPlaylistMap = null;
                }
                if (this.voiceMessagesPlaylist == null || index >= this.voiceMessagesPlaylist.size()) {
                    if ((lastFile.isVoice() || lastFile.isRoundVideo()) && lastFile.getId() != 0) {
                        startRecordingIfFromSpeaker();
                    }
                    NotificationCenter.getInstance(lastFile.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(lastFile.getId()), Boolean.valueOf(stopService));
                    this.pipSwitchingState = 0;
                    if (this.pipRoundVideoView != null) {
                        this.pipRoundVideoView.close(true);
                        this.pipRoundVideoView = null;
                    }
                } else {
                    MessageObject nextVoiceMessage = (MessageObject) this.voiceMessagesPlaylist.get(index);
                    playMessage(nextVoiceMessage);
                    if (!(nextVoiceMessage.isRoundVideo() || this.pipRoundVideoView == null)) {
                        this.pipRoundVideoView.close(true);
                        this.pipRoundVideoView = null;
                    }
                }
            }
            if (stopService) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
        }
    }

    private boolean isSamePlayingMessage(MessageObject messageObject) {
        if (this.playingMessageObject != null && this.playingMessageObject.getDialogId() == messageObject.getDialogId() && this.playingMessageObject.getId() == messageObject.getId()) {
            if ((this.playingMessageObject.eventId == 0) == (messageObject.eventId == 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean seekToProgress(MessageObject messageObject, float progress) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        try {
            if (this.audioPlayer != null) {
                long duration = this.audioPlayer.getDuration();
                if (duration == C0016C.TIME_UNSET) {
                    this.seekToProgressPending = progress;
                } else {
                    int seekTo = (int) (((float) duration) * progress);
                    this.audioPlayer.seekTo((long) seekTo);
                    this.lastProgress = (long) seekTo;
                }
            } else if (this.videoPlayer != null) {
                this.videoPlayer.seekTo((long) (((float) this.videoPlayer.getDuration()) * progress));
            }
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidSeek, Integer.valueOf(this.playingMessageObject.getId()), Float.valueOf(progress));
            return true;
        } catch (Throwable e) {
            FileLog.m14e(e);
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
            if (SharedConfig.shuffleMusic) {
                buildShuffledPlayList();
                this.currentPlaylistNum = 0;
            }
            if (loadMusic) {
                DataQuery.getInstance(current.currentAccount).loadMusic(current.getDialogId(), ((MessageObject) this.playlist.get(0)).getIdWithChannel());
            }
        }
        return playMessage(current);
    }

    public void playNextMessage() {
        playNextMessageWithoutOrder(false);
    }

    public boolean findMessageInPlaylistAndPlay(MessageObject messageObject) {
        int index = this.playlist.indexOf(messageObject);
        if (index == -1) {
            return playMessage(messageObject);
        }
        playMessageAtIndex(index);
        return true;
    }

    public void playMessageAtIndex(int index) {
        if (this.currentPlaylistNum >= 0 && this.currentPlaylistNum < this.playlist.size()) {
            this.currentPlaylistNum = index;
            this.playMusicAgain = true;
            if (this.playingMessageObject != null) {
                this.playingMessageObject.resetPlayingProgress();
            }
            playMessage((MessageObject) this.playlist.get(this.currentPlaylistNum));
        }
    }

    private void playNextMessageWithoutOrder(boolean byStop) {
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (byStop && SharedConfig.repeatMode == 2 && !this.forceLoopCurrentPlaylist) {
            cleanupPlayer(false, false);
            MessageObject messageObject = (MessageObject) currentPlayList.get(this.currentPlaylistNum);
            messageObject.audioProgress = 0.0f;
            messageObject.audioProgressSec = 0;
            playMessage(messageObject);
            return;
        }
        boolean last = false;
        if (SharedConfig.playOrderReversed) {
            this.currentPlaylistNum++;
            if (this.currentPlaylistNum >= currentPlayList.size()) {
                this.currentPlaylistNum = 0;
                last = true;
            }
        } else {
            this.currentPlaylistNum--;
            if (this.currentPlaylistNum < 0) {
                this.currentPlaylistNum = currentPlayList.size() - 1;
                last = true;
            }
        }
        if (last && byStop && SharedConfig.repeatMode == 0 && !this.forceLoopCurrentPlaylist) {
            if (this.audioPlayer != null || this.videoPlayer != null) {
                if (this.audioPlayer != null) {
                    try {
                        this.audioPlayer.releasePlayer();
                    } catch (Throwable e) {
                        FileLog.m14e(e);
                    }
                    this.audioPlayer = null;
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
                        FileLog.m14e(e2);
                    }
                }
                stopProgressTimer();
                this.lastProgress = 0;
                this.isPaused = true;
                this.playingMessageObject.audioProgress = 0.0f;
                this.playingMessageObject.audioProgressSec = 0;
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            }
        } else if (this.currentPlaylistNum >= 0 && this.currentPlaylistNum < currentPlayList.size()) {
            if (this.playingMessageObject != null) {
                this.playingMessageObject.resetPlayingProgress();
            }
            this.playMusicAgain = true;
            playMessage((MessageObject) currentPlayList.get(this.currentPlaylistNum));
        }
    }

    public void playPreviousMessage() {
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!currentPlayList.isEmpty() && this.currentPlaylistNum >= 0 && this.currentPlaylistNum < currentPlayList.size()) {
            MessageObject currentSong = (MessageObject) currentPlayList.get(this.currentPlaylistNum);
            if (currentSong.audioProgressSec > 10) {
                seekToProgress(currentSong, 0.0f);
                return;
            }
            if (SharedConfig.playOrderReversed) {
                this.currentPlaylistNum--;
                if (this.currentPlaylistNum < 0) {
                    this.currentPlaylistNum = currentPlayList.size() - 1;
                }
            } else {
                this.currentPlaylistNum++;
                if (this.currentPlaylistNum >= currentPlayList.size()) {
                    this.currentPlaylistNum = 0;
                }
            }
            if (this.currentPlaylistNum >= 0 && this.currentPlaylistNum < currentPlayList.size()) {
                this.playMusicAgain = true;
                playMessage((MessageObject) currentPlayList.get(this.currentPlaylistNum));
            }
        }
    }

    protected void checkIsNextMediaFileDownloaded() {
        if (this.playingMessageObject != null && this.playingMessageObject.isMusic()) {
            checkIsNextMusicFileDownloaded(this.playingMessageObject.currentAccount);
        }
    }

    private void checkIsNextVoiceFileDownloaded(int currentAccount) {
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
                int i = 0;
            }
            if (cacheFile != null && cacheFile != file && !cacheFile.exists()) {
                FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), false, 0);
            }
        }
    }

    private void checkIsNextMusicFileDownloaded(int currentAccount) {
        if ((DownloadController.getInstance(currentAccount).getCurrentDownloadMask() & 16) != 0) {
            ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
            if (currentPlayList != null && currentPlayList.size() >= 2) {
                int nextIndex;
                if (SharedConfig.playOrderReversed) {
                    nextIndex = this.currentPlaylistNum + 1;
                    if (nextIndex >= currentPlayList.size()) {
                        nextIndex = 0;
                    }
                } else {
                    nextIndex = this.currentPlaylistNum - 1;
                    if (nextIndex < 0) {
                        nextIndex = currentPlayList.size() - 1;
                    }
                }
                MessageObject nextAudio = (MessageObject) currentPlayList.get(nextIndex);
                if (DownloadController.getInstance(currentAccount).canDownloadMedia(nextAudio)) {
                    File file = null;
                    if (!TextUtils.isEmpty(nextAudio.messageOwner.attachPath)) {
                        file = new File(nextAudio.messageOwner.attachPath);
                        if (!file.exists()) {
                            file = null;
                        }
                    }
                    File cacheFile = file != null ? file : FileLoader.getPathToMessage(nextAudio.messageOwner);
                    if (cacheFile == null || !cacheFile.exists()) {
                        int i = 0;
                    }
                    if (cacheFile != null && cacheFile != file && !cacheFile.exists() && nextAudio.isMusic()) {
                        FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), false, 0);
                    }
                }
            }
        }
    }

    public void setVoiceMessagesPlaylist(ArrayList<MessageObject> playlist, boolean unread) {
        this.voiceMessagesPlaylist = playlist;
        if (this.voiceMessagesPlaylist != null) {
            this.voiceMessagesPlaylistUnread = unread;
            this.voiceMessagesPlaylistMap = new SparseArray();
            for (int a = 0; a < this.voiceMessagesPlaylist.size(); a++) {
                MessageObject messageObject = (MessageObject) this.voiceMessagesPlaylist.get(a);
                this.voiceMessagesPlaylistMap.put(messageObject.getId(), messageObject);
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
                result = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
            } else {
                result = NotificationsController.audioManager.requestAudioFocus(this, 3, neededAudioFocus == 2 ? 3 : 1);
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
                        this.pipRoundVideoView.show(this.baseActivity, new C045612());
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
        if (textureView != null) {
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
    }

    public void setFlagSecure(BaseFragment parentFragment, boolean set) {
        if (set) {
            try {
                parentFragment.getParentActivity().getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
            } catch (Exception e) {
            }
            this.flagSecureFragment = parentFragment;
        } else if (this.flagSecureFragment == parentFragment) {
            try {
                parentFragment.getParentActivity().getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
            } catch (Exception e2) {
            }
            this.flagSecureFragment = null;
        }
    }

    public void setBaseActivity(Activity activity, boolean set) {
        if (set) {
            this.baseActivity = activity;
        } else if (this.baseActivity == activity) {
            this.baseActivity = null;
        }
    }

    public void setFeedbackView(View view, boolean set) {
        if (set) {
            this.feedbackView = view;
        } else if (this.feedbackView == view) {
            this.feedbackView = null;
        }
    }

    public void setPlaybackSpeed(float speed) {
        this.currentPlaybackSpeed = speed;
        if (this.audioPlayer != null) {
            this.audioPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
        } else if (this.videoPlayer != null) {
            this.videoPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
        }
        MessagesController.getGlobalMainSettings().edit().putFloat("playbackSpeed", speed).commit();
    }

    public float getPlaybackSpeed() {
        return this.currentPlaybackSpeed;
    }

    /* JADX WARNING: Removed duplicated region for block: B:111:0x03e6  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x02a5  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x02ba  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean playMessage(MessageObject messageObject) {
        if (messageObject == null) {
            return false;
        }
        if (!(this.audioPlayer == null && this.videoPlayer == null) && isSamePlayingMessage(messageObject)) {
            if (this.isPaused) {
                resumeAudio(messageObject);
            }
            if (!SharedConfig.raiseToSpeak) {
                startRaiseToEarSensors(this.raiseChat);
            }
            return true;
        }
        if (!messageObject.isOut() && messageObject.isContentUnread()) {
            MessagesController.getInstance(messageObject.currentAccount).markMessageContentAsRead(messageObject);
        }
        boolean notify = !this.playMusicAgain;
        if (this.playingMessageObject != null) {
            notify = false;
            if (!this.playMusicAgain) {
                this.playingMessageObject.resetPlayingProgress();
            }
        }
        cleanupPlayer(notify, false);
        this.playMusicAgain = false;
        this.seekToProgressPending = 0.0f;
        File file = null;
        boolean exists = false;
        if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
            file = new File(messageObject.messageOwner.attachPath);
            exists = file.exists();
            if (!exists) {
                file = null;
            }
        }
        File cacheFile = file != null ? file : FileLoader.getPathToMessage(messageObject.messageOwner);
        boolean canStream = SharedConfig.streamMedia && messageObject.isMusic() && ((int) messageObject.getDialogId()) != 0;
        if (!(cacheFile == null || cacheFile == file)) {
            exists = cacheFile.exists();
            if (!(exists || canStream)) {
                FileLoader.getInstance(messageObject.currentAccount).loadFile(messageObject.getDocument(), false, 0);
                this.downloadingCurrentMessage = true;
                this.isPaused = false;
                this.lastProgress = 0;
                this.audioInfo = null;
                this.playingMessageObject = messageObject;
                if (this.playingMessageObject.isMusic()) {
                    try {
                        ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                    } catch (Throwable e) {
                        FileLog.m14e(e);
                    }
                } else {
                    ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                }
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
                return true;
            }
        }
        this.downloadingCurrentMessage = false;
        if (messageObject.isMusic()) {
            checkIsNextMusicFileDownloaded(messageObject.currentAccount);
        } else {
            checkIsNextVoiceFileDownloaded(messageObject.currentAccount);
        }
        if (this.currentAspectRatioFrameLayout != null) {
            this.isDrawingWasReady = false;
            this.currentAspectRatioFrameLayout.setDrawingReady(false);
        }
        if (messageObject.isRoundVideo()) {
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.videoPlayer = new VideoPlayer();
            this.videoPlayer.setDelegate(new C183413());
            this.currentAspectRatioFrameLayoutReady = false;
            if (this.pipRoundVideoView == null) {
                if (MessagesController.getInstance(messageObject.currentAccount).isDialogVisible(messageObject.getDialogId())) {
                    if (this.currentTextureView != null) {
                        this.videoPlayer.setTextureView(this.currentTextureView);
                    }
                    this.videoPlayer.preparePlayer(Uri.fromFile(cacheFile), "other");
                    this.videoPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                    if (this.currentPlaybackSpeed > VOLUME_NORMAL) {
                        this.videoPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
                    }
                    this.videoPlayer.play();
                }
            }
            if (this.pipRoundVideoView == null) {
                try {
                    this.pipRoundVideoView = new PipRoundVideoView();
                    this.pipRoundVideoView.show(this.baseActivity, new C045814());
                } catch (Exception e2) {
                    this.pipRoundVideoView = null;
                }
            }
            if (this.pipRoundVideoView != null) {
                this.videoPlayer.setTextureView(this.pipRoundVideoView.getTextureView());
            }
            this.videoPlayer.preparePlayer(Uri.fromFile(cacheFile), "other");
            if (this.useFrontSpeaker) {
            }
            this.videoPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
            if (this.currentPlaybackSpeed > VOLUME_NORMAL) {
            }
            this.videoPlayer.play();
        } else {
            if (this.pipRoundVideoView != null) {
                this.pipRoundVideoView.close(true);
                this.pipRoundVideoView = null;
            }
            try {
                this.audioPlayer = new VideoPlayer();
                final MessageObject messageObject2 = messageObject;
                this.audioPlayer.setDelegate(new VideoPlayerDelegate() {
                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState == 4) {
                            if (MediaController.this.playlist.isEmpty() || MediaController.this.playlist.size() <= 1) {
                                MediaController mediaController = MediaController.this;
                                boolean z = messageObject2 != null && messageObject2.isVoice();
                                mediaController.cleanupPlayer(true, true, z);
                                return;
                            }
                            MediaController.this.playNextMessageWithoutOrder(true);
                        } else if (MediaController.this.seekToProgressPending == 0.0f) {
                        } else {
                            if (playbackState == 3 || playbackState == 1) {
                                int seekTo = (int) (((float) MediaController.this.audioPlayer.getDuration()) * MediaController.this.seekToProgressPending);
                                MediaController.this.audioPlayer.seekTo((long) seekTo);
                                MediaController.this.lastProgress = (long) seekTo;
                                MediaController.this.seekToProgressPending = 0.0f;
                            }
                        }
                    }

                    public void onError(Exception e) {
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                    }

                    public void onRenderedFirstFrame() {
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }
                });
                if (exists) {
                    if (!(messageObject.mediaExists || cacheFile == file)) {
                        messageObject2 = messageObject;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.FileDidLoaded, FileLoader.getAttachFileName(messageObject2.getDocument()));
                            }
                        });
                    }
                    this.audioPlayer.preparePlayer(Uri.fromFile(cacheFile), "other");
                } else {
                    Document document = messageObject.getDocument();
                    this.audioPlayer.preparePlayer(Uri.parse("tg://" + messageObject.getFileName() + ("?account=" + messageObject.currentAccount + "&id=" + document.f84id + "&hash=" + document.access_hash + "&dc=" + document.dc_id + "&size=" + document.size + "&mime=" + URLEncoder.encode(document.mime_type, C0016C.UTF8_NAME) + "&name=" + URLEncoder.encode(FileLoader.getDocumentFileName(document), C0016C.UTF8_NAME))), "other");
                }
                if (messageObject.isVoice()) {
                    if (this.currentPlaybackSpeed > VOLUME_NORMAL) {
                        this.audioPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
                    }
                    this.audioInfo = null;
                    this.playlist.clear();
                    this.shuffledPlaylist.clear();
                } else {
                    try {
                        this.audioInfo = AudioInfo.getAudioInfo(cacheFile);
                    } catch (Throwable e3) {
                        FileLog.m14e(e3);
                    }
                }
                this.audioPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                this.audioPlayer.play();
            } catch (Throwable e32) {
                FileLog.m14e(e32);
                NotificationCenter instance = NotificationCenter.getInstance(messageObject.currentAccount);
                int i = NotificationCenter.messagePlayingPlayStateChanged;
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf(this.playingMessageObject != null ? this.playingMessageObject.getId() : 0);
                instance.postNotificationName(i, objArr);
                if (this.audioPlayer != null) {
                    this.audioPlayer.releasePlayer();
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
        this.playingMessageObject = messageObject;
        if (!SharedConfig.raiseToSpeak) {
            startRaiseToEarSensors(this.raiseChat);
        }
        startProgressTimer(this.playingMessageObject);
        NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidStarted, messageObject);
        long duration;
        if (this.videoPlayer != null) {
            try {
                if (this.playingMessageObject.audioProgress != 0.0f) {
                    duration = this.audioPlayer.getDuration();
                    if (duration == C0016C.TIME_UNSET) {
                        duration = ((long) this.playingMessageObject.getDuration()) * 1000;
                    }
                    this.videoPlayer.seekTo((long) ((int) (((float) duration) * this.playingMessageObject.audioProgress)));
                }
            } catch (Throwable e22) {
                this.playingMessageObject.audioProgress = 0.0f;
                this.playingMessageObject.audioProgressSec = 0;
                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                FileLog.m14e(e22);
            }
        } else if (this.audioPlayer != null) {
            try {
                if (this.playingMessageObject.audioProgress != 0.0f) {
                    duration = this.audioPlayer.getDuration();
                    if (duration == C0016C.TIME_UNSET) {
                        duration = ((long) this.playingMessageObject.getDuration()) * 1000;
                    }
                    this.audioPlayer.seekTo((long) ((int) (((float) duration) * this.playingMessageObject.audioProgress)));
                }
            } catch (Throwable e222) {
                this.playingMessageObject.resetPlayingProgress();
                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                FileLog.m14e(e222);
            }
        }
        if (this.playingMessageObject.isMusic()) {
            try {
                ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            } catch (Throwable e322) {
                FileLog.m14e(e322);
            }
        } else {
            ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
        }
        return true;
    }

    public void stopAudio() {
        if ((this.audioPlayer != null || this.videoPlayer != null) && this.playingMessageObject != null) {
            try {
                if (this.audioPlayer != null) {
                    this.audioPlayer.pause();
                } else if (this.videoPlayer != null) {
                    this.videoPlayer.pause();
                }
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
            try {
                if (this.audioPlayer != null) {
                    this.audioPlayer.releasePlayer();
                    this.audioPlayer = null;
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
                        FileLog.m14e(e2);
                    }
                }
            } catch (Throwable e22) {
                FileLog.m14e(e22);
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

    public void toggleShuffleMusic(int type) {
        boolean oldShuffle = SharedConfig.shuffleMusic;
        SharedConfig.toggleShuffleMusic(type);
        if (oldShuffle == SharedConfig.shuffleMusic) {
            return;
        }
        if (SharedConfig.shuffleMusic) {
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

    public boolean isCurrentPlayer(VideoPlayer player) {
        return this.videoPlayer == player || this.audioPlayer == player;
    }

    public boolean pauseMessage(MessageObject messageObject) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        stopProgressTimer();
        try {
            if (this.audioPlayer != null) {
                this.audioPlayer.pause();
            } else if (this.videoPlayer != null) {
                this.videoPlayer.pause();
            }
            this.isPaused = true;
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        } catch (Throwable e) {
            FileLog.m14e(e);
            this.isPaused = false;
            return false;
        }
    }

    public boolean resumeAudio(MessageObject messageObject) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        try {
            startProgressTimer(this.playingMessageObject);
            if (this.audioPlayer != null) {
                this.audioPlayer.play();
            } else if (this.videoPlayer != null) {
                this.videoPlayer.play();
            }
            checkAudioFocus(messageObject);
            this.isPaused = false;
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        } catch (Throwable e) {
            FileLog.m14e(e);
            return false;
        }
    }

    public boolean isRoundVideoDrawingReady() {
        return this.currentAspectRatioFrameLayout != null && this.currentAspectRatioFrameLayout.isDrawingReady();
    }

    public ArrayList<MessageObject> getPlaylist() {
        return this.playlist;
    }

    public boolean isPlayingMessage(MessageObject messageObject) {
        boolean z = true;
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null) {
            return false;
        }
        if (this.playingMessageObject.eventId != 0 && this.playingMessageObject.eventId == messageObject.eventId) {
            if (this.downloadingCurrentMessage) {
                z = false;
            }
            return z;
        } else if (!isSamePlayingMessage(messageObject)) {
            return false;
        } else {
            if (this.downloadingCurrentMessage) {
                z = false;
            }
            return z;
        }
    }

    public boolean isMessagePaused() {
        return this.isPaused || this.downloadingCurrentMessage;
    }

    public boolean isDownloadingCurrentMessage() {
        return this.downloadingCurrentMessage;
    }

    public void setReplyingMessage(MessageObject reply_to_msg) {
        this.recordReplyingMessageObject = reply_to_msg;
    }

    public void startRecording(int currentAccount, long dialog_id, MessageObject reply_to_msg) {
        long j;
        boolean paused = false;
        if (!(this.playingMessageObject == null || !isPlayingMessage(this.playingMessageObject) || isMessagePaused())) {
            paused = true;
            pauseMessage(this.playingMessageObject);
        }
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        final int i = currentAccount;
        final long j2 = dialog_id;
        final MessageObject messageObject = reply_to_msg;
        Runnable c046417 = new Runnable() {

            /* renamed from: org.telegram.messenger.MediaController$17$1 */
            class C04601 implements Runnable {
                C04601() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
            }

            /* renamed from: org.telegram.messenger.MediaController$17$2 */
            class C04612 implements Runnable {
                C04612() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
            }

            /* renamed from: org.telegram.messenger.MediaController$17$3 */
            class C04623 implements Runnable {
                C04623() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
            }

            /* renamed from: org.telegram.messenger.MediaController$17$4 */
            class C04634 implements Runnable {
                C04634() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStarted, new Object[0]);
                }
            }

            public void run() {
                if (MediaController.this.audioRecorder != null) {
                    AndroidUtilities.runOnUIThread(new C04601());
                    return;
                }
                MediaController.this.recordingAudio = new TL_document();
                MediaController.this.recordingAudio.dc_id = Integer.MIN_VALUE;
                MediaController.this.recordingAudio.f84id = (long) SharedConfig.getLastLocalId();
                MediaController.this.recordingAudio.user_id = UserConfig.getInstance(i).getClientUserId();
                MediaController.this.recordingAudio.mime_type = "audio/ogg";
                MediaController.this.recordingAudio.thumb = new TL_photoSizeEmpty();
                MediaController.this.recordingAudio.thumb.type = "s";
                SharedConfig.saveConfig();
                MediaController.this.recordingAudioFile = new File(FileLoader.getDirectory(4), FileLoader.getAttachFileName(MediaController.this.recordingAudio));
                try {
                    if (MediaController.this.startRecord(MediaController.this.recordingAudioFile.getAbsolutePath()) == 0) {
                        AndroidUtilities.runOnUIThread(new C04612());
                        return;
                    }
                    MediaController.this.audioRecorder = new AudioRecord(1, 16000, 16, 2, MediaController.this.recordBufferSize * 10);
                    MediaController.this.recordStartTime = System.currentTimeMillis();
                    MediaController.this.recordTimeCount = 0;
                    MediaController.this.samplesCount = 0;
                    MediaController.this.recordDialogId = j2;
                    MediaController.this.recordingCurrentAccount = i;
                    MediaController.this.recordReplyingMessageObject = messageObject;
                    MediaController.this.fileBuffer.rewind();
                    MediaController.this.audioRecorder.startRecording();
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(new C04634());
                } catch (Throwable e) {
                    FileLog.m14e(e);
                    MediaController.this.recordingAudio = null;
                    MediaController.this.stopRecord();
                    MediaController.this.recordingAudioFile.delete();
                    MediaController.this.recordingAudioFile = null;
                    try {
                        MediaController.this.audioRecorder.release();
                        MediaController.this.audioRecorder = null;
                    } catch (Throwable e2) {
                        FileLog.m14e(e2);
                    }
                    AndroidUtilities.runOnUIThread(new C04623());
                }
            }
        };
        this.recordStartRunnable = c046417;
        if (paused) {
            j = 500;
        } else {
            j = 50;
        }
        dispatchQueue.postRunnable(c046417, j);
    }

    public void generateWaveform(MessageObject messageObject) {
        final String id = messageObject.getId() + "_" + messageObject.getDialogId();
        final String path = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(id)) {
            this.generatingWaveform.put(id, messageObject);
            Utilities.globalQueue.postRunnable(new Runnable() {
                public void run() {
                    final byte[] waveform = MediaController.this.getWaveform(path);
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
                                MessagesStorage.getInstance(messageObject.currentAccount).putMessages(messagesRes, messageObject.getDialogId(), -1, 0, false);
                                new ArrayList().add(messageObject);
                                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList);
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

                /* renamed from: org.telegram.messenger.MediaController$19$1 */
                class C04671 implements Runnable {
                    C04671() {
                    }

                    public void run() {
                        audioToSend.date = ConnectionsManager.getInstance(MediaController.this.recordingCurrentAccount).getCurrentTime();
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
                            if (send == 1) {
                                SendMessagesHelper.getInstance(MediaController.this.recordingCurrentAccount).sendMessage(audioToSend, null, recordingAudioFileToSend.getAbsolutePath(), MediaController.this.recordDialogId, MediaController.this.recordReplyingMessageObject, null, null, null, null, 0);
                            }
                            NotificationCenter instance = NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount);
                            int i = NotificationCenter.audioDidSent;
                            Object[] objArr = new Object[2];
                            objArr[0] = send == 2 ? audioToSend : null;
                            objArr[1] = send == 2 ? recordingAudioFileToSend.getAbsolutePath() : null;
                            instance.postNotificationName(i, objArr);
                            return;
                        }
                        recordingAudioFileToSend.delete();
                    }
                }

                public void run() {
                    MediaController.this.stopRecord();
                    AndroidUtilities.runOnUIThread(new C04671());
                }
            });
        }
        try {
            if (this.audioRecorder != null) {
                this.audioRecorder.release();
                this.audioRecorder = null;
            }
        } catch (Throwable e) {
            FileLog.m14e(e);
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

            /* renamed from: org.telegram.messenger.MediaController$20$1 */
            class C04701 implements Runnable {
                C04701() {
                }

                public void run() {
                    int i = 1;
                    NotificationCenter instance = NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount);
                    int i2 = NotificationCenter.recordStopped;
                    Object[] objArr = new Object[1];
                    if (send != 2) {
                        i = 0;
                    }
                    objArr[0] = Integer.valueOf(i);
                    instance.postNotificationName(i2, objArr);
                }
            }

            public void run() {
                if (MediaController.this.audioRecorder != null) {
                    try {
                        MediaController.this.sendAfterDone = send;
                        MediaController.this.audioRecorder.stop();
                    } catch (Throwable e) {
                        FileLog.m14e(e);
                        if (MediaController.this.recordingAudioFile != null) {
                            MediaController.this.recordingAudioFile.delete();
                        }
                    }
                    if (send == 0) {
                        MediaController.this.stopRecordingInternal(0);
                    }
                    try {
                        MediaController.this.feedbackView.performHapticFeedback(3, 2);
                    } catch (Exception e2) {
                    }
                    AndroidUtilities.runOnUIThread(new C04701());
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
                if (!file.exists() || AndroidUtilities.isInternalUri(Uri.fromFile(file))) {
                    file = null;
                }
            }
            if (file != null) {
                final File sourceFile = file;
                final boolean[] cancelled = new boolean[]{false};
                if (sourceFile.exists()) {
                    AlertDialog progressDialog = null;
                    if (!(context == null || type == 0)) {
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
                                FileLog.m14e(e);
                                finalProgress = progressDialog;
                                i = type;
                                str = name;
                                str2 = mime;
                                new Thread(new Runnable() {

                                    /* renamed from: org.telegram.messenger.MediaController$22$2 */
                                    class C04742 implements Runnable {
                                        C04742() {
                                        }

                                        public void run() {
                                            try {
                                                finalProgress.dismiss();
                                            } catch (Throwable e) {
                                                FileLog.m14e(e);
                                            }
                                        }
                                    }

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
                                                                    FileLog.m14e(e);
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
                                                FileLog.m14e(e3);
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
                                            FileLog.m14e(e32);
                                        }
                                        if (finalProgress != null) {
                                            AndroidUtilities.runOnUIThread(new C04742());
                                        }
                                    }
                                }).start();
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.m14e(e);
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
                                FileLog.m14e(e2);
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
                    FileLog.m14e(e22);
                }
            }
        } catch (Throwable e) {
            FileLog.m14e(e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.m14e(e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.m14e(e2222);
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
                            FileLog.m14e(e2);
                        }
                    }
                    return z;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e22) {
                    FileLog.m14e(e22);
                }
            }
        } catch (Throwable e) {
            FileLog.m14e(e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.m14e(e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.m14e(e2222);
                }
            }
        }
        return z;
    }

    public static String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
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
                FileLog.m14e(e);
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

    /* JADX WARNING: Removed duplicated region for block: B:57:0x00b7 A:{SYNTHETIC, Splitter: B:57:0x00b7} */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00bc A:{SYNTHETIC, Splitter: B:60:0x00bc} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String copyFileToCache(Uri uri, String ext) {
        Throwable e;
        Throwable th;
        InputStream inputStream = null;
        FileOutputStream output = null;
        try {
            String name = FileLoader.fixFileName(getFileName(uri));
            if (name == null) {
                int id = SharedConfig.getLastLocalId();
                SharedConfig.saveConfig();
                name = String.format(Locale.US, "%d.%s", new Object[]{Integer.valueOf(id), ext});
            }
            File f = new File(FileLoader.getDirectory(4), "sharing/");
            f.mkdirs();
            File f2 = new File(f, name);
            if (AndroidUtilities.isInternalUri(Uri.fromFile(f2))) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e2) {
                        FileLog.m14e(e2);
                    }
                }
                if (output == null) {
                    return null;
                }
                try {
                    output.close();
                    return null;
                } catch (Throwable e22) {
                    FileLog.m14e(e22);
                    return null;
                }
            }
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            FileOutputStream output2 = new FileOutputStream(f2);
            try {
                byte[] buffer = new byte[CacheDataSink.DEFAULT_BUFFER_SIZE];
                while (true) {
                    int len = inputStream.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    output2.write(buffer, 0, len);
                }
                String absolutePath = f2.getAbsolutePath();
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e222) {
                        FileLog.m14e(e222);
                    }
                }
                if (output2 != null) {
                    try {
                        output2.close();
                    } catch (Throwable e2222) {
                        FileLog.m14e(e2222);
                    }
                }
                output = output2;
                return absolutePath;
            } catch (Exception e3) {
                e = e3;
                output = output2;
            } catch (Throwable th2) {
                th = th2;
                output = output2;
                if (inputStream != null) {
                }
                if (output != null) {
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            try {
                FileLog.m14e(e);
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e22222) {
                        FileLog.m14e(e22222);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (Throwable e222222) {
                        FileLog.m14e(e222222);
                    }
                }
                return null;
            } catch (Throwable th3) {
                th = th3;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e2222222) {
                        FileLog.m14e(e2222222);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (Throwable e22222222) {
                        FileLog.m14e(e22222222);
                    }
                }
                throw th;
            }
        }
    }

    public static void loadGalleryPhotosAlbums(final int guid) {
        Thread thread = new Thread(new Runnable() {

            /* renamed from: org.telegram.messenger.MediaController$23$1 */
            class C04761 implements Comparator<PhotoEntry> {
                C04761() {
                }

                public int compare(PhotoEntry o1, PhotoEntry o2) {
                    if (o1.dateTaken < o2.dateTaken) {
                        return 1;
                    }
                    if (o1.dateTaken > o2.dateTaken) {
                        return -1;
                    }
                    return 0;
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:73:0x01e1 A:{Catch:{ Throwable -> 0x0332, all -> 0x0345 }} */
            /* JADX WARNING: Removed duplicated region for block: B:121:0x0307 A:{SYNTHETIC, Splitter: B:121:0x0307} */
            /* JADX WARNING: Removed duplicated region for block: B:126:0x0314 A:{LOOP_END, LOOP:2: B:124:0x030c->B:126:0x0314} */
            /* JADX WARNING: Removed duplicated region for block: B:126:0x0314 A:{LOOP_END, LOOP:2: B:124:0x030c->B:126:0x0314} */
            /* JADX WARNING: Removed duplicated region for block: B:141:0x034c A:{SYNTHETIC, Splitter: B:141:0x034c} */
            /* JADX WARNING: Removed duplicated region for block: B:73:0x01e1 A:{Catch:{ Throwable -> 0x0332, all -> 0x0345 }} */
            /* JADX WARNING: Removed duplicated region for block: B:121:0x0307 A:{SYNTHETIC, Splitter: B:121:0x0307} */
            /* JADX WARNING: Removed duplicated region for block: B:126:0x0314 A:{LOOP_END, LOOP:2: B:124:0x030c->B:126:0x0314} */
            /* JADX WARNING: Removed duplicated region for block: B:73:0x01e1 A:{Catch:{ Throwable -> 0x0332, all -> 0x0345 }} */
            /* JADX WARNING: Removed duplicated region for block: B:121:0x0307 A:{SYNTHETIC, Splitter: B:121:0x0307} */
            /* JADX WARNING: Removed duplicated region for block: B:126:0x0314 A:{LOOP_END, LOOP:2: B:124:0x030c->B:126:0x0314} */
            /* JADX WARNING: Removed duplicated region for block: B:102:0x02cb A:{SYNTHETIC, Splitter: B:102:0x02cb} */
            /* JADX WARNING: Removed duplicated region for block: B:61:0x01ac A:{SYNTHETIC, Splitter: B:61:0x01ac} */
            /* JADX WARNING: Removed duplicated region for block: B:73:0x01e1 A:{Catch:{ Throwable -> 0x0332, all -> 0x0345 }} */
            /* JADX WARNING: Removed duplicated region for block: B:121:0x0307 A:{SYNTHETIC, Splitter: B:121:0x0307} */
            /* JADX WARNING: Removed duplicated region for block: B:126:0x0314 A:{LOOP_END, LOOP:2: B:124:0x030c->B:126:0x0314} */
            /* JADX WARNING: Removed duplicated region for block: B:102:0x02cb A:{SYNTHETIC, Splitter: B:102:0x02cb} */
            /* Code decompiled incorrectly, please refer to instructions dump. */
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
                Integer mediaCameraAlbumId;
                int a;
                Throwable th;
                AlbumEntry albumEntry2;
                ArrayList<AlbumEntry> mediaAlbumsSorted = new ArrayList();
                ArrayList<AlbumEntry> photoAlbumsSorted = new ArrayList();
                SparseArray<AlbumEntry> mediaAlbums = new SparseArray();
                SparseArray<AlbumEntry> photoAlbums = new SparseArray();
                AlbumEntry allPhotosAlbum = null;
                AlbumEntry allMediaAlbum2 = null;
                String cameraFolder = null;
                try {
                    cameraFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/";
                } catch (Throwable e2) {
                    FileLog.m14e(e2);
                }
                Integer mediaCameraAlbumId2 = null;
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
                        AlbumEntry allPhotosAlbum2 = null;
                        while (cursor.moveToNext()) {
                            try {
                                imageId = cursor.getInt(imageIdColumn);
                                bucketId = cursor.getInt(bucketIdColumn);
                                bucketName = cursor.getString(bucketNameColumn);
                                path = cursor.getString(dataColumn);
                                dateTaken = cursor.getLong(dateColumn);
                                int orientation = cursor.getInt(orientationColumn);
                                if (!(path == null || path.length() == 0)) {
                                    PhotoEntry photoEntry = new PhotoEntry(bucketId, imageId, dateTaken, path, orientation, false);
                                    if (allPhotosAlbum2 == null) {
                                        albumEntry = new AlbumEntry(0, LocaleController.getString("AllPhotos", R.string.AllPhotos), photoEntry);
                                        try {
                                            photoAlbumsSorted.add(0, albumEntry);
                                        } catch (Throwable th2) {
                                            th = th2;
                                            allMediaAlbum2 = allMediaAlbum;
                                            if (cursor != null) {
                                            }
                                            throw th;
                                        }
                                    }
                                    allPhotosAlbum = allPhotosAlbum2;
                                    if (allMediaAlbum == null) {
                                        allMediaAlbum2 = new AlbumEntry(0, LocaleController.getString("AllMedia", R.string.AllMedia), photoEntry);
                                        mediaAlbumsSorted.add(0, allMediaAlbum2);
                                    } else {
                                        allMediaAlbum2 = allMediaAlbum;
                                    }
                                    allPhotosAlbum.addPhoto(photoEntry);
                                    allMediaAlbum2.addPhoto(photoEntry);
                                    albumEntry2 = (AlbumEntry) mediaAlbums.get(bucketId);
                                    if (albumEntry2 == null) {
                                        albumEntry = new AlbumEntry(bucketId, bucketName, photoEntry);
                                        mediaAlbums.put(bucketId, albumEntry);
                                        if (mediaCameraAlbumId2 != null || cameraFolder == null || path == null || !path.startsWith(cameraFolder)) {
                                            try {
                                                mediaAlbumsSorted.add(albumEntry);
                                            } catch (Throwable th3) {
                                                e2 = th3;
                                            }
                                        } else {
                                            mediaAlbumsSorted.add(0, albumEntry);
                                            mediaCameraAlbumId2 = Integer.valueOf(bucketId);
                                        }
                                    }
                                    albumEntry2.addPhoto(photoEntry);
                                    albumEntry2 = (AlbumEntry) photoAlbums.get(bucketId);
                                    if (albumEntry2 == null) {
                                        albumEntry = new AlbumEntry(bucketId, bucketName, photoEntry);
                                        photoAlbums.put(bucketId, albumEntry);
                                        if (photoCameraAlbumId != null || cameraFolder == null || path == null || !path.startsWith(cameraFolder)) {
                                            photoAlbumsSorted.add(albumEntry);
                                        } else {
                                            photoAlbumsSorted.add(0, albumEntry);
                                            photoCameraAlbumId = Integer.valueOf(bucketId);
                                        }
                                    }
                                    albumEntry2.addPhoto(photoEntry);
                                    allMediaAlbum = allMediaAlbum2;
                                    allPhotosAlbum2 = allPhotosAlbum;
                                }
                            } catch (Throwable th4) {
                                th = th4;
                                allMediaAlbum2 = allMediaAlbum;
                                allPhotosAlbum = allPhotosAlbum2;
                                if (cursor != null) {
                                }
                                throw th;
                            }
                        }
                        allMediaAlbum2 = allMediaAlbum;
                        allPhotosAlbum = allPhotosAlbum2;
                    }
                }
                if (cursor != null) {
                    try {
                        cursor.close();
                        mediaCameraAlbumId = mediaCameraAlbumId2;
                        allMediaAlbum = allMediaAlbum2;
                    } catch (Throwable e22) {
                        FileLog.m14e(e22);
                        mediaCameraAlbumId = mediaCameraAlbumId2;
                        allMediaAlbum = allMediaAlbum2;
                    }
                    if (VERSION.SDK_INT < 23 || (VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0)) {
                        cursor = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, MediaController.projectionVideo, null, null, "datetaken DESC");
                        if (cursor != null) {
                            imageIdColumn = cursor.getColumnIndex("_id");
                            bucketIdColumn = cursor.getColumnIndex("bucket_id");
                            bucketNameColumn = cursor.getColumnIndex("bucket_display_name");
                            dataColumn = cursor.getColumnIndex("_data");
                            dateColumn = cursor.getColumnIndex("datetaken");
                            int durationColumn = cursor.getColumnIndex("duration");
                            while (cursor.moveToNext()) {
                                imageId = cursor.getInt(imageIdColumn);
                                bucketId = cursor.getInt(bucketIdColumn);
                                bucketName = cursor.getString(bucketNameColumn);
                                path = cursor.getString(dataColumn);
                                dateTaken = cursor.getLong(dateColumn);
                                long duration = cursor.getLong(durationColumn);
                                if (!(path == null || path.length() == 0)) {
                                    PhotoEntry photoEntry2 = new PhotoEntry(bucketId, imageId, dateTaken, path, (int) (duration / 1000), true);
                                    if (allMediaAlbum == null) {
                                        allMediaAlbum2 = new AlbumEntry(0, LocaleController.getString("AllMedia", R.string.AllMedia), photoEntry2);
                                        try {
                                            mediaAlbumsSorted.add(0, allMediaAlbum2);
                                        } catch (Throwable th5) {
                                            th = th5;
                                            mediaCameraAlbumId2 = mediaCameraAlbumId;
                                        }
                                    } else {
                                        allMediaAlbum2 = allMediaAlbum;
                                    }
                                    allMediaAlbum2.addPhoto(photoEntry2);
                                    albumEntry2 = (AlbumEntry) mediaAlbums.get(bucketId);
                                    if (albumEntry2 == null) {
                                        albumEntry = new AlbumEntry(bucketId, bucketName, photoEntry2);
                                        mediaAlbums.put(bucketId, albumEntry);
                                        if (mediaCameraAlbumId != null || cameraFolder == null || path == null || !path.startsWith(cameraFolder)) {
                                            mediaAlbumsSorted.add(albumEntry);
                                        } else {
                                            mediaAlbumsSorted.add(0, albumEntry);
                                            mediaCameraAlbumId2 = Integer.valueOf(bucketId);
                                            albumEntry2.addPhoto(photoEntry2);
                                            mediaCameraAlbumId = mediaCameraAlbumId2;
                                            allMediaAlbum = allMediaAlbum2;
                                        }
                                    }
                                    mediaCameraAlbumId2 = mediaCameraAlbumId;
                                    try {
                                        albumEntry2.addPhoto(photoEntry2);
                                        mediaCameraAlbumId = mediaCameraAlbumId2;
                                        allMediaAlbum = allMediaAlbum2;
                                    } catch (Throwable th6) {
                                        e22 = th6;
                                    }
                                }
                            }
                        }
                    }
                    mediaCameraAlbumId2 = mediaCameraAlbumId;
                    allMediaAlbum2 = allMediaAlbum;
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Throwable e222) {
                            FileLog.m14e(e222);
                        }
                    }
                    for (a = 0; a < mediaAlbumsSorted.size(); a++) {
                        Collections.sort(((AlbumEntry) mediaAlbumsSorted.get(a)).photos, new C04761());
                    }
                    MediaController.broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, mediaCameraAlbumId2, allMediaAlbum2, allPhotosAlbum, 0);
                }
                mediaCameraAlbumId = mediaCameraAlbumId2;
                allMediaAlbum = allMediaAlbum2;
                try {
                    cursor = Media.query(ApplicationLoader.applicationContext.getContentResolver(), Video.Media.EXTERNAL_CONTENT_URI, MediaController.projectionVideo, null, null, "datetaken DESC");
                    if (cursor != null) {
                    }
                    mediaCameraAlbumId2 = mediaCameraAlbumId;
                    allMediaAlbum2 = allMediaAlbum;
                    if (cursor != null) {
                    }
                } catch (Throwable th7) {
                    th = th7;
                    mediaCameraAlbumId2 = mediaCameraAlbumId;
                    allMediaAlbum2 = allMediaAlbum;
                    if (cursor != null) {
                    }
                    throw th;
                }
                while (a < mediaAlbumsSorted.size()) {
                }
                MediaController.broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, mediaCameraAlbumId2, allMediaAlbum2, allPhotosAlbum, 0);
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
        Runnable c047824 = new Runnable() {
            public void run() {
                if (PhotoViewer.getInstance().isVisible()) {
                    MediaController.broadcastNewPhotos(i, arrayList, arrayList2, num, albumEntry, albumEntry2, 1000);
                    return;
                }
                MediaController.broadcastPhotosRunnable = null;
                MediaController.allPhotosAlbumEntry = albumEntry2;
                MediaController.allMediaAlbumEntry = albumEntry;
                for (int a = 0; a < 3; a++) {
                    NotificationCenter.getInstance(a).postNotificationName(NotificationCenter.albumsDidLoaded, Integer.valueOf(i), arrayList, arrayList2, num);
                }
            }
        };
        broadcastPhotosRunnable = c047824;
        AndroidUtilities.runOnUIThread(c047824, (long) delay);
    }

    public void scheduleVideoConvert(MessageObject messageObject) {
        scheduleVideoConvert(messageObject, false);
    }

    public boolean scheduleVideoConvert(MessageObject messageObject, boolean isEmpty) {
        if (messageObject == null || messageObject.videoEditedInfo == null) {
            return false;
        }
        if (isEmpty && !this.videoConvertQueue.isEmpty()) {
            return false;
        }
        if (isEmpty) {
            new File(messageObject.messageOwner.attachPath).delete();
        }
        this.videoConvertQueue.add(messageObject);
        if (this.videoConvertQueue.size() == 1) {
            startVideoConvertFromQueue();
        }
        return true;
    }

    public void cancelVideoConvert(MessageObject messageObject) {
        if (messageObject == null) {
            synchronized (this.videoConvertSync) {
                this.cancelCurrentVideoConversion = true;
            }
        } else if (!this.videoConvertQueue.isEmpty()) {
            int a = 0;
            while (a < this.videoConvertQueue.size()) {
                MessageObject object = (MessageObject) this.videoConvertQueue.get(a);
                if (object.getId() != messageObject.getId() || object.currentAccount != messageObject.currentAccount) {
                    a++;
                } else if (a == 0) {
                    synchronized (this.videoConvertSync) {
                        this.cancelCurrentVideoConversion = true;
                    }
                    return;
                } else {
                    this.videoConvertQueue.remove(a);
                    return;
                }
            }
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
        intent.putExtra("currentAccount", messageObject.currentAccount);
        if (messageObject.messageOwner.media.document != null) {
            for (int a = 0; a < messageObject.messageOwner.media.document.attributes.size(); a++) {
                if (((DocumentAttribute) messageObject.messageOwner.media.document.attributes.get(a)) instanceof TL_documentAttributeAnimated) {
                    intent.putExtra("gif", true);
                    break;
                }
            }
        }
        if (messageObject.getId() != 0) {
            try {
                ApplicationLoader.applicationContext.startService(intent);
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
        }
        VideoConvertRunnable.runConversion(messageObject);
        return true;
    }

    @SuppressLint({"NewApi"})
    public static MediaCodecInfo selectCodec(String mimeType) {
        MediaCodecInfo mediaCodecInfo;
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo lastCodecInfo = null;
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (codecInfo.isEncoder()) {
                for (String type : codecInfo.getSupportedTypes()) {
                    if (type.equalsIgnoreCase(mimeType)) {
                        lastCodecInfo = codecInfo;
                        String name = lastCodecInfo.getName();
                        if (name == null) {
                            continue;
                        } else if (!name.equals("OMX.SEC.avc.enc")) {
                            return lastCodecInfo;
                        } else if (name.equals("OMX.SEC.AVC.Encoder")) {
                            mediaCodecInfo = lastCodecInfo;
                            return lastCodecInfo;
                        }
                    }
                }
                continue;
            }
        }
        mediaCodecInfo = lastCodecInfo;
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
        int i = lastColorFormat;
        return lastColorFormat;
    }

    private int findTrack(MediaExtractor extractor, boolean audio) {
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
                    NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.FilePreparingFailed, messageObject2, file2.toString());
                    return;
                }
                if (firstWrite) {
                    NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.FilePreparingStarted, messageObject2, file2.toString());
                }
                NotificationCenter instance = NotificationCenter.getInstance(messageObject2.currentAccount);
                int i = NotificationCenter.FileNewChunkAvailable;
                Object[] objArr = new Object[4];
                objArr[0] = messageObject2;
                objArr[1] = file2.toString();
                objArr[2] = Long.valueOf(file2.length());
                objArr[3] = Long.valueOf(z2 ? file2.length() : 0);
                instance.postNotificationName(i, objArr);
            }
        });
    }

    private long readAndWriteTracks(MessageObject messageObject, MediaExtractor extractor, MP4Builder mediaMuxer, BufferInfo info, long start, long end, File file, boolean needAudio) throws Exception {
        MediaFormat trackFormat;
        int videoTrackIndex = findTrack(extractor, false);
        int audioTrackIndex = needAudio ? findTrack(extractor, true) : -1;
        int muxerVideoTrackIndex = -1;
        int muxerAudioTrackIndex = -1;
        boolean inputDone = false;
        int maxBufferSize = 0;
        if (videoTrackIndex >= 0) {
            extractor.selectTrack(videoTrackIndex);
            trackFormat = extractor.getTrackFormat(videoTrackIndex);
            muxerVideoTrackIndex = mediaMuxer.addTrack(trackFormat, false);
            maxBufferSize = trackFormat.getInteger("max-input-size");
            if (start > 0) {
                extractor.seekTo(start, 0);
            } else {
                extractor.seekTo(0, 0);
            }
        }
        if (audioTrackIndex >= 0) {
            extractor.selectTrack(audioTrackIndex);
            trackFormat = extractor.getTrackFormat(audioTrackIndex);
            muxerAudioTrackIndex = mediaMuxer.addTrack(trackFormat, true);
            maxBufferSize = Math.max(trackFormat.getInteger("max-input-size"), maxBufferSize);
            if (start > 0) {
                extractor.seekTo(start, 0);
            } else {
                extractor.seekTo(0, 0);
            }
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
        if (audioTrackIndex < 0 && videoTrackIndex < 0) {
            return -1;
        }
        long startTime = -1;
        checkConversionCanceled();
        while (!inputDone) {
            int muxerTrackIndex;
            checkConversionCanceled();
            boolean eof = false;
            info.size = extractor.readSampleData(buffer, 0);
            int index = extractor.getSampleTrackIndex();
            if (index == videoTrackIndex) {
                muxerTrackIndex = muxerVideoTrackIndex;
            } else if (index == audioTrackIndex) {
                muxerTrackIndex = muxerAudioTrackIndex;
            } else {
                muxerTrackIndex = -1;
            }
            if (muxerTrackIndex != -1) {
                if (VERSION.SDK_INT < 21) {
                    buffer.position(0);
                    buffer.limit(info.size);
                }
                if (index != audioTrackIndex) {
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
                    if (index == videoTrackIndex && start > 0 && startTime == -1) {
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
        if (videoTrackIndex >= 0) {
            extractor.unselectTrack(videoTrackIndex);
        }
        if (audioTrackIndex < 0) {
            return startTime;
        }
        extractor.unselectTrack(audioTrackIndex);
        return startTime;
    }

    private void checkConversionCanceled() {
        boolean cancelConversion;
        synchronized (this.videoConvertSync) {
            cancelConversion = this.cancelCurrentVideoConversion;
        }
        if (cancelConversion) {
            throw new RuntimeException("canceled conversion");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:189:0x051d A:{Splitter: B:122:0x03b6, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01e9 A:{Catch:{ Exception -> 0x0923, all -> 0x051d }} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01ee A:{Catch:{ Exception -> 0x0923, all -> 0x051d }} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01f3 A:{Catch:{ Exception -> 0x0923, all -> 0x051d }} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01fb A:{Catch:{ Exception -> 0x0923, all -> 0x051d }} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0206  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x020b A:{SYNTHETIC, Splitter: B:75:0x020b} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0993  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0212  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x051d A:{Splitter: B:122:0x03b6, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01e9 A:{Catch:{ Exception -> 0x0923, all -> 0x051d }} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01ee A:{Catch:{ Exception -> 0x0923, all -> 0x051d }} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01f3 A:{Catch:{ Exception -> 0x0923, all -> 0x051d }} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01fb A:{Catch:{ Exception -> 0x0923, all -> 0x051d }} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0206  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x020b A:{SYNTHETIC, Splitter: B:75:0x020b} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0212  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0993  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x051d A:{Splitter: B:122:0x03b6, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Missing block: B:90:0x027e, code:
            if (r61.equals("nokia") != false) goto L_0x0280;
     */
    /* JADX WARNING: Missing block: B:189:0x051d, code:
            r6 = th;
     */
    /* JADX WARNING: Missing block: B:190:0x051e, code:
            r49 = r50;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideo(MessageObject messageObject) {
        Throwable e;
        String videoPath = messageObject.videoEditedInfo.originalPath;
        long startTime = messageObject.videoEditedInfo.startTime;
        long endTime = messageObject.videoEditedInfo.endTime;
        int resultWidth = messageObject.videoEditedInfo.resultWidth;
        int resultHeight = messageObject.videoEditedInfo.resultHeight;
        int rotationValue = messageObject.videoEditedInfo.rotationValue;
        int originalWidth = messageObject.videoEditedInfo.originalWidth;
        int originalHeight = messageObject.videoEditedInfo.originalHeight;
        int framerate = messageObject.videoEditedInfo.framerate;
        int bitrate = messageObject.videoEditedInfo.bitrate;
        int rotateRender = 0;
        boolean isSecret = ((int) messageObject.getDialogId()) == 0;
        File file = new File(messageObject.messageOwner.attachPath);
        if (videoPath == null) {
            videoPath = TtmlNode.ANONYMOUS_REGION_ID;
        }
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
        long time = System.currentTimeMillis();
        if (resultWidth == 0 || resultHeight == 0) {
            preferences.edit().putBoolean("isPreviousOk", true).commit();
            didWriteData(messageObject, file, true, true);
            return false;
        }
        MP4Builder mediaMuxer = null;
        MediaExtractor extractor = null;
        try {
            BufferInfo info = new BufferInfo();
            Mp4Movie movie = new Mp4Movie();
            movie.setCacheFile(file);
            movie.setRotation(rotationValue);
            movie.setSize(resultWidth, resultHeight);
            mediaMuxer = new MP4Builder().createMovie(movie, isSecret);
            MediaExtractor extractor2 = new MediaExtractor();
            try {
                extractor2.setDataSource(videoPath);
                checkConversionCanceled();
                if (resultWidth == originalWidth && resultHeight == originalHeight && rotateRender == 0 && !messageObject.videoEditedInfo.roundVideo) {
                    readAndWriteTracks(messageObject, extractor2, mediaMuxer, info, startTime, endTime, file, bitrate != -1);
                } else {
                    int videoIndex = findTrack(extractor2, false);
                    int audioIndex = bitrate != -1 ? findTrack(extractor2, true) : -1;
                    if (videoIndex >= 0) {
                        MediaCodec decoder = null;
                        MediaCodec encoder = null;
                        InputSurface inputSurface = null;
                        OutputSurface outputSurface = null;
                        long videoTime = -1;
                        boolean outputDone = false;
                        boolean inputDone = false;
                        boolean decoderDone = false;
                        int swapUV = 0;
                        int videoTrackIndex = -5;
                        int audioTrackIndex = -5;
                        int processorType = 0;
                        try {
                            int colorFormat;
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
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m11d("codec = " + codecInfo.getName() + " manufacturer = " + manufacturer + "device = " + Build.MODEL);
                                }
                            } else {
                                colorFormat = NUM;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m11d("colorFormat = " + colorFormat);
                            }
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
                            MediaFormat videoFormat = extractor2.getTrackFormat(videoIndex);
                            ByteBuffer audioBuffer = null;
                            if (audioIndex >= 0) {
                                extractor2.selectTrack(audioIndex);
                                MediaFormat audioFormat = extractor2.getTrackFormat(audioIndex);
                                audioBuffer = ByteBuffer.allocateDirect(audioFormat.getInteger("max-input-size"));
                                audioTrackIndex = mediaMuxer.addTrack(audioFormat, true);
                            }
                            if (startTime > 0) {
                                extractor2.seekTo(startTime, 0);
                            } else {
                                extractor2.seekTo(0, 0);
                            }
                            MediaFormat outputFormat = MediaFormat.createVideoFormat("video/avc", resultWidth, resultHeight);
                            outputFormat.setInteger("color-format", colorFormat);
                            String str = "bitrate";
                            if (bitrate <= 0) {
                                bitrate = 921600;
                            }
                            outputFormat.setInteger(str, bitrate);
                            str = "frame-rate";
                            if (framerate == 0) {
                                framerate = 25;
                            }
                            outputFormat.setInteger(str, framerate);
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
                                    FileLog.m14e(e);
                                    error = true;
                                    extractor2.unselectTrack(videoIndex);
                                    if (outputSurface != null) {
                                    }
                                    if (inputSurface != null) {
                                    }
                                    if (decoder != null) {
                                    }
                                    if (encoder != null) {
                                    }
                                    checkConversionCanceled();
                                    if (extractor2 != null) {
                                    }
                                    if (mediaMuxer != null) {
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                    }
                                    preferences.edit().putBoolean("isPreviousOk", true).commit();
                                    didWriteData(messageObject, file, true, error);
                                    return true;
                                } catch (Throwable th) {
                                }
                            }
                            boolean errorWait;
                            try {
                                encoder.start();
                                decoder = MediaCodec.createDecoderByType(videoFormat.getString("mime"));
                                if (VERSION.SDK_INT >= 18) {
                                    outputSurface = new OutputSurface();
                                } else {
                                    outputSurface = new OutputSurface(resultWidth, resultHeight, rotateRender);
                                }
                                decoder.configure(videoFormat, outputSurface.getSurface(), null, 0);
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
                                        } else if (audioIndex != -1 && index == audioIndex) {
                                            info.size = extractor2.readSampleData(audioBuffer, 0);
                                            if (VERSION.SDK_INT < 21) {
                                                audioBuffer.position(0);
                                                audioBuffer.limit(info.size);
                                            }
                                            if (info.size >= 0) {
                                                info.presentationTimeUs = extractor2.getSampleTime();
                                                extractor2.advance();
                                            } else {
                                                info.size = 0;
                                                inputDone = true;
                                            }
                                            if (info.size > 0 && (endTime < 0 || info.presentationTimeUs < endTime)) {
                                                info.offset = 0;
                                                info.flags = extractor2.getSampleFlags();
                                                if (mediaMuxer.writeSampleData(audioTrackIndex, audioBuffer, info, false)) {
                                                    didWriteData(messageObject, file, false, false);
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
                                            MediaFormat newFormat;
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
                                                    videoTrackIndex = mediaMuxer.addTrack(newFormat, false);
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
                                                        if (mediaMuxer.writeSampleData(videoTrackIndex, encodedData, info, true)) {
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
                                                        videoTrackIndex = mediaMuxer.addTrack(newFormat, false);
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
                                                    newFormat = decoder.getOutputFormat();
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        FileLog.m11d("newFormat = " + newFormat);
                                                    }
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
                                                            if (BuildVars.LOGS_ENABLED) {
                                                                FileLog.m11d("drop frame startTime = " + startTime + " present time = " + info.presentationTimeUs);
                                                            }
                                                        } else {
                                                            videoTime = info.presentationTimeUs;
                                                        }
                                                    }
                                                    decoder.releaseOutputBuffer(decoderStatus, doRender);
                                                    if (doRender) {
                                                        errorWait = false;
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
                                                                } else if (BuildVars.LOGS_ENABLED) {
                                                                    FileLog.m11d("input buffer not available");
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if ((info.flags & 4) != 0) {
                                                        decoderOutputAvailable = false;
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            FileLog.m11d("decoder stream end");
                                                        }
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
                            } catch (Throwable e3) {
                                errorWait = true;
                                FileLog.m14e(e3);
                            } catch (Throwable th2) {
                            }
                        } catch (Exception e4) {
                            e3 = e4;
                            FileLog.m14e(e3);
                            error = true;
                            extractor2.unselectTrack(videoIndex);
                            if (outputSurface != null) {
                            }
                            if (inputSurface != null) {
                            }
                            if (decoder != null) {
                            }
                            if (encoder != null) {
                            }
                            checkConversionCanceled();
                            if (extractor2 != null) {
                            }
                            if (mediaMuxer != null) {
                            }
                            if (BuildVars.LOGS_ENABLED) {
                            }
                            preferences.edit().putBoolean("isPreviousOk", true).commit();
                            didWriteData(messageObject, file, true, error);
                            return true;
                        } catch (Throwable th22) {
                        }
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
                    }
                }
                if (extractor2 != null) {
                    extractor2.release();
                }
                if (mediaMuxer != null) {
                    try {
                        mediaMuxer.finishMovie();
                    } catch (Throwable e32) {
                        FileLog.m14e(e32);
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m11d("time = " + (System.currentTimeMillis() - time));
                    extractor = extractor2;
                } else {
                    extractor = extractor2;
                }
            } catch (Exception e5) {
                e32 = e5;
                extractor = extractor2;
            } catch (Throwable th222) {
            }
        } catch (Exception e6) {
            e32 = e6;
            error = true;
            try {
                FileLog.m14e(e32);
                if (extractor != null) {
                    extractor.release();
                }
                if (mediaMuxer != null) {
                    try {
                        mediaMuxer.finishMovie();
                    } catch (Throwable e322) {
                        FileLog.m14e(e322);
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m11d("time = " + (System.currentTimeMillis() - time));
                }
                preferences.edit().putBoolean("isPreviousOk", true).commit();
                didWriteData(messageObject, file, true, error);
                return true;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                if (extractor != null) {
                    extractor.release();
                }
                if (mediaMuxer != null) {
                    try {
                        mediaMuxer.finishMovie();
                    } catch (Throwable e3222) {
                        FileLog.m14e(e3222);
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m11d("time = " + (System.currentTimeMillis() - time));
                }
                throw th4;
            }
        }
        preferences.edit().putBoolean("isPreviousOk", true).commit();
        didWriteData(messageObject, file, true, error);
        return true;
    }
}
