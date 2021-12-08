package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaCodecInfo;
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
import android.widget.FrameLayout;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.gms.internal.icing.zzby$$ExternalSyntheticBackport0;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
import org.telegram.messenger.video.MediaCodecVideoConvertor;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
    AudioManager.OnAudioFocusChangeListener audioRecordFocusChangedListener = new MediaController$$ExternalSyntheticLambda32(this);
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
    private TLRPC.EncryptedChat lastSecretChat;
    private long lastTimestamp = 0;
    private TLRPC.User lastUser;
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
        /* JADX WARNING: Removed duplicated region for block: B:52:0x011f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r16 = this;
                r1 = r16
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                android.media.AudioRecord r0 = r0.audioRecorder
                if (r0 == 0) goto L_0x016e
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                boolean r0 = r0.isEmpty()
                r2 = 0
                if (r0 != 0) goto L_0x002e
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                java.lang.Object r0 = r0.get(r2)
                java.nio.ByteBuffer r0 = (java.nio.ByteBuffer) r0
                org.telegram.messenger.MediaController r3 = org.telegram.messenger.MediaController.this
                java.util.ArrayList r3 = r3.recordBuffers
                r3.remove(r2)
                r3 = r0
                goto L_0x003e
            L_0x002e:
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                int r0 = r0.recordBufferSize
                java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r0)
                java.nio.ByteOrder r3 = java.nio.ByteOrder.nativeOrder()
                r0.order(r3)
                r3 = r0
            L_0x003e:
                r3.rewind()
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                android.media.AudioRecord r0 = r0.audioRecorder
                int r4 = r3.capacity()
                int r4 = r0.read(r3, r4)
                if (r4 <= 0) goto L_0x0146
                r3.limit(r4)
                r5 = 0
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x0101 }
                long r7 = r0.samplesCount     // Catch:{ Exception -> 0x0101 }
                int r0 = r4 / 2
                long r9 = (long) r0     // Catch:{ Exception -> 0x0101 }
                long r7 = r7 + r9
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x0101 }
                long r9 = r0.samplesCount     // Catch:{ Exception -> 0x0101 }
                double r9 = (double) r9
                double r11 = (double) r7
                java.lang.Double.isNaN(r9)
                java.lang.Double.isNaN(r11)
                double r9 = r9 / r11
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x0101 }
                short[] r0 = r0.recordSamples     // Catch:{ Exception -> 0x0101 }
                int r0 = r0.length     // Catch:{ Exception -> 0x0101 }
                double r11 = (double) r0
                java.lang.Double.isNaN(r11)
                double r9 = r9 * r11
                int r0 = (int) r9
                org.telegram.messenger.MediaController r9 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x0101 }
                short[] r9 = r9.recordSamples     // Catch:{ Exception -> 0x0101 }
                int r9 = r9.length     // Catch:{ Exception -> 0x0101 }
                int r9 = r9 - r0
                if (r0 == 0) goto L_0x00ad
                org.telegram.messenger.MediaController r10 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00aa }
                short[] r10 = r10.recordSamples     // Catch:{ Exception -> 0x00aa }
                int r10 = r10.length     // Catch:{ Exception -> 0x00aa }
                float r10 = (float) r10     // Catch:{ Exception -> 0x00aa }
                float r11 = (float) r0     // Catch:{ Exception -> 0x00aa }
                float r10 = r10 / r11
                r11 = 0
                r12 = 0
            L_0x0093:
                if (r12 >= r0) goto L_0x00ad
                org.telegram.messenger.MediaController r13 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00aa }
                short[] r13 = r13.recordSamples     // Catch:{ Exception -> 0x00aa }
                org.telegram.messenger.MediaController r14 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00aa }
                short[] r14 = r14.recordSamples     // Catch:{ Exception -> 0x00aa }
                int r15 = (int) r11     // Catch:{ Exception -> 0x00aa }
                short r14 = r14[r15]     // Catch:{ Exception -> 0x00aa }
                r13[r12] = r14     // Catch:{ Exception -> 0x00aa }
                float r11 = r11 + r10
                int r12 = r12 + 1
                goto L_0x0093
            L_0x00aa:
                r0 = move-exception
                r15 = r3
                goto L_0x0103
            L_0x00ad:
                r10 = r0
                r11 = 0
                float r12 = (float) r4
                r13 = 1073741824(0x40000000, float:2.0)
                float r12 = r12 / r13
                float r13 = (float) r9
                float r12 = r12 / r13
                r13 = 0
            L_0x00b6:
                int r14 = r4 / 2
                if (r13 >= r14) goto L_0x00f7
                short r14 = r3.getShort()     // Catch:{ Exception -> 0x0101 }
                int r15 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0101 }
                r2 = 21
                if (r15 >= r2) goto L_0x00d3
                r2 = 2500(0x9c4, float:3.503E-42)
                if (r14 <= r2) goto L_0x00d1
                int r2 = r14 * r14
                r15 = r3
                double r2 = (double) r2
                java.lang.Double.isNaN(r2)
                double r5 = r5 + r2
                goto L_0x00db
            L_0x00d1:
                r15 = r3
                goto L_0x00db
            L_0x00d3:
                r15 = r3
                int r2 = r14 * r14
                double r2 = (double) r2
                java.lang.Double.isNaN(r2)
                double r5 = r5 + r2
            L_0x00db:
                int r2 = (int) r11
                if (r13 != r2) goto L_0x00f2
                org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00ff }
                short[] r2 = r2.recordSamples     // Catch:{ Exception -> 0x00ff }
                int r2 = r2.length     // Catch:{ Exception -> 0x00ff }
                if (r10 >= r2) goto L_0x00f2
                org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00ff }
                short[] r2 = r2.recordSamples     // Catch:{ Exception -> 0x00ff }
                r2[r10] = r14     // Catch:{ Exception -> 0x00ff }
                float r11 = r11 + r12
                int r10 = r10 + 1
            L_0x00f2:
                int r13 = r13 + 1
                r3 = r15
                r2 = 0
                goto L_0x00b6
            L_0x00f7:
                r15 = r3
                org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.this     // Catch:{ Exception -> 0x00ff }
                long unused = r2.samplesCount = r7     // Catch:{ Exception -> 0x00ff }
                goto L_0x0106
            L_0x00ff:
                r0 = move-exception
                goto L_0x0103
            L_0x0101:
                r0 = move-exception
                r15 = r3
            L_0x0103:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0106:
                r2 = r15
                r3 = 0
                r2.position(r3)
                double r7 = (double) r4
                java.lang.Double.isNaN(r7)
                double r7 = r5 / r7
                r9 = 4611686018427387904(0xNUM, double:2.0)
                double r7 = r7 / r9
                double r7 = java.lang.Math.sqrt(r7)
                r0 = r2
                int r9 = r2.capacity()
                if (r4 == r9) goto L_0x0120
                r3 = 1
            L_0x0120:
                org.telegram.messenger.MediaController r9 = org.telegram.messenger.MediaController.this
                org.telegram.messenger.DispatchQueue r9 = r9.fileEncodingQueue
                org.telegram.messenger.MediaController$2$$ExternalSyntheticLambda2 r10 = new org.telegram.messenger.MediaController$2$$ExternalSyntheticLambda2
                r10.<init>(r1, r0, r3)
                r9.postRunnable(r10)
                org.telegram.messenger.MediaController r9 = org.telegram.messenger.MediaController.this
                org.telegram.messenger.DispatchQueue r9 = r9.recordQueue
                org.telegram.messenger.MediaController r10 = org.telegram.messenger.MediaController.this
                java.lang.Runnable r10 = r10.recordRunnable
                r9.postRunnable(r10)
                org.telegram.messenger.MediaController$2$$ExternalSyntheticLambda0 r9 = new org.telegram.messenger.MediaController$2$$ExternalSyntheticLambda0
                r9.<init>(r1, r7)
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r9)
                goto L_0x016e
            L_0x0146:
                r2 = r3
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                r0.add(r2)
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                int r0 = r0.sendAfterDone
                r3 = 3
                if (r0 == r3) goto L_0x016e
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                int r3 = r0.sendAfterDone
                org.telegram.messenger.MediaController r5 = org.telegram.messenger.MediaController.this
                boolean r5 = r5.sendAfterDoneNotify
                org.telegram.messenger.MediaController r6 = org.telegram.messenger.MediaController.this
                int r6 = r6.sendAfterDoneScheduleDate
                r0.stopRecordingInternal(r3, r5, r6)
            L_0x016e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.AnonymousClass2.run():void");
        }

        /* renamed from: lambda$run$1$org-telegram-messenger-MediaController$2  reason: not valid java name */
        public /* synthetic */ void m753lambda$run$1$orgtelegrammessengerMediaController$2(ByteBuffer finalBuffer, boolean flush) {
            while (finalBuffer.hasRemaining()) {
                int oldLimit = -1;
                if (finalBuffer.remaining() > MediaController.this.fileBuffer.remaining()) {
                    oldLimit = finalBuffer.limit();
                    finalBuffer.limit(MediaController.this.fileBuffer.remaining() + finalBuffer.position());
                }
                MediaController.this.fileBuffer.put(finalBuffer);
                if (MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit() || flush) {
                    MediaController mediaController = MediaController.this;
                    if (mediaController.writeFrame(mediaController.fileBuffer, !flush ? MediaController.this.fileBuffer.limit() : finalBuffer.position()) != 0) {
                        MediaController.this.fileBuffer.rewind();
                        MediaController mediaController2 = MediaController.this;
                        MediaController.access$1614(mediaController2, (long) ((mediaController2.fileBuffer.limit() / 2) / (MediaController.this.sampleRate / 1000)));
                    }
                }
                if (oldLimit != -1) {
                    finalBuffer.limit(oldLimit);
                }
            }
            MediaController.this.recordQueue.postRunnable(new MediaController$2$$ExternalSyntheticLambda1(this, finalBuffer));
        }

        /* renamed from: lambda$run$0$org-telegram-messenger-MediaController$2  reason: not valid java name */
        public /* synthetic */ void m752lambda$run$0$orgtelegrammessengerMediaController$2(ByteBuffer finalBuffer) {
            MediaController.this.recordBuffers.add(finalBuffer);
        }

        /* renamed from: lambda$run$2$org-telegram-messenger-MediaController$2  reason: not valid java name */
        public /* synthetic */ void m754lambda$run$2$orgtelegrammessengerMediaController$2(double amplitude) {
            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(MediaController.this.recordingGuid), Double.valueOf(amplitude));
        }
    };
    /* access modifiers changed from: private */
    public short[] recordSamples = new short[1024];
    /* access modifiers changed from: private */
    public Runnable recordStartRunnable;
    private long recordStartTime;
    private long recordTimeCount;
    /* access modifiers changed from: private */
    public TLRPC.TL_document recordingAudio;
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

    public static native int isOpusFile(String str);

    private native int startRecord(String str, int i);

    private native void stopRecord();

    /* access modifiers changed from: private */
    public native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    static /* synthetic */ long access$1614(MediaController x0, long x1) {
        long j = x0.recordTimeCount + x1;
        x0.recordTimeCount = j;
        return j;
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

        public AudioBuffer(int capacity) {
            this.buffer = ByteBuffer.allocateDirect(capacity);
            this.bufferBytes = new byte[capacity];
        }
    }

    static {
        String[] strArr = new String[9];
        strArr[0] = "_id";
        strArr[1] = "bucket_id";
        strArr[2] = "bucket_display_name";
        strArr[3] = "_data";
        String str = "date_modified";
        strArr[4] = Build.VERSION.SDK_INT > 28 ? str : "datetaken";
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
        if (Build.VERSION.SDK_INT <= 28) {
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

        public AlbumEntry(int bucketId2, String bucketName2, PhotoEntry coverPhoto2) {
            this.bucketId = bucketId2;
            this.bucketName = bucketName2;
            this.coverPhoto = coverPhoto2;
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
        public ArrayList<TLRPC.MessageEntity> entities;
        public String filterPath;
        public String fullPaintPath;
        public String imagePath;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isPainted;
        public ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
        public String paintPath;
        public SavedFilterState savedFilterState;
        public ArrayList<TLRPC.InputDocument> stickers;
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

        public void copyFrom(MediaEditState state) {
            this.caption = state.caption;
            this.thumbPath = state.thumbPath;
            this.imagePath = state.imagePath;
            this.filterPath = state.filterPath;
            this.paintPath = state.paintPath;
            this.croppedPaintPath = state.croppedPaintPath;
            this.fullPaintPath = state.fullPaintPath;
            this.entities = state.entities;
            this.savedFilterState = state.savedFilterState;
            this.mediaEntities = state.mediaEntities;
            this.croppedMediaEntities = state.croppedMediaEntities;
            this.stickers = state.stickers;
            this.editedInfo = state.editedInfo;
            this.averageDuration = state.averageDuration;
            this.isFiltered = state.isFiltered;
            this.isPainted = state.isPainted;
            this.isCropped = state.isCropped;
            this.ttl = state.ttl;
            this.cropState = state.cropState;
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

        public PhotoEntry(int bucketId2, int imageId2, long dateTaken2, String path2, int orientation2, boolean isVideo2, int width2, int height2, long size2) {
            this.bucketId = bucketId2;
            this.imageId = imageId2;
            this.dateTaken = dateTaken2;
            this.path = path2;
            this.width = width2;
            this.height = height2;
            this.size = size2;
            if (isVideo2) {
                this.duration = orientation2;
            } else {
                this.orientation = orientation2;
            }
            this.isVideo = isVideo2;
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
        public TLRPC.Document document;
        public int height;
        public String id;
        public String imageUrl;
        public TLRPC.BotInlineResult inlineResult;
        public HashMap<String, String> params;
        public TLRPC.Photo photo;
        public TLRPC.PhotoSize photoSize;
        public int size;
        public TLRPC.PhotoSize thumbPhotoSize;
        public String thumbUrl;
        public int type;
        public int width;

        public String getPath() {
            TLRPC.PhotoSize photoSize2 = this.photoSize;
            if (photoSize2 != null) {
                return FileLoader.getPathToAttach(photoSize2, true).getAbsolutePath();
            }
            TLRPC.Document document2 = this.document;
            if (document2 != null) {
                return FileLoader.getPathToAttach(document2, true).getAbsolutePath();
            }
            return ImageLoader.getHttpFilePath(this.imageUrl, "jpg").getAbsolutePath();
        }

        public void reset() {
            super.reset();
        }

        public String getAttachName() {
            TLRPC.PhotoSize photoSize2 = this.photoSize;
            if (photoSize2 != null) {
                return FileLoader.getAttachFileName(photoSize2);
            }
            TLRPC.Document document2 = this.document;
            if (document2 != null) {
                return FileLoader.getAttachFileName(document2);
            }
            return Utilities.MD5(this.imageUrl) + "." + ImageLoader.getHttpUrlExtension(this.imageUrl, "jpg");
        }

        public String getPathToAttach() {
            TLRPC.PhotoSize photoSize2 = this.photoSize;
            if (photoSize2 != null) {
                return FileLoader.getPathToAttach(photoSize2, true).getAbsolutePath();
            }
            TLRPC.Document document2 = this.document;
            if (document2 != null) {
                return FileLoader.getPathToAttach(document2, true).getAbsolutePath();
            }
            return this.imageUrl;
        }
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m91lambda$new$0$orgtelegrammessengerMediaController(int focusChange) {
        if (focusChange != 1) {
            this.hasRecordAudioFocus = false;
        }
    }

    private static class VideoConvertMessage {
        public int currentAccount;
        public MessageObject messageObject;
        public VideoEditedInfo videoEditedInfo;

        public VideoConvertMessage(MessageObject object, VideoEditedInfo info) {
            this.messageObject = object;
            this.currentAccount = object.currentAccount;
            this.videoEditedInfo = info;
        }
    }

    private class InternalObserver extends ContentObserver {
        public InternalObserver() {
            super((Handler) null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MediaController.this.processMediaObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        }
    }

    private class ExternalObserver extends ContentObserver {
        public ExternalObserver() {
            super((Handler) null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
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

        /* renamed from: lambda$scheduleReloadRunnable$0$org-telegram-messenger-MediaController$GalleryObserverInternal  reason: not valid java name */
        public /* synthetic */ void m760x25ddcvar_() {
            if (PhotoViewer.getInstance().isVisible()) {
                scheduleReloadRunnable();
                return;
            }
            Runnable unused = MediaController.refreshGalleryRunnable = null;
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

    private static class GalleryObserverExternal extends ContentObserver {
        public GalleryObserverExternal() {
            super((Handler) null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = MediaController$GalleryObserverExternal$$ExternalSyntheticLambda0.INSTANCE, 2000);
        }

        static /* synthetic */ void lambda$onChange$0() {
            Runnable unused = MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public static void checkGallery() {
        AlbumEntry albumEntry;
        if (Build.VERSION.SDK_INT >= 24 && (albumEntry = allPhotosAlbumEntry) != null) {
            Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda37(albumEntry.photos.size()), 2000);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0031, code lost:
        if (r3 != null) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0033, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003b, code lost:
        if (r3 == null) goto L_0x003e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0044, code lost:
        if (org.telegram.messenger.ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0046, code lost:
        r3 = android.provider.MediaStore.Images.Media.query(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new java.lang.String[]{"COUNT(_id)"}, (java.lang.String) null, (java.lang.String[]) null, (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005a, code lost:
        if (r3 == null) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0060, code lost:
        if (r3.moveToNext() == false) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0066, code lost:
        r2 = r2 + r3.getInt(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0067, code lost:
        if (r3 == null) goto L_0x0074;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0069, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x006d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0071, code lost:
        if (r3 == null) goto L_0x0074;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0074, code lost:
        if (r13 == r2) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0076, code lost:
        r0 = refreshGalleryRunnable;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0078, code lost:
        if (r0 == null) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x007a, code lost:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        refreshGalleryRunnable = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0080, code lost:
        loadGalleryPhotosAlbums(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0084, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0085, code lost:
        if (r3 != null) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0087, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x008a, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$checkGallery$1(int r13) {
        /*
            java.lang.String r0 = "COUNT(_id)"
            java.lang.String r1 = "android.permission.READ_EXTERNAL_STORAGE"
            r2 = 0
            r3 = 0
            r4 = 1
            r5 = 0
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0037 }
            int r6 = r6.checkSelfPermission(r1)     // Catch:{ all -> 0x0037 }
            if (r6 != 0) goto L_0x0031
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0037 }
            android.content.ContentResolver r7 = r6.getContentResolver()     // Catch:{ all -> 0x0037 }
            android.net.Uri r8 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x0037 }
            java.lang.String[] r9 = new java.lang.String[r4]     // Catch:{ all -> 0x0037 }
            r9[r5] = r0     // Catch:{ all -> 0x0037 }
            r10 = 0
            r11 = 0
            r12 = 0
            android.database.Cursor r6 = android.provider.MediaStore.Images.Media.query(r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x0037 }
            r3 = r6
            if (r3 == 0) goto L_0x0031
            boolean r6 = r3.moveToNext()     // Catch:{ all -> 0x0037 }
            if (r6 == 0) goto L_0x0031
            int r6 = r3.getInt(r5)     // Catch:{ all -> 0x0037 }
            int r2 = r2 + r6
        L_0x0031:
            if (r3 == 0) goto L_0x003e
        L_0x0033:
            r3.close()
            goto L_0x003e
        L_0x0037:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x008b }
            if (r3 == 0) goto L_0x003e
            goto L_0x0033
        L_0x003e:
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x006d }
            int r1 = r6.checkSelfPermission(r1)     // Catch:{ all -> 0x006d }
            if (r1 != 0) goto L_0x0067
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x006d }
            android.content.ContentResolver r6 = r1.getContentResolver()     // Catch:{ all -> 0x006d }
            android.net.Uri r7 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x006d }
            java.lang.String[] r8 = new java.lang.String[r4]     // Catch:{ all -> 0x006d }
            r8[r5] = r0     // Catch:{ all -> 0x006d }
            r9 = 0
            r10 = 0
            r11 = 0
            android.database.Cursor r0 = android.provider.MediaStore.Images.Media.query(r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x006d }
            r3 = r0
            if (r3 == 0) goto L_0x0067
            boolean r0 = r3.moveToNext()     // Catch:{ all -> 0x006d }
            if (r0 == 0) goto L_0x0067
            int r0 = r3.getInt(r5)     // Catch:{ all -> 0x006d }
            int r2 = r2 + r0
        L_0x0067:
            if (r3 == 0) goto L_0x0074
        L_0x0069:
            r3.close()
            goto L_0x0074
        L_0x006d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0084 }
            if (r3 == 0) goto L_0x0074
            goto L_0x0069
        L_0x0074:
            if (r13 == r2) goto L_0x0083
            java.lang.Runnable r0 = refreshGalleryRunnable
            if (r0 == 0) goto L_0x0080
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r0 = 0
            refreshGalleryRunnable = r0
        L_0x0080:
            loadGalleryPhotosAlbums(r5)
        L_0x0083:
            return
        L_0x0084:
            r0 = move-exception
            if (r3 == 0) goto L_0x008a
            r3.close()
        L_0x008a:
            throw r0
        L_0x008b:
            r0 = move-exception
            if (r3 == 0) goto L_0x0091
            r3.close()
        L_0x0091:
            goto L_0x0093
        L_0x0092:
            throw r0
        L_0x0093:
            goto L_0x0092
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
        DispatchQueue dispatchQueue = new DispatchQueue("recordQueue");
        this.recordQueue = dispatchQueue;
        dispatchQueue.setPriority(10);
        DispatchQueue dispatchQueue2 = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue = dispatchQueue2;
        dispatchQueue2.setPriority(10);
        this.recordQueue.postRunnable(new MediaController$$ExternalSyntheticLambda42(this));
        Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda0(this));
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda1(this));
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

    /* renamed from: lambda$new$2$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m92lambda$new$2$orgtelegrammessengerMediaController() {
        try {
            this.sampleRate = 16000;
            int minBuferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
            if (minBuferSize <= 0) {
                minBuferSize = 1280;
            }
            this.recordBufferSize = minBuferSize;
            for (int a = 0; a < 5; a++) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(this.recordBufferSize);
                buffer.order(ByteOrder.nativeOrder());
                this.recordBuffers.add(buffer);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$new$3$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m93lambda$new$3$orgtelegrammessengerMediaController() {
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
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                public void onCallStateChanged(int state, String incomingNumber) {
                    AndroidUtilities.runOnUIThread(new MediaController$4$$ExternalSyntheticLambda0(this, state));
                }

                /* renamed from: lambda$onCallStateChanged$0$org-telegram-messenger-MediaController$4  reason: not valid java name */
                public /* synthetic */ void m755x5CLASSNAMEee(int state) {
                    if (state == 1) {
                        MediaController mediaController = MediaController.this;
                        if (mediaController.isPlayingMessage(mediaController.playingMessageObject) && !MediaController.this.isMessagePaused()) {
                            MediaController mediaController2 = MediaController.this;
                            mediaController2.m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(mediaController2.playingMessageObject);
                        } else if (!(MediaController.this.recordStartRunnable == null && MediaController.this.recordingAudio == null)) {
                            MediaController.this.stopRecording(2, false, 0);
                        }
                        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
                        if (embedBottomSheet != null) {
                            embedBottomSheet.pause();
                        }
                        boolean unused = MediaController.this.callInProgress = true;
                    } else if (state == 0) {
                        boolean unused2 = MediaController.this.callInProgress = false;
                    } else if (state == 2) {
                        EmbedBottomSheet embedBottomSheet2 = EmbedBottomSheet.getInstance();
                        if (embedBottomSheet2 != null) {
                            embedBottomSheet2.pause();
                        }
                        boolean unused3 = MediaController.this.callInProgress = true;
                    }
                }
            };
            TelephonyManager mgr = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (mgr != null) {
                mgr.listen(phoneStateListener, 32);
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m94lambda$new$4$orgtelegrammessengerMediaController() {
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.didReceiveNewMessages);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.musicDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.mediaDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    public void onAudioFocusChange(int focusChange) {
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda6(this, focusChange));
    }

    /* renamed from: lambda$onAudioFocusChange$5$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m95x37ee53c8(int focusChange) {
        if (focusChange == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.playingMessageObject);
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
                m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.playingMessageObject);
                this.resumeAudioOnFocusGain = true;
            }
        }
        setPlayerVolume();
    }

    /* access modifiers changed from: private */
    public void setPlayerVolume() {
        float volume;
        try {
            if (this.audioFocus != 1) {
                volume = 1.0f;
            } else {
                volume = 0.2f;
            }
            VideoPlayer videoPlayer2 = this.audioPlayer;
            if (videoPlayer2 != null) {
                videoPlayer2.setVolume(this.audioVolume * volume);
                return;
            }
            VideoPlayer videoPlayer3 = this.videoPlayer;
            if (videoPlayer3 != null) {
                videoPlayer3.setVolume(volume);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public VideoPlayer getVideoPlayer() {
        return this.videoPlayer;
    }

    private void startProgressTimer(final MessageObject currentPlayingMessageObject) {
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
            String fileName = currentPlayingMessageObject.getFileName();
            Timer timer2 = new Timer();
            this.progressTimer = timer2;
            timer2.schedule(new TimerTask() {
                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new MediaController$5$$ExternalSyntheticLambda1(this, currentPlayingMessageObject));
                    }
                }

                /* renamed from: lambda$run$1$org-telegram-messenger-MediaController$5  reason: not valid java name */
                public /* synthetic */ void m756lambda$run$1$orgtelegrammessengerMediaController$5(MessageObject currentPlayingMessageObject) {
                    long progress;
                    long duration;
                    float value;
                    float value2;
                    if (!(MediaController.this.audioPlayer == null && MediaController.this.videoPlayer == null) && !MediaController.this.isPaused) {
                        try {
                            if (MediaController.this.videoPlayer != null) {
                                duration = MediaController.this.videoPlayer.getDuration();
                                progress = MediaController.this.videoPlayer.getCurrentPosition();
                                if (progress < 0) {
                                    return;
                                }
                                if (duration > 0) {
                                    value2 = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / ((float) duration);
                                    value = ((float) progress) / ((float) duration);
                                    if (value >= 1.0f) {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                duration = MediaController.this.audioPlayer.getDuration();
                                progress = MediaController.this.audioPlayer.getCurrentPosition();
                                float value3 = duration >= 0 ? ((float) progress) / ((float) duration) : 0.0f;
                                float bufferedValue = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) duration);
                                if (duration != -9223372036854775807L && progress >= 0) {
                                    if (MediaController.this.seekToProgressPending == 0.0f) {
                                        value = value3;
                                        value2 = bufferedValue;
                                    } else {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            }
                            long unused = MediaController.this.lastProgress = progress;
                            currentPlayingMessageObject.audioPlayerDuration = (int) (duration / 1000);
                            currentPlayingMessageObject.audioProgress = value;
                            currentPlayingMessageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                            currentPlayingMessageObject.bufferedProgress = value2;
                            if (value >= 0.0f && MediaController.this.shouldSavePositionForCurrentAudio != null && SystemClock.elapsedRealtime() - MediaController.this.lastSaveTime >= 1000) {
                                String saveFor = MediaController.this.shouldSavePositionForCurrentAudio;
                                long unused2 = MediaController.this.lastSaveTime = SystemClock.elapsedRealtime();
                                Utilities.globalQueue.postRunnable(new MediaController$5$$ExternalSyntheticLambda0(saveFor, value));
                            }
                            NotificationCenter.getInstance(currentPlayingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(currentPlayingMessageObject.getId()), Float.valueOf(value));
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
        for (int a = 0; a < 3; a++) {
            DownloadController.getInstance(a).cleanup();
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
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x005c, code lost:
        if (r6.toLowerCase().contains("screenshot") == false) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0068, code lost:
        if (r7.toLowerCase().contains("screenshot") != false) goto L_0x0082;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0074, code lost:
        if (r8.toLowerCase().contains("screenshot") != false) goto L_0x0082;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processMediaObserver(android.net.Uri r17) {
        /*
            r16 = this;
            r1 = r16
            r2 = 0
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ Exception -> 0x00d9 }
            r3 = r0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00d9 }
            android.content.ContentResolver r4 = r0.getContentResolver()     // Catch:{ Exception -> 0x00d9 }
            java.lang.String[] r6 = r1.mediaProjections     // Catch:{ Exception -> 0x00d9 }
            r7 = 0
            r8 = 0
            java.lang.String r9 = "date_added DESC LIMIT 1"
            r5 = r17
            android.database.Cursor r0 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x00d9 }
            r2 = r0
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x00d9 }
            r0.<init>()     // Catch:{ Exception -> 0x00d9 }
            r4 = r0
            if (r2 == 0) goto L_0x00bf
        L_0x0023:
            boolean r0 = r2.moveToNext()     // Catch:{ Exception -> 0x00d9 }
            if (r0 == 0) goto L_0x00bc
            java.lang.String r0 = ""
            r5 = r0
            r0 = 0
            java.lang.String r0 = r2.getString(r0)     // Catch:{ Exception -> 0x00d9 }
            r6 = r0
            r0 = 1
            java.lang.String r7 = r2.getString(r0)     // Catch:{ Exception -> 0x00d9 }
            r8 = 2
            java.lang.String r8 = r2.getString(r8)     // Catch:{ Exception -> 0x00d9 }
            r9 = 3
            long r9 = r2.getLong(r9)     // Catch:{ Exception -> 0x00d9 }
            r11 = 4
            java.lang.String r11 = r2.getString(r11)     // Catch:{ Exception -> 0x00d9 }
            r12 = 5
            int r12 = r2.getInt(r12)     // Catch:{ Exception -> 0x00d9 }
            r13 = 6
            int r13 = r2.getInt(r13)     // Catch:{ Exception -> 0x00d9 }
            java.lang.String r14 = "screenshot"
            if (r6 == 0) goto L_0x005e
            java.lang.String r15 = r6.toLowerCase()     // Catch:{ Exception -> 0x00d9 }
            boolean r15 = r15.contains(r14)     // Catch:{ Exception -> 0x00d9 }
            if (r15 != 0) goto L_0x0082
        L_0x005e:
            if (r7 == 0) goto L_0x006a
            java.lang.String r15 = r7.toLowerCase()     // Catch:{ Exception -> 0x00d9 }
            boolean r15 = r15.contains(r14)     // Catch:{ Exception -> 0x00d9 }
            if (r15 != 0) goto L_0x0082
        L_0x006a:
            if (r8 == 0) goto L_0x0076
            java.lang.String r15 = r8.toLowerCase()     // Catch:{ Exception -> 0x00d9 }
            boolean r15 = r15.contains(r14)     // Catch:{ Exception -> 0x00d9 }
            if (r15 != 0) goto L_0x0082
        L_0x0076:
            if (r11 == 0) goto L_0x00ba
            java.lang.String r15 = r11.toLowerCase()     // Catch:{ Exception -> 0x00d9 }
            boolean r14 = r15.contains(r14)     // Catch:{ Exception -> 0x00d9 }
            if (r14 == 0) goto L_0x00ba
        L_0x0082:
            if (r12 == 0) goto L_0x0086
            if (r13 != 0) goto L_0x0096
        L_0x0086:
            android.graphics.BitmapFactory$Options r14 = new android.graphics.BitmapFactory$Options     // Catch:{ Exception -> 0x00b2 }
            r14.<init>()     // Catch:{ Exception -> 0x00b2 }
            r14.inJustDecodeBounds = r0     // Catch:{ Exception -> 0x00b2 }
            android.graphics.BitmapFactory.decodeFile(r6, r14)     // Catch:{ Exception -> 0x00b2 }
            int r0 = r14.outWidth     // Catch:{ Exception -> 0x00b2 }
            r12 = r0
            int r0 = r14.outHeight     // Catch:{ Exception -> 0x00b2 }
            r13 = r0
        L_0x0096:
            if (r12 <= 0) goto L_0x00aa
            if (r13 <= 0) goto L_0x00aa
            int r0 = r3.x     // Catch:{ Exception -> 0x00b2 }
            if (r12 != r0) goto L_0x00a2
            int r0 = r3.y     // Catch:{ Exception -> 0x00b2 }
            if (r13 == r0) goto L_0x00aa
        L_0x00a2:
            int r0 = r3.x     // Catch:{ Exception -> 0x00b2 }
            if (r13 != r0) goto L_0x00b1
            int r0 = r3.y     // Catch:{ Exception -> 0x00b2 }
            if (r12 != r0) goto L_0x00b1
        L_0x00aa:
            java.lang.Long r0 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x00b2 }
            r4.add(r0)     // Catch:{ Exception -> 0x00b2 }
        L_0x00b1:
            goto L_0x00ba
        L_0x00b2:
            r0 = move-exception
            java.lang.Long r14 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x00d9 }
            r4.add(r14)     // Catch:{ Exception -> 0x00d9 }
        L_0x00ba:
            goto L_0x0023
        L_0x00bc:
            r2.close()     // Catch:{ Exception -> 0x00d9 }
        L_0x00bf:
            boolean r0 = r4.isEmpty()     // Catch:{ Exception -> 0x00d9 }
            if (r0 != 0) goto L_0x00cd
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda19 r0 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda19     // Catch:{ Exception -> 0x00d9 }
            r0.<init>(r1, r4)     // Catch:{ Exception -> 0x00d9 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x00d9 }
        L_0x00cd:
            if (r2 == 0) goto L_0x00d5
            r2.close()     // Catch:{ Exception -> 0x00d3 }
            goto L_0x00d5
        L_0x00d3:
            r0 = move-exception
            goto L_0x00e3
        L_0x00d5:
            goto L_0x00e3
        L_0x00d6:
            r0 = move-exception
            r3 = r0
            goto L_0x00e4
        L_0x00d9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x00d6 }
            if (r2 == 0) goto L_0x00d5
            r2.close()     // Catch:{ Exception -> 0x00d3 }
            goto L_0x00d5
        L_0x00e3:
            return
        L_0x00e4:
            if (r2 == 0) goto L_0x00ec
            r2.close()     // Catch:{ Exception -> 0x00ea }
            goto L_0x00ec
        L_0x00ea:
            r0 = move-exception
            goto L_0x00ed
        L_0x00ec:
        L_0x00ed:
            goto L_0x00ef
        L_0x00ee:
            throw r3
        L_0x00ef:
            goto L_0x00ee
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.processMediaObserver(android.net.Uri):void");
    }

    /* renamed from: lambda$processMediaObserver$6$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m99xa1e516d(ArrayList screenshotDates) {
        NotificationCenter.getInstance(this.lastChatAccount).postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
        checkScreenshots(screenshotDates);
    }

    private void checkScreenshots(ArrayList<Long> dates) {
        if (dates != null && !dates.isEmpty() && this.lastChatEnterTime != 0) {
            if (this.lastUser != null || (this.lastSecretChat instanceof TLRPC.TL_encryptedChat)) {
                boolean send = false;
                for (int a = 0; a < dates.size(); a++) {
                    Long date = dates.get(a);
                    if ((this.lastMediaCheckTime == 0 || date.longValue() > this.lastMediaCheckTime) && date.longValue() >= this.lastChatEnterTime && (this.lastChatLeaveTime == 0 || date.longValue() <= this.lastChatLeaveTime + 2000)) {
                        this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, date.longValue());
                        send = true;
                    }
                }
                if (!send) {
                    return;
                }
                if (this.lastSecretChat != null) {
                    SecretChatHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastSecretChat, this.lastChatVisibleMessages, (TLRPC.Message) null);
                } else {
                    SendMessagesHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastUser, this.lastMessageId, (TLRPC.Message) null);
                }
            }
        }
    }

    public void setLastVisibleMessageIds(int account, long enterTime, long leaveTime, TLRPC.User user, TLRPC.EncryptedChat encryptedChat, ArrayList<Long> visibleMessages, int visibleMessage) {
        this.lastChatEnterTime = enterTime;
        this.lastChatLeaveTime = leaveTime;
        this.lastChatAccount = account;
        this.lastSecretChat = encryptedChat;
        this.lastUser = user;
        this.lastMessageId = visibleMessage;
        this.lastChatVisibleMessages = visibleMessages;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int newIndex;
        int i = id;
        if (i == NotificationCenter.fileLoaded || i == NotificationCenter.httpFileDidLoad) {
            String fileName = args[0];
            MessageObject messageObject = this.playingMessageObject;
            if (messageObject == null) {
                int i2 = account;
            } else if (messageObject.currentAccount == account && FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(fileName)) {
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
            if (!args[2].booleanValue()) {
                long channelId = args[1].longValue();
                ArrayList<Integer> markAsDeletedMessages = args[0];
                MessageObject messageObject2 = this.playingMessageObject;
                if (messageObject2 != null && channelId == messageObject2.messageOwner.peer_id.channel_id && markAsDeletedMessages.contains(Integer.valueOf(this.playingMessageObject.getId()))) {
                    cleanupPlayer(true, true);
                }
                ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
                if (arrayList != null && !arrayList.isEmpty() && channelId == this.voiceMessagesPlaylist.get(0).messageOwner.peer_id.channel_id) {
                    for (int a = 0; a < markAsDeletedMessages.size(); a++) {
                        Integer key = markAsDeletedMessages.get(a);
                        MessageObject messageObject3 = this.voiceMessagesPlaylistMap.get(key.intValue());
                        this.voiceMessagesPlaylistMap.remove(key.intValue());
                        if (messageObject3 != null) {
                            this.voiceMessagesPlaylist.remove(messageObject3);
                        }
                    }
                }
                int a2 = account;
            }
        } else if (i == NotificationCenter.removeAllMessagesFromDialog) {
            long did = args[0].longValue();
            MessageObject messageObject4 = this.playingMessageObject;
            if (messageObject4 != null && messageObject4.getDialogId() == did) {
                cleanupPlayer(false, true);
            }
            int i3 = account;
        } else if (i == NotificationCenter.musicDidLoad) {
            long did2 = args[0].longValue();
            MessageObject messageObject5 = this.playingMessageObject;
            if (messageObject5 != null && messageObject5.isMusic() && this.playingMessageObject.getDialogId() == did2 && !this.playingMessageObject.scheduled) {
                this.playlist.addAll(0, args[1]);
                this.playlist.addAll(args[2]);
                int N = this.playlist.size();
                for (int a3 = 0; a3 < N; a3++) {
                    MessageObject object = this.playlist.get(a3);
                    this.playlistMap.put(Integer.valueOf(object.getId()), object);
                    int[] iArr = this.playlistMaxId;
                    iArr[0] = Math.min(iArr[0], object.getId());
                }
                sortPlaylist();
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                } else {
                    MessageObject messageObject6 = this.playingMessageObject;
                    if (messageObject6 != null && (newIndex = this.playlist.indexOf(messageObject6)) >= 0) {
                        this.currentPlaylistNum = newIndex;
                    }
                }
                this.playlistClassGuid = ConnectionsManager.generateClassGuid();
            }
            int i4 = account;
        } else if (i == NotificationCenter.mediaDidLoad) {
            if (args[3].intValue() == this.playlistClassGuid && this.playingMessageObject != null) {
                long did3 = args[0].longValue();
                int intValue = args[4].intValue();
                ArrayList<MessageObject> arr = args[2];
                boolean isEncryptedDialog = DialogObject.isEncryptedDialog(did3);
                int loadIndex = did3 == this.playlistMergeDialogId ? 1 : 0;
                if (!arr.isEmpty()) {
                    this.playlistEndReached[loadIndex] = args[5].booleanValue();
                }
                int addedCount = 0;
                for (int a4 = 0; a4 < arr.size(); a4++) {
                    MessageObject message = arr.get(a4);
                    if (!this.playlistMap.containsKey(Integer.valueOf(message.getId()))) {
                        addedCount++;
                        this.playlist.add(0, message);
                        this.playlistMap.put(Integer.valueOf(message.getId()), message);
                        int[] iArr2 = this.playlistMaxId;
                        iArr2[loadIndex] = Math.min(iArr2[loadIndex], message.getId());
                    }
                }
                sortPlaylist();
                int newIndex2 = this.playlist.indexOf(this.playingMessageObject);
                if (newIndex2 >= 0) {
                    this.currentPlaylistNum = newIndex2;
                }
                this.loadingPlaylist = false;
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                }
                if (addedCount != 0) {
                    NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.moreMusicDidLoad, Integer.valueOf(addedCount));
                }
            }
            int i5 = account;
        } else {
            if (i == NotificationCenter.didReceiveNewMessages) {
                if (!args[2].booleanValue()) {
                    ArrayList<MessageObject> arrayList2 = this.voiceMessagesPlaylist;
                    if (arrayList2 != null && !arrayList2.isEmpty() && args[0].longValue() == this.voiceMessagesPlaylist.get(0).getDialogId()) {
                        ArrayList<MessageObject> arr2 = args[1];
                        for (int a5 = 0; a5 < arr2.size(); a5++) {
                            MessageObject messageObject7 = arr2.get(a5);
                            if ((messageObject7.isVoice() || messageObject7.isRoundVideo()) && (!this.voiceMessagesPlaylistUnread || (messageObject7.isContentUnread() && !messageObject7.isOut()))) {
                                this.voiceMessagesPlaylist.add(messageObject7);
                                this.voiceMessagesPlaylistMap.put(messageObject7.getId(), messageObject7);
                            }
                        }
                    }
                } else {
                    return;
                }
            } else if (i == NotificationCenter.playerDidStartPlaying) {
                if (!getInstance().isCurrentPlayer(args[0])) {
                    getInstance().m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(getInstance().getPlayingMessageObject());
                    int i6 = account;
                    return;
                }
                int i7 = account;
                return;
            }
            int i8 = account;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isRecordingAudio() {
        return (this.recordStartRunnable == null && this.recordingAudio == null) ? false : true;
    }

    private boolean isNearToSensor(float value) {
        return value < 5.0f && value != this.proximitySensor.getMaximumRange();
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

    public void onSensorChanged(SensorEvent event) {
        PowerManager.WakeLock wakeLock;
        PowerManager.WakeLock wakeLock2;
        PowerManager.WakeLock wakeLock3;
        int i;
        PowerManager.WakeLock wakeLock4;
        boolean z;
        PowerManager.WakeLock wakeLock5;
        int sign;
        boolean goodValue;
        double alpha;
        SensorEvent sensorEvent = event;
        if (this.sensorsStarted && VoIPService.getSharedInstance() == null) {
            if (sensorEvent.sensor == this.proximitySensor) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("proximity changed to " + sensorEvent.values[0] + " max value = " + this.proximitySensor.getMaximumRange());
                }
                float f = this.lastProximityValue;
                if (f == -100.0f) {
                    this.lastProximityValue = sensorEvent.values[0];
                } else if (f != sensorEvent.values[0]) {
                    this.proximityHasDifferentValues = true;
                }
                if (this.proximityHasDifferentValues) {
                    this.proximityTouched = isNearToSensor(sensorEvent.values[0]);
                }
            } else if (sensorEvent.sensor == this.accelerometerSensor) {
                if (this.lastTimestamp == 0) {
                    alpha = 0.9800000190734863d;
                } else {
                    double d = (double) (sensorEvent.timestamp - this.lastTimestamp);
                    Double.isNaN(d);
                    alpha = 1.0d / ((d / 1.0E9d) + 1.0d);
                }
                this.lastTimestamp = sensorEvent.timestamp;
                float[] fArr = this.gravity;
                double d2 = (double) fArr[0];
                Double.isNaN(d2);
                double d3 = (double) sensorEvent.values[0];
                Double.isNaN(d3);
                fArr[0] = (float) ((d2 * alpha) + ((1.0d - alpha) * d3));
                float[] fArr2 = this.gravity;
                double d4 = (double) fArr2[1];
                Double.isNaN(d4);
                double d5 = (double) sensorEvent.values[1];
                Double.isNaN(d5);
                fArr2[1] = (float) ((d4 * alpha) + ((1.0d - alpha) * d5));
                float[] fArr3 = this.gravity;
                double d6 = (double) fArr3[2];
                Double.isNaN(d6);
                double d7 = (double) sensorEvent.values[2];
                Double.isNaN(d7);
                fArr3[2] = (float) ((d6 * alpha) + ((1.0d - alpha) * d7));
                this.gravityFast[0] = (this.gravity[0] * 0.8f) + (sensorEvent.values[0] * 0.19999999f);
                this.gravityFast[1] = (this.gravity[1] * 0.8f) + (sensorEvent.values[1] * 0.19999999f);
                this.gravityFast[2] = (this.gravity[2] * 0.8f) + (sensorEvent.values[2] * 0.19999999f);
                this.linearAcceleration[0] = sensorEvent.values[0] - this.gravity[0];
                this.linearAcceleration[1] = sensorEvent.values[1] - this.gravity[1];
                this.linearAcceleration[2] = sensorEvent.values[2] - this.gravity[2];
            } else if (sensorEvent.sensor == this.linearSensor) {
                this.linearAcceleration[0] = sensorEvent.values[0];
                this.linearAcceleration[1] = sensorEvent.values[1];
                this.linearAcceleration[2] = sensorEvent.values[2];
            } else if (sensorEvent.sensor == this.gravitySensor) {
                float[] fArr4 = this.gravityFast;
                float[] fArr5 = this.gravity;
                float f2 = sensorEvent.values[0];
                fArr5[0] = f2;
                fArr4[0] = f2;
                float[] fArr6 = this.gravityFast;
                float[] fArr7 = this.gravity;
                float f3 = sensorEvent.values[1];
                fArr7[1] = f3;
                fArr6[1] = f3;
                float[] fArr8 = this.gravityFast;
                float[] fArr9 = this.gravity;
                float f4 = sensorEvent.values[2];
                fArr9[2] = f4;
                fArr8[2] = f4;
            }
            if (sensorEvent.sensor == this.linearSensor || sensorEvent.sensor == this.gravitySensor || sensorEvent.sensor == this.accelerometerSensor) {
                float[] fArr10 = this.gravity;
                float f5 = fArr10[0];
                float[] fArr11 = this.linearAcceleration;
                float val = (f5 * fArr11[0]) + (fArr10[1] * fArr11[1]) + (fArr10[2] * fArr11[2]);
                int i2 = this.raisedToBack;
                if (i2 != 6 && ((val > 0.0f && this.previousAccValue > 0.0f) || (val < 0.0f && this.previousAccValue < 0.0f))) {
                    if (val > 0.0f) {
                        goodValue = val > 15.0f;
                        sign = 1;
                    } else {
                        goodValue = val < -15.0f;
                        sign = 2;
                    }
                    int i3 = this.raisedToTopSign;
                    if (i3 != 0 && i3 != sign) {
                        int i4 = this.raisedToTop;
                        if (i4 != 6 || !goodValue) {
                            if (!goodValue) {
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
                    } else if (goodValue && i2 == 0 && (i3 == 0 || i3 == sign)) {
                        int i6 = this.raisedToTop;
                        if (i6 < 6 && !this.proximityTouched) {
                            this.raisedToTopSign = sign;
                            int i7 = i6 + 1;
                            this.raisedToTop = i7;
                            if (i7 == 6) {
                                this.countLess = 0;
                            }
                        }
                    } else {
                        if (!goodValue) {
                            this.countLess++;
                        }
                        if (!(i3 == sign && this.countLess != 10 && this.raisedToTop == 6 && i2 == 0)) {
                            this.raisedToBack = 0;
                            this.raisedToTop = 0;
                            this.raisedToTopSign = 0;
                            this.countLess = 0;
                        }
                    }
                }
                this.previousAccValue = val;
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
                    if (messageObject == null) {
                        i = 0;
                    } else if (!messageObject.isVoice() && !this.playingMessageObject.isRoundVideo()) {
                        i = 0;
                    } else if (!this.useFrontSpeaker) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start listen");
                        }
                        if (this.proximityHasDifferentValues && (wakeLock4 = this.proximityWakeLock) != null && !wakeLock4.isHeld()) {
                            this.proximityWakeLock.acquire();
                        }
                        setUseFrontSpeaker(true);
                        i = 0;
                        startAudioAgain(false);
                        this.ignoreOnPause = true;
                    } else {
                        i = 0;
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
                        z = true;
                        setUseFrontSpeaker(true);
                    } else {
                        z = true;
                    }
                    this.ignoreOnPause = z;
                    if (!this.proximityHasDifferentValues || (wakeLock5 = this.proximityWakeLock) == null || wakeLock5.isHeld()) {
                        i = 0;
                    } else {
                        this.proximityWakeLock.acquire();
                        i = 0;
                    }
                } else {
                    i = 0;
                }
                this.raisedToBack = i;
                this.raisedToTop = i;
                this.raisedToTopSign = i;
                this.countLess = i;
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
        if (this.useFrontSpeaker && this.raiseChat != null && this.allowStartRecord && SharedConfig.raiseToSpeak) {
            this.raiseToEarRecord = true;
            startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), (MessageObject) null, this.raiseChat.getThreadMessage(), this.raiseChat.getClassGuid());
            this.ignoreOnPause = true;
        }
    }

    private void startAudioAgain(boolean paused) {
        VideoPlayer videoPlayer2;
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null) {
            int i = 0;
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.audioRouteChanged, Boolean.valueOf(this.useFrontSpeaker));
            VideoPlayer videoPlayer3 = this.videoPlayer;
            if (videoPlayer3 != null) {
                if (!this.useFrontSpeaker) {
                    i = 3;
                }
                videoPlayer3.setStreamType(i);
                if (!paused) {
                    if (this.videoPlayer.getCurrentPosition() < 1000) {
                        this.videoPlayer.seekTo(0);
                    }
                    this.videoPlayer.play();
                    return;
                }
                m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.playingMessageObject);
                return;
            }
            boolean post = this.audioPlayer != null;
            MessageObject currentMessageObject = this.playingMessageObject;
            float progress = this.playingMessageObject.audioProgress;
            int duration = this.playingMessageObject.audioPlayerDuration;
            if (paused || (videoPlayer2 = this.audioPlayer) == null || !videoPlayer2.isPlaying() || ((float) duration) * progress > 1.0f) {
                currentMessageObject.audioProgress = progress;
            } else {
                currentMessageObject.audioProgress = 0.0f;
            }
            cleanupPlayer(false, true);
            playMessage(currentMessageObject);
            if (!paused) {
                return;
            }
            if (post) {
                AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda20(this, currentMessageObject), 100);
            } else {
                m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(currentMessageObject);
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
                Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda4(this));
                this.sensorsStarted = true;
            }
        }
    }

    /* renamed from: lambda$startRaiseToEarSensors$8$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m103xfb47254e() {
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

    public void stopRaiseToEarSensors(ChatActivity chatActivity, boolean fromChat) {
        PowerManager.WakeLock wakeLock;
        if (this.ignoreOnPause) {
            this.ignoreOnPause = false;
            return;
        }
        stopRecording(fromChat ? 2 : 0, false, 0);
        if (this.sensorsStarted && !this.ignoreOnPause) {
            if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null && this.raiseChat == chatActivity) {
                this.raiseChat = null;
                this.sensorsStarted = false;
                this.accelerometerVertical = false;
                this.proximityTouched = false;
                this.raiseToEarRecord = false;
                this.useFrontSpeaker = false;
                Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda5(this));
                if (this.proximityHasDifferentValues && (wakeLock = this.proximityWakeLock) != null && wakeLock.isHeld()) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    /* renamed from: lambda$stopRaiseToEarSensors$9$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m109xc4b35327() {
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

    public void cleanupPlayer(boolean notify, boolean stopService) {
        cleanupPlayer(notify, stopService, false, false);
    }

    public void cleanupPlayer(boolean notify, boolean stopService, boolean byVoiceEnd, boolean transferPlayerToPhotoViewer) {
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
                final VideoPlayer playerFinal = this.audioPlayer;
                ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(new float[]{this.audioVolume, 0.0f});
                valueAnimator2.addUpdateListener(new MediaController$$ExternalSyntheticLambda10(this, playerFinal));
                valueAnimator2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        try {
                            playerFinal.releasePlayer(true);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                valueAnimator2.setDuration(300);
                valueAnimator2.start();
            }
            this.audioPlayer = null;
            Theme.unrefAudioVisualizeDrawable(this.playingMessageObject);
        } else {
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                this.currentAspectRatioFrameLayout = null;
                this.currentTextureViewContainer = null;
                this.currentAspectRatioFrameLayoutReady = false;
                this.isDrawingWasReady = false;
                this.currentTextureView = null;
                this.goingToShowMessageObject = null;
                if (transferPlayerToPhotoViewer) {
                    PhotoViewer.getInstance().injectVideoPlayer(this.videoPlayer);
                    MessageObject messageObject2 = this.playingMessageObject;
                    this.goingToShowMessageObject = messageObject2;
                    NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, true);
                } else {
                    long position = videoPlayer2.getCurrentPosition();
                    MessageObject messageObject3 = this.playingMessageObject;
                    if (messageObject3 != null && messageObject3.isVideo() && position > 0) {
                        this.playingMessageObject.audioProgressMs = (int) position;
                        NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, false);
                    }
                    this.videoPlayer.releasePlayer(true);
                    this.videoPlayer = null;
                }
                try {
                    this.baseActivity.getWindow().clearFlags(128);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                if (this.playingMessageObject != null && !transferPlayerToPhotoViewer) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
            }
        }
        stopProgressTimer();
        this.lastProgress = 0;
        this.isPaused = false;
        if (!this.useFrontSpeaker && !SharedConfig.raiseToSpeak) {
            ChatActivity chat = this.raiseChat;
            stopRaiseToEarSensors(this.raiseChat, false);
            this.raiseChat = chat;
        }
        PowerManager.WakeLock wakeLock = this.proximityWakeLock;
        if (wakeLock != null && wakeLock.isHeld() && !this.proximityTouched) {
            this.proximityWakeLock.release();
        }
        MessageObject messageObject4 = this.playingMessageObject;
        if (messageObject4 != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance(messageObject4.currentAccount).cancelLoadFile(this.playingMessageObject.getDocument());
            }
            MessageObject lastFile = this.playingMessageObject;
            if (notify) {
                this.playingMessageObject.resetPlayingProgress();
                NotificationCenter.getInstance(lastFile.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), 0);
            }
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (notify) {
                NotificationsController.audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                int index = -1;
                ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
                if (arrayList != null) {
                    if (byVoiceEnd) {
                        int indexOf = arrayList.indexOf(lastFile);
                        index = indexOf;
                        if (indexOf >= 0) {
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
                ArrayList<MessageObject> arrayList2 = this.voiceMessagesPlaylist;
                if (arrayList2 == null || index >= arrayList2.size()) {
                    if ((lastFile.isVoice() || lastFile.isRoundVideo()) && lastFile.getId() != 0) {
                        startRecordingIfFromSpeaker();
                    }
                    NotificationCenter.getInstance(lastFile.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(lastFile.getId()), Boolean.valueOf(stopService));
                    this.pipSwitchingState = 0;
                    PipRoundVideoView pipRoundVideoView3 = this.pipRoundVideoView;
                    if (pipRoundVideoView3 != null) {
                        pipRoundVideoView3.close(true);
                        this.pipRoundVideoView = null;
                    }
                } else {
                    MessageObject nextVoiceMessage = this.voiceMessagesPlaylist.get(index);
                    playMessage(nextVoiceMessage);
                    if (!nextVoiceMessage.isRoundVideo() && (pipRoundVideoView2 = this.pipRoundVideoView) != null) {
                        pipRoundVideoView2.close(true);
                        this.pipRoundVideoView = null;
                    }
                }
            }
            if (stopService) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
        }
    }

    /* renamed from: lambda$cleanupPlayer$10$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m85lambda$cleanupPlayer$10$orgtelegrammessengerMediaController(VideoPlayer playerFinal, ValueAnimator valueAnimator1) {
        float volume;
        if (this.audioFocus != 1) {
            volume = 1.0f;
        } else {
            volume = 0.2f;
        }
        playerFinal.setVolume(((Float) valueAnimator1.getAnimatedValue()).floatValue() * volume);
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

    public boolean seekToProgress(MessageObject messageObject, float progress) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        try {
            VideoPlayer videoPlayer2 = this.audioPlayer;
            if (videoPlayer2 != null) {
                long duration = videoPlayer2.getDuration();
                if (duration == -9223372036854775807L) {
                    this.seekToProgressPending = progress;
                } else {
                    this.playingMessageObject.audioProgress = progress;
                    int seekTo = (int) (((float) duration) * progress);
                    this.audioPlayer.seekTo((long) seekTo);
                    this.lastProgress = (long) seekTo;
                }
            } else {
                VideoPlayer videoPlayer3 = this.videoPlayer;
                if (videoPlayer3 != null) {
                    videoPlayer3.seekTo((long) (((float) videoPlayer3.getDuration()) * progress));
                }
            }
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidSeek, Integer.valueOf(this.playingMessageObject.getId()), Float.valueOf(progress));
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
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
            ArrayList<MessageObject> all = new ArrayList<>(this.playlist);
            this.shuffledPlaylist.clear();
            MessageObject messageObject = this.playlist.get(this.currentPlaylistNum);
            all.remove(this.currentPlaylistNum);
            int count = all.size();
            for (int a = 0; a < count; a++) {
                int index = Utilities.random.nextInt(all.size());
                this.shuffledPlaylist.add(all.get(index));
                all.remove(index);
            }
            this.shuffledPlaylist.add(messageObject);
            this.currentPlaylistNum = this.shuffledPlaylist.size() - 1;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_search} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadMoreMusic() {
        /*
            r14 = this;
            boolean r0 = r14.loadingPlaylist
            if (r0 != 0) goto L_0x01a5
            org.telegram.messenger.MessageObject r0 = r14.playingMessageObject
            if (r0 == 0) goto L_0x01a5
            boolean r0 = r0.scheduled
            if (r0 != 0) goto L_0x01a5
            org.telegram.messenger.MessageObject r0 = r14.playingMessageObject
            long r0 = r0.getDialogId()
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r0)
            if (r0 != 0) goto L_0x01a5
            int r0 = r14.playlistClassGuid
            if (r0 != 0) goto L_0x001e
            goto L_0x01a5
        L_0x001e:
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r0 = r14.playlistGlobalSearchParams
            r1 = 0
            r2 = 1
            r3 = 0
            if (r0 == 0) goto L_0x0151
            int r5 = r14.playlistClassGuid
            boolean r0 = r0.endReached
            if (r0 != 0) goto L_0x0150
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r14.playlist
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0150
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r14.playlist
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r0 = r0.currentAccount
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r1 = r14.playlistGlobalSearchParams
            long r6 = r1.dialogId
            r1 = 20
            r8 = 1000(0x3e8, double:4.94E-321)
            int r10 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r10 == 0) goto L_0x00b3
            org.telegram.tgnet.TLRPC$TL_messages_search r6 = new org.telegram.tgnet.TLRPC$TL_messages_search
            r6.<init>()
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            java.lang.String r7 = r7.query
            r6.q = r7
            r6.limit = r1
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r1 = r14.playlistGlobalSearchParams
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r1 = r1.filter
            if (r1 != 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty
            r1.<init>()
            goto L_0x0069
        L_0x0063:
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r1 = r14.playlistGlobalSearchParams
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r1 = r1.filter
            org.telegram.tgnet.TLRPC$MessagesFilter r1 = r1.filter
        L_0x0069:
            r6.filter = r1
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            long r10 = r7.dialogId
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer((long) r10)
            r6.peer = r1
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r14.playlist
            int r7 = r1.size()
            int r7 = r7 - r2
            java.lang.Object r1 = r1.get(r7)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r7 = r1.getId()
            r6.offset_id = r7
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            long r10 = r7.minDate
            int r7 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r7 <= 0) goto L_0x00a0
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            long r10 = r7.minDate
            long r10 = r10 / r8
            int r7 = (int) r10
            r6.min_date = r7
        L_0x00a0:
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            long r10 = r7.maxDate
            int r7 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r7 <= 0) goto L_0x00b0
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r3 = r14.playlistGlobalSearchParams
            long r3 = r3.maxDate
            long r3 = r3 / r8
            int r4 = (int) r3
            r6.min_date = r4
        L_0x00b0:
            r1 = r6
            goto L_0x0142
        L_0x00b3:
            org.telegram.tgnet.TLRPC$TL_messages_searchGlobal r6 = new org.telegram.tgnet.TLRPC$TL_messages_searchGlobal
            r6.<init>()
            r6.limit = r1
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r1 = r14.playlistGlobalSearchParams
            java.lang.String r1 = r1.query
            r6.q = r1
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r1 = r14.playlistGlobalSearchParams
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r1 = r1.filter
            org.telegram.tgnet.TLRPC$MessagesFilter r1 = r1.filter
            r6.filter = r1
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r14.playlist
            int r7 = r1.size()
            int r7 = r7 - r2
            java.lang.Object r1 = r1.get(r7)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r7 = r1.getId()
            r6.offset_id = r7
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            int r7 = r7.nextSearchRate
            r6.offset_rate = r7
            int r7 = r6.flags
            r7 = r7 | r2
            r6.flags = r7
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            int r7 = r7.folderId
            r6.folder_id = r7
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r10 = r7.channel_id
            int r7 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x00fe
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r10 = r7.channel_id
            long r10 = -r10
            goto L_0x0116
        L_0x00fe:
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r10 = r7.chat_id
            int r7 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x0110
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r10 = r7.chat_id
            long r10 = -r10
            goto L_0x0116
        L_0x0110:
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r10 = r7.user_id
        L_0x0116:
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$InputPeer r7 = r7.getInputPeer((long) r10)
            r6.offset_peer = r7
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            long r12 = r7.minDate
            int r7 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r7 <= 0) goto L_0x0130
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            long r12 = r7.minDate
            long r12 = r12 / r8
            int r7 = (int) r12
            r6.min_date = r7
        L_0x0130:
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r7 = r14.playlistGlobalSearchParams
            long r12 = r7.maxDate
            int r7 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r7 <= 0) goto L_0x0140
            org.telegram.messenger.MediaController$PlaylistGlobalSearchParams r3 = r14.playlistGlobalSearchParams
            long r3 = r3.maxDate
            long r3 = r3 / r8
            int r4 = (int) r3
            r6.min_date = r4
        L_0x0140:
            r3 = r6
            r1 = r3
        L_0x0142:
            r14.loadingPlaylist = r2
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda36 r3 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda36
            r3.<init>(r14, r5, r0)
            r2.sendRequest(r1, r3)
        L_0x0150:
            return
        L_0x0151:
            boolean[] r0 = r14.playlistEndReached
            boolean r5 = r0[r1]
            if (r5 != 0) goto L_0x017b
            r14.loadingPlaylist = r2
            org.telegram.messenger.MessageObject r0 = r14.playingMessageObject
            int r0 = r0.currentAccount
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.MediaDataController r2 = r0.getMediaDataController()
            org.telegram.messenger.MessageObject r0 = r14.playingMessageObject
            long r3 = r0.getDialogId()
            r5 = 50
            int[] r0 = r14.playlistMaxId
            r6 = r0[r1]
            r7 = 0
            r8 = 4
            r9 = 1
            int r10 = r14.playlistClassGuid
            r11 = 0
            r2.loadMedia(r3, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x01a4
        L_0x017b:
            long r5 = r14.playlistMergeDialogId
            int r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x01a4
            boolean r0 = r0[r2]
            if (r0 != 0) goto L_0x01a4
            r14.loadingPlaylist = r2
            org.telegram.messenger.MessageObject r0 = r14.playingMessageObject
            int r0 = r0.currentAccount
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.MediaDataController r2 = r0.getMediaDataController()
            long r3 = r14.playlistMergeDialogId
            r5 = 50
            int[] r0 = r14.playlistMaxId
            r6 = r0[r1]
            r7 = 0
            r8 = 4
            r9 = 1
            int r10 = r14.playlistClassGuid
            r11 = 0
            r2.loadMedia(r3, r5, r6, r7, r8, r9, r10, r11)
        L_0x01a4:
            return
        L_0x01a5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.loadMoreMusic():void");
    }

    /* renamed from: lambda$loadMoreMusic$12$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m90lambda$loadMoreMusic$12$orgtelegrammessengerMediaController(int finalPlaylistGuid, int currentAccount, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda14(this, finalPlaylistGuid, error, response, currentAccount));
    }

    /* renamed from: lambda$loadMoreMusic$11$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m89lambda$loadMoreMusic$11$orgtelegrammessengerMediaController(int finalPlaylistGuid, TLRPC.TL_error error, TLObject response, int currentAccount) {
        PlaylistGlobalSearchParams playlistGlobalSearchParams2;
        if (this.playlistClassGuid == finalPlaylistGuid && (playlistGlobalSearchParams2 = this.playlistGlobalSearchParams) != null && this.playingMessageObject != null && error == null) {
            this.loadingPlaylist = false;
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            playlistGlobalSearchParams2.nextSearchRate = res.next_rate;
            MessagesStorage.getInstance(currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            MessagesController.getInstance(currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(currentAccount).putChats(res.chats, false);
            int n = res.messages.size();
            int addedCount = 0;
            for (int i = 0; i < n; i++) {
                MessageObject messageObject = new MessageObject(currentAccount, res.messages.get(i), false, true);
                if (!this.playlistMap.containsKey(Integer.valueOf(messageObject.getId()))) {
                    this.playlist.add(0, messageObject);
                    this.playlistMap.put(Integer.valueOf(messageObject.getId()), messageObject);
                    addedCount++;
                }
            }
            sortPlaylist();
            this.loadingPlaylist = false;
            this.playlistGlobalSearchParams.endReached = this.playlist.size() == this.playlistGlobalSearchParams.totalCount;
            if (SharedConfig.shuffleMusic) {
                buildShuffledPlayList();
            }
            if (addedCount != 0) {
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.moreMusicDidLoad, Integer.valueOf(addedCount));
            }
        }
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current, long mergeDialogId, PlaylistGlobalSearchParams globalSearchParams) {
        return setPlaylist(messageObjects, current, mergeDialogId, true, globalSearchParams);
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current, long mergeDialogId) {
        return setPlaylist(messageObjects, current, mergeDialogId, true, (PlaylistGlobalSearchParams) null);
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current, long mergeDialogId, boolean loadMusic, PlaylistGlobalSearchParams params) {
        ArrayList<MessageObject> arrayList = messageObjects;
        MessageObject messageObject = current;
        if (this.playingMessageObject == messageObject) {
            int newIdx = this.playlist.indexOf(messageObject);
            if (newIdx >= 0) {
                this.currentPlaylistNum = newIdx;
            }
            return playMessage(messageObject);
        }
        this.forceLoopCurrentPlaylist = !loadMusic;
        this.playlistMergeDialogId = mergeDialogId;
        this.playMusicAgain = !this.playlist.isEmpty();
        clearPlaylist();
        this.playlistGlobalSearchParams = params;
        boolean z = false;
        if (!messageObjects.isEmpty() && DialogObject.isEncryptedDialog(arrayList.get(0).getDialogId())) {
            z = true;
        }
        boolean isSecretChat = z;
        int minId = Integer.MAX_VALUE;
        int maxId = Integer.MIN_VALUE;
        for (int a = messageObjects.size() - 1; a >= 0; a--) {
            MessageObject messageObject2 = arrayList.get(a);
            if (messageObject2.isMusic()) {
                int id = messageObject2.getId();
                if (id > 0 || isSecretChat) {
                    minId = Math.min(minId, id);
                    maxId = Math.max(maxId, id);
                }
                this.playlist.add(messageObject2);
                this.playlistMap.put(Integer.valueOf(id), messageObject2);
            }
        }
        sortPlaylist();
        int indexOf = this.playlist.indexOf(messageObject);
        this.currentPlaylistNum = indexOf;
        if (indexOf == -1) {
            clearPlaylist();
            this.currentPlaylistNum = this.playlist.size();
            this.playlist.add(messageObject);
            this.playlistMap.put(Integer.valueOf(current.getId()), messageObject);
        }
        if (current.isMusic() && !messageObject.scheduled) {
            if (SharedConfig.shuffleMusic) {
                buildShuffledPlayList();
            }
            if (loadMusic) {
                if (this.playlistGlobalSearchParams == null) {
                    MediaDataController.getInstance(messageObject.currentAccount).loadMusic(current.getDialogId(), (long) minId, (long) maxId);
                } else {
                    this.playlistClassGuid = ConnectionsManager.generateClassGuid();
                }
            }
        }
        return playMessage(messageObject);
    }

    private void sortPlaylist() {
        Collections.sort(this.playlist, MediaController$$ExternalSyntheticLambda35.INSTANCE);
    }

    static /* synthetic */ int lambda$sortPlaylist$13(MessageObject o1, MessageObject o2) {
        int mid1 = o1.getId();
        int mid2 = o2.getId();
        long group1 = o1.messageOwner.grouped_id;
        long group2 = o2.messageOwner.grouped_id;
        if (mid1 >= 0 || mid2 >= 0) {
            if (group1 == 0 || group1 != group2) {
                return zzby$$ExternalSyntheticBackport0.m(mid1, mid2);
            }
            return zzby$$ExternalSyntheticBackport0.m(mid2, mid1);
        } else if (group1 == 0 || group1 != group2) {
            return zzby$$ExternalSyntheticBackport0.m(mid2, mid1);
        } else {
            return zzby$$ExternalSyntheticBackport0.m(mid1, mid2);
        }
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
        int i = this.currentPlaylistNum;
        if (i >= 0 && i < this.playlist.size()) {
            this.currentPlaylistNum = index;
            this.playMusicAgain = true;
            MessageObject messageObject = this.playlist.get(index);
            if (this.playingMessageObject != null && !isSamePlayingMessage(messageObject)) {
                this.playingMessageObject.resetPlayingProgress();
            }
            playMessage(messageObject);
        }
    }

    /* access modifiers changed from: private */
    public void playNextMessageWithoutOrder(boolean byStop) {
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!byStop || (!(SharedConfig.repeatMode == 2 || (SharedConfig.repeatMode == 1 && currentPlayList.size() == 1)) || this.forceLoopCurrentPlaylist)) {
            boolean last = false;
            if (SharedConfig.playOrderReversed) {
                int i = this.currentPlaylistNum + 1;
                this.currentPlaylistNum = i;
                if (i >= currentPlayList.size()) {
                    this.currentPlaylistNum = 0;
                    last = true;
                }
            } else {
                int i2 = this.currentPlaylistNum - 1;
                this.currentPlaylistNum = i2;
                if (i2 < 0) {
                    this.currentPlaylistNum = currentPlayList.size() - 1;
                    last = true;
                }
            }
            if (!last || !byStop || SharedConfig.repeatMode != 0 || this.forceLoopCurrentPlaylist) {
                int i3 = this.currentPlaylistNum;
                if (i3 >= 0 && i3 < currentPlayList.size()) {
                    MessageObject messageObject = this.playingMessageObject;
                    if (messageObject != null) {
                        messageObject.resetPlayingProgress();
                    }
                    this.playMusicAgain = true;
                    playMessage(currentPlayList.get(this.currentPlaylistNum));
                    return;
                }
                return;
            }
            VideoPlayer videoPlayer2 = this.audioPlayer;
            if (videoPlayer2 != null || this.videoPlayer != null) {
                if (videoPlayer2 != null) {
                    try {
                        videoPlayer2.releasePlayer(true);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    this.audioPlayer = null;
                    Theme.unrefAudioVisualizeDrawable(this.playingMessageObject);
                } else {
                    this.currentAspectRatioFrameLayout = null;
                    this.currentTextureViewContainer = null;
                    this.currentAspectRatioFrameLayoutReady = false;
                    this.currentTextureView = null;
                    this.videoPlayer.releasePlayer(true);
                    this.videoPlayer = null;
                    try {
                        this.baseActivity.getWindow().clearFlags(128);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
                stopProgressTimer();
                this.lastProgress = 0;
                this.isPaused = true;
                this.playingMessageObject.audioProgress = 0.0f;
                this.playingMessageObject.audioProgressSec = 0;
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), 0);
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
                return;
            }
            return;
        }
        cleanupPlayer(false, false);
        MessageObject messageObject2 = currentPlayList.get(this.currentPlaylistNum);
        messageObject2.audioProgress = 0.0f;
        messageObject2.audioProgressSec = 0;
        playMessage(messageObject2);
    }

    public void playPreviousMessage() {
        int i;
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!currentPlayList.isEmpty() && (i = this.currentPlaylistNum) >= 0 && i < currentPlayList.size()) {
            MessageObject currentSong = currentPlayList.get(this.currentPlaylistNum);
            if (currentSong.audioProgressSec > 10) {
                seekToProgress(currentSong, 0.0f);
                return;
            }
            if (SharedConfig.playOrderReversed) {
                int i2 = this.currentPlaylistNum - 1;
                this.currentPlaylistNum = i2;
                if (i2 < 0) {
                    this.currentPlaylistNum = currentPlayList.size() - 1;
                }
            } else {
                int i3 = this.currentPlaylistNum + 1;
                this.currentPlaylistNum = i3;
                if (i3 >= currentPlayList.size()) {
                    this.currentPlaylistNum = 0;
                }
            }
            if (this.currentPlaylistNum < currentPlayList.size()) {
                this.playMusicAgain = true;
                playMessage(currentPlayList.get(this.currentPlaylistNum));
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

    private void checkIsNextVoiceFileDownloaded(int currentAccount) {
        ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
        if (arrayList != null && arrayList.size() >= 2) {
            MessageObject nextAudio = this.voiceMessagesPlaylist.get(1);
            File file = null;
            if (nextAudio.messageOwner.attachPath != null && nextAudio.messageOwner.attachPath.length() > 0) {
                file = new File(nextAudio.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            File cacheFile = file != null ? file : FileLoader.getPathToMessage(nextAudio.messageOwner);
            boolean exists = cacheFile.exists();
            if (cacheFile != file && !cacheFile.exists()) {
                FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
            }
        }
    }

    private void checkIsNextMusicFileDownloaded(int currentAccount) {
        int nextIndex;
        if (DownloadController.getInstance(currentAccount).canDownloadNextTrack()) {
            ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
            if (currentPlayList != null && currentPlayList.size() >= 2) {
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
                    MessageObject nextAudio = currentPlayList.get(nextIndex);
                    File file = null;
                    if (!TextUtils.isEmpty(nextAudio.messageOwner.attachPath)) {
                        file = new File(nextAudio.messageOwner.attachPath);
                        if (!file.exists()) {
                            file = null;
                        }
                    }
                    File cacheFile = file != null ? file : FileLoader.getPathToMessage(nextAudio.messageOwner);
                    boolean exists = cacheFile.exists();
                    if (cacheFile != file && !cacheFile.exists() && nextAudio.isMusic()) {
                        FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
                    }
                }
            }
        }
    }

    public void setVoiceMessagesPlaylist(ArrayList<MessageObject> playlist2, boolean unread) {
        this.voiceMessagesPlaylist = playlist2;
        if (playlist2 != null) {
            this.voiceMessagesPlaylistUnread = unread;
            this.voiceMessagesPlaylistMap = new SparseArray<>();
            for (int a = 0; a < this.voiceMessagesPlaylist.size(); a++) {
                MessageObject messageObject = this.voiceMessagesPlaylist.get(a);
                this.voiceMessagesPlaylistMap.put(messageObject.getId(), messageObject);
            }
        }
    }

    private void checkAudioFocus(MessageObject messageObject) {
        int neededAudioFocus;
        int result;
        if (!messageObject.isVoice() && !messageObject.isRoundVideo()) {
            neededAudioFocus = 1;
        } else if (this.useFrontSpeaker != 0) {
            neededAudioFocus = 3;
        } else {
            neededAudioFocus = 2;
        }
        if (this.hasAudioFocus != neededAudioFocus) {
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
        AspectRatioFrameLayout aspectRatioFrameLayout = this.currentAspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            if (visible) {
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
                        pipRoundVideoView3.show(this.baseActivity, new MediaController$$ExternalSyntheticLambda3(this));
                    } catch (Exception e) {
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

    /* renamed from: lambda$setCurrentVideoVisible$14$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m100xefb5270b() {
        cleanupPlayer(true, true);
    }

    public void setTextureView(TextureView textureView, AspectRatioFrameLayout aspectRatioFrameLayout, FrameLayout container, boolean set) {
        if (textureView != null) {
            boolean z = true;
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
                PipRoundVideoView pipRoundVideoView2 = this.pipRoundVideoView;
                if (pipRoundVideoView2 != null) {
                    this.videoPlayer.setTextureView(pipRoundVideoView2.getTextureView());
                } else {
                    this.videoPlayer.setTextureView(textureView);
                }
                this.currentAspectRatioFrameLayout = aspectRatioFrameLayout;
                this.currentTextureViewContainer = container;
                if (this.currentAspectRatioFrameLayoutReady && aspectRatioFrameLayout != null) {
                    aspectRatioFrameLayout.setAspectRatio(this.currentAspectRatioFrameLayoutRatio, this.currentAspectRatioFrameLayoutRotation);
                }
            }
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

    public void setPlaybackSpeed(boolean music, float speed) {
        if (music) {
            if (this.currentMusicPlaybackSpeed >= 6.0f && speed == 1.0f && this.playingMessageObject != null) {
                this.audioPlayer.pause();
                AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda22(this, this.playingMessageObject, this.playingMessageObject.audioProgress), 50);
            }
            this.currentMusicPlaybackSpeed = speed;
            if (Math.abs(speed - 1.0f) > 0.001f) {
                this.fastMusicPlaybackSpeed = speed;
            }
        } else {
            this.currentPlaybackSpeed = speed;
            if (Math.abs(speed - 1.0f) > 0.001f) {
                this.fastPlaybackSpeed = speed;
            }
        }
        VideoPlayer videoPlayer2 = this.audioPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.setPlaybackSpeed(speed);
        } else {
            VideoPlayer videoPlayer3 = this.videoPlayer;
            if (videoPlayer3 != null) {
                videoPlayer3.setPlaybackSpeed(speed);
            }
        }
        MessagesController.getGlobalMainSettings().edit().putFloat(music ? "musicPlaybackSpeed" : "playbackSpeed", speed).putFloat(music ? "fastMusicPlaybackSpeed" : "fastPlaybackSpeed", music ? this.fastMusicPlaybackSpeed : this.fastPlaybackSpeed).commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.messagePlayingSpeedChanged, new Object[0]);
    }

    /* renamed from: lambda$setPlaybackSpeed$15$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m101x93032886(MessageObject currentMessage, float p) {
        if (this.audioPlayer != null && this.playingMessageObject != null && !this.isPaused) {
            if (isSamePlayingMessage(currentMessage)) {
                seekToProgress(this.playingMessageObject, p);
            }
            this.audioPlayer.play();
        }
    }

    public float getPlaybackSpeed(boolean music) {
        return music ? this.currentMusicPlaybackSpeed : this.currentPlaybackSpeed;
    }

    public float getFastPlaybackSpeed(boolean music) {
        return music ? this.fastMusicPlaybackSpeed : this.fastPlaybackSpeed;
    }

    /* access modifiers changed from: private */
    public void updateVideoState(MessageObject messageObject, int[] playCount, boolean destroyAtEnd, boolean playWhenReady, int playbackState) {
        MessageObject messageObject2;
        if (this.videoPlayer != null) {
            if (playbackState == 4 || playbackState == 1) {
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
            if (playbackState == 3) {
                this.playerWasReady = true;
                MessageObject messageObject3 = this.playingMessageObject;
                if (messageObject3 != null && (messageObject3.isVideo() || this.playingMessageObject.isRoundVideo())) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(messageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
                this.currentAspectRatioFrameLayoutReady = true;
            } else if (playbackState == 2) {
                if (playWhenReady && (messageObject2 = this.playingMessageObject) != null) {
                    if (!messageObject2.isVideo() && !this.playingMessageObject.isRoundVideo()) {
                        return;
                    }
                    if (this.playerWasReady) {
                        this.setLoadingRunnable.run();
                    } else {
                        AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000);
                    }
                }
            } else if (this.videoPlayer.isPlaying() && playbackState == 4) {
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

    public void injectVideoPlayer(VideoPlayer player, MessageObject messageObject) {
        if (player != null && messageObject != null) {
            FileLoader.getInstance(messageObject.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
            this.playerWasReady = false;
            clearPlaylist();
            this.videoPlayer = player;
            this.playingMessageObject = messageObject;
            final int tag = this.playerNum + 1;
            this.playerNum = tag;
            final MessageObject messageObject2 = messageObject;
            player.setDelegate(new VideoPlayer.VideoPlayerDelegate((int[]) null, true) {
                public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                }

                public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                }

                public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                }

                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    if (tag == MediaController.this.playerNum) {
                        MediaController.this.updateVideoState(messageObject2, null, true, playWhenReady, playbackState);
                    }
                }

                public void onError(VideoPlayer player, Exception e) {
                    FileLog.e((Throwable) e);
                }

                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                    int unused = MediaController.this.currentAspectRatioFrameLayoutRotation = unappliedRotationDegrees;
                    if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                        int temp = width;
                        width = height;
                        height = temp;
                    }
                    float unused2 = MediaController.this.currentAspectRatioFrameLayoutRatio = height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height);
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
                                } catch (Exception e) {
                                    PipRoundVideoView unused3 = MediaController.this.pipRoundVideoView = null;
                                }
                            }
                            if (MediaController.this.pipRoundVideoView != null) {
                                if (MediaController.this.pipRoundVideoView.getTextureView().getSurfaceTexture() != surfaceTexture) {
                                    MediaController.this.pipRoundVideoView.getTextureView().setSurfaceTexture(surfaceTexture);
                                }
                                MediaController.this.videoPlayer.setTextureView(MediaController.this.pipRoundVideoView.getTextureView());
                            }
                        }
                        int unused4 = MediaController.this.pipSwitchingState = 0;
                        return true;
                    } else if (!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isInjectingVideoPlayer()) {
                        return false;
                    } else {
                        PhotoViewer.getInstance().injectVideoPlayerSurface(surfaceTexture);
                        return true;
                    }
                }

                /* renamed from: lambda$onSurfaceDestroyed$0$org-telegram-messenger-MediaController$7  reason: not valid java name */
                public /* synthetic */ void m757x819a586() {
                    MediaController.this.cleanupPlayer(true, true);
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
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

    public void playEmojiSound(AccountInstance accountInstance, String emoji, MessagesController.EmojiSound sound, boolean loadOnly) {
        if (sound != null) {
            Utilities.stageQueue.postRunnable(new MediaController$$ExternalSyntheticLambda23(this, sound, accountInstance, loadOnly));
        }
    }

    /* renamed from: lambda$playEmojiSound$18$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m97lambda$playEmojiSound$18$orgtelegrammessengerMediaController(MessagesController.EmojiSound sound, AccountInstance accountInstance, boolean loadOnly) {
        TLRPC.Document document = new TLRPC.TL_document();
        document.access_hash = sound.accessHash;
        document.id = sound.id;
        document.mime_type = "sound/ogg";
        document.file_reference = sound.fileReference;
        document.dc_id = accountInstance.getConnectionsManager().getCurrentDatacenterId();
        File file = FileLoader.getPathToAttach(document, true);
        if (!file.exists()) {
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda41(accountInstance, document));
        } else if (!loadOnly) {
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda16(this, file));
        }
    }

    /* renamed from: lambda$playEmojiSound$16$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m96lambda$playEmojiSound$16$orgtelegrammessengerMediaController(File file) {
        try {
            final int tag = this.emojiSoundPlayerNum + 1;
            this.emojiSoundPlayerNum = tag;
            VideoPlayer videoPlayer2 = this.emojiSoundPlayer;
            if (videoPlayer2 != null) {
                videoPlayer2.releasePlayer(true);
            }
            VideoPlayer videoPlayer3 = new VideoPlayer(false);
            this.emojiSoundPlayer = videoPlayer3;
            videoPlayer3.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                }

                public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                }

                public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                }

                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    AndroidUtilities.runOnUIThread(new MediaController$8$$ExternalSyntheticLambda0(this, tag, playbackState));
                }

                /* renamed from: lambda$onStateChanged$0$org-telegram-messenger-MediaController$8  reason: not valid java name */
                public /* synthetic */ void m758lambda$onStateChanged$0$orgtelegrammessengerMediaController$8(int tag, int playbackState) {
                    if (tag == MediaController.this.emojiSoundPlayerNum && playbackState == 4 && MediaController.this.emojiSoundPlayer != null) {
                        try {
                            MediaController.this.emojiSoundPlayer.releasePlayer(true);
                            VideoPlayer unused = MediaController.this.emojiSoundPlayer = null;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                }

                public void onError(VideoPlayer player, Exception e) {
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

    /* JADX WARNING: Removed duplicated region for block: B:171:0x03ac  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x03b1  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x03c0  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x03d4 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0469  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0473  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x049c  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x04b5 A[SYNTHETIC, Splitter:B:215:0x04b5] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0563  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0595  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x05af  */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x05f8 A[SYNTHETIC, Splitter:B:252:0x05f8] */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0664  */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x06c4  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x06d9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean playMessage(org.telegram.messenger.MessageObject r29) {
        /*
            r28 = this;
            r7 = r28
            r8 = r29
            r9 = 0
            java.lang.Integer r10 = java.lang.Integer.valueOf(r9)
            if (r8 != 0) goto L_0x000c
            return r9
        L_0x000c:
            org.telegram.ui.Components.VideoPlayer r0 = r7.audioPlayer
            r11 = 1
            if (r0 != 0) goto L_0x0015
            org.telegram.ui.Components.VideoPlayer r0 = r7.videoPlayer
            if (r0 == 0) goto L_0x002c
        L_0x0015:
            boolean r0 = r28.isSamePlayingMessage(r29)
            if (r0 == 0) goto L_0x002c
            boolean r0 = r7.isPaused
            if (r0 == 0) goto L_0x0022
            r28.resumeAudio(r29)
        L_0x0022:
            boolean r0 = org.telegram.messenger.SharedConfig.raiseToSpeak
            if (r0 != 0) goto L_0x002b
            org.telegram.ui.ChatActivity r0 = r7.raiseChat
            r7.startRaiseToEarSensors(r0)
        L_0x002b:
            return r11
        L_0x002c:
            boolean r0 = r29.isOut()
            if (r0 != 0) goto L_0x0041
            boolean r0 = r29.isContentUnread()
            if (r0 == 0) goto L_0x0041
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            r0.markMessageContentAsRead(r8)
        L_0x0041:
            boolean r0 = r7.playMusicAgain
            r1 = r0 ^ 1
            org.telegram.messenger.MessageObject r2 = r7.playingMessageObject
            r12 = 2
            if (r2 == 0) goto L_0x006f
            r1 = 0
            if (r0 != 0) goto L_0x006d
            r2.resetPlayingProgress()
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject
            int r0 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            java.lang.Object[] r3 = new java.lang.Object[r12]
            org.telegram.messenger.MessageObject r4 = r7.playingMessageObject
            int r4 = r4.getId()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r9] = r4
            r3[r11] = r10
            r0.postNotificationName(r2, r3)
        L_0x006d:
            r13 = r1
            goto L_0x0070
        L_0x006f:
            r13 = r1
        L_0x0070:
            r7.cleanupPlayer(r13, r9)
            r14 = 0
            r7.shouldSavePositionForCurrentAudio = r14
            r1 = 0
            r7.lastSaveTime = r1
            r7.playMusicAgain = r9
            r15 = 0
            r7.seekToProgressPending = r15
            r0 = 0
            r3 = 0
            org.telegram.tgnet.TLRPC$Message r4 = r8.messageOwner
            java.lang.String r4 = r4.attachPath
            if (r4 == 0) goto L_0x00a6
            org.telegram.tgnet.TLRPC$Message r4 = r8.messageOwner
            java.lang.String r4 = r4.attachPath
            int r4 = r4.length()
            if (r4 <= 0) goto L_0x00a6
            java.io.File r4 = new java.io.File
            org.telegram.tgnet.TLRPC$Message r5 = r8.messageOwner
            java.lang.String r5 = r5.attachPath
            r4.<init>(r5)
            r0 = r4
            boolean r3 = r0.exists()
            if (r3 != 0) goto L_0x00a4
            r0 = 0
            r4 = r0
            goto L_0x00a7
        L_0x00a4:
            r4 = r0
            goto L_0x00a7
        L_0x00a6:
            r4 = r0
        L_0x00a7:
            if (r4 == 0) goto L_0x00ab
            r0 = r4
            goto L_0x00b1
        L_0x00ab:
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
        L_0x00b1:
            r5 = r0
            boolean r0 = org.telegram.messenger.SharedConfig.streamMedia
            if (r0 == 0) goto L_0x00da
            boolean r0 = r29.isMusic()
            if (r0 != 0) goto L_0x00ce
            boolean r0 = r29.isRoundVideo()
            if (r0 != 0) goto L_0x00ce
            boolean r0 = r29.isVideo()
            if (r0 == 0) goto L_0x00da
            boolean r0 = r29.canStreamVideo()
            if (r0 == 0) goto L_0x00da
        L_0x00ce:
            long r16 = r29.getDialogId()
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r16)
            if (r0 != 0) goto L_0x00da
            r0 = 1
            goto L_0x00db
        L_0x00da:
            r0 = 0
        L_0x00db:
            r16 = r0
            if (r5 == r4) goto L_0x0147
            boolean r0 = r5.exists()
            r3 = r0
            if (r0 != 0) goto L_0x0144
            if (r16 != 0) goto L_0x0144
            int r0 = r8.currentAccount
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)
            org.telegram.tgnet.TLRPC$Document r6 = r29.getDocument()
            r0.loadFile(r6, r8, r9, r9)
            r7.downloadingCurrentMessage = r11
            r7.isPaused = r9
            r7.lastProgress = r1
            r7.audioInfo = r14
            r7.playingMessageObject = r8
            boolean r0 = r29.isMusic()
            if (r0 == 0) goto L_0x011a
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.MusicPlayerService> r2 = org.telegram.messenger.MusicPlayerService.class
            r0.<init>(r1, r2)
            r1 = r0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0115 }
            r0.startService(r1)     // Catch:{ all -> 0x0115 }
            goto L_0x0119
        L_0x0115:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0119:
            goto L_0x0128
        L_0x011a:
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.MusicPlayerService> r2 = org.telegram.messenger.MusicPlayerService.class
            r0.<init>(r1, r2)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r1.stopService(r0)
        L_0x0128:
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject
            int r0 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            java.lang.Object[] r2 = new java.lang.Object[r11]
            org.telegram.messenger.MessageObject r6 = r7.playingMessageObject
            int r6 = r6.getId()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r2[r9] = r6
            r0.postNotificationName(r1, r2)
            return r11
        L_0x0144:
            r17 = r3
            goto L_0x0149
        L_0x0147:
            r17 = r3
        L_0x0149:
            r7.downloadingCurrentMessage = r9
            boolean r0 = r29.isMusic()
            if (r0 == 0) goto L_0x0157
            int r0 = r8.currentAccount
            r7.checkIsNextMusicFileDownloaded(r0)
            goto L_0x015c
        L_0x0157:
            int r0 = r8.currentAccount
            r7.checkIsNextVoiceFileDownloaded(r0)
        L_0x015c:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r7.currentAspectRatioFrameLayout
            if (r0 == 0) goto L_0x0165
            r7.isDrawingWasReady = r9
            r0.setDrawingReady(r9)
        L_0x0165:
            boolean r18 = r29.isVideo()
            boolean r0 = r29.isRoundVideo()
            java.lang.String r3 = "&rid="
            java.lang.String r6 = "&mime="
            java.lang.String r1 = "&size="
            java.lang.String r2 = "&dc="
            java.lang.String r12 = "&hash="
            java.lang.String r15 = "&id="
            java.lang.String r9 = "?account="
            r21 = 981668463(0x3a83126f, float:0.001)
            java.lang.String r14 = "UTF-8"
            java.lang.String r11 = "other"
            r23 = r13
            if (r0 != 0) goto L_0x03d6
            if (r18 == 0) goto L_0x018f
            r25 = r4
            r24 = r10
            r4 = 0
            goto L_0x03db
        L_0x018f:
            org.telegram.ui.Components.PipRoundVideoView r0 = r7.pipRoundVideoView
            if (r0 == 0) goto L_0x019a
            r13 = 1
            r0.close(r13)
            r13 = 0
            r7.pipRoundVideoView = r13
        L_0x019a:
            org.telegram.ui.Components.VideoPlayer r0 = new org.telegram.ui.Components.VideoPlayer     // Catch:{ Exception -> 0x0397 }
            r0.<init>()     // Catch:{ Exception -> 0x0397 }
            r7.audioPlayer = r0     // Catch:{ Exception -> 0x0397 }
            int r13 = r7.playerNum     // Catch:{ Exception -> 0x0397 }
            r22 = 1
            int r13 = r13 + 1
            r7.playerNum = r13     // Catch:{ Exception -> 0x0397 }
            r24 = r10
            org.telegram.messenger.MediaController$10 r10 = new org.telegram.messenger.MediaController$10     // Catch:{ Exception -> 0x0397 }
            r10.<init>(r13, r8)     // Catch:{ Exception -> 0x0397 }
            r0.setDelegate(r10)     // Catch:{ Exception -> 0x0397 }
            org.telegram.ui.Components.VideoPlayer r0 = r7.audioPlayer     // Catch:{ Exception -> 0x0397 }
            org.telegram.messenger.MediaController$11 r10 = new org.telegram.messenger.MediaController$11     // Catch:{ Exception -> 0x0397 }
            r10.<init>()     // Catch:{ Exception -> 0x0397 }
            r0.setAudioVisualizerDelegate(r10)     // Catch:{ Exception -> 0x0397 }
            if (r17 == 0) goto L_0x01e2
            boolean r0 = r8.mediaExists     // Catch:{ Exception -> 0x01dd }
            if (r0 != 0) goto L_0x01cd
            if (r5 == r4) goto L_0x01cd
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda28 r0 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda28     // Catch:{ Exception -> 0x01dd }
            r0.<init>(r8, r5)     // Catch:{ Exception -> 0x01dd }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x01dd }
        L_0x01cd:
            org.telegram.ui.Components.VideoPlayer r0 = r7.audioPlayer     // Catch:{ Exception -> 0x01dd }
            android.net.Uri r1 = android.net.Uri.fromFile(r5)     // Catch:{ Exception -> 0x01dd }
            r0.preparePlayer(r1, r11)     // Catch:{ Exception -> 0x01dd }
            r1 = 0
            r7.isStreamingCurrentAudio = r1     // Catch:{ Exception -> 0x01dd }
            r25 = r4
            goto L_0x0280
        L_0x01dd:
            r0 = move-exception
            r25 = r4
            goto L_0x039a
        L_0x01e2:
            int r0 = r8.currentAccount     // Catch:{ Exception -> 0x0397 }
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)     // Catch:{ Exception -> 0x0397 }
            int r0 = r0.getFileReference(r8)     // Catch:{ Exception -> 0x0397 }
            org.telegram.tgnet.TLRPC$Document r10 = r29.getDocument()     // Catch:{ Exception -> 0x0397 }
            r25 = r4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0395 }
            r4.<init>()     // Catch:{ Exception -> 0x0395 }
            r4.append(r9)     // Catch:{ Exception -> 0x0395 }
            int r9 = r8.currentAccount     // Catch:{ Exception -> 0x0395 }
            r4.append(r9)     // Catch:{ Exception -> 0x0395 }
            r4.append(r15)     // Catch:{ Exception -> 0x0395 }
            long r8 = r10.id     // Catch:{ Exception -> 0x0391 }
            r4.append(r8)     // Catch:{ Exception -> 0x0391 }
            r4.append(r12)     // Catch:{ Exception -> 0x0391 }
            long r8 = r10.access_hash     // Catch:{ Exception -> 0x0391 }
            r4.append(r8)     // Catch:{ Exception -> 0x0391 }
            r4.append(r2)     // Catch:{ Exception -> 0x0391 }
            int r2 = r10.dc_id     // Catch:{ Exception -> 0x0391 }
            r4.append(r2)     // Catch:{ Exception -> 0x0391 }
            r4.append(r1)     // Catch:{ Exception -> 0x0391 }
            int r1 = r10.size     // Catch:{ Exception -> 0x0391 }
            r4.append(r1)     // Catch:{ Exception -> 0x0391 }
            r4.append(r6)     // Catch:{ Exception -> 0x0391 }
            java.lang.String r1 = r10.mime_type     // Catch:{ Exception -> 0x0391 }
            java.lang.String r1 = java.net.URLEncoder.encode(r1, r14)     // Catch:{ Exception -> 0x0391 }
            r4.append(r1)     // Catch:{ Exception -> 0x0391 }
            r4.append(r3)     // Catch:{ Exception -> 0x0391 }
            r4.append(r0)     // Catch:{ Exception -> 0x0391 }
            java.lang.String r1 = "&name="
            r4.append(r1)     // Catch:{ Exception -> 0x0391 }
            java.lang.String r1 = org.telegram.messenger.FileLoader.getDocumentFileName(r10)     // Catch:{ Exception -> 0x0391 }
            java.lang.String r1 = java.net.URLEncoder.encode(r1, r14)     // Catch:{ Exception -> 0x0391 }
            r4.append(r1)     // Catch:{ Exception -> 0x0391 }
            java.lang.String r1 = "&reference="
            r4.append(r1)     // Catch:{ Exception -> 0x0391 }
            byte[] r1 = r10.file_reference     // Catch:{ Exception -> 0x0391 }
            if (r1 == 0) goto L_0x024d
            byte[] r1 = r10.file_reference     // Catch:{ Exception -> 0x0391 }
            goto L_0x0251
        L_0x024d:
            r1 = 0
            byte[] r2 = new byte[r1]     // Catch:{ Exception -> 0x0391 }
            r1 = r2
        L_0x0251:
            java.lang.String r1 = org.telegram.messenger.Utilities.bytesToHex(r1)     // Catch:{ Exception -> 0x0391 }
            r4.append(r1)     // Catch:{ Exception -> 0x0391 }
            java.lang.String r1 = r4.toString()     // Catch:{ Exception -> 0x0391 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0391 }
            r2.<init>()     // Catch:{ Exception -> 0x0391 }
            java.lang.String r3 = "tg://"
            r2.append(r3)     // Catch:{ Exception -> 0x0391 }
            java.lang.String r3 = r29.getFileName()     // Catch:{ Exception -> 0x0391 }
            r2.append(r3)     // Catch:{ Exception -> 0x0391 }
            r2.append(r1)     // Catch:{ Exception -> 0x0391 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0391 }
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0391 }
            org.telegram.ui.Components.VideoPlayer r3 = r7.audioPlayer     // Catch:{ Exception -> 0x0391 }
            r3.preparePlayer(r2, r11)     // Catch:{ Exception -> 0x0391 }
            r3 = 1
            r7.isStreamingCurrentAudio = r3     // Catch:{ Exception -> 0x0391 }
        L_0x0280:
            boolean r0 = r29.isVoice()     // Catch:{ Exception -> 0x0391 }
            java.lang.String r1 = "media_saved_pos"
            if (r0 == 0) goto L_0x02d9
            java.lang.String r0 = r29.getFileName()     // Catch:{ Exception -> 0x0391 }
            if (r0 == 0) goto L_0x02bc
            int r2 = r29.getDuration()     // Catch:{ Exception -> 0x0391 }
            r3 = 300(0x12c, float:4.2E-43)
            if (r2 < r3) goto L_0x02bc
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0391 }
            r3 = 0
            android.content.SharedPreferences r1 = r2.getSharedPreferences(r1, r3)     // Catch:{ Exception -> 0x0391 }
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            float r3 = r1.getFloat(r0, r2)     // Catch:{ Exception -> 0x0391 }
            r2 = r3
            r3 = 0
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x02b7
            r3 = 1065185444(0x3f7d70a4, float:0.99)
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x02b7
            r7.seekToProgressPending = r2     // Catch:{ Exception -> 0x0391 }
            r8 = r29
            r8.audioProgress = r2     // Catch:{ Exception -> 0x0395 }
            goto L_0x02b9
        L_0x02b7:
            r8 = r29
        L_0x02b9:
            r7.shouldSavePositionForCurrentAudio = r0     // Catch:{ Exception -> 0x0395 }
            goto L_0x02be
        L_0x02bc:
            r8 = r29
        L_0x02be:
            float r1 = r7.currentPlaybackSpeed     // Catch:{ Exception -> 0x0395 }
            r2 = 1065353216(0x3var_, float:1.0)
            float r1 = r1 - r2
            float r1 = java.lang.Math.abs(r1)     // Catch:{ Exception -> 0x0395 }
            int r1 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r1 <= 0) goto L_0x02d2
            org.telegram.ui.Components.VideoPlayer r1 = r7.audioPlayer     // Catch:{ Exception -> 0x0395 }
            float r2 = r7.currentPlaybackSpeed     // Catch:{ Exception -> 0x0395 }
            r1.setPlaybackSpeed(r2)     // Catch:{ Exception -> 0x0395 }
        L_0x02d2:
            r1 = 0
            r7.audioInfo = r1     // Catch:{ Exception -> 0x0395 }
            r28.clearPlaylist()     // Catch:{ Exception -> 0x0395 }
            goto L_0x032c
        L_0x02d9:
            r8 = r29
            org.telegram.messenger.audioinfo.AudioInfo r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r5)     // Catch:{ Exception -> 0x02e2 }
            r7.audioInfo = r0     // Catch:{ Exception -> 0x02e2 }
            goto L_0x02e6
        L_0x02e2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0395 }
        L_0x02e6:
            java.lang.String r0 = r29.getFileName()     // Catch:{ Exception -> 0x0395 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0395 }
            if (r2 != 0) goto L_0x032c
            int r2 = r29.getDuration()     // Catch:{ Exception -> 0x0395 }
            r3 = 600(0x258, float:8.41E-43)
            if (r2 < r3) goto L_0x032c
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0395 }
            r3 = 0
            android.content.SharedPreferences r1 = r2.getSharedPreferences(r1, r3)     // Catch:{ Exception -> 0x0395 }
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            float r3 = r1.getFloat(r0, r2)     // Catch:{ Exception -> 0x0395 }
            r2 = r3
            r3 = 0
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x0316
            r3 = 1065336439(0x3f7fbe77, float:0.999)
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0316
            r7.seekToProgressPending = r2     // Catch:{ Exception -> 0x0395 }
            r8.audioProgress = r2     // Catch:{ Exception -> 0x0395 }
        L_0x0316:
            r7.shouldSavePositionForCurrentAudio = r0     // Catch:{ Exception -> 0x0395 }
            float r3 = r7.currentMusicPlaybackSpeed     // Catch:{ Exception -> 0x0395 }
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r3 - r4
            float r3 = java.lang.Math.abs(r3)     // Catch:{ Exception -> 0x0395 }
            int r3 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r3 <= 0) goto L_0x032c
            org.telegram.ui.Components.VideoPlayer r3 = r7.audioPlayer     // Catch:{ Exception -> 0x0395 }
            float r4 = r7.currentMusicPlaybackSpeed     // Catch:{ Exception -> 0x0395 }
            r3.setPlaybackSpeed(r4)     // Catch:{ Exception -> 0x0395 }
        L_0x032c:
            float r0 = r8.forceSeekTo     // Catch:{ Exception -> 0x0395 }
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x033d
            float r0 = r8.forceSeekTo     // Catch:{ Exception -> 0x0395 }
            r7.seekToProgressPending = r0     // Catch:{ Exception -> 0x0395 }
            r8.audioProgress = r0     // Catch:{ Exception -> 0x0395 }
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r8.forceSeekTo = r1     // Catch:{ Exception -> 0x0395 }
        L_0x033d:
            org.telegram.ui.Components.VideoPlayer r0 = r7.audioPlayer     // Catch:{ Exception -> 0x0395 }
            boolean r1 = r7.useFrontSpeaker     // Catch:{ Exception -> 0x0395 }
            if (r1 == 0) goto L_0x0345
            r1 = 0
            goto L_0x0346
        L_0x0345:
            r1 = 3
        L_0x0346:
            r0.setStreamType(r1)     // Catch:{ Exception -> 0x0395 }
            org.telegram.ui.Components.VideoPlayer r0 = r7.audioPlayer     // Catch:{ Exception -> 0x0395 }
            r0.play()     // Catch:{ Exception -> 0x0395 }
            boolean r0 = r29.isVoice()     // Catch:{ Exception -> 0x0395 }
            if (r0 != 0) goto L_0x0385
            android.animation.ValueAnimator r0 = r7.audioVolumeAnimator     // Catch:{ Exception -> 0x0395 }
            if (r0 == 0) goto L_0x0360
            r0.removeAllListeners()     // Catch:{ Exception -> 0x0395 }
            android.animation.ValueAnimator r0 = r7.audioVolumeAnimator     // Catch:{ Exception -> 0x0395 }
            r0.cancel()     // Catch:{ Exception -> 0x0395 }
        L_0x0360:
            r1 = 2
            float[] r0 = new float[r1]     // Catch:{ Exception -> 0x0395 }
            float r1 = r7.audioVolume     // Catch:{ Exception -> 0x0395 }
            r2 = 0
            r0[r2] = r1     // Catch:{ Exception -> 0x0395 }
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 1
            r0[r2] = r1     // Catch:{ Exception -> 0x0395 }
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)     // Catch:{ Exception -> 0x0395 }
            r7.audioVolumeAnimator = r0     // Catch:{ Exception -> 0x0395 }
            android.animation.ValueAnimator$AnimatorUpdateListener r1 = r7.audioVolumeUpdateListener     // Catch:{ Exception -> 0x0395 }
            r0.addUpdateListener(r1)     // Catch:{ Exception -> 0x0395 }
            android.animation.ValueAnimator r0 = r7.audioVolumeAnimator     // Catch:{ Exception -> 0x0395 }
            r1 = 300(0x12c, double:1.48E-321)
            r0.setDuration(r1)     // Catch:{ Exception -> 0x0395 }
            android.animation.ValueAnimator r0 = r7.audioVolumeAnimator     // Catch:{ Exception -> 0x0395 }
            r0.start()     // Catch:{ Exception -> 0x0395 }
            goto L_0x038c
        L_0x0385:
            r1 = 1065353216(0x3var_, float:1.0)
            r7.audioVolume = r1     // Catch:{ Exception -> 0x0395 }
            r28.setPlayerVolume()     // Catch:{ Exception -> 0x0395 }
        L_0x038c:
            r13 = r5
            r14 = r25
            goto L_0x059c
        L_0x0391:
            r0 = move-exception
            r8 = r29
            goto L_0x039a
        L_0x0395:
            r0 = move-exception
            goto L_0x039a
        L_0x0397:
            r0 = move-exception
            r25 = r4
        L_0x039a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            int r1 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            org.telegram.messenger.MessageObject r3 = r7.playingMessageObject
            if (r3 == 0) goto L_0x03b1
            int r3 = r3.getId()
            goto L_0x03b2
        L_0x03b1:
            r3 = 0
        L_0x03b2:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r6 = 0
            r4[r6] = r3
            r1.postNotificationName(r2, r4)
            org.telegram.ui.Components.VideoPlayer r1 = r7.audioPlayer
            if (r1 == 0) goto L_0x03d4
            r2 = 1
            r1.releasePlayer(r2)
            r1 = 0
            r7.audioPlayer = r1
            org.telegram.messenger.MessageObject r2 = r7.playingMessageObject
            org.telegram.ui.ActionBar.Theme.unrefAudioVisualizeDrawable(r2)
            r4 = 0
            r7.isPaused = r4
            r7.playingMessageObject = r1
            r7.downloadingCurrentMessage = r4
            goto L_0x03d5
        L_0x03d4:
            r4 = 0
        L_0x03d5:
            return r4
        L_0x03d6:
            r25 = r4
            r24 = r10
            r4 = 0
        L_0x03db:
            int r0 = r8.currentAccount
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)
            org.telegram.tgnet.TLRPC$Document r10 = r29.getDocument()
            r13 = 1
            r0.setLoadingVideoForPlayer(r10, r13)
            r7.playerWasReady = r4
            if (r18 == 0) goto L_0x0406
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            r4 = r1
            long r0 = r0.channel_id
            r19 = 0
            int r10 = (r0 > r19 ? 1 : (r0 == r19 ? 0 : -1))
            if (r10 != 0) goto L_0x0404
            float r0 = r8.audioProgress
            r1 = 1036831949(0x3dcccccd, float:0.1)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L_0x0404
            goto L_0x0409
        L_0x0404:
            r0 = 0
            goto L_0x040a
        L_0x0406:
            r4 = r1
            r19 = 0
        L_0x0409:
            r0 = 1
        L_0x040a:
            r10 = r6
            r6 = r0
            if (r18 == 0) goto L_0x041d
            int r0 = r29.getDuration()
            r1 = 30
            if (r0 > r1) goto L_0x041d
            r1 = 1
            int[] r0 = new int[r1]
            r13 = 0
            r0[r13] = r1
            goto L_0x041e
        L_0x041d:
            r0 = 0
        L_0x041e:
            r13 = r5
            r5 = r0
            r28.clearPlaylist()
            org.telegram.ui.Components.VideoPlayer r0 = new org.telegram.ui.Components.VideoPlayer
            r0.<init>()
            r7.videoPlayer = r0
            int r1 = r7.playerNum
            r22 = 1
            int r1 = r1 + 1
            r7.playerNum = r1
            r26 = r3
            r3 = r1
            org.telegram.messenger.MediaController$9 r1 = new org.telegram.messenger.MediaController$9
            r19 = r14
            r14 = r4
            r4 = r1
            r20 = r10
            r10 = r2
            r2 = r28
            r27 = r14
            r14 = r25
            r25 = r10
            r10 = r4
            r4 = r29
            r1.<init>(r3, r4, r5, r6)
            r0.setDelegate(r10)
            r1 = 0
            r7.currentAspectRatioFrameLayoutReady = r1
            org.telegram.ui.Components.PipRoundVideoView r0 = r7.pipRoundVideoView
            if (r0 != 0) goto L_0x0473
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = r29.getDialogId()
            boolean r4 = r8.scheduled
            boolean r0 = r0.isDialogVisible(r1, r4)
            if (r0 != 0) goto L_0x0469
            goto L_0x0473
        L_0x0469:
            android.view.TextureView r0 = r7.currentTextureView
            if (r0 == 0) goto L_0x049a
            org.telegram.ui.Components.VideoPlayer r1 = r7.videoPlayer
            r1.setTextureView(r0)
            goto L_0x049a
        L_0x0473:
            org.telegram.ui.Components.PipRoundVideoView r0 = r7.pipRoundVideoView
            if (r0 != 0) goto L_0x048d
            org.telegram.ui.Components.PipRoundVideoView r0 = new org.telegram.ui.Components.PipRoundVideoView     // Catch:{ Exception -> 0x0489 }
            r0.<init>()     // Catch:{ Exception -> 0x0489 }
            r7.pipRoundVideoView = r0     // Catch:{ Exception -> 0x0489 }
            android.app.Activity r1 = r7.baseActivity     // Catch:{ Exception -> 0x0489 }
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda2 r2 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda2     // Catch:{ Exception -> 0x0489 }
            r2.<init>(r7)     // Catch:{ Exception -> 0x0489 }
            r0.show(r1, r2)     // Catch:{ Exception -> 0x0489 }
            goto L_0x048d
        L_0x0489:
            r0 = move-exception
            r1 = 0
            r7.pipRoundVideoView = r1
        L_0x048d:
            org.telegram.ui.Components.PipRoundVideoView r0 = r7.pipRoundVideoView
            if (r0 == 0) goto L_0x049a
            org.telegram.ui.Components.VideoPlayer r1 = r7.videoPlayer
            android.view.TextureView r0 = r0.getTextureView()
            r1.setTextureView(r0)
        L_0x049a:
            if (r17 == 0) goto L_0x04b5
            boolean r0 = r8.mediaExists
            if (r0 != 0) goto L_0x04aa
            if (r13 == r14) goto L_0x04aa
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda27 r0 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda27
            r0.<init>(r8, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x04aa:
            org.telegram.ui.Components.VideoPlayer r0 = r7.videoPlayer
            android.net.Uri r1 = android.net.Uri.fromFile(r13)
            r0.preparePlayer(r1, r11)
            goto L_0x055d
        L_0x04b5:
            int r0 = r8.currentAccount     // Catch:{ Exception -> 0x0559 }
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)     // Catch:{ Exception -> 0x0559 }
            int r0 = r0.getFileReference(r8)     // Catch:{ Exception -> 0x0559 }
            org.telegram.tgnet.TLRPC$Document r1 = r29.getDocument()     // Catch:{ Exception -> 0x0559 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0559 }
            r2.<init>()     // Catch:{ Exception -> 0x0559 }
            r2.append(r9)     // Catch:{ Exception -> 0x0559 }
            int r4 = r8.currentAccount     // Catch:{ Exception -> 0x0559 }
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            r2.append(r15)     // Catch:{ Exception -> 0x0559 }
            long r9 = r1.id     // Catch:{ Exception -> 0x0559 }
            r2.append(r9)     // Catch:{ Exception -> 0x0559 }
            r2.append(r12)     // Catch:{ Exception -> 0x0559 }
            long r9 = r1.access_hash     // Catch:{ Exception -> 0x0559 }
            r2.append(r9)     // Catch:{ Exception -> 0x0559 }
            r4 = r25
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            int r4 = r1.dc_id     // Catch:{ Exception -> 0x0559 }
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            r4 = r27
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            int r4 = r1.size     // Catch:{ Exception -> 0x0559 }
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            r4 = r20
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            java.lang.String r4 = r1.mime_type     // Catch:{ Exception -> 0x0559 }
            r9 = r19
            java.lang.String r4 = java.net.URLEncoder.encode(r4, r9)     // Catch:{ Exception -> 0x0559 }
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            r4 = r26
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            r2.append(r0)     // Catch:{ Exception -> 0x0559 }
            java.lang.String r4 = "&name="
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            java.lang.String r4 = org.telegram.messenger.FileLoader.getDocumentFileName(r1)     // Catch:{ Exception -> 0x0559 }
            java.lang.String r4 = java.net.URLEncoder.encode(r4, r9)     // Catch:{ Exception -> 0x0559 }
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            java.lang.String r4 = "&reference="
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            byte[] r4 = r1.file_reference     // Catch:{ Exception -> 0x0559 }
            if (r4 == 0) goto L_0x0528
            byte[] r4 = r1.file_reference     // Catch:{ Exception -> 0x0559 }
            goto L_0x052c
        L_0x0528:
            r4 = 0
            byte[] r9 = new byte[r4]     // Catch:{ Exception -> 0x0559 }
            r4 = r9
        L_0x052c:
            java.lang.String r4 = org.telegram.messenger.Utilities.bytesToHex(r4)     // Catch:{ Exception -> 0x0559 }
            r2.append(r4)     // Catch:{ Exception -> 0x0559 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0559 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0559 }
            r4.<init>()     // Catch:{ Exception -> 0x0559 }
            java.lang.String r9 = "tg://"
            r4.append(r9)     // Catch:{ Exception -> 0x0559 }
            java.lang.String r9 = r29.getFileName()     // Catch:{ Exception -> 0x0559 }
            r4.append(r9)     // Catch:{ Exception -> 0x0559 }
            r4.append(r2)     // Catch:{ Exception -> 0x0559 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0559 }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x0559 }
            org.telegram.ui.Components.VideoPlayer r9 = r7.videoPlayer     // Catch:{ Exception -> 0x0559 }
            r9.preparePlayer(r4, r11)     // Catch:{ Exception -> 0x0559 }
            goto L_0x055d
        L_0x0559:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x055d:
            boolean r0 = r29.isRoundVideo()
            if (r0 == 0) goto L_0x0595
            org.telegram.ui.Components.VideoPlayer r0 = r7.videoPlayer
            boolean r1 = r7.useFrontSpeaker
            if (r1 == 0) goto L_0x056b
            r1 = 0
            goto L_0x056c
        L_0x056b:
            r1 = 3
        L_0x056c:
            r0.setStreamType(r1)
            float r0 = r7.currentPlaybackSpeed
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r0 - r1
            float r0 = java.lang.Math.abs(r0)
            int r0 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1))
            if (r0 <= 0) goto L_0x0583
            org.telegram.ui.Components.VideoPlayer r0 = r7.videoPlayer
            float r1 = r7.currentPlaybackSpeed
            r0.setPlaybackSpeed(r1)
        L_0x0583:
            float r0 = r8.forceSeekTo
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x059b
            float r0 = r8.forceSeekTo
            r7.seekToProgressPending = r0
            r8.audioProgress = r0
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r8.forceSeekTo = r1
            goto L_0x059b
        L_0x0595:
            org.telegram.ui.Components.VideoPlayer r0 = r7.videoPlayer
            r1 = 3
            r0.setStreamType(r1)
        L_0x059b:
        L_0x059c:
            r28.checkAudioFocus(r29)
            r28.setPlayerVolume()
            r1 = 0
            r7.isPaused = r1
            r1 = 0
            r7.lastProgress = r1
            r7.playingMessageObject = r8
            boolean r0 = org.telegram.messenger.SharedConfig.raiseToSpeak
            if (r0 != 0) goto L_0x05b4
            org.telegram.ui.ChatActivity r0 = r7.raiseChat
            r7.startRaiseToEarSensors(r0)
        L_0x05b4:
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
            if (r0 != 0) goto L_0x05d7
            android.os.PowerManager$WakeLock r0 = r7.proximityWakeLock
            if (r0 == 0) goto L_0x05d7
            boolean r0 = r0.isHeld()
            if (r0 != 0) goto L_0x05d7
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject
            boolean r0 = r0.isVoice()
            if (r0 != 0) goto L_0x05d2
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject
            boolean r0 = r0.isRoundVideo()
            if (r0 == 0) goto L_0x05d7
        L_0x05d2:
            android.os.PowerManager$WakeLock r0 = r7.proximityWakeLock
            r0.acquire()
        L_0x05d7:
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject
            r7.startProgressTimer(r0)
            int r0 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r8
            r0.postNotificationName(r1, r3)
            org.telegram.ui.Components.VideoPlayer r0 = r7.videoPlayer
            r1 = 1000(0x3e8, double:4.94E-321)
            r3 = -9223372036854775807(0xNUM, double:-4.9E-324)
            if (r0 == 0) goto L_0x0664
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject     // Catch:{ Exception -> 0x0633 }
            float r0 = r0.audioProgress     // Catch:{ Exception -> 0x0633 }
            r5 = 0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x0632
            org.telegram.ui.Components.VideoPlayer r0 = r7.videoPlayer     // Catch:{ Exception -> 0x0633 }
            long r5 = r0.getDuration()     // Catch:{ Exception -> 0x0633 }
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0614
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject     // Catch:{ Exception -> 0x0633 }
            int r0 = r0.getDuration()     // Catch:{ Exception -> 0x0633 }
            long r3 = (long) r0     // Catch:{ Exception -> 0x0633 }
            long r5 = r3 * r1
        L_0x0614:
            float r0 = (float) r5     // Catch:{ Exception -> 0x0633 }
            org.telegram.messenger.MessageObject r1 = r7.playingMessageObject     // Catch:{ Exception -> 0x0633 }
            float r1 = r1.audioProgress     // Catch:{ Exception -> 0x0633 }
            float r0 = r0 * r1
            int r0 = (int) r0     // Catch:{ Exception -> 0x0633 }
            org.telegram.messenger.MessageObject r1 = r7.playingMessageObject     // Catch:{ Exception -> 0x0633 }
            int r1 = r1.audioProgressMs     // Catch:{ Exception -> 0x0633 }
            if (r1 == 0) goto L_0x062c
            org.telegram.messenger.MessageObject r1 = r7.playingMessageObject     // Catch:{ Exception -> 0x0633 }
            int r1 = r1.audioProgressMs     // Catch:{ Exception -> 0x0633 }
            r0 = r1
            org.telegram.messenger.MessageObject r1 = r7.playingMessageObject     // Catch:{ Exception -> 0x0633 }
            r2 = 0
            r1.audioProgressMs = r2     // Catch:{ Exception -> 0x0633 }
        L_0x062c:
            org.telegram.ui.Components.VideoPlayer r1 = r7.videoPlayer     // Catch:{ Exception -> 0x0633 }
            long r2 = (long) r0     // Catch:{ Exception -> 0x0633 }
            r1.seekTo(r2)     // Catch:{ Exception -> 0x0633 }
        L_0x0632:
            goto L_0x065e
        L_0x0633:
            r0 = move-exception
            org.telegram.messenger.MessageObject r1 = r7.playingMessageObject
            r2 = 0
            r1.audioProgress = r2
            org.telegram.messenger.MessageObject r1 = r7.playingMessageObject
            r2 = 0
            r1.audioProgressSec = r2
            int r1 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r3 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            org.telegram.messenger.MessageObject r5 = r7.playingMessageObject
            int r5 = r5.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r2] = r5
            r2 = 1
            r4[r2] = r24
            r1.postNotificationName(r3, r4)
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x065e:
            org.telegram.ui.Components.VideoPlayer r0 = r7.videoPlayer
            r0.play()
            goto L_0x06ba
        L_0x0664:
            org.telegram.ui.Components.VideoPlayer r0 = r7.audioPlayer
            if (r0 == 0) goto L_0x06ba
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject     // Catch:{ Exception -> 0x0693 }
            float r0 = r0.audioProgress     // Catch:{ Exception -> 0x0693 }
            r5 = 0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x0692
            org.telegram.ui.Components.VideoPlayer r0 = r7.audioPlayer     // Catch:{ Exception -> 0x0693 }
            long r5 = r0.getDuration()     // Catch:{ Exception -> 0x0693 }
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0684
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject     // Catch:{ Exception -> 0x0693 }
            int r0 = r0.getDuration()     // Catch:{ Exception -> 0x0693 }
            long r3 = (long) r0     // Catch:{ Exception -> 0x0693 }
            long r5 = r3 * r1
        L_0x0684:
            float r0 = (float) r5     // Catch:{ Exception -> 0x0693 }
            org.telegram.messenger.MessageObject r1 = r7.playingMessageObject     // Catch:{ Exception -> 0x0693 }
            float r1 = r1.audioProgress     // Catch:{ Exception -> 0x0693 }
            float r0 = r0 * r1
            int r0 = (int) r0     // Catch:{ Exception -> 0x0693 }
            org.telegram.ui.Components.VideoPlayer r1 = r7.audioPlayer     // Catch:{ Exception -> 0x0693 }
            long r2 = (long) r0     // Catch:{ Exception -> 0x0693 }
            r1.seekTo(r2)     // Catch:{ Exception -> 0x0693 }
        L_0x0692:
            goto L_0x06ba
        L_0x0693:
            r0 = move-exception
            org.telegram.messenger.MessageObject r1 = r7.playingMessageObject
            r1.resetPlayingProgress()
            int r1 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.MessageObject r4 = r7.playingMessageObject
            int r4 = r4.getId()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r5 = 0
            r3[r5] = r4
            r4 = 1
            r3[r4] = r24
            r1.postNotificationName(r2, r3)
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x06ba:
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject
            if (r0 == 0) goto L_0x06d9
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x06d9
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.MusicPlayerService> r2 = org.telegram.messenger.MusicPlayerService.class
            r0.<init>(r1, r2)
            r1 = r0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x06d4 }
            r0.startService(r1)     // Catch:{ all -> 0x06d4 }
            goto L_0x06d8
        L_0x06d4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x06d8:
            goto L_0x06e7
        L_0x06d9:
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.MusicPlayerService> r2 = org.telegram.messenger.MusicPlayerService.class
            r0.<init>(r1, r2)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r1.stopService(r0)
        L_0x06e7:
            r1 = 1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.playMessage(org.telegram.messenger.MessageObject):boolean");
    }

    /* renamed from: lambda$playMessage$19$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m98lambda$playMessage$19$orgtelegrammessengerMediaController() {
        cleanupPlayer(true, true);
    }

    public AudioInfo getAudioInfo() {
        return this.audioInfo;
    }

    public void setPlaybackOrderType(int type) {
        boolean oldShuffle = SharedConfig.shuffleMusic;
        SharedConfig.setPlaybackOrderType(type);
        if (oldShuffle == SharedConfig.shuffleMusic) {
            return;
        }
        if (SharedConfig.shuffleMusic) {
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

    public boolean isCurrentPlayer(VideoPlayer player) {
        return this.videoPlayer == player || this.audioPlayer == player;
    }

    /* renamed from: pauseMessage */
    public boolean m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MessageObject messageObject) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
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
                    public void onAnimationEnd(Animator animation) {
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
            return false;
        }
    }

    private boolean resumeAudio(MessageObject messageObject) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
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
            return false;
        }
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
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || (messageObject2 = this.playingMessageObject) == null) {
            return false;
        }
        if (messageObject2.eventId != 0 && this.playingMessageObject.eventId == messageObject.eventId) {
            return !this.downloadingCurrentMessage;
        }
        if (isSamePlayingMessage(messageObject)) {
            return !this.downloadingCurrentMessage;
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

    public void setReplyingMessage(MessageObject replyToMsg, MessageObject replyToTopMsg) {
        this.recordReplyingMsg = replyToMsg;
        this.recordReplyingTopMsg = replyToTopMsg;
    }

    public void requestAudioFocus(boolean request) {
        if (request) {
            if (!this.hasRecordAudioFocus && SharedConfig.pauseMusicOnRecord && NotificationsController.audioManager.requestAudioFocus(this.audioRecordFocusChangedListener, 3, 2) == 1) {
                this.hasRecordAudioFocus = true;
            }
        } else if (this.hasRecordAudioFocus) {
            NotificationsController.audioManager.abandonAudioFocus(this.audioRecordFocusChangedListener);
            this.hasRecordAudioFocus = false;
        }
    }

    public void startRecording(int currentAccount, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, int guid) {
        boolean paused;
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject == null || !isPlayingMessage(messageObject) || isMessagePaused()) {
            paused = false;
        } else {
            paused = true;
        }
        requestAudioFocus(true);
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        MediaController$$ExternalSyntheticLambda13 mediaController$$ExternalSyntheticLambda13 = new MediaController$$ExternalSyntheticLambda13(this, currentAccount, guid, dialogId, replyToMsg, replyToTopMsg);
        this.recordStartRunnable = mediaController$$ExternalSyntheticLambda13;
        dispatchQueue.postRunnable(mediaController$$ExternalSyntheticLambda13, paused ? 500 : 50);
    }

    /* renamed from: lambda$startRecording$26$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m108lambda$startRecording$26$orgtelegrammessengerMediaController(int currentAccount, int guid, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda8(this, currentAccount, guid));
            return;
        }
        this.sendAfterDone = 0;
        TLRPC.TL_document tL_document = new TLRPC.TL_document();
        this.recordingAudio = tL_document;
        this.recordingGuid = guid;
        tL_document.file_reference = new byte[0];
        this.recordingAudio.dc_id = Integer.MIN_VALUE;
        this.recordingAudio.id = (long) SharedConfig.getLastLocalId();
        this.recordingAudio.user_id = UserConfig.getInstance(currentAccount).getClientUserId();
        this.recordingAudio.mime_type = "audio/ogg";
        this.recordingAudio.file_reference = new byte[0];
        SharedConfig.saveConfig();
        File file = new File(FileLoader.getDirectory(4), FileLoader.getAttachFileName(this.recordingAudio));
        this.recordingAudioFile = file;
        try {
            if (startRecord(file.getAbsolutePath(), 16000) == 0) {
                AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda9(this, currentAccount, guid));
                return;
            }
            this.audioRecorder = new AudioRecord(0, this.sampleRate, 16, 2, this.recordBufferSize);
            this.recordStartTime = System.currentTimeMillis();
            this.recordTimeCount = 0;
            this.samplesCount = 0;
            this.recordDialogId = dialogId;
            this.recordingCurrentAccount = currentAccount;
            this.recordReplyingMsg = replyToMsg;
            this.recordReplyingTopMsg = replyToTopMsg;
            this.fileBuffer.rewind();
            this.audioRecorder.startRecording();
            this.recordQueue.postRunnable(this.recordRunnable);
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda12(this, currentAccount, guid));
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
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda11(this, currentAccount, guid));
        }
    }

    /* renamed from: lambda$startRecording$22$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m104lambda$startRecording$22$orgtelegrammessengerMediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(guid));
    }

    /* renamed from: lambda$startRecording$23$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m105lambda$startRecording$23$orgtelegrammessengerMediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(guid));
    }

    /* renamed from: lambda$startRecording$24$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m106lambda$startRecording$24$orgtelegrammessengerMediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(guid));
    }

    /* renamed from: lambda$startRecording$25$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m107lambda$startRecording$25$orgtelegrammessengerMediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(guid), true);
    }

    public void generateWaveform(MessageObject messageObject) {
        String id = messageObject.getId() + "_" + messageObject.getDialogId();
        String path = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(id)) {
            this.generatingWaveform.put(id, messageObject);
            Utilities.globalQueue.postRunnable(new MediaController$$ExternalSyntheticLambda17(this, path, id, messageObject));
        }
    }

    /* renamed from: lambda$generateWaveform$28$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m88x7812ae4c(String path, String id, MessageObject messageObject) {
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda18(this, id, getWaveform(path), messageObject));
    }

    /* renamed from: lambda$generateWaveform$27$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m87x20f4bd6d(String id, byte[] waveform, MessageObject messageObject) {
        MessageObject messageObject1 = this.generatingWaveform.remove(id);
        if (messageObject1 != null && waveform != null && messageObject1.getDocument() != null) {
            int a = 0;
            while (true) {
                if (a >= messageObject1.getDocument().attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute = messageObject1.getDocument().attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    attribute.waveform = waveform;
                    attribute.flags |= 4;
                    break;
                }
                a++;
            }
            TLRPC.TL_messages_messages messagesRes = new TLRPC.TL_messages_messages();
            messagesRes.messages.add(messageObject1.messageOwner);
            MessagesStorage.getInstance(messageObject1.currentAccount).putMessages((TLRPC.messages_Messages) messagesRes, messageObject1.getDialogId(), -1, 0, false, messageObject.scheduled);
            ArrayList<MessageObject> arrayList = new ArrayList<>();
            arrayList.add(messageObject1);
            NotificationCenter.getInstance(messageObject1.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject1.getDialogId()), arrayList);
        }
    }

    /* access modifiers changed from: private */
    public void stopRecordingInternal(int send, boolean notify, int scheduleDate) {
        if (send != 0) {
            this.fileEncodingQueue.postRunnable(new MediaController$$ExternalSyntheticLambda25(this, this.recordingAudio, this.recordingAudioFile, send, notify, scheduleDate));
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

    /* renamed from: lambda$stopRecordingInternal$30$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m113xb01e7e15(TLRPC.TL_document audioToSend, File recordingAudioFileToSend, int send, boolean notify, int scheduleDate) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda24(this, audioToSend, recordingAudioFileToSend, send, notify, scheduleDate));
    }

    /* renamed from: lambda$stopRecordingInternal$29$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m112x338bcaeb(TLRPC.TL_document audioToSend, File recordingAudioFileToSend, int send, boolean notify, int scheduleDate) {
        boolean z;
        char c;
        long duration;
        TLRPC.TL_document tL_document = audioToSend;
        int i = send;
        tL_document.date = ConnectionsManager.getInstance(this.recordingCurrentAccount).getCurrentTime();
        tL_document.size = (int) recordingAudioFileToSend.length();
        TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
        attributeAudio.voice = true;
        short[] sArr = this.recordSamples;
        attributeAudio.waveform = getWaveform2(sArr, sArr.length);
        if (attributeAudio.waveform != null) {
            attributeAudio.flags |= 4;
        }
        long duration2 = this.recordTimeCount;
        attributeAudio.duration = (int) (this.recordTimeCount / 1000);
        tL_document.attributes.add(attributeAudio);
        if (duration2 > 700) {
            if (i == 1) {
                duration = duration2;
                c = 1;
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio = attributeAudio;
                SendMessagesHelper.getInstance(this.recordingCurrentAccount).sendMessage(audioToSend, (VideoEditedInfo) null, recordingAudioFileToSend.getAbsolutePath(), this.recordDialogId, this.recordReplyingMsg, this.recordReplyingTopMsg, (String) null, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate, 0, (Object) null, (MessageObject.SendAnimationData) null);
            } else {
                duration = duration2;
                c = 1;
            }
            NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
            int i2 = NotificationCenter.audioDidSent;
            Object[] objArr = new Object[3];
            z = false;
            objArr[0] = Integer.valueOf(this.recordingGuid);
            String str = null;
            int i3 = send;
            objArr[c] = i3 == 2 ? audioToSend : null;
            if (i3 == 2) {
                str = recordingAudioFileToSend.getAbsolutePath();
            }
            objArr[2] = str;
            instance.postNotificationName(i2, objArr);
            long j = duration;
        } else {
            int i4 = i;
            z = false;
            NotificationCenter.getInstance(this.recordingCurrentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), false, Integer.valueOf((int) duration2));
            recordingAudioFileToSend.delete();
        }
        requestAudioFocus(z);
    }

    public void stopRecording(int send, boolean notify, int scheduleDate) {
        Runnable runnable = this.recordStartRunnable;
        if (runnable != null) {
            this.recordQueue.cancelRunnable(runnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new MediaController$$ExternalSyntheticLambda15(this, send, notify, scheduleDate));
    }

    /* renamed from: lambda$stopRecording$32$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m111lambda$stopRecording$32$orgtelegrammessengerMediaController(int send, boolean notify, int scheduleDate) {
        if (this.sendAfterDone == 3) {
            this.sendAfterDone = 0;
            stopRecordingInternal(send, notify, scheduleDate);
            return;
        }
        AudioRecord audioRecord = this.audioRecorder;
        if (audioRecord != null) {
            try {
                this.sendAfterDone = send;
                this.sendAfterDoneNotify = notify;
                this.sendAfterDoneScheduleDate = scheduleDate;
                audioRecord.stop();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                File file = this.recordingAudioFile;
                if (file != null) {
                    file.delete();
                }
            }
            if (send == 0) {
                stopRecordingInternal(0, false, 0);
            }
            try {
                this.feedbackView.performHapticFeedback(3, 2);
            } catch (Exception e2) {
            }
            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda7(this, send));
        }
    }

    /* renamed from: lambda$stopRecording$31$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m110lambda$stopRecording$31$orgtelegrammessengerMediaController(int send) {
        NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
        int i = NotificationCenter.recordStopped;
        Object[] objArr = new Object[2];
        int i2 = 0;
        objArr[0] = Integer.valueOf(this.recordingGuid);
        if (send == 2) {
            i2 = 1;
        }
        objArr[1] = Integer.valueOf(i2);
        instance.postNotificationName(i, objArr);
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

        public MediaLoader(Context context, AccountInstance accountInstance, ArrayList<MessageObject> messages, MessagesStorage.IntCallback onFinish) {
            this.currentAccount = accountInstance;
            this.messageObjects = messages;
            this.onFinishRunnable = onFinish;
            this.isMusic = messages.get(0).isMusic();
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

        /* renamed from: lambda$new$0$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m768lambda$new$0$orgtelegrammessengerMediaController$MediaLoader(DialogInterface d) {
            this.cancelled = true;
        }

        public void start() {
            AndroidUtilities.runOnUIThread(new MediaController$MediaLoader$$ExternalSyntheticLambda4(this), 250);
            new Thread(new MediaController$MediaLoader$$ExternalSyntheticLambda5(this)).start();
        }

        /* renamed from: lambda$start$1$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m769x745944a8() {
            if (!this.finished) {
                this.progressDialog.show();
            }
        }

        /* renamed from: lambda$start$2$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m770xa231dvar_() {
            File dir;
            String newName;
            try {
                if (this.isMusic) {
                    dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                } else {
                    dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                }
                dir.mkdir();
                int N = this.messageObjects.size();
                for (int b = 0; b < N; b++) {
                    MessageObject message = this.messageObjects.get(b);
                    String name = message.getDocumentName();
                    File destFile = new File(dir, name);
                    if (destFile.exists()) {
                        int idx = name.lastIndexOf(46);
                        int a = 0;
                        while (true) {
                            if (a >= 10) {
                                break;
                            }
                            if (idx != -1) {
                                newName = name.substring(0, idx) + "(" + (a + 1) + ")" + name.substring(idx);
                            } else {
                                newName = name + "(" + (a + 1) + ")";
                            }
                            destFile = new File(dir, newName);
                            if (!destFile.exists()) {
                                break;
                            }
                            a++;
                        }
                    }
                    if (destFile.exists() == 0) {
                        destFile.createNewFile();
                    }
                    String path = message.messageOwner.attachPath;
                    if (path != null && path.length() > 0 && !new File(path).exists()) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(message.messageOwner).toString();
                    }
                    File sourceFile = new File(path);
                    if (!sourceFile.exists()) {
                        this.waitingForFile = new CountDownLatch(1);
                        addMessageToLoad(message);
                        this.waitingForFile.await();
                    }
                    copyFile(sourceFile, destFile, message.getMimeType());
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

        /* renamed from: lambda$checkIfFinished$4$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m763x6aaa6330() {
            try {
                if (this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                } else {
                    this.finished = true;
                }
                if (this.onFinishRunnable != null) {
                    AndroidUtilities.runOnUIThread(new MediaController$MediaLoader$$ExternalSyntheticLambda1(this));
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoaded);
            this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadFailed);
        }

        /* renamed from: lambda$checkIfFinished$3$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m762x3cd1c8d1() {
            this.onFinishRunnable.run(this.copiedFiles);
        }

        private void addMessageToLoad(MessageObject messageObject) {
            AndroidUtilities.runOnUIThread(new MediaController$MediaLoader$$ExternalSyntheticLambda9(this, messageObject));
        }

        /* renamed from: lambda$addMessageToLoad$5$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m761x4d3var_c5(MessageObject messageObject) {
            TLRPC.Document document = messageObject.getDocument();
            if (document != null) {
                this.loadingMessageObjects.put(FileLoader.getAttachFileName(document), messageObject);
                this.currentAccount.getFileLoader().loadFile(document, messageObject, 1, 0);
            }
        }

        /* JADX WARNING: Unknown top exception splitter block from list: {B:83:0x0162=Splitter:B:83:0x0162, B:30:0x0063=Splitter:B:30:0x0063, B:74:0x0154=Splitter:B:74:0x0154, B:110:0x0189=Splitter:B:110:0x0189} */
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
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x018c }
                r3 = r32
                r0.<init>(r3)     // Catch:{ Exception -> 0x018c }
                r4 = r0
                java.nio.channels.FileChannel r0 = r4.getChannel()     // Catch:{ all -> 0x0180 }
                r11 = r0
                java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0172 }
                r12 = r33
                r0.<init>(r12)     // Catch:{ all -> 0x0170 }
                java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ all -> 0x0170 }
                r13 = r0
                long r5 = r11.size()     // Catch:{ all -> 0x0166 }
                r14 = r5
                java.lang.Class<java.io.FileDescriptor> r0 = java.io.FileDescriptor.class
                java.lang.String r5 = "getInt$"
                java.lang.Class[] r6 = new java.lang.Class[r2]     // Catch:{ all -> 0x0068 }
                java.lang.reflect.Method r0 = r0.getDeclaredMethod(r5, r6)     // Catch:{ all -> 0x0068 }
                java.io.FileDescriptor r5 = r4.getFD()     // Catch:{ all -> 0x0068 }
                java.lang.Object[] r6 = new java.lang.Object[r2]     // Catch:{ all -> 0x0068 }
                java.lang.Object r5 = r0.invoke(r5, r6)     // Catch:{ all -> 0x0068 }
                java.lang.Integer r5 = (java.lang.Integer) r5     // Catch:{ all -> 0x0068 }
                int r5 = r5.intValue()     // Catch:{ all -> 0x0068 }
                boolean r6 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r5)     // Catch:{ all -> 0x0068 }
                if (r6 == 0) goto L_0x0067
                org.telegram.ui.ActionBar.AlertDialog r6 = r1.progressDialog     // Catch:{ all -> 0x0068 }
                if (r6 == 0) goto L_0x0058
                org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda3 r6 = new org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda3     // Catch:{ all -> 0x0068 }
                r6.<init>(r1)     // Catch:{ all -> 0x0068 }
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)     // Catch:{ all -> 0x0068 }
            L_0x0058:
                if (r13 == 0) goto L_0x005e
                r13.close()     // Catch:{ all -> 0x0170 }
            L_0x005e:
                if (r11 == 0) goto L_0x0063
                r11.close()     // Catch:{ all -> 0x017e }
            L_0x0063:
                r4.close()     // Catch:{ Exception -> 0x018a }
                return r2
            L_0x0067:
                goto L_0x006c
            L_0x0068:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0166 }
            L_0x006c:
                r5 = 0
                r7 = 0
                r16 = r5
                r9 = r7
            L_0x0073:
                r0 = 1120403456(0x42CLASSNAME, float:100.0)
                int r5 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
                if (r5 >= 0) goto L_0x00c7
                boolean r5 = r1.cancelled     // Catch:{ all -> 0x0166 }
                if (r5 == 0) goto L_0x007e
                goto L_0x00c8
            L_0x007e:
                long r5 = r14 - r9
                r7 = 4096(0x1000, double:2.0237E-320)
                long r18 = java.lang.Math.min(r7, r5)     // Catch:{ all -> 0x0166 }
                r5 = r13
                r6 = r11
                r20 = r7
                r7 = r9
                r2 = r9
                r9 = r18
                r5.transferFrom(r6, r7, r9)     // Catch:{ all -> 0x0166 }
                long r9 = r2 + r20
                int r5 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
                if (r5 >= 0) goto L_0x00a2
                long r5 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0166 }
                r7 = 500(0x1f4, double:2.47E-321)
                long r5 = r5 - r7
                int r7 = (r16 > r5 ? 1 : (r16 == r5 ? 0 : -1))
                if (r7 > 0) goto L_0x00c1
            L_0x00a2:
                long r5 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0166 }
                float r7 = r1.finishedProgress     // Catch:{ all -> 0x0166 }
                java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r1.messageObjects     // Catch:{ all -> 0x0166 }
                int r8 = r8.size()     // Catch:{ all -> 0x0166 }
                float r8 = (float) r8     // Catch:{ all -> 0x0166 }
                float r0 = r0 / r8
                float r8 = (float) r2     // Catch:{ all -> 0x0166 }
                float r0 = r0 * r8
                float r8 = (float) r14     // Catch:{ all -> 0x0166 }
                float r0 = r0 / r8
                float r7 = r7 + r0
                int r0 = (int) r7     // Catch:{ all -> 0x0166 }
                org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda6 r7 = new org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda6     // Catch:{ all -> 0x0166 }
                r7.<init>(r1, r0)     // Catch:{ all -> 0x0166 }
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)     // Catch:{ all -> 0x0166 }
                r16 = r5
            L_0x00c1:
                long r9 = r2 + r20
                r3 = r32
                r2 = 0
                goto L_0x0073
            L_0x00c7:
                r2 = r9
            L_0x00c8:
                boolean r2 = r1.cancelled     // Catch:{ all -> 0x0166 }
                if (r2 != 0) goto L_0x0158
                boolean r2 = r1.isMusic     // Catch:{ all -> 0x0166 }
                if (r2 == 0) goto L_0x00d4
                org.telegram.messenger.AndroidUtilities.addMediaToGallery((java.io.File) r33)     // Catch:{ all -> 0x0166 }
                goto L_0x012d
            L_0x00d4:
                android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0166 }
                java.lang.String r3 = "download"
                java.lang.Object r2 = r2.getSystemService(r3)     // Catch:{ all -> 0x0166 }
                r22 = r2
                android.app.DownloadManager r22 = (android.app.DownloadManager) r22     // Catch:{ all -> 0x0166 }
                r2 = r34
                boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0166 }
                if (r3 == 0) goto L_0x0114
                android.webkit.MimeTypeMap r3 = android.webkit.MimeTypeMap.getSingleton()     // Catch:{ all -> 0x0166 }
                java.lang.String r5 = r33.getName()     // Catch:{ all -> 0x0166 }
                r6 = 46
                int r6 = r5.lastIndexOf(r6)     // Catch:{ all -> 0x0166 }
                r7 = -1
                java.lang.String r8 = "text/plain"
                if (r6 == r7) goto L_0x0113
                int r7 = r6 + 1
                java.lang.String r7 = r5.substring(r7)     // Catch:{ all -> 0x0166 }
                java.lang.String r9 = r7.toLowerCase()     // Catch:{ all -> 0x0166 }
                java.lang.String r9 = r3.getMimeTypeFromExtension(r9)     // Catch:{ all -> 0x0166 }
                r2 = r9
                boolean r9 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0166 }
                if (r9 == 0) goto L_0x0112
                r2 = r8
            L_0x0112:
                goto L_0x0114
            L_0x0113:
                r2 = r8
            L_0x0114:
                java.lang.String r23 = r33.getName()     // Catch:{ all -> 0x0166 }
                java.lang.String r24 = r33.getName()     // Catch:{ all -> 0x0166 }
                r25 = 0
                java.lang.String r27 = r33.getAbsolutePath()     // Catch:{ all -> 0x0166 }
                long r28 = r33.length()     // Catch:{ all -> 0x0166 }
                r30 = 1
                r26 = r2
                r22.addCompletedDownload(r23, r24, r25, r26, r27, r28, r30)     // Catch:{ all -> 0x0166 }
            L_0x012d:
                float r2 = r1.finishedProgress     // Catch:{ all -> 0x0166 }
                java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.messageObjects     // Catch:{ all -> 0x0166 }
                int r3 = r3.size()     // Catch:{ all -> 0x0166 }
                float r3 = (float) r3     // Catch:{ all -> 0x0166 }
                float r0 = r0 / r3
                float r2 = r2 + r0
                r1.finishedProgress = r2     // Catch:{ all -> 0x0166 }
                int r0 = (int) r2     // Catch:{ all -> 0x0166 }
                org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda7 r2 = new org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda7     // Catch:{ all -> 0x0166 }
                r2.<init>(r1, r0)     // Catch:{ all -> 0x0166 }
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x0166 }
                int r2 = r1.copiedFiles     // Catch:{ all -> 0x0166 }
                r3 = 1
                int r2 = r2 + r3
                r1.copiedFiles = r2     // Catch:{ all -> 0x0166 }
                if (r13 == 0) goto L_0x014f
                r13.close()     // Catch:{ all -> 0x0170 }
            L_0x014f:
                if (r11 == 0) goto L_0x0154
                r11.close()     // Catch:{ all -> 0x017e }
            L_0x0154:
                r4.close()     // Catch:{ Exception -> 0x018a }
                return r3
            L_0x0158:
                if (r13 == 0) goto L_0x015d
                r13.close()     // Catch:{ all -> 0x0170 }
            L_0x015d:
                if (r11 == 0) goto L_0x0162
                r11.close()     // Catch:{ all -> 0x017e }
            L_0x0162:
                r4.close()     // Catch:{ Exception -> 0x018a }
                goto L_0x0192
            L_0x0166:
                r0 = move-exception
                r2 = r0
                if (r13 == 0) goto L_0x016f
                r13.close()     // Catch:{ all -> 0x016e }
                goto L_0x016f
            L_0x016e:
                r0 = move-exception
            L_0x016f:
                throw r2     // Catch:{ all -> 0x0170 }
            L_0x0170:
                r0 = move-exception
                goto L_0x0175
            L_0x0172:
                r0 = move-exception
                r12 = r33
            L_0x0175:
                r2 = r0
                if (r11 == 0) goto L_0x017d
                r11.close()     // Catch:{ all -> 0x017c }
                goto L_0x017d
            L_0x017c:
                r0 = move-exception
            L_0x017d:
                throw r2     // Catch:{ all -> 0x017e }
            L_0x017e:
                r0 = move-exception
                goto L_0x0183
            L_0x0180:
                r0 = move-exception
                r12 = r33
            L_0x0183:
                r2 = r0
                r4.close()     // Catch:{ all -> 0x0188 }
                goto L_0x0189
            L_0x0188:
                r0 = move-exception
            L_0x0189:
                throw r2     // Catch:{ Exception -> 0x018a }
            L_0x018a:
                r0 = move-exception
                goto L_0x018f
            L_0x018c:
                r0 = move-exception
                r12 = r33
            L_0x018f:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0192:
                r33.delete()
                r2 = 0
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.MediaLoader.copyFile(java.io.File, java.io.File, java.lang.String):boolean");
        }

        /* renamed from: lambda$copyFile$6$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m764x99201a9a() {
            try {
                this.progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        /* renamed from: lambda$copyFile$7$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m765xc6f8b4f9(int progress) {
            try {
                this.progressDialog.setProgress(progress);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        /* renamed from: lambda$copyFile$8$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m766xf4d14var_(int progress) {
            try {
                this.progressDialog.setProgress(progress);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.fileLoaded || id == NotificationCenter.fileLoadFailed) {
                if (this.loadingMessageObjects.remove(args[0]) != null) {
                    this.waitingForFile.countDown();
                }
            } else if (id == NotificationCenter.fileLoadProgressChanged) {
                if (this.loadingMessageObjects.containsKey(args[0])) {
                    AndroidUtilities.runOnUIThread(new MediaController$MediaLoader$$ExternalSyntheticLambda8(this, (int) (this.finishedProgress + (((((float) args[1].longValue()) / ((float) args[2].longValue())) / ((float) this.messageObjects.size())) * 100.0f))));
                }
            }
        }

        /* renamed from: lambda$didReceivedNotification$9$org-telegram-messenger-MediaController$MediaLoader  reason: not valid java name */
        public /* synthetic */ void m767x85fd3dd7(int progress) {
            try {
                this.progressDialog.setProgress(progress);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void saveFilesFromMessages(Context context, AccountInstance accountInstance, ArrayList<MessageObject> messageObjects, MessagesStorage.IntCallback onSaved) {
        if (messageObjects != null && !messageObjects.isEmpty()) {
            new MediaLoader(context, accountInstance, messageObjects, onSaved).start();
        }
    }

    public static void saveFile(String fullPath, Context context, int type, String name, String mime) {
        saveFile(fullPath, context, type, name, mime, (Runnable) null);
    }

    public static void saveFile(String fullPath, Context context, int type, String name, String mime, Runnable onSaved) {
        File file;
        AlertDialog progressDialog;
        String str = fullPath;
        Context context2 = context;
        if (str != null && context2 != null) {
            if (!TextUtils.isEmpty(fullPath)) {
                File file2 = new File(str);
                if (!file2.exists() || AndroidUtilities.isInternalUri(Uri.fromFile(file2))) {
                    file = null;
                } else {
                    file = file2;
                }
            } else {
                file = null;
            }
            if (file != null) {
                File sourceFile = file;
                boolean[] cancelled = {false};
                if (sourceFile.exists()) {
                    boolean[] finished = new boolean[1];
                    if (!(context2 == null || type == 0)) {
                        try {
                            AlertDialog dialog = new AlertDialog(context2, 2);
                            dialog.setMessage(LocaleController.getString("Loading", NUM));
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(true);
                            dialog.setOnCancelListener(new MediaController$$ExternalSyntheticLambda21(cancelled));
                            AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda33(finished, dialog), 250);
                            progressDialog = dialog;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        AlertDialog alertDialog = progressDialog;
                        MediaController$$ExternalSyntheticLambda39 mediaController$$ExternalSyntheticLambda39 = r2;
                        MediaController$$ExternalSyntheticLambda39 mediaController$$ExternalSyntheticLambda392 = new MediaController$$ExternalSyntheticLambda39(type, sourceFile, context, name, progressDialog, cancelled, mime, onSaved, finished);
                        new Thread(mediaController$$ExternalSyntheticLambda39).start();
                    }
                    progressDialog = null;
                    AlertDialog alertDialog2 = progressDialog;
                    MediaController$$ExternalSyntheticLambda39 mediaController$$ExternalSyntheticLambda393 = mediaController$$ExternalSyntheticLambda392;
                    MediaController$$ExternalSyntheticLambda39 mediaController$$ExternalSyntheticLambda3922 = new MediaController$$ExternalSyntheticLambda39(type, sourceFile, context, name, progressDialog, cancelled, mime, onSaved, finished);
                    new Thread(mediaController$$ExternalSyntheticLambda393).start();
                }
            }
        }
    }

    static /* synthetic */ void lambda$saveFile$33(boolean[] cancelled, DialogInterface d) {
        cancelled[0] = true;
    }

    static /* synthetic */ void lambda$saveFile$34(boolean[] finished, AlertDialog dialog) {
        if (!finished[0]) {
            dialog.show();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:161:0x0317  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x0322  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:114:0x02a0=Splitter:B:114:0x02a0, B:89:0x0244=Splitter:B:89:0x0244, B:137:0x02bf=Splitter:B:137:0x02bf} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$saveFile$38(int r24, java.io.File r25, android.content.Context r26, java.lang.String r27, org.telegram.ui.ActionBar.AlertDialog r28, boolean[] r29, java.lang.String r30, java.lang.Runnable r31, boolean[] r32) {
        /*
            r1 = r24
            r2 = r25
            r3 = r27
            r4 = r28
            r5 = 1
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0311 }
            r6 = 29
            java.lang.String r9 = "Telegram"
            r10 = 1
            if (r0 < r6) goto L_0x0122
            r0 = r24
            android.content.ContentValues r6 = new android.content.ContentValues     // Catch:{ Exception -> 0x011b }
            r6.<init>()     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = r25.getAbsolutePath()     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = android.webkit.MimeTypeMap.getFileExtensionFromUrl(r11)     // Catch:{ Exception -> 0x011b }
            r12 = 0
            if (r11 == 0) goto L_0x002d
            android.webkit.MimeTypeMap r13 = android.webkit.MimeTypeMap.getSingleton()     // Catch:{ Exception -> 0x011b }
            java.lang.String r13 = r13.getMimeTypeFromExtension(r11)     // Catch:{ Exception -> 0x011b }
            r12 = r13
        L_0x002d:
            r13 = 0
            if (r1 == 0) goto L_0x0032
            if (r1 != r10) goto L_0x0046
        L_0x0032:
            if (r12 == 0) goto L_0x0046
            java.lang.String r14 = "image"
            boolean r14 = r12.startsWith(r14)     // Catch:{ Exception -> 0x011b }
            if (r14 == 0) goto L_0x003d
            r0 = 0
        L_0x003d:
            java.lang.String r14 = "video"
            boolean r14 = r12.startsWith(r14)     // Catch:{ Exception -> 0x011b }
            if (r14 == 0) goto L_0x0046
            r0 = 1
        L_0x0046:
            java.lang.String r14 = "mime_type"
            java.lang.String r15 = "_display_name"
            java.lang.String r7 = "relative_path"
            java.lang.String r17 = "external_primary"
            if (r0 != 0) goto L_0x007d
            android.net.Uri r10 = android.provider.MediaStore.Images.Media.getContentUri(r17)     // Catch:{ Exception -> 0x011b }
            java.io.File r13 = new java.io.File     // Catch:{ Exception -> 0x011b }
            java.lang.String r8 = android.os.Environment.DIRECTORY_PICTURES     // Catch:{ Exception -> 0x011b }
            r13.<init>(r8, r9)     // Catch:{ Exception -> 0x011b }
            r8 = r13
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011b }
            r9.<init>()     // Catch:{ Exception -> 0x011b }
            r9.append(r8)     // Catch:{ Exception -> 0x011b }
            java.lang.String r13 = java.io.File.separator     // Catch:{ Exception -> 0x011b }
            r9.append(r13)     // Catch:{ Exception -> 0x011b }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x011b }
            r6.put(r7, r9)     // Catch:{ Exception -> 0x011b }
            r7 = 0
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.generateFileName(r7, r11)     // Catch:{ Exception -> 0x011b }
            r6.put(r15, r7)     // Catch:{ Exception -> 0x011b }
            r6.put(r14, r12)     // Catch:{ Exception -> 0x011b }
            goto L_0x00fa
        L_0x007d:
            if (r0 != r10) goto L_0x00a8
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x011b }
            java.lang.String r10 = android.os.Environment.DIRECTORY_MOVIES     // Catch:{ Exception -> 0x011b }
            r8.<init>(r10, r9)     // Catch:{ Exception -> 0x011b }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011b }
            r9.<init>()     // Catch:{ Exception -> 0x011b }
            r9.append(r8)     // Catch:{ Exception -> 0x011b }
            java.lang.String r10 = java.io.File.separator     // Catch:{ Exception -> 0x011b }
            r9.append(r10)     // Catch:{ Exception -> 0x011b }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x011b }
            r6.put(r7, r9)     // Catch:{ Exception -> 0x011b }
            android.net.Uri r7 = android.provider.MediaStore.Video.Media.getContentUri(r17)     // Catch:{ Exception -> 0x011b }
            r10 = r7
            r7 = 1
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.generateFileName(r7, r11)     // Catch:{ Exception -> 0x011b }
            r6.put(r15, r7)     // Catch:{ Exception -> 0x011b }
            goto L_0x00fa
        L_0x00a8:
            r8 = 2
            if (r0 != r8) goto L_0x00d3
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x011b }
            java.lang.String r10 = android.os.Environment.DIRECTORY_DOWNLOADS     // Catch:{ Exception -> 0x011b }
            r8.<init>(r10, r9)     // Catch:{ Exception -> 0x011b }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011b }
            r9.<init>()     // Catch:{ Exception -> 0x011b }
            r9.append(r8)     // Catch:{ Exception -> 0x011b }
            java.lang.String r10 = java.io.File.separator     // Catch:{ Exception -> 0x011b }
            r9.append(r10)     // Catch:{ Exception -> 0x011b }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x011b }
            r6.put(r7, r9)     // Catch:{ Exception -> 0x011b }
            android.net.Uri r7 = android.provider.MediaStore.Downloads.getContentUri(r17)     // Catch:{ Exception -> 0x011b }
            r10 = r7
            java.lang.String r7 = r25.getName()     // Catch:{ Exception -> 0x011b }
            r6.put(r15, r7)     // Catch:{ Exception -> 0x011b }
            goto L_0x00fa
        L_0x00d3:
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x011b }
            java.lang.String r10 = android.os.Environment.DIRECTORY_MUSIC     // Catch:{ Exception -> 0x011b }
            r8.<init>(r10, r9)     // Catch:{ Exception -> 0x011b }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011b }
            r9.<init>()     // Catch:{ Exception -> 0x011b }
            r9.append(r8)     // Catch:{ Exception -> 0x011b }
            java.lang.String r10 = java.io.File.separator     // Catch:{ Exception -> 0x011b }
            r9.append(r10)     // Catch:{ Exception -> 0x011b }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x011b }
            r6.put(r7, r9)     // Catch:{ Exception -> 0x011b }
            android.net.Uri r7 = android.provider.MediaStore.Audio.Media.getContentUri(r17)     // Catch:{ Exception -> 0x011b }
            r10 = r7
            java.lang.String r7 = r25.getName()     // Catch:{ Exception -> 0x011b }
            r6.put(r15, r7)     // Catch:{ Exception -> 0x011b }
        L_0x00fa:
            r6.put(r14, r12)     // Catch:{ Exception -> 0x011b }
            android.content.ContentResolver r7 = r26.getContentResolver()     // Catch:{ Exception -> 0x011b }
            android.net.Uri r7 = r7.insert(r10, r6)     // Catch:{ Exception -> 0x011b }
            if (r7 == 0) goto L_0x0120
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ Exception -> 0x011b }
            r8.<init>(r2)     // Catch:{ Exception -> 0x011b }
            android.content.ContentResolver r9 = r26.getContentResolver()     // Catch:{ Exception -> 0x011b }
            java.io.OutputStream r9 = r9.openOutputStream(r7)     // Catch:{ Exception -> 0x011b }
            org.telegram.messenger.AndroidUtilities.copyFile((java.io.InputStream) r8, (java.io.OutputStream) r9)     // Catch:{ Exception -> 0x011b }
            r8.close()     // Catch:{ Exception -> 0x011b }
            goto L_0x0120
        L_0x011b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0311 }
            r5 = 0
        L_0x0120:
            goto L_0x0305
        L_0x0122:
            if (r1 != 0) goto L_0x0143
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0311 }
            java.lang.String r6 = android.os.Environment.DIRECTORY_PICTURES     // Catch:{ Exception -> 0x0311 }
            java.io.File r6 = android.os.Environment.getExternalStoragePublicDirectory(r6)     // Catch:{ Exception -> 0x0311 }
            r0.<init>(r6, r9)     // Catch:{ Exception -> 0x0311 }
            r0.mkdirs()     // Catch:{ Exception -> 0x0311 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0311 }
            java.lang.String r7 = org.telegram.messenger.FileLoader.getFileExtension(r25)     // Catch:{ Exception -> 0x0311 }
            r8 = 0
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.generateFileName(r8, r7)     // Catch:{ Exception -> 0x0311 }
            r6.<init>(r0, r7)     // Catch:{ Exception -> 0x0311 }
            r0 = r6
            goto L_0x01e6
        L_0x0143:
            r0 = 1
            if (r1 != r0) goto L_0x0165
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0311 }
            java.lang.String r6 = android.os.Environment.DIRECTORY_MOVIES     // Catch:{ Exception -> 0x0311 }
            java.io.File r6 = android.os.Environment.getExternalStoragePublicDirectory(r6)     // Catch:{ Exception -> 0x0311 }
            r0.<init>(r6, r9)     // Catch:{ Exception -> 0x0311 }
            r0.mkdirs()     // Catch:{ Exception -> 0x0311 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0311 }
            java.lang.String r7 = org.telegram.messenger.FileLoader.getFileExtension(r25)     // Catch:{ Exception -> 0x0311 }
            r8 = 1
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.generateFileName(r8, r7)     // Catch:{ Exception -> 0x0311 }
            r6.<init>(r0, r7)     // Catch:{ Exception -> 0x0311 }
            r0 = r6
            goto L_0x01e6
        L_0x0165:
            r6 = 2
            if (r1 != r6) goto L_0x016f
            java.lang.String r0 = android.os.Environment.DIRECTORY_DOWNLOADS     // Catch:{ Exception -> 0x0311 }
            java.io.File r0 = android.os.Environment.getExternalStoragePublicDirectory(r0)     // Catch:{ Exception -> 0x0311 }
            goto L_0x0175
        L_0x016f:
            java.lang.String r0 = android.os.Environment.DIRECTORY_MUSIC     // Catch:{ Exception -> 0x0311 }
            java.io.File r0 = android.os.Environment.getExternalStoragePublicDirectory(r0)     // Catch:{ Exception -> 0x0311 }
        L_0x0175:
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0311 }
            r6.<init>(r0, r9)     // Catch:{ Exception -> 0x0311 }
            r0 = r6
            r0.mkdirs()     // Catch:{ Exception -> 0x0311 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0311 }
            r6.<init>(r0, r3)     // Catch:{ Exception -> 0x0311 }
            boolean r7 = r6.exists()     // Catch:{ Exception -> 0x0311 }
            if (r7 == 0) goto L_0x01e6
            r7 = 46
            int r7 = r3.lastIndexOf(r7)     // Catch:{ Exception -> 0x0311 }
            r8 = 0
        L_0x0190:
            r9 = 10
            if (r8 >= r9) goto L_0x01e6
            r9 = -1
            java.lang.String r10 = ")"
            java.lang.String r11 = "("
            if (r7 == r9) goto L_0x01bf
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0311 }
            r9.<init>()     // Catch:{ Exception -> 0x0311 }
            r12 = 0
            java.lang.String r13 = r3.substring(r12, r7)     // Catch:{ Exception -> 0x0311 }
            r9.append(r13)     // Catch:{ Exception -> 0x0311 }
            r9.append(r11)     // Catch:{ Exception -> 0x0311 }
            int r11 = r8 + 1
            r9.append(r11)     // Catch:{ Exception -> 0x0311 }
            r9.append(r10)     // Catch:{ Exception -> 0x0311 }
            java.lang.String r10 = r3.substring(r7)     // Catch:{ Exception -> 0x0311 }
            r9.append(r10)     // Catch:{ Exception -> 0x0311 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0311 }
            goto L_0x01d6
        L_0x01bf:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0311 }
            r9.<init>()     // Catch:{ Exception -> 0x0311 }
            r9.append(r3)     // Catch:{ Exception -> 0x0311 }
            r9.append(r11)     // Catch:{ Exception -> 0x0311 }
            int r11 = r8 + 1
            r9.append(r11)     // Catch:{ Exception -> 0x0311 }
            r9.append(r10)     // Catch:{ Exception -> 0x0311 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0311 }
        L_0x01d6:
            java.io.File r10 = new java.io.File     // Catch:{ Exception -> 0x0311 }
            r10.<init>(r0, r9)     // Catch:{ Exception -> 0x0311 }
            r6 = r10
            boolean r10 = r6.exists()     // Catch:{ Exception -> 0x0311 }
            if (r10 != 0) goto L_0x01e3
            goto L_0x01e6
        L_0x01e3:
            int r8 = r8 + 1
            goto L_0x0190
        L_0x01e6:
            boolean r0 = r6.exists()     // Catch:{ Exception -> 0x0311 }
            if (r0 != 0) goto L_0x01ef
            r6.createNewFile()     // Catch:{ Exception -> 0x0311 }
        L_0x01ef:
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0311 }
            r9 = 500(0x1f4, double:2.47E-321)
            long r7 = r7 - r9
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x02c0 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x02c0 }
            r11 = r0
            java.nio.channels.FileChannel r0 = r11.getChannel()     // Catch:{ all -> 0x02b8 }
            r12 = r0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x02ae }
            r0.<init>(r6)     // Catch:{ all -> 0x02ae }
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ all -> 0x02ae }
            r13 = r0
            long r14 = r12.size()     // Catch:{ all -> 0x02a4 }
            java.lang.Class<java.io.FileDescriptor> r0 = java.io.FileDescriptor.class
            java.lang.String r9 = "getInt$"
            r10 = 0
            java.lang.Class[] r2 = new java.lang.Class[r10]     // Catch:{ all -> 0x0249 }
            java.lang.reflect.Method r0 = r0.getDeclaredMethod(r9, r2)     // Catch:{ all -> 0x0249 }
            java.io.FileDescriptor r2 = r11.getFD()     // Catch:{ all -> 0x0249 }
            java.lang.Object[] r9 = new java.lang.Object[r10]     // Catch:{ all -> 0x0249 }
            java.lang.Object r2 = r0.invoke(r2, r9)     // Catch:{ all -> 0x0249 }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ all -> 0x0249 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x0249 }
            boolean r9 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r2)     // Catch:{ all -> 0x0249 }
            if (r9 == 0) goto L_0x0248
            if (r4 == 0) goto L_0x023a
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda29 r9 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda29     // Catch:{ all -> 0x0249 }
            r9.<init>(r4)     // Catch:{ all -> 0x0249 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r9)     // Catch:{ all -> 0x0249 }
        L_0x023a:
            if (r13 == 0) goto L_0x023f
            r13.close()     // Catch:{ all -> 0x02ae }
        L_0x023f:
            if (r12 == 0) goto L_0x0244
            r12.close()     // Catch:{ all -> 0x02b8 }
        L_0x0244:
            r11.close()     // Catch:{ Exception -> 0x02c0 }
            return
        L_0x0248:
            goto L_0x024d
        L_0x0249:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x02a4 }
        L_0x024d:
            r9 = 0
        L_0x024f:
            int r0 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x0296
            r2 = 0
            boolean r0 = r29[r2]     // Catch:{ all -> 0x02a4 }
            if (r0 == 0) goto L_0x0259
            goto L_0x0296
        L_0x0259:
            long r2 = r14 - r9
            r0 = 4096(0x1000, double:2.0237E-320)
            long r22 = java.lang.Math.min(r0, r2)     // Catch:{ all -> 0x02a4 }
            r18 = r13
            r19 = r12
            r20 = r9
            r18.transferFrom(r19, r20, r22)     // Catch:{ all -> 0x02a4 }
            if (r4 == 0) goto L_0x028e
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x02a4 }
            r18 = 500(0x1f4, double:2.47E-321)
            long r2 = r2 - r18
            int r17 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r17 > 0) goto L_0x0290
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x02a4 }
            r7 = r2
            float r2 = (float) r9     // Catch:{ all -> 0x02a4 }
            float r3 = (float) r14     // Catch:{ all -> 0x02a4 }
            float r2 = r2 / r3
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            float r2 = r2 * r3
            int r2 = (int) r2     // Catch:{ all -> 0x02a4 }
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda30 r3 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda30     // Catch:{ all -> 0x02a4 }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x02a4 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ all -> 0x02a4 }
            goto L_0x0290
        L_0x028e:
            r18 = 500(0x1f4, double:2.47E-321)
        L_0x0290:
            long r9 = r9 + r0
            r1 = r24
            r3 = r27
            goto L_0x024f
        L_0x0296:
            if (r13 == 0) goto L_0x029b
            r13.close()     // Catch:{ all -> 0x02ae }
        L_0x029b:
            if (r12 == 0) goto L_0x02a0
            r12.close()     // Catch:{ all -> 0x02b8 }
        L_0x02a0:
            r11.close()     // Catch:{ Exception -> 0x02c0 }
            goto L_0x02c5
        L_0x02a4:
            r0 = move-exception
            r1 = r0
            if (r13 == 0) goto L_0x02ad
            r13.close()     // Catch:{ all -> 0x02ac }
            goto L_0x02ad
        L_0x02ac:
            r0 = move-exception
        L_0x02ad:
            throw r1     // Catch:{ all -> 0x02ae }
        L_0x02ae:
            r0 = move-exception
            r1 = r0
            if (r12 == 0) goto L_0x02b7
            r12.close()     // Catch:{ all -> 0x02b6 }
            goto L_0x02b7
        L_0x02b6:
            r0 = move-exception
        L_0x02b7:
            throw r1     // Catch:{ all -> 0x02b8 }
        L_0x02b8:
            r0 = move-exception
            r1 = r0
            r11.close()     // Catch:{ all -> 0x02be }
            goto L_0x02bf
        L_0x02be:
            r0 = move-exception
        L_0x02bf:
            throw r1     // Catch:{ Exception -> 0x02c0 }
        L_0x02c0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x030d }
            r5 = 0
        L_0x02c5:
            r1 = 0
            boolean r0 = r29[r1]     // Catch:{ Exception -> 0x030d }
            if (r0 == 0) goto L_0x02cf
            r6.delete()     // Catch:{ Exception -> 0x030d }
            r0 = 0
            r5 = r0
        L_0x02cf:
            if (r5 == 0) goto L_0x0303
            r2 = 2
            r1 = r24
            if (r1 != r2) goto L_0x02fb
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0311 }
            java.lang.String r2 = "download"
            java.lang.Object r0 = r0.getSystemService(r2)     // Catch:{ Exception -> 0x0311 }
            r9 = r0
            android.app.DownloadManager r9 = (android.app.DownloadManager) r9     // Catch:{ Exception -> 0x0311 }
            java.lang.String r10 = r6.getName()     // Catch:{ Exception -> 0x0311 }
            java.lang.String r11 = r6.getName()     // Catch:{ Exception -> 0x0311 }
            r12 = 0
            java.lang.String r14 = r6.getAbsolutePath()     // Catch:{ Exception -> 0x0311 }
            long r15 = r6.length()     // Catch:{ Exception -> 0x0311 }
            r17 = 1
            r13 = r30
            r9.addCompletedDownload(r10, r11, r12, r13, r14, r15, r17)     // Catch:{ Exception -> 0x0311 }
            goto L_0x0305
        L_0x02fb:
            java.io.File r0 = r6.getAbsoluteFile()     // Catch:{ Exception -> 0x0311 }
            org.telegram.messenger.AndroidUtilities.addMediaToGallery((java.io.File) r0)     // Catch:{ Exception -> 0x0311 }
            goto L_0x0305
        L_0x0303:
            r1 = r24
        L_0x0305:
            if (r5 == 0) goto L_0x030c
            if (r31 == 0) goto L_0x030c
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r31)     // Catch:{ Exception -> 0x0311 }
        L_0x030c:
            goto L_0x0315
        L_0x030d:
            r0 = move-exception
            r1 = r24
            goto L_0x0312
        L_0x0311:
            r0 = move-exception
        L_0x0312:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0315:
            if (r4 == 0) goto L_0x0322
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda31 r0 = new org.telegram.messenger.MediaController$$ExternalSyntheticLambda31
            r2 = r32
            r0.<init>(r4, r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0324
        L_0x0322:
            r2 = r32
        L_0x0324:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$saveFile$38(int, java.io.File, android.content.Context, java.lang.String, org.telegram.ui.ActionBar.AlertDialog, boolean[], java.lang.String, java.lang.Runnable, boolean[]):void");
    }

    static /* synthetic */ void lambda$saveFile$35(AlertDialog finalProgress) {
        try {
            finalProgress.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$saveFile$36(AlertDialog finalProgress, int progress) {
        try {
            finalProgress.setProgress(progress);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$saveFile$37(AlertDialog finalProgress, boolean[] finished) {
        try {
            if (finalProgress.isShowing()) {
                finalProgress.dismiss();
            } else {
                finished[0] = true;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static String getStickerExt(Uri uri) {
        InputStream inputStream;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
        } catch (Exception e) {
            inputStream = null;
        }
        if (inputStream == null) {
            File file = new File(uri.getPath());
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            }
        }
        byte[] header = new byte[12];
        if (inputStream.read(header, 0, 12) == 12) {
            if (header[0] == -119 && header[1] == 80 && header[2] == 78 && header[3] == 71 && header[4] == 13 && header[5] == 10 && header[6] == 26 && header[7] == 10) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                return "png";
            }
            try {
                if (header[0] == 31 && header[1] == -117) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e22) {
                            FileLog.e((Throwable) e22);
                        }
                    }
                    return "tgs";
                }
                String str = new String(header).toLowerCase();
                if (str.startsWith("riff") && str.endsWith("webp")) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e23) {
                            FileLog.e((Throwable) e23);
                        }
                    }
                    return "webp";
                }
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
                if (inputStream == null) {
                    return null;
                }
                inputStream.close();
                return null;
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e24) {
                        FileLog.e((Throwable) e24);
                    }
                }
                throw th;
            }
        }
        if (inputStream == null) {
            return null;
        }
        try {
            inputStream.close();
            return null;
        } catch (Exception e25) {
            FileLog.e((Throwable) e25);
            return null;
        }
    }

    public static boolean isWebp(Uri uri) {
        InputStream inputStream = null;
        try {
            InputStream inputStream2 = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[12];
            if (inputStream2.read(header, 0, 12) == 12) {
                String str = new String(header).toLowerCase();
                if (str.startsWith("riff") && str.endsWith("webp")) {
                    if (inputStream2 != null) {
                        try {
                            inputStream2.close();
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    }
                    return true;
                }
            }
            if (inputStream2 != null) {
                try {
                    inputStream2.close();
                } catch (Exception e22) {
                    FileLog.e((Throwable) e22);
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e23) {
                    FileLog.e((Throwable) e23);
                }
            }
            throw th;
        }
        return false;
    }

    public static boolean isGif(Uri uri) {
        InputStream inputStream = null;
        try {
            InputStream inputStream2 = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[3];
            if (inputStream2.read(header, 0, 3) != 3 || !new String(header).equalsIgnoreCase("gif")) {
                if (inputStream2 != null) {
                    try {
                        inputStream2.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                return false;
            }
            if (inputStream2 != null) {
                try {
                    inputStream2.close();
                } catch (Exception e22) {
                    FileLog.e((Throwable) e22);
                }
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e23) {
                    FileLog.e((Throwable) e23);
                }
            }
            throw th;
        }
    }

    public static String getFileName(Uri uri) {
        Cursor cursor;
        if (uri == null) {
            return "";
        }
        String result = null;
        try {
            if (uri.getScheme().equals("content")) {
                try {
                    cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{"_display_name"}, (String) null, (String[]) null, (String) null);
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex("_display_name"));
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                } catch (Throwable th) {
                }
            }
            if (result != null) {
                return result;
            }
            String result2 = uri.getPath();
            int cut = result2.lastIndexOf(47);
            if (cut != -1) {
                return result2.substring(cut + 1);
            }
            return result2;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return "";
        }
        throw th;
    }

    public static String copyFileToCache(Uri uri, String ext) {
        return copyFileToCache(uri, ext, -1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:78:?, code lost:
        r5 = r4.getAbsolutePath();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0124, code lost:
        if (r1 == null) goto L_0x0133;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x012a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x012b, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0157 A[SYNTHETIC, Splitter:B:103:0x0157] */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0165 A[SYNTHETIC, Splitter:B:108:0x0165] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0182 A[SYNTHETIC, Splitter:B:120:0x0182] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0190 A[SYNTHETIC, Splitter:B:125:0x0190] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String copyFileToCache(android.net.Uri r15, java.lang.String r16, long r17) {
        /*
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            java.lang.String r0 = getFileName(r15)     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            java.lang.String r0 = org.telegram.messenger.FileLoader.fixFileName(r0)     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            r8 = 0
            if (r0 != 0) goto L_0x0030
            int r9 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            java.util.Locale r10 = java.util.Locale.US     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            java.lang.String r11 = "%d.%s"
            r12 = 2
            java.lang.Object[] r12 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            r12[r8] = r13     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            r13 = 1
            r12[r13] = r16     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            java.lang.String r10 = java.lang.String.format(r10, r11, r12)     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            r0 = r10
            r9 = r0
            goto L_0x0031
        L_0x0030:
            r9 = r0
        L_0x0031:
            java.io.File r0 = org.telegram.messenger.AndroidUtilities.getSharingDirectory()     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            r4 = r0
            r4.mkdirs()     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            r0.<init>(r4, r9)     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            r4 = r0
            android.net.Uri r0 = android.net.Uri.fromFile(r4)     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            if (r0 == 0) goto L_0x0073
            if (r1 == 0) goto L_0x0057
            r1.close()     // Catch:{ Exception -> 0x0050 }
            goto L_0x0057
        L_0x0050:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0058
        L_0x0057:
        L_0x0058:
            if (r2 == 0) goto L_0x0065
            r2.close()     // Catch:{ Exception -> 0x005e }
            goto L_0x0065
        L_0x005e:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0066
        L_0x0065:
        L_0x0066:
            int r0 = (r17 > r6 ? 1 : (r17 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x0072
            long r6 = (long) r3
            int r0 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x0072
            r4.delete()
        L_0x0072:
            return r5
        L_0x0073:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ Exception -> 0x0150, all -> 0x014c }
            r10 = r15
            java.io.InputStream r0 = r0.openInputStream(r15)     // Catch:{ Exception -> 0x014a }
            r1 = r0
            boolean r0 = r1 instanceof java.io.FileInputStream     // Catch:{ Exception -> 0x014a }
            if (r0 == 0) goto L_0x00d9
            r0 = r1
            java.io.FileInputStream r0 = (java.io.FileInputStream) r0     // Catch:{ Exception -> 0x014a }
            r11 = r0
            java.lang.Class<java.io.FileDescriptor> r0 = java.io.FileDescriptor.class
            java.lang.String r12 = "getInt$"
            java.lang.Class[] r13 = new java.lang.Class[r8]     // Catch:{ all -> 0x00d5 }
            java.lang.reflect.Method r0 = r0.getDeclaredMethod(r12, r13)     // Catch:{ all -> 0x00d5 }
            r12 = r0
            java.io.FileDescriptor r0 = r11.getFD()     // Catch:{ all -> 0x00d5 }
            java.lang.Object[] r13 = new java.lang.Object[r8]     // Catch:{ all -> 0x00d5 }
            java.lang.Object r0 = r12.invoke(r0, r13)     // Catch:{ all -> 0x00d5 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x00d5 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x00d5 }
            r13 = r0
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r13)     // Catch:{ all -> 0x00d5 }
            if (r0 == 0) goto L_0x00d4
            if (r1 == 0) goto L_0x00b8
            r1.close()     // Catch:{ Exception -> 0x00b1 }
            goto L_0x00b8
        L_0x00b1:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x00b9
        L_0x00b8:
        L_0x00b9:
            if (r2 == 0) goto L_0x00c6
            r2.close()     // Catch:{ Exception -> 0x00bf }
            goto L_0x00c6
        L_0x00bf:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x00c7
        L_0x00c6:
        L_0x00c7:
            int r0 = (r17 > r6 ? 1 : (r17 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x00d3
            long r6 = (long) r3
            int r0 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x00d3
            r4.delete()
        L_0x00d3:
            return r5
        L_0x00d4:
            goto L_0x00d9
        L_0x00d5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x014a }
        L_0x00d9:
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x014a }
            r0.<init>(r4)     // Catch:{ Exception -> 0x014a }
            r2 = r0
            r0 = 20480(0x5000, float:2.8699E-41)
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x014a }
            r11 = r0
        L_0x00e4:
            int r0 = r1.read(r11)     // Catch:{ Exception -> 0x014a }
            r12 = r0
            r13 = -1
            if (r0 == r13) goto L_0x0120
            r2.write(r11, r8, r12)     // Catch:{ Exception -> 0x014a }
            int r3 = r3 + r12
            int r0 = (r17 > r6 ? 1 : (r17 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x00e4
            long r13 = (long) r3
            int r0 = (r13 > r17 ? 1 : (r13 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x00e4
            if (r1 == 0) goto L_0x0107
            r1.close()     // Catch:{ Exception -> 0x0100 }
            goto L_0x0107
        L_0x0100:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0108
        L_0x0107:
        L_0x0108:
            r2.close()     // Catch:{ Exception -> 0x010d }
            goto L_0x0113
        L_0x010d:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0113:
            int r0 = (r17 > r6 ? 1 : (r17 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x011f
            long r6 = (long) r3
            int r0 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x011f
            r4.delete()
        L_0x011f:
            return r5
        L_0x0120:
            java.lang.String r5 = r4.getAbsolutePath()     // Catch:{ Exception -> 0x014a }
            if (r1 == 0) goto L_0x0131
            r1.close()     // Catch:{ Exception -> 0x012a }
            goto L_0x0131
        L_0x012a:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0132
        L_0x0131:
        L_0x0132:
            r2.close()     // Catch:{ Exception -> 0x0137 }
            goto L_0x013d
        L_0x0137:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x013d:
            int r0 = (r17 > r6 ? 1 : (r17 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x0149
            long r6 = (long) r3
            int r0 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x0149
            r4.delete()
        L_0x0149:
            return r5
        L_0x014a:
            r0 = move-exception
            goto L_0x0152
        L_0x014c:
            r0 = move-exception
            r10 = r15
        L_0x014e:
            r5 = r0
            goto L_0x0180
        L_0x0150:
            r0 = move-exception
            r10 = r15
        L_0x0152:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x017e }
            if (r1 == 0) goto L_0x0162
            r1.close()     // Catch:{ Exception -> 0x015b }
            goto L_0x0162
        L_0x015b:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0163
        L_0x0162:
        L_0x0163:
            if (r2 == 0) goto L_0x0170
            r2.close()     // Catch:{ Exception -> 0x0169 }
            goto L_0x0170
        L_0x0169:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0171
        L_0x0170:
        L_0x0171:
            int r0 = (r17 > r6 ? 1 : (r17 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x017d
            long r6 = (long) r3
            int r0 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x017d
            r4.delete()
        L_0x017d:
            return r5
        L_0x017e:
            r0 = move-exception
            goto L_0x014e
        L_0x0180:
            if (r1 == 0) goto L_0x018d
            r1.close()     // Catch:{ Exception -> 0x0186 }
            goto L_0x018d
        L_0x0186:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x018e
        L_0x018d:
        L_0x018e:
            if (r2 == 0) goto L_0x019b
            r2.close()     // Catch:{ Exception -> 0x0194 }
            goto L_0x019b
        L_0x0194:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x019c
        L_0x019b:
        L_0x019c:
            int r0 = (r17 > r6 ? 1 : (r17 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x01a8
            long r6 = (long) r3
            int r0 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x01a8
            r4.delete()
        L_0x01a8:
            goto L_0x01aa
        L_0x01a9:
            throw r5
        L_0x01aa:
            goto L_0x01a9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.copyFileToCache(android.net.Uri, java.lang.String, long):java.lang.String");
    }

    public static void loadGalleryPhotosAlbums(int guid) {
        Thread thread = new Thread(new MediaController$$ExternalSyntheticLambda38(guid));
        thread.setPriority(1);
        thread.start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:135:0x02dd  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02eb A[SYNTHETIC, Splitter:B:137:0x02eb] */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0310 A[SYNTHETIC, Splitter:B:148:0x0310] */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0325 A[SYNTHETIC, Splitter:B:157:0x0325] */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0356 A[Catch:{ all -> 0x0511 }] */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0359 A[Catch:{ all -> 0x0511 }] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0073 A[SYNTHETIC, Splitter:B:16:0x0073] */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x036c A[SYNTHETIC, Splitter:B:171:0x036c] */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x04fc  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0506 A[SYNTHETIC, Splitter:B:254:0x0506] */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x051b A[SYNTHETIC, Splitter:B:263:0x051b] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0533 A[LOOP:2: B:268:0x052d->B:270:0x0533, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00c6 A[SYNTHETIC, Splitter:B:32:0x00c6] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$loadGalleryPhotosAlbums$40(int r51) {
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
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r13 = r0
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r21 = r0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x005d }
            r0.<init>()     // Catch:{ Exception -> 0x005d }
            java.lang.String r20 = android.os.Environment.DIRECTORY_DCIM     // Catch:{ Exception -> 0x005d }
            java.io.File r20 = android.os.Environment.getExternalStoragePublicDirectory(r20)     // Catch:{ Exception -> 0x005d }
            r22 = r11
            java.lang.String r11 = r20.getAbsolutePath()     // Catch:{ Exception -> 0x005b }
            r0.append(r11)     // Catch:{ Exception -> 0x005b }
            java.lang.String r11 = "/Camera/"
            r0.append(r11)     // Catch:{ Exception -> 0x005b }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x005b }
            r19 = r0
            r11 = r19
            goto L_0x0065
        L_0x005b:
            r0 = move-exception
            goto L_0x0060
        L_0x005d:
            r0 = move-exception
            r22 = r11
        L_0x0060:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r11 = r19
        L_0x0065:
            r19 = 0
            r20 = 0
            r23 = 0
            r24 = r12
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0300 }
            r12 = 23
            if (r0 < r12) goto L_0x0095
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0088 }
            int r0 = r0.checkSelfPermission(r10)     // Catch:{ all -> 0x0088 }
            if (r0 != 0) goto L_0x007c
            goto L_0x0095
        L_0x007c:
            r36 = r2
            r30 = r5
            r31 = r6
            r34 = r10
            r12 = r21
            goto L_0x02e9
        L_0x0088:
            r0 = move-exception
            r36 = r2
            r30 = r5
            r31 = r6
            r34 = r10
            r12 = r21
            goto L_0x030b
        L_0x0095:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0300 }
            android.content.ContentResolver r28 = r0.getContentResolver()     // Catch:{ all -> 0x0300 }
            android.net.Uri r29 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x0300 }
            java.lang.String[] r30 = projectionPhotos     // Catch:{ all -> 0x0300 }
            r31 = 0
            r32 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0300 }
            r0.<init>()     // Catch:{ all -> 0x0300 }
            int r12 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0300 }
            r34 = r10
            r10 = 28
            if (r12 <= r10) goto L_0x00b3
            r10 = r22
            goto L_0x00b5
        L_0x00b3:
            r10 = r24
        L_0x00b5:
            r0.append(r10)     // Catch:{ all -> 0x02f6 }
            r0.append(r9)     // Catch:{ all -> 0x02f6 }
            java.lang.String r33 = r0.toString()     // Catch:{ all -> 0x02f6 }
            android.database.Cursor r0 = android.provider.MediaStore.Images.Media.query(r28, r29, r30, r31, r32, r33)     // Catch:{ all -> 0x02f6 }
            r10 = r0
            if (r10 == 0) goto L_0x02dd
            int r0 = r10.getColumnIndex(r8)     // Catch:{ all -> 0x02cf }
            int r12 = r10.getColumnIndex(r7)     // Catch:{ all -> 0x02cf }
            int r23 = r10.getColumnIndex(r6)     // Catch:{ all -> 0x02cf }
            r28 = r23
            int r23 = r10.getColumnIndex(r5)     // Catch:{ all -> 0x02cf }
            r29 = r23
            r30 = r5
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x02cb }
            r31 = r6
            r6 = 28
            if (r5 <= r6) goto L_0x00e7
            r5 = r22
            goto L_0x00e9
        L_0x00e7:
            r5 = r24
        L_0x00e9:
            int r5 = r10.getColumnIndex(r5)     // Catch:{ all -> 0x02c7 }
            java.lang.String r6 = "orientation"
            int r6 = r10.getColumnIndex(r6)     // Catch:{ all -> 0x02c7 }
            int r23 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x02c7 }
            r32 = r23
            int r23 = r10.getColumnIndex(r3)     // Catch:{ all -> 0x02c7 }
            r33 = r23
            int r23 = r10.getColumnIndex(r2)     // Catch:{ all -> 0x02c7 }
            r35 = r23
        L_0x0105:
            boolean r23 = r10.moveToNext()     // Catch:{ all -> 0x02c7 }
            if (r23 == 0) goto L_0x02b4
            r36 = r2
            r2 = r29
            java.lang.String r23 = r10.getString(r2)     // Catch:{ all -> 0x02b2 }
            r29 = r23
            boolean r23 = android.text.TextUtils.isEmpty(r29)     // Catch:{ all -> 0x02b2 }
            if (r23 == 0) goto L_0x0120
            r29 = r2
            r2 = r36
            goto L_0x0105
        L_0x0120:
            int r39 = r10.getInt(r0)     // Catch:{ all -> 0x02b2 }
            int r23 = r10.getInt(r12)     // Catch:{ all -> 0x02b2 }
            r49 = r23
            r23 = r0
            r0 = r28
            java.lang.String r28 = r10.getString(r0)     // Catch:{ all -> 0x02b2 }
            r50 = r28
            long r40 = r10.getLong(r5)     // Catch:{ all -> 0x02b2 }
            int r43 = r10.getInt(r6)     // Catch:{ all -> 0x02b2 }
            r28 = r0
            r0 = r32
            int r45 = r10.getInt(r0)     // Catch:{ all -> 0x02b2 }
            r32 = r0
            r0 = r33
            int r46 = r10.getInt(r0)     // Catch:{ all -> 0x02b2 }
            r33 = r0
            r0 = r35
            long r47 = r10.getLong(r0)     // Catch:{ all -> 0x02b2 }
            org.telegram.messenger.MediaController$PhotoEntry r35 = new org.telegram.messenger.MediaController$PhotoEntry     // Catch:{ all -> 0x02b2 }
            r44 = 0
            r37 = r35
            r38 = r49
            r42 = r29
            r37.<init>(r38, r39, r40, r42, r43, r44, r45, r46, r47)     // Catch:{ all -> 0x02b2 }
            r37 = r35
            if (r16 != 0) goto L_0x0193
            r35 = r0
            org.telegram.messenger.MediaController$AlbumEntry r0 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x018c }
            r38 = r2
            java.lang.String r2 = "AllPhotos"
            r42 = r5
            r5 = 2131624257(0x7f0e0141, float:1.8875689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x018c }
            r5 = r37
            r37 = r6
            r6 = 0
            r0.<init>(r6, r2, r5)     // Catch:{ all -> 0x018c }
            r2 = r0
            r14.add(r6, r2)     // Catch:{ all -> 0x0183 }
            goto L_0x019f
        L_0x0183:
            r0 = move-exception
            r16 = r2
            r23 = r10
            r12 = r21
            goto L_0x030b
        L_0x018c:
            r0 = move-exception
            r23 = r10
            r12 = r21
            goto L_0x030b
        L_0x0193:
            r35 = r0
            r38 = r2
            r42 = r5
            r5 = r37
            r37 = r6
            r2 = r16
        L_0x019f:
            if (r18 != 0) goto L_0x01d4
            org.telegram.messenger.MediaController$AlbumEntry r0 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x01c9 }
            r44 = r10
            r6 = 2131624256(0x7f0e0140, float:1.8875687E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x01c0 }
            r6 = 0
            r0.<init>(r6, r10, r5)     // Catch:{ all -> 0x01c0 }
            r10 = r0
            r15.add(r6, r10)     // Catch:{ all -> 0x01b5 }
            goto L_0x01d8
        L_0x01b5:
            r0 = move-exception
            r16 = r2
            r18 = r10
            r12 = r21
            r23 = r44
            goto L_0x030b
        L_0x01c0:
            r0 = move-exception
            r16 = r2
            r12 = r21
            r23 = r44
            goto L_0x030b
        L_0x01c9:
            r0 = move-exception
            r44 = r10
            r16 = r2
            r12 = r21
            r23 = r44
            goto L_0x030b
        L_0x01d4:
            r44 = r10
            r10 = r18
        L_0x01d8:
            r2.addPhoto(r5)     // Catch:{ all -> 0x02a5 }
            r10.addPhoto(r5)     // Catch:{ all -> 0x02a5 }
            r0 = r49
            java.lang.Object r6 = r13.get(r0)     // Catch:{ all -> 0x02a5 }
            org.telegram.messenger.MediaController$AlbumEntry r6 = (org.telegram.messenger.MediaController.AlbumEntry) r6     // Catch:{ all -> 0x02a5 }
            if (r6 != 0) goto L_0x0234
            r16 = r2
            org.telegram.messenger.MediaController$AlbumEntry r2 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0229 }
            r18 = r6
            r6 = r50
            r2.<init>(r0, r6, r5)     // Catch:{ all -> 0x0229 }
            r13.put(r0, r2)     // Catch:{ all -> 0x0229 }
            if (r19 != 0) goto L_0x0216
            if (r11 == 0) goto L_0x0216
            r49 = r10
            r10 = r29
            if (r10 == 0) goto L_0x0213
            boolean r18 = r10.startsWith(r11)     // Catch:{ all -> 0x0220 }
            if (r18 == 0) goto L_0x0213
            r29 = r12
            r12 = 0
            r15.add(r12, r2)     // Catch:{ all -> 0x0220 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0220 }
            r19 = r12
            goto L_0x0242
        L_0x0213:
            r29 = r12
            goto L_0x021c
        L_0x0216:
            r49 = r10
            r10 = r29
            r29 = r12
        L_0x021c:
            r15.add(r2)     // Catch:{ all -> 0x0220 }
            goto L_0x0242
        L_0x0220:
            r0 = move-exception
            r12 = r21
            r23 = r44
            r18 = r49
            goto L_0x030b
        L_0x0229:
            r0 = move-exception
            r49 = r10
            r12 = r21
            r23 = r44
            r18 = r49
            goto L_0x030b
        L_0x0234:
            r16 = r2
            r18 = r6
            r49 = r10
            r10 = r29
            r6 = r50
            r29 = r12
            r2 = r18
        L_0x0242:
            r2.addPhoto(r5)     // Catch:{ all -> 0x029c }
            r12 = r21
            java.lang.Object r18 = r12.get(r0)     // Catch:{ all -> 0x0295 }
            org.telegram.messenger.MediaController$AlbumEntry r18 = (org.telegram.messenger.MediaController.AlbumEntry) r18     // Catch:{ all -> 0x0295 }
            r2 = r18
            if (r2 != 0) goto L_0x027a
            r18 = r2
            org.telegram.messenger.MediaController$AlbumEntry r2 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0295 }
            r2.<init>(r0, r6, r5)     // Catch:{ all -> 0x0295 }
            r12.put(r0, r2)     // Catch:{ all -> 0x0295 }
            if (r20 != 0) goto L_0x0274
            if (r11 == 0) goto L_0x0274
            if (r10 == 0) goto L_0x0274
            boolean r18 = r10.startsWith(r11)     // Catch:{ all -> 0x0295 }
            if (r18 == 0) goto L_0x0274
            r21 = r6
            r6 = 0
            r14.add(r6, r2)     // Catch:{ all -> 0x0295 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0295 }
            r20 = r6
            goto L_0x027e
        L_0x0274:
            r21 = r6
            r14.add(r2)     // Catch:{ all -> 0x0295 }
            goto L_0x027e
        L_0x027a:
            r18 = r2
            r21 = r6
        L_0x027e:
            r2.addPhoto(r5)     // Catch:{ all -> 0x0295 }
            r21 = r12
            r0 = r23
            r12 = r29
            r2 = r36
            r6 = r37
            r29 = r38
            r5 = r42
            r10 = r44
            r18 = r49
            goto L_0x0105
        L_0x0295:
            r0 = move-exception
            r23 = r44
            r18 = r49
            goto L_0x030b
        L_0x029c:
            r0 = move-exception
            r12 = r21
            r23 = r44
            r18 = r49
            goto L_0x030b
        L_0x02a5:
            r0 = move-exception
            r16 = r2
            r49 = r10
            r12 = r21
            r23 = r44
            r18 = r49
            goto L_0x030b
        L_0x02b2:
            r0 = move-exception
            goto L_0x02d6
        L_0x02b4:
            r23 = r0
            r36 = r2
            r42 = r5
            r37 = r6
            r44 = r10
            r38 = r29
            r29 = r12
            r12 = r21
            r23 = r44
            goto L_0x02e9
        L_0x02c7:
            r0 = move-exception
            r36 = r2
            goto L_0x02d6
        L_0x02cb:
            r0 = move-exception
            r36 = r2
            goto L_0x02d4
        L_0x02cf:
            r0 = move-exception
            r36 = r2
            r30 = r5
        L_0x02d4:
            r31 = r6
        L_0x02d6:
            r44 = r10
            r12 = r21
            r23 = r44
            goto L_0x030b
        L_0x02dd:
            r36 = r2
            r30 = r5
            r31 = r6
            r44 = r10
            r12 = r21
            r23 = r44
        L_0x02e9:
            if (r23 == 0) goto L_0x031b
            r23.close()     // Catch:{ Exception -> 0x02ef }
            goto L_0x0313
        L_0x02ef:
            r0 = move-exception
            r2 = r0
            r0 = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0313
        L_0x02f6:
            r0 = move-exception
            r36 = r2
            r30 = r5
            r31 = r6
            r12 = r21
            goto L_0x030b
        L_0x0300:
            r0 = move-exception
            r36 = r2
            r30 = r5
            r31 = r6
            r34 = r10
            r12 = r21
        L_0x030b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x056a }
            if (r23 == 0) goto L_0x031b
            r23.close()     // Catch:{ Exception -> 0x0314 }
        L_0x0313:
            goto L_0x031b
        L_0x0314:
            r0 = move-exception
            r2 = r0
            r0 = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0313
        L_0x031b:
            r2 = r16
            r5 = r20
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0511 }
            r6 = 23
            if (r0 < r6) goto L_0x033d
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0336 }
            r6 = r34
            int r0 = r0.checkSelfPermission(r6)     // Catch:{ all -> 0x0336 }
            if (r0 != 0) goto L_0x0330
            goto L_0x033d
        L_0x0330:
            r21 = r5
            r22 = r12
            goto L_0x0504
        L_0x0336:
            r0 = move-exception
            r21 = r5
            r22 = r12
            goto L_0x0516
        L_0x033d:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0511 }
            android.content.ContentResolver r37 = r0.getContentResolver()     // Catch:{ all -> 0x0511 }
            android.net.Uri r38 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x0511 }
            java.lang.String[] r39 = projectionVideo     // Catch:{ all -> 0x0511 }
            r40 = 0
            r41 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0511 }
            r0.<init>()     // Catch:{ all -> 0x0511 }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0511 }
            r10 = 28
            if (r6 <= r10) goto L_0x0359
            r6 = r22
            goto L_0x035b
        L_0x0359:
            r6 = r24
        L_0x035b:
            r0.append(r6)     // Catch:{ all -> 0x0511 }
            r0.append(r9)     // Catch:{ all -> 0x0511 }
            java.lang.String r42 = r0.toString()     // Catch:{ all -> 0x0511 }
            android.database.Cursor r0 = android.provider.MediaStore.Images.Media.query(r37, r38, r39, r40, r41, r42)     // Catch:{ all -> 0x0511 }
            r6 = r0
            if (r6 == 0) goto L_0x04fc
            int r0 = r6.getColumnIndex(r8)     // Catch:{ all -> 0x04f2 }
            int r7 = r6.getColumnIndex(r7)     // Catch:{ all -> 0x04f2 }
            r8 = r31
            int r8 = r6.getColumnIndex(r8)     // Catch:{ all -> 0x04f2 }
            r9 = r30
            int r9 = r6.getColumnIndex(r9)     // Catch:{ all -> 0x04f2 }
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x04f2 }
            r21 = r5
            r5 = 28
            if (r10 <= r5) goto L_0x038b
            r5 = r22
            goto L_0x038d
        L_0x038b:
            r5 = r24
        L_0x038d:
            int r5 = r6.getColumnIndex(r5)     // Catch:{ all -> 0x04ea }
            java.lang.String r10 = "duration"
            int r10 = r6.getColumnIndex(r10)     // Catch:{ all -> 0x04ea }
            int r4 = r6.getColumnIndex(r4)     // Catch:{ all -> 0x04ea }
            int r3 = r6.getColumnIndex(r3)     // Catch:{ all -> 0x04ea }
            r22 = r12
            r12 = r36
            int r12 = r6.getColumnIndex(r12)     // Catch:{ all -> 0x04e4 }
        L_0x03a7:
            boolean r16 = r6.moveToNext()     // Catch:{ all -> 0x04e4 }
            if (r16 == 0) goto L_0x04d7
            java.lang.String r16 = r6.getString(r9)     // Catch:{ all -> 0x04e4 }
            r20 = r16
            boolean r16 = android.text.TextUtils.isEmpty(r20)     // Catch:{ all -> 0x04e4 }
            if (r16 == 0) goto L_0x03ba
            goto L_0x03a7
        L_0x03ba:
            int r30 = r6.getInt(r0)     // Catch:{ all -> 0x04e4 }
            int r16 = r6.getInt(r7)     // Catch:{ all -> 0x04e4 }
            r23 = r16
            java.lang.String r16 = r6.getString(r8)     // Catch:{ all -> 0x04e4 }
            r24 = r16
            long r31 = r6.getLong(r5)     // Catch:{ all -> 0x04e4 }
            long r26 = r6.getLong(r10)     // Catch:{ all -> 0x04e4 }
            int r36 = r6.getInt(r4)     // Catch:{ all -> 0x04e4 }
            int r37 = r6.getInt(r3)     // Catch:{ all -> 0x04e4 }
            long r38 = r6.getLong(r12)     // Catch:{ all -> 0x04e4 }
            org.telegram.messenger.MediaController$PhotoEntry r16 = new org.telegram.messenger.MediaController$PhotoEntry     // Catch:{ all -> 0x04e4 }
            r28 = 1000(0x3e8, double:4.94E-321)
            r41 = r3
            r40 = r4
            long r3 = r26 / r28
            int r4 = (int) r3     // Catch:{ all -> 0x04e4 }
            r35 = 1
            r28 = r16
            r29 = r23
            r33 = r20
            r34 = r4
            r28.<init>(r29, r30, r31, r33, r34, r35, r36, r37, r38)     // Catch:{ all -> 0x04e4 }
            r3 = r16
            if (r17 != 0) goto L_0x0429
            org.telegram.messenger.MediaController$AlbumEntry r4 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0424 }
            r16 = r0
            java.lang.String r0 = "AllVideos"
            r28 = r5
            r5 = 2131624258(0x7f0e0142, float:1.887569E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r5)     // Catch:{ all -> 0x0424 }
            r5 = 0
            r4.<init>(r5, r0, r3)     // Catch:{ all -> 0x0424 }
            r0 = 1
            r4.videoOnly = r0     // Catch:{ all -> 0x041d }
            r0 = 0
            if (r18 == 0) goto L_0x0415
            int r0 = r0 + 1
        L_0x0415:
            if (r2 == 0) goto L_0x0419
            int r0 = r0 + 1
        L_0x0419:
            r15.add(r0, r4)     // Catch:{ all -> 0x041d }
            goto L_0x042f
        L_0x041d:
            r0 = move-exception
            r17 = r4
            r23 = r6
            goto L_0x0516
        L_0x0424:
            r0 = move-exception
            r23 = r6
            goto L_0x0516
        L_0x0429:
            r16 = r0
            r28 = r5
            r4 = r17
        L_0x042f:
            if (r18 != 0) goto L_0x045e
            org.telegram.messenger.MediaController$AlbumEntry r0 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0455 }
            r25 = r6
            r5 = 2131624256(0x7f0e0140, float:1.8875687E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x044e }
            r5 = 0
            r0.<init>(r5, r6, r3)     // Catch:{ all -> 0x044e }
            r6 = r0
            r15.add(r5, r6)     // Catch:{ all -> 0x0445 }
            goto L_0x0462
        L_0x0445:
            r0 = move-exception
            r17 = r4
            r18 = r6
            r23 = r25
            goto L_0x0516
        L_0x044e:
            r0 = move-exception
            r17 = r4
            r23 = r25
            goto L_0x0516
        L_0x0455:
            r0 = move-exception
            r25 = r6
            r17 = r4
            r23 = r25
            goto L_0x0516
        L_0x045e:
            r25 = r6
            r6 = r18
        L_0x0462:
            r4.addPhoto(r3)     // Catch:{ all -> 0x04cd }
            r6.addPhoto(r3)     // Catch:{ all -> 0x04cd }
            r0 = r23
            java.lang.Object r5 = r13.get(r0)     // Catch:{ all -> 0x04cd }
            org.telegram.messenger.MediaController$AlbumEntry r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5     // Catch:{ all -> 0x04cd }
            if (r5 != 0) goto L_0x04a7
            r29 = r1
            org.telegram.messenger.MediaController$AlbumEntry r1 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x04cd }
            r23 = r4
            r4 = r24
            r1.<init>(r0, r4, r3)     // Catch:{ all -> 0x04c5 }
            r5 = r1
            r13.put(r0, r5)     // Catch:{ all -> 0x04c5 }
            if (r19 != 0) goto L_0x04a0
            if (r11 == 0) goto L_0x04a0
            r1 = r20
            if (r1 == 0) goto L_0x049c
            boolean r18 = r1.startsWith(r11)     // Catch:{ all -> 0x04c5 }
            if (r18 == 0) goto L_0x049c
            r18 = r1
            r1 = 0
            r15.add(r1, r5)     // Catch:{ all -> 0x04c5 }
            java.lang.Integer r20 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x04c5 }
            r19 = r20
            goto L_0x04b0
        L_0x049c:
            r18 = r1
            r1 = 0
            goto L_0x04a3
        L_0x04a0:
            r18 = r20
            r1 = 0
        L_0x04a3:
            r15.add(r5)     // Catch:{ all -> 0x04c5 }
            goto L_0x04b0
        L_0x04a7:
            r29 = r1
            r23 = r4
            r18 = r20
            r4 = r24
            r1 = 0
        L_0x04b0:
            r5.addPhoto(r3)     // Catch:{ all -> 0x04c5 }
            r18 = r6
            r0 = r16
            r17 = r23
            r6 = r25
            r5 = r28
            r1 = r29
            r4 = r40
            r3 = r41
            goto L_0x03a7
        L_0x04c5:
            r0 = move-exception
            r18 = r6
            r17 = r23
            r23 = r25
            goto L_0x0516
        L_0x04cd:
            r0 = move-exception
            r23 = r4
            r18 = r6
            r17 = r23
            r23 = r25
            goto L_0x0516
        L_0x04d7:
            r16 = r0
            r41 = r3
            r40 = r4
            r28 = r5
            r25 = r6
            r23 = r25
            goto L_0x0504
        L_0x04e4:
            r0 = move-exception
            r25 = r6
            r23 = r25
            goto L_0x0516
        L_0x04ea:
            r0 = move-exception
            r25 = r6
            r22 = r12
            r23 = r25
            goto L_0x0516
        L_0x04f2:
            r0 = move-exception
            r21 = r5
            r25 = r6
            r22 = r12
            r23 = r25
            goto L_0x0516
        L_0x04fc:
            r21 = r5
            r25 = r6
            r22 = r12
            r23 = r25
        L_0x0504:
            if (r23 == 0) goto L_0x0526
            r23.close()     // Catch:{ Exception -> 0x050a }
            goto L_0x051e
        L_0x050a:
            r0 = move-exception
            r1 = r0
            r0 = r1
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x051e
        L_0x0511:
            r0 = move-exception
            r21 = r5
            r22 = r12
        L_0x0516:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0558 }
            if (r23 == 0) goto L_0x0526
            r23.close()     // Catch:{ Exception -> 0x051f }
        L_0x051e:
            goto L_0x0526
        L_0x051f:
            r0 = move-exception
            r1 = r0
            r0 = r1
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x051e
        L_0x0526:
            r0 = r17
            r1 = r18
            r3 = r19
            r4 = 0
        L_0x052d:
            int r5 = r15.size()
            if (r4 >= r5) goto L_0x0543
            java.lang.Object r5 = r15.get(r4)
            org.telegram.messenger.MediaController$AlbumEntry r5 = (org.telegram.messenger.MediaController.AlbumEntry) r5
            java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r5 = r5.photos
            org.telegram.messenger.MediaController$$ExternalSyntheticLambda34 r6 = org.telegram.messenger.MediaController$$ExternalSyntheticLambda34.INSTANCE
            java.util.Collections.sort(r5, r6)
            int r4 = r4 + 1
            goto L_0x052d
        L_0x0543:
            r20 = 0
            r4 = r13
            r13 = r51
            r5 = r14
            r14 = r15
            r6 = r15
            r15 = r5
            r16 = r3
            r17 = r1
            r18 = r2
            r19 = r0
            broadcastNewPhotos(r13, r14, r15, r16, r17, r18, r19, r20)
            return
        L_0x0558:
            r0 = move-exception
            r4 = r13
            r5 = r14
            r6 = r15
            r1 = r0
            if (r23 == 0) goto L_0x0569
            r23.close()     // Catch:{ Exception -> 0x0563 }
            goto L_0x0569
        L_0x0563:
            r0 = move-exception
            r3 = r0
            r0 = r3
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0569:
            throw r1
        L_0x056a:
            r0 = move-exception
            r22 = r12
            r4 = r13
            r5 = r14
            r6 = r15
            r1 = r0
            if (r23 == 0) goto L_0x057d
            r23.close()     // Catch:{ Exception -> 0x0577 }
            goto L_0x057d
        L_0x0577:
            r0 = move-exception
            r2 = r0
            r0 = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x057d:
            goto L_0x057f
        L_0x057e:
            throw r1
        L_0x057f:
            goto L_0x057e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$loadGalleryPhotosAlbums$40(int):void");
    }

    static /* synthetic */ int lambda$loadGalleryPhotosAlbums$39(PhotoEntry o1, PhotoEntry o2) {
        if (o1.dateTaken < o2.dateTaken) {
            return 1;
        }
        if (o1.dateTaken > o2.dateTaken) {
            return -1;
        }
        return 0;
    }

    private static void broadcastNewPhotos(int guid, ArrayList<AlbumEntry> mediaAlbumsSorted, ArrayList<AlbumEntry> photoAlbumsSorted, Integer cameraAlbumIdFinal, AlbumEntry allMediaAlbumFinal, AlbumEntry allPhotosAlbumFinal, AlbumEntry allVideosAlbumFinal, int delay) {
        Runnable runnable = broadcastPhotosRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        MediaController$$ExternalSyntheticLambda40 mediaController$$ExternalSyntheticLambda40 = new MediaController$$ExternalSyntheticLambda40(guid, mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal, allMediaAlbumFinal, allPhotosAlbumFinal, allVideosAlbumFinal);
        broadcastPhotosRunnable = mediaController$$ExternalSyntheticLambda40;
        AndroidUtilities.runOnUIThread(mediaController$$ExternalSyntheticLambda40, (long) delay);
    }

    static /* synthetic */ void lambda$broadcastNewPhotos$41(int guid, ArrayList mediaAlbumsSorted, ArrayList photoAlbumsSorted, Integer cameraAlbumIdFinal, AlbumEntry allMediaAlbumFinal, AlbumEntry allPhotosAlbumFinal, AlbumEntry allVideosAlbumFinal) {
        if (PhotoViewer.getInstance().isVisible()) {
            broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal, allMediaAlbumFinal, allPhotosAlbumFinal, allVideosAlbumFinal, 1000);
            return;
        }
        allMediaAlbums = mediaAlbumsSorted;
        allPhotoAlbums = photoAlbumsSorted;
        broadcastPhotosRunnable = null;
        allPhotosAlbumEntry = allPhotosAlbumFinal;
        allMediaAlbumEntry = allMediaAlbumFinal;
        allVideosAlbumEntry = allVideosAlbumFinal;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.albumsDidLoad, Integer.valueOf(guid), mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal);
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
        this.videoConvertQueue.add(new VideoConvertMessage(messageObject, messageObject.videoEditedInfo));
        if (this.videoConvertQueue.size() == 1) {
            startVideoConvertFromQueue();
        }
        return true;
    }

    public void cancelVideoConvert(MessageObject messageObject) {
        if (messageObject != null && !this.videoConvertQueue.isEmpty()) {
            int a = 0;
            while (a < this.videoConvertQueue.size()) {
                VideoConvertMessage videoConvertMessage = this.videoConvertQueue.get(a);
                MessageObject object = videoConvertMessage.messageObject;
                if (!object.equals(messageObject) || object.currentAccount != messageObject.currentAccount) {
                    a++;
                } else if (a == 0) {
                    synchronized (this.videoConvertSync) {
                        videoConvertMessage.videoEditedInfo.canceled = true;
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
            int a = 0;
            while (true) {
                if (a >= messageObject.messageOwner.media.document.attributes.size()) {
                    break;
                } else if (messageObject.messageOwner.media.document.attributes.get(a) instanceof TLRPC.TL_documentAttributeAnimated) {
                    intent.putExtra("gif", true);
                    break;
                } else {
                    a++;
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
        VideoConvertRunnable.runConversion(videoConvertMessage);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0023, code lost:
        r1 = r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.media.MediaCodecInfo selectCodec(java.lang.String r10) {
        /*
            int r0 = android.media.MediaCodecList.getCodecCount()
            r1 = 0
            r2 = 0
        L_0x0006:
            if (r2 >= r0) goto L_0x0042
            android.media.MediaCodecInfo r3 = android.media.MediaCodecList.getCodecInfoAt(r2)
            boolean r4 = r3.isEncoder()
            if (r4 != 0) goto L_0x0013
            goto L_0x003f
        L_0x0013:
            java.lang.String[] r4 = r3.getSupportedTypes()
            int r5 = r4.length
            r6 = 0
        L_0x0019:
            if (r6 >= r5) goto L_0x003f
            r7 = r4[r6]
            boolean r8 = r7.equalsIgnoreCase(r10)
            if (r8 == 0) goto L_0x003c
            r1 = r3
            java.lang.String r8 = r1.getName()
            if (r8 == 0) goto L_0x003c
            java.lang.String r9 = "OMX.SEC.avc.enc"
            boolean r9 = r8.equals(r9)
            if (r9 != 0) goto L_0x0033
            return r1
        L_0x0033:
            java.lang.String r9 = "OMX.SEC.AVC.Encoder"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x003c
            return r1
        L_0x003c:
            int r6 = r6 + 1
            goto L_0x0019
        L_0x003f:
            int r2 = r2 + 1
            goto L_0x0006
        L_0x0042:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.selectCodec(java.lang.String):android.media.MediaCodecInfo");
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

    public static int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
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

    public static int findTrack(MediaExtractor extractor, boolean audio) {
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

    /* access modifiers changed from: private */
    public void didWriteData(VideoConvertMessage message, File file, boolean last, long lastFrameTimestamp, long availableSize, boolean error, float progress) {
        VideoConvertMessage videoConvertMessage = message;
        boolean firstWrite = videoConvertMessage.videoEditedInfo.videoConvertFirstWrite;
        if (firstWrite) {
            videoConvertMessage.videoEditedInfo.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new MediaController$$ExternalSyntheticLambda26(this, error, last, message, file, progress, lastFrameTimestamp, firstWrite, availableSize));
    }

    /* renamed from: lambda$didWriteData$42$org-telegram-messenger-MediaController  reason: not valid java name */
    public /* synthetic */ void m86lambda$didWriteData$42$orgtelegrammessengerMediaController(boolean error, boolean last, VideoConvertMessage message, File file, float progress, long lastFrameTimestamp, boolean firstWrite, long availableSize) {
        VideoConvertMessage videoConvertMessage = message;
        if (error || last) {
            synchronized (this.videoConvertSync) {
                videoConvertMessage.videoEditedInfo.canceled = false;
            }
            this.videoConvertQueue.remove(message);
            startVideoConvertFromQueue();
        }
        if (error) {
            NotificationCenter.getInstance(videoConvertMessage.currentAccount).postNotificationName(NotificationCenter.filePreparingFailed, videoConvertMessage.messageObject, file.toString(), Float.valueOf(progress), Long.valueOf(lastFrameTimestamp));
            return;
        }
        if (firstWrite) {
            NotificationCenter.getInstance(videoConvertMessage.currentAccount).postNotificationName(NotificationCenter.filePreparingStarted, videoConvertMessage.messageObject, file.toString(), Float.valueOf(progress), Long.valueOf(lastFrameTimestamp));
        }
        NotificationCenter instance = NotificationCenter.getInstance(videoConvertMessage.currentAccount);
        int i = NotificationCenter.fileNewChunkAvailable;
        Object[] objArr = new Object[6];
        objArr[0] = videoConvertMessage.messageObject;
        objArr[1] = file.toString();
        objArr[2] = Long.valueOf(availableSize);
        objArr[3] = Long.valueOf(last ? file.length() : 0);
        objArr[4] = Float.valueOf(progress);
        objArr[5] = Long.valueOf(lastFrameTimestamp);
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
                MessageObject currentMessageObject = this.playingMessageObject;
                cleanupPlayer(false, false);
                playMessage(currentMessageObject);
                return;
            }
            this.audioPlayer.play();
        }
    }

    private static class VideoConvertRunnable implements Runnable {
        private VideoConvertMessage convertMessage;

        private VideoConvertRunnable(VideoConvertMessage message) {
            this.convertMessage = message;
        }

        public void run() {
            boolean unused = MediaController.getInstance().convertVideo(this.convertMessage);
        }

        public static void runConversion(VideoConvertMessage obj) {
            new Thread(new MediaController$VideoConvertRunnable$$ExternalSyntheticLambda0(obj)).start();
        }

        static /* synthetic */ void lambda$runConversion$0(VideoConvertMessage obj) {
            try {
                Thread th = new Thread(new VideoConvertRunnable(obj), "VideoConvertRunnable");
                th.start();
                th.join();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean convertVideo(VideoConvertMessage convertMessage) {
        int originalBitrate;
        String videoPath;
        long duration;
        long endTime;
        final VideoEditedInfo info;
        int framerate;
        int resultHeight;
        int resultWidth;
        boolean canceled;
        boolean canceled2;
        VideoConvertMessage videoConvertMessage = convertMessage;
        MessageObject messageObject = videoConvertMessage.messageObject;
        VideoEditedInfo info2 = videoConvertMessage.videoEditedInfo;
        if (messageObject == null) {
            VideoEditedInfo videoEditedInfo = info2;
            return false;
        } else if (info2 == null) {
            MessageObject messageObject2 = messageObject;
            VideoEditedInfo videoEditedInfo2 = info2;
            return false;
        } else {
            String videoPath2 = info2.originalPath;
            long startTime = info2.startTime;
            long avatarStartTime = info2.avatarStartTime;
            long endTime2 = info2.endTime;
            int resultWidth2 = info2.resultWidth;
            int resultHeight2 = info2.resultHeight;
            int rotationValue = info2.rotationValue;
            int originalWidth = info2.originalWidth;
            int originalHeight = info2.originalHeight;
            int framerate2 = info2.framerate;
            int bitrate = info2.bitrate;
            int bitrate2 = info2.originalBitrate;
            boolean isSecret = DialogObject.isEncryptedDialog(messageObject.getDialogId());
            VideoEditedInfo info3 = info2;
            int originalBitrate2 = bitrate2;
            final File cacheFile = new File(messageObject.messageOwner.attachPath);
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder sb = new StringBuilder();
                MessageObject messageObject3 = messageObject;
                sb.append("begin convert ");
                sb.append(videoPath2);
                sb.append(" startTime = ");
                sb.append(startTime);
                sb.append(" avatarStartTime = ");
                sb.append(avatarStartTime);
                sb.append(" endTime ");
                sb.append(endTime2);
                sb.append(" rWidth = ");
                sb.append(resultWidth2);
                sb.append(" rHeight = ");
                sb.append(resultHeight2);
                sb.append(" rotation = ");
                sb.append(rotationValue);
                sb.append(" oWidth = ");
                sb.append(originalWidth);
                sb.append(" oHeight = ");
                sb.append(originalHeight);
                sb.append(" framerate = ");
                sb.append(framerate2);
                sb.append(" bitrate = ");
                sb.append(bitrate);
                sb.append(" originalBitrate = ");
                originalBitrate = originalBitrate2;
                sb.append(originalBitrate);
                FileLog.d(sb.toString());
            } else {
                originalBitrate = originalBitrate2;
            }
            if (videoPath2 == null) {
                videoPath = "";
            } else {
                videoPath = videoPath2;
            }
            if (startTime > 0 && endTime2 > 0) {
                duration = endTime2 - startTime;
                info = info3;
                endTime = endTime2;
            } else if (endTime2 > 0) {
                duration = endTime2;
                info = info3;
                endTime = endTime2;
            } else if (startTime > 0) {
                info = info3;
                endTime = endTime2;
                duration = info.originalDuration - startTime;
            } else {
                info = info3;
                endTime = endTime2;
                duration = info.originalDuration;
            }
            if (framerate2 == 0) {
                framerate = 25;
            } else {
                framerate = framerate2;
            }
            if (rotationValue == 90 || rotationValue == 270) {
                resultWidth = resultHeight2;
                resultHeight = resultWidth2;
            } else {
                resultWidth = resultWidth2;
                resultHeight = resultHeight2;
            }
            boolean needCompress = (avatarStartTime == -1 && info.cropState == null && info.mediaEntities == null && info.paintPath == null && info.filterState == null && resultWidth == originalWidth && resultHeight == originalHeight && rotationValue == 0 && !info.roundVideo && startTime == -1) ? false : true;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("videoconvert", 0);
            long time = System.currentTimeMillis();
            final VideoConvertMessage videoConvertMessage2 = convertMessage;
            VideoConvertorListener callback = new VideoConvertorListener() {
                private long lastAvailableSize = 0;

                public boolean checkConversionCanceled() {
                    return info.canceled;
                }

                public void didWriteData(long availableSize, float progress) {
                    if (!info.canceled) {
                        if (availableSize < 0) {
                            availableSize = cacheFile.length();
                        }
                        if (info.needUpdateProgress || this.lastAvailableSize != availableSize) {
                            this.lastAvailableSize = availableSize;
                            MediaController.this.didWriteData(videoConvertMessage2, cacheFile, false, 0, availableSize, false, progress);
                        }
                    }
                }
            };
            info.videoConvertFirstWrite = true;
            int originalWidth2 = originalWidth;
            MediaCodecVideoConvertor videoConvertor = new MediaCodecVideoConvertor();
            boolean error = videoConvertor.convertVideo(videoPath, cacheFile, rotationValue, isSecret, originalWidth2, originalHeight, resultWidth, resultHeight, framerate, bitrate, originalBitrate, startTime, endTime, avatarStartTime, needCompress, duration, info.filterState, info.paintPath, info.mediaEntities, info.isPhoto, info.cropState, callback);
            boolean canceled3 = info.canceled;
            if (!canceled3) {
                boolean z = canceled3;
                synchronized (this.videoConvertSync) {
                    canceled2 = info.canceled;
                }
                canceled = canceled2;
            } else {
                canceled = canceled3;
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder sb2 = new StringBuilder();
                VideoEditedInfo videoEditedInfo3 = info;
                sb2.append("time=");
                int i = rotationValue;
                sb2.append(System.currentTimeMillis() - time);
                sb2.append(" canceled=");
                sb2.append(canceled);
                FileLog.d(sb2.toString());
            } else {
                int i2 = rotationValue;
            }
            preferences.edit().putBoolean("isPreviousOk", true).apply();
            int i3 = resultWidth;
            int i4 = resultHeight;
            long j = endTime;
            long lastFrameTimestamp = videoConvertor.getLastFrameTimestamp();
            long j2 = avatarStartTime;
            long j3 = startTime;
            didWriteData(convertMessage, cacheFile, true, lastFrameTimestamp, cacheFile.length(), error || canceled, 1.0f);
            return true;
        }
    }

    public static int getVideoBitrate(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int bitrate = 0;
        try {
            retriever.setDataSource(path);
            bitrate = Integer.parseInt(retriever.extractMetadata(20));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        retriever.release();
        return bitrate;
    }

    public static int makeVideoBitrate(int originalHeight, int originalWidth, int originalBitrate, int height, int width) {
        float minCompressFactor;
        float compressFactor;
        int maxBitrate;
        if (Math.min(height, width) >= 1080) {
            maxBitrate = 6800000;
            compressFactor = 1.0f;
            minCompressFactor = 1.0f;
        } else if (Math.min(height, width) >= 720) {
            maxBitrate = 3200000;
            compressFactor = 1.0f;
            minCompressFactor = 1.0f;
        } else if (Math.min(height, width) >= 480) {
            maxBitrate = 1000000;
            compressFactor = 0.8f;
            minCompressFactor = 0.9f;
        } else {
            maxBitrate = 750000;
            compressFactor = 0.6f;
            minCompressFactor = 0.7f;
        }
        int remeasuredBitrate = (int) (((float) ((int) (((float) originalBitrate) / Math.min(((float) originalHeight) / ((float) height), ((float) originalWidth) / ((float) width))))) * compressFactor);
        int minBitrate = (int) (((float) getVideoBitrateWithFactor(minCompressFactor)) / (921600.0f / ((float) (width * height))));
        if (originalBitrate < minBitrate) {
            return remeasuredBitrate;
        }
        if (remeasuredBitrate > maxBitrate) {
            return maxBitrate;
        }
        return Math.max(remeasuredBitrate, minBitrate);
    }

    private static int getVideoBitrateWithFactor(float f) {
        return (int) (2000.0f * f * 1000.0f * 1.13f);
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

        public PlaylistGlobalSearchParams(String query2, long dialogId2, long minDate2, long maxDate2, FiltersView.MediaFilterData filter2) {
            this.filter = filter2;
            this.query = query2;
            this.dialogId = dialogId2;
            this.minDate = minDate2;
            this.maxDate = maxDate2;
        }
    }

    public boolean currentPlaylistIsGlobalSearch() {
        return this.playlistGlobalSearchParams != null;
    }
}
