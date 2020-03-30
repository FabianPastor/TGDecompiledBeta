package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
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
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
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
    AudioManager.OnAudioFocusChangeListener audioRecordFocusChangedListener = new AudioManager.OnAudioFocusChangeListener() {
        public final void onAudioFocusChange(int i) {
            MediaController.this.lambda$new$0$MediaController(i);
        }
    };
    /* access modifiers changed from: private */
    public AudioRecord audioRecorder;
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
    public ExternalObserver externalObserver;
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
    private MessageObject recordReplyingMessageObject;
    /* access modifiers changed from: private */
    public Runnable recordRunnable = new Runnable() {
        /* JADX WARNING: Removed duplicated region for block: B:36:0x00d5 A[SYNTHETIC, Splitter:B:36:0x00d5] */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x0115  */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x0118  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x00e9 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r18 = this;
                r1 = r18
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                android.media.AudioRecord r0 = r0.audioRecorder
                if (r0 == 0) goto L_0x0165
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
                if (r4 <= 0) goto L_0x013e
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
                if (r4 == 0) goto L_0x0126
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                org.telegram.messenger.DispatchQueue r0 = r0.fileEncodingQueue
                org.telegram.messenger.-$$Lambda$MediaController$2$FRC3BsVbfkarri1fq2pbdFf8in8 r4 = new org.telegram.messenger.-$$Lambda$MediaController$2$FRC3BsVbfkarri1fq2pbdFf8in8
                r4.<init>(r3, r2)
                r0.postRunnable(r4)
            L_0x0126:
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                org.telegram.messenger.DispatchQueue r0 = r0.recordQueue
                org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.this
                java.lang.Runnable r2 = r2.recordRunnable
                r0.postRunnable(r2)
                org.telegram.messenger.-$$Lambda$MediaController$2$c_rZm06Vbt5svAWMsWH6lgBDhHI r0 = new org.telegram.messenger.-$$Lambda$MediaController$2$c_rZm06Vbt5svAWMsWH6lgBDhHI
                r0.<init>(r5)
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                goto L_0x0165
            L_0x013e:
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                r0.add(r3)
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                int r0 = r0.sendAfterDone
                r2 = 3
                if (r0 == r2) goto L_0x0165
                org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.this
                int r2 = r0.sendAfterDone
                org.telegram.messenger.MediaController r3 = org.telegram.messenger.MediaController.this
                boolean r3 = r3.sendAfterDoneNotify
                org.telegram.messenger.MediaController r4 = org.telegram.messenger.MediaController.this
                int r4 = r4.sendAfterDoneScheduleDate
                r0.stopRecordingInternal(r2, r3, r4)
            L_0x0165:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.AnonymousClass2.run():void");
        }

        public /* synthetic */ void lambda$run$1$MediaController$2(ByteBuffer byteBuffer, boolean z) {
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
                        long unused = mediaController2.recordTimeCount = mediaController2.recordTimeCount + ((long) ((MediaController.this.fileBuffer.limit() / 2) / (MediaController.this.sampleRate / 1000)));
                    }
                }
                if (i != -1) {
                    byteBuffer.limit(i);
                }
            }
            MediaController.this.recordQueue.postRunnable(new Runnable(byteBuffer) {
                private final /* synthetic */ ByteBuffer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaController.AnonymousClass2.this.lambda$null$0$MediaController$2(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$MediaController$2(ByteBuffer byteBuffer) {
            MediaController.this.recordBuffers.add(byteBuffer);
        }

        public /* synthetic */ void lambda$run$2$MediaController$2(double d) {
            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(MediaController.this.recordingGuid), Double.valueOf(d));
        }
    };
    /* access modifiers changed from: private */
    public short[] recordSamples = new short[1024];
    /* access modifiers changed from: private */
    public Runnable recordStartRunnable;
    private long recordStartTime;
    /* access modifiers changed from: private */
    public long recordTimeCount;
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

    public static class PhotoEntry {
        public int bucketId;
        public boolean canDeleteAfter;
        public CharSequence caption;
        public long dateTaken;
        public int duration;
        public VideoEditedInfo editedInfo;
        public ArrayList<TLRPC$MessageEntity> entities;
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
        public ArrayList<TLRPC$InputDocument> stickers = new ArrayList<>();
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

    public static class SearchImage {
        public CharSequence caption;
        public int date;
        public TLRPC$Document document;
        public ArrayList<TLRPC$MessageEntity> entities;
        public int height;
        public String id;
        public String imagePath;
        public String imageUrl;
        public TLRPC$BotInlineResult inlineResult;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isPainted;
        public HashMap<String, String> params;
        public TLRPC$Photo photo;
        public TLRPC$PhotoSize photoSize;
        public SavedFilterState savedFilterState;
        public int size;
        public ArrayList<TLRPC$InputDocument> stickers = new ArrayList<>();
        public String thumbPath;
        public TLRPC$PhotoSize thumbPhotoSize;
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

    public /* synthetic */ void lambda$new$0$MediaController(int i) {
        if (i != 1) {
            this.hasRecordAudioFocus = false;
        }
    }

    private class VideoConvertMessage {
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

    private class GalleryObserverInternal extends ContentObserver {
        public GalleryObserverInternal() {
            super((Handler) null);
        }

        private void scheduleReloadRunnable() {
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new Runnable() {
                public final void run() {
                    MediaController.GalleryObserverInternal.this.lambda$scheduleReloadRunnable$0$MediaController$GalleryObserverInternal();
                }
            }, 2000);
        }

        public /* synthetic */ void lambda$scheduleReloadRunnable$0$MediaController$GalleryObserverInternal() {
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

    private class GalleryObserverExternal extends ContentObserver {
        public GalleryObserverExternal() {
            super((Handler) null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = $$Lambda$MediaController$GalleryObserverExternal$dwi1SqFQz7StRlsnN0S1Sqd6M.INSTANCE, 2000);
        }

        static /* synthetic */ void lambda$onChange$0() {
            Runnable unused = MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public static void checkGallery() {
        AlbumEntry albumEntry;
        if (Build.VERSION.SDK_INT >= 24 && (albumEntry = allPhotosAlbumEntry) != null) {
            Utilities.globalQueue.postRunnable(new Runnable(albumEntry.photos.size()) {
                private final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    MediaController.lambda$checkGallery$1(this.f$0);
                }
            }, 2000);
        }
    }

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
    static /* synthetic */ void lambda$checkGallery$1(int r13) {
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
        this.recordQueue.postRunnable(new Runnable() {
            public final void run() {
                MediaController.this.lambda$new$2$MediaController();
            }
        });
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                MediaController.this.lambda$new$3$MediaController();
            }
        });
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                MediaController.this.lambda$new$4$MediaController();
            }
        });
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

    public /* synthetic */ void lambda$new$2$MediaController() {
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

    public /* synthetic */ void lambda$new$3$MediaController() {
        try {
            this.currentPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("playbackSpeed", 1.0f);
            this.currentMusicPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("musicPlaybackSpeed", 1.0f);
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
            this.proximityWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            AnonymousClass3 r1 = new PhoneStateListener() {
                public void onCallStateChanged(int i, String str) {
                    AndroidUtilities.runOnUIThread(new Runnable(i) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            MediaController.AnonymousClass3.this.lambda$onCallStateChanged$0$MediaController$3(this.f$1);
                        }
                    });
                }

                public /* synthetic */ void lambda$onCallStateChanged$0$MediaController$3(int i) {
                    if (i == 1) {
                        MediaController mediaController = MediaController.this;
                        if (mediaController.isPlayingMessage(mediaController.playingMessageObject) && !MediaController.this.isMessagePaused()) {
                            MediaController mediaController2 = MediaController.this;
                            mediaController2.lambda$startAudioAgain$7$MediaController(mediaController2.playingMessageObject);
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
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaController.this.lambda$onAudioFocusChange$5$MediaController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onAudioFocusChange$5$MediaController(int i) {
        if (i == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                lambda$startAudioAgain$7$MediaController(this.playingMessageObject);
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
                lambda$startAudioAgain$7$MediaController(this.playingMessageObject);
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
            FileLog.e((Throwable) e);
        }
    }

    private void startProgressTimer(final MessageObject messageObject) {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            messageObject.getFileName();
            Timer timer = new Timer();
            this.progressTimer = timer;
            timer.schedule(new TimerTask() {
                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new Runnable(messageObject) {
                            private final /* synthetic */ MessageObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                MediaController.AnonymousClass4.this.lambda$run$1$MediaController$4(this.f$1);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$run$1$MediaController$4(MessageObject messageObject) {
                    long j;
                    long j2;
                    float f;
                    float f2;
                    if (messageObject == null) {
                        return;
                    }
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
                                    f = j2 >= 0 ? ((float) j) / f3 : 0.0f;
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
                                String unused2 = MediaController.this.shouldSavePositionForCurrentAudio;
                                long unused3 = MediaController.this.lastSaveTime = SystemClock.elapsedRealtime();
                                Utilities.globalQueue.postRunnable(new Runnable(f) {
                                    private final /* synthetic */ float f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void run() {
                                        MediaController.AnonymousClass4.this.lambda$null$0$MediaController$4(this.f$1);
                                    }
                                });
                            }
                            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(messageObject.getId()), Float.valueOf(f));
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
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
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    public void cleanup() {
        cleanupPlayer(false, true);
        this.audioInfo = null;
        this.playMusicAgain = false;
        for (int i = 0; i < 3; i++) {
            DownloadController.getInstance(i).cleanup();
        }
        this.videoConvertQueue.clear();
        this.playlist.clear();
        this.shuffledPlaylist.clear();
        this.generatingWaveform.clear();
        this.voiceMessagesPlaylist = null;
        this.voiceMessagesPlaylistMap = null;
        cancelVideoConvert((MessageObject) null);
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
    /* JADX WARNING: Can't wrap try/catch for region: R(3:36|37|63) */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0052, code lost:
        if (r2.toLowerCase().contains("screenshot") == false) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005e, code lost:
        if (r4.toLowerCase().contains("screenshot") != false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006a, code lost:
        if (r5.toLowerCase().contains("screenshot") != false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        r14.add(java.lang.Long.valueOf(r6));
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:36:0x00a7 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processMediaObserver(android.net.Uri r14) {
        /*
            r13 = this;
            r0 = 0
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ Exception -> 0x00ca }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00ca }
            android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ Exception -> 0x00ca }
            java.lang.String[] r5 = r13.mediaProjections     // Catch:{ Exception -> 0x00ca }
            r6 = 0
            r7 = 0
            java.lang.String r8 = "date_added DESC LIMIT 1"
            r4 = r14
            android.database.Cursor r0 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x00ca }
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ Exception -> 0x00ca }
            r14.<init>()     // Catch:{ Exception -> 0x00ca }
            if (r0 == 0) goto L_0x00b3
        L_0x001d:
            boolean r2 = r0.moveToNext()     // Catch:{ Exception -> 0x00ca }
            if (r2 == 0) goto L_0x00b0
            r2 = 0
            java.lang.String r2 = r0.getString(r2)     // Catch:{ Exception -> 0x00ca }
            r3 = 1
            java.lang.String r4 = r0.getString(r3)     // Catch:{ Exception -> 0x00ca }
            r5 = 2
            java.lang.String r5 = r0.getString(r5)     // Catch:{ Exception -> 0x00ca }
            r6 = 3
            long r6 = r0.getLong(r6)     // Catch:{ Exception -> 0x00ca }
            r8 = 4
            java.lang.String r8 = r0.getString(r8)     // Catch:{ Exception -> 0x00ca }
            r9 = 5
            int r9 = r0.getInt(r9)     // Catch:{ Exception -> 0x00ca }
            r10 = 6
            int r10 = r0.getInt(r10)     // Catch:{ Exception -> 0x00ca }
            java.lang.String r11 = "screenshot"
            if (r2 == 0) goto L_0x0054
            java.lang.String r12 = r2.toLowerCase()     // Catch:{ Exception -> 0x00ca }
            boolean r12 = r12.contains(r11)     // Catch:{ Exception -> 0x00ca }
            if (r12 != 0) goto L_0x0078
        L_0x0054:
            if (r4 == 0) goto L_0x0060
            java.lang.String r4 = r4.toLowerCase()     // Catch:{ Exception -> 0x00ca }
            boolean r4 = r4.contains(r11)     // Catch:{ Exception -> 0x00ca }
            if (r4 != 0) goto L_0x0078
        L_0x0060:
            if (r5 == 0) goto L_0x006c
            java.lang.String r4 = r5.toLowerCase()     // Catch:{ Exception -> 0x00ca }
            boolean r4 = r4.contains(r11)     // Catch:{ Exception -> 0x00ca }
            if (r4 != 0) goto L_0x0078
        L_0x006c:
            if (r8 == 0) goto L_0x001d
            java.lang.String r4 = r8.toLowerCase()     // Catch:{ Exception -> 0x00ca }
            boolean r4 = r4.contains(r11)     // Catch:{ Exception -> 0x00ca }
            if (r4 == 0) goto L_0x001d
        L_0x0078:
            if (r9 == 0) goto L_0x007c
            if (r10 != 0) goto L_0x008a
        L_0x007c:
            android.graphics.BitmapFactory$Options r4 = new android.graphics.BitmapFactory$Options     // Catch:{ Exception -> 0x00a7 }
            r4.<init>()     // Catch:{ Exception -> 0x00a7 }
            r4.inJustDecodeBounds = r3     // Catch:{ Exception -> 0x00a7 }
            android.graphics.BitmapFactory.decodeFile(r2, r4)     // Catch:{ Exception -> 0x00a7 }
            int r9 = r4.outWidth     // Catch:{ Exception -> 0x00a7 }
            int r10 = r4.outHeight     // Catch:{ Exception -> 0x00a7 }
        L_0x008a:
            if (r9 <= 0) goto L_0x009e
            if (r10 <= 0) goto L_0x009e
            int r2 = r1.x     // Catch:{ Exception -> 0x00a7 }
            if (r9 != r2) goto L_0x0096
            int r2 = r1.y     // Catch:{ Exception -> 0x00a7 }
            if (r10 == r2) goto L_0x009e
        L_0x0096:
            int r2 = r1.x     // Catch:{ Exception -> 0x00a7 }
            if (r10 != r2) goto L_0x001d
            int r2 = r1.y     // Catch:{ Exception -> 0x00a7 }
            if (r9 != r2) goto L_0x001d
        L_0x009e:
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x00a7 }
            r14.add(r2)     // Catch:{ Exception -> 0x00a7 }
            goto L_0x001d
        L_0x00a7:
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x00ca }
            r14.add(r2)     // Catch:{ Exception -> 0x00ca }
            goto L_0x001d
        L_0x00b0:
            r0.close()     // Catch:{ Exception -> 0x00ca }
        L_0x00b3:
            boolean r1 = r14.isEmpty()     // Catch:{ Exception -> 0x00ca }
            if (r1 != 0) goto L_0x00c4
            org.telegram.messenger.-$$Lambda$MediaController$V7DFJT2LOvar_pC7uloGUOOPOs8 r1 = new org.telegram.messenger.-$$Lambda$MediaController$V7DFJT2LOvar_pC7uloGUOOPOs8     // Catch:{ Exception -> 0x00ca }
            r1.<init>(r14)     // Catch:{ Exception -> 0x00ca }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ Exception -> 0x00ca }
            goto L_0x00c4
        L_0x00c2:
            r14 = move-exception
            goto L_0x00d2
        L_0x00c4:
            if (r0 == 0) goto L_0x00d1
        L_0x00c6:
            r0.close()     // Catch:{ Exception -> 0x00d1 }
            goto L_0x00d1
        L_0x00ca:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)     // Catch:{ all -> 0x00c2 }
            if (r0 == 0) goto L_0x00d1
            goto L_0x00c6
        L_0x00d1:
            return
        L_0x00d2:
            if (r0 == 0) goto L_0x00d7
            r0.close()     // Catch:{ Exception -> 0x00d7 }
        L_0x00d7:
            goto L_0x00d9
        L_0x00d8:
            throw r14
        L_0x00d9:
            goto L_0x00d8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.processMediaObserver(android.net.Uri):void");
    }

    public /* synthetic */ void lambda$processMediaObserver$6$MediaController(ArrayList arrayList) {
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
        MessageObject messageObject;
        ArrayList<MessageObject> arrayList;
        int i3 = 0;
        if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.httpFileDidLoad) {
            String str = objArr[0];
            if (this.downloadingCurrentMessage && (messageObject = this.playingMessageObject) != null && messageObject.currentAccount == i2 && FileLoader.getAttachFileName(messageObject.getDocument()).equals(str)) {
                this.playMusicAgain = true;
                playMessage(this.playingMessageObject);
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!objArr[2].booleanValue()) {
                int intValue = objArr[1].intValue();
                ArrayList arrayList2 = objArr[0];
                MessageObject messageObject2 = this.playingMessageObject;
                if (messageObject2 != null && intValue == messageObject2.messageOwner.to_id.channel_id && arrayList2.contains(Integer.valueOf(messageObject2.getId()))) {
                    cleanupPlayer(true, true);
                }
                ArrayList<MessageObject> arrayList3 = this.voiceMessagesPlaylist;
                if (arrayList3 != null && !arrayList3.isEmpty() && intValue == this.voiceMessagesPlaylist.get(0).messageOwner.to_id.channel_id) {
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
            long longValue = objArr[0].longValue();
            MessageObject messageObject4 = this.playingMessageObject;
            if (messageObject4 != null && messageObject4.getDialogId() == longValue) {
                cleanupPlayer(false, true);
            }
        } else if (i == NotificationCenter.musicDidLoad) {
            long longValue2 = objArr[0].longValue();
            MessageObject messageObject5 = this.playingMessageObject;
            if (messageObject5 != null && messageObject5.isMusic() && this.playingMessageObject.getDialogId() == longValue2 && !this.playingMessageObject.scheduled) {
                ArrayList arrayList4 = objArr[1];
                this.playlist.addAll(0, arrayList4);
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                    this.currentPlaylistNum = 0;
                    return;
                }
                this.currentPlaylistNum += arrayList4.size();
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!objArr[2].booleanValue() && (arrayList = this.voiceMessagesPlaylist) != null && !arrayList.isEmpty() && objArr[0].longValue() == this.voiceMessagesPlaylist.get(0).getDialogId()) {
                ArrayList arrayList5 = objArr[1];
                while (i3 < arrayList5.size()) {
                    MessageObject messageObject6 = (MessageObject) arrayList5.get(i3);
                    if ((messageObject6.isVoice() || messageObject6.isRoundVideo()) && (!this.voiceMessagesPlaylistUnread || (messageObject6.isContentUnread() && !messageObject6.isOut()))) {
                        this.voiceMessagesPlaylist.add(messageObject6);
                        this.voiceMessagesPlaylistMap.put(messageObject6.getId(), messageObject6);
                    }
                    i3++;
                }
            }
        } else if (i == NotificationCenter.playerDidStartPlaying) {
            if (!getInstance().isCurrentPlayer(objArr[0])) {
                getInstance().lambda$startAudioAgain$7$MediaController(getInstance().getPlayingMessageObject());
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
        int i2;
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
                if (this.raisedToBack != 6 && ((f6 > 0.0f && this.previousAccValue > 0.0f) || (f6 < 0.0f && this.previousAccValue < 0.0f))) {
                    if (f6 > 0.0f) {
                        z = f6 > 15.0f;
                        i = 1;
                    } else {
                        z = f6 < -15.0f;
                        i = 2;
                    }
                    int i3 = this.raisedToTopSign;
                    if (i3 == 0 || i3 == i) {
                        if (z && this.raisedToBack == 0 && ((i2 = this.raisedToTopSign) == 0 || i2 == i)) {
                            int i4 = this.raisedToTop;
                            if (i4 < 6 && !this.proximityTouched) {
                                this.raisedToTopSign = i;
                                int i5 = i4 + 1;
                                this.raisedToTop = i5;
                                if (i5 == 6) {
                                    this.countLess = 0;
                                }
                            }
                        } else {
                            if (!z) {
                                this.countLess++;
                            }
                            if (!(this.raisedToTopSign == i && this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                                this.raisedToBack = 0;
                                this.raisedToTop = 0;
                                this.raisedToTopSign = 0;
                                this.countLess = 0;
                            }
                        }
                    } else if (this.raisedToTop != 6 || !z) {
                        if (!z) {
                            this.countLess++;
                        }
                        if (!(this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                            this.raisedToTop = 0;
                            this.raisedToTopSign = 0;
                            this.raisedToBack = 0;
                            this.countLess = 0;
                        }
                    } else {
                        int i6 = this.raisedToBack;
                        if (i6 < 6) {
                            int i7 = i6 + 1;
                            this.raisedToBack = i7;
                            if (i7 == 6) {
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
                        startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), (MessageObject) null, this.raiseChat.getClassGuid());
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
            startRecording(chatActivity.getCurrentAccount(), this.raiseChat.getDialogId(), (MessageObject) null, this.raiseChat.getClassGuid());
            this.ignoreOnPause = true;
        }
    }

    private void startAudioAgain(boolean z) {
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
                if (!z) {
                    if (this.videoPlayer.getCurrentPosition() < 1000) {
                        this.videoPlayer.seekTo(0);
                    }
                    this.videoPlayer.play();
                    return;
                }
                lambda$startAudioAgain$7$MediaController(this.playingMessageObject);
                return;
            }
            boolean z2 = this.audioPlayer != null;
            MessageObject messageObject2 = this.playingMessageObject;
            float f = messageObject2.audioProgress;
            int i2 = messageObject2.audioPlayerDuration;
            if (z || (videoPlayer2 = this.audioPlayer) == null || !videoPlayer2.isPlaying() || ((float) i2) * f > 1.0f) {
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
                AndroidUtilities.runOnUIThread(new Runnable(messageObject2) {
                    private final /* synthetic */ MessageObject f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MediaController.this.lambda$startAudioAgain$7$MediaController(this.f$1);
                    }
                }, 100);
            } else {
                lambda$startAudioAgain$7$MediaController(messageObject2);
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
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        MediaController.this.lambda$startRaiseToEarSensors$8$MediaController();
                    }
                });
                this.sensorsStarted = true;
            }
        }
    }

    public /* synthetic */ void lambda$startRaiseToEarSensors$8$MediaController() {
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
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        MediaController.this.lambda$stopRaiseToEarSensors$9$MediaController();
                    }
                });
                if (this.proximityHasDifferentValues && (wakeLock = this.proximityWakeLock) != null && wakeLock.isHeld()) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    public /* synthetic */ void lambda$stopRaiseToEarSensors$9$MediaController() {
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
        VideoPlayer videoPlayer2 = this.audioPlayer;
        if (videoPlayer2 != null) {
            try {
                videoPlayer2.releasePlayer(true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.audioPlayer = null;
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
                    MessageObject messageObject = this.playingMessageObject;
                    this.goingToShowMessageObject = messageObject;
                    NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, true);
                } else {
                    long currentPosition = videoPlayer3.getCurrentPosition();
                    MessageObject messageObject2 = this.playingMessageObject;
                    if (messageObject2 != null && messageObject2.isVideo() && currentPosition > 0 && currentPosition != -9223372036854775807L) {
                        MessageObject messageObject3 = this.playingMessageObject;
                        messageObject3.audioProgressMs = (int) currentPosition;
                        NotificationCenter.getInstance(messageObject3.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, false);
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
        MessageObject messageObject4 = this.playingMessageObject;
        if (messageObject4 != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance(messageObject4.currentAccount).cancelLoadFile(this.playingMessageObject.getDocument());
            }
            MessageObject messageObject5 = this.playingMessageObject;
            if (z) {
                messageObject5.resetPlayingProgress();
                NotificationCenter.getInstance(messageObject5.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), 0);
            }
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (z) {
                NotificationsController.audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                int i = -1;
                ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
                if (arrayList != null) {
                    if (!z3 || (i = arrayList.indexOf(messageObject5)) < 0) {
                        this.voiceMessagesPlaylist = null;
                        this.voiceMessagesPlaylistMap = null;
                    } else {
                        this.voiceMessagesPlaylist.remove(i);
                        this.voiceMessagesPlaylistMap.remove(messageObject5.getId());
                        if (this.voiceMessagesPlaylist.isEmpty()) {
                            this.voiceMessagesPlaylist = null;
                            this.voiceMessagesPlaylistMap = null;
                        }
                    }
                }
                ArrayList<MessageObject> arrayList2 = this.voiceMessagesPlaylist;
                if (arrayList2 == null || i >= arrayList2.size()) {
                    if ((messageObject5.isVoice() || messageObject5.isRoundVideo()) && messageObject5.getId() != 0) {
                        startRecordingIfFromSpeaker();
                    }
                    NotificationCenter.getInstance(messageObject5.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(messageObject5.getId()), Boolean.valueOf(z2));
                    this.pipSwitchingState = 0;
                    PipRoundVideoView pipRoundVideoView3 = this.pipRoundVideoView;
                    if (pipRoundVideoView3 != null) {
                        pipRoundVideoView3.close(true);
                        this.pipRoundVideoView = null;
                    }
                } else {
                    MessageObject messageObject6 = this.voiceMessagesPlaylist.get(i);
                    playMessage(messageObject6);
                    if (!messageObject6.isRoundVideo() && (pipRoundVideoView2 = this.pipRoundVideoView) != null) {
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
                FileLog.e((Throwable) e);
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
            arrayList.remove(this.currentPlaylistNum);
            this.shuffledPlaylist.add(this.playlist.get(this.currentPlaylistNum));
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
        this.forceLoopCurrentPlaylist = !z;
        this.playMusicAgain = !this.playlist.isEmpty();
        this.playlist.clear();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            MessageObject messageObject2 = arrayList.get(size);
            if (messageObject2.isMusic()) {
                this.playlist.add(messageObject2);
            }
        }
        int indexOf = this.playlist.indexOf(messageObject);
        this.currentPlaylistNum = indexOf;
        if (indexOf == -1) {
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
                MediaDataController.getInstance(messageObject.currentAccount).loadMusic(messageObject.getDialogId(), this.playlist.get(0).getIdWithChannel());
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
            playMessage(this.playlist.get(this.currentPlaylistNum));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0057 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:63:? A[RETURN, SYNTHETIC] */
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
            if (r5 == 0) goto L_0x00fa
            if (r8 == 0) goto L_0x00fa
            int r8 = org.telegram.messenger.SharedConfig.repeatMode
            if (r8 != 0) goto L_0x00fa
            boolean r8 = r7.forceLoopCurrentPlaylist
            if (r8 != 0) goto L_0x00fa
            org.telegram.ui.Components.VideoPlayer r8 = r7.audioPlayer
            if (r8 != 0) goto L_0x0069
            org.telegram.ui.Components.VideoPlayer r8 = r7.videoPlayer
            if (r8 == 0) goto L_0x00f9
        L_0x0069:
            org.telegram.ui.Components.VideoPlayer r8 = r7.audioPlayer
            r0 = 0
            if (r8 == 0) goto L_0x0079
            r8.releasePlayer(r4)     // Catch:{ Exception -> 0x0072 }
            goto L_0x0076
        L_0x0072:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0076:
            r7.audioPlayer = r0
            goto L_0x00b0
        L_0x0079:
            org.telegram.ui.Components.VideoPlayer r8 = r7.videoPlayer
            if (r8 == 0) goto L_0x00b0
            r7.currentAspectRatioFrameLayout = r0
            r7.currentTextureViewContainer = r0
            r7.currentAspectRatioFrameLayoutReady = r3
            r7.currentTextureView = r0
            r8.releasePlayer(r4)
            r7.videoPlayer = r0
            android.app.Activity r8 = r7.baseActivity     // Catch:{ Exception -> 0x0096 }
            android.view.Window r8 = r8.getWindow()     // Catch:{ Exception -> 0x0096 }
            r0 = 128(0x80, float:1.794E-43)
            r8.clearFlags(r0)     // Catch:{ Exception -> 0x0096 }
            goto L_0x009a
        L_0x0096:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x009a:
            java.lang.Runnable r8 = r7.setLoadingRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r8)
            org.telegram.messenger.MessageObject r8 = r7.playingMessageObject
            int r8 = r8.currentAccount
            org.telegram.messenger.FileLoader r8 = org.telegram.messenger.FileLoader.getInstance(r8)
            org.telegram.messenger.MessageObject r0 = r7.playingMessageObject
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            r8.removeLoadingVideo(r0, r4, r3)
        L_0x00b0:
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
        L_0x00f9:
            return
        L_0x00fa:
            int r8 = r7.currentPlaylistNum
            if (r8 < 0) goto L_0x0119
            int r1 = r0.size()
            if (r8 < r1) goto L_0x0105
            goto L_0x0119
        L_0x0105:
            org.telegram.messenger.MessageObject r8 = r7.playingMessageObject
            if (r8 == 0) goto L_0x010c
            r8.resetPlayingProgress()
        L_0x010c:
            r7.playMusicAgain = r4
            int r8 = r7.currentPlaylistNum
            java.lang.Object r8 = r0.get(r8)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            r7.playMessage(r8)
        L_0x0119:
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
            int i4 = this.currentPlaylistNum;
            if (i4 >= 0 && i4 < arrayList.size()) {
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
            if (file != null) {
                boolean exists = file.exists();
            }
            if (file != null && file != file2 && !file.exists()) {
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
                    if (file != null) {
                        boolean exists = file.exists();
                    }
                    if (file != null && file != file2 && !file.exists() && messageObject.isMusic()) {
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
                        PipRoundVideoView pipRoundVideoView3 = new PipRoundVideoView();
                        this.pipRoundVideoView = pipRoundVideoView3;
                        pipRoundVideoView3.show(this.baseActivity, new Runnable() {
                            public final void run() {
                                MediaController.this.lambda$setCurrentVideoVisible$10$MediaController();
                            }
                        });
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

    public /* synthetic */ void lambda$setCurrentVideoVisible$10$MediaController() {
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
                if (this.currentAspectRatioFrameLayoutReady && aspectRatioFrameLayout != null && aspectRatioFrameLayout != null) {
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
            this.currentMusicPlaybackSpeed = f;
        } else {
            this.currentPlaybackSpeed = f;
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
        MessagesController.getGlobalMainSettings().edit().putFloat(z ? "musicPlaybackSpeed" : "playbackSpeed", f).commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.messagePlayingSpeedChanged, new Object[0]);
    }

    public float getPlaybackSpeed(boolean z) {
        return z ? this.currentMusicPlaybackSpeed : this.currentPlaybackSpeed;
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
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.videoPlayer = videoPlayer2;
            this.playingMessageObject = messageObject;
            final int i = this.playerNum + 1;
            this.playerNum = i;
            final MessageObject messageObject2 = messageObject;
            videoPlayer2.setDelegate(new VideoPlayer.VideoPlayerDelegate((int[]) null, true) {
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }

                public void onStateChanged(boolean z, int i) {
                    if (i == MediaController.this.playerNum) {
                        MediaController.this.updateVideoState(messageObject2, null, true, z, i);
                    }
                }

                public void onError(Exception exc) {
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
                                    MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new Runnable() {
                                        public final void run() {
                                            MediaController.AnonymousClass5.this.lambda$onSurfaceDestroyed$0$MediaController$5();
                                        }
                                    });
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

    public /* synthetic */ void lambda$playMessage$11$MediaController() {
        cleanupPlayer(true, true);
    }

    public boolean playMessage(MessageObject messageObject) {
        boolean z;
        File file;
        File file2;
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
        boolean z4 = SharedConfig.streamMedia && (messageObject.isMusic() || messageObject.isRoundVideo() || (messageObject.isVideo() && messageObject.canStreamVideo())) && ((int) messageObject.getDialogId()) != 0;
        if (file2 == null || file2 == file || (z = file2.exists()) || z4) {
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
            String str2 = "&reference=";
            if (messageObject.isRoundVideo() || isVideo) {
                String str3 = "&rid=";
                String str4 = "&name=";
                FileLoader.getInstance(messageObject2.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
                this.playerWasReady = false;
                boolean z6 = !isVideo || (messageObject2.messageOwner.to_id.channel_id == 0 && messageObject2.audioProgress <= 0.1f);
                int[] iArr = (!isVideo || messageObject.getDuration() > 30) ? null : new int[]{1};
                this.playlist.clear();
                this.shuffledPlaylist.clear();
                VideoPlayer videoPlayer2 = new VideoPlayer();
                this.videoPlayer = videoPlayer2;
                final int i = this.playerNum + 1;
                this.playerNum = i;
                String str5 = "UTF-8";
                AnonymousClass6 r10 = r1;
                String str6 = str3;
                String str7 = "&mime=";
                String str8 = str4;
                VideoPlayer videoPlayer3 = videoPlayer2;
                final MessageObject messageObject4 = messageObject;
                String str9 = str2;
                String str10 = "&size=";
                String str11 = "other";
                final int[] iArr2 = iArr;
                String str12 = "&dc=";
                File file3 = file2;
                final boolean z7 = z6;
                AnonymousClass6 r1 = new VideoPlayer.VideoPlayerDelegate() {
                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }

                    public void onStateChanged(boolean z, int i) {
                        if (i == MediaController.this.playerNum) {
                            MediaController.this.updateVideoState(messageObject4, iArr2, z7, z, i);
                        }
                    }

                    public void onError(Exception exc) {
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
                                        MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new Runnable() {
                                            public final void run() {
                                                MediaController.AnonymousClass6.this.lambda$onSurfaceDestroyed$0$MediaController$6();
                                            }
                                        });
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

                    public /* synthetic */ void lambda$onSurfaceDestroyed$0$MediaController$6() {
                        MediaController.this.cleanupPlayer(true, true);
                    }
                };
                videoPlayer3.setDelegate(r10);
                this.currentAspectRatioFrameLayoutReady = false;
                if (this.pipRoundVideoView != null || !MessagesController.getInstance(messageObject2.currentAccount).isDialogVisible(messageObject.getDialogId(), messageObject2.scheduled)) {
                    if (this.pipRoundVideoView == null) {
                        try {
                            PipRoundVideoView pipRoundVideoView2 = new PipRoundVideoView();
                            this.pipRoundVideoView = pipRoundVideoView2;
                            pipRoundVideoView2.show(this.baseActivity, new Runnable() {
                                public final void run() {
                                    MediaController.this.lambda$playMessage$11$MediaController();
                                }
                            });
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
                        AndroidUtilities.runOnUIThread(new Runnable(file3) {
                            private final /* synthetic */ File f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                NotificationCenter.getInstance(MessageObject.this.currentAccount).postNotificationName(NotificationCenter.fileDidLoad, FileLoader.getAttachFileName(MessageObject.this.getDocument()), this.f$1);
                            }
                        });
                    }
                    this.videoPlayer.preparePlayer(Uri.fromFile(file3), str11);
                } else {
                    try {
                        int fileReference = FileLoader.getInstance(messageObject2.currentAccount).getFileReference(messageObject2);
                        TLRPC$Document document = messageObject.getDocument();
                        StringBuilder sb = new StringBuilder();
                        sb.append("?account=");
                        sb.append(messageObject2.currentAccount);
                        sb.append("&id=");
                        sb.append(document.id);
                        sb.append("&hash=");
                        sb.append(document.access_hash);
                        sb.append(str12);
                        sb.append(document.dc_id);
                        sb.append(str10);
                        sb.append(document.size);
                        sb.append(str7);
                        String str13 = str5;
                        sb.append(URLEncoder.encode(document.mime_type, str13));
                        sb.append(str6);
                        sb.append(fileReference);
                        sb.append(str8);
                        sb.append(URLEncoder.encode(FileLoader.getDocumentFileName(document), str13));
                        sb.append(str9);
                        sb.append(Utilities.bytesToHex(document.file_reference != null ? document.file_reference : new byte[0]));
                        this.videoPlayer.preparePlayer(Uri.parse("tg://" + messageObject.getFileName() + sb.toString()), str11);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                if (messageObject.isRoundVideo()) {
                    this.videoPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                    float f = this.currentPlaybackSpeed;
                    if (f > 1.0f) {
                        this.videoPlayer.setPlaybackSpeed(f);
                    }
                } else {
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
                    String str14 = "&name=";
                    videoPlayer4.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                        public void onError(Exception exc) {
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
                            if (i2 == MediaController.this.playerNum) {
                                if (i == 4 || ((i == 1 || i == 2) && z && messageObject2.audioProgress >= 0.999f)) {
                                    if (MediaController.this.playlist.isEmpty() || (MediaController.this.playlist.size() <= 1 && messageObject2.isVoice())) {
                                        MediaController mediaController = MediaController.this;
                                        MessageObject messageObject = messageObject2;
                                        mediaController.cleanupPlayer(true, true, messageObject != null && messageObject.isVoice(), false);
                                        return;
                                    }
                                    MediaController.this.playNextMessageWithoutOrder(true);
                                } else if (MediaController.this.seekToProgressPending == 0.0f) {
                                } else {
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
                    if (z5) {
                        if (!messageObject2.mediaExists && file2 != file) {
                            AndroidUtilities.runOnUIThread(new Runnable(file2) {
                                private final /* synthetic */ File f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    NotificationCenter.getInstance(MessageObject.this.currentAccount).postNotificationName(NotificationCenter.fileDidLoad, FileLoader.getAttachFileName(MessageObject.this.getDocument()), this.f$1);
                                }
                            });
                        }
                        this.audioPlayer.preparePlayer(Uri.fromFile(file2), "other");
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
                        sb2.append(str14);
                        sb2.append(URLEncoder.encode(FileLoader.getDocumentFileName(document2), "UTF-8"));
                        sb2.append(str2);
                        sb2.append(Utilities.bytesToHex(document2.file_reference != null ? document2.file_reference : new byte[0]));
                        this.audioPlayer.preparePlayer(Uri.parse("tg://" + messageObject.getFileName() + sb2.toString()), "other");
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
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        String fileName = messageObject.getFileName();
                        if (!TextUtils.isEmpty(fileName) && messageObject.getDuration() >= 1200) {
                            float f2 = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getFloat(fileName, -1.0f);
                            if (f2 > 0.0f && f2 < 0.999f) {
                                this.seekToProgressPending = f2;
                                messageObject2.audioProgress = f2;
                            }
                            this.shouldSavePositionForCurrentAudio = fileName;
                            if (this.currentMusicPlaybackSpeed > 1.0f) {
                                this.audioPlayer.setPlaybackSpeed(this.currentMusicPlaybackSpeed);
                            }
                        }
                    }
                    if (messageObject2.forceSeekTo >= 0.0f) {
                        float f3 = messageObject2.forceSeekTo;
                        this.seekToProgressPending = f3;
                        messageObject2.audioProgress = f3;
                        messageObject2.forceSeekTo = -1.0f;
                    }
                    this.audioPlayer.setStreamType(this.useFrontSpeaker ? 0 : 3);
                    this.audioPlayer.play();
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
                    if (this.playingMessageObject.audioProgress != 0.0f) {
                        long duration = videoPlayer6.getDuration();
                        if (duration == -9223372036854775807L) {
                            duration = ((long) this.playingMessageObject.getDuration()) * 1000;
                        }
                        int i4 = (int) (((float) duration) * this.playingMessageObject.audioProgress);
                        if (this.playingMessageObject.audioProgressMs != 0) {
                            i4 = this.playingMessageObject.audioProgressMs;
                            this.playingMessageObject.audioProgressMs = 0;
                        }
                        this.videoPlayer.seekTo((long) i4);
                    }
                } catch (Exception e4) {
                    MessageObject messageObject6 = this.playingMessageObject;
                    messageObject6.audioProgress = 0.0f;
                    messageObject6.audioProgressSec = 0;
                    NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), null);
                    FileLog.e((Throwable) e4);
                }
                this.videoPlayer.play();
            } else {
                VideoPlayer videoPlayer7 = this.audioPlayer;
                if (videoPlayer7 != null) {
                    try {
                        if (this.playingMessageObject.audioProgress != 0.0f) {
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
            MessageObject messageObject7 = this.playingMessageObject;
            if (messageObject7 == null || !messageObject7.isMusic()) {
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
            int indexOf = this.playlist.indexOf(messageObject);
            this.currentPlaylistNum = indexOf;
            if (indexOf == -1) {
                this.playlist.clear();
                this.shuffledPlaylist.clear();
                cleanupPlayer(true, true);
            }
        }
    }

    public boolean isCurrentPlayer(VideoPlayer videoPlayer2) {
        return this.videoPlayer == videoPlayer2 || this.audioPlayer == videoPlayer2;
    }

    /* renamed from: pauseMessage */
    public boolean lambda$startAudioAgain$7$MediaController(MessageObject messageObject) {
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

    public void setReplyingMessage(MessageObject messageObject) {
        this.recordReplyingMessageObject = messageObject;
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

    public void startRecording(int i, long j, MessageObject messageObject, int i2) {
        MessageObject messageObject2 = this.playingMessageObject;
        boolean z = messageObject2 != null && isPlayingMessage(messageObject2) && !isMessagePaused();
        requestAudioFocus(true);
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        $$Lambda$MediaController$mfuAZ38cha75Th9PPAwXrDA_YcI r2 = new Runnable(i, i2, j, messageObject) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ MessageObject f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
            }

            public final void run() {
                MediaController.this.lambda$startRecording$18$MediaController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        };
        this.recordStartRunnable = r2;
        dispatchQueue.postRunnable(r2, z ? 500 : 50);
    }

    public /* synthetic */ void lambda$startRecording$18$MediaController(int i, int i2, long j, MessageObject messageObject) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new Runnable(i, i2) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaController.this.lambda$null$14$MediaController(this.f$1, this.f$2);
                }
            });
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
                AndroidUtilities.runOnUIThread(new Runnable(i, i2) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaController.this.lambda$null$15$MediaController(this.f$1, this.f$2);
                    }
                });
                return;
            }
            this.audioRecorder = new AudioRecord(0, this.sampleRate, 16, 2, this.recordBufferSize);
            this.recordStartTime = System.currentTimeMillis();
            this.recordTimeCount = 0;
            this.samplesCount = 0;
            this.recordDialogId = j;
            this.recordingCurrentAccount = i;
            this.recordReplyingMessageObject = messageObject;
            this.fileBuffer.rewind();
            this.audioRecorder.startRecording();
            this.recordQueue.postRunnable(this.recordRunnable);
            AndroidUtilities.runOnUIThread(new Runnable(i, i2) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaController.this.lambda$null$17$MediaController(this.f$1, this.f$2);
                }
            });
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
            AndroidUtilities.runOnUIThread(new Runnable(i, i2) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaController.this.lambda$null$16$MediaController(this.f$1, this.f$2);
                }
            });
        }
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
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(i2));
    }

    public /* synthetic */ void lambda$null$17$MediaController(int i, int i2) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(i2), true);
    }

    public void generateWaveform(MessageObject messageObject) {
        String str = messageObject.getId() + "_" + messageObject.getDialogId();
        String absolutePath = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(str)) {
            this.generatingWaveform.put(str, messageObject);
            Utilities.globalQueue.postRunnable(new Runnable(absolutePath, str, messageObject) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ MessageObject f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MediaController.this.lambda$generateWaveform$20$MediaController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$generateWaveform$20$MediaController(String str, String str2, MessageObject messageObject) {
        AndroidUtilities.runOnUIThread(new Runnable(str2, getWaveform(str), messageObject) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ byte[] f$2;
            private final /* synthetic */ MessageObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaController.this.lambda$null$19$MediaController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$19$MediaController(String str, byte[] bArr, MessageObject messageObject) {
        MessageObject remove = this.generatingWaveform.remove(str);
        if (remove != null && bArr != null) {
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
            this.fileEncodingQueue.postRunnable(new Runnable(this.recordingAudio, this.recordingAudioFile, i, z, i2) {
                private final /* synthetic */ TLRPC$TL_document f$1;
                private final /* synthetic */ File f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ int f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    MediaController.this.lambda$stopRecordingInternal$22$MediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
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
            FileLog.e((Throwable) e);
        }
        this.recordingAudio = null;
        this.recordingAudioFile = null;
    }

    public /* synthetic */ void lambda$stopRecordingInternal$22$MediaController(TLRPC$TL_document tLRPC$TL_document, File file, int i, boolean z, int i2) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_document, file, i, z, i2) {
            private final /* synthetic */ TLRPC$TL_document f$1;
            private final /* synthetic */ File f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                MediaController.this.lambda$null$21$MediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$21$MediaController(TLRPC$TL_document tLRPC$TL_document, File file, int i, boolean z, int i2) {
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
                SendMessagesHelper.getInstance(this.recordingCurrentAccount).sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, file.getAbsolutePath(), this.recordDialogId, this.recordReplyingMessageObject, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2, 0, (Object) null);
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
            NotificationCenter.getInstance(this.recordingCurrentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), false, Integer.valueOf((int) j));
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
        this.recordQueue.postRunnable(new Runnable(i, z, i2) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaController.this.lambda$stopRecording$24$MediaController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$stopRecording$24$MediaController(int i, boolean z, int i2) {
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
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaController.this.lambda$null$23$MediaController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$23$MediaController(int i) {
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

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0025 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0026  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void saveFile(java.lang.String r9, android.content.Context r10, int r11, java.lang.String r12, java.lang.String r13) {
        /*
            if (r9 != 0) goto L_0x0003
            return
        L_0x0003:
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            r1 = 0
            if (r0 != 0) goto L_0x0022
            java.io.File r0 = new java.io.File
            r0.<init>(r9)
            boolean r9 = r0.exists()
            if (r9 == 0) goto L_0x0022
            android.net.Uri r9 = android.net.Uri.fromFile(r0)
            boolean r9 = org.telegram.messenger.AndroidUtilities.isInternalUri(r9)
            if (r9 == 0) goto L_0x0020
            goto L_0x0022
        L_0x0020:
            r4 = r0
            goto L_0x0023
        L_0x0022:
            r4 = r1
        L_0x0023:
            if (r4 != 0) goto L_0x0026
            return
        L_0x0026:
            r9 = 1
            boolean[] r6 = new boolean[r9]
            r0 = 0
            r6[r0] = r0
            boolean r2 = r4.exists()
            if (r2 == 0) goto L_0x0074
            if (r10 == 0) goto L_0x0062
            if (r11 == 0) goto L_0x0062
            org.telegram.ui.ActionBar.AlertDialog r2 = new org.telegram.ui.ActionBar.AlertDialog     // Catch:{ Exception -> 0x005e }
            r3 = 2
            r2.<init>(r10, r3)     // Catch:{ Exception -> 0x005e }
            java.lang.String r10 = "Loading"
            r1 = 2131625575(0x7f0e0667, float:1.8878362E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r1)     // Catch:{ Exception -> 0x005b }
            r2.setMessage(r10)     // Catch:{ Exception -> 0x005b }
            r2.setCanceledOnTouchOutside(r0)     // Catch:{ Exception -> 0x005b }
            r2.setCancelable(r9)     // Catch:{ Exception -> 0x005b }
            org.telegram.messenger.-$$Lambda$MediaController$9G8Y_4s94DhQGf7yl7S6aWrmfyQ r9 = new org.telegram.messenger.-$$Lambda$MediaController$9G8Y_4s94DhQGf7yl7S6aWrmfyQ     // Catch:{ Exception -> 0x005b }
            r9.<init>(r6)     // Catch:{ Exception -> 0x005b }
            r2.setOnCancelListener(r9)     // Catch:{ Exception -> 0x005b }
            r2.show()     // Catch:{ Exception -> 0x005b }
            r7 = r2
            goto L_0x0063
        L_0x005b:
            r9 = move-exception
            r1 = r2
            goto L_0x005f
        L_0x005e:
            r9 = move-exception
        L_0x005f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
        L_0x0062:
            r7 = r1
        L_0x0063:
            java.lang.Thread r9 = new java.lang.Thread
            org.telegram.messenger.-$$Lambda$MediaController$eRZH1-Xi3nZkminm_nfkIaga_pI r10 = new org.telegram.messenger.-$$Lambda$MediaController$eRZH1-Xi3nZkminm_nfkIaga_pI
            r2 = r10
            r3 = r11
            r5 = r12
            r8 = r13
            r2.<init>(r3, r4, r5, r6, r7, r8)
            r9.<init>(r10)
            r9.start()
        L_0x0074:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.saveFile(java.lang.String, android.content.Context, int, java.lang.String, java.lang.String):void");
    }

    static /* synthetic */ void lambda$saveFile$25(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:65:0x011f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0120, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0121, code lost:
        if (r18 != null) goto L_0x0123;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:?, code lost:
        r18.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:?, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0127, code lost:
        r0 = th;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:55:0x0110, B:63:0x011e, B:70:0x0126] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:70:0x0126 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:82:0x0134 */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x010c A[EDGE_INSN: B:101:0x010c->B:53:0x010c ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:107:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a0 A[Catch:{ Exception -> 0x0014 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00c8 A[Catch:{ all -> 0x011b }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0110 A[SYNTHETIC, Splitter:B:55:0x0110] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0115 A[SYNTHETIC, Splitter:B:58:0x0115] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x013f A[Catch:{ Exception -> 0x0014 }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0144 A[Catch:{ Exception -> 0x0014 }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0147 A[Catch:{ Exception -> 0x0014 }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x017d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$saveFile$28(int r19, java.io.File r20, java.lang.String r21, boolean[] r22, org.telegram.ui.ActionBar.AlertDialog r23, java.lang.String r24) {
        /*
            r1 = r19
            r0 = r21
            r2 = r23
            r3 = 2
            r4 = 1
            r5 = 0
            if (r1 != 0) goto L_0x0017
            java.lang.String r0 = org.telegram.messenger.FileLoader.getFileExtension(r20)     // Catch:{ Exception -> 0x0014 }
            java.io.File r0 = org.telegram.messenger.AndroidUtilities.generatePicturePath(r5, r0)     // Catch:{ Exception -> 0x0014 }
            goto L_0x001d
        L_0x0014:
            r0 = move-exception
            goto L_0x0178
        L_0x0017:
            if (r1 != r4) goto L_0x0020
            java.io.File r0 = org.telegram.messenger.AndroidUtilities.generateVideoPath()     // Catch:{ Exception -> 0x0014 }
        L_0x001d:
            r7 = r0
            goto L_0x009a
        L_0x0020:
            if (r1 != r3) goto L_0x0029
            java.lang.String r6 = android.os.Environment.DIRECTORY_DOWNLOADS     // Catch:{ Exception -> 0x0014 }
            java.io.File r6 = android.os.Environment.getExternalStoragePublicDirectory(r6)     // Catch:{ Exception -> 0x0014 }
            goto L_0x002f
        L_0x0029:
            java.lang.String r6 = android.os.Environment.DIRECTORY_MUSIC     // Catch:{ Exception -> 0x0014 }
            java.io.File r6 = android.os.Environment.getExternalStoragePublicDirectory(r6)     // Catch:{ Exception -> 0x0014 }
        L_0x002f:
            r6.mkdir()     // Catch:{ Exception -> 0x0014 }
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0014 }
            r7.<init>(r6, r0)     // Catch:{ Exception -> 0x0014 }
            boolean r8 = r7.exists()     // Catch:{ Exception -> 0x0014 }
            if (r8 == 0) goto L_0x009a
            r8 = 46
            int r8 = r0.lastIndexOf(r8)     // Catch:{ Exception -> 0x0014 }
            r9 = 0
        L_0x0044:
            r10 = 10
            if (r9 >= r10) goto L_0x009a
            r7 = -1
            java.lang.String r10 = ")"
            java.lang.String r11 = "("
            if (r8 == r7) goto L_0x0072
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0014 }
            r7.<init>()     // Catch:{ Exception -> 0x0014 }
            java.lang.String r12 = r0.substring(r5, r8)     // Catch:{ Exception -> 0x0014 }
            r7.append(r12)     // Catch:{ Exception -> 0x0014 }
            r7.append(r11)     // Catch:{ Exception -> 0x0014 }
            int r11 = r9 + 1
            r7.append(r11)     // Catch:{ Exception -> 0x0014 }
            r7.append(r10)     // Catch:{ Exception -> 0x0014 }
            java.lang.String r10 = r0.substring(r8)     // Catch:{ Exception -> 0x0014 }
            r7.append(r10)     // Catch:{ Exception -> 0x0014 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0014 }
            goto L_0x0089
        L_0x0072:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0014 }
            r7.<init>()     // Catch:{ Exception -> 0x0014 }
            r7.append(r0)     // Catch:{ Exception -> 0x0014 }
            r7.append(r11)     // Catch:{ Exception -> 0x0014 }
            int r11 = r9 + 1
            r7.append(r11)     // Catch:{ Exception -> 0x0014 }
            r7.append(r10)     // Catch:{ Exception -> 0x0014 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0014 }
        L_0x0089:
            java.io.File r10 = new java.io.File     // Catch:{ Exception -> 0x0014 }
            r10.<init>(r6, r7)     // Catch:{ Exception -> 0x0014 }
            boolean r7 = r10.exists()     // Catch:{ Exception -> 0x0014 }
            if (r7 != 0) goto L_0x0096
            r7 = r10
            goto L_0x009a
        L_0x0096:
            int r9 = r9 + 1
            r7 = r10
            goto L_0x0044
        L_0x009a:
            boolean r0 = r7.exists()     // Catch:{ Exception -> 0x0014 }
            if (r0 != 0) goto L_0x00a3
            r7.createNewFile()     // Catch:{ Exception -> 0x0014 }
        L_0x00a3:
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0014 }
            r10 = 500(0x1f4, double:2.47E-321)
            long r8 = r8 - r10
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0135 }
            r6 = r20
            r0.<init>(r6)     // Catch:{ Exception -> 0x0135 }
            java.nio.channels.FileChannel r6 = r0.getChannel()     // Catch:{ Exception -> 0x0135 }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0129 }
            r0.<init>(r7)     // Catch:{ all -> 0x0129 }
            java.nio.channels.FileChannel r18 = r0.getChannel()     // Catch:{ all -> 0x0129 }
            long r14 = r6.size()     // Catch:{ all -> 0x011b }
            r12 = 0
        L_0x00c4:
            int r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x010c
            boolean r0 = r22[r5]     // Catch:{ all -> 0x011b }
            if (r0 == 0) goto L_0x00cd
            goto L_0x010c
        L_0x00cd:
            long r3 = r14 - r12
            r0 = 4096(0x1000, double:2.0237E-320)
            long r16 = java.lang.Math.min(r0, r3)     // Catch:{ all -> 0x011b }
            r3 = r12
            r12 = r18
            r13 = r6
            r21 = r6
            r5 = r14
            r14 = r3
            r12.transferFrom(r13, r14, r16)     // Catch:{ all -> 0x010a }
            if (r2 == 0) goto L_0x00ff
            long r12 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x010a }
            long r12 = r12 - r10
            int r14 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r14 > 0) goto L_0x00ff
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x010a }
            float r12 = (float) r3     // Catch:{ all -> 0x010a }
            float r13 = (float) r5     // Catch:{ all -> 0x010a }
            float r12 = r12 / r13
            r13 = 1120403456(0x42CLASSNAME, float:100.0)
            float r12 = r12 * r13
            int r12 = (int) r12     // Catch:{ all -> 0x010a }
            org.telegram.messenger.-$$Lambda$MediaController$zgNVm6auedw0CFyBYptFe_RXXLI r13 = new org.telegram.messenger.-$$Lambda$MediaController$zgNVm6auedw0CFyBYptFe_RXXLI     // Catch:{ all -> 0x010a }
            r13.<init>(r12)     // Catch:{ all -> 0x010a }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r13)     // Catch:{ all -> 0x010a }
        L_0x00ff:
            long r12 = r3 + r0
            r1 = r19
            r14 = r5
            r3 = 2
            r4 = 1
            r5 = 0
            r6 = r21
            goto L_0x00c4
        L_0x010a:
            r0 = move-exception
            goto L_0x011e
        L_0x010c:
            r21 = r6
            if (r18 == 0) goto L_0x0113
            r18.close()     // Catch:{ all -> 0x0127 }
        L_0x0113:
            if (r21 == 0) goto L_0x0118
            r21.close()     // Catch:{ Exception -> 0x0135 }
        L_0x0118:
            r1 = 0
            r4 = 1
            goto L_0x013b
        L_0x011b:
            r0 = move-exception
            r21 = r6
        L_0x011e:
            throw r0     // Catch:{ all -> 0x011f }
        L_0x011f:
            r0 = move-exception
            r1 = r0
            if (r18 == 0) goto L_0x0126
            r18.close()     // Catch:{ all -> 0x0126 }
        L_0x0126:
            throw r1     // Catch:{ all -> 0x0127 }
        L_0x0127:
            r0 = move-exception
            goto L_0x012c
        L_0x0129:
            r0 = move-exception
            r21 = r6
        L_0x012c:
            throw r0     // Catch:{ all -> 0x012d }
        L_0x012d:
            r0 = move-exception
            r1 = r0
            if (r21 == 0) goto L_0x0134
            r21.close()     // Catch:{ all -> 0x0134 }
        L_0x0134:
            throw r1     // Catch:{ Exception -> 0x0135 }
        L_0x0135:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0014 }
            r1 = 0
            r4 = 0
        L_0x013b:
            boolean r0 = r22[r1]     // Catch:{ Exception -> 0x0014 }
            if (r0 == 0) goto L_0x0144
            r7.delete()     // Catch:{ Exception -> 0x0014 }
            r5 = 0
            goto L_0x0145
        L_0x0144:
            r5 = r4
        L_0x0145:
            if (r5 == 0) goto L_0x017b
            r3 = 2
            r1 = r19
            if (r1 != r3) goto L_0x0170
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0014 }
            java.lang.String r1 = "download"
            java.lang.Object r0 = r0.getSystemService(r1)     // Catch:{ Exception -> 0x0014 }
            r8 = r0
            android.app.DownloadManager r8 = (android.app.DownloadManager) r8     // Catch:{ Exception -> 0x0014 }
            java.lang.String r9 = r7.getName()     // Catch:{ Exception -> 0x0014 }
            java.lang.String r10 = r7.getName()     // Catch:{ Exception -> 0x0014 }
            r11 = 0
            java.lang.String r13 = r7.getAbsolutePath()     // Catch:{ Exception -> 0x0014 }
            long r14 = r7.length()     // Catch:{ Exception -> 0x0014 }
            r16 = 1
            r12 = r24
            r8.addCompletedDownload(r9, r10, r11, r12, r13, r14, r16)     // Catch:{ Exception -> 0x0014 }
            goto L_0x017b
        L_0x0170:
            android.net.Uri r0 = android.net.Uri.fromFile(r7)     // Catch:{ Exception -> 0x0014 }
            org.telegram.messenger.AndroidUtilities.addMediaToGallery((android.net.Uri) r0)     // Catch:{ Exception -> 0x0014 }
            goto L_0x017b
        L_0x0178:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x017b:
            if (r2 == 0) goto L_0x0185
            org.telegram.messenger.-$$Lambda$MediaController$R8rrajrq18e7fPxKe4SwY_kxVuk r0 = new org.telegram.messenger.-$$Lambda$MediaController$R8rrajrq18e7fPxKe4SwY_kxVuk
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x0185:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$saveFile$28(int, java.io.File, java.lang.String, boolean[], org.telegram.ui.ActionBar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$26(AlertDialog alertDialog, int i) {
        try {
            alertDialog.setProgress(i);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$null$27(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static boolean isWebp(Uri uri) {
        InputStream inputStream = null;
        try {
            InputStream openInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] bArr = new byte[12];
            if (openInputStream.read(bArr, 0, 12) == 12) {
                String lowerCase = new String(bArr).toLowerCase();
                if (lowerCase.startsWith("riff") && lowerCase.endsWith("webp")) {
                    if (openInputStream != null) {
                        try {
                            openInputStream.close();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    return true;
                }
            }
            if (openInputStream != null) {
                try {
                    openInputStream.close();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
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
                if (openInputStream != null) {
                    try {
                        openInputStream.close();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                return false;
            }
            if (openInputStream != null) {
                try {
                    openInputStream.close();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
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

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003b, code lost:
        if (r1 != null) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0040 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getFileName(android.net.Uri r10) {
        /*
            java.lang.String r0 = "_display_name"
            java.lang.String r1 = r10.getScheme()
            java.lang.String r2 = "content"
            boolean r1 = r1.equals(r2)
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0045
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0041 }
            android.content.ContentResolver r4 = r1.getContentResolver()     // Catch:{ Exception -> 0x0041 }
            java.lang.String[] r6 = new java.lang.String[r2]     // Catch:{ Exception -> 0x0041 }
            r1 = 0
            r6[r1] = r0     // Catch:{ Exception -> 0x0041 }
            r7 = 0
            r8 = 0
            r9 = 0
            r5 = r10
            android.database.Cursor r1 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0041 }
            boolean r4 = r1.moveToFirst()     // Catch:{ all -> 0x0038 }
            if (r4 == 0) goto L_0x0032
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0038 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x0038 }
            r3 = r0
        L_0x0032:
            if (r1 == 0) goto L_0x0045
            r1.close()     // Catch:{ Exception -> 0x0041 }
            goto L_0x0045
        L_0x0038:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x003a }
        L_0x003a:
            r0 = move-exception
            if (r1 == 0) goto L_0x0040
            r1.close()     // Catch:{ all -> 0x0040 }
        L_0x0040:
            throw r0     // Catch:{ Exception -> 0x0041 }
        L_0x0041:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0045:
            if (r3 != 0) goto L_0x0059
            java.lang.String r3 = r10.getPath()
            r10 = 47
            int r10 = r3.lastIndexOf(r10)
            r0 = -1
            if (r10 == r0) goto L_0x0059
            int r10 = r10 + r2
            java.lang.String r3 = r3.substring(r10)
        L_0x0059:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.getFileName(android.net.Uri):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x0095 A[SYNTHETIC, Splitter:B:43:0x0095] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x009f A[SYNTHETIC, Splitter:B:48:0x009f] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00ad A[SYNTHETIC, Splitter:B:56:0x00ad] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00b7 A[SYNTHETIC, Splitter:B:61:0x00b7] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String copyFileToCache(android.net.Uri r7, java.lang.String r8) {
        /*
            r0 = 0
            java.lang.String r1 = getFileName(r7)     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            java.lang.String r1 = org.telegram.messenger.FileLoader.fixFileName(r1)     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            r2 = 0
            if (r1 != 0) goto L_0x0027
            int r1 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            java.lang.String r4 = "%d.%s"
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            r5[r2] = r1     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            r1 = 1
            r5[r1] = r8     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            java.lang.String r1 = java.lang.String.format(r3, r4, r5)     // Catch:{ Exception -> 0x008d, all -> 0x008a }
        L_0x0027:
            java.io.File r8 = org.telegram.messenger.AndroidUtilities.getSharingDirectory()     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            r8.mkdirs()     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            r3.<init>(r8, r1)     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            android.net.Uri r8 = android.net.Uri.fromFile(r3)     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            boolean r8 = org.telegram.messenger.AndroidUtilities.isInternalUri(r8)     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            if (r8 == 0) goto L_0x003e
            return r0
        L_0x003e:
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            android.content.ContentResolver r8 = r8.getContentResolver()     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            java.io.InputStream r7 = r8.openInputStream(r7)     // Catch:{ Exception -> 0x008d, all -> 0x008a }
            java.io.FileOutputStream r8 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0084, all -> 0x007e }
            r8.<init>(r3)     // Catch:{ Exception -> 0x0084, all -> 0x007e }
            r1 = 20480(0x5000, float:2.8699E-41)
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x0078, all -> 0x0073 }
        L_0x0051:
            int r4 = r7.read(r1)     // Catch:{ Exception -> 0x0078, all -> 0x0073 }
            r5 = -1
            if (r4 == r5) goto L_0x005c
            r8.write(r1, r2, r4)     // Catch:{ Exception -> 0x0078, all -> 0x0073 }
            goto L_0x0051
        L_0x005c:
            java.lang.String r0 = r3.getAbsolutePath()     // Catch:{ Exception -> 0x0078, all -> 0x0073 }
            if (r7 == 0) goto L_0x006a
            r7.close()     // Catch:{ Exception -> 0x0066 }
            goto L_0x006a
        L_0x0066:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x006a:
            r8.close()     // Catch:{ Exception -> 0x006e }
            goto L_0x0072
        L_0x006e:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x0072:
            return r0
        L_0x0073:
            r0 = move-exception
            r6 = r0
            r0 = r7
            r7 = r6
            goto L_0x00ab
        L_0x0078:
            r1 = move-exception
            r6 = r8
            r8 = r7
            r7 = r1
            r1 = r6
            goto L_0x0090
        L_0x007e:
            r8 = move-exception
            r6 = r0
            r0 = r7
            r7 = r8
            r8 = r6
            goto L_0x00ab
        L_0x0084:
            r8 = move-exception
            r1 = r0
            r6 = r8
            r8 = r7
            r7 = r6
            goto L_0x0090
        L_0x008a:
            r7 = move-exception
            r8 = r0
            goto L_0x00ab
        L_0x008d:
            r7 = move-exception
            r8 = r0
            r1 = r8
        L_0x0090:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ all -> 0x00a8 }
            if (r8 == 0) goto L_0x009d
            r8.close()     // Catch:{ Exception -> 0x0099 }
            goto L_0x009d
        L_0x0099:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x009d:
            if (r1 == 0) goto L_0x00a7
            r1.close()     // Catch:{ Exception -> 0x00a3 }
            goto L_0x00a7
        L_0x00a3:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x00a7:
            return r0
        L_0x00a8:
            r7 = move-exception
            r0 = r8
            r8 = r1
        L_0x00ab:
            if (r0 == 0) goto L_0x00b5
            r0.close()     // Catch:{ Exception -> 0x00b1 }
            goto L_0x00b5
        L_0x00b1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00b5:
            if (r8 == 0) goto L_0x00bf
            r8.close()     // Catch:{ Exception -> 0x00bb }
            goto L_0x00bf
        L_0x00bb:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x00bf:
            goto L_0x00c1
        L_0x00c0:
            throw r7
        L_0x00c1:
            goto L_0x00c0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.copyFileToCache(android.net.Uri, java.lang.String):java.lang.String");
    }

    public static void loadGalleryPhotosAlbums(int i) {
        Thread thread = new Thread(new Runnable(i) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                MediaController.lambda$loadGalleryPhotosAlbums$30(this.f$0);
            }
        });
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v23, resolved type: org.telegram.messenger.MediaController$AlbumEntry} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v18, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v20, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: org.telegram.messenger.MediaController$AlbumEntry} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0264  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x027a A[SYNTHETIC, Splitter:B:127:0x027a] */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x02b3 A[SYNTHETIC, Splitter:B:140:0x02b3] */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x02c4 A[Catch:{ all -> 0x0439 }] */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x02ed A[Catch:{ all -> 0x0439 }] */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x02f0 A[Catch:{ all -> 0x0439 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0062 A[Catch:{ all -> 0x0293 }] */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x0304 A[Catch:{ all -> 0x0439 }] */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0432 A[SYNTHETIC, Splitter:B:214:0x0432] */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0440 A[SYNTHETIC, Splitter:B:223:0x0440] */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0456 A[LOOP:2: B:229:0x0450->B:231:0x0456, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00bb A[SYNTHETIC, Splitter:B:30:0x00bb] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$loadGalleryPhotosAlbums$30(int r51) {
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
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0052 }
            r0.<init>()     // Catch:{ Exception -> 0x0052 }
            java.lang.String r18 = android.os.Environment.DIRECTORY_DCIM     // Catch:{ Exception -> 0x0052 }
            java.io.File r18 = android.os.Environment.getExternalStoragePublicDirectory(r18)     // Catch:{ Exception -> 0x0052 }
            r19 = r12
            java.lang.String r12 = r18.getAbsolutePath()     // Catch:{ Exception -> 0x0050 }
            r0.append(r12)     // Catch:{ Exception -> 0x0050 }
            java.lang.String r12 = "/Camera/"
            r0.append(r12)     // Catch:{ Exception -> 0x0050 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0050 }
            r12 = r0
            goto L_0x005a
        L_0x0050:
            r0 = move-exception
            goto L_0x0055
        L_0x0052:
            r0 = move-exception
            r19 = r12
        L_0x0055:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r12 = r17
        L_0x005a:
            r18 = r11
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0293 }
            r11 = 23
            if (r0 < r11) goto L_0x008b
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0293 }
            if (r0 < r11) goto L_0x006f
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0293 }
            int r0 = r0.checkSelfPermission(r10)     // Catch:{ all -> 0x0293 }
            if (r0 != 0) goto L_0x006f
            goto L_0x008b
        L_0x006f:
            r35 = r2
            r30 = r3
            r28 = r4
            r25 = r5
            r24 = r6
            r23 = r7
            r26 = r8
            r27 = r9
            r29 = r10
            r10 = r17
            r31 = r10
        L_0x0085:
            r32 = r31
            r33 = r32
            goto L_0x0278
        L_0x008b:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0293 }
            android.content.ContentResolver r23 = r0.getContentResolver()     // Catch:{ all -> 0x0293 }
            android.net.Uri r24 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x0293 }
            java.lang.String[] r25 = projectionPhotos     // Catch:{ all -> 0x0293 }
            r26 = 0
            r27 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0293 }
            r0.<init>()     // Catch:{ all -> 0x0293 }
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0293 }
            r29 = r10
            r10 = 28
            if (r11 <= r10) goto L_0x00a9
            r10 = r16
            goto L_0x00ab
        L_0x00a9:
            r10 = r19
        L_0x00ab:
            r0.append(r10)     // Catch:{ all -> 0x0281 }
            r0.append(r9)     // Catch:{ all -> 0x0281 }
            java.lang.String r28 = r0.toString()     // Catch:{ all -> 0x0281 }
            android.database.Cursor r10 = android.provider.MediaStore.Images.Media.query(r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x0281 }
            if (r10 == 0) goto L_0x0264
            int r0 = r10.getColumnIndex(r8)     // Catch:{ all -> 0x0250 }
            int r11 = r10.getColumnIndex(r7)     // Catch:{ all -> 0x0250 }
            r23 = r7
            int r7 = r10.getColumnIndex(r6)     // Catch:{ all -> 0x0244 }
            r24 = r6
            int r6 = r10.getColumnIndex(r5)     // Catch:{ all -> 0x023a }
            r25 = r5
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0232 }
            r26 = r8
            r8 = 28
            if (r5 <= r8) goto L_0x00dc
            r5 = r16
            goto L_0x00de
        L_0x00dc:
            r5 = r19
        L_0x00de:
            int r5 = r10.getColumnIndex(r5)     // Catch:{ all -> 0x022a }
            java.lang.String r8 = "orientation"
            int r8 = r10.getColumnIndex(r8)     // Catch:{ all -> 0x022a }
            r27 = r9
            int r9 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x0222 }
            r28 = r4
            int r4 = r10.getColumnIndex(r3)     // Catch:{ all -> 0x021c }
            r30 = r3
            int r3 = r10.getColumnIndex(r2)     // Catch:{ all -> 0x0218 }
            r31 = r17
            r32 = r31
            r33 = r32
            r34 = r33
        L_0x0102:
            boolean r35 = r10.moveToNext()     // Catch:{ all -> 0x0213 }
            if (r35 == 0) goto L_0x020f
            r35 = r2
            java.lang.String r2 = r10.getString(r6)     // Catch:{ all -> 0x020c }
            boolean r36 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x020c }
            if (r36 == 0) goto L_0x0117
            r2 = r35
            goto L_0x0102
        L_0x0117:
            int r38 = r10.getInt(r0)     // Catch:{ all -> 0x020c }
            r48 = r0
            int r0 = r10.getInt(r11)     // Catch:{ all -> 0x020c }
            r49 = r6
            java.lang.String r6 = r10.getString(r7)     // Catch:{ all -> 0x020c }
            long r39 = r10.getLong(r5)     // Catch:{ all -> 0x020c }
            int r42 = r10.getInt(r8)     // Catch:{ all -> 0x020c }
            int r44 = r10.getInt(r9)     // Catch:{ all -> 0x020c }
            int r45 = r10.getInt(r4)     // Catch:{ all -> 0x020c }
            long r46 = r10.getLong(r3)     // Catch:{ all -> 0x020c }
            r50 = r3
            org.telegram.messenger.MediaController$PhotoEntry r3 = new org.telegram.messenger.MediaController$PhotoEntry     // Catch:{ all -> 0x020c }
            r43 = 0
            r36 = r3
            r37 = r0
            r41 = r2
            r36.<init>(r37, r38, r39, r41, r42, r43, r44, r45, r46)     // Catch:{ all -> 0x020c }
            if (r31 != 0) goto L_0x0165
            r36 = r4
            org.telegram.messenger.MediaController$AlbumEntry r4 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x020c }
            r37 = r5
            java.lang.String r5 = "AllPhotos"
            r38 = r7
            r7 = 2131624154(0x7f0e00da, float:1.887548E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r7)     // Catch:{ all -> 0x020c }
            r7 = 0
            r4.<init>(r7, r5, r3)     // Catch:{ all -> 0x020c }
            r15.add(r7, r4)     // Catch:{ all -> 0x0182 }
            goto L_0x016d
        L_0x0165:
            r36 = r4
            r37 = r5
            r38 = r7
            r4 = r31
        L_0x016d:
            if (r32 != 0) goto L_0x0187
            org.telegram.messenger.MediaController$AlbumEntry r5 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0182 }
            r39 = r8
            r7 = 2131624153(0x7f0e00d9, float:1.8875478E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r1, r7)     // Catch:{ all -> 0x0182 }
            r7 = 0
            r5.<init>(r7, r8, r3)     // Catch:{ all -> 0x0182 }
            r14.add(r7, r5)     // Catch:{ all -> 0x0205 }
            goto L_0x018b
        L_0x0182:
            r0 = move-exception
            r31 = r4
            goto L_0x02ae
        L_0x0187:
            r39 = r8
            r5 = r32
        L_0x018b:
            r4.addPhoto(r3)     // Catch:{ all -> 0x0205 }
            r5.addPhoto(r3)     // Catch:{ all -> 0x0205 }
            java.lang.Object r7 = r13.get(r0)     // Catch:{ all -> 0x0205 }
            org.telegram.messenger.MediaController$AlbumEntry r7 = (org.telegram.messenger.MediaController.AlbumEntry) r7     // Catch:{ all -> 0x0205 }
            if (r7 != 0) goto L_0x01bb
            org.telegram.messenger.MediaController$AlbumEntry r7 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0205 }
            r7.<init>(r0, r6, r3)     // Catch:{ all -> 0x0205 }
            r13.put(r0, r7)     // Catch:{ all -> 0x0205 }
            if (r33 != 0) goto L_0x01b8
            if (r12 == 0) goto L_0x01b8
            if (r2 == 0) goto L_0x01b8
            boolean r8 = r2.startsWith(r12)     // Catch:{ all -> 0x0205 }
            if (r8 == 0) goto L_0x01b8
            r8 = 0
            r14.add(r8, r7)     // Catch:{ all -> 0x0205 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0205 }
            r33 = r8
            goto L_0x01bb
        L_0x01b8:
            r14.add(r7)     // Catch:{ all -> 0x0205 }
        L_0x01bb:
            r7.addPhoto(r3)     // Catch:{ all -> 0x0205 }
            r7 = r18
            java.lang.Object r8 = r7.get(r0)     // Catch:{ all -> 0x0205 }
            org.telegram.messenger.MediaController$AlbumEntry r8 = (org.telegram.messenger.MediaController.AlbumEntry) r8     // Catch:{ all -> 0x0205 }
            if (r8 != 0) goto L_0x01ea
            org.telegram.messenger.MediaController$AlbumEntry r8 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0205 }
            r8.<init>(r0, r6, r3)     // Catch:{ all -> 0x0205 }
            r7.put(r0, r8)     // Catch:{ all -> 0x0205 }
            if (r34 != 0) goto L_0x01e7
            if (r12 == 0) goto L_0x01e7
            if (r2 == 0) goto L_0x01e7
            boolean r2 = r2.startsWith(r12)     // Catch:{ all -> 0x0205 }
            if (r2 == 0) goto L_0x01e7
            r2 = 0
            r15.add(r2, r8)     // Catch:{ all -> 0x0205 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0205 }
            r34 = r0
            goto L_0x01ea
        L_0x01e7:
            r15.add(r8)     // Catch:{ all -> 0x0205 }
        L_0x01ea:
            r8.addPhoto(r3)     // Catch:{ all -> 0x0205 }
            r31 = r4
            r32 = r5
            r18 = r7
            r2 = r35
            r4 = r36
            r5 = r37
            r7 = r38
            r8 = r39
            r0 = r48
            r6 = r49
            r3 = r50
            goto L_0x0102
        L_0x0205:
            r0 = move-exception
            r31 = r4
            r32 = r5
            goto L_0x02ae
        L_0x020c:
            r0 = move-exception
            goto L_0x02ae
        L_0x020f:
            r35 = r2
            goto L_0x0278
        L_0x0213:
            r0 = move-exception
            r35 = r2
            goto L_0x02ae
        L_0x0218:
            r0 = move-exception
            r35 = r2
            goto L_0x0261
        L_0x021c:
            r0 = move-exception
            r35 = r2
            r30 = r3
            goto L_0x0261
        L_0x0222:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            goto L_0x0261
        L_0x022a:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            goto L_0x025f
        L_0x0232:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            goto L_0x025d
        L_0x023a:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r25 = r5
            goto L_0x025d
        L_0x0244:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r25 = r5
            r24 = r6
            goto L_0x025d
        L_0x0250:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r25 = r5
            r24 = r6
            r23 = r7
        L_0x025d:
            r26 = r8
        L_0x025f:
            r27 = r9
        L_0x0261:
            r31 = r17
            goto L_0x02aa
        L_0x0264:
            r35 = r2
            r30 = r3
            r28 = r4
            r25 = r5
            r24 = r6
            r23 = r7
            r26 = r8
            r27 = r9
            r31 = r17
            goto L_0x0085
        L_0x0278:
            if (r10 == 0) goto L_0x02bc
            r10.close()     // Catch:{ Exception -> 0x027e }
            goto L_0x02bc
        L_0x027e:
            r0 = move-exception
            r2 = r0
            goto L_0x02b9
        L_0x0281:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r25 = r5
            r24 = r6
            r23 = r7
            r26 = r8
            r27 = r9
            goto L_0x02a6
        L_0x0293:
            r0 = move-exception
            r35 = r2
            r30 = r3
            r28 = r4
            r25 = r5
            r24 = r6
            r23 = r7
            r26 = r8
            r27 = r9
            r29 = r10
        L_0x02a6:
            r10 = r17
            r31 = r10
        L_0x02aa:
            r32 = r31
            r33 = r32
        L_0x02ae:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x047c }
            if (r10 == 0) goto L_0x02bc
            r10.close()     // Catch:{ Exception -> 0x02b7 }
            goto L_0x02bc
        L_0x02b7:
            r0 = move-exception
            r2 = r0
        L_0x02b9:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x02bc:
            r18 = r31
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0439 }
            r2 = 23
            if (r0 < r2) goto L_0x02d6
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0439 }
            if (r0 < r2) goto L_0x02d3
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0439 }
            r2 = r29
            int r0 = r0.checkSelfPermission(r2)     // Catch:{ all -> 0x0439 }
            if (r0 != 0) goto L_0x02d3
            goto L_0x02d6
        L_0x02d3:
            r2 = 0
            goto L_0x0430
        L_0x02d6:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0439 }
            android.content.ContentResolver r2 = r0.getContentResolver()     // Catch:{ all -> 0x0439 }
            android.net.Uri r3 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x0439 }
            java.lang.String[] r4 = projectionVideo     // Catch:{ all -> 0x0439 }
            r5 = 0
            r6 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0439 }
            r0.<init>()     // Catch:{ all -> 0x0439 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0439 }
            r8 = 28
            if (r7 <= r8) goto L_0x02f0
            r7 = r16
            goto L_0x02f2
        L_0x02f0:
            r7 = r19
        L_0x02f2:
            r0.append(r7)     // Catch:{ all -> 0x0439 }
            r7 = r27
            r0.append(r7)     // Catch:{ all -> 0x0439 }
            java.lang.String r7 = r0.toString()     // Catch:{ all -> 0x0439 }
            android.database.Cursor r10 = android.provider.MediaStore.Images.Media.query(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0439 }
            if (r10 == 0) goto L_0x02d3
            r2 = r26
            int r0 = r10.getColumnIndex(r2)     // Catch:{ all -> 0x0439 }
            r2 = r23
            int r2 = r10.getColumnIndex(r2)     // Catch:{ all -> 0x0439 }
            r3 = r24
            int r3 = r10.getColumnIndex(r3)     // Catch:{ all -> 0x0439 }
            r4 = r25
            int r4 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x0439 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0439 }
            r6 = 28
            if (r5 <= r6) goto L_0x0325
            r11 = r16
            goto L_0x0327
        L_0x0325:
            r11 = r19
        L_0x0327:
            int r5 = r10.getColumnIndex(r11)     // Catch:{ all -> 0x0439 }
            java.lang.String r6 = "duration"
            int r6 = r10.getColumnIndex(r6)     // Catch:{ all -> 0x0439 }
            r7 = r28
            int r7 = r10.getColumnIndex(r7)     // Catch:{ all -> 0x0439 }
            r8 = r30
            int r8 = r10.getColumnIndex(r8)     // Catch:{ all -> 0x0439 }
            r9 = r35
            int r9 = r10.getColumnIndex(r9)     // Catch:{ all -> 0x0439 }
        L_0x0343:
            boolean r11 = r10.moveToNext()     // Catch:{ all -> 0x0439 }
            if (r11 == 0) goto L_0x02d3
            java.lang.String r11 = r10.getString(r4)     // Catch:{ all -> 0x0439 }
            boolean r16 = android.text.TextUtils.isEmpty(r11)     // Catch:{ all -> 0x0439 }
            if (r16 == 0) goto L_0x0354
            goto L_0x0343
        L_0x0354:
            int r36 = r10.getInt(r0)     // Catch:{ all -> 0x0439 }
            r16 = r0
            int r0 = r10.getInt(r2)     // Catch:{ all -> 0x0439 }
            r19 = r2
            java.lang.String r2 = r10.getString(r3)     // Catch:{ all -> 0x0439 }
            long r37 = r10.getLong(r5)     // Catch:{ all -> 0x0439 }
            long r21 = r10.getLong(r6)     // Catch:{ all -> 0x0439 }
            int r42 = r10.getInt(r7)     // Catch:{ all -> 0x0439 }
            int r43 = r10.getInt(r8)     // Catch:{ all -> 0x0439 }
            long r44 = r10.getLong(r9)     // Catch:{ all -> 0x0439 }
            r23 = r3
            org.telegram.messenger.MediaController$PhotoEntry r3 = new org.telegram.messenger.MediaController$PhotoEntry     // Catch:{ all -> 0x0439 }
            r24 = 1000(0x3e8, double:4.94E-321)
            r26 = r4
            r27 = r5
            long r4 = r21 / r24
            int r5 = (int) r4     // Catch:{ all -> 0x0439 }
            r41 = 1
            r34 = r3
            r35 = r0
            r39 = r11
            r40 = r5
            r34.<init>(r35, r36, r37, r39, r40, r41, r42, r43, r44)     // Catch:{ all -> 0x0439 }
            if (r17 != 0) goto L_0x03b4
            org.telegram.messenger.MediaController$AlbumEntry r4 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0439 }
            java.lang.String r5 = "AllVideos"
            r21 = r6
            r6 = 2131624155(0x7f0e00db, float:1.8875482E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0439 }
            r6 = 0
            r4.<init>(r6, r5, r3)     // Catch:{ all -> 0x0439 }
            r5 = 1
            r4.videoOnly = r5     // Catch:{ all -> 0x03d4 }
            if (r32 == 0) goto L_0x03ab
            goto L_0x03ac
        L_0x03ab:
            r5 = 0
        L_0x03ac:
            if (r18 == 0) goto L_0x03b0
            int r5 = r5 + 1
        L_0x03b0:
            r14.add(r5, r4)     // Catch:{ all -> 0x03d4 }
            goto L_0x03b8
        L_0x03b4:
            r21 = r6
            r4 = r17
        L_0x03b8:
            if (r32 != 0) goto L_0x03d9
            org.telegram.messenger.MediaController$AlbumEntry r5 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x03d4 }
            r20 = r7
            r6 = 2131624153(0x7f0e00d9, float:1.8875478E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x03d4 }
            r6 = 0
            r5.<init>(r6, r7, r3)     // Catch:{ all -> 0x03d4 }
            r14.add(r6, r5)     // Catch:{ all -> 0x03cd }
            goto L_0x03dd
        L_0x03cd:
            r0 = move-exception
            r17 = r4
            r32 = r5
            goto L_0x043a
        L_0x03d4:
            r0 = move-exception
            r17 = r4
            goto L_0x043a
        L_0x03d9:
            r20 = r7
            r5 = r32
        L_0x03dd:
            r4.addPhoto(r3)     // Catch:{ all -> 0x0429 }
            r5.addPhoto(r3)     // Catch:{ all -> 0x0429 }
            java.lang.Object r6 = r13.get(r0)     // Catch:{ all -> 0x0429 }
            org.telegram.messenger.MediaController$AlbumEntry r6 = (org.telegram.messenger.MediaController.AlbumEntry) r6     // Catch:{ all -> 0x0429 }
            if (r6 != 0) goto L_0x040f
            org.telegram.messenger.MediaController$AlbumEntry r6 = new org.telegram.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0429 }
            r6.<init>(r0, r2, r3)     // Catch:{ all -> 0x0429 }
            r13.put(r0, r6)     // Catch:{ all -> 0x0429 }
            if (r33 != 0) goto L_0x040a
            if (r12 == 0) goto L_0x040a
            if (r11 == 0) goto L_0x040a
            boolean r2 = r11.startsWith(r12)     // Catch:{ all -> 0x0429 }
            if (r2 == 0) goto L_0x040a
            r2 = 0
            r14.add(r2, r6)     // Catch:{ all -> 0x0427 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0427 }
            r33 = r0
            goto L_0x0410
        L_0x040a:
            r2 = 0
            r14.add(r6)     // Catch:{ all -> 0x0427 }
            goto L_0x0410
        L_0x040f:
            r2 = 0
        L_0x0410:
            r6.addPhoto(r3)     // Catch:{ all -> 0x0427 }
            r17 = r4
            r32 = r5
            r0 = r16
            r2 = r19
            r7 = r20
            r6 = r21
            r3 = r23
            r4 = r26
            r5 = r27
            goto L_0x0343
        L_0x0427:
            r0 = move-exception
            goto L_0x042b
        L_0x0429:
            r0 = move-exception
            r2 = 0
        L_0x042b:
            r17 = r4
            r32 = r5
            goto L_0x043b
        L_0x0430:
            if (r10 == 0) goto L_0x0449
            r10.close()     // Catch:{ Exception -> 0x0436 }
            goto L_0x0449
        L_0x0436:
            r0 = move-exception
            r1 = r0
            goto L_0x0446
        L_0x0439:
            r0 = move-exception
        L_0x043a:
            r2 = 0
        L_0x043b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x046e }
            if (r10 == 0) goto L_0x0449
            r10.close()     // Catch:{ Exception -> 0x0444 }
            goto L_0x0449
        L_0x0444:
            r0 = move-exception
            r1 = r0
        L_0x0446:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0449:
            r19 = r17
            r17 = r32
            r16 = r33
            r11 = 0
        L_0x0450:
            int r0 = r14.size()
            if (r11 >= r0) goto L_0x0466
            java.lang.Object r0 = r14.get(r11)
            org.telegram.messenger.MediaController$AlbumEntry r0 = (org.telegram.messenger.MediaController.AlbumEntry) r0
            java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r0 = r0.photos
            org.telegram.messenger.-$$Lambda$MediaController$gntuUzO_4_fTyV8bz3N6RrfajeU r1 = org.telegram.messenger.$$Lambda$MediaController$gntuUzO_4_fTyV8bz3N6RrfajeU.INSTANCE
            java.util.Collections.sort(r0, r1)
            int r11 = r11 + 1
            goto L_0x0450
        L_0x0466:
            r20 = 0
            r13 = r51
            broadcastNewPhotos(r13, r14, r15, r16, r17, r18, r19, r20)
            return
        L_0x046e:
            r0 = move-exception
            r1 = r0
            if (r10 == 0) goto L_0x047b
            r10.close()     // Catch:{ Exception -> 0x0476 }
            goto L_0x047b
        L_0x0476:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x047b:
            throw r1
        L_0x047c:
            r0 = move-exception
            r1 = r0
            if (r10 == 0) goto L_0x0489
            r10.close()     // Catch:{ Exception -> 0x0484 }
            goto L_0x0489
        L_0x0484:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0489:
            goto L_0x048b
        L_0x048a:
            throw r1
        L_0x048b:
            goto L_0x048a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$loadGalleryPhotosAlbums$30(int):void");
    }

    static /* synthetic */ int lambda$null$29(PhotoEntry photoEntry, PhotoEntry photoEntry2) {
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
        $$Lambda$MediaController$sfYDVGl6HXU6g4wE30VsRlfsGw r1 = new Runnable(i, arrayList, arrayList2, num, albumEntry, albumEntry2, albumEntry3) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ Integer f$3;
            private final /* synthetic */ MediaController.AlbumEntry f$4;
            private final /* synthetic */ MediaController.AlbumEntry f$5;
            private final /* synthetic */ MediaController.AlbumEntry f$6;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MediaController.lambda$broadcastNewPhotos$31(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        };
        broadcastPhotosRunnable = r1;
        AndroidUtilities.runOnUIThread(r1, (long) i2);
    }

    static /* synthetic */ void lambda$broadcastNewPhotos$31(int i, ArrayList arrayList, ArrayList arrayList2, Integer num, AlbumEntry albumEntry, AlbumEntry albumEntry2, AlbumEntry albumEntry3) {
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
    public void didWriteData(VideoConvertMessage videoConvertMessage, File file, boolean z, long j, boolean z2, float f) {
        VideoEditedInfo videoEditedInfo = videoConvertMessage.videoEditedInfo;
        boolean z3 = videoEditedInfo.videoConvertFirstWrite;
        if (z3) {
            videoEditedInfo.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new Runnable(z2, z, videoConvertMessage, file, f, z3, j) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ MediaController.VideoConvertMessage f$3;
            private final /* synthetic */ File f$4;
            private final /* synthetic */ float f$5;
            private final /* synthetic */ boolean f$6;
            private final /* synthetic */ long f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                MediaController.this.lambda$didWriteData$32$MediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$didWriteData$32$MediaController(boolean z, boolean z2, VideoConvertMessage videoConvertMessage, File file, float f, boolean z3, long j) {
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

    private static class VideoConvertRunnable implements Runnable {
        private VideoConvertMessage convertMessage;

        private VideoConvertRunnable(VideoConvertMessage videoConvertMessage) {
            this.convertMessage = videoConvertMessage;
        }

        public void run() {
            boolean unused = MediaController.getInstance().convertVideo(this.convertMessage);
        }

        public static void runConversion(VideoConvertMessage videoConvertMessage) {
            new Thread(new Runnable() {
                public final void run() {
                    MediaController.VideoConvertRunnable.lambda$runConversion$0(MediaController.VideoConvertMessage.this);
                }
            }).start();
        }

        static /* synthetic */ void lambda$runConversion$0(VideoConvertMessage videoConvertMessage) {
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
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00a8 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0143 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean convertVideo(org.telegram.messenger.MediaController.VideoConvertMessage r33) {
        /*
            r32 = this;
            r9 = r32
            r0 = r33
            org.telegram.messenger.MessageObject r1 = r0.messageObject
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            if (r1 == 0) goto L_0x015c
            if (r2 != 0) goto L_0x000e
            goto L_0x015c
        L_0x000e:
            java.lang.String r4 = r2.originalPath
            long r5 = r2.startTime
            long r7 = r2.endTime
            int r10 = r2.resultWidth
            int r11 = r2.resultHeight
            int r12 = r2.rotationValue
            int r13 = r2.originalWidth
            int r14 = r2.originalHeight
            int r15 = r2.framerate
            int r3 = r2.bitrate
            r16 = r10
            r17 = r11
            long r10 = r1.getDialogId()
            int r11 = (int) r10
            if (r11 != 0) goto L_0x0030
            r18 = 1
            goto L_0x0032
        L_0x0030:
            r18 = 0
        L_0x0032:
            java.io.File r11 = new java.io.File
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.attachPath
            r11.<init>(r1)
            boolean r1 = r11.exists()
            if (r1 == 0) goto L_0x0044
            r11.delete()
        L_0x0044:
            if (r4 != 0) goto L_0x0048
            java.lang.String r4 = ""
        L_0x0048:
            r19 = 0
            int r1 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0057
            int r1 = (r7 > r19 ? 1 : (r7 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0057
            long r19 = r7 - r5
            r24 = r19
            goto L_0x005d
        L_0x0057:
            int r1 = (r7 > r19 ? 1 : (r7 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0060
            r24 = r7
        L_0x005d:
            r19 = r11
            goto L_0x0070
        L_0x0060:
            int r1 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x006a
            r19 = r11
            long r10 = r2.originalDuration
            long r10 = r10 - r5
            goto L_0x006e
        L_0x006a:
            r19 = r11
            long r10 = r2.originalDuration
        L_0x006e:
            r24 = r10
        L_0x0070:
            r10 = 59
            if (r15 != 0) goto L_0x0079
            r10 = 25
            r20 = 25
            goto L_0x0080
        L_0x0079:
            if (r15 <= r10) goto L_0x007e
            r20 = 59
            goto L_0x0080
        L_0x007e:
            r20 = r15
        L_0x0080:
            r10 = 270(0x10e, float:3.78E-43)
            r11 = 180(0xb4, float:2.52E-43)
            r15 = 90
            if (r12 != r15) goto L_0x008e
            r11 = r16
            r12 = r17
        L_0x008c:
            r15 = 0
            goto L_0x00a6
        L_0x008e:
            if (r12 != r11) goto L_0x0097
            r12 = r16
            r11 = r17
            r10 = 180(0xb4, float:2.52E-43)
            goto L_0x008c
        L_0x0097:
            if (r12 != r10) goto L_0x00a0
            r11 = r16
            r12 = r17
            r10 = 90
            goto L_0x008c
        L_0x00a0:
            r15 = r12
            r12 = r16
            r11 = r17
            r10 = 0
        L_0x00a6:
            if (r12 != r13) goto L_0x00c0
            if (r11 != r14) goto L_0x00c0
            if (r10 != 0) goto L_0x00c0
            boolean r10 = r2.roundVideo
            if (r10 != 0) goto L_0x00c0
            int r10 = android.os.Build.VERSION.SDK_INT
            r13 = 18
            if (r10 < r13) goto L_0x00bd
            r13 = -1
            int r10 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r10 == 0) goto L_0x00bd
            goto L_0x00c0
        L_0x00bd:
            r23 = 0
            goto L_0x00c2
        L_0x00c0:
            r23 = 1
        L_0x00c2:
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r13 = "videoconvert"
            r14 = 0
            android.content.SharedPreferences r28 = r10.getSharedPreferences(r13, r14)
            long r29 = java.lang.System.currentTimeMillis()
            org.telegram.messenger.MediaController$8 r10 = new org.telegram.messenger.MediaController$8
            r26 = r10
            r13 = r19
            r10.<init>(r2, r13, r0)
            r1 = 1
            r2.videoConvertFirstWrite = r1
            org.telegram.messenger.video.MediaCodecVideoConvertor r14 = new org.telegram.messenger.video.MediaCodecVideoConvertor
            r10 = r14
            r14.<init>()
            r16 = r11
            r31 = r13
            r11 = r4
            r17 = r12
            r12 = r31
            r13 = r15
            r14 = r18
            r15 = r17
            r17 = r20
            r18 = r3
            r19 = r5
            r21 = r7
            boolean r3 = r10.convertVideo(r11, r12, r13, r14, r15, r16, r17, r18, r19, r21, r23, r24, r26)
            boolean r4 = r2.canceled
            if (r4 != 0) goto L_0x0109
            java.lang.Object r5 = r9.videoConvertSync
            monitor-enter(r5)
            boolean r4 = r2.canceled     // Catch:{ all -> 0x0106 }
            monitor-exit(r5)     // Catch:{ all -> 0x0106 }
            goto L_0x0109
        L_0x0106:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0106 }
            throw r0
        L_0x0109:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x012f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "time="
            r2.append(r5)
            long r5 = java.lang.System.currentTimeMillis()
            long r5 = r5 - r29
            r2.append(r5)
            java.lang.String r5 = " canceled="
            r2.append(r5)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x012f:
            android.content.SharedPreferences$Editor r2 = r28.edit()
            java.lang.String r5 = "isPreviousOk"
            android.content.SharedPreferences$Editor r2 = r2.putBoolean(r5, r1)
            r2.apply()
            r5 = 1
            long r6 = r31.length()
            if (r3 != 0) goto L_0x0149
            if (r4 == 0) goto L_0x0146
            goto L_0x0149
        L_0x0146:
            r27 = 0
            goto L_0x014b
        L_0x0149:
            r27 = 1
        L_0x014b:
            r8 = 1065353216(0x3var_, float:1.0)
            r10 = 1
            r1 = r32
            r2 = r33
            r3 = r31
            r4 = r5
            r5 = r6
            r7 = r27
            r1.didWriteData(r2, r3, r4, r5, r7, r8)
            return r10
        L_0x015c:
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

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0056 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0057  */
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
            goto L_0x0036
        L_0x0010:
            int r0 = java.lang.Math.min(r8, r9)
            r2 = 720(0x2d0, float:1.009E-42)
            if (r0 < r2) goto L_0x001b
            r0 = 2621440(0x280000, float:3.67342E-39)
            goto L_0x000d
        L_0x001b:
            int r0 = java.lang.Math.min(r8, r9)
            r1 = 480(0x1e0, float:6.73E-43)
            if (r0 < r1) goto L_0x002d
            r0 = 1000000(0xvar_, float:1.401298E-39)
            r1 = 1061997773(0x3f4ccccd, float:0.8)
            r2 = 1063675494(0x3var_, float:0.9)
            goto L_0x0036
        L_0x002d:
            r0 = 750000(0xb71b0, float:1.050974E-39)
            r1 = 1058642330(0x3var_a, float:0.6)
            r2 = 1060320051(0x3var_, float:0.7)
        L_0x0036:
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
            if (r7 >= r6) goto L_0x0057
            return r5
        L_0x0057:
            if (r5 <= r0) goto L_0x005a
            return r0
        L_0x005a:
            if (r5 >= r6) goto L_0x005d
            return r6
        L_0x005d:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.makeVideoBitrate(int, int, int, int, int):int");
    }
}
