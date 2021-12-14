package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.gms.internal.vision.zzhv$$ExternalSyntheticBackport0;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputDocument;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_encryptedChat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.PhotoViewer;

public class MediaController implements AudioManager.OnAudioFocusChangeListener, NotificationCenter.NotificationCenterDelegate, SensorEventListener {
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
    public static ArrayList<AlbumEntry> allMediaAlbums = new ArrayList<>();
    public static ArrayList<AlbumEntry> allPhotoAlbums = new ArrayList<>();
    public static AlbumEntry allPhotosAlbumEntry;
    public static AlbumEntry allVideosAlbumEntry;
    private static Runnable broadcastPhotosRunnable;
    private static final String[] projectionPhotos;
    private static final String[] projectionVideo;
    /* access modifiers changed from: private */
    public static Runnable refreshGalleryRunnable;
    private Sensor accelerometerSensor;
    private boolean accelerometerVertical;
    private boolean allowStartRecord;
    private int audioFocus = 0;
    private AudioInfo audioInfo;
    /* access modifiers changed from: private */
    public VideoPlayer audioPlayer = null;
    AudioManager.OnAudioFocusChangeListener audioRecordFocusChangedListener = new MediaController$$ExternalSyntheticLambda2(this);
    /* access modifiers changed from: private */
    public AudioRecord audioRecorder;
    /* access modifiers changed from: private */
    public float audioVolume;
    private ValueAnimator audioVolumeAnimator;
    private final ValueAnimator.AnimatorUpdateListener audioVolumeUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float unused = MediaController.this.audioVolume = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            MediaController.this.setPlayerVolume();
        }
    };
    /* access modifiers changed from: private */
    public Activity baseActivity;
    /* access modifiers changed from: private */
    public boolean callInProgress;
    private int countLess;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout currentAspectRatioFrameLayout;
    /* access modifiers changed from: private */
    public float currentAspectRatioFrameLayoutRatio;
    private boolean currentAspectRatioFrameLayoutReady;
    /* access modifiers changed from: private */
    public int currentAspectRatioFrameLayoutRotation;
    private float currentMusicPlaybackSpeed = 1.0f;
    private float currentPlaybackSpeed = 1.0f;
    private int currentPlaylistNum;
    /* access modifiers changed from: private */
    public TextureView currentTextureView;
    /* access modifiers changed from: private */
    public FrameLayout currentTextureViewContainer;
    private boolean downloadingCurrentMessage;
    /* access modifiers changed from: private */
    public VideoPlayer emojiSoundPlayer = null;
    /* access modifiers changed from: private */
    public int emojiSoundPlayerNum = 0;
    /* access modifiers changed from: private */
    public ExternalObserver externalObserver;
    private float fastMusicPlaybackSpeed = 1.0f;
    private float fastPlaybackSpeed = 1.0f;
    private View feedbackView;
    /* access modifiers changed from: private */
    public ByteBuffer fileBuffer;
    /* access modifiers changed from: private */
    public DispatchQueue fileEncodingQueue;
    private BaseFragment flagSecureFragment;
    private boolean forceLoopCurrentPlaylist;
    private HashMap<String, MessageObject> generatingWaveform = new HashMap<>();
    private MessageObject goingToShowMessageObject;
    private float[] gravity = new float[3];
    private float[] gravityFast = new float[3];
    private Sensor gravitySensor;
    private int hasAudioFocus;
    private boolean hasRecordAudioFocus;
    private boolean ignoreOnPause;
    private boolean ignoreProximity;
    private boolean inputFieldHasText;
    /* access modifiers changed from: private */
    public InternalObserver internalObserver;
    /* access modifiers changed from: private */
    public boolean isDrawingWasReady;
    /* access modifiers changed from: private */
    public boolean isPaused = false;
    private boolean isStreamingCurrentAudio;
    private int lastChatAccount;
    private long lastChatEnterTime;
    private long lastChatLeaveTime;
    private ArrayList<Long> lastChatVisibleMessages;
    private long lastMediaCheckTime;
    private int lastMessageId;
    /* access modifiers changed from: private */
    public long lastProgress = 0;
    private float lastProximityValue = -100.0f;
    /* access modifiers changed from: private */
    public long lastSaveTime;
    private TLRPC$EncryptedChat lastSecretChat;
    private long lastTimestamp = 0;
    private TLRPC$User lastUser;
    private float[] linearAcceleration = new float[3];
    private Sensor linearSensor;
    private boolean loadingPlaylist;
    private String[] mediaProjections;
    /* access modifiers changed from: private */
    public PipRoundVideoView pipRoundVideoView;
    /* access modifiers changed from: private */
    public int pipSwitchingState;
    private boolean playMusicAgain;
    /* access modifiers changed from: private */
    public int playerNum;
    private boolean playerWasReady;
    /* access modifiers changed from: private */
    public MessageObject playingMessageObject;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> playlist = new ArrayList<>();
    private int playlistClassGuid;
    private boolean[] playlistEndReached = {false, false};
    private PlaylistGlobalSearchParams playlistGlobalSearchParams;
    private HashMap<Integer, MessageObject> playlistMap = new HashMap<>();
    private int[] playlistMaxId = {Integer.MAX_VALUE, Integer.MAX_VALUE};
    private long playlistMergeDialogId;
    private float previousAccValue;
    private Timer progressTimer = null;
    private final Object progressTimerSync = new Object();
    private boolean proximityHasDifferentValues;
    private Sensor proximitySensor;
    private boolean proximityTouched;
    private PowerManager.WakeLock proximityWakeLock;
    private ChatActivity raiseChat;
    private boolean raiseToEarRecord;
    private int raisedToBack;
    private int raisedToTop;
    private int raisedToTopSign;
    public int recordBufferSize = 1280;
    /* access modifiers changed from: private */
    public ArrayList<ByteBuffer> recordBuffers = new ArrayList<>();
    private long recordDialogId;
    /* access modifiers changed from: private */
    public DispatchQueue recordQueue;
    private MessageObject recordReplyingMsg;
    private MessageObject recordReplyingTopMsg;
    /* access modifiers changed from: private */
    public Runnable recordRunnable = new Runnable() {
        /* JADX WARNING: Removed duplicated region for block: B:36:0x00d5 A[SYNTHETIC, Splitter:B:36:0x00d5] */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x0115  */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x00e9 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r18 = this;
                r1 = r18
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                android.media.AudioRecord r0 = r0.audioRecorder
                if (r0 == 0) goto L_0x0163
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                boolean r0 = r0.isEmpty()
                r2 = 0
                if (r0 != 0) goto L_0x002d
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                java.lang.Object r0 = r0.get(r2)
                java.nio.ByteBuffer r0 = (java.nio.ByteBuffer) r0
                org.telegram.messenger.MediaController r3 = org.telegram.messenger.MediaController.this
                java.util.ArrayList r3 = r3.recordBuffers
                r3.remove(r2)
                goto L_0x003c
            L_0x002d:
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                int r0 = r0.recordBufferSize
                java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r0)
                java.nio.ByteOrder r3 = java.nio.ByteOrder.nativeOrder()
                r0.order(r3)
            L_0x003c:
                r3 = r0
                r3.rewind()
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                android.media.AudioRecord r0 = r0.audioRecorder
                int r4 = r3.capacity()
                int r4 = r0.read(r3, r4)
                if (r4 <= 0) goto L_0x013c
                r3.limit(r4)
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f6 }
                long r7 = r0.samplesCount     // Catch:{ Exception -> 0x00f6 }
                int r0 = r4 / 2
                long r9 = (long) r0     // Catch:{ Exception -> 0x00f6 }
                long r7 = r7 + r9
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f6 }
                long r9 = r0.samplesCount     // Catch:{ Exception -> 0x00f6 }
                double r9 = (double) r9
                double r11 = (double) r7
                java.lang.Double.isNaN(r9)
                java.lang.Double.isNaN(r11)
                double r9 = r9 / r11
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f6 }
                short[] r0 = r0.recordSamples     // Catch:{ Exception -> 0x00f6 }
                int r0 = r0.length     // Catch:{ Exception -> 0x00f6 }
                double r11 = (double) r0
                java.lang.Double.isNaN(r11)
                double r9 = r9 * r11
                int r0 = (int) r9
                org.telegram.messenger.MediaController r9 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f6 }
                short[] r9 = r9.recordSamples     // Catch:{ Exception -> 0x00f6 }
                int r9 = r9.length     // Catch:{ Exception -> 0x00f6 }
                int r9 = r9 - r0
                r10 = 0
                if (r0 == 0) goto L_0x00a8
                org.telegram.messenger.MediaController r11 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f6 }
                short[] r11 = r11.recordSamples     // Catch:{ Exception -> 0x00f6 }
                int r11 = r11.length     // Catch:{ Exception -> 0x00f6 }
                float r11 = (float) r11     // Catch:{ Exception -> 0x00f6 }
                float r12 = (float) r0     // Catch:{ Exception -> 0x00f6 }
                float r11 = r11 / r12
                r12 = 0
                r13 = 0
            L_0x0091:
                if (r12 >= r0) goto L_0x00a8
                org.telegram.messenger.MediaController r14 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f6 }
                short[] r14 = r14.recordSamples     // Catch:{ Exception -> 0x00f6 }
                org.telegram.messenger.MediaController r15 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f6 }
                short[] r15 = r15.recordSamples     // Catch:{ Exception -> 0x00f6 }
                int r5 = (int) r13     // Catch:{ Exception -> 0x00f6 }
                short r5 = r15[r5]     // Catch:{ Exception -> 0x00f6 }
                r14[r12] = r5     // Catch:{ Exception -> 0x00f6 }
                float r13 = r13 + r11
                int r12 = r12 + 1
                goto L_0x0091
            L_0x00a8:
                float r5 = (float) r4
                r6 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 / r6
                float r6 = (float) r9
                float r5 = r5 / r6
                r6 = 0
                r16 = 0
            L_0x00b1:
                int r9 = r4 / 2
                if (r6 >= r9) goto L_0x00ec
                short r9 = r3.getShort()     // Catch:{ Exception -> 0x00f2 }
                int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00f2 }
                r12 = 21
                if (r11 >= r12) goto L_0x00ca
                r11 = 2500(0x9c4, float:3.503E-42)
                if (r9 <= r11) goto L_0x00d2
                int r11 = r9 * r9
                double r11 = (double) r11
                java.lang.Double.isNaN(r11)
                goto L_0x00d0
            L_0x00ca:
                int r11 = r9 * r9
                double r11 = (double) r11
                java.lang.Double.isNaN(r11)
            L_0x00d0:
                double r16 = r16 + r11
            L_0x00d2:
                int r11 = (int) r10
                if (r6 != r11) goto L_0x00e9
                org.telegram.messenger.MediaController r11 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f2 }
                short[] r11 = r11.recordSamples     // Catch:{ Exception -> 0x00f2 }
                int r11 = r11.length     // Catch:{ Exception -> 0x00f2 }
                if (r0 >= r11) goto L_0x00e9
                org.telegram.messenger.MediaController r11 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f2 }
                short[] r11 = r11.recordSamples     // Catch:{ Exception -> 0x00f2 }
                r11[r0] = r9     // Catch:{ Exception -> 0x00f2 }
                float r10 = r10 + r5
                int r0 = r0 + 1
            L_0x00e9:
                int r6 = r6 + 1
                goto L_0x00b1
            L_0x00ec:
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00f2 }
                long unused = r0.samplesCount = r7     // Catch:{ Exception -> 0x00f2 }
                goto L_0x00fe
            L_0x00f2:
                r0 = move-exception
                r5 = r16
                goto L_0x00f9
            L_0x00f6:
                r0 = move-exception
                r5 = 0
            L_0x00f9:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r16 = r5
            L_0x00fe:
                r3.position(r2)
                double r5 = (double) r4
                java.lang.Double.isNaN(r5)
                double r16 = r16 / r5
                r5 = 4611686018427387904(0xNUM, double:2.0)
                double r16 = r16 / r5
                double r5 = java.lang.Math.sqrt(r16)
                int r0 = r3.capacity()
                if (r4 == r0) goto L_0x0116
                r2 = 1
            L_0x0116:
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                org.telegram.messenger.DispatchQueue r0 = r0.fileEncodingQueue
                org.telegram.messenger.MediaController$2$$ExternalSyntheticLambda2 r4 = new org.telegram.messenger.MediaController$2$$ExternalSyntheticLambda2
                r4.<init>(r1, r3, r2)
                r0.postRunnable(r4)
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                org.telegram.messenger.DispatchQueue r0 = r0.recordQueue
                org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.this
                java.lang.Runnable r2 = r2.recordRunnable
                r0.postRunnable(r2)
                org.telegram.messenger.MediaController$2$$ExternalSyntheticLambda0 r0 = new org.telegram.messenger.MediaController$2$$ExternalSyntheticLambda0
                r0.<init>(r1, r5)
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                goto L_0x0163
            L_0x013c:
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                r0.add(r3)
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                int r0 = r0.sendAfterDone
                r2 = 3
                if (r0 == r2) goto L_0x0163
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                int r2 = r0.sendAfterDone
                org.telegram.messenger.MediaController r3 = org.telegram.messenger.MediaController.this
                boolean r3 = r3.sendAfterDoneNotify
                org.telegram.messenger.MediaController r4 = org.telegram.messenger.MediaController.this
                int r4 = r4.sendAfterDoneScheduleDate
                r0.stopRecordingInternal(r2, r3, r4)
            L_0x0163:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.AnonymousClass2.run():void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$1(ByteBuffer byteBuffer, boolean z) {
            int i;
            while (byteBuffer.hasRemaining()) {
                if (byteBuffer.remaining() > MediaController.this.fileBuffer.remaining()) {
                    i = byteBuffer.limit();
                    byteBuffer.limit(MediaController.this.fileBuffer.remaining() + byteBuffer.position());
                } else {
                    i = -1;
                }
                MediaController.this.fileBuffer.put(byteBuffer);
                if (MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit() || z) {
                    MediaController mediaController = MediaController.this;
                    if (mediaController.writeFrame(mediaController.fileBuffer, !z ? MediaController.this.fileBuffer.limit() : byteBuffer.position()) != 0) {
                        MediaController.this.fileBuffer.rewind();
                        MediaController mediaController2 = MediaController.this;
                        MediaController.access$1614(mediaController2, (long) ((mediaController2.fileBuffer.limit() / 2) / (MediaController.this.sampleRate / 1000)));
                    }
                }
                if (i != -1) {
                    byteBuffer.limit(i);
                }
            }
            MediaController.this.recordQueue.postRunnable(new MediaController$2$$ExternalSyntheticLambda1(this, byteBuffer));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0(ByteBuffer byteBuffer) {
            MediaController.this.recordBuffers.add(byteBuffer);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$2(double d) {
            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(MediaController.this.recordingGuid), Double.valueOf(d));
        }
    };
    /* access modifiers changed from: private */
    public short[] recordSamples = new short[1024];
    /* access modifiers changed from: private */
    public Runnable recordStartRunnable;
    private long recordStartTime;
    private long recordTimeCount;
    /* access modifiers changed from: private */
    public TLRPC$TL_document recordingAudio;
    private File recordingAudioFile;
    /* access modifiers changed from: private */
    public int recordingCurrentAccount;
    /* access modifiers changed from: private */
    public int recordingGuid = -1;
    private boolean resumeAudioOnFocusGain;
    public int sampleRate = 16000;
    /* access modifiers changed from: private */
    public long samplesCount;
    /* access modifiers changed from: private */
    public float seekToProgressPending;
    /* access modifiers changed from: private */
    public int sendAfterDone;
    /* access modifiers changed from: private */
    public boolean sendAfterDoneNotify;
    /* access modifiers changed from: private */
    public int sendAfterDoneScheduleDate;
    private SensorManager sensorManager;
    private boolean sensorsStarted;
    private Runnable setLoadingRunnable = new Runnable() {
        public void run() {
            if (MediaController.this.playingMessageObject != null) {
                FileLoader.getInstance(MediaController.this.playingMessageObject.currentAccount).setLoadingVideo(MediaController.this.playingMessageObject.getDocument(), true, false);
            }
        }
    };
    /* access modifiers changed from: private */
    public String shouldSavePositionForCurrentAudio;
    private ArrayList<MessageObject> shuffledPlaylist = new ArrayList<>();
    /* access modifiers changed from: private */
    public int startObserverToken;
    private StopMediaObserverRunnable stopMediaObserverRunnable;
    /* access modifiers changed from: private */
    public final Object sync = new Object();
    private long timeSinceRaise;
    private boolean useFrontSpeaker;
    private ArrayList<VideoConvertMessage> videoConvertQueue = new ArrayList<>();
    private final Object videoConvertSync = new Object();
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;
    private final Object videoQueueSync = new Object();
    private ArrayList<MessageObject> voiceMessagesPlaylist;
    private SparseArray<MessageObject> voiceMessagesPlaylistMap;
    private boolean voiceMessagesPlaylistUnread;

    public static class AudioEntry {
        public String author;
        public int duration;
        public String genre;
        public long id;
        public MessageObject messageObject;
        public String path;
        public String title;
    }

    public static class CropState {
        public float cropPh = 1.0f;
        public float cropPw = 1.0f;
        public float cropPx;
        public float cropPy;
        public float cropRotate;
        public float cropScale = 1.0f;
        public boolean freeform;
        public int height;
        public boolean initied;
        public float lockedAspectRatio;
        public Matrix matrix;
        public boolean mirrored;
        public float scale;
        public float stateScale;
        public int transformHeight;
        public int transformRotation;
        public int transformWidth;
        public int width;
    }

    public static class SavedFilterState {
        public float blurAngle;
        public float blurExcludeBlurSize;
        public Point blurExcludePoint;
        public float blurExcludeSize;
        public int blurType;
        public float contrastValue;
        public PhotoFilterView.CurvesToolValue curvesToolValue = new PhotoFilterView.CurvesToolValue();
        public float enhanceValue;
        public float exposureValue;
        public float fadeValue;
        public float grainValue;
        public float highlightsValue;
        public float saturationValue;
        public float shadowsValue;
        public float sharpenValue;
        public float softenSkinValue;
        public int tintHighlightsColor;
        public int tintShadowsColor;
        public float vignetteValue;
        public float warmthValue;
    }

    public interface VideoConvertorListener {
        boolean checkConversionCanceled();

        void didWriteData(long j, float f);
    }

    private static int getVideoBitrateWithFactor(float f) {
        return (int) (f * 2000.0f * 1000.0f * 1.13f);
    }

    public static native int isOpusFile(String str);

    private static boolean isRecognizedFormat(int i) {
        if (i == 39 || i == NUM) {
            return true;
        }
        switch (i) {
            case 19:
            case 20:
            case 21:
                return true;
            default:
                return false;
        }
    }

    private native int startRecord(String str, int i);

    private native void stopRecord();

    /* access modifiers changed from: private */
    public native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    static /* synthetic */ long access$1614(MediaController mediaController, long j) {
        long j2 = mediaController.recordTimeCount + j;
        mediaController.recordTimeCount = j2;
        return j2;
    }

    public boolean isBuffering() {
        VideoPlayer videoPlayer2 = this.audioPlayer;
        if (videoPlayer2 != null) {
            return videoPlayer2.isBuffering();
        }
        return false;
    }

    private static class AudioBuffer {
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

    static {
        String[] strArr = new String[9];
        strArr[0] = "_id";
        strArr[1] = "bucket_id";
        strArr[2] = "bucket_display_name";
        strArr[3] = "_data";
        int i = Build.VERSION.SDK_INT;
        String str = "date_modified";
        strArr[4] = i > 28 ? str : "datetaken";
        strArr[5] = "orientation";
        strArr[6] = "width";
        strArr[7] = "height";
        strArr[8] = "_size";
        projectionPhotos = strArr;
        String[] strArr2 = new String[9];
        strArr2[0] = "_id";
        strArr2[1] = "bucket_id";
        strArr2[2] = "bucket_display_name";
        strArr2[3] = "_data";
        if (i <= 28) {
            str = "datetaken";
        }
        strArr2[4] = str;
        strArr2[5] = "duration";
        strArr2[6] = "width";
        strArr2[7] = "height";
        strArr2[8] = "_size";
        projectionVideo = strArr2;
    }

    public static class AlbumEntry {
        public int bucketId;
        public String bucketName;
        public PhotoEntry coverPhoto;
        public ArrayList<PhotoEntry> photos = new ArrayList<>();
        public SparseArray<PhotoEntry> photosByIds = new SparseArray<>();
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

    public static class MediaEditState {
        public long averageDuration;
        public CharSequence caption;
        public CropState cropState;
        public ArrayList<VideoEditedInfo.MediaEntity> croppedMediaEntities;
        public String croppedPaintPath;
        public VideoEditedInfo editedInfo;
        public ArrayList<TLRPC$MessageEntity> entities;
        public String filterPath;
        public String fullPaintPath;
        public String imagePath;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isPainted;
        public ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
        public String paintPath;
        public SavedFilterState savedFilterState;
        public ArrayList<TLRPC$InputDocument> stickers;
        public String thumbPath;
        public int ttl;

        public String getPath() {
            return null;
        }

        public void reset() {
            this.caption = null;
            this.thumbPath = null;
            this.filterPath = null;
            this.imagePath = null;
            this.paintPath = null;
            this.croppedPaintPath = null;
            this.isFiltered = false;
            this.isPainted = false;
            this.isCropped = false;
            this.ttl = 0;
            this.mediaEntities = null;
            this.editedInfo = null;
            this.entities = null;
            this.savedFilterState = null;
            this.stickers = null;
            this.cropState = null;
        }

        public void copyFrom(MediaEditState mediaEditState) {
            this.caption = mediaEditState.caption;
            this.thumbPath = mediaEditState.thumbPath;
            this.imagePath = mediaEditState.imagePath;
            this.filterPath = mediaEditState.filterPath;
            this.paintPath = mediaEditState.paintPath;
            this.croppedPaintPath = mediaEditState.croppedPaintPath;
            this.fullPaintPath = mediaEditState.fullPaintPath;
            this.entities = mediaEditState.entities;
            this.savedFilterState = mediaEditState.savedFilterState;
            this.mediaEntities = mediaEditState.mediaEntities;
            this.croppedMediaEntities = mediaEditState.croppedMediaEntities;
            this.stickers = mediaEditState.stickers;
            this.editedInfo = mediaEditState.editedInfo;
            this.averageDuration = mediaEditState.averageDuration;
            this.isFiltered = mediaEditState.isFiltered;
            this.isPainted = mediaEditState.isPainted;
            this.isCropped = mediaEditState.isCropped;
            this.ttl = mediaEditState.ttl;
            this.cropState = mediaEditState.cropState;
        }
    }

    public static class PhotoEntry extends MediaEditState {
        public int bucketId;
        public boolean canDeleteAfter;
        public long dateTaken;
        public int duration;
        public int height;
        public int imageId;
        public boolean isMuted;
        public boolean isVideo;
        public int orientation;
        public String path;
        public long size;
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

        public String getPath() {
            return this.path;
        }

        public void reset() {
            if (this.isVideo && this.filterPath != null) {
                new File(this.filterPath).delete();
                this.filterPath = null;
            }
            super.reset();
        }
    }

    public static class SearchImage extends MediaEditState {
        public CharSequence caption;
        public int date;
        public TLRPC$Document document;
        public int height;
        public String id;
        public String imageUrl;
        public TLRPC$BotInlineResult inlineResult;
        public HashMap<String, String> params;
        public TLRPC$Photo photo;
        public TLRPC$PhotoSize photoSize;
        public int size;
        public TLRPC$PhotoSize thumbPhotoSize;
        public String thumbUrl;
        public int type;
        public int width;

        public String getPath() {
            TLRPC$PhotoSize tLRPC$PhotoSize = this.photoSize;
            if (tLRPC$PhotoSize != null) {
                return FileLoader.getPathToAttach(tLRPC$PhotoSize, true).getAbsolutePath();
            }
            TLRPC$Document tLRPC$Document = this.document;
            if (tLRPC$Document != null) {
                return FileLoader.getPathToAttach(tLRPC$Document, true).getAbsolutePath();
            }
            return ImageLoader.getHttpFilePath(this.imageUrl, "jpg").getAbsolutePath();
        }

        public void reset() {
            super.reset();
        }

        public String getAttachName() {
            TLRPC$PhotoSize tLRPC$PhotoSize = this.photoSize;
            if (tLRPC$PhotoSize != null) {
                return FileLoader.getAttachFileName(tLRPC$PhotoSize);
            }
            TLRPC$Document tLRPC$Document = this.document;
            if (tLRPC$Document != null) {
                return FileLoader.getAttachFileName(tLRPC$Document);
            }
            return Utilities.MD5(this.imageUrl) + "." + ImageLoader.getHttpUrlExtension(this.imageUrl, "jpg");
        }

        public String getPathToAttach() {
            TLRPC$PhotoSize tLRPC$PhotoSize = this.photoSize;
            if (tLRPC$PhotoSize != null) {
                return FileLoader.getPathToAttach(tLRPC$PhotoSize, true).getAbsolutePath();
            }
            TLRPC$Document tLRPC$Document = this.document;
            if (tLRPC$Document != null) {
                return FileLoader.getPathToAttach(tLRPC$Document, true).getAbsolutePath();
            }
            return this.imageUrl;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        if (i != 1) {
            this.hasRecordAudioFocus = false;
        }
    }

    private static class VideoConvertMessage {
        public int currentAccount;
        public MessageObject messageObject;
        public VideoEditedInfo videoEditedInfo;

        public VideoConvertMessage(MessageObject messageObject2, VideoEditedInfo videoEditedInfo2) {
            this.messageObject = messageObject2;
            this.currentAccount = messageObject2.currentAccount;
            this.videoEditedInfo = videoEditedInfo2;
        }
    }

    private class InternalObserver extends ContentObserver {
        public InternalObserver() {
            super((Handler) null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            MediaController.this.processMediaObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        }
    }

    private class ExternalObserver extends ContentObserver {
        public ExternalObserver() {
            super((Handler) null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            MediaController.this.processMediaObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
    }

    private static class GalleryObserverInternal extends ContentObserver {
        public GalleryObserverInternal() {
            super((Handler) null);
        }

        private void scheduleReloadRunnable() {
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new MediaController$GalleryObserverInternal$$ExternalSyntheticLambda0(this), 2000);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$scheduleReloadRunnable$0() {
            if (PhotoViewer.getInstance().isVisible()) {
                scheduleReloadRunnable();
                return;
            }
            Runnable unused = MediaController.refreshGalleryRunnable = null;
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

    private static class GalleryObserverExternal extends ContentObserver {
        public GalleryObserverExternal() {
            super((Handler) null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = MediaController$GalleryObserverExternal$$ExternalSyntheticLambda0.INSTANCE, 2000);
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$onChange$0() {
            Runnable unused = MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public static void checkGallery() {
        AlbumEntry albumEntry;
        if (Build.VERSION.SDK_INT >= 24 && (albumEntry = allPhotosAlbumEntry) != null) {
            Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda4(albumEntry.photos.size()), 2000);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006d, code lost:
        if (r5 != null) goto L_0x006f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x006f, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0077, code lost:
        if (r5 == null) goto L_0x007a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007a, code lost:
        if (r13 == r6) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x007c, code lost:
        r13 = refreshGalleryRunnable;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x007e, code lost:
        if (r13 == null) goto L_0x0085;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0080, code lost:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r13);
        refreshGalleryRunnable = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0085, code lost:
        loadGalleryPhotosAlbums(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:?, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0041 A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$checkGallery$1(int r13) {
        /*
            java.lang.String r0 = "COUNT(_id)"
            java.lang.String r1 = "android.permission.READ_EXTERNAL_STORAGE"
            r2 = 1
            r3 = 0
            r4 = 0
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x003a }
            int r5 = r5.checkSelfPermission(r1)     // Catch:{ all -> 0x003a }
            if (r5 != 0) goto L_0x0032
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x003a }
            android.content.ContentResolver r6 = r5.getContentResolver()     // Catch:{ all -> 0x003a }
            android.net.Uri r7 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x003a }
            java.lang.String[] r8 = new java.lang.String[r2]     // Catch:{ all -> 0x003a }
            r8[r4] = r0     // Catch:{ all -> 0x003a }
            r9 = 0
            r10 = 0
            r11 = 0
            android.database.Cursor r5 = android.provider.MediaStore.Images.Media.query(r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x003a }
            if (r5 == 0) goto L_0x0033
            boolean r6 = r5.moveToNext()     // Catch:{ all -> 0x0030 }
            if (r6 == 0) goto L_0x0033
            int r6 = r5.getInt(r4)     // Catch:{ all -> 0x0030 }
            int r6 = r6 + r4
            goto L_0x0034
        L_0x0030:
            r6 = move-exception
            goto L_0x003c
        L_0x0032:
            r5 = r3
        L_0x0033:
            r6 = 0
        L_0x0034:
            if (r5 == 0) goto L_0x0045
            r5.close()
            goto L_0x0045
        L_0x003a:
            r6 = move-exception
            r5 = r3
        L_0x003c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x0090 }
            if (r5 == 0) goto L_0x0044
            r5.close()
        L_0x0044:
            r6 = 0
        L_0x0045:
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0073 }
            int r1 = r7.checkSelfPermission(r1)     // Catch:{ all -> 0x0073 }
            if (r1 != 0) goto L_0x006d
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0073 }
            android.content.ContentResolver r7 = r1.getContentResolver()     // Catch:{ all -> 0x0073 }
            android.net.Uri r8 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x0073 }
            java.lang.String[] r9 = new java.lang.String[r2]     // Catch:{ all -> 0x0073 }
            r9[r4] = r0     // Catch:{ all -> 0x0073 }
            r10 = 0
            r11 = 0
            r12 = 0
            android.database.Cursor r5 = android.provider.MediaStore.Images.Media.query(r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x0073 }
            if (r5 == 0) goto L_0x006d
            boolean r0 = r5.moveToNext()     // Catch:{ all -> 0x0073 }
            if (r0 == 0) goto L_0x006d
            int r0 = r5.getInt(r4)     // Catch:{ all -> 0x0073 }
            int r6 = r6 + r0
        L_0x006d:
            if (r5 == 0) goto L_0x007a
        L_0x006f:
            r5.close()
            goto L_0x007a
        L_0x0073:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0089 }
            if (r5 == 0) goto L_0x007a
            goto L_0x006f
        L_0x007a:
            if (r13 == r6) goto L_0x0088
            java.lang.Runnable r13 = refreshGalleryRunnable
            if (r13 == 0) goto L_0x0085
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r13)
            refreshGalleryRunnable = r3
        L_0x0085:
            loadGalleryPhotosAlbums(r4)
        L_0x0088:
            return
        L_0x0089:
            r13 = move-exception
            if (r5 == 0) goto L_0x008f
            r5.close()
        L_0x008f:
            throw r13
        L_0x0090:
            r13 = move-exception
            if (r5 == 0) goto L_0x0096
            r5.close()
        L_0x0096:
            goto L_0x0098
        L_0x0097:
            throw r13
        L_0x0098:
            goto L_0x0097
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$checkGallery$1(int):void");
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
                        InternalObserver unused = MediaController.this.internalObserver = null;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    if (MediaController.this.externalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
                        ExternalObserver unused2 = MediaController.this.externalObserver = null;
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
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
        DispatchQueue dispatchQueue = new DispatchQueue("recordQueue");
        this.recordQueue = dispatchQueue;
        dispatchQueue.setPriority(10);
        DispatchQueue dispatchQueue2 = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue = dispatchQueue2;
        dispatchQueue2.setPriority(10);
        this.recordQueue.postRunnable(new MediaController$$ExternalSyntheticLambda9(this));
        Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda13(this));
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda11(this));
        String[] strArr = new String[7];
        strArr[0] = "_data";
        strArr[1] = "_display_name";
        strArr[2] = "bucket_display_name";
        strArr[3] = Build.VERSION.SDK_INT > 28 ? "date_modified" : "datetaken";
        strArr[4] = "title";
        strArr[5] = "width";
        strArr[6] = "height";
        this.mediaProjections = strArr;
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        try {
            contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            contentResolver.registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        try {
            contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        try {
            contentResolver.registerContentObserver(MediaStore.Video.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Exception e4) {
            FileLog.e((Throwable) e4);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        try {
            this.sampleRate = 16000;
            int minBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
            if (minBufferSize <= 0) {
                minBufferSize = 1280;
            }
            this.recordBufferSize = minBufferSize;
            for (int i = 0; i < 5; i++) {
                ByteBuffer allocateDirect = ByteBuffer.allocateDirect(this.recordBufferSize);
                allocateDirect.order(ByteOrder.nativeOrder());
                this.recordBuffers.add(allocateDirect);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3() {
        try {
            this.currentPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("playbackSpeed", 1.0f);
            this.currentMusicPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("musicPlaybackSpeed", 1.0f);
            this.fastPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("fastPlaybackSpeed", 1.8f);
            this.fastMusicPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("fastMusicPlaybackSpeed", 1.8f);
            SensorManager sensorManager2 = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
            this.sensorManager = sensorManager2;
            this.linearSensor = sensorManager2.getDefaultSensor(10);
            Sensor defaultSensor = this.sensorManager.getDefaultSensor(9);
            this.gravitySensor = defaultSensor;
            if (this.linearSensor == null || defaultSensor == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("gravity or linear sensor not found");
                }
                this.accelerometerSensor = this.sensorManager.getDefaultSensor(1);
                this.linearSensor = null;
                this.gravitySensor = null;
            }
            this.proximitySensor = this.sensorManager.getDefaultSensor(8);
            this.proximityWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "telegram:proximity_lock");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            AnonymousClass4 r1 = new PhoneStateListener() {
                public void onCallStateChanged(int i, String str) {
                    AndroidUtilities.runOnUIThread(new MediaController$4$$ExternalSyntheticLambda0(this, i));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onCallStateChanged$0(int i) {
                    if (i == 1) {
                        MediaController mediaController = MediaController.this;
                        if (mediaController.isPlayingMessage(mediaController.playingMessageObject) && !MediaController.this.isMessagePaused()) {
                            MediaController mediaController2 = MediaController.this;
                            mediaController2.lambda$startAudioAgain$7(mediaController2.playingMessageObject);
                        } else if (!(MediaController.this.recordStartRunnable == null && MediaController.this.recordingAudio == null)) {
                            MediaController.this.stopRecording(2, false, 0);
                        }
                        EmbedBottomSheet instance = EmbedBottomSheet.getInstance();
                        if (instance != null) {
                            instance.pause();
                        }
                        boolean unused = MediaController.this.callInProgress = true;
                    } else if (i == 0) {
                        boolean unused2 = MediaController.this.callInProgress = false;
                    } else if (i == 2) {
                        EmbedBottomSheet instance2 = EmbedBottomSheet.getInstance();
                        if (instance2 != null) {
                            instance2.pause();
                        }
                        boolean unused3 = MediaController.this.callInProgress = true;
                    }
                }
            };
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                telephonyManager.listen(r1, 32);
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.didReceiveNewMessages);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.musicDidLoad);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.mediaDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    public void onAudioFocusChange(int i) {
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda15(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAudioFocusChange$5(int i) {
        if (i == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                lambda$startAudioAgain$7(this.playingMessageObject);
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
                lambda$startAudioAgain$7(this.playingMessageObject);
                this.resumeAudioOnFocusGain = true;
            }
        }
        setPlayerVolume();
    }

    /* access modifiers changed from: private */
    public void setPlayerVolume() {
        try {
            float f = this.audioFocus != 1 ? 1.0f : 0.2f;
            VideoPlayer videoPlayer2 = this.audioPlayer;
            if (videoPlayer2 != null) {
                videoPlayer2.setVolume(f * this.audioVolume);
                return;
            }
            VideoPlayer videoPlayer3 = this.videoPlayer;
            if (videoPlayer3 != null) {
                videoPlayer3.setVolume(f);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public VideoPlayer getVideoPlayer() {
        return this.videoPlayer;
    }

    private void startProgressTimer(final MessageObject messageObject) {
        synchronized (this.progressTimerSync) {
            Timer timer = this.progressTimer;
            if (timer != null) {
                try {
                    timer.cancel();
                    this.progressTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            messageObject.getFileName();
            Timer timer2 = new Timer();
            this.progressTimer = timer2;
            timer2.schedule(new TimerTask() {
                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new MediaController$5$$ExternalSyntheticLambda1(this, messageObject));
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$1(MessageObject messageObject) {
                    long j;
                    long j2;
                    float f;
                    float f2;
                    if (!(MediaController.this.audioPlayer == null && MediaController.this.videoPlayer == null) && !MediaController.this.isPaused) {
                        try {
                            if (MediaController.this.videoPlayer != null) {
                                j2 = MediaController.this.videoPlayer.getDuration();
                                j = MediaController.this.videoPlayer.getCurrentPosition();
                                if (j < 0) {
                                    return;
                                }
                                if (j2 > 0) {
                                    float f3 = (float) j2;
                                    f2 = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / f3;
                                    f = ((float) j) / f3;
                                    if (f >= 1.0f) {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                j2 = MediaController.this.audioPlayer.getDuration();
                                j = MediaController.this.audioPlayer.getCurrentPosition();
                                float f4 = j2 >= 0 ? ((float) j) / ((float) j2) : 0.0f;
                                float bufferedPosition = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) j2);
                                if (j2 != -9223372036854775807L && j >= 0) {
                                    if (MediaController.this.seekToProgressPending == 0.0f) {
                                        f = f4;
                                        f2 = bufferedPosition;
                                    } else {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            }
                            long unused = MediaController.this.lastProgress = j;
                            messageObject.audioPlayerDuration = (int) (j2 / 1000);
                            messageObject.audioProgress = f;
                            messageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                            messageObject.bufferedProgress = f2;
                            if (f >= 0.0f && MediaController.this.shouldSavePositionForCurrentAudio != null && SystemClock.elapsedRealtime() - MediaController.this.lastSaveTime >= 1000) {
                                String access$3000 = MediaController.this.shouldSavePositionForCurrentAudio;
                                long unused2 = MediaController.this.lastSaveTime = SystemClock.elapsedRealtime();
                                Utilities.globalQueue.postRunnable(new MediaController$5$$ExternalSyntheticLambda0(access$3000, f));
                            }
                            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(messageObject.getId()), Float.valueOf(f));
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                }
            }, 0, 17);
        }
    }

    private void stopProgressTimer() {
        synchronized (this.progressTimerSync) {
            Timer timer = this.progressTimer;
            if (timer != null) {
                try {
                    timer.cancel();
                    this.progressTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    public void cleanup() {
        cleanupPlayer(true, true);
        this.audioInfo = null;
        this.playMusicAgain = false;
        for (int i = 0; i < 3; i++) {
            DownloadController.getInstance(i).cleanup();
        }
        this.videoConvertQueue.clear();
        this.generatingWaveform.clear();
        this.voiceMessagesPlaylist = null;
        this.voiceMessagesPlaylistMap = null;
        clearPlaylist();
        cancelVideoConvert((MessageObject) null);
    }

    private void clearPlaylist() {
        this.playlist.clear();
        this.playlistMap.clear();
        this.shuffledPlaylist.clear();
        this.playlistClassGuid = 0;
        boolean[] zArr = this.playlistEndReached;
        zArr[1] = false;
        zArr[0] = false;
        this.playlistMergeDialogId = 0;
        int[] iArr = this.playlistMaxId;
        iArr[1] = Integer.MAX_VALUE;
        iArr[0] = Integer.MAX_VALUE;
        this.loadingPlaylist = false;
        this.playlistGlobalSearchParams = null;
    }

    public void startMediaObserver() {
        ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
        this.startObserverToken++;
        try {
            if (this.internalObserver == null) {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ExternalObserver externalObserver2 = new ExternalObserver();
                this.externalObserver = externalObserver2;
                contentResolver.registerContentObserver(uri, false, externalObserver2);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            if (this.externalObserver == null) {
                ContentResolver contentResolver2 = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri2 = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
                InternalObserver internalObserver2 = new InternalObserver();
                this.internalObserver = internalObserver2;
                contentResolver2.registerContentObserver(uri2, false, internalObserver2);
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    public void stopMediaObserver() {
        if (this.stopMediaObserverRunnable == null) {
            this.stopMediaObserverRunnable = new StopMediaObserverRunnable();
        }
        this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
        ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, 5000);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(3:35|36|62) */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0052, code lost:
        if (r2.toLowerCase().contains("screenshot") == false) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005e, code lost:
        if (r4.toLowerCase().contains("screenshot") != false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006a, code lost:
        if (r5.toLowerCase().contains("screenshot") != false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        r14.add(java.lang.Long.valueOf(r6));
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:35:0x00a5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processMediaObserver(android.net.Uri r14) {
        /*
            r13 = this;
            r0 = 0
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ Exception -> 0x00c7 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00c7 }
            android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ Exception -> 0x00c7 }
            java.lang.String[] r5 = r13.mediaProjections     // Catch:{ Exception -> 0x00c7 }
            r6 = 0
            r7 = 0
            java.lang.String r8 = "date_added DESC LIMIT 1"
            r4 = r14
            android.database.Cursor r0 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x00c7 }
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ Exception -> 0x00c7 }
            r14.<init>()     // Catch:{ Exception -> 0x00c7 }
            if (r0 == 0) goto L_0x00b1
        L_0x001d:
            boolean r2 = r0.moveToNext()     // Catch:{ Exception -> 0x00c7 }
            if (r2 == 0) goto L_0x00ae
            r2 = 0
            java.lang.String r2 = r0.getString(r2)     // Catch:{ Exception -> 0x00c7 }
            r3 = 1
            java.lang.String r4 = r0.getString(r3)     // Catch:{ Exception -> 0x00c7 }
            r5 = 2
            java.lang.String r5 = r0.getString(r5)     // Catch:{ Exception -> 0x00c7 }
            r6 = 3
            long r6 = r0.getLong(r6)     // Catch:{ Exception -> 0x00c7 }
            r8 = 4
            java.lang.String r8 = r0.getString(r8)     // Catch:{ Exception -> 0x00c7 }
            r9 = 5
            int r9 = r0.getInt(r9)     // Catch:{ Exception -> 0x00c7 }
            r10 = 6
            int r10 = r0.getInt(r10)     // Catch:{ Exception -> 0x00c7 }
            java.lang.String r11 = "screenshot"
            if (r2 == 0) goto L_0x0054
            java.lang.String r12 = r2.toLowerCase()     // Catch:{ Exception -> 0x00c7 }
            boolean r12 = r12.contains(r11)     // Catch:{ Exception -> 0x00c7 }
            if (r12 != 0) goto L_0x0078
        L_0x0054:
            if (r4 == 0) goto L_0x0060
            java.lang.String r4 = r4.toLowerCase()     // Catch:{ Exception -> 0x00c7 }
            boolean r4 = r4.contains(r11)     // Catch:{ Exception -> 0x00c7 }
            if (r4 != 0) goto L_0x0078
        L_0x0060:
            if (r5 == 0) goto L_0x006c
            java.lang.String r4 = r5.toLowerCase()     // Catch:{ Exception -> 0x00c7 }
            boolean r4 = r4.contains(r11)     // Catch:{ Exception -> 0x00c7 }
            if (r4 != 0) goto L_0x0078
        L_0x006c:
            if (r8 == 0) goto L_0x001d
            java.lang.String r4 = r8.toLowerCase()     // Catch:{ Exception -> 0x00c7 }
            boolean r4 = r4.contains(r11)     // Catch:{ Exception -> 0x00c7 }
            if (r4 == 0) goto L_0x001d
        L_0x0078:
            if (r9 == 0) goto L_0x007c
            if (r10 != 0) goto L_0x008a
        L_0x007c:
            android.graphics.BitmapFactory$Options r4 = new android.graphics.BitmapFactory$Options     // Catch:{ Exception -> 0x00a5 }
            r4.<init>()     // Catch:{ Exception -> 0x00a5 }
            r4.inJustDecodeBounds = r3     // Catch:{ Exception -> 0x00a5 }
            android.graphics.BitmapFactory.decodeFile(r2, r4)     // Catch:{ Exception -> 0x00a5 }
            int r9 = r4.outWidth     // Catch:{ Exception -> 0x00a5 }
            int r10 = r4.outHeight     // Catch:{ Exception -> 0x00a5 }
        L_0x008a:
            if (r9 <= 0) goto L_0x009c
            if (r10 <= 0) goto L_0x009c
            int r2 = r1.x     // Catch:{ Exception -> 0x00a5 }
            if (r9 != r2) goto L_0x0096
            int r3 = r1.y     // Catch:{ Exception -> 0x00a5 }
            if (r10 == r3) goto L_0x009c
        L_0x0096:
            if (r10 != r2) goto L_0x001d
            int r2 = r1.y     // Catch:{ Exception -> 0x00a5 }
            if (r9 != r2) goto L_0x001d
        L_0x009c:
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x00a5 }
            r14.add(r2)     // Catch:{ Exception -> 0x00a5 }
            goto L_0x001d
        L_0x00a5:
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x00c7 }
            r14.add(r2)     // Catch:{ Exception -> 0x00c7 }
            goto L_0x001d
        L_0x00ae:
            r0.close()     // Catch:{ Exception -> 0x00c7 }
        L_0x00b1:
            boolean r1 = r14.isEmpty()     // Catch:{ Exception -> 0x00c7 }
            if (r1 != 0) goto L_0x00bf
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda27 r1 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda27     // Catch:{ Exception -> 0x00c7 }
            r1.<init>(r13, r14)     // Catch:{ Exception -> 0x00c7 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ Exception -> 0x00c7 }
        L_0x00bf:
            if (r0 == 0) goto L_0x00ce
        L_0x00c1:
            r0.close()     // Catch:{ Exception -> 0x00ce }
            goto L_0x00ce
        L_0x00c5:
            r14 = move-exception
            goto L_0x00cf
        L_0x00c7:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)     // Catch:{ all -> 0x00c5 }
            if (r0 == 0) goto L_0x00ce
            goto L_0x00c1
        L_0x00ce:
            return
        L_0x00cf:
            if (r0 == 0) goto L_0x00d4
            r0.close()     // Catch:{ Exception -> 0x00d4 }
        L_0x00d4:
            goto L_0x00d6
        L_0x00d5:
            throw r14
        L_0x00d6:
            goto L_0x00d5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.processMediaObserver(android.net.Uri):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processMediaObserver$6(ArrayList arrayList) {
        NotificationCenter.getInstance(this.lastChatAccount).postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
        checkScreenshots(arrayList);
    }

    private void checkScreenshots(ArrayList<Long> arrayList) {
        if (arrayList != null && !arrayList.isEmpty() && this.lastChatEnterTime != 0) {
            if (this.lastUser != null || (this.lastSecretChat instanceof TLRPC$TL_encryptedChat)) {
                boolean z = false;
                for (int i = 0; i < arrayList.size(); i++) {
                    Long l = arrayList.get(i);
                    if ((this.lastMediaCheckTime == 0 || l.longValue() > this.lastMediaCheckTime) && l.longValue() >= this.lastChatEnterTime && (this.lastChatLeaveTime == 0 || l.longValue() <= this.lastChatLeaveTime + 2000)) {
                        this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, l.longValue());
                        z = true;
                    }
                }
                if (!z) {
                    return;
                }
                if (this.lastSecretChat != null) {
                    SecretChatHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastSecretChat, this.lastChatVisibleMessages, (TLRPC$Message) null);
                } else {
                    SendMessagesHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastUser, this.lastMessageId, (TLRPC$Message) null);
                }
            }
        }
    }

    public void setLastVisibleMessageIds(int i, long j, long j2, TLRPC$User tLRPC$User, TLRPC$EncryptedChat tLRPC$EncryptedChat, ArrayList<Long> arrayList, int i2) {
        this.lastChatEnterTime = j;
        this.lastChatLeaveTime = j2;
        this.lastChatAccount = i;
        this.lastSecretChat = tLRPC$EncryptedChat;
        this.lastUser = tLRPC$User;
        this.lastMessageId = i2;
        this.lastChatVisibleMessages = arrayList;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ArrayList<MessageObject> arrayList;
        int indexOf;
        int i3 = 0;
        if (i == NotificationCenter.fileLoaded || i == NotificationCenter.httpFileDidLoad) {
            String str = objArr[0];
            MessageObject messageObject = this.playingMessageObject;
            if (messageObject != null && messageObject.currentAccount == i2 && FileLoader.getAttachFileName(messageObject.getDocument()).equals(str)) {
                if (this.downloadingCurrentMessage) {
                    this.playMusicAgain = true;
                    playMessage(this.playingMessageObject);
                } else if (this.audioInfo == null) {
                    try {
                        this.audioInfo = AudioInfo.getAudioInfo(FileLoader.getPathToMessage(this.playingMessageObject.messageOwner));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!objArr[2].booleanValue()) {
                long longValue = objArr[1].longValue();
                ArrayList arrayList2 = objArr[0];
                MessageObject messageObject2 = this.playingMessageObject;
                if (messageObject2 != null && longValue == messageObject2.messageOwner.peer_id.channel_id && arrayList2.contains(Integer.valueOf(messageObject2.getId()))) {
                    cleanupPlayer(true, true);
                }
                ArrayList<MessageObject> arrayList3 = this.voiceMessagesPlaylist;
                if (arrayList3 != null && !arrayList3.isEmpty() && longValue == this.voiceMessagesPlaylist.get(0).messageOwner.peer_id.channel_id) {
                    while (i3 < arrayList2.size()) {
                        Integer num = (Integer) arrayList2.get(i3);
                        MessageObject messageObject3 = this.voiceMessagesPlaylistMap.get(num.intValue());
                        this.voiceMessagesPlaylistMap.remove(num.intValue());
                        if (messageObject3 != null) {
                            this.voiceMessagesPlaylist.remove(messageObject3);
                        }
                        i3++;
                    }
                }
            }
        } else if (i == NotificationCenter.removeAllMessagesFromDialog) {
            long longValue2 = objArr[0].longValue();
            MessageObject messageObject4 = this.playingMessageObject;
            if (messageObject4 != null && messageObject4.getDialogId() == longValue2) {
                cleanupPlayer(false, true);
            }
        } else if (i == NotificationCenter.musicDidLoad) {
            long longValue3 = objArr[0].longValue();
            MessageObject messageObject5 = this.playingMessageObject;
            if (messageObject5 != null && messageObject5.isMusic() && this.playingMessageObject.getDialogId() == longValue3 && !this.playingMessageObject.scheduled) {
                this.playlist.addAll(0, objArr[1]);
                this.playlist.addAll(objArr[2]);
                int size = this.playlist.size();
                for (int i4 = 0; i4 < size; i4++) {
                    MessageObject messageObject6 = this.playlist.get(i4);
                    this.playlistMap.put(Integer.valueOf(messageObject6.getId()), messageObject6);
                    int[] iArr = this.playlistMaxId;
                    iArr[0] = Math.min(iArr[0], messageObject6.getId());
                }
                sortPlaylist();
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                } else {
                    MessageObject messageObject7 = this.playingMessageObject;
                    if (messageObject7 != null && (indexOf = this.playlist.indexOf(messageObject7)) >= 0) {
                        this.currentPlaylistNum = indexOf;
                    }
                }
                this.playlistClassGuid = ConnectionsManager.generateClassGuid();
            }
        } else if (i == NotificationCenter.mediaDidLoad) {
            if (objArr[3].intValue() == this.playlistClassGuid && this.playingMessageObject != null) {
                long longValue4 = objArr[0].longValue();
                objArr[4].intValue();
                ArrayList arrayList4 = objArr[2];
                DialogObject.isEncryptedDialog(longValue4);
                char c = longValue4 == this.playlistMergeDialogId ? (char) 1 : 0;
                if (!arrayList4.isEmpty()) {
                    this.playlistEndReached[c] = objArr[5].booleanValue();
                }
                int i5 = 0;
                for (int i6 = 0; i6 < arrayList4.size(); i6++) {
                    MessageObject messageObject8 = (MessageObject) arrayList4.get(i6);
                    if (!this.playlistMap.containsKey(Integer.valueOf(messageObject8.getId()))) {
                        i5++;
                        this.playlist.add(0, messageObject8);
                        this.playlistMap.put(Integer.valueOf(messageObject8.getId()), messageObject8);
                        int[] iArr2 = this.playlistMaxId;
                        iArr2[c] = Math.min(iArr2[c], messageObject8.getId());
                    }
                }
                sortPlaylist();
                int indexOf2 = this.playlist.indexOf(this.playingMessageObject);
                if (indexOf2 >= 0) {
                    this.currentPlaylistNum = indexOf2;
                }
                this.loadingPlaylist = false;
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                }
                if (i5 != 0) {
                    NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.moreMusicDidLoad, Integer.valueOf(i5));
                }
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!objArr[2].booleanValue() && (arrayList = this.voiceMessagesPlaylist) != null && !arrayList.isEmpty() && objArr[0].longValue() == this.voiceMessagesPlaylist.get(0).getDialogId()) {
                ArrayList arrayList5 = objArr[1];
                while (i3 < arrayList5.size()) {
                    MessageObject messageObject9 = (MessageObject) arrayList5.get(i3);
                    if ((messageObject9.isVoice() || messageObject9.isRoundVideo()) && (!this.voiceMessagesPlaylistUnread || (messageObject9.isContentUnread() && !messageObject9.isOut()))) {
                        this.voiceMessagesPlaylist.add(messageObject9);
                        this.voiceMessagesPlaylistMap.put(messageObject9.getId(), messageObject9);
                    }
                    i3++;
                }
            }
        } else if (i == NotificationCenter.playerDidStartPlaying) {
            if (!getInstance().isCurrentPlayer(objArr[0])) {
                getInstance().lambda$startAudioAgain$7(getInstance().getPlayingMessageObject());
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isRecordingAudio() {
        return (this.recordStartRunnable == null && this.recordingAudio == null) ? false : true;
    }

    private boolean isNearToSensor(float f) {
        return f < 5.0f && f != this.proximitySensor.getMaximumRange();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        r0 = r1.playingMessageObject;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isRecordingOrListeningByProximity() {
        /*
            r1 = this;
            boolean r0 = r1.proximityTouched
            if (r0 == 0) goto L_0x001e
            boolean r0 = r1.isRecordingAudio()
            if (r0 != 0) goto L_0x001c
            org.telegram.messenger.MessageObject r0 = r1.playingMessageObject
            if (r0 == 0) goto L_0x001e
            boolean r0 = r0.isVoice()
            if (r0 != 0) goto L_0x001c
            org.telegram.messenger.MessageObject r0 = r1.playingMessageObject
            boolean r0 = r0.isRoundVideo()
            if (r0 == 0) goto L_0x001e
        L_0x001c:
            r0 = 1
            goto L_0x001f
        L_0x001e:
            r0 = 0
        L_0x001f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.isRecordingOrListeningByProximity():boolean");
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        PowerManager.WakeLock wakeLock;
        PowerManager.WakeLock wakeLock2;
        PowerManager.WakeLock wakeLock3;
        PowerManager.WakeLock wakeLock4;
        PowerManager.WakeLock wakeLock5;
        int i;
        boolean z;
        double d;
        SensorEvent sensorEvent2 = sensorEvent;
        if (this.sensorsStarted && VoIPService.getSharedInstance() == null) {
            Sensor sensor = sensorEvent2.sensor;
            if (sensor == this.proximitySensor) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("proximity changed to " + sensorEvent2.values[0] + " max value = " + this.proximitySensor.getMaximumRange());
                }
                float f = this.lastProximityValue;
                if (f == -100.0f) {
                    this.lastProximityValue = sensorEvent2.values[0];
                } else if (f != sensorEvent2.values[0]) {
                    this.proximityHasDifferentValues = true;
                }
                if (this.proximityHasDifferentValues) {
                    this.proximityTouched = isNearToSensor(sensorEvent2.values[0]);
                }
            } else if (sensor == this.accelerometerSensor) {
                long j = this.lastTimestamp;
                if (j == 0) {
                    d = 0.9800000190734863d;
                } else {
                    double d2 = (double) (sensorEvent2.timestamp - j);
                    Double.isNaN(d2);
                    d = 1.0d / ((d2 / 1.0E9d) + 1.0d);
                }
                this.lastTimestamp = sensorEvent2.timestamp;
                float[] fArr = this.gravity;
                double d3 = (double) fArr[0];
                Double.isNaN(d3);
                double d4 = 1.0d - d;
                float[] fArr2 = sensorEvent2.values;
                double d5 = (double) fArr2[0];
                Double.isNaN(d5);
                fArr[0] = (float) ((d3 * d) + (d5 * d4));
                double d6 = (double) fArr[1];
                Double.isNaN(d6);
                double d7 = (double) fArr2[1];
                Double.isNaN(d7);
                fArr[1] = (float) ((d6 * d) + (d7 * d4));
                double d8 = (double) fArr[2];
                Double.isNaN(d8);
                double d9 = d * d8;
                double d10 = (double) fArr2[2];
                Double.isNaN(d10);
                fArr[2] = (float) (d9 + (d4 * d10));
                float[] fArr3 = this.gravityFast;
                fArr3[0] = (fArr[0] * 0.8f) + (fArr2[0] * 0.19999999f);
                fArr3[1] = (fArr[1] * 0.8f) + (fArr2[1] * 0.19999999f);
                fArr3[2] = (fArr[2] * 0.8f) + (fArr2[2] * 0.19999999f);
                float[] fArr4 = this.linearAcceleration;
                fArr4[0] = fArr2[0] - fArr[0];
                fArr4[1] = fArr2[1] - fArr[1];
                fArr4[2] = fArr2[2] - fArr[2];
            } else if (sensor == this.linearSensor) {
                float[] fArr5 = this.linearAcceleration;
                float[] fArr6 = sensorEvent2.values;
                fArr5[0] = fArr6[0];
                fArr5[1] = fArr6[1];
                fArr5[2] = fArr6[2];
            } else if (sensor == this.gravitySensor) {
                float[] fArr7 = this.gravityFast;
                float[] fArr8 = this.gravity;
                float[] fArr9 = sensorEvent2.values;
                float f2 = fArr9[0];
                fArr8[0] = f2;
                fArr7[0] = f2;
                float f3 = fArr9[1];
                fArr8[1] = f3;
                fArr7[1] = f3;
                float f4 = fArr9[2];
                fArr8[2] = f4;
                fArr7[2] = f4;
            }
            Sensor sensor2 = sensorEvent2.sensor;
            if (sensor2 == this.linearSensor || sensor2 == this.gravitySensor || sensor2 == this.accelerometerSensor) {
                float[] fArr10 = this.gravity;
                float f5 = fArr10[0];
                float[] fArr11 = this.linearAcceleration;
                float f6 = (f5 * fArr11[0]) + (fArr10[1] * fArr11[1]) + (fArr10[2] * fArr11[2]);
                int i2 = this.raisedToBack;
                if (i2 != 6 && ((f6 > 0.0f && this.previousAccValue > 0.0f) || (f6 < 0.0f && this.previousAccValue < 0.0f))) {
                    if (f6 > 0.0f) {
                        z = f6 > 15.0f;
                        i = 1;
                    } else {
                        z = f6 < -15.0f;
                        i = 2;
                    }
                    int i3 = this.raisedToTopSign;
                    if (i3 != 0 && i3 != i) {
                        int i4 = this.raisedToTop;
                        if (i4 != 6 || !z) {
                            if (!z) {
                                this.countLess++;
                            }
                            if (!(this.countLess != 10 && i4 == 6 && i2 == 0)) {
                                this.raisedToTop = 0;
                                this.raisedToTopSign = 0;
                                this.raisedToBack = 0;
                                this.countLess = 0;
                            }
                        } else if (i2 < 6) {
                            int i5 = i2 + 1;
                            this.raisedToBack = i5;
                            if (i5 == 6) {
                                this.raisedToTop = 0;
                                this.raisedToTopSign = 0;
                                this.countLess = 0;
                                this.timeSinceRaise = System.currentTimeMillis();
                                if (BuildVars.LOGS_ENABLED && BuildVars.DEBUG_PRIVATE_VERSION) {
                                    FileLog.d("motion detected");
                                }
                            }
                        }
                    } else if (z && i2 == 0 && (i3 == 0 || i3 == i)) {
                        int i6 = this.raisedToTop;
                        if (i6 < 6 && !this.proximityTouched) {
                            this.raisedToTopSign = i;
                            int i7 = i6 + 1;
                            this.raisedToTop = i7;
                            if (i7 == 6) {
                                this.countLess = 0;
                            }
                        }
                    } else {
                        if (!z) {
                            this.countLess++;
                        }
                        if (!(i3 == i && this.countLess != 10 && this.raisedToTop == 6 && i2 == 0)) {
                            this.raisedToBack = 0;
                            this.raisedToTop = 0;
                            this.raisedToTopSign = 0;
                            this.countLess = 0;
                        }
                    }
                }
                this.previousAccValue = f6;
                float[] fArr12 = this.gravityFast;
                this.accelerometerVertical = fArr12[1] > 2.5f && Math.abs(fArr12[2]) < 4.0f && Math.abs(this.gravityFast[0]) > 1.5f;
            }
            if (this.raisedToBack != 6 || !this.accelerometerVertical || !this.proximityTouched || NotificationsController.audioManager.isWiredHeadsetOn()) {
                boolean z2 = this.proximityTouched;
                if (z2) {
                    if (this.playingMessageObject != null && !ApplicationLoader.mainInterfacePaused && ((this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker && !NotificationsController.audioManager.isWiredHeadsetOn())) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start listen by proximity only");
                        }
                        if (this.proximityHasDifferentValues && (wakeLock3 = this.proximityWakeLock) != null && !wakeLock3.isHeld()) {
                            this.proximityWakeLock.acquire();
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        this.ignoreOnPause = true;
                    }
                } else if (!z2) {
                    if (this.raiseToEarRecord) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("stop record");
                        }
                        stopRecording(2, false, 0);
                        this.raiseToEarRecord = false;
                        this.ignoreOnPause = false;
                        if (this.proximityHasDifferentValues && (wakeLock2 = this.proximityWakeLock) != null && wakeLock2.isHeld()) {
                            this.proximityWakeLock.release();
                        }
                    } else if (this.useFrontSpeaker) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("stop listen");
                        }
                        this.useFrontSpeaker = false;
                        startAudioAgain(true);
                        this.ignoreOnPause = false;
                        if (this.proximityHasDifferentValues && (wakeLock = this.proximityWakeLock) != null && wakeLock.isHeld()) {
                            this.proximityWakeLock.release();
                        }
                    }
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("sensor values reached");
                }
                if (this.playingMessageObject != null || this.recordStartRunnable != null || this.recordingAudio != null || PhotoViewer.getInstance().isVisible() || !ApplicationLoader.isScreenOn || this.inputFieldHasText || !this.allowStartRecord || this.raiseChat == null || this.callInProgress) {
                    MessageObject messageObject = this.playingMessageObject;
                    if (messageObject != null && ((messageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start listen");
                        }
                        if (this.proximityHasDifferentValues && (wakeLock4 = this.proximityWakeLock) != null && !wakeLock4.isHeld()) {
                            this.proximityWakeLock.acquire();
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
                        startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), (MessageObject) null, this.raiseChat.getThreadMessage(), this.raiseChat.getClassGuid());
                    }
                    if (this.useFrontSpeaker) {
                        setUseFrontSpeaker(true);
                    }
                    this.ignoreOnPause = true;
                    if (this.proximityHasDifferentValues && (wakeLock5 = this.proximityWakeLock) != null && !wakeLock5.isHeld()) {
                        this.proximityWakeLock.acquire();
                    }
                }
                this.raisedToBack = 0;
                this.raisedToTop = 0;
                this.raisedToTopSign = 0;
                this.countLess = 0;
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
        if (z) {
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(false);
            return;
        }
        audioManager.setSpeakerphoneOn(true);
    }

    public void startRecordingIfFromSpeaker() {
        ChatActivity chatActivity;
        if (this.useFrontSpeaker && (chatActivity = this.raiseChat) != null && this.allowStartRecord && SharedConfig.raiseToSpeak) {
            this.raiseToEarRecord = true;
            startRecording(chatActivity.getCurrentAccount(), this.raiseChat.getDialogId(), (MessageObject) null, this.raiseChat.getThreadMessage(), this.raiseChat.getClassGuid());
            this.ignoreOnPause = true;
        }
    }

    private void startAudioAgain(boolean z) {
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null) {
            int i = 0;
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.audioRouteChanged, Boolean.valueOf(this.useFrontSpeaker));
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                if (!this.useFrontSpeaker) {
                    i = 3;
                }
                videoPlayer2.setStreamType(i);
                if (!z) {
                    if (this.videoPlayer.getCurrentPosition() < 1000) {
                        this.videoPlayer.seekTo(0);
                    }
                    this.videoPlayer.play();
                    return;
                }
                lambda$startAudioAgain$7(this.playingMessageObject);
                return;
            }
            VideoPlayer videoPlayer3 = this.audioPlayer;
            boolean z2 = videoPlayer3 != null;
            MessageObject messageObject2 = this.playingMessageObject;
            float f = messageObject2.audioProgress;
            int i2 = messageObject2.audioPlayerDuration;
            if (z || videoPlayer3 == null || !videoPlayer3.isPlaying() || ((float) i2) * f > 1.0f) {
                messageObject2.audioProgress = f;
            } else {
                messageObject2.audioProgress = 0.0f;
            }
            cleanupPlayer(false, true);
            playMessage(messageObject2);
            if (!z) {
                return;
            }
            if (z2) {
                AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda28(this, messageObject2), 100);
            } else {
                lambda$startAudioAgain$7(messageObject2);
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
        if (chatActivity == null) {
            return;
        }
        if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null) {
            this.raiseChat = chatActivity;
            if (!SharedConfig.raiseToSpeak) {
                MessageObject messageObject = this.playingMessageObject;
                if (messageObject == null) {
                    return;
                }
                if (!messageObject.isVoice() && !this.playingMessageObject.isRoundVideo()) {
                    return;
                }
            }
            if (!this.sensorsStarted) {
                float[] fArr = this.gravity;
                fArr[2] = 0.0f;
                fArr[1] = 0.0f;
                fArr[0] = 0.0f;
                float[] fArr2 = this.linearAcceleration;
                fArr2[2] = 0.0f;
                fArr2[1] = 0.0f;
                fArr2[0] = 0.0f;
                float[] fArr3 = this.gravityFast;
                fArr3[2] = 0.0f;
                fArr3[1] = 0.0f;
                fArr3[0] = 0.0f;
                this.lastTimestamp = 0;
                this.previousAccValue = 0.0f;
                this.raisedToTop = 0;
                this.raisedToTopSign = 0;
                this.countLess = 0;
                this.raisedToBack = 0;
                Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda8(this));
                this.sensorsStarted = true;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startRaiseToEarSensors$8() {
        Sensor sensor = this.gravitySensor;
        if (sensor != null) {
            this.sensorManager.registerListener(this, sensor, 30000);
        }
        Sensor sensor2 = this.linearSensor;
        if (sensor2 != null) {
            this.sensorManager.registerListener(this, sensor2, 30000);
        }
        Sensor sensor3 = this.accelerometerSensor;
        if (sensor3 != null) {
            this.sensorManager.registerListener(this, sensor3, 30000);
        }
        this.sensorManager.registerListener(this, this.proximitySensor, 3);
    }

    public void stopRaiseToEarSensors(ChatActivity chatActivity, boolean z) {
        PowerManager.WakeLock wakeLock;
        if (this.ignoreOnPause) {
            this.ignoreOnPause = false;
            return;
        }
        stopRecording(z ? 2 : 0, false, 0);
        if (this.sensorsStarted && !this.ignoreOnPause) {
            if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null && this.raiseChat == chatActivity) {
                this.raiseChat = null;
                this.sensorsStarted = false;
                this.accelerometerVertical = false;
                this.proximityTouched = false;
                this.raiseToEarRecord = false;
                this.useFrontSpeaker = false;
                Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda14(this));
                if (this.proximityHasDifferentValues && (wakeLock = this.proximityWakeLock) != null && wakeLock.isHeld()) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$stopRaiseToEarSensors$9() {
        Sensor sensor = this.linearSensor;
        if (sensor != null) {
            this.sensorManager.unregisterListener(this, sensor);
        }
        Sensor sensor2 = this.gravitySensor;
        if (sensor2 != null) {
            this.sensorManager.unregisterListener(this, sensor2);
        }
        Sensor sensor3 = this.accelerometerSensor;
        if (sensor3 != null) {
            this.sensorManager.unregisterListener(this, sensor3);
        }
        this.sensorManager.unregisterListener(this, this.proximitySensor);
    }

    public void cleanupPlayer(boolean z, boolean z2) {
        cleanupPlayer(z, z2, false, false);
    }

    public void cleanupPlayer(boolean z, boolean z2, boolean z3, boolean z4) {
        PipRoundVideoView pipRoundVideoView2;
        MessageObject messageObject;
        if (this.audioPlayer != null) {
            ValueAnimator valueAnimator = this.audioVolumeAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllUpdateListeners();
                this.audioVolumeAnimator.cancel();
            }
            if (!this.audioPlayer.isPlaying() || (messageObject = this.playingMessageObject) == null || messageObject.isVoice()) {
                try {
                    this.audioPlayer.releasePlayer(true);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                final VideoPlayer videoPlayer2 = this.audioPlayer;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.audioVolume, 0.0f});
                ofFloat.addUpdateListener(new MediaController$$ExternalSyntheticLambda0(this, videoPlayer2));
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        try {
                            videoPlayer2.releasePlayer(true);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                ofFloat.setDuration(300);
                ofFloat.start();
            }
            this.audioPlayer = null;
            Theme.unrefAudioVisualizeDrawable(this.playingMessageObject);
        } else {
            VideoPlayer videoPlayer3 = this.videoPlayer;
            if (videoPlayer3 != null) {
                this.currentAspectRatioFrameLayout = null;
                this.currentTextureViewContainer = null;
                this.currentAspectRatioFrameLayoutReady = false;
                this.isDrawingWasReady = false;
                this.currentTextureView = null;
                this.goingToShowMessageObject = null;
                if (z4) {
                    PhotoViewer.getInstance().injectVideoPlayer(this.videoPlayer);
                    MessageObject messageObject2 = this.playingMessageObject;
                    this.goingToShowMessageObject = messageObject2;
                    NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, Boolean.TRUE);
                } else {
                    long currentPosition = videoPlayer3.getCurrentPosition();
                    MessageObject messageObject3 = this.playingMessageObject;
                    if (messageObject3 != null && messageObject3.isVideo() && currentPosition > 0) {
                        MessageObject messageObject4 = this.playingMessageObject;
                        messageObject4.audioProgressMs = (int) currentPosition;
                        NotificationCenter.getInstance(messageObject4.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, Boolean.FALSE);
                    }
                    this.videoPlayer.releasePlayer(true);
                    this.videoPlayer = null;
                }
                try {
                    this.baseActivity.getWindow().clearFlags(128);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                if (this.playingMessageObject != null && !z4) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
            }
        }
        stopProgressTimer();
        this.lastProgress = 0;
        this.isPaused = false;
        if (!this.useFrontSpeaker && !SharedConfig.raiseToSpeak) {
            ChatActivity chatActivity = this.raiseChat;
            stopRaiseToEarSensors(chatActivity, false);
            this.raiseChat = chatActivity;
        }
        PowerManager.WakeLock wakeLock = this.proximityWakeLock;
        if (wakeLock != null && wakeLock.isHeld() && !this.proximityTouched) {
            this.proximityWakeLock.release();
        }
        MessageObject messageObject5 = this.playingMessageObject;
        if (messageObject5 != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance(messageObject5.currentAccount).cancelLoadFile(this.playingMessageObject.getDocument());
            }
            MessageObject messageObject6 = this.playingMessageObject;
            if (z) {
                messageObject6.resetPlayingProgress();
                NotificationCenter.getInstance(messageObject6.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), 0);
            }
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (z) {
                NotificationsController.audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                int i = -1;
                ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
                if (arrayList != null) {
                    if (!z3 || (i = arrayList.indexOf(messageObject6)) < 0) {
                        this.voiceMessagesPlaylist = null;
                        this.voiceMessagesPlaylistMap = null;
                    } else {
                        this.voiceMessagesPlaylist.remove(i);
                        this.voiceMessagesPlaylistMap.remove(messageObject6.getId());
                        if (this.voiceMessagesPlaylist.isEmpty()) {
                            this.voiceMessagesPlaylist = null;
                            this.voiceMessagesPlaylistMap = null;
                        }
                    }
                }
                ArrayList<MessageObject> arrayList2 = this.voiceMessagesPlaylist;
                if (arrayList2 == null || i >= arrayList2.size()) {
                    if ((messageObject6.isVoice() || messageObject6.isRoundVideo()) && messageObject6.getId() != 0) {
                        startRecordingIfFromSpeaker();
                    }
                    NotificationCenter.getInstance(messageObject6.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(messageObject6.getId()), Boolean.valueOf(z2));
                    this.pipSwitchingState = 0;
                    PipRoundVideoView pipRoundVideoView3 = this.pipRoundVideoView;
                    if (pipRoundVideoView3 != null) {
                        pipRoundVideoView3.close(true);
                        this.pipRoundVideoView = null;
                    }
                } else {
                    MessageObject messageObject7 = this.voiceMessagesPlaylist.get(i);
                    playMessage(messageObject7);
                    if (!messageObject7.isRoundVideo() && (pipRoundVideoView2 = this.pipRoundVideoView) != null) {
                        pipRoundVideoView2.close(true);
                        this.pipRoundVideoView = null;
                    }
                }
            }
            if (z2) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanupPlayer$10(VideoPlayer videoPlayer2, ValueAnimator valueAnimator) {
        videoPlayer2.setVolume((this.audioFocus != 1 ? 1.0f : 0.2f) * ((Float) valueAnimator.getAnimatedValue()).floatValue());
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
            if ((this.playingMessageObject.eventId == 0) == (messageObject.eventId == 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean seekToProgress(MessageObject messageObject, float f) {
        if (!((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject))) {
            try {
                VideoPlayer videoPlayer2 = this.audioPlayer;
                if (videoPlayer2 != null) {
                    long duration = videoPlayer2.getDuration();
                    if (duration == -9223372036854775807L) {
                        this.seekToProgressPending = f;
                    } else {
                        this.playingMessageObject.audioProgress = f;
                        long j = (long) ((int) (((float) duration) * f));
                        this.audioPlayer.seekTo(j);
                        this.lastProgress = j;
                    }
                } else {
                    VideoPlayer videoPlayer3 = this.videoPlayer;
                    if (videoPlayer3 != null) {
                        videoPlayer3.seekTo((long) (((float) videoPlayer3.getDuration()) * f));
                    }
                }
                NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidSeek, Integer.valueOf(this.playingMessageObject.getId()), Float.valueOf(f));
                return true;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        return false;
    }

    public long getDuration() {
        VideoPlayer videoPlayer2 = this.audioPlayer;
        if (videoPlayer2 == null) {
            return 0;
        }
        return videoPlayer2.getDuration();
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
            MessageObject messageObject = this.playlist.get(this.currentPlaylistNum);
            arrayList.remove(this.currentPlaylistNum);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                int nextInt = Utilities.random.nextInt(arrayList.size());
                this.shuffledPlaylist.add((MessageObject) arrayList.get(nextInt));
                arrayList.remove(nextInt);
            }
            this.shuffledPlaylist.add(messageObject);
            this.currentPlaylistNum = this.shuffledPlaylist.size() - 1;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_search} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0103  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadMoreMusic() {
        /*
            r13 = this;
            boolean r0 = r13.loadingPlaylist
            if (r0 != 0) goto L_0x0169
            org.telegram.messenger.MessageObject r0 = r13.playingMessageObject
            if (r0 == 0) goto L_0x0169
            boolean r1 = r0.scheduled
            if (r1 != 0) goto L_0x0169
            long r0 = r0.getDialogId()
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r0)
            if (r0 != 0) goto L_0x0169
            int r0 = r13.playlistClassGuid
            if (r0 != 0) goto L_0x001c
            goto L_0x0169
        L_0x001c:
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r1 = r13.playlistGlobalSearchParams
            r2 = 0
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x0116
            boolean r1 = r1.endReached
            if (r1 != 0) goto L_0x0115
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r13.playlist
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0115
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r13.playlist
            java.lang.Object r1 = r1.get(r2)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r1 = r1.currentAccount
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r2 = r13.playlistGlobalSearchParams
            long r6 = r2.dialogId
            r2 = 20
            r8 = 1000(0x3e8, double:4.94E-321)
            int r10 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r10 == 0) goto L_0x009d
            org.telegram.tgnet.TLRPC$TL_messages_search r6 = new org.telegram.tgnet.TLRPC$TL_messages_search
            r6.<init>()
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r13.playlistGlobalSearchParams
            java.lang.String r10 = r7.query
            r6.q = r10
            r6.limit = r2
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r2 = r7.filter
            if (r2 != 0) goto L_0x005d
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty
            r2.<init>()
            goto L_0x005f
        L_0x005d:
            org.telegram.tgnet.TLRPC$MessagesFilter r2 = r2.filter
        L_0x005f:
            r6.filter = r2
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r1)
            org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r13.playlistGlobalSearchParams
            long r10 = r7.dialogId
            org.telegram.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer((long) r10)
            r6.peer = r2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r13.playlist
            int r7 = r2.size()
            int r7 = r7 - r3
            java.lang.Object r2 = r2.get(r7)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r2 = r2.getId()
            r6.offset_id = r2
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r2 = r13.playlistGlobalSearchParams
            long r10 = r2.minDate
            int r7 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r7 <= 0) goto L_0x0092
            long r10 = r10 / r8
            int r7 = (int) r10
            r6.min_date = r7
        L_0x0092:
            long r10 = r2.maxDate
            int r2 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0107
            long r10 = r10 / r8
            int r2 = (int) r10
            r6.min_date = r2
            goto L_0x0107
        L_0x009d:
            org.telegram.tgnet.TLRPC$TL_messages_searchGlobal r6 = new org.telegram.tgnet.TLRPC$TL_messages_searchGlobal
            r6.<init>()
            r6.limit = r2
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r2 = r13.playlistGlobalSearchParams
            java.lang.String r7 = r2.query
            r6.q = r7
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r2 = r2.filter
            org.telegram.tgnet.TLRPC$MessagesFilter r2 = r2.filter
            r6.filter = r2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r13.playlist
            int r7 = r2.size()
            int r7 = r7 - r3
            java.lang.Object r2 = r2.get(r7)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r7 = r2.getId()
            r6.offset_id = r7
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r13.playlistGlobalSearchParams
            int r10 = r7.nextSearchRate
            r6.offset_rate = r10
            int r10 = r6.flags
            r10 = r10 | r3
            r6.flags = r10
            int r7 = r7.folderId
            r6.folder_id = r7
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            long r10 = r2.channel_id
            int r7 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x00de
        L_0x00dc:
            long r10 = -r10
            goto L_0x00e7
        L_0x00de:
            long r10 = r2.chat_id
            int r7 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x00e5
            goto L_0x00dc
        L_0x00e5:
            long r10 = r2.user_id
        L_0x00e7:
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer((long) r10)
            r6.offset_peer = r2
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r2 = r13.playlistGlobalSearchParams
            long r10 = r2.minDate
            int r7 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r7 <= 0) goto L_0x00fd
            long r10 = r10 / r8
            int r7 = (int) r10
            r6.min_date = r7
        L_0x00fd:
            long r10 = r2.maxDate
            int r2 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0107
            long r10 = r10 / r8
            int r2 = (int) r10
            r6.min_date = r2
        L_0x0107:
            r13.loadingPlaylist = r3
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda42 r3 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda42
            r3.<init>(r13, r0, r1)
            r2.sendRequest(r6, r3)
        L_0x0115:
            return
        L_0x0116:
            boolean[] r0 = r13.playlistEndReached
            boolean r1 = r0[r2]
            if (r1 != 0) goto L_0x0140
            r13.loadingPlaylist = r3
            org.telegram.messenger.MessageObject r0 = r13.playingMessageObject
            int r0 = r0.currentAccount
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.MediaDataController r3 = r0.getMediaDataController()
            org.telegram.messenger.MessageObject r0 = r13.playingMessageObject
            long r4 = r0.getDialogId()
            r6 = 50
            int[] r0 = r13.playlistMaxId
            r7 = r0[r2]
            r8 = 0
            r9 = 4
            r10 = 1
            int r11 = r13.playlistClassGuid
            r12 = 0
            r3.loadMedia(r4, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x0169
        L_0x0140:
            long r6 = r13.playlistMergeDialogId
            int r1 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r1 == 0) goto L_0x0169
            boolean r0 = r0[r3]
            if (r0 != 0) goto L_0x0169
            r13.loadingPlaylist = r3
            org.telegram.messenger.MessageObject r0 = r13.playingMessageObject
            int r0 = r0.currentAccount
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.MediaDataController r3 = r0.getMediaDataController()
            long r4 = r13.playlistMergeDialogId
            r6 = 50
            int[] r0 = r13.playlistMaxId
            r7 = r0[r2]
            r8 = 0
            r9 = 4
            r10 = 1
            int r11 = r13.playlistClassGuid
            r12 = 0
            r3.loadMedia(r4, r6, r7, r8, r9, r10, r11, r12)
        L_0x0169:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.loadMoreMusic():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMoreMusic$12(int i, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda22(this, i, tLRPC$TL_error, tLObject, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMoreMusic$11(int i, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i2) {
        PlaylistGlobalSearchParams playlistGlobalSearchParams2;
        if (this.playlistClassGuid == i && (playlistGlobalSearchParams2 = this.playlistGlobalSearchParams) != null && this.playingMessageObject != null && tLRPC$TL_error == null) {
            this.loadingPlaylist = false;
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            playlistGlobalSearchParams2.nextSearchRate = tLRPC$messages_Messages.next_rate;
            MessagesStorage.getInstance(i2).putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            MessagesController.getInstance(i2).putUsers(tLRPC$messages_Messages.users, false);
            MessagesController.getInstance(i2).putChats(tLRPC$messages_Messages.chats, false);
            int size = tLRPC$messages_Messages.messages.size();
            int i3 = 0;
            for (int i4 = 0; i4 < size; i4++) {
                MessageObject messageObject = new MessageObject(i2, tLRPC$messages_Messages.messages.get(i4), false, true);
                if (!this.playlistMap.containsKey(Integer.valueOf(messageObject.getId()))) {
                    this.playlist.add(0, messageObject);
                    this.playlistMap.put(Integer.valueOf(messageObject.getId()), messageObject);
                    i3++;
                }
            }
            sortPlaylist();
            this.loadingPlaylist = false;
            this.playlistGlobalSearchParams.endReached = this.playlist.size() == this.playlistGlobalSearchParams.totalCount;
            if (SharedConfig.shuffleMusic) {
                buildShuffledPlayList();
            }
            if (i3 != 0) {
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.moreMusicDidLoad, Integer.valueOf(i3));
            }
        }
    }

    public boolean setPlaylist(ArrayList<MessageObject> arrayList, MessageObject messageObject, long j, PlaylistGlobalSearchParams playlistGlobalSearchParams2) {
        return setPlaylist(arrayList, messageObject, j, true, playlistGlobalSearchParams2);
    }

    public boolean setPlaylist(ArrayList<MessageObject> arrayList, MessageObject messageObject, long j) {
        return setPlaylist(arrayList, messageObject, j, true, (PlaylistGlobalSearchParams) null);
    }

    public boolean setPlaylist(ArrayList<MessageObject> arrayList, MessageObject messageObject, long j, boolean z, PlaylistGlobalSearchParams playlistGlobalSearchParams2) {
        if (this.playingMessageObject == messageObject) {
            int indexOf = this.playlist.indexOf(messageObject);
            if (indexOf >= 0) {
                this.currentPlaylistNum = indexOf;
            }
            return playMessage(messageObject);
        }
        this.forceLoopCurrentPlaylist = !z;
        this.playlistMergeDialogId = j;
        this.playMusicAgain = !this.playlist.isEmpty();
        clearPlaylist();
        this.playlistGlobalSearchParams = playlistGlobalSearchParams2;
        boolean z2 = false;
        if (!arrayList.isEmpty() && DialogObject.isEncryptedDialog(arrayList.get(0).getDialogId())) {
            z2 = true;
        }
        int i = Integer.MAX_VALUE;
        int i2 = Integer.MIN_VALUE;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            MessageObject messageObject2 = arrayList.get(size);
            if (messageObject2.isMusic()) {
                int id = messageObject2.getId();
                if (id > 0 || z2) {
                    i = Math.min(i, id);
                    i2 = Math.max(i2, id);
                }
                this.playlist.add(messageObject2);
                this.playlistMap.put(Integer.valueOf(id), messageObject2);
            }
        }
        sortPlaylist();
        int indexOf2 = this.playlist.indexOf(messageObject);
        this.currentPlaylistNum = indexOf2;
        if (indexOf2 == -1) {
            clearPlaylist();
            this.currentPlaylistNum = this.playlist.size();
            this.playlist.add(messageObject);
            this.playlistMap.put(Integer.valueOf(messageObject.getId()), messageObject);
        }
        if (messageObject.isMusic() && !messageObject.scheduled) {
            if (SharedConfig.shuffleMusic) {
                buildShuffledPlayList();
            }
            if (z) {
                if (this.playlistGlobalSearchParams == null) {
                    MediaDataController.getInstance(messageObject.currentAccount).loadMusic(messageObject.getDialogId(), (long) i, (long) i2);
                } else {
                    this.playlistClassGuid = ConnectionsManager.generateClassGuid();
                }
            }
        }
        return playMessage(messageObject);
    }

    private void sortPlaylist() {
        Collections.sort(this.playlist, MediaController$$ExternalSyntheticLambda41.INSTANCE);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$sortPlaylist$13(MessageObject messageObject, MessageObject messageObject2) {
        int id = messageObject.getId();
        int id2 = messageObject2.getId();
        long j = messageObject.messageOwner.grouped_id;
        long j2 = messageObject2.messageOwner.grouped_id;
        if (id >= 0 || id2 >= 0) {
            if (j == 0 || j != j2) {
                return zzhv$$ExternalSyntheticBackport0.m(id, id2);
            }
            return zzhv$$ExternalSyntheticBackport0.m(id2, id);
        } else if (j == 0 || j != j2) {
            return zzhv$$ExternalSyntheticBackport0.m(id2, id);
        } else {
            return zzhv$$ExternalSyntheticBackport0.m(id, id2);
        }
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
            MessageObject messageObject = this.playlist.get(i);
            if (this.playingMessageObject != null && !isSamePlayingMessage(messageObject)) {
                this.playingMessageObject.resetPlayingProgress();
            }
            playMessage(messageObject);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0057 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:61:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void playNextMessageWithoutOrder(boolean r8) {
        /*
            r7 = this;
            boolean r0 = org.telegram.messenger.SharedConfig.shuffleMusic
            if (r0 == 0) goto L_0x0007
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.shuffledPlaylist
            goto L_0x0009
        L_0x0007:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.playlist
        L_0x0009:
            r1 = 0
            r2 = 2
            r3 = 0
            r4 = 1
            if (r8 == 0) goto L_0x0032
            int r5 = org.telegram.messenger.SharedConfig.repeatMode
            if (r5 == r2) goto L_0x001b
            if (r5 != r4) goto L_0x0032
            int r5 = r0.size()
            if (r5 != r4) goto L_0x0032
        L_0x001b:
            boolean r5 = r7.forceLoopCurrentPlaylist
            if (r5 != 0) goto L_0x0032
            r7.cleanupPlayer(r3, r3)
            int r8 = r7.currentPlaylistNum
            java.lang.Object r8 = r0.get(r8)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            r8.audioProgress = r1
            r8.audioProgressSec = r3
            r7.playMessage(r8)
            return
        L_0x0032:
            boolean r5 = org.telegram.messenger.SharedConfig.playOrderReversed
            if (r5 == 0) goto L_0x0044
            int r5 = r7.currentPlaylistNum
            int r5 = r5 + r4
            r7.currentPlaylistNum = r5
            int r6 = r0.size()
            if (r5 < r6) goto L_0x0054
            r7.currentPlaylistNum = r3
            goto L_0x0052
        L_0x0044:
            int r5 = r7.currentPlaylistNum
            int r5 = r5 - r4
            r7.currentPlaylistNum = r5
            if (r5 >= 0) goto L_0x0054
            int r5 = r0.size()
            int r5 = r5 - r4
            r7.currentPlaylistNum = r5
        L_0x0052:
            r5 = 1
            goto L_0x0055
        L_0x0054:
            r5 = 0
        L_0x0055:
            if (r5 == 0) goto L_0x00fb
            if (r8 == 0) goto L_0x00fb
            int r8 = org.telegram.messenger.SharedConfig.repeatMode
            if (r8 != 0) goto L_0x00fb
            boolean r8 = r7.forceLoopCurrentPlaylist
            if (r8 != 0) goto L_0x00fb
            org.telegram.ui.Components.VideoPlayer r8 = r7.audioPlayer
            if (r8 != 0) goto L_0x0069
            org.telegram.ui.Components.VideoPlayer r0 = r7.videoPlayer
            if (r0 == 0) goto L_0x00fa
        L_0x0069:
            r0 = 0
            if (r8 == 0) goto L_0x007c
            r8.releasePlayer(r4)     // Catch:{ Exception -> 0x0070 }
            goto L_0x0074
        L_0x0070:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0074:
            r7.audioPlayer = r0
            org.telegram.messenger.MessageObject r8 = r7.playingMessageObject
            org.telegram.ui.ActionBar.Theme.unrefAudioVisualizeDrawable(r8)
            goto L_0x00b1
        L_0x007c:
            r7.currentAspectRatioFrameLayout = r0
            r7.currentTextureViewContainer = r0
            r7.currentAspectRatioFrameLayoutReady = r3
            r7.currentTextureView = r0
            org.telegram.ui.Components.VideoPlayer r8 = r7.videoPlayer
            r8.releasePlayer(r4)
            r7.videoPlayer = r0
            android.app.Activity r8 = r7.baseActivity     // Catch:{ Exception -> 0x0097 }
            android.view.Window r8 = r8.getWindow()     // Catch:{ Exception -> 0x0097 }
            r0 = 128(0x80, float:1.794E-43)
            r8.clearFlags(r0)     // Catch:{ Exception -> 0x0097 }
            goto L_0x009b
        L_0x0097:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x009b:
            java.lang.Runnable r8 = r7.setLoadingRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r8)
            org.telegram.messenger.MessageObject r8 = r7.playingMessageObject
            int r8 = r8.currentAccount
            org.telegram.messenger.FileLoader r8 = org.telegram.messenger.FileLoader.getInstance(r8)
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            r8.removeLoadingVideo(r0, r4, r3)
        L_0x00b1:
            r7.stopProgressTimer()
            r5 = 0
            r7.lastProgress = r5
            r7.isPaused = r4
            org.telegram.messenger.MessageObject r8 = r7.playingMessageObject
            r8.audioProgress = r1
            r8.audioProgressSec = r3
            int r8 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r8 = org.telegram.messenger.NotificationCenter.getInstance(r8)
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            java.lang.Object[] r1 = new java.lang.Object[r2]
            org.telegram.messenger.MessageObject r2 = r7.playingMessageObject
            int r2 = r2.getId()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1[r3] = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            r1[r4] = r2
            r8.postNotificationName(r0, r1)
            org.telegram.messenger.MessageObject r8 = r7.playingMessageObject
            int r8 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r8 = org.telegram.messenger.NotificationCenter.getInstance(r8)
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            java.lang.Object[] r1 = new java.lang.Object[r4]
            org.telegram.messenger.MessageObject r2 = r7.playingMessageObject
            int r2 = r2.getId()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1[r3] = r2
            r8.postNotificationName(r0, r1)
        L_0x00fa:
            return
        L_0x00fb:
            int r8 = r7.currentPlaylistNum
            if (r8 < 0) goto L_0x011a
            int r1 = r0.size()
            if (r8 < r1) goto L_0x0106
            goto L_0x011a
        L_0x0106:
            org.telegram.messenger.MessageObject r8 = r7.playingMessageObject
            if (r8 == 0) goto L_0x010d
            r8.resetPlayingProgress()
        L_0x010d:
            r7.playMusicAgain = r4
            int r8 = r7.currentPlaylistNum
            java.lang.Object r8 = r0.get(r8)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            r7.playMessage(r8)
        L_0x011a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.playNextMessageWithoutOrder(boolean):void");
    }

    public void playPreviousMessage() {
        int i;
        ArrayList<MessageObject> arrayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!arrayList.isEmpty() && (i = this.currentPlaylistNum) >= 0 && i < arrayList.size()) {
            MessageObject messageObject = arrayList.get(this.currentPlaylistNum);
            if (messageObject.audioProgressSec > 10) {
                seekToProgress(messageObject, 0.0f);
                return;
            }
            if (SharedConfig.playOrderReversed) {
                int i2 = this.currentPlaylistNum - 1;
                this.currentPlaylistNum = i2;
                if (i2 < 0) {
                    this.currentPlaylistNum = arrayList.size() - 1;
                }
            } else {
                int i3 = this.currentPlaylistNum + 1;
                this.currentPlaylistNum = i3;
                if (i3 >= arrayList.size()) {
                    this.currentPlaylistNum = 0;
                }
            }
            if (this.currentPlaylistNum < arrayList.size()) {
                this.playMusicAgain = true;
                playMessage(arrayList.get(this.currentPlaylistNum));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkIsNextMediaFileDownloaded() {
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null && messageObject.isMusic()) {
            checkIsNextMusicFileDownloaded(this.playingMessageObject.currentAccount);
        }
    }

    private void checkIsNextVoiceFileDownloaded(int i) {
        File file;
        ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
        if (arrayList != null && arrayList.size() >= 2) {
            MessageObject messageObject = this.voiceMessagesPlaylist.get(1);
            String str = messageObject.messageOwner.attachPath;
            File file2 = null;
            if (str != null && str.length() > 0) {
                File file3 = new File(messageObject.messageOwner.attachPath);
                if (file3.exists()) {
                    file2 = file3;
                }
            }
            if (file2 != null) {
                file = file2;
            } else {
                file = FileLoader.getPathToMessage(messageObject.messageOwner);
            }
            file.exists();
            if (file != file2 && !file.exists()) {
                FileLoader.getInstance(i).loadFile(messageObject.getDocument(), messageObject, 0, 0);
            }
        }
    }

    private void checkIsNextMusicFileDownloaded(int i) {
        int i2;
        File file;
        if (DownloadController.getInstance(i).canDownloadNextTrack()) {
            ArrayList<MessageObject> arrayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
            if (arrayList != null && arrayList.size() >= 2) {
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
                    MessageObject messageObject = arrayList.get(i2);
                    File file2 = null;
                    if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                        File file3 = new File(messageObject.messageOwner.attachPath);
                        if (file3.exists()) {
                            file2 = file3;
                        }
                    }
                    if (file2 != null) {
                        file = file2;
                    } else {
                        file = FileLoader.getPathToMessage(messageObject.messageOwner);
                    }
                    file.exists();
                    if (file != file2 && !file.exists() && messageObject.isMusic()) {
                        FileLoader.getInstance(i).loadFile(messageObject.getDocument(), messageObject, 0, 0);
                    }
                }
            }
        }
    }

    public void setVoiceMessagesPlaylist(ArrayList<MessageObject> arrayList, boolean z) {
        this.voiceMessagesPlaylist = arrayList;
        if (arrayList != null) {
            this.voiceMessagesPlaylistUnread = z;
            this.voiceMessagesPlaylistMap = new SparseArray<>();
            for (int i = 0; i < this.voiceMessagesPlaylist.size(); i++) {
                MessageObject messageObject = this.voiceMessagesPlaylist.get(i);
                this.voiceMessagesPlaylistMap.put(messageObject.getId(), messageObject);
            }
        }
    }

    private void checkAudioFocus(MessageObject messageObject) {
        int i;
        int i2;
        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
            i = this.useFrontSpeaker ? 3 : 2;
        } else {
            i = 1;
        }
        if (this.hasAudioFocus != i) {
            this.hasAudioFocus = i;
            if (i == 3) {
                i2 = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
            } else {
                i2 = NotificationsController.audioManager.requestAudioFocus(this, 3, i == 2 ? 3 : 1);
            }
            if (i2 == 1) {
                this.audioFocus = 2;
            }
        }
    }

    public void setCurrentVideoVisible(boolean z) {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.currentAspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            if (z) {
                PipRoundVideoView pipRoundVideoView2 = this.pipRoundVideoView;
                if (pipRoundVideoView2 != null) {
                    this.pipSwitchingState = 2;
                    pipRoundVideoView2.close(true);
                    this.pipRoundVideoView = null;
                    return;
                }
                if (aspectRatioFrameLayout.getParent() == null) {
                    this.currentTextureViewContainer.addView(this.currentAspectRatioFrameLayout);
                }
                this.videoPlayer.setTextureView(this.currentTextureView);
            } else if (aspectRatioFrameLayout.getParent() != null) {
                this.pipSwitchingState = 1;
                this.currentTextureViewContainer.removeView(this.currentAspectRatioFrameLayout);
            } else {
                if (this.pipRoundVideoView == null) {
                    try {
                        PipRoundVideoView pipRoundVideoView3 = new PipRoundVideoView();
                        this.pipRoundVideoView = pipRoundVideoView3;
                        pipRoundVideoView3.show(this.baseActivity, new MediaController$$ExternalSyntheticLambda10(this));
                    } catch (Exception unused) {
                        this.pipRoundVideoView = null;
                    }
                }
                PipRoundVideoView pipRoundVideoView4 = this.pipRoundVideoView;
                if (pipRoundVideoView4 != null) {
                    this.videoPlayer.setTextureView(pipRoundVideoView4.getTextureView());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setCurrentVideoVisible$14() {
        cleanupPlayer(true, true);
    }

    public void setTextureView(TextureView textureView, AspectRatioFrameLayout aspectRatioFrameLayout, FrameLayout frameLayout, boolean z) {
        if (textureView != null) {
            boolean z2 = true;
            if (!z && this.currentTextureView == textureView) {
                this.pipSwitchingState = 1;
                this.currentTextureView = null;
                this.currentAspectRatioFrameLayout = null;
                this.currentTextureViewContainer = null;
            } else if (this.videoPlayer != null && textureView != this.currentTextureView) {
                if (aspectRatioFrameLayout == null || !aspectRatioFrameLayout.isDrawingReady()) {
                    z2 = false;
                }
                this.isDrawingWasReady = z2;
                this.currentTextureView = textureView;
                PipRoundVideoView pipRoundVideoView2 = this.pipRoundVideoView;
                if (pipRoundVideoView2 != null) {
                    this.videoPlayer.setTextureView(pipRoundVideoView2.getTextureView());
                } else {
                    this.videoPlayer.setTextureView(textureView);
                }
                this.currentAspectRatioFrameLayout = aspectRatioFrameLayout;
                this.currentTextureViewContainer = frameLayout;
                if (this.currentAspectRatioFrameLayoutReady && aspectRatioFrameLayout != null) {
                    aspectRatioFrameLayout.setAspectRatio(this.currentAspectRatioFrameLayoutRatio, this.currentAspectRatioFrameLayoutRotation);
                }
            }
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
            if (this.currentMusicPlaybackSpeed >= 6.0f && f == 1.0f && this.playingMessageObject != null) {
                this.audioPlayer.pause();
                MessageObject messageObject = this.playingMessageObject;
                AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda29(this, messageObject, messageObject.audioProgress), 50);
            }
            this.currentMusicPlaybackSpeed = f;
            if (Math.abs(f - 1.0f) > 0.001f) {
                this.fastMusicPlaybackSpeed = f;
            }
        } else {
            this.currentPlaybackSpeed = f;
            if (Math.abs(f - 1.0f) > 0.001f) {
                this.fastPlaybackSpeed = f;
            }
        }
        VideoPlayer videoPlayer2 = this.audioPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.setPlaybackSpeed(f);
        } else {
            VideoPlayer videoPlayer3 = this.videoPlayer;
            if (videoPlayer3 != null) {
                videoPlayer3.setPlaybackSpeed(f);
            }
        }
        MessagesController.getGlobalMainSettings().edit().putFloat(z ? "musicPlaybackSpeed" : "playbackSpeed", f).putFloat(z ? "fastMusicPlaybackSpeed" : "fastPlaybackSpeed", z ? this.fastMusicPlaybackSpeed : this.fastPlaybackSpeed).commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.messagePlayingSpeedChanged, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setPlaybackSpeed$15(MessageObject messageObject, float f) {
        if (this.audioPlayer != null && this.playingMessageObject != null && !this.isPaused) {
            if (isSamePlayingMessage(messageObject)) {
                seekToProgress(this.playingMessageObject, f);
            }
            this.audioPlayer.play();
        }
    }

    public float getPlaybackSpeed(boolean z) {
        return z ? this.currentMusicPlaybackSpeed : this.currentPlaybackSpeed;
    }

    public float getFastPlaybackSpeed(boolean z) {
        return z ? this.fastMusicPlaybackSpeed : this.fastPlaybackSpeed;
    }

    /* access modifiers changed from: private */
    public void updateVideoState(MessageObject messageObject, int[] iArr, boolean z, boolean z2, int i) {
        MessageObject messageObject2;
        if (this.videoPlayer != null) {
            if (i == 4 || i == 1) {
                try {
                    this.baseActivity.getWindow().clearFlags(128);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                try {
                    this.baseActivity.getWindow().addFlags(128);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            if (i == 3) {
                this.playerWasReady = true;
                MessageObject messageObject3 = this.playingMessageObject;
                if (messageObject3 != null && (messageObject3.isVideo() || this.playingMessageObject.isRoundVideo())) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(messageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
                this.currentAspectRatioFrameLayoutReady = true;
            } else if (i == 2) {
                if (z2 && (messageObject2 = this.playingMessageObject) != null) {
                    if (!messageObject2.isVideo() && !this.playingMessageObject.isRoundVideo()) {
                        return;
                    }
                    if (this.playerWasReady) {
                        this.setLoadingRunnable.run();
                    } else {
                        AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000);
                    }
                }
            } else if (this.videoPlayer.isPlaying() && i == 4) {
                if (!this.playingMessageObject.isVideo() || z || (iArr != null && iArr[0] >= 4)) {
                    cleanupPlayer(true, true, true, false);
                    return;
                }
                this.videoPlayer.seekTo(0);
                if (iArr != null) {
                    iArr[0] = iArr[0] + 1;
                }
            }
        }
    }

    public void injectVideoPlayer(VideoPlayer videoPlayer2, MessageObject messageObject) {
        if (videoPlayer2 != null && messageObject != null) {
            FileLoader.getInstance(messageObject.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
            this.playerWasReady = false;
            clearPlaylist();
            this.videoPlayer = videoPlayer2;
            this.playingMessageObject = messageObject;
            final int i = this.playerNum + 1;
            this.playerNum = i;
            final MessageObject messageObject2 = messageObject;
            videoPlayer2.setDelegate(new VideoPlayer.VideoPlayerDelegate((int[]) null, true) {
                public /* bridge */ /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                }

                public /* bridge */ /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                }

                public /* bridge */ /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }

                public void onStateChanged(boolean z, int i) {
                    if (i == MediaController.this.playerNum) {
                        MediaController.this.updateVideoState(messageObject2, null, true, z, i);
                    }
                }

                public void onError(VideoPlayer videoPlayer, Exception exc) {
                    FileLog.e((Throwable) exc);
                }

                public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                    int unused = MediaController.this.currentAspectRatioFrameLayoutRotation = i3;
                    if (!(i3 == 90 || i3 == 270)) {
                        int i4 = i2;
                        i2 = i;
                        i = i4;
                    }
                    float unused2 = MediaController.this.currentAspectRatioFrameLayoutRatio = i == 0 ? 1.0f : (((float) i2) * f) / ((float) i);
                    if (MediaController.this.currentAspectRatioFrameLayout != null) {
                        MediaController.this.currentAspectRatioFrameLayout.setAspectRatio(MediaController.this.currentAspectRatioFrameLayoutRatio, MediaController.this.currentAspectRatioFrameLayoutRotation);
                    }
                }

                public void onRenderedFirstFrame() {
                    if (MediaController.this.currentAspectRatioFrameLayout != null && !MediaController.this.currentAspectRatioFrameLayout.isDrawingReady()) {
                        boolean unused = MediaController.this.isDrawingWasReady = true;
                        MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                        MediaController.this.currentTextureViewContainer.setTag(1);
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
                        int unused = MediaController.this.pipSwitchingState = 0;
                        return true;
                    } else if (MediaController.this.pipSwitchingState == 1) {
                        if (MediaController.this.baseActivity != null) {
                            if (MediaController.this.pipRoundVideoView == null) {
                                try {
                                    PipRoundVideoView unused2 = MediaController.this.pipRoundVideoView = new PipRoundVideoView();
                                    MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new MediaController$7$$ExternalSyntheticLambda0(this));
                                } catch (Exception unused3) {
                                    PipRoundVideoView unused4 = MediaController.this.pipRoundVideoView = null;
                                }
                            }
                            if (MediaController.this.pipRoundVideoView != null) {
                                if (MediaController.this.pipRoundVideoView.getTextureView().getSurfaceTexture() != surfaceTexture) {
                                    MediaController.this.pipRoundVideoView.getTextureView().setSurfaceTexture(surfaceTexture);
                                }
                                MediaController.this.videoPlayer.setTextureView(MediaController.this.pipRoundVideoView.getTextureView());
                            }
                        }
                        int unused5 = MediaController.this.pipSwitchingState = 0;
                        return true;
                    } else if (!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isInjectingVideoPlayer()) {
                        return false;
                    } else {
                        PhotoViewer.getInstance().injectVideoPlayerSurface(surfaceTexture);
                        return true;
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onSurfaceDestroyed$0() {
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

    public void playEmojiSound(AccountInstance accountInstance, String str, MessagesController.EmojiSound emojiSound, boolean z) {
        if (emojiSound != null) {
            Utilities.stageQueue.postRunnable(new MediaController$$ExternalSyntheticLambda30(this, emojiSound, accountInstance, z));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$playEmojiSound$18(MessagesController.EmojiSound emojiSound, AccountInstance accountInstance, boolean z) {
        TLRPC$TL_document tLRPC$TL_document = new TLRPC$TL_document();
        tLRPC$TL_document.access_hash = emojiSound.accessHash;
        tLRPC$TL_document.id = emojiSound.id;
        tLRPC$TL_document.mime_type = "sound/ogg";
        tLRPC$TL_document.file_reference = emojiSound.fileReference;
        tLRPC$TL_document.dc_id = accountInstance.getConnectionsManager().getCurrentDatacenterId();
        File pathToAttach = FileLoader.getPathToAttach(tLRPC$TL_document, true);
        if (!pathToAttach.exists()) {
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda7(accountInstance, tLRPC$TL_document));
        } else if (!z) {
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda24(this, pathToAttach));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$playEmojiSound$16(File file) {
        try {
            final int i = this.emojiSoundPlayerNum + 1;
            this.emojiSoundPlayerNum = i;
            VideoPlayer videoPlayer2 = this.emojiSoundPlayer;
            if (videoPlayer2 != null) {
                videoPlayer2.releasePlayer(true);
            }
            VideoPlayer videoPlayer3 = new VideoPlayer(false);
            this.emojiSoundPlayer = videoPlayer3;
            videoPlayer3.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                public void onError(VideoPlayer videoPlayer, Exception exc) {
                }

                public void onRenderedFirstFrame() {
                }

                public /* bridge */ /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                }

                public /* bridge */ /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                }

                public /* bridge */ /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                }

                public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }

                public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                }

                public void onStateChanged(boolean z, int i) {
                    AndroidUtilities.runOnUIThread(new MediaController$8$$ExternalSyntheticLambda0(this, i, i));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onStateChanged$0(int i, int i2) {
                    if (i == MediaController.this.emojiSoundPlayerNum && i2 == 4 && MediaController.this.emojiSoundPlayer != null) {
                        try {
                            MediaController.this.emojiSoundPlayer.releasePlayer(true);
                            VideoPlayer unused = MediaController.this.emojiSoundPlayer = null;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                }
            });
            this.emojiSoundPlayer.preparePlayer(Uri.fromFile(file), "other");
            this.emojiSoundPlayer.setStreamType(3);
            this.emojiSoundPlayer.play();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            VideoPlayer videoPlayer4 = this.emojiSoundPlayer;
            if (videoPlayer4 != null) {
                videoPlayer4.releasePlayer(true);
                this.emojiSoundPlayer = null;
            }
        }
    }

    public boolean playMessage(MessageObject messageObject) {
        boolean z;
        File file;
        File file2;
        float f;
        PowerManager.WakeLock wakeLock;
        final MessageObject messageObject2 = messageObject;
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
        if (!messageObject.isOut() && messageObject.isContentUnread()) {
            MessagesController.getInstance(messageObject2.currentAccount).markMessageContentAsRead(messageObject2);
        }
        boolean z2 = this.playMusicAgain;
        boolean z3 = !z2;
        MessageObject messageObject3 = this.playingMessageObject;
        if (messageObject3 != null) {
            if (!z2) {
                messageObject3.resetPlayingProgress();
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), 0);
            }
            z3 = false;
        }
        cleanupPlayer(z3, false);
        this.shouldSavePositionForCurrentAudio = null;
        this.lastSaveTime = 0;
        this.playMusicAgain = false;
        this.seekToProgressPending = 0.0f;
        String str = messageObject2.messageOwner.attachPath;
        if (str == null || str.length() <= 0) {
            file = null;
            z = false;
        } else {
            file = new File(messageObject2.messageOwner.attachPath);
            z = file.exists();
            if (!z) {
                file = null;
            }
        }
        if (file != null) {
            file2 = file;
        } else {
            file2 = FileLoader.getPathToMessage(messageObject2.messageOwner);
        }
        boolean z4 = SharedConfig.streamMedia && (messageObject.isMusic() || messageObject.isRoundVideo() || (messageObject.isVideo() && messageObject.canStreamVideo())) && !DialogObject.isEncryptedDialog(messageObject.getDialogId());
        if (file2 == file || (z = file2.exists()) || z4) {
            boolean z5 = z;
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
            boolean isVideo = messageObject.isVideo();
            if (messageObject.isRoundVideo() || isVideo) {
                String str2 = "&mime=";
                String str3 = "&rid=";
                FileLoader.getInstance(messageObject2.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
                this.playerWasReady = false;
                boolean z6 = !isVideo || (messageObject2.messageOwner.peer_id.channel_id == 0 && messageObject2.audioProgress <= 0.1f);
                int[] iArr = (!isVideo || messageObject.getDuration() > 30) ? null : new int[]{1};
                clearPlaylist();
                VideoPlayer videoPlayer2 = new VideoPlayer();
                this.videoPlayer = videoPlayer2;
                final int i = this.playerNum + 1;
                this.playerNum = i;
                String str4 = "UTF-8";
                AnonymousClass9 r13 = r1;
                String str5 = str2;
                String str6 = "&size=";
                String str7 = str3;
                VideoPlayer videoPlayer3 = videoPlayer2;
                final MessageObject messageObject4 = messageObject;
                String str8 = "&dc=";
                File file3 = file2;
                final int[] iArr2 = iArr;
                String str9 = "&hash=";
                String str10 = "&id=";
                final boolean z7 = z6;
                AnonymousClass9 r1 = new VideoPlayer.VideoPlayerDelegate() {
                    public /* bridge */ /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                        VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                    }

                    public /* bridge */ /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                    }

                    public /* bridge */ /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }

                    public void onStateChanged(boolean z, int i) {
                        if (i == MediaController.this.playerNum) {
                            MediaController.this.updateVideoState(messageObject4, iArr2, z7, z, i);
                        }
                    }

                    public void onError(VideoPlayer videoPlayer, Exception exc) {
                        FileLog.e((Throwable) exc);
                    }

                    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                        int unused = MediaController.this.currentAspectRatioFrameLayoutRotation = i3;
                        if (!(i3 == 90 || i3 == 270)) {
                            int i4 = i2;
                            i2 = i;
                            i = i4;
                        }
                        float unused2 = MediaController.this.currentAspectRatioFrameLayoutRatio = i == 0 ? 1.0f : (((float) i2) * f) / ((float) i);
                        if (MediaController.this.currentAspectRatioFrameLayout != null) {
                            MediaController.this.currentAspectRatioFrameLayout.setAspectRatio(MediaController.this.currentAspectRatioFrameLayoutRatio, MediaController.this.currentAspectRatioFrameLayoutRotation);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (MediaController.this.currentAspectRatioFrameLayout != null && !MediaController.this.currentAspectRatioFrameLayout.isDrawingReady()) {
                            boolean unused = MediaController.this.isDrawingWasReady = true;
                            MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                            MediaController.this.currentTextureViewContainer.setTag(1);
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
                            int unused = MediaController.this.pipSwitchingState = 0;
                            return true;
                        } else if (MediaController.this.pipSwitchingState == 1) {
                            if (MediaController.this.baseActivity != null) {
                                if (MediaController.this.pipRoundVideoView == null) {
                                    try {
                                        PipRoundVideoView unused2 = MediaController.this.pipRoundVideoView = new PipRoundVideoView();
                                        MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new MediaController$9$$ExternalSyntheticLambda0(this));
                                    } catch (Exception unused3) {
                                        PipRoundVideoView unused4 = MediaController.this.pipRoundVideoView = null;
                                    }
                                }
                                if (MediaController.this.pipRoundVideoView != null) {
                                    if (MediaController.this.pipRoundVideoView.getTextureView().getSurfaceTexture() != surfaceTexture) {
                                        MediaController.this.pipRoundVideoView.getTextureView().setSurfaceTexture(surfaceTexture);
                                    }
                                    MediaController.this.videoPlayer.setTextureView(MediaController.this.pipRoundVideoView.getTextureView());
                                }
                            }
                            int unused5 = MediaController.this.pipSwitchingState = 0;
                            return true;
                        } else if (!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isInjectingVideoPlayer()) {
                            return false;
                        } else {
                            PhotoViewer.getInstance().injectVideoPlayerSurface(surfaceTexture);
                            return true;
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onSurfaceDestroyed$0() {
                        MediaController.this.cleanupPlayer(true, true);
                    }
                };
                videoPlayer3.setDelegate(r13);
                this.currentAspectRatioFrameLayoutReady = false;
                if (this.pipRoundVideoView != null || !MessagesController.getInstance(messageObject2.currentAccount).isDialogVisible(messageObject.getDialogId(), messageObject2.scheduled)) {
                    if (this.pipRoundVideoView == null) {
                        try {
                            PipRoundVideoView pipRoundVideoView2 = new PipRoundVideoView();
                            this.pipRoundVideoView = pipRoundVideoView2;
                            pipRoundVideoView2.show(this.baseActivity, new MediaController$$ExternalSyntheticLambda12(this));
                        } catch (Exception unused) {
                            this.pipRoundVideoView = null;
                        }
                    }
                    PipRoundVideoView pipRoundVideoView3 = this.pipRoundVideoView;
                    if (pipRoundVideoView3 != null) {
                        this.videoPlayer.setTextureView(pipRoundVideoView3.getTextureView());
                    }
                } else {
                    TextureView textureView = this.currentTextureView;
                    if (textureView != null) {
                        this.videoPlayer.setTextureView(textureView);
                    }
                }
                if (z5) {
                    if (!messageObject2.mediaExists && file3 != file) {
                        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda35(messageObject2, file3));
                    }
                    this.videoPlayer.preparePlayer(Uri.fromFile(file3), "other");
                } else {
                    try {
                        int fileReference = FileLoader.getInstance(messageObject2.currentAccount).getFileReference(messageObject2);
                        TLRPC$Document document = messageObject.getDocument();
                        StringBuilder sb = new StringBuilder();
                        sb.append("?account=");
                        sb.append(messageObject2.currentAccount);
                        sb.append(str10);
                        sb.append(document.id);
                        sb.append(str9);
                        sb.append(document.access_hash);
                        sb.append(str8);
                        sb.append(document.dc_id);
                        sb.append(str6);
                        sb.append(document.size);
                        sb.append(str5);
                        String str11 = str4;
                        sb.append(URLEncoder.encode(document.mime_type, str11));
                        sb.append(str7);
                        sb.append(fileReference);
                        sb.append("&name=");
                        sb.append(URLEncoder.encode(FileLoader.getDocumentFileName(document), str11));
                        sb.append("&reference=");
                        byte[] bArr = document.file_reference;
                        if (bArr == null) {
                            bArr = new byte[0];
                        }
                        sb.append(Utilities.bytesToHex(bArr));
                        this.videoPlayer.preparePlayer(Uri.parse("tg://" + messageObject.getFileName() + sb.toString()), "other");
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                if (messageObject.isRoundVideo()) {
                    this.videoPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                    if (Math.abs(this.currentPlaybackSpeed - 1.0f) > 0.001f) {
                        this.videoPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
                    }
                    float f2 = messageObject2.forceSeekTo;
                    f = 0.0f;
                    if (f2 >= 0.0f) {
                        this.seekToProgressPending = f2;
                        messageObject2.audioProgress = f2;
                        messageObject2.forceSeekTo = -1.0f;
                    }
                } else {
                    f = 0.0f;
                    this.videoPlayer.setStreamType(3);
                }
            } else {
                PipRoundVideoView pipRoundVideoView4 = this.pipRoundVideoView;
                if (pipRoundVideoView4 != null) {
                    pipRoundVideoView4.close(true);
                    this.pipRoundVideoView = null;
                }
                try {
                    VideoPlayer videoPlayer4 = new VideoPlayer();
                    this.audioPlayer = videoPlayer4;
                    final int i2 = this.playerNum + 1;
                    this.playerNum = i2;
                    videoPlayer4.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                        public void onError(VideoPlayer videoPlayer, Exception exc) {
                        }

                        public void onRenderedFirstFrame() {
                        }

                        public /* bridge */ /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                            VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                        }

                        public /* bridge */ /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                        }

                        public /* bridge */ /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                        }

                        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                            return false;
                        }

                        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                        }

                        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                        }

                        public void onStateChanged(boolean z, int i) {
                            if (i2 == MediaController.this.playerNum) {
                                if (i == 4 || ((i == 1 || i == 2) && z && messageObject2.audioProgress >= 0.999f)) {
                                    MessageObject messageObject = messageObject2;
                                    messageObject.audioProgress = 1.0f;
                                    NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(messageObject2.getId()), 0);
                                    if (MediaController.this.playlist.isEmpty() || (MediaController.this.playlist.size() <= 1 && messageObject2.isVoice())) {
                                        MediaController.this.cleanupPlayer(true, true, messageObject2.isVoice(), false);
                                    } else {
                                        MediaController.this.playNextMessageWithoutOrder(true);
                                    }
                                } else if (MediaController.this.audioPlayer != null && MediaController.this.seekToProgressPending != 0.0f) {
                                    if (i == 3 || i == 1) {
                                        long duration = (long) ((int) (((float) MediaController.this.audioPlayer.getDuration()) * MediaController.this.seekToProgressPending));
                                        MediaController.this.audioPlayer.seekTo(duration);
                                        long unused = MediaController.this.lastProgress = duration;
                                        float unused2 = MediaController.this.seekToProgressPending = 0.0f;
                                    }
                                }
                            }
                        }
                    });
                    this.audioPlayer.setAudioVisualizerDelegate(new VideoPlayer.AudioVisualizerDelegate() {
                        public void onVisualizerUpdate(boolean z, boolean z2, float[] fArr) {
                            Theme.getCurrentAudiVisualizerDrawable().setWaveform(z, z2, fArr);
                        }

                        public boolean needUpdate() {
                            return Theme.getCurrentAudiVisualizerDrawable().getParentView() != null;
                        }
                    });
                    if (z5) {
                        if (!messageObject2.mediaExists && file2 != file) {
                            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda34(messageObject2, file2));
                        }
                        this.audioPlayer.preparePlayer(Uri.fromFile(file2), "other");
                        this.isStreamingCurrentAudio = false;
                    } else {
                        int fileReference2 = FileLoader.getInstance(messageObject2.currentAccount).getFileReference(messageObject2);
                        TLRPC$Document document2 = messageObject.getDocument();
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("?account=");
                        sb2.append(messageObject2.currentAccount);
                        sb2.append("&id=");
                        sb2.append(document2.id);
                        sb2.append("&hash=");
                        sb2.append(document2.access_hash);
                        sb2.append("&dc=");
                        sb2.append(document2.dc_id);
                        sb2.append("&size=");
                        sb2.append(document2.size);
                        sb2.append("&mime=");
                        sb2.append(URLEncoder.encode(document2.mime_type, "UTF-8"));
                        sb2.append("&rid=");
                        sb2.append(fileReference2);
                        sb2.append("&name=");
                        sb2.append(URLEncoder.encode(FileLoader.getDocumentFileName(document2), "UTF-8"));
                        sb2.append("&reference=");
                        byte[] bArr2 = document2.file_reference;
                        if (bArr2 == null) {
                            bArr2 = new byte[0];
                        }
                        sb2.append(Utilities.bytesToHex(bArr2));
                        this.audioPlayer.preparePlayer(Uri.parse("tg://" + messageObject.getFileName() + sb2.toString()), "other");
                        this.isStreamingCurrentAudio = true;
                    }
                    if (messageObject.isVoice()) {
                        String fileName = messageObject.getFileName();
                        if (fileName != null && messageObject.getDuration() >= 300) {
                            float f3 = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getFloat(fileName, -1.0f);
                            if (f3 > 0.0f && f3 < 0.99f) {
                                this.seekToProgressPending = f3;
                                messageObject2.audioProgress = f3;
                            }
                            this.shouldSavePositionForCurrentAudio = fileName;
                        }
                        if (Math.abs(this.currentPlaybackSpeed - 1.0f) > 0.001f) {
                            this.audioPlayer.setPlaybackSpeed(this.currentPlaybackSpeed);
                        }
                        this.audioInfo = null;
                        clearPlaylist();
                    } else {
                        try {
                            this.audioInfo = AudioInfo.getAudioInfo(file2);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        String fileName2 = messageObject.getFileName();
                        if (!TextUtils.isEmpty(fileName2) && messageObject.getDuration() >= 600) {
                            float f4 = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getFloat(fileName2, -1.0f);
                            if (f4 > 0.0f && f4 < 0.999f) {
                                this.seekToProgressPending = f4;
                                messageObject2.audioProgress = f4;
                            }
                            this.shouldSavePositionForCurrentAudio = fileName2;
                            if (Math.abs(this.currentMusicPlaybackSpeed - 1.0f) > 0.001f) {
                                this.audioPlayer.setPlaybackSpeed(this.currentMusicPlaybackSpeed);
                            }
                        }
                    }
                    float f5 = messageObject2.forceSeekTo;
                    if (f5 >= 0.0f) {
                        this.seekToProgressPending = f5;
                        messageObject2.audioProgress = f5;
                        messageObject2.forceSeekTo = -1.0f;
                    }
                    this.audioPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                    this.audioPlayer.play();
                    if (!messageObject.isVoice()) {
                        ValueAnimator valueAnimator = this.audioVolumeAnimator;
                        if (valueAnimator != null) {
                            valueAnimator.removeAllListeners();
                            this.audioVolumeAnimator.cancel();
                        }
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.audioVolume, 1.0f});
                        this.audioVolumeAnimator = ofFloat;
                        ofFloat.addUpdateListener(this.audioVolumeUpdateListener);
                        this.audioVolumeAnimator.setDuration(300);
                        this.audioVolumeAnimator.start();
                    } else {
                        this.audioVolume = 1.0f;
                        setPlayerVolume();
                    }
                    f = 0.0f;
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                    NotificationCenter instance = NotificationCenter.getInstance(messageObject2.currentAccount);
                    int i3 = NotificationCenter.messagePlayingPlayStateChanged;
                    Object[] objArr = new Object[1];
                    MessageObject messageObject5 = this.playingMessageObject;
                    objArr[0] = Integer.valueOf(messageObject5 != null ? messageObject5.getId() : 0);
                    instance.postNotificationName(i3, objArr);
                    VideoPlayer videoPlayer5 = this.audioPlayer;
                    if (videoPlayer5 != null) {
                        videoPlayer5.releasePlayer(true);
                        this.audioPlayer = null;
                        Theme.unrefAudioVisualizeDrawable(this.playingMessageObject);
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
            if (!ApplicationLoader.mainInterfacePaused && (wakeLock = this.proximityWakeLock) != null && !wakeLock.isHeld() && (this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo())) {
                this.proximityWakeLock.acquire();
            }
            startProgressTimer(this.playingMessageObject);
            NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidStart, messageObject2);
            VideoPlayer videoPlayer6 = this.videoPlayer;
            if (videoPlayer6 != null) {
                try {
                    if (this.playingMessageObject.audioProgress != f) {
                        long duration = videoPlayer6.getDuration();
                        if (duration == -9223372036854775807L) {
                            duration = ((long) this.playingMessageObject.getDuration()) * 1000;
                        }
                        MessageObject messageObject6 = this.playingMessageObject;
                        int i4 = (int) (((float) duration) * messageObject6.audioProgress);
                        int i5 = messageObject6.audioProgressMs;
                        if (i5 != 0) {
                            messageObject6.audioProgressMs = 0;
                            i4 = i5;
                        }
                        this.videoPlayer.seekTo((long) i4);
                    }
                } catch (Exception e4) {
                    MessageObject messageObject7 = this.playingMessageObject;
                    messageObject7.audioProgress = f;
                    messageObject7.audioProgressSec = 0;
                    NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), null);
                    FileLog.e((Throwable) e4);
                }
                this.videoPlayer.play();
            } else {
                VideoPlayer videoPlayer7 = this.audioPlayer;
                if (videoPlayer7 != null) {
                    try {
                        if (this.playingMessageObject.audioProgress != f) {
                            long duration2 = videoPlayer7.getDuration();
                            if (duration2 == -9223372036854775807L) {
                                duration2 = ((long) this.playingMessageObject.getDuration()) * 1000;
                            }
                            this.audioPlayer.seekTo((long) ((int) (((float) duration2) * this.playingMessageObject.audioProgress)));
                        }
                    } catch (Exception e5) {
                        this.playingMessageObject.resetPlayingProgress();
                        NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), null);
                        FileLog.e((Throwable) e5);
                    }
                }
            }
            MessageObject messageObject8 = this.playingMessageObject;
            if (messageObject8 == null || !messageObject8.isMusic()) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                return true;
            }
            try {
                ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                return true;
            } catch (Throwable th) {
                FileLog.e(th);
                return true;
            }
        } else {
            FileLoader.getInstance(messageObject2.currentAccount).loadFile(messageObject.getDocument(), messageObject2, 0, 0);
            this.downloadingCurrentMessage = true;
            this.isPaused = false;
            this.lastProgress = 0;
            this.audioInfo = null;
            this.playingMessageObject = messageObject2;
            if (messageObject.isMusic()) {
                try {
                    ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                } catch (Throwable th2) {
                    FileLog.e(th2);
                }
            } else {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$playMessage$19() {
        cleanupPlayer(true, true);
    }

    public AudioInfo getAudioInfo() {
        return this.audioInfo;
    }

    public void setPlaybackOrderType(int i) {
        boolean z = SharedConfig.shuffleMusic;
        SharedConfig.setPlaybackOrderType(i);
        boolean z2 = SharedConfig.shuffleMusic;
        if (z == z2) {
            return;
        }
        if (z2) {
            buildShuffledPlayList();
            return;
        }
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null) {
            int indexOf = this.playlist.indexOf(messageObject);
            this.currentPlaylistNum = indexOf;
            if (indexOf == -1) {
                clearPlaylist();
                cleanupPlayer(true, true);
            }
        }
    }

    public boolean isStreamingCurrentAudio() {
        return this.isStreamingCurrentAudio;
    }

    public boolean isCurrentPlayer(VideoPlayer videoPlayer2) {
        return this.videoPlayer == videoPlayer2 || this.audioPlayer == videoPlayer2;
    }

    /* renamed from: pauseMessage */
    public boolean lambda$startAudioAgain$7(MessageObject messageObject) {
        if (!((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject))) {
            stopProgressTimer();
            try {
                if (this.audioPlayer == null) {
                    VideoPlayer videoPlayer2 = this.videoPlayer;
                    if (videoPlayer2 != null) {
                        videoPlayer2.pause();
                    }
                } else if (this.playingMessageObject.isVoice() || ((float) this.playingMessageObject.getDuration()) * (1.0f - this.playingMessageObject.audioProgress) <= 1000.0f) {
                    this.audioPlayer.pause();
                } else {
                    ValueAnimator valueAnimator = this.audioVolumeAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllUpdateListeners();
                        this.audioVolumeAnimator.cancel();
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                    this.audioVolumeAnimator = ofFloat;
                    ofFloat.addUpdateListener(this.audioVolumeUpdateListener);
                    this.audioVolumeAnimator.setDuration(300);
                    this.audioVolumeAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (MediaController.this.audioPlayer != null) {
                                MediaController.this.audioPlayer.pause();
                            }
                        }
                    });
                    this.audioVolumeAnimator.start();
                }
                this.isPaused = true;
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
                return true;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                this.isPaused = false;
            }
        }
        return false;
    }

    private boolean resumeAudio(MessageObject messageObject) {
        if (!((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject))) {
            try {
                startProgressTimer(this.playingMessageObject);
                ValueAnimator valueAnimator = this.audioVolumeAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.audioVolumeAnimator.cancel();
                }
                if (!messageObject.isVoice()) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.audioVolume, 1.0f});
                    this.audioVolumeAnimator = ofFloat;
                    ofFloat.addUpdateListener(this.audioVolumeUpdateListener);
                    this.audioVolumeAnimator.setDuration(300);
                    this.audioVolumeAnimator.start();
                } else {
                    this.audioVolume = 1.0f;
                    setPlayerVolume();
                }
                VideoPlayer videoPlayer2 = this.audioPlayer;
                if (videoPlayer2 != null) {
                    videoPlayer2.play();
                } else {
                    VideoPlayer videoPlayer3 = this.videoPlayer;
                    if (videoPlayer3 != null) {
                        videoPlayer3.play();
                    }
                }
                checkAudioFocus(messageObject);
                this.isPaused = false;
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
                return true;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
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
        MessageObject messageObject2;
        if (!((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || (messageObject2 = this.playingMessageObject) == null)) {
            long j = messageObject2.eventId;
            if (j != 0 && j == messageObject.eventId) {
                return !this.downloadingCurrentMessage;
            }
            if (isSamePlayingMessage(messageObject)) {
                return !this.downloadingCurrentMessage;
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

    public void setReplyingMessage(MessageObject messageObject, MessageObject messageObject2) {
        this.recordReplyingMsg = messageObject;
        this.recordReplyingTopMsg = messageObject2;
    }

    public void requestAudioFocus(boolean z) {
        if (z) {
            if (!this.hasRecordAudioFocus && SharedConfig.pauseMusicOnRecord && NotificationsController.audioManager.requestAudioFocus(this.audioRecordFocusChangedListener, 3, 2) == 1) {
                this.hasRecordAudioFocus = true;
            }
        } else if (this.hasRecordAudioFocus) {
            NotificationsController.audioManager.abandonAudioFocus(this.audioRecordFocusChangedListener);
            this.hasRecordAudioFocus = false;
        }
    }

    public void startRecording(int i, long j, MessageObject messageObject, MessageObject messageObject2, int i2) {
        MessageObject messageObject3 = this.playingMessageObject;
        boolean z = messageObject3 != null && isPlayingMessage(messageObject3) && !isMessagePaused();
        requestAudioFocus(true);
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        MediaController$$ExternalSyntheticLambda21 mediaController$$ExternalSyntheticLambda21 = new MediaController$$ExternalSyntheticLambda21(this, i, i2, j, messageObject, messageObject2);
        this.recordStartRunnable = mediaController$$ExternalSyntheticLambda21;
        dispatchQueue.postRunnable(mediaController$$ExternalSyntheticLambda21, z ? 500 : 50);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startRecording$26(int i, int i2, long j, MessageObject messageObject, MessageObject messageObject2) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda19(this, i, i2));
            return;
        }
        this.sendAfterDone = 0;
        TLRPC$TL_document tLRPC$TL_document = new TLRPC$TL_document();
        this.recordingAudio = tLRPC$TL_document;
        this.recordingGuid = i2;
        tLRPC$TL_document.file_reference = new byte[0];
        tLRPC$TL_document.dc_id = Integer.MIN_VALUE;
        tLRPC$TL_document.id = (long) SharedConfig.getLastLocalId();
        this.recordingAudio.user_id = UserConfig.getInstance(i).getClientUserId();
        TLRPC$TL_document tLRPC$TL_document2 = this.recordingAudio;
        tLRPC$TL_document2.mime_type = "audio/ogg";
        tLRPC$TL_document2.file_reference = new byte[0];
        SharedConfig.saveConfig();
        File file = new File(FileLoader.getDirectory(4), FileLoader.getAttachFileName(this.recordingAudio));
        this.recordingAudioFile = file;
        try {
            if (startRecord(file.getAbsolutePath(), 16000) == 0) {
                AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda18(this, i, i2));
                return;
            }
            this.audioRecorder = new AudioRecord(0, this.sampleRate, 16, 2, this.recordBufferSize);
            this.recordStartTime = System.currentTimeMillis();
            this.recordTimeCount = 0;
            this.samplesCount = 0;
            this.recordDialogId = j;
            this.recordingCurrentAccount = i;
            this.recordReplyingMsg = messageObject;
            this.recordReplyingTopMsg = messageObject2;
            this.fileBuffer.rewind();
            this.audioRecorder.startRecording();
            this.recordQueue.postRunnable(this.recordRunnable);
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda17(this, i, i2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.recordingAudio = null;
            stopRecord();
            this.recordingAudioFile.delete();
            this.recordingAudioFile = null;
            try {
                this.audioRecorder.release();
                this.audioRecorder = null;
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda20(this, i, i2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startRecording$22(int i, int i2) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startRecording$23(int i, int i2) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startRecording$24(int i, int i2) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startRecording$25(int i, int i2) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(i2), Boolean.TRUE);
    }

    public void generateWaveform(MessageObject messageObject) {
        String str = messageObject.getId() + "_" + messageObject.getDialogId();
        String absolutePath = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(str)) {
            this.generatingWaveform.put(str, messageObject);
            Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda25(this, absolutePath, str, messageObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$generateWaveform$28(String str, String str2, MessageObject messageObject) {
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda26(this, str2, getWaveform(str), messageObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$generateWaveform$27(String str, byte[] bArr, MessageObject messageObject) {
        MessageObject remove = this.generatingWaveform.remove(str);
        if (remove != null && bArr != null && remove.getDocument() != null) {
            int i = 0;
            while (true) {
                if (i >= remove.getDocument().attributes.size()) {
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = remove.getDocument().attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                    tLRPC$DocumentAttribute.waveform = bArr;
                    tLRPC$DocumentAttribute.flags |= 4;
                    break;
                }
                i++;
            }
            TLRPC$TL_messages_messages tLRPC$TL_messages_messages = new TLRPC$TL_messages_messages();
            tLRPC$TL_messages_messages.messages.add(remove.messageOwner);
            MessagesStorage.getInstance(remove.currentAccount).putMessages((TLRPC$messages_Messages) tLRPC$TL_messages_messages, remove.getDialogId(), -1, 0, false, messageObject.scheduled);
            ArrayList arrayList = new ArrayList();
            arrayList.add(remove);
            NotificationCenter.getInstance(remove.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(remove.getDialogId()), arrayList);
        }
    }

    /* access modifiers changed from: private */
    public void stopRecordingInternal(int i, boolean z, int i2) {
        if (i != 0) {
            this.fileEncodingQueue.postRunnable(new MediaController$$ExternalSyntheticLambda31(this, this.recordingAudio, this.recordingAudioFile, i, z, i2));
        } else {
            File file = this.recordingAudioFile;
            if (file != null) {
                file.delete();
            }
            requestAudioFocus(false);
        }
        try {
            AudioRecord audioRecord = this.audioRecorder;
            if (audioRecord != null) {
                audioRecord.release();
                this.audioRecorder = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.recordingAudio = null;
        this.recordingAudioFile = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$stopRecordingInternal$30(TLRPC$TL_document tLRPC$TL_document, File file, int i, boolean z, int i2) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda32(this, tLRPC$TL_document, file, i, z, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$stopRecordingInternal$29(TLRPC$TL_document tLRPC$TL_document, File file, int i, boolean z, int i2) {
        boolean z2;
        char c;
        TLRPC$TL_document tLRPC$TL_document2 = tLRPC$TL_document;
        int i3 = i;
        tLRPC$TL_document2.date = ConnectionsManager.getInstance(this.recordingCurrentAccount).getCurrentTime();
        tLRPC$TL_document2.size = (int) file.length();
        TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio = new TLRPC$TL_documentAttributeAudio();
        tLRPC$TL_documentAttributeAudio.voice = true;
        short[] sArr = this.recordSamples;
        byte[] waveform2 = getWaveform2(sArr, sArr.length);
        tLRPC$TL_documentAttributeAudio.waveform = waveform2;
        if (waveform2 != null) {
            tLRPC$TL_documentAttributeAudio.flags |= 4;
        }
        long j = this.recordTimeCount;
        tLRPC$TL_documentAttributeAudio.duration = (int) (j / 1000);
        tLRPC$TL_document2.attributes.add(tLRPC$TL_documentAttributeAudio);
        if (j > 700) {
            if (i3 == 1) {
                c = 1;
                SendMessagesHelper.getInstance(this.recordingCurrentAccount).sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, file.getAbsolutePath(), this.recordDialogId, this.recordReplyingMsg, this.recordReplyingTopMsg, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2, 0, (Object) null, (MessageObject.SendAnimationData) null);
            } else {
                c = 1;
            }
            NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
            int i4 = NotificationCenter.audioDidSent;
            Object[] objArr = new Object[3];
            z2 = false;
            objArr[0] = Integer.valueOf(this.recordingGuid);
            String str = null;
            int i5 = i;
            objArr[c] = i5 == 2 ? tLRPC$TL_document : null;
            if (i5 == 2) {
                str = file.getAbsolutePath();
            }
            objArr[2] = str;
            instance.postNotificationName(i4, objArr);
        } else {
            z2 = false;
            NotificationCenter.getInstance(this.recordingCurrentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), Boolean.FALSE, Integer.valueOf((int) j));
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
        this.recordQueue.postRunnable(new MediaController$$ExternalSyntheticLambda23(this, i, z, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$stopRecording$32(int i, boolean z, int i2) {
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
                FileLog.e((Throwable) e);
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
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda16(this, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$stopRecording$31(int i) {
        NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
        int i2 = NotificationCenter.recordStopped;
        Object[] objArr = new Object[2];
        int i3 = 0;
        objArr[0] = Integer.valueOf(this.recordingGuid);
        if (i == 2) {
            i3 = 1;
        }
        objArr[1] = Integer.valueOf(i3);
        instance.postNotificationName(i2, objArr);
    }

    private static class MediaLoader implements NotificationCenter.NotificationCenterDelegate {
        private boolean cancelled;
        private int copiedFiles;
        private AccountInstance currentAccount;
        private boolean finished;
        private float finishedProgress;
        private boolean isMusic;
        private HashMap<String, MessageObject> loadingMessageObjects = new HashMap<>();
        private ArrayList<MessageObject> messageObjects;
        private MessagesStorage.IntCallback onFinishRunnable;
        private AlertDialog progressDialog;
        private CountDownLatch waitingForFile;

        public MediaLoader(Context context, AccountInstance accountInstance, ArrayList<MessageObject> arrayList, MessagesStorage.IntCallback intCallback) {
            this.currentAccount = accountInstance;
            this.messageObjects = arrayList;
            this.onFinishRunnable = intCallback;
            this.isMusic = arrayList.get(0).isMusic();
            this.currentAccount.getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
            this.currentAccount.getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
            this.currentAccount.getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
            AlertDialog alertDialog = new AlertDialog(context, 2);
            this.progressDialog = alertDialog;
            alertDialog.setMessage(LocaleController.getString("Loading", NUM));
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setCancelable(true);
            this.progressDialog.setOnCancelListener(new MediaController$MediaLoader$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(DialogInterface dialogInterface) {
            this.cancelled = true;
        }

        public void start() {
            AndroidUtilities.runOnUIThread(new MediaController$MediaLoader$$ExternalSyntheticLambda4(this), 250);
            new Thread(new MediaController$MediaLoader$$ExternalSyntheticLambda3(this)).start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$start$1() {
            if (!this.finished) {
                this.progressDialog.show();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$start$2() {
            File file;
            String str;
            try {
                int i = 0;
                if (Build.VERSION.SDK_INT >= 29) {
                    int size = this.messageObjects.size();
                    while (true) {
                        if (i >= size) {
                            break;
                        }
                        MessageObject messageObject = this.messageObjects.get(i);
                        String str2 = messageObject.messageOwner.attachPath;
                        String documentName = messageObject.getDocumentName();
                        if (str2 != null && str2.length() > 0 && !new File(str2).exists()) {
                            str2 = null;
                        }
                        if (str2 == null || str2.length() == 0) {
                            str2 = FileLoader.getPathToMessage(messageObject.messageOwner).toString();
                        }
                        File file2 = new File(str2);
                        if (!file2.exists()) {
                            this.waitingForFile = new CountDownLatch(1);
                            addMessageToLoad(messageObject);
                            this.waitingForFile.await();
                        }
                        if (this.cancelled) {
                            break;
                        }
                        if (file2.exists()) {
                            boolean unused = MediaController.saveFileInternal(this.isMusic ? 3 : 2, file2, documentName);
                        }
                        i++;
                    }
                } else {
                    if (this.isMusic) {
                        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    } else {
                        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    }
                    file.mkdir();
                    int size2 = this.messageObjects.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        MessageObject messageObject2 = this.messageObjects.get(i2);
                        String documentName2 = messageObject2.getDocumentName();
                        File file3 = new File(file, documentName2);
                        if (file3.exists()) {
                            int lastIndexOf = documentName2.lastIndexOf(46);
                            int i3 = 0;
                            while (true) {
                                if (i3 >= 10) {
                                    break;
                                }
                                if (lastIndexOf != -1) {
                                    str = documentName2.substring(0, lastIndexOf) + "(" + (i3 + 1) + ")" + documentName2.substring(lastIndexOf);
                                } else {
                                    str = documentName2 + "(" + (i3 + 1) + ")";
                                }
                                File file4 = new File(file, str);
                                if (!file4.exists()) {
                                    file3 = file4;
                                    break;
                                } else {
                                    i3++;
                                    file3 = file4;
                                }
                            }
                        }
                        if (!file3.exists()) {
                            file3.createNewFile();
                        }
                        String str3 = messageObject2.messageOwner.attachPath;
                        if (str3 != null && str3.length() > 0 && !new File(str3).exists()) {
                            str3 = null;
                        }
                        if (str3 == null || str3.length() == 0) {
                            str3 = FileLoader.getPathToMessage(messageObject2.messageOwner).toString();
                        }
                        File file5 = new File(str3);
                        if (!file5.exists()) {
                            this.waitingForFile = new CountDownLatch(1);
                            addMessageToLoad(messageObject2);
                            this.waitingForFile.await();
                        }
                        if (file5.exists()) {
                            copyFile(file5, file3, messageObject2.getMimeType());
                        }
                    }
                }
                checkIfFinished();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        private void checkIfFinished() {
            if (this.loadingMessageObjects.isEmpty()) {
                AndroidUtilities.runOnUIThread(new MediaController$MediaLoader$$ExternalSyntheticLambda2(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$checkIfFinished$4() {
            try {
                if (this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                } else {
                    this.finished = true;
                }
                if (this.onFinishRunnable != null) {
                    AndroidUtilities.runOnUIThread(new MediaController$MediaLoader$$ExternalSyntheticLambda5(this));
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoaded);
            this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadFailed);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$checkIfFinished$3() {
            this.onFinishRunnable.run(this.copiedFiles);
        }

        private void addMessageToLoad(MessageObject messageObject) {
            AndroidUtilities.runOnUIThread(new MediaController$MediaLoader$$ExternalSyntheticLambda9(this, messageObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$addMessageToLoad$5(MessageObject messageObject) {
            TLRPC$Document document = messageObject.getDocument();
            if (document != null) {
                this.loadingMessageObjects.put(FileLoader.getAttachFileName(document), messageObject);
                this.currentAccount.getFileLoader().loadFile(document, messageObject, 1, 0);
            }
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(5:83|84|(2:86|87)|88|89) */
        /* JADX WARNING: Can't wrap try/catch for region: R(5:91|94|(2:96|97)|98|99) */
        /* JADX WARNING: Can't wrap try/catch for region: R(6:29|103|104|105|106|107) */
        /* JADX WARNING: Missing exception handler attribute for start block: B:106:0x017c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:88:0x0162 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:98:0x0171 */
        /* JADX WARNING: Removed duplicated region for block: B:86:0x015f A[SYNTHETIC, Splitter:B:86:0x015f] */
        /* JADX WARNING: Removed duplicated region for block: B:96:0x016e A[SYNTHETIC, Splitter:B:96:0x016e] */
        /* JADX WARNING: Unknown top exception splitter block from list: {B:78:0x0151=Splitter:B:78:0x0151, B:98:0x0171=Splitter:B:98:0x0171, B:70:0x0145=Splitter:B:70:0x0145} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean copyFile(java.io.File r32, java.io.File r33, java.lang.String r34) {
            /*
                r31 = this;
                r1 = r31
                android.net.Uri r0 = android.net.Uri.fromFile(r32)
                boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)
                r2 = 0
                if (r0 == 0) goto L_0x000e
                return r2
            L_0x000e:
                java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ Exception -> 0x017f }
                r0 = r32
                r3.<init>(r0)     // Catch:{ Exception -> 0x017f }
                java.nio.channels.FileChannel r10 = r3.getChannel()     // Catch:{ all -> 0x0174 }
                java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0167 }
                r11 = r33
                r0.<init>(r11)     // Catch:{ all -> 0x0165 }
                java.nio.channels.FileChannel r12 = r0.getChannel()     // Catch:{ all -> 0x0165 }
                long r13 = r10.size()     // Catch:{ all -> 0x015a }
                java.lang.Class<java.io.FileDescriptor> r0 = java.io.FileDescriptor.class
                java.lang.String r4 = "getInt$"
                java.lang.Class[] r5 = new java.lang.Class[r2]     // Catch:{ all -> 0x0063 }
                java.lang.reflect.Method r0 = r0.getDeclaredMethod(r4, r5)     // Catch:{ all -> 0x0063 }
                java.io.FileDescriptor r4 = r3.getFD()     // Catch:{ all -> 0x0063 }
                java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch:{ all -> 0x0063 }
                java.lang.Object r0 = r0.invoke(r4, r5)     // Catch:{ all -> 0x0063 }
                java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0063 }
                int r0 = r0.intValue()     // Catch:{ all -> 0x0063 }
                boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r0)     // Catch:{ all -> 0x0063 }
                if (r0 == 0) goto L_0x0067
                org.telegram.ui.ActionBar.AlertDialog r0 = r1.progressDialog     // Catch:{ all -> 0x0063 }
                if (r0 == 0) goto L_0x0054
                org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda1 r0 = new org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda1     // Catch:{ all -> 0x0063 }
                r0.<init>(r1)     // Catch:{ all -> 0x0063 }
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ all -> 0x0063 }
            L_0x0054:
                if (r12 == 0) goto L_0x0059
                r12.close()     // Catch:{ all -> 0x0165 }
            L_0x0059:
                r10.close()     // Catch:{ all -> 0x0060 }
                r3.close()     // Catch:{ Exception -> 0x017d }
                return r2
            L_0x0060:
                r0 = move-exception
                goto L_0x0177
            L_0x0063:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x015a }
            L_0x0067:
                r4 = 0
                r8 = r4
                r15 = r8
            L_0x006b:
                r0 = 1120403456(0x42CLASSNAME, float:100.0)
                int r4 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
                if (r4 >= 0) goto L_0x00be
                boolean r4 = r1.cancelled     // Catch:{ all -> 0x015a }
                if (r4 == 0) goto L_0x0076
                goto L_0x00be
            L_0x0076:
                long r4 = r13 - r8
                r6 = 4096(0x1000, double:2.0237E-320)
                long r17 = java.lang.Math.min(r6, r4)     // Catch:{ all -> 0x015a }
                r4 = r12
                r5 = r10
                r19 = r6
                r6 = r8
                r21 = r3
                r2 = r8
                r8 = r17
                r4.transferFrom(r5, r6, r8)     // Catch:{ all -> 0x0158 }
                long r8 = r2 + r19
                int r4 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
                if (r4 >= 0) goto L_0x009c
                long r4 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0158 }
                r6 = 500(0x1f4, double:2.47E-321)
                long r4 = r4 - r6
                int r6 = (r15 > r4 ? 1 : (r15 == r4 ? 0 : -1))
                if (r6 > 0) goto L_0x00ba
            L_0x009c:
                long r4 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0158 }
                float r6 = r1.finishedProgress     // Catch:{ all -> 0x0158 }
                java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r1.messageObjects     // Catch:{ all -> 0x0158 }
                int r7 = r7.size()     // Catch:{ all -> 0x0158 }
                float r7 = (float) r7     // Catch:{ all -> 0x0158 }
                float r0 = r0 / r7
                float r2 = (float) r2     // Catch:{ all -> 0x0158 }
                float r0 = r0 * r2
                float r2 = (float) r13     // Catch:{ all -> 0x0158 }
                float r0 = r0 / r2
                float r6 = r6 + r0
                int r0 = (int) r6     // Catch:{ all -> 0x0158 }
                org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda6 r2 = new org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda6     // Catch:{ all -> 0x0158 }
                r2.<init>(r1, r0)     // Catch:{ all -> 0x0158 }
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x0158 }
                r15 = r4
            L_0x00ba:
                r3 = r21
                r2 = 0
                goto L_0x006b
            L_0x00be:
                r21 = r3
                boolean r2 = r1.cancelled     // Catch:{ all -> 0x0158 }
                if (r2 != 0) goto L_0x014c
                boolean r2 = r1.isMusic     // Catch:{ all -> 0x0158 }
                r3 = 1
                if (r2 == 0) goto L_0x00cd
                org.telegram.messenger.AndroidUtilities.addMediaToGallery((java.io.File) r33)     // Catch:{ all -> 0x0158 }
                goto L_0x0125
            L_0x00cd:
                android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0158 }
                java.lang.String r4 = "download"
                java.lang.Object r2 = r2.getSystemService(r4)     // Catch:{ all -> 0x0158 }
                r22 = r2
                android.app.DownloadManager r22 = (android.app.DownloadManager) r22     // Catch:{ all -> 0x0158 }
                boolean r2 = android.text.TextUtils.isEmpty(r34)     // Catch:{ all -> 0x0158 }
                java.lang.String r4 = "text/plain"
                if (r2 == 0) goto L_0x010c
                android.webkit.MimeTypeMap r2 = android.webkit.MimeTypeMap.getSingleton()     // Catch:{ all -> 0x0158 }
                java.lang.String r5 = r33.getName()     // Catch:{ all -> 0x0158 }
                r6 = 46
                int r6 = r5.lastIndexOf(r6)     // Catch:{ all -> 0x0158 }
                r7 = -1
                if (r6 == r7) goto L_0x0109
                int r6 = r6 + r3
                java.lang.String r5 = r5.substring(r6)     // Catch:{ all -> 0x0158 }
                java.lang.String r5 = r5.toLowerCase()     // Catch:{ all -> 0x0158 }
                java.lang.String r2 = r2.getMimeTypeFromExtension(r5)     // Catch:{ all -> 0x0158 }
                boolean r5 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0158 }
                if (r5 == 0) goto L_0x0106
                r2 = r4
            L_0x0106:
                r26 = r2
                goto L_0x010e
            L_0x0109:
                r26 = r4
                goto L_0x010e
            L_0x010c:
                r26 = r34
            L_0x010e:
                java.lang.String r23 = r33.getName()     // Catch:{ all -> 0x0158 }
                java.lang.String r24 = r33.getName()     // Catch:{ all -> 0x0158 }
                r25 = 0
                java.lang.String r27 = r33.getAbsolutePath()     // Catch:{ all -> 0x0158 }
                long r28 = r33.length()     // Catch:{ all -> 0x0158 }
                r30 = 1
                r22.addCompletedDownload(r23, r24, r25, r26, r27, r28, r30)     // Catch:{ all -> 0x0158 }
            L_0x0125:
                float r2 = r1.finishedProgress     // Catch:{ all -> 0x0158 }
                java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r1.messageObjects     // Catch:{ all -> 0x0158 }
                int r4 = r4.size()     // Catch:{ all -> 0x0158 }
                float r4 = (float) r4     // Catch:{ all -> 0x0158 }
                float r0 = r0 / r4
                float r2 = r2 + r0
                r1.finishedProgress = r2     // Catch:{ all -> 0x0158 }
                int r0 = (int) r2     // Catch:{ all -> 0x0158 }
                org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda8 r2 = new org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda8     // Catch:{ all -> 0x0158 }
                r2.<init>(r1, r0)     // Catch:{ all -> 0x0158 }
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x0158 }
                int r0 = r1.copiedFiles     // Catch:{ all -> 0x0158 }
                int r0 = r0 + r3
                r1.copiedFiles = r0     // Catch:{ all -> 0x0158 }
                if (r12 == 0) goto L_0x0145
                r12.close()     // Catch:{ all -> 0x0163 }
            L_0x0145:
                r10.close()     // Catch:{ all -> 0x0172 }
                r21.close()     // Catch:{ Exception -> 0x017d }
                return r3
            L_0x014c:
                if (r12 == 0) goto L_0x0151
                r12.close()     // Catch:{ all -> 0x0163 }
            L_0x0151:
                r10.close()     // Catch:{ all -> 0x0172 }
                r21.close()     // Catch:{ Exception -> 0x017d }
                goto L_0x0185
            L_0x0158:
                r0 = move-exception
                goto L_0x015d
            L_0x015a:
                r0 = move-exception
                r21 = r3
            L_0x015d:
                if (r12 == 0) goto L_0x0162
                r12.close()     // Catch:{ all -> 0x0162 }
            L_0x0162:
                throw r0     // Catch:{ all -> 0x0163 }
            L_0x0163:
                r0 = move-exception
                goto L_0x016c
            L_0x0165:
                r0 = move-exception
                goto L_0x016a
            L_0x0167:
                r0 = move-exception
                r11 = r33
            L_0x016a:
                r21 = r3
            L_0x016c:
                if (r10 == 0) goto L_0x0171
                r10.close()     // Catch:{ all -> 0x0171 }
            L_0x0171:
                throw r0     // Catch:{ all -> 0x0172 }
            L_0x0172:
                r0 = move-exception
                goto L_0x0179
            L_0x0174:
                r0 = move-exception
                r11 = r33
            L_0x0177:
                r21 = r3
            L_0x0179:
                r21.close()     // Catch:{ all -> 0x017c }
            L_0x017c:
                throw r0     // Catch:{ Exception -> 0x017d }
            L_0x017d:
                r0 = move-exception
                goto L_0x0182
            L_0x017f:
                r0 = move-exception
                r11 = r33
            L_0x0182:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0185:
                r33.delete()
                r2 = 0
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.MediaLoader.copyFile(java.io.File, java.io.File, java.lang.String):boolean");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$copyFile$6() {
            try {
                this.progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$copyFile$7(int i) {
            try {
                this.progressDialog.setProgress(i);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$copyFile$8(int i) {
            try {
                this.progressDialog.setProgress(i);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.fileLoaded || i == NotificationCenter.fileLoadFailed) {
                if (this.loadingMessageObjects.remove(objArr[0]) != null) {
                    this.waitingForFile.countDown();
                }
            } else if (i == NotificationCenter.fileLoadProgressChanged) {
                if (this.loadingMessageObjects.containsKey(objArr[0])) {
                    AndroidUtilities.runOnUIThread(new MediaController$MediaLoader$$ExternalSyntheticLambda7(this, (int) (this.finishedProgress + (((((float) objArr[1].longValue()) / ((float) objArr[2].longValue())) / ((float) this.messageObjects.size())) * 100.0f))));
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$didReceivedNotification$9(int i) {
            try {
                this.progressDialog.setProgress(i);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void saveFilesFromMessages(Context context, AccountInstance accountInstance, ArrayList<MessageObject> arrayList, MessagesStorage.IntCallback intCallback) {
        if (arrayList != null && !arrayList.isEmpty()) {
            new MediaLoader(context, accountInstance, arrayList, intCallback).start();
        }
    }

    public static void saveFile(String str, Context context, int i, String str2, String str3) {
        saveFile(str, context, i, str2, str3, (Runnable) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002a A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x002b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void saveFile(java.lang.String r13, android.content.Context r14, int r15, java.lang.String r16, java.lang.String r17, java.lang.Runnable r18) {
        /*
            r0 = r13
            r1 = r14
            if (r0 == 0) goto L_0x0081
            if (r1 != 0) goto L_0x0008
            goto L_0x0081
        L_0x0008:
            boolean r2 = android.text.TextUtils.isEmpty(r13)
            r3 = 0
            if (r2 != 0) goto L_0x0027
            java.io.File r2 = new java.io.File
            r2.<init>(r13)
            boolean r0 = r2.exists()
            if (r0 == 0) goto L_0x0027
            android.net.Uri r0 = android.net.Uri.fromFile(r2)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)
            if (r0 == 0) goto L_0x0025
            goto L_0x0027
        L_0x0025:
            r6 = r2
            goto L_0x0028
        L_0x0027:
            r6 = r3
        L_0x0028:
            if (r6 != 0) goto L_0x002b
            return
        L_0x002b:
            r0 = 1
            boolean[] r9 = new boolean[r0]
            r2 = 0
            r9[r2] = r2
            boolean r4 = r6.exists()
            if (r4 == 0) goto L_0x0081
            boolean[] r12 = new boolean[r0]
            if (r15 == 0) goto L_0x006b
            org.telegram.ui.ActionBar.AlertDialog r4 = new org.telegram.ui.ActionBar.AlertDialog     // Catch:{ Exception -> 0x0067 }
            r5 = 2
            r4.<init>(r14, r5)     // Catch:{ Exception -> 0x0067 }
            java.lang.String r1 = "Loading"
            r5 = 2131626173(0x7f0e08bd, float:1.8879575E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r5)     // Catch:{ Exception -> 0x0067 }
            r4.setMessage(r1)     // Catch:{ Exception -> 0x0067 }
            r4.setCanceledOnTouchOutside(r2)     // Catch:{ Exception -> 0x0067 }
            r4.setCancelable(r0)     // Catch:{ Exception -> 0x0067 }
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda1 r0 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda1     // Catch:{ Exception -> 0x0067 }
            r0.<init>(r9)     // Catch:{ Exception -> 0x0067 }
            r4.setOnCancelListener(r0)     // Catch:{ Exception -> 0x0067 }
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda39 r0 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda39     // Catch:{ Exception -> 0x0067 }
            r0.<init>(r12, r4)     // Catch:{ Exception -> 0x0067 }
            r1 = 250(0xfa, double:1.235E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)     // Catch:{ Exception -> 0x0067 }
            r8 = r4
            goto L_0x006c
        L_0x0067:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x006b:
            r8 = r3
        L_0x006c:
            java.lang.Thread r0 = new java.lang.Thread
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda5 r1 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda5
            r4 = r1
            r5 = r15
            r7 = r16
            r10 = r17
            r11 = r18
            r4.<init>(r5, r6, r7, r8, r9, r10, r11, r12)
            r0.<init>(r1)
            r0.start()
        L_0x0081:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.saveFile(java.lang.String, android.content.Context, int, java.lang.String, java.lang.String, java.lang.Runnable):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveFile$33(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveFile$34(boolean[] zArr, AlertDialog alertDialog) {
        if (!zArr[0]) {
            alertDialog.show();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:82|83|(2:85|86)|87|88) */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:90|91|(2:93|94)|95|96) */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:98|99|100|101|102|103) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:102:0x01ad */
    /* JADX WARNING: Missing exception handler attribute for start block: B:87:0x0199 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:95:0x01a4 */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x01bc A[Catch:{ Exception -> 0x01f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01c1 A[Catch:{ Exception -> 0x01f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x01c4 A[Catch:{ Exception -> 0x01f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01ff  */
    /* JADX WARNING: Removed duplicated region for block: B:133:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0196 A[SYNTHETIC, Splitter:B:85:0x0196] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:95:0x01a4=Splitter:B:95:0x01a4, B:77:0x0188=Splitter:B:77:0x0188} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$saveFile$38(int r20, java.io.File r21, java.lang.String r22, org.telegram.ui.ActionBar.AlertDialog r23, boolean[] r24, java.lang.String r25, java.lang.Runnable r26, boolean[] r27) {
        /*
            r1 = r20
            r0 = r21
            r2 = r22
            r3 = r23
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x01f9 }
            r5 = 29
            if (r4 < r5) goto L_0x0015
            r2 = 0
            boolean r0 = saveFileInternal(r1, r0, r2)     // Catch:{ Exception -> 0x01f9 }
            goto L_0x01f1
        L_0x0015:
            r4 = 2
            java.lang.String r5 = "Telegram"
            r6 = 1
            r7 = 0
            if (r1 != 0) goto L_0x0039
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r8 = android.os.Environment.DIRECTORY_PICTURES     // Catch:{ Exception -> 0x01f9 }
            java.io.File r8 = android.os.Environment.getExternalStoragePublicDirectory(r8)     // Catch:{ Exception -> 0x01f9 }
            r2.<init>(r8, r5)     // Catch:{ Exception -> 0x01f9 }
            r2.mkdirs()     // Catch:{ Exception -> 0x01f9 }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r8 = org.telegram.messenger.FileLoader.getFileExtension(r21)     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r8 = org.telegram.messenger.AndroidUtilities.generateFileName(r7, r8)     // Catch:{ Exception -> 0x01f9 }
            r5.<init>(r2, r8)     // Catch:{ Exception -> 0x01f9 }
            goto L_0x00d7
        L_0x0039:
            if (r1 != r6) goto L_0x0058
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r8 = android.os.Environment.DIRECTORY_MOVIES     // Catch:{ Exception -> 0x01f9 }
            java.io.File r8 = android.os.Environment.getExternalStoragePublicDirectory(r8)     // Catch:{ Exception -> 0x01f9 }
            r2.<init>(r8, r5)     // Catch:{ Exception -> 0x01f9 }
            r2.mkdirs()     // Catch:{ Exception -> 0x01f9 }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r8 = org.telegram.messenger.FileLoader.getFileExtension(r21)     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r8 = org.telegram.messenger.AndroidUtilities.generateFileName(r6, r8)     // Catch:{ Exception -> 0x01f9 }
            r5.<init>(r2, r8)     // Catch:{ Exception -> 0x01f9 }
            goto L_0x00d7
        L_0x0058:
            if (r1 != r4) goto L_0x0061
            java.lang.String r8 = android.os.Environment.DIRECTORY_DOWNLOADS     // Catch:{ Exception -> 0x01f9 }
            java.io.File r8 = android.os.Environment.getExternalStoragePublicDirectory(r8)     // Catch:{ Exception -> 0x01f9 }
            goto L_0x0067
        L_0x0061:
            java.lang.String r8 = android.os.Environment.DIRECTORY_MUSIC     // Catch:{ Exception -> 0x01f9 }
            java.io.File r8 = android.os.Environment.getExternalStoragePublicDirectory(r8)     // Catch:{ Exception -> 0x01f9 }
        L_0x0067:
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x01f9 }
            r9.<init>(r8, r5)     // Catch:{ Exception -> 0x01f9 }
            r9.mkdirs()     // Catch:{ Exception -> 0x01f9 }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x01f9 }
            r5.<init>(r9, r2)     // Catch:{ Exception -> 0x01f9 }
            boolean r8 = r5.exists()     // Catch:{ Exception -> 0x01f9 }
            if (r8 == 0) goto L_0x00d7
            r8 = 46
            int r8 = r2.lastIndexOf(r8)     // Catch:{ Exception -> 0x01f9 }
            r10 = 0
        L_0x0081:
            r11 = 10
            if (r10 >= r11) goto L_0x00d7
            r5 = -1
            java.lang.String r11 = ")"
            java.lang.String r12 = "("
            if (r8 == r5) goto L_0x00af
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f9 }
            r5.<init>()     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r13 = r2.substring(r7, r8)     // Catch:{ Exception -> 0x01f9 }
            r5.append(r13)     // Catch:{ Exception -> 0x01f9 }
            r5.append(r12)     // Catch:{ Exception -> 0x01f9 }
            int r12 = r10 + 1
            r5.append(r12)     // Catch:{ Exception -> 0x01f9 }
            r5.append(r11)     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r11 = r2.substring(r8)     // Catch:{ Exception -> 0x01f9 }
            r5.append(r11)     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x01f9 }
            goto L_0x00c6
        L_0x00af:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f9 }
            r5.<init>()     // Catch:{ Exception -> 0x01f9 }
            r5.append(r2)     // Catch:{ Exception -> 0x01f9 }
            r5.append(r12)     // Catch:{ Exception -> 0x01f9 }
            int r12 = r10 + 1
            r5.append(r12)     // Catch:{ Exception -> 0x01f9 }
            r5.append(r11)     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x01f9 }
        L_0x00c6:
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x01f9 }
            r11.<init>(r9, r5)     // Catch:{ Exception -> 0x01f9 }
            boolean r5 = r11.exists()     // Catch:{ Exception -> 0x01f9 }
            if (r5 != 0) goto L_0x00d3
            r5 = r11
            goto L_0x00d7
        L_0x00d3:
            int r10 = r10 + 1
            r5 = r11
            goto L_0x0081
        L_0x00d7:
            boolean r2 = r5.exists()     // Catch:{ Exception -> 0x01f9 }
            if (r2 != 0) goto L_0x00e0
            r5.createNewFile()     // Catch:{ Exception -> 0x01f9 }
        L_0x00e0:
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x01f9 }
            r10 = 500(0x1f4, double:2.47E-321)
            long r8 = r8 - r10
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ Exception -> 0x01b0 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x01b0 }
            java.nio.channels.FileChannel r18 = r2.getChannel()     // Catch:{ all -> 0x01a7 }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x019c }
            r0.<init>(r5)     // Catch:{ all -> 0x019c }
            java.nio.channels.FileChannel r19 = r0.getChannel()     // Catch:{ all -> 0x019c }
            long r14 = r18.size()     // Catch:{ all -> 0x0191 }
            java.lang.Class<java.io.FileDescriptor> r0 = java.io.FileDescriptor.class
            java.lang.String r12 = "getInt$"
            java.lang.Class[] r13 = new java.lang.Class[r7]     // Catch:{ all -> 0x0133 }
            java.lang.reflect.Method r0 = r0.getDeclaredMethod(r12, r13)     // Catch:{ all -> 0x0133 }
            java.io.FileDescriptor r12 = r2.getFD()     // Catch:{ all -> 0x0133 }
            java.lang.Object[] r13 = new java.lang.Object[r7]     // Catch:{ all -> 0x0133 }
            java.lang.Object r0 = r0.invoke(r12, r13)     // Catch:{ all -> 0x0133 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0133 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0133 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r0)     // Catch:{ all -> 0x0133 }
            if (r0 == 0) goto L_0x0137
            if (r3 == 0) goto L_0x0127
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda36 r0 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda36     // Catch:{ all -> 0x0133 }
            r0.<init>(r3)     // Catch:{ all -> 0x0133 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ all -> 0x0133 }
        L_0x0127:
            if (r19 == 0) goto L_0x012c
            r19.close()     // Catch:{ all -> 0x019c }
        L_0x012c:
            r18.close()     // Catch:{ all -> 0x01a7 }
            r2.close()     // Catch:{ Exception -> 0x01b0 }
            return
        L_0x0133:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0191 }
        L_0x0137:
            r12 = 0
        L_0x0139:
            int r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x0181
            boolean r0 = r24[r7]     // Catch:{ all -> 0x0191 }
            if (r0 == 0) goto L_0x0142
            goto L_0x0181
        L_0x0142:
            r22 = r5
            long r4 = r14 - r12
            r6 = 4096(0x1000, double:2.0237E-320)
            long r16 = java.lang.Math.min(r6, r4)     // Catch:{ all -> 0x017f }
            r4 = r12
            r12 = r19
            r13 = r18
            r6 = r14
            r14 = r4
            r12.transferFrom(r13, r14, r16)     // Catch:{ all -> 0x017f }
            if (r3 == 0) goto L_0x0175
            long r12 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x017f }
            long r12 = r12 - r10
            int r0 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r0 > 0) goto L_0x0175
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x017f }
            float r0 = (float) r4     // Catch:{ all -> 0x017f }
            float r12 = (float) r6     // Catch:{ all -> 0x017f }
            float r0 = r0 / r12
            r12 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 * r12
            int r0 = (int) r0     // Catch:{ all -> 0x017f }
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda37 r12 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda37     // Catch:{ all -> 0x017f }
            r12.<init>(r3, r0)     // Catch:{ all -> 0x017f }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12)     // Catch:{ all -> 0x017f }
        L_0x0175:
            r12 = 4096(0x1000, double:2.0237E-320)
            long r12 = r12 + r4
            r5 = r22
            r14 = r6
            r4 = 2
            r6 = 1
            r7 = 0
            goto L_0x0139
        L_0x017f:
            r0 = move-exception
            goto L_0x0194
        L_0x0181:
            r22 = r5
            if (r19 == 0) goto L_0x0188
            r19.close()     // Catch:{ all -> 0x019a }
        L_0x0188:
            r18.close()     // Catch:{ all -> 0x01a5 }
            r2.close()     // Catch:{ Exception -> 0x01ae }
            r2 = 0
            r6 = 1
            goto L_0x01b8
        L_0x0191:
            r0 = move-exception
            r22 = r5
        L_0x0194:
            if (r19 == 0) goto L_0x0199
            r19.close()     // Catch:{ all -> 0x0199 }
        L_0x0199:
            throw r0     // Catch:{ all -> 0x019a }
        L_0x019a:
            r0 = move-exception
            goto L_0x019f
        L_0x019c:
            r0 = move-exception
            r22 = r5
        L_0x019f:
            if (r18 == 0) goto L_0x01a4
            r18.close()     // Catch:{ all -> 0x01a4 }
        L_0x01a4:
            throw r0     // Catch:{ all -> 0x01a5 }
        L_0x01a5:
            r0 = move-exception
            goto L_0x01aa
        L_0x01a7:
            r0 = move-exception
            r22 = r5
        L_0x01aa:
            r2.close()     // Catch:{ all -> 0x01ad }
        L_0x01ad:
            throw r0     // Catch:{ Exception -> 0x01ae }
        L_0x01ae:
            r0 = move-exception
            goto L_0x01b3
        L_0x01b0:
            r0 = move-exception
            r22 = r5
        L_0x01b3:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x01f9 }
            r2 = 0
            r6 = 0
        L_0x01b8:
            boolean r0 = r24[r2]     // Catch:{ Exception -> 0x01f9 }
            if (r0 == 0) goto L_0x01c1
            r22.delete()     // Catch:{ Exception -> 0x01f9 }
            r0 = 0
            goto L_0x01c2
        L_0x01c1:
            r0 = r6
        L_0x01c2:
            if (r0 == 0) goto L_0x01f1
            r2 = 2
            if (r1 != r2) goto L_0x01ea
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r2 = "download"
            java.lang.Object r1 = r1.getSystemService(r2)     // Catch:{ Exception -> 0x01f9 }
            r4 = r1
            android.app.DownloadManager r4 = (android.app.DownloadManager) r4     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r5 = r22.getName()     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r6 = r22.getName()     // Catch:{ Exception -> 0x01f9 }
            r7 = 0
            java.lang.String r9 = r22.getAbsolutePath()     // Catch:{ Exception -> 0x01f9 }
            long r10 = r22.length()     // Catch:{ Exception -> 0x01f9 }
            r12 = 1
            r8 = r25
            r4.addCompletedDownload(r5, r6, r7, r8, r9, r10, r12)     // Catch:{ Exception -> 0x01f9 }
            goto L_0x01f1
        L_0x01ea:
            java.io.File r1 = r22.getAbsoluteFile()     // Catch:{ Exception -> 0x01f9 }
            org.telegram.messenger.AndroidUtilities.addMediaToGallery((java.io.File) r1)     // Catch:{ Exception -> 0x01f9 }
        L_0x01f1:
            if (r0 == 0) goto L_0x01fd
            if (r26 == 0) goto L_0x01fd
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r26)     // Catch:{ Exception -> 0x01f9 }
            goto L_0x01fd
        L_0x01f9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01fd:
            if (r3 == 0) goto L_0x0209
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda38 r0 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda38
            r1 = r27
            r0.<init>(r3, r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x0209:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$saveFile$38(int, java.io.File, java.lang.String, org.telegram.ui.ActionBar.AlertDialog, boolean[], java.lang.String, java.lang.Runnable, boolean[]):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveFile$35(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveFile$36(AlertDialog alertDialog, int i) {
        try {
            alertDialog.setProgress(i);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveFile$37(AlertDialog alertDialog, boolean[] zArr) {
        try {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            } else {
                zArr[0] = true;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public static boolean saveFileInternal(int i, File file, String str) {
        Uri uri;
        try {
            ContentValues contentValues = new ContentValues();
            String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
            String str2 = null;
            if (fileExtensionFromUrl != null) {
                str2 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
            }
            if ((i == 0 || i == 1) && str2 != null) {
                if (str2.startsWith("image")) {
                    i = 0;
                }
                if (str2.startsWith("video")) {
                    i = 1;
                }
            }
            if (i == 0) {
                if (str == null) {
                    str = AndroidUtilities.generateFileName(0, fileExtensionFromUrl);
                }
                uri = MediaStore.Images.Media.getContentUri("external_primary");
                File file2 = new File(Environment.DIRECTORY_PICTURES, "Telegram");
                contentValues.put("relative_path", file2 + File.separator);
                contentValues.put("_display_name", str);
                contentValues.put("mime_type", str2);
            } else if (i == 1) {
                if (str == null) {
                    str = AndroidUtilities.generateFileName(1, fileExtensionFromUrl);
                }
                File file3 = new File(Environment.DIRECTORY_MOVIES, "Telegram");
                contentValues.put("relative_path", file3 + File.separator);
                uri = MediaStore.Video.Media.getContentUri("external_primary");
                contentValues.put("_display_name", str);
            } else if (i == 2) {
                if (str == null) {
                    str = file.getName();
                }
                File file4 = new File(Environment.DIRECTORY_DOWNLOADS, "Telegram");
                contentValues.put("relative_path", file4 + File.separator);
                uri = MediaStore.Downloads.getContentUri("external_primary");
                contentValues.put("_display_name", str);
            } else {
                if (str == null) {
                    str = file.getName();
                }
                File file5 = new File(Environment.DIRECTORY_MUSIC, "Telegram");
                contentValues.put("relative_path", file5 + File.separator);
                uri = MediaStore.Audio.Media.getContentUri("external_primary");
                contentValues.put("_display_name", str);
            }
            contentValues.put("mime_type", str2);
            Uri insert = ApplicationLoader.applicationContext.getContentResolver().insert(uri, contentValues);
            if (insert != null) {
                FileInputStream fileInputStream = new FileInputStream(file);
                AndroidUtilities.copyFile((InputStream) fileInputStream, ApplicationLoader.applicationContext.getContentResolver().openOutputStream(insert));
                fileInputStream.close();
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c3 A[SYNTHETIC, Splitter:B:70:0x00c3] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getStickerExt(android.net.Uri r8) {
        /*
            java.lang.String r0 = "webp"
            r1 = 0
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0011, all -> 0x000e }
            android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ Exception -> 0x0011, all -> 0x000e }
            java.io.InputStream r2 = r2.openInputStream(r8)     // Catch:{ Exception -> 0x0011, all -> 0x000e }
            goto L_0x0012
        L_0x000e:
            r8 = move-exception
            goto L_0x00c1
        L_0x0011:
            r2 = r1
        L_0x0012:
            if (r2 != 0) goto L_0x0029
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x00b2 }
            java.lang.String r8 = r8.getPath()     // Catch:{ Exception -> 0x00b2 }
            r3.<init>(r8)     // Catch:{ Exception -> 0x00b2 }
            boolean r8 = r3.exists()     // Catch:{ Exception -> 0x00b2 }
            if (r8 == 0) goto L_0x0029
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00b2 }
            r8.<init>(r3)     // Catch:{ Exception -> 0x00b2 }
            r2 = r8
        L_0x0029:
            r8 = 12
            byte[] r3 = new byte[r8]     // Catch:{ Exception -> 0x00b2 }
            r4 = 0
            int r5 = r2.read(r3, r4, r8)     // Catch:{ Exception -> 0x00b2 }
            if (r5 != r8) goto L_0x00ab
            byte r8 = r3[r4]     // Catch:{ Exception -> 0x00b2 }
            r5 = -119(0xfffffffffffffvar_, float:NaN)
            r6 = 1
            if (r8 != r5) goto L_0x0074
            byte r8 = r3[r6]     // Catch:{ Exception -> 0x00b2 }
            r5 = 80
            if (r8 != r5) goto L_0x0074
            r8 = 2
            byte r8 = r3[r8]     // Catch:{ Exception -> 0x00b2 }
            r5 = 78
            if (r8 != r5) goto L_0x0074
            r8 = 3
            byte r8 = r3[r8]     // Catch:{ Exception -> 0x00b2 }
            r5 = 71
            if (r8 != r5) goto L_0x0074
            r8 = 4
            byte r8 = r3[r8]     // Catch:{ Exception -> 0x00b2 }
            r5 = 13
            if (r8 != r5) goto L_0x0074
            r8 = 5
            byte r8 = r3[r8]     // Catch:{ Exception -> 0x00b2 }
            r5 = 10
            if (r8 != r5) goto L_0x0074
            r8 = 6
            byte r8 = r3[r8]     // Catch:{ Exception -> 0x00b2 }
            r7 = 26
            if (r8 != r7) goto L_0x0074
            r8 = 7
            byte r8 = r3[r8]     // Catch:{ Exception -> 0x00b2 }
            if (r8 != r5) goto L_0x0074
            java.lang.String r8 = "png"
            r2.close()     // Catch:{ Exception -> 0x006f }
            goto L_0x0073
        L_0x006f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0073:
            return r8
        L_0x0074:
            byte r8 = r3[r4]     // Catch:{ Exception -> 0x00b2 }
            r4 = 31
            if (r8 != r4) goto L_0x008b
            byte r8 = r3[r6]     // Catch:{ Exception -> 0x00b2 }
            r4 = -117(0xffffffffffffff8b, float:NaN)
            if (r8 != r4) goto L_0x008b
            java.lang.String r8 = "tgs"
            r2.close()     // Catch:{ Exception -> 0x0086 }
            goto L_0x008a
        L_0x0086:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x008a:
            return r8
        L_0x008b:
            java.lang.String r8 = new java.lang.String     // Catch:{ Exception -> 0x00b2 }
            r8.<init>(r3)     // Catch:{ Exception -> 0x00b2 }
            java.lang.String r8 = r8.toLowerCase()     // Catch:{ Exception -> 0x00b2 }
            java.lang.String r3 = "riff"
            boolean r3 = r8.startsWith(r3)     // Catch:{ Exception -> 0x00b2 }
            if (r3 == 0) goto L_0x00ab
            boolean r8 = r8.endsWith(r0)     // Catch:{ Exception -> 0x00b2 }
            if (r8 == 0) goto L_0x00ab
            r2.close()     // Catch:{ Exception -> 0x00a6 }
            goto L_0x00aa
        L_0x00a6:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x00aa:
            return r0
        L_0x00ab:
            r2.close()     // Catch:{ Exception -> 0x00bc }
            goto L_0x00c0
        L_0x00af:
            r8 = move-exception
            r1 = r2
            goto L_0x00c1
        L_0x00b2:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ all -> 0x00af }
            if (r2 == 0) goto L_0x00c0
            r2.close()     // Catch:{ Exception -> 0x00bc }
            goto L_0x00c0
        L_0x00bc:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x00c0:
            return r1
        L_0x00c1:
            if (r1 == 0) goto L_0x00cb
            r1.close()     // Catch:{ Exception -> 0x00c7 }
            goto L_0x00cb
        L_0x00c7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00cb:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.getStickerExt(android.net.Uri):java.lang.String");
    }

    public static boolean isWebp(Uri uri) {
        InputStream inputStream = null;
        try {
            InputStream openInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] bArr = new byte[12];
            if (openInputStream.read(bArr, 0, 12) == 12) {
                String lowerCase = new String(bArr).toLowerCase();
                if (lowerCase.startsWith("riff") && lowerCase.endsWith("webp")) {
                    try {
                        openInputStream.close();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    return true;
                }
            }
            try {
                openInputStream.close();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            throw th;
        }
        return false;
    }

    public static boolean isGif(Uri uri) {
        InputStream inputStream = null;
        try {
            InputStream openInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] bArr = new byte[3];
            if (openInputStream.read(bArr, 0, 3) != 3 || !new String(bArr).equalsIgnoreCase("gif")) {
                try {
                    openInputStream.close();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                return false;
            }
            try {
                openInputStream.close();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            return true;
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0041 */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:21:0x0041=Splitter:B:21:0x0041, B:15:0x0037=Splitter:B:15:0x0037} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getFileName(android.net.Uri r11) {
        /*
            java.lang.String r0 = "_display_name"
            java.lang.String r1 = ""
            if (r11 != 0) goto L_0x0007
            return r1
        L_0x0007:
            r2 = 0
            java.lang.String r3 = r11.getScheme()     // Catch:{ Exception -> 0x005b }
            java.lang.String r4 = "content"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x005b }
            r4 = 1
            if (r3 == 0) goto L_0x0046
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0042 }
            android.content.ContentResolver r5 = r3.getContentResolver()     // Catch:{ Exception -> 0x0042 }
            java.lang.String[] r7 = new java.lang.String[r4]     // Catch:{ Exception -> 0x0042 }
            r3 = 0
            r7[r3] = r0     // Catch:{ Exception -> 0x0042 }
            r8 = 0
            r9 = 0
            r10 = 0
            r6 = r11
            android.database.Cursor r3 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0042 }
            boolean r5 = r3.moveToFirst()     // Catch:{ all -> 0x003b }
            if (r5 == 0) goto L_0x0037
            int r0 = r3.getColumnIndex(r0)     // Catch:{ all -> 0x003b }
            java.lang.String r0 = r3.getString(r0)     // Catch:{ all -> 0x003b }
            r2 = r0
        L_0x0037:
            r3.close()     // Catch:{ Exception -> 0x0042 }
            goto L_0x0046
        L_0x003b:
            r0 = move-exception
            if (r3 == 0) goto L_0x0041
            r3.close()     // Catch:{ all -> 0x0041 }
        L_0x0041:
            throw r0     // Catch:{ Exception -> 0x0042 }
        L_0x0042:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x005b }
        L_0x0046:
            if (r2 != 0) goto L_0x005a
            java.lang.String r2 = r11.getPath()     // Catch:{ Exception -> 0x005b }
            r11 = 47
            int r11 = r2.lastIndexOf(r11)     // Catch:{ Exception -> 0x005b }
            r0 = -1
            if (r11 == r0) goto L_0x005a
            int r11 = r11 + r4
            java.lang.String r2 = r2.substring(r11)     // Catch:{ Exception -> 0x005b }
        L_0x005a:
            return r2
        L_0x005b:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.getFileName(android.net.Uri):java.lang.String");
    }

    public static String copyFileToCache(Uri uri, String str) {
        return copyFileToCache(uri, str, -1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:69:?, code lost:
        r0 = r5.getAbsolutePath();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:?, code lost:
        r11.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x00da, code lost:
        r11 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x00db, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r11);
     */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0132 A[SYNTHETIC, Splitter:B:112:0x0132] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x013c A[SYNTHETIC, Splitter:B:117:0x013c] */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0157 A[SYNTHETIC, Splitter:B:131:0x0157] */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0161 A[SYNTHETIC, Splitter:B:136:0x0161] */
    @android.annotation.SuppressLint({"DiscouragedPrivateApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String copyFileToCache(android.net.Uri r11, java.lang.String r12, long r13) {
        /*
            r0 = 0
            r1 = 0
            r3 = 0
            java.lang.String r4 = getFileName(r11)     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            java.lang.String r4 = org.telegram.messenger.FileLoader.fixFileName(r4)     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            if (r4 != 0) goto L_0x0029
            int r4 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            java.util.Locale r5 = java.util.Locale.US     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            java.lang.String r6 = "%d.%s"
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            r7[r0] = r4     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            r4 = 1
            r7[r4] = r12     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            java.lang.String r4 = java.lang.String.format(r5, r6, r7)     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
        L_0x0029:
            java.io.File r12 = org.telegram.messenger.AndroidUtilities.getSharingDirectory()     // Catch:{ Exception -> 0x0128, all -> 0x0124 }
            r12.mkdirs()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r5.<init>(r12, r4)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            android.net.Uri r12 = android.net.Uri.fromFile(r5)     // Catch:{ Exception -> 0x0119, all -> 0x0117 }
            boolean r12 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r12)     // Catch:{ Exception -> 0x0119, all -> 0x0117 }
            if (r12 == 0) goto L_0x004c
            int r11 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r11 <= 0) goto L_0x004b
            long r11 = (long) r0
            int r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x004b
            r5.delete()
        L_0x004b:
            return r3
        L_0x004c:
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0119, all -> 0x0117 }
            android.content.ContentResolver r12 = r12.getContentResolver()     // Catch:{ Exception -> 0x0119, all -> 0x0117 }
            java.io.InputStream r11 = r12.openInputStream(r11)     // Catch:{ Exception -> 0x0119, all -> 0x0117 }
            boolean r12 = r11 instanceof java.io.FileInputStream     // Catch:{ Exception -> 0x0110, all -> 0x010a }
            if (r12 == 0) goto L_0x0098
            r12 = r11
            java.io.FileInputStream r12 = (java.io.FileInputStream) r12     // Catch:{ Exception -> 0x0110, all -> 0x010a }
            java.lang.Class<java.io.FileDescriptor> r4 = java.io.FileDescriptor.class
            java.lang.String r6 = "getInt$"
            java.lang.Class[] r7 = new java.lang.Class[r0]     // Catch:{ all -> 0x0094 }
            java.lang.reflect.Method r4 = r4.getDeclaredMethod(r6, r7)     // Catch:{ all -> 0x0094 }
            java.io.FileDescriptor r12 = r12.getFD()     // Catch:{ all -> 0x0094 }
            java.lang.Object[] r6 = new java.lang.Object[r0]     // Catch:{ all -> 0x0094 }
            java.lang.Object r12 = r4.invoke(r12, r6)     // Catch:{ all -> 0x0094 }
            java.lang.Integer r12 = (java.lang.Integer) r12     // Catch:{ all -> 0x0094 }
            int r12 = r12.intValue()     // Catch:{ all -> 0x0094 }
            boolean r12 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r12)     // Catch:{ all -> 0x0094 }
            if (r12 == 0) goto L_0x0098
            if (r11 == 0) goto L_0x0087
            r11.close()     // Catch:{ Exception -> 0x0083 }
            goto L_0x0087
        L_0x0083:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x0087:
            int r11 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r11 <= 0) goto L_0x0093
            long r11 = (long) r0
            int r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0093
            r5.delete()
        L_0x0093:
            return r3
        L_0x0094:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)     // Catch:{ Exception -> 0x0110, all -> 0x010a }
        L_0x0098:
            java.io.FileOutputStream r12 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0110, all -> 0x010a }
            r12.<init>(r5)     // Catch:{ Exception -> 0x0110, all -> 0x010a }
            r4 = 20480(0x5000, float:2.8699E-41)
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0104, all -> 0x00fe }
            r6 = 0
        L_0x00a2:
            int r7 = r11.read(r4)     // Catch:{ Exception -> 0x00f8, all -> 0x00f3 }
            r8 = -1
            if (r7 == r8) goto L_0x00d2
            r12.write(r4, r0, r7)     // Catch:{ Exception -> 0x00f8, all -> 0x00f3 }
            int r6 = r6 + r7
            int r7 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r7 <= 0) goto L_0x00a2
            long r7 = (long) r6
            int r9 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r9 <= 0) goto L_0x00a2
            r11.close()     // Catch:{ Exception -> 0x00ba }
            goto L_0x00be
        L_0x00ba:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x00be:
            r12.close()     // Catch:{ Exception -> 0x00c2 }
            goto L_0x00c6
        L_0x00c2:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x00c6:
            int r11 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r11 <= 0) goto L_0x00d1
            int r11 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r11 <= 0) goto L_0x00d1
            r5.delete()
        L_0x00d1:
            return r3
        L_0x00d2:
            java.lang.String r0 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x00f8, all -> 0x00f3 }
            r11.close()     // Catch:{ Exception -> 0x00da }
            goto L_0x00de
        L_0x00da:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x00de:
            r12.close()     // Catch:{ Exception -> 0x00e2 }
            goto L_0x00e6
        L_0x00e2:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x00e6:
            int r11 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r11 <= 0) goto L_0x00f2
            long r11 = (long) r6
            int r1 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r1 <= 0) goto L_0x00f2
            r5.delete()
        L_0x00f2:
            return r0
        L_0x00f3:
            r0 = move-exception
            r3 = r11
            r11 = r0
            goto L_0x0154
        L_0x00f8:
            r0 = move-exception
            r10 = r12
            r12 = r11
            r11 = r0
            r0 = r10
            goto L_0x012d
        L_0x00fe:
            r3 = move-exception
            r10 = r3
            r3 = r11
            r11 = r10
            goto L_0x0155
        L_0x0104:
            r4 = move-exception
            r0 = r12
            r6 = 0
            r12 = r11
            r11 = r4
            goto L_0x012d
        L_0x010a:
            r12 = move-exception
            r10 = r3
            r3 = r11
            r11 = r12
            r12 = r10
            goto L_0x0155
        L_0x0110:
            r12 = move-exception
            r0 = r3
            r6 = 0
            r10 = r12
            r12 = r11
            r11 = r10
            goto L_0x012d
        L_0x0117:
            r11 = move-exception
            goto L_0x011d
        L_0x0119:
            r11 = move-exception
            goto L_0x0121
        L_0x011b:
            r11 = move-exception
            r5 = r12
        L_0x011d:
            r12 = r3
            goto L_0x0155
        L_0x011f:
            r11 = move-exception
            r5 = r12
        L_0x0121:
            r12 = r3
            r0 = r12
            goto L_0x012c
        L_0x0124:
            r11 = move-exception
            r12 = r3
            r5 = r12
            goto L_0x0155
        L_0x0128:
            r11 = move-exception
            r12 = r3
            r0 = r12
            r5 = r0
        L_0x012c:
            r6 = 0
        L_0x012d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)     // Catch:{ all -> 0x0151 }
            if (r12 == 0) goto L_0x013a
            r12.close()     // Catch:{ Exception -> 0x0136 }
            goto L_0x013a
        L_0x0136:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x013a:
            if (r0 == 0) goto L_0x0144
            r0.close()     // Catch:{ Exception -> 0x0140 }
            goto L_0x0144
        L_0x0140:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x0144:
            int r11 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r11 <= 0) goto L_0x0150
            long r11 = (long) r6
            int r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0150
            r5.delete()
        L_0x0150:
            return r3
        L_0x0151:
            r11 = move-exception
            r3 = r12
            r12 = r0
        L_0x0154:
            r0 = r6
        L_0x0155:
            if (r3 == 0) goto L_0x015f
            r3.close()     // Catch:{ Exception -> 0x015b }
            goto L_0x015f
        L_0x015b:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x015f:
            if (r12 == 0) goto L_0x0169
            r12.close()     // Catch:{ Exception -> 0x0165 }
            goto L_0x0169
        L_0x0165:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
        L_0x0169:
            int r12 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r12 <= 0) goto L_0x0175
            long r0 = (long) r0
            int r12 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r12 <= 0) goto L_0x0175
            r5.delete()
        L_0x0175:
            goto L_0x0177
        L_0x0176:
            throw r11
        L_0x0177:
            goto L_0x0176
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.copyFileToCache(android.net.Uri, java.lang.String, long):java.lang.String");
    }

    public static void loadGalleryPhotosAlbums(int i) {
        Thread thread = new Thread(new MediaController$$ExternalSyntheticLambda3(i));
        thread.setPriority(1);
        thread.start();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v14, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v16, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: org.telegram.messenger.MediaController$AlbumEntry} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v18, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v20, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v23, resolved type: org.telegram.messenger.MediaController$AlbumEntry} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0261  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0277 A[SYNTHETIC, Splitter:B:124:0x0277] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02b0 A[SYNTHETIC, Splitter:B:137:0x02b0] */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x02c1 A[Catch:{ all -> 0x042e }] */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x02e4 A[Catch:{ all -> 0x042e }] */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02e7 A[Catch:{ all -> 0x042e }] */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x02fb A[Catch:{ all -> 0x042e }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0061 A[Catch:{ all -> 0x0290 }] */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x0427 A[SYNTHETIC, Splitter:B:209:0x0427] */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0435 A[SYNTHETIC, Splitter:B:218:0x0435] */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x044b A[LOOP:2: B:224:0x0445->B:226:0x044b, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00b4 A[SYNTHETIC, Splitter:B:28:0x00b4] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$loadGalleryPhotosAlbums$40(int r51) {
        /*
            java.lang.String r1 = "AllMedia"
            java.lang.String r2 = "_size"
            java.lang.String r3 = "height"
            java.lang.String r4 = "width"
            java.lang.String r5 = "_data"
            java.lang.String r6 = "bucket_display_name"
            java.lang.String r7 = "bucket_id"
            java.lang.String r8 = "_id"
            java.lang.String r9 = " DESC"
            java.lang.String r10 = "android.permission.READ_EXTERNAL_STORAGE"
            java.lang.String r11 = "date_modified"
            java.lang.String r12 = "datetaken"
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            android.util.SparseArray r13 = new android.util.SparseArray
            r13.<init>()
            r16 = r11
            android.util.SparseArray r11 = new android.util.SparseArray
            r11.<init>()
            r17 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0051 }
            r0.<init>()     // Catch:{ Exception -> 0x0051 }
            java.lang.String r18 = android.os.Environment.DIRECTORY_DCIM     // Catch:{ Exception -> 0x0051 }
            java.io.File r18 = android.os.Environment.getExternalStoragePublicDirectory(r18)     // Catch:{ Exception -> 0x0051 }
            r19 = r12
            java.lang.String r12 = r18.getAbsolutePath()     // Catch:{ Exception -> 0x004f }
            r0.append(r12)     // Catch:{ Exception -> 0x004f }
            java.lang.String r12 = "/Camera/"
            r0.append(r12)     // Catch:{ Exception -> 0x004f }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x004f }
            r12 = r0
            goto L_0x0059
        L_0x004f:
            r0 = move-exception
            goto L_0x0054
        L_0x0051:
            r0 = move-exception
            r19 = r12
        L_0x0054:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r12 = r17
        L_0x0059:
            r18 = r11
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0290 }
            r11 = 23
            if (r0 < r11) goto L_0x0086
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0290 }
            int r11 = r11.checkSelfPermission(r10)     // Catch:{ all -> 0x0290 }
            if (r11 != 0) goto L_0x006a
            goto L_0x0086
        L_0x006a:
            r35 = r2
            r30 = r3
            r28 = r4
            r26 = r5
            r25 = r6
            r24 = r7
            r23 = r8
            r27 = r9
            r29 = r10
            r10 = r17
            r31 = r10
        L_0x0080:
            r32 = r31
            r33 = r32
            goto L_0x0275
        L_0x0086:
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0290 }
            android.content.ContentResolver r23 = r11.getContentResolver()     // Catch:{ all -> 0x0290 }
            android.net.Uri r24 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x0290 }
            java.lang.String[] r25 = projectionPhotos     // Catch:{ all -> 0x0290 }
            r26 = 0
            r27 = 0
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0290 }
            r11.<init>()     // Catch:{ all -> 0x0290 }
            r29 = r10
            r10 = 28
            if (r0 <= r10) goto L_0x00a2
            r10 = r16
            goto L_0x00a4
        L_0x00a2:
            r10 = r19
        L_0x00a4:
            r11.append(r10)     // Catch:{ all -> 0x027e }
            r11.append(r9)     // Catch:{ all -> 0x027e }
            java.lang.String r28 = r11.toString()     // Catch:{ all -> 0x027e }
            android.database.Cursor r10 = android.provider.MediaStore.Images.Media.query(r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x027e }
            if (r10 == 0) goto L_0x0261
            int r11 = r10.getColumnIndex(r8)     // Catch:{ all -> 0x024d }
            r23 = r8
            int r8 = r10.getColumnIndex(r7)     // Catch:{ all -> 0x023f }
            r24 = r7
            int r7 = r10.getColumnIndex(r6)     // Catch:{ all -> 0x0233 }
            r25 = r6
            int r6 = r10.getColumnIndex(r5)     // Catch:{ all -> 0x0229 }
            r26 = r5
            r5 = 28
            if (r0 <= r5) goto L_0x00d3
            r0 = r16
            goto L_0x00d5
        L_0x00d3:
            r0 = r19
        L_0x00d5:
            int r0 = r10.getColumnIndex(r0)     // Catch:{ all -> 0x0221 }
            java.lang.String r5 = "orientation"
            int r5 = r10.getColumnIndex(r5)     // Catch:{ all -> 0x0221 }
            r27 = r9
            int r9 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x0219 }
            r28 = r4
            int r4 = r10.getColumnIndex(r3)     // Catch:{ all -> 0x0213 }
            r30 = r3
            int r3 = r10.getColumnIndex(r2)     // Catch:{ all -> 0x020f }
            r31 = r17
            r32 = r31
            r33 = r32
            r34 = r33
        L_0x00f9:
            boolean r35 = r10.moveToNext()     // Catch:{ all -> 0x020a }
            if (r35 == 0) goto L_0x0206
            r35 = r2
            java.lang.String r2 = r10.getString(r6)     // Catch:{ all -> 0x0203 }
            boolean r36 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0203 }
            if (r36 == 0) goto L_0x010e
            r2 = r35
            goto L_0x00f9
        L_0x010e:
            int r38 = r10.getInt(r11)     // Catch:{ all -> 0x0203 }
            r48 = r6
            int r6 = r10.getInt(r8)     // Catch:{ all -> 0x0203 }
            r49 = r8
            java.lang.String r8 = r10.getString(r7)     // Catch:{ all -> 0x0203 }
            long r39 = r10.getLong(r0)     // Catch:{ all -> 0x0203 }
            int r42 = r10.getInt(r5)     // Catch:{ all -> 0x0203 }
            int r44 = r10.getInt(r9)     // Catch:{ all -> 0x0203 }
            int r45 = r10.getInt(r4)     // Catch:{ all -> 0x0203 }
            long r46 = r10.getLong(r3)     // Catch:{ all -> 0x0203 }
            r50 = r0
            org.telegram.messenger.MediaController$PhotoEntry r0 = new org.telegram.messenger.MediaController$PhotoEntry     // Catch:{ all -> 0x0203 }
            r43 = 0
            r36 = r0
            r37 = r6
            r41 = r2
            r36.<init>(r37, r38, r39, r41, r42, r43, r44, r45, r46)     // Catch:{ all -> 0x0203 }
            if (r31 != 0) goto L_0x015c
            r36 = r3
            org.telegram.messenger.MediaController$AlbumEntry r3 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0203 }
            r37 = r4
            java.lang.String r4 = "AllPhotos"
            r38 = r5
            r5 = 2131624257(0x7f0e0141, float:1.8875689E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)     // Catch:{ all -> 0x0203 }
            r5 = 0
            r3.<init>(r5, r4, r0)     // Catch:{ all -> 0x0203 }
            r15.add(r5, r3)     // Catch:{ all -> 0x0179 }
            goto L_0x0164
        L_0x015c:
            r36 = r3
            r37 = r4
            r38 = r5
            r3 = r31
        L_0x0164:
            if (r32 != 0) goto L_0x017e
            org.telegram.messenger.MediaController$AlbumEntry r4 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0179 }
            r39 = r7
            r5 = 2131624256(0x7f0e0140, float:1.8875687E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x0179 }
            r5 = 0
            r4.<init>(r5, r7, r0)     // Catch:{ all -> 0x0179 }
            r14.add(r5, r4)     // Catch:{ all -> 0x01fc }
            goto L_0x0182
        L_0x0179:
            r0 = move-exception
            r31 = r3
            goto L_0x02ab
        L_0x017e:
            r39 = r7
            r4 = r32
        L_0x0182:
            r3.addPhoto(r0)     // Catch:{ all -> 0x01fc }
            r4.addPhoto(r0)     // Catch:{ all -> 0x01fc }
            java.lang.Object r5 = r13.get(r6)     // Catch:{ all -> 0x01fc }
            org.telegram.messenger.MediaController$AlbumEntry r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5     // Catch:{ all -> 0x01fc }
            if (r5 != 0) goto L_0x01b2
            org.telegram.messenger.MediaController$AlbumEntry r5 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x01fc }
            r5.<init>(r6, r8, r0)     // Catch:{ all -> 0x01fc }
            r13.put(r6, r5)     // Catch:{ all -> 0x01fc }
            if (r33 != 0) goto L_0x01af
            if (r12 == 0) goto L_0x01af
            if (r2 == 0) goto L_0x01af
            boolean r7 = r2.startsWith(r12)     // Catch:{ all -> 0x01fc }
            if (r7 == 0) goto L_0x01af
            r7 = 0
            r14.add(r7, r5)     // Catch:{ all -> 0x01fc }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x01fc }
            r33 = r7
            goto L_0x01b2
        L_0x01af:
            r14.add(r5)     // Catch:{ all -> 0x01fc }
        L_0x01b2:
            r5.addPhoto(r0)     // Catch:{ all -> 0x01fc }
            r5 = r18
            java.lang.Object r7 = r5.get(r6)     // Catch:{ all -> 0x01fc }
            org.telegram.messenger.MediaController$AlbumEntry r7 = (org.telegram.messenger.MediaController.AlbumEntry) r7     // Catch:{ all -> 0x01fc }
            if (r7 != 0) goto L_0x01e1
            org.telegram.messenger.MediaController$AlbumEntry r7 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x01fc }
            r7.<init>(r6, r8, r0)     // Catch:{ all -> 0x01fc }
            r5.put(r6, r7)     // Catch:{ all -> 0x01fc }
            if (r34 != 0) goto L_0x01de
            if (r12 == 0) goto L_0x01de
            if (r2 == 0) goto L_0x01de
            boolean r2 = r2.startsWith(r12)     // Catch:{ all -> 0x01fc }
            if (r2 == 0) goto L_0x01de
            r2 = 0
            r15.add(r2, r7)     // Catch:{ all -> 0x01fc }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x01fc }
            r34 = r2
            goto L_0x01e1
        L_0x01de:
            r15.add(r7)     // Catch:{ all -> 0x01fc }
        L_0x01e1:
            r7.addPhoto(r0)     // Catch:{ all -> 0x01fc }
            r31 = r3
            r32 = r4
            r18 = r5
            r2 = r35
            r3 = r36
            r4 = r37
            r5 = r38
            r7 = r39
            r6 = r48
            r8 = r49
            r0 = r50
            goto L_0x00f9
        L_0x01fc:
            r0 = move-exception
            r31 = r3
            r32 = r4
            goto L_0x02ab
        L_0x0203:
            r0 = move-exception
            goto L_0x02ab
        L_0x0206:
            r35 = r2
            goto L_0x0275
        L_0x020a:
            r0 = move-exception
            r35 = r2
            goto L_0x02ab
        L_0x020f:
            r0 = move-exception
            r35 = r2
            goto L_0x025e
        L_0x0213:
            r0 = move-exception
            r35 = r2
            r30 = r3
            goto L_0x025e
        L_0x0219:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            goto L_0x025e
        L_0x0221:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            goto L_0x025c
        L_0x0229:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r26 = r5
            goto L_0x025c
        L_0x0233:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r26 = r5
            r25 = r6
            goto L_0x025c
        L_0x023f:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r26 = r5
            r25 = r6
            r24 = r7
            goto L_0x025c
        L_0x024d:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r26 = r5
            r25 = r6
            r24 = r7
            r23 = r8
        L_0x025c:
            r27 = r9
        L_0x025e:
            r31 = r17
            goto L_0x02a7
        L_0x0261:
            r35 = r2
            r30 = r3
            r28 = r4
            r26 = r5
            r25 = r6
            r24 = r7
            r23 = r8
            r27 = r9
            r31 = r17
            goto L_0x0080
        L_0x0275:
            if (r10 == 0) goto L_0x02b9
            r10.close()     // Catch:{ Exception -> 0x027b }
            goto L_0x02b9
        L_0x027b:
            r0 = move-exception
            r2 = r0
            goto L_0x02b6
        L_0x027e:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r26 = r5
            r25 = r6
            r24 = r7
            r23 = r8
            r27 = r9
            goto L_0x02a3
        L_0x0290:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r26 = r5
            r25 = r6
            r24 = r7
            r23 = r8
            r27 = r9
            r29 = r10
        L_0x02a3:
            r10 = r17
            r31 = r10
        L_0x02a7:
            r32 = r31
            r33 = r32
        L_0x02ab:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0471 }
            if (r10 == 0) goto L_0x02b9
            r10.close()     // Catch:{ Exception -> 0x02b4 }
            goto L_0x02b9
        L_0x02b4:
            r0 = move-exception
            r2 = r0
        L_0x02b6:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x02b9:
            r18 = r31
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x042e }
            r2 = 23
            if (r0 < r2) goto L_0x02cf
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x042e }
            r3 = r29
            int r2 = r2.checkSelfPermission(r3)     // Catch:{ all -> 0x042e }
            if (r2 != 0) goto L_0x02cc
            goto L_0x02cf
        L_0x02cc:
            r3 = 0
            goto L_0x0425
        L_0x02cf:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x042e }
            android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ all -> 0x042e }
            android.net.Uri r4 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x042e }
            java.lang.String[] r5 = projectionVideo     // Catch:{ all -> 0x042e }
            r6 = 0
            r7 = 0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x042e }
            r2.<init>()     // Catch:{ all -> 0x042e }
            r8 = 28
            if (r0 <= r8) goto L_0x02e7
            r8 = r16
            goto L_0x02e9
        L_0x02e7:
            r8 = r19
        L_0x02e9:
            r2.append(r8)     // Catch:{ all -> 0x042e }
            r8 = r27
            r2.append(r8)     // Catch:{ all -> 0x042e }
            java.lang.String r8 = r2.toString()     // Catch:{ all -> 0x042e }
            android.database.Cursor r10 = android.provider.MediaStore.Images.Media.query(r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x042e }
            if (r10 == 0) goto L_0x02cc
            r2 = r23
            int r2 = r10.getColumnIndex(r2)     // Catch:{ all -> 0x042e }
            r3 = r24
            int r3 = r10.getColumnIndex(r3)     // Catch:{ all -> 0x042e }
            r4 = r25
            int r4 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x042e }
            r5 = r26
            int r5 = r10.getColumnIndex(r5)     // Catch:{ all -> 0x042e }
            r6 = 28
            if (r0 <= r6) goto L_0x031a
            r11 = r16
            goto L_0x031c
        L_0x031a:
            r11 = r19
        L_0x031c:
            int r0 = r10.getColumnIndex(r11)     // Catch:{ all -> 0x042e }
            java.lang.String r6 = "duration"
            int r6 = r10.getColumnIndex(r6)     // Catch:{ all -> 0x042e }
            r7 = r28
            int r7 = r10.getColumnIndex(r7)     // Catch:{ all -> 0x042e }
            r8 = r30
            int r8 = r10.getColumnIndex(r8)     // Catch:{ all -> 0x042e }
            r9 = r35
            int r9 = r10.getColumnIndex(r9)     // Catch:{ all -> 0x042e }
        L_0x0338:
            boolean r11 = r10.moveToNext()     // Catch:{ all -> 0x042e }
            if (r11 == 0) goto L_0x02cc
            java.lang.String r11 = r10.getString(r5)     // Catch:{ all -> 0x042e }
            boolean r16 = android.text.TextUtils.isEmpty(r11)     // Catch:{ all -> 0x042e }
            if (r16 == 0) goto L_0x0349
            goto L_0x0338
        L_0x0349:
            int r36 = r10.getInt(r2)     // Catch:{ all -> 0x042e }
            r16 = r2
            int r2 = r10.getInt(r3)     // Catch:{ all -> 0x042e }
            r19 = r3
            java.lang.String r3 = r10.getString(r4)     // Catch:{ all -> 0x042e }
            long r37 = r10.getLong(r0)     // Catch:{ all -> 0x042e }
            long r21 = r10.getLong(r6)     // Catch:{ all -> 0x042e }
            int r42 = r10.getInt(r7)     // Catch:{ all -> 0x042e }
            int r43 = r10.getInt(r8)     // Catch:{ all -> 0x042e }
            long r44 = r10.getLong(r9)     // Catch:{ all -> 0x042e }
            r23 = r0
            org.telegram.messenger.MediaController$PhotoEntry r0 = new org.telegram.messenger.MediaController$PhotoEntry     // Catch:{ all -> 0x042e }
            r24 = 1000(0x3e8, double:4.94E-321)
            r26 = r4
            r27 = r5
            long r4 = r21 / r24
            int r5 = (int) r4     // Catch:{ all -> 0x042e }
            r41 = 1
            r34 = r0
            r35 = r2
            r39 = r11
            r40 = r5
            r34.<init>(r35, r36, r37, r39, r40, r41, r42, r43, r44)     // Catch:{ all -> 0x042e }
            if (r17 != 0) goto L_0x03a9
            org.telegram.messenger.MediaController$AlbumEntry r4 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x042e }
            java.lang.String r5 = "AllVideos"
            r21 = r6
            r6 = 2131624258(0x7f0e0142, float:1.887569E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x042e }
            r6 = 0
            r4.<init>(r6, r5, r0)     // Catch:{ all -> 0x042e }
            r5 = 1
            r4.videoOnly = r5     // Catch:{ all -> 0x03c9 }
            if (r32 == 0) goto L_0x03a0
            goto L_0x03a1
        L_0x03a0:
            r5 = 0
        L_0x03a1:
            if (r18 == 0) goto L_0x03a5
            int r5 = r5 + 1
        L_0x03a5:
            r14.add(r5, r4)     // Catch:{ all -> 0x03c9 }
            goto L_0x03ad
        L_0x03a9:
            r21 = r6
            r4 = r17
        L_0x03ad:
            if (r32 != 0) goto L_0x03ce
            org.telegram.messenger.MediaController$AlbumEntry r5 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x03c9 }
            r20 = r7
            r6 = 2131624256(0x7f0e0140, float:1.8875687E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x03c9 }
            r6 = 0
            r5.<init>(r6, r7, r0)     // Catch:{ all -> 0x03c9 }
            r14.add(r6, r5)     // Catch:{ all -> 0x03c2 }
            goto L_0x03d2
        L_0x03c2:
            r0 = move-exception
            r17 = r4
            r32 = r5
            goto L_0x042f
        L_0x03c9:
            r0 = move-exception
            r17 = r4
            goto L_0x042f
        L_0x03ce:
            r20 = r7
            r5 = r32
        L_0x03d2:
            r4.addPhoto(r0)     // Catch:{ all -> 0x041e }
            r5.addPhoto(r0)     // Catch:{ all -> 0x041e }
            java.lang.Object r6 = r13.get(r2)     // Catch:{ all -> 0x041e }
            org.telegram.messenger.MediaController$AlbumEntry r6 = (org.telegram.messenger.MediaController.AlbumEntry) r6     // Catch:{ all -> 0x041e }
            if (r6 != 0) goto L_0x0404
            org.telegram.messenger.MediaController$AlbumEntry r6 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x041e }
            r6.<init>(r2, r3, r0)     // Catch:{ all -> 0x041e }
            r13.put(r2, r6)     // Catch:{ all -> 0x041e }
            if (r33 != 0) goto L_0x03ff
            if (r12 == 0) goto L_0x03ff
            if (r11 == 0) goto L_0x03ff
            boolean r3 = r11.startsWith(r12)     // Catch:{ all -> 0x041e }
            if (r3 == 0) goto L_0x03ff
            r3 = 0
            r14.add(r3, r6)     // Catch:{ all -> 0x041c }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x041c }
            r33 = r2
            goto L_0x0405
        L_0x03ff:
            r3 = 0
            r14.add(r6)     // Catch:{ all -> 0x041c }
            goto L_0x0405
        L_0x0404:
            r3 = 0
        L_0x0405:
            r6.addPhoto(r0)     // Catch:{ all -> 0x041c }
            r17 = r4
            r32 = r5
            r2 = r16
            r3 = r19
            r7 = r20
            r6 = r21
            r0 = r23
            r4 = r26
            r5 = r27
            goto L_0x0338
        L_0x041c:
            r0 = move-exception
            goto L_0x0420
        L_0x041e:
            r0 = move-exception
            r3 = 0
        L_0x0420:
            r17 = r4
            r32 = r5
            goto L_0x0430
        L_0x0425:
            if (r10 == 0) goto L_0x043e
            r10.close()     // Catch:{ Exception -> 0x042b }
            goto L_0x043e
        L_0x042b:
            r0 = move-exception
            r1 = r0
            goto L_0x043b
        L_0x042e:
            r0 = move-exception
        L_0x042f:
            r3 = 0
        L_0x0430:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0463 }
            if (r10 == 0) goto L_0x043e
            r10.close()     // Catch:{ Exception -> 0x0439 }
            goto L_0x043e
        L_0x0439:
            r0 = move-exception
            r1 = r0
        L_0x043b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x043e:
            r19 = r17
            r17 = r32
            r16 = r33
            r11 = 0
        L_0x0445:
            int r0 = r14.size()
            if (r11 >= r0) goto L_0x045b
            java.lang.Object r0 = r14.get(r11)
            org.telegram.messenger.MediaController$AlbumEntry r0 = (org.telegram.messenger.MediaController.AlbumEntry) r0
            java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r0 = r0.photos
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda40 r1 = org.telegram.messenger.MediaController$$ExternalSyntheticLambda40.INSTANCE
            java.util.Collections.sort(r0, r1)
            int r11 = r11 + 1
            goto L_0x0445
        L_0x045b:
            r20 = 0
            r13 = r51
            broadcastNewPhotos(r13, r14, r15, r16, r17, r18, r19, r20)
            return
        L_0x0463:
            r0 = move-exception
            r1 = r0
            if (r10 == 0) goto L_0x0470
            r10.close()     // Catch:{ Exception -> 0x046b }
            goto L_0x0470
        L_0x046b:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0470:
            throw r1
        L_0x0471:
            r0 = move-exception
            r1 = r0
            if (r10 == 0) goto L_0x047e
            r10.close()     // Catch:{ Exception -> 0x0479 }
            goto L_0x047e
        L_0x0479:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x047e:
            goto L_0x0480
        L_0x047f:
            throw r1
        L_0x0480:
            goto L_0x047f
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$loadGalleryPhotosAlbums$40(int):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$loadGalleryPhotosAlbums$39(PhotoEntry photoEntry, PhotoEntry photoEntry2) {
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
        MediaController$$ExternalSyntheticLambda6 mediaController$$ExternalSyntheticLambda6 = new MediaController$$ExternalSyntheticLambda6(i, arrayList, arrayList2, num, albumEntry, albumEntry2, albumEntry3);
        broadcastPhotosRunnable = mediaController$$ExternalSyntheticLambda6;
        AndroidUtilities.runOnUIThread(mediaController$$ExternalSyntheticLambda6, (long) i2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$broadcastNewPhotos$41(int i, ArrayList arrayList, ArrayList arrayList2, Integer num, AlbumEntry albumEntry, AlbumEntry albumEntry2, AlbumEntry albumEntry3) {
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
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.albumsDidLoad, Integer.valueOf(i), arrayList, arrayList2, num);
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
                VideoConvertMessage videoConvertMessage = this.videoConvertQueue.get(i);
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
        VideoConvertMessage videoConvertMessage = this.videoConvertQueue.get(0);
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
            while (true) {
                if (i >= messageObject.messageOwner.media.document.attributes.size()) {
                    break;
                } else if (messageObject.messageOwner.media.document.attributes.get(i) instanceof TLRPC$TL_documentAttributeAnimated) {
                    intent.putExtra("gif", true);
                    break;
                } else {
                    i++;
                }
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
                for (String equalsIgnoreCase : codecInfoAt.getSupportedTypes()) {
                    if (equalsIgnoreCase.equalsIgnoreCase(str)) {
                        String name = codecInfoAt.getName();
                        if (name != null && (!name.equals("OMX.SEC.avc.enc") || name.equals("OMX.SEC.AVC.Encoder"))) {
                            return codecInfoAt;
                        }
                        mediaCodecInfo = codecInfoAt;
                    }
                }
                continue;
            }
        }
        return mediaCodecInfo;
    }

    @SuppressLint({"NewApi"})
    public static int selectColorFormat(MediaCodecInfo mediaCodecInfo, String str) {
        int i;
        MediaCodecInfo.CodecCapabilities capabilitiesForType = mediaCodecInfo.getCapabilitiesForType(str);
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

    /* access modifiers changed from: private */
    public void didWriteData(VideoConvertMessage videoConvertMessage, File file, boolean z, long j, long j2, boolean z2, float f) {
        VideoEditedInfo videoEditedInfo = videoConvertMessage.videoEditedInfo;
        boolean z3 = videoEditedInfo.videoConvertFirstWrite;
        if (z3) {
            videoEditedInfo.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda33(this, z2, z, videoConvertMessage, file, f, j, z3, j2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didWriteData$42(boolean z, boolean z2, VideoConvertMessage videoConvertMessage, File file, float f, long j, boolean z3, long j2) {
        VideoConvertMessage videoConvertMessage2 = videoConvertMessage;
        if (z || z2) {
            synchronized (this.videoConvertSync) {
                videoConvertMessage2.videoEditedInfo.canceled = false;
            }
            this.videoConvertQueue.remove(videoConvertMessage);
            startVideoConvertFromQueue();
        }
        if (z) {
            NotificationCenter.getInstance(videoConvertMessage2.currentAccount).postNotificationName(NotificationCenter.filePreparingFailed, videoConvertMessage2.messageObject, file.toString(), Float.valueOf(f), Long.valueOf(j));
            return;
        }
        if (z3) {
            NotificationCenter.getInstance(videoConvertMessage2.currentAccount).postNotificationName(NotificationCenter.filePreparingStarted, videoConvertMessage2.messageObject, file.toString(), Float.valueOf(f), Long.valueOf(j));
        }
        NotificationCenter instance = NotificationCenter.getInstance(videoConvertMessage2.currentAccount);
        int i = NotificationCenter.fileNewChunkAvailable;
        Object[] objArr = new Object[6];
        objArr[0] = videoConvertMessage2.messageObject;
        objArr[1] = file.toString();
        objArr[2] = Long.valueOf(j2);
        objArr[3] = Long.valueOf(z2 ? file.length() : 0);
        objArr[4] = Float.valueOf(f);
        objArr[5] = Long.valueOf(j);
        instance.postNotificationName(i, objArr);
    }

    public void pauseByRewind() {
        VideoPlayer videoPlayer2 = this.audioPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.pause();
        }
    }

    public void resumeByRewind() {
        VideoPlayer videoPlayer2 = this.audioPlayer;
        if (videoPlayer2 != null && this.playingMessageObject != null && !this.isPaused) {
            if (videoPlayer2.isBuffering()) {
                MessageObject messageObject = this.playingMessageObject;
                cleanupPlayer(false, false);
                playMessage(messageObject);
                return;
            }
            this.audioPlayer.play();
        }
    }

    private static class VideoConvertRunnable implements Runnable {
        private VideoConvertMessage convertMessage;

        private VideoConvertRunnable(VideoConvertMessage videoConvertMessage) {
            this.convertMessage = videoConvertMessage;
        }

        public void run() {
            boolean unused = MediaController.getInstance().convertVideo(this.convertMessage);
        }

        public static void runConversion(VideoConvertMessage videoConvertMessage) {
            new Thread(new MediaController$VideoConvertRunnable$$ExternalSyntheticLambda0(videoConvertMessage)).start();
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$runConversion$0(VideoConvertMessage videoConvertMessage) {
            try {
                Thread thread = new Thread(new VideoConvertRunnable(videoConvertMessage), "VideoConvertRunnable");
                thread.start();
                thread.join();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00f2  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0102  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x018b  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0199  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean convertVideo(org.telegram.messenger.MediaController.VideoConvertMessage r45) {
        /*
            r44 = this;
            r11 = r44
            r0 = r45
            org.telegram.messenger.MessageObject r1 = r0.messageObject
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            if (r1 == 0) goto L_0x01eb
            if (r2 != 0) goto L_0x000e
            goto L_0x01eb
        L_0x000e:
            java.lang.String r4 = r2.originalPath
            long r5 = r2.startTime
            long r7 = r2.avatarStartTime
            long r9 = r2.endTime
            int r12 = r2.resultWidth
            int r13 = r2.resultHeight
            int r15 = r2.rotationValue
            int r14 = r2.originalWidth
            int r3 = r2.originalHeight
            int r0 = r2.framerate
            int r11 = r2.bitrate
            r16 = r11
            int r11 = r2.originalBitrate
            long r17 = r1.getDialogId()
            boolean r17 = org.telegram.messenger.DialogObject.isEncryptedDialog(r17)
            r40 = r2
            java.io.File r2 = new java.io.File
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.attachPath
            r2.<init>(r1)
            boolean r1 = r2.exists()
            if (r1 == 0) goto L_0x0044
            r2.delete()
        L_0x0044:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x00b9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r41 = r2
            java.lang.String r2 = "begin convert "
            r1.append(r2)
            r1.append(r4)
            java.lang.String r2 = " startTime = "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r2 = " avatarStartTime = "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = " endTime "
            r1.append(r2)
            r1.append(r9)
            java.lang.String r2 = " rWidth = "
            r1.append(r2)
            r1.append(r12)
            java.lang.String r2 = " rHeight = "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r2 = " rotation = "
            r1.append(r2)
            r1.append(r15)
            java.lang.String r2 = " oWidth = "
            r1.append(r2)
            r1.append(r14)
            java.lang.String r2 = " oHeight = "
            r1.append(r2)
            r1.append(r3)
            java.lang.String r2 = " framerate = "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r2 = " bitrate = "
            r1.append(r2)
            r2 = r16
            r1.append(r2)
            java.lang.String r2 = " originalBitrate = "
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.FileLog.d(r1)
            goto L_0x00bb
        L_0x00b9:
            r41 = r2
        L_0x00bb:
            if (r4 != 0) goto L_0x00bf
            java.lang.String r4 = ""
        L_0x00bf:
            r1 = 0
            int r18 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r18 <= 0) goto L_0x00ce
            int r18 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r18 <= 0) goto L_0x00ce
            long r1 = r9 - r5
            r31 = r1
            goto L_0x00d4
        L_0x00ce:
            int r18 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r18 <= 0) goto L_0x00da
            r31 = r9
        L_0x00d4:
            r2 = r12
            r18 = r13
            r1 = r40
            goto L_0x00f0
        L_0x00da:
            int r18 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r18 <= 0) goto L_0x00e7
            r2 = r12
            r18 = r13
            r1 = r40
            long r12 = r1.originalDuration
            long r12 = r12 - r5
            goto L_0x00ee
        L_0x00e7:
            r2 = r12
            r18 = r13
            r1 = r40
            long r12 = r1.originalDuration
        L_0x00ee:
            r31 = r12
        L_0x00f0:
            if (r0 != 0) goto L_0x00f7
            r0 = 25
            r21 = 25
            goto L_0x00f9
        L_0x00f7:
            r21 = r0
        L_0x00f9:
            r0 = 90
            if (r15 == r0) goto L_0x0105
            r0 = 270(0x10e, float:3.78E-43)
            if (r15 != r0) goto L_0x0102
            goto L_0x0105
        L_0x0102:
            r0 = r18
            goto L_0x0108
        L_0x0105:
            r0 = r2
            r2 = r18
        L_0x0108:
            r12 = -1
            r26 = r9
            r9 = 1
            int r10 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1))
            if (r10 != 0) goto L_0x0133
            org.telegram.messenger.MediaController$CropState r10 = r1.cropState
            if (r10 != 0) goto L_0x0133
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r10 = r1.mediaEntities
            if (r10 != 0) goto L_0x0133
            java.lang.String r10 = r1.paintPath
            if (r10 != 0) goto L_0x0133
            org.telegram.messenger.MediaController$SavedFilterState r10 = r1.filterState
            if (r10 != 0) goto L_0x0133
            if (r2 != r14) goto L_0x0133
            if (r0 != r3) goto L_0x0133
            if (r15 != 0) goto L_0x0133
            boolean r10 = r1.roundVideo
            if (r10 != 0) goto L_0x0133
            int r10 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r10 == 0) goto L_0x0130
            goto L_0x0133
        L_0x0130:
            r30 = 0
            goto L_0x0135
        L_0x0133:
            r30 = 1
        L_0x0135:
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r12 = "videoconvert"
            r13 = 0
            android.content.SharedPreferences r10 = r10.getSharedPreferences(r12, r13)
            long r42 = java.lang.System.currentTimeMillis()
            org.telegram.messenger.MediaController$13 r12 = new org.telegram.messenger.MediaController$13
            r38 = r12
            r13 = r44
            r28 = r7
            r40 = r10
            r22 = r16
            r7 = r41
            r10 = r45
            r12.<init>(r1, r7, r10)
            r1.videoConvertFirstWrite = r9
            org.telegram.messenger.video.MediaCodecVideoConvertor r8 = new org.telegram.messenger.video.MediaCodecVideoConvertor
            r12 = r8
            r8.<init>()
            org.telegram.messenger.MediaController$SavedFilterState r9 = r1.filterState
            r33 = r9
            java.lang.String r9 = r1.paintPath
            r34 = r9
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r9 = r1.mediaEntities
            r35 = r9
            boolean r9 = r1.isPhoto
            r36 = r9
            org.telegram.messenger.MediaController$CropState r9 = r1.cropState
            r37 = r9
            r9 = r13
            r13 = r4
            r4 = r14
            r14 = r7
            r16 = r17
            r17 = r4
            r18 = r3
            r19 = r2
            r20 = r0
            r23 = r11
            r24 = r5
            boolean r0 = r12.convertVideo(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r26, r28, r30, r31, r33, r34, r35, r36, r37, r38)
            boolean r2 = r1.canceled
            if (r2 != 0) goto L_0x0195
            java.lang.Object r3 = r9.videoConvertSync
            monitor-enter(r3)
            boolean r2 = r1.canceled     // Catch:{ all -> 0x0192 }
            monitor-exit(r3)     // Catch:{ all -> 0x0192 }
            goto L_0x0195
        L_0x0192:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0192 }
            throw r0
        L_0x0195:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x01bb
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "time="
            r1.append(r3)
            long r3 = java.lang.System.currentTimeMillis()
            long r3 = r3 - r42
            r1.append(r3)
            java.lang.String r3 = " canceled="
            r1.append(r3)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.FileLog.d(r1)
        L_0x01bb:
            android.content.SharedPreferences$Editor r1 = r40.edit()
            java.lang.String r3 = "isPreviousOk"
            r11 = 1
            android.content.SharedPreferences$Editor r1 = r1.putBoolean(r3, r11)
            r1.apply()
            r4 = 1
            long r5 = r8.getLastFrameTimestamp()
            long r12 = r7.length()
            if (r0 != 0) goto L_0x01da
            if (r2 == 0) goto L_0x01d7
            goto L_0x01da
        L_0x01d7:
            r39 = 0
            goto L_0x01dc
        L_0x01da:
            r39 = 1
        L_0x01dc:
            r0 = 1065353216(0x3var_, float:1.0)
            r1 = r44
            r3 = r7
            r2 = r45
            r7 = r12
            r9 = r39
            r10 = r0
            r1.didWriteData(r2, r3, r4, r5, r7, r9, r10)
            return r11
        L_0x01eb:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MediaController$VideoConvertMessage):boolean");
    }

    public static int getVideoBitrate(String str) {
        int i;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(str);
            i = Integer.parseInt(mediaMetadataRetriever.extractMetadata(20));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            i = 0;
        }
        mediaMetadataRetriever.release();
        return i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0057 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0058  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int makeVideoBitrate(int r5, int r6, int r7, int r8, int r9) {
        /*
            int r0 = java.lang.Math.min(r8, r9)
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 1080(0x438, float:1.513E-42)
            if (r0 < r2) goto L_0x0010
            r0 = 6800000(0x67CLASSNAME, float:9.52883E-39)
        L_0x000d:
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0037
        L_0x0010:
            int r0 = java.lang.Math.min(r8, r9)
            r2 = 720(0x2d0, float:1.009E-42)
            if (r0 < r2) goto L_0x001c
            r0 = 3200000(0x30d400, float:4.484155E-39)
            goto L_0x000d
        L_0x001c:
            int r0 = java.lang.Math.min(r8, r9)
            r1 = 480(0x1e0, float:6.73E-43)
            if (r0 < r1) goto L_0x002e
            r0 = 1000000(0xvar_, float:1.401298E-39)
            r1 = 1061997773(0x3f4ccccd, float:0.8)
            r2 = 1063675494(0x3var_, float:0.9)
            goto L_0x0037
        L_0x002e:
            r0 = 750000(0xb71b0, float:1.050974E-39)
            r1 = 1058642330(0x3var_a, float:0.6)
            r2 = 1060320051(0x3var_, float:0.7)
        L_0x0037:
            float r3 = (float) r7
            float r5 = (float) r5
            float r4 = (float) r8
            float r5 = r5 / r4
            float r6 = (float) r6
            float r4 = (float) r9
            float r6 = r6 / r4
            float r5 = java.lang.Math.min(r5, r6)
            float r3 = r3 / r5
            int r5 = (int) r3
            float r5 = (float) r5
            float r5 = r5 * r1
            int r5 = (int) r5
            int r6 = getVideoBitrateWithFactor(r2)
            float r6 = (float) r6
            r1 = 1231093760(0x49610000, float:921600.0)
            int r9 = r9 * r8
            float r8 = (float) r9
            float r1 = r1 / r8
            float r6 = r6 / r1
            int r6 = (int) r6
            if (r7 >= r6) goto L_0x0058
            return r5
        L_0x0058:
            if (r5 <= r0) goto L_0x005b
            return r0
        L_0x005b:
            int r5 = java.lang.Math.max(r5, r6)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.makeVideoBitrate(int, int, int, int, int):int");
    }

    public static class PlaylistGlobalSearchParams {
        final long dialogId;
        public boolean endReached;
        final FiltersView.MediaFilterData filter;
        public int folderId;
        final long maxDate;
        final long minDate;
        public int nextSearchRate;
        final String query;
        public int totalCount;

        public PlaylistGlobalSearchParams(String str, long j, long j2, long j3, FiltersView.MediaFilterData mediaFilterData) {
            this.filter = mediaFilterData;
            this.query = str;
            this.dialogId = j;
            this.minDate = j2;
            this.maxDate = j3;
        }
    }

    public boolean currentPlaylistIsGlobalSearch() {
        return this.playlistGlobalSearchParams != null;
    }
}
