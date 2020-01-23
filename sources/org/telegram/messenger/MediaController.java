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
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
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
import java.net.URLEncoder;
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
    public static final String AUIDO_MIME_TYPE = "audio/mp4a-latm";
    private static volatile MediaController Instance = null;
    public static final int VIDEO_BITRATE_1080 = 6800000;
    public static final int VIDEO_BITRATE_360 = 750000;
    public static final int VIDEO_BITRATE_480 = 1000000;
    public static final int VIDEO_BITRATE_720 = 2621440;
    public static final String VIDEO_MIME_TYPE = "video/avc";
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
    OnAudioFocusChangeListener audioRecordFocusChangedListener = new -$$Lambda$MediaController$ZT9BGfAVJxxizLGGBCyNY3k7LiI(this);
    private AudioRecord audioRecorder;
    private Activity baseActivity;
    private boolean callInProgress;
    private int countLess;
    private AspectRatioFrameLayout currentAspectRatioFrameLayout;
    private float currentAspectRatioFrameLayoutRatio;
    private boolean currentAspectRatioFrameLayoutReady;
    private int currentAspectRatioFrameLayoutRotation;
    private float currentMusicPlaybackSpeed = 1.0f;
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
    private boolean hasRecordAudioFocus;
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
    private long lastSaveTime;
    private EncryptedChat lastSecretChat;
    private long lastTimestamp = 0;
    private User lastUser;
    private float[] linearAcceleration = new float[3];
    private Sensor linearSensor;
    private String[] mediaProjections;
    private PipRoundVideoView pipRoundVideoView;
    private int pipSwitchingState;
    private boolean playMusicAgain;
    private int playerNum;
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
    private String shouldSavePositionForCurrentAudio;
    private ArrayList<MessageObject> shuffledPlaylist = new ArrayList();
    private int startObserverToken;
    private StopMediaObserverRunnable stopMediaObserverRunnable;
    private final Object sync = new Object();
    private long timeSinceRaise;
    private boolean useFrontSpeaker;
    private ArrayList<VideoConvertMessage> videoConvertQueue = new ArrayList();
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
        public int height;
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
        public long size;
        public ArrayList<InputDocument> stickers = new ArrayList();
        public String thumbPath;
        public int ttl;
        public int width;

        public PhotoEntry(int i, int i2, long j, String str, int i3, boolean z, int i4, int i5, long j2) {
            this.bucketId = i;
            this.imageId = i2;
            this.dateTaken = j;
            this.path = str;
            this.width = i4;
            this.height = i5;
            this.size = j2;
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

    private class VideoConvertMessage {
        public int currentAccount = this.messageObject.currentAccount;
        public MessageObject messageObject;
        public VideoEditedInfo videoEditedInfo;

        public VideoConvertMessage(MessageObject messageObject, VideoEditedInfo videoEditedInfo) {
            this.messageObject = messageObject;
            this.videoEditedInfo = videoEditedInfo;
        }
    }

    private static class VideoConvertRunnable implements Runnable {
        private VideoConvertMessage convertMessage;

        private VideoConvertRunnable(VideoConvertMessage videoConvertMessage) {
            this.convertMessage = videoConvertMessage;
        }

        public void run() {
            MediaController.getInstance().convertVideo(this.convertMessage);
        }

        public static void runConversion(VideoConvertMessage videoConvertMessage) {
            new Thread(new -$$Lambda$MediaController$VideoConvertRunnable$1idm9QEQAyeKKRBr0GlKKvYUXr4(videoConvertMessage)).start();
        }

        static /* synthetic */ void lambda$runConversion$0(VideoConvertMessage videoConvertMessage) {
            try {
                Thread thread = new Thread(new VideoConvertRunnable(videoConvertMessage), "VideoConvertRunnable");
                thread.start();
                thread.join();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public interface VideoConvertorListener {
        boolean checkConversionCanceled();

        void didWriteData(long j, float f);
    }

    private static int getVideoBitrateWithFactor(float f) {
        return (int) (((f * 2000.0f) * 1000.0f) * 1.13f);
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
        String[] strArr = new String[9];
        String str = "_id";
        strArr[0] = str;
        String str2 = "bucket_id";
        strArr[1] = str2;
        String str3 = "bucket_display_name";
        strArr[2] = str3;
        String str4 = "_data";
        strArr[3] = str4;
        String str5 = "date_modified";
        String str6 = "datetaken";
        strArr[4] = VERSION.SDK_INT > 28 ? str5 : str6;
        strArr[5] = "orientation";
        strArr[6] = "width";
        strArr[7] = "height";
        strArr[8] = "_size";
        projectionPhotos = strArr;
        String[] strArr2 = new String[9];
        strArr2[0] = str;
        strArr2[1] = str2;
        strArr2[2] = str3;
        strArr2[3] = str4;
        if (VERSION.SDK_INT <= 28) {
            str5 = str6;
        }
        strArr2[4] = str5;
        strArr2[5] = "duration";
        strArr2[6] = "width";
        strArr2[7] = "height";
        strArr2[8] = "_size";
        projectionVideo = strArr2;
    }

    public /* synthetic */ void lambda$new$0$MediaController(int i) {
        if (i != 1) {
            this.hasRecordAudioFocus = false;
        }
    }

    public static void checkGallery() {
        if (VERSION.SDK_INT >= 24) {
            AlbumEntry albumEntry = allPhotosAlbumEntry;
            if (albumEntry != null) {
                Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$4uH3EizVAYrBgH-5VEe7LRHjets(albumEntry.photos.size()), 2000);
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
    static /* synthetic */ void lambda$checkGallery$1(int r13) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$checkGallery$1(int):void");
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
        this.recordQueue.postRunnable(new -$$Lambda$MediaController$FaCBRlQgZxu6gGs04ETW1GD6ZDk(this));
        Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$Jq_ZASoLiwPvRrenXbD34k0cp8A(this));
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$-DNTraRPU1olMcDZHLwy4AP-aJw(this));
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

    public /* synthetic */ void lambda$new$2$MediaController() {
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

    public /* synthetic */ void lambda$new$3$MediaController() {
        try {
            this.currentPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("playbackSpeed", 1.0f);
            this.currentMusicPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("musicPlaybackSpeed", 1.0f);
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
                            mediaController.lambda$startAudioAgain$6$MediaController(mediaController.playingMessageObject);
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

    public /* synthetic */ void lambda$new$4$MediaController() {
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
                lambda$startAudioAgain$6$MediaController(this.playingMessageObject);
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
                lambda$startAudioAgain$6$MediaController(this.playingMessageObject);
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
                        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$4$qUbIyuVGbhalvPYfdd7YaV8EXS8(this, messageObject));
                    }
                }

                public /* synthetic */ void lambda$run$1$MediaController$4(MessageObject messageObject) {
                    if (!(messageObject == null || ((MediaController.this.audioPlayer == null && MediaController.this.videoPlayer == null) || MediaController.this.isPaused))) {
                        try {
                            long duration;
                            long currentPosition;
                            float f;
                            float bufferedPosition;
                            if (MediaController.this.videoPlayer != null) {
                                duration = MediaController.this.videoPlayer.getDuration();
                                currentPosition = MediaController.this.videoPlayer.getCurrentPosition();
                                if (currentPosition >= 0) {
                                    if (duration > 0) {
                                        f = (float) duration;
                                        bufferedPosition = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / f;
                                        float f2 = duration >= 0 ? ((float) currentPosition) / f : 0.0f;
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
                            if (bufferedPosition >= 0.0f && MediaController.this.shouldSavePositionForCurrentAudio != null && SystemClock.elapsedRealtime() - MediaController.this.lastSaveTime >= 1000) {
                                MediaController.this.shouldSavePositionForCurrentAudio;
                                MediaController.this.lastSaveTime = SystemClock.elapsedRealtime();
                                Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$4$_1d-mI3DSqZHNDnGss_mjwhPhjQ(this, bufferedPosition));
                            }
                            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(messageObject.getId()), Float.valueOf(bufferedPosition));
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                }

                public /* synthetic */ void lambda$null$0$MediaController$4(float f) {
                    ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit().putFloat(MediaController.this.shouldSavePositionForCurrentAudio, f).commit();
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
        r1 = new org.telegram.messenger.-$$Lambda$MediaController$vtFn9TLGRgqEGYcE0tVJigcL1_Q;	 Catch:{ Exception -> 0x00ca }
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

    public /* synthetic */ void lambda$processMediaObserver$5$MediaController(ArrayList arrayList) {
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
                getInstance().lambda$startAudioAgain$6$MediaController(getInstance().getPlayingMessageObject());
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
                    if (!(this.playingMessageObject == null || ApplicationLoader.mainInterfacePaused || ((!this.playingMessageObject.isVoice() && !this.playingMessageObject.isRoundVideo()) || this.useFrontSpeaker || NotificationsController.audioManager.isWiredHeadsetOn()))) {
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
            if (chatActivity != null && this.allowStartRecord && SharedConfig.raiseToSpeak) {
                this.raiseToEarRecord = true;
                startRecording(chatActivity.getCurrentAccount(), this.raiseChat.getDialogId(), null, this.raiseChat.getClassGuid());
                this.ignoreOnPause = true;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x007b  */
    private void startAudioAgain(boolean r8) {
        /*
        r7 = this;
        r0 = r7.playingMessageObject;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r0.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r1 = org.telegram.messenger.NotificationCenter.audioRouteChanged;
        r2 = 1;
        r3 = new java.lang.Object[r2];
        r4 = r7.useFrontSpeaker;
        r4 = java.lang.Boolean.valueOf(r4);
        r5 = 0;
        r3[r5] = r4;
        r0.postNotificationName(r1, r3);
        r0 = r7.videoPlayer;
        if (r0 == 0) goto L_0x004a;
    L_0x0020:
        r1 = r7.useFrontSpeaker;
        if (r1 == 0) goto L_0x0025;
    L_0x0024:
        goto L_0x0026;
    L_0x0025:
        r5 = 3;
    L_0x0026:
        r0.setStreamType(r5);
        if (r8 != 0) goto L_0x0044;
    L_0x002b:
        r8 = r7.videoPlayer;
        r0 = r8.getCurrentPosition();
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r8 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r8 >= 0) goto L_0x003e;
    L_0x0037:
        r8 = r7.videoPlayer;
        r0 = 0;
        r8.seekTo(r0);
    L_0x003e:
        r8 = r7.videoPlayer;
        r8.play();
        goto L_0x008b;
    L_0x0044:
        r8 = r7.playingMessageObject;
        r7.lambda$startAudioAgain$6$MediaController(r8);
        goto L_0x008b;
    L_0x004a:
        r0 = r7.audioPlayer;
        if (r0 == 0) goto L_0x0050;
    L_0x004e:
        r0 = 1;
        goto L_0x0051;
    L_0x0050:
        r0 = 0;
    L_0x0051:
        r1 = r7.playingMessageObject;
        r3 = r1.audioProgress;
        r4 = r1.audioPlayerDuration;
        if (r8 != 0) goto L_0x0071;
    L_0x0059:
        r6 = r7.audioPlayer;
        if (r6 == 0) goto L_0x0071;
    L_0x005d:
        r6 = r6.isPlaying();
        if (r6 == 0) goto L_0x0071;
    L_0x0063:
        r4 = (float) r4;
        r4 = r4 * r3;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 <= 0) goto L_0x006d;
    L_0x006c:
        goto L_0x0071;
    L_0x006d:
        r3 = 0;
        r1.audioProgress = r3;
        goto L_0x0073;
    L_0x0071:
        r1.audioProgress = r3;
    L_0x0073:
        r7.cleanupPlayer(r5, r2);
        r7.playMessage(r1);
        if (r8 == 0) goto L_0x008b;
    L_0x007b:
        if (r0 == 0) goto L_0x0088;
    L_0x007d:
        r8 = new org.telegram.messenger.-$$Lambda$MediaController$2ilNP-Y2X6vBiQTog9zw4C4IJA0;
        r8.<init>(r7, r1);
        r0 = 100;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r0);
        goto L_0x008b;
    L_0x0088:
        r7.lambda$startAudioAgain$6$MediaController(r1);
    L_0x008b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.startAudioAgain(boolean):void");
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
                Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$o3ZUglQtLh9sJP5KoXzuD4HTmvQ(this));
                this.sensorsStarted = true;
            }
        }
    }

    public /* synthetic */ void lambda$startRaiseToEarSensors$7$MediaController() {
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
            Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$Pk3hOyheAhTCFxb5ULl84sURJDM(this));
            if (this.proximityHasDifferentValues) {
                WakeLock wakeLock = this.proximityWakeLock;
                if (wakeLock != null && wakeLock.isHeld()) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    public /* synthetic */ void lambda$stopRaiseToEarSensors$8$MediaController() {
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
        WakeLock wakeLock = this.proximityWakeLock;
        if (!(wakeLock == null || !wakeLock.isHeld() || this.proximityTouched)) {
            this.proximityWakeLock.release();
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
                        this.playingMessageObject.audioProgress = f;
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
                        this.pipRoundVideoView.show(this.baseActivity, new -$$Lambda$MediaController$oLUc7US38bnTKt3tP0jeXETk0_s(this));
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

    public /* synthetic */ void lambda$setCurrentVideoVisible$9$MediaController() {
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

    public void setPlaybackSpeed(boolean z, float f) {
        if (z) {
            this.currentMusicPlaybackSpeed = f;
        } else {
            this.currentPlaybackSpeed = f;
        }
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer != null) {
            videoPlayer.setPlaybackSpeed(f);
        } else {
            videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                videoPlayer.setPlaybackSpeed(f);
            }
        }
        MessagesController.getGlobalMainSettings().edit().putFloat(z ? "musicPlaybackSpeed" : "playbackSpeed", f).commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.messagePlayingSpeedChanged, new Object[0]);
    }

    public float getPlaybackSpeed(boolean z) {
        return z ? this.currentMusicPlaybackSpeed : this.currentPlaybackSpeed;
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

    public void injectVideoPlayer(VideoPlayer videoPlayer, MessageObject messageObject) {
        if (videoPlayer != null && messageObject != null) {
            FileLoader.getInstance(messageObject.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
            this.playerWasReady = false;
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.videoPlayer = videoPlayer;
            this.playingMessageObject = messageObject;
            final int i = this.playerNum + 1;
            this.playerNum = i;
            final MessageObject messageObject2 = messageObject;
            this.videoPlayer.setDelegate(new VideoPlayerDelegate(null, true) {
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }

                public void onStateChanged(boolean z, int i) {
                    if (i == MediaController.this.playerNum) {
                        MediaController.this.updateVideoState(messageObject2, null, true, z, i);
                    }
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

    public boolean playMessage(MessageObject messageObject) {
        Object obj;
        VideoPlayer videoPlayer;
        final MessageObject messageObject2 = messageObject;
        Integer valueOf = Integer.valueOf(0);
        if (messageObject2 == null) {
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
        File file;
        File file2;
        int fileReference;
        if (!messageObject.isOut() && messageObject.isContentUnread()) {
            MessagesController.getInstance(messageObject2.currentAccount).markMessageContentAsRead(messageObject2);
        }
        boolean z = this.playMusicAgain;
        boolean z2 = z ^ 1;
        MessageObject messageObject3 = this.playingMessageObject;
        if (messageObject3 != null) {
            if (!z) {
                messageObject3.resetPlayingProgress();
            }
            z2 = false;
        }
        cleanupPlayer(z2, false);
        this.shouldSavePositionForCurrentAudio = null;
        this.lastSaveTime = 0;
        this.playMusicAgain = false;
        this.seekToProgressPending = 0.0f;
        String str = messageObject2.messageOwner.attachPath;
        if (str == null || str.length() <= 0) {
            file = null;
            z2 = false;
        } else {
            file = new File(messageObject2.messageOwner.attachPath);
            z2 = file.exists();
            if (!z2) {
                file = null;
            }
        }
        if (file != null) {
            file2 = file;
        } else {
            file2 = FileLoader.getPathToMessage(messageObject2.messageOwner);
        }
        Object obj2 = (!SharedConfig.streamMedia || (!(messageObject.isMusic() || messageObject.isRoundVideo() || (messageObject.isVideo() && messageObject.canStreamVideo())) || ((int) messageObject.getDialogId()) == 0)) ? null : 1;
        if (!(file2 == null || file2 == file)) {
            z2 = file2.exists();
            if (!z2 && obj2 == null) {
                FileLoader.getInstance(messageObject2.currentAccount).loadFile(messageObject.getDocument(), messageObject2, 0, 0);
                this.downloadingCurrentMessage = true;
                this.isPaused = false;
                this.lastProgress = 0;
                this.audioInfo = null;
                this.playingMessageObject = messageObject2;
                if (this.playingMessageObject.isMusic()) {
                    try {
                        ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                } else {
                    ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                }
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
                return true;
            }
        }
        boolean z3 = z2;
        this.downloadingCurrentMessage = false;
        if (messageObject.isMusic()) {
            checkIsNextMusicFileDownloaded(messageObject2.currentAccount);
        } else {
            checkIsNextVoiceFileDownloaded(messageObject2.currentAccount);
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = this.currentAspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            this.isDrawingWasReady = false;
            aspectRatioFrameLayout.setDrawingReady(false);
        }
        z2 = messageObject.isVideo();
        String str2 = "&hash=";
        String str3 = "&id=";
        String str4 = "?account=";
        String str5 = "UTF-8";
        String str6 = "other";
        int i;
        final MessageObject messageObject4;
        PipRoundVideoView pipRoundVideoView;
        Document document;
        StringBuilder stringBuilder;
        StringBuilder stringBuilder2;
        float f;
        if (messageObject.isRoundVideo() || z2) {
            FileLoader.getInstance(messageObject2.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
            this.playerWasReady = false;
            boolean z4 = !z2 || (messageObject2.messageOwner.to_id.channel_id == 0 && messageObject2.audioProgress <= 0.1f);
            int[] iArr = (!z2 || messageObject.getDuration() > 30) ? null : new int[]{1};
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.videoPlayer = new VideoPlayer();
            i = this.playerNum + 1;
            this.playerNum = i;
            obj = valueOf;
            String str7 = str4;
            final int i2 = i;
            String str8 = str5;
            str5 = str3;
            messageObject4 = messageObject;
            String str9 = str2;
            final int[] iArr2 = iArr;
            File file3 = file2;
            final boolean z5 = z4;
            this.videoPlayer.setDelegate(new VideoPlayerDelegate() {
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }

                public void onStateChanged(boolean z, int i) {
                    if (i2 == MediaController.this.playerNum) {
                        MediaController.this.updateVideoState(messageObject4, iArr2, z5, z, i);
                    }
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
                                    MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new -$$Lambda$MediaController$6$8PAmwbvWSqlIkfrUwfiuydCYWZ0(this));
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

                public /* synthetic */ void lambda$onSurfaceDestroyed$0$MediaController$6() {
                    MediaController.this.cleanupPlayer(true, true);
                }
            });
            this.currentAspectRatioFrameLayoutReady = false;
            if (this.pipRoundVideoView == null && MessagesController.getInstance(messageObject2.currentAccount).isDialogVisible(messageObject.getDialogId(), messageObject2.scheduled)) {
                TextureView textureView = this.currentTextureView;
                if (textureView != null) {
                    this.videoPlayer.setTextureView(textureView);
                }
            } else {
                if (this.pipRoundVideoView == null) {
                    try {
                        this.pipRoundVideoView = new PipRoundVideoView();
                        this.pipRoundVideoView.show(this.baseActivity, new -$$Lambda$MediaController$wd_d47VCVcpsYhOaXoleF-gs_eQ(this));
                    } catch (Exception unused) {
                        this.pipRoundVideoView = null;
                    }
                }
                pipRoundVideoView = this.pipRoundVideoView;
                if (pipRoundVideoView != null) {
                    this.videoPlayer.setTextureView(pipRoundVideoView.getTextureView());
                }
            }
            if (z3) {
                if (!(messageObject2.mediaExists || file3 == file)) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$7XHGlNaKi1-Kl3GUSi4RIeFecPw(messageObject2, file3));
                }
                this.videoPlayer.preparePlayer(Uri.fromFile(file3), str6);
            } else {
                try {
                    fileReference = FileLoader.getInstance(messageObject2.currentAccount).getFileReference(messageObject2);
                    document = messageObject.getDocument();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str7);
                    stringBuilder.append(messageObject2.currentAccount);
                    stringBuilder.append(str5);
                    stringBuilder.append(document.id);
                    stringBuilder.append(str9);
                    stringBuilder.append(document.access_hash);
                    stringBuilder.append("&dc=");
                    stringBuilder.append(document.dc_id);
                    stringBuilder.append("&size=");
                    stringBuilder.append(document.size);
                    stringBuilder.append("&mime=");
                    str3 = str8;
                    stringBuilder.append(URLEncoder.encode(document.mime_type, str3));
                    stringBuilder.append("&rid=");
                    stringBuilder.append(fileReference);
                    stringBuilder.append("&name=");
                    stringBuilder.append(URLEncoder.encode(FileLoader.getDocumentFileName(document), str3));
                    stringBuilder.append("&reference=");
                    stringBuilder.append(Utilities.bytesToHex(document.file_reference != null ? document.file_reference : new byte[0]));
                    str = stringBuilder.toString();
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("tg://");
                    stringBuilder2.append(messageObject.getFileName());
                    stringBuilder2.append(str);
                    this.videoPlayer.preparePlayer(Uri.parse(stringBuilder2.toString()), str6);
                } catch (Exception th2) {
                    FileLog.e(th2);
                }
            }
            if (messageObject.isRoundVideo()) {
                this.videoPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                f = this.currentPlaybackSpeed;
                if (f > 1.0f) {
                    this.videoPlayer.setPlaybackSpeed(f);
                }
            } else {
                this.videoPlayer.setStreamType(3);
            }
        } else {
            pipRoundVideoView = this.pipRoundVideoView;
            if (pipRoundVideoView != null) {
                pipRoundVideoView.close(true);
                this.pipRoundVideoView = null;
            }
            try {
                this.audioPlayer = new VideoPlayer();
                i = this.playerNum + 1;
                this.playerNum = i;
                this.audioPlayer.setDelegate(new VideoPlayerDelegate() {
                    public void onError(Exception exception) {
                    }

                    public void onRenderedFirstFrame() {
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }

                    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                    }

                    public void onStateChanged(boolean z, int i) {
                        if (i == MediaController.this.playerNum) {
                            if (i == 4 || ((i == 1 || i == 2) && z && messageObject2.audioProgress >= 0.999f)) {
                                if (MediaController.this.playlist.isEmpty() || (MediaController.this.playlist.size() <= 1 && messageObject2.isVoice())) {
                                    MediaController mediaController = MediaController.this;
                                    MessageObject messageObject = messageObject2;
                                    boolean z2 = messageObject != null && messageObject.isVoice();
                                    mediaController.cleanupPlayer(true, true, z2, false);
                                } else {
                                    MediaController.this.playNextMessageWithoutOrder(true);
                                }
                            } else if (MediaController.this.seekToProgressPending != 0.0f && (i == 3 || i == 1)) {
                                long duration = (long) ((int) (((float) MediaController.this.audioPlayer.getDuration()) * MediaController.this.seekToProgressPending));
                                MediaController.this.audioPlayer.seekTo(duration);
                                MediaController.this.lastProgress = duration;
                                MediaController.this.seekToProgressPending = 0.0f;
                            }
                        }
                    }
                });
                if (z3) {
                    if (!(messageObject2.mediaExists || file2 == file)) {
                        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$E4t72zCnnsM26yjhlvXuBB6_2HQ(messageObject2, file2));
                    }
                    this.audioPlayer.preparePlayer(Uri.fromFile(file2), str6);
                } else {
                    fileReference = FileLoader.getInstance(messageObject2.currentAccount).getFileReference(messageObject2);
                    document = messageObject.getDocument();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str4);
                    stringBuilder.append(messageObject2.currentAccount);
                    stringBuilder.append(str3);
                    stringBuilder.append(document.id);
                    stringBuilder.append(str2);
                    stringBuilder.append(document.access_hash);
                    stringBuilder.append("&dc=");
                    stringBuilder.append(document.dc_id);
                    stringBuilder.append("&size=");
                    stringBuilder.append(document.size);
                    stringBuilder.append("&mime=");
                    stringBuilder.append(URLEncoder.encode(document.mime_type, str5));
                    stringBuilder.append("&rid=");
                    stringBuilder.append(fileReference);
                    stringBuilder.append("&name=");
                    stringBuilder.append(URLEncoder.encode(FileLoader.getDocumentFileName(document), str5));
                    stringBuilder.append("&reference=");
                    stringBuilder.append(Utilities.bytesToHex(document.file_reference != null ? document.file_reference : new byte[0]));
                    str = stringBuilder.toString();
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("tg://");
                    stringBuilder2.append(messageObject.getFileName());
                    stringBuilder2.append(str);
                    this.audioPlayer.preparePlayer(Uri.parse(stringBuilder2.toString()), str6);
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
                        this.audioInfo = AudioInfo.getAudioInfo(file2);
                    } catch (Exception th22) {
                        FileLog.e(th22);
                    }
                    str = messageObject.getFileName();
                    if (!TextUtils.isEmpty(str) && messageObject.getDuration() >= 1200) {
                        float f2 = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getFloat(str, -1.0f);
                        if (f2 > 0.0f && f2 < 0.999f) {
                            this.seekToProgressPending = f2;
                            messageObject2.audioProgress = f2;
                        }
                        this.shouldSavePositionForCurrentAudio = str;
                        if (this.currentMusicPlaybackSpeed > 1.0f) {
                            this.audioPlayer.setPlaybackSpeed(this.currentMusicPlaybackSpeed);
                        }
                    }
                }
                if (messageObject2.forceSeekTo >= 0.0f) {
                    f = messageObject2.forceSeekTo;
                    this.seekToProgressPending = f;
                    messageObject2.audioProgress = f;
                    messageObject2.forceSeekTo = -1.0f;
                }
                this.audioPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                this.audioPlayer.play();
                obj = valueOf;
            } catch (Exception th222) {
                FileLog.e(th222);
                NotificationCenter instance = NotificationCenter.getInstance(messageObject2.currentAccount);
                i = NotificationCenter.messagePlayingPlayStateChanged;
                Object[] objArr = new Object[1];
                messageObject4 = this.playingMessageObject;
                objArr[0] = Integer.valueOf(messageObject4 != null ? messageObject4.getId() : 0);
                instance.postNotificationName(i, objArr);
                videoPlayer = this.audioPlayer;
                if (videoPlayer != null) {
                    videoPlayer.releasePlayer(true);
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
        this.playingMessageObject = messageObject2;
        if (!SharedConfig.raiseToSpeak) {
            startRaiseToEarSensors(this.raiseChat);
        }
        if (!ApplicationLoader.mainInterfacePaused) {
            WakeLock wakeLock = this.proximityWakeLock;
            if (!(wakeLock == null || wakeLock.isHeld() || (!this.playingMessageObject.isVoice() && !this.playingMessageObject.isRoundVideo()))) {
                this.proximityWakeLock.acquire();
            }
        }
        startProgressTimer(this.playingMessageObject);
        NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidStart, messageObject2);
        videoPlayer = this.videoPlayer;
        long duration;
        if (videoPlayer != null) {
            try {
                if (this.playingMessageObject.audioProgress != 0.0f) {
                    duration = videoPlayer.getDuration();
                    if (duration == -9223372036854775807L) {
                        duration = ((long) this.playingMessageObject.getDuration()) * 1000;
                    }
                    fileReference = (int) (((float) duration) * this.playingMessageObject.audioProgress);
                    if (this.playingMessageObject.audioProgressMs != 0) {
                        fileReference = this.playingMessageObject.audioProgressMs;
                        this.playingMessageObject.audioProgressMs = 0;
                    }
                    this.videoPlayer.seekTo((long) fileReference);
                }
            } catch (Exception th2222) {
                MessageObject messageObject5 = this.playingMessageObject;
                messageObject5.audioProgress = 0.0f;
                messageObject5.audioProgressSec = 0;
                NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), obj);
                FileLog.e(th2222);
            }
            this.videoPlayer.play();
        } else {
            videoPlayer = this.audioPlayer;
            if (videoPlayer != null) {
                try {
                    if (this.playingMessageObject.audioProgress != 0.0f) {
                        duration = videoPlayer.getDuration();
                        if (duration == -9223372036854775807L) {
                            duration = ((long) this.playingMessageObject.getDuration()) * 1000;
                        }
                        this.audioPlayer.seekTo((long) ((int) (((float) duration) * this.playingMessageObject.audioProgress)));
                    }
                } catch (Exception th22222) {
                    this.playingMessageObject.resetPlayingProgress();
                    NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), obj);
                    FileLog.e(th22222);
                }
            }
        }
        MessageObject messageObject6 = this.playingMessageObject;
        if (messageObject6 == null || !messageObject6.isMusic()) {
            ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
        } else {
            try {
                ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            } catch (Throwable th222222) {
                FileLog.e(th222222);
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$playMessage$10$MediaController() {
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
    public boolean lambda$startAudioAgain$6$MediaController(MessageObject messageObject) {
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

    private boolean resumeAudio(MessageObject messageObject) {
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

    public void requestAudioFocus(boolean z) {
        if (z) {
            if (!this.hasRecordAudioFocus && NotificationsController.audioManager.requestAudioFocus(this.audioRecordFocusChangedListener, 3, 2) == 1) {
                this.hasRecordAudioFocus = true;
            }
        } else if (this.hasRecordAudioFocus) {
            NotificationsController.audioManager.abandonAudioFocus(this.audioRecordFocusChangedListener);
            this.hasRecordAudioFocus = false;
        }
    }

    public void startRecording(int i, long j, MessageObject messageObject, int i2) {
        MessageObject messageObject2 = this.playingMessageObject;
        Object obj = (messageObject2 == null || !isPlayingMessage(messageObject2) || isMessagePaused()) ? null : 1;
        requestAudioFocus(true);
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        -$$Lambda$MediaController$cD9gtk5N4Criwa1U5ICrL9b1x98 -__lambda_mediacontroller_cd9gtk5n4criwa1u5icrl9b1x98 = new -$$Lambda$MediaController$cD9gtk5N4Criwa1U5ICrL9b1x98(this, i, i2, j, messageObject);
        this.recordStartRunnable = -__lambda_mediacontroller_cd9gtk5n4criwa1u5icrl9b1x98;
        dispatchQueue.postRunnable(-__lambda_mediacontroller_cd9gtk5n4criwa1u5icrl9b1x98, obj != null ? 500 : 50);
    }

    public /* synthetic */ void lambda$startRecording$17$MediaController(int i, int i2, long j, MessageObject messageObject) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$IhaPZ6tp_jvFQYsGIvGIeQMQRcA(this, i, i2));
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$Ib5QJlgVGnTZDKjbN5E4V3c7y0c(this, i, i2));
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$T-MgBPX1crPIAHCGOIYcEn8gluY(this, i, i2));
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$PnbUDppg1FjWNHXK0EV2FGHJ8Gc(this, i, i2));
        }
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
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(i2));
    }

    public /* synthetic */ void lambda$null$16$MediaController(int i, int i2) {
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
            Utilities.globalQueue.postRunnable(new -$$Lambda$MediaController$P_7MAEz90raUUsQ0wjt9yEVVvsA(this, absolutePath, stringBuilder2, messageObject));
        }
    }

    public /* synthetic */ void lambda$generateWaveform$19$MediaController(String str, String str2, MessageObject messageObject) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$M4ZhVpUUtvVs0wE9j13RDsfzpsc(this, str2, getWaveform(str), messageObject));
    }

    public /* synthetic */ void lambda$null$18$MediaController(String str, byte[] bArr, MessageObject messageObject) {
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
            this.fileEncodingQueue.postRunnable(new -$$Lambda$MediaController$snqctalfE2yUifG9iJRofIh6-DI(this, this.recordingAudio, this.recordingAudioFile, i, z, i2));
        } else {
            File file = this.recordingAudioFile;
            if (file != null) {
                file.delete();
            }
            requestAudioFocus(false);
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

    public /* synthetic */ void lambda$stopRecordingInternal$21$MediaController(TL_document tL_document, File file, int i, boolean z, int i2) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$ZjY-i5bgGxhi9da7M9qOwNlNiY0(this, tL_document, file, i, z, i2));
    }

    public /* synthetic */ void lambda$null$20$MediaController(TL_document tL_document, File file, int i, boolean z, int i2) {
        boolean z2;
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
            z2 = false;
            objArr[0] = Integer.valueOf(this.recordingGuid);
            String str = null;
            int i6 = i;
            objArr[i4] = i6 == 2 ? tL_document : null;
            if (i6 == 2) {
                str = file.getAbsolutePath();
            }
            objArr[2] = str;
            instance.postNotificationName(i5, objArr);
        } else {
            z2 = false;
            NotificationCenter.getInstance(this.recordingCurrentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), Boolean.valueOf(false));
            file.delete();
        }
        requestAudioFocus(z2);
    }

    public void stopRecording(int i, boolean z, int i2) {
        Runnable runnable = this.recordStartRunnable;
        if (runnable != null) {
            this.recordQueue.cancelRunnable(runnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new -$$Lambda$MediaController$bhjlln11i2Wq4Tm6VYvsDNdQ31I(this, i, z, i2));
    }

    public /* synthetic */ void lambda$stopRecording$23$MediaController(int i, boolean z, int i2) {
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$UcPBoy0lgNwZcooqPzpn4kCxUyU(this, i));
        }
    }

    public /* synthetic */ void lambda$null$22$MediaController(int i) {
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

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0026  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0025 A:{RETURN} */
    public static void saveFile(java.lang.String r9, android.content.Context r10, int r11, java.lang.String r12, java.lang.String r13) {
        /*
        if (r9 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r0 = android.text.TextUtils.isEmpty(r9);
        r1 = 0;
        if (r0 != 0) goto L_0x0022;
    L_0x000a:
        r0 = new java.io.File;
        r0.<init>(r9);
        r9 = r0.exists();
        if (r9 == 0) goto L_0x0022;
    L_0x0015:
        r9 = android.net.Uri.fromFile(r0);
        r9 = org.telegram.messenger.AndroidUtilities.isInternalUri(r9);
        if (r9 == 0) goto L_0x0020;
    L_0x001f:
        goto L_0x0022;
    L_0x0020:
        r4 = r0;
        goto L_0x0023;
    L_0x0022:
        r4 = r1;
    L_0x0023:
        if (r4 != 0) goto L_0x0026;
    L_0x0025:
        return;
    L_0x0026:
        r9 = 1;
        r6 = new boolean[r9];
        r0 = 0;
        r6[r0] = r0;
        r2 = r4.exists();
        if (r2 == 0) goto L_0x0074;
    L_0x0032:
        if (r10 == 0) goto L_0x0062;
    L_0x0034:
        if (r11 == 0) goto L_0x0062;
    L_0x0036:
        r2 = new org.telegram.ui.ActionBar.AlertDialog;	 Catch:{ Exception -> 0x005e }
        r3 = 2;
        r2.<init>(r10, r3);	 Catch:{ Exception -> 0x005e }
        r10 = "Loading";
        r1 = NUM; // 0x7f0e05ee float:1.8878116E38 double:1.0531629066E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r1);	 Catch:{ Exception -> 0x005b }
        r2.setMessage(r10);	 Catch:{ Exception -> 0x005b }
        r2.setCanceledOnTouchOutside(r0);	 Catch:{ Exception -> 0x005b }
        r2.setCancelable(r9);	 Catch:{ Exception -> 0x005b }
        r9 = new org.telegram.messenger.-$$Lambda$MediaController$n0ai62a0FgAWQdkvJgV1wS-Wjrg;	 Catch:{ Exception -> 0x005b }
        r9.<init>(r6);	 Catch:{ Exception -> 0x005b }
        r2.setOnCancelListener(r9);	 Catch:{ Exception -> 0x005b }
        r2.show();	 Catch:{ Exception -> 0x005b }
        r7 = r2;
        goto L_0x0063;
    L_0x005b:
        r9 = move-exception;
        r1 = r2;
        goto L_0x005f;
    L_0x005e:
        r9 = move-exception;
    L_0x005f:
        org.telegram.messenger.FileLog.e(r9);
    L_0x0062:
        r7 = r1;
    L_0x0063:
        r9 = new java.lang.Thread;
        r10 = new org.telegram.messenger.-$$Lambda$MediaController$CcKCcTUYgkGF_15jWw7PcOpOgFI;
        r2 = r10;
        r3 = r11;
        r5 = r12;
        r8 = r13;
        r2.<init>(r3, r4, r5, r6, r7, r8);
        r9.<init>(r10);
        r9.start();
    L_0x0074:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.saveFile(java.lang.String, android.content.Context, int, java.lang.String, java.lang.String):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a3 A:{Catch:{ Exception -> 0x0014 }} */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ce A:{Catch:{ all -> 0x011a }} */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x010e A:{SYNTHETIC, Splitter:B:53:0x010e} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0113 A:{SYNTHETIC, Splitter:B:56:0x0113} */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0135 A:{Catch:{ Exception -> 0x0014 }} */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x013b A:{Catch:{ Exception -> 0x0014 }} */
    /* JADX WARNING: Removed duplicated region for block: B:101:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0171  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:67:0x0121 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:77:0x012b */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:62|63|(2:65|66)|67|68) */
    /* JADX WARNING: Missing block: B:62:0x011a, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:63:0x011b, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:64:0x011c, code skipped:
            if (r18 != null) goto L_0x011e;
     */
    /* JADX WARNING: Missing block: B:66:?, code skipped:
            r18.close();
     */
    /* JADX WARNING: Missing block: B:72:0x0124, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:73:0x0125, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:74:0x0126, code skipped:
            if (r17 != null) goto L_0x0128;
     */
    /* JADX WARNING: Missing block: B:76:?, code skipped:
            r17.close();
     */
    static /* synthetic */ void lambda$saveFile$27(int r21, java.io.File r22, java.lang.String r23, boolean[] r24, org.telegram.ui.ActionBar.AlertDialog r25, java.lang.String r26) {
        /*
        r1 = r21;
        r0 = r23;
        r2 = r25;
        r3 = 2;
        r4 = 1;
        r5 = 0;
        if (r1 != 0) goto L_0x0017;
    L_0x000b:
        r0 = org.telegram.messenger.FileLoader.getFileExtension(r22);	 Catch:{ Exception -> 0x0014 }
        r0 = org.telegram.messenger.AndroidUtilities.generatePicturePath(r5, r0);	 Catch:{ Exception -> 0x0014 }
        goto L_0x001d;
    L_0x0014:
        r0 = move-exception;
        goto L_0x016c;
    L_0x0017:
        if (r1 != r4) goto L_0x0020;
    L_0x0019:
        r0 = org.telegram.messenger.AndroidUtilities.generateVideoPath();	 Catch:{ Exception -> 0x0014 }
    L_0x001d:
        r10 = r0;
        goto L_0x009d;
    L_0x0020:
        if (r1 != r3) goto L_0x0029;
    L_0x0022:
        r6 = android.os.Environment.DIRECTORY_DOWNLOADS;	 Catch:{ Exception -> 0x0014 }
        r6 = android.os.Environment.getExternalStoragePublicDirectory(r6);	 Catch:{ Exception -> 0x0014 }
        goto L_0x002f;
    L_0x0029:
        r6 = android.os.Environment.DIRECTORY_MUSIC;	 Catch:{ Exception -> 0x0014 }
        r6 = android.os.Environment.getExternalStoragePublicDirectory(r6);	 Catch:{ Exception -> 0x0014 }
    L_0x002f:
        r6.mkdir();	 Catch:{ Exception -> 0x0014 }
        r7 = new java.io.File;	 Catch:{ Exception -> 0x0014 }
        r7.<init>(r6, r0);	 Catch:{ Exception -> 0x0014 }
        r8 = r7.exists();	 Catch:{ Exception -> 0x0014 }
        if (r8 == 0) goto L_0x009c;
    L_0x003d:
        r8 = 46;
        r8 = r0.lastIndexOf(r8);	 Catch:{ Exception -> 0x0014 }
        r9 = r7;
        r7 = 0;
    L_0x0045:
        r10 = 10;
        if (r7 >= r10) goto L_0x009a;
    L_0x0049:
        r9 = -1;
        r10 = ")";
        r11 = "(";
        if (r8 == r9) goto L_0x0073;
    L_0x0050:
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0014 }
        r9.<init>();	 Catch:{ Exception -> 0x0014 }
        r12 = r0.substring(r5, r8);	 Catch:{ Exception -> 0x0014 }
        r9.append(r12);	 Catch:{ Exception -> 0x0014 }
        r9.append(r11);	 Catch:{ Exception -> 0x0014 }
        r11 = r7 + 1;
        r9.append(r11);	 Catch:{ Exception -> 0x0014 }
        r9.append(r10);	 Catch:{ Exception -> 0x0014 }
        r10 = r0.substring(r8);	 Catch:{ Exception -> 0x0014 }
        r9.append(r10);	 Catch:{ Exception -> 0x0014 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0014 }
        goto L_0x008a;
    L_0x0073:
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0014 }
        r9.<init>();	 Catch:{ Exception -> 0x0014 }
        r9.append(r0);	 Catch:{ Exception -> 0x0014 }
        r9.append(r11);	 Catch:{ Exception -> 0x0014 }
        r11 = r7 + 1;
        r9.append(r11);	 Catch:{ Exception -> 0x0014 }
        r9.append(r10);	 Catch:{ Exception -> 0x0014 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0014 }
    L_0x008a:
        r10 = new java.io.File;	 Catch:{ Exception -> 0x0014 }
        r10.<init>(r6, r9);	 Catch:{ Exception -> 0x0014 }
        r9 = r10.exists();	 Catch:{ Exception -> 0x0014 }
        if (r9 != 0) goto L_0x0096;
    L_0x0095:
        goto L_0x009d;
    L_0x0096:
        r7 = r7 + 1;
        r9 = r10;
        goto L_0x0045;
    L_0x009a:
        r10 = r9;
        goto L_0x009d;
    L_0x009c:
        r10 = r7;
    L_0x009d:
        r0 = r10.exists();	 Catch:{ Exception -> 0x0014 }
        if (r0 != 0) goto L_0x00a6;
    L_0x00a3:
        r10.createNewFile();	 Catch:{ Exception -> 0x0014 }
    L_0x00a6:
        r6 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0014 }
        r8 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r6 = r6 - r8;
        r0 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x012c }
        r11 = r22;
        r0.<init>(r11);	 Catch:{ Exception -> 0x012c }
        r17 = r0.getChannel();	 Catch:{ Exception -> 0x012c }
        r0 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0122 }
        r0.<init>(r10);	 Catch:{ all -> 0x0122 }
        r18 = r0.getChannel();	 Catch:{ all -> 0x0122 }
        r13 = r17.size();	 Catch:{ all -> 0x0118 }
        r11 = 0;
        r19 = r6;
        r6 = r11;
    L_0x00ca:
        r0 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1));
        if (r0 >= 0) goto L_0x010c;
    L_0x00ce:
        r0 = r24[r5];	 Catch:{ all -> 0x0118 }
        if (r0 == 0) goto L_0x00d3;
    L_0x00d2:
        goto L_0x010c;
    L_0x00d3:
        r11 = r13 - r6;
        r3 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r15 = java.lang.Math.min(r3, r11);	 Catch:{ all -> 0x0118 }
        r11 = r18;
        r12 = r17;
        r0 = r13;
        r13 = r6;
        r11.transferFrom(r12, r13, r15);	 Catch:{ all -> 0x0118 }
        if (r2 == 0) goto L_0x0105;
    L_0x00e6:
        r11 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0118 }
        r11 = r11 - r8;
        r13 = (r19 > r11 ? 1 : (r19 == r11 ? 0 : -1));
        if (r13 > 0) goto L_0x0105;
    L_0x00ef:
        r11 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0118 }
        r13 = (float) r6;	 Catch:{ all -> 0x0118 }
        r14 = (float) r0;	 Catch:{ all -> 0x0118 }
        r13 = r13 / r14;
        r14 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r13 = r13 * r14;
        r13 = (int) r13;	 Catch:{ all -> 0x0118 }
        r14 = new org.telegram.messenger.-$$Lambda$MediaController$xP6EJyziTyW6Dts1XTRPHXBp5mc;	 Catch:{ all -> 0x0118 }
        r14.<init>(r2, r13);	 Catch:{ all -> 0x0118 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r14);	 Catch:{ all -> 0x0118 }
        r19 = r11;
    L_0x0105:
        r6 = r6 + r3;
        r3 = 2;
        r4 = 1;
        r13 = r0;
        r1 = r21;
        goto L_0x00ca;
    L_0x010c:
        if (r18 == 0) goto L_0x0111;
    L_0x010e:
        r18.close();	 Catch:{ all -> 0x0122 }
    L_0x0111:
        if (r17 == 0) goto L_0x0116;
    L_0x0113:
        r17.close();	 Catch:{ Exception -> 0x012c }
    L_0x0116:
        r0 = 1;
        goto L_0x0131;
    L_0x0118:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x011a }
    L_0x011a:
        r0 = move-exception;
        r1 = r0;
        if (r18 == 0) goto L_0x0121;
    L_0x011e:
        r18.close();	 Catch:{ all -> 0x0121 }
    L_0x0121:
        throw r1;	 Catch:{ all -> 0x0122 }
    L_0x0122:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x0124 }
    L_0x0124:
        r0 = move-exception;
        r1 = r0;
        if (r17 == 0) goto L_0x012b;
    L_0x0128:
        r17.close();	 Catch:{ all -> 0x012b }
    L_0x012b:
        throw r1;	 Catch:{ Exception -> 0x012c }
    L_0x012c:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0014 }
        r0 = 0;
    L_0x0131:
        r1 = r24[r5];	 Catch:{ Exception -> 0x0014 }
        if (r1 == 0) goto L_0x0139;
    L_0x0135:
        r10.delete();	 Catch:{ Exception -> 0x0014 }
        r0 = 0;
    L_0x0139:
        if (r0 == 0) goto L_0x016f;
    L_0x013b:
        r3 = 2;
        r1 = r21;
        if (r1 != r3) goto L_0x0164;
    L_0x0140:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0014 }
        r1 = "download";
        r0 = r0.getSystemService(r1);	 Catch:{ Exception -> 0x0014 }
        r11 = r0;
        r11 = (android.app.DownloadManager) r11;	 Catch:{ Exception -> 0x0014 }
        r12 = r10.getName();	 Catch:{ Exception -> 0x0014 }
        r13 = r10.getName();	 Catch:{ Exception -> 0x0014 }
        r14 = 0;
        r16 = r10.getAbsolutePath();	 Catch:{ Exception -> 0x0014 }
        r17 = r10.length();	 Catch:{ Exception -> 0x0014 }
        r19 = 1;
        r15 = r26;
        r11.addCompletedDownload(r12, r13, r14, r15, r16, r17, r19);	 Catch:{ Exception -> 0x0014 }
        goto L_0x016f;
    L_0x0164:
        r0 = android.net.Uri.fromFile(r10);	 Catch:{ Exception -> 0x0014 }
        org.telegram.messenger.AndroidUtilities.addMediaToGallery(r0);	 Catch:{ Exception -> 0x0014 }
        goto L_0x016f;
    L_0x016c:
        org.telegram.messenger.FileLog.e(r0);
    L_0x016f:
        if (r2 == 0) goto L_0x0179;
    L_0x0171:
        r0 = new org.telegram.messenger.-$$Lambda$MediaController$8aPsCpejhVSDf6eH_kBt7psjuS8;
        r0.<init>(r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x0179:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$saveFile$27(int, java.io.File, java.lang.String, boolean[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$25(AlertDialog alertDialog, int i) {
        try {
            alertDialog.setProgress(i);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    static /* synthetic */ void lambda$null$26(AlertDialog alertDialog) {
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

    /* JADX WARNING: Removed duplicated region for block: B:43:0x0095 A:{SYNTHETIC, Splitter:B:43:0x0095} */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x009f A:{SYNTHETIC, Splitter:B:48:0x009f} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00ad A:{SYNTHETIC, Splitter:B:56:0x00ad} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00b7 A:{SYNTHETIC, Splitter:B:61:0x00b7} */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0095 A:{SYNTHETIC, Splitter:B:43:0x0095} */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x009f A:{SYNTHETIC, Splitter:B:48:0x009f} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00ad A:{SYNTHETIC, Splitter:B:56:0x00ad} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00b7 A:{SYNTHETIC, Splitter:B:61:0x00b7} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00ad A:{SYNTHETIC, Splitter:B:56:0x00ad} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00b7 A:{SYNTHETIC, Splitter:B:61:0x00b7} */
    public static java.lang.String copyFileToCache(android.net.Uri r7, java.lang.String r8) {
        /*
        r0 = 0;
        r1 = getFileName(r7);	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r1 = org.telegram.messenger.FileLoader.fixFileName(r1);	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r2 = 0;
        if (r1 != 0) goto L_0x0027;
    L_0x000c:
        r1 = org.telegram.messenger.SharedConfig.getLastLocalId();	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        org.telegram.messenger.SharedConfig.saveConfig();	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r4 = "%d.%s";
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r5[r2] = r1;	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r1 = 1;
        r5[r1] = r8;	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r1 = java.lang.String.format(r3, r4, r5);	 Catch:{ Exception -> 0x008d, all -> 0x008a }
    L_0x0027:
        r8 = org.telegram.messenger.AndroidUtilities.getSharingDirectory();	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r8.mkdirs();	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r3 = new java.io.File;	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r3.<init>(r8, r1);	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r8 = android.net.Uri.fromFile(r3);	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r8 = org.telegram.messenger.AndroidUtilities.isInternalUri(r8);	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        if (r8 == 0) goto L_0x003e;
    L_0x003d:
        return r0;
    L_0x003e:
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r8 = r8.getContentResolver();	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r7 = r8.openInputStream(r7);	 Catch:{ Exception -> 0x008d, all -> 0x008a }
        r8 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0084, all -> 0x007e }
        r8.<init>(r3);	 Catch:{ Exception -> 0x0084, all -> 0x007e }
        r1 = 20480; // 0x5000 float:2.8699E-41 double:1.01185E-319;
        r1 = new byte[r1];	 Catch:{ Exception -> 0x0078, all -> 0x0073 }
    L_0x0051:
        r4 = r7.read(r1);	 Catch:{ Exception -> 0x0078, all -> 0x0073 }
        r5 = -1;
        if (r4 == r5) goto L_0x005c;
    L_0x0058:
        r8.write(r1, r2, r4);	 Catch:{ Exception -> 0x0078, all -> 0x0073 }
        goto L_0x0051;
    L_0x005c:
        r0 = r3.getAbsolutePath();	 Catch:{ Exception -> 0x0078, all -> 0x0073 }
        if (r7 == 0) goto L_0x006a;
    L_0x0062:
        r7.close();	 Catch:{ Exception -> 0x0066 }
        goto L_0x006a;
    L_0x0066:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x006a:
        r8.close();	 Catch:{ Exception -> 0x006e }
        goto L_0x0072;
    L_0x006e:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x0072:
        return r0;
    L_0x0073:
        r0 = move-exception;
        r6 = r0;
        r0 = r7;
        r7 = r6;
        goto L_0x00ab;
    L_0x0078:
        r1 = move-exception;
        r6 = r8;
        r8 = r7;
        r7 = r1;
        r1 = r6;
        goto L_0x0090;
    L_0x007e:
        r8 = move-exception;
        r6 = r0;
        r0 = r7;
        r7 = r8;
        r8 = r6;
        goto L_0x00ab;
    L_0x0084:
        r8 = move-exception;
        r1 = r0;
        r6 = r8;
        r8 = r7;
        r7 = r6;
        goto L_0x0090;
    L_0x008a:
        r7 = move-exception;
        r8 = r0;
        goto L_0x00ab;
    L_0x008d:
        r7 = move-exception;
        r8 = r0;
        r1 = r8;
    L_0x0090:
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ all -> 0x00a8 }
        if (r8 == 0) goto L_0x009d;
    L_0x0095:
        r8.close();	 Catch:{ Exception -> 0x0099 }
        goto L_0x009d;
    L_0x0099:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x009d:
        if (r1 == 0) goto L_0x00a7;
    L_0x009f:
        r1.close();	 Catch:{ Exception -> 0x00a3 }
        goto L_0x00a7;
    L_0x00a3:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x00a7:
        return r0;
    L_0x00a8:
        r7 = move-exception;
        r0 = r8;
        r8 = r1;
    L_0x00ab:
        if (r0 == 0) goto L_0x00b5;
    L_0x00ad:
        r0.close();	 Catch:{ Exception -> 0x00b1 }
        goto L_0x00b5;
    L_0x00b1:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00b5:
        if (r8 == 0) goto L_0x00bf;
    L_0x00b7:
        r8.close();	 Catch:{ Exception -> 0x00bb }
        goto L_0x00bf;
    L_0x00bb:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x00bf:
        goto L_0x00c1;
    L_0x00c0:
        throw r7;
    L_0x00c1:
        goto L_0x00c0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.copyFileToCache(android.net.Uri, java.lang.String):java.lang.String");
    }

    public static void loadGalleryPhotosAlbums(int i) {
        Thread thread = new Thread(new -$$Lambda$MediaController$Jn0TuEoIp5Q99EMH6pG70Nc7_hs(i));
        thread.setPriority(1);
        thread.start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0255 A:{SYNTHETIC, Splitter:B:121:0x0255} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0255 A:{SYNTHETIC, Splitter:B:121:0x0255} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0428 A:{SYNTHETIC, Splitter:B:221:0x0428} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0428 A:{SYNTHETIC, Splitter:B:221:0x0428} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0428 A:{SYNTHETIC, Splitter:B:221:0x0428} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0291 A:{SYNTHETIC, Splitter:B:135:0x0291} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a0 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02cb A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02ce A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e2 A:{Catch:{ all -> 0x041f }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0418 A:{SYNTHETIC, Splitter:B:212:0x0418} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0428 A:{SYNTHETIC, Splitter:B:221:0x0428} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x043b A:{LOOP_END, LOOP:2: B:227:0x0435->B:229:0x043b} */
    static /* synthetic */ void lambda$loadGalleryPhotosAlbums$29(int r48) {
        /*
        r1 = "height";
        r2 = "width";
        r3 = "_data";
        r4 = "bucket_display_name";
        r5 = "bucket_id";
        r6 = "_id";
        r7 = " DESC";
        r8 = "android.permission.READ_EXTERNAL_STORAGE";
        r9 = "date_modified";
        r10 = "datetaken";
        r12 = new java.util.ArrayList;
        r12.<init>();
        r13 = new java.util.ArrayList;
        r13.<init>();
        r11 = new android.util.SparseArray;
        r11.<init>();
        r14 = new android.util.SparseArray;
        r14.<init>();
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r0.<init>();	 Catch:{ Exception -> 0x0045 }
        r16 = android.os.Environment.DIRECTORY_DCIM;	 Catch:{ Exception -> 0x0045 }
        r16 = android.os.Environment.getExternalStoragePublicDirectory(r16);	 Catch:{ Exception -> 0x0045 }
        r15 = r16.getAbsolutePath();	 Catch:{ Exception -> 0x0045 }
        r0.append(r15);	 Catch:{ Exception -> 0x0045 }
        r15 = "/Camera/";
        r0.append(r15);	 Catch:{ Exception -> 0x0045 }
        r15 = r0.toString();	 Catch:{ Exception -> 0x0045 }
        goto L_0x004a;
    L_0x0045:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r15 = 0;
    L_0x004a:
        r16 = r9;
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0273 }
        r9 = 23;
        if (r0 < r9) goto L_0x007a;
    L_0x0052:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0273 }
        if (r0 < r9) goto L_0x005f;
    L_0x0056:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0273 }
        r0 = r0.checkSelfPermission(r8);	 Catch:{ all -> 0x0273 }
        if (r0 != 0) goto L_0x005f;
    L_0x005e:
        goto L_0x007a;
    L_0x005f:
        r27 = r1;
        r25 = r2;
        r22 = r3;
        r21 = r4;
        r20 = r5;
        r23 = r6;
        r24 = r7;
        r32 = r8;
        r26 = r10;
        r9 = 0;
    L_0x0072:
        r28 = 0;
        r29 = 0;
        r30 = 0;
        goto L_0x0253;
    L_0x007a:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0273 }
        r20 = r0.getContentResolver();	 Catch:{ all -> 0x0273 }
        r21 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ all -> 0x0273 }
        r22 = projectionPhotos;	 Catch:{ all -> 0x0273 }
        r23 = 0;
        r24 = 0;
        r0 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0273 }
        r0.<init>();	 Catch:{ all -> 0x0273 }
        r9 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0273 }
        r26 = r10;
        r10 = 28;
        if (r9 <= r10) goto L_0x0098;
    L_0x0095:
        r9 = r16;
        goto L_0x009a;
    L_0x0098:
        r9 = r26;
    L_0x009a:
        r0.append(r9);	 Catch:{ all -> 0x0261 }
        r0.append(r7);	 Catch:{ all -> 0x0261 }
        r25 = r0.toString();	 Catch:{ all -> 0x0261 }
        r9 = android.provider.MediaStore.Images.Media.query(r20, r21, r22, r23, r24, r25);	 Catch:{ all -> 0x0261 }
        if (r9 == 0) goto L_0x0241;
    L_0x00aa:
        r0 = r9.getColumnIndex(r6);	 Catch:{ all -> 0x022e }
        r10 = r9.getColumnIndex(r5);	 Catch:{ all -> 0x022e }
        r20 = r5;
        r5 = r9.getColumnIndex(r4);	 Catch:{ all -> 0x0224 }
        r21 = r4;
        r4 = r9.getColumnIndex(r3);	 Catch:{ all -> 0x021c }
        r22 = r3;
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0216 }
        r23 = r6;
        r6 = 28;
        if (r3 <= r6) goto L_0x00cb;
    L_0x00c8:
        r3 = r16;
        goto L_0x00cd;
    L_0x00cb:
        r3 = r26;
    L_0x00cd:
        r3 = r9.getColumnIndex(r3);	 Catch:{ all -> 0x0210 }
        r6 = "orientation";
        r6 = r9.getColumnIndex(r6);	 Catch:{ all -> 0x0210 }
        r24 = r7;
        r7 = r9.getColumnIndex(r2);	 Catch:{ all -> 0x020a }
        r25 = r2;
        r2 = r9.getColumnIndex(r1);	 Catch:{ all -> 0x0206 }
        r27 = r1;
        r1 = "_size";
        r1 = r9.getColumnIndex(r1);	 Catch:{ all -> 0x0204 }
        r28 = 0;
        r29 = 0;
        r30 = 0;
        r31 = 0;
    L_0x00f3:
        r32 = r9.moveToNext();	 Catch:{ all -> 0x01fd }
        if (r32 == 0) goto L_0x01f9;
    L_0x00f9:
        r32 = r8;
        r8 = r9.getString(r4);	 Catch:{ all -> 0x01f7 }
        r33 = android.text.TextUtils.isEmpty(r8);	 Catch:{ all -> 0x01f7 }
        if (r33 == 0) goto L_0x0108;
    L_0x0105:
        r8 = r32;
        goto L_0x00f3;
    L_0x0108:
        r35 = r9.getInt(r0);	 Catch:{ all -> 0x01f7 }
        r45 = r0;
        r0 = r9.getInt(r10);	 Catch:{ all -> 0x01f7 }
        r46 = r4;
        r4 = r9.getString(r5);	 Catch:{ all -> 0x01f7 }
        r36 = r9.getLong(r3);	 Catch:{ all -> 0x01f7 }
        r39 = r9.getInt(r6);	 Catch:{ all -> 0x01f7 }
        r41 = r9.getInt(r7);	 Catch:{ all -> 0x01f7 }
        r42 = r9.getInt(r2);	 Catch:{ all -> 0x01f7 }
        r43 = r9.getLong(r1);	 Catch:{ all -> 0x01f7 }
        r47 = r1;
        r1 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ all -> 0x01f7 }
        r40 = 0;
        r33 = r1;
        r34 = r0;
        r38 = r8;
        r33.<init>(r34, r35, r36, r38, r39, r40, r41, r42, r43);	 Catch:{ all -> 0x01f7 }
        if (r28 != 0) goto L_0x0156;
    L_0x013d:
        r33 = r2;
        r2 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x01f7 }
        r34 = r3;
        r3 = "AllPhotos";
        r35 = r5;
        r5 = NUM; // 0x7f0e00d7 float:1.8875474E38 double:1.053162263E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ all -> 0x01f7 }
        r5 = 0;
        r2.<init>(r5, r3, r1);	 Catch:{ all -> 0x01f7 }
        r13.add(r5, r2);	 Catch:{ all -> 0x0175 }
        goto L_0x015e;
    L_0x0156:
        r33 = r2;
        r34 = r3;
        r35 = r5;
        r2 = r28;
    L_0x015e:
        if (r29 != 0) goto L_0x0178;
    L_0x0160:
        r3 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x0175 }
        r5 = "AllMedia";
        r36 = r6;
        r6 = NUM; // 0x7f0e00d6 float:1.8875472E38 double:1.0531622624E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);	 Catch:{ all -> 0x0175 }
        r6 = 0;
        r3.<init>(r6, r5, r1);	 Catch:{ all -> 0x0175 }
        r12.add(r6, r3);	 Catch:{ all -> 0x01f2 }
        goto L_0x017c;
    L_0x0175:
        r0 = move-exception;
        goto L_0x028c;
    L_0x0178:
        r36 = r6;
        r3 = r29;
    L_0x017c:
        r2.addPhoto(r1);	 Catch:{ all -> 0x01f2 }
        r3.addPhoto(r1);	 Catch:{ all -> 0x01f2 }
        r5 = r11.get(r0);	 Catch:{ all -> 0x01f2 }
        r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5;	 Catch:{ all -> 0x01f2 }
        if (r5 != 0) goto L_0x01ac;
    L_0x018a:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x01f2 }
        r5.<init>(r0, r4, r1);	 Catch:{ all -> 0x01f2 }
        r11.put(r0, r5);	 Catch:{ all -> 0x01f2 }
        if (r30 != 0) goto L_0x01a9;
    L_0x0194:
        if (r15 == 0) goto L_0x01a9;
    L_0x0196:
        if (r8 == 0) goto L_0x01a9;
    L_0x0198:
        r6 = r8.startsWith(r15);	 Catch:{ all -> 0x01f2 }
        if (r6 == 0) goto L_0x01a9;
    L_0x019e:
        r6 = 0;
        r12.add(r6, r5);	 Catch:{ all -> 0x01f2 }
        r6 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x01f2 }
        r30 = r6;
        goto L_0x01ac;
    L_0x01a9:
        r12.add(r5);	 Catch:{ all -> 0x01f2 }
    L_0x01ac:
        r5.addPhoto(r1);	 Catch:{ all -> 0x01f2 }
        r5 = r14.get(r0);	 Catch:{ all -> 0x01f2 }
        r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5;	 Catch:{ all -> 0x01f2 }
        if (r5 != 0) goto L_0x01d9;
    L_0x01b7:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x01f2 }
        r5.<init>(r0, r4, r1);	 Catch:{ all -> 0x01f2 }
        r14.put(r0, r5);	 Catch:{ all -> 0x01f2 }
        if (r31 != 0) goto L_0x01d6;
    L_0x01c1:
        if (r15 == 0) goto L_0x01d6;
    L_0x01c3:
        if (r8 == 0) goto L_0x01d6;
    L_0x01c5:
        r4 = r8.startsWith(r15);	 Catch:{ all -> 0x01f2 }
        if (r4 == 0) goto L_0x01d6;
    L_0x01cb:
        r4 = 0;
        r13.add(r4, r5);	 Catch:{ all -> 0x01f2 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x01f2 }
        r31 = r0;
        goto L_0x01d9;
    L_0x01d6:
        r13.add(r5);	 Catch:{ all -> 0x01f2 }
    L_0x01d9:
        r5.addPhoto(r1);	 Catch:{ all -> 0x01f2 }
        r28 = r2;
        r29 = r3;
        r8 = r32;
        r2 = r33;
        r3 = r34;
        r5 = r35;
        r6 = r36;
        r0 = r45;
        r4 = r46;
        r1 = r47;
        goto L_0x00f3;
    L_0x01f2:
        r0 = move-exception;
        r29 = r3;
        goto L_0x028c;
    L_0x01f7:
        r0 = move-exception;
        goto L_0x0200;
    L_0x01f9:
        r32 = r8;
        goto L_0x0253;
    L_0x01fd:
        r0 = move-exception;
        r32 = r8;
    L_0x0200:
        r2 = r28;
        goto L_0x028c;
    L_0x0204:
        r0 = move-exception;
        goto L_0x023d;
    L_0x0206:
        r0 = move-exception;
        r27 = r1;
        goto L_0x023d;
    L_0x020a:
        r0 = move-exception;
        r27 = r1;
        r25 = r2;
        goto L_0x023d;
    L_0x0210:
        r0 = move-exception;
        r27 = r1;
        r25 = r2;
        goto L_0x023b;
    L_0x0216:
        r0 = move-exception;
        r27 = r1;
        r25 = r2;
        goto L_0x0239;
    L_0x021c:
        r0 = move-exception;
        r27 = r1;
        r25 = r2;
        r22 = r3;
        goto L_0x0239;
    L_0x0224:
        r0 = move-exception;
        r27 = r1;
        r25 = r2;
        r22 = r3;
        r21 = r4;
        goto L_0x0239;
    L_0x022e:
        r0 = move-exception;
        r27 = r1;
        r25 = r2;
        r22 = r3;
        r21 = r4;
        r20 = r5;
    L_0x0239:
        r23 = r6;
    L_0x023b:
        r24 = r7;
    L_0x023d:
        r32 = r8;
        r2 = 0;
        goto L_0x0288;
    L_0x0241:
        r27 = r1;
        r25 = r2;
        r22 = r3;
        r21 = r4;
        r20 = r5;
        r23 = r6;
        r24 = r7;
        r32 = r8;
        goto L_0x0072;
    L_0x0253:
        if (r9 == 0) goto L_0x025e;
    L_0x0255:
        r9.close();	 Catch:{ Exception -> 0x0259 }
        goto L_0x025e;
    L_0x0259:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x025e:
        r2 = r28;
        goto L_0x029a;
    L_0x0261:
        r0 = move-exception;
        r27 = r1;
        r25 = r2;
        r22 = r3;
        r21 = r4;
        r20 = r5;
        r23 = r6;
        r24 = r7;
        r32 = r8;
        goto L_0x0286;
    L_0x0273:
        r0 = move-exception;
        r27 = r1;
        r25 = r2;
        r22 = r3;
        r21 = r4;
        r20 = r5;
        r23 = r6;
        r24 = r7;
        r32 = r8;
        r26 = r10;
    L_0x0286:
        r2 = 0;
        r9 = 0;
    L_0x0288:
        r29 = 0;
        r30 = 0;
    L_0x028c:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0463 }
        if (r9 == 0) goto L_0x029a;
    L_0x0291:
        r9.close();	 Catch:{ Exception -> 0x0295 }
        goto L_0x029a;
    L_0x0295:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x029a:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x041f }
        r1 = 23;
        if (r0 < r1) goto L_0x02b4;
    L_0x02a0:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x041f }
        if (r0 < r1) goto L_0x02af;
    L_0x02a4:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x041f }
        r1 = r32;
        r0 = r0.checkSelfPermission(r1);	 Catch:{ all -> 0x041f }
        if (r0 != 0) goto L_0x02af;
    L_0x02ae:
        goto L_0x02b4;
    L_0x02af:
        r1 = 0;
        r17 = 0;
        goto L_0x0416;
    L_0x02b4:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x041f }
        r3 = r0.getContentResolver();	 Catch:{ all -> 0x041f }
        r4 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ all -> 0x041f }
        r5 = projectionVideo;	 Catch:{ all -> 0x041f }
        r6 = 0;
        r7 = 0;
        r0 = new java.lang.StringBuilder;	 Catch:{ all -> 0x041f }
        r0.<init>();	 Catch:{ all -> 0x041f }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x041f }
        r8 = 28;
        if (r1 <= r8) goto L_0x02ce;
    L_0x02cb:
        r1 = r16;
        goto L_0x02d0;
    L_0x02ce:
        r1 = r26;
    L_0x02d0:
        r0.append(r1);	 Catch:{ all -> 0x041f }
        r1 = r24;
        r0.append(r1);	 Catch:{ all -> 0x041f }
        r8 = r0.toString();	 Catch:{ all -> 0x041f }
        r9 = android.provider.MediaStore.Images.Media.query(r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x041f }
        if (r9 == 0) goto L_0x02af;
    L_0x02e2:
        r1 = r23;
        r0 = r9.getColumnIndex(r1);	 Catch:{ all -> 0x041f }
        r1 = r20;
        r1 = r9.getColumnIndex(r1);	 Catch:{ all -> 0x041f }
        r3 = r21;
        r3 = r9.getColumnIndex(r3);	 Catch:{ all -> 0x041f }
        r4 = r22;
        r4 = r9.getColumnIndex(r4);	 Catch:{ all -> 0x041f }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x041f }
        r6 = 28;
        if (r5 <= r6) goto L_0x0303;
    L_0x0300:
        r5 = r16;
        goto L_0x0305;
    L_0x0303:
        r5 = r26;
    L_0x0305:
        r5 = r9.getColumnIndex(r5);	 Catch:{ all -> 0x041f }
        r6 = "duration";
        r6 = r9.getColumnIndex(r6);	 Catch:{ all -> 0x041f }
        r7 = r25;
        r7 = r9.getColumnIndex(r7);	 Catch:{ all -> 0x041f }
        r8 = r27;
        r8 = r9.getColumnIndex(r8);	 Catch:{ all -> 0x041f }
        r10 = "_size";
        r10 = r9.getColumnIndex(r10);	 Catch:{ all -> 0x041f }
        r17 = 0;
    L_0x0323:
        r14 = r9.moveToNext();	 Catch:{ all -> 0x0413 }
        if (r14 == 0) goto L_0x0411;
    L_0x0329:
        r14 = r9.getString(r4);	 Catch:{ all -> 0x0413 }
        r16 = android.text.TextUtils.isEmpty(r14);	 Catch:{ all -> 0x0413 }
        if (r16 == 0) goto L_0x0334;
    L_0x0333:
        goto L_0x0323;
    L_0x0334:
        r33 = r9.getInt(r0);	 Catch:{ all -> 0x0413 }
        r16 = r0;
        r0 = r9.getInt(r1);	 Catch:{ all -> 0x0413 }
        r18 = r1;
        r1 = r9.getString(r3);	 Catch:{ all -> 0x0413 }
        r34 = r9.getLong(r5);	 Catch:{ all -> 0x0413 }
        r20 = r9.getLong(r6);	 Catch:{ all -> 0x0413 }
        r39 = r9.getInt(r7);	 Catch:{ all -> 0x0413 }
        r40 = r9.getInt(r8);	 Catch:{ all -> 0x0413 }
        r41 = r9.getLong(r10);	 Catch:{ all -> 0x0413 }
        r19 = r3;
        r3 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ all -> 0x0413 }
        r22 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r24 = r4;
        r25 = r5;
        r4 = r20 / r22;
        r5 = (int) r4;	 Catch:{ all -> 0x0413 }
        r38 = 1;
        r31 = r3;
        r32 = r0;
        r36 = r14;
        r37 = r5;
        r31.<init>(r32, r33, r34, r36, r37, r38, r39, r40, r41);	 Catch:{ all -> 0x0413 }
        if (r17 != 0) goto L_0x0394;
    L_0x0374:
        r4 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x0413 }
        r5 = "AllVideos";
        r20 = r6;
        r6 = NUM; // 0x7f0e00d8 float:1.8875476E38 double:1.0531622633E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);	 Catch:{ all -> 0x0413 }
        r6 = 0;
        r4.<init>(r6, r5, r3);	 Catch:{ all -> 0x0413 }
        r5 = 1;
        r4.videoOnly = r5;	 Catch:{ all -> 0x03b6 }
        if (r29 == 0) goto L_0x038b;
    L_0x038a:
        goto L_0x038c;
    L_0x038b:
        r5 = 0;
    L_0x038c:
        if (r2 == 0) goto L_0x0390;
    L_0x038e:
        r5 = r5 + 1;
    L_0x0390:
        r12.add(r5, r4);	 Catch:{ all -> 0x03b6 }
        goto L_0x0398;
    L_0x0394:
        r20 = r6;
        r4 = r17;
    L_0x0398:
        if (r29 != 0) goto L_0x03ba;
    L_0x039a:
        r5 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x03b6 }
        r6 = "AllMedia";
        r21 = r7;
        r7 = NUM; // 0x7f0e00d6 float:1.8875472E38 double:1.0531622624E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x03b6 }
        r7 = 0;
        r5.<init>(r7, r6, r3);	 Catch:{ all -> 0x03b6 }
        r12.add(r7, r5);	 Catch:{ all -> 0x03af }
        goto L_0x03be;
    L_0x03af:
        r0 = move-exception;
        r17 = r4;
        r29 = r5;
        goto L_0x0414;
    L_0x03b6:
        r0 = move-exception;
        r17 = r4;
        goto L_0x0414;
    L_0x03ba:
        r21 = r7;
        r5 = r29;
    L_0x03be:
        r4.addPhoto(r3);	 Catch:{ all -> 0x040a }
        r5.addPhoto(r3);	 Catch:{ all -> 0x040a }
        r6 = r11.get(r0);	 Catch:{ all -> 0x040a }
        r6 = (org.telegram.messenger.MediaController.AlbumEntry) r6;	 Catch:{ all -> 0x040a }
        if (r6 != 0) goto L_0x03f0;
    L_0x03cc:
        r6 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ all -> 0x040a }
        r6.<init>(r0, r1, r3);	 Catch:{ all -> 0x040a }
        r11.put(r0, r6);	 Catch:{ all -> 0x040a }
        if (r30 != 0) goto L_0x03eb;
    L_0x03d6:
        if (r15 == 0) goto L_0x03eb;
    L_0x03d8:
        if (r14 == 0) goto L_0x03eb;
    L_0x03da:
        r1 = r14.startsWith(r15);	 Catch:{ all -> 0x040a }
        if (r1 == 0) goto L_0x03eb;
    L_0x03e0:
        r1 = 0;
        r12.add(r1, r6);	 Catch:{ all -> 0x0408 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x0408 }
        r30 = r0;
        goto L_0x03f1;
    L_0x03eb:
        r1 = 0;
        r12.add(r6);	 Catch:{ all -> 0x0408 }
        goto L_0x03f1;
    L_0x03f0:
        r1 = 0;
    L_0x03f1:
        r6.addPhoto(r3);	 Catch:{ all -> 0x0408 }
        r17 = r4;
        r29 = r5;
        r0 = r16;
        r1 = r18;
        r3 = r19;
        r6 = r20;
        r7 = r21;
        r4 = r24;
        r5 = r25;
        goto L_0x0323;
    L_0x0408:
        r0 = move-exception;
        goto L_0x040c;
    L_0x040a:
        r0 = move-exception;
        r1 = 0;
    L_0x040c:
        r17 = r4;
        r29 = r5;
        goto L_0x0423;
    L_0x0411:
        r1 = 0;
        goto L_0x0416;
    L_0x0413:
        r0 = move-exception;
    L_0x0414:
        r1 = 0;
        goto L_0x0423;
    L_0x0416:
        if (r9 == 0) goto L_0x0431;
    L_0x0418:
        r9.close();	 Catch:{ Exception -> 0x041c }
        goto L_0x0431;
    L_0x041c:
        r0 = move-exception;
        r3 = r0;
        goto L_0x042e;
    L_0x041f:
        r0 = move-exception;
        r1 = 0;
        r17 = 0;
    L_0x0423:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0455 }
        if (r9 == 0) goto L_0x0431;
    L_0x0428:
        r9.close();	 Catch:{ Exception -> 0x042c }
        goto L_0x0431;
    L_0x042c:
        r0 = move-exception;
        r3 = r0;
    L_0x042e:
        org.telegram.messenger.FileLog.e(r3);
    L_0x0431:
        r15 = r29;
        r14 = r30;
    L_0x0435:
        r0 = r12.size();
        if (r1 >= r0) goto L_0x044b;
    L_0x043b:
        r0 = r12.get(r1);
        r0 = (org.telegram.messenger.MediaController.AlbumEntry) r0;
        r0 = r0.photos;
        r3 = org.telegram.messenger.-$$Lambda$MediaController$otH22KKTmWs60d556iNAuKETyVk.INSTANCE;
        java.util.Collections.sort(r0, r3);
        r1 = r1 + 1;
        goto L_0x0435;
    L_0x044b:
        r18 = 0;
        r11 = r48;
        r16 = r2;
        broadcastNewPhotos(r11, r12, r13, r14, r15, r16, r17, r18);
        return;
    L_0x0455:
        r0 = move-exception;
        r1 = r0;
        if (r9 == 0) goto L_0x0462;
    L_0x0459:
        r9.close();	 Catch:{ Exception -> 0x045d }
        goto L_0x0462;
    L_0x045d:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0462:
        throw r1;
    L_0x0463:
        r0 = move-exception;
        r1 = r0;
        if (r9 == 0) goto L_0x0470;
    L_0x0467:
        r9.close();	 Catch:{ Exception -> 0x046b }
        goto L_0x0470;
    L_0x046b:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0470:
        goto L_0x0472;
    L_0x0471:
        throw r1;
    L_0x0472:
        goto L_0x0471;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$loadGalleryPhotosAlbums$29(int):void");
    }

    static /* synthetic */ int lambda$null$28(PhotoEntry photoEntry, PhotoEntry photoEntry2) {
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
        -$$Lambda$MediaController$waoAtF3OnIAj_o9cfnBp-Ey1q0M -__lambda_mediacontroller_waoatf3oniaj_o9cfnbp-ey1q0m = new -$$Lambda$MediaController$waoAtF3OnIAj_o9cfnBp-Ey1q0M(i, arrayList, arrayList2, num, albumEntry, albumEntry2, albumEntry3);
        broadcastPhotosRunnable = -__lambda_mediacontroller_waoatf3oniaj_o9cfnbp-ey1q0m;
        AndroidUtilities.runOnUIThread(-__lambda_mediacontroller_waoatf3oniaj_o9cfnbp-ey1q0m, (long) i2);
    }

    static /* synthetic */ void lambda$broadcastNewPhotos$30(int i, ArrayList arrayList, ArrayList arrayList2, Integer num, AlbumEntry albumEntry, AlbumEntry albumEntry2, AlbumEntry albumEntry3) {
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
        this.videoConvertQueue.add(new VideoConvertMessage(messageObject, messageObject.videoEditedInfo));
        if (this.videoConvertQueue.size() == 1) {
            startVideoConvertFromQueue();
        }
        return true;
    }

    public void cancelVideoConvert(MessageObject messageObject) {
        if (messageObject != null && !this.videoConvertQueue.isEmpty()) {
            int i = 0;
            while (i < this.videoConvertQueue.size()) {
                VideoConvertMessage videoConvertMessage = (VideoConvertMessage) this.videoConvertQueue.get(i);
                MessageObject messageObject2 = videoConvertMessage.messageObject;
                if (!messageObject2.equals(messageObject) || messageObject2.currentAccount != messageObject.currentAccount) {
                    i++;
                } else if (i == 0) {
                    synchronized (this.videoConvertSync) {
                        videoConvertMessage.videoEditedInfo.canceled = true;
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
        VideoConvertMessage videoConvertMessage = (VideoConvertMessage) this.videoConvertQueue.get(0);
        MessageObject messageObject = videoConvertMessage.messageObject;
        VideoEditedInfo videoEditedInfo = videoConvertMessage.videoEditedInfo;
        synchronized (this.videoConvertSync) {
            if (videoEditedInfo != null) {
                videoEditedInfo.canceled = false;
            }
        }
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
        VideoConvertRunnable.runConversion(videoConvertMessage);
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

    public static int findTrack(MediaExtractor mediaExtractor, boolean z) {
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

    private void didWriteData(VideoConvertMessage videoConvertMessage, File file, boolean z, long j, boolean z2, float f) {
        VideoEditedInfo videoEditedInfo = videoConvertMessage.videoEditedInfo;
        boolean z3 = videoEditedInfo.videoConvertFirstWrite;
        if (z3) {
            videoEditedInfo.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaController$rEPWbo0gmy3DDvtD04KagC-xojo(this, z2, z, videoConvertMessage, file, f, z3, j));
    }

    public /* synthetic */ void lambda$didWriteData$31$MediaController(boolean z, boolean z2, VideoConvertMessage videoConvertMessage, File file, float f, boolean z3, long j) {
        if (z || z2) {
            synchronized (this.videoConvertSync) {
                videoConvertMessage.videoEditedInfo.canceled = false;
            }
            this.videoConvertQueue.remove(videoConvertMessage);
            startVideoConvertFromQueue();
        }
        if (z) {
            NotificationCenter.getInstance(videoConvertMessage.currentAccount).postNotificationName(NotificationCenter.filePreparingFailed, videoConvertMessage.messageObject, file.toString(), Float.valueOf(f));
            return;
        }
        if (z3) {
            NotificationCenter.getInstance(videoConvertMessage.currentAccount).postNotificationName(NotificationCenter.filePreparingStarted, videoConvertMessage.messageObject, file.toString(), Float.valueOf(f));
        }
        NotificationCenter instance = NotificationCenter.getInstance(videoConvertMessage.currentAccount);
        int i = NotificationCenter.fileNewChunkAvailable;
        Object[] objArr = new Object[5];
        objArr[0] = videoConvertMessage.messageObject;
        objArr[1] = file.toString();
        objArr[2] = Long.valueOf(j);
        objArr[3] = Long.valueOf(z2 ? file.length() : 0);
        objArr[4] = Float.valueOf(f);
        instance.postNotificationName(i, objArr);
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00aa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0145 A:{SKIP} */
    private boolean convertVideo(org.telegram.messenger.MediaController.VideoConvertMessage r33) {
        /*
        r32 = this;
        r9 = r32;
        r0 = r33;
        r1 = r0.messageObject;
        r2 = r0.videoEditedInfo;
        if (r1 == 0) goto L_0x015e;
    L_0x000a:
        if (r2 != 0) goto L_0x000e;
    L_0x000c:
        goto L_0x015e;
    L_0x000e:
        r4 = r2.originalPath;
        r5 = r2.startTime;
        r7 = r2.endTime;
        r10 = r2.resultWidth;
        r11 = r2.resultHeight;
        r12 = r2.rotationValue;
        r13 = r2.originalWidth;
        r14 = r2.originalHeight;
        r15 = r2.framerate;
        r3 = r2.bitrate;
        r16 = r10;
        r17 = r11;
        r10 = r1.getDialogId();
        r11 = (int) r10;
        if (r11 != 0) goto L_0x0030;
    L_0x002d:
        r18 = 1;
        goto L_0x0032;
    L_0x0030:
        r18 = 0;
    L_0x0032:
        r11 = new java.io.File;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        r11.<init>(r1);
        r1 = r11.exists();
        if (r1 == 0) goto L_0x0044;
    L_0x0041:
        r11.delete();
    L_0x0044:
        if (r4 != 0) goto L_0x0048;
    L_0x0046:
        r4 = "";
    L_0x0048:
        r19 = 0;
        r1 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1));
        if (r1 <= 0) goto L_0x0057;
    L_0x004e:
        r1 = (r7 > r19 ? 1 : (r7 == r19 ? 0 : -1));
        if (r1 <= 0) goto L_0x0057;
    L_0x0052:
        r19 = r7 - r5;
        r24 = r19;
        goto L_0x005d;
    L_0x0057:
        r1 = (r7 > r19 ? 1 : (r7 == r19 ? 0 : -1));
        if (r1 <= 0) goto L_0x0060;
    L_0x005b:
        r24 = r7;
    L_0x005d:
        r19 = r11;
        goto L_0x0070;
    L_0x0060:
        r1 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1));
        if (r1 <= 0) goto L_0x006a;
    L_0x0064:
        r19 = r11;
        r10 = r2.originalDuration;
        r10 = r10 - r5;
        goto L_0x006e;
    L_0x006a:
        r19 = r11;
        r10 = r2.originalDuration;
    L_0x006e:
        r24 = r10;
    L_0x0070:
        r10 = 59;
        if (r15 != 0) goto L_0x0079;
    L_0x0074:
        r10 = 25;
        r20 = 25;
        goto L_0x0080;
    L_0x0079:
        if (r15 <= r10) goto L_0x007e;
    L_0x007b:
        r20 = 59;
        goto L_0x0080;
    L_0x007e:
        r20 = r15;
    L_0x0080:
        r10 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r11 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r15 = 90;
        if (r12 != r15) goto L_0x008f;
    L_0x0088:
        r12 = r16;
        r15 = r17;
    L_0x008c:
        r16 = 0;
        goto L_0x00a8;
    L_0x008f:
        if (r12 != r11) goto L_0x0098;
    L_0x0091:
        r15 = r16;
        r12 = r17;
        r10 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        goto L_0x008c;
    L_0x0098:
        if (r12 != r10) goto L_0x00a1;
    L_0x009a:
        r12 = r16;
        r15 = r17;
        r10 = 90;
        goto L_0x008c;
    L_0x00a1:
        r15 = r16;
        r10 = 0;
        r16 = r12;
        r12 = r17;
    L_0x00a8:
        if (r15 != r13) goto L_0x00c2;
    L_0x00aa:
        if (r12 != r14) goto L_0x00c2;
    L_0x00ac:
        if (r10 != 0) goto L_0x00c2;
    L_0x00ae:
        r10 = r2.roundVideo;
        if (r10 != 0) goto L_0x00c2;
    L_0x00b2:
        r10 = android.os.Build.VERSION.SDK_INT;
        r11 = 18;
        if (r10 < r11) goto L_0x00bf;
    L_0x00b8:
        r10 = -1;
        r13 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1));
        if (r13 == 0) goto L_0x00bf;
    L_0x00be:
        goto L_0x00c2;
    L_0x00bf:
        r23 = 0;
        goto L_0x00c4;
    L_0x00c2:
        r23 = 1;
    L_0x00c4:
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r11 = "videoconvert";
        r13 = 0;
        r28 = r10.getSharedPreferences(r11, r13);
        r29 = java.lang.System.currentTimeMillis();
        r10 = new org.telegram.messenger.MediaController$8;
        r26 = r10;
        r11 = r19;
        r10.<init>(r2, r11, r0);
        r1 = 1;
        r2.videoConvertFirstWrite = r1;
        r13 = new org.telegram.messenger.video.MediaCodecVideoConvertor;
        r10 = r13;
        r13.<init>();
        r31 = r11;
        r11 = r4;
        r17 = r12;
        r12 = r31;
        r13 = r16;
        r14 = r18;
        r16 = r17;
        r17 = r20;
        r18 = r3;
        r19 = r5;
        r21 = r7;
        r3 = r10.convertVideo(r11, r12, r13, r14, r15, r16, r17, r18, r19, r21, r23, r24, r26);
        r4 = r2.canceled;
        if (r4 != 0) goto L_0x010b;
    L_0x0101:
        r5 = r9.videoConvertSync;
        monitor-enter(r5);
        r4 = r2.canceled;	 Catch:{ all -> 0x0108 }
        monitor-exit(r5);	 Catch:{ all -> 0x0108 }
        goto L_0x010b;
    L_0x0108:
        r0 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0108 }
        throw r0;
    L_0x010b:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0131;
    L_0x010f:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r5 = "time=";
        r2.append(r5);
        r5 = java.lang.System.currentTimeMillis();
        r5 = r5 - r29;
        r2.append(r5);
        r5 = " canceled=";
        r2.append(r5);
        r2.append(r4);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x0131:
        r2 = r28.edit();
        r5 = "isPreviousOk";
        r2 = r2.putBoolean(r5, r1);
        r2.apply();
        r5 = 1;
        r6 = r31.length();
        if (r3 != 0) goto L_0x014b;
    L_0x0145:
        if (r4 == 0) goto L_0x0148;
    L_0x0147:
        goto L_0x014b;
    L_0x0148:
        r27 = 0;
        goto L_0x014d;
    L_0x014b:
        r27 = 1;
    L_0x014d:
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = 1;
        r1 = r32;
        r2 = r33;
        r3 = r31;
        r4 = r5;
        r5 = r6;
        r7 = r27;
        r1.didWriteData(r2, r3, r4, r5, r7, r8);
        return r10;
    L_0x015e:
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MediaController$VideoConvertMessage):boolean");
    }

    public static int getVideoBitrate(String str) {
        int parseInt;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(str);
            parseInt = Integer.parseInt(mediaMetadataRetriever.extractMetadata(20));
        } catch (Exception e) {
            FileLog.e(e);
            parseInt = 0;
        }
        mediaMetadataRetriever.release();
        return parseInt;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0056 A:{RETURN} */
    public static int makeVideoBitrate(int r5, int r6, int r7, int r8, int r9) {
        /*
        r0 = java.lang.Math.min(r8, r9);
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = 1080; // 0x438 float:1.513E-42 double:5.336E-321;
        if (r0 < r2) goto L_0x0010;
    L_0x000a:
        r0 = 6800000; // 0x67CLASSNAME float:9.52883E-39 double:3.3596464E-317;
    L_0x000d:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0036;
    L_0x0010:
        r0 = java.lang.Math.min(r8, r9);
        r2 = 720; // 0x2d0 float:1.009E-42 double:3.557E-321;
        if (r0 < r2) goto L_0x001b;
    L_0x0018:
        r0 = 2621440; // 0x280000 float:3.67342E-39 double:1.2951634E-317;
        goto L_0x000d;
    L_0x001b:
        r0 = java.lang.Math.min(r8, r9);
        r1 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r0 < r1) goto L_0x002d;
    L_0x0023:
        r0 = 1000000; // 0xvar_ float:1.401298E-39 double:4.940656E-318;
        r1 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
        r2 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
        goto L_0x0036;
    L_0x002d:
        r0 = 750000; // 0xb71b0 float:1.050974E-39 double:3.70549E-318;
        r1 = NUM; // 0x3var_a float:0.6 double:5.230388065E-315;
        r2 = NUM; // 0x3var_ float:0.7 double:5.23867711E-315;
    L_0x0036:
        r3 = (float) r7;
        r5 = (float) r5;
        r4 = (float) r8;
        r5 = r5 / r4;
        r6 = (float) r6;
        r4 = (float) r9;
        r6 = r6 / r4;
        r5 = java.lang.Math.min(r5, r6);
        r3 = r3 / r5;
        r5 = (int) r3;
        r5 = (float) r5;
        r5 = r5 * r1;
        r5 = (int) r5;
        r6 = getVideoBitrateWithFactor(r2);
        r6 = (float) r6;
        r1 = NUM; // 0x49610000 float:921600.0 double:6.082411336E-315;
        r9 = r9 * r8;
        r8 = (float) r9;
        r1 = r1 / r8;
        r6 = r6 / r1;
        r6 = (int) r6;
        if (r7 >= r6) goto L_0x0057;
    L_0x0056:
        return r5;
    L_0x0057:
        if (r5 <= r0) goto L_0x005a;
    L_0x0059:
        return r0;
    L_0x005a:
        if (r5 >= r6) goto L_0x005d;
    L_0x005c:
        return r6;
    L_0x005d:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.makeVideoBitrate(int, int, int, int, int):int");
    }
}
