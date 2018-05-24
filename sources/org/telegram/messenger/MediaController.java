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
import android.media.AudioTrack;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import org.telegram.messenger.exoplayer2.C0600C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSink;
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
    private String[] mediaProjections;
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
    private Runnable recordRunnable = new C02921();
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

    /* renamed from: org.telegram.messenger.MediaController$18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ File val$cacheFile;
        final /* synthetic */ CountDownLatch val$countDownLatch;
        final /* synthetic */ Boolean[] val$result;

        AnonymousClass18(Boolean[] boolArr, File file, CountDownLatch countDownLatch) {
            this.val$result = boolArr;
            this.val$cacheFile = file;
            this.val$countDownLatch = countDownLatch;
        }

        public void run() {
            boolean z;
            Boolean[] boolArr = this.val$result;
            if (MediaController.this.openOpusFile(this.val$cacheFile.getAbsolutePath()) != 0) {
                z = true;
            } else {
                z = false;
            }
            boolArr[0] = Boolean.valueOf(z);
            this.val$countDownLatch.countDown();
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$1 */
    class C02921 implements Runnable {
        C02921() {
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
                        FileLog.m3e(e);
                    }
                    buffer.position(0);
                    final double amplitude = Math.sqrt((d / ((double) len)) / 2.0d);
                    final ByteBuffer finalBuffer = buffer;
                    final boolean flush = len != buffer.capacity();
                    if (len != 0) {
                        MediaController.this.fileEncodingQueue.postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.MediaController$1$1$1 */
                            class C02861 implements Runnable {
                                C02861() {
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
                                MediaController.this.recordQueue.postRunnable(new C02861());
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

    /* renamed from: org.telegram.messenger.MediaController$21 */
    class AnonymousClass21 implements Runnable {
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass21(MessageObject messageObject) {
            this.val$messageObject = messageObject;
        }

        public void run() {
            NotificationCenter.getInstance(this.val$messageObject.currentAccount).postNotificationName(NotificationCenter.FileDidLoaded, FileLoader.getAttachFileName(this.val$messageObject.getDocument()));
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$3 */
    class C03043 implements Runnable {
        C03043() {
        }

        public void run() {
            try {
                int a;
                MediaController.this.recordBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
                if (MediaController.this.recordBufferSize <= 0) {
                    MediaController.this.recordBufferSize = 1280;
                }
                MediaController.this.playerBufferSize = AudioTrack.getMinBufferSize(48000, 4, 2);
                if (MediaController.this.playerBufferSize <= 0) {
                    MediaController.this.playerBufferSize = 3840;
                }
                for (a = 0; a < 5; a++) {
                    ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                    buffer.order(ByteOrder.nativeOrder());
                    MediaController.this.recordBuffers.add(buffer);
                }
                for (a = 0; a < 3; a++) {
                    MediaController.this.freePlayerBuffers.add(new AudioBuffer(MediaController.this.playerBufferSize));
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$4 */
    class C03074 implements Runnable {

        /* renamed from: org.telegram.messenger.MediaController$4$1 */
        class C03061 extends PhoneStateListener {
            C03061() {
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

        C03074() {
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
                PhoneStateListener phoneStateListener = new C03061();
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
    class C03085 implements Runnable {
        C03085() {
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
    class C03117 implements Runnable {
        C03117() {
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
    class C03139 implements Runnable {
        C03139() {
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
        class C03141 implements Runnable {
            C03141() {
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
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new C03141(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    private class GalleryObserverInternal extends ContentObserver {

        /* renamed from: org.telegram.messenger.MediaController$GalleryObserverInternal$1 */
        class C03151 implements Runnable {
            C03151() {
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
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new C03151(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
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

    /* renamed from: org.telegram.messenger.MediaController$20 */
    class AnonymousClass20 implements VideoPlayerDelegate {
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass20(MessageObject messageObject) {
            this.val$messageObject = messageObject;
        }

        public void onStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == 4) {
                if (MediaController.this.playlist.isEmpty() || MediaController.this.playlist.size() <= 1) {
                    MediaController mediaController = MediaController.this;
                    boolean z = this.val$messageObject != null && this.val$messageObject.isVoice();
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
        this.recordQueue.setPriority(10);
        this.fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue.setPriority(10);
        this.playerQueue = new DispatchQueue("playerQueue");
        this.fileDecodingQueue = new DispatchQueue("fileDecodingQueue");
        this.recordQueue.postRunnable(new C03043());
        Utilities.globalQueue.postRunnable(new C03074());
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new C03085());
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
                class C03091 implements Runnable {
                    C03091() {
                    }

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
                                } else if (MediaController.this.audioPlayer != null) {
                                    duration = MediaController.this.audioPlayer.getDuration();
                                    progress = MediaController.this.audioPlayer.getCurrentPosition();
                                    if (duration == C0600C.TIME_UNSET || duration < 0) {
                                        value = 0.0f;
                                    } else {
                                        value = ((float) progress) / ((float) duration);
                                    }
                                    bufferedValue = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) duration);
                                    if (duration != C0600C.TIME_UNSET && progress >= 0) {
                                        if (MediaController.this.seekToProgressPending != 0.0f) {
                                            return;
                                        }
                                    }
                                    return;
                                } else {
                                    duration = 0;
                                    progress = (long) ((int) (((float) MediaController.this.lastPlayPcm) / 48.0f));
                                    value = ((float) MediaController.this.lastPlayPcm) / ((float) MediaController.this.currentTotalPcmDuration);
                                    bufferedValue = 0.0f;
                                    if (progress == MediaController.this.lastProgress) {
                                        return;
                                    }
                                }
                                MediaController.this.lastProgress = progress;
                                currentPlayingMessageObject.audioPlayerDuration = (int) (duration / 1000);
                                currentPlayingMessageObject.audioProgress = value;
                                currentPlayingMessageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                                currentPlayingMessageObject.bufferedProgress = bufferedValue;
                                NotificationCenter.getInstance(currentPlayingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(currentPlayingMessageObject.getId()), Float.valueOf(value));
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    }
                }

                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new C03091());
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
            AndroidUtilities.runOnUIThread(new C03117(), 300000);
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
        try {
            android.graphics.Point size = AndroidUtilities.getRealScreenSize();
            Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
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
        } catch (Throwable e2) {
            FileLog.m3e(e2);
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

    private void checkDecoderQueue() {
        this.fileDecodingQueue.postRunnable(new C03139());
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

    public boolean isRecordingOrListeningByProximity() {
        return this.proximityTouched && (isRecordingAudio() || (this.playingMessageObject != null && (this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo())));
    }

    public void onSensorChanged(SensorEvent event) {
        if (this.sensorsStarted && VoIPService.getSharedInstance() == null) {
            if (event.sensor == this.proximitySensor) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("proximity changed to " + event.values[0]);
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
                                FileLog.m0d("motion detected");
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
                    FileLog.m0d("sensor values reached");
                }
                if (this.playingMessageObject == null && this.recordStartRunnable == null && this.recordingAudio == null && !PhotoViewer.getInstance().isVisible() && ApplicationLoader.isScreenOn && !this.inputFieldHasText && this.allowStartRecord && this.raiseChat != null && !this.callInProgress) {
                    if (!this.raiseToEarRecord) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("start record");
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
                        FileLog.m0d("start listen");
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
                if (this.playingMessageObject != null && ((this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("start listen by proximity only");
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
                        FileLog.m0d("stop record");
                    }
                    stopRecording(2);
                    this.raiseToEarRecord = false;
                    this.ignoreOnPause = false;
                    if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                        this.proximityWakeLock.release();
                    }
                } else if (this.useFrontSpeaker) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("stop listen");
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
            return;
        }
        stopRecording(0);
        if (this.sensorsStarted && !this.ignoreOnPause) {
            if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null && this.raiseChat == chatActivity) {
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
            } catch (Throwable e222) {
                FileLog.m3e(e222);
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
            }
        }
    }

    private void seekOpusPlayer(final float progress) {
        if (progress != VOLUME_NORMAL) {
            if (!this.isPaused) {
                this.audioTrackPlayer.pause();
            }
            this.audioTrackPlayer.flush();
            this.fileDecodingQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MediaController$14$1 */
                class C02901 implements Runnable {
                    C02901() {
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
                    AndroidUtilities.runOnUIThread(new C02901());
                }
            });
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
        if ((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        try {
            if (this.audioPlayer != null) {
                long duration = this.audioPlayer.getDuration();
                if (duration == C0600C.TIME_UNSET) {
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
            if (this.audioPlayer != null || this.audioTrackPlayer != null || this.videoPlayer != null) {
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
                    } catch (Throwable e222) {
                        FileLog.m3e(e222);
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

    public boolean playMessage(org.telegram.messenger.MessageObject r27) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r18_1 'file' java.io.File) in PHI: PHI: (r18_3 'file' java.io.File) = (r18_0 'file' java.io.File), (r18_0 'file' java.io.File), (r18_1 'file' java.io.File), (r18_2 'file' java.io.File) binds: {(r18_0 'file' java.io.File)=B:31:0x0086, (r18_0 'file' java.io.File)=B:33:0x0092, (r18_1 'file' java.io.File)=B:35:0x00a5, (r18_2 'file' java.io.File)=B:36:0x00a7}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
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
        r26 = this;
        if (r27 != 0) goto L_0x0004;
    L_0x0002:
        r2 = 0;
    L_0x0003:
        return r2;
    L_0x0004:
        r0 = r26;
        r2 = r0.audioTrackPlayer;
        if (r2 != 0) goto L_0x0016;
    L_0x000a:
        r0 = r26;
        r2 = r0.audioPlayer;
        if (r2 != 0) goto L_0x0016;
    L_0x0010:
        r0 = r26;
        r2 = r0.videoPlayer;
        if (r2 == 0) goto L_0x0034;
    L_0x0016:
        r2 = r26.isSamePlayingMessage(r27);
        if (r2 == 0) goto L_0x0034;
    L_0x001c:
        r0 = r26;
        r2 = r0.isPaused;
        if (r2 == 0) goto L_0x0025;
    L_0x0022:
        r26.resumeAudio(r27);
    L_0x0025:
        r2 = org.telegram.messenger.SharedConfig.raiseToSpeak;
        if (r2 != 0) goto L_0x0032;
    L_0x0029:
        r0 = r26;
        r2 = r0.raiseChat;
        r0 = r26;
        r0.startRaiseToEarSensors(r2);
    L_0x0032:
        r2 = 1;
        goto L_0x0003;
    L_0x0034:
        r2 = r27.isOut();
        if (r2 != 0) goto L_0x004d;
    L_0x003a:
        r2 = r27.isContentUnread();
        if (r2 == 0) goto L_0x004d;
    L_0x0040:
        r0 = r27;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r0 = r27;
        r2.markMessageContentAsRead(r0);
    L_0x004d:
        r0 = r26;
        r2 = r0.playMusicAgain;
        if (r2 != 0) goto L_0x013f;
    L_0x0053:
        r20 = 1;
    L_0x0055:
        r0 = r26;
        r2 = r0.playingMessageObject;
        if (r2 == 0) goto L_0x006a;
    L_0x005b:
        r20 = 0;
        r0 = r26;
        r2 = r0.playMusicAgain;
        if (r2 != 0) goto L_0x006a;
    L_0x0063:
        r0 = r26;
        r2 = r0.playingMessageObject;
        r2.resetPlayingProgress();
    L_0x006a:
        r2 = 0;
        r0 = r26;
        r1 = r20;
        r0.cleanupPlayer(r1, r2);
        r2 = 0;
        r0 = r26;
        r0.playMusicAgain = r2;
        r2 = 0;
        r0 = r26;
        r0.seekToProgressPending = r2;
        r18 = 0;
        r17 = 0;
        r0 = r27;
        r2 = r0.messageOwner;
        r2 = r2.attachPath;
        if (r2 == 0) goto L_0x00a9;
    L_0x0088:
        r0 = r27;
        r2 = r0.messageOwner;
        r2 = r2.attachPath;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x00a9;
    L_0x0094:
        r18 = new java.io.File;
        r0 = r27;
        r2 = r0.messageOwner;
        r2 = r2.attachPath;
        r0 = r18;
        r0.<init>(r2);
        r17 = r18.exists();
        if (r17 != 0) goto L_0x00a9;
    L_0x00a7:
        r18 = 0;
    L_0x00a9:
        if (r18 == 0) goto L_0x0143;
    L_0x00ab:
        r9 = r18;
    L_0x00ad:
        r2 = org.telegram.messenger.SharedConfig.streamMedia;
        if (r2 == 0) goto L_0x014d;
    L_0x00b1:
        r2 = r27.isMusic();
        if (r2 == 0) goto L_0x014d;
    L_0x00b7:
        r2 = r27.getDialogId();
        r2 = (int) r2;
        if (r2 == 0) goto L_0x014d;
    L_0x00be:
        r10 = 1;
    L_0x00bf:
        if (r9 == 0) goto L_0x0168;
    L_0x00c1:
        r0 = r18;
        if (r9 == r0) goto L_0x0168;
    L_0x00c5:
        r17 = r9.exists();
        if (r17 != 0) goto L_0x0168;
    L_0x00cb:
        if (r10 != 0) goto L_0x0168;
    L_0x00cd:
        r0 = r27;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.FileLoader.getInstance(r2);
        r3 = r27.getDocument();
        r4 = 0;
        r5 = 0;
        r2.loadFile(r3, r4, r5);
        r2 = 1;
        r0 = r26;
        r0.downloadingCurrentMessage = r2;
        r2 = 0;
        r0 = r26;
        r0.isPaused = r2;
        r2 = 0;
        r0 = r26;
        r0.lastProgress = r2;
        r2 = 0;
        r0 = r26;
        r0.lastPlayPcm = r2;
        r2 = 0;
        r0 = r26;
        r0.audioInfo = r2;
        r0 = r27;
        r1 = r26;
        r1.playingMessageObject = r0;
        r0 = r26;
        r2 = r0.playingMessageObject;
        r2 = r2.isMusic();
        if (r2 == 0) goto L_0x0155;
    L_0x0109:
        r19 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = org.telegram.messenger.MusicPlayerService.class;
        r0 = r19;
        r0.<init>(r2, r3);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0150 }
        r0 = r19;	 Catch:{ Throwable -> 0x0150 }
        r2.startService(r0);	 Catch:{ Throwable -> 0x0150 }
    L_0x011b:
        r0 = r26;
        r2 = r0.playingMessageObject;
        r2 = r2.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r0 = r26;
        r6 = r0.playingMessageObject;
        r6 = r6.getId();
        r6 = java.lang.Integer.valueOf(r6);
        r4[r5] = r6;
        r2.postNotificationName(r3, r4);
        r2 = 1;
        goto L_0x0003;
    L_0x013f:
        r20 = 0;
        goto L_0x0055;
    L_0x0143:
        r0 = r27;
        r2 = r0.messageOwner;
        r9 = org.telegram.messenger.FileLoader.getPathToMessage(r2);
        goto L_0x00ad;
    L_0x014d:
        r10 = 0;
        goto L_0x00bf;
    L_0x0150:
        r13 = move-exception;
        org.telegram.messenger.FileLog.m3e(r13);
        goto L_0x011b;
    L_0x0155:
        r19 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = org.telegram.messenger.MusicPlayerService.class;
        r0 = r19;
        r0.<init>(r2, r3);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = r19;
        r2.stopService(r0);
        goto L_0x011b;
    L_0x0168:
        r2 = 0;
        r0 = r26;
        r0.downloadingCurrentMessage = r2;
        r2 = r27.isMusic();
        if (r2 == 0) goto L_0x02d5;
    L_0x0173:
        r0 = r27;
        r2 = r0.currentAccount;
        r0 = r26;
        r0.checkIsNextMusicFileDownloaded(r2);
    L_0x017c:
        r0 = r26;
        r2 = r0.currentAspectRatioFrameLayout;
        if (r2 == 0) goto L_0x018f;
    L_0x0182:
        r2 = 0;
        r0 = r26;
        r0.isDrawingWasReady = r2;
        r0 = r26;
        r2 = r0.currentAspectRatioFrameLayout;
        r3 = 0;
        r2.setDrawingReady(r3);
    L_0x018f:
        r2 = r27.isRoundVideo();
        if (r2 == 0) goto L_0x02fe;
    L_0x0195:
        r0 = r26;
        r2 = r0.playlist;
        r2.clear();
        r0 = r26;
        r2 = r0.shuffledPlaylist;
        r2.clear();
        r2 = new org.telegram.ui.Components.VideoPlayer;
        r2.<init>();
        r0 = r26;
        r0.videoPlayer = r2;
        r0 = r26;
        r2 = r0.videoPlayer;
        r3 = new org.telegram.messenger.MediaController$16;
        r0 = r26;
        r3.<init>();
        r2.setDelegate(r3);
        r2 = 0;
        r0 = r26;
        r0.currentAspectRatioFrameLayoutReady = r2;
        r0 = r26;
        r2 = r0.pipRoundVideoView;
        if (r2 != 0) goto L_0x01d7;
    L_0x01c5:
        r0 = r27;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r4 = r27.getDialogId();
        r2 = r2.isDialogCreated(r4);
        if (r2 != 0) goto L_0x02e8;
    L_0x01d7:
        r0 = r26;
        r2 = r0.pipRoundVideoView;
        if (r2 != 0) goto L_0x01f8;
    L_0x01dd:
        r2 = new org.telegram.ui.Components.PipRoundVideoView;	 Catch:{ Exception -> 0x02e0 }
        r2.<init>();	 Catch:{ Exception -> 0x02e0 }
        r0 = r26;	 Catch:{ Exception -> 0x02e0 }
        r0.pipRoundVideoView = r2;	 Catch:{ Exception -> 0x02e0 }
        r0 = r26;	 Catch:{ Exception -> 0x02e0 }
        r2 = r0.pipRoundVideoView;	 Catch:{ Exception -> 0x02e0 }
        r0 = r26;	 Catch:{ Exception -> 0x02e0 }
        r3 = r0.baseActivity;	 Catch:{ Exception -> 0x02e0 }
        r4 = new org.telegram.messenger.MediaController$17;	 Catch:{ Exception -> 0x02e0 }
        r0 = r26;	 Catch:{ Exception -> 0x02e0 }
        r4.<init>();	 Catch:{ Exception -> 0x02e0 }
        r2.show(r3, r4);	 Catch:{ Exception -> 0x02e0 }
    L_0x01f8:
        r0 = r26;
        r2 = r0.pipRoundVideoView;
        if (r2 == 0) goto L_0x020d;
    L_0x01fe:
        r0 = r26;
        r2 = r0.videoPlayer;
        r0 = r26;
        r3 = r0.pipRoundVideoView;
        r3 = r3.getTextureView();
        r2.setTextureView(r3);
    L_0x020d:
        r0 = r26;
        r2 = r0.videoPlayer;
        r3 = android.net.Uri.fromFile(r9);
        r4 = "other";
        r2.preparePlayer(r3, r4);
        r0 = r26;
        r3 = r0.videoPlayer;
        r0 = r26;
        r2 = r0.useFrontSpeaker;
        if (r2 == 0) goto L_0x02fb;
    L_0x0225:
        r2 = 0;
    L_0x0226:
        r3.setStreamType(r2);
        r0 = r26;
        r2 = r0.videoPlayer;
        r2.play();
    L_0x0230:
        r26.checkAudioFocus(r27);
        r26.setPlayerVolume();
        r2 = 0;
        r0 = r26;
        r0.isPaused = r2;
        r2 = 0;
        r0 = r26;
        r0.lastProgress = r2;
        r2 = 0;
        r0 = r26;
        r0.lastPlayPcm = r2;
        r0 = r27;
        r1 = r26;
        r1.playingMessageObject = r0;
        r2 = org.telegram.messenger.SharedConfig.raiseToSpeak;
        if (r2 != 0) goto L_0x025a;
    L_0x0251:
        r0 = r26;
        r2 = r0.raiseChat;
        r0 = r26;
        r0.startRaiseToEarSensors(r2);
    L_0x025a:
        r0 = r26;
        r2 = r0.playingMessageObject;
        r0 = r26;
        r0.startProgressTimer(r2);
        r0 = r27;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.messagePlayingDidStarted;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r4[r5] = r27;
        r2.postNotificationName(r3, r4);
        r0 = r26;
        r2 = r0.videoPlayer;
        if (r2 == 0) goto L_0x05a4;
    L_0x027c:
        r0 = r26;	 Catch:{ Exception -> 0x0569 }
        r2 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0569 }
        r2 = r2.audioProgress;	 Catch:{ Exception -> 0x0569 }
        r3 = 0;	 Catch:{ Exception -> 0x0569 }
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));	 Catch:{ Exception -> 0x0569 }
        if (r2 == 0) goto L_0x02b6;	 Catch:{ Exception -> 0x0569 }
    L_0x0287:
        r0 = r26;	 Catch:{ Exception -> 0x0569 }
        r2 = r0.audioPlayer;	 Catch:{ Exception -> 0x0569 }
        r14 = r2.getDuration();	 Catch:{ Exception -> 0x0569 }
        r2 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;	 Catch:{ Exception -> 0x0569 }
        r2 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1));	 Catch:{ Exception -> 0x0569 }
        if (r2 != 0) goto L_0x02a1;	 Catch:{ Exception -> 0x0569 }
    L_0x0298:
        r0 = r26;	 Catch:{ Exception -> 0x0569 }
        r2 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0569 }
        r2 = r2.getDuration();	 Catch:{ Exception -> 0x0569 }
        r14 = (long) r2;	 Catch:{ Exception -> 0x0569 }
    L_0x02a1:
        r2 = (float) r14;	 Catch:{ Exception -> 0x0569 }
        r0 = r26;	 Catch:{ Exception -> 0x0569 }
        r3 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0569 }
        r3 = r3.audioProgress;	 Catch:{ Exception -> 0x0569 }
        r2 = r2 * r3;	 Catch:{ Exception -> 0x0569 }
        r0 = (int) r2;	 Catch:{ Exception -> 0x0569 }
        r23 = r0;	 Catch:{ Exception -> 0x0569 }
        r0 = r26;	 Catch:{ Exception -> 0x0569 }
        r2 = r0.videoPlayer;	 Catch:{ Exception -> 0x0569 }
        r0 = r23;	 Catch:{ Exception -> 0x0569 }
        r4 = (long) r0;	 Catch:{ Exception -> 0x0569 }
        r2.seekTo(r4);	 Catch:{ Exception -> 0x0569 }
    L_0x02b6:
        r0 = r26;
        r2 = r0.playingMessageObject;
        r2 = r2.isMusic();
        if (r2 == 0) goto L_0x0649;
    L_0x02c0:
        r19 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = org.telegram.messenger.MusicPlayerService.class;
        r0 = r19;
        r0.<init>(r2, r3);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0643 }
        r0 = r19;	 Catch:{ Throwable -> 0x0643 }
        r2.startService(r0);	 Catch:{ Throwable -> 0x0643 }
    L_0x02d2:
        r2 = 1;
        goto L_0x0003;
    L_0x02d5:
        r0 = r27;
        r2 = r0.currentAccount;
        r0 = r26;
        r0.checkIsNextVoiceFileDownloaded(r2);
        goto L_0x017c;
    L_0x02e0:
        r13 = move-exception;
        r2 = 0;
        r0 = r26;
        r0.pipRoundVideoView = r2;
        goto L_0x01f8;
    L_0x02e8:
        r0 = r26;
        r2 = r0.currentTextureView;
        if (r2 == 0) goto L_0x020d;
    L_0x02ee:
        r0 = r26;
        r2 = r0.videoPlayer;
        r0 = r26;
        r3 = r0.currentTextureView;
        r2.setTextureView(r3);
        goto L_0x020d;
    L_0x02fb:
        r2 = 3;
        goto L_0x0226;
    L_0x02fe:
        r2 = r27.isMusic();
        if (r2 != 0) goto L_0x03da;
    L_0x0304:
        r2 = r9.getAbsolutePath();
        r2 = isOpusFile(r2);
        r3 = 1;
        if (r2 != r3) goto L_0x03da;
    L_0x030f:
        r0 = r26;
        r2 = r0.pipRoundVideoView;
        if (r2 == 0) goto L_0x0322;
    L_0x0315:
        r0 = r26;
        r2 = r0.pipRoundVideoView;
        r3 = 1;
        r2.close(r3);
        r2 = 0;
        r0 = r26;
        r0.pipRoundVideoView = r2;
    L_0x0322:
        r0 = r26;
        r2 = r0.playlist;
        r2.clear();
        r0 = r26;
        r2 = r0.shuffledPlaylist;
        r2.clear();
        r0 = r26;
        r0 = r0.playerObjectSync;
        r25 = r0;
        monitor-enter(r25);
        r2 = 3;
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r0.ignoreFirstProgress = r2;	 Catch:{ Exception -> 0x03b1 }
        r11 = new java.util.concurrent.CountDownLatch;	 Catch:{ Exception -> 0x03b1 }
        r2 = 1;	 Catch:{ Exception -> 0x03b1 }
        r11.<init>(r2);	 Catch:{ Exception -> 0x03b1 }
        r2 = 1;	 Catch:{ Exception -> 0x03b1 }
        r0 = new java.lang.Boolean[r2];	 Catch:{ Exception -> 0x03b1 }
        r22 = r0;	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r2 = r0.fileDecodingQueue;	 Catch:{ Exception -> 0x03b1 }
        r3 = new org.telegram.messenger.MediaController$18;	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r1 = r22;	 Catch:{ Exception -> 0x03b1 }
        r3.<init>(r1, r9, r11);	 Catch:{ Exception -> 0x03b1 }
        r2.postRunnable(r3);	 Catch:{ Exception -> 0x03b1 }
        r11.await();	 Catch:{ Exception -> 0x03b1 }
        r2 = 0;	 Catch:{ Exception -> 0x03b1 }
        r2 = r22[r2];	 Catch:{ Exception -> 0x03b1 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x03b1 }
        if (r2 != 0) goto L_0x036a;
    L_0x0363:
        r2 = 0;
        monitor-exit(r25);	 Catch:{ all -> 0x0367 }
        goto L_0x0003;	 Catch:{ all -> 0x0367 }
    L_0x0367:
        r2 = move-exception;	 Catch:{ all -> 0x0367 }
        monitor-exit(r25);	 Catch:{ all -> 0x0367 }
        throw r2;
    L_0x036a:
        r2 = r26.getTotalPcmDuration();	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r0.currentTotalPcmDuration = r2;	 Catch:{ Exception -> 0x03b1 }
        r2 = new android.media.AudioTrack;	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r3 = r0.useFrontSpeaker;	 Catch:{ Exception -> 0x03b1 }
        if (r3 == 0) goto L_0x03af;	 Catch:{ Exception -> 0x03b1 }
    L_0x037a:
        r3 = 0;	 Catch:{ Exception -> 0x03b1 }
    L_0x037b:
        r4 = 48000; // 0xbb80 float:6.7262E-41 double:2.3715E-319;	 Catch:{ Exception -> 0x03b1 }
        r5 = 4;	 Catch:{ Exception -> 0x03b1 }
        r6 = 2;	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r7 = r0.playerBufferSize;	 Catch:{ Exception -> 0x03b1 }
        r8 = 1;	 Catch:{ Exception -> 0x03b1 }
        r2.<init>(r3, r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r0.audioTrackPlayer = r2;	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r2 = r0.audioTrackPlayer;	 Catch:{ Exception -> 0x03b1 }
        r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x03b1 }
        r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x03b1 }
        r2.setStereoVolume(r3, r4);	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r2 = r0.audioTrackPlayer;	 Catch:{ Exception -> 0x03b1 }
        r3 = new org.telegram.messenger.MediaController$19;	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r3.<init>();	 Catch:{ Exception -> 0x03b1 }
        r2.setPlaybackPositionUpdateListener(r3);	 Catch:{ Exception -> 0x03b1 }
        r0 = r26;	 Catch:{ Exception -> 0x03b1 }
        r2 = r0.audioTrackPlayer;	 Catch:{ Exception -> 0x03b1 }
        r2.play();	 Catch:{ Exception -> 0x03b1 }
        monitor-exit(r25);	 Catch:{ all -> 0x0367 }
        goto L_0x0230;	 Catch:{ all -> 0x0367 }
    L_0x03af:
        r3 = 3;	 Catch:{ all -> 0x0367 }
        goto L_0x037b;	 Catch:{ all -> 0x0367 }
    L_0x03b1:
        r13 = move-exception;	 Catch:{ all -> 0x0367 }
        org.telegram.messenger.FileLog.m3e(r13);	 Catch:{ all -> 0x0367 }
        r0 = r26;	 Catch:{ all -> 0x0367 }
        r2 = r0.audioTrackPlayer;	 Catch:{ all -> 0x0367 }
        if (r2 == 0) goto L_0x03d6;	 Catch:{ all -> 0x0367 }
    L_0x03bb:
        r0 = r26;	 Catch:{ all -> 0x0367 }
        r2 = r0.audioTrackPlayer;	 Catch:{ all -> 0x0367 }
        r2.release();	 Catch:{ all -> 0x0367 }
        r2 = 0;	 Catch:{ all -> 0x0367 }
        r0 = r26;	 Catch:{ all -> 0x0367 }
        r0.audioTrackPlayer = r2;	 Catch:{ all -> 0x0367 }
        r2 = 0;	 Catch:{ all -> 0x0367 }
        r0 = r26;	 Catch:{ all -> 0x0367 }
        r0.isPaused = r2;	 Catch:{ all -> 0x0367 }
        r2 = 0;	 Catch:{ all -> 0x0367 }
        r0 = r26;	 Catch:{ all -> 0x0367 }
        r0.playingMessageObject = r2;	 Catch:{ all -> 0x0367 }
        r2 = 0;	 Catch:{ all -> 0x0367 }
        r0 = r26;	 Catch:{ all -> 0x0367 }
        r0.downloadingCurrentMessage = r2;	 Catch:{ all -> 0x0367 }
    L_0x03d6:
        r2 = 0;	 Catch:{ all -> 0x0367 }
        monitor-exit(r25);	 Catch:{ all -> 0x0367 }
        goto L_0x0003;
    L_0x03da:
        r0 = r26;
        r2 = r0.pipRoundVideoView;
        if (r2 == 0) goto L_0x03ed;
    L_0x03e0:
        r0 = r26;
        r2 = r0.pipRoundVideoView;
        r3 = 1;
        r2.close(r3);
        r2 = 0;
        r0 = r26;
        r0.pipRoundVideoView = r2;
    L_0x03ed:
        r2 = new org.telegram.ui.Components.VideoPlayer;	 Catch:{ Exception -> 0x045c }
        r2.<init>();	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r0.audioPlayer = r2;	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r3 = r0.audioPlayer;	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r2 = r0.useFrontSpeaker;	 Catch:{ Exception -> 0x045c }
        if (r2 == 0) goto L_0x04a9;	 Catch:{ Exception -> 0x045c }
    L_0x0400:
        r2 = 0;	 Catch:{ Exception -> 0x045c }
    L_0x0401:
        r3.setStreamType(r2);	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r2 = r0.audioPlayer;	 Catch:{ Exception -> 0x045c }
        r3 = new org.telegram.messenger.MediaController$20;	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r1 = r27;	 Catch:{ Exception -> 0x045c }
        r3.<init>(r1);	 Catch:{ Exception -> 0x045c }
        r2.setDelegate(r3);	 Catch:{ Exception -> 0x045c }
        if (r17 == 0) goto L_0x04ac;	 Catch:{ Exception -> 0x045c }
    L_0x0416:
        r0 = r27;	 Catch:{ Exception -> 0x045c }
        r2 = r0.mediaExists;	 Catch:{ Exception -> 0x045c }
        if (r2 != 0) goto L_0x042c;	 Catch:{ Exception -> 0x045c }
    L_0x041c:
        r0 = r18;	 Catch:{ Exception -> 0x045c }
        if (r9 == r0) goto L_0x042c;	 Catch:{ Exception -> 0x045c }
    L_0x0420:
        r2 = new org.telegram.messenger.MediaController$21;	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r1 = r27;	 Catch:{ Exception -> 0x045c }
        r2.<init>(r1);	 Catch:{ Exception -> 0x045c }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x045c }
    L_0x042c:
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r2 = r0.audioPlayer;	 Catch:{ Exception -> 0x045c }
        r3 = android.net.Uri.fromFile(r9);	 Catch:{ Exception -> 0x045c }
        r4 = "other";	 Catch:{ Exception -> 0x045c }
        r2.preparePlayer(r3, r4);	 Catch:{ Exception -> 0x045c }
    L_0x043a:
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r2 = r0.audioPlayer;	 Catch:{ Exception -> 0x045c }
        r2.play();	 Catch:{ Exception -> 0x045c }
        r2 = r27.isVoice();	 Catch:{ Exception -> 0x045c }
        if (r2 == 0) goto L_0x0556;	 Catch:{ Exception -> 0x045c }
    L_0x0447:
        r2 = 0;	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r0.audioInfo = r2;	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r2 = r0.playlist;	 Catch:{ Exception -> 0x045c }
        r2.clear();	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r2 = r0.shuffledPlaylist;	 Catch:{ Exception -> 0x045c }
        r2.clear();	 Catch:{ Exception -> 0x045c }
        goto L_0x0230;
    L_0x045c:
        r13 = move-exception;
        org.telegram.messenger.FileLog.m3e(r13);
        r0 = r27;
        r2 = r0.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r4 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r2 = 1;
        r5 = new java.lang.Object[r2];
        r6 = 0;
        r0 = r26;
        r2 = r0.playingMessageObject;
        if (r2 == 0) goto L_0x0566;
    L_0x0474:
        r0 = r26;
        r2 = r0.playingMessageObject;
        r2 = r2.getId();
    L_0x047c:
        r2 = java.lang.Integer.valueOf(r2);
        r5[r6] = r2;
        r3.postNotificationName(r4, r5);
        r0 = r26;
        r2 = r0.audioPlayer;
        if (r2 == 0) goto L_0x04a6;
    L_0x048b:
        r0 = r26;
        r2 = r0.audioPlayer;
        r2.releasePlayer();
        r2 = 0;
        r0 = r26;
        r0.audioPlayer = r2;
        r2 = 0;
        r0 = r26;
        r0.isPaused = r2;
        r2 = 0;
        r0 = r26;
        r0.playingMessageObject = r2;
        r2 = 0;
        r0 = r26;
        r0.downloadingCurrentMessage = r2;
    L_0x04a6:
        r2 = 0;
        goto L_0x0003;
    L_0x04a9:
        r2 = 3;
        goto L_0x0401;
    L_0x04ac:
        r12 = r27.getDocument();	 Catch:{ Exception -> 0x045c }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x045c }
        r2.<init>();	 Catch:{ Exception -> 0x045c }
        r3 = "?account=";	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r0 = r27;	 Catch:{ Exception -> 0x045c }
        r3 = r0.currentAccount;	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r3 = "&id=";	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r4 = r12.id;	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x045c }
        r3 = "&hash=";	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r4 = r12.access_hash;	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x045c }
        r3 = "&dc=";	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r3 = r12.dc_id;	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r3 = "&size=";	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r3 = r12.size;	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r3 = "&mime=";	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r3 = r12.mime_type;	 Catch:{ Exception -> 0x045c }
        r4 = "UTF-8";	 Catch:{ Exception -> 0x045c }
        r3 = java.net.URLEncoder.encode(r3, r4);	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r3 = "&name=";	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r3 = org.telegram.messenger.FileLoader.getDocumentFileName(r12);	 Catch:{ Exception -> 0x045c }
        r4 = "UTF-8";	 Catch:{ Exception -> 0x045c }
        r3 = java.net.URLEncoder.encode(r3, r4);	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r21 = r2.toString();	 Catch:{ Exception -> 0x045c }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x045c }
        r2.<init>();	 Catch:{ Exception -> 0x045c }
        r3 = "tg://";	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r3 = r27.getFileName();	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x045c }
        r0 = r21;	 Catch:{ Exception -> 0x045c }
        r2 = r2.append(r0);	 Catch:{ Exception -> 0x045c }
        r2 = r2.toString();	 Catch:{ Exception -> 0x045c }
        r24 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x045c }
        r0 = r26;	 Catch:{ Exception -> 0x045c }
        r2 = r0.audioPlayer;	 Catch:{ Exception -> 0x045c }
        r3 = "other";	 Catch:{ Exception -> 0x045c }
        r0 = r24;	 Catch:{ Exception -> 0x045c }
        r2.preparePlayer(r0, r3);	 Catch:{ Exception -> 0x045c }
        goto L_0x043a;
    L_0x0556:
        r2 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r9);	 Catch:{ Exception -> 0x0560 }
        r0 = r26;	 Catch:{ Exception -> 0x0560 }
        r0.audioInfo = r2;	 Catch:{ Exception -> 0x0560 }
        goto L_0x0230;
    L_0x0560:
        r13 = move-exception;
        org.telegram.messenger.FileLog.m3e(r13);	 Catch:{ Exception -> 0x045c }
        goto L_0x0230;
    L_0x0566:
        r2 = 0;
        goto L_0x047c;
    L_0x0569:
        r16 = move-exception;
        r0 = r26;
        r2 = r0.playingMessageObject;
        r3 = 0;
        r2.audioProgress = r3;
        r0 = r26;
        r2 = r0.playingMessageObject;
        r3 = 0;
        r2.audioProgressSec = r3;
        r0 = r27;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r0 = r26;
        r6 = r0.playingMessageObject;
        r6 = r6.getId();
        r6 = java.lang.Integer.valueOf(r6);
        r4[r5] = r6;
        r5 = 1;
        r6 = 0;
        r6 = java.lang.Integer.valueOf(r6);
        r4[r5] = r6;
        r2.postNotificationName(r3, r4);
        org.telegram.messenger.FileLog.m3e(r16);
        goto L_0x02b6;
    L_0x05a4:
        r0 = r26;
        r2 = r0.audioPlayer;
        if (r2 == 0) goto L_0x061a;
    L_0x05aa:
        r0 = r26;	 Catch:{ Exception -> 0x05e6 }
        r2 = r0.playingMessageObject;	 Catch:{ Exception -> 0x05e6 }
        r2 = r2.audioProgress;	 Catch:{ Exception -> 0x05e6 }
        r3 = 0;	 Catch:{ Exception -> 0x05e6 }
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));	 Catch:{ Exception -> 0x05e6 }
        if (r2 == 0) goto L_0x02b6;	 Catch:{ Exception -> 0x05e6 }
    L_0x05b5:
        r0 = r26;	 Catch:{ Exception -> 0x05e6 }
        r2 = r0.audioPlayer;	 Catch:{ Exception -> 0x05e6 }
        r14 = r2.getDuration();	 Catch:{ Exception -> 0x05e6 }
        r2 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;	 Catch:{ Exception -> 0x05e6 }
        r2 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1));	 Catch:{ Exception -> 0x05e6 }
        if (r2 != 0) goto L_0x05cf;	 Catch:{ Exception -> 0x05e6 }
    L_0x05c6:
        r0 = r26;	 Catch:{ Exception -> 0x05e6 }
        r2 = r0.playingMessageObject;	 Catch:{ Exception -> 0x05e6 }
        r2 = r2.getDuration();	 Catch:{ Exception -> 0x05e6 }
        r14 = (long) r2;	 Catch:{ Exception -> 0x05e6 }
    L_0x05cf:
        r2 = (float) r14;	 Catch:{ Exception -> 0x05e6 }
        r0 = r26;	 Catch:{ Exception -> 0x05e6 }
        r3 = r0.playingMessageObject;	 Catch:{ Exception -> 0x05e6 }
        r3 = r3.audioProgress;	 Catch:{ Exception -> 0x05e6 }
        r2 = r2 * r3;	 Catch:{ Exception -> 0x05e6 }
        r0 = (int) r2;	 Catch:{ Exception -> 0x05e6 }
        r23 = r0;	 Catch:{ Exception -> 0x05e6 }
        r0 = r26;	 Catch:{ Exception -> 0x05e6 }
        r2 = r0.audioPlayer;	 Catch:{ Exception -> 0x05e6 }
        r0 = r23;	 Catch:{ Exception -> 0x05e6 }
        r4 = (long) r0;	 Catch:{ Exception -> 0x05e6 }
        r2.seekTo(r4);	 Catch:{ Exception -> 0x05e6 }
        goto L_0x02b6;
    L_0x05e6:
        r16 = move-exception;
        r0 = r26;
        r2 = r0.playingMessageObject;
        r2.resetPlayingProgress();
        r0 = r27;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r0 = r26;
        r6 = r0.playingMessageObject;
        r6 = r6.getId();
        r6 = java.lang.Integer.valueOf(r6);
        r4[r5] = r6;
        r5 = 1;
        r6 = 0;
        r6 = java.lang.Integer.valueOf(r6);
        r4[r5] = r6;
        r2.postNotificationName(r3, r4);
        org.telegram.messenger.FileLog.m3e(r16);
        goto L_0x02b6;
    L_0x061a:
        r0 = r26;
        r2 = r0.audioTrackPlayer;
        if (r2 == 0) goto L_0x02b6;
    L_0x0620:
        r0 = r26;
        r2 = r0.playingMessageObject;
        r2 = r2.audioProgress;
        r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 != 0) goto L_0x0633;
    L_0x062c:
        r0 = r26;
        r2 = r0.playingMessageObject;
        r3 = 0;
        r2.audioProgress = r3;
    L_0x0633:
        r0 = r26;
        r2 = r0.fileDecodingQueue;
        r3 = new org.telegram.messenger.MediaController$22;
        r0 = r26;
        r3.<init>();
        r2.postRunnable(r3);
        goto L_0x02b6;
    L_0x0643:
        r13 = move-exception;
        org.telegram.messenger.FileLog.m3e(r13);
        goto L_0x02d2;
    L_0x0649:
        r19 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = org.telegram.messenger.MusicPlayerService.class;
        r0 = r19;
        r0.<init>(r2, r3);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = r19;
        r2.stopService(r0);
        goto L_0x02d2;
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
        if ((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
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
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        } catch (Throwable e) {
            FileLog.m3e(e);
            this.isPaused = false;
            return false;
        }
    }

    public boolean resumeAudio(MessageObject messageObject) {
        if ((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
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

    public boolean isRoundVideoDrawingReady() {
        return this.currentAspectRatioFrameLayout != null && this.currentAspectRatioFrameLayout.isDrawingReady();
    }

    public ArrayList<MessageObject> getPlaylist() {
        return this.playlist;
    }

    public boolean isPlayingMessage(MessageObject messageObject) {
        boolean z = true;
        if ((this.audioTrackPlayer == null && this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null) {
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
        Runnable anonymousClass23 = new Runnable() {

            /* renamed from: org.telegram.messenger.MediaController$23$1 */
            class C02931 implements Runnable {
                C02931() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
            }

            /* renamed from: org.telegram.messenger.MediaController$23$2 */
            class C02942 implements Runnable {
                C02942() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
            }

            /* renamed from: org.telegram.messenger.MediaController$23$3 */
            class C02953 implements Runnable {
                C02953() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
            }

            /* renamed from: org.telegram.messenger.MediaController$23$4 */
            class C02964 implements Runnable {
                C02964() {
                }

                public void run() {
                    MediaController.this.recordStartRunnable = null;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStarted, new Object[0]);
                }
            }

            public void run() {
                if (MediaController.this.audioRecorder != null) {
                    AndroidUtilities.runOnUIThread(new C02931());
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
                        AndroidUtilities.runOnUIThread(new C02942());
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
                    AndroidUtilities.runOnUIThread(new C02964());
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
                    AndroidUtilities.runOnUIThread(new C02953());
                }
            }
        };
        this.recordStartRunnable = anonymousClass23;
        if (paused) {
            j = 500;
        } else {
            j = 50;
        }
        dispatchQueue.postRunnable(anonymousClass23, j);
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

                /* renamed from: org.telegram.messenger.MediaController$25$1 */
                class C02981 implements Runnable {
                    C02981() {
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
                    AndroidUtilities.runOnUIThread(new C02981());
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
            class C02991 implements Runnable {
                C02991() {
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
                    AndroidUtilities.runOnUIThread(new C02991());
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
                                FileLog.m3e(e);
                                finalProgress = progressDialog;
                                i = type;
                                str = name;
                                str2 = mime;
                                new Thread(new Runnable() {

                                    /* renamed from: org.telegram.messenger.MediaController$28$2 */
                                    class C03012 implements Runnable {
                                        C03012() {
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
                                                                    FileLog.m3e(e);
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
                                                FileLog.m3e(e3);
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
                                            FileLog.m3e(e32);
                                        }
                                        if (finalProgress != null) {
                                            AndroidUtilities.runOnUIThread(new C03012());
                                        }
                                    }
                                }).start();
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.m3e(e);
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
                                FileLog.m3e(e2);
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
                    FileLog.m3e(e22);
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.m3e(e222);
                }
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
                            FileLog.m3e(e2);
                        }
                    }
                    return z;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.m3e(e222);
                }
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
        Throwable e;
        Throwable th;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
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
                        FileLog.m3e(e2);
                    }
                }
                if (fileOutputStream == null) {
                    return null;
                }
                try {
                    fileOutputStream.close();
                    return null;
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                    return null;
                }
            }
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            FileOutputStream output = new FileOutputStream(f2);
            try {
                byte[] buffer = new byte[CacheDataSink.DEFAULT_BUFFER_SIZE];
                while (true) {
                    int len = inputStream.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    output.write(buffer, 0, len);
                }
                String absolutePath = f2.getAbsolutePath();
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e222) {
                        FileLog.m3e(e222);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (Throwable e2222) {
                        FileLog.m3e(e2222);
                    }
                }
                fileOutputStream = output;
                return absolutePath;
            } catch (Exception e3) {
                e = e3;
                fileOutputStream = output;
                try {
                    FileLog.m3e(e);
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e22222) {
                            FileLog.m3e(e22222);
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e222222) {
                            FileLog.m3e(e222222);
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e2222222) {
                            FileLog.m3e(e2222222);
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e22222222) {
                            FileLog.m3e(e22222222);
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
            FileLog.m3e(e);
            if (inputStream != null) {
                inputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            return null;
        }
    }

    public static void loadGalleryPhotosAlbums(final int guid) {
        Thread thread = new Thread(new Runnable() {

            /* renamed from: org.telegram.messenger.MediaController$29$1 */
            class C03021 implements Comparator<PhotoEntry> {
                C03021() {
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

            public void run() {
                /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r22_1 'allPhotosAlbum' org.telegram.messenger.MediaController$AlbumEntry) in PHI: PHI: (r22_2 'allPhotosAlbum' org.telegram.messenger.MediaController$AlbumEntry) = (r22_1 'allPhotosAlbum' org.telegram.messenger.MediaController$AlbumEntry), (r22_3 'allPhotosAlbum' org.telegram.messenger.MediaController$AlbumEntry) binds: {(r22_1 'allPhotosAlbum' org.telegram.messenger.MediaController$AlbumEntry)=B:28:?, (r22_3 'allPhotosAlbum' org.telegram.messenger.MediaController$AlbumEntry)=B:172:0x0397}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
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
                r43 = this;
                r37 = new java.util.ArrayList;
                r37.<init>();
                r41 = new java.util.ArrayList;
                r41.<init>();
                r36 = new android.util.SparseArray;
                r36.<init>();
                r40 = new android.util.SparseArray;
                r40.<init>();
                r22 = 0;
                r14 = 0;
                r27 = 0;
                r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0198 }
                r2.<init>();	 Catch:{ Exception -> 0x0198 }
                r10 = android.os.Environment.DIRECTORY_DCIM;	 Catch:{ Exception -> 0x0198 }
                r10 = android.os.Environment.getExternalStoragePublicDirectory(r10);	 Catch:{ Exception -> 0x0198 }
                r10 = r10.getAbsolutePath();	 Catch:{ Exception -> 0x0198 }
                r2 = r2.append(r10);	 Catch:{ Exception -> 0x0198 }
                r10 = "/Camera/";	 Catch:{ Exception -> 0x0198 }
                r2 = r2.append(r10);	 Catch:{ Exception -> 0x0198 }
                r27 = r2.toString();	 Catch:{ Exception -> 0x0198 }
            L_0x0037:
                r13 = 0;
                r42 = 0;
                r28 = 0;
                r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x01a6 }
                r10 = 23;	 Catch:{ Throwable -> 0x01a6 }
                if (r2 < r10) goto L_0x0053;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0042:
                r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x01a6 }
                r10 = 23;	 Catch:{ Throwable -> 0x01a6 }
                if (r2 < r10) goto L_0x02d3;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0048:
                r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x01a6 }
                r10 = "android.permission.READ_EXTERNAL_STORAGE";	 Catch:{ Throwable -> 0x01a6 }
                r2 = r2.checkSelfPermission(r10);	 Catch:{ Throwable -> 0x01a6 }
                if (r2 != 0) goto L_0x02d3;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0053:
                r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x01a6 }
                r2 = r2.getContentResolver();	 Catch:{ Throwable -> 0x01a6 }
                r3 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x01a6 }
                r4 = org.telegram.messenger.MediaController.projectionPhotos;	 Catch:{ Throwable -> 0x01a6 }
                r5 = 0;	 Catch:{ Throwable -> 0x01a6 }
                r6 = 0;	 Catch:{ Throwable -> 0x01a6 }
                r7 = "datetaken DESC";	 Catch:{ Throwable -> 0x01a6 }
                r28 = android.provider.MediaStore.Images.Media.query(r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x01a6 }
                if (r28 == 0) goto L_0x02d3;	 Catch:{ Throwable -> 0x01a6 }
            L_0x006a:
                r2 = "_id";	 Catch:{ Throwable -> 0x01a6 }
                r0 = r28;	 Catch:{ Throwable -> 0x01a6 }
                r35 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x01a6 }
                r2 = "bucket_id";	 Catch:{ Throwable -> 0x01a6 }
                r0 = r28;	 Catch:{ Throwable -> 0x01a6 }
                r24 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x01a6 }
                r2 = "bucket_display_name";	 Catch:{ Throwable -> 0x01a6 }
                r0 = r28;	 Catch:{ Throwable -> 0x01a6 }
                r26 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x01a6 }
                r2 = "_data";	 Catch:{ Throwable -> 0x01a6 }
                r0 = r28;	 Catch:{ Throwable -> 0x01a6 }
                r29 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x01a6 }
                r2 = "datetaken";	 Catch:{ Throwable -> 0x01a6 }
                r0 = r28;	 Catch:{ Throwable -> 0x01a6 }
                r30 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x01a6 }
                r2 = "orientation";	 Catch:{ Throwable -> 0x01a6 }
                r0 = r28;	 Catch:{ Throwable -> 0x01a6 }
                r39 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x01a6 }
                r21 = r14;
                r23 = r22;
            L_0x00a4:
                r2 = r28.moveToNext();	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                if (r2 == 0) goto L_0x02cf;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
            L_0x00aa:
                r0 = r28;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r1 = r35;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r5 = r0.getInt(r1);	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r0 = r28;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r1 = r24;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r4 = r0.getInt(r1);	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r0 = r28;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r1 = r26;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r25 = r0.getString(r1);	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r8 = r28.getString(r29);	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r0 = r28;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r1 = r30;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r6 = r0.getLong(r1);	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r0 = r28;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r1 = r39;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r9 = r0.getInt(r1);	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                if (r8 == 0) goto L_0x00a4;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
            L_0x00d8:
                r2 = r8.length();	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                if (r2 == 0) goto L_0x00a4;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
            L_0x00de:
                r3 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r10 = 0;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r3.<init>(r4, r5, r6, r8, r9, r10);	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                if (r23 != 0) goto L_0x0397;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
            L_0x00e6:
                r22 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r2 = 0;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r10 = "AllPhotos";	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r11 = NUM; // 0x7f0c0057 float:1.8609368E38 double:1.0530974414E-314;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r10 = org.telegram.messenger.LocaleController.getString(r10, r11);	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r0 = r22;	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r0.<init>(r2, r10, r3);	 Catch:{ Throwable -> 0x037d, all -> 0x0371 }
                r2 = 0;
                r0 = r41;	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
                r1 = r22;	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
                r0.add(r2, r1);	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
            L_0x0100:
                if (r21 != 0) goto L_0x0393;	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
            L_0x0102:
                r14 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
                r2 = 0;	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
                r10 = "AllMedia";	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
                r11 = NUM; // 0x7f0c0056 float:1.8609366E38 double:1.053097441E-314;	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
                r10 = org.telegram.messenger.LocaleController.getString(r10, r11);	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
                r14.<init>(r2, r10, r3);	 Catch:{ Throwable -> 0x0384, all -> 0x0378 }
                r2 = 0;
                r0 = r37;	 Catch:{ Throwable -> 0x01a6 }
                r0.add(r2, r14);	 Catch:{ Throwable -> 0x01a6 }
            L_0x0118:
                r0 = r22;	 Catch:{ Throwable -> 0x01a6 }
                r0.addPhoto(r3);	 Catch:{ Throwable -> 0x01a6 }
                r14.addPhoto(r3);	 Catch:{ Throwable -> 0x01a6 }
                r0 = r36;	 Catch:{ Throwable -> 0x01a6 }
                r20 = r0.get(r4);	 Catch:{ Throwable -> 0x01a6 }
                r20 = (org.telegram.messenger.MediaController.AlbumEntry) r20;	 Catch:{ Throwable -> 0x01a6 }
                if (r20 != 0) goto L_0x0154;	 Catch:{ Throwable -> 0x01a6 }
            L_0x012a:
                r20 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x01a6 }
                r0 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r1 = r25;	 Catch:{ Throwable -> 0x01a6 }
                r0.<init>(r4, r1, r3);	 Catch:{ Throwable -> 0x01a6 }
                r0 = r36;	 Catch:{ Throwable -> 0x01a6 }
                r1 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r0.put(r4, r1);	 Catch:{ Throwable -> 0x01a6 }
                if (r13 != 0) goto L_0x019e;	 Catch:{ Throwable -> 0x01a6 }
            L_0x013c:
                if (r27 == 0) goto L_0x019e;	 Catch:{ Throwable -> 0x01a6 }
            L_0x013e:
                if (r8 == 0) goto L_0x019e;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0140:
                r0 = r27;	 Catch:{ Throwable -> 0x01a6 }
                r2 = r8.startsWith(r0);	 Catch:{ Throwable -> 0x01a6 }
                if (r2 == 0) goto L_0x019e;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0148:
                r2 = 0;	 Catch:{ Throwable -> 0x01a6 }
                r0 = r37;	 Catch:{ Throwable -> 0x01a6 }
                r1 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r0.add(r2, r1);	 Catch:{ Throwable -> 0x01a6 }
                r13 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x01a6 }
            L_0x0154:
                r0 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r0.addPhoto(r3);	 Catch:{ Throwable -> 0x01a6 }
                r0 = r40;	 Catch:{ Throwable -> 0x01a6 }
                r20 = r0.get(r4);	 Catch:{ Throwable -> 0x01a6 }
                r20 = (org.telegram.messenger.MediaController.AlbumEntry) r20;	 Catch:{ Throwable -> 0x01a6 }
                if (r20 != 0) goto L_0x018d;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0163:
                r20 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x01a6 }
                r0 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r1 = r25;	 Catch:{ Throwable -> 0x01a6 }
                r0.<init>(r4, r1, r3);	 Catch:{ Throwable -> 0x01a6 }
                r0 = r40;	 Catch:{ Throwable -> 0x01a6 }
                r1 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r0.put(r4, r1);	 Catch:{ Throwable -> 0x01a6 }
                if (r42 != 0) goto L_0x02bf;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0175:
                if (r27 == 0) goto L_0x02bf;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0177:
                if (r8 == 0) goto L_0x02bf;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0179:
                r0 = r27;	 Catch:{ Throwable -> 0x01a6 }
                r2 = r8.startsWith(r0);	 Catch:{ Throwable -> 0x01a6 }
                if (r2 == 0) goto L_0x02bf;	 Catch:{ Throwable -> 0x01a6 }
            L_0x0181:
                r2 = 0;	 Catch:{ Throwable -> 0x01a6 }
                r0 = r41;	 Catch:{ Throwable -> 0x01a6 }
                r1 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r0.add(r2, r1);	 Catch:{ Throwable -> 0x01a6 }
                r42 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x01a6 }
            L_0x018d:
                r0 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r0.addPhoto(r3);	 Catch:{ Throwable -> 0x01a6 }
                r21 = r14;
                r23 = r22;
                goto L_0x00a4;
            L_0x0198:
                r34 = move-exception;
                org.telegram.messenger.FileLog.m3e(r34);
                goto L_0x0037;
            L_0x019e:
                r0 = r37;	 Catch:{ Throwable -> 0x01a6 }
                r1 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r0.add(r1);	 Catch:{ Throwable -> 0x01a6 }
                goto L_0x0154;
            L_0x01a6:
                r34 = move-exception;
            L_0x01a7:
                org.telegram.messenger.FileLog.m3e(r34);	 Catch:{ all -> 0x02c8 }
                if (r28 == 0) goto L_0x038d;
            L_0x01ac:
                r28.close();	 Catch:{ Exception -> 0x02e8 }
                r38 = r13;
                r21 = r14;
            L_0x01b3:
                r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r10 = 23;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                if (r2 < r10) goto L_0x01ca;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
            L_0x01b9:
                r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r10 = 23;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                if (r2 < r10) goto L_0x0301;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
            L_0x01bf:
                r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r10 = "android.permission.READ_EXTERNAL_STORAGE";	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r2 = r2.checkSelfPermission(r10);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                if (r2 != 0) goto L_0x0301;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
            L_0x01ca:
                r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r10 = r2.getContentResolver();	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r11 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r12 = org.telegram.messenger.MediaController.projectionVideo;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r13 = 0;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r14 = 0;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r15 = "datetaken DESC";	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r28 = android.provider.MediaStore.Images.Media.query(r10, r11, r12, r13, r14, r15);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                if (r28 == 0) goto L_0x0301;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
            L_0x01e1:
                r2 = "_id";	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r35 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r2 = "bucket_id";	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r24 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r2 = "bucket_display_name";	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r26 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r2 = "_data";	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r29 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r2 = "datetaken";	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r30 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r2 = "duration";	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r31 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
            L_0x0217:
                r2 = r28.moveToNext();	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                if (r2 == 0) goto L_0x0301;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
            L_0x021d:
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r1 = r35;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r5 = r0.getInt(r1);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r1 = r24;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r4 = r0.getInt(r1);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r1 = r26;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r25 = r0.getString(r1);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r8 = r28.getString(r29);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r1 = r30;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r6 = r0.getLong(r1);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = r28;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r1 = r31;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r32 = r0.getLong(r1);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                if (r8 == 0) goto L_0x0217;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
            L_0x024b:
                r2 = r8.length();	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                if (r2 == 0) goto L_0x0217;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
            L_0x0251:
                r3 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r10 = r32 / r10;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r0 = (int) r10;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r17 = r0;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r18 = 1;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r11 = r3;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r12 = r4;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r13 = r5;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r14 = r6;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r16 = r8;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r11.<init>(r12, r13, r14, r16, r17, r18);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                if (r21 != 0) goto L_0x0389;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
            L_0x0267:
                r14 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r2 = 0;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r10 = "AllMedia";	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r11 = NUM; // 0x7f0c0056 float:1.8609366E38 double:1.053097441E-314;	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r10 = org.telegram.messenger.LocaleController.getString(r10, r11);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r14.<init>(r2, r10, r3);	 Catch:{ Throwable -> 0x0332, all -> 0x0345 }
                r2 = 0;
                r0 = r37;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r0.add(r2, r14);	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
            L_0x027d:
                r14.addPhoto(r3);	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r0 = r36;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r20 = r0.get(r4);	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r20 = (org.telegram.messenger.MediaController.AlbumEntry) r20;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                if (r20 != 0) goto L_0x02fe;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
            L_0x028a:
                r20 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r0 = r20;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r1 = r25;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r0.<init>(r4, r1, r3);	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r0 = r36;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r1 = r20;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r0.put(r4, r1);	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                if (r38 != 0) goto L_0x02f7;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
            L_0x029c:
                if (r27 == 0) goto L_0x02f7;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
            L_0x029e:
                if (r8 == 0) goto L_0x02f7;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
            L_0x02a0:
                r0 = r27;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r2 = r8.startsWith(r0);	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                if (r2 == 0) goto L_0x02f7;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
            L_0x02a8:
                r2 = 0;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r0 = r37;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r1 = r20;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r0.add(r2, r1);	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r13 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
            L_0x02b4:
                r0 = r20;	 Catch:{ Throwable -> 0x036f }
                r0.addPhoto(r3);	 Catch:{ Throwable -> 0x036f }
                r38 = r13;
                r21 = r14;
                goto L_0x0217;
            L_0x02bf:
                r0 = r41;	 Catch:{ Throwable -> 0x01a6 }
                r1 = r20;	 Catch:{ Throwable -> 0x01a6 }
                r0.add(r1);	 Catch:{ Throwable -> 0x01a6 }
                goto L_0x018d;
            L_0x02c8:
                r2 = move-exception;
            L_0x02c9:
                if (r28 == 0) goto L_0x02ce;
            L_0x02cb:
                r28.close();	 Catch:{ Exception -> 0x02f2 }
            L_0x02ce:
                throw r2;
            L_0x02cf:
                r14 = r21;
                r22 = r23;
            L_0x02d3:
                if (r28 == 0) goto L_0x038d;
            L_0x02d5:
                r28.close();	 Catch:{ Exception -> 0x02de }
                r38 = r13;
                r21 = r14;
                goto L_0x01b3;
            L_0x02de:
                r34 = move-exception;
                org.telegram.messenger.FileLog.m3e(r34);
                r38 = r13;
                r21 = r14;
                goto L_0x01b3;
            L_0x02e8:
                r34 = move-exception;
                org.telegram.messenger.FileLog.m3e(r34);
                r38 = r13;
                r21 = r14;
                goto L_0x01b3;
            L_0x02f2:
                r34 = move-exception;
                org.telegram.messenger.FileLog.m3e(r34);
                goto L_0x02ce;
            L_0x02f7:
                r0 = r37;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r1 = r20;	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
                r0.add(r1);	 Catch:{ Throwable -> 0x036b, all -> 0x0365 }
            L_0x02fe:
                r13 = r38;
                goto L_0x02b4;
            L_0x0301:
                r13 = r38;
                r14 = r21;
                if (r28 == 0) goto L_0x030a;
            L_0x0307:
                r28.close();	 Catch:{ Exception -> 0x032d }
            L_0x030a:
                r19 = 0;
            L_0x030c:
                r2 = r37.size();
                r0 = r19;
                if (r0 >= r2) goto L_0x0355;
            L_0x0314:
                r0 = r37;
                r1 = r19;
                r2 = r0.get(r1);
                r2 = (org.telegram.messenger.MediaController.AlbumEntry) r2;
                r2 = r2.photos;
                r10 = new org.telegram.messenger.MediaController$29$1;
                r0 = r43;
                r10.<init>();
                java.util.Collections.sort(r2, r10);
                r19 = r19 + 1;
                goto L_0x030c;
            L_0x032d:
                r34 = move-exception;
                org.telegram.messenger.FileLog.m3e(r34);
                goto L_0x030a;
            L_0x0332:
                r34 = move-exception;
                r13 = r38;
                r14 = r21;
            L_0x0337:
                org.telegram.messenger.FileLog.m3e(r34);	 Catch:{ all -> 0x0369 }
                if (r28 == 0) goto L_0x030a;
            L_0x033c:
                r28.close();	 Catch:{ Exception -> 0x0340 }
                goto L_0x030a;
            L_0x0340:
                r34 = move-exception;
                org.telegram.messenger.FileLog.m3e(r34);
                goto L_0x030a;
            L_0x0345:
                r2 = move-exception;
                r13 = r38;
                r14 = r21;
            L_0x034a:
                if (r28 == 0) goto L_0x034f;
            L_0x034c:
                r28.close();	 Catch:{ Exception -> 0x0350 }
            L_0x034f:
                throw r2;
            L_0x0350:
                r34 = move-exception;
                org.telegram.messenger.FileLog.m3e(r34);
                goto L_0x034f;
            L_0x0355:
                r0 = r43;
                r10 = r2;
                r16 = 0;
                r11 = r37;
                r12 = r41;
                r15 = r22;
                org.telegram.messenger.MediaController.broadcastNewPhotos(r10, r11, r12, r13, r14, r15, r16);
                return;
            L_0x0365:
                r2 = move-exception;
                r13 = r38;
                goto L_0x034a;
            L_0x0369:
                r2 = move-exception;
                goto L_0x034a;
            L_0x036b:
                r34 = move-exception;
                r13 = r38;
                goto L_0x0337;
            L_0x036f:
                r34 = move-exception;
                goto L_0x0337;
            L_0x0371:
                r2 = move-exception;
                r14 = r21;
                r22 = r23;
                goto L_0x02c9;
            L_0x0378:
                r2 = move-exception;
                r14 = r21;
                goto L_0x02c9;
            L_0x037d:
                r34 = move-exception;
                r14 = r21;
                r22 = r23;
                goto L_0x01a7;
            L_0x0384:
                r34 = move-exception;
                r14 = r21;
                goto L_0x01a7;
            L_0x0389:
                r14 = r21;
                goto L_0x027d;
            L_0x038d:
                r38 = r13;
                r21 = r14;
                goto L_0x01b3;
            L_0x0393:
                r14 = r21;
                goto L_0x0118;
            L_0x0397:
                r22 = r23;
                goto L_0x0100;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.29.run():void");
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
                FileLog.m3e(e);
            }
        }
        VideoConvertRunnable.runConversion(messageObject);
        return true;
    }

    @SuppressLint({"NewApi"})
    public static MediaCodecInfo selectCodec(String mimeType) {
        MediaCodecInfo lastCodecInfo;
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo lastCodecInfo2 = null;
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (codecInfo.isEncoder()) {
                for (String type : codecInfo.getSupportedTypes()) {
                    if (type.equalsIgnoreCase(mimeType)) {
                        lastCodecInfo2 = codecInfo;
                        String name = lastCodecInfo2.getName();
                        if (name == null) {
                            continue;
                        } else if (!name.equals("OMX.SEC.avc.enc")) {
                            lastCodecInfo = lastCodecInfo2;
                            return lastCodecInfo2;
                        } else if (name.equals("OMX.SEC.AVC.Encoder")) {
                            lastCodecInfo = lastCodecInfo2;
                            return lastCodecInfo2;
                        }
                    }
                }
                continue;
            }
        }
        lastCodecInfo = lastCodecInfo2;
        return lastCodecInfo2;
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
        int videoTrackIndex = findTrack(extractor, false);
        int audioTrackIndex = needAudio ? findTrack(extractor, true) : -1;
        int muxerVideoTrackIndex = -1;
        int muxerAudioTrackIndex = -1;
        boolean inputDone = false;
        int maxBufferSize = 0;
        if (videoTrackIndex >= 0) {
            extractor.selectTrack(videoTrackIndex);
            MediaFormat trackFormat = extractor.getTrackFormat(videoTrackIndex);
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
        synchronized (this.videoConvertSync) {
            boolean cancelConversion = this.cancelCurrentVideoConversion;
        }
        if (cancelConversion) {
            throw new RuntimeException("canceled conversion");
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideo(MessageObject messageObject) {
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
        int framerate = messageObject.videoEditedInfo.framerate;
        int bitrate = messageObject.videoEditedInfo.bitrate;
        int rotateRender = 0;
        boolean isSecret = ((int) messageObject.getDialogId()) == 0;
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
            mP4Builder = new MP4Builder().createMovie(movie, isSecret);
            MediaExtractor extractor2 = new MediaExtractor();
            try {
                extractor2.setDataSource(videoPath);
                checkConversionCanceled();
                if (resultWidth == originalWidth && resultHeight == originalHeight && rotateRender == 0 && !messageObject.videoEditedInfo.roundVideo) {
                    readAndWriteTracks(messageObject, extractor2, mP4Builder, info, startTime, endTime, file, bitrate != -1);
                } else {
                    int videoIndex = findTrack(extractor2, false);
                    int audioIndex = bitrate != -1 ? findTrack(extractor2, true) : -1;
                    if (videoIndex >= 0) {
                        int colorFormat;
                        MediaCodec decoder = null;
                        MediaCodec encoder = null;
                        InputSurface inputSurface = null;
                        OutputSurface outputSurface = null;
                        long videoTime = -1;
                        boolean outputDone = false;
                        boolean z = false;
                        boolean decoderDone = false;
                        int swapUV = 0;
                        int videoTrackIndex = -5;
                        int audioTrackIndex = -5;
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
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("codec = " + codecInfo.getName() + " manufacturer = " + manufacturer + "device = " + Build.MODEL);
                            }
                        } else {
                            colorFormat = NUM;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("colorFormat = " + colorFormat);
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
                            audioTrackIndex = mP4Builder.addTrack(audioFormat, true);
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
                                FileLog.m3e(e);
                                error = true;
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
                                if (extractor2 != null) {
                                    extractor2.release();
                                }
                                if (mP4Builder != null) {
                                    try {
                                        mP4Builder.finishMovie();
                                    } catch (Throwable e3) {
                                        FileLog.m3e(e3);
                                    }
                                }
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("time = " + (System.currentTimeMillis() - time));
                                    extractor = extractor2;
                                } else {
                                    extractor = extractor2;
                                }
                                preferences.edit().putBoolean("isPreviousOk", true).commit();
                                didWriteData(messageObject, file, true, error);
                                return true;
                            } catch (Throwable th2) {
                                th = th2;
                                extractor = extractor2;
                            }
                        }
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
                                if (!z) {
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
                                                z = true;
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
                                            z = true;
                                        }
                                        if (info.size > 0 && (endTime < 0 || info.presentationTimeUs < endTime)) {
                                            info.offset = 0;
                                            info.flags = extractor2.getSampleFlags();
                                            if (mP4Builder.writeSampleData(audioTrackIndex, audioBuffer, info, false)) {
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
                                            z = true;
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
                                                newFormat = decoder.getOutputFormat();
                                                if (BuildVars.LOGS_ENABLED) {
                                                    FileLog.m0d("newFormat = " + newFormat);
                                                }
                                            } else if (decoderStatus < 0) {
                                                throw new RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                                            } else {
                                                boolean doRender = VERSION.SDK_INT >= 18 ? info.size != 0 : (info.size == 0 && info.presentationTimeUs == 0) ? false : true;
                                                if (endTime > 0 && info.presentationTimeUs >= endTime) {
                                                    z = true;
                                                    decoderDone = true;
                                                    doRender = false;
                                                    info.flags |= 4;
                                                }
                                                if (startTime > 0 && videoTime == -1) {
                                                    if (info.presentationTimeUs < startTime) {
                                                        doRender = false;
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            FileLog.m0d("drop frame startTime = " + startTime + " present time = " + info.presentationTimeUs);
                                                        }
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
                                                            } else if (BuildVars.LOGS_ENABLED) {
                                                                FileLog.m0d("input buffer not available");
                                                            }
                                                        }
                                                    }
                                                }
                                                if ((info.flags & 4) != 0) {
                                                    decoderOutputAvailable = false;
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        FileLog.m0d("decoder stream end");
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
                        } catch (Exception e4) {
                            e3 = e4;
                        } catch (Throwable th22) {
                            th = th22;
                            extractor = extractor2;
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
                if (mP4Builder != null) {
                    mP4Builder.finishMovie();
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("time = " + (System.currentTimeMillis() - time));
                    extractor = extractor2;
                } else {
                    extractor = extractor2;
                }
            } catch (Exception e5) {
                e3 = e5;
                extractor = extractor2;
                error = true;
                try {
                    FileLog.m3e(e3);
                    if (extractor != null) {
                        extractor.release();
                    }
                    if (mP4Builder != null) {
                        try {
                            mP4Builder.finishMovie();
                        } catch (Throwable e32) {
                            FileLog.m3e(e32);
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("time = " + (System.currentTimeMillis() - time));
                    }
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
                            FileLog.m3e(e322);
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("time = " + (System.currentTimeMillis() - time));
                    }
                    throw th;
                }
            } catch (Throwable th222) {
                th = th222;
                extractor = extractor2;
            }
        } catch (Exception e6) {
            e322 = e6;
            error = true;
            FileLog.m3e(e322);
            if (extractor != null) {
                extractor.release();
            }
            if (mP4Builder != null) {
                mP4Builder.finishMovie();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("time = " + (System.currentTimeMillis() - time));
            }
            preferences.edit().putBoolean("isPreviousOk", true).commit();
            didWriteData(messageObject, file, true, error);
            return true;
        }
        preferences.edit().putBoolean("isPreviousOk", true).commit();
        didWriteData(messageObject, file, true, error);
        return true;
    }
}
