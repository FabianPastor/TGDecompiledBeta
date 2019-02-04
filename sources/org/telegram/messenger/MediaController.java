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
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
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
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    buffer.position(0);
                    double amplitude = Math.sqrt((sum / ((double) len)) / 2.0d);
                    ByteBuffer finalBuffer = buffer;
                    boolean flush = len != buffer.capacity();
                    if (len != 0) {
                        MediaController.this.fileEncodingQueue.postRunnable(new MediaController$1$$Lambda$0(this, finalBuffer, flush));
                    }
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(new MediaController$1$$Lambda$1(this, amplitude));
                    return;
                }
                MediaController.this.recordBuffers.add(buffer);
                MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
            }
        }

        final /* synthetic */ void lambda$run$1$MediaController$1(ByteBuffer finalBuffer, boolean flush) {
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
            MediaController.this.recordQueue.postRunnable(new MediaController$1$$Lambda$2(this, finalBuffer));
        }

        final /* synthetic */ void lambda$null$0$MediaController$1(ByteBuffer finalBuffer) {
            MediaController.this.recordBuffers.add(finalBuffer);
        }

        final /* synthetic */ void lambda$run$2$MediaController$1(double amplitude) {
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

        final /* synthetic */ void lambda$scheduleReloadRunnable$0$MediaController$GalleryObserverInternal() {
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

        public static void runConversion(MessageObject obj) {
            new Thread(new MediaController$VideoConvertRunnable$$Lambda$0(obj)).start();
        }

        static final /* synthetic */ void lambda$runConversion$0$MediaController$VideoConvertRunnable(MessageObject obj) {
            try {
                Thread th = new Thread(new VideoConvertRunnable(obj), "VideoConvertRunnable");
                th.start();
                th.join();
            } catch (Throwable e) {
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
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            contentResolver.registerContentObserver(Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        try {
            contentResolver.registerContentObserver(Video.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Throwable e22) {
            FileLog.e(e22);
        }
        try {
            contentResolver.registerContentObserver(Video.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Throwable e222) {
            FileLog.e(e222);
        }
    }

    final /* synthetic */ void lambda$new$1$MediaController() {
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
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    final /* synthetic */ void lambda$new$2$MediaController() {
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
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                public void onCallStateChanged(int state, String incomingNumber) {
                    AndroidUtilities.runOnUIThread(new MediaController$2$$Lambda$0(this, state));
                }

                final /* synthetic */ void lambda$onCallStateChanged$0$MediaController$2(int state) {
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
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
    }

    final /* synthetic */ void lambda$new$3$MediaController() {
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
            String fileName = currentPlayingMessageObject.getFileName();
            this.progressTimer = new Timer();
            this.progressTimer.schedule(new TimerTask() {
                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new MediaController$3$$Lambda$0(this, currentPlayingMessageObject));
                    }
                }

                final /* synthetic */ void lambda$run$0$MediaController$3(MessageObject currentPlayingMessageObject) {
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
                                if (progress < 0 || value >= 1.0f) {
                                    return;
                                }
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
                        } catch (Throwable e) {
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
        } catch (Throwable e3) {
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

    final /* synthetic */ void lambda$processMediaObserver$4$MediaController(ArrayList screenshotDates) {
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

    final /* synthetic */ void lambda$startRaiseToEarSensors$6$MediaController() {
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

    final /* synthetic */ void lambda$stopRaiseToEarSensors$7$MediaController() {
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
        cleanupPlayer(notify, stopService, false);
    }

    public void cleanupPlayer(boolean notify, boolean stopService, boolean byVoiceEnd) {
        if (this.audioPlayer != null) {
            try {
                this.audioPlayer.releasePlayer();
            } catch (Throwable e) {
                FileLog.e(e);
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
                FileLog.e(e2);
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
                        FileLog.e(e);
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
                        FileLog.e(e2);
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
                FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
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
                if (nextIndex >= 0 && nextIndex < currentPlayList.size()) {
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
                            FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
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

    final /* synthetic */ void lambda$setCurrentRoundVisible$8$MediaController() {
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
                FileLoader.getInstance(messageObject.currentAccount).loadFile(messageObject.getDocument(), messageObject, 0, 0);
                this.downloadingCurrentMessage = true;
                this.isPaused = false;
                this.lastProgress = 0;
                this.audioInfo = null;
                this.playingMessageObject = messageObject;
                if (this.playingMessageObject.isMusic()) {
                    try {
                        ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                    } catch (Throwable e) {
                        FileLog.e(e);
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
                    MediaController.this.currentAspectRatioFrameLayoutRatio = height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height);
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
                                    MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new MediaController$4$$Lambda$0(this));
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

                final /* synthetic */ void lambda$onSurfaceDestroyed$0$MediaController$4() {
                    MediaController.this.cleanupPlayer(true, true);
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }
            });
            this.currentAspectRatioFrameLayoutReady = false;
            if (this.pipRoundVideoView != null || !MessagesController.getInstance(messageObject.currentAccount).isDialogVisible(messageObject.getDialogId())) {
                if (this.pipRoundVideoView == null) {
                    try {
                        this.pipRoundVideoView = new PipRoundVideoView();
                        this.pipRoundVideoView.show(this.baseActivity, new MediaController$$Lambda$9(this));
                    } catch (Exception e2) {
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
            if (this.currentPlaybackSpeed > 1.0f) {
                this.videoPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
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
                        AndroidUtilities.runOnUIThread(new MediaController$$Lambda$10(messageObject));
                    }
                    this.audioPlayer.preparePlayer(Uri.fromFile(cacheFile), "other");
                } else {
                    byte[] bArr;
                    int reference = FileLoader.getInstance(messageObject.currentAccount).getFileReference(messageObject);
                    Document document = messageObject.getDocument();
                    StringBuilder append = new StringBuilder().append("?account=").append(messageObject.currentAccount).append("&id=").append(document.id).append("&hash=").append(document.access_hash).append("&dc=").append(document.dc_id).append("&size=").append(document.size).append("&mime=").append(URLEncoder.encode(document.mime_type, "UTF-8")).append("&rid=").append(reference).append("&name=").append(URLEncoder.encode(FileLoader.getDocumentFileName(document), "UTF-8")).append("&reference=");
                    if (document.file_reference != null) {
                        bArr = document.file_reference;
                    } else {
                        bArr = new byte[0];
                    }
                    this.audioPlayer.preparePlayer(Uri.parse("tg://" + messageObject.getFileName() + append.append(Utilities.bytesToHex(bArr)).toString()), "other");
                }
                if (messageObject.isVoice()) {
                    if (this.currentPlaybackSpeed > 1.0f) {
                        this.audioPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
                    }
                    this.audioInfo = null;
                    this.playlist.clear();
                    this.shuffledPlaylist.clear();
                } else {
                    try {
                        this.audioInfo = AudioInfo.getAudioInfo(cacheFile);
                    } catch (Throwable e3) {
                        FileLog.e(e3);
                    }
                }
                this.audioPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                this.audioPlayer.play();
            } catch (Throwable e32) {
                FileLog.e(e32);
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
        NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidStart, messageObject);
        long duration;
        if (this.videoPlayer != null) {
            try {
                if (this.playingMessageObject.audioProgress != 0.0f) {
                    duration = this.audioPlayer.getDuration();
                    if (duration == -9223372036854775807L) {
                        duration = ((long) this.playingMessageObject.getDuration()) * 1000;
                    }
                    this.videoPlayer.seekTo((long) ((int) (((float) duration) * this.playingMessageObject.audioProgress)));
                }
            } catch (Throwable e22) {
                this.playingMessageObject.audioProgress = 0.0f;
                this.playingMessageObject.audioProgressSec = 0;
                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                FileLog.e(e22);
            }
        } else if (this.audioPlayer != null) {
            try {
                if (this.playingMessageObject.audioProgress != 0.0f) {
                    duration = this.audioPlayer.getDuration();
                    if (duration == -9223372036854775807L) {
                        duration = ((long) this.playingMessageObject.getDuration()) * 1000;
                    }
                    this.audioPlayer.seekTo((long) ((int) (((float) duration) * this.playingMessageObject.audioProgress)));
                }
            } catch (Throwable e222) {
                this.playingMessageObject.resetPlayingProgress();
                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
                FileLog.e(e222);
            }
        }
        if (this.playingMessageObject.isMusic()) {
            try {
                ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            } catch (Throwable e322) {
                FileLog.e(e322);
            }
        } else {
            ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
        }
        return true;
    }

    final /* synthetic */ void lambda$playMessage$9$MediaController() {
        cleanupPlayer(true, true);
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
                FileLog.e(e);
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
        } catch (Throwable e) {
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
        } catch (Throwable e) {
            FileLog.e(e);
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
            lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
        }
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        Runnable mediaController$$Lambda$11 = new MediaController$$Lambda$11(this, currentAccount, dialog_id, reply_to_msg);
        this.recordStartRunnable = mediaController$$Lambda$11;
        if (paused) {
            j = 500;
        } else {
            j = 50;
        }
        dispatchQueue.postRunnable(mediaController$$Lambda$11, j);
    }

    final /* synthetic */ void lambda$startRecording$15$MediaController(int currentAccount, long dialog_id, MessageObject reply_to_msg) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$26(this, currentAccount));
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
                AndroidUtilities.runOnUIThread(new MediaController$$Lambda$27(this, currentAccount));
                return;
            }
            this.audioRecorder = new AudioRecord(1, 16000, 16, 2, this.recordBufferSize * 10);
            this.recordStartTime = System.currentTimeMillis();
            this.recordTimeCount = 0;
            this.samplesCount = 0;
            this.recordDialogId = dialog_id;
            this.recordingCurrentAccount = currentAccount;
            this.recordReplyingMessageObject = reply_to_msg;
            this.fileBuffer.rewind();
            this.audioRecorder.startRecording();
            this.recordQueue.postRunnable(this.recordRunnable);
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$29(this, currentAccount));
        } catch (Throwable e) {
            FileLog.e(e);
            this.recordingAudio = null;
            stopRecord();
            this.recordingAudioFile.delete();
            this.recordingAudioFile = null;
            try {
                this.audioRecorder.release();
                this.audioRecorder = null;
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$28(this, currentAccount));
        }
    }

    final /* synthetic */ void lambda$null$11$MediaController(int currentAccount) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
    }

    final /* synthetic */ void lambda$null$12$MediaController(int currentAccount) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
    }

    final /* synthetic */ void lambda$null$13$MediaController(int currentAccount) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
    }

    final /* synthetic */ void lambda$null$14$MediaController(int currentAccount) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStarted, new Object[0]);
    }

    public void generateWaveform(MessageObject messageObject) {
        String id = messageObject.getId() + "_" + messageObject.getDialogId();
        String path = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(id)) {
            this.generatingWaveform.put(id, messageObject);
            Utilities.globalQueue.postRunnable(new MediaController$$Lambda$12(this, path, id));
        }
    }

    final /* synthetic */ void lambda$generateWaveform$17$MediaController(String path, String id) {
        AndroidUtilities.runOnUIThread(new MediaController$$Lambda$25(this, id, getWaveform(path)));
    }

    final /* synthetic */ void lambda$null$16$MediaController(String id, byte[] waveform) {
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
            this.fileEncodingQueue.postRunnable(new MediaController$$Lambda$13(this, this.recordingAudio, this.recordingAudioFile, send));
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

    final /* synthetic */ void lambda$stopRecordingInternal$19$MediaController(TL_document audioToSend, File recordingAudioFileToSend, int send) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new MediaController$$Lambda$24(this, audioToSend, recordingAudioFileToSend, send));
    }

    final /* synthetic */ void lambda$null$18$MediaController(TL_document audioToSend, File recordingAudioFileToSend, int send) {
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
        this.recordQueue.postRunnable(new MediaController$$Lambda$14(this, send));
    }

    final /* synthetic */ void lambda$stopRecording$21$MediaController(int send) {
        if (this.audioRecorder != null) {
            try {
                this.sendAfterDone = send;
                this.audioRecorder.stop();
            } catch (Throwable e) {
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
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$23(this, send));
        }
    }

    final /* synthetic */ void lambda$null$20$MediaController(int send) {
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
                                progressDialog2.setMessage(LocaleController.getString("Loading", R.string.Loading));
                                progressDialog2.setCanceledOnTouchOutside(false);
                                progressDialog2.setCancelable(true);
                                progressDialog2.setOnCancelListener(new MediaController$$Lambda$15(cancelled));
                                progressDialog2.show();
                                progressDialog = progressDialog2;
                            } catch (Exception e2) {
                                e = e2;
                                progressDialog = progressDialog2;
                                FileLog.e(e);
                                new Thread(new MediaController$$Lambda$16(type, name, sourceFile, cancelled, progressDialog, mime)).start();
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.e(e);
                            new Thread(new MediaController$$Lambda$16(type, name, sourceFile, cancelled, progressDialog, mime)).start();
                        }
                    }
                    new Thread(new MediaController$$Lambda$16(type, name, sourceFile, cancelled, progressDialog, mime)).start();
                }
            }
        }
    }

    static final /* synthetic */ void lambda$saveFile$25$MediaController(int type, String name, File sourceFile, boolean[] cancelled, AlertDialog finalProgress, String mime) {
        File destFile;
        long a;
        if (type == 0) {
            try {
                destFile = AndroidUtilities.generatePicturePath();
            } catch (Throwable e) {
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
                    AndroidUtilities.runOnUIThread(new MediaController$$Lambda$21(finalProgress, (int) ((((float) a) / ((float) size)) * 100.0f)));
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
        } catch (Throwable e4) {
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
            AndroidUtilities.runOnUIThread(new MediaController$$Lambda$22(finalProgress));
        }
    }

    static final /* synthetic */ void lambda$null$23$MediaController(AlertDialog finalProgress, int progress) {
        try {
            finalProgress.setProgress(progress);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    static final /* synthetic */ void lambda$null$24$MediaController(AlertDialog finalProgress) {
        try {
            finalProgress.dismiss();
        } catch (Throwable e) {
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

    /* JADX WARNING: Removed duplicated region for block: B:57:0x00b7 A:{SYNTHETIC, Splitter: B:57:0x00b7} */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00bc A:{SYNTHETIC, Splitter: B:60:0x00bc} */
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
        Thread thread = new Thread(new MediaController$$Lambda$17(guid));
        thread.setPriority(1);
        thread.start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter: B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e7 A:{Catch:{ Throwable -> 0x036c, all -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter: B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0386 A:{SYNTHETIC, Splitter: B:152:0x0386} */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0376 A:{SYNTHETIC, Splitter: B:145:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0386 A:{SYNTHETIC, Splitter: B:152:0x0386} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e7 A:{Catch:{ Throwable -> 0x036c, all -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter: B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e7 A:{Catch:{ Throwable -> 0x036c, all -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter: B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0308 A:{SYNTHETIC, Splitter: B:113:0x0308} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x01b3 A:{SYNTHETIC, Splitter: B:61:0x01b3} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e7 A:{Catch:{ Throwable -> 0x036c, all -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0346 A:{SYNTHETIC, Splitter: B:132:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0353 A:{LOOP_END, LOOP:2: B:135:0x034b->B:137:0x0353} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0308 A:{SYNTHETIC, Splitter: B:113:0x0308} */
    static final /* synthetic */ void lambda$loadGalleryPhotosAlbums$27$MediaController(int r47) {
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
        r13 = NUM; // 0x7f0CLASSNAME float:1.860942E38 double:1.053097454E-314;
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
        r13 = NUM; // 0x7f0CLASSNAMEf float:1.8609417E38 double:1.0530974533E-314;
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
        r13 = NUM; // 0x7f0CLASSNAME float:1.8609421E38 double:1.0530974543E-314;
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
        r13 = NUM; // 0x7f0CLASSNAMEf float:1.8609417E38 double:1.0530974533E-314;
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
        r12 = org.telegram.messenger.MediaController$$Lambda$20.$instance;
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$loadGalleryPhotosAlbums$27$MediaController(int):void");
    }

    static final /* synthetic */ int lambda$null$26$MediaController(PhotoEntry o1, PhotoEntry o2) {
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
        Runnable mediaController$$Lambda$18 = new MediaController$$Lambda$18(guid, mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal, allMediaAlbumFinal, allPhotosAlbumFinal, allVideosAlbumFinal);
        broadcastPhotosRunnable = mediaController$$Lambda$18;
        AndroidUtilities.runOnUIThread(mediaController$$Lambda$18, (long) delay);
    }

    static final /* synthetic */ void lambda$broadcastNewPhotos$28$MediaController(int guid, ArrayList mediaAlbumsSorted, ArrayList photoAlbumsSorted, Integer cameraAlbumIdFinal, AlbumEntry allMediaAlbumFinal, AlbumEntry allPhotosAlbumFinal, AlbumEntry allVideosAlbumFinal) {
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

    private void didWriteData(MessageObject messageObject, File file, boolean last, boolean error) {
        boolean firstWrite = this.videoConvertFirstWrite;
        if (firstWrite) {
            this.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new MediaController$$Lambda$19(this, error, last, messageObject, file, firstWrite));
    }

    final /* synthetic */ void lambda$didWriteData$29$MediaController(boolean error, boolean last, MessageObject messageObject, File file, boolean firstWrite) {
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
        objArr[2] = Long.valueOf(file.length());
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

    /* JADX WARNING: Removed duplicated region for block: B:193:0x0528 A:{Splitter: B:126:0x03c1, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01f4 A:{Catch:{ Exception -> 0x092e, all -> 0x0528 }} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01f9 A:{Catch:{ Exception -> 0x092e, all -> 0x0528 }} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01fe A:{Catch:{ Exception -> 0x092e, all -> 0x0528 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0206 A:{Catch:{ Exception -> 0x092e, all -> 0x0528 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0216 A:{SYNTHETIC, Splitter: B:79:0x0216} */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x099e  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0528 A:{Splitter: B:126:0x03c1, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01f4 A:{Catch:{ Exception -> 0x092e, all -> 0x0528 }} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01f9 A:{Catch:{ Exception -> 0x092e, all -> 0x0528 }} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01fe A:{Catch:{ Exception -> 0x092e, all -> 0x0528 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0206 A:{Catch:{ Exception -> 0x092e, all -> 0x0528 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0216 A:{SYNTHETIC, Splitter: B:79:0x0216} */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x099e  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0528 A:{Splitter: B:126:0x03c1, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Missing block: B:94:0x0289, code:
            if (r61.equals("nokia") != false) goto L_0x028b;
     */
    /* JADX WARNING: Missing block: B:193:0x0528, code:
            r6 = th;
     */
    /* JADX WARNING: Missing block: B:194:0x0529, code:
            r49 = r50;
     */
    private boolean convertVideo(org.telegram.messenger.MessageObject r96) {
        /*
        r95 = this;
        if (r96 == 0) goto L_0x0008;
    L_0x0002:
        r0 = r96;
        r6 = r0.videoEditedInfo;
        if (r6 != 0) goto L_0x000a;
    L_0x0008:
        r6 = 0;
    L_0x0009:
        return r6;
    L_0x000a:
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.originalPath;
        r91 = r0;
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.startTime;
        r82 = r0;
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.endTime;
        r18 = r0;
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.resultWidth;
        r78 = r0;
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.resultHeight;
        r76 = r0;
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.rotationValue;
        r80 = r0;
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.originalWidth;
        r67 = r0;
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.originalHeight;
        r66 = r0;
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.framerate;
        r51 = r0;
        r0 = r96;
        r6 = r0.videoEditedInfo;
        r0 = r6.bitrate;
        r28 = r0;
        r79 = 0;
        r10 = r96.getDialogId();
        r6 = (int) r10;
        if (r6 != 0) goto L_0x00f0;
    L_0x0063:
        r60 = 1;
    L_0x0065:
        r20 = new java.io.File;
        r0 = r96;
        r6 = r0.messageOwner;
        r6 = r6.attachPath;
        r0 = r20;
        r0.<init>(r6);
        if (r91 != 0) goto L_0x0077;
    L_0x0074:
        r91 = "";
    L_0x0077:
        r6 = android.os.Build.VERSION.SDK_INT;
        r10 = 18;
        if (r6 >= r10) goto L_0x00f4;
    L_0x007d:
        r0 = r76;
        r1 = r78;
        if (r0 <= r1) goto L_0x00f4;
    L_0x0083:
        r0 = r78;
        r1 = r67;
        if (r0 == r1) goto L_0x00f4;
    L_0x0089:
        r0 = r76;
        r1 = r66;
        if (r0 == r1) goto L_0x00f4;
    L_0x008f:
        r85 = r76;
        r76 = r78;
        r78 = r85;
        r80 = 90;
        r79 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
    L_0x0099:
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r10 = "videoconvert";
        r11 = 0;
        r74 = r6.getSharedPreferences(r10, r11);
        r56 = new java.io.File;
        r0 = r56;
        r1 = r91;
        r0.<init>(r1);
        r6 = r96.getId();
        if (r6 == 0) goto L_0x0128;
    L_0x00b2:
        r6 = "isPreviousOk";
        r10 = 1;
        r0 = r74;
        r59 = r0.getBoolean(r6, r10);
        r6 = r74.edit();
        r10 = "isPreviousOk";
        r11 = 0;
        r6 = r6.putBoolean(r10, r11);
        r6.commit();
        r6 = r56.canRead();
        if (r6 == 0) goto L_0x00d3;
    L_0x00d1:
        if (r59 != 0) goto L_0x0128;
    L_0x00d3:
        r6 = 1;
        r10 = 1;
        r0 = r95;
        r1 = r96;
        r2 = r20;
        r0.didWriteData(r1, r2, r6, r10);
        r6 = r74.edit();
        r10 = "isPreviousOk";
        r11 = 1;
        r6 = r6.putBoolean(r10, r11);
        r6.commit();
        r6 = 0;
        goto L_0x0009;
    L_0x00f0:
        r60 = 0;
        goto L_0x0065;
    L_0x00f4:
        r6 = android.os.Build.VERSION.SDK_INT;
        r10 = 20;
        if (r6 <= r10) goto L_0x0099;
    L_0x00fa:
        r6 = 90;
        r0 = r80;
        if (r0 != r6) goto L_0x010b;
    L_0x0100:
        r85 = r76;
        r76 = r78;
        r78 = r85;
        r80 = 0;
        r79 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x0099;
    L_0x010b:
        r6 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r0 = r80;
        if (r0 != r6) goto L_0x0116;
    L_0x0111:
        r79 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r80 = 0;
        goto L_0x0099;
    L_0x0116:
        r6 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r0 = r80;
        if (r0 != r6) goto L_0x0099;
    L_0x011c:
        r85 = r76;
        r76 = r78;
        r78 = r85;
        r80 = 0;
        r79 = 90;
        goto L_0x0099;
    L_0x0128:
        r6 = 1;
        r0 = r95;
        r0.videoConvertFirstWrite = r6;
        r47 = 0;
        r86 = java.lang.System.currentTimeMillis();
        if (r78 == 0) goto L_0x0977;
    L_0x0135:
        if (r76 == 0) goto L_0x0977;
    L_0x0137:
        r63 = 0;
        r49 = 0;
        r53 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0997 }
        r53.<init>();	 Catch:{ Exception -> 0x0997 }
        r64 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0997 }
        r64.<init>();	 Catch:{ Exception -> 0x0997 }
        r0 = r64;
        r1 = r20;
        r0.setCacheFile(r1);	 Catch:{ Exception -> 0x0997 }
        r0 = r64;
        r1 = r80;
        r0.setRotation(r1);	 Catch:{ Exception -> 0x0997 }
        r0 = r64;
        r1 = r78;
        r2 = r76;
        r0.setSize(r1, r2);	 Catch:{ Exception -> 0x0997 }
        r6 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0997 }
        r6.<init>();	 Catch:{ Exception -> 0x0997 }
        r0 = r64;
        r1 = r60;
        r63 = r6.createMovie(r0, r1);	 Catch:{ Exception -> 0x0997 }
        r50 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0997 }
        r50.<init>();	 Catch:{ Exception -> 0x0997 }
        r0 = r50;
        r1 = r91;
        r0.setDataSource(r1);	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        r95.checkConversionCanceled();	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        r0 = r78;
        r1 = r67;
        if (r0 != r1) goto L_0x018e;
    L_0x017e:
        r0 = r76;
        r1 = r66;
        if (r0 != r1) goto L_0x018e;
    L_0x0184:
        if (r79 != 0) goto L_0x018e;
    L_0x0186:
        r0 = r96;
        r6 = r0.videoEditedInfo;	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        r6 = r6.roundVideo;	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        if (r6 == 0) goto L_0x0916;
    L_0x018e:
        r6 = 0;
        r0 = r95;
        r1 = r50;
        r90 = r0.findTrack(r1, r6);	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        r6 = -1;
        r0 = r28;
        if (r0 == r6) goto L_0x025a;
    L_0x019c:
        r6 = 1;
        r0 = r95;
        r1 = r50;
        r26 = r0.findTrack(r1, r6);	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
    L_0x01a5:
        if (r90 < 0) goto L_0x020f;
    L_0x01a7:
        r4 = 0;
        r41 = 0;
        r57 = 0;
        r70 = 0;
        r92 = -1;
        r68 = 0;
        r55 = 0;
        r34 = 0;
        r84 = 0;
        r94 = -5;
        r27 = -5;
        r75 = 0;
        r6 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r61 = r6.toLowerCase();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 18;
        if (r6 >= r10) goto L_0x04d0;
    L_0x01ca:
        r6 = "video/avc";
        r30 = selectCodec(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "video/avc";
        r0 = r30;
        r32 = selectColorFormat(r0, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r32 != 0) goto L_0x025e;
    L_0x01dc:
        r6 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = "no supported color format";
        r6.<init>(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        throw r6;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x01e5:
        r39 = move-exception;
    L_0x01e6:
        org.telegram.messenger.FileLog.e(r39);	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        r47 = 1;
    L_0x01eb:
        r0 = r50;
        r1 = r90;
        r0.unselectTrack(r1);	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        if (r70 == 0) goto L_0x01f7;
    L_0x01f4:
        r70.release();	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
    L_0x01f7:
        if (r57 == 0) goto L_0x01fc;
    L_0x01f9:
        r57.release();	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
    L_0x01fc:
        if (r4 == 0) goto L_0x0204;
    L_0x01fe:
        r4.stop();	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        r4.release();	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
    L_0x0204:
        if (r41 == 0) goto L_0x020c;
    L_0x0206:
        r41.stop();	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        r41.release();	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
    L_0x020c:
        r95.checkConversionCanceled();	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
    L_0x020f:
        if (r50 == 0) goto L_0x0214;
    L_0x0211:
        r50.release();
    L_0x0214:
        if (r63 == 0) goto L_0x0219;
    L_0x0216:
        r63.finishMovie();	 Catch:{ Exception -> 0x0966 }
    L_0x0219:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x099e;
    L_0x021d:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r10 = "time = ";
        r6 = r6.append(r10);
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r86;
        r6 = r6.append(r10);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.d(r6);
        r49 = r50;
    L_0x023c:
        r6 = r74.edit();
        r10 = "isPreviousOk";
        r11 = 1;
        r6 = r6.putBoolean(r10, r11);
        r6.commit();
        r6 = 1;
        r0 = r95;
        r1 = r96;
        r2 = r20;
        r3 = r47;
        r0.didWriteData(r1, r2, r6, r3);
        r6 = 1;
        goto L_0x0009;
    L_0x025a:
        r26 = -1;
        goto L_0x01a5;
    L_0x025e:
        r31 = r30.getName();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "OMX.qcom.";
        r0 = r31;
        r6 = r0.contains(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x0492;
    L_0x026d:
        r75 = 1;
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 16;
        if (r6 != r10) goto L_0x028d;
    L_0x0275:
        r6 = "lge";
        r0 = r61;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 != 0) goto L_0x028b;
    L_0x0280:
        r6 = "nokia";
        r0 = r61;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x028d;
    L_0x028b:
        r84 = 1;
    L_0x028d:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x02c6;
    L_0x0291:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6.<init>();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = "codec = ";
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = r30.getName();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = " manufacturer = ";
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r61;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = "device = ";
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x02c6:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x02e3;
    L_0x02ca:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6.<init>();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = "colorFormat = ";
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r32;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x02e3:
        r77 = r76;
        r72 = 0;
        r6 = r78 * r76;
        r6 = r6 * 3;
        r29 = r6 / 2;
        if (r75 != 0) goto L_0x04d5;
    L_0x02ef:
        r6 = r76 % 16;
        if (r6 == 0) goto L_0x0303;
    L_0x02f3:
        r6 = r76 % 16;
        r6 = 16 - r6;
        r77 = r77 + r6;
        r6 = r77 - r76;
        r72 = r78 * r6;
        r6 = r72 * 5;
        r6 = r6 / 4;
        r29 = r29 + r6;
    L_0x0303:
        r0 = r50;
        r1 = r90;
        r0.selectTrack(r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r50;
        r1 = r90;
        r89 = r0.getTrackFormat(r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r24 = 0;
        if (r26 < 0) goto L_0x033b;
    L_0x0316:
        r0 = r50;
        r1 = r26;
        r0.selectTrack(r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r50;
        r1 = r26;
        r25 = r0.getTrackFormat(r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "max-input-size";
        r0 = r25;
        r62 = r0.getInteger(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r24 = java.nio.ByteBuffer.allocateDirect(r62);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = 1;
        r0 = r63;
        r1 = r25;
        r27 = r0.addTrack(r1, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x033b:
        r10 = 0;
        r6 = (r82 > r10 ? 1 : (r82 == r10 ? 0 : -1));
        if (r6 <= 0) goto L_0x051e;
    L_0x0341:
        r6 = 0;
        r0 = r50;
        r1 = r82;
        r0.seekTo(r1, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x0349:
        r6 = "video/avc";
        r0 = r78;
        r1 = r76;
        r69 = android.media.MediaFormat.createVideoFormat(r6, r0, r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "color-format";
        r0 = r69;
        r1 = r32;
        r0.setInteger(r6, r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "bitrate";
        if (r28 <= 0) goto L_0x0557;
    L_0x0363:
        r0 = r69;
        r1 = r28;
        r0.setInteger(r6, r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "frame-rate";
        if (r51 == 0) goto L_0x055c;
    L_0x036f:
        r0 = r69;
        r1 = r51;
        r0.setInteger(r6, r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "i-frame-interval";
        r10 = 10;
        r0 = r69;
        r0.setInteger(r6, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 18;
        if (r6 >= r10) goto L_0x039a;
    L_0x0386:
        r6 = "stride";
        r10 = r78 + 32;
        r0 = r69;
        r0.setInteger(r6, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "slice-height";
        r0 = r69;
        r1 = r76;
        r0.setInteger(r6, r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x039a:
        r6 = "video/avc";
        r41 = android.media.MediaCodec.createEncoderByType(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = 0;
        r10 = 0;
        r11 = 1;
        r0 = r41;
        r1 = r69;
        r0.configure(r1, r6, r10, r11);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 18;
        if (r6 < r10) goto L_0x03c1;
    L_0x03b1:
        r58 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r41.createInputSurface();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r58;
        r0.<init>(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r58.makeCurrent();	 Catch:{ Exception -> 0x0999, all -> 0x0528 }
        r57 = r58;
    L_0x03c1:
        r41.start();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "mime";
        r0 = r89;
        r6 = r0.getString(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r4 = android.media.MediaCodec.createDecoderByType(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 18;
        if (r6 < r10) goto L_0x0560;
    L_0x03d7:
        r71 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r71.<init>();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r70 = r71;
    L_0x03de:
        r6 = r70.getSurface();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 0;
        r11 = 0;
        r0 = r89;
        r4.configure(r0, r6, r10, r11);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r4.start();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r22 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r35 = 0;
        r44 = 0;
        r42 = 0;
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 21;
        if (r6 >= r10) goto L_0x040c;
    L_0x03fa:
        r35 = r4.getInputBuffers();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r44 = r41.getOutputBuffers();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 18;
        if (r6 >= r10) goto L_0x040c;
    L_0x0408:
        r42 = r41.getInputBuffers();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x040c:
        r95.checkConversionCanceled();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x040f:
        if (r68 != 0) goto L_0x01eb;
    L_0x0411:
        r95.checkConversionCanceled();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r55 != 0) goto L_0x045d;
    L_0x0416:
        r46 = 0;
        r52 = r50.getSampleTrackIndex();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r52;
        r1 = r90;
        if (r0 != r1) goto L_0x0585;
    L_0x0422:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r5 = r4.dequeueInputBuffer(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r5 < 0) goto L_0x0447;
    L_0x042a:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 21;
        if (r6 >= r10) goto L_0x0571;
    L_0x0430:
        r54 = r35[r5];	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x0432:
        r6 = 0;
        r0 = r50;
        r1 = r54;
        r7 = r0.readSampleData(r1, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r7 >= 0) goto L_0x0577;
    L_0x043d:
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r10 = 4;
        r4.queueInputBuffer(r5, r6, r7, r8, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r55 = 1;
    L_0x0447:
        if (r46 == 0) goto L_0x045d;
    L_0x0449:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r5 = r4.dequeueInputBuffer(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r5 < 0) goto L_0x045d;
    L_0x0451:
        r10 = 0;
        r11 = 0;
        r12 = 0;
        r14 = 4;
        r8 = r4;
        r9 = r5;
        r8.queueInputBuffer(r9, r10, r11, r12, r14);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r55 = 1;
    L_0x045d:
        if (r34 != 0) goto L_0x0611;
    L_0x045f:
        r36 = 1;
    L_0x0461:
        r43 = 1;
    L_0x0463:
        if (r36 != 0) goto L_0x0467;
    L_0x0465:
        if (r43 == 0) goto L_0x040f;
    L_0x0467:
        r95.checkConversionCanceled();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r41;
        r1 = r53;
        r45 = r0.dequeueOutputBuffer(r1, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = -1;
        r0 = r45;
        if (r0 != r6) goto L_0x0615;
    L_0x0479:
        r43 = 0;
    L_0x047b:
        r6 = -1;
        r0 = r45;
        if (r0 != r6) goto L_0x0463;
    L_0x0480:
        if (r34 != 0) goto L_0x0463;
    L_0x0482:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r53;
        r37 = r4.dequeueOutputBuffer(r0, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = -1;
        r0 = r37;
        if (r0 != r6) goto L_0x078e;
    L_0x048f:
        r36 = 0;
        goto L_0x0463;
    L_0x0492:
        r6 = "OMX.Intel.";
        r0 = r31;
        r6 = r0.contains(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x04a1;
    L_0x049d:
        r75 = 2;
        goto L_0x028d;
    L_0x04a1:
        r6 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r0 = r31;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x04b0;
    L_0x04ac:
        r75 = 3;
        goto L_0x028d;
    L_0x04b0:
        r6 = "OMX.SEC.AVC.Encoder";
        r0 = r31;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x04c1;
    L_0x04bb:
        r75 = 4;
        r84 = 1;
        goto L_0x028d;
    L_0x04c1:
        r6 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r0 = r31;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x028d;
    L_0x04cc:
        r75 = 5;
        goto L_0x028d;
    L_0x04d0:
        r32 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        goto L_0x02c6;
    L_0x04d5:
        r6 = 1;
        r0 = r75;
        if (r0 != r6) goto L_0x04f7;
    L_0x04da:
        r6 = r61.toLowerCase();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = "lge";
        r6 = r6.equals(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 != 0) goto L_0x0303;
    L_0x04e7:
        r6 = r78 * r76;
        r6 = r6 + 2047;
        r0 = r6 & -2048;
        r88 = r0;
        r6 = r78 * r76;
        r72 = r88 - r6;
        r29 = r29 + r72;
        goto L_0x0303;
    L_0x04f7:
        r6 = 5;
        r0 = r75;
        if (r0 == r6) goto L_0x0303;
    L_0x04fc:
        r6 = 3;
        r0 = r75;
        if (r0 != r6) goto L_0x0303;
    L_0x0501:
        r6 = "baidu";
        r0 = r61;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x0303;
    L_0x050c:
        r6 = r76 % 16;
        r6 = 16 - r6;
        r77 = r77 + r6;
        r6 = r77 - r76;
        r72 = r78 * r6;
        r6 = r72 * 5;
        r6 = r6 / 4;
        r29 = r29 + r6;
        goto L_0x0303;
    L_0x051e:
        r10 = 0;
        r6 = 0;
        r0 = r50;
        r0.seekTo(r10, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0349;
    L_0x0528:
        r6 = move-exception;
        r49 = r50;
    L_0x052b:
        if (r49 == 0) goto L_0x0530;
    L_0x052d:
        r49.release();
    L_0x0530:
        if (r63 == 0) goto L_0x0535;
    L_0x0532:
        r63.finishMovie();	 Catch:{ Exception -> 0x0971 }
    L_0x0535:
        r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r10 == 0) goto L_0x0556;
    L_0x0539:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "time = ";
        r10 = r10.append(r11);
        r12 = java.lang.System.currentTimeMillis();
        r12 = r12 - r86;
        r10 = r10.append(r12);
        r10 = r10.toString();
        org.telegram.messenger.FileLog.d(r10);
    L_0x0556:
        throw r6;
    L_0x0557:
        r28 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x0363;
    L_0x055c:
        r51 = 25;
        goto L_0x036f;
    L_0x0560:
        r71 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r71;
        r1 = r78;
        r2 = r76;
        r3 = r79;
        r0.<init>(r1, r2, r3);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r70 = r71;
        goto L_0x03de;
    L_0x0571:
        r54 = r4.getInputBuffer(r5);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0432;
    L_0x0577:
        r6 = 0;
        r8 = r50.getSampleTime();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 0;
        r4.queueInputBuffer(r5, r6, r7, r8, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r50.advance();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0447;
    L_0x0585:
        r6 = -1;
        r0 = r26;
        if (r0 == r6) goto L_0x0608;
    L_0x058a:
        r0 = r52;
        r1 = r26;
        if (r0 != r1) goto L_0x0608;
    L_0x0590:
        r6 = 0;
        r0 = r50;
        r1 = r24;
        r6 = r0.readSampleData(r1, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r53;
        r0.size = r6;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 21;
        if (r6 >= r10) goto L_0x05b2;
    L_0x05a3:
        r6 = 0;
        r0 = r24;
        r0.position(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r53;
        r6 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r24;
        r0.limit(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x05b2:
        r0 = r53;
        r6 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 < 0) goto L_0x0600;
    L_0x05b8:
        r10 = r50.getSampleTime();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r53;
        r0.presentationTimeUs = r10;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r50.advance();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x05c3:
        r0 = r53;
        r6 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 <= 0) goto L_0x0447;
    L_0x05c9:
        r10 = 0;
        r6 = (r18 > r10 ? 1 : (r18 == r10 ? 0 : -1));
        if (r6 < 0) goto L_0x05d7;
    L_0x05cf:
        r0 = r53;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1));
        if (r6 >= 0) goto L_0x0447;
    L_0x05d7:
        r6 = 0;
        r0 = r53;
        r0.offset = r6;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r50.getSampleFlags();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r53;
        r0.flags = r6;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = 0;
        r0 = r63;
        r1 = r27;
        r2 = r24;
        r3 = r53;
        r6 = r0.writeSampleData(r1, r2, r3, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x0447;
    L_0x05f3:
        r6 = 0;
        r10 = 0;
        r0 = r95;
        r1 = r96;
        r2 = r20;
        r0.didWriteData(r1, r2, r6, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0447;
    L_0x0600:
        r6 = 0;
        r0 = r53;
        r0.size = r6;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r55 = 1;
        goto L_0x05c3;
    L_0x0608:
        r6 = -1;
        r0 = r52;
        if (r0 != r6) goto L_0x0447;
    L_0x060d:
        r46 = 1;
        goto L_0x0447;
    L_0x0611:
        r36 = 0;
        goto L_0x0461;
    L_0x0615:
        r6 = -3;
        r0 = r45;
        if (r0 != r6) goto L_0x0626;
    L_0x061a:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 21;
        if (r6 >= r10) goto L_0x047b;
    L_0x0620:
        r44 = r41.getOutputBuffers();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x047b;
    L_0x0626:
        r6 = -2;
        r0 = r45;
        if (r0 != r6) goto L_0x063f;
    L_0x062b:
        r65 = r41.getOutputFormat();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = -5;
        r0 = r94;
        if (r0 != r6) goto L_0x047b;
    L_0x0634:
        r6 = 0;
        r0 = r63;
        r1 = r65;
        r94 = r0.addTrack(r1, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x047b;
    L_0x063f:
        if (r45 >= 0) goto L_0x065d;
    L_0x0641:
        r6 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10.<init>();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r11 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r45;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6.<init>(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        throw r6;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x065d:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 21;
        if (r6 >= r10) goto L_0x068a;
    L_0x0663:
        r40 = r44[r45];	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x0665:
        if (r40 != 0) goto L_0x0693;
    L_0x0667:
        r6 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10.<init>();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r11 = "encoderOutputBuffer ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r45;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r11 = " was null";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6.<init>(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        throw r6;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x068a:
        r0 = r41;
        r1 = r45;
        r40 = r0.getOutputBuffer(r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0665;
    L_0x0693:
        r0 = r53;
        r6 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 1;
        if (r6 <= r10) goto L_0x06bc;
    L_0x069a:
        r0 = r53;
        r6 = r0.flags;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6 & 2;
        if (r6 != 0) goto L_0x06d0;
    L_0x06a2:
        r6 = 1;
        r0 = r63;
        r1 = r94;
        r2 = r40;
        r3 = r53;
        r6 = r0.writeSampleData(r1, r2, r3, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x06bc;
    L_0x06b1:
        r6 = 0;
        r10 = 0;
        r0 = r95;
        r1 = r96;
        r2 = r20;
        r0.didWriteData(r1, r2, r6, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x06bc:
        r0 = r53;
        r6 = r0.flags;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6 & 4;
        if (r6 == 0) goto L_0x078a;
    L_0x06c4:
        r68 = 1;
    L_0x06c6:
        r6 = 0;
        r0 = r41;
        r1 = r45;
        r0.releaseOutputBuffer(r1, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x047b;
    L_0x06d0:
        r6 = -5;
        r0 = r94;
        if (r0 != r6) goto L_0x06bc;
    L_0x06d5:
        r0 = r53;
        r6 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = new byte[r6];	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r33 = r0;
        r0 = r53;
        r6 = r0.offset;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r53;
        r10 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6 + r10;
        r0 = r40;
        r0.limit(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r53;
        r6 = r0.offset;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r40;
        r0.position(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r40;
        r1 = r33;
        r0.get(r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r81 = 0;
        r73 = 0;
        r0 = r53;
        r6 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r23 = r6 + -1;
    L_0x0705:
        if (r23 < 0) goto L_0x0758;
    L_0x0707:
        r6 = 3;
        r0 = r23;
        if (r0 <= r6) goto L_0x0758;
    L_0x070c:
        r6 = r33[r23];	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 1;
        if (r6 != r10) goto L_0x0786;
    L_0x0711:
        r6 = r23 + -1;
        r6 = r33[r6];	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 != 0) goto L_0x0786;
    L_0x0717:
        r6 = r23 + -2;
        r6 = r33[r6];	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 != 0) goto L_0x0786;
    L_0x071d:
        r6 = r23 + -3;
        r6 = r33[r6];	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 != 0) goto L_0x0786;
    L_0x0723:
        r6 = r23 + -3;
        r81 = java.nio.ByteBuffer.allocate(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r53;
        r6 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = r23 + -3;
        r6 = r6 - r10;
        r73 = java.nio.ByteBuffer.allocate(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = 0;
        r10 = r23 + -3;
        r0 = r81;
        r1 = r33;
        r6 = r0.put(r1, r6, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 0;
        r6.position(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r23 + -3;
        r0 = r53;
        r10 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r11 = r23 + -3;
        r10 = r10 - r11;
        r0 = r73;
        r1 = r33;
        r6 = r0.put(r1, r6, r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 0;
        r6.position(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x0758:
        r6 = "video/avc";
        r0 = r78;
        r1 = r76;
        r65 = android.media.MediaFormat.createVideoFormat(r6, r0, r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r81 == 0) goto L_0x077b;
    L_0x0765:
        if (r73 == 0) goto L_0x077b;
    L_0x0767:
        r6 = "csd-0";
        r0 = r65;
        r1 = r81;
        r0.setByteBuffer(r6, r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = "csd-1";
        r0 = r65;
        r1 = r73;
        r0.setByteBuffer(r6, r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x077b:
        r6 = 0;
        r0 = r63;
        r1 = r65;
        r94 = r0.addTrack(r1, r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x06bc;
    L_0x0786:
        r23 = r23 + -1;
        goto L_0x0705;
    L_0x078a:
        r68 = 0;
        goto L_0x06c6;
    L_0x078e:
        r6 = -3;
        r0 = r37;
        if (r0 == r6) goto L_0x0463;
    L_0x0793:
        r6 = -2;
        r0 = r37;
        if (r0 != r6) goto L_0x07bb;
    L_0x0798:
        r65 = r4.getOutputFormat();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x0463;
    L_0x07a0:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6.<init>();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = "newFormat = ";
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r65;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0463;
    L_0x07bb:
        if (r37 >= 0) goto L_0x07d9;
    L_0x07bd:
        r6 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10.<init>();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r11 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r37;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6.<init>(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        throw r6;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x07d9:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 18;
        if (r6 < r10) goto L_0x0895;
    L_0x07df:
        r0 = r53;
        r6 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x0891;
    L_0x07e5:
        r38 = 1;
    L_0x07e7:
        r10 = 0;
        r6 = (r18 > r10 ? 1 : (r18 == r10 ? 0 : -1));
        if (r6 <= 0) goto L_0x0805;
    L_0x07ed:
        r0 = r53;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1));
        if (r6 < 0) goto L_0x0805;
    L_0x07f5:
        r55 = 1;
        r34 = 1;
        r38 = 0;
        r0 = r53;
        r6 = r0.flags;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6 | 4;
        r0 = r53;
        r0.flags = r6;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x0805:
        r10 = 0;
        r6 = (r82 > r10 ? 1 : (r82 == r10 ? 0 : -1));
        if (r6 <= 0) goto L_0x0847;
    L_0x080b:
        r10 = -1;
        r6 = (r92 > r10 ? 1 : (r92 == r10 ? 0 : -1));
        if (r6 != 0) goto L_0x0847;
    L_0x0811:
        r0 = r53;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = (r10 > r82 ? 1 : (r10 == r82 ? 0 : -1));
        if (r6 >= 0) goto L_0x08ac;
    L_0x0819:
        r38 = 0;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x0847;
    L_0x081f:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6.<init>();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = "drop frame startTime = ";
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r82;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = " present time = ";
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r53;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x0847:
        r0 = r37;
        r1 = r38;
        r4.releaseOutputBuffer(r0, r1);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r38 == 0) goto L_0x0872;
    L_0x0850:
        r48 = 0;
        r70.awaitNewImage();	 Catch:{ Exception -> 0x08b3, all -> 0x0528 }
    L_0x0855:
        if (r48 != 0) goto L_0x0872;
    L_0x0857:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 18;
        if (r6 < r10) goto L_0x08ba;
    L_0x085d:
        r6 = 0;
        r0 = r70;
        r0.drawImage(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r0 = r53;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r12 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r10 = r10 * r12;
        r0 = r57;
        r0.setPresentationTime(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r57.swapBuffers();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x0872:
        r0 = r53;
        r6 = r0.flags;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r6 = r6 & 4;
        if (r6 == 0) goto L_0x0463;
    L_0x087a:
        r36 = 0;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x0886;
    L_0x0880:
        r6 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
    L_0x0886:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = 18;
        if (r6 < r10) goto L_0x08fc;
    L_0x088c:
        r41.signalEndOfInputStream();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0463;
    L_0x0891:
        r38 = 0;
        goto L_0x07e7;
    L_0x0895:
        r0 = r53;
        r6 = r0.size;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 != 0) goto L_0x08a5;
    L_0x089b:
        r0 = r53;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r12 = 0;
        r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r6 == 0) goto L_0x08a9;
    L_0x08a5:
        r38 = 1;
    L_0x08a7:
        goto L_0x07e7;
    L_0x08a9:
        r38 = 0;
        goto L_0x08a7;
    L_0x08ac:
        r0 = r53;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r92 = r0;
        goto L_0x0847;
    L_0x08b3:
        r39 = move-exception;
        r48 = 1;
        org.telegram.messenger.FileLog.e(r39);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0855;
    L_0x08ba:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r41;
        r5 = r0.dequeueInputBuffer(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r5 < 0) goto L_0x08f0;
    L_0x08c4:
        r6 = 1;
        r0 = r70;
        r0.drawImage(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r8 = r70.getFrame();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r9 = r42[r5];	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r9.clear();	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r10 = r32;
        r11 = r78;
        r12 = r76;
        r13 = r72;
        r14 = r84;
        org.telegram.messenger.Utilities.convertVideoFrame(r8, r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r12 = 0;
        r0 = r53;
        r14 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r16 = 0;
        r10 = r41;
        r11 = r5;
        r13 = r29;
        r10.queueInputBuffer(r11, r12, r13, r14, r16);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0872;
    L_0x08f0:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r6 == 0) goto L_0x0872;
    L_0x08f4:
        r6 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0872;
    L_0x08fc:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r41;
        r5 = r0.dequeueInputBuffer(r10);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        if (r5 < 0) goto L_0x0463;
    L_0x0906:
        r12 = 0;
        r13 = 1;
        r0 = r53;
        r14 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        r16 = 4;
        r10 = r41;
        r11 = r5;
        r10.queueInputBuffer(r11, r12, r13, r14, r16);	 Catch:{ Exception -> 0x01e5, all -> 0x0528 }
        goto L_0x0463;
    L_0x0916:
        r6 = -1;
        r0 = r28;
        if (r0 == r6) goto L_0x0963;
    L_0x091b:
        r21 = 1;
    L_0x091d:
        r11 = r95;
        r12 = r96;
        r13 = r50;
        r14 = r63;
        r15 = r53;
        r16 = r82;
        r11.readAndWriteTracks(r12, r13, r14, r15, r16, r18, r20, r21);	 Catch:{ Exception -> 0x092e, all -> 0x0528 }
        goto L_0x020f;
    L_0x092e:
        r39 = move-exception;
        r49 = r50;
    L_0x0931:
        r47 = 1;
        org.telegram.messenger.FileLog.e(r39);	 Catch:{ all -> 0x0994 }
        if (r49 == 0) goto L_0x093b;
    L_0x0938:
        r49.release();
    L_0x093b:
        if (r63 == 0) goto L_0x0940;
    L_0x093d:
        r63.finishMovie();	 Catch:{ Exception -> 0x096c }
    L_0x0940:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x023c;
    L_0x0944:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r10 = "time = ";
        r6 = r6.append(r10);
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r86;
        r6 = r6.append(r10);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.d(r6);
        goto L_0x023c;
    L_0x0963:
        r21 = 0;
        goto L_0x091d;
    L_0x0966:
        r39 = move-exception;
        org.telegram.messenger.FileLog.e(r39);
        goto L_0x0219;
    L_0x096c:
        r39 = move-exception;
        org.telegram.messenger.FileLog.e(r39);
        goto L_0x0940;
    L_0x0971:
        r39 = move-exception;
        org.telegram.messenger.FileLog.e(r39);
        goto L_0x0535;
    L_0x0977:
        r6 = r74.edit();
        r10 = "isPreviousOk";
        r11 = 1;
        r6 = r6.putBoolean(r10, r11);
        r6.commit();
        r6 = 1;
        r10 = 1;
        r0 = r95;
        r1 = r96;
        r2 = r20;
        r0.didWriteData(r1, r2, r6, r10);
        r6 = 0;
        goto L_0x0009;
    L_0x0994:
        r6 = move-exception;
        goto L_0x052b;
    L_0x0997:
        r39 = move-exception;
        goto L_0x0931;
    L_0x0999:
        r39 = move-exception;
        r57 = r58;
        goto L_0x01e6;
    L_0x099e:
        r49 = r50;
        goto L_0x023c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MessageObject):boolean");
    }
}
