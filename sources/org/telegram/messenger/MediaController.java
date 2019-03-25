package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.voip.VoIPService;
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
    public static AlbumEntry allVideosAlbumEntry;
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
    private float currentPlaybackSpeed = 1.0f;
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
    private MessageObject goingToShowMessageObject;
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
    private boolean playerWasReady;
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
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    buffer.position(0);
                    double amplitude = Math.sqrt((sum / ((double) len)) / 2.0d);
                    ByteBuffer finalBuffer = buffer;
                    boolean flush = len != buffer.capacity();
                    if (len != 0) {
                        MediaController.this.fileEncodingQueue.postRunnable(new MediaController$2$$Lambda$0(this, finalBuffer, flush));
                    }
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(new MediaController$2$$Lambda$1(this, amplitude));
                    return;
                }
                MediaController.this.recordBuffers.add(buffer);
                MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$run$1$MediaController$2(ByteBuffer finalBuffer, boolean flush) {
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
            MediaController.this.recordQueue.postRunnable(new MediaController$2$$Lambda$2(this, finalBuffer));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$0$MediaController$2(ByteBuffer finalBuffer) {
            MediaController.this.recordBuffers.add(finalBuffer);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$run$2$MediaController$2(double amplitude) {
            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(amplitude));
        }
    };
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
    private Runnable setLoadingRunnable = new Runnable() {
        public void run() {
            if (MediaController.this.playingMessageObject != null) {
                FileLoader.getInstance(MediaController.this.playingMessageObject.currentAccount).setLoadingVideo(MediaController.this.playingMessageObject.getDocument(), true, false);
            }
        }
    };
    private ArrayList<MessageObject> shuffledPlaylist = new ArrayList();
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
        public GalleryObserverExternal() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = MediaController$GalleryObserverExternal$$Lambda$0.$instance, 2000);
        }

        static final /* synthetic */ void lambda$onChange$0$MediaController$GalleryObserverExternal() {
            MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    private class GalleryObserverInternal extends ContentObserver {
        public GalleryObserverInternal() {
            super(null);
        }

        private void scheduleReloadRunnable() {
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new MediaController$GalleryObserverInternal$$Lambda$0(this), 2000);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$scheduleReloadRunnable$0$MediaController$GalleryObserverInternal() {
            if (PhotoViewer.getInstance().isVisible()) {
                scheduleReloadRunnable();
                return;
            }
            MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
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
        public boolean canDeleteAfter;
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
                this.localUrl = "";
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

    private final class StopMediaObserverRunnable implements Runnable {
        public int currentObserverToken;

        private StopMediaObserverRunnable() {
            this.currentObserverToken = 0;
        }

        /* synthetic */ StopMediaObserverRunnable(MediaController x0, AnonymousClass1 x1) {
            this();
        }

        public void run() {
            if (this.currentObserverToken == MediaController.this.startObserverToken) {
                try {
                    if (MediaController.this.internalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
                        MediaController.this.internalObserver = null;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    if (MediaController.this.externalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
                        MediaController.this.externalObserver = null;
                    }
                } catch (Exception e2) {
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

        public static void runConversion(MessageObject obj) {
            new Thread(new MediaController$VideoConvertRunnable$$Lambda$0(obj)).start();
        }

        static final /* synthetic */ void lambda$runConversion$0$MediaController$VideoConvertRunnable(MessageObject obj) {
            try {
                Thread th = new Thread(new VideoConvertRunnable(obj), "VideoConvertRunnable");
                th.start();
                th.join();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static native int isOpusFile(String str);

    private native int startRecord(String str);

    private native void stopRecord();

    private native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    public static void checkGallery() {
        if (VERSION.SDK_INT >= 24 && allPhotosAlbumEntry != null) {
            Utilities.globalQueue.postRunnable(new MediaController$$Lambda$0(allPhotosAlbumEntry.photos.size()), 2000);
        }
    }

    static final /* synthetic */ void lambda$checkGallery$0$MediaController(int prevSize) {
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
            if (refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(refreshGalleryRunnable);
                refreshGalleryRunnable = null;
            }
            loadGalleryPhotosAlbums(0);
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
        this.recordQueue.postRunnable(new MediaController$$Lambda$1(this));
        Utilities.globalQueue.postRunnable(new MediaController$$Lambda$2(this));
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new MediaController$$Lambda$3(this));
        this.mediaProjections = new String[]{"_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height"};
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        try {
            contentResolver.registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            contentResolver.registerContentObserver(Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            contentResolver.registerContentObserver(Video.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Exception e22) {
            FileLog.e(e22);
        }
        try {
            contentResolver.registerContentObserver(Video.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Exception e222) {
            FileLog.e(e222);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$1$MediaController() {
        try {
            this.recordBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
            if (this.recordBufferSize <= 0) {
                this.recordBufferSize = 1280;
            }
            for (int a = 0; a < 5; a++) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                buffer.order(ByteOrder.nativeOrder());
                this.recordBuffers.add(buffer);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$2$MediaController() {
        try {
            this.currentPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("playbackSpeed", 1.0f);
            this.sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
            this.linearSensor = this.sensorManager.getDefaultSensor(10);
            this.gravitySensor = this.sensorManager.getDefaultSensor(9);
            if (this.linearSensor == null || this.gravitySensor == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("gravity or linear sensor not found");
                }
                this.accelerometerSensor = this.sensorManager.getDefaultSensor(1);
                this.linearSensor = null;
                this.gravitySensor = null;
            }
            this.proximitySensor = this.sensorManager.getDefaultSensor(8);
            this.proximityWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                public void onCallStateChanged(int state, String incomingNumber) {
                    AndroidUtilities.runOnUIThread(new MediaController$3$$Lambda$0(this, state));
                }

                /* Access modifiers changed, original: final|synthetic */
                public final /* synthetic */ void lambda$onCallStateChanged$0$MediaController$3(int state) {
                    EmbedBottomSheet embedBottomSheet;
                    if (state == 1) {
                        if (MediaController.this.isPlayingMessage(MediaController.this.playingMessageObject) && !MediaController.this.isMessagePaused()) {
                            MediaController.this.lambda$startAudioAgain$5$MediaController(MediaController.this.playingMessageObject);
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
            };
            TelephonyManager mgr = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (mgr != null) {
                mgr.listen(phoneStateListener, 32);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$3$MediaController() {
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.didReceiveNewMessages);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.musicDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
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
                lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
                this.resumeAudioOnFocusGain = true;
            }
        }
        setPlayerVolume();
    }

    private void setPlayerVolume() {
        try {
            float volume;
            if (this.audioFocus != 1) {
                volume = 1.0f;
            } else {
                volume = 0.2f;
            }
            if (this.audioPlayer != null) {
                this.audioPlayer.setVolume(volume);
            } else if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(volume);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void startProgressTimer(final MessageObject currentPlayingMessageObject) {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            String fileName = currentPlayingMessageObject.getFileName();
            this.progressTimer = new Timer();
            this.progressTimer.schedule(new TimerTask() {
                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new MediaController$4$$Lambda$0(this, currentPlayingMessageObject));
                    }
                }

                /* Access modifiers changed, original: final|synthetic */
                public final /* synthetic */ void lambda$run$0$MediaController$4(MessageObject currentPlayingMessageObject) {
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
                                if (duration != -9223372036854775807L && progress != -9223372036854775807L && progress >= 0 && duration > 0) {
                                    bufferedValue = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / ((float) duration);
                                    if (duration >= 0) {
                                        value = ((float) progress) / ((float) duration);
                                    } else {
                                        value = 0.0f;
                                    }
                                    if (value >= 1.0f) {
                                        return;
                                    }
                                }
                                return;
                            }
                            duration = MediaController.this.audioPlayer.getDuration();
                            progress = MediaController.this.audioPlayer.getCurrentPosition();
                            if (duration == -9223372036854775807L || duration < 0) {
                                value = 0.0f;
                            } else {
                                value = ((float) progress) / ((float) duration);
                            }
                            bufferedValue = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) duration);
                            if (duration == -9223372036854775807L || progress < 0 || MediaController.this.seekToProgressPending != 0.0f) {
                                return;
                            }
                            MediaController.this.lastProgress = progress;
                            currentPlayingMessageObject.audioPlayerDuration = (int) (duration / 1000);
                            currentPlayingMessageObject.audioProgress = value;
                            currentPlayingMessageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                            currentPlayingMessageObject.bufferedProgress = bufferedValue;
                            NotificationCenter.getInstance(currentPlayingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(currentPlayingMessageObject.getId()), Float.valueOf(value));
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
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
                } catch (Exception e) {
                    FileLog.e(e);
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
        ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
        this.startObserverToken++;
        try {
            if (this.internalObserver == null) {
                contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                uri = Media.EXTERNAL_CONTENT_URI;
                ExternalObserver externalObserver = new ExternalObserver();
                this.externalObserver = externalObserver;
                contentResolver.registerContentObserver(uri, false, externalObserver);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            if (this.externalObserver == null) {
                contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                uri = Media.INTERNAL_CONTENT_URI;
                InternalObserver internalObserver = new InternalObserver();
                this.internalObserver = internalObserver;
                contentResolver.registerContentObserver(uri, false, internalObserver);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void stopMediaObserver() {
        if (this.stopMediaObserverRunnable == null) {
            this.stopMediaObserverRunnable = new StopMediaObserverRunnable(this, null);
        }
        this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
        ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, 5000);
    }

    private void processMediaObserver(Uri uri) {
        Cursor cursor = null;
        try {
            android.graphics.Point size = AndroidUtilities.getRealScreenSize();
            cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
            ArrayList<Long> screenshotDates = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String val = "";
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
                AndroidUtilities.runOnUIThread(new MediaController$$Lambda$4(this, screenshotDates));
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e2) {
                }
            }
        } catch (Exception e3) {
            FileLog.e(e3);
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

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processMediaObserver$4$MediaController(ArrayList screenshotDates) {
        NotificationCenter.getInstance(this.lastChatAccount).postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
        checkScreenshots(screenshotDates);
    }

    private void checkScreenshots(ArrayList<Long> dates) {
        if (dates != null && !dates.isEmpty() && this.lastChatEnterTime != 0) {
            if (this.lastUser != null || (this.lastSecretChat instanceof TL_encryptedChat)) {
                boolean send = false;
                for (int a = 0; a < dates.size(); a++) {
                    Long date = (Long) dates.get(a);
                    if ((this.lastMediaCheckTime == 0 || date.longValue() > this.lastMediaCheckTime) && date.longValue() >= this.lastChatEnterTime && (this.lastChatLeaveTime == 0 || date.longValue() <= this.lastChatLeaveTime + 2000)) {
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
        if (id == NotificationCenter.fileDidLoad || id == NotificationCenter.httpFileDidLoad) {
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
        } else if (id == NotificationCenter.musicDidLoad) {
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
        } else if (id == NotificationCenter.didReceiveNewMessages) {
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
                getInstance().lambda$startAudioAgain$5$MediaController(getInstance().getPlayingMessageObject());
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean isRecordingAudio() {
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
                    FileLog.d("proximity changed to " + event.values[0]);
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
                                FileLog.d("motion detected");
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
                    FileLog.d("sensor values reached");
                }
                if (this.playingMessageObject == null && this.recordStartRunnable == null && this.recordingAudio == null && !PhotoViewer.getInstance().isVisible() && ApplicationLoader.isScreenOn && !this.inputFieldHasText && this.allowStartRecord && this.raiseChat != null && !this.callInProgress) {
                    if (!this.raiseToEarRecord) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start record");
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
                        FileLog.d("start listen");
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
                        FileLog.d("start listen by proximity only");
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
                        FileLog.d("stop record");
                    }
                    stopRecording(2);
                    this.raiseToEarRecord = false;
                    this.ignoreOnPause = false;
                    if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                        this.proximityWakeLock.release();
                    }
                } else if (this.useFrontSpeaker) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("stop listen");
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
                    lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
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
            MessageObject currentMessageObject = this.playingMessageObject;
            float progress = this.playingMessageObject.audioProgress;
            cleanupPlayer(false, true);
            currentMessageObject.audioProgress = progress;
            playMessage(currentMessageObject);
            if (!paused) {
                return;
            }
            if (post) {
                AndroidUtilities.runOnUIThread(new MediaController$$Lambda$5(this, currentMessageObject), 100);
            } else {
                lambda$startAudioAgain$5$MediaController(currentMessageObject);
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
                Utilities.globalQueue.postRunnable(new MediaController$$Lambda$6(this));
                this.sensorsStarted = true;
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$startRaiseToEarSensors$6$MediaController() {
        if (this.gravitySensor != null) {
            this.sensorManager.registerListener(this, this.gravitySensor, 30000);
        }
        if (this.linearSensor != null) {
            this.sensorManager.registerListener(this, this.linearSensor, 30000);
        }
        if (this.accelerometerSensor != null) {
            this.sensorManager.registerListener(this, this.accelerometerSensor, 30000);
        }
        this.sensorManager.registerListener(this, this.proximitySensor, 3);
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
                Utilities.globalQueue.postRunnable(new MediaController$$Lambda$7(this));
                if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$stopRaiseToEarSensors$7$MediaController() {
        if (this.linearSensor != null) {
            this.sensorManager.unregisterListener(this, this.linearSensor);
        }
        if (this.gravitySensor != null) {
            this.sensorManager.unregisterListener(this, this.gravitySensor);
        }
        if (this.accelerometerSensor != null) {
            this.sensorManager.unregisterListener(this, this.accelerometerSensor);
        }
        this.sensorManager.unregisterListener(this, this.proximitySensor);
    }

    public void cleanupPlayer(boolean notify, boolean stopService) {
        cleanupPlayer(notify, stopService, false, false);
    }

    public void cleanupPlayer(boolean notify, boolean stopService, boolean byVoiceEnd, boolean transferPlayerToPhotoViewer) {
        if (this.audioPlayer != null) {
            try {
                this.audioPlayer.releasePlayer(true);
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.audioPlayer = null;
        } else if (this.videoPlayer != null) {
            this.currentAspectRatioFrameLayout = null;
            this.currentTextureViewContainer = null;
            this.currentAspectRatioFrameLayoutReady = false;
            this.isDrawingWasReady = false;
            this.currentTextureView = null;
            this.goingToShowMessageObject = null;
            if (transferPlayerToPhotoViewer) {
                PhotoViewer.getInstance().injectVideoPlayer(this.videoPlayer);
                this.goingToShowMessageObject = this.playingMessageObject;
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, Boolean.valueOf(true));
            } else {
                long position = this.videoPlayer.getCurrentPosition();
                if (this.playingMessageObject != null && this.playingMessageObject.isVideo() && position > 0 && position != -9223372036854775807L) {
                    this.playingMessageObject.audioProgressMs = (int) position;
                    NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, Boolean.valueOf(false));
                }
                this.videoPlayer.releasePlayer(true);
                this.videoPlayer = null;
            }
            try {
                this.baseActivity.getWindow().clearFlags(128);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            if (!(this.playingMessageObject == null || transferPlayerToPhotoViewer)) {
                AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
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

    public boolean isGoingToShowMessageObject(MessageObject messageObject) {
        return this.goingToShowMessageObject == messageObject;
    }

    public void resetGoingToShowMessageObject() {
        this.goingToShowMessageObject = null;
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
                if (duration == -9223372036854775807L) {
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
        } catch (Exception e) {
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
                        this.audioPlayer.releasePlayer(true);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    this.audioPlayer = null;
                } else if (this.videoPlayer != null) {
                    this.currentAspectRatioFrameLayout = null;
                    this.currentTextureViewContainer = null;
                    this.currentAspectRatioFrameLayoutReady = false;
                    this.currentTextureView = null;
                    this.videoPlayer.releasePlayer(true);
                    this.videoPlayer = null;
                    try {
                        this.baseActivity.getWindow().clearFlags(128);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
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

    /* Access modifiers changed, original: protected */
    public void checkIsNextMediaFileDownloaded() {
        if (this.playingMessageObject != null && this.playingMessageObject.isMusic()) {
            checkIsNextMusicFileDownloaded(this.playingMessageObject.currentAccount);
        }
    }

    private void checkIsNextVoiceFileDownloaded(int currentAccount) {
        if (this.voiceMessagesPlaylist != null && this.voiceMessagesPlaylist.size() >= 2) {
            Object nextAudio = (MessageObject) this.voiceMessagesPlaylist.get(true);
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
                FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
            }
        }
    }

    private void checkIsNextMusicFileDownloaded(int currentAccount) {
        if (DownloadController.getInstance(currentAccount).canDownloadNextTrack()) {
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
                if (nextIndex >= 0 && nextIndex < currentPlayList.size()) {
                    Object nextAudio = (MessageObject) currentPlayList.get(nextIndex);
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
                        FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
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

    public void setCurrentVideoVisible(boolean visible) {
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
                        this.pipRoundVideoView.show(this.baseActivity, new MediaController$$Lambda$8(this));
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

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setCurrentVideoVisible$8$MediaController() {
        cleanupPlayer(true, true);
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
                if (this.currentAspectRatioFrameLayoutReady && this.currentAspectRatioFrameLayout != null && this.currentAspectRatioFrameLayout != null) {
                    this.currentAspectRatioFrameLayout.setAspectRatio(this.currentAspectRatioFrameLayoutRatio, this.currentAspectRatioFrameLayoutRotation);
                }
            }
        }
    }

    public boolean hasFlagSecureFragment() {
        return this.flagSecureFragment != null;
    }

    public void setFlagSecure(BaseFragment parentFragment, boolean set) {
        if (set) {
            try {
                parentFragment.getParentActivity().getWindow().setFlags(8192, 8192);
            } catch (Exception e) {
            }
            this.flagSecureFragment = parentFragment;
        } else if (this.flagSecureFragment == parentFragment) {
            try {
                parentFragment.getParentActivity().getWindow().clearFlags(8192);
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

    private void updateVideoState(MessageObject messageObject, int[] playCount, boolean destroyAtEnd, boolean playWhenReady, int playbackState) {
        if (this.videoPlayer != null) {
            if (playbackState == 4 || playbackState == 1) {
                try {
                    this.baseActivity.getWindow().clearFlags(128);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else {
                try {
                    this.baseActivity.getWindow().addFlags(128);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            if (playbackState == 3) {
                this.playerWasReady = true;
                if (this.playingMessageObject != null && (this.playingMessageObject.isVideo() || this.playingMessageObject.isRoundVideo())) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(messageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
                this.currentAspectRatioFrameLayoutReady = true;
            } else if (playbackState == 2) {
                if (playWhenReady && this.playingMessageObject != null) {
                    if (!this.playingMessageObject.isVideo() && !this.playingMessageObject.isRoundVideo()) {
                        return;
                    }
                    if (this.playerWasReady) {
                        this.setLoadingRunnable.run();
                    } else {
                        AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000);
                    }
                }
            } else if (!this.videoPlayer.isPlaying() || playbackState != 4) {
            } else {
                if (!this.playingMessageObject.isVideo() || destroyAtEnd || (playCount != null && playCount[0] >= 4)) {
                    cleanupPlayer(true, true, true, false);
                    return;
                }
                this.videoPlayer.seekTo(0);
                if (playCount != null) {
                    playCount[0] = playCount[0] + 1;
                }
            }
        }
    }

    public void injectVideoPlayer(VideoPlayer player, final MessageObject messageObject) {
        if (player != null && messageObject != null) {
            FileLoader.getInstance(messageObject.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
            this.playerWasReady = false;
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.videoPlayer = player;
            this.playingMessageObject = messageObject;
            this.videoPlayer.setDelegate(new VideoPlayerDelegate(null, true) {
                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    MediaController.this.updateVideoState(messageObject, null, true, playWhenReady, playbackState);
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
                    MediaController.this.currentAspectRatioFrameLayoutRatio = height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height);
                    if (MediaController.this.currentAspectRatioFrameLayout != null) {
                        MediaController.this.currentAspectRatioFrameLayout.setAspectRatio(MediaController.this.currentAspectRatioFrameLayoutRatio, MediaController.this.currentAspectRatioFrameLayoutRotation);
                    }
                }

                public void onRenderedFirstFrame() {
                    if (MediaController.this.currentAspectRatioFrameLayout != null && !MediaController.this.currentAspectRatioFrameLayout.isDrawingReady()) {
                        MediaController.this.isDrawingWasReady = true;
                        MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                        MediaController.this.currentTextureViewContainer.setTag(Integer.valueOf(1));
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
                    } else if (MediaController.this.pipSwitchingState == 1) {
                        if (MediaController.this.baseActivity != null) {
                            if (MediaController.this.pipRoundVideoView == null) {
                                try {
                                    MediaController.this.pipRoundVideoView = new PipRoundVideoView();
                                    MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new MediaController$5$$Lambda$0(this));
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
                    } else if (!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isInjectingVideoPlayer()) {
                        return false;
                    } else {
                        PhotoViewer.getInstance().injectVideoPlayerSurface(surfaceTexture);
                        return true;
                    }
                }

                /* Access modifiers changed, original: final|synthetic */
                public final /* synthetic */ void lambda$onSurfaceDestroyed$0$MediaController$5() {
                    MediaController.this.cleanupPlayer(true, true);
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }
            });
            this.currentAspectRatioFrameLayoutReady = false;
            if (this.currentTextureView != null) {
                this.videoPlayer.setTextureView(this.currentTextureView);
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
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidStart, messageObject);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:144:0x04cd A:{SYNTHETIC, Splitter:B:144:0x04cd} */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0317  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x05e4  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0343  */
    public boolean playMessage(org.telegram.messenger.MessageObject r30) {
        /*
        r29 = this;
        if (r30 != 0) goto L_0x0005;
    L_0x0002:
        r24 = 0;
    L_0x0004:
        return r24;
    L_0x0005:
        r0 = r29;
        r0 = r0.audioPlayer;
        r24 = r0;
        if (r24 != 0) goto L_0x0015;
    L_0x000d:
        r0 = r29;
        r0 = r0.videoPlayer;
        r24 = r0;
        if (r24 == 0) goto L_0x003a;
    L_0x0015:
        r24 = r29.isSamePlayingMessage(r30);
        if (r24 == 0) goto L_0x003a;
    L_0x001b:
        r0 = r29;
        r0 = r0.isPaused;
        r24 = r0;
        if (r24 == 0) goto L_0x0026;
    L_0x0023:
        r29.resumeAudio(r30);
    L_0x0026:
        r24 = org.telegram.messenger.SharedConfig.raiseToSpeak;
        if (r24 != 0) goto L_0x0037;
    L_0x002a:
        r0 = r29;
        r0 = r0.raiseChat;
        r24 = r0;
        r0 = r29;
        r1 = r24;
        r0.startRaiseToEarSensors(r1);
    L_0x0037:
        r24 = 1;
        goto L_0x0004;
    L_0x003a:
        r24 = r30.isOut();
        if (r24 != 0) goto L_0x0057;
    L_0x0040:
        r24 = r30.isContentUnread();
        if (r24 == 0) goto L_0x0057;
    L_0x0046:
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r24 = org.telegram.messenger.MessagesController.getInstance(r24);
        r0 = r24;
        r1 = r30;
        r0.markMessageContentAsRead(r1);
    L_0x0057:
        r0 = r29;
        r0 = r0.playMusicAgain;
        r24 = r0;
        if (r24 != 0) goto L_0x01a6;
    L_0x005f:
        r18 = 1;
    L_0x0061:
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        if (r24 == 0) goto L_0x007c;
    L_0x0069:
        r18 = 0;
        r0 = r29;
        r0 = r0.playMusicAgain;
        r24 = r0;
        if (r24 != 0) goto L_0x007c;
    L_0x0073:
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        r24.resetPlayingProgress();
    L_0x007c:
        r24 = 0;
        r0 = r29;
        r1 = r18;
        r2 = r24;
        r0.cleanupPlayer(r1, r2);
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.playMusicAgain = r0;
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.seekToProgressPending = r0;
        r15 = 0;
        r14 = 0;
        r0 = r30;
        r0 = r0.messageOwner;
        r24 = r0;
        r0 = r24;
        r0 = r0.attachPath;
        r24 = r0;
        if (r24 == 0) goto L_0x00d3;
    L_0x00a7:
        r0 = r30;
        r0 = r0.messageOwner;
        r24 = r0;
        r0 = r24;
        r0 = r0.attachPath;
        r24 = r0;
        r24 = r24.length();
        if (r24 <= 0) goto L_0x00d3;
    L_0x00b9:
        r15 = new java.io.File;
        r0 = r30;
        r0 = r0.messageOwner;
        r24 = r0;
        r0 = r24;
        r0 = r0.attachPath;
        r24 = r0;
        r0 = r24;
        r15.<init>(r0);
        r14 = r15.exists();
        if (r14 != 0) goto L_0x00d3;
    L_0x00d2:
        r15 = 0;
    L_0x00d3:
        if (r15 == 0) goto L_0x01aa;
    L_0x00d5:
        r6 = r15;
    L_0x00d6:
        r24 = org.telegram.messenger.SharedConfig.streamMedia;
        if (r24 == 0) goto L_0x01b6;
    L_0x00da:
        r24 = r30.isMusic();
        if (r24 != 0) goto L_0x00f2;
    L_0x00e0:
        r24 = r30.isRoundVideo();
        if (r24 != 0) goto L_0x00f2;
    L_0x00e6:
        r24 = r30.isVideo();
        if (r24 == 0) goto L_0x01b6;
    L_0x00ec:
        r24 = r30.canStreamVideo();
        if (r24 == 0) goto L_0x01b6;
    L_0x00f2:
        r24 = r30.getDialogId();
        r0 = r24;
        r0 = (int) r0;
        r24 = r0;
        if (r24 == 0) goto L_0x01b6;
    L_0x00fd:
        r7 = 1;
    L_0x00fe:
        if (r6 == 0) goto L_0x01d7;
    L_0x0100:
        if (r6 == r15) goto L_0x01d7;
    L_0x0102:
        r14 = r6.exists();
        if (r14 != 0) goto L_0x01d7;
    L_0x0108:
        if (r7 != 0) goto L_0x01d7;
    L_0x010a:
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r24 = org.telegram.messenger.FileLoader.getInstance(r24);
        r25 = r30.getDocument();
        r26 = 0;
        r27 = 0;
        r0 = r24;
        r1 = r25;
        r2 = r30;
        r3 = r26;
        r4 = r27;
        r0.loadFile(r1, r2, r3, r4);
        r24 = 1;
        r0 = r24;
        r1 = r29;
        r1.downloadingCurrentMessage = r0;
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.isPaused = r0;
        r24 = 0;
        r0 = r24;
        r2 = r29;
        r2.lastProgress = r0;
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.audioInfo = r0;
        r0 = r30;
        r1 = r29;
        r1.playingMessageObject = r0;
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        r24 = r24.isMusic();
        if (r24 == 0) goto L_0x01be;
    L_0x015b:
        r16 = new android.content.Intent;
        r24 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r25 = org.telegram.messenger.MusicPlayerService.class;
        r0 = r16;
        r1 = r24;
        r2 = r25;
        r0.<init>(r1, r2);
        r24 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x01b9 }
        r0 = r24;
        r1 = r16;
        r0.startService(r1);	 Catch:{ Throwable -> 0x01b9 }
    L_0x0173:
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        r0 = r24;
        r0 = r0.currentAccount;
        r24 = r0;
        r24 = org.telegram.messenger.NotificationCenter.getInstance(r24);
        r25 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r26 = 1;
        r0 = r26;
        r0 = new java.lang.Object[r0];
        r26 = r0;
        r27 = 0;
        r0 = r29;
        r0 = r0.playingMessageObject;
        r28 = r0;
        r28 = r28.getId();
        r28 = java.lang.Integer.valueOf(r28);
        r26[r27] = r28;
        r24.postNotificationName(r25, r26);
        r24 = 1;
        goto L_0x0004;
    L_0x01a6:
        r18 = 0;
        goto L_0x0061;
    L_0x01aa:
        r0 = r30;
        r0 = r0.messageOwner;
        r24 = r0;
        r6 = org.telegram.messenger.FileLoader.getPathToMessage(r24);
        goto L_0x00d6;
    L_0x01b6:
        r7 = 0;
        goto L_0x00fe;
    L_0x01b9:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
        goto L_0x0173;
    L_0x01be:
        r16 = new android.content.Intent;
        r24 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r25 = org.telegram.messenger.MusicPlayerService.class;
        r0 = r16;
        r1 = r24;
        r2 = r25;
        r0.<init>(r1, r2);
        r24 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = r24;
        r1 = r16;
        r0.stopService(r1);
        goto L_0x0173;
    L_0x01d7:
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.downloadingCurrentMessage = r0;
        r24 = r30.isMusic();
        if (r24 == 0) goto L_0x0493;
    L_0x01e5:
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r0 = r29;
        r1 = r24;
        r0.checkIsNextMusicFileDownloaded(r1);
    L_0x01f2:
        r0 = r29;
        r0 = r0.currentAspectRatioFrameLayout;
        r24 = r0;
        if (r24 == 0) goto L_0x020d;
    L_0x01fa:
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.isDrawingWasReady = r0;
        r0 = r29;
        r0 = r0.currentAspectRatioFrameLayout;
        r24 = r0;
        r25 = 0;
        r24.setDrawingReady(r25);
    L_0x020d:
        r17 = r30.isVideo();
        r24 = r30.isRoundVideo();
        if (r24 != 0) goto L_0x0219;
    L_0x0217:
        if (r17 == 0) goto L_0x05f1;
    L_0x0219:
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r24 = org.telegram.messenger.FileLoader.getInstance(r24);
        r25 = r30.getDocument();
        r26 = 1;
        r24.setLoadingVideoForPlayer(r25, r26);
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.playerWasReady = r0;
        if (r17 == 0) goto L_0x0257;
    L_0x0236:
        r0 = r30;
        r0 = r0.messageOwner;
        r24 = r0;
        r0 = r24;
        r0 = r0.to_id;
        r24 = r0;
        r0 = r24;
        r0 = r0.channel_id;
        r24 = r0;
        if (r24 != 0) goto L_0x04a2;
    L_0x024a:
        r0 = r30;
        r0 = r0.audioProgress;
        r24 = r0;
        r25 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r24 = (r24 > r25 ? 1 : (r24 == r25 ? 0 : -1));
        if (r24 > 0) goto L_0x04a2;
    L_0x0257:
        r8 = 1;
    L_0x0258:
        if (r17 == 0) goto L_0x04a5;
    L_0x025a:
        r24 = r30.getDuration();
        r25 = 30;
        r0 = r24;
        r1 = r25;
        if (r0 > r1) goto L_0x04a5;
    L_0x0266:
        r24 = 1;
        r0 = r24;
        r0 = new int[r0];
        r20 = r0;
        r24 = 0;
        r25 = 1;
        r20[r24] = r25;
    L_0x0274:
        r0 = r29;
        r0 = r0.playlist;
        r24 = r0;
        r24.clear();
        r0 = r29;
        r0 = r0.shuffledPlaylist;
        r24 = r0;
        r24.clear();
        r24 = new org.telegram.ui.Components.VideoPlayer;
        r24.<init>();
        r0 = r24;
        r1 = r29;
        r1.videoPlayer = r0;
        r0 = r29;
        r0 = r0.videoPlayer;
        r24 = r0;
        r25 = new org.telegram.messenger.MediaController$6;
        r0 = r25;
        r1 = r29;
        r2 = r30;
        r3 = r20;
        r0.<init>(r2, r3, r8);
        r24.setDelegate(r25);
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.currentAspectRatioFrameLayoutReady = r0;
        r0 = r29;
        r0 = r0.pipRoundVideoView;
        r24 = r0;
        if (r24 != 0) goto L_0x02cf;
    L_0x02b7:
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r24 = org.telegram.messenger.MessagesController.getInstance(r24);
        r26 = r30.getDialogId();
        r0 = r24;
        r1 = r26;
        r24 = r0.isDialogVisible(r1);
        if (r24 != 0) goto L_0x04b4;
    L_0x02cf:
        r0 = r29;
        r0 = r0.pipRoundVideoView;
        r24 = r0;
        if (r24 != 0) goto L_0x02fa;
    L_0x02d7:
        r24 = new org.telegram.ui.Components.PipRoundVideoView;	 Catch:{ Exception -> 0x04a9 }
        r24.<init>();	 Catch:{ Exception -> 0x04a9 }
        r0 = r24;
        r1 = r29;
        r1.pipRoundVideoView = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = r29;
        r0 = r0.pipRoundVideoView;	 Catch:{ Exception -> 0x04a9 }
        r24 = r0;
        r0 = r29;
        r0 = r0.baseActivity;	 Catch:{ Exception -> 0x04a9 }
        r25 = r0;
        r26 = new org.telegram.messenger.MediaController$$Lambda$9;	 Catch:{ Exception -> 0x04a9 }
        r0 = r26;
        r1 = r29;
        r0.<init>(r1);	 Catch:{ Exception -> 0x04a9 }
        r24.show(r25, r26);	 Catch:{ Exception -> 0x04a9 }
    L_0x02fa:
        r0 = r29;
        r0 = r0.pipRoundVideoView;
        r24 = r0;
        if (r24 == 0) goto L_0x0315;
    L_0x0302:
        r0 = r29;
        r0 = r0.videoPlayer;
        r24 = r0;
        r0 = r29;
        r0 = r0.pipRoundVideoView;
        r25 = r0;
        r25 = r25.getTextureView();
        r24.setTextureView(r25);
    L_0x0315:
        if (r14 == 0) goto L_0x04cd;
    L_0x0317:
        r0 = r30;
        r0 = r0.mediaExists;
        r24 = r0;
        if (r24 != 0) goto L_0x032d;
    L_0x031f:
        if (r6 == r15) goto L_0x032d;
    L_0x0321:
        r24 = new org.telegram.messenger.MediaController$$Lambda$10;
        r0 = r24;
        r1 = r30;
        r0.<init>(r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r24);
    L_0x032d:
        r0 = r29;
        r0 = r0.videoPlayer;
        r24 = r0;
        r25 = android.net.Uri.fromFile(r6);
        r26 = "other";
        r24.preparePlayer(r25, r26);
    L_0x033d:
        r24 = r30.isRoundVideo();
        if (r24 == 0) goto L_0x05e4;
    L_0x0343:
        r0 = r29;
        r0 = r0.videoPlayer;
        r25 = r0;
        r0 = r29;
        r0 = r0.useFrontSpeaker;
        r24 = r0;
        if (r24 == 0) goto L_0x05e0;
    L_0x0351:
        r24 = 0;
    L_0x0353:
        r0 = r25;
        r1 = r24;
        r0.setStreamType(r1);
        r0 = r29;
        r0 = r0.currentPlaybackSpeed;
        r24 = r0;
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r24 = (r24 > r25 ? 1 : (r24 == r25 ? 0 : -1));
        if (r24 <= 0) goto L_0x0375;
    L_0x0366:
        r0 = r29;
        r0 = r0.videoPlayer;
        r24 = r0;
        r0 = r29;
        r0 = r0.currentPlaybackSpeed;
        r25 = r0;
        r24.setPlaybackSpeed(r25);
    L_0x0375:
        r29.checkAudioFocus(r30);
        r29.setPlayerVolume();
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.isPaused = r0;
        r24 = 0;
        r0 = r24;
        r2 = r29;
        r2.lastProgress = r0;
        r0 = r30;
        r1 = r29;
        r1.playingMessageObject = r0;
        r24 = org.telegram.messenger.SharedConfig.raiseToSpeak;
        if (r24 != 0) goto L_0x03a2;
    L_0x0395:
        r0 = r29;
        r0 = r0.raiseChat;
        r24 = r0;
        r0 = r29;
        r1 = r24;
        r0.startRaiseToEarSensors(r1);
    L_0x03a2:
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        r0 = r29;
        r1 = r24;
        r0.startProgressTimer(r1);
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r24 = org.telegram.messenger.NotificationCenter.getInstance(r24);
        r25 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart;
        r26 = 1;
        r0 = r26;
        r0 = new java.lang.Object[r0];
        r26 = r0;
        r27 = 0;
        r26[r27] = r30;
        r24.postNotificationName(r25, r26);
        r0 = r29;
        r0 = r0.videoPlayer;
        r24 = r0;
        if (r24 == 0) goto L_0x0898;
    L_0x03d2:
        r0 = r29;
        r0 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r0 = r24;
        r0 = r0.audioProgress;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r25 = 0;
        r24 = (r24 > r25 ? 1 : (r24 == r25 ? 0 : -1));
        if (r24 == 0) goto L_0x045a;
    L_0x03e4:
        r0 = r29;
        r0 = r0.videoPlayer;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r10 = r24.getDuration();	 Catch:{ Exception -> 0x0843 }
        r24 = -NUM; // 0xNUM float:1.4E-45 double:-4.9E-324;
        r24 = (r10 > r24 ? 1 : (r10 == r24 ? 0 : -1));
        if (r24 != 0) goto L_0x040a;
    L_0x03f7:
        r0 = r29;
        r0 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r24 = r24.getDuration();	 Catch:{ Exception -> 0x0843 }
        r0 = r24;
        r0 = (long) r0;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r26 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r10 = r24 * r26;
    L_0x040a:
        r0 = (float) r10;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r0 = r29;
        r0 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0843 }
        r25 = r0;
        r0 = r25;
        r0 = r0.audioProgress;	 Catch:{ Exception -> 0x0843 }
        r25 = r0;
        r24 = r24 * r25;
        r0 = r24;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0843 }
        r22 = r0;
        r0 = r29;
        r0 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r0 = r24;
        r0 = r0.audioProgressMs;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        if (r24 == 0) goto L_0x0448;
    L_0x042e:
        r0 = r29;
        r0 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r0 = r24;
        r0 = r0.audioProgressMs;	 Catch:{ Exception -> 0x0843 }
        r22 = r0;
        r0 = r29;
        r0 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r25 = 0;
        r0 = r25;
        r1 = r24;
        r1.audioProgressMs = r0;	 Catch:{ Exception -> 0x0843 }
    L_0x0448:
        r0 = r29;
        r0 = r0.videoPlayer;	 Catch:{ Exception -> 0x0843 }
        r24 = r0;
        r0 = r22;
        r0 = (long) r0;	 Catch:{ Exception -> 0x0843 }
        r26 = r0;
        r0 = r24;
        r1 = r26;
        r0.seekTo(r1);	 Catch:{ Exception -> 0x0843 }
    L_0x045a:
        r0 = r29;
        r0 = r0.videoPlayer;
        r24 = r0;
        r24.play();
    L_0x0463:
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        if (r24 == 0) goto L_0x094a;
    L_0x046b:
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        r24 = r24.isMusic();
        if (r24 == 0) goto L_0x094a;
    L_0x0477:
        r16 = new android.content.Intent;
        r24 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r25 = org.telegram.messenger.MusicPlayerService.class;
        r0 = r16;
        r1 = r24;
        r2 = r25;
        r0.<init>(r1, r2);
        r24 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0944 }
        r0 = r24;
        r1 = r16;
        r0.startService(r1);	 Catch:{ Throwable -> 0x0944 }
    L_0x048f:
        r24 = 1;
        goto L_0x0004;
    L_0x0493:
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r0 = r29;
        r1 = r24;
        r0.checkIsNextVoiceFileDownloaded(r1);
        goto L_0x01f2;
    L_0x04a2:
        r8 = 0;
        goto L_0x0258;
    L_0x04a5:
        r20 = 0;
        goto L_0x0274;
    L_0x04a9:
        r12 = move-exception;
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.pipRoundVideoView = r0;
        goto L_0x02fa;
    L_0x04b4:
        r0 = r29;
        r0 = r0.currentTextureView;
        r24 = r0;
        if (r24 == 0) goto L_0x0315;
    L_0x04bc:
        r0 = r29;
        r0 = r0.videoPlayer;
        r24 = r0;
        r0 = r29;
        r0 = r0.currentTextureView;
        r25 = r0;
        r24.setTextureView(r25);
        goto L_0x0315;
    L_0x04cd:
        r0 = r30;
        r0 = r0.currentAccount;	 Catch:{ Exception -> 0x05d1 }
        r24 = r0;
        r24 = org.telegram.messenger.FileLoader.getInstance(r24);	 Catch:{ Exception -> 0x05d1 }
        r0 = r24;
        r1 = r30;
        r21 = r0.getFileReference(r1);	 Catch:{ Exception -> 0x05d1 }
        r9 = r30.getDocument();	 Catch:{ Exception -> 0x05d1 }
        r24 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x05d1 }
        r24.<init>();	 Catch:{ Exception -> 0x05d1 }
        r25 = "?account=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r0 = r30;
        r0 = r0.currentAccount;	 Catch:{ Exception -> 0x05d1 }
        r25 = r0;
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r25 = "&id=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r0 = r9.id;	 Catch:{ Exception -> 0x05d1 }
        r26 = r0;
        r0 = r24;
        r1 = r26;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x05d1 }
        r25 = "&hash=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r0 = r9.access_hash;	 Catch:{ Exception -> 0x05d1 }
        r26 = r0;
        r0 = r24;
        r1 = r26;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x05d1 }
        r25 = "&dc=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r0 = r9.dc_id;	 Catch:{ Exception -> 0x05d1 }
        r25 = r0;
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r25 = "&size=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r0 = r9.size;	 Catch:{ Exception -> 0x05d1 }
        r25 = r0;
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r25 = "&mime=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r0 = r9.mime_type;	 Catch:{ Exception -> 0x05d1 }
        r25 = r0;
        r26 = "UTF-8";
        r25 = java.net.URLEncoder.encode(r25, r26);	 Catch:{ Exception -> 0x05d1 }
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r25 = "&rid=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r0 = r24;
        r1 = r21;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x05d1 }
        r25 = "&name=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r25 = org.telegram.messenger.FileLoader.getDocumentFileName(r9);	 Catch:{ Exception -> 0x05d1 }
        r26 = "UTF-8";
        r25 = java.net.URLEncoder.encode(r25, r26);	 Catch:{ Exception -> 0x05d1 }
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r25 = "&reference=";
        r25 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r0 = r9.file_reference;	 Catch:{ Exception -> 0x05d1 }
        r24 = r0;
        if (r24 == 0) goto L_0x05d7;
    L_0x0585:
        r0 = r9.file_reference;	 Catch:{ Exception -> 0x05d1 }
        r24 = r0;
    L_0x0589:
        r24 = org.telegram.messenger.Utilities.bytesToHex(r24);	 Catch:{ Exception -> 0x05d1 }
        r0 = r25;
        r1 = r24;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x05d1 }
        r19 = r24.toString();	 Catch:{ Exception -> 0x05d1 }
        r24 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x05d1 }
        r24.<init>();	 Catch:{ Exception -> 0x05d1 }
        r25 = "tg://";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r25 = r30.getFileName();	 Catch:{ Exception -> 0x05d1 }
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x05d1 }
        r0 = r24;
        r1 = r19;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x05d1 }
        r24 = r24.toString();	 Catch:{ Exception -> 0x05d1 }
        r23 = android.net.Uri.parse(r24);	 Catch:{ Exception -> 0x05d1 }
        r0 = r29;
        r0 = r0.videoPlayer;	 Catch:{ Exception -> 0x05d1 }
        r24 = r0;
        r25 = "other";
        r0 = r24;
        r1 = r23;
        r2 = r25;
        r0.preparePlayer(r1, r2);	 Catch:{ Exception -> 0x05d1 }
        goto L_0x033d;
    L_0x05d1:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
        goto L_0x033d;
    L_0x05d7:
        r24 = 0;
        r0 = r24;
        r0 = new byte[r0];	 Catch:{ Exception -> 0x05d1 }
        r24 = r0;
        goto L_0x0589;
    L_0x05e0:
        r24 = 3;
        goto L_0x0353;
    L_0x05e4:
        r0 = r29;
        r0 = r0.videoPlayer;
        r24 = r0;
        r25 = 3;
        r24.setStreamType(r25);
        goto L_0x0375;
    L_0x05f1:
        r0 = r29;
        r0 = r0.pipRoundVideoView;
        r24 = r0;
        if (r24 == 0) goto L_0x060c;
    L_0x05f9:
        r0 = r29;
        r0 = r0.pipRoundVideoView;
        r24 = r0;
        r25 = 1;
        r24.close(r25);
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.pipRoundVideoView = r0;
    L_0x060c:
        r24 = new org.telegram.ui.Components.VideoPlayer;	 Catch:{ Exception -> 0x06b0 }
        r24.<init>();	 Catch:{ Exception -> 0x06b0 }
        r0 = r24;
        r1 = r29;
        r1.audioPlayer = r0;	 Catch:{ Exception -> 0x06b0 }
        r0 = r29;
        r0 = r0.audioPlayer;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        r25 = new org.telegram.messenger.MediaController$7;	 Catch:{ Exception -> 0x06b0 }
        r0 = r25;
        r1 = r29;
        r2 = r30;
        r0.<init>(r2);	 Catch:{ Exception -> 0x06b0 }
        r24.setDelegate(r25);	 Catch:{ Exception -> 0x06b0 }
        if (r14 == 0) goto L_0x071c;
    L_0x062d:
        r0 = r30;
        r0 = r0.mediaExists;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        if (r24 != 0) goto L_0x0643;
    L_0x0635:
        if (r6 == r15) goto L_0x0643;
    L_0x0637:
        r24 = new org.telegram.messenger.MediaController$$Lambda$11;	 Catch:{ Exception -> 0x06b0 }
        r0 = r24;
        r1 = r30;
        r0.<init>(r1);	 Catch:{ Exception -> 0x06b0 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r24);	 Catch:{ Exception -> 0x06b0 }
    L_0x0643:
        r0 = r29;
        r0 = r0.audioPlayer;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        r25 = android.net.Uri.fromFile(r6);	 Catch:{ Exception -> 0x06b0 }
        r26 = "other";
        r24.preparePlayer(r25, r26);	 Catch:{ Exception -> 0x06b0 }
    L_0x0653:
        r24 = r30.isVoice();	 Catch:{ Exception -> 0x06b0 }
        if (r24 == 0) goto L_0x0829;
    L_0x0659:
        r0 = r29;
        r0 = r0.currentPlaybackSpeed;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r24 = (r24 > r25 ? 1 : (r24 == r25 ? 0 : -1));
        if (r24 <= 0) goto L_0x0674;
    L_0x0665:
        r0 = r29;
        r0 = r0.audioPlayer;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        r0 = r29;
        r0 = r0.currentPlaybackSpeed;	 Catch:{ Exception -> 0x06b0 }
        r25 = r0;
        r24.setPlaybackSpeed(r25);	 Catch:{ Exception -> 0x06b0 }
    L_0x0674:
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.audioInfo = r0;	 Catch:{ Exception -> 0x06b0 }
        r0 = r29;
        r0 = r0.playlist;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        r24.clear();	 Catch:{ Exception -> 0x06b0 }
        r0 = r29;
        r0 = r0.shuffledPlaylist;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        r24.clear();	 Catch:{ Exception -> 0x06b0 }
    L_0x068e:
        r0 = r29;
        r0 = r0.audioPlayer;	 Catch:{ Exception -> 0x06b0 }
        r25 = r0;
        r0 = r29;
        r0 = r0.useFrontSpeaker;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        if (r24 == 0) goto L_0x083b;
    L_0x069c:
        r24 = 0;
    L_0x069e:
        r0 = r25;
        r1 = r24;
        r0.setStreamType(r1);	 Catch:{ Exception -> 0x06b0 }
        r0 = r29;
        r0 = r0.audioPlayer;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        r24.play();	 Catch:{ Exception -> 0x06b0 }
        goto L_0x0375;
    L_0x06b0:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r25 = org.telegram.messenger.NotificationCenter.getInstance(r24);
        r26 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r24 = 1;
        r0 = r24;
        r0 = new java.lang.Object[r0];
        r27 = r0;
        r28 = 0;
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        if (r24 == 0) goto L_0x083f;
    L_0x06d2:
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        r24 = r24.getId();
    L_0x06dc:
        r24 = java.lang.Integer.valueOf(r24);
        r27[r28] = r24;
        r25.postNotificationName(r26, r27);
        r0 = r29;
        r0 = r0.audioPlayer;
        r24 = r0;
        if (r24 == 0) goto L_0x0718;
    L_0x06ed:
        r0 = r29;
        r0 = r0.audioPlayer;
        r24 = r0;
        r25 = 1;
        r24.releasePlayer(r25);
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.audioPlayer = r0;
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.isPaused = r0;
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.playingMessageObject = r0;
        r24 = 0;
        r0 = r24;
        r1 = r29;
        r1.downloadingCurrentMessage = r0;
    L_0x0718:
        r24 = 0;
        goto L_0x0004;
    L_0x071c:
        r0 = r30;
        r0 = r0.currentAccount;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        r24 = org.telegram.messenger.FileLoader.getInstance(r24);	 Catch:{ Exception -> 0x06b0 }
        r0 = r24;
        r1 = r30;
        r21 = r0.getFileReference(r1);	 Catch:{ Exception -> 0x06b0 }
        r9 = r30.getDocument();	 Catch:{ Exception -> 0x06b0 }
        r24 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06b0 }
        r24.<init>();	 Catch:{ Exception -> 0x06b0 }
        r25 = "?account=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r0 = r30;
        r0 = r0.currentAccount;	 Catch:{ Exception -> 0x06b0 }
        r25 = r0;
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r25 = "&id=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r0 = r9.id;	 Catch:{ Exception -> 0x06b0 }
        r26 = r0;
        r0 = r24;
        r1 = r26;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x06b0 }
        r25 = "&hash=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r0 = r9.access_hash;	 Catch:{ Exception -> 0x06b0 }
        r26 = r0;
        r0 = r24;
        r1 = r26;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x06b0 }
        r25 = "&dc=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r0 = r9.dc_id;	 Catch:{ Exception -> 0x06b0 }
        r25 = r0;
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r25 = "&size=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r0 = r9.size;	 Catch:{ Exception -> 0x06b0 }
        r25 = r0;
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r25 = "&mime=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r0 = r9.mime_type;	 Catch:{ Exception -> 0x06b0 }
        r25 = r0;
        r26 = "UTF-8";
        r25 = java.net.URLEncoder.encode(r25, r26);	 Catch:{ Exception -> 0x06b0 }
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r25 = "&rid=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r0 = r24;
        r1 = r21;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x06b0 }
        r25 = "&name=";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r25 = org.telegram.messenger.FileLoader.getDocumentFileName(r9);	 Catch:{ Exception -> 0x06b0 }
        r26 = "UTF-8";
        r25 = java.net.URLEncoder.encode(r25, r26);	 Catch:{ Exception -> 0x06b0 }
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r25 = "&reference=";
        r25 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r0 = r9.file_reference;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        if (r24 == 0) goto L_0x0820;
    L_0x07d4:
        r0 = r9.file_reference;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
    L_0x07d8:
        r24 = org.telegram.messenger.Utilities.bytesToHex(r24);	 Catch:{ Exception -> 0x06b0 }
        r0 = r25;
        r1 = r24;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x06b0 }
        r19 = r24.toString();	 Catch:{ Exception -> 0x06b0 }
        r24 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06b0 }
        r24.<init>();	 Catch:{ Exception -> 0x06b0 }
        r25 = "tg://";
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r25 = r30.getFileName();	 Catch:{ Exception -> 0x06b0 }
        r24 = r24.append(r25);	 Catch:{ Exception -> 0x06b0 }
        r0 = r24;
        r1 = r19;
        r24 = r0.append(r1);	 Catch:{ Exception -> 0x06b0 }
        r24 = r24.toString();	 Catch:{ Exception -> 0x06b0 }
        r23 = android.net.Uri.parse(r24);	 Catch:{ Exception -> 0x06b0 }
        r0 = r29;
        r0 = r0.audioPlayer;	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        r25 = "other";
        r0 = r24;
        r1 = r23;
        r2 = r25;
        r0.preparePlayer(r1, r2);	 Catch:{ Exception -> 0x06b0 }
        goto L_0x0653;
    L_0x0820:
        r24 = 0;
        r0 = r24;
        r0 = new byte[r0];	 Catch:{ Exception -> 0x06b0 }
        r24 = r0;
        goto L_0x07d8;
    L_0x0829:
        r24 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r6);	 Catch:{ Exception -> 0x0835 }
        r0 = r24;
        r1 = r29;
        r1.audioInfo = r0;	 Catch:{ Exception -> 0x0835 }
        goto L_0x068e;
    L_0x0835:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);	 Catch:{ Exception -> 0x06b0 }
        goto L_0x068e;
    L_0x083b:
        r24 = 3;
        goto L_0x069e;
    L_0x083f:
        r24 = 0;
        goto L_0x06dc;
    L_0x0843:
        r13 = move-exception;
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        r25 = 0;
        r0 = r25;
        r1 = r24;
        r1.audioProgress = r0;
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        r25 = 0;
        r0 = r25;
        r1 = r24;
        r1.audioProgressSec = r0;
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r24 = org.telegram.messenger.NotificationCenter.getInstance(r24);
        r25 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r26 = 2;
        r0 = r26;
        r0 = new java.lang.Object[r0];
        r26 = r0;
        r27 = 0;
        r0 = r29;
        r0 = r0.playingMessageObject;
        r28 = r0;
        r28 = r28.getId();
        r28 = java.lang.Integer.valueOf(r28);
        r26[r27] = r28;
        r27 = 1;
        r28 = 0;
        r28 = java.lang.Integer.valueOf(r28);
        r26[r27] = r28;
        r24.postNotificationName(r25, r26);
        org.telegram.messenger.FileLog.e(r13);
        goto L_0x045a;
    L_0x0898:
        r0 = r29;
        r0 = r0.audioPlayer;
        r24 = r0;
        if (r24 == 0) goto L_0x0463;
    L_0x08a0:
        r0 = r29;
        r0 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0902 }
        r24 = r0;
        r0 = r24;
        r0 = r0.audioProgress;	 Catch:{ Exception -> 0x0902 }
        r24 = r0;
        r25 = 0;
        r24 = (r24 > r25 ? 1 : (r24 == r25 ? 0 : -1));
        if (r24 == 0) goto L_0x0463;
    L_0x08b2:
        r0 = r29;
        r0 = r0.audioPlayer;	 Catch:{ Exception -> 0x0902 }
        r24 = r0;
        r10 = r24.getDuration();	 Catch:{ Exception -> 0x0902 }
        r24 = -NUM; // 0xNUM float:1.4E-45 double:-4.9E-324;
        r24 = (r10 > r24 ? 1 : (r10 == r24 ? 0 : -1));
        if (r24 != 0) goto L_0x08d8;
    L_0x08c5:
        r0 = r29;
        r0 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0902 }
        r24 = r0;
        r24 = r24.getDuration();	 Catch:{ Exception -> 0x0902 }
        r0 = r24;
        r0 = (long) r0;	 Catch:{ Exception -> 0x0902 }
        r24 = r0;
        r26 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r10 = r24 * r26;
    L_0x08d8:
        r0 = (float) r10;	 Catch:{ Exception -> 0x0902 }
        r24 = r0;
        r0 = r29;
        r0 = r0.playingMessageObject;	 Catch:{ Exception -> 0x0902 }
        r25 = r0;
        r0 = r25;
        r0 = r0.audioProgress;	 Catch:{ Exception -> 0x0902 }
        r25 = r0;
        r24 = r24 * r25;
        r0 = r24;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0902 }
        r22 = r0;
        r0 = r29;
        r0 = r0.audioPlayer;	 Catch:{ Exception -> 0x0902 }
        r24 = r0;
        r0 = r22;
        r0 = (long) r0;	 Catch:{ Exception -> 0x0902 }
        r26 = r0;
        r0 = r24;
        r1 = r26;
        r0.seekTo(r1);	 Catch:{ Exception -> 0x0902 }
        goto L_0x0463;
    L_0x0902:
        r13 = move-exception;
        r0 = r29;
        r0 = r0.playingMessageObject;
        r24 = r0;
        r24.resetPlayingProgress();
        r0 = r30;
        r0 = r0.currentAccount;
        r24 = r0;
        r24 = org.telegram.messenger.NotificationCenter.getInstance(r24);
        r25 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r26 = 2;
        r0 = r26;
        r0 = new java.lang.Object[r0];
        r26 = r0;
        r27 = 0;
        r0 = r29;
        r0 = r0.playingMessageObject;
        r28 = r0;
        r28 = r28.getId();
        r28 = java.lang.Integer.valueOf(r28);
        r26[r27] = r28;
        r27 = 1;
        r28 = 0;
        r28 = java.lang.Integer.valueOf(r28);
        r26[r27] = r28;
        r24.postNotificationName(r25, r26);
        org.telegram.messenger.FileLog.e(r13);
        goto L_0x0463;
    L_0x0944:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
        goto L_0x048f;
    L_0x094a:
        r16 = new android.content.Intent;
        r24 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r25 = org.telegram.messenger.MusicPlayerService.class;
        r0 = r16;
        r1 = r24;
        r2 = r25;
        r0.<init>(r1, r2);
        r24 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = r24;
        r1 = r16;
        r0.stopService(r1);
        goto L_0x048f;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.playMessage(org.telegram.messenger.MessageObject):boolean");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$playMessage$9$MediaController() {
        cleanupPlayer(true, true);
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

    /* renamed from: pauseMessage */
    public boolean lambda$startAudioAgain$5$MediaController(MessageObject messageObject) {
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
        } catch (Exception e) {
            FileLog.e(e);
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
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public boolean isVideoDrawingReady() {
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

    public boolean isPlayingMessageAndReadyToDraw(MessageObject messageObject) {
        return this.isDrawingWasReady && isPlayingMessage(messageObject);
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
            lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
        }
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        MediaController$$Lambda$12 mediaController$$Lambda$12 = new MediaController$$Lambda$12(this, currentAccount, dialog_id, reply_to_msg);
        this.recordStartRunnable = mediaController$$Lambda$12;
        if (paused) {
            j = 500;
        } else {
            j = 50;
        }
        dispatchQueue.postRunnable(mediaController$$Lambda$12, j);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$startRecording$16$MediaController(int currentAccount, long dialog_id, MessageObject reply_to_msg) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$27(this, currentAccount));
            return;
        }
        this.recordingAudio = new TL_document();
        this.recordingAudio.file_reference = new byte[0];
        this.recordingAudio.dc_id = Integer.MIN_VALUE;
        this.recordingAudio.id = (long) SharedConfig.getLastLocalId();
        this.recordingAudio.user_id = UserConfig.getInstance(currentAccount).getClientUserId();
        this.recordingAudio.mime_type = "audio/ogg";
        this.recordingAudio.file_reference = new byte[0];
        SharedConfig.saveConfig();
        this.recordingAudioFile = new File(FileLoader.getDirectory(4), FileLoader.getAttachFileName(this.recordingAudio));
        try {
            if (startRecord(this.recordingAudioFile.getAbsolutePath()) == 0) {
                AndroidUtilities.runOnUIThread(new MediaController$$Lambda$28(this, currentAccount));
                return;
            }
            this.audioRecorder = new AudioRecord(0, 16000, 16, 2, this.recordBufferSize * 10);
            this.recordStartTime = System.currentTimeMillis();
            this.recordTimeCount = 0;
            this.samplesCount = 0;
            this.recordDialogId = dialog_id;
            this.recordingCurrentAccount = currentAccount;
            this.recordReplyingMessageObject = reply_to_msg;
            this.fileBuffer.rewind();
            this.audioRecorder.startRecording();
            this.recordQueue.postRunnable(this.recordRunnable);
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$30(this, currentAccount));
        } catch (Exception e) {
            FileLog.e(e);
            this.recordingAudio = null;
            stopRecord();
            this.recordingAudioFile.delete();
            this.recordingAudioFile = null;
            try {
                this.audioRecorder.release();
                this.audioRecorder = null;
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$29(this, currentAccount));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$12$MediaController(int currentAccount) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$13$MediaController(int currentAccount) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$14$MediaController(int currentAccount) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$15$MediaController(int currentAccount) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStarted, new Object[0]);
    }

    public void generateWaveform(MessageObject messageObject) {
        String id = messageObject.getId() + "_" + messageObject.getDialogId();
        String path = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(id)) {
            this.generatingWaveform.put(id, messageObject);
            Utilities.globalQueue.postRunnable(new MediaController$$Lambda$13(this, path, id));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$generateWaveform$18$MediaController(String path, String id) {
        AndroidUtilities.runOnUIThread(new MediaController$$Lambda$26(this, id, getWaveform(path)));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$17$MediaController(String id, byte[] waveform) {
        MessageObject messageObject1 = (MessageObject) this.generatingWaveform.remove(id);
        if (messageObject1 != null && waveform != null) {
            for (int a = 0; a < messageObject1.getDocument().attributes.size(); a++) {
                DocumentAttribute attribute = (DocumentAttribute) messageObject1.getDocument().attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    attribute.waveform = waveform;
                    attribute.flags |= 4;
                    break;
                }
            }
            messages_Messages messagesRes = new TL_messages_messages();
            messagesRes.messages.add(messageObject1.messageOwner);
            MessagesStorage.getInstance(messageObject1.currentAccount).putMessages(messagesRes, messageObject1.getDialogId(), -1, 0, false);
            new ArrayList().add(messageObject1);
            NotificationCenter.getInstance(messageObject1.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject1.getDialogId()), arrayList);
        }
    }

    private void stopRecordingInternal(int send) {
        if (send != 0) {
            this.fileEncodingQueue.postRunnable(new MediaController$$Lambda$14(this, this.recordingAudio, this.recordingAudioFile, send));
        }
        try {
            if (this.audioRecorder != null) {
                this.audioRecorder.release();
                this.audioRecorder = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.recordingAudio = null;
        this.recordingAudioFile = null;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$stopRecordingInternal$20$MediaController(TL_document audioToSend, File recordingAudioFileToSend, int send) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new MediaController$$Lambda$25(this, audioToSend, recordingAudioFileToSend, send));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$19$MediaController(TL_document audioToSend, File recordingAudioFileToSend, int send) {
        audioToSend.date = ConnectionsManager.getInstance(this.recordingCurrentAccount).getCurrentTime();
        audioToSend.size = (int) recordingAudioFileToSend.length();
        TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
        attributeAudio.voice = true;
        attributeAudio.waveform = getWaveform2(this.recordSamples, this.recordSamples.length);
        if (attributeAudio.waveform != null) {
            attributeAudio.flags |= 4;
        }
        long duration = this.recordTimeCount;
        attributeAudio.duration = (int) (this.recordTimeCount / 1000);
        audioToSend.attributes.add(attributeAudio);
        if (duration > 700) {
            if (send == 1) {
                SendMessagesHelper.getInstance(this.recordingCurrentAccount).sendMessage(audioToSend, null, recordingAudioFileToSend.getAbsolutePath(), this.recordDialogId, this.recordReplyingMessageObject, null, null, null, null, 0, null);
            }
            NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
            int i = NotificationCenter.audioDidSent;
            Object[] objArr = new Object[2];
            if (send != 2) {
                audioToSend = null;
            }
            objArr[0] = audioToSend;
            objArr[1] = send == 2 ? recordingAudioFileToSend.getAbsolutePath() : null;
            instance.postNotificationName(i, objArr);
            return;
        }
        NotificationCenter.getInstance(this.recordingCurrentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Boolean.valueOf(false));
        recordingAudioFileToSend.delete();
    }

    public void stopRecording(int send) {
        if (this.recordStartRunnable != null) {
            this.recordQueue.cancelRunnable(this.recordStartRunnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new MediaController$$Lambda$15(this, send));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$stopRecording$22$MediaController(int send) {
        if (this.audioRecorder != null) {
            try {
                this.sendAfterDone = send;
                this.audioRecorder.stop();
            } catch (Exception e) {
                FileLog.e(e);
                if (this.recordingAudioFile != null) {
                    this.recordingAudioFile.delete();
                }
            }
            if (send == 0) {
                stopRecordingInternal(0);
            }
            try {
                this.feedbackView.performHapticFeedback(3, 2);
            } catch (Exception e2) {
            }
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$24(this, send));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$21$MediaController(int send) {
        int i = 1;
        NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
        int i2 = NotificationCenter.recordStopped;
        Object[] objArr = new Object[1];
        if (send != 2) {
            i = 0;
        }
        objArr[0] = Integer.valueOf(i);
        instance.postNotificationName(i2, objArr);
    }

    public static void saveFile(String fullPath, Context context, int type, String name, String mime) {
        Throwable e;
        if (fullPath != null) {
            File file = null;
            if (!(fullPath == null || fullPath.length() == 0)) {
                file = new File(fullPath);
                if (!file.exists() || AndroidUtilities.isInternalUri(Uri.fromFile(file))) {
                    file = null;
                }
            }
            if (file != null) {
                File sourceFile = file;
                boolean[] cancelled = new boolean[]{false};
                if (sourceFile.exists()) {
                    AlertDialog progressDialog = null;
                    if (!(context == null || type == 0)) {
                        try {
                            AlertDialog progressDialog2 = new AlertDialog(context, 2);
                            try {
                                progressDialog2.setMessage(LocaleController.getString("Loading", NUM));
                                progressDialog2.setCanceledOnTouchOutside(false);
                                progressDialog2.setCancelable(true);
                                progressDialog2.setOnCancelListener(new MediaController$$Lambda$16(cancelled));
                                progressDialog2.show();
                                progressDialog = progressDialog2;
                            } catch (Exception e2) {
                                e = e2;
                                progressDialog = progressDialog2;
                                FileLog.e(e);
                                new Thread(new MediaController$$Lambda$17(type, name, sourceFile, cancelled, progressDialog, mime)).start();
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.e(e);
                            new Thread(new MediaController$$Lambda$17(type, name, sourceFile, cancelled, progressDialog, mime)).start();
                        }
                    }
                    new Thread(new MediaController$$Lambda$17(type, name, sourceFile, cancelled, progressDialog, mime)).start();
                }
            }
        }
    }

    static final /* synthetic */ void lambda$saveFile$26$MediaController(int type, String name, File sourceFile, boolean[] cancelled, AlertDialog finalProgress, String mime) {
        File destFile;
        long a;
        if (type == 0) {
            try {
                destFile = AndroidUtilities.generatePicturePath();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (type == 1) {
            destFile = AndroidUtilities.generateVideoPath();
        } else {
            File dir;
            if (type == 2) {
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            } else {
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            }
            dir.mkdir();
            destFile = new File(dir, name);
            if (destFile.exists()) {
                int idx = name.lastIndexOf(46);
                for (a = null; a < 10; a++) {
                    String newName;
                    if (idx != -1) {
                        newName = name.substring(0, idx) + "(" + (a + 1) + ")" + name.substring(idx);
                    } else {
                        newName = name + "(" + (a + 1) + ")";
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
                    AndroidUtilities.runOnUIThread(new MediaController$$Lambda$22(finalProgress, (int) ((((float) a) / ((float) size)) * 100.0f)));
                }
            }
            if (source != null) {
                try {
                    source.close();
                } catch (Exception e2) {
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (Exception e3) {
                }
            }
        } catch (Exception e4) {
            FileLog.e(e4);
            result = false;
            if (source != null) {
                try {
                    source.close();
                } catch (Exception e5) {
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (Exception e6) {
                }
            }
        } catch (Throwable th) {
            if (source != null) {
                try {
                    source.close();
                } catch (Exception e7) {
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (Exception e8) {
                }
            }
        }
        if (cancelled[0]) {
            destFile.delete();
            result = false;
        }
        if (result) {
            if (type == 2) {
                ((DownloadManager) ApplicationLoader.applicationContext.getSystemService("download")).addCompletedDownload(destFile.getName(), destFile.getName(), false, mime, destFile.getAbsolutePath(), destFile.length(), true);
            } else {
                AndroidUtilities.addMediaToGallery(Uri.fromFile(destFile));
            }
        }
        if (finalProgress != null) {
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$23(finalProgress));
        }
    }

    static final /* synthetic */ void lambda$null$24$MediaController(AlertDialog finalProgress, int progress) {
        try {
            finalProgress.setProgress(progress);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    static final /* synthetic */ void lambda$null$25$MediaController(AlertDialog finalProgress) {
        try {
            finalProgress.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
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
                            } catch (Exception e2) {
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
                } catch (Exception e22) {
                    FileLog.e(e22);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e222) {
                    FileLog.e(e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e2222) {
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
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                    return z;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e22) {
                    FileLog.e(e22);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e222) {
                    FileLog.e(e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e2222) {
                    FileLog.e(e2222);
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
            } catch (Exception e) {
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

    /* JADX WARNING: Removed duplicated region for block: B:57:0x00b7 A:{SYNTHETIC, Splitter:B:57:0x00b7} */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00bc A:{SYNTHETIC, Splitter:B:60:0x00bc} */
    public static java.lang.String copyFileToCache(android.net.Uri r17, java.lang.String r18) {
        /*
        r7 = 0;
        r10 = 0;
        r12 = getFileName(r17);	 Catch:{ Exception -> 0x00cd }
        r9 = org.telegram.messenger.FileLoader.fixFileName(r12);	 Catch:{ Exception -> 0x00cd }
        if (r9 != 0) goto L_0x0029;
    L_0x000c:
        r6 = org.telegram.messenger.SharedConfig.getLastLocalId();	 Catch:{ Exception -> 0x00cd }
        org.telegram.messenger.SharedConfig.saveConfig();	 Catch:{ Exception -> 0x00cd }
        r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x00cd }
        r13 = "%d.%s";
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x00cd }
        r15 = 0;
        r16 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x00cd }
        r14[r15] = r16;	 Catch:{ Exception -> 0x00cd }
        r15 = 1;
        r14[r15] = r18;	 Catch:{ Exception -> 0x00cd }
        r9 = java.lang.String.format(r12, r13, r14);	 Catch:{ Exception -> 0x00cd }
    L_0x0029:
        r4 = new java.io.File;	 Catch:{ Exception -> 0x00cd }
        r12 = 4;
        r12 = org.telegram.messenger.FileLoader.getDirectory(r12);	 Catch:{ Exception -> 0x00cd }
        r13 = "sharing/";
        r4.<init>(r12, r13);	 Catch:{ Exception -> 0x00cd }
        r4.mkdirs();	 Catch:{ Exception -> 0x00cd }
        r5 = new java.io.File;	 Catch:{ Exception -> 0x00cd }
        r5.<init>(r4, r9);	 Catch:{ Exception -> 0x00cd }
        r12 = android.net.Uri.fromFile(r5);	 Catch:{ Exception -> 0x00cd }
        r12 = org.telegram.messenger.AndroidUtilities.isInternalUri(r12);	 Catch:{ Exception -> 0x00cd }
        if (r12 == 0) goto L_0x005e;
    L_0x0048:
        r12 = 0;
        if (r7 == 0) goto L_0x004e;
    L_0x004b:
        r7.close();	 Catch:{ Exception -> 0x0054 }
    L_0x004e:
        if (r10 == 0) goto L_0x0053;
    L_0x0050:
        r10.close();	 Catch:{ Exception -> 0x0059 }
    L_0x0053:
        return r12;
    L_0x0054:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x004e;
    L_0x0059:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x0053;
    L_0x005e:
        r12 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00cd }
        r12 = r12.getContentResolver();	 Catch:{ Exception -> 0x00cd }
        r0 = r17;
        r7 = r12.openInputStream(r0);	 Catch:{ Exception -> 0x00cd }
        r11 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00cd }
        r11.<init>(r5);	 Catch:{ Exception -> 0x00cd }
        r12 = 20480; // 0x5000 float:2.8699E-41 double:1.01185E-319;
        r1 = new byte[r12];	 Catch:{ Exception -> 0x007f, all -> 0x00ca }
    L_0x0073:
        r8 = r7.read(r1);	 Catch:{ Exception -> 0x007f, all -> 0x00ca }
        r12 = -1;
        if (r8 == r12) goto L_0x0090;
    L_0x007a:
        r12 = 0;
        r11.write(r1, r12, r8);	 Catch:{ Exception -> 0x007f, all -> 0x00ca }
        goto L_0x0073;
    L_0x007f:
        r2 = move-exception;
        r10 = r11;
    L_0x0081:
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x00b4 }
        if (r7 == 0) goto L_0x0089;
    L_0x0086:
        r7.close();	 Catch:{ Exception -> 0x00aa }
    L_0x0089:
        if (r10 == 0) goto L_0x008e;
    L_0x008b:
        r10.close();	 Catch:{ Exception -> 0x00af }
    L_0x008e:
        r12 = 0;
        goto L_0x0053;
    L_0x0090:
        r12 = r5.getAbsolutePath();	 Catch:{ Exception -> 0x007f, all -> 0x00ca }
        if (r7 == 0) goto L_0x0099;
    L_0x0096:
        r7.close();	 Catch:{ Exception -> 0x00a0 }
    L_0x0099:
        if (r11 == 0) goto L_0x009e;
    L_0x009b:
        r11.close();	 Catch:{ Exception -> 0x00a5 }
    L_0x009e:
        r10 = r11;
        goto L_0x0053;
    L_0x00a0:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x0099;
    L_0x00a5:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x009e;
    L_0x00aa:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x0089;
    L_0x00af:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x008e;
    L_0x00b4:
        r12 = move-exception;
    L_0x00b5:
        if (r7 == 0) goto L_0x00ba;
    L_0x00b7:
        r7.close();	 Catch:{ Exception -> 0x00c0 }
    L_0x00ba:
        if (r10 == 0) goto L_0x00bf;
    L_0x00bc:
        r10.close();	 Catch:{ Exception -> 0x00c5 }
    L_0x00bf:
        throw r12;
    L_0x00c0:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x00ba;
    L_0x00c5:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x00bf;
    L_0x00ca:
        r12 = move-exception;
        r10 = r11;
        goto L_0x00b5;
    L_0x00cd:
        r2 = move-exception;
        goto L_0x0081;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.copyFileToCache(android.net.Uri, java.lang.String):java.lang.String");
    }

    public static void loadGalleryPhotosAlbums(int guid) {
        Thread thread = new Thread(new MediaController$$Lambda$18(guid));
        thread.setPriority(1);
        thread.start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter:B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e7 A:{Catch:{ Throwable -> 0x036c, all -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter:B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0376 A:{SYNTHETIC, Splitter:B:145:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0386 A:{SYNTHETIC, Splitter:B:152:0x0386} */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0386 A:{SYNTHETIC, Splitter:B:152:0x0386} */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0376 A:{SYNTHETIC, Splitter:B:145:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0386 A:{SYNTHETIC, Splitter:B:152:0x0386} */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0376 A:{SYNTHETIC, Splitter:B:145:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e7 A:{Catch:{ Throwable -> 0x036c, all -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter:B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e7 A:{Catch:{ Throwable -> 0x036c, all -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter:B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0308 A:{SYNTHETIC, Splitter:B:113:0x0308} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x01b3 A:{SYNTHETIC, Splitter:B:61:0x01b3} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e7 A:{Catch:{ Throwable -> 0x036c, all -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter:B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0308 A:{SYNTHETIC, Splitter:B:113:0x0308} */
    static final /* synthetic */ void lambda$loadGalleryPhotosAlbums$28$MediaController(int r47) {
        /*
        r41 = new java.util.ArrayList;
        r41.<init>();
        r45 = new java.util.ArrayList;
        r45.<init>();
        r40 = new android.util.SparseArray;
        r40.<init>();
        r44 = new android.util.SparseArray;
        r44.<init>();
        r24 = 0;
        r18 = 0;
        r16 = 0;
        r30 = 0;
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019f }
        r4.<init>();	 Catch:{ Exception -> 0x019f }
        r12 = android.os.Environment.DIRECTORY_DCIM;	 Catch:{ Exception -> 0x019f }
        r12 = android.os.Environment.getExternalStoragePublicDirectory(r12);	 Catch:{ Exception -> 0x019f }
        r12 = r12.getAbsolutePath();	 Catch:{ Exception -> 0x019f }
        r4 = r4.append(r12);	 Catch:{ Exception -> 0x019f }
        r12 = "/Camera/";
        r4 = r4.append(r12);	 Catch:{ Exception -> 0x019f }
        r30 = r4.toString();	 Catch:{ Exception -> 0x019f }
    L_0x003a:
        r15 = 0;
        r46 = 0;
        r31 = 0;
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x01ad }
        r12 = 23;
        if (r4 < r12) goto L_0x0056;
    L_0x0045:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x01ad }
        r12 = 23;
        if (r4 < r12) goto L_0x0310;
    L_0x004b:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x01ad }
        r12 = "android.permission.READ_EXTERNAL_STORAGE";
        r4 = r4.checkSelfPermission(r12);	 Catch:{ Throwable -> 0x01ad }
        if (r4 != 0) goto L_0x0310;
    L_0x0056:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x01ad }
        r4 = r4.getContentResolver();	 Catch:{ Throwable -> 0x01ad }
        r5 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x01ad }
        r6 = projectionPhotos;	 Catch:{ Throwable -> 0x01ad }
        r7 = 0;
        r8 = 0;
        r9 = "datetaken DESC";
        r31 = android.provider.MediaStore.Images.Media.query(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x01ad }
        if (r31 == 0) goto L_0x0310;
    L_0x006b:
        r4 = "_id";
        r0 = r31;
        r38 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x01ad }
        r4 = "bucket_id";
        r0 = r31;
        r27 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x01ad }
        r4 = "bucket_display_name";
        r0 = r31;
        r29 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x01ad }
        r4 = "_data";
        r0 = r31;
        r32 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x01ad }
        r4 = "datetaken";
        r0 = r31;
        r33 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x01ad }
        r4 = "orientation";
        r0 = r31;
        r43 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x01ad }
        r23 = r16;
        r25 = r24;
    L_0x00a5:
        r4 = r31.moveToNext();	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        if (r4 == 0) goto L_0x030c;
    L_0x00ab:
        r0 = r31;
        r1 = r38;
        r7 = r0.getInt(r1);	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        r0 = r31;
        r1 = r27;
        r6 = r0.getInt(r1);	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        r0 = r31;
        r1 = r29;
        r28 = r0.getString(r1);	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        r10 = r31.getString(r32);	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        r0 = r31;
        r1 = r33;
        r8 = r0.getLong(r1);	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        r0 = r31;
        r1 = r43;
        r11 = r0.getInt(r1);	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        if (r10 == 0) goto L_0x00a5;
    L_0x00d9:
        r4 = r10.length();	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        if (r4 == 0) goto L_0x00a5;
    L_0x00df:
        r5 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        r12 = 0;
        r5.<init>(r6, r7, r8, r10, r11, r12);	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        if (r25 != 0) goto L_0x03e9;
    L_0x00e7:
        r24 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        r4 = 0;
        r12 = "AllPhotos";
        r13 = NUM; // 0x7f0CLASSNAMEbc float:1.8609573E38 double:1.0530974913E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r13);	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        r0 = r24;
        r0.<init>(r4, r12, r5);	 Catch:{ Throwable -> 0x03c5, all -> 0x03b9 }
        r4 = 0;
        r0 = r45;
        r1 = r24;
        r0.add(r4, r1);	 Catch:{ Throwable -> 0x03cc, all -> 0x03c0 }
    L_0x0101:
        if (r23 != 0) goto L_0x03e5;
    L_0x0103:
        r16 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x03cc, all -> 0x03c0 }
        r4 = 0;
        r12 = "AllMedia";
        r13 = NUM; // 0x7f0CLASSNAMEbb float:1.8609571E38 double:1.053097491E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r13);	 Catch:{ Throwable -> 0x03cc, all -> 0x03c0 }
        r0 = r16;
        r0.<init>(r4, r12, r5);	 Catch:{ Throwable -> 0x03cc, all -> 0x03c0 }
        r4 = 0;
        r0 = r41;
        r1 = r16;
        r0.add(r4, r1);	 Catch:{ Throwable -> 0x01ad }
    L_0x011d:
        r0 = r24;
        r0.addPhoto(r5);	 Catch:{ Throwable -> 0x01ad }
        r0 = r16;
        r0.addPhoto(r5);	 Catch:{ Throwable -> 0x01ad }
        r0 = r40;
        r22 = r0.get(r6);	 Catch:{ Throwable -> 0x01ad }
        r22 = (org.telegram.messenger.MediaController.AlbumEntry) r22;	 Catch:{ Throwable -> 0x01ad }
        if (r22 != 0) goto L_0x015b;
    L_0x0131:
        r22 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x01ad }
        r0 = r22;
        r1 = r28;
        r0.<init>(r6, r1, r5);	 Catch:{ Throwable -> 0x01ad }
        r0 = r40;
        r1 = r22;
        r0.put(r6, r1);	 Catch:{ Throwable -> 0x01ad }
        if (r15 != 0) goto L_0x01a5;
    L_0x0143:
        if (r30 == 0) goto L_0x01a5;
    L_0x0145:
        if (r10 == 0) goto L_0x01a5;
    L_0x0147:
        r0 = r30;
        r4 = r10.startsWith(r0);	 Catch:{ Throwable -> 0x01ad }
        if (r4 == 0) goto L_0x01a5;
    L_0x014f:
        r4 = 0;
        r0 = r41;
        r1 = r22;
        r0.add(r4, r1);	 Catch:{ Throwable -> 0x01ad }
        r15 = java.lang.Integer.valueOf(r6);	 Catch:{ Throwable -> 0x01ad }
    L_0x015b:
        r0 = r22;
        r0.addPhoto(r5);	 Catch:{ Throwable -> 0x01ad }
        r0 = r44;
        r22 = r0.get(r6);	 Catch:{ Throwable -> 0x01ad }
        r22 = (org.telegram.messenger.MediaController.AlbumEntry) r22;	 Catch:{ Throwable -> 0x01ad }
        if (r22 != 0) goto L_0x0194;
    L_0x016a:
        r22 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x01ad }
        r0 = r22;
        r1 = r28;
        r0.<init>(r6, r1, r5);	 Catch:{ Throwable -> 0x01ad }
        r0 = r44;
        r1 = r22;
        r0.put(r6, r1);	 Catch:{ Throwable -> 0x01ad }
        if (r46 != 0) goto L_0x02fc;
    L_0x017c:
        if (r30 == 0) goto L_0x02fc;
    L_0x017e:
        if (r10 == 0) goto L_0x02fc;
    L_0x0180:
        r0 = r30;
        r4 = r10.startsWith(r0);	 Catch:{ Throwable -> 0x01ad }
        if (r4 == 0) goto L_0x02fc;
    L_0x0188:
        r4 = 0;
        r0 = r45;
        r1 = r22;
        r0.add(r4, r1);	 Catch:{ Throwable -> 0x01ad }
        r46 = java.lang.Integer.valueOf(r6);	 Catch:{ Throwable -> 0x01ad }
    L_0x0194:
        r0 = r22;
        r0.addPhoto(r5);	 Catch:{ Throwable -> 0x01ad }
        r23 = r16;
        r25 = r24;
        goto L_0x00a5;
    L_0x019f:
        r37 = move-exception;
        org.telegram.messenger.FileLog.e(r37);
        goto L_0x003a;
    L_0x01a5:
        r0 = r41;
        r1 = r22;
        r0.add(r1);	 Catch:{ Throwable -> 0x01ad }
        goto L_0x015b;
    L_0x01ad:
        r37 = move-exception;
    L_0x01ae:
        org.telegram.messenger.FileLog.e(r37);	 Catch:{ all -> 0x0305 }
        if (r31 == 0) goto L_0x03df;
    L_0x01b3:
        r31.close();	 Catch:{ Exception -> 0x0325 }
        r42 = r15;
        r23 = r16;
    L_0x01ba:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r12 = 23;
        if (r4 < r12) goto L_0x01d1;
    L_0x01c0:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r12 = 23;
        if (r4 < r12) goto L_0x03d9;
    L_0x01c6:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r12 = "android.permission.READ_EXTERNAL_STORAGE";
        r4 = r4.checkSelfPermission(r12);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        if (r4 != 0) goto L_0x03d9;
    L_0x01d1:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r12 = r4.getContentResolver();	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r13 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r14 = projectionVideo;	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r15 = 0;
        r16 = 0;
        r17 = "datetaken DESC";
        r31 = android.provider.MediaStore.Images.Media.query(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        if (r31 == 0) goto L_0x03d9;
    L_0x01e7:
        r4 = "_id";
        r0 = r31;
        r38 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r4 = "bucket_id";
        r0 = r31;
        r27 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r4 = "bucket_display_name";
        r0 = r31;
        r29 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r4 = "_data";
        r0 = r31;
        r32 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r4 = "datetaken";
        r0 = r31;
        r33 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r4 = "duration";
        r0 = r31;
        r36 = r0.getColumnIndex(r4);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r26 = r18;
    L_0x021f:
        r4 = r31.moveToNext();	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        if (r4 == 0) goto L_0x033e;
    L_0x0225:
        r0 = r31;
        r1 = r38;
        r7 = r0.getInt(r1);	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r0 = r31;
        r1 = r27;
        r6 = r0.getInt(r1);	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r0 = r31;
        r1 = r29;
        r28 = r0.getString(r1);	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r10 = r31.getString(r32);	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r0 = r31;
        r1 = r33;
        r8 = r0.getLong(r1);	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r0 = r31;
        r1 = r36;
        r34 = r0.getLong(r1);	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        if (r10 == 0) goto L_0x021f;
    L_0x0253:
        r4 = r10.length();	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        if (r4 == 0) goto L_0x021f;
    L_0x0259:
        r5 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r12 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r12 = r34 / r12;
        r0 = (int) r12;	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r19 = r0;
        r20 = 1;
        r13 = r5;
        r14 = r6;
        r15 = r7;
        r16 = r8;
        r18 = r10;
        r13.<init>(r14, r15, r16, r18, r19, r20);	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        if (r26 != 0) goto L_0x03d5;
    L_0x0270:
        r18 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r4 = 0;
        r12 = "AllVideos";
        r13 = NUM; // 0x7f0CLASSNAMEbd float:1.8609575E38 double:1.053097492E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r13);	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r0 = r18;
        r0.<init>(r4, r12, r5);	 Catch:{ Throwable -> 0x03ab, all -> 0x039d }
        r39 = 0;
        if (r23 == 0) goto L_0x0288;
    L_0x0286:
        r39 = r39 + 1;
    L_0x0288:
        if (r24 == 0) goto L_0x028c;
    L_0x028a:
        r39 = r39 + 1;
    L_0x028c:
        r0 = r41;
        r1 = r39;
        r2 = r18;
        r0.add(r1, r2);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
    L_0x0295:
        if (r23 != 0) goto L_0x03d1;
    L_0x0297:
        r16 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r4 = 0;
        r12 = "AllMedia";
        r13 = NUM; // 0x7f0CLASSNAMEbb float:1.8609571E38 double:1.053097491E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r13);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r0 = r16;
        r0.<init>(r4, r12, r5);	 Catch:{ Throwable -> 0x036c, all -> 0x037f }
        r4 = 0;
        r0 = r41;
        r1 = r16;
        r0.add(r4, r1);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
    L_0x02b1:
        r0 = r18;
        r0.addPhoto(r5);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
        r0 = r16;
        r0.addPhoto(r5);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
        r0 = r40;
        r22 = r0.get(r6);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
        r22 = (org.telegram.messenger.MediaController.AlbumEntry) r22;	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
        if (r22 != 0) goto L_0x033b;
    L_0x02c5:
        r22 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
        r0 = r22;
        r1 = r28;
        r0.<init>(r6, r1, r5);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
        r0 = r40;
        r1 = r22;
        r0.put(r6, r1);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
        if (r42 != 0) goto L_0x0334;
    L_0x02d7:
        if (r30 == 0) goto L_0x0334;
    L_0x02d9:
        if (r10 == 0) goto L_0x0334;
    L_0x02db:
        r0 = r30;
        r4 = r10.startsWith(r0);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
        if (r4 == 0) goto L_0x0334;
    L_0x02e3:
        r4 = 0;
        r0 = r41;
        r1 = r22;
        r0.add(r4, r1);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
        r15 = java.lang.Integer.valueOf(r6);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
    L_0x02ef:
        r0 = r22;
        r0.addPhoto(r5);	 Catch:{ Throwable -> 0x03b7 }
        r42 = r15;
        r23 = r16;
        r26 = r18;
        goto L_0x021f;
    L_0x02fc:
        r0 = r45;
        r1 = r22;
        r0.add(r1);	 Catch:{ Throwable -> 0x01ad }
        goto L_0x0194;
    L_0x0305:
        r4 = move-exception;
    L_0x0306:
        if (r31 == 0) goto L_0x030b;
    L_0x0308:
        r31.close();	 Catch:{ Exception -> 0x032f }
    L_0x030b:
        throw r4;
    L_0x030c:
        r16 = r23;
        r24 = r25;
    L_0x0310:
        if (r31 == 0) goto L_0x03df;
    L_0x0312:
        r31.close();	 Catch:{ Exception -> 0x031b }
        r42 = r15;
        r23 = r16;
        goto L_0x01ba;
    L_0x031b:
        r37 = move-exception;
        org.telegram.messenger.FileLog.e(r37);
        r42 = r15;
        r23 = r16;
        goto L_0x01ba;
    L_0x0325:
        r37 = move-exception;
        org.telegram.messenger.FileLog.e(r37);
        r42 = r15;
        r23 = r16;
        goto L_0x01ba;
    L_0x032f:
        r37 = move-exception;
        org.telegram.messenger.FileLog.e(r37);
        goto L_0x030b;
    L_0x0334:
        r0 = r41;
        r1 = r22;
        r0.add(r1);	 Catch:{ Throwable -> 0x03b3, all -> 0x03a5 }
    L_0x033b:
        r15 = r42;
        goto L_0x02ef;
    L_0x033e:
        r15 = r42;
        r16 = r23;
        r18 = r26;
    L_0x0344:
        if (r31 == 0) goto L_0x0349;
    L_0x0346:
        r31.close();	 Catch:{ Exception -> 0x0367 }
    L_0x0349:
        r21 = 0;
    L_0x034b:
        r4 = r41.size();
        r0 = r21;
        if (r0 >= r4) goto L_0x038f;
    L_0x0353:
        r0 = r41;
        r1 = r21;
        r4 = r0.get(r1);
        r4 = (org.telegram.messenger.MediaController.AlbumEntry) r4;
        r4 = r4.photos;
        r12 = org.telegram.messenger.MediaController$$Lambda$21.$instance;
        java.util.Collections.sort(r4, r12);
        r21 = r21 + 1;
        goto L_0x034b;
    L_0x0367:
        r37 = move-exception;
        org.telegram.messenger.FileLog.e(r37);
        goto L_0x0349;
    L_0x036c:
        r37 = move-exception;
        r15 = r42;
        r16 = r23;
    L_0x0371:
        org.telegram.messenger.FileLog.e(r37);	 Catch:{ all -> 0x03a9 }
        if (r31 == 0) goto L_0x0349;
    L_0x0376:
        r31.close();	 Catch:{ Exception -> 0x037a }
        goto L_0x0349;
    L_0x037a:
        r37 = move-exception;
        org.telegram.messenger.FileLog.e(r37);
        goto L_0x0349;
    L_0x037f:
        r4 = move-exception;
        r15 = r42;
        r16 = r23;
    L_0x0384:
        if (r31 == 0) goto L_0x0389;
    L_0x0386:
        r31.close();	 Catch:{ Exception -> 0x038a }
    L_0x0389:
        throw r4;
    L_0x038a:
        r37 = move-exception;
        org.telegram.messenger.FileLog.e(r37);
        goto L_0x0389;
    L_0x038f:
        r19 = 0;
        r12 = r47;
        r13 = r41;
        r14 = r45;
        r17 = r24;
        broadcastNewPhotos(r12, r13, r14, r15, r16, r17, r18, r19);
        return;
    L_0x039d:
        r4 = move-exception;
        r15 = r42;
        r16 = r23;
        r18 = r26;
        goto L_0x0384;
    L_0x03a5:
        r4 = move-exception;
        r15 = r42;
        goto L_0x0384;
    L_0x03a9:
        r4 = move-exception;
        goto L_0x0384;
    L_0x03ab:
        r37 = move-exception;
        r15 = r42;
        r16 = r23;
        r18 = r26;
        goto L_0x0371;
    L_0x03b3:
        r37 = move-exception;
        r15 = r42;
        goto L_0x0371;
    L_0x03b7:
        r37 = move-exception;
        goto L_0x0371;
    L_0x03b9:
        r4 = move-exception;
        r16 = r23;
        r24 = r25;
        goto L_0x0306;
    L_0x03c0:
        r4 = move-exception;
        r16 = r23;
        goto L_0x0306;
    L_0x03c5:
        r37 = move-exception;
        r16 = r23;
        r24 = r25;
        goto L_0x01ae;
    L_0x03cc:
        r37 = move-exception;
        r16 = r23;
        goto L_0x01ae;
    L_0x03d1:
        r16 = r23;
        goto L_0x02b1;
    L_0x03d5:
        r18 = r26;
        goto L_0x0295;
    L_0x03d9:
        r15 = r42;
        r16 = r23;
        goto L_0x0344;
    L_0x03df:
        r42 = r15;
        r23 = r16;
        goto L_0x01ba;
    L_0x03e5:
        r16 = r23;
        goto L_0x011d;
    L_0x03e9:
        r24 = r25;
        goto L_0x0101;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$loadGalleryPhotosAlbums$28$MediaController(int):void");
    }

    static final /* synthetic */ int lambda$null$27$MediaController(PhotoEntry o1, PhotoEntry o2) {
        if (o1.dateTaken < o2.dateTaken) {
            return 1;
        }
        if (o1.dateTaken > o2.dateTaken) {
            return -1;
        }
        return 0;
    }

    private static void broadcastNewPhotos(int guid, ArrayList<AlbumEntry> mediaAlbumsSorted, ArrayList<AlbumEntry> photoAlbumsSorted, Integer cameraAlbumIdFinal, AlbumEntry allMediaAlbumFinal, AlbumEntry allPhotosAlbumFinal, AlbumEntry allVideosAlbumFinal, int delay) {
        if (broadcastPhotosRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(broadcastPhotosRunnable);
        }
        MediaController$$Lambda$19 mediaController$$Lambda$19 = new MediaController$$Lambda$19(guid, mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal, allMediaAlbumFinal, allPhotosAlbumFinal, allVideosAlbumFinal);
        broadcastPhotosRunnable = mediaController$$Lambda$19;
        AndroidUtilities.runOnUIThread(mediaController$$Lambda$19, (long) delay);
    }

    static final /* synthetic */ void lambda$broadcastNewPhotos$29$MediaController(int guid, ArrayList mediaAlbumsSorted, ArrayList photoAlbumsSorted, Integer cameraAlbumIdFinal, AlbumEntry allMediaAlbumFinal, AlbumEntry allPhotosAlbumFinal, AlbumEntry allVideosAlbumFinal) {
        if (PhotoViewer.getInstance().isVisible()) {
            broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal, allMediaAlbumFinal, allPhotosAlbumFinal, allVideosAlbumFinal, 1000);
            return;
        }
        broadcastPhotosRunnable = null;
        allPhotosAlbumEntry = allPhotosAlbumFinal;
        allMediaAlbumEntry = allMediaAlbumFinal;
        allVideosAlbumEntry = allVideosAlbumFinal;
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).postNotificationName(NotificationCenter.albumsDidLoad, Integer.valueOf(guid), mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal);
        }
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
                FileLog.e(e);
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

    private void didWriteData(MessageObject messageObject, File file, boolean last, long availableSize, boolean error) {
        boolean firstWrite = this.videoConvertFirstWrite;
        if (firstWrite) {
            this.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new MediaController$$Lambda$20(this, error, last, messageObject, file, firstWrite, availableSize));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$didWriteData$30$MediaController(boolean error, boolean last, MessageObject messageObject, File file, boolean firstWrite, long availableSize) {
        if (error || last) {
            synchronized (this.videoConvertSync) {
                this.cancelCurrentVideoConversion = false;
            }
            this.videoConvertQueue.remove(messageObject);
            startVideoConvertFromQueue();
        }
        if (error) {
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.filePreparingFailed, messageObject, file.toString());
            return;
        }
        if (firstWrite) {
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.filePreparingStarted, messageObject, file.toString());
        }
        NotificationCenter instance = NotificationCenter.getInstance(messageObject.currentAccount);
        int i = NotificationCenter.fileNewChunkAvailable;
        Object[] objArr = new Object[4];
        objArr[0] = messageObject;
        objArr[1] = file.toString();
        objArr[2] = Long.valueOf(availableSize);
        objArr[3] = Long.valueOf(last ? file.length() : 0);
        instance.postNotificationName(i, objArr);
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
                        long availableSize = mediaMuxer.writeSampleData(muxerTrackIndex, buffer, info, false);
                        if (availableSize != 0) {
                            didWriteData(messageObject, file, false, availableSize, false);
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

    /* JADX WARNING: Removed duplicated region for block: B:198:0x0531  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0536 A:{SYNTHETIC, Splitter:B:200:0x0536} */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x053d  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01f0 A:{Catch:{ Exception -> 0x09c9, all -> 0x09bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01f5 A:{Catch:{ Exception -> 0x09c9, all -> 0x09bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01fa A:{Catch:{ Exception -> 0x09c9, all -> 0x09bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0202 A:{Catch:{ Exception -> 0x09c9, all -> 0x09bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x020d  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0212 A:{SYNTHETIC, Splitter:B:81:0x0212} */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x09d2  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0219  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x052a A:{ExcHandler: all (th java.lang.Throwable), Splitter:B:128:0x03be} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x052a A:{ExcHandler: all (th java.lang.Throwable), Splitter:B:128:0x03be} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01f0 A:{Catch:{ Exception -> 0x09c9, all -> 0x09bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01f5 A:{Catch:{ Exception -> 0x09c9, all -> 0x09bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01fa A:{Catch:{ Exception -> 0x09c9, all -> 0x09bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0202 A:{Catch:{ Exception -> 0x09c9, all -> 0x09bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x020d  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0212 A:{SYNTHETIC, Splitter:B:81:0x0212} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0219  */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x09d2  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x052a A:{ExcHandler: all (th java.lang.Throwable), Splitter:B:128:0x03be} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x095f  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0964 A:{SYNTHETIC, Splitter:B:376:0x0964} */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x096b  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0531  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0536 A:{SYNTHETIC, Splitter:B:200:0x0536} */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x053d  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x095f  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0964 A:{SYNTHETIC, Splitter:B:376:0x0964} */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x096b  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0531  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0536 A:{SYNTHETIC, Splitter:B:200:0x0536} */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x053d  */
    /* JADX WARNING: Missing block: B:96:0x0286, code skipped:
            if (r71.equals("nokia") != false) goto L_0x0288;
     */
    /* JADX WARNING: Missing block: B:195:0x052a, code skipped:
            r4 = th;
     */
    /* JADX WARNING: Missing block: B:196:0x052b, code skipped:
            r59 = r60;
            r26 = false;
     */
    private boolean convertVideo(org.telegram.messenger.MessageObject r106) {
        /*
        r105 = this;
        if (r106 == 0) goto L_0x0008;
    L_0x0002:
        r0 = r106;
        r4 = r0.videoEditedInfo;
        if (r4 != 0) goto L_0x000a;
    L_0x0008:
        r4 = 0;
    L_0x0009:
        return r4;
    L_0x000a:
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.originalPath;
        r101 = r0;
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.startTime;
        r92 = r0;
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.endTime;
        r28 = r0;
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.resultWidth;
        r88 = r0;
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.resultHeight;
        r86 = r0;
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.rotationValue;
        r90 = r0;
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.originalWidth;
        r77 = r0;
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.originalHeight;
        r76 = r0;
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.framerate;
        r61 = r0;
        r0 = r106;
        r4 = r0.videoEditedInfo;
        r0 = r4.bitrate;
        r38 = r0;
        r89 = 0;
        r4 = r106.getDialogId();
        r4 = (int) r4;
        if (r4 != 0) goto L_0x00ee;
    L_0x0063:
        r70 = 1;
    L_0x0065:
        r6 = new java.io.File;
        r0 = r106;
        r4 = r0.messageOwner;
        r4 = r4.attachPath;
        r6.<init>(r4);
        if (r101 != 0) goto L_0x0075;
    L_0x0072:
        r101 = "";
    L_0x0075:
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 18;
        if (r4 >= r5) goto L_0x00f2;
    L_0x007b:
        r0 = r86;
        r1 = r88;
        if (r0 <= r1) goto L_0x00f2;
    L_0x0081:
        r0 = r88;
        r1 = r77;
        if (r0 == r1) goto L_0x00f2;
    L_0x0087:
        r0 = r86;
        r1 = r76;
        if (r0 == r1) goto L_0x00f2;
    L_0x008d:
        r95 = r86;
        r86 = r88;
        r88 = r95;
        r90 = 90;
        r89 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
    L_0x0097:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = "videoconvert";
        r7 = 0;
        r84 = r4.getSharedPreferences(r5, r7);
        r66 = new java.io.File;
        r0 = r66;
        r1 = r101;
        r0.<init>(r1);
        r4 = r106.getId();
        if (r4 == 0) goto L_0x0126;
    L_0x00b0:
        r4 = "isPreviousOk";
        r5 = 1;
        r0 = r84;
        r69 = r0.getBoolean(r4, r5);
        r4 = r84.edit();
        r5 = "isPreviousOk";
        r7 = 0;
        r4 = r4.putBoolean(r5, r7);
        r4.commit();
        r4 = r66.canRead();
        if (r4 == 0) goto L_0x00d1;
    L_0x00cf:
        if (r69 != 0) goto L_0x0126;
    L_0x00d1:
        r7 = 1;
        r8 = 0;
        r10 = 1;
        r4 = r105;
        r5 = r106;
        r4.didWriteData(r5, r6, r7, r8, r10);
        r4 = r84.edit();
        r5 = "isPreviousOk";
        r7 = 1;
        r4 = r4.putBoolean(r5, r7);
        r4.commit();
        r4 = 0;
        goto L_0x0009;
    L_0x00ee:
        r70 = 0;
        goto L_0x0065;
    L_0x00f2:
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 20;
        if (r4 <= r5) goto L_0x0097;
    L_0x00f8:
        r4 = 90;
        r0 = r90;
        if (r0 != r4) goto L_0x0109;
    L_0x00fe:
        r95 = r86;
        r86 = r88;
        r88 = r95;
        r90 = 0;
        r89 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x0097;
    L_0x0109:
        r4 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r0 = r90;
        if (r0 != r4) goto L_0x0114;
    L_0x010f:
        r89 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r90 = 0;
        goto L_0x0097;
    L_0x0114:
        r4 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r0 = r90;
        if (r0 != r4) goto L_0x0097;
    L_0x011a:
        r95 = r86;
        r86 = r88;
        r88 = r95;
        r90 = 0;
        r89 = 90;
        goto L_0x0097;
    L_0x0126:
        r4 = 1;
        r0 = r105;
        r0.videoConvertFirstWrite = r4;
        r57 = 0;
        r96 = java.lang.System.currentTimeMillis();
        if (r88 == 0) goto L_0x0995;
    L_0x0133:
        if (r86 == 0) goto L_0x0995;
    L_0x0135:
        r73 = 0;
        r59 = 0;
        r63 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r63.<init>();	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r74 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r74.<init>();	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r0 = r74;
        r0.setCacheFile(r6);	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r0 = r74;
        r1 = r90;
        r0.setRotation(r1);	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r0 = r74;
        r1 = r88;
        r2 = r86;
        r0.setSize(r1, r2);	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r4 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r4.<init>();	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r0 = r74;
        r1 = r70;
        r73 = r4.createMovie(r0, r1);	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r60 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r60.<init>();	 Catch:{ Exception -> 0x0955, all -> 0x09b6 }
        r0 = r60;
        r1 = r101;
        r0.setDataSource(r1);	 Catch:{ Exception -> 0x09c3, all -> 0x052a }
        r105.checkConversionCanceled();	 Catch:{ Exception -> 0x09c3, all -> 0x052a }
        r0 = r88;
        r1 = r77;
        if (r0 != r1) goto L_0x018a;
    L_0x017a:
        r0 = r86;
        r1 = r76;
        if (r0 != r1) goto L_0x018a;
    L_0x0180:
        if (r89 != 0) goto L_0x018a;
    L_0x0182:
        r0 = r106;
        r4 = r0.videoEditedInfo;	 Catch:{ Exception -> 0x09c3, all -> 0x052a }
        r4 = r4.roundVideo;	 Catch:{ Exception -> 0x09c3, all -> 0x052a }
        if (r4 == 0) goto L_0x0930;
    L_0x018a:
        r4 = 0;
        r0 = r105;
        r1 = r60;
        r100 = r0.findTrack(r1, r4);	 Catch:{ Exception -> 0x09c3, all -> 0x052a }
        r4 = -1;
        r0 = r38;
        if (r0 == r4) goto L_0x0257;
    L_0x0198:
        r4 = 1;
        r0 = r105;
        r1 = r60;
        r36 = r0.findTrack(r1, r4);	 Catch:{ Exception -> 0x09c3, all -> 0x052a }
    L_0x01a1:
        if (r100 < 0) goto L_0x0948;
    L_0x01a3:
        r8 = 0;
        r51 = 0;
        r67 = 0;
        r80 = 0;
        r102 = -1;
        r78 = 0;
        r65 = 0;
        r44 = 0;
        r94 = 0;
        r104 = -5;
        r37 = -5;
        r85 = 0;
        r4 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r71 = r4.toLowerCase();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 18;
        if (r4 >= r5) goto L_0x04d2;
    L_0x01c6:
        r4 = "video/avc";
        r40 = selectCodec(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "video/avc";
        r0 = r40;
        r42 = selectColorFormat(r0, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r42 != 0) goto L_0x025b;
    L_0x01d8:
        r4 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = "no supported color format";
        r4.<init>(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        throw r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x01e1:
        r49 = move-exception;
    L_0x01e2:
        org.telegram.messenger.FileLog.e(r49);	 Catch:{ Exception -> 0x09c3, all -> 0x052a }
        r26 = 1;
    L_0x01e7:
        r0 = r60;
        r1 = r100;
        r0.unselectTrack(r1);	 Catch:{ Exception -> 0x09c9, all -> 0x09bb }
        if (r80 == 0) goto L_0x01f3;
    L_0x01f0:
        r80.release();	 Catch:{ Exception -> 0x09c9, all -> 0x09bb }
    L_0x01f3:
        if (r67 == 0) goto L_0x01f8;
    L_0x01f5:
        r67.release();	 Catch:{ Exception -> 0x09c9, all -> 0x09bb }
    L_0x01f8:
        if (r8 == 0) goto L_0x0200;
    L_0x01fa:
        r8.stop();	 Catch:{ Exception -> 0x09c9, all -> 0x09bb }
        r8.release();	 Catch:{ Exception -> 0x09c9, all -> 0x09bb }
    L_0x0200:
        if (r51 == 0) goto L_0x0208;
    L_0x0202:
        r51.stop();	 Catch:{ Exception -> 0x09c9, all -> 0x09bb }
        r51.release();	 Catch:{ Exception -> 0x09c9, all -> 0x09bb }
    L_0x0208:
        r105.checkConversionCanceled();	 Catch:{ Exception -> 0x09c9, all -> 0x09bb }
    L_0x020b:
        if (r60 == 0) goto L_0x0210;
    L_0x020d:
        r60.release();
    L_0x0210:
        if (r73 == 0) goto L_0x0215;
    L_0x0212:
        r73.finishMovie();	 Catch:{ Exception -> 0x094f }
    L_0x0215:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x09d2;
    L_0x0219:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "time = ";
        r4 = r4.append(r5);
        r12 = java.lang.System.currentTimeMillis();
        r12 = r12 - r96;
        r4 = r4.append(r12);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
        r59 = r60;
    L_0x0238:
        r4 = r84.edit();
        r5 = "isPreviousOk";
        r7 = 1;
        r4 = r4.putBoolean(r5, r7);
        r4.commit();
        r23 = 1;
        r24 = 0;
        r20 = r105;
        r21 = r106;
        r22 = r6;
        r20.didWriteData(r21, r22, r23, r24, r26);
        r4 = 1;
        goto L_0x0009;
    L_0x0257:
        r36 = -1;
        goto L_0x01a1;
    L_0x025b:
        r41 = r40.getName();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "OMX.qcom.";
        r0 = r41;
        r4 = r0.contains(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x0494;
    L_0x026a:
        r85 = 1;
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 16;
        if (r4 != r5) goto L_0x028a;
    L_0x0272:
        r4 = "lge";
        r0 = r71;
        r4 = r0.equals(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 != 0) goto L_0x0288;
    L_0x027d:
        r4 = "nokia";
        r0 = r71;
        r4 = r0.equals(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x028a;
    L_0x0288:
        r94 = 1;
    L_0x028a:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x02c3;
    L_0x028e:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4.<init>();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = "codec = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = r40.getName();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = " manufacturer = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r71;
        r4 = r4.append(r0);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = "device = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4.toString();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x02c3:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x02e0;
    L_0x02c7:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4.<init>();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = "colorFormat = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r42;
        r4 = r4.append(r0);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4.toString();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x02e0:
        r87 = r86;
        r82 = 0;
        r4 = r88 * r86;
        r4 = r4 * 3;
        r39 = r4 / 2;
        if (r85 != 0) goto L_0x04d7;
    L_0x02ec:
        r4 = r86 % 16;
        if (r4 == 0) goto L_0x0300;
    L_0x02f0:
        r4 = r86 % 16;
        r4 = 16 - r4;
        r87 = r87 + r4;
        r4 = r87 - r86;
        r82 = r88 * r4;
        r4 = r82 * 5;
        r4 = r4 / 4;
        r39 = r39 + r4;
    L_0x0300:
        r0 = r60;
        r1 = r100;
        r0.selectTrack(r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r60;
        r1 = r100;
        r99 = r0.getTrackFormat(r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r34 = 0;
        if (r36 < 0) goto L_0x0338;
    L_0x0313:
        r0 = r60;
        r1 = r36;
        r0.selectTrack(r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r60;
        r1 = r36;
        r35 = r0.getTrackFormat(r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "max-input-size";
        r0 = r35;
        r72 = r0.getInteger(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r34 = java.nio.ByteBuffer.allocateDirect(r72);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = 1;
        r0 = r73;
        r1 = r35;
        r37 = r0.addTrack(r1, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0338:
        r4 = 0;
        r4 = (r92 > r4 ? 1 : (r92 == r4 ? 0 : -1));
        if (r4 <= 0) goto L_0x0520;
    L_0x033e:
        r4 = 0;
        r0 = r60;
        r1 = r92;
        r0.seekTo(r1, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0346:
        r4 = "video/avc";
        r0 = r88;
        r1 = r86;
        r79 = android.media.MediaFormat.createVideoFormat(r4, r0, r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "color-format";
        r0 = r79;
        r1 = r42;
        r0.setInteger(r4, r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "bitrate";
        if (r38 <= 0) goto L_0x055b;
    L_0x0360:
        r0 = r79;
        r1 = r38;
        r0.setInteger(r4, r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "frame-rate";
        if (r61 == 0) goto L_0x0560;
    L_0x036c:
        r0 = r79;
        r1 = r61;
        r0.setInteger(r4, r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "i-frame-interval";
        r5 = 10;
        r0 = r79;
        r0.setInteger(r4, r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 18;
        if (r4 >= r5) goto L_0x0397;
    L_0x0383:
        r4 = "stride";
        r5 = r88 + 32;
        r0 = r79;
        r0.setInteger(r4, r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "slice-height";
        r0 = r79;
        r1 = r86;
        r0.setInteger(r4, r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0397:
        r4 = "video/avc";
        r51 = android.media.MediaCodec.createEncoderByType(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = 0;
        r5 = 0;
        r7 = 1;
        r0 = r51;
        r1 = r79;
        r0.configure(r1, r4, r5, r7);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 18;
        if (r4 < r5) goto L_0x03be;
    L_0x03ae:
        r68 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r51.createInputSurface();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r68;
        r0.<init>(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r68.makeCurrent();	 Catch:{ Exception -> 0x09cd, all -> 0x052a }
        r67 = r68;
    L_0x03be:
        r51.start();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "mime";
        r0 = r99;
        r4 = r0.getString(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r8 = android.media.MediaCodec.createDecoderByType(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 18;
        if (r4 < r5) goto L_0x0564;
    L_0x03d4:
        r81 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r81.<init>();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r80 = r81;
    L_0x03db:
        r4 = r80.getSurface();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 0;
        r7 = 0;
        r0 = r99;
        r8.configure(r0, r4, r5, r7);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r8.start();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r32 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r45 = 0;
        r54 = 0;
        r52 = 0;
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 21;
        if (r4 >= r5) goto L_0x0409;
    L_0x03f7:
        r45 = r8.getInputBuffers();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r54 = r51.getOutputBuffers();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 18;
        if (r4 >= r5) goto L_0x0409;
    L_0x0405:
        r52 = r51.getInputBuffers();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0409:
        r105.checkConversionCanceled();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x040c:
        if (r78 != 0) goto L_0x092c;
    L_0x040e:
        r105.checkConversionCanceled();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r65 != 0) goto L_0x045f;
    L_0x0413:
        r56 = 0;
        r62 = r60.getSampleTrackIndex();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r62;
        r1 = r100;
        if (r0 != r1) goto L_0x0589;
    L_0x041f:
        r4 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r9 = r8.dequeueInputBuffer(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r9 < 0) goto L_0x0444;
    L_0x0427:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 21;
        if (r4 >= r5) goto L_0x0575;
    L_0x042d:
        r64 = r45[r9];	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x042f:
        r4 = 0;
        r0 = r60;
        r1 = r64;
        r11 = r0.readSampleData(r1, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r11 >= 0) goto L_0x057b;
    L_0x043a:
        r10 = 0;
        r11 = 0;
        r12 = 0;
        r14 = 4;
        r8.queueInputBuffer(r9, r10, r11, r12, r14);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r65 = 1;
    L_0x0444:
        if (r56 == 0) goto L_0x045f;
    L_0x0446:
        r4 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r9 = r8.dequeueInputBuffer(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r9 < 0) goto L_0x045f;
    L_0x044e:
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r24 = 4;
        r18 = r8;
        r19 = r9;
        r18.queueInputBuffer(r19, r20, r21, r22, r24);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r65 = 1;
    L_0x045f:
        if (r44 != 0) goto L_0x0619;
    L_0x0461:
        r46 = 1;
    L_0x0463:
        r53 = 1;
    L_0x0465:
        if (r46 != 0) goto L_0x0469;
    L_0x0467:
        if (r53 == 0) goto L_0x040c;
    L_0x0469:
        r105.checkConversionCanceled();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r51;
        r1 = r63;
        r55 = r0.dequeueOutputBuffer(r1, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = -1;
        r0 = r55;
        if (r0 != r4) goto L_0x061d;
    L_0x047b:
        r53 = 0;
    L_0x047d:
        r4 = -1;
        r0 = r55;
        if (r0 != r4) goto L_0x0465;
    L_0x0482:
        if (r44 != 0) goto L_0x0465;
    L_0x0484:
        r4 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r63;
        r47 = r8.dequeueOutputBuffer(r0, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = -1;
        r0 = r47;
        if (r0 != r4) goto L_0x079a;
    L_0x0491:
        r46 = 0;
        goto L_0x0465;
    L_0x0494:
        r4 = "OMX.Intel.";
        r0 = r41;
        r4 = r0.contains(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x04a3;
    L_0x049f:
        r85 = 2;
        goto L_0x028a;
    L_0x04a3:
        r4 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r0 = r41;
        r4 = r0.equals(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x04b2;
    L_0x04ae:
        r85 = 3;
        goto L_0x028a;
    L_0x04b2:
        r4 = "OMX.SEC.AVC.Encoder";
        r0 = r41;
        r4 = r0.equals(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x04c3;
    L_0x04bd:
        r85 = 4;
        r94 = 1;
        goto L_0x028a;
    L_0x04c3:
        r4 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r0 = r41;
        r4 = r0.equals(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x028a;
    L_0x04ce:
        r85 = 5;
        goto L_0x028a;
    L_0x04d2:
        r42 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        goto L_0x02c3;
    L_0x04d7:
        r4 = 1;
        r0 = r85;
        if (r0 != r4) goto L_0x04f9;
    L_0x04dc:
        r4 = r71.toLowerCase();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = "lge";
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 != 0) goto L_0x0300;
    L_0x04e9:
        r4 = r88 * r86;
        r4 = r4 + 2047;
        r0 = r4 & -2048;
        r98 = r0;
        r4 = r88 * r86;
        r82 = r98 - r4;
        r39 = r39 + r82;
        goto L_0x0300;
    L_0x04f9:
        r4 = 5;
        r0 = r85;
        if (r0 == r4) goto L_0x0300;
    L_0x04fe:
        r4 = 3;
        r0 = r85;
        if (r0 != r4) goto L_0x0300;
    L_0x0503:
        r4 = "baidu";
        r0 = r71;
        r4 = r0.equals(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x0300;
    L_0x050e:
        r4 = r86 % 16;
        r4 = 16 - r4;
        r87 = r87 + r4;
        r4 = r87 - r86;
        r82 = r88 * r4;
        r4 = r82 * 5;
        r4 = r4 / 4;
        r39 = r39 + r4;
        goto L_0x0300;
    L_0x0520:
        r4 = 0;
        r7 = 0;
        r0 = r60;
        r0.seekTo(r4, r7);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x0346;
    L_0x052a:
        r4 = move-exception;
        r59 = r60;
        r26 = r57;
    L_0x052f:
        if (r59 == 0) goto L_0x0534;
    L_0x0531:
        r59.release();
    L_0x0534:
        if (r73 == 0) goto L_0x0539;
    L_0x0536:
        r73.finishMovie();	 Catch:{ Exception -> 0x098f }
    L_0x0539:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x055a;
    L_0x053d:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r7 = "time = ";
        r5 = r5.append(r7);
        r12 = java.lang.System.currentTimeMillis();
        r12 = r12 - r96;
        r5 = r5.append(r12);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x055a:
        throw r4;
    L_0x055b:
        r38 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x0360;
    L_0x0560:
        r61 = 25;
        goto L_0x036c;
    L_0x0564:
        r81 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r81;
        r1 = r88;
        r2 = r86;
        r3 = r89;
        r0.<init>(r1, r2, r3);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r80 = r81;
        goto L_0x03db;
    L_0x0575:
        r64 = r8.getInputBuffer(r9);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x042f;
    L_0x057b:
        r10 = 0;
        r12 = r60.getSampleTime();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r14 = 0;
        r8.queueInputBuffer(r9, r10, r11, r12, r14);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r60.advance();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x0444;
    L_0x0589:
        r4 = -1;
        r0 = r36;
        if (r0 == r4) goto L_0x0610;
    L_0x058e:
        r0 = r62;
        r1 = r36;
        if (r0 != r1) goto L_0x0610;
    L_0x0594:
        r4 = 0;
        r0 = r60;
        r1 = r34;
        r4 = r0.readSampleData(r1, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r63;
        r0.size = r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 21;
        if (r4 >= r5) goto L_0x05b6;
    L_0x05a7:
        r4 = 0;
        r0 = r34;
        r0.position(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r63;
        r4 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r34;
        r0.limit(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x05b6:
        r0 = r63;
        r4 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 < 0) goto L_0x0608;
    L_0x05bc:
        r4 = r60.getSampleTime();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r63;
        r0.presentationTimeUs = r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r60.advance();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x05c7:
        r0 = r63;
        r4 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 <= 0) goto L_0x0444;
    L_0x05cd:
        r4 = 0;
        r4 = (r28 > r4 ? 1 : (r28 == r4 ? 0 : -1));
        if (r4 < 0) goto L_0x05db;
    L_0x05d3:
        r0 = r63;
        r4 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = (r4 > r28 ? 1 : (r4 == r28 ? 0 : -1));
        if (r4 >= 0) goto L_0x0444;
    L_0x05db:
        r4 = 0;
        r0 = r63;
        r0.offset = r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r60.getSampleFlags();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r63;
        r0.flags = r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = 0;
        r0 = r73;
        r1 = r37;
        r2 = r34;
        r3 = r63;
        r16 = r0.writeSampleData(r1, r2, r3, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = 0;
        r4 = (r16 > r4 ? 1 : (r16 == r4 ? 0 : -1));
        if (r4 == 0) goto L_0x0444;
    L_0x05fb:
        r15 = 0;
        r18 = 0;
        r12 = r105;
        r13 = r106;
        r14 = r6;
        r12.didWriteData(r13, r14, r15, r16, r18);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x0444;
    L_0x0608:
        r4 = 0;
        r0 = r63;
        r0.size = r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r65 = 1;
        goto L_0x05c7;
    L_0x0610:
        r4 = -1;
        r0 = r62;
        if (r0 != r4) goto L_0x0444;
    L_0x0615:
        r56 = 1;
        goto L_0x0444;
    L_0x0619:
        r46 = 0;
        goto L_0x0463;
    L_0x061d:
        r4 = -3;
        r0 = r55;
        if (r0 != r4) goto L_0x062e;
    L_0x0622:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 21;
        if (r4 >= r5) goto L_0x047d;
    L_0x0628:
        r54 = r51.getOutputBuffers();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x047d;
    L_0x062e:
        r4 = -2;
        r0 = r55;
        if (r0 != r4) goto L_0x0647;
    L_0x0633:
        r75 = r51.getOutputFormat();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = -5;
        r0 = r104;
        if (r0 != r4) goto L_0x047d;
    L_0x063c:
        r4 = 0;
        r0 = r73;
        r1 = r75;
        r104 = r0.addTrack(r1, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x047d;
    L_0x0647:
        if (r55 >= 0) goto L_0x0665;
    L_0x0649:
        r4 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5.<init>();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r7 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r5 = r5.append(r7);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r55;
        r5 = r5.append(r0);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = r5.toString();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4.<init>(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        throw r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0665:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 21;
        if (r4 >= r5) goto L_0x0692;
    L_0x066b:
        r50 = r54[r55];	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x066d:
        if (r50 != 0) goto L_0x069b;
    L_0x066f:
        r4 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5.<init>();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r7 = "encoderOutputBuffer ";
        r5 = r5.append(r7);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r55;
        r5 = r5.append(r0);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r7 = " was null";
        r5 = r5.append(r7);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = r5.toString();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4.<init>(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        throw r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0692:
        r0 = r51;
        r1 = r55;
        r50 = r0.getOutputBuffer(r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x066d;
    L_0x069b:
        r0 = r63;
        r4 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 1;
        if (r4 <= r5) goto L_0x06c8;
    L_0x06a2:
        r0 = r63;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4 & 2;
        if (r4 != 0) goto L_0x06dc;
    L_0x06aa:
        r4 = 1;
        r0 = r73;
        r1 = r104;
        r2 = r50;
        r3 = r63;
        r16 = r0.writeSampleData(r1, r2, r3, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = 0;
        r4 = (r16 > r4 ? 1 : (r16 == r4 ? 0 : -1));
        if (r4 == 0) goto L_0x06c8;
    L_0x06bd:
        r15 = 0;
        r18 = 0;
        r12 = r105;
        r13 = r106;
        r14 = r6;
        r12.didWriteData(r13, r14, r15, r16, r18);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x06c8:
        r0 = r63;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x0796;
    L_0x06d0:
        r78 = 1;
    L_0x06d2:
        r4 = 0;
        r0 = r51;
        r1 = r55;
        r0.releaseOutputBuffer(r1, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x047d;
    L_0x06dc:
        r4 = -5;
        r0 = r104;
        if (r0 != r4) goto L_0x06c8;
    L_0x06e1:
        r0 = r63;
        r4 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = new byte[r4];	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r43 = r0;
        r0 = r63;
        r4 = r0.offset;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r63;
        r5 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4 + r5;
        r0 = r50;
        r0.limit(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r63;
        r4 = r0.offset;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r50;
        r0.position(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r50;
        r1 = r43;
        r0.get(r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r91 = 0;
        r83 = 0;
        r0 = r63;
        r4 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r33 = r4 + -1;
    L_0x0711:
        if (r33 < 0) goto L_0x0764;
    L_0x0713:
        r4 = 3;
        r0 = r33;
        if (r0 <= r4) goto L_0x0764;
    L_0x0718:
        r4 = r43[r33];	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 1;
        if (r4 != r5) goto L_0x0792;
    L_0x071d:
        r4 = r33 + -1;
        r4 = r43[r4];	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 != 0) goto L_0x0792;
    L_0x0723:
        r4 = r33 + -2;
        r4 = r43[r4];	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 != 0) goto L_0x0792;
    L_0x0729:
        r4 = r33 + -3;
        r4 = r43[r4];	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 != 0) goto L_0x0792;
    L_0x072f:
        r4 = r33 + -3;
        r91 = java.nio.ByteBuffer.allocate(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r63;
        r4 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = r33 + -3;
        r4 = r4 - r5;
        r83 = java.nio.ByteBuffer.allocate(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = 0;
        r5 = r33 + -3;
        r0 = r91;
        r1 = r43;
        r4 = r0.put(r1, r4, r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 0;
        r4.position(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r33 + -3;
        r0 = r63;
        r5 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r7 = r33 + -3;
        r5 = r5 - r7;
        r0 = r83;
        r1 = r43;
        r4 = r0.put(r1, r4, r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 0;
        r4.position(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0764:
        r4 = "video/avc";
        r0 = r88;
        r1 = r86;
        r75 = android.media.MediaFormat.createVideoFormat(r4, r0, r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r91 == 0) goto L_0x0787;
    L_0x0771:
        if (r83 == 0) goto L_0x0787;
    L_0x0773:
        r4 = "csd-0";
        r0 = r75;
        r1 = r91;
        r0.setByteBuffer(r4, r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = "csd-1";
        r0 = r75;
        r1 = r83;
        r0.setByteBuffer(r4, r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0787:
        r4 = 0;
        r0 = r73;
        r1 = r75;
        r104 = r0.addTrack(r1, r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x06c8;
    L_0x0792:
        r33 = r33 + -1;
        goto L_0x0711;
    L_0x0796:
        r78 = 0;
        goto L_0x06d2;
    L_0x079a:
        r4 = -3;
        r0 = r47;
        if (r0 == r4) goto L_0x0465;
    L_0x079f:
        r4 = -2;
        r0 = r47;
        if (r0 != r4) goto L_0x07c7;
    L_0x07a4:
        r75 = r8.getOutputFormat();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x0465;
    L_0x07ac:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4.<init>();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = "newFormat = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r75;
        r4 = r4.append(r0);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4.toString();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x0465;
    L_0x07c7:
        if (r47 >= 0) goto L_0x07e5;
    L_0x07c9:
        r4 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5.<init>();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r7 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r5 = r5.append(r7);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r47;
        r5 = r5.append(r0);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = r5.toString();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4.<init>(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        throw r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x07e5:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 18;
        if (r4 < r5) goto L_0x08a1;
    L_0x07eb:
        r0 = r63;
        r4 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x089d;
    L_0x07f1:
        r48 = 1;
    L_0x07f3:
        r4 = 0;
        r4 = (r28 > r4 ? 1 : (r28 == r4 ? 0 : -1));
        if (r4 <= 0) goto L_0x0811;
    L_0x07f9:
        r0 = r63;
        r4 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = (r4 > r28 ? 1 : (r4 == r28 ? 0 : -1));
        if (r4 < 0) goto L_0x0811;
    L_0x0801:
        r65 = 1;
        r44 = 1;
        r48 = 0;
        r0 = r63;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4 | 4;
        r0 = r63;
        r0.flags = r4;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0811:
        r4 = 0;
        r4 = (r92 > r4 ? 1 : (r92 == r4 ? 0 : -1));
        if (r4 <= 0) goto L_0x0853;
    L_0x0817:
        r4 = -1;
        r4 = (r102 > r4 ? 1 : (r102 == r4 ? 0 : -1));
        if (r4 != 0) goto L_0x0853;
    L_0x081d:
        r0 = r63;
        r4 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = (r4 > r92 ? 1 : (r4 == r92 ? 0 : -1));
        if (r4 >= 0) goto L_0x08b8;
    L_0x0825:
        r48 = 0;
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x0853;
    L_0x082b:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4.<init>();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = "drop frame startTime = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r92;
        r4 = r4.append(r0);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = " present time = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r63;
        r12 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4.append(r12);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4.toString();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0853:
        r0 = r47;
        r1 = r48;
        r8.releaseOutputBuffer(r0, r1);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r48 == 0) goto L_0x087e;
    L_0x085c:
        r58 = 0;
        r80.awaitNewImage();	 Catch:{ Exception -> 0x08bf, all -> 0x052a }
    L_0x0861:
        if (r58 != 0) goto L_0x087e;
    L_0x0863:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 18;
        if (r4 < r5) goto L_0x08c6;
    L_0x0869:
        r4 = 0;
        r0 = r80;
        r0.drawImage(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r0 = r63;
        r4 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r12 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r4 = r4 * r12;
        r0 = r67;
        r0.setPresentationTime(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r67.swapBuffers();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x087e:
        r0 = r63;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x0465;
    L_0x0886:
        r46 = 0;
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x0892;
    L_0x088c:
        r4 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
    L_0x0892:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r5 = 18;
        if (r4 < r5) goto L_0x090d;
    L_0x0898:
        r51.signalEndOfInputStream();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x0465;
    L_0x089d:
        r48 = 0;
        goto L_0x07f3;
    L_0x08a1:
        r0 = r63;
        r4 = r0.size;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 != 0) goto L_0x08b1;
    L_0x08a7:
        r0 = r63;
        r4 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r12 = 0;
        r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x08b5;
    L_0x08b1:
        r48 = 1;
    L_0x08b3:
        goto L_0x07f3;
    L_0x08b5:
        r48 = 0;
        goto L_0x08b3;
    L_0x08b8:
        r0 = r63;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r102 = r0;
        goto L_0x0853;
    L_0x08bf:
        r49 = move-exception;
        r58 = 1;
        org.telegram.messenger.FileLog.e(r49);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x0861;
    L_0x08c6:
        r4 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r51;
        r9 = r0.dequeueInputBuffer(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r9 < 0) goto L_0x0901;
    L_0x08d0:
        r4 = 1;
        r0 = r80;
        r0.drawImage(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r18 = r80.getFrame();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r19 = r52[r9];	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r19.clear();	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r20 = r42;
        r21 = r88;
        r22 = r86;
        r23 = r82;
        r24 = r94;
        org.telegram.messenger.Utilities.convertVideoFrame(r18, r19, r20, r21, r22, r23, r24);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r22 = 0;
        r0 = r63;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r24 = r0;
        r26 = 0;
        r20 = r51;
        r21 = r9;
        r23 = r39;
        r20.queueInputBuffer(r21, r22, r23, r24, r26);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x087e;
    L_0x0901:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r4 == 0) goto L_0x087e;
    L_0x0905:
        r4 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x087e;
    L_0x090d:
        r4 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r51;
        r9 = r0.dequeueInputBuffer(r4);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        if (r9 < 0) goto L_0x0465;
    L_0x0917:
        r22 = 0;
        r23 = 1;
        r0 = r63;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        r24 = r0;
        r26 = 4;
        r20 = r51;
        r21 = r9;
        r20.queueInputBuffer(r21, r22, r23, r24, r26);	 Catch:{ Exception -> 0x01e1, all -> 0x052a }
        goto L_0x0465;
    L_0x092c:
        r26 = r57;
        goto L_0x01e7;
    L_0x0930:
        r4 = -1;
        r0 = r38;
        if (r0 == r4) goto L_0x094c;
    L_0x0935:
        r31 = 1;
    L_0x0937:
        r21 = r105;
        r22 = r106;
        r23 = r60;
        r24 = r73;
        r25 = r63;
        r26 = r92;
        r30 = r6;
        r21.readAndWriteTracks(r22, r23, r24, r25, r26, r28, r30, r31);	 Catch:{ Exception -> 0x09c3, all -> 0x052a }
    L_0x0948:
        r26 = r57;
        goto L_0x020b;
    L_0x094c:
        r31 = 0;
        goto L_0x0937;
    L_0x094f:
        r49 = move-exception;
        org.telegram.messenger.FileLog.e(r49);
        goto L_0x0215;
    L_0x0955:
        r49 = move-exception;
        r26 = r57;
    L_0x0958:
        r26 = 1;
        org.telegram.messenger.FileLog.e(r49);	 Catch:{ all -> 0x09c0 }
        if (r59 == 0) goto L_0x0962;
    L_0x095f:
        r59.release();
    L_0x0962:
        if (r73 == 0) goto L_0x0967;
    L_0x0964:
        r73.finishMovie();	 Catch:{ Exception -> 0x098a }
    L_0x0967:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0238;
    L_0x096b:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "time = ";
        r4 = r4.append(r5);
        r12 = java.lang.System.currentTimeMillis();
        r12 = r12 - r96;
        r4 = r4.append(r12);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
        goto L_0x0238;
    L_0x098a:
        r49 = move-exception;
        org.telegram.messenger.FileLog.e(r49);
        goto L_0x0967;
    L_0x098f:
        r49 = move-exception;
        org.telegram.messenger.FileLog.e(r49);
        goto L_0x0539;
    L_0x0995:
        r4 = r84.edit();
        r5 = "isPreviousOk";
        r7 = 1;
        r4 = r4.putBoolean(r5, r7);
        r4.commit();
        r23 = 1;
        r24 = 0;
        r26 = 1;
        r20 = r105;
        r21 = r106;
        r22 = r6;
        r20.didWriteData(r21, r22, r23, r24, r26);
        r4 = 0;
        goto L_0x0009;
    L_0x09b6:
        r4 = move-exception;
        r26 = r57;
        goto L_0x052f;
    L_0x09bb:
        r4 = move-exception;
        r59 = r60;
        goto L_0x052f;
    L_0x09c0:
        r4 = move-exception;
        goto L_0x052f;
    L_0x09c3:
        r49 = move-exception;
        r59 = r60;
        r26 = r57;
        goto L_0x0958;
    L_0x09c9:
        r49 = move-exception;
        r59 = r60;
        goto L_0x0958;
    L_0x09cd:
        r49 = move-exception;
        r67 = r68;
        goto L_0x01e2;
    L_0x09d2:
        r59 = r60;
        goto L_0x0238;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MessageObject):boolean");
    }
}
