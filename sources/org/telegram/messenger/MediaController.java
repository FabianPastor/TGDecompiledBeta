package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.net.Uri;
import android.os.Build.VERSION;
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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
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

public class MediaController implements OnAudioFocusChangeListener, NotificationCenterDelegate, SensorEventListener {
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
    private static final String[] projectionPhotos;
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
        /* JADX WARNING: Removed duplicated region for block: B:44:0x0107  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x010a  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x0107  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x010a  */
        public void run() {
            /*
            r18 = this;
            r1 = r18;
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.audioRecorder;
            if (r0 == 0) goto L_0x0142;
        L_0x000a:
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.recordBuffers;
            r0 = r0.isEmpty();
            r2 = 0;
            if (r0 != 0) goto L_0x002d;
        L_0x0017:
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.recordBuffers;
            r0 = r0.get(r2);
            r0 = (java.nio.ByteBuffer) r0;
            r3 = org.telegram.messenger.MediaController.this;
            r3 = r3.recordBuffers;
            r3.remove(r2);
            goto L_0x003e;
        L_0x002d:
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.recordBufferSize;
            r0 = java.nio.ByteBuffer.allocateDirect(r0);
            r3 = java.nio.ByteOrder.nativeOrder();
            r0.order(r3);
        L_0x003e:
            r3 = r0;
            r3.rewind();
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.audioRecorder;
            r4 = r3.capacity();
            r4 = r0.read(r3, r4);
            if (r4 <= 0) goto L_0x0130;
        L_0x0052:
            r3.limit(r4);
            r0 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00ea }
            r7 = r0.samplesCount;	 Catch:{ Exception -> 0x00ea }
            r0 = r4 / 2;
            r9 = (long) r0;	 Catch:{ Exception -> 0x00ea }
            r7 = r7 + r9;
            r0 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00ea }
            r9 = r0.samplesCount;	 Catch:{ Exception -> 0x00ea }
            r9 = (double) r9;
            r11 = (double) r7;
            java.lang.Double.isNaN(r9);
            java.lang.Double.isNaN(r11);
            r9 = r9 / r11;
            r0 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00ea }
            r0 = r0.recordSamples;	 Catch:{ Exception -> 0x00ea }
            r0 = r0.length;	 Catch:{ Exception -> 0x00ea }
            r11 = (double) r0;
            java.lang.Double.isNaN(r11);
            r9 = r9 * r11;
            r0 = (int) r9;
            r9 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00ea }
            r9 = r9.recordSamples;	 Catch:{ Exception -> 0x00ea }
            r9 = r9.length;	 Catch:{ Exception -> 0x00ea }
            r9 = r9 - r0;
            r10 = 0;
            if (r0 == 0) goto L_0x00aa;
        L_0x0087:
            r11 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00ea }
            r11 = r11.recordSamples;	 Catch:{ Exception -> 0x00ea }
            r11 = r11.length;	 Catch:{ Exception -> 0x00ea }
            r11 = (float) r11;	 Catch:{ Exception -> 0x00ea }
            r12 = (float) r0;	 Catch:{ Exception -> 0x00ea }
            r11 = r11 / r12;
            r12 = 0;
            r13 = 0;
        L_0x0093:
            if (r12 >= r0) goto L_0x00aa;
        L_0x0095:
            r14 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00ea }
            r14 = r14.recordSamples;	 Catch:{ Exception -> 0x00ea }
            r15 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00ea }
            r15 = r15.recordSamples;	 Catch:{ Exception -> 0x00ea }
            r5 = (int) r13;	 Catch:{ Exception -> 0x00ea }
            r5 = r15[r5];	 Catch:{ Exception -> 0x00ea }
            r14[r12] = r5;	 Catch:{ Exception -> 0x00ea }
            r13 = r13 + r11;
            r12 = r12 + 1;
            goto L_0x0093;
        L_0x00aa:
            r5 = (float) r4;
            r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r5 = r5 / r6;
            r6 = (float) r9;
            r5 = r5 / r6;
            r6 = r0;
            r0 = 0;
            r16 = 0;
        L_0x00b4:
            r9 = r4 / 2;
            if (r0 >= r9) goto L_0x00e2;
        L_0x00b8:
            r9 = r3.getShort();	 Catch:{ Exception -> 0x00e8 }
            r11 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
            if (r9 <= r11) goto L_0x00c8;
        L_0x00c0:
            r11 = r9 * r9;
            r11 = (double) r11;
            java.lang.Double.isNaN(r11);
            r16 = r16 + r11;
        L_0x00c8:
            r11 = (int) r10;
            if (r0 != r11) goto L_0x00df;
        L_0x00cb:
            r11 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00e8 }
            r11 = r11.recordSamples;	 Catch:{ Exception -> 0x00e8 }
            r11 = r11.length;	 Catch:{ Exception -> 0x00e8 }
            if (r6 >= r11) goto L_0x00df;
        L_0x00d4:
            r11 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00e8 }
            r11 = r11.recordSamples;	 Catch:{ Exception -> 0x00e8 }
            r11[r6] = r9;	 Catch:{ Exception -> 0x00e8 }
            r10 = r10 + r5;
            r6 = r6 + 1;
        L_0x00df:
            r0 = r0 + 1;
            goto L_0x00b4;
        L_0x00e2:
            r0 = org.telegram.messenger.MediaController.this;	 Catch:{ Exception -> 0x00e8 }
            r0.samplesCount = r7;	 Catch:{ Exception -> 0x00e8 }
            goto L_0x00f0;
        L_0x00e8:
            r0 = move-exception;
            goto L_0x00ed;
        L_0x00ea:
            r0 = move-exception;
            r16 = 0;
        L_0x00ed:
            org.telegram.messenger.FileLog.e(r0);
        L_0x00f0:
            r3.position(r2);
            r5 = (double) r4;
            java.lang.Double.isNaN(r5);
            r16 = r16 / r5;
            r5 = NUM; // 0xNUM float:0.0 double:2.0;
            r16 = r16 / r5;
            r5 = java.lang.Math.sqrt(r16);
            r0 = r3.capacity();
            if (r4 == r0) goto L_0x0108;
        L_0x0107:
            r2 = 1;
        L_0x0108:
            if (r4 == 0) goto L_0x0118;
        L_0x010a:
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.fileEncodingQueue;
            r4 = new org.telegram.messenger.-$$Lambda$MediaController$2$FRC3BsVbfkarri1fq2pbdFf8in8;
            r4.<init>(r1, r3, r2);
            r0.postRunnable(r4);
        L_0x0118:
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.recordQueue;
            r2 = org.telegram.messenger.MediaController.this;
            r2 = r2.recordRunnable;
            r0.postRunnable(r2);
            r0 = new org.telegram.messenger.-$$Lambda$MediaController$2$c_rZm06Vbt5svAWMsWH6lgBDhHI;
            r0.<init>(r1, r5);
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
            goto L_0x0142;
        L_0x0130:
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.recordBuffers;
            r0.add(r3);
            r0 = org.telegram.messenger.MediaController.this;
            r2 = r0.sendAfterDone;
            r0.stopRecordingInternal(r2);
        L_0x0142:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController$AnonymousClass2.run():void");
        }

        public /* synthetic */ void lambda$run$1$MediaController$2(ByteBuffer byteBuffer, boolean z) {
            while (byteBuffer.hasRemaining()) {
                int limit;
                if (byteBuffer.remaining() > MediaController.this.fileBuffer.remaining()) {
                    limit = byteBuffer.limit();
                    byteBuffer.limit(MediaController.this.fileBuffer.remaining() + byteBuffer.position());
                } else {
                    limit = -1;
                }
                MediaController.this.fileBuffer.put(byteBuffer);
                if (MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit() || z) {
                    MediaController mediaController = MediaController.this;
                    if (mediaController.writeFrame(mediaController.fileBuffer, !z ? MediaController.this.fileBuffer.limit() : byteBuffer.position()) != 0) {
                        MediaController.this.fileBuffer.rewind();
                        mediaController = MediaController.this;
                        mediaController.recordTimeCount = mediaController.recordTimeCount + ((long) ((MediaController.this.fileBuffer.limit() / 2) / 16));
                    }
                }
                if (limit != -1) {
                    byteBuffer.limit(limit);
                }
            }
            MediaController.this.recordQueue.postRunnable(new -$$Lambda$MediaController$2$nwth1UltC3xjlr4YyG_2LuvmB6Q(this, byteBuffer));
        }

        public /* synthetic */ void lambda$null$0$MediaController$2(ByteBuffer byteBuffer) {
            MediaController.this.recordBuffers.add(byteBuffer);
        }

        public /* synthetic */ void lambda$run$2$MediaController$2(double d) {
            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(d));
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
        public GalleryObserverExternal() {
            super(null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            -$$Lambda$MediaController$GalleryObserverExternal$dwi1SqFQz-7StR-lsnN0S1Sqd6M -__lambda_mediacontroller_galleryobserverexternal_dwi1sqfqz-7str-lsnn0s1sqd6m = -$$Lambda$MediaController$GalleryObserverExternal$dwi1SqFQz-7StR-lsnN0S1Sqd6M.INSTANCE;
            MediaController.refreshGalleryRunnable = -__lambda_mediacontroller_galleryobserverexternal_dwi1sqfqz-7str-lsnn0s1sqd6m;
            AndroidUtilities.runOnUIThread(-__lambda_mediacontroller_galleryobserverexternal_dwi1sqfqz-7str-lsnn0s1sqd6m, 2000);
        }

        static /* synthetic */ void lambda$onChange$0() {
            MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    private class GalleryObserverInternal extends ContentObserver {
        public GalleryObserverInternal() {
            super(null);
        }

        private void scheduleReloadRunnable() {
            -$$Lambda$MediaController$GalleryObserverInternal$1zsHrCjjtoIwj0Amhv_aO54Om6g -__lambda_mediacontroller_galleryobserverinternal_1zshrcjjtoiwj0amhv_ao54om6g = new -$$Lambda$MediaController$GalleryObserverInternal$1zsHrCjjtoIwj0Amhv_aO54Om6g(this);
            MediaController.refreshGalleryRunnable = -__lambda_mediacontroller_galleryobserverinternal_1zshrcjjtoiwj0amhv_ao54om6g;
            AndroidUtilities.runOnUIThread(-__lambda_mediacontroller_galleryobserverinternal_1zshrcjjtoiwj0amhv_ao54om6g, 2000);
        }

        public /* synthetic */ void lambda$scheduleReloadRunnable$0$MediaController$GalleryObserverInternal() {
            if (PhotoViewer.getInstance().isVisible()) {
                scheduleReloadRunnable();
                return;
            }
            MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
        }

        public void onChange(boolean z) {
            super.onChange(z);
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

        public void onChange(boolean z) {
            super.onChange(z);
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
            PhotoSize photoSize = this.photoSize;
            if (photoSize != null) {
                return FileLoader.getAttachFileName(photoSize);
            }
            Document document = this.document;
            if (document != null) {
                return FileLoader.getAttachFileName(document);
            }
            if (this.type != 1) {
                String str = this.localUrl;
                if (str != null && str.length() > 0) {
                    File file = new File(this.localUrl);
                    if (file.exists()) {
                        return file.getName();
                    }
                    this.localUrl = "";
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Utilities.MD5(this.imageUrl));
            stringBuilder.append(".");
            stringBuilder.append(ImageLoader.getHttpUrlExtension(this.imageUrl, "jpg"));
            return stringBuilder.toString();
        }

        public String getPathToAttach() {
            PhotoSize photoSize = this.photoSize;
            if (photoSize != null) {
                return FileLoader.getPathToAttach(photoSize, true).getAbsolutePath();
            }
            Document document = this.document;
            if (document != null) {
                return FileLoader.getPathToAttach(document, true).getAbsolutePath();
            }
            return this.imageUrl;
        }
    }

    private final class StopMediaObserverRunnable implements Runnable {
        public int currentObserverToken;

        private StopMediaObserverRunnable() {
            this.currentObserverToken = 0;
        }

        /* synthetic */ StopMediaObserverRunnable(MediaController mediaController, AnonymousClass1 anonymousClass1) {
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

        private VideoConvertRunnable(MessageObject messageObject) {
            this.messageObject = messageObject;
        }

        public void run() {
            MediaController.getInstance().convertVideo(this.messageObject);
        }

        public static void runConversion(MessageObject messageObject) {
            new Thread(new -$$Lambda$MediaController$VideoConvertRunnable$HHYJEBFJxn1b5hYjDjNcGHVKJ_Y(messageObject)).start();
        }

        static /* synthetic */ void lambda$runConversion$0(MessageObject messageObject) {
            try {
                Thread thread = new Thread(new VideoConvertRunnable(messageObject), "VideoConvertRunnable");
                thread.start();
                thread.join();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

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

    private native int startRecord(String str);

    private native void stopRecord();

    private native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    static {
        r1 = new String[6];
        r1[0] = "_id";
        r1[1] = "bucket_id";
        r1[2] = "bucket_display_name";
        r1[3] = "_data";
        r1[4] = "datetaken";
        r1[5] = "orientation";
        projectionPhotos = r1;
    }

    public static void checkGallery() {
        if (VERSION.SDK_INT >= 24) {
            AlbumEntry albumEntry = allPhotosAlbumEntry;
            if (albumEntry != null) {
                Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$-5Ec-d3Vawho174Y-PrhWoRflEw(albumEntry.photos.size()), 2000);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x004f A:{Catch:{ Throwable -> 0x0077, all -> 0x0075 }} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x004f A:{Catch:{ Throwable -> 0x0077, all -> 0x0075 }} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x004f A:{Catch:{ Throwable -> 0x0077, all -> 0x0075 }} */
    /* JADX WARNING: Missing block: B:33:0x006f, code skipped:
            if (r5 == null) goto L_0x007e;
     */
    /* JADX WARNING: Missing block: B:34:0x0071, code skipped:
            r5.close();
     */
    /* JADX WARNING: Missing block: B:39:0x007b, code skipped:
            if (r5 == null) goto L_0x007e;
     */
    /* JADX WARNING: Missing block: B:40:0x007e, code skipped:
            if (r13 == r6) goto L_?;
     */
    /* JADX WARNING: Missing block: B:41:0x0080, code skipped:
            r13 = refreshGalleryRunnable;
     */
    /* JADX WARNING: Missing block: B:42:0x0082, code skipped:
            if (r13 == null) goto L_0x0089;
     */
    /* JADX WARNING: Missing block: B:43:0x0084, code skipped:
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r13);
            refreshGalleryRunnable = null;
     */
    /* JADX WARNING: Missing block: B:44:0x0089, code skipped:
            loadGalleryPhotosAlbums(0);
     */
    /* JADX WARNING: Missing block: B:53:?, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:54:?, code skipped:
            return;
     */
    static /* synthetic */ void lambda$checkGallery$0(int r13) {
        /*
        r0 = "COUNT(_id)";
        r1 = "android.permission.READ_EXTERNAL_STORAGE";
        r2 = 1;
        r3 = 0;
        r4 = 0;
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x003c, all -> 0x003a }
        r5 = r5.checkSelfPermission(r1);	 Catch:{ Throwable -> 0x003c, all -> 0x003a }
        if (r5 != 0) goto L_0x0032;
    L_0x000f:
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x003c, all -> 0x003a }
        r6 = r5.getContentResolver();	 Catch:{ Throwable -> 0x003c, all -> 0x003a }
        r7 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x003c, all -> 0x003a }
        r8 = new java.lang.String[r2];	 Catch:{ Throwable -> 0x003c, all -> 0x003a }
        r8[r4] = r0;	 Catch:{ Throwable -> 0x003c, all -> 0x003a }
        r9 = 0;
        r10 = 0;
        r11 = 0;
        r5 = android.provider.MediaStore.Images.Media.query(r6, r7, r8, r9, r10, r11);	 Catch:{ Throwable -> 0x003c, all -> 0x003a }
        if (r5 == 0) goto L_0x0033;
    L_0x0024:
        r6 = r5.moveToNext();	 Catch:{ Throwable -> 0x0030 }
        if (r6 == 0) goto L_0x0033;
    L_0x002a:
        r6 = r5.getInt(r4);	 Catch:{ Throwable -> 0x0030 }
        r6 = r6 + r4;
        goto L_0x0034;
    L_0x0030:
        r6 = move-exception;
        goto L_0x003e;
    L_0x0032:
        r5 = r3;
    L_0x0033:
        r6 = 0;
    L_0x0034:
        if (r5 == 0) goto L_0x0047;
    L_0x0036:
        r5.close();
        goto L_0x0047;
    L_0x003a:
        r13 = move-exception;
        goto L_0x0095;
    L_0x003c:
        r6 = move-exception;
        r5 = r3;
    L_0x003e:
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ all -> 0x0093 }
        if (r5 == 0) goto L_0x0046;
    L_0x0043:
        r5.close();
    L_0x0046:
        r6 = 0;
    L_0x0047:
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0077 }
        r1 = r7.checkSelfPermission(r1);	 Catch:{ Throwable -> 0x0077 }
        if (r1 != 0) goto L_0x006f;
    L_0x004f:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0077 }
        r7 = r1.getContentResolver();	 Catch:{ Throwable -> 0x0077 }
        r8 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x0077 }
        r9 = new java.lang.String[r2];	 Catch:{ Throwable -> 0x0077 }
        r9[r4] = r0;	 Catch:{ Throwable -> 0x0077 }
        r10 = 0;
        r11 = 0;
        r12 = 0;
        r5 = android.provider.MediaStore.Images.Media.query(r7, r8, r9, r10, r11, r12);	 Catch:{ Throwable -> 0x0077 }
        if (r5 == 0) goto L_0x006f;
    L_0x0064:
        r0 = r5.moveToNext();	 Catch:{ Throwable -> 0x0077 }
        if (r0 == 0) goto L_0x006f;
    L_0x006a:
        r0 = r5.getInt(r4);	 Catch:{ Throwable -> 0x0077 }
        r6 = r6 + r0;
    L_0x006f:
        if (r5 == 0) goto L_0x007e;
    L_0x0071:
        r5.close();
        goto L_0x007e;
    L_0x0075:
        r13 = move-exception;
        goto L_0x008d;
    L_0x0077:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0075 }
        if (r5 == 0) goto L_0x007e;
    L_0x007d:
        goto L_0x0071;
    L_0x007e:
        if (r13 == r6) goto L_0x008c;
    L_0x0080:
        r13 = refreshGalleryRunnable;
        if (r13 == 0) goto L_0x0089;
    L_0x0084:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r13);
        refreshGalleryRunnable = r3;
    L_0x0089:
        loadGalleryPhotosAlbums(r4);
    L_0x008c:
        return;
    L_0x008d:
        if (r5 == 0) goto L_0x0092;
    L_0x008f:
        r5.close();
    L_0x0092:
        throw r13;
    L_0x0093:
        r13 = move-exception;
        r3 = r5;
    L_0x0095:
        if (r3 == 0) goto L_0x009a;
    L_0x0097:
        r3.close();
    L_0x009a:
        goto L_0x009c;
    L_0x009b:
        throw r13;
    L_0x009c:
        goto L_0x009b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$checkGallery$0(int):void");
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
        this.recordQueue.postRunnable(new -$$Lambda$MediaController$qSDZPxerqEszkXBWWApUdhxsISQ(this));
        Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$FaCBRlQgZxu6gGs04ETW1GD6ZDk(this));
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$Jq_ZASoLiwPvRrenXbD34k0cp8A(this));
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
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public /* synthetic */ void lambda$new$1$MediaController() {
        try {
            this.recordBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
            if (this.recordBufferSize <= 0) {
                this.recordBufferSize = 1280;
            }
            for (int i = 0; i < 5; i++) {
                ByteBuffer allocateDirect = ByteBuffer.allocateDirect(4096);
                allocateDirect.order(ByteOrder.nativeOrder());
                this.recordBuffers.add(allocateDirect);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$new$2$MediaController() {
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
            AnonymousClass3 anonymousClass3 = new PhoneStateListener() {
                public void onCallStateChanged(int i, String str) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$3$kfHEHMBmovTxgGbvrlQqhhaeP5A(this, i));
                }

                public /* synthetic */ void lambda$onCallStateChanged$0$MediaController$3(int i) {
                    EmbedBottomSheet instance;
                    if (i == 1) {
                        MediaController mediaController = MediaController.this;
                        if (mediaController.isPlayingMessage(mediaController.playingMessageObject) && !MediaController.this.isMessagePaused()) {
                            mediaController = MediaController.this;
                            mediaController.lambda$startAudioAgain$5$MediaController(mediaController.playingMessageObject);
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
            };
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                telephonyManager.listen(anonymousClass3, 32);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public /* synthetic */ void lambda$new$3$MediaController() {
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.didReceiveNewMessages);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.musicDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    public void onAudioFocusChange(int i) {
        if (i == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
            }
            this.hasAudioFocus = 0;
            this.audioFocus = 0;
        } else if (i == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                if (isPlayingMessage(getPlayingMessageObject()) && isMessagePaused()) {
                    playMessage(getPlayingMessageObject());
                }
            }
        } else if (i == -3) {
            this.audioFocus = 1;
        } else if (i == -2) {
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
            float f = this.audioFocus != 1 ? 1.0f : 0.2f;
            if (this.audioPlayer != null) {
                this.audioPlayer.setVolume(f);
            } else if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(f);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void startProgressTimer(final MessageObject messageObject) {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            messageObject.getFileName();
            this.progressTimer = new Timer();
            this.progressTimer.schedule(new TimerTask() {
                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$4$r0MGx90x6zCLeWW-3FGDHLnfRxc(this, messageObject));
                    }
                }

                public /* synthetic */ void lambda$run$0$MediaController$4(MessageObject messageObject) {
                    if (!(messageObject == null || ((MediaController.this.audioPlayer == null && MediaController.this.videoPlayer == null) || MediaController.this.isPaused))) {
                        try {
                            long duration;
                            long currentPosition;
                            float bufferedPosition;
                            float f;
                            float f2 = 0.0f;
                            if (MediaController.this.videoPlayer != null) {
                                duration = MediaController.this.videoPlayer.getDuration();
                                currentPosition = MediaController.this.videoPlayer.getCurrentPosition();
                                if (!(duration == -9223372036854775807L || currentPosition == -9223372036854775807L || currentPosition < 0)) {
                                    if (duration > 0) {
                                        float f3 = (float) duration;
                                        bufferedPosition = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / f3;
                                        if (duration >= 0) {
                                            f2 = ((float) currentPosition) / f3;
                                        }
                                        if (f2 < 1.0f) {
                                            f = bufferedPosition;
                                            bufferedPosition = f2;
                                        } else {
                                            return;
                                        }
                                    }
                                }
                                return;
                            }
                            duration = MediaController.this.audioPlayer.getDuration();
                            currentPosition = MediaController.this.audioPlayer.getCurrentPosition();
                            bufferedPosition = (duration == -9223372036854775807L || duration < 0) ? 0.0f : ((float) currentPosition) / ((float) duration);
                            f = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) duration);
                            if (duration != -9223372036854775807L && currentPosition >= 0) {
                                if (MediaController.this.seekToProgressPending != 0.0f) {
                                }
                            }
                            return;
                            MediaController.this.lastProgress = currentPosition;
                            messageObject.audioPlayerDuration = (int) (duration / 1000);
                            messageObject.audioProgress = bufferedPosition;
                            messageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                            messageObject.bufferedProgress = f;
                            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(messageObject.getId()), Float.valueOf(bufferedPosition));
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:37:0x00a7 */
    /* JADX WARNING: Can't wrap try/catch for region: R(3:37|38|64) */
    /* JADX WARNING: Missing block: B:11:0x0052, code skipped:
            if (r2.toLowerCase().contains(r11) == false) goto L_0x0054;
     */
    /* JADX WARNING: Missing block: B:14:0x005e, code skipped:
            if (r4.toLowerCase().contains(r11) != false) goto L_0x0078;
     */
    /* JADX WARNING: Missing block: B:17:0x006a, code skipped:
            if (r5.toLowerCase().contains(r11) != false) goto L_0x0078;
     */
    /* JADX WARNING: Missing block: B:38:?, code skipped:
            r14.add(java.lang.Long.valueOf(r6));
     */
    private void processMediaObserver(android.net.Uri r14) {
        /*
        r13 = this;
        r0 = 0;
        r1 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();	 Catch:{ Exception -> 0x00ca }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00ca }
        r3 = r2.getContentResolver();	 Catch:{ Exception -> 0x00ca }
        r5 = r13.mediaProjections;	 Catch:{ Exception -> 0x00ca }
        r6 = 0;
        r7 = 0;
        r8 = "date_added DESC LIMIT 1";
        r4 = r14;
        r0 = r3.query(r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x00ca }
        r14 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00ca }
        r14.<init>();	 Catch:{ Exception -> 0x00ca }
        if (r0 == 0) goto L_0x00b3;
    L_0x001d:
        r2 = r0.moveToNext();	 Catch:{ Exception -> 0x00ca }
        if (r2 == 0) goto L_0x00b0;
    L_0x0023:
        r2 = 0;
        r2 = r0.getString(r2);	 Catch:{ Exception -> 0x00ca }
        r3 = 1;
        r4 = r0.getString(r3);	 Catch:{ Exception -> 0x00ca }
        r5 = 2;
        r5 = r0.getString(r5);	 Catch:{ Exception -> 0x00ca }
        r6 = 3;
        r6 = r0.getLong(r6);	 Catch:{ Exception -> 0x00ca }
        r8 = 4;
        r8 = r0.getString(r8);	 Catch:{ Exception -> 0x00ca }
        r9 = 5;
        r9 = r0.getInt(r9);	 Catch:{ Exception -> 0x00ca }
        r10 = 6;
        r10 = r0.getInt(r10);	 Catch:{ Exception -> 0x00ca }
        r11 = "screenshot";
        if (r2 == 0) goto L_0x0054;
    L_0x004a:
        r12 = r2.toLowerCase();	 Catch:{ Exception -> 0x00ca }
        r12 = r12.contains(r11);	 Catch:{ Exception -> 0x00ca }
        if (r12 != 0) goto L_0x0078;
    L_0x0054:
        if (r4 == 0) goto L_0x0060;
    L_0x0056:
        r4 = r4.toLowerCase();	 Catch:{ Exception -> 0x00ca }
        r4 = r4.contains(r11);	 Catch:{ Exception -> 0x00ca }
        if (r4 != 0) goto L_0x0078;
    L_0x0060:
        if (r5 == 0) goto L_0x006c;
    L_0x0062:
        r4 = r5.toLowerCase();	 Catch:{ Exception -> 0x00ca }
        r4 = r4.contains(r11);	 Catch:{ Exception -> 0x00ca }
        if (r4 != 0) goto L_0x0078;
    L_0x006c:
        if (r8 == 0) goto L_0x001d;
    L_0x006e:
        r4 = r8.toLowerCase();	 Catch:{ Exception -> 0x00ca }
        r4 = r4.contains(r11);	 Catch:{ Exception -> 0x00ca }
        if (r4 == 0) goto L_0x001d;
    L_0x0078:
        if (r9 == 0) goto L_0x007c;
    L_0x007a:
        if (r10 != 0) goto L_0x008a;
    L_0x007c:
        r4 = new android.graphics.BitmapFactory$Options;	 Catch:{ Exception -> 0x00a7 }
        r4.<init>();	 Catch:{ Exception -> 0x00a7 }
        r4.inJustDecodeBounds = r3;	 Catch:{ Exception -> 0x00a7 }
        android.graphics.BitmapFactory.decodeFile(r2, r4);	 Catch:{ Exception -> 0x00a7 }
        r9 = r4.outWidth;	 Catch:{ Exception -> 0x00a7 }
        r10 = r4.outHeight;	 Catch:{ Exception -> 0x00a7 }
    L_0x008a:
        if (r9 <= 0) goto L_0x009e;
    L_0x008c:
        if (r10 <= 0) goto L_0x009e;
    L_0x008e:
        r2 = r1.x;	 Catch:{ Exception -> 0x00a7 }
        if (r9 != r2) goto L_0x0096;
    L_0x0092:
        r2 = r1.y;	 Catch:{ Exception -> 0x00a7 }
        if (r10 == r2) goto L_0x009e;
    L_0x0096:
        r2 = r1.x;	 Catch:{ Exception -> 0x00a7 }
        if (r10 != r2) goto L_0x001d;
    L_0x009a:
        r2 = r1.y;	 Catch:{ Exception -> 0x00a7 }
        if (r9 != r2) goto L_0x001d;
    L_0x009e:
        r2 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x00a7 }
        r14.add(r2);	 Catch:{ Exception -> 0x00a7 }
        goto L_0x001d;
    L_0x00a7:
        r2 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x00ca }
        r14.add(r2);	 Catch:{ Exception -> 0x00ca }
        goto L_0x001d;
    L_0x00b0:
        r0.close();	 Catch:{ Exception -> 0x00ca }
    L_0x00b3:
        r1 = r14.isEmpty();	 Catch:{ Exception -> 0x00ca }
        if (r1 != 0) goto L_0x00c4;
    L_0x00b9:
        r1 = new org.telegram.messenger.-$$Lambda$MediaController$M715dCmB5sndyTLyXH8F6AQFBc4;	 Catch:{ Exception -> 0x00ca }
        r1.<init>(r13, r14);	 Catch:{ Exception -> 0x00ca }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ Exception -> 0x00ca }
        goto L_0x00c4;
    L_0x00c2:
        r14 = move-exception;
        goto L_0x00d2;
    L_0x00c4:
        if (r0 == 0) goto L_0x00d1;
    L_0x00c6:
        r0.close();	 Catch:{ Exception -> 0x00d1 }
        goto L_0x00d1;
    L_0x00ca:
        r14 = move-exception;
        org.telegram.messenger.FileLog.e(r14);	 Catch:{ all -> 0x00c2 }
        if (r0 == 0) goto L_0x00d1;
    L_0x00d0:
        goto L_0x00c6;
    L_0x00d1:
        return;
    L_0x00d2:
        if (r0 == 0) goto L_0x00d7;
    L_0x00d4:
        r0.close();	 Catch:{ Exception -> 0x00d7 }
    L_0x00d7:
        goto L_0x00d9;
    L_0x00d8:
        throw r14;
    L_0x00d9:
        goto L_0x00d8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.processMediaObserver(android.net.Uri):void");
    }

    public /* synthetic */ void lambda$processMediaObserver$4$MediaController(ArrayList arrayList) {
        NotificationCenter.getInstance(this.lastChatAccount).postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
        checkScreenshots(arrayList);
    }

    private void checkScreenshots(ArrayList<Long> arrayList) {
        if (arrayList != null && !arrayList.isEmpty() && this.lastChatEnterTime != 0) {
            if (this.lastUser != null || (this.lastSecretChat instanceof TL_encryptedChat)) {
                Object obj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    Long l = (Long) arrayList.get(i);
                    if ((this.lastMediaCheckTime == 0 || l.longValue() > this.lastMediaCheckTime) && l.longValue() >= this.lastChatEnterTime && (this.lastChatLeaveTime == 0 || l.longValue() <= this.lastChatLeaveTime + 2000)) {
                        this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, l.longValue());
                        obj = 1;
                    }
                }
                if (obj == null) {
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
        MessageObject messageObject;
        long longValue;
        ArrayList arrayList;
        if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.httpFileDidLoad) {
            String str = (String) objArr[0];
            if (this.downloadingCurrentMessage) {
                messageObject = this.playingMessageObject;
                if (messageObject != null && messageObject.currentAccount == i2 && FileLoader.getAttachFileName(messageObject.getDocument()).equals(str)) {
                    this.playMusicAgain = true;
                    playMessage(this.playingMessageObject);
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            i = ((Integer) objArr[1]).intValue();
            ArrayList arrayList2 = (ArrayList) objArr[0];
            messageObject = this.playingMessageObject;
            if (messageObject != null && i == messageObject.messageOwner.to_id.channel_id && arrayList2.contains(Integer.valueOf(messageObject.getId()))) {
                cleanupPlayer(true, true);
            }
            ArrayList arrayList3 = this.voiceMessagesPlaylist;
            if (arrayList3 != null && !arrayList3.isEmpty() && i == ((MessageObject) this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id) {
                while (i3 < arrayList2.size()) {
                    Integer num = (Integer) arrayList2.get(i3);
                    messageObject = (MessageObject) this.voiceMessagesPlaylistMap.get(num.intValue());
                    this.voiceMessagesPlaylistMap.remove(num.intValue());
                    if (messageObject != null) {
                        this.voiceMessagesPlaylist.remove(messageObject);
                    }
                    i3++;
                }
            }
        } else if (i == NotificationCenter.removeAllMessagesFromDialog) {
            longValue = ((Long) objArr[0]).longValue();
            messageObject = this.playingMessageObject;
            if (messageObject != null && messageObject.getDialogId() == longValue) {
                cleanupPlayer(false, true);
            }
        } else if (i == NotificationCenter.musicDidLoad) {
            longValue = ((Long) objArr[0]).longValue();
            MessageObject messageObject2 = this.playingMessageObject;
            if (messageObject2 != null && messageObject2.isMusic() && this.playingMessageObject.getDialogId() == longValue) {
                arrayList = (ArrayList) objArr[1];
                this.playlist.addAll(0, arrayList);
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                    this.currentPlaylistNum = 0;
                    return;
                }
                this.currentPlaylistNum += arrayList.size();
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            arrayList = this.voiceMessagesPlaylist;
            if (arrayList != null && !arrayList.isEmpty()) {
                if (((Long) objArr[0]).longValue() == ((MessageObject) this.voiceMessagesPlaylist.get(0)).getDialogId()) {
                    arrayList = (ArrayList) objArr[1];
                    while (i3 < arrayList.size()) {
                        MessageObject messageObject3 = (MessageObject) arrayList.get(i3);
                        if ((messageObject3.isVoice() || messageObject3.isRoundVideo()) && (!this.voiceMessagesPlaylistUnread || (messageObject3.isContentUnread() && !messageObject3.isOut()))) {
                            this.voiceMessagesPlaylist.add(messageObject3);
                            this.voiceMessagesPlaylistMap.put(messageObject3.getId(), messageObject3);
                        }
                        i3++;
                    }
                }
            }
        } else if (i == NotificationCenter.playerDidStartPlaying) {
            if (!getInstance().isCurrentPlayer((VideoPlayer) objArr[0])) {
                getInstance().lambda$startAudioAgain$5$MediaController(getInstance().getPlayingMessageObject());
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean isRecordingAudio() {
        return (this.recordStartRunnable == null && this.recordingAudio == null) ? false : true;
    }

    private boolean isNearToSensor(float f) {
        return f < 5.0f && f != this.proximitySensor.getMaximumRange();
    }

    /* JADX WARNING: Missing block: B:9:0x001a, code skipped:
            if (r1.playingMessageObject.isRoundVideo() != false) goto L_0x001c;
     */
    public boolean isRecordingOrListeningByProximity() {
        /*
        r1 = this;
        r0 = r1.proximityTouched;
        if (r0 == 0) goto L_0x001e;
    L_0x0004:
        r0 = r1.isRecordingAudio();
        if (r0 != 0) goto L_0x001c;
    L_0x000a:
        r0 = r1.playingMessageObject;
        if (r0 == 0) goto L_0x001e;
    L_0x000e:
        r0 = r0.isVoice();
        if (r0 != 0) goto L_0x001c;
    L_0x0014:
        r0 = r1.playingMessageObject;
        r0 = r0.isRoundVideo();
        if (r0 == 0) goto L_0x001e;
    L_0x001c:
        r0 = 1;
        goto L_0x001f;
    L_0x001e:
        r0 = 0;
    L_0x001f:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.isRecordingOrListeningByProximity():boolean");
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        SensorEvent sensorEvent2 = sensorEvent;
        if (this.sensorsStarted && VoIPService.getSharedInstance() == null) {
            float f;
            float[] fArr;
            boolean z;
            Sensor sensor = sensorEvent2.sensor;
            float[] fArr2;
            float[] fArr3;
            if (sensor == this.proximitySensor) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("proximity changed to ");
                    stringBuilder.append(sensorEvent2.values[0]);
                    stringBuilder.append(" max value = ");
                    stringBuilder.append(this.proximitySensor.getMaximumRange());
                    FileLog.d(stringBuilder.toString());
                }
                f = this.lastProximityValue;
                if (f == -100.0f) {
                    this.lastProximityValue = sensorEvent2.values[0];
                } else if (f != sensorEvent2.values[0]) {
                    this.proximityHasDifferentValues = true;
                }
                if (this.proximityHasDifferentValues) {
                    this.proximityTouched = isNearToSensor(sensorEvent2.values[0]);
                }
            } else if (sensor == this.accelerometerSensor) {
                double d;
                long j = this.lastTimestamp;
                if (j == 0) {
                    d = 0.9800000190734863d;
                } else {
                    d = (double) (sensorEvent2.timestamp - j);
                    Double.isNaN(d);
                    d = 1.0d / ((d / 1.0E9d) + 1.0d);
                }
                this.lastTimestamp = sensorEvent2.timestamp;
                float[] fArr4 = this.gravity;
                double d2 = (double) fArr4[0];
                Double.isNaN(d2);
                d2 *= d;
                double d3 = 1.0d - d;
                float[] fArr5 = sensorEvent2.values;
                double d4 = (double) fArr5[0];
                Double.isNaN(d4);
                fArr4[0] = (float) (d2 + (d4 * d3));
                d4 = (double) fArr4[1];
                Double.isNaN(d4);
                d4 *= d;
                d2 = (double) fArr5[1];
                Double.isNaN(d2);
                fArr4[1] = (float) (d4 + (d2 * d3));
                d4 = (double) fArr4[2];
                Double.isNaN(d4);
                d *= d4;
                d4 = (double) fArr5[2];
                Double.isNaN(d4);
                fArr4[2] = (float) (d + (d3 * d4));
                fArr2 = this.gravityFast;
                fArr2[0] = (fArr4[0] * 0.8f) + (fArr5[0] * 0.19999999f);
                fArr2[1] = (fArr4[1] * 0.8f) + (fArr5[1] * 0.19999999f);
                fArr2[2] = (fArr4[2] * 0.8f) + (fArr5[2] * 0.19999999f);
                fArr2 = this.linearAcceleration;
                fArr2[0] = fArr5[0] - fArr4[0];
                fArr2[1] = fArr5[1] - fArr4[1];
                fArr2[2] = fArr5[2] - fArr4[2];
            } else if (sensor == this.linearSensor) {
                fArr2 = this.linearAcceleration;
                fArr3 = sensorEvent2.values;
                fArr2[0] = fArr3[0];
                fArr2[1] = fArr3[1];
                fArr2[2] = fArr3[2];
            } else if (sensor == this.gravitySensor) {
                fArr2 = this.gravityFast;
                fArr3 = this.gravity;
                fArr = sensorEvent2.values;
                float f2 = fArr[0];
                fArr3[0] = f2;
                fArr2[0] = f2;
                f2 = fArr[1];
                fArr3[1] = f2;
                fArr2[1] = f2;
                float f3 = fArr[2];
                fArr3[2] = f3;
                fArr2[2] = f3;
            }
            Sensor sensor2 = sensorEvent2.sensor;
            if (sensor2 == this.linearSensor || sensor2 == this.gravitySensor || sensor2 == this.accelerometerSensor) {
                float[] fArr6 = this.gravity;
                f = fArr6[0];
                fArr = this.linearAcceleration;
                f = ((f * fArr[0]) + (fArr6[1] * fArr[1])) + (fArr6[2] * fArr[2]);
                if (this.raisedToBack != 6 && ((f > 0.0f && this.previousAccValue > 0.0f) || (f < 0.0f && this.previousAccValue < 0.0f))) {
                    Object obj;
                    int i;
                    if (f > 0.0f) {
                        obj = f > 15.0f ? 1 : null;
                        i = 1;
                    } else {
                        obj = f < -15.0f ? 1 : null;
                        i = 2;
                    }
                    int i2 = this.raisedToTopSign;
                    if (i2 == 0 || i2 == i) {
                        if (obj != null && this.raisedToBack == 0) {
                            i2 = this.raisedToTopSign;
                            if (i2 == 0 || i2 == i) {
                                int i3 = this.raisedToTop;
                                if (i3 < 6 && !this.proximityTouched) {
                                    this.raisedToTopSign = i;
                                    this.raisedToTop = i3 + 1;
                                    if (this.raisedToTop == 6) {
                                        this.countLess = 0;
                                    }
                                }
                            }
                        }
                        if (obj == null) {
                            this.countLess++;
                        }
                        if (!(this.raisedToTopSign == i && this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                            this.raisedToBack = 0;
                            this.raisedToTop = 0;
                            this.raisedToTopSign = 0;
                            this.countLess = 0;
                        }
                    } else if (this.raisedToTop != 6 || obj == null) {
                        if (obj == null) {
                            this.countLess++;
                        }
                        if (!(this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                            this.raisedToTop = 0;
                            this.raisedToTopSign = 0;
                            this.raisedToBack = 0;
                            this.countLess = 0;
                        }
                    } else {
                        i = this.raisedToBack;
                        if (i < 6) {
                            this.raisedToBack = i + 1;
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
                }
                this.previousAccValue = f;
                fArr6 = this.gravityFast;
                z = fArr6[1] > 2.5f && Math.abs(fArr6[2]) < 4.0f && Math.abs(this.gravityFast[0]) > 1.5f;
                this.accelerometerVertical = z;
            }
            WakeLock wakeLock;
            if (this.raisedToBack == 6 && this.accelerometerVertical && this.proximityTouched && !NotificationsController.audioManager.isWiredHeadsetOn()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("sensor values reached");
                }
                if (this.playingMessageObject != null || this.recordStartRunnable != null || this.recordingAudio != null || PhotoViewer.getInstance().isVisible() || !ApplicationLoader.isScreenOn || this.inputFieldHasText || !this.allowStartRecord || this.raiseChat == null || this.callInProgress) {
                    MessageObject messageObject = this.playingMessageObject;
                    if (messageObject != null && ((messageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start listen");
                        }
                        if (this.proximityHasDifferentValues) {
                            wakeLock = this.proximityWakeLock;
                            if (!(wakeLock == null || wakeLock.isHeld())) {
                                this.proximityWakeLock.acquire();
                            }
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        this.ignoreOnPause = true;
                    }
                } else if (!this.raiseToEarRecord) {
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
                    if (this.proximityHasDifferentValues) {
                        wakeLock = this.proximityWakeLock;
                        if (!(wakeLock == null || wakeLock.isHeld())) {
                            this.proximityWakeLock.acquire();
                        }
                    }
                }
                this.raisedToBack = 0;
                this.raisedToTop = 0;
                this.raisedToTopSign = 0;
                this.countLess = 0;
            } else {
                z = this.proximityTouched;
                if (z) {
                    if (!(this.playingMessageObject == null || ApplicationLoader.mainInterfacePaused || ((!this.playingMessageObject.isVoice() && !this.playingMessageObject.isRoundVideo()) || this.useFrontSpeaker))) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start listen by proximity only");
                        }
                        if (this.proximityHasDifferentValues) {
                            wakeLock = this.proximityWakeLock;
                            if (!(wakeLock == null || wakeLock.isHeld())) {
                                this.proximityWakeLock.acquire();
                            }
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        this.ignoreOnPause = true;
                    }
                } else if (!z) {
                    if (this.raiseToEarRecord) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("stop record");
                        }
                        stopRecording(2);
                        this.raiseToEarRecord = false;
                        this.ignoreOnPause = false;
                        if (this.proximityHasDifferentValues) {
                            wakeLock = this.proximityWakeLock;
                            if (wakeLock != null && wakeLock.isHeld()) {
                                this.proximityWakeLock.release();
                            }
                        }
                    } else if (this.useFrontSpeaker) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("stop listen");
                        }
                        this.useFrontSpeaker = false;
                        startAudioAgain(true);
                        this.ignoreOnPause = false;
                        if (this.proximityHasDifferentValues) {
                            wakeLock = this.proximityWakeLock;
                            if (wakeLock != null && wakeLock.isHeld()) {
                                this.proximityWakeLock.release();
                            }
                        }
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

    private void setUseFrontSpeaker(boolean z) {
        this.useFrontSpeaker = z;
        AudioManager audioManager = NotificationsController.audioManager;
        if (this.useFrontSpeaker) {
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(false);
            return;
        }
        audioManager.setSpeakerphoneOn(true);
    }

    public void startRecordingIfFromSpeaker() {
        if (this.useFrontSpeaker) {
            ChatActivity chatActivity = this.raiseChat;
            if (chatActivity != null && this.allowStartRecord) {
                this.raiseToEarRecord = true;
                startRecording(chatActivity.getCurrentAccount(), this.raiseChat.getDialogId(), null);
                this.ignoreOnPause = true;
            }
        }
    }

    private void startAudioAgain(boolean z) {
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null) {
            NotificationCenter instance = NotificationCenter.getInstance(messageObject.currentAccount);
            int i = NotificationCenter.audioRouteChanged;
            Object[] objArr = new Object[1];
            int i2 = 0;
            objArr[0] = Boolean.valueOf(this.useFrontSpeaker);
            instance.postNotificationName(i, objArr);
            VideoPlayer videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                if (!this.useFrontSpeaker) {
                    i2 = 3;
                }
                videoPlayer.setStreamType(i2);
                if (z) {
                    lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
                } else {
                    this.videoPlayer.play();
                }
            } else {
                Object obj = this.audioPlayer != null ? 1 : null;
                MessageObject messageObject2 = this.playingMessageObject;
                float f = messageObject2.audioProgress;
                cleanupPlayer(false, true);
                messageObject2.audioProgress = f;
                playMessage(messageObject2);
                if (z) {
                    if (obj != null) {
                        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$lSLgqwMUP5qD77OV_XW0fHoQAiY(this, messageObject2), 100);
                    } else {
                        lambda$startAudioAgain$5$MediaController(messageObject2);
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

    public void startRaiseToEarSensors(ChatActivity chatActivity) {
        if (!(chatActivity == null || ((this.accelerometerSensor == null && (this.gravitySensor == null || this.linearAcceleration == null)) || this.proximitySensor == null))) {
            this.raiseChat = chatActivity;
            if (!SharedConfig.raiseToSpeak) {
                MessageObject messageObject = this.playingMessageObject;
                if (messageObject == null || !(messageObject.isVoice() || this.playingMessageObject.isRoundVideo())) {
                    return;
                }
            }
            if (!this.sensorsStarted) {
                float[] fArr = this.gravity;
                fArr[2] = 0.0f;
                fArr[1] = 0.0f;
                fArr[0] = 0.0f;
                fArr = this.linearAcceleration;
                fArr[2] = 0.0f;
                fArr[1] = 0.0f;
                fArr[0] = 0.0f;
                fArr = this.gravityFast;
                fArr[2] = 0.0f;
                fArr[1] = 0.0f;
                fArr[0] = 0.0f;
                this.lastTimestamp = 0;
                this.previousAccValue = 0.0f;
                this.raisedToTop = 0;
                this.raisedToTopSign = 0;
                this.countLess = 0;
                this.raisedToBack = 0;
                Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$XP8XA61VZfZeH2YNAAQQ28iI33I(this));
                this.sensorsStarted = true;
            }
        }
    }

    public /* synthetic */ void lambda$startRaiseToEarSensors$6$MediaController() {
        Sensor sensor = this.gravitySensor;
        if (sensor != null) {
            this.sensorManager.registerListener(this, sensor, 30000);
        }
        sensor = this.linearSensor;
        if (sensor != null) {
            this.sensorManager.registerListener(this, sensor, 30000);
        }
        sensor = this.accelerometerSensor;
        if (sensor != null) {
            this.sensorManager.registerListener(this, sensor, 30000);
        }
        this.sensorManager.registerListener(this, this.proximitySensor, 3);
    }

    public void stopRaiseToEarSensors(ChatActivity chatActivity, boolean z) {
        if (this.ignoreOnPause) {
            this.ignoreOnPause = false;
            return;
        }
        stopRecording(z ? 2 : 0);
        if (!(!this.sensorsStarted || this.ignoreOnPause || ((this.accelerometerSensor == null && (this.gravitySensor == null || this.linearAcceleration == null)) || this.proximitySensor == null || this.raiseChat != chatActivity))) {
            this.raiseChat = null;
            this.sensorsStarted = false;
            this.accelerometerVertical = false;
            this.proximityTouched = false;
            this.raiseToEarRecord = false;
            this.useFrontSpeaker = false;
            Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$5zcBDCHMQng3baqUjT_bO0RgccA(this));
            if (this.proximityHasDifferentValues) {
                WakeLock wakeLock = this.proximityWakeLock;
                if (wakeLock != null && wakeLock.isHeld()) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    public /* synthetic */ void lambda$stopRaiseToEarSensors$7$MediaController() {
        Sensor sensor = this.linearSensor;
        if (sensor != null) {
            this.sensorManager.unregisterListener(this, sensor);
        }
        sensor = this.gravitySensor;
        if (sensor != null) {
            this.sensorManager.unregisterListener(this, sensor);
        }
        sensor = this.accelerometerSensor;
        if (sensor != null) {
            this.sensorManager.unregisterListener(this, sensor);
        }
        this.sensorManager.unregisterListener(this, this.proximitySensor);
    }

    public void cleanupPlayer(boolean z, boolean z2) {
        cleanupPlayer(z, z2, false, false);
    }

    public void cleanupPlayer(boolean z, boolean z2, boolean z3, boolean z4) {
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer != null) {
            try {
                videoPlayer.releasePlayer(true);
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.audioPlayer = null;
        } else {
            videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                this.currentAspectRatioFrameLayout = null;
                this.currentTextureViewContainer = null;
                this.currentAspectRatioFrameLayoutReady = false;
                this.isDrawingWasReady = false;
                this.currentTextureView = null;
                this.goingToShowMessageObject = null;
                MessageObject messageObject;
                if (z4) {
                    PhotoViewer.getInstance().injectVideoPlayer(this.videoPlayer);
                    messageObject = this.playingMessageObject;
                    this.goingToShowMessageObject = messageObject;
                    NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, Boolean.valueOf(true));
                } else {
                    long currentPosition = videoPlayer.getCurrentPosition();
                    messageObject = this.playingMessageObject;
                    if (messageObject != null && messageObject.isVideo() && currentPosition > 0 && currentPosition != -9223372036854775807L) {
                        messageObject = this.playingMessageObject;
                        messageObject.audioProgressMs = (int) currentPosition;
                        NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, Boolean.valueOf(false));
                    }
                    this.videoPlayer.releasePlayer(true);
                    this.videoPlayer = null;
                }
                try {
                    this.baseActivity.getWindow().clearFlags(128);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                if (!(this.playingMessageObject == null || z4)) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
            }
        }
        stopProgressTimer();
        this.lastProgress = 0;
        this.isPaused = false;
        if (!(this.useFrontSpeaker || SharedConfig.raiseToSpeak)) {
            ChatActivity chatActivity = this.raiseChat;
            stopRaiseToEarSensors(chatActivity, false);
            this.raiseChat = chatActivity;
        }
        MessageObject messageObject2 = this.playingMessageObject;
        if (messageObject2 != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance(messageObject2.currentAccount).cancelLoadFile(this.playingMessageObject.getDocument());
            }
            messageObject2 = this.playingMessageObject;
            if (z) {
                messageObject2.resetPlayingProgress();
                NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0));
            }
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (z) {
                NotificationsController.audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                int i = -1;
                ArrayList arrayList = this.voiceMessagesPlaylist;
                if (arrayList != null) {
                    if (z3) {
                        i = arrayList.indexOf(messageObject2);
                        if (i >= 0) {
                            this.voiceMessagesPlaylist.remove(i);
                            this.voiceMessagesPlaylistMap.remove(messageObject2.getId());
                            if (this.voiceMessagesPlaylist.isEmpty()) {
                                this.voiceMessagesPlaylist = null;
                                this.voiceMessagesPlaylistMap = null;
                            }
                        }
                    }
                    this.voiceMessagesPlaylist = null;
                    this.voiceMessagesPlaylistMap = null;
                }
                ArrayList arrayList2 = this.voiceMessagesPlaylist;
                PipRoundVideoView pipRoundVideoView;
                if (arrayList2 == null || i >= arrayList2.size()) {
                    if ((messageObject2.isVoice() || messageObject2.isRoundVideo()) && messageObject2.getId() != 0) {
                        startRecordingIfFromSpeaker();
                    }
                    NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(messageObject2.getId()), Boolean.valueOf(z2));
                    this.pipSwitchingState = 0;
                    pipRoundVideoView = this.pipRoundVideoView;
                    if (pipRoundVideoView != null) {
                        pipRoundVideoView.close(true);
                        this.pipRoundVideoView = null;
                    }
                } else {
                    MessageObject messageObject3 = (MessageObject) this.voiceMessagesPlaylist.get(i);
                    playMessage(messageObject3);
                    if (!messageObject3.isRoundVideo()) {
                        pipRoundVideoView = this.pipRoundVideoView;
                        if (pipRoundVideoView != null) {
                            pipRoundVideoView.close(true);
                            this.pipRoundVideoView = null;
                        }
                    }
                }
            }
            if (z2) {
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
        MessageObject messageObject2 = this.playingMessageObject;
        if (messageObject2 != null && messageObject2.getDialogId() == messageObject.getDialogId() && this.playingMessageObject.getId() == messageObject.getId()) {
            if ((this.playingMessageObject.eventId == 0 ? 1 : null) == (messageObject.eventId == 0 ? 1 : null)) {
                return true;
            }
        }
        return false;
    }

    public boolean seekToProgress(MessageObject messageObject, float f) {
        if (!((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject))) {
            try {
                if (this.audioPlayer != null) {
                    long duration = this.audioPlayer.getDuration();
                    if (duration == -9223372036854775807L) {
                        this.seekToProgressPending = f;
                    } else {
                        long j = (long) ((int) (((float) duration) * f));
                        this.audioPlayer.seekTo(j);
                        this.lastProgress = j;
                    }
                } else if (this.videoPlayer != null) {
                    this.videoPlayer.seekTo((long) (((float) this.videoPlayer.getDuration()) * f));
                }
                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidSeek, Integer.valueOf(this.playingMessageObject.getId()), Float.valueOf(f));
                return true;
            } catch (Exception e) {
                FileLog.e(e);
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
        if (messageObject.isMusic()) {
            if (SharedConfig.shuffleMusic) {
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
        int i2 = this.currentPlaylistNum;
        if (i2 >= 0 && i2 < this.playlist.size()) {
            this.currentPlaylistNum = i;
            this.playMusicAgain = true;
            MessageObject messageObject = this.playingMessageObject;
            if (messageObject != null) {
                messageObject.resetPlayingProgress();
            }
            playMessage((MessageObject) this.playlist.get(this.currentPlaylistNum));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0053 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0105  */
    private void playNextMessageWithoutOrder(boolean r9) {
        /*
        r8 = this;
        r0 = org.telegram.messenger.SharedConfig.shuffleMusic;
        if (r0 == 0) goto L_0x0007;
    L_0x0004:
        r0 = r8.shuffledPlaylist;
        goto L_0x0009;
    L_0x0007:
        r0 = r8.playlist;
    L_0x0009:
        r1 = 0;
        r2 = 2;
        r3 = 0;
        if (r9 == 0) goto L_0x0029;
    L_0x000e:
        r4 = org.telegram.messenger.SharedConfig.repeatMode;
        if (r4 != r2) goto L_0x0029;
    L_0x0012:
        r4 = r8.forceLoopCurrentPlaylist;
        if (r4 != 0) goto L_0x0029;
    L_0x0016:
        r8.cleanupPlayer(r3, r3);
        r9 = r8.currentPlaylistNum;
        r9 = r0.get(r9);
        r9 = (org.telegram.messenger.MessageObject) r9;
        r9.audioProgress = r1;
        r9.audioProgressSec = r3;
        r8.playMessage(r9);
        return;
    L_0x0029:
        r4 = org.telegram.messenger.SharedConfig.playOrderReversed;
        r5 = 1;
        if (r4 == 0) goto L_0x003e;
    L_0x002e:
        r4 = r8.currentPlaylistNum;
        r4 = r4 + r5;
        r8.currentPlaylistNum = r4;
        r4 = r8.currentPlaylistNum;
        r6 = r0.size();
        if (r4 < r6) goto L_0x0050;
    L_0x003b:
        r8.currentPlaylistNum = r3;
        goto L_0x004e;
    L_0x003e:
        r4 = r8.currentPlaylistNum;
        r4 = r4 - r5;
        r8.currentPlaylistNum = r4;
        r4 = r8.currentPlaylistNum;
        if (r4 >= 0) goto L_0x0050;
    L_0x0047:
        r4 = r0.size();
        r4 = r4 - r5;
        r8.currentPlaylistNum = r4;
    L_0x004e:
        r4 = 1;
        goto L_0x0051;
    L_0x0050:
        r4 = 0;
    L_0x0051:
        if (r4 == 0) goto L_0x00f6;
    L_0x0053:
        if (r9 == 0) goto L_0x00f6;
    L_0x0055:
        r9 = org.telegram.messenger.SharedConfig.repeatMode;
        if (r9 != 0) goto L_0x00f6;
    L_0x0059:
        r9 = r8.forceLoopCurrentPlaylist;
        if (r9 != 0) goto L_0x00f6;
    L_0x005d:
        r9 = r8.audioPlayer;
        if (r9 != 0) goto L_0x0065;
    L_0x0061:
        r9 = r8.videoPlayer;
        if (r9 == 0) goto L_0x00f5;
    L_0x0065:
        r9 = r8.audioPlayer;
        r0 = 0;
        if (r9 == 0) goto L_0x0075;
    L_0x006a:
        r9.releasePlayer(r5);	 Catch:{ Exception -> 0x006e }
        goto L_0x0072;
    L_0x006e:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
    L_0x0072:
        r8.audioPlayer = r0;
        goto L_0x00ac;
    L_0x0075:
        r9 = r8.videoPlayer;
        if (r9 == 0) goto L_0x00ac;
    L_0x0079:
        r8.currentAspectRatioFrameLayout = r0;
        r8.currentTextureViewContainer = r0;
        r8.currentAspectRatioFrameLayoutReady = r3;
        r8.currentTextureView = r0;
        r9.releasePlayer(r5);
        r8.videoPlayer = r0;
        r9 = r8.baseActivity;	 Catch:{ Exception -> 0x0092 }
        r9 = r9.getWindow();	 Catch:{ Exception -> 0x0092 }
        r0 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r9.clearFlags(r0);	 Catch:{ Exception -> 0x0092 }
        goto L_0x0096;
    L_0x0092:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
    L_0x0096:
        r9 = r8.setLoadingRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r9);
        r9 = r8.playingMessageObject;
        r9 = r9.currentAccount;
        r9 = org.telegram.messenger.FileLoader.getInstance(r9);
        r0 = r8.playingMessageObject;
        r0 = r0.getDocument();
        r9.removeLoadingVideo(r0, r5, r3);
    L_0x00ac:
        r8.stopProgressTimer();
        r6 = 0;
        r8.lastProgress = r6;
        r8.isPaused = r5;
        r9 = r8.playingMessageObject;
        r9.audioProgress = r1;
        r9.audioProgressSec = r3;
        r9 = r9.currentAccount;
        r9 = org.telegram.messenger.NotificationCenter.getInstance(r9);
        r0 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r1 = new java.lang.Object[r2];
        r2 = r8.playingMessageObject;
        r2 = r2.getId();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r3] = r2;
        r2 = java.lang.Integer.valueOf(r3);
        r1[r5] = r2;
        r9.postNotificationName(r0, r1);
        r9 = r8.playingMessageObject;
        r9 = r9.currentAccount;
        r9 = org.telegram.messenger.NotificationCenter.getInstance(r9);
        r0 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r1 = new java.lang.Object[r5];
        r2 = r8.playingMessageObject;
        r2 = r2.getId();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r3] = r2;
        r9.postNotificationName(r0, r1);
    L_0x00f5:
        return;
    L_0x00f6:
        r9 = r8.currentPlaylistNum;
        if (r9 < 0) goto L_0x0115;
    L_0x00fa:
        r1 = r0.size();
        if (r9 < r1) goto L_0x0101;
    L_0x0100:
        goto L_0x0115;
    L_0x0101:
        r9 = r8.playingMessageObject;
        if (r9 == 0) goto L_0x0108;
    L_0x0105:
        r9.resetPlayingProgress();
    L_0x0108:
        r8.playMusicAgain = r5;
        r9 = r8.currentPlaylistNum;
        r9 = r0.get(r9);
        r9 = (org.telegram.messenger.MessageObject) r9;
        r8.playMessage(r9);
    L_0x0115:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.playNextMessageWithoutOrder(boolean):void");
    }

    public void playPreviousMessage() {
        ArrayList arrayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!arrayList.isEmpty()) {
            int i = this.currentPlaylistNum;
            if (i >= 0 && i < arrayList.size()) {
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
                i = this.currentPlaylistNum;
                if (i >= 0 && i < arrayList.size()) {
                    this.playMusicAgain = true;
                    playMessage((MessageObject) arrayList.get(this.currentPlaylistNum));
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void checkIsNextMediaFileDownloaded() {
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null && messageObject.isMusic()) {
            checkIsNextMusicFileDownloaded(this.playingMessageObject.currentAccount);
        }
    }

    private void checkIsNextVoiceFileDownloaded(int i) {
        ArrayList arrayList = this.voiceMessagesPlaylist;
        if (arrayList != null && arrayList.size() >= 2) {
            File file;
            MessageObject messageObject = (MessageObject) this.voiceMessagesPlaylist.get(1);
            String str = messageObject.messageOwner.attachPath;
            File file2 = null;
            if (str != null && str.length() > 0) {
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
            if (file != null && file != file2 && !file.exists()) {
                FileLoader.getInstance(i).loadFile(messageObject.getDocument(), messageObject, 0, 0);
            }
        }
    }

    private void checkIsNextMusicFileDownloaded(int i) {
        if (DownloadController.getInstance(i).canDownloadNextTrack()) {
            ArrayList arrayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
            if (arrayList != null && arrayList.size() >= 2) {
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
                if (i2 >= 0 && i2 < arrayList.size()) {
                    File file;
                    MessageObject messageObject = (MessageObject) arrayList.get(i2);
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
                        FileLoader.getInstance(i).loadFile(messageObject.getDocument(), messageObject, 0, 0);
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
            for (int i = 0; i < this.voiceMessagesPlaylist.size(); i++) {
                MessageObject messageObject = (MessageObject) this.voiceMessagesPlaylist.get(i);
                this.voiceMessagesPlaylistMap.put(messageObject.getId(), messageObject);
            }
        }
    }

    private void checkAudioFocus(MessageObject messageObject) {
        int i = (messageObject.isVoice() || messageObject.isRoundVideo()) ? this.useFrontSpeaker ? 3 : 2 : 1;
        if (this.hasAudioFocus != i) {
            this.hasAudioFocus = i;
            if (i == 3) {
                i = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
            } else {
                i = NotificationsController.audioManager.requestAudioFocus(this, 3, i == 2 ? 3 : 1);
            }
            if (i == 1) {
                this.audioFocus = 2;
            }
        }
    }

    public void setCurrentVideoVisible(boolean z) {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.currentAspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            PipRoundVideoView pipRoundVideoView;
            if (z) {
                pipRoundVideoView = this.pipRoundVideoView;
                if (pipRoundVideoView != null) {
                    this.pipSwitchingState = 2;
                    pipRoundVideoView.close(true);
                    this.pipRoundVideoView = null;
                } else if (aspectRatioFrameLayout != null) {
                    if (aspectRatioFrameLayout.getParent() == null) {
                        this.currentTextureViewContainer.addView(this.currentAspectRatioFrameLayout);
                    }
                    this.videoPlayer.setTextureView(this.currentTextureView);
                }
            } else if (aspectRatioFrameLayout.getParent() != null) {
                this.pipSwitchingState = 1;
                this.currentTextureViewContainer.removeView(this.currentAspectRatioFrameLayout);
            } else {
                if (this.pipRoundVideoView == null) {
                    try {
                        this.pipRoundVideoView = new PipRoundVideoView();
                        this.pipRoundVideoView.show(this.baseActivity, new -$$Lambda$MediaController$GKrg4OGTs8RBmP_jQs-T_HQHgXA(this));
                    } catch (Exception unused) {
                        this.pipRoundVideoView = null;
                    }
                }
                pipRoundVideoView = this.pipRoundVideoView;
                if (pipRoundVideoView != null) {
                    this.videoPlayer.setTextureView(pipRoundVideoView.getTextureView());
                }
            }
        }
    }

    public /* synthetic */ void lambda$setCurrentVideoVisible$8$MediaController() {
        cleanupPlayer(true, true);
    }

    public void setTextureView(TextureView textureView, AspectRatioFrameLayout aspectRatioFrameLayout, FrameLayout frameLayout, boolean z) {
        if (textureView != null) {
            boolean z2 = true;
            if (z || this.currentTextureView != textureView) {
                if (!(this.videoPlayer == null || textureView == this.currentTextureView)) {
                    if (aspectRatioFrameLayout == null || !aspectRatioFrameLayout.isDrawingReady()) {
                        z2 = false;
                    }
                    this.isDrawingWasReady = z2;
                    this.currentTextureView = textureView;
                    PipRoundVideoView pipRoundVideoView = this.pipRoundVideoView;
                    if (pipRoundVideoView != null) {
                        this.videoPlayer.setTextureView(pipRoundVideoView.getTextureView());
                    } else {
                        this.videoPlayer.setTextureView(this.currentTextureView);
                    }
                    this.currentAspectRatioFrameLayout = aspectRatioFrameLayout;
                    this.currentTextureViewContainer = frameLayout;
                    if (this.currentAspectRatioFrameLayoutReady) {
                        AspectRatioFrameLayout aspectRatioFrameLayout2 = this.currentAspectRatioFrameLayout;
                        if (!(aspectRatioFrameLayout2 == null || aspectRatioFrameLayout2 == null)) {
                            aspectRatioFrameLayout2.setAspectRatio(this.currentAspectRatioFrameLayoutRatio, this.currentAspectRatioFrameLayoutRotation);
                        }
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

    public boolean hasFlagSecureFragment() {
        return this.flagSecureFragment != null;
    }

    public void setFlagSecure(BaseFragment baseFragment, boolean z) {
        if (z) {
            try {
                baseFragment.getParentActivity().getWindow().setFlags(8192, 8192);
            } catch (Exception unused) {
            }
            this.flagSecureFragment = baseFragment;
        } else if (this.flagSecureFragment == baseFragment) {
            try {
                baseFragment.getParentActivity().getWindow().clearFlags(8192);
            } catch (Exception unused2) {
            }
            this.flagSecureFragment = null;
        }
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

    public void setPlaybackSpeed(float f) {
        this.currentPlaybackSpeed = f;
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer != null) {
            videoPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
        } else {
            videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                videoPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
            }
        }
        MessagesController.getGlobalMainSettings().edit().putFloat("playbackSpeed", f).commit();
    }

    public float getPlaybackSpeed() {
        return this.currentPlaybackSpeed;
    }

    private void updateVideoState(MessageObject messageObject, int[] iArr, boolean z, boolean z2, int i) {
        if (this.videoPlayer != null) {
            if (i == 4 || i == 1) {
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
            if (i == 3) {
                this.playerWasReady = true;
                MessageObject messageObject2 = this.playingMessageObject;
                if (messageObject2 != null && (messageObject2.isVideo() || this.playingMessageObject.isRoundVideo())) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(messageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
                this.currentAspectRatioFrameLayoutReady = true;
            } else if (i == 2) {
                if (z2) {
                    messageObject = this.playingMessageObject;
                    if (messageObject != null && (messageObject.isVideo() || this.playingMessageObject.isRoundVideo())) {
                        if (this.playerWasReady) {
                            this.setLoadingRunnable.run();
                        } else {
                            AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000);
                        }
                    }
                }
            } else if (this.videoPlayer.isPlaying() && i == 4) {
                if (!this.playingMessageObject.isVideo() || z || (iArr != null && iArr[0] >= 4)) {
                    cleanupPlayer(true, true, true, false);
                } else {
                    this.videoPlayer.seekTo(0);
                    if (iArr != null) {
                        iArr[0] = iArr[0] + 1;
                    }
                }
            }
        }
    }

    public void injectVideoPlayer(VideoPlayer videoPlayer, final MessageObject messageObject) {
        if (videoPlayer != null && messageObject != null) {
            FileLoader.getInstance(messageObject.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
            this.playerWasReady = false;
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.videoPlayer = videoPlayer;
            this.playingMessageObject = messageObject;
            this.videoPlayer.setDelegate(new VideoPlayerDelegate(null, true) {
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }

                public void onStateChanged(boolean z, int i) {
                    MediaController.this.updateVideoState(messageObject, null, true, z, i);
                }

                public void onError(Exception exception) {
                    FileLog.e((Throwable) exception);
                }

                public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                    MediaController.this.currentAspectRatioFrameLayoutRotation = i3;
                    if (!(i3 == 90 || i3 == 270)) {
                        int i4 = i2;
                        i2 = i;
                        i = i4;
                    }
                    MediaController.this.currentAspectRatioFrameLayoutRatio = i == 0 ? 1.0f : (((float) i2) * f) / ((float) i);
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
                                    MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new -$$Lambda$MediaController$5$ROZf_OsRqepDnAAg1NpMCnDNXO8(this));
                                } catch (Exception unused) {
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

                public /* synthetic */ void lambda$onSurfaceDestroyed$0$MediaController$5() {
                    MediaController.this.cleanupPlayer(true, true);
                }
            });
            this.currentAspectRatioFrameLayoutReady = false;
            TextureView textureView = this.currentTextureView;
            if (textureView != null) {
                this.videoPlayer.setTextureView(textureView);
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

    /* JADX WARNING: Removed duplicated region for block: B:155:0x0346 A:{SYNTHETIC, Splitter:B:155:0x0346} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x032d  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0409  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x03f1  */
    public boolean playMessage(org.telegram.messenger.MessageObject r18) {
        /*
        r17 = this;
        r1 = r17;
        r2 = r18;
        r3 = 0;
        r4 = java.lang.Integer.valueOf(r3);
        if (r2 != 0) goto L_0x000c;
    L_0x000b:
        return r3;
    L_0x000c:
        r0 = r1.audioPlayer;
        r5 = 1;
        if (r0 != 0) goto L_0x0015;
    L_0x0011:
        r0 = r1.videoPlayer;
        if (r0 == 0) goto L_0x002c;
    L_0x0015:
        r0 = r17.isSamePlayingMessage(r18);
        if (r0 == 0) goto L_0x002c;
    L_0x001b:
        r0 = r1.isPaused;
        if (r0 == 0) goto L_0x0022;
    L_0x001f:
        r17.resumeAudio(r18);
    L_0x0022:
        r0 = org.telegram.messenger.SharedConfig.raiseToSpeak;
        if (r0 != 0) goto L_0x002b;
    L_0x0026:
        r0 = r1.raiseChat;
        r1.startRaiseToEarSensors(r0);
    L_0x002b:
        return r5;
    L_0x002c:
        r0 = r18.isOut();
        if (r0 != 0) goto L_0x0041;
    L_0x0032:
        r0 = r18.isContentUnread();
        if (r0 == 0) goto L_0x0041;
    L_0x0038:
        r0 = r2.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0.markMessageContentAsRead(r2);
    L_0x0041:
        r0 = r1.playMusicAgain;
        r6 = r0 ^ 1;
        r7 = r1.playingMessageObject;
        if (r7 == 0) goto L_0x004f;
    L_0x0049:
        if (r0 != 0) goto L_0x004e;
    L_0x004b:
        r7.resetPlayingProgress();
    L_0x004e:
        r6 = 0;
    L_0x004f:
        r1.cleanupPlayer(r6, r3);
        r1.playMusicAgain = r3;
        r6 = 0;
        r1.seekToProgressPending = r6;
        r0 = r2.messageOwner;
        r0 = r0.attachPath;
        r7 = 0;
        if (r0 == 0) goto L_0x0075;
    L_0x005e:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x0075;
    L_0x0064:
        r0 = new java.io.File;
        r8 = r2.messageOwner;
        r8 = r8.attachPath;
        r0.<init>(r8);
        r8 = r0.exists();
        if (r8 != 0) goto L_0x0077;
    L_0x0073:
        r0 = r7;
        goto L_0x0077;
    L_0x0075:
        r0 = r7;
        r8 = 0;
    L_0x0077:
        if (r0 == 0) goto L_0x007b;
    L_0x0079:
        r9 = r0;
        goto L_0x0081;
    L_0x007b:
        r9 = r2.messageOwner;
        r9 = org.telegram.messenger.FileLoader.getPathToMessage(r9);
    L_0x0081:
        r10 = org.telegram.messenger.SharedConfig.streamMedia;
        if (r10 == 0) goto L_0x00a6;
    L_0x0085:
        r10 = r18.isMusic();
        if (r10 != 0) goto L_0x009d;
    L_0x008b:
        r10 = r18.isRoundVideo();
        if (r10 != 0) goto L_0x009d;
    L_0x0091:
        r10 = r18.isVideo();
        if (r10 == 0) goto L_0x00a6;
    L_0x0097:
        r10 = r18.canStreamVideo();
        if (r10 == 0) goto L_0x00a6;
    L_0x009d:
        r10 = r18.getDialogId();
        r11 = (int) r10;
        if (r11 == 0) goto L_0x00a6;
    L_0x00a4:
        r10 = 1;
        goto L_0x00a7;
    L_0x00a6:
        r10 = 0;
    L_0x00a7:
        r11 = 0;
        if (r9 == 0) goto L_0x0112;
    L_0x00ab:
        if (r9 == r0) goto L_0x0112;
    L_0x00ad:
        r8 = r9.exists();
        if (r8 != 0) goto L_0x0112;
    L_0x00b3:
        if (r10 != 0) goto L_0x0112;
    L_0x00b5:
        r0 = r2.currentAccount;
        r0 = org.telegram.messenger.FileLoader.getInstance(r0);
        r4 = r18.getDocument();
        r0.loadFile(r4, r2, r3, r3);
        r1.downloadingCurrentMessage = r5;
        r1.isPaused = r3;
        r1.lastProgress = r11;
        r1.audioInfo = r7;
        r1.playingMessageObject = r2;
        r0 = r1.playingMessageObject;
        r0 = r0.isMusic();
        if (r0 == 0) goto L_0x00e8;
    L_0x00d4:
        r0 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = org.telegram.messenger.MusicPlayerService.class;
        r0.<init>(r2, r4);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x00e3 }
        r2.startService(r0);	 Catch:{ Throwable -> 0x00e3 }
        goto L_0x00f6;
    L_0x00e3:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x00f6;
    L_0x00e8:
        r0 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = org.telegram.messenger.MusicPlayerService.class;
        r0.<init>(r2, r4);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r2.stopService(r0);
    L_0x00f6:
        r0 = r1.playingMessageObject;
        r0 = r0.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r4 = new java.lang.Object[r5];
        r6 = r1.playingMessageObject;
        r6 = r6.getId();
        r6 = java.lang.Integer.valueOf(r6);
        r4[r3] = r6;
        r0.postNotificationName(r2, r4);
        return r5;
    L_0x0112:
        r1.downloadingCurrentMessage = r3;
        r10 = r18.isMusic();
        if (r10 == 0) goto L_0x0120;
    L_0x011a:
        r10 = r2.currentAccount;
        r1.checkIsNextMusicFileDownloaded(r10);
        goto L_0x0125;
    L_0x0120:
        r10 = r2.currentAccount;
        r1.checkIsNextVoiceFileDownloaded(r10);
    L_0x0125:
        r10 = r1.currentAspectRatioFrameLayout;
        if (r10 == 0) goto L_0x012e;
    L_0x0129:
        r1.isDrawingWasReady = r3;
        r10.setDrawingReady(r3);
    L_0x012e:
        r10 = r18.isVideo();
        r13 = r18.isRoundVideo();
        r14 = "&hash=";
        r15 = "&id=";
        r6 = "?account=";
        r16 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = "UTF-8";
        r11 = "other";
        if (r13 != 0) goto L_0x028d;
    L_0x0144:
        if (r10 == 0) goto L_0x0148;
    L_0x0146:
        goto L_0x028d;
    L_0x0148:
        r10 = r1.pipRoundVideoView;
        if (r10 == 0) goto L_0x0151;
    L_0x014c:
        r10.close(r5);
        r1.pipRoundVideoView = r7;
    L_0x0151:
        r10 = new org.telegram.ui.Components.VideoPlayer;	 Catch:{ Exception -> 0x025b }
        r10.<init>();	 Catch:{ Exception -> 0x025b }
        r1.audioPlayer = r10;	 Catch:{ Exception -> 0x025b }
        r10 = r1.audioPlayer;	 Catch:{ Exception -> 0x025b }
        r13 = new org.telegram.messenger.MediaController$7;	 Catch:{ Exception -> 0x025b }
        r13.<init>(r2);	 Catch:{ Exception -> 0x025b }
        r10.setDelegate(r13);	 Catch:{ Exception -> 0x025b }
        if (r8 == 0) goto L_0x017d;
    L_0x0164:
        r6 = r2.mediaExists;	 Catch:{ Exception -> 0x025b }
        if (r6 != 0) goto L_0x0172;
    L_0x0168:
        if (r9 == r0) goto L_0x0172;
    L_0x016a:
        r0 = new org.telegram.messenger.-$$Lambda$MediaController$auT6qepqgY7CkLB80AAQ5iO7pfI;	 Catch:{ Exception -> 0x025b }
        r0.<init>(r2);	 Catch:{ Exception -> 0x025b }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x025b }
    L_0x0172:
        r0 = r1.audioPlayer;	 Catch:{ Exception -> 0x025b }
        r6 = android.net.Uri.fromFile(r9);	 Catch:{ Exception -> 0x025b }
        r0.preparePlayer(r6, r11);	 Catch:{ Exception -> 0x025b }
        goto L_0x021c;
    L_0x017d:
        r0 = r2.currentAccount;	 Catch:{ Exception -> 0x025b }
        r0 = org.telegram.messenger.FileLoader.getInstance(r0);	 Catch:{ Exception -> 0x025b }
        r0 = r0.getFileReference(r2);	 Catch:{ Exception -> 0x025b }
        r8 = r18.getDocument();	 Catch:{ Exception -> 0x025b }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x025b }
        r10.<init>();	 Catch:{ Exception -> 0x025b }
        r10.append(r6);	 Catch:{ Exception -> 0x025b }
        r6 = r2.currentAccount;	 Catch:{ Exception -> 0x025b }
        r10.append(r6);	 Catch:{ Exception -> 0x025b }
        r10.append(r15);	 Catch:{ Exception -> 0x025b }
        r5 = r8.id;	 Catch:{ Exception -> 0x025b }
        r10.append(r5);	 Catch:{ Exception -> 0x025b }
        r10.append(r14);	 Catch:{ Exception -> 0x025b }
        r5 = r8.access_hash;	 Catch:{ Exception -> 0x025b }
        r10.append(r5);	 Catch:{ Exception -> 0x025b }
        r5 = "&dc=";
        r10.append(r5);	 Catch:{ Exception -> 0x025b }
        r5 = r8.dc_id;	 Catch:{ Exception -> 0x025b }
        r10.append(r5);	 Catch:{ Exception -> 0x025b }
        r5 = "&size=";
        r10.append(r5);	 Catch:{ Exception -> 0x025b }
        r5 = r8.size;	 Catch:{ Exception -> 0x025b }
        r10.append(r5);	 Catch:{ Exception -> 0x025b }
        r5 = "&mime=";
        r10.append(r5);	 Catch:{ Exception -> 0x025b }
        r5 = r8.mime_type;	 Catch:{ Exception -> 0x025b }
        r5 = java.net.URLEncoder.encode(r5, r12);	 Catch:{ Exception -> 0x025b }
        r10.append(r5);	 Catch:{ Exception -> 0x025b }
        r5 = "&rid=";
        r10.append(r5);	 Catch:{ Exception -> 0x025b }
        r10.append(r0);	 Catch:{ Exception -> 0x025b }
        r0 = "&name=";
        r10.append(r0);	 Catch:{ Exception -> 0x025b }
        r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r8);	 Catch:{ Exception -> 0x025b }
        r0 = java.net.URLEncoder.encode(r0, r12);	 Catch:{ Exception -> 0x025b }
        r10.append(r0);	 Catch:{ Exception -> 0x025b }
        r0 = "&reference=";
        r10.append(r0);	 Catch:{ Exception -> 0x025b }
        r0 = r8.file_reference;	 Catch:{ Exception -> 0x025b }
        if (r0 == 0) goto L_0x01ee;
    L_0x01eb:
        r0 = r8.file_reference;	 Catch:{ Exception -> 0x025b }
        goto L_0x01f0;
    L_0x01ee:
        r0 = new byte[r3];	 Catch:{ Exception -> 0x025b }
    L_0x01f0:
        r0 = org.telegram.messenger.Utilities.bytesToHex(r0);	 Catch:{ Exception -> 0x025b }
        r10.append(r0);	 Catch:{ Exception -> 0x025b }
        r0 = r10.toString();	 Catch:{ Exception -> 0x025b }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x025b }
        r5.<init>();	 Catch:{ Exception -> 0x025b }
        r6 = "tg://";
        r5.append(r6);	 Catch:{ Exception -> 0x025b }
        r6 = r18.getFileName();	 Catch:{ Exception -> 0x025b }
        r5.append(r6);	 Catch:{ Exception -> 0x025b }
        r5.append(r0);	 Catch:{ Exception -> 0x025b }
        r0 = r5.toString();	 Catch:{ Exception -> 0x025b }
        r0 = android.net.Uri.parse(r0);	 Catch:{ Exception -> 0x025b }
        r5 = r1.audioPlayer;	 Catch:{ Exception -> 0x025b }
        r5.preparePlayer(r0, r11);	 Catch:{ Exception -> 0x025b }
    L_0x021c:
        r0 = r18.isVoice();	 Catch:{ Exception -> 0x025b }
        if (r0 == 0) goto L_0x023c;
    L_0x0222:
        r0 = r1.currentPlaybackSpeed;	 Catch:{ Exception -> 0x025b }
        r0 = (r0 > r16 ? 1 : (r0 == r16 ? 0 : -1));
        if (r0 <= 0) goto L_0x022f;
    L_0x0228:
        r0 = r1.audioPlayer;	 Catch:{ Exception -> 0x025b }
        r5 = r1.currentPlaybackSpeed;	 Catch:{ Exception -> 0x025b }
        r0.setPlaybackSpeed(r5);	 Catch:{ Exception -> 0x025b }
    L_0x022f:
        r1.audioInfo = r7;	 Catch:{ Exception -> 0x025b }
        r0 = r1.playlist;	 Catch:{ Exception -> 0x025b }
        r0.clear();	 Catch:{ Exception -> 0x025b }
        r0 = r1.shuffledPlaylist;	 Catch:{ Exception -> 0x025b }
        r0.clear();	 Catch:{ Exception -> 0x025b }
        goto L_0x0247;
    L_0x023c:
        r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r9);	 Catch:{ Exception -> 0x0243 }
        r1.audioInfo = r0;	 Catch:{ Exception -> 0x0243 }
        goto L_0x0247;
    L_0x0243:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x025b }
    L_0x0247:
        r0 = r1.audioPlayer;	 Catch:{ Exception -> 0x025b }
        r5 = r1.useFrontSpeaker;	 Catch:{ Exception -> 0x025b }
        if (r5 == 0) goto L_0x024f;
    L_0x024d:
        r5 = 0;
        goto L_0x0250;
    L_0x024f:
        r5 = 3;
    L_0x0250:
        r0.setStreamType(r5);	 Catch:{ Exception -> 0x025b }
        r0 = r1.audioPlayer;	 Catch:{ Exception -> 0x025b }
        r0.play();	 Catch:{ Exception -> 0x025b }
        r10 = r4;
        goto L_0x040f;
    L_0x025b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = r2.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r4 = 1;
        r5 = new java.lang.Object[r4];
        r6 = r1.playingMessageObject;
        if (r6 == 0) goto L_0x0273;
    L_0x026e:
        r6 = r6.getId();
        goto L_0x0274;
    L_0x0273:
        r6 = 0;
    L_0x0274:
        r6 = java.lang.Integer.valueOf(r6);
        r5[r3] = r6;
        r0.postNotificationName(r2, r5);
        r0 = r1.audioPlayer;
        if (r0 == 0) goto L_0x028c;
    L_0x0281:
        r0.releasePlayer(r4);
        r1.audioPlayer = r7;
        r1.isPaused = r3;
        r1.playingMessageObject = r7;
        r1.downloadingCurrentMessage = r3;
    L_0x028c:
        return r3;
    L_0x028d:
        r5 = r2.currentAccount;
        r5 = org.telegram.messenger.FileLoader.getInstance(r5);
        r13 = r18.getDocument();
        r7 = 1;
        r5.setLoadingVideoForPlayer(r13, r7);
        r1.playerWasReady = r3;
        if (r10 == 0) goto L_0x02b3;
    L_0x029f:
        r5 = r2.messageOwner;
        r5 = r5.to_id;
        r5 = r5.channel_id;
        if (r5 != 0) goto L_0x02b1;
    L_0x02a7:
        r5 = r2.audioProgress;
        r7 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r5 > 0) goto L_0x02b1;
    L_0x02b0:
        goto L_0x02b3;
    L_0x02b1:
        r5 = 0;
        goto L_0x02b4;
    L_0x02b3:
        r5 = 1;
    L_0x02b4:
        if (r10 == 0) goto L_0x02c4;
    L_0x02b6:
        r7 = r18.getDuration();
        r10 = 30;
        if (r7 > r10) goto L_0x02c4;
    L_0x02be:
        r7 = 1;
        r10 = new int[r7];
        r10[r3] = r7;
        goto L_0x02c5;
    L_0x02c4:
        r10 = 0;
    L_0x02c5:
        r7 = r1.playlist;
        r7.clear();
        r7 = r1.shuffledPlaylist;
        r7.clear();
        r7 = new org.telegram.ui.Components.VideoPlayer;
        r7.<init>();
        r1.videoPlayer = r7;
        r7 = r1.videoPlayer;
        r13 = new org.telegram.messenger.MediaController$6;
        r13.<init>(r2, r10, r5);
        r7.setDelegate(r13);
        r1.currentAspectRatioFrameLayoutReady = r3;
        r5 = r1.pipRoundVideoView;
        if (r5 != 0) goto L_0x0302;
    L_0x02e6:
        r5 = r2.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r10 = r4;
        r3 = r18.getDialogId();
        r3 = r5.isDialogVisible(r3);
        if (r3 != 0) goto L_0x02f8;
    L_0x02f7:
        goto L_0x0303;
    L_0x02f8:
        r3 = r1.currentTextureView;
        if (r3 == 0) goto L_0x032b;
    L_0x02fc:
        r4 = r1.videoPlayer;
        r4.setTextureView(r3);
        goto L_0x032b;
    L_0x0302:
        r10 = r4;
    L_0x0303:
        r3 = r1.pipRoundVideoView;
        if (r3 != 0) goto L_0x031e;
    L_0x0307:
        r3 = new org.telegram.ui.Components.PipRoundVideoView;	 Catch:{ Exception -> 0x031b }
        r3.<init>();	 Catch:{ Exception -> 0x031b }
        r1.pipRoundVideoView = r3;	 Catch:{ Exception -> 0x031b }
        r3 = r1.pipRoundVideoView;	 Catch:{ Exception -> 0x031b }
        r4 = r1.baseActivity;	 Catch:{ Exception -> 0x031b }
        r5 = new org.telegram.messenger.-$$Lambda$MediaController$u8rACRf9hl-QJDf7Qe2JZbJv__Q;	 Catch:{ Exception -> 0x031b }
        r5.<init>(r1);	 Catch:{ Exception -> 0x031b }
        r3.show(r4, r5);	 Catch:{ Exception -> 0x031b }
        goto L_0x031e;
    L_0x031b:
        r3 = 0;
        r1.pipRoundVideoView = r3;
    L_0x031e:
        r3 = r1.pipRoundVideoView;
        if (r3 == 0) goto L_0x032b;
    L_0x0322:
        r4 = r1.videoPlayer;
        r3 = r3.getTextureView();
        r4.setTextureView(r3);
    L_0x032b:
        if (r8 == 0) goto L_0x0346;
    L_0x032d:
        r3 = r2.mediaExists;
        if (r3 != 0) goto L_0x033b;
    L_0x0331:
        if (r9 == r0) goto L_0x033b;
    L_0x0333:
        r0 = new org.telegram.messenger.-$$Lambda$MediaController$UNhwNBqeTy1Z6WZHWcGGmYV-bRw;
        r0.<init>(r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x033b:
        r0 = r1.videoPlayer;
        r3 = android.net.Uri.fromFile(r9);
        r0.preparePlayer(r3, r11);
        goto L_0x03eb;
    L_0x0346:
        r0 = r2.currentAccount;	 Catch:{ Exception -> 0x03e7 }
        r0 = org.telegram.messenger.FileLoader.getInstance(r0);	 Catch:{ Exception -> 0x03e7 }
        r0 = r0.getFileReference(r2);	 Catch:{ Exception -> 0x03e7 }
        r3 = r18.getDocument();	 Catch:{ Exception -> 0x03e7 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03e7 }
        r4.<init>();	 Catch:{ Exception -> 0x03e7 }
        r4.append(r6);	 Catch:{ Exception -> 0x03e7 }
        r5 = r2.currentAccount;	 Catch:{ Exception -> 0x03e7 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r4.append(r15);	 Catch:{ Exception -> 0x03e7 }
        r5 = r3.id;	 Catch:{ Exception -> 0x03e7 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r4.append(r14);	 Catch:{ Exception -> 0x03e7 }
        r5 = r3.access_hash;	 Catch:{ Exception -> 0x03e7 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r5 = "&dc=";
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r5 = r3.dc_id;	 Catch:{ Exception -> 0x03e7 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r5 = "&size=";
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r5 = r3.size;	 Catch:{ Exception -> 0x03e7 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r5 = "&mime=";
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r5 = r3.mime_type;	 Catch:{ Exception -> 0x03e7 }
        r5 = java.net.URLEncoder.encode(r5, r12);	 Catch:{ Exception -> 0x03e7 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r5 = "&rid=";
        r4.append(r5);	 Catch:{ Exception -> 0x03e7 }
        r4.append(r0);	 Catch:{ Exception -> 0x03e7 }
        r0 = "&name=";
        r4.append(r0);	 Catch:{ Exception -> 0x03e7 }
        r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r3);	 Catch:{ Exception -> 0x03e7 }
        r0 = java.net.URLEncoder.encode(r0, r12);	 Catch:{ Exception -> 0x03e7 }
        r4.append(r0);	 Catch:{ Exception -> 0x03e7 }
        r0 = "&reference=";
        r4.append(r0);	 Catch:{ Exception -> 0x03e7 }
        r0 = r3.file_reference;	 Catch:{ Exception -> 0x03e7 }
        if (r0 == 0) goto L_0x03b7;
    L_0x03b4:
        r0 = r3.file_reference;	 Catch:{ Exception -> 0x03e7 }
        goto L_0x03ba;
    L_0x03b7:
        r3 = 0;
        r0 = new byte[r3];	 Catch:{ Exception -> 0x03e7 }
    L_0x03ba:
        r0 = org.telegram.messenger.Utilities.bytesToHex(r0);	 Catch:{ Exception -> 0x03e7 }
        r4.append(r0);	 Catch:{ Exception -> 0x03e7 }
        r0 = r4.toString();	 Catch:{ Exception -> 0x03e7 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03e7 }
        r3.<init>();	 Catch:{ Exception -> 0x03e7 }
        r4 = "tg://";
        r3.append(r4);	 Catch:{ Exception -> 0x03e7 }
        r4 = r18.getFileName();	 Catch:{ Exception -> 0x03e7 }
        r3.append(r4);	 Catch:{ Exception -> 0x03e7 }
        r3.append(r0);	 Catch:{ Exception -> 0x03e7 }
        r0 = r3.toString();	 Catch:{ Exception -> 0x03e7 }
        r0 = android.net.Uri.parse(r0);	 Catch:{ Exception -> 0x03e7 }
        r3 = r1.videoPlayer;	 Catch:{ Exception -> 0x03e7 }
        r3.preparePlayer(r0, r11);	 Catch:{ Exception -> 0x03e7 }
        goto L_0x03eb;
    L_0x03e7:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03eb:
        r0 = r18.isRoundVideo();
        if (r0 == 0) goto L_0x0409;
    L_0x03f1:
        r0 = r1.videoPlayer;
        r3 = r1.useFrontSpeaker;
        if (r3 == 0) goto L_0x03f9;
    L_0x03f7:
        r3 = 0;
        goto L_0x03fa;
    L_0x03f9:
        r3 = 3;
    L_0x03fa:
        r0.setStreamType(r3);
        r0 = r1.currentPlaybackSpeed;
        r3 = (r0 > r16 ? 1 : (r0 == r16 ? 0 : -1));
        if (r3 <= 0) goto L_0x040f;
    L_0x0403:
        r3 = r1.videoPlayer;
        r3.setPlaybackSpeed(r0);
        goto L_0x040f;
    L_0x0409:
        r0 = r1.videoPlayer;
        r3 = 3;
        r0.setStreamType(r3);
    L_0x040f:
        r17.checkAudioFocus(r18);
        r17.setPlayerVolume();
        r3 = 0;
        r1.isPaused = r3;
        r3 = 0;
        r1.lastProgress = r3;
        r1.playingMessageObject = r2;
        r0 = org.telegram.messenger.SharedConfig.raiseToSpeak;
        if (r0 != 0) goto L_0x0427;
    L_0x0422:
        r0 = r1.raiseChat;
        r1.startRaiseToEarSensors(r0);
    L_0x0427:
        r0 = r1.playingMessageObject;
        r1.startProgressTimer(r0);
        r0 = r2.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r3 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart;
        r4 = 1;
        r5 = new java.lang.Object[r4];
        r4 = 0;
        r5[r4] = r2;
        r0.postNotificationName(r3, r5);
        r0 = r1.videoPlayer;
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = -NUM; // 0xNUM float:1.4E-45 double:-4.9E-324;
        r8 = 2;
        if (r0 == 0) goto L_0x04af;
    L_0x0449:
        r9 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0481 }
        r9 = r9.audioProgress;	 Catch:{ Exception -> 0x0481 }
        r11 = 0;
        r9 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r9 == 0) goto L_0x04a9;
    L_0x0452:
        r11 = r0.getDuration();	 Catch:{ Exception -> 0x0481 }
        r0 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1));
        if (r0 != 0) goto L_0x0463;
    L_0x045a:
        r0 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0481 }
        r0 = r0.getDuration();	 Catch:{ Exception -> 0x0481 }
        r5 = (long) r0;	 Catch:{ Exception -> 0x0481 }
        r11 = r5 * r3;
    L_0x0463:
        r0 = (float) r11;	 Catch:{ Exception -> 0x0481 }
        r3 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0481 }
        r3 = r3.audioProgress;	 Catch:{ Exception -> 0x0481 }
        r0 = r0 * r3;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0481 }
        r3 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0481 }
        r3 = r3.audioProgressMs;	 Catch:{ Exception -> 0x0481 }
        if (r3 == 0) goto L_0x047a;
    L_0x0471:
        r0 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0481 }
        r0 = r0.audioProgressMs;	 Catch:{ Exception -> 0x0481 }
        r3 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0481 }
        r4 = 0;
        r3.audioProgressMs = r4;	 Catch:{ Exception -> 0x0481 }
    L_0x047a:
        r3 = r1.videoPlayer;	 Catch:{ Exception -> 0x0481 }
        r4 = (long) r0;	 Catch:{ Exception -> 0x0481 }
        r3.seekTo(r4);	 Catch:{ Exception -> 0x0481 }
        goto L_0x04a9;
    L_0x0481:
        r0 = move-exception;
        r3 = r1.playingMessageObject;
        r4 = 0;
        r3.audioProgress = r4;
        r4 = 0;
        r3.audioProgressSec = r4;
        r2 = r2.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r5 = new java.lang.Object[r8];
        r6 = r1.playingMessageObject;
        r6 = r6.getId();
        r6 = java.lang.Integer.valueOf(r6);
        r5[r4] = r6;
        r4 = 1;
        r5[r4] = r10;
        r2.postNotificationName(r3, r5);
        org.telegram.messenger.FileLog.e(r0);
    L_0x04a9:
        r0 = r1.videoPlayer;
        r0.play();
        goto L_0x0502;
    L_0x04af:
        r0 = r1.audioPlayer;
        if (r0 == 0) goto L_0x0502;
    L_0x04b3:
        r9 = r1.playingMessageObject;	 Catch:{ Exception -> 0x04dc }
        r9 = r9.audioProgress;	 Catch:{ Exception -> 0x04dc }
        r11 = 0;
        r9 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r9 == 0) goto L_0x0502;
    L_0x04bc:
        r11 = r0.getDuration();	 Catch:{ Exception -> 0x04dc }
        r0 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1));
        if (r0 != 0) goto L_0x04cd;
    L_0x04c4:
        r0 = r1.playingMessageObject;	 Catch:{ Exception -> 0x04dc }
        r0 = r0.getDuration();	 Catch:{ Exception -> 0x04dc }
        r5 = (long) r0;	 Catch:{ Exception -> 0x04dc }
        r11 = r5 * r3;
    L_0x04cd:
        r0 = (float) r11;	 Catch:{ Exception -> 0x04dc }
        r3 = r1.playingMessageObject;	 Catch:{ Exception -> 0x04dc }
        r3 = r3.audioProgress;	 Catch:{ Exception -> 0x04dc }
        r0 = r0 * r3;
        r0 = (int) r0;	 Catch:{ Exception -> 0x04dc }
        r3 = r1.audioPlayer;	 Catch:{ Exception -> 0x04dc }
        r4 = (long) r0;	 Catch:{ Exception -> 0x04dc }
        r3.seekTo(r4);	 Catch:{ Exception -> 0x04dc }
        goto L_0x0502;
    L_0x04dc:
        r0 = move-exception;
        r3 = r1.playingMessageObject;
        r3.resetPlayingProgress();
        r2 = r2.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r4 = new java.lang.Object[r8];
        r5 = r1.playingMessageObject;
        r5 = r5.getId();
        r5 = java.lang.Integer.valueOf(r5);
        r6 = 0;
        r4[r6] = r5;
        r5 = 1;
        r4[r5] = r10;
        r2.postNotificationName(r3, r4);
        org.telegram.messenger.FileLog.e(r0);
    L_0x0502:
        r0 = r1.playingMessageObject;
        if (r0 == 0) goto L_0x0520;
    L_0x0506:
        r0 = r0.isMusic();
        if (r0 == 0) goto L_0x0520;
    L_0x050c:
        r0 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = org.telegram.messenger.MusicPlayerService.class;
        r0.<init>(r2, r3);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x051b }
        r2.startService(r0);	 Catch:{ Throwable -> 0x051b }
        goto L_0x052e;
    L_0x051b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x052e;
    L_0x0520:
        r0 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = org.telegram.messenger.MusicPlayerService.class;
        r0.<init>(r2, r3);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r2.stopService(r0);
    L_0x052e:
        r2 = 1;
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.playMessage(org.telegram.messenger.MessageObject):boolean");
    }

    public /* synthetic */ void lambda$playMessage$9$MediaController() {
        cleanupPlayer(true, true);
    }

    public AudioInfo getAudioInfo() {
        return this.audioInfo;
    }

    public void toggleShuffleMusic(int i) {
        boolean z = SharedConfig.shuffleMusic;
        SharedConfig.toggleShuffleMusic(i);
        boolean z2 = SharedConfig.shuffleMusic;
        if (z == z2) {
            return;
        }
        if (z2) {
            buildShuffledPlayList();
            this.currentPlaylistNum = 0;
            return;
        }
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null) {
            this.currentPlaylistNum = this.playlist.indexOf(messageObject);
            if (this.currentPlaylistNum == -1) {
                this.playlist.clear();
                this.shuffledPlaylist.clear();
                cleanupPlayer(true, true);
            }
        }
    }

    public boolean isCurrentPlayer(VideoPlayer videoPlayer) {
        return this.videoPlayer == videoPlayer || this.audioPlayer == videoPlayer;
    }

    /* renamed from: pauseMessage */
    public boolean lambda$startAudioAgain$5$MediaController(MessageObject messageObject) {
        if (!((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject))) {
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
            }
        }
        return false;
    }

    public boolean resumeAudio(MessageObject messageObject) {
        if (!((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject))) {
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
            }
        }
        return false;
    }

    public boolean isVideoDrawingReady() {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.currentAspectRatioFrameLayout;
        return aspectRatioFrameLayout != null && aspectRatioFrameLayout.isDrawingReady();
    }

    public ArrayList<MessageObject> getPlaylist() {
        return this.playlist;
    }

    public boolean isPlayingMessage(MessageObject messageObject) {
        if (!((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null)) {
            MessageObject messageObject2 = this.playingMessageObject;
            if (messageObject2 != null) {
                long j = messageObject2.eventId;
                if (j != 0 && j == messageObject.eventId) {
                    return this.downloadingCurrentMessage ^ 1;
                }
                if (isSamePlayingMessage(messageObject)) {
                    return this.downloadingCurrentMessage ^ 1;
                }
            }
        }
        return false;
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

    public void setReplyingMessage(MessageObject messageObject) {
        this.recordReplyingMessageObject = messageObject;
    }

    public void startRecording(int i, long j, MessageObject messageObject) {
        Object obj;
        MessageObject messageObject2 = this.playingMessageObject;
        if (messageObject2 == null || !isPlayingMessage(messageObject2) || isMessagePaused()) {
            obj = null;
        } else {
            obj = 1;
            lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
        }
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        -$$Lambda$MediaController$aNYrkuu1k-Q9tWrqTPM_3Nml9Zg -__lambda_mediacontroller_anyrkuu1k-q9twrqtpm_3nml9zg = new -$$Lambda$MediaController$aNYrkuu1k-Q9tWrqTPM_3Nml9Zg(this, i, j, messageObject);
        this.recordStartRunnable = -__lambda_mediacontroller_anyrkuu1k-q9twrqtpm_3nml9zg;
        dispatchQueue.postRunnable(-__lambda_mediacontroller_anyrkuu1k-q9twrqtpm_3nml9zg, obj != null ? 500 : 50);
    }

    public /* synthetic */ void lambda$startRecording$16$MediaController(int i, long j, MessageObject messageObject) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$0vK2qidtIRh3-XACyYZKBRfQ4hw(this, i));
            return;
        }
        this.recordingAudio = new TL_document();
        TL_document tL_document = this.recordingAudio;
        tL_document.file_reference = new byte[0];
        tL_document.dc_id = Integer.MIN_VALUE;
        tL_document.id = (long) SharedConfig.getLastLocalId();
        this.recordingAudio.user_id = UserConfig.getInstance(i).getClientUserId();
        tL_document = this.recordingAudio;
        tL_document.mime_type = "audio/ogg";
        tL_document.file_reference = new byte[0];
        SharedConfig.saveConfig();
        this.recordingAudioFile = new File(FileLoader.getDirectory(4), FileLoader.getAttachFileName(this.recordingAudio));
        try {
            if (startRecord(this.recordingAudioFile.getAbsolutePath()) == 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$nGj-0r_eXVZCxic8wg0ezzh44to(this, i));
                return;
            }
            this.audioRecorder = new AudioRecord(0, 16000, 16, 2, this.recordBufferSize * 10);
            this.recordStartTime = System.currentTimeMillis();
            this.recordTimeCount = 0;
            this.samplesCount = 0;
            this.recordDialogId = j;
            this.recordingCurrentAccount = i;
            this.recordReplyingMessageObject = messageObject;
            this.fileBuffer.rewind();
            this.audioRecorder.startRecording();
            this.recordQueue.postRunnable(this.recordRunnable);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$4mLjLT_A6jHHX3ZJ-fQaPM8p2w8(this, i));
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$Kr2i6XYE85QZs4bv6nfGdyZ2YL8(this, i));
        }
    }

    public /* synthetic */ void lambda$null$12$MediaController(int i) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
    }

    public /* synthetic */ void lambda$null$13$MediaController(int i) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
    }

    public /* synthetic */ void lambda$null$14$MediaController(int i) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, new Object[0]);
    }

    public /* synthetic */ void lambda$null$15$MediaController(int i) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStarted, new Object[0]);
    }

    public void generateWaveform(MessageObject messageObject) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageObject.getId());
        stringBuilder.append("_");
        stringBuilder.append(messageObject.getDialogId());
        String stringBuilder2 = stringBuilder.toString();
        String absolutePath = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(stringBuilder2)) {
            this.generatingWaveform.put(stringBuilder2, messageObject);
            Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$8PZv0oHSpRt-Io2cMkMt61RbJcc(this, absolutePath, stringBuilder2));
        }
    }

    public /* synthetic */ void lambda$generateWaveform$18$MediaController(String str, String str2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$a6_0vlMNb1v7JsUjt_pOJUmvDtE(this, str2, getWaveform(str)));
    }

    public /* synthetic */ void lambda$null$17$MediaController(String str, byte[] bArr) {
        MessageObject messageObject = (MessageObject) this.generatingWaveform.remove(str);
        if (!(messageObject == null || bArr == null)) {
            for (int i = 0; i < messageObject.getDocument().attributes.size(); i++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) messageObject.getDocument().attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
                    documentAttribute.waveform = bArr;
                    documentAttribute.flags |= 4;
                    break;
                }
            }
            messages_Messages tL_messages_messages = new TL_messages_messages();
            tL_messages_messages.messages.add(messageObject.messageOwner);
            MessagesStorage.getInstance(messageObject.currentAccount).putMessages(tL_messages_messages, messageObject.getDialogId(), -1, 0, false);
            new ArrayList().add(messageObject);
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), r11);
        }
    }

    private void stopRecordingInternal(int i) {
        if (i != 0) {
            this.fileEncodingQueue.postRunnable(new -$$Lambda$MediaController$xOPvRRaZp12WyiiCi3uolis74HI(this, this.recordingAudio, this.recordingAudioFile, i));
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

    public /* synthetic */ void lambda$stopRecordingInternal$20$MediaController(TL_document tL_document, File file, int i) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$0rWtUTn2QUBBNpu8veTtOVfGYSQ(this, tL_document, file, i));
    }

    public /* synthetic */ void lambda$null$19$MediaController(TL_document tL_document, File file, int i) {
        Document document = tL_document;
        int i2 = i;
        document.date = ConnectionsManager.getInstance(this.recordingCurrentAccount).getCurrentTime();
        document.size = (int) file.length();
        TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
        tL_documentAttributeAudio.voice = true;
        short[] sArr = this.recordSamples;
        tL_documentAttributeAudio.waveform = getWaveform2(sArr, sArr.length);
        if (tL_documentAttributeAudio.waveform != null) {
            tL_documentAttributeAudio.flags |= 4;
        }
        long j = this.recordTimeCount;
        tL_documentAttributeAudio.duration = (int) (j / 1000);
        document.attributes.add(tL_documentAttributeAudio);
        if (j > 700) {
            int i3;
            if (i2 == 1) {
                i3 = 1;
                SendMessagesHelper.getInstance(this.recordingCurrentAccount).sendMessage(tL_document, null, file.getAbsolutePath(), this.recordDialogId, this.recordReplyingMessageObject, null, null, null, null, 0, null);
            } else {
                i3 = 1;
            }
            NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
            int i4 = NotificationCenter.audioDidSent;
            Object[] objArr = new Object[2];
            String str = null;
            objArr[0] = i2 == 2 ? tL_document : null;
            if (i2 == 2) {
                str = file.getAbsolutePath();
            }
            objArr[i3] = str;
            instance.postNotificationName(i4, objArr);
            return;
        }
        NotificationCenter.getInstance(this.recordingCurrentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Boolean.valueOf(false));
        file.delete();
    }

    public void stopRecording(int i) {
        Runnable runnable = this.recordStartRunnable;
        if (runnable != null) {
            this.recordQueue.cancelRunnable(runnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new -$$Lambda$MediaController$9Mt4rRJGCphHi4pab0_bDovIi84(this, i));
    }

    public /* synthetic */ void lambda$stopRecording$22$MediaController(int i) {
        AudioRecord audioRecord = this.audioRecorder;
        if (audioRecord != null) {
            try {
                this.sendAfterDone = i;
                audioRecord.stop();
            } catch (Exception e) {
                FileLog.e(e);
                File file = this.recordingAudioFile;
                if (file != null) {
                    file.delete();
                }
            }
            if (i == 0) {
                stopRecordingInternal(0);
            }
            try {
                this.feedbackView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$armogyKrIRQ5ksTUkaLSIPyjvEA(this, i));
        }
    }

    public /* synthetic */ void lambda$null$21$MediaController(int i) {
        NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
        int i2 = NotificationCenter.recordStopped;
        int i3 = 1;
        Object[] objArr = new Object[1];
        if (i != 2) {
            i3 = 0;
        }
        objArr[0] = Integer.valueOf(i3);
        instance.postNotificationName(i2, objArr);
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0028  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0027 A:{RETURN} */
    public static void saveFile(java.lang.String r9, android.content.Context r10, int r11, java.lang.String r12, java.lang.String r13) {
        /*
        if (r9 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r0 = 0;
        if (r9 == 0) goto L_0x0024;
    L_0x0006:
        r1 = r9.length();
        if (r1 == 0) goto L_0x0024;
    L_0x000c:
        r1 = new java.io.File;
        r1.<init>(r9);
        r9 = r1.exists();
        if (r9 == 0) goto L_0x0024;
    L_0x0017:
        r9 = android.net.Uri.fromFile(r1);
        r9 = org.telegram.messenger.AndroidUtilities.isInternalUri(r9);
        if (r9 == 0) goto L_0x0022;
    L_0x0021:
        goto L_0x0024;
    L_0x0022:
        r5 = r1;
        goto L_0x0025;
    L_0x0024:
        r5 = r0;
    L_0x0025:
        if (r5 != 0) goto L_0x0028;
    L_0x0027:
        return;
    L_0x0028:
        r9 = 1;
        r6 = new boolean[r9];
        r1 = 0;
        r6[r1] = r1;
        r2 = r5.exists();
        if (r2 == 0) goto L_0x0076;
    L_0x0034:
        if (r10 == 0) goto L_0x0064;
    L_0x0036:
        if (r11 == 0) goto L_0x0064;
    L_0x0038:
        r2 = new org.telegram.ui.ActionBar.AlertDialog;	 Catch:{ Exception -> 0x0060 }
        r3 = 2;
        r2.<init>(r10, r3);	 Catch:{ Exception -> 0x0060 }
        r10 = "Loading";
        r0 = NUM; // 0x7f0d0558 float:1.874489E38 double:1.0531304534E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r0);	 Catch:{ Exception -> 0x005d }
        r2.setMessage(r10);	 Catch:{ Exception -> 0x005d }
        r2.setCanceledOnTouchOutside(r1);	 Catch:{ Exception -> 0x005d }
        r2.setCancelable(r9);	 Catch:{ Exception -> 0x005d }
        r9 = new org.telegram.messenger.-$$Lambda$MediaController$hrz-cghaZ1kTzzeIoiWSaviEy-E;	 Catch:{ Exception -> 0x005d }
        r9.<init>(r6);	 Catch:{ Exception -> 0x005d }
        r2.setOnCancelListener(r9);	 Catch:{ Exception -> 0x005d }
        r2.show();	 Catch:{ Exception -> 0x005d }
        r7 = r2;
        goto L_0x0065;
    L_0x005d:
        r9 = move-exception;
        r0 = r2;
        goto L_0x0061;
    L_0x0060:
        r9 = move-exception;
    L_0x0061:
        org.telegram.messenger.FileLog.e(r9);
    L_0x0064:
        r7 = r0;
    L_0x0065:
        r9 = new java.lang.Thread;
        r10 = new org.telegram.messenger.-$$Lambda$MediaController$nx3Q4nKr4qmGQfXNSzGCKTPGTMo;
        r2 = r10;
        r3 = r11;
        r4 = r12;
        r8 = r13;
        r2.<init>(r3, r4, r5, r6, r7, r8);
        r9.<init>(r10);
        r9.start();
    L_0x0076:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.saveFile(java.lang.String, android.content.Context, int, java.lang.String, java.lang.String):void");
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:77:0x0131=Splitter:B:77:0x0131, B:95:0x017a=Splitter:B:95:0x017a} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x009f A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00cb A:{Catch:{ Exception -> 0x0118, all -> 0x0102 }} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x010e A:{SYNTHETIC, Splitter:B:52:0x010e} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0113 A:{SYNTHETIC, Splitter:B:56:0x0113} */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0135 A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x013b A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0135 A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x013b A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:108:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:95:0x017a */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0128 A:{SYNTHETIC, Splitter:B:69:0x0128} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012d A:{SYNTHETIC, Splitter:B:73:0x012d} */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0135 A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x013b A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0172 A:{SYNTHETIC, Splitter:B:89:0x0172} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0177 A:{SYNTHETIC, Splitter:B:93:0x0177} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0172 A:{SYNTHETIC, Splitter:B:89:0x0172} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0177 A:{SYNTHETIC, Splitter:B:93:0x0177} */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:86|87|(0)|(0)|95|96) */
    static /* synthetic */ void lambda$saveFile$26(int r21, java.lang.String r22, java.io.File r23, boolean[] r24, org.telegram.ui.ActionBar.AlertDialog r25, java.lang.String r26) {
        /*
        r1 = r21;
        r0 = r22;
        r2 = r25;
        r3 = 2;
        r4 = 1;
        r5 = 0;
        if (r1 != 0) goto L_0x0015;
    L_0x000b:
        r0 = org.telegram.messenger.AndroidUtilities.generatePicturePath();	 Catch:{ Exception -> 0x0012 }
    L_0x000f:
        r10 = r0;
        goto L_0x0099;
    L_0x0012:
        r0 = move-exception;
        goto L_0x017b;
    L_0x0015:
        if (r1 != r4) goto L_0x001c;
    L_0x0017:
        r0 = org.telegram.messenger.AndroidUtilities.generateVideoPath();	 Catch:{ Exception -> 0x0012 }
        goto L_0x000f;
    L_0x001c:
        if (r1 != r3) goto L_0x0025;
    L_0x001e:
        r6 = android.os.Environment.DIRECTORY_DOWNLOADS;	 Catch:{ Exception -> 0x0012 }
        r6 = android.os.Environment.getExternalStoragePublicDirectory(r6);	 Catch:{ Exception -> 0x0012 }
        goto L_0x002b;
    L_0x0025:
        r6 = android.os.Environment.DIRECTORY_MUSIC;	 Catch:{ Exception -> 0x0012 }
        r6 = android.os.Environment.getExternalStoragePublicDirectory(r6);	 Catch:{ Exception -> 0x0012 }
    L_0x002b:
        r6.mkdir();	 Catch:{ Exception -> 0x0012 }
        r7 = new java.io.File;	 Catch:{ Exception -> 0x0012 }
        r7.<init>(r6, r0);	 Catch:{ Exception -> 0x0012 }
        r8 = r7.exists();	 Catch:{ Exception -> 0x0012 }
        if (r8 == 0) goto L_0x0098;
    L_0x0039:
        r8 = 46;
        r8 = r0.lastIndexOf(r8);	 Catch:{ Exception -> 0x0012 }
        r9 = r7;
        r7 = 0;
    L_0x0041:
        r10 = 10;
        if (r7 >= r10) goto L_0x0096;
    L_0x0045:
        r9 = -1;
        r10 = ")";
        r11 = "(";
        if (r8 == r9) goto L_0x006f;
    L_0x004c:
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0012 }
        r9.<init>();	 Catch:{ Exception -> 0x0012 }
        r12 = r0.substring(r5, r8);	 Catch:{ Exception -> 0x0012 }
        r9.append(r12);	 Catch:{ Exception -> 0x0012 }
        r9.append(r11);	 Catch:{ Exception -> 0x0012 }
        r11 = r7 + 1;
        r9.append(r11);	 Catch:{ Exception -> 0x0012 }
        r9.append(r10);	 Catch:{ Exception -> 0x0012 }
        r10 = r0.substring(r8);	 Catch:{ Exception -> 0x0012 }
        r9.append(r10);	 Catch:{ Exception -> 0x0012 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0012 }
        goto L_0x0086;
    L_0x006f:
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0012 }
        r9.<init>();	 Catch:{ Exception -> 0x0012 }
        r9.append(r0);	 Catch:{ Exception -> 0x0012 }
        r9.append(r11);	 Catch:{ Exception -> 0x0012 }
        r11 = r7 + 1;
        r9.append(r11);	 Catch:{ Exception -> 0x0012 }
        r9.append(r10);	 Catch:{ Exception -> 0x0012 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0012 }
    L_0x0086:
        r10 = new java.io.File;	 Catch:{ Exception -> 0x0012 }
        r10.<init>(r6, r9);	 Catch:{ Exception -> 0x0012 }
        r9 = r10.exists();	 Catch:{ Exception -> 0x0012 }
        if (r9 != 0) goto L_0x0092;
    L_0x0091:
        goto L_0x0099;
    L_0x0092:
        r7 = r7 + 1;
        r9 = r10;
        goto L_0x0041;
    L_0x0096:
        r10 = r9;
        goto L_0x0099;
    L_0x0098:
        r10 = r7;
    L_0x0099:
        r0 = r10.exists();	 Catch:{ Exception -> 0x0012 }
        if (r0 != 0) goto L_0x00a2;
    L_0x009f:
        r10.createNewFile();	 Catch:{ Exception -> 0x0012 }
    L_0x00a2:
        r6 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0012 }
        r8 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r6 = r6 - r8;
        r11 = 0;
        r0 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0121, all -> 0x011d }
        r12 = r23;
        r0.<init>(r12);	 Catch:{ Exception -> 0x0121, all -> 0x011d }
        r18 = r0.getChannel();	 Catch:{ Exception -> 0x0121, all -> 0x011d }
        r0 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r0.<init>(r10);	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r11 = r0.getChannel();	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r14 = r18.size();	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r12 = 0;
        r19 = r6;
        r6 = r12;
    L_0x00c7:
        r0 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1));
        if (r0 >= 0) goto L_0x010c;
    L_0x00cb:
        r0 = r24[r5];	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        if (r0 == 0) goto L_0x00d0;
    L_0x00cf:
        goto L_0x010c;
    L_0x00d0:
        r12 = r14 - r6;
        r3 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r16 = java.lang.Math.min(r3, r12);	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r12 = r11;
        r13 = r18;
        r0 = r14;
        r14 = r6;
        r12.transferFrom(r13, r14, r16);	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        if (r2 == 0) goto L_0x0105;
    L_0x00e2:
        r12 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r12 = r12 - r8;
        r14 = (r19 > r12 ? 1 : (r19 == r12 ? 0 : -1));
        if (r14 > 0) goto L_0x0105;
    L_0x00eb:
        r12 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r14 = (float) r6;	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r15 = (float) r0;	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r14 = r14 / r15;
        r15 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r14 = r14 * r15;
        r14 = (int) r14;	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r15 = new org.telegram.messenger.-$$Lambda$MediaController$d2YFTKUcKqMuUp1bMMx0EKpPu88;	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r15.<init>(r2, r14);	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r15);	 Catch:{ Exception -> 0x0118, all -> 0x0102 }
        r19 = r12;
        goto L_0x0105;
    L_0x0102:
        r0 = move-exception;
        goto L_0x0170;
    L_0x0105:
        r6 = r6 + r3;
        r14 = r0;
        r3 = 2;
        r4 = 1;
        r1 = r21;
        goto L_0x00c7;
    L_0x010c:
        if (r18 == 0) goto L_0x0111;
    L_0x010e:
        r18.close();	 Catch:{ Exception -> 0x0111 }
    L_0x0111:
        if (r11 == 0) goto L_0x0116;
    L_0x0113:
        r11.close();	 Catch:{ Exception -> 0x0116 }
    L_0x0116:
        r0 = 1;
        goto L_0x0131;
    L_0x0118:
        r0 = move-exception;
        r1 = r11;
        r11 = r18;
        goto L_0x0123;
    L_0x011d:
        r0 = move-exception;
        r18 = r11;
        goto L_0x0170;
    L_0x0121:
        r0 = move-exception;
        r1 = r11;
    L_0x0123:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x016c }
        if (r11 == 0) goto L_0x012b;
    L_0x0128:
        r11.close();	 Catch:{ Exception -> 0x012b }
    L_0x012b:
        if (r1 == 0) goto L_0x0130;
    L_0x012d:
        r1.close();	 Catch:{ Exception -> 0x0130 }
    L_0x0130:
        r0 = 0;
    L_0x0131:
        r1 = r24[r5];	 Catch:{ Exception -> 0x0012 }
        if (r1 == 0) goto L_0x0139;
    L_0x0135:
        r10.delete();	 Catch:{ Exception -> 0x0012 }
        r0 = 0;
    L_0x0139:
        if (r0 == 0) goto L_0x017e;
    L_0x013b:
        r3 = 2;
        r1 = r21;
        if (r1 != r3) goto L_0x0164;
    L_0x0140:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0012 }
        r1 = "download";
        r0 = r0.getSystemService(r1);	 Catch:{ Exception -> 0x0012 }
        r11 = r0;
        r11 = (android.app.DownloadManager) r11;	 Catch:{ Exception -> 0x0012 }
        r12 = r10.getName();	 Catch:{ Exception -> 0x0012 }
        r13 = r10.getName();	 Catch:{ Exception -> 0x0012 }
        r14 = 0;
        r16 = r10.getAbsolutePath();	 Catch:{ Exception -> 0x0012 }
        r17 = r10.length();	 Catch:{ Exception -> 0x0012 }
        r19 = 1;
        r15 = r26;
        r11.addCompletedDownload(r12, r13, r14, r15, r16, r17, r19);	 Catch:{ Exception -> 0x0012 }
        goto L_0x017e;
    L_0x0164:
        r0 = android.net.Uri.fromFile(r10);	 Catch:{ Exception -> 0x0012 }
        org.telegram.messenger.AndroidUtilities.addMediaToGallery(r0);	 Catch:{ Exception -> 0x0012 }
        goto L_0x017e;
    L_0x016c:
        r0 = move-exception;
        r18 = r11;
        r11 = r1;
    L_0x0170:
        if (r18 == 0) goto L_0x0175;
    L_0x0172:
        r18.close();	 Catch:{ Exception -> 0x0175 }
    L_0x0175:
        if (r11 == 0) goto L_0x017a;
    L_0x0177:
        r11.close();	 Catch:{ Exception -> 0x017a }
    L_0x017a:
        throw r0;	 Catch:{ Exception -> 0x0012 }
    L_0x017b:
        org.telegram.messenger.FileLog.e(r0);
    L_0x017e:
        if (r2 == 0) goto L_0x0188;
    L_0x0180:
        r0 = new org.telegram.messenger.-$$Lambda$MediaController$8qrRdww485ZG9hqc7_0dXYZW_go;
        r0.<init>(r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x0188:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$saveFile$26(int, java.lang.String, java.io.File, boolean[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$24(AlertDialog alertDialog, int i) {
        try {
            alertDialog.setProgress(i);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    static /* synthetic */ void lambda$null$25(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static boolean isWebp(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] bArr = new byte[12];
            if (inputStream.read(bArr, 0, 12) == 12) {
                String toLowerCase = new String(bArr).toLowerCase();
                if (toLowerCase.startsWith("riff") && toLowerCase.endsWith("webp")) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    return true;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        } catch (Exception e22) {
            FileLog.e(e22);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
        }
        return false;
    }

    public static boolean isGif(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] bArr = new byte[3];
            if (inputStream.read(bArr, 0, 3) == 3 && new String(bArr).equalsIgnoreCase("gif")) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                return true;
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            return false;
        } catch (Exception e22) {
            FileLog.e(e22);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0048  */
    /* JADX WARNING: Missing block: B:9:0x0032, code skipped:
            if (r1 != null) goto L_0x0034;
     */
    /* JADX WARNING: Missing block: B:10:0x0034, code skipped:
            r1.close();
     */
    /* JADX WARNING: Missing block: B:17:0x0041, code skipped:
            if (r1 != null) goto L_0x0034;
     */
    public static java.lang.String getFileName(android.net.Uri r10) {
        /*
        r0 = "_display_name";
        r1 = r10.getScheme();
        r2 = "content";
        r1 = r1.equals(r2);
        r2 = 1;
        r3 = 0;
        if (r1 == 0) goto L_0x004c;
    L_0x0010:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x003c, all -> 0x003a }
        r4 = r1.getContentResolver();	 Catch:{ Exception -> 0x003c, all -> 0x003a }
        r6 = new java.lang.String[r2];	 Catch:{ Exception -> 0x003c, all -> 0x003a }
        r1 = 0;
        r6[r1] = r0;	 Catch:{ Exception -> 0x003c, all -> 0x003a }
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r5 = r10;
        r1 = r4.query(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x003c, all -> 0x003a }
        r4 = r1.moveToFirst();	 Catch:{ Exception -> 0x0038 }
        if (r4 == 0) goto L_0x0032;
    L_0x0029:
        r0 = r1.getColumnIndex(r0);	 Catch:{ Exception -> 0x0038 }
        r0 = r1.getString(r0);	 Catch:{ Exception -> 0x0038 }
        r3 = r0;
    L_0x0032:
        if (r1 == 0) goto L_0x004c;
    L_0x0034:
        r1.close();
        goto L_0x004c;
    L_0x0038:
        r0 = move-exception;
        goto L_0x003e;
    L_0x003a:
        r10 = move-exception;
        goto L_0x0046;
    L_0x003c:
        r0 = move-exception;
        r1 = r3;
    L_0x003e:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0044 }
        if (r1 == 0) goto L_0x004c;
    L_0x0043:
        goto L_0x0034;
    L_0x0044:
        r10 = move-exception;
        r3 = r1;
    L_0x0046:
        if (r3 == 0) goto L_0x004b;
    L_0x0048:
        r3.close();
    L_0x004b:
        throw r10;
    L_0x004c:
        if (r3 != 0) goto L_0x0060;
    L_0x004e:
        r3 = r10.getPath();
        r10 = 47;
        r10 = r3.lastIndexOf(r10);
        r0 = -1;
        if (r10 == r0) goto L_0x0060;
    L_0x005b:
        r10 = r10 + r2;
        r3 = r3.substring(r10);
    L_0x0060:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.getFileName(android.net.Uri):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:56:0x00b5 A:{SYNTHETIC, Splitter:B:56:0x00b5} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00bf A:{SYNTHETIC, Splitter:B:61:0x00bf} */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x009d A:{SYNTHETIC, Splitter:B:43:0x009d} */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00a7 A:{SYNTHETIC, Splitter:B:48:0x00a7} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00b5 A:{SYNTHETIC, Splitter:B:56:0x00b5} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00bf A:{SYNTHETIC, Splitter:B:61:0x00bf} */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x009d A:{SYNTHETIC, Splitter:B:43:0x009d} */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00a7 A:{SYNTHETIC, Splitter:B:48:0x00a7} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00b5 A:{SYNTHETIC, Splitter:B:56:0x00b5} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00bf A:{SYNTHETIC, Splitter:B:61:0x00bf} */
    public static java.lang.String copyFileToCache(android.net.Uri r7, java.lang.String r8) {
        /*
        r0 = 0;
        r1 = getFileName(r7);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r1 = org.telegram.messenger.FileLoader.fixFileName(r1);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r2 = 0;
        if (r1 != 0) goto L_0x0027;
    L_0x000c:
        r1 = org.telegram.messenger.SharedConfig.getLastLocalId();	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        org.telegram.messenger.SharedConfig.saveConfig();	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r4 = "%d.%s";
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r5[r2] = r1;	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r1 = 1;
        r5[r1] = r8;	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r1 = java.lang.String.format(r3, r4, r5);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
    L_0x0027:
        r8 = new java.io.File;	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r3 = 4;
        r3 = org.telegram.messenger.FileLoader.getDirectory(r3);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r4 = "sharing/";
        r8.<init>(r3, r4);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r8.mkdirs();	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r3 = new java.io.File;	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r3.<init>(r8, r1);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r8 = android.net.Uri.fromFile(r3);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r8 = org.telegram.messenger.AndroidUtilities.isInternalUri(r8);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        if (r8 == 0) goto L_0x0046;
    L_0x0045:
        return r0;
    L_0x0046:
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r8 = r8.getContentResolver();	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r7 = r8.openInputStream(r7);	 Catch:{ Exception -> 0x0095, all -> 0x0092 }
        r8 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x008c, all -> 0x0086 }
        r8.<init>(r3);	 Catch:{ Exception -> 0x008c, all -> 0x0086 }
        r1 = 20480; // 0x5000 float:2.8699E-41 double:1.01185E-319;
        r1 = new byte[r1];	 Catch:{ Exception -> 0x0080, all -> 0x007b }
    L_0x0059:
        r4 = r7.read(r1);	 Catch:{ Exception -> 0x0080, all -> 0x007b }
        r5 = -1;
        if (r4 == r5) goto L_0x0064;
    L_0x0060:
        r8.write(r1, r2, r4);	 Catch:{ Exception -> 0x0080, all -> 0x007b }
        goto L_0x0059;
    L_0x0064:
        r0 = r3.getAbsolutePath();	 Catch:{ Exception -> 0x0080, all -> 0x007b }
        if (r7 == 0) goto L_0x0072;
    L_0x006a:
        r7.close();	 Catch:{ Exception -> 0x006e }
        goto L_0x0072;
    L_0x006e:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x0072:
        r8.close();	 Catch:{ Exception -> 0x0076 }
        goto L_0x007a;
    L_0x0076:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x007a:
        return r0;
    L_0x007b:
        r0 = move-exception;
        r6 = r0;
        r0 = r7;
        r7 = r6;
        goto L_0x00b3;
    L_0x0080:
        r1 = move-exception;
        r6 = r8;
        r8 = r7;
        r7 = r1;
        r1 = r6;
        goto L_0x0098;
    L_0x0086:
        r8 = move-exception;
        r6 = r0;
        r0 = r7;
        r7 = r8;
        r8 = r6;
        goto L_0x00b3;
    L_0x008c:
        r8 = move-exception;
        r1 = r0;
        r6 = r8;
        r8 = r7;
        r7 = r6;
        goto L_0x0098;
    L_0x0092:
        r7 = move-exception;
        r8 = r0;
        goto L_0x00b3;
    L_0x0095:
        r7 = move-exception;
        r8 = r0;
        r1 = r8;
    L_0x0098:
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ all -> 0x00b0 }
        if (r8 == 0) goto L_0x00a5;
    L_0x009d:
        r8.close();	 Catch:{ Exception -> 0x00a1 }
        goto L_0x00a5;
    L_0x00a1:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x00a5:
        if (r1 == 0) goto L_0x00af;
    L_0x00a7:
        r1.close();	 Catch:{ Exception -> 0x00ab }
        goto L_0x00af;
    L_0x00ab:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x00af:
        return r0;
    L_0x00b0:
        r7 = move-exception;
        r0 = r8;
        r8 = r1;
    L_0x00b3:
        if (r0 == 0) goto L_0x00bd;
    L_0x00b5:
        r0.close();	 Catch:{ Exception -> 0x00b9 }
        goto L_0x00bd;
    L_0x00b9:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00bd:
        if (r8 == 0) goto L_0x00c7;
    L_0x00bf:
        r8.close();	 Catch:{ Exception -> 0x00c3 }
        goto L_0x00c7;
    L_0x00c3:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x00c7:
        goto L_0x00c9;
    L_0x00c8:
        throw r7;
    L_0x00c9:
        goto L_0x00c8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.copyFileToCache(android.net.Uri, java.lang.String):java.lang.String");
    }

    public static void loadGalleryPhotosAlbums(int i) {
        Thread thread = new Thread(new -$$Lambda$MediaController$SBcnC-DI67Ol01XNU9e0YE4yw3c(i));
        thread.setPriority(1);
        thread.start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x01fb A:{SYNTHETIC, Splitter:B:111:0x01fb} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x037b A:{SYNTHETIC, Splitter:B:206:0x037b} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x037b A:{SYNTHETIC, Splitter:B:206:0x037b} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x03ba A:{SYNTHETIC, Splitter:B:226:0x03ba} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x037b A:{SYNTHETIC, Splitter:B:206:0x037b} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x03ba A:{SYNTHETIC, Splitter:B:226:0x03ba} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x03ba A:{SYNTHETIC, Splitter:B:226:0x03ba} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0196 A:{Splitter:B:44:0x00f3, PHI: r30 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x037b A:{SYNTHETIC, Splitter:B:206:0x037b} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d4 A:{Splitter:B:18:0x007f, PHI: r15 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d4 A:{Splitter:B:18:0x007f, PHI: r15 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0196 A:{Splitter:B:44:0x00f3, PHI: r30 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d4 A:{Splitter:B:18:0x007f, PHI: r15 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d4 A:{Splitter:B:18:0x007f, PHI: r15 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d4 A:{Splitter:B:18:0x007f, PHI: r15 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0223 A:{SYNTHETIC, Splitter:B:124:0x0223} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0232 A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x025a A:{Catch:{ Throwable -> 0x0372 }} */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0368 A:{SYNTHETIC, Splitter:B:195:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0196 A:{Splitter:B:44:0x00f3, PHI: r30 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0390 A:{LOOP_END, LOOP:2: B:212:0x038a->B:214:0x0390} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:50:0x0101, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:51:0x0102, code skipped:
            r30 = r15;
     */
    /* JADX WARNING: Missing block: B:61:0x0125, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:63:0x0128, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:64:0x0129, code skipped:
            r31 = r5;
     */
    /* JADX WARNING: Missing block: B:88:0x0196, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:89:0x0198, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:90:0x0199, code skipped:
            r24 = r4;
     */
    /* JADX WARNING: Missing block: B:98:0x01c8, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:99:0x01ca, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:100:0x01cb, code skipped:
            r21 = r2;
     */
    /* JADX WARNING: Missing block: B:101:0x01ce, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:102:0x01cf, code skipped:
            r21 = r2;
            r20 = r3;
     */
    /* JADX WARNING: Missing block: B:103:0x01d4, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:104:0x01d5, code skipped:
            r30 = r15;
     */
    /* JADX WARNING: Missing block: B:105:0x01d7, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:106:0x01da, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:107:0x01db, code skipped:
            r21 = r2;
            r20 = r3;
            r19 = r4;
     */
    /* JADX WARNING: Missing block: B:108:0x01e1, code skipped:
            r31 = r5;
            r12 = r15;
            r3 = 0;
     */
    /* JADX WARNING: Missing block: B:125:?, code skipped:
            r12.close();
     */
    /* JADX WARNING: Missing block: B:126:0x0227, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:127:0x0228, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:227:?, code skipped:
            r30.close();
     */
    /* JADX WARNING: Missing block: B:228:0x03be, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:229:0x03bf, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    static /* synthetic */ void lambda$loadGalleryPhotosAlbums$28(int r39) {
        /*
        r1 = "AllMedia";
        r2 = "datetaken";
        r3 = "_data";
        r4 = "bucket_display_name";
        r5 = "bucket_id";
        r6 = "_id";
        r7 = "android.permission.READ_EXTERNAL_STORAGE";
        r9 = new java.util.ArrayList;
        r9.<init>();
        r10 = new java.util.ArrayList;
        r10.<init>();
        r8 = new android.util.SparseArray;
        r8.<init>();
        r11 = new android.util.SparseArray;
        r11.<init>();
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x003f }
        r0.<init>();	 Catch:{ Exception -> 0x003f }
        r13 = android.os.Environment.DIRECTORY_DCIM;	 Catch:{ Exception -> 0x003f }
        r13 = android.os.Environment.getExternalStoragePublicDirectory(r13);	 Catch:{ Exception -> 0x003f }
        r13 = r13.getAbsolutePath();	 Catch:{ Exception -> 0x003f }
        r0.append(r13);	 Catch:{ Exception -> 0x003f }
        r13 = "/Camera/";
        r0.append(r13);	 Catch:{ Exception -> 0x003f }
        r0 = r0.toString();	 Catch:{ Exception -> 0x003f }
        r13 = r0;
        goto L_0x0044;
    L_0x003f:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r13 = 0;
    L_0x0044:
        r15 = 23;
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x020f, all -> 0x0209 }
        if (r0 < r15) goto L_0x0069;
    L_0x004a:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x020f, all -> 0x0209 }
        if (r0 < r15) goto L_0x0057;
    L_0x004e:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x020f, all -> 0x0209 }
        r0 = r0.checkSelfPermission(r7);	 Catch:{ Throwable -> 0x020f, all -> 0x0209 }
        if (r0 != 0) goto L_0x0057;
    L_0x0056:
        goto L_0x0069;
    L_0x0057:
        r21 = r2;
        r20 = r3;
        r19 = r4;
        r31 = r5;
        r22 = 0;
        r24 = 0;
        r25 = 0;
        r30 = 0;
        goto L_0x01f9;
    L_0x0069:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x020f, all -> 0x0209 }
        r17 = r0.getContentResolver();	 Catch:{ Throwable -> 0x020f, all -> 0x0209 }
        r18 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x020f, all -> 0x0209 }
        r19 = projectionPhotos;	 Catch:{ Throwable -> 0x020f, all -> 0x0209 }
        r20 = 0;
        r21 = 0;
        r22 = "datetaken DESC";
        r15 = android.provider.MediaStore.Images.Media.query(r17, r18, r19, r20, r21, r22);	 Catch:{ Throwable -> 0x020f, all -> 0x0209 }
        if (r15 == 0) goto L_0x01e9;
    L_0x007f:
        r0 = r15.getColumnIndex(r6);	 Catch:{ Throwable -> 0x01da, all -> 0x01d4 }
        r14 = r15.getColumnIndex(r5);	 Catch:{ Throwable -> 0x01da, all -> 0x01d4 }
        r12 = r15.getColumnIndex(r4);	 Catch:{ Throwable -> 0x01da, all -> 0x01d4 }
        r19 = r4;
        r4 = r15.getColumnIndex(r3);	 Catch:{ Throwable -> 0x01ce, all -> 0x01d4 }
        r20 = r3;
        r3 = r15.getColumnIndex(r2);	 Catch:{ Throwable -> 0x01ca, all -> 0x01d4 }
        r21 = r2;
        r2 = "orientation";
        r2 = r15.getColumnIndex(r2);	 Catch:{ Throwable -> 0x01c8, all -> 0x01d4 }
        r22 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
    L_0x00a7:
        r27 = r15.moveToNext();	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        if (r27 == 0) goto L_0x01b8;
    L_0x00ad:
        r30 = r15.getInt(r0);	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        r27 = r0;
        r0 = r15.getInt(r14);	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        r36 = r14;
        r14 = r15.getString(r12);	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        r37 = r12;
        r12 = r15.getString(r4);	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        r31 = r15.getLong(r3);	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        r34 = r15.getInt(r2);	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        if (r12 == 0) goto L_0x019c;
    L_0x00cd:
        r28 = r12.length();	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        if (r28 != 0) goto L_0x00d5;
    L_0x00d3:
        goto L_0x019c;
    L_0x00d5:
        r38 = r2;
        r2 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        r35 = 0;
        r28 = r2;
        r29 = r0;
        r33 = r12;
        r28.<init>(r29, r30, r31, r33, r34, r35);	 Catch:{ Throwable -> 0x01bd, all -> 0x01d4 }
        if (r22 != 0) goto L_0x0108;
    L_0x00e6:
        r28 = r3;
        r3 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0101, all -> 0x01d4 }
        r29 = r4;
        r4 = "AllPhotos";
        r30 = r15;
        r15 = NUM; // 0x7f0d00cb float:1.8742526E38 double:1.053129878E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r15);	 Catch:{ Throwable -> 0x00ff, all -> 0x0196 }
        r15 = 0;
        r3.<init>(r15, r4, r2);	 Catch:{ Throwable -> 0x00ff, all -> 0x0196 }
        r10.add(r15, r3);	 Catch:{ Throwable -> 0x0128, all -> 0x0196 }
        goto L_0x0110;
    L_0x00ff:
        r0 = move-exception;
        goto L_0x0104;
    L_0x0101:
        r0 = move-exception;
        r30 = r15;
    L_0x0104:
        r31 = r5;
        goto L_0x01c2;
    L_0x0108:
        r28 = r3;
        r29 = r4;
        r30 = r15;
        r3 = r22;
    L_0x0110:
        if (r24 != 0) goto L_0x012d;
    L_0x0112:
        r4 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0128, all -> 0x0196 }
        r31 = r5;
        r15 = NUM; // 0x7f0d00ca float:1.8742524E38 double:1.0531298773E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r1, r15);	 Catch:{ Throwable -> 0x0125, all -> 0x0196 }
        r15 = 0;
        r4.<init>(r15, r5, r2);	 Catch:{ Throwable -> 0x0125, all -> 0x0196 }
        r9.add(r15, r4);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        goto L_0x0131;
    L_0x0125:
        r0 = move-exception;
        goto L_0x01c4;
    L_0x0128:
        r0 = move-exception;
        r31 = r5;
        goto L_0x01c4;
    L_0x012d:
        r31 = r5;
        r4 = r24;
    L_0x0131:
        r3.addPhoto(r2);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r4.addPhoto(r2);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r5 = r8.get(r0);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5;	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        if (r5 != 0) goto L_0x0161;
    L_0x013f:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r5.<init>(r0, r14, r2);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r8.put(r0, r5);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        if (r25 != 0) goto L_0x015e;
    L_0x0149:
        if (r13 == 0) goto L_0x015e;
    L_0x014b:
        if (r12 == 0) goto L_0x015e;
    L_0x014d:
        r15 = r12.startsWith(r13);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        if (r15 == 0) goto L_0x015e;
    L_0x0153:
        r15 = 0;
        r9.add(r15, r5);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r15 = java.lang.Integer.valueOf(r0);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r25 = r15;
        goto L_0x0161;
    L_0x015e:
        r9.add(r5);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
    L_0x0161:
        r5.addPhoto(r2);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r5 = r11.get(r0);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5;	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        if (r5 != 0) goto L_0x018e;
    L_0x016c:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r5.<init>(r0, r14, r2);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r11.put(r0, r5);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        if (r26 != 0) goto L_0x018b;
    L_0x0176:
        if (r13 == 0) goto L_0x018b;
    L_0x0178:
        if (r12 == 0) goto L_0x018b;
    L_0x017a:
        r12 = r12.startsWith(r13);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        if (r12 == 0) goto L_0x018b;
    L_0x0180:
        r12 = 0;
        r10.add(r12, r5);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r26 = r0;
        goto L_0x018e;
    L_0x018b:
        r10.add(r5);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
    L_0x018e:
        r5.addPhoto(r2);	 Catch:{ Throwable -> 0x0198, all -> 0x0196 }
        r22 = r3;
        r24 = r4;
        goto L_0x01a6;
    L_0x0196:
        r0 = move-exception;
        goto L_0x01d7;
    L_0x0198:
        r0 = move-exception;
        r24 = r4;
        goto L_0x01c4;
    L_0x019c:
        r38 = r2;
        r28 = r3;
        r29 = r4;
        r31 = r5;
        r30 = r15;
    L_0x01a6:
        r0 = r27;
        r3 = r28;
        r4 = r29;
        r15 = r30;
        r5 = r31;
        r14 = r36;
        r12 = r37;
        r2 = r38;
        goto L_0x00a7;
    L_0x01b8:
        r31 = r5;
        r30 = r15;
        goto L_0x01f9;
    L_0x01bd:
        r0 = move-exception;
        r31 = r5;
        r30 = r15;
    L_0x01c2:
        r3 = r22;
    L_0x01c4:
        r12 = r30;
        goto L_0x021e;
    L_0x01c8:
        r0 = move-exception;
        goto L_0x01e1;
    L_0x01ca:
        r0 = move-exception;
        r21 = r2;
        goto L_0x01e1;
    L_0x01ce:
        r0 = move-exception;
        r21 = r2;
        r20 = r3;
        goto L_0x01e1;
    L_0x01d4:
        r0 = move-exception;
        r30 = r15;
    L_0x01d7:
        r1 = r0;
        goto L_0x03b8;
    L_0x01da:
        r0 = move-exception;
        r21 = r2;
        r20 = r3;
        r19 = r4;
    L_0x01e1:
        r31 = r5;
        r30 = r15;
        r12 = r30;
        r3 = 0;
        goto L_0x021a;
    L_0x01e9:
        r21 = r2;
        r20 = r3;
        r19 = r4;
        r31 = r5;
        r30 = r15;
        r22 = 0;
        r24 = 0;
        r25 = 0;
    L_0x01f9:
        if (r30 == 0) goto L_0x0204;
    L_0x01fb:
        r30.close();	 Catch:{ Exception -> 0x01ff }
        goto L_0x0204;
    L_0x01ff:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0204:
        r3 = r22;
        r12 = r30;
        goto L_0x022c;
    L_0x0209:
        r0 = move-exception;
        r1 = r0;
        r30 = 0;
        goto L_0x03b8;
    L_0x020f:
        r0 = move-exception;
        r21 = r2;
        r20 = r3;
        r19 = r4;
        r31 = r5;
        r3 = 0;
        r12 = 0;
    L_0x021a:
        r24 = 0;
        r25 = 0;
    L_0x021e:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x03b4 }
        if (r12 == 0) goto L_0x022c;
    L_0x0223:
        r12.close();	 Catch:{ Exception -> 0x0227 }
        goto L_0x022c;
    L_0x0227:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x022c:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0372 }
        r2 = 23;
        if (r0 < r2) goto L_0x0244;
    L_0x0232:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0372 }
        if (r0 < r2) goto L_0x023f;
    L_0x0236:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0372 }
        r0 = r0.checkSelfPermission(r7);	 Catch:{ Throwable -> 0x0372 }
        if (r0 != 0) goto L_0x023f;
    L_0x023e:
        goto L_0x0244;
    L_0x023f:
        r15 = 0;
        r16 = 0;
        goto L_0x0366;
    L_0x0244:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0372 }
        r32 = r0.getContentResolver();	 Catch:{ Throwable -> 0x0372 }
        r33 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x0372 }
        r34 = projectionVideo;	 Catch:{ Throwable -> 0x0372 }
        r35 = 0;
        r36 = 0;
        r37 = "datetaken DESC";
        r12 = android.provider.MediaStore.Images.Media.query(r32, r33, r34, r35, r36, r37);	 Catch:{ Throwable -> 0x0372 }
        if (r12 == 0) goto L_0x023f;
    L_0x025a:
        r0 = r12.getColumnIndex(r6);	 Catch:{ Throwable -> 0x0372 }
        r2 = r31;
        r2 = r12.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0372 }
        r4 = r19;
        r4 = r12.getColumnIndex(r4);	 Catch:{ Throwable -> 0x0372 }
        r5 = r20;
        r5 = r12.getColumnIndex(r5);	 Catch:{ Throwable -> 0x0372 }
        r6 = r21;
        r6 = r12.getColumnIndex(r6);	 Catch:{ Throwable -> 0x0372 }
        r7 = "duration";
        r7 = r12.getColumnIndex(r7);	 Catch:{ Throwable -> 0x0372 }
        r16 = 0;
    L_0x027e:
        r11 = r12.moveToNext();	 Catch:{ Throwable -> 0x0363 }
        if (r11 == 0) goto L_0x0361;
    L_0x0284:
        r28 = r12.getInt(r0);	 Catch:{ Throwable -> 0x0363 }
        r11 = r12.getInt(r2);	 Catch:{ Throwable -> 0x0363 }
        r14 = r12.getString(r4);	 Catch:{ Throwable -> 0x0363 }
        r15 = r12.getString(r5);	 Catch:{ Throwable -> 0x0363 }
        r29 = r12.getLong(r6);	 Catch:{ Throwable -> 0x0363 }
        r19 = r12.getLong(r7);	 Catch:{ Throwable -> 0x0363 }
        if (r15 == 0) goto L_0x034a;
    L_0x029e:
        r21 = r15.length();	 Catch:{ Throwable -> 0x0363 }
        if (r21 != 0) goto L_0x02a6;
    L_0x02a4:
        goto L_0x034a;
    L_0x02a6:
        r21 = r0;
        r0 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x0363 }
        r22 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r34 = r4;
        r35 = r5;
        r4 = r19 / r22;
        r5 = (int) r4;	 Catch:{ Throwable -> 0x0363 }
        r33 = 1;
        r26 = r0;
        r27 = r11;
        r31 = r15;
        r32 = r5;
        r26.<init>(r27, r28, r29, r31, r32, r33);	 Catch:{ Throwable -> 0x0363 }
        if (r16 != 0) goto L_0x02e0;
    L_0x02c2:
        r4 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0363 }
        r5 = "AllVideos";
        r19 = r2;
        r2 = NUM; // 0x7f0d00cc float:1.8742529E38 double:1.0531298783E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r5, r2);	 Catch:{ Throwable -> 0x0363 }
        r5 = 0;
        r4.<init>(r5, r2, r0);	 Catch:{ Throwable -> 0x0363 }
        if (r24 == 0) goto L_0x02d7;
    L_0x02d5:
        r2 = 1;
        goto L_0x02d8;
    L_0x02d7:
        r2 = 0;
    L_0x02d8:
        if (r3 == 0) goto L_0x02dc;
    L_0x02da:
        r2 = r2 + 1;
    L_0x02dc:
        r9.add(r2, r4);	 Catch:{ Throwable -> 0x02fd }
        goto L_0x02e4;
    L_0x02e0:
        r19 = r2;
        r4 = r16;
    L_0x02e4:
        if (r24 != 0) goto L_0x0302;
    L_0x02e6:
        r2 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x02fd }
        r17 = r6;
        r5 = NUM; // 0x7f0d00ca float:1.8742524E38 double:1.0531298773E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r1, r5);	 Catch:{ Throwable -> 0x02fd }
        r5 = 0;
        r2.<init>(r5, r6, r0);	 Catch:{ Throwable -> 0x02fd }
        r9.add(r5, r2);	 Catch:{ Throwable -> 0x02f9 }
        goto L_0x0306;
    L_0x02f9:
        r0 = move-exception;
        r24 = r2;
        goto L_0x02fe;
    L_0x02fd:
        r0 = move-exception;
    L_0x02fe:
        r16 = r4;
        goto L_0x0364;
    L_0x0302:
        r17 = r6;
        r2 = r24;
    L_0x0306:
        r4.addPhoto(r0);	 Catch:{ Throwable -> 0x0343 }
        r2.addPhoto(r0);	 Catch:{ Throwable -> 0x0343 }
        r5 = r8.get(r11);	 Catch:{ Throwable -> 0x0343 }
        r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5;	 Catch:{ Throwable -> 0x0343 }
        if (r5 != 0) goto L_0x0338;
    L_0x0314:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0343 }
        r5.<init>(r11, r14, r0);	 Catch:{ Throwable -> 0x0343 }
        r8.put(r11, r5);	 Catch:{ Throwable -> 0x0343 }
        if (r25 != 0) goto L_0x0333;
    L_0x031e:
        if (r13 == 0) goto L_0x0333;
    L_0x0320:
        if (r15 == 0) goto L_0x0333;
    L_0x0322:
        r6 = r15.startsWith(r13);	 Catch:{ Throwable -> 0x0343 }
        if (r6 == 0) goto L_0x0333;
    L_0x0328:
        r15 = 0;
        r9.add(r15, r5);	 Catch:{ Throwable -> 0x0341 }
        r6 = java.lang.Integer.valueOf(r11);	 Catch:{ Throwable -> 0x0341 }
        r25 = r6;
        goto L_0x0339;
    L_0x0333:
        r15 = 0;
        r9.add(r5);	 Catch:{ Throwable -> 0x0341 }
        goto L_0x0339;
    L_0x0338:
        r15 = 0;
    L_0x0339:
        r5.addPhoto(r0);	 Catch:{ Throwable -> 0x0341 }
        r24 = r2;
        r16 = r4;
        goto L_0x0355;
    L_0x0341:
        r0 = move-exception;
        goto L_0x0345;
    L_0x0343:
        r0 = move-exception;
        r15 = 0;
    L_0x0345:
        r24 = r2;
        r16 = r4;
        goto L_0x0376;
    L_0x034a:
        r21 = r0;
        r19 = r2;
        r34 = r4;
        r35 = r5;
        r17 = r6;
        r15 = 0;
    L_0x0355:
        r6 = r17;
        r2 = r19;
        r0 = r21;
        r4 = r34;
        r5 = r35;
        goto L_0x027e;
    L_0x0361:
        r15 = 0;
        goto L_0x0366;
    L_0x0363:
        r0 = move-exception;
    L_0x0364:
        r15 = 0;
        goto L_0x0376;
    L_0x0366:
        if (r12 == 0) goto L_0x0384;
    L_0x0368:
        r12.close();	 Catch:{ Exception -> 0x036c }
        goto L_0x0384;
    L_0x036c:
        r0 = move-exception;
        r1 = r0;
        goto L_0x0381;
    L_0x036f:
        r0 = move-exception;
        r1 = r0;
        goto L_0x03a8;
    L_0x0372:
        r0 = move-exception;
        r15 = 0;
        r16 = 0;
    L_0x0376:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x036f }
        if (r12 == 0) goto L_0x0384;
    L_0x037b:
        r12.close();	 Catch:{ Exception -> 0x037f }
        goto L_0x0384;
    L_0x037f:
        r0 = move-exception;
        r1 = r0;
    L_0x0381:
        org.telegram.messenger.FileLog.e(r1);
    L_0x0384:
        r14 = r16;
        r12 = r24;
        r11 = r25;
    L_0x038a:
        r0 = r9.size();
        if (r15 >= r0) goto L_0x03a0;
    L_0x0390:
        r0 = r9.get(r15);
        r0 = (org.telegram.messenger.MediaController.AlbumEntry) r0;
        r0 = r0.photos;
        r1 = org.telegram.messenger.-$$Lambda$MediaController$8Ha8hH_xAKjV0FeIhj43YMHbiZQ.INSTANCE;
        java.util.Collections.sort(r0, r1);
        r15 = r15 + 1;
        goto L_0x038a;
    L_0x03a0:
        r15 = 0;
        r8 = r39;
        r13 = r3;
        broadcastNewPhotos(r8, r9, r10, r11, r12, r13, r14, r15);
        return;
    L_0x03a8:
        if (r12 == 0) goto L_0x03b3;
    L_0x03aa:
        r12.close();	 Catch:{ Exception -> 0x03ae }
        goto L_0x03b3;
    L_0x03ae:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x03b3:
        throw r1;
    L_0x03b4:
        r0 = move-exception;
        r1 = r0;
        r30 = r12;
    L_0x03b8:
        if (r30 == 0) goto L_0x03c3;
    L_0x03ba:
        r30.close();	 Catch:{ Exception -> 0x03be }
        goto L_0x03c3;
    L_0x03be:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x03c3:
        goto L_0x03c5;
    L_0x03c4:
        throw r1;
    L_0x03c5:
        goto L_0x03c4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$loadGalleryPhotosAlbums$28(int):void");
    }

    static /* synthetic */ int lambda$null$27(PhotoEntry photoEntry, PhotoEntry photoEntry2) {
        long j = photoEntry.dateTaken;
        long j2 = photoEntry2.dateTaken;
        if (j < j2) {
            return 1;
        }
        return j > j2 ? -1 : 0;
    }

    private static void broadcastNewPhotos(int i, ArrayList<AlbumEntry> arrayList, ArrayList<AlbumEntry> arrayList2, Integer num, AlbumEntry albumEntry, AlbumEntry albumEntry2, AlbumEntry albumEntry3, int i2) {
        Runnable runnable = broadcastPhotosRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        -$$Lambda$MediaController$FEhqTC-6dgiRDF8VPuSngG1CY9Y -__lambda_mediacontroller_fehqtc-6dgirdf8vpusngg1cy9y = new -$$Lambda$MediaController$FEhqTC-6dgiRDF8VPuSngG1CY9Y(i, arrayList, arrayList2, num, albumEntry, albumEntry2, albumEntry3);
        broadcastPhotosRunnable = -__lambda_mediacontroller_fehqtc-6dgirdf8vpusngg1cy9y;
        AndroidUtilities.runOnUIThread(-__lambda_mediacontroller_fehqtc-6dgirdf8vpusngg1cy9y, (long) i2);
    }

    static /* synthetic */ void lambda$broadcastNewPhotos$29(int i, ArrayList arrayList, ArrayList arrayList2, Integer num, AlbumEntry albumEntry, AlbumEntry albumEntry2, AlbumEntry albumEntry3) {
        if (PhotoViewer.getInstance().isVisible()) {
            broadcastNewPhotos(i, arrayList, arrayList2, num, albumEntry, albumEntry2, albumEntry3, 1000);
            return;
        }
        broadcastPhotosRunnable = null;
        allPhotosAlbumEntry = albumEntry2;
        allMediaAlbumEntry = albumEntry;
        allVideosAlbumEntry = albumEntry3;
        for (int i2 = 0; i2 < 3; i2++) {
            NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.albumsDidLoad, Integer.valueOf(i), arrayList, arrayList2, num);
        }
    }

    public void scheduleVideoConvert(MessageObject messageObject) {
        scheduleVideoConvert(messageObject, false);
    }

    public boolean scheduleVideoConvert(MessageObject messageObject, boolean z) {
        if (messageObject == null || messageObject.videoEditedInfo == null) {
            return false;
        }
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
                FileLog.e(th);
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
        int i;
        CodecCapabilities capabilitiesForType = mediaCodecInfo.getCapabilitiesForType(str);
        int i2 = 0;
        int i3 = 0;
        while (true) {
            int[] iArr = capabilitiesForType.colorFormats;
            if (i2 >= iArr.length) {
                return i3;
            }
            i = iArr[i2];
            if (isRecognizedFormat(i)) {
                if (!mediaCodecInfo.getName().equals("OMX.SEC.AVC.Encoder") || i != 19) {
                    return i;
                }
                i3 = i;
            }
            i2++;
        }
        return i;
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

    private void didWriteData(MessageObject messageObject, File file, boolean z, long j, boolean z2) {
        boolean z3 = this.videoConvertFirstWrite;
        if (z3) {
            this.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$fwtpfrjmuwNTgxHqhKcXwShkFp0(this, z2, z, messageObject, file, z3, j));
    }

    public /* synthetic */ void lambda$didWriteData$30$MediaController(boolean z, boolean z2, MessageObject messageObject, File file, boolean z3, long j) {
        if (z || z2) {
            synchronized (this.videoConvertSync) {
                this.cancelCurrentVideoConversion = false;
            }
            this.videoConvertQueue.remove(messageObject);
            startVideoConvertFromQueue();
        }
        if (z) {
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.filePreparingFailed, messageObject, file.toString());
            return;
        }
        if (z3) {
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.filePreparingStarted, messageObject, file.toString());
        }
        NotificationCenter instance = NotificationCenter.getInstance(messageObject.currentAccount);
        int i = NotificationCenter.fileNewChunkAvailable;
        Object[] objArr = new Object[4];
        objArr[0] = messageObject;
        objArr[1] = file.toString();
        objArr[2] = Long.valueOf(j);
        objArr[3] = Long.valueOf(z2 ? file.length() : 0);
        instance.postNotificationName(i, objArr);
    }

    /* JADX WARNING: Removed duplicated region for block: B:62:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x010a  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x017f  */
    /* JADX WARNING: Missing block: B:47:0x00d0, code skipped:
            if (r6[r15 + 3] != (byte) 1) goto L_0x00d4;
     */
    private long readAndWriteTracks(org.telegram.messenger.MessageObject r29, android.media.MediaExtractor r30, org.telegram.messenger.video.MP4Builder r31, android.media.MediaCodec.BufferInfo r32, long r33, long r35, java.io.File r37, boolean r38) throws java.lang.Exception {
        /*
        r28 = this;
        r7 = r28;
        r8 = r30;
        r9 = r31;
        r10 = r32;
        r11 = r33;
        r13 = 0;
        r14 = r7.findTrack(r8, r13);
        r15 = -1;
        r6 = 1;
        if (r38 == 0) goto L_0x0019;
    L_0x0013:
        r0 = r7.findTrack(r8, r6);
        r4 = r0;
        goto L_0x001a;
    L_0x0019:
        r4 = -1;
    L_0x001a:
        r0 = "max-input-size";
        r2 = 0;
        if (r14 < 0) goto L_0x003d;
    L_0x0020:
        r8.selectTrack(r14);
        r1 = r8.getTrackFormat(r14);
        r5 = r9.addTrack(r1, r13);
        r1 = r1.getInteger(r0);
        r16 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1));
        if (r16 <= 0) goto L_0x0037;
    L_0x0033:
        r8.seekTo(r11, r13);
        goto L_0x003a;
    L_0x0037:
        r8.seekTo(r2, r13);
    L_0x003a:
        r16 = r5;
        goto L_0x0040;
    L_0x003d:
        r1 = 0;
        r16 = -1;
    L_0x0040:
        if (r4 < 0) goto L_0x0061;
    L_0x0042:
        r8.selectTrack(r4);
        r5 = r8.getTrackFormat(r4);
        r17 = r9.addTrack(r5, r6);
        r0 = r5.getInteger(r0);
        r1 = java.lang.Math.max(r0, r1);
        r0 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x005d;
    L_0x0059:
        r8.seekTo(r11, r13);
        goto L_0x0063;
    L_0x005d:
        r8.seekTo(r2, r13);
        goto L_0x0063;
    L_0x0061:
        r17 = -1;
    L_0x0063:
        r5 = java.nio.ByteBuffer.allocateDirect(r1);
        r18 = -1;
        if (r4 >= 0) goto L_0x006f;
    L_0x006b:
        if (r14 < 0) goto L_0x006e;
    L_0x006d:
        goto L_0x006f;
    L_0x006e:
        return r18;
    L_0x006f:
        r28.checkConversionCanceled();
        r0 = r18;
        r20 = 0;
    L_0x0076:
        if (r20 != 0) goto L_0x01a7;
    L_0x0078:
        r28.checkConversionCanceled();
        r2 = r8.readSampleData(r5, r13);
        r10.size = r2;
        r2 = r30.getSampleTrackIndex();
        if (r2 != r14) goto L_0x008a;
    L_0x0087:
        r3 = r16;
        goto L_0x0090;
    L_0x008a:
        if (r2 != r4) goto L_0x008f;
    L_0x008c:
        r3 = r17;
        goto L_0x0090;
    L_0x008f:
        r3 = -1;
    L_0x0090:
        if (r3 == r15) goto L_0x0186;
    L_0x0092:
        r15 = android.os.Build.VERSION.SDK_INT;
        r6 = 21;
        if (r15 >= r6) goto L_0x00a0;
    L_0x0098:
        r5.position(r13);
        r6 = r10.size;
        r5.limit(r6);
    L_0x00a0:
        if (r2 == r4) goto L_0x0104;
    L_0x00a2:
        r6 = r5.array();
        if (r6 == 0) goto L_0x0104;
    L_0x00a8:
        r15 = r5.arrayOffset();
        r24 = r5.limit();
        r24 = r15 + r24;
        r13 = -1;
    L_0x00b3:
        r26 = 4;
        r38 = r4;
        r4 = r24 + -4;
        if (r15 > r4) goto L_0x0106;
    L_0x00bb:
        r27 = r6[r15];
        if (r27 != 0) goto L_0x00d3;
    L_0x00bf:
        r27 = r15 + 1;
        r27 = r6[r27];
        if (r27 != 0) goto L_0x00d3;
    L_0x00c5:
        r27 = r15 + 2;
        r27 = r6[r27];
        if (r27 != 0) goto L_0x00d3;
    L_0x00cb:
        r27 = r15 + 3;
        r7 = r6[r27];
        r8 = 1;
        if (r7 == r8) goto L_0x00d6;
    L_0x00d2:
        goto L_0x00d4;
    L_0x00d3:
        r8 = 1;
    L_0x00d4:
        if (r15 != r4) goto L_0x00fb;
    L_0x00d6:
        r7 = -1;
        if (r13 == r7) goto L_0x00fa;
    L_0x00d9:
        r7 = r15 - r13;
        if (r15 == r4) goto L_0x00de;
    L_0x00dd:
        goto L_0x00e0;
    L_0x00de:
        r26 = 0;
    L_0x00e0:
        r7 = r7 - r26;
        r4 = r7 >> 24;
        r4 = (byte) r4;
        r6[r13] = r4;
        r4 = r13 + 1;
        r8 = r7 >> 16;
        r8 = (byte) r8;
        r6[r4] = r8;
        r4 = r13 + 2;
        r8 = r7 >> 8;
        r8 = (byte) r8;
        r6[r4] = r8;
        r13 = r13 + 3;
        r4 = (byte) r7;
        r6[r13] = r4;
    L_0x00fa:
        r13 = r15;
    L_0x00fb:
        r15 = r15 + 1;
        r7 = r28;
        r8 = r30;
        r4 = r38;
        goto L_0x00b3;
    L_0x0104:
        r38 = r4;
    L_0x0106:
        r4 = r10.size;
        if (r4 < 0) goto L_0x0112;
    L_0x010a:
        r6 = r30.getSampleTime();
        r10.presentationTimeUs = r6;
        r7 = 0;
        goto L_0x0116;
    L_0x0112:
        r4 = 0;
        r10.size = r4;
        r7 = 1;
    L_0x0116:
        r4 = r10.size;
        if (r4 <= 0) goto L_0x0173;
    L_0x011a:
        if (r7 != 0) goto L_0x0173;
    L_0x011c:
        if (r2 != r14) goto L_0x012b;
    L_0x011e:
        r21 = 0;
        r2 = (r11 > r21 ? 1 : (r11 == r21 ? 0 : -1));
        if (r2 <= 0) goto L_0x012d;
    L_0x0124:
        r2 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1));
        if (r2 != 0) goto L_0x012d;
    L_0x0128:
        r0 = r10.presentationTimeUs;
        goto L_0x012d;
    L_0x012b:
        r21 = 0;
    L_0x012d:
        r26 = r0;
        r0 = (r35 > r21 ? 1 : (r35 == r21 ? 0 : -1));
        if (r0 < 0) goto L_0x0144;
    L_0x0133:
        r0 = r10.presentationTimeUs;
        r2 = (r0 > r35 ? 1 : (r0 == r35 ? 0 : -1));
        if (r2 >= 0) goto L_0x013a;
    L_0x0139:
        goto L_0x0144;
    L_0x013a:
        r15 = r38;
        r13 = r5;
        r7 = 1;
        r8 = 0;
        r21 = 0;
    L_0x0141:
        r23 = 1;
        goto L_0x017d;
    L_0x0144:
        r8 = 0;
        r10.offset = r8;
        r0 = r30.getSampleFlags();
        r10.flags = r0;
        r24 = r9.writeSampleData(r3, r5, r10, r8);
        r2 = 0;
        r0 = (r24 > r2 ? 1 : (r24 == r2 ? 0 : -1));
        if (r0 == 0) goto L_0x016d;
    L_0x0157:
        r4 = 0;
        r6 = 0;
        r0 = r28;
        r1 = r29;
        r21 = r2;
        r2 = r37;
        r3 = r4;
        r15 = r38;
        r13 = r5;
        r4 = r24;
        r23 = 1;
        r0.didWriteData(r1, r2, r3, r4, r6);
        goto L_0x017d;
    L_0x016d:
        r15 = r38;
        r21 = r2;
        r13 = r5;
        goto L_0x0141;
    L_0x0173:
        r15 = r38;
        r13 = r5;
        r8 = 0;
        r21 = 0;
        r23 = 1;
        r26 = r0;
    L_0x017d:
        if (r7 != 0) goto L_0x0182;
    L_0x017f:
        r30.advance();
    L_0x0182:
        r0 = r26;
        r3 = -1;
        goto L_0x0196;
    L_0x0186:
        r15 = r4;
        r13 = r5;
        r3 = -1;
        r8 = 0;
        r21 = 0;
        r23 = 1;
        if (r2 != r3) goto L_0x0192;
    L_0x0190:
        r7 = 1;
        goto L_0x0196;
    L_0x0192:
        r30.advance();
        r7 = 0;
    L_0x0196:
        if (r7 == 0) goto L_0x019a;
    L_0x0198:
        r20 = 1;
    L_0x019a:
        r7 = r28;
        r8 = r30;
        r5 = r13;
        r4 = r15;
        r2 = r21;
        r6 = 1;
        r13 = 0;
        r15 = -1;
        goto L_0x0076;
    L_0x01a7:
        r15 = r4;
        if (r14 < 0) goto L_0x01b0;
    L_0x01aa:
        r2 = r30;
        r2.unselectTrack(r14);
        goto L_0x01b2;
    L_0x01b0:
        r2 = r30;
    L_0x01b2:
        if (r15 < 0) goto L_0x01b7;
    L_0x01b4:
        r2.unselectTrack(r15);
    L_0x01b7:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.readAndWriteTracks(org.telegram.messenger.MessageObject, android.media.MediaExtractor, org.telegram.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, java.io.File, boolean):long");
    }

    private void checkConversionCanceled() {
        boolean z;
        synchronized (this.videoConvertSync) {
            z = this.cancelCurrentVideoConversion;
        }
        if (z) {
            throw new RuntimeException("canceled conversion");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:112:0x01fb A:{Catch:{ Exception -> 0x020c, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x01cc A:{Catch:{ Exception -> 0x020c, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x01cc A:{Catch:{ Exception -> 0x020c, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01fb A:{Catch:{ Exception -> 0x020c, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x0557  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0538  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0538  */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x0557  */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x0557  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0538  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0538  */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x0557  */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x089b A:{Catch:{ Exception -> 0x092b, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x086b A:{Catch:{ Exception -> 0x092b, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:479:0x07f6 A:{SYNTHETIC, Splitter:B:479:0x07f6} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x086b A:{Catch:{ Exception -> 0x092b, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x089b A:{Catch:{ Exception -> 0x092b, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:479:0x07f6 A:{SYNTHETIC, Splitter:B:479:0x07f6} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x089b A:{Catch:{ Exception -> 0x092b, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x086b A:{Catch:{ Exception -> 0x092b, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x079d A:{Catch:{ Exception -> 0x0763, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0787 A:{Catch:{ Exception -> 0x0763, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x07a9 A:{Catch:{ Exception -> 0x0763, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:479:0x07f6 A:{SYNTHETIC, Splitter:B:479:0x07f6} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x086b A:{Catch:{ Exception -> 0x092b, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x089b A:{Catch:{ Exception -> 0x092b, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x06f8 A:{Catch:{ Exception -> 0x08e3, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x06f2 A:{Catch:{ Exception -> 0x08e3, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0707  */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0707  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03c7 A:{Catch:{ Exception -> 0x0942, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x02cf A:{Catch:{ Exception -> 0x026e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x02b5 A:{SYNTHETIC, Splitter:B:165:0x02b5} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x02d8 A:{Catch:{ Exception -> 0x026e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02f9 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02f8 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0306 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0303 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0317 A:{SYNTHETIC, Splitter:B:186:0x0317} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0352  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0332 A:{SYNTHETIC, Splitter:B:197:0x0332} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0376 A:{SYNTHETIC, Splitter:B:217:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0368 A:{SYNTHETIC, Splitter:B:213:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x03b2  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0390 A:{SYNTHETIC, Splitter:B:227:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03c7 A:{Catch:{ Exception -> 0x0942, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x02b5 A:{SYNTHETIC, Splitter:B:165:0x02b5} */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x02cf A:{Catch:{ Exception -> 0x026e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x02d8 A:{Catch:{ Exception -> 0x026e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02f8 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02f9 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0303 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0306 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0317 A:{SYNTHETIC, Splitter:B:186:0x0317} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0332 A:{SYNTHETIC, Splitter:B:197:0x0332} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0352  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0368 A:{SYNTHETIC, Splitter:B:213:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0376 A:{SYNTHETIC, Splitter:B:217:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0390 A:{SYNTHETIC, Splitter:B:227:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x03b2  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03c7 A:{Catch:{ Exception -> 0x0942, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x02cf A:{Catch:{ Exception -> 0x026e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x02b5 A:{SYNTHETIC, Splitter:B:165:0x02b5} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x02d8 A:{Catch:{ Exception -> 0x026e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02f9 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02f8 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0306 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0303 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0317 A:{SYNTHETIC, Splitter:B:186:0x0317} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0352  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0332 A:{SYNTHETIC, Splitter:B:197:0x0332} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0376 A:{SYNTHETIC, Splitter:B:217:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0368 A:{SYNTHETIC, Splitter:B:213:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x03b2  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0390 A:{SYNTHETIC, Splitter:B:227:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03c7 A:{Catch:{ Exception -> 0x0942, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x02b5 A:{SYNTHETIC, Splitter:B:165:0x02b5} */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x02cf A:{Catch:{ Exception -> 0x026e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x02d8 A:{Catch:{ Exception -> 0x026e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02f8 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02f9 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0303 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0306 A:{Catch:{ Exception -> 0x098e, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0317 A:{SYNTHETIC, Splitter:B:186:0x0317} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0332 A:{SYNTHETIC, Splitter:B:197:0x0332} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0352  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0368 A:{SYNTHETIC, Splitter:B:213:0x0368} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0376 A:{SYNTHETIC, Splitter:B:217:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0390 A:{SYNTHETIC, Splitter:B:227:0x0390} */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x03b2  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03c7 A:{Catch:{ Exception -> 0x0942, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:619:0x0a91  */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0a96 A:{SYNTHETIC, Splitter:B:621:0x0a96} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0aa3  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0a49 A:{SYNTHETIC, Splitter:B:606:0x0a49} */
    /* JADX WARNING: Removed duplicated region for block: B:612:0x0a56  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0a49 A:{SYNTHETIC, Splitter:B:606:0x0a49} */
    /* JADX WARNING: Removed duplicated region for block: B:612:0x0a56  */
    /* JADX WARNING: Removed duplicated region for block: B:619:0x0a91  */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0a96 A:{SYNTHETIC, Splitter:B:621:0x0a96} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0aa3  */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0a49 A:{SYNTHETIC, Splitter:B:606:0x0a49} */
    /* JADX WARNING: Removed duplicated region for block: B:612:0x0a56  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:619:0x0a91  */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0a96 A:{SYNTHETIC, Splitter:B:621:0x0a96} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0aa3  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0a49 A:{SYNTHETIC, Splitter:B:606:0x0a49} */
    /* JADX WARNING: Removed duplicated region for block: B:612:0x0a56  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0a05 A:{Splitter:B:46:0x00fc, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0a49 A:{SYNTHETIC, Splitter:B:606:0x0a49} */
    /* JADX WARNING: Removed duplicated region for block: B:612:0x0a56  */
    /* JADX WARNING: Removed duplicated region for block: B:619:0x0a91  */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0a96 A:{SYNTHETIC, Splitter:B:621:0x0a96} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0aa3  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a01 A:{Splitter:B:121:0x0222, PHI: r13 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x09af A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x09b4 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x09b9 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09c1 A:{Catch:{ Exception -> 0x09cd, all -> 0x0a01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x09d7 A:{SYNTHETIC, Splitter:B:576:0x09d7} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x09e4  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:61:0x012d, B:121:0x0222] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:63:0x0132, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:64:0x0133, code skipped:
            r13 = r5;
            r31 = r8;
            r32 = r9;
     */
    /* JADX WARNING: Missing block: B:73:0x014f, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:74:0x0150, code skipped:
            r1 = r0;
            r7 = r13;
            r34 = r14;
     */
    /* JADX WARNING: Missing block: B:129:0x023d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:130:0x023e, code skipped:
            r34 = r14;
     */
    /* JADX WARNING: Missing block: B:145:0x026e, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:204:0x034a, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:205:0x034b, code skipped:
            r1 = r0;
            r59 = r8;
            r4 = null;
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:215:0x036e, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:216:0x036f, code skipped:
            r1 = r0;
            r59 = r8;
            r5 = r17;
     */
    /* JADX WARNING: Missing block: B:233:0x03aa, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:234:0x03ab, code skipped:
            r1 = r0;
            r59 = r8;
            r5 = r17;
     */
    /* JADX WARNING: Missing block: B:271:0x0450, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:272:0x0451, code skipped:
            r1 = r0;
            r59 = r8;
     */
    /* JADX WARNING: Missing block: B:281:0x046b, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:282:0x046c, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:293:0x0482, code skipped:
            if (r4.presentationTimeUs < r18) goto L_0x0484;
     */
    /* JADX WARNING: Missing block: B:302:0x04c7, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:303:0x04c8, code skipped:
            r1 = r0;
            r23 = r17;
     */
    /* JADX WARNING: Missing block: B:308:0x04ff, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:309:0x0501, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:310:0x0502, code skipped:
            r59 = r8;
     */
    /* JADX WARNING: Missing block: B:311:0x0504, code skipped:
            r68 = r17;
            r17 = r23;
            r64 = r37;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:312:0x050b, code skipped:
            r4 = r64;
     */
    /* JADX WARNING: Missing block: B:313:0x050f, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:314:0x0510, code skipped:
            r64 = r4;
     */
    /* JADX WARNING: Missing block: B:326:0x0551, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:327:0x0552, code skipped:
            r1 = r0;
            r4 = r5;
     */
    /* JADX WARNING: Missing block: B:331:0x055e, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:332:0x055f, code skipped:
            r5 = r4;
     */
    /* JADX WARNING: Missing block: B:333:0x0560, code skipped:
            r59 = r8;
            r68 = r17;
            r17 = r23;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:339:0x0596, code skipped:
            r4 = r5;
            r43 = r6;
            r1 = r22;
            r22 = r23;
            r37 = r28;
            r6 = r58;
            r8 = r59;
            r40 = r62;
            r35 = r67;
            r7 = r70;
            r23 = r17;
            r17 = r68;
     */
    /* JADX WARNING: Missing block: B:403:0x06ca, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:405:0x06d2, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:406:0x06d3, code skipped:
            r73 = r5;
     */
    /* JADX WARNING: Missing block: B:407:0x06d5, code skipped:
            r1 = r0;
            r23 = r17;
            r5 = r68;
            r4 = r73;
     */
    /* JADX WARNING: Missing block: B:435:0x0763, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:436:0x0764, code skipped:
            r1 = r0;
            r4 = r8;
     */
    /* JADX WARNING: Missing block: B:444:0x0772, code skipped:
            if (r2.size != 0) goto L_0x0774;
     */
    /* JADX WARNING: Missing block: B:482:0x07fb, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:485:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r3 = 1;
     */
    /* JADX WARNING: Missing block: B:496:0x081e, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:520:0x08bb, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:524:0x08e3, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:525:0x08e4, code skipped:
            r4 = r17;
            r7 = r68;
            r8 = r73;
     */
    /* JADX WARNING: Missing block: B:530:0x092b, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:531:0x092d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:532:0x092e, code skipped:
            r8 = r5;
     */
    /* JADX WARNING: Missing block: B:533:0x092f, code skipped:
            r4 = r17;
     */
    /* JADX WARNING: Missing block: B:534:0x0931, code skipped:
            r7 = r68;
     */
    /* JADX WARNING: Missing block: B:536:0x0942, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:537:0x0943, code skipped:
            r59 = r8;
            r7 = r17;
            r8 = r4;
            r4 = r23;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:538:0x094c, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:539:0x094d, code skipped:
            r59 = r8;
            r7 = r17;
            r8 = r4;
            r4 = r5;
     */
    /* JADX WARNING: Missing block: B:540:0x0953, code skipped:
            r1 = r0;
            r23 = r4;
     */
    /* JADX WARNING: Missing block: B:541:0x0956, code skipped:
            r5 = r7;
            r4 = r8;
     */
    /* JADX WARNING: Missing block: B:542:0x095a, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:543:0x095b, code skipped:
            r59 = r8;
            r35 = null;
            r8 = r4;
            r1 = r0;
            r5 = r17;
     */
    /* JADX WARNING: Missing block: B:544:0x0965, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:545:0x0966, code skipped:
            r7 = r5;
            r59 = r8;
            r35 = null;
            r8 = r4;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:546:0x096d, code skipped:
            r23 = r35;
     */
    /* JADX WARNING: Missing block: B:547:0x0970, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:548:0x0971, code skipped:
            r7 = r5;
            r59 = r8;
            r1 = r0;
            r4 = null;
            r23 = r4;
     */
    /* JADX WARNING: Missing block: B:549:0x097c, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:550:0x097d, code skipped:
            r59 = r8;
            r35 = null;
     */
    /* JADX WARNING: Missing block: B:551:0x0982, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:552:0x0983, code skipped:
            r35 = null;
            r59 = r8;
     */
    /* JADX WARNING: Missing block: B:553:0x0987, code skipped:
            r1 = r0;
            r4 = r35;
            r5 = r4;
            r23 = r5;
     */
    /* JADX WARNING: Missing block: B:554:0x098e, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:555:0x098f, code skipped:
            r59 = r8;
     */
    /* JADX WARNING: Missing block: B:572:0x09cd, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:584:0x0a01, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:586:0x0a05, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:587:0x0a06, code skipped:
            r13 = r5;
     */
    /* JADX WARNING: Missing block: B:588:0x0a07, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:604:0x0a44, code skipped:
            r7.release();
     */
    /* JADX WARNING: Missing block: B:607:?, code skipped:
            r15.finishMovie();
     */
    /* JADX WARNING: Missing block: B:608:0x0a4d, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:609:0x0a4e, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:612:0x0a56, code skipped:
            r1 = new java.lang.StringBuilder();
            r1.append("time = ");
            r1.append(java.lang.System.currentTimeMillis() - r29);
            org.telegram.messenger.FileLog.d(r1.toString());
     */
    /* JADX WARNING: Missing block: B:619:0x0a91, code skipped:
            r13.release();
     */
    /* JADX WARNING: Missing block: B:622:?, code skipped:
            r15.finishMovie();
     */
    /* JADX WARNING: Missing block: B:623:0x0a9a, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:624:0x0a9b, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:627:0x0aa3, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("time = ");
            r2.append(java.lang.System.currentTimeMillis() - r29);
            org.telegram.messenger.FileLog.d(r2.toString());
     */
    private boolean convertVideo(org.telegram.messenger.MessageObject r75) {
        /*
        r74 = this;
        r12 = r74;
        r13 = r75;
        if (r13 == 0) goto L_0x0ade;
    L_0x0006:
        r1 = r13.videoEditedInfo;
        if (r1 != 0) goto L_0x000c;
    L_0x000a:
        goto L_0x0ade;
    L_0x000c:
        r2 = r1.originalPath;
        r8 = r1.startTime;
        r10 = r1.endTime;
        r3 = r1.resultWidth;
        r4 = r1.resultHeight;
        r5 = r1.rotationValue;
        r6 = r1.originalWidth;
        r7 = r1.originalHeight;
        r15 = r1.framerate;
        r1 = r1.bitrate;
        r17 = r15;
        r14 = r75.getDialogId();
        r15 = (int) r14;
        if (r15 != 0) goto L_0x002b;
    L_0x0029:
        r15 = 1;
        goto L_0x002c;
    L_0x002b:
        r15 = 0;
    L_0x002c:
        r14 = new java.io.File;
        r18 = r10;
        r10 = r13.messageOwner;
        r10 = r10.attachPath;
        r14.<init>(r10);
        if (r2 != 0) goto L_0x003b;
    L_0x0039:
        r2 = "";
    L_0x003b:
        r10 = android.os.Build.VERSION.SDK_INT;
        r11 = 18;
        if (r10 >= r11) goto L_0x0050;
    L_0x0041:
        if (r4 <= r3) goto L_0x0050;
    L_0x0043:
        if (r3 == r6) goto L_0x0050;
    L_0x0045:
        if (r4 == r7) goto L_0x0050;
    L_0x0047:
        r5 = 90;
        r10 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r11 = r3;
        r10 = r4;
        r3 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x007b;
    L_0x0050:
        r10 = android.os.Build.VERSION.SDK_INT;
        r11 = 20;
        if (r10 <= r11) goto L_0x0078;
    L_0x0056:
        r10 = 90;
        if (r5 != r10) goto L_0x0062;
    L_0x005a:
        r5 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r11 = r3;
        r10 = r4;
        r3 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
    L_0x0060:
        r5 = 0;
        goto L_0x007b;
    L_0x0062:
        r10 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        if (r5 != r10) goto L_0x006d;
    L_0x0066:
        r5 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r10 = r3;
        r11 = r4;
        r3 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        goto L_0x0060;
    L_0x006d:
        r10 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r5 != r10) goto L_0x0078;
    L_0x0071:
        r5 = 90;
        r11 = r3;
        r10 = r4;
        r3 = 90;
        goto L_0x0060;
    L_0x0078:
        r10 = r3;
        r11 = r4;
        r3 = 0;
    L_0x007b:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r27 = r8;
        r8 = "videoconvert";
        r9 = 0;
        r8 = r4.getSharedPreferences(r8, r9);
        r4 = new java.io.File;
        r4.<init>(r2);
        r16 = r75.getId();
        r20 = r7;
        r7 = "isPreviousOk";
        if (r16 == 0) goto L_0x00cd;
    L_0x0096:
        r9 = 1;
        r21 = r8.getBoolean(r7, r9);
        r9 = r8.edit();
        r22 = r1;
        r1 = 0;
        r9 = r9.putBoolean(r7, r1);
        r9.commit();
        r1 = r4.canRead();
        if (r1 == 0) goto L_0x00b1;
    L_0x00af:
        if (r21 != 0) goto L_0x00cf;
    L_0x00b1:
        r4 = 1;
        r5 = 0;
        r9 = 1;
        r1 = r74;
        r2 = r75;
        r3 = r14;
        r10 = r7;
        r7 = r9;
        r1.didWriteData(r2, r3, r4, r5, r7);
        r1 = r8.edit();
        r4 = 1;
        r1 = r1.putBoolean(r10, r4);
        r1.commit();
    L_0x00cb:
        r1 = 0;
        return r1;
    L_0x00cd:
        r22 = r1;
    L_0x00cf:
        r9 = r7;
        r4 = 1;
        r12.videoConvertFirstWrite = r4;
        r29 = java.lang.System.currentTimeMillis();
        if (r10 == 0) goto L_0x0abe;
    L_0x00d9:
        if (r11 == 0) goto L_0x0abe;
    L_0x00db:
        r4 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r4.<init>();	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r1 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r1.<init>();	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r1.setCacheFile(r14);	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r1.setRotation(r5);	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r1.setSize(r10, r11);	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r5 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r5.<init>();	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r15 = r5.createMovie(r1, r15);	 Catch:{ Exception -> 0x0a32, all -> 0x0a2a }
        r5 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0a1d, all -> 0x0a15 }
        r5.<init>();	 Catch:{ Exception -> 0x0a1d, all -> 0x0a15 }
        r5.setDataSource(r2);	 Catch:{ Exception -> 0x0a0a, all -> 0x0a05 }
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x0a0a, all -> 0x0a05 }
        r2 = -1;
        if (r10 != r6) goto L_0x0139;
    L_0x0105:
        r1 = r20;
        if (r11 != r1) goto L_0x0139;
    L_0x0109:
        if (r3 != 0) goto L_0x0139;
    L_0x010b:
        r1 = r13.videoEditedInfo;	 Catch:{ Exception -> 0x0132, all -> 0x0a05 }
        r1 = r1.roundVideo;	 Catch:{ Exception -> 0x0132, all -> 0x0a05 }
        if (r1 == 0) goto L_0x0112;
    L_0x0111:
        goto L_0x0139;
    L_0x0112:
        r1 = r22;
        if (r1 == r2) goto L_0x0118;
    L_0x0116:
        r11 = 1;
        goto L_0x0119;
    L_0x0118:
        r11 = 0;
    L_0x0119:
        r1 = r74;
        r2 = r75;
        r3 = r5;
        r6 = r4;
        r4 = r15;
        r10 = r5;
        r5 = r6;
        r6 = r27;
        r31 = r8;
        r13 = r9;
        r8 = r18;
        r32 = r13;
        r13 = r10;
        r10 = r14;
        r1.readAndWriteTracks(r2, r3, r4, r5, r6, r8, r10, r11);	 Catch:{ Exception -> 0x014f, all -> 0x0a01 }
        goto L_0x09cf;
    L_0x0132:
        r0 = move-exception;
        r13 = r5;
        r31 = r8;
        r32 = r9;
        goto L_0x0150;
    L_0x0139:
        r6 = r4;
        r13 = r5;
        r31 = r8;
        r32 = r9;
        r1 = r22;
        r4 = 0;
        r8 = r12.findTrack(r13, r4);	 Catch:{ Exception -> 0x0a03, all -> 0x0a01 }
        if (r1 == r2) goto L_0x0156;
    L_0x0148:
        r4 = 1;
        r5 = r12.findTrack(r13, r4);	 Catch:{ Exception -> 0x014f, all -> 0x0a01 }
        r9 = r5;
        goto L_0x0157;
    L_0x014f:
        r0 = move-exception;
    L_0x0150:
        r1 = r0;
        r7 = r13;
        r34 = r14;
        goto L_0x0a3f;
    L_0x0156:
        r9 = -1;
    L_0x0157:
        if (r8 < 0) goto L_0x09cf;
    L_0x0159:
        r20 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0992, all -> 0x0a01 }
        r4 = r20.toLowerCase();	 Catch:{ Exception -> 0x0992, all -> 0x0a01 }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0992, all -> 0x0a01 }
        r7 = "video/avc";
        r33 = 4;
        r2 = 18;
        if (r5 >= r2) goto L_0x0219;
    L_0x016a:
        r2 = selectCodec(r7);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r5 = selectColorFormat(r2, r7);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        if (r5 == 0) goto L_0x0204;
    L_0x0174:
        r34 = r5;
        r5 = r2.getName();	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r12 = "OMX.qcom.";
        r12 = r5.contains(r12);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        if (r12 == 0) goto L_0x019e;
    L_0x0182:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r12 = 16;
        if (r5 != r12) goto L_0x019b;
    L_0x0188:
        r5 = "lge";
        r5 = r4.equals(r5);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        if (r5 != 0) goto L_0x0198;
    L_0x0190:
        r5 = "nokia";
        r5 = r4.equals(r5);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        if (r5 == 0) goto L_0x019b;
    L_0x0198:
        r5 = 1;
    L_0x0199:
        r12 = 1;
        goto L_0x01c8;
    L_0x019b:
        r5 = 1;
    L_0x019c:
        r12 = 0;
        goto L_0x01c8;
    L_0x019e:
        r12 = "OMX.Intel.";
        r12 = r5.contains(r12);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        if (r12 == 0) goto L_0x01a8;
    L_0x01a6:
        r5 = 2;
        goto L_0x019c;
    L_0x01a8:
        r12 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r12 = r5.equals(r12);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        if (r12 == 0) goto L_0x01b2;
    L_0x01b0:
        r5 = 3;
        goto L_0x019c;
    L_0x01b2:
        r12 = "OMX.SEC.AVC.Encoder";
        r12 = r5.equals(r12);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        if (r12 == 0) goto L_0x01bc;
    L_0x01ba:
        r5 = 4;
        goto L_0x0199;
    L_0x01bc:
        r12 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r5 = r5.equals(r12);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        if (r5 == 0) goto L_0x01c6;
    L_0x01c4:
        r5 = 5;
        goto L_0x019c;
    L_0x01c6:
        r5 = 0;
        goto L_0x019c;
    L_0x01c8:
        r35 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        if (r35 == 0) goto L_0x01fb;
    L_0x01cc:
        r35 = r5;
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r5.<init>();	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r36 = r12;
        r12 = "codec = ";
        r5.append(r12);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r2 = r2.getName();	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r5.append(r2);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r2 = " manufacturer = ";
        r5.append(r2);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r5.append(r4);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r2 = "device = ";
        r5.append(r2);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r2 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r5.append(r2);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r2 = r5.toString();	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        goto L_0x01ff;
    L_0x01fb:
        r35 = r5;
        r36 = r12;
    L_0x01ff:
        r12 = r34;
        r2 = r35;
        goto L_0x0222;
    L_0x0204:
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        r2 = "no supported color format";
        r1.<init>(r2);	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
        throw r1;	 Catch:{ Exception -> 0x020c, all -> 0x0a01 }
    L_0x020c:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
        r34 = r14;
    L_0x0212:
        r4 = 0;
        r5 = 0;
        r14 = 0;
    L_0x0215:
        r23 = 0;
        goto L_0x09a0;
    L_0x0219:
        r2 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r2 = 0;
        r12 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r36 = 0;
    L_0x0222:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0992, all -> 0x0a01 }
        if (r5 == 0) goto L_0x0244;
    L_0x0226:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x023d, all -> 0x0a01 }
        r5.<init>();	 Catch:{ Exception -> 0x023d, all -> 0x0a01 }
        r34 = r14;
        r14 = "colorFormat = ";
        r5.append(r14);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r5.append(r12);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        org.telegram.messenger.FileLog.d(r5);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        goto L_0x0246;
    L_0x023d:
        r0 = move-exception;
        r34 = r14;
    L_0x0240:
        r1 = r0;
        r59 = r8;
        goto L_0x0212;
    L_0x0244:
        r34 = r14;
    L_0x0246:
        r5 = r10 * r11;
        r14 = r5 * 3;
        r25 = 2;
        r14 = r14 / 2;
        if (r2 != 0) goto L_0x0270;
    L_0x0250:
        r2 = r11 % 16;
        if (r2 == 0) goto L_0x026a;
    L_0x0254:
        r2 = r11 % 16;
        r4 = 16;
        r2 = 16 - r2;
        r2 = r2 + r11;
        r2 = r2 - r11;
        r2 = r2 * r10;
        r4 = r2 * 5;
        r4 = r4 / 4;
        r14 = r14 + r4;
        r35 = r6;
    L_0x0265:
        r44 = r14;
        r5 = 3;
    L_0x0268:
        r14 = r2;
        goto L_0x02ac;
    L_0x026a:
        r35 = r6;
    L_0x026c:
        r5 = 3;
        goto L_0x02a9;
    L_0x026e:
        r0 = move-exception;
        goto L_0x0240;
    L_0x0270:
        r35 = r6;
        r6 = 1;
        if (r2 != r6) goto L_0x0288;
    L_0x0275:
        r2 = r4.toLowerCase();	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r4 = "lge";
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        if (r2 != 0) goto L_0x026c;
    L_0x0281:
        r2 = r5 + 2047;
        r2 = r2 & -2048;
        r2 = r2 - r5;
        r14 = r14 + r2;
        goto L_0x0265;
    L_0x0288:
        r5 = 5;
        if (r2 != r5) goto L_0x028c;
    L_0x028b:
        goto L_0x026c;
    L_0x028c:
        r5 = 3;
        if (r2 != r5) goto L_0x02a9;
    L_0x028f:
        r2 = "baidu";
        r2 = r4.equals(r2);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        if (r2 == 0) goto L_0x02a9;
    L_0x0297:
        r2 = r11 % 16;
        r4 = 16;
        r2 = 16 - r2;
        r2 = r2 + r11;
        r2 = r2 - r11;
        r2 = r2 * r10;
        r4 = r2 * 5;
        r4 = r4 / 4;
        r14 = r14 + r4;
        r44 = r14;
        goto L_0x0268;
    L_0x02a9:
        r44 = r14;
        r14 = 0;
    L_0x02ac:
        r13.selectTrack(r8);	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
        r2 = r13.getTrackFormat(r8);	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
        if (r9 < 0) goto L_0x02cf;
    L_0x02b5:
        r13.selectTrack(r9);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r4 = r13.getTrackFormat(r9);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r5 = "max-input-size";
        r5 = r4.getInteger(r5);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r5 = java.nio.ByteBuffer.allocateDirect(r5);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r6 = 1;
        r4 = r15.addTrack(r4, r6);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r6 = r4;
        r37 = r5;
        goto L_0x02d2;
    L_0x02cf:
        r6 = -5;
        r37 = 0;
    L_0x02d2:
        r4 = 0;
        r38 = (r27 > r4 ? 1 : (r27 == r4 ? 0 : -1));
        if (r38 <= 0) goto L_0x02e3;
    L_0x02d8:
        r4 = r27;
        r27 = r14;
        r14 = 0;
        r13.seekTo(r4, r14);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r40 = r4;
        goto L_0x02eb;
    L_0x02e3:
        r40 = r27;
        r27 = r14;
        r14 = 0;
        r13.seekTo(r4, r14);	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
    L_0x02eb:
        r4 = android.media.MediaFormat.createVideoFormat(r7, r10, r11);	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
        r5 = "color-format";
        r4.setInteger(r5, r12);	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
        r5 = "bitrate";
        if (r1 <= 0) goto L_0x02f9;
    L_0x02f8:
        goto L_0x02fc;
    L_0x02f9:
        r1 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
    L_0x02fc:
        r4.setInteger(r5, r1);	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
        r1 = "frame-rate";
        if (r17 == 0) goto L_0x0306;
    L_0x0303:
        r5 = r17;
        goto L_0x0308;
    L_0x0306:
        r5 = 25;
    L_0x0308:
        r4.setInteger(r1, r5);	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
        r1 = "i-frame-interval";
        r5 = 2;
        r4.setInteger(r1, r5);	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
        r14 = 18;
        if (r1 >= r14) goto L_0x0323;
    L_0x0317:
        r1 = "stride";
        r14 = r10 + 32;
        r4.setInteger(r1, r14);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
        r1 = "slice-height";
        r4.setInteger(r1, r11);	 Catch:{ Exception -> 0x026e, all -> 0x0a01 }
    L_0x0323:
        r14 = android.media.MediaCodec.createEncoderByType(r7);	 Catch:{ Exception -> 0x098e, all -> 0x0a01 }
        r1 = 0;
        r5 = 1;
        r14.configure(r4, r1, r1, r5);	 Catch:{ Exception -> 0x0982, all -> 0x0a01 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x097c, all -> 0x0a01 }
        r4 = 18;
        if (r1 < r4) goto L_0x0352;
    L_0x0332:
        r1 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x034a, all -> 0x0a01 }
        r4 = r14.createInputSurface();	 Catch:{ Exception -> 0x034a, all -> 0x0a01 }
        r1.<init>(r4);	 Catch:{ Exception -> 0x034a, all -> 0x0a01 }
        r1.makeCurrent();	 Catch:{ Exception -> 0x0340, all -> 0x0a01 }
        r5 = r1;
        goto L_0x0353;
    L_0x0340:
        r0 = move-exception;
        r5 = r1;
        r59 = r8;
        r4 = 0;
        r23 = 0;
        r1 = r0;
        goto L_0x09a0;
    L_0x034a:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
        r4 = 0;
        r5 = 0;
        goto L_0x0215;
    L_0x0352:
        r5 = 0;
    L_0x0353:
        r14.start();	 Catch:{ Exception -> 0x0970, all -> 0x0a01 }
        r1 = "mime";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0970, all -> 0x0a01 }
        r4 = android.media.MediaCodec.createDecoderByType(r1);	 Catch:{ Exception -> 0x0970, all -> 0x0a01 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0965, all -> 0x0a01 }
        r17 = r5;
        r5 = 18;
        if (r1 < r5) goto L_0x0376;
    L_0x0368:
        r1 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x036e, all -> 0x0a01 }
        r1.<init>();	 Catch:{ Exception -> 0x036e, all -> 0x0a01 }
        goto L_0x037b;
    L_0x036e:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
        r5 = r17;
        goto L_0x0215;
    L_0x0376:
        r1 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x095a, all -> 0x0a01 }
        r1.<init>(r10, r11, r3);	 Catch:{ Exception -> 0x095a, all -> 0x0a01 }
    L_0x037b:
        r5 = r1;
        r1 = r5.getSurface();	 Catch:{ Exception -> 0x094c, all -> 0x0a01 }
        r23 = r5;
        r3 = 0;
        r5 = 0;
        r4.configure(r2, r1, r3, r5);	 Catch:{ Exception -> 0x0942, all -> 0x0a01 }
        r4.start();	 Catch:{ Exception -> 0x0942, all -> 0x0a01 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0942, all -> 0x0a01 }
        r5 = 21;
        if (r1 >= r5) goto L_0x03b2;
    L_0x0390:
        r1 = r4.getInputBuffers();	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        r2 = r14.getOutputBuffers();	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        r5 = 18;
        if (r3 >= r5) goto L_0x03a7;
    L_0x039e:
        r3 = r14.getInputBuffers();	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        r52 = r1;
        r53 = r3;
        goto L_0x03b7;
    L_0x03a7:
        r52 = r1;
        goto L_0x03b5;
    L_0x03aa:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
        r5 = r17;
        goto L_0x09a0;
    L_0x03b2:
        r2 = 0;
        r52 = 0;
    L_0x03b5:
        r53 = 0;
    L_0x03b7:
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x0942, all -> 0x0a01 }
        r54 = r2;
        r1 = 0;
        r21 = 0;
        r22 = 0;
        r43 = -5;
        r55 = -1;
    L_0x03c5:
        if (r21 != 0) goto L_0x0934;
    L_0x03c7:
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x0942, all -> 0x0a01 }
        r2 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        if (r1 != 0) goto L_0x056b;
    L_0x03ce:
        r5 = r13.getSampleTrackIndex();	 Catch:{ Exception -> 0x055e, all -> 0x0a01 }
        if (r5 != r8) goto L_0x042d;
    L_0x03d4:
        r5 = r4.dequeueInputBuffer(r2);	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        if (r5 < 0) goto L_0x0411;
    L_0x03da:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        r3 = 21;
        if (r2 >= r3) goto L_0x03e3;
    L_0x03e0:
        r2 = r52[r5];	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        goto L_0x03e7;
    L_0x03e3:
        r2 = r4.getInputBuffer(r5);	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
    L_0x03e7:
        r3 = 0;
        r48 = r13.readSampleData(r2, r3);	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        if (r48 >= 0) goto L_0x03ff;
    L_0x03ee:
        r47 = 0;
        r48 = 0;
        r49 = 0;
        r51 = 4;
        r45 = r4;
        r46 = r5;
        r45.queueInputBuffer(r46, r47, r48, r49, r51);	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        r1 = 1;
        goto L_0x0411;
    L_0x03ff:
        r47 = 0;
        r49 = r13.getSampleTime();	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        r51 = 0;
        r45 = r4;
        r46 = r5;
        r45.queueInputBuffer(r46, r47, r48, r49, r51);	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
        r13.advance();	 Catch:{ Exception -> 0x03aa, all -> 0x0a01 }
    L_0x0411:
        r64 = r4;
        r58 = r6;
        r70 = r7;
        r59 = r8;
        r68 = r17;
        r17 = r23;
        r67 = r35;
        r28 = r37;
        r62 = r40;
        r7 = -1;
        r8 = 3;
        r35 = 0;
        r57 = 2;
        r65 = 0;
        goto L_0x0533;
    L_0x042d:
        r2 = -1;
        if (r9 == r2) goto L_0x0513;
    L_0x0430:
        if (r5 != r9) goto L_0x0513;
    L_0x0432:
        r3 = r37;
        r2 = 0;
        r5 = r13.readSampleData(r3, r2);	 Catch:{ Exception -> 0x050f, all -> 0x0a01 }
        r37 = r4;
        r4 = r35;
        r4.size = r5;	 Catch:{ Exception -> 0x0501, all -> 0x0a01 }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0501, all -> 0x0a01 }
        r35 = r7;
        r7 = 21;
        if (r5 >= r7) goto L_0x045a;
    L_0x0447:
        r3.position(r2);	 Catch:{ Exception -> 0x0450, all -> 0x0a01 }
        r2 = r4.size;	 Catch:{ Exception -> 0x0450, all -> 0x0a01 }
        r3.limit(r2);	 Catch:{ Exception -> 0x0450, all -> 0x0a01 }
        goto L_0x045a;
    L_0x0450:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
    L_0x0454:
        r5 = r17;
        r4 = r37;
        goto L_0x09a0;
    L_0x045a:
        r2 = r4.size;	 Catch:{ Exception -> 0x0501, all -> 0x0a01 }
        if (r2 < 0) goto L_0x046e;
    L_0x045e:
        r59 = r8;
        r7 = r13.getSampleTime();	 Catch:{ Exception -> 0x046b, all -> 0x0a01 }
        r4.presentationTimeUs = r7;	 Catch:{ Exception -> 0x046b, all -> 0x0a01 }
        r13.advance();	 Catch:{ Exception -> 0x046b, all -> 0x0a01 }
        r8 = r1;
        goto L_0x0474;
    L_0x046b:
        r0 = move-exception;
        r1 = r0;
        goto L_0x0454;
    L_0x046e:
        r59 = r8;
        r1 = 0;
        r4.size = r1;	 Catch:{ Exception -> 0x04ff, all -> 0x0a01 }
        r8 = 1;
    L_0x0474:
        r1 = r4.size;	 Catch:{ Exception -> 0x04ff, all -> 0x0a01 }
        if (r1 <= 0) goto L_0x04e3;
    L_0x0478:
        r1 = 0;
        r5 = (r18 > r1 ? 1 : (r18 == r1 ? 0 : -1));
        if (r5 < 0) goto L_0x0484;
    L_0x047e:
        r1 = r4.presentationTimeUs;	 Catch:{ Exception -> 0x046b, all -> 0x0a01 }
        r5 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1));
        if (r5 >= 0) goto L_0x04e3;
    L_0x0484:
        r1 = 0;
        r4.offset = r1;	 Catch:{ Exception -> 0x04ff, all -> 0x0a01 }
        r2 = r13.getSampleFlags();	 Catch:{ Exception -> 0x04ff, all -> 0x0a01 }
        r4.flags = r2;	 Catch:{ Exception -> 0x04ff, all -> 0x0a01 }
        r45 = r15.writeSampleData(r6, r3, r4, r1);	 Catch:{ Exception -> 0x04ff, all -> 0x0a01 }
        r38 = 0;
        r1 = (r45 > r38 ? 1 : (r45 == r38 ? 0 : -1));
        if (r1 == 0) goto L_0x04cc;
    L_0x0497:
        r5 = 0;
        r7 = 0;
        r1 = r74;
        r57 = 2;
        r2 = r75;
        r20 = r3;
        r25 = 0;
        r3 = r34;
        r28 = r4;
        r64 = r37;
        r65 = r38;
        r62 = r40;
        r4 = r5;
        r58 = r6;
        r68 = r17;
        r17 = r23;
        r67 = r28;
        r28 = r20;
        r20 = 3;
        r5 = r45;
        r23 = r8;
        r70 = r35;
        r8 = 3;
        r35 = r25;
        r1.didWriteData(r2, r3, r4, r5, r7);	 Catch:{ Exception -> 0x04c7, all -> 0x0a01 }
        goto L_0x04fc;
    L_0x04c7:
        r0 = move-exception;
        r1 = r0;
        r23 = r17;
        goto L_0x050b;
    L_0x04cc:
        r28 = r3;
        r67 = r4;
        r58 = r6;
        r68 = r17;
        r17 = r23;
        r70 = r35;
        r64 = r37;
        r65 = r38;
        r62 = r40;
        r35 = 0;
        r57 = 2;
        goto L_0x04f9;
    L_0x04e3:
        r28 = r3;
        r67 = r4;
        r58 = r6;
        r68 = r17;
        r17 = r23;
        r70 = r35;
        r64 = r37;
        r62 = r40;
        r35 = 0;
        r57 = 2;
        r65 = 0;
    L_0x04f9:
        r23 = r8;
        r8 = 3;
    L_0x04fc:
        r1 = 0;
        r7 = -1;
        goto L_0x0536;
    L_0x04ff:
        r0 = move-exception;
        goto L_0x0504;
    L_0x0501:
        r0 = move-exception;
        r59 = r8;
    L_0x0504:
        r68 = r17;
        r17 = r23;
        r64 = r37;
        r1 = r0;
    L_0x050b:
        r4 = r64;
        goto L_0x0567;
    L_0x050f:
        r0 = move-exception;
        r64 = r4;
        goto L_0x0560;
    L_0x0513:
        r64 = r4;
        r58 = r6;
        r70 = r7;
        r59 = r8;
        r68 = r17;
        r17 = r23;
        r67 = r35;
        r28 = r37;
        r62 = r40;
        r8 = 3;
        r35 = 0;
        r57 = 2;
        r65 = 0;
        r7 = -1;
        if (r5 != r7) goto L_0x0533;
    L_0x052f:
        r23 = r1;
        r1 = 1;
        goto L_0x0536;
    L_0x0533:
        r23 = r1;
        r1 = 0;
    L_0x0536:
        if (r1 == 0) goto L_0x0557;
    L_0x0538:
        r5 = r64;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r46 = r5.dequeueInputBuffer(r3);	 Catch:{ Exception -> 0x0551, all -> 0x0a01 }
        if (r46 < 0) goto L_0x055b;
    L_0x0542:
        r47 = 0;
        r48 = 0;
        r49 = 0;
        r51 = 4;
        r45 = r5;
        r45.queueInputBuffer(r46, r47, r48, r49, r51);	 Catch:{ Exception -> 0x0551, all -> 0x0a01 }
        r1 = 1;
        goto L_0x0585;
    L_0x0551:
        r0 = move-exception;
        r1 = r0;
        r4 = r5;
    L_0x0554:
        r23 = r17;
        goto L_0x0567;
    L_0x0557:
        r5 = r64;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x055b:
        r1 = r23;
        goto L_0x0585;
    L_0x055e:
        r0 = move-exception;
        r5 = r4;
    L_0x0560:
        r59 = r8;
        r68 = r17;
        r17 = r23;
        r1 = r0;
    L_0x0567:
        r5 = r68;
        goto L_0x09a0;
    L_0x056b:
        r5 = r4;
        r58 = r6;
        r70 = r7;
        r59 = r8;
        r68 = r17;
        r17 = r23;
        r67 = r35;
        r28 = r37;
        r62 = r40;
        r7 = -1;
        r8 = 3;
        r35 = 0;
        r57 = 2;
        r65 = 0;
        r3 = r2;
    L_0x0585:
        r2 = r22 ^ 1;
        r45 = r2;
        r23 = r22;
        r6 = r43;
        r20 = 1;
        r22 = r1;
    L_0x0591:
        if (r45 != 0) goto L_0x05af;
    L_0x0593:
        if (r20 == 0) goto L_0x0596;
    L_0x0595:
        goto L_0x05af;
    L_0x0596:
        r4 = r5;
        r43 = r6;
        r1 = r22;
        r22 = r23;
        r37 = r28;
        r6 = r58;
        r8 = r59;
        r40 = r62;
        r35 = r67;
        r7 = r70;
        r23 = r17;
        r17 = r68;
        goto L_0x03c5;
    L_0x05af:
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x092d, all -> 0x0a01 }
        r2 = r67;
        r1 = r14.dequeueOutputBuffer(r2, r3);	 Catch:{ Exception -> 0x092d, all -> 0x0a01 }
        if (r1 != r7) goto L_0x05cb;
    L_0x05ba:
        r3 = r1;
        r73 = r5;
        r5 = r6;
        r61 = r9;
        r4 = r21;
        r1 = r70;
        r6 = 3;
        r7 = 0;
    L_0x05c6:
        r9 = -1;
        r60 = 21;
        goto L_0x0705;
    L_0x05cb:
        r3 = -3;
        if (r1 != r3) goto L_0x05e6;
    L_0x05ce:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0551, all -> 0x0a01 }
        r4 = 21;
        if (r3 >= r4) goto L_0x05d8;
    L_0x05d4:
        r54 = r14.getOutputBuffers();	 Catch:{ Exception -> 0x0551, all -> 0x0a01 }
    L_0x05d8:
        r3 = r1;
        r73 = r5;
        r5 = r6;
        r61 = r9;
        r7 = r20;
        r4 = r21;
        r1 = r70;
        r6 = 3;
        goto L_0x05c6;
    L_0x05e6:
        r4 = 21;
        r3 = -2;
        if (r1 != r3) goto L_0x05f8;
    L_0x05eb:
        r3 = r14.getOutputFormat();	 Catch:{ Exception -> 0x0551, all -> 0x0a01 }
        r8 = -5;
        if (r6 != r8) goto L_0x05d8;
    L_0x05f2:
        r7 = 0;
        r6 = r15.addTrack(r3, r7);	 Catch:{ Exception -> 0x0551, all -> 0x0a01 }
        goto L_0x05d8;
    L_0x05f8:
        r8 = -5;
        if (r1 < 0) goto L_0x090e;
    L_0x05fb:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x092d, all -> 0x0a01 }
        if (r3 >= r4) goto L_0x0602;
    L_0x05ff:
        r3 = r54[r1];	 Catch:{ Exception -> 0x0551, all -> 0x0a01 }
        goto L_0x0606;
    L_0x0602:
        r3 = r14.getOutputBuffer(r1);	 Catch:{ Exception -> 0x092d, all -> 0x0a01 }
    L_0x0606:
        if (r3 == 0) goto L_0x08ec;
    L_0x0608:
        r7 = r2.size;	 Catch:{ Exception -> 0x092d, all -> 0x0a01 }
        r4 = 1;
        if (r7 <= r4) goto L_0x06de;
    L_0x060d:
        r7 = r2.flags;	 Catch:{ Exception -> 0x06d2, all -> 0x0a01 }
        r7 = r7 & 2;
        if (r7 != 0) goto L_0x0645;
    L_0x0613:
        r25 = r15.writeSampleData(r6, r3, r2, r4);	 Catch:{ Exception -> 0x06d2, all -> 0x0a01 }
        r3 = (r25 > r65 ? 1 : (r25 == r65 ? 0 : -1));
        if (r3 == 0) goto L_0x0637;
    L_0x061b:
        r4 = 0;
        r7 = 0;
        r3 = r1;
        r1 = r74;
        r71 = r2;
        r2 = r75;
        r72 = r3;
        r3 = r34;
        r60 = 21;
        r73 = r5;
        r8 = r6;
        r5 = r25;
        r61 = r9;
        r9 = -1;
        r1.didWriteData(r2, r3, r4, r5, r7);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        goto L_0x06cc;
    L_0x0637:
        r72 = r1;
        r71 = r2;
        r73 = r5;
        r8 = r6;
        r61 = r9;
        r9 = -1;
        r60 = 21;
        goto L_0x06cc;
    L_0x0645:
        r72 = r1;
        r71 = r2;
        r73 = r5;
        r8 = r6;
        r61 = r9;
        r1 = -5;
        r9 = -1;
        r60 = 21;
        if (r8 != r1) goto L_0x06cc;
    L_0x0654:
        r2 = r71;
        r4 = r2.size;	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r4 = new byte[r4];	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r5 = r2.offset;	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r6 = r2.size;	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r5 = r5 + r6;
        r3.limit(r5);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r5 = r2.offset;	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r3.position(r5);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r3.get(r4);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r3 = r2.size;	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r5 = 1;
        r3 = r3 - r5;
    L_0x066e:
        if (r3 < 0) goto L_0x06ac;
    L_0x0670:
        r6 = 3;
        if (r3 <= r6) goto L_0x06ad;
    L_0x0673:
        r7 = r4[r3];	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        if (r7 != r5) goto L_0x06a7;
    L_0x0677:
        r5 = r3 + -1;
        r5 = r4[r5];	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        if (r5 != 0) goto L_0x06a7;
    L_0x067d:
        r5 = r3 + -2;
        r5 = r4[r5];	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        if (r5 != 0) goto L_0x06a7;
    L_0x0683:
        r5 = r3 + -3;
        r7 = r4[r5];	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        if (r7 != 0) goto L_0x06a7;
    L_0x0689:
        r7 = java.nio.ByteBuffer.allocate(r5);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r3 = r2.size;	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r3 = r3 - r5;
        r3 = java.nio.ByteBuffer.allocate(r3);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r8 = 0;
        r1 = r7.put(r4, r8, r5);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r1.position(r8);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r1 = r2.size;	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r1 = r1 - r5;
        r1 = r3.put(r4, r5, r1);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r1.position(r8);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        goto L_0x06b0;
    L_0x06a7:
        r3 = r3 + -1;
        r1 = -5;
        r5 = 1;
        goto L_0x066e;
    L_0x06ac:
        r6 = 3;
    L_0x06ad:
        r3 = r35;
        r7 = r3;
    L_0x06b0:
        r1 = r70;
        r4 = android.media.MediaFormat.createVideoFormat(r1, r10, r11);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        if (r7 == 0) goto L_0x06c4;
    L_0x06b8:
        if (r3 == 0) goto L_0x06c4;
    L_0x06ba:
        r5 = "csd-0";
        r4.setByteBuffer(r5, r7);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        r5 = "csd-1";
        r4.setByteBuffer(r5, r3);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
    L_0x06c4:
        r3 = 0;
        r4 = r15.addTrack(r4, r3);	 Catch:{ Exception -> 0x06ca, all -> 0x0a01 }
        goto L_0x06ec;
    L_0x06ca:
        r0 = move-exception;
        goto L_0x06d5;
    L_0x06cc:
        r1 = r70;
        r2 = r71;
        r6 = 3;
        goto L_0x06eb;
    L_0x06d2:
        r0 = move-exception;
        r73 = r5;
    L_0x06d5:
        r1 = r0;
        r23 = r17;
        r5 = r68;
        r4 = r73;
        goto L_0x09a0;
    L_0x06de:
        r72 = r1;
        r73 = r5;
        r8 = r6;
        r61 = r9;
        r1 = r70;
        r6 = 3;
        r9 = -1;
        r60 = 21;
    L_0x06eb:
        r4 = r8;
    L_0x06ec:
        r3 = r2.flags;	 Catch:{ Exception -> 0x08e3, all -> 0x0a01 }
        r3 = r3 & 4;
        if (r3 == 0) goto L_0x06f8;
    L_0x06f2:
        r3 = r72;
        r5 = 0;
        r21 = 1;
        goto L_0x06fd;
    L_0x06f8:
        r3 = r72;
        r5 = 0;
        r21 = 0;
    L_0x06fd:
        r14.releaseOutputBuffer(r3, r5);	 Catch:{ Exception -> 0x08e3, all -> 0x0a01 }
        r5 = r4;
        r7 = r20;
        r4 = r21;
    L_0x0705:
        if (r3 == r9) goto L_0x071a;
    L_0x0707:
        r70 = r1;
        r67 = r2;
        r21 = r4;
        r6 = r5;
        r20 = r7;
        r9 = r61;
        r5 = r73;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r7 = -1;
        r8 = 3;
        goto L_0x0591;
    L_0x071a:
        if (r23 != 0) goto L_0x08be;
    L_0x071c:
        r64 = r7;
        r8 = r73;
        r6 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r3 = r8.dequeueOutputBuffer(r2, r6);	 Catch:{ Exception -> 0x08bb, all -> 0x0a01 }
        if (r3 != r9) goto L_0x0735;
    L_0x0728:
        r69 = r4;
        r9 = r5;
        r4 = r17;
        r70 = r62;
        r7 = r68;
    L_0x0731:
        r45 = 0;
        goto L_0x08cb;
    L_0x0735:
        r9 = -3;
        if (r3 != r9) goto L_0x0743;
    L_0x0738:
        r69 = r4;
        r9 = r5;
        r4 = r17;
        r70 = r62;
        r7 = r68;
        goto L_0x08cb;
    L_0x0743:
        r9 = -2;
        if (r3 != r9) goto L_0x0768;
    L_0x0746:
        r3 = r8.getOutputFormat();	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        if (r9 == 0) goto L_0x0738;
    L_0x074e:
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r9.<init>();	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r6 = "newFormat = ";
        r9.append(r6);	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r9.append(r3);	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r3 = r9.toString();	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        goto L_0x0738;
    L_0x0763:
        r0 = move-exception;
        r1 = r0;
        r4 = r8;
        goto L_0x0554;
    L_0x0768:
        if (r3 < 0) goto L_0x08a0;
    L_0x076a:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x08bb, all -> 0x0a01 }
        r7 = 18;
        if (r6 < r7) goto L_0x0778;
    L_0x0770:
        r6 = r2.size;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        if (r6 == 0) goto L_0x0776;
    L_0x0774:
        r6 = 1;
        goto L_0x0783;
    L_0x0776:
        r6 = 0;
        goto L_0x0783;
    L_0x0778:
        r6 = r2.size;	 Catch:{ Exception -> 0x08bb, all -> 0x0a01 }
        if (r6 != 0) goto L_0x0774;
    L_0x077c:
        r6 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r9 = (r6 > r65 ? 1 : (r6 == r65 ? 0 : -1));
        if (r9 == 0) goto L_0x0776;
    L_0x0782:
        goto L_0x0774;
    L_0x0783:
        r7 = (r18 > r65 ? 1 : (r18 == r65 ? 0 : -1));
        if (r7 <= 0) goto L_0x079d;
    L_0x0787:
        r7 = r4;
        r9 = r5;
        r4 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r20 = (r4 > r18 ? 1 : (r4 == r18 ? 0 : -1));
        if (r20 < 0) goto L_0x079f;
    L_0x078f:
        r4 = r2.flags;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r4 = r4 | 4;
        r2.flags = r4;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r4 = r62;
        r6 = 0;
        r62 = 1;
        r63 = 1;
        goto L_0x07a5;
    L_0x079d:
        r7 = r4;
        r9 = r5;
    L_0x079f:
        r4 = r62;
        r62 = r22;
        r63 = r23;
    L_0x07a5:
        r20 = (r4 > r65 ? 1 : (r4 == r65 ? 0 : -1));
        if (r20 <= 0) goto L_0x07e9;
    L_0x07a9:
        r20 = -1;
        r22 = (r55 > r20 ? 1 : (r55 == r20 ? 0 : -1));
        if (r22 != 0) goto L_0x07e9;
    L_0x07af:
        r20 = r6;
        r69 = r7;
        r6 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r21 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r21 >= 0) goto L_0x07e2;
    L_0x07b9:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        if (r6 == 0) goto L_0x07de;
    L_0x07bd:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r6.<init>();	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r7 = "drop frame startTime = ";
        r6.append(r7);	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r6.append(r4);	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r7 = " present time = ";
        r6.append(r7);	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r70 = r4;
        r4 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r6.append(r4);	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r4 = r6.toString();	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        goto L_0x07e0;
    L_0x07de:
        r70 = r4;
    L_0x07e0:
        r4 = 0;
        goto L_0x07f1;
    L_0x07e2:
        r70 = r4;
        r4 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0763, all -> 0x0a01 }
        r55 = r4;
        goto L_0x07ef;
    L_0x07e9:
        r70 = r4;
        r20 = r6;
        r69 = r7;
    L_0x07ef:
        r4 = r20;
    L_0x07f1:
        r8.releaseOutputBuffer(r3, r4);	 Catch:{ Exception -> 0x08bb, all -> 0x0a01 }
        if (r4 == 0) goto L_0x0861;
    L_0x07f6:
        r17.awaitNewImage();	 Catch:{ Exception -> 0x07fb, all -> 0x0a01 }
        r3 = 0;
        goto L_0x0801;
    L_0x07fb:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);	 Catch:{ Exception -> 0x08bb, all -> 0x0a01 }
        r3 = 1;
    L_0x0801:
        if (r3 != 0) goto L_0x0861;
    L_0x0803:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x08bb, all -> 0x0a01 }
        r4 = 18;
        if (r3 < r4) goto L_0x0821;
    L_0x0809:
        r4 = r17;
        r3 = 0;
        r4.drawImage(r3);	 Catch:{ Exception -> 0x081e, all -> 0x0a01 }
        r5 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x081e, all -> 0x0a01 }
        r20 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 * r20;
        r7 = r68;
        r7.setPresentationTime(r5);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r7.swapBuffers();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        goto L_0x0865;
    L_0x081e:
        r0 = move-exception;
        goto L_0x0931;
    L_0x0821:
        r4 = r17;
        r7 = r68;
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r38 = r14.dequeueInputBuffer(r5);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        if (r38 < 0) goto L_0x0857;
    L_0x082d:
        r3 = 1;
        r4.drawImage(r3);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r20 = r4.getFrame();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r21 = r53[r38];	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r21.clear();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r22 = r12;
        r23 = r10;
        r24 = r11;
        r25 = r27;
        r26 = r36;
        org.telegram.messenger.Utilities.convertVideoFrame(r20, r21, r22, r23, r24, r25, r26);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r39 = 0;
        r5 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r43 = 0;
        r37 = r14;
        r40 = r44;
        r41 = r5;
        r37.queueInputBuffer(r38, r39, r40, r41, r43);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        goto L_0x0865;
    L_0x0857:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        if (r3 == 0) goto L_0x0865;
    L_0x085b:
        r3 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        goto L_0x0865;
    L_0x0861:
        r4 = r17;
        r7 = r68;
    L_0x0865:
        r3 = r2.flags;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r3 = r3 & 4;
        if (r3 == 0) goto L_0x089b;
    L_0x086b:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        if (r3 == 0) goto L_0x0874;
    L_0x086f:
        r3 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
    L_0x0874:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r5 = 18;
        if (r3 < r5) goto L_0x087e;
    L_0x087a:
        r14.signalEndOfInputStream();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        goto L_0x0895;
    L_0x087e:
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r46 = r14.dequeueInputBuffer(r5);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        if (r46 < 0) goto L_0x0895;
    L_0x0886:
        r47 = 0;
        r48 = 1;
        r5 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r51 = 4;
        r45 = r14;
        r49 = r5;
        r45.queueInputBuffer(r46, r47, r48, r49, r51);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
    L_0x0895:
        r22 = r62;
        r23 = r63;
        goto L_0x0731;
    L_0x089b:
        r22 = r62;
        r23 = r63;
        goto L_0x08cb;
    L_0x08a0:
        r4 = r17;
        r7 = r68;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2.<init>();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r5 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r2.append(r5);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2.append(r3);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        throw r1;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
    L_0x08bb:
        r0 = move-exception;
        goto L_0x092f;
    L_0x08be:
        r69 = r4;
        r9 = r5;
        r64 = r7;
        r4 = r17;
        r70 = r62;
        r7 = r68;
        r8 = r73;
    L_0x08cb:
        r67 = r2;
        r17 = r4;
        r68 = r7;
        r5 = r8;
        r6 = r9;
        r9 = r61;
        r20 = r64;
        r21 = r69;
        r62 = r70;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r7 = -1;
        r8 = 3;
        r70 = r1;
        goto L_0x0591;
    L_0x08e3:
        r0 = move-exception;
        r4 = r17;
        r7 = r68;
        r8 = r73;
        goto L_0x0953;
    L_0x08ec:
        r3 = r1;
        r8 = r5;
        r4 = r17;
        r7 = r68;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2.<init>();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r5 = "encoderOutputBuffer ";
        r2.append(r5);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2.append(r3);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r3 = " was null";
        r2.append(r3);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        throw r1;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
    L_0x090e:
        r3 = r1;
        r8 = r5;
        r4 = r17;
        r7 = r68;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2.<init>();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r5 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r2.append(r5);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2.append(r3);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
        throw r1;	 Catch:{ Exception -> 0x092b, all -> 0x0a01 }
    L_0x092b:
        r0 = move-exception;
        goto L_0x0953;
    L_0x092d:
        r0 = move-exception;
        r8 = r5;
    L_0x092f:
        r4 = r17;
    L_0x0931:
        r7 = r68;
        goto L_0x0953;
    L_0x0934:
        r59 = r8;
        r7 = r17;
        r8 = r4;
        r4 = r23;
        r5 = r7;
        r1 = r59;
        r16 = 0;
        goto L_0x09aa;
    L_0x0942:
        r0 = move-exception;
        r59 = r8;
        r7 = r17;
        r8 = r4;
        r4 = r23;
        r1 = r0;
        goto L_0x0956;
    L_0x094c:
        r0 = move-exception;
        r59 = r8;
        r7 = r17;
        r8 = r4;
        r4 = r5;
    L_0x0953:
        r1 = r0;
        r23 = r4;
    L_0x0956:
        r5 = r7;
        r4 = r8;
        goto L_0x09a0;
    L_0x095a:
        r0 = move-exception;
        r59 = r8;
        r7 = r17;
        r35 = 0;
        r8 = r4;
        r1 = r0;
        r5 = r7;
        goto L_0x096d;
    L_0x0965:
        r0 = move-exception;
        r7 = r5;
        r59 = r8;
        r35 = 0;
        r8 = r4;
        r1 = r0;
    L_0x096d:
        r23 = r35;
        goto L_0x09a0;
    L_0x0970:
        r0 = move-exception;
        r7 = r5;
        r59 = r8;
        r35 = 0;
        r1 = r0;
        r4 = r35;
        r23 = r4;
        goto L_0x09a0;
    L_0x097c:
        r0 = move-exception;
        r59 = r8;
        r35 = 0;
        goto L_0x0987;
    L_0x0982:
        r0 = move-exception;
        r35 = r1;
        r59 = r8;
    L_0x0987:
        r1 = r0;
        r4 = r35;
        r5 = r4;
        r23 = r5;
        goto L_0x09a0;
    L_0x098e:
        r0 = move-exception;
        r59 = r8;
        goto L_0x0997;
    L_0x0992:
        r0 = move-exception;
        r59 = r8;
        r34 = r14;
    L_0x0997:
        r35 = 0;
        r1 = r0;
        r4 = r35;
        r5 = r4;
        r14 = r5;
        r23 = r14;
    L_0x09a0:
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x09cd, all -> 0x0a01 }
        r8 = r4;
        r4 = r23;
        r1 = r59;
        r16 = 1;
    L_0x09aa:
        r13.unselectTrack(r1);	 Catch:{ Exception -> 0x09cd, all -> 0x0a01 }
        if (r4 == 0) goto L_0x09b2;
    L_0x09af:
        r4.release();	 Catch:{ Exception -> 0x09cd, all -> 0x0a01 }
    L_0x09b2:
        if (r5 == 0) goto L_0x09b7;
    L_0x09b4:
        r5.release();	 Catch:{ Exception -> 0x09cd, all -> 0x0a01 }
    L_0x09b7:
        if (r8 == 0) goto L_0x09bf;
    L_0x09b9:
        r8.stop();	 Catch:{ Exception -> 0x09cd, all -> 0x0a01 }
        r8.release();	 Catch:{ Exception -> 0x09cd, all -> 0x0a01 }
    L_0x09bf:
        if (r14 == 0) goto L_0x09c7;
    L_0x09c1:
        r14.stop();	 Catch:{ Exception -> 0x09cd, all -> 0x0a01 }
        r14.release();	 Catch:{ Exception -> 0x09cd, all -> 0x0a01 }
    L_0x09c7:
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x09cd, all -> 0x0a01 }
        r14 = r16;
        goto L_0x09d2;
    L_0x09cd:
        r0 = move-exception;
        goto L_0x0a12;
    L_0x09cf:
        r34 = r14;
        r14 = 0;
    L_0x09d2:
        r13.release();
        if (r15 == 0) goto L_0x09e0;
    L_0x09d7:
        r15.finishMovie();	 Catch:{ Exception -> 0x09db }
        goto L_0x09e0;
    L_0x09db:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x09e0:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x09fe;
    L_0x09e4:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "time = ";
        r1.append(r2);
        r2 = java.lang.System.currentTimeMillis();
        r2 = r2 - r29;
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x09fe:
        r7 = r14;
        goto L_0x0a71;
    L_0x0a01:
        r0 = move-exception;
        goto L_0x0a07;
    L_0x0a03:
        r0 = move-exception;
        goto L_0x0a10;
    L_0x0a05:
        r0 = move-exception;
        r13 = r5;
    L_0x0a07:
        r1 = r0;
        goto L_0x0a8f;
    L_0x0a0a:
        r0 = move-exception;
        r13 = r5;
        r31 = r8;
        r32 = r9;
    L_0x0a10:
        r34 = r14;
    L_0x0a12:
        r1 = r0;
        r7 = r13;
        goto L_0x0a3f;
    L_0x0a15:
        r0 = move-exception;
        r35 = 0;
        r1 = r0;
        r13 = r35;
        goto L_0x0a8f;
    L_0x0a1d:
        r0 = move-exception;
        r31 = r8;
        r32 = r9;
        r34 = r14;
        r35 = 0;
        r1 = r0;
        r7 = r35;
        goto L_0x0a3f;
    L_0x0a2a:
        r0 = move-exception;
        r35 = 0;
        r1 = r0;
        r13 = r35;
        r15 = r13;
        goto L_0x0a8f;
    L_0x0a32:
        r0 = move-exception;
        r31 = r8;
        r32 = r9;
        r34 = r14;
        r35 = 0;
        r1 = r0;
        r7 = r35;
        r15 = r7;
    L_0x0a3f:
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0a8c }
        if (r7 == 0) goto L_0x0a47;
    L_0x0a44:
        r7.release();
    L_0x0a47:
        if (r15 == 0) goto L_0x0a52;
    L_0x0a49:
        r15.finishMovie();	 Catch:{ Exception -> 0x0a4d }
        goto L_0x0a52;
    L_0x0a4d:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0a52:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0a70;
    L_0x0a56:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "time = ";
        r1.append(r2);
        r2 = java.lang.System.currentTimeMillis();
        r2 = r2 - r29;
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0a70:
        r7 = 1;
    L_0x0a71:
        r1 = r31.edit();
        r2 = r32;
        r8 = 1;
        r1 = r1.putBoolean(r2, r8);
        r1.commit();
        r4 = 1;
        r5 = 0;
        r1 = r74;
        r2 = r75;
        r3 = r34;
        r1.didWriteData(r2, r3, r4, r5, r7);
        return r8;
    L_0x0a8c:
        r0 = move-exception;
        r1 = r0;
        r13 = r7;
    L_0x0a8f:
        if (r13 == 0) goto L_0x0a94;
    L_0x0a91:
        r13.release();
    L_0x0a94:
        if (r15 == 0) goto L_0x0a9f;
    L_0x0a96:
        r15.finishMovie();	 Catch:{ Exception -> 0x0a9a }
        goto L_0x0a9f;
    L_0x0a9a:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0a9f:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0abd;
    L_0x0aa3:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "time = ";
        r2.append(r3);
        r3 = java.lang.System.currentTimeMillis();
        r3 = r3 - r29;
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x0abd:
        throw r1;
    L_0x0abe:
        r31 = r8;
        r2 = r9;
        r34 = r14;
        r1 = r31.edit();
        r3 = 1;
        r1 = r1.putBoolean(r2, r3);
        r1.commit();
        r4 = 1;
        r5 = 0;
        r7 = 1;
        r1 = r74;
        r2 = r75;
        r3 = r34;
        r1.didWriteData(r2, r3, r4, r5, r7);
        goto L_0x00cb;
    L_0x0ade:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MessageObject):boolean");
    }
}
