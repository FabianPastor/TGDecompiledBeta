package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
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
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
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
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;
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
            Throwable e;
            boolean flush;
            final ByteBuffer finalBuffer;
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
                    double sum;
                    double sum2;
                    buffer.limit(len);
                    try {
                        float sampleStep;
                        int newPart;
                        long newSamplesCount = MediaController.this.samplesCount + ((long) (len / 2));
                        int currentPart = (int) ((((double) MediaController.this.samplesCount) / ((double) newSamplesCount)) * ((double) MediaController.this.recordSamples.length));
                        int newPart2 = MediaController.this.recordSamples.length - currentPart;
                        if (currentPart != 0) {
                            sampleStep = ((float) MediaController.this.recordSamples.length) / ((float) currentPart);
                            float currentNum = 0.0f;
                            for (int a = 0; a < currentPart; a++) {
                                MediaController.this.recordSamples[a] = MediaController.this.recordSamples[(int) currentNum];
                                currentNum += sampleStep;
                            }
                        }
                        sampleStep = 0.0f;
                        float sampleStep2 = (((float) len) / 2.0f) / ((float) newPart2);
                        sum = 0.0d;
                        sum2 = currentPart;
                        int i = 0;
                        while (i < len / 2) {
                            try {
                                int currentPart2;
                                short peak = buffer.getShort();
                                if (peak > (short) 2500) {
                                    currentPart2 = currentPart;
                                    newPart = newPart2;
                                    sum += (double) (peak * peak);
                                } else {
                                    currentPart2 = currentPart;
                                    newPart = newPart2;
                                }
                                if (i == ((int) sampleStep) && sum2 < MediaController.this.recordSamples.length) {
                                    MediaController.this.recordSamples[sum2] = peak;
                                    sampleStep += sampleStep2;
                                    sum2++;
                                }
                                i++;
                                currentPart = currentPart2;
                                newPart2 = newPart;
                            } catch (Throwable e2) {
                                e = e2;
                            }
                        }
                        newPart = newPart2;
                        MediaController.this.samplesCount = newSamplesCount;
                    } catch (Throwable e22) {
                        e = e22;
                        sum = 0.0d;
                        FileLog.m3e(e);
                        flush = false;
                        buffer.position(0);
                        sum2 = Math.sqrt((sum / ((double) len)) / 2.0d);
                        finalBuffer = buffer;
                        if (len != buffer.capacity()) {
                            flush = true;
                        }
                        if (len != 0) {
                            MediaController.this.fileEncodingQueue.postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.MediaController$1$1$1 */
                                class C02481 implements Runnable {
                                    C02481() {
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
                                    MediaController.this.recordQueue.postRunnable(new C02481());
                                }
                            });
                        }
                        MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(sum2));
                            }
                        });
                        return;
                    }
                    flush = false;
                    buffer.position(0);
                    sum2 = Math.sqrt((sum / ((double) len)) / 2.0d);
                    finalBuffer = buffer;
                    if (len != buffer.capacity()) {
                        flush = true;
                    }
                    if (len != 0) {
                        MediaController.this.fileEncodingQueue.postRunnable(/* anonymous class already generated */);
                    }
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    return;
                }
                MediaController.this.recordBuffers.add(buffer);
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
                int a = 0;
                for (int a2 = 0; a2 < 5; a2++) {
                    ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                    buffer.order(ByteOrder.nativeOrder());
                    MediaController.this.recordBuffers.add(buffer);
                }
                while (a < 3) {
                    MediaController.this.freePlayerBuffers.add(new AudioBuffer(MediaController.this.playerBufferSize));
                    a++;
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
                PhoneStateListener phoneStateListener = new C02681();
                TelephonyManager mgr = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (mgr != null) {
                    mgr.listen(phoneStateListener, 32);
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

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.refreshGalleryRunnable != null) {
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

        public void onChange(boolean selfChange) {
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
                        FileLog.m3e(e);
                    }
                }
            }).start();
        }
    }

    private native void closeOpusFile();

    private native long getTotalPcmDuration();

    public static native int isOpusFile(String str);

    private native int openOpusFile(String str);

    private native void readOpusFile(ByteBuffer byteBuffer, int i, int[] iArr);

    private native int seekOpusFile(float f);

    private native int startRecord(String str);

    private native void stopRecord();

    private native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    private void readSms() {
    }

    public static void checkGallery() {
        if (VERSION.SDK_INT >= 24) {
            if (allPhotosAlbumEntry != null) {
                final int prevSize = allPhotosAlbumEntry.photos.size();
                Utilities.globalQueue.postRunnable(new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
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
                        } catch (Throwable th) {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        }
    }

    public static MediaController getInstance() {
        MediaController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    MediaController mediaController = new MediaController();
                    localInstance = mediaController;
                    Instance = mediaController;
                }
            }
        }
        return localInstance;
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
        } catch (Throwable e222) {
            FileLog.m3e(e222);
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
            } else if (this.audioTrackPlayer != null) {
                this.audioTrackPlayer.setStereoVolume(volume, volume);
            } else if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(volume);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void startProgressTimer(final MessageObject currentPlayingMessageObject) {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
            String fileName = currentPlayingMessageObject.getFileName();
            this.progressTimer = new Timer();
            this.progressTimer.schedule(new TimerTask() {

                /* renamed from: org.telegram.messenger.MediaController$6$1 */
                class C02711 implements Runnable {
                    C02711() {
                    }

                    public void run() {
                        if (!(currentPlayingMessageObject == null || ((MediaController.this.audioPlayer == null && MediaController.this.audioTrackPlayer == null && MediaController.this.videoPlayer == null) || MediaController.this.isPaused))) {
                            try {
                                if (MediaController.this.ignoreFirstProgress != 0) {
                                    MediaController.this.ignoreFirstProgress = MediaController.this.ignoreFirstProgress - 1;
                                    return;
                                }
                                long duration;
                                long progress;
                                float bufferedValue;
                                float f = 0.0f;
                                if (MediaController.this.videoPlayer != null) {
                                    duration = MediaController.this.videoPlayer.getDuration();
                                    progress = MediaController.this.videoPlayer.getCurrentPosition();
                                    bufferedValue = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / ((float) duration);
                                    if (duration >= 0) {
                                        f = ((float) progress) / ((float) duration);
                                    }
                                    if (progress < 0 || value >= MediaController.VOLUME_NORMAL) {
                                        return;
                                    }
                                } else if (MediaController.this.audioPlayer != null) {
                                    duration = MediaController.this.audioPlayer.getDuration();
                                    progress = MediaController.this.audioPlayer.getCurrentPosition();
                                    bufferedValue = (duration == C0542C.TIME_UNSET || duration < 0) ? 0.0f : ((float) progress) / ((float) duration);
                                    float bufferedValue2 = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) duration);
                                    if (duration != C0542C.TIME_UNSET && progress >= 0) {
                                        if (MediaController.this.seekToProgressPending == 0.0f) {
                                            f = bufferedValue;
                                            bufferedValue = bufferedValue2;
                                        }
                                    }
                                    return;
                                } else {
                                    duration = 0;
                                    progress = (long) ((int) (((float) MediaController.this.lastPlayPcm) / 48.0f));
                                    f = ((float) MediaController.this.lastPlayPcm) / ((float) MediaController.this.currentTotalPcmDuration);
                                    bufferedValue = 0.0f;
                                    if (progress == MediaController.this.lastProgress) {
                                        return;
                                    }
                                }
                                MediaController.this.lastProgress = progress;
                                currentPlayingMessageObject.audioPlayerDuration = (int) (duration / 1000);
                                currentPlayingMessageObject.audioProgress = f;
                                currentPlayingMessageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                                currentPlayingMessageObject.bufferedProgress = bufferedValue;
                                NotificationCenter.getInstance(currentPlayingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(currentPlayingMessageObject.getId()), Float.valueOf(f));
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
        int a = 0;
        cleanupPlayer(false, true);
        this.audioInfo = null;
        this.playMusicAgain = false;
        while (a < 3) {
            DownloadController.getInstance(a).cleanup();
            a++;
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

    private void processMediaObserver(Uri uri) {
        MediaController mediaController = this;
        try {
            android.graphics.Point size = AndroidUtilities.getRealScreenSize();
            Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, mediaController.mediaProjections, null, null, "date_added DESC LIMIT 1");
            final ArrayList<Long> screenshotDates = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String val = TtmlNode.ANONYMOUS_REGION_ID;
                    String data = cursor.getString(null);
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(MediaController.this.lastChatAccount).postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
                        MediaController.this.checkScreenshots(screenshotDates);
                    }
                });
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
    }

    private void checkScreenshots(ArrayList<Long> dates) {
        if (!(dates == null || dates.isEmpty() || this.lastChatEnterTime == 0)) {
            if (this.lastUser != null || (this.lastSecretChat instanceof TL_encryptedChat)) {
                boolean send = false;
                for (int a = 0; a < dates.size(); a++) {
                    Long date = (Long) dates.get(a);
                    if (this.lastMediaCheckTime == 0 || date.longValue() > this.lastMediaCheckTime) {
                        if (date.longValue() >= this.lastChatEnterTime && (this.lastChatLeaveTime == 0 || date.longValue() <= this.lastChatLeaveTime + AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS)) {
                            this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, date.longValue());
                            send = true;
                        }
                    }
                }
                if (send) {
                    if (this.lastSecretChat != null) {
                        SecretChatHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastSecretChat, this.lastChatVisibleMessages, null);
                    } else {
                        SendMessagesHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastUser, this.lastMessageId, null);
                    }
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
        int a = 0;
        if (id != NotificationCenter.FileDidLoaded) {
            if (id != NotificationCenter.httpFileDidLoaded) {
                if (id == NotificationCenter.messagesDeleted) {
                    int channelId = ((Integer) args[1]).intValue();
                    ArrayList<Integer> markAsDeletedMessages = args[0];
                    if (this.playingMessageObject != null && channelId == this.playingMessageObject.messageOwner.to_id.channel_id && markAsDeletedMessages.contains(Integer.valueOf(this.playingMessageObject.getId()))) {
                        cleanupPlayer(true, true);
                    }
                    if (!(this.voiceMessagesPlaylist == null || this.voiceMessagesPlaylist.isEmpty() || channelId != ((MessageObject) this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id)) {
                        while (a < markAsDeletedMessages.size()) {
                            Integer key = (Integer) markAsDeletedMessages.get(a);
                            MessageObject messageObject = (MessageObject) this.voiceMessagesPlaylistMap.get(key.intValue());
                            this.voiceMessagesPlaylistMap.remove(key.intValue());
                            if (messageObject != null) {
                                this.voiceMessagesPlaylist.remove(messageObject);
                            }
                            a++;
                        }
                    }
                    return;
                } else if (id == NotificationCenter.removeAllMessagesFromDialog) {
                    did = ((Long) args[0]).longValue();
                    if (this.playingMessageObject != null && this.playingMessageObject.getDialogId() == did) {
                        cleanupPlayer(false, true);
                    }
                    return;
                } else if (id == NotificationCenter.musicDidLoaded) {
                    did = ((Long) args[0]).longValue();
                    if (this.playingMessageObject != null && this.playingMessageObject.isMusic() && this.playingMessageObject.getDialogId() == did) {
                        ArrayList<MessageObject> arrayList = args[1];
                        this.playlist.addAll(0, arrayList);
                        if (SharedConfig.shuffleMusic) {
                            buildShuffledPlayList();
                            this.currentPlaylistNum = 0;
                        } else {
                            this.currentPlaylistNum += arrayList.size();
                        }
                    }
                    return;
                } else if (id == NotificationCenter.didReceivedNewMessages) {
                    if (this.voiceMessagesPlaylist != null && !this.voiceMessagesPlaylist.isEmpty()) {
                        if (((Long) args[0]).longValue() == ((MessageObject) this.voiceMessagesPlaylist.get(0)).getDialogId()) {
                            ArrayList<MessageObject> arr = args[1];
                            while (a < arr.size()) {
                                MessageObject messageObject2 = (MessageObject) arr.get(a);
                                if ((messageObject2.isVoice() || messageObject2.isRoundVideo()) && (!this.voiceMessagesPlaylistUnread || (messageObject2.isContentUnread() && !messageObject2.isOut()))) {
                                    this.voiceMessagesPlaylist.add(messageObject2);
                                    this.voiceMessagesPlaylistMap.put(messageObject2.getId(), messageObject2);
                                }
                                a++;
                            }
                        }
                        return;
                    }
                    return;
                } else if (id == NotificationCenter.playerDidStartPlaying) {
                    if (!getInstance().isCurrentPlayer(args[0])) {
                        getInstance().pauseMessage(getInstance().getPlayingMessageObject());
                        return;
                    }
                    return;
                } else {
                    return;
                }
            }
        }
        String fileName = args[0];
        if (this.downloadingCurrentMessage && this.playingMessageObject != null && this.playingMessageObject.currentAccount == account && FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(fileName)) {
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
                if (count > 0) {
                    long pcm = buffer.pcmOffset;
                    final int marker = buffer.finished == 1 ? count : -1;
                    final long j = pcm;
                    final int access$5400 = MediaController.this.buffersWrited;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MediaController.this.lastPlayPcm = j;
                            if (marker != -1) {
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
        if (this.recordStartRunnable == null) {
            if (this.recordingAudio == null) {
                return false;
            }
        }
        return true;
    }

    private boolean isNearToSensor(float value) {
        return value < 5.0f && value != this.proximitySensor.getMaximumRange();
    }

    public boolean isRecordingOrListeningByProximity() {
        return this.proximityTouched && (isRecordingAudio() || (this.playingMessageObject != null && (this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo())));
    }

    public void onSensorChanged(SensorEvent event) {
        SensorEvent sensorEvent = event;
        if (this.sensorsStarted) {
            if (VoIPService.getSharedInstance() == null) {
                if (sensorEvent.sensor == r0.proximitySensor) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("proximity changed to ");
                        stringBuilder.append(sensorEvent.values[0]);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    if (r0.lastProximityValue == -100.0f) {
                        r0.lastProximityValue = sensorEvent.values[0];
                    } else if (r0.lastProximityValue != sensorEvent.values[0]) {
                        r0.proximityHasDifferentValues = true;
                    }
                    if (r0.proximityHasDifferentValues) {
                        r0.proximityTouched = isNearToSensor(sensorEvent.values[0]);
                    }
                } else if (sensorEvent.sensor == r0.accelerometerSensor) {
                    double alpha = r0.lastTimestamp == 0 ? 0.9800000190734863d : 1.0d / ((((double) (sensorEvent.timestamp - r0.lastTimestamp)) / 1.0E9d) + 1.0d);
                    r0.lastTimestamp = sensorEvent.timestamp;
                    r0.gravity[0] = (float) ((((double) r0.gravity[0]) * alpha) + ((1.0d - alpha) * ((double) sensorEvent.values[0])));
                    r0.gravity[1] = (float) ((((double) r0.gravity[1]) * alpha) + ((1.0d - alpha) * ((double) sensorEvent.values[1])));
                    r0.gravity[2] = (float) ((((double) r0.gravity[2]) * alpha) + ((1.0d - alpha) * ((double) sensorEvent.values[2])));
                    r0.gravityFast[0] = (r0.gravity[0] * 0.8f) + (sensorEvent.values[0] * 0.19999999f);
                    r0.gravityFast[1] = (r0.gravity[1] * 0.8f) + (sensorEvent.values[1] * 0.19999999f);
                    r0.gravityFast[2] = (0.8f * r0.gravity[2]) + (0.19999999f * sensorEvent.values[2]);
                    r0.linearAcceleration[0] = sensorEvent.values[0] - r0.gravity[0];
                    r0.linearAcceleration[1] = sensorEvent.values[1] - r0.gravity[1];
                    r0.linearAcceleration[2] = sensorEvent.values[2] - r0.gravity[2];
                } else if (sensorEvent.sensor == r0.linearSensor) {
                    r0.linearAcceleration[0] = sensorEvent.values[0];
                    r0.linearAcceleration[1] = sensorEvent.values[1];
                    r0.linearAcceleration[2] = sensorEvent.values[2];
                } else if (sensorEvent.sensor == r0.gravitySensor) {
                    float[] fArr = r0.gravityFast;
                    float[] fArr2 = r0.gravity;
                    float f = sensorEvent.values[0];
                    fArr2[0] = f;
                    fArr[0] = f;
                    fArr = r0.gravityFast;
                    fArr2 = r0.gravity;
                    f = sensorEvent.values[1];
                    fArr2[1] = f;
                    fArr[1] = f;
                    fArr = r0.gravityFast;
                    fArr2 = r0.gravity;
                    f = sensorEvent.values[2];
                    fArr2[2] = f;
                    fArr[2] = f;
                }
                if (sensorEvent.sensor == r0.linearSensor || sensorEvent.sensor == r0.gravitySensor || sensorEvent.sensor == r0.accelerometerSensor) {
                    boolean goodValue;
                    float val = ((r0.gravity[0] * r0.linearAcceleration[0]) + (r0.gravity[1] * r0.linearAcceleration[1])) + (r0.gravity[2] * r0.linearAcceleration[2]);
                    if (r0.raisedToBack != 6 && ((val > 0.0f && r0.previousAccValue > 0.0f) || (val < 0.0f && r0.previousAccValue < 0.0f))) {
                        int sign;
                        if (val > 0.0f) {
                            goodValue = val > 15.0f;
                            sign = 1;
                        } else {
                            goodValue = val < -15.0f;
                            sign = 2;
                        }
                        if (r0.raisedToTopSign == 0 || r0.raisedToTopSign == sign) {
                            if (!goodValue || r0.raisedToBack != 0 || (r0.raisedToTopSign != 0 && r0.raisedToTopSign != sign)) {
                                if (!goodValue) {
                                    r0.countLess++;
                                }
                                if (!(r0.raisedToTopSign == sign && r0.countLess != 10 && r0.raisedToTop == 6 && r0.raisedToBack == 0)) {
                                    r0.raisedToBack = 0;
                                    r0.raisedToTop = 0;
                                    r0.raisedToTopSign = 0;
                                    r0.countLess = 0;
                                }
                            } else if (r0.raisedToTop < 6 && !r0.proximityTouched) {
                                r0.raisedToTopSign = sign;
                                r0.raisedToTop++;
                                if (r0.raisedToTop == 6) {
                                    r0.countLess = 0;
                                }
                            }
                        } else if (r0.raisedToTop != 6 || !goodValue) {
                            if (!goodValue) {
                                r0.countLess++;
                            }
                            if (!(r0.countLess != 10 && r0.raisedToTop == 6 && r0.raisedToBack == 0)) {
                                r0.raisedToTop = 0;
                                r0.raisedToTopSign = 0;
                                r0.raisedToBack = 0;
                                r0.countLess = 0;
                            }
                        } else if (r0.raisedToBack < 6) {
                            r0.raisedToBack++;
                            if (r0.raisedToBack == 6) {
                                r0.raisedToTop = 0;
                                r0.raisedToTopSign = 0;
                                r0.countLess = 0;
                                r0.timeSinceRaise = System.currentTimeMillis();
                                if (BuildVars.LOGS_ENABLED && BuildVars.DEBUG_PRIVATE_VERSION) {
                                    FileLog.m0d("motion detected");
                                }
                            }
                        }
                    }
                    r0.previousAccValue = val;
                    goodValue = r0.gravityFast[1] > 2.5f && Math.abs(r0.gravityFast[2]) < 4.0f && Math.abs(r0.gravityFast[0]) > 1.5f;
                    r0.accelerometerVertical = goodValue;
                }
                if (r0.raisedToBack == 6 && r0.accelerometerVertical && r0.proximityTouched && !NotificationsController.audioManager.isWiredHeadsetOn()) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("sensor values reached");
                    }
                    if (r0.playingMessageObject == null && r0.recordStartRunnable == null && r0.recordingAudio == null && !PhotoViewer.getInstance().isVisible() && ApplicationLoader.isScreenOn && !r0.inputFieldHasText && r0.allowStartRecord && r0.raiseChat != null && !r0.callInProgress) {
                        if (!r0.raiseToEarRecord) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start record");
                            }
                            r0.useFrontSpeaker = true;
                            if (!r0.raiseChat.playFirstUnreadVoiceMessage()) {
                                r0.raiseToEarRecord = true;
                                r0.useFrontSpeaker = false;
                                startRecording(r0.raiseChat.getCurrentAccount(), r0.raiseChat.getDialogId(), null);
                            }
                            if (r0.useFrontSpeaker) {
                                setUseFrontSpeaker(true);
                            }
                            r0.ignoreOnPause = true;
                            if (!(!r0.proximityHasDifferentValues || r0.proximityWakeLock == null || r0.proximityWakeLock.isHeld())) {
                                r0.proximityWakeLock.acquire();
                            }
                        }
                    } else if (r0.playingMessageObject != null && ((r0.playingMessageObject.isVoice() || r0.playingMessageObject.isRoundVideo()) && !r0.useFrontSpeaker)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("start listen");
                        }
                        if (!(!r0.proximityHasDifferentValues || r0.proximityWakeLock == null || r0.proximityWakeLock.isHeld())) {
                            r0.proximityWakeLock.acquire();
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        r0.ignoreOnPause = true;
                    }
                    r0.raisedToBack = 0;
                    r0.raisedToTop = 0;
                    r0.raisedToTopSign = 0;
                    r0.countLess = 0;
                } else if (r0.proximityTouched) {
                    if (r0.playingMessageObject != null && ((r0.playingMessageObject.isVoice() || r0.playingMessageObject.isRoundVideo()) && !r0.useFrontSpeaker)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("start listen by proximity only");
                        }
                        if (!(!r0.proximityHasDifferentValues || r0.proximityWakeLock == null || r0.proximityWakeLock.isHeld())) {
                            r0.proximityWakeLock.acquire();
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        r0.ignoreOnPause = true;
                    }
                } else if (!r0.proximityTouched) {
                    if (r0.raiseToEarRecord) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("stop record");
                        }
                        stopRecording(2);
                        r0.raiseToEarRecord = false;
                        r0.ignoreOnPause = false;
                        if (r0.proximityHasDifferentValues && r0.proximityWakeLock != null && r0.proximityWakeLock.isHeld()) {
                            r0.proximityWakeLock.release();
                        }
                    } else if (r0.useFrontSpeaker) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("stop listen");
                        }
                        r0.useFrontSpeaker = false;
                        startAudioAgain(true);
                        r0.ignoreOnPause = false;
                        if (r0.proximityHasDifferentValues && r0.proximityWakeLock != null && r0.proximityWakeLock.isHeld()) {
                            r0.proximityWakeLock.release();
                        }
                    }
                }
                if (r0.timeSinceRaise != 0 && r0.raisedToBack == 6 && Math.abs(System.currentTimeMillis() - r0.timeSinceRaise) > 1000) {
                    r0.raisedToBack = 0;
                    r0.raisedToTop = 0;
                    r0.raisedToTopSign = 0;
                    r0.countLess = 0;
                    r0.timeSinceRaise = 0;
                }
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
        if (this.useFrontSpeaker && this.raiseChat != null) {
            if (this.allowStartRecord) {
                this.raiseToEarRecord = true;
                startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null);
                this.ignoreOnPause = true;
            }
        }
    }

    private void startAudioAgain(boolean paused) {
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
                if (paused) {
                    this.videoPlayer.pause();
                } else {
                    this.videoPlayer.play();
                }
            } else {
                boolean post = this.audioPlayer != null;
                final MessageObject currentMessageObject = this.playingMessageObject;
                float progress = this.playingMessageObject.audioProgress;
                cleanupPlayer(false, true);
                currentMessageObject.audioProgress = progress;
                playMessage(currentMessageObject);
                if (paused) {
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startRaiseToEarSensors(ChatActivity chatActivity) {
        if (!(chatActivity == null || (this.accelerometerSensor == null && (this.gravitySensor == null || this.linearAcceleration == null)))) {
            if (this.proximitySensor != null) {
                this.raiseChat = chatActivity;
                if ((SharedConfig.raiseToSpeak || (this.playingMessageObject != null && (this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo()))) && !this.sensorsStarted) {
                    float[] fArr = this.gravity;
                    float[] fArr2 = this.gravity;
                    this.gravity[2] = 0.0f;
                    fArr2[1] = 0.0f;
                    fArr[0] = 0.0f;
                    fArr = this.linearAcceleration;
                    float[] fArr3 = this.linearAcceleration;
                    this.linearAcceleration[2] = 0.0f;
                    fArr3[1] = 0.0f;
                    fArr[0] = 0.0f;
                    fArr = this.gravityFast;
                    fArr3 = this.gravityFast;
                    this.gravityFast[2] = 0.0f;
                    fArr3[1] = 0.0f;
                    fArr[0] = 0.0f;
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
            ChatActivity chat = this.raiseChat;
            stopRaiseToEarSensors(this.raiseChat);
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
                if (this.voiceMessagesPlaylist != null) {
                    if (byVoiceEnd && this.voiceMessagesPlaylist.get(0) == lastFile) {
                        this.voiceMessagesPlaylist.remove(0);
                        this.voiceMessagesPlaylistMap.remove(lastFile.getId());
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
                    NotificationCenter.getInstance(lastFile.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(lastFile.getId()), Boolean.valueOf(stopService));
                    this.pipSwitchingState = 0;
                    if (this.pipRoundVideoView != null) {
                        this.pipRoundVideoView.close(true);
                        this.pipRoundVideoView = null;
                    }
                }
            }
            if (stopService) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                return;
            }
            return;
        }
        return;
    }

    private void seekOpusPlayer(final float progress) {
        if (progress != VOLUME_NORMAL) {
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
                            MediaController.this.lastPlayPcm = (long) (((float) MediaController.this.currentTotalPcmDuration) * progress);
                            if (MediaController.this.audioTrackPlayer != null) {
                                MediaController.this.audioTrackPlayer.play();
                            }
                            MediaController.this.lastProgress = (long) ((int) ((((float) MediaController.this.currentTotalPcmDuration) / 48.0f) * progress));
                            MediaController.this.checkPlayerQueue();
                        }
                    }
                }

                public void run() {
                    MediaController.this.seekOpusFile(progress);
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
        return ((this.playingMessageObject.eventId > 0 ? 1 : (this.playingMessageObject.eventId == 0 ? 0 : -1)) == 0) == ((messageObject.eventId > 0 ? 1 : (messageObject.eventId == 0 ? 0 : -1)) == 0);
    }

    public boolean seekToProgress(MessageObject messageObject, float progress) {
        if (!((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null)) {
            if (isSamePlayingMessage(messageObject)) {
                try {
                    if (this.audioPlayer != null) {
                        long duration = this.audioPlayer.getDuration();
                        if (duration == C0542C.TIME_UNSET) {
                            this.seekToProgressPending = progress;
                        } else {
                            int seekTo = (int) (((float) duration) * progress);
                            this.audioPlayer.seekTo((long) seekTo);
                            this.lastProgress = (long) seekTo;
                        }
                    } else if (this.audioTrackPlayer != null) {
                        seekOpusPlayer(progress);
                    } else if (this.videoPlayer != null) {
                        this.videoPlayer.seekTo((long) (((float) this.videoPlayer.getDuration()) * progress));
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
        if (this.playingMessageObject == current) {
            return playMessage(current);
        }
        this.forceLoopCurrentPlaylist = loadMusic ^ 1;
        this.playMusicAgain = this.playlist.isEmpty() ^ 1;
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
        if (this.currentPlaylistNum >= 0) {
            if (this.currentPlaylistNum < this.playlist.size()) {
                this.currentPlaylistNum = index;
                this.playMusicAgain = true;
                if (this.playingMessageObject != null) {
                    this.playingMessageObject.resetPlayingProgress();
                }
                playMessage((MessageObject) this.playlist.get(this.currentPlaylistNum));
            }
        }
    }

    private void playNextMessageWithoutOrder(boolean byStop) {
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (byStop && SharedConfig.repeatMode == 2 && !this.forceLoopCurrentPlaylist) {
            cleanupPlayer(false, false);
            playMessage((MessageObject) currentPlayList.get(this.currentPlaylistNum));
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
            if (!(this.audioPlayer == null && this.audioTrackPlayer == null && this.videoPlayer == null)) {
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
                this.isPaused = true;
                this.playingMessageObject.audioProgress = 0.0f;
                this.playingMessageObject.audioProgressSec = 0;
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            }
            return;
        }
        if (this.currentPlaylistNum >= 0) {
            if (this.currentPlaylistNum < currentPlayList.size()) {
                if (this.playingMessageObject != null) {
                    this.playingMessageObject.resetPlayingProgress();
                }
                this.playMusicAgain = true;
                playMessage((MessageObject) currentPlayList.get(this.currentPlaylistNum));
                return;
            }
        }
        return;
    }

    public void playPreviousMessage() {
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!currentPlayList.isEmpty() && this.currentPlaylistNum >= 0) {
            if (this.currentPlaylistNum < currentPlayList.size()) {
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
                if (this.currentPlaylistNum >= 0) {
                    if (this.currentPlaylistNum < currentPlayList.size()) {
                        this.playMusicAgain = true;
                        playMessage((MessageObject) currentPlayList.get(this.currentPlaylistNum));
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

    private void checkIsNextVoiceFileDownloaded(int currentAccount) {
        if (this.voiceMessagesPlaylist != null) {
            if (this.voiceMessagesPlaylist.size() >= 2) {
                MessageObject nextAudio = (MessageObject) this.voiceMessagesPlaylist.get(1);
                File file = null;
                if (nextAudio.messageOwner.attachPath != null && nextAudio.messageOwner.attachPath.length() > 0) {
                    file = new File(nextAudio.messageOwner.attachPath);
                    if (!file.exists()) {
                        file = null;
                    }
                }
                File cacheFile = file != null ? file : FileLoader.getPathToMessage(nextAudio.messageOwner);
                if (cacheFile == null || !cacheFile.exists()) {
                    boolean exist = false;
                }
                if (!(cacheFile == null || cacheFile == file || cacheFile.exists())) {
                    FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), false, 0);
                }
            }
        }
    }

    private void checkIsNextMusicFileDownloaded(int currentAccount) {
        if ((DownloadController.getInstance(currentAccount).getCurrentDownloadMask() & 16) != 0) {
            ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
            if (currentPlayList != null) {
                if (currentPlayList.size() >= 2) {
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
                            boolean exist = false;
                        }
                        if (!(cacheFile == null || cacheFile == file || cacheFile.exists() || !nextAudio.isMusic())) {
                            FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), false, 0);
                        }
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
        int result;
        if (!messageObject.isVoice()) {
            if (!messageObject.isRoundVideo()) {
                neededAudioFocus = 1;
                if (this.hasAudioFocus != neededAudioFocus) {
                    this.hasAudioFocus = neededAudioFocus;
                    if (neededAudioFocus != 3) {
                        result = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
                    } else {
                        result = NotificationsController.audioManager.requestAudioFocus(this, 3, neededAudioFocus != 2 ? 3 : 1);
                    }
                    if (result == 1) {
                        this.audioFocus = 2;
                    }
                }
            }
        }
        if (this.useFrontSpeaker) {
            neededAudioFocus = 3;
        } else {
            neededAudioFocus = 2;
        }
        if (this.hasAudioFocus != neededAudioFocus) {
            this.hasAudioFocus = neededAudioFocus;
            if (neededAudioFocus != 3) {
                if (neededAudioFocus != 2) {
                }
                result = NotificationsController.audioManager.requestAudioFocus(this, 3, neededAudioFocus != 2 ? 3 : 1);
            } else {
                result = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
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
        if (textureView != null) {
            boolean z = true;
            if (set || this.currentTextureView != textureView) {
                if (this.videoPlayer != null) {
                    if (textureView != this.currentTextureView) {
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean playMessage(MessageObject messageObject) {
        Throwable e;
        NotificationCenter instance;
        Object[] objArr;
        Throwable e2;
        MediaController mediaController = this;
        final MessageObject messageObject2 = messageObject;
        if (messageObject2 == null) {
            return false;
        }
        if (!(mediaController.audioTrackPlayer == null && mediaController.audioPlayer == null && mediaController.videoPlayer == null) && isSamePlayingMessage(messageObject)) {
            if (mediaController.isPaused) {
                resumeAudio(messageObject);
            }
            if (!SharedConfig.raiseToSpeak) {
                startRaiseToEarSensors(mediaController.raiseChat);
            }
            return true;
        }
        if (!messageObject.isOut() && messageObject.isContentUnread()) {
            MessagesController.getInstance(messageObject2.currentAccount).markMessageContentAsRead(messageObject2);
        }
        boolean notify = mediaController.playMusicAgain ^ true;
        if (mediaController.playingMessageObject != null) {
            notify = false;
            if (!mediaController.playMusicAgain) {
                mediaController.playingMessageObject.resetPlayingProgress();
            }
        }
        cleanupPlayer(notify, false);
        mediaController.playMusicAgain = false;
        mediaController.seekToProgressPending = 0.0f;
        File file = null;
        boolean exists = false;
        if (messageObject2.messageOwner.attachPath != null && messageObject2.messageOwner.attachPath.length() > 0) {
            file = new File(messageObject2.messageOwner.attachPath);
            exists = file.exists();
            if (!exists) {
                file = null;
            }
        }
        final File cacheFile = file != null ? file : FileLoader.getPathToMessage(messageObject2.messageOwner);
        boolean canStream = SharedConfig.streamMedia && messageObject.isMusic() && ((int) messageObject.getDialogId()) != 0;
        if (!(cacheFile == null || cacheFile == file)) {
            boolean exists2 = cacheFile.exists();
            exists = exists2;
            if (!(exists2 || canStream)) {
                FileLoader.getInstance(messageObject2.currentAccount).loadFile(messageObject.getDocument(), false, 0);
                mediaController.downloadingCurrentMessage = true;
                mediaController.isPaused = false;
                mediaController.lastProgress = 0;
                mediaController.lastPlayPcm = 0;
                mediaController.audioInfo = null;
                mediaController.playingMessageObject = messageObject2;
                if (mediaController.playingMessageObject.isMusic()) {
                    try {
                        ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                    } catch (Throwable th) {
                        FileLog.m3e(th);
                    }
                } else {
                    ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                }
                NotificationCenter.getInstance(mediaController.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(mediaController.playingMessageObject.getId()));
                return true;
            }
        }
        mediaController.downloadingCurrentMessage = false;
        if (messageObject.isMusic()) {
            checkIsNextMusicFileDownloaded(messageObject2.currentAccount);
        } else {
            checkIsNextVoiceFileDownloaded(messageObject2.currentAccount);
        }
        if (mediaController.currentAspectRatioFrameLayout != null) {
            mediaController.isDrawingWasReady = false;
            mediaController.currentAspectRatioFrameLayout.setDrawingReady(false);
        }
        int i = 3;
        if (messageObject.isRoundVideo()) {
            VideoPlayer videoPlayer;
            mediaController.playlist.clear();
            mediaController.shuffledPlaylist.clear();
            mediaController.videoPlayer = new VideoPlayer();
            mediaController.videoPlayer.setDelegate(new VideoPlayerDelegate() {

                /* renamed from: org.telegram.messenger.MediaController$16$1 */
                class C02531 implements Runnable {
                    C02531() {
                    }

                    public void run() {
                        MediaController.this.cleanupPlayer(true, true);
                    }
                }

                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    if (MediaController.this.videoPlayer != null) {
                        if (playbackState == 4 || playbackState == 1) {
                            try {
                                MediaController.this.baseActivity.getWindow().clearFlags(128);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        } else {
                            try {
                                MediaController.this.baseActivity.getWindow().addFlags(128);
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                        }
                        if (playbackState == 3) {
                            MediaController.this.currentAspectRatioFrameLayoutReady = true;
                            if (!(MediaController.this.currentTextureViewContainer == null || MediaController.this.currentTextureViewContainer.getVisibility() == 0)) {
                                MediaController.this.currentTextureViewContainer.setVisibility(0);
                            }
                        } else if (MediaController.this.videoPlayer.isPlaying() && playbackState == 4) {
                            MediaController.this.cleanupPlayer(true, true, true);
                        }
                    }
                }

                public void onError(Exception e) {
                    FileLog.m3e((Throwable) e);
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
                                    MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new C02531());
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
            mediaController.currentAspectRatioFrameLayoutReady = false;
            if (mediaController.pipRoundVideoView == null) {
                if (MessagesController.getInstance(messageObject2.currentAccount).isDialogCreated(messageObject.getDialogId())) {
                    if (mediaController.currentTextureView != null) {
                        mediaController.videoPlayer.setTextureView(mediaController.currentTextureView);
                    }
                    mediaController.videoPlayer.preparePlayer(Uri.fromFile(cacheFile), "other");
                    videoPlayer = mediaController.videoPlayer;
                    if (mediaController.useFrontSpeaker) {
                        i = 0;
                    }
                    videoPlayer.setStreamType(i);
                    mediaController.videoPlayer.play();
                }
            }
            if (mediaController.pipRoundVideoView == null) {
                try {
                    mediaController.pipRoundVideoView = new PipRoundVideoView();
                    mediaController.pipRoundVideoView.show(mediaController.baseActivity, new Runnable() {
                        public void run() {
                            MediaController.this.cleanupPlayer(true, true);
                        }
                    });
                } catch (Exception e3) {
                    mediaController.pipRoundVideoView = null;
                }
            }
            if (mediaController.pipRoundVideoView != null) {
                mediaController.videoPlayer.setTextureView(mediaController.pipRoundVideoView.getTextureView());
            }
            mediaController.videoPlayer.preparePlayer(Uri.fromFile(cacheFile), "other");
            videoPlayer = mediaController.videoPlayer;
            if (mediaController.useFrontSpeaker) {
                i = 0;
            }
            videoPlayer.setStreamType(i);
            mediaController.videoPlayer.play();
        } else {
            File file2;
            if (messageObject.isMusic() || isOpusFile(cacheFile.getAbsolutePath()) != 1) {
                file2 = file;
                if (mediaController.pipRoundVideoView != null) {
                    mediaController.pipRoundVideoView.close(true);
                    mediaController.pipRoundVideoView = null;
                }
                try {
                    mediaController.audioPlayer = new VideoPlayer();
                    mediaController.audioPlayer.setStreamType(mediaController.useFrontSpeaker ? 0 : 3);
                    mediaController.audioPlayer.setDelegate(new VideoPlayerDelegate() {
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
                        if (messageObject2.mediaExists) {
                        } else if (cacheFile != file2) {
                            try {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.FileDidLoaded, FileLoader.getAttachFileName(messageObject2.getDocument()));
                                    }
                                });
                            } catch (Throwable th2) {
                                e = th2;
                                FileLog.m3e(e);
                                instance = NotificationCenter.getInstance(messageObject2.currentAccount);
                                i = NotificationCenter.messagePlayingPlayStateChanged;
                                objArr = new Object[1];
                                objArr[0] = Integer.valueOf(mediaController.playingMessageObject == null ? 0 : mediaController.playingMessageObject.getId());
                                instance.postNotificationName(i, objArr);
                                if (mediaController.audioPlayer != null) {
                                    mediaController.audioPlayer.releasePlayer();
                                    mediaController.audioPlayer = null;
                                    mediaController.isPaused = false;
                                    mediaController.playingMessageObject = null;
                                    mediaController.downloadingCurrentMessage = false;
                                }
                                return false;
                            }
                        }
                        mediaController.audioPlayer.preparePlayer(Uri.fromFile(cacheFile), "other");
                    } else {
                        Document document = messageObject.getDocument();
                        String params = new StringBuilder();
                        params.append("?account=");
                        params.append(messageObject2.currentAccount);
                        params.append("&id=");
                        params.append(document.id);
                        params.append("&hash=");
                        params.append(document.access_hash);
                        params.append("&dc=");
                        params.append(document.dc_id);
                        params.append("&size=");
                        params.append(document.size);
                        params.append("&mime=");
                        params.append(URLEncoder.encode(document.mime_type, C0542C.UTF8_NAME));
                        params.append("&name=");
                        params.append(URLEncoder.encode(FileLoader.getDocumentFileName(document), C0542C.UTF8_NAME));
                        params = params.toString();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("tg://");
                        stringBuilder.append(messageObject.getFileName());
                        stringBuilder.append(params);
                        mediaController.audioPlayer.preparePlayer(Uri.parse(stringBuilder.toString()), "other");
                    }
                    mediaController.audioPlayer.play();
                    if (messageObject.isVoice()) {
                        mediaController.audioInfo = null;
                        mediaController.playlist.clear();
                        mediaController.shuffledPlaylist.clear();
                    } else {
                        try {
                            mediaController.audioInfo = AudioInfo.getAudioInfo(cacheFile);
                        } catch (Throwable th22) {
                            FileLog.m3e(th22);
                        }
                    }
                } catch (Throwable th222) {
                    file = file2;
                    e = th222;
                    FileLog.m3e(e);
                    instance = NotificationCenter.getInstance(messageObject2.currentAccount);
                    i = NotificationCenter.messagePlayingPlayStateChanged;
                    objArr = new Object[1];
                    if (mediaController.playingMessageObject == null) {
                    }
                    objArr[0] = Integer.valueOf(mediaController.playingMessageObject == null ? 0 : mediaController.playingMessageObject.getId());
                    instance.postNotificationName(i, objArr);
                    if (mediaController.audioPlayer != null) {
                        mediaController.audioPlayer.releasePlayer();
                        mediaController.audioPlayer = null;
                        mediaController.isPaused = false;
                        mediaController.playingMessageObject = null;
                        mediaController.downloadingCurrentMessage = false;
                    }
                    return false;
                }
            }
            if (mediaController.pipRoundVideoView != null) {
                mediaController.pipRoundVideoView.close(true);
                mediaController.pipRoundVideoView = null;
            }
            mediaController.playlist.clear();
            mediaController.shuffledPlaylist.clear();
            synchronized (mediaController.playerObjectSync) {
                try {
                    mediaController.ignoreFirstProgress = 3;
                    final CountDownLatch countDownLatch = new CountDownLatch(1);
                    final Boolean[] result = new Boolean[1];
                    mediaController.fileDecodingQueue.postRunnable(new Runnable() {
                        public void run() {
                            result[0] = Boolean.valueOf(MediaController.this.openOpusFile(cacheFile.getAbsolutePath()) != 0);
                            countDownLatch.countDown();
                        }
                    });
                    countDownLatch.await();
                    if (result[0].booleanValue()) {
                        file2 = file;
                        try {
                            mediaController.currentTotalPcmDuration = getTotalPcmDuration();
                            mediaController.audioTrackPlayer = new AudioTrack(mediaController.useFrontSpeaker ? 0 : 3, 48000, 4, 2, mediaController.playerBufferSize, 1);
                            mediaController.audioTrackPlayer.setStereoVolume(VOLUME_NORMAL, VOLUME_NORMAL);
                            mediaController.audioTrackPlayer.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
                                public void onMarkerReached(AudioTrack audioTrack) {
                                    MediaController.this.cleanupPlayer(true, true, true);
                                }

                                public void onPeriodicNotification(AudioTrack audioTrack) {
                                }
                            });
                            mediaController.audioTrackPlayer.play();
                        } catch (Throwable th2222) {
                            e2 = th2222;
                            FileLog.m3e(e2);
                            if (mediaController.audioTrackPlayer != null) {
                                mediaController.audioTrackPlayer.release();
                                mediaController.audioTrackPlayer = null;
                                mediaController.isPaused = false;
                                mediaController.playingMessageObject = null;
                                mediaController.downloadingCurrentMessage = false;
                            }
                            return false;
                        } catch (Throwable th22222) {
                            e2 = th22222;
                            throw e2;
                        }
                    }
                    try {
                        return false;
                    } catch (Throwable th222222) {
                        e2 = th222222;
                        file2 = file;
                        throw e2;
                    }
                } catch (Throwable th2222222) {
                    file2 = file;
                    e2 = th2222222;
                    FileLog.m3e(e2);
                    if (mediaController.audioTrackPlayer != null) {
                        mediaController.audioTrackPlayer.release();
                        mediaController.audioTrackPlayer = null;
                        mediaController.isPaused = false;
                        mediaController.playingMessageObject = null;
                        mediaController.downloadingCurrentMessage = false;
                    }
                    return false;
                } catch (Throwable th22222222) {
                    file2 = file;
                    e2 = th22222222;
                    throw e2;
                }
            }
        }
        checkAudioFocus(messageObject);
        setPlayerVolume();
        mediaController.isPaused = false;
        mediaController.lastProgress = 0;
        mediaController.lastPlayPcm = 0;
        mediaController.playingMessageObject = messageObject2;
        if (!SharedConfig.raiseToSpeak) {
            startRaiseToEarSensors(mediaController.raiseChat);
        }
        startProgressTimer(mediaController.playingMessageObject);
        NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidStarted, messageObject2);
        long duration;
        if (mediaController.videoPlayer != null) {
            try {
                if (mediaController.playingMessageObject.audioProgress != 0.0f) {
                    duration = mediaController.audioPlayer.getDuration();
                    if (duration == C0542C.TIME_UNSET) {
                        duration = (long) mediaController.playingMessageObject.getDuration();
                    }
                    mediaController.videoPlayer.seekTo((long) ((int) (((float) duration) * mediaController.playingMessageObject.audioProgress)));
                }
            } catch (Throwable th222222222) {
                e = th222222222;
                mediaController.playingMessageObject.audioProgress = 0.0f;
                mediaController.playingMessageObject.audioProgressSec = 0;
                NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(mediaController.playingMessageObject.getId()), Integer.valueOf(0));
                FileLog.m3e(e);
            }
        } else if (mediaController.audioPlayer != null) {
            try {
                if (mediaController.playingMessageObject.audioProgress != 0.0f) {
                    duration = mediaController.audioPlayer.getDuration();
                    if (duration == C0542C.TIME_UNSET) {
                        duration = (long) mediaController.playingMessageObject.getDuration();
                    }
                    mediaController.audioPlayer.seekTo((long) ((int) (((float) duration) * mediaController.playingMessageObject.audioProgress)));
                }
            } catch (Throwable thNUM) {
                e = thNUM;
                mediaController.playingMessageObject.resetPlayingProgress();
                NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(mediaController.playingMessageObject.getId()), Integer.valueOf(0));
                FileLog.m3e(e);
            }
        } else if (mediaController.audioTrackPlayer != null) {
            if (mediaController.playingMessageObject.audioProgress == VOLUME_NORMAL) {
                mediaController.playingMessageObject.audioProgress = 0.0f;
            }
            mediaController.fileDecodingQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        if (!(MediaController.this.playingMessageObject == null || MediaController.this.playingMessageObject.audioProgress == 0.0f)) {
                            MediaController.this.lastPlayPcm = (long) (((float) MediaController.this.currentTotalPcmDuration) * MediaController.this.playingMessageObject.audioProgress);
                            MediaController.this.seekOpusFile(MediaController.this.playingMessageObject.audioProgress);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
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
        if (mediaController.playingMessageObject.isMusic()) {
            try {
                ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            } catch (Throwable th2NUM) {
                FileLog.m3e(th2NUM);
            }
        } else {
            ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
        }
        return true;
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
        if (this.videoPlayer != player) {
            if (this.audioPlayer != player) {
                return false;
            }
        }
        return true;
    }

    public boolean pauseMessage(MessageObject messageObject) {
        if (!((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null)) {
            if (isSamePlayingMessage(messageObject)) {
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
                if (isSamePlayingMessage(messageObject)) {
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

    public void setReplyingMessage(MessageObject reply_to_msg) {
        this.recordReplyingMessageObject = reply_to_msg;
    }

    public void startRecording(int currentAccount, long dialog_id, MessageObject reply_to_msg) {
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
        final long j = dialog_id;
        final MessageObject messageObject = reply_to_msg;
        Runnable anonymousClass23 = new Runnable() {

            /* renamed from: org.telegram.messenger.MediaController$23$1 */
            class C02551 implements Runnable {
                C02551() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
            }

            /* renamed from: org.telegram.messenger.MediaController$23$2 */
            class C02562 implements Runnable {
                C02562() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
            }

            /* renamed from: org.telegram.messenger.MediaController$23$3 */
            class C02573 implements Runnable {
                C02573() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
            }

            /* renamed from: org.telegram.messenger.MediaController$23$4 */
            class C02584 implements Runnable {
                C02584() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStarted, new Object[0]);
                }
            }

            public void run() {
                if (MediaController.this.audioRecorder != null) {
                    AndroidUtilities.runOnUIThread(new C02551());
                    return;
                }
                MediaController.this.recordingAudio = new TL_document();
                MediaController.this.recordingAudio.dc_id = Integer.MIN_VALUE;
                MediaController.this.recordingAudio.id = (long) SharedConfig.getLastLocalId();
                MediaController.this.recordingAudio.user_id = UserConfig.getInstance(i).getClientUserId();
                MediaController.this.recordingAudio.mime_type = "audio/ogg";
                MediaController.this.recordingAudio.thumb = new TL_photoSizeEmpty();
                MediaController.this.recordingAudio.thumb.type = "s";
                SharedConfig.saveConfig();
                MediaController.this.recordingAudioFile = new File(FileLoader.getDirectory(4), FileLoader.getAttachFileName(MediaController.this.recordingAudio));
                try {
                    if (MediaController.this.startRecord(MediaController.this.recordingAudioFile.getAbsolutePath()) == 0) {
                        AndroidUtilities.runOnUIThread(new C02562());
                        return;
                    }
                    MediaController.this.audioRecorder = new AudioRecord(1, 16000, 16, 2, MediaController.this.recordBufferSize * 10);
                    MediaController.this.recordStartTime = System.currentTimeMillis();
                    MediaController.this.recordTimeCount = 0;
                    MediaController.this.samplesCount = 0;
                    MediaController.this.recordDialogId = j;
                    MediaController.this.recordingCurrentAccount = i;
                    MediaController.this.recordReplyingMessageObject = messageObject;
                    MediaController.this.fileBuffer.rewind();
                    MediaController.this.audioRecorder.startRecording();
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(new C02584());
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    MediaController.this.recordingAudio = null;
                    MediaController.this.stopRecord();
                    MediaController.this.recordingAudioFile.delete();
                    MediaController.this.recordingAudioFile = null;
                    try {
                        MediaController.this.audioRecorder.release();
                        MediaController.this.audioRecorder = null;
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                    AndroidUtilities.runOnUIThread(new C02573());
                }
            }
        };
        this.recordStartRunnable = anonymousClass23;
        dispatchQueue.postRunnable(anonymousClass23, paused ? 500 : 50);
    }

    public void generateWaveform(MessageObject messageObject) {
        String id = new StringBuilder();
        id.append(messageObject.getId());
        id.append("_");
        id.append(messageObject.getDialogId());
        id = id.toString();
        final String path = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(id)) {
            this.generatingWaveform.put(id, messageObject);
            Utilities.globalQueue.postRunnable(new Runnable() {
                public void run() {
                    final byte[] waveform = MediaController.this.getWaveform(path);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessageObject messageObject = (MessageObject) MediaController.this.generatingWaveform.remove(id);
                            if (!(messageObject == null || waveform == null)) {
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

                /* renamed from: org.telegram.messenger.MediaController$25$1 */
                class C02601 implements Runnable {
                    C02601() {
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
                            String str = null;
                            objArr[0] = send == 2 ? audioToSend : null;
                            if (send == 2) {
                                str = recordingAudioFileToSend.getAbsolutePath();
                            }
                            objArr[1] = str;
                            instance.postNotificationName(i, objArr);
                            return;
                        }
                        recordingAudioFileToSend.delete();
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

    public void stopRecording(final int send) {
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
                    if (send != 2) {
                        i2 = 0;
                    }
                    objArr[0] = Integer.valueOf(i2);
                    instance.postNotificationName(i, objArr);
                }
            }

            public void run() {
                if (MediaController.this.audioRecorder != null) {
                    try {
                        MediaController.this.sendAfterDone = send;
                        MediaController.this.audioRecorder.stop();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
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
                    AndroidUtilities.runOnUIThread(new C02611());
                }
            }
        });
    }

    public static void saveFile(String fullPath, Context context, int type, String name, String mime) {
        String str = fullPath;
        Context context2 = context;
        if (str != null) {
            File file = null;
            if (!(str == null || fullPath.length() == 0)) {
                file = new File(str);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file != null) {
                File sourceFile = file;
                final boolean[] cancelled = new boolean[]{false};
                if (sourceFile.exists()) {
                    AlertDialog progressDialog = null;
                    if (!(context2 == null || type == 0)) {
                        try {
                            progressDialog = new AlertDialog(context2, 2);
                            progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(true);
                            progressDialog.setOnCancelListener(new OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    cancelled[0] = true;
                                }
                            });
                            progressDialog.show();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                    final AlertDialog finalProgress = progressDialog;
                    final int i = type;
                    final String str2 = name;
                    final File file2 = sourceFile;
                    final boolean[] zArr = cancelled;
                    AnonymousClass28 anonymousClass28 = r4;
                    final String str3 = mime;
                    AnonymousClass28 anonymousClass282 = new Runnable() {

                        /* renamed from: org.telegram.messenger.MediaController$28$2 */
                        class C02632 implements Runnable {
                            C02632() {
                            }

                            public void run() {
                                try {
                                    finalProgress.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        }

                        public void run() {
                            Throwable e;
                            FileChannel fileChannel;
                            try {
                                File destFile;
                                FileChannel fileChannel2;
                                FileChannel destination;
                                boolean result;
                                long lastProgress;
                                long size;
                                long a;
                                FileChannel source;
                                long a2;
                                long size2;
                                final int progress;
                                long a3;
                                int i = 0;
                                if (i == 0) {
                                    destFile = AndroidUtilities.generatePicturePath();
                                } else if (i == 1) {
                                    destFile = AndroidUtilities.generateVideoPath();
                                } else {
                                    if (i == 2) {
                                        destFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                    } else {
                                        destFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                                    }
                                    destFile.mkdir();
                                    File destFile2 = new File(destFile, str2);
                                    if (destFile2.exists()) {
                                        int idx = str2.lastIndexOf(46);
                                        File destFile3 = destFile2;
                                        for (destFile2 = null; destFile2 < 10; destFile2++) {
                                            String newName;
                                            if (idx != -1) {
                                                newName = new StringBuilder();
                                                newName.append(str2.substring(0, idx));
                                                newName.append("(");
                                                newName.append(destFile2 + 1);
                                                newName.append(")");
                                                newName.append(str2.substring(idx));
                                                newName = newName.toString();
                                            } else {
                                                StringBuilder stringBuilder = new StringBuilder();
                                                stringBuilder.append(str2);
                                                stringBuilder.append("(");
                                                stringBuilder.append(destFile2 + 1);
                                                stringBuilder.append(")");
                                                newName = stringBuilder.toString();
                                            }
                                            destFile3 = new File(destFile, newName);
                                            if (!destFile3.exists()) {
                                                break;
                                            }
                                        }
                                        destFile = destFile3;
                                    } else {
                                        destFile = destFile2;
                                    }
                                    if (!destFile.exists()) {
                                        destFile.createNewFile();
                                    }
                                    fileChannel2 = null;
                                    destination = null;
                                    result = true;
                                    lastProgress = System.currentTimeMillis() - 500;
                                    fileChannel2 = new FileInputStream(file2).getChannel();
                                    destination = new FileOutputStream(destFile).getChannel();
                                    size = fileChannel2.size();
                                    a = 0;
                                    while (a < size) {
                                        try {
                                            if (zArr[i]) {
                                                source = fileChannel2;
                                                break;
                                            }
                                            source = fileChannel2;
                                            try {
                                                a2 = a;
                                                size2 = size;
                                                destination.transferFrom(source, a2, Math.min(4096, size - a));
                                                if (finalProgress == null) {
                                                    size = 500;
                                                    if (lastProgress <= System.currentTimeMillis() - 500) {
                                                        lastProgress = System.currentTimeMillis();
                                                        progress = (int) ((((float) a2) / ((float) size2)) * NUM);
                                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                                            public void run() {
                                                                try {
                                                                    finalProgress.setProgress(progress);
                                                                } catch (Throwable e) {
                                                                    FileLog.m3e(e);
                                                                }
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    size = 500;
                                                }
                                                a3 = a2 + 4096;
                                                a2 = size;
                                                a = a3;
                                                size = size2;
                                                fileChannel2 = source;
                                                i = 0;
                                            } catch (Throwable e2) {
                                                e = e2;
                                                fileChannel2 = source;
                                            } catch (Throwable e22) {
                                                e = e22;
                                                fileChannel2 = source;
                                            }
                                        } catch (Throwable e222) {
                                            source = fileChannel2;
                                            e = e222;
                                        } catch (Throwable e2222) {
                                            source = fileChannel2;
                                            e = e2222;
                                        }
                                    }
                                    source = fileChannel2;
                                    if (source == null) {
                                        try {
                                            source.close();
                                        } catch (Exception e3) {
                                        }
                                    }
                                    if (destination != null) {
                                        try {
                                            destination.close();
                                        } catch (Exception e4) {
                                        }
                                    }
                                    if (zArr[0]) {
                                        destFile.delete();
                                        result = false;
                                    }
                                    if (result) {
                                        if (i != 2) {
                                            ((DownloadManager) ApplicationLoader.applicationContext.getSystemService("download")).addCompletedDownload(destFile.getName(), destFile.getName(), false, str3, destFile.getAbsolutePath(), destFile.length(), true);
                                        } else {
                                            AndroidUtilities.addMediaToGallery(Uri.fromFile(destFile));
                                        }
                                    }
                                    if (finalProgress != null) {
                                        AndroidUtilities.runOnUIThread(new C02632());
                                    }
                                }
                                if (destFile.exists()) {
                                    destFile.createNewFile();
                                }
                                fileChannel2 = null;
                                destination = null;
                                result = true;
                                lastProgress = System.currentTimeMillis() - 500;
                                try {
                                    fileChannel2 = new FileInputStream(file2).getChannel();
                                    try {
                                        destination = new FileOutputStream(destFile).getChannel();
                                        size = fileChannel2.size();
                                        a = 0;
                                        while (a < size) {
                                            if (zArr[i]) {
                                                source = fileChannel2;
                                                break;
                                            }
                                            source = fileChannel2;
                                            a2 = a;
                                            size2 = size;
                                            destination.transferFrom(source, a2, Math.min(4096, size - a));
                                            if (finalProgress == null) {
                                                size = 500;
                                            } else {
                                                size = 500;
                                                if (lastProgress <= System.currentTimeMillis() - 500) {
                                                    lastProgress = System.currentTimeMillis();
                                                    progress = (int) ((((float) a2) / ((float) size2)) * NUM);
                                                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                                }
                                            }
                                            a3 = a2 + 4096;
                                            a2 = size;
                                            a = a3;
                                            size = size2;
                                            fileChannel2 = source;
                                            i = 0;
                                        }
                                        source = fileChannel2;
                                        if (source == null) {
                                        } else {
                                            source.close();
                                        }
                                        if (destination != null) {
                                            destination.close();
                                        }
                                    } catch (Throwable e22222) {
                                        fileChannel = fileChannel2;
                                        e = e22222;
                                        try {
                                            FileLog.m3e(e);
                                            result = false;
                                            if (fileChannel2 != null) {
                                                try {
                                                    fileChannel2.close();
                                                } catch (Exception e5) {
                                                    if (destination != null) {
                                                        try {
                                                            destination.close();
                                                        } catch (Exception e6) {
                                                            if (zArr[0]) {
                                                                destFile.delete();
                                                                result = false;
                                                            }
                                                            if (result) {
                                                                if (i != 2) {
                                                                    ((DownloadManager) ApplicationLoader.applicationContext.getSystemService("download")).addCompletedDownload(destFile.getName(), destFile.getName(), false, str3, destFile.getAbsolutePath(), destFile.length(), true);
                                                                } else {
                                                                    AndroidUtilities.addMediaToGallery(Uri.fromFile(destFile));
                                                                }
                                                            }
                                                            if (finalProgress != null) {
                                                                AndroidUtilities.runOnUIThread(new C02632());
                                                            }
                                                        }
                                                    }
                                                    if (zArr[0]) {
                                                        destFile.delete();
                                                        result = false;
                                                    }
                                                    if (result) {
                                                        if (i != 2) {
                                                            ((DownloadManager) ApplicationLoader.applicationContext.getSystemService("download")).addCompletedDownload(destFile.getName(), destFile.getName(), false, str3, destFile.getAbsolutePath(), destFile.length(), true);
                                                        } else {
                                                            AndroidUtilities.addMediaToGallery(Uri.fromFile(destFile));
                                                        }
                                                    }
                                                    if (finalProgress != null) {
                                                        AndroidUtilities.runOnUIThread(new C02632());
                                                    }
                                                }
                                            }
                                            if (destination != null) {
                                                destination.close();
                                            }
                                            if (zArr[0]) {
                                                destFile.delete();
                                                result = false;
                                            }
                                            if (result) {
                                                if (i != 2) {
                                                    AndroidUtilities.addMediaToGallery(Uri.fromFile(destFile));
                                                } else {
                                                    ((DownloadManager) ApplicationLoader.applicationContext.getSystemService("download")).addCompletedDownload(destFile.getName(), destFile.getName(), false, str3, destFile.getAbsolutePath(), destFile.length(), true);
                                                }
                                            }
                                            if (finalProgress != null) {
                                                AndroidUtilities.runOnUIThread(new C02632());
                                            }
                                        } catch (Throwable e222222) {
                                            e = e222222;
                                            if (fileChannel2 != null) {
                                                try {
                                                    fileChannel2.close();
                                                } catch (Exception e7) {
                                                    if (destination != null) {
                                                        try {
                                                            destination.close();
                                                        } catch (Exception e8) {
                                                        }
                                                    }
                                                    throw e;
                                                }
                                            }
                                            if (destination != null) {
                                                destination.close();
                                            }
                                            throw e;
                                        }
                                    } catch (Throwable e2222222) {
                                        fileChannel = fileChannel2;
                                        e = e2222222;
                                        if (fileChannel2 != null) {
                                            fileChannel2.close();
                                        }
                                        if (destination != null) {
                                            destination.close();
                                        }
                                        throw e;
                                    }
                                } catch (Throwable e22222222) {
                                    e = e22222222;
                                    FileLog.m3e(e);
                                    result = false;
                                    if (fileChannel2 != null) {
                                        fileChannel2.close();
                                    }
                                    if (destination != null) {
                                        destination.close();
                                    }
                                    if (zArr[0]) {
                                        destFile.delete();
                                        result = false;
                                    }
                                    if (result) {
                                        if (i != 2) {
                                            AndroidUtilities.addMediaToGallery(Uri.fromFile(destFile));
                                        } else {
                                            ((DownloadManager) ApplicationLoader.applicationContext.getSystemService("download")).addCompletedDownload(destFile.getName(), destFile.getName(), false, str3, destFile.getAbsolutePath(), destFile.length(), true);
                                        }
                                    }
                                    if (finalProgress != null) {
                                        AndroidUtilities.runOnUIThread(new C02632());
                                    }
                                }
                                if (zArr[0]) {
                                    destFile.delete();
                                    result = false;
                                }
                                if (result) {
                                    if (i != 2) {
                                        AndroidUtilities.addMediaToGallery(Uri.fromFile(destFile));
                                    } else {
                                        ((DownloadManager) ApplicationLoader.applicationContext.getSystemService("download")).addCompletedDownload(destFile.getName(), destFile.getName(), false, str3, destFile.getAbsolutePath(), destFile.length(), true);
                                    }
                                }
                            } catch (Throwable e222222222) {
                                FileLog.m3e(e222222222);
                            }
                            if (finalProgress != null) {
                                AndroidUtilities.runOnUIThread(new C02632());
                            }
                        }
                    };
                    new Thread(anonymousClass28).start();
                }
            }
        }
    }

    public static boolean isWebp(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[12];
            if (inputStream.read(header, 0, 12) == 12) {
                String str = new String(header);
                if (str != null) {
                    str = str.toLowerCase();
                    if (str.startsWith("riff") && str.endsWith("webp")) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                        }
                        return true;
                    }
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
        } catch (Throwable e222) {
            FileLog.m3e(e222);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.m3e(e2222);
                }
            }
        }
        return false;
    }

    public static boolean isGif(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[3];
            if (inputStream.read(header, 0, 3) == 3) {
                String str = new String(header);
                if (str != null && str.equalsIgnoreCase("gif")) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                    return true;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
        } catch (Throwable e222) {
            FileLog.m3e(e222);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.m3e(e2222);
                }
            }
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex("_display_name"));
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
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
            f = new File(f, name);
            if (AndroidUtilities.isInternalUri(Uri.fromFile(f))) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                }
                return null;
            }
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            output = new FileOutputStream(f);
            byte[] buffer = new byte[20480];
            while (true) {
                int read = inputStream.read(buffer);
                int len = read;
                if (read == -1) {
                    break;
                }
                output.write(buffer, 0, len);
            }
            String absolutePath = f.getAbsolutePath();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e23) {
                    FileLog.m3e(e23);
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (Throwable e232) {
                    FileLog.m3e(e232);
                }
            }
            return absolutePath;
        } catch (Throwable e) {
            FileLog.m3e(e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e3) {
                    FileLog.m3e(e3);
                    if (output != null) {
                        try {
                            output.close();
                        } catch (Throwable e32) {
                            FileLog.m3e(e32);
                            return null;
                        }
                    }
                    return null;
                }
            }
            if (output != null) {
                output.close();
            }
            return null;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e322) {
                    FileLog.m3e(e322);
                    if (output != null) {
                        try {
                            output.close();
                        } catch (Throwable e3222) {
                            FileLog.m3e(e3222);
                        }
                    }
                }
            }
            if (output != null) {
                output.close();
            }
        }
    }

    public static void loadGalleryPhotosAlbums(final int guid) {
        Thread thread = new Thread(new Runnable() {

            /* renamed from: org.telegram.messenger.MediaController$29$1 */
            class C02641 implements Comparator<PhotoEntry> {
                C02641() {
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

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                AlbumEntry albumEntry;
                AlbumEntry allMediaAlbum;
                AlbumEntry albumEntry2;
                int i;
                Integer mediaCameraAlbumId;
                Throwable th;
                int bucketIdColumn;
                int bucketNameColumn;
                int dataColumn;
                int dateColumn;
                Cursor cursor;
                int durationColumn;
                int imageId;
                int bucketId;
                String bucketName;
                String path;
                long dateTaken;
                long duration;
                int imageIdColumn;
                int durationColumn2;
                int bucketIdColumn2;
                int bucketNameColumn2;
                AlbumEntry allMediaAlbum2;
                AlbumEntry albumEntry3;
                AlbumEntry allMediaAlbum3;
                AlbumEntry albumEntry4;
                String path2;
                Object obj;
                Cursor cursor2;
                Object obj2;
                AlbumEntry allPhotosAlbum;
                AlbumEntry allMediaAlbum4;
                AnonymousClass29 anonymousClass29 = this;
                ArrayList<AlbumEntry> mediaAlbumsSorted = new ArrayList();
                ArrayList<AlbumEntry> photoAlbumsSorted = new ArrayList();
                SparseArray<AlbumEntry> mediaAlbums = new SparseArray();
                SparseArray<AlbumEntry> photoAlbums = new SparseArray();
                AlbumEntry allMediaAlbum5 = null;
                Cursor cursor3 = null;
                String cameraFolder = null;
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
                    stringBuilder.append("/Camera/");
                    cameraFolder = stringBuilder.toString();
                } catch (Throwable e) {
                    Throwable e2;
                    FileLog.m3e(e2);
                }
                String cameraFolder2 = cameraFolder;
                Integer num = 0;
                Integer photoCameraAlbumId = null;
                try {
                    Integer num2;
                    if (VERSION.SDK_INT >= 23) {
                        try {
                            if (VERSION.SDK_INT < 23 || ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                                albumEntry = null;
                                allMediaAlbum = null;
                                if (cursor3 != null) {
                                    try {
                                        cursor3.close();
                                    } catch (Throwable e22) {
                                        FileLog.m3e(e22);
                                    }
                                }
                                num2 = photoCameraAlbumId;
                                allMediaAlbum5 = allMediaAlbum;
                                if (VERSION.SDK_INT >= 23) {
                                    try {
                                        if (VERSION.SDK_INT >= 23 || ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                                            albumEntry2 = allMediaAlbum5;
                                            i = 0;
                                            if (cursor3 != null) {
                                                try {
                                                    cursor3.close();
                                                } catch (Throwable e222) {
                                                    FileLog.m3e(e222);
                                                }
                                            }
                                            mediaCameraAlbumId = num;
                                            for (i = 
/*
Method generation error in method: org.telegram.messenger.MediaController.29.run():void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r3_86 'i' int) = (r3_80 'i' int), (r3_83 'i' int), (r3_85 'i' int) binds: {(r3_80 'i' int)=B:249:0x0465, (r3_83 'i' int)=B:261:0x047e, (r3_85 'i' int)=B:264:0x0483} in method: org.telegram.messenger.MediaController.29.run():void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:184)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
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
	... 72 more

*/
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
                                        Runnable anonymousClass30 = new Runnable() {
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
                                        broadcastPhotosRunnable = anonymousClass30;
                                        AndroidUtilities.runOnUIThread(anonymousClass30, (long) delay);
                                    }

                                    public void scheduleVideoConvert(MessageObject messageObject) {
                                        scheduleVideoConvert(messageObject, false);
                                    }

                                    public boolean scheduleVideoConvert(MessageObject messageObject, boolean isEmpty) {
                                        if (messageObject != null) {
                                            if (messageObject.videoEditedInfo != null) {
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
                                        }
                                        return false;
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
                                        int a = 0;
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
                                            while (a < messageObject.messageOwner.media.document.attributes.size()) {
                                                if (((DocumentAttribute) messageObject.messageOwner.media.document.attributes.get(a)) instanceof TL_documentAttributeAnimated) {
                                                    intent.putExtra("gif", true);
                                                    break;
                                                }
                                                a++;
                                            }
                                        }
                                        if (messageObject.getId() != 0) {
                                            try {
                                                ApplicationLoader.applicationContext.startService(intent);
                                            } catch (Throwable e) {
                                                FileLog.m3e(e);
                                            }
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
                                                MediaCodecInfo lastCodecInfo2 = lastCodecInfo;
                                                for (String type : codecInfo.getSupportedTypes()) {
                                                    if (type.equalsIgnoreCase(mimeType)) {
                                                        lastCodecInfo2 = codecInfo;
                                                        String name = lastCodecInfo2.getName();
                                                        if (name != null && (!name.equals("OMX.SEC.avc.enc") || name.equals("OMX.SEC.AVC.Encoder"))) {
                                                            return lastCodecInfo2;
                                                        }
                                                    }
                                                }
                                                lastCodecInfo = lastCodecInfo2;
                                            }
                                        }
                                        return lastCodecInfo;
                                    }

                                    private static boolean isRecognizedFormat(int colorFormat) {
                                        if (!(colorFormat == 39 || colorFormat == NUM)) {
                                            switch (colorFormat) {
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
                                        boolean firstWrite = this.videoConvertFirstWrite;
                                        if (firstWrite) {
                                            this.videoConvertFirstWrite = false;
                                        }
                                        final boolean z = error;
                                        final boolean z2 = last;
                                        final MessageObject messageObject2 = messageObject;
                                        final File file2 = file;
                                        final boolean z3 = firstWrite;
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
                                                if (z3) {
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
                                        MediaController mediaController;
                                        int videoTrackIndex;
                                        int maxBufferSize;
                                        int videoTrackIndex2;
                                        int maxBufferSize2;
                                        int muxerVideoTrackIndex;
                                        int i;
                                        int i2;
                                        MediaController mediaController2;
                                        MediaExtractor mediaExtractor = extractor;
                                        MP4Builder mP4Builder = mediaMuxer;
                                        BufferInfo bufferInfo = info;
                                        long j = start;
                                        int videoTrackIndex3 = findTrack(mediaExtractor, false);
                                        int audioTrackIndex = needAudio ? findTrack(mediaExtractor, true) : -1;
                                        int muxerVideoTrackIndex2 = -1;
                                        int muxerAudioTrackIndex = -1;
                                        boolean inputDone = false;
                                        if (videoTrackIndex3 >= 0) {
                                            mediaExtractor.selectTrack(videoTrackIndex3);
                                            MediaFormat trackFormat = mediaExtractor.getTrackFormat(videoTrackIndex3);
                                            muxerVideoTrackIndex2 = mP4Builder.addTrack(trackFormat, false);
                                            int maxBufferSize3 = trackFormat.getInteger("max-input-size");
                                            videoTrackIndex = videoTrackIndex3;
                                            if (j > 0) {
                                                mediaExtractor.seekTo(j, 0);
                                            } else {
                                                mediaExtractor.seekTo(0, 0);
                                            }
                                            maxBufferSize = maxBufferSize3;
                                        } else {
                                            videoTrackIndex = videoTrackIndex3;
                                            maxBufferSize = 0;
                                        }
                                        if (audioTrackIndex >= 0) {
                                            int i3;
                                            mediaExtractor.selectTrack(audioTrackIndex);
                                            MediaFormat trackFormat2 = mediaExtractor.getTrackFormat(audioTrackIndex);
                                            muxerAudioTrackIndex = mP4Builder.addTrack(trackFormat2, true);
                                            maxBufferSize = Math.max(trackFormat2.getInteger("max-input-size"), maxBufferSize);
                                            if (j > 0) {
                                                mediaExtractor.seekTo(j, 0);
                                                i3 = maxBufferSize;
                                            } else {
                                                i3 = maxBufferSize;
                                                MediaFormat mediaFormat = trackFormat2;
                                                mediaExtractor.seekTo(0, 0);
                                            }
                                            maxBufferSize = i3;
                                        }
                                        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
                                        if (audioTrackIndex < 0) {
                                            if (videoTrackIndex < 0) {
                                                return -1;
                                            }
                                        }
                                        long startTime = -1;
                                        checkConversionCanceled();
                                        while (!inputDone) {
                                            byte[] array;
                                            int a;
                                            int i4;
                                            MessageObject messageObject2;
                                            checkConversionCanceled();
                                            boolean eof = false;
                                            bufferInfo.size = mediaExtractor.readSampleData(buffer, 0);
                                            int index = extractor.getSampleTrackIndex();
                                            videoTrackIndex2 = videoTrackIndex;
                                            if (index == videoTrackIndex2) {
                                                videoTrackIndex = muxerVideoTrackIndex2;
                                            } else if (index == audioTrackIndex) {
                                                videoTrackIndex = muxerAudioTrackIndex;
                                            } else {
                                                videoTrackIndex = -1;
                                                maxBufferSize2 = maxBufferSize;
                                                maxBufferSize = videoTrackIndex;
                                                if (maxBufferSize == -1) {
                                                    muxerVideoTrackIndex = muxerVideoTrackIndex2;
                                                    if (VERSION.SDK_INT < 21) {
                                                        buffer.position(0);
                                                        buffer.limit(bufferInfo.size);
                                                    }
                                                    if (index != audioTrackIndex) {
                                                        array = buffer.array();
                                                        if (array != null) {
                                                            muxerVideoTrackIndex2 = buffer.arrayOffset();
                                                            videoTrackIndex = muxerVideoTrackIndex2 + buffer.limit();
                                                            int offset = muxerVideoTrackIndex2;
                                                            muxerVideoTrackIndex2 = -1;
                                                            a = offset;
                                                            while (true) {
                                                                i = muxerAudioTrackIndex;
                                                                i2 = audioTrackIndex;
                                                                audioTrackIndex = a;
                                                                if (audioTrackIndex <= videoTrackIndex - 4) {
                                                                    break;
                                                                }
                                                                if (array[audioTrackIndex] != (byte) 0 && array[audioTrackIndex + 1] == (byte) 0 && array[audioTrackIndex + 2] == (byte) 0) {
                                                                    if (array[audioTrackIndex + 3] != (byte) 1) {
                                                                    }
                                                                    if (muxerVideoTrackIndex2 != -1) {
                                                                        muxerAudioTrackIndex = (audioTrackIndex - muxerVideoTrackIndex2) - (audioTrackIndex != videoTrackIndex + -4 ? 4 : 0);
                                                                        array[muxerVideoTrackIndex2] = (byte) (muxerAudioTrackIndex >> 24);
                                                                        array[muxerVideoTrackIndex2 + 1] = (byte) (muxerAudioTrackIndex >> 16);
                                                                        array[muxerVideoTrackIndex2 + 2] = (byte) (muxerAudioTrackIndex >> 8);
                                                                        array[muxerVideoTrackIndex2 + 3] = (byte) muxerAudioTrackIndex;
                                                                        i4 = audioTrackIndex;
                                                                    } else {
                                                                        i4 = audioTrackIndex;
                                                                    }
                                                                    muxerVideoTrackIndex2 = i4;
                                                                    a = audioTrackIndex + 1;
                                                                    muxerAudioTrackIndex = i;
                                                                    audioTrackIndex = i2;
                                                                    mediaController = this;
                                                                    mP4Builder = mediaMuxer;
                                                                }
                                                                if (audioTrackIndex != videoTrackIndex - 4) {
                                                                    a = audioTrackIndex + 1;
                                                                    muxerAudioTrackIndex = i;
                                                                    audioTrackIndex = i2;
                                                                    mediaController = this;
                                                                    mP4Builder = mediaMuxer;
                                                                }
                                                                if (muxerVideoTrackIndex2 != -1) {
                                                                    i4 = audioTrackIndex;
                                                                } else {
                                                                    if (audioTrackIndex != videoTrackIndex + -4) {
                                                                    }
                                                                    muxerAudioTrackIndex = (audioTrackIndex - muxerVideoTrackIndex2) - (audioTrackIndex != videoTrackIndex + -4 ? 4 : 0);
                                                                    array[muxerVideoTrackIndex2] = (byte) (muxerAudioTrackIndex >> 24);
                                                                    array[muxerVideoTrackIndex2 + 1] = (byte) (muxerAudioTrackIndex >> 16);
                                                                    array[muxerVideoTrackIndex2 + 2] = (byte) (muxerAudioTrackIndex >> 8);
                                                                    array[muxerVideoTrackIndex2 + 3] = (byte) muxerAudioTrackIndex;
                                                                    i4 = audioTrackIndex;
                                                                }
                                                                muxerVideoTrackIndex2 = i4;
                                                                a = audioTrackIndex + 1;
                                                                muxerAudioTrackIndex = i;
                                                                audioTrackIndex = i2;
                                                                mediaController = this;
                                                                mP4Builder = mediaMuxer;
                                                            }
                                                            if (bufferInfo.size < 0) {
                                                                bufferInfo.presentationTimeUs = extractor.getSampleTime();
                                                            } else {
                                                                bufferInfo.size = 0;
                                                                eof = true;
                                                            }
                                                            if (bufferInfo.size > 0 || eof) {
                                                                messageObject2 = messageObject;
                                                                muxerVideoTrackIndex2 = file;
                                                            } else {
                                                                File file2;
                                                                if (index == videoTrackIndex2 && j > 0) {
                                                                    if (startTime == -1) {
                                                                        startTime = bufferInfo.presentationTimeUs;
                                                                    }
                                                                }
                                                                if (end >= 0) {
                                                                    if (bufferInfo.presentationTimeUs >= end) {
                                                                        messageObject2 = messageObject;
                                                                        file2 = file;
                                                                        eof = true;
                                                                    }
                                                                }
                                                                bufferInfo.offset = 0;
                                                                bufferInfo.flags = extractor.getSampleFlags();
                                                                if (mediaMuxer.writeSampleData(maxBufferSize, buffer, bufferInfo, false)) {
                                                                    didWriteData(messageObject, file, false, false);
                                                                    if (!eof) {
                                                                        extractor.advance();
                                                                    }
                                                                } else {
                                                                    messageObject2 = messageObject;
                                                                    file2 = file;
                                                                    mediaController2 = this;
                                                                    if (eof) {
                                                                        extractor.advance();
                                                                    }
                                                                }
                                                            }
                                                            mP4Builder = mediaMuxer;
                                                            mediaController2 = this;
                                                            if (eof) {
                                                                extractor.advance();
                                                            }
                                                        }
                                                    }
                                                    i2 = audioTrackIndex;
                                                    i = muxerAudioTrackIndex;
                                                    if (bufferInfo.size < 0) {
                                                        bufferInfo.size = 0;
                                                        eof = true;
                                                    } else {
                                                        bufferInfo.presentationTimeUs = extractor.getSampleTime();
                                                    }
                                                    if (bufferInfo.size > 0) {
                                                    }
                                                    messageObject2 = messageObject;
                                                    muxerVideoTrackIndex2 = file;
                                                    mP4Builder = mediaMuxer;
                                                    mediaController2 = this;
                                                    if (eof) {
                                                        extractor.advance();
                                                    }
                                                } else {
                                                    mediaController2 = mediaController;
                                                    i2 = audioTrackIndex;
                                                    muxerVideoTrackIndex = muxerVideoTrackIndex2;
                                                    i = muxerAudioTrackIndex;
                                                    audioTrackIndex = messageObject;
                                                    muxerVideoTrackIndex2 = file;
                                                    if (index != -1) {
                                                        eof = true;
                                                    } else {
                                                        extractor.advance();
                                                    }
                                                }
                                                if (eof) {
                                                    inputDone = true;
                                                }
                                                videoTrackIndex = videoTrackIndex2;
                                                mediaController = mediaController2;
                                                maxBufferSize = maxBufferSize2;
                                                muxerVideoTrackIndex2 = muxerVideoTrackIndex;
                                                muxerAudioTrackIndex = i;
                                                audioTrackIndex = i2;
                                            }
                                            maxBufferSize2 = maxBufferSize;
                                            maxBufferSize = videoTrackIndex;
                                            if (maxBufferSize == -1) {
                                                mediaController2 = mediaController;
                                                i2 = audioTrackIndex;
                                                muxerVideoTrackIndex = muxerVideoTrackIndex2;
                                                i = muxerAudioTrackIndex;
                                                audioTrackIndex = messageObject;
                                                muxerVideoTrackIndex2 = file;
                                                if (index != -1) {
                                                    extractor.advance();
                                                } else {
                                                    eof = true;
                                                }
                                            } else {
                                                muxerVideoTrackIndex = muxerVideoTrackIndex2;
                                                if (VERSION.SDK_INT < 21) {
                                                    buffer.position(0);
                                                    buffer.limit(bufferInfo.size);
                                                }
                                                if (index != audioTrackIndex) {
                                                    array = buffer.array();
                                                    if (array != null) {
                                                        muxerVideoTrackIndex2 = buffer.arrayOffset();
                                                        videoTrackIndex = muxerVideoTrackIndex2 + buffer.limit();
                                                        int offset2 = muxerVideoTrackIndex2;
                                                        muxerVideoTrackIndex2 = -1;
                                                        a = offset2;
                                                        while (true) {
                                                            i = muxerAudioTrackIndex;
                                                            i2 = audioTrackIndex;
                                                            audioTrackIndex = a;
                                                            if (audioTrackIndex <= videoTrackIndex - 4) {
                                                                break;
                                                            }
                                                            if (array[audioTrackIndex] != (byte) 0) {
                                                            }
                                                            if (audioTrackIndex != videoTrackIndex - 4) {
                                                                a = audioTrackIndex + 1;
                                                                muxerAudioTrackIndex = i;
                                                                audioTrackIndex = i2;
                                                                mediaController = this;
                                                                mP4Builder = mediaMuxer;
                                                            }
                                                            if (muxerVideoTrackIndex2 != -1) {
                                                                if (audioTrackIndex != videoTrackIndex + -4) {
                                                                }
                                                                muxerAudioTrackIndex = (audioTrackIndex - muxerVideoTrackIndex2) - (audioTrackIndex != videoTrackIndex + -4 ? 4 : 0);
                                                                array[muxerVideoTrackIndex2] = (byte) (muxerAudioTrackIndex >> 24);
                                                                array[muxerVideoTrackIndex2 + 1] = (byte) (muxerAudioTrackIndex >> 16);
                                                                array[muxerVideoTrackIndex2 + 2] = (byte) (muxerAudioTrackIndex >> 8);
                                                                array[muxerVideoTrackIndex2 + 3] = (byte) muxerAudioTrackIndex;
                                                                i4 = audioTrackIndex;
                                                            } else {
                                                                i4 = audioTrackIndex;
                                                            }
                                                            muxerVideoTrackIndex2 = i4;
                                                            a = audioTrackIndex + 1;
                                                            muxerAudioTrackIndex = i;
                                                            audioTrackIndex = i2;
                                                            mediaController = this;
                                                            mP4Builder = mediaMuxer;
                                                        }
                                                        if (bufferInfo.size < 0) {
                                                            bufferInfo.presentationTimeUs = extractor.getSampleTime();
                                                        } else {
                                                            bufferInfo.size = 0;
                                                            eof = true;
                                                        }
                                                        if (bufferInfo.size > 0) {
                                                        }
                                                        messageObject2 = messageObject;
                                                        muxerVideoTrackIndex2 = file;
                                                        mP4Builder = mediaMuxer;
                                                        mediaController2 = this;
                                                        if (eof) {
                                                            extractor.advance();
                                                        }
                                                    }
                                                }
                                                i2 = audioTrackIndex;
                                                i = muxerAudioTrackIndex;
                                                if (bufferInfo.size < 0) {
                                                    bufferInfo.size = 0;
                                                    eof = true;
                                                } else {
                                                    bufferInfo.presentationTimeUs = extractor.getSampleTime();
                                                }
                                                if (bufferInfo.size > 0) {
                                                }
                                                messageObject2 = messageObject;
                                                muxerVideoTrackIndex2 = file;
                                                mP4Builder = mediaMuxer;
                                                mediaController2 = this;
                                                if (eof) {
                                                    extractor.advance();
                                                }
                                            }
                                            if (eof) {
                                                inputDone = true;
                                            }
                                            videoTrackIndex = videoTrackIndex2;
                                            mediaController = mediaController2;
                                            maxBufferSize = maxBufferSize2;
                                            muxerVideoTrackIndex2 = muxerVideoTrackIndex;
                                            muxerAudioTrackIndex = i;
                                            audioTrackIndex = i2;
                                        }
                                        mediaController2 = mediaController;
                                        maxBufferSize2 = maxBufferSize;
                                        i2 = audioTrackIndex;
                                        muxerVideoTrackIndex = muxerVideoTrackIndex2;
                                        i = muxerAudioTrackIndex;
                                        videoTrackIndex2 = videoTrackIndex;
                                        audioTrackIndex = messageObject;
                                        muxerVideoTrackIndex2 = file;
                                        if (videoTrackIndex2 >= 0) {
                                            mediaExtractor.unselectTrack(videoTrackIndex2);
                                        }
                                        if (i2 >= 0) {
                                            mediaExtractor.unselectTrack(i2);
                                        }
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

                                    private boolean convertVideo(org.telegram.messenger.MessageObject r116) {
                                        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MessageObject):boolean. bs: [B:62:0x0189, B:96:0x0295]
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
                                        r115 = this;
                                        r12 = r115;
                                        r13 = r116;
                                        r1 = r13.videoEditedInfo;
                                        r14 = r1.originalPath;
                                        r1 = r13.videoEditedInfo;
                                        r10 = r1.startTime;
                                        r1 = r13.videoEditedInfo;
                                        r8 = r1.endTime;
                                        r1 = r13.videoEditedInfo;
                                        r1 = r1.resultWidth;
                                        r2 = r13.videoEditedInfo;
                                        r2 = r2.resultHeight;
                                        r3 = r13.videoEditedInfo;
                                        r3 = r3.rotationValue;
                                        r4 = r13.videoEditedInfo;
                                        r15 = r4.originalWidth;
                                        r4 = r13.videoEditedInfo;
                                        r6 = r4.originalHeight;
                                        r4 = r13.videoEditedInfo;
                                        r7 = r4.bitrate;
                                        r4 = 0;
                                        r16 = r4;
                                        r4 = r116.getDialogId();
                                        r4 = (int) r4;
                                        if (r4 != 0) goto L_0x0034;
                                    L_0x0032:
                                        r4 = 1;
                                        goto L_0x0035;
                                    L_0x0034:
                                        r4 = 0;
                                    L_0x0035:
                                        r5 = new java.io.File;
                                        r19 = r8;
                                        r8 = r13.messageOwner;
                                        r8 = r8.attachPath;
                                        r5.<init>(r8);
                                        r8 = r5;
                                        r5 = android.os.Build.VERSION.SDK_INT;
                                        r9 = 18;
                                        if (r5 >= r9) goto L_0x0055;
                                    L_0x0047:
                                        if (r2 <= r1) goto L_0x0055;
                                    L_0x0049:
                                        if (r1 == r15) goto L_0x0055;
                                    L_0x004b:
                                        if (r2 == r6) goto L_0x0055;
                                    L_0x004d:
                                        r5 = r2;
                                        r2 = r1;
                                        r1 = r5;
                                        r3 = 90;
                                        r5 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
                                        goto L_0x0066;
                                    L_0x0055:
                                        r5 = android.os.Build.VERSION.SDK_INT;
                                        r9 = 20;
                                        if (r5 <= r9) goto L_0x0080;
                                    L_0x005b:
                                        r5 = 90;
                                        if (r3 != r5) goto L_0x006d;
                                    L_0x005f:
                                        r5 = r2;
                                        r2 = r1;
                                        r1 = r5;
                                        r3 = 0;
                                        r5 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
                                    L_0x0066:
                                        r9 = r1;
                                        r114 = r5;
                                        r5 = r2;
                                        r2 = r114;
                                        goto L_0x0084;
                                    L_0x006d:
                                        r5 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
                                        if (r3 != r5) goto L_0x0075;
                                    L_0x0071:
                                        r5 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
                                        r3 = 0;
                                        goto L_0x0066;
                                    L_0x0075:
                                        r5 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
                                        if (r3 != r5) goto L_0x0080;
                                    L_0x0079:
                                        r5 = r2;
                                        r2 = r1;
                                        r1 = r5;
                                        r3 = 0;
                                        r5 = 90;
                                        goto L_0x0066;
                                    L_0x0080:
                                        r9 = r1;
                                        r5 = r2;
                                        r2 = r16;
                                    L_0x0084:
                                        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
                                        r29 = r10;
                                        r10 = "videoconvert";
                                        r11 = 0;
                                        r10 = r1.getSharedPreferences(r10, r11);
                                        r1 = new java.io.File;
                                        r1.<init>(r14);
                                        r11 = r1;
                                        r1 = r116.getId();
                                        if (r1 == 0) goto L_0x00d4;
                                    L_0x009b:
                                        r1 = "isPreviousOk";
                                        r31 = r7;
                                        r7 = 1;
                                        r1 = r10.getBoolean(r1, r7);
                                        r7 = r10.edit();
                                        r32 = r2;
                                        r2 = "isPreviousOk";
                                        r33 = r6;
                                        r6 = 0;
                                        r2 = r7.putBoolean(r2, r6);
                                        r2.commit();
                                        r2 = r11.canRead();
                                        if (r2 == 0) goto L_0x00c1;
                                    L_0x00bc:
                                        if (r1 != 0) goto L_0x00bf;
                                    L_0x00be:
                                        goto L_0x00c1;
                                    L_0x00bf:
                                        r6 = 1;
                                        goto L_0x00db;
                                    L_0x00c1:
                                        r6 = 1;
                                        r12.didWriteData(r13, r8, r6, r6);
                                        r2 = r10.edit();
                                        r7 = "isPreviousOk";
                                        r2 = r2.putBoolean(r7, r6);
                                        r2.commit();
                                        r2 = 0;
                                        return r2;
                                    L_0x00d4:
                                        r32 = r2;
                                        r33 = r6;
                                        r31 = r7;
                                        r6 = 1;
                                    L_0x00db:
                                        r12.videoConvertFirstWrite = r6;
                                        r16 = 0;
                                        r34 = java.lang.System.currentTimeMillis();
                                        if (r9 == 0) goto L_0x12c9;
                                    L_0x00e5:
                                        if (r5 == 0) goto L_0x12c9;
                                    L_0x00e7:
                                        r1 = 0;
                                        r2 = 0;
                                        r7 = r2;
                                        r6 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x121f, all -> 0x11f7 }
                                        r6.<init>();	 Catch:{ Exception -> 0x121f, all -> 0x11f7 }
                                        r2 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x121f, all -> 0x11f7 }
                                        r2.<init>();	 Catch:{ Exception -> 0x121f, all -> 0x11f7 }
                                        r2.setCacheFile(r8);	 Catch:{ Exception -> 0x121f, all -> 0x11f7 }
                                        r2.setRotation(r3);	 Catch:{ Exception -> 0x121f, all -> 0x11f7 }
                                        r2.setSize(r9, r5);	 Catch:{ Exception -> 0x121f, all -> 0x11f7 }
                                        r36 = r1;
                                        r1 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x11d4, all -> 0x11ae }
                                        r1.<init>();	 Catch:{ Exception -> 0x11d4, all -> 0x11ae }
                                        r1 = r1.createMovie(r2, r4);	 Catch:{ Exception -> 0x11d4, all -> 0x11ae }
                                        r37 = r1;
                                        r1 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x118b, all -> 0x1164 }
                                        r1.<init>();	 Catch:{ Exception -> 0x118b, all -> 0x1164 }
                                        r7 = r1;
                                        r7.setDataSource(r14);	 Catch:{ Exception -> 0x1142, all -> 0x111d }
                                        r115.checkConversionCanceled();	 Catch:{ Exception -> 0x1142, all -> 0x111d }
                                        if (r9 != r15) goto L_0x0211;
                                    L_0x0118:
                                        r1 = r33;
                                        if (r5 != r1) goto L_0x01f0;
                                    L_0x011c:
                                        if (r32 != 0) goto L_0x01f0;
                                    L_0x011e:
                                        r38 = r1;
                                        r1 = r13.videoEditedInfo;	 Catch:{ Exception -> 0x01c7, all -> 0x01a0 }
                                        r1 = r1.roundVideo;	 Catch:{ Exception -> 0x01c7, all -> 0x01a0 }
                                        if (r1 == 0) goto L_0x0148;
                                    L_0x0126:
                                        r33 = r4;
                                        r42 = r5;
                                        r44 = r6;
                                        r13 = r7;
                                        r45 = r8;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r47 = r29;
                                        r15 = r31;
                                        r46 = r32;
                                        r41 = r37;
                                        r17 = r38;
                                        r31 = r2;
                                        r32 = r3;
                                        r14 = r9;
                                        r29 = r11;
                                        goto L_0x0231;
                                    L_0x0148:
                                        r39 = r2;
                                        r1 = r31;
                                        r2 = -1;
                                        if (r1 == r2) goto L_0x0152;
                                    L_0x014f:
                                        r18 = 1;
                                        goto L_0x0154;
                                    L_0x0152:
                                        r18 = 0;
                                    L_0x0154:
                                        r21 = r1;
                                        r2 = r37;
                                        r17 = r38;
                                        r1 = r12;
                                        r41 = r2;
                                        r40 = r14;
                                        r14 = r32;
                                        r31 = r39;
                                        r2 = r13;
                                        r32 = r3;
                                        r3 = r7;
                                        r33 = r4;
                                        r4 = r41;
                                        r42 = r5;
                                        r43 = r15;
                                        r15 = 1;
                                        r5 = r6;
                                        r44 = r6;
                                        r13 = r7;
                                        r15 = r21;
                                        r6 = r29;
                                        r45 = r8;
                                        r46 = r14;
                                        r14 = r9;
                                        r8 = r19;
                                        r49 = r10;
                                        r47 = r29;
                                        r10 = r45;
                                        r29 = r11;
                                        r11 = r18;
                                        r1.readAndWriteTracks(r2, r3, r4, r5, r6, r8, r10, r11);	 Catch:{ Exception -> 0x0257, all -> 0x0240 }
                                        r5 = r12;
                                        r1 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r14 = r42;
                                        r104 = r45;
                                        r81 = r46;
                                        r9 = r47;
                                        r15 = r116;
                                        goto L_0x10b8;
                                    L_0x01a0:
                                        r0 = move-exception;
                                        r33 = r4;
                                        r13 = r7;
                                        r40 = r14;
                                        r43 = r15;
                                        r47 = r29;
                                        r46 = r32;
                                        r41 = r37;
                                        r17 = r38;
                                        r32 = r3;
                                        r29 = r11;
                                        r1 = r0;
                                        r14 = r5;
                                        r4 = r8;
                                        r110 = r9;
                                        r3 = r10;
                                        r5 = r12;
                                        r82 = r31;
                                        r11 = r41;
                                        r81 = r46;
                                        r9 = r47;
                                        r15 = r116;
                                        goto L_0x1295;
                                    L_0x01c7:
                                        r0 = move-exception;
                                        r33 = r4;
                                        r13 = r7;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r47 = r29;
                                        r46 = r32;
                                        r41 = r37;
                                        r17 = r38;
                                        r32 = r3;
                                        r29 = r11;
                                        r1 = r0;
                                        r14 = r5;
                                        r104 = r8;
                                        r110 = r9;
                                        r5 = r12;
                                        r82 = r31;
                                        r2 = r41;
                                        r81 = r46;
                                        r9 = r47;
                                        r15 = r116;
                                        goto L_0x1242;
                                    L_0x01f0:
                                        r17 = r1;
                                        r33 = r4;
                                        r42 = r5;
                                        r44 = r6;
                                        r13 = r7;
                                        r45 = r8;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r47 = r29;
                                        r15 = r31;
                                        r46 = r32;
                                        r41 = r37;
                                        r31 = r2;
                                        r32 = r3;
                                        r14 = r9;
                                        r29 = r11;
                                        goto L_0x0231;
                                    L_0x0211:
                                        r42 = r5;
                                        r44 = r6;
                                        r13 = r7;
                                        r45 = r8;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r47 = r29;
                                        r15 = r31;
                                        r46 = r32;
                                        r17 = r33;
                                        r41 = r37;
                                        r31 = r2;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r9;
                                        r29 = r11;
                                    L_0x0231:
                                        r1 = 0;
                                        r2 = r12.findTrack(r13, r1);	 Catch:{ Exception -> 0x1106, all -> 0x10ed }
                                        r1 = r2;
                                        r2 = -1;
                                        if (r15 == r2) goto L_0x026d;
                                    L_0x023a:
                                        r2 = 1;
                                        r3 = r12.findTrack(r13, r2);	 Catch:{ Exception -> 0x0257, all -> 0x0240 }
                                        goto L_0x026e;
                                    L_0x0240:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r5 = r12;
                                        r110 = r14;
                                        r82 = r15;
                                        r11 = r41;
                                        r14 = r42;
                                        r4 = r45;
                                        r81 = r46;
                                        r9 = r47;
                                        r3 = r49;
                                    L_0x0253:
                                        r15 = r116;
                                        goto L_0x1295;
                                    L_0x0257:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r5 = r12;
                                        r7 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r2 = r41;
                                        r14 = r42;
                                        r104 = r45;
                                        r81 = r46;
                                        r9 = r47;
                                        r15 = r116;
                                        goto L_0x1242;
                                    L_0x026d:
                                        r3 = -1;
                                    L_0x026e:
                                        r2 = r3;
                                        if (r1 < 0) goto L_0x10a4;
                                    L_0x0271:
                                        r3 = 0;
                                        r4 = 0;
                                        r5 = 0;
                                        r6 = 0;
                                        r7 = r6;
                                        r8 = -1;
                                        r6 = 0;
                                        r10 = 0;
                                        r11 = 0;
                                        r22 = 0;
                                        r23 = -5;
                                        r24 = -5;
                                        r25 = 0;
                                        r50 = r3;
                                        r3 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0fed, all -> 0x0fd2 }
                                        r3 = r3.toLowerCase();	 Catch:{ Exception -> 0x0fed, all -> 0x0fd2 }
                                        r51 = r4;
                                        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0fab, all -> 0x0fd2 }
                                        r52 = r5;
                                        r5 = 18;
                                        if (r4 >= r5) goto L_0x0389;
                                    L_0x0295:
                                        r4 = "video/avc";	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        r4 = selectCodec(r4);	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        r5 = "video/avc";	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        r5 = selectColorFormat(r4, r5);	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        if (r5 != 0) goto L_0x02cf;
                                    L_0x02a3:
                                        r53 = r5;
                                        r5 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x02af, all -> 0x0240 }
                                        r54 = r6;	 Catch:{ Exception -> 0x02af, all -> 0x0240 }
                                        r6 = "no supported color format";	 Catch:{ Exception -> 0x02af, all -> 0x0240 }
                                        r5.<init>(r6);	 Catch:{ Exception -> 0x02af, all -> 0x0240 }
                                        throw r5;	 Catch:{ Exception -> 0x02af, all -> 0x0240 }
                                    L_0x02af:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r3 = r7;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r14 = r42;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r108 = r47;
                                        r8 = r50;
                                        r4 = r51;
                                        r7 = r52;
                                        goto L_0x03ce;
                                    L_0x02cf:
                                        r53 = r5;
                                        r54 = r6;
                                        r5 = r4.getName();	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        r6 = "OMX.qcom.";	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        r6 = r5.contains(r6);	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        if (r6 == 0) goto L_0x02ff;	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                    L_0x02df:
                                        r6 = 1;	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        r55 = r6;	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0363, all -> 0x0240 }
                                        r56 = r7;
                                        r7 = 16;
                                        if (r6 != r7) goto L_0x02fc;
                                    L_0x02ea:
                                        r6 = "lge";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6 = r3.equals(r6);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        if (r6 != 0) goto L_0x02fa;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x02f2:
                                        r6 = "nokia";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6 = r3.equals(r6);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        if (r6 == 0) goto L_0x02fc;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x02fa:
                                        r22 = 1;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x02fc:
                                        r25 = r55;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        goto L_0x032d;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x02ff:
                                        r56 = r7;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6 = "OMX.Intel.";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6 = r5.contains(r6);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        if (r6 == 0) goto L_0x030d;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x0309:
                                        r6 = 2;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x030a:
                                        r25 = r6;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        goto L_0x032d;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x030d:
                                        r6 = "OMX.MTK.VIDEO.ENCODER.AVC";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6 = r5.equals(r6);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        if (r6 == 0) goto L_0x0317;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x0315:
                                        r6 = 3;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        goto L_0x030a;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x0317:
                                        r6 = "OMX.SEC.AVC.Encoder";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6 = r5.equals(r6);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        if (r6 == 0) goto L_0x0323;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x031f:
                                        r6 = 4;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r22 = 1;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        goto L_0x030a;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x0323:
                                        r6 = "OMX.TI.DUCATI1.VIDEO.H264E";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6 = r5.equals(r6);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        if (r6 == 0) goto L_0x032d;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x032b:
                                        r6 = 5;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        goto L_0x030a;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x032d:
                                        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        if (r6 == 0) goto L_0x035b;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                    L_0x0331:
                                        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6.<init>();	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r7 = "codec = ";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6.append(r7);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r7 = r4.getName();	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6.append(r7);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r7 = " manufacturer = ";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6.append(r7);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6.append(r3);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r7 = "device = ";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6.append(r7);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r7 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6.append(r7);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r6 = r6.toString();	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        org.telegram.messenger.FileLog.m0d(r6);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r4 = r22;
                                        r6 = r25;
                                        r5 = r53;
                                        goto L_0x0394;
                                    L_0x0363:
                                        r0 = move-exception;
                                        r56 = r7;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r14 = r42;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r108 = r47;
                                        r8 = r50;
                                        r4 = r51;
                                        r7 = r52;
                                        r3 = r56;
                                        r15 = r116;
                                        r1 = r0;
                                        goto L_0x1013;
                                    L_0x0389:
                                        r54 = r6;
                                        r56 = r7;
                                        r5 = NUM; // 0x7f000789 float:1.701803E38 double:1.0527098025E-314;
                                        r4 = r22;
                                        r6 = r25;
                                    L_0x0394:
                                        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0f87, all -> 0x0fd2 }
                                        if (r7 == 0) goto L_0x03d3;
                                    L_0x0398:
                                        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r7.<init>();	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r57 = r8;	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r8 = "colorFormat = ";	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r7.append(r8);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r7.append(r5);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        r7 = r7.toString();	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        org.telegram.messenger.FileLog.m0d(r7);	 Catch:{ Exception -> 0x03af, all -> 0x0240 }
                                        goto L_0x03d5;
                                    L_0x03af:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r14 = r42;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r108 = r47;
                                        r8 = r50;
                                        r4 = r51;
                                        r7 = r52;
                                        r3 = r56;
                                    L_0x03ce:
                                        r15 = r116;
                                    L_0x03d0:
                                        r1 = r0;
                                        goto L_0x1013;
                                    L_0x03d3:
                                        r57 = r8;
                                    L_0x03d5:
                                        r7 = r42;
                                        r8 = 0;
                                        r9 = r42;
                                        r22 = r14 * r9;
                                        r59 = r8;
                                        r8 = 3;
                                        r22 = r22 * 3;
                                        r22 = r22 / 2;	 Catch:{ Exception -> 0x0f64, all -> 0x0f4a }
                                        if (r6 != 0) goto L_0x0440;
                                    L_0x03e5:
                                        r25 = r9 % 16;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        if (r25 == 0) goto L_0x0403;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x03e9:
                                        r25 = r9 % 16;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r26 = 16;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r25 = 16 - r25;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r7 = r7 + r25;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r25 = r7 - r9;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r25 = r25 * r14;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r26 = r25 * 5;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r26 = r26 / 4;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r22 = r22 + r26;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r60 = r10;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = r22;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r59 = r25;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        goto L_0x0489;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0403:
                                        r60 = r10;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        goto L_0x0487;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0407:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r1 = r0;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r5 = r12;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r110 = r14;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r82 = r15;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r11 = r41;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0410:
                                        r4 = r45;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r81 = r46;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r3 = r49;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r15 = r116;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r14 = r9;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r9 = r47;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        goto L_0x1295;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x041d:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r97 = r1;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r91 = r2;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r98 = r13;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r110 = r14;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r82 = r15;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r107 = r41;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x042a:
                                        r6 = r44;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r104 = r45;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r81 = r46;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r108 = r47;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = r50;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r4 = r51;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r7 = r52;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r3 = r56;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x043a:
                                        r15 = r116;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r1 = r0;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r14 = r9;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        goto L_0x1013;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0440:
                                        r8 = 1;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        if (r6 != r8) goto L_0x0463;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0443:
                                        r8 = r3.toLowerCase();	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r60 = r10;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r10 = "lge";	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = r8.equals(r10);	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        if (r8 != 0) goto L_0x0487;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0451:
                                        r8 = r14 * r9;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = r8 + 2047;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = r8 & -2048;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r10 = r14 * r9;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r10 = r8 - r10;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r22 = r22 + r10;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r59 = r10;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0460:
                                        r8 = r22;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        goto L_0x0489;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0463:
                                        r60 = r10;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = 5;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        if (r6 != r8) goto L_0x0469;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0468:
                                        goto L_0x0487;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0469:
                                        r8 = 3;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        if (r6 != r8) goto L_0x0487;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x046c:
                                        r8 = "baidu";	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = r3.equals(r8);	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        if (r8 == 0) goto L_0x0487;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                    L_0x0474:
                                        r8 = r9 % 16;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r10 = 16;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = 16 - r8;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r7 = r7 + r8;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = r7 - r9;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r8 = r8 * r14;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r10 = r8 * 5;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r10 = r10 / 4;	 Catch:{ Exception -> 0x041d, all -> 0x0407 }
                                        r22 = r22 + r10;
                                        r59 = r8;
                                        goto L_0x0460;
                                    L_0x0487:
                                        r8 = r22;
                                    L_0x0489:
                                        r13.selectTrack(r1);	 Catch:{ Exception -> 0x0f64, all -> 0x0f4a }
                                        r10 = r13.getTrackFormat(r1);	 Catch:{ Exception -> 0x0f64, all -> 0x0f4a }
                                        r22 = 0;
                                        if (r2 < 0) goto L_0x0515;
                                    L_0x0494:
                                        r13.selectTrack(r2);	 Catch:{ Exception -> 0x04f2, all -> 0x04db }
                                        r25 = r13.getTrackFormat(r2);	 Catch:{ Exception -> 0x04f2, all -> 0x04db }
                                        r68 = r25;	 Catch:{ Exception -> 0x04f2, all -> 0x04db }
                                        r69 = r3;	 Catch:{ Exception -> 0x04f2, all -> 0x04db }
                                        r3 = "max-input-size";	 Catch:{ Exception -> 0x04f2, all -> 0x04db }
                                        r70 = r6;	 Catch:{ Exception -> 0x04f2, all -> 0x04db }
                                        r6 = r68;	 Catch:{ Exception -> 0x04f2, all -> 0x04db }
                                        r3 = r6.getInteger(r3);	 Catch:{ Exception -> 0x04f2, all -> 0x04db }
                                        r25 = java.nio.ByteBuffer.allocateDirect(r3);	 Catch:{ Exception -> 0x04f2, all -> 0x04db }
                                        r22 = r25;
                                        r71 = r3;
                                        r72 = r7;
                                        r3 = r41;
                                        r7 = 1;
                                        r25 = r3.addTrack(r6, r7);	 Catch:{ Exception -> 0x04cc, all -> 0x04c2 }
                                        r24 = r25;
                                        r6 = r22;
                                        r7 = r24;
                                        goto L_0x0521;
                                    L_0x04c2:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r11 = r3;
                                        r5 = r12;
                                        r110 = r14;
                                        r82 = r15;
                                        goto L_0x0410;
                                    L_0x04cc:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r107 = r3;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        goto L_0x042a;
                                    L_0x04db:
                                        r0 = move-exception;
                                        r3 = r41;
                                        r1 = r0;
                                        r11 = r3;
                                        r5 = r12;
                                        r110 = r14;
                                        r82 = r15;
                                        r4 = r45;
                                        r81 = r46;
                                        r3 = r49;
                                        r15 = r116;
                                        r14 = r9;
                                        r9 = r47;
                                        goto L_0x1295;
                                    L_0x04f2:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r108 = r47;
                                        r8 = r50;
                                        r4 = r51;
                                        r7 = r52;
                                        r3 = r56;
                                        r15 = r116;
                                        r1 = r0;
                                        r14 = r9;
                                        goto L_0x1013;
                                    L_0x0515:
                                        r69 = r3;
                                        r70 = r6;
                                        r72 = r7;
                                        r3 = r41;
                                        r6 = r22;
                                        r7 = r24;
                                    L_0x0521:
                                        r74 = r3;
                                        r73 = r4;
                                        r3 = 0;
                                        r76 = r7;
                                        r75 = r8;
                                        r7 = r47;
                                        r22 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1));
                                        if (r22 <= 0) goto L_0x056c;
                                    L_0x0531:
                                        r3 = 0;
                                        r13.seekTo(r7, r3);	 Catch:{ Exception -> 0x054d, all -> 0x0538 }
                                        r77 = r7;
                                        goto L_0x0574;
                                    L_0x0538:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r5 = r12;
                                        r110 = r14;
                                        r82 = r15;
                                        r4 = r45;
                                        r81 = r46;
                                        r3 = r49;
                                        r11 = r74;
                                        r15 = r116;
                                        r14 = r9;
                                        r9 = r7;
                                        goto L_0x1295;
                                    L_0x054d:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r108 = r7;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r8 = r50;
                                        r4 = r51;
                                        r7 = r52;
                                        r3 = r56;
                                        r107 = r74;
                                        goto L_0x043a;
                                    L_0x056c:
                                        r3 = 0;
                                        r77 = r7;
                                        r7 = 0;
                                        r13.seekTo(r7, r3);	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                    L_0x0574:
                                        r3 = "video/avc";	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r3 = android.media.MediaFormat.createVideoFormat(r3, r14, r9);	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r4 = "color-format";	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r3.setInteger(r4, r5);	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r4 = "bitrate";	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        if (r15 <= 0) goto L_0x0585;	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                    L_0x0583:
                                        r7 = r15;	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        goto L_0x0588;	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                    L_0x0585:
                                        r7 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                    L_0x0588:
                                        r3.setInteger(r4, r7);	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r4 = "frame-rate";	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r7 = 25;	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r3.setInteger(r4, r7);	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r4 = "i-frame-interval";	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r7 = 10;	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r3.setInteger(r4, r7);	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r7 = 18;
                                        if (r4 >= r7) goto L_0x05e1;
                                    L_0x059f:
                                        r4 = "stride";	 Catch:{ Exception -> 0x05c2, all -> 0x05ac }
                                        r7 = r14 + 32;	 Catch:{ Exception -> 0x05c2, all -> 0x05ac }
                                        r3.setInteger(r4, r7);	 Catch:{ Exception -> 0x05c2, all -> 0x05ac }
                                        r4 = "slice-height";	 Catch:{ Exception -> 0x05c2, all -> 0x05ac }
                                        r3.setInteger(r4, r9);	 Catch:{ Exception -> 0x05c2, all -> 0x05ac }
                                        goto L_0x05e1;
                                    L_0x05ac:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r5 = r12;
                                        r110 = r14;
                                        r82 = r15;
                                        r4 = r45;
                                        r81 = r46;
                                    L_0x05b7:
                                        r3 = r49;
                                        r11 = r74;
                                        r15 = r116;
                                        r14 = r9;
                                        r9 = r77;
                                        goto L_0x1295;
                                    L_0x05c2:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r8 = r50;
                                        r4 = r51;
                                    L_0x05d7:
                                        r7 = r52;
                                    L_0x05d9:
                                        r3 = r56;
                                    L_0x05db:
                                        r107 = r74;
                                        r108 = r77;
                                        goto L_0x043a;
                                    L_0x05e1:
                                        r4 = "video/avc";	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r4 = android.media.MediaCodec.createEncoderByType(r4);	 Catch:{ Exception -> 0x0f27, all -> 0x0f0d }
                                        r7 = 1;
                                        r8 = 0;
                                        r4.configure(r3, r8, r8, r7);	 Catch:{ Exception -> 0x0ef1, all -> 0x0f0d }
                                        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0ef1, all -> 0x0f0d }
                                        r8 = 18;
                                        if (r7 < r8) goto L_0x0627;
                                    L_0x05f2:
                                        r7 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x0613, all -> 0x05ac }
                                        r8 = r4.createInputSurface();	 Catch:{ Exception -> 0x0613, all -> 0x05ac }
                                        r7.<init>(r8);	 Catch:{ Exception -> 0x0613, all -> 0x05ac }
                                        r7.makeCurrent();	 Catch:{ Exception -> 0x05ff, all -> 0x05ac }
                                        goto L_0x0629;
                                    L_0x05ff:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r8 = r50;
                                        goto L_0x05d9;
                                    L_0x0613:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r8 = r50;
                                        goto L_0x05d7;
                                    L_0x0627:
                                        r7 = r52;
                                    L_0x0629:
                                        r4.start();	 Catch:{ Exception -> 0x0ed4, all -> 0x0f0d }
                                        r8 = "mime";	 Catch:{ Exception -> 0x0ed4, all -> 0x0f0d }
                                        r8 = r10.getString(r8);	 Catch:{ Exception -> 0x0ed4, all -> 0x0f0d }
                                        r8 = android.media.MediaCodec.createDecoderByType(r8);	 Catch:{ Exception -> 0x0ed4, all -> 0x0f0d }
                                        r79 = r3;
                                        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0eb9, all -> 0x0f0d }
                                        r80 = r11;
                                        r11 = 18;
                                        if (r3 < r11) goto L_0x065b;
                                    L_0x0640:
                                        r3 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0648, all -> 0x05ac }
                                        r3.<init>();	 Catch:{ Exception -> 0x0648, all -> 0x05ac }
                                        r11 = r46;
                                        goto L_0x0662;
                                    L_0x0648:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        goto L_0x05d9;
                                    L_0x065b:
                                        r3 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0eb9, all -> 0x0f0d }
                                        r11 = r46;
                                        r3.<init>(r14, r9, r11);	 Catch:{ Exception -> 0x0e9c, all -> 0x0e82 }
                                    L_0x0662:
                                        r81 = r11;
                                        r11 = r3.getSurface();	 Catch:{ Exception -> 0x0e69, all -> 0x0e51 }
                                        r83 = r5;
                                        r82 = r15;
                                        r5 = 0;
                                        r15 = 0;
                                        r8.configure(r10, r11, r5, r15);	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                        r8.start();	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                        r11 = 0;	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                        r15 = 0;	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                        r22 = 0;	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                        r84 = r5;	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                        r85 = r10;
                                        r10 = 21;
                                        if (r5 >= r10) goto L_0x06b3;
                                    L_0x0684:
                                        r5 = r8.getInputBuffers();	 Catch:{ Exception -> 0x06a4, all -> 0x069b }
                                        r11 = r5;	 Catch:{ Exception -> 0x06a4, all -> 0x069b }
                                        r5 = r4.getOutputBuffers();	 Catch:{ Exception -> 0x06a4, all -> 0x069b }
                                        r15 = r5;	 Catch:{ Exception -> 0x06a4, all -> 0x069b }
                                        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x06a4, all -> 0x069b }
                                        r10 = 18;	 Catch:{ Exception -> 0x06a4, all -> 0x069b }
                                        if (r5 >= r10) goto L_0x06b3;	 Catch:{ Exception -> 0x06a4, all -> 0x069b }
                                    L_0x0694:
                                        r5 = r4.getInputBuffers();	 Catch:{ Exception -> 0x06a4, all -> 0x069b }
                                        r22 = r5;
                                        goto L_0x06b5;
                                    L_0x069b:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r5 = r12;
                                        r110 = r14;
                                        r4 = r45;
                                        goto L_0x05b7;
                                    L_0x06a4:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r6 = r44;
                                        r104 = r45;
                                        goto L_0x05db;
                                    L_0x06b3:
                                        r5 = r22;
                                    L_0x06b5:
                                        r115.checkConversionCanceled();	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                    L_0x06b8:
                                        if (r54 != 0) goto L_0x0e0f;	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                    L_0x06ba:
                                        r115.checkConversionCanceled();	 Catch:{ Exception -> 0x0e3a, all -> 0x0e24 }
                                        r86 = r9;
                                        if (r60 != 0) goto L_0x08cd;
                                    L_0x06c1:
                                        r22 = 0;
                                        r24 = r13.getSampleTrackIndex();	 Catch:{ Exception -> 0x08b5, all -> 0x08a2 }
                                        r87 = r24;
                                        r9 = r87;
                                        if (r9 != r1) goto L_0x074e;
                                    L_0x06cd:
                                        r88 = r14;
                                        r89 = r15;
                                        r14 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
                                        r10 = r8.dequeueInputBuffer(r14);	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        if (r10 < 0) goto L_0x0715;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                    L_0x06d9:
                                        r14 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r15 = 21;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        if (r14 >= r15) goto L_0x06e2;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                    L_0x06df:
                                        r14 = r11[r10];	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        goto L_0x06e6;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                    L_0x06e2:
                                        r14 = r8.getInputBuffer(r10);	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                    L_0x06e6:
                                        r15 = 0;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r24 = r13.readSampleData(r14, r15);	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r15 = r24;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        if (r15 >= 0) goto L_0x0701;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                    L_0x06ef:
                                        r63 = 0;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r64 = 0;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r65 = 0;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r67 = 4;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r61 = r8;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r62 = r10;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r61.queueInputBuffer(r62, r63, r64, r65, r67);	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r60 = 1;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        goto L_0x0715;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                    L_0x0701:
                                        r63 = 0;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r65 = r13.getSampleTime();	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r67 = 0;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r61 = r8;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r62 = r10;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r64 = r15;	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r61.queueInputBuffer(r62, r63, r64, r65, r67);	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r13.advance();	 Catch:{ Exception -> 0x0739, all -> 0x0728 }
                                        r91 = r2;
                                        r92 = r6;
                                        r90 = r11;
                                        r6 = r44;
                                    L_0x071e:
                                        r2 = r45;
                                        r11 = r74;
                                        r14 = r76;
                                        r15 = r116;
                                        goto L_0x0862;
                                    L_0x0728:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r5 = r12;
                                        r4 = r45;
                                        r3 = r49;
                                        r11 = r74;
                                        r9 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        goto L_0x0253;
                                    L_0x0739:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r6 = r44;
                                    L_0x0742:
                                        r104 = r45;
                                        r107 = r74;
                                        r108 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        goto L_0x03ce;
                                    L_0x074e:
                                        r88 = r14;
                                        r89 = r15;
                                        r10 = -1;
                                        if (r2 == r10) goto L_0x084d;
                                    L_0x0755:
                                        if (r9 != r2) goto L_0x084d;
                                    L_0x0757:
                                        r10 = 0;
                                        r14 = r13.readSampleData(r6, r10);	 Catch:{ Exception -> 0x0835, all -> 0x0822 }
                                        r15 = r44;
                                        r15.size = r14;	 Catch:{ Exception -> 0x080b, all -> 0x0822 }
                                        r14 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x080b, all -> 0x0822 }
                                        r10 = 21;
                                        if (r14 >= r10) goto L_0x0779;
                                    L_0x0766:
                                        r10 = 0;
                                        r6.position(r10);	 Catch:{ Exception -> 0x0770, all -> 0x0728 }
                                        r10 = r15.size;	 Catch:{ Exception -> 0x0770, all -> 0x0728 }
                                        r6.limit(r10);	 Catch:{ Exception -> 0x0770, all -> 0x0728 }
                                        goto L_0x0779;
                                    L_0x0770:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r6 = r15;
                                        goto L_0x0742;
                                    L_0x0779:
                                        r10 = r15.size;	 Catch:{ Exception -> 0x080b, all -> 0x0822 }
                                        if (r10 < 0) goto L_0x0789;
                                    L_0x077d:
                                        r90 = r11;
                                        r10 = r13.getSampleTime();	 Catch:{ Exception -> 0x0770, all -> 0x0728 }
                                        r15.presentationTimeUs = r10;	 Catch:{ Exception -> 0x0770, all -> 0x0728 }
                                        r13.advance();	 Catch:{ Exception -> 0x0770, all -> 0x0728 }
                                        goto L_0x0790;
                                    L_0x0789:
                                        r90 = r11;
                                        r10 = 0;
                                        r15.size = r10;	 Catch:{ Exception -> 0x080b, all -> 0x0822 }
                                        r60 = 1;	 Catch:{ Exception -> 0x080b, all -> 0x0822 }
                                    L_0x0790:
                                        r10 = r15.size;	 Catch:{ Exception -> 0x080b, all -> 0x0822 }
                                        if (r10 <= 0) goto L_0x07fc;
                                    L_0x0794:
                                        r10 = 0;
                                        r14 = (r19 > r10 ? 1 : (r19 == r10 ? 0 : -1));
                                        if (r14 < 0) goto L_0x07a8;
                                    L_0x079a:
                                        r10 = r15.presentationTimeUs;	 Catch:{ Exception -> 0x0770, all -> 0x0728 }
                                        r14 = (r10 > r19 ? 1 : (r10 == r19 ? 0 : -1));
                                        if (r14 >= 0) goto L_0x07a1;
                                    L_0x07a0:
                                        goto L_0x07a8;
                                    L_0x07a1:
                                        r91 = r2;
                                        r92 = r6;
                                        r6 = r15;
                                        goto L_0x071e;
                                    L_0x07a8:
                                        r10 = 0;
                                        r15.offset = r10;	 Catch:{ Exception -> 0x080b, all -> 0x0822 }
                                        r11 = r13.getSampleFlags();	 Catch:{ Exception -> 0x080b, all -> 0x0822 }
                                        r15.flags = r11;	 Catch:{ Exception -> 0x080b, all -> 0x0822 }
                                        r11 = r74;
                                        r14 = r76;
                                        r18 = r11.writeSampleData(r14, r6, r15, r10);	 Catch:{ Exception -> 0x07e5, all -> 0x07d4 }
                                        if (r18 == 0) goto L_0x07c9;
                                    L_0x07bb:
                                        r91 = r2;
                                        r92 = r6;
                                        r6 = r15;
                                        r2 = r45;
                                        r15 = r116;
                                        r12.didWriteData(r15, r2, r10, r10);	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        goto L_0x0862;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x07c9:
                                        r91 = r2;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r92 = r6;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r6 = r15;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r2 = r45;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r15 = r116;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        goto L_0x0862;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x07d4:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r15 = r116;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r1 = r0;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r5 = r12;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r4 = r45;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r3 = r49;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r9 = r77;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r14 = r86;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r110 = r88;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        goto L_0x1295;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x07e5:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r91 = r2;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r6 = r15;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r15 = r116;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r97 = r1;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r107 = r11;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r98 = r13;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r104 = r45;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r108 = r77;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r14 = r86;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r110 = r88;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r1 = r0;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        goto L_0x1013;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x07fc:
                                        r91 = r2;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r92 = r6;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r6 = r15;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r2 = r45;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r11 = r74;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r14 = r76;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r15 = r116;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        goto L_0x0862;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x080b:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r91 = r2;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r6 = r15;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r15 = r116;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r97 = r1;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r98 = r13;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r104 = r45;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r107 = r74;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r108 = r77;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r14 = r86;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r110 = r88;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r1 = r0;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        goto L_0x1013;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x0822:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r11 = r74;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r15 = r116;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r1 = r0;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r5 = r12;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r4 = r45;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r3 = r49;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r9 = r77;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r14 = r86;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r110 = r88;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        goto L_0x1295;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x0835:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r91 = r2;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r6 = r44;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r15 = r116;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r97 = r1;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r98 = r13;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r104 = r45;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r107 = r74;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r108 = r77;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r14 = r86;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r110 = r88;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r1 = r0;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        goto L_0x1013;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x084d:
                                        r91 = r2;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r92 = r6;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r90 = r11;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r6 = r44;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r2 = r45;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r11 = r74;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r14 = r76;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r15 = r116;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r10 = -1;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        if (r9 != r10) goto L_0x0862;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x0860:
                                        r22 = 1;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x0862:
                                        if (r22 == 0) goto L_0x08e1;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x0864:
                                        r93 = r9;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r9 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r24 = r8.dequeueInputBuffer(r9);	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r9 = r24;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        if (r9 < 0) goto L_0x08e1;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                    L_0x0870:
                                        r63 = 0;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r64 = 0;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r65 = 0;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r67 = 4;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r61 = r8;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r62 = r9;	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r61.queueInputBuffer(r62, r63, r64, r65, r67);	 Catch:{ Exception -> 0x0891, all -> 0x0883 }
                                        r60 = 1;
                                        goto L_0x08e1;
                                    L_0x0883:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r4 = r2;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        goto L_0x1295;
                                    L_0x0891:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r104 = r2;
                                        r107 = r11;
                                        r98 = r13;
                                        r108 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        goto L_0x03d0;
                                    L_0x08a2:
                                        r0 = move-exception;
                                        r11 = r74;
                                        r15 = r116;
                                        r1 = r0;
                                        r5 = r12;
                                        r110 = r14;
                                        r4 = r45;
                                        r3 = r49;
                                        r9 = r77;
                                        r14 = r86;
                                        goto L_0x1295;
                                    L_0x08b5:
                                        r0 = move-exception;
                                        r91 = r2;
                                        r6 = r44;
                                        r15 = r116;
                                        r97 = r1;
                                        r98 = r13;
                                        r110 = r14;
                                        r104 = r45;
                                        r107 = r74;
                                        r108 = r77;
                                        r14 = r86;
                                        r1 = r0;
                                        goto L_0x1013;
                                    L_0x08cd:
                                        r91 = r2;
                                        r92 = r6;
                                        r90 = r11;
                                        r88 = r14;
                                        r89 = r15;
                                        r6 = r44;
                                        r2 = r45;
                                        r11 = r74;
                                        r14 = r76;
                                        r15 = r116;
                                    L_0x08e1:
                                        if (r80 != 0) goto L_0x08e5;
                                    L_0x08e3:
                                        r9 = 1;
                                        goto L_0x08e6;
                                    L_0x08e5:
                                        r9 = 0;
                                    L_0x08e6:
                                        r10 = r9;
                                        r94 = r14;
                                        r14 = r23;
                                        r9 = 1;
                                    L_0x08ec:
                                        if (r10 != 0) goto L_0x090a;
                                    L_0x08ee:
                                        if (r9 == 0) goto L_0x08f1;
                                    L_0x08f0:
                                        goto L_0x090a;
                                        r45 = r2;
                                        r44 = r6;
                                        r74 = r11;
                                        r23 = r14;
                                        r9 = r86;
                                        r14 = r88;
                                        r15 = r89;
                                        r11 = r90;
                                        r2 = r91;
                                        r6 = r92;
                                        r76 = r94;
                                        goto L_0x06b8;
                                    L_0x090a:
                                        r115.checkConversionCanceled();	 Catch:{ Exception -> 0x0dfd, all -> 0x0deb }
                                        r96 = r9;	 Catch:{ Exception -> 0x0dfd, all -> 0x0deb }
                                        r95 = r10;	 Catch:{ Exception -> 0x0dfd, all -> 0x0deb }
                                        r9 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;	 Catch:{ Exception -> 0x0dfd, all -> 0x0deb }
                                        r22 = r4.dequeueOutputBuffer(r6, r9);	 Catch:{ Exception -> 0x0dfd, all -> 0x0deb }
                                        r9 = r22;
                                        r10 = -1;
                                        if (r9 != r10) goto L_0x092e;
                                    L_0x091c:
                                        r10 = 0;
                                        r97 = r1;
                                        r104 = r2;
                                        r99 = r5;
                                        r96 = r10;
                                    L_0x0925:
                                        r98 = r13;
                                        r1 = r14;
                                        r14 = r86;
                                        r2 = r88;
                                        goto L_0x0b52;
                                    L_0x092e:
                                        r10 = -3;
                                        if (r9 != r10) goto L_0x0956;
                                    L_0x0931:
                                        r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0944, all -> 0x0883 }
                                        r97 = r1;
                                        r1 = 21;
                                        if (r10 >= r1) goto L_0x093f;
                                    L_0x0939:
                                        r1 = r4.getOutputBuffers();	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r89 = r1;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                    L_0x093f:
                                        r104 = r2;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r99 = r5;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        goto L_0x0925;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                    L_0x0944:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r97 = r1;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r1 = r0;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r104 = r2;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r107 = r11;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r98 = r13;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r108 = r77;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r14 = r86;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r110 = r88;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        goto L_0x1013;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                    L_0x0956:
                                        r97 = r1;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r1 = -2;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        if (r9 != r1) goto L_0x097b;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                    L_0x095b:
                                        r1 = r4.getOutputFormat();	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r10 = -5;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        if (r14 != r10) goto L_0x096a;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                    L_0x0962:
                                        r10 = 0;	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r22 = r11.addTrack(r1, r10);	 Catch:{ Exception -> 0x096b, all -> 0x0883 }
                                        r1 = r22;
                                        r14 = r1;
                                    L_0x096a:
                                        goto L_0x093f;
                                    L_0x096b:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r104 = r2;
                                        r107 = r11;
                                        r98 = r13;
                                    L_0x0973:
                                        r108 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        goto L_0x1013;
                                    L_0x097b:
                                        if (r9 >= 0) goto L_0x09b6;
                                    L_0x097d:
                                        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x09a6, all -> 0x0996 }
                                        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x09a6, all -> 0x0996 }
                                        r10.<init>();	 Catch:{ Exception -> 0x09a6, all -> 0x0996 }
                                        r98 = r13;
                                        r13 = "unexpected result from encoder.dequeueOutputBuffer: ";	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r10.append(r13);	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r10.append(r9);	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r10 = r10.toString();	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r1.<init>(r10);	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        throw r1;	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                    L_0x0996:
                                        r0 = move-exception;
                                        r98 = r13;
                                        r1 = r0;
                                        r4 = r2;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        goto L_0x1295;
                                    L_0x09a6:
                                        r0 = move-exception;
                                        r98 = r13;
                                        r1 = r0;
                                        r104 = r2;
                                        r107 = r11;
                                        r108 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        goto L_0x1013;
                                    L_0x09b6:
                                        r98 = r13;
                                        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0ddd, all -> 0x0dcb }
                                        r10 = 21;
                                        if (r1 >= r10) goto L_0x09d8;
                                    L_0x09be:
                                        r1 = r89[r9];	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        goto L_0x09dc;
                                    L_0x09c1:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r4 = r2;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        r13 = r98;
                                        goto L_0x1295;
                                    L_0x09d1:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r104 = r2;
                                    L_0x09d5:
                                        r107 = r11;
                                        goto L_0x0973;
                                    L_0x09d8:
                                        r1 = r4.getOutputBuffer(r9);	 Catch:{ Exception -> 0x0ddd, all -> 0x0dcb }
                                    L_0x09dc:
                                        if (r1 != 0) goto L_0x09fc;
                                    L_0x09de:
                                        r10 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r13.<init>();	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r99 = r5;	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r5 = "encoderOutputBuffer ";	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r13.append(r5);	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r13.append(r9);	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r5 = " was null";	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r13.append(r5);	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r5 = r13.toString();	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r10.<init>(r5);	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        throw r10;	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                    L_0x09fc:
                                        r99 = r5;
                                        r5 = r6.size;	 Catch:{ Exception -> 0x0ddd, all -> 0x0dcb }
                                        r13 = 1;
                                        if (r5 <= r13) goto L_0x0b37;
                                    L_0x0a03:
                                        r5 = r6.flags;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r5 = r5 & 2;
                                        if (r5 != 0) goto L_0x0a1f;
                                    L_0x0a09:
                                        r5 = r11.writeSampleData(r14, r1, r6, r13);	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        if (r5 == 0) goto L_0x0a13;	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                    L_0x0a0f:
                                        r5 = 0;	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                        r12.didWriteData(r15, r2, r5, r5);	 Catch:{ Exception -> 0x09d1, all -> 0x09c1 }
                                    L_0x0a13:
                                        r100 = r1;
                                        r104 = r2;
                                        r103 = r14;
                                        r14 = r86;
                                        r2 = r88;
                                        goto L_0x0b41;
                                    L_0x0a1f:
                                        r5 = -5;
                                        if (r14 != r5) goto L_0x0b37;
                                    L_0x0a22:
                                        r5 = r6.size;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r5 = new byte[r5];	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r13 = r6.offset;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r10 = r6.size;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r13 = r13 + r10;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r1.limit(r13);	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r10 = r6.offset;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r1.position(r10);	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r1.get(r5);	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r10 = 0;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r13 = 0;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r100 = r1;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r1 = r6.size;	 Catch:{ Exception -> 0x0b29, all -> 0x0b16 }
                                        r101 = r10;
                                        r10 = 1;
                                        r1 = r1 - r10;
                                    L_0x0a40:
                                        if (r1 < 0) goto L_0x0aca;
                                    L_0x0a42:
                                        r10 = 3;
                                        if (r1 <= r10) goto L_0x0aca;
                                    L_0x0a45:
                                        r10 = r5[r1];	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r102 = r13;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r13 = 1;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        if (r10 != r13) goto L_0x0a9e;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                    L_0x0a4c:
                                        r10 = r1 + -1;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r10 = r5[r10];	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        if (r10 != 0) goto L_0x0a9e;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                    L_0x0a52:
                                        r10 = r1 + -2;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r10 = r5[r10];	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        if (r10 != 0) goto L_0x0a9e;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                    L_0x0a58:
                                        r10 = r1 + -3;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r10 = r5[r10];	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        if (r10 != 0) goto L_0x0a9e;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                    L_0x0a5e:
                                        r10 = r1 + -3;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r10 = java.nio.ByteBuffer.allocate(r10);	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r13 = r6.size;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r22 = r1 + -3;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r13 = r13 - r22;	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r13 = java.nio.ByteBuffer.allocate(r13);	 Catch:{ Exception -> 0x0abc, all -> 0x0aac }
                                        r103 = r14;
                                        r14 = r1 + -3;
                                        r104 = r2;
                                        r2 = 0;
                                        r14 = r10.put(r5, r2, r14);	 Catch:{ Exception -> 0x0a9a, all -> 0x0a8d }
                                        r14.position(r2);	 Catch:{ Exception -> 0x0a9a, all -> 0x0a8d }
                                        r2 = r1 + -3;	 Catch:{ Exception -> 0x0a9a, all -> 0x0a8d }
                                        r14 = r6.size;	 Catch:{ Exception -> 0x0a9a, all -> 0x0a8d }
                                        r22 = r1 + -3;	 Catch:{ Exception -> 0x0a9a, all -> 0x0a8d }
                                        r14 = r14 - r22;	 Catch:{ Exception -> 0x0a9a, all -> 0x0a8d }
                                        r2 = r13.put(r5, r2, r14);	 Catch:{ Exception -> 0x0a9a, all -> 0x0a8d }
                                        r14 = 0;	 Catch:{ Exception -> 0x0a9a, all -> 0x0a8d }
                                        r2.position(r14);	 Catch:{ Exception -> 0x0a9a, all -> 0x0a8d }
                                        goto L_0x0ad4;
                                    L_0x0a8d:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        goto L_0x0bb7;
                                    L_0x0a9a:
                                        r0 = move-exception;
                                        r1 = r0;
                                        goto L_0x09d5;
                                    L_0x0a9e:
                                        r104 = r2;
                                        r103 = r14;
                                        r1 = r1 + -1;
                                        r13 = r102;
                                        r14 = r103;
                                        r2 = r104;
                                        r10 = 1;
                                        goto L_0x0a40;
                                    L_0x0aac:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r4 = r2;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        r13 = r98;
                                        goto L_0x1295;
                                    L_0x0abc:
                                        r0 = move-exception;
                                        r104 = r2;
                                        r1 = r0;
                                        r107 = r11;
                                        r108 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        goto L_0x1013;
                                    L_0x0aca:
                                        r104 = r2;
                                        r102 = r13;
                                        r103 = r14;
                                        r10 = r101;
                                        r13 = r102;
                                    L_0x0ad4:
                                        r1 = "video/avc";	 Catch:{ Exception -> 0x0b0a, all -> 0x0af9 }
                                        r14 = r86;
                                        r2 = r88;
                                        r1 = android.media.MediaFormat.createVideoFormat(r1, r2, r14);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        if (r10 == 0) goto L_0x0aef;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0ae0:
                                        if (r13 == 0) goto L_0x0aef;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0ae2:
                                        r105 = r5;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r5 = "csd-0";	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r1.setByteBuffer(r5, r10);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r5 = "csd-1";	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r1.setByteBuffer(r5, r13);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        goto L_0x0af1;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0aef:
                                        r105 = r5;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0af1:
                                        r5 = 0;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r22 = r11.addTrack(r1, r5);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r1 = r22;
                                        goto L_0x0b43;
                                    L_0x0af9:
                                        r0 = move-exception;
                                        r14 = r86;
                                        r1 = r0;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r110 = r88;
                                        r13 = r98;
                                        r4 = r104;
                                        goto L_0x1295;
                                    L_0x0b0a:
                                        r0 = move-exception;
                                        r14 = r86;
                                        r1 = r0;
                                        r107 = r11;
                                        r108 = r77;
                                        r110 = r88;
                                        goto L_0x1013;
                                    L_0x0b16:
                                        r0 = move-exception;
                                        r104 = r2;
                                        r14 = r86;
                                        r1 = r0;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r110 = r88;
                                        r13 = r98;
                                        r4 = r104;
                                        goto L_0x1295;
                                    L_0x0b29:
                                        r0 = move-exception;
                                        r104 = r2;
                                        r14 = r86;
                                        r1 = r0;
                                        r107 = r11;
                                        r108 = r77;
                                        r110 = r88;
                                        goto L_0x1013;
                                    L_0x0b37:
                                        r100 = r1;
                                        r104 = r2;
                                        r103 = r14;
                                        r14 = r86;
                                        r2 = r88;
                                    L_0x0b41:
                                        r1 = r103;
                                    L_0x0b43:
                                        r5 = r6.flags;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r5 = r5 & 4;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        if (r5 == 0) goto L_0x0b4b;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                    L_0x0b49:
                                        r5 = 1;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        goto L_0x0b4c;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                    L_0x0b4b:
                                        r5 = 0;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                    L_0x0b4c:
                                        r54 = r5;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r5 = 0;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r4.releaseOutputBuffer(r9, r5);	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                    L_0x0b52:
                                        r5 = -1;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        if (r9 == r5) goto L_0x0b69;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r88 = r2;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r86 = r14;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r10 = r95;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r9 = r96;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r13 = r98;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r5 = r99;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r2 = r104;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r14 = r1;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r1 = r97;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        goto L_0x08ec;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                    L_0x0b69:
                                        if (r80 != 0) goto L_0x0da9;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                    L_0x0b6b:
                                        r106 = r9;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r9 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r13 = r8.dequeueOutputBuffer(r6, r9);	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r9 = r13;
                                        if (r9 != r5) goto L_0x0b81;
                                    L_0x0b76:
                                        r10 = 0;
                                        r111 = r1;
                                        r110 = r2;
                                        r107 = r11;
                                        r108 = r77;
                                        goto L_0x0db3;
                                    L_0x0b81:
                                        r10 = -3;
                                        if (r9 != r10) goto L_0x0b8e;
                                    L_0x0b84:
                                        r111 = r1;
                                        r110 = r2;
                                        r107 = r11;
                                        r108 = r77;
                                        goto L_0x0db1;
                                    L_0x0b8e:
                                        r10 = -2;
                                        if (r9 != r10) goto L_0x0bc7;
                                    L_0x0b91:
                                        r10 = r8.getOutputFormat();	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r13 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        if (r13 == 0) goto L_0x0bad;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0b99:
                                        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r13.<init>();	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r5 = "newFormat = ";	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r13.append(r5);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r13.append(r10);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r5 = r13.toString();	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        org.telegram.messenger.FileLog.m0d(r5);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0bad:
                                        goto L_0x0b84;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0bae:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r1 = r0;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r110 = r2;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r5 = r12;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r3 = r49;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r9 = r77;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0bb7:
                                        r13 = r98;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r4 = r104;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        goto L_0x1295;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0bbd:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r1 = r0;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r110 = r2;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r107 = r11;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0bc3:
                                        r108 = r77;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        goto L_0x1013;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0bc7:
                                        if (r9 >= 0) goto L_0x0be0;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0bc9:
                                        r5 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r10.<init>();	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r13 = "unexpected result from decoder.dequeueOutputBuffer: ";	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r10.append(r13);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r10.append(r9);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r10 = r10.toString();	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        r5.<init>(r10);	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        throw r5;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                    L_0x0be0:
                                        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        r10 = 18;
                                        if (r5 < r10) goto L_0x0bf0;
                                    L_0x0be6:
                                        r5 = r6.size;	 Catch:{ Exception -> 0x0bbd, all -> 0x0bae }
                                        if (r5 == 0) goto L_0x0bec;
                                    L_0x0bea:
                                        r5 = 1;
                                        goto L_0x0bed;
                                    L_0x0bec:
                                        r5 = 0;
                                    L_0x0bed:
                                        r107 = r11;
                                        goto L_0x0c1a;
                                    L_0x0bf0:
                                        r5 = r6.size;	 Catch:{ Exception -> 0x0d9f, all -> 0x0d8e }
                                        if (r5 != 0) goto L_0x0c17;
                                    L_0x0bf4:
                                        r107 = r11;
                                        r10 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r22 = 0;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r5 = (r10 > r22 ? 1 : (r10 == r22 ? 0 : -1));	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        if (r5 == 0) goto L_0x0bff;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0bfe:
                                        goto L_0x0c19;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0bff:
                                        r5 = 0;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        goto L_0x0c1a;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0c01:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r1 = r0;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r110 = r2;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r5 = r12;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r3 = r49;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r9 = r77;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r13 = r98;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r4 = r104;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r11 = r107;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        goto L_0x1295;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0c12:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r1 = r0;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r110 = r2;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        goto L_0x0bc3;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0c17:
                                        r107 = r11;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0c19:
                                        r5 = 1;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0c1a:
                                        r10 = 0;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r13 = (r19 > r10 ? 1 : (r19 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        if (r13 <= 0) goto L_0x0c31;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0c20:
                                        r10 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r13 = (r10 > r19 ? 1 : (r10 == r19 ? 0 : -1));	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        if (r13 < 0) goto L_0x0c31;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0c26:
                                        r60 = 1;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r80 = 1;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r5 = 0;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r10 = r6.flags;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r10 = r10 | 4;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                        r6.flags = r10;	 Catch:{ Exception -> 0x0c12, all -> 0x0c01 }
                                    L_0x0c31:
                                        r10 = 0;
                                        r13 = (r77 > r10 ? 1 : (r77 == r10 ? 0 : -1));
                                        if (r13 <= 0) goto L_0x0c84;
                                    L_0x0c37:
                                        r22 = -1;
                                        r13 = (r57 > r22 ? 1 : (r57 == r22 ? 0 : -1));
                                        if (r13 != 0) goto L_0x0c84;
                                    L_0x0c3d:
                                        r10 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                        r13 = (r10 > r77 ? 1 : (r10 == r77 ? 0 : -1));	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                        if (r13 >= 0) goto L_0x0c87;	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                    L_0x0c43:
                                        r5 = 0;	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                        r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                        if (r10 == 0) goto L_0x0c84;	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                    L_0x0c48:
                                        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                        r10.<init>();	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                        r11 = "drop frame startTime = ";	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                        r10.append(r11);	 Catch:{ Exception -> 0x0cab, all -> 0x0c9a }
                                        r11 = r77;
                                        r10.append(r11);	 Catch:{ Exception -> 0x0c7c, all -> 0x0c6b }
                                        r13 = " present time = ";	 Catch:{ Exception -> 0x0c7c, all -> 0x0c6b }
                                        r10.append(r13);	 Catch:{ Exception -> 0x0c7c, all -> 0x0c6b }
                                        r108 = r11;
                                        r11 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r10.append(r11);	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r10 = r10.toString();	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        org.telegram.messenger.FileLog.m0d(r10);	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        goto L_0x0cb3;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                    L_0x0c6b:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r1 = r0;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r110 = r2;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r9 = r11;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r3 = r49;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r13 = r98;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r4 = r104;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r11 = r107;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r5 = r115;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        goto L_0x1295;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                    L_0x0c7c:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r108 = r11;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r1 = r0;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r110 = r2;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        goto L_0x1013;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                    L_0x0c84:
                                        r108 = r77;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        goto L_0x0cb3;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                    L_0x0c87:
                                        r108 = r77;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r10 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r57 = r10;
                                        goto L_0x0cb3;
                                    L_0x0c8e:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r110 = r2;
                                        goto L_0x0d65;
                                    L_0x0c94:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r110 = r2;
                                        goto L_0x1013;
                                    L_0x0c9a:
                                        r0 = move-exception;
                                        r1 = r0;
                                        r110 = r2;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r13 = r98;
                                        r4 = r104;
                                        r11 = r107;
                                        goto L_0x1295;
                                    L_0x0cab:
                                        r0 = move-exception;
                                        r108 = r77;
                                        r1 = r0;
                                        r110 = r2;
                                        goto L_0x1013;
                                    L_0x0cb3:
                                        r8.releaseOutputBuffer(r9, r5);	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        if (r5 == 0) goto L_0x0d29;
                                    L_0x0cb8:
                                        r10 = 0;
                                        r11 = r10;
                                        r3.awaitNewImage();	 Catch:{ Exception -> 0x0cbe, all -> 0x0c8e }
                                        goto L_0x0cc4;
                                    L_0x0cbe:
                                        r0 = move-exception;
                                        r10 = r0;
                                        r11 = 1;
                                        org.telegram.messenger.FileLog.m3e(r10);	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                    L_0x0cc4:
                                        if (r11 != 0) goto L_0x0d29;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                    L_0x0cc6:
                                        r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r12 = 18;
                                        if (r10 < r12) goto L_0x0ce1;
                                    L_0x0ccc:
                                        r10 = 0;
                                        r3.drawImage(r10);	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r12 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r22 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r12 = r12 * r22;	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r7.setPresentationTime(r12);	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r7.swapBuffers();	 Catch:{ Exception -> 0x0c94, all -> 0x0c8e }
                                        r111 = r1;
                                        r110 = r2;
                                        goto L_0x0d2d;
                                    L_0x0ce1:
                                        r12 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
                                        r10 = r4.dequeueInputBuffer(r12);	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        if (r10 < 0) goto L_0x0d1b;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                    L_0x0ce9:
                                        r12 = 1;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r3.drawImage(r12);	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r22 = r3.getFrame();	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r12 = r99[r10];	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r12.clear();	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r23 = r12;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r24 = r83;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r25 = r2;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r26 = r14;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r27 = r59;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r28 = r73;	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        org.telegram.messenger.Utilities.convertVideoFrame(r22, r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x0d88, all -> 0x0d76 }
                                        r63 = 0;
                                        r111 = r1;
                                        r110 = r2;
                                        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r67 = 0;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r61 = r4;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r62 = r10;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r64 = r75;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r65 = r1;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r61.queueInputBuffer(r62, r63, r64, r65, r67);	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        goto L_0x0d2d;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d1b:
                                        r111 = r1;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r110 = r2;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        if (r1 == 0) goto L_0x0d2d;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d23:
                                        r1 = "input buffer not available";	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        org.telegram.messenger.FileLog.m0d(r1);	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        goto L_0x0d2d;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d29:
                                        r111 = r1;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r110 = r2;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d2d:
                                        r1 = r6.flags;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r1 = r1 & 4;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        if (r1 == 0) goto L_0x0db1;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d33:
                                        r1 = 0;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        if (r2 == 0) goto L_0x0d3d;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d38:
                                        r2 = "decoder stream end";	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        org.telegram.messenger.FileLog.m0d(r2);	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d3d:
                                        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r10 = 18;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        if (r2 < r10) goto L_0x0d47;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d43:
                                        r4.signalEndOfInputStream();	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        goto L_0x0d60;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d47:
                                        r11 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r2 = r4.dequeueInputBuffer(r11);	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        if (r2 < 0) goto L_0x0d60;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d4f:
                                        r63 = 0;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r64 = 1;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r10 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r67 = 4;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r61 = r4;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r62 = r2;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r65 = r10;	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                        r61.queueInputBuffer(r62, r63, r64, r65, r67);	 Catch:{ Exception -> 0x0d73, all -> 0x0d63 }
                                    L_0x0d60:
                                        r10 = r1;
                                        goto L_0x0db3;
                                    L_0x0d63:
                                        r0 = move-exception;
                                        r1 = r0;
                                    L_0x0d65:
                                        r3 = r49;
                                        r13 = r98;
                                        r4 = r104;
                                        r11 = r107;
                                        r9 = r108;
                                        r5 = r115;
                                        goto L_0x1295;
                                    L_0x0d73:
                                        r0 = move-exception;
                                        goto L_0x03d0;
                                    L_0x0d76:
                                        r0 = move-exception;
                                        r110 = r2;
                                        r1 = r0;
                                        r3 = r49;
                                        r13 = r98;
                                        r4 = r104;
                                        r11 = r107;
                                        r9 = r108;
                                        r5 = r115;
                                        goto L_0x1295;
                                    L_0x0d88:
                                        r0 = move-exception;
                                        r110 = r2;
                                        r1 = r0;
                                        goto L_0x1013;
                                    L_0x0d8e:
                                        r0 = move-exception;
                                        r110 = r2;
                                        r107 = r11;
                                        r1 = r0;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r13 = r98;
                                        r4 = r104;
                                        goto L_0x1295;
                                    L_0x0d9f:
                                        r0 = move-exception;
                                        r110 = r2;
                                        r107 = r11;
                                        r108 = r77;
                                        r1 = r0;
                                        goto L_0x1013;
                                    L_0x0da9:
                                        r111 = r1;
                                        r110 = r2;
                                        r107 = r11;
                                        r108 = r77;
                                    L_0x0db1:
                                        r10 = r95;
                                    L_0x0db3:
                                        r86 = r14;
                                        r9 = r96;
                                        r1 = r97;
                                        r13 = r98;
                                        r5 = r99;
                                        r2 = r104;
                                        r11 = r107;
                                        r77 = r108;
                                        r88 = r110;
                                        r14 = r111;
                                        r12 = r115;
                                        goto L_0x08ec;
                                    L_0x0dcb:
                                        r0 = move-exception;
                                        r107 = r11;
                                        r14 = r86;
                                        r110 = r88;
                                        r1 = r0;
                                        r4 = r2;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        r13 = r98;
                                        goto L_0x1295;
                                    L_0x0ddd:
                                        r0 = move-exception;
                                        r104 = r2;
                                        r107 = r11;
                                        r108 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        r1 = r0;
                                        goto L_0x1013;
                                    L_0x0deb:
                                        r0 = move-exception;
                                        r107 = r11;
                                        r98 = r13;
                                        r14 = r86;
                                        r110 = r88;
                                        r1 = r0;
                                        r4 = r2;
                                        r5 = r12;
                                        r3 = r49;
                                        r9 = r77;
                                        goto L_0x1295;
                                    L_0x0dfd:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r104 = r2;
                                        r107 = r11;
                                        r98 = r13;
                                        r108 = r77;
                                        r14 = r86;
                                        r110 = r88;
                                        r1 = r0;
                                        goto L_0x1013;
                                    L_0x0e0f:
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r6 = r44;
                                        r104 = r45;
                                        r107 = r74;
                                        r108 = r77;
                                        r15 = r116;
                                        r14 = r9;
                                        goto L_0x1019;
                                    L_0x0e24:
                                        r0 = move-exception;
                                        r98 = r13;
                                        r110 = r14;
                                        r107 = r74;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r5 = r12;
                                        r4 = r45;
                                        r3 = r49;
                                        r9 = r77;
                                        r11 = r107;
                                        goto L_0x1295;
                                    L_0x0e3a:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r6 = r44;
                                        r104 = r45;
                                        r107 = r74;
                                        r108 = r77;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        goto L_0x1013;
                                    L_0x0e51:
                                        r0 = move-exception;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r74;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r5 = r12;
                                        r4 = r45;
                                        r3 = r49;
                                        r9 = r77;
                                        r11 = r107;
                                        goto L_0x1295;
                                    L_0x0e69:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r107 = r74;
                                        r108 = r77;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        goto L_0x1013;
                                    L_0x0e82:
                                        r0 = move-exception;
                                        r81 = r11;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r74;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r5 = r12;
                                        r4 = r45;
                                        r3 = r49;
                                        r9 = r77;
                                        r11 = r107;
                                        goto L_0x1295;
                                    L_0x0e9c:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r81 = r11;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r107 = r74;
                                        r108 = r77;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r3 = r56;
                                        goto L_0x1013;
                                    L_0x0eb9:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r107 = r74;
                                        r108 = r77;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        goto L_0x0f46;
                                    L_0x0ed4:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r107 = r74;
                                        r108 = r77;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r8 = r50;
                                        goto L_0x0f46;
                                    L_0x0ef1:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r107 = r74;
                                        r108 = r77;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r8 = r50;
                                        goto L_0x0f44;
                                    L_0x0f0d:
                                        r0 = move-exception;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r81 = r46;
                                        r107 = r74;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r5 = r12;
                                        r4 = r45;
                                        r3 = r49;
                                        r9 = r77;
                                        r11 = r107;
                                        goto L_0x1295;
                                    L_0x0f27:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r107 = r74;
                                        r108 = r77;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r8 = r50;
                                        r4 = r51;
                                    L_0x0f44:
                                        r7 = r52;
                                    L_0x0f46:
                                        r3 = r56;
                                        goto L_0x1013;
                                    L_0x0f4a:
                                        r0 = move-exception;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r81 = r46;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r5 = r12;
                                        r4 = r45;
                                        r9 = r47;
                                        r3 = r49;
                                        r11 = r107;
                                        goto L_0x1295;
                                    L_0x0f64:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r108 = r47;
                                        r15 = r116;
                                        r14 = r9;
                                        r1 = r0;
                                        r8 = r50;
                                        r4 = r51;
                                        r7 = r52;
                                        r3 = r56;
                                        goto L_0x1013;
                                    L_0x0f87:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r14 = r42;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r108 = r47;
                                        r15 = r116;
                                        r1 = r0;
                                        r8 = r50;
                                        r4 = r51;
                                        r7 = r52;
                                        r3 = r56;
                                        goto L_0x1013;
                                    L_0x0fab:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r52 = r5;
                                        r56 = r7;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r14 = r42;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r108 = r47;
                                        r15 = r116;
                                        r1 = r0;
                                        r8 = r50;
                                        r4 = r51;
                                        r7 = r52;
                                        r3 = r56;
                                        goto L_0x1013;
                                    L_0x0fd2:
                                        r0 = move-exception;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r14 = r42;
                                        r81 = r46;
                                        r15 = r116;
                                        r1 = r0;
                                        r5 = r12;
                                        r4 = r45;
                                        r9 = r47;
                                        r3 = r49;
                                        r11 = r107;
                                        goto L_0x1295;
                                    L_0x0fed:
                                        r0 = move-exception;
                                        r97 = r1;
                                        r91 = r2;
                                        r51 = r4;
                                        r52 = r5;
                                        r56 = r7;
                                        r98 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r14 = r42;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r108 = r47;
                                        r15 = r116;
                                        r1 = r0;
                                        r8 = r50;
                                        r7 = r52;
                                        r3 = r56;
                                    L_0x1013:
                                        org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ Exception -> 0x1097, all -> 0x1086 }
                                        r1 = 1;
                                        r16 = r1;
                                    L_0x1019:
                                        r2 = r97;
                                        r1 = r98;
                                        r1.unselectTrack(r2);	 Catch:{ Exception -> 0x107b, all -> 0x106c }
                                        if (r3 == 0) goto L_0x1040;
                                    L_0x1022:
                                        r3.release();	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        goto L_0x1040;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x1026:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r13 = r1;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r3 = r49;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r4 = r104;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r11 = r107;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r9 = r108;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r5 = r115;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x1032:
                                        r1 = r0;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        goto L_0x1295;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x1035:
                                        r0 = move-exception;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r7 = r1;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r2 = r107;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r9 = r108;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r5 = r115;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x103d:
                                        r1 = r0;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        goto L_0x1242;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x1040:
                                        if (r7 == 0) goto L_0x1045;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x1042:
                                        r7.release();	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x1045:
                                        if (r8 == 0) goto L_0x104d;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x1047:
                                        r8.stop();	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r8.release();	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x104d:
                                        if (r4 == 0) goto L_0x1055;	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x104f:
                                        r4.stop();	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                        r4.release();	 Catch:{ Exception -> 0x1035, all -> 0x1026 }
                                    L_0x1055:
                                        r9 = r108;
                                        r5 = r115;
                                        r115.checkConversionCanceled();	 Catch:{ Exception -> 0x1067, all -> 0x105e }
                                        goto L_0x10b8;
                                    L_0x105e:
                                        r0 = move-exception;
                                        r13 = r1;
                                        r3 = r49;
                                        r4 = r104;
                                        r11 = r107;
                                        goto L_0x1032;
                                    L_0x1067:
                                        r0 = move-exception;
                                        r7 = r1;
                                        r2 = r107;
                                        goto L_0x103d;
                                    L_0x106c:
                                        r0 = move-exception;
                                        r9 = r108;
                                        r5 = r115;
                                        r13 = r1;
                                        r3 = r49;
                                        r4 = r104;
                                        r11 = r107;
                                        r1 = r0;
                                        goto L_0x1295;
                                    L_0x107b:
                                        r0 = move-exception;
                                        r9 = r108;
                                        r5 = r115;
                                        r7 = r1;
                                        r2 = r107;
                                        r1 = r0;
                                        goto L_0x1242;
                                    L_0x1086:
                                        r0 = move-exception;
                                        r1 = r98;
                                        r9 = r108;
                                        r5 = r115;
                                        r13 = r1;
                                        r3 = r49;
                                        r4 = r104;
                                        r11 = r107;
                                        r1 = r0;
                                        goto L_0x1295;
                                    L_0x1097:
                                        r0 = move-exception;
                                        r1 = r98;
                                        r9 = r108;
                                        r5 = r115;
                                        r7 = r1;
                                        r2 = r107;
                                        r1 = r0;
                                        goto L_0x1242;
                                    L_0x10a4:
                                        r5 = r12;
                                        r1 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r107 = r41;
                                        r14 = r42;
                                        r6 = r44;
                                        r104 = r45;
                                        r81 = r46;
                                        r9 = r47;
                                        r15 = r116;
                                    L_0x10b8:
                                        if (r1 == 0) goto L_0x10bd;
                                    L_0x10ba:
                                        r1.release();
                                    L_0x10bd:
                                        if (r107 == 0) goto L_0x10cb;
                                    L_0x10bf:
                                        r2 = r107;
                                        r2.finishMovie();	 Catch:{ Exception -> 0x10c5 }
                                        goto L_0x10cd;
                                    L_0x10c5:
                                        r0 = move-exception;
                                        r3 = r0;
                                        org.telegram.messenger.FileLog.m3e(r3);
                                        goto L_0x10cd;
                                    L_0x10cb:
                                        r2 = r107;
                                    L_0x10cd:
                                        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
                                        if (r3 == 0) goto L_0x1275;
                                    L_0x10d1:
                                        r3 = new java.lang.StringBuilder;
                                        r3.<init>();
                                        r4 = "time = ";
                                        r3.append(r4);
                                        r6 = java.lang.System.currentTimeMillis();
                                        r11 = r6 - r34;
                                        r3.append(r11);
                                        r3 = r3.toString();
                                        org.telegram.messenger.FileLog.m0d(r3);
                                        goto L_0x1275;
                                    L_0x10ed:
                                        r0 = move-exception;
                                        r5 = r12;
                                        r1 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r2 = r41;
                                        r14 = r42;
                                        r81 = r46;
                                        r9 = r47;
                                        r15 = r116;
                                        r11 = r2;
                                        r4 = r45;
                                        r3 = r49;
                                        r1 = r0;
                                        goto L_0x1295;
                                    L_0x1106:
                                        r0 = move-exception;
                                        r5 = r12;
                                        r1 = r13;
                                        r110 = r14;
                                        r82 = r15;
                                        r2 = r41;
                                        r14 = r42;
                                        r104 = r45;
                                        r81 = r46;
                                        r9 = r47;
                                        r15 = r116;
                                        r7 = r1;
                                        r1 = r0;
                                        goto L_0x1242;
                                    L_0x111d:
                                        r0 = move-exception;
                                        r1 = r7;
                                        r110 = r9;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r9 = r29;
                                        r82 = r31;
                                        r81 = r32;
                                        r17 = r33;
                                        r2 = r37;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r5;
                                        r29 = r11;
                                        r5 = r12;
                                        r15 = r13;
                                        r13 = r1;
                                        r11 = r2;
                                        r4 = r8;
                                        r3 = r49;
                                        r1 = r0;
                                        goto L_0x1295;
                                    L_0x1142:
                                        r0 = move-exception;
                                        r1 = r7;
                                        r104 = r8;
                                        r110 = r9;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r9 = r29;
                                        r82 = r31;
                                        r81 = r32;
                                        r17 = r33;
                                        r2 = r37;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r5;
                                        r29 = r11;
                                        r5 = r12;
                                        r15 = r13;
                                        r1 = r0;
                                        goto L_0x1242;
                                    L_0x1164:
                                        r0 = move-exception;
                                        r98 = r7;
                                        r110 = r9;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r9 = r29;
                                        r82 = r31;
                                        r81 = r32;
                                        r17 = r33;
                                        r2 = r37;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r5;
                                        r29 = r11;
                                        r5 = r12;
                                        r15 = r13;
                                        r1 = r0;
                                        r11 = r2;
                                        r4 = r8;
                                        r3 = r49;
                                        r13 = r98;
                                        goto L_0x1295;
                                    L_0x118b:
                                        r0 = move-exception;
                                        r98 = r7;
                                        r104 = r8;
                                        r110 = r9;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r9 = r29;
                                        r82 = r31;
                                        r81 = r32;
                                        r17 = r33;
                                        r2 = r37;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r5;
                                        r29 = r11;
                                        r5 = r12;
                                        r15 = r13;
                                        r1 = r0;
                                        goto L_0x1242;
                                    L_0x11ae:
                                        r0 = move-exception;
                                        r98 = r7;
                                        r110 = r9;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r9 = r29;
                                        r82 = r31;
                                        r81 = r32;
                                        r17 = r33;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r5;
                                        r29 = r11;
                                        r5 = r12;
                                        r15 = r13;
                                        r1 = r0;
                                        r4 = r8;
                                        r11 = r36;
                                        r3 = r49;
                                        r13 = r98;
                                        goto L_0x1295;
                                    L_0x11d4:
                                        r0 = move-exception;
                                        r98 = r7;
                                        r104 = r8;
                                        r110 = r9;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r9 = r29;
                                        r82 = r31;
                                        r81 = r32;
                                        r17 = r33;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r5;
                                        r29 = r11;
                                        r5 = r12;
                                        r15 = r13;
                                        r1 = r0;
                                        r2 = r36;
                                        goto L_0x1242;
                                    L_0x11f7:
                                        r0 = move-exception;
                                        r36 = r1;
                                        r98 = r7;
                                        r110 = r9;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r9 = r29;
                                        r82 = r31;
                                        r81 = r32;
                                        r17 = r33;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r5;
                                        r29 = r11;
                                        r5 = r12;
                                        r15 = r13;
                                        r1 = r0;
                                        r4 = r8;
                                        r11 = r36;
                                        r3 = r49;
                                        r13 = r98;
                                        goto L_0x1295;
                                    L_0x121f:
                                        r0 = move-exception;
                                        r36 = r1;
                                        r98 = r7;
                                        r104 = r8;
                                        r110 = r9;
                                        r49 = r10;
                                        r40 = r14;
                                        r43 = r15;
                                        r9 = r29;
                                        r82 = r31;
                                        r81 = r32;
                                        r17 = r33;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r5;
                                        r29 = r11;
                                        r5 = r12;
                                        r15 = r13;
                                        r1 = r0;
                                        r2 = r36;
                                    L_0x1242:
                                        r16 = 1;
                                        org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ all -> 0x128d }
                                        if (r7 == 0) goto L_0x124c;
                                    L_0x1249:
                                        r7.release();
                                    L_0x124c:
                                        if (r2 == 0) goto L_0x1257;
                                    L_0x124e:
                                        r2.finishMovie();	 Catch:{ Exception -> 0x1252 }
                                        goto L_0x1257;
                                    L_0x1252:
                                        r0 = move-exception;
                                        r1 = r0;
                                        org.telegram.messenger.FileLog.m3e(r1);
                                    L_0x1257:
                                        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
                                        if (r1 == 0) goto L_0x1275;
                                    L_0x125b:
                                        r1 = new java.lang.StringBuilder;
                                        r1.<init>();
                                        r3 = "time = ";
                                        r1.append(r3);
                                        r3 = java.lang.System.currentTimeMillis();
                                        r11 = r3 - r34;
                                        r1.append(r11);
                                        r1 = r1.toString();
                                        org.telegram.messenger.FileLog.m0d(r1);
                                    L_0x1275:
                                        r1 = r16;
                                        r3 = r49;
                                        r2 = r3.edit();
                                        r4 = "isPreviousOk";
                                        r6 = 1;
                                        r2 = r2.putBoolean(r4, r6);
                                        r2.commit();
                                        r4 = r104;
                                        r5.didWriteData(r15, r4, r6, r1);
                                        return r6;
                                    L_0x128d:
                                        r0 = move-exception;
                                        r3 = r49;
                                        r4 = r104;
                                        r1 = r0;
                                        r11 = r2;
                                        r13 = r7;
                                    L_0x1295:
                                        if (r13 == 0) goto L_0x129a;
                                    L_0x1297:
                                        r13.release();
                                    L_0x129a:
                                        if (r11 == 0) goto L_0x12a5;
                                    L_0x129c:
                                        r11.finishMovie();	 Catch:{ Exception -> 0x12a0 }
                                        goto L_0x12a5;
                                    L_0x12a0:
                                        r0 = move-exception;
                                        r2 = r0;
                                        org.telegram.messenger.FileLog.m3e(r2);
                                    L_0x12a5:
                                        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
                                        if (r2 == 0) goto L_0x12c6;
                                    L_0x12a9:
                                        r2 = new java.lang.StringBuilder;
                                        r2.<init>();
                                        r6 = "time = ";
                                        r2.append(r6);
                                        r6 = java.lang.System.currentTimeMillis();
                                        r112 = r9;
                                        r8 = r6 - r34;
                                        r2.append(r8);
                                        r2 = r2.toString();
                                        org.telegram.messenger.FileLog.m0d(r2);
                                        goto L_0x12c8;
                                    L_0x12c6:
                                        r112 = r9;
                                    L_0x12c8:
                                        throw r1;
                                    L_0x12c9:
                                        r110 = r9;
                                        r40 = r14;
                                        r43 = r15;
                                        r112 = r29;
                                        r82 = r31;
                                        r81 = r32;
                                        r17 = r33;
                                        r32 = r3;
                                        r33 = r4;
                                        r14 = r5;
                                        r4 = r8;
                                        r3 = r10;
                                        r29 = r11;
                                        r5 = r12;
                                        r15 = r13;
                                        r1 = r3.edit();
                                        r2 = "isPreviousOk";
                                        r6 = 1;
                                        r1 = r1.putBoolean(r2, r6);
                                        r1.commit();
                                        r5.didWriteData(r15, r4, r6, r6);
                                        r1 = 0;
                                        return r1;
                                        */
                                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MessageObject):boolean");
                                    }
                                }
