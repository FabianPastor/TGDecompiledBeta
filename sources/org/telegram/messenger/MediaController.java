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
import org.telegram.tgnet.TLRPC.BotInlineResult;
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
    public static ArrayList<AlbumEntry> allMediaAlbums = new ArrayList();
    public static ArrayList<AlbumEntry> allPhotoAlbums = new ArrayList();
    public static AlbumEntry allPhotosAlbumEntry;
    public static AlbumEntry allVideosAlbumEntry;
    private static Runnable broadcastPhotosRunnable;
    private static final String[] projectionPhotos;
    private static final String[] projectionVideo;
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
            if (r0 == 0) goto L_0x0157;
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
            goto L_0x0157;
        L_0x0130:
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.recordBuffers;
            r0.add(r3);
            r0 = org.telegram.messenger.MediaController.this;
            r0 = r0.sendAfterDone;
            r2 = 3;
            if (r0 == r2) goto L_0x0157;
        L_0x0142:
            r0 = org.telegram.messenger.MediaController.this;
            r2 = r0.sendAfterDone;
            r3 = org.telegram.messenger.MediaController.this;
            r3 = r3.sendAfterDoneNotify;
            r4 = org.telegram.messenger.MediaController.this;
            r4 = r4.sendAfterDoneScheduleDate;
            r0.stopRecordingInternal(r2, r3, r4);
        L_0x0157:
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
            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(MediaController.this.recordingGuid), Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(d));
        }
    };
    private short[] recordSamples = new short[1024];
    private Runnable recordStartRunnable;
    private long recordStartTime;
    private long recordTimeCount;
    private TL_document recordingAudio;
    private File recordingAudioFile;
    private int recordingCurrentAccount;
    private int recordingGuid = -1;
    private boolean resumeAudioOnFocusGain;
    private long samplesCount;
    private float seekToProgressPending;
    private int sendAfterDone;
    private boolean sendAfterDoneNotify;
    private int sendAfterDoneScheduleDate;
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
        public boolean videoOnly;

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
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = -$$Lambda$MediaController$GalleryObserverExternal$dwi1SqFQz-7StR-lsnN0S1Sqd6M.INSTANCE, 2000);
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
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new -$$Lambda$MediaController$GalleryObserverInternal$1zsHrCjjtoIwj0Amhv_aO54Om6g(this), 2000);
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
        public BotInlineResult inlineResult;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isPainted;
        public HashMap<String, String> params;
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
        String str = "_id";
        r1[0] = str;
        String str2 = "bucket_id";
        r1[1] = str2;
        String str3 = "bucket_display_name";
        r1[2] = str3;
        String str4 = "_data";
        r1[3] = str4;
        String str5 = "date_modified";
        String str6 = "datetaken";
        r1[4] = VERSION.SDK_INT > 28 ? str5 : str6;
        r1[5] = "orientation";
        projectionPhotos = r1;
        String[] strArr = new String[6];
        strArr[0] = str;
        strArr[1] = str2;
        strArr[2] = str3;
        strArr[3] = str4;
        if (VERSION.SDK_INT <= 28) {
            str5 = str6;
        }
        strArr[4] = str5;
        strArr[5] = "duration";
        projectionVideo = strArr;
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
    /* JADX WARNING: Removed duplicated region for block: B:24:0x004d A:{Catch:{ all -> 0x0073, all -> 0x0089 }} */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x004d A:{Catch:{ all -> 0x0073, all -> 0x0089 }} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x008f A:{REMOVE} */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x004d A:{Catch:{ all -> 0x0073, all -> 0x0089 }} */
    /* JADX WARNING: Missing block: B:30:0x006d, code skipped:
            if (r5 == null) goto L_0x007a;
     */
    /* JADX WARNING: Missing block: B:31:0x006f, code skipped:
            r5.close();
     */
    /* JADX WARNING: Missing block: B:35:0x0077, code skipped:
            if (r5 == null) goto L_0x007a;
     */
    /* JADX WARNING: Missing block: B:36:0x007a, code skipped:
            if (r13 == r6) goto L_?;
     */
    /* JADX WARNING: Missing block: B:37:0x007c, code skipped:
            r13 = refreshGalleryRunnable;
     */
    /* JADX WARNING: Missing block: B:38:0x007e, code skipped:
            if (r13 == null) goto L_0x0085;
     */
    /* JADX WARNING: Missing block: B:39:0x0080, code skipped:
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r13);
            refreshGalleryRunnable = null;
     */
    /* JADX WARNING: Missing block: B:40:0x0085, code skipped:
            loadGalleryPhotosAlbums(0);
     */
    /* JADX WARNING: Missing block: B:47:?, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:48:?, code skipped:
            return;
     */
    static /* synthetic */ void lambda$checkGallery$0(int r13) {
        /*
        r0 = "COUNT(_id)";
        r1 = "android.permission.READ_EXTERNAL_STORAGE";
        r2 = 1;
        r3 = 0;
        r4 = 0;
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x003a }
        r5 = r5.checkSelfPermission(r1);	 Catch:{ all -> 0x003a }
        if (r5 != 0) goto L_0x0032;
    L_0x000f:
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x003a }
        r6 = r5.getContentResolver();	 Catch:{ all -> 0x003a }
        r7 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ all -> 0x003a }
        r8 = new java.lang.String[r2];	 Catch:{ all -> 0x003a }
        r8[r4] = r0;	 Catch:{ all -> 0x003a }
        r9 = 0;
        r10 = 0;
        r11 = 0;
        r5 = android.provider.MediaStore.Images.Media.query(r6, r7, r8, r9, r10, r11);	 Catch:{ all -> 0x003a }
        if (r5 == 0) goto L_0x0033;
    L_0x0024:
        r6 = r5.moveToNext();	 Catch:{ all -> 0x0030 }
        if (r6 == 0) goto L_0x0033;
    L_0x002a:
        r6 = r5.getInt(r4);	 Catch:{ all -> 0x0030 }
        r6 = r6 + r4;
        goto L_0x0034;
    L_0x0030:
        r6 = move-exception;
        goto L_0x003c;
    L_0x0032:
        r5 = r3;
    L_0x0033:
        r6 = 0;
    L_0x0034:
        if (r5 == 0) goto L_0x0045;
    L_0x0036:
        r5.close();
        goto L_0x0045;
    L_0x003a:
        r6 = move-exception;
        r5 = r3;
    L_0x003c:
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ all -> 0x0090 }
        if (r5 == 0) goto L_0x0044;
    L_0x0041:
        r5.close();
    L_0x0044:
        r6 = 0;
    L_0x0045:
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0073 }
        r1 = r7.checkSelfPermission(r1);	 Catch:{ all -> 0x0073 }
        if (r1 != 0) goto L_0x006d;
    L_0x004d:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0073 }
        r7 = r1.getContentResolver();	 Catch:{ all -> 0x0073 }
        r8 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ all -> 0x0073 }
        r9 = new java.lang.String[r2];	 Catch:{ all -> 0x0073 }
        r9[r4] = r0;	 Catch:{ all -> 0x0073 }
        r10 = 0;
        r11 = 0;
        r12 = 0;
        r5 = android.provider.MediaStore.Images.Media.query(r7, r8, r9, r10, r11, r12);	 Catch:{ all -> 0x0073 }
        if (r5 == 0) goto L_0x006d;
    L_0x0062:
        r0 = r5.moveToNext();	 Catch:{ all -> 0x0073 }
        if (r0 == 0) goto L_0x006d;
    L_0x0068:
        r0 = r5.getInt(r4);	 Catch:{ all -> 0x0073 }
        r6 = r6 + r0;
    L_0x006d:
        if (r5 == 0) goto L_0x007a;
    L_0x006f:
        r5.close();
        goto L_0x007a;
    L_0x0073:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0089 }
        if (r5 == 0) goto L_0x007a;
    L_0x0079:
        goto L_0x006f;
    L_0x007a:
        if (r13 == r6) goto L_0x0088;
    L_0x007c:
        r13 = refreshGalleryRunnable;
        if (r13 == 0) goto L_0x0085;
    L_0x0080:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r13);
        refreshGalleryRunnable = r3;
    L_0x0085:
        loadGalleryPhotosAlbums(r4);
    L_0x0088:
        return;
    L_0x0089:
        r13 = move-exception;
        if (r5 == 0) goto L_0x008f;
    L_0x008c:
        r5.close();
    L_0x008f:
        throw r13;
    L_0x0090:
        r13 = move-exception;
        if (r5 == 0) goto L_0x0096;
    L_0x0093:
        r5.close();
    L_0x0096:
        goto L_0x0098;
    L_0x0097:
        throw r13;
    L_0x0098:
        goto L_0x0097;
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
        String[] strArr = new String[7];
        strArr[0] = "_data";
        strArr[1] = "_display_name";
        strArr[2] = "bucket_display_name";
        strArr[3] = VERSION.SDK_INT > 28 ? "date_modified" : "datetaken";
        strArr[4] = "title";
        strArr[5] = "width";
        strArr[6] = "height";
        this.mediaProjections = strArr;
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
                            MediaController.this.stopRecording(2, false, 0);
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
                            float f;
                            float bufferedPosition;
                            float f2 = 0.0f;
                            if (MediaController.this.videoPlayer != null) {
                                duration = MediaController.this.videoPlayer.getDuration();
                                currentPosition = MediaController.this.videoPlayer.getCurrentPosition();
                                if (currentPosition >= 0) {
                                    if (duration > 0) {
                                        f = (float) duration;
                                        bufferedPosition = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / f;
                                        if (duration >= 0) {
                                            f2 = ((float) currentPosition) / f;
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
                            bufferedPosition = duration >= 0 ? ((float) currentPosition) / ((float) duration) : 0.0f;
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
            if (!((Boolean) objArr[2]).booleanValue()) {
                i = ((Integer) objArr[1]).intValue();
                ArrayList arrayList2 = (ArrayList) objArr[0];
                messageObject = this.playingMessageObject;
                if (messageObject != null && i == messageObject.messageOwner.to_id.channel_id && arrayList2.contains(Integer.valueOf(messageObject.getId()))) {
                    cleanupPlayer(true, true);
                }
                ArrayList arrayList3 = this.voiceMessagesPlaylist;
                if (!(arrayList3 == null || arrayList3.isEmpty() || i != ((MessageObject) this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id)) {
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
            if (messageObject2 != null && messageObject2.isMusic() && this.playingMessageObject.getDialogId() == longValue && !this.playingMessageObject.scheduled) {
                arrayList = (ArrayList) objArr[1];
                this.playlist.addAll(0, arrayList);
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                    this.currentPlaylistNum = 0;
                } else {
                    this.currentPlaylistNum += arrayList.size();
                }
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!((Boolean) objArr[2]).booleanValue()) {
                arrayList = this.voiceMessagesPlaylist;
                if (!(arrayList == null || arrayList.isEmpty())) {
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
                float[] fArr3 = this.gravity;
                double d2 = (double) fArr3[0];
                Double.isNaN(d2);
                d2 *= d;
                double d3 = 1.0d - d;
                float[] fArr4 = sensorEvent2.values;
                double d4 = (double) fArr4[0];
                Double.isNaN(d4);
                fArr3[0] = (float) (d2 + (d4 * d3));
                d4 = (double) fArr3[1];
                Double.isNaN(d4);
                d4 *= d;
                d2 = (double) fArr4[1];
                Double.isNaN(d2);
                fArr3[1] = (float) (d4 + (d2 * d3));
                d4 = (double) fArr3[2];
                Double.isNaN(d4);
                d *= d4;
                d4 = (double) fArr4[2];
                Double.isNaN(d4);
                fArr3[2] = (float) (d + (d3 * d4));
                fArr2 = this.gravityFast;
                fArr2[0] = (fArr3[0] * 0.8f) + (fArr4[0] * 0.19999999f);
                fArr2[1] = (fArr3[1] * 0.8f) + (fArr4[1] * 0.19999999f);
                fArr2[2] = (fArr3[2] * 0.8f) + (fArr4[2] * 0.19999999f);
                fArr2 = this.linearAcceleration;
                fArr2[0] = fArr4[0] - fArr3[0];
                fArr2[1] = fArr4[1] - fArr3[1];
                fArr2[2] = fArr4[2] - fArr3[2];
            } else if (sensor == this.linearSensor) {
                fArr2 = this.linearAcceleration;
                fArr = sensorEvent2.values;
                fArr2[0] = fArr[0];
                fArr2[1] = fArr[1];
                fArr2[2] = fArr[2];
            } else if (sensor == this.gravitySensor) {
                fArr2 = this.gravityFast;
                fArr = this.gravity;
                float[] fArr5 = sensorEvent2.values;
                float f2 = fArr5[0];
                fArr[0] = f2;
                fArr2[0] = f2;
                f2 = fArr5[1];
                fArr[1] = f2;
                fArr2[1] = f2;
                float f3 = fArr5[2];
                fArr[2] = f3;
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
                        startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null, this.raiseChat.getClassGuid());
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
                        stopRecording(2, false, 0);
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
                startRecording(chatActivity.getCurrentAccount(), this.raiseChat.getDialogId(), null, this.raiseChat.getClassGuid());
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
        stopRecording(z ? 2 : 0, false, 0);
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
        if (messageObject.isMusic() && !messageObject.scheduled) {
            if (SharedConfig.shuffleMusic) {
                buildShuffledPlayList();
                this.currentPlaylistNum = 0;
            }
            if (z) {
                MediaDataController.getInstance(messageObject.currentAccount).loadMusic(messageObject.getDialogId(), ((MessageObject) this.playlist.get(0)).getIdWithChannel());
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

    /* JADX WARNING: Removed duplicated region for block: B:58:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x005b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x010d  */
    private void playNextMessageWithoutOrder(boolean r8) {
        /*
        r7 = this;
        r0 = org.telegram.messenger.SharedConfig.shuffleMusic;
        if (r0 == 0) goto L_0x0007;
    L_0x0004:
        r0 = r7.shuffledPlaylist;
        goto L_0x0009;
    L_0x0007:
        r0 = r7.playlist;
    L_0x0009:
        r1 = 0;
        r2 = 2;
        r3 = 0;
        r4 = 1;
        if (r8 == 0) goto L_0x0032;
    L_0x000f:
        r5 = org.telegram.messenger.SharedConfig.repeatMode;
        if (r5 == r2) goto L_0x001b;
    L_0x0013:
        if (r5 != r4) goto L_0x0032;
    L_0x0015:
        r5 = r0.size();
        if (r5 != r4) goto L_0x0032;
    L_0x001b:
        r5 = r7.forceLoopCurrentPlaylist;
        if (r5 != 0) goto L_0x0032;
    L_0x001f:
        r7.cleanupPlayer(r3, r3);
        r8 = r7.currentPlaylistNum;
        r8 = r0.get(r8);
        r8 = (org.telegram.messenger.MessageObject) r8;
        r8.audioProgress = r1;
        r8.audioProgressSec = r3;
        r7.playMessage(r8);
        return;
    L_0x0032:
        r5 = org.telegram.messenger.SharedConfig.playOrderReversed;
        if (r5 == 0) goto L_0x0046;
    L_0x0036:
        r5 = r7.currentPlaylistNum;
        r5 = r5 + r4;
        r7.currentPlaylistNum = r5;
        r5 = r7.currentPlaylistNum;
        r6 = r0.size();
        if (r5 < r6) goto L_0x0058;
    L_0x0043:
        r7.currentPlaylistNum = r3;
        goto L_0x0056;
    L_0x0046:
        r5 = r7.currentPlaylistNum;
        r5 = r5 - r4;
        r7.currentPlaylistNum = r5;
        r5 = r7.currentPlaylistNum;
        if (r5 >= 0) goto L_0x0058;
    L_0x004f:
        r5 = r0.size();
        r5 = r5 - r4;
        r7.currentPlaylistNum = r5;
    L_0x0056:
        r5 = 1;
        goto L_0x0059;
    L_0x0058:
        r5 = 0;
    L_0x0059:
        if (r5 == 0) goto L_0x00fe;
    L_0x005b:
        if (r8 == 0) goto L_0x00fe;
    L_0x005d:
        r8 = org.telegram.messenger.SharedConfig.repeatMode;
        if (r8 != 0) goto L_0x00fe;
    L_0x0061:
        r8 = r7.forceLoopCurrentPlaylist;
        if (r8 != 0) goto L_0x00fe;
    L_0x0065:
        r8 = r7.audioPlayer;
        if (r8 != 0) goto L_0x006d;
    L_0x0069:
        r8 = r7.videoPlayer;
        if (r8 == 0) goto L_0x00fd;
    L_0x006d:
        r8 = r7.audioPlayer;
        r0 = 0;
        if (r8 == 0) goto L_0x007d;
    L_0x0072:
        r8.releasePlayer(r4);	 Catch:{ Exception -> 0x0076 }
        goto L_0x007a;
    L_0x0076:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x007a:
        r7.audioPlayer = r0;
        goto L_0x00b4;
    L_0x007d:
        r8 = r7.videoPlayer;
        if (r8 == 0) goto L_0x00b4;
    L_0x0081:
        r7.currentAspectRatioFrameLayout = r0;
        r7.currentTextureViewContainer = r0;
        r7.currentAspectRatioFrameLayoutReady = r3;
        r7.currentTextureView = r0;
        r8.releasePlayer(r4);
        r7.videoPlayer = r0;
        r8 = r7.baseActivity;	 Catch:{ Exception -> 0x009a }
        r8 = r8.getWindow();	 Catch:{ Exception -> 0x009a }
        r0 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r8.clearFlags(r0);	 Catch:{ Exception -> 0x009a }
        goto L_0x009e;
    L_0x009a:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x009e:
        r8 = r7.setLoadingRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r8);
        r8 = r7.playingMessageObject;
        r8 = r8.currentAccount;
        r8 = org.telegram.messenger.FileLoader.getInstance(r8);
        r0 = r7.playingMessageObject;
        r0 = r0.getDocument();
        r8.removeLoadingVideo(r0, r4, r3);
    L_0x00b4:
        r7.stopProgressTimer();
        r5 = 0;
        r7.lastProgress = r5;
        r7.isPaused = r4;
        r8 = r7.playingMessageObject;
        r8.audioProgress = r1;
        r8.audioProgressSec = r3;
        r8 = r8.currentAccount;
        r8 = org.telegram.messenger.NotificationCenter.getInstance(r8);
        r0 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r1 = new java.lang.Object[r2];
        r2 = r7.playingMessageObject;
        r2 = r2.getId();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r3] = r2;
        r2 = java.lang.Integer.valueOf(r3);
        r1[r4] = r2;
        r8.postNotificationName(r0, r1);
        r8 = r7.playingMessageObject;
        r8 = r8.currentAccount;
        r8 = org.telegram.messenger.NotificationCenter.getInstance(r8);
        r0 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        r1 = new java.lang.Object[r4];
        r2 = r7.playingMessageObject;
        r2 = r2.getId();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r3] = r2;
        r8.postNotificationName(r0, r1);
    L_0x00fd:
        return;
    L_0x00fe:
        r8 = r7.currentPlaylistNum;
        if (r8 < 0) goto L_0x011d;
    L_0x0102:
        r1 = r0.size();
        if (r8 < r1) goto L_0x0109;
    L_0x0108:
        goto L_0x011d;
    L_0x0109:
        r8 = r7.playingMessageObject;
        if (r8 == 0) goto L_0x0110;
    L_0x010d:
        r8.resetPlayingProgress();
    L_0x0110:
        r7.playMusicAgain = r4;
        r8 = r7.currentPlaylistNum;
        r8 = r0.get(r8);
        r8 = (org.telegram.messenger.MessageObject) r8;
        r7.playMessage(r8);
    L_0x011d:
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

    /* JADX WARNING: Removed duplicated region for block: B:155:0x0348 A:{SYNTHETIC, Splitter:B:155:0x0348} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x032f  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x03f3  */
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
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x00e3 }
        r2.startService(r0);	 Catch:{ all -> 0x00e3 }
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
        r0 = new org.telegram.messenger.-$$Lambda$MediaController$7XHGlNaKi1-Kl3GUSi4RIeFecPw;	 Catch:{ Exception -> 0x025b }
        r0.<init>(r2, r9);	 Catch:{ Exception -> 0x025b }
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
        goto L_0x0411;
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
        if (r5 != 0) goto L_0x0304;
    L_0x02e6:
        r5 = r2.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r10 = r4;
        r3 = r18.getDialogId();
        r13 = r2.scheduled;
        r3 = r5.isDialogVisible(r3, r13);
        if (r3 != 0) goto L_0x02fa;
    L_0x02f9:
        goto L_0x0305;
    L_0x02fa:
        r3 = r1.currentTextureView;
        if (r3 == 0) goto L_0x032d;
    L_0x02fe:
        r4 = r1.videoPlayer;
        r4.setTextureView(r3);
        goto L_0x032d;
    L_0x0304:
        r10 = r4;
    L_0x0305:
        r3 = r1.pipRoundVideoView;
        if (r3 != 0) goto L_0x0320;
    L_0x0309:
        r3 = new org.telegram.ui.Components.PipRoundVideoView;	 Catch:{ Exception -> 0x031d }
        r3.<init>();	 Catch:{ Exception -> 0x031d }
        r1.pipRoundVideoView = r3;	 Catch:{ Exception -> 0x031d }
        r3 = r1.pipRoundVideoView;	 Catch:{ Exception -> 0x031d }
        r4 = r1.baseActivity;	 Catch:{ Exception -> 0x031d }
        r5 = new org.telegram.messenger.-$$Lambda$MediaController$u8rACRf9hl-QJDf7Qe2JZbJv__Q;	 Catch:{ Exception -> 0x031d }
        r5.<init>(r1);	 Catch:{ Exception -> 0x031d }
        r3.show(r4, r5);	 Catch:{ Exception -> 0x031d }
        goto L_0x0320;
    L_0x031d:
        r3 = 0;
        r1.pipRoundVideoView = r3;
    L_0x0320:
        r3 = r1.pipRoundVideoView;
        if (r3 == 0) goto L_0x032d;
    L_0x0324:
        r4 = r1.videoPlayer;
        r3 = r3.getTextureView();
        r4.setTextureView(r3);
    L_0x032d:
        if (r8 == 0) goto L_0x0348;
    L_0x032f:
        r3 = r2.mediaExists;
        if (r3 != 0) goto L_0x033d;
    L_0x0333:
        if (r9 == r0) goto L_0x033d;
    L_0x0335:
        r0 = new org.telegram.messenger.-$$Lambda$MediaController$Qgb2OSfNQG6gFX8wVP4jbLspt68;
        r0.<init>(r2, r9);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x033d:
        r0 = r1.videoPlayer;
        r3 = android.net.Uri.fromFile(r9);
        r0.preparePlayer(r3, r11);
        goto L_0x03ed;
    L_0x0348:
        r0 = r2.currentAccount;	 Catch:{ Exception -> 0x03e9 }
        r0 = org.telegram.messenger.FileLoader.getInstance(r0);	 Catch:{ Exception -> 0x03e9 }
        r0 = r0.getFileReference(r2);	 Catch:{ Exception -> 0x03e9 }
        r3 = r18.getDocument();	 Catch:{ Exception -> 0x03e9 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03e9 }
        r4.<init>();	 Catch:{ Exception -> 0x03e9 }
        r4.append(r6);	 Catch:{ Exception -> 0x03e9 }
        r5 = r2.currentAccount;	 Catch:{ Exception -> 0x03e9 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r4.append(r15);	 Catch:{ Exception -> 0x03e9 }
        r5 = r3.id;	 Catch:{ Exception -> 0x03e9 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r4.append(r14);	 Catch:{ Exception -> 0x03e9 }
        r5 = r3.access_hash;	 Catch:{ Exception -> 0x03e9 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r5 = "&dc=";
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r5 = r3.dc_id;	 Catch:{ Exception -> 0x03e9 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r5 = "&size=";
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r5 = r3.size;	 Catch:{ Exception -> 0x03e9 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r5 = "&mime=";
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r5 = r3.mime_type;	 Catch:{ Exception -> 0x03e9 }
        r5 = java.net.URLEncoder.encode(r5, r12);	 Catch:{ Exception -> 0x03e9 }
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r5 = "&rid=";
        r4.append(r5);	 Catch:{ Exception -> 0x03e9 }
        r4.append(r0);	 Catch:{ Exception -> 0x03e9 }
        r0 = "&name=";
        r4.append(r0);	 Catch:{ Exception -> 0x03e9 }
        r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r3);	 Catch:{ Exception -> 0x03e9 }
        r0 = java.net.URLEncoder.encode(r0, r12);	 Catch:{ Exception -> 0x03e9 }
        r4.append(r0);	 Catch:{ Exception -> 0x03e9 }
        r0 = "&reference=";
        r4.append(r0);	 Catch:{ Exception -> 0x03e9 }
        r0 = r3.file_reference;	 Catch:{ Exception -> 0x03e9 }
        if (r0 == 0) goto L_0x03b9;
    L_0x03b6:
        r0 = r3.file_reference;	 Catch:{ Exception -> 0x03e9 }
        goto L_0x03bc;
    L_0x03b9:
        r3 = 0;
        r0 = new byte[r3];	 Catch:{ Exception -> 0x03e9 }
    L_0x03bc:
        r0 = org.telegram.messenger.Utilities.bytesToHex(r0);	 Catch:{ Exception -> 0x03e9 }
        r4.append(r0);	 Catch:{ Exception -> 0x03e9 }
        r0 = r4.toString();	 Catch:{ Exception -> 0x03e9 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03e9 }
        r3.<init>();	 Catch:{ Exception -> 0x03e9 }
        r4 = "tg://";
        r3.append(r4);	 Catch:{ Exception -> 0x03e9 }
        r4 = r18.getFileName();	 Catch:{ Exception -> 0x03e9 }
        r3.append(r4);	 Catch:{ Exception -> 0x03e9 }
        r3.append(r0);	 Catch:{ Exception -> 0x03e9 }
        r0 = r3.toString();	 Catch:{ Exception -> 0x03e9 }
        r0 = android.net.Uri.parse(r0);	 Catch:{ Exception -> 0x03e9 }
        r3 = r1.videoPlayer;	 Catch:{ Exception -> 0x03e9 }
        r3.preparePlayer(r0, r11);	 Catch:{ Exception -> 0x03e9 }
        goto L_0x03ed;
    L_0x03e9:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03ed:
        r0 = r18.isRoundVideo();
        if (r0 == 0) goto L_0x040b;
    L_0x03f3:
        r0 = r1.videoPlayer;
        r3 = r1.useFrontSpeaker;
        if (r3 == 0) goto L_0x03fb;
    L_0x03f9:
        r3 = 0;
        goto L_0x03fc;
    L_0x03fb:
        r3 = 3;
    L_0x03fc:
        r0.setStreamType(r3);
        r0 = r1.currentPlaybackSpeed;
        r3 = (r0 > r16 ? 1 : (r0 == r16 ? 0 : -1));
        if (r3 <= 0) goto L_0x0411;
    L_0x0405:
        r3 = r1.videoPlayer;
        r3.setPlaybackSpeed(r0);
        goto L_0x0411;
    L_0x040b:
        r0 = r1.videoPlayer;
        r3 = 3;
        r0.setStreamType(r3);
    L_0x0411:
        r17.checkAudioFocus(r18);
        r17.setPlayerVolume();
        r3 = 0;
        r1.isPaused = r3;
        r3 = 0;
        r1.lastProgress = r3;
        r1.playingMessageObject = r2;
        r0 = org.telegram.messenger.SharedConfig.raiseToSpeak;
        if (r0 != 0) goto L_0x0429;
    L_0x0424:
        r0 = r1.raiseChat;
        r1.startRaiseToEarSensors(r0);
    L_0x0429:
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
        if (r0 == 0) goto L_0x04b1;
    L_0x044b:
        r9 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0483 }
        r9 = r9.audioProgress;	 Catch:{ Exception -> 0x0483 }
        r11 = 0;
        r9 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r9 == 0) goto L_0x04ab;
    L_0x0454:
        r11 = r0.getDuration();	 Catch:{ Exception -> 0x0483 }
        r0 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1));
        if (r0 != 0) goto L_0x0465;
    L_0x045c:
        r0 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0483 }
        r0 = r0.getDuration();	 Catch:{ Exception -> 0x0483 }
        r5 = (long) r0;	 Catch:{ Exception -> 0x0483 }
        r11 = r5 * r3;
    L_0x0465:
        r0 = (float) r11;	 Catch:{ Exception -> 0x0483 }
        r3 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0483 }
        r3 = r3.audioProgress;	 Catch:{ Exception -> 0x0483 }
        r0 = r0 * r3;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0483 }
        r3 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0483 }
        r3 = r3.audioProgressMs;	 Catch:{ Exception -> 0x0483 }
        if (r3 == 0) goto L_0x047c;
    L_0x0473:
        r0 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0483 }
        r0 = r0.audioProgressMs;	 Catch:{ Exception -> 0x0483 }
        r3 = r1.playingMessageObject;	 Catch:{ Exception -> 0x0483 }
        r4 = 0;
        r3.audioProgressMs = r4;	 Catch:{ Exception -> 0x0483 }
    L_0x047c:
        r3 = r1.videoPlayer;	 Catch:{ Exception -> 0x0483 }
        r4 = (long) r0;	 Catch:{ Exception -> 0x0483 }
        r3.seekTo(r4);	 Catch:{ Exception -> 0x0483 }
        goto L_0x04ab;
    L_0x0483:
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
    L_0x04ab:
        r0 = r1.videoPlayer;
        r0.play();
        goto L_0x0504;
    L_0x04b1:
        r0 = r1.audioPlayer;
        if (r0 == 0) goto L_0x0504;
    L_0x04b5:
        r9 = r1.playingMessageObject;	 Catch:{ Exception -> 0x04de }
        r9 = r9.audioProgress;	 Catch:{ Exception -> 0x04de }
        r11 = 0;
        r9 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r9 == 0) goto L_0x0504;
    L_0x04be:
        r11 = r0.getDuration();	 Catch:{ Exception -> 0x04de }
        r0 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1));
        if (r0 != 0) goto L_0x04cf;
    L_0x04c6:
        r0 = r1.playingMessageObject;	 Catch:{ Exception -> 0x04de }
        r0 = r0.getDuration();	 Catch:{ Exception -> 0x04de }
        r5 = (long) r0;	 Catch:{ Exception -> 0x04de }
        r11 = r5 * r3;
    L_0x04cf:
        r0 = (float) r11;	 Catch:{ Exception -> 0x04de }
        r3 = r1.playingMessageObject;	 Catch:{ Exception -> 0x04de }
        r3 = r3.audioProgress;	 Catch:{ Exception -> 0x04de }
        r0 = r0 * r3;
        r0 = (int) r0;	 Catch:{ Exception -> 0x04de }
        r3 = r1.audioPlayer;	 Catch:{ Exception -> 0x04de }
        r4 = (long) r0;	 Catch:{ Exception -> 0x04de }
        r3.seekTo(r4);	 Catch:{ Exception -> 0x04de }
        goto L_0x0504;
    L_0x04de:
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
    L_0x0504:
        r0 = r1.playingMessageObject;
        if (r0 == 0) goto L_0x0522;
    L_0x0508:
        r0 = r0.isMusic();
        if (r0 == 0) goto L_0x0522;
    L_0x050e:
        r0 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = org.telegram.messenger.MusicPlayerService.class;
        r0.<init>(r2, r3);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x051d }
        r2.startService(r0);	 Catch:{ all -> 0x051d }
        goto L_0x0530;
    L_0x051d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0530;
    L_0x0522:
        r0 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = org.telegram.messenger.MusicPlayerService.class;
        r0.<init>(r2, r3);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r2.stopService(r0);
    L_0x0530:
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

    public void startRecording(int i, long j, MessageObject messageObject, int i2) {
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
        -$$Lambda$MediaController$onr8wNNeHjYhZsxmYXu5wc9kwsI -__lambda_mediacontroller_onr8wnnehjyhzsxmyxu5wc9kwsi = new -$$Lambda$MediaController$onr8wNNeHjYhZsxmYXu5wc9kwsI(this, i, i2, j, messageObject);
        this.recordStartRunnable = -__lambda_mediacontroller_onr8wnnehjyhzsxmyxu5wc9kwsi;
        dispatchQueue.postRunnable(-__lambda_mediacontroller_onr8wnnehjyhzsxmyxu5wc9kwsi, obj != null ? 500 : 50);
    }

    public /* synthetic */ void lambda$startRecording$16$MediaController(int i, int i2, long j, MessageObject messageObject) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$oKS3MydZ6et8s4VeBczz9UWiYjw(this, i, i2));
            return;
        }
        this.sendAfterDone = 0;
        this.recordingAudio = new TL_document();
        this.recordingGuid = i2;
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$IhaPZ6tp_jvFQYsGIvGIeQMQRcA(this, i, i2));
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$PnbUDppg1FjWNHXK0EV2FGHJ8Gc(this, i, i2));
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$Ib5QJlgVGnTZDKjbN5E4V3c7y0c(this, i, i2));
        }
    }

    public /* synthetic */ void lambda$null$12$MediaController(int i, int i2) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(i2));
    }

    public /* synthetic */ void lambda$null$13$MediaController(int i, int i2) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(i2));
    }

    public /* synthetic */ void lambda$null$14$MediaController(int i, int i2) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(i2));
    }

    public /* synthetic */ void lambda$null$15$MediaController(int i, int i2) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(i2));
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
            Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$iS-_8TLsJ8t20K7yUHsuxvRoKYA(this, absolutePath, stringBuilder2, messageObject));
        }
    }

    public /* synthetic */ void lambda$generateWaveform$18$MediaController(String str, String str2, MessageObject messageObject) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$ZEgjP6oCFhAKmpnEawPeSsUWjtk(this, str2, getWaveform(str), messageObject));
    }

    public /* synthetic */ void lambda$null$17$MediaController(String str, byte[] bArr, MessageObject messageObject) {
        MessageObject messageObject2 = (MessageObject) this.generatingWaveform.remove(str);
        if (!(messageObject2 == null || bArr == null)) {
            for (int i = 0; i < messageObject2.getDocument().attributes.size(); i++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) messageObject2.getDocument().attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
                    documentAttribute.waveform = bArr;
                    documentAttribute.flags |= 4;
                    break;
                }
            }
            messages_Messages tL_messages_messages = new TL_messages_messages();
            tL_messages_messages.messages.add(messageObject2.messageOwner);
            MessagesStorage.getInstance(messageObject2.currentAccount).putMessages(tL_messages_messages, messageObject2.getDialogId(), -1, 0, false, messageObject.scheduled);
            new ArrayList().add(messageObject2);
            NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject2.getDialogId()), r12);
        }
    }

    private void stopRecordingInternal(int i, boolean z, int i2) {
        if (i != 0) {
            this.fileEncodingQueue.postRunnable(new -$$Lambda$MediaController$TvXOdXZBU9gMtgAhtFqXXe_XiIs(this, this.recordingAudio, this.recordingAudioFile, i, z, i2));
        } else {
            File file = this.recordingAudioFile;
            if (file != null) {
                file.delete();
            }
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

    public /* synthetic */ void lambda$stopRecordingInternal$20$MediaController(TL_document tL_document, File file, int i, boolean z, int i2) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$l2RPzmRSE8YVOWZ10p3ER3-6KNg(this, tL_document, file, i, z, i2));
    }

    public /* synthetic */ void lambda$null$19$MediaController(TL_document tL_document, File file, int i, boolean z, int i2) {
        Document document = tL_document;
        int i3 = i;
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
            int i4;
            if (i3 == 1) {
                i4 = 1;
                SendMessagesHelper.getInstance(this.recordingCurrentAccount).sendMessage(tL_document, null, file.getAbsolutePath(), this.recordDialogId, this.recordReplyingMessageObject, null, null, null, null, z, i2, 0, null);
            } else {
                i4 = 1;
            }
            NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
            int i5 = NotificationCenter.audioDidSent;
            Object[] objArr = new Object[3];
            objArr[0] = Integer.valueOf(this.recordingGuid);
            String str = null;
            int i6 = i;
            objArr[i4] = i6 == 2 ? tL_document : null;
            if (i6 == 2) {
                str = file.getAbsolutePath();
            }
            objArr[2] = str;
            instance.postNotificationName(i5, objArr);
            return;
        }
        NotificationCenter.getInstance(this.recordingCurrentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), Boolean.valueOf(false));
        file.delete();
    }

    public void stopRecording(int i, boolean z, int i2) {
        Runnable runnable = this.recordStartRunnable;
        if (runnable != null) {
            this.recordQueue.cancelRunnable(runnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new -$$Lambda$MediaController$r_SS-brxcjXj1vX46zlpzitYpTI(this, i, z, i2));
    }

    public /* synthetic */ void lambda$stopRecording$22$MediaController(int i, boolean z, int i2) {
        if (this.sendAfterDone == 3) {
            this.sendAfterDone = 0;
            stopRecordingInternal(i, z, i2);
            return;
        }
        AudioRecord audioRecord = this.audioRecorder;
        if (audioRecord != null) {
            try {
                this.sendAfterDone = i;
                this.sendAfterDoneNotify = z;
                this.sendAfterDoneScheduleDate = i2;
                audioRecord.stop();
            } catch (Exception e) {
                FileLog.e(e);
                File file = this.recordingAudioFile;
                if (file != null) {
                    file.delete();
                }
            }
            if (i == 0) {
                stopRecordingInternal(0, false, 0);
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
        r3 = new Object[2];
        int i3 = 0;
        r3[0] = Integer.valueOf(this.recordingGuid);
        if (i == 2) {
            i3 = 1;
        }
        r3[1] = Integer.valueOf(i3);
        instance.postNotificationName(i2, r3);
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
        r0 = NUM; // 0x7f0d0590 float:1.8745003E38 double:1.053130481E-314;
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

    /* JADX WARNING: Removed duplicated region for block: B:31:0x009f A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ca A:{Catch:{ all -> 0x0116 }} */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x010a A:{SYNTHETIC, Splitter:B:53:0x010a} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x010f A:{SYNTHETIC, Splitter:B:56:0x010f} */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0131 A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0137 A:{Catch:{ Exception -> 0x0012 }} */
    /* JADX WARNING: Removed duplicated region for block: B:101:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x016d  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:67:0x011d */
    /* JADX WARNING: Missing exception handler attribute for start block: B:77:0x0127 */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:62|63|(2:65|66)|67|68) */
    /* JADX WARNING: Missing block: B:62:0x0116, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:63:0x0117, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:64:0x0118, code skipped:
            if (r18 != null) goto L_0x011a;
     */
    /* JADX WARNING: Missing block: B:66:?, code skipped:
            r18.close();
     */
    /* JADX WARNING: Missing block: B:72:0x0120, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:73:0x0121, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:74:0x0122, code skipped:
            if (r17 != null) goto L_0x0124;
     */
    /* JADX WARNING: Missing block: B:76:?, code skipped:
            r17.close();
     */
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
        goto L_0x0168;
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
        r0 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0128 }
        r11 = r23;
        r0.<init>(r11);	 Catch:{ Exception -> 0x0128 }
        r17 = r0.getChannel();	 Catch:{ Exception -> 0x0128 }
        r0 = new java.io.FileOutputStream;	 Catch:{ all -> 0x011e }
        r0.<init>(r10);	 Catch:{ all -> 0x011e }
        r18 = r0.getChannel();	 Catch:{ all -> 0x011e }
        r13 = r17.size();	 Catch:{ all -> 0x0114 }
        r11 = 0;
        r19 = r6;
        r6 = r11;
    L_0x00c6:
        r0 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1));
        if (r0 >= 0) goto L_0x0108;
    L_0x00ca:
        r0 = r24[r5];	 Catch:{ all -> 0x0114 }
        if (r0 == 0) goto L_0x00cf;
    L_0x00ce:
        goto L_0x0108;
    L_0x00cf:
        r11 = r13 - r6;
        r3 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r15 = java.lang.Math.min(r3, r11);	 Catch:{ all -> 0x0114 }
        r11 = r18;
        r12 = r17;
        r0 = r13;
        r13 = r6;
        r11.transferFrom(r12, r13, r15);	 Catch:{ all -> 0x0114 }
        if (r2 == 0) goto L_0x0101;
    L_0x00e2:
        r11 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0114 }
        r11 = r11 - r8;
        r13 = (r19 > r11 ? 1 : (r19 == r11 ? 0 : -1));
        if (r13 > 0) goto L_0x0101;
    L_0x00eb:
        r11 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0114 }
        r13 = (float) r6;	 Catch:{ all -> 0x0114 }
        r14 = (float) r0;	 Catch:{ all -> 0x0114 }
        r13 = r13 / r14;
        r14 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r13 = r13 * r14;
        r13 = (int) r13;	 Catch:{ all -> 0x0114 }
        r14 = new org.telegram.messenger.-$$Lambda$MediaController$d2YFTKUcKqMuUp1bMMx0EKpPu88;	 Catch:{ all -> 0x0114 }
        r14.<init>(r2, r13);	 Catch:{ all -> 0x0114 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r14);	 Catch:{ all -> 0x0114 }
        r19 = r11;
    L_0x0101:
        r6 = r6 + r3;
        r13 = r0;
        r3 = 2;
        r4 = 1;
        r1 = r21;
        goto L_0x00c6;
    L_0x0108:
        if (r18 == 0) goto L_0x010d;
    L_0x010a:
        r18.close();	 Catch:{ all -> 0x011e }
    L_0x010d:
        if (r17 == 0) goto L_0x0112;
    L_0x010f:
        r17.close();	 Catch:{ Exception -> 0x0128 }
    L_0x0112:
        r0 = 1;
        goto L_0x012d;
    L_0x0114:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x0116 }
    L_0x0116:
        r0 = move-exception;
        r1 = r0;
        if (r18 == 0) goto L_0x011d;
    L_0x011a:
        r18.close();	 Catch:{ all -> 0x011d }
    L_0x011d:
        throw r1;	 Catch:{ all -> 0x011e }
    L_0x011e:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x0120 }
    L_0x0120:
        r0 = move-exception;
        r1 = r0;
        if (r17 == 0) goto L_0x0127;
    L_0x0124:
        r17.close();	 Catch:{ all -> 0x0127 }
    L_0x0127:
        throw r1;	 Catch:{ Exception -> 0x0128 }
    L_0x0128:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0012 }
        r0 = 0;
    L_0x012d:
        r1 = r24[r5];	 Catch:{ Exception -> 0x0012 }
        if (r1 == 0) goto L_0x0135;
    L_0x0131:
        r10.delete();	 Catch:{ Exception -> 0x0012 }
        r0 = 0;
    L_0x0135:
        if (r0 == 0) goto L_0x016b;
    L_0x0137:
        r3 = 2;
        r1 = r21;
        if (r1 != r3) goto L_0x0160;
    L_0x013c:
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
        goto L_0x016b;
    L_0x0160:
        r0 = android.net.Uri.fromFile(r10);	 Catch:{ Exception -> 0x0012 }
        org.telegram.messenger.AndroidUtilities.addMediaToGallery(r0);	 Catch:{ Exception -> 0x0012 }
        goto L_0x016b;
    L_0x0168:
        org.telegram.messenger.FileLog.e(r0);
    L_0x016b:
        if (r2 == 0) goto L_0x0175;
    L_0x016d:
        r0 = new org.telegram.messenger.-$$Lambda$MediaController$8qrRdww485ZG9hqc7_0dXYZW_go;
        r0.<init>(r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x0175:
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0040 */
    /* JADX WARNING: Missing block: B:16:0x003b, code skipped:
            if (r1 != null) goto L_0x003d;
     */
    /* JADX WARNING: Missing block: B:18:?, code skipped:
            r1.close();
     */
    public static java.lang.String getFileName(android.net.Uri r10) {
        /*
        r0 = "_display_name";
        r1 = r10.getScheme();
        r2 = "content";
        r1 = r1.equals(r2);
        r2 = 1;
        r3 = 0;
        if (r1 == 0) goto L_0x0045;
    L_0x0010:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0041 }
        r4 = r1.getContentResolver();	 Catch:{ Exception -> 0x0041 }
        r6 = new java.lang.String[r2];	 Catch:{ Exception -> 0x0041 }
        r1 = 0;
        r6[r1] = r0;	 Catch:{ Exception -> 0x0041 }
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r5 = r10;
        r1 = r4.query(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x0041 }
        r4 = r1.moveToFirst();	 Catch:{ all -> 0x0038 }
        if (r4 == 0) goto L_0x0032;
    L_0x0029:
        r0 = r1.getColumnIndex(r0);	 Catch:{ all -> 0x0038 }
        r0 = r1.getString(r0);	 Catch:{ all -> 0x0038 }
        r3 = r0;
    L_0x0032:
        if (r1 == 0) goto L_0x0045;
    L_0x0034:
        r1.close();	 Catch:{ Exception -> 0x0041 }
        goto L_0x0045;
    L_0x0038:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x003a }
    L_0x003a:
        r0 = move-exception;
        if (r1 == 0) goto L_0x0040;
    L_0x003d:
        r1.close();	 Catch:{ all -> 0x0040 }
    L_0x0040:
        throw r0;	 Catch:{ Exception -> 0x0041 }
    L_0x0041:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0045:
        if (r3 != 0) goto L_0x0059;
    L_0x0047:
        r3 = r10.getPath();
        r10 = 47;
        r10 = r3.lastIndexOf(r10);
        r0 = -1;
        if (r10 == r0) goto L_0x0059;
    L_0x0054:
        r10 = r10 + r2;
        r3 = r3.substring(r10);
    L_0x0059:
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

    /* JADX WARNING: Removed duplicated region for block: B:234:0x03b6 A:{SYNTHETIC, Splitter:B:234:0x03b6} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x03b6 A:{SYNTHETIC, Splitter:B:234:0x03b6} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0226 A:{SYNTHETIC, Splitter:B:124:0x0226} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ca A:{SYNTHETIC, Splitter:B:244:0x03ca} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ca A:{SYNTHETIC, Splitter:B:244:0x03ca} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ca A:{SYNTHETIC, Splitter:B:244:0x03ca} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ca A:{SYNTHETIC, Splitter:B:244:0x03ca} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ca A:{SYNTHETIC, Splitter:B:244:0x03ca} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ca A:{SYNTHETIC, Splitter:B:244:0x03ca} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ca A:{SYNTHETIC, Splitter:B:244:0x03ca} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ca A:{SYNTHETIC, Splitter:B:244:0x03ca} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03df A:{LOOP_END, LOOP:2: B:250:0x03d9->B:252:0x03df} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x024b A:{SYNTHETIC, Splitter:B:134:0x024b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025a A:{SYNTHETIC, Splitter:B:142:0x025a} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0288 A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b A:{Catch:{ all -> 0x03c0 }} */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0293 A:{SYNTHETIC, Splitter:B:159:0x0293} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03b0  */
    static /* synthetic */ void lambda$loadGalleryPhotosAlbums$28(int r41) {
        /*
        r1 = "AllMedia";
        r2 = "datetaken";
        r3 = "_data";
        r4 = "bucket_display_name";
        r5 = "bucket_id";
        r6 = "_id";
        r7 = "datetaken DESC";
        r8 = "android.permission.READ_EXTERNAL_STORAGE";
        r9 = "date_modified";
        r11 = new java.util.ArrayList;
        r11.<init>();
        r12 = new java.util.ArrayList;
        r12.<init>();
        r10 = new android.util.SparseArray;
        r10.<init>();
        r13 = new android.util.SparseArray;
        r13.<init>();
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0043 }
        r0.<init>();	 Catch:{ Exception -> 0x0043 }
        r15 = android.os.Environment.DIRECTORY_DCIM;	 Catch:{ Exception -> 0x0043 }
        r15 = android.os.Environment.getExternalStoragePublicDirectory(r15);	 Catch:{ Exception -> 0x0043 }
        r15 = r15.getAbsolutePath();	 Catch:{ Exception -> 0x0043 }
        r0.append(r15);	 Catch:{ Exception -> 0x0043 }
        r15 = "/Camera/";
        r0.append(r15);	 Catch:{ Exception -> 0x0043 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0043 }
        r15 = r0;
        goto L_0x0048;
    L_0x0043:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r15 = 0;
    L_0x0048:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0232 }
        r14 = 23;
        if (r0 < r14) goto L_0x0071;
    L_0x004e:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0232 }
        if (r0 < r14) goto L_0x005b;
    L_0x0052:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0232 }
        r0 = r0.checkSelfPermission(r8);	 Catch:{ all -> 0x0232 }
        if (r0 != 0) goto L_0x005b;
    L_0x005a:
        goto L_0x0071;
    L_0x005b:
        r20 = r2;
        r23 = r3;
        r24 = r4;
        r33 = r5;
        r21 = r7;
        r22 = r9;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r32 = 0;
        goto L_0x0224;
    L_0x0071:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0232 }
        r20 = r0.getContentResolver();	 Catch:{ all -> 0x0232 }
        r21 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ all -> 0x0232 }
        r22 = projectionPhotos;	 Catch:{ all -> 0x0232 }
        r23 = 0;
        r24 = 0;
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0232 }
        r14 = 28;
        if (r0 <= r14) goto L_0x0088;
    L_0x0085:
        r25 = r9;
        goto L_0x008a;
    L_0x0088:
        r25 = r7;
    L_0x008a:
        r14 = android.provider.MediaStore.Images.Media.query(r20, r21, r22, r23, r24, r25);	 Catch:{ all -> 0x0232 }
        if (r14 == 0) goto L_0x0210;
    L_0x0090:
        r0 = r14.getColumnIndex(r6);	 Catch:{ all -> 0x01fb }
        r20 = r2;
        r2 = r14.getColumnIndex(r5);	 Catch:{ all -> 0x01f9 }
        r21 = r7;
        r7 = r14.getColumnIndex(r4);	 Catch:{ all -> 0x01f1 }
        r22 = r9;
        r9 = r14.getColumnIndex(r3);	 Catch:{ all -> 0x01e9 }
        r23 = r3;
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x01e7 }
        r24 = r4;
        r4 = 28;
        if (r3 <= r4) goto L_0x00b3;
    L_0x00b0:
        r3 = r22;
        goto L_0x00b5;
    L_0x00b3:
        r3 = r20;
    L_0x00b5:
        r3 = r14.getColumnIndex(r3);	 Catch:{ all -> 0x01e5 }
        r4 = "orientation";
        r4 = r14.getColumnIndex(r4);	 Catch:{ all -> 0x01e5 }
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
    L_0x00c7:
        r29 = r14.moveToNext();	 Catch:{ all -> 0x01dc }
        if (r29 == 0) goto L_0x01d7;
    L_0x00cd:
        r32 = r14.getInt(r0);	 Catch:{ all -> 0x01dc }
        r29 = r0;
        r0 = r14.getInt(r2);	 Catch:{ all -> 0x01dc }
        r38 = r2;
        r2 = r14.getString(r7);	 Catch:{ all -> 0x01dc }
        r39 = r7;
        r7 = r14.getString(r9);	 Catch:{ all -> 0x01dc }
        r33 = r14.getLong(r3);	 Catch:{ all -> 0x01dc }
        r36 = r14.getInt(r4);	 Catch:{ all -> 0x01dc }
        if (r7 == 0) goto L_0x01bb;
    L_0x00ed:
        r30 = r7.length();	 Catch:{ all -> 0x01dc }
        if (r30 != 0) goto L_0x00f5;
    L_0x00f3:
        goto L_0x01bb;
    L_0x00f5:
        r40 = r3;
        r3 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ all -> 0x01dc }
        r37 = 0;
        r30 = r3;
        r31 = r0;
        r35 = r7;
        r30.<init>(r31, r32, r33, r35, r36, r37);	 Catch:{ all -> 0x01dc }
        if (r25 != 0) goto L_0x0128;
    L_0x0106:
        r30 = r4;
        r4 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x0121 }
        r31 = r9;
        r9 = "AllPhotos";
        r32 = r14;
        r14 = NUM; // 0x7f0d00cf float:1.8742535E38 double:1.05312988E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r14);	 Catch:{ all -> 0x011f }
        r14 = 0;
        r4.<init>(r14, r9, r3);	 Catch:{ all -> 0x011f }
        r12.add(r14, r4);	 Catch:{ all -> 0x0148 }
        goto L_0x0130;
    L_0x011f:
        r0 = move-exception;
        goto L_0x0124;
    L_0x0121:
        r0 = move-exception;
        r32 = r14;
    L_0x0124:
        r33 = r5;
        goto L_0x01e1;
    L_0x0128:
        r30 = r4;
        r31 = r9;
        r32 = r14;
        r4 = r25;
    L_0x0130:
        if (r26 != 0) goto L_0x014d;
    L_0x0132:
        r14 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x0148 }
        r33 = r5;
        r9 = NUM; // 0x7f0d00ce float:1.8742533E38 double:1.0531298793E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r1, r9);	 Catch:{ all -> 0x0145 }
        r9 = 0;
        r14.<init>(r9, r5, r3);	 Catch:{ all -> 0x0145 }
        r11.add(r9, r14);	 Catch:{ all -> 0x01b6 }
        goto L_0x0151;
    L_0x0145:
        r0 = move-exception;
        goto L_0x0246;
    L_0x0148:
        r0 = move-exception;
        r33 = r5;
        goto L_0x0246;
    L_0x014d:
        r33 = r5;
        r14 = r26;
    L_0x0151:
        r4.addPhoto(r3);	 Catch:{ all -> 0x01b6 }
        r14.addPhoto(r3);	 Catch:{ all -> 0x01b6 }
        r5 = r10.get(r0);	 Catch:{ all -> 0x01b6 }
        r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5;	 Catch:{ all -> 0x01b6 }
        if (r5 != 0) goto L_0x0181;
    L_0x015f:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x01b6 }
        r5.<init>(r0, r2, r3);	 Catch:{ all -> 0x01b6 }
        r10.put(r0, r5);	 Catch:{ all -> 0x01b6 }
        if (r27 != 0) goto L_0x017e;
    L_0x0169:
        if (r15 == 0) goto L_0x017e;
    L_0x016b:
        if (r7 == 0) goto L_0x017e;
    L_0x016d:
        r9 = r7.startsWith(r15);	 Catch:{ all -> 0x01b6 }
        if (r9 == 0) goto L_0x017e;
    L_0x0173:
        r9 = 0;
        r11.add(r9, r5);	 Catch:{ all -> 0x01b6 }
        r9 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x01b6 }
        r27 = r9;
        goto L_0x0181;
    L_0x017e:
        r11.add(r5);	 Catch:{ all -> 0x01b6 }
    L_0x0181:
        r5.addPhoto(r3);	 Catch:{ all -> 0x01b6 }
        r5 = r13.get(r0);	 Catch:{ all -> 0x01b6 }
        r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5;	 Catch:{ all -> 0x01b6 }
        if (r5 != 0) goto L_0x01ae;
    L_0x018c:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x01b6 }
        r5.<init>(r0, r2, r3);	 Catch:{ all -> 0x01b6 }
        r13.put(r0, r5);	 Catch:{ all -> 0x01b6 }
        if (r28 != 0) goto L_0x01ab;
    L_0x0196:
        if (r15 == 0) goto L_0x01ab;
    L_0x0198:
        if (r7 == 0) goto L_0x01ab;
    L_0x019a:
        r2 = r7.startsWith(r15);	 Catch:{ all -> 0x01b6 }
        if (r2 == 0) goto L_0x01ab;
    L_0x01a0:
        r2 = 0;
        r12.add(r2, r5);	 Catch:{ all -> 0x01b6 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x01b6 }
        r28 = r0;
        goto L_0x01ae;
    L_0x01ab:
        r12.add(r5);	 Catch:{ all -> 0x01b6 }
    L_0x01ae:
        r5.addPhoto(r3);	 Catch:{ all -> 0x01b6 }
        r25 = r4;
        r26 = r14;
        goto L_0x01c5;
    L_0x01b6:
        r0 = move-exception;
        r26 = r14;
        goto L_0x0246;
    L_0x01bb:
        r40 = r3;
        r30 = r4;
        r33 = r5;
        r31 = r9;
        r32 = r14;
    L_0x01c5:
        r0 = r29;
        r4 = r30;
        r9 = r31;
        r14 = r32;
        r5 = r33;
        r2 = r38;
        r7 = r39;
        r3 = r40;
        goto L_0x00c7;
    L_0x01d7:
        r33 = r5;
        r32 = r14;
        goto L_0x0224;
    L_0x01dc:
        r0 = move-exception;
        r33 = r5;
        r32 = r14;
    L_0x01e1:
        r4 = r25;
        goto L_0x0246;
    L_0x01e5:
        r0 = move-exception;
        goto L_0x01ee;
    L_0x01e7:
        r0 = move-exception;
        goto L_0x01ec;
    L_0x01e9:
        r0 = move-exception;
        r23 = r3;
    L_0x01ec:
        r24 = r4;
    L_0x01ee:
        r33 = r5;
        goto L_0x0208;
    L_0x01f1:
        r0 = move-exception;
        r23 = r3;
        r24 = r4;
        r33 = r5;
        goto L_0x0206;
    L_0x01f9:
        r0 = move-exception;
        goto L_0x01fe;
    L_0x01fb:
        r0 = move-exception;
        r20 = r2;
    L_0x01fe:
        r23 = r3;
        r24 = r4;
        r33 = r5;
        r21 = r7;
    L_0x0206:
        r22 = r9;
    L_0x0208:
        r32 = r14;
        r4 = 0;
        r26 = 0;
        r27 = 0;
        goto L_0x0246;
    L_0x0210:
        r20 = r2;
        r23 = r3;
        r24 = r4;
        r33 = r5;
        r21 = r7;
        r22 = r9;
        r32 = r14;
        r25 = 0;
        r26 = 0;
        r27 = 0;
    L_0x0224:
        if (r32 == 0) goto L_0x022f;
    L_0x0226:
        r32.close();	 Catch:{ Exception -> 0x022a }
        goto L_0x022f;
    L_0x022a:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x022f:
        r4 = r25;
        goto L_0x0254;
    L_0x0232:
        r0 = move-exception;
        r20 = r2;
        r23 = r3;
        r24 = r4;
        r33 = r5;
        r21 = r7;
        r22 = r9;
        r4 = 0;
        r26 = 0;
        r27 = 0;
        r32 = 0;
    L_0x0246:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0406 }
        if (r32 == 0) goto L_0x0254;
    L_0x024b:
        r32.close();	 Catch:{ Exception -> 0x024f }
        goto L_0x0254;
    L_0x024f:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0254:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x03c0 }
        r2 = 23;
        if (r0 < r2) goto L_0x0274;
    L_0x025a:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x026e }
        if (r0 < r2) goto L_0x0267;
    L_0x025e:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x026e }
        r0 = r0.checkSelfPermission(r8);	 Catch:{ all -> 0x026e }
        if (r0 != 0) goto L_0x0267;
    L_0x0266:
        goto L_0x0274;
    L_0x0267:
        r19 = r32;
    L_0x0269:
        r6 = 0;
        r16 = 0;
        goto L_0x03b4;
    L_0x026e:
        r0 = move-exception;
        r19 = r32;
    L_0x0271:
        r6 = 0;
        goto L_0x03c4;
    L_0x0274:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x03c0 }
        r34 = r0.getContentResolver();	 Catch:{ all -> 0x03c0 }
        r35 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ all -> 0x03c0 }
        r36 = projectionVideo;	 Catch:{ all -> 0x03c0 }
        r37 = 0;
        r38 = 0;
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x03c0 }
        r2 = 28;
        if (r0 <= r2) goto L_0x028b;
    L_0x0288:
        r39 = r22;
        goto L_0x028d;
    L_0x028b:
        r39 = r21;
    L_0x028d:
        r2 = android.provider.MediaStore.Images.Media.query(r34, r35, r36, r37, r38, r39);	 Catch:{ all -> 0x03c0 }
        if (r2 == 0) goto L_0x03b0;
    L_0x0293:
        r0 = r2.getColumnIndex(r6);	 Catch:{ all -> 0x03ab }
        r3 = r33;
        r3 = r2.getColumnIndex(r3);	 Catch:{ all -> 0x03ab }
        r5 = r24;
        r5 = r2.getColumnIndex(r5);	 Catch:{ all -> 0x03ab }
        r6 = r23;
        r6 = r2.getColumnIndex(r6);	 Catch:{ all -> 0x03ab }
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x03ab }
        r8 = 28;
        if (r7 <= r8) goto L_0x02b2;
    L_0x02af:
        r7 = r22;
        goto L_0x02b4;
    L_0x02b2:
        r7 = r20;
    L_0x02b4:
        r7 = r2.getColumnIndex(r7);	 Catch:{ all -> 0x03ab }
        r8 = "duration";
        r8 = r2.getColumnIndex(r8);	 Catch:{ all -> 0x03ab }
        r14 = 0;
    L_0x02bf:
        r9 = r2.moveToNext();	 Catch:{ all -> 0x03a6 }
        if (r9 == 0) goto L_0x03a0;
    L_0x02c5:
        r30 = r2.getInt(r0);	 Catch:{ all -> 0x03a6 }
        r9 = r2.getInt(r3);	 Catch:{ all -> 0x03a6 }
        r13 = r2.getString(r5);	 Catch:{ all -> 0x03a6 }
        r18 = r0;
        r0 = r2.getString(r6);	 Catch:{ all -> 0x03a6 }
        r31 = r2.getLong(r7);	 Catch:{ all -> 0x03a6 }
        r20 = r2.getLong(r8);	 Catch:{ all -> 0x03a6 }
        if (r0 == 0) goto L_0x038b;
    L_0x02e1:
        r16 = r0.length();	 Catch:{ all -> 0x03a6 }
        if (r16 != 0) goto L_0x02e9;
    L_0x02e7:
        goto L_0x038b;
    L_0x02e9:
        r19 = r2;
        r2 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ all -> 0x0389 }
        r22 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r24 = r5;
        r25 = r6;
        r5 = r20 / r22;
        r6 = (int) r5;	 Catch:{ all -> 0x0389 }
        r35 = 1;
        r28 = r2;
        r29 = r9;
        r33 = r0;
        r34 = r6;
        r28.<init>(r29, r30, r31, r33, r34, r35);	 Catch:{ all -> 0x0389 }
        if (r14 != 0) goto L_0x032d;
    L_0x0305:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x0389 }
        r6 = "AllVideos";
        r20 = r3;
        r3 = NUM; // 0x7f0d00d0 float:1.8742537E38 double:1.0531298803E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r6, r3);	 Catch:{ all -> 0x0389 }
        r6 = 0;
        r5.<init>(r6, r3, r2);	 Catch:{ all -> 0x032a }
        r14 = 1;
        r5.videoOnly = r14;	 Catch:{ all -> 0x0326 }
        if (r26 == 0) goto L_0x031c;
    L_0x031b:
        goto L_0x031d;
    L_0x031c:
        r14 = 0;
    L_0x031d:
        if (r4 == 0) goto L_0x0321;
    L_0x031f:
        r14 = r14 + 1;
    L_0x0321:
        r11.add(r14, r5);	 Catch:{ all -> 0x0326 }
        r14 = r5;
        goto L_0x032f;
    L_0x0326:
        r0 = move-exception;
        r14 = r5;
        goto L_0x03a9;
    L_0x032a:
        r0 = move-exception;
        goto L_0x03c5;
    L_0x032d:
        r20 = r3;
    L_0x032f:
        if (r26 != 0) goto L_0x0347;
    L_0x0331:
        r3 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x0389 }
        r5 = NUM; // 0x7f0d00ce float:1.8742533E38 double:1.0531298793E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r1, r5);	 Catch:{ all -> 0x0389 }
        r5 = 0;
        r3.<init>(r5, r6, r2);	 Catch:{ all -> 0x0389 }
        r11.add(r5, r3);	 Catch:{ all -> 0x0342 }
        goto L_0x0349;
    L_0x0342:
        r0 = move-exception;
        r26 = r3;
        goto L_0x03a9;
    L_0x0347:
        r3 = r26;
    L_0x0349:
        r14.addPhoto(r2);	 Catch:{ all -> 0x0384 }
        r3.addPhoto(r2);	 Catch:{ all -> 0x0384 }
        r5 = r10.get(r9);	 Catch:{ all -> 0x0384 }
        r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5;	 Catch:{ all -> 0x0384 }
        if (r5 != 0) goto L_0x037b;
    L_0x0357:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x0384 }
        r5.<init>(r9, r13, r2);	 Catch:{ all -> 0x0384 }
        r10.put(r9, r5);	 Catch:{ all -> 0x0384 }
        if (r27 != 0) goto L_0x0376;
    L_0x0361:
        if (r15 == 0) goto L_0x0376;
    L_0x0363:
        if (r0 == 0) goto L_0x0376;
    L_0x0365:
        r0 = r0.startsWith(r15);	 Catch:{ all -> 0x0384 }
        if (r0 == 0) goto L_0x0376;
    L_0x036b:
        r6 = 0;
        r11.add(r6, r5);	 Catch:{ all -> 0x0382 }
        r0 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0382 }
        r27 = r0;
        goto L_0x037c;
    L_0x0376:
        r6 = 0;
        r11.add(r5);	 Catch:{ all -> 0x0382 }
        goto L_0x037c;
    L_0x037b:
        r6 = 0;
    L_0x037c:
        r5.addPhoto(r2);	 Catch:{ all -> 0x0382 }
        r26 = r3;
        goto L_0x0394;
    L_0x0382:
        r0 = move-exception;
        goto L_0x0386;
    L_0x0384:
        r0 = move-exception;
        r6 = 0;
    L_0x0386:
        r26 = r3;
        goto L_0x03c5;
    L_0x0389:
        r0 = move-exception;
        goto L_0x03a9;
    L_0x038b:
        r19 = r2;
        r20 = r3;
        r24 = r5;
        r25 = r6;
        r6 = 0;
    L_0x0394:
        r0 = r18;
        r2 = r19;
        r3 = r20;
        r5 = r24;
        r6 = r25;
        goto L_0x02bf;
    L_0x03a0:
        r19 = r2;
        r6 = 0;
        r16 = r14;
        goto L_0x03b4;
    L_0x03a6:
        r0 = move-exception;
        r19 = r2;
    L_0x03a9:
        r6 = 0;
        goto L_0x03c5;
    L_0x03ab:
        r0 = move-exception;
        r19 = r2;
        goto L_0x0271;
    L_0x03b0:
        r19 = r2;
        goto L_0x0269;
    L_0x03b4:
        if (r19 == 0) goto L_0x03d5;
    L_0x03b6:
        r19.close();	 Catch:{ Exception -> 0x03ba }
        goto L_0x03d5;
    L_0x03ba:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x03d5;
    L_0x03c0:
        r0 = move-exception;
        r6 = 0;
        r19 = r32;
    L_0x03c4:
        r14 = 0;
    L_0x03c5:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x03f8 }
        if (r19 == 0) goto L_0x03d3;
    L_0x03ca:
        r19.close();	 Catch:{ Exception -> 0x03ce }
        goto L_0x03d3;
    L_0x03ce:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x03d3:
        r16 = r14;
    L_0x03d5:
        r14 = r26;
        r13 = r27;
    L_0x03d9:
        r0 = r11.size();
        if (r6 >= r0) goto L_0x03ef;
    L_0x03df:
        r0 = r11.get(r6);
        r0 = (org.telegram.messenger.MediaController.AlbumEntry) r0;
        r0 = r0.photos;
        r1 = org.telegram.messenger.-$$Lambda$MediaController$8Ha8hH_xAKjV0FeIhj43YMHbiZQ.INSTANCE;
        java.util.Collections.sort(r0, r1);
        r6 = r6 + 1;
        goto L_0x03d9;
    L_0x03ef:
        r17 = 0;
        r10 = r41;
        r15 = r4;
        broadcastNewPhotos(r10, r11, r12, r13, r14, r15, r16, r17);
        return;
    L_0x03f8:
        r0 = move-exception;
        r1 = r0;
        if (r19 == 0) goto L_0x0405;
    L_0x03fc:
        r19.close();	 Catch:{ Exception -> 0x0400 }
        goto L_0x0405;
    L_0x0400:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0405:
        throw r1;
    L_0x0406:
        r0 = move-exception;
        r1 = r0;
        if (r32 == 0) goto L_0x0413;
    L_0x040a:
        r32.close();	 Catch:{ Exception -> 0x040e }
        goto L_0x0413;
    L_0x040e:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0413:
        goto L_0x0415;
    L_0x0414:
        throw r1;
    L_0x0415:
        goto L_0x0414;
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
        allMediaAlbums = arrayList;
        allPhotoAlbums = arrayList2;
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
    /* JADX WARNING: Removed duplicated region for block: B:85:0x017c  */
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
        if (r20 != 0) goto L_0x01a4;
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
        if (r3 == r15) goto L_0x0183;
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
        if (r4 <= 0) goto L_0x0170;
    L_0x011a:
        if (r7 != 0) goto L_0x0170;
    L_0x011c:
        r21 = 0;
        if (r2 != r14) goto L_0x012a;
    L_0x0120:
        r2 = (r11 > r21 ? 1 : (r11 == r21 ? 0 : -1));
        if (r2 <= 0) goto L_0x012a;
    L_0x0124:
        r2 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1));
        if (r2 != 0) goto L_0x012a;
    L_0x0128:
        r0 = r10.presentationTimeUs;
    L_0x012a:
        r26 = r0;
        r0 = (r35 > r21 ? 1 : (r35 == r21 ? 0 : -1));
        if (r0 < 0) goto L_0x0141;
    L_0x0130:
        r0 = r10.presentationTimeUs;
        r2 = (r0 > r35 ? 1 : (r0 == r35 ? 0 : -1));
        if (r2 >= 0) goto L_0x0137;
    L_0x0136:
        goto L_0x0141;
    L_0x0137:
        r15 = r38;
        r13 = r5;
        r7 = 1;
        r8 = 0;
        r21 = 0;
    L_0x013e:
        r23 = 1;
        goto L_0x017a;
    L_0x0141:
        r8 = 0;
        r10.offset = r8;
        r0 = r30.getSampleFlags();
        r10.flags = r0;
        r24 = r9.writeSampleData(r3, r5, r10, r8);
        r2 = 0;
        r0 = (r24 > r2 ? 1 : (r24 == r2 ? 0 : -1));
        if (r0 == 0) goto L_0x016a;
    L_0x0154:
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
        goto L_0x017a;
    L_0x016a:
        r15 = r38;
        r21 = r2;
        r13 = r5;
        goto L_0x013e;
    L_0x0170:
        r15 = r38;
        r13 = r5;
        r8 = 0;
        r21 = 0;
        r23 = 1;
        r26 = r0;
    L_0x017a:
        if (r7 != 0) goto L_0x017f;
    L_0x017c:
        r30.advance();
    L_0x017f:
        r0 = r26;
        r3 = -1;
        goto L_0x0193;
    L_0x0183:
        r15 = r4;
        r13 = r5;
        r3 = -1;
        r8 = 0;
        r21 = 0;
        r23 = 1;
        if (r2 != r3) goto L_0x018f;
    L_0x018d:
        r7 = 1;
        goto L_0x0193;
    L_0x018f:
        r30.advance();
        r7 = 0;
    L_0x0193:
        if (r7 == 0) goto L_0x0197;
    L_0x0195:
        r20 = 1;
    L_0x0197:
        r7 = r28;
        r8 = r30;
        r5 = r13;
        r4 = r15;
        r2 = r21;
        r6 = 1;
        r13 = 0;
        r15 = -1;
        goto L_0x0076;
    L_0x01a4:
        r15 = r4;
        r2 = r30;
        if (r14 < 0) goto L_0x01ac;
    L_0x01a9:
        r2.unselectTrack(r14);
    L_0x01ac:
        if (r15 < 0) goto L_0x01b1;
    L_0x01ae:
        r2.unselectTrack(r15);
    L_0x01b1:
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

    /* JADX WARNING: Removed duplicated region for block: B:116:0x0202 A:{Catch:{ Exception -> 0x0213, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x01d3 A:{Catch:{ Exception -> 0x0213, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x01d3 A:{Catch:{ Exception -> 0x0213, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0202 A:{Catch:{ Exception -> 0x0213, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x055e  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x053f  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x053f  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x055e  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x055e  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x053f  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x053f  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x055e  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x08a1 A:{Catch:{ Exception -> 0x0931, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0871 A:{Catch:{ Exception -> 0x0931, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x07fc A:{SYNTHETIC, Splitter:B:482:0x07fc} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0871 A:{Catch:{ Exception -> 0x0931, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x08a1 A:{Catch:{ Exception -> 0x0931, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x07fc A:{SYNTHETIC, Splitter:B:482:0x07fc} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x08a1 A:{Catch:{ Exception -> 0x0931, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0871 A:{Catch:{ Exception -> 0x0931, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x07a3 A:{Catch:{ Exception -> 0x0769, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x078d A:{Catch:{ Exception -> 0x0769, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x07af A:{Catch:{ Exception -> 0x0769, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x07fc A:{SYNTHETIC, Splitter:B:482:0x07fc} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0871 A:{Catch:{ Exception -> 0x0931, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x08a1 A:{Catch:{ Exception -> 0x0931, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x06fe A:{Catch:{ Exception -> 0x08e9, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x06f8 A:{Catch:{ Exception -> 0x08e9, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x070d  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x070d  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ce A:{Catch:{ Exception -> 0x0948, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02d6 A:{Catch:{ Exception -> 0x0275, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02bc A:{SYNTHETIC, Splitter:B:169:0x02bc} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x02df A:{Catch:{ Exception -> 0x0275, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0300 A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x02ff A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x030d A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x030a A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x031e A:{SYNTHETIC, Splitter:B:190:0x031e} */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0339 A:{SYNTHETIC, Splitter:B:201:0x0339} */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x037d A:{SYNTHETIC, Splitter:B:221:0x037d} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x036f A:{SYNTHETIC, Splitter:B:217:0x036f} */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x03b9  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0397 A:{SYNTHETIC, Splitter:B:231:0x0397} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ce A:{Catch:{ Exception -> 0x0948, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02bc A:{SYNTHETIC, Splitter:B:169:0x02bc} */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02d6 A:{Catch:{ Exception -> 0x0275, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x02df A:{Catch:{ Exception -> 0x0275, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x02ff A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0300 A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x030a A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x030d A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x031e A:{SYNTHETIC, Splitter:B:190:0x031e} */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0339 A:{SYNTHETIC, Splitter:B:201:0x0339} */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x036f A:{SYNTHETIC, Splitter:B:217:0x036f} */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x037d A:{SYNTHETIC, Splitter:B:221:0x037d} */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0397 A:{SYNTHETIC, Splitter:B:231:0x0397} */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x03b9  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ce A:{Catch:{ Exception -> 0x0948, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02d6 A:{Catch:{ Exception -> 0x0275, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02bc A:{SYNTHETIC, Splitter:B:169:0x02bc} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x02df A:{Catch:{ Exception -> 0x0275, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0300 A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x02ff A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x030d A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x030a A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x031e A:{SYNTHETIC, Splitter:B:190:0x031e} */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0339 A:{SYNTHETIC, Splitter:B:201:0x0339} */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x037d A:{SYNTHETIC, Splitter:B:221:0x037d} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x036f A:{SYNTHETIC, Splitter:B:217:0x036f} */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x03b9  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0397 A:{SYNTHETIC, Splitter:B:231:0x0397} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ce A:{Catch:{ Exception -> 0x0948, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02bc A:{SYNTHETIC, Splitter:B:169:0x02bc} */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02d6 A:{Catch:{ Exception -> 0x0275, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x02df A:{Catch:{ Exception -> 0x0275, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x02ff A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0300 A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x030a A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x030d A:{Catch:{ Exception -> 0x0994, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x031e A:{SYNTHETIC, Splitter:B:190:0x031e} */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0339 A:{SYNTHETIC, Splitter:B:201:0x0339} */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x036f A:{SYNTHETIC, Splitter:B:217:0x036f} */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x037d A:{SYNTHETIC, Splitter:B:221:0x037d} */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0397 A:{SYNTHETIC, Splitter:B:231:0x0397} */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x03b9  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03ce A:{Catch:{ Exception -> 0x0948, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x0a97  */
    /* JADX WARNING: Removed duplicated region for block: B:624:0x0a9c A:{SYNTHETIC, Splitter:B:624:0x0a9c} */
    /* JADX WARNING: Removed duplicated region for block: B:630:0x0aa9  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0a4a  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0a4f A:{SYNTHETIC, Splitter:B:609:0x0a4f} */
    /* JADX WARNING: Removed duplicated region for block: B:615:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0a4a  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0a4f A:{SYNTHETIC, Splitter:B:609:0x0a4f} */
    /* JADX WARNING: Removed duplicated region for block: B:615:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x0a97  */
    /* JADX WARNING: Removed duplicated region for block: B:624:0x0a9c A:{SYNTHETIC, Splitter:B:624:0x0a9c} */
    /* JADX WARNING: Removed duplicated region for block: B:630:0x0aa9  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0a4a  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0a4f A:{SYNTHETIC, Splitter:B:609:0x0a4f} */
    /* JADX WARNING: Removed duplicated region for block: B:615:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x0a97  */
    /* JADX WARNING: Removed duplicated region for block: B:624:0x0a9c A:{SYNTHETIC, Splitter:B:624:0x0a9c} */
    /* JADX WARNING: Removed duplicated region for block: B:630:0x0aa9  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0a4a  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0a4f A:{SYNTHETIC, Splitter:B:609:0x0a4f} */
    /* JADX WARNING: Removed duplicated region for block: B:615:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:589:0x0a0b A:{Splitter:B:45:0x00f8, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0a4a  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0a4f A:{SYNTHETIC, Splitter:B:609:0x0a4f} */
    /* JADX WARNING: Removed duplicated region for block: B:615:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x0a97  */
    /* JADX WARNING: Removed duplicated region for block: B:624:0x0a9c A:{SYNTHETIC, Splitter:B:624:0x0a9c} */
    /* JADX WARNING: Removed duplicated region for block: B:630:0x0aa9  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a07 A:{Splitter:B:125:0x0229, ExcHandler: all (th java.lang.Throwable), PHI: r13 } */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x09b5 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x09ba A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x09bf A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x09c7 A:{Catch:{ Exception -> 0x09d3, all -> 0x0a07 }} */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x09dd A:{SYNTHETIC, Splitter:B:579:0x09dd} */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x09ea  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x0135, B:125:0x0229] */
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
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:67:0x013a, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:68:0x013b, code skipped:
            r13 = r5;
            r32 = r8;
            r31 = r9;
     */
    /* JADX WARNING: Missing block: B:77:0x0157, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:78:0x0158, code skipped:
            r1 = r0;
            r7 = r13;
            r34 = r14;
     */
    /* JADX WARNING: Missing block: B:149:0x0275, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:206:0x0347, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:207:0x0348, code skipped:
            r5 = r1;
            r59 = r8;
            r4 = null;
            r23 = null;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:208:0x0351, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:209:0x0352, code skipped:
            r1 = r0;
            r59 = r8;
            r4 = null;
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:219:0x0375, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:220:0x0376, code skipped:
            r1 = r0;
            r59 = r8;
            r5 = r17;
     */
    /* JADX WARNING: Missing block: B:237:0x03b1, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:238:0x03b2, code skipped:
            r1 = r0;
            r59 = r8;
            r5 = r17;
     */
    /* JADX WARNING: Missing block: B:275:0x0459, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:276:0x045a, code skipped:
            r1 = r0;
            r59 = r8;
     */
    /* JADX WARNING: Missing block: B:285:0x0474, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:286:0x0475, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:297:0x048b, code skipped:
            if (r4.presentationTimeUs < r18) goto L_0x048d;
     */
    /* JADX WARNING: Missing block: B:306:0x04d0, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:307:0x04d1, code skipped:
            r1 = r0;
            r23 = r17;
     */
    /* JADX WARNING: Missing block: B:312:0x0508, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:313:0x050a, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:314:0x050b, code skipped:
            r59 = r8;
     */
    /* JADX WARNING: Missing block: B:315:0x050d, code skipped:
            r68 = r17;
            r17 = r23;
            r64 = r37;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:316:0x0514, code skipped:
            r4 = r64;
     */
    /* JADX WARNING: Missing block: B:317:0x0518, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:318:0x0519, code skipped:
            r64 = r4;
     */
    /* JADX WARNING: Missing block: B:330:0x0558, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:331:0x0559, code skipped:
            r1 = r0;
            r4 = r5;
     */
    /* JADX WARNING: Missing block: B:335:0x0565, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:336:0x0566, code skipped:
            r5 = r4;
     */
    /* JADX WARNING: Missing block: B:337:0x0567, code skipped:
            r59 = r8;
            r68 = r17;
            r17 = r23;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:343:0x059d, code skipped:
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
    /* JADX WARNING: Missing block: B:406:0x06d0, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:408:0x06d8, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:409:0x06d9, code skipped:
            r73 = r5;
     */
    /* JADX WARNING: Missing block: B:410:0x06db, code skipped:
            r1 = r0;
            r23 = r17;
            r5 = r68;
            r4 = r73;
     */
    /* JADX WARNING: Missing block: B:438:0x0769, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:439:0x076a, code skipped:
            r1 = r0;
            r4 = r8;
     */
    /* JADX WARNING: Missing block: B:447:0x0778, code skipped:
            if (r2.size != 0) goto L_0x077a;
     */
    /* JADX WARNING: Missing block: B:485:0x0801, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:488:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r3 = 1;
     */
    /* JADX WARNING: Missing block: B:499:0x0824, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:523:0x08c1, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:527:0x08e9, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:528:0x08ea, code skipped:
            r4 = r17;
            r7 = r68;
            r8 = r73;
     */
    /* JADX WARNING: Missing block: B:533:0x0931, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:534:0x0933, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:535:0x0934, code skipped:
            r8 = r5;
     */
    /* JADX WARNING: Missing block: B:536:0x0935, code skipped:
            r4 = r17;
     */
    /* JADX WARNING: Missing block: B:537:0x0937, code skipped:
            r7 = r68;
     */
    /* JADX WARNING: Missing block: B:539:0x0948, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:540:0x0949, code skipped:
            r59 = r8;
            r7 = r17;
            r8 = r4;
            r4 = r23;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:541:0x0952, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:542:0x0953, code skipped:
            r59 = r8;
            r7 = r17;
            r8 = r4;
            r4 = r5;
     */
    /* JADX WARNING: Missing block: B:543:0x0959, code skipped:
            r1 = r0;
            r23 = r4;
     */
    /* JADX WARNING: Missing block: B:544:0x095c, code skipped:
            r5 = r7;
            r4 = r8;
     */
    /* JADX WARNING: Missing block: B:545:0x0960, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:546:0x0961, code skipped:
            r59 = r8;
            r35 = null;
            r8 = r4;
            r1 = r0;
            r5 = r17;
     */
    /* JADX WARNING: Missing block: B:547:0x096b, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:548:0x096c, code skipped:
            r7 = r5;
            r59 = r8;
            r35 = null;
            r8 = r4;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:549:0x0973, code skipped:
            r23 = r35;
     */
    /* JADX WARNING: Missing block: B:550:0x0976, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:551:0x0977, code skipped:
            r7 = r5;
            r59 = r8;
            r1 = r0;
            r4 = null;
            r23 = r4;
     */
    /* JADX WARNING: Missing block: B:552:0x0982, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:553:0x0983, code skipped:
            r59 = r8;
            r35 = null;
     */
    /* JADX WARNING: Missing block: B:554:0x0988, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:555:0x0989, code skipped:
            r35 = null;
            r59 = r8;
     */
    /* JADX WARNING: Missing block: B:556:0x098d, code skipped:
            r1 = r0;
            r4 = r35;
            r5 = r4;
            r23 = r5;
     */
    /* JADX WARNING: Missing block: B:557:0x0994, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:558:0x0995, code skipped:
            r59 = r8;
     */
    /* JADX WARNING: Missing block: B:575:0x09d3, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:587:0x0a07, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:589:0x0a0b, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:590:0x0a0c, code skipped:
            r13 = r5;
     */
    /* JADX WARNING: Missing block: B:591:0x0a0d, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:607:0x0a4a, code skipped:
            r7.release();
     */
    /* JADX WARNING: Missing block: B:610:?, code skipped:
            r15.finishMovie();
     */
    /* JADX WARNING: Missing block: B:611:0x0a53, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:612:0x0a54, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:615:0x0a5c, code skipped:
            r1 = new java.lang.StringBuilder();
            r1.append("time = ");
            r1.append(java.lang.System.currentTimeMillis() - r29);
            org.telegram.messenger.FileLog.d(r1.toString());
     */
    /* JADX WARNING: Missing block: B:622:0x0a97, code skipped:
            r13.release();
     */
    /* JADX WARNING: Missing block: B:625:?, code skipped:
            r15.finishMovie();
     */
    /* JADX WARNING: Missing block: B:626:0x0aa0, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:627:0x0aa1, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:630:0x0aa9, code skipped:
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
        if (r13 == 0) goto L_0x0ae4;
    L_0x0006:
        r1 = r13.videoEditedInfo;
        if (r1 != 0) goto L_0x000c;
    L_0x000a:
        goto L_0x0ae4;
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
        r20 = r1;
        r1 = "videoconvert";
        r27 = r8;
        r8 = 0;
        r9 = r4.getSharedPreferences(r1, r8);
        r1 = new java.io.File;
        r1.<init>(r2);
        r4 = r75.getId();
        r21 = r7;
        r7 = "isPreviousOk";
        if (r4 == 0) goto L_0x00cb;
    L_0x0097:
        r4 = 1;
        r22 = r9.getBoolean(r7, r4);
        r4 = r9.edit();
        r4 = r4.putBoolean(r7, r8);
        r4.commit();
        r1 = r1.canRead();
        if (r1 == 0) goto L_0x00af;
    L_0x00ad:
        if (r22 != 0) goto L_0x00cb;
    L_0x00af:
        r4 = 1;
        r5 = 0;
        r8 = 1;
        r1 = r74;
        r2 = r75;
        r3 = r14;
        r10 = r7;
        r7 = r8;
        r1.didWriteData(r2, r3, r4, r5, r7);
        r1 = r9.edit();
        r4 = 1;
        r1 = r1.putBoolean(r10, r4);
        r1.commit();
    L_0x00c9:
        r1 = 0;
        return r1;
    L_0x00cb:
        r8 = r7;
        r4 = 1;
        r12.videoConvertFirstWrite = r4;
        r29 = java.lang.System.currentTimeMillis();
        if (r10 == 0) goto L_0x0ac4;
    L_0x00d5:
        if (r11 == 0) goto L_0x0ac4;
    L_0x00d7:
        r4 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r4.<init>();	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r1 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r1.<init>();	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r1.setCacheFile(r14);	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r1.setRotation(r5);	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r1.setSize(r10, r11);	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r5 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r5.<init>();	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r15 = r5.createMovie(r1, r15);	 Catch:{ Exception -> 0x0a38, all -> 0x0a30 }
        r5 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0a23, all -> 0x0a1b }
        r5.<init>();	 Catch:{ Exception -> 0x0a23, all -> 0x0a1b }
        r5.setDataSource(r2);	 Catch:{ Exception -> 0x0a10, all -> 0x0a0b }
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x0a10, all -> 0x0a0b }
        r2 = -1;
        if (r10 != r6) goto L_0x0141;
    L_0x0101:
        r1 = r21;
        if (r11 != r1) goto L_0x0141;
    L_0x0105:
        if (r3 != 0) goto L_0x0141;
    L_0x0107:
        r1 = r13.videoEditedInfo;	 Catch:{ Exception -> 0x013a, all -> 0x0a0b }
        r1 = r1.roundVideo;	 Catch:{ Exception -> 0x013a, all -> 0x0a0b }
        if (r1 != 0) goto L_0x0141;
    L_0x010d:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x013a, all -> 0x0a0b }
        r6 = 18;
        if (r1 < r6) goto L_0x011a;
    L_0x0113:
        r21 = -1;
        r1 = (r27 > r21 ? 1 : (r27 == r21 ? 0 : -1));
        if (r1 == 0) goto L_0x011a;
    L_0x0119:
        goto L_0x0141;
    L_0x011a:
        r1 = r20;
        if (r1 == r2) goto L_0x0120;
    L_0x011e:
        r11 = 1;
        goto L_0x0121;
    L_0x0120:
        r11 = 0;
    L_0x0121:
        r1 = r74;
        r2 = r75;
        r3 = r5;
        r6 = r4;
        r4 = r15;
        r10 = r5;
        r5 = r6;
        r6 = r27;
        r13 = r8;
        r31 = r9;
        r8 = r18;
        r32 = r13;
        r13 = r10;
        r10 = r14;
        r1.readAndWriteTracks(r2, r3, r4, r5, r6, r8, r10, r11);	 Catch:{ Exception -> 0x0157, all -> 0x0a07 }
        goto L_0x09d5;
    L_0x013a:
        r0 = move-exception;
        r13 = r5;
        r32 = r8;
        r31 = r9;
        goto L_0x0158;
    L_0x0141:
        r6 = r4;
        r13 = r5;
        r32 = r8;
        r31 = r9;
        r1 = r20;
        r4 = 0;
        r8 = r12.findTrack(r13, r4);	 Catch:{ Exception -> 0x0a09, all -> 0x0a07 }
        if (r1 == r2) goto L_0x015e;
    L_0x0150:
        r4 = 1;
        r5 = r12.findTrack(r13, r4);	 Catch:{ Exception -> 0x0157, all -> 0x0a07 }
        r9 = r5;
        goto L_0x015f;
    L_0x0157:
        r0 = move-exception;
    L_0x0158:
        r1 = r0;
        r7 = r13;
        r34 = r14;
        goto L_0x0a45;
    L_0x015e:
        r9 = -1;
    L_0x015f:
        if (r8 < 0) goto L_0x09d5;
    L_0x0161:
        r20 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0998, all -> 0x0a07 }
        r4 = r20.toLowerCase();	 Catch:{ Exception -> 0x0998, all -> 0x0a07 }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0998, all -> 0x0a07 }
        r7 = "video/avc";
        r33 = 4;
        r2 = 18;
        if (r5 >= r2) goto L_0x0220;
    L_0x0171:
        r2 = selectCodec(r7);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r5 = selectColorFormat(r2, r7);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        if (r5 == 0) goto L_0x020b;
    L_0x017b:
        r34 = r5;
        r5 = r2.getName();	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r12 = "OMX.qcom.";
        r12 = r5.contains(r12);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        if (r12 == 0) goto L_0x01a5;
    L_0x0189:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r12 = 16;
        if (r5 != r12) goto L_0x01a2;
    L_0x018f:
        r5 = "lge";
        r5 = r4.equals(r5);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        if (r5 != 0) goto L_0x019f;
    L_0x0197:
        r5 = "nokia";
        r5 = r4.equals(r5);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        if (r5 == 0) goto L_0x01a2;
    L_0x019f:
        r5 = 1;
    L_0x01a0:
        r12 = 1;
        goto L_0x01cf;
    L_0x01a2:
        r5 = 1;
    L_0x01a3:
        r12 = 0;
        goto L_0x01cf;
    L_0x01a5:
        r12 = "OMX.Intel.";
        r12 = r5.contains(r12);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        if (r12 == 0) goto L_0x01af;
    L_0x01ad:
        r5 = 2;
        goto L_0x01a3;
    L_0x01af:
        r12 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r12 = r5.equals(r12);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        if (r12 == 0) goto L_0x01b9;
    L_0x01b7:
        r5 = 3;
        goto L_0x01a3;
    L_0x01b9:
        r12 = "OMX.SEC.AVC.Encoder";
        r12 = r5.equals(r12);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        if (r12 == 0) goto L_0x01c3;
    L_0x01c1:
        r5 = 4;
        goto L_0x01a0;
    L_0x01c3:
        r12 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r5 = r5.equals(r12);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        if (r5 == 0) goto L_0x01cd;
    L_0x01cb:
        r5 = 5;
        goto L_0x01a3;
    L_0x01cd:
        r5 = 0;
        goto L_0x01a3;
    L_0x01cf:
        r35 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        if (r35 == 0) goto L_0x0202;
    L_0x01d3:
        r35 = r5;
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r5.<init>();	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r36 = r12;
        r12 = "codec = ";
        r5.append(r12);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r2 = r2.getName();	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r5.append(r2);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r2 = " manufacturer = ";
        r5.append(r2);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r5.append(r4);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r2 = "device = ";
        r5.append(r2);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r2 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r5.append(r2);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r2 = r5.toString();	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        goto L_0x0206;
    L_0x0202:
        r35 = r5;
        r36 = r12;
    L_0x0206:
        r12 = r34;
        r2 = r35;
        goto L_0x0229;
    L_0x020b:
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        r2 = "no supported color format";
        r1.<init>(r2);	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
        throw r1;	 Catch:{ Exception -> 0x0213, all -> 0x0a07 }
    L_0x0213:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
        r34 = r14;
    L_0x0219:
        r4 = 0;
        r5 = 0;
        r14 = 0;
    L_0x021c:
        r23 = 0;
        goto L_0x09a6;
    L_0x0220:
        r2 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r2 = 0;
        r12 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r36 = 0;
    L_0x0229:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0998, all -> 0x0a07 }
        if (r5 == 0) goto L_0x024b;
    L_0x022d:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0244, all -> 0x0a07 }
        r5.<init>();	 Catch:{ Exception -> 0x0244, all -> 0x0a07 }
        r34 = r14;
        r14 = "colorFormat = ";
        r5.append(r14);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r5.append(r12);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        org.telegram.messenger.FileLog.d(r5);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        goto L_0x024d;
    L_0x0244:
        r0 = move-exception;
        r34 = r14;
    L_0x0247:
        r1 = r0;
        r59 = r8;
        goto L_0x0219;
    L_0x024b:
        r34 = r14;
    L_0x024d:
        r5 = r10 * r11;
        r14 = r5 * 3;
        r25 = 2;
        r14 = r14 / 2;
        if (r2 != 0) goto L_0x0277;
    L_0x0257:
        r2 = r11 % 16;
        if (r2 == 0) goto L_0x0271;
    L_0x025b:
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
    L_0x026c:
        r44 = r14;
        r5 = 3;
    L_0x026f:
        r14 = r2;
        goto L_0x02b3;
    L_0x0271:
        r35 = r6;
    L_0x0273:
        r5 = 3;
        goto L_0x02b0;
    L_0x0275:
        r0 = move-exception;
        goto L_0x0247;
    L_0x0277:
        r35 = r6;
        r6 = 1;
        if (r2 != r6) goto L_0x028f;
    L_0x027c:
        r2 = r4.toLowerCase();	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r4 = "lge";
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        if (r2 != 0) goto L_0x0273;
    L_0x0288:
        r2 = r5 + 2047;
        r2 = r2 & -2048;
        r2 = r2 - r5;
        r14 = r14 + r2;
        goto L_0x026c;
    L_0x028f:
        r5 = 5;
        if (r2 != r5) goto L_0x0293;
    L_0x0292:
        goto L_0x0273;
    L_0x0293:
        r5 = 3;
        if (r2 != r5) goto L_0x02b0;
    L_0x0296:
        r2 = "baidu";
        r2 = r4.equals(r2);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        if (r2 == 0) goto L_0x02b0;
    L_0x029e:
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
        goto L_0x026f;
    L_0x02b0:
        r44 = r14;
        r14 = 0;
    L_0x02b3:
        r13.selectTrack(r8);	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
        r2 = r13.getTrackFormat(r8);	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
        if (r9 < 0) goto L_0x02d6;
    L_0x02bc:
        r13.selectTrack(r9);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r4 = r13.getTrackFormat(r9);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r5 = "max-input-size";
        r5 = r4.getInteger(r5);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r5 = java.nio.ByteBuffer.allocateDirect(r5);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r6 = 1;
        r4 = r15.addTrack(r4, r6);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r6 = r4;
        r37 = r5;
        goto L_0x02d9;
    L_0x02d6:
        r6 = -5;
        r37 = 0;
    L_0x02d9:
        r4 = 0;
        r38 = (r27 > r4 ? 1 : (r27 == r4 ? 0 : -1));
        if (r38 <= 0) goto L_0x02ea;
    L_0x02df:
        r4 = r27;
        r27 = r14;
        r14 = 0;
        r13.seekTo(r4, r14);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r40 = r4;
        goto L_0x02f2;
    L_0x02ea:
        r40 = r27;
        r27 = r14;
        r14 = 0;
        r13.seekTo(r4, r14);	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
    L_0x02f2:
        r4 = android.media.MediaFormat.createVideoFormat(r7, r10, r11);	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
        r5 = "color-format";
        r4.setInteger(r5, r12);	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
        r5 = "bitrate";
        if (r1 <= 0) goto L_0x0300;
    L_0x02ff:
        goto L_0x0303;
    L_0x0300:
        r1 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
    L_0x0303:
        r4.setInteger(r5, r1);	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
        r1 = "frame-rate";
        if (r17 == 0) goto L_0x030d;
    L_0x030a:
        r5 = r17;
        goto L_0x030f;
    L_0x030d:
        r5 = 25;
    L_0x030f:
        r4.setInteger(r1, r5);	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
        r1 = "i-frame-interval";
        r5 = 2;
        r4.setInteger(r1, r5);	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
        r14 = 18;
        if (r1 >= r14) goto L_0x032a;
    L_0x031e:
        r1 = "stride";
        r14 = r10 + 32;
        r4.setInteger(r1, r14);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
        r1 = "slice-height";
        r4.setInteger(r1, r11);	 Catch:{ Exception -> 0x0275, all -> 0x0a07 }
    L_0x032a:
        r14 = android.media.MediaCodec.createEncoderByType(r7);	 Catch:{ Exception -> 0x0994, all -> 0x0a07 }
        r1 = 0;
        r5 = 1;
        r14.configure(r4, r1, r1, r5);	 Catch:{ Exception -> 0x0988, all -> 0x0a07 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0982, all -> 0x0a07 }
        r4 = 18;
        if (r1 < r4) goto L_0x0359;
    L_0x0339:
        r1 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x0351, all -> 0x0a07 }
        r4 = r14.createInputSurface();	 Catch:{ Exception -> 0x0351, all -> 0x0a07 }
        r1.<init>(r4);	 Catch:{ Exception -> 0x0351, all -> 0x0a07 }
        r1.makeCurrent();	 Catch:{ Exception -> 0x0347, all -> 0x0a07 }
        r5 = r1;
        goto L_0x035a;
    L_0x0347:
        r0 = move-exception;
        r5 = r1;
        r59 = r8;
        r4 = 0;
        r23 = 0;
        r1 = r0;
        goto L_0x09a6;
    L_0x0351:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
        r4 = 0;
        r5 = 0;
        goto L_0x021c;
    L_0x0359:
        r5 = 0;
    L_0x035a:
        r14.start();	 Catch:{ Exception -> 0x0976, all -> 0x0a07 }
        r1 = "mime";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0976, all -> 0x0a07 }
        r4 = android.media.MediaCodec.createDecoderByType(r1);	 Catch:{ Exception -> 0x0976, all -> 0x0a07 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x096b, all -> 0x0a07 }
        r17 = r5;
        r5 = 18;
        if (r1 < r5) goto L_0x037d;
    L_0x036f:
        r1 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0375, all -> 0x0a07 }
        r1.<init>();	 Catch:{ Exception -> 0x0375, all -> 0x0a07 }
        goto L_0x0382;
    L_0x0375:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
        r5 = r17;
        goto L_0x021c;
    L_0x037d:
        r1 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0960, all -> 0x0a07 }
        r1.<init>(r10, r11, r3);	 Catch:{ Exception -> 0x0960, all -> 0x0a07 }
    L_0x0382:
        r5 = r1;
        r1 = r5.getSurface();	 Catch:{ Exception -> 0x0952, all -> 0x0a07 }
        r23 = r5;
        r3 = 0;
        r5 = 0;
        r4.configure(r2, r1, r3, r5);	 Catch:{ Exception -> 0x0948, all -> 0x0a07 }
        r4.start();	 Catch:{ Exception -> 0x0948, all -> 0x0a07 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0948, all -> 0x0a07 }
        r5 = 21;
        if (r1 >= r5) goto L_0x03b9;
    L_0x0397:
        r1 = r4.getInputBuffers();	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        r2 = r14.getOutputBuffers();	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        r5 = 18;
        if (r3 >= r5) goto L_0x03ae;
    L_0x03a5:
        r3 = r14.getInputBuffers();	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        r52 = r1;
        r53 = r3;
        goto L_0x03be;
    L_0x03ae:
        r52 = r1;
        goto L_0x03bc;
    L_0x03b1:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
        r5 = r17;
        goto L_0x09a6;
    L_0x03b9:
        r2 = 0;
        r52 = 0;
    L_0x03bc:
        r53 = 0;
    L_0x03be:
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x0948, all -> 0x0a07 }
        r54 = r2;
        r1 = 0;
        r21 = 0;
        r22 = 0;
        r43 = -5;
        r55 = -1;
    L_0x03cc:
        if (r21 != 0) goto L_0x093a;
    L_0x03ce:
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x0948, all -> 0x0a07 }
        r2 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        if (r1 != 0) goto L_0x0572;
    L_0x03d5:
        r5 = r13.getSampleTrackIndex();	 Catch:{ Exception -> 0x0565, all -> 0x0a07 }
        if (r5 != r8) goto L_0x0436;
    L_0x03db:
        r5 = r4.dequeueInputBuffer(r2);	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        if (r5 < 0) goto L_0x0418;
    L_0x03e1:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        r3 = 21;
        if (r2 >= r3) goto L_0x03ea;
    L_0x03e7:
        r2 = r52[r5];	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        goto L_0x03ee;
    L_0x03ea:
        r2 = r4.getInputBuffer(r5);	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
    L_0x03ee:
        r3 = 0;
        r48 = r13.readSampleData(r2, r3);	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        if (r48 >= 0) goto L_0x0406;
    L_0x03f5:
        r47 = 0;
        r48 = 0;
        r49 = 0;
        r51 = 4;
        r45 = r4;
        r46 = r5;
        r45.queueInputBuffer(r46, r47, r48, r49, r51);	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        r1 = 1;
        goto L_0x0418;
    L_0x0406:
        r47 = 0;
        r49 = r13.getSampleTime();	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        r51 = 0;
        r45 = r4;
        r46 = r5;
        r45.queueInputBuffer(r46, r47, r48, r49, r51);	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
        r13.advance();	 Catch:{ Exception -> 0x03b1, all -> 0x0a07 }
    L_0x0418:
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
        r23 = r1;
        goto L_0x053c;
    L_0x0436:
        r2 = -1;
        if (r9 == r2) goto L_0x051c;
    L_0x0439:
        if (r5 != r9) goto L_0x051c;
    L_0x043b:
        r3 = r37;
        r2 = 0;
        r5 = r13.readSampleData(r3, r2);	 Catch:{ Exception -> 0x0518, all -> 0x0a07 }
        r37 = r4;
        r4 = r35;
        r4.size = r5;	 Catch:{ Exception -> 0x050a, all -> 0x0a07 }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x050a, all -> 0x0a07 }
        r35 = r7;
        r7 = 21;
        if (r5 >= r7) goto L_0x0463;
    L_0x0450:
        r3.position(r2);	 Catch:{ Exception -> 0x0459, all -> 0x0a07 }
        r2 = r4.size;	 Catch:{ Exception -> 0x0459, all -> 0x0a07 }
        r3.limit(r2);	 Catch:{ Exception -> 0x0459, all -> 0x0a07 }
        goto L_0x0463;
    L_0x0459:
        r0 = move-exception;
        r1 = r0;
        r59 = r8;
    L_0x045d:
        r5 = r17;
        r4 = r37;
        goto L_0x09a6;
    L_0x0463:
        r2 = r4.size;	 Catch:{ Exception -> 0x050a, all -> 0x0a07 }
        if (r2 < 0) goto L_0x0477;
    L_0x0467:
        r59 = r8;
        r7 = r13.getSampleTime();	 Catch:{ Exception -> 0x0474, all -> 0x0a07 }
        r4.presentationTimeUs = r7;	 Catch:{ Exception -> 0x0474, all -> 0x0a07 }
        r13.advance();	 Catch:{ Exception -> 0x0474, all -> 0x0a07 }
        r8 = r1;
        goto L_0x047d;
    L_0x0474:
        r0 = move-exception;
        r1 = r0;
        goto L_0x045d;
    L_0x0477:
        r59 = r8;
        r1 = 0;
        r4.size = r1;	 Catch:{ Exception -> 0x0508, all -> 0x0a07 }
        r8 = 1;
    L_0x047d:
        r1 = r4.size;	 Catch:{ Exception -> 0x0508, all -> 0x0a07 }
        if (r1 <= 0) goto L_0x04ec;
    L_0x0481:
        r1 = 0;
        r5 = (r18 > r1 ? 1 : (r18 == r1 ? 0 : -1));
        if (r5 < 0) goto L_0x048d;
    L_0x0487:
        r1 = r4.presentationTimeUs;	 Catch:{ Exception -> 0x0474, all -> 0x0a07 }
        r5 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1));
        if (r5 >= 0) goto L_0x04ec;
    L_0x048d:
        r1 = 0;
        r4.offset = r1;	 Catch:{ Exception -> 0x0508, all -> 0x0a07 }
        r2 = r13.getSampleFlags();	 Catch:{ Exception -> 0x0508, all -> 0x0a07 }
        r4.flags = r2;	 Catch:{ Exception -> 0x0508, all -> 0x0a07 }
        r45 = r15.writeSampleData(r6, r3, r4, r1);	 Catch:{ Exception -> 0x0508, all -> 0x0a07 }
        r38 = 0;
        r1 = (r45 > r38 ? 1 : (r45 == r38 ? 0 : -1));
        if (r1 == 0) goto L_0x04d5;
    L_0x04a0:
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
        r1.didWriteData(r2, r3, r4, r5, r7);	 Catch:{ Exception -> 0x04d0, all -> 0x0a07 }
        goto L_0x0505;
    L_0x04d0:
        r0 = move-exception;
        r1 = r0;
        r23 = r17;
        goto L_0x0514;
    L_0x04d5:
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
        goto L_0x0502;
    L_0x04ec:
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
    L_0x0502:
        r23 = r8;
        r8 = 3;
    L_0x0505:
        r1 = 0;
        r7 = -1;
        goto L_0x053d;
    L_0x0508:
        r0 = move-exception;
        goto L_0x050d;
    L_0x050a:
        r0 = move-exception;
        r59 = r8;
    L_0x050d:
        r68 = r17;
        r17 = r23;
        r64 = r37;
        r1 = r0;
    L_0x0514:
        r4 = r64;
        goto L_0x056e;
    L_0x0518:
        r0 = move-exception;
        r64 = r4;
        goto L_0x0567;
    L_0x051c:
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
        r23 = r1;
        if (r5 != r7) goto L_0x053c;
    L_0x053a:
        r1 = 1;
        goto L_0x053d;
    L_0x053c:
        r1 = 0;
    L_0x053d:
        if (r1 == 0) goto L_0x055e;
    L_0x053f:
        r5 = r64;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r46 = r5.dequeueInputBuffer(r3);	 Catch:{ Exception -> 0x0558, all -> 0x0a07 }
        if (r46 < 0) goto L_0x0562;
    L_0x0549:
        r47 = 0;
        r48 = 0;
        r49 = 0;
        r51 = 4;
        r45 = r5;
        r45.queueInputBuffer(r46, r47, r48, r49, r51);	 Catch:{ Exception -> 0x0558, all -> 0x0a07 }
        r1 = 1;
        goto L_0x058c;
    L_0x0558:
        r0 = move-exception;
        r1 = r0;
        r4 = r5;
    L_0x055b:
        r23 = r17;
        goto L_0x056e;
    L_0x055e:
        r5 = r64;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x0562:
        r1 = r23;
        goto L_0x058c;
    L_0x0565:
        r0 = move-exception;
        r5 = r4;
    L_0x0567:
        r59 = r8;
        r68 = r17;
        r17 = r23;
        r1 = r0;
    L_0x056e:
        r5 = r68;
        goto L_0x09a6;
    L_0x0572:
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
    L_0x058c:
        r2 = r22 ^ 1;
        r45 = r2;
        r23 = r22;
        r6 = r43;
        r20 = 1;
        r22 = r1;
    L_0x0598:
        if (r45 != 0) goto L_0x05b6;
    L_0x059a:
        if (r20 == 0) goto L_0x059d;
    L_0x059c:
        goto L_0x05b6;
    L_0x059d:
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
        goto L_0x03cc;
    L_0x05b6:
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x0933, all -> 0x0a07 }
        r2 = r67;
        r1 = r14.dequeueOutputBuffer(r2, r3);	 Catch:{ Exception -> 0x0933, all -> 0x0a07 }
        if (r1 != r7) goto L_0x05d2;
    L_0x05c1:
        r3 = r1;
        r73 = r5;
        r5 = r6;
        r61 = r9;
        r4 = r21;
        r1 = r70;
        r6 = 3;
        r7 = 0;
    L_0x05cd:
        r9 = -1;
        r60 = 21;
        goto L_0x070b;
    L_0x05d2:
        r3 = -3;
        if (r1 != r3) goto L_0x05ed;
    L_0x05d5:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0558, all -> 0x0a07 }
        r4 = 21;
        if (r3 >= r4) goto L_0x05df;
    L_0x05db:
        r54 = r14.getOutputBuffers();	 Catch:{ Exception -> 0x0558, all -> 0x0a07 }
    L_0x05df:
        r3 = r1;
        r73 = r5;
        r5 = r6;
        r61 = r9;
        r7 = r20;
        r4 = r21;
        r1 = r70;
        r6 = 3;
        goto L_0x05cd;
    L_0x05ed:
        r4 = 21;
        r3 = -2;
        if (r1 != r3) goto L_0x05ff;
    L_0x05f2:
        r3 = r14.getOutputFormat();	 Catch:{ Exception -> 0x0558, all -> 0x0a07 }
        r8 = -5;
        if (r6 != r8) goto L_0x05df;
    L_0x05f9:
        r7 = 0;
        r6 = r15.addTrack(r3, r7);	 Catch:{ Exception -> 0x0558, all -> 0x0a07 }
        goto L_0x05df;
    L_0x05ff:
        r8 = -5;
        if (r1 < 0) goto L_0x0914;
    L_0x0602:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0933, all -> 0x0a07 }
        if (r3 >= r4) goto L_0x0609;
    L_0x0606:
        r3 = r54[r1];	 Catch:{ Exception -> 0x0558, all -> 0x0a07 }
        goto L_0x060d;
    L_0x0609:
        r3 = r14.getOutputBuffer(r1);	 Catch:{ Exception -> 0x0933, all -> 0x0a07 }
    L_0x060d:
        if (r3 == 0) goto L_0x08f2;
    L_0x060f:
        r7 = r2.size;	 Catch:{ Exception -> 0x0933, all -> 0x0a07 }
        r4 = 1;
        if (r7 <= r4) goto L_0x06e4;
    L_0x0614:
        r7 = r2.flags;	 Catch:{ Exception -> 0x06d8, all -> 0x0a07 }
        r7 = r7 & 2;
        if (r7 != 0) goto L_0x064c;
    L_0x061a:
        r25 = r15.writeSampleData(r6, r3, r2, r4);	 Catch:{ Exception -> 0x06d8, all -> 0x0a07 }
        r3 = (r25 > r65 ? 1 : (r25 == r65 ? 0 : -1));
        if (r3 == 0) goto L_0x063e;
    L_0x0622:
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
        r1.didWriteData(r2, r3, r4, r5, r7);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        goto L_0x06d2;
    L_0x063e:
        r72 = r1;
        r71 = r2;
        r73 = r5;
        r8 = r6;
        r61 = r9;
        r9 = -1;
        r60 = 21;
        goto L_0x06d2;
    L_0x064c:
        r72 = r1;
        r71 = r2;
        r73 = r5;
        r8 = r6;
        r61 = r9;
        r1 = -5;
        r9 = -1;
        r60 = 21;
        if (r8 != r1) goto L_0x06d2;
    L_0x065b:
        r2 = r71;
        r4 = r2.size;	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r4 = new byte[r4];	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r5 = r2.offset;	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r6 = r2.size;	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r5 = r5 + r6;
        r3.limit(r5);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r5 = r2.offset;	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r3.position(r5);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r3.get(r4);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r3 = r2.size;	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r5 = 1;
        r3 = r3 - r5;
    L_0x0675:
        r6 = 3;
        if (r3 < 0) goto L_0x06b3;
    L_0x0678:
        if (r3 <= r6) goto L_0x06b3;
    L_0x067a:
        r7 = r4[r3];	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        if (r7 != r5) goto L_0x06ae;
    L_0x067e:
        r5 = r3 + -1;
        r5 = r4[r5];	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        if (r5 != 0) goto L_0x06ae;
    L_0x0684:
        r5 = r3 + -2;
        r5 = r4[r5];	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        if (r5 != 0) goto L_0x06ae;
    L_0x068a:
        r5 = r3 + -3;
        r7 = r4[r5];	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        if (r7 != 0) goto L_0x06ae;
    L_0x0690:
        r7 = java.nio.ByteBuffer.allocate(r5);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r3 = r2.size;	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r3 = r3 - r5;
        r3 = java.nio.ByteBuffer.allocate(r3);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r8 = 0;
        r1 = r7.put(r4, r8, r5);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r1.position(r8);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r1 = r2.size;	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r1 = r1 - r5;
        r1 = r3.put(r4, r5, r1);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r1.position(r8);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        goto L_0x06b6;
    L_0x06ae:
        r3 = r3 + -1;
        r1 = -5;
        r5 = 1;
        goto L_0x0675;
    L_0x06b3:
        r3 = r35;
        r7 = r3;
    L_0x06b6:
        r1 = r70;
        r4 = android.media.MediaFormat.createVideoFormat(r1, r10, r11);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        if (r7 == 0) goto L_0x06ca;
    L_0x06be:
        if (r3 == 0) goto L_0x06ca;
    L_0x06c0:
        r5 = "csd-0";
        r4.setByteBuffer(r5, r7);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        r5 = "csd-1";
        r4.setByteBuffer(r5, r3);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
    L_0x06ca:
        r3 = 0;
        r4 = r15.addTrack(r4, r3);	 Catch:{ Exception -> 0x06d0, all -> 0x0a07 }
        goto L_0x06f2;
    L_0x06d0:
        r0 = move-exception;
        goto L_0x06db;
    L_0x06d2:
        r1 = r70;
        r2 = r71;
        r6 = 3;
        goto L_0x06f1;
    L_0x06d8:
        r0 = move-exception;
        r73 = r5;
    L_0x06db:
        r1 = r0;
        r23 = r17;
        r5 = r68;
        r4 = r73;
        goto L_0x09a6;
    L_0x06e4:
        r72 = r1;
        r73 = r5;
        r8 = r6;
        r61 = r9;
        r1 = r70;
        r6 = 3;
        r9 = -1;
        r60 = 21;
    L_0x06f1:
        r4 = r8;
    L_0x06f2:
        r3 = r2.flags;	 Catch:{ Exception -> 0x08e9, all -> 0x0a07 }
        r3 = r3 & 4;
        if (r3 == 0) goto L_0x06fe;
    L_0x06f8:
        r3 = r72;
        r5 = 0;
        r21 = 1;
        goto L_0x0703;
    L_0x06fe:
        r3 = r72;
        r5 = 0;
        r21 = 0;
    L_0x0703:
        r14.releaseOutputBuffer(r3, r5);	 Catch:{ Exception -> 0x08e9, all -> 0x0a07 }
        r5 = r4;
        r7 = r20;
        r4 = r21;
    L_0x070b:
        if (r3 == r9) goto L_0x0720;
    L_0x070d:
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
        goto L_0x0598;
    L_0x0720:
        if (r23 != 0) goto L_0x08c4;
    L_0x0722:
        r64 = r7;
        r8 = r73;
        r6 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r3 = r8.dequeueOutputBuffer(r2, r6);	 Catch:{ Exception -> 0x08c1, all -> 0x0a07 }
        if (r3 != r9) goto L_0x073b;
    L_0x072e:
        r69 = r4;
        r9 = r5;
        r4 = r17;
        r70 = r62;
        r7 = r68;
    L_0x0737:
        r45 = 0;
        goto L_0x08d1;
    L_0x073b:
        r9 = -3;
        if (r3 != r9) goto L_0x0749;
    L_0x073e:
        r69 = r4;
        r9 = r5;
        r4 = r17;
        r70 = r62;
        r7 = r68;
        goto L_0x08d1;
    L_0x0749:
        r9 = -2;
        if (r3 != r9) goto L_0x076e;
    L_0x074c:
        r3 = r8.getOutputFormat();	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        if (r9 == 0) goto L_0x073e;
    L_0x0754:
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r9.<init>();	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r6 = "newFormat = ";
        r9.append(r6);	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r9.append(r3);	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r3 = r9.toString();	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        goto L_0x073e;
    L_0x0769:
        r0 = move-exception;
        r1 = r0;
        r4 = r8;
        goto L_0x055b;
    L_0x076e:
        if (r3 < 0) goto L_0x08a6;
    L_0x0770:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x08c1, all -> 0x0a07 }
        r7 = 18;
        if (r6 < r7) goto L_0x077e;
    L_0x0776:
        r6 = r2.size;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        if (r6 == 0) goto L_0x077c;
    L_0x077a:
        r6 = 1;
        goto L_0x0789;
    L_0x077c:
        r6 = 0;
        goto L_0x0789;
    L_0x077e:
        r6 = r2.size;	 Catch:{ Exception -> 0x08c1, all -> 0x0a07 }
        if (r6 != 0) goto L_0x077a;
    L_0x0782:
        r6 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r9 = (r6 > r65 ? 1 : (r6 == r65 ? 0 : -1));
        if (r9 == 0) goto L_0x077c;
    L_0x0788:
        goto L_0x077a;
    L_0x0789:
        r7 = (r18 > r65 ? 1 : (r18 == r65 ? 0 : -1));
        if (r7 <= 0) goto L_0x07a3;
    L_0x078d:
        r7 = r4;
        r9 = r5;
        r4 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r20 = (r4 > r18 ? 1 : (r4 == r18 ? 0 : -1));
        if (r20 < 0) goto L_0x07a5;
    L_0x0795:
        r4 = r2.flags;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r4 = r4 | 4;
        r2.flags = r4;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r4 = r62;
        r6 = 0;
        r62 = 1;
        r63 = 1;
        goto L_0x07ab;
    L_0x07a3:
        r7 = r4;
        r9 = r5;
    L_0x07a5:
        r4 = r62;
        r62 = r22;
        r63 = r23;
    L_0x07ab:
        r20 = (r4 > r65 ? 1 : (r4 == r65 ? 0 : -1));
        if (r20 <= 0) goto L_0x07ef;
    L_0x07af:
        r20 = -1;
        r22 = (r55 > r20 ? 1 : (r55 == r20 ? 0 : -1));
        if (r22 != 0) goto L_0x07ef;
    L_0x07b5:
        r20 = r6;
        r69 = r7;
        r6 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r21 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r21 >= 0) goto L_0x07e8;
    L_0x07bf:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        if (r6 == 0) goto L_0x07e4;
    L_0x07c3:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r6.<init>();	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r7 = "drop frame startTime = ";
        r6.append(r7);	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r6.append(r4);	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r7 = " present time = ";
        r6.append(r7);	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r70 = r4;
        r4 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r6.append(r4);	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r4 = r6.toString();	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        goto L_0x07e6;
    L_0x07e4:
        r70 = r4;
    L_0x07e6:
        r4 = 0;
        goto L_0x07f7;
    L_0x07e8:
        r70 = r4;
        r4 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0769, all -> 0x0a07 }
        r55 = r4;
        goto L_0x07f5;
    L_0x07ef:
        r70 = r4;
        r20 = r6;
        r69 = r7;
    L_0x07f5:
        r4 = r20;
    L_0x07f7:
        r8.releaseOutputBuffer(r3, r4);	 Catch:{ Exception -> 0x08c1, all -> 0x0a07 }
        if (r4 == 0) goto L_0x0867;
    L_0x07fc:
        r17.awaitNewImage();	 Catch:{ Exception -> 0x0801, all -> 0x0a07 }
        r3 = 0;
        goto L_0x0807;
    L_0x0801:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);	 Catch:{ Exception -> 0x08c1, all -> 0x0a07 }
        r3 = 1;
    L_0x0807:
        if (r3 != 0) goto L_0x0867;
    L_0x0809:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x08c1, all -> 0x0a07 }
        r4 = 18;
        if (r3 < r4) goto L_0x0827;
    L_0x080f:
        r4 = r17;
        r3 = 0;
        r4.drawImage(r3);	 Catch:{ Exception -> 0x0824, all -> 0x0a07 }
        r5 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0824, all -> 0x0a07 }
        r20 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 * r20;
        r7 = r68;
        r7.setPresentationTime(r5);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r7.swapBuffers();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        goto L_0x086b;
    L_0x0824:
        r0 = move-exception;
        goto L_0x0937;
    L_0x0827:
        r4 = r17;
        r7 = r68;
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r38 = r14.dequeueInputBuffer(r5);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        if (r38 < 0) goto L_0x085d;
    L_0x0833:
        r3 = 1;
        r4.drawImage(r3);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r20 = r4.getFrame();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r21 = r53[r38];	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r21.clear();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r22 = r12;
        r23 = r10;
        r24 = r11;
        r25 = r27;
        r26 = r36;
        org.telegram.messenger.Utilities.convertVideoFrame(r20, r21, r22, r23, r24, r25, r26);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r39 = 0;
        r5 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r43 = 0;
        r37 = r14;
        r40 = r44;
        r41 = r5;
        r37.queueInputBuffer(r38, r39, r40, r41, r43);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        goto L_0x086b;
    L_0x085d:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        if (r3 == 0) goto L_0x086b;
    L_0x0861:
        r3 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        goto L_0x086b;
    L_0x0867:
        r4 = r17;
        r7 = r68;
    L_0x086b:
        r3 = r2.flags;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r3 = r3 & 4;
        if (r3 == 0) goto L_0x08a1;
    L_0x0871:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        if (r3 == 0) goto L_0x087a;
    L_0x0875:
        r3 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
    L_0x087a:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r5 = 18;
        if (r3 < r5) goto L_0x0884;
    L_0x0880:
        r14.signalEndOfInputStream();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        goto L_0x089b;
    L_0x0884:
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r46 = r14.dequeueInputBuffer(r5);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        if (r46 < 0) goto L_0x089b;
    L_0x088c:
        r47 = 0;
        r48 = 1;
        r5 = r2.presentationTimeUs;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r51 = 4;
        r45 = r14;
        r49 = r5;
        r45.queueInputBuffer(r46, r47, r48, r49, r51);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
    L_0x089b:
        r22 = r62;
        r23 = r63;
        goto L_0x0737;
    L_0x08a1:
        r22 = r62;
        r23 = r63;
        goto L_0x08d1;
    L_0x08a6:
        r4 = r17;
        r7 = r68;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2.<init>();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r5 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r2.append(r5);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2.append(r3);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        throw r1;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
    L_0x08c1:
        r0 = move-exception;
        goto L_0x0935;
    L_0x08c4:
        r69 = r4;
        r9 = r5;
        r64 = r7;
        r4 = r17;
        r70 = r62;
        r7 = r68;
        r8 = r73;
    L_0x08d1:
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
        goto L_0x0598;
    L_0x08e9:
        r0 = move-exception;
        r4 = r17;
        r7 = r68;
        r8 = r73;
        goto L_0x0959;
    L_0x08f2:
        r3 = r1;
        r8 = r5;
        r4 = r17;
        r7 = r68;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2.<init>();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r5 = "encoderOutputBuffer ";
        r2.append(r5);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2.append(r3);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r3 = " was null";
        r2.append(r3);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        throw r1;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
    L_0x0914:
        r3 = r1;
        r8 = r5;
        r4 = r17;
        r7 = r68;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2.<init>();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r5 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r2.append(r5);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2.append(r3);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
        throw r1;	 Catch:{ Exception -> 0x0931, all -> 0x0a07 }
    L_0x0931:
        r0 = move-exception;
        goto L_0x0959;
    L_0x0933:
        r0 = move-exception;
        r8 = r5;
    L_0x0935:
        r4 = r17;
    L_0x0937:
        r7 = r68;
        goto L_0x0959;
    L_0x093a:
        r59 = r8;
        r7 = r17;
        r8 = r4;
        r4 = r23;
        r5 = r7;
        r1 = r59;
        r16 = 0;
        goto L_0x09b0;
    L_0x0948:
        r0 = move-exception;
        r59 = r8;
        r7 = r17;
        r8 = r4;
        r4 = r23;
        r1 = r0;
        goto L_0x095c;
    L_0x0952:
        r0 = move-exception;
        r59 = r8;
        r7 = r17;
        r8 = r4;
        r4 = r5;
    L_0x0959:
        r1 = r0;
        r23 = r4;
    L_0x095c:
        r5 = r7;
        r4 = r8;
        goto L_0x09a6;
    L_0x0960:
        r0 = move-exception;
        r59 = r8;
        r7 = r17;
        r35 = 0;
        r8 = r4;
        r1 = r0;
        r5 = r7;
        goto L_0x0973;
    L_0x096b:
        r0 = move-exception;
        r7 = r5;
        r59 = r8;
        r35 = 0;
        r8 = r4;
        r1 = r0;
    L_0x0973:
        r23 = r35;
        goto L_0x09a6;
    L_0x0976:
        r0 = move-exception;
        r7 = r5;
        r59 = r8;
        r35 = 0;
        r1 = r0;
        r4 = r35;
        r23 = r4;
        goto L_0x09a6;
    L_0x0982:
        r0 = move-exception;
        r59 = r8;
        r35 = 0;
        goto L_0x098d;
    L_0x0988:
        r0 = move-exception;
        r35 = r1;
        r59 = r8;
    L_0x098d:
        r1 = r0;
        r4 = r35;
        r5 = r4;
        r23 = r5;
        goto L_0x09a6;
    L_0x0994:
        r0 = move-exception;
        r59 = r8;
        goto L_0x099d;
    L_0x0998:
        r0 = move-exception;
        r59 = r8;
        r34 = r14;
    L_0x099d:
        r35 = 0;
        r1 = r0;
        r4 = r35;
        r5 = r4;
        r14 = r5;
        r23 = r14;
    L_0x09a6:
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x09d3, all -> 0x0a07 }
        r8 = r4;
        r4 = r23;
        r1 = r59;
        r16 = 1;
    L_0x09b0:
        r13.unselectTrack(r1);	 Catch:{ Exception -> 0x09d3, all -> 0x0a07 }
        if (r4 == 0) goto L_0x09b8;
    L_0x09b5:
        r4.release();	 Catch:{ Exception -> 0x09d3, all -> 0x0a07 }
    L_0x09b8:
        if (r5 == 0) goto L_0x09bd;
    L_0x09ba:
        r5.release();	 Catch:{ Exception -> 0x09d3, all -> 0x0a07 }
    L_0x09bd:
        if (r8 == 0) goto L_0x09c5;
    L_0x09bf:
        r8.stop();	 Catch:{ Exception -> 0x09d3, all -> 0x0a07 }
        r8.release();	 Catch:{ Exception -> 0x09d3, all -> 0x0a07 }
    L_0x09c5:
        if (r14 == 0) goto L_0x09cd;
    L_0x09c7:
        r14.stop();	 Catch:{ Exception -> 0x09d3, all -> 0x0a07 }
        r14.release();	 Catch:{ Exception -> 0x09d3, all -> 0x0a07 }
    L_0x09cd:
        r74.checkConversionCanceled();	 Catch:{ Exception -> 0x09d3, all -> 0x0a07 }
        r14 = r16;
        goto L_0x09d8;
    L_0x09d3:
        r0 = move-exception;
        goto L_0x0a18;
    L_0x09d5:
        r34 = r14;
        r14 = 0;
    L_0x09d8:
        r13.release();
        if (r15 == 0) goto L_0x09e6;
    L_0x09dd:
        r15.finishMovie();	 Catch:{ Exception -> 0x09e1 }
        goto L_0x09e6;
    L_0x09e1:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x09e6:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0a04;
    L_0x09ea:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "time = ";
        r1.append(r2);
        r2 = java.lang.System.currentTimeMillis();
        r2 = r2 - r29;
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0a04:
        r7 = r14;
        goto L_0x0a77;
    L_0x0a07:
        r0 = move-exception;
        goto L_0x0a0d;
    L_0x0a09:
        r0 = move-exception;
        goto L_0x0a16;
    L_0x0a0b:
        r0 = move-exception;
        r13 = r5;
    L_0x0a0d:
        r1 = r0;
        goto L_0x0a95;
    L_0x0a10:
        r0 = move-exception;
        r13 = r5;
        r32 = r8;
        r31 = r9;
    L_0x0a16:
        r34 = r14;
    L_0x0a18:
        r1 = r0;
        r7 = r13;
        goto L_0x0a45;
    L_0x0a1b:
        r0 = move-exception;
        r35 = 0;
        r1 = r0;
        r13 = r35;
        goto L_0x0a95;
    L_0x0a23:
        r0 = move-exception;
        r32 = r8;
        r31 = r9;
        r34 = r14;
        r35 = 0;
        r1 = r0;
        r7 = r35;
        goto L_0x0a45;
    L_0x0a30:
        r0 = move-exception;
        r35 = 0;
        r1 = r0;
        r13 = r35;
        r15 = r13;
        goto L_0x0a95;
    L_0x0a38:
        r0 = move-exception;
        r32 = r8;
        r31 = r9;
        r34 = r14;
        r35 = 0;
        r1 = r0;
        r7 = r35;
        r15 = r7;
    L_0x0a45:
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0a92 }
        if (r7 == 0) goto L_0x0a4d;
    L_0x0a4a:
        r7.release();
    L_0x0a4d:
        if (r15 == 0) goto L_0x0a58;
    L_0x0a4f:
        r15.finishMovie();	 Catch:{ Exception -> 0x0a53 }
        goto L_0x0a58;
    L_0x0a53:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0a58:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0a76;
    L_0x0a5c:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "time = ";
        r1.append(r2);
        r2 = java.lang.System.currentTimeMillis();
        r2 = r2 - r29;
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0a76:
        r7 = 1;
    L_0x0a77:
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
    L_0x0a92:
        r0 = move-exception;
        r1 = r0;
        r13 = r7;
    L_0x0a95:
        if (r13 == 0) goto L_0x0a9a;
    L_0x0a97:
        r13.release();
    L_0x0a9a:
        if (r15 == 0) goto L_0x0aa5;
    L_0x0a9c:
        r15.finishMovie();	 Catch:{ Exception -> 0x0aa0 }
        goto L_0x0aa5;
    L_0x0aa0:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0aa5:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0ac3;
    L_0x0aa9:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "time = ";
        r2.append(r3);
        r3 = java.lang.System.currentTimeMillis();
        r3 = r3 - r29;
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x0ac3:
        throw r1;
    L_0x0ac4:
        r2 = r8;
        r31 = r9;
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
        goto L_0x00c9;
    L_0x0ae4:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MessageObject):boolean");
    }
}
